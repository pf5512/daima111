package com.coco.wallpaper.wallpaperbox;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.coco.theme.themebox.service.ThemesDB;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Tools;


public class WallpaperInfo
{
	
	private Context mContext;
	private final String wallpaperPath = "launcher/wallpapers";
	private String customWallpaperPath;
	private int width = 0;
	boolean useCustomWallpaper = false;
	private List<String> wallpaperLocal = new ArrayList<String>();
	private boolean disable_set_wallpaper_dimensions = false;
	
	public WallpaperInfo(
			Context context )
	{
		mContext = context;
	}
	
	public int getScreenDisplayMetricsHeight()
	{
		Resources res = mContext.getResources();
		return res.getDisplayMetrics().heightPixels;
	}
	
	public int getScreenWidth()
	{
		if( width != 0 )
		{
			return width;
		}
		Resources res = mContext.getResources();
		width = res.getDisplayMetrics().widthPixels;
		return width;
	}
	
	public void selsectWallpaper(
			Bitmap bmp )
	{
		WallpaperManager wpm = (WallpaperManager)mContext.getSystemService( Context.WALLPAPER_SERVICE );
		try
		{
			if( bmp != null )
			{
				Log.v( "wallpaperinfo" , "bmp = " + bmp.getWidth() + " , " + bmp.getHeight() );
				wpm.setBitmap( bmp );
				if( !disable_set_wallpaper_dimensions )
				{
					setWallpaperNewDim( wpm );
				}
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public void selectWallpaper(
			int position )
	{
		InputStream is = null;
		InputStream newDimIs = null;
		if( useCustomWallpaper )
		{
			try
			{
				is = new FileInputStream( customWallpaperPath + "/" + wallpaperLocal.get( position ).replace( "_small" , "" ) );
				newDimIs = new FileInputStream( customWallpaperPath + "/" + wallpaperLocal.get( position ).replace( "_small" , "" ) );
			}
			catch( FileNotFoundException e )
			{
				e.printStackTrace();
			}
		}
		else
		{
			Context remoteContext = null;
			try
			{
				remoteContext = mContext.createPackageContext( ThemesDB.LAUNCHER_PACKAGENAME , Context.CONTEXT_IGNORE_SECURITY );
			}
			catch( NameNotFoundException e1 )
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if( remoteContext == null )
			{
				return;
			}
			AssetManager asset = remoteContext.getResources().getAssets();
			try
			{
				is = asset.open( wallpaperPath + "/" + wallpaperLocal.get( position ).replace( "_small" , "" ) );
				newDimIs = asset.open( wallpaperPath + "/" + wallpaperLocal.get( position ).replace( "_small" , "" ) );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		WallpaperManager wpm = (WallpaperManager)mContext.getSystemService( Context.WALLPAPER_SERVICE );
		try
		{
			int size[] = new int[2];
			Bitmap scaleBmp = getBitmapFromFile( newDimIs , size );
			if( scaleBmp != null )
			{
				Log.v( "wallpaperinfo" , "scaleBmp = " + scaleBmp.getWidth() + " , " + scaleBmp.getHeight() );
				wpm.setBitmap( scaleBmp );
			}
			else
			{
				wpm.setStream( ( is ) );
			}
			if( !disable_set_wallpaper_dimensions )
			{
				setWallpaperNewDim( size[0] , size[1] , wpm );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public void setWallpaperByPath(
			String path )
	{// zjp
		WallpaperManager wpm = (WallpaperManager)mContext.getSystemService( Context.WALLPAPER_SERVICE );
		InputStream is = null;
		InputStream newDimIs = null;
		try      
		{
			is = new FileInputStream( path );
			newDimIs = new FileInputStream( path );
			int size[] = new int[2];
			Bitmap scaleBmp = getBitmapFromFile( newDimIs , size );
			if( scaleBmp != null )
			{
				Log.v( "wallpaperinfo" , "setWallpaperByPath scaleBmp = " + scaleBmp.getWidth() + " , " + scaleBmp.getHeight() );
				wpm.setBitmap( scaleBmp );
			}
			else
			{
				wpm.setStream( ( is ) );
			}
			if( !disable_set_wallpaper_dimensions )
				setWallpaperNewDim( size[0] , size[1] , wpm );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	private void setWallpaperNewDim(
			WallpaperManager wpm )
	{
		DisplayMetrics displayMetrics = new DisplayMetrics();
		( (Activity)mContext ).getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		wpm.suggestDesiredDimensions( (int)( displayMetrics.widthPixels * 2 ) , (int)( displayMetrics.heightPixels ) );
	}
	
	public void setWallpaperNewDim(
			int width ,
			int height ,
			WallpaperManager wpm )
	{
		Log.v( "jbc" , "fuckwallpaper WallpaperChooser setWallpaperNewDim()" );
		DisplayMetrics displayMetrics = new DisplayMetrics();
		( (Activity)mContext ).getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		final int maxDim = Math.max( displayMetrics.widthPixels , displayMetrics.heightPixels );
		final int minDim = Math.min( displayMetrics.widthPixels , displayMetrics.heightPixels );
		int mWallpaperWidth = width;
		int mWallpaperHeight = height;
		Log.v( "jbc" , "fuckwallpaper WallpaperChooser options.w=" + mWallpaperWidth + " options.h=" + mWallpaperHeight );
		float scale = 1;
		if( mWallpaperWidth < minDim )
		{
			scale = (float)minDim / (float)mWallpaperWidth;
		}
		if( mWallpaperHeight * scale < maxDim )
		{
			scale = (float)maxDim / (float)mWallpaperHeight;
		}
		Log.v( "jbc" , "fuckwallpaper WallpaperChooser getDesired w=" + wpm.getDesiredMinimumWidth() + " h=" + wpm.getDesiredMinimumHeight() );
		int style = 5;
		int mininumWidth = -1;
		int mininumHeight = -1;
		switch( style )
		{
			case 0://效果同3。一些手机设置-1会重启，禁用
				mininumWidth = -1;
				mininumHeight = -1;
				break;
			case 1://单屏宽高
				mininumWidth = minDim;
				mininumHeight = maxDim;
				break;
			case 2://双屏宽高
				mininumWidth = minDim * 2;
				mininumHeight = maxDim;
				break;
			case 3://壁纸宽高
				mininumWidth = mWallpaperWidth;
				mininumHeight = mWallpaperHeight;
				break;
			case 4://老方案
				mininumWidth = (int)( mWallpaperWidth * scale );
				mininumHeight = (int)( mWallpaperHeight * scale );
				break;
			case 5://add by jbc 131221，相比较老方案的优点在于，对于高大于屏高的 壁纸，显示壁纸中间的而非上面的屏高部分
				mininumWidth = (int)( mWallpaperWidth * scale );
				mininumHeight = maxDim;
				break;
		}
		/*suggestDesiredDimensions系统处理过程说明 add by jbc
		1、若壁纸宽小于mininumWidth或壁纸高小于mininumHeight，
		则将壁纸按原始比例拉伸至宽和高不小于mininumWidth和mininumHeight
		2、截取壁纸中央的宽为mininumWidth高为mininumHeight的部分作为WallpaperManager的drawable
		3、将该drawable显示在屏幕上，宽即mininumWidth，高即mininumHeight；
		若宽大于屏宽则可滑动，高大于屏高则显示上面屏高部分；
		若宽或高小于屏宽或屏高，则依据手机的不同显示在屏幕中央或左上角
		*无论设置成什么值，都不用担心壁纸会不按照原始比例进行缩放，因为系统会自动处理
		*小壁纸会按原始比例拉伸至宽和高不小于mininumWidth和mininumHeight，而大壁纸则取中间部分，不会缩小
		*若想铺满屏幕（不想有黑边的话），设置的值应至少不小于单屏宽高
		*/
		Log.v( "jbc" , "fuckwallpaper WallpaperChooser suggestDesired w=" + mininumWidth + " h=" + mininumHeight );
		wpm.suggestDesiredDimensions( mininumWidth , mininumHeight );
		Log.v( "jbc" , "fuckwallpaper WallpaperChooser suggestDesired done!" );
	}
	
	public void findWallpapers(
			List<String> thumbs )
	{
		findWallpapers( thumbs , FunctionConfig.getCustomWallpaperPath() );
	}
	
	public void findWallpapers(
			List<String> thumbs ,
			String custompath )
	{
		if( custompath == null )
		{
			custompath = "";
		}
		customWallpaperPath = custompath;
		File dir = new File( customWallpaperPath );
		if( dir.exists() && dir.isDirectory() )
		{
			useCustomWallpaper = true;
		}
		Context remoteContext = null;
		try
		{
			remoteContext = mContext.createPackageContext( ThemesDB.LAUNCHER_PACKAGENAME , Context.CONTEXT_IGNORE_SECURITY );
		}
		catch( NameNotFoundException e1 )
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if( remoteContext == null && !useCustomWallpaper )
		{
			return;
		}
		String[] wallpapers = null;
		final Resources resources = remoteContext.getResources();
		AssetManager assManager = resources.getAssets();
		try
		{
			if( useCustomWallpaper )
			{
				wallpapers = dir.list();
			}
			else
				wallpapers = assManager.list( wallpaperPath );
			Tools.getThumblist( wallpapers , wallpaperLocal );
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collections.sort( wallpaperLocal , new ByStringValue() );
		if( thumbs == null )
		{
			thumbs = new ArrayList<String>();
		}
		thumbs.clear();
		thumbs.addAll( wallpaperLocal );
	}
	
	public boolean isUseCustomWallpaper()
	{
		return useCustomWallpaper;
	}
	
	public String getCustomWallpaperPath()
	{
		return customWallpaperPath;
	}
	
	public Bitmap getBitmapFromFile(
			InputStream newDimIs ,
			int[] size )
	{
		if( newDimIs == null )
		{
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream( newDimIs , null , options );
		DisplayMetrics displayMetrics = new DisplayMetrics();
		( (Activity)mContext ).getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		/* 如果墙纸的大小不等于当前屏幕的两倍宽高，表示需要缩放 */
		if( size != null && size.length == 2 )
		{
			size[0] = options.outWidth;
			size[1] = options.outHeight;
		}
		Log.v( "wallpaperinfo" , "option.outWidth = " + options.outWidth + " option.outHeight = " + options.outHeight + " displayMetrics.widthPixels = " + displayMetrics.widthPixels );
		if( options.outWidth != displayMetrics.widthPixels * 2 )
		{
			// 计算图片缩放比例
			final int minSideLength = 2 * displayMetrics.widthPixels;
			options.inSampleSize = computeSampleSize( options , minSideLength , minSideLength * displayMetrics.heightPixels );
			options.inJustDecodeBounds = false;
			options.inInputShareable = true;
			options.inPurgeable = true;
			Log.v( "wallpaperinfo" , " options.inSampleSize = " + options.inSampleSize );
			try
			{
				return BitmapFactory.decodeStream( newDimIs , null , options );
			}
			catch( OutOfMemoryError e )
			{
				return null;
			}
		}
		return null;
	}
	
	private int computeInitialSampleSize(
			BitmapFactory.Options options ,
			int minSideLength ,
			int maxNumOfPixels )
	{
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = ( maxNumOfPixels == -1 ) ? 1 : (int)Math.ceil( Math.sqrt( w * h / maxNumOfPixels ) );
		int upperBound = ( minSideLength == -1 ) ? 128 : (int)Math.min( Math.floor( w / minSideLength ) , Math.floor( h / minSideLength ) );
		if( upperBound < lowerBound )
		{
			return lowerBound;
		}
		if( ( maxNumOfPixels == -1 ) && ( minSideLength == -1 ) )
		{
			return 1;
		}
		else if( minSideLength == -1 )
		{
			return lowerBound;
		}
		else
		{
			return upperBound;
		}
	}
	
	public int computeSampleSize(
			BitmapFactory.Options options ,
			int minSideLength ,
			int maxNumOfPixels )
	{
		int initialSize = computeInitialSampleSize( options , minSideLength , maxNumOfPixels );
		int roundedSize;
		if( initialSize <= 8 )
		{
			roundedSize = 1;
			while( roundedSize < initialSize )
			{
				roundedSize <<= 1;
			}
		}
		else
		{
			roundedSize = ( initialSize + 7 ) / 8 * 8;
		}
		return roundedSize;
	}
	
	public void setDisableSetWallpaperDimensions(
			boolean disableSetWallpaperDimensions )
	{
		this.disable_set_wallpaper_dimensions = disableSetWallpaperDimensions;
	}
	
	class ByStringValue implements Comparator<String>
	{
		
		@Override
		public int compare(
				String lhs ,
				String rhs )
		{
			// TODO Auto-generated method stub
			if( lhs.compareTo( rhs ) > 0 )
			{
				return 1;
			}
			else if( lhs.compareTo( rhs ) < 0 )
			{
				return -1;
			}
			return 0;
		}
	}
}
