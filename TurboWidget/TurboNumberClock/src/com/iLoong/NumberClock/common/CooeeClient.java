package com.iLoong.NumberClock.common;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.content.Context;
import android.os.Bundle;

import com.iLoong.NumberClock.R;
import com.iLoong.NumberClock.view.NumberCityFindInLand;
import com.iLoong.NumberClock.view.WidgetNumberClock;


public class CooeeClient
{
	
	private static final int TIMEOUT_CONNECT = 10 * 1000;//设置请求超时10秒钟  
	private static final int TIMEOUT_SOCKET = 30 * 1000; //设置等待数据超时时间10秒钟 
	public final static String COOEE_FORCAST_URL = "http://widget.coeeland.com/w2/tianqi2.ashx?city=%s&m=2603&l=320480&f=2424&s=B01_HVGA&imsi=460008623197253&sc=+8613800210500&iccid=898600810910f6287253";
	private static String Update_city = null;
	
	public static WeatherEntity getWeatherInfo(
			Context context ,
			String city_num ,
			int what )
	{
		WeatherEntity dataentity = null;
		if( city_num == null )
		{
			return null;
		}
		Update_city = city_num;
		if( NumberClockHelper.isHaveInternet( context ) )
		{
			Reader responseReader = null;
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout( httpParameters , TIMEOUT_CONNECT );
			HttpConnectionParams.setSoTimeout( httpParameters , TIMEOUT_SOCKET );
			HttpClient client = new DefaultHttpClient( httpParameters );
			try
			{
				String encode_city = URLEncoder.encode( city_num , "UTF-8" );
				HttpGet request = new HttpGet( String.format( COOEE_FORCAST_URL , encode_city ) );
				HttpResponse response = client.execute( request );
				int status = response.getStatusLine().getStatusCode();
				if( status == 200 )
				{
					HttpEntity entity = response.getEntity();
					Header header = response.getFirstHeader( "Date" );
					Date date = null;
					long timeUTC = 0;
					if( header == null )
					{
						date = new Date( System.currentTimeMillis() );
						timeUTC = date.getTime();
					}
					else
					{
						date = new Date( header.getValue() );
						timeUTC = date.getTime();
					}
					responseReader = new InputStreamReader( entity.getContent() , "GB2312" );
					dataentity = CooeeWeatherParseWeatherData( context , responseReader , timeUTC );
					if( what == 1 )
					{
						if( dataentity != null && dataentity.getDetails() != null && dataentity.getDetails().size() == 5 )
						{
							Bundle bundle = new Bundle();
							bundle.putSerializable( "weatherdataentity" , dataentity );
							if( NumberCityFindInLand.mHandler != null )
							{
								NumberCityFindInLand.mHandler.obtainMessage( NumberCityFindInLand.MSG_SUCCESS , bundle ).sendToTarget();
							}
						}
						else
						{
							if( NumberCityFindInLand.mHandler != null )
							{
								NumberCityFindInLand.mHandler.obtainMessage( NumberCityFindInLand.MSG_FAILURE ).sendToTarget();
							}
						}
					}
					else if( what == 2 )
					{
						if( dataentity != null && dataentity.getDetails() != null && dataentity.getDetails().size() == 5 )
						{
							Bundle bundle = new Bundle();
							bundle.putSerializable( "weatherupdatedataentity" , dataentity );
							if( WidgetNumberClock.updatehandle != null )
							{
								WidgetNumberClock.updatehandle.obtainMessage( WidgetNumberClock.MSG_UpdateInlandSUCCESS , bundle ).sendToTarget();
							}
						}
						else
						{
							if(WidgetNumberClock.updatehandle != null )
							{
								WidgetNumberClock.updatehandle.obtainMessage( WidgetNumberClock.MSG_UpdateInlandFAILURE ).sendToTarget();
							}
						}
					}
				}
				else
				{
					if( NumberCityFindInLand.mHandler != null )
					{
						NumberCityFindInLand.mHandler.obtainMessage( NumberCityFindInLand.MSG_FAILURE ).sendToTarget();
					}else if(WidgetNumberClock.updatehandle != null )
					{
						WidgetNumberClock.updatehandle.obtainMessage( WidgetNumberClock.MSG_UpdateInlandFAILURE ).sendToTarget();
					}
				}
				client.getConnectionManager().shutdown();
			}
			catch( Exception e )
			{
				e.printStackTrace();
				if( NumberCityFindInLand.mHandler != null )
				{
					NumberCityFindInLand.mHandler.obtainMessage( NumberCityFindInLand.MSG_FAILURE ).sendToTarget();
				}else if(WidgetNumberClock.updatehandle != null )
				{
					WidgetNumberClock.updatehandle.obtainMessage( WidgetNumberClock.MSG_UpdateInlandFAILURE ).sendToTarget();
				}
			}
		}
		else
		{
			if( NumberCityFindInLand.mHandler != null )
			{
				NumberCityFindInLand.mHandler.obtainMessage( NumberCityFindInLand.MSG_FAILURE ).sendToTarget();
			}else if(WidgetNumberClock.updatehandle != null )
			{
				WidgetNumberClock.updatehandle.obtainMessage( WidgetNumberClock.MSG_UpdateInlandFAILURE ).sendToTarget();
			}
		}
		return dataentity;
	}
	
	public static WeatherEntity CooeeWeatherParseWeatherData(
			Context context ,
			Reader responseReader ,
			long time )
	{
		WeatherEntity dataEntity = new WeatherEntity();
		WeatherForestEntity forecastentity = null;
		char[] buffer = new char[1024];
		try
		{
			responseReader.read( buffer );
			if( buffer.length > 100 )
			{
				String buf = new String( buffer );
				String s[] = buf.split( "</it>" );
				queryCurWeatherData( s[0] , dataEntity , time );
				for( int i = 0 ; i < s.length - 1 ; i++ )
				{
					forecastentity = cooeeForcastDataQuery( s[i] );
					dataEntity.getDetails().add( forecastentity );
				}
			}
			else
			{
				if( NumberCityFindInLand.mHandler != null )
				{
					NumberCityFindInLand.mHandler.obtainMessage( NumberCityFindInLand.MSG_FAILURE ).sendToTarget();
				}else if(WidgetNumberClock.updatehandle != null )
				{
					WidgetNumberClock.updatehandle.obtainMessage( WidgetNumberClock.MSG_UpdateInlandFAILURE ).sendToTarget();
				}
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
			if( NumberCityFindInLand.mHandler != null )
			{
				NumberCityFindInLand.mHandler.obtainMessage( NumberCityFindInLand.MSG_FAILURE ).sendToTarget();
			}else if(WidgetNumberClock.updatehandle != null )
			{
				WidgetNumberClock.updatehandle.obtainMessage( WidgetNumberClock.MSG_UpdateInlandFAILURE ).sendToTarget();
			}
		}
		return dataEntity;
	}
	
	public static void queryCurWeatherData(
			String s ,
			WeatherEntity dataEntity ,
			long time )
	{
		dataEntity.setCity( Update_city );
		dataEntity.setPostalCode( Update_city );
		String c1[] = s.split( "<wd>" );
		String c2[] = c1[1].split( "</wd>" );
		CooeeCurFormatTemprature( dataEntity , c2[0] );
		String t1[] = s.split( "<tq>" );
		String t2[] = t1[1].split( "</tq>" );
		getCondition( t2[0] , dataEntity );
		getWindData( s , dataEntity );
		getHumidity( s , dataEntity );
		getLunarCalendar( s , dataEntity );
		dataEntity.setTempC( CalTempC( time , Integer.parseInt( dataEntity.getTempH() ) , Integer.parseInt( dataEntity.getTempL() ) ) );
	}
	
	private static void getLunarCalendar(
			String s ,
			WeatherEntity dataEntity )
	{
		String lunar1[] = s.split( "<nl>" );
		String lunar2[] = lunar1[1].split( "</nl>" );
		dataEntity.setLunarcalendar( lunar2[0] );
	}
	
	private static void getHumidity(
			String s ,
			WeatherEntity dataEntity )
	{
		String humidity1[] = s.split( "<sd>" );
		String humidity2[] = humidity1[1].split( "</sd>" );
		dataEntity.setHumidity( humidity2[0] );
	}
	
	private static void getWindData(
			String s ,
			WeatherEntity dataEntity )
	{
		String wind_type1[] = s.split( "<fx>" );
		String wind_type2[] = wind_type1[1].split( "</fx>" );
		String wind_type[] = wind_type2[0].split( "," );
		String wind_power1[] = s.split( "<fl>" );
		String wind_power2[] = wind_power1[1].split( "</fl>" );
		String wind_power[] = wind_power2[0].split( "," );
		String winddatas = "";
		if( wind_type.length == wind_power.length )
		{
			for( int i = 0 ; i < wind_type.length ; i++ )
			{
				if( i == ( wind_type.length - 1 ) )
				{
					winddatas += wind_type[i] + "," + wind_power[i];
				}
				else
				{
					winddatas += wind_type[i] + "," + wind_power[i] + ";";
				}
			}
		}
		dataEntity.setWindCondition( winddatas );
	}
	
	public static void getCondition(
			String s ,
			WeatherEntity dataEntity )
	{
		String t[] = s.split( "\\," );
		if( t[0].equals( s ) )
		{
			dataEntity.setCondition( t[0] );
		}
		else
		{
			Date dates = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime( dates );
			int hour = cal.get( Calendar.HOUR_OF_DAY );
			if( hour >= 0 && hour < 18 )
			{
				dataEntity.setCondition( t[0] );
			}
			else
			{
				dataEntity.setCondition( t[1] );
			}
		}
	}
	
	public static void CooeeCurFormatTemprature(
			WeatherEntity dataEntity ,
			String temp )
	{
		String[] s = temp.split( "\\," );
		String high = s[1];
		String low = s[0];
		dataEntity.setTempL( low );
		dataEntity.setTempH( high );
	}
	
	private static Integer CalTempC(
			final long ld ,
			Integer tempH ,
			Integer tempL )
	{
		Integer tempC = 0;
		Calendar cal = Calendar.getInstance();
		Date date = new Date( ld );
		cal.setTime( date );
		int hour = cal.get( Calendar.HOUR_OF_DAY );
		float f;
		if( hour >= 0 && hour < 6 )
		{
			tempC = tempL;
		}
		else if( hour >= 6 && hour <= 14 )
		{
			f = tempH - tempL;
			f = f / ( 14 - 5 ) * ( hour - 5 );
			tempC = tempL + (int)f;
		}
		else if( hour >= 15 && hour <= 20 )
		{
			f = tempH - tempL;
			f = f / ( 20 - 14 ) * ( hour - 14 );
			tempC = tempH - (int)f;
		}
		else if( hour > 20 && hour < 24 )
		{
			tempC = tempL;
		}
		return tempC;
	}
	
	public static WeatherForestEntity cooeeForcastDataQuery(
			String s )
	{
		WeatherForestEntity forecastentity = new WeatherForestEntity();
		String t1[] = s.split( "<tq>" );
		String t2[] = t1[1].split( "</tq>" );
		String t3[] = t2[0].split( "\\," );
		if( t3[0].equals( t3[1] ) )
		{
			forecastentity.setCondition( t3[0] );
		}
		else
		{
			String res = t2[0].replaceAll( "," , "转" );
			forecastentity.setCondition( res );
		}
		String c1[] = s.split( "<wd>" );
		String c2[] = c1[1].split( "</wd>" );
		CooeeFormatTemprature( forecastentity , c2[0] );
		String w1[] = s.split( "<xq>" );
		String w2[] = w1[1].split( "</xq>" );
		int week = formatDayOfWeek( w2[0] );
		forecastentity.setDayOfWeek( week );
		return forecastentity;
	}
	
	public static void CooeeFormatTemprature(
			WeatherForestEntity dataEntity ,
			String temp )
	{
		String[] s = temp.split( "\\," );
		String high = s[1];
		String low = s[0];
		dataEntity.setLow( low );
		dataEntity.setHight( high );
	}
	
	public static int formatDayOfWeek(
			String week )
	{
		int dayofweek = 0;
		if( week.equals( "星期日" ) )
		{
			dayofweek = 0;
		}
		else if( week.equals( "星期一" ) )
		{
			dayofweek = 1;
		}
		else if( week.equals( "星期二" ) )
		{
			dayofweek = 2;
		}
		else if( week.equals( "星期三" ) )
		{
			dayofweek = 3;
		}
		else if( week.equals( "星期四" ) )
		{
			dayofweek = 4;
		}
		else if( week.equals( "星期五" ) )
		{
			dayofweek = 5;
		}
		else if( week.equals( "星期六" ) )
		{
			dayofweek = 6;
		}
		return dayofweek;
	}
}
