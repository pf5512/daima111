package com.coco.font.fontbox;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.model.ThemeInfoItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.database.service.HotService;


public class FontService
{
	
	private Context mContext;
	
	public FontService(
			Context context )
	{
		mContext = context;
	}
	
	public List<FontInformation> queryInstallList()
	{
		List<FontInformation> result = new ArrayList<FontInformation>();
		List<ApplicationInfo> infoList = queryFontActivityList();
		for( ApplicationInfo activityInfo : infoList )
		{
			result.add( activityToFont( activityInfo ) );
		}
		return result;
	}
	
	public List<FontInformation> queryDownloadList()
	{
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		List<DownloadThemeItem> downlist = dSv.queryTable( DownloadList.Font_Type );
		List<FontInformation> result = new ArrayList<FontInformation>();
		for( DownloadThemeItem item : downlist )
		{
			FontInformation infor = new FontInformation();
			infor.setDownloadItem( item );
			if( infor.isDownloadFinish() )
				result.add( infor );
		}
		Collections.sort( result , new ByStringValue() );
		return result;
	}
	
	public List<FontInformation> queryHotList()
	{
		HotService hotSv = new HotService( mContext );
		List<ThemeInfoItem> hotlist = hotSv.queryTable( DownloadList.Font_Type );
		List<FontInformation> result = new ArrayList<FontInformation>();
		for( ThemeInfoItem item : hotlist )
		{
			FontInformation infor = new FontInformation();
			infor.setThemeItem( item );
			result.add( infor );
		}
		return result;
	}
	
	public List<FontInformation> queryDownloadListIngoreFinish()
	{
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		List<DownloadThemeItem> downlist = dSv.queryTable( DownloadList.Font_Type );
		List<FontInformation> result = new ArrayList<FontInformation>();
		for( DownloadThemeItem item : downlist )
		{
			FontInformation infor = new FontInformation();
			infor.setDownloadItem( item );
			result.add( infor );
		}
		Collections.sort( result , new ByStringValue() );
		return result;
	}
	
	public List<FontInformation> queryShowList()
	{
		Map<String , FontInformation> allMap = new HashMap<String , FontInformation>();
		List<FontInformation> hotList = queryHotList();
		for( FontInformation item : hotList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<FontInformation> downList = queryDownloadListIngoreFinish();
		for( FontInformation item : downList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<FontInformation> resultList = new ArrayList<FontInformation>();
		for( FontInformation item : hotList )
		{
			resultList.add( allMap.get( item.getPackageName() ) );
		}
		return resultList;
	}
	
	public FontInformation queryFont(
			String packageName )
	{
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		DownloadThemeItem downItem = dSv.queryByPackageName( packageName , DownloadList.Font_Type );
		if( downItem != null )
		{
			FontInformation infor = new FontInformation();
			infor.setDownloadItem( downItem );
			return infor;
		}
		HotService hotSv = new HotService( mContext );
		ThemeInfoItem hotItem = hotSv.queryByPackageName( packageName , DownloadList.Font_Type );
		if( hotItem != null )
		{
			FontInformation infor = new FontInformation();
			infor.setThemeItem( hotItem );
			return infor;
		}
		return null;
	}
	
	class ByStringValue implements Comparator<FontInformation>
	{
		
		@Override
		public int compare(
				FontInformation lhs ,
				FontInformation rhs )
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
	
	public FontInformation queryFont(
			String packageName ,
			String className )
	{
		List<ApplicationInfo> installList = queryFontListByPackageName( packageName );
		for( ApplicationInfo item : installList )
		{
			return activityToFont( item );
		}
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		DownloadThemeItem downItem = dSv.queryByPackageName( packageName , DownloadList.Font_Type );
		if( downItem != null )
		{
			FontInformation infor = new FontInformation();
			infor.setDownloadItem( downItem );
			return infor;
		}
		HotService hotSv = new HotService( mContext );
		ThemeInfoItem hotItem = hotSv.queryByPackageName( packageName , DownloadList.Font_Type );
		if( hotItem != null )
		{
			FontInformation infor = new FontInformation();
			infor.setThemeItem( hotItem );
			return infor;
		}
		return null;
	}
	
	public ComponentName queryComponent(
			String packageName )
	{
		List<ApplicationInfo> installList = queryFontListByPackageName( packageName );
		if( installList.size() > 0 )
		{
			ApplicationInfo item = installList.get( 0 );
			return new ComponentName( item.packageName , item.name );
		}
		return null;
	}
	
	private List<ApplicationInfo> queryFontActivityList()
	{
		List<ApplicationInfo> resultList = new ArrayList<ApplicationInfo>();
		PackageManager pm = mContext.getPackageManager();
		List<ApplicationInfo> appinfo = pm.getInstalledApplications( PackageManager.GET_DISABLED_COMPONENTS );
		for( ApplicationInfo info : appinfo )
		{
			if( info.packageName.startsWith( "com.monotype.android.font.moppo." ) )
			{
				resultList.add( info );
			}
			else if( info.packageName.equals( mContext.getPackageName() ) )
			{
				resultList.add( info );
			}
		}
		return resultList;
	}
	
	private List<ApplicationInfo> queryFontListByPackageName(
			String pkgName )
	{
		List<ApplicationInfo> resultList = new ArrayList<ApplicationInfo>();
		List<ApplicationInfo> appinfo = mContext.getPackageManager().getInstalledApplications( PackageManager.GET_DISABLED_COMPONENTS );
		for( ApplicationInfo info : appinfo )
		{
			if( info.packageName.equals( pkgName ) )
			{
				resultList.add( info );
			}
		}
		return resultList;
	}
	
	private FontInformation activityToFont(
			ApplicationInfo activityInfo )
	{
		FontInformation item = new FontInformation();
		item.setActivity( mContext , activityInfo );
		return item;
	}
}
