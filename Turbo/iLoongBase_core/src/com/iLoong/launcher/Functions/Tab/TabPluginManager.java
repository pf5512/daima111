package com.iLoong.launcher.Functions.Tab;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Base64;

import com.iLoong.launcher.Desktop3D.Log;


public class TabPluginManager implements RemoteDataObserver
{
	
	public static String productName = "tabplugin";
	public static boolean devTestMode = true;
	private static TabPluginManager mInstance = null;
	private static String tabPluginListFile = "/cooee/launcher/plugin/tab/remotePluginList.xml";
	private HandlerThread sWorkerThread = null;
	private Handler sWorker = null;
	private ArrayList<PluginDataObserver> observers;
	private TabContext mTabContext;
	private ArrayList<Plugin> mPluginList = new ArrayList<Plugin>();
	private ITabTitlePlugin mTabTitlePlugin = null;
	private SharedPreferences preferences = null;
	private ArrayList<TabPluginMetaData> remoteMetaDataList;
	private ArrayList<TabPluginMetaData> allInstalledMetaDataList;
	
	public ITabTitlePlugin getmTabTitlePlugin()
	{
		return mTabTitlePlugin;
	}
	
	public void setmTabTitlePlugin(
			ITabTitlePlugin mTabTitlePlugin )
	{
		this.mTabTitlePlugin = mTabTitlePlugin;
	}
	
	public TabPluginManager(
			TabContext tabContext )
	{
		this.mTabContext = tabContext;
		observers = new ArrayList<PluginDataObserver>();
		preferences = mTabContext.mContainerContext.getSharedPreferences( "plugins.tab" , Activity.MODE_PRIVATE );
		//tabPluginListFile = TabPluginUtils.getSDPath() + tabPluginListFile;
		//tabPluginListFile = TabPuginPathUtil.getTabPluginListFilePath( mTabContext.mContainerContext );
		createDir();
		sWorkerThread = new HandlerThread( "TabPluginManager" );
		sWorkerThread.start();
		sWorker = new Handler( sWorkerThread.getLooper() );
		mInstance = this;
	}
	
	private void createDir()
	{
		try
		{
			boolean sdCardExist = Environment.getExternalStorageState().equals( android.os.Environment.MEDIA_MOUNTED );
			if( sdCardExist && tabPluginListFile!=null)
			{
				File dir = new File( tabPluginListFile.substring( 0 , tabPluginListFile.lastIndexOf( "/" ) ) );
				Log.e( "test" , "dir:" + dir );
				if( !dir.exists() )
				{
					Log.e( "test" , "dir 2:" + dir );
					boolean result = dir.mkdirs();
					Log.e( "test" , "dir 3:" + result );
				}
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	public void build()
	{
		syncLocalPlugins();
	}
	
	boolean syncLoadLocalConfig = true;
	
	public void addTabPluginDataObserver(
			PluginDataObserver observer )
	{
		if( !observers.contains( observer ) )
		{
			observers.add( observer );
		}
	}
	
	private void syncLocalPlugins()
	{
		if( syncLoadLocalConfig )
		{
			loadLocalPlugins();
		}
		else
		{
			sWorker.post( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					loadLocalPlugins();
				}
			} );
		}
	}
	
	public ArrayList<TabPluginMetaData> filterInstalledMetaData()
	{
		ArrayList<TabPluginMetaData> configedMetaDataList = loadLocalConfigedPluginMetaData();
		ArrayList<TabPluginMetaData> installedMetaDataList = loadInstalledPluginMetaData();
		final ArrayList<TabPluginMetaData> showMetaDataList = new ArrayList<TabPluginMetaData>();
		for( int i = 0 ; i < installedMetaDataList.size() ; i++ )
		{
			TabPluginMetaData installedMetaData = installedMetaDataList.get( i );
			for( int j = 0 ; j < configedMetaDataList.size() ; j++ )
			{
				TabPluginMetaData configMetaData = configedMetaDataList.get( j );
				if( configMetaData.show )
				{
					if( installedMetaData.packageName.equals( configMetaData.packageName ) )
					{
						configMetaData.setResolveInfo( installedMetaData.resolveInfo );
						showMetaDataList.add( configMetaData );
						break;
					}
				}
			}
		}
		return showMetaDataList;
	}
	
	public ArrayList<TabPluginMetaData> filterRemoteMetaData(
			ArrayList<TabPluginMetaData> launcherMetaDataList ,
			ArrayList<TabPluginMetaData> remoteMetaDataList )
	{
		if( remoteMetaDataList.size() > 0 )
		{
			ArrayList<TabPluginMetaData> tmpPlugins = new ArrayList<TabPluginMetaData>();
			for( TabPluginMetaData lastMetaData : remoteMetaDataList )
			{
				if( lastMetaData.show )
				{
					for( TabPluginMetaData curMetaData : launcherMetaDataList )
					{
						if( lastMetaData.packageName.equals( curMetaData.packageName ) )
						{
							lastMetaData.setResolveInfo( curMetaData.resolveInfo );
							tmpPlugins.add( lastMetaData );
							break;
						}
					}
				}
			}
			launcherMetaDataList.clear();
			launcherMetaDataList.addAll( tmpPlugins );
		}
		return launcherMetaDataList;
	}
	
	public void loadLocalPlugins()
	{
		ArrayList<TabPluginMetaData> showMetaDataList = new ArrayList<TabPluginMetaData>();
		remoteMetaDataList = loadSavedRemotePluginMetaData();
		if( remoteMetaDataList != null && remoteMetaDataList.size() > 0 )
		{
			ArrayList<TabPluginMetaData> installedTabPluginMetaDataList = loadInstalledPluginMetaData();
			showMetaDataList = filterRemoteMetaData( installedTabPluginMetaDataList , remoteMetaDataList );
		}
		else
		{
			showMetaDataList = filterInstalledMetaData();
		}
		ArrayList<Plugin> plugins = new ArrayList<Plugin>( showMetaDataList.size() );
		for( TabPluginMetaData curMetaData : showMetaDataList )
		{
			Plugin plugin = new Plugin( mTabContext , curMetaData );
			plugin.setTabTitlePlugin( mTabTitlePlugin );
			// plugin.buildTabTitle3D();
			plugins.add( plugin );
		}
		sortPlugins( plugins );
		if( mPluginList == null )
			mPluginList = new ArrayList<Plugin>( plugins.size() );
		else
		{
			for( int i = 0 ; i < mPluginList.size() ; i++ )
			{
				mPluginList.get( i ).dispose();
			}
			mPluginList.clear();
		}
		mPluginList.addAll( plugins );
		plugins.clear();
	}
	
	public static TabPluginManager getInstance()
	{
		return mInstance;
	}
	
	public ArrayList<TabPluginMetaData> loadSavedRemotePluginMetaData()
	{
		ConfigHandler configHandler = new ConfigHandler( this.mTabContext );
		if(tabPluginListFile!=null)
		{
		configHandler.LoadXml( tabPluginListFile );
		}
		ArrayList<TabPluginMetaData> configedPlugins = configHandler.getTabPluginMetaDataList();
		String locale = mTabContext.mContainerContext.getResources().getConfiguration().locale.toString();
		for( int i = 0 ; i < configedPlugins.size() ; i++ )
		{
			TabPluginMetaData metaData = configedPlugins.get( i );
			if( locale.contains( "cn" ) || locale.contains( "CN" ) )
			{
				metaData.pluginTitle = metaData.cnName;
			}
			else if( locale.contains( "tw" ) || locale.contains( "TW" ) )
			{
				metaData.pluginTitle = metaData.twName;
			}
			else if( locale.contains( "en" ) || locale.contains( "EN" ) )
			{
				metaData.pluginTitle = metaData.enName;
			}
			else
			{
				metaData.pluginTitle = metaData.enName;
			}
			if( metaData.pluginTitle == null || metaData.pluginTitle.length() == 0 )
			{
				metaData.pluginTitle = "UNKNOW";
			}
		}
		return configedPlugins;
	}
	
	public ArrayList<TabPluginMetaData> loadLocalConfigedPluginMetaData()
	{
		ConfigHandler configHandler = new ConfigHandler( this.mTabContext );
		configHandler.LoadDefaultLayoutXml();
		ArrayList<TabPluginMetaData> configedPlugins = configHandler.getTabPluginMetaDataList();
		return configedPlugins;
	}
	
	public ArrayList<TabPluginMetaData> loadInstalledPluginMetaData()
	{
		Intent intent = new Intent( "com.iLoong.tab.plugin" , null );
		PackageManager pm = mTabContext.mContainerContext.getPackageManager();
		List<ResolveInfo> mWidgetResolveInfoList = pm.queryIntentActivities( intent , PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA );
		ArrayList<TabPluginMetaData> list = new ArrayList<TabPluginMetaData>();
		if( allInstalledMetaDataList != null )
		{
			allInstalledMetaDataList.clear();
		}
		else
		{
			allInstalledMetaDataList = new ArrayList<TabPluginMetaData>();
		}
		for( int i = 0 ; i < mWidgetResolveInfoList.size() ; i++ )
		{
			TabPluginMetaData metaData = new TabPluginMetaData();
			metaData.setResolveInfo( mWidgetResolveInfoList.get( i ) );
			String locale = mTabContext.mContainerContext.getResources().getConfiguration().locale.toString();
			if( locale.contains( "cn" ) || locale.contains( "CN" ) )
			{
				metaData.pluginTitle = metaData.cnName;
			}
			else if( locale.contains( "tw" ) || locale.contains( "TW" ) )
			{
				metaData.pluginTitle = metaData.twName;
			}
			else if( locale.contains( "en" ) || locale.contains( "EN" ) )
			{
				metaData.pluginTitle = metaData.enName;
			}
			else
			{
				metaData.pluginTitle = metaData.enName;
			}
			if( metaData.pluginTitle == null || metaData.pluginTitle.length() == 0 )
			{
				metaData.pluginTitle = "UNKNOW";
			}
			list.add( metaData );
			allInstalledMetaDataList.add( metaData );
		}
		return list;
	}
	
	public ArrayList<TabPluginMetaData> getAllInstalledMetaDataList()
	{
		return allInstalledMetaDataList;
	}
	
	public ArrayList<Plugin> getPluginList()
	{
		return mPluginList;
	}
	
	private void compareVersion(
			Plugin plugin )
	{
		Context widgetPluginContext = null;
		try
		{
			int verCode = mTabContext.mContainerContext.getPackageManager().getPackageInfo( plugin.mTabContentMetaData.packageName , 0 ).versionCode;
			if( verCode < plugin.mTabContentMetaData.versionCode )
			{
				plugin.mTabContentNeedUpdate = true;
			}
			else
			{
				plugin.mTabContentNeedUpdate = false;
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		mTabContext.mWidgetContext = widgetPluginContext;
	}
	
	public synchronized void notifyObservers(
			ArrayList<Plugin> plugins )
	{
		if( observers != null )
		{
			for( PluginDataObserver observer : observers )
			{
				observer.update( plugins );
			}
		}
	}
	
	public interface PluginDataObserver
	{
		
		public void update(
				ArrayList<Plugin> plugins );
	}
	
	public void saveObject(
			TabPluginMetaData metaData )
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream( baos );
			oos.writeObject( metaData );
			SharedPreferences preferences = mTabContext.mContainerContext.getSharedPreferences( "tabPlugin" , Activity.MODE_PRIVATE );
			Editor editor = preferences.edit();
			// 将对象转换成byte数组，并将其进行base64转换。
			String productBase64 = new String( Base64.encode( baos.toByteArray() , Base64.DEFAULT ) );
			editor.putString( "plugins" , productBase64 );
			editor.commit();
			oos.close();
		}
		catch( Exception e )
		{
			// TODO: handle exception
		}
	}
	
	public TabPluginMetaData readObject()
	{
		TabPluginMetaData product = null;
		try
		{
			SharedPreferences preferences = mTabContext.mContainerContext.getSharedPreferences( "tabPlugin" , Activity.MODE_PRIVATE );
			// 从源xml文件中，读取Product对象的base64格式字符串
			String base64Product = preferences.getString( "plugins" , "" );
			// 将base64格式字符串还原成byte数组
			byte[] productBytes = Base64.decode( base64Product.getBytes() , Base64.DEFAULT );
			ByteArrayInputStream bais = new ByteArrayInputStream( productBytes );
			ObjectInputStream ois = new ObjectInputStream( bais );
			// byte数组，转换成Product对象
			product = (TabPluginMetaData)ois.readObject();
			ois.close();
			bais.close();
		}
		catch( Exception e )
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		return product;
	}
	
	@Override
	public synchronized void onPluginInstall(
			String packageName )
	{
		// TODO Auto-generated method stub
		TabPluginMetaData metaData = null;
		Log.e( "tabpluginmanager" , "onPluginInstall 1 package:" + packageName + "mPluginList.size:" + ( mPluginList == null ? 0 : mPluginList.size() ) );
		boolean canShow = false;
		if( remoteMetaDataList.size() > 0 )
		{
			for( int i = 0 ; i < this.remoteMetaDataList.size() ; i++ )
			{
				metaData = remoteMetaDataList.get( i );
				if( metaData.packageName.equals( packageName ) && metaData.show )
				{
					canShow = true;
					break;
				}
			}
		}
		else
		{
			ArrayList<TabPluginMetaData> launcherConfigedMetaList = loadLocalConfigedPluginMetaData();
			if( launcherConfigedMetaList.size() > 0 )
			{
				for( int i = 0 ; i < launcherConfigedMetaList.size() ; i++ )
				{
					metaData = launcherConfigedMetaList.get( i );
					if( metaData.packageName.equals( packageName ) && metaData.show )
					{
						canShow = true;
						break;
					}
				}
			}
		}
		if( canShow )
		{
			Plugin curPlugin = null;
			boolean alreadyShow = false;
			for( int j = 0 ; j < this.mPluginList.size() ; j++ )
			{
				curPlugin = mPluginList.get( j );
				if( curPlugin.mTabContentMetaData.packageName.equals( packageName ) )
				{
					alreadyShow = true;
					break;
				}
			}
			if( alreadyShow )
			{
				curPlugin.disposeTabContent();
			}
			else
			{
				Plugin plugin = new Plugin( mTabContext , metaData );
				plugin.setTabTitlePlugin( mTabTitlePlugin );
				this.mPluginList.add( plugin );
			}
			if( mPluginList == null )
			{
				mPluginList = new ArrayList<Plugin>();
			}
			sortPlugins( mPluginList );
			mTabContext.mGdxApplication.postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					Log.e( "tabpluginmanager" , "onPluginInstall 2 mPluginList.size:" + ( mPluginList == null ? 0 : mPluginList.size() ) );
					notifyObservers( mPluginList );
				}
			} );
		}
	}
	
	@Override
	public synchronized void onPluginUninstall(
			String packageName )
	{
		Log.e( "tabpluginmanager" , "onPluginUninstall 1 package:" + packageName + " mPluginList.size:" + ( mPluginList == null ? 0 : mPluginList.size() ) );
		// TODO Auto-generated method stub
		Plugin curPlugin = null;
		Plugin tmpUninstallPlugin = null;
		for( int j = 0 ; j < this.mPluginList.size() ; j++ )
		{
			curPlugin = mPluginList.get( j );
			if( curPlugin.mTabContentMetaData.packageName.equals( packageName ) )
			{
				//curPlugin.onUninstall();
				tmpUninstallPlugin = curPlugin;
				mPluginList.remove( j );
				break;
			}
		}
		for( int j = 0 ; j < this.allInstalledMetaDataList.size() ; j++ )
		{
			TabPluginMetaData metaData = allInstalledMetaDataList.get( j );
			if( metaData.packageName.equals( packageName ) )
			{
				allInstalledMetaDataList.remove( j );
				break;
			}
		}
		if( mPluginList == null )
		{
			mPluginList = new ArrayList<Plugin>();
		}
		sortPlugins( mPluginList );
		final Plugin uninstallPlugin = tmpUninstallPlugin;
		mTabContext.mGdxApplication.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				notifyObservers( mPluginList );
				if( uninstallPlugin != null )
				{
					Log.e(
							"tabpluginmanager" ,
							"onPluginUninstall 2 package:" + uninstallPlugin.mTabContentMetaData.packageName + "mPluginList.size:" + ( mPluginList == null ? 0 : mPluginList.size() ) );
					uninstallPlugin.onUninstall();
				}
			}
		} );
	}
	
	public TabPluginMetaData isTabPlugin(
			String packageName )
	{
		// TODO Auto-generated method stub
		TabPluginMetaData metaData = null;
		for( int j = 0 ; j < this.remoteMetaDataList.size() ; j++ )
		{
			TabPluginMetaData tmpMetaData = remoteMetaDataList.get( j );
			if( tmpMetaData.packageName.equals( packageName ) )
			{
				metaData = tmpMetaData;
				break;
			}
		}
		return metaData;
	}
	
	private void sortPlugins(
			ArrayList<Plugin> plugins )
	{
		Collections.sort( plugins , new Comparator<Plugin>() {
			
			public int compare(
					Plugin b1 ,
					Plugin b2 )
			{
				return b1.mTabContentMetaData.order - ( b2.mTabContentMetaData.order );
			}
		} );
	}
}
