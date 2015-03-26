package com.iLoong.Flashlight;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.widget.RemoteViews;

import com.cooeeui.turboflashlight.R;


public class Flashlight extends AppWidgetProvider
{
	
	private static boolean isOn = false;
	private static boolean isFirst = true;
	
	public Flashlight()
	{
		Log.i( "MyFlashlight" , "construction" );
	}
	
	private void turnOn()
	{
		Parameters params = FlashlightApplication.get().getParameters();
		params.setFlashMode( Parameters.FLASH_MODE_TORCH );
		FlashlightApplication.get().setParameters( params );
		isOn = true;
	}
	
	private void turnOff()
	{
		Parameters params = FlashlightApplication.get().getParameters();
		params.setFlashMode( Parameters.FLASH_MODE_OFF );
		FlashlightApplication.get().setParameters( params );
		FlashlightApplication.get().stopPreview();
		FlashlightApplication.release();
		isOn = false;
	}
	
	@Override
	public void onReceive(
			Context context ,
			Intent intent )
	{
		Log.i( "MyFlashlight" , "onReceive" );
		if( intent.getAction().equals( "com.flashlight.click" ) )
		{
			Log.i( "MyFlashlight" , "onClick" );
			if( isFirst )
			{
				isFirst = false;
			}
			if( !isOn )
			{
				turnOn();
			}
			else
			{
				turnOff();
			}
		}
		if( intent.getAction().equals( "com.flashlight.inform.widget" ) && !isFirst )
		{
			Log.i( "MyFlashlight" , "inform" );
		}
		super.onReceive( context , intent );
	}
	
	@Override
	public void onUpdate(
			Context context ,
			AppWidgetManager appWidgetManager ,
			int[] appWidgetIds )
	{
		final int N = appWidgetIds.length;
		for( int i = 0 ; i < N ; i++ )
		{
			int appWidgetId = appWidgetIds[i];
			Log.i( "MyFlashlight" , "this is [" + appWidgetId + "] onUpdate" );
		}
		Intent intent = new Intent( "com.flashlight.click" );
		PendingIntent pending = PendingIntent.getBroadcast( context , 0 , intent , 0 );
		RemoteViews rv = new RemoteViews( context.getPackageName() , R.layout.flashlight_widget );
		rv.setOnClickPendingIntent( R.id.iv_widget_img , pending );
		appWidgetManager.updateAppWidget( appWidgetIds , rv );
	}
	
	@Override
	public void onDeleted(
			Context context ,
			int[] appWidgetIds )
	{
		final int N = appWidgetIds.length;
		for( int i = 0 ; i < N ; i++ )
		{
			int appWidgetId = appWidgetIds[i];
			Log.i( "MyFlashlight" , "this is [" + appWidgetId + "] onDeleted" );
		}
	}
	
	@Override
	public void onDisabled(
			Context context )
	{
		Log.i( "MyFlashlight" , "onDisabled" );
		super.onDisabled( context );
	}
	
	@Override
	public void onEnabled(
			Context context )
	{
		Log.i( "MyFlashlight" , "onEnabled" );
		super.onEnabled( context );
	}
}
