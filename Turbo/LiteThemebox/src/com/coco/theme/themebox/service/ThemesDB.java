package com.coco.theme.themebox.service;


import android.content.Context;
import android.content.Intent;

import com.coco.download.Assets;
import com.coco.theme.themebox.util.Log;


public class ThemesDB
{
	
	//public static String LAUNCHER_PACKAGENAME = "com.cool.launcher";
	public static String LAUNCHER_PACKAGENAME = "com.cooeeui.brand.turbolauncher";
	public static String ACTION_LAUNCHER_RESTART = "com.coco.launcher.restart";
	public static String ACTION_LAUNCHER_APPLY_THEME = "com.coco.launcher.apply_theme";
	public static String default_theme_package_name = null;
	Context mContext;
	
	public ThemesDB(
			Context context )
	{
		mContext = context;
	}
	
	public ThemeConfig getTheme()
	{
		ThemeConfig themeconf = new ThemeConfig();
		String curTheme = Assets.getTheme( mContext , "theme" );
		if( curTheme == null || curTheme.trim().length() == 0 )
		{
			curTheme = ThemesDB.default_theme_package_name;
		}
		themeconf.theme = curTheme;
		return themeconf;
	}
	
	public void SaveThemes(
			ThemeConfig themeconf )
	{
		Intent intent = new Intent( ThemesDB.ACTION_LAUNCHER_APPLY_THEME );
		intent.putExtra( "theme_status" , 1 );
		intent.putExtra( "theme" , themeconf.theme );
		mContext.sendBroadcast( intent );
		Log.v( "theme" , "save theme 1:" + themeconf.theme );
	}
	
	public void SaveThemes(
			String themeconf )
	{
		Intent intent = new Intent( ThemesDB.ACTION_LAUNCHER_APPLY_THEME );
		intent.putExtra( "theme_status" , 1 );
		intent.putExtra( "theme" , themeconf );
		mContext.sendBroadcast( intent );
		Log.v( "theme" , "save theme 2:" + themeconf );
	}
}
