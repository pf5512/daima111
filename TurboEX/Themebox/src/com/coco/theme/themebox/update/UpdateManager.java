package com.coco.theme.themebox.update;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.coco.download.Assets;
import com.coco.download.CustomerHttpClient;
import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;

public class UpdateManager {

	private static final int SHOW_NOTICE_DIALOG = 1;
	private static final int TOAST_MESSAGE = 2;
	private static final int SHOW_MESSAGE_NEWEST = 3;
	/* 保存解析的XML信息 */
	HashMap<String, Object> mHashMap = new HashMap<String, Object>();
	private Context mContext;
	private Dialog noticeDialog;
	private final String ACTION_LIST = "1320";
	private final String DEFAULT_KEY = "f24657aafcb842b185c98a9d3d7c6f4725f6cc4597c3a4d531c70631f7c7210fd7afd2f8287814f3dfa662ad82d1b02268104e8ab3b2baee13fab062b3d27bff";
	// private final String SERVER_URL_TEST =
	// "http://192.168.1.225/iloong/pui/ServicesEngine/DataService";
	// private final String SERVER_URL_TEST =
	// "http://58.246.135.237:20180/iloong/pui/ServicesEngine/DataService";
	private final String SERVER_URL_TEST = "http://uifolder.coolauncher.com.cn/iloong/pui/ServicesEngine/DataService";
	private Object syncObject = new Object();
	private UpdateApkInfo updateApkInfo = null;
	private boolean isManual = false;
	private ProgressDialog mProgressDialog = null;
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_NOTICE_DIALOG:
				try {
					showNoticeDialog();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case TOAST_MESSAGE:
				if (msg.obj != null) {
					if (mProgressDialog != null && mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
					Toast.makeText(mContext, (String) msg.obj,
							Toast.LENGTH_SHORT).show();
				}
				break;
			case SHOW_MESSAGE_NEWEST:
				if (mProgressDialog != null) {
					if (mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
				}
				showMessageDailog();
				break;
			default:
				break;
			}
		};
	};

	public UpdateManager(Context context) {
		mContext = context;
	}

	private String getMD5EncruptKey(String logInfo) {
		String res = null;
		MessageDigest messagedigest;
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		messagedigest.update(logInfo.getBytes());
		res = bufferToHex(messagedigest.digest());
		// Log.v("http", "getMD5EncruptKey res =  " + res);
		return res;
	}

	protected char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4]; // 取字节中�?4 位的数字转换, >>>
												// 为逻辑右移，将符号位一起右�?此处未发现两种符号有何不�?
		char c1 = hexDigits[bt & 0xf]; // 取字节中�?4 位的数字转换
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	private String getShellID() {
		// return "R001_SHELLTST";
		return "";
	}

	private String getParams(String logText, boolean isAddMd5) {
		String action = null;
		String[] itemsTemp = logText.split("#");
		int len = itemsTemp.length;
		if (len > 0) {
			action = itemsTemp[0];
		}
		String appid = null;
		String sn = null;
		PackageManager pm;
		JSONObject res;
		appid = Assets.getAppId(mContext);
		sn = Assets.getSerialNo(mContext);
		Log.v("downloadlist", "sn = " + sn);
		if (appid == null || sn == null)
			return null;
		pm = mContext.getPackageManager();
		res = new JSONObject();
		try {
			res.put("Action", action);
			if (isAddMd5) {
				res.put("h01", mContext.getPackageName());
				res.put("h02",
						pm.getPackageInfo(mContext.getPackageName(), 0).versionCode);
				res.put("h03",
						pm.getPackageInfo(mContext.getPackageName(), 0).versionName);
				res.put("h04", sn);
				res.put("h05", appid);
				res.put("h06", getShellID());
				TelephonyManager mTelephonyMgr = (TelephonyManager) mContext
						.getSystemService(Context.TELEPHONY_SERVICE);
				res.put("h07", mTelephonyMgr.getSubscriberId() == null ? ""
						: mTelephonyMgr.getSubscriberId());
				res.put("h08", mTelephonyMgr.getSimSerialNumber() == null ? ""
						: mTelephonyMgr.getSimSerialNumber());
				res.put("h09", mTelephonyMgr.getDeviceId() == null ? ""
						: mTelephonyMgr.getDeviceId());
				res.put("h10", mTelephonyMgr.getLine1Number() == null ? ""
						: mTelephonyMgr.getLine1Number());
				JSONObject obj = new JSONObject();
				JSONArray array = new JSONArray();
				obj.put("h01", mContext.getPackageName());
				obj.put("h02",
						pm.getPackageInfo(mContext.getPackageName(), 0).versionCode);
				obj.put("h03",
						pm.getPackageInfo(mContext.getPackageName(), 0).versionName);
				obj.put("h04", sn);
				obj.put("h05", appid);
				obj.put("h11", "com.coco.theme.themebox.MainActivity");
				array.put(0, obj);
				res.put("list", array);
			}
			String content = res.toString();
			String params = content;
			if (isAddMd5) {
				String md5_res = getMD5EncruptKey(content + DEFAULT_KEY);
				// res.put("md5", md5_res);
				String newContent = content.substring(0,
						content.lastIndexOf('}'));
				params = newContent + ",\"md5\":\"" + md5_res + "\"}";
			}
			return params;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// 记录安装时间
	private void recordTime() {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(mContext).edit();
		editor.putLong("selfrefresh", System.currentTimeMillis());
		editor.putLong("updateDelta", (Long) mHashMap.get("uptime"));
		editor.commit();
	}

	public void updateApkInfo(boolean ismanual) {
		if (ismanual) {
			if (mProgressDialog == null) {
				mProgressDialog = new ProgressDialog(mContext);
				mProgressDialog.setMessage(mContext
						.getString(R.string.soft_update_checking));
			}
			if (!mProgressDialog.isShowing()) {
				mProgressDialog.setCancelable(false);
				mProgressDialog.show();
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (mProgressDialog.isShowing()) {
							mProgressDialog.dismiss();
							Toast.makeText(mContext, R.string.internet_err,
									Toast.LENGTH_SHORT).show();
						}
					}
				}, 30 * 1000);
			}
		}
		synchronized (this.syncObject) {
			if (updateApkInfo == null) {
				isManual = ismanual;
				updateApkInfo = new UpdateApkInfo();
				updateApkInfo.start();
			}
		}
	}

	public void showMessageDailog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_no);
		// 更新
		builder.setPositiveButton(R.string.delete_ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		if (noticeDialog != null) {
			noticeDialog = null;
		}
		noticeDialog = builder.create();
		noticeDialog.setCancelable(false);
		noticeDialog.show();
	}

	public void showDialog() {
		if (noticeDialog != null) {
			if (noticeDialog.isShowing()) {
				noticeDialog.dismiss();
				noticeDialog.show();
			}
		}
	}

	/**
	 * 显示软件更新对话框
	 */
	public void showNoticeDialog() {
		// 构造对话框
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		builder.setMessage(mHashMap.get("desc").toString());
		// 更新
		builder.setPositiveButton(R.string.soft_update_updatebtn,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 显示下载对话框
						Intent it = new Intent(mContext, UpdateService.class);
						it.putExtra("url", (String) mHashMap.get("url"));
						mContext.startService(it);
						NotificationManager manager = (NotificationManager) mContext
								.getSystemService(Context.NOTIFICATION_SERVICE);
						manager.cancel(1);
						recordTime();
					}
				});
		// 稍后更新
		builder.setNegativeButton(R.string.soft_update_later,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						recordTime();
					}
				});
		builder.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				System.out.println("builder oncancel");
			}
		});
		if (noticeDialog != null) {
			noticeDialog = null;
		}
		noticeDialog = builder.create();
		noticeDialog.setCancelable(false);
		noticeDialog.show();
	}

	private class UpdateApkInfo extends Thread {

		private volatile boolean isExit = false;

		public UpdateApkInfo() {
		}

		public void stopRun() {
			isExit = true;
		}

		@Override
		public void run() {
			if (!isManual) {
				long currTime = System.currentTimeMillis();
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(mContext);
				long record = preferences.getLong("selfrefresh", 0);
				long delta = preferences.getLong("updateDelta", 0);
				if (currTime - record < delta * 60 * 1000) {
					synchronized (syncObject) {
						updateApkInfo = null;
						return;
					}
				}
			}
			if (Tools.isServiceRunning(mContext,
					"com.coco.theme.themebox.update.UpdateService")) {
				synchronized (syncObject) {
					updateApkInfo = null;
					return;
				}
			}
			String url = SERVER_URL_TEST;
			String params = getParams(ACTION_LIST, true);
			boolean isSucceed = false;
			if (params != null) {
				CustomerHttpClient client = new CustomerHttpClient(mContext);
				String[] res = client.post(url, params);
				if (isExit) {
					synchronized (syncObject) {
						updateApkInfo = null;
						return;
					}
				}
				if (res != null) {
					String content = res[0];
					Log.v("downloadlist", "content = " + content);
					JSONObject json = null;
					try {
						json = new JSONObject(content);
						int retCode = json.getInt("recode");
						mHashMap.clear();
						mHashMap.put("uptime", json.getLong("uptime"));
						if (retCode == 0) {
							JSONArray array = json.getJSONArray("list");
							JSONObject obj = (JSONObject) array.get(0);
							mHashMap.put("h01", obj.getString("h01"));
							mHashMap.put("url", obj.getString("url"));
							mHashMap.put("size", obj.getLong("size"));
							mHashMap.put("desc", obj.getString("desc"));
							isSucceed = true;
						} else {
							if (isManual) {
								mHandler.sendEmptyMessage(SHOW_MESSAGE_NEWEST);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {// 没有获取到数据，网络不稳定
					if (isManual) {
						Message msg = new Message();
						msg.what = TOAST_MESSAGE;
						msg.obj = mContext.getString(R.string.internet_unusual);
						mHandler.sendMessage(msg);
					}
				}
			}
			if (isSucceed) {
				mHandler.sendEmptyMessage(SHOW_NOTICE_DIALOG);
			}
			synchronized (syncObject) {
				updateApkInfo = null;
				return;
			}
		}
	}
}
