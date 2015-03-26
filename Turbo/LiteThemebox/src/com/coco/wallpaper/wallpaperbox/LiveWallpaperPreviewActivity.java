package com.coco.wallpaper.wallpaperbox;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.coco.theme.themebox.database.model.ThemeInfoItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.database.service.HotService;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;


public class LiveWallpaperPreviewActivity extends Activity implements AdapterView.OnItemSelectedListener , OnClickListener
{
	
	private Gallery mGallery;
	private ImageView mImageView;
	private Bitmap mBitmap;
	private PreviewImageTask mPreviewLoader;
	private Context mThemeContext;
	private WallpaperInfo infos;
	ImageHotAdapter mHotAdapter;
	Button setwallpaper;
	private DownModule downModule;
	private ProgressBar progressbar;
	private RelativeLayout relativeNormal;
	private RelativeLayout relativeDownload;
	private BroadcastReceiver packageReceiver;
	private ImageButton delete;
	int position = 0;
	private boolean isPriceVisible = false;
	
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
		isPriceVisible = it.getBooleanExtra( "isPriceVisible" , false );
		if( isPriceVisible )
		{
			//CooeeSdk.initCooeeSdk( this );
		}
		setContentView( R.layout.preview_wallpaper );
		( (TextView)findViewById( R.id.wallpaper ) ).setText( R.string.wallpaper_live );
		progressbar = (ProgressBar)findViewById( R.id.progressBar );
		mGallery = (Gallery)findViewById( R.id.thumbs );
		delete = (ImageButton)findViewById( R.id.btnDel );
		delete.setOnClickListener( this );
		relativeNormal = (RelativeLayout)findViewById( R.id.layoutNormal );
		relativeDownload = (RelativeLayout)findViewById( R.id.layoutDownload );
		relativeDownload.setOnClickListener( this );
		mGallery.setOnItemSelectedListener( this );
		downModule = DownModule.getInstance( this );
		position = getIntent().getIntExtra( "position" , 0 );
		mHotAdapter = new ImageHotAdapter( this , downModule );
		// mHotAdapter.queryPackage();
		mGallery.setAdapter( mHotAdapter );
		progressbar.setVisibility( View.VISIBLE );
		mGallery.setCallbackDuringFling( false );
		findViewById( R.id.btnReturn ).setOnClickListener( this );
		setwallpaper = (Button)findViewById( R.id.setwallpaper );
		setwallpaper.setOnClickListener( this );
		findViewById( R.id.btnBuy ).setOnClickListener( this );
		findViewById( R.id.btnDownload ).setOnClickListener( this );
		findViewById( R.id.btnInstall ).setOnClickListener( this );
		mImageView = (ImageView)findViewById( R.id.preview );
		mImageView.setScaleType( ScaleType.CENTER_CROP );
		packageReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(
					Context context ,
					Intent intent )
			{
				String actionName = intent.getAction();
				if( actionName.equals( StaticClass.ACTION_LIVE_THUMB_CHANGED ) )
				{
					if( com.coco.theme.themebox.util.FunctionConfig.isLiveWallpaperShow() )
					{
						mHotAdapter.updateThumb( intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) );
					}
				}
				else if( actionName.equals( StaticClass.ACTION_HOTLIST_CHANGED ) )
				{
					if( com.coco.theme.themebox.util.FunctionConfig.isLiveWallpaperShow() )
					{
						mHotAdapter.reloadPackage();
					}
				}
				else if( actionName.equals( StaticClass.ACTION_LIVE_DOWNLOAD_STATUS_CHANGED ) )
				{
					if( com.coco.theme.themebox.util.FunctionConfig.isLiveWallpaperShow() )
					{
						for( int i = 0 ; i < mGallery.getChildCount() ; i++ )
						{
							WallpaperInformation information = (WallpaperInformation)mGallery.getSelectedItem();
							if( information != null && information.getPackageName().equals( intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) ) )
							{
								LiveWallpaperService service = new LiveWallpaperService( mThemeContext );
								service.queryWallpaper( information.getPackageName() , information );
								break;
							}
						}
						mHotAdapter.reloadPackage();
					}
				}
				else if( actionName.equals( StaticClass.ACTION_LIVE_PREVIEW_CHANGED ) )
				{
					String pkg = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
					if( pkg != null && mGallery != null && mGallery.getSelectedItem() != null && pkg.equals( ( (WallpaperInformation)mGallery.getSelectedItem() ).getPackageName() ) )
					{
						if( mPreviewLoader != null && mPreviewLoader.getStatus() != PreviewImageTask.Status.FINISHED )
						{
							mPreviewLoader.cancel();
						}
						mPreviewLoader = (PreviewImageTask)new PreviewImageTask().execute( pkg );
					}
				}
				else if( actionName.equals( StaticClass.ACTION_LIVE_DOWNLOAD_SIZE_CHANGED ) )
				{
					if( com.coco.theme.themebox.util.FunctionConfig.isLiveWallpaperShow() )
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
				else if( Intent.ACTION_PACKAGE_REMOVED.equals( actionName ) || Intent.ACTION_PACKAGE_ADDED.equals( actionName ) )
				{
					String actionPkgName = intent.getData().getSchemeSpecificPart();
					if( mHotAdapter.containPackage( actionPkgName ) )
					{
						mHotAdapter.reloadPackage();
					}
				}
			}
		};
		IntentFilter pkgFilter = new IntentFilter();
		pkgFilter.addAction( Intent.ACTION_PACKAGE_REMOVED );
		pkgFilter.addAction( Intent.ACTION_PACKAGE_ADDED );
		pkgFilter.addDataScheme( "package" );
		registerReceiver( packageReceiver , pkgFilter );
		IntentFilter screenFilter1 = new IntentFilter();
		screenFilter1.addAction( StaticClass.ACTION_LIVE_THUMB_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_HOTLIST_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_LIVE_DOWNLOAD_SIZE_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_LIVE_DOWNLOAD_STATUS_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_LIVE_PREVIEW_CHANGED );
		registerReceiver( packageReceiver , screenFilter1 );
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if( mPreviewLoader != null && mPreviewLoader.getStatus() != PreviewImageTask.Status.FINISHED )
		{
			mPreviewLoader.cancel( true );
			mPreviewLoader = null;
		}
		if( packageReceiver != null )
		{
			unregisterReceiver( packageReceiver );
		}
		if( mHotAdapter != null )
		{
			mHotAdapter.onDestory();
		}
		ActivityManager.popupActivity( this );
	}
	
	public void onItemSelected(
			AdapterView parent ,
			View v ,
			int position ,
			long id )
	{
		if( mPreviewLoader != null && mPreviewLoader.getStatus() != PreviewImageTask.Status.FINISHED )
		{
			mPreviewLoader.cancel();
		}
		mPreviewLoader = (PreviewImageTask)new PreviewImageTask().execute( ( (WallpaperInformation)mHotAdapter.getItem( position ) ).getPackageName() );
		updateShowStatus();
		if( mGallery.getSelectedItem() instanceof WallpaperInformation )
		{
			if( ( (WallpaperInformation)mGallery.getSelectedItem() ).isLiveDownloaded( this ) && ( (WallpaperInformation)mGallery.getSelectedItem() ).getDownloadStatus() != DownloadStatus.StatusDownloading )
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
		if( !( mGallery.getSelectedItem() instanceof WallpaperInformation ) )
		{
			return;
		}
		WallpaperInformation Information = (WallpaperInformation)mGallery.getSelectedItem();
		if( ( Information.isLiveDownloaded( this ) && Information.getDownloadStatus() != DownloadStatus.StatusDownloading ) || Information.isInstalled( this ) )
		{
			delete.setVisibility( View.VISIBLE );
		}
		else
		{
			delete.setVisibility( View.GONE );
		}
		relativeDownload.setClickable( false );
		if( Information.isInstalled( this ) )
		{
			relativeDownload.setVisibility( View.GONE );
			relativeNormal.setVisibility( View.VISIBLE );
			relativeNormal.findViewById( R.id.btnDownload ).setVisibility( View.GONE );
			relativeNormal.findViewById( R.id.setwallpaper ).setVisibility( View.VISIBLE );
			relativeNormal.findViewById( R.id.btnInstall ).setVisibility( View.GONE );
			relativeNormal.findViewById( R.id.btnBuy ).setVisibility( View.GONE );
			return;
		}
		if( !Information.isLiveDownloaded( this ) )
		{
			boolean ispay = Tools.isContentPurchased( this , DownloadList.LiveWallpaper_Type , Information.getPackageName() );
			if( FunctionConfig.isPriceVisible() && Information.getPrice() > 0 && !ispay )
			{
				relativeNormal.setVisibility( View.VISIBLE );
				relativeNormal.findViewById( R.id.btnDownload ).setVisibility( View.GONE );
				relativeNormal.findViewById( R.id.setwallpaper ).setVisibility( View.GONE );
				relativeNormal.findViewById( R.id.btnInstall ).setVisibility( View.GONE );
				relativeNormal.findViewById( R.id.btnBuy ).setVisibility( View.VISIBLE );
				return;
			}
			else
			{
				relativeDownload.setVisibility( View.GONE );
				relativeNormal.setVisibility( View.VISIBLE );
				relativeNormal.findViewById( R.id.btnDownload ).setVisibility( View.VISIBLE );
				relativeNormal.findViewById( R.id.setwallpaper ).setVisibility( View.GONE );
				relativeNormal.findViewById( R.id.btnInstall ).setVisibility( View.GONE );
				relativeNormal.findViewById( R.id.btnBuy ).setVisibility( View.GONE );
				return;
			}
		}
		if( Information.getDownloadStatus() == DownloadStatus.StatusFinish )
		{
			relativeDownload.setVisibility( View.GONE );
			relativeNormal.setVisibility( View.VISIBLE );
			relativeNormal.findViewById( R.id.btnDownload ).setVisibility( View.GONE );
			relativeNormal.findViewById( R.id.setwallpaper ).setVisibility( View.GONE );
			relativeNormal.findViewById( R.id.btnInstall ).setVisibility( View.VISIBLE );
			relativeNormal.findViewById( R.id.btnBuy ).setVisibility( View.GONE );
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
			if( imagePath != null )
			{
				needDownImage = false;
				FileInputStream is = null;
				FileInputStream newis = null;
				try
				{
					is = new FileInputStream( imagePath );
					newis = new FileInputStream( imagePath );
				}
				catch( FileNotFoundException e1 )
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
						needDownImage = true;
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
					downModule.downloadPreview( packageName , DownloadList.LiveWallpaper_Type );
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
		
		public boolean containPackage(
				String packageName )
		{
			return findPackageIndex( packageName ) >= 0;
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
			LiveWallpaperService service = new LiveWallpaperService( context );
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
					if( item.getPackageName().startsWith( "com.vlife.coco.wallpaper" ) || !set.contains( item.getPackageName() ) )
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
				updateShowStatus();
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
				LiveWallpaperService sv = new LiveWallpaperService( LiveWallpaperPreviewActivity.this );
				List<WallpaperInformation> installList = sv.queryInstallList();
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
					downThumb.downloadThumb( info.getPackageName() , DownloadList.LiveWallpaper_Type );
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
	
	private void startLiveWallpaperPicker()
	{
		String pkg = "com.android.wallpaper.livepicker";
		Intent pickWallpaper = new Intent( Intent.ACTION_SET_WALLPAPER );
		List<ResolveInfo> infos = getPackageManager().queryIntentActivities( pickWallpaper , PackageManager.GET_ACTIVITIES );
		for( ResolveInfo info : infos )
		{
			if( info.activityInfo.packageName.equals( pkg ) )
			{
				String cls = info.activityInfo.name;
				Intent it = new Intent( Intent.ACTION_SET_WALLPAPER );
				ComponentName comp = new ComponentName( pkg , cls );
				it.setComponent( comp );
				startActivity( it );
			}
		}
	}
	
	private void setCommonWallpaper(
			WallpaperInformation info )
	{
		if( info.getPackageName().startsWith( "com.vlife" ) )
		{
			PackageManager pkgMgt = getPackageManager();
			Intent localIntent = new Intent( "android.intent.action.MAIN" , null );
			localIntent.addCategory( "com.vlife.coco.intent.category.VLIFE_LIVE_WALLPAPER" );// 壁纸插件的category
			List<ResolveInfo> list = pkgMgt.queryIntentServices( localIntent , 0 );
			if( list.size() == 1 && "com.vlife.coco.wallpaper".equals( list.get( 0 ).serviceInfo.packageName ) )
			{
				Intent it = new Intent();
				it.setPackage( info.getPackageName() );// 壁纸包的包名，例如com.vlife.coco.wallpaper.res.number345
				it.addCategory( "com.vlife.coco.intent.category.VLIFE_SET_WALLPAPER" );
				it.putExtra( "core_package_name" , list.get( 0 ).serviceInfo.packageName.toLowerCase( Locale.ENGLISH ) );
				it.putExtra( "set_wallpaper" , true );
				startService( it );
			}
			else
			{
				HotService hotservice = new HotService( this );
				ThemeInfoItem item = hotservice.queryByPackageName( info.getPackageName() , DownloadList.LiveWallpaper_Type );
				Tools.showNoticeDialog( this , item.getEnginepackname() , item.getEnginedesc() , item.getEngineurl() , item.getEnginesize() );
			}
		}
		else
		{
			boolean builtIn = ( getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0;
			if( builtIn )
			{
				String packageName = info.getPackageName();
				String className = info.getClassName();
				Intent intent = new Intent( android.service.wallpaper.WallpaperService.SERVICE_INTERFACE );
				intent.setClassName( packageName , className );
				setWallpaper( intent.getComponent() );
				Toast.makeText( this , R.string.toast_setwallpaper_success , Toast.LENGTH_SHORT ).show();
			}
			else
			{
				startLiveWallpaperPicker();
			}
		}
	}
	
	private void setWallpaper(
			ComponentName component )
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
		Class<?> IWallpaperManager = null;
		try
		{
			IWallpaperManager = Class.forName( "android.app.IWallpaperManager" );
		}
		catch( ClassNotFoundException e2 )
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if( WallpaperManager != null && IWallpaperManager != null )
		{
			Method getIWallpaperManager = null;
			try
			{
				getIWallpaperManager = WallpaperManager.getMethod( "getIWallpaperManager" , null );
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
			Method setWallpaperComponent = null;
			try
			{
				setWallpaperComponent = IWallpaperManager.getMethod( "setWallpaperComponent" , ComponentName.class );
			}
			catch( SecurityException e1 )
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch( NoSuchMethodException e1 )
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Object am = null;
			if( getIWallpaperManager != null && setWallpaperComponent != null )
			{
				try
				{
					am = getIWallpaperManager.invoke( mWallpaperManager , null );
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
			if( am != null )
			{
				try
				{
					setWallpaperComponent.invoke( IWallpaperManager.cast( am ) , component );
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
		mWallpaperManager.setWallpaperOffsetSteps( 0.5f , 0.0f );
		mWallpaperManager.setWallpaperOffsets( setwallpaper.getRootView().getWindowToken() , 0.5f , 0.0f );
		setResult( RESULT_OK );
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
			setCommonWallpaper( (WallpaperInformation)mGallery.getSelectedItem() );
		}
		else if( v.getId() == R.id.btnDownload )
		{
			if( !com.coco.theme.themebox.StaticClass.isAllowDownloadWithToast( this ) )
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
					DownloadThemeService dSv = new DownloadThemeService( LiveWallpaperPreviewActivity.this );
					DownloadThemeItem dItem = dSv.queryByPackageName( Information.getPackageName() , DownloadList.LiveWallpaper_Type );
					if( dItem == null )
					{
						dItem = new DownloadThemeItem();
						dItem.copyFromThemeInfo( Information.getInfoItem() );
						dItem.setDownloadStatus( DownloadStatus.StatusDownloading );
						dSv.insertItem( dItem );
					}
					LiveWallpaperService service = new LiveWallpaperService( LiveWallpaperPreviewActivity.this );
					service.queryWallpaper( Information.getPackageName() , Information );
					runOnUiThread( new Runnable() {
						
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.setAction( StaticClass.ACTION_LIVE_START_DOWNLOAD_APK );
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
				Intent intent = new Intent( StaticClass.ACTION_LIVE_PAUSE_DOWNLOAD_APK );
				intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , pkg );
				intent.putExtra( "apkname" , ( (WallpaperInformation)mGallery.getSelectedItem() ).getDisplayName() );
				sendBroadcast( intent );
				switchToPause();
			}
			else
			{
				Intent intent = new Intent( StaticClass.ACTION_LIVE_START_DOWNLOAD_APK );
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
				WallpaperInformation info = (WallpaperInformation)mGallery.getSelectedItem();
				if( info.isInstalled( this ) )
				{
					String delApkPackname = "package:" + info.getPackageName();
					Uri packageURI = Uri.parse( delApkPackname );
					Intent uninstallIntent = new Intent( Intent.ACTION_DELETE , packageURI );
					startActivity( uninstallIntent );
					return;
				}
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
						Intent intent = new Intent( StaticClass.ACTION_LIVE_PAUSE_DOWNLOAD_APK );
						intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , packageName );
						intent.putExtra( "apkname" , ( (WallpaperInformation)mGallery.getSelectedItem() ).getDisplayName() );
						sendBroadcast( intent );
						DownloadThemeService dSv = new DownloadThemeService( LiveWallpaperPreviewActivity.this );
						dSv.deleteItem( packageName , DownloadList.LiveWallpaper_Type );
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
			//			CooeePayment.getInstance().startPayService( LiveWallpaperPreviewActivity.this , paymentInfo );
		}
		else if( v.getId() == R.id.btnInstall )
		{
			installApk( ( (WallpaperInformation)mGallery.getSelectedItem() ).getPackageName() );
		}
	}
	
	public void installApk(
			String pkgName )
	{
		String filepath = PathTool.getAppLiveFile( pkgName );
		File file = new File( filepath );
		Log.v( "OpenFile" , file.getName() );
		Intent intent = new Intent();
		intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent.setAction( android.content.Intent.ACTION_VIEW );
		intent.setDataAndType( Uri.fromFile( file ) , "application/vnd.android.package-archive" );
		startActivity( intent );
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
	//					Toast.makeText( LiveWallpaperPreviewActivity.this , "计费成功" , Toast.LENGTH_SHORT ).show();
	//					WallpaperInformation information = (WallpaperInformation)mGallery.getSelectedItem();
	//					Tools.writePurchasedData( LiveWallpaperPreviewActivity.this , DownloadList.LiveWallpaper_Type , information.getPackageName() );
	//					// Message message = iapHandler.obtainMessage(BILLING_FINISH);
	//					// message.sendToTarget();
	//					Intent intent = new Intent( StaticClass.ACTION_LIVE_DOWNLOAD_STATUS_CHANGED );
	//					intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , information.getPackageName() );
	//					sendBroadcast( intent );
	//					break;
	//				case CooeePaymentResultNotify.COOEE_PAYMENT_RESULT_FAIL:
	//					Toast.makeText( LiveWallpaperPreviewActivity.this , "计费失败" + paymentInfo.getVersionName() , Toast.LENGTH_SHORT ).show();
	//					break;
	//				case CooeePaymentResultNotify.COOEE_PAYMENT_RESULT_CANCEL_BY_USER:
	//					Toast.makeText( LiveWallpaperPreviewActivity.this , "用户取消付费" , Toast.LENGTH_SHORT ).show();
	//					break;
	//				case 1:
	//					Toast.makeText( LiveWallpaperPreviewActivity.this , "配置免费" , Toast.LENGTH_SHORT ).show();
	//					break;
	//				case 2:
	//					Toast.makeText( LiveWallpaperPreviewActivity.this , "不需要重复计费" , Toast.LENGTH_SHORT ).show();
	//					break;
	//				case 3:
	//					Toast.makeText( LiveWallpaperPreviewActivity.this , "无可用指令" , Toast.LENGTH_SHORT ).show();
	//					break;
	//			}
	//		}
	//	}
}
