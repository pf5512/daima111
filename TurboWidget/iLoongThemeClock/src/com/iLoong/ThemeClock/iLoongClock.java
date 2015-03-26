package com.iLoong.ThemeClock;

import android.content.Context;

import com.iLoong.ThemeClock.View.WidgetClock;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.Widget3D.IWidget3DPlugin;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;

public class iLoongClock implements IWidget3DPlugin {
	CooGdx cooGdx = null;

	@Override
	public View3D getWidget(MainAppContext context, int widgetId) {
		// TODO Auto-generated method stub
		return new WidgetClock("widgetClock", context, widgetId);
	}

	/**
	 * Launcher加载时预加载Clock的一些资源，加快Clock拖出的速度
	 */
	@Override
	public void preInitialize(MainAppContext context) {
		
	}

	@Override
	public WidgetPluginViewMetaData getWidgetPluginMetaData(
			MainAppContext mainAppContext, int widgetId) {
		Context mContext = mainAppContext.mWidgetContext;
		WidgetPluginViewMetaData metaData = new WidgetPluginViewMetaData();
		metaData.spanX = Integer.valueOf(mContext.getResources().getInteger(
				R.integer.spanX));
		metaData.spanY = Integer.valueOf(mContext.getResources().getInteger(
				R.integer.spanY));
		metaData.maxInstanceCount = mContext.getResources().getInteger(
				R.integer.max_instance);
		metaData.maxInstanceAlert = mContext.getResources().getString(
				R.string.max_instance_alert);
		metaData.iconResourceId = R.drawable.widget_ico;
		metaData.width = mContext.getResources().getInteger(R.integer.width);
		metaData.height = mContext.getResources().getInteger(R.integer.height);
		return metaData;
	}

}
