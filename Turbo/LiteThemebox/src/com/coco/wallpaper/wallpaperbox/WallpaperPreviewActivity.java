package com.coco.wallpaper.wallpaperbox;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.ActivityManager;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.service.ThemesDB;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;


public class WallpaperPreviewActivity extends Activity implements AdapterView.OnItemSelectedListener , OnClickListener
{
	
	private String wallpaperPath = "launcher/wallpapers";
	private String customWallpaperPath;
	private String wallpapers_from_other_apk = null;
	private boolean useCustomWallpaper = false;
	private Gallery mGallery;
	private ImageView mImageView;
	private Bitmap mBitmap;
	private ArrayList<String> mThumbs = new ArrayList<String>( 24 );
	private List<WallpaperInformation> localList = new ArrayList<WallpaperInformation>();
	private WallpaperLoader mLoader;
	private PreviewImageTask mPreviewLoader;
	private Context mThemeContext;
	private WallpaperInfo infos;
	ImageAdapter mAdapter;
	ImageHotAdapter mHotAdapter;
	Button setwallpaper;
	int buttonsize = 0;
	private String type = "local";
	private DownModule downModule;
	private ProgressBar progressbar;
	private RelativeLayout relativeNormal;
	private RelativeLayout relativeDownload;
	private BroadcastReceiver packageReceiver;
	private ImageButton delete;
	int position = 0;
	private boolean isPriceVisible = false;
	private String customPath = null;
	private List<Bitmap> localBmp = new ArrayList<Bitmap>();
	
	@Override
	public void onCreate(
			Bundle icicle )
	{
		super.onCreate( icicle );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		ActivityManager.pushActivity( this );
		infos = new WallpaperInfo( this );
		mThemeContext = this;
		Intent it = getIntent();
		if( it != null )
		{
			infos.setDisableSetWallpaperDimensions( it.getBooleanExtra( "disableSetWallpaperDimensions" , false ) );
			customPath = it.getStringExtra( "customWallpaperPath" );
			isPriceVisible = it.getBooleanExtra( "isPriceVisible" , false );
		}
		String launcher = it.getStringExtra( "launchername" );
		if( launcher != null )
		{
			ThemesDB.LAUNCHER_PACKAGENAME = launcher;
		}
		if( isPriceVisible )
		{
			//	CooeeSdk.initCooeeSdk( this );
		}
		setContentView( R.layout.preview_wallpaper );
		progressbar = (ProgressBar)findViewById( R.id.progressBar );
		mGallery = (Gallery)findViewById( R.id.thumbs );
		delete = (ImageButton)findViewById( R.id.btnDel );
		delete.setOnClickListener( this );
		relativeNormal = (RelativeLayout)findViewById( R.id.layoutNormal );
		relativeDownload = (RelativeLayout)findViewById( R.id.layoutDownload );
		relativeDownload.setOnClickListener( this );
		mGallery.setOnItemSelectedListener( this );
		if( it != null && it.getStringExtra( "type" ).equals( "hot" ) )
		{
			downModule = DownModule.getInstance( this );
			type = "hot";
			position = getIntent().getIntExtra( "position" , 0 );
			mHotAdapter = new ImageHotAdapter( this , downModule );
			// mHotAdapter.queryPackage();
			mGallery.setAdapter( mHotAdapter );
			progressbar.setVisibility( View.VISIBLE );
		}
		else
		{
			type = "local";
			buttonsize = getIntent().getIntExtra( "buttonsize" , 0 );
			position = getIntent().getIntExtra( "position" , buttonsize ) - buttonsize;
			wallpapers_from_other_apk = getIntent().getStringExtra( "fromotherapk" );
			mAdapter = new ImageAdapter( this );
			mGallery.setAdapter( mAdapter );
			progressbar.setVisibility( View.GONE );
		}
		mGallery.setCallbackDuringFling( false );
		findViewById( R.id.btnReturn ).setOnClickListener( this );
		setwallpaper = (Button)findViewById( R.id.setwallpaper );
		setwallpaper.setOnClickListener( this );
		findViewById( R.id.btnBuy ).setOnClickListener( this );
		findViewById( R.id.btnDownload ).setOnClickListener( this );
		mImageView = (ImageView)findViewById( R.id.preview );
		mImageView.setScaleType( ScaleType.CENTER_CROP );
		packageReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(
					Context context ,
					Intent intent )
			{
				String actionName = intent.getAction();
				if( actionName.equals( StaticClass.ACTION_THUMB_CHANGED ) )
				{
					if( com.coco.theme.themebox.util.FunctionConfig.isShowHotWallpaper() && type.equals( "hot" ) )
					{
						mHotAdapter.updateThumb( intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) );
					}
				}
				else if( actionName.equals( StaticClass.ACTION_HOTLIST_CHANGED ) )
				{
					if( com.coco.theme.themebox.util.FunctionConfig.isShowHotWallpaper() && type.equals( "hot" ) )
					{
						mHotAdapter.reloadPackage();
					}
				}
				else if( actionName.equals( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED ) )
				{
					if( com.coco.theme.themebox.util.FunctionConfig.isShowHotWallpaper() && type.equals( "hot" ) )
					{
						for( int i = 0 ; i < mGallery.getChildCount() ; i++ )
						{
							WallpaperInformation information = (WallpaperInformation)mGallery.getSelectedItem();
							if( information != null && information.getPackageName().equals( intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) ) )
							{
								WallpaperService service = new WallpaperService( mThemeContext );
								service.queryWallpaper( information.getPackageName() , information );
								break;
							}
						}
						updateShowStatus();
						mHotAdapter.reloadPackage();
					}
					else
					{
						new Thread( new Runnable() {
							
							@Override
							public void run()
							{
								// TODO Auto-generated method stub
								queryDownloadList();
								runOnUiThread( new Runnable() {
									
									@Override
									public void run()
									{
										// TODO Auto-generated method stub
										mAdapter.notifyDataSetChanged();
									}
								} );
							}
						} ).start();
					}
				}
				else if( actionName.equals( StaticClass.ACTION_PREVIEW_CHANGED ) )
				{
					String pkg = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
					if( pkg != null && pkg.equals( ( (WallpaperInformation)mGallery.getSelectedItem() ).getPackageName() ) )
					{
						if( mPreviewLoader != null && mPreviewLoader.getStatus() != PreviewImageTask.Status.FINISHED )
						{
							mPreviewLoader.cancel();
						}
						mPreviewLoader = (PreviewImageTask)new PreviewImageTask().execute( pkg );
					}
				}
				else if( actionName.equals( StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED ) )
				{
					if( com.coco.theme.themebox.util.FunctionConfig.isShowHotWallpaper() && type.equals( "hot" ) )
					{
						for( int i = 0 ; i < mGallery.getCount() ; i++ )
						{
							WallpaperInformation information = (WallpaperInformation)mGallery.getItemAtPosition( i );
							if( information.getPackageName().equals( intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) ) )
							{
								information.setDownloadSize( intent.getIntExtra( StaticClass.EXTRA_DOWNLOAD_SIZE , 0 ) );
								information.setTotalSize( intent.getIntExtra( StaticClass.EXTRA_TOTAL_SIZE , 0 ) );
								if( i == mGallery.getSelectedItemPosition() )
									updateProgressSize();
								break;
							}
						}
						mHotAdapter.reloadPackage();
					}
				}
			}
		};
		if( "local".equals( type ) )
			initInfo();
		IntentFilter screenFilter1 = new IntentFilter();
		screenFilter1.addAction( StaticClass.ACTION_THUMB_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_HOTLIST_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_PREVIEW_CHANGED );
		registerReceiver( packageReceiver , screenFilter1 );
	}
	
	private void initInfo()
	{
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				if( wallpapers_from_other_apk != null )
				{
					try
					{
						Context remountContext = mThemeContext.createPackageContext( wallpapers_from_other_apk , Context.CONTEXT_IGNORE_SECURITY );
						Resources res = remountContext.getResources();
						for( int i = 1 ; ; i++ )
						{
							try
							{
								int drawable = res.getIdentifier( "wallpaper_" + ( i < 10 ? "0" + i : i ) + "_small" , "drawable" , wallpapers_from_other_apk );
								if( drawable == 0 )
								{
									break;
								}
								Bitmap bitmap = Tools.drawableToBitmap( res.getDrawable( drawable ) );
								mThumbs.add( "wallpaper_" + ( i < 10 ? "0" + i : i ) );
								localBmp.add( bitmap );
							}
							catch( IllegalArgumentException e )
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					catch( NameNotFoundException e )
					{
						Log.e( "tabwallpaper" , "createPackageContext exception: " + e );
					}
				}
				else
				{
					infos.findWallpapers( mThumbs , customPath );
					useCustomWallpaper = infos.isUseCustomWallpaper();
					customWallpaperPath = infos.getCustomWallpaperPath();
					for( String str : mThumbs )
					{
						InputStream is = null;
						if( useCustomWallpaper )
						{
							try
							{
								is = new FileInputStream( customWallpaperPath + "/" + str );
							}
							catch( FileNotFoundException e )
							{
								e.printStackTrace();
							}
						}
						else
						{
							try
							{
								Context remoteContext = mThemeContext.createPackageContext( ThemesDB.LAUNCHER_PACKAGENAME , Context.CONTEXT_IGNORE_SECURITY );
								AssetManager asset = remoteContext.getResources().getAssets();
								try
								{
									is = asset.open( wallpaperPath + "/" + str );
								}
								catch( IOException e )
								{
									e.printStackTrace();
								}
							}
							catch( NameNotFoundException e1 )
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						if( is != null )
						{
							Bitmap bitmap = BitmapFactory.decodeStream( is );
							localBmp.add( bitmap );
						}
					}
				}
				queryDownloadList();
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
	
	private void queryDownloadList()
	{
		localList.clear();
		WallpaperService themeSv = new WallpaperService( WallpaperPreviewActivity.this );
		List<WallpaperInformation> installList = themeSv.queryDownloadList();
		for( WallpaperInformation info : installList )
		{
			info.setThumbImage( WallpaperPreviewActivity.this , info.getPackageName() , info.getClassName() );
			localList.add( info );
		}
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
		if( mPreviewLoader != null && mPreviewLoader.getStatus() != PreviewImageTask.Status.FINISHED )
		{
			mPreviewLoader.cancel( true );
			mPreviewLoader = null;
		}
		if( packageReceiver != null )
		{
			unregisterReceiver( packageReceiver );
		}
		if( mAdapter != null )
		{
			mAdapter.onDestory();
		}
		if( mHotAdapter != null )
		{
			mHotAdapter.onDestory();
		}
		//		ActivityManager.popupActivity( this );
		//		ActivityManager.KillActivity();
		//		System.exit( 0 );
	}
	
	public void onItemSelected(
			AdapterView parent ,
			View v ,
			int position ,
			long id )
	{
		System.out.println( "selected position = " + position );
		if( type.equals( "local" ) )
		{
			if( mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED )
			{
				mLoader.cancel();
			}
			relativeNormal.findViewById( R.id.setwallpaper ).setVisibility( View.VISIBLE );
			mLoader = (WallpaperLoader)new WallpaperLoader().execute( position );
		}
		else
		{
			if( mPreviewLoader != null && mPreviewLoader.getStatus() != PreviewImageTask.Status.FINISHED )
			{
				mPreviewLoader.cancel();
			}
			mPreviewLoader = (PreviewImageTask)new PreviewImageTask().execute( ( (WallpaperInformation)mHotAdapter.getItem( position ) ).getPackageName() );
			updateShowStatus();
		}
		if( mGallery.getSelectedItem() instanceof WallpaperInformation )
		{
			if( ( (WallpaperInformation)mGallery.getSelectedItem() ).isDownloaded( this ) && ( (WallpaperInformation)mGallery.getSelectedItem() ).getDownloadStatus() != DownloadStatus.StatusDownloading )
			{
				delete.setVisibility( View.VISIBLE );
				return;
			}
		}
		else
		{
			delete.setVisibility( View.GONE );
		}
	}
	
	private void updateShowStatus()
	{
		WallpaperInformation Information = (WallpaperInformation)mGallery.getSelectedItem();
		if( !( mGallery.getSelectedItem() instanceof WallpaperInformation ) )
		{
			return;
		}
		if( Information.isDownloaded( this ) && Information.getDownloadStatus() != DownloadStatus.StatusDownloading )
		{
			delete.setVisibility( View.VISIBLE );
		}
		else
		{
			delete.setVisibility( View.GONE );
		}
		relativeDownload.setClickable( false );
		if( Information == null )
		{
			return;
		}
		if( !Information.isDownloaded( this ) )
		{
			boolean ispay = Tools.isContentPurchased( this , DownloadList.Wallpaper_Type , Information.getPackageName() );
			if( isPriceVisible && Information.getPrice() > 0 && !ispay )
			{
				relativeDownload.setVisibility( View.GONE );
				relativeNormal.setVisibility( View.VISIBLE );
				relativeNormal.findViewById( R.id.btnDownload ).setVisibility( View.INVISIBLE );
				relativeNormal.findViewById( R.id.setwallpaper ).setVisibility( View.INVISIBLE );
				relativeNormal.findViewById( R.id.btnBuy ).setVisibility( View.VISIBLE );
				return;
			}
			else
			{
				relativeDownload.setVisibility( View.GONE );
				relativeNormal.setVisibility( View.VISIBLE );
				relativeNormal.findViewById( R.id.btnDownload ).setVisibility( View.VISIBLE );
				relativeNormal.findViewById( R.id.setwallpaper ).setVisibility( View.GONE );
				relativeNormal.findViewById( R.id.btnBuy ).setVisibility( View.GONE );
				return;
			}
		}
		if( Information.getDownloadStatus() == DownloadStatus.StatusFinish )
		{
			relativeDownload.setVisibility( View.GONE );
			relativeNormal.setVisibility( View.VISIBLE );
			relativeNormal.findViewById( R.id.btnDownload ).setVisibility( View.GONE );
			relativeNormal.findViewById( R.id.setwallpaper ).setVisibility( View.VISIBLE );
			if( progressbar != null && progressbar.getVisibility() == View.VISIBLE )
			{
				if( mPreviewLoader != null && mPreviewLoader.getStatus() != PreviewImageTask.Status.FINISHED )
				{
					mPreviewLoader.cancel();
				}
				mPreviewLoader = (PreviewImageTask)new PreviewImageTask().execute( Information.getPackageName() );
			}
			return;
		}
		relativeDownload.setClickable( true );
		if( Information.getDownloadStatus() == DownloadStatus.StatusDownloading )
		{
			switchToDownloading();
		}
		else
		{
			switchToPause();
		}
	}
	
	private void switchToDownloading()
	{
		relativeDownload.setVisibility( View.VISIBLE );
		relativeNormal.setVisibility( View.GONE );
		relativeDownload.findViewById( R.id.linearDownload ).setVisibility( View.VISIBLE );
		relativeDownload.findViewById( R.id.linearPause ).setVisibility( View.GONE );
		delete.setVisibility( View.INVISIBLE );
		updateProgressSize();
	}
	
	private void switchToPause()
	{
		relativeDownload.setVisibility( View.VISIBLE );
		relativeNormal.setVisibility( View.GONE );
		relativeDownload.findViewById( R.id.linearDownload ).setVisibility( View.GONE );
		relativeDownload.findViewById( R.id.linearPause ).setVisibility( View.VISIBLE );
		delete.setVisibility( View.VISIBLE );
		updateProgressSize();
	}
	
	private void updateProgressSize()
	{
		WallpaperInformation Information = (WallpaperInformation)mGallery.getSelectedItem();
		if( Information == null )
		{
			return;
		}
		if( findViewById( R.id.linearDownload ).getVisibility() == View.VISIBLE )
		{
			ProgressBar progressBar = (ProgressBar)findViewById( R.id.progressBarDown );
			progressBar.setProgress( Information.getDownloadPercent() );
			TextView text = (TextView)findViewById( R.id.textDownPercent );
			text.setText( getString( R.string.textDownloading , Information.getDownloadPercent() ) );
		}
		else
		{
			ProgressBar progressBar = (ProgressBar)findViewById( R.id.progressBarPause );
			progressBar.setProgress( Information.getDownloadPercent() );
			TextView text = (TextView)findViewById( R.id.textPausePercent );
			text.setText( getString( R.string.textPause , Information.getDownloadPercent() ) );
		}
	}
	
	public class PreviewImageTask extends AsyncTask<String , Integer , Bitmap>
	{
		
		BitmapFactory.Options mOptions;
		
		public PreviewImageTask()
		{
			mOptions = new BitmapFactory.Options();
			mOptions.inDither = false;
		}
		
		@Override
		protected void onPostExecute(
				Bitmap result )
		{
			// TODO Auto-generated method stub
			if( result == null )
			{
				if( progressbar != null )
				{
					// TODO Auto-generated method stub
					progressbar.setVisibility( View.VISIBLE );
				}
				return;
			}
			if( !isCancelled() && !mOptions.mCancel )
			{
				// Help the GC
				if( mBitmap != null )
				{
					mBitmap.recycle();
				}
				final ImageView view = mImageView;
				view.setImageBitmap( result );
				if( progressbar != null )
				{
					// TODO Auto-generated method stub
					progressbar.setVisibility( View.GONE );
				}
				mBitmap = result;
				final Drawable drawable = view.getDrawable();
				drawable.setFilterBitmap( true );
				drawable.setDither( true );
				view.postInvalidate();
				mPreviewLoader = null;
			}
			else
			{
				result.recycle();
			}
		}
		
		@Override
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
			if( progressbar != null && progressbar.getVisibility() != View.VISIBLE )
			{
				progressbar.setVisibility( View.VISIBLE );
			}
		}
		
		@Override
		protected Bitmap doInBackground(
				String ... params )
		{
			// TODO Auto-generated method stub
			// List<LockInformation> result = queryPackage();
			// return result;
			String packageName = params[0];
			String[] strArray = PathTool.getPreviewLists( packageName );
			boolean needDownImage = true;
			Bitmap result = null;
			String imagePath = null;
			if( strArray != null && strArray.length != 0 )
			{
				imagePath = strArray[0];
			}
			else if( PathTool.getAppFile( packageName ) != null )
			{
				imagePath = PathTool.getAppFile( packageName );
			}
			if( imagePath != null )
			{
				FileInputStream is = null;
				FileInputStream newis = null;
				try
				{
					is = new FileInputStream( imagePath );
					newis = new FileInputStream( imagePath );
					needDownImage = false;
				}
				catch( FileNotFoundException e1 )
				{
					// TODO Auto-generated catch block
					needDownImage = true;
				}
				if( is != null )
				{
					BitmapFactory.Options option = new BitmapFactory.Options();
					option.inJustDecodeBounds = true;
					BitmapFactory.decodeStream( is , null , option );
					option.inJustDecodeBounds = false;
					option.inSampleSize = infos.computeSampleSize( option , infos.getScreenWidth() * 2 , infos.getScreenWidth() * 2 * infos.getScreenDisplayMetricsHeight() );
					try
					{
						result = BitmapFactory.decodeStream( newis , null , option );
					}
					catch( Exception e )
					{
						e.printStackTrace();
					}
					if( result == null )
					{
						String path = PathTool.getAppFile( packageName );
						try
						{
							is = new FileInputStream( path );
							newis = new FileInputStream( path );
						}
						catch( FileNotFoundException e1 )
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if( is != null )
						{
							option = new BitmapFactory.Options();
							option.inJustDecodeBounds = true;
							BitmapFactory.decodeStream( is , null , option );
							option.inJustDecodeBounds = false;
							option.inSampleSize = infos.computeSampleSize( option , infos.getScreenWidth() * 2 , infos.getScreenWidth() * 2 * infos.getScreenDisplayMetricsHeight() );
							try
							{
								result = BitmapFactory.decodeStream( newis , null , option );
							}
							catch( Exception e )
							{
								e.printStackTrace();
							}
						}
						if( result == null )
						{
							needDownImage = true;
						}
					}
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
				}
			}
			if( needDownImage )
			{
				if( downModule != null )
					downModule.downloadPreview( packageName , DownloadList.Wallpaper_Type );
			}
			return result;
		}
		
		void cancel()
		{
			mOptions.requestCancelDecode();
			super.cancel( true );
		}
	}
	
	public void onNothingSelected(
			AdapterView parent )
	{
	}
	
	private class ImageHotAdapter extends BaseAdapter
	{
		
		private List<WallpaperInformation> appList = new ArrayList<WallpaperInformation>();
		private Context context;
		private DownModule downThumb;
		private Bitmap imgDefaultThumb;
		private Set<String> firstAdd = new HashSet<String>();
		private PageTask pageTask = null;
		
		public ImageHotAdapter(
				Context cxt ,
				DownModule downModule )
		{
			context = cxt;
			downThumb = downModule;
			imgDefaultThumb = ( (BitmapDrawable)cxt.getResources().getDrawable( R.drawable.default_img_large ) ).getBitmap();
			if( pageTask != null && pageTask.getStatus() != PageTask.Status.FINISHED )
			{
				pageTask.cancel( true );
			}
			pageTask = (PageTask)new PageTask().execute();
		}
		
		public void reloadPackage()
		{
			if( pageTask != null && pageTask.getStatus() != PageTask.Status.FINISHED )
			{
				pageTask.cancel( true );
			}
			pageTask = (PageTask)new PageTask().execute();
		}
		
		public ArrayList<WallpaperInformation> queryPackage(
				Set<String> set )
		{
			ArrayList<WallpaperInformation> list = new ArrayList<WallpaperInformation>();
			WallpaperService service = new WallpaperService( context );
			List<WallpaperInformation> hotList = service.queryShowList();
			if( firstAdd.size() > 0 )
			{
				for( WallpaperInformation item : hotList )
				{
					if( firstAdd.contains( item.getPackageName() ) )
					{
						service.queryWallpaper( item.getPackageName() , item );
						list.add( item );
					}
				}
			}
			else
			{
				for( WallpaperInformation item : hotList )
				{
					if( !set.contains( item.getPackageName() ) )
					{
						service.queryWallpaper( item.getPackageName() , item );
						list.add( item );
						firstAdd.add( item.getPackageName() );
					}
				}
			}
			return list;
		}
		
		private int findPackageIndex(
				String packageName )
		{
			if( packageName == null )
			{
				return -1;
			}
			int i = 0;
			for( i = 0 ; i < appList.size() ; i++ )
			{
				if( packageName.equals( appList.get( i ).getPackageName() ) )
				{
					return i;
				}
			}
			return -1;
		}
		
		public class PageTask extends AsyncTask<String , Integer , List<WallpaperInformation>>
		{
			
			public PageTask()
			{
			}
			
			@Override
			protected void onPostExecute(
					List<WallpaperInformation> result )
			{
				// TODO Auto-generated method stub
				synchronized( appList )
				{
					for( WallpaperInformation info : appList )
					{
						if( info != null )
						{
							info.disposeThumb();
						}
					}
					appList.clear();
					appList.addAll( result );
				}
				notifyDataSetChanged();
				if( position != -1 )
				{
					mGallery.setSelection( position );
					position = -1;
				}
				pageTask = null;
			}
			
			@Override
			protected void onPreExecute()
			{
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
			
			@Override
			protected List<WallpaperInformation> doInBackground(
					String ... params )
			{
				// TODO Auto-generated method stub
				if( firstAdd.size() > 0 )
				{
					return queryPackage( null );
				}
				Set<String> packageNameSet = new HashSet<String>();
				WallpaperService sv = new WallpaperService( WallpaperPreviewActivity.this );
				List<WallpaperInformation> installList = sv.queryDownloadList();
				for( WallpaperInformation info : installList )
				{
					packageNameSet.add( info.getPackageName() );
				}
				return queryPackage( packageNameSet );
			}
		}
		
		public class ImageTask extends AsyncTask<String , Integer , Bitmap>
		{
			
			public ImageTask()
			{
			}
			
			@Override
			protected void onPostExecute(
					Bitmap result )
			{
				// TODO Auto-generated method stub
				notifyDataSetChanged();
			}
			
			@Override
			protected void onPreExecute()
			{
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
			
			@Override
			protected Bitmap doInBackground(
					String ... params )
			{
				// TODO Auto-generated method stub
				// List<LockInformation> result = queryPackage();
				// return result;
				int findIndex = findPackageIndex( params[0] );
				if( findIndex < 0 )
				{
					return null;
				}
				WallpaperInformation info = appList.get( findIndex );
				info.reloadThumb();
				return null;
			}
		}
		
		public void updateThumb(
				String pkgName )
		{
			new ImageTask().execute( pkgName );
			// int findIndex = findPackageIndex(pkgName);
			// if (findIndex < 0) {
			// return;
			// }
			// WallpaperInformation info = appList.get(findIndex);
			// info.reloadThumb();
			// notifyDataSetChanged();
		}
		
		public void onDestory()
		{
			if( pageTask == null )
			{
				for( WallpaperInformation info : appList )
				{
					info.disposeThumb();
				}
				appList.clear();
			}
		}
		
		@Override
		public int getCount()
		{
			return appList.size();
		}
		
		@Override
		public Object getItem(
				int position )
		{
			return appList.get( position );
		}
		
		@Override
		public long getItemId(
				int position )
		{
			return position;
		}
		
		@Override
		public View getView(
				int position ,
				View convertView ,
				ViewGroup parent )
		{
			ImageView image = null;
			if( convertView == null )
			{
				image = (ImageView)LayoutInflater.from( context ).inflate( R.layout.wallpaper_preview_item , parent , false );
			}
			else
			{
				image = (ImageView)convertView;
			}
			WallpaperInformation info = (WallpaperInformation)getItem( position );
			if( info.isNeedLoadDetail() )
			{
				info.loadDetail( context );
				if( info.getThumbImage() == null )
				{
					downThumb.downloadThumb( info.getPackageName() , DownloadList.Wallpaper_Type );
				}
			}
			Bitmap imgThumb = info.getThumbImage();
			if( imgThumb == null )
			{
				imgThumb = imgDefaultThumb;
			}
			image.setImageBitmap( imgThumb );
			return image;
		}
	}
	
	private class ImageAdapter extends BaseAdapter
	{
		
		private LayoutInflater mLayoutInflater;
		
		ImageAdapter(
				WallpaperPreviewActivity context )
		{
			mLayoutInflater = context.getLayoutInflater();
		}
		
		public int getCount()
		{
			return localBmp.size() + localList.size();
		}
		
		public Object getItem(
				int position )
		{
			if( position < mThumbs.size() )
			{
				return position;
			}
			return localList.get( position - mThumbs.size() );
		}
		
		public void onDestory()
		{
			if( localList != null )
			{
				for( WallpaperInformation info : localList )
				{
					info.disposeThumb();
				}
				localList.clear();
				localList = null;
			}
			for( Bitmap bmp : localBmp )
			{
				if( bmp != null && !bmp.isRecycled() )
				{
					bmp.recycle();
					bmp = null;
				}
			}
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
			if( position < mThumbs.size() )
			{
				image.setImageBitmap( localBmp.get( position ) );
			}
			else
			{
				Bitmap bmp = ( (WallpaperInformation)getItem( position ) ).getThumbImage();
				if( bmp != null )
				{
					image.setImageBitmap( bmp );
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
			dialog.setMessage( getString( R.string.changingWallpaper ) );
			dialog.setCancelable( false );
			dialog.show();
			String select = null;
			if( mGallery.getSelectedItem() instanceof WallpaperInformation )
			{
				select = ( (WallpaperInformation)mGallery.getSelectedItem() ).getPackageName();
			}
			else
			{
				select = mThumbs.get( mGallery.getSelectedItemPosition() ).replace( "_small" , "" );
			}
			Intent it = new Intent( "com.coco.wallpaper.update" );
			it.putExtra( "wallpaper" , select );
			sendBroadcast( it );
			new Thread( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					try
					{
						Thread.sleep( 200 );
					}
					catch( InterruptedException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if( mGallery.getSelectedItem() instanceof WallpaperInformation )
					{
						infos.setWallpaperByPath( PathTool.getAppFile( ( (WallpaperInformation)mGallery.getSelectedItem() ).getPackageName() ) );
					}
					else
					{
						if( wallpapers_from_other_apk != null )
						{
							if( mBitmap != null && !mBitmap.isRecycled() )
								infos.selsectWallpaper( mBitmap );
						}
						else
						{
							infos.selectWallpaper( mGallery.getSelectedItemPosition() );
						}
					}
					setwallpaper.post( new Runnable() {
						
						@Override
						public void run()
						{
							dialog.dismiss();
							if( !( mGallery.getSelectedItem() instanceof WallpaperInformation ) )
							{
								sendBroadcast( new Intent( "com.cooee.scene.wallpaper.change" ) );
							}
							Toast.makeText( getApplicationContext() , R.string.toast_setwallpaper_success , Toast.LENGTH_SHORT ).show();
							Intent intent = new Intent();
							intent.setClassName( "com.cooeeui.brand.turbolauncher" , "com.iLoong.launcher.desktop.iLoongLauncher" );
							startActivity( intent );
							WallpaperPreviewActivity.this.finish();
						}
					} );
				}
			} ).start();
		}
		else if( v.getId() == R.id.btnDownload )
		{
			//MobclickAgent.onEvent( mThemeContext , "BoxDownloadWallpaper" );
			if( com.coco.theme.themebox.StaticClass.canDownToInternal )
			{
				File f = new File( PathTool.getAppDir() );
				int num = f.listFiles().length;
				if( num >= 5 )
				{
					recursionDeleteFile( new File( PathTool.getDownloadingDir() ) );
				}
			}
			else if( !com.coco.theme.themebox.StaticClass.isAllowDownloadWithToast( this ) )
			{
				return;
			}
			final WallpaperInformation Information = (WallpaperInformation)mGallery.getSelectedItem();
			if( Information == null )
			{
				return;
			}
			String enginePKG = Information.getEnginepackname();
			if( enginePKG != null && !enginePKG.equals( "" ) && !enginePKG.equals( "null" ) )
			{
				if( !Tools.isAppInstalled( this , enginePKG ) )
				{// 第三方引擎没有安装
					Tools.showNoticeDialog( this , enginePKG , Information.getEnginedesc() , Information.getEngineurl() , Information.getEnginesize() );
					return;
				}
			}
			new Thread( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					DownloadThemeService dSv = new DownloadThemeService( WallpaperPreviewActivity.this );
					DownloadThemeItem dItem = dSv.queryByPackageName( Information.getPackageName() , DownloadList.Wallpaper_Type );
					if( dItem == null )
					{
						dItem = new DownloadThemeItem();
						dItem.copyFromThemeInfo( Information.getInfoItem() );
						dItem.setDownloadStatus( DownloadStatus.StatusDownloading );
						dSv.insertItem( dItem );
					}
					WallpaperService service = new WallpaperService( WallpaperPreviewActivity.this );
					service.queryWallpaper( Information.getPackageName() , Information );
					runOnUiThread( new Runnable() {
						
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.setAction( StaticClass.ACTION_START_DOWNLOAD_APK );
							intent.putExtra( "apkname" , Information.getDisplayName() );
							intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , Information.getPackageName() );
							sendBroadcast( intent );
							updateShowStatus();
						}
					} );
				}
			} ).start();
		}
		else if( v.getId() == R.id.layoutDownload )
		{
			String pkg = ( (WallpaperInformation)mGallery.getSelectedItem() ).getPackageName();
			if( relativeDownload.findViewById( R.id.linearDownload ).getVisibility() == View.VISIBLE )
			{
				Intent intent = new Intent( StaticClass.ACTION_PAUSE_DOWNLOAD_APK );
				intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , pkg );
				intent.putExtra( "apkname" , ( (WallpaperInformation)mGallery.getSelectedItem() ).getDisplayName() );
				sendBroadcast( intent );
				switchToPause();
			}
			else
			{
				Intent intent = new Intent( StaticClass.ACTION_START_DOWNLOAD_APK );
				intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , pkg );
				intent.putExtra( "apkname" , ( (WallpaperInformation)mGallery.getSelectedItem() ).getDisplayName() );
				sendBroadcast( intent );
				switchToDownloading();
			}
		}
		else if( v.getId() == R.id.btnDel )
		{
			if( mGallery.getSelectedItem() instanceof WallpaperInformation )
			{
				AlertDialog.Builder builder = new AlertDialog.Builder( this );
				builder.setMessage( R.string.makesure_to_delete );
				builder.setPositiveButton( R.string.delete_ok , new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(
							DialogInterface dialog ,
							int which )
					{
						// TODO Auto-generated method stub
						String packageName = ( (WallpaperInformation)mGallery.getSelectedItem() ).getPackageName();
						Intent intent = new Intent( StaticClass.ACTION_PAUSE_DOWNLOAD_APK );
						intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , packageName );
						intent.putExtra( "apkname" , ( (WallpaperInformation)mGallery.getSelectedItem() ).getDisplayName() );
						sendBroadcast( intent );
						DownloadThemeService dSv = new DownloadThemeService( WallpaperPreviewActivity.this );
						dSv.deleteItem( packageName , DownloadList.Wallpaper_Type );
						if( "local".equals( type ) )
							mGallery.setSelection( 0 );
					}
				} );
				builder.setNegativeButton( R.string.delete_cancel , new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(
							DialogInterface dialog ,
							int which )
					{
						// TODO Auto-generated method stub
					}
				} );
				builder.create().show();
			}
		}
		else if( v.getId() == R.id.btnBuy )
		{
			//			WallpaperInformation information = (WallpaperInformation)mGallery.getSelectedItem();
			//			CooeePaymentInfo paymentInfo = new CooeePaymentInfo();
			//			paymentInfo.setPrice( information.getPrice() );
			//			paymentInfo.setPayDesc( information.getDisplayName() );
			//			String payid = information.getPricePoint();
			//			Log.v( "themebox" , "payid wallpaper = " + payid );
			//			if( payid == null || payid.equals( "" ) || payid.equals( "null" ) )
			//			{
			//				paymentInfo.setPayId( FunctionConfig.getCooeePayID( information.getPrice() ) );
			//			}
			//			else
			//			{
			//				paymentInfo.setPayId( payid );
			//			}
			//			paymentInfo.setCpId( information.getThirdparty() );
			//			paymentInfo.setPayName( information.getDisplayName() );
			//			paymentInfo.setPayType( CooeePaymentInfo.PAY_TYPE_EVERY_TIME );
			//			paymentInfo.setNotify( new PaymentResultReceiver() );
			//			CooeePayment.getInstance().startPayService( WallpaperPreviewActivity.this , paymentInfo );
		}
	}
	
	private void recursionDeleteFile(
			File file )
	{
		if( file.isFile() )
		{
			file.delete();
			return;
		}
		if( file.isDirectory() )
		{
			File[] childFile = file.listFiles();
			if( childFile == null )
			{
				return;
			}
			for( File f : childFile )
			{
				recursionDeleteFile( f );
			}
			file.delete();
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
			try
			{
				InputStream is = null;
				InputStream newis = null;
				if( params[0] < mThumbs.size() )
				{
					if( wallpapers_from_other_apk != null )
					{
						try
						{
							Context remountContext = mThemeContext.createPackageContext( wallpapers_from_other_apk , Context.CONTEXT_IGNORE_SECURITY );
							Resources res = remountContext.getResources();
							try
							{
								int drawable = res.getIdentifier( mThumbs.get( params[0] ) , "drawable" , wallpapers_from_other_apk );
								return Tools.drawableToBitmap( res.getDrawable( drawable ) );
							}
							catch( IllegalArgumentException e )
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						catch( NameNotFoundException e1 )
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					else
					{
						if( useCustomWallpaper )
						{
							try
							{
								is = new FileInputStream( customWallpaperPath + "/" + mThumbs.get( params[0] ).replace( "_small" , "" ) );
								newis = new FileInputStream( customWallpaperPath + "/" + mThumbs.get( params[0] ).replace( "_small" , "" ) );
							}
							catch( FileNotFoundException e )
							{
								e.printStackTrace();
								return null;
							}
						}
						else
						{
							try
							{
								Context remoteContext = mThemeContext.createPackageContext( ThemesDB.LAUNCHER_PACKAGENAME , Context.CONTEXT_IGNORE_SECURITY );
								AssetManager asset = remoteContext.getResources().getAssets();
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
							catch( NameNotFoundException e1 )
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
								return null;
							}
						}
					}
				}
				else
				{
					String pkg = ( (WallpaperInformation)localList.get( params[0] - mThumbs.size() ) ).getPackageName();
					try
					{
						is = new FileInputStream( PathTool.getAppFile( pkg ) );
						newis = new FileInputStream( PathTool.getAppFile( pkg ) );
					}
					catch( FileNotFoundException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if( is == null )
				{
					return null;
				}
				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inJustDecodeBounds = true;
				BitmapFactory.decodeStream( is , null , option );
				System.out.println( "option.weigth =" + option.outWidth );
				option.inJustDecodeBounds = false;
				option.inSampleSize = infos.computeSampleSize( option , infos.getScreenWidth() * 2 , infos.getScreenWidth() * 2 * infos.getScreenDisplayMetricsHeight() );
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
	//	class PaymentResultReceiver implements CooeePaymentResultNotify
	//	{
	//		
	//		public void paymentResult(
	//				int resultCode ,
	//				CooeePaymentInfo paymentInfo )
	//		{
	//			switch( resultCode )
	//			{
	//				case CooeePaymentResultNotify.COOEE_PAYMENT_RESULT_SUCCESS:
	//					Toast.makeText( WallpaperPreviewActivity.this , "计费成功" , Toast.LENGTH_SHORT ).show();
	//					WallpaperInformation information = (WallpaperInformation)mGallery.getSelectedItem();
	//					Tools.writePurchasedData( WallpaperPreviewActivity.this , DownloadList.Wallpaper_Type , information.getPackageName() );
	//					// Message message = iapHandler.obtainMessage(BILLING_FINISH);
	//					// message.sendToTarget();
	//					Intent intent = new Intent( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED );
	//					intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , information.getPackageName() );
	//					sendBroadcast( intent );
	//					break;
	//				case CooeePaymentResultNotify.COOEE_PAYMENT_RESULT_FAIL:
	//					Toast.makeText( WallpaperPreviewActivity.this , "计费失败" + paymentInfo.getVersionName() , Toast.LENGTH_SHORT ).show();
	//					break;
	//				case CooeePaymentResultNotify.COOEE_PAYMENT_RESULT_CANCEL_BY_USER:
	//					Toast.makeText( WallpaperPreviewActivity.this , "用户取消付费" , Toast.LENGTH_SHORT ).show();
	//					break;
	//				case 1:
	//					Toast.makeText( WallpaperPreviewActivity.this , "配置免费" , Toast.LENGTH_SHORT ).show();
	//					break;
	//				case 2:
	//					Toast.makeText( WallpaperPreviewActivity.this , "不需要重复计费" , Toast.LENGTH_SHORT ).show();
	//					break;
	//				case 3:
	//					Toast.makeText( WallpaperPreviewActivity.this , "无可用指令" , Toast.LENGTH_SHORT ).show();
	//					break;
	//			}
	//		}
	//	}
}
