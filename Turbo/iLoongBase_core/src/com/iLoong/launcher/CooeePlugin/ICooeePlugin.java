package com.iLoong.launcher.CooeePlugin;


import android.content.Context;
import android.view.View;


public interface ICooeePlugin
{
	
	void setContext(
			Context hostContext ,
			Context pluginContext ,
			boolean flag );
	
	int getPermittedCount();
	
	View getPluginView();
	
	void releasePluginView();
	
	void onPiflowIn();
	
	void onPiflowOut();
}
