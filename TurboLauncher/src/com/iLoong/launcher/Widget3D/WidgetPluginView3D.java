package com.iLoong.launcher.Widget3D;


import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.adapter.ICooPluginHostCallback;
import com.iLoong.launcher.UI3DEngine.adapter.IRefreshRender;
import com.iLoong.launcher.action.ActionData;
import com.iLoong.launcher.action.ActionHolder;
import com.iLoong.launcher.core.Assets;
import com.iLoong.launcher.desktop.iLoongLauncher;


public abstract class WidgetPluginView3D extends ViewGroup3D
{
	
	protected IRefreshRender refreshRender = null;
	protected ICooPluginHostCallback cooPluginHostCallback = null;
	protected MainAppContext appContext;
	protected String appid = null;
	protected String mobileSn = null;
	protected int widgetVersion = -1;
	
	public HashMap<String , Object> getParams()
	{
		if( appContext != null )
		{
			return appContext.paramsMap;
		}
		else
		{
			return null;
		}
	}
	
	public WidgetPluginViewMetaData getPluginViewMetaData()
	{
		return null;
	}
	
	public void setCooPluginHostCallback(
			ICooPluginHostCallback cooPluginHostCallback )
	{
		this.cooPluginHostCallback = cooPluginHostCallback;
	}
	
	public IRefreshRender getRefreshRender()
	{
		return refreshRender;
	}
	
	public void setRefreshRender(
			IRefreshRender refreshRender )
	{
		this.refreshRender = refreshRender;
	}
	
	// public WidgetPluginView3D(MainAppContext appContext, String name) {
	// this(name);
	// this.appContext = appContext;
	// registerOnCreate(appContext);
	// }
	public WidgetPluginView3D(
			String name )
	{
		super( name );
		transform = true;
	}
	
	public boolean isOpened()
	{
		return false;
	}
	
	public void onDelete()
	{
		// if (appContext != null) {
		// int instanceCount = Widget3DManager.getInstance()
		// .getWidgetInstanceCount(
		// appContext.mWidgetContext.getPackageName());
		// if (instanceCount == 0) {
		// SharedPreferences prefs = appContext.mContainerContext
		// .getSharedPreferences("widget",
		// Activity.MODE_WORLD_WRITEABLE);
		// String key = appContext.mWidgetContext.getPackageName() + "_"
		// + "firstCreate";
		// prefs.edit().putBoolean(key, true).commit();
		// }
		// }
	}
	public void onChangeSize(float moveX,float moveY,int what,int cellX,int cellY)
	{
	}
	public void onStart()
	{
	}
	
	public void onResume()
	{
		// if (DefaultLayout.enable_content_staistic)
		// registerOnResume();
	}
	
	public void onPause()
	{
	}
	
	public void onStop()
	{
	}
	
	public void onDestroy()
	{
	}
	
	public void onUninstall()
	{
		// if (appContext != null) {
		// SharedPreferences prefs = appContext.mContainerContext
		// .getSharedPreferences("widget",
		// Activity.MODE_WORLD_WRITEABLE);
		// String key = appContext.mWidgetContext.getPackageName() + "_"
		// + "firstCreate";
		// prefs.edit().putBoolean(key, true).commit();
		// }
	}
	
	public void onKeyEvent(
			int keycode ,
			int keyEventCode )
	{
	}
	
	public View getParticalView()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	// xiatian add start //Widget3D adaptation "Naked eye 3D"
	public boolean onIsShowSensor()
	{
		return DefaultLayout.show_sensor;
	}
	
	public boolean isPhoneSupportSensor()
	{
		return DefaultLayout.getInstance().isPhoneSupportSensor();
	}
	
	public boolean onIsOpenSensor()
	{
		return DefaultLayout.getInstance().isOpenSensor();
	}
	
	// xiatian add end
	// teapotXu add start for doov customization
	/**
	 * 
	 * @param obj
	 * @param widgetAnimType
	 *            0:长按菜单拖出widget 1：划屏widget显示 2：从主菜单widget列表拖出来
	 * @param widgetAnimDirection
	 *            1：向左滑动 2：向右滑动
	 * @return
	 */
	public boolean onStartWidgetAnimation(
			Object obj ,
			int widgetAnimType ,
			int widgetAnimDirection )
	{
		return false;
	}
	
	// teapotXu add end
	public void registerOnResume()
	{
		// xiatian add start //StatisticsNew
		new Thread() {
			
			@Override
			public void run()
			{
				if( appContext != null )
				{
					// StatisticsExpandNew
					// .startUp(iLoongLauncher.getInstance(),
					// mobileSn,
					// appid,
					// CooeeSdk.cooeeGetCooeeId(appContext.mWidgetContext),
					// 2, appContext.mWidgetContext
					// .getPackageName(), ""
					// + widgetVersion);
				}
			}
		}.start();
		// xiatian add end
	}
	
	/**
	 * 
	 * @param appContext
	 * @param flag
	 *            0:第一次创建 1：从数据库中读取
	 */
	public void registerOnCreate(
			MainAppContext appContext )
	{
		this.appContext = appContext;
		final MainAppContext finalContext = appContext;
		new Thread() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				try
				{
					SharedPreferences prefs = finalContext.mContainerContext.getSharedPreferences( "widget" , Activity.MODE_WORLD_WRITEABLE );
					String key = finalContext.mWidgetContext.getPackageName() + "_" + "firstCreate";
					boolean firstCreate = prefs.getBoolean( key , true );
					if( firstCreate )
					{
						JSONObject tmp = Assets.config;
						PackageManager mPackageManager = finalContext.mContainerContext.getPackageManager();
						JSONObject config = tmp.getJSONObject( "config" );
						appid = config.getString( "app_id" );
						mobileSn = config.getString( "serialno" );
						widgetVersion = mPackageManager.getPackageInfo( finalContext.mWidgetContext.getPackageName() , 0 ).versionCode;
						prefs.edit().putBoolean( key , false ).commit();
					}
				}
				catch( Exception e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void reset()
	{
		// TODO Auto-generated method stub
	}
	
	public void onThemeChanged()
	{
		// TODO Auto-generated method stub
	}
	
	public enum SERVICE_TYPE{
		SERVICE_GET_CELL_DIMENSION,
		
	};
	
	public Bundle getServiceData(SERVICE_TYPE type){
		Bundle bundle=null;
		switch(type){
			case SERVICE_GET_CELL_DIMENSION:
			   bundle =new Bundle();
			   bundle.putInt( "Workspace_cell_each_width" , R3D.Workspace_cell_each_width );
			   bundle.putInt( "Workspace_cell_each_height" , R3D.Workspace_cell_each_height );
		}
		
		
		return bundle;
	}
	//0：正面
		//1:反面
		public void onClockStateChanged(
				int state )
		{
			if(DefaultLayout.WorkspaceActionGuide){
				if( state == 1 )
				{
					if( ActionHolder.getInstance() != null )
						ActionHolder.getInstance().onClockStateChanged();
				}
			}
			
			
		}
		
		public int checkValidity(){
			if(DefaultLayout.WorkspaceActionGuide){
				 if(ActionData.getInstance()!=null)
					 return ActionData.getInstance().checkValidity();
			}
			return 0;
		}
		public void changeWidgetState(Object state){
			
		}
		public void onCellMove(
			int cellX ,
			int cellY )
	{
		
	}

}
