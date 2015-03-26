package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class ApplistVirtualIconManager
{
	
	private static ArrayList<ApplicationInfo> currentVirtualIcons = null;
	private static AppList3D mAppList;
	
	public ApplistVirtualIconManager(
			AppList3D appList )
	{
		mAppList = appList;
		currentVirtualIcons = loadAppListVirtualIcons();
	}
	
	public static ArrayList<ApplicationInfo> getCurrentVirtualIcons()
	{
		return currentVirtualIcons;
	}
	
	public static AppList3D getAppList()
	{
		return mAppList;
	}
	
	public static void setAppList(
			AppList3D applist )
	{
		mAppList = applist;
	}
	
	private static List<ResolveInfo> queryAllApps()
	{
		final Intent mainIntent = new Intent( Intent.ACTION_MAIN , null );
		mainIntent.addCategory( Intent.CATEGORY_LAUNCHER );
		final PackageManager packageManager = iLoongLauncher.getInstance().getPackageManager();
		List<ResolveInfo> apps = packageManager.queryIntentActivities( mainIntent , 0 );
		return apps;
	}
	
	public static void insertVirtualIcon()
	{
		if( currentVirtualIcons == null )
		{
			currentVirtualIcons = loadAppListVirtualIcons();
		}
		List<ResolveInfo> apps = queryAllApps();
		ArrayList<ApplicationInfo> needToAdd = new ArrayList<ApplicationInfo>();
		for( int j = 0 ; j < currentVirtualIcons.size() ; j++ )
		{
			boolean find = false;
			for( int i = 0 ; i < apps.size() ; i++ )
			{
				if( apps.get( i ).activityInfo.packageName.equals( currentVirtualIcons.get( j ).packageName ) )
				{
					find = true;
					break;
				}
			}
			if( !find )
			{
				needToAdd.add( currentVirtualIcons.get( j ) );
			}
		}
		R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
		mAppList.addApps( needToAdd , true , false );
	}
	
	public static void onAppsAdd(
			ArrayList<ApplicationInfo> apps )
	{
		mAppList.removeVirtualApps( apps );
	}
	
	public static void onAppsRemoved(
			ArrayList<ApplicationInfo> apps )
	{
		ArrayList<ApplicationInfo> needToAdd = new ArrayList<ApplicationInfo>();
		for( int j = 0 ; j < apps.size() ; j++ )
		{
			for( int i = 0 ; i < currentVirtualIcons.size() ; i++ )
			{
				if( currentVirtualIcons.get( i ).packageName.equals( apps.get( j ).packageName ) )
				{
					needToAdd.add( currentVirtualIcons.get( i ) );
				}
			}
		}
		insertVirtualIcons( needToAdd );
	}
	
	public static void insertVirtualIcons(
			final ArrayList<ApplicationInfo> virtualIcons )
	{
		// iLoongLauncher.getInstance().postRunnable(new Runnable() {
		// @Override
		// public void run() {
		// TODO Auto-generated method stub
		List<ResolveInfo> apps = queryAllApps();
		ArrayList<ApplicationInfo> needToAdd = new ArrayList<ApplicationInfo>();
		for( int j = 0 ; j < virtualIcons.size() ; j++ )
		{
			boolean find = false;
			for( int i = 0 ; i < apps.size() ; i++ )
			{
				if( apps.get( i ).activityInfo.packageName.equals( virtualIcons.get( j ).packageName ) )
				{
					find = true;
					break;
				}
			}
			if( !find )
			{
				needToAdd.add( virtualIcons.get( j ) );
			}
		}
		R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
		// mAppList.addApps(needToAdd, isSortApps,
		// isSynchronization)addApps(needToAdd, true, false);
		mAppList.addVirtualApps( needToAdd );
		// }
		// });
	}
	
	private static ArrayList<ApplicationInfo> loadAppListVirtualIcons()
	{
		int count = DefaultLayout.allAppListVirtualIcon.size();
		ArrayList<ApplicationInfo> virtualIcons = new ArrayList<ApplicationInfo>();
		for( int i = 0 ; i < count ; i++ )
		{
			String title = DefaultLayout.getAppListVirtualIconTitle( i );
			String packageName = DefaultLayout.getAppListVirtualIconPkgName( i );
			String imageName = DefaultLayout.getAppListVirtualIconImageName( i );
			ApplicationInfo appsItem = new ApplicationInfo( packageName , true );
			// iconBitmap
			//xiatian start	//New Requirement 20130507
			//			appsItem.iconBitmap = ThemeManager.getInstance().getBitmap("theme/dynamic_menu/" + imageName);	//xiatian del
			appsItem.iconBitmap = ThemeManager.getInstance().getBitmap( "theme/icon/80/" + imageName ); //xiatian add
			//xiatian end
			int w = DefaultLayout.app_icon_size;
			int h = DefaultLayout.app_icon_size;
			//xiatian start	//New Requirement 20130507
			//			if(!ThemeManager.getInstance().loadFromTheme("theme/dynamic_menu/" + imageName)){	//xiatian del			
			if( !ThemeManager.getInstance().loadFromTheme( "theme/icon/80/" + imageName ) )
			{ //xiatian add
				//xiatian end
				w *= DefaultLayout.thirdapk_icon_scaleFactor;
				h *= DefaultLayout.thirdapk_icon_scaleFactor;
			}
			appsItem.iconBitmap = Tools.resizeBitmap( appsItem.iconBitmap , w , h );
			Resources res = iLoongLauncher.getInstance().getResources();
			int identifier = res.getIdentifier( title , "string" , iLoongLauncher.getInstance().getPackageName() );
			if( identifier != 0 )
			{
				appsItem.title = res.getString( identifier );
			}
			else
			{
				appsItem.title = DefaultLayout.getAppListVirtualIconName( i );
			}
			// intent
			appsItem.intent.putExtra( "isApplistVirtualIcon" , true );
			//xiatian add start	//New Requirement 20130507
			appsItem.intent.setComponent( new ComponentName( "com.coco.lock2.lockbox" , "com.coco.lock2.lockbox.MainActivity" ) );
			//xiatian add end
			// pack image
			ShortcutInfo sInfo = appsItem.makeShortcut();
			sInfo.setIcon( appsItem.iconBitmap );
			sInfo.title = appsItem.title;
			R3D.pack( sInfo );
			virtualIcons.add( appsItem );
		}
		return virtualIcons;
	}
}
