package com.iLoong.launcher.Desktop3D;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.cooeecomet.launcher.R;
import com.iLoong.RR;
import com.iLoong.launcher.Folder3D.Folder3D;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.Folder3D.FolderMIUI3D;
import com.iLoong.launcher.HotSeat3D.DockbarObjcetGroup;
import com.iLoong.launcher.HotSeat3D.HotDockGroup;
import com.iLoong.launcher.HotSeat3D.HotGridView3D;
import com.iLoong.launcher.HotSeat3D.HotObjBackGround3D;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.HotSeat3D.ObjButton;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.MenuActionListener;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.SetupMenu.Actions.ShowDesktop;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.ParticleAnim.ParticleCallback;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupOBJ3D;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.app.AppListDB;
import com.iLoong.launcher.app.LauncherModel;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.FolderInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.FeatureConfig;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.desktopEdit.DesktopEdit;
import com.iLoong.launcher.dockbarAdd.AddApp;
import com.iLoong.launcher.dockbarAdd.AddFolder;
import com.iLoong.launcher.dockbarAdd.AddFolder.myAppAdd;
import com.iLoong.launcher.dockbarAdd.AddShortcut;
import com.iLoong.launcher.dockbarAdd.AddWidget;
import com.iLoong.launcher.dockbarAdd.CooeeIcon3D;
import com.iLoong.launcher.dockbarAdd.WidgetAddList;
import com.iLoong.launcher.scene.SceneManager;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.widget.Widget;

import dalvik.system.DexClassLoader;


// import com.iLoong.launcher.min3d.ObjectView;
public class Root3D extends ViewGroup3D implements MenuActionListener
{
	
	public final static int MSG_SET_WALLPAPER_OFFSET = 10000;
	private static boolean enable_show_fps = false;
	private static int clickCount;
	private static long currentTimeMillis;
	private BitmapFont bitmapFont;
	private WallpaperManager mWallpaperManager;
	public static boolean mainMenuEntry = false;
	private DragLayer3D dragLayer;
	private DesktopEdit desktopEdit; // added by Hugo.ye 
	private boolean bExitDesktopEditMode;
	public AppHost3D appHost;
	private Workspace3D workspace;
	private TrashIcon3D trashIcon;
	// private HotButton hotButton;
	private PageIndicator3D pageIndicator;
	public HotSeat3D hotseatBar;
	public ImageView3D hostBg;
	public boolean folderOpened = false;
	public static FolderIcon3D folder;
	public static iLoongLauncher launcher;
	private boolean goHome = false;
	private Timer timer = null;
	private worksapceTweenTask TweenTimerTask;
	private static float workspaceTweenDuration = 0.1f;
	private Timeline workspaceAndAppTween;
	private Tween s3EffectTween;
	private Tween s4ScrollEffectTween;
	private static final int DRAG_END = 6;
	public boolean is_delete = false;
	private View3D focusWidget;
	private float[] focusWidgetPos;
	private float focusWidgetMov;
	private View3D shadowView;
	private float s3EffectScale = 0.8f;
	// wanghongjian add start //enable_DefaultScene
	private float pageIndicatorOldY = 0;// 自由桌面参数
	private float hotbuttonx = 0;// 自由桌面参数
	public static boolean isFolder = false;// 判断在文件夹状态下不能进去场景桌面，true表示不可进入
	public static boolean isDragon = false;// 判断在长龙状态下不能进去场景桌面，true表示不可进入
	private boolean freeMainCreated = false;
	protected static View3D freemain = null;
	public int applistPage = 0;
	private Tween stopFreeTween = null;
	private Tween statrFreeTween = null;// 场景桌面开始动画
	public static String scenePkg = "";
	public static String sceneCls = "";
	private Handler sceneHandler = null;
	private SharedPreferences preferences = null;
	private Intent sceneReciveIntent = null;
	private Intent addAppIntent = null;
	private Intent removeAppIntent = null;
	private Intent deleteSceneIntent = null;
	public Intent upSceneIntent = null;
	private boolean isSceneDown = false;// 判断场景桌面是否拉下，true表示拉下
	public static boolean isSceneTheme = false;// 判断场景主题是否选择，true表示选择，当为false时不进行场景操作
	public SetupMenu oldSetupMenu = null;
	// wanghongjian add end
	public static boolean scroll_indicator = false;
	public static Root3D mInstance;
	public static DockbarObjcetGroup dockBarButtons;
	public static Vector2 clickPoint = new Vector2();
	public NinePatch layoutFrame;
	public NinePatch layoutBg;
	public Timeline timeLine;
	public static Mesh[] line = new Mesh[4];
	public final float ScreenShotDegree = this.height / ( this.width / 4.0f );
	public float headPixels = 11;
	public float bottomPixels = 37;
	public float middlePixels = 119;
	private Timeline timeline;
	private Timeline viewLine;
	public static ViewGroup3D widgetLayout;
	public View3D splitScreenBg;
	public View3D splitScreenBg2;
	// public static ScreenView screenBgView;
	public static ScreenGroup screenGroup;
	//  public TextureRegion middleScreen;
	Icon3D[] view = new Icon3D[4];
	private View3D touchView;
	public View3D downViewBg;
	public ViewGroup3D downViewGrp;
	public View3D downView;
	public static View3D upViewBg;
	public static View3D upView;
	public ViewGroup3D upViewGrp;
	// public Texture screenTexture;
	public TextureRegion gdxSreenRegion = new TextureRegion();
	//public static  TextureRegion wallpaperTextureRegion =new TextureRegion();
	private float parentAlpha;
	private Texture frameTexture;
	private FrameBuffer fb = null;
	public View3D screenBg;
	public float screenScale = 1.0f;
	private boolean widgetLayoutExist = false;
	public View3D[] lineView = new View3D[2];
	public static final int SPLIT_SCREEN_OPEN_VIEW = 0;
	public static final int SPLIT_SCREEN_CLOSE_VIEW = 1;
	public static final int SPLIT_SCREEN_HIDE_LINE_VIEW = 3;
	public static final int SPLIT_SCREEN_ONLY_CLOSE_UP_VIEW = 4;
	public int userAction = 0;
	public final float openDelay = 0.2f;
	public static View3D upWallpaperView;
	public static View3D downWallpaperView;
	public View3D frameSurface[] = new View3D[2];
	public static ScreenUtils3D screenUtils = new ScreenUtils3D();
	FeaturePanel fp;
	public boolean bLongClickOnWorkspace;
	
	class worksapceTweenTask extends TimerTask
	{
		
		@Override
		public void run()
		{
			dealworkspaceTweenFinish();
		}
	}
	
	private void startRootTimer(
			long duration )
	{
		if( timer != null )
			timer.cancel();
		TweenTimerTask = new worksapceTweenTask();
		timer = new Timer();
		timer.schedule( TweenTimerTask , duration );
	}
	
	private void stopRootTimer()
	{
		if( timer != null )
		{
			TweenTimerTask.cancel();
			timer.cancel();
			timer = null;
		}
	}
	
	public AppHost3D getAppHost()
	{
		return appHost;
	}
	
	public HotSeat3D getHotSeatBar()
	{
		return hotseatBar;
	}
	
	public HotDockGroup getHotDockGroup()
	{
		return hotseatBar.getDockGroup();
	}
	
	public Root3D(
			String name )
	{
		super( name );
		mInstance = this;
		//		if (enable_show_fps) {
		bitmapFont = new BitmapFont( Gdx.files.internal( "test.fnt" ) , Gdx.files.internal( "test.png" ) , false );
		//		}
		//zqh add start
		//this is assigned to store screen buffer..
		// in order to get the screen shot timely it is necessary to keep it all time. 
		fb = new FrameBuffer( Format.RGBA8888 , (int)( this.getWidth() ) , (int)( this.getHeight() ) , true );
		//zqh add end
		// wanghongjian add start //enable_DefaultScene
		setActionListener();
		if( FeatureConfig.enable_DefaultScene )
		{
			this.setSceneReciverIntent();
			sceneHandler = new Handler( iLoongLauncher.getInstance().getMainLooper() );
		}
		// wanghongjian add end
		hotseatBar = new HotSeat3D( "HotSeat3DBar" );
		this.addView( hotseatBar );
		hostBg = new ImageView3D( "hostBackground" , R3D.getTextureRegion( R3D.apphost_bg ) );
		hostBg.setSize( Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
		hostBg.setPosition( 0 , 0 );
		hostBg.hide();
		this.addView( hostBg );
		// TODO Auto-generated constructor stub
	}
	
	public void setLauncher(
			iLoongLauncher l )
	{
		this.launcher = l;
		mWallpaperManager = WallpaperManager.getInstance( launcher );
	}
	
	public void setDesktopEdit(
			View3D v )
	{
		this.desktopEdit = (DesktopEdit)v;
		this.desktopEdit.hide();
		this.addView( v );
	}
	
	public DesktopEdit getDesktopEdit()
	{
		return this.desktopEdit;
	}
	
	public void enterDesktopEditMode()
	{
		if( !desktopEdit.isVisible() )
		{
			workspace.setScaleZ( 0.1f );
			int currentPage = workspace.getCurrentScreen();
			appHost.hide();
			workspace.hide();
			workspace.releaseFocus();
			pageIndicator.hide();
			trashIcon.hide();
			desktopEdit.setupDesktopEditMode( workspace.getViewList() , workspace.getHomePage() , currentPage );
			desktopEdit.show();
			desktopEdit.setUser( (float)currentPage / ( workspace.getPageNum() - 1 ) );
		}
	}
	
	public void exitDesktopEditMode()
	{
		bExitDesktopEditMode = true;
		desktopEdit.hide();
		int curPageIndex = desktopEdit.getCurrentPageIndex();
		desktopEdit.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0.5f , (float)curPageIndex / ( workspace.getPageNum() - 1 ) , 0 , 0 ).setCallback( this );
		workspace.setCurrentPage( curPageIndex );
		workspace.setScaleZ( 1f );
		workspace.show();
		appHost.hide();
		if( !pageIndicator.isVisible() )
		{
			pageIndicator.show();
		}
		if( DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
		{
			trashIcon.hide();
			trashIcon.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , trashIcon.x , 0 , 0 );
		}
		if( dragLayer.isVisible() )
		{
			if( dragLayer.draging )
			{
				trashIcon.show();
				if( DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
				{
					trashIcon.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , trashIcon.x , 0 , 0 );
				}
			}
			else
			{
				workspace.forceSetCellLayoutDropType( workspace.getCurrentPage() );
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
			SendMsgToAndroid.sendShowWorkspaceMsg();
		}
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
	
	public void setPageIndicator(
			View3D v )
	{
		this.pageIndicator = (PageIndicator3D)v;
		this.addView( v );
		if( iLoongLauncher.showAllAppFirst )
			pageIndicator.hideNoAnim();
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
		// this.addView(new ObjectView("3d"));
		trashIcon.bringToFront();
		this.dragLayer.hide();
		this.addView( v );
	}
	
	public void saveSceneIndex(
			final int index ,
			final String pkg ,
			final String cls )
	{
		if( sceneHandler != null )
		{
			sceneHandler.post( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					if( preferences == null )
					{
						preferences = iLoongLauncher.getInstance().getSharedPreferences( "scene_first" , Activity.MODE_WORLD_WRITEABLE );
					}
					if( preferences != null )
					{
						preferences.edit().putString( "scenepkg" , pkg ).commit();
						preferences.edit().putString( "scenecls" , cls ).commit();
						preferences.edit().putInt( "sceneIndex" , index ).commit();
						preferences.edit().commit();
					}
				}
			} );
		}
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
	}
	
	private void dropListBacktoHotSeat(
			ArrayList<View3D> child_list )
	{
		View3D view;
		ArrayList<ItemInfo> listInfo = new ArrayList<ItemInfo>();
		view = child_list.get( 0 );
		ItemInfo info = ( (IconBase3D)view ).getItemInfo();
		listInfo.add( info );
		hotseatBar.bindBackItems( listInfo );
	}
	
	private void dropListBacktoOrig(
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
		{/*
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
	
	// @Override
	// public boolean touchDown (float x, float y, int pointer) {
	// super.onTouchDown(x, y, pointer);
	// return true;
	// }
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
		workspace.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , workspaceTweenDuration , s3EffectScale , s3EffectScale , 0f );
		workspace.setUser( 0 );
		workspace.startTween( View3DTweenAccessor.USER , Cubic.OUT , workspaceTweenDuration , 1 , 0f , 0f );
	}
	
	public void stopDragS3Effect(
			boolean is_delete )
	{
		workspace.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , workspaceTweenDuration , 1f , 1f , 0f );
		workspace.setUser( 1f );
		s3EffectTween = workspace.startTween( View3DTweenAccessor.USER , Cubic.OUT , workspaceTweenDuration , 0f , 0f , 0f ).setUserData( DRAG_END ).setCallback( this );
	}
	
	public void startDragScrollIndicatorEffect()
	{
		if( ClingManager.getInstance().folderClingFired )
			SendMsgToAndroid.sendRefreshClingStateMsg();
		Workspace3D.is_longKick = true;
		workspace.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , workspaceTweenDuration , s3EffectScale , s3EffectScale , 0f );
		workspace.setUser2( 0 );
		workspace.startTween( View3DTweenAccessor.USER2 , Cubic.OUT , workspaceTweenDuration , 1 , 0f , 0f );
	}
	
	public void stopDragScrollIndicatorEffect(
			boolean is_delete )
	{
		workspace.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , workspaceTweenDuration , 1f , 1f , 0f );
		workspace.setUser2( 1f );
		s4ScrollEffectTween = workspace.startTween( View3DTweenAccessor.USER2 , Cubic.OUT , workspaceTweenDuration , 0f , 0f , 0f ).setUserData( DRAG_END ).setCallback( this );
	}
	
	private void launcherGlobalSearch()
	{
		Intent intent = new Intent();
		intent.setAction( "android.search.action.GLOBAL_SEARCH" );
		List<ResolveInfo> allMatches = launcher.getPackageManager().queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
		// intent.setComponent(new ComponentName("com.baidu.searchbox",
		// "com.baidu.searchbox.MainActivity" ));
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
		if( sender instanceof HotSeat3D )
		{
			switch( event_id )
			{
			// case HotSeat3D.MSG_MOVETO_HOTBUTTON:
			// //HotMainMenuView3D.onFocus=true;
			// break;
				case CellLayout3D.MSG_REFRESH_PAGE:
					Log.v( "focus" , "CellLayout3D.MSG_REFRESH_PAGE" );
					Object refreshObj = sender.getTag();
					if( refreshObj instanceof Integer )
					{
						int tag = (Integer)refreshObj;
						CellLayout3D layout = this.workspace.getCurrentCellLayout();
						// layout.refreshLocation(tag);
					}
					break;
				//			case HotSeat3D.MSG_SHOW_ADD_APP_VIEW:
				//			
				//				addViewBefore(dragLayer, desktopEdit);
				//				desktopEdit.showAddEditMode();
				//				break;
				case HotSeat3D.MSG_SHOW_FOLDER_VIEW:
					addViewBefore( dragLayer , desktopEdit );
					showAddFolderEditMode();
					break;
				case HotSeat3D.MSG_SHOW_ADD_SHORTCUT_VIEW:
					addViewBefore( dragLayer , desktopEdit );
					showShortcutEditMode();
					break;
				case HotSeat3D.MSG_SHOW_ADD_WIDGET_VIEW:
					addViewBefore( dragLayer , desktopEdit );
					showWidgetEditMode();
					break;
			}
		}
		if( sender instanceof AddApp )
		{
			switch( event_id )
			{
				case AddApp.MSG_HIDE_ADD_VIEW:
					removeAddEditMode();
					addViewBefore( hotseatBar , desktopEdit );
					break;
			}
		}
		//		if(sender instanceof GuidXinShou){
		//			switch (event_id) {
		//			case GuidXinShou.MSG_HIDE_ADD_VIEW:
		//				removeGuidXinShouEditMode();
		//				addViewBefore(hotseatBar, desktopEdit);
		//				break;
		//			}
		//		}
		if( sender instanceof myAppAdd )
		{
			switch( event_id )
			{
				case AddApp.MSG_HIDE_ADD_VIEW:
					removeAddFolderAppEditMode();
					addViewBefore( hotseatBar , desktopEdit );
					folder = null;
					break;
			}
		}
		if( sender instanceof AddFolder )
		{
			AddFolder folder = (AddFolder)sender;
			switch( event_id )
			{
				case AddFolder.MSG_HIDE_ADD_VIEW:
					if( folder.bFolderRename )
					{
						removeFolderRename();
					}
					else
					{
						removeAddFolderEditMode();
					}
					addViewBefore( hotseatBar , desktopEdit );
					break;
				case AddFolder.MSG_SHOW_ADD_APP:
					if( folder.bFolderRename )
					{
						removeFolderRename();
					}
					else
					{
						removeAddFolderEditMode();
						showAddFolderAddApp();
					}
					break;
			}
		}
		if( sender instanceof AddShortcut )
		{
			switch( event_id )
			{
				case AddShortcut.MSG_HIDE_ADD_VIEW:
					removeShortcutEditMode();
					addViewBefore( hotseatBar , desktopEdit );
					break;
				case AddShortcut.MSG_SHOW_SYSTEM_SHORTCUT_VIEW:
					removeShortcutEditMode();
					SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.system_shortcut ) );
					SendMsgToAndroid.sendAddShortcutMsg( 1 , 1 );
					addViewBefore( hotseatBar , desktopEdit );
					break;
			}
		}
		//		if(sender instanceof AddShortcutList){
		//			switch (event_id) {
		//			case AddShortcut.MSG_HIDE_ADD_VIEW:
		//				desktopEdit.removeShortcutEditMode();
		//				hotseatBar.show();
		//				break;
		//				
		//			case DragSource3D.MSG_START_DRAG:
		//				desktopEdit.removeShortcutEditMode();
		//				hotseatBar.show();
		//				break;
		//			}
		//		}
		if( sender instanceof AddWidget || sender instanceof WidgetAddList )
		{
			switch( event_id )
			{
				case AddWidget.MSG_HIDE_ADD_VIEW:
					removeWidgetEditMode();
					addViewBefore( hotseatBar , desktopEdit );
					break;
				case DragSource3D.MSG_START_DRAG:
					removeWidgetEditMode();
					desktopEdit.exitEditMode();
					addViewBefore( hotseatBar , desktopEdit );
					break;
				case AddWidget.MSG_WIDGET3D_SHORTCUT_CLICK:
					removeWidgetEditMode();
					//				desktopEdit.addWidget();
					//				desktopEdit.exitEditMode();
					//				workspace.onDrop(desktopEdit.getWidget(), 0, 0);
					iLoongLauncher.getInstance().addCometWidget( desktopEdit.getWidget() );
					addViewBefore( hotseatBar , desktopEdit );
					break;
				case AddWidget.MSG_SHOW_SYSTEM_WIDGET_VIEW:
					removeWidgetEditMode();
					SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.system_shortcut ) );
					SendMsgToAndroid.sendAddShortcutMsg( 0 , 0 );
					addViewBefore( hotseatBar , desktopEdit );
					break;
			}
		}
		if( sender instanceof HotObjBackGround3D )
		{
			switch( event_id )
			{
				case HotObjBackGround3D.MSG_HOTOBJ_OPEN:
					workspace.touchable = false;
					break;
			}
		}
		// if(sender instanceof HotMainMenuView3D){
		// switch(event_id){
		// case HotSeat3D.MSG_MOVETO_HOTSEAT:
		// this.hotseatBar.cellMoveToLast();
		// break;
		// case HotSeat3D.MSG_HOTKEY_CLICK:
		// Log.v("onCtrlEvent", "onCtrlEvent");
		// this.workspace.getCurrentCellLayout().resetCurrFocus();
		// break;
		// }
		//
		// }
		// if(sender instanceof HotSeat3D){
		// switch(event_id){
		// case HotSeat3D.MSG_HOTKEY_CLICK:
		// Log.v("onCtrlEvent", "onCtrlEvent");
		// this.workspace.getCurrentCellLayout().resetCurrFocus();
		// break;
		// }
		// }
		if( sender instanceof DragSource3D )
		{
			DragSource3D source = (DragSource3D)sender;
			switch( event_id )
			{
				case DragSource3D.MSG_START_DRAG:
					//				if (!pageContainer.isVisible()) {
					if( !desktopEdit.isVisible() )
					{
						// teapotXu add start for Folder in Mainmenu
						if( DefaultLayout.mainmenu_folder_function == true && appHost.getAppList3DMode() == AppList3D.APPLIST_MODE_UNINSTALL )
						{
							// 在AppList中不需要S3 Effect
						}
						else
							// teapotXu add end for Folder in Mainmenu
							startDragS3Effect();
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
					if( sender instanceof DesktopEdit )
					{
						if( desktopEdit.getPageList().size() >= 1 )
						{
							DesktopEdit.editModeChangeView.stopAllTween();
							DesktopEdit.editModeChangeView.startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.6f , 0 , 0 , 0 );
							trashIcon.show();
						}
					}
					else if( sender instanceof Workspace3D || sender instanceof HotSeat3D )
					{
						appHost.hide();
						desktopEdit.hide();
						if( !workspace.isVisible() )
							workspace.show();
						// if
						// (DefaultLayout.mainmenu_pos==HotGridView3D.MAINMENU_MIDDLE)
						// {
						trashIcon.show();
						//
						// }
						if( sender instanceof HotSeat3D )
						{
							hotseatBar.startMainGroupOutDragAnim();
						}
						// hotButton.changeState(HotMainMenuView3D.STATE_HOME);
					}
					else if( sender instanceof AppList3D )
					{
						hostBg.hide();
						appHost.releaseFocus();
						workspace.color.a = 1;
						hotseatBar.color.a = 1;
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
							// showPageContainer(-1);
							appHost.hide();
							desktopEdit.hide();
							workspace.show();
							hotseatBar.show();
							hotseatBar.setVisible( true );
							hotseatBar.touchable = true;
							if( DefaultLayout.hotseat_style_ex == false )
							{
								hotseatBar.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , workspaceTweenDuration , s3EffectScale , s3EffectScale , 0f );
							}
							trashIcon.show();
							if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
							{
							}
							else
							{
								trashIcon.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , trashIcon.x , 0 , 0 );
							}
							// if
							// (DefaultLayout.trash_icon_pos!=TrashIcon3D.TRASH_POS_TOP)
							// {
							// hotButton.hide();
							// }
							// if (DefaultLayout.hotseat_style_ex==true)
							// {
							// hotButton.show();
							// }
							pageIndicator.show();
							// hotButton.changeState(HotMainMenuView3D.STATE_HOME);
						}
					}
					else if( sender instanceof Folder3D )
					{
						// Folder3D.getInstance().DealButtonOKDown();
					}
					else if( sender instanceof FolderMIUI3D )
					{
						folder.mFolderMIUI3D.DealButtonOKDown();
					}
					// if(!pageContainer.isVisible() && !folderOpened)
					// dragLayer.setBorder(true);
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
					Log.v( "iconclick" , "root Icon3D" );
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
							else
							{
								//							SendMsgToAndroid.startActivity(tag);
								if( tag.title.equals( iLoongLauncher.getInstance().getResources().getString( R.string.mainmenu ) ) )
								{
									if( DefaultLayout.mainmenu_inout_no_anim )
									{
										showAllAppFromWorkspaceEx();
									}
									else
									{
										//									if (folderOpened) {
										//										if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
										//												|| DefaultLayout.miui_v5_folder) {
										//											if (folder.getParent() == this) {
										//												if (folder.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT) {
										//													workspace.addBackInScreen(folder,
										//															folder.mInfo.x, folder.mInfo.y);
										//												} else if (folder.getFromWhere() == FolderIcon3D.FROM_HOTSEAT) {
										//													folder.setSize(R3D.workspace_cell_width,
										//															R3D.workspace_cell_height);
										//													ArrayList<View3D> tmp = new ArrayList<View3D>();
										//													tmp.add((View3D) folder);
										//													hotseatBar.getDockGroup().backtoOrig(tmp);
										//													folder.setFolderIconSize(0, 0,
										//															folder.mInfo.x, folder.mInfo.y);
										//												}
										//
										//											}
										//										}
										//									}
										if( folderOpened )
										{
											final FolderIcon3D findFolder = getOpenFolder();
											if( findFolder != null )
											{
												mainMenuEntry = true;
												if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
												{
													if( findFolder.mFolderMIUI3D.bEnableTouch == true )
													{
														findFolder.mFolderMIUI3D.setIconPosition( icon.getX() + icon.width / 2 , icon.getY() + icon.height / 2 );
														findFolder.mFolderMIUI3D.DealButtonOKDown();
													}
												}
												else if( findFolder.mFolder.bEnableTouch == true )
												{
													findFolder.mFolder.DealButtonOKDown();
												}
											}
										}
										else
										{
											showAllAppFromWorkspace( icon.getX() + icon.width / 2 , icon.getY() + icon.height / 2 );
										}
									}
								}
								else if( tag.title.equals( iLoongLauncher.getInstance().getResources().getString( R.string.wallpapers ) ) )
								{
									SendMsgToAndroid.sendSelectWallpaper();
								}
								else if( tag.title.equals( iLoongLauncher.getInstance().getResources().getString( R.string.preferences ) ) )
								{
									SendMsgToAndroid.sendSelectZhuMianSheZhi();
								}
								else
								{
									SendMsgToAndroid.startActivity( tag );
								}
							}
						}
					}
					break;
			}
			return true;
		}
		if( sender instanceof DragLayer3D )
		{
			DragLayer3D dragLayer = (DragLayer3D)sender;
			switch( event_id )
			{
				case DragLayer3D.MSG_DRAG_INBORDER:
					// teapotXu add start for Folder in Mainmenu
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						if( appHost.isVisible() == true && dragLayer.is_dragging_in_apphost == true )
						{
							appHost.onCtrlEvent( sender , DragLayer3D.MSG_DRAG_INBORDER );
						}
						else
							DesktopEdit.bDragActionForIcon = true;
						enterDesktopEditMode();
					}
					else
					{
						// teapotXu add end for Folder in Mainmenu
						DesktopEdit.bDragActionForIcon = true;
						enterDesktopEditMode();
					}
					break;
				case DragLayer3D.MSG_DRAG_OVER:
				{
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
						// Log.d("launcher", "target="+target);
						if( target instanceof FolderIcon3D )
						{
							//						dragLayer.setColor(0f, 1f, 0f, 1f);	// removed by Hugo.ye 20131112
							folder = (FolderIcon3D)target;
						}
						else
						{
							if( folder != null )
							{
								folder.onDragOverLeave();
								folder = null;
							}
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
						if( !( target instanceof Workspace3D ) )
						{
							workspace.onDragOverLeave();
						}
						if( !( target instanceof HotSeat3D ) )
						{
							hotseatBar.onDragOverLeave();
						}
					}
					else if( desktopEdit.isVisible() )
					{
						DropTarget3D target = (DropTarget3D)dragLayer.getTag();
						if( target instanceof TrashIcon3D )
						{
							dragLayer.setColor( 1f , 0f , 0f , 0.5f );
							trashIcon.set( true );
						}
						else
						{
							trashIcon.set( false );
						}
						if( !( target instanceof HotSeat3D ) )
						{
							hotseatBar.onDragOverLeave();
						}
						if( target instanceof FolderIcon3D )
						{
							folder = (FolderIcon3D)target;
						}
						else
						{
							if( folder != null )
							{
								folder.onDragOverLeave();
								folder = null;
							}
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
						isDragEnd = false;
					DropTarget3D target = dragLayer.getTargetOver();
					// teapotXu add start for Folder in Mainmenu
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						// when drag end, reset this flag
						dragLayer.is_dragging_in_apphost = false;
					}
					// teapotXu add end for Folder in Mainmenu
					// Log.e("test", "target:1 " + target);
					// if (target instanceof TrashIcon3D &&
					// getNotEmptyFolder().size()>0)
					if( target instanceof TrashIcon3D )
					{
						// SendMsgToAndroid.sendPopDeleteFolderDialogMsg();
						is_delete = true;
					}
					else
					{
						is_delete = false;
					}
					if( desktopEdit.isVisible() )
					{
						if( target == null )
						{
							if( dragLayer.getDragList().size() > 0 )
							{
								if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
								{
									if( dragLayer.dropX > trashIcon.getX() && dragLayer.dropX < trashIcon.getX() + trashIcon.getWidth() && dragLayer.dropY > trashIcon.getY() && dragLayer.dropY < trashIcon
											.getY() + trashIcon.getHeight() )
									{
										dragLayer.setColor( 1f , 0f , 0f , 0.5f );
										trashIcon.set( true );
										desktopEdit.deletePage();
										DesktopEdit.bDragActionForPage = false;
									}
									else
									{
										if( DesktopEdit.bDragActionForIcon )
										{
											workspace.setScale( 1 , 1 );
											Workspace3D.is_longKick = false;
										}
										desktopEdit.dragActionFinish();
									}
								}
							}
						}
						else if( target instanceof TrashIcon3D )
						{
							if( dragLayer.getDragList().size() > 0 )
							{
								if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
								{
									desktopEdit.deletePage();
									DesktopEdit.bDragActionForPage = false;
								}
							}
						}
						else
						{
							if( DesktopEdit.bDragActionForIcon )
							{
								workspace.setScale( 1 , 1 );
								Workspace3D.is_longKick = false;
							}
							desktopEdit.dragActionFinish();
						}
					}
					// teapotXu add start for Folder in Mainmenu
					else if( DefaultLayout.mainmenu_folder_function == true && appHost.isVisible() && appHost.getAppList3DMode() == AppList3D.APPLIST_MODE_UNINSTALL )
					{
						// when folder is not open,
						if( !appHost.folderOpened )
						{
							DropTarget3D dropTarget = dragLayer.onDrop();
							// 如果此时拖动的icon放置到AppList里边的Folder时
							Log.v( "cooee" , "Root3D ---- MSG_DRAG_END --- drop in Mainmenu ---" );
							if( dropTarget != null )
							{
								// 如果此时拖动的icon放置到AppList里边的Folder时
								if( dropTarget instanceof FolderIcon3D && ( (View3D)dropTarget ).getParent() instanceof GridView3D )
								{
									// if(target != null && !(target instanceof
									// FolderIcon3D))
									// {
									// Log.e("cooee","------error--------test -");
									// }
									// 此时需要在AppList中Remove掉拖动到文件夹里边的ICON，并重新排序
									if( ( (FolderIcon3D)dropTarget ).getOnDrop() == true )
									{
										Log.v( "cooee" , "Root3D ---- MSG_DRAG_END --- move icon into folder in Mainmenu --- " );
										ArrayList<View3D> dragViewList = dragLayer.getDragList();
										appHost.removeDragViews( dragViewList );
									}
								}
								else
								{
									// in this condition dropTarget is always
									// AppHost, and it has been done in onDrop()
									Log.v( "cooee" , "Root3D ---- MSG_DRAG_END --- move icon in Mainmenu --- dropTarget: " + dropTarget.toString() );
								}
							}
							else
							{
								// error condition
								// if in this condition, add back the views in
								// draglist
								Log.e( "cooee" , "Root3D ---- MSG_DRAG_END --- drop in Mainmenu  target == null ---error---" );
							}
							appHost.setDragviewAddFromFolderStatus( false );
							dragLayer.removeAllViews();
							dragLayer.hide();
						}
					}
					// teapotXu add end for Folder in Mainmenu
					// else if (appHost.isVisible() && sidebar.isIconListShow())
					// {
					// if (target instanceof SideBar)
					// dragLayer.onDrop();
					// dragLayer.removeAllViews();
					// dragLayer.hide();
					// }
					else
					{
						appHost.hide();
						// Log.e("test", "target:2 " + target);
						if( !folderOpened )
						{
							// Log.e("test", "target:3 " + target);
							workspace.show();
							if( dragLayer.getDragList().size() > 0 )
							{
								DropTarget3D tmpTarget = dragLayer.onDrop();
								// Log.e("test", "target:3-1 tmpTarget " +
								// tmpTarget);
								if( tmpTarget == null )
								{
									// Log.e("test", "target:3-1 " + target);
									hotseatBar.getDockGroup().removeVirtueFolderIcon();
									dropListBacktoOrig( dragLayer.getDragList() );
								}
								else
								{
									// Log.e("test", "target:3-2 " + target);
									if( target instanceof HotSeat3D && !target.equals( tmpTarget ) )
									{
										hotseatBar.getDockGroup().removeVirtueFolderIcon();
									}
								}
							}
							else if( dragLayer.getDragList().size() > 0 && target instanceof HotSeat3D )
							{
								DropTarget3D tmpTarget = dragLayer.onDrop();
								// Log.e("test", "tmpTarget:4 " + tmpTarget);
								if( !( tmpTarget instanceof HotSeat3D ) )
								{
									// 防止快速仍到底边栏后生成文件夹然后ondrop却为桌面，导致底边栏生成的文件夹没删�?
									// Log.e("test", "target:4 " + target);
									hotseatBar.getDockGroup().removeVirtueFolderIcon();
								}
							}
							dragLayer.removeAllViews();
							dragLayer.hide();
							if( DefaultLayout.hotseat_style_ex )
							{
								hotseatBar.show();
							}
							pageIndicator.show();
							stopDragS3Effect( is_delete );
						}
						if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
						{
							// Log.e("test", "target:5 " + target);
							trashIcon.hide();
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
					break;
				case Workspace3D.MSG_LONGCLICK:
				{
					//				Object obj = workspace.getTag();
					//				if (obj instanceof Vector2)
					//					SendMsgToAndroid.sendAddShortcutMsg(
					//							(int) ((Vector2) obj).x, (int) ((Vector2) obj).y);
					//				else
					//					SendMsgToAndroid.sendAddShortcutMsg(0, 0);
					//				enterDesktopEditMode();
					//				hotseatBar.startDockbarTweenExternal();
					//zqh add start,
					/**
					 * do not remove any bellowing code unless you know what may be exactly caused.
					 
					    call this function to create a new layout consist of four widgets
					
					    when invoked ,this function would be creating a splitting screen effect,then widget,folder show out.
					 **/
					//			    screenUtils.setCurrScreenWPTexture();
					//			    splitScreen();
					bLongClickOnWorkspace = true;
					getStarted();
					//zqh end
					SendMsgToAndroid.vibrator( R3D.vibrator_duration );
					return true;
				}
				case Workspace3D.MSG_ADD_DRAGLAYER:
					dragLayer.addDropTargetBefore( desktopEdit , (DropTarget3D)workspace.getTag() );
					return true;
				case Workspace3D.MSG_DEL_POP_ALL:
					trashIcon.show();
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
					// launcher.addAppWidgetFromDrop(widget.widgetInfo.component,
					// new int[]{(int) widget.x,(int) widget.y});
					return true;
				case Workspace3D.MSG_ADD_SHORTCUT:
					Widget2DShortcut shortcut = (Widget2DShortcut)workspace.getTag();
					int[] pos = (int[])shortcut.getTag();
					SendMsgToAndroid.addShortcutFromAllApp( shortcut.widgetInfo.component , pos[0] , pos[1] );
					// launcher.addAppWidgetFromDrop(widget.widgetInfo.component,
					// new int[]{(int) widget.x,(int) widget.y});
					return true;
				case Workspace3D.MSG_PAGE_SHOW_EDIT:
					if( workspace.scaleX == 1.0f && dragLayer.isVisible() == false && workspace.xScale == 0 )
					{
						//					showPageEdit();		// deleted by zhenNan.ye
						enterDesktopEditMode();
						//					hotseatBar.startDockbarTweenExternal();
						hotseatBar.dockbarTurnDown();
					}
					return true;
				case Workspace3D.MSG_CLICK_SPACE:
				{
					long curTime = System.currentTimeMillis();
					int countMax = 10;
					float elpasedTime = 250;
					Log.i( "" , "###### MSG_CLICK_SPACE curTime = " + curTime + ", sysTimeRecord = " + currentTimeMillis + "######" );
					if( currentTimeMillis == 0 )
					{
						currentTimeMillis = curTime;
						Log.i( "" , "###### MSG_CLICK_SPACE sysTimeRecord == 0 ######" );
					}
					if( !enable_show_fps )
					{
						if( ( curTime - currentTimeMillis ) < elpasedTime )
						{
							if( clickCount < countMax )
							{
								clickCount++;
								currentTimeMillis = curTime;
								Log.i( "" , "###### MSG_CLICK_SPACE clickCount++; ######" );
							}
						}
						else
						{
							clickCount = 0;
							Log.i( "" , "###### MSG_CLICK_SPACE clickCount = 0; ######" );
						}
					}
					else
					{
						if( ( curTime - currentTimeMillis ) < elpasedTime )
						{
							if( clickCount > 0 )
							{
								clickCount--;
								currentTimeMillis = curTime;
								Log.i( "" , "###### MSG_CLICK_SPACE clickCount--; ######" );
							}
						}
						else
						{
							clickCount = countMax;
							Log.i( "" , "###### MSG_CLICK_SPACE clickCount = countMax; ######" );
						}
					}
					if( clickCount >= countMax )
					{
						enable_show_fps = true;
						currentTimeMillis = 0;
						Log.i( "" , "###### MSG_CLICK_SPACE clickCount >= countMax ######" );
					}
					else if( clickCount <= 0 )
					{
						enable_show_fps = false;
						currentTimeMillis = 0;
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
			}
		}
		if( sender instanceof PageIndicator3D )
		{
			switch( event_id )
			{
			//			case PageIndicator3D.PAGE_INDICATOR_CLICK:
			// if (pageContainer.isVisible()) {
			// pageContainer.hide(true);
			// }
			// else if (!appHost.isVisible()){
			// appHost.show();
			// appHost.setColor(new Color(appHost.color.r, appHost.color.g,
			// appHost.color.b, 0));
			// appHost.startTween(View3DTweenAccessor.OPACITY, Cubic.IN,
			// 0.3f, 1.0f, 0, 0).delay(0.1f);
			// workspace.startTween(View3DTweenAccessor.OPACITY, Cubic.OUT,
			// 0.3f, 0, 0, 0).setCallback(this);
			// workspace.setTag(null);
			// return true;
			// }
			// else{
			// workspace.show();
			// workspace.setColor(new Color(workspace.color.r,
			// workspace.color.g, workspace.color.b, 0));
			// workspace.startTween(View3DTweenAccessor.OPACITY, Cubic.IN,
			// 0.3f, 1.0f, 0, 0).delay(0.1f);
			// appHost.startTween(View3DTweenAccessor.OPACITY, Cubic.OUT,
			// 0.3f, 0, 0, 0).setCallback(this);
			// return true;
			// }
			// break;
				case PageIndicator3D.PAGE_INDICATOR_DROP_OVER:
				case PageIndicator3D.PAGE_INDICATOR_SCROLL:
					//				showPageContainer(-1);
					return true;
			}
		}
		if( sender instanceof DesktopEdit )
		{
			ArrayList<View3D> cellLayoutList;
			switch( event_id )
			{
				case DesktopEdit.MSG_DESKTOP_EDIT_MODE_EXIT:
					exitDesktopEditMode();
					return true;
				case DesktopEdit.MSG_DESKTOP_EDIT_SET_HOME:
					int homePage = desktopEdit.getHomePageIndex();
					PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).edit().putInt( "home_page" , homePage ).commit();
					workspace.setHomePage( homePage );
					return true;
				case DesktopEdit.MSG_DESKTOP_EDIT_APPEND_PAGE:
					int addIndex = desktopEdit.getCurrentPageIndex();
					CellLayout3D addedCell = new CellLayout3D( "celllayout" );
					cellLayoutList = workspace.getViewList();
					cellLayoutList.add( addIndex , addedCell );
					workspace.addView( addedCell );
					//				workspace.addPage(addedCell);
					cellLayoutList = workspace.getViewList();
					for( int i = addIndex + 1 ; i < cellLayoutList.size() ; i++ )
					{
						ViewGroup3D cell = (ViewGroup3D)cellLayoutList.get( i );
						for( int j = 0 ; j < cell.getChildCount() ; j++ )
						{
							View3D v = cell.getChildAt( j );
							if( v instanceof IconBase3D )
							{
								ItemInfo info = ( (IconBase3D)v ).getItemInfo();
								info.screen = i;
								addOrMoveDB( info );
							}
						}
					}
					ThemeManager.getInstance().getThemeDB().SaveScreenCount( workspace.getPageNum() );
					//				SendMsgToAndroid.sendAddWorkspaceCellMsg(-1);
					SendMsgToAndroid.sendAddWorkspaceCellMsg( addIndex );
					pageIndicator.setPageNum( workspace.getPageNum() );
					return true;
				case DesktopEdit.MSG_DESKTOP_EDIT_DELETE_PAGE:
					int toDeleteIndex = desktopEdit.getDragPage().pageIndex;
					cellLayoutList = workspace.getViewList();
					ViewGroup3D deletedCell = (ViewGroup3D)cellLayoutList.remove( toDeleteIndex );
					for( int i = toDeleteIndex ; i < cellLayoutList.size() ; i++ )
					{
						ViewGroup3D cell = (ViewGroup3D)cellLayoutList.get( i );
						for( int j = 0 ; j < cell.getChildCount() ; j++ )
						{
							View3D v = cell.getChildAt( j );
							if( v instanceof IconBase3D )
							{
								ItemInfo info = ( (IconBase3D)v ).getItemInfo();
								info.screen = i;
								addOrMoveDB( info );
							}
						}
					}
					trashIcon.onDrop( new ArrayList<View3D>( deletedCell.getActors() ) , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() - trashIcon.height );
					ThemeManager.getInstance().getThemeDB().SaveScreenCount( cellLayoutList.size() );
					SendMsgToAndroid.sendRemoveWorkspaceCellMsg( toDeleteIndex );
					pageIndicator.setPageNum( workspace.getPageNum() );
					return true;
				case DesktopEdit.MSG_DESKTOP_EDIT_PAGE_POSITION_CHANGE:
					int toChangeIndex = desktopEdit.toChangePageIndex;
					cellLayoutList = workspace.getViewList();
					View3D changeCell;
					int curPageIndex = desktopEdit.getCurrentPageIndex();
					if( DesktopEdit.curDesktopEditMode == DesktopEdit.DESKTOP_EDIT_MODE_CYLINDER )
					{
						if( toChangeIndex == desktopEdit.getPageNum() - 1 && curPageIndex == 0 )
						{
							changeCell = cellLayoutList.remove( toChangeIndex );
							View3D changeCell2 = cellLayoutList.remove( curPageIndex );
							cellLayoutList.add( curPageIndex , changeCell );
							cellLayoutList.add( changeCell2 );
							SendMsgToAndroid.sendExchangeWorkspaceCellMsg( curPageIndex , toChangeIndex );
						}
						else if( toChangeIndex == 0 && curPageIndex == desktopEdit.getPageNum() - 1 )
						{
							View3D changeCell2 = cellLayoutList.remove( curPageIndex );
							changeCell = cellLayoutList.remove( toChangeIndex );
							cellLayoutList.add( 0 , changeCell2 );
							cellLayoutList.add( changeCell );
							SendMsgToAndroid.sendExchangeWorkspaceCellMsg( toChangeIndex , curPageIndex );
						}
						else
						{
							changeCell = cellLayoutList.remove( toChangeIndex );
							cellLayoutList.add( curPageIndex , changeCell );
							SendMsgToAndroid.sendReorderWorkspaceCellMsg( toChangeIndex , curPageIndex );
						}
					}
					else if( DesktopEdit.curDesktopEditMode == DesktopEdit.DESKTOP_EDIT_MODE_PLANE )
					{
						changeCell = cellLayoutList.remove( toChangeIndex );
						cellLayoutList.add( curPageIndex , changeCell );
						SendMsgToAndroid.sendReorderWorkspaceCellMsg( toChangeIndex , curPageIndex );
					}
					for( int i = 0 ; i < cellLayoutList.size() ; i++ )
					{
						ViewGroup3D celllayout = (ViewGroup3D)cellLayoutList.get( i );
						for( int j = 0 ; j < celllayout.getChildCount() ; j++ )
						{
							View3D v = celllayout.getChildAt( j );
							if( v instanceof IconBase3D )
							{
								ItemInfo info = ( (IconBase3D)v ).getItemInfo();
								info.screen = i;
								addOrMoveDB( info );
							}
						}
					}
					return true;
			}
		}
		if( sender instanceof HotSeat3D )
		{
			HotSeat3D sidebar = (HotSeat3D)sender;
			switch( event_id )
			{
				case HotSeat3D.MSG_VIEW_START_MAIN:
					//				if (DefaultLayout.mainmenu_inout_no_anim) {
					//					showAllAppFromWorkspaceEx();
					//				} else {
					//					showAllAppFromWorkspace();
					//				}
					return true;
					// case HotSeat3D.MSG_DOCKGROUP_DISPLAY_MAINMENU:
					// if(workspace == null || workspace.isVisible())
					// hotButton.showNoAnim();
					// return true;
					// case HotSeat3D.MSG_DISPLAY_MAINMENU_VIEW:
					// if(workspace == null || workspace.isVisible())
					// {
					// if (trashIcon.isVisible()==false &&
					// (hotButton.isVisible()==false||hotButton.y<0))
					// {
					// hotButton.showNoAnim();
					// }
					// }
					// if(workspace != null && workspace.isVisible())
					// SendMsgToAndroid.sendShowWorkspaceMsg();
					// return true;
				case HotSeat3D.MSG_ON_DROP:
					View3D view = (View3D)sidebar.getTag();
					Vector2 vec2 = new Vector2();
					view.toAbsoluteCoords( vec2 );
					ArrayList<View3D> tmpList = new ArrayList<View3D>();
					tmpList.add( view );
					sidebar.setTag( null );
					workspace.onDrop( tmpList , vec2.x , vec2.y );
					return true;
					// case SideBar.MSG_LONGCLICK_INAPPLIST:
					// return showPageContainer(-1);
			}
		}
		if( sender instanceof FolderIcon3D )
		{
			final FolderIcon3D folderIcon3D = (FolderIcon3D)sender;
			switch( event_id )
			{
				case FolderIcon3D.MSG_FOLDERICON3D_CLICK:
					folder = folderIcon3D;
					return true;
				case FolderIcon3D.MSG_FOLDERICON_BACKTO_ORIG:
					ArrayList<View3D> child_list = (ArrayList<View3D>)folderIcon3D.getTag();
					folder_DropList_backtoOrig( child_list );
					return true;
				case FolderIcon3D.MSG_FOLDERICON_TO_ROOT3D:
					// zhujieping add
					if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder && !isDragon )
					{
						return dealMIUIFolderToRoot3D( folderIcon3D );
					}
					appHost.hide();
					hotseatBar.hide();
					hotseatBar.getDockGroup().releaseFocus();
					hotseatBar.getMainGroup().releaseFocus();
					// hotButton.hide();
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
					if( ( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder ) && !isDragon && !( DefaultLayout.mainmenu_folder_function == true && ( folderIcon3D
							.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D && folderIcon3D.getExitToWhere() == FolderIcon3D.TO_CELLLAYOUT ) ) )
					{
						dealMIUIFolderToCellLayout( folderIcon3D );
						return true;
					}
					// zhujieping add end
					Log.d( "test12345" , "MSG_FOLDERICON_TO_CELLLAYOUT" );
					// this.touchable=true;
					Log.d( "testdrag" , " FolderIcon3D.MSG_FOLDERICON_TO_CELLLAYOUT" );
					// teapotXu add start for Folder in Mainmenu
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						// 如果此时是从主菜单的文件夹中拖动图标到Idle上，
						if( folderIcon3D.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D && folderIcon3D.getExitToWhere() == FolderIcon3D.TO_CELLLAYOUT )
						{
							// 此处设置标志，使再次进入AppList的时候，重新刷新并排序，显示出文件夹
							appHost.hide();
							( (AppList3D)appHost.appList ).force_applist_refesh = true;
							// 同时把该Folder add入 dragLayer
							dragLayer.addDropTarget( (DropTarget3D)folderIcon3D );
						}
					}
					// teapotXu add end for Folder in Mainmenu
					workspace.setScale( 0f , 0f );
					workspace.show();
					boolean closeFolderByDrag = folderIcon3D.mFolder.getColseFolderByDragVal();
					hotseatBar.show();
					if( DefaultLayout.hotseat_style_ex )
					{
						// hotButton.show();
					}
					else
					{
						if( !closeFolderByDrag )
						{
							// hotButton.show();
						}
					}
					if( DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
					{
						trashIcon.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , trashIcon.x , 0 , 0 );
					}
					if( folderIcon3D.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT )
					{
						hotseatBar.setTag2( null );
						// teapotXu add start for Fodler in Mainmenu
					}
					else if( DefaultLayout.mainmenu_folder_function == true && folderIcon3D.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D )
					{
						// 如果此时是从主菜单的文件夹中拖动图标到Idle上，
						hotseatBar.setTag2( null );
						// teapotXu add end for Folder in Mainmenu
					}
					else
					{
						hotseatBar.setTag2( folderIcon3D );
					}
					if( closeFolderByDrag && dragLayer.draging )
					{
						workspace.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.3f , s3EffectScale , s3EffectScale , 0f ).setCallback( this );
					}
					else
					{
						Tween tmp = workspace.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.3f , 1f , 1f , 0f ).setCallback( this );// todo：这里有可能收不到回调！！！！！
						Log.d( "launcher" , "MSG_FOLDERICON_TO_CELLLAYOUT:" + tmp );
					}
					workspace.setTag( folderIcon3D );
					hotseatBar.touchable = false;
					workspace.touchable = false;
					if( folderIcon3D.getItemInfo().container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
						workspace.getCurrentCellLayout().cellFindViewAndRemove( folderIcon3D.getItemInfo().cellX , folderIcon3D.getItemInfo().cellY );
					/*
					 * 避免这里的动画有时候收不到回调，启动定时器�? 定时器到时长度一定要大于这里启动动画的时长，保证动画的回调函数能跑到一次，
					 * 避免回调跑不到，root3d.touchable为False，触摸无法反应，定屏问题的解�? added by zfshi
					 * 2012-08-19
					 */
					startRootTimer( (long)( workspaceTweenDuration * 4 * 1000 ) );
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
							// TODO Auto-generated method stub
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
							// TODO Auto-generated method stub
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
					launcher.postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							folder3D.onInputNameChanged();
						}
					} );
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
						}
						else if( viewList.get( i ) instanceof Widget )
						{
							SendMsgToAndroid.deleteSysWidget( (Widget)viewList.get( i ) );
							Widget widget = (Widget)viewList.get( i );
							widget.dispose();
						}
						// xiatian add start //Widget adaptation
						// "com.android.gallery3d"
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
							// LauncherModel.deleteUserFolderContentsFromDatabase(launcher,
							// item);
							// Root3D.deleteFromDB(info)
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
					return true;
			}
		}
		switch( event_id )
		{
			case MSG_SET_WALLPAPER_OFFSET:
				// Log.e("launcher", "MSG_SET_WALLPAPER_OFFSET");
				final IBinder token = launcher.getWindow().getCurrentFocus().getWindowToken();
				final NPageBase page = (NPageBase)sender;
				if( token != null )
				{
					launcher.postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							if( !DefaultLayout.disable_move_wallpaper )
							{
								mWallpaperManager.setWallpaperOffsetSteps( 1.0f / ( page.getPageNum() - 1 ) , 0 );
								float offset = page.getTotalOffset();
								// Log.e("test", "offset:" +
								// offset+" index:"+page.getCurrentPage());
								if( bExitDesktopEditMode )
								{
									offset = desktopEdit.getUser();
									Log.i( "" , "###### MSG_SET_WALLPAPER_OFFSET : offset = " + offset );
								}
								mWallpaperManager.setWallpaperOffsets( token , offset , 0 );
							}
						}
					} );
				}
				// mWallpaperManager.setWallpaperOffsets(windowToken, xOffset,
				// yOffset)
				return true;
		}
		return false;
	}
	
	public void showWorkSpaceFromAllApp()
	{
		if( workspaceAndAppTween != null && workspaceAndAppTween.isStarted() )
		{
			return;
		}
		//	setWidgetLiveState(true);
		//		if (DefConfig.DEF_NEW_SIDEBAR == true) {
		//			hotseatBar.showDelay(workspaceTweenDuration);
		//		}
		// workspace.setColor(new Color(workspace.color.r, workspace.color.g,
		// workspace.color.b, 0));
		workspaceAndAppTween = Timeline.createParallel();
		//		workspace.color.a = 0;
		workspaceAndAppTween.push( workspace.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , /*workspaceTweenDuration*/0.5f , 1 , 0 , 0 ).delay( workspaceTweenDuration ) );
		workspaceAndAppTween.push( hotseatBar.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , /*workspaceTweenDuration*/0.5f , 1 , 0 , 0 ) );
		//		workspace.setUser(Utils3D.getScreenHeight() / 5);
		//		workspaceAndAppTween.push(workspace.obtainTween(
		//				View3DTweenAccessor.USER, Cubic.OUT, workspaceTweenDuration, 0,
		//				0, 0).delay(workspaceTweenDuration));
		// workspace.setRotationVector(1, 0, 0);
		// workspace.setRotation(-30);
		//		workspace.setScale(0.9f, 0.9f);
		//		workspaceAndAppTween.push(workspace.obtainTween(
		//				View3DTweenAccessor.SCALE_XY, Cubic.OUT,
		//				workspaceTweenDuration, 1, 1, 0).delay(workspaceTweenDuration));
		//		workspace.show();
		//		NPageBase contentList = appHost.getContentList();
		//		contentList.setUser(0);
		//		Color c = contentList.indicatorView.getColor();
		//		contentList.indicatorView.setColor(c.r, c.g, c.b, 0);
		//		workspaceAndAppTween.push(contentList.obtainTween(
		//				View3DTweenAccessor.USER, Cubic.IN, workspaceTweenDuration,
		//				-contentList.height / 5, 0, 0));
		//		workspaceAndAppTween.push(contentList.obtainTween(
		//				View3DTweenAccessor.OPACITY, Cubic.IN, workspaceTweenDuration,
		//				0, 0, 0));
		// appHost.appList.setRotationVector(1, 0, 0);
		// appHost.appList.setRotation(0);
		//		workspaceAndAppTween.push(contentList.obtainTween(
		//				View3DTweenAccessor.SCALE_XY, Cubic.IN, workspaceTweenDuration,
		//				0.1f, 0.1f, 0));
		if( appHost.appBar != null )
		{
			//			appHost.appBar.color.a = 1;
			//			workspaceAndAppTween.push(appHost.appBar.obtainTween(
			//					View3DTweenAccessor.OPACITY, Cubic.IN,
			//					workspaceTweenDuration, 0, 0, 0));
			appHost.forbidTouch = true;
			workspaceAndAppTween.push( appHost.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , /*workspaceTweenDuration*/0.5f , 0 , 0 , 0 ) );
			workspaceAndAppTween.push( appHost.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , /*workspaceTweenDuration*/0.5f , 0 , 0 , 0 ) );
			workspaceAndAppTween.push( hostBg.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , /*workspaceTweenDuration*/0.5f , 0 , 0 , 0 ) );
		}
		workspaceAndAppTween.setCallback( this ).start( View3DTweenAccessor.manager ).setUserData( 0 );
		Log.d( "launcher" , "showWorkSpaceFromAllApp" );
		// appHost.hide();
		if( DefaultLayout.broadcast_state )
		{
			iLoongLauncher.getInstance().sendBroadcast( new Intent( "com.cooee.launcher.action.show_workspace" ) );
		}
	}
	
	public void showWorkSpaceFromAllAppEx()
	{
		// if(workspaceAndAppTween != null && workspaceAndAppTween.isStarted()){
		// return;
		// }
		// if (DefConfig.DEF_NEW_SIDEBAR==true)
		// {
		// hotseatBar.showEx();
		// //hotButton.showEx();
		// if (DefaultLayout.trash_icon_pos!=TrashIcon3D.TRASH_POS_TOP)
		// {
		// trashIcon.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT,
		// 0.0f, trashIcon.x, 0, 0);
		// }
		// }
		// //workspace.setColor(new Color(workspace.color.r, workspace.color.g,
		// workspace.color.b, 0));
		// workspaceAndAppTween = Timeline.createParallel();
		// workspace.color.a = 0;
		// workspaceAndAppTween.push(workspace.obtainTween(View3DTweenAccessor.OPACITY,
		// Cubic.OUT, 0, 1, 0, 0).delay(0));
		// workspace.setUser(Utils3D.getScreenHeight()/5);
		// workspaceAndAppTween.push(workspace.obtainTween(View3DTweenAccessor.USER,
		// Cubic.OUT, 0, 0, 0, 0).delay(0));
		// // workspace.setRotationVector(1, 0, 0);
		// // workspace.setRotation(-30);
		// workspace.setScale(0.9f, 0.9f);
		// workspaceAndAppTween.push(workspace.obtainTween(View3DTweenAccessor.SCALE_XY,
		// Cubic.OUT, 0, 1, 1, 0).delay(0));
		// workspace.show();
		//
		// appHost.appList.setUser(DefaultLayout.applist_style_classic?R3D.hot_obj_height:0);
		// Color c = appHost.appList.indicatorView.getColor();
		// appHost.appList.indicatorView.setColor(c.r, c.g, c.b, 0);
		// workspaceAndAppTween.push(appHost.appList.obtainTween(View3DTweenAccessor.USER,
		// Cubic.IN, 0,
		// -appHost.appList.height/5+(DefaultLayout.applist_style_classic?R3D.hot_obj_height:0),
		// 0, 0));
		// workspaceAndAppTween.push(appHost.appList.obtainTween(View3DTweenAccessor.OPACITY,
		// Cubic.IN, 0, 0, 0, 0));
		// // appHost.appList.setRotationVector(1, 0, 0);
		// // appHost.appList.setRotation(0);
		// workspaceAndAppTween.push(appHost.appList.obtainTween(View3DTweenAccessor.SCALE_XY,
		// Cubic.IN, 0, 0.9f, 0.9f, 0));
		// if(appHost.appBar != null){
		// appHost.appBar.color.a = 1;
		// workspaceAndAppTween.push(appHost.appBar.obtainTween(View3DTweenAccessor.OPACITY,
		// Cubic.IN, 0, 0, 0, 0));
		// }
		// workspaceAndAppTween.setCallback(this).start(View3DTweenAccessor.manager).setUserData(0);
		// hotButton.changeState(HotMainMenuView3D.STATE_HOME);
		// //appHost.hide();
		// if(DefaultLayout.broadcast_state){
		// iLoongLauncher.getInstance().sendBroadcast(new
		// Intent("com.cooee.launcher.action.show_workspace"));
		// }
	}
	
	public FolderIcon3D getOpenFolder()
	{
		int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			View3D view = getChildAt( i );
			if( view instanceof FolderIcon3D )
			{
				return (FolderIcon3D)view;
			}
		}
		return null;
	}
	
	public void setWidgetLiveState(
			int live )
	{
		int curIndex = workspace.getCurrentScreen();
		if( curIndex >= workspace.getChildCount() )
		{
			return;
		}
		View3D child = workspace.getChildAt( workspace.getCurrentScreen() );
		CellLayout3D layout = null;
		if( child instanceof CellLayout3D )
		{
			layout = (CellLayout3D)child;
			for( int i = 0 ; i < layout.getChildCount() ; i++ )
			{
				if( layout.getChildAt( i ) instanceof Widget3D )
				{
					Widget3D widget = (Widget3D)layout.getChildAt( i );
					for( int j = 0 ; j < widget.getChildCount() ; j++ )
					{
						if( widget.getChildAt( j ) instanceof WidgetPluginView3D )
						{
							WidgetPluginView3D plugin = (WidgetPluginView3D)widget.getChildAt( j );
							android.util.Log.v( "Clock" , "find me !,my name is: " + plugin.name );
							plugin.setObjectState( live );
						}
					}
				}
			}
		}
		int preIndex = workspace.preIndex();
		if( preIndex >= workspace.getChildCount() )
		{
			return;
		}
		child = workspace.view_list.get( preIndex );
		if( child instanceof CellLayout3D )
		{
			layout = (CellLayout3D)child;
			for( int i = 0 ; i < layout.getChildCount() ; i++ )
			{
				if( layout.getChildAt( i ) instanceof Widget3D )
				{
					Widget3D widget = (Widget3D)layout.getChildAt( i );
					for( int j = 0 ; j < widget.getChildCount() ; j++ )
					{
						if( widget.getChildAt( j ) instanceof WidgetPluginView3D )
						{
							WidgetPluginView3D plugin = (WidgetPluginView3D)widget.getChildAt( j );
							android.util.Log.v( "Clock" , "find me !,my name is: " + plugin.name );
							plugin.setObjectState( live );
						}
					}
				}
			}
		}
		int nextIndex = workspace.nextIndex();
		if( nextIndex >= workspace.getChildCount() )
		{
			return;
		}
		child = workspace.view_list.get( nextIndex );
		if( child instanceof CellLayout3D )
		{
			layout = (CellLayout3D)child;
			for( int i = 0 ; i < layout.getChildCount() ; i++ )
			{
				if( layout.getChildAt( i ) instanceof Widget3D )
				{
					Widget3D widget = (Widget3D)layout.getChildAt( i );
					for( int j = 0 ; j < widget.getChildCount() ; j++ )
					{
						if( widget.getChildAt( j ) instanceof WidgetPluginView3D )
						{
							WidgetPluginView3D plugin = (WidgetPluginView3D)widget.getChildAt( j );
							android.util.Log.v( "Clock" , "find me !,my name is: " + plugin.name );
							plugin.setObjectState( live );
						}
					}
				}
			}
		}
	}
	
	public void showAllAppFromWorkspace(
			float x ,
			float y )
	{
		//|| 
		if( ( workspaceAndAppTween != null && workspaceAndAppTween.isStarted() ) )
		{
			return;
		}
		setWidgetLiveState( 1 );
		boolean hideHotbar = DefaultLayout.applist_style_classic ? false : true;
		addViewBefore( dragLayer , hostBg );
		addViewBefore( dragLayer , appHost );
		appHost.show();
		mainMenuEntry = false;
		hostBg.show();
		pageIndicator.hide();
		appHost.requestFocus();
		//		if (DefConfig.DEF_NEW_SIDEBAR == true) {
		//			if (hideHotbar) {
		//				hotseatBar.hide();
		//				// hotButton.hide();
		//			}
		//			if (DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP) {
		//				trashIcon.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT,
		//						0.3f, trashIcon.x, -trashIcon.height * 1.5f, 0);
		//			}
		//		}
		workspaceAndAppTween = Timeline.createParallel();
		//		appHost.appList
		//				.setUser(-appHost.appList.height
		//						/ 5
		//						+ (DefaultLayout.applist_style_classic ? R3D.hot_obj_height
		//								: 0));
		//		workspaceAndAppTween.push(appHost.appList.obtainTween(
		//				View3DTweenAccessor.USER, Cubic.OUT, workspaceTweenDuration,
		//				(DefaultLayout.applist_style_classic ? R3D.hot_obj_height : 0),
		//				0, 0).delay(workspaceTweenDuration));
		//		appHost.appList.color.a = 0;
		//		workspaceAndAppTween.push(appHost.appList.obtainTween(
		//				View3DTweenAccessor.OPACITY, Cubic.OUT, workspaceTweenDuration,
		//				1, 0, 0).delay(workspaceTweenDuration));
		// appHost.appList.setRotationVector(1, 0, 0);
		// appHost.appList.setRotation(-30);
		//		appHost.appList.setPosition(Utils3D.getScreenWidth()/2, Utils3D.getScreenHeight()/2);
		//		appHost.appList.setScale(0.2f, 0.2f);
		//		appHost.appList.setOrigin(Utils3D.getScreenWidth()/2, 0);
		//		workspaceAndAppTween.push(appHost.appList.obtainTween(
		//				View3DTweenAccessor.SCALE_XY, Cubic.OUT,
		//				workspaceTweenDuration, 1f, 1f, 0));//.delay(workspaceTweenDuration));
		if( appHost.appBar != null )
		{
			//			appHost.appBar.color.a = 0;
			//			workspaceAndAppTween.push(appHost.appBar.obtainTween(
			//					View3DTweenAccessor.OPACITY, Cubic.OUT,
			//					workspaceTweenDuration, 1, 0, 0));//.delay(
			//					workspaceTweenDuration));
			Log.v( "scale_origin" , "scaleX=" + appHost.getScaleX() + "scaleY=" + appHost.getScaleY() + "originX" + appHost.originX + "originY" + appHost.originY );
			appHost.forbidTouch = true;
			//			appHost.requestFocus();
			appHost.setScale( 0 , 0 );
			//			appHost.setPosition((x-appHost.width*0.1f)/2, y);
			appHost.setOrigin( x , y );
			appHost.color.a = 0;
			appHost.transform = true;
			workspaceAndAppTween.push( appHost.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT ,
			/*workspaceTweenDuration*/0.5f , 1f , 1f , 0 ) );
			workspaceAndAppTween.push( appHost.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , /*workspaceTweenDuration*/0.5f , 1f , 0 , 0 ) );
			hostBg.color.a = 0;
			workspaceAndAppTween.push( hostBg.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , /*workspaceTweenDuration*/0.5f , 0.7f , 0 , 0 ) );
		}
		//		workspace.setUser(0);
		//		workspaceAndAppTween.push(workspace.obtainTween(
		//				View3DTweenAccessor.USER, Cubic.IN, workspaceTweenDuration,
		//				Utils3D.getScreenHeight() / 5, 0, 0));
		workspace.color.a = 1;
		workspaceAndAppTween.push( workspace.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , /*workspaceTweenDuration*/0.5f , 0.3f , 0 , 0 ) );
		hotseatBar.color.a = 1;
		workspaceAndAppTween.push( hotseatBar.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , /*workspaceTweenDuration*/0.5f , 0.3f , 0 , 0 ) );
		//		// workspace.setRotationVector(1, 0, 0);
		//		workspaceAndAppTween.push(workspace.obtainTween(
		//				View3DTweenAccessor.SCALE_XY, Cubic.IN, workspaceTweenDuration,
		//				0.9f, 0.9f, 0));
		workspaceAndAppTween.setCallback( this ).start( View3DTweenAccessor.manager ).setUserData( 1 );
		// hotButton.changeState(HotMainMenuView3D.STATE_APP);
		Messenger.sendMsg( Messenger.MSG_START_COVER_MTKWIDGET , 0 , 0 );
		SendMsgToAndroid.sendHideWorkspaceMsg();
		ClingManager.getInstance().cancelAllAppCling();
		if( DefaultLayout.broadcast_state )
		{
			iLoongLauncher.getInstance().sendBroadcast( new Intent( "com.cooee.launcher.action.show_app" ) );
		}
		//		SendMsgToAndroid.sysPlaySoundEffect();
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
	
	private void dealworkspaceTweenFinish()
	{
		Log.v( "Root3D" , "dealworkspaceTweenFinish" );
		if( workspace.touchable )
			return;
		Object view = workspace.getTag();
		stopRootTimer();
		if( view == null )
		{
			return;
		}
		else if( view instanceof FolderIcon3D )
		{
			FolderIcon3D folderIcon3D = (com.iLoong.launcher.Folder3D.FolderIcon3D)view;
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
				trashIcon.show();
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
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// TODO Auto-generated method stub
		if( source == desktopEdit.getTween() && type == TweenCallback.COMPLETE )
		{
			bExitDesktopEditMode = false;
		}
		if( source == timeLine && type == TweenCallback.COMPLETE )
		{
			Log.v( "Root3D" , "TIME LIEN COMPLETE" );
			//		    
			if( source.getUserData() != null )
			{
				int animKind = (Integer)( source.getUserData() );
				if( animKind == SPLIT_SCREEN_CLOSE_VIEW )
				{
					if( timeLine != null )
					{
						timeLine.free();
					}
					timeLine = Timeline.createParallel();
					timeLine.push( Tween.to( lineView[0] , View3DTweenAccessor.OPACITY , 0.1f ).target( 0 , 0 ).ease( Cubic.INOUT ) );
					timeLine.push( Tween.to( lineView[1] , View3DTweenAccessor.OPACITY , 0.1f ).target( 0 , 0 ).ease( Cubic.INOUT ) );
					timeLine.push( Tween.to( fp.getFrameSurface0() , View3DTweenAccessor.OPACITY , 0.1f ).target( 0 , 0 ).ease( Cubic.INOUT ) );
					timeLine.push( Tween.to( fp.getFrameSurface1() , View3DTweenAccessor.OPACITY , 0.1f ).target( 0 , 0 ).ease( Cubic.INOUT ) );
					timeLine.start( View3DTweenAccessor.manager ).setCallback( this ).setUserData( SPLIT_SCREEN_HIDE_LINE_VIEW );
					//  userAction= SPLIT_SCREEN_HIDE_LINE_VIEW;
				}
				else if( animKind == SPLIT_SCREEN_HIDE_LINE_VIEW )
				{
					if( userAction != SPLIT_SCREEN_ONLY_CLOSE_UP_VIEW )
					{
						uninitialize();
					}
				}
				else if( animKind == SPLIT_SCREEN_OPEN_VIEW )
				{
					fp.onEffectionComplete();
				}
			}
		}
		if( source == workspaceAndAppTween && type == TweenCallback.COMPLETE && workspaceAndAppTween.getUserData().equals( 0 ) )
		{
			Log.d( "launcher" , "event:hide app onevent" );
			appHost.releaseFocus();
			workspaceAndAppTween = null;
			appHost.setScale( 1f , 1f );
			appHost.setOrigin( Utils3D.getScreenWidth() / 2 , Utils3D.getScreenHeight() / 2 );
			appHost.transform = false;
			appHost.forbidTouch = false;
			appHost.hide();
			hostBg.hide();
			//			appHost.getContentList().setUser(0);
			//			appHost.getContentList().setRotation(0);
			//			appHost.getContentList().color.a = 1;
			//			appHost.getContentList().setScale(1, 1);
			//			if (appHost.appBar != null)
			//				appHost.appBar.color.a = 1;
			appHost.getContentList().stopTween();
			SendMsgToAndroid.sendShowWorkspaceMsg();
			setWidgetLiveState( 0 );
		}
		else if( source == workspaceAndAppTween && type == TweenCallback.COMPLETE && workspaceAndAppTween.getUserData().equals( 1 ) )
		{
			Log.d( "launcher" , "event:hide workspace" );
			workspaceAndAppTween = null;
			appHost.forbidTouch = false;
			//			workspace.hide();
			//			workspace.setUser(0);
			//			workspace.setRotation(0);
			//			workspace.color.a = 1;
			//			workspace.setScale(1, 1);
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
		else if( source == workspace.getTween() && type == TweenCallback.COMPLETE )
		{
			dealworkspaceTweenFinish();
		}
		else if( source instanceof Timeline && type == TweenCallback.COMPLETE && trashIcon.getTag() != null )
		{
			trashIcon.onDrop( (ArrayList<View3D>)trashIcon.getTag() , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() - trashIcon.height );
			trashIcon.hide();
			trashIcon.setTag( null );
			SendMsgToAndroid.sendShowWorkspaceMsg();
		}
		else if( shadowView != null && shadowView.color.a == 0 )
			this.removeView( shadowView );
		// wanghongjian add start //enable_DefaultScene
		else if( source == statrFreeTween && type == TweenCallback.COMPLETE )
		{
			statrFreeTween = null;
			// Log.v("", "CooeeScene 测试  hotball下滑动画结束  y is " + hotseatBar.y);
		}
		// wanghongjian add end
	}
	
	public static boolean allowWidgetRefresh()
	{
		// return !PageIndicator3D.animating && !TrashIcon3D.animating &&
		// !HotButton.animating;
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
				workspace.addInCurrenScreen( item , (int)item.x , (int)item.y );
				item.startTween( View3DTweenAccessor.POS_XY , Elastic.OUT , 0.3f , ( (FolderIcon3D)item ).mInfo.x , ( (FolderIcon3D)item ).mInfo.y , 0 );
			}
			else if( ( (FolderIcon3D)item ).mInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
			{
				// hotseatBar.calcCoordinate(item);
				dropListBacktoOrig( folderList );
			}
		}
		dragLayer.removeAllViews();
		dragLayer.hide();
		trashIcon.hide();
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
			// pageIndicator.normal();
		}
		trashIcon.hide();
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		// zhujieping add
		if( enable_show_fps )
		{
			bitmapFont.draw( batch , "fps:" + Gdx.graphics.getFramesPerSecond() , 5 , Gdx.graphics.getHeight() );
		}
		if( bExitDesktopEditMode )
		{
			onCtrlEvent( workspace , MSG_SET_WALLPAPER_OFFSET );
		}
		this.parentAlpha = parentAlpha;
		if( ( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder ) && DefaultLayout.blur_enable && folder != null )
		{
			MiuiV5FolderBoxBlur( batch , parentAlpha );
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
		//		if(gdxSreenRegion!=null &&gdxSreenRegion.getTexture()!=null)
		//		  batch.draw(gdxSreenRegion,0, 0, this.width, this.height);
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_SEARCH )
			return true;
		return super.keyDown( keycode );
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_SEARCH )
		{
			launcherGlobalSearch();
			return true;
		}
		return super.keyUp( keycode );
	}
	
	public void onHomeKey(
			boolean alreadyOnHome )
	{
		// if(sidebar != null)sidebar.hide();
		// Log.d("testdrag", " Root3D onHomeKey hotseatBar.y="+hotseatBar.y +
		// "hotButton.y"+hotButton.y);
		if( workspace != null && workspace.isVisible() && ( hotseatBar.y < 0 ) )
		{
			Log.d( "testdrag" , " Root3D onHomeKey return" );
			return;
		}
		if( appHost != null && appHost.isVisible() )
		{
			appHost.releaseFocus();
			workspace.color.a = 1;
			hotseatBar.color.a = 1;
			hostBg.hide();
			// teapotXu add start for Fodler in Mainmenu
			// when folder in Mainmenu is open
			if( appHost.folderOpened == true && appHost.getFolderIconOpendInAppHost() != null )
			{
				FolderIcon3D appHost_folder = appHost.getFolderIconOpendInAppHost();
				if( appHost.closeFolder2goHome )
					return;
				if( appHost_folder.onHomeKey( alreadyOnHome ) )
					appHost.closeFolder2goHome = true;
			}
			// teapotXu add end for Fodler in Mainmenu
			// if(alreadyOnHome){
			// showWorkSpaceFromAllApp();
			// } else {
			appHost.hide();
			workspace.show();
			hotseatBar.showNoAnim();
			workspace.setCurrentScreen( workspace.getHomePage() );
			// }
		}
		else if( desktopEdit != null && desktopEdit.isVisible() )
		{
			if( desktopEdit.isAnimation() )
			{
				desktopEdit.stopAllAnimation();
			}
			desktopEdit.prepareHide();
			hotseatBar.dockbarTurnUp();
			exitDesktopEditMode();
			if( alreadyOnHome )
			{
				workspace.scrollTo( workspace.getHomePage() );
			}
			else
			{
				workspace.setCurrentScreen( workspace.getHomePage() );
			}
		}
		else if( launcher.folderIcon != null && launcher.folderIcon.mInfo.contents.size() == 0 && launcher.folderIcon.bRenameFolder )
		{
			launcher.renameFoldercleanup();
			workspace.scrollTo( workspace.getHomePage() );
		}
		else if( bLongClickOnWorkspace )
		{
			removeAddEditMode();
			removeAddFolderAppEditMode();
			removeAddFolderEditMode();
			removeFolderRename();
			removeShortcutEditMode();
			removeWidgetEditMode();
			finishLayout( Root3D.SPLIT_SCREEN_CLOSE_VIEW );
			launcher.renameFoldercleanup();
			exitFolder();
			bLongClickOnWorkspace = false;
			if( alreadyOnHome )
			{
				workspace.scrollTo( workspace.getHomePage() );
			}
			else
			{
				workspace.setCurrentScreen( workspace.getHomePage() );
			}
		}
		else if( folder != null )
		{
			if( workspace.isManualScrollTo || goHome )
				return;
			if( folder.bAnimate )
				return;
			else if( folder.onHomeKey( alreadyOnHome ) )
				goHome = true;
			else
				workspace.scrollTo( workspace.getHomePage() );
			launcher.renameFoldercleanup();
			exitFolder();
			folder = null;
			// if(!alreadyOnHome)workspace.setCurrentScreen(workspace.getHomePage());
		}
		else if( workspace != null )
		{
			launcher.DismissShortcutDialog();
			if( workspace.isManualScrollTo || goHome )
				return;
			workspace.show();
			if( alreadyOnHome )
				workspace.scrollTo( workspace.getHomePage() );
			else
			{
				workspace.setCurrentScreen( workspace.getHomePage() );
			}
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
				// batch.draw(R3D.findRegion("translucent-bg"),0,0);
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
			// widgetPluginView.setVisible(false);
			// if(shadowView == null){
			// shadowView = new ShadowView("shadow root");
			// }
			// this.addView(shadowView);
			this.addView( widgetPluginView );
			// launcher.postRunnable(new Runnable() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// shadowView.startTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
			// 0.8f, 0.7f, 0, 0);
			// }
			// });
			ClingManager.getInstance().startWait();
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
						// TODO Auto-generated method stub
						widgetPluginView.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.4f , focusWidgetPos[0] , focusWidgetPos[1] , 0 );
					}
				} );
			}
			// focusWidget.setVisible(true);
			parent.addView( focusWidget );
			if( shadowView != null )
			{
				launcher.postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						shadowView.startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.8f , 0 , 0 , 0 ).setCallback( shadowView.getParent() );
					}
				} );
			}
		}
		ClingManager.getInstance().cancelWait();
	}
	
	// workspace.focusWidget(widgetPluginView,state);
	public void setEffectType(
			int select )
	{
		if( appHost != null )
			appHost.appList.setEffectType( select );
	}
	
	// wanghongjian add start //enable_DefaultScene
	private void setSceneReciverIntent()
	{
		sceneReciveIntent = new Intent();
		sceneReciveIntent.setAction( "com.cooee.scene.receive" );
		addAppIntent = new Intent( "com.cooee.scene.addapp" );
		removeAppIntent = new Intent( "com.cooee.scene.removeapp" );
		deleteSceneIntent = new Intent( "com.cooee.scene.delete" );
		upSceneIntent = new Intent( "com.cooee.scene.change" );
	}
	
	public boolean FreeMainVisible()
	{
		return isSceneDown;
	}
	
	@SuppressLint( "NewApi" )
	private View3D getFreeView()
	{
		View3D view = null;
		DexClassLoader loader = null;
		Intent intent = new Intent( "com.cooee.scene" , null );
		intent.setPackage( scenePkg );
		PackageManager pm = iLoongApplication.ctx.getPackageManager();
		List<ResolveInfo> resolveInfoList = pm.queryIntentActivities( intent , PackageManager.GET_META_DATA );
		if( resolveInfoList != null )
		{
			if( resolveInfoList.size() > 0 )
			{
				ResolveInfo info = resolveInfoList.get( 0 );
				loader = getClassLoader( info );
				Class<?> clazz;
				try
				{
					Context sceneContext = iLoongApplication.ctx.createPackageContext( info.activityInfo.applicationInfo.packageName , Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY );
					clazz = loader.loadClass( sceneCls );
					Constructor<?> c = clazz.getConstructor( String.class , Context.class , Context.class , Root3D.class );
					view = (View3D)c.newInstance( "name" , iLoongLauncher.getInstance() , sceneContext , this );
					if( view != null )
					{
						Log.v( "Ascene" , "sceneContext.getPackageName() is " + sceneContext.getPackageName() );
					}
				}
				catch( Exception e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return view;
	}
	
	@SuppressLint( "NewApi" )
	private DexClassLoader getClassLoader(
			ResolveInfo resolveInfo )
	{
		// Log.e("Widget3DManager", "come in  getClassLoader");
		ActivityInfo ainfo = resolveInfo.activityInfo;
		String dexPath = ainfo.applicationInfo.sourceDir;
		// String dexOutputDir = ainfo.applicationInfo.dataDir;
		// 插件输出目录，目前为launcher的子目录
		String dexOutputDir = iLoongLauncher.getInstance().getApplicationInfo().dataDir;
		dexOutputDir = dexOutputDir + File.separator + "widget" + File.separator + ainfo.packageName.substring( ainfo.packageName.lastIndexOf( "." ) + 1 );
		creatDataDir( dexOutputDir );
		// String libPath = ainfo.applicationInfo.nativeLibraryDir;
		Integer sdkVersion = Integer.valueOf( android.os.Build.VERSION.SDK );
		String libPath = null;
		if( sdkVersion > 8 )
		{
			libPath = ainfo.applicationInfo.nativeLibraryDir;
		}
		DexClassLoader loader = new DexClassLoader( dexPath , dexOutputDir , libPath , iLoongApplication.ctx.getClassLoader() );
		// Log.e("Widget3DManager", "come out  getClassLoader");
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
	
	public void startScene()
	{
		if( FeatureConfig.enable_DefaultScene == false )
		{
			return;
		}
		// Log.v("", "pkg is " + scenePkg + " cls is " + sceneCls);
		// if (DefaultLayout.editmode_permit_toScene)
		// {
		// if (Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode)
		// {
		//
		// stopMIUIEditEffect();
		// }
		// }
		if( FeatureConfig.enable_DefaultScene )
		{
			if( freeMainCreated == false )
			{
				freeMainCreated = true;
				if( freemain == null )
				{
					freemain = getFreeView();
					if( freemain != null )
						freemain.y = Utils3D.getScreenHeight();
				}
			}
			if( freemain != null )
			{
				if( statrFreeTween == null )
				{
					float downTime = 0.4f;
					// DefaultLayout.scene_shortcut_style = false;
					for( int i = 0 ; i < workspace.getChildCount() ; i++ )
					{
						View3D view = workspace.getChildAt( i );
						view.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , downTime , 0f , -Utils3D.getScreenHeight() * 1.5f , 0f );
					}
					SendMsgToAndroid.sendHideWorkspaceMsg();
					// pageIndicatorOldY=pageIndicator.y;
					// hotbuttonx = hotButton.x;
					// Log.v("", "before hotbuttonx is " + hotbuttonx +
					// " pageIndicatorOldY is " + pageIndicatorOldY);
					// hotButton.startTween(View3DTweenAccessor.POS_XY,
					// Linear.INOUT, downTime, hotbuttonx,
					// -Utils3D.getScreenHeight()*1.5f, 0f);
					pageIndicator.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , downTime , 0f , -Utils3D.getScreenHeight() * 1.5f , 0f );
					statrFreeTween = hotseatBar.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , downTime , 0f , -Utils3D.getScreenHeight() * 1.5f , 0f ).setCallback( this );
					// Log.v("Cooee", "CooeeScene root�?  下滑动画开�?开始加载add场景桌面");
					DefaultLayout.cooee_scene_style = true;
					sendFreeReceive( sceneReciveIntent );
					isSceneDown = true;// 场景桌面开�?
					if( ( DefaultLayout.enable_particle ) && ( ParticleManager.particleManagerEnable ) )
						ParticleManager.particleManagerEnable = false;
					saveSceneIndex( 0 , scenePkg , sceneCls );
					if( SceneManager.getInstance() != null )
					{
						SceneManager.getInstance().setmUseByPkg( scenePkg );
					}
					this.addView( freemain );// 开始添加MIUI书房桌面
					// Log.v("Cooee", "CooeeScene freemain is add");
				}
			}
		}
	}
	
	private void sendFreeReceive(
			final Intent intent )
	{
		if( sceneHandler != null )
		{
			sceneHandler.post( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					if( intent != null )
						iLoongLauncher.getInstance().sendBroadcast( intent );
				}
			} );
		}
	}
	
	@Override
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		// Log.e("root3d", "multiTouch2 1");
		if( Desktop3DListener.initDone() == false )
		{
			System.out.println( "initDone" );
			return true;
		}
		if( workspace.getX() != 0 )
		{
			return true;
		}
		// Log.e("root3d", "multiTouch2 2");
		if( FeatureConfig.enable_DefaultScene == false )
		{
			return super.multiTouch2( initialFirstPointer , initialSecondPointer , firstPointer , secondPointer );
		}
		if( FeatureConfig.enable_DefaultScene )
		{
			if( freeMainCreated && ( isSceneDown ) )
			{
				// System.out.println("root freeMainCreated");
				return true;
			}
			if( super.multiTouch2( initialFirstPointer , initialSecondPointer , firstPointer , secondPointer ) )
			{
				// Log.v("Cooee", "CooeeScene doubleClick");
				return true;
			}
			if( initialFirstPointer.y > firstPointer.y && initialSecondPointer.y > secondPointer.y )
			{
				if( freeMainCreated == false && !isSceneDown )
				{
					if( freeMainCreated == false && isSceneTheme )
					{
						freeMainCreated = true;
						if( freemain == null )
						{
							freemain = getFreeView();
							if( freemain != null )
								freemain.y = Utils3D.getScreenHeight();
						}
					}
				}
				if( freemain != null )
				{
					// if ( freemain != null)
					// {
					// if ( freemain.y == Utils3D.getScreenHeight())
					// {
					// startScene();
					// }
					// }
					if( !( super.multiTouch2( initialFirstPointer , initialSecondPointer , firstPointer , secondPointer ) ) )
					{
						if( freemain != null )
						{
							if( freemain.y == Utils3D.getScreenHeight() )
							{
								startScene();
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		// Log.v("", "scroll isSceneDown is " + isSceneDown +
		// "getBeginStartAnim() is " +
		// ((MiuiFreeMain)freemain).getBeginStartAnim() );
		if( freemain != null )
		{
			if( isSceneDown )
				return true;// 确保双手指下拉自由桌面的时候不会响应其他的up事件
		}
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		// TODO Auto-generated method stub
		// Log.v("", "scroll isSceneDown is " + isSceneDown +
		// "getBeginStartAnim() is " +
		// ((MiuiFreeMain)freemain).getBeginStartAnim() );
		// Log.v("workspace", " root xScale is " + workspace.getScaleX());
		if( freemain != null )
		{
			if( isSceneDown )
				return true;// 确保双手指下拉自由桌面的时候不会响应其他的up事
		}
		return super.scroll( x , y , deltaX , deltaY );
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
	
	public void addApps(
			ArrayList<ApplicationInfo> apps )
	{
		if( FeatureConfig.enable_DefaultScene )
		{
			if( apps.size() > 0 )
			{
				String appPkg = apps.get( 0 ).packageName;
				Log.v( "addapp" , "appadd  is pkg is  " + appPkg );
				addAppIntent.putExtra( "sceneAddApp" , appPkg );
				sendFreeReceive( addAppIntent );
			}
		}
	}
	
	public void addWidget()
	{
		AddWidget addwidget = (AddWidget)this.findView( "add widget" );
		if( addwidget != null )
		{
			addwidget.startapp();
		}
	}
	
	public void removeDBIntents(
			ApplicationInfo appInfo )
	{
		if( FeatureConfig.enable_DefaultScene )
		{
			String appPkg = appInfo.packageName;
			Log.v( "appremove" , "appremove  is pkg is  " + appPkg );
			removeAppIntent.putExtra( "sceneappremove" , appPkg );
			sendFreeReceive( removeAppIntent );
		}
	}
	
	public void setSceneTheme(
			String pkg ,
			String cls )
	{
		// Log.v("", "isSceneTheme is " + isSceneTheme + " scenepkg is " +
		// scenePkg + " pkg id " + pkg);
		if( scenePkg.equals( pkg ) )
		{
			return;
		}
		else
		{
			sendFreeReceive( upSceneIntent );
			scenePkg = pkg;
			sceneCls = cls;
			// Log.v("", "isSceneTheme is " + isSceneTheme + " freemain is " +
			// freemain);
			if( isSceneTheme )// 表示已经有一个场景加载，则开始替换场景
			{
				if( freemain != null )
				{
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							changeScene();
						}
					} );
				}
			}
			isSceneTheme = true;// 当加载一次场景后就为true
		}
	}
	
	/*
	 * 删除以前场景
	 */
	public void deleteScene()
	{
		// sendFreeReceive(deleteSceneIntent);
		freeMainCreated = false;
		isSceneTheme = false;// 确保删除以前场景后可以去加载最新的场景
		if( freemain != null )
		{
			freemain.releaseFocus();
			this.removeView( freemain );
			// int old = Utils3D.showPidMemoryInfo("notclear");
			// Log.v("", "root list not clear 内存为：" + old);
			freemain.dispose();
			Log.v( "memory" , "root list clear后 内存为：" + Utils3D.showPidMemoryInfo( "clear" ) );
		}
		freemain = null;
		// isadd = true;
	}
	
	/*
	 * 卸载场景
	 */
	public void uninstallScene()
	{
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				deleteScene();
			}
		} );
	}
	
	/*
	 * 应用场景
	 */
	public void applyNewScene()
	{
		isSceneTheme = true;// 当加载一次场景后就为true
		// startScene();
	}
	
	private void changeScene()
	{
		// TODO Auto-generated method stub
		// freemain =
		deleteScene();
		applyNewScene();
	}
	
	public void closeRootDoor()
	{
		for( int i = 0 ; i < workspace.getChildCount() ; i++ )
		{
			View3D view = workspace.getChildAt( i );
			view.y = 0;
		}
		// hotButton.x = hotbuttonx;
		// hotButton.y = 0;
		pageIndicator.y = pageIndicatorOldY;
		hotseatBar.y = 0;
	}
	
	public void stopFreeView(
			float allfreedownTime )
	{
		// TODO Auto-generated method stub
		if( allfreedownTime > 0.1f )
		{
			allfreedownTime = allfreedownTime - 0.1f;
		}
		for( int i = 0 ; i < workspace.getChildCount() ; i++ )
		{
			View3D view = workspace.getChildAt( i );
			view.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , allfreedownTime , 0f , 0f , 0f );
		}
		stopFreeTween = null;
		statrFreeTween = null;
		isSceneDown = false;
		if( ( DefaultLayout.enable_particle ) && ( !ParticleManager.particleManagerEnable ) )
			ParticleManager.particleManagerEnable = ParticleManager.getParticleManager().partilceCanRander();
		// hotButton.startTween(View3DTweenAccessor.POS_XY,
		// Linear.INOUT, allfreedownTime, hotbuttonx,
		// 0f, 0f);
		pageIndicator.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , allfreedownTime , 0 , pageIndicatorOldY , 0f );
		stopFreeTween = hotseatBar.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , allfreedownTime , 0f , 0f , 0f ).setCallback( this );
		saveSceneIndex( 1 , scenePkg , sceneCls );
		if( oldSetupMenu == null )
		{
			Handler handler = new Handler( iLoongLauncher.getInstance().getMainLooper() );
			handler.post( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					DefaultLayout.scene_main_menu = false;// 使菜单返回原始菜单
					oldSetupMenu = new SetupMenu( iLoongLauncher.getInstance() );
					DefaultLayout.scene_main_menu = true;
				}
			} );
		}
	}
	
	@Override
	public void setActionListener()
	{
		// TODO Auto-generated method stub
		if( FeatureConfig.enable_DefaultScene )
		{
			SetupMenuActions.getInstance().RegisterListener( ActionSetting.ACTION_START_SCENE , this );
		}
	}
	
	@Override
	public void OnAction(
			int actionid ,
			Bundle bundle )
	{
		// TODO Auto-generated method stub
		// Log.v("scene", "scene root");
		if( FeatureConfig.enable_DefaultScene )
		{
			iLoongLauncher.getInstance().postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					startScene();
				}
			} );
		}
	}
	
	// wanghongjian add end
	// zhujieping add begin
	private void MiuiV5FolderBoxBlur(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( folder.captureCurScreen )
		{
			// draw to fbo
			folder.fbo.begin();
			Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
			// 先绘制壁纸到fbo上
			if( FolderIcon3D.wallpaperTextureRegion != null )
			{
				int wpWidth = FolderIcon3D.wallpaperTextureRegion.getTexture().getWidth();
				int wpHeight = FolderIcon3D.wallpaperTextureRegion.getTexture().getHeight();
				batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
				batch.draw( FolderIcon3D.wallpaperTextureRegion , folder.wpOffsetX , 0 , wpWidth , wpHeight );
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
					// TODO Auto-generated method stub
					if( arg0 == TweenCallback.COMPLETE )
					{
						setScale( 1.0f , 1.0f );
					}
				}
			} );
			folder.captureCurScreen = false;
			folder.blurBegin = true;
			hotseatBar.getModel3DGroup().color.a = 1;
			hotseatBar.getModel3DGroup().startTween( View3DTweenAccessor.OPACITY , Quint.IN , DefaultLayout.blurDuration , 0 , 0 , 0 );
			super.draw( batch , parentAlpha );
		}
		else
		{
			super.draw( batch , parentAlpha );
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
						folder.addView( folder.blurredView );
						addView( folder );
						this.hideOtherView();
						if( FolderIcon3D.liveWallpaperActive )
						{
							FolderIcon3D.lwpBackView = new ImageView3D( "lwpBackView" , ThemeManager.getInstance().getBitmap( "theme/pack_source/translucent-black.png" ) );
							FolderIcon3D.lwpBackView.setSize( Gdx.graphics.getWidth() , Gdx.graphics.getHeight() );
							FolderIcon3D.lwpBackView.setPosition( 0 , 0 );
							folder.addView( FolderIcon3D.lwpBackView );
						}
					}
					else
					{
						if( FolderIcon3D.liveWallpaperActive )
						{
							FolderIcon3D.lwpBackView = new ImageView3D( "lwpBackView" , ThemeManager.getInstance().getBitmap( "theme/pack_source/translucent-black.png" ) );
							FolderIcon3D.lwpBackView.setSize( Gdx.graphics.getWidth() , Gdx.graphics.getHeight() );
							FolderIcon3D.lwpBackView.setPosition( 0 , 0 );
							this.addViewBefore( hotseatBar , FolderIcon3D.lwpBackView );
						}
						folder.addViewAt( folder.mFolderIndex , folder.mFolderMIUI3D );
						this.addViewBefore( hotseatBar , folder.blurredView );
						this.addView( folder );
						this.hideOtherView();
						folder.color.a = 0f;
						folder.ishide = true;
					}
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
		if( folder != null )
		{
			folder.color.a = 1f;
			folder.ishide = false;
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
		// Log.e("whywhy", " startOPACITYAnim begin");
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
		if( ( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) // zhenNan.ye
		|| DefaultLayout.miui_v5_folder ) )
		{
			if( folder.getFromWhere() == folder.FROM_CELLLAYOUT )
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
		stopRootTimer();
		if( view == null )
		{
			return;
		}
		if( view != null && view instanceof FolderIcon3D )
		{
			FolderIcon3D folderIcon3D = (com.iLoong.launcher.Folder3D.FolderIcon3D)view;
			folder = null;
			{
				if( folderIcon3D.mInfo.contents.size() >= 0 || folderIcon3D.mFolderMIUI3D.getColseFolderByDragVal() == false )
				{
					boolean bNeedAddDragLayer = true;
					if( folderIcon3D.mInfo.contents.size() == 1 )
					{
						// Log.e("test123", "dealMIUIFolderTweenFinish 5");
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
							dragLayer.addDropTargetBefore( desktopEdit , (DropTarget3D)folderIcon3D );
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
		if( view instanceof FolderIcon3D )
		{
			FolderIcon3D folderIcon3D = (com.iLoong.launcher.Folder3D.FolderIcon3D)view;
			if( folderIcon3D.getParent() == this )
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
		}
		if( dragLayer.isVisible() && dragLayer.draging )
		{
			// Log.e("test123", "dealMIUIFolderTweenFinish trashIcon.show();");
			trashIcon.show();
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
	}
	
	// zhujieping add
	public boolean isDragEnd = false;
	
	@Override
	public boolean pointerInParent(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
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
	//zqh start
	public void requestDockbarFocus()
	{
		if( dockBarButtons == null )
		{
			dockBarButtons = new DockbarObjcetGroup( "dockBarButtons" );
			dockBarButtons.height = R3D.hot_obj_height * 2;
			dockBarButtons.width = Utils3D.getScreenWidth();
			this.addView( dockBarButtons );
			dockBarButtons.bringToFront();
		}
		dockBarButtons.requestFocus();
	}
	
	public void addDockbarToRoot(
			ViewGroupOBJ3D grp1 ,
			ViewGroupOBJ3D grp2 )
	{
		if( grp1 == null || grp2 == null )
		{
			return;
		}
		dockBarButtons.requestFocus();
		grp1.x = 0;
		grp1.y = dockBarButtons.y + R3D.hot_obj_height;
		;
		grp1.height = R3D.hot_obj_height;
		grp1.width = dockBarButtons.width;
		dockBarButtons.addView( grp1 );
		grp2.x = 0;
		grp2.y = 0;
		grp2.height = R3D.hot_obj_height;
		grp2.width = dockBarButtons.width;
		dockBarButtons.addView( grp2 );
		Log.v( "Hotseat" , "onctrlevent: HOTSEAT_OBJ_TWEEN_COMPLETE " );
		//hotseatBar.hide();
	}
	
	public static void releaseBtnDark()
	{
		if( dockBarButtons.getChildCount() > 0 )
		{
			for( int i = 0 ; i < 2 ; i++ )
			{
				ViewGroupOBJ3D grp = (ViewGroupOBJ3D)dockBarButtons.getChildAt( i );
				for( int j = 0 ; j < grp.getChildCount() ; j++ )
				{
					ObjButton btn = (ObjButton)grp.getChildAt( j );
					btn.releaseDark();
				}
			}
		}
	}
	
	public void releaseDockBarFocus()
	{
		if( dockBarButtons != null )
			dockBarButtons.releaseFocus();
	}
	
	public static Root3D getInstance()
	{
		return mInstance;
	}
	
	public void entryCompiler()
	{
		enterDesktopEditMode();
		//        hotseatBar.startDockbarTweenExternal();
		hotseatBar.dockbarTurnDown();
		SendMsgToAndroid.vibrator( R3D.vibrator_duration );
	}
	
	public Bitmap getWallpaper()
	{
		int wpOffsetX;
		Resources res = iLoongLauncher.getInstance().getResources();
		int screenWidth = res.getDisplayMetrics().widthPixels;
		int screenHeight = res.getDisplayMetrics().heightPixels;
		WallpaperManager wallpaperManager = WallpaperManager.getInstance( iLoongLauncher.getInstance() );
		WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
		Drawable drawable = wallpaperManager.getDrawable();
		Bitmap wallpaperBitmap = ( (BitmapDrawable)drawable ).getBitmap();
		int wpWidth = wallpaperBitmap.getWidth();
		int wpHeight = wallpaperBitmap.getHeight();
		int[] pixData = new int[wallpaperBitmap.getWidth() * wallpaperBitmap.getHeight()];
		if( wpWidth > screenWidth )
		{
			int curScreen = iLoongLauncher.getInstance().getCurrentScreen();
			int screenNum = iLoongLauncher.getInstance().getScreenCount();
			int gapWidth = wpWidth - screenWidth;
			wpOffsetX = (int)( (float)gapWidth * curScreen / ( screenNum - 1 ) );
			//wallpaperBitmap.getPixels(pixData, 0, wpWidth, wpOffsetX, 0, screenWidth, screenHeight);
			//  Bitmap screenShot =Bitmap.createBitmap(pixData, 0, wpWidth, wpWidth, wpHeight, Bitmap.Config.ARGB_8888);
			Bitmap screenShot = Bitmap.createBitmap( wallpaperBitmap , wpOffsetX , Utils3D.getStatusBarHeight() , screenWidth , screenHeight - Utils3D.getStatusBarHeight() );
			return screenShot;
		}
		return wallpaperBitmap;
	}
	
	public void onViewClick(
			View3D view )
	{
		int index = (Integer)view.getTag();
		widgetLayout.releaseFocus();
		widgetLayout.hide();
		finishLayout( SPLIT_SCREEN_ONLY_CLOSE_UP_VIEW );
		Log.v( "Root3D" , "xxxx index : " + index );
		switch( index )
		{
			case 0:
				//	            addViewBefore(dragLayer, desktopEdit);
				showAddEditMode();
				break;
			case 1:
				showWidgetEditMode();
				break;
			case 2:
				if( Workspace3D.getInstance().getCurCellIconCount() <= 0 )
				{
					SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.no_space_add_icon ) );
					exitFolder();
				}
				else
				{
					createNewFolder();
					//showAddFolderEditMode();
				}
				break;
			case 3:
				//addViewBefore(dragLayer, desktopEdit);
				showShortcutEditMode();
				break;
			default:
				break;
		}
	}
	
	public void createNewFolder()
	{
		UserFolderInfo folderInfo = iLoongLauncher.getInstance().addFolder( 0 , 0 );
		FolderIcon3D folderIcon3D = new FolderIcon3D( "FolderIcon3DView" , folderInfo );
		this.folder = folderIcon3D;
		folderIcon3D.mInfo.folderFrom = FolderInfo.FOLDER_FROM_NEW;
		SendMsgToAndroid.SendCometFolderRename( folderIcon3D );
	}
	
	public void exitFolder()
	{
		removeFolderRename();
		addViewBefore( hotseatBar , desktopEdit );
		uninitialize();
	}
	
	public void showAddEditMode()
	{
		showScreenBg();
		AddApp addEditMode = (AddApp)this.findView( "appAdd" );
		if( addEditMode == null )
		{
			addEditMode = new AddApp( "appAdd" );
			CooeeIcon3D.addToFolder = false;
			this.addView( addEditMode );
			addEditMode.requestFocus();
		}
		else
		{
			addEditMode.requestFocus();
		}
	}
	
	public void removeAddEditMode()
	{
		AddApp addEditMode = (AddApp)this.findView( "appAdd" );
		if( addEditMode != null )
		{
			addEditMode.releaseFocus();
			removeView( addEditMode );
		}
		removeScreenBg();
	}
	
	public void showWidgetEditMode()
	{
		showScreenBg();
		AddWidget addwidget = (AddWidget)this.findView( "add widget" );
		if( addwidget == null )
		{
			addwidget = new AddWidget( "add widget" );
			this.addView( addwidget );
			addwidget.requestFocus();
		}
		else
		{
			addwidget.requestFocus();
		}
	}
	
	public void removeWidgetEditMode()
	{
		AddWidget addwidget = (AddWidget)this.findView( "add widget" );
		if( addwidget != null )
		{
			addwidget.releaseFocus();
			addwidget.removelist();
			removeView( addwidget );
		}
		removeScreenBg();
	}
	
	public void showShortcutEditMode()
	{
		showScreenBg();
		AddShortcut addShortcut = (AddShortcut)this.findView( "add shortcut" );
		if( addShortcut == null )
		{
			addShortcut = new AddShortcut( "add shortcut" );
			this.addView( addShortcut );
			addShortcut.requestFocus();
		}
		else
		{
			addShortcut.requestFocus();
		}
	}
	
	public void removeShortcutEditMode()
	{
		AddShortcut addShortcut = null;
		View3D temView3d = this.findView( "add shortcut" );
		if( temView3d != null && temView3d instanceof AddShortcut )
		{
			addShortcut = (AddShortcut)temView3d;
		}
		if( addShortcut != null )
		{
			addShortcut.releaseFocus();
			removeView( addShortcut );
		}
		removeScreenBg();
	}
	
	public void showAddFolderEditMode()
	{
		showScreenBg();
		AddFolder addEditMode = (AddFolder)this.findView( "AddFolder" );
		if( addEditMode == null )
		{
			String folderTitle = "Folder";
			if( folder != null )
			{
				folderTitle = folder.mInfo.title.toString();
				if( folderTitle.endsWith( "x.z" ) )
				{
					int length = folderTitle.length();
					if( length > 3 )
					{
						folderTitle = folderTitle.substring( 0 , length - 3 );
					}
				}
			}
			addEditMode = new AddFolder( "AddFolder" , false , folderTitle );
			this.addView( addEditMode );
			addEditMode.requestFocus();
		}
		else
		{
			addEditMode.requestFocus();
		}
	}
	
	public void removeAddFolderEditMode()
	{
		AddFolder addEditMode = (AddFolder)this.findView( "AddFolder" );
		if( addEditMode != null )
		{
			addEditMode.releaseFocus();
			removeView( addEditMode );
		}
		removeScreenBg();
	}
	
	public void showAddFolderAddApp()
	{
		showScreenBg();
		AddFolder.myAppAdd addEditMode = (AddFolder.myAppAdd)this.findView( "FolderAddApp" );
		if( addEditMode == null )
		{
			addEditMode = new AddFolder.myAppAdd( "FolderAddApp" );
			CooeeIcon3D.addToFolder = true;
			this.addView( addEditMode );
			addEditMode.requestFocus();
		}
		else
		{
			addEditMode.requestFocus();
		}
	}
	
	public void removeAddFolderAppEditMode()
	{
		AddFolder.myAppAdd addEditMode = (AddFolder.myAppAdd)this.findView( "FolderAddApp" );
		if( addEditMode != null )
		{
			addEditMode.releaseFocus();
			removeView( addEditMode );
		}
		removeScreenBg();
	}
	
	public void showFolderRename()
	{
		showScreenBg();
		AddFolder addEditMode = (AddFolder)this.findView( "AddFolder" );
		if( addEditMode == null )
		{
			String folderTitle = "Folder";
			if( folder != null )
			{
				folderTitle = folder.mInfo.title.toString();
				if( folderTitle.endsWith( "x.z" ) )
				{
					int length = folderTitle.length();
					if( length > 3 )
					{
						folderTitle = folderTitle.substring( 0 , length - 3 );
					}
				}
			}
			addEditMode = new AddFolder( "AddFolder" , true , folderTitle );
			this.addView( addEditMode );
			addEditMode.requestFocus();
		}
	}
	
	public void removeFolderRename()
	{
		if( folder != null )
		{
			//			if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
			//					|| DefaultLayout.miui_v5_folder) {
			//				folder.mFolderMIUI3D.setEditText(desktopEdit.foldername);
			//			} else {
			//				folder.mFolder.setEditText(desktopEdit.foldername);
			//			}
			folder = null;
		}
		removeScreenBg();
		AddFolder addEditMode = (AddFolder)this.findView( "AddFolder" );
		if( addEditMode != null )
		{
			addEditMode.releaseFocus();
			addEditMode.remove();
		}
	}
	
	public void showScreenBg()
	{
		if( screenBg == null )
		{
			screenBg = new View3D( "screen bg" ) {
				
				@Override
				public boolean onTouchDown(
						float x ,
						float y ,
						int pointer )
				{
					requestFocus();
					return true;
				}
				
				@Override
				public boolean onTouchUp(
						float x ,
						float y ,
						int pointer )
				{
					releaseFocus();
					return true;
				}
				
				@Override
				public void remove()
				{
					// TODO Auto-generated method stub
					finishLayout( SPLIT_SCREEN_CLOSE_VIEW );
					if( userAction == SPLIT_SCREEN_ONLY_CLOSE_UP_VIEW )
					{
						uninitialize();
						Log.v( "Root3D" , "remove upviewbg" );
					}
					super.remove();
				}
			};
			if( layoutBg == null )
			{
				try
				{
					Bitmap bmp = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/pack_source/frameBg.png" ) );
					if( bmp.getConfig() != Config.ARGB_8888 )
					{
						bmp = bmp.copy( Config.ARGB_8888 , false );
					}
					Texture t = new BitmapTexture( bmp );
					layoutBg = new NinePatch( new TextureRegion( t ) , 1 , 1 , 1 , 1 );
					bmp.recycle();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
			screenBg.setBackgroud( layoutBg );
			this.addView( screenBg );
		}
	}
	
	public void removeScreenBg()
	{
		if( screenBg != null )
		{
			screenBg.releaseFocus();
			screenBg.remove();
			screenBg = null;
		}
	}
	
	public final float totalLenght = 100;
	public final float vel = totalLenght / 1000;
	public float startTime = 0;
	
	public Vector2 getNewPoint(
			int ms )
	{
		Vector2 point = new Vector2();
		if( ms - startTime < totalLenght / vel )
		{
		}
		return point;
	}
	
	public class ScreenGroup extends ViewGroup3D
	{
		
		public ScreenGroup(
				String name )
		{
			super( name );
			this.height = widgetLayout.height;
			this.width = widgetLayout.width;
			this.x = 0;
			this.y = 0;
			setOrigin( this.width / 2 , this.height / 2 );
		}
		
		public void dispose()
		{
			for( int i = 0 ; i < this.getChildCount() ; i++ )
			{
				if( this.getChildAt( i ) instanceof View3D )
				{
					View3D view = (View3D)this.getChildAt( i );
					view.dispose();
				}
			}
		}
	}
	
	public class ScreenView extends View3D
	{
		
		public ScreenView(
				String name )
		{
			super( name );
		}
		
		public void destory()
		{
			if( region != null && region.getTexture() != null )
			{
				region.getTexture().dispose();
			}
		}
		
		public void setScreen(
				Texture texture )
		{
			this.region.setTexture( texture );
		}
	};
	
	public class SplitViewGroup extends ViewGroup3D
	{
		
		public SplitViewGroup(
				String name )
		{
			super( name );
			this.height = widgetLayout.height;
			this.width = Utils3D.getScreenWidth();
			this.setOrigin( this.width / 2 , this.height * 0.72f );
			this.transform = true;
			//Utils3D.getScreenHeight() (bottomPixels)/(bottomPixels+headPixels+middlePixels)
		}
	}
	
	public class SplitView extends View3D
	{
		
		public SplitView(
				String name )
		{
			super( name );
			this.height = widgetLayout.height;
			this.width = Utils3D.getScreenWidth();
			this.setOrigin( this.width / 2 , this.height / 2 );
		}
		
		public void init()
		{
		}
		
		public void setV2(
				float v2 )
		{
			this.region.setV2( v2 );
		}
		
		public void setV1(
				float v1 )
		{
			this.region.setV( v1 );
		}
	}
	
	public static void getSceenDrawable()
	{
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			public void run()
			{
				Log.v( "Root3D " , " change wallpager" );
				Bitmap wallpaperBitmap = ShowDesktop.getWallpaper();
				BitmapTexture texture = new BitmapTexture( wallpaperBitmap );
				//                Texture t = wallpaperTextureRegion.getTexture();
				//                if (t != null)
				//                    t.dispose();
				//               
				//                wallpaperTextureRegion.setRegion(texture);
				if( upWallpaperView != null && upWallpaperView.region != null && upWallpaperView.region.getTexture() != null )
				{
					upWallpaperView.region.getTexture().dispose();
				}
				upWallpaperView = new View3D( "wallpaperView" );
				upWallpaperView.region.setRegion( texture );
				if( downWallpaperView != null && downWallpaperView.region != null && downWallpaperView.region.getTexture() != null )
				{
					downWallpaperView.region.getTexture().dispose();
				}
				downWallpaperView = new View3D( "downWallpaperView" );
				downWallpaperView.region.setRegion( texture );
			}
		} );
	}
	
	public void initFrameSurface()
	{
		frameSurface[0] = new View3D( "frameSurface[0]" ) {};
		if( frameSurface != null )
		{
			try
			{
				Bitmap bmp = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/pack_source/splitBg.png" ) );
				if( bmp.getConfig() != Config.ARGB_8888 )
				{
					bmp = bmp.copy( Config.ARGB_8888 , false );
				}
				Texture t = new BitmapTexture( bmp );
				bmp.recycle();
				splitScreenBg.region = new TextureRegion( t );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	public void getStarted()
	{
		if( !screenUtils.getReady() )
		{
			Log.v( "Root3D" , "NOT READY" );
			return;
		}
		screenUtils.setCurrScreenWPTexture();
		verifyWPType();
		setCurrScreenTexture();
		splitScreen();
	}
	
	public void verifyWPType()
	{
		WallpaperManager wallpaperManager = WallpaperManager.getInstance( iLoongLauncher.getInstance() );
		WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
		if( wallpaperInfo != null )
		{
			try
			{
				Bitmap bmp = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/pack_source/translucent-black.png" ) );
				if( bmp.getConfig() != Config.ARGB_8888 )
				{
					bmp = bmp.copy( Config.ARGB_8888 , false );
				}
				Texture t = new BitmapTexture( bmp );
				screenUtils.wallpaperTextureRegion.setTexture( t );
				bmp.recycle();
				screenUtils.isLiveWp = true;
				Log.v( "Root3D" , "USE DEFAULT BG" );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	public void setCurrScreenTexture()
	{
		//         if(upWallpaperView!=null&&upWallpaperView.region!=null&&upWallpaperView.region.getTexture()!=null){
		//             upWallpaperView.region.getTexture().dispose();
		//         }
		upWallpaperView = new View3D( "wallpaperView" );
		//         if(downWallpaperView!=null&&downWallpaperView.region!=null&&downWallpaperView.region.getTexture()!=null){
		//             downWallpaperView.region.getTexture().dispose();
		//         }
		downWallpaperView = new View3D( "downWallpaperView" );
		if( screenUtils.isLiveWP() )
		{
			upWallpaperView.region = screenUtils.getSreenRetion();
			Log.v( "Root3D" , "get default region" );
			upWallpaperView.setSize( Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
			downWallpaperView.region = screenUtils.getSreenRetion();
			downWallpaperView.setSize( Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
		}
		else
		{
			upWallpaperView.region.setRegion( screenUtils.getCurrScreenTexture() );
			downWallpaperView.region.setRegion( screenUtils.getCurrScreenTexture() );
		}
	}
	
	public void splitScreen()
	{
		Log.v( "Root3D" , "splitScreen" );
		if( fp == null )
			fp = new FeaturePanel();
		//request should get started after recent animal ends;
		if( fp.getStart() )
			return;
		if( upWallpaperView == null || downWallpaperView == null )
			return;
		renderScreen();
		createWidgetLayout();
		int screenHeight = Utils3D.getScreenHeight();
		int screenWidth = Utils3D.getScreenWidth();
		final float v1 = touchView.y / this.height;
		final float v2 = ( this.height - ( touchView.y + widgetLayout.height ) ) / this.height;
		final float headDegree = 1 / 4.0f;
		final float bottonDegree = 1 / 16.0f;
		final float middleXDegree = v1 + bottonDegree;
		final Vector2 vPoint = new Vector2( 0 , touchView.y );
		fp.initData( vPoint );
		userAction = SPLIT_SCREEN_ONLY_CLOSE_UP_VIEW;
		upViewGrp = new ViewGroup3D( "upViewGrp" );
		upViewGrp.x = 0;
		upViewGrp.y = touchView.y;
		upViewGrp.height = screenHeight - touchView.y;
		upViewGrp.width = screenWidth;
		this.addView( upViewGrp );
		if( !screenUtils.isLiveWP() )
		{
			upWallpaperView.region.setV( upWallpaperView.region.getV2() );
			upWallpaperView.region.setV2( v1 );
		}
		// upWallpaperView.region.setV2(1-v1);
		upWallpaperView.height = screenHeight - touchView.y;
		// wallpaperView.region.setV((v1)*wallpaperView.region.getV2());
		upViewGrp.addView( upWallpaperView );
		upView = new View3D( "upScrren" );
		upView.x = 0;
		upView.y = 0;
		upView.height = screenHeight - touchView.y;
		upView.width = screenWidth;
		upViewGrp.addView( upView );
		upViewGrp.addView( upViewBg );
		upViewGrp.addView( fp.getFrameSurface0() );
		TextureRegion upScreen = new TextureRegion( frameTexture );
		upScreen.setV( 1.0f );
		upScreen.setV2( ( v1 ) * upScreen.getV2() );
		upView.region = upScreen;
		downViewGrp = new ViewGroup3D( "downViewGrp" );
		downViewGrp.x = 0;
		downViewGrp.y = 0;
		downViewGrp.height = touchView.y;
		downViewGrp.width = screenWidth;
		this.addView( downViewGrp );
		if( !screenUtils.isLiveWP() )
		{
			downWallpaperView.region.setV( v1 );
			downWallpaperView.region.setV2( 0 );
		}
		downWallpaperView.height = touchView.y;
		downViewGrp.addView( downWallpaperView );
		downView = new View3D( "downView" );
		downView.x = 0;
		downView.y = 0;
		downView.height = touchView.y;
		downView.width = screenWidth;
		downViewGrp.addView( downView );
		downViewGrp.addView( downViewBg );
		downViewGrp.addView( fp.getFrameSurface1() );
		// splitViews.put("upScrren", upView);
		//upView.setBackgroud(new NinePatch(R3D.screenBackRegion));
		Log.v( "Root3D" , "v1: " + v1 + " v2: " + v2 );
		TextureRegion downScreen = new TextureRegion( frameTexture );
		downScreen.setV( ( v1 ) * downScreen.getV2() );
		downScreen.setV2( 0.0f );
		downView.region = downScreen;
		// downScreen.setV(v2*upScreen.getV2());
		// downScreen.setV2(0.0f);
		//	     
		//	     TextureRegion bellowScreen =new TextureRegion(screenTexture);
		//	    // bellowScreen.setV(bellowScreen.getV2()/2);
		//	     bellowScreen.setV(v2);
		//	     
		TextureRegion middleScreen = new TextureRegion( frameTexture );
		middleScreen.setV( v1 * middleScreen.getV2() );
		middleScreen.setV2( v2 * middleScreen.getV2() );
		float duration = 1.5f;
		//reset the state;
		//	     if(screenBgView!=null)
		//             screenBgView.destory();
		if( screenGroup != null )
		{
			screenGroup.dispose();
		}
		screenGroup = new ScreenGroup( "screen group" );
		widgetLayout.addView( screenGroup );
		SplitViewGroup splitGroup = new SplitViewGroup( "splitGroup" );
		screenGroup.addView( splitGroup );
		SplitView middleView = new SplitView( "middle view" );
		middleView.setPosition( 0 , 0 );
		middleView.region = new TextureRegion( middleScreen );
		if( splitScreenBg == null )
		{
			splitScreenBg = new View3D( "splitScreenBg" );
			splitScreenBg.x = 0;
			splitScreenBg.y = 0;
			splitScreenBg.height = widgetLayout.height;
			splitScreenBg.width = Utils3D.getScreenWidth();
			try
			{
				Bitmap bmp = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/pack_source/splitBg.png" ) );
				if( bmp.getConfig() != Config.ARGB_8888 )
				{
					bmp = bmp.copy( Config.ARGB_8888 , false );
				}
				Texture t = new BitmapTexture( bmp );
				bmp.recycle();
				splitScreenBg.region = new TextureRegion( t );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		if( splitScreenBg2 == null )
		{
			splitScreenBg2 = new View3D( "splitScreenBg" );
			splitScreenBg2.x = 0;
			splitScreenBg2.y = 0;
			splitScreenBg2.height = widgetLayout.height;
			splitScreenBg2.width = Utils3D.getScreenWidth();
			try
			{
				Bitmap bmp = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/pack_source/frameBg.png" ) );
				if( bmp.getConfig() != Config.ARGB_8888 )
				{
					bmp = bmp.copy( Config.ARGB_8888 , false );
				}
				Texture t = new BitmapTexture( bmp );
				bmp.recycle();
				splitScreenBg2.region = new TextureRegion( t );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		splitScreenBg2.color.a = 0;
		//splitScreenBg.color.a=0;
		splitGroup.addView( middleView );
		splitGroup.addView( splitScreenBg );
		splitGroup.transform = true;
		splitGroup.addView( splitScreenBg2 );
		//splitGroup.addView(upView);
		screenGroup.addView( splitGroup );
		screenGroup.transform = true;
		addLayoutItems();
		fp.setStart( true );
		timeLine = Timeline.createParallel();
		for( int i = 0 ; i < 4 ; i++ )
		{
			timeLine.push( Tween.to( view[i] , View3DTweenAccessor.OPACITY , 0 ).target( 1 , 1 ).delay( openDelay + i / 10f ) );
		}
		timeLine.push( Tween.to( downView , View3DTweenAccessor.OPACITY , 0.6f ).delay( openDelay ).target( 0.50f , 0.50f ).ease( Cubic.INOUT ) );
		timeLine.push( Tween.to( upView , View3DTweenAccessor.OPACITY , 0.6f ).delay( openDelay ).target( 0.50f , 0.50f ).ease( Cubic.INOUT ) );
		timeLine.push( Tween.to( splitGroup , View3DTweenAccessor.SCALE_XY , 0.6f ).delay( openDelay ).target( 0.70f , 0.70f ).ease( Cubic.INOUT ) );
		timeLine.push( Tween.to( downViewBg , View3DTweenAccessor.OPACITY , 0.6f ).delay( openDelay ).target( 1 , 1 ).ease( Cubic.INOUT ) );
		timeLine.push( Tween.to( upViewGrp , View3DTweenAccessor.POS_XY , 0.4f ).target( 0 , touchView.y + widgetLayout.height ).ease( Cubic.INOUT ) );
		timeLine.push( Tween.to( upViewBg , View3DTweenAccessor.OPACITY , 0.6f ).delay( openDelay ).target( 1 , 1 ).ease( Cubic.INOUT ) );
		timeLine.push( Tween.to( splitScreenBg2 , View3DTweenAccessor.OPACITY , 0.6f ).delay( openDelay ).target( 1 , 1 ).ease( Cubic.INOUT ) );
		//  timeLine.push(Tween.to(splitScreenBg2, View3DTweenAccessor.SCALE_XY,0.6f).target(0.70f,0.70f).ease(Cubic.INOUT));
		timeLine.start( View3DTweenAccessor.manager ).setCallback( this ).setUserData( SPLIT_SCREEN_OPEN_VIEW );
		rotateLayoutItems();
	}
	
	public void rotateLayoutItems()
	{
		viewLine = Timeline.createParallel();
		float float_y = -10;
		float duration = 0.250f;
		for( int i = 0 ; i < 4 ; i++ )
		{
			view[i].show();
		}
		if( Utils3D.getScreenWidth() == 480 && Utils3D.getScreenHeight() <= 800 )
			float_y = -20;
		viewLine.push( Tween.to( view[0] , View3DTweenAccessor.POS_XY , duration ).target( view[0].x , float_y ).delay( 0.4f + openDelay ).ease( Back.OUT ) );
		viewLine.push( Tween.to( view[1] , View3DTweenAccessor.POS_XY , duration ).target( view[1].x , float_y ).delay( 0.5f + openDelay ).ease( Back.OUT ) );
		viewLine.push( Tween.to( view[2] , View3DTweenAccessor.POS_XY , duration ).target( view[2].x , float_y ).delay( 0.6f + openDelay ).ease( Back.OUT ) );
		viewLine.push( Tween.to( view[3] , View3DTweenAccessor.POS_XY , duration ).target( view[3].x , float_y ).delay( 0.7f + openDelay ).ease( Back.OUT ) );
		viewLine.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	public void addLayoutItems()
	{
		final float leftPadding = 40 * Gdx.graphics.getDensity();
		final float rightPadding = 40 * Gdx.graphics.getDensity();
		final float scaleX = this.width / 720;
		final int itemNum = 4;
		//float density =Gdx.graphics.getDensity();
		final float itemWidth = ( this.width - leftPadding - rightPadding ) / itemNum;
		String name[] = { "add_app_icon.png" , "add_widget_icon.png" , "add_folder_icon.png" , "add_shortcut_icon.png" };
		String name1[] = {
				iLoongLauncher.getInstance().getString( RR.string.add_add_app ) ,
				iLoongLauncher.getInstance().getString( RR.string.add_widget ) ,
				iLoongLauncher.getInstance().getString( RR.string.add_folder ) ,
				iLoongLauncher.getInstance().getString( RR.string.add_shortcut ) };
		ShortcutInfo info = new ShortcutInfo();
		try
		{
			int i;
			for( i = 0 ; i < 4 ; i++ )
			{
				Bitmap appBitmap = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/hotseatbar/" + name[i] ) );
				appBitmap = Tools.resizeBitmap( appBitmap , scaleX );
				if( appBitmap != null )
				{
					view[i] = new Icon3D( "add app" , appBitmap , name1[i] , null ) {
						
						private boolean isStart = false;
						
						public boolean onClick(
								float x ,
								float y )
						{
							onViewClick( this );
							return true;
						}
						
						@Override
						public void onParticleCallback(
								int type )
						{
							// TODO Auto-generated method stub
							if( type == ParticleCallback.END )
							{
								Log.v( "Root3D" , "xxxx particle stop " );
							}
						}
						
						public void hide()
						{
							stopParticle( this.particleType );
							super.hide();
						}
						
						public boolean onDoubleClick(
								float x ,
								float y )
						{
							return true;
						}
						
						public void draw(
								SpriteBatch batch ,
								float parentAlpha )
						{
							super.draw( batch , parentAlpha );
							if( isStart )
							{
								for( int i = 0 ; i < this.width ; i++ )
								{
								}
							}
						}
					};
					widgetLayout.transform = true;
					view[i].setTag( i );
					view[i].setItemInfo( info );
					view[i].x = leftPadding + i * itemWidth + ( itemWidth - view[i].width ) / 2;
					view[i].y = widgetLayout.height - 10;
					//view[i].setOrigin(view[i].x/2, view[i].y/2);
					//view[i].setScale(scaleX,scaleX);
					//     view[i].width=this.wi
					// view[i].setRotationX(-91);
					//  view[i].hide();
					view[i].color.a = 0;
					widgetLayout.addView( view[i] );
					//                    Utils3D.changeTextureRegion(view[i],
					//                            Utils3D.getIconBmpHeight(), true);
					appBitmap.recycle();
				}
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		//if(!widgetLayoutExist){
		try
		{
			Bitmap appBitmap = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/pack_source/splitLine.png" ) );
			Texture t = new BitmapTexture( appBitmap );
			if( appBitmap != null )
			{
				for( int i = 0 ; i < 2 ; i++ )
				{
					lineView[i] = new View3D( "lineView" );
					lineView[i].x = 0;
					lineView[i].y = 0;
					lineView[i].height = widgetLayout.height / 30;
					lineView[i].width = widgetLayout.width;
					lineView[i].region.setRegion( t );
					//  lineView[i].color.a=0.8f;
				}
				lineView[0].y = -2;
				lineView[1].y = touchView.y;
				upViewGrp.addView( lineView[0] );
				downViewGrp.addView( lineView[1] );
				widgetLayoutExist = true;
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		// }
	}
	
	public void dispose()
	{
		if( timeLine != null )
		{
			timeLine.free();
			timeLine = null;
		}
		if( viewLine != null )
		{
			viewLine.free();
			viewLine = null;
		}
		if( widgetLayout != null )
		{
			for( int i = 0 ; i < widgetLayout.getChildCount() ; i++ )
			{
				if( widgetLayout.getChildAt( i ) instanceof View3D )
				{
					View3D view = (View3D)widgetLayout.getChildAt( i );
					view.dispose();
				}
			}
		}
		if( widgetLayout != null )
		{
			disposeTexture();
		}
	}
	
	public void disposeTexture()
	{
		if( widgetLayout.region.getTexture() != null )
		{
			widgetLayout.region.getTexture().dispose();
		}
		//         if (widgetLayout.background9 != null) {
		//             TextureRegion[] tr = background9.getPatches();
		//             for (int i = 0; i < tr.length; i++) {
		//                 if (tr[i] != null && tr[i].getTexture() != null) {
		//                     tr[i].getTexture().dispose();
		//                 }
		//             }
		//         }
		widgetLayout = null;
	}
	
	public void finishLayout(
			int userAction )
	{
		//	     Root3D.widgetLayout.releaseFocus();
		//         Root3D.widgetLayout.hide();
		//         workspace.show();
		//         hotseatBar.show();
		//         this.removeView(upViewGrp);
		//         this.removeView(downViewGrp);
		if( fp == null )
		{
			return;
		}
		if( fp.getStart() )
			return;
		this.userAction = userAction;
		if( timeLine != null )
		{
			timeLine.free();
		}
		fp.setStart( true );
		timeLine = Timeline.createParallel();
		timeLine.push( Tween.to( downView , View3DTweenAccessor.OPACITY , 0.6f ).target( 1.0f , 1.0f ).ease( Cubic.INOUT ) );
		timeLine.push( Tween.to( upView , View3DTweenAccessor.OPACITY , 0.6f ).target( 1.0f , 1.0f ).ease( Cubic.INOUT ) );
		timeLine.push( Tween.to( upViewGrp , View3DTweenAccessor.POS_XY , 0.6f ).target( 0 , touchView.y ).ease( Cubic.INOUT ) );
		if( userAction != SPLIT_SCREEN_ONLY_CLOSE_UP_VIEW )
		{
			// Log.v("Root3D", "finishLayout  ");
			timeLine.push( Tween.to( upViewBg , View3DTweenAccessor.OPACITY , 0.6f ).target( 0 , 0 ).ease( Linear.INOUT ) );
			timeLine.push( Tween.to( downViewBg , View3DTweenAccessor.OPACITY , 0.6f ).target( 0 , 0 ).ease( Linear.INOUT ) );
		}
		timeLine.start( View3DTweenAccessor.manager ).setCallback( this ).setUserData( SPLIT_SCREEN_CLOSE_VIEW );
		//        
		// upViewGrp.dispose();
	}
	
	public void uninitialize()
	{
		//this flat is consdered to judge whether the layout has been initlialized.
		if( upViewGrp != null )
		{
			Root3D.widgetLayout.releaseFocus();
			Root3D.widgetLayout.hide();
			hotseatBar.show();
			this.removeView( upViewGrp );
			this.removeView( downViewGrp );
			this.removeView( widgetLayout );
			fp.onEffectionComplete();
		}
	}
	
	public void closeWidgetLayout()
	{
		userAction = SPLIT_SCREEN_ONLY_CLOSE_UP_VIEW;
		upViewGrp.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , 0.4f , 0 , touchView.y , 0 ).setUserData( SPLIT_SCREEN_CLOSE_VIEW ).setCallback( this );
	}
	
	public void renderScreen()
	{
		fb.begin();
		float a = this.getStage().getRoot().color.a;
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		Gdx.gl.glClear( GL10.GL_DEPTH_BUFFER_BIT ); // 清除屏幕及深度缓存
		SpriteBatch batch = this.getStage().getSpriteBatch();
		this.getStage().draw();
		fb.end();
		frameTexture = fb.getColorBufferTexture();
	}
	
	public void createWidgetLayout()
	{
		if( widgetLayout != null )
		{
			dispose();
		}
		if( widgetLayout == null )
		{
			if( downViewBg == null )
			{
				downViewBg = new View3D( "screen bg" ) {
					
					@Override
					public boolean onTouchDown(
							float x ,
							float y ,
							int pointer )
					{
						requestFocus();
						return true;
					}
					
					@Override
					public boolean onTouchUp(
							float x ,
							float y ,
							int pointer )
					{
						releaseFocus();
						return true;
					}
				};
				try
				{
					Bitmap bmp = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/pack_source/frameBg.png" ) );
					if( bmp.getConfig() != Config.ARGB_8888 )
					{
						bmp = bmp.copy( Config.ARGB_8888 , false );
					}
					Texture t = new BitmapTexture( bmp );
					layoutBg = new NinePatch( new TextureRegion( t ) , 1 , 1 , 1 , 1 );
					bmp.recycle();
					//screenBg.height=widgetLayout.height;
					// screenBg.region=new TextureRegion(t);
					downViewBg.setBackgroud( layoutBg );
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				this.addView( downViewBg );
			}
			if( upViewBg == null )
			{
				upViewBg = new View3D( "upViewBg" );
				upViewBg.setBackgroud( layoutBg );
			}
			downViewBg.color.a = 0;
			upViewBg.color.a = 0;
			if( layoutFrame == null )
			{
				try
				{
					Bitmap bmp = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/pack_source/screenShotFrame.png" ) );
					if( bmp.getConfig() != Config.ARGB_8888 )
					{
						bmp = bmp.copy( Config.ARGB_8888 , false );
					}
					Texture t = new BitmapTexture( bmp );
					layoutFrame = new NinePatch( new TextureRegion( t ) , 1 , 1 , 1 , 1 );
					bmp.recycle();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
			widgetLayout = new ViewGroup3D( "widgetLayout" ) {
				
				public boolean onLongClick(
						float x ,
						float y )
				{
					Log.v( "Root3D" , "xxxx parent longclick : " );
					return true;
				}
				
				@Override
				public boolean onTouchDown(
						float x ,
						float y ,
						int pointer )
				{
					if( x >= 0 && x < width && y >= 0 && y < height )
					{
						return super.onTouchDown( x , y , pointer );
					}
					else
					{
						finishLayout( SPLIT_SCREEN_CLOSE_VIEW );
						return true;
					}
				}
				
				public void hide()
				{
					//     screenBg.hide();
					for( int i = 0 ; i < getChildCount() ; i++ )
					{
						if( getChildAt( i ) instanceof Icon3D )
						{
							Icon3D icon = (Icon3D)getChildAt( i );
							icon.stopParticle( icon.particleType );
						}
					}
					stopParticle( this.particleType );
					super.hide();
				}
				
				public void show()
				{
					if( screenBg != null )
						screenBg.show();
					super.show();
				}
			};
			if( touchView == null )
			{
				touchView = new View3D( "touchView" ) {
					
					public boolean onClick(
							float x ,
							float y )
					{
						Log.v( "Root3D" , "xxxx my click : " );
						return true;
					}
					
					public boolean onLongClick(
							float x ,
							float y )
					{
						Log.v( "Root3D" , "xxxx my longclick : " );
						return true;
					}
				};
				touchView.region = R3D.findRegion( "long_touch_light" );
				touchView.width = R3D.workspace_cell_width;
				touchView.height = R3D.workspace_cell_height;
			}
			if( clickPoint.x - R3D.workspace_cell_width / 2 > 0 )
				touchView.x = clickPoint.x - R3D.workspace_cell_width / 2;
			else
				touchView.x = 0;
			if( clickPoint.y + 1 / ScreenShotDegree * this.height > Utils3D.getScreenHeight() )
				touchView.y = Utils3D.getScreenHeight() - 1 / ScreenShotDegree * this.height;
			else
				touchView.y = clickPoint.y;
			widgetLayout.x = 0;
			widgetLayout.y = touchView.y;
			widgetLayout.width = this.width;
			widgetLayout.height = 1 / ScreenShotDegree * this.height;
			downViewBg.height = touchView.y;
			if( layoutFrame != null )
				widgetLayout.setBackgroud( layoutFrame );
			this.addView( widgetLayout );
			//  widgetLayout.addView(touchView);
			widgetLayout.requestFocus();
			widgetLayout.show();
		}
	}
	
	//zqh end
	public void workspaceAnimWhenHotseatRotation()
	{
		if( !HotSeat3D.menuAnimalComplete )
		{
			return;
		}
		float duration = 0.35f;
		if( HotSeat3D.menuOpened )
		{
			if( workspace.getTween() != null )
			{
				workspace.getTween().free();
			}
			workspace.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , duration , 1f , 1f , 0 );
		}
		else
		{
			if( workspace.getTween() != null )
			{
				workspace.getTween().free();
			}
			workspace.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , duration , 0.83f , 0.83f , 0 );
		}
	}
}
