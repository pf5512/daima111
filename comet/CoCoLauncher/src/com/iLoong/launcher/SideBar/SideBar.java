package com.iLoong.launcher.SideBar;

import java.util.ArrayList;
import java.util.List;

import com.iLoong.launcher.Desktop3D.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DragSource3D;
import com.iLoong.launcher.Desktop3D.DropTarget3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.IconBase3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class SideBar extends ViewGroup3D implements DropTarget3D, DragSource3D {

	// public static final float SIDEBAR_OPCITY_WIDTH =
	// 12*SetupMenu.mScreenScale; //背景透明部分
	public static final int MSG_START_DRAG = 0;
	public static final int TYPE_ICON = 0;
	public static final int TYPE_WIDGET = 1;
	public static final int MSG_ON_DROP = 2;
	public static final int MSG_LONGCLICK_INAPPLIST = 3;
	public static final int MSG_MAINGROUP_SCROLL_DOWN = 4;
	
	public static final int STATE_HIDE = 11;
	public static final int STATE_SHOW = 12;
	public static final int STATE_NORMAL = 13;
	
	
	private final int SCROLL_UP = 13;
	private final int SCROLL_DOWN = 14;

	private IconCache iconCache;

	private SidebarMainGroup mainGroup;
	private SidebarButtonGroup buttonGroup = new SidebarButtonGroup(
			"buttongroup");

	// 记录当按下屏幕时触摸点的X坐标，为相对坐标
	private float mLastX = 0;
	// 动画实例
	private Tween hideTween, showTween;;

	View3D dragObj;

	private int type;
	private static int state = STATE_NORMAL;
	private int scrollDir = -1;
	private float transY;

	public SideBar(String name) {
		super(name);
		// this.setBackgroud(new
		// NinePatch(R3D.getTextureRegion("shell-interactive-grid-bg")));
		// mTexture_bg = R3D.getTextureRegion("menu-bg");
		// mTexture_button_widget = R3D.getTextureRegion("menu-tool-button1");
		// mTexture_button_widget_hl =
		// R3D.getTextureRegion("menu-tool-button1");
		// mTexture_button_icon = R3D.getTextureRegion("menu-user-button1");
		// mTexture_button_icon_hl = R3D.getTextureRegion("menu-user-button2");
		width = Utils3D.getScreenWidth();
		height = R3D.sidebar_height + buttonGroup.height;

		transY = R3D.sidebar_height;

		x = 0;
		y = -transY;

		buttonGroup.x = width / 2 - buttonGroup.width / 2;
		buttonGroup.y = transY - 1;
		mainGroup = new SidebarMainGroup("maingroup", (int) width,
				(int) R3D.sidebar_height);

		
		addView(buttonGroup);
		addView(mainGroup);
		
		type = TYPE_ICON;
	}

	public int getType() {
		return type;
	}
	
	public boolean isIconListShow() {
		if (state == STATE_SHOW)
			return mainGroup.isInShortcutList();
		else
			return false;
	}

	@Override
	public void setRotation(float rotate) {
		this.setPosition(x, rotate);

	}

	@Override
	public float getRotation() {
		// TODO Auto-generated method stub
		return this.y;
	}

	void startAutoEffect() {
		// Log.e("sidebar", "startAutoEffect");
		if (scrollDir == SCROLL_DOWN) {
			showTween = Tween.to(this, View3DTweenAccessor.ROTATION, 0.5f)
					.ease(Cubic.OUT).target(0)
					.start(View3DTweenAccessor.manager);
			state = STATE_SHOW;
			Log.v("sidebar", " auto scroll: down");
		} else if (scrollDir == SCROLL_UP) {
			hideTween = Tween.to(this, View3DTweenAccessor.ROTATION, 0.5f)
					.ease(Cubic.OUT).target(-transY).setCallback(this)
					.start(View3DTweenAccessor.manager);
			state = STATE_NORMAL;
			Log.v("sidebar", " auto scroll: up");
		}
		//if(ClingManager.getInstance().widgetClingFired)SendMsgToAndroid.sendRefreshClingStateMsg();
		// float half = (-R3D.sidebar_width * 0.88f) / 2;
		// if (state == STATE_HIDE && scrollDir == SCROLL_RIGHT) {
		// showTween = Tween.to(this, View3DTweenAccessor.ROTATION, 0.5f)
		// .ease(Cubic.OUT).target(0).start(View3DTweenAccessor.manager).setCallback(this);
		//
		// }
		// else if(state == STATE_HIDE && scrollDir == SCROLL_LEFT){
		// hideTween = Tween.to(this, View3DTweenAccessor.ROTATION, 0.5f)
		// .ease(Cubic.OUT).target(-transX).setCallback(this).start(View3DTweenAccessor.manager);
		// }
		// else if (state == STATE_SHOW && scrollDir == SCROLL_RIGHT) {
		// showTween = Tween.to(this, View3DTweenAccessor.ROTATION, 0.5f)
		// .ease(Cubic.OUT).target(0).start(View3DTweenAccessor.manager).setCallback(this);
		//
		// }
		// else if(state == STATE_SHOW && scrollDir == SCROLL_LEFT){
		// hideTween = Tween.to(this, View3DTweenAccessor.ROTATION, 0.5f)
		// .ease(Cubic.OUT).target(-transX).setCallback(this).start(View3DTweenAccessor.manager);
		// }
	}

	void stopAutoEffect() {
		// TODO Auto-generated method stub
		if (hideTween != null && !hideTween.isFinished()) {
			hideTween.free();
			hideTween = null;
		}
		if (showTween != null && !showTween.isFinished()) {
			showTween.free();
			showTween = null;
		}
	}

	// @Override
	// public void draw(SpriteBatch batch, float parentAlpha) {
	// batch.draw(mTexture_bg, x, y, R3D.sidebar_width, height);
	// batch.draw(mTexture_button_widget, x + mButton_x, y + mButton_1_y,
	// mTexture_button_widget.getRegionWidth(),
	// mTexture_button_widget.getRegionHeight());
	// batch.draw(mTexture_button_icon, x + mButton_x, y + mButton_2_y,
	// mTexture_button_icon.getRegionWidth(),
	// mTexture_button_widget.getRegionHeight());
	//
	// // TODO Auto-generated method stub
	// super.draw(batch, parentAlpha);
	// }
	//
	// @Override
	// public boolean scroll(float x, float y, float deltaX, float deltaY) {
	// // TODO Auto-generated method stub
	// Log.v("gesture","View3D scroll:" + name +" x:" + x + " y:"+ y + " dx:" +
	// deltaX + " dy:" + deltaY);
	//
	// float px = x + deltaX;
	// float py = y - deltaY;
	// // if(shortcutButton.pointerIn(px, py) || widgetButton.pointerIn(px,
	// py)){
	// // if(this.x + deltaX < 0 && this.x + deltaX + mainGroup.width >= 0){
	// // this.x += deltaX;
	// // }
	// // return true;
	// // }
	//
	// if(mShortcutView.pointerIn(px, py)){
	// // if(mainGroup.y < height && mainGroup.y + )
	// mShortcutView.y -= deltaY;
	// }
	//
	// return false;
	// }
	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		// Log.e("sidebar", "onTouchDown x:" + this.x + " y:" + this.y);
		point.x = x;
		point.y = y;
		toLocalCoordinates(buttonGroup, point);
		if (buttonGroup.hit(point.x, point.y) != null) {
			if (buttonGroup.onTouchDown(point.x, point.y, pointer))
				return true;
		}
		return super.onTouchDown(x, y, pointer);
	}

	// @Override
	// public boolean onTouchDragged(float x, float y, int pointer) {
	// // Log.e("sidebar", "onTouchDragged x:" + this.x + " y:" + this.y);
	// if (x < R3D.sidebar_width * 0.88f)
	// return super.onTouchDragged(x, y, pointer);
	// // return super.onTouchDragged(x, y, pointer);
	// return true;
	// }
	// @Override
	// public boolean onTouchUp(float x, float y, int pointer) {
	// // Log.e("sidebar", "onTouchUp x:" + this.x + " y:" + this.y);
	// if(!super.onTouchUp(x, y, pointer)){
	// // if(shortcutButton.pointerIn(x, y) || widgetButton.pointerIn(x, y)){
	// // startAutoEffect();
	// // if(state == STATE_SHOW) state = STATE_HIDE;
	// // else if(state == STATE_HIDE) state = STATE_SHOW;
	// // }
	// }
	// releaseFocus();
	// return false;
	// }
	public ArrayList<View3D> getDragObjects() {
		ArrayList<View3D> list = new ArrayList<View3D>();
		// list.add(dragObj);
		if (dragObj != null) {
			if (this.type == TYPE_WIDGET) {
				list.add(dragObj);
			} else if (this.type == TYPE_ICON) {
				list.add(dragObj);
			}
		}
		return list;
	}

	@Override
	public boolean onCtrlEvent(View3D sender, int event_id) {
		// TODO Auto-generated method stub
		if (sender instanceof Icon3D) {
			Icon3D icon = (Icon3D) sender;
			Vector2 vec = null;
			if(icon.getTag() instanceof Vector2){
				vec = (Vector2) icon.getTag();
				vec.y += icon.height/2;//向上偏移
			}
			switch (event_id) {

			case Icon3D.MSG_ICON_LONGCLICK:
				if (icon.getParent() instanceof Widget3DShortcut) {
					Widget3DShortcut shortcut = (Widget3DShortcut) icon
							.getParent();
					dragObj = shortcut.getWidget3D();
					icon.toAbsoluteCoords(point);
					if (dragObj != null) {
						dragObj.setPosition(point.x, point.y);
					} else {
						return true;
					}
				} else
					dragObj = icon; 
				vec.x += icon.width/2 - dragObj.width/2;
				this.setTag(vec);
				mainGroup.releaseFocus();
				return viewParent
						.onCtrlEvent(this, DragSource3D.MSG_START_DRAG);
			}
		}

		if (sender instanceof GridView3D) {
			switch (event_id) {
			case GridView3D.MSG_VIEW_OUTREGION:
				View3D focus = mainGroup.getFocusView();
				this.setTag(focus);
				return viewParent.onCtrlEvent(this, MSG_ON_DROP);

			}
		}

		if(sender instanceof SidebarMainGroup){
			switch (event_id) {
			case SideBar.MSG_LONGCLICK_INAPPLIST:
				return viewParent.onCtrlEvent(this, event_id);
			case SideBar.MSG_MAINGROUP_SCROLL_DOWN:// 判断手势方向，用来增强拖拉敏感度
				buttonGroup.requestFocus();
				return true;
			}
		}
		if (sender instanceof SidebarButtonGroup) {
			SidebarButtonGroup buttonGroup = (SidebarButtonGroup) sender;
			switch (event_id) {
			case SidebarButtonGroup.MSG_BUTTON_SCROLL:
				float deltaY = (Float) buttonGroup.getTag();
				if (deltaY > 0)
					scrollDir = SCROLL_UP;
				if (deltaY < 0)
					scrollDir = SCROLL_DOWN;
				if (this.y - deltaY < 0
						&& this.y - deltaY + mainGroup.height >= 0) {
					this.y -= deltaY;
				}
				return true;

			case SidebarButtonGroup.MSG_BUTTON_UP_SHORTCUT:

				if (state ==STATE_SHOW && type != TYPE_ICON) {
					type = TYPE_ICON;
					mainGroup.setVisible(TYPE_ICON);
					buttonGroup.reset();
					buttonGroup.press();
				} else {
					if (state == STATE_SHOW) {
						scrollDir = SCROLL_UP;
					} else {
						scrollDir = SCROLL_DOWN;
					}
					
					startAutoEffect();
				}
				return true;
			case SidebarButtonGroup.MSG_BUTTON_UP_WIDGET:
				if (state == STATE_SHOW && type != TYPE_WIDGET) {
					type = TYPE_WIDGET;
					mainGroup.setVisible(TYPE_WIDGET);
					buttonGroup.reset();
					buttonGroup.press();
				} else {
					if (state == STATE_SHOW) {
						scrollDir = SCROLL_UP;
					} else {
						scrollDir = SCROLL_DOWN;
					}
					
					startAutoEffect();
				}
				return true;
			case SidebarButtonGroup.MSG_BUTTON_SCROLL_UP:

				if (state == STATE_SHOW) {
					scrollDir = SCROLL_UP;
				} else {
					scrollDir = SCROLL_DOWN;
				}
				startAutoEffect();

				return true;
				// case SidebarButtonGroup.MSG_BUTTON_DOWN_SHORTCUT:
				// if(type != TYPE_ICON){
				// type = TYPE_ICON;
				// mainGroup.setVisible(TYPE_ICON);
				// }
				// return true;
				// case SidebarButtonGroup.MSG_BUTTON_DOWN_WIDGET:
				// if(type != TYPE_WIDGET){
				// type = TYPE_WIDGET;
				// mainGroup.setVisible(TYPE_WIDGET);
				// }
				// return true;
			}

		}

		return viewParent.onCtrlEvent(sender, event_id);
	}

	boolean isDrop = false;

	@Override
	public boolean onDrop(ArrayList<View3D> list, float x, float y) {
		// TODO Auto-generated method stub

		isDrop = true;
		int count = mainGroup.getShortcutCount() + list.size();
		if (type == TYPE_WIDGET || count > SidebarMainGroup.MAX_ICON_NUM) {
			Log.e("sidebar", "ondrop X:" + x + " Y:" + y);
			// Utils3D.showMessage("不能放在这里");
			Log.e("sidebar", "ondrop X:" + this.x + " Y:" + y);
			if (state == STATE_SHOW) {
				state = STATE_NORMAL;
				hideTween = Tween.to(this, View3DTweenAccessor.ROTATION, 0.5f)
						.ease(Cubic.OUT).target(-transY).setCallback(this)
						.start(View3DTweenAccessor.manager);
			}
			
			if(count > SidebarMainGroup.MAX_ICON_NUM){
				SendMsgToAndroid.sendOurToastMsg(R3D.getString(RR.string.max_added_thirty));
			}
			Log.e("sidebar", "ondrop X:" + this.x + " Y:" + y);
			return false;
		}
		if (type == TYPE_ICON) {
			if (list.size() > 0) {
				View3D view = list.get(0);
				if (!(view instanceof Icon3D)) {
					if (state == STATE_SHOW) {
						state = STATE_NORMAL;
						hideTween = Tween
								.to(this, View3DTweenAccessor.ROTATION, 0.5f)
								.ease(Cubic.OUT).target(-transY)
								.setCallback(this)
								.start(View3DTweenAccessor.manager);
					}
					SendMsgToAndroid.sendOurToastMsg(R3D.getString(RR.string.catnot_add_to_sidebar));
					return false;
				}

			}

			mainGroup.addItems(list);
			list.clear();
		}
		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if (state == STATE_SHOW && keycode == KeyEvent.KEYCODE_BACK)
			return true;
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		if (keycode == KeyEvent.KEYCODE_BACK) {
			if (state == STATE_SHOW) {
				scrollDir = SCROLL_UP;
				startAutoEffect();
				return true;
			}

		}
		return super.keyUp(keycode);
	}

	

	@Override
	public boolean onDropOver(ArrayList<View3D> list, float x, float y) {
		// TODO Auto-generated method stub
		return true;
	}

	public void show() {
		mainGroup.dragAble = true;
		if (this.state == STATE_SHOW)
			return;
		this.state = STATE_NORMAL;
		stopTween();
		super.show();
		this.startTween(View3DTweenAccessor.POS_XY, Cubic.INOUT, 0.5f, x,
				-(this.height - buttonGroup.height), 0);
	}

	public void hide() {
		this.touchable = false;
		this.state = STATE_HIDE;
		stopTween();
		this.startTween(View3DTweenAccessor.POS_XY, Cubic.INOUT, 0.5f, x,
				-this.height, 0);
	}

	public void disableDrag() {
		mainGroup.dragAble = false;
	}

	@Override
	public void onEvent(int type, BaseTween source) {
		// TODO Auto-generated method stub
		if (source == hideTween && type == TweenCallback.COMPLETE) {
			// buttonGroup.fold();
			// buttonGroup.reset(null);
		} else if (source == showTween && type == TweenCallback.COMPLETE) {
		}

	}

	public void bindItems(ArrayList<ItemInfo> items) {
		sortItemsByIndex(items);
		GridView3D view = mainGroup.getShortcutGridview();
		view.enableAnimation(false);
		for (ItemInfo item : items) {
			ShortcutInfo info = (ShortcutInfo) item;
			Icon3D icon = new Icon3D(info.title.toString(),
					info.getIcon(iconCache), info.title.toString());
			icon.setItemInfo(info);
			mainGroup.bindItem(icon);
		}
		view.enableAnimation(true);
	}

	private void sortItemsByIndex(ArrayList<ItemInfo> items) {
		int i, j;
		int index1 = 0, index2 = 0;
		ItemInfo tempInfo1 = null, tempInfo2 = null;

		int count = items.size();
		for (j = 0; j <= count - 1; j++) {
			for (i = j; i < count; i++) {
				tempInfo1 = items.get(j);
				tempInfo2 = items.get(i);
				index1 = tempInfo1.screen;
				index2 = tempInfo2.screen;
				if (index1 > index2) {
					items.set(j, tempInfo2);
					items.set(i, tempInfo1);
				}
			}

		}
	}

	public void setIconCache(IconCache iconCache) {
		this.iconCache = iconCache;
	}

	public static int getState() {
		return state;
	}

	public ViewGroup3D getMainGroup() {
		return this.mainGroup;
	}

	@Override
	public void onDropCompleted(View3D target, boolean success) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<View3D> getDragList() {
		// TODO Auto-generated method stub
		ArrayList<View3D> l = new ArrayList<View3D>();
		l.add(dragObj);
		return l;
	}
	
}
