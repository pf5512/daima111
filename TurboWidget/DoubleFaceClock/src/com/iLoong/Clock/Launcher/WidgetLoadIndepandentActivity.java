package com.iLoong.Clock.Launcher;


import java.net.URLEncoder;

import com.cooeecomet.clock.R;
import com.iLoong.Clock.Launcher.WidgetLoadIndepandentActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import com.iLoong.launcher.Desktop3D.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;


public class WidgetLoadIndepandentActivity extends Activity
{
	
	public static final String TAG = "WidgetLoadIndepandentActivity";
	public static final boolean LOG = false;
	public static final String LAUNCHER_COCO_PACKAGE_NAME = "com.coco.launcher";
	public static final String LAUNCHER_HTC_PACKAGE_NAME = "com.cooee.launcherHTC";
	public static final String LAUNCHER_XIAOMI2_PACKAGE_NAME = "com.cooee.Mylauncher";
	public static final String LAUNCHER_S3_PACKAGE_NAME = "com.cooee.launcherS3";
	public static final String LAUNCHER_MAIN_ACTIVITY_CLASS_NAME = "com.iLoong.launcher.desktop.iLoongLauncher";
	public static String LAUNCHER_PACKAGE_NAME = null;
	public static final String DEFAULT_APK_URL = "http://ku01.coomoe.com/uiv2/getApp.ashx";
	
	@Override
	protected void onResume()
	{
		if( LOG )
			Log.v( TAG , "WidgetLoadIndepandentActivity --- onResume" );
		super.onResume();
		gotoLoadWidgetInLauncher( this );
	}
	
	public void gotoLoadWidgetInLauncher(
			Context cxt )
	{
		if( !isCocoLauncherInstalled( cxt ) )
		{
			if( LOG )
				Log.v( TAG , "gotoLoadWidgetInLauncher - false" );
			goToDownloadCoCoLauncher();
			return;
		}
		if( LOG )
			Log.v( TAG , "gotoThemeBox - LAUNCHER_PACKAGE_NAME:" + LAUNCHER_PACKAGE_NAME );
		Intent intent = new Intent();
		intent.setClassName( LAUNCHER_PACKAGE_NAME , LAUNCHER_MAIN_ACTIVITY_CLASS_NAME );
		//In iLoongLauncher.onNewIntent() will determine the follow action
		intent.setAction( Intent.ACTION_MAIN );
		cxt.startActivity( intent );
		finish();
	}
	
	public static boolean isCocoLauncherInstalled(
			Context cxt )
	{
		boolean ret = true;
		Intent intent = new Intent();
		intent.setClassName( LAUNCHER_COCO_PACKAGE_NAME , LAUNCHER_MAIN_ACTIVITY_CLASS_NAME );
		ResolveInfo info = cxt.getPackageManager().resolveActivity( intent , 0 );
		if( info == null )
		{
			ret = false;
			if( LOG )
				Log.v( TAG , "COCO - !" );
		}
		else
		{
			LAUNCHER_PACKAGE_NAME = LAUNCHER_COCO_PACKAGE_NAME;
			ret = true;
			if( LOG )
				Log.v( TAG , "COCO - ok" );
		}
		if( ret == false )
		{
			intent.setClassName( LAUNCHER_S3_PACKAGE_NAME , LAUNCHER_MAIN_ACTIVITY_CLASS_NAME );
			info = cxt.getPackageManager().resolveActivity( intent , 0 );
			if( info == null )
			{
				ret = false;
				if( LOG )
					Log.v( TAG , "S3 - !" );
			}
			else
			{
				LAUNCHER_PACKAGE_NAME = LAUNCHER_S3_PACKAGE_NAME;
				ret = true;
				if( LOG )
					Log.v( TAG , "S3 - ok" );
			}
		}
		if( ret == false )
		{
			intent.setClassName( LAUNCHER_XIAOMI2_PACKAGE_NAME , LAUNCHER_MAIN_ACTIVITY_CLASS_NAME );
			info = cxt.getPackageManager().resolveActivity( intent , 0 );
			if( info == null )
			{
				ret = false;
				if( LOG )
					Log.v( TAG , "XIAOMI2 - !" );
			}
			else
			{
				LAUNCHER_PACKAGE_NAME = LAUNCHER_XIAOMI2_PACKAGE_NAME;
				ret = true;
				if( LOG )
					Log.v( TAG , "XIAOMI2 - ok" );
			}
		}
		if( ret == false )
		{
			intent.setClassName( LAUNCHER_HTC_PACKAGE_NAME , LAUNCHER_MAIN_ACTIVITY_CLASS_NAME );
			info = cxt.getPackageManager().resolveActivity( intent , 0 );
			if( info == null )
			{
				ret = false;
				if( LOG )
					Log.v( TAG , "HTC - !" );
			}
			else
			{
				LAUNCHER_PACKAGE_NAME = LAUNCHER_HTC_PACKAGE_NAME;
				ret = true;
				if( LOG )
					Log.v( TAG , "HTC - ok" );
			}
		}
		return ret;
	}
	
	public void goToDownloadCoCoLauncher()
	{
		AlertDialog.Builder builder = new Builder( this );
		builder.setTitle( getResources().getString( R.string.pop_to_download_title ) );
		builder.setMessage( getResources().getString( R.string.pop_to_download_message ) );
		builder.setPositiveButton( getResources().getString( R.string.pop_to_download_botton_left ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				WidgetLoadIndepandentActivity.this.startDownloadCoCoLauncher();
				dialog.dismiss();
				WidgetLoadIndepandentActivity.this.finish();
			}
		} );
		builder.setNegativeButton( getResources().getString( R.string.pop_to_download_botton_right ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
				WidgetLoadIndepandentActivity.this.finish();
			}
		} );
		builder.setOnKeyListener( new OnKeyListener() {
			
			@Override
			public boolean onKey(
					DialogInterface dialog ,
					int keyCode ,
					KeyEvent event )
			{
				// TODO Auto-generated method stub
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN )
				{
					dialog.dismiss();
					WidgetLoadIndepandentActivity.this.finish();
					return true;
				}
				return false;
			}
		} );
		builder.create().show();
	}
	
	public void startDownloadCoCoLauncher()
	{
		//		String apkUrl = getThemeApkUrl();
		//		Intent intent1 = new Intent();
		//		intent1.setAction(Intent.ACTION_VIEW);
		//		Uri content_url = Uri.parse(apkUrl);
		//		intent1.setData(content_url);
		//		this.startActivity(intent1);
		if( !isAllowDownloadWithToast( this ) )
		{
			return;
		}
		String fileName = this.getResources().getString( R.string.server_download_file_name1 );
		downloadAPK( this , this.getApplicationInfo().packageName , LAUNCHER_COCO_PACKAGE_NAME , fileName );
	}
	
	public static boolean isAllowDownloadWithToast(
			Context cxt )
	{
		if( !Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) )
		{
			Toast.makeText( cxt , cxt.getString( R.string.textSdCardNotPrepare ) , Toast.LENGTH_SHORT ).show();
			return false;
		}
		return true;
	}
	
	public void downloadAPK(
			Context context ,
			String srcPackageName ,
			String destPackageName ,
			String fileName )
	{
		Intent intent = new Intent();
		intent.setClass( context , DownloadCoCoLauncherService.class );
		intent.putExtra( DownloadCoCoLauncherService.DOWNLOAD_FILE_NAME , fileName );
		intent.putExtra( DownloadCoCoLauncherService.DOWNLOAD_URL_KEY , getApkUrl( context , srcPackageName , destPackageName ) );
		/*ComponentName res = */context.startService( intent );
	}
	
	public String getApkUrl(
			Context context ,
			String srcPackageName ,
			String destPackageName )
	{
		String apkUrl = DEFAULT_APK_URL;
		String result = String.format( "%s?p01=%s&p06=1&p07=%s&%s" , apkUrl , destPackageName , srcPackageName , getPhoneParams() );
		return result;
	}
	
	private String getPhoneParams()
	{
		QueryStringBuilder builder = new QueryStringBuilder();
		builder.add( "a01" , Build.MODEL ).add( "a02" , Build.DISPLAY ).add( "a05" , Build.PRODUCT ).add( "a06" , Build.DEVICE ).add( "a07" , Build.BOARD ).add( "a08" , Build.MANUFACTURER )
				.add( "a09" , Build.BRAND ).add( "a12" , Build.HARDWARE ).add( "a14" , Build.VERSION.RELEASE ).add( "a15" , Build.VERSION.SDK_INT );
		{
			WindowManager winMgr = (WindowManager)this.getSystemService( Context.WINDOW_SERVICE );
			Display display = winMgr.getDefaultDisplay();
			int scrWidth = display.getWidth();
			int scrHeight = display.getHeight();
			builder.add( "a04" , String.format( "%dX%d" , scrWidth , scrHeight ) );
		}
		{
			TelephonyManager telMgr = (TelephonyManager)this.getSystemService( Context.TELEPHONY_SERVICE );
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
		ConnectivityManager connMgr = (ConnectivityManager)this.getSystemService( Context.CONNECTIVITY_SERVICE );
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
		if( LOG )
			Log.v( TAG , "getPhoneParams:" + builder.toString() );
		return builder.toString();
	}
	
	public class QueryStringBuilder
	{
		
		private StringBuilder builder = new StringBuilder();
		
		public QueryStringBuilder add(
				String param ,
				String value )
		{
			if( builder.length() != 0 )
			{
				builder.append( "&" );
			}
			if( value == null )
			{
				value = "";
			}
			builder.append( param ).append( "=" ).append( URLEncoder.encode( value ) );
			return this;
		}
		
		public QueryStringBuilder add(
				String param ,
				int value )
		{
			return add( param , Integer.toString( value ) );
		}
		
		@Override
		public String toString()
		{
			return builder.toString();
		}
	}
	
	public static boolean isCooeeLauncherPackage(
			String packageName )
	{
		if( LAUNCHER_COCO_PACKAGE_NAME.equals( packageName ) || LAUNCHER_S3_PACKAGE_NAME.equals( packageName ) || LAUNCHER_XIAOMI2_PACKAGE_NAME.equals( packageName ) || LAUNCHER_HTC_PACKAGE_NAME
				.equals( packageName ) )
		{
			return true;
		}
		return false;
	}
}
