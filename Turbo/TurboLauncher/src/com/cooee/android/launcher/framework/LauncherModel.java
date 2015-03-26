/* Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.cooee.android.launcher.framework;


import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.AppList3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreview3D;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.Widget3D.Contact3DShortcut;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.app.AppListDB;
import com.iLoong.launcher.core.DeferredHandler;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.CooeePluginInfo;
import com.iLoong.launcher.data.FolderInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.LiveFolderInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.data.Widget2DInfo;
import com.iLoong.launcher.data.Widget3DInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


/**
 * Maintains in-memory state of the Launcher. It is expected that there should
 * be only one LauncherModel object held in a static. Also provide APIs for
 * updating the database state for the Launcher.
 */
public class LauncherModel extends BroadcastReceiver
{
	
	static final boolean DEBUG_LOADERS = false;
	static final String TAG = "Launcher.Model";
	public static Object db_lock = new Object();
	private static final int ITEMS_CHUNK = 6; // batch size for the workspace icons
	private int mBatchSize; // 0 is all apps at once
	private int mAllAppsLoadDelay; // milliseconds between batches
	private final iLoongApplication mApp;
	private final Object mLock = new Object();
	private DeferredHandler mHandler = new DeferredHandler();
	private LoaderTask mLoaderTask;
	private int loadTaskId = 0;
	private static final HandlerThread sWorkerThread = new HandlerThread( "launcher-loader" );
	public static boolean loadAppListPreDone = false;
	static
	{
		sWorkerThread.start();
	}
	private static final Handler sWorker = new Handler( sWorkerThread.getLooper() );
	// We start off with everything not loaded. After that, we assume that
	// our monitoring of the package manager provides all updates and we never
	// need to do a requery. These are only ever touched from the loader thread.
	public static boolean mWorkspaceLoaded;
	private boolean mAllAppsLoaded;
	private boolean mAllWidgetsLoaded;
	private WeakReference<Callbacks> mCallbacks;
	public List<ResolveInfo> apps = null;
	public int[] sortArray = null;
	private AllAppsList mAllAppsList; // only access in worker thread
	private ArrayList<Object> mAllWidgets;
	// teapotXu add start
	private ArrayList<ApplicationInfo> mAppsInMainmenuFolder = new ArrayList<ApplicationInfo>();
	// teapotXu add end
	private IconCache mIconCache;
	public final ArrayList<ItemInfo> mItems = new ArrayList<ItemInfo>();
	final ArrayList<ItemInfo> mCustomShortcutItems = new ArrayList<ItemInfo>();
	final ArrayList<Widget2DInfo> mAppWidgets = new ArrayList<Widget2DInfo>();
	final ArrayList<Widget3DInfo> mWidget3Ds = new ArrayList<Widget3DInfo>();
	final HashMap<Long , FolderInfo> mFolders = new HashMap<Long , FolderInfo>();
	final ArrayList<ShortcutInfo> mWidgetView = new ArrayList<ShortcutInfo>();
	final ArrayList<CooeePluginInfo> mCooeePlugins = new ArrayList<CooeePluginInfo>();
	public final ArrayList<ItemInfo> mSidebarItems = new ArrayList<ItemInfo>();
	// xiatian start //for mainmenu sort by user
	// final HashMap<String, Integer> appListItems = new HashMap<String,
	// Integer>();//xiatian del
	final HashMap<String , ShortcutInfo> appListItems = new HashMap<String , ShortcutInfo>();// xiatian
																								// add
	// xiatian end
	final HashMap<Long , FolderInfo> appListFolders = new HashMap<Long , FolderInfo>();
	// private Bitmap mDefaultIcon;
	public static boolean waitSidebar = true;
	public static boolean waitD3dInit = true;
	public static boolean waitBefore3DWidget = true;
	public static boolean waitBefore2DWidget = true;
	public static boolean waitBindApp = true;
	private Object lock_3dwidget = new Object();
	private Object lock_2dwidget = new Object();
	public static Object lock_allapp = new Object();
	static Uri retAddBDResult;
	public static boolean appListLoaded = false;
	
	public DeferredHandler getDeferredHandler()
	{
		return mHandler;
	}
	
	public static Handler getWorkerThread()
	{
		return sWorker;
	}
	
	public interface Callbacks
	{
		
		public boolean setLoadOnResume();
		
		public boolean paused();
		
		public int getCurrentWorkspaceScreen();
		
		public void startBinding();
		
		public void bindItemsOnThread(
				ArrayList<ItemInfo> shortcuts ,
				int start ,
				int end );
		
		public void bindItems(
				ArrayList<ItemInfo> shortcuts ,
				int start ,
				int end );
		
		public void bindCustomShortcutItems(
				ArrayList<ItemInfo> shortcuts ,
				int start ,
				int end );
		
		public void bindFolders(
				HashMap<Long , FolderInfo> folders );
		
		public void bindWidgetView(
				ArrayList<ShortcutInfo> arrList );
		
		public void finishBindWorkspace();
		
		public void bindAppWidget(
				Widget2DInfo info );
		
		public void bindWidget3D(
				Widget3DInfo info );
		
		public void bindCooeePlugin(
				CooeePluginInfo info );
		
		public void bindSidebarItems(
				ArrayList<ItemInfo> info );// xp_20120425
		
		public void afterBindSidebarItems();// xp_20120425
		
		public void finishBindApplications();
		
		public void bindAppListFolders(
				ArrayList<FolderInfo> folders );
		
		public void bindAppListFoldersAdded(
				ArrayList<FolderInfo> folders );
		
		public void bindAllApplications(
				ArrayList<ApplicationInfo> apps );
		
		public void bindAppsAdded(
				ArrayList<ApplicationInfo> apps );
		
		public void bindAppsUpdated(
				ArrayList<ApplicationInfo> apps );
		
		public void bindAppsRemoved(
				ArrayList<ApplicationInfo> apps ,
				boolean permanent );
		
		public boolean isAllAppsVisible();
		
		public void bindWidget3DAdded(
				String packageName );
		
		public void bindWidget3DUpdated(
				String packageName );
		
		public void bindWidget3DRemoved(
				String packageName );
		
		public void bindAllWidgets(
				ArrayList<Object> list );
		
		public void startBindingTrue();
	}
	
	public LauncherModel(
			iLoongApplication app ,
			IconCache iconCache )
	{
		mApp = app;
		mAllAppsList = new AllAppsList( iconCache );
		mAllWidgets = new ArrayList<Object>();
		mIconCache = iconCache;
		// mDefaultIcon = Utilities.createIconBitmap(app.getPackageManager()
		// .getDefaultActivityIcon(), app);
		// mDefaultIcon = makeDefaultIcon(app.getPackageManager()
		// .getDefaultActivityIcon());
		// mDefaultIcon =
		// Utilities.createIconBitmap(mApp.getApplicationContext()
		// .getResources().getDrawable(R.drawable.ic_contact)
		// , app);
		mAllAppsLoadDelay = 0;
		mBatchSize = 10;
		waitSidebar = true;
		waitD3dInit = true;
	}
	
	public Bitmap getFallbackIcon()
	{
		return null;
		// return Bitmap.createBitmap(mDefaultIcon);
	}
	
	/**
	 * Adds an item to the DB if it was not created previously, or move it to a
	 * new <container, screen, cellX, cellY>
	 */
	public static void addOrMoveItemInDatabase(
			Context context ,
			ItemInfo item ,
			long container ,
			int screen ,
			int X ,
			int Y )
	{
		// teapotXu add start for longClick in workspace to enter EditMode like
		// miui
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && Workspace3D.b_editmode_include_addpage == true )
		{
			if( container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
			{
				screen--;
				if( screen < 0 )
				{
					screen = 0;
				}
			}
		}
		// teapotXu add end
		if( item.container == ItemInfo.NO_ID || item.container >= LauncherSettings.Favorites.CONTAINER_APPLIST )
		{
			// From all apps
			addItemToDatabase( context , item , container , screen , X , Y , item.cellX , item.cellY , false );
		}
		else
		{
			// From somewhere else
			moveItemInDatabase( context , item , container , screen , X , Y , item.cellX , item.cellY );
		}
	}
	
	public static void addOrMoveItemInDatabase(
			Context context ,
			ItemInfo item ,
			long container ,
			int screen ,
			int X ,
			int Y ,
			int spanX ,
			int spanY )
	{
		// teapotXu add start for longClick in workspace to enter EditMode like
		// miui
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && Workspace3D.b_editmode_include_addpage == true )
		{
			if( container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
			{
				screen--;
				if( screen < 0 )
				{
					screen = 0;
				}
			}
		}
		// teapotXu add end
		if( item.container == ItemInfo.NO_ID || item.container >= LauncherSettings.Favorites.CONTAINER_APPLIST )
		{
			// From all apps
			addItemToDatabase( context , item , container , screen , X , Y , item.cellX , item.cellY , false );
		}
		else
		{
			// From somewhere else
			moveItemInDatabase( context , item , container , screen , X , Y , item.cellX , item.cellY , spanX , spanY );
		}
	}
	
	public static void addOrMoveItemInDatabase(
			Context context ,
			ItemInfo item ,
			long container ,
			int screen ,
			int X ,
			int Y ,
			Intent intent )
	{
		//teapotXu add start for longClick in workspace to enter EditMode like miui
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			if( container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
			{
				screen--;
				if( screen < 0 )
				{
					screen = 0;
				}
			}
		}
		//teapotXu add end
		if( item.container == ItemInfo.NO_ID || item.container >= LauncherSettings.Favorites.CONTAINER_APPLIST )
		{
			// From all apps
			addItemToDatabase( context , item , container , screen , X , Y , item.cellX , item.cellY , false , intent );
		}
		else
		{
			// From somewhere else
			moveItemInDatabase( context , item , container , screen , X , Y , item.cellX , item.cellY );
		}
	}
	
	/**
	 * Move an item in the DB to a new <container, screen, cellX, cellY>
	 */
	public static void moveItemInDatabase(
			Context context ,
			ItemInfo item ,
			long container ,
			int screen ,
			int X ,
			int Y ,
			int cellX ,
			int cellY )
	{
		item.container = container;
		item.screen = screen;
		item.x = X;
		item.y = Y;
		item.cellX = cellX;
		item.cellY = cellY;
		final Uri uri;
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			uri = LauncherSettings.Favorites.getContentUriBigIcon( item.id , false );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			uri = LauncherSettings.Favorites.getContentUriSmallIcon( item.id , false );
		}
		else
		{
			uri = LauncherSettings.Favorites.getContentUri( item.id , false );
		}
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();
		values.put( LauncherSettings.Favorites.CONTAINER , item.container );
		values.put( LauncherSettings.Favorites.X , item.x );
		values.put( LauncherSettings.Favorites.Y , item.y );
		values.put( LauncherSettings.Favorites.CELLX , item.cellX );
		values.put( LauncherSettings.Favorites.CELLY , item.cellY );
		values.put( LauncherSettings.Favorites.SCREEN , item.screen );
		values.put( LauncherSettings.Favorites.ANGLE , item.angle );
		if( DefaultLayout.post_database )
		{
			sWorker.post( new Runnable() {
				
				public void run()
				{
					synchronized( db_lock )
					{
						cr.update( uri , values , null , null );
					}
				}
			} );
		}
		else
		{
			synchronized( db_lock )
			{
				cr.update( uri , values , null , null );
			}
		}
	}
	
	public static void moveItemInDatabase(
			Context context ,
			ItemInfo item ,
			long container ,
			int screen ,
			int X ,
			int Y ,
			int cellX ,
			int cellY ,
			int spanX ,
			int spanY )
	{
		item.container = container;
		item.screen = screen;
		item.x = X;
		item.y = Y;
		item.cellX = cellX;
		item.cellY = cellY;
		item.spanX = spanX;
		item.spanY = spanY;
		final Uri uri;
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			uri = LauncherSettings.Favorites.getContentUriBigIcon( item.id , false );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			uri = LauncherSettings.Favorites.getContentUriSmallIcon( item.id , false );
		}
		else
		{
			uri = LauncherSettings.Favorites.getContentUri( item.id , false );
		}
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();
		values.put( LauncherSettings.Favorites.CONTAINER , item.container );
		values.put( LauncherSettings.Favorites.X , item.x );
		values.put( LauncherSettings.Favorites.Y , item.y );
		values.put( LauncherSettings.Favorites.CELLX , item.cellX );
		values.put( LauncherSettings.Favorites.CELLY , item.cellY );
		values.put( LauncherSettings.Favorites.SPANX , item.spanX );
		values.put( LauncherSettings.Favorites.SPANY , item.spanY );
		values.put( LauncherSettings.Favorites.SCREEN , item.screen );
		values.put( LauncherSettings.Favorites.ANGLE , item.angle );
		if( DefaultLayout.post_database )
		{
			sWorker.post( new Runnable() {
				
				public void run()
				{
					synchronized( db_lock )
					{
						cr.update( uri , values , null , null );
					}
				}
			} );
		}
		else
		{
			synchronized( db_lock )
			{
				cr.update( uri , values , null , null );
			}
		}
	}
	
	public static String getPackageName(
			String src )
	{
		if( src == null )
			return "";
		int length = src.length();
		int start = 0;
		int end = 0;
		String part = src;
		String leftName = src;
		String limit = "component="; /* delimiter */
		if( length <= 0 )
			return "";
		end = leftName.indexOf( limit );
		if( end == -1 )
		{
			return src;
		}
		// leftName = leftName.substring(end + 1);
		int nextEnd = -1;
		String str = "/";
		nextEnd = src.indexOf( str , end );
		String midStr = src.substring( end + 1 , nextEnd );
		return midStr;
	}
	
	public static String GetPureCompanentString(
			String src )
	{
		if( src == null )
			return "";
		int length = src.length();
		int start = 0;
		int end = 0;
		String part = src;
		String leftName = src;
		String limit = "launchFlags"; /* delimiter */
		if( length <= 0 )
			return "";
		end = leftName.indexOf( limit );
		if( end == -1 )
		{
			return src;
		}
		else
			part = leftName.substring( start , end );
		leftName = leftName.substring( end + 1 );
		int nextEnd = -1;
		String str = ";";
		nextEnd = leftName.indexOf( str );
		String midStr = src.substring( end + 1 , end + nextEnd );
		leftName = src.substring( end + nextEnd + 2 );
		part += leftName;
		return part;
	}
	
	public static boolean GetIntentMatchString(
			String dest ,
			String src )
	{
		if( src == null )
			return false;
		int length = src.length();
		int start = 0;
		int end = 0;
		String part = src;
		String leftName = src;
		String limit = "launchFlags"; /* delimiter */
		if( length <= 0 )
			return false;
		// get first part
		end = leftName.indexOf( limit );
		if( end == -1 )
		{
			part = leftName;
			if( dest.equals( part ) )
			{
				return true;
			}
			else
				return false;
		}
		else
			part = leftName.substring( start , end );
		if( end == -1 )
			leftName = null;
		else
			leftName = leftName.substring( end + 1 );
		int nextEnd = -1;
		String str = ";";
		nextEnd = leftName.indexOf( str );
		if( nextEnd == -1 )
		{
			if( dest.equals( part ) )
				return true;
			else
				return false;
		}
		String midStr = src.substring( end + 1 , end + nextEnd );
		leftName = src.substring( end + nextEnd + 2 );
		part += leftName;
		if( dest.equals( part ) )
		{
			Log.v( "appshortcut" , "GetIntentMatchString###: fffcckkk" );
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Returns true if the shortcuts already exists in the database. we identify
	 * a shortcut by its title and intent.
	 */
	public static boolean shortcutExists(
			Context context ,
			String title ,
			Intent intent )
	{
		boolean result = false;
		Cursor cursor = null;
		ArrayList<ShortcutInfo> addShortcutList;
		final ContentResolver cr = context.getContentResolver();
		Log.v( "appshortcut" , "title: " + title + "intent :" + intent.toUri( 0 ) );
		// Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI,
		// new String[] { "title", "intent" }, "title=? and intent=?",
		// new String[] { title, intent.toUri(0) }, null);
		addShortcutList = iLoongLauncher.getInstance().d3dListener.getShortcutlist();
		if( addShortcutList != null )
		{
			for( ShortcutInfo currentinfo : addShortcutList )
			{
				if( currentinfo.title.toString().equals( title ) )
				{
					return true;
				}
			}
		}
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			cursor = cr.query( LauncherSettings.Favorites.CONTENT_URI_BIG_ICON , new String[]{
					LauncherSettings.Favorites.TITLE ,
					LauncherSettings.Favorites.INTENT ,
					LauncherSettings.Favorites.ANGLE ,
					LauncherSettings.Favorites.CONTAINER } , null , null , null );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			cursor = cr.query( LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON , new String[]{
					LauncherSettings.Favorites.TITLE ,
					LauncherSettings.Favorites.INTENT ,
					LauncherSettings.Favorites.ANGLE ,
					LauncherSettings.Favorites.CONTAINER } , null , null , null );
		}
		else
		{
			cursor = cr.query( LauncherSettings.Favorites.CONTENT_URI , new String[]{
					LauncherSettings.Favorites.TITLE ,
					LauncherSettings.Favorites.INTENT ,
					LauncherSettings.Favorites.ANGLE ,
					LauncherSettings.Favorites.CONTAINER } , null , null , null );
		}
		if( cursor == null )
		{
			return false;
		}
		else
		{
			int titleIndex = cursor.getColumnIndexOrThrow( LauncherSettings.Favorites.TITLE );
			int dataIndex = cursor.getColumnIndexOrThrow( LauncherSettings.Favorites.INTENT );
			int angleIndex = cursor.getColumnIndexOrThrow( LauncherSettings.Favorites.ANGLE );
			int containerIndex = cursor.getColumnIndexOrThrow( LauncherSettings.Favorites.CONTAINER );
			if( !cursor.moveToFirst() )
			{
				cursor.close();
				return false;
			}
			do
			{
				String dbTitle = cursor.getString( titleIndex );// 标题
				String dbIntent = cursor.getString( dataIndex );
				int dbAngle = cursor.getInt( angleIndex );
				long container = cursor.getLong( containerIndex );
				if( !( container == LauncherSettings.Favorites.CONTAINER_HOTSEAT && dbAngle == HotSeat3D.TYPE_ICON ) && ( !"".equals( dbIntent ) ) && ( dbIntent != null ) )
				{
					if( getPackageName( dbIntent ).equals( getPackageName( intent.toUri( 0 ).toString() ) ) )
					{
						if( dbTitle != null && title != null && dbTitle.equals( title ) )
						{
							cursor.close();
							return true;
						}
					}
				}
			}
			while( cursor.moveToNext() );
		}
		cursor.close();
		return false;
		// Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI,
		// new String[] { "intent" }, "intent=?",
		// new String[] {
		// "#Intent;action=android.intent.action.MAIN;category=android.intent.category.LAUNCHER;component=com.sohu.newsclient/.app.SplashActivity;end"
		// }, null);
		// try {
		// result = c.moveToFirst();
		// } finally {
		// c.close();
		// }
	}
	
	/**
	 * Returns true if the shortcuts already exists in the database. we identify
	 * a shortcut by its title and intent.
	 */
	public static boolean isWidgetExists(
			Context context ,
			String packagename ,
			String clsname )
	{
		if( packagename == null || clsname == null )
			return false;
		Cursor cursor = null;
		ArrayList<ShortcutInfo> addShortcutList;
		final ContentResolver cr = context.getContentResolver();
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			cursor = cr.query(
					LauncherSettings.Favorites.CONTENT_URI_BIG_ICON ,
					new String[]{ LauncherSettings.Favorites.ICON_PACKAGE , LauncherSettings.Favorites.ICON_RESOURCE , } ,
					null ,
					null ,
					null );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			cursor = cr.query(
					LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON ,
					new String[]{ LauncherSettings.Favorites.ICON_PACKAGE , LauncherSettings.Favorites.ICON_RESOURCE , } ,
					null ,
					null ,
					null );
		}
		else
		{
			cursor = cr.query( LauncherSettings.Favorites.CONTENT_URI , new String[]{ LauncherSettings.Favorites.ICON_PACKAGE , LauncherSettings.Favorites.ICON_RESOURCE , } , null , null , null );
		}
		if( cursor == null )
		{
			return false;
		}
		else
		{
			int packnameIndex = cursor.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON_PACKAGE );
			int clsNameIndex = cursor.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON_RESOURCE );
			if( !cursor.moveToFirst() )
			{
				cursor.close();
				return false;
			}
			do
			{
				String dbPkgName = cursor.getString( packnameIndex );// 标题
				String dbClsName = cursor.getString( clsNameIndex );
				if( packagename.equals( dbPkgName ) && clsname.equals( dbClsName ) )
				{
					cursor.close();
					return true;
				}
			}
			while( cursor.moveToNext() );
		}
		cursor.close();
		return false;
	}
	
	/**
	 * Find a folder in the db, creating the FolderInfo if necessary, and adding
	 * it to folderList.
	 */
	FolderInfo getFolderById(
			Context context ,
			HashMap<Long , FolderInfo> folderList ,
			long id )
	{
		final ContentResolver cr = context.getContentResolver();
		Cursor c = null;
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			c = cr.query(
					LauncherSettings.Favorites.CONTENT_URI_BIG_ICON ,
					null ,
					"_id=? and (itemType=? or itemType=?)" ,
					new String[]{ String.valueOf( id ) , String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER ) , String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER ) } ,
					null );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			c = cr.query(
					LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON ,
					null ,
					"_id=? and (itemType=? or itemType=?)" ,
					new String[]{ String.valueOf( id ) , String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER ) , String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER ) } ,
					null );
		}
		else
		{
			c = cr.query(
					LauncherSettings.Favorites.CONTENT_URI ,
					null ,
					"_id=? and (itemType=? or itemType=?)" ,
					new String[]{ String.valueOf( id ) , String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER ) , String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER ) } ,
					null );
		}
		try
		{
			if( c.moveToFirst() )
			{
				final int itemTypeIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ITEM_TYPE );
				final int titleIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.TITLE );
				final int containerIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.CONTAINER );
				final int screenIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.SCREEN );
				final int XIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.X );
				final int YIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.Y );
				FolderInfo folderInfo = null;
				switch( c.getInt( itemTypeIndex ) )
				{
					case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
						folderInfo = findOrMakeUserFolder( folderList , id );
						break;
					case LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER:
						folderInfo = findOrMakeLiveFolder( folderList , id );
						break;
				}
				folderInfo.title = c.getString( titleIndex );
				folderInfo.id = id;
				folderInfo.container = c.getInt( containerIndex );
				folderInfo.screen = c.getInt( screenIndex );
				folderInfo.x = c.getInt( XIndex );
				folderInfo.y = c.getInt( YIndex );
				return folderInfo;
			}
		}
		finally
		{
			c.close();
		}
		return null;
	}
	
	/**
	 * Return an existing UserFolderInfo object if we have encountered this ID
	 * previously, or make a new one.
	 */
	private static UserFolderInfo findOrMakeUserFolder(
			HashMap<Long , FolderInfo> folders ,
			long id )
	{
		// xiatian add start //for mainmenu sort by user
		if( id == LauncherSettings.Favorites.CONTAINER_APPLIST )
		{
			return null;
		}
		// xiatian add end
		// See if a placeholder was created for us already
		FolderInfo folderInfo = folders.get( id );
		if( folderInfo == null || !( folderInfo instanceof UserFolderInfo ) )
		{
			// No placeholder -- create a new instance
			folderInfo = new UserFolderInfo();
			folders.put( id , folderInfo );
		}
		return (UserFolderInfo)folderInfo;
	}
	
	public UserFolderInfo getFolderInfoWithContainerID(
			long id )
	{
		return findOrMakeUserFolder( mFolders , id );
	}
	
	/**
	 * Return an existing UserFolderInfo object if we have encountered this ID
	 * previously, or make a new one.
	 */
	private static LiveFolderInfo findOrMakeLiveFolder(
			HashMap<Long , FolderInfo> folders ,
			long id )
	{
		// See if a placeholder was created for us already
		FolderInfo folderInfo = folders.get( id );
		if( folderInfo == null || !( folderInfo instanceof LiveFolderInfo ) )
		{
			// No placeholder -- create a new instance
			folderInfo = new LiveFolderInfo();
			folders.put( id , folderInfo );
		}
		return (LiveFolderInfo)folderInfo;
	}
	
	public static Bitmap getBitmapFromDB(
			Context context ,
			ShortcutInfo item )
	{
		String[] PROJECTION = new String[]{ LauncherSettings.Favorites.ICON , LauncherSettings.Favorites._ID , LauncherSettings.Favorites.ICON_RESOURCE , LauncherSettings.Favorites.ICON_PACKAGE };
		StringBuilder where = new StringBuilder();
		where.append( LauncherSettings.Favorites._ID + " = " + item.id );
		final ContentResolver contentResolver = context.getContentResolver();
		Cursor c = null;
		Bitmap bmp = null;
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			c = contentResolver.query( LauncherSettings.Favorites.CONTENT_URI_BIG_ICON , PROJECTION , where.toString() , null , null );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			c = contentResolver.query( LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON , PROJECTION , where.toString() , null , null );
		}
		else
		{
			c = contentResolver.query( LauncherSettings.Favorites.CONTENT_URI , PROJECTION , where.toString() , null , null );
		}
		if( c != null )
		{
			final int iconIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON );
			final int iconPackageIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON_PACKAGE );
			final int iconResourceIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON_RESOURCE );
			while( c.moveToNext() )
			{
				String packageName = c.getString( iconPackageIndex );
				String resourceName = c.getString( iconResourceIndex );
				PackageManager packageManager = context.getPackageManager();
				// the resource
				try
				{
					Resources resources = packageManager.getResourcesForApplication( packageName );
					if( resources != null )
					{
						final int id = resources.getIdentifier( resourceName , null , null );
						bmp = Utilities.createIconBitmap( resources.getDrawable( id ) , context );
					}
				}
				catch( Exception e )
				{
					// drop this. we have other places to look for icons
				}
				// the db
				if( bmp == null )
				{
					bmp = getIconFromCursor( c , iconIndex );
				}
			}
		}
		return bmp;
	}
	
	/**
	 * Add an item to the database in a specified container. Sets the container,
	 * screen, cellX and cellY fields of the item. Also assigns an ID to the
	 * item.
	 */
	public static void addItemToDatabase(
			Context context ,
			ItemInfo item ,
			long container ,
			int screen ,
			int X ,
			int Y ,
			int cellX ,
			int cellY ,
			boolean notify )
	{
		item.container = container;
		item.screen = screen;
		item.x = X;
		item.y = Y;
		item.cellX = cellX;
		item.cellY = cellY;
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();
		item.onAddToDatabase( values );
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		final Uri url;
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			url = notify ? LauncherSettings.Favorites.CONTENT_URI_BIG_ICON : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION_BIG_ICON;
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			url = notify ? LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION_SMALL_ICON;
		}
		else
		{
			url = notify ? LauncherSettings.Favorites.CONTENT_URI : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION;
		}
		if( ( DefaultLayout.post_database ) && ( ShortcutInfo.isFirstRun == false ) )
		{
			sWorker.post( new Runnable() {
				
				public void run()
				{
					synchronized( db_lock )
					{
						retAddBDResult = cr.insert( url , values );
					}
				}
			} );
		}
		else
		{
			synchronized( db_lock )
			{
				retAddBDResult = cr.insert( url , values );
			}
		}
		if( retAddBDResult != null )
		{
			item.id = Integer.parseInt( retAddBDResult.getPathSegments().get( 1 ) );
		}
	}
	
	public static void addItemToDatabase(
			Context context ,
			ItemInfo item ,
			long container ,
			int screen ,
			int X ,
			int Y ,
			int cellX ,
			int cellY ,
			boolean notify ,
			Intent intent )
	{
		item.container = container;
		item.screen = screen;
		item.x = X;
		item.y = Y;
		item.cellX = cellX;
		item.cellY = cellY;
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();
		item.onAddToDatabase( values );
		values.put( "intent" , intent != null ? intent.toUri( 0 ) : null );
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		final Uri url;
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			url = notify ? LauncherSettings.Favorites.CONTENT_URI_BIG_ICON : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION_BIG_ICON;
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			url = notify ? LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION_SMALL_ICON;
		}
		else
		{
			url = notify ? LauncherSettings.Favorites.CONTENT_URI : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION;
		}
		if( ( DefaultLayout.post_database ) && ( ShortcutInfo.isFirstRun == false ) )
		{
			sWorker.post( new Runnable() {
				
				public void run()
				{
					synchronized( db_lock )
					{
						retAddBDResult = cr.insert( url , values );
					}
				}
			} );
		}
		else
		{
			synchronized( db_lock )
			{
				retAddBDResult = cr.insert( url , values );
			}
		}
		if( retAddBDResult != null )
		{
			item.id = Integer.parseInt( retAddBDResult.getPathSegments().get( 1 ) );
		}
	}
	
	public static void addWidget3DInDatabase(
			Context context ,
			Widget3DInfo item ,
			int screen ,
			int x ,
			int y )
	{
		boolean notify = false;
		item.x = x;
		item.y = y;
		item.screen = screen;
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();
		item.onAddToDatabase( values );
		values.put( LauncherSettings.Favorites.ICON_PACKAGE , item.packageName );
		values.put( LauncherSettings.Favorites.APPWIDGET_ID , item.widgetId );
		Uri result = null;
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			result = cr.insert( notify ? LauncherSettings.Favorites.CONTENT_URI_BIG_ICON : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION_BIG_ICON , values );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			result = cr.insert( notify ? LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION_SMALL_ICON , values );
		}
		else
		{
			result = cr.insert( notify ? LauncherSettings.Favorites.CONTENT_URI : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION , values );
		}
		if( result != null )
		{
			item.id = Integer.parseInt( result.getPathSegments().get( 1 ) );
		}
	}
	
	public static void addSysWidgetInDatabase(
			Context context ,
			Widget2DInfo item )
	{
		boolean notify = false;
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();
		item.onAddToDatabase( values );
		Uri result = null;
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			result = cr.insert( notify ? LauncherSettings.Favorites.CONTENT_URI_BIG_ICON : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION_BIG_ICON , values );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			result = cr.insert( notify ? LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION_SMALL_ICON , values );
		}
		else
		{
			result = cr.insert( notify ? LauncherSettings.Favorites.CONTENT_URI : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION , values );
		}
		if( result != null )
		{
			item.id = Integer.parseInt( result.getPathSegments().get( 1 ) );
		}
	}
	
	public static void addSysShortcutInDatabase(
			Context context ,
			ShortcutInfo item )
	{
		boolean notify = false;
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();
		item.onAddToDatabase( values );
		Uri result = null;
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			result = cr.insert( notify ? LauncherSettings.Favorites.CONTENT_URI_BIG_ICON : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION_BIG_ICON , values );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			result = cr.insert( notify ? LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION_SMALL_ICON , values );
		}
		else
		{
			result = cr.insert( notify ? LauncherSettings.Favorites.CONTENT_URI : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION , values );
		}
		if( result != null )
		{
			item.id = Integer.parseInt( result.getPathSegments().get( 1 ) );
		}
	}
	
	/**
	 * Update an item to the database in a specified container.
	 */
	public static void updateItemInDatabase(
			Context context ,
			ItemInfo item )
	{
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();
		item.onAddToDatabase( values );
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			cr.update( LauncherSettings.Favorites.getContentUriBigIcon( item.id , false ) , values , null , null );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			cr.update( LauncherSettings.Favorites.getContentUriSmallIcon( item.id , false ) , values , null , null );
		}
		else
		{
			cr.update( LauncherSettings.Favorites.getContentUri( item.id , false ) , values , null , null );
		}
	}
	
	public static void batchUpdateItemsInDatabase(
			Context context ,
			ArrayList<ItemInfo> infoList )
	{
		final ContentResolver cr = context.getContentResolver();
		ContentValues values = new ContentValues();
		final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		ContentProviderOperation operation = null;
		Uri uri = null;
		final int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		for( ItemInfo info : infoList )
		{
			info.onAddToDatabase( values );
			if( DefaultLayout.show_icon_size_different_layout )
			{
				if( icon_size_type == 0 )
				{
					uri = LauncherSettings.Favorites.getContentUriBigIcon( info.id , false );
				}
				else if( icon_size_type == 2 )
				{
					uri = LauncherSettings.Favorites.getContentUriSmallIcon( info.id , false );
				}
			}
			else
			{
				uri = LauncherSettings.Favorites.getContentUri( info.id , false );
			}
			operation = ContentProviderOperation.newUpdate( uri ).withValues( values ).build();
			operationList.add( operation );
		}
		if( DefaultLayout.post_database )
		{
			sWorker.post( new Runnable() {
				
				public void run()
				{
					synchronized( db_lock )
					{
						try
						{
							ContentProviderResult[] results;
							if( DefaultLayout.show_icon_size_different_layout )
							{
								if( icon_size_type == 0 )
								{
									results = cr.applyBatch( LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION_BIG_ICON.getAuthority() , operationList );
								}
								else if( icon_size_type == 2 )
								{
									results = cr.applyBatch( LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION_SMALL_ICON.getAuthority() , operationList );
								}
							}
							else
							{
								results = cr.applyBatch( LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION.getAuthority() , operationList );
							}
						}
						catch( RemoteException e )
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						catch( OperationApplicationException e )
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} );
		}
		else
		{
			synchronized( db_lock )
			{
				try
				{
					ContentProviderResult[] results;
					if( DefaultLayout.show_icon_size_different_layout )
					{
						if( icon_size_type == 0 )
						{
							results = cr.applyBatch( LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION_BIG_ICON.getAuthority() , operationList );
						}
						else if( icon_size_type == 2 )
						{
							results = cr.applyBatch( LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION_SMALL_ICON.getAuthority() , operationList );
						}
					}
					else
					{
						results = cr.applyBatch( LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION.getAuthority() , operationList );
					}
				}
				catch( RemoteException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch( OperationApplicationException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Removes the specified item from the database
	 * 
	 * @param context
	 * @param item
	 */
	public static void deleteItemFromDatabase(
			Context context ,
			ItemInfo item )
	{
		final ContentResolver cr = context.getContentResolver();
		final Uri uriToDelete;
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			uriToDelete = LauncherSettings.Favorites.getContentUriBigIcon( item.id , false );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			uriToDelete = LauncherSettings.Favorites.getContentUriSmallIcon( item.id , false );
		}
		else
		{
			uriToDelete = LauncherSettings.Favorites.getContentUri( item.id , false );
		}
		if( DefaultLayout.post_database )
		{
			sWorker.post( new Runnable() {
				
				public void run()
				{
					synchronized( db_lock )
					{
						cr.delete( uriToDelete , null , null );
					}
				}
			} );
		}
		else
		{
			synchronized( db_lock )
			{
				cr.delete( uriToDelete , null , null );
			}
		}
	}
	
	/**
	 * Remove the contents of the specified folder from the database
	 */
	public static void deleteUserFolderContentsFromDatabase(
			Context context ,
			UserFolderInfo info )
	{
		final ContentResolver cr = context.getContentResolver();
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			cr.delete( LauncherSettings.Favorites.getContentUriBigIcon( info.id , false ) , null , null );
			cr.delete( LauncherSettings.Favorites.CONTENT_URI_BIG_ICON , LauncherSettings.Favorites.CONTAINER + "=" + info.id , null );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			cr.delete( LauncherSettings.Favorites.getContentUriSmallIcon( info.id , false ) , null , null );
			cr.delete( LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON , LauncherSettings.Favorites.CONTAINER + "=" + info.id , null );
		}
		else
		{
			cr.delete( LauncherSettings.Favorites.getContentUri( info.id , false ) , null , null );
			cr.delete( LauncherSettings.Favorites.CONTENT_URI , LauncherSettings.Favorites.CONTAINER + "=" + info.id , null );
		}
	}
	
	/**
	 * Set this as the current Launcher activity object for the loader.
	 */
	public void initialize(
			Callbacks callbacks )
	{
		synchronized( mLock )
		{
			mCallbacks = new WeakReference<Callbacks>( callbacks );
		}
	}
	
	/**
	 * Call from the handler for ACTION_PACKAGE_ADDED, ACTION_PACKAGE_REMOVED
	 * and ACTION_PACKAGE_CHANGED.
	 */
	public void onReceive(
			Context context ,
			Intent intent )
	{
		if( DEBUG_LOADERS )
			Log.d( TAG , "onReceive intent=" + intent );
		if( iLoongLauncher.getInstance() == null )
			return;
		if( !Desktop3DListener.bCreat1Done )
			return;
		final String action = intent.getAction();
		if( Intent.ACTION_PACKAGE_CHANGED.equals( action ) || Intent.ACTION_PACKAGE_REMOVED.equals( action ) || Intent.ACTION_PACKAGE_ADDED.equals( action ) )
		{
			final String packageName = intent.getData().getSchemeSpecificPart();
			final boolean replacing = intent.getBooleanExtra( Intent.EXTRA_REPLACING , false );
			int op = PackageUpdatedTask.OP_NONE;
			if( packageName == null || packageName.length() == 0 )
			{
				// they sent us a bad intent
				return;
			}
			if( Intent.ACTION_PACKAGE_CHANGED.equals( action ) )
			{
				op = PackageUpdatedTask.OP_UPDATE;
			}
			else if( Intent.ACTION_PACKAGE_REMOVED.equals( action ) )
			{
				if( !replacing )
				{
					op = PackageUpdatedTask.OP_REMOVE;
				}
				// else, we are replacing the package, so a PACKAGE_ADDED will
				// be sent
				// later, we will update the package at this time
			}
			else if( Intent.ACTION_PACKAGE_ADDED.equals( action ) )
			{
				if( !replacing )
				{
					op = PackageUpdatedTask.OP_ADD;
				}
				else
				{
					op = PackageUpdatedTask.OP_UPDATE;
				}
			}
			if( op != PackageUpdatedTask.OP_NONE )
			{
				enqueuePackageUpdated( new PackageUpdatedTask( op , new String[]{ packageName } ) );
			}
			if( DefaultLayout.enable_content_staistic && Intent.ACTION_PACKAGE_REMOVED.equals( action ) )
			{
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
				boolean register = prefs.getBoolean( "registerTheme-" + packageName , false );
				if( register )
					prefs.edit().putBoolean( "registerTheme-" + packageName , false ).commit();
				register = prefs.getBoolean( "registerScene-" + packageName , false );
				if( register )
					prefs.edit().putBoolean( "registerScene-" + packageName , false ).commit();
				SharedPreferences prefs1 = context.getSharedPreferences( "widget" , Activity.MODE_WORLD_WRITEABLE );
				String key = packageName + "_" + "firstCreate";
				register = prefs1.getBoolean( key , true );
				if( !register )
					prefs1.edit().putBoolean( key , true ).commit();
			}
		}
		else if( Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals( action ) )
		{
			// First, schedule to add these apps back in.
			String[] packages = intent.getStringArrayExtra( Intent.EXTRA_CHANGED_PACKAGE_LIST );
			Log.e( "launcher" , "AVAILABLE:" + packages[0] );
			enqueuePackageUpdated( new PackageUpdatedTask( PackageUpdatedTask.OP_ADD , packages ) );
			// Then, rebind everything.
			boolean runLoader = true;
			if( mCallbacks != null )
			{
				Callbacks callbacks = mCallbacks.get();
				if( callbacks != null )
				{
					// If they're paused, we can skip loading, because they'll
					// do it again anyway
					if( callbacks.setLoadOnResume() )
					{
						runLoader = false;
					}
				}
			}
			if( runLoader )
			{
				startLoader( mApp , false );
			}
		}
		else if( Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals( action ) )
		{
			String[] packages = intent.getStringArrayExtra( Intent.EXTRA_CHANGED_PACKAGE_LIST );
			Log.e( "launcher" , "UNAVAILABLE:" + packages[0] );
			enqueuePackageUpdated( new PackageUpdatedTask( PackageUpdatedTask.OP_UNAVAILABLE , packages ) );
		}
		// xiatian add start //EffectPreview
		else if( ( DefaultLayout.enable_effect_preview ) && ( EffectPreview3D.ACTION_EFFECT_PREVIEW.equals( action ) ) )
		{
			if( ( iLoongLauncher.getInstance() != null ) && ( iLoongLauncher.getInstance().getD3dListener() != null ) && ( iLoongLauncher.getInstance().getD3dListener().getRoot() != null ) )
				iLoongLauncher.getInstance().getD3dListener().getRoot().dealEffectPreview( intent );
		}
		// xiatian add end
	}
	
	public void startLoader(
			Context context ,
			boolean isLaunching )
	{
		synchronized( mLock )
		{
			if( DEBUG_LOADERS )
			{
				Log.d( TAG , "startLoader isLaunching=" + isLaunching );
			}
			// Don't bother to start the thread if we know it's not going to do
			// anything
			if( mCallbacks != null && mCallbacks.get() != null )
			{
				// If there is already one running, tell it to stop.
				LoaderTask oldTask = mLoaderTask;
				if( oldTask != null )
				{
					if( oldTask.isLaunching() )
					{
						// don't downgrade isLaunching if we're already running
						isLaunching = true;
					}
					oldTask.stopLocked();
				}
				mLoaderTask = new LoaderTask( context , isLaunching );
				sWorker.post( mLoaderTask );
			}
		}
	}
	
	public void stopLoader()
	{
		synchronized( mLock )
		{
			if( mLoaderTask != null )
			{
				mLoaderTask.stopLocked();
			}
		}
	}
	
	/**
	 * Runnable for the thread that loads the contents of the launcher: -
	 * workspace icons - widgets - all apps icons
	 */
	private class LoaderTask implements Runnable
	{
		
		private Context mContext;
		private Thread mWaitThread;
		private boolean mIsLaunching;
		private boolean mStopped;
		private boolean mLoadAndBindStepFinished;
		
		LoaderTask(
				Context context ,
				boolean isLaunching )
		{
			mContext = context;
			mIsLaunching = isLaunching;
		}
		
		boolean isLaunching()
		{
			return mIsLaunching;
		}
		
		private void loadAndBindWorkspace()
		{
			// Load the workspace
			Utils3D.showTimeFromStart( "loadAndBind workspace" );
			// For now, just always reload the workspace. It's ~100 ms vs. the
			// binding which takes many hundreds of ms.
			// We can reconsider.
			if( DEBUG_LOADERS )
			{
				Log.d( TAG , "loadAndBindWorkspace mWorkspaceLoaded=" + mWorkspaceLoaded );
			}
			if( true || !mWorkspaceLoaded )
			{
				loadWorkspace();
				Utils3D.showTimeFromStart( "finish load workspace" );
				if( mStopped )
				{
					return;
				}
				mWorkspaceLoaded = true;
			}
			// Bind the workspace
			bindWorkspace();
		}
		
		private void waitForSidebar()
		{
			synchronized( HotSeat3D.lock )
			{
				final long workspaceWaitTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
				while( !mStopped && waitSidebar )
				{
					try
					{
						HotSeat3D.lock.wait();
					}
					catch( InterruptedException ex )
					{
						// Ignore
					}
				}
				if( DEBUG_LOADERS )
				{
					Log.i( TAG , "waited " + ( SystemClock.uptimeMillis() - workspaceWaitTime ) + "ms for sidebar to finish binding" );
				}
			}
		}
		
		private void waitForD3dInit()
		{
			synchronized( Desktop3DListener.init_lock )
			{
				final long workspaceWaitTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
				while( !mStopped && waitD3dInit )
				{
					try
					{
						Desktop3DListener.init_lock.wait();
					}
					catch( InterruptedException ex )
					{
						// Ignore
					}
				}
				waitD3dInit = true;
				if( DEBUG_LOADERS )
				{
					Log.i( TAG , "waited " + ( SystemClock.uptimeMillis() - workspaceWaitTime ) + "ms for d3d to finish init" );
				}
			}
		}
		
		private void waitBefore3DWidget()
		{
			synchronized( lock_3dwidget )
			{
				final long workspaceWaitTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
				while( !mStopped && waitBefore3DWidget )
				{
					try
					{
						lock_3dwidget.wait();
					}
					catch( InterruptedException ex )
					{
						// Ignore
					}
				}
				waitBefore3DWidget = true;
				if( DEBUG_LOADERS )
				{
					Log.i( TAG , "waited " + ( SystemClock.uptimeMillis() - workspaceWaitTime ) + "ms before load 3dwidget" );
				}
			}
		}
		
		private void waitBefore2DWidget()
		{
			synchronized( lock_2dwidget )
			{
				final long workspaceWaitTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
				while( !mStopped && waitBefore2DWidget )
				{
					try
					{
						lock_2dwidget.wait();
					}
					catch( InterruptedException ex )
					{
						// Ignore
					}
				}
				waitBefore2DWidget = true;
				if( DEBUG_LOADERS )
				{
					Log.i( TAG , "waited " + ( SystemClock.uptimeMillis() - workspaceWaitTime ) + "ms before load 2dwidget" );
				}
			}
		}
		
		private void waitForBindApp()
		{
			synchronized( lock_allapp )
			{
				final long workspaceWaitTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
				while( !mStopped && waitBindApp )
				{
					try
					{
						lock_allapp.wait();
					}
					catch( InterruptedException ex )
					{
						// Ignore
					}
				}
				waitBindApp = true;
				if( DEBUG_LOADERS )
				{
					Log.i( TAG , "waited " + ( SystemClock.uptimeMillis() - workspaceWaitTime ) + "ms for bind apps" );
				}
			}
		}
		
		private void waitForIdle()
		{
			// Wait until the either we're stopped or the other threads are
			// done.
			// This way we don't start loading all apps until the workspace has
			// settled
			// down.
			synchronized( LoaderTask.this )
			{
				final long workspaceWaitTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
				// mHandler.postIdle(new Runnable() {
				mHandler.post( new Runnable() {
					
					public void run()
					{
						synchronized( LoaderTask.this )
						{
							mLoadAndBindStepFinished = true;
							if( DEBUG_LOADERS )
							{
								Log.d( TAG , "done with previous binding step" );
							}
							LoaderTask.this.notify();
						}
					}
				} );
				while( !mStopped && !mLoadAndBindStepFinished )
				{
					try
					{
						this.wait();
					}
					catch( InterruptedException ex )
					{
						// Ignore
					}
				}
				if( DEBUG_LOADERS )
				{
					Log.d( TAG , "waited " + ( SystemClock.uptimeMillis() - workspaceWaitTime ) + "ms for previous step to finish binding" );
				}
			}
		}
		
		public void run()
		{
			// Optimize for end-user experience: if the Launcher is up and running with the
			// All Apps interface in the foreground, load All Apps first.
			// Otherwise, load the workspace first (default).
			loadTaskId = android.os.Process.myTid();
			final Callbacks cbk = mCallbacks.get();
			final boolean loadWorkspaceFirst = true;
			if( DEBUG_LOADERS )
			{
				Log.d( "LoaderTask" , "run loadWorkspaceFirst=" + loadWorkspaceFirst );
			}
			keep_running:
			{
				// Elevate priority when Home launches for the first time to avoid
				// starving at boot time. Staring at a blank home is not cool.
				// synchronized (mLock) {
				// android.os.Process
				// .setThreadPriority(mIsLaunching ?
				// Process.THREAD_PRIORITY_DEFAULT
				// : Process.THREAD_PRIORITY_BACKGROUND);
				// }
				if( DEBUG_LOADERS )
				{
					Log.d( "LoaderTask" , "run keep_running=" + loadWorkspaceFirst );
				}
				DefaultLayout.getInstance().setDefaultIcon();
				if( DefaultLayout.dynamic_icon )
				{
					DefaultLayout.getInstance().getDynamicInfo();
					DefaultLayout.getInstance().setDynamicIcon();
				}
				DefaultLayout.getInstance().setDefaultShortcutIcon();
				if( ShortcutInfo.isFirstRun )
				{
					if( !mAllAppsLoaded )
					{
						queryAllApps();
						DefaultLayout.getInstance().setApplist( apps );
					}
					DefaultLayout.getInstance().InsertAllItem();
				}
				if( loadWorkspaceFirst )
				{
					if( DEBUG_LOADERS )
						Log.d( TAG , "step 1: loading workspace" );
					loadAndBindWorkspace();
				}
				else
				{
					if( !mAllAppsLoaded )
					{
						queryAllApps();
						DefaultLayout.getInstance().setApplist( apps );
					}
					if( DEBUG_LOADERS )
						Log.d( TAG , "step 1: special: loading all apps" );
					DefaultLayout.getInstance().GetFactoryAppInfo();
					loadAndBindAllApps();
				}
				if( mStopped )
				{
					break keep_running;
				}
				if( DefaultLayout.loadapp_in_background && !DefaultLayout.cancel_dialog_last )
				{
					android.os.Process.setThreadPriority( Process.THREAD_PRIORITY_BACKGROUND );
					waitForIdle();
				}
				if( !ShortcutInfo.isFirstRun && loadWorkspaceFirst )
				{
					if( !mAllAppsLoaded )
					{
						queryAllApps();
						DefaultLayout.getInstance().setApplist( apps );
					}
				}
				ShortcutInfo.isFirstRun = false;
				// We don't load all apps when the mobile starts. 
				//				// second step
				//				if( loadWorkspaceFirst )
				//				{
				//					if( DEBUG_LOADERS )
				//						Log.d( TAG , "step 2: loading all apps" );
				//					DefaultLayout.getInstance().GetFactoryAppInfo();
				//					loadAndBindAllApps();
				//				}
				//				else
				//				{
				//					if( DEBUG_LOADERS )
				//						Log.d( TAG , "step 2: special: loading workspace" );
				//					loadAndBindWorkspace();  
				//				}
				
				loadAppListPre();
			}
			if( iLoongApplication.BuiltIn && !DefaultLayout.hide_mainmenu_widget )
			{
				loadAndBindAllWidgets();
			}
			if( !iLoongApplication.BuiltIn )
			{
				AppList3D.allInit = true;
			}
			//added by hugo.ye begin
			if( DefaultLayout.needToSaveSpecifiedIconXml )
			{
				DefaultLayout.getInstance().saveSpecifiedIconToXml();
				DefaultLayout.needToSaveSpecifiedIconXml = false;
			}
			// added by hugo.ye end
			// Clear out this reference, otherwise we end up holding it until
			// all of the
			// callback runnables are done.
			mContext = null;
			synchronized( mLock )
			{
				// If we are still the last one to be scheduled, remove
				// ourselves.
				if( mLoaderTask == this )
				{
					mLoaderTask = null;
				}
			}
			// Trigger a gc to try to clean up after the stuff is done, since
			// the
			// renderscript allocations aren't charged to the java heap.
			if( mStopped )
			{
				mHandler.post( new Runnable() {
					
					public void run()
					{
						System.gc();
					}
				} );
			}
			else
			{
				mHandler.postIdle( new Runnable() {
					
					public void run()
					{
						System.gc();
					}
				} );
			}
		}
		
		public void stopLocked()
		{
			synchronized( LoaderTask.this )
			{
				mStopped = true;
				this.notify();
			}
		}
		
		/**
		 * Gets the callbacks object. If we've been stopped, or if the launcher
		 * object has somehow been garbage collected, return null instead. Pass
		 * in the Callbacks object that was around when the deferred message was
		 * scheduled, and if there's a new Callbacks object around then also
		 * return null. This will save us from calling onto it with data that
		 * will be ignored.
		 */
		Callbacks tryGetCallbacks(
				Callbacks oldCallbacks )
		{
			synchronized( mLock )
			{
				if( mStopped )
				{
					return null;
				}
				if( mCallbacks == null )
				{
					return null;
				}
				final Callbacks callbacks = mCallbacks.get();
				if( callbacks != oldCallbacks )
				{
					return null;
				}
				if( callbacks == null )
				{
					Log.w( TAG , "no mCallbacks" );
					return null;
				}
				return callbacks;
			}
		}
		
		// check & update map of what's occupied; used to discard
		// overlapping/invalid items
		private boolean checkItemPlacement(
				ItemInfo occupied[][][] ,
				ItemInfo item )
		{
			if( item.container != LauncherSettings.Favorites.CONTAINER_DESKTOP )
			{
				return true;
			}
			for( int x = item.cellX ; x < ( item.cellX + item.spanX ) ; x++ )
			{
				for( int y = item.cellY ; y < ( item.cellY + item.spanY ) ; y++ )
				{
					if( occupied[item.screen][x][y] != null )
					{
						Log.e( TAG , "Error loading shortcut " + item + " into cell (" + item.screen + ":" + x + "," + y + ") occupied by " + occupied[item.screen][x][y] );
						return false;
					}
				}
			}
			for( int x = item.cellX ; x < ( item.cellX + item.spanX ) ; x++ )
			{
				for( int y = item.cellY ; y < ( item.cellY + item.spanY ) ; y++ )
				{
					occupied[item.screen][x][y] = item;
				}
			}
			return true;
		}
		
		private void queryAllApps()
		{
			final Intent mainIntent = new Intent( Intent.ACTION_MAIN , null );
			mainIntent.addCategory( Intent.CATEGORY_LAUNCHER );
			final PackageManager packageManager = mContext.getPackageManager();
			Intent widget = new Intent( "com.iLoong.widget" , null );
			List<ResolveInfo> mWidgetResolveInfoList = packageManager.queryIntentActivities( widget , PackageManager.GET_ACTIVITIES );
			final long qiaTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
			apps = packageManager.queryIntentActivities( mainIntent , 0 );
			Iterator<ResolveInfo> ite = apps.iterator();
			while( ite.hasNext() )
			{
				ResolveInfo info = ite.next();
				if( info.activityInfo.applicationInfo.packageName == null || info.activityInfo.name == null )
					continue;
				for( int j = 0 ; j < DefaultLayout.hideAppList.size() ; j++ )
				{
					String compName = DefaultLayout.hideAppList.get( j );
					ComponentName componentName = new ComponentName( info.activityInfo.applicationInfo.packageName , info.activityInfo.name );
					if( componentName.toString().contains( compName ) )
						ite.remove();
				}
				// xiatian add start
				// in !CoCoLauncher show theme icon in Launcher.if CoCoLauncher
				// uninstalled goto download CoCoCoCoLauncher,else goto themes
				// activity.
				if( HideThemesInAppList( info.activityInfo.applicationInfo.packageName ) )
				{
					ite.remove();
					continue;
				}
				// xiatian add end
				// teapotXu_20130305: add start
				// Widget icon will not be shown in Launcher
				if( IsInstalledCooeeWidgets( mWidgetResolveInfoList , info.activityInfo.applicationInfo.packageName ) )
				{
					ite.remove();
					continue;
				}
				// teapotXu_20130305: add end
			}
			// 在主菜单显示某些在mainfest中配置的Activity显示为Icon
			for( int j = 0 ; j < DefaultLayout.showAppList.size() ; j++ )
			{
				String appStr = DefaultLayout.showAppList.get( j );
				String packageName = null;
				String className = null;
				Intent intent = new Intent();
				if( appStr.contains( ";" ) )
				{
					packageName = appStr.split( ";" )[0];
					className = appStr.split( ";" )[1];
					ComponentName component = null;
					if( DefaultLayout.personal_center_internal )
					{
						component = new ComponentName( iLoongLauncher.getInstance() , className );
					}
					else
					{
						component = new ComponentName( packageName , className );
					}
					intent.setComponent( component );
				}
				else
				{
					intent.setClassName( iLoongLauncher.getInstance() , DefaultLayout.showAppList.get( j ) );
				}
				List<ResolveInfo> showInAppList = packageManager.queryIntentActivities( intent , 0 );
				if( showInAppList != null && showInAppList.size() > 0 )
				{
					apps.addAll( showInAppList );
				}
			}
			if( DEBUG_LOADERS )
			{
				Log.d( TAG , "queryIntentActivities took " + ( SystemClock.uptimeMillis() - qiaTime ) + "ms" );
			}
		}
		
		private void loadWorkspace()
		{
			final long t = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
			final Context context = mContext;
			final ContentResolver contentResolver = context.getContentResolver();
			final PackageManager manager = context.getPackageManager();
			final AppWidgetManager widgets = AppWidgetManager.getInstance( context );
			final boolean isSafeMode = manager.isSafeMode();
			mItems.clear();
			mCustomShortcutItems.clear();
			mAppWidgets.clear();
			mFolders.clear();
			mSidebarItems.clear();
			mWidgetView.clear();
			mWidget3Ds.clear();
			mCooeePlugins.clear();
			final ArrayList<Long> itemsToRemove = new ArrayList<Long>();
			final ArrayList<Widget2DInfo> itemsToAdd = new ArrayList<Widget2DInfo>();
			Cursor c = null;
			int icon_size_type = DefaultLayout.getDefaultIconSizeType();
			if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
			{
				c = contentResolver.query( LauncherSettings.Favorites.CONTENT_URI_BIG_ICON , null , null , null , null );
			}
			else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
			{
				c = contentResolver.query( LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON , null , null , null , null );
			}
			else
			{
				c = contentResolver.query( LauncherSettings.Favorites.CONTENT_URI , null , null , null , null );
			}
			/*  
			 * didn't need to declare occupied for free layout
			 * 
			 * final ItemInfo occupied[][][] = new
			 * ItemInfo[iLoongLauncher.SCREEN_COUNT
			 * ][iLoongLauncher.NUMBER_CELLS_X][iLoongLauncher.NUMBER_CELLS_Y];
			 */
			if( c != null )
			{
				try
				{
					final int idIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites._ID );
					final int intentIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.INTENT );
					final int titleIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.TITLE );
					final int iconTypeIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON_TYPE );
					final int iconIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON );
					final int iconPackageIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON_PACKAGE );
					final int iconResourceIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON_RESOURCE );
					final int containerIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.CONTAINER );
					final int itemTypeIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ITEM_TYPE );
					final int appWidgetIdIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.APPWIDGET_ID );
					final int screenIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.SCREEN );
					final int cellXIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.CELLX );
					final int cellYIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.CELLY );
					final int spanXIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.SPANX );
					final int spanYIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.SPANY );
					// added for workspace free layout items
					final int XIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.X );
					final int YIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.Y );
					// added for workspace free layout items
					final int uriIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.URI );
					final int displayModeIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.DISPLAY_MODE );
					final int angleIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ANGLE );
					ShortcutInfo info;
					String intentDescription;
					Widget2DInfo appWidgetInfo;
					Widget3DInfo widget3DInfo;
					int container;
					long id;
					Intent intent;
					while( !mStopped && c.moveToNext() )
					{
						try
						{
							int itemType = c.getInt( itemTypeIndex );
							switch( itemType )
							{
								case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
								case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
									intentDescription = c.getString( intentIndex );
									try
									{
										intent = Intent.parseUri( intentDescription , 0 );
									}
									catch( URISyntaxException e )
									{
										continue;
									}
									if( itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
									{
										info = getShortcutInfo( manager , intent , context , c , iconIndex , titleIndex );
									}
									else
									{
										info = getShortcutInfo( c , context , iconTypeIndex , iconPackageIndex , iconResourceIndex , iconIndex , titleIndex , intent );
									}
									if( info != null )
									{
										updateSavedIcon( context , info , c , iconIndex );
										info.intent = intent;
										info.id = c.getLong( idIndex );
										container = c.getInt( containerIndex );
										info.container = container;
										info.screen = c.getInt( screenIndex );
										info.cellX = c.getInt( cellXIndex );
										info.cellY = c.getInt( cellYIndex );
										// added for workspace free layout items
										info.x = c.getInt( XIndex );
										info.y = c.getInt( YIndex );
										info.angle = c.getInt( angleIndex );
										// added for workspace free layout items
										/*
										 * delete for free layout
										 * 
										 * // check & update map of what's occupied
										 * if (!checkItemPlacement(occupied, info))
										 * { break; }
										 */
										switch( container )
										{
											case LauncherSettings.Favorites.CONTAINER_DESKTOP:
												mItems.add( info );
												break;
											case LauncherSettings.Favorites.CONTAINER_HOTSEAT:
												// sunyinwei added for do not add
												// shortcut icon when package is not
												// exist
												if( info.intent == null || info.intent.getComponent() == null || info.intent.getComponent().getPackageName() == null )
												{
													mSidebarItems.add( info );
												}
												else if( DefaultLayout.checkApkExist( iLoongLauncher.getInstance() , info.intent.getComponent().getPackageName() ) )
												{
													mSidebarItems.add( info );
												}
												break;
											case LauncherSettings.Favorites.CONTAINER_SIDEBAR: // xp_20120425
												break;
											default:
												// Item is in a user folder
												UserFolderInfo folderInfo = findOrMakeUserFolder( mFolders , container );
												// sunyinwei added for do not add
												// shortcut icon when package is not
												// exist
												if( info.intent == null || info.intent.getComponent() == null || info.intent.getComponent().getPackageName() == null )
												{
													if( folderInfo.contents.size() >= R3D.folder_max_num )
													{
														//when this folder's member is full, so delete the current item
														deleteItemFromDatabase( context , info );
													}
													else
														folderInfo.add( info );
												}
												else if( DefaultLayout.checkApkExist( iLoongLauncher.getInstance() , info.intent.getComponent().getPackageName() ) )
												{
													if( folderInfo.contents.size() >= R3D.folder_max_num )
													{
														//when this folder's member is full, so delete the current item
														deleteItemFromDatabase( context , info );
													}
													else
														folderInfo.add( info );
												}
												//												else
												//												{
												//													// when this app doen't installed
												//													// now, delete it from the database.
												//													deleteItemFromDatabase( context , info );
												//												}
												break;
										}
									}
									else
									{
										// Failed to load the shortcut, probably
										// because the
										// activity manager couldn't resolve it
										// (maybe the app
										// was uninstalled), or the db row was
										// somehow screwed up.
										// Delete it.
										id = c.getLong( idIndex );
										Log.e( TAG , "Error loading shortcut " + id + ", removing it" );
										if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
										{
											contentResolver.delete( LauncherSettings.Favorites.getContentUriBigIcon( id , false ) , null , null );
										}
										else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
										{
											contentResolver.delete( LauncherSettings.Favorites.getContentUriSmallIcon( id , false ) , null , null );
										}
										else
										{
											contentResolver.delete( LauncherSettings.Favorites.getContentUri( id , false ) , null , null );
										}
									}
									break;
								case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
									id = c.getLong( idIndex );
									UserFolderInfo folderInfo = findOrMakeUserFolder( mFolders , id );
									folderInfo.title = c.getString( titleIndex );
									int curLanguage = iLoongLauncher.curLanguage;/*
																					* 0:
																					* zh_CN
																					* ,
																					* 1
																					* :
																					* zh_TW
																					* 2
																					* :
																					* en
																					*/
									String title = folderInfo.title.toString();
									if( title.endsWith( "x.z" ) )
									{
										int length = title.length();
										if( length > 3 )
										{
											title = title.substring( 0 , length - 3 );
										}
									}
									if( curLanguage == 0 )
									{
										if( title.equals( "Folder" ) || title.equals( "資料夾" ) )
										{
											folderInfo.title = "文件夹";
										}
									}
									else if( curLanguage == 1 )
									{
										if( title.equals( "Folder" ) || title.equals( "文件夹" ) )
										{
											folderInfo.title = "資料夾";
										}
									}
									else
									{
										if( title.equals( "文件夹" ) || title.equals( "資料夾" ) )
										{
											folderInfo.title = "Folder";
										}
									}
									folderInfo.id = id;
									container = c.getInt( containerIndex );
									folderInfo.container = container;
									folderInfo.screen = c.getInt( screenIndex );
									folderInfo.cellX = c.getInt( cellXIndex );
									folderInfo.cellY = c.getInt( cellYIndex );
									folderInfo.x = c.getInt( XIndex );
									folderInfo.y = c.getInt( YIndex );
									folderInfo.iconResource = c.getString( iconResourceIndex );
									intentDescription = c.getString( intentIndex );
									if( intentDescription != null )
									{
										try
										{
											intent = Intent.parseUri( intentDescription , 0 );
										}
										catch( URISyntaxException e )
										{
											break;
										}
										if( intent != null )
										{
											folderInfo.folderid = intent.getStringExtra( "folderid" );
											folderInfo.iconName = intent.getStringExtra( "iconName" );
										}
									}
									/*
									 * delete for free layout
									 * 
									 * // check & update map of what's occupied if
									 * (!checkItemPlacement(occupied, folderInfo)) {
									 * break; }
									 */
									switch( container )
									{
										case LauncherSettings.Favorites.CONTAINER_DESKTOP:
											mItems.add( folderInfo );
											break;
										case LauncherSettings.Favorites.CONTAINER_HOTSEAT:
											mSidebarItems.add( folderInfo );
											break;
									}
									mFolders.put( folderInfo.id , folderInfo );
									break;
								case LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER:
									id = c.getLong( idIndex );
									Uri uri = Uri.parse( c.getString( uriIndex ) );
									// Make sure the live folder exists
									final ProviderInfo providerInfo = context.getPackageManager().resolveContentProvider( uri.getAuthority() , 0 );
									if( providerInfo == null && !isSafeMode )
									{
										itemsToRemove.add( id );
									}
									else
									{
										LiveFolderInfo liveFolderInfo = findOrMakeLiveFolder( mFolders , id );
										intentDescription = c.getString( intentIndex );
										intent = null;
										if( intentDescription != null )
										{
											try
											{
												intent = Intent.parseUri( intentDescription , 0 );
											}
											catch( URISyntaxException e )
											{
												// Ignore, a live folder might not
												// have a base intent
											}
										}
										liveFolderInfo.title = c.getString( titleIndex );
										liveFolderInfo.id = id;
										liveFolderInfo.uri = uri;
										container = c.getInt( containerIndex );
										liveFolderInfo.container = container;
										liveFolderInfo.screen = c.getInt( screenIndex );
										liveFolderInfo.cellX = c.getInt( cellXIndex );
										liveFolderInfo.cellY = c.getInt( cellYIndex );
										liveFolderInfo.x = c.getInt( XIndex );
										liveFolderInfo.y = c.getInt( YIndex );
										liveFolderInfo.baseIntent = intent;
										liveFolderInfo.displayMode = c.getInt( displayModeIndex );
										/*
										 * delete for free layout
										 * 
										 * // check & update map of what's occupied
										 * if (!checkItemPlacement(occupied,
										 * liveFolderInfo)) { break; }
										 */
										// loadLiveFolderIcon(context, c,
										// iconTypeIndex, iconPackageIndex,
										// iconResourceIndex, liveFolderInfo);
										switch( container )
										{
											case LauncherSettings.Favorites.CONTAINER_DESKTOP:
												mItems.add( liveFolderInfo );
												break;
										}
										mFolders.put( liveFolderInfo.id , liveFolderInfo );
									}
									break;
								case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
									// Read all Launcher-specific widget details
									int appWidgetId = c.getInt( appWidgetIdIndex );
									id = c.getLong( idIndex );
									Log.e( TAG , "appWidgetIdIndex= " + appWidgetIdIndex + "appWidgetId= " + appWidgetId );
									final AppWidgetProviderInfo provider = widgets.getAppWidgetInfo( appWidgetId );
									if( !isSafeMode && ( provider == null || provider.provider == null || provider.provider.getPackageName() == null ) )
									{
										Log.e(
												TAG ,
												"Deleting widget that isn't installed anymore: id=" + id + " appWidgetId=" + appWidgetId + "iLoongApplication.BuiltIn=" + iLoongApplication.BuiltIn );
										itemsToRemove.add( id );
										if( iLoongApplication.BuiltIn && appWidgetId != -1 )
										{
											container = c.getInt( containerIndex );
											if( container != LauncherSettings.Favorites.CONTAINER_DESKTOP )
											{
												Log.e( TAG , "Widget found where container " + "!= CONTAINER_DESKTOP -- ignoring!" );
												continue;
											}
											if( c.getString( iconResourceIndex ) == null || c.getString( iconPackageIndex ) == null )
											{
												Log.e( TAG , "Widget class name null" );
												continue;
											}
											iLoongLauncher.getInstance().mAppWidgetHost.deleteAppWidgetId( appWidgetId );
											int myWidgetId = iLoongLauncher.getInstance().mAppWidgetHost.allocateAppWidgetId();
											appWidgetInfo = new Widget2DInfo( myWidgetId );
											appWidgetInfo.id = myWidgetId;
											appWidgetInfo.screen = c.getInt( screenIndex );
											appWidgetInfo.cellX = c.getInt( cellXIndex );
											appWidgetInfo.cellY = c.getInt( cellYIndex );
											appWidgetInfo.spanX = c.getInt( spanXIndex );
											appWidgetInfo.spanY = c.getInt( spanYIndex );
											if( appWidgetInfo.spanX > R3D.Workspace_cellCountX )
												appWidgetInfo.spanX = R3D.Workspace_cellCountX;
											if( appWidgetInfo.spanY > R3D.Workspace_cellCountY )
												appWidgetInfo.spanY = R3D.Workspace_cellCountY;
											appWidgetInfo.x = c.getInt( XIndex );
											appWidgetInfo.y = c.getInt( YIndex );
											appWidgetInfo.setInfo( c.getString( iconPackageIndex ) , c.getString( iconResourceIndex ) );
											ComponentName cn = new ComponentName( appWidgetInfo.getPackageName() , appWidgetInfo.getClassName() );
											//
											//											//if(DefaultLayout.net_version){
											//											//=============================Jone modify for matching 4.1 or upper.================================
											//											//label: JONE_MOD_WIDGET	
											//											widgets.bindAppWidgetIdIfAllowed(myWidgetId, cn);
											//											//===============================original source ==================================
											//											//widgets.bindAppWidgetId( myWidgetId , cn );
											//											//==================================================================================
											//xujin 适用于api 16+
											UtilsBase.bindAppWidgetId( widgets , myWidgetId , cn );
											//	widgets.bindAppWidgetId( myWidgetId , cn );
											appWidgetInfo.container = container;
											itemsToAdd.add( appWidgetInfo );
											Log.e( TAG , "New Item widget  appWidgetId=" + myWidgetId );
											/*
											 * delete for free layout
											 * 
											 * // check & update map of what's
											 * occupied if
											 * (!checkItemPlacement(occupied,
											 * appWidgetInfo)) { break; }
											 */
											mAppWidgets.add( appWidgetInfo );
										}
									}
									else
									{
										appWidgetInfo = new Widget2DInfo( appWidgetId );
										Log.e( TAG , "provide not null appWidgetIdIndex= " + appWidgetIdIndex + "appWidgetId= " + appWidgetId );
										appWidgetInfo.id = id;
										appWidgetInfo.screen = c.getInt( screenIndex );
										appWidgetInfo.cellX = c.getInt( cellXIndex );
										appWidgetInfo.cellY = c.getInt( cellYIndex );
										appWidgetInfo.spanX = c.getInt( spanXIndex );
										appWidgetInfo.spanY = c.getInt( spanYIndex );
										if( appWidgetInfo.spanX > R3D.Workspace_cellCountX )
											appWidgetInfo.spanX = R3D.Workspace_cellCountX;
										if( appWidgetInfo.spanY > R3D.Workspace_cellCountY )
											appWidgetInfo.spanY = R3D.Workspace_cellCountY;
										appWidgetInfo.x = c.getInt( XIndex );
										appWidgetInfo.y = c.getInt( YIndex );
										appWidgetInfo.setInfo( c.getString( iconPackageIndex ) , c.getString( iconResourceIndex ) );
										container = c.getInt( containerIndex );
										if( container != LauncherSettings.Favorites.CONTAINER_DESKTOP )
										{
											Log.e( TAG , "Widget found where container " + "!= CONTAINER_DESKTOP -- ignoring!" );
											continue;
										}
										appWidgetInfo.container = c.getInt( containerIndex );
										/*
										 * delete for free layout
										 * 
										 * // check & update map of what's occupied
										 * if (!checkItemPlacement(occupied,
										 * appWidgetInfo)) { break; }
										 */
										mAppWidgets.add( appWidgetInfo );
									}
									break;
								case LauncherSettings.Favorites.ITEM_TYPE_WIDGET3D:
									// 自定义widget
									container = c.getInt( containerIndex );
									if( container != LauncherSettings.Favorites.CONTAINER_DESKTOP )
									{
										Log.e( TAG , "Widget found where container " + "!= CONTAINER_DESKTOP -- ignoring!" );
										continue;
									}
									widget3DInfo = new Widget3DInfo();
									widget3DInfo.id = c.getLong( idIndex );
									widget3DInfo.container = container;
									widget3DInfo.widgetId = c.getInt( appWidgetIdIndex );
									widget3DInfo.packageName = c.getString( iconPackageIndex );
									container = c.getInt( containerIndex );
									widget3DInfo.screen = c.getInt( screenIndex );
									widget3DInfo.cellX = c.getInt( cellXIndex );
									widget3DInfo.cellY = c.getInt( cellYIndex );
									widget3DInfo.spanX = c.getInt( spanXIndex );
									widget3DInfo.spanY = c.getInt( spanYIndex );
									widget3DInfo.x = c.getInt( XIndex );
									widget3DInfo.y = c.getInt( YIndex );
									Log.d( "launcher" , "load widget:" + widget3DInfo.packageName );
									Log.d( "launcher" , "widget3d size=" + mWidget3Ds.size() );
									mWidget3Ds.add( widget3DInfo );
									break;
								case LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW:
								case LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW:
									intentDescription = c.getString( intentIndex );
									try
									{
										intent = Intent.parseUri( intentDescription , 0 );
									}
									catch( URISyntaxException e )
									{
										break;
									}
									if( intent == null )
										break;
									ShortcutInfo shortInfo = new ShortcutInfo();
									shortInfo.intent = intent;
									if( itemType == LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW )
									{
										shortInfo.title = DefaultLayout.GetDefaultWidgetNameWithPkgName( intent.getComponent().getPackageName() );
										shortInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW;
									}
									else
									{
										shortInfo.title = DefaultLayout.GetDefaultVirtureNameWithPkgClassName( intent.getComponent().getPackageName() , intent.getComponent().getClassName() );
										shortInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW;
									}
									container = c.getInt( containerIndex );
									shortInfo.id = c.getLong( idIndex );
									shortInfo.container = container;
									shortInfo.screen = c.getInt( screenIndex );
									shortInfo.cellX = c.getInt( cellXIndex );
									shortInfo.cellY = c.getInt( cellYIndex );
									shortInfo.spanX = c.getInt( spanXIndex );
									shortInfo.spanY = c.getInt( spanYIndex );
									shortInfo.x = c.getInt( XIndex );
									shortInfo.y = c.getInt( YIndex );
									shortInfo.angle = c.getInt( angleIndex );
									if( itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW )
									{
										switch( container )
										{
											case LauncherSettings.Favorites.CONTAINER_DESKTOP:
												mWidgetView.add( shortInfo );
												break;
											case LauncherSettings.Favorites.CONTAINER_HOTSEAT:
												mSidebarItems.add( shortInfo );
												break;
											case LauncherSettings.Favorites.CONTAINER_SIDEBAR: // xp_20120425
												break;
											default:
												// Item is in a user folder
												updateSavedIcon( context , shortInfo , c , iconIndex );
												UserFolderInfo foldInfo = findOrMakeUserFolder( mFolders , container );
												foldInfo.add( shortInfo );
												break;
										}
									}
									else
									{
										mWidgetView.add( shortInfo );
									}
									break;
								case LauncherSettings.Favorites.ITEM_TYPE_COOEE_PLUGIN:
									CooeePluginInfo pluginInfo = new CooeePluginInfo();
									pluginInfo.id = c.getLong( idIndex );
									pluginInfo.screen = c.getInt( screenIndex );
									pluginInfo.setInfo( c.getString( iconPackageIndex ) , c.getString( iconResourceIndex ) );
									pluginInfo.cellX = c.getInt( cellXIndex );
									pluginInfo.cellY = c.getInt( cellYIndex );
									pluginInfo.spanX = c.getInt( spanXIndex );
									pluginInfo.spanY = c.getInt( spanYIndex );
									pluginInfo.angle = c.getInt( angleIndex );
									pluginInfo.fullScreen = pluginInfo.angle == 1;
									mCooeePlugins.add( pluginInfo );
									break;
								case LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_SHORTCUT:
									intentDescription = c.getString( intentIndex );
									try
									{
										intent = Intent.parseUri( intentDescription , 0 );
									}
									catch( URISyntaxException e )
									{
										break;
									}
									if( intent == null )
										break;
									ShortcutInfo customShortInfo = new ShortcutInfo();
									customShortInfo.intent = intent;
									customShortInfo.title = c.getString( titleIndex );
									customShortInfo.itemType = itemType;
									container = c.getInt( containerIndex );
									customShortInfo.id = c.getLong( idIndex );
									customShortInfo.container = container;
									customShortInfo.screen = c.getInt( screenIndex );
									customShortInfo.cellX = c.getInt( cellXIndex );
									customShortInfo.cellY = c.getInt( cellYIndex );
									customShortInfo.spanX = c.getInt( spanXIndex );
									customShortInfo.spanY = c.getInt( spanYIndex );
									customShortInfo.x = c.getInt( XIndex );
									customShortInfo.y = c.getInt( YIndex );
									customShortInfo.angle = c.getInt( angleIndex );
									switch( container )
									{
										case LauncherSettings.Favorites.CONTAINER_DESKTOP:
											mCustomShortcutItems.add( customShortInfo );
											break;
										case LauncherSettings.Favorites.CONTAINER_HOTSEAT:
											mSidebarItems.add( customShortInfo );
											break;
										default:
											// Item is in a user folder
											UserFolderInfo foldInfo = findOrMakeUserFolder( mFolders , container );
											foldInfo.add( customShortInfo );
											break;
									}
									break;
							}
						}
						catch( Exception e )
						{
							Log.w( TAG , "Desktop items loading interrupted:" , e );
						}
					}
				}
				finally
				{
					c.close();
				}
			}
			if( itemsToRemove.size() > 0 )
			{
				ContentProviderClient client = null;
				if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
				{
					client = contentResolver.acquireContentProviderClient( LauncherSettings.Favorites.CONTENT_URI_BIG_ICON );
				}
				else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
				{
					client = contentResolver.acquireContentProviderClient( LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON );
				}
				else
				{
					client = contentResolver.acquireContentProviderClient( LauncherSettings.Favorites.CONTENT_URI );
				}
				// Remove dead items
				// client.up
				for( long id : itemsToRemove )
				{
					if( DEBUG_LOADERS )
					{
						Log.d( TAG , "Removed id = " + id );
					}
					// Don't notify content observers
					try
					{
						if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
						{
							client.delete( LauncherSettings.Favorites.getContentUriBigIcon( id , false ) , null , null );
						}
						else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
						{
							client.delete( LauncherSettings.Favorites.getContentUriSmallIcon( id , false ) , null , null );
						}
						else
						{
							client.delete( LauncherSettings.Favorites.getContentUri( id , false ) , null , null );
						}
					}
					catch( RemoteException e )
					{
						Log.w( TAG , "Could not remove id = " + id );
					}
				}
			}
			if( itemsToAdd.size() > 0 )
			{
				for( Widget2DInfo widget2DInfo : itemsToAdd )
				{
					addSysWidgetInDatabase( context , widget2DInfo );
				}
			}
		}
		
		/**
		 * Read everything out of our database.
		 */
		private void bindWorkspace()
		{
			final long t = SystemClock.uptimeMillis();
			// Don't use these two variables in any of the callback runnables.
			// Otherwise we hold a reference to them.
			final Callbacks oldCallbacks = mCallbacks.get();
			if( oldCallbacks == null )
			{
				// This launcher has exited and nobody bothered to tell us. Just
				// bail.
				Log.w( TAG , "LoaderTask running with no launcher" );
				return;
			}
			int N;
			// if(iLoongLauncher.getInstance().waitBindWorkspace){
			// iLoongLauncher.getInstance().waitBindWorkspace = false;
			// synchronized (iLoongLauncher.lock) {
			// iLoongLauncher.lock.notify();
			// }
			// }
			// Tell the workspace that we're about to start firing items at it
			mHandler.post( new Runnable() {
				
				public void run()
				{
					Callbacks callbacks = tryGetCallbacks( oldCallbacks );
					if( callbacks != null )
					{
						callbacks.startBindingTrue();
					}
				}
			} );
			// 添加item到sidebar上面 xp_20120425
			mHandler.post( new Runnable() {
				
				public void run()
				{
					Callbacks callbacks = tryGetCallbacks( oldCallbacks );
					if( callbacks != null )
					{
						callbacks.bindSidebarItems( mSidebarItems );
					}
				}
			} );
			waitForSidebar();
			Utils3D.showTimeFromStart( "finish bind sidebar" );
			mHandler.post( new Runnable() {
				
				public void run()
				{
					Callbacks callbacks = tryGetCallbacks( oldCallbacks );
					if( callbacks != null )
					{
						callbacks.afterBindSidebarItems();
					}
				}
			} );
			waitForD3dInit();
			Utils3D.showTimeFromStart( "finish init d3d" );
			final int currentScreen = oldCallbacks.getCurrentWorkspaceScreen();
			// Add the items to the workspace.
			final ArrayList<ItemInfo> currentScreenItems = new ArrayList<ItemInfo>();
			Iterator<ItemInfo> ite = mItems.iterator();
			ItemInfo info = null;
			while( ite.hasNext() )
			{
				info = ite.next();
				if( info.screen == currentScreen )
				{
					currentScreenItems.add( info );
					ite.remove();
				}
			}
			N = currentScreenItems.size();
			for( int i = 0 ; i < N ; i += ITEMS_CHUNK )
			{
				final int start = i;
				final int chunkSize = ( i + ITEMS_CHUNK <= N ) ? ITEMS_CHUNK : ( N - i );
				mHandler.post( new Runnable() {
					
					public void run()
					{
						Callbacks callbacks = tryGetCallbacks( oldCallbacks );
						if( callbacks != null )
						{
							callbacks.bindItemsOnThread( currentScreenItems , start , start + chunkSize );
							callbacks.bindItems( currentScreenItems , start , start + chunkSize );
						}
					}
				} );
			}
			final ArrayList<ItemInfo> currentScreenCustomShortcutItems = new ArrayList<ItemInfo>();
			ite = mCustomShortcutItems.iterator();
			while( ite.hasNext() )
			{
				info = ite.next();
				if( info.screen == currentScreen )
				{
					currentScreenCustomShortcutItems.add( info );
					ite.remove();
				}
			}
			N = currentScreenCustomShortcutItems.size();
			for( int i = 0 ; i < N ; i += ITEMS_CHUNK )
			{
				final int start = i;
				final int chunkSize = ( i + ITEMS_CHUNK <= N ) ? ITEMS_CHUNK : ( N - i );
				mHandler.post( new Runnable() {
					
					public void run()
					{
						Callbacks callbacks = tryGetCallbacks( oldCallbacks );
						if( callbacks != null )
						{
							callbacks.bindCustomShortcutItems( currentScreenCustomShortcutItems , start , start + chunkSize );
						}
					}
				} );
			}
			final ArrayList<ShortcutInfo> currentScreenWidgetView = new ArrayList<ShortcutInfo>();
			Iterator<ShortcutInfo> widgetViewIte = mWidgetView.iterator();
			ShortcutInfo shortcutInfo = null;
			while( widgetViewIte.hasNext() )
			{
				shortcutInfo = widgetViewIte.next();
				if( shortcutInfo.screen == currentScreen )
				{
					currentScreenWidgetView.add( shortcutInfo );
					widgetViewIte.remove();
				}
			}
			mHandler.post( new Runnable() {
				
				public void run()
				{
					Callbacks callbacks = tryGetCallbacks( oldCallbacks );
					if( callbacks != null )
					{
						callbacks.bindWidgetView( currentScreenWidgetView );
					}
				}
			} );
			N = mItems.size();
			for( int i = 0 ; i < N ; i += ITEMS_CHUNK )
			{
				final int start = i;
				final int chunkSize = ( i + ITEMS_CHUNK <= N ) ? ITEMS_CHUNK : ( N - i );
				mHandler.post( new Runnable() {
					
					public void run()
					{
						Callbacks callbacks = tryGetCallbacks( oldCallbacks );
						if( callbacks != null )
						{
							callbacks.bindItemsOnThread( mItems , start , start + chunkSize );
							callbacks.bindItems( mItems , start , start + chunkSize );
						}
					}
				} );
			}
			N = mCustomShortcutItems.size();
			for( int i = 0 ; i < N ; i += ITEMS_CHUNK )
			{
				final int start = i;
				final int chunkSize = ( i + ITEMS_CHUNK <= N ) ? ITEMS_CHUNK : ( N - i );
				mHandler.post( new Runnable() {
					
					public void run()
					{
						Callbacks callbacks = tryGetCallbacks( oldCallbacks );
						if( callbacks != null )
						{
							callbacks.bindCustomShortcutItems( mCustomShortcutItems , start , start + chunkSize );
						}
					}
				} );
			}
			mHandler.post( new Runnable() {
				
				public void run()
				{
					Callbacks callbacks = tryGetCallbacks( oldCallbacks );
					if( callbacks != null )
					{
						callbacks.bindWidgetView( mWidgetView );
					}
				}
			} );
			mHandler.post( new Runnable() {
				
				public void run()
				{
					Callbacks callbacks = tryGetCallbacks( oldCallbacks );
					if( callbacks != null )
					{
						callbacks.bindFolders( mFolders );
					}
				}
			} );
			// Wait until the queue goes empty.
			mHandler.post( new Runnable() {
				
				public void run()
				{
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							synchronized( lock_2dwidget )
							{
								waitBefore2DWidget = false;
								lock_2dwidget.notify();
							}
						}
					} );
					if( DEBUG_LOADERS )
					{
						Log.d( TAG , "Going to start binding 2d widgets soon." );
					}
				}
			} );
			// Bind the widgets, one at a time.
			// WARNING: this is calling into the workspace from the background
			// thread,
			// but since getCurrentScreen() just returns the int, we should be
			// okay. This
			// is just a hint for the order, and if it's wrong, we'll be okay.
			// TODO: instead, we should have that push the current screen into
			// here.
			waitBefore2DWidget();
			N = mAppWidgets.size();
			// once for the current screen
			for( int i = 0 ; i < N ; i++ )
			{
				final Widget2DInfo widget = mAppWidgets.get( i );
				if( widget.screen == currentScreen )
				{
					mHandler.post( new Runnable() {
						
						public void run()
						{
							Callbacks callbacks = tryGetCallbacks( oldCallbacks );
							if( callbacks != null )
							{
								callbacks.bindAppWidget( widget );
							}
						}
					} );
				}
			}
			mHandler.post( new Runnable() {
				
				public void run()
				{
					synchronized( lock_3dwidget )
					{
						waitBefore3DWidget = false;
						lock_3dwidget.notify();
					}
					if( DEBUG_LOADERS )
					{
						Log.d( TAG , "Going to start binding 3d widgets soon." );
					}
				}
			} );
			waitBefore3DWidget();
			// 自定义widget添加到当前桌面上
			// once for the current screen
			for( int i = 0 ; i < mWidget3Ds.size() ; i++ )
			{
				final Widget3DInfo widget = mWidget3Ds.get( i );
				if( widget.screen == currentScreen )
				{
					mHandler.post( new Runnable() {
						
						public void run()
						{
							Callbacks callbacks = tryGetCallbacks( oldCallbacks );
							if( callbacks != null )
							{
								callbacks.bindWidget3D( widget );
							}
						}
					} );
				}
			}
			// once for the current screen
			for( int i = 0 ; i < mCooeePlugins.size() ; i++ )
			{
				final CooeePluginInfo plugin = mCooeePlugins.get( i );
				if( plugin.screen == currentScreen )
				{
					mHandler.post( new Runnable() {
						
						public void run()
						{
							Callbacks callbacks = tryGetCallbacks( oldCallbacks );
							if( callbacks != null )
							{
								callbacks.bindCooeePlugin( plugin );
							}
						}
					} );
				}
			}
			// once for the other screens
			N = mAppWidgets.size();
			for( int i = 0 ; i < N ; i++ )
			{
				final Widget2DInfo widget = mAppWidgets.get( i );
				if( widget.screen != currentScreen )
				{
					mHandler.post( new Runnable() {
						
						public void run()
						{
							Callbacks callbacks = tryGetCallbacks( oldCallbacks );
							if( callbacks != null )
							{
								callbacks.bindAppWidget( widget );
							}
						}
					} );
				}
			}
			// 自定义widget添加到非当前桌面
			for( int i = 0 ; i < mWidget3Ds.size() ; i++ )
			{
				final Widget3DInfo widget = mWidget3Ds.get( i );
				if( widget.screen != currentScreen )
				{
					mHandler.post( new Runnable() {
						
						public void run()
						{
							Callbacks callbacks = tryGetCallbacks( oldCallbacks );
							if( callbacks != null )
							{
								callbacks.bindWidget3D( widget );
							}
						}
					} );
				}
			}
			// 自定义widget添加到非当前桌面
			for( int i = 0 ; i < mCooeePlugins.size() ; i++ )
			{
				final CooeePluginInfo plugin = mCooeePlugins.get( i );
				if( plugin.screen != currentScreen )
				{
					mHandler.post( new Runnable() {
						
						public void run()
						{
							Callbacks callbacks = tryGetCallbacks( oldCallbacks );
							if( callbacks != null )
							{
								callbacks.bindCooeePlugin( plugin );
							}
						}
					} );
				}
			}
			// Tell the workspace that we're done.
			// mHandler.post(new Runnable() {
			// public void run() {
			// Callbacks callbacks = tryGetCallbacks(oldCallbacks);
			// if (callbacks != null) {
			// callbacks.finishBindingItems();
			// }
			// }
			// });
			// If we're profiling, this is the last thing in the queue.
			mHandler.post( new Runnable() {
				
				public void run()
				{
					if( DEBUG_LOADERS )
					{
						Log.d( TAG , "bound workspace in " + ( SystemClock.uptimeMillis() - t ) + "ms" );
					}
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							Callbacks callbacks = tryGetCallbacks( oldCallbacks );
							if( callbacks != null )
							{
								callbacks.finishBindWorkspace();
							}
						}
					} );
				}
			} );
		}
		
		public void loadAndBindAllWidgets()
		{
			if( DEBUG_LOADERS )
			{
				Log.d( TAG , "loadAndBindAllWidgets mAllWidgetsLoaded=" + mAllWidgetsLoaded );
			}
			if( !mAllWidgetsLoaded )
			{
				loadAllWidgets();
				if( mStopped )
				{
					return;
				}
				mAllWidgetsLoaded = true;
			}
			else
			{
				onlyBindAllWidgets();
			}
		}
		
		private void onlyBindAllWidgets()
		{
			final Callbacks oldCallbacks = mCallbacks.get();
			if( oldCallbacks == null )
			{
				// This launcher has exited and nobody bothered to tell us. Just
				// bail.
				Log.w( TAG , "LoaderTask running with no launcher (onlyBindAllWidgets)" );
				return;
			}
			final ArrayList<Object> list = (ArrayList<Object>)mAllWidgets.clone();
			mHandler.post( new Runnable() {
				
				public void run()
				{
					final long t = SystemClock.uptimeMillis();
					final Callbacks callbacks = tryGetCallbacks( oldCallbacks );
					if( callbacks != null )
					{
						if( DefaultLayout.enable_release_2Dwidget )
						{
							if( AppList3D.hasbind2Dwidget )
							{
								callbacks.bindAllWidgets( list );
							}
						}
						else
						{
							callbacks.bindAllWidgets( list );
						}
					}
					if( DEBUG_LOADERS )
					{
						Log.d( TAG , "bound all " + list.size() + " widgets from cache in " + ( SystemClock.uptimeMillis() - t ) + "ms" );
					}
				}
			} );
		}
		
		private void loadAllWidgets()
		{
			final long t = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
			// Don't use these two variables in any of the callback runnables.
			// Otherwise we hold a reference to them.
			final Callbacks oldCallbacks = mCallbacks.get();
			if( oldCallbacks == null )
			{
				// This launcher has exited and nobody bothered to tell us. Just
				// bail.
				Log.w( TAG , "LoaderTask running with no launcher (loadAllWidgets)" );
				return;
			}
			// Get the list of widgets and shortcuts
			mAllWidgets.clear();
			final PackageManager packageManager = mContext.getPackageManager();
			List<AppWidgetProviderInfo> widgets = AppWidgetManager.getInstance( mContext ).getInstalledProviders();
			Intent shortcutsIntent = new Intent( Intent.ACTION_CREATE_SHORTCUT );
			List<ResolveInfo> shortcuts = packageManager.queryIntentActivities( shortcutsIntent , 0 );
			for( AppWidgetProviderInfo widget : widgets )
			{
				if( widget.minWidth > 0 && widget.minHeight > 0 )
				{
					mAllWidgets.add( widget );
				}
				else
				{
					Log.e( TAG , "Widget " + widget.provider + " has invalid dimensions (" + widget.minWidth + ", " + widget.minHeight + ")" );
				}
			}
			mAllWidgets.addAll( shortcuts );
			final long sortTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
			Collections.sort( mAllWidgets , new LauncherModel.WidgetAndShortcutNameComparator( packageManager ) );
			if( DEBUG_LOADERS )
			{
				Log.d( TAG , "sort widgets took " + ( SystemClock.uptimeMillis() - sortTime ) + "ms" );
			}
			final ArrayList<Object> list = (ArrayList<Object>)mAllWidgets.clone();
			mHandler.post( new Runnable() {
				
				public void run()
				{
					final long t = SystemClock.uptimeMillis();
					final Callbacks callbacks = tryGetCallbacks( oldCallbacks );
					if( callbacks != null )
					{
						if( DefaultLayout.enable_release_2Dwidget )
						{
							if( AppList3D.hasbind2Dwidget )
							{
								callbacks.bindAllWidgets( list );
							}
						}
						else
						{
							callbacks.bindAllWidgets( list );
						}
					}
					if( DEBUG_LOADERS )
					{
						Log.d( TAG , "bound all " + list.size() + " widgets in " + ( SystemClock.uptimeMillis() - t ) + "ms" );
					}
				}
			} );
		}
		
		private void loadAndBindAllApps()
		{
			if( DEBUG_LOADERS )
			{
				Log.d( TAG , "loadAndBindAllApps mAllAppsLoaded=" + mAllAppsLoaded );
			}
			if( !mAllAppsLoaded )
			{
				if( DefaultLayout.mainmenu_folder_function )
					loadAllAppsByBatch();
				else
					loadAllAppsByBatchOld();
				if( mStopped )
				{
					return;
				}
				mAllAppsLoaded = true;
			}
			else
			{
				onlyBindAllApps();
			}
		}
		
		private void onlyBindAllApps()
		{
			final Callbacks oldCallbacks = mCallbacks.get();
			if( oldCallbacks == null )
			{
				// This launcher has exited and nobody bothered to tell us. Just
				// bail.
				Log.w( TAG , "LoaderTask running with no launcher (onlyBindAllApps)" );
				return;
			}
			// shallow copy
			final ArrayList<ApplicationInfo> list = (ArrayList<ApplicationInfo>)mAllAppsList.data.clone();
			mHandler.post( new Runnable() {
				
				public void run()
				{
					final long t = SystemClock.uptimeMillis();
					final Callbacks callbacks = tryGetCallbacks( oldCallbacks );
					if( callbacks != null )
					{
						callbacks.bindAllApplications( list );
						callbacks.finishBindApplications();
					}
					if( DEBUG_LOADERS )
					{
						Log.d( TAG , "bound all " + list.size() + " apps from cache in " + ( SystemClock.uptimeMillis() - t ) + "ms" );
					}
				}
			} );
		}
		
		private void loadAllAppsDB()
		{
			final long t = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
			final Context context = mContext;
			final ContentResolver contentResolver = context.getContentResolver();
			final PackageManager manager = context.getPackageManager();
			final boolean isSafeMode = manager.isSafeMode();
			appListItems.clear();
			appListFolders.clear();
			Cursor c = AppListDB.getInstance().queryAll();
			if( c != null )
			{
				try
				{
					final int idIndex = c.getColumnIndexOrThrow( "id" );
					final int intentIndex = c.getColumnIndexOrThrow( "intent" );
					final int titleIndex = c.getColumnIndexOrThrow( "title" );
					final int itemTypeIndex = c.getColumnIndexOrThrow( "item_type" );
					final int containerIndex = c.getColumnIndexOrThrow( "container" );
					final int lastUpdateTimeIndex = c.getColumnIndexOrThrow( "last_update_time" );
					final int useFrequencyIndex = c.getColumnIndexOrThrow( "use_frequency" );
					final int mainmenuLoactionIndex = c.getColumnIndexOrThrow( "int1" );// xiatian add //for mainmenu sort by user
					ApplicationInfo info;
					String packageName;
					String className;
					int container;
					long id;
					Intent intent;
					String intentDescription;
					ResolveInfo resolveInfo;
					while( !mStopped && c.moveToNext() )
					{
						try
						{
							int itemType = c.getInt( itemTypeIndex );
							switch( itemType )
							{
								case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
									container = c.getInt( containerIndex );
									if( container
									// xiatian start //for mainmenu sort by user
									// >//xiatian del
									>= // xiatian add
										// xiatian end
									LauncherSettings.Favorites.CONTAINER_APPLIST )
									{
										ShortcutInfo sInfo = null;
										ComponentName componentName = null;
										intentDescription = c.getString( intentIndex );
										try
										{
											intent = Intent.parseUri( intentDescription , 0 );
										}
										catch( URISyntaxException e )
										{
											continue;
										}
										componentName = intent.getComponent();
										if( componentName == null )
										{
											continue;
										}
										resolveInfo = manager.resolveActivity( intent , 0 );
										if( resolveInfo == null )
										{
											continue;
										}
										info = new ApplicationInfo( resolveInfo );
										// from the resource
										if( resolveInfo != null )
										{
											info.title = resolveInfo.activityInfo.loadLabel( manager );
										}
										if( info.title == null )
										{
											info.title = c.getString( titleIndex );
										}
										info.container = container;
										sInfo = info.makeShortcut();
										sInfo.id = c.getLong( idIndex );
										sInfo.appInfo.id = sInfo.id;// xiatian add
																	// //for
																	// mainmenu sort
																	// by user
										UserFolderInfo folderInfo = findOrMakeUserFolder( appListFolders , container );
										if( folderInfo != null )// xiatian add //for
																// mainmenu sort by
																// user
											folderInfo.add( sInfo );
										sInfo.container = container;
										sInfo.location_in_mainmenu = c.getInt( mainmenuLoactionIndex );
										sInfo.appInfo.container = sInfo.container;
										sInfo.appInfo.location_in_mainmenu = sInfo.location_in_mainmenu;
										appListItems.put( componentName.toString() , sInfo );
									}
									else
									{
										// Failed to load the shortcut, probably
										// because the
										// activity manager couldn't resolve it
										// (maybe the app
										// was uninstalled), or the db row was
										// somehow screwed up.
										// Delete it.
										id = c.getLong( idIndex );
										Log.e( TAG , "Error loading shortcut " + id + ", removing it" );
									}
									break;
								case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
									id = c.getLong( idIndex ) + LauncherSettings.Favorites.CONTAINER_APPLIST + 1;
									UserFolderInfo folderInfo = findOrMakeUserFolder( appListFolders , id );
									folderInfo.title = c.getString( titleIndex );
									folderInfo.lastUpdateTime = c.getLong( lastUpdateTimeIndex );
									folderInfo.use_frequency = c.getInt( useFrequencyIndex );
									folderInfo.location_in_mainmenu = c.getInt( mainmenuLoactionIndex );// xiatian
																										// add
																										// //for
																										// mainmenu
																										// sort
																										// by
																										// user
									int curLanguage = iLoongLauncher.curLanguage;
									if( curLanguage == 0 )
									{
										if( folderInfo.title.equals( "Folder" ) || folderInfo.title.equals( "資料夾" ) )
										{
											folderInfo.title = "文件夹";
										}
									}
									else if( curLanguage == 1 )
									{
										if( folderInfo.title.equals( "Folder" ) || folderInfo.title.equals( "文件夹" ) )
										{
											folderInfo.title = "資料夾";
										}
									}
									else
									{
										if( folderInfo.title.equals( "文件夹" ) || folderInfo.title.equals( "資料夾" ) )
										{
											folderInfo.title = "Folder";
										}
									}
									folderInfo.id = id;
									folderInfo.container = c.getInt( containerIndex );
									intentDescription = c.getString( intentIndex );
									if( intentDescription != null )
									{
										try
										{
											intent = Intent.parseUri( intentDescription , 0 );
										}
										catch( URISyntaxException e )
										{
											break;
										}
										if( intent != null )
										{
											folderInfo.folderid = intent.getStringExtra( "folderid" );
											folderInfo.iconName = intent.getStringExtra( "iconName" );
										}
									}
									iLoongLauncher.getInstance().addFolderInfoToSFolders( folderInfo );
									break;
							}
						}
						catch( Exception e )
						{
							Log.w( TAG , "Desktop items loading interrupted:" , e );
						}
					}
				}
				finally
				{
					c.close();
				}
			}
		}
		
		private void loadAllAppsByBatch()
		{
			final long t = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
			// Don't use these two variables in any of the callback runnables.
			// Otherwise we hold a reference to them.
			final Callbacks oldCallbacks = mCallbacks.get();
			if( oldCallbacks == null )
			{
				// This launcher has exited and nobody bothered to tell us. Just
				// bail.
				Log.w( TAG , "LoaderTask running with no launcher (loadAllAppsByBatch)" );
				return;
			}
			loadAllAppsDB();
			Iterator<FolderInfo> ite = appListFolders.values().iterator();
			boolean first = true;
			while( ite.hasNext() || first )
			{
				final boolean add = !first;
				first = false;
				final Callbacks callbacks = tryGetCallbacks( oldCallbacks );
				final ArrayList<FolderInfo> addFolders = new ArrayList<FolderInfo>();
				UserFolderInfo folder = null;
				int n = 0;
				while( n < 6 && ite.hasNext() )
				{
					folder = (UserFolderInfo)ite.next();
					addFolders.add( folder );
					n += folder.contents.size();
				}
				mHandler.post( new Runnable() {
					
					public void run()
					{
						if( callbacks != null )
						{
							if( !add )
							{
								callbacks.bindAppListFolders( addFolders );
							}
							else
							{
								callbacks.bindAppListFoldersAdded( addFolders );
							}
						}
						else
						{
							synchronized( LauncherModel.lock_allapp )
							{
								LauncherModel.waitBindApp = false;
								LauncherModel.lock_allapp.notify();
							}
							Log.i( TAG , "not binding apps: no Launcher activity" );
						}
					}
				} );
				waitForBindApp();
			}
			int N = Integer.MAX_VALUE;
			int startIndex;
			int i = 0;
			int batchSize = -1;
			String packageName;
			String className;
			int[] intkey = null;
			while( i < N && !mStopped )
			{
				if( i == 0 )
				{
					mAllAppsList.clear();
					if( apps == null )
					{
						return;
					}
					N = apps.size();
					if( DEBUG_LOADERS )
					{
						Log.d( TAG , "queryIntentActivities got " + N + " apps" );
					}
					if( N == 0 )
					{
						// There are no apps?!?
						return;
					}
					if( mBatchSize == 0 )
					{
						batchSize = N;
					}
					else
					{
						batchSize = mBatchSize;
					}
				}
				final long t2 = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
				startIndex = i;
				// teapotXu add start
				mAppsInMainmenuFolder.clear();
				// teapotXu add end
				for( int j = 0 ; i < N && j < batchSize ; j++ )
				{
					// This builds the icon bitmaps.
					packageName = apps.get( i ).activityInfo.packageName;
					className = apps.get( i ).activityInfo.name;
					if( packageName.equals( iLoongLauncher.getInstance().getPackageName() ) && className.equals( iLoongLauncher.getInstance().getComponentName().getClassName() ) )
					{
						i++;
						continue;
					}
					// xiatian start //for mainmenu sort by user
					// if(appListItems.containsKey(new
					// ComponentName(packageName,className).toString()))//xiatian
					// del
					// xiatian add start
					ShortcutInfo shortcutInfo = appListItems.get( new ComponentName( packageName , className ).toString() );
					if( shortcutInfo != null && shortcutInfo.container > LauncherSettings.Favorites.CONTAINER_APPLIST )// which
																														// container
																														// >
																														// CONTAINER_APPLIST
																														// -->
																														// are
																														// icons
																														// in
																														// Mainmenu
																														// folder
					// xiatian add end
					// xiatian end
					{
						// teapotXu add start
						mAppsInMainmenuFolder.add( new ApplicationInfo( apps.get( i ) , mIconCache ) );
						// teapotXu add end
						i++;
						continue;
					}
					mAllAppsList.add( new ApplicationInfo( apps.get( i ) , mIconCache ) );
					i++;
				}
				// final boolean first = i <= batchSize;
				final Callbacks callbacks = tryGetCallbacks( oldCallbacks );
				final ArrayList<ApplicationInfo> added = mAllAppsList.added;
				mAllAppsList.added = new ArrayList<ApplicationInfo>();
				// xiatian add start //for mainmenu sort by user
				// 此处需要把mAllAppsList中的icon都更新入AppListDB
				if( DefaultLayout.mainmenu_sort_by_user_fun == true )
				{
					ArrayList<ItemInfo> addAppItems = new ArrayList<ItemInfo>();
					for( ApplicationInfo appInfo : added )
					{
						long item_container = LauncherSettings.Favorites.CONTAINER_APPLIST;
						ComponentName appComponentName = appInfo.componentName;
						if( !appListItems.containsKey( appComponentName.toString() ) )
						{
							ShortcutInfo sInfo = appInfo.makeShortcut();
							sInfo.container = item_container;
							addAppItems.add( sInfo );
						}
						else
						{
							ShortcutInfo sInfo_DB = appListItems.get( appComponentName.toString() );
							appInfo.id = sInfo_DB.id;
							appInfo.container = sInfo_DB.container;
							appInfo.location_in_mainmenu = sInfo_DB.location_in_mainmenu;
						}
					}
					AppListDB.getInstance().BatchItemsInsert( addAppItems );
					loadAllAppsDB();
				}
				// xiatian add end
				mHandler.post( new Runnable() {
					
					public void run()
					{
						final long t = SystemClock.uptimeMillis();
						if( callbacks != null )
						{
							callbacks.bindAppsAdded( added );
							if( DEBUG_LOADERS )
							{
								Log.i( TAG , "bound " + added.size() + " apps in " + ( SystemClock.uptimeMillis() - t ) + "ms" );
							}
						}
						else
						{
							synchronized( LauncherModel.lock_allapp )
							{
								LauncherModel.waitBindApp = false;
								LauncherModel.lock_allapp.notify();
							}
							Log.i( TAG , "not binding apps: no Launcher activity" );
						}
					}
				} );
				waitForBindApp();
			}
			mHandler.post( new Runnable() {
				
				public void run()
				{
					final Callbacks callbacks = tryGetCallbacks( oldCallbacks );
					if( callbacks != null )
					{
						callbacks.finishBindApplications();
					}
					else
					{
						Log.i( TAG , "not finish binding apps: no Launcher activity" );
					}
				}
			} );
			// teapotXu add start: mAllAppList should include appInfos in
			// MainmenuFolder
			for( ApplicationInfo appInfo : mAppsInMainmenuFolder )
			{
				mAllAppsList.add( appInfo );
			}
			mAllAppsList.added.clear();
			// teapotXu add end
		}
		
		private void loadAllAppsByBatchOld()
		{
			final long t = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
			// Don't use these two variables in any of the callback runnables.
			// Otherwise we hold a reference to them.
			final Callbacks oldCallbacks = mCallbacks.get();
			if( oldCallbacks == null )
			{
				// This launcher has exited and nobody bothered to tell us. Just
				// bail.
				Log.w( TAG , "LoaderTask running with no launcher (loadAllAppsByBatch)" );
				return;
			}
			int N = Integer.MAX_VALUE;
			int startIndex;
			int i = 0;
			int batchSize = -1;
			while( i < N && !mStopped )
			{
				if( i == 0 )
				{
					mAllAppsList.clear();
					if( apps == null )
					{
						return;
					}
					N = apps.size();
					if( DEBUG_LOADERS )
					{
						Log.i( TAG , "queryIntentActivities got " + N + " apps" );
					}
					if( N == 0 )
					{
						// There are no apps?!?
						return;
					}
					if( mBatchSize == 0 )
					{
						batchSize = N;
					}
					else
					{
						batchSize = mBatchSize;
					}
					// sortApps(apps);
				}
				final long t2 = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
				startIndex = i;
				for( int j = 0 ; i < N && j < batchSize ; j++ )
				{
					// This builds the icon bitmaps.
					if( apps.get( i ).activityInfo.packageName.equals( iLoongLauncher.getInstance().getPackageName() ) && apps.get( i ).activityInfo.name.equals( iLoongLauncher.getInstance()
							.getComponentName().getClassName() ) )
					{
						i++;
						continue;
					}
					mAllAppsList.add( new ApplicationInfo( apps.get( i ) , mIconCache ) );
					i++;
				}
				final boolean first = i <= batchSize;
				final Callbacks callbacks = tryGetCallbacks( oldCallbacks );
				final ArrayList<ApplicationInfo> added = mAllAppsList.added;
				mAllAppsList.added = new ArrayList<ApplicationInfo>();
				mHandler.post( new Runnable() {
					
					public void run()
					{
						final long t = SystemClock.uptimeMillis();
						if( callbacks != null )
						{
							if( first )
							{
								callbacks.bindAllApplications( added );
							}
							else
							{
								callbacks.bindAppsAdded( added );
							}
							if( DEBUG_LOADERS )
							{
								Log.i( TAG , "bound " + added.size() + " apps in " + ( SystemClock.uptimeMillis() - t ) + "ms" );
							}
						}
						else
						{
							Log.i( TAG , "not binding apps: no Launcher activity" );
						}
					}
				} );
				waitForBindApp();
			}
			mHandler.post( new Runnable() {
				
				public void run()
				{
					final Callbacks callbacks = tryGetCallbacks( oldCallbacks );
					if( callbacks != null )
					{
						callbacks.finishBindApplications();
					}
					else
					{
						Log.i( TAG , "not finish binding apps: no Launcher activity" );
					}
				}
			} );
		}
	}
	
	public static class WidgetAndShortcutNameComparator implements Comparator<Object>
	{
		
		private PackageManager mPackageManager;
		private HashMap<Object , String> mLabelCache;
		
		public WidgetAndShortcutNameComparator(
				PackageManager pm )
		{
			mPackageManager = pm;
			mLabelCache = new HashMap<Object , String>();
		}
		
		public final int compare(
				Object a ,
				Object b )
		{
			String labelA , labelB;
			if( mLabelCache.containsKey( a ) )
			{
				labelA = mLabelCache.get( a );
			}
			else
			{
				labelA = ( a instanceof AppWidgetProviderInfo ) ? ( (AppWidgetProviderInfo)a ).label : ( (ResolveInfo)a ).loadLabel( mPackageManager ).toString();
				mLabelCache.put( a , labelA );
			}
			if( mLabelCache.containsKey( b ) )
			{
				labelB = mLabelCache.get( b );
			}
			else
			{
				labelB = ( b instanceof AppWidgetProviderInfo ) ? ( (AppWidgetProviderInfo)b ).label : ( (ResolveInfo)b ).loadLabel( mPackageManager ).toString();
				mLabelCache.put( b , labelB );
			}
			return sCollator.compare( labelA , labelB );
		}
	}
	
	void enqueuePackageUpdated(
			PackageUpdatedTask task )
	{
		sWorker.post( task );
	}
	
	public void sortApps(
			List<ResolveInfo> apps )
	{
		sortArray = new int[apps.size()];
		for( int i = 0 ; i < sortArray.length ; i++ )
		{
			sortArray[i] = -1;
		}
		SharedPreferences prefs = iLoongLauncher.getInstance().getSharedPreferences( "appsort" , Activity.MODE_PRIVATE );
		for( int i = 0 ; i < apps.size() ; i++ )
		{
			ComponentName componentName = new ComponentName( apps.get( i ).activityInfo.applicationInfo.packageName , apps.get( i ).activityInfo.name );
			int position = prefs.getInt( componentName.toString() , -1 );
			if( position > -1 && position < sortArray.length )
				sortArray[position] = i;
		}
		int index = 0;
		for( int i = 0 ; i < apps.size() ; i++ )
		{
			ComponentName componentName = new ComponentName( apps.get( i ).activityInfo.applicationInfo.packageName , apps.get( i ).activityInfo.name );
			int position = prefs.getInt( componentName.toString() , -1 );
			if( position < 0 || position >= sortArray.length )
			{
				for( int j = index ; j < sortArray.length ; j++ )
				{
					if( sortArray[j] == -1 )
					{
						sortArray[j] = i;
						index = j + 1;
						break;
					}
				}
			}
		}
	}
	
	private class PackageUpdatedTask implements Runnable
	{
		
		int mOp;
		String[] mPackages;
		public static final int OP_NONE = 0;
		public static final int OP_ADD = 1;
		public static final int OP_UPDATE = 2;
		public static final int OP_REMOVE = 3; // uninstlled
		public static final int OP_UNAVAILABLE = 4; // external media unmounted
		
		public PackageUpdatedTask(
				int op ,
				String[] packages )
		{
			mOp = op;
			mPackages = packages;
		}
		
		public void run()
		{
			final Context context = mApp;
			final String[] packages = mPackages;
			final int N = packages.length;
			final Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
			switch( mOp )
			{
				case OP_ADD:
					for( int i = 0 ; i < N ; i++ )
					{
						if( DEBUG_LOADERS )
							Log.d( TAG , "mAllAppsList.addPackage " + packages[i] );
						//Jone add start
						if( RR.net_version )
						{
							//its not necessary to add themebox to applist , which is embed to desktop for net version
							if( packages[i].equals( RR.getPackageName() ) )
							{
								Log.v( "Themebox" , "update applist bacause themebox has been launched " );
								continue;
							}
						}
						//Jone end
						mAllAppsList.addPackage( context , packages[i] );
						ThemeManager.getInstance().AddPackage( packages[i] );
						if( packages[i].equals( "com.iLoong.base.themebox" ) )
						{
							iLoongLauncher.getInstance().themeCenterDown.removeFinishNotification( context , packages[i] );
						}
						if( callbacks == null )
						{
							Log.w( TAG , "Nobody to tell about the new app.  Launcher is probably loading." );
							return;
						}
						final String packageName = packages[i];
						iLoongLauncher.getInstance().postRunnable( new Runnable() {
							
							public void run()
							{
								callbacks.bindWidget3DAdded( packageName );
							}
						} );
					}
					break;
				case OP_UPDATE:
					for( int i = 0 ; i < N ; i++ )
					{
						if( DEBUG_LOADERS )
							Log.d( TAG , "mAllAppsList.updatePackage " + packages[i] );
						//Jone add start
						if( RR.net_version )
						{
							if( packages[i].equalsIgnoreCase( RR.getPackageName() ) )
							{
								Log.v( "Themebox" , "update applist bacause themebox has been launched " );
								continue;
							}
						}
						mAllAppsList.updatePackage( context , packages[i] );
						ThemeManager.getInstance().UpdatePackage( packages[i] );
						if( callbacks == null )
						{
							Log.w( TAG , "Nobody to tell about the new app.  Launcher is probably loading." );
							return;
						}
						final String packageName = packages[i];
						iLoongLauncher.getInstance().postRunnable( new Runnable() {
							
							public void run()
							{
								if( callbacks == mCallbacks.get() )
								{
									callbacks.bindWidget3DUpdated( packageName );
								}
							}
						} );
					}
					break;
				case OP_REMOVE:
				case OP_UNAVAILABLE:
					for( int i = 0 ; i < N ; i++ )
					{
						if( DEBUG_LOADERS )
							Log.d( TAG , "mAllAppsList.removePackage " + packages[i] );
						mAllAppsList.removePackage( packages[i] );
						ThemeManager.getInstance().RemovePackage( packages[i] );
						Context mcontext = iLoongLauncher.getInstance();
						SharedPreferences sp = mcontext.getSharedPreferences( "CurrentTheme" , mcontext.MODE_PRIVATE );
						String currentTheme = sp.getString( "currenttheme_pkg" , "com.cooeeui.brand.turbolauncher" );
						if( currentTheme.equals( packages[i] ) )
						{
							sp.edit().putString( "currenttheme_pkg" , "com.cooeeui.brand.turbolauncher" ).commit();
						}
						if( callbacks == null )
						{
							Log.w( TAG , "Nobody to tell about the new app.  Launcher is probably loading." );
							return;
						}
						final String packageName = packages[i];
						iLoongLauncher.getInstance().postRunnable( new Runnable() {
							
							public void run()
							{
								if( callbacks == mCallbacks.get() )
								{
									callbacks.bindWidget3DRemoved( packageName );
								}
							}
						} );
					}
					break;
			}
			ArrayList<ApplicationInfo> added = null;
			ArrayList<ApplicationInfo> removed = null;
			ArrayList<ApplicationInfo> modified = null;
			if( mAllAppsList.added.size() > 0 )
			{
				added = mAllAppsList.added;
				mAllAppsList.added = new ArrayList<ApplicationInfo>();
			}
			if( mAllAppsList.removed.size() > 0 )
			{
				removed = mAllAppsList.removed;
				mAllAppsList.removed = new ArrayList<ApplicationInfo>();
				for( ApplicationInfo info : removed )
				{
					mIconCache.remove( info.intent.getComponent() );
				}
			}
			if( mAllAppsList.modified.size() > 0 )
			{
				modified = mAllAppsList.modified;
				mAllAppsList.modified = new ArrayList<ApplicationInfo>();
			}
			if( callbacks == null )
			{
				Log.w( TAG , "Nobody to tell about the new app.  Launcher is probably loading." );
				return;
			}
			if( added != null )
			{
				final ArrayList<ApplicationInfo> addedFinal = added;
				mHandler.post( new Runnable() {
					
					public void run()
					{
						if( callbacks == mCallbacks.get() )
						{
							callbacks.bindAppsAdded( addedFinal );

						}
					}
				} );
			}
			if( modified != null )
			{
				final ArrayList<ApplicationInfo> modifiedFinal = modified;
				mHandler.post( new Runnable() {
					
					public void run()
					{
						if( callbacks == mCallbacks.get() )
						{
							callbacks.bindAppsUpdated( modifiedFinal );
						}
					}
				} );
			}
			if( removed != null )
			{
				final boolean permanent = mOp != OP_UNAVAILABLE;
				final ArrayList<ApplicationInfo> removedFinal = removed;
				mHandler.post( new Runnable() {
					
					public void run()
					{
						if( callbacks == mCallbacks.get() )
						{
							callbacks.bindAppsRemoved( removedFinal , permanent );
						}
					}
				} );
			}
			if( iLoongApplication.BuiltIn && !DefaultLayout.hide_mainmenu_widget )
			{
				if( Desktop3DListener.initDone() )
					SendMsgToAndroid.updatePackage();
			}
		}
	}
	
	public void updatePackage()
	{
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				mAllWidgetsLoaded = false;
				LoaderTask loaderTask = new LoaderTask( iLoongLauncher.getInstance() , true );
				loaderTask.loadAndBindAllWidgets();
			}
		} ).start();
	}
	
	/**
	 * Make an ShortcutInfo object for a shortcut that is an application.
	 * 
	 * If c is not null, then it will be used to fill in missing data like the
	 * title and icon.
	 */
	public ShortcutInfo getShortcutInfo(
			PackageManager manager ,
			Intent intent ,
			Context context ,
			Cursor c ,
			int iconIndex ,
			int titleIndex )
	{
		Bitmap icon = null;
		final ShortcutInfo info = new ShortcutInfo();
		ComponentName componentName = intent.getComponent();
		if( componentName == null )
		{
			return null;
		}
		// TODO: See if the PackageManager knows about this case. If it doesn't
		// then return null & delete this.
		// the resource -- This may implicitly give us back the fallback icon,
		// but don't worry about that. All we're doing with usingFallbackIcon is
		// to avoid saving lots of copies of that in the database, and most apps
		// have icons anyway.
		final ResolveInfo resolveInfo = manager.resolveActivity( intent , 0 );
		if( resolveInfo != null )
		{
			icon = mIconCache.getIcon( componentName , resolveInfo );
		}
		// the db
		if( icon == null )
		{
			if( c != null )
			{
				icon = getIconFromCursor( c , iconIndex );
			}
		}
		// the fallback icon
		if( icon == null )
		{
			icon = getFallbackIcon();
			info.usingFallbackIcon = true;
		}
		info.setIcon( icon );
		// from the resource
		if( resolveInfo != null )
		{
			info.title = resolveInfo.activityInfo.loadLabel( manager );
		}
		// from the db
		if( info.title == null )
		{
			if( c != null )
			{
				info.title = c.getString( titleIndex );
			}
		}
		// fall back to the class name of the activity
		if( info.title == null )
		{
			info.title = componentName.getClassName();
		}
		info.itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
		return info;
	}
	
	/**
	 * Make an ShortcutInfo object for a shortcut that isn't an application.
	 */
	private ShortcutInfo getShortcutInfo(
			Cursor c ,
			Context context ,
			int iconTypeIndex ,
			int iconPackageIndex ,
			int iconResourceIndex ,
			int iconIndex ,
			int titleIndex ,
			Intent intent )
	{
		Bitmap icon = null;
		final ShortcutInfo info = new ShortcutInfo();
		info.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
		// TODO: If there's an explicit component and we can't install that,
		// delete it.
		info.title = c.getString( titleIndex );
		int iconType = c.getInt( iconTypeIndex );
		switch( iconType )
		{
			case LauncherSettings.Favorites.ICON_TYPE_RESOURCE:
				String packageName = c.getString( iconPackageIndex );
				String resourceName = c.getString( iconResourceIndex );
				PackageManager packageManager = context.getPackageManager();
				info.customIcon = false;
				// teapotXu add start for vanzo
				ResolveInfo resolveInfo = packageManager.resolveActivity( intent , 0 );
				if( DefaultLayout.customer_vanzo_operations && resolveInfo != null )
				{
					icon = Utilities.createIconBitmap( resolveInfo.activityInfo.loadIcon( packageManager ) , context );
				}
				else
				{
					// the resource
					try
					{
						Resources resources = packageManager.getResourcesForApplication( packageName );
						if( resources != null )
						{
							final int id = resources.getIdentifier( resourceName , null , null );
							icon = Utilities.createIconBitmap( resources.getDrawable( id ) , context );
						}
					}
					catch( Exception e )
					{
						// drop this. we have other places to look for icons
					}
				}
				// teapotXu add end
				// the db
				if( icon == null )
				{
					icon = getIconFromCursor( c , iconIndex );
				}
				int icon_type = 0;
				if( ( icon_type = Contact3DShortcut.isAContactShortcut( intent ) ) != Contact3DShortcut.CONTACT_NONE )
				{
					Bitmap bmp1;
					if( icon_type == Contact3DShortcut.CONTACT_DEFAULT )
					{
						int curLanguage = iLoongLauncher.curLanguage;/*
																		* 0: zh_CN, 1:
																		* zh_TW 2: en
																		*/
						if( curLanguage == 0 )
						{
							if( info.title.equals( "Contacts" ) || info.title.equals( "聯絡人" ) )
							{
								info.title = "联系人";
							}
						}
						else if( curLanguage == 1 )
						{
							if( info.title.equals( "Contacts" ) || info.title.equals( "联系" ) )
							{
								info.title = "聯絡人";
							}
						}
						else
						{
							if( info.title.equals( "联系人" ) || info.title.equals( "聯絡人" ) )
							{
								info.title = "Contacts";
							}
						}
						//Jone add
						//Receate quick contact name after changing of language
						if( RR.net_version )
						{
							if( curLanguage == 0 )
							{
								if( info.title.equals( "Quick Contacts" ) || info.title.equals( "快捷聯絡人" ) )
								{
									info.title = "快捷联系人";
								}
							}
							else if( curLanguage == 1 )
							{
								if( info.title.equals( "Quick Contacts" ) || info.title.equals( "快捷联系人" ) )
								{
									info.title = "快捷聯絡人";
								}
							}
							else
							{
								if( info.title.equals( "快捷联系人" ) || info.title.equals( "快捷聯絡人" ) )
								{
									info.title = "Quick Contacts";
								}
							}
						}
						//Jone end
					}
					Log.v( "test" , "getShortcutInfo right" );
					if( icon_type == Contact3DShortcut.CONTACT_NO_ICON )
					{
						bmp1 = Bitmap.createBitmap( Utilities.createIconBitmap( mApp.getApplicationContext().getResources().getDrawable( RR.drawable.ic_defaultcontact ) , mApp ) );
					}
					else
					{
						bmp1 = ThemeManager.getInstance().getBitmap( "theme/iconbg/contactperson-icon.png" );
					}
					icon = Bitmap.createScaledBitmap( bmp1 , R3D.sidebar_widget_w , R3D.sidebar_widget_h , true );
					if( bmp1 != icon )
						bmp1.recycle();
				}
				// the fallback icon
				if( icon == null )
				{
					icon = getFallbackIcon();
					info.usingFallbackIcon = true;
				}
				break;
			case LauncherSettings.Favorites.ICON_TYPE_BITMAP:
				icon = getIconFromCursor( c , iconIndex );
				if( icon == null )
				{
					icon = getFallbackIcon();
					info.customIcon = false;
					info.usingFallbackIcon = true;
				}
				else
				{
					info.customIcon = true;
				}
				break;
			default:
				icon = getFallbackIcon();
				info.usingFallbackIcon = true;
				info.customIcon = false;
				break;
		}
		// xiatian add start //fix bug:a apk in defaultIcon list, after auto
		// addShortcut,the icon not replace and icon scale as thirdapk icon
		if( intent.getComponent() != null )
		{
			Bitmap replaceIcon = null;
			replaceIcon = DefaultLayout.getInstance().getReplaceIcon( intent.getComponent().getPackageName() , intent.getComponent().getClassName() );
			if( replaceIcon != null )
			{
				icon = Tools.resizeBitmap( replaceIcon , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
			}
		}
		// xiatian add end
		info.setIcon( icon );
		return info;
	}
	
	public static Bitmap getIconFromCursor(
			Cursor c ,
			int iconIndex )
	{
		byte[] data = c.getBlob( iconIndex );
		try
		{
			return BitmapFactory.decodeByteArray( data , 0 , data.length );
		}
		catch( Exception e )
		{
			return null;
		}
	}
	
	void updateSavedIcon(
			Context context ,
			ShortcutInfo info ,
			Cursor c ,
			int iconIndex )
	{
		return;
		// // If this icon doesn't have a custom icon, check to see
		// // what's stored in the DB, and if it doesn't match what
		// // we're going to show, store what we are going to show back
		// // into the DB. We do this so when we're loading, if the
		// // package manager can't find an icon (for example because
		// // the app is on SD) then we can use that instead.
		// if (!info.customIcon && !info.usingFallbackIcon) {
		// boolean needSave = false;
		// byte[] data = c.getBlob(iconIndex);
		// try {
		// if (data != null) {
		// Bitmap saved = BitmapFactory.decodeByteArray(data, 0,
		// data.length);
		// Bitmap loaded = info.getIcon(mIconCache);
		// // needSave = !saved.sameAs(loaded);
		// } else {
		// needSave = true;
		// }
		// } catch (Exception e) {
		// needSave = true;
		// }
		// if (needSave) {
		// // This is slower than is ideal, but this only happens either
		// // after the froyo OTA or when the app is updated with a new
		// // icon.
		// updateItemInDatabase(context, info);
		// }
		// }
	}
	
	private static String getLabel(
			PackageManager manager ,
			ActivityInfo activityInfo )
	{
		String label = activityInfo.loadLabel( manager ).toString();
		if( label == null )
		{
			label = manager.getApplicationLabel( activityInfo.applicationInfo ).toString();
			if( label == null )
			{
				label = activityInfo.name;
			}
		}
		return label;
	}
	
	private static final Collator sCollator = Collator.getInstance();
	
	public void dumpState()
	{
		Log.d( TAG , "mCallbacks=" + mCallbacks );
		ApplicationInfo.dumpApplicationInfoList( TAG , "mAllAppsList.data" , mAllAppsList.data );
		ApplicationInfo.dumpApplicationInfoList( TAG , "mAllAppsList.added" , mAllAppsList.added );
		ApplicationInfo.dumpApplicationInfoList( TAG , "mAllAppsList.removed" , mAllAppsList.removed );
		ApplicationInfo.dumpApplicationInfoList( TAG , "mAllAppsList.modified" , mAllAppsList.modified );
		Log.d( TAG , "mItems size=" + mItems.size() );
	}
	
	public static int getCellCountX()
	{
		return 4;
	}
	
	public static int getCellCountY()
	{
		return 4;
	}
	
	public ShortcutInfo infoFromShortcutIntent(
			Context context ,
			Intent data )
	{
		Intent intent = data.getParcelableExtra( Intent.EXTRA_SHORTCUT_INTENT );
		String name = data.getStringExtra( Intent.EXTRA_SHORTCUT_NAME );
		Parcelable bitmap = data.getParcelableExtra( Intent.EXTRA_SHORTCUT_ICON );
		if( intent == null )
		{
			// If the intent is null, we can't construct a valid ShortcutInfo,
			// so we return null
			Log.e( TAG , "Can't construct ShorcutInfo with null intent" );
			return null;
		}
		Bitmap icon = null;
		Bitmap newIcon = null;
		boolean customIcon = false;
		ShortcutIconResource iconResource = null;
		if( bitmap != null && bitmap instanceof Bitmap )
		{
			BitmapDrawable drawable = new BitmapDrawable( (Bitmap)bitmap );
			drawable.setTargetDensity( iLoongLauncher.getInstance().getResources().getDisplayMetrics() );
			icon = Utilities.createIconBitmap( drawable , context );
			( (Bitmap)bitmap ).recycle();
			customIcon = true;
		}
		else
		{
			Parcelable extra = data.getParcelableExtra( Intent.EXTRA_SHORTCUT_ICON_RESOURCE );
			if( extra != null && extra instanceof ShortcutIconResource )
			{
				try
				{
					iconResource = (ShortcutIconResource)extra;
					final PackageManager packageManager = context.getPackageManager();
					ResolveInfo resolveInfo = packageManager.resolveActivity( intent , 0 );
					if( DefaultLayout.customer_vanzo_operations && resolveInfo != null )
					{
						icon = Utilities.createIconBitmap( resolveInfo.activityInfo.loadIcon( packageManager ) , context );
					}
					else
					{
						Resources resources = packageManager.getResourcesForApplication( iconResource.packageName );
						final int id = resources.getIdentifier( iconResource.resourceName , null , null );
						icon = Utilities.createIconBitmap( mIconCache.getFullResIcon( iconResource.packageName , id ) , context );
					}
				}
				catch( Exception e )
				{
					Log.w( TAG , "Could not load shortcut icon: " + extra );
				}
			}
		}
		final ShortcutInfo info = new ShortcutInfo();
		if( icon == null )
		{
			icon = Bitmap.createBitmap( Utilities.createIconBitmap( mApp.getApplicationContext().getResources().getDrawable( RR.drawable.ic_defaultcontact ) , mApp ) );
			// info.usingFallbackIcon = true;
		}
		info.title = name;
		info.intent = intent;
		info.customIcon = customIcon;
		info.iconResource = iconResource;
		if( icon != null )
		{
			// info.setIcon(icon);
			// newIcon = Bitmap.createBitmap(info
			// .getIcon(iLoongApplication.mIconCache));
			// if (info.intent != null && info.intent.getComponent() != null) {
			// if (DefaultLayout.thirdapk_icon_scaleFactor != 1.0f
			// && !R3D.doNotNeedScale(info.intent.getComponent()
			// .getPackageName(), info.intent.getComponent()
			// .getClassName())) {
			// newIcon = Tools.resizeBitmap(icon,
			// DefaultLayout.thirdapk_icon_scaleFactor);
			// }
			// } else {
			// if (DefaultLayout.thirdapk_icon_scaleFactor != 1.0f
			// && !R3D.doNotNeedScale(null, null)) {
			// newIcon = Tools.resizeBitmap(icon,
			// DefaultLayout.thirdapk_icon_scaleFactor);
			// }
			// }
			//
			// // newIcon = Tools.resizeBitmap(newIcon,
			// // DefaultLayout.thirdapk_icon_scaleFactor);
			// Bitmap copyIcon = newIcon.copy(newIcon.getConfig(), true);
			// info.setIcon(copyIcon);
			//
			// newIcon = Utils3D.IconToPixmap3D(newIcon, info.title.toString(),
			// Icon3D.getIconBg(), Icon3D.titleBg, false);
			// R3D.pack(info, name, newIcon, false);
			Bitmap bg = Icon3D.getIconBg();
			// if(DefaultLayout.use_cover_plate){
			// xiatian add start //fix bug:a apk in defaultIcon list, after auto
			// addShortcut,the icon not replace and icon scale as thirdapk icon
			if( intent.getComponent() != null )
			{
				Bitmap replaceIcon = null;
				replaceIcon = DefaultLayout.getInstance().getReplaceIcon( intent.getComponent().getPackageName() , intent.getComponent().getClassName() );
				if( replaceIcon != null )
				{
					icon = Tools.resizeBitmap( replaceIcon , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
					bg = null;
				}
			}
			// xiatian add end
			// }
			// sunyinwei changed for not save the scale shortcut icon 20131014
			Bitmap copyIcon = icon.copy( icon.getConfig() , true );
			info.setIcon( copyIcon );
			newIcon = Bitmap.createBitmap( info.getIcon( iLoongApplication.getInstance().mIconCache ) );
			if( intent.getComponent() != null )
			{
				if( !R3D.doNotNeedScale( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() ) )
				{
					newIcon = Tools.resizeBitmap( newIcon , (int)( DefaultLayout.app_icon_size * 0.98f ) , (int)( DefaultLayout.app_icon_size * 0.98f ) );
				}
			}
			else
			{
				if( DefaultLayout.thirdapk_icon_scaleFactor != 1.0f && !R3D.doNotNeedScale( null , null ) )
				{
					newIcon = Tools.resizeBitmap( newIcon , DefaultLayout.thirdapk_icon_scaleFactor );
				}
			}
			// if(DefaultLayout.use_cover_plate){
			// xiatian start //fix bug:a apk in defaultIcon list, after auto
			// addShortcut,the icon not replace and icon scale as thirdapk icon
			// xiatian del start
			// newIcon = Tools.resizeBitmap(newIcon,
			// DefaultLayout.thirdapk_icon_scaleFactor);
			// newIcon = Utils3D.IconToPixmap3D(newIcon, info.title.toString(),
			// Icon3D.getIconBg(), Icon3D.titleBg, false);
			// xiatian del end
			// xiatian add start
			if( info.intent != null && info.intent.getComponent() != null )
			{
				if( DefaultLayout.thirdapk_icon_scaleFactor != 1.0f && !R3D.doNotNeedScale( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() ) )
				{
					newIcon = Tools.resizeBitmap( icon , (int)( DefaultLayout.app_icon_size * 0.98f ) , (int)( DefaultLayout.app_icon_size * 0.98f ) );
				}
			}
			else
			{
				if( DefaultLayout.thirdapk_icon_scaleFactor != 1.0f && !R3D.doNotNeedScale( null , null ) )
				{
					newIcon = Tools.resizeBitmap( icon , DefaultLayout.thirdapk_icon_scaleFactor );
				}
			}
			// if(DefaultLayout.icon_similar_bg){
			// bg = Icon3D.getIconSimilarBg(newIcon);
			// }
			// sunyinwei add for default system shortcut start 20131031
			if( info.intent != null && info.intent.getComponent() != null && info.intent.getComponent().getPackageName() != null && info.intent.getComponent().getClassName() != null )
			{
				Bitmap replaceIcon = null;
				replaceIcon = DefaultLayout.getInstance().getDefaultShortcutIcon( intent.getComponent().getPackageName() , intent.getComponent().getClassName() );
				if( replaceIcon != null )
				{
					newIcon = Tools.resizeBitmap( replaceIcon , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
					info.setIcon( newIcon );
					bg = null;
				}
				else if( DefaultLayout.getInstance().hasSysShortcutIcon( intent.getComponent().getPackageName() , intent.getComponent().getClassName() ) )
				{
					replaceIcon = info.getIcon( iLoongApplication.mIconCache );
					if( replaceIcon != null )
					{
						newIcon = Tools.resizeBitmap( replaceIcon , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
						info.setIcon( newIcon );
						newIcon = Tools.resizeBitmap( newIcon , DefaultLayout.thirdapk_icon_scaleFactor );
					}
				}
			}
			// sunyinwei add for default system shortcut end 20131031
			newIcon = Utils3D.IconToPixmap3D( newIcon , info.title.toString() , bg , Icon3D.titleBg , false );
			// }
			// else{
			// //info.setIcon(icon);
			// if(DefaultLayout.icon_similar_bg){
			// newIcon = Utils3D.IconToPixmap3D(newIcon, info.title.toString(),
			// Icon3D.getIconSimilarBg(newIcon), Icon3D.titleBg, false);
			// }
			// else{
			// newIcon = Utils3D.IconToPixmap3D(newIcon, info.title.toString(),
			// Icon3D.getIconBg(), Icon3D.titleBg, false);
			// }
			// }
			// sunyinwei changed for just added shortcut not change by theme
			// without restart shortcut start 20131031
			R3D.pack( info , "" , newIcon , false );
		}
		else
		{
			R3D.pack( info , "" );
			info.setIcon( icon );
		}
		// sunyinwei changed for just added shortcut not ochange by theme without
		// restart shortcut end 20131031
		// info.iconResource = iconResource;
		return info;
	}
	
	public ShortcutInfo infoFromContactShortcutIntent(
			Context context ,
			Intent data )
	{
		String name = null;
		Bitmap bitmap = null;
		Intent intent = data.getParcelableExtra( Intent.EXTRA_SHORTCUT_INTENT );
		name = data.getStringExtra( Intent.EXTRA_SHORTCUT_NAME );
		bitmap = data.getParcelableExtra( Intent.EXTRA_SHORTCUT_ICON );
		Bitmap icon = null;
		boolean customIcon = false;
		ShortcutIconResource iconResource = null;
		if( bitmap != null && bitmap instanceof Bitmap )
		{
			// icon = Utilities.createIconBitmap(new BitmapDrawable(
			// (Bitmap) bitmap), context);
			// bitmap.recycle();
			icon = Utilities.createIconBitmap( new BitmapDrawable( bitmap ) , context );
			customIcon = true;
		}
		else
		{
			Parcelable extra = data.getParcelableExtra( Intent.EXTRA_SHORTCUT_ICON_RESOURCE );
			if( extra != null && extra instanceof ShortcutIconResource )
			{
				try
				{
					iconResource = (ShortcutIconResource)extra;
					final PackageManager packageManager = context.getPackageManager();
					Resources resources = packageManager.getResourcesForApplication( iconResource.packageName );
					final int id = resources.getIdentifier( iconResource.resourceName , null , null );
					icon = Utilities.createIconBitmap( resources.getDrawable( id ) , context );
				}
				catch( Exception e )
				{
					Log.w( TAG , "Could not load shortcut icon: " + extra );
				}
			}
		}
		final ShortcutInfo info = new ShortcutInfo();
		// if (icon == null) {
		// icon = getFallbackIcon();
		// info.usingFallbackIcon = true;
		// }
		if( icon == null )
		{
			icon = Bitmap.createBitmap( Utilities.createIconBitmap( mApp.getApplicationContext().getResources().getDrawable( RR.drawable.ic_defaultcontact ) , mApp ) );
			// intent.setComponent(new ComponentName("mark.as.default.contact",
			// "mark.as.default.contact"));
			// info.usingFallbackIcon = true;
		}
		info.setIcon( icon );
		info.title = name;
		info.intent = intent;
		info.customIcon = customIcon;
		return info;
	}
	
	public void changeLoadThreadPriority()
	{
		if( mLoaderTask != null )
		{
			try
			{
				if( DefaultLayout.cancel_dialog_last )
					return;
				else if( DefaultLayout.reduce_load_priority )
					android.os.Process.setThreadPriority( loadTaskId , Process.THREAD_PRIORITY_DEFAULT + 1 );
				else
					android.os.Process.setThreadPriority( loadTaskId , Process.THREAD_PRIORITY_DEFAULT );
			}
			catch( IllegalArgumentException e )
			{
				e.printStackTrace();
			}
			catch( SecurityException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	// xiatian add start
	// in !CoCoLauncher show theme icon in Launcher.if CoCoLauncher uninstalled
	// goto download CoCoCoCoLauncher,else goto themes activity.
	public boolean HideThemesInAppList(
			String packageName )
	{
		if( packageName != null )
		{
			return packageName.startsWith( ThemeManager.ACTION_INTENT_THEME );
		}
		return false;
	}
	
	// xiatian add end
	// teapotXu add start
	// Widget icon will not be shown in Launcher
	public boolean IsInstalledCooeeWidgets(
			List<ResolveInfo> mWidgetResolveInfoList ,
			String packageName )
	{
		for( ResolveInfo resolveInfo : mWidgetResolveInfoList )
		{
			if( packageName.equals( resolveInfo.activityInfo.packageName ) )
			{
				return true;
			}
		}
		return false;
	}
	
	// teapotXu add end
	public ArrayList<ItemInfo> getShortcutInfoListFromDb()
	{
		final long t = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
		final Context context = iLoongApplication.getInstance();
		final ContentResolver contentResolver = context.getContentResolver();
		final PackageManager manager = context.getPackageManager();
		final boolean isSafeMode = manager.isSafeMode();
		mItems.clear();
		Cursor c = null;
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			c = contentResolver.query( LauncherSettings.Favorites.CONTENT_URI_BIG_ICON , null , null , null , null );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			c = contentResolver.query( LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON , null , null , null , null );
		}
		else
		{
			c = contentResolver.query( LauncherSettings.Favorites.CONTENT_URI , null , null , null , null );
		}
		if( c != null )
		{
			try
			{
				final int idIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites._ID );
				final int intentIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.INTENT );
				final int titleIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.TITLE );
				final int iconTypeIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON_TYPE );
				final int iconIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON );
				final int iconPackageIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON_PACKAGE );
				final int iconResourceIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON_RESOURCE );
				final int containerIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.CONTAINER );
				final int itemTypeIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ITEM_TYPE );
				final int appWidgetIdIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.APPWIDGET_ID );
				final int screenIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.SCREEN );
				final int cellXIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.CELLX );
				final int cellYIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.CELLY );
				final int spanXIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.SPANX );
				final int spanYIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.SPANY );
				// added for workspace free layout items
				final int XIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.X );
				final int YIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.Y );
				// added for workspace free layout items
				final int uriIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.URI );
				final int displayModeIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.DISPLAY_MODE );
				final int angleIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ANGLE );
				ShortcutInfo info;
				String intentDescription;
				int container;
				long id;
				Intent intent;
				while( c.moveToNext() )
				{
					try
					{
						int itemType = c.getInt( itemTypeIndex );
						switch( itemType )
						{
							case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
								intentDescription = c.getString( intentIndex );
								try
								{
									intent = Intent.parseUri( intentDescription , 0 );
								}
								catch( URISyntaxException e )
								{
									continue;
								}
								if( itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
								{
									info = getShortcutInfo( manager , intent , context , c , iconIndex , titleIndex );
								}
								else
								{
									info = getShortcutInfo( c , context , iconTypeIndex , iconPackageIndex , iconResourceIndex , iconIndex , titleIndex , intent );
								}
								if( info != null )
								{
									updateSavedIcon( context , info , c , iconIndex );
									info.intent = intent;
									info.id = c.getLong( idIndex );
									container = c.getInt( containerIndex );
									info.container = container;
									info.screen = c.getInt( screenIndex );
									info.cellX = c.getInt( cellXIndex );
									info.cellY = c.getInt( cellYIndex );
									// added for workspace free layout items
									info.x = c.getInt( XIndex );
									info.y = c.getInt( YIndex );
									info.angle = c.getInt( angleIndex );
									// added for workspace free layout items
									/*
									 * delete for free layout
									 * 
									 * // check & update map of what's occupied if
									 * (!checkItemPlacement(occupied, info)) {
									 * break; }
									 */
									switch( container )
									{
										case LauncherSettings.Favorites.CONTAINER_DESKTOP:
										case LauncherSettings.Favorites.CONTAINER_HOTSEAT:
											mItems.add( info );
											break;
										default:
											break;
									}
								}
								else
								{
									// Failed to load the shortcut, probably
									// because the
									// activity manager couldn't resolve it
									// (maybe the app
									// was uninstalled), or the db row was
									// somehow screwed up.
									// Delete it.
									id = c.getLong( idIndex );
									Log.e( TAG , "Error loading shortcut " + id + ", removing it" );
									if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
									{
										contentResolver.delete( LauncherSettings.Favorites.getContentUriBigIcon( id , false ) , null , null );
									}
									else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
									{
										contentResolver.delete( LauncherSettings.Favorites.getContentUriSmallIcon( id , false ) , null , null );
									}
									else
									{
										contentResolver.delete( LauncherSettings.Favorites.getContentUri( id , false ) , null , null );
									}
								}
								break;
						}
					}
					catch( Exception e )
					{
						Log.w( TAG , "Desktop items loading interrupted:" , e );
					}
				}
			}
			finally
			{
				c.close();
			}
		}
		return mItems;
	}
	
	/* Remove the specified contents of the specified folder from the database */
	public static void deleteUserFolderContentsFromDatabase2(
			Context context ,
			UserFolderInfo info )
	{
		final ContentResolver cr = context.getContentResolver();
		Intent intent;
		String intentDescription;
		String title;
		ResolveInfo temp;
		cr.delete( LauncherSettings.Favorites.getContentUri( info.id , false ) , null , null );
		String selection = " container = info.id ";
		Cursor c = cr.query( LauncherSettings.Favorites.CONTENT_URI , null , "container =" + info.id , null , null );
		Log.v( TAG , "deleteUserFolderContentsFromDatabase1 rownum=" + c.getCount() );
		if( c != null )
		{
			try
			{
				while( c.moveToNext() )
				{
					intentDescription = c.getString( c.getColumnIndexOrThrow( LauncherSettings.Favorites.INTENT ) );
					Log.v( TAG , "deleteUserFolderContentsFromDatabase0 intentDescription=" + intentDescription );
					try
					{
						intent = Intent.parseUri( intentDescription , 0 );
						title = c.getString( c.getColumnIndexOrThrow( LauncherSettings.Favorites.TITLE ) );
						temp = context.getPackageManager().resolveActivity( intent , 0 );
						Log.v( TAG , "deleteUserFolderContentsFromDatabase1 intent=" + intent + "title=" + title + "temp=" + temp );
						if( temp == null )
						{
							cr.delete( LauncherSettings.Favorites.getContentUri( c.getLong( c.getColumnIndexOrThrow( LauncherSettings.Favorites._ID ) ) , false ) , null , null );
							Log.v( TAG , "deleteUserFolderContentsFromDatabase2 intent=" + intent + "title=" + title + "id=" + c.getLong( c.getColumnIndexOrThrow( LauncherSettings.Favorites._ID ) ) );
						}
					}
					catch( URISyntaxException e )
					{
						continue;
					}
				}
			}
			finally
			{
				c.close();
			}
		}
	}
	
	// xiatian add start //for mainmenu sort by user
	public HashMap<String , ShortcutInfo> getAppSInfosInAppListDB()
	{
		return appListItems;
	}
	
	public HashMap<Long , FolderInfo> getFolderInfosInAppListDB()
	{
		return appListFolders;
	}
	
	// xiatian add end
	public ArrayList<ItemInfo> getDesktopIcon()
	{
		final Context context = iLoongLauncher.getInstance();
		final ContentResolver contentResolver = context.getContentResolver();
		final PackageManager manager = context.getPackageManager();
		ArrayList<ItemInfo> itemInfos = new ArrayList<ItemInfo>();
		Cursor c = null;
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			c = contentResolver.query( LauncherSettings.Favorites.CONTENT_URI_BIG_ICON , null , null , null , null );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			c = contentResolver.query( LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON , null , null , null , null );
		}
		else
		{
			c = contentResolver.query( LauncherSettings.Favorites.CONTENT_URI , null , null , null , null );
		}
		if( c != null )
		{
			try
			{
				final int idIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites._ID );
				final int intentIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.INTENT );
				final int titleIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.TITLE );
				final int iconTypeIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON_TYPE );
				final int iconIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON );
				final int iconPackageIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON_PACKAGE );
				final int iconResourceIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ICON_RESOURCE );
				final int containerIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.CONTAINER );
				final int itemTypeIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ITEM_TYPE );
				final int screenIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.SCREEN );
				final int cellXIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.CELLX );
				final int cellYIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.CELLY );
				final int XIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.X );
				final int YIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.Y );
				final int angleIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.ANGLE );
				ShortcutInfo info;
				String intentDescription;
				int container;
				long id;
				Intent intent;
				while( c.moveToNext() )
				{
					int itemType = c.getInt( itemTypeIndex );
					switch( itemType )
					{
						case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
						case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
							intentDescription = c.getString( intentIndex );
							try
							{
								intent = Intent.parseUri( intentDescription , 0 );
							}
							catch( URISyntaxException e )
							{
								continue;
							}
							if( itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
							{
								info = getShortcutInfo( manager , intent , context , c , iconIndex , titleIndex );
							}
							else
							{
								info = getShortcutInfo( c , context , iconTypeIndex , iconPackageIndex , iconResourceIndex , iconIndex , titleIndex , intent );
							}
							if( info != null )
							{
								updateSavedIcon( context , info , c , iconIndex );
								info.intent = intent;
								info.id = c.getLong( idIndex );
								container = c.getInt( containerIndex );
								info.container = container;
								info.screen = c.getInt( screenIndex );
								info.cellX = c.getInt( cellXIndex );
								info.cellY = c.getInt( cellYIndex );
								info.angle = c.getInt( angleIndex );
								switch( container )
								{
									case LauncherSettings.Favorites.CONTAINER_DESKTOP:
										itemInfos.add( info );
										break;
									case LauncherSettings.Favorites.CONTAINER_HOTSEAT:
										if( info.intent == null || info.intent.getComponent() == null || info.intent.getComponent().getPackageName() == null )
										{
											itemInfos.add( info );
										}
										else if( DefaultLayout.checkApkExist( iLoongLauncher.getInstance() , info.intent.getComponent().getPackageName() ) )
										{
											itemInfos.add( info );
										}
										break;
									case LauncherSettings.Favorites.CONTAINER_SIDEBAR:
										break;
									default:
										UserFolderInfo folderInfo = findOrMakeUserFolder( mFolders , container );
										if( info.intent == null || info.intent.getComponent() == null || info.intent.getComponent().getPackageName() == null )
										{
											if( folderInfo.contents.size() >= R3D.folder_max_num )
											{
												deleteItemFromDatabase( context , info );
											}
											else
											{
												folderInfo.add( info );
											}
										}
										else if( DefaultLayout.checkApkExist( context , info.intent.getComponent().getPackageName() ) )
										{
											if( folderInfo.contents.size() >= R3D.folder_max_num )
											{
												deleteItemFromDatabase( context , info );
											}
											else
											{
												folderInfo.add( info );
											}
										}
										break;
								}
							}
							else
							{
								id = c.getLong( idIndex );
								if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
								{
									contentResolver.delete( LauncherSettings.Favorites.getContentUriBigIcon( id , false ) , null , null );
								}
								else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
								{
									contentResolver.delete( LauncherSettings.Favorites.getContentUriSmallIcon( id , false ) , null , null );
								}
								else
								{
									contentResolver.delete( LauncherSettings.Favorites.getContentUri( id , false ) , null , null );
								}
							}
							break;
						case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
							id = c.getLong( idIndex );
							UserFolderInfo folderInfo = findOrMakeUserFolder( mFolders , id );
							folderInfo.title = c.getString( titleIndex );
							int curLanguage = iLoongLauncher.curLanguage;
							String title = folderInfo.title.toString();
							if( title.endsWith( "x.z" ) )
							{
								int length = title.length();
								if( length > 3 )
								{
									title = title.substring( 0 , length - 3 );
								}
							}
							if( curLanguage == 0 )
							{
								if( title.equals( "Folder" ) || title.equals( "資料夾" ) )
								{
									folderInfo.title = "文件夹";
								}
							}
							else if( curLanguage == 1 )
							{
								if( title.equals( "Folder" ) || title.equals( "文件夹" ) )
								{
									folderInfo.title = "資料夾";
								}
							}
							else
							{
								if( title.equals( "文件夾" ) || title.equals( "資料夾" ) )
								{
									folderInfo.title = "Folder";
								}
							}
							folderInfo.id = id;
							container = c.getInt( containerIndex );
							folderInfo.screen = c.getInt( screenIndex );
							folderInfo.cellX = c.getInt( cellXIndex );
							folderInfo.cellY = c.getInt( cellYIndex );
							folderInfo.x = c.getInt( XIndex );
							folderInfo.y = c.getInt( YIndex );
							folderInfo.iconResource = c.getString( iconResourceIndex );
							intentDescription = c.getString( intentIndex );
							if( intentDescription != null )
							{
								try
								{
									intent = Intent.parseUri( intentDescription , 0 );
								}
								catch( URISyntaxException e )
								{
									break;
								}
								if( intent != null )
								{
									folderInfo.folderid = intent.getStringExtra( "folderid" );
									folderInfo.iconName = intent.getStringExtra( "iconName" );
								}
							}
							switch( container )
							{
								case LauncherSettings.Favorites.CONTAINER_DESKTOP:
								case LauncherSettings.Favorites.CONTAINER_HOTSEAT:
									itemInfos.add( folderInfo );
									break;
							}
							mFolders.put( folderInfo.id , folderInfo );
							break;
						default:
							break;
					}
				}
			}
			finally
			{
				c.close();
			}
		}
		return itemInfos;
	}
	
	private void loadAllAppsFromDB()
	{
		final long t = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
		final Context context = iLoongLauncher.getInstance();
		final ContentResolver contentResolver = context.getContentResolver();
		final PackageManager manager = context.getPackageManager();
		final boolean isSafeMode = manager.isSafeMode();
		appListItems.clear();
		appListFolders.clear();
		Cursor c = AppListDB.getInstance().queryAll();
		if( c != null )
		{
			try
			{
				final int idIndex = c.getColumnIndexOrThrow( "id" );
				final int intentIndex = c.getColumnIndexOrThrow( "intent" );
				final int titleIndex = c.getColumnIndexOrThrow( "title" );
				final int itemTypeIndex = c.getColumnIndexOrThrow( "item_type" );
				final int containerIndex = c.getColumnIndexOrThrow( "container" );
				final int lastUpdateTimeIndex = c.getColumnIndexOrThrow( "last_update_time" );
				final int useFrequencyIndex = c.getColumnIndexOrThrow( "use_frequency" );
				final int mainmenuLoactionIndex = c.getColumnIndexOrThrow( "int1" );// xiatian add //for mainmenu sort by user
				ApplicationInfo info;
				String packageName;
				String className;
				int container;
				long id;
				Intent intent;
				String intentDescription;
				ResolveInfo resolveInfo;
				while( c.moveToNext() )
				{
					try
					{
						int itemType = c.getInt( itemTypeIndex );
						switch( itemType )
						{
							case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
								container = c.getInt( containerIndex );
								if( container
								// xiatian start //for mainmenu sort by user
								// >//xiatian del
								>= // xiatian add
									// xiatian end
								LauncherSettings.Favorites.CONTAINER_APPLIST )
								{
									ShortcutInfo sInfo = null;
									ComponentName componentName = null;
									intentDescription = c.getString( intentIndex );
									try
									{
										intent = Intent.parseUri( intentDescription , 0 );
									}
									catch( URISyntaxException e )
									{
										continue;
									}
									componentName = intent.getComponent();
									if( componentName == null )
									{
										continue;
									}
									resolveInfo = manager.resolveActivity( intent , 0 );
									if( resolveInfo == null )
									{
										continue;
									}
									info = new ApplicationInfo( resolveInfo );
									// from the resource
									if( resolveInfo != null )
									{
										info.title = resolveInfo.activityInfo.loadLabel( manager );
									}
									if( info.title == null )
									{
										info.title = c.getString( titleIndex );
									}
									info.container = container;
									sInfo = info.makeShortcut();
									sInfo.id = c.getLong( idIndex );
									sInfo.appInfo.id = sInfo.id;// xiatian add
																// //for
																// mainmenu sort
																// by user
									UserFolderInfo folderInfo = findOrMakeUserFolder( appListFolders , container );
									if( folderInfo != null )// xiatian add //for
															// mainmenu sort by
															// user
										folderInfo.add( sInfo );
									sInfo.container = container;
									sInfo.location_in_mainmenu = c.getInt( mainmenuLoactionIndex );
									sInfo.appInfo.container = sInfo.container;
									sInfo.appInfo.location_in_mainmenu = sInfo.location_in_mainmenu;
									appListItems.put( componentName.toString() , sInfo );
								}
								else
								{
									// Failed to load the shortcut, probably
									// because the
									// activity manager couldn't resolve it
									// (maybe the app
									// was uninstalled), or the db row was
									// somehow screwed up.
									// Delete it.
									id = c.getLong( idIndex );
									Log.e( TAG , "Error loading shortcut " + id + ", removing it" );
								}
								break;
							case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
								id = c.getLong( idIndex ) + LauncherSettings.Favorites.CONTAINER_APPLIST + 1;
								UserFolderInfo folderInfo = findOrMakeUserFolder( appListFolders , id );
								folderInfo.title = c.getString( titleIndex );
								folderInfo.lastUpdateTime = c.getLong( lastUpdateTimeIndex );
								folderInfo.use_frequency = c.getInt( useFrequencyIndex );
								folderInfo.location_in_mainmenu = c.getInt( mainmenuLoactionIndex );// xiatian
																									// add
																									// //for
																									// mainmenu
																									// sort
																									// by
																									// user
								int curLanguage = iLoongLauncher.curLanguage;
								if( curLanguage == 0 )
								{
									if( folderInfo.title.equals( "Folder" ) || folderInfo.title.equals( "資料夾" ) )
									{
										folderInfo.title = "文件夹";
									}
								}
								else if( curLanguage == 1 )
								{
									if( folderInfo.title.equals( "Folder" ) || folderInfo.title.equals( "文件夹" ) )
									{
										folderInfo.title = "資料夾";
									}
								}
								else
								{
									if( folderInfo.title.equals( "文件夹" ) || folderInfo.title.equals( "資料夾" ) )
									{
										folderInfo.title = "Folder";
									}
								}
								folderInfo.id = id;
								folderInfo.container = c.getInt( containerIndex );
								intentDescription = c.getString( intentIndex );
								if( intentDescription != null )
								{
									try
									{
										intent = Intent.parseUri( intentDescription , 0 );
									}
									catch( URISyntaxException e )
									{
										break;
									}
									if( intent != null )
									{
										folderInfo.folderid = intent.getStringExtra( "folderid" );
										folderInfo.iconName = intent.getStringExtra( "iconName" );
									}
								}
								iLoongLauncher.getInstance().addFolderInfoToSFolders( folderInfo );
								break;
						}
					}
					catch( Exception e )
					{
						Log.w( TAG , "Desktop items loading interrupted:" , e );
					}
				}
			}
			finally
			{
				c.close();
			}
		}
	}
	
	public void loadAppListPre()
	{
		final Callbacks oldCallbacks = mCallbacks.get();
		if( oldCallbacks == null )
		{
			// This launcher has exited and nobody bothered to tell us. Just
			// bail.
			Log.w( TAG , "LoaderTask running with no launcher (loadAllAppsByBatch)" );
			return;
		}
		loadAllAppsFromDB();

		
		int N = Integer.MAX_VALUE;
		int startIndex;
		int i = 0;
		int batchSize = -1;
		String packageName;
		String className;
		int[] intkey = null;
		while( i < N )
		{
			if( i == 0 )
			{
				mAllAppsList.clear();
				if( apps == null )
				{
					return;
				}
				N = apps.size();
				if( DEBUG_LOADERS )
				{
					Log.d( TAG , "queryIntentActivities got " + N + " apps" );
				}
				if( N == 0 )
				{
					// There are no apps?!?
					return;
				}
				if( mBatchSize == 0 )
				{
					batchSize = N;
				}
				else
				{
					batchSize = mBatchSize;
				}
			}
			
			startIndex = i;
			// teapotXu add start
			mAppsInMainmenuFolder.clear();
			// teapotXu add end
			for( int j = 0 ; i < N && j < batchSize ; j++ )
			{
				// This builds the icon bitmaps.
				packageName = apps.get( i ).activityInfo.packageName;
				className = apps.get( i ).activityInfo.name;
				if( packageName.equals( iLoongLauncher.getInstance().getPackageName() ) && className.equals( iLoongLauncher.getInstance().getComponentName().getClassName() ) )
				{
					i++;
					continue;
				}
				// xiatian start //for mainmenu sort by user
				// if(appListItems.containsKey(new
				// ComponentName(packageName,className).toString()))//xiatian
				// del
				// xiatian add start
				ShortcutInfo shortcutInfo = appListItems.get( new ComponentName( packageName , className ).toString() );
				if( shortcutInfo != null && shortcutInfo.container > LauncherSettings.Favorites.CONTAINER_APPLIST )// which
																													// container
																													// >
																													// CONTAINER_APPLIST
																													// -->
																													// are
																													// icons
																													// in
																													// Mainmenu
																													// folder
				// xiatian add end
				// xiatian end
				{
					// teapotXu add start
					mAppsInMainmenuFolder.add( new ApplicationInfo( apps.get( i ) , mIconCache ) );
					// teapotXu add end
					i++;
					continue;
				}
				mAllAppsList.add( new ApplicationInfo( apps.get( i ) , mIconCache ) );
				i++;
			}
			// final boolean first = i <= batchSize;
//			final Callbacks callbacks = mCallbacks.get();
//			final ArrayList<ApplicationInfo> added = mAllAppsList.added;
//			mAllAppsList.added = new ArrayList<ApplicationInfo>();
//			// xiatian add start //for mainmenu sort by user
//			// 此处需要把mAllAppsList中的icon都更新入AppListDB
//			if( DefaultLayout.mainmenu_sort_by_user_fun == true )
//			{
//				ArrayList<ItemInfo> addAppItems = new ArrayList<ItemInfo>();
//				for( ApplicationInfo appInfo : added )
//				{
//					long item_container = LauncherSettings.Favorites.CONTAINER_APPLIST;
//					ComponentName appComponentName = appInfo.componentName;
//					if( !appListItems.containsKey( appComponentName.toString() ) )
//					{
//						ShortcutInfo sInfo = appInfo.makeShortcut();
//						sInfo.container = item_container;
//						addAppItems.add( sInfo );
//					}
//					else
//					{
//						ShortcutInfo sInfo_DB = appListItems.get( appComponentName.toString() );
//						appInfo.id = sInfo_DB.id;
//						appInfo.container = sInfo_DB.container;
//						appInfo.location_in_mainmenu = sInfo_DB.location_in_mainmenu;
//					}
//				}
//				AppListDB.getInstance().BatchItemsInsert( addAppItems );
//				loadAllAppsFromDB();
//			}
			
		}
		
		loadAppListPreDone = true;
	}
	
	public synchronized void loadAppList()
	{
		if( appListLoaded )
		{
			return;
		}
		final long t = SystemClock.uptimeMillis();
		// Don't use these two variables in any of the callback runnables.
		// Otherwise we hold a reference to them.
		
		Iterator<FolderInfo> ite = appListFolders.values().iterator();
		boolean first = true;
		while( ite.hasNext() || first )
		{
			final boolean add = !first;
			first = false;
			final Callbacks callbacks = mCallbacks.get();
			final ArrayList<FolderInfo> addFolders = new ArrayList<FolderInfo>();
			UserFolderInfo folder = null;
			int n = 0;
			while( n < 6 && ite.hasNext() )
			{
				folder = (UserFolderInfo)ite.next();
				addFolders.add( folder );
				n += folder.contents.size();
			}
			if( !add )
			{
				callbacks.bindAppListFolders( addFolders );
			}
			else
			{
				callbacks.bindAppListFoldersAdded( addFolders );
			}
		}
		
		final Callbacks callbacks = mCallbacks.get();
		
		for( ApplicationInfo appInfo : mAppsInMainmenuFolder )
		{
			mAllAppsList.add( appInfo );
		}
		
		callbacks.bindAppsAdded( mAllAppsList.added );
		
		callbacks.finishBindApplications();
		// teapotXu add start: mAllAppList should include appInfos in
		// MainmenuFolder

		
		// teapotXu add end
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				mAllAppsList.added.clear();
				appListLoaded = true;
			}
		} );
	}
}
