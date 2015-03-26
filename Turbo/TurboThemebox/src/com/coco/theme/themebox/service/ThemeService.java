package com.coco.theme.themebox.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.ThemeInformation;
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.model.ThemeInfoItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.database.service.HotService;


public class ThemeService
{
	
	private Context mContext;
	
	public ThemeService(
			Context context )
	{
		mContext = context;
	}
	
	public List<ThemeInformation> queryInstallList()
	{
		List<ThemeInformation> result = new ArrayList<ThemeInformation>();
		List<ActivityInfo> infoList = queryThemeActivityList();
		for( ActivityInfo activityInfo : infoList )
		{
			result.add( activityToTheme( activityInfo ) );
		}
		return result;
	}
	
	public List<ThemeInformation> queryDownloadList()
	{
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		List<DownloadThemeItem> downlist = dSv.queryTable( DownloadList.Theme_Type );
		List<ThemeInformation> result = new ArrayList<ThemeInformation>();
		for( DownloadThemeItem item : downlist )
		{
			ThemeInformation infor = new ThemeInformation();
			infor.setDownloadItem( item );
			result.add( infor );
		}
		return result;
	}
	
	public List<ThemeInformation> queryHotList()
	{
		HotService hotSv = new HotService( mContext );
		List<ThemeInfoItem> hotlist = hotSv.queryTable( DownloadList.Theme_Type );
		List<ThemeInformation> result = new ArrayList<ThemeInformation>();
		for( ThemeInfoItem item : hotlist )
		{
			ThemeInformation infor = new ThemeInformation();
			infor.setThemeItem( item );
			result.add( infor );
		}
		return result;
	}
	
	public List<ThemeInformation> queryShowList()
	{
		Map<String , ThemeInformation> allMap = new HashMap<String , ThemeInformation>();
		List<ThemeInformation> hotList = queryHotList();
		for( ThemeInformation item : hotList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<ThemeInformation> downList = queryDownloadList();
		for( ThemeInformation item : downList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<ThemeInformation> installList = queryInstallList();
		for( ThemeInformation item : installList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<ThemeInformation> resultList = new ArrayList<ThemeInformation>();
		for( ThemeInformation item : hotList )
		{
			resultList.add( allMap.get( item.getPackageName() ) );
		}
		return resultList;
	}
	
	public ComponentName queryCurrentTheme()
	{
		ThemesDB db = new ThemesDB( mContext );
		ThemeConfig cfg = db.getTheme();
		List<ActivityInfo> activityList = queryThemeActivityList();
		if( activityList.size() <= 0 )
		{
			return new ComponentName( "" , "" );
		}
		for( ActivityInfo info : activityList )
		{
			if( info.packageName.equals( cfg.theme ) )
			{
				return new ComponentName( info.packageName , info.name );
			}
		}
		return new ComponentName( activityList.get( 0 ).packageName , activityList.get( 0 ).name );
	}
	
	public boolean applyTheme(
			ComponentName apply )
	{
		ThemesDB db = new ThemesDB( mContext );
		db.SaveThemes( apply.getPackageName() );
		if( ThemesDB.ACTION_LAUNCHER_RESTART == null || ThemesDB.ACTION_LAUNCHER_RESTART.equals( "" ) )
		{
			ThemesDB.ACTION_LAUNCHER_RESTART = "com.coco.launcher.restart";
		}
		mContext.sendBroadcast( new Intent( ThemesDB.ACTION_LAUNCHER_RESTART ) );
		return true;
	}
	
	public ThemeInformation queryTheme(
			String packageName ,
			String className )
	{
		if( className != null && !className.equals( "" ) )
		{
			List<ActivityInfo> installList = queryThemeListByPackageName( packageName );
			for( ActivityInfo item : installList )
			{
				if( item.name.equals( className ) )
				{
					return activityToTheme( item );
				}
			}
		}
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		DownloadThemeItem downItem = dSv.queryByPackageName( packageName , DownloadList.Theme_Type );
		if( downItem != null )
		{
			ThemeInformation infor = new ThemeInformation();
			infor.setDownloadItem( downItem );
			return infor;
		}
		HotService hotSv = new HotService( mContext );
		ThemeInfoItem hotItem = hotSv.queryByPackageName( packageName , DownloadList.Theme_Type );
		if( hotItem != null )
		{
			ThemeInformation infor = new ThemeInformation();
			infor.setThemeItem( hotItem );
			return infor;
		}
		return null;
	}
	
	public ComponentName queryComponent(
			String packageName )
	{
		List<ActivityInfo> installList = queryThemeListByPackageName( packageName );
		if( installList.size() > 0 )
		{
			ActivityInfo item = installList.get( 0 );
			return new ComponentName( item.packageName , item.name );
		}
		return null;
	}
	
	public List<ActivityInfo> queryThemeActivityList()
	{
		List<ActivityInfo> resultList = new ArrayList<ActivityInfo>();
		Intent intent = new Intent( ThemeManager.ACTION_INTENT_THEME , null );
		List<ResolveInfo> themesinfo = mContext.getPackageManager().queryIntentActivities( intent , 0 );
		Collections.sort( themesinfo , new ResolveInfo.DisplayNameComparator( mContext.getPackageManager() ) );
		for( ResolveInfo info : themesinfo )
		{
			resultList.add( info.activityInfo );
		}
		Intent systemmain = new Intent( "android.intent.action.MAIN" , null );
		systemmain.addCategory( "android.intent.category.LAUNCHER" );
		systemmain.addCategory( "android.intent.category.Theme" );
		systemmain.setPackage( ThemesDB.LAUNCHER_PACKAGENAME );
		themesinfo = mContext.getPackageManager().queryIntentActivities( systemmain , 0 );
		if( themesinfo != null )
		{
			for( ResolveInfo info : themesinfo )
			{
				resultList.add( info.activityInfo );
			}
		}
		return resultList;
	}
	
	private List<ActivityInfo> queryThemeListByPackageName(
			String pkgName )
	{
		List<ActivityInfo> resultList = new ArrayList<ActivityInfo>();
		Intent intent = new Intent( ThemeManager.ACTION_INTENT_THEME , null );
		intent.setPackage( pkgName );
		List<ResolveInfo> themesinfo = mContext.getPackageManager().queryIntentActivities( intent , 0 );
		Collections.sort( themesinfo , new ResolveInfo.DisplayNameComparator( mContext.getPackageManager() ) );
		for( ResolveInfo info : themesinfo )
		{
			resultList.add( info.activityInfo );
		}
		if( pkgName == null || pkgName.equals( ThemesDB.LAUNCHER_PACKAGENAME ) )
		{
			Intent systemmain = new Intent( "android.intent.action.MAIN" , null );
			systemmain.addCategory( "android.intent.category.LAUNCHER" );
			systemmain.setPackage( ThemesDB.LAUNCHER_PACKAGENAME );
			themesinfo = mContext.getPackageManager().queryIntentActivities( systemmain , 0 );
			for( ResolveInfo info : themesinfo )
			{
				resultList.add( info.activityInfo );
			}
		}
		return resultList;
	}
	
	private ThemeInformation activityToTheme(
			ActivityInfo activityInfo )
	{
		ThemeInformation themeItem = new ThemeInformation();
		themeItem.setActivity( mContext , activityInfo );
		return themeItem;
	}
}
