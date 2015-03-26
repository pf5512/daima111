// wanghongjian add whole file //enable_DefaultScene
package com.iLoong.launcher.scene;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.iLoong.launcher.Desktop3D.DefaultLayout;


public class ScenesDB
{
	
	Context mContext;
	
	public ScenesDB(
			Context context )
	{
		mContext = context;
	}
	
	public SceneConfig getTheme()
	{
		SceneConfig themeconf = new SceneConfig();
		SharedPreferences prefs = mContext.getSharedPreferences( "themeScene" , Activity.MODE_WORLD_WRITEABLE );
		themeconf.themeScene = prefs.getString( "themeScene" , "" );
		return themeconf;
	}
	
	public void SaveThemes(
			SceneConfig themeconf )
	{
		SharedPreferences prefs = mContext.getSharedPreferences( "themeScene" , Activity.MODE_WORLD_WRITEABLE );
		prefs.edit().putString( "themeScene" , themeconf.themeScene ).commit();
	}
	
	public void SaveThemesStatus(
			int status )
	{
		SharedPreferences prefs = mContext.getSharedPreferences( "themeScene" , Activity.MODE_WORLD_WRITEABLE );
		prefs.edit().putInt( "themeScene_status" , status ).commit();
	}
	
	public int getThemesStatus()
	{
		SharedPreferences prefs = mContext.getSharedPreferences( "themeScene" , Activity.MODE_WORLD_WRITEABLE );
		int ret;
		if( DefaultLayout.getInstance().install_change_wallpaper == true )
		{
			ret = prefs.getInt( "themeScene_status" , 1 );
		}
		else
		{
			ret = prefs.getInt( "themeScene_status" , 0 );
		}
		return ret;
	}
}
