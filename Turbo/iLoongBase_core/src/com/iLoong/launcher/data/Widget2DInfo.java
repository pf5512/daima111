/* Copyright (C) 2009 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.iLoong.launcher.data;


import android.content.ContentValues;

import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.launcher.widget.WidgetHostView;


/**
 * Represents a widget, which just contains an identifier.
 */
public class Widget2DInfo extends ItemInfo
{
	
	/**
	 * Identifier for this widget when talking with
	 * {@link android.appwidget.AppWidgetManager} for updates.
	 */
	public int appWidgetId;
	public boolean canMove = true;
	/**
	 * View that holds this widget after it's been created.  This view isn't created
	 * until Launcher knows it's needed.
	 */
	public WidgetHostView hostView = null;
	public float minWidth;
	public float minHeight;
	private String wdgPackageName;
	private String wdgClassName;
	
	public Widget2DInfo(
			int appWidgetId )
	{
		itemType = LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET;
		this.appWidgetId = appWidgetId;
	}
	
	public void setInfo(
			String pkgName ,
			String clsName )
	{
		wdgPackageName = pkgName;
		wdgClassName = clsName;
	}
	
	public void checkMove()
	{
		//    	if(wdgPackageName.equals("com.android.browser"))canMove = false;
	}
	
	public String getPackageName()
	{
		return wdgPackageName;
	}
	
	public String getClassName()
	{
		return wdgClassName;
	}
	
	@Override
	public void onAddToDatabase(
			ContentValues values )
	{
		super.onAddToDatabase( values );
		values.put( LauncherSettings.Favorites.APPWIDGET_ID , appWidgetId );
		values.put( LauncherSettings.Favorites.CELLX , cellX );
		values.put( LauncherSettings.Favorites.CELLY , cellY );
		values.put( LauncherSettings.Favorites.SPANX , spanX );
		values.put( LauncherSettings.Favorites.SPANY , spanY );
		values.put( LauncherSettings.Favorites.SCREEN , screen );
		values.put( LauncherSettings.Favorites.ITEM_TYPE , LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET );
		values.put( LauncherSettings.Favorites.CONTAINER , LauncherSettings.Favorites.CONTAINER_DESKTOP );
		values.put( LauncherSettings.Favorites.ICON_PACKAGE , wdgPackageName );
		values.put( LauncherSettings.Favorites.ICON_RESOURCE , wdgClassName );
	}
	
	@Override
	public String toString()
	{
		return "AppWidget(id=" + Integer.toString( appWidgetId ) + ")";
	}
	
	@Override
	void unbind()
	{
		super.unbind();
		hostView = null;
	}
}
