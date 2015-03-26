package com.coco.wallpaper.wallpaperbox;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco.theme.themebox.ActivityManager;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;


public class LockWallpaperPreview extends Activity implements AdapterView.OnItemSelectedListener , OnClickListener
{
	
	private String wallpaperPath = "launcher/lockwallpapers";
	private String customLockWallpaperPath;
	private boolean useCustomLockWallpaper = false;
	private Gallery mGallery;
	private ImageView mImageView;
	private Bitmap mBitmap;
	private ArrayList<String> mThumbs = new ArrayList<String>( 24 );
	private WallpaperLoader mLoader;
	private Context mThemeContext;
	ImageAdapter mAdapter;
	Button setwallpaper;
	private RelativeLayout relativeNormal;
	private ImageButton delete;
	int position = 0;
	
	@Override
	public void onCreate(
			Bundle icicle )
	{
		super.onCreate( icicle );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		ActivityManager.pushActivity( this );
		mThemeContext = this;
		setContentView( R.layout.preview_wallpaper );
		findViewById( R.id.progressBar ).setVisibility( View.GONE );
		mGallery = (Gallery)findViewById( R.id.thumbs );
		delete = (ImageButton)findViewById( R.id.btnDel );
		delete.setVisibility( View.GONE );
		mGallery.setOnItemSelectedListener( this );
		mAdapter = new ImageAdapter( this );
		mGallery.setAdapter( mAdapter );
		mGallery.setCallbackDuringFling( false );
		TextView title = (TextView)findViewById( R.id.wallpaper );
		title.setText( R.string.lock_wallpaper );
		findViewById( R.id.btnReturn ).setOnClickListener( this );
		setwallpaper = (Button)findViewById( R.id.setwallpaper );
		setwallpaper.setText( R.string.set_lock_wallpaper );
		setwallpaper.setVisibility( View.VISIBLE );
		setwallpaper.setOnClickListener( this );
		findViewById( R.id.btnBuy ).setOnClickListener( this );
		findViewById( R.id.btnDownload ).setOnClickListener( this );
		mImageView = (ImageView)findViewById( R.id.preview );
		mImageView.setScaleType( ScaleType.CENTER_CROP );
		initInfo();
	}
	
	private void initInfo()
	{
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				findLockWallpapers();
				runOnUiThread( new Runnable() {
					
					public void run()
					{
						if( mAdapter != null )
						{
							mAdapter.notifyDataSetChanged();
						}
						mGallery.setSelection( position );
					}
				} );
			}
		} ).start();
	}
	
	private void findLockWallpapers()
	{
		customLockWallpaperPath = FunctionConfig.getCustomLockWallpaperPath();
		String[] wallpapers = null;
		if( customLockWallpaperPath != null )
		{
			File dir = new File( customLockWallpaperPath );
			if( dir.exists() && dir.isDirectory() )
			{
				useCustomLockWallpaper = true;
				wallpapers = dir.list();
			}
		}
		else
		{
			AssetManager assManager = getResources().getAssets();
			try
			{
				wallpapers = assManager.list( wallpaperPath );
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Tools.getThumblist( wallpapers , mThumbs );
		Collections.sort( mThumbs , new ByStringValue() );
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
		ActivityManager.popupActivity( this );
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
	
	public void onNothingSelected(
			AdapterView parent )
	{
	}
	
	private class ImageAdapter extends BaseAdapter
	{
		
		private LayoutInflater mLayoutInflater;
		
		ImageAdapter(
				LockWallpaperPreview context )
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
			ImageView image = null;
			if( convertView == null )
			{
				image = (ImageView)mLayoutInflater.inflate( R.layout.wallpaper_preview_item , parent , false );
			}
			else
			{
				image = (ImageView)convertView;
			}
			// int thumbRes = mThumbs.get(position);
			// image.setImageResource(thumbRes);
			InputStream is = null;
			if( position < mThumbs.size() )
			{
				if( useCustomLockWallpaper )
				{
					try
					{
						is = new FileInputStream( customLockWallpaperPath + "/" + mThumbs.get( position ) );
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
			}
			if( is != null )
			{
				image.setImageBitmap( BitmapFactory.decodeStream( is ) );
				try
				{
					is.close();
				}
				catch( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Drawable thumbDrawable = image.getDrawable();
			if( thumbDrawable != null )
			{
				thumbDrawable.setDither( true );
			}
			return image;
		}
	}
	
	public void onClick(
			View v )
	{
		if( v.getId() == R.id.btnReturn )
		{
			finish();
		}
		else if( v.getId() == R.id.setwallpaper )
		{
			final ProgressDialog dialog = new ProgressDialog( this );
			dialog.setMessage( getString( R.string.changeLockWallpaper ) );
			dialog.setCancelable( false );
			dialog.show();
			new Thread( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					while( mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED )
					{
						try
						{
							Thread.sleep( 20 );
						}
						catch( InterruptedException e )
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if( mLoader == null || mLoader.getStatus() == WallpaperLoader.Status.FINISHED )
					{
						setLockWallpaper();
						dialog.dismiss();
					}
				}
			} ).start();
		}
	}
	
	private void setLockWallpaper()//展讯平台方法
	{
		WallpaperManager mWallpaperManager = WallpaperManager.getInstance( this );
		Class<?> WallpaperManager = null;
		try
		{
			WallpaperManager = Class.forName( "android.app.WallpaperManager" );
		}
		catch( ClassNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if( WallpaperManager != null )
		{
			Method setBitmap = null;
			try
			{
				setBitmap = WallpaperManager.getMethod( "setBitmap" , Bitmap.class , int.class );
			}
			catch( SecurityException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( NoSuchMethodException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if( setBitmap != null )
			{
				try
				{
					setBitmap.invoke( mWallpaperManager , mBitmap , 1 );
				}
				catch( IllegalArgumentException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch( IllegalAccessException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch( InvocationTargetException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	class WallpaperLoader extends AsyncTask<Integer , Void , Bitmap>
	{
		
		BitmapFactory.Options mOptions;
		
		WallpaperLoader()
		{
			mOptions = new BitmapFactory.Options();
			mOptions.inDither = false;
		}
		
		protected Bitmap doInBackground(
				Integer ... params )
		{
			if( isCancelled() )
				return null;
			try
			{
				InputStream is = null;
				InputStream newis = null;
				if( params[0] < mThumbs.size() )
				{
					if( useCustomLockWallpaper )
					{
						try
						{
							is = new FileInputStream( customLockWallpaperPath + "/" + mThumbs.get( params[0] ).replace( "_small" , "" ) );
							newis = new FileInputStream( customLockWallpaperPath + "/" + mThumbs.get( params[0] ).replace( "_small" , "" ) );
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
							is = asset.open( wallpaperPath + "/" + mThumbs.get( params[0] ).replace( "_small" , "" ) );
							newis = asset.open( wallpaperPath + "/" + mThumbs.get( params[0] ).replace( "_small" , "" ) );
						}
						catch( IOException e )
						{
							e.printStackTrace();
							return null;
						}
					}
				}
				if( is == null )
				{
					return null;
				}
				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inJustDecodeBounds = true;
				BitmapFactory.decodeStream( is , null , option );
				option.inJustDecodeBounds = false;
				int h = option.outHeight / getResources().getDisplayMetrics().heightPixels;
				int w = option.outWidth / getResources().getDisplayMetrics().widthPixels;
				option.inSampleSize = w < h ? w : h;
				Bitmap temp = null;
				try
				{
					temp = BitmapFactory.decodeStream( newis , null , option );
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
				if( temp != null )
				{
					try
					{
						if( is != null )
						{
							is.close();
						}
						if( newis != null )
						{
							newis.close();
						}
					}
					catch( IOException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return temp;
				}
			}
			catch( OutOfMemoryError e )
			{
				e.printStackTrace();
				return null;
			}
			return null;
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
