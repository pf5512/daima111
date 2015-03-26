package com.iLoong.launcher.UI3DEngine;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.CallLog.Calls;
import android.view.ViewConfiguration;

import com.badlogic.gdx.Gdx;
import com.iLoong.launcher.Desktop3D.Log;


public class UtilsBase
{
	
	public static Activity activity;
	private static int width = 0;
	private static int height = 0;
	private static int statusBarHeight = 0;
	private static int navigationBarHeight = 0;
	public static long resumeTime;
	public static long pauseTime;
	private static long totalCallTime = -1;
	private static long earliestCallDate = -1;
	static Object exe_lock = new Object();
	
	public static void init(
			Activity _activity )
	{
		activity = _activity;
	}
	
	public static int getStatusBarHeight()
	{
		if( statusBarHeight != 0 )
			return statusBarHeight;
		Resources res = activity.getResources();
		int resourceId = res.getIdentifier( "status_bar_height" , "dimen" , "android" );
		if( resourceId > 0 )
		{
			statusBarHeight = res.getDimensionPixelSize( resourceId );
		}
		return statusBarHeight;
	}
	
	public static int getNavigationBarHeight()
	{
		if( navigationBarHeight != 0 )
			return navigationBarHeight;
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
		{
			if( !ViewConfiguration.get( activity ).hasPermanentMenuKey() )
			{
				int resourceId = activity.getResources().getIdentifier( "navigation_bar_height" , "dimen" , "android" );
				if( resourceId > 0 )
				{
					navigationBarHeight = activity.getResources().getDimensionPixelSize( resourceId );
				}
			}
		}
		return navigationBarHeight;
	}
	
	// fuck the魅族的SmartBar
	public static boolean hasMeiZuSmartBar()
	{
		boolean result = false;
		try
		{
			// 新型号可用反射调用Build.hasSmartBar()
			Method method = Class.forName( "android.os.Build" ).getMethod( "hasSmartBar" );
			return ( (Boolean)method.invoke( null ) ).booleanValue();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		// 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
		if( Build.DEVICE.equals( "mx2" ) )
		{
			result = true;
		}
		else if( Build.DEVICE.equals( "mx" ) || Build.DEVICE.equals( "m9" ) )
		{
			result = false;
		}
		return result;
	}
	
	public static int getScreenWidth()
	{
		if( width != 0 )
			return width;
		Resources res = activity.getResources();
		width = res.getDisplayMetrics().widthPixels;
		return width;
	}
	
	public static int getScreenHeight()
	{
		if( height != 0 )
		{
			return height;
		}
		Resources res = activity.getResources();
		int screenHeight = res.getDisplayMetrics().heightPixels;
		if( ConfigBase.set_status_bar_background )
		{
			height = screenHeight;
		}
		else
		{
			if( screenHeight == Gdx.graphics.getHeight() )
			{
				height = screenHeight;
			}
			else
			{
				height = screenHeight;
			}
		}
		return height;
	}
	
	public static int getDeviceHeightPixels()
	{
		Resources res = activity.getResources();
		return res.getDisplayMetrics().heightPixels;
	}
	
	public static void resetSize()
	{
		width = 0;
		height = 0;
	}
	
	public static boolean saveBmp(
			Bitmap thumbnail ,
			String name )
	{
		// TODO Auto-generated method stub
		File f = new File( "/mnt/sdcard/" + name + ".png" );
		try
		{
			f.createNewFile();
		}
		catch( IOException e1 )
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		FileOutputStream fOut = null;
		try
		{
			fOut = new FileOutputStream( f );
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		if( fOut == null )
			return false;
		thumbnail.compress( Bitmap.CompressFormat.PNG , 100 , fOut );
		try
		{
			fOut.flush();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		try
		{
			fOut.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public static long getTotalCallTime()
	{
		if( totalCallTime != -1 )
			return totalCallTime;
		initCallData();
		return totalCallTime;
	}
	
	public static long getTotalCallTime(
			long max_totalCallTime )
	{
		if( totalCallTime != -1 && totalCallTime >= max_totalCallTime )
			return totalCallTime;
		initCallData();
		return totalCallTime;
	}
	
	public static long getEarliestCallDate()
	{
		if( earliestCallDate != -1 )
			return earliestCallDate;
		initCallData();
		return earliestCallDate;
	}
	
	public static long getEarliestCallDate(
			long max_callDateInterval )
	{
		long currentTimeMillis = System.currentTimeMillis();
		if( earliestCallDate != -1 && ( currentTimeMillis - earliestCallDate ) >= max_callDateInterval )
			return earliestCallDate;
		initCallData();
		return earliestCallDate;
	}
	
	public static void initCallData()
	{
		Cursor cursor = null;
		try
		{
			cursor = activity.getContentResolver().query( Calls.CONTENT_URI , new String[]{ Calls.DURATION , Calls.TYPE , Calls.DATE } , null , null , Calls.DEFAULT_SORT_ORDER );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return;
		}
		//		long incoming = 0L;
		//		long outgoing = 0L;
		if( cursor != null )
		{
			try
			{
				boolean hasRecord = cursor.moveToFirst();
				while( hasRecord )
				{
					int type = cursor.getInt( cursor.getColumnIndex( Calls.TYPE ) );
					long duration = cursor.getLong( cursor.getColumnIndex( Calls.DURATION ) );
					long date = cursor.getLong( cursor.getColumnIndex( Calls.DATE ) );
					switch( type )
					{
						case Calls.INCOMING_TYPE:
							//						incoming += duration;
							//						break;
						case Calls.OUTGOING_TYPE:
							//						outgoing += duration;
							if( totalCallTime == -1 )
							{
								totalCallTime = 0;
							}
							totalCallTime += duration;
						default:
							break;
					}
					if( date < earliestCallDate )
						earliestCallDate = date;
					if( earliestCallDate == -1 )
						earliestCallDate = date;
					hasRecord = cursor.moveToNext();
				}
			}
			finally
			{
				cursor.close();
			}
		}
		//		totalCallTime = (incoming + outgoing);
		Log.v( "call" , "callog time=" + totalCallTime + " date=" + earliestCallDate );
	}
	
	static public String sync_do_exec(
			String cmd )
	{
		String s = "\n";
		try
		{
			java.lang.Process p = Runtime.getRuntime().exec( cmd );
			BufferedReader in = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
			String line = null;
			while( ( line = in.readLine() ) != null )
			{
				s += line + "\n";
			}
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	static public boolean do_exec(
			final String cmd ,
			String packageName )
	{
		boolean success = false;
		new Thread() {
			
			@Override
			public void run()
			{
				String s = "\n";
				try
				{
					java.lang.Process p = Runtime.getRuntime().exec( cmd );
					BufferedReader in = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
					String line = null;
					while( ( line = in.readLine() ) != null )
					{
						s += line + "\n";
					}
				}
				catch( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.run();
				synchronized( exe_lock )
				{
					Log.d( "apk" , "exe_lock notify" );
					exe_lock.notify();
				}
			}
		}.start();
		int i = 0;
		PackageManager pm = activity.getPackageManager();
		synchronized( exe_lock )
		{
			while( !success && i < 12 )
			{
				Log.d( "apk" , "exe_lock wait" );
				try
				{
					exe_lock.wait( 10000 );
					Log.d( "apk" , "exe_lock wait finish" );
				}
				catch( InterruptedException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if( packageName != null )
				{
					try
					{
						pm.getPackageInfo( packageName , PackageManager.GET_ACTIVITIES );
						Log.e( "apk" , "has install,do not wait:" + packageName );
						success = true;
						break;
					}
					catch( Exception e )
					{
						Log.e( "apk" , "wait again:" + packageName );
					}
				}
				i++;
			}
		}
		return success;
	}
	
	//xujin 
	/**
	 * 根据api level 绑定widget
	 * @param widget AppWidgetManager
	 * @param appWidgetId widgetID
	 * @param provider 组建名
	 */
	public static void bindAppWidgetId(
			AppWidgetManager widget ,
			int appWidgetId ,
			ComponentName provider )
	{
		if( widget == null || provider == null )
		{
			return;
		}
		//api 15以下方法
		widget.bindAppWidgetId( appWidgetId , provider );
		//内置发生错误，普通app可以通过
		//		widget.bindAppWidgetIdIfAllowed( appWidgetId , provider );
		//		if( Build.VERSION.SDK_INT >= 16 )
		//		{
		//			widget.bindAppWidgetIdIfAllowed( appWidgetId , provider );
		//		}
		//		else
		//		{
		//			try
		//			{
		//				Class<?> clz = widget.getClass();
		//				Method method = clz.getMethod( "bindAppWidgetId" , Integer.class , ComponentName.class );
		//				method.invoke( widget , appWidgetId , provider );
		//			}
		//			catch( NoSuchMethodException e )
		//			{
		//				Log.e( "jinxu" , e.getLocalizedMessage() );
		//				e.printStackTrace();
		//			}
		//			catch( IllegalArgumentException e )
		//			{
		//				Log.e( "jinxu" , e.getLocalizedMessage() );
		//				e.printStackTrace();
		//			}
		//			catch( IllegalAccessException e )
		//			{
		//				Log.e( "jinxu" , e.getLocalizedMessage() );
		//				e.printStackTrace();
		//			}
		//			catch( InvocationTargetException e )
		//			{
		//				Log.e( "jinxu" , e.getLocalizedMessage() );
		//				e.printStackTrace();
		//			}
		//		}
	}
}
