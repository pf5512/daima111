package com.iLoong.launcher.Widget3D;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class Widget3DHost
{
	
	private static Widget3DHost mWidget3DHostManagerInstance = null;
	private static final Object mSyncObject = new Object();
	// key为packagename，值为Widget3DProvider对象
	private HashMap<String , Widget3DProvider> mProviderHaspMap = null;
	private List<Widget3D> mWidget3DList;
	
	/**
	 * 私有构造函数，无法在外部创建HostManager的实例
	 */
	private Widget3DHost()
	{
		mProviderHaspMap = new HashMap<String , Widget3DHost.Widget3DProvider>();
		mWidget3DList = new ArrayList<Widget3D>();
	}
	
	/**
	 * HostManager获取实例方法
	 * 
	 * @return
	 */
	public static Widget3DHost getInstance()
	{
		if( mWidget3DHostManagerInstance == null )
		{
			synchronized( mSyncObject )
			{
				if( mWidget3DHostManagerInstance == null )
				{
					mWidget3DHostManagerInstance = new Widget3DHost();
				}
			}
		}
		return mWidget3DHostManagerInstance;
	}
	
	/**
	 * 出始化Widget3DHost
	 * 
	 * @param resolveInfoList
	 */
	public void initialize(
			List<ResolveInfo> resolveInfoList )
	{
		Bundle metaData = null;
		for( ResolveInfo resolveInfo : resolveInfoList )
		{
			Widget3DProvider provider = new Widget3DProvider();
			provider.resolveInfo = resolveInfo;
			metaData = resolveInfo.activityInfo.applicationInfo.metaData;
			if( metaData != null )
			{
				provider.spanX = metaData.getInt( "spanX" , -1 );
				provider.spanY = metaData.getInt( "spanY" , -1 );
			}
			if( !mProviderHaspMap.containsKey( resolveInfo.activityInfo.packageName ) )
			{
				SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
				if(sp.getBoolean( resolveInfo.activityInfo.packageName , false )){
					int spanX=sp.getInt( resolveInfo.activityInfo.packageName+":spanX" , -1 );
					int spanY=sp.getInt( resolveInfo.activityInfo.packageName+":spanY" , -1 );
					if(spanX!=-1&&spanY!=-1){
						provider.spanX=spanX;
						provider.spanY=spanY;
					}
				}
				mProviderHaspMap.put( resolveInfo.activityInfo.packageName , provider );
			}
		}
	}
	
	public void updateWidgetInfo(String pkgName,Widget3DProvider provider){
		mProviderHaspMap.put( pkgName , provider );
	}
	
	public ResolveInfo getResolveInfo(
			String packageName )
	{
		if( mProviderHaspMap != null )
		{
			Widget3DProvider provider = mProviderHaspMap.get( packageName );
			if( provider != null )
			{
				return provider.resolveInfo;
			}
			else
			{
				return null;
			}
		}
		return null;
	}
	
	public void addWidget3DProvider(
			String packageName ,
			Widget3DProvider provider )
	{
		if( !mProviderHaspMap.containsKey( packageName ) )
		{
			SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
			if(sp.getBoolean( packageName , false )){
				int spanX=sp.getInt( packageName+":spanX" , -1 );
				int spanY=sp.getInt( packageName+":spanY" , -1 );
				if(spanX!=-1&&spanY!=-1){
					provider.spanX=spanX;
					provider.spanY=spanY;
				}
			}
			mProviderHaspMap.put( packageName , provider );
		}
	}
	
	public boolean containsWidget3DProvider(
			String packageName )
	{
		return mProviderHaspMap.containsKey( packageName );
	}
	
	public Widget3DProvider getWidget3DProvider(
			String packageName )
	{
		return mProviderHaspMap.get( packageName );
	}
	
	public void removeWidget3D(
			String packageName )
	{
		mProviderHaspMap.remove( packageName );
	}
	
	public void uninstallWidget3D(
			String packageName )
	{
		mProviderHaspMap.remove( packageName );
		List<Widget3D> deleteIds = new ArrayList<Widget3D>();
		for( int i = 0 ; i < mWidget3DList.size() ; i++ )
		{
			if( mWidget3DList.get( i ).getPackageName().equals( packageName ) )
			{
				deleteIds.add( mWidget3DList.get( i ) );
			}
		}
		if( deleteIds != null && deleteIds.size() > 0 )
		{
			mWidget3DList.removeAll( deleteIds );
		}
	}
	
	/**
	 * 根据相应的规则生成WiegetID
	 * 
	 * @param packageName
	 * @return
	 */
	public int generateWidgetID(
			String packageName )
	{
		if( mProviderHaspMap.containsKey( packageName ) )
		{
			Widget3DProvider provider = mProviderHaspMap.get( packageName );
			return provider.generateWidgetId();
		}
		else
		{
			return -1;
		}
	}
	
	public void addWidget3D(
			Widget3D widget )
	{
		if( !this.mWidget3DList.contains( widget ) )
		{
			this.mWidget3DList.add( widget );
		}
	}
	
	public Widget3D getWidget3D(
			String packageName )
	{
		Widget3D widget = null;
		for( int i = 0 ; i < mWidget3DList.size() ; i++ )
		{
			if( mWidget3DList.get( i ).getPackageName().equals( packageName ) )
			{
				widget = mWidget3DList.get( i );
				break;
			}
		}
		return widget;
	}
	
	public boolean containsWidget(
			Widget3D widget )
	{
		if( this.mWidget3DList == null )
		{
			return false;
		}
		return this.mWidget3DList.contains( widget );
	}
	
	public void deleteWidget3DInstance(
			Widget3D widget )
	{
		if( this.mWidget3DList.contains( widget ) )
		{
			this.mWidget3DList.remove( widget );
		}
	}
	
	public List<Widget3D> getWidget3DInstanceList()
	{
		return this.mWidget3DList;
	}
	
	public Widget3DProvider newWidget3DProvider()
	{
		return new Widget3DProvider();
	}
	
	/**
	 * widget源数据信息以及此源数据信息下的最大的widgetId
	 * 
	 * @author Administrator
	 * 
	 */
	public class Widget3DProvider
	{
		
		ResolveInfo resolveInfo;
		int currentMaxWidgetId = 0;
		int instanceCount = 0;
		int maxInstanceCount = -1;
		public int spanX;
		public int spanY;
		
		public int generateWidgetId()
		{
			instanceCount++;
			// Log.v("generateWidgetId", "instanceCount:" + instanceCount);
			return ++currentMaxWidgetId;
		}
		
		public void deleteWidgetId()
		{
			if( instanceCount <= 0 )
			{
				instanceCount = 0;
			}
			else
			{
				instanceCount--;
			}
			Log.e( "Widget3DHost" , "instanceCount:" + instanceCount );
		}
		
		public void addWidgetId(
				int widgetId )
		{
			instanceCount++;
			currentMaxWidgetId = currentMaxWidgetId > widgetId ? currentMaxWidgetId : widgetId;
			// Log.v("addWidgetId", "instanceCount:" + instanceCount);
		}
	}
}
