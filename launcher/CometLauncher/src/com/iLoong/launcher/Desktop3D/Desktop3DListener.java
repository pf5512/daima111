package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.HotSeat3D.DefConfig;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.SetupMenu.Actions.SystemAction;
import com.iLoong.launcher.SetupMenu.Actions.SystemAction.ResestActivity;
import com.iLoong.launcher.UI3DEngine.Desktop3D;
import com.iLoong.launcher.UI3DEngine.ParticleLoader;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Contact3DShortcut;
import com.iLoong.launcher.Widget3D.Folder3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.app.LauncherModel;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.cling.ClingManager.ClingTarget;
import com.iLoong.launcher.cling.FlipView;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.FolderInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.data.Widget2DInfo;
import com.iLoong.launcher.data.Widget3DInfo;
import com.iLoong.launcher.data.WidgetShortcutInfo;
import com.iLoong.launcher.desktop.FeatureConfig;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.desktopEdit.DesktopEdit;
import com.iLoong.launcher.scene.SceneManager;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.widget.Widget;


public class Desktop3DListener implements ApplicationListener , LauncherModel.Callbacks
{
	
	public static final String TAG = "D3DListener";
	public static Object lock = new Object();
	public static Object init_lock = new Object();
	private iLoongLauncher launcher;
	public static Desktop3D d3d = null;
	public static Root3D root;
	private PageIndicator3D pageSelectIcon;
	private TrashIcon3D trashIcon;
	public Workspace3D workspace;
	private DesktopEdit desktopEdit; // added by zhenNan.ye
	private static AppHost3D appHost;
	private DragLayer3D dragLayer;
	public static boolean bCreatDone = false;
	public static boolean bSetHomepageDone = false;
	public static boolean bCreat1Done = false;
	public static boolean bDesktopDone = false;
	public static boolean bAppDone = false;
	public static boolean bWidgetDone = false;
	private IconCache iconCache;
	private ArrayList<ItemInfo> mDesktopItems = new ArrayList<ItemInfo>();
	private ArrayList<ItemInfo> mShortcutItems = new ArrayList<ItemInfo>();
	private ArrayList<WidgetShortcutInfo> allWidgetInfo = new ArrayList<WidgetShortcutInfo>();
	public static Folder3DShortcut folder3DHost;
	public static Contact3DShortcut contact3DHost;
	// 自定义widget管理?
	private Widget3DManager mWidget3DManagerInstance;
	private Drawable mDefaultWidgetBackground;
	public boolean mPaused = false;
	public boolean mOnResumeNeedsLoad = false;
	private FlipView flipView;
	public boolean showIntroduction;
	private boolean hasCreate = false;
	// xiatian add start //fix bug:bindWidget3DUpdated lead to lose Widget3DView
	// in Workspace3D
	private boolean mIsWidget3DUpdated = false;
	private Widget3DInfo mUpdatedWidget3DInfo = null;
	
	// xiatian add end
	// private Class<?> c = null;
	// public Desktop3DListener(Class<?> c) {
	// this.c = c;
	// }
	public void forceTouchUp()
	{
		if( d3d != null )
		{
			if( dragLayer != null )
				dragLayer.forceTouchUp();
			d3d.forceTouchUp();
		}
	}
	
	public Desktop3DListener(
			iLoongLauncher iloong )
	{
		this.launcher = iloong;
		iconCache = ( (iLoongApplication)launcher.getApplication() ).getIconCache();
		bCreatDone = false;
	}
	
	public AppHost3D getAppList()
	{
		return appHost;
	}
	
	public DragLayer3D getDragLayer()
	{
		return dragLayer;
	}
	
	public TrashIcon3D getTrashIcon()
	{
		return trashIcon;
	}
	
	public Workspace3D getWorkspace3D()
	{
		return workspace;
	}
	
	public Root3D getRoot()
	{
		return root;
	}
	
	public static boolean initDone()
	{
		return bDesktopDone & bAppDone & ( bWidgetDone || !iLoongApplication.BuiltIn || DefaultLayout.hide_mainmenu_widget );
	}
	
	// wanghongjian add start //enable_DefaultScene
	public boolean freeMainVisible()
	{
		return root.FreeMainVisible();
	}
	
	// wanghongjian add end
	@Override
	public void create()
	{
		// Utils3D.showPidMemoryInfo("create d3d");
		Utils3D.showTimeFromStart( "d3d create1 1" );
		R3D.initialize( iLoongLauncher.getInstance() );
		Utils3D.showTimeFromStart( "d3d create1 1.1" );
		Widget3DManager.getInstance().processDefaultWidgetView();
		Utils3D.showTimeFromStart( "d3d create1 1.2" );
		Tween.registerAccessor( View3D.class , new View3DTweenAccessor() );
		R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
		Utils3D.showTimeFromStart( "d3d create1 1.3" );
		d3d = new Desktop3D( Utils3D.getScreenWidth() , Utils3D.getScreenHeight() , true );
		Gdx.input.setInputProcessor( d3d );
		// Tween.setWaypointsLimit(10);
		Utils3D.showTimeFromStart( "d3d create1 2" );
		root = new Root3D( "root" );
		root.setSize( Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
		root.setLauncher( launcher );
		d3d.addView( root );
		Utils3D.showTimeFromStart( "d3d create1 3" );
		pageSelectIcon = new PageIndicator3D( "pageselected3dicon" );
		root.setPageIndicator( pageSelectIcon );
		// pageSelectIcon.setPosition(0, 0);
		// int cellNum = PreferenceManager.getDefaultSharedPreferences(
		// SetupMenu.getContext()).getInt("cell_num",
		// DefaultLayout.default_workspace_pagecounts);
		int cellNum = ThemeManager.getInstance().getThemeDB().getScreenCount();
		if( cellNum == 0 )
		{
			cellNum = DefaultLayout.default_workspace_pagecounts;
			ThemeManager.getInstance().getThemeDB().SaveScreenCount( cellNum );
		}
		pageSelectIcon.setPageNum( cellNum );
		// DragLayer must be added at last!!!
		root.getHotSeatBar().setIconCache( iconCache );
		dragLayer = new DragLayer3D( "draglayer" );
		root.setDragLayer( dragLayer );
		DefaultLayout.setIconCache( iconCache );
		bCreat1Done = true;
		synchronized( lock )
		{
			lock.notify();
		}
		if( showIntroduction )
		{
			flipView = new FlipView( "intro" );
			flipView.setLauncher( launcher );
			d3d.addView( flipView );
			root.hide();
			iLoongLauncher.getInstance().introductionShown = true;
		}
		Utils3D.showTimeFromStart( "d3d create1 4" );
		/****************** added by zhenNan.ye begin *******************/
		if( DefaultLayout.enable_particle )
		{
			ParticleLoader.loadAllParticleEffect();
		}
		/****************** added by zhenNan.ye end *******************/
	}
	
	/**
	 * 
	 */
	public void create2()
	{
		Utils3D.showTimeFromStart( "start d3d create2" );
		R3D.initialize2( iLoongLauncher.getInstance() );
		folder3DHost = new Folder3DShortcut( "folder3d" );
		contact3DHost = new Contact3DShortcut( "contact3d" );
		workspace = new Workspace3D( "workspace" );
		int defaultHomePageIndex = DefaultLayout.default_home_page;
		int homePage = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getInt( "home_page" , defaultHomePageIndex );
		// int cellNum = PreferenceManager.getDefaultSharedPreferences(
		// SetupMenu.getContext()).getInt("cell_num",
		// DefaultLayout.default_workspace_pagecounts);
		int cellNum = ThemeManager.getInstance().getThemeDB().getScreenCount();
		if( cellNum == 0 )
		{
			cellNum = DefaultLayout.default_workspace_pagecounts;
			ThemeManager.getInstance().getThemeDB().SaveScreenCount( cellNum );
		}
		for( int i = 0 ; i < cellNum ; i++ )
		{
			CellLayout3D cellLayout = new CellLayout3D( "celllayout" );
			cellLayout.setScreen( i );
			workspace.addPage( cellLayout );
		}
		workspace.setHomePage( homePage );
		workspace.setCurrentScreen( homePage );
		appHost = new AppHost3D( "apphost" );
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
			appHost.setDragLayer( dragLayer );
		// teapotXu add end for Folder in Mainmenu
		desktopEdit = new DesktopEdit( "desktopEdit" );
		root.setDesktopEdit( desktopEdit );
		root.setWorkspace( workspace );
		root.setAppHost( appHost );
		addPageScrollListener( pageSelectIcon );
		addPageScrollListener( iLoongLauncher.getInstance().xWorkspace );
		dragLayer.addDropTarget( workspace );
		dragLayer.addDropTarget( desktopEdit );
		pageSelectIcon.bringToFront();
		trashIcon = new TrashIcon3D();
		root.setTrashIcon( trashIcon );
		dragLayer.addDropTarget( root.getHotSeatBar() );
		dragLayer.addDropTarget( trashIcon );
		root.addDragLayer( dragLayer );
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
			dragLayer.addDropTarget( appHost );
		// teapotXu add end for Folder in Mainmenu
		appHost.setIconCache( iconCache );
		workspace.setIconCache( iconCache );
		mWidget3DManagerInstance = Widget3DManager.getInstance();
		DefaultLayout.setEnv( root , workspace );
		mDefaultWidgetBackground = launcher.getResources().getDrawable( RR.drawable.default_widget_preview_holo );
		bCreatDone = true;
		Gdx.graphics.setContinuousRendering( false );
		pause();
		Utils3D.showTimeFromStart( "end d3d create2" );
		System.gc();
	}
	
	public void showIntroduction()
	{
		if( bCreat1Done )
		{
			flipView = new FlipView( "intro" );
			flipView.setLauncher( launcher );
			d3d.addView( flipView );
			root.hide();
			iLoongLauncher.getInstance().introductionShown = true;
		}
		else
			showIntroduction = true;
	}
	
	@Override
	public void resume()
	{
		// TODO Auto-generated method stub
		// launcher.refreshWidget();
		// Gdx.graphics.setContinuousRendering(true);
		if( appHost != null )
			appHost.resume();
		// zhujieping add start
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable )
		{
			FolderIcon3D.genWallpaperTextureRegion();
		}
		// zhujieping add end
	}
	
	@Override
	public void render()
	{
		// TODO Auto-generated method stub
		// cur = System.currentTimeMillis();
		View3DTweenAccessor.manager.update( Gdx.graphics.getRawDeltaTime() );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT );
		// Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
		// Gdx.gl.glEnable(GL10.GL_ALPHA_TEST);
		d3d.draw();
		// Utils3D.showPidMemoryInfo("render");
	}
	
	public void setFlipView(
			FlipView flipView )
	{
		this.flipView = flipView;
	}
	
	@Override
	public void resize(
			int width ,
			int height )
	{
		Utils3D.resetSize();
		Log.v( "resize" , "width:" + width + " height:" + height );
		Log.v( "resize" , "utils3d width:" + Utils3D.getScreenWidth() + " height:" + Utils3D.getScreenHeight() );
		if( Utils3D.getScreenWidth() > Utils3D.getScreenHeight() )
		{
			Log.e( "resize" , "width and height error" );
			if( iLoongLauncher.getInstance().stoped )
			{
				Log.e( "resize" , "width and height error:stoped" );
				iLoongLauncher.getInstance().checkSize = true;
			}
			else if( iLoongLauncher.getInstance().d3dListener.mPaused )
			{
				Log.e( "resize" , "width and height error:paused" );
				iLoongLauncher.getInstance().checkSize = true;
				// Log.e("resize","width and height error:Remove view...");
				// SendMsgToAndroid.sendRemoveGLViewMsg();
			}
			else
			{
				if( width > height )
				{
					Log.e( "resize" , "width and height error:Restart..." );
					SystemAction.RestartSystem();
				}
				else
				{
					Log.e( "resize" , "width and height error:resumed..." );
					iLoongLauncher.getInstance().checkSize = true;
					SendMsgToAndroid.sendCheckSizeMsg( 3000 );
				}
			}
		}
		// if(width < d3d.width() || height < d3d.height())
		// SystemAction.RestartSystem();
	}
	
	@Override
	public void pause()
	{
		// TODO Auto-generated method stub
		Log.v( "Desktop3D listener" , "pause" );
		root.pause();
		d3d.resetGesture();
	}
	
	public FolderIcon3D getOpenFolder()
	{
		if( root == null )
		{
			return null;
		}
		int count = root.getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			View3D view = root.getChildAt( i );
			if( view instanceof FolderIcon3D )
			{
				return (FolderIcon3D)view;
			}
		}
		return null;
	}
	
	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub
		View3DTweenAccessor.manager.killAll();
		// zhujieping add start
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable )
		{
			if( FolderIcon3D.shader != null )
			{
				FolderIcon3D.shader.dispose();
				FolderIcon3D.shader = null;
			}
		}
		// zhujieping add end
		/****************** added by zhenNan.ye begin *******************/
		if( DefaultLayout.enable_particle )
		{
			ParticleLoader.freeAllParticleEffect();
		}
		/****************** added by zhenNan.ye end *******************/
	}
	
	public boolean setLoadOnResume()
	{
		if( mPaused )
		{
			Log.i( TAG , "setLoadOnResume" );
			mOnResumeNeedsLoad = true;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean paused()
	{
		return mPaused;
	}
	
	@Override
	public int getCurrentWorkspaceScreen()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void startBinding()
	{
		// TODO Auto-generated method stub
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				startBindingTrue();
				// Log.v("bind", "startBindingTrue done");
			}
		} );
	}
	
	public void startBindingTrue()
	{
		// TODO Auto-generated method stub
		Log.d( "launcher" , "startBindingTrue" );
		/* 热插拔T卡的时候，有时候会加载两次，先清除掉hotseatBar上面的图 */
		{
			final HotSeat3D sideBar = root.getHotSeatBar();
			final ViewGroup3D mainGroup = sideBar.getMainGroup();
			mainGroup.removeAllViews();
			final ViewGroup3D dockGroup = sideBar.getDockGroup();
			dockGroup.removeAllViews();
		}
	}
	
	@Override
	public void bindItemsOnThread(
			final ArrayList<ItemInfo> shortcuts ,
			final int start ,
			final int end )
	{
		for( int i = start ; i < end ; i++ )
		{
			final ItemInfo item = shortcuts.get( i );
			replaceItemInfoIcon( item );
			if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
			{
				ShortcutInfo sInfo = (ShortcutInfo)item;
				String name = sInfo.title.toString();
				if( !name.equals( R3D.folder3D_name ) )
				{
					R3D.pack( sInfo );
				}
			}
			else if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT )
			{
				ShortcutInfo sInfo = (ShortcutInfo)item;
				String name = sInfo.title.toString();
				if( !( sInfo.intent != null && sInfo.intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) ) && !name.equals( R3D.folder3D_name ) )
				{
					R3D.pack( sInfo );
				}
			}
			else if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER )
			{
				UserFolderInfo folderInfo = (UserFolderInfo)item;
				String name = folderInfo.title.toString();
				if( !name.equals( R3D.folder3D_name ) )
				{
					R3D.pack( name , FolderIcon3D.titleToTexture( name , Color.WHITE ) );
				}
				ArrayList<ShortcutInfo> children = folderInfo.contents;
				int Count = children.size();
				for( int j = 0 ; j < Count ; j++ )
				{
					ShortcutInfo child = (ShortcutInfo)children.get( j );
					replaceItemInfoIcon( child );
					if( child.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || child.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
					{
						ShortcutInfo sInfo = (ShortcutInfo)child;
						if( child.intent != null && child.intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
						{
							continue;
						}
						else
						{
							R3D.pack( sInfo );
						}
					}
				}
			}
		}
	}
	
	@Override
	public void bindItems(
			final ArrayList<ItemInfo> shortcuts ,
			final int start ,
			final int end )
	{
		// TODO Auto-generated method stub
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				Utils3D.showTimeFromStart( "bindItems:" );
				bindItemsTrue( shortcuts , start , end );
			}
		} );
	}
	
	private void replaceItemInfoIcon(
			ItemInfo item )
	{
		if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
		{
			ShortcutInfo sInfo = (ShortcutInfo)item;
			int findIndex = launcher.equalHotSeatIntent( sInfo.intent );
			if( findIndex != -1 )
			{
				/* 需要从主题中寻找替换的图标 */
				Bitmap findBmp = launcher.findHotSeatBitmap( findIndex );
				if( findBmp != null )
				{
					// teapotXu_20130328 add start: 如果是HotSeat的图标，且已经更换图标，那么增加标识
					sInfo.hotseatDefaultIcon = true;
					// teapotXu_20130328 add end
					sInfo.setIcon( findBmp );
					sInfo.title = launcher.getHotSeatString( findIndex );
					sInfo.usingFallbackIcon = false;
				}
			}
			else
			{
				if( ThemeManager.getInstance().getCurrentThemeDescription().mSystem )
				{
					HotSeat3D hotseat = root.getHotSeatBar();
					hotseat.getDockGroup().replaceIntent( sInfo );
					findIndex = launcher.equalHotSeatIntent( sInfo.intent );
					if( findIndex != -1 )
					{
						/* 需要从主题中寻找替换的图标 */
						Bitmap findBmp = launcher.findHotSeatBitmap( findIndex );
						if( findBmp != null )
						{
							// teapotXu_20130328 add start:
							// 如果是HotSeat的图标，且已经更换图标，那么增加标识
							sInfo.hotseatDefaultIcon = true;
							// teapotXu_20130328 add end
							sInfo.setIcon( findBmp );
							sInfo.title = launcher.getHotSeatString( findIndex );
							sInfo.usingFallbackIcon = false;
						}
					}
				}
			}
		}
	}
	
	public void bindItemsTrue(
			ArrayList<ItemInfo> shortcuts ,
			int start ,
			int end )
	{
		R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
		for( int i = start ; i < end ; i++ )
		{
			final ItemInfo item = shortcuts.get( i );
			mDesktopItems.add( item );
			workspace.bindItem( item );
			// Log.v("bind", "bindItemsTrue done");
		}
		Utils3D.showTimeFromStart( "total time" );
	}
	
	@Override
	public void bindFolders(
			final HashMap<Long , FolderInfo> folders )
	{
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				bindFoldersTrue( folders );
				Log.e( "bind" , "bindFolders done" );
			}
		} );
	}
	
	@Override
	public void bindWidgetView(
			final ArrayList<ShortcutInfo> arrList )
	{
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				for( int i = 0 ; i < arrList.size() ; i++ )
				{
					Log.d( "launcher" , "bind widgetview:" + i );
					// boolean loadByInternal = DefaultLayout
					// .isWidgetLoadByInternal(arrList.get(i));
					// if (loadByInternal) {
					// DefaultLayout
					// .showDefaultWidgetViewLoadByInternal(arrList
					// .get(i));
					// } else {
					// DefaultLayout.showDefaultWidgetView(arrList.get(i));
					// }
					DefaultLayout.showDefaultWidgetView( arrList.get( i ) );
				}
				// Log.v("bind", "bindFolders done");
			}
		} );
	}
	
	public void bindFoldersTrue(
			final HashMap<Long , FolderInfo> folders )
	{
		setLoadOnResume();
		iLoongLauncher.LauncherbindFolders( folders );
	}
	
	public Icon3D addShortcut(
			final ShortcutInfo info )
	{
		// Icon3D icon = new
		// Icon3D(info.title.toString(),info.getIcon(iconCache),info.title.toString());
		if( workspace == null )
		{
			return null;
		}
		final Object obj = workspace.getCurIcon();
		if( obj == null )
			return null;
		final ItemInfo oldInfo = ( (Icon3D)obj ).getItemInfo();
		if( info.title == null )
		{
			new AlertDialog.Builder( iLoongLauncher.getInstance() ).setTitle( R3D.getString( RR.string.group_contacts ) ).setMessage( R3D.getString( RR.string.contact_error_pls_check ) )
					.setPositiveButton( R3D.getString( RR.string.circle_ok_action ) , null ).show();
			return (Icon3D)obj;
		}
		if( oldInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
		{
			// info.container=oldInfo.container;
			info.angle = oldInfo.angle;
		}
		info.screen = oldInfo.screen;
		info.cellX = oldInfo.cellX;
		info.cellY = oldInfo.cellY;
		info.itemType = oldInfo.itemType;
		info.spanX = oldInfo.spanX;
		info.spanY = oldInfo.spanY;
		info.cellTempX = oldInfo.cellX;
		info.cellTempY = oldInfo.cellY;
		final Bitmap bitmapRemain = Bitmap.createBitmap( info.getIcon( iLoongApplication.mIconCache ) );
		final Bitmap bitmapTmp = Bitmap.createBitmap( info.getIcon( iLoongApplication.mIconCache ) );
		( (Icon3D)obj ).setItemInfo( info );
		Root3D.addOrMoveDB( info , oldInfo.container );
		Root3D.deleteFromDB( oldInfo );
		info.setIcon( bitmapRemain );
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				// if (R3D.pack(info, "", true) == true) {
				// R3D.packer.updateTextureAtlas(R3D.packerAtlas,
				// TextureFilter.Linear, TextureFilter.Linear);
				// }
				Bitmap newIcon = bitmapTmp;
				float scaleFactor = 1f;
				if( newIcon.getWidth() != DefaultLayout.app_icon_size || newIcon.getHeight() != DefaultLayout.app_icon_size )
				{
					scaleFactor = (float)DefaultLayout.app_icon_size / newIcon.getWidth();
					if( scaleFactor > 1 )
						scaleFactor = 1;
				}
				if( DefaultLayout.thirdapk_icon_scaleFactor != 1f && !R3D.doNotNeedScale( null , null ) )
				{
					scaleFactor = scaleFactor * DefaultLayout.thirdapk_icon_scaleFactor;
				}
				if( scaleFactor != 1f )
				{
					newIcon = Tools.resizeBitmap( newIcon , scaleFactor );
				}
				Texture texture = new IconToTexture3D( newIcon , info.title.toString() , Icon3D.getIconBg() , Icon3D.titleBg );
				TextureRegion newRegion = new TextureRegion( texture );
				if( !DefConfig.DEF_S3_SUPPORT && newRegion.getRegionHeight() == R3D.workspace_cell_height )
				{
					if( oldInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
					{
						float scale = (float)R3D.workspace_cell_width / (float)R3D.workspace_cell_height;
						Utils3D.changeTextureRegionHeight( newRegion , scale );
					}
				}
				( (View3D)obj ).region = newRegion;
				// if (!bitmapRemain.isRecycled()) bitmapRemain.recycle();
				// tmp.getTexture().dispose();
			}
		} );
		// ((Icon3D) obj).setItemInfo(info);
		// Root3D.addOrMoveDB(info, oldInfo.container);
		// Root3D.deleteFromDB(oldInfo);
		workspace.setTag( null );
		return null;
	}
	
	public void addShortcutFromDrop(
			final ShortcutInfo info )
	{
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
				Icon3D icon = new Icon3D( info.title.toString() , R3D.findRegion( info , info.title.toString() ) );
				icon.setItemInfo( info );
				workspace.setTag( null );
				if( workspace.addInScreen( icon , info.screen , info.x , info.y , false ) )
				{
					// Root3D.addOrMoveDB(info,
					// LauncherSettings.Favorites.CONTAINER_DESKTOP);
				}
			}
		} );
		Gdx.graphics.requestRendering();
		return;
	}
	
	public Widget addAppWidget(
			Widget2DInfo item )
	{
		Widget widget2D = new Widget( "widget_" + item.appWidgetId , item );
		workspace.setTag( null );
		if( workspace.addInScreen( widget2D , item.screen , item.x , item.y , false ) )
			return widget2D;
		return null;
	}
	
	@Override
	public void bindAppWidget(
			final Widget2DInfo item )
	{
		// TODO Auto-generated method stub
		// launcher.postRunnable(new Runnable() {
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		Widget widget = addAppWidget( item );
		if( widget == null )
			return;
		Widget2DInfo info = (Widget2DInfo)widget.getItemInfo();
		iLoongLauncher.getInstance().addAppWidget( info , true );
		info.hostView.setWidget( widget );
		// SendMsgToAndroid.sendAddAppWidgetMsg(addAppWidget(item));
		// // Log.v("bind", "bindAppWidget done");
		// }
		//
		// });
	}
	
	@Override
	public void bindAppsAdded(
			final ArrayList<ApplicationInfo> apps )
	{
		// TODO Auto-generated method stub
		if( !bCreatDone )
		{
			Log.e( "launcher" , "bindAppsAdded but not CreatDone!!!!!!" );
			return;
		}
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				Log.v( "launcher" , "bindAppsAdded" );
				boolean hasNewTexture = false;
				Iterator<ApplicationInfo> ite = apps.iterator();
				while( ite.hasNext() )
				{
					ApplicationInfo info = ite.next();
					// teapotXu_20130305: add start
					// Widget icon will not be shown in Launcher
					if( bAppDone )
					{
						// xiatian add start //fix bug:0001881
						if( HideThemesInAppList( info.packageName ) )
						{
							ite.remove();// xiatian add //fix bug:0001918
							continue;
						}
						// xiatian add end
						if( IsInstalledCooeeWidgets( iLoongLauncher.getInstance() , info.packageName ) )
						{
							ite.remove();
							continue;
						}
					}
					// teapotXu_20130305: add end
					ShortcutInfo sInfo = info.makeShortcut();
					hasNewTexture |= R3D.pack( sInfo );
					// Log.v("icon",sInfo.title +" :" +
					// sInfo.intent.toString());
				}
				R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
				appHost.addApps( apps );
				desktopEdit.addApps( apps );
				root.addApps( apps );// wanghongjian add //enable_DefaultScene
				if( bAppDone )
				{
					for( int i = 0 ; i < apps.size() ; i++ )
					{
						ApplicationInfo info = apps.get( i );
						DefaultLayout.onAddApp( info );
					}
				}
				synchronized( LauncherModel.lock_allapp )
				{
					LauncherModel.waitBindApp = false;
					LauncherModel.lock_allapp.notify();
				}
				// Log.v("bind", "bindAppsAdded done");
			}
		} );
	}
	
	@Override
	public void bindAllWidgets(
			final ArrayList<Object> widgets )
	{
		if( !bCreatDone )
		{
			Log.e( "launcher" , "bindAppsRemoved but not CreatDone!!!!!!" );
			return;
		}
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				synchronized( Desktop3DListener.this )
				{
					final long time = System.currentTimeMillis();
					allWidgetInfo.clear();
					for( int i = 0 ; i < widgets.size() ; i++ )
					{
						WidgetShortcutInfo widgetInfo = new WidgetShortcutInfo();
						Object rawInfo = widgets.get( i );
						Bitmap b = null;
						String name = "";
						String label = "";
						if( rawInfo instanceof AppWidgetProviderInfo )
						{
							AppWidgetProviderInfo info = (AppWidgetProviderInfo)rawInfo;
							Log.v( "jbc" , "sysWidgetName=" + info.label + " packageName=" + info.provider.getPackageName() + " providerName=" + info.provider.getClassName() );
							int[] cellSpans = launcher.getSpanForWidget( info , null );
							/* 在小组件界面，屏蔽spanX或spanY大于4的小组件 */
							if( cellSpans[0] > 4 || cellSpans[1] > 4 )
							{
								continue;
							}
							// if
							// (info.provider.getPackageName().equals("com.google.android.apps.maps"))
							// {
							// if
							// (info.provider.getClassName().equals("com.google.googlenav.appwidget.traffic.TrafficAppWidget"))
							// {
							// continue;
							// }
							// }
							int previewImage = 0;
							int sysVersion = Integer.parseInt( VERSION.SDK );
							if( sysVersion >= 11 )
								previewImage = info.previewImage;
							b = getWidgetPreview( info.provider , previewImage , info.icon , cellSpans[0] , cellSpans[1] , -1 , -1 );
							name = info.provider.toString();
							label = info.label;
							widgetInfo.cellHSpan = cellSpans[0];
							widgetInfo.cellVSpan = cellSpans[1];
							widgetInfo.label = label;
							widgetInfo.component = info.provider;
							widgetInfo.isWidget = true;
						}
						else if( rawInfo instanceof ResolveInfo )
						{
							ResolveInfo info = (ResolveInfo)rawInfo;
							b = getShortcutPreview( info );
							name = info.activityInfo.name;
							label = iconCache.getLabel( info );
							widgetInfo.label = label;
							widgetInfo.component = new ComponentName( info.activityInfo.packageName , info.activityInfo.name );
							widgetInfo.isShortcut = true;
						}
						if( b != null )
						{
							R3D.pack( name , b );
							widgetInfo.textureName = name;
							widgetInfo.isHide = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getBoolean( "HIDE:" + widgetInfo.textureName , false );
							allWidgetInfo.add( widgetInfo );
						}
					}
					launcher.postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
							appHost.setWidgets( allWidgetInfo );
							if( !bWidgetDone )
							{
								bWidgetDone = true;
								checkLoadProgress();
							}
						}
					} );
				}
			}
		} ).start();
	}
	
	public void bindAppListFolders(
			final ArrayList<FolderInfo> folders )
	{
		if( !R3D.hasPack( R3D.contact_name ) )
		{
			Bitmap bmp1 = ThemeManager.getInstance().getBitmap( "theme/iconbg/contactperson-icon.png" );
			Bitmap bmp = Bitmap.createScaledBitmap( bmp1 , R3D.sidebar_widget_w , R3D.sidebar_widget_h , true );
			R3D.pack( R3D.contact_name , Utils3D.IconToPixmap3D( bmp , R3D.contact_name , null , Icon3D.titleBg ) );
			bmp1.recycle();
			bmp.recycle();
		}
		launcher.postRunnable( new Runnable() {
			
			public void run()
			{
				final int N = folders.size();
				for( int i = 0 ; i < N ; i++ )
				{
					FolderInfo info = folders.get( i );
					UserFolderInfo folderInfo = (UserFolderInfo)info;
					String name = folderInfo.title.toString();
					if( !name.equals( R3D.folder3D_name ) )
					{
						R3D.pack( name , FolderIcon3D.titleToTexture( name , Color.WHITE ) );
					}
					ArrayList<ShortcutInfo> children = folderInfo.contents;
					int Count = children.size();
					for( int j = 0 ; j < Count ; j++ )
					{
						ShortcutInfo child = (ShortcutInfo)children.get( j );
						if( child.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || child.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
						{
							ShortcutInfo sInfo = (ShortcutInfo)child;
							if( child.intent != null && child.intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
							{
								continue;
							}
							else
							{
								R3D.pack( sInfo );
							}
						}
					}
				}
				R3D.packer.updateTextureAtlas( R3D.packerAtlas , R3D.filter , R3D.Magfilter );
				appHost.setFolders( folders );
				synchronized( LauncherModel.lock_allapp )
				{
					LauncherModel.waitBindApp = false;
					LauncherModel.lock_allapp.notify();
				}
			}
		} );
	}
	
	public void bindAppListFoldersAdded(
			final ArrayList<FolderInfo> folders )
	{
		if( !R3D.hasPack( R3D.contact_name ) )
		{
			Bitmap bmp1 = ThemeManager.getInstance().getBitmap( "theme/iconbg/contactperson-icon.png" );
			Bitmap bmp = Bitmap.createScaledBitmap( bmp1 , R3D.sidebar_widget_w , R3D.sidebar_widget_h , true );
			R3D.pack( R3D.contact_name , Utils3D.IconToPixmap3D( bmp , R3D.contact_name , null , Icon3D.titleBg ) );
			bmp1.recycle();
			bmp.recycle();
		}
		launcher.postRunnable( new Runnable() {
			
			public void run()
			{
				final int N = folders.size();
				for( int i = 0 ; i < N ; i++ )
				{
					FolderInfo info = folders.get( i );
					UserFolderInfo folderInfo = (UserFolderInfo)info;
					String name = folderInfo.title.toString();
					if( !name.equals( R3D.folder3D_name ) )
					{
						R3D.pack( name , FolderIcon3D.titleToTexture( name , Color.WHITE ) );
					}
					ArrayList<ShortcutInfo> children = folderInfo.contents;
					int Count = children.size();
					for( int j = 0 ; j < Count ; j++ )
					{
						ShortcutInfo child = (ShortcutInfo)children.get( j );
						if( child.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || child.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
						{
							ShortcutInfo sInfo = (ShortcutInfo)child;
							if( child.intent != null && child.intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
							{
								continue;
							}
							else
							{
								R3D.pack( sInfo );
							}
						}
					}
				}
				R3D.packer.updateTextureAtlas( R3D.packerAtlas , R3D.filter , R3D.Magfilter );
				appHost.addFolders( folders );
				synchronized( LauncherModel.lock_allapp )
				{
					LauncherModel.waitBindApp = false;
					LauncherModel.lock_allapp.notify();
				}
			}
		} );
	}
	
	@Override
	public void bindAllApplications(
			final ArrayList<ApplicationInfo> apps )
	{
		//		if( !R3D.hasPack( R3D.contact_name ) )
		//		{
		//			Bitmap bmp1 = ThemeManager.getInstance().getBitmap( "theme/iconbg/contactperson-icon.png" );
		//			Bitmap bmp = Bitmap.createScaledBitmap( bmp1 , R3D.sidebar_widget_w , R3D.sidebar_widget_h , true );
		//			R3D.pack( R3D.contact_name , Utils3D.IconToPixmap3D( bmp , R3D.contact_name , null , Icon3D.titleBg ) );
		//			bmp1.recycle();
		//			bmp.recycle();
		//		}
		final int N = apps.size();
		launcher.postRunnable( new Runnable() {
			
			public void run()
			{
				CellLayout3D.canShowReminder = true;
				for( int j = 0 ; j < N ; j++ )
				{
					ApplicationInfo info = apps.get( j );
					ShortcutInfo sInfo = info.makeShortcut();
					R3D.pack( sInfo );
				}
				R3D.packer.updateTextureAtlas( R3D.packerAtlas , R3D.filter , R3D.Magfilter );
				appHost.setApps( apps );
				synchronized( LauncherModel.lock_allapp )
				{
					LauncherModel.waitBindApp = false;
					LauncherModel.lock_allapp.notify();
				}
			}
		} );
	}
	
	public void finishBindApplications()
	{
		Log.e( "load" , "finish app" );
		Utils3D.showTimeFromStart( "finish app" );
		if( bAppDone )
		{
			Log.e( "load" , "app has done" );
			return;
		}
		bAppDone = true;
		resume();
		checkLoadProgress();
		Root3D.screenUtils.setWallpagerBitmap();
		//Root3D.getSceenDrawable();
	}
	
	private void checkLoadProgress()
	{
		if( initDone() )
		{
			iLoongLauncher.getInstance().finishLoad();
		}
	}
	
	void updateDockbarShortcuts(
			ArrayList<ApplicationInfo> apps )
	{
		IconCache mIconCache = ( (iLoongApplication)launcher.getApplication() ).getIconCache();
		final PackageManager pm = launcher.getPackageManager();
		final ViewGroup3D sideMainGroup = root.getHotSeatBar().getDockGroup();
		final int count = sideMainGroup.getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			View3D mView3D = sideMainGroup.getChildAt( i );
			if( !( mView3D instanceof ViewGroup3D ) )
			{
				continue;
			}
			final ViewGroup3D layout = (ViewGroup3D)mView3D;
			int childCount = layout.getChildCount();
			for( int j = 0 ; j < childCount ; j++ )
			{
				View3D viewtmp = layout.getChildAt( j );
				if( !( viewtmp instanceof IconBase3D ) )
					continue;
				final IconBase3D view = (IconBase3D)viewtmp;
				Object tag = view.getItemInfo();
				if( tag instanceof ShortcutInfo )
				{
					ShortcutInfo info = (ShortcutInfo)tag;
					// We need to check for ACTION_MAIN otherwise getComponent()
					// might
					// return null for some shortcuts (for instance, for
					// shortcuts to
					// web pages.)
					final Intent intent = info.intent;
					final ComponentName name = intent.getComponent();
					if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION && Intent.ACTION_MAIN.equals( intent.getAction() ) && name != null )
					{
						final int appCount = apps.size();
						for( int k = 0 ; k < appCount ; k++ )
						{
							ApplicationInfo app = apps.get( k );
							if( app.componentName.equals( name ) )
							{
								ShortcutInfo sInfo = app.makeShortcut();
								( (View3D)view ).region = new TextureRegion( R3D.findRegion( sInfo ) );
							}
						}
					}
				}
				else if( tag instanceof UserFolderInfo )
				{
					updateFolder( (FolderIcon3D)view , apps );
				}
			}
		}
	}
	
	@Override
	public void bindAppsUpdated(
			final ArrayList<ApplicationInfo> apps )
	{
		if( !bCreatDone )
		{
			Log.e( "launcher" , "bindAppsUpdated but not CreatDone!!!!!!" );
			return;
		}
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				Log.v( "pack" , "bindAppsUpdated" );
				for( int i = 0 ; i < apps.size() ; i++ )
				{
					ApplicationInfo info = apps.get( i );
					ShortcutInfo sInfo = info.makeShortcut();
					R3D.pack( sInfo );
					// Log.v("icon",sInfo.title +" :" +
					// sInfo.intent.toString());
				}
				R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
				appHost.updateApps( apps );
				updateWorkapceShortcuts( apps );
				// updateSidebarShortcuts(apps);
				// updateDockbarShortcuts(apps);
				// updateFolder(getOpenFolder(), apps);
				// Log.v("bind", "bindAppsUpdated done");
			}
		} );
	}
	
	void removeDockbarItems(
			final ArrayList<ApplicationInfo> apps )
	{
		final HotSeat3D sideBar = root.getHotSeatBar();
		final Context context = launcher.getApplicationContext();
		final ViewGroup3D mainGroup = sideBar.getDockGroup();
		final int count = mainGroup.getChildCount();
		final PackageManager manager = context.getPackageManager();
		final AppWidgetManager widgets = AppWidgetManager.getInstance( context );
		final HashSet<String> packageNames = new HashSet<String>();
		final int appCount = apps.size();
		for( int i = 0 ; i < appCount ; i++ )
		{
			packageNames.add( apps.get( i ).componentName.getPackageName() );
		}
		for( int i = 0 ; i < count ; i++ )
		{
			View3D mView3D = mainGroup.getChildAt( i );
			if( !( mView3D instanceof ViewGroup3D ) )
			{
				continue;
			}
			final ViewGroup3D layout = (ViewGroup3D)mView3D;
			// Avoid ANRs by treating each screen separately
			launcher.postRunnable( new Runnable() {
				
				public void run()
				{
					final ArrayList<IconBase3D> childrenToRemove = new ArrayList<IconBase3D>();
					childrenToRemove.clear();
					int childCount = layout.getChildCount();
					for( int j = 0 ; j < childCount ; j++ )
					{
						View3D viewtmp = layout.getChildAt( j );
						if( !( viewtmp instanceof IconBase3D ) )
							continue;
						final IconBase3D view = (IconBase3D)viewtmp;
						Object tag = view.getItemInfo();
						if( tag instanceof ShortcutInfo )
						{
							final ShortcutInfo info = (ShortcutInfo)tag;
							final Intent intent = info.intent;
							final ComponentName name = intent.getComponent();
							if( Intent.ACTION_MAIN.equals( intent.getAction() ) && name != null )
							{
								for( String packageName : packageNames )
								{
									if( packageName.equals( name.getPackageName() ) )
									{
										// LauncherModel.deleteItemFromDatabase(mLauncher,
										// info);
										Root3D.deleteFromDB( info );
										childrenToRemove.add( view );
									}
								}
							}
						}
						else if( tag instanceof UserFolderInfo )
						{
							removeFolderItems( (FolderIcon3D)view , apps );
						}
					}
					childCount = childrenToRemove.size();
					for( int j = 0 ; j < childCount ; j++ )
					{
						View3D child = (View3D)childrenToRemove.get( j );
						child.remove();
						// layout.removeViewInLayout(child);
						if( child instanceof DropTarget3D )
						{
							dragLayer.removeDropTarget( (DropTarget3D)child );
						}
					}
				}
			} );
		}
	}
	
	public void removeDesktopEditItems(
			final ArrayList<ApplicationInfo> apps )
	{
		final Context context = launcher.getApplicationContext();
		final int count = desktopEdit.getCelllayoutList().size();
		final AppWidgetManager widgets = AppWidgetManager.getInstance( context );
		final HashSet<String> packageNames = new HashSet<String>();
		final int appCount = apps.size();
		for( int i = 0 ; i < appCount ; i++ )
		{
			packageNames.add( apps.get( i ).componentName.getPackageName() );
		}
		for( int i = 0 ; i < count ; i++ )
		{
			View3D viewTmp = desktopEdit.getCelllayoutList().get( i );
			if( !( viewTmp instanceof CellLayout3D ) )
				continue;
			final CellLayout3D layout = (CellLayout3D)viewTmp;
			final ArrayList<IconBase3D> childrenToRemove = new ArrayList<IconBase3D>();
			childrenToRemove.clear();
			int childCount = layout.getChildCount();
			for( int j = 0 ; j < childCount ; j++ )
			{
				final IconBase3D view = (IconBase3D)layout.getChildAt( j );
				Object tag = view.getItemInfo();
				if( tag instanceof ShortcutInfo )
				{
					final ShortcutInfo info = (ShortcutInfo)tag;
					final Intent intent = info.intent;
					final ComponentName name = intent.getComponent();
					if( Intent.ACTION_MAIN.equals( intent.getAction() ) && name != null )
					{
						for( String packageName : packageNames )
						{
							if( packageName.equals( name.getPackageName() ) )
							{
								// LauncherModel.deleteItemFromDatabase(mLauncher,
								// info);
								Root3D.deleteFromDB( info );
								childrenToRemove.add( view );
							}
						}
					}
				}
				else if( tag instanceof UserFolderInfo )
				{
					removeFolderItems( (FolderIcon3D)view , apps );
				}
				else if( tag instanceof Widget2DInfo )
				{
					final Widget2DInfo info = (Widget2DInfo)tag;
					for( String packageName : packageNames )
					{
						if( packageName.equals( info.getPackageName() ) )
						{
							Root3D.deleteFromDB( info );
							// LauncherModel.deleteItemFromDatabase(mLauncher,
							// info);
							childrenToRemove.add( view );
							SendMsgToAndroid.deleteSysWidget( (Widget)view );
						}
					}
				}
			}
			childCount = childrenToRemove.size();
			for( int j = 0 ; j < childCount ; j++ )
			{
				View3D child = (View3D)childrenToRemove.get( j );
				child.remove();
				// layout.removeViewInLayout(child);
				if( child instanceof DropTarget3D )
				{
					dragLayer.removeDropTarget( (DropTarget3D)child );
				}
			}
		}
	}
	
	@Override
	public void bindAppsRemoved(
			final ArrayList<ApplicationInfo> apps ,
			final boolean permanent )
	{
		if( !bCreatDone )
		{
			Log.e( "launcher" , "bindAppsRemoved but not CreatDone!!!!!!" );
			return;
		}
		Log.v( "launcher" , "bindAppsRemoved" );
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				if( permanent )
				{
					if( !root.getDesktopEdit().isVisible() )
					{
						removeWorkspaceItems( apps );
					}
					else
					{
						removeDesktopEditItems( apps );
					}
					removeSidebarItems( apps );
					removeDockbarItems( apps );
					removeFolderItems( getOpenFolder() , apps );
				}
				// ApplistVirtualIconManager.onAppsRemoved(apps);
				appHost.reomveApps( apps , permanent );
				// wanghongjian add start //enable_DefaultScene
				for( int i = 0 ; i < apps.size() ; i++ )
				{
					root.removeDBIntents( apps.get( i ) );
				}
				// wanghongjian add end
				Log.v( "bind" , "bindAppsRemoved done" );
			}
		} );
	}
	
	@Override
	public boolean isAllAppsVisible()
	{
		if( appHost == null )
			return false;
		return appHost.isVisible();
	}
	
	@Override
	public void bindWidget3D(
			final Widget3DInfo item )
	{
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				Utils3D.showTimeFromStart( "load 3dwidget 1:" + item.packageName );
				Widget3D widget3D = null;
				boolean find = false;
				WidgetItem widgetItem = null;
				for( int i = 0 ; i < DefaultLayout.allWidgetFinal.size() ; i++ )
				{
					if( DefaultLayout.allWidgetFinal.get( i ).pkgName.equals( item.packageName ) )
					{
						widgetItem = DefaultLayout.allWidgetFinal.get( i );
						find = true;
						break;
					}
				}
				// 如果安装了启用安装的Widget3D启动if (loadByInternal &&
				// !widgetItem.hasInstall) {//
				if( find && widgetItem.loadByInternal )
				{
					Utils3D.showTimeFromStart( "load 3dwidget 2:" + item.packageName );
					widget3D = Widget3DManager.getInstance().getWidget3D( widgetItem.pkgName , widgetItem.className );
					if( widget3D != null )
					{
						widget3D.setItemInfo( item );
					}
					// workspace.addInScreen(widget3D, item.screen, item.x,
					// item.y, false);
				}
				else
				{
					widget3D = mWidget3DManagerInstance.getWidget3D( item );
				}
				Utils3D.showTimeFromStart( "load 3dwidget 3:" + item.packageName );
				// Log.e("bindWidget3D", "widgetId:" + widget3D.getWidgetID()
				// + " x:" + widget3D.x + " y:" + widget3D.y + " width:"
				// + widget3D.width + " height:" + widget3D.height);
				if( widget3D != null )
				{
					workspace.addInScreen( widget3D , item.screen , item.x , item.y , false );
				}
				Utils3D.showTimeFromStart( "load 3dwidget 4:" + item.packageName );
				// Log.v("bind", "bindWidget3D done");
			}
		} );
	}
	
	public void addWidget3DToScreen(
			View3D widget3D ,
			int x ,
			int y )
	{
		CellLayout3D cellLayout = workspace.getCurrentCellLayout();
		cellLayout.setScreen( workspace.getCurrentScreen() );
		this.workspace.addInCurrenScreen( widget3D , x , y );
	}
	
	@Override
	public void bindSidebarItems(
			final ArrayList<ItemInfo> info )
	{
		// Utils3D.showTimeFromStart("bindSidebarItems 1");
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				// Utils3D.showTimeFromStart("bindSidebarItems 2");
				int count = info.size();
				for( int i = 0 ; i < count ; i++ )
				{
					// Utils3D.showTimeFromStart("bindSidebarItems 3");
					final ItemInfo item = info.get( i );
					replaceItemInfoIcon( item );
					if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
					{
						ShortcutInfo sInfo = (ShortcutInfo)item;
						String name = sInfo.title.toString();
						if( !name.equals( R3D.folder3D_name ) )
						{
							if( R3D.icon_bg_num > 0 )
							{
								if( DefaultLayout.app_icon_size > ( sInfo.getIcon( iLoongApplication.mIconCache ).getHeight() ) )
								{
									if( sInfo.intent != null && sInfo.intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
									{
										continue;
									}
									else
									{
										R3D.packHotseat( sInfo , true );
									}
								}
								else
								{
									if( sInfo.intent != null && sInfo.intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
									{
										continue;
									}
									else
									{
										R3D.packHotseat( sInfo , false );
									}
								}
							}
							else
							{
								if( sInfo.intent != null && sInfo.intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
								{
									continue;
								}
								else
								{
									R3D.packHotseat( sInfo , false );
								}
							}
						}
					}
					if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER )
					{
						UserFolderInfo folderInfo = (UserFolderInfo)item;
						String name = folderInfo.title.toString();
						if( !name.equals( R3D.folder3D_name ) )
						{
							R3D.pack( name , FolderIcon3D.titleToTexture( name , Color.WHITE ) );
						}
						ArrayList<ShortcutInfo> children = folderInfo.contents;
						int Count = children.size();
						for( int j = 0 ; j < Count ; j++ )
						{
							ItemInfo child = (ItemInfo)children.get( j );
							replaceItemInfoIcon( child );
							if( child.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || child.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
							{
								ShortcutInfo sInfo = (ShortcutInfo)child;
								R3D.pack( sInfo );
								// R3D.pack(sInfo, Utils3D.IconToPixmap3D(
								// sInfo.getIcon(iconCache),
								// sInfo.title.toString(),
								// Icon3D.getIconBg(),
								// Icon3D.titleBg));
								Log.v( "icon" , sInfo.title + " :" + sInfo.intent.toString() );
							}
						}
					}
				}
				// Utils3D.showTimeFromStart("bindSidebarItems 4");
				// TODO Auto-generated method stub
				R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
				// Utils3D.showTimeFromStart("bindSidebarItems 5");
				HotSeat3D sidebar = root.getHotSeatBar();
				sidebar.bindItems( info );
				// Utils3D.showTimeFromStart("bindSidebarItems 6");
				// Log.v("bind", "bindSidebarItems done");
			}
		} );
	}
	
	@Override
	public void bindShortcutItems(
			final ArrayList<ItemInfo> info )
	{
		// Utils3D.showTimeFromStart("bindSidebarItems 1");
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				// Utils3D.showTimeFromStart("bindSidebarItems 2");
				int count = info.size();
				for( int i = 0 ; i < count ; i++ )
				{
					// Utils3D.showTimeFromStart("bindSidebarItems 3");
					final ItemInfo item = info.get( i );
					replaceItemInfoIcon( item );
					if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
					{
						ShortcutInfo sInfo = (ShortcutInfo)item;
						String name = sInfo.title.toString();
						if( !name.equals( R3D.folder3D_name ) )
						{
							if( R3D.icon_bg_num > 0 )
							{
								if( DefaultLayout.app_icon_size > ( sInfo.getIcon( iLoongApplication.mIconCache ).getHeight() ) )
								{
									if( sInfo.intent != null && sInfo.intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
									{
										continue;
									}
									else
									{
										R3D.packHotseat( sInfo , true );
									}
								}
								else
								{
									if( sInfo.intent != null && sInfo.intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
									{
										continue;
									}
									else
									{
										R3D.packHotseat( sInfo , false );
									}
								}
							}
							else
							{
								if( sInfo.intent != null && sInfo.intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
								{
									continue;
								}
								else
								{
									R3D.packHotseat( sInfo , false );
								}
							}
						}
					}
					if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER )
					{
						UserFolderInfo folderInfo = (UserFolderInfo)item;
						String name = folderInfo.title.toString();
						if( !name.equals( R3D.folder3D_name ) )
						{
							R3D.pack( name , FolderIcon3D.titleToTexture( name , Color.WHITE ) );
						}
						ArrayList<ShortcutInfo> children = folderInfo.contents;
						int Count = children.size();
						for( int j = 0 ; j < Count ; j++ )
						{
							ItemInfo child = (ItemInfo)children.get( j );
							replaceItemInfoIcon( child );
							if( child.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || child.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
							{
								ShortcutInfo sInfo = (ShortcutInfo)child;
								R3D.pack( sInfo );
								// R3D.pack(sInfo, Utils3D.IconToPixmap3D(
								// sInfo.getIcon(iconCache),
								// sInfo.title.toString(),
								// Icon3D.getIconBg(),
								// Icon3D.titleBg));
								Log.v( "icon" , sInfo.title + " :" + sInfo.intent.toString() );
							}
						}
					}
				}
				// Utils3D.showTimeFromStart("bindSidebarItems 4");
				// TODO Auto-generated method stub
				R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
				// Utils3D.showTimeFromStart("bindSidebarItems 5");
				HotSeat3D sidebar = root.getHotSeatBar();
				sidebar.bindShortItems( info );
				// Utils3D.showTimeFromStart("bindSidebarItems 6");
				// Log.v("bind", "bindSidebarItems done");
			}
		} );
	}
	
	@Override
	public void afterBindSidebarItems()
	{
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				synchronized( this )
				{
					if( hasCreate )
					{
						clearWorkspace();
					}
					else
					{
						hasCreate = true;
						create2();
						clearWorkspace();
					}
					setLoadOnResume();
					if( LauncherModel.waitD3dInit )
					{
						LauncherModel.waitD3dInit = false;
						synchronized( init_lock )
						{
							init_lock.notify();
						}
					}
				}
			}
		} );
	}
	
	public void clearWorkspace()
	{
		int count = workspace.getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			View3D view = workspace.getChildAt( i );
			if( view instanceof CellLayout3D )
			{
				CellLayout3D cellLayout = (CellLayout3D)view;
				if( cellLayout.getChildCount() > 0 )
				{
					Log.d( "launcher" , "break" );
				}
				for( int j = 0 ; j < cellLayout.getChildCount() ; j++ )
				{
					View3D child = cellLayout.getChildAt( j );
					if( child instanceof Widget3D )
					{
						Widget3D widget3D = (Widget3D)child;
						Widget3DManager.getInstance().deleteWidget3D( widget3D );
					}
					if( child instanceof DropTarget3D && dragLayer != null )
					{
						dragLayer.removeDropTarget( (DropTarget3D)child );
					}
				}
				cellLayout.removeAllViews();
			}
		}
	}
	
	public void removeWorkspaceItems(
			final ArrayList<ApplicationInfo> apps )
	{
		final Context context = launcher.getApplicationContext();
		final int count = workspace.getChildCount();
		final AppWidgetManager widgets = AppWidgetManager.getInstance( context );
		final HashSet<String> packageNames = new HashSet<String>();
		final int appCount = apps.size();
		for( int i = 0 ; i < appCount ; i++ )
		{
			packageNames.add( apps.get( i ).componentName.getPackageName() );
		}
		for( int i = 0 ; i < count ; i++ )
		{
			View3D viewTmp = workspace.getChildAt( i );
			if( !( viewTmp instanceof CellLayout3D ) )
				continue;
			final CellLayout3D layout = (CellLayout3D)viewTmp;
			// Avoid ANRs by treating each screen separately
			// launcher.postRunnable(new Runnable() {
			// public void run() {
			final ArrayList<IconBase3D> childrenToRemove = new ArrayList<IconBase3D>();
			childrenToRemove.clear();
			Log.v( "launcher" , "exe remove app" );
			int childCount = layout.getChildCount();
			for( int j = 0 ; j < childCount ; j++ )
			{
				final IconBase3D view = (IconBase3D)layout.getChildAt( j );
				Object tag = view.getItemInfo();
				if( tag instanceof ShortcutInfo )
				{
					final ShortcutInfo info = (ShortcutInfo)tag;
					final Intent intent = info.intent;
					if( intent == null )
					{
						continue;
					}
					final ComponentName name = intent.getComponent();
					if( Intent.ACTION_MAIN.equals( intent.getAction() ) && name != null )
					{
						for( String packageName : packageNames )
						{
							if( packageName.equals( name.getPackageName() ) )
							{
								// LauncherModel.deleteItemFromDatabase(mLauncher,
								// info);
								Root3D.deleteFromDB( info );
								childrenToRemove.add( view );
							}
						}
					}
				}
				else if( tag instanceof UserFolderInfo )
				{
					removeFolderItems( (FolderIcon3D)view , apps );
				}
				else if( tag instanceof Widget2DInfo )
				{
					final Widget2DInfo info = (Widget2DInfo)tag;
					for( String packageName : packageNames )
					{
						if( packageName.equals( info.getPackageName() ) )
						{
							Root3D.deleteFromDB( info );
							// LauncherModel.deleteItemFromDatabase(mLauncher,
							// info);
							childrenToRemove.add( view );
							SendMsgToAndroid.deleteSysWidget( (Widget)view );
						}
					}
				}
			}
			childCount = childrenToRemove.size();
			for( int j = 0 ; j < childCount ; j++ )
			{
				View3D child = (View3D)childrenToRemove.get( j );
				child.remove();
				// layout.removeViewInLayout(child);
				if( child instanceof DropTarget3D )
				{
					dragLayer.removeDropTarget( (DropTarget3D)child );
				}
			}
			// if (childCount > 0) {
			// layout.requestLayout();
			// layout.invalidate();
			// }
		}
		// });
		// }
	}
	
	void updateWorkapceShortcuts(
			ArrayList<ApplicationInfo> apps )
	{
		IconCache mIconCache = ( (iLoongApplication)launcher.getApplication() ).getIconCache();
		final PackageManager pm = launcher.getPackageManager();
		final int count = workspace.getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			View3D viewTmp = workspace.getChildAt( i );
			if( !( viewTmp instanceof CellLayout3D ) )
				continue;
			final CellLayout3D layout = (CellLayout3D)viewTmp;
			int childCount = layout.getChildCount();
			for( int j = 0 ; j < childCount ; j++ )
			{
				final IconBase3D view = (IconBase3D)layout.getChildAt( j );
				Object tag = view.getItemInfo();
				if( tag instanceof ShortcutInfo )
				{
					ShortcutInfo info = (ShortcutInfo)tag;
					// We need to check for ACTION_MAIN otherwise getComponent()
					// might
					// return null for some shortcuts (for instance, for
					// shortcuts to
					// web pages.)
					final Intent intent = info.intent;
					ComponentName name = null;
					if( intent != null )
						name = intent.getComponent();
					if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION && Intent.ACTION_MAIN.equals( intent.getAction() ) && name != null )
					{
						final int appCount = apps.size();
						for( int k = 0 ; k < appCount ; k++ )
						{
							ApplicationInfo app = apps.get( k );
							if( app.componentName.equals( name ) )
							{
								Log.v( "update" , " workspace:" + app.title.toString() );
								ShortcutInfo sInfo = app.makeShortcut();
								( (View3D)view ).region = R3D.findRegion( sInfo );
								//
							}
						}
					}
				}
				else if( tag instanceof UserFolderInfo )
				{
					updateFolder( (FolderIcon3D)view , apps );
				}
			}
		}
	}
	
	private void updateFolder(
			FolderIcon3D folder ,
			ArrayList<ApplicationInfo> apps )
	{
		// 这里有报空指针错误！！！！！
		if( folder == null )
			return;
		final FolderIcon3D myFolder = folder;
		final UserFolderInfo info = (UserFolderInfo)myFolder.getItemInfo();
		final ArrayList<ShortcutInfo> contents = info.contents;
		final ArrayList<ShortcutInfo> toUpdate = new ArrayList<ShortcutInfo>( 1 );
		final int contentsCount = contents.size();
		final int appCount = apps.size();
		for( int m = 0 ; m < appCount ; m++ )
		{
			ApplicationInfo app = apps.get( m );
			for( int k = 0 ; k < contentsCount ; k++ )
			{
				final ShortcutInfo appInfo = contents.get( k );
				final Intent intent = appInfo.intent;
				final ComponentName name = intent.getComponent();
				if( app.componentName.equals( name ) )
				{
					Log.v( "update" , " folder item:" + name.toString() );
					// ShortcutInfo sInfo = app.makeShortcut();
					// ((View3D) view).region = R3D.findRegion(sInfo);
				}
			}
		}
	}
	
	void removeFolderItems(
			FolderIcon3D folder ,
			ArrayList<ApplicationInfo> apps )
	{
		if( folder == null )
			return;
		Log.v( "update" , " folder:" + folder.getItemInfo() );
		final HashSet<String> packageNames = new HashSet<String>();
		final int appCount = apps.size();
		for( int i = 0 ; i < appCount ; i++ )
		{
			packageNames.add( apps.get( i ).componentName.getPackageName() );
		}
		final UserFolderInfo info = (UserFolderInfo)folder.getItemInfo();
		final ArrayList<ShortcutInfo> contents = info.contents;
		final ArrayList<ShortcutInfo> toRemove = new ArrayList<ShortcutInfo>( 1 );
		final int contentsCount = contents.size();
		boolean removedFromFolder = false;
		for( int k = contentsCount - 1 ; k >= 0 ; k-- )
		{
			final ShortcutInfo appInfo = contents.get( k );
			final Intent intent = appInfo.intent;
			final ComponentName name = intent.getComponent();
			if( Intent.ACTION_MAIN.equals( intent.getAction() ) && name != null )
			{
				for( String packageName : packageNames )
				{
					if( packageName.equals( name.getPackageName() ) )
					{
						toRemove.add( appInfo );
						Root3D.deleteFromDB( appInfo );
						info.remove( appInfo );
						removedFromFolder = true;
					}
				}
			}
		}
	}
	
	void removeSidebarItems(
			final ArrayList<ApplicationInfo> apps )
	{
		final HotSeat3D sideBar = root.getHotSeatBar();
		final Context context = launcher.getApplicationContext();
		final ViewGroup3D mainGroup = sideBar.getMainGroup();
		final int count = mainGroup.getChildCount();
		final PackageManager manager = context.getPackageManager();
		final AppWidgetManager widgets = AppWidgetManager.getInstance( context );
		final HashSet<String> packageNames = new HashSet<String>();
		final int appCount = apps.size();
		for( int i = 0 ; i < appCount ; i++ )
		{
			packageNames.add( apps.get( i ).componentName.getPackageName() );
		}
		for( int i = 0 ; i < count ; i++ )
		{
			final ViewGroup3D layout = (ViewGroup3D)mainGroup.getChildAt( i );
			// Avoid ANRs by treating each screen separately
			launcher.postRunnable( new Runnable() {
				
				public void run()
				{
					final ArrayList<IconBase3D> childrenToRemove = new ArrayList<IconBase3D>();
					childrenToRemove.clear();
					int childCount = layout.getChildCount();
					for( int j = 0 ; j < childCount ; j++ )
					{
						View3D viewtmp = layout.getChildAt( j );
						if( !( viewtmp instanceof IconBase3D ) )
							continue;
						final IconBase3D view = (IconBase3D)viewtmp;
						Object tag = view.getItemInfo();
						if( tag instanceof ShortcutInfo )
						{
							final ShortcutInfo info = (ShortcutInfo)tag;
							final Intent intent = info.intent;
							final ComponentName name = intent.getComponent();
							if( Intent.ACTION_MAIN.equals( intent.getAction() ) && name != null )
							{
								for( String packageName : packageNames )
								{
									if( packageName.equals( name.getPackageName() ) )
									{
										// LauncherModel.deleteItemFromDatabase(mLauncher,
										// info);
										Root3D.deleteFromDB( info );
										childrenToRemove.add( view );
									}
								}
							}
							// } else if (tag instanceof UserFolderInfo) {
							// final UserFolderInfo info = (UserFolderInfo) tag;
							// final ArrayList<ShortcutInfo> contents =
							// info.contents;
							// final ArrayList<ShortcutInfo> toRemove = new
							// ArrayList<ShortcutInfo>(1);
							// final int contentsCount = contents.size();
							// boolean removedFromFolder = false;
							//
							// for (int k = 0; k < contentsCount; k++) {
							// final ShortcutInfo appInfo = contents.get(k);
							// final Intent intent = appInfo.intent;
							// final ComponentName name = intent.getComponent();
							//
							// if (Intent.ACTION_MAIN.equals(intent.getAction())
							// && name != null) {
							// for (String packageName: packageNames) {
							// if (packageName.equals(name.getPackageName())) {
							// toRemove.add(appInfo);
							// LauncherModel.deleteItemFromDatabase(mLauncher,
							// appInfo);
							// removedFromFolder = true;
							// }
							// }
							// }
							// }
							//
							// contents.removeAll(toRemove);
							// if (removedFromFolder) {
							// final Folder folder = getOpenFolder();
							// if (folder != null)
							// folder.notifyDataSetChanged();
							// }
							// } else if (tag instanceof LiveFolderInfo) {
							// final LiveFolderInfo info = (LiveFolderInfo) tag;
							// final Uri uri = info.uri;
							// final ProviderInfo providerInfo =
							// manager.resolveContentProvider(
							// uri.getAuthority(), 0);
							//
							// if (providerInfo != null) {
							// for (String packageName: packageNames) {
							// if (packageName.equals(providerInfo.packageName))
							// {
							// LauncherModel.deleteItemFromDatabase(mLauncher,
							// info);
							// childrenToRemove.add(view);
							// }
							// }
							// }
						}
						else if( tag instanceof UserFolderInfo )
						{
							removeFolderItems( (FolderIcon3D)view , apps );
						}
						else if( tag instanceof Widget2DInfo )
						{
							final Widget2DInfo info = (Widget2DInfo)tag;
							final AppWidgetProviderInfo provider = widgets.getAppWidgetInfo( info.appWidgetId );
							if( provider != null )
							{
								for( String packageName : packageNames )
								{
									if( packageName.equals( provider.provider.getPackageName() ) )
									{
										Root3D.deleteFromDB( info );
										// LauncherModel.deleteItemFromDatabase(mLauncher,
										// info);
										childrenToRemove.add( view );
									}
								}
							}
						}
					}
					childCount = childrenToRemove.size();
					for( int j = 0 ; j < childCount ; j++ )
					{
						View3D child = (View3D)childrenToRemove.get( j );
						child.remove();
						// layout.removeViewInLayout(child);
						if( child instanceof DropTarget3D )
						{
							dragLayer.removeDropTarget( (DropTarget3D)child );
						}
					}
				}
			} );
		}
	}
	
	// void updateSidebarShortcuts(ArrayList<ApplicationInfo> apps) {
	//
	// IconCache mIconCache = ((iLoongApplication) launcher.getApplication())
	// .getIconCache();
	// final PackageManager pm = launcher.getPackageManager();
	// final ViewGroup3D sideMainGroup = root.getHotSeatBar().getMainGroup();
	// final int count = sideMainGroup.getChildCount();
	// for (int i = 0; i < count; i++) {
	// final ViewGroup3D layout = (ViewGroup3D) sideMainGroup
	// .getChildAt(i);
	// int childCount = layout.getChildCount();
	// for (int j = 0; j < childCount; j++) {
	// View3D viewtmp = layout.getChildAt(j);
	// if (!(viewtmp instanceof IconBase3D))
	// continue;
	// final IconBase3D view = (IconBase3D) viewtmp;
	// Object tag = view.getItemInfo();
	// if (tag instanceof ShortcutInfo) {
	// ShortcutInfo info = (ShortcutInfo) tag;
	// // We need to check for ACTION_MAIN otherwise getComponent()
	// // might
	// // return null for some shortcuts (for instance, for
	// // shortcuts to
	// // web pages.)
	// final Intent intent = info.intent;
	// final ComponentName name = intent.getComponent();
	// if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION
	// && Intent.ACTION_MAIN.equals(intent.getAction())
	// && name != null) {
	// final int appCount = apps.size();
	// for (int k = 0; k < appCount; k++) {
	// ApplicationInfo app = apps.get(k);
	// if (app.componentName.equals(name)) {
	// ShortcutInfo sInfo = app.makeShortcut();
	// ((View3D) view).region = new TextureRegion(
	// R3D.findRegion(sInfo));
	// // Texture3D t3d = new IconToTexture3D(
	// // sInfo.getIcon(mIconCache),
	// // app.title.toString(), Icon3D.iconBg,
	// // Icon3D.titleBg);
	// // TextureRegion tr = new TextureRegion(t3d);
	// // TextureRegion tmp = ((View3D) view).region;
	// // ((View3D) view).region = tr;
	// // tmp.getTexture().dispose();
	//
	// // ((View3D)view).setTexture(new
	// // Texture3D(Utils3D.bmp2Pixmap(info.getIcon(mIconCache))));
	// // ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(null,
	// // new
	// // FastBitmapDrawable(info.getIcon(mIconCache)),
	// // null, null);
	// }
	// }
	// }
	// } else if (tag instanceof UserFolderInfo) {
	//
	// updateFolder((FolderIcon3D) view, apps);
	// }
	// }
	// }
	// }
	public int getCurrentScreen()
	{
		if( workspace == null )
			return -1;
		return workspace.getCurrentScreen();
	}
	
	public int getScreenCount()
	{
		return workspace.getPageNum();
	}
	
	public CellLayout3D getCurrentCellLayout()
	{
		if( workspace == null )
			return null;
		if( workspace.getChildCount() == 1 )
		{
			return null;
		}
		else
		{
			return workspace.getCurrentCellLayout();
		}
	}
	
	public void exeDeletePage()
	{
		desktopEdit.exeDeletePage();
	}
	
	public void cancelExeDeletePage()
	{
		desktopEdit.cancelExeDeletePage();
	}
	
	public boolean isWorkspaceVisible()
	{
		if( workspace == null )
			return true;
		return workspace.visible;
	}
	
	public void showAllApp()
	{
		//		if (DefaultLayout.mainmenu_inout_no_anim) {
		//			root.showAllAppFromWorkspaceEx();
		//		} else {
		//			root.showAllAppFromWorkspace();
		//		}
	}
	
	public void addPageScrollListener(
			PageScrollListener scrollListener )
	{
		workspace.addScrollListener( scrollListener );
		scrollListener.setCurrentPage( workspace.getCurrentPage() );
		bSetHomepageDone = true;
	}
	
	// public void moveAppWidget(View3D view, int screen, int x, int y) {
	// ArrayList<View3D> list = new ArrayList<View3D>();
	// list.add(view);
	// workspace.dropScreen = screen;
	// workspace.onDrop(list, x, y);
	// }
	public void onHomeKey(
			boolean alreadyOnHome )
	{
		if( !bCreatDone )
			return;
		root.onHomeKey( alreadyOnHome );
	}
	
	@Override
	public void bindWidget3DAdded(
			String packageName )
	{
		// TODO Auto-generated method stub
		Log.v( "launcher" , "bindWidget3DAdded:" + packageName );
		if( !bCreatDone )
		{
			Log.e( "launcher" , "bindWidget3DAdded but not CreatDone!!!!!!" );
			Widget3DManager.getInstance().updateWidget3DInfo();
			return;
		}
		Intent intent = new Intent( iLoongLauncher.EXTRA_SEARCH_WIDGET , null );
		PackageManager pm = iLoongApplication.ctx.getPackageManager();
		List<ResolveInfo> infoList = pm.queryIntentActivities( intent , PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA );
		ResolveInfo resolveInfo = null;
		for( ResolveInfo resolve : infoList )
		{
			if( resolve.activityInfo.packageName.equals( packageName ) )
			{
				resolveInfo = resolve;
				break;
			}
		}
		if( resolveInfo != null )
		{
			Widget3DManager.getInstance().installWidget( resolveInfo );
			addWidget3DInUpdated( resolveInfo ); // xiatian add //fix
													// bug:bindWidget3DUpdated lead
													// to lose Widget3DView in
													// Workspace3D
			if( !DefaultLayout.isWidgetLoadByInternal( resolveInfo.activityInfo.packageName ) )
			{
				Widget3DShortcut shortcut = new Widget3DShortcut( "Widget3DShortcut" , resolveInfo );
				AppHost3D appHost = (AppHost3D)root.getAppHost();
				appHost.addWidget( shortcut );
				root.addWidget();
			}
			// } else {
			// SidebarMainGroup mainGroup = (SidebarMainGroup) root
			// .getSidebar().getMainGroup();
			// mainGroup.bindWidget3D(shortcut);
			// }
		}
	}
	
	@Override
	public void bindWidget3DUpdated(
			String packageName )
	{
		// TODO Auto-generated method stub
		Log.v( "bindWidget3DUpdated" , packageName );
		// Widget3DManager.getInstance().unInstallWidget(packageName);
		mIsWidget3DUpdated = true; // xiatian add //fix bug:bindWidget3DUpdated
									// lead to lose Widget3DView in Workspace3D
		bindWidget3DRemoved( packageName );
		bindWidget3DAdded( packageName );
		mIsWidget3DUpdated = false; // xiatian add //fix bug:bindWidget3DUpdated
									// lead to lose Widget3DView in Workspace3D
	}
	
	@Override
	public void bindWidget3DRemoved(
			final String packageName )
	{
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		Log.v( "bindWidget3DRemoved" , packageName );
		if( DefaultLayout.isWidgetLoadByInternal( packageName ) )
		{
			return;
		}
		if( !bCreatDone )
		{
			Log.e( "launcher" , "bindWidget3DRemoved but not CreatDone!!!!!!" );
			Widget3DManager.getInstance().updateWidget3DInfo();
			return;
		}
		// 取得Widget3DHost中将要卸载的程序?
		ResolveInfo resolveInfo = Widget3DManager.getInstance().getResolveInfo( packageName );
		// 处理manager集合中保存的数据
		Widget3DManager.getInstance().unInstallWidget( packageName );
		// 清理sidebar上面的图?
		AppHost3D appHost = (AppHost3D)root.getAppHost();
		appHost.removeWidget( packageName );
		root.addWidget();
		// } else {
		// SidebarMainGroup mainGroup = (SidebarMainGroup) root.getSidebar()
		// .getMainGroup();
		// mainGroup.unBindWidget3D(packageName);
		// }
		// 清理桌面以及数据库中保存的图?
		final int count = workspace.getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			View3D viewTmp = workspace.getChildAt( i );
			if( !( viewTmp instanceof CellLayout3D ) )
				continue;
			final CellLayout3D layout = (CellLayout3D)viewTmp;
			// Avoid ANRs by treating each screen separately
			// launcher.postRunnable(new Runnable() {
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			try
			{
				final ArrayList<IconBase3D> childrenToRemove = new ArrayList<IconBase3D>();
				childrenToRemove.clear();
				int childCount = layout.getChildCount();
				for( int j = 0 ; j < childCount ; j++ )
				{
					final IconBase3D view = (IconBase3D)layout.getChildAt( j );
					Object tag = view.getItemInfo();
					if( tag instanceof Widget3DInfo )
					{
						final Widget3DInfo info = (Widget3DInfo)tag;
						if( info.packageName.equals( packageName ) )
						{
							// xiatian add start //fix bug:bindWidget3DUpdated
							// lead to lose Widget3DView in Workspace3D
							if( mIsWidget3DUpdated )
							{
								mUpdatedWidget3DInfo = info;
							}
							// xiatian add end
							Root3D.deleteFromDB( info );
							childrenToRemove.add( view );
						}
					}
				}
				childCount = childrenToRemove.size();
				for( int j = 0 ; j < childCount ; j++ )
				{
					View3D child = (View3D)childrenToRemove.get( j );
					if( child instanceof Widget3D )
					{
						Widget3D widget3D = (Widget3D)child;
						widget3D.onUninstall();
					}
					child.remove();
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
			// }
			// });
		}
		try
		{
			if( resolveInfo != null )
			{
				int flag = ( resolveInfo.activityInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_EXTERNAL_STORAGE );
				// 可移动的应用
				if( flag != 0 )
				{
					// 安装在SD?
					Log.v( "bindWidget3DRemoved" , "   " + resolveInfo.activityInfo.packageName + " 需要重启Launcher" );
					// 重启launcher
					launcher.mMainHandler.post( new Runnable() {
						
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.setClass( launcher , ResestActivity.class );
							launcher.startActivity( intent );
						}
					} );
				}
				else
				{
					// 安装在手机内
					Log.e( "***********" , resolveInfo.activityInfo.packageName + " 不需要重启Launcher  " + flag );
				}
			}
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// PackageManager pm = iLoongApplication.ctx.getPackageManager();
		// PackageInfo packageinfo;
		// try {
		// packageinfo = pm.getPackageInfo(packageName, 0);
		// Field field = packageinfo.getClass().getField("installLocation");
		// int i = 0;
		// i = (Integer) field.get(packageinfo);
		// int flag = (packageinfo.applicationInfo.flags &
		// android.content.pm.ApplicationInfo.FLAG_EXTERNAL_STORAGE);
		// // 可移动的应用
		// if (i == 0) {
		// if (flag != 0) {
		// // 安装在SD?
		// Log.e("***********", i + "   " + flag);
		//
		// } else {
		// // 安装在手机内?
		// Log.e("***********", i + "   " + flag);
		// }
		// } else {
		// // 不可移动的应?
		// Log.e("***********", i + "   " + flag);
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// //重启launcher
		// launcher.mMainHandler.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent();
		// intent.setClass(launcher, ResestActivity.class);
		// launcher.startActivity(intent);
		// }
		//
		// });
	}
	
	private Bitmap getShortcutPreview(
			ResolveInfo info )
	{
		// Render the background
		int offset = 0;
		int bitmapSizeW = R3D.workspace_cell_width;
		int bitmapSizeH = R3D.workspace_cell_height;
		Bitmap preview = Bitmap.createBitmap( bitmapSizeW , bitmapSizeH , Config.ARGB_8888 );
		// Render the icon
		Drawable icon = iconCache.getFullResIcon( info );
		if( icon == null )
			icon = iconCache.getDefaultIconDrawable();
		int hsize = icon.getIntrinsicWidth();
		int ysize = icon.getIntrinsicHeight();
		int xoffset = ( bitmapSizeW - hsize ) / 2;
		int yoffset = ( bitmapSizeH - ysize ) / 2;
		if( xoffset >= 0 && yoffset >= 0 )
		{
		}
		else
		{
			float ratio = 1f;
			float ratioX = (float)hsize / (float)bitmapSizeW;
			float ratioY = (float)ysize / (float)bitmapSizeH;
			ratio = ratioX > ratioY ? ratioX : ratioY;
			hsize = (int)( (float)bitmapSizeW / ratio );
			ysize = (int)( (float)bitmapSizeH / ratio );
			xoffset = ( bitmapSizeW - hsize ) / 2;
			yoffset = ( bitmapSizeH - ysize ) / 2;
		}
		renderDrawableToBitmap( icon , preview , xoffset , yoffset , hsize , ysize );
		return preview;
	}
	
	public static Bitmap getWidgetPreview(
			Bitmap bitmap ,
			int cellHSpan ,
			int cellVSpan )
	{
		int bitmapWidth;
		int bitmapHeight;
		int maxWidth;
		int maxHeight;
		bitmapWidth = bitmap.getWidth();
		bitmapHeight = bitmap.getHeight();
		// Cap the size so widget previews don't appear larger than the actual
		// widget
		if( cellHSpan < 3 )
			cellHSpan = 3;
		if( cellVSpan < 3 )
			cellVSpan = 3;
		maxWidth = appHost.estimateWidgetCellWidth( cellHSpan );
		maxHeight = appHost.estimateWidgetCellHeight( cellVSpan );
		float scale = 1f;
		if( bitmapWidth > maxWidth )
		{
			scale = maxWidth / (float)bitmapWidth;
		}
		if( bitmapHeight * scale > maxHeight )
		{
			scale = maxHeight / (float)bitmapHeight;
		}
		if( DefaultLayout.show_widget_shortcut_bg || DefaultLayout.widget_shortcut_lefttop )
		{
			bitmapWidth *= 0.9f;
			bitmapHeight *= 0.9f;
		}
		// if (scale != 1f) {
		bitmapWidth = (int)( scale * bitmapWidth );
		bitmapHeight = (int)( scale * bitmapHeight );
		Bitmap preview = Bitmap.createScaledBitmap( bitmap , bitmapWidth , bitmapHeight , false );
		if( !bitmap.equals( preview ) )
			bitmap.recycle();
		return preview;
		// } else
		// return bitmap;
	}
	
	private Bitmap getWidgetPreview(
			ComponentName provider ,
			int previewImage ,
			int iconId ,
			int cellHSpan ,
			int cellVSpan ,
			int maxWidth ,
			int maxHeight )
	{
		// Load the preview image if possible
		if( cellHSpan == 1 && cellVSpan == 1 )
		{
			cellHSpan = 2;
			cellVSpan = 2;
		}
		String packageName = provider.getPackageName();
		if( maxWidth < 0 )
			maxWidth = Integer.MAX_VALUE;
		if( maxHeight < 0 )
			maxHeight = Integer.MAX_VALUE;
		Drawable drawable = null;
		if( previewImage != 0 )
		{
			drawable = launcher.getPackageManager().getDrawable( packageName , previewImage , null );
			if( drawable == null )
			{
				Log.w( TAG , "Can't load widget preview drawable 0x" + Integer.toHexString( previewImage ) + " for provider: " + provider );
			}
		}
		int bitmapWidth;
		int bitmapHeight;
		boolean widgetPreviewExists = ( drawable != null );
		if( widgetPreviewExists )
		{
			bitmapWidth = drawable.getIntrinsicWidth();
			bitmapHeight = drawable.getIntrinsicHeight();
			maxWidth = Math.min( maxWidth , appHost.estimateWidgetCellWidth( 4 ) );
			maxHeight = Math.min( maxHeight , appHost.estimateWidgetCellHeight( 4 ) );
		}
		else
		{
			if( DefaultLayout.display_widget_preview_hole )
			{
				bitmapWidth = Math.min( maxWidth , appHost.estimateWidgetCellWidth( cellHSpan ) );
				bitmapHeight = Math.min( maxHeight , appHost.estimateWidgetCellHeight( cellVSpan ) );
				if( bitmapWidth < R3D.workspace_cell_width )
					bitmapWidth = R3D.workspace_cell_width;
				if( bitmapHeight < R3D.workspace_cell_height )
					bitmapHeight = R3D.workspace_cell_height;
				maxWidth = Math.min( maxWidth , appHost.estimateWidgetCellWidth( 4 ) );
				maxHeight = Math.min( maxHeight , appHost.estimateWidgetCellHeight( 4 ) );
			}
			else
			{
				bitmapWidth = R3D.workspace_cell_width;
				bitmapHeight = R3D.workspace_cell_height;
				maxWidth = bitmapWidth;
				maxHeight = bitmapHeight;
			}
		}
		float scale = 1f;
		if( bitmapWidth > maxWidth )
		{
			scale = maxWidth / (float)bitmapWidth;
		}
		if( bitmapHeight * scale > maxHeight )
		{
			scale = maxHeight / (float)bitmapHeight;
		}
		if( scale != 1f )
		{
			bitmapWidth = (int)( scale * bitmapWidth );
			bitmapHeight = (int)( scale * bitmapHeight );
		}
		if( DefaultLayout.show_widget_shortcut_bg || DefaultLayout.widget_shortcut_lefttop )
		{
			bitmapWidth *= 0.9f;
			bitmapHeight *= 0.9f;
		}
		// Log.d("launcher", "width,height="+bitmapWidth+","+bitmapHeight);
		Bitmap preview = Bitmap.createBitmap( bitmapWidth , bitmapHeight , Config.ARGB_8888 );
		if( widgetPreviewExists )
		{
			renderDrawableToBitmap( drawable , preview , 0 , 0 , bitmapWidth , bitmapHeight );
		}
		else
		{
			if( DefaultLayout.display_widget_preview_hole )
			{
				renderDrawableToBitmap( mDefaultWidgetBackground , preview , 0 , 0 , bitmapWidth , bitmapHeight );
			}
			try
			{
				Drawable icon = null;
				if( iconId > 0 )
					icon = iconCache.getFullResIcon( packageName , iconId );
				if( icon == null )
					icon = iconCache.getDefaultIconDrawable();
				int hsize = icon.getIntrinsicWidth();
				int ysize = icon.getIntrinsicHeight();
				float scale2 = 1f;
				if( icon.getIntrinsicWidth() > bitmapWidth )
				{
					scale2 = (float)bitmapWidth / (float)icon.getIntrinsicWidth();
				}
				if( icon.getIntrinsicHeight() * scale2 > bitmapHeight )
				{
					scale2 = (float)bitmapHeight / (float)icon.getIntrinsicHeight();
				}
				if( scale2 != 1f )
				{
					hsize = (int)( scale2 * icon.getIntrinsicWidth() );
					ysize = (int)( scale2 * icon.getIntrinsicHeight() );
				}
				int hoffset = ( bitmapWidth - hsize ) / 2;
				int yoffset = ( bitmapHeight - ysize ) / 2;
				renderDrawableToBitmap( icon , preview , hoffset , yoffset , hsize , ysize );
			}
			catch( Resources.NotFoundException e )
			{
			}
		}
		return preview;
	}
	
	public static void renderDrawableToBitmap(
			Drawable d ,
			Bitmap bitmap ,
			int x ,
			int y ,
			int w ,
			int h )
	{
		renderDrawableToBitmap( d , bitmap , x , y , w , h , 1f , 0xFFFFFFFF );
	}
	
	public static void renderDrawableToBitmap(
			Drawable d ,
			Bitmap bitmap ,
			int x ,
			int y ,
			int w ,
			int h ,
			float scale ,
			int multiplyColor )
	{
		if( bitmap != null )
		{
			Canvas c = new Canvas( bitmap );
			c.scale( scale , scale );
			Rect oldBounds = d.copyBounds();
			d.setBounds( x , y , x + w , y + h );
			if( DefaultLayout.widget_shortcut_lefttop )
			{
				d.setBounds( 0 , 0 , w , h );
			}
			d.draw( c );
			d.setBounds( oldBounds ); // Restore the bounds
			// if (multiplyColor != 0xFFFFFFFF) {
			// c.drawColor(mDragViewMultiplyColor, PorterDuff.Mode.MULTIPLY);
			// }
		}
	}
	
	@Override
	public void finishBindWorkspace()
	{
		Log.e( "load" , "finish workspace" );
		Utils3D.showTimeFromStart( "finish workspace" );
		if( bDesktopDone )
		{
			Log.i( "load" , "has finish workspace" );
			return;
		}
		bDesktopDone = true;
		checkLoadProgress();
		if( FeatureConfig.enable_DefaultScene && DefaultLayout.scene_old )
		{
			SharedPreferences preferences = iLoongLauncher.getInstance().getSharedPreferences( "scene_first" , Activity.MODE_WORLD_WRITEABLE );
			String pkg = preferences.getString( "scenepkg" , FeatureConfig.scene_pkg );
			String cls = preferences.getString( "scenecls" , FeatureConfig.scene_cls );
			// Log.v("", "pkg is " + pkg + " cls is " + cls);
			if( SceneManager.getInstance().isCunZaiPkg( pkg ) )
			{
				root.scenePkg = pkg;
				root.sceneCls = cls;
			}
		}
	}
	
	public void sortApp(
			final int checkId )
	{
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				appHost.sortApp( checkId );
			}
		} );
	}
	
	public void dismissIntroduction()
	{
		d3d.RemoveView( flipView );
		flipView.disposeTexture();
		root.show();
	}
	
	public ClingTarget getHotDockGroup()
	{
		return root.getHotDockGroup();
	}
	
	public void focusWidget(
			WidgetPluginView3D widgetPluginView ,
			int state )
	{
		root.focusWidget( widgetPluginView , state );
	}
	
	public boolean findCellForSpan(
			int screen ,
			int x ,
			int y ,
			int[] xy ,
			int spanX ,
			int spanY )
	{
		xy[0] = -1;
		xy[1] = -1;
		if( workspace == null )
			return false;
		if( screen >= workspace.getPageNum() )
			return false;
		CellLayout3D cell = (CellLayout3D)workspace.getChildAt( screen );
		//		if (desktopEdit == null) {
		//			return false;
		//		}
		//		if (screen >= desktopEdit.getPageNumIncludeAddPage()) {
		//			return false;
		//		}
		//		View3D page = desktopEdit.getPageList().get(screen);
		//		CellLayout3D cell = (CellLayout3D)((ViewGroup3D)page).findView("celllayout");
		if( x >= 0 && y >= 0 )
			// xy = cell.findNearestArea(x, y, spanX, spanY, xy);
			xy = cell.findNearestAreaAvailuable( x , y , spanX , spanY , xy );
		else
			cell.findCellForSpan( xy , 1 , 1 );
		return !( xy[0] == -1 || xy[1] == -1 );
	}
	
	public void setAppEffectType(
			int select )
	{
		if( root != null )
			root.setEffectType( select );
	}
	
	// xiatian add start //fix bug:0001881
	public boolean HideThemesInAppList(
			String packageName )
	{
		return packageName.startsWith( "com.cooeecomet.themes." );
	}
	
	// xiatian add end
	// teapotXu add start
	// Widget icon will not be shown in Launcher
	public boolean IsInstalledCooeeWidgets(
			iLoongLauncher launcher ,
			String packageName )
	{
		PackageManager pm = launcher.getPackageManager();
		Intent intent = new Intent( iLoongLauncher.EXTRA_SEARCH_WIDGET , null );
		List<ResolveInfo> mWidgetResolveInfoList = pm.queryIntentActivities( intent , PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA );
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
	// xiatian add start //fix bug:bindWidget3DUpdated lead to lose Widget3DView
	// in Workspace3D
	private void addWidget3DInUpdated(
			ResolveInfo resolveInfo )
	{
		if( mIsWidget3DUpdated )
		{
			Widget3D mWidget3D = Widget3DManager.getInstance().getWidget3D( resolveInfo );
			Widget3DInfo wdgInfo = mWidget3D.getItemInfo();
			if( mUpdatedWidget3DInfo != null )
				wdgInfo = mUpdatedWidget3DInfo;
			wdgInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_WIDGET3D;
			mWidget3D.setItemInfo( wdgInfo );
			Root3D.addOrMoveDB( wdgInfo , ItemInfo.NO_ID );
			getWorkspace3D().addInScreen( mWidget3D , wdgInfo.screen , wdgInfo.x , wdgInfo.y , false );
			DefaultLayout.addWidgetView( mWidget3D , resolveInfo.activityInfo.packageName );
			mUpdatedWidget3DInfo = null;
		}
	}
	// xiatian add end
}
