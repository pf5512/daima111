package com.iLoong.MusicPlay;

import java.util.Locale;

import android.content.Context;

import com.cooeeui.cometmusicplay.R;
import com.iLoong.MusicPlay.view.WidgetMusicPlay;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.IWidget3DPlugin;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;

public class iLoongMusicPlay implements IWidget3DPlugin{

	@Override
	public View3D getWidget(MainAppContext context, int widgetId) {
		// TODO Auto-generated method stub
		return new WidgetMusicPlay("widgetMusicPlay",context,widgetId);
	}
	@Override
	public void preInitialize(MainAppContext arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public WidgetPluginViewMetaData getWidgetPluginMetaData(
			MainAppContext mainAppContext, int widgetId) {
		Context mContext = mainAppContext.mWidgetContext;
		WidgetPluginViewMetaData metaData = new WidgetPluginViewMetaData();
		metaData.spanX = Integer.valueOf(mContext.getResources().getInteger(
				4));
		metaData.spanY = Integer.valueOf(mContext.getResources().getInteger(
				2));
		metaData.maxInstanceCount = mContext.getResources().getInteger( R.integer.max_instance );
		String lan = Locale.getDefault().getLanguage();
		if( lan.equals( "zh" ) )
		{
			metaData.maxInstanceAlert = "已存在，不可重新添加"; 
		}
		else
		{
			metaData.maxInstanceAlert = "Already exists, can not add another one";
		}
		metaData.iconResourceId = R.drawable.widget_ico;
		return metaData;
	}
}
