package com.iLoong.launcher.UI3DEngine;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.res.AssetManager;
import com.badlogic.gdx.graphics.Texture.TextureFilter;


public class ConfigBase
{
	
	public static String AUTHORITY;
	public static boolean show_sensor;
	public static boolean disable_double_click;
	public static boolean widget_revise_complete;
	public static int Workspace_cell_each_width;
	public static int Workspace_cell_each_height;
	public static int Workspace_cell_each_width_ori;
	public static int Workspace_cell_each_height_ori;
	public static TextureFilter filter = TextureFilter.Nearest;
	public static TextureFilter Magfilter = TextureFilter.Nearest;
	public static boolean set_status_bar_background;
	public static String custom_sdcard_root_path = "/cooee/";
	public static boolean gallery3d_support_scrollV = true;
	public static boolean releaseGL = false;
	
	//xiatian add start	//change the way to read the switch of CooeeShellSDK
	static public boolean isEnableShellSDK(
			Context context )
	{
		JSONObject base = getBase( context , "base.ini" );
		if( base == null )
		{
			return false;
		}
		try
		{
			JSONObject tmp = base.getJSONObject( "base" );
			String useBaseStr = tmp.getString( "useBase" );
			boolean ret = useBaseStr.equals( "1" );
			return ret;
		}
		catch( JSONException e1 )
		{
			e1.printStackTrace();
			return false;
		}
	}
	
	static private JSONObject getBase(
			Context context ,
			String fileName )
	{
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;
		try
		{
			inputStream = assetManager.open( fileName );
			String base = readTextFile( inputStream );
			JSONObject jObject = new JSONObject( base );
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
	
	static private String readTextFile(
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
	//xiatian add end
}
