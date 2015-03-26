package com.iLoong.ThemeClock.Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

//import com.iLoong.RR;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

class ThemeConfig {
	public String theme;
}


public class ThemeParse
{
	private static ThemeParse mInstance;
	private static Context mContext;
	private MyThemeDescription mSystemThemeDescription;
	private MyThemeDescription mThemeDescription;
	private final PackageManager mPackageManager;
	private MyThemeDescription mThemeDefaultConfig;
	private static final String CONFIGBASE_FILENAME =  "theme/configbase.xml";
//	private Vector<MyThemeDescription> mThemeDescriptionList;
	private Context mThemeContext = null;
	public ThemeParse(Context context)
	{
		mInstance = this;
		mContext = context;
		mPackageManager = context.getPackageManager();
		init();
	}
	
	private ThemeConfig getTheme() {
		ThemeConfig themeconf = new ThemeConfig();
		SharedPreferences prefs = mContext.getSharedPreferences("theme", Activity.MODE_WORLD_WRITEABLE);
		themeconf.theme = prefs.getString("theme", "");
		return themeconf;
	}
	private ArrayList<ResolveInfo> getItemsList() {
		ArrayList<ResolveInfo> reslist = new ArrayList<ResolveInfo>();
		Intent intent = null;
		intent = new Intent("com.coco.themes", null);
		List<ResolveInfo> themesinfo = mContext.getPackageManager().queryIntentActivities(intent, 0);
		Collections.sort(themesinfo, new ResolveInfo.DisplayNameComparator(mContext.getPackageManager()));

		int themescount = themesinfo.size();
		PackageManager packmanager = mContext.getPackageManager();

		Intent systemmain = new Intent("android.intent.action.MAIN", null);
		systemmain.addCategory("android.intent.category.LAUNCHER");

		Iterator<ResolveInfo> it = packmanager.queryIntentActivities(systemmain, 0).iterator();

		while (it.hasNext()) {
			ResolveInfo resinfo = it.next();
			reslist.add(resinfo);
		}

		for (int index = 0; index < themescount; index++) {
			ResolveInfo resinfo = themesinfo.get(index);
			reslist.add(resinfo);
		}
		return reslist;
	}
	private MyThemeDescription CreateThemeDescription(Context context, ResolveInfo resinfo) {
		MyThemeDescription themeDescription = new MyThemeDescription(context);
		themeDescription.componentName = new ComponentName(resinfo.activityInfo.applicationInfo.packageName,
				resinfo.activityInfo.name);

		themeDescription.title = resinfo.loadLabel(mPackageManager);
		
		themeDescription.mBuiltIn = (resinfo.activityInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0;
		return themeDescription;
	}
	private MyThemeDescription getThemeDescription(ResolveInfo resinfo) {
		Context slaveContext = null;
		try {
			if (!resinfo.activityInfo.applicationInfo.packageName.equals(mContext.getPackageName()))
				slaveContext = mContext.createPackageContext(resinfo.activityInfo.applicationInfo.packageName,
						Context.CONTEXT_IGNORE_SECURITY);
			else
				slaveContext = mContext;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		MyThemeDescription themeDes = CreateThemeDescription(slaveContext, resinfo);
		return themeDes;
	}
	
	
	
	private MyThemeDescription CreateThemeDescription2(Context context, ResolveInfo resinfo) {
		MyThemeDescription themeDescription = new MyThemeDescription(context,MyThemeDescription.CONFIG_FILENAME,
																			MyThemeDescription.WIDGET_CLOCK_CONFIG_FILENAME);
		themeDescription.componentName = new ComponentName(resinfo.activityInfo.applicationInfo.packageName,
				resinfo.activityInfo.name);

		themeDescription.title = resinfo.loadLabel(mPackageManager);
		
		themeDescription.mBuiltIn = (resinfo.activityInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0;
		return themeDescription;
	}
	private MyThemeDescription getThemeDescription2(ResolveInfo resinfo) {
		Context slaveContext = null;
		try {
			if (!resinfo.activityInfo.applicationInfo.packageName.equals(mContext.getPackageName()))
				slaveContext = mContext.createPackageContext(resinfo.activityInfo.applicationInfo.packageName,
						Context.CONTEXT_IGNORE_SECURITY);
			else
				slaveContext = mContext;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		MyThemeDescription themeDes = CreateThemeDescription2(slaveContext, resinfo);
		return themeDes;
	}
	private void init() {
		ThemeConfig dbThemeConf = getTheme();
		ArrayList<ResolveInfo> localResDesList = getItemsList();

		Iterator<ResolveInfo> it = localResDesList.iterator();
		ResolveInfo resinfo = null;
		while (it.hasNext()) {
			resinfo = it.next();
			if (!resinfo.activityInfo.applicationInfo.packageName.equals(mContext.getPackageName()))
				continue;
			mSystemThemeDescription = getThemeDescription(resinfo);
			mThemeDefaultConfig = new MyThemeDescription(mContext, CONFIGBASE_FILENAME);
			break;
		}

		MyThemeDescription ThemeDesc;
		localResDesList.remove(resinfo);
		it = localResDesList.iterator();
		Log.v("asdf", "dbThemeConf.theme = "+dbThemeConf.theme);
		while (it.hasNext()) {
			resinfo = it.next();
			if (resinfo.activityInfo.applicationInfo.packageName.equals(dbThemeConf.theme)) 
			{
				ThemeDesc = getThemeDescription2(resinfo);
				mThemeDescription = ThemeDesc;
				try 
				{
					mThemeContext = mContext.createPackageContext(resinfo.activityInfo.applicationInfo.packageName,
							Context.CONTEXT_IGNORE_SECURITY);
				} 
				catch (NameNotFoundException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		mThemeDescription.mUse = true;
	}
	
	public Context getThemeContext()
	{
		return mThemeContext;
	}
	
	public int getInteger(String key) {
		int result = -1;
		result = mThemeDescription.getInteger(key);	
		if (result==-1)
		{
			result = mThemeDefaultConfig.getInteger(key);
		}
		return result;
	}

	public String getString(String key) {
		String result = null;
		result = mThemeDescription.getString(key);	
		if (result==null)
		{
			result = mThemeDefaultConfig.getString(key);
		}
		return result;
	}

}