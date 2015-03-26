package com.coco.lock2.lockbox.util;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.coco.lock2.lockbox.LockInformation;
import com.coco.lock2.lockbox.PlatformInfo;
import com.coco.lock2.lockbox.StaticClass;
import com.coco.lock2.lockbox.database.model.AddressType;
import com.coco.lock2.lockbox.database.model.ApplicationType;
import com.coco.lock2.lockbox.database.model.DownloadLockItem;
import com.coco.lock2.lockbox.database.model.DownloadStatus;
import com.coco.lock2.lockbox.database.remoting.ClassicLockService;
import com.coco.lock2.lockbox.database.remoting.DownloadLockService;
import com.coco.lock2.lockbox.database.remoting.HotLockService;
import com.coco.lock2.lockbox.database.remoting.SimpleLockService;
import com.coco.lock2.lockbox.database.remoting.UrlAddressService;
import com.coco.theme.themebox.service.ThemesDB;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import com.coco.theme.themebox.util.Log;
import android.view.Display;
import android.view.WindowManager;


public class DownModule
{
	
	private final static String LOG_TAG = "DownModule";
	private Context mContext;
	private DownloadImageThread downImageThread = null;
	private DownloadApkThread downApkThread = null;
	private DownloadListThread downListThread = null;
	private List<DownImageNode> downImgList = new ArrayList<DownImageNode>();
	private List<DownApkNode> downApkList = new ArrayList<DownApkNode>();
	private DownloadLockService downApkDb;
	private Object syncObject = new Object();
	public static final String CONFIG_FILE_NAME = "config.ini";
	
	public DownModule(
			Context context )
	{
		mContext = context;
		downApkDb = new DownloadLockService( context );
	}
	
	public void dispose()
	{
		Log.d( LOG_TAG , "dispose" );
		synchronized( this.syncObject )
		{
			downImgList.clear();
			synchronized( downApkDb )
			{
				for( DownApkNode node : downApkList )
				{
					downApkDb.updateDownloadStatus( node.packname , DownloadStatus.StatusPause );
				}
			}
			downApkList.clear();
			if( downImageThread != null )
			{
				downImageThread.stopRun();
				downImageThread = null;
			}
			if( downApkThread != null )
			{
				downApkThread.stopRun();
				downApkThread = null;
			}
			if( downListThread != null )
			{
				downListThread.stopRun();
				downListThread = null;
			}
		}
	}
	
	public void stopDownlist()
	{
		if( downListThread != null )
			downListThread.stopRun();
	}
	
	// 安装apk
	public void installApk(
			String pkgName )
	{
		String filepath = PathTool.getAppFile( pkgName );
		File file = new File( filepath );
		Log.e( "OpenFile" , file.getName() );
		Intent intent = new Intent();
		intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent.setAction( android.content.Intent.ACTION_VIEW );
		intent.setDataAndType( Uri.fromFile( file ) , "application/vnd.android.package-archive" );
		mContext.startActivity( intent );
	}
	
	// 下载列表完成
	private void downloadListFinish()
	{
		XmlParser parserXml = new XmlParser( mContext );
		if( !parserXml.parseList( PathTool.getDownloadingList() ) )
		{
			Intent intent = new Intent( StaticClass.ACTION_HOTLIST_NOCHANGED );
			mContext.sendBroadcast( intent );
			return;
		}
		HotLockService sv = new HotLockService( mContext );
		sv.clearTable();
		sv.batchInsert( parserXml.getLockList() );
		SimpleLockService si = new SimpleLockService( mContext );
		si.clearTable();
		si.batchInsert( parserXml.getSimpleList() );
		ClassicLockService cl = new ClassicLockService( mContext );
		cl.clearTable();
		cl.batchInsert( parserXml.getClassicList() );
		UrlAddressService uas = new UrlAddressService( mContext );
		uas.deleteAddress( ApplicationType.AppLock , AddressType.AddressThumb );
		uas.batchInsertAddress( ApplicationType.AppLock , AddressType.AddressThumb , parserXml.getPictureAddress() );
		uas.deleteAddress( ApplicationType.AppLock , AddressType.AddressPreview );
		uas.batchInsertAddress( ApplicationType.AppLock , AddressType.AddressPreview , parserXml.getPictureAddress() );
		uas.deleteAddress( ApplicationType.AppLock , AddressType.AddressApp );
		uas.batchInsertAddress( ApplicationType.AppLock , AddressType.AddressApp , parserXml.getApplicationAddress() );
		saveListTime();
		Intent intent = new Intent( StaticClass.ACTION_HOTLIST_CHANGED );
		mContext.sendBroadcast( intent );
	}
	
	// private long sizeChangeTimeMillis = 0;
	// private void downloadApkContinue(String pkgName, int curSize, int
	// totalSize) {
	// downApkDb.updateDownloadSizeAndStatus(pkgName, curSize, totalSize,
	// DownloadStatus.StatusDownloading);
	//
	// long currentTimeMillis = System.currentTimeMillis();
	// if (currentTimeMillis-sizeChangeTimeMillis>0
	// && currentTimeMillis-sizeChangeTimeMillis<1000) {
	// return;
	// }
	// sizeChangeTimeMillis = currentTimeMillis;
	//
	// Intent intent = new Intent(StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED);
	// intent.putExtra(StaticClass.EXTRA_PACKAGE_NAME, pkgName);
	// intent.putExtra(StaticClass.EXTRA_DOWNLOAD_SIZE, curSize);
	// intent.putExtra(StaticClass.EXTRA_TOTAL_SIZE, totalSize);
	// mContext.sendBroadcast(intent);
	// }
	public void downloadApkStatusUpdate(
			String pkgName ,
			DownloadStatus status )
	{
		synchronized( downApkDb )
		{
			downApkDb.updateDownloadStatus( pkgName , status );
		}
		Intent intent = new Intent( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED );
		intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , pkgName );
		mContext.sendBroadcast( intent );
	}
	
	public List<LockInformation> queryInstallList()
	{
		Intent intentLockView = new Intent( StaticClass.ACTION_LOCK_VIEW );
		intentLockView.addCategory( Intent.CATEGORY_INFO );
		List<ResolveInfo> infoList = mContext.getPackageManager().queryIntentActivities( intentLockView , 0 );
		List<LockInformation> result = new ArrayList<LockInformation>();
		for( ResolveInfo info : infoList )
		{
			LockInformation infor = new LockInformation();
			infor.setActivity( mContext , info.activityInfo );
			result.add( infor );
		}
		return result;
	}
	
	public List<LockInformation> queryDownFinishList()
	{
		Map<String , LockInformation> allMap = new HashMap<String , LockInformation>();
		Map<String , LockInformation> installMap = new HashMap<String , LockInformation>();
		DownloadLockService dSv = new DownloadLockService( mContext );
		List<DownloadLockItem> downlist = dSv.queryTable();
		List<LockInformation> installlist = queryInstallList();
		for( DownloadLockItem item : downlist )
		{
			LockInformation infor = new LockInformation();
			infor.setDownloadItem( item );
			if( infor.getDownloadStatus() == DownloadStatus.StatusFinish )
			{
				allMap.put( infor.getPackageName() , infor );
			}
		}
		for( LockInformation item : installlist )
		{
			installMap.put( item.getPackageName() , item );
		}
		for( LockInformation item : installlist )
		{
			if( allMap.containsKey( item.getPackageName() ) )
			{
				allMap.remove( item.getPackageName() );
			}
		}
		List<LockInformation> result = new ArrayList<LockInformation>();
		result.addAll( allMap.values() );
		return result;
	}
	
	private void downloadApkFinish(
			String pkgName )
	{
		synchronized( downApkDb )
		{
			downApkDb.updateDownloadStatus( pkgName , DownloadStatus.StatusFinish );
		}
		PathTool.copyFile( PathTool.getDownloadingApp( pkgName ) , PathTool.getAppFile( pkgName ) );
		Intent intent = new Intent( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED );
		intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , pkgName );
		mContext.sendBroadcast( intent );
	}
	
	// 下载缩略图完�?
	private void downloadThumbFinish(
			String pkgName )
	{
		ImageParser par = new ImageParser();
		PathTool.makeDirImage( pkgName );
		boolean result = par.parseThumbFile( PathTool.getDownloadingThumb( pkgName ) , PathTool.getThumbFile( pkgName ) );
		if( result )
		{
			Intent intent = new Intent( StaticClass.ACTION_THUMB_CHANGED );
			intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , pkgName );
			mContext.sendBroadcast( intent );
		}
	}
	
	// 下载预览图完�?
	private void downloadPreviewFinish(
			String pkgName )
	{
		Log.d( LOG_TAG , "downloadPreviewFinish=" + pkgName );
		ImageParser par = new ImageParser();
		boolean result = par.parsePreviewFile( PathTool.getDownloadingPreview( pkgName ) , PathTool.getPreviewDir( pkgName ) );
		if( result )
		{
			Intent intent = new Intent( StaticClass.ACTION_PREVIEW_CHANGED );
			intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , pkgName );
			mContext.sendBroadcast( intent );
		}
	}
	
	private boolean findApkDownData(
			String pkgName ,
			DownType type )
	{
		for( int i = 0 ; i < downApkList.size() ; i++ )
		{
			DownApkNode node = downApkList.get( i );
			if( node.packname.equals( pkgName ) && node.downType == type )
			{
				return true;
			}
		}
		return false;
	}
	
	public void downloadApk(
			String pkgName )
	{
		Log.v( LOG_TAG , "downloadApk=" + pkgName );
		if( !isAllowDownload() )
		{
			return;
		}
		synchronized( this.syncObject )
		{
			if( findApkDownData( pkgName , DownType.TYPE_APK_DOWNLOAD ) )
			{
				return;
			}
			if( downApkThread != null && downApkThread.isPackage( pkgName ) )
			{
				return;
			}
			downApkList.add( new DownApkNode( pkgName , DownType.TYPE_APK_DOWNLOAD ) );
			downloadApkStatusUpdate( pkgName , DownloadStatus.StatusDownloading );
			if( downApkThread == null )
			{
				downApkThread = new DownloadApkThread();
				downApkThread.start();
			}
		}
	}
	
	public void stopDownApk(
			String pkgName )
	{
		Log.v( LOG_TAG , "stopDownApk=" + pkgName );
		synchronized( this.syncObject )
		{
			for( int i = downApkList.size() - 1 ; i >= 0 ; i-- )
			{
				DownApkNode node = downApkList.get( i );
				if( node.packname.equals( pkgName ) && node.downType == DownType.TYPE_APK_DOWNLOAD )
				{
					Log.v( LOG_TAG , "remove array" );
					downApkList.remove( i );
				}
			}
			if( downApkThread != null )
			{
				Log.v( LOG_TAG , "stop apk thread" );
				downApkThread.stopApk( pkgName );
			}
			downloadApkStatusUpdate( pkgName , DownloadStatus.StatusPause );
		}
	}
	
	public void downloadList()
	{
		Log.v( LOG_TAG , "downloadList" );
		if( !isAllowDownload() )
		{
			return;
		}
		synchronized( this.syncObject )
		{
			if( downListThread == null )
			{
				downListThread = new DownloadListThread();
				downListThread.start();
			}
		}
	}
	
	private boolean isAllowDownload()
	{
		return StaticClass.isAllowDownload( mContext );
	}
	
	public void downloadThumb(
			String pkgName )
	{
		Log.v( LOG_TAG , "downloadThumb=" + pkgName );
		if( !isAllowDownload() )
		{
			return;
		}
		synchronized( this.syncObject )
		{
			if( !findImageDownData( pkgName , DownType.TYPE_IMAGE_THUMB ) )
			{
				downImgList.add( new DownImageNode( pkgName , DownType.TYPE_IMAGE_THUMB ) );
			}
			if( downImageThread == null )
			{
				downImageThread = new DownloadImageThread();
				downImageThread.start();
			}
		}
	}
	
	public boolean isRefreshList()
	{
		SharedPreferences sharedPrefer = PreferenceManager.getDefaultSharedPreferences( mContext );
		String downListDate = sharedPrefer.getString( StaticClass.HOT_LIST_DATE , "" );
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd" );
		String curDateString = sdf.format( new Date() );
		String down = "";
		if( downListDate != "" && downListDate.length() > 8 )
		{
			down = downListDate.substring( 0 , 8 );
		}
		Log.v( "themecreate" , "curDateString = " + curDateString + "    down = " + downListDate );
		if( curDateString.equals( down ) )
		{
			return false;
		}
		return true;
	}
	
	private void saveListTime()
	{
		Time curTime = new Time();
		String curDateString = curTime.format( "yyyyMMddkk" );
		Log.v( "savelist" , "zhuti_suoping_downmodel time = " + curDateString );
		SharedPreferences sharedPrefer = PreferenceManager.getDefaultSharedPreferences( mContext );
		Editor edit = sharedPrefer.edit();
		edit.putString( StaticClass.HOT_LIST_DATE , curDateString );
		edit.commit();
	}
	
	public void downloadPreview(
			String pkgName )
	{
		Log.v( LOG_TAG , "downloadPreview=" + pkgName );
		if( !isAllowDownload() )
		{
			return;
		}
		synchronized( this.syncObject )
		{
			if( findImageDownData( pkgName , DownType.TYPE_IMAGE_PREVIEW ) )
			{
				return;
			}
			if( downImageThread != null && downImageThread.isPackage( pkgName ) )
			{
				return;
			}
			downImgList.add( new DownImageNode( pkgName , DownType.TYPE_IMAGE_PREVIEW ) );
			if( downImageThread == null )
			{
				downImageThread = new DownloadImageThread();
				downImageThread.start();
			}
		}
	}
	
	private boolean findImageDownData(
			String pkgName ,
			DownType type )
	{
		for( int i = 0 ; i < downImgList.size() ; i++ )
		{
			DownImageNode node = downImgList.get( i );
			if( node.packname.equals( pkgName ) && node.downType == type )
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取手机的其他信�?
	 */
	private static String getPhoneParams(
			Context context )
	{
		QueryStringBuilder builder = new QueryStringBuilder();
		builder.add( "a01" , Build.MODEL ).add( "a02" , Build.DISPLAY ).add( "a05" , Build.PRODUCT ).add( "a06" , Build.DEVICE ).add( "a07" , Build.BOARD ).add( "a08" , Build.MANUFACTURER )
				.add( "a09" , Build.BRAND ).add( "a12" , Build.HARDWARE ).add( "a14" , Build.VERSION.RELEASE ).add( "a15" , Build.VERSION.SDK_INT );
		{
			WindowManager winMgr = (WindowManager)context.getSystemService( Context.WINDOW_SERVICE );
			Display display = winMgr.getDefaultDisplay();
			int scrWidth = display.getWidth();
			int scrHeight = display.getHeight();
			builder.add( "a04" , String.format( "%dX%d" , scrWidth , scrHeight ) );
		}
		{
			TelephonyManager telMgr = (TelephonyManager)context.getSystemService( Context.TELEPHONY_SERVICE );
			if( telMgr != null )
			{
				builder.add( "u01" , telMgr.getSubscriberId() )
				// IMSI
						.add( "u03" , telMgr.getDeviceId() )
						// IMEI
						.add( "u04" , telMgr.getSimSerialNumber() )
						// ICCID
						.add( "u05" , telMgr.getLine1Number() );
			}
		}
		ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if( netInfo != null )
		{
			if( netInfo.getTypeName().equals( "WIFI" ) )
			{
				builder.add( "u07" , "2" );
			}
			else if( netInfo.getTypeName().equals( "mobile" ) )
			{
				builder.add( "u07" , "1" );
			}
		}
		builder.add( "p04" , getSerialNo( context ) );
		builder.add( "a00" , getAppId( context ) );
		return builder.toString();
	}
	
	public static String getApkUrl(
			Context context ,
			String srcPackageName ,
			String destPackageName )
	{
		String apkUrl = StaticClass.DEFAULT_APK_URL;
		Log.d( LOG_TAG , "apkUrl=" + apkUrl );
		String result = String.format( "%s?p01=%s&p06=1&p07=%s&%s" , apkUrl , destPackageName , srcPackageName , getPhoneParams( context ) );
		Log.d( LOG_TAG , "lockUrl=" + result );
		return result;
	}
	
	/*
	 * 下载apk的线�?
	 */
	private class DownloadApkThread extends Thread
	{
		
		private volatile DownApkNode curDownApk;
		private volatile HttpURLConnection urlConn;
		private volatile boolean isExit = false;
		private UrlAddressService urlService = new UrlAddressService( mContext );
		private DownloadLockService threadDb = new DownloadLockService( mContext );
		
		private String getUrlAddress()
		{
			AddressType aType = AddressType.AddressApp;
			List<String> urlList = urlService.queryAddress( ApplicationType.AppLock , aType );
			if( urlList.size() == 0 )
			{
				Log.d( LOG_TAG , "use default apk url" );
				return StaticClass.DEFAULT_APK_URL;
			}
			else
			{
				String url = urlList.get( (int)( Math.random() * urlList.size() ) );
				return url;
			}
		}
		
		public void stopRun()
		{
			isExit = true;
			if( urlConn != null )
			{
				urlConn.disconnect();
			}
			DownApkNode dNode = curDownApk;
			if( dNode != null )
			{
				threadDb.updateDownloadStatus( dNode.packname , DownloadStatus.StatusPause );
			}
		}
		
		public void stopApk(
				String pkgName )
		{
			DownApkNode dNode = curDownApk;
			if( dNode != null && pkgName.equals( dNode.packname ) )
			{
				stopRun();
			}
		}
		
		public boolean isPackage(
				String pkgName )
		{
			DownApkNode node = curDownApk;
			if( node != null && node.packname.equals( pkgName ) )
			{
				return true;
			}
			return false;
		}
		
		private long sizeChangeTimeMillis = 0;
		
		private void downloadApkContinue(
				String pkgName ,
				int curSize ,
				int totalSize )
		{
			threadDb.updateDownloadSizeAndStatus( pkgName , curSize , totalSize , DownloadStatus.StatusDownloading );
			long currentTimeMillis = System.currentTimeMillis();
			if( currentTimeMillis - sizeChangeTimeMillis > 0 && currentTimeMillis - sizeChangeTimeMillis < 1000 )
			{
				return;
			}
			sizeChangeTimeMillis = currentTimeMillis;
			Intent intent = new Intent( StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED );
			intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , pkgName );
			intent.putExtra( StaticClass.EXTRA_DOWNLOAD_SIZE , curSize );
			intent.putExtra( StaticClass.EXTRA_TOTAL_SIZE , totalSize );
			mContext.sendBroadcast( intent );
		}
		
		@Override
		public void run()
		{
			while( true )
			{
				synchronized( syncObject )
				{
					if( downApkList.size() == 0 )
					{
						break;
					}
					curDownApk = downApkList.get( 0 );
					downApkList.remove( 0 );
				}
				RandomAccessFile fileOut = null;
				InputStream netStream = null;
				boolean isSucceed = false;
				try
				{
					downloadApkStatusUpdate( curDownApk.packname , DownloadStatus.StatusDownloading );
					String sdpath = PathTool.getDownloadingApp( curDownApk.packname );
					fileOut = new RandomAccessFile( sdpath , "rw" );
					URL url = new URL( getUrlAddress() + "?" + curDownApk.getParams() + "&" + "p07=" + "com.coco.lock2.lockbox" + "&" + getPhoneParams( mContext ) );
					Log.d( LOG_TAG , "downApk,url=" + url.toString() );
					// 创建连接
					urlConn = (HttpURLConnection)url.openConnection();
					if( isExit )
					{
						break;
					}
					DownloadLockItem item = threadDb.queryByPackageName( curDownApk.packname );
					int curSize = (int)item.getDownloadSize();
					if( curSize > 0 )
					{
						fileOut.seek( curSize );
						String ranges = String.format( "bytes=%d-" , curSize );
						urlConn.addRequestProperty( "RANGE" , ranges );
						Log.d( LOG_TAG , "RANGE:" + ranges );
					}
					urlConn.connect();
					// 获取文件大小
					// int length = conn.getContentLength();
					int totalLength = urlConn.getContentLength();
					if( curSize > 0 )
					{
						totalLength = (int)item.getApplicationSize();
					}
					// 创建输入�?
					netStream = urlConn.getInputStream();
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024 * 10];
					// 写入到文件中
					while( true )
					{
						int numread = netStream.read( buf );
						count += numread;
						// 计算进度条位�?
						// int progress = (int) (((float) count / length) *
						// 100);
						if( numread <= 0 )
						{
							// 下载完成
							break;
						}
						// 更新进度
						downloadApkContinue( curDownApk.packname , curSize + count , totalLength );
						// 写入文件
						fileOut.write( buf , 0 , numread );
					}
					isSucceed = true;
				}
				catch( MalformedURLException e )
				{
					e.printStackTrace();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				finally
				{
					if( fileOut != null )
					{
						try
						{
							fileOut.close();
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
						fileOut = null;
					}
					if( netStream != null )
					{
						try
						{
							netStream.close();
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
						netStream = null;
					}
					if( urlConn != null )
					{
						urlConn.disconnect();
						urlConn = null;
					}
				}
				if( isSucceed )
				{
					downloadApkStatusUpdate( curDownApk.packname , DownloadStatus.StatusFinish );
					downloadApkFinish( curDownApk.packname );
				}
				else
				{
					downloadApkStatusUpdate( curDownApk.packname , DownloadStatus.StatusPause );
				}
			}
			synchronized( syncObject )
			{
				downApkThread = null;
			}
		}
	}
	
	/*
	 * 下载图片xml的线�?
	 */
	private class DownloadImageThread extends Thread
	{
		
		private volatile DownImageNode curDownImage;
		private volatile HttpURLConnection urlConn;
		private volatile boolean isExit = false;
		private UrlAddressService urlService = new UrlAddressService( mContext );
		
		public void stopRun()
		{
			isExit = true;
			if( urlConn != null )
			{
				urlConn.disconnect();
			}
		}
		
		public boolean isPackage(
				String pkgName )
		{
			DownImageNode node = curDownImage;
			if( node != null && node.packname.equals( pkgName ) )
			{
				return true;
			}
			return false;
		}
		
		private String getUrlAddress(
				DownType downType )
		{
			AddressType aType = AddressType.AddressPreview;
			if( downType == DownType.TYPE_IMAGE_THUMB )
			{
				aType = AddressType.AddressThumb;
			}
			List<String> urlList = urlService.queryAddress( ApplicationType.AppLock , aType );
			if( urlList.size() == 0 )
			{
				Log.d( LOG_TAG , "use default image url" );
				return StaticClass.DEFAULT_IMAGE_URL;
			}
			else
			{
				String url = urlList.get( (int)( Math.random() * urlList.size() ) );
				return url;
			}
		}
		
		@Override
		public void run()
		{
			while( true )
			{
				synchronized( syncObject )
				{
					if( downImgList.size() == 0 )
					{
						break;
					}
					curDownImage = downImgList.get( 0 );
					downImgList.remove( 0 );
				}
				FileOutputStream fileOut = null;
				InputStream netStream = null;
				boolean isSucceed = false;
				try
				{
					String sdpath = null;
					if( curDownImage.downType == DownType.TYPE_IMAGE_THUMB )
					{
						sdpath = PathTool.getDownloadingThumb( curDownImage.packname );
					}
					else if( curDownImage.downType == DownType.TYPE_IMAGE_PREVIEW )
					{
						sdpath = PathTool.getDownloadingPreview( curDownImage.packname );
					}
					URL url = new URL( getUrlAddress( curDownImage.downType ) + "?p07=" + "com.coco.lock2.lockbox" + "&" + curDownImage.getParams() + "&" + getPhoneParams( mContext ) );
					Log.d( LOG_TAG , "downImage,url=" + url.toString() );
					// 创建连接
					urlConn = (HttpURLConnection)url.openConnection();
					urlConn.connect();
					if( isExit )
					{
						break;
					}
					// 获取文件大小
					// urlConn.getContentLength();
					// 创建输入�?
					netStream = urlConn.getInputStream();
					File apkFile = new File( sdpath );
					fileOut = new FileOutputStream( apkFile , false );
					// int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					while( true )
					{
						int numread = netStream.read( buf );
						if( numread <= 0 )
						{
							break;
						}
						// 写入文件
						fileOut.write( buf , 0 , numread );
					}
					isSucceed = true;
				}
				catch( MalformedURLException e )
				{
					e.printStackTrace();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				finally
				{
					if( fileOut != null )
					{
						try
						{
							fileOut.close();
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
						fileOut = null;
					}
					if( netStream != null )
					{
						try
						{
							netStream.close();
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
						netStream = null;
					}
					if( urlConn != null )
					{
						urlConn.disconnect();
						urlConn = null;
					}
				}
				if( isSucceed )
				{
					if( curDownImage.downType == DownType.TYPE_IMAGE_THUMB )
					{
						downloadThumbFinish( curDownImage.packname );
					}
					else if( curDownImage.downType == DownType.TYPE_IMAGE_PREVIEW )
					{
						downloadPreviewFinish( curDownImage.packname );
					}
				}
			}
			synchronized( syncObject )
			{
				downImageThread = null;
			}
		}
	}
	
	private static String readTextFile(
			InputStream inputStream )
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		try
		{
			while( ( len = inputStream.read( buf ) ) != -1 )
			{
				outputStream.write( buf , 0 , len );
			}
			String result = outputStream.toString();
			return result;
		}
		catch( IOException e )
		{
			e.printStackTrace();
			return "";
		}
		finally
		{
			try
			{
				outputStream.close();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	private static JSONObject getConfig(
			Context context ,
			String fileName )
	{
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;
		try
		{
			inputStream = assetManager.open( fileName );
			String config = readTextFile( inputStream );
			JSONObject jObject = new JSONObject( config );
			return jObject;
		}
		catch( JSONException e1 )
		{
			e1.printStackTrace();
			return null;
		}
		catch( IOException e )
		{
			return null;
		}
		finally
		{
			if( inputStream != null )
			{
				try
				{
					inputStream.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private static String getSerialNo(
			Context context )
	{
		PlatformInfo pi = PlatformInfo.getInstance( context );
		if( pi.isSupportViewLock() )
		{
			return pi.getChannel();
		}
		JSONObject config = getConfig( context , CONFIG_FILE_NAME );
		if( config == null )
		{
			return "";
		}
		try
		{
			JSONObject tmp = config.getJSONObject( "config" );
			String serialno = tmp.getString( "serialno" );
			return serialno;
		}
		catch( JSONException e1 )
		{
			e1.printStackTrace();
			return "";
		}
	}
	
	private static String getAppId(
			Context context )
	{
		JSONObject config = getConfig( context , CONFIG_FILE_NAME );
		if( config == null )
		{
			return "";
		}
		try
		{
			JSONObject tmp = config.getJSONObject( "config" );
			String app_id = tmp.getString( "app_id" );
			return app_id;
		}
		catch( JSONException e1 )
		{
			e1.printStackTrace();
			return "";
		}
	}
	
	/*
	 * 下载列表的线�?
	 */
	private class DownloadListThread extends Thread
	{
		
		private volatile HttpURLConnection urlConn;
		private volatile boolean isExit = false;
		
		public void stopRun()
		{
			isExit = true;
			if( urlConn != null )
			{
				urlConn.disconnect();
			}
		}
		
		@Override
		public void run()
		{
			FileOutputStream fileOut = null;
			InputStream netStream = null;
			boolean isSucceed = false;
			try
			{
				String sdpath = null;
				sdpath = PathTool.getDownloadingList();
				SharedPreferences sharedPrefer = PreferenceManager.getDefaultSharedPreferences( mContext );
				String ver = sharedPrefer.getString( StaticClass.LIST_VER , "" );
				URL url = null;
				if( ver != "" )
				{
					url = new URL( "http://yu01.coomoe.com/ui02/getapplist.ashx?p07=com.coco.lock2.lockbox" + "&p08=" + ver + "&" + getPhoneParams( mContext ) );
				}
				else
				{
					url = new URL( "http://yu01.coomoe.com/ui02/getapplist.ashx?p07=com.coco.lock2.lockbox" + "&" + getPhoneParams( mContext ) );
				}
				Log.d( LOG_TAG , "downlist,url=" + url.toString() );
				// 创建连接
				urlConn = (HttpURLConnection)url.openConnection();
				urlConn.connect();
				if( isExit )
				{
					synchronized( syncObject )
					{
						downListThread = null;
						return;
					}
				}
				// 获取文件大小
				int length = urlConn.getContentLength();
				Log.d( LOG_TAG , "DownloadListThread len=" + length );
				// 创建输入�?
				netStream = urlConn.getInputStream();
				File xmlFile = new File( sdpath );
				fileOut = new FileOutputStream( xmlFile );
				int count = 0;
				// 缓存
				byte buf[] = new byte[1024];
				// 写入到文�?
				while( true )
				{
					int numread = netStream.read( buf );
					count += numread;
					if( numread <= 0 )
					{
						// 下载完成
						break;
					}
					// 写入文件
					fileOut.write( buf , 0 , numread );
				}
				Log.d( LOG_TAG , "DownloadListThread count=" + count );
				isSucceed = true;
			}
			catch( MalformedURLException e )
			{
				e.printStackTrace();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			finally
			{
				if( fileOut != null )
				{
					try
					{
						fileOut.close();
					}
					catch( IOException e )
					{
						e.printStackTrace();
					}
					fileOut = null;
				}
				if( netStream != null )
				{
					try
					{
						netStream.close();
					}
					catch( IOException e )
					{
						e.printStackTrace();
					}
					netStream = null;
				}
				if( urlConn != null )
				{
					urlConn.disconnect();
					urlConn = null;
				}
			}
			if( isSucceed )
			{
				downloadListFinish();
			}
			synchronized( syncObject )
			{
				downListThread = null;
				return;
			}
		}
	}
}
