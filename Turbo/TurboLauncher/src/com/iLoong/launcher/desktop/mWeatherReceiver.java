package com.iLoong.launcher.desktop;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;


public class mWeatherReceiver extends BroadcastReceiver
{
	
	private boolean isRegisterReceiver = false;
	private String TAG = "ScreenStateReceiver";
	private static String CLOSED_UPDATE_LAUNCHER = "com.cooee.weather.Weather.action.CLOSED_UPDATE_LAUNCHER";
	private static String UPDATE_SUCCES_LAUNCHER = "com.cooee.weather.Weather.action.UPDATE_SUCCES_LAUNCHER";
	private static String UPDATE_TEXTURE_MINUTE = "android.intent.action.TIME_TICK";
	
	@Override
	public void onReceive(
			Context context ,
			Intent intent )
	{
		String action = intent.getAction();
		PowerManager pm = (PowerManager)context.getSystemService( Context.POWER_SERVICE );
		boolean screen = pm.isScreenOn();
		if( action.equals( CLOSED_UPDATE_LAUNCHER ) )
		{
			if( intent.getStringExtra( "result" ).equals( "ERROR" ) )
			{
				return;
			}
			if( iLoongLauncher.getInstance() != null && iLoongLauncher.getInstance().getCooeeWeather() != null )
			{
				iLoongLauncher.getInstance().getCooeeWeather().updateWeather( intent );
			}
		}
		else if( action.equals( UPDATE_SUCCES_LAUNCHER ) )
		{
			if( iLoongLauncher.getInstance() != null && iLoongLauncher.getInstance().getCooeeWeather() != null )
			{
				iLoongLauncher.getInstance().getCooeeWeather().updateWeather( intent );
				SendMsgToAndroid.updateTextureAtlasDelay( 1000 );
			}
		}
		else if( action.equals( UPDATE_TEXTURE_MINUTE ) && screen )
		{
			SendMsgToAndroid.updateTextureAtlasDelay( 1000 );
		}
	}
	
	public void registerweatherReceiver(
			Context mContext )
	{
		if( !isRegisterReceiver )
		{
			isRegisterReceiver = true;
			IntentFilter filter = new IntentFilter();
			filter.addAction( CLOSED_UPDATE_LAUNCHER );
			filter.addAction( UPDATE_SUCCES_LAUNCHER );
			filter.addAction( UPDATE_TEXTURE_MINUTE );
			mContext.registerReceiver( mWeatherReceiver.this , filter );
		}
	}
	
	public void unRegisterweatherReceiver(
			Context mContext )
	{
		if( isRegisterReceiver )
		{
			isRegisterReceiver = false;
			mContext.unregisterReceiver( mWeatherReceiver.this );
		}
	}
}
