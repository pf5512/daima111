package com.iLoong.launcher.recent;


import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;

import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.WidgetIcon;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.FolderInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class RecentAppBiz
{
	
	private final int COLUMN = 4;
	private final int MAX_RECENT_TASKS = 8;
	private ArrayList<RecentApp> recentAppList = null;
	private final static String TAG = "RecentAppBiz";
	ArrayList<ShortcutInfo> recentApp;
	public static ArrayList<ShortcutInfo> lockedApp = new ArrayList<ShortcutInfo>();
	private ArrayList<ShortcutInfo> removedApp = new ArrayList<ShortcutInfo>();
	
	public RecentAppBiz()
	{
		recentApp = iLoongLauncher.getInstance().recentApp;
	}
	
	//这函数只返回需要动画删除的应用
	public ArrayList<ShortcutInfo> getClearedList()
	{
		if( removedApp != null )
			removedApp.clear();
		else
			removedApp = new ArrayList<ShortcutInfo>();
		for( ShortcutInfo info : recentApp )
		{
			boolean locked = false;
			for( ShortcutInfo li : lockedApp )
			{
				if( info.intent.getComponent().getClassName().equals( li.intent.getComponent().getClassName() ) && info.intent.getComponent().getPackageName()
						.equals( li.intent.getComponent().getPackageName() ) )
				{
					locked = true;
				}
			}
			if( !locked )
			{
				removedApp.add( info );
			}
		}
		for( ShortcutInfo ra : removedApp )
		{
			Iterator<ShortcutInfo> ite = lockedApp.iterator();
			while( ite.hasNext() )
			{
				ShortcutInfo info = ite.next();
				if( info.intent.getComponent().getClassName().equals( ra.intent.getComponent().getClassName() ) && info.intent.getComponent().getPackageName()
						.equals( ra.intent.getComponent().getPackageName() ) )
				{
					ite.remove();
				}
			}
		}
		//		//如果锁定的应用小于一页
		//		if(lockedApp.size()<COLUMN)
		//		{
		//			if(lockedApp.size()+removedApp.size()>COLUMN){
		//				int start=COLUMN-lockedApp.size();
		//				ArrayList<ShortcutInfo> tem=new ArrayList<ShortcutInfo>();
		//				for(int i=start;i<removedApp.size();i++){
		//					tem.add( removedApp.get( i ) );
		//				}
		//				removedApp.removeAll(tem);
		//			}
		//		}
		//		else//如果大于一页，直接
		//		{
		//			recentApp.removeAll( removedApp );
		//		}
		return removedApp;
	}
	
	public int getLockedAppCount()
	{
		return lockedApp.size();
	}
	
	public void execClear()
	{
		recentApp.removeAll( removedApp );
	}
	
	//	public void loadRecentApps()
	//	{
	//		
	//		
	//	    if( recentAppList == null ){
	//			recentAppList = new ArrayList<RecentApp>();
	//		}
	//		recentAppList.clear();
	//		final Context context = iLoongLauncher.getInstance();
	//		final PackageManager pm = context.getPackageManager();
	//		final ActivityManager am = (ActivityManager)context.getSystemService( Context.ACTIVITY_SERVICE );
	//		final List<ActivityManager.RecentTaskInfo> recentTasks = am.getRecentTasks( 64 , ActivityManager.RECENT_IGNORE_UNAVAILABLE );
	//		ActivityInfo homeInfo = new Intent( Intent.ACTION_MAIN ).addCategory( Intent.CATEGORY_HOME ).resolveActivityInfo( pm , 0 );
	//		int index = 0;
	//		int numTasks = recentTasks.size();
	//		for( int i = 0 ; i < numTasks && ( index < MAX_RECENT_TASKS ) ; ++i )
	//		{
	//			final ActivityManager.RecentTaskInfo info = recentTasks.get( i );
	//			Intent intent = new Intent( info.baseIntent );
	//			if( info.origActivity != null )
	//			{
	//				intent.setComponent( info.origActivity );
	//			}
	//			if( homeInfo != null )
	//			{
	//				if( homeInfo.packageName.equals( intent.getComponent().getPackageName() ) && homeInfo.name.equals( intent.getComponent().getClassName() ) )
	//				{
	//					continue;
	//				}
	//			}
	//			RecentApp ra = RecentAppFactory( intent.getComponent().getPackageName() , intent.getComponent().getClassName() );
	//			if( ra != null )
	//			{
	//			
	//			
	//				recentAppList.add( ra );
	//				index++;
	//			}
	//		}
	//		if(recentAppList.size()>8)
	//			recentAppList.remove( recentAppList.size()-1 );
	//		//filterPakageName(iLoongLauncher.getInstance(),recentAppList);
	//	}
	public void loadRecentApps()
	{
		if( recentAppList == null )
		{
			recentAppList = new ArrayList<RecentApp>();
		}
		recentAppList.clear();
		for( int i = 0 ; i < recentApp.size() ; i++ )
		{
			ShortcutInfo info = recentApp.get( i );
			RecentApp ra = RecentAppFactory( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() );
			if( ra != null )
			{
				recentAppList.add( ra );
			}
		}
		ArrayList<RecentApp> removedApp = new ArrayList<RecentApp>();
		for( RecentApp ra : recentAppList )
		{
			Iterator<ShortcutInfo> ite = lockedApp.iterator();
			while( ite.hasNext() )
			{
				ShortcutInfo info = ite.next();
				ShortcutInfo rainfo = (ShortcutInfo)( ra.getItemInfo() );
				if( info.intent.getComponent().getClassName().equals( rainfo.intent.getComponent().getClassName() ) && info.intent.getComponent().getPackageName()
						.equals( rainfo.intent.getComponent().getPackageName() ) )
				{
					removedApp.add( ra );
				}
			}
		}
		recentAppList.removeAll( removedApp );
		ArrayList<RecentApp> favarite = new ArrayList<RecentApp>();
		for( ShortcutInfo info : lockedApp )
		{
			RecentApp ra = RecentAppFactory( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() );
			if( ra != null )
			{
				ra.isLocked = true;
				favarite.add( ra );
			}
		}
		recentAppList.addAll( 0 , favarite );
		if( recentAppList.size() > 8 )
		{
			for( int i = 8 ; i < recentAppList.size() ; i++ )
			{
				recentAppList.remove( i );
			}
		}
	}
	
	public int getRecentAppCount()
	{
		return recentAppList.size() - removedApp.size();
	}
	
	public void resetClearState()
	{
		removedApp.clear();
	}
	
	public void addLockeddApp(
			ShortcutInfo tag )
	{
		Iterator<ShortcutInfo> ite = lockedApp.iterator();
		while( ite.hasNext() )
		{
			ShortcutInfo info = ite.next();
			if( info.intent.getComponent().getClassName().equals( tag.intent.getComponent().getClassName() ) && info.intent.getComponent().getPackageName()
					.equals( tag.intent.getComponent().getPackageName() ) )
			{
				return;
			}
		}
		lockedApp.add( tag );
	}
	
	public void deleteApp(
			ShortcutInfo tag )
	{
		Iterator<ShortcutInfo> ite = lockedApp.iterator();
		while( ite.hasNext() )
		{
			ShortcutInfo info = ite.next();
			if( info.intent.getComponent().getClassName().equals( tag.intent.getComponent().getClassName() ) && info.intent.getComponent().getPackageName()
					.equals( tag.intent.getComponent().getPackageName() ) )
			{
				ite.remove();
			}
		}
		Iterator<ShortcutInfo> raite = recentApp.iterator();
		while( raite.hasNext() )
		{
			ShortcutInfo info = raite.next();
			if( info.intent.getComponent().getClassName().equals( tag.intent.getComponent().getClassName() ) && info.intent.getComponent().getPackageName()
					.equals( tag.intent.getComponent().getPackageName() ) )
			{
				raite.remove();
			}
		}
	}
	
	public void removeLockedApp(
			ShortcutInfo tag )
	{
		Iterator<ShortcutInfo> ite = lockedApp.iterator();
		while( ite.hasNext() )
		{
			ShortcutInfo info = ite.next();
			if( info.intent.getComponent().getClassName().equals( tag.intent.getComponent().getClassName() ) && info.intent.getComponent().getPackageName()
					.equals( tag.intent.getComponent().getPackageName() ) )
			{
				ite.remove();
			}
		}
	}
	
	public ArrayList<RecentApp> getRecentAppList()
	{
		loadRecentApps();
		getUsefulData( recentAppList );
		return recentAppList;
	}
	
	public void getUsefulData(
			ArrayList<RecentApp> list )
	{
	}
	
	public static RecentApp RecentAppFactory(
			String comName ,
			String clsName )
	{
		RecentApp cooeeicon = null;
		if( AppHost3D.appList.mApps != null )
		{
			if( AppHost3D.appList.mApps.size() == 0 )
			{
				if( AppHost3D.appList.mItemInfos != null && AppHost3D.appList.mItemInfos.size() > 0 )
				{
					for( int i = 0 ; i < AppHost3D.appList.mItemInfos.size() ; i++ )
					{
						if( AppHost3D.appList.mItemInfos.get( i ) instanceof ApplicationInfo )
						{
							ApplicationInfo info = (ApplicationInfo)AppHost3D.appList.mItemInfos.get( i );
							if( info.intent.getComponent() != null )
							{
								if( info.intent.getComponent().getPackageName().equalsIgnoreCase( comName ) && info.intent.getComponent().getClassName().equalsIgnoreCase( clsName ) )
								{
									ShortcutInfo sInfo = info.makeShortcut();
									String appName = R3D.getInfoName( sInfo );
									Icon3D icon = AppHost3D.appList.iconMap.get( appName );
									if( icon != null )
									{
										cooeeicon = new RecentApp( icon.name , icon.region );
										cooeeicon.setItemInfo( new ShortcutInfo( (ShortcutInfo)icon.getItemInfo() ) );
										cooeeicon.setSize( icon.getWidth() , icon.getHeight() );
										return cooeeicon;
									}
								}
							}
						}
						else if( AppHost3D.appList.mItemInfos.get( i ) instanceof FolderInfo )
						{
							FolderInfo info = (FolderInfo)AppHost3D.appList.mItemInfos.get( i );
							ArrayList<ShortcutInfo> contents = ( (UserFolderInfo)info ).contents;
							if( contents != null && contents.size() > 0 )
							{
								for( int j = 0 ; j < contents.size() ; j++ )
								{
									ShortcutInfo sInfo = contents.get( j );
									if( sInfo.intent.getComponent().getPackageName().equalsIgnoreCase( comName ) && sInfo.intent.getComponent().getClassName().equalsIgnoreCase( clsName ) )
									{
										String appName = R3D.getInfoName( sInfo );
										cooeeicon = new RecentApp( appName , R3D.findRegion( sInfo ) );
										if( cooeeicon != null )
										{
											cooeeicon.setItemInfo( sInfo );
											return cooeeicon;
										}
									}
								}
							}
						}
					}
				}
			}
			else
			{
				for( int i = 0 ; i < AppHost3D.appList.mApps.size() ; i++ )
				{
					ApplicationInfo info = AppHost3D.appList.mApps.get( i );
					if( info.intent.getComponent() != null )
					{
						ShortcutInfo sInfo = info.makeShortcut();
						String appName = R3D.getInfoName( sInfo );
						Icon3D icon = AppHost3D.appList.iconMap.get( appName );
						if( icon != null )
						{
							cooeeicon = new RecentApp( icon.name , icon.region );
							cooeeicon.setItemInfo( new ShortcutInfo( (ShortcutInfo)icon.getItemInfo() ) );
							cooeeicon.setSize( icon.getWidth() , icon.getHeight() );
							return cooeeicon;
						}
					}
				}
			}
		}
		//处理桌面的虚拟图标
		if( cooeeicon == null )
		{
			Workspace3D workspace = Workspace3D.getInstance();
			for( int i = 0 ; i < workspace.getChildCount() ; i++ )
			{
				if( workspace.getChildAt( i ) instanceof ViewGroup3D )
				{
					ViewGroup3D gv = (ViewGroup3D)workspace.getChildAt( i );
					for( int j = 0 ; j < gv.getChildCount() ; j++ )
					{
						View3D v = gv.getChildAt( j );
						if( v instanceof WidgetIcon )
						{
							WidgetIcon wi = (WidgetIcon)v;
							ShortcutInfo info = (ShortcutInfo)wi.getItemInfo();
							String pkgname = info.intent.getComponent().getPackageName();
							if( pkgname.equalsIgnoreCase( comName ) )
							{
								Log.v( TAG , "find WidgetIcon " + pkgname );
								cooeeicon = new RecentApp( wi.name , wi.region );
								cooeeicon.setItemInfo( new ShortcutInfo( info ) );
								cooeeicon.setSize( wi.getWidth() , wi.getHeight() );
								return cooeeicon;
							}
						}
					}
				}
			}
		}
		return cooeeicon;
	}
	
	private boolean isBackgroundRunning(
			String processName )
	{
		// = "match.android.activity";
		ActivityManager activityManager = (ActivityManager)iLoongLauncher.getInstance().getSystemService( iLoongLauncher.getInstance().ACTIVITY_SERVICE );
		KeyguardManager keyguardManager = (KeyguardManager)iLoongLauncher.getInstance().getSystemService( iLoongLauncher.getInstance().KEYGUARD_SERVICE );
		if( activityManager == null )
			return false;
		// get running application processes
		List<ActivityManager.RunningAppProcessInfo> processList = activityManager.getRunningAppProcesses();
		for( ActivityManager.RunningAppProcessInfo process : processList )
		{
			if( process.processName.startsWith( processName ) )
			{
				boolean isBackground = process.importance != IMPORTANCE_FOREGROUND && process.importance != IMPORTANCE_VISIBLE;
				boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
				if( isBackground || isLockedState )
					return true;
				else
					return false;
			}
		}
		return false;
	}
	
	public static boolean isServiceRunning(
			Context mContext ,
			String className )
	{
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager)mContext.getSystemService( Context.ACTIVITY_SERVICE );
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices( 30 );
		if( !( serviceList.size() > 0 ) )
		{
			return false;
		}
		for( int i = 0 ; i < serviceList.size() ; i++ )
		{
			if( serviceList.get( i ).service.getClassName().equals( className ) == true )
			{
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
	
	private String filterPakageName(
			Context context ,
			ArrayList<RecentApp> list )
	{
		String processName = "";
		ActivityManager am = (ActivityManager)context.getSystemService( context.ACTIVITY_SERVICE );
		List l = am.getRunningAppProcesses();
		for( int m = 0 ; m < list.size() ; m++ )
		{
			int j = 0;
			for( j = 0 ; j < l.size() ; j++ )
			{
				ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)( l.get( j ) );
				Intent intent = null;
				if( list.get( m ).getItemInfo() instanceof ShortcutInfo )
				{
					ShortcutInfo mShortcutInfo = (ShortcutInfo)list.get( m ).getItemInfo();
					if( mShortcutInfo.appInfo != null )
					{
						intent = mShortcutInfo.intent;
						processName = intent.getComponent().getPackageName().toString();
						//Log.v( TAG , "processName: " + processName );
						if( info.processName.equals( processName ) )
						{
							//	Log.v( TAG , "equals processName: " + processName );
							break;
						}
					}
				}
			}
			if( j >= l.size() )
			{
				//				ShortcutInfo mShortcutInfo = (ShortcutInfo)list.get( m ).getItemInfo();
				//				if( mShortcutInfo.appInfo != null )
				//				{
				//					 Intent intent = mShortcutInfo.intent;
				//					  Log.v( TAG , "remove processName: " + intent.getComponent().getPackageName().toString() );
				//				}
				list.remove( m );
			}
		}
		return processName;
	}
}
