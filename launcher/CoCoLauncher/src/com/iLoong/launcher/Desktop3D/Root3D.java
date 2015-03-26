package com.iLoong.launcher.Desktop3D;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Folder3D.Folder3D;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.Folder3D.FolderMIUI3D;
import com.iLoong.launcher.HotSeat3D.DefConfig;
import com.iLoong.launcher.HotSeat3D.HotDockGroup;
import com.iLoong.launcher.HotSeat3D.HotGridView3D;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.MenuActionListener;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.SideBar.SideBar;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.app.AppListDB;
import com.iLoong.launcher.app.LauncherModel;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.FeatureConfig;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.scene.SceneManager;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.widget.Widget;

import dalvik.system.DexClassLoader;
//import com.iLoong.launcher.min3d.ObjectView;

public class Root3D extends ViewGroup3D implements MenuActionListener {
	public final static int MSG_SET_WALLPAPER_OFFSET = 10000;
	private WallpaperManager mWallpaperManager;

	private DragLayer3D dragLayer;
	private PageContainer3D pageContainer;
	public AppHost3D appHost;
	private Workspace3D workspace;
	private TrashIcon3D trashIcon;
	// private HotButton hotButton;
	private PageIndicator3D pageIndicator;
	private SideBar sidebar;
	private HotSeat3D hotseatBar;
	private Timeline timeline;
	public boolean folderOpened = false;
	private FolderIcon3D folder;
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

	class worksapceTweenTask extends TimerTask {

		@Override
		public void run() {
			dealworkspaceTweenFinish();
		}

	}

	private void startRootTimer(long duration) {
		if (timer != null)
			timer.cancel();
		TweenTimerTask = new worksapceTweenTask();
		timer = new Timer();
		timer.schedule(TweenTimerTask, duration);
	}

	private void stopRootTimer() {
		if (timer != null) {
			TweenTimerTask.cancel();
			timer.cancel();
			timer = null;
		}
	}

	public SideBar getSidebar() {
		return sidebar;
	}

	public AppHost3D getAppHost() {
		return appHost;
	}

	public HotSeat3D getHotSeatBar() {
		return hotseatBar;
	}

	public HotDockGroup getHotDockGroup() {
		return hotseatBar.getDockGroup();
	}

	public Root3D(String name) {
		super(name);

		// wanghongjian add start //enable_DefaultScene
		setActionListener();
		if (FeatureConfig.enable_DefaultScene) {
			this.setSceneReciverIntent();
			sceneHandler = new Handler(iLoongLauncher.getInstance()
					.getMainLooper());
		}
		// wanghongjian add end

		if (DefConfig.DEF_NEW_SIDEBAR == true) {
			hotseatBar = new HotSeat3D("HotSeat3DBar");
			this.addView(hotseatBar);
		} else {
			sidebar = new SideBar("sidebar");
			this.addView(sidebar);
		}
		// TODO Auto-generated constructor stub
	}

	public void setLauncher(iLoongLauncher l) {
		this.launcher = l;
		mWallpaperManager = WallpaperManager.getInstance(launcher);
	}

	public void setPageContainer(View3D v) {
		this.pageContainer = (PageContainer3D) v;
		this.pageContainer.hide();
		this.pageContainer.setLauncher(launcher);
		pageIndicatorOldY = pageIndicator.y;// wanghongjian add
											// //enable_DefaultScene
		this.addView(v);
	}

	public void setAppHost(View3D v) {
		this.appHost = (AppHost3D) v;
		if (!iLoongLauncher.showAllAppFirst)
			appHost.hide();
		this.addView(v);
	}

	public void setWorkspace(View3D v) {
		this.workspace = (Workspace3D) v;
		this.addView(v);
		if (hotseatBar != null) {
			hotseatBar.setWorkspace(this.workspace);
		}
		workspace.onDegreeChanged();
		setDragS3EffectScale();
		if (iLoongLauncher.showAllAppFirst)
			workspace.hide();
	}

	public void setTrashIcon(View3D v) {
		this.trashIcon = (TrashIcon3D) v;
		this.trashIcon.hide();
		this.addView(v);
	}

	public void setPageIndicator(View3D v) {
		this.pageIndicator = (PageIndicator3D) v;
		this.addView(v);
		if (iLoongLauncher.showAllAppFirst)
			pageIndicator.hideNoAnim();
	}

	public void setDragLayer(View3D v) {
		this.dragLayer = (DragLayer3D) v;
	}

	public void addDragLayer(View3D v) {
		if (DefConfig.DEF_NEW_SIDEBAR == true) {
			hotseatBar.bringToFront();
		} else {
			sidebar.bringToFront();
		}
		// this.addView(new ObjectView("3d"));
		trashIcon.bringToFront();
		this.dragLayer.hide();
		this.addView(v);
	}

	public void saveSceneIndex(final int index, final String pkg,
			final String cls) {
		if (sceneHandler != null) {
			sceneHandler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (preferences == null) {
						preferences = iLoongLauncher.getInstance()
								.getSharedPreferences("scene_first",
										Activity.MODE_WORLD_WRITEABLE);
					}
					if (preferences != null) {
						preferences.edit().putString("scenepkg", pkg).commit();
						preferences.edit().putString("scenecls", cls).commit();
						preferences.edit().putInt("sceneIndex", index).commit();
						preferences.edit().commit();
					}
				}
			});
		}

	}

	public boolean isPageContainerVisible() {
		return this.pageContainer.isVisible();
	}

	public static void addOrMoveDB(ItemInfo info, long container) {
		if (container >= LauncherSettings.Favorites.CONTAINER_APPLIST) {
			AppListDB.getInstance().addOrMoveItem(info, container);
		} else
			LauncherModel.addOrMoveItemInDatabase(
					iLoongApplication.getInstance(), info, container,
					info.screen, info.x, info.y);
	}

	public static void addOrMoveDB(ItemInfo info) {
		LauncherModel.addOrMoveItemInDatabase(iLoongApplication.getInstance(),
				info, LauncherSettings.Favorites.CONTAINER_DESKTOP,
				info.screen, info.x, info.y);
	}

	public static void deleteFromDB(ItemInfo info) {
		if (info.container >= LauncherSettings.Favorites.CONTAINER_APPLIST) {
			AppListDB.getInstance().deleteItem(info);
		} else
			LauncherModel.deleteItemFromDatabase(
					iLoongApplication.getInstance(), info);
	}

	private void folder_DropList_backtoOrig(ArrayList<View3D> child_list) {
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

		View3D view = child_list.get(0);
		if (view instanceof IconBase3D) {
			if (((IconBase3D) view).getItemInfo().container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
				dropListBacktoHotSeat(child_list);
				return;
			}
		}
		View3D parent = view.getParent();
		ItemInfo info = null;

		if (view instanceof IconBase3D)
			info = ((IconBase3D) view).getItemInfo();

		if (info != null
				&& info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) /*
																					 * is
																					 * not
																					 * come
																					 * from
																					 * mainmenu
																					 */
		{
			for (int i = 0; i < child_list.size(); i++) {
				view = child_list.get(i);
				workspace.addBackInScreen(view, 0, 0);
			}
		}
	}

	private void dropListBacktoHotSeat(ArrayList<View3D> child_list) {
		View3D view;
		ArrayList<ItemInfo> listInfo = new ArrayList<ItemInfo>();
		view = child_list.get(0);
		ItemInfo info = ((IconBase3D) view).getItemInfo();
		listInfo.add(info);
		hotseatBar.bindItems(listInfo);
	}

	private void dropListBacktoOrig(ArrayList<View3D> child_list) {
		View3D view;
		View3D findView = null;
		if (child_list.size() <= 0)
			return;

		view = child_list.get(0);
		if ((view instanceof IconBase3D) == false)
			return;
		ItemInfo info = ((IconBase3D) view).getItemInfo();

		if (view instanceof Widget3D && info.container == -1) {/*
																 * 处理将widget 3d
																 * 扔到其它地方时的资源收集
																 */
			Root3D.deleteFromDB(info);
			Widget3D widget3D = (Widget3D) view;
			Widget3DManager.getInstance().deleteWidget3D(widget3D);
		}

		if (child_list.size() == 1) {
			if (view instanceof IconBase3D) {

				if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
					/* back to hotseat */
					dropListBacktoHotSeat(child_list);
					child_list.clear();
					return;
				}

				/* 从文件夹中来 */
				if (info.container >= 0) {
					UserFolderInfo folderInfo = launcher
							.getFolderInfo(info.container);

					// teapotXu add start for Folder in Maimenu
					if (DefaultLayout.mainmenu_folder_function == true) {
						// folder is from mainmenu, so do nothing, just clear
						// the childlist
						if (folderInfo == null) {
							child_list.clear();
							return;
						}
					}
					// teapotXu add end for Folder in Mainmenu
					if (folderInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
						findView = hotseatBar.findExistView(folderInfo.screen);
					} else if (folderInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
						findView = workspace.getViewByItemInfo(folderInfo);
					}

					if (findView != null && findView instanceof FolderIcon3D) {
						((FolderIcon3D) findView).onDrop(child_list, 0, 0);
					}
					child_list.clear();
					return;
				}
			}
		}

		if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
			for (int i = 0; i < child_list.size(); i++) {
				view = child_list.get(i);

				workspace.addBackInScreen(view, 0, 0);
			}
		}

		child_list.clear();
	}

	// @Override
	// public boolean touchDown (float x, float y, int pointer) {
	// super.onTouchDown(x, y, pointer);
	// return true;
	// }
	private void setDragS3EffectScale() {
		if (Utils3D.getScreenHeight() < 700) {
			Log.v("test123", "0.95f");
			s3EffectScale = 0.9f;
		} else {
			Log.v("test123", "0.8f");
			s3EffectScale = 0.8f;
		}
	}

	public void startDragS3Effect() {
		Workspace3D.is_longKick = true;
		workspace.startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT,
				workspaceTweenDuration, s3EffectScale, s3EffectScale, 0f);
		workspace.setUser(0);
		workspace.startTween(View3DTweenAccessor.USER, Cubic.OUT,
				workspaceTweenDuration, 1, 0f, 0f);
	}

	public void stopDragS3Effect(boolean is_delete) {
		workspace.startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT,
				workspaceTweenDuration, 1f, 1f, 0f);
		workspace.setUser(1f);
		s3EffectTween = workspace
				.startTween(View3DTweenAccessor.USER, Cubic.OUT,
						workspaceTweenDuration, 0f, 0f, 0f)
				.setUserData(DRAG_END).setCallback(this);

	}

	public void startDragScrollIndicatorEffect() {
		if (ClingManager.getInstance().folderClingFired)
			SendMsgToAndroid.sendRefreshClingStateMsg();

		Workspace3D.is_longKick = true;
		workspace.startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT,
				workspaceTweenDuration, s3EffectScale, s3EffectScale, 0f);
		workspace.setUser2(0);
		workspace.startTween(View3DTweenAccessor.USER2, Cubic.OUT,
				workspaceTweenDuration, 1, 0f, 0f);
	}

	public void stopDragScrollIndicatorEffect(boolean is_delete) {
		workspace.startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT,
				workspaceTweenDuration, 1f, 1f, 0f);
		workspace.setUser2(1f);
		s4ScrollEffectTween = workspace
				.startTween(View3DTweenAccessor.USER2, Cubic.OUT,
						workspaceTweenDuration, 0f, 0f, 0f)
				.setUserData(DRAG_END).setCallback(this);
	}

	private void launcherGlobalSearch() {
		Intent intent = new Intent();
		intent.setAction("android.search.action.GLOBAL_SEARCH");
		List<ResolveInfo> allMatches = launcher.getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);

		// intent.setComponent(new ComponentName("com.baidu.searchbox",
		// "com.baidu.searchbox.MainActivity" ));

		if (allMatches == null || allMatches.size() == 0) {
			return;
		} else {
			String pkgName = "com.baidu.searchbox";
			for (ResolveInfo ri : allMatches) {
				if (ri.activityInfo.applicationInfo.packageName.equals(pkgName)) {
					ComponentName com = new ComponentName(
							ri.activityInfo.applicationInfo.packageName,
							ri.activityInfo.name);
					intent.setComponent(com);
					break;
				}
			}
		}

		iLoongLauncher.getInstance().startActivity(intent);
	}

	@Override
	public boolean onCtrlEvent(View3D sender, int event_id) {
		if (sender instanceof HotSeat3D) {
			switch (event_id) {
			// case HotSeat3D.MSG_MOVETO_HOTBUTTON:
			// //HotMainMenuView3D.onFocus=true;
			// break;
			case CellLayout3D.MSG_REFRESH_PAGE:
				Log.v("focus", "CellLayout3D.MSG_REFRESH_PAGE");
				Object refreshObj = sender.getTag();
				if (refreshObj instanceof Integer) {
					int tag = (Integer) refreshObj;
					CellLayout3D layout = this.workspace.getCurrentCellLayout();
					// layout.refreshLocation(tag);
				}
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
		if (sender instanceof DragSource3D) {
			DragSource3D source = (DragSource3D) sender;
			switch (event_id) {
			case DragSource3D.MSG_START_DRAG:
				if (!pageContainer.isVisible()) {
					// teapotXu add start for Folder in Mainmenu
					if (DefaultLayout.mainmenu_folder_function == true
							&& appHost.getAppList3DMode() == AppList3D.APPLIST_MODE_UNINSTALL) {
						// 在AppList中不需要S3 Effect
					} else
						// teapotXu add end for Folder in Mainmenu
						startDragS3Effect();
				}
				Object obj = source.getTag();
				if (!(obj instanceof Vector2))
					break;
				dragLayer.show();
				Vector2 vec = (Vector2) obj;
				ArrayList<View3D> list = source.getDragList();
				for (View3D view : list) {
					if (view instanceof DropTarget3D) {
						dragLayer.removeDropTarget((DropTarget3D) view);
					}
				}

				/************************ added by zhenNan.ye begin ***************************/
				if (DefaultLayout.enable_particle) {
					if (ParticleManager.particleManagerEnable) {
						if (sender instanceof Workspace3D
								|| sender instanceof AppList3D) {
							float positionX, positionY;
							View3D startDragView = list.get(0);
							if (list.size() > 1) {
								float iconHeight = Utils3D.getIconBmpHeight();
								positionX = vec.x + startDragView.width / 2;
								positionY = vec.y
										+ (startDragView.height - iconHeight)
										+ iconHeight / 2;
							} else {
								if (startDragView instanceof WidgetView /*
																		 * ||
																		 * startDragView
																		 * instanceof
																		 * Widget3D
																		 */
										|| startDragView instanceof Widget) {
									positionX = vec.x + startDragView.width / 2;
									positionY = vec.y + startDragView.height
											/ 2;
								} else {
									float iconHeight = Utils3D
											.getIconBmpHeight();
									positionX = vec.x + startDragView.width / 2;
									positionY = vec.y
											+ (startDragView.height - iconHeight)
											+ iconHeight / 2;
								}
							}
							startDragView
									.startParticle(
											ParticleManager.PARTICLE_TYPE_NAME_START_DRAG,
											positionX, positionY);
						}
					}
				}
				/************************ added by zhenNan.ye end ***************************/

				dragLayer.startDrag(list, vec.x, vec.y);

				// teapotXu add start for Folder in Mainmenu
				if (DefaultLayout.mainmenu_folder_function == true) {
					// init this flag
					dragLayer.is_dragging_in_apphost = false;
				}
				// teapotXu add end for Folder in Mainmenu
				if (sender instanceof Workspace3D
						|| sender instanceof HotSeat3D) {
					appHost.hide();
					pageContainer.hide();
					if (!workspace.isVisible())
						workspace.show();
					// if
					// (DefaultLayout.mainmenu_pos==HotGridView3D.MAINMENU_MIDDLE)
					// {
					trashIcon.show();
					//
					// }
					if (sender instanceof HotSeat3D) {
						hotseatBar.startMainGroupOutDragAnim();
					}
					// hotButton.changeState(HotMainMenuView3D.STATE_HOME);
				} else if (sender instanceof AppList3D) {

					AppList3D appList3D_sender = (AppList3D) sender;

					// teapotXu add start for Folder in Mainmenu
					if (DefaultLayout.mainmenu_folder_function == true
							&& appHost.isVisible() == true
							&& appHost.getAppList3DMode() == AppList3D.APPLIST_MODE_UNINSTALL) {
						// this condition the Icon will drag in AppHost
						dragLayer.is_dragging_in_apphost = true;

						// appHost must be added before FolderIcons which belong
						// to Applist
						ArrayList<DropTarget3D> dropTagList = dragLayer
								.getDropTargetList();
						if (dropTagList != null) {
							boolean is_dropTagList_including_Mainmenu_folder = false;
							for (int index = 0; index < dropTagList.size(); index++) {
								DropTarget3D dropTag = dropTagList.get(index);

								if (dropTag instanceof FolderIcon3D
										&& ((FolderIcon3D) dropTag).getParent() instanceof GridView3D) {
									dragLayer.addDropTargetBefore(dropTag,
											(DropTarget3D) appHost);
									is_dropTagList_including_Mainmenu_folder = true;
									break;
								}
							}
							if (is_dropTagList_including_Mainmenu_folder == false) {
								dragLayer.addDropTarget((DropTarget3D) appHost);
							}
						}
					} else
					// teapotXu add end for Folder in Mainmenu
					{
						if (DefConfig.DEF_NEW_SIDEBAR == true) {
							// showPageContainer(-1);
							appHost.hide();
							pageContainer.hide();
							workspace.show();
							hotseatBar.show();

							hotseatBar.setVisible(true);
							hotseatBar.touchable = true;
							if (DefaultLayout.hotseat_style_ex == false) {
								hotseatBar.startTween(
										View3DTweenAccessor.SCALE_XY,
										Cubic.OUT, workspaceTweenDuration,
										s3EffectScale, s3EffectScale, 0f);
							}
							trashIcon.show();
							if (DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP) {
							} else {

								trashIcon.startTween(
										View3DTweenAccessor.POS_XY, Cubic.OUT,
										0.3f, trashIcon.x, 0, 0);
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
				} else if (sender instanceof PageEdit3D) {
					// trashIcon.show();
				} else if (sender instanceof Folder3D) {
					// Folder3D.getInstance().DealButtonOKDown();
				} else {
					if (DefConfig.DEF_NEW_SIDEBAR == false) {
						if (sender instanceof SideBar && appHost.isVisible()) {
							if (!sidebar.isIconListShow()) {
								showPageContainer(-1);
							}
						}
					}
				}

				// if(!pageContainer.isVisible() && !folderOpened)
				// dragLayer.setBorder(true);
				SendMsgToAndroid.vibrator(R3D.vibrator_duration);
				return true;
			}
		}
		if (sender instanceof Icon3D) {
			Icon3D icon = (Icon3D) sender;
			switch (event_id) {
			case Icon3D.MSG_ICON_CLICK:
				ItemInfo tag = icon.getItemInfo();
				if (tag instanceof ShortcutInfo) {
					// Open shortcut
					workspace.setCurIcon(icon);
					Vector2 IconPoint = new Vector2();
					final Intent intent = ((ShortcutInfo) tag).intent;
					icon.toAbsoluteCoords(IconPoint);
					Rect rect = new Rect(
							(int) IconPoint.x,
							(int) (Utils3D.getScreenHeight() - IconPoint.y - icon
									.getHeight()), (int) (IconPoint.x + icon
									.getWidth()), (int) (Utils3D
									.getScreenHeight() - IconPoint.y));

					intent.setSourceBounds(rect);

					if (intent.getAction() != null
							&& intent.getAction().equals(
									Intent.ACTION_CREATE_SHORTCUT)) {
						SendMsgToAndroid.sendSelectShortcutMsg(intent);
					} else if (intent.getAction() != null
							&& intent
									.getAction()
									.equals("com.android.contacts.action.QUICK_CONTACT")) {
						ContactsContract.QuickContact.showQuickContact(
								launcher, rect, intent.getData(),
								ContactsContract.QuickContact.MODE_SMALL, null);
					} else {
						if (tag.container == ItemInfo.NO_ID
								&& intent.getComponent() != null
								&& intent.getComponent().getPackageName()
										.equals(launcher.getPackageName())
								&& intent
										.getComponent()
										.getClassName()
										.equals(launcher.getComponentName()
												.getClassName())) {
							if (DefaultLayout.mainmenu_inout_no_anim) {
								showWorkSpaceFromAllAppEx();
							} else {
								showWorkSpaceFromAllApp();
							}
						} else if (tag.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT
								&& intent.getComponent() != null
								&& intent.getComponent().getPackageName()
										.equals(launcher.getPackageName())
								&& intent
										.getComponent()
										.getClassName()
										.equals(launcher.getComponentName()
												.getClassName())) {
							showPageContainer(0);
						}

						else {
							SendMsgToAndroid.startActivity(tag);

						}

					}
				}
				break;

			}
			return true;
		}

		if (sender instanceof DragLayer3D) {
			DragLayer3D dragLayer = (DragLayer3D) sender;
			switch (event_id) {
			case DragLayer3D.MSG_DRAG_INBORDER:
				// teapotXu add start for Folder in Mainmenu
				if (DefaultLayout.mainmenu_folder_function == true) {
					if (appHost.isVisible() == true
							&& dragLayer.is_dragging_in_apphost == true) {
						appHost.onCtrlEvent(sender,
								DragLayer3D.MSG_DRAG_INBORDER);
					} else
						showPageContainer(-1);
				} else
					// teapotXu add end for Folder in Mainmenu
					showPageContainer(-1);
				break;
			case DragLayer3D.MSG_DRAG_OVER: {
				dragLayer.setColor(1f, 1f, 1f, 0.8f);
				// teapotXu add start for Folder in Mainmenu
				if (DefaultLayout.mainmenu_folder_function == true) {
					dragLayer.is_dragging_in_apphost = false;
				}
				// teapotXu add end for Folder in Mainmenu
				if (workspace.isVisible() == true) {
					DropTarget3D target = (DropTarget3D) dragLayer.getTag();
					// Log.d("launcher", "target="+target);
					if (target instanceof FolderIcon3D) {
						dragLayer.setColor(0f, 1f, 0f, 1f);
					}
					if (target instanceof TrashIcon3D) {
						dragLayer.setColor(1f, 0f, 0f, 0.5f);
						trashIcon.set(true);
					} else {
						trashIcon.set(false);
					}
					if (!(target instanceof Workspace3D)) {
						workspace.onDragOverLeave();
					}
					if (!(target instanceof HotSeat3D)) {
						hotseatBar.onDragOverLeave();
					}
				} else {
					// teapotXu add start for Folder in Mainmenu
					if (DefaultLayout.mainmenu_folder_function == true) {
						if (appHost.isVisible() == true
								&& appHost.getAppList3DMode() == AppList3D.APPLIST_MODE_UNINSTALL) {
							// appHost is shown
							DropTarget3D target = (DropTarget3D) dragLayer
									.getTag();

							dragLayer.is_dragging_in_apphost = true;

							if (!(target instanceof AppHost3D)) {
								appHost.onDropLeave();
							}
							return true;
						}
					}
					// teapotXu add end for Folder in Mainmenu

					if (DefConfig.DEF_NEW_SIDEBAR == true) {

					} else {
						if (sidebar.isVisible()) {
							DropTarget3D target = (DropTarget3D) dragLayer
									.getTag();
							if (target instanceof SideBar) {
								// ;
							}
						}
					}
				}
				return true;
			}
			case DragLayer3D.MSG_DRAG_END:
				// zhujieping add
				if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
						|| DefaultLayout.miui_v5_folder
						&& DefaultLayout.blur_enable)
					isDragEnd = false;
				DropTarget3D target = dragLayer.getTargetOver();
				// teapotXu add start for Folder in Mainmenu
				if (DefaultLayout.mainmenu_folder_function == true) {
					// when drag end, reset this flag
					dragLayer.is_dragging_in_apphost = false;
				}
				// teapotXu add end for Folder in Mainmenu

				// Log.e("test", "target:1 " + target);
				// if (target instanceof TrashIcon3D &&
				// getNotEmptyFolder().size()>0)
				if (target instanceof TrashIcon3D) {
					// SendMsgToAndroid.sendPopDeleteFolderDialogMsg();

					is_delete = true;
				} else {
					is_delete = false;
				}
				if (pageContainer.isVisible()) {
					workspace.setScale(1, 1);
					hotseatBar.setScale(1, 1);
					Workspace3D.is_longKick = false;
					if (pageContainer.pageEdit.isVisible()) {
						dragLayer.onDrop();
						dragLayer.removeAllViews();
						dragLayer.hide();
					} else {
						if (pageContainer.pageSelect.animatingShow)
							pageContainer.pageSelect.needHide = true;
						else
							pageContainer.hide(true);
					}
				}
				// teapotXu add start for Folder in Mainmenu
				else if (DefaultLayout.mainmenu_folder_function == true
						&& appHost.isVisible()
						&& appHost.getAppList3DMode() == AppList3D.APPLIST_MODE_UNINSTALL) {
					// when folder is not open,
					if (!appHost.folderOpened) {
						DropTarget3D dropTarget = dragLayer.onDrop();

						// 如果此时拖动的icon放置到AppList里边的Folder时
						Log.v("cooee",
								"Root3D ---- MSG_DRAG_END --- drop in Mainmenu ---");

						if (dropTarget != null) {
							// 如果此时拖动的icon放置到AppList里边的Folder时
							if (dropTarget instanceof FolderIcon3D
									&& ((View3D) dropTarget).getParent() instanceof GridView3D) {
								// if(target != null && !(target instanceof
								// FolderIcon3D))
								// {
								// Log.e("cooee","------error--------test -");
								// }
								// 此时需要在AppList中Remove掉拖动到文件夹里边的ICON，并重新排序
								if (((FolderIcon3D) dropTarget).getOnDrop() == true) {
									Log.v("cooee",
											"Root3D ---- MSG_DRAG_END --- move icon into folder in Mainmenu --- ");

									ArrayList<View3D> dragViewList = dragLayer
											.getDragList();
									appHost.removeDragViews(dragViewList);
								}
							} else {
								// in this condition dropTarget is always
								// AppHost, and it has been done in onDrop()
								Log.v("cooee",
										"Root3D ---- MSG_DRAG_END --- move icon in Mainmenu --- dropTarget: "
												+ dropTarget.toString());
							}
						} else {
							// error condition
							// if in this condition, add back the views in
							// draglist
							Log.e("cooee",
									"Root3D ---- MSG_DRAG_END --- drop in Mainmenu  target == null ---error---");
						}
						appHost.setDragviewAddFromFolderStatus(false);
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
				else {
					appHost.hide();
					// Log.e("test", "target:2 " + target);
					if (!folderOpened) {
						// Log.e("test", "target:3 " + target);
						workspace.show();
						if (dragLayer.getDragList().size() > 0) {
							DropTarget3D tmpTarget = dragLayer.onDrop();
							// Log.e("test", "target:3-1 tmpTarget " +
							// tmpTarget);
							if (tmpTarget == null) {
								// Log.e("test", "target:3-1 " + target);
								hotseatBar.getDockGroup()
										.removeVirtueFolderIcon();
								dropListBacktoOrig(dragLayer.getDragList());
							} else {
								// Log.e("test", "target:3-2 " + target);
								if (target instanceof HotSeat3D
										&& !target.equals(tmpTarget)) {
									hotseatBar.getDockGroup()
											.removeVirtueFolderIcon();
								}
							}
						} else if (dragLayer.getDragList().size() > 0
								&& target instanceof HotSeat3D) {
							DropTarget3D tmpTarget = dragLayer.onDrop();
							// Log.e("test", "tmpTarget:4 " + tmpTarget);
							if (!(tmpTarget instanceof HotSeat3D)) {
								// 防止快速仍到底边栏后生成文件夹然后ondrop却为桌面，导致底边栏生成的文件夹没删�?
								// Log.e("test", "target:4 " + target);
								hotseatBar.getDockGroup()
										.removeVirtueFolderIcon();
							}
						}
						dragLayer.removeAllViews();
						dragLayer.hide();
						if (DefaultLayout.hotseat_style_ex) {
							hotseatBar.show();
						}
						pageIndicator.show();

						stopDragS3Effect(is_delete);

					}
					if (DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP) {
						// Log.e("test", "target:5 " + target);
						trashIcon.hide();
					}

				}
				return true;
			}
		}

		if (sender instanceof AppList3D) {
			switch (event_id) {
			case AppList3D.APP_LIST3D_KEY_BACK:
				if (DefaultLayout.mainmenu_inout_no_anim) {
					showWorkSpaceFromAllAppEx();
				} else {
					showWorkSpaceFromAllApp();
				}
				return true;
			case AppList3D.APP_LIST3D_SHOW:
				if (DefaultLayout.mainmenu_inout_no_anim) {
					pageIndicator.hideEx();
				} else {
					pageIndicator.hide();
				}
				return true;
			case AppList3D.APP_LIST3D_HIDE:
				if (DefaultLayout.mainmenu_inout_no_anim) {
					pageIndicator.showEx();
				} else {
					pageIndicator.show();
				}
				return true;
				// teapotXu add start for Folder in Mainmenu
			case AppList3D.MSG_ADD_DRAGLAYER:
				if (DefaultLayout.mainmenu_folder_function == true) {
					AppList3D appList3D_sender = (AppList3D) sender;
					dragLayer
							.addDropTarget((DropTarget3D) appList3D_sender.drageTarget_new_child);
					return true;
				}
				break;
			// teapotXu add end for Folder in Mainmenu

			}
		}

		if (sender instanceof HotGridView3D) {
			HotGridView3D hotGrid = (HotGridView3D) sender;
			switch (event_id) {
			case HotGridView3D.MSG_ADD_DRAGLAYER:
				dragLayer.addDropTarget((DropTarget3D) hotGrid.getTag());
				return true;
			}
		}

		if (sender instanceof Workspace3D) {
			Workspace3D workspace = (Workspace3D) sender;
			switch (event_id) {
			case Workspace3D.MSG_FINISH_EFFECT:
				pageIndicator.finishAutoEffect();
				break;
			case Workspace3D.MSG_LONGCLICK: {
				Object obj = workspace.getTag();
				if (obj instanceof Vector2)
					SendMsgToAndroid.sendAddShortcutMsg(
							(int) ((Vector2) obj).x, (int) ((Vector2) obj).y);
				else
					SendMsgToAndroid.sendAddShortcutMsg(0, 0);
				SendMsgToAndroid.vibrator(R3D.vibrator_duration);
				return true;
			}
			case Workspace3D.MSG_ADD_DRAGLAYER:
				dragLayer.addDropTargetBefore(pageContainer.pageEdit,
						(DropTarget3D) workspace.getTag());
				return true;
			case Workspace3D.MSG_DEL_POP_ALL:
				trashIcon.show();
				timeline = Timeline.createParallel();
				ArrayList<View3D> toDeleteList = (ArrayList<View3D>) workspace
						.getTag();
				for (int i = 0; i < toDeleteList.size(); i++) {
					View3D view = toDeleteList.get(i);
					timeline.push(view.obtainTween(View3DTweenAccessor.POS_XY,
							Cubic.OUT, 0.5f, Utils3D.getScreenWidth() / 2
									+ trashIcon.getWidth() / 4,
							Utils3D.getScreenHeight(), 0));
				}
				trashIcon.setTag(toDeleteList);
				timeline.setCallback(this).start(View3DTweenAccessor.manager);
				return true;
			case Workspace3D.MSG_ADD_WIDGET:
				Widget2DShortcut widget = (Widget2DShortcut) workspace.getTag();
				if (widget.getTag() instanceof int[]) {
					int[] pos = (int[]) widget.getTag();
					widget.x = pos[0];
					widget.y = pos[1];
				}
				SendMsgToAndroid.addWidgetFromAllApp(
						widget.widgetInfo.component, (int) widget.x,
						(int) widget.y);
				// launcher.addAppWidgetFromDrop(widget.widgetInfo.component,
				// new int[]{(int) widget.x,(int) widget.y});
				return true;
			case Workspace3D.MSG_ADD_SHORTCUT:
				Widget2DShortcut shortcut = (Widget2DShortcut) workspace
						.getTag();
				int[] pos = (int[]) shortcut.getTag();
				SendMsgToAndroid.addShortcutFromAllApp(
						shortcut.widgetInfo.component, pos[0], pos[1]);
				// launcher.addAppWidgetFromDrop(widget.widgetInfo.component,
				// new int[]{(int) widget.x,(int) widget.y});
				return true;
			case Workspace3D.MSG_PAGE_SHOW_EDIT:
				if (workspace.scaleX == 1.0f && dragLayer.isVisible() == false
						&& workspace.xScale == 0) {
					showPageEdit();
				}
				return true;
			case Workspace3D.MSG_CLICK_SPACE: {
				Object objclick = workspace.getTag();
				if (objclick instanceof Vector2) {
					SendMsgToAndroid.sendClickToWallPaperMsg(
							(int) ((Vector2) objclick).x,
							(int) ((Vector2) objclick).y);
				}
				return true;
			}
			case Workspace3D.MSG_GLOBAL_SEARCH: {
				launcherGlobalSearch();
				return true;
			}
			}

		}

		if (sender instanceof PageIndicator3D) {
			switch (event_id) {

			case PageIndicator3D.PAGE_INDICATOR_CLICK:
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
				showPageContainer(-1);
				return true;
			}
		}

		if (sender instanceof PageContainer3D) {
			switch (event_id) {
			case PageContainer3D.MSG_PAGE_CONTAINER_HIDE:
				workspace.setCurrentPage(pageContainer.getSelectedIndex());
				appHost.hide();
				workspace.show();
				hotseatBar.show();

				if (DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP) {
					trashIcon.hide();
					trashIcon.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT,
							0.3f, trashIcon.x, 0, 0);
				}
				if (dragLayer.isVisible()) {
					if (dragLayer.draging) {
						trashIcon.show();
						if (DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP) {
							trashIcon.startTween(View3DTweenAccessor.POS_XY,
									Cubic.OUT, 0.3f, trashIcon.x, 0, 0);
						} else {
							// hotButton.show();
						}
						// hotbgView.startTween(View3DTweenAccessor.POS_XY,
						// Cubic.OUT,0.3f, 0, 0, 0);
					} else {
						workspace
								.forceSetCellLayoutDropType(pageContainer.pageSelect.currentIndex);
						if (dragLayer.getDragList().size() > 0
								&& dragLayer.onDrop() == null) {
							dropListBacktoOrig(dragLayer.getDragList());
						}
						dragLayer.removeAllViews();
						dragLayer.hide();
						// hotButton.setPosition(hotButton.x,
						// -hotButton.height*1.5f);
						if (DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP) {
							// hotButton.setScale(1.0f, 1.0f);
						} else {
							// trashIcon.hide();
							// trashIcon.setPosition(trashIcon.x,
							// -hotButton.height*1.5f);
							// trashIcon.startTween(View3DTweenAccessor.POS_XY,
							// Cubic.OUT,0.3f, trashIcon.x, 0, 0);
						}
						// hotButton.show();

					}
				} else {
					// zhujieping add
					if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
							|| DefaultLayout.miui_v5_folder) {
						if (workspace.getScaleX() < 1
								|| workspace.getScaleY() < 1) {
							workspace.setScale(1.0f, 1.0f);
						}
					}
					// hotButton.show();
					SendMsgToAndroid.sendShowWorkspaceMsg();
				}

				if (!pageIndicator.isVisible())
					pageIndicator.show();
				workspace.setScaleZ(1f);
				return true;
			case PageContainer3D.MSG_PAGE_SET_HOME:
				workspace.setHomePage(pageContainer.getHomePage());
				return true;
			case PageContainer3D.MSG_PAGE_APPEND_PAGE:
				CellLayout3D cell = new CellLayout3D("celllayout");
				workspace.addPage(cell);
				SendMsgToAndroid.sendAddWorkspaceCellMsg(-1);
				// pageContainer.tmpCell = cell;
				pageIndicator.setPageNum(workspace.getPageNum());
				return true;
			case PageContainer3D.MSG_PAGE_REMOVE_CELL:
				pageIndicator.setPageNum(workspace.getPageNum());
				Object obj = pageContainer.getTag();
				if (obj != null) {
					trashIcon.onDrop((ArrayList<View3D>) obj,
							Utils3D.getScreenWidth(), Utils3D.getScreenHeight()
									- trashIcon.height);
				}
				return true;
			case PageContainer3D.MSG_PAGE_ADD_CELL:
				pageIndicator.setPageNum(workspace.getPageNum());
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
		if (DefConfig.DEF_NEW_SIDEBAR == false) {
			if (sender instanceof SideBar) {
				SideBar sidebar = (SideBar) sender;
				switch (event_id) {

				case SideBar.MSG_ON_DROP:
					View3D view = (View3D) sidebar.getTag();
					Vector2 vec2 = new Vector2();
					view.toAbsoluteCoords(vec2);
					ArrayList<View3D> tmpList = new ArrayList<View3D>();
					tmpList.add(view);
					workspace.onDrop(tmpList, vec2.x, vec2.y);
					return true;
					// case SideBar.MSG_LONGCLICK_INAPPLIST:
					// return showPageContainer(-1);
				}

			}
		}

		if (sender instanceof HotSeat3D) {
			HotSeat3D sidebar = (HotSeat3D) sender;
			switch (event_id) {
			case HotSeat3D.MSG_VIEW_START_MAIN:
				if (DefaultLayout.mainmenu_inout_no_anim) {
					showAllAppFromWorkspaceEx();
				} else {
					showAllAppFromWorkspace();
				}
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
				View3D view = (View3D) sidebar.getTag();
				Vector2 vec2 = new Vector2();
				view.toAbsoluteCoords(vec2);
				ArrayList<View3D> tmpList = new ArrayList<View3D>();
				tmpList.add(view);
				sidebar.setTag(null);
				workspace.onDrop(tmpList, vec2.x, vec2.y);
				return true;
				// case SideBar.MSG_LONGCLICK_INAPPLIST:
				// return showPageContainer(-1);
			}

		}

		if (sender instanceof FolderIcon3D) {
			final FolderIcon3D folderIcon3D = (FolderIcon3D) sender;

			switch (event_id) {

			case FolderIcon3D.MSG_FOLDERICON_BACKTO_ORIG:
				ArrayList<View3D> child_list = (ArrayList<View3D>) folderIcon3D
						.getTag();
				folder_DropList_backtoOrig(child_list);
				return true;
			case FolderIcon3D.MSG_FOLDERICON_TO_ROOT3D:
				// zhujieping add
				if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
						|| DefaultLayout.miui_v5_folder && !isDragon) {
					return dealMIUIFolderToRoot3D(folderIcon3D);
				}
				appHost.hide();
				if (DefConfig.DEF_NEW_SIDEBAR == true) {
					hotseatBar.hide();
					hotseatBar.getDockGroup().releaseFocus();
					hotseatBar.getMainGroup().releaseFocus();
					// hotButton.hide();
					if (DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP) {
						trashIcon.startTween(View3DTweenAccessor.POS_XY,
								Cubic.OUT, 0.3f, trashIcon.x,
								-trashIcon.height * 1.5f, 0);
					}
				} else {
					sidebar.hide();
				}
				workspace.releaseFocus();
				workspace.hide();
				pageIndicator.hide();
				addView(folderIcon3D);
				dragLayer.removeDropTarget(folderIcon3D);
				folderOpened = true;
				folder = folderIcon3D;
				return true;
			case FolderIcon3D.MSG_FOLDERICON_TO_CELLLAYOUT:
				// appList.show();
				// zhujieping add start
				if ((ThemeManager.getInstance().getBoolean("miui_v5_folder") || DefaultLayout.miui_v5_folder)
						&& !isDragon
						&& !(DefaultLayout.mainmenu_folder_function == true && (folderIcon3D
								.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D && folderIcon3D
								.getExitToWhere() == FolderIcon3D.TO_CELLLAYOUT))) {
					dealMIUIFolderToCellLayout(folderIcon3D);
					return true;
				}
				// zhujieping add end
				Log.d("test12345", "MSG_FOLDERICON_TO_CELLLAYOUT");
				// this.touchable=true;
				Log.d("testdrag", " FolderIcon3D.MSG_FOLDERICON_TO_CELLLAYOUT");

				// teapotXu add start for Folder in Mainmenu
				if (DefaultLayout.mainmenu_folder_function == true) {
					// 如果此时是从主菜单的文件夹中拖动图标到Idle上，
					if (folderIcon3D.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D
							&& folderIcon3D.getExitToWhere() == FolderIcon3D.TO_CELLLAYOUT) {
						// 此处设置标志，使再次进入AppList的时候，重新刷新并排序，显示出文件夹
						appHost.hide();
						((AppList3D) appHost.appList).force_applist_refesh = true;
						// 同时把该Folder add入 dragLayer
						dragLayer.addDropTarget((DropTarget3D) folderIcon3D);
					}
				}
				// teapotXu add end for Folder in Mainmenu

				workspace.setScale(0f, 0f);
				workspace.show();
				boolean closeFolderByDrag = folderIcon3D.mFolder
						.getColseFolderByDragVal();
				if (DefConfig.DEF_NEW_SIDEBAR == true) {
					hotseatBar.show();

					if (DefaultLayout.hotseat_style_ex) {
						// hotButton.show();
					} else {
						if (!closeFolderByDrag) {
							// hotButton.show();
						}
					}
					if (DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP) {
						trashIcon.startTween(View3DTweenAccessor.POS_XY,
								Cubic.OUT, 0.3f, trashIcon.x, 0, 0);
					}
					if (folderIcon3D.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT) {
						hotseatBar.setTag2(null);

						// teapotXu add start for Fodler in Mainmenu
					} else if (DefaultLayout.mainmenu_folder_function == true
							&& folderIcon3D.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D) {
						// 如果此时是从主菜单的文件夹中拖动图标到Idle上，
						hotseatBar.setTag2(null);
						// teapotXu add end for Folder in Mainmenu

					} else {
						hotseatBar.setTag2(folderIcon3D);
					}
				}
				if (closeFolderByDrag && dragLayer.draging) {
					workspace.startTween(View3DTweenAccessor.SCALE_XY,
							Cubic.OUT, 0.3f, s3EffectScale, s3EffectScale, 0f)
							.setCallback(this);
				} else {
					Tween tmp = workspace.startTween(
							View3DTweenAccessor.SCALE_XY, Cubic.OUT, 0.3f, 1f,
							1f, 0f).setCallback(this);// todo：这里有可能收不到回调！！！！！
					Log.d("launcher", "MSG_FOLDERICON_TO_CELLLAYOUT:" + tmp);
				}
				workspace.setTag(folderIcon3D);
				hotseatBar.touchable = false;
				workspace.touchable = false;
				if (folderIcon3D.getItemInfo().container == LauncherSettings.Favorites.CONTAINER_DESKTOP)
					workspace.getCurrentCellLayout().cellFindViewAndRemove(
							folderIcon3D.getItemInfo().cellX,
							folderIcon3D.getItemInfo().cellY);
				/*
				 * 避免这里的动画有时候收不到回调，启动定时器�? 定时器到时长度一定要大于这里启动动画的时长，保证动画的回调函数能跑到一次，
				 * 避免回调跑不到，root3d.touchable为False，触摸无法反应，定屏问题的解�? added by zfshi
				 * 2012-08-19
				 */
				startRootTimer((long) (workspaceTweenDuration * 4 * 1000));
				return true;
				// zhujieping add start
			case FolderIcon3D.MSG_WORKSPACE_ALPAH_TO_ONE: {
				if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
						|| DefaultLayout.miui_v5_folder) {
					resetWorkspace(folderIcon3D);
				}

				return true;
			}
			case FolderIcon3D.MSG_WORKSPACE_RECOVER: {
				if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
						|| DefaultLayout.miui_v5_folder) {
					if (folderIcon3D.getParent() == this) {
						if (folderIcon3D.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT) {
							workspace.addBackInScreen(folderIcon3D,
									folderIcon3D.mInfo.x, folderIcon3D.mInfo.y);
						} else if (folderIcon3D.getFromWhere() == FolderIcon3D.FROM_HOTSEAT) {
							folderIcon3D.setSize(R3D.workspace_cell_width,
									R3D.workspace_cell_height);
							ArrayList<View3D> tmp = new ArrayList<View3D>();
							tmp.add((View3D) folderIcon3D);
							hotseatBar.getDockGroup().backtoOrig(tmp);
							folderIcon3D.setFolderIconSize(0, 0,
									folderIcon3D.mInfo.x, folderIcon3D.mInfo.y);
						}

					}
				}

				return true;
			}
			// zhujieping add end
			case FolderIcon3D.MSG_UPDATE_VIEW:
				Object obj = folderIcon3D.getTag();
				String str = null;
				if (obj instanceof String) {
					str = (String) obj;
				}
				R3D.pack(str, folderIcon3D.titleToPixmap());
				launcher.postRunnable(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						R3D.packer.updateTextureAtlas(R3D.packerAtlas,
								TextureFilter.Linear, TextureFilter.Linear);
						folderIcon3D.setTexture();
					}
				});
				return true;
			case FolderIcon3D.MSG_FOLDER_CLING_VISIBLE:
				return workspace.isVisible();
			}

		}
		if (sender instanceof Folder3D) {
			final Folder3D folder3D = (Folder3D) sender;
			switch (event_id) {
			case Folder3D.MSG_ON_DROP:
				View3D view = (View3D) folder3D.getTag();
				Vector2 vec2 = new Vector2();
				view.toAbsoluteCoords(vec2);
				ArrayList<View3D> tmpList = new ArrayList<View3D>();
				tmpList.add(view);
				workspace.onDrop(tmpList, vec2.x, vec2.y);
				return true;
			case Folder3D.MSG_UPDATE_VIEW:
				launcher.postRunnable(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						folder3D.onInputNameChanged();
					}
				});
				return true;
			}

		}
		
		if (sender instanceof FolderMIUI3D) {
			final FolderMIUI3D folder3D = (FolderMIUI3D) sender;
			switch (event_id) {
			case FolderMIUI3D.MSG_UPDATE_VIEW:
				launcher.postRunnable(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						folder3D.onInputNameChanged();
					}
				});
				return true;
			}
		}
		
		if (sender instanceof TrashIcon3D) {
			TrashIcon3D trash = (TrashIcon3D) sender;
			switch (event_id) {
			case TrashIcon3D.MSG_TRASH_DELETE:
				List<?> viewList = (List<?>) trash.getTag();
				for (int i = 0; i < viewList.size(); i++) {
					if (viewList.get(i) instanceof Widget3D) {
						Widget3D widget3D = (Widget3D) viewList.get(i);
						Widget3DManager.getInstance().deleteWidget3D(widget3D);
					} else if (viewList.get(i) instanceof Widget) {
						SendMsgToAndroid.deleteSysWidget((Widget) viewList
								.get(i));
						Widget widget = (Widget) viewList.get(i);
						widget.dispose();
					}

					// xiatian add start //Widget adaptation
					// "com.android.gallery3d"
					if (viewList.get(i) instanceof PageScrollListener) {
						workspace
								.removeScrollListener((PageScrollListener) viewList
										.get(i));
					}
					// xiatian add end

					if (viewList.get(i) instanceof DropTarget3D) {

						dragLayer.removeDropTarget((DropTarget3D) viewList
								.get(i));

					}
					if (viewList.get(i) instanceof FolderIcon3D) {

						FolderIcon3D folderIcon = (FolderIcon3D) viewList
								.get(i);
						UserFolderInfo item = (UserFolderInfo) folderIcon
								.getItemInfo();
						// LauncherModel.deleteUserFolderContentsFromDatabase(launcher,
						// item);
						// Root3D.deleteFromDB(info)
						for (int j = 0; j < folderIcon.getChildCount(); j++) {
							View3D myView = folderIcon.getChildAt(j);
							if (myView instanceof Icon3D) {
								ItemInfo iconItem = ((Icon3D) myView)
										.getItemInfo();
								Root3D.deleteFromDB(iconItem);
							}
						}
						launcher.removeFolder(item);

					}
				}
				viewList.clear();
				return true;
			}
		}

		switch (event_id) {
		case MSG_SET_WALLPAPER_OFFSET:
			// Log.e("launcher", "MSG_SET_WALLPAPER_OFFSET");
			final IBinder token = launcher.getWindow().getCurrentFocus()
					.getWindowToken();
			final NPageBase page = (NPageBase) sender;
			if (token != null) {
				launcher.postRunnable(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (!DefaultLayout.disable_move_wallpaper) {
							mWallpaperManager.setWallpaperOffsetSteps(
									1.0f / (page.getPageNum() - 1), 0);
							float offset = page.getTotalOffset();
							// Log.e("test", "offset:" +
							// offset+" index:"+page.getCurrentPage());
							mWallpaperManager.setWallpaperOffsets(token,
									offset, 0);
						}
					}
				});

			}
			// mWallpaperManager.setWallpaperOffsets(windowToken, xOffset,
			// yOffset)
			return true;
		}
		return false;
	}

	private boolean showPageEdit() {
		if (pageContainer.pageEdit != null
				&& pageContainer.pageEdit.isAnimating())
			return true;
		if (!pageContainer.isVisible()) {
			workspace.setScaleZ(0.1f);
			int selectedPage = workspace.getCurrentScreen();

			appHost.hide();

			if (DefConfig.DEF_NEW_SIDEBAR == true) {
				hotseatBar.hide();
			} else {
				sidebar.hide();
			}
			workspace.hide();
			workspace.releaseFocus();
			pageIndicator.hide();
			trashIcon.hide();

			pageContainer.setupPageEdit(workspace.getViewList(), selectedPage,
					workspace.getHomePage());
			pageContainer.show();
			return true;
		} else {
			if (pageContainer.pageMode == PageContainer3D.PAGE_MODE_SELECT)
				pageContainer.changeMode();
		}
		return true;
	}

	public void showWorkSpaceFromAllApp() {
		if (workspaceAndAppTween != null && workspaceAndAppTween.isStarted()) {
			return;
		}
		if (DefConfig.DEF_NEW_SIDEBAR == true) {
			hotseatBar.showDelay(workspaceTweenDuration);
		}
		// workspace.setColor(new Color(workspace.color.r, workspace.color.g,
		// workspace.color.b, 0));
		workspaceAndAppTween = Timeline.createParallel();
		workspace.color.a = 0;
		workspaceAndAppTween.push(workspace.obtainTween(
				View3DTweenAccessor.OPACITY, Cubic.OUT, workspaceTweenDuration,
				1, 0, 0).delay(workspaceTweenDuration));
		workspace.setUser(Utils3D.getScreenHeight() / 5);
		workspaceAndAppTween.push(workspace.obtainTween(
				View3DTweenAccessor.USER, Cubic.OUT, workspaceTweenDuration, 0,
				0, 0).delay(workspaceTweenDuration));
		// workspace.setRotationVector(1, 0, 0);
		// workspace.setRotation(-30);
		workspace.setScale(0.9f, 0.9f);
		workspaceAndAppTween.push(workspace.obtainTween(
				View3DTweenAccessor.SCALE_XY, Cubic.OUT,
				workspaceTweenDuration, 1, 1, 0).delay(workspaceTweenDuration));
		workspace.show();

		NPageBase contentList = appHost.getContentList();
		contentList.setUser(0);
		Color c = contentList.indicatorView.getColor();
		contentList.indicatorView.setColor(c.r, c.g, c.b, 0);
		workspaceAndAppTween.push(contentList.obtainTween(
				View3DTweenAccessor.USER, Cubic.IN, workspaceTweenDuration,
				-contentList.height / 5, 0, 0));
		workspaceAndAppTween.push(contentList.obtainTween(
				View3DTweenAccessor.OPACITY, Cubic.IN, workspaceTweenDuration,
				0, 0, 0));
		// appHost.appList.setRotationVector(1, 0, 0);
		// appHost.appList.setRotation(0);
		workspaceAndAppTween.push(contentList.obtainTween(
				View3DTweenAccessor.SCALE_XY, Cubic.IN, workspaceTweenDuration,
				0.9f, 0.9f, 0));
		if (appHost.appBar != null) {
			appHost.appBar.color.a = 1;
			workspaceAndAppTween.push(appHost.appBar.obtainTween(
					View3DTweenAccessor.OPACITY, Cubic.IN,
					workspaceTweenDuration, 0, 0, 0));
		}
		workspaceAndAppTween.setCallback(this)
				.start(View3DTweenAccessor.manager).setUserData(0);
		Log.d("launcher", "showWorkSpaceFromAllApp");
		// appHost.hide();
		if (DefaultLayout.broadcast_state) {
			iLoongLauncher.getInstance().sendBroadcast(
					new Intent("com.cooee.launcher.action.show_workspace"));
		}
	}

	public void showWorkSpaceFromAllAppEx() {
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

	public void showAllAppFromWorkspace() {
		if ((workspaceAndAppTween != null && workspaceAndAppTween.isStarted())
				|| folderOpened) {
			return;
		}
		boolean hideHotbar = DefaultLayout.applist_style_classic ? false : true;
		appHost.show();
		pageIndicator.hide();
		if (DefConfig.DEF_NEW_SIDEBAR == true) {
			if (hideHotbar) {
				hotseatBar.hide();
				// hotButton.hide();
			}
			if (DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP) {
				trashIcon.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT,
						0.3f, trashIcon.x, -trashIcon.height * 1.5f, 0);
			}
		}
		workspaceAndAppTween = Timeline.createParallel();
		appHost.appList
				.setUser(-appHost.appList.height
						/ 5
						+ (DefaultLayout.applist_style_classic ? R3D.hot_obj_height
								: 0));
		workspaceAndAppTween.push(appHost.appList.obtainTween(
				View3DTweenAccessor.USER, Cubic.OUT, workspaceTweenDuration,
				(DefaultLayout.applist_style_classic ? R3D.hot_obj_height : 0),
				0, 0).delay(workspaceTweenDuration));
		appHost.appList.color.a = 0;
		workspaceAndAppTween.push(appHost.appList.obtainTween(
				View3DTweenAccessor.OPACITY, Cubic.OUT, workspaceTweenDuration,
				1, 0, 0).delay(workspaceTweenDuration));
		// appHost.appList.setRotationVector(1, 0, 0);
		// appHost.appList.setRotation(-30);
		appHost.appList.setScale(0.9f, 0.9f);
		workspaceAndAppTween.push(appHost.appList.obtainTween(
				View3DTweenAccessor.SCALE_XY, Cubic.OUT,
				workspaceTweenDuration, 1, 1, 0).delay(workspaceTweenDuration));
		if (appHost.appBar != null) {
			appHost.appBar.color.a = 0;
			workspaceAndAppTween.push(appHost.appBar.obtainTween(
					View3DTweenAccessor.OPACITY, Cubic.OUT,
					workspaceTweenDuration, 1, 0, 0).delay(
					workspaceTweenDuration));
		}

		workspace.setUser(0);
		workspaceAndAppTween.push(workspace.obtainTween(
				View3DTweenAccessor.USER, Cubic.IN, workspaceTweenDuration,
				Utils3D.getScreenHeight() / 5, 0, 0));
		workspace.color.a = 1;
		workspaceAndAppTween.push(workspace.obtainTween(
				View3DTweenAccessor.OPACITY, Cubic.IN, workspaceTweenDuration,
				0, 0, 0));
		// workspace.setRotationVector(1, 0, 0);
		workspaceAndAppTween.push(workspace.obtainTween(
				View3DTweenAccessor.SCALE_XY, Cubic.IN, workspaceTweenDuration,
				0.9f, 0.9f, 0));
		workspaceAndAppTween.setCallback(this)
				.start(View3DTweenAccessor.manager).setUserData(1);
		// hotButton.changeState(HotMainMenuView3D.STATE_APP);
		Messenger.sendMsg(Messenger.MSG_START_COVER_MTKWIDGET, 0, 0);
		SendMsgToAndroid.sendHideWorkspaceMsg();
		ClingManager.getInstance().cancelAllAppCling();
		if (DefaultLayout.broadcast_state) {
			iLoongLauncher.getInstance().sendBroadcast(
					new Intent("com.cooee.launcher.action.show_app"));
		}
		SendMsgToAndroid.sysPlaySoundEffect();
	}

	public void showAllAppFromWorkspaceEx() {
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

	private boolean showPageContainer(int enterPage) {

		if (!pageContainer.isVisible()
				&& !pageContainer.pageSelect.isAnimating()) {
			workspace.setScaleZ(0.1f);
			int selectedPage = 0;
			selectedPage = workspace.getCurrentScreen();
			hotseatBar.hide();
			appHost.hide();
			workspace.hide();
			pageIndicator.hide();
			pageContainer.show();
			pageContainer.setEnterPage(selectedPage);
			pageContainer.setupPageSelect(workspace.getViewList(),
					selectedPage, workspace.getHomePage());
			pageContainer.pageSelect.setCurrentCell(workspace
					.getCurrentScreen());
			if (DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP) {
				trashIcon.touchable = false;
				trashIcon.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT,
						0.3f, trashIcon.x, -trashIcon.height * 1.5f, 0);
			} else {
				trashIcon.hide();
			}
			// hotButton.hide();
			// hotbgView.startTween(View3DTweenAccessor.POS_XY,
			// Cubic.OUT,0.3f, 0.0f, -hotbg_posy*1.5f, 0);
			return true;
		}
		return false;
	}

	private void dealworkspaceTweenFinish() {

		if (workspace.touchable)
			return;
		Object view = workspace.getTag();
		stopRootTimer();
		if (view == null) {
			return;
		} else if (view instanceof FolderIcon3D) {
			FolderIcon3D folderIcon3D = (com.iLoong.launcher.Folder3D.FolderIcon3D) view;
			workspace.setScale(1, 1);
			if (DefConfig.DEF_NEW_SIDEBAR == true) {
				if (folderIcon3D.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT) {

					workspace.addBackInScreen(folderIcon3D,
							folderIcon3D.mInfo.x, folderIcon3D.mInfo.y);
				} else if (folderIcon3D.getFromWhere() == FolderIcon3D.FROM_HOTSEAT) {
					ArrayList<View3D> tmp = new ArrayList<View3D>();
					tmp.add((View3D) view);
					hotseatBar.getDockGroup().backtoOrig(tmp);
				}
			} else {
				workspace.addBackInScreen(folderIcon3D, folderIcon3D.mInfo.x,
						folderIcon3D.mInfo.y);
				// workspace.addInCurrenScreen(folderIcon3D,
				// folderIcon3D.mInfo.x,
				// folderIcon3D.mInfo.y);
			}

			pageIndicator.show();
			folderOpened = false;

			// teapotXu add start for Folder in Mainmenu
			if (DefaultLayout.mainmenu_folder_function == true) {
				if (appHost != null)
					appHost.folderOpened = false;
			}
			// teapotXu add end for Folder in Mainmenu

			if (folderIcon3D.getParent() == this)
				folderIcon3D.remove();
			folderIcon3D = null;
			if (dragLayer.isVisible() && !dragLayer.draging) {
				workspace.dropAnim = false;

				if (dragLayer.getDragList().size() > 0
						&& dragLayer.onDrop() == null) {
					dropListBacktoOrig(dragLayer.getDragList());
				}
				dragLayer.removeAllViews();
				dragLayer.hide();
				workspace.dropAnim = true;
				stopDragS3Effect(is_delete);
			}

			if (dragLayer.isVisible() && dragLayer.draging)
				trashIcon.show();
			else {

				if (DefConfig.DEF_NEW_SIDEBAR == false) {
					sidebar.show();
				}
				SendMsgToAndroid.sendShowWorkspaceMsg();
			}
			if (goHome) {
				goHome = false;
				workspace.scrollTo(workspace.getHomePage());
			}
			hotseatBar.touchable = true;
			workspace.touchable = true;
		}

	}

	@Override
	public void onEvent(int type, BaseTween source) {
		// TODO Auto-generated method stub
		if (source == workspaceAndAppTween && type == TweenCallback.COMPLETE
				&& workspaceAndAppTween.getUserData().equals(0)) {
			Log.d("launcher", "event:hide app");
			workspaceAndAppTween = null;
			appHost.hide();
			appHost.getContentList().setUser(0);
			appHost.getContentList().setRotation(0);
			appHost.getContentList().color.a = 1;
			appHost.getContentList().setScale(1, 1);
			if (appHost.appBar != null)
				appHost.appBar.color.a = 1;
			appHost.getContentList().stopTween();
			SendMsgToAndroid.sendShowWorkspaceMsg();
		} else if (source == workspaceAndAppTween
				&& type == TweenCallback.COMPLETE
				&& workspaceAndAppTween.getUserData().equals(1)) {
			Log.d("launcher", "event:hide workspace");
			workspaceAndAppTween = null;
			workspace.hide();
			workspace.setUser(0);
			workspace.setRotation(0);
			workspace.color.a = 1;
			workspace.setScale(1, 1);
		} else if (source == s3EffectTween && type == TweenCallback.COMPLETE) {
			if (s3EffectTween != null
					&& s3EffectTween.getUserData().equals(DRAG_END)) {
				Workspace3D.is_longKick = false;
				s3EffectTween = null;
				if (is_delete) {
					is_delete = false;
				}
			}
		} else if (source == s4ScrollEffectTween
				&& type == TweenCallback.COMPLETE) {
			if (s4ScrollEffectTween != null
					&& s4ScrollEffectTween.getUserData().equals(DRAG_END)) {
				Workspace3D.is_longKick = false;
				s4ScrollEffectTween = null;
				workspace.recoverPageSequence();
			}
		} else if (source == workspace.getTween()
				&& type == TweenCallback.COMPLETE) {
			dealworkspaceTweenFinish();
		} else if (source instanceof Timeline && trashIcon.getTag() != null) {
			trashIcon.onDrop((ArrayList<View3D>) trashIcon.getTag(),
					Utils3D.getScreenWidth(), Utils3D.getScreenHeight()
							- trashIcon.height);
			trashIcon.hide();
			trashIcon.setTag(null);
			SendMsgToAndroid.sendShowWorkspaceMsg();
		} else if (shadowView != null && shadowView.color.a == 0)
			this.removeView(shadowView);

		// wanghongjian add start //enable_DefaultScene
		else if (source == statrFreeTween && type == TweenCallback.COMPLETE) {
			statrFreeTween = null;
			// Log.v("", "CooeeScene 测试  hotball下滑动画结束  y is " + hotseatBar.y);
		}
		// wanghongjian add end
	}

	public static boolean allowWidgetRefresh() {
		// return !PageIndicator3D.animating && !TrashIcon3D.animating &&
		// !HotButton.animating;
		return true;
	}

	public void pause() {
		if (workspace != null)
			workspace.clearDragObjs();
		if (appHost != null)
			appHost.clearDragObjs();
	}

	private ArrayList<View3D> getNotEmptyFolder() {
		ArrayList<View3D> dragList = dragLayer.getDragList();
		ArrayList<View3D> delFolder = new ArrayList<View3D>();
		// delFolder.clear();
		for (View3D view : dragList) {
			if (view instanceof FolderIcon3D) {
				if (((FolderIcon3D) view).mInfo.contents.size() > 0) {
					delFolder.add(view);
				}
			}
		}

		return delFolder;
	}

	private void cancle_delete_folder() {
		ArrayList<View3D> folderList = getNotEmptyFolder();
		View3D item;
		appHost.hide();
		workspace.show();
		if (DefConfig.DEF_NEW_SIDEBAR == true) {
			hotseatBar.show();
		}

		for (int i = 0; i < folderList.size(); i++) {
			item = folderList.get(i);
			item.setColor(1f, 1f, 1f, 1f);
			item.isDragging = false;
			if (((FolderIcon3D) item).mInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
				workspace.calcCoordinate(item);
				workspace.addInCurrenScreen(item, (int) item.x, (int) item.y);
				item.startTween(View3DTweenAccessor.POS_XY, Elastic.OUT, 0.3f,
						((FolderIcon3D) item).mInfo.x,
						((FolderIcon3D) item).mInfo.y, 0);
			} else if (((FolderIcon3D) item).mInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
				// hotseatBar.calcCoordinate(item);
				dropListBacktoOrig(folderList);
			}

		}
		dragLayer.removeAllViews();
		dragLayer.hide();
		if (DefConfig.DEF_NEW_SIDEBAR == false) {
			sidebar.show();
		}

		trashIcon.hide();
	}

	private void ack_delete_folder() {
		appHost.hide();

		if (!folderOpened) {
			workspace.show();
			dragLayer.onDrop();
			dragLayer.removeAllViews();
			dragLayer.hide();

			if (DefConfig.DEF_NEW_SIDEBAR == true) {
				hotseatBar.show();
			} else {
				sidebar.show();
			}
			// pageIndicator.normal();
		}
		trashIcon.hide();
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		// zhujieping add
		if ((ThemeManager.getInstance().getBoolean("miui_v5_folder") || DefaultLayout.miui_v5_folder)
				&& DefaultLayout.blur_enable && folder != null) {
			MiuiV5FolderBoxBlur(batch, parentAlpha);
		} else {
			super.draw(batch, parentAlpha);
		}
		if (launcher.trashdeleteFolderResult == Workspace3D.CIRCLE_POP_CANCEL_ACTION) {
			launcher.trashdeleteFolderResult = Workspace3D.CIRCLE_POP_NONE_ACTION;
			cancle_delete_folder();
		} else if (launcher.trashdeleteFolderResult == Workspace3D.CIRCLE_POP_ACK_ACTION) {
			launcher.trashdeleteFolderResult = Workspace3D.CIRCLE_POP_NONE_ACTION;
			ack_delete_folder();
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if (keycode == KeyEvent.KEYCODE_SEARCH)
			return true;
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		if (keycode == KeyEvent.KEYCODE_SEARCH) {
			launcherGlobalSearch();
			return true;
		}
		return super.keyUp(keycode);
	}

	public void onHomeKey(boolean alreadyOnHome) {
		// if(sidebar != null)sidebar.hide();
		// Log.d("testdrag", " Root3D onHomeKey hotseatBar.y="+hotseatBar.y +
		// "hotButton.y"+hotButton.y);
		if (workspace != null && workspace.isVisible() && (hotseatBar.y < 0)) {
			Log.d("testdrag", " Root3D onHomeKey return");
			return;
		}
		if (appHost != null && appHost.isVisible()) {

			// teapotXu add start for Fodler in Mainmenu
			// when folder in Mainmenu is open
			if (appHost.folderOpened == true
					&& appHost.getFolderIconOpendInAppHost() != null) {
				FolderIcon3D appHost_folder = appHost
						.getFolderIconOpendInAppHost();
				if (appHost.closeFolder2goHome)
					return;
				if (appHost_folder.onHomeKey(alreadyOnHome))
					appHost.closeFolder2goHome = true;
			}
			// teapotXu add end for Fodler in Mainmenu

			// if(alreadyOnHome){
			// showWorkSpaceFromAllApp();
			// } else {
			appHost.hide();
			workspace.show();
			if (DefConfig.DEF_NEW_SIDEBAR == true) {
				hotseatBar.showNoAnim();
			} else {
				sidebar.show();
			}
			workspace.setCurrentScreen(workspace.getHomePage());
			// }
		} else if (pageContainer != null && pageContainer.isVisible()) {
			if (pageContainer.pageSelect.isVisible()) {
				if (alreadyOnHome) {
					pageContainer.switchToPage(workspace.getHomePage());
				} else {
					pageContainer.hide(false);
					workspace.setCurrentScreen(workspace.getHomePage());
				}
			} else if (pageContainer.pageEdit.isVisible()) {
				launcher.DismissPageDeleteDialog();
				if (alreadyOnHome)
					pageContainer.pageEdit.enterHomePage(true);
				else {
					pageContainer.pageEdit.enterHomePage(false);
					workspace.setCurrentScreen(workspace.getHomePage());
				}
			}
		} else if (launcher.folderIcon != null
				&& launcher.folderIcon.mInfo.contents.size() == 0
				&& launcher.folderIcon.bRenameFolder) {
			launcher.renameFoldercleanup();
			workspace.scrollTo(workspace.getHomePage());
		} else if (folder != null) {
			if (workspace.isManualScrollTo || goHome)
				return;
			if (folder.bAnimate)
				return;
			else if (folder.onHomeKey(alreadyOnHome))
				goHome = true;
			else
				workspace.scrollTo(workspace.getHomePage());
			folder = null;
			// if(!alreadyOnHome)workspace.setCurrentScreen(workspace.getHomePage());
		} else if (workspace != null) {
			launcher.DismissShortcutDialog();
			if (workspace.isManualScrollTo || goHome)
				return;
			workspace.show();
			if (alreadyOnHome)
				workspace.scrollTo(workspace.getHomePage());
			else {
				workspace.setCurrentScreen(workspace.getHomePage());
			}
		}
	}

	class ShadowView extends View3D {

		public ShadowView(String name) {
			super(name);
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			if (AppList3D.translucentBg != null) {
				batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
				AppList3D.translucentBg.draw(batch, 0, 0, width, height);
				// batch.draw(R3D.findRegion("translucent-bg"),0,0);
			}
		}
	}

	public void focusWidget(final WidgetPluginView3D widgetPluginView, int state) {
		if (state == Widget3DManager.WIDGET_STATE_OPEN) {
			point.x = 0;
			point.y = 0;
			widgetPluginView.toAbsolute(point);
			focusWidget = widgetPluginView;
			focusWidgetPos = new float[2];
			focusWidgetPos[0] = focusWidget.x;
			focusWidgetPos[1] = focusWidget.y;
			widgetPluginView.setPosition(point.x, point.y);
			focusWidgetMov = 0;
			if (point.y + widgetPluginView.height * 1.2f > Utils3D
					.getScreenHeight()) {
				launcher.postRunnable(new Runnable() {
					@Override
					public void run() {
						widgetPluginView.startTween(View3DTweenAccessor.POS_XY,
								Cubic.OUT, 0.4f, point.x,
								Utils3D.getScreenHeight()
										- widgetPluginView.height * 1.2f, 0);
					}
				});
				focusWidgetMov = Utils3D.getScreenHeight()
						- widgetPluginView.height * 1.2f - point.y;
			}
			widgetPluginView.setTag(widgetPluginView.getParent());
			// widgetPluginView.setVisible(false);
			// if(shadowView == null){
			// shadowView = new ShadowView("shadow root");
			// }
			// this.addView(shadowView);
			this.addView(widgetPluginView);
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
		} else {
			ViewGroup3D parent = (ViewGroup3D) focusWidget.getTag();
			focusWidget.setPosition(focusWidgetPos[0], focusWidgetPos[1]
					+ focusWidgetMov);
			if (focusWidgetMov != 0) {
				launcher.postRunnable(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						widgetPluginView.startTween(View3DTweenAccessor.POS_XY,
								Cubic.OUT, 0.4f, focusWidgetPos[0],
								focusWidgetPos[1], 0);
					}
				});

			}
			// focusWidget.setVisible(true);
			parent.addView(focusWidget);
			if (shadowView != null) {
				launcher.postRunnable(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						shadowView.startTween(View3DTweenAccessor.OPACITY,
								Linear.INOUT, 0.8f, 0, 0, 0).setCallback(
								shadowView.getParent());
					}
				});
			}
		}
		ClingManager.getInstance().cancelWait();
	}

	// workspace.focusWidget(widgetPluginView,state);
	public void setEffectType(int select) {
		if (appHost != null)
			appHost.appList.setEffectType(select);
	}

	// wanghongjian add start //enable_DefaultScene
	private void setSceneReciverIntent() {
		sceneReciveIntent = new Intent();
		sceneReciveIntent.setAction("com.cooee.scene.receive");
		addAppIntent = new Intent("com.cooee.scene.addapp");
		removeAppIntent = new Intent("com.cooee.scene.removeapp");
		deleteSceneIntent = new Intent("com.cooee.scene.delete");
		upSceneIntent = new Intent("com.cooee.scene.change");
	}

	public boolean FreeMainVisible() {
		return isSceneDown;
	}

	@SuppressLint("NewApi")
	private View3D getFreeView() {
		View3D view = null;
		DexClassLoader loader = null;
		Intent intent = new Intent("com.cooee.scene", null);
		intent.setPackage(scenePkg);
		PackageManager pm = iLoongApplication.ctx.getPackageManager();
		List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent,
				PackageManager.GET_META_DATA);
		if (resolveInfoList != null) {
			if (resolveInfoList.size() > 0) {
				ResolveInfo info = resolveInfoList.get(0);
				loader = getClassLoader(info);
				Class<?> clazz;
				try {

					Context sceneContext = iLoongApplication.ctx
							.createPackageContext(
									info.activityInfo.applicationInfo.packageName,
									Context.CONTEXT_INCLUDE_CODE
											| Context.CONTEXT_IGNORE_SECURITY);
					clazz = loader.loadClass(sceneCls);
					Constructor<?> c = clazz.getConstructor(String.class,
							Context.class, Context.class, Root3D.class);
					view = (View3D) c.newInstance("name",
							iLoongLauncher.getInstance(), sceneContext, this);
					if (view != null) {
						Log.v("Ascene", "sceneContext.getPackageName() is "
								+ sceneContext.getPackageName());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		return view;
	}

	@SuppressLint("NewApi")
	private DexClassLoader getClassLoader(ResolveInfo resolveInfo) {
		// Log.e("Widget3DManager", "come in  getClassLoader");
		ActivityInfo ainfo = resolveInfo.activityInfo;
		String dexPath = ainfo.applicationInfo.sourceDir;
		// String dexOutputDir = ainfo.applicationInfo.dataDir;
		// 插件输出目录，目前为launcher的子目录
		String dexOutputDir = iLoongLauncher.getInstance().getApplicationInfo().dataDir;

		dexOutputDir = dexOutputDir
				+ File.separator
				+ "widget"
				+ File.separator
				+ ainfo.packageName.substring(ainfo.packageName
						.lastIndexOf(".") + 1);
		creatDataDir(dexOutputDir);
		// String libPath = ainfo.applicationInfo.nativeLibraryDir;
		Integer sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
		String libPath = null;
		if (sdkVersion > 8) {
			libPath = ainfo.applicationInfo.nativeLibraryDir;
		}
		DexClassLoader loader = new DexClassLoader(dexPath, dexOutputDir,
				libPath, iLoongApplication.ctx.getClassLoader());
		// Log.e("Widget3DManager", "come out  getClassLoader");
		return loader;
	}

	private File creatDataDir(String dirName) {
		File dir = new File(dirName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public void startScene() {
		if (FeatureConfig.enable_DefaultScene == false) {
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
		if (FeatureConfig.enable_DefaultScene) {
			if (freeMainCreated == false) {
				freeMainCreated = true;
				if (freemain == null) {

					freemain = getFreeView();
					if (freemain != null)
						freemain.y = Utils3D.getScreenHeight();
				}
			}
			if (freemain != null) {
				if (statrFreeTween == null) {
					float downTime = 0.4f;

					// DefaultLayout.scene_shortcut_style = false;
					for (int i = 0; i < workspace.getChildCount(); i++) {
						View3D view = workspace.getChildAt(i);
						view.startTween(View3DTweenAccessor.POS_XY,
								Linear.INOUT, downTime, 0f,
								-Utils3D.getScreenHeight() * 1.5f, 0f);
					}
					SendMsgToAndroid.sendHideWorkspaceMsg();
					// pageIndicatorOldY=pageIndicator.y;
					// hotbuttonx = hotButton.x;
					// Log.v("", "before hotbuttonx is " + hotbuttonx +
					// " pageIndicatorOldY is " + pageIndicatorOldY);
					// hotButton.startTween(View3DTweenAccessor.POS_XY,
					// Linear.INOUT, downTime, hotbuttonx,
					// -Utils3D.getScreenHeight()*1.5f, 0f);
					pageIndicator.startTween(View3DTweenAccessor.POS_XY,
							Linear.INOUT, downTime, 0f,
							-Utils3D.getScreenHeight() * 1.5f, 0f);
					statrFreeTween = hotseatBar.startTween(
							View3DTweenAccessor.POS_XY, Linear.INOUT, downTime,
							0f, -Utils3D.getScreenHeight() * 1.5f, 0f)
							.setCallback(this);
					// Log.v("Cooee", "CooeeScene root�?  下滑动画开�?开始加载add场景桌面");
					DefaultLayout.cooee_scene_style = true;
					sendFreeReceive(sceneReciveIntent);
					isSceneDown = true;// 场景桌面开�?

					if ((DefaultLayout.enable_particle)
							&& (ParticleManager.particleManagerEnable))
						ParticleManager.particleManagerEnable = false;
					saveSceneIndex(0, scenePkg, sceneCls);
					if (SceneManager.getInstance() != null) {
						SceneManager.getInstance().setmUseByPkg(scenePkg);
					}
					this.addView(freemain);// 开始添加MIUI书房桌面

					// Log.v("Cooee", "CooeeScene freemain is add");

				}
			}
		}

	}

	private void sendFreeReceive(final Intent intent) {
		if (sceneHandler != null) {
			sceneHandler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (intent != null)
						iLoongLauncher.getInstance().sendBroadcast(intent);
				}
			});
		}
	}

	@Override
	public boolean multiTouch2(Vector2 initialFirstPointer,
			Vector2 initialSecondPointer, Vector2 firstPointer,
			Vector2 secondPointer) {
		// Log.e("root3d", "multiTouch2 1");

		if (Desktop3DListener.initDone() == false) {
			System.out.println("initDone");
			return true;
		}
		if (workspace.getX() != 0) {
			return true;
		}
		// Log.e("root3d", "multiTouch2 2");
		if (FeatureConfig.enable_DefaultScene == false) {

			return super.multiTouch2(initialFirstPointer, initialSecondPointer,
					firstPointer, secondPointer);
		}
		if (FeatureConfig.enable_DefaultScene) {
			if (freeMainCreated && (isSceneDown)) {
				// System.out.println("root freeMainCreated");
				return true;
			}
			if (super.multiTouch2(initialFirstPointer, initialSecondPointer,
					firstPointer, secondPointer)) {
				// Log.v("Cooee", "CooeeScene doubleClick");
				return true;
			}
			if (initialFirstPointer.y > firstPointer.y
					&& initialSecondPointer.y > secondPointer.y) {
				if (freeMainCreated == false && !isSceneDown) {
					if (freeMainCreated == false && isSceneTheme) {
						freeMainCreated = true;
						if (freemain == null) {
							freemain = getFreeView();
							if (freemain != null)
								freemain.y = Utils3D.getScreenHeight();
						}
					}
				}
				if (freemain != null) {
					// if ( freemain != null)
					// {
					// if ( freemain.y == Utils3D.getScreenHeight())
					// {
					// startScene();
					// }
					// }

					if (!(super.multiTouch2(initialFirstPointer,
							initialSecondPointer, firstPointer, secondPointer))) {
						if (freemain != null) {
							if (freemain.y == Utils3D.getScreenHeight()) {
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
	public boolean onTouchUp(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		// Log.v("", "scroll isSceneDown is " + isSceneDown +
		// "getBeginStartAnim() is " +
		// ((MiuiFreeMain)freemain).getBeginStartAnim() );
		if (freemain != null) {

			if (isSceneDown)
				return true;// 确保双手指下拉自由桌面的时候不会响应其他的up事件

		}
		return super.onTouchUp(x, y, pointer);
	}

	@Override
	public boolean scroll(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		// Log.v("", "scroll isSceneDown is " + isSceneDown +
		// "getBeginStartAnim() is " +
		// ((MiuiFreeMain)freemain).getBeginStartAnim() );
		// Log.v("workspace", " root xScale is " + workspace.getScaleX());
		if (freemain != null) {
			if (isSceneDown)
				return true;// 确保双手指下拉自由桌面的时候不会响应其他的up事
		}
		return super.scroll(x, y, deltaX, deltaY);
	}

	public static void updateItemInDatabase(ItemInfo item) {
		if (item.container >= LauncherSettings.Favorites.CONTAINER_APPLIST) {
			AppListDB.getInstance().moveItem(item);
		} else
			LauncherModel.updateItemInDatabase(iLoongLauncher.getInstance(),
					item);
	}

	public void addApps(ArrayList<ApplicationInfo> apps) {
		if (FeatureConfig.enable_DefaultScene) {
			if (apps.size() > 0){
				String appPkg = apps.get(0).packageName;
				Log.v("addapp", "appadd  is pkg is  " + appPkg);
				addAppIntent.putExtra("sceneAddApp", appPkg);
				sendFreeReceive(addAppIntent);
			}
		}

	}

	public void removeDBIntents(ApplicationInfo appInfo) {
		if (FeatureConfig.enable_DefaultScene) {
			String appPkg = appInfo.packageName;
			Log.v("appremove", "appremove  is pkg is  " + appPkg);
			removeAppIntent.putExtra("sceneappremove", appPkg);
			sendFreeReceive(removeAppIntent);
		}

	}

	public void setSceneTheme(String pkg, String cls) {
		// Log.v("", "isSceneTheme is " + isSceneTheme + " scenepkg is " +
		// scenePkg + " pkg id " + pkg);
		if (scenePkg.equals(pkg)) {
			return;
		} else {
			sendFreeReceive(upSceneIntent);
			scenePkg = pkg;
			sceneCls = cls;
			// Log.v("", "isSceneTheme is " + isSceneTheme + " freemain is " +
			// freemain);
			if (isSceneTheme)// 表示已经有一个场景加载，则开始替换场景
			{
				if (freemain != null) {
					iLoongLauncher.getInstance().postRunnable(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							changeScene();
						}
					});

				}
			}
			isSceneTheme = true;// 当加载一次场景后就为true
		}

	}

	/*
	 * 删除以前场景
	 */
	public void deleteScene() {
		// sendFreeReceive(deleteSceneIntent);

		freeMainCreated = false;
		isSceneTheme = false;// 确保删除以前场景后可以去加载最新的场景
		if (freemain != null) {
			freemain.releaseFocus();
			this.removeView(freemain);
			// int old = Utils3D.showPidMemoryInfo("notclear");
			// Log.v("", "root list not clear 内存为：" + old);
			freemain.dispose();

			Log.v("memory",
					"root list clear后 内存为："
							+ Utils3D.showPidMemoryInfo("clear"));

		}
		freemain = null;
		// isadd = true;
	}

	/*
	 * 卸载场景
	 */
	public void uninstallScene() {
		iLoongLauncher.getInstance().postRunnable(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				deleteScene();
			}
		});

	}

	/*
	 * 应用场景
	 */
	public void applyNewScene() {
		isSceneTheme = true;// 当加载一次场景后就为true
		// startScene();
	}

	private void changeScene() {
		// TODO Auto-generated method stub
		// freemain =
		deleteScene();
		applyNewScene();
	}

	public void closeRootDoor() {
		for (int i = 0; i < workspace.getChildCount(); i++) {
			View3D view = workspace.getChildAt(i);
			view.y = 0;
		}
		// hotButton.x = hotbuttonx;
		// hotButton.y = 0;
		pageIndicator.y = pageIndicatorOldY;
		hotseatBar.y = 0;
	}

	public void stopFreeView(float allfreedownTime) {
		// TODO Auto-generated method stub

		if (allfreedownTime > 0.1f) {
			allfreedownTime = allfreedownTime - 0.1f;
		}
		for (int i = 0; i < workspace.getChildCount(); i++) {
			View3D view = workspace.getChildAt(i);
			view.startTween(View3DTweenAccessor.POS_XY, Linear.INOUT,
					allfreedownTime, 0f, 0f, 0f);
		}
		stopFreeTween = null;
		statrFreeTween = null;
		isSceneDown = false;

		if ((DefaultLayout.enable_particle)
				&& (!ParticleManager.particleManagerEnable))
			ParticleManager.particleManagerEnable = ParticleManager
					.getParticleManager().partilceCanRander();

		// hotButton.startTween(View3DTweenAccessor.POS_XY,
		// Linear.INOUT, allfreedownTime, hotbuttonx,
		// 0f, 0f);
		pageIndicator.startTween(View3DTweenAccessor.POS_XY, Linear.INOUT,
				allfreedownTime, 0, pageIndicatorOldY, 0f);
		stopFreeTween = hotseatBar.startTween(View3DTweenAccessor.POS_XY,
				Linear.INOUT, allfreedownTime, 0f, 0f, 0f).setCallback(this);
		saveSceneIndex(1, scenePkg, sceneCls);
		if (oldSetupMenu == null) {

			Handler handler = new Handler(iLoongLauncher.getInstance()
					.getMainLooper());
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					DefaultLayout.scene_main_menu = false;// 使菜单返回原始菜单
					oldSetupMenu = new SetupMenu(iLoongLauncher.getInstance());
					DefaultLayout.scene_main_menu = true;
				}
			});

		}

	}

	@Override
	public void setActionListener() {
		// TODO Auto-generated method stub
		if (FeatureConfig.enable_DefaultScene) {
			SetupMenuActions.getInstance().RegisterListener(
					ActionSetting.ACTION_START_SCENE, this);
		}
	}

	@Override
	public void OnAction(int actionid, Bundle bundle) {
		// TODO Auto-generated method stub
		// Log.v("scene", "scene root");
		if (FeatureConfig.enable_DefaultScene) {
			iLoongLauncher.getInstance().postRunnable(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					startScene();
				}
			});
		}

	}

	// wanghongjian add end
	// zhujieping add begin
	private void MiuiV5FolderBoxBlur(SpriteBatch batch, float parentAlpha) {
		if (folder.captureCurScreen) {

			// draw to fbo
			folder.fbo.begin();
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

			// 先绘制壁纸到fbo上
			if (FolderIcon3D.wallpaperTextureRegion != null) {
				int wpWidth = FolderIcon3D.wallpaperTextureRegion.getTexture()
						.getWidth();
				int wpHeight = FolderIcon3D.wallpaperTextureRegion.getTexture()
						.getHeight();
				batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
				batch.draw(FolderIcon3D.wallpaperTextureRegion,
						folder.wpOffsetX, 0, wpWidth, wpHeight);
			}

			this.setScale(0.8f, 0.8f);
			this.transform = true;

			// 绘制当前屏幕画面到fbo上
			super.draw(batch, parentAlpha);
			folder.fbo.end();
			folder.mFolderIndex = folder.getViewIndex(folder.mFolderMIUI3D);
			folder.mFolderMIUI3D.remove();

			this.setScale(1.0f, 1.0f);
			this.startTween(View3DTweenAccessor.SCALE_XY, Quint.IN,
					DefaultLayout.blurDuration, 0.8f, 0.8f, 0).setCallback(
					new TweenCallback() {

						@Override
						public void onEvent(int arg0, BaseTween arg1) {
							// TODO Auto-generated method stub
							if (arg0 == TweenCallback.COMPLETE) {
								setScale(1.0f, 1.0f);
							}
						}
					});

			folder.captureCurScreen = false;
			folder.blurBegin = true;
			
			hotseatBar.getModel3DGroup().color.a = 1;
			hotseatBar.getModel3DGroup().startTween(View3DTweenAccessor.OPACITY, Quint.IN, 
					DefaultLayout.blurDuration, 0, 0, 0);
			super.draw(batch, parentAlpha);
		} else {
			super.draw(batch, parentAlpha);
			if (transform) {
				this.folder.mFolderMIUI3D.draw(batch, parentAlpha);
			}

			if (folder.blurBegin) {
				if ((folder.blurCount % 2) == 0) {
					folder.renderTo(folder.fbo, folder.fbo2);
				} else {
					folder.renderTo(folder.fbo2, folder.fbo);
				}
				folder.blurCount++;

				if (folder.blurCount >= (DefaultLayout.blurInterate << 1)) {
					folder.blurBegin = false;
				}

			}

			if (!folder.blurCompleted) {
				int count = (DefaultLayout.blurInterate << 1)
						- folder.blurCount;
				for (int i = 0; i < count; i++) {
					if ((folder.blurCount % 2) == 0) {
						folder.renderTo(folder.fbo, folder.fbo2);
					} else {
						folder.renderTo(folder.fbo2, folder.fbo);
					}
					folder.blurCount++;
				}

				if (folder.blurredView == null) {
					float scaleFactor = 1 / DefaultLayout.fboScale;
					folder.blurredView = new ImageView3D("blurredView",
							folder.fboRegion);
					folder.blurredView.show();
					folder.blurredView.setScale(scaleFactor, scaleFactor);
					folder.blurredView
							.setPosition(
									folder.blurredView.getWidth()
											/ 2
											+ (Gdx.graphics.getWidth() / 2 - folder.blurredView
													.getWidth()),
									folder.blurredView.getHeight()
											/ 2
											+ (Gdx.graphics.getHeight() / 2 - folder.blurredView
													.getHeight()));
					Log.v("blur", "aaaa " + folder.blurredView);
					this.transform = false;
					this.setScale(1.0f, 1.0f);

					if (folder.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT) {
						folder.addViewAt(folder.mFolderIndex,
								folder.mFolderMIUI3D);
						folder.addView(folder.blurredView);
						addView(folder);
						this.hideOtherView();
						
						if (FolderIcon3D.liveWallpaperActive) {
							FolderIcon3D.lwpBackView = new ImageView3D("lwpBackView",
									ThemeManager.getInstance().getBitmap(
											"theme/pack_source/translucent-black.png"));
							FolderIcon3D.lwpBackView.setSize(Gdx.graphics.getWidth(),
									Gdx.graphics.getHeight());
							FolderIcon3D.lwpBackView.setPosition(0, 0);
							folder.addView(FolderIcon3D.lwpBackView);
						}
					} else {						
						if (FolderIcon3D.liveWallpaperActive) {
							FolderIcon3D.lwpBackView = new ImageView3D("lwpBackView",
									ThemeManager.getInstance().getBitmap(
											"theme/pack_source/translucent-black.png"));
							FolderIcon3D.lwpBackView.setSize(Gdx.graphics.getWidth(),
									Gdx.graphics.getHeight());
							FolderIcon3D.lwpBackView.setPosition(0, 0);
							this.addViewBefore(hotseatBar, FolderIcon3D.lwpBackView);
						}
						
						folder.addViewAt(folder.mFolderIndex,
								folder.mFolderMIUI3D);
						this.addViewBefore(hotseatBar, folder.blurredView);
						this.addView(folder);
						this.hideOtherView();
						folder.color.a = 0f;
						folder.ishide = true;						
					}
				}

				folder.blurCompleted = true;
			}
		}
	}

	public void hideOtherView() {
		if (hotseatBar != null) {
			hotseatBar.color.a = 0f;
		}
		if (pageIndicator != null) {
			pageIndicator.color.a = 0f;
		}

	}

	public void showOtherView() {
		if (hotseatBar != null) {
			hotseatBar.color.a = 1f;
		}
		if (pageIndicator != null) {
			pageIndicator.color.a = 1f;
		}
		if (folder != null) {
			folder.color.a = 1f;
			folder.ishide = false;
		}
	}

	private boolean dealMIUIFolderToRoot3D(FolderIcon3D folderIcon3D) {
		float targetOpacity = 0.1f;
		float duration = 0.3f;

		if (folderOpened) {
			Log.e("whywhy", " folderOpened=" + folderOpened);
			return false;
		}
		dragLayer.removeDropTarget(folderIcon3D);
		folderOpened = true;
		folder = folderIcon3D;
		// Log.e("whywhy", " startOPACITYAnim begin");
		Object obj = folderIcon3D.getTag();
		String tag = null;
		if (obj != null && obj instanceof String) {
			tag = (String) obj;
			if (tag.equals("miui_v5_folder")) {
				targetOpacity = 0.2f;
			}
		}
		if ((ThemeManager.getInstance().getBoolean("miui_v5_folder") // zhenNan.ye
		|| DefaultLayout.miui_v5_folder)) {
			if (folder.getFromWhere() == folder.FROM_CELLLAYOUT) {
				this.addViewBefore(workspace, pageIndicator);
				this.addViewBefore(workspace, hotseatBar);
			}
		}
		return true;

	}

	private void resetWorkspace(FolderIcon3D folder) {
		if (folder == null) {
			return;
		}
		if (folder.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT) {
			this.addViewAfter(appHost, pageIndicator);
			this.addViewAfter(pageIndicator, hotseatBar);
		}
	}

	private void dealMIUIFolderToCellLayout(FolderIcon3D view) {

		stopRootTimer();
		if (view == null) {
			return;
		}
		if (view != null && view instanceof FolderIcon3D) {
			FolderIcon3D folderIcon3D = (com.iLoong.launcher.Folder3D.FolderIcon3D) view;
			folder = null;
			{
				if (folderIcon3D.mInfo.contents.size() >= 0
						|| folderIcon3D.mFolderMIUI3D.getColseFolderByDragVal() == false) {
					boolean bNeedAddDragLayer = true;
					if (folderIcon3D.mInfo.contents.size() == 1) {
						// Log.e("test123", "dealMIUIFolderTweenFinish 5");
						ShortcutInfo tempInfo = folderIcon3D.mInfo.contents
								.get(0);
						if (tempInfo.container < 0) {
							workspace.removeViewInWorkspace(folderIcon3D);
							bNeedAddDragLayer = false;
						}
					}
					if (bNeedAddDragLayer) {
						if (folderIcon3D.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT) {
							dragLayer.addDropTargetBefore(
									pageContainer.pageEdit,
									(DropTarget3D) folderIcon3D);
						} else if (folderIcon3D.getFromWhere() == FolderIcon3D.FROM_HOTSEAT) {
							dragLayer
									.addDropTarget((DropTarget3D) folderIcon3D);
						}
						folderIcon3D = null;
					}
				}
			}

		}
		folderOpened = false;
		if (view instanceof FolderIcon3D) {
			FolderIcon3D folderIcon3D = (com.iLoong.launcher.Folder3D.FolderIcon3D) view;
			if (folderIcon3D.getParent() == this) {
				if (folderIcon3D.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT) {
					workspace.addBackInScreen(folderIcon3D,
							folderIcon3D.mInfo.x, folderIcon3D.mInfo.y);
				} else if (folderIcon3D.getFromWhere() == FolderIcon3D.FROM_HOTSEAT) {
					ArrayList<View3D> tmp = new ArrayList<View3D>();
					tmp.add((View3D) view);
					hotseatBar.getDockGroup().backtoOrig(tmp);

				}
			}
		}

		if (dragLayer.isVisible() && !dragLayer.draging) {
			workspace.dropAnim = false;
			if (dragLayer.getDragList().size() > 0
					&& dragLayer.onDrop() == null) {
				dropListBacktoOrig(dragLayer.getDragList());
			}
			dragLayer.removeAllViews();
			dragLayer.hide();
			workspace.dropAnim = true;
			trashIcon.hide();
		}
		if (dragLayer.isVisible() && dragLayer.draging) {
			// Log.e("test123", "dealMIUIFolderTweenFinish trashIcon.show();");
			trashIcon.show();
		} else {
			SendMsgToAndroid.sendShowWorkspaceMsg();
		}

		if (goHome) {
			goHome = false;
			workspace.scrollTo(workspace.getHomePage());
		}
	}

	// zhujieping add
	public boolean isDragEnd = false;

	@Override
	public boolean pointerInParent(float x, float y) {
		// TODO Auto-generated method stub
		float scaleX = getScaleX();
		float scaleY = getScaleY();
		if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable) {
			if (isDragEnd) {// super.pointerInParent(x, y)计算的是scale变化后的坐标
				if (scaleX < 1 || scaleY < 1) {
					point.x = x - this.x;
					point.y = y - this.y;
					return ((point.x >= 0 && point.x < width) && (point.y >= 0 && point.y < height));
				}
			}
		}
		return super.pointerInParent(x, y);
	}
	// zhujieping add end
}