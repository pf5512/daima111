package com.coco.lock2.lockbox.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;

import com.coco.download.DownloadList;
import com.coco.lock2.lockbox.AppConfig;
import com.coco.lock2.lockbox.LockInformation;
import com.coco.lock2.lockbox.PlatformInfo;
import com.coco.lock2.lockbox.StaticClass;
import com.coco.pub.provider.PubProviderHelper;
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.model.ThemeInfoItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.database.service.HotService;
import com.coco.theme.themebox.util.Log;


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
	
	public List<LockInformation> queryInstallList()
	{
		Intent intentLockView = new Intent( StaticClass.ACTION_LOCK_VIEW );
		intentLockView.addCategory( Intent.CATEGORY_INFO );
		List<ResolveInfo> infoList = mContext.getPackageManager().queryIntentActivities( intentLockView , 0 );
		List<LockInformation> result = new ArrayList<LockInformation>();
		for( ResolveInfo info : infoList )
		{
			LockInformation infor = new LockInformation();
			infor.setActivity( mContext , info.activityInfo );
			result.add( infor );
		}
		intentLockView.setAction( "com.coco.third.lock.action.VIEW" );
		intentLockView.addCategory( Intent.CATEGORY_INFO );
		infoList = mContext.getPackageManager().queryIntentActivities( intentLockView , 0 );
		for( ResolveInfo info : infoList )
		{
			LockInformation infor = new LockInformation();
			infor.setActivity( mContext , info.activityInfo );
			result.add( infor );
		}
		return result;
	}
	
	public List<LockInformation> queryShowList()
	{
		Map<String , LockInformation> allMap = new HashMap<String , LockInformation>();
		List<LockInformation> hotList = queryHotList();
		for( LockInformation item : hotList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<LockInformation> downList = queryDownloadList();
		for( LockInformation item : downList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<LockInformation> installList = queryInstallList();
		for( LockInformation item : installList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<LockInformation> resultList = new ArrayList<LockInformation>();
		for( LockInformation item : hotList )
		{
			resultList.add( allMap.get( item.getPackageName() ) );
		}
		return resultList;
	}
	
	public List<LockInformation> queryDownloadList()
	{
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		List<DownloadThemeItem> downlist = dSv.queryTable( DownloadList.Lock_Type );
		List<LockInformation> result = new ArrayList<LockInformation>();
		for( DownloadThemeItem item : downlist )
		{
			LockInformation infor = new LockInformation();
			infor.setDownloadItem( item );
			result.add( infor );
		}
		return result;
	}
	
	public List<LockInformation> queryHotList()
	{
		HotService hotSv = new HotService( mContext );
		List<ThemeInfoItem> hotList = hotSv.queryTable( DownloadList.Lock_Type );
		List<LockInformation> result = new ArrayList<LockInformation>();
		for( ThemeInfoItem item : hotList )
		{
			LockInformation infor = new LockInformation();
			infor.setThemeItem( item );
			result.add( infor );
		}
		return result;
	}
	
	public ComponentName queryCurrentLock()
	{
		SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences( mContext );
		String defaultPkgName = AppConfig.getInstance( mContext ).getDefaultLockscreenPackage();
		String defaultClassName = AppConfig.getInstance( mContext ).getDefaultLockscreenClass();
		if( PlatformInfo.getInstance( mContext ).isSupportViewLock() )
		{
			String settingStr = android.provider.Settings.System.getString( mContext.getContentResolver() , SYS_SETTING_CURRENT_LOCK );
			Log.d( LOG_TAG , "settingStr=" + settingStr );
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
			String pkgName = sharedPre.getString( StaticClass.USE_LOCK_PACKAGE_NAME , defaultPkgName );
			String className = sharedPre.getString( StaticClass.USE_LOCK_CLASS_NAME , defaultClassName );
			return new ComponentName( pkgName , className );
		}
	}
	
	public ComponentName queryCurrentWrap()
	{
		SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences( mContext );
		String defaultPkgName = sharedPre.getString( StaticClass.DEFAULT_LOCKSCREEN_PACKAGE , AppConfig.getInstance( mContext ).getDefaultLockscreenPackage() );
		String defaultWrap = sharedPre.getString( StaticClass.DEFAULT_LOCKSCREEN_WRAP , AppConfig.getInstance( mContext ).getDefaultLockscreenWrap() );
		if( PlatformInfo.getInstance( mContext ).isSupportViewLock() )
		{
			String settingStr = android.provider.Settings.System.getString( mContext.getContentResolver() , SYS_SETTING_CURRENT_LOCK );
			Log.d( LOG_TAG , "settingStr=" + settingStr );
			if( settingStr == null )
			{
				return new ComponentName( defaultPkgName , defaultWrap );
			}
			String[] strArray = settingStr.split( "," );
			if( strArray.length < 4 || !strArray[0].equals( Integer.toString( SYS_SETTING_LOCK_VERSION ) ) )
			{
				return new ComponentName( defaultPkgName , defaultWrap );
			}
			return new ComponentName( strArray[1] , strArray[2] );
		}
		else
		{
			String pkgName = sharedPre.getString( StaticClass.USE_LOCK_PACKAGE_NAME , defaultPkgName );
			String className = sharedPre.getString( StaticClass.USE_LOCK_WRAP_NAME , defaultWrap );
			return new ComponentName( pkgName , className );
		}
	}
	
	public void applyLock(
			String packageName ,
			String className ,
			String wrapName )
	{
		if( PlatformInfo.getInstance( mContext ).isSupportViewLock() )
		{
			String strValue = String.format( Locale.getDefault() , "%d,%s,%s,%s" , SYS_SETTING_LOCK_VERSION , packageName , wrapName , className );
			Log.d( LOG_TAG , strValue );
			android.provider.Settings.System.putString( mContext.getContentResolver() , SYS_SETTING_CURRENT_LOCK , strValue );
		}
		else
		{
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences( mContext ).edit();
			editor.putString( StaticClass.USE_LOCK_PACKAGE_NAME , packageName );
			editor.putString( StaticClass.USE_LOCK_CLASS_NAME , className );
			editor.putString( StaticClass.USE_LOCK_WRAP_NAME , wrapName );
			editor.commit();
			PubProviderHelper.addOrUpdateValue( "lock" , StaticClass.USE_LOCK_PACKAGE_NAME , packageName );
			PubProviderHelper.addOrUpdateValue( "lock" , StaticClass.USE_LOCK_CLASS_NAME , className );
			PubProviderHelper.addOrUpdateValue( "lock" , StaticClass.USE_LOCK_WRAP_NAME , wrapName );
		}
	}
	
	public LockInformation queryLock(
			String packageName ,
			String className )
	{
		if( className != null && !className.equals( "" ) )
		{
			Intent intentLock = new Intent( StaticClass.ACTION_LOCK_VIEW );
			intentLock.addCategory( Intent.CATEGORY_INFO );
			intentLock.setPackage( packageName );
			List<ResolveInfo> infoList = mContext.getPackageManager().queryIntentActivities( intentLock , 0 );
			for( ResolveInfo item : infoList )
			{
				if( item.activityInfo.packageName.equals( packageName ) && item.activityInfo.name.equals( className ) )
				{
					LockInformation infor = new LockInformation();
					infor.setActivity( mContext , item.activityInfo );
					return infor;
				}
			}
			intentLock.setAction( "com.coco.third.lock.action.VIEW" );
			intentLock.addCategory( Intent.CATEGORY_INFO );
			intentLock.setPackage( packageName );
			infoList = mContext.getPackageManager().queryIntentActivities( intentLock , 0 );
			for( ResolveInfo item : infoList )
			{
				if( item.activityInfo.packageName.equals( packageName ) && item.activityInfo.name.equals( className ) )
				{
					LockInformation infor = new LockInformation();
					infor.setActivity( mContext , item.activityInfo );
					return infor;
				}
			}
		}
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		DownloadThemeItem downItem = dSv.queryByPackageName( packageName , DownloadList.Lock_Type );
		if( downItem != null )
		{
			LockInformation infor = new LockInformation();
			infor.setDownloadItem( downItem );
			return infor;
		}
		HotService hotSv = new HotService( mContext );
		ThemeInfoItem hotItem = hotSv.queryByPackageName( packageName , DownloadList.Lock_Type );
		if( hotItem != null )
		{
			LockInformation infor = new LockInformation();
			infor.setThemeItem( hotItem );
			return infor;
		}
		return null;
	}
	
	public ComponentName queryComponent(
			String packageName )
	{
		Intent intentLock = new Intent( StaticClass.ACTION_LOCK_VIEW );
		intentLock.addCategory( Intent.CATEGORY_INFO );
		intentLock.setPackage( packageName );
		List<ResolveInfo> infoList = mContext.getPackageManager().queryIntentActivities( intentLock , 0 );
		if( infoList != null )
		{
			if( infoList.size() > 0 )
			{
				ResolveInfo item = infoList.get( 0 );
				return new ComponentName( packageName , item.activityInfo.name );
			}
		}
		intentLock.setAction( "com.coco.third.lock.action.VIEW" );
		intentLock.addCategory( Intent.CATEGORY_INFO );
		intentLock.setPackage( packageName );
		infoList = mContext.getPackageManager().queryIntentActivities( intentLock , 0 );
		if( infoList != null )
		{
			if( infoList.size() > 0 )
			{
				ResolveInfo item = infoList.get( 0 );
				return new ComponentName( packageName , item.activityInfo.name );
			}
		}
		return null;
	}
	
	public boolean isenableCooeeLock()
	{// 判断当前锁屏是否存在
		ComponentName comName = queryCurrentLock();
		Intent intentActivity = new Intent();
		intentActivity.setComponent( comName );
		intentActivity.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS );
		Log.v( "service" , "startLockActivity comName = " + comName );
		ResolveInfo info = mContext.getPackageManager().resolveActivity( intentActivity , 0 );
		if( info != null )
		{
			return true;
		}
		return false;
	}
}
