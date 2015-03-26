package com.iLoong.launcher.core;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import android.app.Application;

import com.iLoong.launcher.Desktop3D.Log;


public class UEHandler implements Thread.UncaughtExceptionHandler
{
	
	private Application softApp;
	private File fileErrorLog;
	
	public UEHandler(
			Application app ,
			String path )
	{
		softApp = app;
		fileErrorLog = new File( path );
	}
	
	@Override
	public void uncaughtException(
			Thread thread ,
			Throwable ex )
	{
		String info = null;
		ByteArrayOutputStream baos = null;
		PrintStream printStream = null;
		try
		{
			baos = new ByteArrayOutputStream();
			printStream = new PrintStream( baos );
			ex.printStackTrace( printStream );
			byte[] data = baos.toByteArray();
			info = new String( data );
			data = null;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( printStream != null )
				{
					printStream.close();
				}
				if( baos != null )
				{
					baos.close();
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		// print
		long threadId = thread.getId();
		Log.d( "ANDROID_LAB" , "Thread.getName()=" + thread.getName() + " id=" + threadId + " state=" + thread.getState() );
		Log.d( "ANDROID_LAB" , "Error[" + info + "]" );
		if( info.contains( "createWindowSurface" ) )
		{
			Log.e( "" , "GL contain createWindowSurface,return" );
			return;
		}
		if( threadId != 1 )
		{
			android.os.Process.killProcess( android.os.Process.myPid() );
		}
		else
		{
			android.os.Process.killProcess( android.os.Process.myPid() );
		}
	}
	
	private void write2ErrorLog(
			File file ,
			String content )
	{
		FileOutputStream fos = null;
		try
		{
			if( file.exists() )
			{
				// 清空之前的记�?				file.delete();
			}
			else
			{
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
			fos = new FileOutputStream( file );
			fos.write( content.getBytes() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( fos != null )
				{
					fos.close();
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}
}
