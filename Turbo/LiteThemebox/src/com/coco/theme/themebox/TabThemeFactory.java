package com.coco.theme.themebox;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.Toast;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.PullToRefreshView.OnFooterRefreshListener;
import com.coco.theme.themebox.PullToRefreshView.OnHeaderRefreshListener;
import com.coco.theme.themebox.preview.ThemePreviewHotActivity;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Log;
import com.iLoong.base.themebox.R;


public class TabThemeFactory implements TabHost.TabContentFactory , OnHeaderRefreshListener , OnFooterRefreshListener
{
	
	private Context mContext;
	private GridView themeGridViewLocal;
	private GridView themeGridViewHot;
	private ThemeGridLocalAdapter themeLocalAdapter;
	private ThemeGridHotAdapter themeHotAdapter;
	private ViewPager themeGridPager;
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
	private boolean ifselectHot=false;
	public TabThemeFactory(
			Context context ,
			DownModule module,boolean ifselectHotTheme )
	{
		mContext = context;
		downModule = module;
		ifselectHot=ifselectHotTheme;
	}
	
	public void onDestroy()
	{
		if( packageReceiver != null )
			mContext.unregisterReceiver( packageReceiver );
		if( themeLocalAdapter != null )
		{
			themeLocalAdapter.onDestory();
		}
		if( themeHotAdapter != null )
		{
			themeHotAdapter.onDestory();
		}
	}
	
	@Override
	public View createTabContent(
			String tag )
	{
		long preTime = System.currentTimeMillis();
		View result = View.inflate( mContext , R.layout.theme_main , null );
		View containHot = result.findViewById( R.id.containHot );
		// 本地主题
		themeGridViewLocal = (GridView)( View.inflate( mContext , R.layout.lock_grid , null ) );
		themeLocalAdapter = new ThemeGridLocalAdapter( mContext , downModule );
		themeGridViewLocal.setAdapter( themeLocalAdapter );
		themeGridViewLocal.setOnItemClickListener( new OnItemClickListener() {
			
			@Override
			public void onItemClick(
					AdapterView<?> parent ,
					View v ,
					int position ,
					long id )
			{
				ThemeInformation themeInfo = (ThemeInformation)parent.getItemAtPosition( position );
				Intent i = new Intent();
				i.putExtra( StaticClass.EXTRA_PACKAGE_NAME , themeInfo.getPackageName() );
				Log.v( "************" , "000000000000packname = " + themeInfo.getPackageName() );
				i.putExtra( StaticClass.EXTRA_CLASS_NAME , themeInfo.getClassName() );
				i.setClass( mContext , ThemePreviewHotActivity.class );
				// i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				mContext.startActivity( i );
			}
		} );
		if( com.coco.theme.themebox.util.FunctionConfig.isHotThemeVisible() )
		{
			// 热门主题
			hotView = View.inflate( mContext , R.layout.lock_grid_hot , null );
			mPullToRefreshView = (PullToRefreshView)hotView.findViewById( R.id.main_pull_refresh_view );
			mPullToRefreshView.setOnHeaderRefreshListener( this );
			mPullToRefreshView.setOnFooterRefreshListener( this );
			themeGridViewHot = (GridView)hotView.findViewById( R.id.gridViewLock );
			themeHotAdapter = new ThemeGridHotAdapter( mContext , downModule );
			// themeHotAdapter.queryPackage(themeLocalAdapter.getPackageNameSet());
			themeGridViewHot.setAdapter( themeHotAdapter );
			// ViewPager
			themeGridPager = (ViewPager)result.findViewById( R.id.themeGridPager );
			themePagerAdapter = new GridPagerAdapter( themeGridViewLocal , hotView );
			themePagerAdapter.setGridView( themeGridViewHot );
			themeGridPager.setAdapter( themePagerAdapter );
			if( themeHotAdapter.showProgress() || downModule.isRefreshList() )
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
									if( themeGridPager.getCurrentItem() == INDEX_HOT )
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
					if( themeGridPager.getCurrentItem() == INDEX_HOT )
						Toast.makeText( mContext , R.string.internet_err , Toast.LENGTH_SHORT ).show();
					interneterr = true;
					if( themePagerAdapter != null && themePagerAdapter.viewDownloading != null )
					{
						themePagerAdapter.viewDownloading.setVisibility( View.INVISIBLE );
					}
				}
			}
			themeGridViewHot.setOnItemClickListener( new OnItemClickListener() {
				
				@Override
				public void onItemClick(
						AdapterView<?> parent ,
						View v ,
						int position ,
						long id )
				{
					ThemeInformation infor = (ThemeInformation)parent.getItemAtPosition( position );
					Intent i = new Intent();
					i.putExtra( StaticClass.EXTRA_PACKAGE_NAME , infor.getPackageName() );
					i.putExtra( StaticClass.EXTRA_CLASS_NAME , infor.getClassName() );
					i.setClass( mContext , ThemePreviewHotActivity.class );
					// i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					mContext.startActivity( i );
				}
			} );
			themeGridPager.setOverScrollMode( View.OVER_SCROLL_NEVER );
			Log.v( "time" , "lockcreate = " + ( System.currentTimeMillis() - preTime ) + "" );
			// 热门按钮
			final RadioButton themeHotButton = (RadioButton)result.findViewById( R.id.btnHotTheme );
			final RadioButton themeLocalButton = (RadioButton)result.findViewById( R.id.btnLocalTheme );
			
			if(ifselectHot){
				themeHotButton.performClick();
				themeGridPager.setCurrentItem( INDEX_HOT , true );
			}
			
			themeGridPager.setOnPageChangeListener( new OnPageChangeListener() {
				
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
						themeLocalButton.toggle();
					}
					else if( index == INDEX_HOT )
					{
						if( StaticClass.isAllowDownload( mContext ) )
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
								Log.v( "Tab" , "count = " + themeHotAdapter.getCount() );
								if( themeHotAdapter.getCount() == 0 )
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
						}
						else
						{
							Toast.makeText( mContext , R.string.sdcard_not_available , Toast.LENGTH_SHORT ).show();
						}
						themeHotButton.toggle();
					}
				}
			} );
			// 热门
			themeHotButton.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(
						View arg0 )
				{
					themeGridPager.setCurrentItem( INDEX_HOT , true );
				}
			} );
			// 本地按钮
			themeLocalButton.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(
						View arg0 )
				{
					themeGridPager.setCurrentItem( INDEX_LOCAL , true );
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
			themeGridPager = (ViewPager)result.findViewById( R.id.themeGridPager );
			themePagerAdapter = new GridPagerAdapter( themeGridViewLocal );
			themeGridPager.setAdapter( themePagerAdapter );
			themeGridPager.setOverScrollMode( View.OVER_SCROLL_NEVER );
		}
		packageReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(
					Context context ,
					Intent intent )
			{
				String actionName = intent.getAction();
				if( Intent.ACTION_PACKAGE_REMOVED.equals( actionName ) )
				{
					String packageName = intent.getData().getSchemeSpecificPart();
					if( themeLocalAdapter.containPackage( packageName ) )
					{
						themeLocalAdapter.reloadPackage();
						if( com.coco.theme.themebox.util.FunctionConfig.isHotThemeVisible() )
						{
							themeHotAdapter.reloadPackage();
						}
						// DownloadTab.getInstance(mContext).startUICenterLog(DownloadTab.ACTION_UNINSTALL_LOG,"",packageName);
					}
				}
				else if( Intent.ACTION_PACKAGE_ADDED.equals( actionName ) )
				{
					String packageName = intent.getData().getSchemeSpecificPart();
					if( packageName.equals( StaticClass.LOCKBOX_PACKAGE_NAME ) )
					{
						if( StaticClass.isLockBoxInstalled( context ) )
						{
							( (Activity)( TabThemeFactory.this.mContext ) ).finish();
							return;
						}
					}
					themeLocalAdapter.reloadPackage();
					if( com.coco.theme.themebox.util.FunctionConfig.isHotThemeVisible() )
					{
						themeHotAdapter.reloadPackage();
					}
					// HotService sv = new HotService(mContext);
					// String resid =
					// sv.queryResid(packageName,DownloadTab.Theme_Type);
					// if(resid != null){
					// DownloadTab.getInstance(mContext).startUICenterLog(DownloadTab.ACTION_INSTALL_LOG,resid,packageName);
					// }
				}
				else if( actionName.equals( StaticClass.ACTION_THUMB_CHANGED ) )
				{
					if( com.coco.theme.themebox.util.FunctionConfig.isHotThemeVisible() )
					{
						themeHotAdapter.updateThumb( intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) );
					}
				}
				else if( actionName.equals( StaticClass.ACTION_HOTLIST_CHANGED ) )
				{
					Log.v( "***************" , "222222" + StaticClass.ACTION_HOTLIST_CHANGED );
					if( com.coco.theme.themebox.util.FunctionConfig.isHotThemeVisible() )
					{
						themeHotAdapter.reloadPackage();
						themeHotAdapter.setShowProgress( false );
						themePagerAdapter.notifyDataSetChanged();
					}
					// themePagerAdapter.notifyDataSetChanged();
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
					themeLocalAdapter.reloadPackage();
					if( com.coco.theme.themebox.util.FunctionConfig.isHotThemeVisible() )
					{
						themeHotAdapter.reloadPackage();
					}
				}
				else if( actionName.equals( StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED ) )
				{
					if( com.coco.theme.themebox.util.FunctionConfig.isHotThemeVisible() )
					{
						themeHotAdapter.updateDownloadSize(
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
					it.putExtra( "type" , DownloadList.Theme_Type );
					it.putExtra( "status" , "download" );
					it.setClass( mContext , DownloadApkContentService.class );
					mContext.startService( it );
					if( com.coco.theme.themebox.util.FunctionConfig.isHotThemeVisible() )
					{
						themeHotAdapter.notifyDataSetChanged();
					}
				}
				else if( actionName.equals( StaticClass.ACTION_PAUSE_DOWNLOAD_APK ) )
				{
					String packName = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
					// downModule.stopDownApk(packName);
					Intent it = new Intent();
					it.putExtra( "packageName" , packName );
					it.putExtra( "type" , DownloadList.Theme_Type );
					it.putExtra( "name" , intent.getStringExtra( "apkname" ) );
					it.putExtra( "status" , "pause" );
					it.setClass( mContext , DownloadApkContentService.class );
					mContext.startService( it );
					if( com.coco.theme.themebox.util.FunctionConfig.isHotThemeVisible() )
					{
						themeHotAdapter.notifyDataSetChanged();
					}
					Log.v( "********" , "receive packName = " + packName );
				}
				else if( actionName.equals( StaticClass.ACTION_DEFAULT_THEME_CHANGED ) )
				{
					// new Thread() {
					// @Override
					// public void run() {
					// themeLocalAdapter.reloadPackage();
					// }
					// }.start();
					themeLocalAdapter.reloadCurrent();
				}
			}
		};
		// 注册删除事件
		IntentFilter pkgFilter = new IntentFilter();
		pkgFilter.addAction( Intent.ACTION_PACKAGE_REMOVED );
		pkgFilter.addAction( Intent.ACTION_PACKAGE_ADDED );
		pkgFilter.addDataScheme( "package" );
		mContext.registerReceiver( packageReceiver , pkgFilter );
		// 下载成功
		IntentFilter screenFilter1 = new IntentFilter();
		screenFilter1.addAction( StaticClass.ACTION_START_DOWNLOAD_APK );
		screenFilter1.addAction( StaticClass.ACTION_THUMB_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_HOTLIST_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_PAUSE_DOWNLOAD_APK );
		screenFilter1.addAction( StaticClass.ACTION_DEFAULT_THEME_CHANGED );
		mContext.registerReceiver( packageReceiver , screenFilter1 );
		return result;
	}
	
	private BroadcastReceiver packageReceiver = null;
	
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
		private ThemeGridHotAdapter hotAdapter;
		
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
			hotAdapter = (ThemeGridHotAdapter)gridHot.getAdapter();
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
			if( viewDownloading != null && isViewFromObject( viewDownloading , object ) && !hotAdapter.showProgress() )
			{
				return PagerAdapter.POSITION_NONE;
			}
			return PagerAdapter.POSITION_UNCHANGED;
		}
		
		@Override
		public int getCount()
		{
			if( !com.coco.theme.themebox.util.FunctionConfig.isHotThemeVisible() )
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
			if( com.coco.theme.themebox.util.FunctionConfig.isHotThemeVisible() )
			{
				if( hotAdapter.showProgress() )
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
	
	@Override
	public void onFooterRefresh(
			PullToRefreshView view )
	{
		Log.v( "PullToRefreshView" , "tablock_onFooterRefresh" );
		if( themeGridPager.getCurrentItem() == INDEX_HOT )
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
		if( themeGridPager.getCurrentItem() == INDEX_HOT )
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
}
