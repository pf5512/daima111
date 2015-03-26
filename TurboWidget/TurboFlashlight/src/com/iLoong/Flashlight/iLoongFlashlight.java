package com.iLoong.Flashlight;


import java.util.Locale;

import com.iLoong.Flashlight.view.WidgetFlashlight;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.IWidget3DPlugin;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;


public class iLoongFlashlight implements IWidget3DPlugin
{
	
	@Override
	public View3D getWidget(
			MainAppContext mainAppContext ,
			int widgetId )
	{
		return new WidgetFlashlight( "widgetFlashlight" , mainAppContext , widgetId );
	}
	
	@Override
	public void preInitialize(
			MainAppContext mainAppContext )
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public WidgetPluginViewMetaData getWidgetPluginMetaData(
			MainAppContext mainAppContext ,
			int widgetId )
	{
		//		Context mContext = mainAppContext.mWidgetContext;
		WidgetPluginViewMetaData metaData = new WidgetPluginViewMetaData();
		metaData.spanX = 1;//Integer.valueOf( mContext.getResources().getInteger( 4 ) );
		metaData.spanY = 1;//Integer.valueOf( mContext.getResources().getInteger( 4 ) );
		String lan = Locale.getDefault().getLanguage();
		if( lan.equals( "zh" ) )
		{
			metaData.maxInstanceAlert = "已存在，不可重新添加";
		}
		else
		{
			metaData.maxInstanceAlert = "Already exists, can not add another one";
		}
		return metaData;
	}
}
