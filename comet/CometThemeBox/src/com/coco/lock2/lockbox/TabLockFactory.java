package com.coco.lock2.lockbox;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.coco.lock2.lockbox.database.model.DownloadStatus;
import com.coco.lock2.lockbox.util.DownModule;
import com.coco.lock2.lockbox.util.PathTool;
import com.coco.theme.themebox.PullToRefreshView;
import com.coco.theme.themebox.PullToRefreshView.OnFooterRefreshListener;
import com.coco.theme.themebox.PullToRefreshView.OnHeaderRefreshListener;
import com.coco.theme.themebox.service.ThemesDB;
import com.coco.theme.themebox.util.Log;
import com.iLoong.base.themebox.R;


public class TabLockFactory implements TabHost.TabContentFactory , OnHeaderRefreshListener , OnFooterRefreshListener , OnClickListener
{
	
	private final String LOG_TAG = "TabLockContentFactory";
	private final int INDEX_LOCAL = 0;
	private final int INDEX_ALL = 1;
	private final int INDEX_NEW = 2;
	private final int INDEX_HOT = 3;
	private GridView gridViewLocal;
	private GridView gridViewHot;
	private View hotView; // 热门全部列表界面
	private GridView simpleView;// 简约列表界面
	private GridView classicView;// 经典列表界面
	private GridLocalAdapter localAdapter;
	private GridHotAdapter hotAdapter;
	private GridSimpleAdapter simpleAdapter;
	private GridClassicAdapter classicAdapter;
	private ViewPager gridPager;
	private DownModule downModule;
	private GridPagerAdapter pagerAdapter;
	private Context mContext;
	private PullToRefreshView mPullToRefreshView;
	private boolean footerRefresh = false;
	private boolean headerRefresh = false;
	private boolean listRefresh = false;
	private Handler handler = new Handler();
	private static boolean interneterr = false;
	private LinearLayout fenleiLinearLayout;
	private int Screen_width;
	private int Screen_Hight;
	private String title[] = { "" , "" , "" };
	private LinearLayout linearLayout;
	private ArrayList<TextView> textViews;
	private HorizontalScrollView horizontalScrollView;
	private RadioButton localButton;
	private boolean registerState = false;
	
	public TabLockFactory(
			Context context ,
			DownModule module ,
			int Screen_w ,
			int Screen_h )
	{
		PathTool.makeDirApp();
		mContext = context;
		downModule = module;
		Screen_width = Screen_w;
		Screen_Hight = Screen_h;
		title[0] = context.getString( R.string.fenlei_all );
		title[1] = context.getString( R.string.fenlei_simple );
		title[2] = context.getString( R.string.fenlei_classic );
	}
	
	public void onDestroy()
	{
		if( registerState )
		{
			mContext.unregisterReceiver( packageReceiver );
		}
	}
	
	private View createLockDown()
	{
		View result = View.inflate( mContext , R.layout.lock_download , null );
		Button getThemeBtn = (Button)result.findViewById( R.id.btnGetLock );
		getThemeBtn.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View arg0 )
			{
				if( !StaticClass.isAllowDownloadWithToast( mContext ) )
				{
					return;
				}
				//下载 锁屏盒子 统计
				//				MobclickAgent.onEvent( mContext , "DownLockBox" );
				String fileName = mContext.getResources().getString( R.string.server_download_file_name );
				File file = new File( DownloadLockBoxService.DOWNLOAD_PATH , fileName + ".apk" );
				if( file.exists() )
				{
					Intent intent = new Intent();
					intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
					intent.setAction( android.content.Intent.ACTION_VIEW );
					intent.setDataAndType( Uri.fromFile( file ) , "application/vnd.android.package-archive" );
					mContext.startActivity( intent );
				}
				else
				{
					downloadAPK( mContext , ThemesDB.LAUNCHER_PACKAGENAME , StaticClass.LOCKBOX_PACKAGE_NAME , fileName );
				}
			}
		} );
		return result;
	}
	
	public static void downloadAPK(
			Context context ,
			String srcPackageName ,
			String destPackageName ,
			String fileName )
	{
		Intent intent = new Intent();
		intent.setClass( context , DownloadLockBoxService.class );
		intent.putExtra( DownloadLockBoxService.DOWNLOAD_FILE_NAME , fileName );
		intent.putExtra( DownloadLockBoxService.DOWNLOAD_URL_KEY , DownModule.getApkUrl( context , srcPackageName , destPackageName ) );
		Intent intentGetToDownloadAPKName = new Intent( "com.coco.lock2.lockbox.GetToDownloadAPKName" );
		intentGetToDownloadAPKName.putExtra( "ToDownloadAPKName" , destPackageName );
		context.sendBroadcast( intentGetToDownloadAPKName );
		ComponentName res = context.startService( intent );
	}
	
	public void initTile()
	{
		textViews = new ArrayList<TextView>();
		int H_width = Screen_width / 3;
		int height = 70;
		for( int i = 0 ; i < 3 ; i++ )
		{
			TextView textView = new TextView( mContext );
			textView.setText( title[i] );
			textView.setTextSize( 17 );
			textView.setTextColor( 0xFF4f7510 );
			textView.setWidth( H_width );
			int h = (int)( 50 * (float)( Screen_Hight / 854 ) );
			Log.v( "aa" , "text_width=" + h );
			textView.setHeight( height - 20 );
			if( Screen_Hight > 1000 )
			{
				textView.setHeight( 72 );
			}
			textView.setGravity( Gravity.CENTER );
			textView.setId( i );
			textView.setOnClickListener( this );
			textViews.add( textView );
			linearLayout.addView( textView );
		}
	}
	
	/***
	 * 选中效果
	 */
	public void setSelector(
			int id )
	{
		Log.v( "setSelector" , "id = " + id );
		for( int i = 0 ; i < title.length ; i++ )
		{
			if( id == i )
			{
				Bitmap bitmap = BitmapFactory.decodeResource( mContext.getResources() , R.drawable.fenleianxia );
				if( id > 0 )
				{
					textViews.get( id - 1 ).setBackgroundDrawable( new BitmapDrawable( bitmap ) );
					textViews.get( id - 1 ).setTextColor( 0xFF4e760e );
				}
				if( i > 3 )
				{
					horizontalScrollView.smoothScrollTo( Screen_width , 0 );
				}
				else
				{
					horizontalScrollView.smoothScrollTo( 0 , 0 );
				}
				gridPager.setCurrentItem( i );
			}
			else
			{
				if( i > 0 )
				{
					textViews.get( i - 1 ).setBackgroundDrawable( new BitmapDrawable() );
					textViews.get( i - 1 ).setTextColor( 0xFF4f7510 );
				}
			}
		}
		if( id == 3 )
		{
			Bitmap bitmap = BitmapFactory.decodeResource( mContext.getResources() , R.drawable.fenleianxia );
			textViews.get( id - 1 ).setBackgroundDrawable( new BitmapDrawable( bitmap ) );
			textViews.get( id - 1 ).setTextColor( 0xFF4e760e );
			horizontalScrollView.smoothScrollTo( ( textViews.get( 2 ).getWidth() * ( 2 ) - 180 ) , 0 );
			gridPager.setCurrentItem( 3 );
		}
		else
		{
			textViews.get( 2 ).setBackgroundDrawable( new BitmapDrawable() );
			textViews.get( 2 ).setTextColor( 0xFF4f7510 );
		}
	}
	
	@Override
	public View createTabContent(
			String tag )
	{
		if( !StaticClass.isLockBoxInstalled( mContext ) )
		{
			return createLockDown();
		}
		registerState = true;
		View result = View.inflate( mContext , R.layout.lock_main , null );
		horizontalScrollView = (HorizontalScrollView)result.findViewById( R.id.horizontalScrollView );
		horizontalScrollView.setOverScrollMode( View.OVER_SCROLL_NEVER );
		linearLayout = (LinearLayout)result.findViewById( R.id.ll_main );
		fenleiLinearLayout = (LinearLayout)result.findViewById( R.id.fenleilinearLayout );
		initTile();
		{
			long preTime = System.currentTimeMillis();
			// 本地锁屏
			gridViewLocal = (GridView)( View.inflate( mContext , R.layout.lock_grid , null ) );
			localAdapter = new GridLocalAdapter( mContext , downModule );
			gridViewLocal.setAdapter( localAdapter );
			// 热门锁屏
			hotView = View.inflate( mContext , R.layout.lock_grid_hot , null );
			mPullToRefreshView = (PullToRefreshView)hotView.findViewById( R.id.main_pull_refresh_view );
			mPullToRefreshView.setOnHeaderRefreshListener( this );
			mPullToRefreshView.setOnFooterRefreshListener( this );
			gridViewHot = (GridView)hotView.findViewById( R.id.gridViewLock );
			hotAdapter = new GridHotAdapter( mContext , downModule );
			hotAdapter.queryPackage( localAdapter.getPackageNameSet() );
			gridViewHot.setAdapter( hotAdapter );
			Log.v( "themecreate " , "hotAdapter.showProgress() = " + hotAdapter.showProgress() + "    downModule.isRefreshList() = " + downModule.isRefreshList() );
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
								Toast.makeText( mContext , R.string.internet_unusual , Toast.LENGTH_SHORT ).show();
							}
							if( pagerAdapter != null && pagerAdapter.viewDownloading != null )
							{
								pagerAdapter.viewDownloading.setVisibility( View.INVISIBLE );
							}
						}
					} , 1000 * 30 );
				}
				else
				{
					Toast.makeText( mContext , R.string.internet_err , Toast.LENGTH_SHORT ).show();
					interneterr = true;
					if( pagerAdapter != null && pagerAdapter.viewDownloading != null )
					{
						pagerAdapter.viewDownloading.setVisibility( View.INVISIBLE );
					}
				}
			}
			// 简约解锁
			simpleView = (GridView)( View.inflate( mContext , R.layout.lock_grid , null ) );
			simpleAdapter = new GridSimpleAdapter( mContext , downModule );
			simpleAdapter.queryPackage( localAdapter.getPackageNameSet() );
			simpleView.setAdapter( simpleAdapter );
			// 经典解锁
			classicView = (GridView)( View.inflate( mContext , R.layout.lock_grid , null ) );
			classicAdapter = new GridClassicAdapter( mContext , downModule );
			classicAdapter.queryPackage( localAdapter.getPackageNameSet() );
			classicView.setAdapter( classicAdapter );
			// ViewPager
			gridPager = (ViewPager)result.findViewById( R.id.gridPager );
			pagerAdapter = new GridPagerAdapter( gridViewLocal , hotView , simpleView , classicView );
			pagerAdapter.setGridView( gridViewHot );
			gridPager.setAdapter( pagerAdapter );
			final int apiLevel = Build.VERSION.SDK_INT;
			if( apiLevel >= 9 )
			{
				gridPager.setOverScrollMode( View.OVER_SCROLL_NEVER );
			}
			Log.v( "time" , "lockcreate = " + ( System.currentTimeMillis() - preTime ) + "" );
		}
		// 热门按钮
		final RadioButton hotButton = (RadioButton)result.findViewById( R.id.btnHotLock );
		localButton = (RadioButton)result.findViewById( R.id.btnLocalLock );
		setSelector( 1 );
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
				setSelector( index );
				Log.v( "Tab" , "index  =" + index );
				if( index == INDEX_LOCAL )
				{
					//友盟   Tab页统计 本地
					//					MobclickAgent.onEvent( mContext , "Tab" , "LOCK_LOCAL" );
					fenleiLinearLayout.setVisibility( View.INVISIBLE );
					localButton.toggle();
				}
				else if( index == INDEX_ALL )
				{
					//友盟   Tab页统计 全部
					//					MobclickAgent.onEvent( mContext , "Tab" , "LOCK_ALL" );
					Log.v( "Tab" , "index == INDEX_HOT" );
					fenleiLinearLayout.setVisibility( View.VISIBLE );
					// 无网络
					if( IsHaveInternet( mContext ) == false )
					{
						Toast.makeText( mContext , R.string.internet_err , Toast.LENGTH_SHORT ).show();
						if( pagerAdapter != null && pagerAdapter.viewDownloading != null )
						{
							pagerAdapter.viewDownloading.setVisibility( View.INVISIBLE );
						}
					}
					else
					{
						if( pagerAdapter != null && pagerAdapter.viewDownloading != null )
						{
							pagerAdapter.viewDownloading.setVisibility( View.VISIBLE );
						}
						Log.v( "Tab" , "count = " + hotAdapter.getCount() );
						if( hotAdapter.getCount() == 0 )
						{
							pagerAdapter.notifyDataSetChanged();
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
											Toast.makeText( mContext , R.string.internet_unusual , Toast.LENGTH_SHORT ).show();
										}
										if( pagerAdapter.viewDownloading != null )
										{
											pagerAdapter.viewDownloading.setVisibility( View.INVISIBLE );
										}
									}
								} , 1000 * 30 );
							}
						}
					}
					hotButton.toggle();
				}
				else if( index == INDEX_NEW )
				{
					//友盟   Tab页统计 最新
					//					MobclickAgent.onEvent( mContext , "Tab" , "LOCK_NEW" );
				}
				else if( index == INDEX_HOT )
				{
					//友盟   Tab页统计 热门
					//					MobclickAgent.onEvent( mContext , "Tab" , "LOCK_HOT" );
				}
			}
		} );
		// 热门
		hotButton.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View arg0 )
			{
				gridPager.setCurrentItem( INDEX_ALL , true );
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
		screenFilter1.addAction( StaticClass.ACTION_HOTLIST_NOCHANGED );
		screenFilter1.addAction( StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED );
		screenFilter1.addAction( StaticClass.ACTION_PAUSE_DOWNLOAD_APK );
		screenFilter1.addAction( StaticClass.ACTION_DEFAULT_LOCK_CHANGED );
		mContext.registerReceiver( packageReceiver , screenFilter1 );
		// 监听设置按钮
		ImageButton btnSetting = (ImageButton)result.findViewById( R.id.btnSetting );
		btnSetting.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View arg0 )
			{
				Intent intentSetting = new Intent();
				intentSetting.setClassName( StaticClass.LOCKBOX_PACKAGE_NAME , StaticClass.LOCKBOX_SETTING_ACTIVITY );
				mContext.startActivity( intentSetting );
			}
		} );
		return result;
	}
	
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
	
	private void saveListTime()
	{
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddkk" );
		String curDateString = sdf.format( new Date() );
		SharedPreferences sharedPrefer = PreferenceManager.getDefaultSharedPreferences( mContext );
		Editor edit = sharedPrefer.edit();
		edit.putString( StaticClass.HOT_LIST_DATE , curDateString );
		edit.commit();
	}
	
	@Override
	public void onFooterRefresh(
			PullToRefreshView view )
	{
		Log.v( "PullToRefreshView" , "tablock_onFooterRefresh" );
		if( gridPager.getCurrentItem() == INDEX_ALL )
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
							Toast.makeText( mContext , R.string.internet_unusual , Toast.LENGTH_SHORT ).show();
						}
						if( headerRefresh )
						{
							mPullToRefreshView.onHeaderRefreshComplete();
							headerRefresh = false;
							Toast.makeText( mContext , R.string.internet_unusual , Toast.LENGTH_SHORT ).show();
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
		if( gridPager.getCurrentItem() == INDEX_ALL )
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
							Toast.makeText( mContext , R.string.internet_unusual , Toast.LENGTH_SHORT ).show();
						}
						if( headerRefresh )
						{
							mPullToRefreshView.onHeaderRefreshComplete();
							headerRefresh = false;
							Toast.makeText( mContext , R.string.internet_unusual , Toast.LENGTH_SHORT ).show();
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
	
	private BroadcastReceiver packageReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(
				Context context ,
				Intent intent )
		{
			String actionName = intent.getAction();
			Log.d( LOG_TAG , String.format( "action=%s" , actionName ) );
			if( Intent.ACTION_PACKAGE_REMOVED.equals( actionName ) )
			{
				localAdapter.reloadPackage();
				hotAdapter.reloadPackage( localAdapter.getPackageNameSet() );
				simpleAdapter.reloadPackage( localAdapter.getPackageNameSet() );
				classicAdapter.reloadPackage( localAdapter.getPackageNameSet() );
			}
			else if( Intent.ACTION_PACKAGE_ADDED.equals( actionName ) )
			{
				localAdapter.reloadPackage();
				hotAdapter.reloadPackage( localAdapter.getPackageNameSet() );
				simpleAdapter.reloadPackage( localAdapter.getPackageNameSet() );
				classicAdapter.reloadPackage( localAdapter.getPackageNameSet() );
			}
			else if( actionName.equals( StaticClass.ACTION_THUMB_CHANGED ) )
			{
				hotAdapter.updateThumb( intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) );
				localAdapter.updateThumb( intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) );
				simpleAdapter.updateThumb( intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) );
				classicAdapter.updateThumb( intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) );
			}
			else if( actionName.equals( StaticClass.ACTION_HOTLIST_CHANGED ) )
			{
				saveListTime();
				hotAdapter.reloadPackage( localAdapter.getPackageNameSet() );
				simpleAdapter.reloadPackage( localAdapter.getPackageNameSet() );
				classicAdapter.reloadPackage( localAdapter.getPackageNameSet() );
				localAdapter.reloadPackage();
				pagerAdapter.notifyDataSetChanged();
				Log.v( "onHeaderRefresh" , "ACTION_HOTLIST_CHANGED footerRefresh = " + footerRefresh + " headerRefresh = " + headerRefresh );
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
			else if( actionName.equals( StaticClass.ACTION_HOTLIST_NOCHANGED ) )
			{
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
				Toast.makeText( mContext , R.string.list_isnew , Toast.LENGTH_SHORT ).show();
			}
			else if( actionName.equals( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED ) )
			{
				hotAdapter.reloadPackage( localAdapter.getPackageNameSet() );
				simpleAdapter.reloadPackage( localAdapter.getPackageNameSet() );
				localAdapter.reloadPackage();
				classicAdapter.reloadPackage( localAdapter.getPackageNameSet() );
			}
			else if( actionName.equals( StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED ) )
			{
				hotAdapter.updateDownloadSize(
						intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) ,
						intent.getIntExtra( StaticClass.EXTRA_DOWNLOAD_SIZE , 0 ) ,
						intent.getIntExtra( StaticClass.EXTRA_TOTAL_SIZE , 0 ) );
				simpleAdapter.updateDownloadSize(
						intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) ,
						intent.getIntExtra( StaticClass.EXTRA_DOWNLOAD_SIZE , 0 ) ,
						intent.getIntExtra( StaticClass.EXTRA_TOTAL_SIZE , 0 ) );
				classicAdapter.updateDownloadSize(
						intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) ,
						intent.getIntExtra( StaticClass.EXTRA_DOWNLOAD_SIZE , 0 ) ,
						intent.getIntExtra( StaticClass.EXTRA_TOTAL_SIZE , 0 ) );
			}
			else if( actionName.equals( StaticClass.ACTION_START_DOWNLOAD_APK ) )
			{
				String curdownApkname = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
				downModule.downloadApk( curdownApkname );
				hotAdapter.updateDownState( curdownApkname , DownloadStatus.StatusDownloading );
				simpleAdapter.updateDownState( curdownApkname , DownloadStatus.StatusDownloading );
				classicAdapter.updateDownState( curdownApkname , DownloadStatus.StatusDownloading );
			}
			else if( actionName.equals( StaticClass.ACTION_PAUSE_DOWNLOAD_APK ) )
			{
				String packName = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
				downModule.stopDownApk( packName );
				hotAdapter.updateDownState( packName , DownloadStatus.StatusPause );
				simpleAdapter.updateDownState( packName , DownloadStatus.StatusPause );
				classicAdapter.updateDownState( packName , DownloadStatus.StatusPause );
				Log.v( "********" , "receive packName = " + packName );
			}
			else if( actionName.equals( StaticClass.ACTION_DEFAULT_LOCK_CHANGED ) )
			{
				new Thread() {
					
					@Override
					public void run()
					{
						localAdapter.reloadPackage();
					}
				}.start();
			}
		}
	};
	
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
		private GridHotAdapter hotAdapter;
		private View SimpleView;
		private View ClassicView;
		
		public GridPagerAdapter(
				GridView local ,
				View view ,
				View simple ,
				View classic )
		{
			gridLocal = local;
			hotView = view;
			SimpleView = simple;
			ClassicView = classic;
		}
		
		public void setGridView(
				GridView hot )
		{
			gridHot = hot;
			hotAdapter = (GridHotAdapter)gridHot.getAdapter();
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
			switch( position )
			{
				case 0:
					( (ViewPager)container ).removeView( gridLocal );
					break;
				case 1:
					( (ViewPager)container ).removeView( hotView );
					break;
				case 2:
					( (ViewPager)container ).removeView( SimpleView );
					break;
				case 3:
					( (ViewPager)container ).removeView( ClassicView );
					break;
				default:
					break;
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
			return 4;
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
			if( position == 1 )
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
			}
			else if( position == 2 )
			{
				container.addView( SimpleView );
				return SimpleView;
			}
			else if( position == 3 )
			{
				container.addView( ClassicView );
				return ClassicView;
			}
			// ((ViewPager) container).addView(gridHot);
			( (ViewPager)container ).addView( hotView );
			return hotView;
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
	public void onClick(
			View v )
	{
		setClickSelector( v.getId() );
		Log.v( "onclick" , v.getId() + "" );
	}
	
	public void setClickSelector(
			int id )
	{
		for( int i = 0 ; i < title.length ; i++ )
		{
			if( id == i )
			{
				Bitmap bitmap = BitmapFactory.decodeResource( mContext.getResources() , R.drawable.fenleianxia );
				textViews.get( id ).setBackgroundDrawable( new BitmapDrawable( bitmap ) );
				textViews.get( id ).setTextColor( 0xFF4e760e );
				if( i > 2 )
				{
					horizontalScrollView.smoothScrollTo( ( textViews.get( i ).getWidth() * i - 180 ) , 0 );
				}
				else
				{
					horizontalScrollView.smoothScrollTo( 0 , 0 );
				}
				gridPager.setCurrentItem( i + 1 );
			}
			else
			{
				textViews.get( i ).setBackgroundDrawable( new BitmapDrawable() );
				textViews.get( i ).setTextColor( 0xFF4f7510 );
			}
		}
	}
}
