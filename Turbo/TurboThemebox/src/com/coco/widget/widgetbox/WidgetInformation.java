package com.coco.widget.widgetbox;


import java.io.File;
import java.io.InputStream;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.ThemeInformation;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.database.model.ThemeInfoItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.service.ThemesDB;
import com.coco.theme.themebox.util.ContentConfig;
import com.coco.theme.themebox.util.Tools;


public class WidgetInformation extends ThemeInformation
{
	
	public boolean isDownloaded(
			Context context )
	{
		File file = new File( PathTool.getAppDir() + "/" + getPackageName() + ".apk" );
		if( !file.exists() && ( getDownloadStatus() == DownloadStatus.StatusInit || getDownloadStatus() == DownloadStatus.StatusFinish ) )
		{
			File f1 = new File( PathTool.getDownloadingDir() + getPackageName() + "_app.tmp" );
			f1.delete();
			DownloadThemeService dSv = new DownloadThemeService( context );
			dSv.updateDownloadSizeAndStatus( getPackageName() , 0 , DownloadStatus.StatusInit , DownloadList.Widget_Type );
			return false;
		}
		return downloaded;
	}
	
	public void setThumbImage(
			Context mContext ,
			String pkgName ,
			String actName )
	{
		try
		{
			File f = mContext.getDir( "widget" , Context.MODE_PRIVATE );
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
		checkThemePrefix();
		mSystem = activity.packageName.equals( ThemesDB.LAUNCHER_PACKAGENAME );
	}
	
	public void setActivity(
			Context cxt ,
			AppWidgetProviderInfo provider )
	{
		className = provider.provider.getClassName();
		installed = true;
		disposeThumb();
		thumbImage = null;
		needLoadDetail = true;
		downloadStatus = DownloadStatus.StatusFinish;
		themeInfo = new ThemeInfoItem();
		themeInfo.setPackageName( provider.provider.getPackageName() );
		downloadSize = 0;
		displayName = provider.label;
		mSystem = provider.provider.getPackageName().equals( ThemesDB.LAUNCHER_PACKAGENAME );
	}
	
	public void loadDetail(
			Context cxt )
	{
		needLoadDetail = false;
		disposeThumb();
		thumbImage = null;
		if( installed )
		{
			InputStream stream = null;
			try
			{
				Context remoteContext = cxt.createPackageContext( themeInfo.getPackageName() , Context.CONTEXT_IGNORE_SECURITY );
				String iconPath = null;
				try
				{
					if( stream == null )
					{
						try
						{
							DisplayMetrics displayMetrics = cxt.getResources().getDisplayMetrics();
							iconPath = "iLoong/image/" + displayMetrics.widthPixels + "x" + displayMetrics.heightPixels + "/widget_ico.png";
							stream = remoteContext.getAssets().open( iconPath );
						}
						catch( Exception e )
						{
							e.printStackTrace();
						}
						if( stream == null )
						{
							iconPath = "iLoong/image/widget_ico.png";
							stream = remoteContext.getAssets().open( iconPath );
						}
						try
						{
							thumbImage = Tools.getPurgeableBitmap( stream , -1 , -1 );//BitmapFactory.decodeStream( stream );
						}
						catch( OutOfMemoryError error )
						{
							Log.v( "thumbImage" , error.toString() );
						}
					}
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}
			catch( Exception e )
			{
			}
			finally
			{
				if( thumbImage == null )
				{
					try
					{
						PackageManager pm = cxt.getPackageManager();
						PackageInfo pkgInfo = pm.getPackageInfo( themeInfo.getPackageName() , 0 );
						if( pkgInfo != null )
						{
							ApplicationInfo appInfo = pkgInfo.applicationInfo;
							Drawable icon2 = appInfo.loadIcon( pm );
							thumbImage = Tools.drawableToBitmap( icon2 );
						}
					}
					catch( NameNotFoundException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch( OutOfMemoryError error )
					{
						error.printStackTrace();
					}
				}
				try
				{
					if( stream != null )
					{
						stream.close();
					}
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			String thumbPath = PathTool.getThumbFile( themeInfo.getPackageName() );
			if( new File( thumbPath ).exists() )
			{
				try
				{
					thumbImage = Tools.getPurgeableBitmap( thumbPath , -1 , -1 );// BitmapFactory.decodeFile( thumbPath );
				}
				catch( OutOfMemoryError error )
				{
					Log.v( "thumbImage" , error.toString() );
				}
			}
		}
	}
	
	public Bitmap getThumbImage(
			Context remoteContext ,
			ContentConfig cfg )
	{
		return cfg.loadThumbImage( remoteContext );
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
	
	protected void checkThemePrefix()
	{
	}
}
