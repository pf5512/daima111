package com.coco.theme.themebox.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import com.coco.theme.themebox.util.Log;

public class ThemesDB{
	public static String LAUNCHER_PACKAGENAME = "com.coco.launcher";
	public static String ACTION_LAUNCHER_RESTART = "com.coco.launcher.restart";
	public static String ACTION_LAUNCHER_APPLY_THEME = "com.cooeecomet.launcher.apply_theme";
	Context mContext;
	SharedPreferences pref;
	
	public ThemesDB(Context context) {
		mContext = context;
		Context slaveContext = null;

		try {
			slaveContext = mContext.createPackageContext(LAUNCHER_PACKAGENAME,
						Context.CONTEXT_IGNORE_SECURITY);
			pref = slaveContext.getSharedPreferences("theme", Activity.MODE_WORLD_WRITEABLE);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public ThemeConfig getTheme() {
		
		ThemeConfig themeconf = new ThemeConfig();
		if(pref != null)themeconf.theme = pref.getString("theme", "");
		Log.e("theme", "get theme :"+themeconf.theme);	
		return themeconf;
	}

	public void SaveThemes(ThemeConfig themeconf) {
		Intent intent = new Intent(ThemesDB.ACTION_LAUNCHER_APPLY_THEME);
		intent.putExtra("theme_status", 1);
		intent.putExtra("theme", themeconf.theme);
		mContext.sendBroadcast(intent);
		Log.e("theme", "save theme 1:"+themeconf.theme);
	}
	public void SaveThemes(String themeconf) {
		Intent intent = new Intent(ThemesDB.ACTION_LAUNCHER_APPLY_THEME);
		intent.putExtra("theme_status", 1);
		intent.putExtra("theme", themeconf);
		mContext.sendBroadcast(intent);
		Log.e("theme", "save theme 2:"+themeconf);
	}
}
