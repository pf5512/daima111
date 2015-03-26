package com.iLoong.launcher.desktop;


import java.io.File;
import java.lang.ref.WeakReference;

import com.badlogic.gdx.files.FileHandle;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.ForegroundServiceClient;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.ConfigBase;
import com.iLoong.launcher.UI3DEngine.MyPixmapPacker;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.app.*;
import com.iLoong.launcher.core.Assets;
import com.iLoong.launcher.core.CustomerHttpClient;
import com.iLoong.launcher.core.UEHandler;
import com.iLoong.launcher.scene.SceneManager;
import com.iLoong.launcher.theme.ThemeManager;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Environment;
import android.os.Handler;
import com.iLoong.launcher.Desktop3D.Log;


// import dalvik.system.VMRuntime;
public class iLoongApplication extends Application
{
	
	/** "/data/data/<app_package>/files/error.log" */
	public static String PATH_ERROR_LOG = "error.log";
	public static String PATH_ENABLE_LOG = "enablelog.log";
	/** 标识是否需要退出。为true时表示当前的Activity要执行finish() */
	private boolean need2Exit;
	/** 异常处理类 */
	private UEHandler ueHandler;
	public static Context ctx;
	public static boolean BuiltIn = false;
	
	public void setNeed2Exit(
			boolean bool )
	{
		need2Exit = bool;
	}
	
	public boolean need2Exit()
	{
		return need2Exit;
	}
	
	public static iLoongApplication getInstance()
	{
		iLoongApplication localApplication;
		if( ctx != null )
			localApplication = (iLoongApplication)ctx;
		else
			localApplication = null;
		return localApplication;
	}
	
	private static boolean sIsScreenLarge;
	private static float sScreenDensity;
	public LauncherModel mModel;
	public static IconCache mIconCache;
	WeakReference<LauncherProvider> mLauncherProvider;
	public ThemeManager mThemeManager;
	public SceneManager mSceneManager; // wanghongjian add //enable_DefaultScene
	public FeatureConfig featureConfig;
	private ForegroundServiceClient fsc;
	// enable_themebox
	public static boolean needRestart = false;
	public static boolean init = false;
	
	// enable_themebox
	@Override
	public void onCreate()
	{
		// VMRuntime.getRuntime().setMinimumHeapSize(4 * 1024 * 1024);
		super.onCreate();
		ctx = this;
		Assets.initAssets( ctx );
		// enable_themebox
		init = true;
		com.coco.theme.themebox.service.ThemesDB.LAUNCHER_PACKAGENAME = RR.getPackageName();
		com.coco.theme.themebox.service.ThemesDB.ACTION_LAUNCHER_APPLY_THEME = ThemeReceiver.ACTION_LAUNCHER_APPLY_THEME;
		com.coco.theme.themebox.service.ThemesDB.ACTION_LAUNCHER_RESTART = ThemeReceiver.ACTION_LAUNCHER_RESTART;
		mThemeManager = new ThemeManager( this );
		featureConfig = new FeatureConfig();
		// wanghongjian add start //enable_DefaultScene
		if( FeatureConfig.enable_DefaultScene )
		{
			mSceneManager = new SceneManager( this );
		}
		// wanghongjian add end
		// enable_themebox
		BuiltIn = ( this.getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0;
		// BuiltIn = true;
		ConfigBase.AUTHORITY = RR.getDBAuthority();
		String sdpath = getSDPath();
		if( sdpath != null )
			PATH_ERROR_LOG = sdpath + File.separator + "error.log";
		if( sdpath != null )
		{
			PATH_ENABLE_LOG = sdpath + File.separator + "enablelog.log";
			File dir = new File( PATH_ENABLE_LOG );
			//			if (dir.exists()) {
			Log.setEnableLog( true );
			//			}
		}
		need2Exit = false;
		ueHandler = new UEHandler( this , PATH_ERROR_LOG );
		// 设置异常处理实例
		Thread.setDefaultUncaughtExceptionHandler( ueHandler );
		Log.v( "iLoongApplication" , "应用程序启动" );
		if( !iLoongApplication.BuiltIn )
		{
			fsc = new ForegroundServiceClient( this );
			fsc.StartServer();
		}
		mIconCache = new IconCache( this );
		mModel = new LauncherModel( this , mIconCache );
		new CustomerHttpClient( this );
		// Register intent receivers
		IntentFilter filter = new IntentFilter( Intent.ACTION_PACKAGE_ADDED );
		filter.addAction( Intent.ACTION_PACKAGE_REMOVED );
		filter.addAction( Intent.ACTION_PACKAGE_CHANGED );
		filter.addDataScheme( "package" );
		registerReceiver( mModel , filter );
		filter = new IntentFilter();
		filter.addAction( Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE );
		filter.addAction( Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE );
		registerReceiver( mModel , filter );
		filter = new IntentFilter( Intent.ACTION_DEFAULT );
		// filter.addAction("show mem");
		registerReceiver( mModel , filter );
		// Register for changes to the favorites
		ContentResolver resolver = getContentResolver();
		resolver.registerContentObserver( LauncherSettings.Favorites.CONTENT_URI , true , mFavoritesObserver );
	}
	
	/**
	 * There's no guarantee that this function is ever called.
	 */
	@Override
	public void onTerminate()
	{
		super.onTerminate();
		unregisterReceiver( mModel );
		ContentResolver resolver = getContentResolver();
		resolver.unregisterContentObserver( mFavoritesObserver );
	}
	
	/**
	 * Receives notifications whenever the user favorites have changed.
	 */
	private final ContentObserver mFavoritesObserver = new ContentObserver( new Handler() ) {
		
		@Override
		public void onChange(
				boolean selfChange )
		{
			mModel.startLoader( iLoongApplication.this , false );
		}
	};
	
	public void StopServer()
	{
		// if(!iLoongApplication.BuiltIn)fsc.StopServer();
	}
	
	public LauncherModel setLauncher(
			iLoongLauncher launcher )
	{
		// mModel.initialize(launcher);
		return mModel;
	}
	
	public IconCache getIconCache()
	{
		return mIconCache;
	}
	
	public LauncherModel getModel()
	{
		return mModel;
	}
	
	public void setLauncherProvider(
			LauncherProvider provider )
	{
		mLauncherProvider = new WeakReference<LauncherProvider>( provider );
	}
	
	public LauncherProvider getLauncherProvider()
	{
		return mLauncherProvider.get();
	}
	
	public static boolean isScreenLarge()
	{
		return sIsScreenLarge;
	}
	
	public static boolean isScreenLandscape(
			Context context )
	{
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}
	
	public static float getScreenDensity()
	{
		return sScreenDensity;
	}
	
	public static String getAppPath()
	{
		String sd = getSDPath();
		if( sd == null )
			return null;
		File file = new File( sd , "/coco/" );
		if( !file.exists() )
			file.mkdir();
		return file.getAbsolutePath();
	}
	
	public static String getDownloadPath()
	{
		String sd = getSDPath();
		if( sd == null )
			return null;
		if( DefaultLayout.useCustomVirtualDownload )
		{
			return DefaultLayout.custom_virtual_download_path;
		}
		File file = new File( sd , "/coco/download/" );
		if( !file.exists() )
			file.mkdirs();
		return file.getAbsolutePath();
	}
	
	public static String getBackupPath()
	{
		String sd = getSDPath();
		if( sd == null )
			return null;
		String backup = "/coco/backup/" + iLoongApplication.getInstance().getPackageName();
		File file = new File( sd , backup );
		if( !file.exists() )
			file.mkdirs();
		return file.getAbsolutePath();
	}
	
	public static String getSDPath()
	{
		File SDdir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals( android.os.Environment.MEDIA_MOUNTED );
		if( sdCardExist )
		{
			SDdir = Environment.getExternalStorageDirectory();
		}
		if( SDdir != null )
		{
			return SDdir.toString();
		}
		else
		{
			return null;
		}
	}
	
	public String getVersionCode()
	{
		PackageInfo info;
		try
		{
			info = getPackageManager().getPackageInfo( getPackageName() , 0 );
			return String.valueOf( info.versionCode );
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		return "0";
	}
	
	public String getVersionName()
	{
		PackageInfo info;
		try
		{
			info = getPackageManager().getPackageInfo( getPackageName() , 0 );
			return info.versionName;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		return null;
	}
}
