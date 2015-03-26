package com.iLoong.launcher.theme;


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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.UI3DEngine.ParticleLoader.ParticleFileHandle;
import com.iLoong.launcher.desktop.FeatureConfig;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.AssetFile.AssetFilePrefix;


class ThemeConfig
{
	
	public String theme;
	public String className;
}

interface OnThemeChangeListener
{
	
	public void OnThemeChange();
}

public class ThemeManager
{
	
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
	private Vector<ThemeDescription> mThemeDescriptionList;
	private ThemesDB mThemesDB;
	private OnThemeChangeListener mThemeChangeListener;
	private boolean mDirty = true;
	private Bitmap mWallpaper;
	private Vector<Activity> mActivitys = new Vector<Activity>();
	private String mClassName;
	
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
		{
			mWallpaper.recycle();
			mWallpaper = null;
		}
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
		//		if (!FeatureConfig.enable_themebox)
		//			intent = new Intent("com.iLoong.themes", null);
		//		else
		intent = new Intent( "com.cooeecomet.themes" , null );
		List<ResolveInfo> themesinfo = mContext.getPackageManager().queryIntentActivities( intent , 0 );
		Collections.sort( themesinfo , new ResolveInfo.DisplayNameComparator( mContext.getPackageManager() ) );
		int themescount = themesinfo.size();
		PackageManager packmanager = mContext.getPackageManager();
		Intent defaultintent = new Intent( "com.cooeecomet.themes.default" , null );
		//		Intent systemmain = new Intent("android.intent.action.MAIN", null);
		//		systemmain.addCategory("android.intent.category.LAUNCHER");
		//		systemmain.setPackage(RR.getPackageName());
		Iterator<ResolveInfo> it = packmanager.queryIntentActivities( defaultintent , 0 ).iterator();
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
	
	private void init()
	{
		ThemeConfig dbThemeConf = mThemesDB.getTheme();
		mThemeDescriptionList = new Vector<ThemeDescription>();
		ArrayList<ResolveInfo> installedThemeList = getInstalledThemes();
		Iterator<ResolveInfo> it = installedThemeList.iterator();
		ResolveInfo resinfo = null;
		while( it.hasNext() )
		{
			resinfo = it.next();
			if( !resinfo.activityInfo.applicationInfo.packageName.equals( mContext.getPackageName() ) )
				continue;
			setThemeClassName( resinfo.activityInfo.name );
			mThemeDescription = mSystemThemeDescription = getThemeDescription( resinfo );
			mSystemThemeDescription.mSystem = true;
			mThemeDescriptionList.addElement( mSystemThemeDescription );
			mThemeDefaultConfig = new ThemeDescription( mContext , CONFIGBASE_FILENAME );
			break;
		}
		ThemeDescription ThemeDesc;
		installedThemeList.remove( resinfo );
		it = installedThemeList.iterator();
		while( it.hasNext() )
		{
			resinfo = it.next();
			setThemeClassName( resinfo.activityInfo.name );
			ThemeDesc = getThemeDescription( resinfo );
			if( resinfo.activityInfo.applicationInfo.packageName.equals( dbThemeConf.theme ) && resinfo.activityInfo.name.equals( dbThemeConf.className ) )
			{
				mThemeDescription = ThemeDesc;
			}
			mThemeDescriptionList.addElement( ThemeDesc );
		}
		if( mThemeDescription != null )
		{
			if( mThemeDescription.equals( mSystemThemeDescription ) && ( dbThemeConf.theme != null && !dbThemeConf.theme.equals( mContext.getPackageName() ) && !dbThemeConf.className
					.equals( mThemeDescription.componentName.getClassName() ) ) )
			{
				DBSaveTheme( mThemeDescription.componentName.getPackageName() , mThemeDescription.componentName.getClassName() );
			}
			mThemeDescription.mUse = true;
		}
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
	
	//zhujieping add
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
	
	public InputStream getInputStream(
			String fileName )
	{
		InputStream instr = null;
		String filePrefix = null;
		if( fileName.contains( "/" ) )
		{
			//teapotXu add start: make difference from "theme" and "theme/iconbg"
			if( fileName.contains( "theme/iconbg/" ) )
				filePrefix = "theme/iconbg";
			//xiatian add start	//Mainmenu Bg	//load bg image only in SystemTheme
			else if( fileName.contains( "theme/pack_source/translucent-b" ) )
				filePrefix = "theme/applist_bg";
			//xiatian add end
			else
				//teapotXu add end
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
		else
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
			fileExist = getFile( assetFilePrefix.needAdapt , temFileName , mThemeDescription );
			if( fileExist )
			{
				Files files = new AndroidFiles( mThemeDescription.getContext().getAssets() );
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
		if( resinfo.activityInfo.applicationInfo.packageName.equals( mContext.getPackageName() ) )
			themeDescription.title = mContext.getString( RR.string.defaulttheme );
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
		mThemeDescription = mSystemThemeDescription;
		mThemeDescription.mUse = true;
		DBSaveTheme( mThemeDescription.componentName.getPackageName() , mThemeDescription.componentName.getClassName() );
		// ApplyWallpaper();
		mThemesDB.SaveThemesStatus( 1 );
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
		mThemesDB.SaveThemesStatus( 1 );
		DBSaveTheme( mThemeDescription.componentName.getPackageName() , mThemeDescription.componentName.getClassName() );
		// ApplyWallpaper();
		NotifyThemeChange();
		KillActivity();
		RestartSystem();
	}
	
	public void ApplyWallpaper()
	{
		//xiatian add start	//when change theme,wallpaper not show whole pic
		SharedPreferences prefs = iLoongLauncher.getInstance().getSharedPreferences( "launcher" , Activity.MODE_PRIVATE );
		String pathName = null;
		//xiatian add end
		customDefaultWallpaperName = DefaultLayout.custom_default_wallpaper_name;
		File file = new File( customDefaultWallpaperName );
		if( file.exists() && currentThemeIsSystemTheme() //xiatian add	//when change theme,wallpaper not show whole pic
		)
		{
			useCustomDefaultWallpaper = true;
		}
		WallpaperManager wpm = (WallpaperManager)mContext.getSystemService( Context.WALLPAPER_SERVICE );
		InputStream is = null;
		InputStream newDimIs = null;
		if( useCustomDefaultWallpaper )
		{
			try
			{
				is = new FileInputStream( customDefaultWallpaperName );
				newDimIs = new FileInputStream( customDefaultWallpaperName );
				pathName = customDefaultWallpaperName;//xiatian add	//when change theme,wallpaper not show whole pic
			}
			catch( FileNotFoundException e )
			{
				e.printStackTrace();
			}
		}
		else
		{
			//xiatian start	//when change theme,wallpaper not show whole pic
			//xiatian del start
			//			is = getInputStream("theme/wallpaper/default.jpg");
			//			newDimIs = getInputStream("theme/wallpaper/default.jpg");
			//xiatian del end
			//xiatian add start
			is = mThemeDescription.getStream( "wallpaper/default.jpg" );
			newDimIs = mThemeDescription.getStream( "wallpaper/default.jpg" );
			pathName = "wallpaper/default.jpg";
			//xiatian add end
			//xiatian end
		}
		try
		{
			//xiatian start	//when change theme,wallpaper not show whole pic
			//xiatian del start
			//			setWallpaperNewDim(newDimIs,wpm);
			//			wpm.setStream((is));
			//xiatian del end
			//xiatian add start
			/*避免setBitmap导致的ANR，将设定墙纸的方法统一调用setStream*/
			Bitmap scaleBmp = getBitmapFromFile( newDimIs );
			newDimIs.close();
			if( scaleBmp != null )
			{
				wpm.setBitmap( scaleBmp );
			}
			else
			{
				wpm.setStream( ( is ) );
				is.close();
			}
			setWallpaperNewDim( wpm );
			prefs.edit().putLong( "apply_wallpaper_time" , System.currentTimeMillis() ).commit();
			//xiatian add end
			//xiatian end
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	//xiatian start	//when change theme,wallpaper not show whole pic
	//xiatian del start
	//	private void setWallpaperNewDim(InputStream newDimIs, WallpaperManager wpm)
	//	 {
	//			BitmapFactory.Options options = new BitmapFactory.Options();
	//
	//			options.inJustDecodeBounds = true;
	//			BitmapFactory.decodeStream(newDimIs, null, options);
	//			DisplayMetrics displayMetrics = new DisplayMetrics();
	//			iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay()
	//					.getMetrics(displayMetrics);
	//			final int maxDim = Math.max(displayMetrics.widthPixels,
	//					displayMetrics.heightPixels);
	//			final int minDim = Math.min(displayMetrics.widthPixels,
	//					displayMetrics.heightPixels);
	//			int mWallpaperWidth = options.outWidth;
	//			int mWallpaperHeight = options.outHeight;
	//
	//			if (mWallpaperWidth < minDim)
	//				mWallpaperWidth = minDim;
	//			if (mWallpaperHeight < maxDim)
	//				mWallpaperHeight = maxDim;
	//
	//			wpm.suggestDesiredDimensions(mWallpaperWidth, mWallpaperHeight);
	//			Log.v("test", "wallpaper chooser wpm.widht="+mWallpaperWidth + " mWallpaperHeight="+mWallpaperHeight);
	//		}
	//xiatian del end
	//xiatian add start
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
	
	public Bitmap getBitmapFromFile(
			InputStream newDimIs )
	{
		if( newDimIs == null )
		{
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream( newDimIs , null , options );
		int mWallpaperWidth = options.outWidth;
		int mWallpaperHeight = options.outHeight;
		DisplayMetrics displayMetrics = new DisplayMetrics();
		iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		/*如果墙纸的大小不等于当前屏幕的两倍宽高，表示需要缩放*/
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
	
	private void setWallpaperNewDim(
			WallpaperManager wpm )
	{
		DisplayMetrics displayMetrics = new DisplayMetrics();
		iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		wpm.suggestDesiredDimensions( (int)( displayMetrics.widthPixels * 2 ) , (int)( displayMetrics.heightPixels ) );
		Log.v( "test" , "ThemeManager wallpaper displayMetrics.widthPixels=" + displayMetrics.widthPixels + " displayMetrics.heightPixels=" + displayMetrics.heightPixels );
		Log.e( "test" , "ThemeManager wallpaper chooser wpm.widht=" + wpm.getDesiredMinimumWidth() + " mWallpaperHeight=" + wpm.getDesiredMinimumHeight() );
	}
	
	//xiatian add end
	//xiatian end
	private void RestartSystem()
	{
		SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_RESTART );
	}
	
	private void DBSaveTheme(
			String Packname ,
			String className )
	{
		ThemeConfig dbthemeconfig = new ThemeConfig();
		dbthemeconfig.theme = Packname;
		dbthemeconfig.className = className;
		mThemesDB.SaveThemes( dbthemeconfig );
	}
	
	private void DBRemoveTheme(
			String Packname )
	{
		ThemeConfig dbthemeconfig = new ThemeConfig();
		dbthemeconfig.theme = mThemeDescription.componentName.getPackageName();
		dbthemeconfig.className = mThemeDescription.componentName.getClassName();
		mThemesDB.RemoveThemes( dbthemeconfig );
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
		if( !FeatureConfig.enable_themebox )
			intent = new Intent( "com.iLoong.themes" , null );
		else
			intent = new Intent( "com.cooeecomet.themes" , null );
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
	
	//xiatian add start	//adjust third apk icon offset when have iconbg
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
	
	//xiatian add end
	private void setThemeClassName(
			String str )
	{
		mClassName = str;
	}
	
	public String getThemeClassName()
	{
		return mClassName;
	}
	
	public String getThemeDir()
	{
		return mThemeDescription.getThemeDir();
	}
	
	//	public AndroidFiles getAndroidFiles(){
	//		return mThemeDescription.getAndroidFiles();
	//	}
	public String getDefaultDir()
	{
		return mThemeDescription.getDefaultDir();
	}
}
