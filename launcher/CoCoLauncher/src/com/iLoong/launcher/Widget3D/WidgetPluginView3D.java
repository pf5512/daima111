package com.iLoong.launcher.Widget3D;

import android.view.View;

import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.adapter.ICooPluginHostCallback;
import com.iLoong.launcher.UI3DEngine.adapter.IRefreshRender;
import com.iLoong.launcher.desktop.iLoongLauncher;

public abstract class WidgetPluginView3D extends ViewGroup3D {
	protected IRefreshRender refreshRender = null;
	protected ICooPluginHostCallback cooPluginHostCallback = null;

	public WidgetPluginViewMetaData getPluginViewMetaData() {
		return null;
	}

	public void setCooPluginHostCallback(
			ICooPluginHostCallback cooPluginHostCallback) {
		this.cooPluginHostCallback = cooPluginHostCallback;
	}

	public IRefreshRender getRefreshRender() {
		return refreshRender;
	}

	public void setRefreshRender(IRefreshRender refreshRender) {
		this.refreshRender = refreshRender;
	}

	public WidgetPluginView3D(String name) {
		super(name);
		transform = true;
		// TODO Auto-generated constructor stub
	}

	public boolean isOpened() {
		return false;
	}

	public abstract void onDelete();

	public void onStart() {

	}

	public void onResume() {

	}

	public void onPause() {

	}

	public void onStop() {

	}

	public void onDestroy() {

	}

	public void onUninstall() {

	}

	public void onKeyEvent(int keycode, int keyEventCode) {

	}

	public View getParticalView() {
		// TODO Auto-generated method stub
		return null;
	}

	// xiatian add start //Widget3D adaptation "Naked eye 3D"
	public void onSensorAngleChange(float xAngle, float yAngle, boolean isInit) {
		if (DefaultLayout.show_sensor == false) {
			return;
		}
	}

	public boolean onIsOpenSensor() {
		if (DefaultLayout.show_sensor) {
			return iLoongLauncher.getInstance().isOpenSensor();
		} else {
			return false;
		}
	}

	public boolean onIsShowSensor() {
		if (DefaultLayout.show_sensor) {
			return true;
		} else {
			return false;
		}
	}
	// xiatian add end

}
