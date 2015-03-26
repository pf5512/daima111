package com.iLoong.Clock.Launcher;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.iLoong.launcher.Desktop3D.Log;


public class InstallCoCoLauncherReceiver extends BroadcastReceiver
{
	
	private final static String TAG = "InstallCoCoLauncherReceiver";
	public static final boolean LOG = false;
	public static int mToDownloadCoCoLauncherNotifyId = -1;
	public static boolean mIsSelfToDownloadCoCoLauncher = false;
	public NotificationManager mDownloadNotificationManager = null;
	
	@Override
	public void onReceive(
			Context context ,
			Intent intent )
	{
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if( Intent.ACTION_PACKAGE_ADDED.equals( action ) )
		{
			String packageName = intent.getDataString().substring( 8 );//intent.getData().getSchemeSpecificPart(); 
			if( LOG )
			{
				Log.d( TAG , "ACTION_PACKAGE_ADDED - packageName:" + packageName );
				Log.d( TAG , "ACTION_PACKAGE_ADDED - mIsSelfToDownloadCoCoLauncher:" + mIsSelfToDownloadCoCoLauncher );
				Log.d( TAG , "ACTION_PACKAGE_ADDED - mToDownloadCoCoLauncherNotifyId:" + mToDownloadCoCoLauncherNotifyId );
			}
			if( ( mIsSelfToDownloadCoCoLauncher ) && ( mToDownloadCoCoLauncherNotifyId != -1 ) && ( packageName.equals( "com.coco.launcher" ) ) )
			{
				mDownloadNotificationManager = (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );
				mDownloadNotificationManager.cancel( mToDownloadCoCoLauncherNotifyId );
				mDownloadNotificationManager = null;
				mIsSelfToDownloadCoCoLauncher = false;
				mToDownloadCoCoLauncherNotifyId = -1;
			}
			//
			if( packageName != null && true == WidgetLoadIndepandentActivity.isCooeeLauncherPackage( packageName ) )
			{
				//the installed app is coco launcher, so the widget icon on desktop needs be hide
				PackageManager p = context.getPackageManager();
				ComponentName mComponentName = new ComponentName( context , WidgetLoadIndepandentActivity.class );
				if( PackageManager.COMPONENT_ENABLED_STATE_ENABLED == p.getComponentEnabledSetting( mComponentName ) )
				{
					Log.d( TAG , "iLoongClock --- InstallDownloadAPKReceiver --- ACTION_PACKAGE_ADDED --- hide Widget icon " );
					p.setComponentEnabledSetting( mComponentName , PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP );
				}
			}
		}
		else if( Intent.ACTION_PACKAGE_REMOVED.equals( action ) )
		{
			String packageName = intent.getDataString().substring( 8 );
			//when this is the last Cooee launcher uninstalled, it needs to show the widget icon in app list
			if( packageName != null && true == WidgetLoadIndepandentActivity.isCooeeLauncherPackage( packageName ) && false == WidgetLoadIndepandentActivity.isCocoLauncherInstalled( context ) )
			{
				//the uninstalled app is coco launcher, so the widget icon on desktop needs be shown
				Log.d( TAG , "iLoongClock --- InstallDownloadAPKReceiver --- ACTION_PACKAGE_REMOVED --- Show Widget icon " );
				PackageManager p = context.getPackageManager();
				p.setComponentEnabledSetting( new ComponentName( context , WidgetLoadIndepandentActivity.class ) , PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP );
			}
		}
		else if( action.equals( "com.coco.lock.action.CHECK_ICON" ) )
		{
			//launcher notice the Widget to hide the icon on desktop
			String packageName_from_launcher = intent.getPackage();
			Log.d( TAG , "iLoongClock --- InstallDownloadAPKReceiver --- CHECK_ICON --- packageName_from_launcher = " + packageName_from_launcher );
			if( packageName_from_launcher != null && packageName_from_launcher.equals( context.getPackageName() ) )
			{
				Log.d( TAG , "iLoongClock --- InstallDownloadAPKReceiver --- CHECK_ICON --- Hide Widget icon " );
				PackageManager p = context.getPackageManager();
				p.setComponentEnabledSetting( new ComponentName( context , WidgetLoadIndepandentActivity.class ) , PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP );
			}
		}
		else if( action.equals( "com.iLoong.Clock.Launcher.ToDownloadCoCoLauncher" ) )
		{
			mIsSelfToDownloadCoCoLauncher = true;
			if( LOG )
				Log.d( TAG , "ToDownloadCoCoLauncher - mIsSelfToDownloadCoCoLauncher" );
		}
		else if( action.equals( "com.iLoong.Clock.Launcher.DownloadCoCoLauncherComplete" ) )
		{
			mToDownloadCoCoLauncherNotifyId = intent.getIntExtra( "mNotifyId" , -1 );
			if( LOG )
				Log.d( TAG , "DownloadCoCoLauncherComplete - mToDownloadCoCoLauncherNotifyId:" + mToDownloadCoCoLauncherNotifyId );
		}
	}
}
