package com.coco.download;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.QueryStringBuilder;


public class Assets
{
	
	public static final String CONFIG_FILE_NAME = "config.ini";
	public static final String PREFERENCE_KEY_CONFIG = "config";
	public static final String PREFERENCE_KEY_CONFIG_DOMAIN = "domain";
	public static final String PREFERENCE_KEY_CONFIG_SERIALNO = "serialno";
	public static final String PREFERENCE_KEY_CONFIG_APPID = "app_id";
	public static final String PREFERENCE_KEY_CONFIG_TEMPLATEID = "template_id";
	public static final String PREFERENCE_KEY_CONFIG_CHANNELID = "channel_id";
	public static final String TURBO_LAUNCHER_PROVIDER_AUTHOR = "com.cooeeui.brand.turbolauncher.pub.provider";
	private static Context mContext;
	private static JSONObject config;
	
	public JSONObject getConfig(
			Context context )
	{
		if( config == null )
		{
			initAssets( context );
		}
		return config;
	}
	
	public static String getSerialNo(
			Context context )
	{
		if( config == null )
		{
			initAssets( context );
		}
		PlatformInfo pi = PlatformInfo.getInstance( context );
		if( pi.isSupportViewLock() )
		{
			return pi.getChannel();
		}
		if( config != null )
		{
			try
			{
				String serialno = config.getString( PREFERENCE_KEY_CONFIG_SERIALNO );
				return serialno;
			}
			catch( JSONException e1 )
			{
				e1.printStackTrace();
				return "";
			}
		}
		else
		{
			return "";
		}
	}
	
	public static String getPhoneParams(
			Context context )
	{
		if( config == null )
		{
			initAssets( context );
		}
		QueryStringBuilder builder = new QueryStringBuilder();
		builder.add( "a01" , Build.MODEL ).add( "a02" , Build.DISPLAY ).add( "a05" , Build.PRODUCT ).add( "a06" , Build.DEVICE ).add( "a07" , Build.BOARD ).add( "a08" , Build.MANUFACTURER )
				.add( "a09" , Build.BRAND ).add( "a12" , Build.HARDWARE ).add( "a14" , Build.VERSION.RELEASE ).add( "a15" , Build.VERSION.SDK_INT );
		{
			if( mContext == null )
			{
				initAssets( context );
			}
			WindowManager winMgr = (WindowManager)mContext.getSystemService( Context.WINDOW_SERVICE );
			Display display = winMgr.getDefaultDisplay();
			int scrWidth = display.getWidth();
			int scrHeight = display.getHeight();
			builder.add( "a04" , String.format( "%dX%d" , scrWidth , scrHeight ) );
		}
		{
			TelephonyManager telMgr = (TelephonyManager)mContext.getSystemService( Context.TELEPHONY_SERVICE );
			if( telMgr != null )
			{
				builder.add( "u01" , telMgr.getSubscriberId() )
				// IMSI
						.add( "u03" , telMgr.getDeviceId() )
						// IMEI
						.add( "u04" , telMgr.getSimSerialNumber() )
						// ICCID
						.add( "u05" , telMgr.getLine1Number() );
			}
		}
		ConnectivityManager connMgr = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
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
		builder.add( "p04" , Assets.getSerialNo( context ) );
		builder.add( "a00" , Assets.getAppId( context ) );
		return builder.toString();
	}
	
	public static String getAppId(
			Context context )
	{
		if( config == null )
		{
			initAssets( context );
		}
		if( config != null )
		{
			try
			{
				String serialno = config.getString( PREFERENCE_KEY_CONFIG_APPID );
				return serialno;
			}
			catch( JSONException e1 )
			{
				e1.printStackTrace();
				return "";
			}
		}
		else
		{
			return "";
		}
	}
	
	private static void initAssets(
			Context context )
	{
		mContext = context;
		String serialno = "";
		String domain = "";
		String app_id = "";
		String template_id = "";
		String channel_id = "";
		ContentResolver resolver = context.getContentResolver();
		Uri uri;
		if( com.coco.theme.themebox.util.FunctionConfig.isNetVersion() )
			uri = Uri.parse( "content://" + TURBO_LAUNCHER_PROVIDER_AUTHOR + "/" + "config" );
		else
			uri = Uri.parse( "content://" + "com.iLoong.launcher.pub.provider" + "/" + "config" );
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		config = new JSONObject();
		try
		{
			selection = " propertyName=? ";
			selectionArgs = new String[]{ PREFERENCE_KEY_CONFIG_DOMAIN };
			Cursor cursor = resolver.query( uri , projection , selection , selectionArgs , sortOrder );
			if( cursor != null )
			{
				if( cursor.moveToFirst() )
				{
					domain = cursor.getString( cursor.getColumnIndex( "propertyValue" ) );
				}
				cursor.close();
			}
			else
			{
				domain = "";
			}
			config.put( PREFERENCE_KEY_CONFIG_DOMAIN , domain );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		try
		{
			selection = " propertyName=? ";
			selectionArgs = new String[]{ PREFERENCE_KEY_CONFIG_SERIALNO };
			Cursor cursor = resolver.query( uri , projection , selection , selectionArgs , sortOrder );
			if( cursor != null )
			{
				if( cursor.moveToFirst() )
				{
					serialno = cursor.getString( cursor.getColumnIndex( "propertyValue" ) );
				}
				cursor.close();
			}
			else
			{
				serialno = "";
			}
			config.put( PREFERENCE_KEY_CONFIG_SERIALNO , serialno );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		try
		{
			selection = " propertyName=? ";
			selectionArgs = new String[]{ PREFERENCE_KEY_CONFIG_APPID };
			Cursor cursor = resolver.query( uri , projection , selection , selectionArgs , sortOrder );
			if( cursor != null )
			{
				if( cursor.moveToFirst() )
				{
					app_id = cursor.getString( cursor.getColumnIndex( "propertyValue" ) );
				}
				cursor.close();
			}
			else
			{
				app_id = "";
			}
			config.put( PREFERENCE_KEY_CONFIG_APPID , app_id );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		try
		{
			selection = " propertyName=? ";
			selectionArgs = new String[]{ PREFERENCE_KEY_CONFIG_TEMPLATEID };
			Cursor cursor = resolver.query( uri , projection , selection , selectionArgs , sortOrder );
			if( cursor != null )
			{
				if( cursor.moveToFirst() )
				{
					template_id = cursor.getString( cursor.getColumnIndex( "propertyValue" ) );
				}
				cursor.close();
			}
			else
			{
				template_id = "";
			}
			config.put( PREFERENCE_KEY_CONFIG_TEMPLATEID , template_id );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		try
		{
			selection = " propertyName=? ";
			selectionArgs = new String[]{ PREFERENCE_KEY_CONFIG_CHANNELID };
			Cursor cursor = resolver.query( uri , projection , selection , selectionArgs , sortOrder );
			if( cursor != null )
			{
				if( cursor.moveToFirst() )
				{
					channel_id = cursor.getString( cursor.getColumnIndex( "propertyValue" ) );
				}
				cursor.close();
			}
			else
			{
				channel_id = "";
			}
			config.put( PREFERENCE_KEY_CONFIG_CHANNELID , channel_id );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		try
		{
			Log.v( "theme" , "assets " + PREFERENCE_KEY_CONFIG_DOMAIN + ":" + config.getString( PREFERENCE_KEY_CONFIG_DOMAIN ) );
			Log.v( "theme" , "assets " + PREFERENCE_KEY_CONFIG_SERIALNO + ":" + config.getString( PREFERENCE_KEY_CONFIG_SERIALNO ) );
			Log.v( "theme" , "assets " + PREFERENCE_KEY_CONFIG_APPID + ":" + config.getString( PREFERENCE_KEY_CONFIG_APPID ) );
			Log.v( "theme" , "assets " + PREFERENCE_KEY_CONFIG_TEMPLATEID + ":" + config.getString( PREFERENCE_KEY_CONFIG_TEMPLATEID ) );
			Log.v( "theme" , "assets " + PREFERENCE_KEY_CONFIG_CHANNELID + ":" + config.getString( PREFERENCE_KEY_CONFIG_CHANNELID ) );
		}
		catch( Exception ex )
		{
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
				// JSONObject jRes = new
				// JSONObject(jObject.getString("config"));
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
			e.printStackTrace();
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
	
	public static String getEffect(
			Context context ,
			String name )
	{
		ContentResolver resolver = context.getContentResolver();
		Uri uri;
		if( com.coco.theme.themebox.util.FunctionConfig.isNetVersion() )
			uri = Uri.parse( "content://" + TURBO_LAUNCHER_PROVIDER_AUTHOR + "/" + "effect" );
		else
			uri = Uri.parse( "content://" + "com.iLoong.launcher.pub.provider" + "/" + "effect" );
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		String effect = "0";
		try
		{
			selection = " propertyName=? ";
			selectionArgs = new String[]{ name };
			Cursor cursor = resolver.query( uri , projection , selection , selectionArgs , sortOrder );
			if( cursor != null )
			{
				if( cursor.moveToFirst() )
				{
					effect = cursor.getString( cursor.getColumnIndex( "propertyValue" ) );
				}
				cursor.close();
			}
			else
			{
				effect = "0";
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		if( effect == null || effect.trim().length() == 0 )
		{
			effect = "0";
		}
		return effect;
	}
	
	public static String getTheme(
			Context context ,
			String name )
	{
		ContentResolver resolver = context.getContentResolver();
		Uri uri;
		if( com.coco.theme.themebox.util.FunctionConfig.isNetVersion() )
		{
			uri = Uri.parse( "content://" + TURBO_LAUNCHER_PROVIDER_AUTHOR + "/" + "theme" );
		}
		else
		{
			uri = Uri.parse( "content://" + "com.iLoong.launcher.pub.provider" + "/" + "theme" );
		}
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		String theme = "0";
		try
		{
			selection = " propertyName=? ";
			selectionArgs = new String[]{ name };
			Cursor cursor = resolver.query( uri , projection , selection , selectionArgs , sortOrder );
			if( cursor != null )
			{
				if( cursor.moveToFirst() )
				{
					theme = cursor.getString( cursor.getColumnIndex( "propertyValue" ) );
				}
				cursor.close();
			}
			else
			{
				theme = null;
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		if( theme == null || theme.trim().length() == 0 )
		{
			theme = null;
		}
		return theme;
	}
	
	public static String getScene(
			Context context ,
			String name )
	{
		ContentResolver resolver = context.getContentResolver();
		Uri uri;
		if( com.coco.theme.themebox.util.FunctionConfig.isNetVersion() )
			uri = Uri.parse( "content://" + TURBO_LAUNCHER_PROVIDER_AUTHOR + "/" + "scene" );
		else
			uri = Uri.parse( "content://" + "com.iLoong.launcher.pub.provider" + "/" + "scene" );
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		String theme = "0";
		try
		{
			selection = " propertyName=? ";
			selectionArgs = new String[]{ name };
			Cursor cursor = resolver.query( uri , projection , selection , selectionArgs , sortOrder );
			if( cursor != null )
			{
				if( cursor.moveToFirst() )
				{
					theme = cursor.getString( cursor.getColumnIndex( "propertyValue" ) );
				}
				cursor.close();
			}
			else
			{
				theme = null;
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		if( theme == null || theme.trim().length() == 0 )
		{
			theme = null;
		}
		return theme;
	}
	
	public static String getWallpaper(
			Context context ,
			String name )
	{
		ContentResolver resolver = context.getContentResolver();
		Uri uri;
		if( com.coco.theme.themebox.util.FunctionConfig.isNetVersion() )
			uri = Uri.parse( "content://" + TURBO_LAUNCHER_PROVIDER_AUTHOR + "/" + "wallpaper" );
		else
			uri = Uri.parse( "content://" + "com.iLoong.launcher.pub.provider" + "/" + "wallpaper" );
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		String theme = "default";
		try
		{
			selection = " propertyName=? ";
			selectionArgs = new String[]{ name };
			Cursor cursor = resolver.query( uri , projection , selection , selectionArgs , sortOrder );
			if( cursor != null )
			{
				if( cursor.moveToFirst() )
				{
					theme = cursor.getString( cursor.getColumnIndex( "propertyValue" ) );
				}
				cursor.close();
			}
			else
			{
				theme = null;
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		if( theme == null || theme.trim().length() == 0 )
		{
			theme = null;
		}
		return theme;
	}
}
