package com.iLoong.launcher.CooeePlugin;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.view.View;


public class CooeePluginBridge implements ICooeePlugin
{
	
	Object plugin;
	
	public void setPlugin(
			Object obj )
	{
		this.plugin = obj;
	}
	
	@Override
	public void setContext(
			Context hostContext ,
			Context pluginContext ,
			boolean flag )
	{
		try
		{
			Method method = plugin.getClass().getDeclaredMethod( "setContext" , Context.class , Context.class , boolean.class );
			method.invoke( this.plugin , hostContext , pluginContext , flag );
		}
		catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		}
		catch( NoSuchMethodException e )
		{
			e.printStackTrace();
		}
		catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}
		catch( InvocationTargetException e )
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public int getPermittedCount()
	{
		return 0;
	}
	
	@Override
	public View getPluginView()
	{
		try
		{
			Method method = plugin.getClass().getDeclaredMethod( "getPluginView" );
			return (View)method.invoke( this.plugin );
		}
		catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		}
		catch( NoSuchMethodException e )
		{
			e.printStackTrace();
		}
		catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}
		catch( InvocationTargetException e )
		{
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void releasePluginView()
	{
		try
		{
			Method method = plugin.getClass().getDeclaredMethod( "releasePluginView" );
			method.invoke( this.plugin );
		}
		catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		}
		catch( NoSuchMethodException e )
		{
			e.printStackTrace();
		}
		catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}
		catch( InvocationTargetException e )
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPiflowIn()
	{
		try
		{
			Method method = plugin.getClass().getDeclaredMethod( "onPiflowIn" );
			method.invoke( this.plugin );
		}
		catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		}
		catch( NoSuchMethodException e )
		{
			e.printStackTrace();
		}
		catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}
		catch( InvocationTargetException e )
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPiflowOut()
	{
		try
		{
			Method method = plugin.getClass().getDeclaredMethod( "onPiflowOut" );
			method.invoke( this.plugin );
		}
		catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		}
		catch( NoSuchMethodException e )
		{
			e.printStackTrace();
		}
		catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}
		catch( InvocationTargetException e )
		{
			e.printStackTrace();
		}
	}
}
