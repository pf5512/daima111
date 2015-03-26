/* Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.iLoong.launcher.data;


import java.io.File;
import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;

import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.desktop.iLoongLauncher;


/**
 * Represents an app in AllAppsView.
 */
public class ApplicationInfo extends ItemInfo
{
	
	/**
	 * The application name.
	 */
	/**
	 * A bitmap of the application's text in the bubble.
	 */
	public Bitmap titleBitmap;
	/**
	 * The intent used to start the application.
	 */
	public Intent intent;
	/**
	 * A bitmap version of the application icon.
	 */
	public Bitmap iconBitmap;
	public ComponentName componentName;
	public int flags = 0;
	public String packageName = "";
	public long lastUpdateTime = 0;
	public boolean isHideIcon = false;
	
	ApplicationInfo()
	{
		itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT;
	}
	
	public ApplicationInfo(
			ResolveInfo info )
	{
		this.componentName = new ComponentName( info.activityInfo.applicationInfo.packageName , info.activityInfo.name );
		flags = info.activityInfo.applicationInfo.flags;
		packageName = info.activityInfo.applicationInfo.packageName;
		int sysVersion = Integer.parseInt( VERSION.SDK );
		if( sysVersion < 9 )
		{
			boolean installed = false;
			if( ( flags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP ) != 0 )
			{
				installed = true;
			}
			else if( ( flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) == 0 )
			{
				installed = true;
			}
			if( installed )
			{
				String dir = info.activityInfo.applicationInfo.publicSourceDir;
				lastUpdateTime = new File( dir ).lastModified();
			}
		}
		else
		{
			PackageManager pm = iLoongLauncher.getInstance().getPackageManager();
			try
			{
				PackageInfo packageInfo = pm.getPackageInfo( packageName , 0 );
				lastUpdateTime = packageInfo.lastUpdateTime;
			}
			catch( NameNotFoundException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		isHideIcon = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getBoolean( "HIDE:" + componentName.toString() , false );
		// Log.d("launcher",
		// "HIDE:"+intent.getComponent().toString()+"  "+isHideIcon);
		this.container = ItemInfo.NO_ID;
		this.setActivity( componentName , Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
	}
	
	/**
	 * Must not hold the Context.
	 */
	public ApplicationInfo(
			ResolveInfo info ,
			IconCache iconCache )
	{
		this.componentName = new ComponentName( info.activityInfo.applicationInfo.packageName , info.activityInfo.name );
		flags = info.activityInfo.applicationInfo.flags;
		packageName = info.activityInfo.applicationInfo.packageName;
		int sysVersion = Integer.parseInt( VERSION.SDK );
		if( sysVersion < 9 )
		{
			boolean installed = false;
			if( ( flags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP ) != 0 )
			{
				installed = true;
			}
			else if( ( flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) == 0 )
			{
				installed = true;
			}
			if( installed )
			{
				String dir = info.activityInfo.applicationInfo.publicSourceDir;
				lastUpdateTime = new File( dir ).lastModified();
			}
		}
		else
		{
			PackageManager pm = iLoongLauncher.getInstance().getPackageManager();
			try
			{
				PackageInfo packageInfo = pm.getPackageInfo( packageName , 0 );
				lastUpdateTime = packageInfo.lastUpdateTime;
			}
			catch( NameNotFoundException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		isHideIcon = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getBoolean( "HIDE:" + componentName.toString() , false );
		// Log.d("launcher",
		// "HIDE:"+intent.getComponent().toString()+"  "+isHideIcon);
		this.container = ItemInfo.NO_ID;
		this.setActivity( componentName , Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
		iconCache.getTitleAndIcon( this , info );
	}
	
	public ApplicationInfo(
			ApplicationInfo info )
	{
		super( info );
		componentName = info.componentName;
		title = info.title.toString();
		intent = new Intent( info.intent );
		flags = info.flags;
		packageName = info.packageName;
		lastUpdateTime = info.lastUpdateTime;
	}
	
	public int getUseFrequency()
	{
		int useFrequency = 0;
		if( intent != null && intent.getComponent() != null )
		{
			useFrequency = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getInt( "FREQUENCY:" + intent.getComponent().toString() , 0 );
		}
		// Log.d("launcher",
		// "intent,frequency="+intent.getComponent().toString()+","+useFrequency);
		return useFrequency;
	}
	
	public void removeUseFrequency()
	{
		if( intent != null && intent.getComponent() != null )
		{
			PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().remove( "FREQUENCY:" + intent.getComponent().toString() ).commit();
		}
	}
	
	public void removeHide()
	{
		if( intent != null && intent.getComponent() != null )
		{
			PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().remove( "HIDE:" + intent.getComponent().toString() ).commit();
		}
	}
	
	/**
	 * Creates the application intent based on a component name and various
	 * launch flags. Sets {@link #itemType} to
	 * {@link LauncherSettings.BaseLauncherColumns#ITEM_TYPE_APPLICATION}.
	 * 
	 * @param className
	 *            the class name of the component representing the intent
	 * @param launchFlags
	 *            the launch flags
	 */
	final void setActivity(
			ComponentName className ,
			int launchFlags )
	{
		intent = new Intent( Intent.ACTION_MAIN );
		intent.addCategory( Intent.CATEGORY_LAUNCHER );
		intent.setComponent( className );
		intent.setFlags( launchFlags );
		itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION;
	}
	
	@Override
	public String toString()
	{
		return "ApplicationInfo(title=" + title.toString() + ")";
	}
	
	public static void dumpApplicationInfoList(
			String tag ,
			String label ,
			ArrayList<ApplicationInfo> list )
	{
		Log.d( tag , label + " size=" + list.size() );
		for( ApplicationInfo info : list )
		{
			Log.d( tag , "   title=\"" + info.title + "\" titleBitmap=" + info.titleBitmap + " iconBitmap=" + info.iconBitmap );
		}
	}
	
	public ShortcutInfo makeShortcut()
	{
		return new ShortcutInfo( this );
	}
	
	public ApplicationInfo(
			String packageName ,
			boolean isIconToDownload )
	{
		intent = new Intent( packageName );
		iconBitmap = null;
		componentName = null;
		flags = 0;
		this.packageName = packageName;
		lastUpdateTime = 0;
		isHideIcon = false;
		title = null;
	}
}
