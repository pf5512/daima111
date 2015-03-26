package com.iLoong.launcher.DesktopEdit;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coco.theme.themebox.util.Tools;
import com.cooee.android.launcher.framework.IconCache;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.theme.ThemeManager;


public class ThemeAndPagerHelper
{
	
	public static Paint paint = new Paint();
	public static Canvas canvas = new Canvas();
	public static float scale = Utils3D.getScreenWidth() / 720f;
	
	public static Bitmap BitmapChange(
			Bitmap b ,
			int textureWidth ,
			int textureHeight ,
			boolean recycle ,
			Bitmap oriMask )
	{
		Bitmap bmp = Bitmap.createBitmap( textureWidth , textureHeight , Config.ARGB_8888 );
		canvas.setBitmap( bmp );
		paint.reset();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		if( b != null && !b.isRecycled() )
		{
			canvas.drawBitmap( b , 0 , 0 , null );
			if( recycle && b != IconCache.mDefaultIcon )
				b.recycle();
		}
		//		Bitmap oriMask = ThemeManager.getInstance().getBitmap(
		//				"theme/desktopEdit/popupmask.png");
		Bitmap mask = null;
		if( oriMask != null )
		{
			mask = Tools.resizeBitmap( oriMask , textureWidth , textureHeight );
			if( oriMask != mask )
				oriMask.recycle();
		}
		if( mask != null )
		{
			paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.MULTIPLY ) );
			canvas.drawBitmap( mask , 0 , 0 , paint );
			paint.setXfermode( null );
		}
		return bmp;
	}
	
	public static TextureRegion getRegion(
			String name )
	{
		Bitmap middleImgView;
		Bitmap tmp = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/" + name );
		if( tmp.getWidth() == DefaultLayout.app_icon_size && tmp.getHeight() == DefaultLayout.app_icon_size )
		{
			middleImgView = tmp;
		}
		else
		{
			middleImgView = Bitmap.createScaledBitmap( tmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
			tmp.recycle();
		}
		TextureRegion tr = new TextureRegion( new BitmapTexture( middleImgView ) );
		return tr;
	}
	
	public static Bitmap getBitmap(
			String name )
	{
		Bitmap middleImgView;
		Bitmap tmp = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/" + name );
		if( tmp.getWidth() == DefaultLayout.app_icon_size && tmp.getHeight() == DefaultLayout.app_icon_size )
		{
			middleImgView = tmp;
		}
		else
		{
			middleImgView = Bitmap.createScaledBitmap( tmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
			tmp.recycle();
		}
		if( middleImgView != null )
		{
			return middleImgView;
		}
		return null;
	}
	
	public static ArrayList<String> mThumbs;
	public static ArrayList<String> mImages;
	public static Context mThemeContext;
	public static String wallpaperPath = "launcher/wallpapers";
	public static String customWallpaperPath;
	public static boolean useCustomWallpaper = false;
	
	public static void findWallpapers()
	{
		String tempWallPaperPath = "";
		tempWallPaperPath = ThemeManager.getInstance().getCurrentThemeFileDir( wallpaperPath , true );
		if( tempWallPaperPath == null )
		{
			tempWallPaperPath = ThemeManager.getInstance().getSystemThemeFileDir( wallpaperPath , true );
			mThemeContext = ThemeManager.getInstance().getSystemContext();
		}
		else
		{
			mThemeContext = ThemeManager.getInstance().getCurrentThemeContext();
		}
		wallpaperPath = tempWallPaperPath;
		mThumbs = new ArrayList<String>( 24 );
		mImages = new ArrayList<String>( 24 );
		//		ArrayList<String> mTemp = new ArrayList<String>(24);
		//		ArrayList<String> mFound = new ArrayList<String>(24);
		//		final Resources resources = mThemeContext.getResources();
		customWallpaperPath = DefaultLayout.custom_wallpapers_path;
		File dir = new File( customWallpaperPath );
		if( dir.exists() && dir.isDirectory() )
		{
			useCustomWallpaper = true;
		}
		//		AssetManager assManager = resources.getAssets();
		//		String[] wallpapers = null;
		//		try {
		//			if (useCustomWallpaper) {
		//				wallpapers = dir.list();
		//			} else
		//				wallpapers = assManager.list(wallpaperPath);
		//			for (String name : wallpapers) {
		//				Log.v("wallpaper", name);
		//				if (!name.contains("_small")) {
		//					mImages.add(name);
		//				} else {
		//					mTemp.add(name);
		//				}
		//			}
		//			for (String name : mImages) {
		//				for (String nameTmp : mTemp) {
		//					if (name.equals(nameTmp.replace("_small", ""))) {
		//						mThumbs.add(nameTmp);
		//						mFound.add(name);
		//						break;
		//					}
		//				}
		//			}
		//			mImages.clear();
		//			mImages.addAll(mFound);
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}
	}
	
	public static InputStream GetBitByName(
			String name )
	{
		InputStream is = null;
		if( useCustomWallpaper )
		{
			try
			{
				is = new FileInputStream( customWallpaperPath + "/" + name );
			}
			catch( FileNotFoundException e )
			{
				e.printStackTrace();
			}
		}
		else
		{
			AssetManager asset = mThemeContext.getResources().getAssets();
			try
			{
				is = asset.open( wallpaperPath + "/" + name );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		//Bitmap bit = BitmapFactory.decodeStream( is );
//		try
//		{
//			if( is != null )
//			{
//				is.close();
//			}
//		}
//		catch( IOException e )
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if( is != null )
		{
			return is;
		}
		return null;
	}
	
	public static Bitmap GetBitByName(
			String name ,
			String path )
	{
		InputStream is = null;
		if( useCustomWallpaper )
		{
			try
			{
				is = new FileInputStream( path + "/" + name );
			}
			catch( FileNotFoundException e )
			{
				e.printStackTrace();
			}
		}
		else
		{
			AssetManager asset = mThemeContext.getResources().getAssets();
			try
			{
				is = asset.open( path + "/" + name );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		Bitmap bit = BitmapFactory.decodeStream( is );
		try
		{
			if( is != null )
			{
				is.close();
			}
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if( bit != null )
		{
			return bit;
		}
		return null;
	}
}
