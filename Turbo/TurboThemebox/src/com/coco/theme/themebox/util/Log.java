package com.coco.theme.themebox.util;


public final class Log
{
	
	private static boolean bEnableLog = true;
	
	private Log()
	{
	}
	
	public static void setEnableLog(
			boolean bEnable )
	{
		// if (bEnable)
		{
			bEnableLog = bEnable;
		}
	}
	
	public static final int v(
			String tag ,
			String msg )
	{
		int result = 0;
		if( bEnableLog )
		{
			result = android.util.Log.v( tag , msg );
		}
		return result;
	}
	
	public static final int v(
			String tag ,
			String msg ,
			Throwable tr )
	{
		int result = 0;
		if( bEnableLog )
		{
			result = android.util.Log.v( tag , msg , tr );
		}
		return result;
	}
	
	public static final int d(
			String tag ,
			String msg )
	{
		int result = 0;
		if( bEnableLog )
		{
			result = android.util.Log.d( tag , msg );
		}
		return result;
	}
	
	public static final int d(
			String tag ,
			String msg ,
			Throwable tr )
	{
		int result = 0;
		if( bEnableLog )
		{
			result = android.util.Log.d( tag , msg , tr );
		}
		return result;
	}
	
	public static final int i(
			String tag ,
			String msg )
	{
		int result = 0;
		if( bEnableLog )
		{
			result = android.util.Log.i( tag , msg );
		}
		return result;
	}
	
	public static final int i(
			String tag ,
			String msg ,
			Throwable tr )
	{
		int result = 0;
		if( bEnableLog )
		{
			result = android.util.Log.i( tag , msg , tr );
		}
		return result;
	}
	
	public static final int w(
			String tag ,
			String msg )
	{
		int result = 0;
		if( bEnableLog )
		{
			result = android.util.Log.w( tag , msg );
		}
		return result;
	}
	
	public static final int w(
			String tag ,
			String msg ,
			Throwable tr )
	{
		int result = 0;
		if( bEnableLog )
		{
			result = android.util.Log.w( tag , msg , tr );
		}
		return result;
	}
	
	public static final int e(
			String tag ,
			String msg )
	{
		int result = 0;
		if( bEnableLog )
		{
			result = android.util.Log.e( tag , msg );
		}
		return result;
	}
	
	public static final int e(
			String tag ,
			String msg ,
			Throwable tr )
	{
		int result = 0;
		if( bEnableLog )
		{
			result = android.util.Log.e( tag , msg , tr );
		}
		return result;
	}
}
