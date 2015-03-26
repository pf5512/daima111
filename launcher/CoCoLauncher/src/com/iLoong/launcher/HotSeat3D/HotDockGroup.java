package com.iLoong.launcher.HotSeat3D;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.IconBase3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupOBJ3D;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class HotDockGroup extends ViewGroupOBJ3D implements
		ClingManager.ClingTarget {
	public HotSeat3D hotSeat3D;
	private final int SCROLL_UP = 0;
	private final int SCROLL_DOWN = 1;
	// private final int SCROLL_LEFT = 2;
	// private final int SCROLL_RIGHT = 3;

	public static final int MAX_ICON_NUM = 5;

	private final float VELOCITY_DIV = 30f;

	private HotGridView3D mShortcutView;
	private HotGridView3D mFocusGridView;
	View3D folder_icon;
	private int scrollDir = -1;
	private boolean isFling = false;
	private boolean bStartScrollDown = false;
	private float scaleY = 0;
	// private Tween flingTween = null;
	Vector2 IconPoint = new Vector2();
	public boolean dragAble = true;
	private boolean isScroll = false;
	// private int curFolderPos=-1;
	private Workspace3D workspace;

	public int clingR;
	public int clingX;
	public int clingY;
	//teapotXu add start for hotseat's middle icon
	private View3D mHotSeatMiddleImgView;
	//teapotXu add end for hotseat's middle icon
	
	private List<ResolveInfo> hotDialIntentMap = new ArrayList<ResolveInfo>();
	private List<ResolveInfo> hotMmsIntentMap = new ArrayList<ResolveInfo>();
	private List<ResolveInfo> hotContactIntentMap = new ArrayList<ResolveInfo>();
	private List<ResolveInfo> hotBorwserIntentMap = new ArrayList<ResolveInfo>();

	// static Texture t = new Texture(Gdx.files.internal("bgtest.png"));
	public HotDockGroup(String name) {
		super(name);

	}

	public boolean isInShortcutList() {
		return mFocusGridView == mShortcutView;
	}

	public void setWorkspace(View3D v) {
		this.workspace = (Workspace3D) v;
	}

	//teapotXu add start for hotseat's middle icon
    private TextureRegion loadTexture(Context context, String imageFile) {
		Texture texture = new BitmapTexture(ThemeManager.getInstance().getBitmap(imageFile));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion region = new TextureRegion(texture);
		return region;
	}	
    private Vector2 getHotseatMiddleImgPos(float bmpWidth,float bmpHeight,int hotDockGroupWidth,int padding_left,int padding_right,int padding_top,int padding_bottom)
    {
    	Vector2 vector = new Vector2();
    	float space_height = bmpHeight / 10;
    	
    	vector.x = (hotDockGroupWidth - bmpWidth)/2;
    	
    	vector.y = padding_bottom + space_height;
    	
    	return vector;
    }
	//teapotXu add end for hotseat's middle icon
	
	public HotDockGroup(String name, int width, int height) {
		super(name);

		// float gridViewHeight = height - SideBar.SIDEBAR_OPCITY_WIDTH;
		int padding = (int) ((height - R3D.workspace_cell_height) / 2);

		if (padding < 0) {
			padding = 0;
		}

		mShortcutView = new HotGridView3D("shortcutview", width, height,
				R3D.hot_dock_icon_number, 1);

		mShortcutView.setPadding(R3D.hot_grid_left_margin,
				R3D.hot_grid_right_margin, 0, R3D.hot_grid_bottom_margin);
		mShortcutView.setPosition(0, 0);
		addView(mShortcutView);
		setVisible(HotSeat3D.TYPE_WIDGET);
		setSize(width, height);
		this.setOrigin(width / 2, height / 2);
		mFocusGridView = mShortcutView;

		clingR = R3D.workspace_cell_width / 2;
		clingX = Utils3D.getScreenWidth() / 2;
		clingY = (int) (R3D.hot_grid_bottom_margin * 1.5f + (height - R3D.hot_grid_bottom_margin) / 2);
		
		//teapotXu add start for hotseat's middle icon
		if(DefaultLayout.same_spacing_btw_hotseat_icons == true)
		{
			mHotSeatMiddleImgView = new View3D("MiddleImgView",loadTexture(iLoongApplication.ctx,"theme/dock3dbar/middle.png"));
			mHotSeatMiddleImgView.setSize(Utilities.sIconTextureHeight, Utilities.sIconTextureHeight);
			Vector2 middleImgViewPos = getHotseatMiddleImgPos(Utilities.sIconTextureHeight, 
					Utilities.sIconTextureHeight,
					width,
					R3D.hot_grid_left_margin,
					R3D.hot_grid_right_margin, 
					0, 
					R3D.hot_grid_bottom_margin);
			mHotSeatMiddleImgView.setPosition(middleImgViewPos.x,middleImgViewPos.y);
			
			addView(mHotSeatMiddleImgView);
		}
		//teapotXu add end for hotseat's hotseat's middle icon
		
		initHotIntentMap();
	}

	public int getIntentType(Intent intent) {
		if (intent == null || intent.getComponent() == null) {
			return -1;
		}
		String packageName = intent.getComponent().getPackageName();
		String className = intent.getComponent().getClassName();
		for (int i = 0; i < hotDialIntentMap.size(); i++) {
			if (hotDialIntentMap.get(i).activityInfo.packageName
					.equals(packageName)
					&& hotDialIntentMap.get(i).activityInfo.name
							.equals(className)) {
				return 0;
			}
		}
		for (int i = 0; i < hotContactIntentMap.size(); i++) {
			if (hotContactIntentMap.get(i).activityInfo.packageName
					.equals(packageName)
					&& hotContactIntentMap.get(i).activityInfo.name
							.equals(className)) {
				return 1;
			}
		}
		for (int i = 0; i < hotMmsIntentMap.size(); i++) {
			if (hotMmsIntentMap.get(i).activityInfo.packageName
					.equals(packageName)
					&& hotMmsIntentMap.get(i).activityInfo.name
							.equals(className)) {
				return 2;
			}
		}
		for (int i = 0; i < hotBorwserIntentMap.size(); i++) {
			if (hotBorwserIntentMap.get(i).activityInfo.packageName
					.equals(packageName)
					&& hotBorwserIntentMap.get(i).activityInfo.name
							.equals(className)) {
				return 3;
			}
		}
		return -1;
	}

	private void initHotIntentMap() {

		Intent mainIntent = null;
		List<ResolveInfo> list = null;
		PackageManager pm = iLoongLauncher.getInstance().getPackageManager();
		try {
			mainIntent = Intent.parseUri(
					"intent:#Intent;action=android.intent.action.DIAL;end", 0);
			list = pm.queryIntentActivities(mainIntent, 0);
			for (int i = 0; i < list.size(); i++) {
				int flags = list.get(i).activityInfo.applicationInfo.flags;
				if (((flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0)) {
					hotDialIntentMap.add(list.get(i));
				}
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mainIntent = Intent
					.parseUri(
							"intent:content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;end",
							0);
			list = pm.queryIntentActivities(mainIntent, 0);
			for (int i = 0; i < list.size(); i++) {
				int flags = list.get(i).activityInfo.applicationInfo.flags;
				if (((flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0)) {
					hotContactIntentMap.add(list.get(i));
				}
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mainIntent = Intent
					.parseUri(
							"intent:#Intent;action=android.intent.action.MAIN;type=vnd.android-dir/mms-sms;end",
							0);
			list = pm.queryIntentActivities(mainIntent, 0);
			for (int i = 0; i < list.size(); i++) {
				int flags = list.get(i).activityInfo.applicationInfo.flags;
				if (((flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0)) {
					hotMmsIntentMap.add(list.get(i));
				}
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mainIntent = new Intent("android.intent.action.VIEW");
			mainIntent.addCategory("android.intent.category.DEFAULT");
			mainIntent.addCategory("android.intent.category.BROWSABLE");
			Uri uri = Uri.parse("http://");
			mainIntent.setDataAndType(uri, null);
			// mainIntent.addCategory(Intent.CATEGORY_BROWSABLE);
			list = pm.queryIntentActivities(mainIntent, 0);
			for (int i = 0; i < list.size(); i++) {
				int flags = list.get(i).activityInfo.applicationInfo.flags;
				if (((flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0)) {
					hotBorwserIntentMap.add(list.get(i));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void findBrowser() {
		// PackageManager packageManager = iLoongLauncher.getInstance()
		// .getPackageManager();
		// String str1 = "android.intent.category.DEFAULT";
		// String str2 = "android.intent.category.BROWSABLE";
		// String str3 = "android.intent.action.VIEW";
		//
		// // 设置默认项的必须参数之一,用户的操作符合该过滤器时,默认设置起效
		// IntentFilter filter = new IntentFilter(str3);
		// filter.addCategory(str1);
		// filter.addCategory(str2);
		// filter.addDataScheme("http");
		// // 设置浏览页面用的Activity
		// ComponentName component = new ComponentName(context.getPackageName(),
		// BrowserActivity.class.getName());
		//
		// Intent intent = new Intent(str3);
		// intent.addCategory(str2);
		// intent.addCategory(str1);
		// Uri uri = Uri.parse("http://");
		// intent.setDataAndType(uri, null);
		//
		// // 找出手机当前安装的所有浏览器程序
		// List<ResolveInfo> resolveInfoList = packageManager
		// .queryIntentActivities(intent,
		// PackageManager.GET_INTENT_FILTERS);
		//
		// int size = resolveInfoList.size();
		// ComponentName[] arrayOfComponentName = new ComponentName[size];
		// for (int i = 0; i < size; i++) {
		// ActivityInfo activityInfo = resolveInfoList.get(i).activityInfo;
		// String packageName = activityInfo.packageName;
		// String className = activityInfo.name;
		// // 清除之前的默认设置
		// packageManager.clearPackagePreferredActivities(packageName);
		// ComponentName componentName = new ComponentName(packageName,
		// className);
		// arrayOfComponentName[i] = componentName;
		// }
		// packageManager.addPreferredActivity(filter,
		// IntentFilter.MATCH_CATEGORY_SCHEME, arrayOfComponentName,
		// component);

	}

	private View3D createVirturFolder() {
		UserFolderInfo folderInfo = new UserFolderInfo();
		folderInfo.title = R3D.folder3D_name;
		folderInfo.screen = workspace.getCurrentScreen();
		folderInfo.x = 0;
		folderInfo.y = 0;
		FolderIcon3D folderIcon3D = new FolderIcon3D("FolderIcon3DView",
				folderInfo);
		folderIcon3D.changeFolderFrontRegion(true);
		return folderIcon3D;
	}

	public void removeVirtueFolderIcon() {
		if (folder_icon != null) {
			this.removeView(folder_icon);
			folder_icon = null;
		}
	}

	@Override
	public void removeAllViews() {
		mFocusGridView.removeAllViews();
	}

	private void cellMakeFolder(float posx, float posy, boolean preview) {

//		removeVirtueFolderIcon();
		if (folder_icon == null){
			folder_icon = createVirturFolder();
			folder_icon.setVisible(false);
		}
		folder_icon.x = posx;
		folder_icon.y = posy;
//		if (preview) {
//			Color color = folder_icon.getColor();
//			color.a = 0.5f;
//			folder_icon.setColor(color);
//		}
//		folder_icon.show();
//		addViewAt(getChildCount(), folder_icon);
		super.addViewAt(0, folder_icon);
		if (!folder_icon.getVisible())
			FolderLargeAnim(0.3f, folder_icon);
	}

	public void setVisible(int type) {
		// if(ClingManager.getInstance().widgetClingFired)SendMsgToAndroid.sendRefreshClingStateMsg();
	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		if (pointer == 1) {
			return false;
		}
		mFocusGridView.stopTween();
		mFocusGridView.setUser(0);
		isFling = false;
		isScroll = false;

		super.onTouchDown(x, y, pointer);
		requestFocus();
		return true;
	}

	@Override
	public boolean scroll(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		if (mFocusGridView.lastTouchedChild != null
				&& (mFocusGridView.lastTouchedChild instanceof Icon3D || mFocusGridView.lastTouchedChild instanceof FolderIcon3D)) {
			//Log.e("test", "focus:" + mFocusGridView.getFocusView());
			mFocusGridView.lastTouchedChild.color.a = 1f;
		}

		//requestFocus();
		scaleY += 2 * deltaY / this.height;
		// Log.v("hotobj", " hotdock scroll name:" + this.name + "  isScroll:" +
		// isScroll + " deltaY:"
		// + deltaY+" deltaX:" +deltaX + " y=" +y+ " x=" +x);

		if (scaleY > 0 && bStartScrollDown == false) {
			bStartScrollDown = true;
		}
		if (scaleY >= 1) {
			scaleY = 1;
		}
		if (scaleY <= -0.5f) {
			scaleY = -0.5f;
		}
		// Log.e("hotobj", " scaleY="+scaleY);
		if (!isScroll && super.scroll(x, y, deltaX, deltaY)) {
			Log.v("hotobj", " hotdock scroll return true");
			return true;
		}
		if (bStartScrollDown == false) {
			if (!isScroll && y < this.height
					&& (deltaX == 0 || Math.abs(deltaY) / Math.abs(deltaX) > 1)) {
				this.setTag(deltaY);
				isScroll = true;
				viewParent.onCtrlEvent(this, HotSeat3D.MSG_DOCKGROUP_SCROLL_UP);
			}
		} else {
			// Log.e("hotobj", " MSG_DOCKGROUP_SCROLL_DOWN=");
			if ((deltaX == 0 || Math.abs(deltaY) / Math.abs(deltaX) > 1)) {
				this.setTag(scaleY);

				isScroll = true;
				viewParent.onCtrlEvent(this,
						HotSeat3D.MSG_DOCKGROUP_SCROLL_DOWN);
			}
		}

		return true;
	}

	private void initDock() {
		scaleY = 0;
		isScroll = false;
		isFling = false;
	}

	@Override
	public boolean onTouchUp(float x, float y, int pointer) {
		// TODO Auto-generated method stub

		if (pointer == 1) {
			return false;
		}
		releaseFocus();

		if (bStartScrollDown && isScroll == true) {
			this.setTag(-2.0f);
			bStartScrollDown = false;
			initDock();
			viewParent.onCtrlEvent(this, HotSeat3D.MSG_DOCKGROUP_SCROLL_DOWN);
			return true;
		}
		{
			initDock();

			return super.onTouchUp(x, y, pointer);
		}

	}

	@Override
	public boolean onLongClick(float x, float y) {
		releaseFocus();
		SendMsgToAndroid.sendHideWorkspaceMsg();
		boolean ret = false;
		if (!dragAble) {
			if (mFocusGridView == mShortcutView) {
				mFocusGridView.setAutoDrag(false);
			}
			// viewParent.onCtrlEvent(this, HotSeat3D.MSG_LONGCLICK_INAPPLIST);
		}

		ret = super.onLongClick(x, y);
		if (ret) {
			if (mFocusGridView.getFocusView() != null) {
				mFocusGridView.getFocusView().color.a = 1f;
			}
		}
		if (!dragAble) {
			if (mFocusGridView == mShortcutView) {
				mFocusGridView.setAutoDrag(true);
			}
		}
		return ret;
	}

	@Override
	public boolean onDoubleClick(float x, float y) {
		// TODO Auto-generated method stub
		return true;
	}

	public View3D getFocusView() {
		return mFocusGridView.getFocusView();
	}

	private void addSingleItem(View3D view, int index) {

		if (view instanceof IconBase3D) {
			ItemInfo info = ((IconBase3D) view).getItemInfo();

			info.screen = index;

			mFocusGridView.getViewPos(view, IconPoint);
			info.x = (int) IconPoint.x;
			info.y = (int) IconPoint.y;
			// Log.v("HotObj", "additems info.screen=" + info.screen);
			info.angle = HotSeat3D.TYPE_WIDGET;
			Root3D.addOrMoveDB(info,
					LauncherSettings.Favorites.CONTAINER_HOTSEAT);
			if (view instanceof Icon3D) {
				// Icon3D iconView = (Icon3D)view;
				// iconView.setItemInfo(iconView.getItemInfo());
			}
		}

		if (index < mFocusGridView.getChildCount()) {
			mFocusGridView.addItem(view, index);
		} else {
			mFocusGridView.addItem(view);
		}
	}

	private void createFolderItem(ArrayList<View3D> child_list, int index) {
		mFocusGridView.getEmptyCellIndexPos(index, IconPoint, true);
		UserFolderInfo folderInfo = iLoongLauncher.getInstance()
				.addHotSeatFolder((int) IconPoint.x, (int) IconPoint.y, index);
		FolderIcon3D folderIcon3D = new FolderIcon3D("FolderIcon3DHot",
				folderInfo);
		if (DefaultLayout.hotseat_hide_title) {
			folderIcon3D.changeFolderFrontRegion(true);
		}
		if (index < mFocusGridView.getChildCount()) {
			mFocusGridView.addItem(folderIcon3D, index);
		} else {
			mFocusGridView.addItem(folderIcon3D);
		}
		folderIcon3D.onDrop(child_list, 0, 0);
		folderIcon3D.setOnDropFalse();

	}

	public void backtoOrig(ArrayList<View3D> child_list) {
		ItemInfo itemInfo;
		View3D findView = null;
		for (int i = 0; i < child_list.size(); i++) {
			View3D view = child_list.get(i);
			if (view instanceof FolderIcon3D) {
				((FolderIcon3D) view).setLongClick(false);
			}
			view.stopTween();
			if ((view instanceof IconBase3D) == false)
				return;
			itemInfo = ((IconBase3D) view).getItemInfo();
			if (itemInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
				if (DefaultLayout.hotseat_hide_title) {
					if (view instanceof Icon3D) {
						Utils3D.changeTextureRegion(view,
								Utils3D.getIconBmpHeight(), true);
					} else if (view instanceof FolderIcon3D) {
						((FolderIcon3D) view).changeFolderFrontRegion(true);
					}
				}
				addSingleItem(view, itemInfo.screen);

			} else if (itemInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
				// workspace.addInCurrenScreen(view, itemInfo.x, itemInfo.y);
				workspace.addBackInScreen(view, itemInfo.x, itemInfo.y);
				view.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT, 0.5f,
						itemInfo.x, itemInfo.y, 0);
			} else if (itemInfo.container >= 0) {

				UserFolderInfo folderInfo = iLoongLauncher.getInstance()
						.getFolderInfo(itemInfo.container);
				if (folderInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
					findView = mFocusGridView.findExistView(folderInfo.screen);
				} else if (folderInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
					findView = workspace.getViewByItemInfo(folderInfo);
				}

				if (findView != null && findView instanceof FolderIcon3D) {
					((FolderIcon3D) findView).onDrop(child_list, 0, 0);
				}

			}

		}

	}

	public void dealDockGroupDropOver(ArrayList<View3D> child_list, float x,
			float y) {
		// View3D view = child_list.get(0);
		int index = mFocusGridView.getIndex((int) x, (int) y);
		// Log.v("HotObj", "dealDockGroupDropOver index ="+index);
		if (index < 0) {
			removeVirtueFolderIcon();
			return;
		}
		View3D findView = mFocusGridView.findExistView(index);

		if (findView == null) {
			// Log.v("HotObj", "dealDockGroupDropOver findView==null ");
			removeVirtueFolderIcon();
			return;
		}
		if (findView instanceof FolderIcon3D) {
			removeVirtueFolderIcon();
			return;
		}
		if (child_list.size() == 1 && child_list.get(0) instanceof FolderIcon3D) {
			removeVirtueFolderIcon();
			return;
		}

		mFocusGridView.getIndexPos(index, IconPoint);
		// curFolderPos=index;
		cellMakeFolder(IconPoint.x, IconPoint.y, true);

	}

	public void addItems(ArrayList<View3D> child_list) {

		View3D view = child_list.get(child_list.size() - 1);
		mFocusGridView.calcCoordinate(view);
		int index = mFocusGridView.getIndex((int) (view.x + view.width / 2),
				(int) mFocusGridView.height / 2);
		// Log.v("HotObj", "addItems index ="+index);
		removeVirtueFolderIcon();
		if (index == -1) {

			backtoOrig(child_list);
			SendMsgToAndroid.sendOurToastMsg(R3D
					.getString(RR.string.add_failure));
			return;
		}

		if (index == -2) {

			backtoOrig(child_list);
			SendMsgToAndroid.sendOurToastMsg(R3D
					.getString(RR.string.deny_add_on_mainmenu));
			return;
		}

		View3D findView = mFocusGridView.findExistView(index);

		if (findView != null) {
			/*
			 * 这里表示拖到的地方已经被占用�? 需要创建文件夹
			 */
			if (view instanceof FolderIcon3D && child_list.size() == 1) {
				backtoOrig(child_list);
				// SendMsgToAndroid.sendOurToastMsg("文件");
			} else {
				if (findView instanceof FolderIcon3D) {
					((FolderIcon3D) findView).onDrop(child_list, 0, 0);
					((FolderIcon3D) findView).setOnDropFalse();
				} else {
					if (DefaultLayout.hotseat_disable_make_folder)
						backtoOrig(child_list);
					else {
						child_list.add(findView);
						mFocusGridView.removeView(findView);
						createFolderItem(child_list, index);
					}
				}
			}
			return;
		} else {

			if (child_list.size() == 1) {
				/* 直接插入 */
				if (DefaultLayout.hotseat_hide_title) {
					if (view instanceof Icon3D) {
						if (ThemeManager.getInstance()
								.getCurrentThemeDescription().mSystem) {
							replaceIcon((Icon3D) view);
						}
						Utils3D.changeTextureRegion(child_list,
								Utils3D.getIconBmpHeight(), true);
					} else if (view instanceof FolderIcon3D) {
						((FolderIcon3D) view).changeFolderFrontRegion(true);
					}
				}
				addSingleItem(view, index);
			} else {
				// Utils3D.changeTextureRegion(child_list,Utils3D.getIconBmpHeight(),true);
				createFolderItem(child_list, index);
			}
		}
	}

	public static String getInfoName(ShortcutInfo info) {

		ComponentName cmp = info.intent.getComponent();
		String name = "coco";
		if (cmp != null)
			name = cmp.toString();
		else {
			String temp = null;
			if (info.intent.getAction() != null) {
				name = info.intent.getAction().toString();
			}
			if (info.intent.getType() != null) {
				temp = info.intent.getType().toString();
				name += temp;
			}
			if (info.intent.getDataString() != null) {
				temp = info.intent.getDataString();
				name += temp;
			}

		}
		// name = info.intent.toString();
		return name;
	}

	private Uri getDefaultBrowserUri() {
		String url = iLoongLauncher.getInstance().getString(
				RR.string.default_browser_url);
		if (url.indexOf("{CID}") != -1) {
			url = url.replace("{CID}", "android-google");
		}
		return Uri.parse(url);
	}

	public void replaceIntent(ShortcutInfo sInfo) {
		int iconFlag = getIntentType(sInfo.intent);
		if (iconFlag == 0) {

			try {
				Log.e("test", "before :" + getInfoName(sInfo));
				sInfo.intent = Intent.parseUri(
						"intent:#Intent;action=android.intent.action.DIAL;end",
						0);
				Log.e("test", "after :" + getInfoName(sInfo));
				sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 电话
		} else if (iconFlag == 1) {
			// 联系人

			try {
				Log.e("test", "before :" + getInfoName(sInfo));
				sInfo.intent = Intent
						.parseUri(
								"intent:content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;end",
								0);
				sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
				Log.e("test", "after :" + getInfoName(sInfo));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (iconFlag == 2) {
			// 短信
			try {
				sInfo.intent = Intent
						.parseUri(
								"intent:#Intent;action=android.intent.action.MAIN;type=vnd.android-dir/mms-sms;end",
								0);
				sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (iconFlag == 3) {
			String customUri = DefaultLayout.defaultUri;
			String defaultUri;// = getString(RR.string.default_browser_url);
			if (customUri != null) {
				defaultUri = customUri;
			} else {
				defaultUri = "http://www.google.cn";// 用百度代替空白网�?about:blank"否则欧鹏浏览器无法识�?
			}

			Intent intent = new Intent(Intent.ACTION_VIEW,
					((defaultUri != null) ? Uri.parse(defaultUri)
							: getDefaultBrowserUri()))
					.addCategory(Intent.CATEGORY_BROWSABLE);
			if (DefaultLayout.default_explorer != null) {
				intent.setPackage(DefaultLayout.default_explorer);
				List<ResolveInfo> allMatches = iLoongLauncher
						.getInstance()
						.getPackageManager()
						.queryIntentActivities(intent,
								PackageManager.MATCH_DEFAULT_ONLY);
				if (allMatches == null || allMatches.size() == 0) {
					intent.setPackage(null);
				}
			}
			sInfo.intent = intent;
			// sInfo.intent = new Intent("android.intent.action.VIEW");
			// sInfo.intent.addCategory("android.intent.category.DEFAULT");
			// sInfo.intent.addCategory("android.intent.category.BROWSABLE");
			// Uri uri = Uri.parse("http://www.google.cn");
			// sInfo.intent.setDataAndType(uri, null);
			sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
		}
	}

	public void replaceIcon(Icon3D icon) {
		ShortcutInfo sInfo = (ShortcutInfo) icon.getItemInfo();
		int iconFlag = getIntentType(sInfo.intent);
		if (iconFlag == 0) {

			try {
				Log.e("test", "before :" + getInfoName(sInfo));
				sInfo.intent = Intent.parseUri(
						"intent:#Intent;action=android.intent.action.DIAL;end",
						0);
				Log.e("test", "after :" + getInfoName(sInfo));
				sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
				icon.region.setRegion(R3D.findRegion(getInfoName(sInfo)));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 电话
		} else if (iconFlag == 1) {
			// 联系人

			try {
				Log.e("test", "before :" + getInfoName(sInfo));
				sInfo.intent = Intent
						.parseUri(
								"intent:content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;end",
								0);
				sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
				Log.e("test", "after :" + getInfoName(sInfo));
				icon.region.setRegion(R3D.findRegion(getInfoName(sInfo)));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (iconFlag == 2) {
			// 短信
			try {
				sInfo.intent = Intent
						.parseUri(
								"intent:#Intent;action=android.intent.action.MAIN;type=vnd.android-dir/mms-sms;end",
								0);
				sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
				icon.region.setRegion(R3D.findRegion(getInfoName(sInfo)));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (iconFlag == 3) {
			String customUri = DefaultLayout.defaultUri;
			String defaultUri;// = getString(RR.string.default_browser_url);
			if (customUri != null) {
				defaultUri = customUri;
			} else {
				defaultUri = "http://www.google.cn";// 用百度代替空白网�?about:blank"否则欧鹏浏览器无法识�?
			}

			Intent intent = new Intent(Intent.ACTION_VIEW,
					((defaultUri != null) ? Uri.parse(defaultUri)
							: getDefaultBrowserUri()))
					.addCategory(Intent.CATEGORY_BROWSABLE);
			if (DefaultLayout.default_explorer != null) {
				intent.setPackage(DefaultLayout.default_explorer);
				List<ResolveInfo> allMatches = iLoongLauncher
						.getInstance()
						.getPackageManager()
						.queryIntentActivities(intent,
								PackageManager.MATCH_DEFAULT_ONLY);
				if (allMatches == null || allMatches.size() == 0) {
					intent.setPackage(null);
				}
			}
			sInfo.intent = intent;
			// sInfo.intent = new Intent("android.intent.action.VIEW");
			// sInfo.intent.addCategory("android.intent.category.DEFAULT");
			// sInfo.intent.addCategory("android.intent.category.BROWSABLE");
			// Uri uri = Uri.parse("http://www.google.cn");
			// sInfo.intent.setDataAndType(uri, null);
			sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
			icon.region.setRegion(R3D.findRegion(getInfoName(sInfo)));
		}
	}

	public void bindItem(View3D view) {
		if (view instanceof Icon3D) {
			if (ThemeManager.getInstance().getCurrentThemeDescription().mSystem) {
				replaceIcon((Icon3D) view);
			}
		}
		mShortcutView.addItem(view);
	}

	public void removeItem(View3D view) {

		mShortcutView.removeView(view);
	}

	public void bindItem(FolderIcon3D view) {
		// View3D findView=mFocusGridView.findExistView(view.mInfo.screen);
		// if (findView==null)
		// {
		// mShortcutView.addFolder(view);

		// }
	}

	public void show() {
		this.visible = true;
		this.touchable = true;
		mFocusGridView.show();
	}

	public void hide() {
		this.visible = false;
		this.touchable = false;
		mFocusGridView.hide();
	}

	// @Override
	// public void draw(SpriteBatch batch, float parentAlpha) {
	// // TODO Auto-generated method stub
	// super.draw(batch, parentAlpha);
	//
	// if (isScrollEnd()) {
	// // if (flingTween != null && !flingTween.isFinished()) {
	// // mFocusGridView.stopTween();
	// // flingTween = null;
	// // }
	// if (!mFocusGridView.isAutoMove)
	// mFocusGridView.setUser(mFocusGridView.getUser() / 1.5f);
	// }
	//
	// if (mFocusGridView.isAutoMove) {
	// if (mFocusGridView.isScrollToEnd(-(int)mFocusGridView.getUser())) {
	// mFocusGridView.x += mFocusGridView.getUser();
	// mFocusGridView.getFocusView().x += -mFocusGridView.getUser();
	// }
	// } else {
	// if (Math.abs(mFocusGridView.getUser()) > 2) {
	// mFocusGridView.x += mFocusGridView.getUser();
	// } else {
	// // if (isFling) {
	// // startScrollTween();
	// // }
	// }
	// }
	// }

	private boolean isScrollEnd() { // first or last in screen
		if (mFocusGridView.getChildCount() == 0)
			return false;
		float mFocusViewX = mFocusGridView.x;
		float mFocusViewWidth = mFocusGridView.getEffectiveWidth();
		if (mFocusViewX + mFocusViewWidth < width || mFocusViewX > 0) {
			return true;
		}
		return false;
	}

	public void onDragOverLeave() {
		removeVirtueFolderIcon();
	}

	private void dealFolderIconMove() {

		View3D findView = (View3D) mFocusGridView.getTag();
		View3D focusView = mFocusGridView.getFocusView();
		int index = ((IconBase3D) findView).getItemInfo().screen;
		mFocusGridView.getIndexPos(index, IconPoint);
		cellMakeFolder(IconPoint.x, IconPoint.y, true);

	}

	private void dealCreateFolder() {
		View3D findView = (View3D) mFocusGridView.getTag();
		View3D focusView = mFocusGridView.getFocusView();
		removeVirtueFolderIcon();
		int index = ((Icon3D) findView).getItemInfo().screen;
		mFocusGridView.getIndexPos(index, IconPoint);
		ArrayList<View3D> child_list = new ArrayList<View3D>();
		child_list.add(findView);
		child_list.add(focusView);
		mFocusGridView.removeView(findView);
		mFocusGridView.removeView(focusView);
		createFolderItem(child_list, index);
		// cellMakeFolder(IconPoint.x, IconPoint.y, true);
	}

	private void dealMergeFolder() {
		FolderIcon3D findView = (FolderIcon3D) mFocusGridView.getTag();
		View3D focusView = mFocusGridView.getFocusView();
		removeVirtueFolderIcon();
		// int index = ((IconBase3D)findView).getItemInfo().screen;
		// mFocusGridView.getIndexPos(index,IconPoint);
		ArrayList<View3D> child_list = new ArrayList<View3D>();
		child_list.add(focusView);
		mFocusGridView.removeView(focusView);
		// createFolderItem(child_list,index);
		findView.onDrop(child_list, 0, 0);
		findView.setOnDropFalse();
	}

	@Override
	public boolean onCtrlEvent(View3D sender, int event_id) {
		// TODO Auto-generated method stub
		if (sender instanceof HotGridView3D) {
			HotGridView3D view = (HotGridView3D) sender;
			switch (event_id) {
			case HotGridView3D.MSG_VIEW_CREATE_FOLDER:
				dealCreateFolder();
				return true;
			case HotGridView3D.MSG_VIEW_MERGE_FOLDER:
				dealMergeFolder();
				return true;
			case HotGridView3D.MSG_VIEW_START_MAIN:
				return viewParent.onCtrlEvent(this,
						HotSeat3D.MSG_VIEW_START_MAIN);
			case HotGridView3D.MSG_VIEW_OUTREGION:
				return viewParent.onCtrlEvent(this, HotSeat3D.MSG_ON_DROP);
			case HotGridView3D.MSG_VIEW_MOVED:
				if (view == mShortcutView) {
					if (mShortcutView.getDragState() == HotGridView3D.State_ChangePosition) {
						if (folder_icon != null && folder_icon.getVisible()){
							FolderSmallAnim(0.3f, folder_icon);
						}else{
							removeVirtueFolderIcon();
						}
					} else {
						dealFolderIconMove();
					}
				}
				return false;
			}
		}
		return super.onCtrlEvent(sender, event_id);
	}

	public int getShortcutCount() {
		// TODO Auto-generated method stub
		return mFocusGridView.getChildCount();
	}

	public HotGridView3D getShortcutGridview() {
		return mShortcutView;
	}

	@Override
	public boolean visible() {
		if (this.getParent() != null) {
			return visible && this.getParent().visible;
		}
		return visible;
	}

	@Override
	public int getClingPriority() {
		// TODO Auto-generated method stub
		return ClingManager.ALLAPP_CLING;
	}

	@Override
	public void dismissCling() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPriority(int priority) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean pointerInParent(float x, float y) {
		// TODO Auto-generated method stub
		if (Gdx.graphics.getDensity() > 1) {
			Group.toChildCoordinates(this, x, y, point);
			float offsetY = 20 * Gdx.graphics.getDensity();
			return ((point.x >= 0 && point.x < width) && point.y < (height + offsetY));
		} else {
			return super.pointerInAbs(x, y);
		}
	}
	
	private Tween FolderLargeTween = null;
	private Tween FolderSmallTween = null;
	private void FolderLargeAnim(float duration, View3D view3D) {
		if (view3D == null || duration == 0) {
			return;
		}
		if (FolderLargeTween != null) {
			return;
		}
		view3D.stopTween();
		view3D.setVisible(true);
		view3D.setScale(0, 0);
		FolderLargeTween = view3D
				.startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT, duration,
						1.2f, 1.2f, 0).setUserData(view3D).setCallback(this);
		
	}

	private void FolderSmallAnim(float duration, View3D view3D) {
		if (view3D == null || duration == 0) {
			return;
		}
		if (FolderSmallTween != null) {
			return;
		}
		view3D.stopTween();
		FolderSmallTween = view3D
				.startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT, duration,
						0, 0, 0).setUserData(view3D).setCallback(this);
	}

	@Override
	public void onEvent(int type, BaseTween source) {
		// TODO Auto-generated method stub
		if (FolderSmallTween != null && type == TweenCallback.COMPLETE) {
			View3D view3D = (View3D) source.getUserData();
			// Log.e("testFolder", "why FolderSmallTween onEvent me");
			if (view3D instanceof FolderIcon3D) {
				FolderIcon3D tempFolder = (FolderIcon3D) view3D;
				if (tempFolder.mInfo.contents.size() == 0) {
					view3D.setVisible(false);
				}
			}
			FolderSmallTween.free();
			FolderSmallTween = null;
		}
		if (FolderLargeTween != null && type == TweenCallback.COMPLETE) {
			FolderLargeTween.free();
			FolderLargeTween = null;
		}
	}
	
}
