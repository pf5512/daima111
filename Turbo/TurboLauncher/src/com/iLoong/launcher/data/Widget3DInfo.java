package com.iLoong.launcher.data;


import android.content.ContentValues;

import com.cooee.android.launcher.framework.LauncherSettings;


public class Widget3DInfo extends ShortcutInfo
{
	
	public int widgetId;
	public String packageName;
	
	@Override
	public String toString()
	{
		return "WidgetId=" + Integer.toString( widgetId ) + " packageName=" + packageName + " screenId=" + String.valueOf( screen );
	}
	
	@Override
	public void onAddToDatabase(
			ContentValues values )
	{
		// TODO Auto-generated method stub
		super.onAddToDatabase( values );
		values.put( LauncherSettings.Favorites.ICON_PACKAGE , packageName );
		values.put( LauncherSettings.Favorites.APPWIDGET_ID , widgetId );
	}
	
	@Override
	void unbind()
	{
		// TODO Auto-generated method stub
		super.unbind();
	}
}
