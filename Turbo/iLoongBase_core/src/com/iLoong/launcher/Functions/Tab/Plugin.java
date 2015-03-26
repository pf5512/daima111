package com.iLoong.launcher.Functions.Tab;


import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.iLoong.launcher.Desktop3D.Log;


public class Plugin
{
	
	protected TabContext mTabContext;
	protected ITabTitlePlugin mTabTitlePlugin;
	protected ITabContentPlugin mTabContentPlugin;
	protected TabContent3D mTabContent;
	protected TabTitle3D mTabTitle;
	protected TabPluginMetaData mTabContentMetaData;
	protected TabClassLoader mTabClassLoader;
	public boolean mTabContentNeedUpdate = false;
	public boolean mTabTitleDirty = true;
	public boolean mTabContentDirty = true;
	public boolean mSelected = false;
	
	public TabClassLoader getmTabClassLoader()
	{
		return mTabClassLoader;
	}
	
	public void setmTabClassLoader(
			TabClassLoader mTabClassLoader )
	{
		this.mTabClassLoader = mTabClassLoader;
	}
	
	public TabPluginMetaData getTabPluginMetaData()
	{
		return mTabContentMetaData;
	}
	
	public void setTabPluginMetaData(
			TabPluginMetaData mTabContentMeta )
	{
		this.mTabContentMetaData = mTabContentMeta;
	}
	
	public TabTitle3D getTabTitle()
	{
		//		if (mTabTitle != null) {
		//			if (mTabTitleDirty) {
		//				return buildTabTitle3D();
		//			}
		//			return mTabTitle;
		//		} else {
		//			return buildTabTitle3D();
		//		}
		if( mTabTitleDirty )
		{
			return null;
		}
		return mTabTitle;
	}
	
	public TabContent3D getTabContent()
	{
		//		if (mTabContent != null) {
		//			if (mTabContentDirty) {
		//				return buildTabContent3D();
		//			}
		//			return mTabContent;
		//		} else {
		//			return buildTabContent3D();
		//		}
		if( mTabContentDirty )
		{
			return null;
		}
		return mTabContent;
	}
	
	public Plugin(
			TabContext tabContext ,
			TabPluginMetaData tabPluginMetaData )
	{
		mTabTitleDirty = true;
		mTabContentDirty = true;
		this.mTabContentMetaData = tabPluginMetaData;
		this.mTabContext = new TabContext();
		this.mTabContext.mContainerContext = tabContext.mContainerContext;
		this.mTabContext.mGdxApplication = tabContext.mGdxApplication;
		this.mTabContext.paramsMap = tabContext.paramsMap;
		Context widgetPluginContext = null;
		try
		{
			widgetPluginContext = mTabContext.mContainerContext.createPackageContext( mTabContentMetaData.packageName , Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY );
			// 设置同launcher语言一�?
			widgetPluginContext.getResources().updateConfiguration( mTabContext.mContainerContext.getResources().getConfiguration() , mTabContext.mContainerContext.getResources().getDisplayMetrics() );
		}
		catch( NameNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mTabContext.mWidgetContext = widgetPluginContext;
		mTabClassLoader = new TabClassLoader( mTabContext , tabPluginMetaData );
	}
	
	public TabTitle3D buildTabTitle3D()
	{
		if( mTabTitle != null && !mTabTitleDirty )
		{
			return mTabTitle;
		}
		else
		{
			if( mTabTitlePlugin == null )
			{
				mTabTitlePlugin = mTabClassLoader.loadTabTitlePlugin();
			}
			TabTitlePlugin3D pluginView = (TabTitlePlugin3D)mTabTitlePlugin.getTabTitle( mTabContext , mTabContentMetaData );
			mTabTitle = new TabTitle3D( mTabContentMetaData.enName , pluginView );
			mTabTitleDirty = false;
		}
		return mTabTitle;
	}
	
	public TabContent3D buildTabContent3D()
	{
		if( mTabContent != null && !mTabContentDirty )
		{
			return mTabContent;
		}
		else
		{
			if( mTabContentPlugin == null )
			{
				mTabContentPlugin = mTabClassLoader.loadTabContentPlugin();
			}
			//			Log.e("test", "test package:"+mTabContentMetaData.packageName+" mTabContentMetaData.classname:"+mTabContentMetaData.className);
			//			Log.e("test", "test mTabContentPlugin:"+mTabContentPlugin); 
			if( mTabContentPlugin != null )
			{
				TabContentPlugin3D pluginView = (TabContentPlugin3D)mTabContentPlugin.getTabContent( mTabContext , mTabContentMetaData );
				mTabContent = new TabContent3D( mTabContentMetaData.enName , pluginView );
				mTabContentDirty = false;
			}
			else
			{
				Log.e( "test" , "buildTabContent3D null package:" + mTabContentMetaData.packageName + " mTabContentMetaData.classname:" + mTabContentMetaData.className );
				for( int i = 0 ; i < 3 ; i++ )
				{
					if( mTabContentPlugin == null )
					{
						mTabContentPlugin = mTabClassLoader.loadTabContentPlugin();
					}
					if( mTabContentPlugin != null )
					{
						TabContentPlugin3D pluginView = (TabContentPlugin3D)mTabContentPlugin.getTabContent( mTabContext , mTabContentMetaData );
						mTabContent = new TabContent3D( mTabContentMetaData.enName , pluginView );
						mTabContentDirty = false;
						break;
					}
					try
					{
						Thread.sleep( 100 );
					}
					catch( InterruptedException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.e( "test" , "buildTabContent3D null testcount:" + i + " package:" + mTabContentMetaData.packageName + " mTabContentMetaData.classname:" + mTabContentMetaData.className );
				}
			}
		}
		return mTabContent;
	}
	
	public void build()
	{
		buildTabTitle3D();
		buildTabContent3D();
	}
	
	public void onDelete()
	{
		this.mTabContent.onDelete();
	}
	
	public void onStart()
	{
		this.mTabContent.onStart();
	}
	
	public void onResume()
	{
		this.mTabContent.onResume();
	}
	
	public void onPause()
	{
		this.mTabContent.onPause();
	}
	
	public void onStop()
	{
		this.mTabContent.onStop();
	}
	
	public void onDestroy()
	{
		this.mTabContent.onDestroy();
		if( mTabClassLoader != null )
		{
			mTabClassLoader.onUninstallPlugin( mTabContentMetaData.packageName );
		}
	}
	
	public void onUninstall()
	{
		if( mTabTitle != null )
		{
			this.mTabTitle.onUninstall();
		}
		if( mTabContent != null )
		{
			this.mTabContent.onUninstall();
		}
		mTabContentMetaData.resolveInfo = null;
		if( mTabClassLoader != null )
		{
			mTabClassLoader.onUninstallPlugin( mTabContentMetaData.packageName );
		}
	}
	
	public ITabTitlePlugin getTabTitlePlugin()
	{
		return mTabTitlePlugin;
	}
	
	public void setTabTitlePlugin(
			ITabTitlePlugin mTabTitlePlugin )
	{
		this.mTabTitlePlugin = mTabTitlePlugin;
	}
	
	public void copyFrom(
			Plugin sourcePlugin )
	{
		this.mTabTitlePlugin = sourcePlugin.mTabTitlePlugin;
		this.mTabContentPlugin = sourcePlugin.mTabContentPlugin;
		this.mTabTitle = sourcePlugin.mTabTitle;
		this.mTabContent = sourcePlugin.mTabContent;
	}
	
	public void dispose()
	{
		mTabTitlePlugin = null;
		mTabContentPlugin = null;
		if( mTabContent != null )
		{
			mTabContent.dispose();
		}
		if( mTabTitle != null )
		{
			mTabTitle.dispose();
		}
		if( mTabClassLoader != null )
		{
			mTabClassLoader.onUninstallPlugin( mTabContentMetaData.packageName );
		}
		mTabContentMetaData = null;
		mTabClassLoader = null;
		mTabTitleDirty = true;
		mTabContentDirty = true;
	}
	
	public void disposeTabContent()
	{
		mTabContentDirty = true;
		if( mTabContent != null )
		{
			mTabContent.dispose();
		}
		if( mTabClassLoader != null )
		{
			mTabClassLoader.onUninstallPlugin( mTabContentMetaData.packageName );
		}
	}
	
	public void disposeTabTitle()
	{
		mTabTitleDirty = true;
		if( mTabTitle != null )
		{
			mTabTitle.dispose();
		}
		if( mTabClassLoader != null )
		{
			mTabClassLoader.onUninstallPlugin( mTabContentMetaData.packageName );
		}
	}
	
	//	public void selected(){
	//		mSelected=true;
	//		if(mTabTitle!=null){
	//			mTabTitle.onEntry();
	//		}
	//		if(mTabContent!=null){
	//			mTabContent.onEntry();
	//		}
	//	}
	public void cancelSelected()
	{
		mSelected = false;
		if( mTabTitle != null )
		{
			mTabTitle.onLeave();
		}
		if( mTabContent != null )
		{
			mTabContent.onLeave();
		}
	}
}
