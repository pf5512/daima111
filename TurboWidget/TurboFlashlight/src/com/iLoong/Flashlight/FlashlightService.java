package com.iLoong.Flashlight;


import android.app.Service;
import android.content.Intent;
import android.hardware.Camera.Parameters;
import android.os.IBinder;
import android.util.Log;


public class FlashlightService extends Service
{
	
	@Override
	public void onCreate()
	{
		Log.v( "FlashlightService" , "onCreate" );
		super.onCreate();
	}
	
	@Override
	public void onDestroy()
	{
		Log.v( "FlashlightService" , "onDestroy" );
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(
			Intent intent ,
			int flags ,
			int startId )
	{
		Log.v( "FlashlightService" , "onStartCommand" );
		if( intent != null )
		{
			if( intent.getFlags() == 11 )
			{
				turnOn();
			}
		}
		return super.onStartCommand( intent , flags , startId );
	}
	
	@Override
	public IBinder onBind(
			Intent intent )
	{
		Log.v( "FlashlightService" , "onBind" );
		return null;
	}
	
	public void turnOn()
	{
		Log.v( "FlashlightService" , "turnOn" );
		FlashlightApplication.getParameters().setFlashMode( Parameters.FLASH_MODE_TORCH );
		FlashlightApplication.get().setParameters( FlashlightApplication.getParameters() );
	}
}
