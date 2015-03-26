package com.iLoong.launcher.Widget3D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.IconBase3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3DHost.Widget3DProvider;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.Widget3DInfo;

public class Widget3D extends ViewGroup3D implements IconBase3D {

	// WidgetID，每个Widget的标志
	private int widgetId = INVALID_WIDGETID;

	private String packageName;

	private WidgetPluginView3D pluginInstance;

	// Widget长按消息标志
	public static final int MSG_Widget3D_LONGCLICK = 0;

	public static final int INVALID_WIDGETID = -1;

	public static final String INTENT_UPDATE_WIDGET3D = "com.iLoong.updateWidget3D";

	public Widget3DInfo itemInfo;
	
	private static Camera mCamera;  
	private static Vector3 oldCameraPosition = new Vector3();
	private static Vector3 newCameraPosition = new Vector3();
	private static Vector2 widgetPoistion = new Vector2();
	private static float posOffsetX = 0;
	private static float posOffsetY = 0;
	

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(int widgetID) {
		widgetId = widgetID;
	}

	// 注释此代码，否则影响Memo弹起时被ICON图标覆盖问题
	// @Override
	// public void draw(SpriteBatch batch, float parentAlpha) {
	// // TODO Auto-generated method stub
	// // batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
	// Gdx.gl.glDepthMask(true);
	// Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	// Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓存
	// Gdx.gl.glDepthFunc(GL10.GL_LEQUAL);
	// super.draw(batch, parentAlpha);
	// Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
	// Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓存
	// Gdx.gl.glDepthMask(false);
	// }

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		return super.onTouchDown(x, y, pointer);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		applyCameraPosition(batch);
		super.draw(batch, parentAlpha);
		resetCameraPosition(batch);
	}

	public Widget3D(String name, WidgetPluginView3D widget) {
		super(name);
		this.pluginInstance = widget;
		addView(widget);
		// 这一步必须要做，否则生成的View宽度和高度比较大，点击焦点位置会失灵
		this.width = widget.width;
		this.height = widget.height;
		this.setOrigin(this.width / 2, this.height / 2);
		transform = true;
	}

	@Override
	public View3D clone() {
		Widget3D widget = new Widget3D(name, this.pluginInstance);
		widget.widgetId = this.widgetId;
		widget.packageName = this.packageName;
		return widget;
	}

	@Override
	public boolean onLongClick(float x, float y) {

		if (!this.isDragging) {
			this.toAbsoluteCoords(point);
			this.setTag(new Vector2(point.x, point.y));
			point.x = x;
			point.y = y;
			this.toAbsolute(point);
			DragLayer3D.dragStartX = point.x;
			DragLayer3D.dragStartY = point.y;
			Log.v("launcher", "onLongClick:" + name + " x:" + point.x + " y:"
					+ point.y);
			return viewParent.onCtrlEvent(this, MSG_Widget3D_LONGCLICK);
		}
		return false;
	}

	@Override
	public boolean onClick(float x, float y) {
		Log.v("Widget3D", "onClick" + name + " x:" + x + " y:" + y);
		return super.onClick(x, y);
	}

	public Widget3DInfo getItemInfo() {
		if (itemInfo == null) {
			itemInfo = new Widget3DInfo();
			itemInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_WIDGET3D;
			itemInfo.packageName = this.packageName;
			itemInfo.widgetId = this.widgetId;
		}
		Widget3DProvider provider = Widget3DManager.getInstance()
				.getWidget3DProvider(itemInfo.packageName);
		if (provider != null) {
			itemInfo.spanX = provider.spanX;
			itemInfo.spanY = provider.spanY;
		}
		// itemInfo.screen = screen;
		itemInfo.x = (int) this.x;
		itemInfo.y = (int) this.y;
		return itemInfo;
	}

	@Override
	public void setItemInfo(ItemInfo info) {
		if (info instanceof Widget3DInfo) {
			this.itemInfo = (Widget3DInfo) info;
		}
		// TODO Auto-generated method stub
		// if (itemInfo == null) {
		// itemInfo = new Widget3DInfo();
		// // itemInfo.itemType =
		// // LauncherSettings.Favorites.ITEM_TYPE_WIDGET3D;
		// itemInfo.packageName = this.packageName;
		// itemInfo.widgetId = this.widgetId;
		// }
		// itemInfo.x = info.x;
		// itemInfo.y = info.y;
		// itemInfo.angle = info.angle;
		// itemInfo.cellTempX = info.cellTempX;
		// itemInfo.cellTempY = info.cellTempY;
		// itemInfo.cellX = info.cellX;
		// itemInfo.cellY = info.cellY;
		// itemInfo.screen = info.screen;
		// itemInfo.spanX = info.spanX;
		// itemInfo.spanY = info.spanY;
	}

	@Override
	public boolean onCtrlEvent(View3D sender, int event_id) {
		// TODO Auto-generated method stub
		Log.v("Widget3D", "onLongClick:" + name + " x:" + x + " y:" + y);
		if (!this.isDragging) {
			this.toAbsoluteCoords(point);
			this.setTag(new Vector2(point.x, point.y));
			if (sender.getTag() != null) {
				Vector2 vector = (Vector2) sender.getTag();
				DragLayer3D.dragStartX = vector.x;
				DragLayer3D.dragStartY = vector.y;
			}
			return viewParent.onCtrlEvent(this, MSG_Widget3D_LONGCLICK);
		}
		return false;
	}

	public boolean isOpened() {
		return this.pluginInstance.isOpened();
	}

	public void onDelete() {
		this.pluginInstance.onDelete();
	}

	public void onStart() {
		this.pluginInstance.onStart();
	}

	public void onResume() {
		this.pluginInstance.onResume();
	}

	public void onPause() {
		this.pluginInstance.onPause();
	}

	public void onStop() {
		this.pluginInstance.onStop();
	}

	public void onDestroy() {
		this.pluginInstance.onDestroy();
	}

	public void onKeyEvent(int keycode, int keyEventCode) {
		this.pluginInstance.onKeyEvent(keycode, keyEventCode);
	}

	public void dispose() {
		super.dispose();
		this.pluginInstance.dispose();
	}

	public void onUninstall() {
		this.pluginInstance.onUninstall();
	}

	// xiatian add start //Widget3D adaptation "Naked eye 3D"
	public void onSensorAngleChange(float xAngle, float yAngle, boolean isInit) {
		if (DefaultLayout.show_sensor) {
			this.pluginInstance.onSensorAngleChange(xAngle, yAngle, isInit);
		}
	}

	public WidgetPluginViewMetaData getPluginViewMetaData() {
		return pluginInstance.getPluginViewMetaData();
	}
	// xiatian add end
	
	private void applyCameraPosition(SpriteBatch batch) {
		mCamera = Desktop3DListener.d3d.getCamera();
		oldCameraPosition.set(mCamera.position);
		toAbsoluteCoords(widgetPoistion);
		newCameraPosition.x = widgetPoistion.x + this.width / 2;
		newCameraPosition.y = widgetPoistion.y + this.height / 2;
		newCameraPosition.z = oldCameraPosition.z;
		posOffsetX = newCameraPosition.x - oldCameraPosition.x;
		posOffsetY = newCameraPosition.y - oldCameraPosition.y;
		
		mCamera.position.set(newCameraPosition);
		mCamera.update();
		batch.end();
		Gdx.gl.glViewport((int)posOffsetX, (int)posOffsetY, Utils3D.getScreenWidth(), Utils3D.getScreenHeight());
		batch.setProjectionMatrix(mCamera.combined);
		batch.begin();
	}
	
	private void resetCameraPosition(SpriteBatch batch) {
		mCamera.position.set(oldCameraPosition);
		mCamera.update();
		batch.end();
		Gdx.gl.glViewport(0, 0, Utils3D.getScreenWidth(), Utils3D.getScreenHeight());
		batch.setProjectionMatrix(mCamera.combined);
		batch.begin();
	}

}
