package com.iLoong.theme.adapter;


import java.util.List;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.pub.provider.PubContentProvider;
import com.iLoong.launcher.pub.provider.TurboPubContentProvider;


public class LockManager
{
	
	public static final String FIELD_PACKAGE_NAME = "packageName";
	public static final String FIELD_CLASS_NAME = "className";
	private Context mContext;
	private static final String LOG_TAG = "LockManager";
	private static final String SYS_SETTING_CURRENT_LOCK = "cooee_current_lock";
	private static final int SYS_SETTING_LOCK_VERSION = 2001;
	
	public LockManager(
			Context context )
	{
		mContext = context;
	}
	
	public List<ResolveInfo> queryInstallList()
	{
		Intent intentLockView = new Intent( "com.coco.lock.action.VIEW" );
		intentLockView.addCategory( Intent.CATEGORY_INFO );
		List<ResolveInfo> infoList = mContext.getPackageManager().queryIntentActivities( intentLockView , 0 );
		return infoList;
	}
	
	public static String getCurrentLock(
			Context context ,
			String name )
	{
		ContentResolver resolver = context.getContentResolver();
		Uri uri = null;
		if( RR.net_version )
		{
			if( DefaultLayout.personal_center_internal )
			{
				uri = Uri.parse( "content://" + TurboPubContentProvider.LAUNCHER_AUTHORITY + "/" + "lock" );
			}
			else
			{
				uri = Uri.parse( "content://" + TurboPubContentProvider.PERSONAL_CENTER_AUTHORITY + "/" + "lock" );
			}
		}
		else
		{
			if( DefaultLayout.personal_center_internal )
			{
				uri = Uri.parse( "content://" + PubContentProvider.LAUNCHER_AUTHORITY + "/" + "lock" );
			}
			else
			{
				uri = Uri.parse( "content://" + PubContentProvider.PERSONAL_CENTER_AUTHORITY + "/" + "lock" );
			}
		}
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		String result = null;
		try
		{
			selection = " propertyName=? ";
			selectionArgs = new String[]{ name };
			Cursor cursor = resolver.query( uri , projection , selection , selectionArgs , sortOrder );
			if( cursor != null )
			{
				if( cursor.moveToFirst() )
				{
					result = cursor.getString( cursor.getColumnIndex( "propertyValue" ) );
				}
				cursor.close();
			}
			else
			{
				result = null;
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			result = null;
		}
		return result;
	}
	
	public ComponentName queryCurrentLock()
	{
		Context themeboxContext = null;
		try
		{
			themeboxContext = mContext.createPackageContext( "com.iLoong.base.themebox" , 0 );
		}
		catch( NameNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// SharedPreferences sharedPre = PreferenceManager
		// .getDefaultSharedPreferences(themeboxContext);
		String defaultPkgName = AppConfig.getInstance( themeboxContext ).getDefaultLockscreenPackage();
		String defaultClassName = AppConfig.getInstance( themeboxContext ).getDefaultLockscreenClass();
		if( PlatformInfo.getInstance( themeboxContext ).isSupportViewLock() )
		{
			String settingStr = android.provider.Settings.System.getString( themeboxContext.getContentResolver() , SYS_SETTING_CURRENT_LOCK );
			if( settingStr == null )
			{
				return new ComponentName( defaultPkgName , defaultClassName );
			}
			String[] strArray = settingStr.split( "," );
			if( strArray.length < 4 || !strArray[0].equals( Integer.toString( SYS_SETTING_LOCK_VERSION ) ) )
			{
				return new ComponentName( defaultPkgName , defaultClassName );
			}
			return new ComponentName( strArray[1] , strArray[3] );
		}
		else
		{
			String pkgName = getCurrentLock( mContext , "lockPackageName" );
			if( pkgName == null )
			{
				pkgName = defaultPkgName;
			}
			String className = getCurrentLock( mContext , "lockClassName" );
			if( className == null )
			{
				className = defaultClassName;
			}
			return new ComponentName( pkgName , className );
		}
	}
}
