/* Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.cooee.android.launcher.framework;


import android.net.Uri;
import android.provider.BaseColumns;

import com.iLoong.launcher.UI3DEngine.ConfigBase;


/**
 * Settings related utilities.
 */
public class LauncherSettings
{
	
	public static interface BaseLauncherColumns extends BaseColumns
	{
		
		/**
		 * Descriptive name of the gesture that can be displayed to the user.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String TITLE = "title";
		/**
		 * The Intent URL of the gesture, describing what it points to. This
		 * value is given to
		 * {@link android.content.Intent#parseUri(String, int)} to create an
		 * Intent that can be launched.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String INTENT = "intent";
		/**
		 * The type of the gesture
		 * 
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String ITEM_TYPE = "itemType";
		/**
		 * The gesture is an application
		 */
		public static final int ITEM_TYPE_APPLICATION = 0;
		/**
		 * The gesture is an application created shortcut
		 */
		public static final int ITEM_TYPE_SHORTCUT = 1;
		/**
		 * The icon type.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String ICON_TYPE = "iconType";
		/**
		 * The icon is a resource identified by a package name and an integer
		 * id.
		 */
		public static final int ICON_TYPE_RESOURCE = 0;
		/**
		 * The icon is a bitmap.
		 */
		public static final int ICON_TYPE_BITMAP = 1;
		/**
		 * The icon package name, if icon type is ICON_TYPE_RESOURCE.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String ICON_PACKAGE = "iconPackage";
		/**
		 * The icon resource id, if icon type is ICON_TYPE_RESOURCE.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String ICON_RESOURCE = "iconResource";
		/**
		 * The custom icon bitmap, if icon type is ICON_TYPE_BITMAP.
		 * <P>
		 * Type: BLOB
		 * </P>
		 */
		public static final String ICON = "icon";
	}
	
	/**
	 * Favorites.
	 */
	public static final class Favorites implements BaseLauncherColumns
	{
		
		static final String AUTHORITY = ConfigBase.AUTHORITY;
		static final String TABLE_FAVORITES = "favorites";
		static final String TABLE_FAVORITES_BIG_ICON = "favorites_big_icon";
		static final String TABLE_FAVORITES_SMALL_ICON = "favorites_small_icon";
		static final String PARAMETER_NOTIFY = "notify";
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/" + TABLE_FAVORITES + "?" + PARAMETER_NOTIFY + "=true" );
		public static final Uri CONTENT_URI_BIG_ICON = Uri.parse( "content://" + AUTHORITY + "/" + TABLE_FAVORITES_BIG_ICON + "?" + PARAMETER_NOTIFY + "=true" );
		public static final Uri CONTENT_URI_SMALL_ICON = Uri.parse( "content://" + AUTHORITY + "/" + TABLE_FAVORITES_SMALL_ICON + "?" + PARAMETER_NOTIFY + "=true" );
		/**
		 * The content:// style URL for this table. When this Uri is used, no
		 * notification is sent if the content changes.
		 */
		public static final Uri CONTENT_URI_NO_NOTIFICATION = Uri.parse( "content://" + AUTHORITY + "/" + TABLE_FAVORITES + "?" + PARAMETER_NOTIFY + "=false" );
		public static final Uri CONTENT_URI_NO_NOTIFICATION_BIG_ICON = Uri.parse( "content://" + AUTHORITY + "/" + TABLE_FAVORITES_BIG_ICON + "?" + PARAMETER_NOTIFY + "=false" );
		public static final Uri CONTENT_URI_NO_NOTIFICATION_SMALL_ICON = Uri.parse( "content://" + AUTHORITY + "/" + TABLE_FAVORITES_SMALL_ICON + "?" + PARAMETER_NOTIFY + "=false" );
		
		/**
		 * The content:// style URL for a given row, identified by its id.
		 * 
		 * @param id
		 *            The row id.
		 * @param notify
		 *            True to send a notification is the content changes.
		 * 
		 * @return The unique content URL for the specified row.
		 */
		public static Uri getContentUri(
				long id ,
				boolean notify )
		{
			return Uri.parse( "content://" + AUTHORITY + "/" + TABLE_FAVORITES + "/" + id + "?" + PARAMETER_NOTIFY + "=" + notify );
		}
		
		public static Uri getContentUriBigIcon(
				long id ,
				boolean notify )
		{
			return Uri.parse( "content://" + AUTHORITY + "/" + TABLE_FAVORITES_BIG_ICON + "/" + id + "?" + PARAMETER_NOTIFY + "=" + notify );
		}
		
		public static Uri getContentUriSmallIcon(
				long id ,
				boolean notify )
		{
			return Uri.parse( "content://" + AUTHORITY + "/" + TABLE_FAVORITES_SMALL_ICON + "/" + id + "?" + PARAMETER_NOTIFY + "=" + notify );
		}
		
		/**
		 * The container holding the favorite
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String CONTAINER = "container";
		/**
		 * The icon is a resource identified by a package name and an integer
		 * id.
		 */
		public static final int CONTAINER_DESKTOP = -100;
		public static final int CONTAINER_HOTSEAT = -101;
		public static final int CONTAINER_SIDEBAR = -200;
		public static final int CONTAINER_APPLIST = 10000;
		/**
		 * The screen holding the favorite (if container is CONTAINER_DESKTOP)
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String SCREEN = "screen";
		/**
		 * The X coordinate of the cell holding the favorite (if container is
		 * CONTAINER_DESKTOP or CONTAINER_DOCK)
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String CELLX = "cellX";
		/**
		 * The Y coordinate of the cell holding the favorite (if container is
		 * CONTAINER_DESKTOP)
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String CELLY = "cellY";
		/**
		 * The X span of the cell holding the favorite
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String SPANX = "spanX";
		/**
		 * The Y span of the cell holding the favorite
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String SPANY = "spanY";
		public static final String X = "x";
		public static final String Y = "y";
		public static final String ANGLE = "angle";
		/**
		 * The favorite is a user created folder
		 */
		public static final int ITEM_TYPE_USER_FOLDER = 2;
		/**
		 * The favorite is a live folder
		 */
		public static final int ITEM_TYPE_LIVE_FOLDER = 3;
		/**
		 * The favorite is a widget
		 */
		public static final int ITEM_TYPE_APPWIDGET = 4;
		/**
		 * 自定义widget
		 */
		public static final int ITEM_TYPE_WIDGET3D = 5;
		/**
		 * widget 还未下载
		 */
		public static final int ITEM_TYPE_WIDGET_VIEW = 6;
		/**
		 * icon 还未下载
		 */
		public static final int ITEM_TYPE_VIRTURE_VIEW = 7;
		//系统widget还未安装
		public static final int ITEM_TYPE_VIRTURE_SYS_WIDGET = 8;
		public static final int ITEM_TYPE_COOEE_PLUGIN = 9;
		/**
		 * The favorite is a custom shortcut
		 */
		public static final int ITEM_TYPE_CUSTOM_SHORTCUT = 10;
		/**
		 * The favorite is a clock
		 */
		public static final int ITEM_TYPE_WIDGET_CLOCK = 1000;
		/**
		 * The favorite is a search widget
		 */
		public static final int ITEM_TYPE_WIDGET_SEARCH = 1001;
		/**
		 * The favorite is a photo frame
		 */
		public static final int ITEM_TYPE_WIDGET_PHOTO_FRAME = 1002;
		/**
		 * The appWidgetId of the widget
		 * 
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String APPWIDGET_ID = "appWidgetId";
		/**
		 * Indicates whether this favorite is an application-created shortcut or
		 * not. If the value is 0, the favorite is not an application-created
		 * shortcut, if the value is 1, it is an application-created shortcut.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		@Deprecated
		public static final String IS_SHORTCUT = "isShortcut";
		/**
		 * The URI associated with the favorite. It is used, for instance, by
		 * live folders to find the content provider.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String URI = "uri";
		/**
		 * The display mode if the item is a live folder.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * 
		 * @see android.provider.LiveFolders#DISPLAY_MODE_GRID
		 * @see android.provider.LiveFolders#DISPLAY_MODE_LIST
		 */
		public static final String DISPLAY_MODE = "displayMode";//-100:fixed icon;    >0:isDynamicFolder
	}
}
