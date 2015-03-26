/* Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.iLoong.launcher.desktop;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.pub.provider.PubProviderHelper;
import com.iLoong.launcher.theme.ThemeManager;


public class WallpaperChooser extends Activity implements AdapterView.OnItemSelectedListener , OnClickListener
{
	
	private static final String TAG = "Launcher.WallpaperChooser";
	private String wallpaperPath = "launcher/wallpapers";
	private static String customWallpaperPath;
	private boolean useCustomWallpaper = false;
	private static String customAssetsWallpaperPath;
	private boolean useCustomAssetsWallpaper = false;
	private Gallery mGallery;
	private ImageView mImageView;
	private boolean mIsWallpaperSet;
	private Bitmap mBitmap;
	private ArrayList<String> mThumbs;
	private ArrayList<String> mImages;
	private WallpaperLoader mLoader;
	private Context mThemeContext;
	private BroadcastReceiver changeReceiver = null;
	
	@Override
	public void onCreate(
			Bundle icicle )
	{
		super.onCreate( icicle );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
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
		findWallpapers();
		setContentView( RR.layout.wallpaper_chooser );
		mGallery = (Gallery)findViewById( RR.id.gallery );
		mGallery.setAdapter( new ImageAdapter( this ) );
		mGallery.setOnItemSelectedListener( this );
		mGallery.setCallbackDuringFling( false );
		findViewById( RR.id.set ).setOnClickListener( this );
		mImageView = (ImageView)findViewById( RR.id.wallpaper );
		changeReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(
					Context context ,
					Intent intent )
			{
				// TODO Auto-generated method stub
				Toast.makeText( WallpaperChooser.this , getString( RR.string.setwallpaper_success ) , Toast.LENGTH_SHORT ).show();
				finish();
			}
		};
		IntentFilter pkgFilter = new IntentFilter();
		pkgFilter.addAction( Intent.ACTION_WALLPAPER_CHANGED );
		registerReceiver( changeReceiver , pkgFilter );
	}
	
	private void findWallpapers()
	{
		mThumbs = new ArrayList<String>( 24 );
		mImages = new ArrayList<String>( 24 );
		ArrayList<String> mTemp = new ArrayList<String>( 24 );
		ArrayList<String> mFound = new ArrayList<String>( 24 );
		final Resources resources = mThemeContext.getResources();
		// Context.getPackageName() may return the "original" package name,
		// com.coesns.launcher2; Resources needs the real package name,
		// com.coesns.launcher2. So we ask Resources for what it thinks the
		// package name should be.
		customWallpaperPath = DefaultLayout.custom_wallpapers_path;
		File dir = new File( customWallpaperPath );
		if( dir.exists() && dir.isDirectory() )
		{
			useCustomWallpaper = true;
		}
		else if( DefaultLayout.getInstance().isCustomAssetsFileExist( "/launcher/wallpapers" ) )
		{
			customAssetsWallpaperPath = DefaultLayout.custom_assets_path + "/launcher/wallpapers";
			dir = new File( customAssetsWallpaperPath );
			if( dir.exists() && dir.isDirectory() )
			{
				useCustomAssetsWallpaper = true;
			}
		}
		AssetManager assManager = resources.getAssets();
		String[] wallpapers = null;
		try
		{
			if( useCustomWallpaper || useCustomAssetsWallpaper )
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
			e.printStackTrace();
		}
		// final String packageName =
		// resources.getResourcePackageName(R.array.wallpapers);
		//
		// addWallpapers(resources, packageName, R.array.wallpapers);
		// addWallpapers(resources, packageName, R.array.extra_wallpapers);
	}
	
	// private void addWallpapers(Resources resources, String packageName, int
	// list) {
	// final String[] extras = resources.getStringArray(list);
	// for (String extra : extras) {
	// int res = resources.getIdentifier(extra, "drawable", packageName);
	// if (res != 0) {
	// final int thumbRes = resources.getIdentifier(extra + "_small",
	// "drawable", packageName);
	//
	// if (thumbRes != 0) {
	// mThumbs.add(thumbRes);
	// mImages.add(res);
	// // Log.d(TAG, "addWallpapers: [" + packageName + "]: " + extra + " (" +
	// res + ")");
	// }
	// }
	// }
	// }
	@Override
	protected void onResume()
	{
		super.onResume();
		mIsWallpaperSet = false;
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if( mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED )
		{
			mLoader.cancel( true );
			mLoader = null;
		}
		if( changeReceiver != null )
		{
			unregisterReceiver( changeReceiver );
		}
	}
	
	public void onItemSelected(
			AdapterView parent ,
			View v ,
			int position ,
			long id )
	{
		if( mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED )
		{
			mLoader.cancel();
		}
		mLoader = (WallpaperLoader)new WallpaperLoader().execute( position );
	}
	
	/*
	 * When using touch if you tap an image it triggers both the onItemClick and
	 * the onTouchEvent causing the wallpaper to be set twice. Ensure we only
	 * set the wallpaper once.
	 */
	private void selectWallpaper(
			int position )
	{
		if( mIsWallpaperSet )
		{
			return;
		}
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
		else if( useCustomAssetsWallpaper )
		{
			try
			{
				is = new FileInputStream( customAssetsWallpaperPath + "/" + mImages.get( position ) );
				newDimIs = new FileInputStream( customAssetsWallpaperPath + "/" + mImages.get( position ) );
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
				is = asset.open( wallpaperPath + "/" + mImages.get( position ) );
				newDimIs = asset.open( wallpaperPath + "/" + mImages.get( position ) );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		mIsWallpaperSet = true;
		try
		{
			SharedPreferences prefs = iLoongLauncher.getInstance().getSharedPreferences( "launcher" , Activity.MODE_PRIVATE );
			WallpaperManager wpm = (WallpaperManager)getSystemService( WALLPAPER_SERVICE );
			int[] mWallpaperSize = new int[2];
			getWallpaperSize( newDimIs , mWallpaperSize );
			//			setWallpaperNewDim(wpm,mWallpaperSize[0],mWallpaperSize[1]);
			if( !DefaultLayout.disable_set_wallpaper_dimensions )
			{
				setWallpaperNewDim( wpm );
			}
			if( mWallpaperSize[1] == Utils3D.getScreenDisplayMetricsHeight() )
			{
				wpm.setStream( is );
			}
			else
			{
				Bitmap mWallpaper = Tools.getImageFromInStream( is );
				is.close();
				float minWidth = (float)Math.ceil( wpm.getDesiredMinimumWidth() );
				float minHeight = wpm.getDesiredMinimumHeight();
				float scale = 1f;
				if( mWallpaper.getHeight() <= Utils3D.getScreenDisplayMetricsHeight() )
				{
					wpm.setBitmap( mWallpaper );
					scale = (float)Utils3D.getScreenDisplayMetricsHeight() / mWallpaper.getHeight();
					minWidth = (float)Math.ceil( mWallpaper.getWidth() * scale );
				}
				else
				{
					scale = (float)Utils3D.getScreenDisplayMetricsHeight() / mWallpaper.getHeight();
					Bitmap newWallpaper = Tools.resizeBitmap( mWallpaper , scale );
					wpm.setBitmap( newWallpaper );
					minWidth = (float)Math.ceil( newWallpaper.getWidth() );
				}
				if( minWidth < Utils3D.getScreenWidth() )
				{
					minWidth = Utils3D.getScreenWidth();
				}
			}
			//wpm.suggestDesiredDimensions((int) minWidth, (int) minHeight);
			prefs.edit().putLong( "apply_wallpaper_time" , System.currentTimeMillis() ).commit();
			setResult( RESULT_OK );
			finish();
		}
		catch( IOException e )
		{
			Log.e( TAG , "Failed to set wallpaper: " + e );
		}
		finally
		{
			try
			{
				if( is != null )
				{
					is.close();
				}
				if( newDimIs != null )
				{
					newDimIs.close();
				}
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		pref.edit().putString( "currentWallpaper" , mImages.get( position ) ).commit();
		pref.edit().putBoolean( "cooeechange" , true ).commit();
		PubProviderHelper.addOrUpdateValue( "wallpaper" , "currentWallpaper" , mImages.get( position ) );
		PubProviderHelper.addOrUpdateValue( "wallpaper" , "cooeechange" , "true" );
	}
	
	private void setWallpaperNewDim(
			WallpaperManager wpm )
	{
		DisplayMetrics displayMetrics = new DisplayMetrics();
		iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		wpm.suggestDesiredDimensions( (int)( displayMetrics.widthPixels * 2 ) , (int)( displayMetrics.heightPixels ) );
	}
	
	//teapotXu add start
	private void getWallpaperSize(
			InputStream fileIs ,
			int[] wallpaperSize )
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream( fileIs , null , options );
		wallpaperSize[0] = options.outWidth;
		wallpaperSize[1] = options.outHeight;
	}
	
	//teapotXu add end
	private void setWallpaperNewDim(
			WallpaperManager wpm ,
			int wallpaperWidth ,
			int wallpaperHeight )
	{
		//			BitmapFactory.Options options = new BitmapFactory.Options();
		//
		//			options.inJustDecodeBounds = true;
		//			BitmapFactory.decodeStream(newDimIs, null, options);
		//			int mWallpaperWidth = options.outWidth;
		//			int mWallpaperHeight = options.outHeight;
		DisplayMetrics displayMetrics = new DisplayMetrics();
		iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		final int maxDim = Math.max( displayMetrics.widthPixels , displayMetrics.heightPixels );
		final int minDim = Math.min( displayMetrics.widthPixels , displayMetrics.heightPixels );
		float scale = 1;
		if( wallpaperWidth < minDim )
		{
			scale = (float)minDim / (float)wallpaperWidth;
		}
		if( wallpaperHeight * scale < maxDim )
		{
			scale = (float)maxDim / (float)wallpaperHeight;
		}
		wpm.suggestDesiredDimensions( (int)( wallpaperWidth * scale ) , (int)( wallpaperHeight * scale ) );
		Log.v( "test" , "wallpaper chooser wpm.widht=" + wallpaperWidth + " mWallpaperHeight=" + wallpaperHeight );
	}
	
	public void onNothingSelected(
			AdapterView parent )
	{
	}
	
	private class ImageAdapter extends BaseAdapter
	{
		
		private LayoutInflater mLayoutInflater;
		
		ImageAdapter(
				WallpaperChooser context )
		{
			mLayoutInflater = context.getLayoutInflater();
		}
		
		public int getCount()
		{
			return mThumbs.size();
		}
		
		public Object getItem(
				int position )
		{
			return position;
		}
		
		public long getItemId(
				int position )
		{
			return position;
		}
		
		public View getView(
				int position ,
				View convertView ,
				ViewGroup parent )
		{
			ImageView image;
			if( convertView == null )
			{
				image = (ImageView)mLayoutInflater.inflate( RR.layout.wallpaper_item , parent , false );
			}
			else
			{
				image = (ImageView)convertView;
			}
			InputStream is = null;
			if( useCustomWallpaper )
			{
				try
				{
					is = new FileInputStream( customWallpaperPath + "/" + mThumbs.get( position ) );
				}
				catch( FileNotFoundException e )
				{
					e.printStackTrace();
				}
			}
			else if( useCustomAssetsWallpaper )
			{
				try
				{
					is = new FileInputStream( customAssetsWallpaperPath + "/" + mThumbs.get( position ) );
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
					is = asset.open( wallpaperPath + "/" + mThumbs.get( position ) );
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
			image.setImageBitmap( BitmapFactory.decodeStream( is ) );
			Drawable thumbDrawable = image.getDrawable();
			if( thumbDrawable != null )
			{
				thumbDrawable.setDither( true );
			}
			else
			{
				Log.e( TAG , "Error decoding thumbnail resId=" + mThumbs.get( position ) + " for wallpaper #" + position );
			}
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
			return image;
		}
	}
	
	public void onClick(
			View v )
	{
		if( DefaultLayout.enable_scene_wallpaper )
		{
			Intent it = new Intent( WallpaperChangedReceiver.SCENE_WALLPAPER_CHANGE );
			it.putExtra( "wallpaper" , mImages.get( mGallery.getSelectedItemPosition() ) );
			sendBroadcast( it );
		}
		else
		{
			new Thread( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					selectWallpaper( mGallery.getSelectedItemPosition() );
					//						runOnUiThread(new Runnable() {
					//						
					//							@Override
					//							public void run() {
					//								// TODO Auto-generated method stub
					//								dialog.dismiss();
					//							}
					//						});
				}
			} ).start();
		}
	}
	
	class WallpaperLoader extends AsyncTask<Integer , Void , Bitmap>
	{
		
		BitmapFactory.Options mOptions;
		
		WallpaperLoader()
		{
			mOptions = new BitmapFactory.Options();
			mOptions.inDither = false;
			mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
		}
		
		protected Bitmap doInBackground(
				Integer ... params )
		{
			if( isCancelled() )
				return null;
			InputStream is = null;
			try
			{
				if( useCustomWallpaper )
				{
					try
					{
						is = new FileInputStream( customWallpaperPath + "/" + mImages.get( params[0] ) );
					}
					catch( FileNotFoundException e )
					{
						e.printStackTrace();
						return null;
					}
				}
				else if( useCustomAssetsWallpaper )
				{
					try
					{
						is = new FileInputStream( customAssetsWallpaperPath + "/" + mImages.get( params[0] ) );
					}
					catch( FileNotFoundException e )
					{
						e.printStackTrace();
						return null;
					}
				}
				else
				{
					AssetManager asset = mThemeContext.getResources().getAssets();
					try
					{
						is = asset.open( wallpaperPath + "/" + mImages.get( params[0] ) );
					}
					catch( IOException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
				}
				return BitmapFactory.decodeStream( is );
			}
			catch( OutOfMemoryError e )
			{
				return null;
			}
			finally
			{
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
			}
		}
		
		@Override
		protected void onPostExecute(
				Bitmap b )
		{
			if( b == null )
				return;
			if( !isCancelled() && !mOptions.mCancel )
			{
				// Help the GC
				if( mBitmap != null )
				{
					mBitmap.recycle();
				}
				final ImageView view = mImageView;
				view.setImageBitmap( b );
				mBitmap = b;
				final Drawable drawable = view.getDrawable();
				drawable.setFilterBitmap( true );
				drawable.setDither( true );
				view.postInvalidate();
				mLoader = null;
			}
			else
			{
				b.recycle();
			}
		}
		
		void cancel()
		{
			mOptions.requestCancelDecode();
			super.cancel( true );
		}
	}
}
