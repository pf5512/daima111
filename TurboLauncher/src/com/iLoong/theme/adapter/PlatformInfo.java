package com.iLoong.theme.adapter;


import android.content.Context;

import com.iLoong.launcher.Desktop3D.Log;


public class PlatformInfo
{
	
	private static final String LOG_TAG = "PlatformInfo";
	public static final int MODE_DEFAULT = 0;
	public static final int MODE_VIEW = 1;
	private static PlatformInfo mInstance = null;
	
	public static PlatformInfo getInstance(
			Context context )
	{
		if( mInstance == null )
		{
			mInstance = new PlatformInfo();
			mInstance.init( context );
		}
		return mInstance;
	}
	
	private int versionCode = 0;
	private int mode = 0;
	private String channel = "";
	
	public int getVersionCode()
	{
		return versionCode;
	}
	
	public int getMode()
	{
		return mode;
	}
	
	public String getChannel()
	{
		return channel;
	}
	
	public boolean isSupportViewLock()
	{
		boolean result = ( versionCode > 0 ) && ( mode == MODE_VIEW );
		return result;
	}
	
	private PlatformInfo()
	{
	}
	
	private static String PLATFORM_SETTING_NAME = "cooee_lock_config";
	
	private String readChannel(
			Context context )
	{
		return "";
	}
	
	private void resetVersionDefault(
			String defaultChannel )
	{
		versionCode = 0;
		mode = MODE_DEFAULT;
		channel = defaultChannel;
	}
	
	private void init(
			Context context )
	{
		final String defaultChannel = readChannel( context );
		Log.d( LOG_TAG , "defaultChannel=" + channel );
		resetVersionDefault( defaultChannel );
		String settingStr = android.provider.Settings.System.getString( context.getContentResolver() , PLATFORM_SETTING_NAME );
		Log.d( LOG_TAG , "settingStr=" + settingStr );
		if( settingStr != null )
		{
			String[] strArray = settingStr.split( "," );
			if( strArray.length >= 3 )
			{
				try
				{
					versionCode = Integer.parseInt( strArray[0] );
					if( versionCode >= 1000 )
					{
						mode = Integer.parseInt( strArray[1] );
						channel = strArray[2];
					}
					else
					{
						resetVersionDefault( defaultChannel );
					}
				}
				catch( NumberFormatException ex )
				{
					ex.printStackTrace();
					Log.d( LOG_TAG , ex.toString() );
					resetVersionDefault( defaultChannel );
				}
			}
		}
	}
}
