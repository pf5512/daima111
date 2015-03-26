package com.iLoong.launcher.macinfo;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.iLoong.launcher.Desktop3D.Log;


public class NetInfo
{
	
	static Context mContext;
	static final Uri PREFERRED_APN_URI = Uri.parse( "content://telephony/carriers/preferapn" );
	
	public static void initNetInfo(
			Context context )
	{
		mContext = context;
	}
	
	public boolean networkIsEnable()
	{
		ConnectivityManager cwjManager = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo nInfo = cwjManager.getActiveNetworkInfo();
		if( nInfo == null )
		{
			return false;
		}
		return true;
	}
	
	public static String getApn()
	{
		ConnectivityManager cwjManager = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo nInfo = cwjManager.getActiveNetworkInfo();
		if( nInfo == null )
		{
			return null;
		}
		return nInfo.getExtraInfo();
	}
	
	public static String getLocalIpAddress()
	{
		try
		{
			for( Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces() ; en.hasMoreElements() ; )
			{
				NetworkInterface intf = en.nextElement();
				for( Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses() ; enumIpAddr.hasMoreElements() ; )
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if( !inetAddress.isLoopbackAddress() )
					{
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		}
		catch( SocketException ex )
		{
			Log.e( "ex" , ex.toString() );
		}
		return null;
	}
	
	public static String getDetailedState()
	{
		ConnectivityManager cwjManager = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo nInfo = cwjManager.getActiveNetworkInfo();
		if( nInfo == null )
		{
			return null;
		}
		return nInfo.getDetailedState().toString();
	}
	
	public static String getId()
	{
		Cursor cursor = mContext.getContentResolver().query( PREFERRED_APN_URI , new String[]{ "_id" , "name" , "apn" , "type" , "proxy" } , null , null , null );
		cursor.moveToFirst();
		if( cursor.isAfterLast() )
		{
			return null;
		}
		String id = "" + cursor.getLong( 0 );
		cursor.close();
		return id;
	}
	
	public static String getName()
	{
		Cursor cursor = mContext.getContentResolver().query( PREFERRED_APN_URI , new String[]{ "_id" , "name" , "apn" , "type" , "proxy" } , null , null , null );
		cursor.moveToFirst();
		if( cursor.isAfterLast() )
		{
			return null;
		}
		String name = cursor.getString( 1 );
		cursor.close();
		return name;
	}
	
	public static String getType()
	{
		Cursor cursor = mContext.getContentResolver().query( PREFERRED_APN_URI , new String[]{ "_id" , "name" , "apn" , "type" , "proxy" } , null , null , null );
		cursor.moveToFirst();
		if( cursor.isAfterLast() )
		{
			return null;
		}
		String type = cursor.getString( 3 );
		cursor.close();
		return type;
	}
	
	public static String getProxy()
	{
		Cursor cursor = mContext.getContentResolver().query( PREFERRED_APN_URI , new String[]{ "_id" , "name" , "apn" , "type" , "proxy" } , null , null , null );
		cursor.moveToFirst();
		if( cursor.isAfterLast() )
		{
			return null;
		}
		String proxy = cursor.getString( 4 );
		cursor.close();
		return proxy;
	}
	
	public static JSONObject getInfo()
	{
		JSONObject jObject = new JSONObject();
		ConnectivityManager cwjManager = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo nInfo = cwjManager.getActiveNetworkInfo();
		boolean enable = false;
		if( nInfo == null )
		{
			enable = false;
			try
			{
				JSONObjectUitl.put( jObject , "enable" , enable );
				JSONObjectUitl.put( jObject , "network_type" , "" );
				JSONObjectUitl.put( jObject , "apn" , "" );
				JSONObjectUitl.put( jObject , "ip_address" , "" );
				JSONObjectUitl.put( jObject , "_id" , "" );
				JSONObjectUitl.put( jObject , "name" , "" );
				JSONObjectUitl.put( jObject , "apn" , "" );
				JSONObjectUitl.put( jObject , "type" , "" );
				JSONObjectUitl.put( jObject , "proxy" , "" );
			}
			catch( JSONException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return jObject;
		}
		else
		{
			enable = true;
		}
		try
		{
			if( nInfo.getType() == ConnectivityManager.TYPE_WIFI )
			{
				JSONObjectUitl.put( jObject , "network_type" , "wifi" );
			}
			else if( nInfo.getType() == ConnectivityManager.TYPE_MOBILE )
			{
				JSONObjectUitl.put( jObject , "network_type" , "mobile" );
			}
			else
				JSONObjectUitl.put( jObject , "network_type" , "" );
			JSONObjectUitl.put( jObject , "enable" , enable );
			JSONObjectUitl.put( jObject , "apn" , nInfo.getExtraInfo() );
			JSONObjectUitl.put( jObject , "ip_address" , getLocalIpAddress() );
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Cursor cursor = null;
		try
		{
			cursor = mContext.getContentResolver().query( PREFERRED_APN_URI , new String[]{ "_id" , "name" , "apn" , "type" , "proxy" } , null , null , null );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		if( cursor != null )
		{
			cursor.moveToFirst();
			if( cursor.isAfterLast() )
			{
				try
				{
					JSONObjectUitl.put( jObject , "_id" , "" );
					JSONObjectUitl.put( jObject , "name" , "" );
					JSONObjectUitl.put( jObject , "apn" , "" );
					JSONObjectUitl.put( jObject , "type" , "" );
					JSONObjectUitl.put( jObject , "proxy" , "" );
				}
				catch( JSONException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				String id = "" + cursor.getLong( 0 );
				String name = cursor.getString( 1 );
				String apn = cursor.getString( 2 );
				String type = cursor.getString( 3 );
				String proxy = cursor.getString( 4 );
				try
				{
					JSONObjectUitl.put( jObject , "_id" , id );
					JSONObjectUitl.put( jObject , "name" , name );
					JSONObjectUitl.put( jObject , "apn" , apn );
					JSONObjectUitl.put( jObject , "type" , type );
					JSONObjectUitl.put( jObject , "proxy" , proxy );
				}
				catch( JSONException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			cursor.close();
		}
		else
		{
			try
			{
				JSONObjectUitl.put( jObject , "_id" , "" );
				JSONObjectUitl.put( jObject , "name" , "" );
				JSONObjectUitl.put( jObject , "apn" , "" );
				JSONObjectUitl.put( jObject , "type" , "" );
				JSONObjectUitl.put( jObject , "proxy" , "" );
			}
			catch( JSONException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jObject;
	}
}
