package com.coco.lock2.lockbox;// xiatian add whole file //fix bug same as 0001880


import java.util.List;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.coco.theme.themebox.util.Log;


public class InstallDownloadAPKReceiver extends BroadcastReceiver
{
	
	private final static String TAG = "InstallAPKReceiver";
	public static final boolean LOG = false;
	public static int mToDownloadCoCoLauncherNotifyId = -1;
	public static boolean mIsSelfToDownloadCoCoLauncher = false;
	public static String mDownloadAPKName = null;
	public NotificationManager mDownloadNotificationManager = null;
	
	@Override
	public void onReceive(
			Context context ,
			Intent intent )
	{
		String action = intent.getAction();
		if( Intent.ACTION_PACKAGE_ADDED.equals( action ) )
		{
			String packageName = intent.getDataString().substring( 8 );// intent.getData().getSchemeSpecificPart();
			if( LOG )
			{
				Log.d( TAG , "ACTION_PACKAGE_ADDED - packageName:" + packageName );
				Log.d( TAG , "ACTION_PACKAGE_ADDED - mDownloadAPKName:" + mDownloadAPKName );
				Log.d( TAG , "ACTION_PACKAGE_ADDED - mIsSelfToDownloadCoCoLauncher:" + mIsSelfToDownloadCoCoLauncher );
				Log.d( TAG , "ACTION_PACKAGE_ADDED - mToDownloadCoCoLauncherNotifyId:" + mToDownloadCoCoLauncherNotifyId );
			}
			if( ( mIsSelfToDownloadCoCoLauncher ) && ( mToDownloadCoCoLauncherNotifyId != -1 ) && ( packageName.equals( mDownloadAPKName ) ) )
			{
				mDownloadNotificationManager = (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );
				mDownloadNotificationManager.cancel( mToDownloadCoCoLauncherNotifyId );
				mDownloadNotificationManager = null;
				mIsSelfToDownloadCoCoLauncher = false;
				mToDownloadCoCoLauncherNotifyId = -1;
				mDownloadAPKName = null;
			}
			// teapotXu_20130305: add start
			if( packageName != null )
			{
				// examine whether the installed package is Widget of cooee or
				// not
				PackageManager p = context.getPackageManager();
				List<ResolveInfo> mWidgetResolveInfoList = p.queryIntentActivities( new Intent( "com.iLoong.widget" , null ) , PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA );
				for( ResolveInfo resolveInfo : mWidgetResolveInfoList )
				{
					if( packageName.equals( resolveInfo.activityInfo.packageName ) )
					{
						Log.d( TAG , "iLoongBase --- InstallDownloadAPKReceiver --- widget installed --- packageName = " + packageName );
						// now this installed package is cooee widget, so send
						// message to this widget to hide the icon itself.
						Intent checkIntent = new Intent( StaticClass.ACTION_CHECK_ICON );
						checkIntent.setPackage( packageName );
						context.sendBroadcast( checkIntent );
						break;
					}
				}
			}
			// teapotXu_20130305: add end
		}
		else if( action.equals( "com.coco.lock2.lockbox.ToDownloadAPK" ) )
		{
			mIsSelfToDownloadCoCoLauncher = true;
			if( LOG )
				Log.d( TAG , "ToDownloadAPK - mIsSelfToDownload" );
		}
		else if( action.equals( "com.coco.lock2.lockbox.DownloadAPKComplete" ) )
		{
			mToDownloadCoCoLauncherNotifyId = intent.getIntExtra( "mNotifyId" , -1 );
			if( LOG )
				Log.d( TAG , "DownloadAPKComplete - mToDownloadCoCoLauncherNotifyId:" + mToDownloadCoCoLauncherNotifyId );
		}
		else if( action.equals( "com.coco.lock2.lockbox.GetToDownloadAPKName" ) )
		{
			mDownloadAPKName = intent.getStringExtra( "ToDownloadAPKName" );
			if( LOG )
				Log.d( TAG , "GetToDownloadAPKName - mDownloadAPKName:" + mDownloadAPKName );
		}
	}
}
