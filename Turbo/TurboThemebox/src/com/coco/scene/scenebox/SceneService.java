package com.coco.scene.scenebox;


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
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.model.ThemeInfoItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.database.service.HotService;


public class SceneService
{
	
	private Context mContext;
	
	public SceneService(
			Context context )
	{
		mContext = context;
	}
	
	public List<SceneInformation> queryInstallList()
	{
		List<SceneInformation> result = new ArrayList<SceneInformation>();
		List<ActivityInfo> infoList = querySceneActivityList();
		for( ActivityInfo activityInfo : infoList )
		{
			result.add( activityToScene( activityInfo ) );
		}
		return result;
	}
	
	public List<SceneInformation> queryDownloadList()
	{
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		List<DownloadThemeItem> downlist = dSv.queryTable( DownloadList.Scene_Type );
		List<SceneInformation> result = new ArrayList<SceneInformation>();
		for( DownloadThemeItem item : downlist )
		{
			SceneInformation infor = new SceneInformation();
			infor.setDownloadItem( item );
			result.add( infor );
		}
		return result;
	}
	
	public List<SceneInformation> queryHotList()
	{
		HotService hotSv = new HotService( mContext );
		List<ThemeInfoItem> hotlist = hotSv.queryTable( DownloadList.Scene_Type );
		List<SceneInformation> result = new ArrayList<SceneInformation>();
		for( ThemeInfoItem item : hotlist )
		{
			SceneInformation infor = new SceneInformation();
			infor.setThemeItem( item );
			result.add( infor );
		}
		return result;
	}
	
	public List<SceneInformation> queryShowList()
	{
		Map<String , SceneInformation> allMap = new HashMap<String , SceneInformation>();
		List<SceneInformation> hotList = queryHotList();
		for( SceneInformation item : hotList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<SceneInformation> downList = queryDownloadList();
		for( SceneInformation item : downList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<SceneInformation> installList = queryInstallList();
		for( SceneInformation item : installList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<SceneInformation> resultList = new ArrayList<SceneInformation>();
		for( SceneInformation item : hotList )
		{
			resultList.add( allMap.get( item.getPackageName() ) );
		}
		return resultList;
	}
	
	public ComponentName queryCurrentScene()
	{
		SceneDB db = new SceneDB( mContext );
		SceneConfig cfg = db.getScene();
		List<ActivityInfo> activityList = querySceneActivityList();
		if( activityList.size() <= 0 )
		{
			return new ComponentName( "" , "" );
		}
		for( ActivityInfo info : activityList )
		{
			if( info.packageName.equals( cfg.scene ) )
			{
				return new ComponentName( info.packageName , info.name );
			}
		}
		return new ComponentName( "" , "" );
	}
	
	public boolean applyTheme(
			ComponentName apply )
	{
		SceneDB db = new SceneDB( mContext );
		db.saveScene( apply.getPackageName() );
		return true;
	}
	
	public SceneInformation queryScene(
			String packageName ,
			String className )
	{
		if( className != null && !className.equals( "" ) )
		{
			List<ActivityInfo> installList = querySceneListByPackageName( packageName );
			for( ActivityInfo item : installList )
			{
				if( item.name.equals( className ) )
				{
					return activityToScene( item );
				}
			}
		}
		DownloadThemeService dSv = new DownloadThemeService( mContext );
		DownloadThemeItem downItem = dSv.queryByPackageName( packageName , DownloadList.Scene_Type );
		if( downItem != null )
		{
			SceneInformation infor = new SceneInformation();
			infor.setDownloadItem( downItem );
			return infor;
		}
		HotService hotSv = new HotService( mContext );
		ThemeInfoItem hotItem = hotSv.queryByPackageName( packageName , DownloadList.Scene_Type );
		if( hotItem != null )
		{
			SceneInformation infor = new SceneInformation();
			infor.setThemeItem( hotItem );
			return infor;
		}
		return new SceneInformation();
	}
	
	public ComponentName queryComponent(
			String packageName )
	{
		List<ActivityInfo> installList = querySceneListByPackageName( packageName );
		if( installList.size() > 0 )
		{
			ActivityInfo item = installList.get( 0 );
			return new ComponentName( item.packageName , item.name );
		}
		return null;
	}
	
	private List<ActivityInfo> querySceneActivityList()
	{
		List<ActivityInfo> resultList = new ArrayList<ActivityInfo>();
		ArrayList<ResolveInfo> reslist = new ArrayList<ResolveInfo>();
		Intent intent = null;
		intent = new Intent( "com.cooee.scene" , null );
		List<ResolveInfo> themesinfo = mContext.getPackageManager().queryIntentActivities( intent , 0 );
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
	
	private List<ActivityInfo> querySceneListByPackageName(
			String pkgName )
	{
		List<ActivityInfo> resultList = new ArrayList<ActivityInfo>();
		Intent intent = new Intent( "com.cooee.scene" , null );
		intent.setPackage( pkgName );
		List<ResolveInfo> themesinfo = mContext.getPackageManager().queryIntentActivities( intent , 0 );
		Collections.sort( themesinfo , new ResolveInfo.DisplayNameComparator( mContext.getPackageManager() ) );
		for( ResolveInfo info : themesinfo )
		{
			resultList.add( info.activityInfo );
		}
		return resultList;
	}
	
	private SceneInformation activityToScene(
			ActivityInfo activityInfo )
	{
		SceneInformation item = new SceneInformation();
		item.setActivity( mContext , activityInfo );
		return item;
	}
}
