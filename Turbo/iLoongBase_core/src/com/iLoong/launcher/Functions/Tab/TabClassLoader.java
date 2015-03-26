package com.iLoong.launcher.Functions.Tab;


import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;

import com.iLoong.launcher.Desktop3D.Log;

import dalvik.system.DexClassLoader;


public class TabClassLoader
{
	
	private String sourceDir = "cooee/launcher/plugin/tab/{0}";
	private String targetDir = "cooee/launcher/plugin/tab/";
	private String libDir = null;
	private TabContext mTabContext;
	private TabPluginMetaData mTabPluginMetaData;
	private HashMap<String , DexClassLoader> mWidgetClassLoaderHash = new HashMap<String , DexClassLoader>();
	
	public TabClassLoader(
			TabContext tabContext ,
			TabPluginMetaData tabPluginMetaData )
	{
		this.mTabContext = tabContext;
		this.mTabPluginMetaData = tabPluginMetaData;
		if( tabPluginMetaData.resolveInfo == null )
		{
			Intent intent = new Intent( "com.iLoong.tab.plugin" , null );
			intent.setPackage( tabPluginMetaData.packageName );
			PackageManager pm = mTabContext.mContainerContext.getPackageManager();
			List<ResolveInfo> mWidgetResolveInfoList = pm.queryIntentActivities( intent , PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA );
			if( mWidgetResolveInfoList.size() > 0 )
			{
				tabPluginMetaData.resolveInfo = mWidgetResolveInfoList.get( 0 );
			}
		}
		if( tabPluginMetaData.resolveInfo != null )
		{
			ActivityInfo ainfo = tabPluginMetaData.resolveInfo.activityInfo;
			sourceDir = ainfo.applicationInfo.sourceDir;
			// String dexOutputDir = ainfo.applicationInfo.dataDir;
			// 插件输出目录，目前为launcher的子目录
			targetDir = tabContext.mContainerContext.getApplicationInfo().dataDir;
			targetDir = targetDir + File.separator + "plugins" + File.separator + ainfo.packageName;
			creatDataDir( targetDir );
			// String libPath = ainfo.applicationInfo.nativeLibraryDir;
			Integer sdkVersion = Integer.valueOf( android.os.Build.VERSION.SDK );
			if( sdkVersion > 8 )
			{
				libDir = ainfo.applicationInfo.nativeLibraryDir;
			}
		}
		else
		{
			sourceDir = sourceDir.replace( "{0}" , tabPluginMetaData.url );
			sourceDir = Environment.getExternalStorageDirectory() + "/" + sourceDir;
			targetDir = Environment.getExternalStorageDirectory() + "/" + targetDir;
		}
	}
	
	private File creatDataDir(
			String dirName )
	{
		File dir = new File( dirName );
		if( !dir.exists() )
		{
			dir.mkdirs();
		}
		return dir;
	}
	
	private DexClassLoader getClassLoader()
	{
		creatDataDir( targetDir );
		Log.e( "test" , "test sourceDir:" + sourceDir + " targetDir:" + targetDir );
		DexClassLoader loader = new DexClassLoader( sourceDir , targetDir , libDir , mTabContext.mContainerContext.getClassLoader() );
		return loader;
	}
	
	/**
	 * 通过反射获取WidgetPluginView3D实例
	 * 
	 * @param resolveInfo
	 * @param widgetId
	 * @return
	 */
	public ITabTitlePlugin loadTabTitlePlugin()
	{
		ITabTitlePlugin pluginView = null;
		try
		{
			DexClassLoader loader = null;
			if( !mWidgetClassLoaderHash.containsKey( mTabPluginMetaData.packageName ) )
			{
				loader = getClassLoader();
				mWidgetClassLoaderHash.put( mTabPluginMetaData.packageName , loader );
			}
			else
			{
				loader = mWidgetClassLoaderHash.get( mTabPluginMetaData.packageName );
			}
			Class<?> clazz = ( loader ).loadClass( mTabPluginMetaData.className );
			ITabTitlePlugin plugin = (ITabTitlePlugin)clazz.newInstance();
			return plugin;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return pluginView;
	}
	
	public ITabContentPlugin loadTabContentPlugin()
	{
		ITabContentPlugin pluginView = null;
		try
		{
			DexClassLoader loader = null;
			//			if (!mWidgetClassLoaderHash
			//					.containsKey(mTabPluginMetaData.packageName)) {
			//				loader = getClassLoader();
			//				mWidgetClassLoaderHash.put(mTabPluginMetaData.packageName,
			//						loader);
			//			} else {
			//				loader = mWidgetClassLoaderHash
			//						.get(mTabPluginMetaData.packageName);
			//			}
			loader = getClassLoader();
			Log.e( "test" , "test mTabPluginMetaData.className:" + mTabPluginMetaData.className );
			Class<?> clazz = ( loader ).loadClass( mTabPluginMetaData.className );
			ITabContentPlugin plugin = (ITabContentPlugin)clazz.newInstance();
			return plugin;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return pluginView;
	}
	
	public void onUninstallPlugin(
			String packageName )
	{
		if( mWidgetClassLoaderHash.containsKey( packageName ) )
		{
			mWidgetClassLoaderHash.remove( packageName );
		}
	}
}
