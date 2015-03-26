package com.iLoong.launcher.HotSeat3D;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import com.iLoong.launcher.Desktop3D.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.DropTarget3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.IconBase3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class HotGridView3D extends ViewGroup3D {
	public static final int MSG_VIEW_OUTREGION = 0;
	public static final int MSG_VIEW_MOVED = 1;
	public static final int MSG_CLOSE = 2;
	public static final int MSG_VIEW_OUTREGION_DRAG = 3;
	public static final int MSG_VIEW_START_MAIN = 4;
	public static final int MSG_ADD_DRAGLAYER = 5;
	public static final int MSG_VIEW_CREATE_FOLDER = 6;
	public static final int MSG_VIEW_MERGE_FOLDER = 7;
	public static final int MSG_UPDATE_OBJ3D_SHOW_STATUS = 8;
	public static final int MSG_UPDATE_OBJ3D_INDEX_SHOW_STATUS = 9;
	private View3D mFocus;

	private int mPosx;
	private int mPosy;

	private int mCountX;
	private int mCountY;
	// private int mGapX;
	// private int mGapY;
	private int mCellWidth;
	private int mCellHeight;

	private int mPaddingLeft;
	private int mPaddingRight;
	private int mPaddingTop;
	private int mPaddingBottom;

	private boolean animation_flag = false;
	private float animation_delay = 0f;
	private boolean auto_focus = true;
	Timeline animation_line = null;
	Tween animation_focus = null;

	public static final int State_CreateFolder = 1;
	public static final int State_ChangePosition = 0;
	private int dragState = State_ChangePosition;
	
	/************************ added by zhenNan.ye begin *************************/
	private float last_x = 0;
	private float last_y = 0;
	/************************ added by zhenNan.ye end ***************************/

	public HotGridView3D(String name, float width, float height, int countx,
			int county) {
		super(name);

		this.width = width;
		this.height = height;
		mCountX = countx;
		mCountY = county;
		mPaddingLeft = 0;
		mPaddingRight = 0;
		mPaddingTop = 0;
		mPaddingBottom = 0;
		mCellWidth = (int) ((this.width - mPaddingLeft - mPaddingRight) / mCountX);
		mCellHeight = (int) ((this.height - mPaddingTop - mPaddingBottom) / mCountY);

	}

	public void setAnimationDelay(float delay) {
		animation_delay = delay;
	}

	public void stopAnimation() {
		if (animation_line != null && !animation_line.isFinished()) {
			animation_line.free();
			animation_line = null;
		}
		if (animation_focus != null && !animation_focus.isFinished()) {
			// animation_focus.kill();
			if (mFocus != null)
				mFocus.stopTween();
			animation_focus = null;
		}
	}

	public void enableAnimation(boolean open_anim) {
		animation_flag = false;
		if (animation_flag == false) {
			// stopAnimation();
			// layout(0);
		}
	}

	public void addFolder(FolderIcon3D child) {
		addView(child);
	}

	public void addItem(View3D child) {
		addView(child);
		layout(0);
	}

	public void addItem(List<View3D> child_list) {
		for (View3D i : child_list) {
			addView(i);
		}
		layout(0);
	}

	public void addItem(View3D[] child_list) {
		for (View3D i : child_list) {
			addView(i);
		}
		layout(0);
	}

	public void addItem(View3D child, int index) {
		// addViewAt(index,child);
		InsertHotView(child, index);
		layout(0);
	}

	@Override
	public void addView(View3D actor) {
		super.addView(actor);
		this.viewParent.onCtrlEvent(this, MSG_UPDATE_OBJ3D_SHOW_STATUS);
		if (actor instanceof DropTarget3D) {
			// mDragController.addDropTarget((DropTarget)child);
			this.setTag(actor);
			viewParent.onCtrlEvent(this, MSG_ADD_DRAGLAYER);
		}
	}

	@Override
	public void addViewAt(int index, View3D actor) {
		super.addViewAt(index, actor);
		this.viewParent.onCtrlEvent(this, MSG_UPDATE_OBJ3D_SHOW_STATUS);
		if (actor instanceof DropTarget3D) {
			// mDragController.addDropTarget((DropTarget)child);
			this.setTag(actor);
			viewParent.onCtrlEvent(this, MSG_ADD_DRAGLAYER);
		}
	}

	public void setPadding(int left, int right, int top, int bottom) {
		mPaddingLeft = left;
		mPaddingRight = right;
		mPaddingTop = top;
		mPaddingBottom = bottom;
	}

	public void setSize(int width, int height) {
		if (width == this.width && height == this.height)
			return;
		this.width = width;
		this.height = height;
		this.originX = width / 2;
		this.originY = height / 2;
		layout(0);
	}

	public void setCellCount(int countx, int county) {
		if (countx == mCountX && county == mCountY)
			return;
		mCountX = countx;
		mCountY = county;
		layout(0);
	}

	public int getCellCountX() {
		return mCountX;
	}

	public int getCellCountY() {
		return mCountY;
	}

	public void setAutoDrag(boolean drag) {
		auto_focus = drag;
	}

	public void layout_pub(int index, boolean animation) {
		animation_flag = animation;
		layout(index);
	}

	private void layout(int index) {
		if (mCountX == 0 || mCountY == 0)
			return;
		View3D cur_view;
		mCellWidth = (int) ((this.width - mPaddingLeft - mPaddingRight) / mCountX);
		mCellHeight = (int) ((this.height - mPaddingTop - mPaddingBottom) / mCountY);
		int size = getChildCount();
		if (animation_flag) {
			stopAnimation();
			animation_line = Timeline.createParallel();
		}
		for (int i = index; i < size; i++) {
			cur_view = getChildAt(i);
			// Log.v("HotObj", "layout(int) view1.name ="+cur_view.name+
			// "view1.screen="+((IconBase3D)cur_view).getItemInfo().screen);
			if (cur_view != mFocus) {
				// old_posx = cur_view.x;
				// old_posy = cur_view.y;

				getPos(cur_view);
				// cur_view.setPosition(mPosx, mPosy);

				if (animation_flag) {
					animation_line.push(Tween
							.to(cur_view, View3DTweenAccessor.POS_XY, 0.5f)
							.target(mPosx, mPosy).ease(Cubic.OUT)
							.delay(i * animation_delay));
				} else {
					cur_view.setPosition(mPosx, mPosy);
				}
			}
		}
		if (animation_flag)
			animation_line.start(View3DTweenAccessor.manager).setCallback(this);
	}

	public View3D getFocusView() {
		return mFocus;
	}

	public int getFocusIndex() {
		return ((IconBase3D) mFocus).getItemInfo().screen;
	}

	public int getDragState() {
		return dragState;
	}

	public void getViewPos(View3D cur_view, Vector2 IconPoint) {
		getPos(cur_view);
		IconPoint.x = mPosx;
		IconPoint.y = mPosy;
	}

	public void getEmptyCellIndexPos(int index, Vector2 IconPoint,
			boolean bFolder) {
		if (index < 2) {
			IconPoint.x = (index % mCountX) * (mCellWidth) + mPaddingLeft;
		} else if (index == 2) {
			IconPoint.x = (int) this.width - mPaddingRight - 2 * (mCellWidth);
		} else {
			IconPoint.x = (int) this.width - mPaddingRight - (mCellWidth);
		}

		if (bFolder) {
			IconPoint.y = (int) mPaddingBottom;
		} else {
			IconPoint.y = (int) mPaddingBottom;
		}
		if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder){
			if (R3D.workspace_cell_width > mCellWidth) {
				IconPoint.x -= (R3D.workspace_cell_width - mCellWidth) / 2.0f;
			} else {
				IconPoint.x += (mCellWidth - R3D.workspace_cell_width) / 2.0f;
			}
		}
	}

	public void getIndexPos(int index, Vector2 IconPoint) {
		View3D view = findExistView(index);
		if (view == null) {
			IconPoint.x = 0;
			IconPoint.y = 0;
		} else {
			getPos(view);
			IconPoint.x = mPosx;
			IconPoint.y = mPosy;
		}
	}

	private void getPos(View3D cur_view) {
		if (cur_view == null) {
			mPosx = mPosy = 0;
			return;
		}
		ItemInfo info = ((IconBase3D) cur_view).getItemInfo();
		int index = info.screen;
		// getPos(index);

		// 返回值存在mPosx和mPosy
		if (index < 2) {
			mPosx = (index % mCountX) * (mCellWidth) + mPaddingLeft;
		} else if (index == 2) {
			mPosx = (int) this.width - mPaddingRight - 2 * (mCellWidth);
		} else {
			mPosx = (int) this.width - mPaddingRight - (mCellWidth);
		}
		if (cur_view instanceof FolderIcon3D) {
			mPosy = mPaddingBottom;
		} else {
			mPosy = (int) mPaddingBottom;
		}

		if (cur_view.width > mCellWidth) {
			mPosx -= (cur_view.width - mCellWidth) / 2;
		} else {
			mPosx += (mCellWidth - cur_view.width) / 2;
		}

		// if (children.size()>0)
		// {
		// View3D view0 = children.get(0);
		// if (view0 != null) {
		// mPosx += (view0.getWidth() - cur_view.getWidth()) / 2;
		// }
		// }

	}

	/* index 输入参数表示在网格中的位置，不是在children中的index */
	private void getPos(int index) {
		// 返回值存在mPosx和mPosy
		if (index < 2) {
			mPosx = (index % mCountX) * (mCellWidth) + mPaddingLeft;
		} else if (index == 2) {
			mPosx = (int) this.width - mPaddingRight - 2 * (mCellWidth);
		} else {
			mPosx = (int) this.width - mPaddingRight - (mCellWidth);
		}
		if (mFocus instanceof FolderIcon3D) {
			mPosy = mPaddingBottom;
		} else {
			mPosy = (int) mPaddingBottom;
		}
		if (mFocus.width > mCellWidth) {
			mPosx -= (mFocus.width - mCellWidth) / 2;
		} else {
			mPosx += (mCellWidth - mFocus.width) / 2;
		}

	}

	public int getIndex(int x, int y) {
		// Log.v("HotObj","getIndex_out:" + name +" x:" + x + " y:"+ y);

		int retIndex = 0;
		if (x < mPaddingLeft && y < this.height) {
			retIndex = 0;
		} else if (x > this.width - mPaddingRight && y < this.height) {
			retIndex = 3;
		} else if (y > this.height) {
			retIndex = -1;
		} else {
			retIndex = (x - mPaddingLeft) / mCellWidth;
			if (retIndex == 2) {
				retIndex = -2;
			} else if (retIndex > 1) {
				retIndex = retIndex - 1;
			}

		}
		// Log.v("HotObj","getIndex_out name:" + name +" retIndex=" + retIndex
		// );
		return retIndex;
	}

	private void centerFocus(int x, int y) {
		// mFocus.setPosition(x - mFocus.width/2, y - mFocus.height/2);
		mFocus.setPosition(x - mFocus.width / 2, y - mFocus.height / 8);
	}

	@Override
	public boolean onClick(float x, float y) {
		// TODO Auto-generated method stub

		int retIndex = getIndex((int) x, (int) y);
		// updateCellState(x,y);
		if (retIndex == -2) {
			// Log.v("HotObj","HotGridView3D retIndex==-2" );
			this.viewParent
					.onCtrlEvent(this, HotGridView3D.MSG_VIEW_START_MAIN);
			return true;
		} else {
			return super.onClick(x, y);
		}
	}

	public View3D hit(float x, float y) {
		int len = children.size() - 1;
		for (int i = len; i >= 0; i--) {
			View3D child = children.get(i);
			if (!child.visible)
				continue;
			// toChildCoordinates(child, x, y, point);
			if (child.pointerInParent(x, y)) {
				return child;
			}

		}
		return null;
	}

	@Override
	public boolean onLongClick(float x, float y) {
		if (auto_focus) {

			animation_flag = false;
			mFocus = null;

			mFocus = (View3D) hit(x, y);
			if (mFocus == this)
				mFocus = null;
			if (mFocus != null) {
				SendMsgToAndroid.vibrator(R3D.vibrator_duration);
				stopAnimation();
				// centerFocus((int)x,(int)y);
				requestFocus();
				/*
				 * 长按动画 xp_20120425
				 */
				float tempScalex = mFocus.scaleX;
				float tempScaley = mFocus.scaleY;
				mFocus.setScale(tempScalex * 0.6f, tempScaley * 0.6f);
				mFocus.startTween(View3DTweenAccessor.SCALE_XY, Elastic.OUT,
						0.8f, tempScalex, tempScaley, 0f);
				
				/************************ added by zhenNan.ye begin ***************************/
				if (DefaultLayout.enable_particle)
				{
					if (ParticleManager.particleManagerEnable)
					{
						float positionX, positionY;
						Vector2 point = new Vector2();
						mFocus.toAbsoluteCoords(point);
						positionX = point.x+mFocus.width/2;
						positionY = point.y+mFocus.height/2;
						
						mFocus.startParticle(ParticleManager.PARTICLE_TYPE_NAME_START_DRAG,
								positionX, positionY);
					}
				}
				/************************ added by zhenNan.ye end ***************************/
				
				return true;
			}
		}

		return super.onLongClick(x, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iLoong.launcher.UI3DEngine.ViewGroup3D#scroll(float, float,
	 * float, float)
	 */
	@Override
	public boolean scroll(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		if (mFocus != null)
			return true;
		return super.scroll(x, y, deltaX, deltaY);
	}

	static final int MAX_X_REGION = 70;
	static final int MAX_AUTO_SCROLL_STEP = 20;

	static final int MOVE_LEFT = -1;
	static final int MOVE_RIGHT = 1;
	static final int MOVE_STOP = 0;

	private float VOLOCITY_MAX = 6.0f;
	public boolean isAutoMove = false;

	private void startMove(int dire) {
		Log.v("test", "startMove  = " + dire);
		if (dire != MOVE_STOP) {
			setUser(VOLOCITY_MAX * -dire);
			isAutoMove = true;
		} else {
			isAutoMove = false;
			setUser(0);
		}
	}

	public void autoMoveUpdate() {
		layout(0);
	}

	public boolean isScrollToEnd(int dire) { // first or last in screen
		boolean res = false;

		if (getChildCount() == 0)
			return res;

		if (dire == MOVE_STOP)
			return res;

		int temp = 0;

		if (dire > 0)
			temp = MOVE_RIGHT;
		else
			temp = MOVE_LEFT;

		float mFocusViewX = this.x;
		float mFocusViewWidth = getEffectiveWidth();

		// Log.v("test", "mFocusViewWidth = " + mFocusViewWidth +
		// "mFocusViewX = " + mFocusViewX);

		switch (temp) {
		case MOVE_LEFT:
			if (-mFocusViewX > 30)
				res = true;
			break;
		case MOVE_RIGHT:
			if (-mFocusViewX + Utils3D.getScreenWidth() < mFocusViewWidth - 20)
				res = true;
			break;
		default:
			break;
		}

		if (!res)
			isAutoMove = false;/* 如果不能移动�?刚把移动的也关闭 */

		return res;
	}

	// @Override
	// public void draw(SpriteBatch batch, float parentAlpha) {
	// // TODO Auto-generated method stub
	// super.draw(batch, parentAlpha);
	//
	// if (isAutoMove) {
	// if (!isScrollToEnd(autoMoveDire)) {// && autoMoveTween.isFinished()
	// if (autoMoveTween != null) {
	// this.stopTween();
	// autoMoveTween = null;
	// isAutoMove = false;
	// }
	// } else {
	// // this.x += getUser();
	// }
	// }
	// }

	public View3D findExistView(int index) {
		int Count = getChildCount();
		for (int i = 0; i < Count; i++) {
			View3D view1 = getChildAt(i);
			// Log.v("HotObj", "findExistView view1.name ="+view1.name+
			// " view1.screen="+((IconBase3D)view1).getItemInfo().screen);
			if (((IconBase3D) view1).getItemInfo().screen == index) {
				return view1;
			}
		}
		return null;
	}

	public void InsertHotView(View3D view, int index) {
		int Count = getChildCount();
		int screenIndex = 0;
		ItemInfo itemInfo;
		if (Count >= mCountX - 1) {
			/* No Empty 无法添加 */
			return;
		}
		View3D FindExistView = findExistView(index);
		if (FindExistView != null) {
			for (int i = 0; i < Count; i++) {
				View3D view1 = getChildAt(i);
				itemInfo = ((IconBase3D) view1).getItemInfo();
				screenIndex = itemInfo.screen;
				// Log.v("HotObj", " InsertHotView view1.name =" + view1.name
				// + " view1.screen="
				// + ((IconBase3D) view1).getItemInfo().screen);
				if (screenIndex >= index && screenIndex < (mCountX - 2)) {
					itemInfo.screen = screenIndex + 1;
					Root3D.addOrMoveDB(itemInfo,
							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
			}
		}
		addViewAt(index, view);

	}

	private void changeItemCellIndex(View3D findView) {
		ItemInfo Focus_ItemInfo = ((IconBase3D) mFocus).getItemInfo();
		ItemInfo findView_ItemInfo = ((IconBase3D) findView).getItemInfo();

		int findView_screen = findView_ItemInfo.screen;
		int focusPosx = 0;
		int focusPosy = 0;
		int focus_screen = Focus_ItemInfo.screen;

		getPos(mFocus);
		focusPosx = mPosx;
		focusPosy = mPosy;

		findView.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT, 0.5f, mPosx,
				mPosy, 0);

		getPos(findView);

		mFocus.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT, 0.5f, mPosx,
				mPosy, 0);

		findView_ItemInfo.screen = focus_screen;
		findView_ItemInfo.x = focusPosx;
		findView_ItemInfo.y = focusPosy;
		Root3D.addOrMoveDB(findView_ItemInfo,
				LauncherSettings.Favorites.CONTAINER_HOTSEAT);

		Focus_ItemInfo.screen = findView_screen;
		Focus_ItemInfo.x = mPosx;
		Focus_ItemInfo.y = mPosy;
		Root3D.addOrMoveDB(Focus_ItemInfo,
				LauncherSettings.Favorites.CONTAINER_HOTSEAT);

	}

	private void dealTouchDragged(float x, float y) {
		// int FocusIndex=((IconBase3D)mFocus).getItemInfo().screen;
		int index = getIndex((int) x, (int) y);
		ItemInfo Focus_ItemInfo = ((IconBase3D) mFocus).getItemInfo();
		View3D findView = findExistView(index);
		dragState = State_ChangePosition;
		viewParent.onCtrlEvent(this, MSG_UPDATE_OBJ3D_INDEX_SHOW_STATUS);
		if (index == Focus_ItemInfo.screen) {
			viewParent.onCtrlEvent(this, MSG_VIEW_MOVED);
			return;
		}

		if (findView == null) {
			/* Do nothing ,等到TouchUP去处理 */
			Focus_ItemInfo.screen = index;
			getPos(index);
			Focus_ItemInfo.x = mPosx;
			Focus_ItemInfo.y = mPosy;
			Root3D.addOrMoveDB(Focus_ItemInfo,
					LauncherSettings.Favorites.CONTAINER_HOTSEAT);

		} else {
			if (mFocus instanceof Icon3D) {
				// changeItemCellIndex(findView);
				/* 判断移动方向 */
				if (Focus_ItemInfo.screen < index) {
					getPos(index);
					if (x > mPosx + mCellWidth * 3 / 4) {
						changeItemCellIndex(findView);
					} else {
						// Log.v("packtest",
						// "Focus_ItemInfo.screen<index createFolder");
						this.setTag(findView);
						dragState = State_CreateFolder;

					}
				} else {
					getPos(index);
					if (x < mPosx + mCellWidth / 4) {
						changeItemCellIndex(findView);
					} else {
						this.setTag(findView);
						// Log.v("packtest",
						// "Focus_ItemInfo.screen>index createFolder");
						dragState = State_CreateFolder;
					}
				}
				viewParent.onCtrlEvent(this, MSG_VIEW_MOVED);

			} else {
				changeItemCellIndex(findView);
			}
		}

	}

	@Override
	public boolean onTouchDragged(float x, float y, int pointer) {
		if (mFocus != null) {
			boolean handled = false;
			int index = getIndex((int) x, (int) y);
			// Log.v("HotObj", "TouchDragged index="+index);
			if (index == -1) {
				/************************ added by zhenNan.ye begin ***************************/
				if (DefaultLayout.enable_particle)
				{
					if (ParticleManager.particleManagerEnable)
					{
						mFocus.stopParticle(ParticleManager.PARTICLE_TYPE_NAME_DRAG);
					}
				}
				/************************ added by zhenNan.ye end ***************************/
				
				point.x = x;
				point.y = y;
				this.toAbsolute(point);
				DragLayer3D.dragStartX = point.x;
				DragLayer3D.dragStartY = point.y;
				dragState = State_ChangePosition;
				viewParent.onCtrlEvent(this, MSG_VIEW_MOVED);
				handled = viewParent.onCtrlEvent(this, MSG_VIEW_OUTREGION_DRAG);
				if (handled) {
					releaseFocus();
					mFocus = null;
				}

			} else if (index == -2) {
				/* Do Nothing */
			} else {
				dealTouchDragged(x, y);
				
				/************************ added by zhenNan.ye begin ***************************/
				if (DefaultLayout.enable_particle)
				{
					if (ParticleManager.particleManagerEnable)
					{
						if (Math.abs(x - last_x) > 10 || Math.abs(y - last_y) > 10)
						{
							float positionX, positionY;
							float iconHeight = Utils3D.getIconBmpHeight();
							Vector2 point = new Vector2();
							mFocus.toAbsoluteCoords(point);
							positionX = point.x+mFocus.width/2;
							positionY = point.y+(mFocus.height-iconHeight)+iconHeight/2;
							mFocus.updateParticle(ParticleManager.PARTICLE_TYPE_NAME_DRAG, 
									positionX, positionY);
							
							last_x = x;
							last_y = y;
						}
						else
						{
							mFocus.pauseParticle(ParticleManager.PARTICLE_TYPE_NAME_DRAG);
						}
					}
				}
				/************************ added by zhenNan.ye end ***************************/
			}

			if (!handled)
				centerFocus((int) x, (int) y);
			return true;
		}
		return super.onTouchDragged(x, y, pointer);

	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		/************************ added by zhenNan.ye begin ***************************/
		if (DefaultLayout.enable_particle)
		{
			if (ParticleManager.particleManagerEnable)
			{
				last_x = x;
				last_y = y;
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		
		return super.onTouchDown(x, y, pointer);
	}

	@Override
	public boolean onTouchUp(float x, float y, int pointer) {
		if (isAutoMove) {
			startMove(MOVE_STOP);
		}
		if (mFocus != null) {
			/************************ added by zhenNan.ye begin ***************************/
			if (DefaultLayout.enable_particle)
			{
				if (ParticleManager.particleManagerEnable)
				{
					mFocus.stopParticle(ParticleManager.PARTICLE_TYPE_NAME_DRAG);
				}
			}
			/************************ added by zhenNan.ye end ***************************/
			
			boolean handled = false;
			int index = getIndex((int) x, (int) y);
			// Log.v("packtest",
			// "onTouchUp mShortcutView.getDragState()="+getDragState()+" index="+index);
			if (index == -1) {
				handled = viewParent.onCtrlEvent(this, MSG_VIEW_OUTREGION);
			} else if (index == -2) {
				getPos(mFocus);
				animation_focus = Tween
						.to(mFocus, View3DTweenAccessor.POS_XY, 0.5f)
						.target(mPosx, mPosy).ease(Cubic.OUT);
				animation_focus.start(View3DTweenAccessor.manager);
				dragState = State_ChangePosition;
				viewParent.onCtrlEvent(this, MSG_VIEW_MOVED);
				handled = true;
			}
			// Log.v("packtest", "onTouchUp mShortcutView handled="+handled);
			if (handled == false) {
				View3D findView = findExistView(index);
				if (dragState == State_CreateFolder) {
					this.setTag(findView);
					if (findView instanceof FolderIcon3D) {
						viewParent.onCtrlEvent(this, MSG_VIEW_MERGE_FOLDER);
					} else {
						viewParent.onCtrlEvent(this, MSG_VIEW_CREATE_FOLDER);
					}
				} else {
					getPos(index);
					animation_focus = Tween
							.to(mFocus, View3DTweenAccessor.POS_XY, 0.5f)
							.target(mPosx, mPosy).ease(Cubic.OUT);
					animation_focus.start(View3DTweenAccessor.manager);
				}
			}
			mFocus = null;
			releaseFocus();
			this.viewParent.onCtrlEvent(this, MSG_UPDATE_OBJ3D_SHOW_STATUS);
			return true;
		}
		return super.onTouchUp(x, y, pointer);
	}

	@Override
	public void onEvent(int type, BaseTween source) {
		// TODO Auto-generated method stub
		if (type == TweenCallback.COMPLETE && source == animation_line) {
			animation_line = null;
			animation_delay = 0;
		}
		if (type == TweenCallback.COMPLETE && source == animation_focus) {
			animation_focus = null;

		}
		super.onEvent(type, source);
	}

	public float getEffectiveWidth() {

		if (getChildCount() <= mCountX)
			return getChildCount() * mCellWidth + mPaddingLeft + mPaddingRight;
		return mCountX * mCellWidth + mPaddingLeft + mPaddingRight;
	}

	public float getEffectiveHeight() {
		return (getChildCount() / mCountX + 1) * mCellHeight + mPaddingLeft
				+ mPaddingRight;
	}

	@Override
	public void removeView(View3D actor) {
		// TODO Auto-generated method stub
		super.removeView(actor);
		this.viewParent.onCtrlEvent(this, MSG_UPDATE_OBJ3D_SHOW_STATUS);
		// layout(0);
	}

	@Override
	public boolean pointerInParent(float x, float y) {
		// TODO Auto-generated method stub
		if (Gdx.graphics.getDensity() > 1) {
			Group.toChildCoordinates(this, x, y, point);
			float offsetY = 20 * Gdx.graphics.getDensity();
			return ((point.x >= 0 && point.x < width) && (point.y >= 0 && point.y < (height + offsetY)));
		} else {
			return super.pointerInAbs(x, y);
		}
	}
}