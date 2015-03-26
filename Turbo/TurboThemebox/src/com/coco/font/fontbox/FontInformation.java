package com.coco.font.fontbox;


import java.io.File;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.ThemeInformation;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.database.model.ThemeInfoItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.service.ThemesDB;
import com.coco.theme.themebox.util.Tools;


public class FontInformation extends ThemeInformation
{
	
	// private final String LOG_TAG = "LockInformation";
	public boolean isDownloadFinish()
	{
		File file = new File( PathTool.getAppFile( getPackageName() ) );
		if( file.exists() && ( getDownloadStatus() == DownloadStatus.StatusFinish ) )
		{
			return true;
		}
		return false;
	}
	
	public boolean isDownloaded(
			Context context )
	{
		File file = new File( PathTool.getAppFile( getPackageName() ) );
		if( !file.exists() && ( getDownloadStatus() == DownloadStatus.StatusInit || getDownloadStatus() == DownloadStatus.StatusFinish ) )
		{
			File f1 = new File( PathTool.getDownloadingDir() + getPackageName() + "_app.tmp" );
			f1.delete();
			DownloadThemeService dSv = new DownloadThemeService( context );
			dSv.updateDownloadSizeAndStatus( getPackageName() , 0 , DownloadStatus.StatusInit , DownloadList.Font_Type );
			return false;
		}
		return downloaded;
	}
	
	public void setActivity(
			Context cxt ,
			ApplicationInfo app )
	{
		className = app.name;
		installed = true;
		disposeThumb();
		thumbImage = null;
		needLoadDetail = true;
		downloadStatus = DownloadStatus.StatusFinish;
		themeInfo = new ThemeInfoItem();
		themeInfo.setPackageName( app.packageName );
		downloadSize = 0;
		displayName = app.loadLabel( cxt.getPackageManager() ).toString();
		mSystem = app.packageName.equals( ThemesDB.LAUNCHER_PACKAGENAME );
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
		// Log.d(LOG_TAG, "reloadThumb,pkg="+lockInfo.getPackageName());
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
	
	public void setThumbImage(
			Context mContext ,
			String pkgName ,
			String actName )
	{
		try
		{
			File f = mContext.getDir( "font" , Context.MODE_PRIVATE );
			String thumbPath = f + "/" + pkgName + "/" + actName + ".tupian";
			thumbImage = Tools.getPurgeableBitmap( thumbPath , -1 , -1 );
		}
		catch( OutOfMemoryError e )
		{
			e.printStackTrace();
		}
	}
	
	protected void checkThemePrefix()
	{
	}
}
