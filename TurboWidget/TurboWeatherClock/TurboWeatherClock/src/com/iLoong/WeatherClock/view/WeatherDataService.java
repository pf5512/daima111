package com.iLoong.WeatherClock.view;


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

import com.iLoong.WeatherClock.common.CityResult;
import com.iLoong.WeatherClock.view.Weatherwebservice.FLAG_UPDATE;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class WeatherDataService extends IntentService
{
	
	public static final String ACTION_UPDATE_ALL = "com.cooee.weather.dataprovider.UPDATE_ALL";
	public static final String UPDATE_RESULT = "com.cooee.weather.data.action.UPDATE_RESULT";
	
	public WeatherDataService(
			String name )
	{
		super( "WeatherDataService" );
	}
	
	public WeatherDataService()
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
		if( Weatherwebservice.Update_Result_Flag == FLAG_UPDATE.UPDATE_SUCCES )
		{
			intent.putExtra( "cooee.weather.updateResult" , "UPDATE_SUCCESED" );
		}
		else
		{
			intent.putExtra( "cooee.weather.updateResult" , "UPDATE_FAILED" );
		}
		sendBroadcast( intent );
	}
	
	@Override
	protected void onHandleIntent(
			Intent intent )
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		
		String weatherid = sp.getString( "currentweatherId" , null );
		String weathername = sp.getString( "currentweathercityname" , null );
		String weathercountry = sp.getString( "currentweatherCountry" , null );
		
		CityResult cr=new CityResult();
		cr.setWoeid( weatherid );
		cr.setCityName( weathername );
		cr.setCountry( weathercountry );
		
		long now = System.currentTimeMillis();
		if( weatherid != null &&weathername!=null&&weathercountry!=null)
		{
			Weatherwebservice.updateWeatherData( this , cr , sp.getString( "tmpType" , "f" ) );
		}
		else
		{
			Log.d("mytag","天气时钟还没有设置城市");
			Weatherwebservice.Update_Result_Flag = FLAG_UPDATE.WEBSERVICE_ERROR;
		}
		mySendBroadcast();
		Time time = new Time();
		long interval = 60 * 1000 * 60 * 6;
//		long interval = 60 * 1000 * 2;
		if( Weatherwebservice.Update_Result_Flag != FLAG_UPDATE.UPDATE_SUCCES )
		{
			interval = 60 * 1000 * 30;
//			interval = 60 * 1000 * 1;
		}
		time.set( now + interval );
		long nextUpdate = time.toMillis( true );
		Intent updateIntent = new Intent( ACTION_UPDATE_ALL );
		updateIntent.setClass( this , WeatherDataService.class );
		PendingIntent pendingIntent = PendingIntent.getService( this , 0 , updateIntent , 0 );
		AlarmManager alarmManager = (AlarmManager)getSystemService( Context.ALARM_SERVICE );
		alarmManager.set( AlarmManager.RTC_WAKEUP , nextUpdate , pendingIntent );
		stopSelf();
	}
}
