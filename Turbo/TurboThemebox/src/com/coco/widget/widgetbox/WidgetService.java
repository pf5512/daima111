package com.coco.widget.widgetbox;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.model.ThemeInfoItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.database.service.HotService;


public class WidgetService
{
	
	private Context mContext;
	
	public WidgetService(
			Context context )
	{
		mContext = context;
	}
	
	public List<WidgetInformation> queryInstallList()
	{
		List<WidgetInformation> result = new ArrayList<WidgetInformation>();
		List<ActivityInfo> infoList = queryWidgetActivityList();
		for( ActivityInfo activityInfo : infoList )
		{
			result.add( activityToWidget( activityInfo ) );
		}
		List<AppWidgetProviderInfo> widgets = AppWidgetManager.getInstance( mContext ).getInstalledProviders();
		for( AppWidgetProviderInfo widget : widgets )
		{
			String pkg = widget.provider.getPackageName();
			if( pkg.contains( "com.iLoong" ) || pkg.contains( "com.CoCo" ) || pkg.contains( "com.cooee" ) || pkg.contains( "com.coco" ) )
			{
				result.add( activityToWidget( widget ) );
			}
		}
		return result;
	}
	
	public List<WidgetInformation> queryDownloadList()
	{
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		List<DownloadThemeItem> downlist = dSv.queryTable( DownloadList.Widget_Type );
		List<WidgetInformation> result = new ArrayList<WidgetInformation>();
		for( DownloadThemeItem item : downlist )
		{
			WidgetInformation infor = new WidgetInformation();
			infor.setDownloadItem( item );
			result.add( infor );
		}
		return result;
	}
	
	public List<WidgetInformation> queryHotList()
	{
		HotService hotSv = new HotService( mContext );
		List<ThemeInfoItem> hotlist = hotSv.queryTable( DownloadList.Widget_Type );
		List<WidgetInformation> result = new ArrayList<WidgetInformation>();
		for( ThemeInfoItem item : hotlist )
		{
			WidgetInformation infor = new WidgetInformation();
			infor.setThemeItem( item );
			result.add( infor );
		}
		return result;
	}
	
	public List<WidgetInformation> queryShowList()
	{
		Map<String , WidgetInformation> allMap = new HashMap<String , WidgetInformation>();
		List<WidgetInformation> hotList = queryHotList();
		for( WidgetInformation item : hotList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<WidgetInformation> downList = queryDownloadList();
		for( WidgetInformation item : downList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<WidgetInformation> installList = queryInstallList();
		for( WidgetInformation item : installList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<WidgetInformation> resultList = new ArrayList<WidgetInformation>();
		for( WidgetInformation item : hotList )
		{
			resultList.add( allMap.get( item.getPackageName() ) );
		}
		return resultList;
	}
	
	public WidgetInformation queryWidget(
			String packageName ,
			String className )
	{
		if( className != null && !className.equals( "" ) )
		{
			List<ActivityInfo> installList = queryWidgetListByPackageName( packageName );
			for( ActivityInfo item : installList )
			{
				if( item.name.equals( className ) )
				{
					return activityToWidget( item );
				}
			}
			List<AppWidgetProviderInfo> widgets = AppWidgetManager.getInstance( mContext ).getInstalledProviders();
			for( AppWidgetProviderInfo widget : widgets )
			{
				String pkg = widget.provider.getPackageName();
				if( pkg.equals( packageName ) )
				{
					return activityToWidget( widget );
				}
			}
		}
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		DownloadThemeItem downItem = dSv.queryByPackageName( packageName , DownloadList.Widget_Type );
		if( downItem != null )
		{
			WidgetInformation infor = new WidgetInformation();
			infor.setDownloadItem( downItem );
			return infor;
		}
		HotService hotSv = new HotService( mContext );
		ThemeInfoItem hotItem = hotSv.queryByPackageName( packageName , DownloadList.Widget_Type );
		if( hotItem != null )
		{
			WidgetInformation infor = new WidgetInformation();
			infor.setThemeItem( hotItem );
			return infor;
		}
		return null;
	}
	
	public ComponentName queryComponent(
			String packageName )
	{
		List<ActivityInfo> installList = queryWidgetListByPackageName( packageName );
		if( installList.size() > 0 )
		{
			ActivityInfo item = installList.get( 0 );
			return new ComponentName( item.packageName , item.name );
		}
		List<AppWidgetProviderInfo> widgets = AppWidgetManager.getInstance( mContext ).getInstalledProviders();
		for( AppWidgetProviderInfo widget : widgets )
		{
			String pkg = widget.provider.getPackageName();
			if( pkg.equals( packageName ) )
			{
				return widget.provider.clone();
			}
		}
		return null;
	}
	
	private List<ActivityInfo> queryWidgetActivityList()
	{
		List<ActivityInfo> resultList = new ArrayList<ActivityInfo>();
		ArrayList<ResolveInfo> reslist = new ArrayList<ResolveInfo>();
		Intent intent = new Intent( "com.iLoong.widget" , null );
		PackageManager pm = mContext.getPackageManager();
		List<ResolveInfo> themesinfo = pm.queryIntentActivities( intent , PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA );
		Collections.sort( themesinfo , new ResolveInfo.DisplayNameComparator( mContext.getPackageManager() ) );
		int themescount = themesinfo.size();
		for( int index = 0 ; index < themescount ; index++ )
		{
			ResolveInfo resinfo = themesinfo.get( index );
			reslist.add( resinfo );
		}
		for( ResolveInfo info : reslist )
		{
			resultList.add( info.activityInfo );
		}
		return resultList;
	}
	
	private List<ActivityInfo> queryWidgetListByPackageName(
			String pkgName )
	{
		List<ActivityInfo> resultList = new ArrayList<ActivityInfo>();
		Intent intent = new Intent( "com.iLoong.widget" , null );
		intent.setPackage( pkgName );
		List<ResolveInfo> themesinfo = mContext.getPackageManager().queryIntentActivities( intent , 0 );
		Collections.sort( themesinfo , new ResolveInfo.DisplayNameComparator( mContext.getPackageManager() ) );
		for( ResolveInfo info : themesinfo )
		{
			resultList.add( info.activityInfo );
		}
		return resultList;
	}
	
	private WidgetInformation activityToWidget(
			ActivityInfo activityInfo )
	{
		WidgetInformation item = new WidgetInformation();
		item.setActivity( mContext , activityInfo );
		return item;
	}
	
	private WidgetInformation activityToWidget(
			AppWidgetProviderInfo provider )
	{
		WidgetInformation item = new WidgetInformation();
		item.setActivity( mContext , provider );
		return item;
	}
}
