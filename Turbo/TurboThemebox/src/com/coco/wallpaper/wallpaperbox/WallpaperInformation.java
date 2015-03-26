package com.coco.wallpaper.wallpaperbox;


import java.io.File;

import android.content.Context;
import android.content.pm.ServiceInfo;
import android.util.Log;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.ThemeInformation;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.database.model.ThemeInfoItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.service.ThemesDB;
import com.coco.theme.themebox.util.Tools;


public class WallpaperInformation extends ThemeInformation
{
	
	public void copy(
			WallpaperInformation item )
	{
		this.className = item.className;
		this.installed = item.installed;
		this.displayName = item.displayName;
		this.themeInfo.copyFrom( item.themeInfo );
		this.downloadSize = item.downloadSize;
		this.downloadStatus = item.downloadStatus;
		this.thumbImage = item.thumbImage;
		this.needLoadDetail = item.needLoadDetail;
		this.mSystem = item.mSystem;
		this.downloaded = item.downloaded;
	}
	
	public boolean isDownloadedFinish()
	{
		File file = new File( PathTool.getAppFile( getPackageName() ) );
		File small = new File( PathTool.getAppSmallFile( getPackageName() ) );
		if( ( file.exists() && small.exists() ) && ( getDownloadStatus() == DownloadStatus.StatusFinish ) )
		{
			return true;
		}
		return false;
	}
	
	public boolean isDownloaded(
			Context context )
	{
		File file = new File( PathTool.getAppFile( getPackageName() ) );
		File small = new File( PathTool.getAppSmallFile( getPackageName() ) );
		if( ( !file.exists() || !small.exists() ) && ( getDownloadStatus() == DownloadStatus.StatusInit || getDownloadStatus() == DownloadStatus.StatusFinish ) )
		{
			File f1 = new File( PathTool.getDownloadingDir() + getPackageName() + "_app.tmp" );
			f1.delete();
			DownloadThemeService dSv = new DownloadThemeService( context );
			dSv.updateDownloadSizeAndStatus( getPackageName() , 0 , DownloadStatus.StatusInit , DownloadList.Wallpaper_Type );
			return false;
		}
		return downloaded;
	}
	
	public boolean isLiveDownloaded(
			Context context )
	{
		File file = new File( PathTool.getAppLiveFile( getPackageName() ) );
		File small = new File( PathTool.getAppSmallFile( getPackageName() ) );
		if( ( !file.exists() || !small.exists() ) && ( getDownloadStatus() == DownloadStatus.StatusInit || getDownloadStatus() == DownloadStatus.StatusFinish ) )
		{
			File f1 = new File( PathTool.getDownloadingDir() + getPackageName() + "_app.tmp" );
			f1.delete();
			DownloadThemeService dSv = new DownloadThemeService( context );
			dSv.updateDownloadSizeAndStatus( getPackageName() , 0 , DownloadStatus.StatusInit , DownloadList.Wallpaper_Type );
			return false;
		}
		return downloaded;
	}
	
	public void setThumbImage(
			Context mContext ,
			String pkgName ,
			String actName )
	{
		String thumbPath = PathTool.getAppSmallFile( pkgName );
		try
		{
			thumbImage = Tools.getPurgeableBitmap( thumbPath , -1 , -1 );
		}
		catch( OutOfMemoryError e )
		{
			disposeThumb();
			thumbImage = null;
			e.printStackTrace();
		}
	}
	
	public void loadDetail(
			Context cxt )
	{
		needLoadDetail = false;
		disposeThumb();
		thumbImage = null;
		String thumbPath = PathTool.getThumbFile( themeInfo.getPackageName() );
		if( new File( thumbPath ).exists() )
		{
			try
			{
				thumbImage = Tools.getPurgeableBitmap( thumbPath , -1 , -1 );//BitmapFactory.decodeFile( thumbPath );
			}
			catch( OutOfMemoryError error )
			{
				Log.v( "thumbImage" , error.toString() );
			}
		}
	}
	
	public void reloadThumb()
	{
		String thumbPath = PathTool.getThumbFile( themeInfo.getPackageName() );
		if( new File( thumbPath ).exists() )
		{
			try
			{
				thumbImage = Tools.getPurgeableBitmap( thumbPath , -1 , -1 );//BitmapFactory.decodeFile( thumbPath );
			}
			catch( OutOfMemoryError error )
			{
				error.printStackTrace();
			}
		}
	}
	
	public void setThemeItem(
			ThemeInfoItem item )
	{
		className = "";
		installed = false;
		if( thumbImage != null && !thumbImage.isRecycled() )
		{
			needLoadDetail = false;
		}
		else
		{
			needLoadDetail = true;
		}
		downloadStatus = DownloadStatus.StatusInit;
		themeInfo.copyFrom( item );
		downloadSize = 0;
		displayName = themeInfo.getApplicationName();
		downloaded = false;
		mSystem = false;
	}
	
	public void setService(
			Context cxt ,
			ServiceInfo service )
	{
		className = service.name;
		installed = true;
		disposeThumb();
		thumbImage = null;
		needLoadDetail = true;
		downloadStatus = DownloadStatus.StatusFinish;
		themeInfo = new ThemeInfoItem();
		themeInfo.setPackageName( service.packageName );
		downloadSize = 0;
		displayName = service.loadLabel( cxt.getPackageManager() ).toString();
		mSystem = service.packageName.equals( ThemesDB.LAUNCHER_PACKAGENAME );
	}
}
