package com.iLoong.launcher.Widget3D;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.widget.ScrollView;

import com.iLoong.launcher.CooeePlugin.CooeePluginHostView;
import com.iLoong.launcher.CooeePlugin.CooeePluginManager;
import com.iLoong.launcher.data.CooeePluginInfo;


public class NewsFlowReflect
{
	
	private static CooeePluginHostView newsView;
	private static NewsFlowReflect instance;
	
	public static synchronized int getNewChannelValue()
	{
		Class<?> intent = null;
		try
		{
			intent = Class.forName( "com.inveno.newpiflow.widget.PiSetLinerLayout" );
			Field field = intent.getDeclaredField( "CHANNEL_REQUEST_CODE" );
			int s = (Integer)field.get( intent );
			return s;
		}
		catch( Exception e1 )
		{
			e1.printStackTrace();
		}
		return 0;
	}
	
	public static synchronized void setfinishstate()
	{
		Class<?> intent = null;
		try
		{
			intent = Class.forName( "com.inveno.newpiflow.widget.PiSetLinerLayout" );
			Method m = intent.getDeclaredMethod( "setfinishstate" );
			m.invoke( null );
		}
		catch( Exception e1 )
		{
			e1.printStackTrace();
		}
	}
	
	public static synchronized ScrollView PiScrollView(
			Context context )
	{
		Class<?> intent = null;
		ScrollView obj = null;
		try
		{
			intent = Class.forName( "com.inveno.newpiflow.widget.PiScrollView" );
			Constructor<?> constructor = intent.getConstructor( Context.class );
			obj = (ScrollView)constructor.newInstance( context );
			return obj;
		}
		catch( Exception e1 )
		{
			e1.printStackTrace();
		}
		return null;
	}
	
	public static synchronized boolean PiScrollViewExist()
	{
		Class<?> intent = null;
		try
		{
			intent = Class.forName( "com.inveno.newpiflow.widget.PiScrollView" );
			if( intent != null )
			{
				return true;
			}
		}
		catch( Exception e1 )
		{
			e1.printStackTrace();
		}
		return false;
	}
	
	public static synchronized void piScrollViewclearAll(
			ScrollView piScrollView )
	{
		Class<?> intent = null;
		try
		{
			intent = Class.forName( "com.inveno.newpiflow.widget.PiScrollView" );
			Method m = intent.getDeclaredMethod( "clearAll" );
			m.invoke( piScrollView );
			return;
		}
		catch( Exception e1 )
		{
			e1.printStackTrace();
		}
		return;
	}
	
	public static synchronized void DeviceConfigSetHeightAndWidth(
			int widthPixels ,
			int heightPixels ,
			Context context )
	{
		Class<?> intent = null;
		Object obj = null;
		Method m = null;
		try
		{
			intent = Class.forName( "com.inveno.newpiflow.tools.DeviceConfig" );
			m = intent.getDeclaredMethod( "getInstance" , Context.class );
			obj = m.invoke( null , context );
			m = intent.getMethod( "setHeight" , int.class );
			m.invoke( obj , new Object[]{ heightPixels } );
			m = intent.getMethod( "setWidth" , int.class );
			m.invoke( obj , new Object[]{ widthPixels } );
			return;
		}
		catch( Exception e1 )
		{
			e1.printStackTrace();
		}
		return;
	}
	
	public static NewsFlowReflect getInstance()
	{
		if( instance == null )
		{
			instance = new NewsFlowReflect();
		}
		return instance;
	}
	
	public static CooeePluginHostView getNewsFlow(
			Context context )
	{
		//		if( newsView == null )
		{
			CooeePluginManager cooeemgr = CooeePluginManager.getInstance( context );
			CooeePluginInfo newsinfo = new CooeePluginInfo();
			newsinfo.setInfo( "com.inveno.newpiflow" , null );
			CooeePluginHostView newslisthostView = cooeemgr.getHostView( newsinfo );
			return newslisthostView;
		}
		//		return newsView;
	}
	
	public boolean removeNewsFlow()
	{
		if( newsView != null )
		{
			newsView.releasePluginView();
			newsView = null;
			return true;
		}
		return false;
	}
	
	public void onPiflowIn()
	{
		if( newsView != null )
		{
			newsView.onPiflowIn();
		}
	}
	
	public void onPiflowOut()
	{
		if( newsView != null )
		{
			newsView.onPiflowOut();
		}
	}
}
