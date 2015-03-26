package com.coco.scene.scenebox;


import java.io.File;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.ThemeInformation;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.util.ContentConfig;
import com.coco.theme.themebox.util.Tools;


public class SceneInformation extends ThemeInformation
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
			dSv.updateDownloadSizeAndStatus( getPackageName() , 0 , DownloadStatus.StatusInit , DownloadList.Scene_Type );
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
			File f = mContext.getDir( "scene" , Context.MODE_PRIVATE );
			String thumbPath = f + "/" + pkgName + "/" + actName + ".tupian";
			thumbImage = Tools.getPurgeableBitmap( thumbPath , -1 , -1 );
		}
		catch( OutOfMemoryError e )
		{
			e.printStackTrace();
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
			reloadThumb( cxt );
		}
	}
	
	public Bitmap getThumbImage(
			Context remoteContext ,
			ContentConfig cfg )
	{
		int imagesize = cfg.getPreviewArrayLength();
		if( imagesize > 0 )
		{
			Bitmap bmp = cfg.loadPreviewImage( remoteContext , 0 );
			if( bmp != null )
			{
				float w = bmp.getWidth() / 1.0f / Tools.dip2px( remoteContext , 120 );
				float h = bmp.getHeight() / 1.0f / Tools.dip2px( remoteContext , 180 );
				float scale = w < h ? w : h;
				Bitmap tmp = Bitmap.createScaledBitmap( bmp , (int)( bmp.getWidth() / scale ) , (int)( bmp.getHeight() / scale ) , true );
				if( tmp != bmp )
				{
					bmp.recycle();
					bmp = null;
				}
				return tmp;
			}
			return bmp;
		}
		return cfg.loadThumbImage( remoteContext );
	}
	
	public void reloadThumb(
			Context cxt )
	{
		// Log.d(LOG_TAG, "reloadThumb,pkg="+lockInfo.getPackageName());
		String thumbPath = PathTool.getThumbFile( themeInfo.getPackageName() );
		if( new File( thumbPath ).exists() )
		{
			try
			{
				thumbImage = Tools.getPurgeableBitmap( thumbPath , Tools.dip2px( cxt , 120 ) , Tools.dip2px( cxt , 180 ) );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}
	
	protected void checkThemePrefix()
	{
	}
}
