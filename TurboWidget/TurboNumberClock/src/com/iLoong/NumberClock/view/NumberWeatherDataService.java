package com.iLoong.NumberClock.view;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import com.iLoong.NumberClock.common.CityResult;
import com.iLoong.NumberClock.view.NumberWeatherwebservice.FLAG_UPDATE;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class NumberWeatherDataService extends IntentService
{
	
	public static final String ACTION_UPDATE_ALL = "com.cooee.numberweather.dataprovider.UPDATE_ALL";
	public static final String UPDATE_RESULT = "com.cooee.numberweather.data.action.UPDATE_RESULT";
	
	public NumberWeatherDataService(
			String name )
	{
		super( "WeatherDataService" );
	}
	
	public NumberWeatherDataService()
	{
		super( "WeatherDataService" );
	}
	
	@Override
	public IBinder onBind(
			Intent intent )
	{
		return null;
	}
	
	@Override
	public void onCreate()
	{
		//		Log.d( "mytag" , "其服务" );
		super.onCreate();
	}
	
	@Override
	public void onStart(
			Intent intent ,
			int startId )
	{
		super.onStart( intent , startId );
	}
	
	@Override
	public int onStartCommand(
			Intent intent ,
			int flags ,
			int startId )
	{
		super.onStartCommand( intent , flags , startId );
		return START_REDELIVER_INTENT;
	}
	
	public void mySendBroadcast()
	{
		Intent intent = new Intent();
		intent.setAction( UPDATE_RESULT );
		if( NumberWeatherwebservice.Update_Result_Flag == FLAG_UPDATE.UPDATE_SUCCES )
		{
			intent.putExtra( "cooee.numberweather.updateResult" , "UPDATE_SUCCESED" );
		}
		else
		{
			intent.putExtra( "cooee.numberweather.updateResult" , "UPDATE_FAILED" );
		}
		sendBroadcast( intent );
	}
	
	@Override
	protected void onHandleIntent(
			Intent intent )
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		long now = System.currentTimeMillis();
		if( DefaultLayout.enable_google_version )
		{
			String weatherid = sp.getString( "currentnumberweatherId" , null );
			String weathername = sp.getString( "currentnumberweathercityname" , null );
			String weathercountry = sp.getString( "currentnumberweatherCountry" , null );
			CityResult cr = new CityResult();
			cr.setWoeid( weatherid );
			cr.setCityName( weathername );
			cr.setCountry( weathercountry );
			if( weatherid != null && weathername != null && weathercountry != null )
			{
				NumberWeatherwebservice.updateWeatherData( this , cr , sp.getString( "numbertmpType" , "f" ) );
			}
			else
			{
				Log.d( "mytag" , "数字时钟国外还没有设置城市" );
				NumberWeatherwebservice.Update_Result_Flag = FLAG_UPDATE.WEBSERVICE_ERROR;
			}
		}
		else
		{
			String updatename = sp.getString( "currentnumbercityname" , null );
			if( updatename != null )
			{
				NumberWeatherwebservice.updateInLandWeatherData( this , updatename );
			}
			else
			{
				Log.d( "mytag" , "数字时钟国内还没有设置城市" );
				NumberWeatherwebservice.Update_Result_Flag = FLAG_UPDATE.WEBSERVICE_ERROR;
			}
		}
		mySendBroadcast();
		Time time = new Time();
//		long interval = 60 * 1000 * 2;
		long interval = 60 * 1000 * 60 * 6;
		if( NumberWeatherwebservice.Update_Result_Flag != FLAG_UPDATE.UPDATE_SUCCES )
		{
//			interval = 60 * 1000 * 1;
			interval = 60 * 1000 * 30;
		}
		time.set( now + interval );
		long nextUpdate = time.toMillis( true );
		Intent updateIntent = new Intent( ACTION_UPDATE_ALL );
		updateIntent.setClass( this , NumberWeatherDataService.class );
		PendingIntent pendingIntent = PendingIntent.getService( this , 0 , updateIntent , 0 );
		AlarmManager alarmManager = (AlarmManager)getSystemService( Context.ALARM_SERVICE );
		alarmManager.set( AlarmManager.RTC_WAKEUP , nextUpdate , pendingIntent );
		stopSelf();
	}
}
