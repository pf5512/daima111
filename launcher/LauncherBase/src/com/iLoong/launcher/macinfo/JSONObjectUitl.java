package com.iLoong.launcher.macinfo;


import org.json.JSONException;
import org.json.JSONObject;


public class JSONObjectUitl
{
	
	public static void put(
			JSONObject jObject ,
			String key ,
			Object value ) throws JSONException
	{
		if( value == null )
		{
			jObject.put( key , "" );
		}
		else
		{
			jObject.put( key , value );
		}
	}
}
