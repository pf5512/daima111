package com.iLoong.NumberClock.common;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.iLoong.NumberClock.R;
import com.iLoong.NumberClock.view.NumberCityFinderActivity;
import com.iLoong.NumberClock.view.WidgetNumberClock;

import android.content.Context;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class YahooClient
{
	
	public static String YAHOO_GEO_URL = "http://where.yahooapis.com/v1";
	public static String YAHOO_WEATHER_URL = "http://weather.yahooapis.com/forecastrss";
	private static String APPID = "dj0yJmk9cHlhcHpjcTZhYVhoJmQ9WVdrOVdGTklXRmhoTlRRbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD0wZA--";
	
	public static List<CityResult> getCityList(
			final String cityName ,
			final Context mContext )
	{
		final List<CityResult> result = new ArrayList<CityResult>();
		if( NumberClockHelper.isHaveInternet( mContext ) )
		{
			HttpURLConnection yahooHttpConn = null;
			try
			{
				String query = makeQueryCityURL( cityName );
				yahooHttpConn = (HttpURLConnection)( new URL( query ) ).openConnection();
				yahooHttpConn.setConnectTimeout( 5000 );
				yahooHttpConn.setReadTimeout( 5000 );
				yahooHttpConn.connect();
				int statue = yahooHttpConn.getResponseCode();
				if( statue == yahooHttpConn.HTTP_OK )
				{
					XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
					parser.setInput( new InputStreamReader( yahooHttpConn.getInputStream() ) );
					int event = parser.getEventType();
					CityResult cty = null;
					String tagName = null;
					String currentTag = null;
					while( event != XmlPullParser.END_DOCUMENT )
					{
						tagName = parser.getName();
						if( event == XmlPullParser.START_TAG )
						{
							if( tagName.equalsIgnoreCase( "html" ) || tagName.equalsIgnoreCase( "h1" ) )
							{
								if( NumberCityFinderActivity.mHandler != null )
								{
									NumberCityFinderActivity.mHandler.obtainMessage( NumberCityFinderActivity.MSG_TOASTCONNECTEDERROR ).sendToTarget();
								}
								break;
							}
							if( tagName.equals( "place" ) )
							{
								cty = new CityResult();
							}
							currentTag = tagName;
						}
						else if( event == XmlPullParser.TEXT )
						{
							if( "woeid".equals( currentTag ) )
								cty.setWoeid( parser.getText() );
							else if( "name".equals( currentTag ) )
								cty.setCityName( parser.getText() );
							else if( "country".equals( currentTag ) )
								cty.setCountry( parser.getText() );
						}
						else if( event == XmlPullParser.END_TAG )
						{
							if( "place".equals( tagName ) )
								result.add( cty );
						}
						event = parser.next();
					}
				}
				else
				{
					if( NumberCityFinderActivity.mHandler != null )
					{
						NumberCityFinderActivity.mHandler.obtainMessage( NumberCityFinderActivity.MSG_TOASTREQUESTFAILED ).sendToTarget();
					}
				}
			}
			catch( Exception e )
			{
				if( NumberCityFinderActivity.mHandler != null )
				{
					NumberCityFinderActivity.mHandler.obtainMessage( NumberCityFinderActivity.MSG_TOASTCONNECTEDERROR ).sendToTarget();
				}
				e.printStackTrace();
			}
			finally
			{
				try
				{
					yahooHttpConn.disconnect();
				}
				catch( Throwable ignore )
				{
				}
			}
		}
		else
		{
			if( NumberCityFinderActivity.mHandler != null )
			{
				NumberCityFinderActivity.mHandler.obtainMessage( NumberCityFinderActivity.MSG_TOASTNOTCONNECTED ).sendToTarget();
			}
		}
		return result;
	}
	
	public static Weather getWeatherInfo(
			CityResult result ,
			String unit ,
			Context mContext ,
			int what )
	{
		Weather weather = new Weather();
		if( NumberClockHelper.isHaveInternet( mContext ) )
		{
			HttpURLConnection yahooHttpConn = null;
			try
			{
				String query = makeWeatherURL( result.getWoeid() , unit );
				yahooHttpConn = (HttpURLConnection)( new URL( query ) ).openConnection();
				yahooHttpConn.setConnectTimeout( 10000 );
				yahooHttpConn.setReadTimeout( 10000 );
				yahooHttpConn.connect();
				int statue = yahooHttpConn.getResponseCode();
				if( statue == yahooHttpConn.HTTP_OK )
				{
					weather = parseResponse( yahooHttpConn.getInputStream() );
					if( what == 1 )
					{
						if( weather != null && weather.getList() != null && weather.getList().size() == 5 )
						{
							Bundle bundle = new Bundle();
							bundle.putSerializable( "mynumberweatherinfo" , weather );
							bundle.putSerializable( "mynumberresult" , result );
							if( NumberCityFinderActivity.mHandler != null )
							{
								NumberCityFinderActivity.mHandler.obtainMessage( NumberCityFinderActivity.MSG_SUCCESS , bundle ).sendToTarget();
							}
						}
						else
						{
							if( NumberCityFinderActivity.mHandler != null )
							{
								NumberCityFinderActivity.mHandler.obtainMessage( NumberCityFinderActivity.MSG_FAILURE ).sendToTarget();
							}
						}
					}
					else if( what == 2 )
					{
						if( weather != null && weather.getList() != null && weather.getList().size() == 5 )
						{
							Bundle bundle = new Bundle();
							bundle.putSerializable( "weatherinfoupdate" , weather );
							if( WidgetNumberClock.updatehandle != null )
							{
								WidgetNumberClock.updatehandle.obtainMessage( WidgetNumberClock.MSG_UpdateForSUCCESS , bundle ).sendToTarget();
							}
						}
						else
						{
							if( WidgetNumberClock.updatehandle != null )
							{
								WidgetNumberClock.updatehandle.obtainMessage( WidgetNumberClock.MSG_UpdateForFAILURE ).sendToTarget();
							}
						}
					}
				}
				else
				{
					if( NumberCityFinderActivity.mHandler != null )
					{
						NumberCityFinderActivity.mHandler.obtainMessage( NumberCityFinderActivity.MSG_FAILURE ).sendToTarget();
					}
					else if( WidgetNumberClock.updatehandle != null )
					{
						WidgetNumberClock.updatehandle.obtainMessage( WidgetNumberClock.MSG_UpdateForFAILURE ).sendToTarget();
					}
				}
			}
			catch( Exception e )
			{
				if( NumberCityFinderActivity.mHandler != null )
				{
					NumberCityFinderActivity.mHandler.obtainMessage( NumberCityFinderActivity.MSG_FAILURE ).sendToTarget();
				}
				else if( WidgetNumberClock.updatehandle != null )
				{
					WidgetNumberClock.updatehandle.obtainMessage( WidgetNumberClock.MSG_UpdateForFAILURE ).sendToTarget();
				}
				e.printStackTrace();
			}
			finally
			{
				try
				{
					yahooHttpConn.disconnect();
				}
				catch( Throwable ignore )
				{
				}
			}
		}
		else
		{
			if( NumberCityFinderActivity.mHandler != null )
			{
				NumberCityFinderActivity.mHandler.obtainMessage( NumberCityFinderActivity.MSG_FAILURE ).sendToTarget();
			}
			else if( WidgetNumberClock.updatehandle != null )
			{
				WidgetNumberClock.updatehandle.obtainMessage( WidgetNumberClock.MSG_UpdateForFAILURE ).sendToTarget();
			}
		}
		return weather;
	}
	
	private static Weather parseResponse(
			InputStream inputStream )
	{
		Weather result = new Weather();
		try
		{
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput( inputStream , "utf-8" );
			String tagName = null;
			List<Weather> list = new ArrayList<Weather>();
			int event = parser.getEventType();
			while( event != XmlPullParser.END_DOCUMENT )
			{
				tagName = parser.getName();
				if( event == XmlPullParser.START_TAG )
				{
					if( tagName.equals( "yweather:forecast" ) )
					{
						Weather weather = new Weather();
						weather.setWeatherweek( parser.getAttributeValue( null , "day" ) );
						weather.setWeatherdate( parser.getAttributeValue( null , "date" ) );
						weather.setHightmp( parser.getAttributeValue( null , "high" ) );
						weather.setLowtmp( parser.getAttributeValue( null , "low" ) );
						weather.setWeathercode( parser.getAttributeValue( null , "code" ) );
						weather.setWeathercondition( parser.getAttributeValue( null , "text" ) );
						list.add( weather );
					}
					else if( tagName.equals( "yweather:condition" ) )
					{
						result.setWeathercode( parser.getAttributeValue( null , "code" ) );
						result.setWeathercondition( parser.getAttributeValue( null , "text" ) );
						result.setCurrtmp( parser.getAttributeValue( null , "temp" ) );
						result.setWeatherdate( parser.getAttributeValue( null , "date" ) );
					}
					else if( tagName.equals( "yweather:atmosphere" ) )
					{
						result.setShidu( parser.getAttributeValue( null , "humidity" ) );
					}
					else if( tagName.equals( "yweather:location" ) )
					{
						result.setWeathercity( parser.getAttributeValue( null , "city" ) );
					}
				}
				else if( event == XmlPullParser.END_TAG )
				{
				}
				else if( event == XmlPullParser.TEXT )
				{
				}
				event = parser.next();
			}
			result.setList( list );
		}
		catch( XmlPullParserException t )
		{
			t.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return result;
	}
	
	private static String makeQueryCityURL(
			String cityName )
	{
		cityName = cityName.replaceAll( " " , "%20" );
		return YAHOO_GEO_URL + "/places.q(" + cityName + "%2A);count=" + Config.MAX_CITY_RESULT + "?appid=" + APPID;
	}
	
	private static String makeWeatherURL(
			String woeid ,
			String unit )
	{
		return YAHOO_WEATHER_URL + "?w=" + woeid + "&u=" + unit;
	}
}
