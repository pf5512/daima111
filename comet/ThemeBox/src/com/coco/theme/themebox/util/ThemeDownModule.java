package com.coco.theme.themebox.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.coco.lock2.lockbox.PlatformInfo;
import com.coco.theme.themebox.StaticClass;
import com.coco.theme.themebox.database.model.AddressType;
import com.coco.theme.themebox.database.model.ApplicationType;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.database.service.HotThemeService;
import com.coco.theme.themebox.database.service.UrlAddressThemeService;
import com.coco.theme.themebox.util.ThemeXmlParser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import com.coco.theme.themebox.util.Log;
import com.umeng.analytics.MobclickAgent;

import android.view.Display;
import android.view.WindowManager;

public class ThemeDownModule {

	private final String LOG_TAG = "ThemeDownModule";
	public static final String CONFIG_FILE_NAME = "config.ini";
	private Context mContext;

	private DownloadImageThread downImageThread = null;
	private DownloadApkThread downApkThread = null;
	private DownloadListThread downListThread = null;

	private List<DownImageNode> downImgList = new ArrayList<DownImageNode>();
	private List<DownApkNode> downApkList = new ArrayList<DownApkNode>();

	private DownloadThemeService downApkDb;

	private Object syncObject = new Object();

	public ThemeDownModule(Context context) {
		mContext = context;
		downApkDb = new DownloadThemeService(context);
	}

	public void dispose() {
		Log.d(LOG_TAG, "dispose");
		synchronized (this.syncObject) {
			downImgList.clear();
			synchronized (downApkDb) {
				for(DownApkNode node:downApkList) {
					downApkDb.updateDownloadStatus(node.packname, 
							DownloadStatus.StatusPause);
				}
			}
			downApkList.clear();

			if (downImageThread != null) {
				downImageThread.stopRun();
				downImageThread = null;
			}

			if (downApkThread != null) {
				downApkThread.stopRun();
				downApkThread = null;
			}

			if (downListThread != null) {
				downListThread.stopRun();
				downListThread = null;
			}
		}
	}

	public void stopDownlist() {
		if(downListThread != null)
			downListThread.stopRun();		
	}
	
	// 安装apk
	public void installApk(String pkgName) {
		String filepath = PathTool.getAppFile(pkgName);
		File file = new File(filepath);

		Log.e("OpenFile", file.getName());
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}

	// 下载列表完成
	private void downloadListFinish() {
		Log.v(LOG_TAG, "downloadListFinish:"
				+ StaticClass.ACTION_HOTLIST_CHANGED);
		
		ThemeXmlParser parserXml = new ThemeXmlParser(mContext);
		if (!parserXml.parseList(PathTool.getThemeDownloadingList())) {
			return;
		}

		HotThemeService sv = new HotThemeService(mContext);
		sv.clearTable();
		sv.batchInsert(parserXml.getThemeList());

		UrlAddressThemeService uas = new UrlAddressThemeService(mContext);
		uas.deleteAddress(ApplicationType.AppTheme, AddressType.AddressThumb);
		uas.batchInsertAddress(ApplicationType.AppTheme,
				AddressType.AddressThumb, parserXml.getPictureAddress());

		uas.deleteAddress(ApplicationType.AppTheme, AddressType.AddressPreview);
		uas.batchInsertAddress(ApplicationType.AppTheme,
				AddressType.AddressPreview, parserXml.getPictureAddress());

		uas.deleteAddress(ApplicationType.AppTheme, AddressType.AddressApp);
		uas.batchInsertAddress(ApplicationType.AppTheme, AddressType.AddressApp,
				parserXml.getApplicationAddress());
		
		saveListTime();
		
		Intent intent = new Intent(StaticClass.ACTION_HOTLIST_CHANGED);
		mContext.sendBroadcast(intent);
	}


	private void downloadApkStatusUpdate(String pkgName, DownloadStatus status) {
		synchronized (downApkDb) {
			downApkDb.updateDownloadStatus(pkgName, status);
		}
		Intent intent = new Intent(StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED);
		intent.putExtra(StaticClass.EXTRA_PACKAGE_NAME, pkgName);
		mContext.sendBroadcast(intent);
	}

	private void downloadApkFinish(String pkgName) {

		
		//友盟  下载完成统计
		MobclickAgent.onEvent(mContext, "FinishDown",pkgName);
				
		downApkDb.updateDownloadStatus(pkgName, DownloadStatus.StatusFinish);
		PathTool.copyFile(PathTool.getDownloadingApp(pkgName),
				PathTool.getAppFile(pkgName));
		installApk(pkgName);
		Intent intent = new Intent(StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED);
		intent.putExtra(StaticClass.EXTRA_PACKAGE_NAME, pkgName);
		mContext.sendBroadcast(intent);
	}

	// 下载缩略图完成
	private void downloadThumbFinish(String pkgName) {
		ImageParser par = new ImageParser();
		PathTool.makeDirImage(pkgName);
		boolean result = par.parseThumbFile(
				PathTool.getDownloadingThumb(pkgName),
				PathTool.getThumbFile(pkgName));
		if (result) {
			Intent intent = new Intent(StaticClass.ACTION_THUMB_CHANGED);
			intent.putExtra(StaticClass.EXTRA_PACKAGE_NAME, pkgName);
			mContext.sendBroadcast(intent);
		}
	}

	// 下载预览图完成
	private void downloadPreviewFinish(String pkgName) {
		Log.d(LOG_TAG, "downloadPreviewFinish=" + pkgName);
		ImageParser par = new ImageParser();
		boolean result = par.parsePreviewFile(
				PathTool.getDownloadingPreview(pkgName),
				PathTool.getPreviewDir(pkgName));
		if (result) {
			Intent intent = new Intent(StaticClass.ACTION_PREVIEW_CHANGED);
			intent.putExtra(StaticClass.EXTRA_PACKAGE_NAME, pkgName);
			mContext.sendBroadcast(intent);
		}
	}

	private boolean findApkDownData(String pkgName, DownType type) {
		for (int i = 0; i < downApkList.size(); i++) {
			DownApkNode node = downApkList.get(i);
			if (node.packname.equals(pkgName) && node.downType == type) {
				return true;
			}
		}
		return false;
	}

	public void downloadApk(String pkgName) {
		Log.v(LOG_TAG, "downloadApk=" + pkgName);
		if (!isAllowDownload()) {
			return;
		}

		synchronized (this.syncObject) {
			if (findApkDownData(pkgName, DownType.TYPE_APK_DOWNLOAD)) {
				return;
			}
			if (downApkThread != null && downApkThread.isPackage(pkgName)) {
				return;
			}
			downApkList
					.add(new DownApkNode(pkgName, DownType.TYPE_APK_DOWNLOAD));
			downloadApkStatusUpdate(pkgName, DownloadStatus.StatusDownloading);
			if (downApkThread == null) {
				downApkThread = new DownloadApkThread();
				downApkThread.start();
			}
		}
	}

	public void stopDownApk(String pkgName) {
		Log.v(LOG_TAG, "stopDownApk=" + pkgName);
		synchronized (this.syncObject) {
			for (int i = downApkList.size() - 1; i >= 0; i--) {
				DownApkNode node = downApkList.get(i);
				if (node.packname.equals(pkgName)
						&& node.downType == DownType.TYPE_APK_DOWNLOAD) {
					Log.v(LOG_TAG, "remove array");
					downApkList.remove(i);
				}
			}
			if (downApkThread != null) {
				Log.v(LOG_TAG, "stop apk thread");
				downApkThread.stopApk(pkgName);
			}

			downloadApkStatusUpdate(pkgName, DownloadStatus.StatusPause);
		}
	}

	public void downloadList() {
		Log.v(LOG_TAG, "downloadList");
		if (!isAllowDownload()) {
			return;
		}

		synchronized (this.syncObject) {
			if (downListThread == null) {
				downListThread = new DownloadListThread();
				downListThread.start();
			}
		}
	}

	private boolean isAllowDownload() {
		if (com.coco.theme.themebox.StaticClass.canDownToInternal) {
			return true;
		}
		return StaticClass.isAllowDownload(mContext);
	}

	public void downloadThumb(String pkgName) {
		Log.v(LOG_TAG, "downloadThumb=" + pkgName);
		if (!isAllowDownload()) {
			return;
		}

		synchronized (this.syncObject) {
			if (!findImageDownData(pkgName, DownType.TYPE_IMAGE_THUMB)) {
				downImgList.add(new DownImageNode(pkgName,
						DownType.TYPE_IMAGE_THUMB));
			}
			if (downImageThread == null) {
				downImageThread = new DownloadImageThread();
				downImageThread.start();
			}
		}
	}
	
	public boolean isRefreshList() {
		SharedPreferences sharedPrefer = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		String downListDate = sharedPrefer.getString(StaticClass.HOT_LIST_DATE,
				"");
		Time curTime = new Time();
		String curDateString = curTime.format("yyyyMMdd");
		if (curDateString.equals(downListDate)) {
			return false;
		}
		return true;
	}

	private void saveListTime() {
		Time curTime = new Time();
		String curDateString = curTime.format("yyyyMMdd");
		SharedPreferences sharedPrefer = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		
		Log.v("savelist", "zhuti_zhuti_downmodel time = "+curDateString);
		Editor edit = sharedPrefer.edit();
		edit.putString(StaticClass.HOT_LIST_DATE, curDateString);
		edit.commit();
	}

	public void downloadPreview(String pkgName) {
		Log.v(LOG_TAG, "downloadPreview=" + pkgName);
		if (!isAllowDownload()) {
			return;
		}

		synchronized (this.syncObject) {
			if (findImageDownData(pkgName, DownType.TYPE_IMAGE_PREVIEW)) {
				return;
			}
			if (downImageThread != null && downImageThread.isPackage(pkgName)) {
				return;
			}
			downImgList.add(new DownImageNode(pkgName,
					DownType.TYPE_IMAGE_PREVIEW));
			if (downImageThread == null) {
				downImageThread = new DownloadImageThread();
				downImageThread.start();
			}
		}
	}

	private boolean findImageDownData(String pkgName, DownType type) {
		for (int i = 0; i < downImgList.size(); i++) {
			DownImageNode node = downImgList.get(i);
			if (node.packname.equals(pkgName) && node.downType == type) {
				return true;
			}
		}
		return false;
	}

	private String getPhoneParams() {

		QueryStringBuilder builder = new QueryStringBuilder();
		builder.add("a01", Build.MODEL)
			.add("a02", Build.DISPLAY)		
			.add("a05", Build.PRODUCT).add("a06", Build.DEVICE)
			.add("a07", Build.BOARD).add("a08", Build.MANUFACTURER)
			.add("a09", Build.BRAND).add("a12", Build.HARDWARE)
			.add("a14", Build.VERSION.RELEASE)
			.add("a15", Build.VERSION.SDK_INT);
		
		{
			WindowManager winMgr = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = winMgr.getDefaultDisplay();
			int scrWidth = display.getWidth();
			int scrHeight = display.getHeight();
			
			builder.add("a04", String.format("%dX%d", scrWidth, scrHeight));
		}

		{
			TelephonyManager telMgr = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (telMgr!=null) {
				builder.add("u01", telMgr.getSubscriberId())
					// IMSI
					.add("u03", telMgr.getDeviceId())
					// IMEI
					.add("u04", telMgr.getSimSerialNumber())
					// ICCID
					.add("u05", telMgr.getLine1Number());
			}
		}

		ConnectivityManager connMgr = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if(netInfo != null)	{
			if(netInfo.getTypeName().equals("WIFI"))
			{
				builder.add("u07", "2");				
			}
			else if(netInfo.getTypeName().equals("mobile"))
			{
				builder.add("u07", "1");
			}
		}
		builder.add("p04", getSerialNo(mContext));
		builder.add("a00", getAppId(mContext));
		return builder.toString();
	}

	private static String getSerialNo(Context context) {
		PlatformInfo pi = PlatformInfo.getInstance(context);
		if (pi.isSupportViewLock()) {
			return pi.getChannel();
		}

		JSONObject config = getConfig(context, CONFIG_FILE_NAME);
		if (config == null) {
			return "";
		}
		try {
			JSONObject tmp = config.getJSONObject("config");
			String serialno = tmp.getString("serialno");
			return serialno;
		} catch (JSONException e1) {
			e1.printStackTrace();
			return "";
		}
	}

	private static String getAppId(Context context) {
		JSONObject config = getConfig(context, CONFIG_FILE_NAME);
		if (config == null) {
			return "";
		}
		try {
			JSONObject tmp = config.getJSONObject("config");
			String app_id = tmp.getString("app_id");
			return app_id;
		} catch (JSONException e1) {
			e1.printStackTrace();
			return "";
		}
	}
	
	private static JSONObject getConfig(Context context, String fileName) {
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open(fileName);
			String config = readTextFile(inputStream);

			JSONObject jObject = new JSONObject(config);		
			return jObject;

		} catch (JSONException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e)	{
			return null;
		} finally {
			if (inputStream!=null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static String readTextFile(InputStream inputStream) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1)	{
				outputStream.write(buf, 0, len);
			}
			String result = outputStream.toString();
			return result;
		} catch (IOException e)	{
			e.printStackTrace();
			return "";
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/*
	 * 下载apk的线程
	 */
	private class DownloadApkThread extends Thread {
		private volatile DownApkNode curDownApk;
		private volatile HttpURLConnection urlConn;
		private volatile boolean isExit = false;
		private UrlAddressThemeService urlService = new UrlAddressThemeService(mContext);
		private DownloadThemeService threadDb = new DownloadThemeService(mContext);
		private String getUrlAddress() {
			AddressType aType = AddressType.AddressApp;
			List<String> urlList = urlService.queryAddress(
					ApplicationType.AppTheme, aType);

			if (urlList.size() == 0) {
				Log.d(LOG_TAG, "use default apk url");
				return StaticClass.DEFAULT_APK_URL;
			} else {
				String url = urlList
						.get((int) (Math.random() * urlList.size()));
				return url;
			}
		}
		
		public void stopRun() {
			isExit = true;
			if (urlConn != null) {
				urlConn.disconnect();
			}
			DownApkNode dNode = curDownApk;
			if (dNode!=null) {
				threadDb.updateDownloadStatus(dNode.packname, DownloadStatus.StatusPause);
			}
		}

		public void stopApk(String pkgName) {
			DownApkNode dNode = curDownApk;
			if (dNode != null && pkgName.equals(dNode.packname)) {
				stopRun();
			}
		}

		public boolean isPackage(String pkgName) {
			DownApkNode node = curDownApk;
			if (node != null && node.packname.equals(pkgName)) {
				return true;
			}
			return false;
		}
		
		private long sizeChangeTimeMillis = 0;
		private void downloadApkContinue(String pkgName, int curSize, int totalSize) {
			threadDb.updateDownloadSizeAndStatus(pkgName, curSize, totalSize,
					DownloadStatus.StatusDownloading);
			
			long currentTimeMillis = System.currentTimeMillis();
			if (currentTimeMillis-sizeChangeTimeMillis>0 
					&& currentTimeMillis-sizeChangeTimeMillis<1000) {
				return;
			}

			sizeChangeTimeMillis = currentTimeMillis;
			Intent intent = new Intent(StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED);
			intent.putExtra(StaticClass.EXTRA_PACKAGE_NAME, pkgName);
			intent.putExtra(StaticClass.EXTRA_DOWNLOAD_SIZE, curSize);
			intent.putExtra(StaticClass.EXTRA_TOTAL_SIZE, totalSize);
			mContext.sendBroadcast(intent);
		}
		
		@Override
		public void run() {
			while (true) {
				synchronized (syncObject) {
					if (downApkList.size() == 0) {
						break;
					}
					curDownApk = downApkList.get(0);
					downApkList.remove(0);
				}
				RandomAccessFile fileOut = null;
				InputStream netStream = null;
				boolean isSucceed = false;
				try {
					downloadApkStatusUpdate(curDownApk.packname,
							DownloadStatus.StatusDownloading);

					String sdpath = PathTool
							.getDownloadingApp(curDownApk.packname);
					fileOut = new RandomAccessFile(sdpath, "rw");

					URL url = new URL(getUrlAddress() + "?"
							+ curDownApk.getParams() + "&"
							+ getPhoneParams());
					Log.d(LOG_TAG, "downApk,url=" + url.toString());
					// 创建连接
					urlConn = (HttpURLConnection) url.openConnection();

					if (isExit) {
						break;
					}

					DownloadThemeItem item = threadDb.queryByPackageName(curDownApk.packname);
					int curSize = (int) item.getDownloadSize();
					if (curSize > 0) {
						fileOut.seek(curSize);
						String ranges = String.format("bytes=%d-", curSize);
						urlConn.addRequestProperty("RANGE", ranges);
						Log.d(LOG_TAG, "RANGE:" + ranges);
					}

					urlConn.connect();

					// 获取文件大小
					// int length = conn.getContentLength();
					int totalLength = urlConn.getContentLength();
					if (curSize > 0) {
						totalLength = (int) item.getApplicationSize();
					}

					// 创建输入流
					netStream = urlConn.getInputStream();

					int count = 0;
					// 缓存
					byte buf[] = new byte[1024 * 10];
					// 写入到文件中
					while (true) {
						int numread = netStream.read(buf);
						count += numread;
						// 计算进度条位置
						// int progress = (int) (((float) count / length) *
						// 100);
						if (numread <= 0) {
							// 下载完成
							break;
						}
						// 更新进度
						downloadApkContinue(curDownApk.packname, curSize
								+ count, totalLength);
						// 写入文件
						fileOut.write(buf, 0, numread);
					}
					isSucceed = true;
				} catch (MalformedURLException e) {
					e.printStackTrace();
					//友盟  下载失败统计
					MobclickAgent1(mContext,"ErrorDown",curDownApk.packname);
				} catch (IOException e) {
					e.printStackTrace();
					//友盟  下载失败统计
					MobclickAgent1(mContext,"ErrorDown",curDownApk.packname);
				} finally {
					if (fileOut != null) {
						try {
							fileOut.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						fileOut = null;
					}
					if (netStream != null) {
						try {
							netStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						netStream = null;
					}
					if (urlConn != null) {
						urlConn.disconnect();
						urlConn = null;
					}
				}
				if (isSucceed) {
					downloadApkStatusUpdate(curDownApk.packname,
							DownloadStatus.StatusFinish);
					downloadApkFinish(curDownApk.packname);
				} else {
					downloadApkStatusUpdate(curDownApk.packname,
							DownloadStatus.StatusPause);
				}
			}

			synchronized (syncObject) {
				downApkThread = null;
			}
		}
	}

	/*
	 * 下载图片xml的线程
	 */
	private class DownloadImageThread extends Thread {
		private volatile DownImageNode curDownImage;
		private volatile HttpURLConnection urlConn;
		private volatile boolean isExit = false;
		private UrlAddressThemeService urlService = new UrlAddressThemeService(mContext);

		public void stopRun() {
			isExit = true;
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}

		public boolean isPackage(String pkgName) {
			DownImageNode node = curDownImage;
			if (node != null && node.packname.equals(pkgName)) {
				return true;
			}
			return false;
		}
		
		private String getUrlAddress(DownType downType) {
			AddressType aType = AddressType.AddressPreview;
			if (downType == DownType.TYPE_IMAGE_THUMB) {
				aType = AddressType.AddressThumb;
			}
			List<String> urlList = urlService.queryAddress(
					ApplicationType.AppTheme, aType);

			if (urlList.size() == 0) {
				Log.d(LOG_TAG, "use default image url");
				return StaticClass.DEFAULT_IMAGE_URL;
			} else {
				String url = urlList
						.get((int) (Math.random() * urlList.size()));
				return url;
			}
		}

		@Override
		public void run() {
			while (true) {
				synchronized (syncObject) {
					if (downImgList.size() == 0) {
						break;
					}
					curDownImage = downImgList.get(0);
					downImgList.remove(0);
				}
				FileOutputStream fileOut = null;
				InputStream netStream = null;
				boolean isSucceed = false;
				try {
					String sdpath = null;
					if (curDownImage.downType == DownType.TYPE_IMAGE_THUMB) {
						sdpath = PathTool
								.getDownloadingThumb(curDownImage.packname);
					} else if (curDownImage.downType == DownType.TYPE_IMAGE_PREVIEW) {
						sdpath = PathTool
								.getDownloadingPreview(curDownImage.packname);
					}
					URL url = new URL(getUrlAddress(curDownImage.downType)
							+ "?" + curDownImage.getParams() + "&"
							+ getPhoneParams());
					Log.d(LOG_TAG, "downImage,url=" + url.toString());
					// 创建连接
					urlConn = (HttpURLConnection) url.openConnection();
					urlConn.connect();

					if (isExit) {
						break;
					}
					// 获取文件大小
					// urlConn.getContentLength();
					// 创建输入流
					netStream = urlConn.getInputStream();
					File apkFile = new File(sdpath);
					fileOut = new FileOutputStream(apkFile, false);
					// int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					while (true) {
						int numread = netStream.read(buf);
						// count += numread;
						// 计算进度条位置
						// int progress = (int) (((float) count / length) *
						// 100);
						// 更新进度
						// mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							break;
						}
						// 写入文件
						fileOut.write(buf, 0, numread);
					}
					isSucceed = true;
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (fileOut != null) {
						try {
							fileOut.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						fileOut = null;
					}
					if (netStream != null) {
						try {
							netStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						netStream = null;
					}
					if (urlConn != null) {
						urlConn.disconnect();
						urlConn = null;
					}
				}
				if (isSucceed) {
					if (curDownImage.downType == DownType.TYPE_IMAGE_THUMB) {
						downloadThumbFinish(curDownImage.packname);
					} else if (curDownImage.downType == DownType.TYPE_IMAGE_PREVIEW) {
						downloadPreviewFinish(curDownImage.packname);
					}
				}
			}
			synchronized (syncObject) {
				downImageThread = null;
			}
		}
	}

	/*
	 * 下载列表的线程
	 */
	private class DownloadListThread extends Thread {
		private volatile HttpURLConnection urlConn;
		private volatile boolean isExit = false;

		public DownloadListThread() {
		}

		public void stopRun() {
			isExit = true;
			if (urlConn != null) {
				urlConn.disconnect();
			}
			Log.v(LOG_TAG, "stopRun");
		}

		@Override
		public void run() {
			FileOutputStream fileOut = null;
			InputStream netStream = null;
			boolean isSucceed = false;
			try {
				String sdpath = PathTool.getThemeDownloadingList();
				String listUrl = StaticClass.DEFAULT_LIST_URL + "?p07=2" + "&"
						+ getPhoneParams();
				URL url = new URL(listUrl);
				Log.d(LOG_TAG, "downlist,url=" + url.toString());
				// 创建连接
				urlConn = (HttpURLConnection) url.openConnection();
				urlConn.connect();

				if (isExit) {
					synchronized (syncObject) {
						downListThread = null;
						Log.v(LOG_TAG, "DownloadListThread exit");
						return;
					}
				}
				// 获取文件大小
				int length = urlConn.getContentLength();
				Log.v("********************", "length = " + length);
				Log.d(LOG_TAG, "DownloadListThread len=" + length);
				// 创建输入流
				netStream = urlConn.getInputStream();
				File xmlFile = new File(sdpath);
				Log.v("********************", "sdpath = " + sdpath);
				fileOut = new FileOutputStream(xmlFile);
				int count = 0;
				// 缓存
				byte buf[] = new byte[1024];
				Log.v("********************",
						"4444444444444444444444444444444444");
				// 写入到文件
				while (true) {
					Log.v("********************", "555555555555555555555555");
					int numread = netStream.read(buf);
					count += numread;
					if (numread <= 0) {
						// 下载完成
						break;
					}
					// 写入文件
					fileOut.write(buf, 0, numread);
				}

				Log.d(LOG_TAG, "DownloadListThread count=" + count);
				isSucceed = true;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileOut != null) {
					try {
						fileOut.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					fileOut = null;
				}
				if (netStream != null) {
					try {
						netStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					netStream = null;
				}
				if (urlConn != null) {
					urlConn.disconnect();
					urlConn = null;
				}
			}
			if (isSucceed) {
				downloadListFinish();
			}
			synchronized (syncObject) {
				downListThread = null;
				return;
			}
		}
	}
	
	public void MobclickAgent1(Context mContext2, String string, String packname) {
		MobclickAgent.onEvent(mContext2, string,packname);
		
	}
}
