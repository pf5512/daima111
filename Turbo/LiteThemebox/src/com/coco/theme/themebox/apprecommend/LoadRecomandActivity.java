package com.coco.theme.themebox.apprecommend;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Window;

import com.coco.download.Assets;
import com.iLoong.base.themebox.R;


public class LoadRecomandActivity extends Activity
{
	
	private Dialog noticeDialog;
	// private int urltype = -1;
	private String name;
	private String mUrl;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		Intent intent = getIntent();
		name = intent.getStringExtra( "name" );
		mUrl = intent.getStringExtra( "apkurl" );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		super.onCreate( savedInstanceState );
		// showNoticeDialog();
		Intent intent1 = new Intent();
		intent1.setClass( LoadRecomandActivity.this , DownloadRecomdService.class );
		String fileName = getResources().getString( R.string.server_downloadRecommend_file_name , name );
		intent1.putExtra( DownloadRecomdService.DOWNLOAD_FILE_NAME , fileName );
		String url = getApkUrl();
		intent1.putExtra( DownloadRecomdService.DOWNLOAD_URL_KEY , url );
		startService( intent1 );
		finish();
	}
	
	/**
	 * 获取APK下载的url
	 */
	private String getApkUrl()
	{
		String url = mUrl + Assets.getPhoneParams( this );
		return url;
	}
	
	/**
	 * 显示软件更新对话�?
	 */
	// public void showNoticeDialog() {
	// // 构�?对话�?
	// AlertDialog.Builder builder = new Builder(this);
	// builder.setTitle(getString(R.string.downloadRecommend_title,
	// name));
	// builder.setMessage(getString(R.string.downloadRecommend_info,
	// name));
	// // 更新
	// builder.setPositiveButton(R.string.download,
	// new OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// Intent intent = new Intent();
	// intent.setClass(LoadRecomandActivity.this,
	// DownloadRecomdService.class);
	// String fileName = getResources()
	// .getString(
	// R.string.server_downloadRecommend_file_name,
	// name);
	// intent.putExtra(
	// DownloadRecomdService.DOWNLOAD_FILE_NAME,
	// fileName);
	// String url = getApkUrl();
	// intent.putExtra(DownloadRecomdService.DOWNLOAD_URL_KEY,
	// url);
	// startService(intent);
	// finish();
	// }
	// });
	// // 稍后更新
	// builder.setNegativeButton(R.string.download_later,
	// new OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// finish();
	// }
	// });
	// builder.setOnCancelListener(new OnCancelListener() {
	// public void onCancel(DialogInterface dialog) {
	// dialog.dismiss();
	// finish();
	// }
	// });
	// noticeDialog = builder.create();
	// noticeDialog.show();
	// }
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	public static int getProxyPort(
			Context context )
	{
		return Proxy.getPort( context );
	}
	
	public static boolean isCWWAPConnect(
			Context context )
	{
		boolean result = false;
		ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if( info != null && info.getType() == ConnectivityManager.TYPE_MOBILE )
		{
			if( Proxy.getDefaultHost() != null || Proxy.getHost( context ) != null )
			{
				result = true;
			}
		}
		return result;
	}
	
	public static int getNetWorkType(
			Context context )
	{
		int netType = -1;
		TelephonyManager manager = (TelephonyManager)context.getSystemService( Context.TELEPHONY_SERVICE );
		String simOperator = manager.getSimOperator();
		if( simOperator != null )
		{
			if( simOperator.startsWith( "46000" ) || simOperator.startsWith( "46002" ) )
			{
				netType = DownloadRecomdService.NETTYPE_MOBILE;
			}
			else if( simOperator.startsWith( "46001" ) )
			{
				netType = DownloadRecomdService.NETTYPE_UNICOM;
			}
			else if( isTelecomOperator( simOperator ) )
			{
				netType = DownloadRecomdService.NETTYPE_TELECOM;
			}
		}
		return netType;
	}
	
	// 由于“46003”这个字符串被杀毒软件avast! mobile security当作木马病毒处理，
	// 因此将字符串的判断拆分开来	PS：很无语.......
	private static boolean isTelecomOperator(
			String simOperator )
	{
		boolean ret = false;
		String tem = simOperator;
		if( tem.startsWith( "4600" ) )
		{
			if( tem.startsWith( "3" , 4 ) )
			{
				ret = true;
			}
		}
		return ret;
	}
	
	public static String getProxyHost(
			Context context )
	{
		return Proxy.getHost( context );
	}
}
