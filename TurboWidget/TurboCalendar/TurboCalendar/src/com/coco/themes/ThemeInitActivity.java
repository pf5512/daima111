package com.coco.themes;
import java.io.IOException;

import com.iLoong.Calender.activity.TurboActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Log;




public class ThemeInitActivity extends Activity
{
	
	public static final String TAG = "ThemeInitActivity";
	public static final boolean LOG = false;
//	public static final String LAUNCHER_COCO_PACKAGE_NAME = "com.coco.launcher";
//	public static final String LAUNCHER_HTC_PACKAGE_NAME = "com.cooee.launcherHTC";
//	public static final String LAUNCHER_XIAOMI2_PACKAGE_NAME = "com.cooee.Mylauncher";
//	public static final String LAUNCHER_S3_PACKAGE_NAME = "com.cooee.launcherS3";
	public static final String LAUNCHER_TURBO_PACKAGE_NAME = "com.cooeeui.brand.turbolauncher";
	public static final String THEMEBOX_MAIN_ACTIVITY = "com.iLoong.launcher.desktop.iLoongLauncher";
	public static String THEMEBOX_LAUNCHER = null;
	public static final String EXTRA_FROM_PACKAGE = "FROM_PACKAGE";
	public static final String EXTRA_MAIN_TAB_INDEX = "MAIN_TAB_INDEX";
//	public static final String DEFAULT_APK_URL = "http://ku01.coomoe.com/uiv2/getApp.ashx";
//	private Dialog dialog;
	
	@Override
	protected void onResume()
	{
		if( LOG )
			Log.v( TAG , "onResume" );
		super.onResume();
		gotoThemeBox( this );
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	public void gotoThemeBox(
			Context cxt )
	{
		if( !isCooeeThemeBoxInstalled( cxt ) )
		{
			if( LOG )
				Log.v( TAG , "isCooeeThemeBoxInstalled - false" );
			gotoDownloadTurbo();
			return;
		}
		if( LOG )
			Log.v( TAG , "gotoThemeBox - THEMEBOX_LAUNCHER:" + THEMEBOX_LAUNCHER );
		Intent intent = new Intent();
		intent.putExtra( EXTRA_FROM_PACKAGE , cxt.getPackageName() );
		intent.putExtra( EXTRA_MAIN_TAB_INDEX , 0 );
//		intent.setPackage( LAUNCHER_TURBO_PACKAGE_NAME );
		if( THEMEBOX_LAUNCHER != null && !THEMEBOX_LAUNCHER.equalsIgnoreCase( "" ) )
		{
			intent.setPackage( THEMEBOX_LAUNCHER );
			intent.setClassName( THEMEBOX_LAUNCHER , THEMEBOX_MAIN_ACTIVITY );
			cxt.startActivity( intent );
		}
//		else
//		{
//			intent.setAction( Intent.ACTION_MAIN );
//			intent.addCategory( "android.intent.category.cooee.themebox" );
//			cxt.startActivity( intent );
//			//Intent chooser = Intent.createChooser( intent , "select launcher" );
//			//cxt.startActivity( chooser );
//		}
		finish();
	}
	
	public boolean inAssertDir()
	{
		String[] apks = null;
		try
		{
			apks = this.getAssets().list( "apk" );
		}
		catch( IOException e1 )
		{
			e1.printStackTrace();
			return false;
		}
		if( apks.length > 0 )
			return true;
		return false;
	}
	
//	public String getAssertAPKPath()
//	{
//		File dir = new File( Environment.getExternalStorageDirectory() + "/cooee/tmp/" );
//		dir.mkdirs();
//		File tmp = new File( Environment.getExternalStorageDirectory() + "/cooee/tmp/coco_launcher.apk" );
//		if( tmp.exists() )
//			tmp.delete();
//		try
//		{
//			tmp.createNewFile();
//		}
//		catch( IOException e1 )
//		{
//			e1.printStackTrace();
//			return null;
//		}
//		FileOutputStream fos = null;
//		BufferedInputStream bis = null;
//		int BUFFER_SIZE = 1024;
//		byte[] buf = new byte[BUFFER_SIZE];
//		int size = 0;
//		try
//		{
//			//bis = new BufferedInputStream(iLoongLauncher.getInstance().getAssets().open("apk/"+apkName));
//			String[] apks = null;
//			try
//			{
//				apks = this.getAssets().list( "apk" );
//			}
//			catch( IOException e1 )
//			{
//				e1.printStackTrace();
//			}
//			bis = new BufferedInputStream( this.getAssets().open( "apk/" + apks[0] ) );
//			fos = new FileOutputStream( tmp );
//			while( ( size = bis.read( buf ) ) != -1 )
//				fos.write( buf , 0 , size );
//			fos.close();
//			bis.close();
//			return tmp.getAbsolutePath();
//		}
//		catch( FileNotFoundException e )
//		{
//			e.printStackTrace();
//		}
//		catch( IOException e )
//		{
//			e.printStackTrace();
//		}
//		return null;
//	}
	
//	public void installAPK(
//			String path )
//	{
//		Intent intent = new Intent( Intent.ACTION_VIEW );
//		intent.setDataAndType( Uri.fromFile( new File( path ) ) , "application/vnd.android.package-archive" );
//		this.startActivity( intent );
//	}
//	
	public static boolean isCooeeThemeBoxInstalled(
			Context cxt )
	{
		boolean ret = false;
		ResolveInfo info = null;
		Intent intent = new Intent();
		intent.setClassName( LAUNCHER_TURBO_PACKAGE_NAME , THEMEBOX_MAIN_ACTIVITY );
		info = cxt.getPackageManager().resolveActivity( intent , 0 );
		if( info == null )
		{
			ret = false;
			if( LOG )
				Log.v( TAG , "TURBO - !" );
//			intent.setClassName( LAUNCHER_TURBO_FREE_PACKAGE_NAME , THEMEBOX_MAIN_ACTIVITY );
//			info = cxt.getPackageManager().resolveActivity( intent , 0 );
//			if(info==null){
//				ret=false;
//			}else{
//				THEMEBOX_LAUNCHER = LAUNCHER_TURBO_FREE_PACKAGE_NAME;	
//				ret=true;
//			}
			
		}
		else
		{
			THEMEBOX_LAUNCHER = LAUNCHER_TURBO_PACKAGE_NAME;
			ret = true;
//			if( LOG )
//				Log.v( TAG , "TURBO - ok" );
		}
		return ret;
	}
	
	private void gotoDownloadTurbo()
	{
		Intent intent = new Intent( this , TurboActivity.class );
		startActivity( intent );
		finish();
	}
	
//	public void goToInstallCoCoLauncher()
//	{
//		AlertDialog.Builder builder = new Builder( this );
//		builder.setTitle( getResources().getString( R.string.pop_to_install_title ) );
//		builder.setMessage( getResources().getString( R.string.pop_to_install_message ) );
//		builder.setPositiveButton( getResources().getString( R.string.pop_to_install_botton_left ) , new OnClickListener() {
//			
//			@Override
//			public void onClick(
//					DialogInterface dialog ,
//					int which )
//			{
//				new Thread( new Runnable() {
//					
//					@Override
//					public void run()
//					{
//						String path = getAssertAPKPath();
//						if( path != null )
//						{
//							installAPK( path );
//						}
//					}
//				} ).start();
//				dialog.dismiss();
//				ThemeInitActivity.this.finish();
//			}
//		} );
//		//		builder.setNegativeButton(getResources().getString(R.string.pop_to_download_botton_right), new OnClickListener() {
//		//			@Override
//		//			public void onClick(DialogInterface dialog, int which) {
//		//				dialog.dismiss();
//		//				ThemeInitActivity.this.finish();
//		//			}
//		//		});
//		dialog = builder.create();
//		dialog.setOnKeyListener( new OnKeyListener() {
//			
//			@Override
//			public boolean onKey(
//					DialogInterface arg0 ,
//					int arg1 ,
//					KeyEvent arg2 )
//			{
//				if( arg1 == KeyEvent.KEYCODE_BACK && arg2.getAction() == KeyEvent.ACTION_DOWN )
//				{
//					dialog.dismiss();
//					finish();
//				}
//				return true;
//			}
//		} );
//		dialog.setCanceledOnTouchOutside( false );
//		dialog.show();
//	}
//	
//	public void goToDownloadCoCoLauncher()
//	{
//		AlertDialog.Builder builder = new Builder( this );
//		builder.setTitle( getResources().getString( R.string.pop_to_download_title ) );
//		builder.setMessage( getResources().getString( R.string.pop_to_download_message ) );
//		builder.setPositiveButton( getResources().getString( R.string.pop_to_download_botton_left ) , new OnClickListener() {
//			
//			@Override
//			public void onClick(
//					DialogInterface dialog ,
//					int which )
//			{
//				ThemeInitActivity.this.startDownloadCoCoLauncher();
//				dialog.dismiss();
//				ThemeInitActivity.this.finish();
//				//友盟统计
//				MobclickAgent.onEvent( ThemeInitActivity.this , "DownLoad" , CoCoMobclickAgent.getPackageName( ThemeInitActivity.this ) );
//			}
//		} );
//		builder.setNegativeButton( getResources().getString( R.string.pop_to_download_botton_right ) , new OnClickListener() {
//			
//			@Override
//			public void onClick(
//					DialogInterface dialog ,
//					int which )
//			{
//				dialog.dismiss();
//				ThemeInitActivity.this.finish();
//			}
//		} );
//		dialog = builder.create();
//		dialog.setOnKeyListener( new OnKeyListener() {
//			
//			@Override
//			public boolean onKey(
//					DialogInterface arg0 ,
//					int arg1 ,
//					KeyEvent arg2 )
//			{
//				if( arg1 == KeyEvent.KEYCODE_BACK && arg2.getAction() == KeyEvent.ACTION_DOWN )
//				{
//					dialog.dismiss();
//					finish();
//				}
//				return true;
//			}
//		} );
//		dialog.setCanceledOnTouchOutside( false );
//		dialog.show();
//	}
//	
//	public void startDownloadCoCoLauncher()
//	{
//		//		String apkUrl = getThemeApkUrl();
//		//		Intent intent1 = new Intent();
//		//		intent1.setAction(Intent.ACTION_VIEW);
//		//		Uri content_url = Uri.parse(apkUrl);
//		//		intent1.setData(content_url);
//		//		this.startActivity(intent1);
//		if( !isAllowDownloadWithToast( this ) )
//		{
//			return;
//		}
//		String fileName = this.getResources().getString( R.string.server_download_file_name );
//		downloadAPK( this , this.getApplicationInfo().packageName , LAUNCHER_COCO_PACKAGE_NAME , fileName );
//	}
//	
//	public static boolean isAllowDownloadWithToast(
//			Context cxt )
//	{
//		if( !Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) )
//		{
//			Toast.makeText( cxt , cxt.getString( R.string.textSdCardNotPrepare ) , Toast.LENGTH_SHORT ).show();
//			return false;
//		}
//		return true;
//	}
	
//	public void downloadAPK(
//			Context context ,
//			String srcPackageName ,
//			String destPackageName ,
//			String fileName )
//	{
//		Intent intent = new Intent();
//		intent.setClass( context , DownloadCoCoLauncherService.class );
//		intent.putExtra( DownloadCoCoLauncherService.DOWNLOAD_FILE_NAME , fileName );
//		intent.putExtra( DownloadCoCoLauncherService.DOWNLOAD_URL_KEY , getApkUrl( context , srcPackageName , destPackageName ) );
//		/*ComponentName res = */context.startService( intent );
//	}
//	
//	public String getApkUrl(
//			Context context ,
//			String srcPackageName ,
//			String destPackageName )
//	{
//		String apkUrl = DEFAULT_APK_URL;
//		String result = String.format( "%s?p01=%s&p06=1&p07=%s&%s" , apkUrl , destPackageName , srcPackageName , getPhoneParams() );
//		return result;
//	}
	
//	private String getPhoneParams()
//	{
//		QueryStringBuilder builder = new QueryStringBuilder();
//		builder.add( "a01" , Build.MODEL ).add( "a02" , Build.DISPLAY ).add( "a05" , Build.PRODUCT ).add( "a06" , Build.DEVICE ).add( "a07" , Build.BOARD ).add( "a08" , Build.MANUFACTURER )
//				.add( "a09" , Build.BRAND ).add( "a12" , Build.HARDWARE ).add( "a14" , Build.VERSION.RELEASE ).add( "a15" , Build.VERSION.SDK_INT );
//		{
//			WindowManager winMgr = (WindowManager)this.getSystemService( Context.WINDOW_SERVICE );
//			Display display = winMgr.getDefaultDisplay();
//			int scrWidth = display.getWidth();
//			int scrHeight = display.getHeight();
//			builder.add( "a04" , String.format( "%dX%d" , scrWidth , scrHeight ) );
//		}
//		{
//			TelephonyManager telMgr = (TelephonyManager)this.getSystemService( Context.TELEPHONY_SERVICE );
//			if( telMgr != null )
//			{
//				builder.add( "u01" , telMgr.getSubscriberId() )
//				// IMSI
//						.add( "u03" , telMgr.getDeviceId() )
//						// IMEI
//						.add( "u04" , telMgr.getSimSerialNumber() )
//						// ICCID
//						.add( "u05" , telMgr.getLine1Number() );
//			}
//		}
//		ConnectivityManager connMgr = (ConnectivityManager)this.getSystemService( Context.CONNECTIVITY_SERVICE );
//		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
//		if( netInfo != null )
//		{
//			if( netInfo.getTypeName().equals( "WIFI" ) )
//			{
//				builder.add( "u07" , "2" );
//			}
//			else if( netInfo.getTypeName().equals( "mobile" ) )
//			{
//				builder.add( "u07" , "1" );
//			}
//		}
//		if( LOG )
//			Log.v( TAG , "getPhoneParams:" + builder.toString() );
//		return builder.toString();
//	}
//	
//	public class QueryStringBuilder
//	{
//		
//		private StringBuilder builder = new StringBuilder();
//		
//		public QueryStringBuilder add(
//				String param ,
//				String value )
//		{
//			if( builder.length() != 0 )
//			{
//				builder.append( "&" );
//			}
//			if( value == null )
//			{
//				value = "";
//			}
//			builder.append( param ).append( "=" ).append( URLEncoder.encode( value ) );
//			return this;
//		}
//		
//		public QueryStringBuilder add(
//				String param ,
//				int value )
//		{
//			return add( param , Integer.toString( value ) );
//		}
//		
//		@Override
//		public String toString()
//		{
//			return builder.toString();
//		}
//	}
}
