package com.coco.theme.themebox.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;

import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;


class ThemeConfig
{
	
	public String theme;
}

interface OnThemeChangeListener
{
	
	public void OnThemeChange();
}

public class ThemeManager
{
	
	public static final String ACTION_INTENT_THEME = "com.turbotheme";
	private final PackageManager mPackageManager;
	private static Context mContext;
	private ThemeDescription mSystemThemeDescription;
	private ThemeDescription mThemeDescription;
	private ThemeDescription mThemeDefaultConfig;
	private static String customDefaultWallpaperName;
	private boolean useCustomDefaultWallpaper = false;
	private Vector<ThemeDescription> mThemeDescriptionList;
	private ThemesDB mThemesDB;
	private OnThemeChangeListener mThemeChangeListener;
	private boolean mDirty = true;
	private Bitmap mWallpaper;
	private Vector<Activity> mActivitys = new Vector<Activity>();
	
	public ThemeManager(
			Context context )
	{
		mContext = context;
		mPackageManager = context.getPackageManager();
		mThemesDB = new ThemesDB( context );
		init();
	}
	
	public ThemesDB getThemeDB()
	{
		return mThemesDB;
	}
	
	public void Release()
	{
		if( mWallpaper != null )
			mWallpaper.recycle();
	}
	
	public void RegisterListener(
			OnThemeChangeListener themelistener )
	{
		mThemeChangeListener = themelistener;
	}
	
	public void pushActivity(
			Activity activity )
	{
		mActivitys.addElement( activity );
	}
	
	public void popupActivity(
			Activity activity )
	{
		mActivitys.removeElement( activity );
	}
	
	public void KillActivity()
	{
		for( int i = 0 ; i < mActivitys.size() ; i++ )
		{
			mActivitys.elementAt( i ).finish();
		}
	}
	
	private void NotifyThemeChange()
	{
		if( mThemeChangeListener != null )
			mThemeChangeListener.OnThemeChange();
	}
	
	private ArrayList<ResolveInfo> getItemsList()
	{
		ArrayList<ResolveInfo> reslist = new ArrayList<ResolveInfo>();
		Intent intent = new Intent( ACTION_INTENT_THEME , null );
		List<ResolveInfo> themesinfo = mContext.getPackageManager().queryIntentActivities( intent , 0 );
		Collections.sort( themesinfo , new ResolveInfo.DisplayNameComparator( mContext.getPackageManager() ) );
		int themescount = themesinfo.size();
		PackageManager packmanager = mContext.getPackageManager();
		Intent systemmain = new Intent( "android.intent.action.MAIN" , null );
		systemmain.addCategory( "android.intent.category.LAUNCHER" );
		systemmain.setPackage( ThemesDB.LAUNCHER_PACKAGENAME );
		Iterator<ResolveInfo> it = packmanager.queryIntentActivities( systemmain , 0 ).iterator();
		while( it.hasNext() )
		{
			ResolveInfo resinfo = it.next();
			reslist.add( resinfo );
		}
		for( int index = 0 ; index < themescount ; index++ )
		{
			ResolveInfo resinfo = themesinfo.get( index );
			reslist.add( resinfo );
		}
		return reslist;
	}
	
	private ThemeDescription getThemeDescription(
			ResolveInfo resinfo )
	{
		Context slaveContext = null;
		try
		{
			if( !resinfo.activityInfo.applicationInfo.packageName.equals( ThemesDB.LAUNCHER_PACKAGENAME ) )
				slaveContext = mContext.createPackageContext( resinfo.activityInfo.applicationInfo.packageName , Context.CONTEXT_IGNORE_SECURITY );
			else
				slaveContext = mContext;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		ThemeDescription themeDes = CreateThemeDescription( slaveContext , resinfo );
		return themeDes;
	}
	
	private void init()
	{
		ThemeConfig dbThemeConf = mThemesDB.getTheme();
		mThemeDescriptionList = new Vector<ThemeDescription>();
		ArrayList<ResolveInfo> localResDesList = getItemsList();
		Iterator<ResolveInfo> it = localResDesList.iterator();
		ResolveInfo resinfo = null;
		boolean found = false;
		while( it.hasNext() )
		{
			resinfo = it.next();
			if( resinfo.activityInfo.applicationInfo.packageName.equals( dbThemeConf.theme ) )
			{
				found = true;
				break;
			}
		}
		if( !found )
		{
			String defaultTheme = ThemesDB.LAUNCHER_PACKAGENAME;
			if( ThemesDB.default_theme_package_name != null )
				defaultTheme = ThemesDB.default_theme_package_name;
			DBSaveTheme( defaultTheme );
		}
		it = localResDesList.iterator();
		while( it.hasNext() )
		{
			resinfo = it.next();
			if( !resinfo.activityInfo.applicationInfo.packageName.equals( ThemesDB.LAUNCHER_PACKAGENAME ) )
				continue;
			mThemeDescription = mSystemThemeDescription = getThemeDescription( resinfo );
			mSystemThemeDescription.mSystem = true;
			mThemeDescriptionList.addElement( mSystemThemeDescription );
			// mThemeDefaultConfig = new ThemeDescription(mContext,
			// CONFIGBASE_FILENAME);
			break;
		}
		ThemeDescription ThemeDesc;
		localResDesList.remove( resinfo );
		it = localResDesList.iterator();
		while( it.hasNext() )
		{
			resinfo = it.next();
			ThemeDesc = getThemeDescription( resinfo );
			if( resinfo.activityInfo.applicationInfo.packageName.equals( dbThemeConf.theme ) )
			{
				mThemeDescription = ThemeDesc;
			}
			mThemeDescriptionList.addElement( ThemeDesc );
		}
		// if (mThemeDescription.equals(mSystemThemeDescription)
		// && (dbThemeConf.theme != null &&
		// !dbThemeConf.theme.equals(ThemesDB.LAUNCHER_PACKAGENAME))) {
		// DBSaveTheme(mThemeDescription.componentName.getPackageName());
		// }
		mThemeDescription.mUse = true;
	}
	
	public ThemeDescription getSystemThemeDescription()
	{
		return mSystemThemeDescription;
	}
	
	public Context getSystemContext()
	{
		return mSystemThemeDescription.getContext();
	}
	
	public Context getContext()
	{
		return mThemeDescription.getContext();
	}
	
	public Bitmap getBitmap(
			String filename )
	{
		InputStream instr = null;
		try
		{
			instr = mThemeDescription.getContext().getAssets().open( filename );
		}
		catch( IOException e )
		{
		}
		try
		{
			if( instr == null )
			{
				instr = mSystemThemeDescription.getContext().getAssets().open( filename );
			}
		}
		catch( IOException e )
		{
		}
		return Tools.getImageFromInStream( instr );
	}
	
	public int getInteger(
			String key )
	{
		int result = -1;
		result = mThemeDescription.getInteger( key );
		// if (result == -1) {
		// result = mSystemThemeDescription.getInteger(key);
		// }
		if( result == -1 )
		{
			result = mThemeDefaultConfig.getInteger( key );
		}
		return result;
	}
	
	public String getString(
			String key )
	{
		String result = null;
		result = mThemeDescription.getString( key );
		// if (result == null) {
		// result = mSystemThemeDescription.getString(key);
		// }
		if( result == null )
		{
			result = mThemeDefaultConfig.getString( key );
		}
		return result;
	}
	
	public InputStream getFile(
			String filename )
	{
		InputStream instr = null;
		try
		{
			instr = mThemeDescription.getContext().getAssets().open( filename );
		}
		catch( IOException e )
		{
		}
		try
		{
			if( instr == null )
			{
				instr = mSystemThemeDescription.getContext().getAssets().open( filename );
			}
		}
		catch( IOException e )
		{
		}
		return instr;
	}
	
	private ThemeDescription CreateThemeDescription(
			Context context ,
			ResolveInfo resinfo )
	{
		ThemeDescription themeDescription = new ThemeDescription( context );
		themeDescription.componentName = new ComponentName( resinfo.activityInfo.applicationInfo.packageName , resinfo.activityInfo.name );
		String defaultTheme = ThemesDB.LAUNCHER_PACKAGENAME;
		if( ThemesDB.default_theme_package_name != null )
			defaultTheme = ThemesDB.default_theme_package_name;
		if( resinfo.activityInfo.applicationInfo.packageName.equals( defaultTheme ) )
			themeDescription.title = mContext.getString( R.string.defaulttheme );
		else if( resinfo.activityInfo.applicationInfo.packageName.equals( ThemesDB.LAUNCHER_PACKAGENAME ) )
			themeDescription.title = mContext.getString( R.string.system_theme );
		else
			themeDescription.title = resinfo.loadLabel( mPackageManager );
		themeDescription.mBuiltIn = ( resinfo.activityInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0;
		return themeDescription;
	}
	
	public ThemeDescription getCurrentThemeDescription()
	{
		return mThemeDescription;
	}
	
	public ThemeDescription getThemeDescriptions(
			String packbname )
	{
		for( int i = 0 ; i < mThemeDescriptionList.size() ; i++ )
		{
			if( packbname.equals( mThemeDescriptionList.elementAt( i ).componentName.getPackageName() ) )
			{
				return mThemeDescriptionList.elementAt( i );
			}
		}
		return null;
	}
	
	public Vector<ThemeDescription> getThemeDescriptions()
	{
		return mThemeDescriptionList;
	}
	
	public void UpdateTheme()
	{
	}
	
	public void RemoveTheme(
			ThemeDescription theme )
	{
		KillActivity();
		Uri packageURI = Uri.parse( "package:" + theme.componentName.getPackageName() );
		Intent intent = new Intent( Intent.ACTION_DELETE );
		intent.setData( packageURI );
		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		mContext.startActivity( intent );
	}
	
	public void ApplySystemTheme()
	{
		mThemeDescription = mSystemThemeDescription;
		mThemeDescription.mUse = true;
		DBSaveTheme( mThemeDescription.componentName.getPackageName() );
		// ApplyWallpaper();
		// mThemesDB.SaveThemesStatus(1);
		// KillActivity();
		NotifyThemeChange();
		// ShowDesktop();
		RestartSystem();
	}
	
	public void ApplyTheme(
			ThemeDescription theme )
	{
		mThemeDescription.mUse = false;
		mThemeDescription = theme;
		mThemeDescription.mUse = true;
		// mThemesDB.SaveThemesStatus(1);
		DBSaveTheme( mThemeDescription.componentName.getPackageName() );
		// ApplyWallpaper();
		NotifyThemeChange();
		KillActivity();
		RestartSystem();
	}
	
	public void ApplyWallpaper()
	{
		// customDefaultWallpaperName =
		// DefaultLayout.custom_default_wallpaper_name;
		File file = new File( customDefaultWallpaperName );
		if( file.exists() )
		{
			useCustomDefaultWallpaper = true;
		}
		WallpaperManager wpm = (WallpaperManager)mContext.getSystemService( Context.WALLPAPER_SERVICE );
		InputStream is = null;
		if( useCustomDefaultWallpaper )
		{
			try
			{
				is = new FileInputStream( customDefaultWallpaperName );
			}
			catch( FileNotFoundException e )
			{
				e.printStackTrace();
			}
		}
		else
		{
			is = mThemeDescription.getStream( "wallpaper/default.jpg" );
		}
		try
		{
			// mWallpaper =
			// mThemeDescription.getBitmap("theme/wallpaper/default.jpg");
			// wpm.setBitmap(mWallpaper);
			// wpm.setResource(R.drawable.default_wallpaper);
			wpm.setStream( ( is ) );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	private void RestartSystem()
	{
		mContext.sendBroadcast( new Intent( ThemesDB.ACTION_LAUNCHER_RESTART ) );
	}
	
	private void DBSaveTheme(
			String Packname )
	{
		ThemeConfig dbthemeconfig = new ThemeConfig();
		dbthemeconfig.theme = Packname;
		mThemesDB.SaveThemes( dbthemeconfig );
	}
	
	public boolean getDataIsDirty()
	{
		return mDirty;
	}
	
	public void Reset()
	{
		mDirty = false;
	}
	
	public void RemovePackage(
			String packageName )
	{
		Log.v( "theme" , "RemovePackage" );
		final int count = mThemeDescriptionList.size();
		for( int i = 0 ; i < count ; i++ )
		{
			if( mThemeDescriptionList.elementAt( i ).componentName.getPackageName().equals( packageName ) )
			{
				if( mThemeDescription.componentName.getPackageName().equals( packageName ) )
				{
					Log.v( "theme" , "RemovePackage = " + packageName );
					ApplySystemTheme();
				}
				else
				{
					mThemeDescriptionList.removeElementAt( i );
					mDirty = true;
				}
				break;
			}
		}
	}
	
	public void AddPackage(
			String packageName )
	{
		List<ResolveInfo> matches = FindThemesForPackage( packageName );
		ThemeDescription ThemeDesc;
		for( int index = 0 ; index < matches.size() ; index++ )
		{
			ThemeDesc = getThemeDescription( matches.get( index ) );
			mThemeDescriptionList.addElement( ThemeDesc );
			mDirty = true;
		}
	}
	
	public void UpdatePackage(
			String packageName )
	{
		if( mThemeDescription.componentName.getPackageName().equals( packageName ) )
		{
			RestartSystem();
		}
		else
		{
			RemovePackage( packageName );
			AddPackage( packageName );
		}
	}
	
	private static List<ResolveInfo> FindThemesForPackage(
			String packageName )
	{
		final PackageManager packageManager = mContext.getPackageManager();
		final Intent mainIntent = new Intent( ACTION_INTENT_THEME , null );
		mainIntent.setPackage( packageName );
		final List<ResolveInfo> apps = packageManager.queryIntentActivities( mainIntent , 0 );
		return apps != null ? apps : new ArrayList<ResolveInfo>();
	}
	
	public boolean FindThemes(
			String packageName )
	{
		final int count = mThemeDescriptionList.size();
		for( int i = 0 ; i < count ; i++ )
		{
			if( mThemeDescriptionList.elementAt( i ).componentName.getPackageName().equals( packageName ) )
			{
				return true;
			}
		}
		return false;
	}
	// private void ShowDesktop() {
	// final Intent intent = new Intent();
	// intent.setClass(mContext, iLoongLauncher.class);
	// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
	// Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	// mContext.startActivity(intent);
	// return;
	// }
}
