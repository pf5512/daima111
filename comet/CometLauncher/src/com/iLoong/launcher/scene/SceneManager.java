// wanghongjian add whole file //enable_DefaultScene
package com.iLoong.launcher.scene;


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
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import com.iLoong.launcher.Desktop3D.Log;

import com.badlogic.gdx.files.FileHandle;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.desktop.FeatureConfig;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.scene.SceneAssetFile.SceneAssetFilePrefix;


class SceneConfig
{
	
	public String themeScene;
}

interface OnSceneChangeListener
{
	
	public void OnSceneChange();
}

public class SceneManager
{
	
	private static SceneManager mInstance;
	private final PackageManager mPackageManager;
	private static Context mContext;
	private SceneDescription mSystemThemeDescription;
	private SceneDescription mThemeDescription;
	private SceneDescription mThemeDefaultConfig;
	private static String customDefaultWallpaperName;
	private static final String CONFIGBASE_FILENAME = "theme/configbase.xml";
	private boolean useCustomDefaultWallpaper = false;
	private Vector<SceneDescription> mThemeDescriptionList;
	private ScenesDB mThemesDB;
	private OnSceneChangeListener mThemeChangeListener;
	private boolean mDirty = true;
	private Bitmap mWallpaper;
	private Vector<Activity> mActivitys = new Vector<Activity>();
	private Root3D root = null;
	
	public SceneManager(
			Context context )
	{
		mInstance = this;
		mContext = context;
		mPackageManager = context.getPackageManager();
		mThemesDB = new ScenesDB( context );
		init();
	}
	
	public ScenesDB getThemeDB()
	{
		return mThemesDB;
	}
	
	public void Release()
	{
		if( mWallpaper != null )
			mWallpaper.recycle();
	}
	
	public static SceneManager getInstance()
	{
		return mInstance;
	}
	
	public void RegisterListener(
			OnSceneChangeListener themelistener )
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
			mThemeChangeListener.OnSceneChange();
	}
	
	public void setmUseByPkg(
			String pkg )
	{
		int num = 0;
		for( int i = 0 ; i < mThemeDescriptionList.size() ; i++ )
		{
			if( pkg.equals( mThemeDescriptionList.get( i ).componentName.getPackageName() ) )
			{
				mThemeDescriptionList.get( i ).mUse = true;
				num = i;
				break;
			}
		}
		for( int j = 0 ; j < mThemeDescriptionList.size() ; j++ )
		{
			if( j != num )
			{
				mThemeDescriptionList.get( j ).mUse = false;
			}
		}
	}
	
	// private ArrayList<ResolveInfo> getItemsList(String action) {
	// ArrayList<ResolveInfo> reslist = new ArrayList<ResolveInfo>();
	//
	// Intent intent = null;
	// intent = new Intent(action, null);
	// List<ResolveInfo> themesinfo =
	// mContext.getPackageManager().queryIntentActivities(intent, 0);
	// Collections.sort(themesinfo, new
	// ResolveInfo.DisplayNameComparator(mContext.getPackageManager()));
	//
	// int themescount = themesinfo.size();
	// PackageManager packmanager = mContext.getPackageManager();
	//
	// Intent systemmain = new Intent("android.intent.action.MAIN", null);
	// systemmain.addCategory("android.intent.category.LAUNCHER");
	// systemmain.setPackage(RR.getPackageName());
	//
	// Iterator<ResolveInfo> it = packmanager.queryIntentActivities(systemmain,
	// 0).iterator();
	//
	// while (it.hasNext()) {
	// ResolveInfo resinfo = it.next();
	// reslist.add(resinfo);
	// }
	//
	// for (int index = 0; index < themescount; index++) {
	// ResolveInfo resinfo = themesinfo.get(index);
	// reslist.add(resinfo);
	// }
	// return reslist;
	// }
	public boolean isCunZaiPkg(
			String pkg )
	{
		boolean ret = false;
		for( int i = 0 ; i < mThemeDescriptionList.size() ; i++ )
		{
			if( pkg.equals( mThemeDescriptionList.get( i ).componentName.getPackageName() ) )
			{
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	private ArrayList<ResolveInfo> getItemsList()
	{
		ArrayList<ResolveInfo> reslist = new ArrayList<ResolveInfo>();
		Intent intent = null;
		intent = new Intent( "com.cooee.scene" , null );
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
	
	private SceneDescription getThemeDescription(
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
		SceneDescription themeDes = CreateThemeDescription( slaveContext , resinfo );
		return themeDes;
	}
	
	private void init()
	{
		SceneConfig dbThemeConf = mThemesDB.getTheme();
		mThemeDescriptionList = new Vector<SceneDescription>();
		ArrayList<ResolveInfo> localResDesList = getItemsList();
		Iterator<ResolveInfo> it = localResDesList.iterator();
		ResolveInfo resinfo = null;
		while( it.hasNext() )
		{
			resinfo = it.next();
			if( !resinfo.activityInfo.applicationInfo.packageName.equals( mContext.getPackageName() ) )
				continue;
			mThemeDescription = mSystemThemeDescription = getThemeDescription( resinfo );
			mThemeDefaultConfig = new SceneDescription( mContext , CONFIGBASE_FILENAME );
			break;
		}
		SceneDescription ThemeDesc;
		localResDesList.remove( resinfo );
		it = localResDesList.iterator();
		while( it.hasNext() )
		{
			resinfo = it.next();
			ThemeDesc = getThemeDescription( resinfo );
			if( resinfo.activityInfo.applicationInfo.packageName.equals( dbThemeConf.themeScene ) )
			{
				mThemeDescription = ThemeDesc;
			}
			mThemeDescriptionList.addElement( ThemeDesc );
		}
		if( mThemeDescription.equals( mSystemThemeDescription ) && ( dbThemeConf.themeScene != null && !dbThemeConf.themeScene.equals( mContext.getPackageName() ) ) )
		{
			DBSaveTheme( mThemeDescription.componentName.getPackageName() );
		}
		mThemeDescription.mUse = true;
		if( mThemeDescriptionList.size() > 0 )
		{
			String pkg = null;
			String cls = null;
			Log.v( "" , "pkg is " + dbThemeConf.themeScene );
			for( int i = 0 ; i < mThemeDescriptionList.size() ; i++ )
			{
				if( dbThemeConf.themeScene.equals( mThemeDescriptionList.get( i ).componentName.getPackageName() ) )
				{
					cls = mThemeDescriptionList.get( i ).componentName.getClassName();
					pkg = mThemeDescriptionList.get( i ).componentName.getPackageName();
					break;
				}
			}
			if( cls == null )
			{
				Root3D.scenePkg = mThemeDescriptionList.get( 0 ).componentName.getPackageName();
				Root3D.sceneCls = mThemeDescriptionList.get( 0 ).componentName.getClassName();
			}
			if( cls != null && pkg != null )
			{
				Root3D.scenePkg = pkg;
				Root3D.sceneCls = cls;
			}
			if( FeatureConfig.isDefaultPkg )
			{
				for( int i = 0 ; i < mThemeDescriptionList.size() ; i++ )
				{
					if( FeatureConfig.scene_pkg.equals( mThemeDescriptionList.get( i ).componentName.getPackageName() ) )
					{
						if( FeatureConfig.scene_cls.equals( mThemeDescriptionList.get( i ).componentName.getClassName() ) )
						{
							Root3D.scenePkg = FeatureConfig.scene_pkg;
							Root3D.sceneCls = FeatureConfig.scene_cls;
							break;
						}
					}
				}
			}
			Root3D.isSceneTheme = true;
			for( int i = 0 ; i < mThemeDescriptionList.size() ; i++ )
			{
				if( Root3D.scenePkg.equals( mThemeDescriptionList.get( i ).componentName.getPackageName() ) )
				{
					mThemeDescriptionList.get( i ).mUse = true;
					break;
				}
			}
			// mThemeDescriptionList.get(0).mUse = true;
		}
	}
	
	public SceneDescription getSystemThemeDescription()
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
	
	public Bitmap getBitmapIgnoreSystemTheme(
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
		return Tools.getImageFromInStream( instr );
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
		SceneAssetFilePrefix assetFilePrefix = SceneAssetFile.getInstance().getAssetSubFile( filePrefix );
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
	
	public FileHandle getGdxTextureResource(
			String rname )
	{
		FileHandle f = null;
		f = mThemeDescription.getGdxContext().internal( rname );
		if( !f.exists() )
		{
			f = mSystemThemeDescription.getGdxContext().internal( rname );
		}
		return f;
	}
	
	public FileHandle getSysGdxTextureResource(
			String rname )
	{
		return mSystemThemeDescription.getGdxContext().internal( rname );
	}
	
	private SceneDescription CreateThemeDescription(
			Context context ,
			ResolveInfo resinfo )
	{
		SceneDescription themeDescription = new SceneDescription( context );
		themeDescription.componentName = new ComponentName( resinfo.activityInfo.applicationInfo.packageName , resinfo.activityInfo.name );
		if( resinfo.activityInfo.applicationInfo.packageName.equals( mContext.getPackageName() ) )
			themeDescription.title = mContext.getString( RR.string.defaulttheme );
		else
			themeDescription.title = resinfo.loadLabel( mPackageManager );
		themeDescription.mBuiltIn = ( resinfo.activityInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0;
		return themeDescription;
	}
	
	public SceneDescription getCurrentThemeDescription()
	{
		return mThemeDescription;
	}
	
	public Vector<SceneDescription> getThemeDescriptions()
	{
		return mThemeDescriptionList;
	}
	
	public void UpdateTheme()
	{
	}
	
	public void RemoveTheme(
			SceneDescription theme )
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
		mThemesDB.SaveThemesStatus( 1 );
		// KillActivity();
		NotifyThemeChange();
		// ShowDesktop();
		RestartSystem();
	}
	
	public void saveScenePkg(
			String pkg )
	{
		Log.v( "" , "PackageName() is " + mThemeDescription.componentName.getPackageName() );
		DBSaveTheme( pkg );
	}
	
	private void setWallpaperNewDim(
			InputStream newDimIs ,
			WallpaperManager wpm )
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream( newDimIs , null , options );
		DisplayMetrics displayMetrics = new DisplayMetrics();
		iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		final int maxDim = Math.max( displayMetrics.widthPixels , displayMetrics.heightPixels );
		final int minDim = Math.min( displayMetrics.widthPixels , displayMetrics.heightPixels );
		int mWallpaperWidth = options.outWidth;
		int mWallpaperHeight = options.outHeight;
		float scale = 1;
		if( mWallpaperWidth < minDim )
		{
			scale = (float)minDim / (float)mWallpaperWidth;
		}
		if( mWallpaperHeight * scale < maxDim )
		{
			scale = (float)maxDim / (float)mWallpaperHeight;
		}
		wpm.suggestDesiredDimensions( (int)( mWallpaperWidth * scale ) , (int)( mWallpaperHeight * scale ) );
		Log.v( "test" , "wallpaper chooser wpm.widht=" + mWallpaperWidth + " mWallpaperHeight=" + mWallpaperHeight );
	}
	
	public void ApplyWallpaper()
	{
		SharedPreferences prefs = iLoongLauncher.getInstance().getSharedPreferences( "launcher" , Activity.MODE_PRIVATE );
		customDefaultWallpaperName = DefaultLayout.custom_default_wallpaper_name;
		File file = new File( customDefaultWallpaperName );
		if( file.exists() )
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
			}
			catch( FileNotFoundException e )
			{
				e.printStackTrace();
			}
		}
		else
		{
			is = mThemeDescription.getStream( "wallpaper/default.jpg" );
			newDimIs = mThemeDescription.getStream( "wallpaper/default.jpg" );
		}
		setWallpaperNewDim( newDimIs , wpm );
		try
		{
			if( wpm.getWallpaperInfo() != null )
			{
				long _time = System.currentTimeMillis();
				long bootSpent = SystemClock.elapsedRealtime();
				long bootTime = _time - bootSpent;
				long lastApplyTime = prefs.getLong( "apply_wallpaper_time" , -1 );
				if( lastApplyTime < bootTime )
				{
					mWallpaper = Tools.getImageFromInStream( is );
					wpm.setBitmap( mWallpaper );
					mWallpaper.recycle();
					mWallpaper = null;
				}
				else
				{
					wpm.setStream( ( is ) );
				}
			}
			else
			{
				wpm.setStream( ( is ) );
			}
			prefs.edit().putLong( "apply_wallpaper_time" , System.currentTimeMillis() ).commit();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	private void RestartSystem()
	{
		SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_RESTART );
	}
	
	private void DBSaveTheme(
			String Packname )
	{
		SceneConfig dbthemeconfig = new SceneConfig();
		// dbthemeconfig.themeScene =
		// mThemeDescription.componentName.getPackageName();
		Log.v( "" , "Packname is " + Packname );
		dbthemeconfig.themeScene = Packname;
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
		if( FeatureConfig.enable_DefaultScene )
		{
			if( mThemeDescriptionList.size() > 0 )
			{
				if( mThemeDescriptionList.size() == 1 )
				{
					if( DefaultLayout.scene_change )
					{
						Handler handler = new Handler( iLoongLauncher.getInstance().getMainLooper() );
						handler.post( new Runnable() {
							
							@Override
							public void run()
							{
								// TODO Auto-generated method stub
								Intent intent = new Intent( "scene_add_two" );
								iLoongLauncher.getInstance().sendBroadcast( intent );
							}
						} );
					}
				}
				if( packageName.equals( Root3D.scenePkg ) )// 如果被删除的就是正在用的场景桌面，那就使用默认的,第一个为默认的
				{
					if( root == null )
					{
						root = iLoongLauncher.getInstance().d3dListener.root;
					}
					if( DefaultLayout.scene_change )
					{
						if( root != null )
						{
							root.setSceneTheme( mThemeDescriptionList.elementAt( 0 ).componentName.getPackageName() , mThemeDescriptionList.elementAt( 0 ).componentName.getClassName() );
							iLoongLauncher.getInstance().postRunnable( new Runnable() {
								
								@Override
								public void run()
								{
									// TODO Auto-generated method stub
									root.startScene();
								}
							} );
						}
					}
					for( int i = 0 ; i < mThemeDescriptionList.size() ; i++ )
					{
						mThemeDescriptionList.elementAt( i ).mUse = false;
					}
					mThemeDescriptionList.elementAt( 0 ).mUse = true;
				}
			}
			else
			{
				if( root == null )
				{
					root = iLoongLauncher.getInstance().d3dListener.root;
				}
				if( root != null )
				{
					root.uninstallScene();
				}
				DefaultLayout.scene_main_menu = false;
				Handler handler = new Handler( iLoongLauncher.getInstance().getMainLooper() );
				handler.post( new Runnable() {
					
					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						if( root.upSceneIntent != null )
						{
							Log.v( "" , "upSceneIntent is send" );
							iLoongLauncher.getInstance().sendBroadcast( root.upSceneIntent );
						}
					}
				} );
				changeSceneMenu( handler );
			}
		}
	}
	
	public void AddPackage(
			String packageName )
	{
		List<ResolveInfo> matches = FindThemesForPackage( packageName );
		SceneDescription ThemeDesc;
		if( matches.size() == 0 )
		{
			return;
		}
		for( int index = 0 ; index < matches.size() ; index++ )
		{
			ThemeDesc = getThemeDescription( matches.get( index ) );
			mThemeDescriptionList.addElement( ThemeDesc );
			mDirty = true;
		}
		if( SetupMenu.getInstance() != null )
		{
			// int size =
			// SetupMenu.getInstance().getSceneMenu("com.cooee.scene");
			// Log.v("", "cooeescene size is size" + size);
			// if ( SetupMenu.getInstance().getSceneMenu("com.cooee.scene") == 1
			// )
			// {
			// DefaultLayout.scene_menu_style = true;
			// SetupMenu.getInstance().LoadSetupMenuXml();
			// }
			if( SetupMenu.getInstance().getSceneMenu( "com.cooee.scene" ) == 1 )
			{
				if( root == null )
				{
					root = iLoongLauncher.getInstance().d3dListener.root;
				}
				if( root != null )
				{
					// String pkg =
					// matches.get(0).activityInfo.applicationInfo.packageName;
					// String cls = matches.get(0).activityInfo.name;
					// Log.v("", "pkg is " + pkg + " cls is " + cls);
					Handler handler = new Handler( iLoongLauncher.getInstance().getMainLooper() );
					// creatSceneMainMenu(handler);
					changeSceneMenu( handler );
					root.setSceneTheme( matches.get( 0 ).activityInfo.applicationInfo.packageName , matches.get( 0 ).activityInfo.name );
				}
				mThemeDescriptionList.get( 0 ).mUse = true;
			}
			if( SetupMenu.getInstance().getSceneMenu( "com.cooee.scene" ) == 2 )
			{
				if( DefaultLayout.scene_change )
				{
					Handler handler = new Handler( iLoongLauncher.getInstance().getMainLooper() );
					handler.post( new Runnable() {
						
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							// if (
							// iLoongLauncher.getInstance().mSceneChangeMenu ==
							// null)
							// {
							// iLoongLauncher.getInstance().mSceneChangeMenu =
							// new SetupMenu(iLoongLauncher.getInstance());
							// }
							// if ( iLoongLauncher.getInstance().mSceneMenu !=
							// null)
							// {
							// iLoongLauncher.getInstance().mSceneMenu.Release();
							// iLoongLauncher.getInstance().mSceneMenu = null;
							// }
							Intent intent = new Intent( "scene_add_two" );
							iLoongLauncher.getInstance().sendBroadcast( intent );
						}
					} );
				}
			}
		}
		// Log.v("", "matches size is " + matches.size());
	}
	
	private void changeSceneMenu(
			Handler handler )
	{
		// TODO Auto-generated method stub
		if( iLoongLauncher.getInstance() != null )
		{
			if( iLoongLauncher.getInstance().mSetupMenu != null )
			{
				iLoongLauncher.getInstance().mSetupMenu.Release();
				// Log.v("", "mSetupMenu is change");
				handler.post( new Runnable() {
					
					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						if( mThemeDescriptionList.size() > 0 )
							DefaultLayout.scene_menu_style = true;
						else
							DefaultLayout.scene_menu_style = false;
						if( DefaultLayout.scene_change )
						{
							DefaultLayout.scene_menu_style = false;
						}
						SetupMenu newSceneMenu = new SetupMenu( iLoongLauncher.getInstance() );
						iLoongLauncher.getInstance().mSetupMenu = newSceneMenu;
					}
				} );
			}
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
		intent = new Intent( "com.cooee.scene" , null );
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
}
