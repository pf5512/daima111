package com.coco.lock2.lockbox;


import java.io.File;
import java.util.Locale;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.coco.download.DownloadList;
import com.coco.lock2.lockbox.util.ContentConfig;
import com.coco.lock2.lockbox.util.PathTool;
import com.coco.theme.themebox.ThemeInformation;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.database.model.ThemeInfoItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Tools;


public class LockInformation extends ThemeInformation
{
	
	private String wrapName = "";
	private String settingClassName = "";
	
	public String getWrapName()
	{
		return wrapName;
	}
	
	public boolean isDownloaded(
			Context context )
	{
		File file = new File( PathTool.getAppDir() + "/" + getPackageName() + ".apk" );
		if( !file.exists() && ( getDownloadStatus() == DownloadStatus.StatusInit || getDownloadStatus() == DownloadStatus.StatusFinish ) )
		{
			File f1 = new File( PathTool.getDownloadingDir() + getPackageName() + "_app.tmp" );
			f1.delete();
			DownloadThemeService dSv = new DownloadThemeService( context );
			dSv.updateDownloadSizeAndStatus( getPackageName() , 0 , DownloadStatus.StatusInit , DownloadList.Lock_Type );
			return false;
		}
		return downloaded;
	}
	
	public String getSettingClassName()
	{
		return settingClassName;
	}
	
	public boolean isSettingExist()
	{
		if( settingClassName.equals( "" ) )
		{
			return false;
		}
		return true;
	}
	
	public void setThumbImage(
			Context mContext ,
			String pkgName ,
			String actName )
	{
		try
		{
			File f = mContext.getDir( "coco" , Context.MODE_PRIVATE );
			String thumbPath = f + "/" + pkgName + "/" + actName + ".tupian";
			thumbImage = Tools.getPurgeableBitmap( thumbPath , -1 , -1 );
		}
		catch( OutOfMemoryError e )
		{
			e.printStackTrace();
		}
	}
	
	public void setActivity(
			Context cxt ,
			ActivityInfo activity )
	{
		className = activity.name;
		installed = true;
		disposeThumb();
		thumbImage = null;
		needLoadDetail = true;
		downloadStatus = DownloadStatus.StatusFinish;
		themeInfo = new ThemeInfoItem();
		themeInfo.setPackageName( activity.packageName );
		downloadSize = 0;
		displayName = activity.loadLabel( cxt.getPackageManager() ).toString();
		checkLockPrefix();
		settingClassName = "";
		mSystem = cxt.getPackageName().equals( activity.packageName );
		downloaded = true;
	}
	
	public void setThemeItem(
			ThemeInfoItem item )
	{
		className = "";
		installed = false;
		disposeThumb();
		thumbImage = null;
		needLoadDetail = true;
		downloadStatus = DownloadStatus.StatusInit;
		themeInfo.copyFrom( item );
		downloadSize = 0;
		String systemLauncher = Locale.getDefault().getLanguage().toString();
		if( systemLauncher.equals( "zh" ) )
		{
			displayName = themeInfo.getApplicationName();
		}
		else
		{
			displayName = themeInfo.getApplicationName_en();
		}
		checkLockPrefix();
		settingClassName = "";
		downloaded = false;
		mSystem = false;
	}
	
	private void checkLockPrefix()
	{
		String namePrefix[] = { "CoCo锁屏" , "CoCo鎖屏" , "CoCo Locker " };
		for( String name : namePrefix )
		{
			if( displayName != null && displayName.startsWith( name ) )
			{
				displayName = displayName.substring( name.length() );
				break;
			}
		}
	}
	
	public void loadDetail(
			Context cxt )
	{
		needLoadDetail = false;
		disposeThumb();
		thumbImage = null;
		if( installed )
		{
			ContentConfig cfg = new ContentConfig();
			try
			{
				Context remoteContext = cxt.createPackageContext( themeInfo.getPackageName() , Context.CONTEXT_IGNORE_SECURITY );
				cfg.loadConfig( remoteContext , className );
				loadInstallDetail( remoteContext , cfg );
			}
			catch( NameNotFoundException e )
			{
				e.printStackTrace();
			}
		}
		else
		{
			String thumbPath = PathTool.getThumbFile( themeInfo.getPackageName() );
			if( new File( thumbPath ).exists() )
			{
				try
				{
					thumbImage = Tools.getPurgeableBitmap( thumbPath , Tools.dip2px( cxt , FunctionConfig.getGridWidth() ) , Tools.dip2px( cxt , FunctionConfig.getGridHeight() ) );//BitmapFactory.decodeFile( thumbPath );
				}
				catch( OutOfMemoryError error )
				{
					Log.v( "thumbImage" , error.toString() );
				}
			}
		}
	}
	
	public void loadInstallDetail(
			Context remoteContext ,
			ContentConfig cfg )
	{
		needLoadDetail = false;
		disposeThumb();
		thumbImage = null;
		installed = true;
		thumbImage = cfg.loadThumbImage( remoteContext );
		themeInfo.setApplicationName( cfg.getApplicationName() );
		themeInfo.setVersionCode( cfg.getVersionCode() );
		themeInfo.setVersionName( cfg.getVersionName() );
		themeInfo.setApplicationSize( cfg.getApplicationSize() );
		downloadSize = cfg.getApplicationSize();
		themeInfo.setAuthor( cfg.getAuthor() );
		themeInfo.setIntroduction( cfg.getIntroduction() );
		themeInfo.setUpdateTime( cfg.getUpdateTime() );
		if( !mSystem )
		{
			mSystem = cfg.getReflection();
		}
		themeInfo.setApplicationName_en( cfg.getApplicationName() );
		themeInfo.setIntroduction_en( cfg.getIntroduction_en() );
		settingClassName = cfg.getSettingClassName();
		wrapName = cfg.getWrapName();
	}
	
	public void reloadThumb()
	{
		// Log.d(LOG_TAG, "reloadThumb,pkg="+lockInfo.getPackageName());
		String thumbPath = PathTool.getThumbFile( themeInfo.getPackageName() );
		if( new File( thumbPath ).exists() )
		{
			try
			{
				thumbImage = Tools.getPurgeableBitmap( thumbPath , -1 , -1 );
			}
			catch( OutOfMemoryError error )
			{
				error.printStackTrace();
			}
		}
	}
}
