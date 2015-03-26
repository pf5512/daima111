package com.cooee.searchbar.theme;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import com.cooee.searchbar.theme.WidgetAssetFile.AssetFilePrefix;


class ThemeConfig
{
	
	public String theme;
}

interface OnThemeChangeListener
{
	
	public void OnThemeChange();
}

public class WidgetThemeManager
{
	
	private static WidgetThemeManager mInstance;
	private static Context mContext;
	private WidgetThemeDescription mSystemThemeDescription;
	private WidgetThemeDescription mThemeDescription;
	private Vector<WidgetThemeDescription> mThemeDescriptionList;
	private WidgetThemesDB mThemesDB;
	
	public WidgetThemeManager(
			Context context )
	{
		mInstance = this;
		mContext = context;
		mThemesDB = new WidgetThemesDB( context );
		init();
	}
	
	public WidgetThemesDB getThemeDB()
	{
		return mThemesDB;
	}
	
	public static WidgetThemeManager getInstance()
	{
		return mInstance;
	}
	
	public static Bitmap getImageFromInStream(
			InputStream is )
	{
		Bitmap image = null;
		try
		{
			image = BitmapFactory.decodeStream( is );
		}
		catch( Exception e )
		{
		}
		return image;
	}
	
	private ArrayList<ResolveInfo> getInstalledThemes()
	{
		ArrayList<ResolveInfo> reslist = new ArrayList<ResolveInfo>();
		Intent intent = null;
		intent = new Intent( "com.coco.themes" , null );
		List<ResolveInfo> themesinfo = mContext.getPackageManager().queryIntentActivities( intent , 0 );
		Collections.sort( themesinfo , new ResolveInfo.DisplayNameComparator( mContext.getPackageManager() ) );
		int themescount = themesinfo.size();
		PackageManager packmanager = mContext.getPackageManager();
		Intent systemmain = new Intent( "android.intent.action.MAIN" , null );
		systemmain.addCategory( "android.intent.category.LAUNCHER" );
		systemmain.setPackage( mContext.getPackageName() );
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
	
	private WidgetThemeDescription getThemeDescription(
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
		WidgetThemeDescription themeDes = createThemeDescription( slaveContext , resinfo );
		return themeDes;
	}
	
	private void init()
	{
		ThemeConfig dbThemeConf = mThemesDB.getTheme();
		mThemeDescriptionList = new Vector<WidgetThemeDescription>();
		ArrayList<ResolveInfo> installedThemeList = getInstalledThemes();
		Iterator<ResolveInfo> it = installedThemeList.iterator();
		ResolveInfo resinfo = null;
		while( it.hasNext() )
		{
			resinfo = it.next();
			if( !resinfo.activityInfo.applicationInfo.packageName.equals( mContext.getPackageName() ) )
				continue;
			mThemeDescription = mSystemThemeDescription = getThemeDescription( resinfo );
			// mSystemThemeDescription.mSystem = true;
			mThemeDescriptionList.addElement( mSystemThemeDescription );
			break;
		}
		WidgetThemeDescription ThemeDesc;
		installedThemeList.remove( resinfo );
		it = installedThemeList.iterator();
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
		if( mThemeDescription.equals( mSystemThemeDescription ) && ( dbThemeConf.theme != null && !dbThemeConf.theme.equals( mContext.getPackageName() ) ) )
		{
			// DBSaveTheme(mThemeDescription.componentName.getPackageName());
		}
		// mThemeDescription.mUse = true;
	}
	
	public WidgetThemeDescription getSystemThemeDescription()
	{
		return mSystemThemeDescription;
	}
	
	public Context getSystemContext()
	{
		return mSystemThemeDescription.getThemeContext();
	}
	
	public Context getCurrentThemeContext()
	{
		return mThemeDescription.getThemeContext();
	}
	
	public Bitmap getBitmap(
			String filename )
	{
		return getBitmap( getInputStream( filename ) );
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
				bitmap = BitmapFactory.decodeStream( inputStream );
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
			filePrefix = fileName.substring( 0 , fileName.indexOf( "/" ) );
		}
		else
		{
			filePrefix = fileName;
		}
		AssetFilePrefix assetFilePrefix = WidgetAssetFile.getInstance().getAssetSubFile( filePrefix );
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
		return getAssetFileDir( mSystemThemeDescription.getThemeContext() , dirName , autoAdapt );
	}
	
	public String getCurrentThemeFileDir(
			String dirName ,
			boolean autoAdapt )
	{
		return getAssetFileDir( mThemeDescription.getThemeContext() , dirName , autoAdapt );
	}
	
	public String[] listAssetFiles(
			String fileDir )
	{
		String apkPath = null;
		Context context = null;
		apkPath = WidgetThemeManager.getInstance().getCurrentThemeFileDir( fileDir , true );
		if( apkPath != null )
		{
			context = WidgetThemeManager.getInstance().getCurrentThemeContext();
		}
		else
		{
			apkPath = WidgetThemeManager.getInstance().getSystemThemeFileDir( fileDir , true );
			context = WidgetThemeManager.getInstance().getSystemContext();
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
			// 精确分辨率比如theme-960*540，则判断此分辨率下的文件是否存在，不存在则取theme-mdpi或者theme-xhdpi等
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
	
	private WidgetThemeDescription createThemeDescription(
			Context context ,
			ResolveInfo resinfo )
	{
		WidgetThemeDescription themeDescription = new WidgetThemeDescription( context );
		// themeDescription.componentName = new ComponentName(
		// resinfo.activityInfo.applicationInfo.packageName,
		// resinfo.activityInfo.name);
		// themeDescription.mBuiltIn =
		// (resinfo.activityInfo.applicationInfo.flags &
		// android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0;
		return themeDescription;
	}
	
	public String getSpecificThemeDir(
			Context context ,
			String prefix )
	{
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return prefix + "-" + metrics.heightPixels + "x" + metrics.widthPixels;
	}
}
