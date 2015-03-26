package com.iLoong.launcher.CooeePlugin;


import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;

import com.iLoong.launcher.UI3DEngine.ConfigBase;
import com.iLoong.launcher.data.CooeePluginInfo;


public class CooeePluginManager
{
	
	public static final String COOEE_PLUGIN_CATEGORY = "cooee.intent.category.COOEE_PLUGIN";
	private static CooeePluginManager manager;
	private Context context;
	private HashMap<String , View> hostViews = new HashMap<String , View>();
	
	public static CooeePluginManager getInstance(
			Context context )
	{
		if( manager == null )
		{
			synchronized( CooeePluginManager.class )
			{
				if( manager == null )
					manager = new CooeePluginManager( context );
			}
		}
		return manager;
	}
	
	public CooeePluginManager(
			Context context )
	{
		this.context = context;
	}
	
	public CooeePluginHostView getHostView(
			CooeePluginInfo info )
	{
		if( info.getPackageName() == null )
			return null;
		Intent intent = new Intent();
		intent.addCategory( COOEE_PLUGIN_CATEGORY );
		intent.setPackage( info.getPackageName() );
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> mResolveInfoList = pm.queryIntentActivities( intent , PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA );
		if( mResolveInfoList == null || mResolveInfoList.size() < 1 )
			return null;
		CooeePluginBridge pluginBridge = reflectPlugin( mResolveInfoList.get( 0 ) , 0 );
		if( pluginBridge == null )
			return null;
		CooeePluginHostView hostView = new CooeePluginHostView( context );
		hostView.bindPlugin( pluginBridge );
		hostViews.put( info.getPackageName() , hostView );
		return hostView;
	}
	
	private CooeePluginBridge reflectPlugin(
			ResolveInfo resolveInfo ,
			int widgetId )
	{
		CooeePluginBridge pluginBridge = null;
		try
		{
			Context widgetPluginContext = context.createPackageContext( resolveInfo.activityInfo.packageName , Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY );
			// 设置同launcher语言
			widgetPluginContext.getResources().updateConfiguration( context.getResources().getConfiguration() , context.getResources().getDisplayMetrics() );
			//CooeePluginClassLoader loader = getClassLoader(resolveInfo,context);
			//Class<?> clazz = loader.loadPluginClass(resolveInfo.activityInfo.name);
			Class<?> clazz = widgetPluginContext.getClassLoader().loadClass( resolveInfo.activityInfo.name );
			pluginBridge = new CooeePluginBridge();
			pluginBridge.setPlugin( clazz.newInstance() );
			pluginBridge.setContext( context , widgetPluginContext , ConfigBase.set_status_bar_background );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return pluginBridge;
	}
	
	private CooeePluginClassLoader getClassLoader(
			ResolveInfo resolveInfo ,
			Context context )
	{
		ActivityInfo ainfo = resolveInfo.activityInfo;
		String dexPath = ainfo.applicationInfo.sourceDir;
		// String dexOutputDir = ainfo.applicationInfo.dataDir;
		// 插件输出目录，目前为launcher的子目录
		String dexOutputDir = this.context.getApplicationInfo().dataDir;
		dexOutputDir = dexOutputDir + File.separator + "plugin" + File.separator + ainfo.packageName;
		creatDataDir( dexOutputDir );
		// String libPath = ainfo.applicationInfo.nativeLibraryDir;
		Integer sdkVersion = Integer.valueOf( android.os.Build.VERSION.SDK );
		String libPath = null;
		if( sdkVersion > 8 )
		{
			libPath = ainfo.applicationInfo.nativeLibraryDir;
		}
		CooeePluginClassLoader loader = new CooeePluginClassLoader( dexPath , dexOutputDir , libPath , context.getClassLoader() );
		return loader;
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
}
