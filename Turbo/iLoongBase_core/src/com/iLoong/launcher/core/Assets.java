package com.iLoong.launcher.core;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;

import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.ConfigBase;
import com.iLoong.launcher.pub.provider.PubContentProvider;
import com.iLoong.launcher.pub.provider.TurboPubContentProvider;


public class Assets
{
	
	public static final String CONFIG_FILE_NAME = "config.ini";
	public static final String PREFERENCE_KEY_CONFIG = "config";
	public static final String PREFERENCE_KEY_CONFIG_DOMAIN = "domain";
	public static final String PREFERENCE_KEY_CONFIG_SERIALNO = "serialno";
	public static final String PREFERENCE_KEY_CONFIG_APPID = "app_id";
	public static final String PREFERENCE_KEY_CONFIG_TEMPLATEID = "template_id";
	public static final String PREFERENCE_KEY_CONFIG_CHANNELID = "channel_id";
	private static Context mContext;
	public static JSONObject config;
	
	public static void initAssets(
			Context context )
	{
		mContext = context;
		config = getConfig( CONFIG_FILE_NAME );
		if( config != null )
		{
			SharedPreferences prefs = mContext.getSharedPreferences( PREFERENCE_KEY_CONFIG , Activity.MODE_WORLD_READABLE );
			try
			{
				JSONObject tmp = config.getJSONObject( "config" );
				final String serialno = tmp.getString( "serialno" );
				final String domain = tmp.getString( "domain" );
				final String app_id = tmp.getString( "app_id" );
				final String template_id = tmp.getString( "template_id" );
				final String channel_id = tmp.getString( "channel_id" );
				Editor edit = prefs.edit();
				edit.putString( PREFERENCE_KEY_CONFIG_DOMAIN , domain );
				edit.putString( PREFERENCE_KEY_CONFIG_SERIALNO , serialno );
				edit.putString( PREFERENCE_KEY_CONFIG_APPID , app_id );
				edit.putString( PREFERENCE_KEY_CONFIG_TEMPLATEID , template_id );
				edit.putString( PREFERENCE_KEY_CONFIG_CHANNELID , channel_id );
				edit.commit();
			}
			catch( JSONException e1 )
			{
				e1.printStackTrace();
			}
		}
	}
	
	public static JSONObject getConfig(
			String fileName )
	{
		AssetManager assetManager = mContext.getAssets();
		InputStream inputStream = null;
		try
		{
			inputStream = assetManager.open( fileName );
			String config = readTextFile( inputStream );
			JSONObject jObject;
			try
			{
				jObject = new JSONObject( config );
				return jObject;
			}
			catch( JSONException e1 )
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		catch( IOException e )
		{
			Log.e( "tag" , e.getMessage() );
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	private static String readTextFile(
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
			outputStream.close();
			inputStream.close();
		}
		catch( IOException e )
		{
		}
		return outputStream.toString();
	}
	
	public static String getWallpaperCooeeChange(
			Context context ,
			String name )
	{
		ContentResolver resolver = context.getContentResolver();
		//	Uri uri = Uri.parse( "content://" + "com.iLoong.launcher.pub.provider" + "/" + "wallpaper" );
		Uri uri = Uri.parse( "content://" + PubContentProvider.LAUNCHER_AUTHORITY + "/" + "wallpaper" );
		if( ConfigBase.net_version )
			uri = Uri.parse( "content://" + TurboPubContentProvider.LAUNCHER_AUTHORITY + "/" + "wallpaper" );
		// uri = Uri.parse( "content://" + "com.cooeeui.brand.turbolauncher.pub.provider" + "/" + "wallpaper" );
		else
			uri = Uri.parse( "content://" + PubContentProvider.LAUNCHER_AUTHORITY + "/" + "wallpaper" );
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		String isCooeeChange = "false";
		try
		{
			selection = " propertyName=? ";
			selectionArgs = new String[]{ name };
			Cursor cursor = resolver.query( uri , projection , selection , selectionArgs , sortOrder );
			if( cursor != null )
			{
				if( cursor.moveToFirst() )
				{
					isCooeeChange = cursor.getString( cursor.getColumnIndex( "propertyValue" ) );
				}
				cursor.close();
			}
			else
			{
				isCooeeChange = "false";
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		if( isCooeeChange == null || isCooeeChange.trim().length() == 0 )
		{
			isCooeeChange = "false";
		}
		return isCooeeChange;
	}
}
