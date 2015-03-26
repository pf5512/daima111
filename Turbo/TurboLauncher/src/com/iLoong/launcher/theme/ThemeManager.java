package com.iLoong.launcher.theme;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DrawDynamicIcon;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.ParticleLoader.ParticleFileHandle;
import com.iLoong.launcher.desktop.ThemeReceiver;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.pub.provider.PubProviderHelper;
import com.iLoong.launcher.theme.AssetFile.AssetFilePrefix;


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
	private String mCurrentThemeHomeDir = null;
	private static ThemeManager mInstance;
	private final PackageManager mPackageManager;
	private static Context mContext;
	private ThemeDescription mSystemThemeDescription;
	private ThemeDescription mThemeDescription;
	private ThemeDescription mThemeDefaultConfig;
	private static String customDefaultWallpaperName;
	private static final String CONFIGBASE_FILENAME = "theme/configbase.xml";
	private boolean useCustomDefaultWallpaper = false;
	private static String customAssetsDefaultWallpaperName;
	private boolean useCustomAssetsDefaultWallpaper = false;
	private Vector<ThemeDescription> mThemeDescriptionList;
	private ThemesDB mThemesDB;
	private OnThemeChangeListener mThemeChangeListener;
	private boolean mDirty = true;
	private Bitmap mWallpaper;
	private Vector<Activity> mActivitys = new Vector<Activity>();
	
	public ThemeManager(
			Context context )
	{
		mInstance = this;
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
		mWallpaper = null;
	}
	
	public static ThemeManager getInstance()
	{
		return mInstance;
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
	
	private ArrayList<ResolveInfo> getInstalledThemes()
	{
		ArrayList<ResolveInfo> reslist = new ArrayList<ResolveInfo>();
		Intent intent = null;
		if( !DefaultLayout.enable_themebox )
			intent = new Intent( "com.iLoong.themes" , null );
		else
			intent = new Intent( ACTION_INTENT_THEME , null );
		List<ResolveInfo> themesinfo = mContext.getPackageManager().queryIntentActivities( intent , 0 );
		Collections.sort( themesinfo , new ResolveInfo.DisplayNameComparator( mContext.getPackageManager() ) );
		int themescount = themesinfo.size();
		PackageManager packmanager = mContext.getPackageManager();
		Intent systemmain = new Intent( "android.intent.action.MAIN" , null );
		systemmain.addCategory( "android.intent.category.LAUNCHER" );
		systemmain.setPackage( RR.getPackageName() );
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
			if( !resinfo.activityInfo.applicationInfo.packageName.equals( mContext.getPackageName() ) )
				slaveContext = mContext.createPackageContext( resinfo.activityInfo.applicationInfo.packageName , Context.CONTEXT_IGNORE_SECURITY );
			else
				slaveContext = mContext;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		ThemeDescription themeDes = createThemeDescription( slaveContext , resinfo );
		return themeDes;
	}
	
	private ArrayList<ResolveInfo> getItemsList()
	{
		ArrayList<ResolveInfo> reslist = new ArrayList<ResolveInfo>();
		Intent intent = null;
		if( !DefaultLayout.enable_themebox && !DefaultLayout.use_new_theme )
			intent = new Intent( "com.iLoong.themes" , null );
		else
			intent = new Intent( ACTION_INTENT_THEME , null );
		PackageManager packmanager = iLoongApplication.getInstance().getPackageManager();
		List<ResolveInfo> themesinfo = packmanager.queryIntentActivities( intent , 0 );
		Collections.sort( themesinfo , new ResolveInfo.DisplayNameComparator( packmanager ) );
		int themescount = themesinfo.size();
		Intent systemmain = new Intent( "android.intent.action.MAIN" , null );
		systemmain.addCategory( "android.intent.category.LAUNCHER" );
		systemmain.setPackage( RR.getPackageName() );
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
	
	public boolean changeTheme()
	{
		ThemeConfig dbThemeConf = mThemesDB.getTheme();
		if( mThemeDescription.componentName.getPackageName().equals( dbThemeConf.theme ) )
			return false;
		Iterator<ThemeDescription> it = mThemeDescriptionList.iterator();
		ThemeDescription tmp;
		while( it.hasNext() )
		{
			tmp = it.next();
			if( tmp.componentName.getPackageName().equals( dbThemeConf.theme ) )
			{
				mThemeDescription.mUse = false;
				mThemeDescription = tmp;
				mThemeDescription.mUse = true;
				if( DefaultLayout.dynamic_icon )
				{
					DrawDynamicIcon.needrefreshres = true;
				}
				return true;
			}
		}
		return false;
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
			String defaultTheme = mContext.getPackageName();
			if( DefaultLayout.default_theme_package_name != null )
				defaultTheme = DefaultLayout.default_theme_package_name;
			// DBSaveTheme(defaultTheme);
			SharedPreferences prefs = mContext.getSharedPreferences( "theme" , Activity.MODE_WORLD_WRITEABLE );
			prefs.edit().putString( "theme" , defaultTheme ).commit();
			PubProviderHelper.addOrUpdateValue( "theme" , "theme" , defaultTheme );
		}
		it = localResDesList.iterator();
		while( it.hasNext() )
		{
			resinfo = it.next();
			if( !resinfo.activityInfo.applicationInfo.packageName.equals( mContext.getPackageName() ) )
				continue;
			mThemeDescription = mSystemThemeDescription = getThemeDescription( resinfo );
			mSystemThemeDescription.mSystem = true;
			mThemeDescriptionList.addElement( mSystemThemeDescription );
			mThemeDefaultConfig = new ThemeDescription( mContext , CONFIGBASE_FILENAME );
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
		// !dbThemeConf.theme.equals(mContext.getPackageName()))) {
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
	
	public Context getCurrentThemeContext()
	{
		return mThemeDescription.getContext();
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
	
	// zhujieping add
	public boolean getBoolean(
			String key )
	{
		String temp = mThemeDescription.getString( key );
		// if (result == null) {
		// result = mSystemThemeDescription.getString(key);
		// }
		if( "true".equals( temp ) )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public Bitmap getBitmap(
			String filename )
	{
		return getBitmap( getInputStream( filename ) );
	}
	
	public Bitmap getBitmap(
			String filename,boolean ifThemePic )
	{
		return getBitmap( getInputStream( filename,ifThemePic ) );
	}
	
	public Bitmap getBitmapIgnoreSystemTheme(
			String filename )
	{
		return getBitmap( getCurrentThemeInputStream( filename ) );
	}
	
	public boolean loadFromTheme(
			String fileName )
	{
		InputStream instr = null;
		String filePrefix = null;
		if( fileName.contains( "/" ) )
		{
			filePrefix = fileName.substring( 0 , fileName.indexOf( "/" ) );
		}
		else
		{
			filePrefix = fileName;
		}
		AssetFilePrefix assetFilePrefix = AssetFile.getInstance().getAssetSubFile( filePrefix );
		if( assetFilePrefix.loadFromTheme )
		{
			instr = mThemeDescription.getInputStream( assetFilePrefix.needAdapt , fileName );
			if( instr == null )
			{
				return false;
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public Bitmap getCurrentThemeBitmap(
			String fileName )
	{
		return getBitmap( getCurrentThemeInputStream( fileName ) );
	}
	
	public Bitmap getBitmap(
			InputStream inputStream )
	{
		Bitmap bitmap = null;
		if( inputStream != null )
		{
			try
			{
				bitmap = Tools.getImageFromInStream( inputStream );
				inputStream.close();
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bitmap;
	}
	
	public boolean isFileExistIgnoreSystem(
			String fileName )
	{
		InputStream instr = null;
		try
		{
			instr = mThemeDescription.getContext().getAssets().open( fileName );
		}
		catch( IOException e )
		{
		}
		if( instr != null )
		{
			try
			{
				instr.close();
				return true;
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public InputStream getCurrThemeInput(
			String path )
	{
		InputStream is = null;
		try
		{
			is = getCurrentThemeContext().getAssets().open( path );
		}
		catch( IOException e )
		{
			is = null;
		}
		return is;
	}
	
	public InputStream getInputStream(
			String fileName )
	{
		InputStream instr = null;
		String filePrefix = null;
		if( fileName.contains( "/" ) )
		{
			// teapotXu add start: make difference from "theme" and
			// "theme/iconbg"
			if( fileName.contains( "theme/iconbg/" ) )
				filePrefix = "theme/iconbg";
			// xiatian add start //Mainmenu Bg //load bg image only in
			// SystemTheme
			else if( fileName.contains( "theme/pack_source/translucent-b" ) )
				filePrefix = "theme/applist_bg";
			// xiatian add end
			else
				// teapotXu add end
				filePrefix = fileName.substring( 0 , fileName.indexOf( "/" ) );
		}
		else
		{
			filePrefix = fileName;
		}
		AssetFilePrefix assetFilePrefix = AssetFile.getInstance().getAssetSubFile( filePrefix );
		if( assetFilePrefix.loadFromTheme )
		{
			instr = mThemeDescription.getInputStream( assetFilePrefix.needAdapt , fileName );
			if( instr == null )
			{
				if( assetFilePrefix.needLoadLauncherIsNotFound )
				{
					if( mThemeDescription != mSystemThemeDescription )
					{
						instr = mSystemThemeDescription.getInputStream( assetFilePrefix.needAdapt , fileName );
						if( instr == null )
						{
							String themeName = mThemeDescription.widgettheme;
							if( fileName.contains( themeName ) )
							{
								fileName = fileName.replace( themeName , "iLoong" );
								instr = mSystemThemeDescription.getInputStream( assetFilePrefix.needAdapt , fileName );
							}
						}
					}
				}
			}
		}
		/*
		 * zhongqihong 修改，在任何情况下如果没找到该图片强制使用默认主题的图片，避免死机问题。
		 * */
		//else
		
		if(instr==null)
		{
			instr = mSystemThemeDescription.getInputStream( assetFilePrefix.needAdapt , fileName );
		}
		return instr;
	}
	
	public InputStream getInputStream(
			String fileName ,boolean ifThemePic)
	{
		InputStream instr = null;
		String filePrefix = null;
		if( fileName.contains( "/" ) )
		{
			// teapotXu add start: make difference from "theme" and
			// "theme/iconbg"
			if( fileName.contains( "theme/iconbg/" ) )
				filePrefix = "theme/iconbg";
			// xiatian add start //Mainmenu Bg //load bg image only in
			// SystemTheme
			else if( fileName.contains( "theme/pack_source/translucent-b" ) )
				filePrefix = "theme/applist_bg";
			// xiatian add end
			else
				// teapotXu add end
				filePrefix = fileName.substring( 0 , fileName.indexOf( "/" ) );
		}
		else
		{
			filePrefix = fileName;
		}
		AssetFilePrefix assetFilePrefix = AssetFile.getInstance().getAssetSubFile( filePrefix );
		if( assetFilePrefix.loadFromTheme && ifThemePic)
		{
			instr = mThemeDescription.getInputStream( assetFilePrefix.needAdapt , fileName );
			if( instr == null )
			{
				if( assetFilePrefix.needLoadLauncherIsNotFound )
				{
					if( mThemeDescription != mSystemThemeDescription )
					{
						instr = mSystemThemeDescription.getInputStream( assetFilePrefix.needAdapt , fileName );
						if( instr == null )
						{
							String themeName = mThemeDescription.widgettheme;
							if( fileName.contains( themeName ) )
							{
								fileName = fileName.replace( themeName , "iLoong" );
								instr = mSystemThemeDescription.getInputStream( assetFilePrefix.needAdapt , fileName );
							}
						}
					}
				}
			}
		}
		/*
		 * zhongqihong 修改，在任何情况下如果没找到该图片强制使用默认主题的图片，避免死机问题。
		 * */
		//else
		
		if(instr==null)
		{
			instr = mSystemThemeDescription.getInputStream( assetFilePrefix.needAdapt , fileName );
		}
		return instr;
	}
	
	/************************ added by zhenNan.ye begin ***************************/
	private boolean getFile(
			boolean autoAdapt ,
			String fileName ,
			ThemeDescription themeDescription )
	{
		boolean fileExist = false;
		Context context = themeDescription.getContext();
		InputStream instr = null;
		String tmpFileName = fileName;
		if( autoAdapt )
		{
			String filePrefix = tmpFileName.substring( 0 , tmpFileName.indexOf( "/" ) );
			if( !filePrefix.contains( "-" ) )
			{
				filePrefix = filePrefix + "-" + context.getResources().getDisplayMetrics().heightPixels + "x" + context.getResources().getDisplayMetrics().widthPixels;
			}
			// 查找精确分辨率如960*540
			try
			{
				fileName = filePrefix + tmpFileName.substring( tmpFileName.indexOf( "/" ) );
				instr = context.getAssets().open( fileName );
				if( instr != null )
				{
					fileExist = true;
				}
			}
			catch( IOException e )
			{
				instr = null;
			}
			String dpiFilePrefix = themeDescription.getAutoAdaptDir( context , filePrefix.substring( 0 , filePrefix.indexOf( "-" ) ) );
			if( instr == null )
			{
				fileName = dpiFilePrefix + tmpFileName.substring( tmpFileName.indexOf( "/" ) );
				try
				{
					instr = context.getAssets().open( fileName );
					if( instr != null )
					{
						fileExist = true;
					}
				}
				catch( IOException e )
				{
				}
			}
			// 在不带dpi的目录下寻找资源，目前系统资源统一放在不带dpi的目录，所以首先寻找不带dpi的目�?
			if( instr == null )
			{
				filePrefix = tmpFileName.substring( 0 , tmpFileName.indexOf( "/" ) );
				if( !filePrefix.equals( dpiFilePrefix ) )
				{
					try
					{
						instr = context.getAssets().open( tmpFileName );
						if( instr != null )
						{
							fileExist = true;
							fileName = tmpFileName;
						}
					}
					catch( IOException e )
					{
					}
				}
			}
		}
		else
		{
			if( instr == null )
			{
				try
				{
					instr = context.getAssets().open( tmpFileName );
					if( instr != null )
					{
						fileExist = true;
						fileName = tmpFileName;
					}
				}
				catch( IOException e )
				{
				}
			}
		}
		return fileExist;
	}
	
	public void getFileHandle(
			String fileName ,
			ParticleFileHandle fileHandle )
	{
		String filePrefix = null;
		boolean fileExist = false;
		String temFileName;
		ThemeDescription useThemeDescription;
		if( DefaultLayout.enable_new_particle )
		{
			useThemeDescription = mSystemThemeDescription;
		}
		else
		{
			useThemeDescription = mThemeDescription;
		}
		if( fileName.contains( "/" ) )
		{
			filePrefix = fileName.substring( 0 , fileName.indexOf( "/" ) );
		}
		else
		{
			filePrefix = fileName;
		}
		AssetFilePrefix assetFilePrefix = AssetFile.getInstance().getAssetSubFile( filePrefix );
		if( assetFilePrefix.loadFromTheme )
		{
			temFileName = fileName;
			fileExist = getFile( assetFilePrefix.needAdapt , temFileName , useThemeDescription );
			if( DefaultLayout.enable_new_particle )
			{
				fileExist = getFile( assetFilePrefix.needAdapt , temFileName , useThemeDescription );
			}
			if( fileExist )
			{
				Files files = new AndroidFiles( useThemeDescription.getContext().getAssets() );
				fileHandle.effectFile = files.internal( temFileName );
				temFileName = temFileName.substring( 0 , temFileName.lastIndexOf( "/" ) );
				fileHandle.imagesDir = files.internal( temFileName );
			}
		}
	}
	
	/************************ added by zhenNan.ye end ***************************/
	public InputStream getSysteThemeInputStream(
			String fileName )
	{
		return mSystemThemeDescription.getInputStream( true , fileName );
	}
	
	public InputStream getCurrentThemeInputStream(
			String fileName )
	{
		return mThemeDescription.getInputStream( true , fileName );
	}
	
	public String getSystemThemeFileDir(
			String dirName ,
			boolean autoAdapt )
	{
		return getAssetFileDir( mSystemThemeDescription.getContext() , dirName , autoAdapt );
	}
	
	public String getCurrentThemeFileDir(
			String dirName ,
			boolean autoAdapt )
	{
		return getAssetFileDir( mThemeDescription.getContext() , dirName , autoAdapt );
	}
	
	public String[] listAssetFiles(
			String fileDir )
	{
		String apkPath = null;
		Context context = null;
		apkPath = ThemeManager.getInstance().getCurrentThemeFileDir( fileDir , true );
		if( apkPath != null )
		{
			context = ThemeManager.getInstance().getCurrentThemeContext();
		}
		else
		{
			apkPath = ThemeManager.getInstance().getSystemThemeFileDir( fileDir , true );
			context = ThemeManager.getInstance().getSystemContext();
		}
		if( apkPath != null )
		{
			try
			{
				return context.getAssets().list( apkPath );
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new String[]{};
	}
	
	public String[] listCurrentThemeAssetFiles(
			String fileDir )
	{
		String apkPath = null;
		Context context = null;
		apkPath = ThemeManager.getInstance().getCurrentThemeFileDir( fileDir , true );
		context = ThemeManager.getInstance().getCurrentThemeContext();
		if( apkPath != null )
		{
			try
			{
				return context.getAssets().list( apkPath );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		return new String[]{};
	}
	
	public String getAssetFileDir(
			String dirName ,
			boolean autoAdapt )
	{
		String dir = null;
		if( mThemeDescription != mSystemThemeDescription )
		{
			dir = getCurrentThemeFileDir( dirName , autoAdapt );
		}
		if( dir == null || dir.length() == 0 )
		{
			dir = getSystemThemeFileDir( dirName , autoAdapt );
		}
		return dir;
	}
	
	public String getAssetFileDir(
			Context context ,
			String dirName ,
			boolean autoAdapt )
	{
		String defaultPrefix = "";
		String filePrefix = null;
		if( dirName.contains( "/" ) )
		{
			filePrefix = dirName.substring( 0 , dirName.indexOf( "/" ) );
			dirName = dirName.substring( dirName.indexOf( "/" ) + 1 );
			if( filePrefix.contains( "-" ) )
			{
				defaultPrefix = filePrefix.substring( 0 , filePrefix.indexOf( "-" ) );
			}
			else
			{
				defaultPrefix = filePrefix;
			}
		}
		else
		{
			filePrefix = "";
		}
		boolean find = false;
		if( autoAdapt )
		{
			if( filePrefix != null && filePrefix.length() > 0 )
			{
				if( !( filePrefix.contains( "-" ) ) )
				{
					filePrefix = this.getSpecificThemeDir( context , filePrefix );
				}
			}
			// 精确分辨率比如theme-960*540，则判断此分辨率下的文件是否存在，不存在则取theme-mdpi或者theme-xhdpi�?
			if( filePrefix.equals( this.getSpecificThemeDir( context , defaultPrefix ) ) )
			{
				find = checkDirExist( context , filePrefix , dirName );
				if( !find )
				{
					filePrefix = defaultPrefix;
				}
			}
		}
		if( !find )
		{
			find = checkDirExist( context , filePrefix , dirName );
		}
		if( find )
		{
			if( filePrefix != null && filePrefix.length() > 0 )
			{
				return filePrefix + File.separator + dirName;
			}
			else
			{
				return dirName;
			}
		}
		else
		{
			return null;
		}
	}
	
	public boolean checkDirExist(
			Context widgetContext ,
			String filePrefix ,
			String dirName )
	{
		boolean find = false;
		try
		{
			if( dirName.startsWith( "/" ) )
			{
				dirName = dirName.substring( 1 );
			}
			if( dirName.endsWith( "/" ) )
			{
				dirName = dirName.substring( 0 , dirName.lastIndexOf( "/" ) );
			}
			while( dirName.contains( "/" ) )
			{
				filePrefix = filePrefix + "/" + dirName.substring( 0 , dirName.indexOf( "/" ) );
				dirName = dirName.substring( dirName.indexOf( "/" ) + 1 );
			}
			String[] themeArray = widgetContext.getAssets().list( filePrefix );
			for( String tmpTheme : themeArray )
			{
				if( tmpTheme.equals( dirName ) )
				{
					find = true;
					break;
				}
			}
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return find;
	}
	
	private ThemeDescription createThemeDescription(
			Context context ,
			ResolveInfo resinfo )
	{
		ThemeDescription themeDescription = new ThemeDescription( context );
		themeDescription.componentName = new ComponentName( resinfo.activityInfo.applicationInfo.packageName , resinfo.activityInfo.name );
		String defaultTheme = mContext.getPackageName();
		if( DefaultLayout.default_theme_package_name != null )
			defaultTheme = DefaultLayout.default_theme_package_name;
		if( resinfo.activityInfo.applicationInfo.packageName.equals( defaultTheme ) )
			themeDescription.title = mContext.getString( RR.string.defaulttheme );
		else if( resinfo.activityInfo.applicationInfo.packageName.equals( mContext.getPackageName() ) )
			themeDescription.title = mContext.getString( RR.string.system_theme );
		else
			themeDescription.title = resinfo.loadLabel( mPackageManager );
		themeDescription.mBuiltIn = ( resinfo.activityInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0;
		return themeDescription;
	}
	
	public ThemeDescription getCurrentThemeDescription()
	{
		return mThemeDescription;
	}
	
	public Vector<ThemeDescription> getThemeDescriptions()
	{
		return mThemeDescriptionList;
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
		// mThemeDescription = mSystemThemeDescription;
		// mThemeDescription.mUse = true;
		// DBSaveTheme(mThemeDescription.componentName.getPackageName());
		// // ApplyWallpaper();
		// mThemesDB.SaveThemesStatus(1);
		// // KillActivity();
		// NotifyThemeChange();
		// // ShowDesktop();
		// RestartSystem();
		ApplyTheme( mSystemThemeDescription );
	}
	
	public void ApplyTheme(
			ThemeDescription theme )
	{
		DBSaveTheme( theme.componentName.getPackageName() );
		// ApplyWallpaper();
		NotifyThemeChange();
		KillActivity();
		RestartSystem();
	}
	
	@SuppressLint( "ServiceCast" )
	public void ApplyWallpaper()
	{
		SharedPreferences prefs = iLoongLauncher.getInstance().getSharedPreferences( "launcher" , Activity.MODE_PRIVATE );
		if( currentThemeIsSystemTheme() )
		{
			customDefaultWallpaperName = DefaultLayout.custom_default_wallpaper_name;
			File file = new File( customDefaultWallpaperName );
			if( file.exists() )
			{
				useCustomDefaultWallpaper = true;
			}
			else if( DefaultLayout.useCustomAssets )
			{
				customAssetsDefaultWallpaperName = DefaultLayout.custom_assets_path + "/theme/wallpaper/default.jpg";
				file = new File( customAssetsDefaultWallpaperName );
				if( file.exists() )
				{
					useCustomAssetsDefaultWallpaper = true;
				}
			}
		}
		else
		{
			useCustomDefaultWallpaper = false;
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
		else if( useCustomAssetsDefaultWallpaper )
		{
			try
			{
				is = new FileInputStream( customAssetsDefaultWallpaperName );
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
			Bitmap scaleBmp = getBitmapFromFile( is );
			if( is != null )
			{
				is.close();
				is = null;
			}
			if( scaleBmp != null )
			{
				is = Bitmap2InputStream( scaleBmp );
			}
			else
			{
				is = mThemeDescription.getStream( "wallpaper/default.jpg" );
			}
			wpm.setStream( is );
			if( is != null )
			{
				is.close();
				is = null;
			}
			setWallpaperNewDim( wpm );
			prefs.edit().putLong( "apply_wallpaper_time" , System.currentTimeMillis() ).commit();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		if( currentThemeIsSystemTheme() )
		{
			pref.edit().putString( "currentWallpaper" , "default" ).commit();
			PubProviderHelper.addOrUpdateValue( "wallpaper" , "currentWallpaper" , "default" );
		}
		else
		{
			pref.edit().putString( "currentWallpaper" , "other" ).commit();
			PubProviderHelper.addOrUpdateValue( "wallpaper" , "currentWallpaper" , "other" );
		}
		// teapotXu add start:换主题后，标识当前的壁纸为主题自带默认壁纸,非用户自定义图片
		pref.edit().putBoolean( "userDefinedWallpaper" , false ).commit();
		PubProviderHelper.addOrUpdateValue( "wallpaper" , "userDefinedWallpaper" , "false" );
		// teapotXu add end
		pref.edit().putBoolean( "cooeechange" , true ).commit();
		PubProviderHelper.addOrUpdateValue( "wallpaper" , "cooeechange" , "true" );
	}
	
	private void setWallpaperNewDim(
			Bitmap bitmap ,
			WallpaperManager wpm )
	{
		Log.v( "jbc" , "fuckwallpaper ThemeManager setWallpaperNewDim()" );
		DisplayMetrics displayMetrics = new DisplayMetrics();
		iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		final int maxDim = Math.max( displayMetrics.widthPixels , displayMetrics.heightPixels );
		final int minDim = Math.min( displayMetrics.widthPixels , displayMetrics.heightPixels );
		int mWallpaperWidth = bitmap.getWidth();
		int mWallpaperHeight = bitmap.getHeight();
		Log.v( "jbc" , "fuckwallpaper ThemeManager bitmap.w=" + mWallpaperWidth + " bitmap.h=" + mWallpaperHeight );
		float scale = 1;
		if( mWallpaperWidth < minDim )
		{
			scale = (float)minDim / (float)mWallpaperWidth;
		}
		if( mWallpaperHeight * scale < maxDim )
		{
			scale = (float)maxDim / (float)mWallpaperHeight;
		}
		Log.v( "jbc" , "fuckwallpaper ThemeManager getDesired w=" + wpm.getDesiredMinimumWidth() + " h=" + wpm.getDesiredMinimumHeight() );
		int mininumWidth = (int)( mWallpaperWidth * scale );
		if( DefaultLayout.disable_move_wallpaper )
		{
			mininumWidth = minDim;
		}
		int mininumHeight = maxDim;
		Log.v( "jbc" , "fuckwallpaper ThemeManager suggestDesired w=" + mininumWidth + " h=" + mininumHeight );
		wpm.suggestDesiredDimensions( mininumWidth , mininumHeight );
		Log.v( "jbc" , "fuckwallpaper ThemeManager suggestDesired done!" );
	}
	
	private void setWallpaperNewDim(
			InputStream newDimIs ,
			WallpaperManager wpm )
	{
		Log.v( "jbc" , "fuckwallpaper ThemeManager setWallpaperNewDim()" );
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream( newDimIs , null , options );
		DisplayMetrics displayMetrics = new DisplayMetrics();
		iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		final int maxDim = Math.max( displayMetrics.widthPixels , displayMetrics.heightPixels );
		final int minDim = Math.min( displayMetrics.widthPixels , displayMetrics.heightPixels );
		int mWallpaperWidth = options.outWidth;
		int mWallpaperHeight = options.outHeight;
		Log.v( "jbc" , "fuckwallpaper ThemeManager options.w=" + options.outWidth + " options.h=" + options.outHeight );
		float scale = 1;
		if( mWallpaperWidth < minDim )
		{
			scale = (float)minDim / (float)mWallpaperWidth;
		}
		if( mWallpaperHeight * scale < maxDim )
		{
			scale = (float)maxDim / (float)mWallpaperHeight;
		}
		Log.v( "jbc" , "fuckwallpaper ThemeManager getDesired w=" + wpm.getDesiredMinimumWidth() + " h=" + wpm.getDesiredMinimumHeight() );
		int mininumWidth = (int)( mWallpaperWidth * scale );
		if( DefaultLayout.disable_move_wallpaper )
		{
			mininumWidth = minDim;
		}
		int mininumHeight = maxDim;
		Log.v( "jbc" , "fuckwallpaper ThemeManager suggestDesired w=" + mininumWidth + " h=" + mininumHeight );
		wpm.suggestDesiredDimensions( mininumWidth , mininumHeight );
		Log.v( "jbc" , "fuckwallpaper ThemeManager suggestDesired done!" );
		if( newDimIs != null )
		{
			try
			{
				newDimIs.close();
			}
			catch( IOException e1 )
			{
				e1.printStackTrace();
			}
		}
	}
	
	@SuppressLint( "ServiceCast" )
	public boolean setWallpaperByPath(
			String path ,
			String defaultpath )
	{
		WallpaperManager wpm = (WallpaperManager)mContext.getSystemService( Context.WALLPAPER_SERVICE );
		InputStream is = null;
		InputStream newDimIs = null;
		boolean isScene = false;
		if( useCustomWallpaper )
		{
			try
			{
				is = new FileInputStream( customWallpaperPath + "/" + path );
				newDimIs = new FileInputStream( customWallpaperPath + "/" + path );
				isScene = true;
			}
			catch( FileNotFoundException e )
			{
				try
				{
					is = new FileInputStream( customWallpaperPath + "/" + defaultpath );
					newDimIs = new FileInputStream( customWallpaperPath + "/" + defaultpath );
				}
				catch( FileNotFoundException e1 )
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		else
		{
			AssetManager asset = mContext.getResources().getAssets();
			try
			{
				is = asset.open( wallpaperPath + "/" + path );
				newDimIs = asset.open( wallpaperPath + "/" + path );
				isScene = true;
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			if( is == null )
			{
				try
				{
					is = asset.open( wallpaperPath + "/" + defaultpath );
					newDimIs = asset.open( wallpaperPath + "/" + defaultpath );
				}
				catch( IOException e1 )
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		if( is == null )
		{
			return false;
		}
		try
		{
			Bitmap scaleBmp = getBitmapFromFile( newDimIs );
			if( scaleBmp != null )
			{
				wpm.setBitmap( scaleBmp );
			}
			else
			{
				wpm.setStream( ( is ) );
			}
			if( !DefaultLayout.disable_set_wallpaper_dimensions )
			{
				setWallpaperNewDim( wpm );
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		pref.edit().putString( "currentWallpaper" , defaultpath ).commit();
		pref.edit().putBoolean( "cooeechange" , true ).commit();
		PubProviderHelper.addOrUpdateValue( "wallpaper" , "currentWallpaper" , defaultpath );
		PubProviderHelper.addOrUpdateValue( "wallpaper" , "cooeechange" , "true" );
		return isScene;
	}
	
	private final String wallpaperPath = "launcher/wallpapers";
	private String customWallpaperPath;
	boolean useCustomWallpaper = false;
	private List<String> mThumbs = new ArrayList<String>( 24 );
	private List<String> mImages = new ArrayList<String>( 24 );
	
	public List<String> getWallpaperImages()
	{
		return mImages;
	}
	
	//	public boolean findFile(String path){
	//		
	//		InputStream is =mThemeDescription.getStream( path );
	//		if(is!=null){
	//			try{
	//				is.close();
	//			}
	//			catch(IOException e){
	//				e.printStackTrace();
	//			}
	//			return true;
	//		}
	//		else
	//			return false;
	//	}
	public void findWallpapers()
	{
		ArrayList<String> mTemp = new ArrayList<String>( 24 );
		ArrayList<String> mFound = new ArrayList<String>( 24 );
		final Resources resources = mContext.getResources();
		customWallpaperPath = DefaultLayout.custom_wallpapers_path;
		File dir = new File( customWallpaperPath );
		if( dir.exists() && dir.isDirectory() )
		{
			useCustomWallpaper = true;
		}
		AssetManager assManager = resources.getAssets();
		String[] wallpapers = null;
		try
		{
			if( useCustomWallpaper )
			{
				wallpapers = dir.list();
			}
			else
				wallpapers = assManager.list( wallpaperPath );
			for( String name : wallpapers )
			{
				Log.v( "wallpaper" , name );
				if( !name.contains( "_small" ) )
				{
					mImages.add( name );
				}
				else
				{
					mTemp.add( name );
				}
			}
			for( String name : mImages )
			{
				for( String nameTmp : mTemp )
				{
					if( name.equals( nameTmp.replace( "_small" , "" ) ) )
					{
						mThumbs.add( nameTmp );
						mFound.add( name );
						break;
					}
				}
			}
			mImages.clear();
			mImages.addAll( mFound );
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collections.sort( mImages , new ByStringValue() );
		Collections.sort( mThumbs , new ByStringValue() );
	}
	
	class ByStringValue implements Comparator<String>
	{
		
		@Override
		public int compare(
				String lhs ,
				String rhs )
		{
			// TODO Auto-generated method stub
			if( lhs.compareTo( rhs ) > 0 )
			{
				return 1;
			}
			else if( lhs.compareTo( rhs ) < 0 )
			{
				return -1;
			}
			return 0;
		}
	}
	
	// xiatian start //when change theme,wallpaper not show whole pic
	// xiatian del start
	// private void setWallpaperNewDim(InputStream newDimIs, WallpaperManager
	// wpm)
	// {
	// BitmapFactory.Options options = new BitmapFactory.Options();
	//
	// options.inJustDecodeBounds = true;
	// BitmapFactory.decodeStream(newDimIs, null, options);
	// DisplayMetrics displayMetrics = new DisplayMetrics();
	// iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay()
	// .getMetrics(displayMetrics);
	// final int maxDim = Math.max(displayMetrics.widthPixels,
	// displayMetrics.heightPixels);
	// final int minDim = Math.min(displayMetrics.widthPixels,
	// displayMetrics.heightPixels);
	// int mWallpaperWidth = options.outWidth;
	// int mWallpaperHeight = options.outHeight;
	//
	// if (mWallpaperWidth < minDim)
	// mWallpaperWidth = minDim;
	// if (mWallpaperHeight < maxDim)
	// mWallpaperHeight = maxDim;
	//
	// wpm.suggestDesiredDimensions(mWallpaperWidth, mWallpaperHeight);
	// Log.v("test", "wallpaper chooser wpm.widht="+mWallpaperWidth +
	// " mWallpaperHeight="+mWallpaperHeight);
	// }
	// xiatian del end
	// xiatian add start
	private int computeSampleSize(
			BitmapFactory.Options options ,
			int minSideLength ,
			int maxNumOfPixels )
	{
		int initialSize = computeInitialSampleSize( options , minSideLength , maxNumOfPixels );
		int roundedSize;
		if( initialSize <= 8 )
		{
			roundedSize = 1;
			while( roundedSize < initialSize )
			{
				roundedSize <<= 1;
			}
		}
		else
		{
			roundedSize = ( initialSize + 7 ) / 8 * 8;
		}
		return roundedSize;
	}
	
	private int computeInitialSampleSize(
			BitmapFactory.Options options ,
			int minSideLength ,
			int maxNumOfPixels )
	{
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = ( maxNumOfPixels == -1 ) ? 1 : (int)Math.ceil( Math.sqrt( w * h / maxNumOfPixels ) );
		int upperBound = ( minSideLength == -1 ) ? 128 : (int)Math.min( Math.floor( w / minSideLength ) , Math.floor( h / minSideLength ) );
		if( upperBound < lowerBound )
		{
			return lowerBound;
		}
		if( ( maxNumOfPixels == -1 ) && ( minSideLength == -1 ) )
		{
			return 1;
		}
		else if( minSideLength == -1 )
		{
			return lowerBound;
		}
		else
		{
			return upperBound;
		}
	}
	
	private Bitmap getBitmapFromFile(
			InputStream newDimIs )
	{
		if( newDimIs == null )
		{
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream( newDimIs , null , options );
		DisplayMetrics displayMetrics = new DisplayMetrics();
		iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		/* 如果墙纸的大小不等于当前屏幕的两倍宽高，表示需要缩放 */
		if( options.outWidth != displayMetrics.widthPixels * 2 )
		{
			// 计算图片缩放比例
			final int minSideLength = 2 * displayMetrics.widthPixels;
			options.inSampleSize = computeSampleSize( options , minSideLength , minSideLength * displayMetrics.heightPixels );
			options.inJustDecodeBounds = false;
			options.inInputShareable = true;
			options.inPurgeable = true;
			try
			{
				return BitmapFactory.decodeStream( newDimIs , null , options );
			}
			catch( OutOfMemoryError e )
			{
				return null;
			}
		}
		return null;
	}
	
	public InputStream Bitmap2InputStream(
			Bitmap bm )
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress( Bitmap.CompressFormat.JPEG , 100 , baos );
		InputStream is = new ByteArrayInputStream( baos.toByteArray() );
		if( baos != null )
		{
			try
			{
				baos.close();
				baos = null;
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		bm.recycle();
		bm = null;
		return is;
	}
	
	private void setWallpaperNewDim(
			WallpaperManager wpm )
	{
		DisplayMetrics displayMetrics = new DisplayMetrics();
		iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		wpm.suggestDesiredDimensions( (int)( displayMetrics.widthPixels * 2 ) , (int)( displayMetrics.heightPixels ) );
		Log.v( "test" , "ThemeManager wallpaper displayMetrics.widthPixels=" + displayMetrics.widthPixels + " displayMetrics.heightPixels=" + displayMetrics.heightPixels );
		Log.e( "test" , "ThemeManager wallpaper chooser wpm.widht=" + wpm.getDesiredMinimumWidth() + " mWallpaperHeight=" + wpm.getDesiredMinimumHeight() );
	}
	
	// xiatian add end
	// xiatian end
	private void RestartSystem()
	{
		if( ThemeReceiver.ACTION_LAUNCHER_RESTART == null || ThemeReceiver.ACTION_LAUNCHER_RESTART.equals( "" ) )
		{
			ThemeReceiver.ACTION_LAUNCHER_RESTART = "com.coco.launcher.restart";
		}
		mContext.sendBroadcast( new Intent( ThemeReceiver.ACTION_LAUNCHER_RESTART ) );
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
		Intent intent = null;
		if( !DefaultLayout.enable_themebox && !DefaultLayout.use_new_theme )
			intent = new Intent( "com.iLoong.themes" , null );
		else
			intent = new Intent( ACTION_INTENT_THEME , null );
		intent.setPackage( packageName );
		final List<ResolveInfo> apps = packageManager.queryIntentActivities( intent , 0 );
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
	
	public boolean findFile(
			String path )
	{
		InputStream is = mThemeDescription.getStream( path );
		if( is == null )
			return false;
		else
		{
			try
			{
				is.close();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			return true;
		}
	}
	
	private void ShowDesktop()
	{
		final Intent intent = new Intent();
		intent.setClass( mContext , iLoongLauncher.class );
		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
		mContext.startActivity( intent );
		return;
	}
	
	public String getDefaultThemDir()
	{
		return "theme";
	}
	
	public String getSpecificThemeDir(
			Context context ,
			String prefix )
	{
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return prefix + "-" + metrics.heightPixels + "x" + metrics.widthPixels;
	}
	
	public boolean themeExist(
			Context widgetContext ,
			String themeName )
	{
		boolean foundTheme = false;
		try
		{
			String[] themeArray = widgetContext.getAssets().list( "" );
			for( String tmpTheme : themeArray )
			{
				if( tmpTheme.equals( themeName ) )
				{
					foundTheme = true;
					break;
				}
			}
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return foundTheme;
	}
	
	public boolean currentThemeIsSystemTheme()
	{
		if( mThemeDescription == null )
		{
			return true;
		}
		if( mThemeDescription.mSystem )
			return true;
		else
		{
			return false;
		}
	}
	
	// xiatian add start //adjust third apk icon offset when have iconbg
	public int getSignedInteger(
			String key )
	{
		int result = -999;
		result = mThemeDescription.getSignedInteger( key );
		if( result == -999 )
		{
			result = mThemeDefaultConfig.getSignedInteger( key );
		}
		return result;
	}
	// xiatian add end
}
