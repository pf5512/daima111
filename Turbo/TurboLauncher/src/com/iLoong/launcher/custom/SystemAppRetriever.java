package com.iLoong.launcher.custom;


import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class SystemAppRetriever
{
	
	private final long REQUEST_HTTP_INTERVAL = 10000;
	public static boolean isRuning = false;
	private PackageTransfer pckTransfer;
	public static String appInfo;
	private static final String SYSTEM_APP_UPDATE_INFO = "system_app_update_info";
	private static final String SYSTEM_APP_UPDATE_FLAG = "system_app_update_flag";
	private static final String DEVICE_VERSION = "device_version";
	private static final String DEVICE_DISPLAY = "device_display";
	private boolean hasUpdate = false;
	// to check whether system apps' info have been uploaded to server.
	public static SharedPreferences preferences;
	Context mContext;
	
	public SystemAppRetriever(
			Context context ,
			int delayTimeMillis )
	{
		String[] version = new String[2];
		String release = null;
		String display = null;
		String lan = Locale.getDefault().getLanguage();
		if( !lan.equals( "zh" ) )
		{
			return;
		}
		/*avoid user from repeating resume phone*/
		if( isRuning )
		{
			return;
		}
		else
		{
			isRuning = true;
		}
		version = getDeviceVersion();
		preferences = iLoongLauncher.getInstance().getSharedPreferences( SYSTEM_APP_UPDATE_INFO , Activity.MODE_PRIVATE );
		hasUpdate = preferences.getBoolean( SYSTEM_APP_UPDATE_FLAG , false );
		release = preferences.getString( DEVICE_VERSION , null );
		display = preferences.getString( DEVICE_DISPLAY , null );
		if( release != null && display != null )
		{
			if( !release.equals( version[0] ) && !display.equals( version[1] ) )
			{
				// it is necessary to upload info  again because device has been updated.
				Log.v( "retrieve" , " upload info again" );
			}
			else
			{
				if( hasUpdate )
				{
					// device has already been uploaded 
					Log.v( "retrieve" , " already updated " );
					return;
				}
			}
		}
		preferences.edit().putString( DEVICE_VERSION , version[0] ).commit();
		preferences.edit().putString( DEVICE_DISPLAY , version[1] ).commit();
		Log.v( "retriever" , "delayTimeMillis " + delayTimeMillis );
		long delay = System.currentTimeMillis() + delayTimeMillis;
		mContext = context;
		getSystemPackage();
		pckTransfer = new PackageTransfer();
		Intent intent = new Intent();
		intent.setAction( PackageTransfer.ACTION_SEND_PACKAGE_INFO );
		PendingIntent pi = PendingIntent.getBroadcast( context , 0 , intent , 0 );
		AlarmManager am = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );
		am.set( AlarmManager.RTC , delay , pi );
	}
	
	public static void httpResponseCallback(
			boolean isComplete )
	{
		Log.v( "retrieve" , "httpResponseCallback :" + isComplete );
		isRuning = false;
		if( isComplete )
		{
			if( preferences != null )
				preferences.edit().putBoolean( SYSTEM_APP_UPDATE_FLAG , true ).commit();
		}
		else
		{
			if( preferences != null )
				preferences.edit().putBoolean( SYSTEM_APP_UPDATE_FLAG , false ).commit();
		}
	}
	
	public void getSystemPackage()
	{
		List<ResolveInfo> apps = null;
		//		List<ResolveInfo> userApp = new ArrayList<ResolveInfo>();
		final Intent mainIntent = new Intent( Intent.ACTION_MAIN , null );
		mainIntent.addCategory( Intent.CATEGORY_LAUNCHER );
		final PackageManager packageManager = mContext.getPackageManager();
		apps = packageManager.queryIntentActivities( mainIntent , 0 );
		//		Iterator<ResolveInfo> ite = apps.iterator();
		//		while( ite.hasNext() )
		//		{
		//			ResolveInfo info = ite.next();
		//			if( !checkSystemApp( info.activityInfo.packageName ) )
		//			{
		//				userApp.add( info );
		//			}
		//		}
		//		apps.removeAll( userApp );
		Iterator<ResolveInfo> sysApp = apps.iterator();
		appInfo = "||";
		while( sysApp.hasNext() )
		{
			ResolveInfo info = sysApp.next();
			appInfo += "label:" + info.loadLabel( packageManager ).toString() + ";";
			appInfo += "pckName:" + info.activityInfo.packageName + ";";
			appInfo += "className:" + info.activityInfo.name + ";||";
		}
	}
	
	public boolean checkSystemApp(
			String pname )
	{
		try
		{
			PackageInfo pInfo = mContext.getPackageManager().getPackageInfo( pname , 0 );
			if( isSystemApp( pInfo ) )
			{
				return true; /* system app */
			}
			else
			{
				return false;/* user app */
			}
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean isSystemApp(
			PackageInfo pInfo )
	{
		return( ( pInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0 );
	}
	
	public static String[] getDeviceVersion()
	{
		String[] deviceVersion = new String[2];
		String oldDisplay = null;
		deviceVersion[0] = android.os.Build.VERSION.RELEASE;
		deviceVersion[1] = android.os.Build.DISPLAY;
		return deviceVersion;
	}
}
