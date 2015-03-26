package com.iLoong.launcher.Desktop3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooee.android.launcher.framework.IconCache;
import com.cooee.android.launcher.framework.LauncherModel;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.DesktopEdit.CustomShortcutIcon;
import com.iLoong.launcher.DesktopEdit.DesktopEditHost;
import com.iLoong.launcher.DesktopEdit.MenuContainer;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreview3D;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreviewTips3D;
import com.iLoong.launcher.HotSeat3D.DefConfig;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.SetupMenu.Actions.SystemAction;
import com.iLoong.launcher.SetupMenu.Actions.SystemAction.ResestActivity;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Desktop3D;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.ParticleLoader;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Contact3DShortcut;
import com.iLoong.launcher.Widget3D.Folder3DShortcut;
import com.iLoong.launcher.Widget3D.List3DShortcut;
import com.iLoong.launcher.Widget3D.OtherTools3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DHost.Widget3DProvider;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.CooeePluginInfo;
import com.iLoong.launcher.data.FolderInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.data.Widget2DInfo;
import com.iLoong.launcher.data.Widget3DInfo;
import com.iLoong.launcher.data.WidgetShortcutInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.miui.MIUIWidgetHost;
import com.iLoong.launcher.newspage.NewsHandle;
import com.iLoong.launcher.pub.provider.PubProviderHelper;
import com.iLoong.launcher.search.QSearchGroup;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.widget.Widget;

public class Desktop3DListener implements ApplicationListener,
		LauncherModel.Callbacks {

	public static final String TAG = "D3DListener";
	public static Object lock = new Object();
	public static Object init_lock = new Object();
	private iLoongLauncher launcher;
	public static Desktop3D d3d = null;
	public static Root3D root;
	private PageIndicator3D pageSelectIcon;
	private TrashIcon3D trashIcon;
	public Workspace3D workspace;
	private SetupMenu3D setupMenu;// zjp
	private ApplicationListHost m_applicationListHost;// zjp
	public PageContainer3D pageContainer;
	private static AppHost3D appHost;
	private DragLayer3D dragLayer;
	public static boolean bCreatDone = false;
	public static boolean bSetHomepageDone = false;
	public static boolean bCreat1Done = false;
	public static boolean bDesktopDone = false;
	public static boolean bAppDone = false;
	public static boolean bWidgetDone = false;
	public IconCache iconCache;
	private ArrayList<ItemInfo> mDesktopItems = new ArrayList<ItemInfo>();
	public static ArrayList<WidgetShortcutInfo> allWidgetInfo = new ArrayList<WidgetShortcutInfo>();
	public static Folder3DShortcut folder3DHost;
	public static Contact3DShortcut contact3DHost;
	// teapotXu add start for longClick in Workspace to editMode like MIUI
	private static MIUIWidgetHost widgetHost;
	public static OtherTools3DShortcut otherToolsHost;
	public static List3DShortcut listShortcutHost;
	// teapotXu add end
	// 自定义widget管理?
	private Widget3DManager mWidget3DManagerInstance;
	private Drawable mDefaultWidgetBackground;
	public boolean mPaused = false;
	public boolean mOnResumeNeedsLoad = false;
	public boolean showIntroduction = false;
	private boolean hasCreate = false;
	// xiatian add start //fix bug:bindWidget3DUpdated lead to lose Widget3DView
	// in Workspace3D
	private boolean mIsWidget3DUpdated = false;
	private Widget3DInfo mUpdatedWidget3DInfo = null;
	// xiatian add end
	// xiatian add start //EffectPreview
	private EffectPreview3D mWorkspaceEffectPreview;
	private EffectPreview3D mApplistEffectPreview;
	private EffectPreviewTips3D mEffectPreviewTips3D;
	// xiatian add end
	public boolean mIsDoingAddShortCut = false;
	public static String currentParticleType = null;
	public static Timeline timeline;// Jone add
	public List<String> widget2DpkgName = new ArrayList<String>();
	public List<Bitmap> allwidget2Dbitmap = new ArrayList<Bitmap>();
	private int bindwidgetnum = 0;
	// public GuideBackground welcomeGuide;
	public static Desktop3DListener instance;

	public void forceTouchUp() {
		if (d3d != null) {
			if (dragLayer != null)
				dragLayer.forceTouchUp();
			d3d.forceTouchUp();
		}
	}

	public Desktop3DListener(iLoongLauncher iloong) {
		this.launcher = iloong;
		instance = this;
		iconCache = ((iLoongApplication) launcher.getApplication())
				.getIconCache();
		bCreatDone = false;
	}

	public static Desktop3DListener getInstance() {
		if (instance == null) {
			throw new RuntimeException("Desktop3DListener is null...");
		}
		return instance;
	}

	public AppHost3D getAppList() {
		return appHost;
	}

	public MIUIWidgetHost getWidgetList() {
		return widgetHost;
	}

	public DragLayer3D getDragLayer() {
		return dragLayer;
	}

	public Workspace3D getWorkspace3D() {
		return workspace;
	}

	public Root3D getRoot() {
		return root;
	}

	public static boolean initDone() {
		return bDesktopDone
				& (bWidgetDone || !iLoongApplication.BuiltIn || DefaultLayout.hide_mainmenu_widget);
		// return bDesktopDone & bAppDone & ( bWidgetDone ||
		// !iLoongApplication.BuiltIn || DefaultLayout.hide_mainmenu_widget );
	}

	public void onThemeChanged() {
		iconCache.flush();
		Utils3D.iconBmpHeight = -1;
		R3D.onThemeChanged();
		contact3DHost.onThemeChanged();
		folder3DHost.onThemeChanged();
		widgetHost.widgetList.onThemeChanged();
		R3D.packer.updateTextureAtlas(R3D.packerAtlas, TextureFilter.Linear,
				TextureFilter.Linear);
		root.onThemeChanged();
		if (DefaultLayout.enable_particle) {
			ParticleLoader.OnThemeChanged();
		}
	}

	@Override
	public void create() {
		R3D.initialize(iLoongLauncher.getInstance());
		Widget3DManager.getInstance().processDefaultWidgetView();
		Tween.registerAccessor(View3D.class, new View3DTweenAccessor());
		R3D.packer.updateTextureAtlas(R3D.packerAtlas, TextureFilter.Linear,
				TextureFilter.Linear);
		d3d = new Desktop3D(Utils3D.getScreenWidth(),
				Utils3D.getScreenHeight(), true);
		Gdx.input.setInputProcessor(d3d);
		root = new Root3D("root");
		root.setSize(Utils3D.getScreenWidth(), Utils3D.getScreenHeight());
		root.setLauncher(launcher);
		d3d.addView(root);
		pageSelectIcon = new PageIndicator3D("pageselected3dicon");
		root.setPageIndicator(pageSelectIcon);
		int cellNum = ThemeManager.getInstance().getThemeDB().getScreenCount();
		if (cellNum == 0) {
			cellNum = DefaultLayout.default_workspace_pagecounts;
			ThemeManager.getInstance().getThemeDB().SaveScreenCount(cellNum);
		}
		pageSelectIcon.setPageNum(cellNum);
		// DragLayer must be added at last!!!
		root.getHotSeatBar().setIconCache(iconCache);
		dragLayer = new DragLayer3D("draglayer");
		root.setDragLayer(dragLayer);
		DefaultLayout.setIconCache(iconCache);
		bCreat1Done = true;
		synchronized (lock) {
			lock.notify();
		}
		/****************** added by hugo.ye begin *******************/
		if (DefaultLayout.enable_particle) {
			ParticleLoader.loadAllParticleEffect();
		}
		/****************** added by hugo.ye end *******************/
		if (DefaultLayout.enable_new_particle) {
			ParticleLoader.loadAllParticleEffect();
			int currentParticleTypeNum = SetupMenuActions
					.getInstance()
					.getStringToIntger(
							SetupMenu
									.getKey(RR.string.setting_key_new_particle));
			if (currentParticleTypeNum == 0) {
				currentParticleType = null;
			} else {
				currentParticleType = ParticleLoader.NEW_PARTICLE_TYPE[currentParticleTypeNum - 1];
			}
		}
		// if( DefaultLayout.needToSaveSpecifiedIconXml )
		// {
		// welcomGuide();
		// }
	}

	public void create2() {
		// if( DefaultLayout.needToSaveSpecifiedIconXml )
		// {
		// DefaultLayout.getInstance().cancelProgressDialog();
		// }
		R3D.initialize2(iLoongLauncher.getInstance());
		folder3DHost = new Folder3DShortcut("folder3d");
		contact3DHost = new Contact3DShortcut("contact3d");
		// teapotXu add start
		if (DefaultLayout.enable_workspace_miui_edit_mode) {
			otherToolsHost = new OtherTools3DShortcut("otherTools3d");
			listShortcutHost = new List3DShortcut("listshortcut3D");
		}
		// teapotXu add end
		workspace = new Workspace3D("workspace");
		// if( welcomeGuide != null )
		// workspace.hide();
		int homePage = DefaultLayout.getInstance().loadHomePage();
		int cellNum = ThemeManager.getInstance().getThemeDB().getScreenCount();
		if (cellNum == 0) {
			cellNum = DefaultLayout.default_workspace_pagecounts;
			ThemeManager.getInstance().getThemeDB().SaveScreenCount(cellNum);
		}
		for (int i = 0; i < cellNum; i++) {
			CellLayout3D cellLayout = new CellLayout3D("celllayout");
			cellLayout.setScreen(i);
			workspace.addPage(cellLayout);
		}
		// xujin
		workspace.addCameraView();
		workspace.addMusicView();
		workspace.setHomePage(homePage);
		if (DefaultLayout.enable_apply_saved_cur_screen_when_reboot) {
			int currentPage = DefaultLayout.getInstance()
					.loadCurrentScreenNum();
			if (currentPage == -1) {
				currentPage = homePage;
			}
			workspace.setCurrentScreen(currentPage);
		} else {
			workspace.setCurrentScreen(homePage);
		}
		pageContainer = new PageContainer3D("pagecontainer");
		if (DefaultLayout.setupmenu_by_system) {
		} else if (DefaultLayout.setupmenu_by_view3d) {
			setupMenu = new SetupMenu3D("setup_menu");
			root.setSetupMenu(setupMenu);
		}
		appHost = new AppHost3D("apphost");
		m_applicationListHost = new ApplicationListHost("list");
		root.setApplicationAppHost(m_applicationListHost);
		// teapotXu add start for Folder in Mainmenu
		if (DefaultLayout.mainmenu_folder_function == true)
			appHost.setDragLayer(dragLayer);
		// teapotXu add end for Folder in Mainmenu
		root.setPageContainer(pageContainer);
		root.setWorkspace(workspace);
		if (!DefaultLayout.enable_google_version || VERSION.SDK_INT >= 15) {
			root.newsHandle = new NewsHandle("news_handle",
					iLoongLauncher.getInstance());
			root.addView(root.newsHandle);
			if (!DefaultLayout.enable_news
					|| !DefaultLayout.show_newspage_with_handle) {
				root.newsHandle.hide();
				Messenger.sendMsg(Messenger.MSG_HIDE_NEWSVIEW_HANDLE, null);
			}
		}
		// 设置页数
		pageSelectIcon.setPageNum(workspace.getPageNum());
		root.setAppHost(appHost);
		// teapotXu add start
		if (DefaultLayout.enable_workspace_miui_edit_mode) {
			widgetHost = new MIUIWidgetHost("widgetHost");
			root.setWidgetHost(widgetHost);
		}
		// teapotXu add end
		addPageScrollListener(pageSelectIcon);
		addPageScrollListener(iLoongLauncher.getInstance().xWorkspace);
		dragLayer.addDropTarget(workspace);
		dragLayer.addDropTarget(pageContainer.pageEdit);
		pageSelectIcon.bringToFront();
		trashIcon = new TrashIcon3D();
		root.setTrashIcon(trashIcon);
		dragLayer.addDropTarget(root.getHotSeatBar());
		dragLayer.addDropTarget(trashIcon);
		root.addDragLayer(dragLayer);
		// teapotXu add start for Folder in Mainmenu
		if (DefaultLayout.mainmenu_folder_function == true)
			dragLayer.addDropTarget(appHost);
		// teapotXu add end for Folder in Mainmenu
		appHost.setIconCache(iconCache);
		workspace.setIconCache(iconCache);
		mWidget3DManagerInstance = Widget3DManager.getInstance();
		DefaultLayout.setEnv(root, workspace);
		mDefaultWidgetBackground = launcher.getResources().getDrawable(
				RR.drawable.default_widget_preview_holo);
		if ((ThemeManager.getInstance().getBoolean("miui_v5_folder") || DefaultLayout.miui_v5_folder)
				&& DefaultLayout.blur_enable) {
			FolderIcon3D.genWallpaperTextureRegion();
		}
		// xiatian add start //EffectPreview
		if (DefaultLayout.enable_effect_preview) {
			initEffectPreview();
		}
		// if( DefaultLayout.enable_quick_search )
		// {
		// root.qSearchGroup = new QSearchGroup( "qSearchGroup" );
		// root.addView( root.qSearchGroup );
		// }
		// xiatian add end
		bCreatDone = true;
		if (root.screenFrontImg != null) {
			root.addView(root.screenFrontImg);
			root.screenFrontImg.hide();
		}
		Gdx.graphics.setContinuousRendering(false);
		pause();
		if (DefaultLayout.needToSaveSpecifiedIconXml) {
			// pageSelectIcon.hide();
			// workspace.hide();
			// root.hotseatBar.hide();
			if (!DefaultLayout.enable_google_version) {
				root.newsHandle.hide();
				Messenger.sendMsg(Messenger.MSG_HIDE_NEWSVIEW_HANDLE, null);
			}
		}
	}

	// public void startWelcomeGuide()
	// {
	// iLoongLauncher.getInstance().postRunnable( new Runnable() {
	//
	// @Override
	// public void run()
	// {
	// welcomGuide();
	// }
	// } );
	// }
	// GuideBackground welcomeGuide;
	// public void welcomGuide()
	// {
	// Log.v( TAG , "welcomGuide" );
	// // SharedPreferences share =
	// PreferenceManager.getDefaultSharedPreferences(
	// iLoongLauncher.getInstance() );
	// // boolean first = share.getBoolean( "first_welcome" , false );
	// // if(!first)
	// // share.edit().putBoolean( "first_welcome" , true ).commit();
	// // else
	// // return ;
	// welcomeGuide = new GuideBackground( "welcomeGuide" );
	// if( root != null )
	// {
	// root.addView( welcomeGuide );
	// }
	// welcomeGuide.bringToFront();
	// if( pageSelectIcon != null )
	// pageSelectIcon.hide();
	// if( root != null )
	// {
	// if( root.getWorkspace() != null )
	// root.getWorkspace().hide();
	// if( root.hotseatBar != null )
	// root.hotseatBar.hide();
	// if( !DefaultLayout.enable_google_version )
	// {
	// if( root.newsHandle != null )
	// root.newsHandle.hide();
	// Messenger.sendMsg( Messenger.MSG_HIDE_NEWSVIEW_HANDLE , null );
	// }
	// }
	// // root.hotseatBar.hide();
	// }
	//
	// public void showWelcomGuide()
	// {
	// if( welcomeGuide != null )
	// {
	// welcomeGuide.show();
	// if( pageSelectIcon != null )
	// pageSelectIcon.hide();
	// if( root != null )
	// {
	// if( root.getWorkspace() != null )
	// root.getWorkspace().hide();
	// if( root.hotseatBar != null )
	// root.hotseatBar.hide();
	// if( !DefaultLayout.enable_google_version )
	// {
	// if( root.newsHandle != null )
	// root.newsHandle.hide();
	// Messenger.sendMsg( Messenger.MSG_HIDE_NEWSVIEW_HANDLE , null );
	// }
	// }
	// }
	// }
	//
	// public void closeWelcomeGuide()
	// {
	// if( welcomeGuide == null )
	// return;
	// iLoongLauncher.getInstance().postRunnable( new Runnable() {
	//
	// @Override
	// public void run()
	// {
	// if( welcomeGuide != null )
	// {
	// workspace.show();
	// root.hotseatBar.show();
	// if( !DefaultLayout.enable_google_version )
	// {
	// if( root.newsHandle != null && DefaultLayout.enable_news &&
	// DefaultLayout.show_newspage_with_handle )
	// {
	// Messenger.sendMsg( Messenger.MSG_SHOW_NEWSVIEW_HANDLE , null );
	// root.newsHandle.show();
	// }
	// else
	// {
	// Messenger.sendMsg( Messenger.MSG_HIDE_NEWSVIEW_HANDLE , null );
	// }
	// }
	// pageSelectIcon.show();
	// welcomeGuide.hide();
	// welcomeGuide.remove();
	// welcomeGuide = null;
	// System.gc();
	// if( DefaultLayout.WorkspaceActionGuide )
	// {
	// if( DefaultLayout.mActionHolder != null )
	// {
	// DefaultLayout.mActionHolder.show();
	// //DefaultLayout.mActionHolder.bringToFront();
	// }
	// }
	// }
	// }
	// } );
	// }
	// Jone add start
	public static void setCoverVisible(boolean visible) {
		if (root.screenFrontImg == null) {
			return;
		}
		if (timeline != null) {
			timeline.free();
		}
		timeline = Timeline.createParallel();
		if (visible) {
			timeline.push(root.screenFrontImg
					.obtainTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
							0.5f, 1.0f, 0, 0));
		} else {
			timeline.push(root.screenFrontImg
					.obtainTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
							0.5f, 0.0f, 0, 0));
			// root.screenFrontImg.hide();
		}
		timeline.start(View3DTweenAccessor.manager);
	}

	// Jone add end
	public void showIntroduction() {
		if (bCreat1Done) {
			root.hide();
			iLoongLauncher.getInstance().introductionShown = true;
		} else
			showIntroduction = true;
	}

	public void reset() {
		if (appHost != null) {
			iLoongLauncher.getInstance().postRunnable(new Runnable() {

				@Override
				public void run() {
					appHost.reset();
				}
			});
		}
	}

	@Override
	public void resume() {
		if (appHost != null && appHost.getVisible() == true
				&& appHost.isNeedReset()) {
			SendMsgToAndroid.sendForceResetMainmenuMessage(500);
		}
		// zhujieping add start
		if ((ThemeManager.getInstance().getBoolean("miui_v5_folder") || DefaultLayout.miui_v5_folder)
				&& DefaultLayout.blur_enable) {
			WallpaperManager wallpaperManager = WallpaperManager
					.getInstance(iLoongLauncher.getInstance());
			WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
			if (wallpaperInfo != null) {// 在墙纸变换的广播不接收动态壁纸的变化，因此这边只有动态壁纸时，才get
				FolderIcon3D.genWallpaperTextureRegion();
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(iLoongLauncher
								.getInstance());
				pref.edit().putString("currentWallpaper", "other").commit();
				// teapotXu add start: 壁纸为用户设定壁纸
				pref.edit().putBoolean("userDefinedWallpaper", true).commit();
				PubProviderHelper.addOrUpdateValue("wallpaper",
						"currentWallpaper", "other");
				PubProviderHelper.addOrUpdateValue("wallpaper",
						"userDefinedWallpaper", "true");
				// teapotXu add end
			}
		}
		// zhujieping add end
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		// cur = System.currentTimeMillis();
		View3DTweenAccessor.manager.update(Gdx.graphics.getRawDeltaTime());
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
		// Gdx.gl.glEnable(GL10.GL_ALPHA_TEST);
		d3d.draw();
		if (!mPaused && DefaultLayout.release_gl) {
			if (BitmapTexture.dynamicLoadTime == 0)
				BitmapTexture.dynamicLoadTime = System.currentTimeMillis() + 1000;
			BitmapTexture.onReloadTextures();
		}
		if (mPaused) {
			renderAfterPause();
		}
		// Utils3D.showPidMemoryInfo("render");
	}

	public void renderAfterPause() {
		if (DefaultLayout.release_widget) {
			releaseWidget();
		}
		if (iLoongLauncher.isTrimMemory) {
			onTrimMemory();
		}
	}

	public void onTrimMemory() {
		if (DefaultLayout.enable_release_2Dwidget && AppHost3D.appList != null
				&& AppList3D.hasbind2Dwidget) {
			if (AppHost3D.appList.iswidgetVisible()
					|| (DefaultLayout.enable_workspace_miui_edit_mode && (Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode))) {
				Log.v("", " 处于widget可见状态");
			} else {
				AppHost3D.appList.release2DWidget();
			}
		}
		if (DefaultLayout.release_gl) {
			BitmapTexture.onTrimMemory();
		}
	}

	@Override
	public void resize(int width, int height) {
		Utils3D.resetSize();
		if (Utils3D.getScreenWidth() > Utils3D.getScreenHeight()) {
			Log.e("resize", "width and height error");
			if (iLoongLauncher.getInstance().stoped) {
				Log.e("resize", "width and height error:stoped");
				iLoongLauncher.getInstance().checkSize = true;
			} else if (iLoongLauncher.getInstance().d3dListener.mPaused) {
				Log.e("resize", "width and height error:paused");
				iLoongLauncher.getInstance().checkSize = true;
				// Log.e("resize","width and height error:Remove view...");
				// SendMsgToAndroid.sendRemoveGLViewMsg();
			} else {
				if (width > height) {
					Log.e("resize", "width and height error:Restart...");
					SystemAction.RestartSystem();
				} else {
					Log.e("resize", "width and height error:resumed...");
					iLoongLauncher.getInstance().checkSize = true;
					SendMsgToAndroid.sendCheckSizeMsg(3000);
				}
			}
		}
		// if(width < d3d.width() || height < d3d.height())
		// SystemAction.RestartSystem();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		Log.v("Desktop3D listener", "pause");
		root.pause();
		d3d.resetGesture();
	}

	public FolderIcon3D getOpenFolder() {
		if (root == null) {
			return null;
		}
		int count = root.getChildCount();
		for (int i = 0; i < count; i++) {
			View3D view = root.getChildAt(i);
			if (view instanceof FolderIcon3D) {
				return (FolderIcon3D) view;
			}
		}
		if (root.getOpenFolder() != null) {
			return root.getOpenFolder();
		}
		return null;
	}

	public boolean isApplitionListToAddShow() {
		if (root == null) {
			return false;
		}
		if (root.getListToAdd() != null && root.getListToAdd().isVisible()) {
			return true;
		}
		return false;
	}

	// teapotXu add start for folder in Mainmenu
	public FolderIcon3D getOpenFolderInMainmenu() {
		if (DefaultLayout.mainmenu_folder_function == true) {
			if (root == null || root.getAppHost() == null) {
				return null;
			}
			int count = root.getAppHost().getChildCount();
			for (int i = 0; i < count; i++) {
				View3D view = root.getAppHost().getChildAt(i);
				if (view instanceof FolderIcon3D) {
					return (FolderIcon3D) view;
				}
			}
		}
		return null;
	}

	// teapotXu add end for folder in Mainmenu
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		View3DTweenAccessor.manager.killAll();
		// zhujieping add start
		if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable) {
			if (FolderIcon3D.shader != null) {
				FolderIcon3D.shader.dispose();
				FolderIcon3D.shader = null;
			}
		}
		// zhujieping add end
		/****************** added by zhenNan.ye begin *******************/
		if (DefaultLayout.enable_particle || DefaultLayout.enable_new_particle) {
			ParticleLoader.freeAllParticleEffect();
		}
		/****************** added by zhenNan.ye end *******************/
	}

	public boolean setLoadOnResume() {
		if (mPaused) {
			Log.i(TAG, "setLoadOnResume");
			mOnResumeNeedsLoad = true;
			return true;
		} else {
			return false;
		}
	}

	public boolean paused() {
		return mPaused;
	}

	@Override
	public int getCurrentWorkspaceScreen() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void startBinding() {
		launcher.postRunnable(new Runnable() {

			@Override
			public void run() {
				startBindingTrue();
			}
		});
	}

	public void startBindingTrue() {
		Log.d("launcher", "startBindingTrue");
		/* 热插拔T卡的时候，有时候会加载两次，先清除掉hotseatBar上面的图 */
		{
			final HotSeat3D sideBar = root.getHotSeatBar();
			if (DefaultLayout.enable_hotseat_rolling) {
				final ViewGroup3D mainGroup = sideBar.getMainGroup();
				mainGroup.removeAllViews();
			}
			final ViewGroup3D dockGroup = sideBar.getDockGroup();
			dockGroup.removeAllViews();
		}
	}

	@Override
	public void bindItemsOnThread(final ArrayList<ItemInfo> shortcuts,
			final int start, final int end) {
		if (!R3D.hasPack(R3D.contact_name)) {
			Bitmap bmp1 = null;
			Bitmap bmp = null;
			bmp1 = ThemeManager.getInstance().getBitmap(
					"theme/iconbg/contactperson-icon.png");
			if (bmp1 != null) {
				bmp = Bitmap.createScaledBitmap(bmp1, R3D.sidebar_widget_w,
						R3D.sidebar_widget_h, true);
				R3D.pack(R3D.contact_name, Utils3D.IconToPixmap3D(bmp,
						R3D.contact_name, null, Icon3D.titleBg));
				bmp1.recycle();
				bmp.recycle();
			}
		}
		for (int i = start; i < end; i++) {
			final ItemInfo item = shortcuts.get(i);
			replaceItemInfoIcon(item);
			if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
				ShortcutInfo sInfo = (ShortcutInfo) item;
				String name = sInfo.title.toString();
				if (!name.equals(R3D.folder3D_name)) {
					R3D.pack(sInfo);
				}
			} else if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT) {
				ShortcutInfo sInfo = (ShortcutInfo) item;
				String name = sInfo.title.toString();
				if (!(sInfo.intent != null && sInfo.intent.getAction().equals(
						"com.android.contacts.action.QUICK_CONTACT"))
						&& !name.equals(R3D.folder3D_name)) {
					R3D.pack(sInfo);
				}
			} else if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER) {
				UserFolderInfo folderInfo = (UserFolderInfo) item;
				String name = folderInfo.title.toString();
				if (!name.equals(R3D.folder3D_name)) {
					R3D.pack(name,
							FolderIcon3D.titleToTexture(name, Color.WHITE));
				}
				ArrayList<ShortcutInfo> children = folderInfo.contents;
				int Count = children.size();
				for (int j = 0; j < Count; j++) {
					ShortcutInfo child = (ShortcutInfo) children.get(j);
					replaceItemInfoIcon(child);
					if (child.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
							|| child.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
						ShortcutInfo sInfo = (ShortcutInfo) child;
						if (child.intent != null
								&& child.intent
										.getAction()
										.equals("com.android.contacts.action.QUICK_CONTACT")) {
							continue;
						} else {
							R3D.pack(sInfo);
						}
					}
				}
			}
		}
	}

	@Override
	public void bindItems(final ArrayList<ItemInfo> shortcuts, final int start,
			final int end) {
		launcher.postRunnable(new Runnable() {

			@Override
			public void run() {
				Utils3D.showTimeFromStart("bindItems:");
				bindItemsTrue(shortcuts, start, end);
			}
		});
	}

	@Override
	public void bindCustomShortcutItems(final ArrayList<ItemInfo> shortcuts,
			final int start, final int end) {
		launcher.postRunnable(new Runnable() {

			@Override
			public void run() {
				View3D shortcut = null;
				ItemInfo item = null;
				for (int i = start; i < end; i++) {
					item = shortcuts.get(i);
					shortcut = CustomShortcutIcon.createCustomShortcut(item,
							true);
					if (shortcut != null) {
						workspace.addInScreen(shortcut, item.screen, item.x,
								item.y, false);
					}
				}
			}
		});
	}

	public static void replaceItemInfoIcon(ItemInfo item) {
		if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
				|| item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
			ShortcutInfo sInfo = (ShortcutInfo) item;
			int findIndex = iLoongLauncher.getInstance().equalHotSeatIntent(
					sInfo.intent);
			if (findIndex != -1) {
				/* 需要从主题中寻找替换的图标 */
				Bitmap findBmp = iLoongLauncher.getInstance()
						.findHotSeatBitmap(findIndex);
				if (findBmp != null) {
					// teapotXu_20130328 add start: 如果是HotSeat的图标，且已经更换图标，那么增加标识
					sInfo.hotseatDefaultIcon = true;
					// teapotXu_20130328 add end
					sInfo.setIcon(findBmp);
					sInfo.title = iLoongLauncher.getInstance()
							.getHotSeatString(findIndex);
					sInfo.usingFallbackIcon = false;
				}
			}
			// teapotXu deleted start:去除当拖动其他图标到底边栏时，底边栏根据类别替换成默认图标的功能
			// else {
			// if
			// (ThemeManager.getInstance().getCurrentThemeDescription().mSystem)
			// {
			// HotSeat3D hotseat = root.getHotSeatBar();
			// //teapotXu add start: restore intent infos when the replace one
			// doesn't exist in hoteat config.
			// Intent intent_bak = new Intent(sInfo.intent);
			// int item_type_bak = sInfo.itemType;
			// //teapotXu add end
			// hotseat.getDockGroup().replaceIntent(sInfo);
			// findIndex =
			// iLoongLauncher.getInstance().equalHotSeatIntent(sInfo.intent);
			// if (findIndex != -1) {
			// /* 需要从主题中寻找替换的图标 */
			// Bitmap findBmp =
			// iLoongLauncher.getInstance().findHotSeatBitmap(findIndex);
			// if (findBmp != null) {
			// //teapotXu_20130328 add start: 如果是HotSeat的图标，且已经更换图标，那么增加标识
			// sInfo.hotseatDefaultIcon = true;
			// //teapotXu_20130328 add end
			// sInfo.setIcon(findBmp);
			// sInfo.title =
			// iLoongLauncher.getInstance().getHotSeatString(findIndex);
			// sInfo.usingFallbackIcon = false;
			// }
			// }else{
			// //also not exist, restore old intent
			// //如果替换完intent之后还是不存在，说明该sInfo不在hotseat配置中，需要还原为原来的intent
			// if(intent_bak != null)
			// sInfo.intent = intent_bak;
			// sInfo.itemType = item_type_bak;
			// }
			// }
			//
			// }
			// teapotXu deleted end
		}
	}

	public void bindItemsTrue(ArrayList<ItemInfo> shortcuts, int start, int end) {
		R3D.packer.updateTextureAtlas(R3D.packerAtlas, TextureFilter.Linear,
				TextureFilter.Linear);
		for (int i = start; i < end; i++) {
			final ItemInfo item = shortcuts.get(i);
			mDesktopItems.add(item);
			workspace.bindItem(item);
			// Log.v("bind", "bindItemsTrue done");
		}
		Utils3D.showTimeFromStart("total time");
	}

	@Override
	public void bindFolders(final HashMap<Long, FolderInfo> folders) {
		launcher.postRunnable(new Runnable() {

			@Override
			public void run() {
				bindFoldersTrue(folders);
				Log.e("bind", "bindFolders done");
			}
		});
	}

	@Override
	public void bindWidgetView(final ArrayList<ShortcutInfo> arrList) {
		launcher.postRunnable(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < arrList.size(); i++) {
					Log.d("launcher", "bind widgetview:" + i);
					DefaultLayout.showDefaultWidgetView(arrList.get(i));
				}
			}
		});
	}

	public void bindFoldersTrue(final HashMap<Long, FolderInfo> folders) {
		setLoadOnResume();
		iLoongLauncher.LauncherbindFolders(folders);
	}

	public Icon3D addShortcut(final ShortcutInfo info) {
		// Icon3D icon = new
		// Icon3D(info.title.toString(),info.getIcon(iconCache),info.title.toString());
		if (workspace == null) {
			return null;
		}
		final Object obj = workspace.getCurIcon();
		if (obj == null)
			return null;
		final ItemInfo oldInfo = ((Icon3D) obj).getItemInfo();
		if (info.title == null) {
			new AlertDialog.Builder(iLoongLauncher.getInstance())
					.setTitle(R3D.getString(RR.string.group_contacts))
					.setMessage(
							R3D.getString(RR.string.contact_error_pls_check))
					.setPositiveButton(
							R3D.getString(RR.string.circle_ok_action), null)
					.show();
			return (Icon3D) obj;
		}
		if (oldInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
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
		final Bitmap bitmapRemain = info.getIcon(iLoongApplication.mIconCache)
				.copy(Bitmap.Config.ARGB_8888, true);
		final Bitmap bitmapTmp = info.getIcon(iLoongApplication.mIconCache)
				.copy(Bitmap.Config.ARGB_8888, true);
		((Icon3D) obj).setItemInfo(info);
		Root3D.addOrMoveDB(info, oldInfo.container);
		Root3D.deleteFromDB(oldInfo);
		info.setIcon(bitmapRemain);
		launcher.postRunnable(new Runnable() {

			@Override
			public void run() {
				if (bitmapTmp == null || bitmapTmp.isRecycled())
					return;
				Bitmap newIcon = bitmapTmp;
				float scaleFactor = 1f;
				if (newIcon.getWidth() != DefaultLayout.app_icon_size
						|| newIcon.getHeight() != DefaultLayout.app_icon_size) {
					scaleFactor = (float) DefaultLayout.app_icon_size
							/ newIcon.getWidth();
					if (scaleFactor > 1)
						scaleFactor = 1;
				}
				if (DefaultLayout.thirdapk_icon_scaleFactor != 1f
						&& !R3D.doNotNeedScale(null, null)) {
					scaleFactor = scaleFactor
							* DefaultLayout.thirdapk_icon_scaleFactor;
				}
				if (scaleFactor != 1f) {
					newIcon = Tools.resizeBitmap(newIcon, scaleFactor);
				}
				Texture texture = new IconToTexture3D(newIcon, info.title
						.toString(), Icon3D.getIconBg(), Icon3D.titleBg);
				TextureRegion newRegion = new TextureRegion(texture);
				if (!DefConfig.DEF_S3_SUPPORT
						&& newRegion.getRegionHeight() == R3D.workspace_cell_height) {
					if (oldInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
						float scale = (float) R3D.workspace_cell_width
								/ (float) R3D.workspace_cell_height;
						Utils3D.changeTextureRegionHeight(newRegion, scale);
					}
				}
				((View3D) obj).region = newRegion;
			}
		});
		workspace.setTag(null);
		return null;
	}

	private ArrayList<ShortcutInfo> addDropShortcutList = new ArrayList<ShortcutInfo>();

	public ArrayList<ShortcutInfo> getShortcutlist() {
		return addDropShortcutList;
	}

	public void addShortcutFromDrop(final ShortcutInfo info) {
		if (addDropShortcutList.size() > 0) {
			for (ShortcutInfo addSInfo : addDropShortcutList) {
				if (addSInfo.intent != null
						&& addSInfo.intent.toString().equals(
								info.intent.toString())) {
					Log.v("cooee",
							"Desktop3DListner ---- addShortcutFromDrop --- want to add the same shortcut :"
									+ info.intent);
					return;
				}
			}
		}
		addDropShortcutList.add(info);
		iLoongLauncher.getInstance().postRunnable(new Runnable() {

			@Override
			public void run() {
				mIsDoingAddShortCut = true;
				R3D.packer.updateTextureAtlas(R3D.packerAtlas,
						TextureFilter.Linear, TextureFilter.Linear);
				if (addDropShortcutList.size() > 0) {
					for (ShortcutInfo sInfo : addDropShortcutList) {
						int[] mCell = new int[2];
						if (sInfo.cellX >= 0 && sInfo.cellY >= 0) {
							// sunyinwei changed for just added shortcut not
							// change by theme without restart shortcut 20131031
							Icon3D icon = new Icon3D(sInfo.title.toString(),
									R3D.findRegion(sInfo, ""));
							icon.setItemInfo(sInfo);
							workspace.setTag(null);
							// root.addFlySysShortcut( icon );//带飞入动画添加
							if (workspace.addInScreen(icon, sInfo.screen,
									sInfo.x, sInfo.y, false)) {
								// Root3D.addOrMoveDB(info,
								// LauncherSettings.Favorites.CONTAINER_DESKTOP);
							}
						} else {
							int cur_srcreen = sInfo.screen;
							for (int i = 0; i < iLoongLauncher.getInstance()
									.getScreenCount(); i++) {
								if (i != cur_srcreen
										&& findCellForSpan(i, -1, -1, mCell, 1,
												1)) {
									sInfo.cellX = mCell[0];
									sInfo.cellY = mCell[1];
									sInfo.screen = i;
									// sunyinwei changed for just added shortcut
									// not change by theme without restart
									// shortcut 20131031
									Icon3D icon = new Icon3D(sInfo.title
											.toString(), R3D.findRegion(sInfo,
											""));
									icon.setItemInfo(sInfo);
									workspace.setTag(null);
									root.addFlySysShortcut(icon);
									// if( workspace.addInScreen( icon ,
									// sInfo.screen , sInfo.x , sInfo.y , false
									// ) )
									// {
									// // Root3D.addOrMoveDB(info,
									// //
									// LauncherSettings.Favorites.CONTAINER_DESKTOP);
									// }
									break;
								}
							}
						}
					}
				}
				addDropShortcutList.clear();
				mIsDoingAddShortCut = false;
			}
		});
		Gdx.graphics.requestRendering();
		return;
	}

	public Widget addAppWidget(Widget2DInfo item) {
		Widget widget2D = new Widget("widget_" + item.appWidgetId, item);
		workspace.setTag(null);
		if (workspace.addInScreen(widget2D, item.screen, item.x, item.y, false))
			return widget2D;
		return null;
	}

	@Override
	public void bindAppWidget(final Widget2DInfo item) {
		Widget widget = addAppWidget(item);
		if (widget == null)
			return;
		Widget2DInfo info = (Widget2DInfo) widget.getItemInfo();
		iLoongLauncher.getInstance().addAppWidget(info, true);
		info.hostView.setWidget(widget);
	}

	@Override
	public void bindAppsAdded(final ArrayList<ApplicationInfo> apps) {
		if (!bCreatDone) {
			Log.e("launcher", "bindAppsAdded but not CreatDone!!!!!!");
			return;
		}
		launcher.postRunnable(new Runnable() {

			@Override
			public void run() {
				Log.v("launcher", "bindAppsAdded");
				boolean hasNewTexture = false;
				Iterator<ApplicationInfo> ite = apps.iterator();
				boolean isHideApp = false;
				while (ite.hasNext()) {
					ApplicationInfo info = ite.next();
					// teapotXu_20130305: add start
					// Widget icon will not be shown in Launcher
					if (bAppDone) {
						// xiatian add start //fix bug:0001881
						if (HideThemesInAppList(info.packageName)) {
							ite.remove();// xiatian add //fix bug:0001918
							continue;
						}
						// xiatian add end
						if (IsInstalledCooeeWidgets(
								iLoongLauncher.getInstance(), info.packageName)) {
							ite.remove();
							continue;
						}
					}
					// teapotXu_20130305: add end
					for (int j = 0; j < DefaultLayout.hideAppList.size(); j++) {
						String compName = DefaultLayout.hideAppList.get(j);
						if ((info.intent.getComponent() != null)
								&& info.intent.getComponent().toString()
										.contains(compName)) {
							isHideApp = true;
							ite.remove();
							// Log.v("hideApp", "removed1: "+compName);
							break;
						} else {
							isHideApp = false;
						}
					}
					if (isHideApp) {
						// Log.v("hideApp", "removed continue");
						continue;
					}
					ShortcutInfo sInfo = info.makeShortcut();
					hasNewTexture |= R3D.pack(sInfo);
					// Log.v("icon",sInfo.title +" :" +
					// sInfo.intent.toString());
				}
				R3D.packer.updateTextureAtlas(R3D.packerAtlas,
						TextureFilter.Linear, TextureFilter.Linear);
				appHost.addApps(apps);
				// teapotXu add start
				if (DefaultLayout.enable_workspace_miui_edit_mode) {
					if (widgetHost != null)
						widgetHost.setInited();
				}
				// teapotXu add end
				if (bAppDone) {
					for (int i = 0; i < apps.size(); i++) {
						ApplicationInfo info = apps.get(i);
						DefaultLayout.onAddApp(info);
					}
				}
				synchronized (LauncherModel.lock_allapp) {
					LauncherModel.waitBindApp = false;
					LauncherModel.lock_allapp.notify();
				}
				if (Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode
						&& DesktopEditHost.getInstance() != null
						&& DesktopEditHost.getInstance().menuContainer != null) {
					DesktopEditHost.getInstance().menuContainer
							.onFreshPage(MenuContainer.APPPAGE);
				}
			}
		});
	}

	@Override
	public void bindAllWidgets(final ArrayList<Object> widgets) {
		if (!bCreatDone) {
			Log.e("launcher", "bindAppsRemoved but not CreatDone!!!!!!");
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (Desktop3DListener.this) {
					bindwidgetnum++;
					final long time = System.currentTimeMillis();
					allWidgetInfo.clear();
					for (int i = 0; i < widgets.size(); i++) {
						WidgetShortcutInfo widgetInfo = new WidgetShortcutInfo();
						Object rawInfo = widgets.get(i);
						Bitmap b = null;
						String name = "";
						String label = "";
						if (rawInfo instanceof AppWidgetProviderInfo) {
							AppWidgetProviderInfo info = (AppWidgetProviderInfo) rawInfo;
							if (info.provider == null) {
								continue;
							}
							Log.v("jbc",
									"sysWidgetName=" + info.label
											+ " packageName="
											+ info.provider.getPackageName()
											+ " providerName="
											+ info.provider.getClassName());
							int[] cellSpans = launcher.getSpanForWidget(info,
									null);
							/* 在小组件界面，屏蔽spanX或spanY大于4的小组件 */
							if (cellSpans[0] > 4 || cellSpans[1] > 4) {
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
							boolean should_hide = false;
							for (int j = 0; j < DefaultLayout.hideWidgetList
									.size(); j++) {
								String compName = DefaultLayout.hideWidgetList
										.get(j);
								if (info.provider.toString().contains(compName)) {
									should_hide = true;
									break;
								}
							}
							if (should_hide) {
								continue;
							}
							int previewImage = 0;
							int sysVersion = Integer.parseInt(VERSION.SDK);
							if (sysVersion >= 11)
								previewImage = info.previewImage;
							// b = getWidgetPreview( info.provider ,
							// previewImage , info.icon , cellSpans[0] ,
							// cellSpans[1] , -1 , -1 );
							name = info.provider.toString();
							if (!widget2DpkgName.contains(name)) {
								b = getWidgetPreview(info.provider,
										previewImage, info.icon, cellSpans[0],
										cellSpans[1], -1, -1);
								widget2DpkgName.add(name);
							}
							label = info.label;
							widgetInfo.cellHSpan = cellSpans[0];
							widgetInfo.cellVSpan = cellSpans[1];
							widgetInfo.label = label;
							widgetInfo.component = info.provider;
							widgetInfo.isWidget = true;
							if (true == DefaultLayout.enable_workspace_miui_edit_mode) {
								String name_ex = info.provider.toString()
										+ "_editMode";
								if (!widget2DpkgName.contains(name_ex)) {
									Bitmap widgetHost_bmp = getWidgetPreviewWorkspaceEditMode(
											info.provider, previewImage,
											info.icon, cellSpans[0],
											cellSpans[1], -1, -1);
									if (widgetHost_bmp != null) {
										widget2DpkgName.add(name_ex);
										allwidget2Dbitmap.add(widgetHost_bmp);
										widgetInfo.widgetHostBitmap = widgetHost_bmp;
										Log.v("hjwmiui",
												"widgetHostBitmap is new  label is "
														+ widgetInfo.label);
									}
								}
							}
						} else if (rawInfo instanceof ResolveInfo) {
							ResolveInfo info = (ResolveInfo) rawInfo;
							if (info.activityInfo == null
									|| info.activityInfo.name == null) {
								continue;
							}
							name = info.activityInfo.name;
							if (!widget2DpkgName.contains(name)) {
								b = getShortcutPreview(info);
								widget2DpkgName.add(name);
							}
							label = iconCache.getLabel(info);
							widgetInfo.label = label;
							widgetInfo.component = new ComponentName(
									info.activityInfo.packageName,
									info.activityInfo.name);
							widgetInfo.isShortcut = true;
						}
						if (b != null) {
							widgetInfo.widget2DBitmap = b;
							allwidget2Dbitmap.add(b);
							Log.v("hjwapplist",
									"widget2DBitmap is new  label is "
											+ widgetInfo.label);
							Log.v("", "whjupdatepack AppWidget pack");
						}
						Log.v("fuck", "xxxxxxxx");
						widgetInfo.textureName = name;
						widgetInfo.isHide = PreferenceManager
								.getDefaultSharedPreferences(
										iLoongLauncher.getInstance())
								.getBoolean("HIDE:" + widgetInfo.textureName,
										false);
						allWidgetInfo.add(widgetInfo);
					}
					launcher.postRunnable(new Runnable() {

						@Override
						public void run() {
							bindwidgetnum--;
							if (bindwidgetnum == 0) {
								allWidgetInfo.clear();
							}
							Log.v("launcher",
									"bindAllWidgets done:"
											+ (System.currentTimeMillis() - time));
							Log.e("load", "finish widget");
							if (!bWidgetDone) {
								bWidgetDone = true;
								checkLoadProgress();
								AppList3D.allInit = true;
								DefaultLayout.getInstance()
										.cancelProgressDialog();
							}
						}
					});
				}
			}
		}).start();
	}

	public void bindAppListFolders(final ArrayList<FolderInfo> folders) {
		if (!R3D.hasPack(R3D.contact_name)) {
			Bitmap bmp1 = null;
			Bitmap bmp = null;
			bmp1 = ThemeManager.getInstance().getBitmap(
					"theme/iconbg/contactperson-icon.png");
			if (bmp1 != null) {
				bmp = Bitmap.createScaledBitmap(bmp1, R3D.sidebar_widget_w,
						R3D.sidebar_widget_h, true);
				R3D.pack(R3D.contact_name, Utils3D.IconToPixmap3D(bmp,
						R3D.contact_name, null, Icon3D.titleBg));
				bmp1.recycle();
				bmp.recycle();
			}
		}
		launcher.postRunnable(new Runnable() {

			public void run() {
				final int N = folders.size();
				for (int i = 0; i < N; i++) {
					FolderInfo info = folders.get(i);
					UserFolderInfo folderInfo = (UserFolderInfo) info;
					String name = null;
					if (folderInfo.title != null) {
						name = folderInfo.title.toString();
					}
					// Jone modify for umeng-exception
					if (name != null && !name.equals(R3D.folder3D_name)) {
						R3D.pack(name,
								FolderIcon3D.titleToTexture(name, Color.WHITE));
					}
					ArrayList<ShortcutInfo> children = folderInfo.contents;
					int Count = children.size();
					for (int j = 0; j < Count; j++) {
						ShortcutInfo child = (ShortcutInfo) children.get(j);
						if (child.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
								|| child.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
							ShortcutInfo sInfo = (ShortcutInfo) child;
							if (child.intent != null
									&& "com.android.contacts.action.QUICK_CONTACT"
											.equals(child.intent.getAction())) {
								continue;
							} else {
								R3D.pack(sInfo);
							}
						}
					}
				}
				if (R3D.packer != null) {
					R3D.packer.updateTextureAtlas(R3D.packerAtlas, R3D.filter,
							R3D.Magfilter);
				}
				if (appHost != null) {
					appHost.setFolders(folders);
				}
				synchronized (LauncherModel.lock_allapp) {
					LauncherModel.waitBindApp = false;
					LauncherModel.lock_allapp.notify();
				}
				if (DefaultLayout.needToSaveSpecifiedIconXml) {
					Log.v("BootTrack", "BootTrack22222");
					// DefaultLayout.getInstance().cancelProgressDialog();
				}
			}
		});
	}

	public void bindAppListFoldersAdded(final ArrayList<FolderInfo> folders) {
		if (!R3D.hasPack(R3D.contact_name)) {
			Bitmap bmp1 = ThemeManager.getInstance().getBitmap(
					"theme/iconbg/contactperson-icon.png");
			if (bmp1 != null) {
				Bitmap bmp = Bitmap.createScaledBitmap(bmp1,
						R3D.sidebar_widget_w, R3D.sidebar_widget_h, true);
				R3D.pack(R3D.contact_name, Utils3D.IconToPixmap3D(bmp,
						R3D.contact_name, null, Icon3D.titleBg));
				bmp1.recycle();
				bmp.recycle();
			}
		}
		launcher.postRunnable(new Runnable() {

			public void run() {
				final int N = folders.size();
				for (int i = 0; i < N; i++) {
					FolderInfo info = folders.get(i);
					UserFolderInfo folderInfo = (UserFolderInfo) info;
					String name = null;
					if (folderInfo.title != null) {
						name = folderInfo.title.toString();
					}
					if (name != null && !name.equals(R3D.folder3D_name)) {
						R3D.pack(name,
								FolderIcon3D.titleToTexture(name, Color.WHITE));
					}
					ArrayList<ShortcutInfo> children = folderInfo.contents;
					int Count = children.size();
					for (int j = 0; j < Count; j++) {
						ShortcutInfo child = (ShortcutInfo) children.get(j);
						if (child.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
								|| child.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
							ShortcutInfo sInfo = (ShortcutInfo) child;
							if (child.intent != null
									&& "com.android.contacts.action.QUICK_CONTACT"
											.equals(child.intent.getAction())) {
								continue;
							} else {
								R3D.pack(sInfo);
							}
						}
					}
				}
				if (R3D.packer != null) {
					R3D.packer.updateTextureAtlas(R3D.packerAtlas, R3D.filter,
							R3D.Magfilter);
				}
				if (appHost != null) {
					appHost.addFolders(folders);
				}
				synchronized (LauncherModel.lock_allapp) {
					LauncherModel.waitBindApp = false;
					LauncherModel.lock_allapp.notify();
				}
			}
		});
	}

	@Override
	public void bindAllApplications(final ArrayList<ApplicationInfo> apps) {
		if (!R3D.hasPack(R3D.contact_name)) {
			Bitmap bmp1 = ThemeManager.getInstance().getBitmap(
					"theme/iconbg/contactperson-icon.png");
			if (bmp1 != null) {
				Bitmap bmp = Bitmap.createScaledBitmap(bmp1,
						R3D.sidebar_widget_w, R3D.sidebar_widget_h, true);
				R3D.pack(R3D.contact_name, Utils3D.IconToPixmap3D(bmp,
						R3D.contact_name, null, Icon3D.titleBg));
				bmp1.recycle();
				bmp.recycle();
			}
		}
		final int N = apps.size();
		launcher.postRunnable(new Runnable() {

			public void run() {
				CellLayout3D.canShowReminder = true;
				for (int j = 0; j < N; j++) {
					ApplicationInfo info = apps.get(j);
					ShortcutInfo sInfo = info.makeShortcut();
					R3D.pack(sInfo);
				}
				R3D.packer.updateTextureAtlas(R3D.packerAtlas, R3D.filter,
						R3D.Magfilter);
				appHost.setApps(apps);
				// teapotXu add start
				if (DefaultLayout.enable_workspace_miui_edit_mode) {
					if (widgetHost != null)
						widgetHost.setInited();
				}
				// teapotXu add end
				synchronized (LauncherModel.lock_allapp) {
					LauncherModel.waitBindApp = false;
					LauncherModel.lock_allapp.notify();
				}
				if (DefaultLayout.needToSaveSpecifiedIconXml) {
					Log.v("BootTrack", "BootTrack11111");
					// DefaultLayout.getInstance().cancelProgressDialog();
				}
			}
		});
	}

	public void finishBindApplications() {
		if (bAppDone) {
			return;
		}
		bAppDone = true;
		if (appHost != null && appHost.appList != null)
			appHost.appList.finishBind();
		checkLoadProgress();
		if (DefaultLayout.enable_release_2Dwidget
				&& DefaultLayout.enable_scroll_to_widget
				&& !AppList3D.hasbind2Dwidget && iLoongApplication.BuiltIn) {
			if (appHost != null && appHost.appList != null) {
				appHost.appList.bind2DWidget();
			}
		}
		if (m_applicationListHost != null && m_applicationListHost.isVisible()) {
			m_applicationListHost.forceSyncAppsPage();
		}
	}

	private void checkLoadProgress() {
		if (DefaultLayout.cancel_dialog_last) {
			if (initDone()) {
				iLoongLauncher.getInstance().cancelProgressDialog();
				iLoongLauncher.getInstance().finishLoad();
			}
			return;
		} else if (DefaultLayout.loadapp_in_background) {
			if (bDesktopDone)
				iLoongLauncher.getInstance().cancelProgressDialog();
		} else {
			if ((!iLoongApplication.BuiltIn || DefaultLayout.hide_mainmenu_widget)) {
				if (bAppDone)
					iLoongLauncher.getInstance().cancelProgressDialog();
			} else if (bWidgetDone)
				iLoongLauncher.getInstance().cancelProgressDialog();
		}
		if (initDone()) {
			iLoongLauncher.getInstance().finishLoad();
		}
	}

	void updateDockbarShortcuts(ArrayList<ApplicationInfo> apps) {
		IconCache mIconCache = ((iLoongApplication) launcher.getApplication())
				.getIconCache();
		final PackageManager pm = launcher.getPackageManager();
		final ViewGroup3D sideMainGroup = root.getHotSeatBar().getDockGroup();
		final int count = sideMainGroup.getChildCount();
		for (int i = 0; i < count; i++) {
			View3D mView3D = sideMainGroup.getChildAt(i);
			if (!(mView3D instanceof ViewGroup3D)) {
				continue;
			}
			final ViewGroup3D layout = (ViewGroup3D) mView3D;
			int childCount = layout.getChildCount();
			for (int j = 0; j < childCount; j++) {
				View3D viewtmp = layout.getChildAt(j);
				if (!(viewtmp instanceof IconBase3D))
					continue;
				final IconBase3D view = (IconBase3D) viewtmp;
				Object tag = view.getItemInfo();
				if (tag instanceof ShortcutInfo) {
					ShortcutInfo info = (ShortcutInfo) tag;
					// We need to check for ACTION_MAIN otherwise getComponent()
					// might
					// return null for some shortcuts (for instance, for
					// shortcuts to
					// web pages.)
					final Intent intent = info.intent;
					final ComponentName name = intent.getComponent();
					if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION
							&& Intent.ACTION_MAIN.equals(intent.getAction())
							&& name != null) {
						final int appCount = apps.size();
						for (int k = 0; k < appCount; k++) {
							ApplicationInfo app = apps.get(k);
							if (app.componentName.equals(name)) {
								ShortcutInfo sInfo = app.makeShortcut();
								((View3D) view).region = new TextureRegion(
										R3D.findRegion(sInfo));
							}
						}
					}
				} else if (tag instanceof UserFolderInfo) {
					updateFolder((FolderIcon3D) view, apps);
				}
			}
		}
	}

	@Override
	public void bindAppsUpdated(final ArrayList<ApplicationInfo> apps) {
		if (!bCreatDone) {
			Log.e("launcher", "bindAppsUpdated but not CreatDone!!!!!!");
			return;
		}
		launcher.postRunnable(new Runnable() {

			@Override
			public void run() {
				Log.v("pack", "bindAppsUpdated");
				for (int i = 0; i < apps.size(); i++) {
					ApplicationInfo info = apps.get(i);
					ShortcutInfo sInfo = info.makeShortcut();
					R3D.pack(sInfo);
				}
				R3D.packer.updateTextureAtlas(R3D.packerAtlas,
						TextureFilter.Linear, TextureFilter.Linear);
				appHost.updateApps(apps);
				updateWorkapceShortcuts(apps);
			}
		});
	}

	void removeDockbarItems(final ArrayList<ApplicationInfo> apps) {
		final HotSeat3D sideBar = root.getHotSeatBar();
		final Context context = launcher.getApplicationContext();
		final ViewGroup3D mainGroup = sideBar.getDockGroup();
		final int count = mainGroup.getChildCount();
		final PackageManager manager = context.getPackageManager();
		final AppWidgetManager widgets = AppWidgetManager.getInstance(context);
		final HashSet<String> packageNames = new HashSet<String>();
		final int appCount = apps.size();
		for (int i = 0; i < appCount; i++) {
			packageNames.add(apps.get(i).componentName.getPackageName());
		}
		for (int i = 0; i < count; i++) {
			View3D mView3D = mainGroup.getChildAt(i);
			if (!(mView3D instanceof ViewGroup3D)) {
				continue;
			}
			final ViewGroup3D layout = (ViewGroup3D) mView3D;
			// Avoid ANRs by treating each screen separately
			launcher.postRunnable(new Runnable() {

				public void run() {
					final ArrayList<IconBase3D> childrenToRemove = new ArrayList<IconBase3D>();
					childrenToRemove.clear();
					int childCount = layout.getChildCount();
					for (int j = 0; j < childCount; j++) {
						View3D viewtmp = layout.getChildAt(j);
						if (!(viewtmp instanceof IconBase3D))
							continue;
						final IconBase3D view = (IconBase3D) viewtmp;
						Object tag = view.getItemInfo();
						if (tag instanceof ShortcutInfo) {
							final ShortcutInfo info = (ShortcutInfo) tag;
							final Intent intent = info.intent;
							final ComponentName name = intent.getComponent();
							if (Intent.ACTION_MAIN.equals(intent.getAction())
									&& name != null) {
								for (String packageName : packageNames) {
									if (packageName.equals(name
											.getPackageName())) {
										// LauncherModel.deleteItemFromDatabase(mLauncher,
										// info);
										Root3D.deleteFromDB(info);
										childrenToRemove.add(view);
									}
								}
							}
						} else if (tag instanceof UserFolderInfo) {
							removeFolderItems((FolderIcon3D) view, apps);
						}
					}
					childCount = childrenToRemove.size();
					for (int j = 0; j < childCount; j++) {
						View3D child = (View3D) childrenToRemove.get(j);
						child.remove();
						// layout.removeViewInLayout(child);
						if (child instanceof DropTarget3D) {
							dragLayer.removeDropTarget((DropTarget3D) child);
						}
					}
				}
			});
		}
	}

	public void removePageEditItems(final ArrayList<ApplicationInfo> apps) {
		final Context context = launcher.getApplicationContext();
		final int count = pageContainer.celllayoutList.size();
		final AppWidgetManager widgets = AppWidgetManager.getInstance(context);
		final HashSet<String> packageNames = new HashSet<String>();
		final int appCount = apps.size();
		for (int i = 0; i < appCount; i++) {
			packageNames.add(apps.get(i).componentName.getPackageName());
		}
		for (int i = 0; i < count; i++) {
			View3D viewTmp = pageContainer.celllayoutList.get(i);
			if (!(viewTmp instanceof CellLayout3D))
				continue;
			final CellLayout3D layout = (CellLayout3D) viewTmp;
			final ArrayList<IconBase3D> childrenToRemove = new ArrayList<IconBase3D>();
			childrenToRemove.clear();
			int childCount = layout.getChildCount();
			for (int j = 0; j < childCount; j++) {
				final IconBase3D view = (IconBase3D) layout.getChildAt(j);
				Object tag = view.getItemInfo();
				if (tag instanceof ShortcutInfo) {
					final ShortcutInfo info = (ShortcutInfo) tag;
					final Intent intent = info.intent;
					final ComponentName name = intent.getComponent();
					if (Intent.ACTION_MAIN.equals(intent.getAction())
							&& name != null) {
						for (String packageName : packageNames) {
							if (packageName.equals(name.getPackageName())) {
								// LauncherModel.deleteItemFromDatabase(mLauncher,
								// info);
								Root3D.deleteFromDB(info);
								childrenToRemove.add(view);
							}
						}
					}
				} else if (tag instanceof UserFolderInfo) {
					removeFolderItems((FolderIcon3D) view, apps);
				} else if (tag instanceof Widget2DInfo) {
					final Widget2DInfo info = (Widget2DInfo) tag;
					for (String packageName : packageNames) {
						if (packageName.equals(info.getPackageName())) {
							Root3D.deleteFromDB(info);
							// LauncherModel.deleteItemFromDatabase(mLauncher,
							// info);
							childrenToRemove.add(view);
							SendMsgToAndroid.deleteSysWidget((Widget) view);
						}
					}
				}
			}
			childCount = childrenToRemove.size();
			for (int j = 0; j < childCount; j++) {
				View3D child = (View3D) childrenToRemove.get(j);
				child.remove();
				// layout.removeViewInLayout(child);
				if (child instanceof DropTarget3D) {
					dragLayer.removeDropTarget((DropTarget3D) child);
				}
			}
		}
	}

	@Override
	public void bindAppsRemoved(final ArrayList<ApplicationInfo> apps,
			final boolean permanent) {
		if (!bCreatDone) {
			Log.e("launcher", "bindAppsRemoved but not CreatDone!!!!!!");
			return;
		}
		Log.v("launcher", "bindAppsRemoved");
		launcher.postRunnable(new Runnable() {

			@Override
			public void run() {
				if (permanent) {
					if (root != null && root.isPageContainerVisible() == false) {
						removeWorkspaceItems(apps);
					} else {
						removePageEditItems(apps);
					}
					removeDockbarItems(apps);
					removeFolderItems(getOpenFolder(), apps);
				}
				if (appHost != null) {
					appHost.reomveApps(apps, permanent);
				}
				Log.v("bind", "bindAppsRemoved done");
				if (DefaultLayout.enable_quick_search) {
					if (Desktop3DListener.root.qSearchGroup != null
							&& QSearchGroup.mLoaded) {
						Desktop3DListener.root.qSearchGroup.reLoad();
					}
				}
				if (Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode
						&& DesktopEditHost.getInstance() != null
						&& DesktopEditHost.getInstance().menuContainer != null) {
					DesktopEditHost.getInstance().menuContainer
							.onFreshPage(MenuContainer.APPPAGE);
				}
			}
		});
	}

	@Override
	public boolean isAllAppsVisible() {
		if (appHost == null)
			return false;
		return appHost.isVisible();
	}

	@Override
	public void bindWidget3D(final Widget3DInfo item) {
		launcher.postRunnable(new Runnable() {

			@Override
			public void run() {
				Utils3D.showTimeFromStart("load 3dwidget 1:" + item.packageName);
				Widget3D widget3D = null;
				boolean find = false;
				boolean addworkapce = true;
				WidgetItem widgetItem = null;
				for (int i = 0; i < DefaultLayout.allWidgetFinal.size(); i++) {
					if (DefaultLayout.allWidgetFinal.get(i).pkgName
							.equals(item.packageName)) {
						widgetItem = DefaultLayout.allWidgetFinal.get(i);
						find = true;
						break;
					}
				}
				if (find && widgetItem.loadByInternal) {
					Utils3D.showTimeFromStart("load 3dwidget 2:"
							+ item.packageName);
					widget3D = Widget3DManager.getInstance().getWidget3D(
							widgetItem.pkgName, widgetItem.className);
					if (widget3D != null) {
						widget3D.setItemInfo(item);
						if (!widgetItem.addDesktop) {
							addworkapce = false;
						}
					} else {
						Log.d("AppHost3D", "不死啦");
					}
				} else if (mWidget3DManagerInstance != null && addworkapce) {
					widget3D = mWidget3DManagerInstance.getWidget3D(item);
				}
				Utils3D.showTimeFromStart("load 3dwidget 3:" + item.packageName);
				if (widget3D != null && workspace != null) {
					workspace.addInScreen(widget3D, item.screen, item.x,
							item.y, false);
				}
				Utils3D.showTimeFromStart("load 3dwidget 4:" + item.packageName);
			}
		});
	}

	public void addWidget3DToScreen(View3D widget3D, int x, int y) {
		this.workspace.addInCurrenScreen(widget3D, x, y, false);
	}

	@Override
	public void bindSidebarItems(final ArrayList<ItemInfo> info) {
		launcher.postRunnable(new Runnable() {

			@Override
			public void run() {
				int count = info.size();
				for (int i = 0; i < count; i++) {
					final ItemInfo item = info.get(i);
					replaceItemInfoIcon(item);
					if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
							|| item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
						ShortcutInfo sInfo = (ShortcutInfo) item;
						String name = sInfo.title.toString();
						if (!name.equals(R3D.folder3D_name)) {
							if (R3D.icon_bg_num > 0) {
								boolean needAddBg = false;
								if (sInfo.intent != null
										&& sInfo.intent.getComponent() != null
										&& sInfo.intent.getComponent()
												.getPackageName() != null) {
									String pkgname = sInfo.intent
											.getComponent().getPackageName();
									String clsname = sInfo.intent
											.getComponent().getClassName();
									String iconPath = DefaultLayout
											.getInstance().getReplaceIconPath(
													pkgname, clsname);
									if (iconPath != null
											&& null != ThemeManager
													.getInstance()
													.getCurrentThemeBitmap(
															iconPath)) {
										needAddBg = false;
									} else {
										needAddBg = true;
									}
								}
								if (DefaultLayout.app_icon_size > (sInfo
										.getIcon(iLoongApplication.mIconCache)
										.getHeight())
										|| needAddBg) {
									if (sInfo.intent != null
											&& sInfo.intent
													.getAction()
													.equals("com.android.contacts.action.QUICK_CONTACT")) {
										continue;
									} else {
										R3D.packHotseat(sInfo, true);
									}
								} else {
									if (sInfo.intent != null
											&& sInfo.intent
													.getAction()
													.equals("com.android.contacts.action.QUICK_CONTACT")) {
										continue;
									} else {
										R3D.packHotseat(sInfo, false);
									}
								}
							} else {
								if (sInfo.intent != null
										&& sInfo.intent
												.getAction()
												.equals("com.android.contacts.action.QUICK_CONTACT")) {
									continue;
								} else {
									R3D.packHotseat(sInfo, false);
								}
							}
						}
					}
					if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER) {
						UserFolderInfo folderInfo = (UserFolderInfo) item;
						String name = folderInfo.title.toString();
						if (!name.equals(R3D.folder3D_name)) {
							R3D.pack(name, FolderIcon3D.titleToTexture(name,
									Color.WHITE));
						}
						ArrayList<ShortcutInfo> children = folderInfo.contents;
						int Count = children.size();
						for (int j = 0; j < Count; j++) {
							ItemInfo child = (ItemInfo) children.get(j);
							replaceItemInfoIcon(child);
							if (child.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
									|| child.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
								ShortcutInfo sInfo = (ShortcutInfo) child;
								R3D.pack(sInfo);
								Log.v("icon",
										sInfo.title + " :"
												+ sInfo.intent.toString());
							}
						}
					}
				}
				R3D.packer.updateTextureAtlas(R3D.packerAtlas,
						TextureFilter.Linear, TextureFilter.Linear);
				HotSeat3D sidebar = root.getHotSeatBar();
				sidebar.bindItems(info);
			}
		});
	}

	@Override
	public void afterBindSidebarItems() {
		launcher.postRunnable(new Runnable() {

			@Override
			public void run() {
				synchronized (this) {
					if (hasCreate) {
						clearWorkspace();
					} else {
						hasCreate = true;
						create2();
						clearWorkspace();
					}
					setLoadOnResume();
					if (LauncherModel.waitD3dInit) {
						LauncherModel.waitD3dInit = false;
						synchronized (init_lock) {
							init_lock.notify();
						}
					}
				}
			}
		});
	}

	public void clearWorkspace() {
		int count = workspace.getChildCount();
		for (int i = 0; i < count; i++) {
			View3D view = workspace.getChildAt(i);
			if (view instanceof CellLayout3D) {
				CellLayout3D cellLayout = (CellLayout3D) view;
				if (cellLayout.getChildCount() > 0) {
					Log.d("launcher", "break");
				}
				for (int j = 0; j < cellLayout.getChildCount(); j++) {
					View3D child = cellLayout.getChildAt(j);
					if (child instanceof Widget3D) {
						Widget3D widget3D = (Widget3D) child;
						Widget3DManager.getInstance().deleteWidget3D(widget3D);
					}
					if (child instanceof DropTarget3D && dragLayer != null) {
						dragLayer.removeDropTarget((DropTarget3D) child);
					}
				}
				cellLayout.removeAllViews();
			}
		}
	}

	public void removeWorkspaceItems(final ArrayList<ApplicationInfo> apps) {
		final Context context = launcher.getApplicationContext();
		final int count = workspace.getChildCount();
		final AppWidgetManager widgets = AppWidgetManager.getInstance(context);
		final HashSet<String> packageNames = new HashSet<String>();
		final int appCount = apps.size();
		for (int i = 0; i < appCount; i++) {
			packageNames.add(apps.get(i).componentName.getPackageName());
		}
		for (int i = 0; i < count; i++) {
			View3D viewTmp = workspace.getChildAt(i);
			if (!(viewTmp instanceof CellLayout3D))
				continue;
			final CellLayout3D layout = (CellLayout3D) viewTmp;
			// Avoid ANRs by treating each screen separately
			// launcher.postRunnable(new Runnable() {
			// public void run() {
			final ArrayList<IconBase3D> childrenToRemove = new ArrayList<IconBase3D>();
			childrenToRemove.clear();
			Log.v("launcher", "exe remove app");
			int childCount = layout.getChildCount();
			for (int j = 0; j < childCount; j++) {
				final IconBase3D view = (IconBase3D) layout.getChildAt(j);
				Object tag = view.getItemInfo();
				if (tag instanceof ShortcutInfo) {
					final ShortcutInfo info = (ShortcutInfo) tag;
					final Intent intent = info.intent;
					if (intent == null) {
						continue;
					}
					final ComponentName name = intent.getComponent();
					if (Intent.ACTION_MAIN.equals(intent.getAction())
							&& name != null) {
						for (String packageName : packageNames) {
							if (packageName.equals(name.getPackageName())) {
								// LauncherModel.deleteItemFromDatabase(mLauncher,
								// info);
								Root3D.deleteFromDB(info);
								childrenToRemove.add(view);
							}
						}
					}
				} else if (tag instanceof UserFolderInfo) {
					removeFolderItems((FolderIcon3D) view, apps);
				} else if (tag instanceof Widget2DInfo) {
					final Widget2DInfo info = (Widget2DInfo) tag;
					for (String packageName : packageNames) {
						if (packageName.equals(info.getPackageName())) {
							Root3D.deleteFromDB(info);
							// LauncherModel.deleteItemFromDatabase(mLauncher,
							// info);
							childrenToRemove.add(view);
							SendMsgToAndroid.deleteSysWidget((Widget) view);
						}
					}
				}
			}
			childCount = childrenToRemove.size();
			for (int j = 0; j < childCount; j++) {
				View3D child = (View3D) childrenToRemove.get(j);
				child.remove();
				// layout.removeViewInLayout(child);
				if (child instanceof DropTarget3D) {
					dragLayer.removeDropTarget((DropTarget3D) child);
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

	void updateWorkapceShortcuts(ArrayList<ApplicationInfo> apps) {
		IconCache mIconCache = ((iLoongApplication) launcher.getApplication())
				.getIconCache();
		final PackageManager pm = launcher.getPackageManager();
		final int count = workspace.getChildCount();
		for (int i = 0; i < count; i++) {
			View3D viewTmp = workspace.getChildAt(i);
			if (!(viewTmp instanceof CellLayout3D))
				continue;
			final CellLayout3D layout = (CellLayout3D) viewTmp;
			int childCount = layout.getChildCount();
			for (int j = 0; j < childCount; j++) {
				final IconBase3D view = (IconBase3D) layout.getChildAt(j);
				Object tag = view.getItemInfo();
				if (tag instanceof ShortcutInfo) {
					ShortcutInfo info = (ShortcutInfo) tag;
					// We need to check for ACTION_MAIN otherwise getComponent()
					// might
					// return null for some shortcuts (for instance, for
					// shortcuts to
					// web pages.)
					final Intent intent = info.intent;
					ComponentName name = null;
					if (intent != null)
						name = intent.getComponent();
					if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION
							&& Intent.ACTION_MAIN.equals(intent.getAction())
							&& name != null) {
						final int appCount = apps.size();
						for (int k = 0; k < appCount; k++) {
							ApplicationInfo app = apps.get(k);
							if (app.componentName.equals(name)) {
								Log.v("update",
										" workspace:" + app.title.toString());
								ShortcutInfo sInfo = app.makeShortcut();
								((View3D) view).region = R3D.findRegion(sInfo);
								//
							}
						}
					}
				} else if (tag instanceof UserFolderInfo) {
					updateFolder((FolderIcon3D) view, apps);
				}
			}
		}
	}

	private void updateFolder(FolderIcon3D folder,
			ArrayList<ApplicationInfo> apps) {
		// 这里有报空指针错误！！！！！
		if (folder == null)
			return;
		final FolderIcon3D myFolder = folder;
		final UserFolderInfo info = (UserFolderInfo) myFolder.getItemInfo();
		final ArrayList<ShortcutInfo> contents = info.contents;
		final ArrayList<ShortcutInfo> toUpdate = new ArrayList<ShortcutInfo>(1);
		final int contentsCount = contents.size();
		final int appCount = apps.size();
		for (int m = 0; m < appCount; m++) {
			ApplicationInfo app = apps.get(m);
			for (int k = 0; k < contentsCount; k++) {
				final ShortcutInfo appInfo = contents.get(k);
				final Intent intent = appInfo.intent;
				final ComponentName name = intent.getComponent();
				if (app.componentName.equals(name)) {
					Log.v("update", " folder item:" + name.toString());
					// ShortcutInfo sInfo = app.makeShortcut();
					// ((View3D) view).region = R3D.findRegion(sInfo);
				}
			}
		}
	}

	void removeFolderItems(FolderIcon3D folder, ArrayList<ApplicationInfo> apps) {
		if (folder == null)
			return;
		Log.v("update", " folder:" + folder.getItemInfo());
		final HashSet<String> packageNames = new HashSet<String>();
		final int appCount = apps.size();
		for (int i = 0; i < appCount; i++) {
			packageNames.add(apps.get(i).componentName.getPackageName());
		}
		final UserFolderInfo info = (UserFolderInfo) folder.getItemInfo();
		final ArrayList<ShortcutInfo> contents = info.contents;
		final ArrayList<ShortcutInfo> toRemove = new ArrayList<ShortcutInfo>(1);
		final int contentsCount = contents.size();
		boolean removedFromFolder = false;
		for (int k = contentsCount - 1; k >= 0; k--) {
			final ShortcutInfo appInfo = contents.get(k);
			final Intent intent = appInfo.intent;
			final ComponentName name = intent.getComponent();
			if (Intent.ACTION_MAIN.equals(intent.getAction()) && name != null) {
				for (String packageName : packageNames) {
					if (packageName.equals(name.getPackageName())) {
						toRemove.add(appInfo);
						Root3D.deleteFromDB(appInfo);
						info.remove(appInfo);
						removedFromFolder = true;
					}
				}
			}
		}
	}

	void removeSidebarItems(final ArrayList<ApplicationInfo> apps) {
		final HotSeat3D sideBar = root.getHotSeatBar();
		final Context context = launcher.getApplicationContext();
		final ViewGroup3D mainGroup = sideBar.getMainGroup();
		final int count = mainGroup.getChildCount();
		final PackageManager manager = context.getPackageManager();
		final AppWidgetManager widgets = AppWidgetManager.getInstance(context);
		final HashSet<String> packageNames = new HashSet<String>();
		final int appCount = apps.size();
		for (int i = 0; i < appCount; i++) {
			packageNames.add(apps.get(i).componentName.getPackageName());
		}
		for (int i = 0; i < count; i++) {
			final ViewGroup3D layout = (ViewGroup3D) mainGroup.getChildAt(i);
			// Avoid ANRs by treating each screen separately
			launcher.postRunnable(new Runnable() {

				public void run() {
					final ArrayList<IconBase3D> childrenToRemove = new ArrayList<IconBase3D>();
					childrenToRemove.clear();
					int childCount = layout.getChildCount();
					for (int j = 0; j < childCount; j++) {
						View3D viewtmp = layout.getChildAt(j);
						if (!(viewtmp instanceof IconBase3D))
							continue;
						final IconBase3D view = (IconBase3D) viewtmp;
						Object tag = view.getItemInfo();
						if (tag instanceof ShortcutInfo) {
							final ShortcutInfo info = (ShortcutInfo) tag;
							final Intent intent = info.intent;
							final ComponentName name = intent.getComponent();
							if (Intent.ACTION_MAIN.equals(intent.getAction())
									&& name != null) {
								for (String packageName : packageNames) {
									if (packageName.equals(name
											.getPackageName())) {
										// LauncherModel.deleteItemFromDatabase(mLauncher,
										// info);
										Root3D.deleteFromDB(info);
										childrenToRemove.add(view);
									}
								}
							}
						} else if (tag instanceof UserFolderInfo) {
							removeFolderItems((FolderIcon3D) view, apps);
						} else if (tag instanceof Widget2DInfo) {
							final Widget2DInfo info = (Widget2DInfo) tag;
							final AppWidgetProviderInfo provider = widgets
									.getAppWidgetInfo(info.appWidgetId);
							if (provider != null) {
								for (String packageName : packageNames) {
									if (packageName.equals(provider.provider
											.getPackageName())) {
										Root3D.deleteFromDB(info);
										// LauncherModel.deleteItemFromDatabase(mLauncher,
										// info);
										childrenToRemove.add(view);
									}
								}
							}
						}
					}
					childCount = childrenToRemove.size();
					for (int j = 0; j < childCount; j++) {
						View3D child = (View3D) childrenToRemove.get(j);
						child.remove();
						// layout.removeViewInLayout(child);
						if (child instanceof DropTarget3D) {
							dragLayer.removeDropTarget((DropTarget3D) child);
						}
					}
				}
			});
		}
	}

	public int getCurrentScreen() {
		if (workspace == null)
			return -1;
		return workspace.getCurrentScreen();
	}

	public int getScreenCount() {
		return workspace.getPageNum();
	}

	public CellLayout3D getCurrentCellLayout() {
		if (workspace == null)
			return null;
		if (workspace.getChildCount() == 1) {
			return null;
		} else {
			return workspace.getCurrentCellLayout();
		}
	}

	public void exeDeletePage() {
		pageContainer.exeDeletePage();
	}

	public boolean isWorkspaceVisible() {
		if (workspace == null)
			return true;
		return workspace.visible;
	}

	public void showAllApp() {
		if (DefaultLayout.mainmenu_inout_no_anim) {
			root.showAllAppFromWorkspaceEx();
		} else {
			root.showAllAppFromWorkspace();
		}
	}

	public void addPageScrollListener(PageScrollListener scrollListener) {
		workspace.addScrollListener(scrollListener);
		scrollListener.setCurrentPage(workspace.getCurrentPage());
		bSetHomepageDone = true;
	}

	public void onHomeKey(final boolean alreadyOnHome) {
		if (!bCreatDone)
			return;
		iLoongLauncher.getInstance().postRunnable(new Runnable() {

			@Override
			public void run() {
				root.onHomeKey(alreadyOnHome);
			}
		});
	}

	@Override
	public void bindWidget3DAdded(String packageName) {
		if (packageName.equals("com.inveno.newpiflow")) {
			Messenger.sendMsg(Messenger.MSG_INSTALL_NEWS_VIEW, null);
			return;
		}
		if (DefaultLayout.enable_quick_search) {
			if (root.qSearchGroup != null && QSearchGroup.mLoaded) {
				QSearchGroup.sNeedChange = true;
				// root.qSearchGroup.reLoad();
			}
		}
		Log.v("launcher", "bindWidget3DAdded:" + packageName);
		if (!bCreatDone) {
			Log.e("launcher", "bindWidget3DAdded but not CreatDone!!!!!!");
			Widget3DManager.getInstance().updateWidget3DInfo();
			return;
		}
		Intent intent = new Intent("com.iLoong.widget", null);
		PackageManager pm = iLoongApplication.getInstance().getPackageManager();
		List<ResolveInfo> infoList = pm.queryIntentActivities(intent,
				PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
		ResolveInfo resolveInfo = null;
		for (ResolveInfo resolve : infoList) {
			if (resolve.activityInfo.packageName.equals(packageName)) {
				resolveInfo = resolve;
				break;
			}
		}
		if (resolveInfo != null) {
			Widget3DManager.getInstance().installWidget(resolveInfo);
			addWidget3DInUpdated(resolveInfo); // xiatian add //fix
												// bug:bindWidget3DUpdated lead
												// to lose Widget3DView in
												// Workspace3D
			if (!DefaultLayout
					.isWidgetLoadByInternal(resolveInfo.activityInfo.packageName)) {
				Widget3DShortcut shortcut = new Widget3DShortcut(
						"Widget3DShortcut", resolveInfo);
				// if (AppHost3D.V2) {
				AppHost3D appHost = (AppHost3D) root.getAppHost();
				appHost.addWidget(shortcut);
				// teapotXu add start
				if (DefaultLayout.enable_workspace_miui_edit_mode) {
					Widget3DShortcut shortcut2 = new Widget3DShortcut(
							"Widget3DShortcut", resolveInfo);
					MIUIWidgetHost widgetHost = (MIUIWidgetHost) root
							.getWidgetHost();
					widgetHost.addWidget(shortcut2);
				}
				// teapotXu add end
			}
			// } else {
			// SidebarMainGroup mainGroup = (SidebarMainGroup) root
			// .getSidebar().getMainGroup();
			// mainGroup.bindWidget3D(shortcut);
			// }
		}
	}

	@Override
	public void bindWidget3DUpdated(String packageName) {
		Log.v("bindWidget3DUpdated", packageName);
		// Widget3DManager.getInstance().unInstallWidget(packageName);
		mIsWidget3DUpdated = true; // xiatian add //fix bug:bindWidget3DUpdated
									// lead to lose Widget3DView in Workspace3D
		bindWidget3DRemoved(packageName);
		bindWidget3DAdded(packageName);
		mIsWidget3DUpdated = false; // xiatian add //fix bug:bindWidget3DUpdated
									// lead to lose Widget3DView in Workspace3D
	}

	@Override
	public void bindWidget3DRemoved(final String packageName) {
		if (packageName.equals("com.inveno.newpiflow")) {
			Messenger.sendMsg(Messenger.MSG_UNINSTALL_NEWS_VIEW, null);
			// UmengMobclickAgent.FirstTime( launcher , "DeleteNewsSys" );
			return;
		} else if (packageName.equals("com.iLoong.Calendar")) {
			// UmengMobclickAgent.FirstTime( launcher , "DeleteCalendarSys" );
			if (iLoongLauncher.getInstance().getD3dListener().getRoot().zoomview != null) {
				ViewGroup3D zoom = iLoongLauncher.getInstance()
						.getD3dListener().getRoot().zoomview;
				if (zoom != null) {
					iLoongLauncher.getInstance().getD3dListener().getRoot()
							.removeView(zoom);
					zoom = null;
					iLoongLauncher.getInstance().getD3dListener().getRoot().zoomview = null;
				}
				iLoongLauncher.getInstance().getD3dListener().getRoot()
						.ReturnClor();
			}
			Widget3DProvider provider = Widget3DManager.getInstance()
					.getWidget3DProvider("com.iLoong.Calendar");
			provider.spanX = 4;
			provider.spanY = 4;
			Widget3DManager.getInstance().updateWidgetInfo(
					"com.iLoong.Calendar", provider);
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(iLoongLauncher.getInstance());
			Editor editor = sp.edit();
			editor.putBoolean("com.iLoong.Calendar", false);
			editor.putInt("com.iLoong.Calendar" + ":spanX", -1);
			editor.putInt("com.iLoong.Calendar" + ":spanY", -1);
			editor.commit();
		} else if (packageName.equals("com.gionee.amiweather")) {
			// UmengMobclickAgent.FirstTime( launcher , "DeleteAmiSys" );
			// return;
		}
		Log.v("bindWidget3DRemoved", packageName);
		if (DefaultLayout.isWidgetLoadByInternal(packageName)) {
			return;
		}
		if (!bCreatDone) {
			Log.e("launcher", "bindWidget3DRemoved but not CreatDone!!!!!!");
			Widget3DManager.getInstance().updateWidget3DInfo();
			return;
		}
		// 取得Widget3DHost中将要卸载的程序?
		ResolveInfo resolveInfo = Widget3DManager.getInstance().getResolveInfo(
				packageName);
		// 处理manager集合中保存的数据
		Widget3DManager.getInstance().unInstallWidget(packageName);
		// 清理sidebar上面的图?
		// if (AppHost3D.V2) {
		AppHost3D appHost = (AppHost3D) root.getAppHost();
		appHost.removeWidget(packageName);
		// teapotXu add start
		if (DefaultLayout.enable_workspace_miui_edit_mode) {
			MIUIWidgetHost widgetHost = (MIUIWidgetHost) root.getWidgetHost();
			widgetHost.removeWidget(packageName);
		}
		// teapotXu add end
		// } else {
		// SidebarMainGroup mainGroup = (SidebarMainGroup) root.getSidebar()
		// .getMainGroup();
		// mainGroup.unBindWidget3D(packageName);
		// }
		// 清理桌面以及数据库中保存的图?
		final int count = workspace.getChildCount();
		for (int i = 0; i < count; i++) {
			View3D viewTmp = workspace.getChildAt(i);
			if (!(viewTmp instanceof CellLayout3D))
				continue;
			final CellLayout3D layout = (CellLayout3D) viewTmp;
			try {
				final ArrayList<IconBase3D> childrenToRemove = new ArrayList<IconBase3D>();
				childrenToRemove.clear();
				int childCount = layout.getChildCount();
				for (int j = 0; j < childCount; j++) {
					final IconBase3D view = (IconBase3D) layout.getChildAt(j);
					Object tag = view.getItemInfo();
					if (tag instanceof Widget3DInfo) {
						final Widget3DInfo info = (Widget3DInfo) tag;
						if (info.packageName.equals(packageName)) {
							// xiatian add start //fix bug:bindWidget3DUpdated
							// lead to lose Widget3DView in Workspace3D
							if (mIsWidget3DUpdated) {
								mUpdatedWidget3DInfo = info;
							}
							// xiatian add end
							Root3D.deleteFromDB(info);
							childrenToRemove.add(view);
							// MobclickAgent.onEvent( launcher ,
							// "DeleteWidget3DSys" , packageName );
						}
					}
				}
				childCount = childrenToRemove.size();
				for (int j = 0; j < childCount; j++) {
					View3D child = (View3D) childrenToRemove.get(j);
					if (child instanceof Widget3D) {
						Widget3D widget3D = (Widget3D) child;
						widget3D.onUninstall();
					}
					child.remove();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			if (resolveInfo != null) {
				int flag = (resolveInfo.activityInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_EXTERNAL_STORAGE);
				// 可移动的应用
				if (flag != 0) {
					// 安装在SD?
					Log.v("bindWidget3DRemoved", "   "
							+ resolveInfo.activityInfo.packageName
							+ " 需要重启Launcher");
					// 重启launcher
					launcher.mMainHandler.post(new Runnable() {

						@Override
						public void run() {
							Intent intent = new Intent();
							intent.setClass(launcher, ResestActivity.class);
							launcher.startActivity(intent);
						}
					});
				} else {
					// 安装在手机内
					Log.e("***********", resolveInfo.activityInfo.packageName
							+ " 不需要重启Launcher  " + flag);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Bitmap getShortcutPreview(ResolveInfo info) {
		// Render the background
		int offset = 0;
		int bitmapSizeW = R3D.workspace_cell_width;
		int bitmapSizeH = R3D.workspace_cell_height;
		Bitmap preview = Bitmap.createBitmap(bitmapSizeW, bitmapSizeH,
				Config.ARGB_8888);
		// Render the icon
		Drawable icon = iconCache.getFullResIcon(info);
		if (icon == null)
			icon = iconCache.getDefaultIconDrawable();
		int hsize = icon.getIntrinsicWidth();
		int ysize = icon.getIntrinsicHeight();
		int xoffset = (bitmapSizeW - hsize) / 2;
		int yoffset = (bitmapSizeH - ysize) / 2;
		if (xoffset >= 0 && yoffset >= 0) {
		} else {
			float ratio = 1f;
			float ratioX = (float) hsize / (float) bitmapSizeW;
			float ratioY = (float) ysize / (float) bitmapSizeH;
			ratio = ratioX > ratioY ? ratioX : ratioY;
			hsize = (int) ((float) bitmapSizeW / ratio);
			ysize = (int) ((float) bitmapSizeH / ratio);
			xoffset = (bitmapSizeW - hsize) / 2;
			yoffset = (bitmapSizeH - ysize) / 2;
		}
		renderDrawableToBitmap(icon, preview, xoffset, yoffset, hsize, ysize);
		return preview;
	}

	// teapotXu add start for longClick in Workspace to editMode as miui
	public static Bitmap getWidgetPreviewWorkspaceEditMode(Bitmap bitmap,
			int cellHSpan, int cellVSpan) {
		int bitmapWidth;
		int bitmapHeight;
		int maxWidth;
		int maxHeight;
		bitmapWidth = bitmap.getWidth();
		bitmapHeight = bitmap.getHeight();
		// Cap the size so widget previews don't appear larger than the actual
		// widget
		if (cellHSpan < 3)
			cellHSpan = 3;
		if (cellVSpan < 3)
			cellVSpan = 3;
		// teapotXu add start
		// if (DefaultLayout.enable_workspace_edit_mode)
		{
			maxWidth = widgetHost.estimateWidgetCellWidth(cellHSpan);
			maxHeight = widgetHost.estimateWidgetCellHeight(cellVSpan);
		}
		float scale = Utils3D.getScreenWidth() / 720f;
		if (bitmapWidth > maxWidth) {
			scale = maxWidth / (float) bitmapWidth;
		}
		if (bitmapHeight * scale > maxHeight) {
			scale = maxHeight / (float) bitmapHeight;
		}
		if (DefaultLayout.show_widget_shortcut_bg
				|| DefaultLayout.widget_shortcut_lefttop) {
			bitmapWidth *= 0.9f;
			bitmapHeight *= 0.9f;
		}
		// if (scale != 1f) {
		bitmapWidth = (int) (scale * bitmapWidth);
		bitmapHeight = (int) (scale * bitmapHeight);
		Bitmap preview = Bitmap.createScaledBitmap(bitmap, bitmapWidth,
				bitmapHeight, true);
		if (!RR.net_version && !bitmap.isRecycled())
			if (!bitmap.equals(preview))
				bitmap.recycle();
		return preview;
		// } else
		// return bitmap;
	}

	public Bitmap getWidgetPreviewWorkspaceEditMode(ComponentName provider,
			int previewImage, int iconId, int cellHSpan, int cellVSpan,
			int maxWidth, int maxHeight) {
		// Load the preview image if possible
		if (cellHSpan == 1 && cellVSpan == 1) {
			cellHSpan = 2;
			cellVSpan = 2;
		}
		String packageName = provider.getPackageName();
		if (maxWidth < 0)
			maxWidth = Integer.MAX_VALUE;
		if (maxHeight < 0)
			maxHeight = Integer.MAX_VALUE;
		Drawable drawable = null;
		if (previewImage != 0) {
			drawable = launcher.getPackageManager().getDrawable(packageName,
					previewImage, null);
			if (drawable == null) {
				Log.w(TAG,
						"Can't load widget preview drawable 0x"
								+ Integer.toHexString(previewImage)
								+ " for provider: " + provider);
			}
		}
		int bitmapWidth;
		int bitmapHeight;
		boolean widgetPreviewExists = (drawable != null);
		if (widgetPreviewExists) {
			bitmapWidth = drawable.getIntrinsicWidth();
			bitmapHeight = drawable.getIntrinsicHeight();
			// teapotXu add start
			// if (DefaultLayout.enable_workspace_edit_mode)
			{
				maxWidth = Math.min(maxWidth,
						widgetHost.estimateWidgetCellWidth(4));
				maxHeight = Math.min(maxHeight,
						widgetHost.estimateWidgetCellHeight(4));
			}
		} else {
			if (DefaultLayout.display_widget_preview_hole) {
				// teapotXu add start
				bitmapWidth = Math.min(maxWidth,
						widgetHost.estimateWidgetCellWidth(cellHSpan));
				bitmapHeight = Math.min(maxHeight,
						widgetHost.estimateWidgetCellHeight(cellVSpan));
				maxWidth = Math.min(maxWidth,
						widgetHost.estimateWidgetCellWidth(4));
				maxHeight = Math.min(maxHeight,
						widgetHost.estimateWidgetCellHeight(4));
			} else {
				bitmapWidth = R3D.workspace_cell_width;
				bitmapHeight = R3D.workspace_cell_height;
				// if (DefaultLayout.ui_style_miui2)
				{
					int app_icon_size = (int) iLoongLauncher.getInstance()
							.getResources()
							.getDimension(android.R.dimen.app_icon_size);
					bitmapWidth = Math.min(app_icon_size,
							widgetHost.estimateWidgetCellWidth(4));
					bitmapHeight = Math.min(app_icon_size,
							widgetHost.estimateWidgetCellHeight(4));
				}
				maxWidth = bitmapWidth;
				maxHeight = bitmapHeight;
			}
		}
		float scale = 1f;
		if (bitmapWidth > maxWidth) {
			scale = maxWidth / (float) bitmapWidth;
		}
		if (bitmapHeight * scale > maxHeight) {
			scale = maxHeight / (float) bitmapHeight;
		}
		if (scale != 1f) {
			bitmapWidth = (int) (scale * bitmapWidth);
			bitmapHeight = (int) (scale * bitmapHeight);
		}
		if (DefaultLayout.show_widget_shortcut_bg
				|| DefaultLayout.widget_shortcut_lefttop) {
			bitmapWidth *= 0.9f;
			bitmapHeight *= 0.9f;
		}
		// Log.d("launcher", "width,height="+bitmapWidth+","+bitmapHeight);
		Bitmap preview = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
				Config.ARGB_8888);
		if (widgetPreviewExists) {
			renderDrawableToBitmap(drawable, preview, 0, 0, bitmapWidth,
					bitmapHeight);
		} else {
			if (DefaultLayout.display_widget_preview_hole) {
				renderDrawableToBitmap(mDefaultWidgetBackground, preview, 0, 0,
						bitmapWidth, bitmapHeight);
			}
			try {
				Drawable icon = null;
				if (iconId > 0)
					icon = iconCache.getFullResIcon(packageName, iconId);
				if (icon == null)
					icon = iconCache.getDefaultIconDrawable();
				int hsize = icon.getIntrinsicWidth();
				int ysize = icon.getIntrinsicHeight();
				float scale2 = 1f;
				if (icon.getIntrinsicWidth() > bitmapWidth) {
					scale2 = (float) bitmapWidth
							/ (float) icon.getIntrinsicWidth();
				}
				if (icon.getIntrinsicHeight() * scale2 > bitmapHeight) {
					scale2 = (float) bitmapHeight
							/ (float) icon.getIntrinsicHeight();
				}
				if (scale2 != 1f) {
					hsize = (int) (scale2 * icon.getIntrinsicWidth());
					ysize = (int) (scale2 * icon.getIntrinsicHeight());
				}
				int hoffset = (bitmapWidth - hsize) / 2;
				int yoffset = (bitmapHeight - ysize) / 2;
				renderDrawableToBitmap(icon, preview, hoffset, yoffset, hsize,
						ysize);
			} catch (Resources.NotFoundException e) {
			}
		}
		return preview;
	}

	// teapotXu add end
	public static Bitmap getWidgetPreview(Bitmap bitmap, int cellHSpan,
			int cellVSpan) {
		int bitmapWidth;
		int bitmapHeight;
		int maxWidth;
		int maxHeight;
		bitmapWidth = bitmap.getWidth();
		bitmapHeight = bitmap.getHeight();
		// Cap the size so widget previews don't appear larger than the actual
		// widget
		if (cellHSpan < 3)
			cellHSpan = 3;
		if (cellVSpan < 3)
			cellVSpan = 3;
		maxWidth = appHost.estimateWidgetCellWidth(cellHSpan);
		maxHeight = appHost.estimateWidgetCellHeight(cellVSpan);
		float scale = 1f;
		if (bitmapWidth > maxWidth) {
			scale = maxWidth / (float) bitmapWidth;
		}
		if (bitmapHeight * scale > maxHeight) {
			scale = maxHeight / (float) bitmapHeight;
		}
		if (DefaultLayout.show_widget_shortcut_bg
				|| DefaultLayout.widget_shortcut_lefttop) {
			bitmapWidth *= 0.9f;
			bitmapHeight *= 0.9f;
		}
		// if (scale != 1f) {
		bitmapWidth = (int) (scale * bitmapWidth);
		bitmapHeight = (int) (scale * bitmapHeight);
		Bitmap preview = Bitmap.createScaledBitmap(bitmap, bitmapWidth,
				bitmapHeight, true);
		// if( !bitmap.equals( preview ) )
		// bitmap.recycle();
		return preview;
		// } else
		// return bitmap;
	}

	public Bitmap getWidgetPreview(ComponentName provider, int previewImage,
			int iconId, int cellHSpan, int cellVSpan, int maxWidth,
			int maxHeight) {
		// Load the preview image if possible
		if (cellHSpan == 1 && cellVSpan == 1) {
			cellHSpan = 2;
			cellVSpan = 2;
		}
		String packageName = provider.getPackageName();
		if (maxWidth < 0)
			maxWidth = Integer.MAX_VALUE;
		if (maxHeight < 0)
			maxHeight = Integer.MAX_VALUE;
		Drawable drawable = null;
		if (previewImage != 0) {
			drawable = launcher.getPackageManager().getDrawable(packageName,
					previewImage, null);
			if (drawable == null) {
				Log.w(TAG,
						"Can't load widget preview drawable 0x"
								+ Integer.toHexString(previewImage)
								+ " for provider: " + provider);
			}
		}
		int bitmapWidth;
		int bitmapHeight;
		boolean widgetPreviewExists = (drawable != null);
		if (widgetPreviewExists) {
			bitmapWidth = drawable.getIntrinsicWidth();
			bitmapHeight = drawable.getIntrinsicHeight();
			maxWidth = Math.min(maxWidth, appHost.estimateWidgetCellWidth(4));
			maxHeight = Math
					.min(maxHeight, appHost.estimateWidgetCellHeight(4));
		} else {
			if (DefaultLayout.display_widget_preview_hole) {
				bitmapWidth = Math.min(maxWidth,
						appHost.estimateWidgetCellWidth(cellHSpan));
				bitmapHeight = Math.min(maxHeight,
						appHost.estimateWidgetCellHeight(cellVSpan));
				if (bitmapWidth < R3D.workspace_cell_width)
					bitmapWidth = R3D.workspace_cell_width;
				if (bitmapHeight < R3D.workspace_cell_height)
					bitmapHeight = R3D.workspace_cell_height;
				maxWidth = Math.min(maxWidth,
						appHost.estimateWidgetCellWidth(4));
				maxHeight = Math.min(maxHeight,
						appHost.estimateWidgetCellHeight(4));
			} else {
				bitmapWidth = R3D.workspace_cell_width;
				bitmapHeight = R3D.workspace_cell_height;
				maxWidth = bitmapWidth;
				maxHeight = bitmapHeight;
			}
		}
		float scale = 1f;
		if (bitmapWidth > maxWidth) {
			scale = maxWidth / (float) bitmapWidth;
		}
		if (bitmapHeight * scale > maxHeight) {
			scale = maxHeight / (float) bitmapHeight;
		}
		if (scale != 1f) {
			bitmapWidth = (int) (scale * bitmapWidth);
			bitmapHeight = (int) (scale * bitmapHeight);
		}
		if (DefaultLayout.show_widget_shortcut_bg
				|| DefaultLayout.widget_shortcut_lefttop) {
			bitmapWidth *= 0.9f;
			bitmapHeight *= 0.9f;
		}
		// Log.d("launcher", "width,height="+bitmapWidth+","+bitmapHeight);
		Bitmap preview = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
				Config.ARGB_8888);
		if (widgetPreviewExists) {
			renderDrawableToBitmap(drawable, preview, 0, 0, bitmapWidth,
					bitmapHeight);
		} else {
			if (DefaultLayout.display_widget_preview_hole) {
				renderDrawableToBitmap(mDefaultWidgetBackground, preview, 0, 0,
						bitmapWidth, bitmapHeight);
			}
			try {
				Drawable icon = null;
				if (iconId > 0)
					icon = iconCache.getFullResIcon(packageName, iconId);
				if (icon == null)
					icon = iconCache.getDefaultIconDrawable();
				int hsize = icon.getIntrinsicWidth();
				int ysize = icon.getIntrinsicHeight();
				float scale2 = 1f;
				if (icon.getIntrinsicWidth() > bitmapWidth) {
					scale2 = (float) bitmapWidth
							/ (float) icon.getIntrinsicWidth();
				}
				if (icon.getIntrinsicHeight() * scale2 > bitmapHeight) {
					scale2 = (float) bitmapHeight
							/ (float) icon.getIntrinsicHeight();
				}
				if (scale2 != 1f) {
					hsize = (int) (scale2 * icon.getIntrinsicWidth());
					ysize = (int) (scale2 * icon.getIntrinsicHeight());
				}
				int hoffset = (bitmapWidth - hsize) / 2;
				int yoffset = (bitmapHeight - ysize) / 2;
				renderDrawableToBitmap(icon, preview, hoffset, yoffset, hsize,
						ysize);
			} catch (Resources.NotFoundException e) {
			}
		}
		return preview;
	}

	public static void renderDrawableToBitmap(Drawable d, Bitmap bitmap, int x,
			int y, int w, int h) {
		renderDrawableToBitmap(d, bitmap, x, y, w, h, 1f, 0xFFFFFFFF);
	}

	public static void renderDrawableToBitmap(Drawable d, Bitmap bitmap, int x,
			int y, int w, int h, float scale, int multiplyColor) {
		if (bitmap != null) {
			Canvas c = new Canvas(bitmap);
			c.scale(scale, scale);
			Rect oldBounds = d.copyBounds();
			d.setBounds(x, y, x + w, y + h);
			if (DefaultLayout.widget_shortcut_lefttop) {
				d.setBounds(0, 0, w, h);
			}
			d.draw(c);
			d.setBounds(oldBounds); // Restore the bounds
			// if (multiplyColor != 0xFFFFFFFF) {
			// c.drawColor(mDragViewMultiplyColor, PorterDuff.Mode.MULTIPLY);
			// }
		}
	}

	@Override
	public void finishBindWorkspace() {
		if (bDesktopDone) {
			return;
		}
		bDesktopDone = true;
		checkLoadProgress();
	}

	public void afainApp(int hang, int lie) {
		appHost.afainApp(hang, lie);
	}

	public void sortApp(final int checkId, final int sortOrigin) {
		launcher.postRunnable(new Runnable() {

			@Override
			public void run() {
				if (sortOrigin == iLoongLauncher.SORT_ORIGIN_APPLIST) {
					appHost.sortApp(checkId);
				} else if (sortOrigin == iLoongLauncher.SORT_ORIGIN_ADD_APP_TO_FOLDER) {
					if (root.getListToAdd() != null) {
						root.getListToAdd().sortApp(checkId);
					}
				}
			}
		});
	}

	public void dismissIntroduction() {
		root.show();
	}

	public void focusWidget(WidgetPluginView3D widgetPluginView, int state) {
		root.focusWidget(widgetPluginView, state);
	}

	public boolean findCellForSpan(int screen, int x, int y, int[] xy,
			int spanX, int spanY) {
		xy[0] = -1;
		xy[1] = -1;
		if (workspace == null)
			return false;
		if (screen >= workspace.getPageNum())
			return false;
		if (DefaultLayout.show_music_page) {
			View3D view = workspace.getChildAt(screen);
			if (view instanceof MediaView3D) {
				return false;
			}
		}
		// xujin
		if (DefaultLayout.enable_camera) {
			View3D view = workspace.getChildAt(screen);
			if (view instanceof MediaView3D) {
				return false;
			}
		}
		CellLayout3D cell;
		if (DefaultLayout.enable_workspace_miui_edit_mode
				&& Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode) {
			View3D child = workspace.getChildAt(screen);
			if (child instanceof CellLayout3D) {
				cell = (CellLayout3D) child;
			} else {
				return false;
			}
		} else {
			cell = (CellLayout3D) workspace.getChildAt(screen);
		}
		if (x >= 0 && y >= 0)
			// xy = cell.findNearestArea(x, y, spanX, spanY, xy);
			xy = cell.findNearestAreaAvailuable(x, y, spanX, spanY, xy);
		else
			cell.findCellForSpan(xy, 1, 1);
		return !(xy[0] == -1 || xy[1] == -1);
	}

	public void setAppEffectType(int select) {
		if (root != null)
			root.setEffectType(select);
	}

	// xiatian add start //fix bug:0001881
	public boolean HideThemesInAppList(String packageName) {
		if (packageName.length() > 15) {
			String s16 = packageName.substring(0, 15);
			// Log.v("xiatian","1 - s16:" + s16);
			if (s16.equals("com.turbotheme.")) {
				// Log.v("xiatian","1 - rm - packageName:" + packageName);
				return true;
			}
		}
		return false;
		// if( packageName != null )
		// {
		// return packageName.startsWith( ThemeManager.ACTION_INTENT_THEME );
		// }
		// return false;
	}

	// xiatian add end
	// teapotXu add start
	// Widget icon will not be shown in Launcher
	public boolean IsInstalledCooeeWidgets(iLoongLauncher launcher,
			String packageName) {
		PackageManager pm = launcher.getPackageManager();
		Intent intent = new Intent("com.iLoong.widget", null);
		List<ResolveInfo> mWidgetResolveInfoList = pm.queryIntentActivities(
				intent, PackageManager.GET_ACTIVITIES
						| PackageManager.GET_META_DATA);
		for (ResolveInfo resolveInfo : mWidgetResolveInfoList) {
			if (packageName.equals(resolveInfo.activityInfo.packageName)) {
				return true;
			}
		}
		return false;
	}

	// teapotXu add end
	// xiatian add start //fix bug:bindWidget3DUpdated lead to lose Widget3DView
	// in Workspace3D
	private void addWidget3DInUpdated(ResolveInfo resolveInfo) {
		if (mIsWidget3DUpdated) {
			Widget3D mWidget3D = Widget3DManager.getInstance().getWidget3D(
					resolveInfo);
			Widget3DInfo wdgInfo = mWidget3D.getItemInfo();
			if (mUpdatedWidget3DInfo != null)
				wdgInfo = mUpdatedWidget3DInfo;
			wdgInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_WIDGET3D;
			mWidget3D.setItemInfo(wdgInfo);
			Root3D.addOrMoveDB(wdgInfo, ItemInfo.NO_ID);
			getWorkspace3D().addInScreen(mWidget3D, wdgInfo.screen, wdgInfo.x,
					wdgInfo.y, false);
			DefaultLayout.addWidgetView(mWidget3D,
					resolveInfo.activityInfo.packageName);
			mUpdatedWidget3DInfo = null;
		}
	}

	// xiatian add end
	public void closeSetupMenu() {
		if (setupMenu != null) {
			if (setupMenu.visible && setupMenu.touchable)
				setupMenu.hide();
		}
	}

	public void setupOnMenuKey() {
		if (setupMenu != null && root.SetupMenuShownPermition()) {
			if (!setupMenu.visible && !setupMenu.touchable)
				setupMenu.show();
			else if (setupMenu.visible && setupMenu.touchable)
				setupMenu.hide();
		}
	}

	public void changeSetupMenu(View3D v) {
		if (v instanceof SetupMenu3D) {
			if (setupMenu != null) {
				for (int i = 0; i < setupMenu.getChildCount(); i++) {
					Log.v("", "setupMenu3D setSetupMenu d3dname is "
							+ setupMenu.getChildAt(i).name);
					if (setupMenu.getChildAt(i) != null) {
					}
				}
				setupMenu = null;
			}
			setupMenu = (SetupMenu3D) v;
			root.setSetupMenu(setupMenu);
		}
	}

	// xiatian add start //EffectPreview
	private void initEffectPreview() {
		mWorkspaceEffectPreview = new EffectPreview3D("EffectPreview3D",
				EffectPreview3D.TYPE_WORKSPACE);
		root.setWorkspaceEffectPreview3D(mWorkspaceEffectPreview);
		mWorkspaceEffectPreview.setWorkspace(workspace);
		workspace.setWorkspaceEffectPreview3D(mWorkspaceEffectPreview);
		pageSelectIcon.setWorkspaceEffectPreview3D(mWorkspaceEffectPreview);
		mApplistEffectPreview = new EffectPreview3D("EffectPreview3D",
				EffectPreview3D.TYPE_APPLIST);
		root.setApplistEffectPreview3D(mApplistEffectPreview);
		mApplistEffectPreview.setAppHost(appHost);
		appHost.appList.setApplistEffectPreview3D(mApplistEffectPreview);
		mEffectPreviewTips3D = new EffectPreviewTips3D("EffectPreviewTips3D");
		root.setEffectPreviewTips3D(mEffectPreviewTips3D);
		workspace.setEffectPreviewTips3D(mEffectPreviewTips3D);
		appHost.appList.setEffectPreviewTips3D(mEffectPreviewTips3D);
		mEffectPreviewTips3D
				.setWorkspaceEffectPreview3D(mWorkspaceEffectPreview);
		mEffectPreviewTips3D.setApplistEffectPreview3D(mApplistEffectPreview);
		mEffectPreviewTips3D.setHotSeat3D(root.getHotSeatBar());
		mEffectPreviewTips3D.setAppHost3D(appHost);
		mEffectPreviewTips3D.setWorkspace(workspace);
	}

	// xiatian add end
	public PageIndicator3D getPageIndicator() {
		return pageSelectIcon;
	}

	@Override
	public void bindCooeePlugin(final CooeePluginInfo info) {
		// xujin temp 暂不做任何处理
		if (!DefaultLayout.enable_news) {
			return;
		}
		// CooeePluginHostView pluginHost = CooeePluginManager.getInstance(
		// iLoongLauncher.getInstance() ).getHostView( info );
		// if( pluginHost != null )
		// {
		// Log.i("jinxu", "addInScreen");
		// iLoongLauncher.getInstance().xWorkspace.addInScreen( pluginHost ,
		// info.screen , 0 , 0 , info.spanX , info.spanY , true , true );
		// }else{
		// Log.i("jinxu", "pluginHost == null");
		// }
		// SaveScreenSize();
		// PiScrollView view = new PiScrollView( iLoongLauncher.getInstance() ,
		// null );
		// CooeePluginHostView hostView = new CooeePluginHostView(
		// iLoongLauncher.getInstance() );
		// hostView.addView( view , new LinearLayout.LayoutParams(
		// UtilsBase.getScreenWidth() , UtilsBase.getScreenHeight() ) );
		// int screen =
		// iLoongLauncher.getInstance().d3dListener.getScreenCount();
		// iLoongLauncher.getInstance().xWorkspace.addInScreen( hostView ,
		// screen - 2 , 0 , 0 , info.spanX , info.spanY , true , true );
		// Log.i( "jinxu" , "bindCooeePlugin" );
	}

	public void releaseWidget() {
		// Log.i( "widget" , "releaseWidget" );
		if (workspace != null) {
			for (int i = 0; i < workspace.getChildCount(); i++) {
				View3D view = workspace.getChildAt(i);
				if (view != null && view instanceof CellLayout3D) {
					if (view == workspace.getCurrentCellLayout())
						continue;
					CellLayout3D cell = (CellLayout3D) view;
					for (int j = 0; j < cell.getChildCount(); j++) {
						View3D view2 = cell.getChildAt(j);
						if (view2 != null && view2 instanceof Widget) {
							Widget widget = (Widget) view2;
							widget.dispose();
						}
					}
				}
			}
		}
	}

	public void disttachWidget() {
		Log.i("widget", "disttachWidget");
		if (workspace != null) {
			for (int i = 0; i < workspace.getChildCount(); i++) {
				View3D view = workspace.getChildAt(i);
				if (view != null && view instanceof CellLayout3D) {
					if (view == workspace.getCurrentCellLayout())
						continue;
					CellLayout3D cell = (CellLayout3D) view;
					for (int j = 0; j < cell.getChildCount(); j++) {
						View3D view2 = cell.getChildAt(j);
						if (view2 != null && view2 instanceof Widget) {
							Widget widget = (Widget) view2;
							widget.disttach();
						}
					}
				}
			}
		}
	}

	public void onRelaeseWidget2D() {
		// TODO Auto-generated method stub
		for (int i = 0; i < allwidget2Dbitmap.size(); i++) {
			Bitmap b = allwidget2Dbitmap.get(i);
			b.recycle();
			b = null;
		}
		allwidget2Dbitmap.clear();
		widget2DpkgName.clear();
	}
}
