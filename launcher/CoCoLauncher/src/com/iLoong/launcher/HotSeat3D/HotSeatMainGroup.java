package com.iLoong.launcher.HotSeat3D;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import com.iLoong.launcher.Desktop3D.Log;
import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.iLoong.launcher.Desktop3D.CellLayout3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DragSource3D;
import com.iLoong.launcher.Desktop3D.DropTarget3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.IconBase3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.SideBar.SideBar;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupOBJ3D;
import com.iLoong.launcher.Widget3D.Contact3DShortcut;
import com.iLoong.launcher.Widget3D.Folder3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class HotSeatMainGroup extends ViewGroupOBJ3D {

	private final int SCROLL_UP = 0;
	private final int SCROLL_DOWN = 1;
	private final int SCROLL_LEFT = 2;
	private final int SCROLL_RIGHT = 3;

	public static final int MAX_ICON_NUM = 20;

	private final float VELOCITY_DIV = 30f;

	private GridView3D mShortcutView;
	private GridView3D mFocusGridView;

	private int scrollDir = -1;
	private boolean isFling = false;
	private float velocity = 0f;
	private Tween flingTween = null;

	private boolean bPermitAnim = true;
	public boolean dragAble = true;
	private boolean isScroll = false;
	private Workspace3D workspace;
	private float cellWidth = 0;
	private float iconTempScale = 1f;

	// static Texture t = new Texture(Gdx.files.internal("bgtest.png"));
	public HotSeatMainGroup(String name) {
		super(name);

	}

	public boolean isInShortcutList() {
		return mFocusGridView == mShortcutView;
	}

	public HotSeatMainGroup(String name, int width, int height) {
		super(name);

		// float gridViewHeight = height - SideBar.SIDEBAR_OPCITY_WIDTH;
		int padding = (int) ((height - R3D.workspace_cell_height) / 2);
		if (padding < 0) {
			padding = 0;
		}

		cellWidth = R3D.workspace_cell_width + padding;
		mShortcutView = new GridView3D("shortcutview",
				cellWidth * MAX_ICON_NUM, height, MAX_ICON_NUM, 1);
		int iconHeight = (int) Utils3D.getIconBmpHeight();
		int paddingTop = (int) ((mShortcutView.height-iconHeight)/2)+R3D.hot_sidebar_top_margin;
		mShortcutView.setPadding(R3D.hot_grid_right_margin,
				R3D.hot_grid_right_margin, paddingTop, 0);
		mShortcutView.setPosition(0, 0);

		addView(mShortcutView);

		setVisible(HotSeat3D.TYPE_ICON);
		// setBackgroud(new NinePatch(R3D.getTextureRegion("menu-bg"), 2, 2, 0,
		// 0));
		// mShortcutView.setBackgroud(new NinePatch(t));
		setSize(width, height);
		this.setOrigin(width / 2, height / 2);
		mFocusGridView = mShortcutView;
	}

	public void setVisible(int type) {
		// if(ClingManager.getInstance().widgetClingFired)SendMsgToAndroid.sendRefreshClingStateMsg();
	}

	public void gridPosReset() {
		if (mFocusGridView == null) {
			return;
		}
		float mFocusViewWidth = mFocusGridView.getEffectiveWidth();
		if (mFocusViewWidth <= width) {
			mFocusGridView.x = 0;
		} else {
			if (scrollDir == SCROLL_LEFT) {
				mFocusGridView.x = width - mFocusViewWidth;
			}
			if (scrollDir == SCROLL_RIGHT) {
				mFocusGridView.x = 0;
			}
		}
	}

	@Override
	public void removeAllViews() {
		mFocusGridView.removeAllViews();
	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		//Log.e("hotseatmaingroup", "onTouchDown");
		super.onTouchDown(x, y, pointer);//icon点击变暗效果
		Log.e("hotseatmaingroup", "onTouchDown:"+mFocusGridView.lastTouchedChild);
		mFocusGridView.stopTween();
		mFocusGridView.setUser(0);
		isFling = false;
		bPermitAnim = true;
		requestFocus();
		return true;// super.onTouchDown(x, y, pointer);
	}

	@Override
	public boolean scroll(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		//Log.e("hotseatmaingroup", "scroll:"+mFocusGridView.getFocusView());
		bPermitAnim = true;
		float mFocusViewWidth = mFocusGridView.getEffectiveWidth();

		if (!isScroll
				&& (deltaX == 0 || Math.abs(deltaY) / Math.abs(deltaX) > 1)) {
			this.setTag(deltaY);
			bPermitAnim = false;

			// if (mFocusGridView.getTween()!=null &&
			// mFocusGridView.getTween().isFinished()==false)
			{
				mFocusGridView.stopTween();
				mFocusGridView.setUser(0);
				isFling = false;
				Log.v("scroll", " scroll 333 mFocusViewWidth="
						+ mFocusViewWidth + "  scrollDir=" + scrollDir
						+ " mFocusGridView.x=" + mFocusGridView.x);
				if (mFocusViewWidth <= width) {
					mFocusGridView.x = 0;

				} else if (mFocusGridView.x < 0
						|| mFocusGridView.x + mFocusViewWidth > width) {

					if (scrollDir == SCROLL_LEFT) {
						Log.v("scroll", " scroll 111  mFocusViewWidth="
								+ mFocusViewWidth + "  scrollDir=" + scrollDir
								+ " mFocusGridView.x=" + mFocusGridView.x);
						mFocusGridView.x = width - mFocusViewWidth;
					}
					if (scrollDir == SCROLL_RIGHT) {
						Log.v("scroll", " scroll 222  mFocusViewWidth="
								+ mFocusViewWidth + "  scrollDir=" + scrollDir
								+ " mFocusGridView.x=" + mFocusGridView.x);
						mFocusGridView.x = 0;
					}
				}
			}

			return viewParent.onCtrlEvent(this,
					HotSeat3D.MSG_MAINGROUP_SCROLL_DOWN);
		}

		if (!super.scroll(x, y, deltaX, deltaY)) {
			
			/************************ added by zhenNan.ye begin ***************************/
			if (DefaultLayout.enable_particle)
			{
				if (ParticleManager.particleManagerEnable)
				{
					ParticleManager.disableClickIcon = true;
				}
			}
			/************************ added by zhenNan.ye end ***************************/	
			
			isScroll = true;
			if (deltaX > 0) {
				scrollDir = SCROLL_RIGHT;

			}

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
		if(mFocusGridView.lastTouchedChild!=null){
			mFocusGridView.lastTouchedChild.color.a=1f;
		}
//		Log.e("hotseatmaingroup", "onTouchUp:"
//				+ mFocusGridView.lastTouchedChild);
		super.onTouchUp(x, y, pointer);
		
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
		//Log.e("hotseatmaingroup", "fling:"+mFocusGridView.lastTouchedChild);
		if (bPermitAnim == false) {
			scrollDir = -1;
			return true;
		}
		if (velocityX > 0) {
			scrollDir = SCROLL_RIGHT;
		}
		// scrollDir = SCROLL_RIGHT;
		// return true;
		else if (velocityX < 0)
			scrollDir = SCROLL_LEFT;
		if (Math.abs(velocityX) < VELOCITY_DIV * 2) {
			return true;
		}

		isFling = true;
		Log.v("scroll", " 222 fling name:" + this.name + " isFling=" + isFling);
		velocity = velocityX / VELOCITY_DIV;
		mFocusGridView.setUser(velocity);
		mFocusGridView.stopTween();
		flingTween = mFocusGridView.startTween(View3DTweenAccessor.USER,
				Cubic.OUT, 2.5f, 0, 0, 0);
		return true;
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

	public void setWorkspace(View3D v) {
		this.workspace = (Workspace3D) v;
	}

	public View3D getFocusView() {
		return mFocusGridView.getFocusView();
	}

	public void backtoOrig(ArrayList<View3D> child_list) {
		ItemInfo itemInfo;
		View3D findView = null;

		for (int i = 0; i < child_list.size(); i++) {
			View3D view = child_list.get(i);
			mFocusGridView.calcCoordinate(view);
			if (view instanceof FolderIcon3D) {
				((FolderIcon3D) view).setLongClick(false);
			}
			view.stopTween();
			itemInfo = ((IconBase3D) view).getItemInfo();
			if (itemInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
				workspace.addInCurrenScreen(view, itemInfo.x, itemInfo.y);
				view.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT, 0.5f,
						itemInfo.x, itemInfo.y, 0);
			} else if (itemInfo.container >= 0) {
				/* From 文件 */
				UserFolderInfo folderInfo = iLoongLauncher.getInstance()
						.getFolderInfo(itemInfo.container);
				if (folderInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
					findView = workspace.getViewByItemInfo(folderInfo);
				}

				if (findView != null && findView instanceof FolderIcon3D) {
					((FolderIcon3D) findView).onDrop(child_list, 0, 0);
				}

			}

		}

	}

	public void addItems(List<View3D> child_list) {
		for (View3D view : child_list) {
			if (view instanceof Icon3D) {
				mFocusGridView.calcCoordinate(view);
				int index = mFocusGridView.getIndex(
						(int) (view.x + view.width),
						(int) mFocusGridView.height / 2);
				float scale = 0.8f / (view.height / this.height);
//				Log.e("test", "before view width:" + view.getWidth()
//						+ " height:" + view.getHeight());
				((Icon3D) view).tempWidth = view.width;
				((Icon3D) view).tempHeight = view.height;
				if (scale < 1) {
//					view.setSize((int)(view.width * scale), (int)(view.height * scale));
				}
				// Log.e("test", "after view width:" + view.getWidth()
				// + " height:" + view.getHeight());
				if (index != -1 && index < mFocusGridView.getChildCount()) {
					mFocusGridView.addItem(view, index);
				} else {
					mFocusGridView.addItem(view);
				}
				ItemInfo info = ((Icon3D) view).getItemInfo();
				info.screen = view.getIndexInParent();
				info.angle = HotSeat3D.TYPE_ICON;
				Root3D.addOrMoveDB(info,
						LauncherSettings.Favorites.CONTAINER_HOTSEAT);
			}
		}
	}

	public void bindItem(View3D view) {
		float scale = 0.8f / (view.height / this.height);
		if (scale < 1) {
			// view.setScale(scale, scale);
//			view.setSize((int)(view.width * scale), (int)(view.height * scale));
		}

		mShortcutView.addItem(view);
	}

	public void removeItem(View3D view) {
		mShortcutView.removeView(view);
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
			if (!mFocusGridView.isAutoMove && mFocusGridView.getTween() == null)
				mFocusGridView.startTween(View3DTweenAccessor.USER, Cubic.OUT,
						0.5f, 0f, 0f, 0f);
			// mFocusGridView.setUser(mFocusGridView.getUser() / 1.5f);
		}

		if (mFocusGridView.isAutoMove && mFocusGridView.getFocusView() != null) {
			if (mFocusGridView.isScrollToEnd(-(int) mFocusGridView.getUser())) {
				mFocusGridView.x += mFocusGridView.getUser();
				mFocusGridView.getFocusView().x += -mFocusGridView.getUser();
			}
		} else {
			if (Math.abs(mFocusGridView.getUser()) > 2) {
				mFocusGridView.x += mFocusGridView.getUser();
			} else {
				if (isFling) {
					Log.v("scroll", " 111 draw  " + "  scrollDir=" + scrollDir
							+ " mFocusGridView.x=" + mFocusGridView.x);
					startScrollTween();
				}
			}
		}
	}

	@Override
	protected void drawChildren(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		parentAlpha *= color.a;
		if (cullingArea != null) {
			if (transform) {
				for (int i = 0; i < children.size(); i++) {
					View3D child = children.get(i);
					if (!child.visible)
						continue;
					if (child.x <= cullingArea.x + cullingArea.width
							&& child.x + child.width >= cullingArea.x
							&& child.y <= cullingArea.y + cullingArea.height
							&& child.y + child.height >= cullingArea.y) {
						if (child.background9 != null) {
							child.background9.draw(batch, child.x, child.y,
									child.width, child.height);
						}
						child.draw(batch, parentAlpha);
					}
				}
				batch.flush();
			} else {
				float offsetX = x;
				float offsetY = y;
				x = 0;
				y = 0;
				for (int i = 0; i < children.size(); i++) {
					View3D child = children.get(i);
					if (!child.visible)
						continue;
					if (child.x <= cullingArea.x + cullingArea.width
							&& child.x + child.width >= cullingArea.x
							&& child.y <= cullingArea.y + cullingArea.height
							&& child.y + child.height >= cullingArea.y) {
						child.x += offsetX;
						child.y += offsetY;
						if (child.background9 != null) {
							child.background9.draw(batch, child.x, child.y,
									child.width, child.height);
						}
						child.draw(batch, parentAlpha);
						child.x -= offsetX;
						child.y -= offsetY;
					}
				}
				x = offsetX;
				y = offsetY;
			}
		} else {
			if (transform) {
				for (int i = 0; i < children.size(); i++) {
					View3D child = children.get(i);
					if (!child.visible)
						continue;
					if (child instanceof ViewGroup3D) {
						if (child.background9 != null) {
							if (child.is3dRotation())
								child.applyTransformChild(batch);
							batch.setColor(child.color.r, child.color.g,
									child.color.b, child.color.a * parentAlpha);
							child.background9.draw(batch, child.x, child.y,
									child.width, child.height);
							if (child.is3dRotation())
								child.resetTransformChild(batch);
						}
						child.draw(batch, parentAlpha);
						continue;
					}
					if (child.is3dRotation())
						child.applyTransformChild(batch);
					if (child.background9 != null) {
						batch.setColor(child.color.r, child.color.g,
								child.color.b, child.color.a * parentAlpha);
						child.background9.draw(batch, child.x, child.y,
								child.width, child.height);
					}
					child.draw(batch, parentAlpha);
					if (child.is3dRotation())
						child.resetTransformChild(batch);
				}
				// batch.flush();
			} else {
				float offsetX = x;
				float offsetY = y;
				x = 0;
				y = 0;
				for (int i = 0; i < children.size(); i++) {
					View3D child = children.get(i);
					if (!child.visible)
						continue;
					child.x += offsetX;
					child.y += offsetY;
					if (child.background9 != null) {
						batch.setColor(child.color.r, child.color.g,
								child.color.b, child.color.a * parentAlpha);
						child.background9.draw(batch, child.x, child.y,
								child.width, child.height);
					}
					child.draw(batch, parentAlpha);
					child.x -= offsetX;
					child.y -= offsetY;
				}
				x = offsetX;
				y = offsetY;
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
			case GridView3D.MSG_VIEW_OUTREGION:
				return viewParent.onCtrlEvent(this, HotSeat3D.MSG_ON_DROP);
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
										LauncherSettings.Favorites.CONTAINER_HOTSEAT);
							}
						}

					}
				}
				return false;
			case GridView3D.MSG_VIEW_OUTREGION_DRAG:
				GridView3D focusGrid = (GridView3D) sender;
				View3D focusView = focusGrid.getFocusView();
				if (focusView != null) {
					//float scale = 0.8f / (focusView.height / this.height);
					focusView.stopTween();
					focusView.setScale(1f, 1f);
//					Log.e("test",
//							"before focusView width:" + focusView.getWidth()
//									+ " height:" + focusView.getHeight()+" scale:"+focusView.scaleX+" "+focusView.scaleY);
					float tempWidth=((Icon3D) focusView).tempWidth;
					float tempHeight=((Icon3D) focusView).tempHeight;
					focusView.setSize(tempWidth, tempHeight);
//					Log.e("test",
//							"after focusView width:" + focusView.getWidth()
//									+ " height:" + focusView.getHeight()+" scale:"+focusView.scaleX+" "+focusView.scaleY);
				}

			}
		}

		return super.onCtrlEvent(sender, event_id);
	}

	public void startOutDragAnim() {
		float mFocusViewX = mFocusGridView.x;
		float mFocusViewWidth = mFocusGridView.getEffectiveWidth();
		if (mFocusViewWidth <= width) {

			mFocusGridView.stopTween();
			mFocusGridView.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT,
					1f, 0, mFocusGridView.y, 0);
		} else if (mFocusViewX < 0 || mFocusViewX + mFocusViewWidth > width) {
			if (isScrollEnd()) {
				Log.d("scroll", "scrollEnd");
				// mFocusGridView.stopTween();
				//
				// mFocusGridView.startTween(View3DTweenAccessor.POS_XY,
				// Cubic.OUT, 1f, width - mFocusViewWidth,
				// mFocusGridView.y, 0);

				if (scrollDir == SCROLL_LEFT) {
					mFocusGridView.stopTween();
					mFocusGridView.startTween(View3DTweenAccessor.POS_XY,
							Cubic.OUT, 1f, width - mFocusViewWidth,
							mFocusGridView.y, 0);
				} else if (scrollDir == SCROLL_RIGHT) {
					mFocusGridView.stopTween();
					mFocusGridView.startTween(View3DTweenAccessor.POS_XY,
							Cubic.OUT, 1f, 0, mFocusGridView.y, 0);
				}
			}

		}
	}

	private void startScrollTween() {
		if (bPermitAnim == false || mFocusGridView == null
				|| mFocusGridView.getChildCount() == 0)
			return;

		float mFocusViewX = (mFocusGridView.x /* + mFocusGridView.getEffectiveX() */);
		float mFocusViewWidth = mFocusGridView.getEffectiveWidth();
		Log.v("scroll", " startScrollTween scrollDir :" + scrollDir
				+ " mFocusViewX,mFocusViewWidth:" + mFocusViewX + " "
				+ mFocusViewWidth);
		isFling = false;
		if (mFocusViewWidth <= width && isScrollEnd()) {
			{
				Log.v("scroll", " 222 startScrollTween scrollDir :" + scrollDir
						+ " mFocusViewX,mFocusViewWidth:" + mFocusViewX + " "
						+ mFocusViewWidth);
				mFocusGridView.stopTween();
				mFocusGridView.startTween(View3DTweenAccessor.POS_XY,
				// Cubic.OUT, 1f, width - mFocusViewWidth,
						Cubic.OUT, 1f, 0, mFocusGridView.y, 0);
				isFling = false;
			}
		} else if (mFocusViewX < 0 || mFocusViewX + mFocusViewWidth > width) {
			if (isScrollEnd()) {
				Log.d("scroll", "scrollEnd");
				if (scrollDir == SCROLL_LEFT) {
					Log.v("scroll", "333 startScrollTween scrollDir :"
							+ scrollDir + " mFocusViewX,mFocusViewWidth:"
							+ mFocusViewX + " " + mFocusViewWidth);
					mFocusGridView.stopTween();
					mFocusGridView.startTween(View3DTweenAccessor.POS_XY,
							Cubic.OUT, 1f, width - mFocusViewWidth,
							mFocusGridView.y, 0);
					isFling = false;
				} else if (scrollDir == SCROLL_RIGHT) {
					Log.v("scroll", " 444 startScrollTween scrollDir :"
							+ scrollDir + " mFocusViewX,mFocusViewWidth:"
							+ mFocusViewX + " " + mFocusViewWidth);
					mFocusGridView.stopTween();
					mFocusGridView.startTween(View3DTweenAccessor.POS_XY,
							Cubic.OUT, 1f, 0, mFocusGridView.y, 0);
					isFling = false;
				}
			}

		}
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if (keycode == KeyEvent.KEYCODE_BACK)
			return true;
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub

		if (keycode == KeyEvent.KEYCODE_BACK) {
			boolean canGoback = true;
			CellLayout3D curCellLayout = iLoongLauncher.getInstance()
					.getD3dListener().getCurrentCellLayout();
			for (int i = 0; i < curCellLayout.getChildCount(); i++) {
				if (curCellLayout.getChildAt(i) instanceof Widget3D) {
					Widget3D curWidget3D = (Widget3D) curCellLayout
							.getChildAt(i);
					if (curWidget3D.isOpened()) {
						canGoback = false;
						break;
					}
				}
			}
			if (canGoback) {
				this.setTag(1f);
				return viewParent.onCtrlEvent(this,
						HotSeat3D.MSG_MAINGROUP_SCROLL_DOWN);
			}
		}
		return super.keyUp(keycode);
	}

	public int getShortcutCount() {
		// TODO Auto-generated method stub
		return mFocusGridView.getChildCount();
	}

	public GridView3D getShortcutGridview() {
		return mShortcutView;
	}
}
