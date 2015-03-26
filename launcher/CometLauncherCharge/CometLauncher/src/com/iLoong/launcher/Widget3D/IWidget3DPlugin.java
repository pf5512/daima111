package com.iLoong.launcher.Widget3D;


import com.iLoong.launcher.UI3DEngine.View3D;


public interface IWidget3DPlugin
{
	
	/**
	 * 
	 * @param context
	 *            通过createPackageContext创建的widget自身的运行环境
	 * @param widgetId
	 * @return
	 */
	public View3D getWidget(
			MainAppContext mainAppContext ,
			int widgetId );
	
	/**
	 * 对Widget进行初始化操作
	 * 
	 * @param context
	 *            通过createPackageContext创建的widget自身的运行环境
	 */
	public void preInitialize(
			MainAppContext mainAppContext );
	
	public WidgetPluginViewMetaData getWidgetPluginMetaData(
			MainAppContext mainAppContext ,
			int widgetId );
}
