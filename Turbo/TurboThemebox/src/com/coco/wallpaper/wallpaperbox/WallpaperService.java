package com.coco.wallpaper.wallpaperbox;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.model.ThemeInfoItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.database.service.HotService;


public class WallpaperService
{
	
	private Context mContext;
	
	public WallpaperService(
			Context context )
	{
		mContext = context;
	}
	
	public List<WallpaperInformation> queryDownloadList()
	{
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		List<DownloadThemeItem> downlist = dSv.queryTable( DownloadList.Wallpaper_Type );
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
		List<ThemeInfoItem> hotlist = hotSv.queryTable( DownloadList.Wallpaper_Type );
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
		List<DownloadThemeItem> downlist = dSv.queryTable( DownloadList.Wallpaper_Type );
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
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		DownloadThemeItem downItem = dSv.queryByPackageName( packageName , DownloadList.Wallpaper_Type );
		if( downItem != null )
		{
			infor.setDownloadItem( downItem );
			return;
		}
		HotService hotSv = new HotService( mContext );
		ThemeInfoItem hotItem = hotSv.queryByPackageName( packageName , DownloadList.Wallpaper_Type );
		if( hotItem != null )
		{
			infor.setThemeItem( hotItem );
			return;
		}
	}
	
	public WallpaperInformation queryWallpaper(
			String packageName )
	{
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		DownloadThemeItem downItem = dSv.queryByPackageName( packageName , DownloadList.Wallpaper_Type );
		if( downItem != null )
		{
			WallpaperInformation infor = new WallpaperInformation();
			infor.setDownloadItem( downItem );
			return infor;
		}
		HotService hotSv = new HotService( mContext );
		ThemeInfoItem hotItem = hotSv.queryByPackageName( packageName , DownloadList.Wallpaper_Type );
		if( hotItem != null )
		{
			WallpaperInformation infor = new WallpaperInformation();
			infor.setThemeItem( hotItem );
			return infor;
		}
		return null;
	}
	
	class ByStringValue implements Comparator<WallpaperInformation>
	{
		
		@Override
		public int compare(
				WallpaperInformation lhs ,
				WallpaperInformation rhs )
		{
			// TODO Auto-generated method stub
			if( lhs.getPackageName().compareTo( rhs.getPackageName() ) > 0 )
			{
				return 1;
			}
			else if( lhs.getPackageName().compareTo( rhs.getPackageName() ) < 0 )
			{
				return -1;
			}
			return 0;
		}
	}
}
