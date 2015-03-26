package com.iLoong.launcher.Desktop3D;


import android.content.Intent;


public class CooeeWeather
{
	
	public int nowweather = DrawDynamicIcon.weathernum - 1;
	public String weatherInfo = "无信号";
	private Intent intent;
	public long milis;
	//private String oldweatherInfo = "";
	//private static String CLOSED_UPDATE_LAUNCHER = "com.cooee.weather.Weather.action.CLOSED_UPDATE_LAUNCHER";
	//private static String UPDATE_SUCCES_LAUNCHER = "com.cooee.weather.Weather.action.UPDATE_SUCCES_LAUNCHER";
	private static CooeeWeather mInstance = null;
	
	public CooeeWeather()
	{
		nowweather = getWeatherConditionIndex( weatherInfo );
		mInstance = this;
	}//注册
	
	/*
	 * 获得所有天气数据
	 */
	public void updateWeather(
			final Intent intent )
	{
		// TODO Auto-generated method stub
		milis = intent.getLongExtra( "T0_lastupdatetime" , -1 );
		long diff = 0;
		if( milis == -1 )
		{
			diff = -1;
		}
		else
		{
			diff = System.currentTimeMillis() - milis;
		}
		if( diff < 0 )
			weatherInfo = intent.getStringExtra( "T0_condition" );
		else if( diff < 24 * 60 * 60 * 1000 )
			weatherInfo = intent.getStringExtra( "T0_condition" );
		else if( diff < 48 * 60 * 60 * 1000 )
			weatherInfo = intent.getStringExtra( "T1_condition" );
		else if( diff < 72 * 60 * 60 * 1000 )
			weatherInfo = intent.getStringExtra( "T2_condition" );
		else
			weatherInfo = intent.getStringExtra( "T3_condition" );
		nowweather = getWeatherConditionIndex( weatherInfo );
	}
	
	private int getWeatherConditionIndex(
			String condition )
	{
		int index = 0;
		WeatherCondition.Condition c = WeatherCondition.convertCondition( condition );
		switch( c )
		{
			case WEATHER_FINE:
			case WEATHER_HOT:
				index = 0;
				break;
			case WEATHER_FOG:
			case WEATHER_CLOUDY:
				index = 1;
				break;
			case WEATHER_OVERCAST:
				index = 2;
				break;
			case WEATHER_SNOW:
				index = 3;
				break;
			case WEATHER_THUNDERSTORM:
				index = 4;
				break;
			case WEATHER_SLEET:
			case WEATHER_STORM:
			case WEATHER_LIGHTRAIN:
			case WEATHER_RAIN:
			case WEATHER_RAINSTORM:
				index = 5;
				break;
			case WEATHER_HAZE:
				index = 6;
				break;
			default:
				index = DrawDynamicIcon.weathernum - 1; //默认无信号
				break;
		}
		return index;
	}
	
	public static CooeeWeather getInstance()
	{
		return mInstance;
	}
	
	public int getnowweather()
	{
		return this.nowweather;
	}
}
