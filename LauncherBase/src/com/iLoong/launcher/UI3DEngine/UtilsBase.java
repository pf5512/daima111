package com.iLoong.launcher.UI3DEngine;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import com.badlogic.gdx.Gdx;
import com.iLoong.launcher.Desktop3D.Log;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.provider.CallLog.Calls;

import android.view.View;
import android.view.Window;


public class UtilsBase
{
	
	public static Activity activity;
	private static int width = 0;
	private static int height = 0;
	private static int statusBarHeight = 0;
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
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0;
		try
		{
			c = Class.forName( "com.android.internal.R$dimen" );
			obj = c.newInstance();
			field = c.getField( "status_bar_height" );
			x = Integer.parseInt( field.get( obj ).toString() );
			statusBarHeight = res.getDimensionPixelSize( x );
		}
		catch( Exception e1 )
		{
			e1.printStackTrace();
			Rect rect = new Rect();
			View v = activity.getWindow().findViewById( Window.ID_ANDROID_CONTENT );
			v.getWindowVisibleDisplayFrame( rect );
			if( rect.top > 0 )
			{
				statusBarHeight = rect.top;
			}
		}
		return statusBarHeight;
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
			// Log.e("height", "1:"+height);
			return height;
		}
		Resources res = activity.getResources();
		int screenHeight = res.getDisplayMetrics().heightPixels;
		if( ConfigBase.set_status_bar_background )
		{
			height = screenHeight - getStatusBarHeight();
		}
		else
		{
			if( screenHeight == Gdx.graphics.getHeight() )
			{
				height = screenHeight;
			}
			else
			{
				height = screenHeight - getStatusBarHeight();
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
	
	public static long getEarliestCallDate()
	{
		if( earliestCallDate != -1 )
			return earliestCallDate;
		initCallData();
		return earliestCallDate;
	}
	
	public static void initCallData()
	{
		Cursor cursor = activity.getContentResolver().query( Calls.CONTENT_URI , new String[]{ Calls.DURATION , Calls.TYPE , Calls.DATE } , null , null , Calls.DEFAULT_SORT_ORDER );
		long incoming = 0L;
		long outgoing = 0L;
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
							incoming += duration;
							break;
						case Calls.OUTGOING_TYPE:
							outgoing += duration;
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
		totalCallTime = ( incoming + outgoing );
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
}
