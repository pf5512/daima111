package com.iLoong.Music;

import android.content.Context;

import com.iLoong.Music.View.WidgetMusic;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.IWidget3DPlugin;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;

public class iLoongMusic implements IWidget3DPlugin {
	@Override
	public View3D getWidget(MainAppContext context, int widgetId) {
		// TODO Auto-generated method stub
		return new WidgetMusic("widgetClock", context, widgetId);
	}

	@Override
	public WidgetPluginViewMetaData getWidgetPluginMetaData(
			MainAppContext mainAppContext, int widgetId) {
		// TODO Auto-generated method stub
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
		return metaData;
	}

	@Override
	public void preInitialize(MainAppContext mainAppContext) {
		// TODO Auto-generated method stub

	}

}
