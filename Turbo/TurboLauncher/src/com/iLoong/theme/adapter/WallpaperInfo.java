package com.iLoong.theme.adapter;


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
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import com.iLoong.launcher.Desktop3D.Log;


public class WallpaperInfo
{
	
	private Context mContext;
	private final String wallpaperPath = "launcher/wallpapers";
	private String customWallpaperPath;
	private int width = 0;
	boolean useCustomWallpaper = false;
	private List<String> mThumbs = new ArrayList<String>( 24 );
	private List<String> mImages = new ArrayList<String>( 24 );
	
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
	
	public void selectWallpaper(
			int position )
	{
		InputStream is = null;
		InputStream newDimIs = null;
		if( useCustomWallpaper )
		{
			try
			{
				is = new FileInputStream( customWallpaperPath + "/" + mImages.get( position ) );
				newDimIs = new FileInputStream( customWallpaperPath + "/" + mImages.get( position ) );
			}
			catch( FileNotFoundException e )
			{
				e.printStackTrace();
			}
		}
		else
		{
			AssetManager asset = mContext.getResources().getAssets();
			try
			{
				is = asset.open( wallpaperPath + "/" + mImages.get( position ) );
				newDimIs = asset.open( wallpaperPath + "/" + mImages.get( position ) );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		WallpaperManager wpm = (WallpaperManager)mContext.getSystemService( Context.WALLPAPER_SERVICE );
		try
		{
			Bitmap scaleBmp = getBitmapFromFile( newDimIs );
			if( scaleBmp != null )
			{
				wpm.setBitmap( scaleBmp );
			}
			else
			{
				wpm.setStream( ( is ) );
			}
			setWallpaperNewDim( wpm );
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
			InputStream newDimIs ,
			WallpaperManager wpm )
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream( newDimIs , null , options );
		DisplayMetrics displayMetrics = new DisplayMetrics();
		( (Activity)mContext ).getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		final int maxDim = Math.max( displayMetrics.widthPixels , displayMetrics.heightPixels );
		final int minDim = Math.min( displayMetrics.widthPixels , displayMetrics.heightPixels );
		int mWallpaperWidth = options.outWidth;
		int mWallpaperHeight = options.outHeight;
		float scale = 1;
		if( mWallpaperWidth < minDim )
		{
			scale = (float)minDim / (float)mWallpaperWidth;
		}
		if( mWallpaperHeight * scale < maxDim )
		{
			scale = (float)maxDim / (float)mWallpaperHeight;
		}
		wpm.suggestDesiredDimensions( (int)( mWallpaperWidth * scale ) , (int)( mWallpaperHeight * scale ) );
	}
	
	public void findWallpapers(
			List<String> images ,
			List<String> thumbs ,
			String custompath )
	{
		ArrayList<String> mTemp = new ArrayList<String>( 24 );
		ArrayList<String> mFound = new ArrayList<String>( 24 );
		final Resources resources = mContext.getResources();
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
		AssetManager assManager = resources.getAssets();
		String[] wallpapers = null;
		try
		{
			if( useCustomWallpaper )
			{
				wallpapers = dir.list();
			}
			else
				wallpapers = assManager.list( wallpaperPath );
			for( String name : wallpapers )
			{
				Log.v( "wallpaper" , name );
				if( !name.contains( "_small" ) )
				{
					mImages.add( name );
				}
				else
				{
					mTemp.add( name );
				}
			}
			for( String name : mImages )
			{
				for( String nameTmp : mTemp )
				{
					if( name.equals( nameTmp.replace( "_small" , "" ) ) )
					{
						mThumbs.add( nameTmp );
						mFound.add( name );
						break;
					}
				}
			}
			mImages.clear();
			mImages.addAll( mFound );
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collections.sort( mImages , new ByStringValue() );
		Collections.sort( mThumbs , new ByStringValue() );
		if( images != null )
		{
			for( String temp : mImages )
			{
				images.add( temp );
			}
		}
		if( thumbs != null )
		{
			for( String temp : mThumbs )
			{
				thumbs.add( temp );
			}
		}
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
			InputStream newDimIs )
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
		/*如果墙纸的大小不等于当前屏幕的两倍宽高，表示需要缩放*/
		if( options.outWidth != displayMetrics.widthPixels * 2 )
		{
			// 计算图片缩放比例
			final int minSideLength = 2 * displayMetrics.widthPixels;
			options.inSampleSize = computeSampleSize( options , minSideLength , minSideLength * displayMetrics.heightPixels );
			options.inJustDecodeBounds = false;
			options.inInputShareable = true;
			options.inPurgeable = true;
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
