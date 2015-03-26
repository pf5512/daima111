package com.iLoong.launcher.desktop;


import java.io.File;
import java.lang.ref.WeakReference;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Environment;

import com.cooee.android.launcher.framework.IconCache;
import com.cooee.android.launcher.framework.LauncherModel;
import com.cooee.android.launcher.framework.LauncherProvider;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreview3D;
import com.iLoong.launcher.UI3DEngine.ConfigBase;
import com.iLoong.launcher.core.Assets;
import com.iLoong.launcher.core.CustomerHttpClient;
import com.iLoong.launcher.core.UEHandler;
import com.iLoong.launcher.pub.provider.PubContentProvider;
import com.iLoong.launcher.pub.provider.PubProviderHelper;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.theme.adapter.ThemeConfig;


public class iLoongApplication extends Application
{
	
	private final String GOOGLE_VERSION_APP_ID = "f2986";
	/** "/data/data/<app_package>/files/error.log" */
	public static String PATH_ERROR_LOG = "error.log";
	public static String PATH_ENABLE_LOG = "enablelog.log";
	/** 标识是否需要退出。为true时表示当前的Activity要执行finish() */
	private boolean need2Exit;
	/** 异常处理类 */
	private UEHandler ueHandler;
	private static Context ctx;
	public static boolean BuiltIn = false;
	public static String dataDir = "";
	private static boolean sIsScreenLarge;
	private static float sScreenDensity;
	public LauncherModel mModel;
	public static IconCache mIconCache;
	WeakReference<LauncherProvider> mLauncherProvider;
	public ThemeManager mThemeManager;
	public static boolean needRestart = false;
	public static boolean init = false;
	public static ThemeConfig themeConfig;
	
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
	
	@Override
	public void onCreate()
	{
		// VMRuntime.getRuntime().setMinimumHeapSize(4 * 1024 * 1024);
		super.onCreate();
		ctx = this;
		PubContentProvider.init( null );
		new PubProviderHelper( ctx );
		if( RR.net_version )
		{
			ConfigBase.net_version = true;
		}
		Assets.initAssets( ctx );
		SharedPreferences sp = getSharedPreferences( Assets.PREFERENCE_KEY_CONFIG , Activity.MODE_WORLD_READABLE );
		String appId = sp.getString( Assets.PREFERENCE_KEY_CONFIG_APPID , "" );
		if( appId.equals( GOOGLE_VERSION_APP_ID ) )
		{
			DefaultLayout.enable_google_version = true;
		}
		else
		{
			DefaultLayout.enable_google_version = false;
		}
		new Thread() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				try
				{
					JSONObject config = Assets.config.getJSONObject( "config" );
					if( config != null )
					{
						PubProviderHelper.addOrUpdateValue( "config" , Assets.PREFERENCE_KEY_CONFIG_DOMAIN , config.getString( Assets.PREFERENCE_KEY_CONFIG_DOMAIN ) );
						PubProviderHelper.addOrUpdateValue( "config" , Assets.PREFERENCE_KEY_CONFIG_SERIALNO , config.getString( Assets.PREFERENCE_KEY_CONFIG_SERIALNO ) );
						PubProviderHelper.addOrUpdateValue( "config" , Assets.PREFERENCE_KEY_CONFIG_APPID , config.getString( Assets.PREFERENCE_KEY_CONFIG_APPID ) );
						PubProviderHelper.addOrUpdateValue( "config" , Assets.PREFERENCE_KEY_CONFIG_TEMPLATEID , config.getString( Assets.PREFERENCE_KEY_CONFIG_TEMPLATEID ) );
						PubProviderHelper.addOrUpdateValue( "config" , Assets.PREFERENCE_KEY_CONFIG_CHANNELID , config.getString( Assets.PREFERENCE_KEY_CONFIG_CHANNELID ) );
					}
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}
		}.start();
		init = true;
		new DefaultLayout();
		mThemeManager = new ThemeManager( this );
		themeConfig = new ThemeConfig();
		dataDir = this.getApplicationInfo().dataDir;
		if( RR.net_version )
			BuiltIn = Integer.valueOf( android.os.Build.VERSION.SDK_INT ) > 15 ? true : false;
		else
			BuiltIn = ( this.getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0;
		ConfigBase.AUTHORITY = RR.getDBAuthority();
		String sdpath = getSDPath();
		if( sdpath != null )
			PATH_ERROR_LOG = sdpath + File.separator + "error.log";
		if( sdpath != null )
		{
			PATH_ENABLE_LOG = sdpath + File.separator + "enablelog.log";
			File dir = new File( PATH_ENABLE_LOG );
			if( dir.exists() )
			{
				Log.setEnableLog( true );
			}
		}
		need2Exit = false;
		ueHandler = new UEHandler( this , PATH_ERROR_LOG );
		// 设置异常处理实例
		Thread.setDefaultUncaughtExceptionHandler( ueHandler );
		mIconCache = new IconCache( this );
		registerAllReceivers();
		new CustomerHttpClient( this );
	}
	
	// Register intent receivers
	private void registerAllReceivers()
	{
		mModel = new LauncherModel( this , mIconCache );
		IntentFilter filter = new IntentFilter( Intent.ACTION_PACKAGE_ADDED );
		filter.addAction( Intent.ACTION_PACKAGE_REMOVED );
		filter.addAction( Intent.ACTION_PACKAGE_CHANGED );
		filter.addDataScheme( "package" );
		registerReceiver( mModel , filter );
		filter = new IntentFilter();
		filter.addAction( Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE );
		filter.addAction( Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE );
		registerReceiver( mModel , filter );
		filter = new IntentFilter( EffectPreview3D.ACTION_EFFECT_PREVIEW );
		registerReceiver( mModel , filter );
	}
	
	/**
	 * There's no guarantee that this function is ever called.
	 */
	@Override
	public void onTerminate()
	{
		super.onTerminate();
		unregisterReceiver( mModel );
	}
	
	public void StopServer()
	{
		// if(!iLoongApplication.BuiltIn)fsc.StopServer();
	}
	
	public LauncherModel setLauncher(
			iLoongLauncher launcher )
	{
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
	
	public static String getPreferencePath()
	{
		if( dataDir == null )
			return null;
		return dataDir + "/shared_prefs/";
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
