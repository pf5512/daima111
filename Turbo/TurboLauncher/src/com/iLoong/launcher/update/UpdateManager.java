package com.iLoong.launcher.update;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.theme.adapter.DownloadLockBoxService;


public class UpdateManager
{
	
	public static final String DEFAULT_APK_UPDATE_URL = "http://ku01.coomoe.com/uiv2/getApp.ashx?p01=com.cool.launcher&p06=1";
	public static final String DEFAULT_UPDATE_URL = "http://ku01.coomoe.com/uiv2/getApp.ashx?p01=com.cool.launcher";
	public static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory() + "/coco/download";
	public static final String DOWNLOAD_LAUNCHER_NAME = "iLoongCoCo.apk";
	public static final String EXTRA_PARAM = "EXTRA_PARAM";
	public static final String EXTRA_VALUE_SERVICE = "service";
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 保存解析的XML信息 */
	HashMap<String , String> mHashMap;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;
	private DownloadApkThread downApk;
	private Context mContext;
	/* 更新进度条 */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	private Dialog noticeDialog;
	private boolean dialogIsShow = false;
	private Handler mHandler = new Handler() {
		
		public void handleMessage(
				Message msg )
		{
			switch( msg.what )
			{
			// 正在下载
				case DOWNLOAD:
					// 设置进度条位置
					mProgress.setProgress( progress );
					break;
				case DOWNLOAD_FINISH:
					// 安装文件
					installApk();
					break;
				default:
					break;
			}
		};
	};
	
	public UpdateManager(
			Context context )
	{
		this.mContext = context;
	}
	
	/**
	 * 获取APK下载的url
	 */
	private String getApkUrl()
	{
		String url = DEFAULT_APK_UPDATE_URL + "&" + getPhoneParams();
		return url;
	}
	
	/**
	 * 获取手机的其他信息
	 */
	public String getPhoneParams()
	{
		QueryStringBuilder builder = new QueryStringBuilder();
		builder.add( "a01" , Build.MODEL ).add( "a02" , Build.DISPLAY ).add( "a05" , Build.PRODUCT ).add( "a06" , Build.DEVICE ).add( "a07" , Build.BOARD ).add( "a08" , Build.MANUFACTURER )
				.add( "a09" , Build.BRAND ).add( "a12" , Build.HARDWARE ).add( "a14" , Build.VERSION.RELEASE ).add( "a15" , Build.VERSION.SDK_INT );
		{
			WindowManager winMgr = (WindowManager)mContext.getSystemService( Context.WINDOW_SERVICE );
			Display display = winMgr.getDefaultDisplay();
			int scrWidth = display.getWidth();
			int scrHeight = display.getHeight();
			builder.add( "a04" , String.format( "%dX%d" , scrWidth , scrHeight ) );
		}
		{
			TelephonyManager telMgr = (TelephonyManager)mContext.getSystemService( Context.TELEPHONY_SERVICE );
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
		ConnectivityManager connMgr = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
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
		return builder.toString();
	}
	
	/**
	 * 返回是否有更新的dialog正在显示
	 */
	public boolean DialogIsShow()
	{
		return dialogIsShow;
	}
	
	private String getUpdateXml()
	{
		String url = DEFAULT_UPDATE_URL + "&p02=" + getVersionCode( iLoongLauncher.getInstance() ) + "&p07=com.cool.launcher&p06=2&" + getPhoneParams();
		Log.v( "getUpdateXml" , "url = " + url );
		return url;
	}
	
	/**
	 * 检查软件是否有更新版本
	 */
	public boolean isUpdate()
	{
		// 获取当前软件版本
		if( iLoongLauncher.getInstance() == null )
			return false;
		int versionCode = getVersionCode( iLoongLauncher.getInstance() );
		// 把version.xml放到网络上，然后获取文件信息
		HttpURLConnection conn = null;
		InputStream inStream = null;
		try
		{
			URL url = new URL( getUpdateXml() );
			conn = (HttpURLConnection)url.openConnection();
			inStream = conn.getInputStream();
			// 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析
			ParseXmlService service = new ParseXmlService();
			mHashMap = service.parseXml( inStream );
			recordTime(); // 更新xml过后记录时间
			if( null != mHashMap )
			{
				String strVersion = mHashMap.get( "version" );
				if( strVersion == null )
				{
					return false;
				}
				int serviceCode = Integer.valueOf( strVersion );
				// 版本判断
				if( serviceCode > versionCode )
				{
					return true;
				}
			}
		}
		catch( IOException e1 )
		{
			e1.printStackTrace();
		}
		finally
		{
			if( inStream != null )
			{
				try
				{
					inStream.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
			if( conn != null )
			{
				conn.disconnect();
			}
		}
		return false;
	}
	
	/**
	 * 获取软件版本号
	 */
	private int getVersionCode(
			Context context )
	{
		int versionCode = 0;
		try
		{
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo( context.getPackageName() , 0 ).versionCode;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		return versionCode;
	}
	
	// 记录安装时间
	private void recordTime()
	{
		Time curTime = new Time();
		curTime.setToNow(); // 取得系统时间。
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences( mContext ).edit();
		int year = curTime.year;
		editor.putInt( "year" , year );
		int month = curTime.month;
		editor.putInt( "month" , month + 1 );
		int day = curTime.monthDay;
		editor.putInt( "day" , day );
		editor.commit();
	}
	
	public String getApkUrl(
			Context context ,
			String srcPackageName ,
			String destPackageName )
	{
		String apkUrl = "http://ku01.coomoe.com/uiv2/getApp.ashx";
		String result = String.format( "%s?p01=%s&p06=1&p07=%s&%s" , apkUrl , destPackageName , srcPackageName , getPhoneParams() );
		return result;
	}
	
	public void downloadAPK(
			Context context ,
			String srcPackageName ,
			String destPackageName ,
			String fileName )
	{
		Intent intent = new Intent();
		intent.setClass( context , DownloadLockBoxService.class );
		intent.putExtra( "downloadFileName" , fileName );
		intent.putExtra( "downloadUrl" , getApkUrl( context , srcPackageName , destPackageName ) );
		Intent intentGetToDownloadAPKName = new Intent( "com.iLoong.launcher.GetToDownloadAPKName" );
		intent.putExtra( "logo" , "cocolauncher" );
		intentGetToDownloadAPKName.putExtra( "ToDownloadAPKName" , destPackageName );
		context.sendBroadcast( intentGetToDownloadAPKName );
		context.startService( intent );
	}
	
	/**
	 * 显示软件更新对话框
	 */
	public void showNoticeDialog(
			final boolean autoFinish )
	{
		dialogIsShow = true;
		// 构造对话框
		AlertDialog.Builder builder = new Builder( mContext );
		builder.setTitle( RR.string.soft_update_title );
		builder.setMessage( RR.string.soft_update_info );
		// 更新
		builder.setPositiveButton( RR.string.soft_update_updatebtn , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
				// 显示下载对话框
				downloadAPK( mContext , "com.cool.launcher" , "com.cool.launcher" , iLoongLauncher.getInstance().getString( RR.string.coco_launcher_download_name ) );
				if( autoFinish )
				{
					if( mContext instanceof Activity )
					{
						( (Activity)mContext ).finish();
					}
				}
				// showDownloadDialog(autoFinish);
				// NotificationManager manager = (NotificationManager)
				// mContext
				// .getSystemService(Context.NOTIFICATION_SERVICE);
				// manager.cancel(1);
			}
		} );
		// 稍后更新
		builder.setNegativeButton( RR.string.soft_update_later , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
				dialogIsShow = false;
				if( autoFinish )
				{
					if( mContext instanceof Activity )
					{
						( (Activity)mContext ).finish();
					}
				}
			}
		} );
		builder.setOnCancelListener( new OnCancelListener() {
			
			public void onCancel(
					DialogInterface dialog )
			{
				dialogIsShow = false;
			}
		} );
		noticeDialog = builder.create();
		noticeDialog.show();
	}
	
	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog(
			final boolean autoFinish )
	{
		dialogIsShow = true;
		// 构造软件下载对话框
		AlertDialog.Builder builder = new Builder( mContext );
		builder.setTitle( RR.string.soft_updating );
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from( mContext );
		View v = inflater.inflate( RR.layout.softupdate_progress , null );
		mProgress = (ProgressBar)v.findViewById( RR.id.update_progress );
		builder.setView( v );
		// 取消更新
		builder.setNegativeButton( RR.string.soft_update_cancel , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
				// 设置取消状态
				cancelUpdate = true;
				dialogIsShow = false;
				if( autoFinish )
				{
					if( mContext instanceof Activity )
					{
						( (Activity)mContext ).finish();
					}
				}
			}
		} );
		// 监听返回键
		builder.setOnCancelListener( new OnCancelListener() {
			
			public void onCancel(
					DialogInterface dialog )
			{
				dialogIsShow = false;
				cancelUpdate = true;
				if( autoFinish )
				{
					if( mContext instanceof Activity )
					{
						( (Activity)mContext ).finish();
					}
				}
			}
		} );
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// 下载文件
		downloadApk();
	}
	
	/**
	 * 下载apk文件
	 */
	private void downloadApk()
	{
		// 启动新线程下载软件
		downApk = new DownloadApkThread();
		downApk.start();
	}
	
	/**
	 * 下载文件线程
	 */
	private class DownloadApkThread extends Thread
	{
		
		@Override
		public void run()
		{
			// 判断SD卡是否存在，并且是否具有读写权限
			if( !Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) )
			{
				return;
			}
			HttpURLConnection conn = null;
			InputStream is = null;
			FileOutputStream fos = null;
			try
			{
				URL url = new URL( getApkUrl() );
				// 创建连接
				conn = (HttpURLConnection)url.openConnection();
				conn.connect();
				// 获取文件大小
				int length = conn.getContentLength();
				// 创建输入流
				is = conn.getInputStream();
				File file = new File( DOWNLOAD_DIR );
				// 判断文件目录是否存在
				if( !file.exists() )
				{
					file.mkdirs();
				}
				File apkFile = new File( DOWNLOAD_DIR , DOWNLOAD_LAUNCHER_NAME );
				fos = new FileOutputStream( apkFile );
				int count = 0;
				// 缓存
				byte buf[] = new byte[1024];
				// 写入到文件中
				do
				{
					int numread = is.read( buf );
					count += numread;
					// 计算进度条位置
					progress = (int)( ( (float)count / length ) * 100 );
					// 更新进度
					mHandler.sendEmptyMessage( DOWNLOAD );
					if( numread <= 0 )
					{
						// 下载完成
						mHandler.sendEmptyMessage( DOWNLOAD_FINISH );
						break;
					}
					// 写入文件
					fos.write( buf , 0 , numread );
				}
				while( !cancelUpdate );// 点击取消就停止下载.
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
				if( fos != null )
				{
					try
					{
						fos.close();
					}
					catch( IOException e )
					{
						e.printStackTrace();
					}
				}
				if( is != null )
				{
					try
					{
						is.close();
					}
					catch( IOException e )
					{
						e.printStackTrace();
					}
				}
				if( conn != null )
				{
					conn.disconnect();
				}
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	};
	
	/**
	 * 安装APK文件
	 */
	private void installApk()
	{
		File apkfile = new File( DOWNLOAD_DIR , DOWNLOAD_LAUNCHER_NAME );
		if( !apkfile.exists() )
		{
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent( Intent.ACTION_VIEW );
		i.setDataAndType( Uri.parse( "file://" + apkfile.toString() ) , "application/vnd.android.package-archive" );
		mContext.startActivity( i );
	}
	
	public static boolean isHaveInternet(
			Context context )
	{
		try
		{
			ConnectivityManager manger = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo info = manger.getActiveNetworkInfo();
			return( info != null && info.isConnected() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return false;
		}
	}
}
