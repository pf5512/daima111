package com.coco.theme.themebox;


import java.lang.reflect.Method;

import android.content.Context;


public final class StatusBarUtils
{// 状态栏工具
	
	private static final String STATUS_BAR_CLASS_NAME = "android.app.StatusBarManager";
	private static final String SET_STATUS_BAR_TRANSPARENT_METHOD = "setStatusBarBackgroundTransparent";
	private static final String STATUS_BAR_SERVICE = "statusbar";
	
	private static Method loadSetStatusBarBgTransparentMethod()
	{// 加载设置状态栏透明度的方法
		Method method = null;
		Class<?> cls = null;
		try
		{
			cls = Class.forName( STATUS_BAR_CLASS_NAME );
			method = cls.getMethod( SET_STATUS_BAR_TRANSPARENT_METHOD , boolean.class );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return method;
	}
	
	/** 设置状态栏透明度 **/
	public static void setStatusBarBackgroundTransparent(
			final Context context ,
			boolean transparent )
	{
		Method method = loadSetStatusBarBgTransparentMethod();
		if( method == null )
		{
			return;
		}
		try
		{
			method.invoke( context.getSystemService( STATUS_BAR_SERVICE ) , transparent );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}
