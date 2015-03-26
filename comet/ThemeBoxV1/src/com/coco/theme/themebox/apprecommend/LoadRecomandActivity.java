package com.coco.theme.themebox.apprecommend;

import com.coco.lock2.lockbox.util.QueryStringBuilder;
import com.iLoong.base.themebox.R;
import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class LoadRecomandActivity extends Activity {
	private Dialog noticeDialog;
	// private int urltype = -1;
	private String name;
	private String mUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		mUrl = intent.getStringExtra("apkurl");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
//		showNoticeDialog();
		Intent intent1 = new Intent();
		intent1.setClass(LoadRecomandActivity.this,
				DownloadRecomdService.class);
		String fileName = getResources()
				.getString(
						R.string.server_downloadRecommend_file_name,
						name);
		intent1.putExtra(
				DownloadRecomdService.DOWNLOAD_FILE_NAME,
				fileName);
		String url = getApkUrl();
		intent1.putExtra(DownloadRecomdService.DOWNLOAD_URL_KEY,
				url);
		startService(intent1);
		finish();
	}

	/**
	 * 获取APK下载的url
	 */
	private String getApkUrl() {
		String url = mUrl + getPhoneParams();
		return url;
	}

	/**
	 * 获取手机的其他信�?
	 */

	private String getPhoneParams() {

		QueryStringBuilder builder = new QueryStringBuilder();
		builder.add("a01", Build.MODEL).add("a02", Build.DISPLAY)
				.add("a05", Build.PRODUCT).add("a06", Build.DEVICE)
				.add("a07", Build.BOARD).add("a08", Build.MANUFACTURER)
				.add("a09", Build.BRAND).add("a12", Build.HARDWARE)
				.add("a14", Build.VERSION.RELEASE)
				.add("a15", Build.VERSION.SDK_INT);

		{
			WindowManager winMgr = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			Display display = winMgr.getDefaultDisplay();
			int scrWidth = display.getWidth();
			int scrHeight = display.getHeight();

			builder.add("a04", String.format("%dX%d", scrWidth, scrHeight));
		}

		{
			TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			if (telMgr != null) {
				builder.add("u01", telMgr.getSubscriberId())
				// IMSI
						.add("u03", telMgr.getDeviceId())
						// IMEI
						.add("u04", telMgr.getSimSerialNumber())
						// ICCID
						.add("u05", telMgr.getLine1Number());
			}
		}

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if (netInfo != null) {
			// 本机号码
			builder.add("u07", netInfo.getTypeName());
		}

		return builder.toString();
	}

	/**
	 * 显示软件更新对话�?
	 */
//	public void showNoticeDialog() {
//		// 构�?对话�?
//		AlertDialog.Builder builder = new Builder(this);
//		builder.setTitle(getString(R.string.downloadRecommend_title,
//				name));
//		builder.setMessage(getString(R.string.downloadRecommend_info,
//				name));
//		// 更新
//		builder.setPositiveButton(R.string.download,
//				new OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						Intent intent = new Intent();
//						intent.setClass(LoadRecomandActivity.this,
//								DownloadRecomdService.class);
//						String fileName = getResources()
//								.getString(
//										R.string.server_downloadRecommend_file_name,
//										name);
//						intent.putExtra(
//								DownloadRecomdService.DOWNLOAD_FILE_NAME,
//								fileName);
//						String url = getApkUrl();
//						intent.putExtra(DownloadRecomdService.DOWNLOAD_URL_KEY,
//								url);
//						startService(intent);
//						finish();
//					}
//				});
//		// 稍后更新
//		builder.setNegativeButton(R.string.download_later,
//				new OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						finish();
//					}
//				});
//		builder.setOnCancelListener(new OnCancelListener() {
//			public void onCancel(DialogInterface dialog) {
//				dialog.dismiss();
//				finish();
//			}
//		});
//		noticeDialog = builder.create();
//		noticeDialog.show();
//	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public static int getProxyPort(Context context) {
		return Proxy.getPort(context);
	}

	public static boolean isCWWAPConnect(Context context) {
		boolean result = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
			if (Proxy.getDefaultHost() != null
					|| Proxy.getHost(context) != null) {
				result = true;
			}
		}
		return result;
	}

	public static int getNetWorkType(Context context) {
		int netType = -1;

		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String simOperator = manager.getSimOperator();
		if (simOperator != null) {
			if (simOperator.startsWith("46000")
					|| simOperator.startsWith("46002")) {
				netType = DownloadRecomdService.NETTYPE_MOBILE;
			} else if (simOperator.startsWith("46001")) {
				netType = DownloadRecomdService.NETTYPE_UNICOM;
			} else if (simOperator.startsWith("46003")) {
				netType = DownloadRecomdService.NETTYPE_TELECOM;
			}
		}
		return netType;
	}

	public static String getProxyHost(Context context) {
		return Proxy.getHost(context);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//友盟统计
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//友盟统计
		MobclickAgent.onResume(this);
	}
}
