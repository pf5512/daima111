package com.coco.wallpaper.wallpaperbox;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.model.ThemeInfoItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.database.service.HotService;


public class LiveWallpaperService extends WallpaperService
{
	
	private Context mContext;
	
	public LiveWallpaperService(
			Context context )
	{
		super( context );
		mContext = context;
	}
	
	public List<WallpaperInformation> queryInstallList()
	{
		List<WallpaperInformation> result = new ArrayList<WallpaperInformation>();
		List<ServiceInfo> infoList = queryLiveWallpaperServiceList();
		for( ServiceInfo activityInfo : infoList )
		{
			result.add( serviceToLiveWallpaper( activityInfo ) );
		}
		return result;
	}
	
	public List<WallpaperInformation> queryDownloadList()
	{
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		List<DownloadThemeItem> downlist = dSv.queryTable( DownloadList.LiveWallpaper_Type );
		List<WallpaperInformation> result = new ArrayList<WallpaperInformation>();
		for( DownloadThemeItem item : downlist )
		{
			WallpaperInformation infor = new WallpaperInformation();
			infor.setDownloadItem( item );
			if( infor.isDownloadedFinish() )
				result.add( infor );
		}
		Collections.sort( result , new ByStringValue() );
		return result;
	}
	
	public List<WallpaperInformation> queryHotList()
	{
		HotService hotSv = new HotService( mContext );
		List<ThemeInfoItem> hotlist = hotSv.queryTable( DownloadList.LiveWallpaper_Type );
		List<WallpaperInformation> result = new ArrayList<WallpaperInformation>();
		for( ThemeInfoItem item : hotlist )
		{
			WallpaperInformation infor = new WallpaperInformation();
			infor.setThemeItem( item );
			result.add( infor );
		}
		return result;
	}
	
	public List<WallpaperInformation> queryDownloadListIngoreFinish()
	{
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		List<DownloadThemeItem> downlist = dSv.queryTable( DownloadList.LiveWallpaper_Type );
		List<WallpaperInformation> result = new ArrayList<WallpaperInformation>();
		for( DownloadThemeItem item : downlist )
		{
			WallpaperInformation infor = new WallpaperInformation();
			infor.setDownloadItem( item );
			result.add( infor );
		}
		Collections.sort( result , new ByStringValue() );
		return result;
	}
	
	public List<WallpaperInformation> queryShowList()
	{
		Map<String , WallpaperInformation> allMap = new HashMap<String , WallpaperInformation>();
		List<WallpaperInformation> hotList = queryHotList();
		for( WallpaperInformation item : hotList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<WallpaperInformation> downList = queryDownloadListIngoreFinish();
		for( WallpaperInformation item : downList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<WallpaperInformation> installList = queryInstallList();
		for( WallpaperInformation item : installList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<WallpaperInformation> resultList = new ArrayList<WallpaperInformation>();
		for( WallpaperInformation item : hotList )
		{
			resultList.add( allMap.get( item.getPackageName() ) );
		}
		return resultList;
	}
	
	public void queryWallpaper(
			String packageName ,
			WallpaperInformation infor )
	{
		String className = infor.getClassName();
		if( className != null && !className.equals( "" ) )
		{
			List<ServiceInfo> installList = queryLiveWallpaperServiceList();
			for( ServiceInfo item : installList )
			{
				if( item.packageName.equals( packageName ) && item.name.equals( className ) )
				{
					infor.copy( serviceToLiveWallpaper( item ) );
					return;
				}
			}
		}
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		DownloadThemeItem downItem = dSv.queryByPackageName( packageName , DownloadList.LiveWallpaper_Type );
		if( downItem != null )
		{
			infor.setDownloadItem( downItem );
			return;
		}
		HotService hotSv = new HotService( mContext );
		ThemeInfoItem hotItem = hotSv.queryByPackageName( packageName , DownloadList.LiveWallpaper_Type );
		if( hotItem != null )
		{
			infor.setThemeItem( hotItem );
			return;
		}
	}
	
	public WallpaperInformation queryWallpaper(
			String packageName ,
			String className )
	{
		if( className != null && !className.equals( "" ) )
		{
			List<ServiceInfo> installList = queryLiveWallpaperServiceList();
			for( ServiceInfo item : installList )
			{
				if( item.name.equals( className ) )
				{
					return serviceToLiveWallpaper( item );
				}
			}
		}
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		DownloadThemeItem downItem = dSv.queryByPackageName( packageName , DownloadList.LiveWallpaper_Type );
		if( downItem != null )
		{
			WallpaperInformation infor = new WallpaperInformation();
			infor.setDownloadItem( downItem );
			return infor;
		}
		HotService hotSv = new HotService( mContext );
		ThemeInfoItem hotItem = hotSv.queryByPackageName( packageName , DownloadList.LiveWallpaper_Type );
		if( hotItem != null )
		{
			WallpaperInformation infor = new WallpaperInformation();
			infor.setThemeItem( hotItem );
			return infor;
		}
		return null;
	}
	
	private List<ServiceInfo> queryLiveWallpaperServiceList()
	{
		List<ServiceInfo> resultList = new ArrayList<ServiceInfo>();
		ArrayList<ResolveInfo> reslist = new ArrayList<ResolveInfo>();
		Intent intent = new Intent( android.service.wallpaper.WallpaperService.SERVICE_INTERFACE );
		// Intent("com.cooee", null);
		PackageManager pm = mContext.getPackageManager();
		List<ResolveInfo> themesinfo = pm.queryIntentServices( intent , PackageManager.GET_META_DATA );
		Collections.sort( themesinfo , new ResolveInfo.DisplayNameComparator( mContext.getPackageManager() ) );
		int themescount = themesinfo.size();
		for( int index = 0 ; index < themescount ; index++ )
		{
			ResolveInfo resinfo = themesinfo.get( index );
			//			if( resinfo.serviceInfo.packageName.startsWith( "com.cooee" ) )
			{
				reslist.add( resinfo );
			}
		}
		for( ResolveInfo info : reslist )
		{
			resultList.add( info.serviceInfo );
		}
		themesinfo.clear();
		Intent localIntent1 = new Intent( "android.intent.action.MAIN" , null );
		localIntent1.addCategory( "com.vlife.coco.intent.category.VLIFE_SET_WALLPAPER" );
		themesinfo = pm.queryIntentServices( localIntent1 , 0 );
		for( ResolveInfo info : themesinfo )
		{
			resultList.add( info.serviceInfo );
		}
		return resultList;
	}
	
	private WallpaperInformation serviceToLiveWallpaper(
			ServiceInfo serviceInfo )
	{
		WallpaperInformation item = new WallpaperInformation();
		item.setService( mContext , serviceInfo );
		return item;
	}
}
