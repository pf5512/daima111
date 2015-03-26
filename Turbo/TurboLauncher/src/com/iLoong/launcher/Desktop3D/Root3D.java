package com.iLoong.launcher.Desktop3D;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.cooee.android.launcher.framework.LauncherModel;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.DesktopEdit.DesktopEditHost;
import com.iLoong.launcher.DesktopEdit.DesktopEditMenu.SingleMenu;
import com.iLoong.launcher.DesktopEdit.DesktopEditMenuItem;
import com.iLoong.launcher.DesktopEdit.MenuContainer;
import com.iLoong.launcher.Folder3D.Folder3D;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.Folder3D.FolderMIUI3D;
import com.iLoong.launcher.Folder3D.FolderTarget3D;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreview3D;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreviewTips3D;
import com.iLoong.launcher.HotSeat3D.HotDockGroup;
import com.iLoong.launcher.HotSeat3D.HotGridView3D;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.HotSeat3D.MediaSeat3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Actions.MenuActionListener;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ConfigBase;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Contact3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DHost.Widget3DProvider;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.Workspace.Workspace;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.app.AppListDB;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.data.Widget2DInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.miui.MIUIWidgetHost;
import com.iLoong.launcher.miui.MIUIWidgetList;
import com.iLoong.launcher.miui.WorkspaceEditView;
import com.iLoong.launcher.newspage.NewsHandle;
import com.iLoong.launcher.recent.RecentAppPage;
import com.iLoong.launcher.search.QSearchGroup;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.widget.Widget;
import com.iLoong.launcher.widget.WidgetHostView;
import com.thirdParty.analytics.umeng.UmengMobclickAgent;

import dalvik.system.DexClassLoader;


public class Root3D extends ViewGroup3D implements MenuActionListener
{
	
	public final static int MSG_SET_WALLPAPER_OFFSET = 10000;
	public static final int MSG_SET_TRASH_POS = 500;
	private static int sClickCount;
	private static boolean sEnableShowFps;
	private static long sCurrentTimeMillis;
	private static BitmapFont sBitmapFont;
	private WallpaperManager mWallpaperManager;
	private DragLayer3D dragLayer;
	private PageContainer3D pageContainer;
	public static AppHost3D appHost;
	private Workspace3D workspace;
	private SetupMenu3D setupMenu;// zjp
	public TrashIcon3D trashIcon;
	private ApplicationListHost listToAdd;
	private FolderTarget3D folderTarget; // teapotXu added //Jone delete, difine duplicately.
	// private HotButton hotButton;
	public PageIndicator3D pageIndicator;
	public static HotSeat3D hotseatBar;
	private MediaSeat3D musicSeat;
	//xujin
	private MediaSeat3D cameraSeat;
	private Timeline timeline;
	public boolean folderOpened = false;
	private FolderIcon3D folder;
	public static iLoongLauncher launcher;
	private boolean goHome = false;
	private static float workspaceTweenDuration = 0.1f;
	private Timeline workspaceAndAppTween;
	public static boolean startWorkspaceAndAppTween = false;
	private Tween s3EffectTween;
	private Tween s4ScrollEffectTween;
	private static final int DRAG_END = 6;
	public boolean is_delete = false;
	private View3D focusWidget;
	private float[] focusWidgetPos;
	private float focusWidgetMov;
	private View3D shadowView;
	public static float s3EffectScale = 0.8f;
	// teapotXu add start for longClick in Workspace to editMode as miui
	private WorkspaceEditView firstView , lastView;
	public static float scaleFactor = 0.87f;// 0.85f;//0.9f ;
	// teapotXu add end
	public static boolean scroll_indicator = false;
	public static boolean hotSeat_scrolling_back = false; // teapotXu add
	public static boolean isDragAutoEffect = false;// teapotXu add 拖拽至前页或后页
	// xiatian add start //EffectPreview
	public EffectPreview3D mWorkspaceEffectPreview;
	public EffectPreview3D mApplistEffectPreview;
	public EffectPreviewTips3D mEffectPreviewTips3D;
	private static int mNeedDelayWorkspaceEffectPreview = -1;
	private static int mNeedDelayApplistEffectPreview = -1;
	public static int mIsInEffectPreviewMode = -1;
	// xiatian add end
	public static boolean IsProhibiteditMode = false;// 是否禁止编辑模式，zjp
	private int RemoveFolderCellX;
	private int RemoveFolderCellY;
	private int removeFolderScreen;
	private List<ItemInfo> mainFolderinfo = new ArrayList<ItemInfo>();
	private List<ItemInfo> allMainFolderAppinfo = new ArrayList<ItemInfo>();
	public static float lastx = 0;
	public static float lasty = 0;
	public static float lastupdatex = 0;
	public static float lastupdatey = 0;
	public static View3D screenFrontImg = null;//Jone
	public NewsHandle newsHandle;
	public QSearchGroup qSearchGroup = null;
	public static boolean appListAniDone = false; // Only use for first time app list loading.
	
	public AppHost3D getAppHost()
	{
		return appHost;
	}
	
	public HotSeat3D getHotSeatBar()
	{
		return hotseatBar;
	}
	
	public MediaSeat3D getMusicSeat()
	{
		return musicSeat;
	}
	
	public MediaSeat3D getCameraSeat()
	{
		return cameraSeat;
	}
	
	public HotDockGroup getHotDockGroup()
	{
		return hotseatBar.getDockGroup();
	}
	
	public Root3D(
			String name )
	{
		super( name );
		if( sBitmapFont == null )
		{
			sBitmapFont = new BitmapFont( Gdx.files.internal( "test.fnt" ) , Gdx.files.internal( "test.png" ) , false );
		}
		hotseatBar = new HotSeat3D( "HotSeat3DBar" );
		this.addView( hotseatBar );
		if( DefaultLayout.show_music_page || DefaultLayout.show_music_page_enable_config )
		{
			musicSeat = new MediaSeat3D( "musicSeat" , "music" );
			this.addView( musicSeat );
		}
		if( RR.net_version )
		{
			Bitmap frontBitmap = ThemeManager.getInstance().getBitmap( "theme/pack_source/screen-front-cover.png" );
			TextureRegion frontReg = null;
			if( frontBitmap != null )
			{
				frontReg = new TextureRegion( new BitmapTexture( frontBitmap , true ) );
				screenFrontImg = new View3D( "screenFrontImg" );
				screenFrontImg.setSize( Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
				screenFrontImg.region = frontReg;
			}
		}
		//xujin
		if( DefaultLayout.enable_camera || DefaultLayout.show_camera_page_enable_config )
		{
			cameraSeat = new MediaSeat3D( "cameraSeat" , "camera" );
			this.addView( cameraSeat );
		}
		DesktopEditHost.popup( this );
		DesktopEditHost.getInstance().recyle();
	}
	
	public void onThemeChanged()
	{
		if( Icon3D.shortcutInfoListDb == null )
		{
			Icon3D.shortcutInfoListDb = iLoongApplication.getInstance().mModel.getShortcutInfoListFromDb();
		}
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				hotseatBar.onThemeChanged();
				pageIndicator.onThemeChanged();
				R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
				iLoongLauncher.getInstance().postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						workspace.onThemeChanged();
						pageContainer.onThemeChanged();
						appHost.onThemeChanged();
						//						qSearchGroup.onThemeChanged();
						trashIcon.onThemeChanged();
						R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
						Icon3D.shortcutInfoListDb = null;
					}
				} );
			}
		} );
	}
	
	public void setLauncher(
			iLoongLauncher l )
	{
		this.launcher = l;
		mWallpaperManager = WallpaperManager.getInstance( launcher );
		// teapotXu add start for mv wallpaper's config menu
		if( DefaultLayout.enable_configmenu_for_move_wallpaper )
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
			if( prefs.getBoolean( SetupMenu.getKey( RR.string.desktop_wallpaper_mv ) , true ) == false )
			{
				IBinder token = iLoongLauncher.getInstance().getWindow().getCurrentFocus().getWindowToken();
				if( token != null )
				{
					mWallpaperManager.setWallpaperOffsets( token , 0 , 0 );
				}
			}
		}
		// teapotXu add end
	}
	
	public void setPageContainer(
			View3D v )
	{
		this.pageContainer = (PageContainer3D)v;
		this.pageContainer.hide();
		this.pageContainer.setLauncher( launcher );
		this.addView( v );
	}
	
	public void setAppHost(
			View3D v )
	{
		this.appHost = (AppHost3D)v;
		if( !iLoongLauncher.showAllAppFirst )
			appHost.hide();
		this.addView( v );
	}
	
	public void setWorkspace(
			View3D v )
	{
		this.workspace = (Workspace3D)v;
		this.addView( v );
		if( hotseatBar != null )
		{
			hotseatBar.setWorkspace( this.workspace );
		}
		workspace.onDegreeChanged();
		setDragS3EffectScale();
		if( iLoongLauncher.showAllAppFirst )
			workspace.hide();
	}
	
	public void setTrashIcon(
			View3D v )
	{
		this.trashIcon = (TrashIcon3D)v;
		this.trashIcon.hide();
		this.addView( v );
	}
	
	// teapotXu add start for add new folder in top-trash bar
	public void setFolderTarget(
			View3D v )
	{
		this.folderTarget = (FolderTarget3D)v;
		this.folderTarget.hide();
		if( DefaultLayout.generate_new_folder_in_top_trash_bar && DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
			this.addView( v );
	}
	
	// teapotXu add end
	public void setSetupMenu(
			View3D v )
	{
		if( setupMenu != null )
		{
			this.removeView( setupMenu );
			for( int i = 0 ; i < setupMenu.getChildCount() ; i++ )
			{
				Log.v( "" , "setupMenu3D setSetupMenu name is " + setupMenu.getChildAt( i ).name );
				if( setupMenu.getChildAt( i ) != null )
				{
					setupMenu.getChildAt( i ).dispose();
				}
			}
			setupMenu = null;
		}
		this.setupMenu = (SetupMenu3D)v;
		this.setupMenu.hideNoAnim();
		this.addView( v );
	}
	
	public void setApplicationAppHost(
			View3D v )
	{
		if( listToAdd == null )
		{
			this.listToAdd = (ApplicationListHost)v;
			this.listToAdd.hide();
			this.addView( v );
		}
	}
	
	public void setPageIndicator(
			View3D v )
	{
		this.pageIndicator = (PageIndicator3D)v;
		this.addView( v );
		if( iLoongLauncher.showAllAppFirst )
			pageIndicator.hideNoAnim();
	}
	
	Timeline timeline_fly = null;
	float duration = 0.3f;
	boolean isFlying = false;
	
	public void addFlySysWidget(
			final Widget2DInfo launcherInfo ,
			Widget v )
	{
		WidgetHostView widgetHostView = launcherInfo.hostView;
		//		Bitmap bmp = widgetHostView.mCustomCache;
		//		View3D v = new View3D( "flySysWidget" , new BitmapTexture( bmp ) );
		//		Widget v = iLoongLauncher.getInstance().d3dListener.addAppWidget( launcherInfo );
		v.setItemInfo( launcherInfo );
		ArrayList<View3D> list = new ArrayList<View3D>();
		list.add( v );
		CellLayout3D cellLayout3D = workspace.getCurrentCellLayout();
		if( cellLayout3D == null )
		{
			return;
		}
		Vector2 targetPosion = cellLayout3D.getTargetAbsolutePosition( list );
		if( targetPosion == null )
		{
			SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.no_space_add_icon ) );
			return;
		}
		this.addView( v );
		v.setPosition( v.x , v.y );
		if( timeline_fly != null )
		{
			timeline_fly.free();
			timeline_fly = null;
		}
		timeline_fly = Timeline.createParallel();
		v.setOrigin( 0 , 0 );
		timeline_fly.push( Tween.to( v , View3DTweenAccessor.POS_XY , duration ).target( targetPosion.x , targetPosion.y , 0 ).ease( Quad.OUT ) );
		//timeline_fly.push( Tween.to( v , View3DTweenAccessor.SCALE_XY , duration ).target( DesktopEditHost.scaleFactor2 , DesktopEditHost.scaleFactor2 , 0 ).ease( Linear.INOUT ) );
		timeline_fly.start( View3DTweenAccessor.manager ).setCallback( new TweenCallback() {
			
			@Override
			public void onEvent(
					int arg0 ,
					BaseTween arg1 )
			{
				if( arg1 == timeline_fly && TweenCallback.COMPLETE == arg0 )
				{
					//					iLoongLauncher.getInstance().xWorkspace.addInScreen( launcherInfo.hostView , launcherInfo.screen , launcherInfo.x , launcherInfo.y ,
					//					/* xy[0], xy[1], */launcherInfo.spanX , launcherInfo.spanY , false );
				}
			}
		} );
	}
	
	public void addFlySysShortcut(
			final Icon3D v )
	{
		ArrayList<View3D> list = new ArrayList<View3D>();
		list.add( v );
		CellLayout3D cellLayout3D = workspace.getCurrentCellLayout();
		if( cellLayout3D == null )
		{
			return;
		}
		Vector2 targetPosion = cellLayout3D.getTargetAbsolutePosition( list );
		if( targetPosion == null )
		{
			SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.no_space_add_icon ) );
			return;
		}
		this.addView( v );
		v.setPosition( v.x , v.y );
		if( timeline_fly != null )
		{
			timeline_fly.free();
			timeline_fly = null;
		}
		timeline_fly = Timeline.createParallel();
		v.setOrigin( 0 , 0 );
		timeline_fly.push( Tween.to( v , View3DTweenAccessor.POS_XY , duration ).target( targetPosion.x , targetPosion.y , 0 ).ease( Quad.OUT ) );
		//timeline_fly.push( Tween.to( v , View3DTweenAccessor.SCALE_XY , duration ).target( DesktopEditHost.scaleFactor2 , DesktopEditHost.scaleFactor2 , 0 ).ease( Linear.INOUT ) );
		timeline_fly.start( View3DTweenAccessor.manager ).setCallback( new TweenCallback() {
			
			@Override
			public void onEvent(
					int arg0 ,
					BaseTween arg1 )
			{
				if( arg1 == timeline_fly && TweenCallback.COMPLETE == arg0 )
				{
					ItemInfo info = v.getItemInfo();
					workspace.addInScreen( v , info.screen , info.x , info.y , false );
				}
			}
		} );
	}
	
	public ViewGroup3D zoomview = null;
	
	public void addZoomWidget(
			View3D view )
	{
		CellLayout3D cellLayout = new CellLayout3D( "celllayout" );
		zoomview = cellLayout.reflectZoomView( view );
		addView( zoomview );
		if( zoomview != null )
		{
			ZoomBox zoombox = (ZoomBox)zoomview.findView( "ZoomViewbg" );
			if( zoombox != null )
			{
				zoombox.requestFocus();
			}
		}
	}
	
	public void ReturnClor()
	{
		int count = ( (ViewGroup3D)( this.getWorkspace().getChildAt( this.getWorkspace().getCurrentPage() ) ) ).getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			( (ViewGroup3D)( this.getWorkspace().getChildAt( this.getWorkspace().getCurrentPage() ) ) ).getChildAt( i ).startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.5f , 1f , 0 , 0 );
		}
		hotseatBar.startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.5f , 1f , 0 , 0 );
	}
	
	public void addFlyWidget(
			final String packageName )
	{
		String className = DefaultLayout.getWidgetItemClassName( packageName );
		final Widget3D widget3D = Widget3DManager.getInstance().getWidget3D( packageName , className );
		ArrayList<View3D> list = new ArrayList<View3D>();
		list.add( widget3D );
		CellLayout3D cellLayout3D = workspace.getCurrentCellLayout();
		if( cellLayout3D == null )
		{
			return;
		}
		Vector2 targetPosion = cellLayout3D.getTargetAbsolutePosition( list );
		if( targetPosion == null )
		{
			SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.no_space_add_icon ) );
			return;
		}
		this.addView( widget3D );
		if( timeline_fly != null )
		{
			timeline_fly.free();
			timeline_fly = null;
		}
		timeline_fly = Timeline.createParallel();
		timeline_fly.push( Tween.to( widget3D , View3DTweenAccessor.POS_XY , duration ).target( targetPosion.x , targetPosion.y , 0 ).ease( Quad.OUT ) );
		//timeline_fly.push( Tween.to( widget3D , View3DTweenAccessor.SCALE_XY , duration ).target( DesktopEditHost.scaleFactor2 , DesktopEditHost.scaleFactor2 , 0 ).ease( Linear.INOUT ) );
		timeline_fly.start( View3DTweenAccessor.manager ).setCallback( new TweenCallback() {
			
			@Override
			public void onEvent(
					int arg0 ,
					BaseTween arg1 )
			{
				if( arg1 == timeline_fly && TweenCallback.COMPLETE == arg0 )
				{
					iLoongLauncher.getInstance().add3DWidget( widget3D );
				}
			}
		} );
	}
	
	public void addFlyView(
			final View3D v )
	{
		if( isFlying )
		{
			return;
		}
		if( v instanceof IconBase3D )
		{
			isFlying = true;
			ArrayList<View3D> list = new ArrayList<View3D>();
			list.add( v );
			CellLayout3D cellLayout3D = workspace.getCurrentCellLayout();
			if( cellLayout3D == null )
			{
				isFlying = false;
				return;
			}
			Vector2 targetPosion = cellLayout3D.getTargetAbsolutePosition( list );
			if( targetPosion == null )
			{
				SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.no_space_add_icon ) );
				isFlying = false;
				return;
			}
			this.addView( v );
			v.setPosition( v.x , v.y );
			if( timeline_fly != null )
			{
				timeline_fly.free();
				timeline_fly = null;
			}
			timeline_fly = Timeline.createParallel();
			v.setOrigin( 0 , 0 );
			timeline_fly.push( Tween.to( v , View3DTweenAccessor.POS_XY , duration ).target( targetPosion.x , targetPosion.y , 0 ).ease( Quad.OUT ) );
			timeline_fly.push( Tween.to( v , View3DTweenAccessor.SCALE_XY , duration ).target( DesktopEditHost.scaleFactor2 , DesktopEditHost.scaleFactor2 , 0 ).ease( Linear.INOUT ) );
			timeline_fly.start( View3DTweenAccessor.manager ).setCallback( new TweenCallback() {
				
				@Override
				public void onEvent(
						int arg0 ,
						BaseTween arg1 )
				{
					if( arg1 == timeline_fly && TweenCallback.COMPLETE == arg0 )
					{
						iLoongLauncher.getInstance().addIcon3D( (Icon3D)v );
						isFlying = false;
					}
				}
			} );
		}
	}
	
	public void setDragLayer(
			View3D v )
	{
		this.dragLayer = (DragLayer3D)v;
	}
	
	public void addDragLayer(
			View3D v )
	{
		hotseatBar.bringToFront();
		if( DefaultLayout.show_music_page || DefaultLayout.show_music_page_enable_config )
		{
			musicSeat.bringToFront();
		}
		//xujin
		if( ( DefaultLayout.enable_camera || DefaultLayout.show_camera_page_enable_config ) && cameraSeat != null )
		{
			cameraSeat.bringToFront();
		}
		// this.addView(new ObjectView("3d"));
		trashIcon.bringToFront();
		if( DefaultLayout.setupmenu_by_system )
		{
		}
		else
		{
			if( DefaultLayout.setupmenu_by_view3d )
				setupMenu.bringToFront();
		}
		this.dragLayer.hide();
		this.addView( v );
	}
	
	public boolean isPageContainerVisible()
	{
		return this.pageContainer.isVisible();
	}
	
	public static void addOrMoveDB(
			ItemInfo info ,
			long container )
	{
		if( container >= LauncherSettings.Favorites.CONTAINER_APPLIST )
		{
			AppListDB.getInstance().addOrMoveItem( info , container );
		}
		else
			LauncherModel.addOrMoveItemInDatabase( iLoongApplication.getInstance() , info , container , info.screen , info.x , info.y );
	}
	
	public static void addOrMoveDB(
			ItemInfo info )
	{
		LauncherModel.addOrMoveItemInDatabase( iLoongApplication.getInstance() , info , LauncherSettings.Favorites.CONTAINER_DESKTOP , info.screen , info.x , info.y );
	}
	
	public static void addOrMoveDBs(
			ItemInfo info ,
			int spanX ,
			int spanY )
	{
		LauncherModel.addOrMoveItemInDatabase( iLoongApplication.getInstance() , info , LauncherSettings.Favorites.CONTAINER_DESKTOP , info.screen , info.x , info.y , spanX , spanY );
	}
	
	public static void addOrMoveDB(
			ItemInfo info ,
			Intent intent )
	{
		LauncherModel.addOrMoveItemInDatabase( iLoongApplication.getInstance() , info , LauncherSettings.Favorites.CONTAINER_DESKTOP , info.screen , info.x , info.y , intent );
	}
	
	public static void deleteFromDB(
			ItemInfo info )
	{
		if( info.container >= LauncherSettings.Favorites.CONTAINER_APPLIST )
		{
			AppListDB.getInstance().deleteItem( info );
		}
		else
			LauncherModel.deleteItemFromDatabase( iLoongApplication.getInstance() , info );
	}
	
	public static void updateItemInDatabase(
			ItemInfo item )
	{
		if( item.container >= LauncherSettings.Favorites.CONTAINER_APPLIST )
		{
			AppListDB.getInstance().moveItem( item );
		}
		else
			LauncherModel.updateItemInDatabase( iLoongLauncher.getInstance() , item );
	}
	
	public static void batchUpdateItems(
			ArrayList<ItemInfo> ItemInfoList )
	{
		LauncherModel.batchUpdateItemsInDatabase( iLoongLauncher.getInstance() , ItemInfoList );
	}
	
	private void folder_DropList_backtoOrig(
			ArrayList<View3D> child_list )
	{
		// ItemInfo itemInfo;
		//
		// for (int i=0;i<child_list.size();i++)
		// {
		// View3D view = child_list.get(i);
		// if (view instanceof FolderIcon3D)
		// {
		// ((FolderIcon3D)view).setLongClick(false);
		// }
		// view.stopTween();
		// itemInfo= ((IconBase3D)view).getItemInfo();
		// workspace.addInCurrenScreen(view, itemInfo.x, itemInfo.y);
		//
		// //workspace.getCurrentCellLayout().addView(view);
		// view.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT, 0.5f,
		// itemInfo.x, itemInfo.y, 0);
		// }
		View3D view = child_list.get( 0 );
		if( view instanceof IconBase3D )
		{
			if( ( (IconBase3D)view ).getItemInfo().container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
			{
				dropListBacktoHotSeat( child_list );
				return;
			}
		}
		View3D parent = view.getParent();
		ItemInfo info = null;
		if( view instanceof IconBase3D )
			info = ( (IconBase3D)view ).getItemInfo();
		if( info != null && info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP ) /*
																								 * is
																								 * not
																								 * come
																								 * from
																								 * mainmenu
																								 */
		{
			for( int i = 0 ; i < child_list.size() ; i++ )
			{
				view = child_list.get( i );
				workspace.addBackInScreen( view , 0 , 0 );
			}
		}
		// teapotXu add start: 添加会桌面的文件夹
		else if( info != null && ( info.container >= 0 && info.container < LauncherSettings.Favorites.CONTAINER_APPLIST ) )
		{
			// item belongs to the Folder in Idle
			workspace.addBackIntoWorkspaceFolder( child_list , info.container );
		}
		// teapotXu add end
	}
	
	private void dropListBacktoHotSeat(
			ArrayList<View3D> child_list )
	{
		View3D view;
		ArrayList<ItemInfo> listInfo = new ArrayList<ItemInfo>();
		view = child_list.get( 0 );
		ItemInfo info = ( (IconBase3D)view ).getItemInfo();
		listInfo.add( info );
		hotseatBar.bindItems( listInfo );
	}
	
	public void dropListBacktoOrig(
			ArrayList<View3D> child_list )
	{
		View3D view;
		View3D findView = null;
		if( child_list.size() <= 0 )
			return;
		view = child_list.get( 0 );
		if( ( view instanceof IconBase3D ) == false )
			return;
		ItemInfo info = ( (IconBase3D)view ).getItemInfo();
		if( view instanceof Widget3D && info.container == -1 )
		{
			/*
			 * 处理将widget 3d
			 * 扔到其它地方时的资源收集
			 */
			Root3D.deleteFromDB( info );
			Widget3D widget3D = (Widget3D)view;
			Widget3DManager.getInstance().deleteWidget3D( widget3D );
		}
		if( child_list.size() == 1 )
		{
			if( view instanceof IconBase3D )
			{
				if( info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
				{
					/* back to hotseat */
					dropListBacktoHotSeat( child_list );
					child_list.clear();
					return;
				}
				/* 从文件夹中来 */
				if( info.container >= 0 )
				{
					UserFolderInfo folderInfo = launcher.getFolderInfo( info.container );
					if( folderInfo == null )
					{
						ItemInfo item = ( (IconBase3D)view ).getItemInfo();
						if( RemoveFolderCellX == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
						{
							item.screen = removeFolderScreen;
							// hotseatBar.additem(view);
						}
						else
						{
							item.cellX = RemoveFolderCellX;
							item.cellY = RemoveFolderCellY;
							item.screen = removeFolderScreen;
							item.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
							if( view instanceof Icon3D )
							{
								Utils3D.changeTextureRegion( view , R3D.workspace_cell_height , false );
							}
							boolean result = workspace.addInScreen( view , removeFolderScreen , 0 , 0 , false );
							if( !result )
							{
								workspace.addBackInScreen( view , 0 , 0 );
							}
						}
					}
					// teapotXu add start for Folder in Maimenu
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						// folder is from mainmenu, so do nothing, just clear
						// the childlist
						if( folderInfo == null )
						{
							child_list.clear();
							return;
						}
					}
					// teapotXu add end for Folder in Mainmenu
					if( folderInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
					{
						findView = hotseatBar.findExistView( folderInfo.screen );
					}
					else if( folderInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
					{
						findView = workspace.getViewByItemInfo( folderInfo );
					}
					if( findView != null && findView instanceof FolderIcon3D )
					{
						Workspace3D.setDragMode( Workspace3D.DRAG_MODE_ADD_TO_FOLDER );
						( (FolderIcon3D)findView ).onDrop( child_list , 0 , 0 );
					}
					child_list.clear();
					return;
				}
			}
		}
		if( info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
		{
			for( int i = 0 ; i < child_list.size() ; i++ )
			{
				view = child_list.get( i );
				workspace.addBackInScreen( view , 0 , 0 );
			}
		}
		child_list.clear();
	}
	
	// teapotXu add start for longClick in Workspace to editMode like miui
	Timeline miui_effect_line = null;
	boolean isQuitAnim = false;
	public boolean is_long_click_doing = false;
	private MIUIWidgetHost widgetHost;
	
	public boolean getWorkspaceEditModeAnimStatus()
	{
		boolean result = false;
		if( miui_effect_line == null )
		{
			result = false;
		}
		else
			result = true;
		return result;
	}
	
	public MIUIWidgetHost getWidgetHost()
	{
		return widgetHost;
	}
	
	public void setWidgetHost(
			View3D v )
	{
		this.widgetHost = (MIUIWidgetHost)v;
		this.addView( v );
	}
	
	private boolean canStartAnim()
	{
		if( miui_effect_line != null && miui_effect_line.isStarted() )
		{
			return false;
		}
		return true;
	}
	
	public FolderIcon3D getOpenFolder()
	{
		if( folder == null )
		{
			if( appHost.getFolderIconOpendInAppHost() != null )
			{
				return appHost.getFolderIconOpendInAppHost();
			}
			else
			{
				return null;
			}
		}
		return folder;
	}
	
	private Timeline qs_fade_out = null;
	
	public void startQsearchFadeoutAmin()
	{
		if( qSearchGroup == null )
		{
			return;
		}
		if( qs_fade_out != null )
		{
			qs_fade_out.free();
			qs_fade_out = null;
		}
		float duration = 0.5f;
		hotseatBar.color.a = 0;
		hotseatBar.setPosition( 0 , 0 );
		qs_fade_out = Timeline.createParallel();
		qs_fade_out.push( Tween.to( qSearchGroup , View3DTweenAccessor.OPACITY , duration ).target( 0 ) );
		qs_fade_out.push( Tween.to( hotseatBar , View3DTweenAccessor.OPACITY , duration ).target( 1 ) );
		qs_fade_out.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	public void workspaceChangeForPopMenu()
	{
		if( DesktopEditHost.getInstance() != null )
		{
			float duration = 0.4f;
			TweenEquation easeEquation = Linear.INOUT;
			miui_effect_line = Timeline.createParallel();
			if( DesktopEditHost.getInstance().getPopMenuStyle() == DesktopEditHost.POP_MENU_STYLE_4X2 )
			{
				miui_effect_line.push( Tween.to( workspace , View3DTweenAccessor.USER , duration ).target( R3D.workspace_offset_high_y , 0 , 0 ).ease( easeEquation ) );
				miui_effect_line.push( Tween.to( workspace , View3DTweenAccessor.SCALE_XY , duration ).target( DesktopEditHost.scaleFactor2 , DesktopEditHost.scaleFactor2 , 0 ).ease( easeEquation ) );
				if( Utils3D.hasMeiZuSmartBar() )
				{
					miui_effect_line.push( Tween
							.to( pageIndicator , View3DTweenAccessor.POS_XY , duration )
							.target(
									pageIndicator.x ,
									R3D.page_indicator_y_high + ( R3D.page_indicator_y_high + R3D.Workspace_celllayout_editmode_padding ) / 2f + R3D.edit_mode_indicator_offset_4x2 + Utils3D.px2dip(
											iLoongLauncher.getInstance() ,
											30 ) ,
									0 ).ease( easeEquation ) );
				}
				else
				{
					miui_effect_line.push( Tween
							.to( pageIndicator , View3DTweenAccessor.POS_XY , duration )
							.target(
									pageIndicator.x ,
									R3D.page_indicator_y_high + ( R3D.page_indicator_y_high + R3D.Workspace_celllayout_editmode_padding ) / 2f + R3D.edit_mode_indicator_offset_4x2 ,
									0 ).ease( easeEquation ) );
				}
			}
			else
			{
				miui_effect_line.push( Tween.to( workspace , View3DTweenAccessor.USER , duration ).target( R3D.workspace_offset_low_y , 0 , 0 ).ease( easeEquation ) );
				miui_effect_line.push( Tween.to( workspace , View3DTweenAccessor.SCALE_XY , duration ).target( DesktopEditHost.scaleFactor , DesktopEditHost.scaleFactor , 0 ).ease( easeEquation ) );
				if( Utils3D.hasMeiZuSmartBar() )
				{
					miui_effect_line.push( Tween.to( pageIndicator , View3DTweenAccessor.POS_XY , duration )
							.target( pageIndicator.x , R3D.page_indicator_y + ( R3D.page_indicator_y + R3D.Workspace_celllayout_editmode_padding ) / 3f , 0 ).ease( easeEquation ) );
				}
				else
				{
					miui_effect_line.push( Tween.to( pageIndicator , View3DTweenAccessor.POS_XY , duration )
							.target( pageIndicator.x , R3D.page_indicator_y + ( R3D.page_indicator_y + R3D.Workspace_celllayout_editmode_padding ) / 2f + R3D.edit_mode_indicator_offset_4x1 , 0 )
							.ease( easeEquation ) );
				}
			}
		}
		miui_effect_line.start( View3DTweenAccessor.manager ).setCallback( this );
		is_long_click_doing = true;
	}
	
	private void MIUIEffectAnim(
			boolean bStart )
	{
		if( bStart && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode )
		{
			return;
		}
		if( !bStart && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			return;
		}
		Log.e( "Root3D" , "MIUIEffectAnim 0" );
		CellLayout3D.isDrawBg = true;
		float duration = 0.4f;
		TweenEquation easeEquation = Linear.INOUT;
		miui_effect_line = Timeline.createParallel();
		hotseatBar.color.a = 1f;
		workspace.color.a = 1f;
		pageIndicator.color.a = 1f;
		if( bStart )
		{
			if( newsHandle != null )
			{
				if( DefaultLayout.enable_news )
				{
					if( DefaultLayout.show_newspage_with_handle )
					{
						newsHandle.hide();
						Messenger.sendMsg( Messenger.MSG_HIDE_NEWSVIEW_HANDLE , 0 );
					}
				}
			}
			isQuitAnim = false;
			setUser2( 1.0f );
			Log.e( "Root3D" , "MIUIEffectAnim 1" );
			miui_effect_line.push( Tween.to( hotseatBar , View3DTweenAccessor.POS_XY , duration / 2 ).target( 0 , -hotseatBar.height , 0 ).ease( easeEquation ) );
			miui_effect_line.push( Tween.to( hotseatBar , View3DTweenAccessor.OPACITY , duration / 2 ).target( 0 , 0 , 0 ).ease( easeEquation ) );
			if( DesktopEditHost.getInstance() != null )
			{
				Workspace3D.hasDownInEditMode = false;
				workspace.initView();
				if( DesktopEditHost.getPopMenuStyle() == DesktopEditHost.POP_MENU_STYLE_4X2 )
				{
					miui_effect_line.push( Tween.to( workspace , View3DTweenAccessor.USER , duration ).target( R3D.workspace_offset_high_y , 0 , 0 ).ease( easeEquation ) );
					miui_effect_line.push( Tween.to( workspace , View3DTweenAccessor.SCALE_XY , duration ).target( DesktopEditHost.scaleFactor2 , DesktopEditHost.scaleFactor2 , 0 )
							.ease( easeEquation ) );
					if( Utils3D.hasMeiZuSmartBar() )
					{
						miui_effect_line.push( Tween
								.to( pageIndicator , View3DTweenAccessor.POS_XY , duration )
								.target(
										pageIndicator.x ,
										R3D.page_indicator_y_high + ( R3D.page_indicator_y_high + R3D.Workspace_celllayout_editmode_padding ) / 2f + R3D.edit_mode_indicator_offset_4x2 + Utils3D
												.px2dip( iLoongLauncher.getInstance() , 30 ) ,
										0 ).ease( easeEquation ) );
					}
					else
					{
						miui_effect_line.push( Tween.to( pageIndicator , View3DTweenAccessor.POS_XY , duration )
								.target( pageIndicator.x , R3D.page_indicator_y_high + ( R3D.page_indicator_y_high + R3D.Workspace_celllayout_editmode_padding ) / 2f , 0 ).ease( easeEquation ) );
					}
				}
				else
				{
					miui_effect_line.push( Tween.to( workspace , View3DTweenAccessor.USER , duration ).target( R3D.workspace_offset_low_y , 0 , 0 ).ease( easeEquation ) );
					miui_effect_line
							.push( Tween.to( workspace , View3DTweenAccessor.SCALE_XY , duration ).target( DesktopEditHost.scaleFactor , DesktopEditHost.scaleFactor , 0 ).ease( easeEquation ) );
					if( Utils3D.hasMeiZuSmartBar() )
					{
						miui_effect_line.push( Tween.to( pageIndicator , View3DTweenAccessor.POS_XY , duration )
								.target( pageIndicator.x , R3D.page_indicator_y + ( R3D.page_indicator_y + R3D.Workspace_celllayout_editmode_padding ) / 3f , 0 ).ease( easeEquation ) );
					}
					else
					{
						miui_effect_line.push( Tween.to( pageIndicator , View3DTweenAccessor.POS_XY , duration )
								.target( pageIndicator.x , R3D.page_indicator_y + ( R3D.page_indicator_y + R3D.Workspace_celllayout_editmode_padding ) / 2f + R3D.edit_mode_indicator_offset_4x1 , 0 )
								.ease( easeEquation ) );
					}
				}
			}
		}
		else
		{
			isQuitAnim = true;
			setUser2( 1.0f );
			{
				hotseatBar.show();
			}
			miui_effect_line.push( Tween.to( this , View3DTweenAccessor.USER2 , duration ).target( 0 , 0 , 0 ).ease( easeEquation ) );
			miui_effect_line.push( Tween.to( workspace , View3DTweenAccessor.USER , duration ).target( 0 , 0 , 0 ).ease( easeEquation ) );
			miui_effect_line.push( Tween.to( workspace , View3DTweenAccessor.SCALE_XY , duration ).target( 1 , 1 , 0 ).ease( easeEquation ) );
			miui_effect_line.push( Tween.to( pageIndicator , View3DTweenAccessor.POS_XY , duration ).target( pageIndicator.x , R3D.page_indicator_y , 0 ).ease( easeEquation ) );
		}
		miui_effect_line.setUserData( bStart );
		miui_effect_line.start( View3DTweenAccessor.manager ).setCallback( this );
		is_long_click_doing = true;
	}
	
	public void startMIUIEditEffect()
	{
		if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			return;
		}
		if( canStartAnim() == false )
		{
			return;
		}
		if( findView( "MiuiFreeMain" ) != null )
		{
			return;
		}
		Messenger.sendMsg( Messenger.MSG_MOVE_OUT_MTK3DWIDGET , null );
		iLoongLauncher.getInstance().cleaWidgetStatus( workspace.page_index );
		// final int pageOrigy = Utils3D.getScreenHeight()
		// - R3D.workspace_cell_height -
		// R3D.Workspace_celllayout_editmode_padding;
		final int pageOrigy = (int)( Utils3D.getScreenHeight() / 2 );
		if( DefaultLayout.show_music_page )
		{
			workspace.removeMusicView();
		}
		//xujin 移除相机页
		if( DefaultLayout.enable_camera )
		{
			workspace.removeCameraView();
			//pageIndicator.setPageNum( workspace.getPageNum() );
		}
		Workspace3D.WorkspaceStatus = WorkspaceStatusEnum.EditMode;
		Workspace.WorkspaceStatus = WorkspaceStatusEnum.EditMode;
		Workspace3D.b_editmode_include_addpage = true;
		Log.v( "Root3D" , "startMIUIEditEffect WorkspaceStatus：" + Workspace3D.WorkspaceStatus );
		// SendMsgToAndroid.sendHideWorkspaceMsg();
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				firstView = new WorkspaceEditView( "firstView" , true );
				lastView = new WorkspaceEditView( "lastView" , false );
				if( workspace.getPageNum() < DefaultLayout.default_workspace_pagecount_max )
				{
					workspace.addPage( 0 , firstView );
					workspace.addPage( workspace.view_list.size() , lastView );
					if( workspace.page_index >= workspace.getPageNum() - 2 - 1 )
					{
						workspace.page_index = workspace.getPageNum() - 2 - 1;
					}
					workspace.setCurrentPage( workspace.page_index + 1 );
				}
				else
				{
					Workspace3D.b_editmode_include_addpage = false;
				}
				Log.v( "cooee" , " Root3D --- startMIUIEditEffect ---- pageOrigy: " + pageOrigy );
				workspace.setFactorAndOrigY( pageOrigy );
				MIUIEffectAnim( true );
			}
		} );
		Gdx.graphics.requestRendering();
	}
	
	public void stopMIUIEditEffect()
	{
		Log.e( "Root3D" , "stopMIUIEditEffect 0" );
		if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode )
		{
			Log.e( "Root3D" , "stopMIUIEditEffect 1" );
			return;
		}
		if( canStartAnim() == false )
		{
			Log.e( "Root3D" , "stopMIUIEditEffect 2" );
			return;
		}
		if( findView( "MiuiFreeMain" ) != null )
		{
			Log.e( "Root3D" , "stopMIUIEditEffect 3 " );
			return;
		}
		// xiatian add start //HotSeat3DShake
		if( DefaultLayout.enable_HotSeat3DShake )
		{
			hotseatBar.setShake();
		}
		// xiatian add end
		Log.e( "Root3D" , "stopMIUIEditEffect 4 workspace.touchable:" + workspace.touchable );
		if( workspace.findViewNew( "firstView" ) != null )
		{
			workspace.removePage( firstView );
		}
		if( workspace.findViewNew( "lastView" ) != null )
		{
			workspace.removePage( lastView );
		}
		workspace.stopAutoEffect();
		if( Workspace3D.b_editmode_include_addpage == true )
		{
			if( workspace.page_index == 0 )
			{
				workspace.setCurrentPage( 0 );
			}
			else
			{
				workspace.setCurrentPage( workspace.page_index - 1 );
			}
			iLoongLauncher.getInstance().cleaWidgetStatus( workspace.page_index - 1 );
		}
		else
		{
			Workspace3D.b_editmode_include_addpage = true;
		}
		Workspace3D.WorkspaceStatus = WorkspaceStatusEnum.NormalMode;
		Workspace.WorkspaceStatus = WorkspaceStatusEnum.NormalMode;
		SendMsgToAndroid.sendShowWorkspaceMsg();
		workspace.setFactorAndOrigY( Utils3D.getScreenHeight() / 2 );
		if( ( DefaultLayout.show_music_page == false ) || ( DefaultLayout.enable_news == false ) || ( DefaultLayout.enable_camera == false ) )
		{
			MIUIEffectAnim( false );
		}
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				//xujin
				if( DefaultLayout.enable_camera )
				{
					workspace.addCameraView();
				}
				if( DefaultLayout.show_music_page )
				{
					workspace.addMusicView();
				}
				MIUIEffectAnim( false );
			}
		} );
		Gdx.graphics.requestRendering();
		Messenger.sendMsg( Messenger.MSG_MOVE_IN_MTK3DWIDGET , null );
	}
	
	private void stopMIUIEditEffectNoAnim()
	{
		if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode )
		{
			return;
		}
		if( canStartAnim() == false )
		{
			return;
		}
		if( findView( "MiuiFreeMain" ) != null )
		{
			Log.e( "Root3D" , "stopMIUIEditEffect 3 " );
			return;
		}
		Workspace3D.WorkspaceStatus = WorkspaceStatusEnum.NormalMode;
		Workspace.WorkspaceStatus = WorkspaceStatusEnum.NormalMode;
		workspace.setFactorAndOrigY( Utils3D.getScreenHeight() / 2 );
		if( workspace.findViewNew( "firstView" ) != null )
		{
			workspace.removePage( firstView );
		}
		if( workspace.findViewNew( "lastView" ) != null )
		{
			workspace.removePage( lastView );
		}
		if( workspace.page_index == 0 )
		{
			workspace.setCurrentPage( 0 );
		}
		else
		{
			workspace.setCurrentPage( workspace.page_index - 1 );
		}
		iLoongLauncher.getInstance().cleaWidgetStatus( workspace.page_index - 1 );
		hotseatBar.color.a = 1f;
		workspace.color.a = 1f;
		pageIndicator.color.a = 1f;
		hotseatBar.show();
		hotseatBar.setPosition( 0 , 0 );
		hotseatBar.setZ( 0f );
		for( int i = 0 ; i < workspace.getChildCount() ; i++ )
		{
			View3D cell = workspace.getChildAt( i );
			if( cell instanceof CellLayout3D )
			{
				cell.setScale( 1.0f , 1.0f );
			}
		}
		pageIndicator.setPosition( pageIndicator.x , R3D.page_indicator_y );
		pageIndicator.setZ( 0f );
		if( dragLayer.isVisible() && !dragLayer.draging )
		{
			dragLayer.removeAllViews();
			dragLayer.hide();
		}
		if( trashIcon.isVisible() )
		{
			trashIcon.hide();
		}
		float effectHeight = widgetHost.height;
		if( effectHeight < hotseatBar.height )
		{
			effectHeight = hotseatBar.height;
		}
		widgetHost.hide();
		widgetHost.setPosition( 0 , -effectHeight * 1.5f );
		widgetHost.setZ( 0f );
		launcher.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				//xujin
				if( DefaultLayout.enable_camera )
				{
					workspace.addCameraView();
				}
				if( DefaultLayout.show_music_page )
				{
					workspace.addMusicView();
				}
			}
		} );
		Gdx.graphics.requestRendering();
		Messenger.sendMsg( Messenger.MSG_MOVE_IN_MTK3DWIDGET , null );
	}
	
	private void setDragS3EffectScale()
	{
		if( Utils3D.getScreenHeight() < 700 )
		{
			Log.v( "test123" , "0.95f" );
			s3EffectScale = 0.9f;
		}
		else
		{
			Log.v( "test123" , "0.8f" );
			s3EffectScale = 0.8f;
		}
	}
	
	public void startDragS3Effect()
	{
		Workspace3D.is_longKick = true;
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
		}
		else
		{
		}
	}
	
	public void stopDragS3Effect(
			boolean is_delete )
	{
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
		}
		else
		{
			s3EffectTween = workspace.startTween( View3DTweenAccessor.USER , Cubic.OUT , workspaceTweenDuration , 0f , 0f , 0f ).setUserData( DRAG_END ).setCallback( this );
		}
	}
	
	public void startDragScrollIndicatorEffect()
	{
		Workspace3D.is_longKick = true;
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
		}
		else
		{
			workspace.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , workspaceTweenDuration , s3EffectScale , s3EffectScale , 0f );
		}
		workspace.setUser2( 0 );
		workspace.startTween( View3DTweenAccessor.USER2 , Cubic.OUT , workspaceTweenDuration , 1 , 0f , 0f );
		if( DefaultLayout.show_music_page )
		{
			musicSeat.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0.3f , 0 , 0 , 0 );
		}
		//xujin
		if( DefaultLayout.enable_camera && cameraSeat != null )
		{
			cameraSeat.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0.3f , 0 , 0 , 0 );
		}
	}
	
	public void stopDragScrollIndicatorEffect(
			boolean is_delete )
	{
		workspace.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , workspaceTweenDuration , 1f , 1f , 0f );
		workspace.setUser2( 1f );
		if( DefaultLayout.show_music_page )
		{
			if( workspace.curIsMusicView() )
			{
				musicSeat.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0.3f , 1 , 0 , 0 );
			}
		}
		//xujin
		if( DefaultLayout.enable_camera )
		{
			if( workspace.curIsCameraView() && cameraSeat != null )
			{
				cameraSeat.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0.3f , 1 , 0 , 0 );
			}
		}
		s4ScrollEffectTween = workspace.startTween( View3DTweenAccessor.USER2 , Cubic.OUT , workspaceTweenDuration , 0f , 0f , 0f ).setUserData( DRAG_END ).setCallback( this );
	}
	
	private void launcherGlobalSearch()
	{
		Intent intent = new Intent();
		intent.setAction( "android.search.action.GLOBAL_SEARCH" );
		List<ResolveInfo> allMatches = launcher.getPackageManager().queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
		if( allMatches == null || allMatches.size() == 0 )
		{
			return;
		}
		else
		{
			String pkgName = "com.baidu.searchbox";
			for( ResolveInfo ri : allMatches )
			{
				if( ri.activityInfo.applicationInfo.packageName.equals( pkgName ) )
				{
					ComponentName com = new ComponentName( ri.activityInfo.applicationInfo.packageName , ri.activityInfo.name );
					intent.setComponent( com );
					break;
				}
			}
		}
		iLoongLauncher.getInstance().startActivity( intent );
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		// teapotXu add start for add new folder in top-trash bar
		if( DefaultLayout.generate_new_folder_in_top_trash_bar )
		{
			if( event_id == MSG_SET_TRASH_POS )
			{
				if( sender instanceof Icon3D || sender instanceof Contact3DShortcut )
				{
					trashIcon.setPosFlag( TrashIcon3D.TRASH_POS_TOP_FLAG_RIGHT );
					FolderTarget3D.setVisibleFlag( FolderTarget3D.FLAG_VISIBLE );
				}
				else
				{
					trashIcon.setPosFlag( TrashIcon3D.TRASH_POS_TOP_FLAG_MIDDLE );
					FolderTarget3D.setVisibleFlag( FolderTarget3D.FLAG_INVISIBLE );
				}
			}
		}
		// teapotXu add end
		if( sender instanceof HotSeat3D )
		{
			switch( event_id )
			{
				case CellLayout3D.MSG_REFRESH_PAGE:
					Log.v( "focus" , "CellLayout3D.MSG_REFRESH_PAGE" );
					Object refreshObj = sender.getTag();
					if( refreshObj instanceof Integer )
					{
						int tag = (Integer)refreshObj;
					}
					break;
			}
		}
		if( sender instanceof DragSource3D )
		{
			DragSource3D source = (DragSource3D)sender;
			switch( event_id )
			{
				case DragSource3D.MSG_START_DRAG:
					if( !pageContainer.isVisible() )
					{
						// teapotXu add start for Folder in Mainmenu
						if( DefaultLayout.mainmenu_folder_function == true && appHost.getAppList3DMode() == AppList3D.APPLIST_MODE_UNINSTALL )
						{
							// 在AppList中不需要S3 Effect
						}
						else
						{
							// teapotXu add end for Folder in Mainmenu
							startDragS3Effect();
							if( DefaultLayout.enable_takein_workspace_by_longclick )
							{
								if( Workspace3D.isHideAll )
								{
									Workspace3D.isHideAll = false;
									workspace.initView();
									hotseatBar.getDockGroup().setTakeinBitmap();
								}
							}
						}
					}
					Object obj = source.getTag();
					if( !( obj instanceof Vector2 ) )
						break;
					dragLayer.show();
					Vector2 vec = (Vector2)obj;
					ArrayList<View3D> list = source.getDragList();
					for( View3D view : list )
					{
						if( view instanceof DropTarget3D )
						{
							dragLayer.removeDropTarget( (DropTarget3D)view );
						}
					}
					/************************ added by zhenNan.ye begin ***************************/
					if( DefaultLayout.enable_particle )
					{
						if( ParticleManager.particleManagerEnable )
						{
							if( sender instanceof Workspace3D || sender instanceof AppList3D )
							{
								float positionX , positionY;
								View3D startDragView = list.get( 0 );
								if( list.size() > 1 )
								{
									float iconHeight = Utils3D.getIconBmpHeight();
									positionX = vec.x + startDragView.width / 2;
									positionY = vec.y + ( startDragView.height - iconHeight ) + iconHeight / 2;
								}
								else
								{
									if( startDragView instanceof WidgetView /*
																			 * ||
																			 * startDragView
																			 * instanceof
																			 * Widget3D
																			 */
											|| startDragView instanceof Widget )
									{
										positionX = vec.x + startDragView.width / 2;
										positionY = vec.y + startDragView.height / 2;
									}
									else
									{
										float iconHeight = Utils3D.getIconBmpHeight();
										positionX = vec.x + startDragView.width / 2;
										positionY = vec.y + ( startDragView.height - iconHeight ) + iconHeight / 2;
									}
								}
								startDragView.startParticle( ParticleManager.PARTICLE_TYPE_NAME_START_DRAG , positionX , positionY );
							}
						}
					}
					/************************ added by zhenNan.ye end ***************************/
					dragLayer.startDrag( list , vec.x , vec.y );
					// teapotXu add start for Folder in Mainmenu
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						// init this flag
						dragLayer.is_dragging_in_apphost = false;
					}
					// teapotXu add end for Folder in Mainmenu
					if( sender instanceof Workspace3D || sender instanceof HotSeat3D || sender instanceof MIUIWidgetList || sender instanceof RecentAppPage )
					{
						appHost.hide();
						pageContainer.hide();
						if( !workspace.isVisible() )
							workspace.show();
						if( !( sender instanceof RecentAppPage ) )
							trashIcon.show();
						// teapotXu add start
						{
							if( folderTarget != null )
								folderTarget.show();
						}
						// teapotXu add end
						if( sender instanceof HotSeat3D )
						{
							hotseatBar.startMainGroupOutDragAnim();
						}
					}
					else if( sender instanceof AppList3D )
					{
						AppList3D appList3D_sender = (AppList3D)sender;
						// teapotXu add start for Folder in Mainmenu
						if( DefaultLayout.mainmenu_folder_function == true && appHost.isVisible() == true && appHost.getAppList3DMode() == AppList3D.APPLIST_MODE_UNINSTALL )
						{
							// this condition the Icon will drag in AppHost
							dragLayer.is_dragging_in_apphost = true;
							// appHost must be added before FolderIcons which belong
							// to Applist
							ArrayList<DropTarget3D> dropTagList = dragLayer.getDropTargetList();
							if( dropTagList != null )
							{
								boolean is_dropTagList_including_Mainmenu_folder = false;
								for( int index = 0 ; index < dropTagList.size() ; index++ )
								{
									DropTarget3D dropTag = dropTagList.get( index );
									if( dropTag instanceof FolderIcon3D && ( (FolderIcon3D)dropTag ).getParent() instanceof GridView3D )
									{
										dragLayer.addDropTargetBefore( dropTag , (DropTarget3D)appHost );
										is_dropTagList_including_Mainmenu_folder = true;
										break;
									}
								}
								if( is_dropTagList_including_Mainmenu_folder == false )
								{
									dragLayer.addDropTarget( (DropTarget3D)appHost );
								}
							}
						}
						else
						// teapotXu add end for Folder in Mainmenu
						{
							appHost.hide();
							pageContainer.hide();
							workspace.show();
							hotseatBar.show();
							hotseatBar.setVisible( true );
							hotseatBar.touchable = true;
							trashIcon.show();
							if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
							{
								// teapotXu add start for add new folder in
								// top-trash bar
								if( DefaultLayout.generate_new_folder_in_top_trash_bar )
								{
									if( folderTarget != null )
									{
										folderTarget.show();
									}
								}
								// teapotXu add end
							}
							else
							{
								trashIcon.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , trashIcon.x , 0 , 0 );
							}
							pageIndicator.show();
							if( DefaultLayout.enable_new_particle )
							{
								TimerTask task = new TimerTask() {
									
									public void run()
									{
										ParticleManager manager = ParticleManager.getParticleManager();
										manager.stopAllAnims();
									}
								};
								Timer timer = new Timer();
								timer.schedule( task , 500 );
							}
							Utils3D.changeStatusbar( "topwisemenu" , false , false );
							Utils3D.changeStatusbar( "topwiseidle" , true , true );
						}
					}
					else if( sender instanceof PageEdit3D )
					{
						// trashIcon.show();
					}
					else if( sender instanceof Folder3D )
					{
						// Folder3D.getInstance().DealButtonOKDown();
					}
					if( !( sender instanceof RecentAppPage ) )
						SendMsgToAndroid.vibrator( R3D.vibrator_duration );
					return true;
			}
		}
		if( sender instanceof Icon3D )
		{
			Icon3D icon = (Icon3D)sender;
			switch( event_id )
			{
				case Icon3D.MSG_ICON_CLICK:
					ItemInfo tag = icon.getItemInfo();
					if( tag instanceof ShortcutInfo )
					{
						// Open shortcut
						workspace.setCurIcon( icon );
						Vector2 IconPoint = new Vector2();
						final Intent intent = ( (ShortcutInfo)tag ).intent;
						icon.toAbsoluteCoords( IconPoint );
						Rect rect = new Rect(
								(int)IconPoint.x ,
								(int)( Utils3D.getScreenHeight() - IconPoint.y - icon.getHeight() ) ,
								(int)( IconPoint.x + icon.getWidth() ) ,
								(int)( Utils3D.getScreenHeight() - IconPoint.y ) );
						intent.setSourceBounds( rect );
						if( intent.getAction() != null && intent.getAction().equals( Intent.ACTION_CREATE_SHORTCUT ) )
						{
							SendMsgToAndroid.sendSelectShortcutMsg( intent );
						}
						else if( intent.getAction() != null && intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
						{
							ContactsContract.QuickContact.showQuickContact( launcher , rect , intent.getData() , ContactsContract.QuickContact.MODE_SMALL , null );
						}
						else
						{
							if( tag.container == ItemInfo.NO_ID && intent.getComponent() != null && intent.getComponent().getPackageName().equals( launcher.getPackageName() ) && intent.getComponent()
									.getClassName().equals( launcher.getComponentName().getClassName() ) )
							{
								if( DefaultLayout.mainmenu_inout_no_anim )
								{
									showWorkSpaceFromAllAppEx();
								}
								else
								{
									showWorkSpaceFromAllApp();
								}
							}
							else if( tag.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT && intent.getComponent() != null && intent.getComponent().getPackageName()
									.equals( launcher.getPackageName() ) && intent.getComponent().getClassName().equals( launcher.getComponentName().getClassName() ) )
							{
								showPageContainer( 0 );
							}
							else
							{
								if( tag.title.equals( iLoongLauncher.getInstance().getResources().getString( RR.string.mainmenu ) ) )
								{
									if( DefaultLayout.mainmenu_inout_no_anim )
									{
										showAllAppFromWorkspaceEx();
									}
									else
									{
										showAllAppFromWorkspace();
									}
								}
								else
								{
									SendMsgToAndroid.startActivity( tag );
								}
							}
						}
					}
					return true;
			}
		}
		if( sender instanceof DragLayer3D )
		{
			DragLayer3D dragLayer = (DragLayer3D)sender;
			switch( event_id )
			{
			// xiatian add start //for mainmenu sort by user
				case DragLayer3D.MSG_DRAG_MOVE_2_WORKSPACE_FROM_APPLIST:
				{
					if( DefaultLayout.mainmenu_sort_by_user_fun )
					{
						appHost.setModeOnly( AppList3D.APPLIST_MODE_NORMAL );
						DragView3D mDragView3D = (DragView3D)dragLayer.getChildAt( 0 );
						for( int index = 0 ; index < mDragView3D.getChildCount() ; index++ )
						{
							View3D mView3D = (View3D)mDragView3D.getChildAt( index );
							if( mView3D instanceof Icon3D )
							{
								Icon3D mIcon3D = (Icon3D)mView3D;
								mIcon3D.clearState();
								AppList3D.needAddDragViewBackToFolder = true;
								AppHost3D.appList.viewBackToFolder( mIcon3D );
							}
							else if( mView3D instanceof FolderIcon3D )
							{
								FolderIcon3D mFolderIcon3D = (FolderIcon3D)mView3D;
								mFolderIcon3D.clearState();
								AppHost3D.appList.resetAppIconsStatusInFolder( mFolderIcon3D );
							}
						}
						AppHost3D.appList.syncAppsPages();
						appHost.hide();
						startDragS3Effect();
						pageContainer.hide();
						workspace.show();
						hotseatBar.show();
						hotseatBar.setVisible( true );
						hotseatBar.touchable = true;
						trashIcon.show();
						if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
						{
							// teapotXu add start for add new folder in top-trash
							// bar
							if( DefaultLayout.generate_new_folder_in_top_trash_bar )
							{
								if( folderTarget != null )
								{
									folderTarget.show();
								}
							}
							// teapotXu add end
						}
						else
						{
							trashIcon.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.6f , trashIcon.x , 0 , 0 );
						}
						pageIndicator.show();
						return true;
					}
					return false;
				}
				// xiatian add end
				case DragLayer3D.MSG_DRAG_INBORDER:
					// teapotXu add start for Folder in Mainmenu
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						if( appHost.isVisible() == true && dragLayer.is_dragging_in_apphost == true )
						{
							appHost.onCtrlEvent( sender , DragLayer3D.MSG_DRAG_INBORDER );
						}
						else
							showPageContainer( -1 );
					}
					else
						// teapotXu add end for Folder in Mainmenu
						showPageContainer( -1 );
					break;
				case DragLayer3D.MSG_DRAG_OVER:
				{
					if( !Workspace3D.isRecentAppVisible() )
						dragLayer.setColor( 1f , 1f , 1f , 0.8f );
					// teapotXu add start for Folder in Mainmenu
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						dragLayer.is_dragging_in_apphost = false;
					}
					// teapotXu add end for Folder in Mainmenu
					if( workspace.isVisible() == true )
					{
						DropTarget3D target = (DropTarget3D)dragLayer.getTag();
						if( target instanceof FolderIcon3D )
						{
							dragLayer.setColor( 0f , 1f , 0f , 1f );
						}
						if( target instanceof TrashIcon3D )
						{
							dragLayer.setColor( 1f , 0f , 0f , 0.5f );
							trashIcon.set( true );
						}
						else
						{
							trashIcon.set( false );
						}
						// teapotXu add start for add folder in top-trash bar
						if( target instanceof FolderTarget3D )
						{
							dragLayer.setColor( 0f , 0.5f , 0.5f , 0.5f );
							if( folderTarget != null )
								folderTarget.set( true );
						}
						else
						{
							if( folderTarget != null )
								folderTarget.set( false );
						}
						// teapotXu add end
						if( !( target instanceof Workspace3D ) )
						{
							boolean belongCurCellLayout = false;
							CellLayout3D group = workspace.getCurrentCellLayout();
							if( group != null )
							{
								int childCount = group.getChildCount();
								for( int i = 0 ; i < childCount ; i++ )
								{
									View3D child = group.getChildAt( i );
									if( child == target )
									{
										belongCurCellLayout = true;
										break;
									}
								}
							}
							if( DefaultLayout.enable_workspace_push_icon && belongCurCellLayout && dragLayer.getDragList().size() <= 1 )
							{
							}
							else
							{
								workspace.onDragOverLeave();
							}
						}
						if( !( target instanceof HotSeat3D ) )
						{
							hotseatBar.onDragOverLeave();
						}
					}
					else
					{
						// teapotXu add start for Folder in Mainmenu
						if( DefaultLayout.mainmenu_folder_function == true )
						{
							if( appHost.isVisible() == true && appHost.getAppList3DMode() == AppList3D.APPLIST_MODE_UNINSTALL )
							{
								// appHost is shown
								DropTarget3D target = (DropTarget3D)dragLayer.getTag();
								dragLayer.is_dragging_in_apphost = true;
								if( !( target instanceof AppHost3D ) )
								{
									appHost.onDropLeave();
								}
								return true;
							}
						}
						// teapotXu add end for Folder in Mainmenu
					}
					return true;
				}
				case DragLayer3D.MSG_DRAG_END:
					// zhujieping add
					if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable )
					{
						isDragEnd = false;
					}
					DropTarget3D target = dragLayer.getTargetOver();
					// teapotXu add start for Folder in Mainmenu
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						// when drag end, reset this flag
						dragLayer.is_dragging_in_apphost = false;
					}
					if( target instanceof TrashIcon3D )
					{
						is_delete = true;
					}
					else
					{
						is_delete = false;
					}
					if( pageContainer.isVisible() )
					{
						workspace.setScale( 1 , 1 );
						hotseatBar.setScale( 1 , 1 );
						Workspace3D.is_longKick = false;
						if( pageContainer.pageEdit.isVisible() )
						{
							dragLayer.onDrop();
							dragLayer.removeAllViews();
							dragLayer.hide();
						}
						else
						{
							if( pageContainer.pageSelect.animatingShow )
								pageContainer.pageSelect.needHide = true;
							else
								pageContainer.hide( true );
						}
					}
					// teapotXu add start for Folder in Mainmenu
					else if( DefaultLayout.mainmenu_folder_function == true && appHost.isVisible() && appHost.getAppList3DMode() == AppList3D.APPLIST_MODE_UNINSTALL )
					{
						// when folder is not open,
						if( !appHost.folderOpened )
						{
							// xiatian add start //for mainmenu sort by user
							if( DefaultLayout.mainmenu_sort_by_user_fun )
							{
								appHost.dragEnd();
							}
							// xiatian add end
							DropTarget3D dropTarget = dragLayer.onDrop();
							// 如果此时拖动的icon放置到AppList里边的Folder时
							Log.v( "cooee" , "Root3D ---- MSG_DRAG_END --- drop in Mainmenu ---" );
							if( dropTarget != null )
							{
								// 如果此时拖动的icon放置到AppList里边的Folder时
								if( dropTarget instanceof FolderIcon3D && ( (View3D)dropTarget ).getParent() instanceof GridView3D )
								{
									// 此时需要在AppList中Remove掉拖动到文件夹里边的ICON，并重新排序
									if( ( (FolderIcon3D)dropTarget ).getOnDrop() == true )
									{
										Log.v( "cooee" , "Root3D ---- MSG_DRAG_END --- move icon into folder in Mainmenu --- " );
										ArrayList<View3D> dragViewList = dragLayer.getDragList();
										// xiatian start //for mainmenu sort by user
										appHost.removeDragViews( dragViewList , true );// xiatian add
										// xiatian end
									}
									//xiatian add start //for mainmenu sort by user
									else
									{
										if( DefaultLayout.mainmenu_sort_by_user_fun )
										{
											appHost.appList.sortApp( appHost.appList.sortId , true );
										}
									}
									//xiatian add end
								}
								else
								{
									Log.v( "cooee" , "Root3D ---- MSG_DRAG_END --- move icon in Mainmenu --- dropTarget: " + dropTarget.toString() );
								}
							}
							else
							{
								Log.e( "cooee" , "Root3D ---- MSG_DRAG_END --- drop in Mainmenu  target == null ---error---" );
							}
							appHost.setDragviewAddFromFolderStatus( false );
							dragLayer.removeAllViews();
							dragLayer.hide();
						}
					}
					else
					{
						boolean isdelete = true;
						appHost.hide();
						if( true )
						{
							workspace.show();
							if( dragLayer.getDragList().size() > 0 )
							{
								if( newsHandle != null )
								{
									if( dragLayer.getDragList().get( 0 ).width == newsHandle.width && dragLayer.getDragList().get( 0 ).height == newsHandle.height )
									{
										addView( newsHandle );
										if( View3D.point.x <= UtilsBase.getScreenWidth() / 2 )
										{
											newsHandle.setPosition( 0 , View3D.point.y - newsHandle.height / 2 );
											newsHandle.region = newsHandle.mLeftHandle;
											Vector2 v = new Vector2( 0 , UtilsBase.getScreenHeight() - View3D.point.y - newsHandle.height / 2 - UtilsBase.getStatusBarHeight() );
											Messenger.sendMsg( Messenger.MSG_CHANGE_NEWS_HANDLE_POS , v );
											newsHandle.isDragging = false;
										}
										else
										{
											newsHandle.setPosition( UtilsBase.getScreenWidth() - newsHandle.width , View3D.point.y - newsHandle.height / 2 );
											newsHandle.region = newsHandle.mRightHandle;
											Vector2 v = new Vector2(
													UtilsBase.getScreenWidth() - newsHandle.width ,
													UtilsBase.getScreenHeight() - View3D.point.y - newsHandle.height / 2 - UtilsBase.getStatusBarHeight() );
											Messenger.sendMsg( Messenger.MSG_CHANGE_NEWS_HANDLE_POS , v );
											newsHandle.isDragging = false;
										}
										dragLayer.removeAllViews();
										dragLayer.hide();
										stopDragS3Effect( is_delete );
										return true;
									}
								}
								DropTarget3D tmpTarget = dragLayer.onDrop();
								if( tmpTarget == null )
								{
									hotseatBar.getDockGroup().removeVirtueFolderIcon();
									dropListBacktoOrig( dragLayer.getDragList() );
								}
								else
								{
									if( target instanceof HotSeat3D && !target.equals( tmpTarget ) )
									{
										hotseatBar.getDockGroup().removeVirtueFolderIcon();
									}
									if( folder != null && target instanceof FolderIcon3D && target.equals( folder ) && target.equals( tmpTarget ) )
									{
										System.out.println( "folder " );
										isdelete = false;
									}
								}
								// teapotXu add start for widget scrolling
								for( View3D dragView : dragLayer.getDragList() )
								{
									if( dragView instanceof Widget3D )
									{
										Object widgetTag2 = ( (Widget3D)dragView ).getWidgetTag2();
										if( widgetTag2 instanceof String && widgetTag2.equals( Widget3D.WIDGET3D_NEED_SCROLL_STR ) )
										{
											( (Widget3D)dragView ).onStartWidgetAnimation( null , Widget3D.WIDGET_ANIMATION_TYPE_ENTRY_FROM_MAINMENU , Widget3D.WIDGET_ANIMATION_DIRECTION_NONE );
											( (Widget3D)dragView ).setWidgetTag2( null );
										}
									}
								}
								// teapotXu add end
							}
							else if( dragLayer.getDragList().size() > 0 && target instanceof HotSeat3D )
							{
								DropTarget3D tmpTarget = dragLayer.onDrop();
								if( !( tmpTarget instanceof HotSeat3D ) )
								{
									hotseatBar.getDockGroup().removeVirtueFolderIcon();
								}
							}
							if( DefaultLayout.rapidly_remove_shortcut && dragLayer.isRecycled )
							{
								float[] pos = dragLayer.getTargetXY();
								dragLayer.dragView.onRecycled( pos[0] , pos[1] , OnRemoveCallBackImpl );
							}
							else
							{
								dragLayer.removeAllViews();
								dragLayer.hide();
							}
							if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
							{
							}
							else
							{
								hotseatBar.show();
							}
							pageIndicator.show();
							stopDragS3Effect( is_delete );
						}
						if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
						{
							if( DefaultLayout.rapidly_remove_shortcut && dragLayer.isRecycled )
							{
							}
							else
							{
								trashIcon.hide();
							}
							// teapotXu add start for add new folder in top-trash
							// bar
							if( DefaultLayout.generate_new_folder_in_top_trash_bar )
							{
								if( folderTarget != null )
								{
									folderTarget.hide();
								}
							}
							// teapotXu add end
						}
					}
					return true;
			}
		}
		if( sender instanceof AppList3D )
		{
			switch( event_id )
			{
				case AppList3D.APP_LIST3D_KEY_BACK:
					if( DefaultLayout.mainmenu_inout_no_anim )
					{
						showWorkSpaceFromAllAppEx();
					}
					else
					{
						showWorkSpaceFromAllApp();
					}
					return true;
				case AppList3D.APP_LIST3D_SHOW:
					if( DefaultLayout.mainmenu_inout_no_anim )
					{
						pageIndicator.hideEx();
					}
					else
					{
						pageIndicator.hide();
					}
					return true;
				case AppList3D.APP_LIST3D_HIDE:
					if( DefaultLayout.mainmenu_inout_no_anim )
					{
						pageIndicator.showEx();
					}
					else
					{
						pageIndicator.show();
					}
					return true;
					// teapotXu add start for Folder in Mainmenu
				case AppList3D.MSG_ADD_DRAGLAYER:
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						AppList3D appList3D_sender = (AppList3D)sender;
						dragLayer.addDropTarget( (DropTarget3D)appList3D_sender.drageTarget_new_child );
						return true;
					}
					break;
			// teapotXu add end for Folder in Mainmenu
			}
		}
		if( sender instanceof TabPluginHost3D )
		{
			switch( event_id )
			{
				case TabPluginHost3D.TABPLUGIN_HOST_SHOW:
					if( DefaultLayout.mainmenu_inout_no_anim )
					{
						pageIndicator.hideEx();
					}
					else
					{
						pageIndicator.hide();
					}
					return true;
				case TabPluginHost3D.TABPLUGIN_HOST_HIDE:
					if( DefaultLayout.mainmenu_inout_no_anim )
					{
						pageIndicator.showEx();
					}
					else
					{
						pageIndicator.show();
					}
					return true;
				case TabPluginHost3D.TABPLUGIN_HOST_KEY_BACK:
					if( DefaultLayout.mainmenu_inout_no_anim )
					{
						showWorkSpaceFromAllAppEx();
					}
					else
					{
						showWorkSpaceFromAllApp();
					}
					return true;
			}
		}
		if( sender instanceof HotGridView3D )
		{
			HotGridView3D hotGrid = (HotGridView3D)sender;
			switch( event_id )
			{
				case HotGridView3D.MSG_ADD_DRAGLAYER:
					dragLayer.addDropTarget( (DropTarget3D)hotGrid.getTag() );
					return true;
			}
		}
		if( sender instanceof Workspace3D )
		{
			Workspace3D workspace = (Workspace3D)sender;
			switch( event_id )
			{
				case Workspace3D.MSG_FINISH_EFFECT:
					pageIndicator.finishAutoEffect();
					// teapotXu add start for widget scroll
					ViewGroup3D cur_page_view = (ViewGroup3D)workspace.getCurrentView();
					if( cur_page_view != null )
					{
						for( int i = 0 ; i < cur_page_view.getChildCount() ; i++ )
						{
							View3D child = cur_page_view.getChildAt( i );
							if( child instanceof Widget3D )
							{
								if( APageEase.is_scroll_from_right_to_left == true )
								{
									( (Widget3D)child ).onStartWidgetAnimation( null , Widget3D.WIDGET_ANIMATION_TYPE_SCROLL , Widget3D.WIDGET_ANIMATION_DIRECTION_TO_LEFT );
								}
								else
								{
									( (Widget3D)child ).onStartWidgetAnimation( null , Widget3D.WIDGET_ANIMATION_TYPE_SCROLL , Widget3D.WIDGET_ANIMATION_DIRECTION_TO_RIGHT );
								}
							}
							else if( child instanceof Widget )
							{
								if( DefaultLayout.enable_haocheng_sys_widget_anim )
								{
									Widget mWidget = (Widget)child;
									if( mWidget.getItemInfo() != null )
									{
										ComponentName cmpName = new ComponentName( mWidget.getItemInfo().getPackageName() , mWidget.getItemInfo().getClassName() );
										Intent intent1 = new Intent( "hct.appwidget.action.APPWIDGET_ANIMATION" );
										intent1.putExtra( "provider" , cmpName );
										intent1.putExtra( "state" , 1 );// 0:create; 1:
																		// slide
										iLoongLauncher.getInstance().sendBroadcast( intent1 );
									}
								}
							}
						}
					}
					// teapotXu add end
					if( DefaultLayout.idlePageTransformOverIntentList.size() > 0 )
					{
						for( String intentAction : DefaultLayout.idlePageTransformOverIntentList )
						{
							if( intentAction != null )
							{
								Intent intent = new Intent();
								intent.setAction( intentAction );
								iLoongLauncher.getInstance().sendBroadcast( intent );
							}
						}
					}
					break;
				case Workspace3D.MSG_LONGCLICK:
				{
					if( DefaultLayout.enable_workspace_miui_edit_mode )
					{
						if( Desktop3DListener.bDesktopDone == false )
						{
							return true;
						}
						if( DefaultLayout.enable_edit_mode_function && Root3D.IsProhibiteditMode == true )
						{
							return true;
						}
						if( DefaultLayout.enable_release_2Dwidget && !AppList3D.hasbind2Dwidget && iLoongApplication.BuiltIn )
						{
							AppHost3D.appList.bind2DWidget();
						}
						if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
						{
							stopMIUIEditEffect();
							if( DesktopEditHost.getInstance() != null )
							{
								DesktopEditHost.getInstance().recyle();
								if( DesktopEditHost.curPopMenuStyle == DesktopEditHost.POP_MENU_STYLE_4X2 )
								{
									DesktopEditHost.getInstance().menuContainer.hide();
									DesktopEditHost.getInstance().mulpMenuHost.hide();
									DesktopEditHost.curPopMenuStyle = DesktopEditHost.POP_MENU_STYLE_4X1;
								}
							}
						}
						else
						{
							UmengMobclickAgent.FirstTime( launcher , "FirstTimeDesktopEditModel" );
							startMIUIEditEffect();
							DesktopEditHost.popup( this );
							DesktopEditHost.getInstance().mulpMenuHost.MenuCallBack( 0 );
						}
						SendMsgToAndroid.vibrator( R3D.vibrator_duration );
					}
					else if( DefaultLayout.enable_desktop_longclick_to_add_widget )
					{
						SendMsgToAndroid.vibrator( R3D.vibrator_duration );
						showWidgetListFromWorkspace();
					}
					else
					{
						Object obj = workspace.getTag();
						if( obj instanceof Vector2 )
							SendMsgToAndroid.sendAddShortcutMsg( (int)( (Vector2)obj ).x , (int)( (Vector2)obj ).y );
						else
							SendMsgToAndroid.sendAddShortcutMsg( 0 , 0 );
						SendMsgToAndroid.vibrator( R3D.vibrator_duration );
					}
					return true;
				}
				case Workspace3D.MSG_ADD_DRAGLAYER:
					dragLayer.addDropTargetBefore( pageContainer.pageEdit , (DropTarget3D)workspace.getTag() );
					return true;
				case Workspace3D.MSG_DEL_POP_ALL:
					// teapotXu add start for add new folder in top-trash bar
					if( DefaultLayout.generate_new_folder_in_top_trash_bar )
					{
						if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
						{
							trashIcon.setPosFlag( TrashIcon3D.TRASH_POS_TOP_FLAG_MIDDLE );
							FolderTarget3D.setVisibleFlag( FolderTarget3D.FLAG_INVISIBLE );
						}
					}
					// teapotXu add end
					trashIcon.show();
					if( folderTarget != null )
					{
						folderTarget.show();
					}
					timeline = Timeline.createParallel();
					ArrayList<View3D> toDeleteList = (ArrayList<View3D>)workspace.getTag();
					for( int i = 0 ; i < toDeleteList.size() ; i++ )
					{
						View3D view = toDeleteList.get( i );
						timeline.push( view.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , Utils3D.getScreenWidth() / 2 + trashIcon.getWidth() / 4 , Utils3D.getScreenHeight() , 0 ) );
					}
					trashIcon.setTag( toDeleteList );
					timeline.setCallback( this ).start( View3DTweenAccessor.manager );
					return true;
				case Workspace3D.MSG_ADD_WIDGET:
					Widget2DShortcut widget = (Widget2DShortcut)workspace.getTag();
					if( widget.getTag() instanceof int[] )
					{
						int[] pos = (int[])widget.getTag();
						widget.x = pos[0];
						widget.y = pos[1];
					}
					SendMsgToAndroid.addWidgetFromAllApp( widget.widgetInfo.component , (int)widget.x , (int)widget.y );
					return true;
				case Workspace3D.MSG_ADD_SHORTCUT:
					Widget2DShortcut shortcut = (Widget2DShortcut)workspace.getTag();
					int[] pos = (int[])shortcut.getTag();
					SendMsgToAndroid.addShortcutFromAllApp( shortcut.widgetInfo.component , pos[0] , pos[1] );
					return true;
				case Workspace3D.MSG_PAGE_SHOW_EDIT:
					if( workspace.scaleX == 1.0f && dragLayer.isVisible() == false && workspace.xScale == 0 )
					{
						showPageEdit();
					}
					return true;
				case Workspace3D.MSG_CLICK_SPACE:
				{
					long curTime = System.currentTimeMillis();
					int countMax = 10;
					float elpasedTime = 250;
					Log.i( "" , "###### MSG_CLICK_SPACE curTime = " + curTime + ", sysTimeRecord = " + sCurrentTimeMillis + "######" );
					if( sCurrentTimeMillis == 0 )
					{
						sCurrentTimeMillis = curTime;
						Log.i( "" , "###### MSG_CLICK_SPACE sysTimeRecord == 0 ######" );
					}
					if( !sEnableShowFps )
					{
						if( ( curTime - sCurrentTimeMillis ) < elpasedTime )
						{
							if( sClickCount < countMax )
							{
								sClickCount++;
								sCurrentTimeMillis = curTime;
								Log.i( "" , "###### MSG_CLICK_SPACE clickCount++; ######" );
							}
						}
						else
						{
							sClickCount = 0;
							Log.i( "" , "###### MSG_CLICK_SPACE clickCount = 0; ######" );
						}
					}
					else
					{
						if( ( curTime - sCurrentTimeMillis ) < elpasedTime )
						{
							if( sClickCount > 0 )
							{
								sClickCount--;
								sCurrentTimeMillis = curTime;
								Log.i( "" , "###### MSG_CLICK_SPACE clickCount--; ######" );
							}
						}
						else
						{
							sClickCount = countMax;
							Log.i( "" , "###### MSG_CLICK_SPACE clickCount = countMax; ######" );
						}
					}
					if( sClickCount >= countMax )
					{
						sEnableShowFps = true;
						Gdx.graphics.setShowFPS( true );
						sCurrentTimeMillis = 0;
						Log.i( "" , "###### MSG_CLICK_SPACE clickCount >= countMax ######" );
					}
					else if( sClickCount <= 0 )
					{
						sEnableShowFps = false;
						sCurrentTimeMillis = 0;
						Log.i( "" , "###### MSG_CLICK_SPACE clickCount <= 0 ######" );
					}
					Object objclick = workspace.getTag();
					if( objclick instanceof Vector2 )
					{
						SendMsgToAndroid.sendClickToWallPaperMsg( (int)( (Vector2)objclick ).x , (int)( (Vector2)objclick ).y );
					}
					return true;
				}
				case Workspace3D.MSG_GLOBAL_SEARCH:
				{
					launcherGlobalSearch();
					return true;
				}
				// teapotXu add start for longClick in Workspace to editMode as miui
				case Workspace3D.MSG_CHANGE_TO_APPEND_PAGE:
					if( workspace.getTag() instanceof WorkspaceEditView )
					{
						WorkspaceEditView temp = ( (WorkspaceEditView)workspace.getTag() );
						if( workspace.getPageNum() == 2 + DefaultLayout.default_workspace_pagecount_max )
						{
							View3D tempOtherView = null;
							if( temp.isFirstPage() )
							{
								tempOtherView = this.findView( "lastView" );
							}
							else
							{
								tempOtherView = this.findView( "firstView" );
							}
							workspace.removePage( temp );
							workspace.removePage( tempOtherView );
							Workspace3D.b_editmode_include_addpage = false;
							int pageNum = workspace.getPageNum();
							ThemeManager.getInstance().getThemeDB().SaveScreenCount( pageNum );
							Log.v( "create2" , " Root3D MSG_CHANGE_TO_APPEND_PAGE cellNum=" + pageNum );
							if( temp.isFirstPage() )
							{
								workspace.setCurrentPage( 0 );
							}
							else
							{
								workspace.setCurrentPage( workspace.getPageNum() - 1 );
							}
							workspace.addEditModeCellLayout( temp.isFirstPage() );
						}
						else
						{
							int pageNum = workspace.getPageNum() - 2;
							ThemeManager.getInstance().getThemeDB().SaveScreenCount( pageNum );
							Log.v( "create2" , " Root3D MSG_CHANGE_TO_APPEND_PAGE cellNum=" + pageNum );
							if( temp.isFirstPage() )
							{
								workspace.setCurrentPage( 1 );
							}
							else
							{
								workspace.setCurrentPage( workspace.getPageNum() - 2 );
							}
							workspace.addEditModeCellLayout( temp.isFirstPage() );
						}
					}
					break;
				case Workspace3D.MSG_CHANGE_TO_DEL_PAGE:
				{
					View3D delView = (View3D)workspace.getTag();
					if( delView instanceof CellLayout3D )
					{
						if( Workspace3D.b_editmode_include_addpage == false )
						{
							if( firstView == null )
							{
								firstView = new WorkspaceEditView( "firstView" , true );
							}
							if( lastView == null )
							{
								lastView = new WorkspaceEditView( "lastView" , false );
							}
							workspace.addPage( 0 , firstView );
							workspace.addPage( workspace.view_list.size() , lastView );
							Workspace3D.b_editmode_include_addpage = true;
							workspace.page_index++;
						}
						{
							workspace.removePage( delView );
							int pageNum = workspace.getPageNum() - 2;
							ThemeManager.getInstance().getThemeDB().SaveScreenCount( pageNum );
							workspace.setCurrentPage( workspace.page_index );
							workspace.removeEditModeCellLayout( workspace.page_index );
						}
					}
				}
					break;
				case Workspace3D.MSG_CHANGE_TO_NORMAL_MODE:
					if( is_long_click_doing )
					{
						return true;
					}
					if( DefaultLayout.enable_workspace_miui_edit_mode == true )
					{
						workspace.workspacetonromal = true;
						stopMIUIEditEffect();
						if( DesktopEditHost.getInstance() != null )
						{
							DesktopEditHost.getInstance().recyle();
							if( DesktopEditHost.curPopMenuStyle == DesktopEditHost.POP_MENU_STYLE_4X2 )
							{
								DesktopEditHost.getInstance().menuContainer.hide();
								DesktopEditHost.getInstance().mulpMenuHost.hide();
								DesktopEditHost.curPopMenuStyle = DesktopEditHost.POP_MENU_STYLE_4X1;
							}
						}
					}
					return true;
			}
			// teapotXu add end
		}
		if( sender instanceof PageIndicator3D )
		{
			switch( event_id )
			{
				case PageIndicator3D.PAGE_INDICATOR_CLICK:
				case PageIndicator3D.PAGE_INDICATOR_DROP_OVER:
				case PageIndicator3D.PAGE_INDICATOR_SCROLL:
					if( DefaultLayout.page_container_shown )
						showPageContainer( -1 );
					return true;
			}
		}
		if( sender instanceof PageContainer3D )
		{
			switch( event_id )
			{
				case PageContainer3D.MSG_PAGE_CONTAINER_HIDE:
					workspace.setCurrentPage( pageContainer.getSelectedIndex() );
					appHost.hide();
					if( DefaultLayout.enable_camera )
					{
						workspace.addCameraView();
					}
					if( DefaultLayout.show_music_page )
					{
						workspace.addMusicView();
					}
					workspace.show();
					hotseatBar.show();
					if( DefaultLayout.enable_news )
					{
						if( DefaultLayout.show_newspage_with_handle )
						{
							if( newsHandle != null )
							{
								Messenger.sendMsg( Messenger.MSG_SHOW_NEWSVIEW_HANDLE , null );
							}
						}
					}
					if( DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
					{
						trashIcon.hide();
						if( folderTarget != null )
						{
							folderTarget.hide();
						}
						trashIcon.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , trashIcon.x , 0 , 0 );
					}
					if( dragLayer.isVisible() )
					{
						if( dragLayer.draging )
						{
							// teapotXu add start for add new folder in top-trash
							// bar
							if( DefaultLayout.generate_new_folder_in_top_trash_bar )
							{
								if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
								{
									trashIcon.setPosFlag( TrashIcon3D.TRASH_POS_TOP_FLAG_RIGHT );
									FolderTarget3D.setVisibleFlag( FolderTarget3D.FLAG_VISIBLE );
								}
							}
							// teapotXu add end
							trashIcon.show();
							if( folderTarget != null )
							{
								folderTarget.show();
							}
							if( DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
							{
								trashIcon.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , trashIcon.x , 0 , 0 );
							}
						}
						else
						{
							workspace.forceSetCellLayoutDropType( pageContainer.pageSelect.currentIndex );
							if( dragLayer.getDragList().size() > 0 && dragLayer.onDrop() == null )
							{
								dropListBacktoOrig( dragLayer.getDragList() );
							}
							dragLayer.removeAllViews();
							dragLayer.hide();
						}
					}
					else
					{
						// zhujieping add
						if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
						{
							if( workspace.getScaleX() < 1 || workspace.getScaleY() < 1 )
							{
								workspace.setScale( 1.0f , 1.0f );
							}
						}
						// hotButton.show();
						SendMsgToAndroid.sendShowWorkspaceMsg();
					}
					if( !pageIndicator.isVisible() )
						pageIndicator.show();
					workspace.setScaleZ( 1f );
					return true;
				case PageContainer3D.MSG_PAGE_SET_HOME:
					workspace.setHomePage( pageContainer.getHomePage() );
					return true;
				case PageContainer3D.MSG_PAGE_APPEND_PAGE:
					CellLayout3D cell = new CellLayout3D( "celllayout" );
					workspace.addPage( cell );
					SendMsgToAndroid.sendAddWorkspaceCellMsg( -1 );
					// pageContainer.tmpCell = cell;
					pageIndicator.setPageNum( workspace.getPageNum() );
					return true;
				case PageContainer3D.MSG_PAGE_REMOVE_CELL:
					pageIndicator.setPageNum( workspace.getPageNum() );
					Object obj = pageContainer.getTag();
					if( obj != null )
					{
						trashIcon.onDrop( (ArrayList<View3D>)obj , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() - trashIcon.height );
					}
					return true;
				case PageContainer3D.MSG_PAGE_ADD_CELL:
					pageIndicator.setPageNum( workspace.getPageNum() );
					return true;
				case PageContainer3D.MSG_PAGE_SWITCH_PAGE:
					// pageIndicator.pageScroll(pageContainer.getSelectedIndex(),workspace.getPageNum());
					return true;
				case PageContainer3D.MSG_PAGE_MODE_EDIT:
					// pageIndicator.hide();
					return true;
				case PageContainer3D.MSG_PAGE_MODE_SELECT:
					// pageIndicator.show();
					return true;
				case PageContainer3D.MSG_PAGE_SHOW_EDIT:
					showPageEdit();
					return true;
			}
		}
		if( sender instanceof HotSeat3D )
		{
			HotSeat3D sidebar = (HotSeat3D)sender;
			switch( event_id )
			{
				case HotSeat3D.MSG_VIEW_START_MAIN:
					if( DefaultLayout.mainmenu_inout_no_anim )
					{
						showAllAppFromWorkspaceEx();
					}
					else
					{
						showAllAppFromWorkspace();
					}
					return true;
				case HotSeat3D.MSG_ON_DROP:
					View3D view = (View3D)sidebar.getTag();
					Vector2 vec2 = new Vector2();
					view.toAbsoluteCoords( vec2 );
					ArrayList<View3D> tmpList = new ArrayList<View3D>();
					tmpList.add( view );
					sidebar.setTag( null );
					workspace.onDrop( tmpList , vec2.x , vec2.y );
					return true;
				case HotSeat3D.MSG_VIEW_HIDE_MAIN:
					if( DefaultLayout.enable_takein_workspace_by_longclick )
						hideorShowWorkspace( true );
					return true;
			}
		}
		if( sender instanceof FolderIcon3D )
		{
			final FolderIcon3D folderIcon3D = (FolderIcon3D)sender;
			switch( event_id )
			{
				case FolderIcon3D.MSG_FOLDERICON_BACKTO_ORIG:
					ArrayList<View3D> child_list = (ArrayList<View3D>)folderIcon3D.getTag();
					folder_DropList_backtoOrig( child_list );
					return true;
				case FolderIcon3D.MSG_FOLDERICON_TO_ROOT3D:
					// zhujieping add
					if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
					{
						return dealMIUIFolderToRoot3D( folderIcon3D );
					}
					appHost.hide();
					hotseatBar.hide();
					hotseatBar.getDockGroup().releaseFocus();
					if( DefaultLayout.enable_hotseat_rolling )
						hotseatBar.getMainGroup().releaseFocus();
					if( DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
					{
						trashIcon.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , trashIcon.x , -trashIcon.height * 1.5f , 0 );
					}
					workspace.releaseFocus();
					workspace.hide();
					pageIndicator.hide();
					addView( folderIcon3D );
					dragLayer.removeDropTarget( folderIcon3D );
					folderOpened = true;
					folder = folderIcon3D;
					return true;
				case FolderIcon3D.MSG_FOLDERICON_TO_CELLLAYOUT:
					// appList.show();
					// zhujieping add start
					if( ( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder ) && !( DefaultLayout.mainmenu_folder_function == true && ( folderIcon3D
							.getFromWhere() == FolderIcon3D.FROM_APPLIST && folderIcon3D.getExitToWhere() == FolderIcon3D.TO_CELLLAYOUT ) ) )
					{
						dealMIUIFolderToCellLayout( folderIcon3D );
						return true;
					}
					// zhujieping add end
					// teapotXu add start for Folder in Mainmenu
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						// 如果此时是从主菜单的文件夹中拖动图标到Idle上，
						if( folderIcon3D.getFromWhere() == FolderIcon3D.FROM_APPLIST && folderIcon3D.getExitToWhere() == FolderIcon3D.TO_CELLLAYOUT )
						{
							// 此处设置标志，使再次进入AppList的时候，重新刷新并排序，显示出文件夹
							appHost.hide();
							( (AppList3D)appHost.appList ).force_applist_refesh = true;
							// 同时把该Folder add入 dragLayer
							dragLayer.addDropTarget( (DropTarget3D)folderIcon3D );
						}
					}
					// teapotXu add end for Folder in Mainmenu
					workspace.show();
					boolean closeFolderByDrag = folderIcon3D.mFolder.getColseFolderByDragVal();
					// xiatian add start //for mainmenu sort by user
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						if( ( folderIcon3D.getFromWhere() == FolderIcon3D.FROM_APPLIST && folderIcon3D.getExitToWhere() == FolderIcon3D.TO_CELLLAYOUT ) && ( ThemeManager.getInstance().getBoolean(
								"miui_v5_folder" ) || DefaultLayout.miui_v5_folder ) )
						{
							closeFolderByDrag = folderIcon3D.getMIUI3DFolder().getColseFolderByDragVal();
						}
					}
					// xiatian add end
					hotseatBar.show();
					if( DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
					{
						trashIcon.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , trashIcon.x , 0 , 0 );
					}
					if( folderIcon3D.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT )
					{
						hotseatBar.setTag2( null );
						// teapotXu add start for Fodler in Mainmenu
					}
					else if( DefaultLayout.mainmenu_folder_function == true && folderIcon3D.getFromWhere() == FolderIcon3D.FROM_APPLIST )
					{
						// 如果此时是从主菜单的文件夹中拖动图标到Idle上，
						hotseatBar.setTag2( null );
						// teapotXu add end for Folder in Mainmenu
					}
					else
					{
						hotseatBar.setTag2( folderIcon3D );
					}
					// teapotXu add start
					if( this.setupMenu != null && this.setupMenu.visible == true )
					{
						this.setupMenu.hide();
					}
					// teapotXu add end
					workspace.setTag( folderIcon3D );
					hotseatBar.touchable = false;
					workspace.touchable = false;
					if( folderIcon3D.getItemInfo().container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
					{
						CellLayout3D cellLayout = workspace.getCurrentCellLayout();
						if( cellLayout != null )
						{
							cellLayout.cellFindViewAndRemove( folderIcon3D.getItemInfo().cellX , folderIcon3D.getItemInfo().cellY );
						}
					}
					dealworkspaceTweenFinish();
					return true;
					// zhujieping add start
				case FolderIcon3D.MSG_WORKSPACE_ALPAH_TO_ONE:
				{
					if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
					{
						resetWorkspace( folderIcon3D );
					}
					return true;
				}
				case FolderIcon3D.MSG_WORKSPACE_RECOVER:
				{
					if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
					{
						if( folderIcon3D.getParent() == this )
						{
							if( folderIcon3D.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT )
							{
								workspace.addBackInScreen( folderIcon3D , folderIcon3D.mInfo.x , folderIcon3D.mInfo.y );
							}
							else if( folderIcon3D.getFromWhere() == FolderIcon3D.FROM_HOTSEAT )
							{
								folderIcon3D.setSize( R3D.workspace_cell_width , R3D.workspace_cell_height );
								ArrayList<View3D> tmp = new ArrayList<View3D>();
								tmp.add( (View3D)folderIcon3D );
								hotseatBar.getDockGroup().backtoOrig( tmp );
								folderIcon3D.setFolderIconSize( 0 , 0 , folderIcon3D.mInfo.x , folderIcon3D.mInfo.y );
							}
						}
					}
					return true;
				}
				// zhujieping add end
				case FolderIcon3D.MSG_UPDATE_VIEW:
					Object obj = folderIcon3D.getTag();
					String str = null;
					if( obj instanceof String )
					{
						str = (String)obj;
					}
					R3D.pack( str , folderIcon3D.titleToPixmap() );
					launcher.postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
							folderIcon3D.setTexture();
						}
					} );
					return true;
				case FolderIcon3D.MSG_FOLDER_CLING_VISIBLE:
					return workspace.isVisible();
			}
		}
		if( sender instanceof Folder3D )
		{
			final Folder3D folder3D = (Folder3D)sender;
			switch( event_id )
			{
				case Folder3D.MSG_ON_DROP:
					View3D view = (View3D)folder3D.getTag();
					Vector2 vec2 = new Vector2();
					view.toAbsoluteCoords( vec2 );
					ArrayList<View3D> tmpList = new ArrayList<View3D>();
					tmpList.add( view );
					workspace.onDrop( tmpList , vec2.x , vec2.y );
					return true;
				case Folder3D.MSG_UPDATE_VIEW:
					launcher.postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							folder3D.onInputNameChanged();
						}
					} );
					return true;
			}
		}
		if( sender instanceof FolderMIUI3D )
		{
			final FolderMIUI3D folder3D = (FolderMIUI3D)sender;
			switch( event_id )
			{
				case FolderMIUI3D.MSG_UPDATE_VIEW:
					return true;
				case FolderMIUI3D.MSG_ADD_FROM_ALLAPP:
					if( !listToAdd.isVisible() )
					{
						listToAdd.bringToFront();
						listToAdd.bNewFolderInApplist = false;
						listToAdd.bNewFolderInWorkspace = false;
						ApplicationList.sbHideIconInFolder = false;
						ApplicationList.sUserFolderInfo = folder3D.mFolderIcon.mInfo;
						listToAdd.showAndSelect(
								folder3D.mFolderIcon.mInfo.title.toString() ,
								(ArrayList<View3D>)folder3D.getFolderChildren().clone() ,
								Utils3D.getScreenWidth() / 2 ,
								Utils3D.getScreenHeight() / 2 );
					}
					return true;
			}
		}
		if( sender instanceof AppPopMenu2 )
		{
			switch( event_id )
			{
				case AppPopMenu2.MSG_NEW_FOLDER_IN_APPLIST:
					if( !listToAdd.isVisible() )
					{
						listToAdd.bringToFront();
						ApplicationList.sUserFolderInfo = null;
						listToAdd.bNewFolderInApplist = true;
						listToAdd.bNewFolderInWorkspace = false;
						ApplicationList.sbHideIconInFolder = true;
						listToAdd.showAndSelect( R3D.folder3D_name , new ArrayList<View3D>() , Utils3D.getScreenWidth() / 2 , Utils3D.getScreenHeight() / 2 );
					}
					return true;
			}
		}
		if( sender instanceof DesktopEditMenuItem.ButtonIcon3D )
		{
			switch( event_id )
			{
				case DesktopEditMenuItem.ButtonIcon3D.MSG_NEW_FOLDER_IN_APPLIST:
					if( !listToAdd.isVisible() )
					{
						listToAdd.bringToFront();
						ApplicationList.sUserFolderInfo = null;
						listToAdd.bNewFolderInWorkspace = true;
						listToAdd.bNewFolderInApplist = false;
						ApplicationList.sbHideIconInFolder = false;
						listToAdd.showAndSelect( R3D.folder3D_name , new ArrayList<View3D>() , Utils3D.getScreenWidth() / 2 , Utils3D.getScreenHeight() / 2 );
					}
					return true;
			}
		}
		// teapotXu add start
		if( sender instanceof FolderTarget3D )
		{
			final FolderTarget3D folderTarget = (FolderTarget3D)sender;
			switch( event_id )
			{
				case FolderTarget3D.MSG_FOLDERTARGET_BACKTO_ORIG:
					ArrayList<View3D> child_list = (ArrayList<View3D>)folderTarget.getTag();
					folder_DropList_backtoOrig( child_list );
					return true;
			}
		}
		if( sender instanceof MIUIWidgetList )
		{
			final MIUIWidgetList miui_widgetlist = (MIUIWidgetList)sender;
			switch( event_id )
			{
				case MIUIWidgetList.MSG_MIUIWIDGET_DECIDE_PAGE_ADDED:
					if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
					{
						View3D tempGroup = workspace.getChildAt( workspace.getCurrentScreen() );
						if( tempGroup instanceof WorkspaceEditView )
						{
							tempGroup.setTag( tempGroup );
							workspace.onCtrlEvent( tempGroup , WorkspaceEditView.MSG_CHANGE_TO_APPEND_PAGE );
						}
					}
					return true;
			}
		}
		//teapotXu add end
		if( sender instanceof ApplicationListHost )
		{
			final ApplicationListHost list = (ApplicationListHost)sender;
			switch( event_id )
			{
				case ApplicationListHost.MSG_VIEW_TO_ADD_FOLDER:
					if( folderOpened && folder != null && folder.mFolderMIUI3D != null )
					{
						folder.mFolderMIUI3D.setEditText( ApplicationListHost.folderName );
						folder.mFolderMIUI3D.addChildrenToFolder( list.listToAdd() );
					}
					// xiatian add start //for mainmenu sort by user
					else
					{
						appHost.onCtrlEvent( list , ApplicationListHost.MSG_VIEW_TO_ADD_FOLDER );
					}
					// xiatian add end
					return true;
				case ApplicationListHost.MSG_FOCUS_FOLDER:
					if( folderOpened && folder != null && folder.mFolderMIUI3D != null )
					{
						folder.requestFocus();
					}
					// xiatian add start //for mainmenu sort by user
					else
					{
						appHost.onCtrlEvent( list , ApplicationListHost.MSG_FOCUS_FOLDER );
					}
					// xiatian add end
					return true;
				case ApplicationListHost.MSG_NEW_FOLDER_IN_APPLIST:
					appHost.onCtrlEvent( list , ApplicationListHost.MSG_NEW_FOLDER_IN_APPLIST );
					return true;
			}
		}
		if( sender instanceof TrashIcon3D )
		{
			TrashIcon3D trash = (TrashIcon3D)sender;
			switch( event_id )
			{
				case TrashIcon3D.MSG_TRASH_DELETE:
					List<?> viewList = (List<?>)trash.getTag();
					for( int i = 0 ; i < viewList.size() ; i++ )
					{
						if( viewList.get( i ) instanceof Widget3D )
						{
							Widget3D widget3D = (Widget3D)viewList.get( i );
							Widget3DManager.getInstance().deleteWidget3D( widget3D );
							UmengMobclickAgent.FirstTime( launcher , "DeleteWidget3D" );
							Widget3DProvider provider = Widget3DManager.getInstance().getWidget3DProvider( widget3D.getPackageName() );
							if( widget3D.packageName.equalsIgnoreCase( "com.iLoong.WeatherClock" ) )
							{
								provider.spanX = 4;
								provider.spanY = 2;
							}
							else if( widget3D.packageName.equalsIgnoreCase( "com.cooee.searchbar" ) )
							{
								provider.spanX = 4;
								provider.spanY = 1;
							}
							else if( widget3D.packageName.equalsIgnoreCase( "com.iLoong.Calendar" ) )
							{
								provider.spanX = 4;
								provider.spanY = 4;
							}
							else if( widget3D.packageName.equalsIgnoreCase( "com.iLoong.Clean4" ) )
							{
								provider.spanX = 4;
								provider.spanY = 1;
							}
							else if( widget3D.packageName.equalsIgnoreCase( "com.iLoong.NumberClock" ) )
							{
								provider.spanX = 4;
								provider.spanY = 2;
							}
							Widget3DManager.getInstance().updateWidgetInfo( widget3D.getPackageName() , provider );
							SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
							Editor editor = sp.edit();
							editor.putBoolean( widget3D.getPackageName() , false );
							editor.putInt( widget3D.getPackageName() + ":spanX" , -1 );
							editor.putInt( widget3D.getPackageName() + ":spanY" , -1 );
							editor.commit();
						}
						else if( viewList.get( i ) instanceof Widget )
						{
							SendMsgToAndroid.deleteSysWidget( (Widget)viewList.get( i ) );
							Widget widget = (Widget)viewList.get( i );
							widget.dispose();
						}
						// xiatian add start //Widget adaptation
						if( viewList.get( i ) instanceof PageScrollListener )
						{
							workspace.removeScrollListener( (PageScrollListener)viewList.get( i ) );
						}
						// xiatian add end
						if( viewList.get( i ) instanceof DropTarget3D )
						{
							dragLayer.removeDropTarget( (DropTarget3D)viewList.get( i ) );
						}
						if( viewList.get( i ) instanceof FolderIcon3D )
						{
							FolderIcon3D folderIcon = (FolderIcon3D)viewList.get( i );
							UserFolderInfo item = (UserFolderInfo)folderIcon.getItemInfo();
							for( int j = 0 ; j < folderIcon.getChildCount() ; j++ )
							{
								View3D myView = folderIcon.getChildAt( j );
								if( myView instanceof Icon3D )
								{
									ItemInfo iconItem = ( (Icon3D)myView ).getItemInfo();
									Root3D.deleteFromDB( iconItem );
								}
							}
							launcher.removeFolder( item );
						}
					}
					viewList.clear();
					trash.setTag( null );
					return true;
			}
		}
		switch( event_id )
		{
			case MSG_SET_WALLPAPER_OFFSET:
				final IBinder token = launcher.getWindow().getCurrentFocus().getWindowToken();
				final NPageBase page = (NPageBase)sender;
				if( token != null )
				{
					launcher.postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							if( !DefaultLayout.disable_move_wallpaper )
							{
								SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
								if( prefs.getBoolean( SetupMenu.getKey( RR.string.desktop_wallpaper_mv ) , true ) == true )
								{
									mWallpaperManager.setWallpaperOffsetSteps( 1.0f / ( page.getPageNum() - 1 ) , 0 );
									float offset = page.getTotalOffset();
									mWallpaperManager.setWallpaperOffsets( token , offset , 0 );
								}
								else
								{
									mWallpaperManager.clearWallpaperOffsets( token );
								}
							}
							// teapotXu add start for mv wallpaper's config menu
							else
							{
								if( DefaultLayout.enable_configmenu_for_move_wallpaper )
								{
									SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
									if( prefs.getBoolean( SetupMenu.getKey( RR.string.desktop_wallpaper_mv ) , true ) == true )
									{
										mWallpaperManager.setWallpaperOffsetSteps( 1.0f / ( page.getPageNum() - 1 ) , 0 );
										float offset = page.getTotalOffset();
										mWallpaperManager.setWallpaperOffsets( token , offset , 0 );
									}
									else
									{
										mWallpaperManager.clearWallpaperOffsets( token );
									}
								}
							}
							// teapotXu add end
						}
					} );
				}
				return true;
		}
		return false;
	}
	
	private boolean showPageEdit()
	{
		if( pageContainer.pageEdit != null && pageContainer.pageEdit.isAnimating() )
			return true;
		// teapotXu add start
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			SendMsgToAndroid.sendOurToastMsg( iLoongLauncher.getInstance().getResources().getString( RR.string.current_in_miui_edit_mode ) );
			return true;
		}
		if( !pageContainer.isVisible() )
		{
			workspace.setScaleZ( 0.1f );
			if( DefaultLayout.show_music_page )
			{
				workspace.removeMusicView();
			}
			if( DefaultLayout.enable_camera )
			{
				workspace.removeCameraView();
			}
			int selectedPage = workspace.getCurrentScreen();
			appHost.hide();
			hotseatBar.hide();
			workspace.hide();
			workspace.releaseFocus();
			pageIndicator.hide();
			trashIcon.hide();
			if( newsHandle != null )
			{
				Messenger.sendMsg( Messenger.MSG_HIDE_NEWSVIEW_HANDLE , null );
			}
			// teapotXu add start for add new folder in top-trash bar
			if( DefaultLayout.generate_new_folder_in_top_trash_bar )
			{
				if( folderTarget != null )
				{
					folderTarget.hide();
				}
			}
			// teapotXu add end
			pageContainer.setupPageEdit( workspace.getViewList() , selectedPage , workspace.getHomePage() );
			pageContainer.show();
			return true;
		}
		else
		{
			if( pageContainer.pageMode == PageContainer3D.PAGE_MODE_SELECT )
				pageContainer.changeMode();
		}
		return true;
	}
	
	public void showWorkSpaceFromAllApp1()
	{
		hotseatBar.setVisible( true );
		hotseatBar.touchable = true;
		workspace.color.a = 1;
		workspace.y = 0;
		workspace.show();
		if( DefaultLayout.broadcast_state )
		{
			iLoongLauncher.getInstance().sendBroadcast( new Intent( "com.cooee.launcher.action.show_workspace" ) );
		}
		Utils3D.changeStatusbar( "topwisemenu" , false , false );
		Utils3D.changeStatusbar( "topwiseidle" , true , true );
	}
	
	public void showWorkSpaceFromAllApp()
	{
		if( workspaceAndAppTween != null && workspaceAndAppTween.isStarted() )
		{
			return;
		}
		if( iLoongLauncher.getInstance().optionsMenuOpen )
		{
			return;
		}
		startWorkspaceAndAppTween = true;
		// xiatian add start //HotSeat3DShake
		if( DefaultLayout.enable_HotSeat3DShake )
		{
			hotseatBar.setShake();
		}
		// xiatian add end
		if( DefaultLayout.show_music_page && workspace.curIsMusicView() )
		{
			musicSeat.showDelay( workspaceTweenDuration );
			hotseatBar.setVisible( true );
			hotseatBar.touchable = true;
		}
		else if( DefaultLayout.enable_camera && workspace.curIsCameraView() )//xujin
		{
			if( cameraSeat != null )
			{
				cameraSeat.showDelay( workspaceTweenDuration );
			}
			hotseatBar.setVisible( true );
			hotseatBar.touchable = true;
		}
		else
		{
			hotseatBar.showDelay( workspaceTweenDuration );
		}
		workspaceAndAppTween = Timeline.createParallel();
		workspace.color.a = 0;
		workspaceAndAppTween.push( workspace.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , workspaceTweenDuration , 1 , 0 , 0 ).delay( workspaceTweenDuration ) );
		workspace.setUser( Utils3D.getScreenHeight() / 5 );
		workspaceAndAppTween.push( workspace.obtainTween( View3DTweenAccessor.USER , Cubic.OUT , workspaceTweenDuration , 0 , 0 , 0 ).delay( workspaceTweenDuration ) );
		workspace.setScale( 0.9f , 0.9f );
		workspaceAndAppTween.push( workspace.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , workspaceTweenDuration , 1 , 1 , 0 ).delay( workspaceTweenDuration ) );
		workspace.show();
		NPageBase contentList = appHost.getContentList();
		if( contentList == null )
		{
			contentList = AppHost3D.appList;
		}
		contentList.setUser( 0 );
		Color c = contentList.indicatorView.getColor();
		contentList.indicatorView.setColor( c.r , c.g , c.b , 0 );
		workspaceAndAppTween.push( contentList.obtainTween( View3DTweenAccessor.USER , Cubic.IN , workspaceTweenDuration , -contentList.height / 5 , 0 , 0 ) );
		workspaceAndAppTween.push( contentList.obtainTween( View3DTweenAccessor.OPACITY , Cubic.IN , workspaceTweenDuration , 0 , 0 , 0 ) );
		workspaceAndAppTween.push( contentList.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.IN , workspaceTweenDuration , 0.9f , 0.9f , 0 ) );
		if( appHost.tabPluginHost != null )
		{
			workspaceAndAppTween.push( appHost.tabPluginHost.obtainTween( View3DTweenAccessor.USER , Cubic.IN , workspaceTweenDuration , -contentList.height / 5 , 0 , 0 ) );
			workspaceAndAppTween.push( appHost.tabPluginHost.obtainTween( View3DTweenAccessor.OPACITY , Cubic.IN , workspaceTweenDuration , 0 , 0 , 0 ) );
			workspaceAndAppTween.push( appHost.tabPluginHost.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.IN , workspaceTweenDuration , 0.9f , 0.9f , 0 ) );
		}
		if( AppHost3D.appBar != null )
		{
			AppHost3D.appBar.color.a = 1;
			workspaceAndAppTween.push( AppHost3D.appBar.obtainTween( View3DTweenAccessor.OPACITY , Cubic.IN , workspaceTweenDuration , 0 , 0 , 0 ) );
		}
		workspaceAndAppTween.setCallback( this ).start( View3DTweenAccessor.manager ).setUserData( 0 );
		Log.d( "launcher" , "showWorkSpaceFromAllApp" );
		if( DefaultLayout.broadcast_state )
		{
			iLoongLauncher.getInstance().sendBroadcast( new Intent( "com.cooee.launcher.action.show_workspace" ) );
		}
		Utils3D.changeStatusbar( "topwisemenu" , false , false );
		Utils3D.changeStatusbar( "topwiseidle" , true , true );
		if( DefaultLayout.enable_new_particle )
		{
			TimerTask task = new TimerTask() {
				
				public void run()
				{
					ParticleManager manager = ParticleManager.getParticleManager();
					manager.stopAllAnims();
				}
			};
			Timer timer = new Timer();
			timer.schedule( task , 500 );
		}
		if( newsHandle != null && DefaultLayout.enable_news && DefaultLayout.show_newspage_with_handle )
		{
			newsHandle.show();
			Messenger.sendMsg( Messenger.MSG_SHOW_NEWSVIEW_HANDLE , null );
		}
		iLoongLauncher.getInstance().showActionGuide();
	}
	
	public void showWorkSpaceFromAllAppEx()
	{
	}
	
	// teapotXu add start for longClick to add widget in mainmenu
	public void showWidgetListFromWorkspace()
	{
		if( appHost != null && AppHost3D.appList != null )
		{
			AppHost3D.appList.setCurrentPage( AppList3D.appPageCount );
		}
		showAllAppFromWorkspace();
	}
	
	public void showAllAppFromWorkspace()
	{
		if (!iLoongApplication.getInstance().getModel().loadAppListPreDone) 
		{
			return;
		}
		
		if( ( workspaceAndAppTween != null && workspaceAndAppTween.isStarted() ) )
		{
			return;
		}
		if( iLoongLauncher.getInstance().optionsMenuOpen )
		{
			return;
		}
		startWorkspaceAndAppTween = true;
		boolean hideHotbar = DefaultLayout.applist_style_classic ? false : true;
		if( appHost.isNeedReset() )
		{
			SendMsgToAndroid.sendForceResetMainmenuMessage( 500 );
		}
		appHost.show();
		pageIndicator.hide();
		if( hideHotbar )
		{
			if( DefaultLayout.show_music_page )
			{
				musicSeat.hide();
			}
			if( DefaultLayout.enable_camera && cameraSeat != null )
			{
				cameraSeat.hide();
			}
			hotseatBar.hide();
		}
		if( DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
		{
			trashIcon.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , trashIcon.x , -trashIcon.height * 1.5f , 0 );
		}
		workspaceAndAppTween = Timeline.createParallel();
		AppHost3D.appList.setUser( -AppHost3D.appList.height / 5 + ( DefaultLayout.applist_style_classic ? R3D.hot_obj_height : 0 ) );
		workspaceAndAppTween.push( AppHost3D.appList.obtainTween( View3DTweenAccessor.USER , Cubic.OUT , workspaceTweenDuration , 0 , 0 , 0 ).delay( workspaceTweenDuration ) );
		AppHost3D.appList.color.a = 0;
		workspaceAndAppTween.push( AppHost3D.appList.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , workspaceTweenDuration , 1 , 0 , 0 ).delay( workspaceTweenDuration ) );
		AppHost3D.appList.setScale( 0.9f , 0.9f );
		workspaceAndAppTween.push( AppHost3D.appList.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , workspaceTweenDuration , 1 , 1 , 0 ).delay( workspaceTweenDuration ) );
		if( appHost.tabPluginHost != null )
		{
			appHost.tabPluginHost.setUser( -appHost.tabPluginHost.height / 5 + ( DefaultLayout.applist_style_classic ? R3D.hot_obj_height : 0 ) );
			workspaceAndAppTween.push( appHost.tabPluginHost.obtainTween(
					View3DTweenAccessor.USER ,
					Cubic.OUT ,
					workspaceTweenDuration ,
					( DefaultLayout.applist_style_classic ? R3D.hot_obj_height : 0 ) ,
					0 ,
					0 ).delay( workspaceTweenDuration ) );
			appHost.tabPluginHost.color.a = 0;
			workspaceAndAppTween.push( appHost.tabPluginHost.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , workspaceTweenDuration , 1 , 0 , 0 ).delay( workspaceTweenDuration ) );
			appHost.tabPluginHost.setScale( 0.9f , 0.9f );
			workspaceAndAppTween.push( appHost.tabPluginHost.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , workspaceTweenDuration , 1 , 1 , 0 ).delay( workspaceTweenDuration ) );
		}
		if( AppHost3D.appBar != null )
		{
			AppHost3D.appBar.color.a = 0;
			workspaceAndAppTween.push( AppHost3D.appBar.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , workspaceTweenDuration , 1 , 0 , 0 ).delay( workspaceTweenDuration ) );
		}
		workspace.setUser( 0 );
		workspaceAndAppTween.push( workspace.obtainTween( View3DTweenAccessor.USER , Cubic.IN , workspaceTweenDuration , Utils3D.getScreenHeight() / 5 , 0 , 0 ) );
		workspace.color.a = 1;
		workspaceAndAppTween.push( workspace.obtainTween( View3DTweenAccessor.OPACITY , Cubic.IN , workspaceTweenDuration , 0 , 0 , 0 ) );
		workspaceAndAppTween.push( workspace.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.IN , workspaceTweenDuration , 0.9f , 0.9f , 0 ) );
		workspaceAndAppTween.setCallback( this ).start( View3DTweenAccessor.manager ).setUserData( 1 );
		Messenger.sendMsg( Messenger.MSG_START_COVER_MTKWIDGET , 0 , 0 );
		SendMsgToAndroid.sendHideWorkspaceMsg();
		if( DefaultLayout.broadcast_state )
		{
			iLoongLauncher.getInstance().sendBroadcast( new Intent( "com.cooee.launcher.action.show_app" ) );
		}
		Utils3D.changeStatusbar( "topwisemenu" , true , true );
		Utils3D.changeStatusbar( "topwiseidle" , false , false );
		if( DefaultLayout.enable_new_particle )
		{
			TimerTask task = new TimerTask() {
				
				public void run()
				{
					ParticleManager manager = ParticleManager.getParticleManager();
					manager.stopAllAnims();
				}
			};
			Timer timer = new Timer();
			timer.schedule( task , 500 );
		}
		if( newsHandle != null )
		{
			newsHandle.hide();
			Messenger.sendMsg( Messenger.MSG_HIDE_NEWSVIEW_HANDLE , null );
		}
	}
	
	public void showAllAppFromWorkspaceEx()
	{
		// if((workspaceAndAppTween != null &&
		// workspaceAndAppTween.isStarted())||folderOpened){
		// return;
		// }
		// boolean hideHotbar = DefaultLayout.applist_style_classic?false:true;
		// appHost.showEx();
		// pageIndicator.hideEx();
		// if (DefConfig.DEF_NEW_SIDEBAR==true)
		// {
		// if(hideHotbar){
		// hotseatBar.hideEx();
		// hotButton.hideEx();
		// }
		// if (DefaultLayout.trash_icon_pos!=TrashIcon3D.TRASH_POS_TOP)
		// {
		// trashIcon.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT, 0.0f,
		// trashIcon.x,-trashIcon.height*1.5f, 0);
		// }
		// }
		// workspaceAndAppTween = Timeline.createParallel();
		// appHost.appList.setUser(-appHost.appList.height/5+(DefaultLayout.applist_style_classic?R3D.hot_obj_height:0));
		// workspaceAndAppTween.push(appHost.appList.obtainTween(View3DTweenAccessor.USER,
		// Cubic.OUT, 0,
		// (DefaultLayout.applist_style_classic?R3D.hot_obj_height:0), 0,
		// 0).delay(0));
		// appHost.appList.color.a = 0;
		// workspaceAndAppTween.push(appHost.appList.obtainTween(View3DTweenAccessor.OPACITY,
		// Cubic.OUT, 0, 1, 0, 0).delay(0));
		// // appHost.appList.setRotationVector(1, 0, 0);
		// // appHost.appList.setRotation(-30);
		// appHost.appList.setScale(0.9f, 0.9f);
		// workspaceAndAppTween.push(appHost.appList.obtainTween(View3DTweenAccessor.SCALE_XY,
		// Cubic.OUT, 0, 1, 1, 0).delay(0));
		// if(appHost.appBar != null){
		// appHost.appBar.color.a = 0;
		// workspaceAndAppTween.push(appHost.appBar.obtainTween(View3DTweenAccessor.OPACITY,
		// Cubic.OUT, 0, 1, 0, 0).delay(0));
		// }
		//
		// workspace.setUser(0);
		// workspaceAndAppTween.push(workspace.obtainTween(View3DTweenAccessor.USER,
		// Cubic.IN, 0, Utils3D.getScreenHeight()/5, 0, 0));
		// workspace.color.a = 1;
		// workspaceAndAppTween.push(workspace.obtainTween(View3DTweenAccessor.OPACITY,
		// Cubic.IN, 0, 0, 0, 0));
		// // workspace.setRotationVector(1, 0, 0);
		// workspaceAndAppTween.push(workspace.obtainTween(View3DTweenAccessor.SCALE_XY,
		// Cubic.IN, 0, 0.9f, 0.9f, 0));
		// workspaceAndAppTween.setCallback(this).start(View3DTweenAccessor.manager).setUserData(1);
		// hotButton.changeState(HotMainMenuView3D.STATE_APP);
		// SendMsgToAndroid.sendStartCoverMTKWidgetMsg();
		// SendMsgToAndroid.sendHideWorkspaceMsg();
		// ClingManager.getInstance().cancelAllAppCling();
		// if(DefaultLayout.broadcast_state){
		// iLoongLauncher.getInstance().sendBroadcast(new
		// Intent("com.cooee.launcher.action.show_app"));
		// }
		// SendMsgToAndroid.sysPlaySoundEffect();
	}
	
	private boolean showPageContainer(
			int enterPage )
	{
		if( !DefaultLayout.page_container_shown )
			return true;
		if( !pageContainer.isVisible() && !pageContainer.pageSelect.isAnimating() )
		{
			workspace.setScaleZ( 0.1f );
			int selectedPage = 0;
			selectedPage = workspace.getCurrentScreen();
			hotseatBar.hide();
			appHost.hide();
			workspace.hide();
			pageIndicator.hide();
			pageContainer.show();
			pageContainer.setEnterPage( selectedPage );
			pageContainer.setupPageSelect( workspace.getViewList() , selectedPage , workspace.getHomePage() );
			pageContainer.pageSelect.setCurrentCell( workspace.getCurrentScreen() );
			if( DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
			{
				trashIcon.touchable = false;
				trashIcon.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , trashIcon.x , -trashIcon.height * 1.5f , 0 );
			}
			else
			{
				trashIcon.hide();
				// teapotXu add start for add new folder in top-trash bar
				if( DefaultLayout.generate_new_folder_in_top_trash_bar )
				{
					if( folderTarget != null )
					{
						folderTarget.hide();
					}
				}
				// teapotXu add end
			}
			return true;
		}
		return false;
	}
	
	private void dealworkspaceTweenFinish()
	{
		if( workspace.touchable )
			return;
		Object view = workspace.getTag();
		if( view == null )
		{
			return;
		}
		else if( view instanceof FolderIcon3D )
		{
			FolderIcon3D folderIcon3D = (com.iLoong.launcher.Folder3D.FolderIcon3D)view;
			// xiatian add start //for mainmenu sort by user
			if( ( DefaultLayout.mainmenu_folder_function ) && ( folderIcon3D.getFromWhere() != FolderIcon3D.FROM_APPLIST ) )
				// xiatian add end
				workspace.setScale( 1 , 1 );
			if( folderIcon3D.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT )
			{
				workspace.addBackInScreen( folderIcon3D , folderIcon3D.mInfo.x , folderIcon3D.mInfo.y );
			}
			else if( folderIcon3D.getFromWhere() == FolderIcon3D.FROM_HOTSEAT )
			{
				ArrayList<View3D> tmp = new ArrayList<View3D>();
				tmp.add( (View3D)view );
				hotseatBar.getDockGroup().backtoOrig( tmp );
			}
			pageIndicator.show();
			folderOpened = false;
			// teapotXu add start for Folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function == true )
			{
				if( appHost != null )
					appHost.folderOpened = false;
			}
			// teapotXu add end for Folder in Mainmenu
			if( folderIcon3D.getParent() == this )
				folderIcon3D.remove();
			folderIcon3D = null;
			if( dragLayer.isVisible() && !dragLayer.draging )
			{
				workspace.dropAnim = false;
				if( dragLayer.getDragList().size() > 0 && dragLayer.onDrop() == null )
				{
					dropListBacktoOrig( dragLayer.getDragList() );
				}
				dragLayer.removeAllViews();
				dragLayer.hide();
				workspace.dropAnim = true;
				stopDragS3Effect( is_delete );
			}
			if( dragLayer.isVisible() && dragLayer.draging )
			{
				// teapotXu add start for add new folder in top-trash bar
				if( DefaultLayout.generate_new_folder_in_top_trash_bar )
				{
					if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
					{
						trashIcon.setPosFlag( TrashIcon3D.TRASH_POS_TOP_FLAG_RIGHT );
						FolderTarget3D.setVisibleFlag( FolderTarget3D.FLAG_VISIBLE );
					}
				}
				// teapotXu add end
				trashIcon.show();
				if( folderTarget != null )
				{
					folderTarget.show();
				}
			}
			else
			{
				SendMsgToAndroid.sendShowWorkspaceMsg();
			}
			if( goHome )
			{
				goHome = false;
				workspace.scrollTo( workspace.getHomePage() );
			}
			hotseatBar.touchable = true;
			workspace.touchable = true;
		}
	}
	
	@SuppressWarnings( "rawtypes" )
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( qs_fade_out != null && qs_fade_out == source && type == TweenCallback.COMPLETE )
		{
			qSearchGroup.hide();
			qSearchGroup.color.a = 1.0f;
			qSearchGroup.resetPostion();
		}
		if( source == miui_effect_line && type == TweenCallback.COMPLETE )
		{
			is_long_click_doing = false;
		}
		if( source == miui_effect_line && type == TweenCallback.COMPLETE )
		{
			if( isQuitAnim )
			{
				CellLayout3D.isDrawBg = false;
				DefaultLayout.disable_double_click = false;
				ConfigBase.disable_double_click = false;
				if( newsHandle != null && DefaultLayout.enable_news && DefaultLayout.show_newspage_with_handle )
				{
					newsHandle.show();
					Messenger.sendMsg( Messenger.MSG_SHOW_NEWSVIEW_HANDLE , 0 );
				}
			}
			else
			{
				DefaultLayout.disable_double_click = true;
				ConfigBase.disable_double_click = true;
			}
		}
		if( source == workspaceAndAppTween && type == TweenCallback.COMPLETE && workspaceAndAppTween.getUserData().equals( 0 ) )
		{
			Log.d( "launcher" , "event:hide app" );
			workspaceAndAppTween = null;
			startWorkspaceAndAppTween = false;
			appHost.hide();
			if( appHost.getContentList() != null )
			{
				appHost.getContentList().setUser( 0 );
				appHost.getContentList().setRotation( 0 );
				appHost.getContentList().color.a = 1;
				appHost.getContentList().setScale( 1 , 1 );
				appHost.getContentList().stopTween();
			}
			if( AppHost3D.appBar != null )
				AppHost3D.appBar.color.a = 1;
			SendMsgToAndroid.sendShowWorkspaceMsg();
			// xiatian add start //EffectPreview
			if( ( DefaultLayout.enable_effect_preview ) && ( mNeedDelayWorkspaceEffectPreview != -1 ) )
			{
				showWorkspaceEffectPreview( mNeedDelayWorkspaceEffectPreview );
				mNeedDelayWorkspaceEffectPreview = -1;
			}
			// xiatian add end
		}
		else if( source == workspaceAndAppTween && type == TweenCallback.COMPLETE && workspaceAndAppTween.getUserData().equals( 1 ) )
		{
			Log.d( "launcher" , "event:hide workspace" );
			workspaceAndAppTween = null;
			startWorkspaceAndAppTween = false;
			workspace.hide();
			workspace.setUser( 0 );
			workspace.setRotation( 0 );
			workspace.color.a = 1;
			workspace.setScale( 1 , 1 );
			// xiatian add start //EffectPreview
			if( ( DefaultLayout.enable_effect_preview ) && ( mNeedDelayApplistEffectPreview != -1 ) )
			{
				showApplistEffectPreview( mNeedDelayApplistEffectPreview );
				mNeedDelayApplistEffectPreview = -1;
			}
			// xiatian add end
			appListAniDone = true;
		}
		else if( source == s3EffectTween && type == TweenCallback.COMPLETE )
		{
			if( s3EffectTween != null && s3EffectTween.getUserData().equals( DRAG_END ) )
			{
				Workspace3D.is_longKick = false;
				s3EffectTween = null;
				if( is_delete )
				{
					is_delete = false;
				}
			}
		}
		else if( source == s4ScrollEffectTween && type == TweenCallback.COMPLETE )
		{
			if( s4ScrollEffectTween != null && s4ScrollEffectTween.getUserData().equals( DRAG_END ) )
			{
				Workspace3D.is_longKick = false;
				s4ScrollEffectTween = null;
				workspace.recoverPageSequence();
			}
		}
		else if( DefaultLayout.enable_workspace_miui_edit_mode && source == miui_effect_line && type == TweenCallback.COMPLETE )
		{
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode )
			{
				if( dragLayer.isVisible() && !dragLayer.draging )
				{
					dragLayer.removeAllViews();
					dragLayer.hide();
				}
				if( trashIcon.isVisible() )
				{
					trashIcon.hide();
				}
				pageIndicator.setPosition( pageIndicator.x , R3D.page_indicator_y );
				if( widgetHost != null )
				{
					widgetHost.hide();
				}
			}
			else
			{
				if( hotseatBar != null )
				{
					hotseatBar.hide();
				}
			}
			miui_effect_line = null;
		}
		else if( source instanceof Timeline && trashIcon.getTag() != null )
		{
			trashIcon.onDrop( (ArrayList<View3D>)trashIcon.getTag() , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() - trashIcon.height );
			trashIcon.hide();
			// teapotXu add start for add new folder in top-trash bar
			if( DefaultLayout.generate_new_folder_in_top_trash_bar )
			{
				if( folderTarget != null )
				{
					folderTarget.hide();
				}
			}
			// teapotXu add end
			trashIcon.setTag( null );
			SendMsgToAndroid.sendShowWorkspaceMsg();
		}
		else if( shadowView != null && shadowView.color.a == 0 )
			this.removeView( shadowView );
		else if( source == animation_line && type == TweenCallback.COMPLETE )
		{
			hotseatBar.getDockGroup().setTakeinBitmap();
			animation_line.free();
			animation_line = null;
		}
	}
	
	public static boolean allowWidgetRefresh()
	{
		return true;
	}
	
	public void pause()
	{
		if( workspace != null )
			workspace.clearDragObjs();
		if( appHost != null )
			appHost.clearDragObjs();
	}
	
	private ArrayList<View3D> getNotEmptyFolder()
	{
		ArrayList<View3D> dragList = dragLayer.getDragList();
		ArrayList<View3D> delFolder = new ArrayList<View3D>();
		// delFolder.clear();
		for( View3D view : dragList )
		{
			if( view instanceof FolderIcon3D )
			{
				if( ( (FolderIcon3D)view ).mInfo.contents.size() > 0 )
				{
					delFolder.add( view );
				}
			}
		}
		return delFolder;
	}
	
	private void cancle_delete_folder()
	{
		ArrayList<View3D> folderList = getNotEmptyFolder();
		View3D item;
		appHost.hide();
		workspace.show();
		hotseatBar.show();
		for( int i = 0 ; i < folderList.size() ; i++ )
		{
			item = folderList.get( i );
			item.setColor( 1f , 1f , 1f , 1f );
			item.isDragging = false;
			if( ( (FolderIcon3D)item ).mInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
			{
				workspace.calcCoordinate( item );
				workspace.addInCurrenScreen( item , (int)item.x , (int)item.y , false );
				item.startTween( View3DTweenAccessor.POS_XY , Elastic.OUT , 0.3f , ( (FolderIcon3D)item ).mInfo.x , ( (FolderIcon3D)item ).mInfo.y , 0 );
			}
			else if( ( (FolderIcon3D)item ).mInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
			{
				dropListBacktoOrig( folderList );
			}
		}
		dragLayer.removeAllViews();
		dragLayer.hide();
		trashIcon.hide();
		// teapotXu add start for add new folder in top-trash bar
		if( DefaultLayout.generate_new_folder_in_top_trash_bar )
		{
			if( folderTarget != null )
			{
				folderTarget.hide();
			}
		}
		// teapotXu add end
	}
	
	private void ack_delete_folder()
	{
		appHost.hide();
		if( !folderOpened )
		{
			workspace.show();
			dragLayer.onDrop();
			dragLayer.removeAllViews();
			dragLayer.hide();
			hotseatBar.show();
		}
		trashIcon.hide();
		// teapotXu add start for add new folder in top-trash bar
		if( DefaultLayout.generate_new_folder_in_top_trash_bar )
		{
			if( folderTarget != null )
			{
				folderTarget.hide();
			}
		}
		// teapotXu add end
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( sEnableShowFps )
		{
			sBitmapFont.draw( batch , "fps:" + Gdx.graphics.getFramesPerSecond() , 5 , Gdx.graphics.getHeight() - Utils3D.getStatusBarHeight() );
		}
		if( ( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder ) && folder != null )
		{
			if( DefaultLayout.blur_enable )
				MiuiV5FolderBoxBlur( batch , parentAlpha );
			else
			{
				super.draw( batch , parentAlpha );
			}
		}
		else
		{
			super.draw( batch , parentAlpha );
		}
		if( launcher.trashdeleteFolderResult == Workspace3D.CIRCLE_POP_CANCEL_ACTION )
		{
			launcher.trashdeleteFolderResult = Workspace3D.CIRCLE_POP_NONE_ACTION;
			cancle_delete_folder();
		}
		else if( launcher.trashdeleteFolderResult == Workspace3D.CIRCLE_POP_ACK_ACTION )
		{
			launcher.trashdeleteFolderResult = Workspace3D.CIRCLE_POP_NONE_ACTION;
			ack_delete_folder();
		}
		if( DefaultLayout.enable_new_particle )
		{
			ParticleManager manager = ParticleManager.getParticleManager();
			if( Desktop3DListener.d3d.hasDown() )
			{
				if( manager.particleAnimListNeedStart() )
				{
					newStartParticle( Desktop3DListener.currentParticleType , lastupdatex , lastupdatey );
				}
			}
			newDrawParticle( batch );
		}
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		if( keycode == KeyEvent.KEYCODE_SEARCH )
			return true;
		return super.keyUp( keycode );
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		if( is_long_click_doing )
		{
			return true;
		}
		if( keycode == KeyEvent.KEYCODE_SEARCH )
		{
			launcherGlobalSearch();
			return true;
		}
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				if( DesktopEditHost.getPopMenuStyle() == DesktopEditHost.POP_MENU_STYLE_4X2 )
				{
					MenuContainer.mSingleMenu.setTag( MenuContainer.mSingleMenu.msg );
					DesktopEditHost.getInstance().onCtrlEvent( MenuContainer.mSingleMenu , SingleMenu.EVENT_BACK );
				}
				else
				{
					stopMIUIEditEffect();
					if( DesktopEditHost.getInstance() != null )
					{
						DesktopEditHost.getInstance().recyle();
					}
				}
			}
			return true;
		}
		if( keycode == KeyEvent.KEYCODE_MENU )
		{
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				//				stopMIUIEditEffect();
				//				if( DesktopEditHost.getInstance() != null )
				//				{
				//					DesktopEditHost.getInstance().recyle();
				//					if( DesktopEditHost.curPopMenuStyle == DesktopEditHost.POP_MENU_STYLE_4X2 )
				//					{
				//						DesktopEditHost.getInstance().menuContainer.hide();
				//						DesktopEditHost.getInstance().mulpMenuHost.hide();
				//						DesktopEditHost.curPopMenuStyle = DesktopEditHost.POP_MENU_STYLE_4X1;
				//					}
				//				}
			}
			return true;
		}
		return super.keyUp( keycode );
	}
	
	public void onHomeKey(
			boolean alreadyOnHome )
	{
		// xiatian add start //EffectPreview
		if( DefaultLayout.enable_effect_preview )
		{
			mIsInEffectPreviewMode = -1;
		}
		// xiatian add end
		// xiatian add start //EffectPreview
		if( DefaultLayout.enable_effect_preview )
		{
			if( ( mWorkspaceEffectPreview != null ) && ( mWorkspaceEffectPreview.isVisible() ) )
			{
				mWorkspaceEffectPreview.hide();
				hotseatBar.show();
			}
			if( ( mApplistEffectPreview != null ) && ( mApplistEffectPreview.isVisible() ) )
			{
				mApplistEffectPreview.hide();
			}
			if( ( mEffectPreviewTips3D != null ) && ( mEffectPreviewTips3D.isVisible() ) )
			{
				mEffectPreviewTips3D.hide();
			}
		}
		// xiatian add end
		if( workspace != null && workspace.isVisible() && ( DefaultLayout.enable_workspace_miui_edit_mode == true && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) )
		{
			stopMIUIEditEffect();
			if( DesktopEditHost.getInstance() != null )
			{
				DesktopEditHost.getInstance().recyle();
				if( DesktopEditHost.curPopMenuStyle == DesktopEditHost.POP_MENU_STYLE_4X2 )
				{
					DesktopEditHost.getInstance().menuContainer.hide();
					DesktopEditHost.getInstance().mulpMenuHost.hide();
					DesktopEditHost.curPopMenuStyle = DesktopEditHost.POP_MENU_STYLE_4X1;
				}
			}
			return;
		}
		if( appHost != null && appHost.isVisible() )
		{
			// teapotXu add start for Fodler in Mainmenu
			// when folder in Mainmenu is open
			if( appHost.folderOpened == true && appHost.getFolderIconOpendInAppHost() != null )
			{
				AppHost3D.onHomeKey = true; // xiatian add //for mainmenu sort by
											// user
				FolderIcon3D appHost_folder = appHost.getFolderIconOpendInAppHost();
				// xiatian add start //for mainmenu sort by user
				ArrayList<View3D> mDragViewList = dragLayer.getDragList();
				if( mDragViewList != null )
				{
					appHost_folder.onDrop( mDragViewList , 0 , 0 );
					mDragViewList = null;
				}
				// xiatian add end
				if( appHost.closeFolder2goHome )
					return;
				if( appHost_folder.onHomeKey( alreadyOnHome ) )
					appHost.closeFolder2goHome = true;
				// xiatian add start //for mainmenu sort by user
				if( listToAdd.visible )
				{
					listToAdd.hideNoAnim();
				}
				AppHost3D.appList.force_applist_refesh = true;
				// xiatian add end
			}
			// teapotXu add end for Fodler in Mainmenu
			if( DefaultLayout.broadcast_state )
			{
				iLoongLauncher.getInstance().sendBroadcast( new Intent( "com.cooee.launcher.action.show_workspace" ) );
			}
			appHost.hide();
			AppHost3D.appList.resetApplistIconState();// xiatian add //for
														// mainmenu sort by user
			workspace.show();
			hotseatBar.showNoAnim();
			// teapotXu added start for dismissed the dialog when home-key
			// pressed
			launcher.DismissLauncherDialogs();
			// teapotXu add end
			workspace.setCurrentScreen( workspace.getHomePage() );
		}
		else if( pageContainer != null && pageContainer.isVisible() )
		{
			if( pageContainer.pageSelect.isVisible() )
			{
				if( alreadyOnHome )
				{
					pageContainer.switchToPage( workspace.getHomePage() );
				}
				else
				{
					pageContainer.hide( false );
					workspace.setCurrentScreen( workspace.getHomePage() );
				}
			}
			else if( pageContainer.pageEdit.isVisible() )
			{
				launcher.DismissPageDeleteDialog();
				if( alreadyOnHome )
					pageContainer.pageEdit.enterHomePage( true );
				else
				{
					pageContainer.pageEdit.enterHomePage( false );
					workspace.setCurrentScreen( workspace.getHomePage() );
				}
			}
		}
		else if( launcher.folderIcon != null && launcher.folderIcon.mInfo.contents.size() == 0 && launcher.folderIcon.bRenameFolder )
		{
			launcher.renameFoldercleanup();
			workspace.scrollTo( workspace.getHomePage() );
		}
		else if( folder != null )
		{
			if( workspace.isManualScrollTo || goHome )
				return;
			if( folder.bAnimate )
				return;
			else if( folder.onHomeKey( alreadyOnHome ) )
			{
				goHome = true;
				if( listToAdd.visible )
				{
					listToAdd.hide();
				}
			}
			else
				workspace.scrollTo( workspace.getHomePage() );
			folder = null;
		}
		else if( workspace != null )
		{
			launcher.DismissShortcutDialog();
			// teapotXu added start for dismissed the dialog when home-key
			// pressed
			launcher.DismissLauncherDialogs();
			// teapotXu add
			if( workspace.isManualScrollTo || goHome )
				return;
			workspace.show();
			if( alreadyOnHome )
			{
				workspace.scrollTo( workspace.getHomePage() );
				//xujin 确保workspace也滑动
				workspace.setDegree( workspace.getX() );
			}
			else
			{
				workspace.setCurrentScreen( workspace.getHomePage() );
				if( DefaultLayout.show_music_page || DefaultLayout.enable_camera )//xujin
				{
					showSuitableSeat();
				}
			}
		}
		if( newsHandle != null && DefaultLayout.enable_news )
		{
			if( DefaultLayout.show_newspage_with_handle )
			{
				newsHandle.show();
				Messenger.sendMsg( Messenger.MSG_SHOW_NEWSVIEW_HANDLE , 0 );
			}
			Messenger.sendMsg( Messenger.MSG_REMOVE_NEWS_AUTO , null );
		}
	}
	
	class ShadowView extends View3D
	{
		
		public ShadowView(
				String name )
		{
			super( name );
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			if( AppList3D.translucentBg != null )
			{
				batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
				AppList3D.translucentBg.draw( batch , 0 , 0 , width , height );
			}
		}
	}
	
	public void focusWidget(
			final WidgetPluginView3D widgetPluginView ,
			int state )
	{
		if( state == Widget3DManager.WIDGET_STATE_OPEN )
		{
			point.x = 0;
			point.y = 0;
			widgetPluginView.toAbsolute( point );
			focusWidget = widgetPluginView;
			focusWidgetPos = new float[2];
			focusWidgetPos[0] = focusWidget.x;
			focusWidgetPos[1] = focusWidget.y;
			widgetPluginView.setPosition( point.x , point.y );
			focusWidgetMov = 0;
			if( point.y + widgetPluginView.height * 1.2f > Utils3D.getScreenHeight() )
			{
				launcher.postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						widgetPluginView.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.4f , point.x , Utils3D.getScreenHeight() - widgetPluginView.height * 1.2f , 0 );
					}
				} );
				focusWidgetMov = Utils3D.getScreenHeight() - widgetPluginView.height * 1.2f - point.y;
			}
			widgetPluginView.setTag( widgetPluginView.getParent() );
			this.addView( widgetPluginView );
		}
		else
		{
			ViewGroup3D parent = (ViewGroup3D)focusWidget.getTag();
			focusWidget.setPosition( focusWidgetPos[0] , focusWidgetPos[1] + focusWidgetMov );
			if( focusWidgetMov != 0 )
			{
				launcher.postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						widgetPluginView.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.4f , focusWidgetPos[0] , focusWidgetPos[1] , 0 );
					}
				} );
			}
			parent.addView( focusWidget );
			if( shadowView != null )
			{
				launcher.postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						shadowView.startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.8f , 0 , 0 , 0 ).setCallback( shadowView.getParent() );
					}
				} );
			}
		}
	}
	
	public void setEffectType(
			int select )
	{
		if( appHost != null )
			AppHost3D.appList.setEffectType( select );
	}
	
	public void setWorkspaceEffectType(
			int select )
	{
		if( workspace != null )
		{
			workspace.setEffectType( select );
			if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				stopMIUIEditEffectNoAnim();
			}
			workspace.startPreviewEffect( true );
		}
	}
	
	@SuppressLint( "NewApi" )
	private DexClassLoader getClassLoader(
			ResolveInfo resolveInfo )
	{
		ActivityInfo ainfo = resolveInfo.activityInfo;
		String dexPath = ainfo.applicationInfo.sourceDir;
		// 插件输出目录，目前为launcher的子目录
		String dexOutputDir = iLoongLauncher.getInstance().getApplicationInfo().dataDir;
		dexOutputDir = dexOutputDir + File.separator + "widget" + File.separator + ainfo.packageName.substring( ainfo.packageName.lastIndexOf( "." ) + 1 );
		creatDataDir( dexOutputDir );
		Integer sdkVersion = Integer.valueOf( android.os.Build.VERSION.SDK );
		String libPath = null;
		if( sdkVersion > 8 )
		{
			libPath = ainfo.applicationInfo.nativeLibraryDir;
		}
		DexClassLoader loader = new DexClassLoader( dexPath , dexOutputDir , libPath , iLoongApplication.getInstance().getClassLoader() );
		return loader;
	}
	
	private File creatDataDir(
			String dirName )
	{
		File dir = new File( dirName );
		if( !dir.exists() )
		{
			dir.mkdirs();
		}
		return dir;
	}
	
	public void findMainFolder()
	{
		if( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_edit_mode )
		{
			mainFolderinfo.clear();
			if( AppHost3D.appList.mItemInfos.size() > 0 )
			{
				for( int i = 0 ; i < AppHost3D.appList.mItemInfos.size() ; i++ )
				{
					if( AppHost3D.appList.mItemInfos.get( i ) instanceof UserFolderInfo )
					{
						UserFolderInfo info = (UserFolderInfo)AppHost3D.appList.mItemInfos.get( i );
						mainFolderinfo.add( info );
					}
				}
			}
			if( mainFolderinfo.size() > 0 )
			{
				AppHost3D.appList.mItemInfos.removeAll( mainFolderinfo );// 去除场景中的文件夹
				allMainFolderAppinfo.clear();
				/*
				 * 添加文件中被添加的应用
				 */
				for( int i = 0 ; i < mainFolderinfo.size() ; i++ )
				{
					if( mainFolderinfo.get( i ) instanceof UserFolderInfo )
					{
						UserFolderInfo userInfo = (UserFolderInfo)mainFolderinfo.get( i );
						ArrayList<ApplicationInfo> mainApplicationInfos = new ArrayList<ApplicationInfo>();
						mainApplicationInfos.clear();
						for( int j = 0 ; j < userInfo.contents.size() ; j++ )
						{
							mainApplicationInfos.add( userInfo.contents.get( j ).appInfo );// 将在文件夹中app应用为ApplicationInfo
						}
						allMainFolderAppinfo.addAll( mainApplicationInfos );
					}
				}
				Log.v( "" , " 主菜单文件个数为 " + mainFolderinfo.size() );
				if( allMainFolderAppinfo.size() > 0 )
				{
					AppHost3D.appList.mItemInfos.addAll( allMainFolderAppinfo );
				}
				AppHost3D.appList.sortApp( AppHost3D.appList.sortId , false );
				AppHost3D.appList.syncAppsPages();
				Log.v( "Folder" , "mainFolderinfo is hideMainFolder" );
			}
		}
	}
	
	private void showMainFolder()
	{
		if( mainFolderinfo.size() > 0 )
		{
			if( allMainFolderAppinfo.size() > 0 )
			{
				AppHost3D.appList.mItemInfos.removeAll( allMainFolderAppinfo );
			}
			AppHost3D.appList.mItemInfos.addAll( mainFolderinfo );
			AppHost3D.appList.sortApp( AppHost3D.appList.sortId , false );
			AppHost3D.appList.syncAppsPages();
			Log.v( "Folder" , "mainFolderinfo is showmainFolderinfo" );
		}
	}
	
	@Override
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		if( Desktop3DListener.initDone() == false )
		{
			System.out.println( "initDone" );
			// teapotXu add start for doov special customization
			if( DefaultLayout.enable_doov_spec_customization )
			{
				SendMsgToAndroid.sendToastMsg( iLoongLauncher.getInstance().getResources().getString( RR.string.applist_load_toast ) );
			}
			// teapotXu add end
			return true;
		}
		if( folder != null )
		{
			return true;
		}
		if( ( DefaultLayout.enable_takein_workspace_by_longclick && Workspace3D.isHideAll ) || ( DefaultLayout.enable_edit_mode_function && Root3D.IsProhibiteditMode ) )
		{
			return true;
		}
		if( workspace.getX() != 0 )
		{
			return true;
		}
		if( appHost != null && appHost.visible )
		{
			return super.multiTouch2( initialFirstPointer , initialSecondPointer , firstPointer , secondPointer );
		}
		return true;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		particleSetRepeatPosition( this.x , this.y , x , y );
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( DefaultLayout.enable_new_particle )
		{
			Desktop3DListener.root.particleScrollRefresh( this.x , this.y , x , y );
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	public void closeRootDoor()
	{
		for( int i = 0 ; i < workspace.getChildCount() ; i++ )
		{
			View3D view = workspace.getChildAt( i );
			view.y = 0;
		}
		hotseatBar.y = 0;
	}
	
	public void stopFreeView(
			float allfreedownTime )
	{
		if( allfreedownTime > 0.1f )
		{
			allfreedownTime = allfreedownTime - 0.1f;
		}
		for( int i = 0 ; i < workspace.getChildCount() ; i++ )
		{
			View3D view = workspace.getChildAt( i );
			view.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , allfreedownTime , 0f , 0f , 0f );
		}
		if( workspace != null )
		{
			workspace.touchable = true;
		}
		if( ( ( DefaultLayout.enable_particle ) && ( !ParticleManager.particleManagerEnable ) ) || DefaultLayout.enable_new_particle )
			ParticleManager.particleManagerEnable = ParticleManager.getParticleManager().partilceCanRander();
		if( iLoongLauncher.findClientMethod )
		{
			try
			{
				iLoongLauncher.methodSetViewToStatusBar.invoke( iLoongLauncher.clientService , "topwisemenu" , false , true );
				iLoongLauncher.methodSetViewToStatusBar.invoke( iLoongLauncher.clientService , "topwiseidle" , true , true );
				iLoongLauncher.methodSetViewToStatusBar.invoke( iLoongLauncher.clientService , "kuyusceneidle" , false , true );
			}
			catch( IllegalArgumentException e )
			{
				e.printStackTrace();
			}
			catch( IllegalAccessException e )
			{
				e.printStackTrace();
			}
			catch( InvocationTargetException e )
			{
				e.printStackTrace();
			}
		}
		showMainFolder();
	}
	
	// zhujieping add begin
	private void MiuiV5FolderBoxBlur(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( FolderIcon3D.captureCurScreen )
		{
			long time = System.currentTimeMillis();
			// draw to fbo
			folder.fbo.begin();
			Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
			// 先绘制壁纸到fbo上
			if( DefaultLayout.blur_bg_enable == true && !RR.net_version )
			{
				if( FolderIcon3D.wallpaperTextureRegion != null )
				{
					int wpWidth = FolderIcon3D.wallpaperTextureRegion.getTexture().getWidth();
					int wpHeight = FolderIcon3D.wallpaperTextureRegion.getTexture().getHeight();
					batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
					batch.draw( FolderIcon3D.wallpaperTextureRegion , folder.wpOffsetX , 0 , wpWidth , wpHeight );
				}
				else
				{
					if( FolderIcon3D.liveWallpaperActive )
					{
						if( AppList3D.translucentBg != null )
						{
							batch.setColor( color.r , color.g , color.b , 0.7f );
							AppList3D.translucentBg.draw( batch , 0 , 0 , Utils3D.realWidth , iLoongLauncher.getInstance().getResources().getDisplayMetrics().heightPixels );
						}
					}
				}
			}
			this.setScale( 0.8f , 0.8f );
			this.transform = true;
			// 绘制当前屏幕画面到fbo上
			super.draw( batch , parentAlpha );
			folder.fbo.end();
			folder.mFolderIndex = folder.getViewIndex( folder.mFolderMIUI3D );
			folder.mFolderMIUI3D.remove();
			this.setScale( 1.0f , 1.0f );
			this.startTween( View3DTweenAccessor.SCALE_XY , Quint.IN , DefaultLayout.blurDuration , 0.8f , 0.8f , 0 ).setCallback( new TweenCallback() {
				
				@Override
				public void onEvent(
						int arg0 ,
						BaseTween arg1 )
				{
					if( arg0 == TweenCallback.COMPLETE )
					{
						setScale( 1.0f , 1.0f );
					}
				}
			} );
			FolderIcon3D.captureCurScreen = false;
			folder.blurBegin = true;
			if( DefaultLayout.enable_hotseat_rolling )
			{
				if( !RR.net_version )
				{
					hotseatBar.getModel3DGroup().color.a = 1;
					hotseatBar.getModel3DGroup().startTween( View3DTweenAccessor.OPACITY , Quint.IN , DefaultLayout.blurDuration , 0 , 0 , 0 );
				}
			}
			Log.i( "blur" , "blur captureCurScreen:" + ( System.currentTimeMillis() - time ) );
			time = System.currentTimeMillis();
			super.draw( batch , parentAlpha );
			Log.i( "blur" , "blur root3d draw:" + ( System.currentTimeMillis() - time ) );
		}
		else
		{
			long time = System.currentTimeMillis();
			super.draw( batch , parentAlpha );
			Log.i( "blur" , "blur root3d draw:" + ( System.currentTimeMillis() - time ) );
			time = System.currentTimeMillis();
			if( transform )
			{
				this.folder.mFolderMIUI3D.draw( batch , parentAlpha );
			}
			if( folder.blurBegin )
			{
				if( ( folder.blurCount % 2 ) == 0 )
				{
					folder.renderTo( folder.fbo , folder.fbo2 );
				}
				else
				{
					folder.renderTo( folder.fbo2 , folder.fbo );
				}
				folder.blurCount++;
				if( folder.blurCount >= ( DefaultLayout.blurInterate << 1 ) )
				{
					folder.blurBegin = false;
				}
				Log.i( "blur" , "blur blurBegin:" + ( System.currentTimeMillis() - time ) );
				time = System.currentTimeMillis();
			}
			if( !folder.blurCompleted )
			{
				int count = ( DefaultLayout.blurInterate << 1 ) - folder.blurCount;
				for( int i = 0 ; i < count ; i++ )
				{
					if( ( folder.blurCount % 2 ) == 0 )
					{
						folder.renderTo( folder.fbo , folder.fbo2 );
					}
					else
					{
						folder.renderTo( folder.fbo2 , folder.fbo );
					}
					folder.blurCount++;
				}
				Log.i( "blur" , "blur blurComplete:" + ( System.currentTimeMillis() - time ) );
				time = System.currentTimeMillis();
				if( folder.blurredView == null )
				{
					float scaleFactor = 1 / DefaultLayout.fboScale;
					folder.blurredView = new ImageView3D( "blurredView" , folder.fboRegion );
					folder.blurredView.show();
					folder.blurredView.setScale( scaleFactor , scaleFactor );
					folder.blurredView.setPosition(
							folder.blurredView.getWidth() / 2 + ( Gdx.graphics.getWidth() / 2 - folder.blurredView.getWidth() ) ,
							folder.blurredView.getHeight() / 2 + ( Gdx.graphics.getHeight() / 2 - folder.blurredView.getHeight() ) );
					Log.v( "blur" , "aaaa " + folder.blurredView );
					this.transform = false;
					this.setScale( 1.0f , 1.0f );
					if( folder.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT )
					{
						folder.addViewAt( folder.mFolderIndex , folder.mFolderMIUI3D );
						if( folder.folder_style == FolderIcon3D.folder_rotate_style )
						{
							folder.addViewBefore( folder.mFolderMIUI3D , folder.blurredView );
						}
						else
						{
							folder.addView( folder.blurredView );
						}
						if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
						{
							Log.v( "teapot" , "Root3D ----- MiuiV5FolderBoxBlur ---- donot add folder into root ---" );
						}
						else
						{
							addView( folder );
						}
						this.hideOtherView();
					}
					else
					{
						folder.addViewAt( folder.mFolderIndex , folder.mFolderMIUI3D );
						this.addViewBefore( hotseatBar , folder.blurredView );
						addView( folder );
						this.hideOtherView();
						folder.color.a = 0f;
						folder.ishide = true;
					}
					Log.i( "blur" , "blur blurredView:" + ( System.currentTimeMillis() - time ) );
				}
				folder.blurCompleted = true;
			}
		}
	}
	
	public void hideOtherView()
	{
		if( hotseatBar != null )
		{
			hotseatBar.color.a = 0f;
		}
		if( pageIndicator != null )
		{
			pageIndicator.color.a = 0f;
		}
		if( workspace != null )
		{
			if( !( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) )
				workspace.color.a = 0f;
			else
			{
				//when in miui edit mode, folder's parentView is also workspace
				View3D celllayout = workspace.getCurrentView();
				if( celllayout instanceof CellLayout3D )
				{
					( (CellLayout3D)celllayout ).b_cell_editmode_bg_show = false;
					for( int i = 0 ; i < ( (CellLayout3D)celllayout ).getChildCount() ; i++ )
					{
						View3D child = ( (CellLayout3D)celllayout ).getChildAt( i );
						if( child instanceof FolderIcon3D )
						{
							long folder_id = folder.getItemInfo().id;
							if( ( (FolderIcon3D)child ).getItemInfo().id != folder_id )
							{
								Color color = child.getColor();
								color.a = 0.0f;
								child.setColor( color );
							}
						}
						else
						{
							Color color = child.getColor();
							color.a = 0.0f;
							child.setColor( color );
						}
					}
				}
			}
		}
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && widgetHost != null )
		{
			widgetHost.color.a = 0f;
		}
	}
	
	public void showOtherView()
	{
		if( hotseatBar != null )
		{
			hotseatBar.color.a = 1f;
		}
		if( pageIndicator != null )
		{
			pageIndicator.color.a = 1f;
		}
		if( workspace != null )
		{
			if( !( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) )
				workspace.color.a = 1f;
			else
			{
				//when in miui edit mode, folder's parentView is also workspace
				View3D celllayout = workspace.getCurrentView();
				if( celllayout instanceof CellLayout3D )
				{
					( (CellLayout3D)celllayout ).b_cell_editmode_bg_show = true;
					for( int i = 0 ; i < ( (CellLayout3D)celllayout ).getChildCount() ; i++ )
					{
						View3D child = ( (CellLayout3D)celllayout ).getChildAt( i );
						if( child instanceof FolderIcon3D )
						{
							long folder_id = folder.getItemInfo().id;
							if( ( (FolderIcon3D)child ).getItemInfo().id != folder_id )
							{
								Color color = child.getColor();
								color.a = 1.0f;
								child.setColor( color );
							}
						}
						else
						{
							Color color = child.getColor();
							color.a = 1.0f;
							child.setColor( color );
						}
					}
				}
			}
		}
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && widgetHost != null )
		{
			widgetHost.color.a = 1f;
		}
		if( folder != null )
		{
			folder.color.a = 1f;
			folder.ishide = false;
			System.out.println( "show folder.ishide = " + folder.ishide );
		}
	}
	
	private boolean dealMIUIFolderToRoot3D(
			FolderIcon3D folderIcon3D )
	{
		float targetOpacity = 0.1f;
		float duration = 0.3f;
		if( folderOpened )
		{
			Log.e( "whywhy" , " folderOpened=" + folderOpened );
			return false;
		}
		dragLayer.removeDropTarget( folderIcon3D );
		folderOpened = true;
		folder = folderIcon3D;
		Object obj = folderIcon3D.getTag();
		String tag = null;
		if( obj != null && obj instanceof String )
		{
			tag = (String)obj;
			if( tag.equals( "miui_v5_folder" ) )
			{
				targetOpacity = 0.2f;
			}
		}
		// teapotXu add start for icon3D's double-click optimization
		if( workspace != null )
			workspace.clearDragObjs();
		// teapotXu add end
		if( ( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) // zhenNan.ye
		|| DefaultLayout.miui_v5_folder ) )
		{
			if( folder.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT )
			{
				this.addViewBefore( workspace , pageIndicator );
				this.addViewBefore( workspace , hotseatBar );
			}
		}
		return true;
	}
	
	private void resetWorkspace(
			FolderIcon3D folder )
	{
		if( folder == null )
		{
			return;
		}
		if( folder.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT )
		{
			this.addViewAfter( appHost , pageIndicator );
			this.addViewAfter( pageIndicator , hotseatBar );
		}
	}
	
	private void dealMIUIFolderToCellLayout(
			FolderIcon3D view )
	{
		if( view == null )
		{
			return;
		}
		if( view != null && view instanceof FolderIcon3D )
		{
			FolderIcon3D folderIcon3D = (com.iLoong.launcher.Folder3D.FolderIcon3D)view;
			{
				if( folderIcon3D.mInfo.contents.size() >= 0 || folderIcon3D.mFolderMIUI3D.getColseFolderByDragVal() == false )
				{
					boolean bNeedAddDragLayer = true;
					if( folderIcon3D.mInfo.contents.size() == 1 )
					{
						ShortcutInfo tempInfo = folderIcon3D.mInfo.contents.get( 0 );
						if( tempInfo.container < 0 )
						{
							workspace.removeViewInWorkspace( folderIcon3D );
							bNeedAddDragLayer = false;
						}
					}
					if( bNeedAddDragLayer )
					{
						if( folderIcon3D.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT )
						{
							dragLayer.addDropTargetBefore( pageContainer.pageEdit , (DropTarget3D)folderIcon3D );
						}
						else if( folderIcon3D.getFromWhere() == FolderIcon3D.FROM_HOTSEAT )
						{
							dragLayer.addDropTarget( (DropTarget3D)folderIcon3D );
						}
						folderIcon3D = null;
					}
				}
			}
		}
		folderOpened = false;
		if( view != null && view instanceof FolderIcon3D )
		{
			FolderIcon3D folderIcon3D = (com.iLoong.launcher.Folder3D.FolderIcon3D)view;
			if( folderIcon3D.getParent() == this && !( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) )
			{
				if( folderIcon3D.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT )
				{
					workspace.addBackInScreen( folderIcon3D , folderIcon3D.mInfo.x , folderIcon3D.mInfo.y );
				}
				else if( folderIcon3D.getFromWhere() == FolderIcon3D.FROM_HOTSEAT )
				{
					ArrayList<View3D> tmp = new ArrayList<View3D>();
					tmp.add( (View3D)view );
					hotseatBar.getDockGroup().backtoOrig( tmp );
				}
			}
			if( folder != null && !( folder.mFolderMIUI3D.isbDragItemClose() ) )
				folder = null;
		}
		if( dragLayer.isVisible() && !dragLayer.draging )
		{
			workspace.dropAnim = false;
			if( dragLayer.getDragList().size() > 0 && dragLayer.onDrop() == null )
			{
				dropListBacktoOrig( dragLayer.getDragList() );
			}
			dragLayer.removeAllViews();
			dragLayer.hide();
			workspace.dropAnim = true;
			trashIcon.hide();
			if( DefaultLayout.generate_new_folder_in_top_trash_bar && folderTarget != null )
			{
				folderTarget.hide();
			}
		}
		if( dragLayer.isVisible() && dragLayer.draging )
		{
			// teapotXu add start for add new folder in top-trash bar
			if( DefaultLayout.generate_new_folder_in_top_trash_bar )
			{
				if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
				{
					trashIcon.setPosFlag( TrashIcon3D.TRASH_POS_TOP_FLAG_RIGHT );
					FolderTarget3D.setVisibleFlag( FolderTarget3D.FLAG_VISIBLE );
				}
			}
			// teapotXu add end
			trashIcon.show();
			if( folderTarget != null )
			{
				folderTarget.show();
			}
		}
		else
		{
			SendMsgToAndroid.sendShowWorkspaceMsg();
		}
		// teapotXu add start
		if( this.setupMenu != null && this.setupMenu.visible == true )
		{
			this.setupMenu.hide();
		}
		// teapotXu add end
		if( goHome )
		{
			goHome = false;
			workspace.scrollTo( workspace.getHomePage() );
		}
	}
	
	public ApplicationListHost getListToAdd()
	{
		return listToAdd;
	}
	
	// teapotXu add start
	public boolean SetupMenuShownPermition()
	{
		// when the folder of workspace is playing openning or closing
		// animation, don't pop the setupmenu
		if( this.folder != null && this.folder.bAnimate == true || listToAdd.isVisible() )
		{
			return false;
		}
		//when circle popup is shown, first distory the popup window
		if( workspace.getCirclePopWin3d() != null )
		{
			// distroy the circlePopwin
			workspace.onCtrlEvent( workspace.getCirclePopWin3d() , circlePopWnd3D.CIRCLE_POP_DESTROY_EVENT );
			return false;
		}
		return true;
	}
	
	// teapotXu add end
	// zhujieping add
	public boolean isDragEnd = false;
	
	@Override
	public boolean pointerInParent(
			float x ,
			float y )
	{
		float scaleX = getScaleX();
		float scaleY = getScaleY();
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable )
		{
			if( isDragEnd )
			{// super.pointerInParent(x, y)计算的是scale变化后的坐标
				if( scaleX < 1 || scaleY < 1 )
				{
					point.x = x - this.x;
					point.y = y - this.y;
					return( ( point.x >= 0 && point.x < width ) && ( point.y >= 0 && point.y < height ) );
				}
			}
		}
		return super.pointerInParent( x , y );
	}
	
	// zhujieping add end
	// xiatian add start //EffectPreview
	public void setWorkspaceEffectPreview3D(
			View3D v )
	{
		this.mWorkspaceEffectPreview = (EffectPreview3D)v;
		this.mWorkspaceEffectPreview.hide();
		this.addView( v );
	}
	
	public void setApplistEffectPreview3D(
			View3D v )
	{
		this.mApplistEffectPreview = (EffectPreview3D)v;
		this.mApplistEffectPreview.hide();
		this.addView( v );
	}
	
	public void setEffectPreviewTips3D(
			View3D v )
	{
		this.mEffectPreviewTips3D = (EffectPreviewTips3D)v;
		this.mEffectPreviewTips3D.hide();
		this.addView( v );
	}
	
	public void dealEffectPreview(
			Intent intent )
	{
		int mEffectType = intent.getIntExtra( EffectPreview3D.ACTION_EFFECT_PREVIEW_EXTRA_TYPE , EffectPreview3D.TYPE_WORKSPACE );
		mIsInEffectPreviewMode = mEffectType;
		int mEffectIndex = intent.getIntExtra( EffectPreview3D.ACTION_EFFECT_PREVIEW_EXTRA_INDEX , 0 );
		if( mEffectType == EffectPreview3D.TYPE_WORKSPACE )
		{
			if( DefaultLayout.enable_camera )
			{
				workspace.removeCameraView();
			}
			if( DefaultLayout.show_music_page )
			{
				workspace.removeMusicView();
			}
			showWorkspaceEffectPreview( mEffectIndex );
		}
		else if( mEffectType == EffectPreview3D.TYPE_APPLIST )
		{
			if( appHost != null && AppHost3D.appBar != null && AppHost3D.appBar.tabIndicator != null )
			{
				AppHost3D.appBar.tabIndicator.tabId = AppBar3D.TAB_CONTENT;
			}
			showApplistEffectPreview( mEffectIndex );
		}
	}
	
	private void showWorkspaceEffectPreview(
			int mEffectIndex )
	{
		if( Root3D.appHost.isVisible() )
		{
			mNeedDelayWorkspaceEffectPreview = mEffectIndex;
			showWorkSpaceFromAllApp();
			return;
		}
		// teapotXu add start
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			stopMIUIEditEffectNoAnim();
		}
		// teapotXu add end
		mWorkspaceEffectPreview.startWorkspaceEffectPreviewAnim( mEffectIndex , hotseatBar );
		mNeedDelayWorkspaceEffectPreview = -1;
	}
	
	private void showApplistEffectPreview(
			int mEffectIndex )
	{
		if( this.workspace.isVisible() )
		{
			mNeedDelayApplistEffectPreview = mEffectIndex;
			// teapotXu add start
			if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				stopMIUIEditEffectNoAnim();
			}
			// teapotXu add end
			showAllAppFromWorkspace();
			return;
		}
		// teapotXu add start: when enter effectPreview in Scene, pageIndicator
		// is visible
		if( pageIndicator.getVisible() == true )
		{
			pageIndicator.hide();
		}
		// teapotXu add end
		mApplistEffectPreview.startApplistEffectPreviewAnim( mEffectIndex );
		mNeedDelayApplistEffectPreview = -1;
	}
	
	public boolean isWorkspaceEffectPreviewMode()
	{
		if( mWorkspaceEffectPreview.isVisible() )
		{
			return true;
		}
		return false;
	}
	
	public boolean isApplistEffectPreviewMode()
	{
		if( mApplistEffectPreview.isVisible() )
		{
			return true;
		}
		return false;
	}
	
	public void backToBoxEffectTab()
	{
		mEffectPreviewTips3D.backToBoxEffectTab();
	}
	
	private Timeline animation_line;
	
	public void hideorShowWorkspace(
			boolean isanim )
	{
		animation_line = Timeline.createParallel();
		if( !Workspace3D.isHideAll )
		{
			int current = workspace.getCurrentScreen();
			for( int i = 0 ; i < workspace.getChildCount() ; i++ )
			{
				View3D child = workspace.getChildAt( i );
				if( i == current && isanim )
				{
					animation_line.push( Tween.to( child , View3DTweenAccessor.SCALE_XY , 0.3f ).target( 0f , 0f ).ease( Cubic.IN ).delay( 0 ) );
					child.setOrigin( hotseatBar.x + hotseatBar.width / 2 , hotseatBar.y + hotseatBar.height / 2 );
					animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
				}
				else
				{
					child.hide();
				}
			}
			Workspace3D.isHideAll = true;
		}
		else
		{
			Workspace3D.isHideAll = false;
			int current = workspace.getCurrentScreen();
			View3D child = workspace.getChildAt( current );
			child.show();
			if( isanim )
			{
				child.setScale( 0f , 0f );
				animation_line.push( Tween.to( child , View3DTweenAccessor.SCALE_XY , 0.3f ).target( 1f , 1f ).ease( Cubic.IN ).delay( 0 ) );
				child.setOrigin( hotseatBar.x + hotseatBar.width / 2 , hotseatBar.y + hotseatBar.height / 2 );
				animation_line.start( View3DTweenAccessor.manager ).setCallback( new TweenCallback() {
					
					@Override
					public void onEvent(
							int type ,
							BaseTween source )
					{
						animation_line.free();
						animation_line = null;
						workspace.initView();
						hotseatBar.getDockGroup().setTakeinBitmap();
					}
				} );
			}
			else
			{
				hotseatBar.getDockGroup().setTakeinBitmap();
				workspace.initView();
			}
		}
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( iLoongLauncher.isShowNews )
		{
			return true;
		}
		return super.onClick( x , y );
	}
	
	public void showSuitableSeat()
	{
		if( getAppHost().getVisible() )
			return;
		if( workspace.curIsMusicView() )
		{
			hotseatBar.stopShakeTween();
			hotseatBar.setPosition( hotseatBar.getX() , -R3D.seatbar_hide_height );
			if( musicSeat != null )
			{
				musicSeat.showNoAnim();
			}
			if( cameraSeat != null )
			{
				cameraSeat.hideNoAnim( 1 );
			}
		}
		else if( workspace.curIsCameraView() )
		{
			hotseatBar.stopShakeTween();
			hotseatBar.setPosition( hotseatBar.getX() , -R3D.seatbar_hide_height );
			if( cameraSeat != null )
			{
				cameraSeat.showNoAnim();
			}
			if( musicSeat != null )
			{
				musicSeat.hideNoAnim( 1 );
			}
		}
		else
		{
			hotseatBar.setPosition( hotseatBar.getX() , 0 );
			if( musicSeat != null )
			{
				musicSeat.hideNoAnim();
			}
			if( cameraSeat != null )
			{
				cameraSeat.hideNoAnim();
			}
		}
	}
	
	public void particleScrollRefresh(
			float viewx ,
			float viewy ,
			float x ,
			float y )
	{
		if( Math.sqrt( ( lastx - viewx - x ) * ( lastx - viewx - x ) + ( lasty - this.y - y ) * ( lasty - viewy - y ) ) < DefaultLayout.particle_scroll_distance )
		{
			lastupdatex = viewx + x;
			lastupdatey = viewy + y;
			newUpdateParticle( Desktop3DListener.currentParticleType , viewx + x , viewy + y );
		}
		else
		{
			particleStart( viewx , viewy , x , y );
		}
	}
	
	public void particleStart(
			float viewx ,
			float viewy ,
			float x ,
			float y )
	{
		particleSetRepeatPosition( viewx , viewy , x , y );
		newStartParticle( Desktop3DListener.currentParticleType , viewx + x , viewy + y );
	}
	
	public void particleSetRepeatPosition(
			float viewx ,
			float viewy ,
			float x ,
			float y )
	{
		lastx = viewx + x;
		lasty = viewy + y;
		lastupdatex = viewx + x;
		lastupdatey = viewy + y;
	}
	
	public PageIndicator3D getPageIndicator()
	{
		return pageIndicator;
	}
	
	public Workspace3D getWorkspace()
	{
		return workspace;
	}
	
	@Override
	public void setActionListener()
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public void OnAction(
			int actionid ,
			Bundle bundle )
	{
		// TODO Auto-generated method stub
	}
	
	IOnRemoveCallBack OnRemoveCallBackImpl = new IOnRemoveCallBack() {
		
		@Override
		public void onRemoveCompleted()
		{
			trashIcon.forceRecycle( dragLayer.getDragList() );
			trashIcon.set( true );
			trashIcon.hide();
			dragLayer.removeAllViews();
			dragLayer.hide();
		}
	};
}
