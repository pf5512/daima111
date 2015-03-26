package com.coco.wallpaper.wallpaperbox;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.coco.download.Assets;
import com.coco.download.DownloadList;
import com.coco.pub.provider.PubContentProvider;
import com.coco.pub.provider.PubProviderHelper;
import com.coco.theme.themebox.DownloadApkContentService;
import com.coco.theme.themebox.PullToRefreshView;
import com.coco.theme.themebox.PullToRefreshView.OnFooterRefreshListener;
import com.coco.theme.themebox.PullToRefreshView.OnHeaderRefreshListener;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.service.ThemesDB;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;


public class TabWallpaperFactory implements TabHost.TabContentFactory , OnHeaderRefreshListener , OnFooterRefreshListener
{
	
	/**
	 * 判断是否联网
	 */
	public boolean IsHaveInternet(
			final Context context )
	{
		try
		{
			ConnectivityManager manger = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo info = manger.getActiveNetworkInfo();
			return( info != null && info.isConnected() );
		}
		catch( Exception e )
		{
			return false;
		}
	}
	
	Context mContext;
	GridView gridviewLocal;
	private GridView gridViewHot;
	private GridView gridViewLive;
	private GridHotWallpaperAdapter hotAdapter;
	private GridLiveWallpaperAdapter liveAdapter;
	private ViewPager gridPager;
	private GridPagerAdapter themePagerAdapter;
	private DownModule downModule;
	private final int INDEX_LOCAL = 0;
	private final int INDEX_HOT = 1;
	private View hotView;
	private PullToRefreshView mPullToRefreshView;
	private boolean footerRefresh = false;
	private boolean headerRefresh = false;
	private Handler handler = new Handler();
	private boolean listRefresh = false;
	private static boolean interneterr = false;
	List<ResolveInfo> mResolveInfoList = new ArrayList<ResolveInfo>();
	private List<String> mThumbs = new ArrayList<String>( 24 );
	boolean useCustomWallpaper = false;
	private final String wallpaperPath = "launcher/wallpapers";
	private String[] galleryPkg;
	private String GALLERY = "";
	private final String LIVEPICKER = "com.android.wallpaper.livepicker";
	private final String UPDATA_CURRENT = "com.coco.wallpaper.update";
	private final String HTC_ONE_PACK_NAME = "com.htc.home.personalize";
	private final String HTC_ONE_RESOLVE_NAME = "com.htc.home.personalize.picker.WallpaperLivePicker";
	String customWallpaperPath;
	GridLocalWallpaperAdapter mAdapter;
	ProgressDialog mDialog;
	private WallpaperInfo infos;
	private List<Bitmap> localBmp = new ArrayList<Bitmap>();
	//Jone add start
	private boolean isfindGallery = false;
	private boolean isfindLivepaper = false;
	private boolean ifselect=false;
	//Jone end 
	public TabWallpaperFactory(
			Context context ,
			DownModule module ,boolean ifselectHot)
	{
		// TODO Auto-generated constructor stub
		mContext = context;
		downModule = module;
		ifselect=ifselectHot;
		PathTool.makeDirApp();
	}
	
	@Override
	public View createTabContent(
			String tag )
	{
		// TODO Auto-generated method stub
		View result = View.inflate( mContext , R.layout.theme_main , null );
		View containHot = result.findViewById( R.id.containHot );
		final RadioButton LocalButton = (RadioButton)result.findViewById( R.id.btnLocalTheme );
		LocalButton.setText( R.string.text_lcoal_Wallpapers );
		infos = new WallpaperInfo( mContext );
		gridviewLocal = (GridView)(GridView)( View.inflate( mContext , R.layout.lock_grid , null ) );
		if( FunctionConfig.isdoovStyle() )
		{
			containHot.setVisibility( View.GONE );
		}
		gridviewLocal.setNumColumns( 2 );
		gridviewLocal.setPadding( 0 , 0 , 0 , 0 );
		gridviewLocal.setHorizontalSpacing( 0 );
		gridviewLocal.setVerticalSpacing( 0 );
		gridviewLocal.setColumnWidth( infos.getScreenWidth() / 2 );
		mDialog = new ProgressDialog( mContext );
		mAdapter = new GridLocalWallpaperAdapter( mContext , downModule );
		gridviewLocal.setAdapter( mAdapter );
		gridviewLocal.setOnItemClickListener( new OnItemClickListener() {
			
			@Override
			public void onItemClick(
					AdapterView<?> parent ,
					View view ,
					int position ,
					long id )
			{
				// TODO Auto-generated method stub
				if( position < mResolveInfoList.size() )
				{
					int requestCode = 0;
					ResolveInfo resolveInfo = mResolveInfoList.get( position );
					ComponentName mComponentName = new ComponentName( resolveInfo.activityInfo.packageName , resolveInfo.activityInfo.name );
					Intent intent = new Intent( Intent.ACTION_SET_WALLPAPER );
					intent.setComponent( mComponentName );
					if( GALLERY.equals( resolveInfo.activityInfo.packageName ) )
					{
						requestCode = 2000;
					}
					else if( LIVEPICKER.equals( resolveInfo.activityInfo.packageName ) )
					{
						requestCode = 2001;
					}
					else if( "com.coco.wallpaper.wallpaperbox.WallpaperPreviewActivity".equals( resolveInfo.activityInfo.name ) )
					{
						intent.setClass( mContext , WallpaperPreviewActivity.class );
						intent.putExtra( "type" , "local" );
						intent.putExtra( "position" , position );
						intent.putExtra( "buttonsize" , mResolveInfoList.size() );
						intent.putExtra( "customWallpaperPath" , FunctionConfig.getCustomWallpaperPath() );
						intent.putExtra( "disableSetWallpaperDimensions" , FunctionConfig.getDisableSetWallpaperDimensions() );
						intent.putExtra( "fromotherapk" , FunctionConfig.getWallpapers_from_other_apk() );
						intent.putExtra( "launchername" , ThemesDB.LAUNCHER_PACKAGENAME );
					}
					else if( "com.coco.wallpaper.wallpaperbox.LockWallpaperPreview".equals( resolveInfo.activityInfo.name ) )
					{
					}
					if( position == 0 && isfindGallery == false )
					{
						Intent chooser = Intent.createChooser( intent , mContext.getText( R.string.select_gallery ) );
						( (Activity)mContext ).startActivityForResult( chooser , requestCode );
					}
					else if( position == 1 && isfindLivepaper == false )
					{
						Intent chooser = Intent.createChooser( intent , mContext.getText( R.string.select_live_wallpaper ) );
						( (Activity)mContext ).startActivityForResult( chooser , requestCode );
					}
					else
						( (Activity)mContext ).startActivityForResult( intent , requestCode );
				}
				else
				{
					Intent it = new Intent();
					it.setClass( mContext , WallpaperPreviewActivity.class );
					it.putExtra( "type" , "local" );
					it.putExtra( "position" , position );
					it.putExtra( "buttonsize" , mResolveInfoList.size() );
					it.putExtra( "customWallpaperPath" , FunctionConfig.getCustomWallpaperPath() );
					it.putExtra( "disableSetWallpaperDimensions" , FunctionConfig.getDisableSetWallpaperDimensions() );
					it.putExtra( "fromotherapk" , FunctionConfig.getWallpapers_from_other_apk() );
					it.putExtra( "launchername" , ThemesDB.LAUNCHER_PACKAGENAME );
					mContext.startActivity( it );
				}
			}
		} );
		packageReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(
					Context context ,
					Intent intent )
			{
				String actionName = intent.getAction();
				if( actionName.equals( UPDATA_CURRENT ) )
				{
					if( intent.getStringExtra( "wallpaper" ) != null )
					{
						SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( mContext );
						pref.edit().putString( "currentWallpaper" , intent.getStringExtra( "wallpaper" ) ).commit();
						pref.edit().putBoolean( "cooeechange" , true ).commit();
						PubProviderHelper.addOrUpdateValue( PubContentProvider.LAUNCHER_AUTHORITY , "wallpaper" , "currentWallpaper" , intent.getStringExtra( "wallpaper" ) );
						PubProviderHelper.addOrUpdateValue( PubContentProvider.LAUNCHER_AUTHORITY , "wallpaper" , "cooeechange" , "true" );
						System.out.println( "pref onclick " + pref.getBoolean( "cooeechange" , false ) );
					}
					mAdapter.notifyDataSetChanged();
				}
				else if( actionName.equals( StaticClass.ACTION_THUMB_CHANGED ) )
				{
					if( com.coco.theme.themebox.util.FunctionConfig.isShowHotWallpaper() )
					{
						hotAdapter.updateThumb( intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) );
					}
				}
				else if( actionName.equals( StaticClass.ACTION_HOTLIST_CHANGED ) )
				{
					Log.v( "***************" , "222222" + StaticClass.ACTION_HOTLIST_CHANGED );
					if( com.coco.theme.themebox.util.FunctionConfig.isShowHotWallpaper() )
					{
						hotAdapter.reloadPackage();
						hotAdapter.setShowProgress( false );
					}
					if( FunctionConfig.isLiveWallpaperShow() )
					{
						liveAdapter.reloadPackage();
						liveAdapter.setShowProgress( false );
					}
					themePagerAdapter.notifyDataSetChanged();
					if( listRefresh )
					{
						listRefresh = false;
					}
					if( footerRefresh )
					{
						mPullToRefreshView.onFooterRefreshComplete();
						footerRefresh = false;
					}
					if( headerRefresh )
					{
						mPullToRefreshView.onHeaderRefreshComplete();
						headerRefresh = false;
					}
				}
				else if( actionName.equals( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED ) )
				{
					mAdapter.reloadPackage();
					if( com.coco.theme.themebox.util.FunctionConfig.isShowHotWallpaper() )
					{
						hotAdapter.reloadPackage();
					}
				}
				else if( actionName.equals( StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED ) )
				{
					if( com.coco.theme.themebox.util.FunctionConfig.isShowHotWallpaper() )
					{
						hotAdapter.updateDownloadSize(
								intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) ,
								intent.getIntExtra( StaticClass.EXTRA_DOWNLOAD_SIZE , 0 ) ,
								intent.getIntExtra( StaticClass.EXTRA_TOTAL_SIZE , 0 ) );
					}
				}
				else if( actionName.equals( StaticClass.ACTION_START_DOWNLOAD_APK ) )
				{
					String curdownApkname = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
					// downModule.downloadApk(curdownApkname);
					Intent it = new Intent();
					it.putExtra( "name" , intent.getStringExtra( "apkname" ) );
					it.putExtra( "packageName" , curdownApkname );
					it.putExtra( "type" , DownloadList.Wallpaper_Type );
					it.putExtra( "status" , "download" );
					it.setClass( mContext , DownloadApkContentService.class );
					mContext.startService( it );
					if( com.coco.theme.themebox.util.FunctionConfig.isShowHotWallpaper() )
					{
						hotAdapter.notifyDataSetChanged();
					}
				}
				else if( actionName.equals( StaticClass.ACTION_PAUSE_DOWNLOAD_APK ) )
				{
					String packName = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
					// downModule.stopDownApk(packName);
					Intent it = new Intent();
					it.putExtra( "packageName" , packName );
					it.putExtra( "type" , DownloadList.Wallpaper_Type );
					it.putExtra( "name" , intent.getStringExtra( "apkname" ) );
					it.putExtra( "status" , "pause" );
					it.setClass( mContext , DownloadApkContentService.class );
					mContext.startService( it );
					if( com.coco.theme.themebox.util.FunctionConfig.isShowHotWallpaper() )
					{
						hotAdapter.notifyDataSetChanged();
					}
					Log.v( "********" , "receive packName = " + packName );
				}
				else if( actionName.equals( StaticClass.ACTION_LIVE_THUMB_CHANGED ) )
				{
					if( FunctionConfig.isLiveWallpaperShow() )
					{
						liveAdapter.updateThumb( intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) );
					}
				}
				else if( actionName.equals( StaticClass.ACTION_LIVE_DOWNLOAD_STATUS_CHANGED ) )
				{
					if( FunctionConfig.isLiveWallpaperShow() )
					{
						liveAdapter.reloadPackage();
					}
				}
				else if( actionName.equals( StaticClass.ACTION_LIVE_DOWNLOAD_SIZE_CHANGED ) )
				{
					if( FunctionConfig.isLiveWallpaperShow() )
					{
						liveAdapter.updateDownloadSize(
								intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) ,
								intent.getIntExtra( StaticClass.EXTRA_DOWNLOAD_SIZE , 0 ) ,
								intent.getIntExtra( StaticClass.EXTRA_TOTAL_SIZE , 0 ) );
					}
				}
				else if( actionName.equals( StaticClass.ACTION_LIVE_START_DOWNLOAD_APK ) )
				{
					if( FunctionConfig.isLiveWallpaperShow() )
					{
						String curdownApkname = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
						// downModule.downloadApk(curdownApkname);
						Intent it = new Intent();
						it.putExtra( "name" , intent.getStringExtra( "apkname" ) );
						it.putExtra( "packageName" , curdownApkname );
						it.putExtra( "type" , DownloadList.LiveWallpaper_Type );
						it.putExtra( "status" , "download" );
						it.setClass( mContext , DownloadApkContentService.class );
						mContext.startService( it );
						liveAdapter.notifyDataSetChanged();
					}
				}
				else if( actionName.equals( StaticClass.ACTION_LIVE_PAUSE_DOWNLOAD_APK ) )
				{
					if( FunctionConfig.isLiveWallpaperShow() )
					{
						String packName = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
						// downModule.stopDownApk(packName);
						Intent it = new Intent();
						it.putExtra( "packageName" , packName );
						it.putExtra( "type" , DownloadList.LiveWallpaper_Type );
						it.putExtra( "name" , intent.getStringExtra( "apkname" ) );
						it.putExtra( "status" , "pause" );
						it.setClass( mContext , DownloadApkContentService.class );
						mContext.startService( it );
						liveAdapter.notifyDataSetChanged();
					}
				}
				else if( Intent.ACTION_PACKAGE_REMOVED.equals( actionName ) )
				{
					if( FunctionConfig.isLiveWallpaperShow() )
					{
						String packageName = intent.getData().getSchemeSpecificPart();
						if( com.coco.theme.themebox.util.FunctionConfig.isShowHotWallpaper() )
						{
							if( liveAdapter.findPackageIndex( packageName ) >= 0 )
								liveAdapter.reloadPackage();
						}
					}
				}
				else if( Intent.ACTION_PACKAGE_ADDED.equals( actionName ) )
				{
					if( FunctionConfig.isLiveWallpaperShow() )
					{
						String packageName = intent.getData().getSchemeSpecificPart();
						if( com.coco.theme.themebox.util.FunctionConfig.isShowHotWallpaper() )
						{
							if( liveAdapter.findPackageIndex( packageName ) >= 0 )
								liveAdapter.reloadPackage();
						}
					}
				}
			}
		};
		if( FunctionConfig.isShowHotWallpaper() || FunctionConfig.isLiveWallpaperShow() )
		{
			hotView = View.inflate( mContext , R.layout.wallpaper_grid_hot , null );
			mPullToRefreshView = (PullToRefreshView)hotView.findViewById( R.id.wallpaper_refresh );
			gridViewHot = (GridView)hotView.findViewById( R.id.gridStatic );
			gridViewLive = (GridView)hotView.findViewById( R.id.gridLive );
			gridPager = (ViewPager)result.findViewById( R.id.themeGridPager );
			if( !( FunctionConfig.isShowHotWallpaper() && FunctionConfig.isLiveWallpaperShow() ) )
			{
				hotView.findViewById( R.id.wallpaperradioGroup ).setVisibility( View.GONE );
				if( FunctionConfig.isShowHotWallpaper() )
				{
					gridViewHot.setVisibility( View.VISIBLE );
					gridViewLive.setVisibility( View.GONE );
				}
				if( FunctionConfig.isLiveWallpaperShow() )
				{
					gridViewHot.setVisibility( View.GONE );
					gridViewLive.setVisibility( View.VISIBLE );
				}
			}
			else
			{
				RadioButton staticwallpaper = (RadioButton)hotView.findViewById( R.id.radioStatic );
				RadioButton livewallpaper = (RadioButton)hotView.findViewById( R.id.radioLive );
				staticwallpaper.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick(
							View v )
					{
						// TODO Auto-generated method stub
						gridViewHot.setVisibility( View.VISIBLE );
						gridViewLive.setVisibility( View.GONE );
						mPullToRefreshView.initContentAdapterView();
					}
				} );
				livewallpaper.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick(
							View v )
					{
						// TODO Auto-generated method stub
						gridViewHot.setVisibility( View.GONE );
						gridViewLive.setVisibility( View.VISIBLE );
						mPullToRefreshView.initContentAdapterView();
					}
				} );
			}
			if( FunctionConfig.isShowHotWallpaper() )
			{
				gridViewHot.setColumnWidth( infos.getScreenWidth() / 2 );
				hotAdapter = new GridHotWallpaperAdapter( mContext , downModule );
				gridViewHot.setAdapter( hotAdapter );
				if( hotAdapter.showProgress() || downModule.isRefreshList() )
				{
					downModule.downloadList();
					if( IsHaveInternet( mContext ) )
					{
						interneterr = false;
						listRefresh = true;
						handler.postDelayed( new Runnable() {
							
							@Override
							public void run()
							{
								downModule.stopDownlist();
								if( listRefresh )
								{
									if( com.coco.theme.themebox.util.FunctionConfig.isPromptVisible() )
									{
										if( gridPager.getCurrentItem() == INDEX_HOT )
											Toast.makeText( mContext , R.string.internet_unusual , Toast.LENGTH_SHORT ).show();
									}
								}
								if( themePagerAdapter != null && themePagerAdapter.viewDownloading != null )
								{
									themePagerAdapter.viewDownloading.setVisibility( View.INVISIBLE );
								}
							}
						} , 1000 * 30 );
					}
					else
					{
						if( gridPager.getCurrentItem() == INDEX_HOT )
							Toast.makeText( mContext , R.string.internet_err , Toast.LENGTH_SHORT ).show();
						interneterr = true;
						if( themePagerAdapter != null && themePagerAdapter.viewDownloading != null )
						{
							themePagerAdapter.viewDownloading.setVisibility( View.INVISIBLE );
						}
					}
				}
				gridViewHot.setOnItemClickListener( new OnItemClickListener() {
					
					@Override
					public void onItemClick(
							AdapterView<?> parent ,
							View v ,
							int position ,
							long id )
					{
						WallpaperInformation infor = (WallpaperInformation)parent.getItemAtPosition( position );
						Intent i = new Intent();
						i.putExtra( "type" , "hot" );
						i.putExtra( StaticClass.EXTRA_PACKAGE_NAME , infor.getPackageName() );
						i.putExtra( "position" , position );
						i.setClass( mContext , WallpaperPreviewActivity.class );
						i.putExtra( "isPriceVisible" , FunctionConfig.isPriceVisible() );
						mContext.startActivity( i );
					}
				} );
			}
			if( FunctionConfig.isLiveWallpaperShow() )
			{
				gridViewLive.setColumnWidth( infos.getScreenWidth() / 2 );
				liveAdapter = new GridLiveWallpaperAdapter( mContext );
				gridViewLive.setAdapter( liveAdapter );
				if( liveAdapter.showProgress() || liveAdapter.getDownModule().isRefreshList() )
				{
					liveAdapter.getDownModule().downloadList();
					if( IsHaveInternet( mContext ) )
					{
						interneterr = false;
						listRefresh = true;
						handler.postDelayed( new Runnable() {
							
							@Override
							public void run()
							{
								liveAdapter.getDownModule().stopDownlist();
								if( listRefresh )
								{
									if( com.coco.theme.themebox.util.FunctionConfig.isPromptVisible() )
									{
										if( gridPager.getCurrentItem() == INDEX_HOT )
											Toast.makeText( mContext , R.string.internet_unusual , Toast.LENGTH_SHORT ).show();
									}
								}
								if( themePagerAdapter != null && themePagerAdapter.viewDownloading != null )
								{
									themePagerAdapter.viewDownloading.setVisibility( View.INVISIBLE );
								}
							}
						} , 1000 * 30 );
					}
					else
					{
						if( gridPager.getCurrentItem() == INDEX_HOT )
							Toast.makeText( mContext , R.string.internet_err , Toast.LENGTH_SHORT ).show();
						interneterr = true;
						if( themePagerAdapter != null && themePagerAdapter.viewDownloading != null )
						{
							themePagerAdapter.viewDownloading.setVisibility( View.INVISIBLE );
						}
					}
				}
				gridViewLive.setOnItemClickListener( new OnItemClickListener() {
					
					@Override
					public void onItemClick(
							AdapterView<?> parent ,
							View view ,
							int position ,
							long id )
					{
						// TODO Auto-generated method stub
						WallpaperInformation infor = (WallpaperInformation)parent.getItemAtPosition( position );
						Intent i = new Intent();
						i.putExtra( StaticClass.EXTRA_PACKAGE_NAME , infor.getPackageName() );
						i.putExtra( "position" , position );
						i.setClass( mContext , LiveWallpaperPreviewActivity.class );
						i.putExtra( "isPriceVisible" , FunctionConfig.isPriceVisible() );
						mContext.startActivity( i );
					}
				} );
			}
			mPullToRefreshView.setOnHeaderRefreshListener( this );
			mPullToRefreshView.setOnFooterRefreshListener( this );
			// ViewPager
			themePagerAdapter = new GridPagerAdapter( gridviewLocal , hotView );
			if( FunctionConfig.isShowHotWallpaper() )
				themePagerAdapter.setGridView( gridViewHot );
			else if( FunctionConfig.isLiveWallpaperShow() )
				themePagerAdapter.setGridView( gridViewLive );
			gridPager.setAdapter( themePagerAdapter );
			gridPager.setOverScrollMode( View.OVER_SCROLL_NEVER );
			// 热门按钮
			final RadioButton hotButton = (RadioButton)result.findViewById( R.id.btnHotTheme );
			final RadioButton localButton = (RadioButton)result.findViewById( R.id.btnLocalTheme );
			
			if(ifselect){
				hotButton.performClick();
				gridPager.setCurrentItem( INDEX_HOT , true );
			}
			
			gridPager.setOnPageChangeListener( new OnPageChangeListener() {
				
				@Override
				public void onPageScrollStateChanged(
						int arg0 )
				{
				}
				
				@Override
				public void onPageScrolled(
						int arg0 ,
						float arg1 ,
						int arg2 )
				{
				}
				
				@Override
				public void onPageSelected(
						int index )
				{
					if( index == INDEX_LOCAL )
					{
						localButton.toggle();
					}
					else if( index == INDEX_HOT )
					{
						if( com.coco.theme.themebox.StaticClass.isAllowDownload( mContext ) )
						{
							// 无网�?
							if( IsHaveInternet( mContext ) == false )
							{
								Toast.makeText( mContext , R.string.internet_err , Toast.LENGTH_SHORT ).show();
								if( themePagerAdapter != null && themePagerAdapter.viewDownloading != null )
								{
									themePagerAdapter.viewDownloading.setVisibility( View.INVISIBLE );
								}
							}
							else
							{
								if( themePagerAdapter != null && themePagerAdapter.viewDownloading != null )
								{
									themePagerAdapter.viewDownloading.setVisibility( View.VISIBLE );
								}
								if( FunctionConfig.isShowHotWallpaper() )
								{
									if( hotAdapter.getCount() == 0 )
									{
										themePagerAdapter.notifyDataSetChanged();
										downModule.downloadList();
										if( IsHaveInternet( mContext ) )
										{
											listRefresh = true;
											handler.postDelayed( new Runnable() {
												
												@Override
												public void run()
												{
													downModule.stopDownlist();
													if( listRefresh )
													{
														if( com.coco.theme.themebox.util.FunctionConfig.isPromptVisible() )
														{
															Toast.makeText( mContext , R.string.internet_unusual , Toast.LENGTH_SHORT ).show();
														}
													}
													if( themePagerAdapter.viewDownloading != null )
													{
														themePagerAdapter.viewDownloading.setVisibility( View.INVISIBLE );
													}
												}
											} , 1000 * 30 );
										}
									}
								}
								else
								{
									if( liveAdapter.getCount() == 0 )
									{
										themePagerAdapter.notifyDataSetChanged();
										liveAdapter.getDownModule().downloadList();
										if( IsHaveInternet( mContext ) )
										{
											listRefresh = true;
											handler.postDelayed( new Runnable() {
												
												@Override
												public void run()
												{
													liveAdapter.getDownModule().stopDownlist();
													if( listRefresh )
													{
														if( com.coco.theme.themebox.util.FunctionConfig.isPromptVisible() )
														{
															Toast.makeText( mContext , R.string.internet_unusual , Toast.LENGTH_SHORT ).show();
														}
													}
													if( themePagerAdapter.viewDownloading != null )
													{
														themePagerAdapter.viewDownloading.setVisibility( View.INVISIBLE );
													}
												}
											} , 1000 * 30 );
										}
									}
								}
							}
						}
						else
						{
							Toast.makeText( mContext , R.string.sdcard_not_available , Toast.LENGTH_SHORT ).show();
						}
						hotButton.toggle();
					}
				}
			} );
			// 热门
			hotButton.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(
						View arg0 )
				{
					gridPager.setCurrentItem( INDEX_HOT , true );
				}
			} );
			// 本地按钮
			localButton.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(
						View arg0 )
				{
					gridPager.setCurrentItem( INDEX_LOCAL , true );
				}
			} );
		}
		else
		{
			// 热门按钮
			if( FunctionConfig.isdoovStyle() )
			{
				containHot.setVisibility( View.GONE );
			}
			else
			{
				containHot.setVisibility( View.VISIBLE );
				final RadioButton themeHotButton = (RadioButton)result.findViewById( R.id.btnHotTheme );
				final RadioButton themeLocalButton = (RadioButton)result.findViewById( R.id.btnLocalTheme );
				themeHotButton.setVisibility( View.GONE );
			}
			// ViewPager
			gridPager = (ViewPager)result.findViewById( R.id.themeGridPager );
			themePagerAdapter = new GridPagerAdapter( gridviewLocal );
			gridPager.setAdapter( themePagerAdapter );
			gridPager.setOverScrollMode( View.OVER_SCROLL_NEVER );
		}
		if( FunctionConfig.isLiveWallpaperShow() )
		{
			IntentFilter pkgFilter = new IntentFilter();
			pkgFilter.addAction( Intent.ACTION_PACKAGE_REMOVED );
			pkgFilter.addAction( Intent.ACTION_PACKAGE_ADDED );
			pkgFilter.addDataScheme( "package" );
			mContext.registerReceiver( packageReceiver , pkgFilter );
		}
		IntentFilter screenFilter1 = new IntentFilter();
		screenFilter1.addAction( UPDATA_CURRENT );
		screenFilter1.addAction( StaticClass.ACTION_START_DOWNLOAD_APK );
		screenFilter1.addAction( StaticClass.ACTION_THUMB_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_HOTLIST_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_PAUSE_DOWNLOAD_APK );
		if( FunctionConfig.isLiveWallpaperShow() )
		{
			screenFilter1.addAction( StaticClass.ACTION_LIVE_START_DOWNLOAD_APK );
			screenFilter1.addAction( StaticClass.ACTION_LIVE_THUMB_CHANGED );
			screenFilter1.addAction( StaticClass.ACTION_LIVE_DOWNLOAD_SIZE_CHANGED );
			screenFilter1.addAction( StaticClass.ACTION_LIVE_DOWNLOAD_STATUS_CHANGED );
			screenFilter1.addAction( StaticClass.ACTION_LIVE_PAUSE_DOWNLOAD_APK );
		}
		mContext.registerReceiver( packageReceiver , screenFilter1 );
		// 下载成功
		return result;
	}
	
	private BroadcastReceiver packageReceiver;
	
	public void onDestroy()
	{
		if( packageReceiver != null )
		{
			mContext.unregisterReceiver( packageReceiver );
		}
		if( mAdapter != null )
		{
			mAdapter.onDestory();
		}
		if( hotAdapter != null )
		{
			hotAdapter.onDestory();
		}
		if( liveAdapter != null )
		{
			liveAdapter.onDestory();
		}
	}
	
	class GridHotWallpaperAdapter extends BaseAdapter
	{
		
		private List<WallpaperInformation> appList = new ArrayList<WallpaperInformation>();
		private Context context;
		private DownModule downThumb;
		private Bitmap imgDefaultThumb;
		private boolean mShowProgress = false;
		private PageTask pageTask = null;
		private Set<ImageView> recycle = new HashSet<ImageView>();
		
		public GridHotWallpaperAdapter(
				Context cxt ,
				DownModule down )
		{
			context = cxt;
			downThumb = down;
			imgDefaultThumb = ( (BitmapDrawable)cxt.getResources().getDrawable( R.drawable.default_img_large ) ).getBitmap();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
			int size = preferences.getInt( "list-" + DownloadList.Wallpaper_Type , 0 );
			if( size == 0 )
			{
				if( !com.coco.theme.themebox.StaticClass.isAllowDownload( context ) )
				{
					mShowProgress = false;
				}
				else
				{
					mShowProgress = true;
				}
			}
			else
			{
				mShowProgress = false;
			}
			if( pageTask != null && pageTask.getStatus() != PageTask.Status.FINISHED )
			{
				pageTask.cancel( true );
			}
			pageTask = (PageTask)new PageTask().execute();
		}
		
		public void onDestory()
		{
			for( WallpaperInformation info : appList )
			{
				info.disposeThumb();
				info = null;
			}
			if( imgDefaultThumb != null && !imgDefaultThumb.isRecycled() )
			{
				imgDefaultThumb.recycle();
			}
		}
		
		public boolean showProgress()
		{
			System.out.println( "hotAdapter showprogress = " + mShowProgress );
			return mShowProgress;
		}
		
		public void setShowProgress(
				boolean isShow )
		{
			mShowProgress = isShow;
		}
		
		public void reloadPackage()
		{
			if( pageTask != null && pageTask.getStatus() != PageTask.Status.FINISHED )
			{
				pageTask.cancel( true );
			}
			pageTask = (PageTask)new PageTask().execute();
		}
		
		public List<WallpaperInformation> queryPackage(
				Set<String> pkgNameSet )
		{
			List<WallpaperInformation> appList = new ArrayList<WallpaperInformation>();
			WallpaperService service = new WallpaperService( context );
			List<WallpaperInformation> hotList = service.queryShowList();
			if( hotList.size() == 0 )
			{
				if( !com.coco.theme.themebox.StaticClass.isAllowDownload( context ) )
				{
					mShowProgress = false;
				}
				else
				{
					mShowProgress = true;
				}
			}
			else
			{
				mShowProgress = false;
			}
			for( WallpaperInformation item : hotList )
			{
				if( !pkgNameSet.contains( item.getPackageName() ) )
				{
					appList.add( item );
				}
			}
			return appList;
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
		
		public void updateThumb(
				final String pkgName )
		{
			new Thread() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					int findIndex = findPackageIndex( pkgName );
					if( findIndex < 0 )
					{
						return;
					}
					WallpaperInformation info = appList.get( findIndex );
					info.reloadThumb();
					( (Activity)context ).runOnUiThread( new Runnable() {
						
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							notifyDataSetChanged();
						}
					} );
				}
			}.start();
		}
		
		public void updateDownloadSize(
				String pkgName ,
				long downSize ,
				long totalSize )
		{
			int findIndex = findPackageIndex( pkgName );
			if( findIndex < 0 )
			{
				return;
			}
			WallpaperInformation info = appList.get( findIndex );
			info.setDownloadSize( downSize );
			info.setTotalSize( totalSize );
			notifyDataSetChanged();
		}
		
		@Override
		public void notifyDataSetChanged()
		{
			// TODO Auto-generated method stub
			recycle.clear();
			super.notifyDataSetChanged();
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
			View retView = convertView;
			ViewHolder viewHolder = null;
			if( retView != null )
			{
				// Log.e("test", "convertView!=null");
				viewHolder = (ViewHolder)convertView.getTag();
			}
			else
			{
				retView = LayoutInflater.from( mContext ).inflate( R.layout.main_font_item , null );
				viewHolder = new ViewHolder();
				viewHolder.viewName = (TextView)retView.findViewById( R.id.textAppName );
				viewHolder.viewThumb = (ImageView)retView.findViewById( R.id.imageThumb );
				viewHolder.imageCover = (ImageView)retView.findViewById( R.id.imageCover );
				viewHolder.imageUsed = (ImageView)retView.findViewById( R.id.imageUsed );
				viewHolder.barPause = (ProgressBar)retView.findViewById( R.id.barPause );
				viewHolder.barDownloading = (ProgressBar)retView.findViewById( R.id.barDownloading );
				viewHolder.pricetxt = (TextView)retView.findViewById( R.id.price );
				viewHolder.imageUsed.setVisibility( View.INVISIBLE );
			}
			recycle.add( viewHolder.viewThumb );
			Recyclebitmap( viewHolder.viewThumb );
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
			viewHolder.viewThumb.setImageBitmap( imgThumb );
			if( FunctionConfig.isPriceVisible() )
			{
				int price = info.getPrice();
				if( info.getPrice() > 0 && !info.isDownloadedFinish() )
				{// 下载完成后，热门推荐中不显示价格
					viewHolder.pricetxt.setVisibility( View.VISIBLE );
					boolean ispay = Tools.isContentPurchased( context , DownloadList.Wallpaper_Type , info.getPackageName() );
					if( ispay )
					{
						viewHolder.pricetxt.setBackgroundResource( R.drawable.buyed_bg );
						viewHolder.pricetxt.setText( R.string.has_bought );
					}
					else
					{
						viewHolder.pricetxt.setBackgroundResource( R.drawable.price_bg );
						viewHolder.pricetxt.setText( "￥：" + price / 100 );
					}
				}
				else
				{
					viewHolder.pricetxt.setVisibility( View.GONE );
				}
			}
			if( info.getDownloadStatus() == DownloadStatus.StatusInit || info.getDownloadStatus() == DownloadStatus.StatusFinish )
			{
				viewHolder.imageCover.setVisibility( View.INVISIBLE );
				viewHolder.barPause.setVisibility( View.INVISIBLE );
				viewHolder.barDownloading.setVisibility( View.INVISIBLE );
			}
			else
			{
				viewHolder.imageCover.setVisibility( View.VISIBLE );
				if( info.getDownloadStatus() == DownloadStatus.StatusDownloading )
				{
					viewHolder.barDownloading.setVisibility( View.VISIBLE );
					viewHolder.barPause.setVisibility( View.INVISIBLE );
					viewHolder.barDownloading.setProgress( info.getDownloadPercent() );
				}
				else
				{
					viewHolder.barDownloading.setVisibility( View.INVISIBLE );
					viewHolder.barPause.setVisibility( View.VISIBLE );
					viewHolder.barPause.setProgress( info.getDownloadPercent() );
				}
			}
			viewHolder.viewName.setVisibility( View.GONE );
			retView.setTag( viewHolder );
			return retView;
		}
		
		private void Recyclebitmap(
				ImageView view )
		{
			boolean isrecycle = true;
			Bitmap bmp = Tools.recycleImageBitmap( view );
			if( bmp == null || bmp.isRecycled() || bmp == imgDefaultThumb )
			{
				return;
			}
			for( ImageView v : recycle )
			{
				if( v == view )
				{
					continue;
				}
				Bitmap temp = Tools.recycleImageBitmap( v );
				if( temp == bmp )
				{
					isrecycle = false;
					break;
				}
			}
			if( isrecycle )
			{
				bmp.recycle();
			}
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
				for( WallpaperInformation info : appList )
				{
					info.disposeThumb();
					info = null;
				}
				appList.clear();
				appList.addAll( result );
				notifyDataSetChanged();
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
				WallpaperService themeSv = new WallpaperService( mContext );
				List<WallpaperInformation> installList = themeSv.queryDownloadList();
				Set<String> packageNameSet = new HashSet<String>();
				for( WallpaperInformation info : installList )
				{
					packageNameSet.add( info.getPackageName() );
				}
				return queryPackage( packageNameSet );
			}
		}
	}
	
	class ViewHolder
	{
		
		ImageView viewThumb;
		TextView viewName;
		ImageView imageCover;
		ImageView imageUsed;
		ProgressBar barPause;
		ProgressBar barDownloading;
		TextView pricetxt;
	}
	
	class GridLocalWallpaperAdapter extends BaseAdapter
	{
		
		private Context mContext;
		private String currentWallpaper;;
		private List<WallpaperInformation> localList = new ArrayList<WallpaperInformation>();
		private Bitmap imgDefaultThumb;
		private DownModule downThumb;
		private Set<String> packageNameSet = new HashSet<String>();
		private Set<ImageView> recycle = new HashSet<ImageView>();
		
		public GridLocalWallpaperAdapter(
				Context context ,
				DownModule module )
		{
			mContext = context;
			downThumb = module;
			imgDefaultThumb = ( (BitmapDrawable)mContext.getResources().getDrawable( R.drawable.default_img_large ) ).getBitmap();
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( mContext );
			currentWallpaper = Assets.getWallpaper( context , "currentWallpaper" );
			// currentWallpaper = pref.getString("currentWallpaper", "default");
			if( currentWallpaper == null || currentWallpaper.trim().length() == 0 )
			{
				currentWallpaper = "default";
			}
			new Thread( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					getWallpapersInfo();
					queryPackage();
					( (Activity)mContext ).runOnUiThread( new Runnable() {
						
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							recycle.clear();
							notifyDataSetChanged();
						}
					} );
				}
			} ).start();
		}
		
		public void onDestory()
		{
			for( WallpaperInformation info : localList )
			{
				info.disposeThumb();
				info = null;
			}
			for( Bitmap bmp : localBmp )
			{
				bmp.recycle();
				bmp = null;
			}
			if( imgDefaultThumb != null && !imgDefaultThumb.isRecycled() )
			{
				imgDefaultThumb.recycle();
			}
		}
		
		private void getWallpapersInfo()
		{
			List<ResolveInfo> temp;
			galleryPkg = FunctionConfig.getGalleryPkg().split( ";" );
			Intent pickWallpaper = new Intent( Intent.ACTION_SET_WALLPAPER );
			temp = mContext.getPackageManager().queryIntentActivities( pickWallpaper , PackageManager.GET_ACTIVITIES );
			mResolveInfoList.clear();
			packageNameSet.clear();
			ResolveInfo gallertResolveInfo = null;
			ResolveInfo LivepaperResolveInfo = null;
			for( ResolveInfo info : temp )
			{
				String packagename = info.activityInfo.packageName;
				String name = info.activityInfo.name;
				if( !isfindGallery )
				{
					for( int i = 0 ; i < galleryPkg.length ; i++ )
					{
						if( galleryPkg[i].equals( packagename ) )
						{
							GALLERY = packagename;
							//							mResolveInfoList.add( info );
							gallertResolveInfo = info;
							isfindGallery = true;
							break;
						}
					}
				}
				if( !isfindLivepaper )
				{
					if( LIVEPICKER.equals( packagename ) || ( packagename.equals( HTC_ONE_PACK_NAME ) && name.equals( HTC_ONE_RESOLVE_NAME ) ) )
					{
						//						mResolveInfoList.add( info );
						LivepaperResolveInfo = info;
						isfindLivepaper = true;
					}
				}
			}
			if( gallertResolveInfo != null )
			{
				mResolveInfoList.add( gallertResolveInfo );
			}
			else
			{
				if( temp != null && temp.size() > 0 )
				{
					if( !isfindGallery )
					{
						mResolveInfoList.add( temp.get( 0 ) );
					}
				}
			}
			if( LivepaperResolveInfo != null )
			{
				mResolveInfoList.add( LivepaperResolveInfo );
			}
			else
			{
				if( temp != null && temp.size() > 0 )
				{
					if( !isfindLivepaper )
					{
						mResolveInfoList.add( temp.get( 0 ) );
					}
				}
			}
			//			//Jone add start
			//			if( temp != null && temp.size() > 0 )
			//			{
			//				if( !isfindGallery )
			//				{
			//					mResolveInfoList.add( temp.get( 0 ) );
			//				}
			//				if( !isfindLivepaper )
			//				{
			//					mResolveInfoList.add( temp.get( 0 ) );
			//				}
			//			}
			//			//Jone add end
			temp = mContext.getPackageManager().queryIntentActivities( new Intent( "com.coco.action.wallpaper" ) , PackageManager.GET_ACTIVITIES );
			for( ResolveInfo info : temp )
			{
				String packagename = info.activityInfo.packageName;
				String cls = info.activityInfo.name;
				if( FunctionConfig.isStatictoIcon() )
				{
					if( mContext.getPackageName().equals( packagename ) && "com.coco.wallpaper.wallpaperbox.WallpaperPreviewActivity".equals( cls ) )
					{
						mResolveInfoList.add( info );
					}
				}
				if( FunctionConfig.isLockwallpaperShow() )
				{
					if( mContext.getPackageName().equals( packagename ) && "com.coco.wallpaper.wallpaperbox.LockWallpaperPreview".equals( cls ) )
					{
						mResolveInfoList.add( info );
					}
				}
			}
			if( FunctionConfig.isEnable_topwise_style() )
			{
				Intent it = new Intent();
				it.setPackage( "topwise.shark.wallpaperSet" );
				it.addCategory( "android.intent.category.LAUNCHER" );
				temp = mContext.getPackageManager().queryIntentActivities( it , PackageManager.GET_ACTIVITIES );
				for( ResolveInfo info : temp )
				{
					mResolveInfoList.add( info );
				}
			}
			if( !FunctionConfig.isStatictoIcon() )
			{
				if( FunctionConfig.getWallpapers_from_other_apk() != null )
				{
					try
					{
						Context remountContext = mContext.createPackageContext( FunctionConfig.getWallpapers_from_other_apk() , Context.CONTEXT_IGNORE_SECURITY );
						Resources res = remountContext.getResources();
						for( int i = 1 ; ; i++ )
						{
							try
							{
								int drawable = res.getIdentifier( "wallpaper_" + ( i < 10 ? "0" + i : i ) + "_small" , "drawable" , FunctionConfig.getWallpapers_from_other_apk() );
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
						return;
					}
					catch( NameNotFoundException e )
					{
						Log.e( "tabwallpaper" , "createPackageContext exception: " + e );
					}
					return;
				}
				infos.findWallpapers( mThumbs );
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
							Context remoteContext = mContext.createPackageContext( ThemesDB.LAUNCHER_PACKAGENAME , Context.CONTEXT_IGNORE_SECURITY );
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
		}
		
		private void queryPackage()
		{
			if( !FunctionConfig.isStatictoIcon() )
			{
				packageNameSet.clear();
				for( ResolveInfo info : mResolveInfoList )
				{
					packageNameSet.add( info.activityInfo.packageName + " local" );
				}
				for( String str : mThumbs )
				{
					packageNameSet.add( str + " local" );
				}
				WallpaperService themeSv = new WallpaperService( mContext );
				List<WallpaperInformation> installList = themeSv.queryDownloadList();
				localList.clear();
				Log.v( "tagWallpaper" , "installList.size() = " + installList.size() );
				for( WallpaperInformation info : installList )
				{
					info.setThumbImage( mContext , info.getPackageName() , info.getClassName() );
					localList.add( info );
					packageNameSet.add( info.getPackageName() );
				}
				Log.v( "tagWallpaper" , Thread.currentThread().getId() + "locallIST.SIZE-- = " + localList.size() );
			}
		}
		
		public synchronized void reloadPackage()
		{
			if( !FunctionConfig.isStatictoIcon() )
			{
				new Thread( new Runnable() {
					
					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						queryPackage();
						( (Activity)mContext ).runOnUiThread( new Runnable() {
							
							@Override
							public void run()
							{
								recycle.clear();
								notifyDataSetChanged();
							}
						} );
					}
				} ).start();
			}
		}
		
		public void updateDownloadSize(
				String pkgName ,
				long downSize ,
				long totalSize )
		{
			int findIndex = findPackageIndex( pkgName );
			if( findIndex < 0 )
			{
				return;
			}
			WallpaperInformation info = localList.get( findIndex );
			info.setDownloadSize( downSize );
			info.setTotalSize( totalSize );
			notifyDataSetChanged();
		}
		
		public Set<String> getPackageNameSet()
		{
			return packageNameSet;
		}
		
		public boolean containPackage(
				String packageName )
		{
			return findPackageIndex( packageName ) >= 0;
		}
		
		private int findPackageIndex(
				String packageName )
		{
			int i = 0;
			for( i = 0 ; i < localList.size() ; i++ )
			{
				if( packageName.equals( localList.get( i ).getPackageName() ) )
				{
					return i;
				}
			}
			return -1;
		}
		
		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			if( FunctionConfig.isStatictoIcon() )
			{
				return mResolveInfoList.size();
			}
			return mResolveInfoList.size() + localBmp.size() + localList.size();
		}
		
		@Override
		public Object getItem(
				int position )
		{
			// TODO Auto-generated method stub
			if( position < mResolveInfoList.size() + localBmp.size() )
			{
				return position;
			}
			if( localList.size() == 0 )
			{
				return null;
			}
			return localList.get( position - mResolveInfoList.size() - localBmp.size() );
		}
		
		@Override
		public long getItemId(
				int position )
		{
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public void notifyDataSetChanged()
		{
			// TODO Auto-generated method stub
			// SharedPreferences pref = PreferenceManager
			// .getDefaultSharedPreferences(mContext);
			// currentWallpaper = pref.getString("currentWallpaper", "default");
			currentWallpaper = Assets.getWallpaper( mContext , "currentWallpaper" );
			// currentWallpaper = pref.getString("currentWallpaper", "default");
			if( currentWallpaper == null || currentWallpaper.trim().length() == 0 )
			{
				currentWallpaper = "default";
			}
			super.notifyDataSetChanged();
		}
		
		@Override
		public View getView(
				int position ,
				View convertView ,
				ViewGroup parent )
		{
			// TODO Auto-generated method stub
			if( convertView == null )
			{
				convertView = LayoutInflater.from( mContext ).inflate( R.layout.main_font_item , null );
			}
			ImageView viewThumb = (ImageView)convertView.findViewById( R.id.imageThumb );
			ImageView viewIcon = (ImageView)convertView.findViewById( R.id.imageIcon );
			TextView viewName = (TextView)convertView.findViewById( R.id.textAppName );
			if( position < mResolveInfoList.size() )
			{
				ResolveInfo resolve = mResolveInfoList.get( position );
				if( GALLERY.equals( resolve.activityInfo.packageName ) || !isfindGallery && position == 0 )
				{
					if( FunctionConfig.isEnable_tophard_style() )
					{
						viewIcon.setImageResource( R.drawable.gallery_scene );
					}
					else
					{
						viewIcon.setImageResource( R.drawable.gallery_normal );
					}
				}
				else if( ( !isfindLivepaper && position == 1 ) || LIVEPICKER.equals( resolve.activityInfo.packageName ) || resolve.activityInfo.name.equals( HTC_ONE_RESOLVE_NAME ) )
				{
					if( FunctionConfig.isEnable_tophard_style() )
					{
						viewIcon.setImageResource( R.drawable.livewallpaper_scene );
					}
					else
					{
						viewIcon.setImageResource( R.drawable.livewallpaper_normal );
					}
				}
				else if( "com.coco.wallpaper.wallpaperbox.WallpaperPreviewActivity".equals( resolve.activityInfo.name ) )
				{
					viewIcon.setImageResource( R.drawable.staticwallpaper_scene );
				}
				else if( "com.coco.wallpaper.wallpaperbox.LockWallpaperPreview".equals( resolve.activityInfo.name ) )
				{
					viewIcon.setImageResource( R.drawable.lockwallpaper_scene );
				}
				else if( "topwise.shark.wallpaperSet".equals( resolve.activityInfo.packageName ) )
				{
					viewIcon.setImageResource( R.drawable.wallpaper_more );
				}
				viewIcon.setVisibility( View.VISIBLE );
				viewThumb.setVisibility( View.INVISIBLE );
				// viewThumb.setBackgroundDrawable(null);
				// viewThumb.setPadding(0, 0, 0, 0);
				// viewThumb.setLayoutParams(new
				// RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				// LayoutParams.WRAP_CONTENT));
				viewName.setText( resolve.loadLabel( mContext.getPackageManager() ).toString() );
				convertView.findViewById( R.id.imageUsed ).setVisibility( View.INVISIBLE );
			}
			else if( position >= mResolveInfoList.size() && position < ( localBmp.size() + mResolveInfoList.size() ) )
			{
				Bitmap bmp = localBmp.get( position - mResolveInfoList.size() );
				if( bmp == null )
				{
					viewThumb.setImageBitmap( imgDefaultThumb );
				}
				else
				{
					viewThumb.setImageBitmap( bmp );
				}
				Drawable thumbDrawable = viewThumb.getDrawable();
				if( thumbDrawable != null )
				{
					thumbDrawable.setDither( true );
				}
				if( mThumbs.get( position - mResolveInfoList.size() ).replace( "_small" , "" ).equals( currentWallpaper ) || ( position - mResolveInfoList.size() == 0 && currentWallpaper
						.equals( "default" ) ) )
				{
					convertView.findViewById( R.id.imageUsed ).setVisibility( View.VISIBLE );
				}
				else
				{
					convertView.findViewById( R.id.imageUsed ).setVisibility( View.INVISIBLE );
				}
				viewIcon.setVisibility( View.INVISIBLE );
				viewThumb.setVisibility( View.VISIBLE );
			}
			else
			{
				if( getItem( position ) == null )
				{
					return convertView;
				}
				WallpaperInformation Info = (WallpaperInformation)getItem( position );
				if( Info.isNeedLoadDetail() )
				{
					Bitmap imgThumb = Info.getThumbImage();
					if( imgThumb == null )
					{
						Info.loadDetail( mContext );
					}
					if( Info.getThumbImage() == null )
					{
						downThumb.downloadThumb( Info.getPackageName() , DownloadList.Wallpaper_Type );
					}
				}
				Bitmap imgThumb = Info.getThumbImage();
				//				recycle.add( viewThumb );
				//				Tools.Recyclebitmap( imgDefaultThumb , imgThumb , viewThumb , recycle );
				if( imgThumb == null )
				{
					imgThumb = imgDefaultThumb;
				}
				viewThumb.setImageBitmap( imgThumb );
				if( ( (WallpaperInformation)getItem( position ) ).getPackageName().equals( currentWallpaper ) )
				{
					convertView.findViewById( R.id.imageUsed ).setVisibility( View.VISIBLE );
				}
				else
				{
					convertView.findViewById( R.id.imageUsed ).setVisibility( View.INVISIBLE );
				}
				viewIcon.setVisibility( View.INVISIBLE );
				viewThumb.setVisibility( View.VISIBLE );
			}
			convertView.findViewById( R.id.imageCover ).setVisibility( View.GONE );
			convertView.findViewById( R.id.barPause ).setVisibility( View.GONE );
			convertView.findViewById( R.id.barDownloading ).setVisibility( View.GONE );
			viewName.setVisibility( View.GONE );
			return convertView;
		}
	}
	
	class GridLiveWallpaperAdapter extends BaseAdapter
	{
		
		private List<WallpaperInformation> appList = new ArrayList<WallpaperInformation>();
		private Context context;
		private DownModule downThumb;
		private Bitmap imgDefaultThumb;
		private boolean mShowProgress = true;
		private PageTask pageTask = null;
		private Set<ImageView> recycle = new HashSet<ImageView>();;
		
		public GridLiveWallpaperAdapter(
				Context cxt )
		{
			context = cxt;
			downThumb = DownModule.getInstance( context );
			imgDefaultThumb = ( (BitmapDrawable)cxt.getResources().getDrawable( R.drawable.default_img_large ) ).getBitmap();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
			int size = preferences.getInt( "list-" + DownloadList.LiveWallpaper_Type , 0 );
			if( size == 0 )
			{
				if( !com.coco.theme.themebox.StaticClass.isAllowDownload( context ) )
				{
					mShowProgress = false;
				}
				else
				{
					mShowProgress = true;
				}
			}
			else
			{
				mShowProgress = false;
			}
			if( pageTask != null && pageTask.getStatus() != PageTask.Status.FINISHED )
			{
				pageTask.cancel( true );
			}
			pageTask = (PageTask)new PageTask().execute();
		}
		
		public void onDestory()
		{
			for( WallpaperInformation info : appList )
			{
				info.disposeThumb();
				info = null;
			}
			if( imgDefaultThumb != null && !imgDefaultThumb.isRecycled() )
			{
				imgDefaultThumb.recycle();
			}
		}
		
		public boolean showProgress()
		{
			return mShowProgress;
		}
		
		public void setShowProgress(
				boolean isShow )
		{
			mShowProgress = isShow;
		}
		
		public void reloadPackage()
		{
			if( pageTask != null && pageTask.getStatus() != PageTask.Status.FINISHED )
			{
				pageTask.cancel( true );
			}
			pageTask = (PageTask)new PageTask().execute();
		}
		
		public List<WallpaperInformation> queryPackage(
				Set<String> pkgNameSet )
		{
			List<WallpaperInformation> appList = new ArrayList<WallpaperInformation>();
			LiveWallpaperService service = new LiveWallpaperService( context );
			List<WallpaperInformation> hotList = service.queryShowList();
			if( hotList.size() == 0 )
			{
				if( !com.coco.theme.themebox.StaticClass.isAllowDownload( context ) )
				{
					mShowProgress = false;
				}
				else
				{
					mShowProgress = true;
				}
			}
			else
			{
				mShowProgress = false;
			}
			for( WallpaperInformation item : hotList )
			{
				if( item.getPackageName().startsWith( "com.vlife.coco.wallpaper" ) || !pkgNameSet.contains( item.getPackageName() ) )
				{
					appList.add( item );
				}
			}
			return appList;
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
		
		public void updateThumb(
				final String pkgName )
		{
			new Thread() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					int findIndex = findPackageIndex( pkgName );
					if( findIndex < 0 )
					{
						return;
					}
					WallpaperInformation info = appList.get( findIndex );
					info.reloadThumb();
					( (Activity)context ).runOnUiThread( new Runnable() {
						
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							notifyDataSetChanged();
						}
					} );
				}
			}.start();
		}
		
		public void updateDownloadSize(
				String pkgName ,
				long downSize ,
				long totalSize )
		{
			int findIndex = findPackageIndex( pkgName );
			if( findIndex < 0 )
			{
				return;
			}
			WallpaperInformation info = appList.get( findIndex );
			info.setDownloadSize( downSize );
			info.setTotalSize( totalSize );
			notifyDataSetChanged();
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
		
		public DownModule getDownModule()
		{
			return downThumb;
		}
		
		@Override
		public View getView(
				int position ,
				View convertView ,
				ViewGroup parent )
		{
			View retView = convertView;
			ViewHolder viewHolder = null;
			if( retView != null )
			{
				// Log.e("test", "convertView!=null");
				viewHolder = (ViewHolder)convertView.getTag();
			}
			else
			{
				retView = LayoutInflater.from( mContext ).inflate( R.layout.main_font_item , null );
				viewHolder = new ViewHolder();
				viewHolder.viewName = (TextView)retView.findViewById( R.id.textAppName );
				viewHolder.viewThumb = (ImageView)retView.findViewById( R.id.imageThumb );
				viewHolder.imageCover = (ImageView)retView.findViewById( R.id.imageCover );
				viewHolder.imageUsed = (ImageView)retView.findViewById( R.id.imageUsed );
				viewHolder.barPause = (ProgressBar)retView.findViewById( R.id.barPause );
				viewHolder.barDownloading = (ProgressBar)retView.findViewById( R.id.barDownloading );
				viewHolder.pricetxt = (TextView)retView.findViewById( R.id.price );
				viewHolder.imageUsed.setVisibility( View.INVISIBLE );
			}
			recycle.add( viewHolder.viewThumb );
			Recyclebitmap( viewHolder.viewThumb );
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
			viewHolder.viewThumb.setImageBitmap( imgThumb );
			if( FunctionConfig.isPriceVisible() )
			{
				int price = info.getPrice();
				if( info.getPrice() > 0 && !info.isDownloadedFinish() )
				{// 下载完成后，热门推荐中不显示价格
					viewHolder.pricetxt.setVisibility( View.VISIBLE );
					boolean ispay = Tools.isContentPurchased( context , DownloadList.LiveWallpaper_Type , info.getPackageName() );
					if( ispay )
					{
						viewHolder.pricetxt.setBackgroundResource( R.drawable.buyed_bg );
						viewHolder.pricetxt.setText( R.string.has_bought );
					}
					else
					{
						viewHolder.pricetxt.setBackgroundResource( R.drawable.price_bg );
						viewHolder.pricetxt.setText( "￥：" + price / 100 );
					}
				}
				else
				{
					viewHolder.pricetxt.setVisibility( View.GONE );
				}
			}
			if( !FunctionConfig.isShowHotWallpaper() && FunctionConfig.isLiveWallpaperShow() )
			{
				retView.findViewById( R.id.live_sign ).setVisibility( View.VISIBLE );
			}
			if( info.getDownloadStatus() == DownloadStatus.StatusInit || info.getDownloadStatus() == DownloadStatus.StatusFinish )
			{
				viewHolder.imageCover.setVisibility( View.INVISIBLE );
				viewHolder.barPause.setVisibility( View.INVISIBLE );
				viewHolder.barDownloading.setVisibility( View.INVISIBLE );
			}
			else
			{
				viewHolder.imageCover.setVisibility( View.VISIBLE );
				if( info.getDownloadStatus() == DownloadStatus.StatusDownloading )
				{
					viewHolder.barDownloading.setVisibility( View.VISIBLE );
					viewHolder.barPause.setVisibility( View.INVISIBLE );
					viewHolder.barDownloading.setProgress( info.getDownloadPercent() );
				}
				else
				{
					viewHolder.barDownloading.setVisibility( View.INVISIBLE );
					viewHolder.barPause.setVisibility( View.VISIBLE );
					viewHolder.barPause.setProgress( info.getDownloadPercent() );
				}
			}
			viewHolder.viewName.setVisibility( View.GONE );
			retView.setTag( viewHolder );
			return retView;
		}
		
		private void Recyclebitmap(
				ImageView view )
		{
			boolean isrecycle = true;
			Bitmap bmp = Tools.recycleImageBitmap( view );
			if( bmp == null || bmp.isRecycled() || bmp == imgDefaultThumb )
			{
				return;
			}
			for( ImageView v : recycle )
			{
				if( v == view )
				{
					continue;
				}
				Bitmap temp = Tools.recycleImageBitmap( v );
				if( temp == bmp )
				{
					isrecycle = false;
					break;
				}
			}
			if( isrecycle )
			{
				bmp.recycle();
			}
		}
		
		@Override
		public void notifyDataSetChanged()
		{
			// TODO Auto-generated method stub
			recycle.clear();
			super.notifyDataSetChanged();
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
				for( WallpaperInformation info : appList )
				{
					info.disposeThumb();
					info = null;
				}
				appList.clear();
				appList.addAll( result );
				notifyDataSetChanged();
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
				LiveWallpaperService lws = new LiveWallpaperService( mContext );
				List<WallpaperInformation> installList = lws.queryInstallList();
				Set<String> packageNameSet = new HashSet<String>();
				for( WallpaperInformation info : installList )
				{
					packageNameSet.add( info.getPackageName() );
				}
				return queryPackage( packageNameSet );
			}
		}
	}
	
	@Override
	public void onFooterRefresh(
			PullToRefreshView view )
	{
		Log.v( "PullToRefreshView" , "tablock_onFooterRefresh" );
		if( gridPager.getCurrentItem() == INDEX_HOT )
		{
			if( IsHaveInternet( mContext ) )
			{
				footerRefresh = true;
				downModule.downloadList();
				handler.postDelayed( new Runnable() {
					
					@Override
					public void run()
					{
						downModule.stopDownlist();
						if( footerRefresh )
						{
							mPullToRefreshView.onFooterRefreshComplete();
							footerRefresh = false;
							if( com.coco.theme.themebox.util.FunctionConfig.isPromptVisible() )
							{
								Toast.makeText( mContext , R.string.internet_unusual , Toast.LENGTH_SHORT ).show();
							}
						}
						if( headerRefresh )
						{
							mPullToRefreshView.onHeaderRefreshComplete();
							headerRefresh = false;
							if( com.coco.theme.themebox.util.FunctionConfig.isPromptVisible() )
							{
								Toast.makeText( mContext , R.string.internet_unusual , Toast.LENGTH_SHORT ).show();
							}
						}
					}
				} , 1000 * 30 );
			}
			else
			{
				Toast.makeText( mContext , R.string.internet_err , Toast.LENGTH_SHORT ).show();
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}
	}
	
	@Override
	public void onHeaderRefresh(
			PullToRefreshView view )
	{
		Log.v( "PullToRefreshView" , "tablock_onFooterRefresh" );
		if( gridPager.getCurrentItem() == INDEX_HOT )
		{
			Log.v( "onHeaderRefresh" , "**************" );
			if( IsHaveInternet( mContext ) )
			{
				headerRefresh = true;
				downModule.downloadList();
				handler.postDelayed( new Runnable() {
					
					@Override
					public void run()
					{
						Log.v( "onHeaderRefresh" , "Run footerRefresh = " + footerRefresh + " headerRefresh = " + headerRefresh );
						downModule.stopDownlist();
						if( footerRefresh )
						{
							mPullToRefreshView.onFooterRefreshComplete();
							footerRefresh = false;
							if( com.coco.theme.themebox.util.FunctionConfig.isPromptVisible() )
							{
								Toast.makeText( mContext , R.string.internet_unusual , Toast.LENGTH_SHORT ).show();
							}
						}
						if( headerRefresh )
						{
							mPullToRefreshView.onHeaderRefreshComplete();
							headerRefresh = false;
							if( com.coco.theme.themebox.util.FunctionConfig.isPromptVisible() )
							{
								Toast.makeText( mContext , R.string.internet_unusual , Toast.LENGTH_SHORT ).show();
							}
						}
					}
				} , 1000 * 30 );
			}
			else
			{
				Toast.makeText( mContext , R.string.internet_err , Toast.LENGTH_SHORT ).show();
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}
	}
	
	/**
	 * ViewPager适配�?
	 */
	private static class GridPagerAdapter extends PagerAdapter
	{
		
		private final String LOG_TAG = "GridPagerAdapter";
		private GridView gridLocal;
		private GridView gridHot;
		private View hotView;
		private View viewDownloading = null;
		private BaseAdapter hotAdapter;
		
		public GridPagerAdapter(
				GridView local ,
				View view )
		{
			gridLocal = local;
			hotView = view;
		}
		
		public GridPagerAdapter(
				GridView local )
		{
			gridLocal = local;
		}
		
		public void setGridView(
				GridView hot )
		{
			gridHot = hot;
			hotAdapter = (BaseAdapter)gridHot.getAdapter();
		}
		
		@Override
		public void destroyItem(
				ViewGroup container ,
				int position ,
				Object object )
		{
			Log.d( LOG_TAG , "destroyItem,pos" + position );
			if( viewDownloading != null && isViewFromObject( viewDownloading , object ) )
			{
				container.removeView( viewDownloading );
				viewDownloading = null;
			}
		}
		
		@Override
		public int getItemPosition(
				Object object )
		{
			if( viewDownloading != null && isViewFromObject( viewDownloading , object ) && ( ( hotAdapter instanceof GridHotWallpaperAdapter && !( (GridHotWallpaperAdapter)hotAdapter ).showProgress() ) || ( hotAdapter instanceof GridLiveWallpaperAdapter && !( (GridLiveWallpaperAdapter)hotAdapter )
					.showProgress() ) ) )
			{
				return PagerAdapter.POSITION_NONE;
			}
			return PagerAdapter.POSITION_UNCHANGED;
		}
		
		@Override
		public int getCount()
		{
			if( !com.coco.theme.themebox.util.FunctionConfig.isShowHotWallpaper() && !FunctionConfig.isLiveWallpaperShow() )
			{
				return 1;
			}
			return 2;
		}
		
		@Override
		public Object instantiateItem(
				ViewGroup container ,
				int position )
		{
			Log.d( LOG_TAG , "instantiateItem,pos=" + position );
			if( position == 0 )
			{
				container.addView( gridLocal );
				return gridLocal;
			}
			if( com.coco.theme.themebox.util.FunctionConfig.isShowHotWallpaper() || FunctionConfig.isLiveWallpaperShow() )
			{
				if( ( hotAdapter instanceof GridHotWallpaperAdapter && ( (GridHotWallpaperAdapter)hotAdapter ).showProgress() ) || ( hotAdapter instanceof GridLiveWallpaperAdapter && ( (GridLiveWallpaperAdapter)hotAdapter )
						.showProgress() ) )
				{
					viewDownloading = View.inflate( container.getContext() , R.layout.grid_item_downloading , null );
					if( interneterr )
					{
						viewDownloading.setVisibility( View.GONE );
					}
					else
					{
						viewDownloading.setVisibility( View.VISIBLE );
					}
					container.addView( viewDownloading );
					return viewDownloading;
				}
				// ((ViewPager) container).addView(gridHot);
				( (ViewPager)container ).addView( hotView );
				return hotView;
			}
			return gridLocal;
		}
		
		@Override
		public boolean isViewFromObject(
				View view ,
				Object object )
		{
			return view == ( object );
		}
		
		@Override
		public void restoreState(
				Parcelable state ,
				ClassLoader loader )
		{
		}
		
		@Override
		public Parcelable saveState()
		{
			return null;
		}
	}
}
