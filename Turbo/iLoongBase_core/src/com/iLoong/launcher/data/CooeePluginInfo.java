/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iLoong.launcher.data;

import android.content.ContentValues;

import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.launcher.CooeePlugin.CooeePluginHostView;
import com.iLoong.launcher.widget.WidgetHostView;

/**
 * Represents a third-party plugin, which implements by android view.
 */
public class CooeePluginInfo extends ItemInfo {


    public int appWidgetId;
    /**
     * View that holds this widget after it's been created.  This view isn't created
     * until Launcher knows it's needed.
     */
    public CooeePluginHostView hostView = null;
    public float minWidth;
    public float minHeight;
    public boolean fullScreen = false;

    private String pluginPackageName;
    private String pluginClassName;
    public CooeePluginInfo() {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_COOEE_PLUGIN;
        //this.appWidgetId = appWidgetId;
    }
    
    public void setInfo(String pkgName,String clsName){
    	pluginPackageName = pkgName;
    	pluginClassName = clsName;
    }
    
    public String getPackageName(){
    	return pluginPackageName;
    }
    
    public String getClassName(){
    	return pluginClassName;
    }
    
    @Override
	public void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);
        values.put(LauncherSettings.Favorites.APPWIDGET_ID, appWidgetId);
        values.put(LauncherSettings.Favorites.CONTAINER, LauncherSettings.Favorites.CONTAINER_DESKTOP);
        values.put(LauncherSettings.Favorites.ICON_PACKAGE, pluginPackageName);
        values.put(LauncherSettings.Favorites.ICON_RESOURCE, pluginClassName);
        values.put(LauncherSettings.Favorites.ANGLE, fullScreen?1:0);
    }

    @Override
    public String toString() {
        return "AppWidget(id=" + Integer.toString(appWidgetId) + ")";
    }


    @Override
    void unbind() {
        super.unbind();
        hostView = null;
    }
}
