package com.iLoong.launcher.SideBar;

import java.util.List;

import android.R.integer;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import com.iLoong.launcher.Desktop3D.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Contact3DShortcut;
import com.iLoong.launcher.Widget3D.Folder3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class SidebarMainGroup extends ViewGroup3D {

	private final int SCROLL_UP = 0;
	private final int SCROLL_DOWN = 1;
	private final int SCROLL_LEFT = 2;
	private final int SCROLL_RIGHT = 3;

	public static final int MAX_ICON_NUM = 20;

	private final float VELOCITY_DIV = 30f;
	private List<Widget3DShortcut> mWidgetList;
	private GridView3D mWidgetView;
	private GridView3D mShortcutView;
	private GridView3D mFocusGridView;

	private int scrollDir = -1;
	private boolean isFling = false;
	private float velocity = 0f;
	private Tween flingTween = null;

	public boolean dragAble = true;
	private boolean isScroll = false;

	// static Texture t = new Texture(Gdx.files.internal("bgtest.png"));
	public SidebarMainGroup(String name) {
		super(name);

	}

	void InitWidget() {
		mWidgetView.addItem(Desktop3DListener.folder3DHost);
		mWidgetView.addItem(Desktop3DListener.contact3DHost);

		mWidgetList = Widget3DManager.getInstance().getWidgetList();
		for (int i = 0; i < mWidgetList.size(); i++) {
			Widget3DShortcut view = mWidgetList.get(i);
			mWidgetView.addItem(view);
		}

		for (int j = 0; j < Widget3DManager.defIcon3DList.size(); j++) {
			mWidgetView.addItem(Widget3DManager.defIcon3DList.get(j));
		}
	}
	
	public boolean isInShortcutList() {
		return mFocusGridView == mShortcutView;
	}

	public SidebarMainGroup(String name, int width, int height) {
		super(name);

		// float gridViewHeight = height - SideBar.SIDEBAR_OPCITY_WIDTH;
		int padding = (int) ((height - R3D.workspace_cell_height) / 2);

		mWidgetView = new GridView3D("widgetgridview",
				(R3D.workspace_cell_width + padding) * MAX_ICON_NUM, height,
				MAX_ICON_NUM, 1);
		mWidgetView.setPadding(padding, padding, padding, padding);
		mWidgetView.setPosition(0, 0);
		mWidgetView.setAutoDrag(false);

		mShortcutView = new GridView3D("shortcutview",
				(R3D.workspace_cell_width + padding) * MAX_ICON_NUM, height,
				MAX_ICON_NUM, 1);
		mShortcutView.setPadding(padding, padding, padding, padding);
		mShortcutView.setPosition(0, 0);

		addView(mWidgetView);
		addView(mShortcutView);

		setVisible(SideBar.TYPE_ICON);
		setBackgroud(new NinePatch(R3D.getTextureRegion("menu-bg"), 2, 2, 0, 0));
		// mShortcutView.setBackgroud(new NinePatch(t));
		setSize(width, height);

		//InitWidget();

	}

	public void setVisible(int type) {
		if (type == SideBar.TYPE_ICON) {
			mFocusGridView = mShortcutView;
			mShortcutView.show();
			mWidgetView.hide();
		} else if (type == SideBar.TYPE_WIDGET) {
			mFocusGridView = mWidgetView;
			mShortcutView.hide();
			mWidgetView.show();
		}
		//if(ClingManager.getInstance().widgetClingFired)SendMsgToAndroid.sendRefreshClingStateMsg();
	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		mFocusGridView.stopTween();
		mFocusGridView.setUser(0);
		isFling = false;
		requestFocus();
		return true;// super.onTouchDown(x, y, pointer);
	}

	@Override
	public boolean scroll(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub

		if (!isScroll
				&& (deltaX == 0 || Math.abs(deltaY) / Math.abs(deltaX) > 1)) {
			this.setTag(deltaY);
			return viewParent.onCtrlEvent(this,
					SideBar.MSG_MAINGROUP_SCROLL_DOWN);
		}
		if (!super.scroll(x, y, deltaX, deltaY)) {
			isScroll = true;
			if (deltaX > 0)
				scrollDir = SCROLL_RIGHT;
			else if (deltaX < 0)
				scrollDir = SCROLL_LEFT;

			if (mFocusGridView != null && mFocusGridView.isVisible()) {
				mFocusGridView.x += deltaX;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onTouchUp(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		if (!isFling) {
			startScrollTween();
		}
		releaseFocus();
		isScroll = false;
		return super.onTouchUp(x, y, pointer);
	}

	@Override
	public boolean onTouchDragged(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean fling(float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		Log.v("fling", " name:" + this.name + "  vx:" + velocityX + " vy:"
				+ velocityY);
		if (velocityX > 0)
			scrollDir = SCROLL_RIGHT;
		else if (velocityX < 0)
			scrollDir = SCROLL_LEFT;
		if (Math.abs(velocityX) < VELOCITY_DIV * 2) {
			return true;
		}

		isFling = true;

		velocity = velocityX / VELOCITY_DIV;
		mFocusGridView.setUser(velocity);
		mFocusGridView.stopTween();
		flingTween = mFocusGridView.startTween(View3DTweenAccessor.USER,
				Cubic.OUT, 2.5f, 0, 0, 0);
		return true;
	}

	@Override
	public boolean onLongClick(float x, float y) {
		SendMsgToAndroid.sendHideWorkspaceMsg();
		boolean ret = false;
		if (!dragAble) {
			if (mFocusGridView == mShortcutView) {
				mFocusGridView.setAutoDrag(false);
			}
			viewParent.onCtrlEvent(this, SideBar.MSG_LONGCLICK_INAPPLIST);
		}

		ret = super.onLongClick(x, y);
		if (!dragAble) {
			if (mFocusGridView == mShortcutView) {
				mFocusGridView.setAutoDrag(true);
			}
		}
		return ret;
	}

	@Override
	public boolean onClick(float x, float y) {
		// TODO Auto-generated method stub
		if (mFocusGridView == mWidgetView) {

		}
		return super.onClick(x, y);
	}

	@Override
	public boolean onDoubleClick(float x, float y) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onEvent(int type, BaseTween source) {
		// TODO Auto-generated method stub
		if (source == flingTween && type == TweenCallback.COMPLETE) {
			startScrollTween();
		}

	}

	public View3D getFocusView() {
		return mFocusGridView.getFocusView();
	}

	public void addItems(List<View3D> child_list) {
		for (View3D view : child_list) {
			if (view instanceof Icon3D) {
				mFocusGridView.calcCoordinate(view);
				int index = mFocusGridView.getIndex(
						(int) (view.x + view.width),
						(int) mFocusGridView.height / 2);
				if (index != -1 && index < mFocusGridView.getChildCount()) {
					mFocusGridView.addItem(view, index);
				} else {
					mFocusGridView.addItem(view);
				}
				ItemInfo info = ((Icon3D) view).getItemInfo();
				info.screen = view.getIndexInParent();
				Root3D.addOrMoveDB(info,
						LauncherSettings.Favorites.CONTAINER_SIDEBAR);
			}
		}
	}

	public void bindWidget3D(Widget3DShortcut shortcut) {
		mWidgetView.addItem(shortcut);
	}

	public void unBindWidget3D(String packageName) {
		int count = mWidgetView.getChildCount();
		for (int i = 0; i < count; i++) {
			View3D view = mWidgetView.getChildAt(i);
			if (view instanceof Widget3DShortcut) {
				Widget3DShortcut shortcut = (Widget3DShortcut) view;
				if (shortcut.getResolveInfo() != null
						&& shortcut.getResolveInfo().activityInfo.packageName
								.equals(packageName)) {
					shortcut.remove();
					break;
				}
			}
		}
	}

	public void updateWidget3D(String packageName) {
		Intent intent = new Intent("com.iLoong.widget", null);
		PackageManager pm = iLoongApplication.ctx.getPackageManager();
		List<ResolveInfo> infoList = pm.queryIntentActivities(intent,
				PackageManager.GET_RECEIVERS | PackageManager.GET_META_DATA);
		ResolveInfo resolveInfo = null;
		for (ResolveInfo resolve : infoList) {
			if (resolve.activityInfo.packageName.equals(packageName)) {
				resolveInfo = resolve;
				break;
			}
		}
		if (resolveInfo != null) {
			int count = mWidgetView.getChildCount();
			for (int i = 0; i < count; i++) {
				View3D view = mWidgetView.getChildAt(i);
				if (view instanceof Widget3DShortcut) {
					Widget3DShortcut shortcut = (Widget3DShortcut) view;
					if (shortcut.getResolveInfo() != null
							&& shortcut.getResolveInfo().activityInfo.packageName
									.equals(packageName)) {
						shortcut.remove();
						break;
					}
				}
			}
			Widget3DShortcut shortcut = new Widget3DShortcut(
					"Widget3DShortcut", resolveInfo);
			mWidgetView.addItem(shortcut);
		}
		Log.e("sidebar updateWidget3D", "after:" + mWidgetView.getChildCount());
	}

	public void bindItem(Icon3D view) {

		mShortcutView.addItem(view);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		super.draw(batch, parentAlpha);

		if (isScrollEnd()) {
			if (flingTween != null && !flingTween.isFinished()) {
				mFocusGridView.stopTween();
				flingTween = null;
			}
			if (!mFocusGridView.isAutoMove)
				mFocusGridView.setUser(mFocusGridView.getUser() / 1.5f);
		}
		
		if (mFocusGridView.isAutoMove) {
			if (mFocusGridView.isScrollToEnd(-(int)mFocusGridView.getUser())) {
				mFocusGridView.x += mFocusGridView.getUser();
				mFocusGridView.getFocusView().x += -mFocusGridView.getUser();
			}
		} else {
			if (Math.abs(mFocusGridView.getUser()) > 2) {
					mFocusGridView.x += mFocusGridView.getUser();
			} else {
				if (isFling) {
					startScrollTween();
				}
			}
		}
	}

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

	@Override
	public boolean onCtrlEvent(View3D sender, int event_id) {
		// TODO Auto-generated method stub
		if (sender instanceof GridView3D) {
			GridView3D view = (GridView3D) sender;
			switch (event_id) {
			case GridView3D.MSG_VIEW_MOVED:
				if (view == mShortcutView) {
					for (int i = 0; i < mShortcutView.getChildCount(); i++) {
						View3D view1 = mShortcutView.getChildAt(i);
						if (view1 instanceof Icon3D) {
							ItemInfo info = ((Icon3D) view1).getItemInfo();
							if (info.screen != i) {
								info.screen = i;
								Root3D.addOrMoveDB(
										info,
										LauncherSettings.Favorites.CONTAINER_SIDEBAR);
							}
						}

					}
				}
				return false;
			}
		}
		return super.onCtrlEvent(sender, event_id);
	}

	private void startScrollTween() {
		if (mFocusGridView == null || mFocusGridView.getChildCount() == 0)
			return;

		float mFocusViewX = (mFocusGridView.x/* + mFocusGridView.getEffectiveX() */);
		float mFocusViewWidth = mFocusGridView.getEffectiveWidth();
		Log.v("scroll", " scrollDir :" + scrollDir
				+ " mFocusViewX,mFocusViewWidth:" + mFocusViewX + " "
				+ mFocusViewWidth);
		isFling = false;
		if (mFocusViewWidth <= width) {
			if (scrollDir == SCROLL_LEFT) {
				mFocusGridView.stopTween();
				mFocusGridView.startTween(View3DTweenAccessor.POS_XY,
						Cubic.OUT, 1f, 0, mFocusGridView.y, 0);
				isFling = false;
			} else if (scrollDir == SCROLL_RIGHT) {
				mFocusGridView.stopTween();
				mFocusGridView.startTween(View3DTweenAccessor.POS_XY,
						Cubic.OUT, 1f, width - mFocusViewWidth,
						mFocusGridView.y, 0);
				isFling = false;
			}
		} else if (mFocusViewX < 0 || mFocusViewX + mFocusViewWidth > width) {
			if (isScrollEnd()) {
				Log.d("scroll", "scrollEnd");
				if (scrollDir == SCROLL_LEFT) {
					mFocusGridView.stopTween();
					mFocusGridView.startTween(View3DTweenAccessor.POS_XY,
							Cubic.OUT, 1f, width - mFocusViewWidth,
							mFocusGridView.y, 0);
					isFling = false;
				} else if (scrollDir == SCROLL_RIGHT) {
					mFocusGridView.stopTween();
					mFocusGridView.startTween(View3DTweenAccessor.POS_XY,
							Cubic.OUT, 1f, 0, mFocusGridView.y, 0);
					isFling = false;
				}
			}

		}
	}

	public int getShortcutCount() {
		// TODO Auto-generated method stub
		return mFocusGridView.getChildCount();
	}

	public GridView3D getShortcutGridview() {
		return mShortcutView;
	}

}