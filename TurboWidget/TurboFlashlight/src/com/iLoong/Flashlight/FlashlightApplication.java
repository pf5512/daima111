package com.iLoong.Flashlight;


import android.app.Application;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;


public class FlashlightApplication extends Application
{
	
	private static Camera camera;
	private static Camera.Parameters parameters;
	
	public static Camera get()
	{
		if( camera == null )
		{
			camera = Camera.open();
			parameters = camera.getParameters();
			camera.startPreview();
		}
		return camera;
	}
	
	public static Parameters getParameters()
	{
		return parameters;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
	}
	
	@Override
	public void onTerminate()
	{
		super.onTerminate();
		release();
	}
	
	public static void release()
	{
		if( camera != null )
		{
			camera.release();
			camera = null;
		}
	}
}
