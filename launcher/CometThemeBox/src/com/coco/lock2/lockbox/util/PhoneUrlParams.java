package com.coco.lock2.lockbox.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.coco.lock2.lockbox.PlatformInfo;
import com.coco.lock2.lockbox.util.QueryStringBuilder;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.coco.theme.themebox.util.Log;
import android.view.Display;
import android.view.WindowManager;


public class PhoneUrlParams
{
	
	public static final String CONFIG_FILE_NAME = "config.ini";
	
	public PhoneUrlParams()
	{
	}
	
	public String getParams(
			Context context )
	{
		QueryStringBuilder builder = new QueryStringBuilder();
		builder.add( "a01" , Build.MODEL ).add( "a02" , Build.DISPLAY ).add( "a05" , Build.PRODUCT ).add( "a06" , Build.DEVICE ).add( "a07" , Build.BOARD ).add( "a08" , Build.MANUFACTURER )
				.add( "a09" , Build.BRAND ).add( "a12" , Build.HARDWARE ).add( "a14" , Build.VERSION.RELEASE ).add( "a15" , Build.VERSION.SDK_INT );
		{
			WindowManager winMgr = (WindowManager)context.getSystemService( Context.WINDOW_SERVICE );
			Display display = winMgr.getDefaultDisplay();
			int scrWidth = display.getWidth();
			int scrHeight = display.getHeight();
			builder.add( "a04" , String.format( "%dX%d" , scrWidth , scrHeight ) );
		}
		{
			TelephonyManager telMgr = (TelephonyManager)context.getSystemService( Context.TELEPHONY_SERVICE );
			if( telMgr != null )
			{
				builder.add( "u01" , telMgr.getSubscriberId() )
				// IMSI
						.add( "u03" , telMgr.getDeviceId() )
						// IMEI
						.add( "u04" , telMgr.getSimSerialNumber() )
						// ICCID
						.add( "u05" , telMgr.getLine1Number() );
				Log.v( "phone" , "imsi = " + telMgr.getSubscriberId() + "  IMEI = " + telMgr.getDeviceId() + " ICCID = " + telMgr.getSimSerialNumber() );
			}
		}
		{
			ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
			if( netInfo != null )
			{
				if( netInfo.getTypeName().equals( "WIFI" ) )
				{
					builder.add( "u07" , "2" );
				}
				else if( netInfo.getTypeName().equals( "mobile" ) )
				{
					builder.add( "u07" , "1" );
				}
			}
		}
		builder.add( "p04" , getSerialNo( context ) );
		builder.add( "a00" , getAppId( context ) );
		return builder.toString();
	}
	
	private String getSerialNo(
			Context context )
	{
		PlatformInfo pi = PlatformInfo.getInstance( context );
		if( pi.isSupportViewLock() )
		{
			return pi.getChannel();
		}
		JSONObject config = getConfig( context , CONFIG_FILE_NAME );
		if( config == null )
		{
			return "";
		}
		try
		{
			JSONObject tmp = config.getJSONObject( "config" );
			String serialno = tmp.getString( "serialno" );
			return serialno;
		}
		catch( JSONException e1 )
		{
			e1.printStackTrace();
			return "";
		}
	}
	
	private String getAppId(
			Context context )
	{
		JSONObject config = getConfig( context , CONFIG_FILE_NAME );
		if( config == null )
		{
			return "";
		}
		try
		{
			JSONObject tmp = config.getJSONObject( "config" );
			String app_id = tmp.getString( "app_id" );
			return app_id;
		}
		catch( JSONException e1 )
		{
			e1.printStackTrace();
			return "";
		}
	}
	
	private String readTextFile(
			InputStream inputStream )
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		try
		{
			while( ( len = inputStream.read( buf ) ) != -1 )
			{
				outputStream.write( buf , 0 , len );
			}
			String result = outputStream.toString();
			return result;
		}
		catch( IOException e )
		{
			e.printStackTrace();
			return "";
		}
		finally
		{
			try
			{
				outputStream.close();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	private JSONObject getConfig(
			Context context ,
			String fileName )
	{
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;
		try
		{
			inputStream = assetManager.open( fileName );
			String config = readTextFile( inputStream );
			JSONObject jObject = new JSONObject( config );
			return jObject;
		}
		catch( JSONException e1 )
		{
			e1.printStackTrace();
			return null;
		}
		catch( IOException e )
		{
			return null;
		}
		finally
		{
			if( inputStream != null )
			{
				try
				{
					inputStream.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
	}
}
