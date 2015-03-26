/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.android.launcher.framework;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Settings related utilities.
 */
public class LauncherSettings {

	/** Columns required on table staht will be subject to backup and restore. */
	static interface ChangeLogColumns extends BaseColumns {
		/**
		 * The time of the last update to this row.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		static final String MODIFIED = "modified";
	}

	static interface BaseLauncherColumns extends BaseColumns {
		/**
		 * Descriptive name of the gesture that can be displayed to the user.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		static final String TITLE = "title";

		/**
		 * The Intent URL of the gesture, describing what it points to. This
		 * value is given to
		 * {@link android.content.Intent#parseUri(String, int)} to create an
		 * Intent that can be launched.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		static final String INTENT = "intent";

		/**
		 * The type of the gesture
		 * 
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		static final String ITEM_TYPE = "itemType";

		/**
		 * The gesture is an application
		 */
		static final int ITEM_TYPE_APPLICATION = 0;

		/**
		 * The gesture is an application created shortcut
		 */
		static final int ITEM_TYPE_SHORTCUT = 1;

		/**
		 * The icon type.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		static final String ICON_TYPE = "iconType";

		/**
		 * The icon is a resource identified by a package name and an integer
		 * id.
		 */
		static final int ICON_TYPE_RESOURCE = 0;

		/**
		 * The icon is a bitmap.
		 */
		static final int ICON_TYPE_BITMAP = 1;

		// God loves Zhongqihong@2014/12/20 ADD START

		static final int ICON_TYPE_CUSTOM = 2;
		// God loves Zhongqihong@2014/12/20 ADD END

		/**
		 * The icon package name, if icon type is ICON_TYPE_RESOURCE.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		static final String ICON_PACKAGE = "iconPackage";

		/**
		 * The icon resource id, if icon type is ICON_TYPE_RESOURCE.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		static final String ICON_RESOURCE = "iconResource";

		/**
		 * The custom icon bitmap, if icon type is ICON_TYPE_BITMAP.
		 * <P>
		 * Type: BLOB
		 * </P>
		 */
		static final String ICON = "icon";
	}

	/**
	 * Workspace Screens.
	 * 
	 * Tracks the order of workspace screens.
	 */
	public static final class WorkspaceScreens implements ChangeLogColumns {
		/**
		 * The content:// style URL for this table
		 */
		static final Uri CONTENT_URI = Uri.parse("content://"
				+ LauncherProvider.AUTHORITY + "/"
				+ LauncherProvider.TABLE_WORKSPACE_SCREENS + "?"
				+ LauncherProvider.PARAMETER_NOTIFY + "=true");

		/**
		 * The rank of this screen -- ie. how it is ordered relative to the
		 * other screens.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		static final String SCREEN_RANK = "screenRank";
	}

	/**
	 * Favorites.
	 */
	public static final class Favorites implements BaseLauncherColumns {
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ LauncherProvider.AUTHORITY + "/"
				+ LauncherProvider.TABLE_FAVORITES + "?"
				+ LauncherProvider.PARAMETER_NOTIFY + "=true");

		/**
		 * The content:// style URL for this table. When this Uri is used, no
		 * notification is sent if the content changes.
		 */
		static final Uri CONTENT_URI_NO_NOTIFICATION = Uri.parse("content://"
				+ LauncherProvider.AUTHORITY + "/"
				+ LauncherProvider.TABLE_FAVORITES + "?"
				+ LauncherProvider.PARAMETER_NOTIFY + "=false");

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
		public static Uri getContentUri(long id, boolean notify) {
			return Uri.parse("content://" + LauncherProvider.AUTHORITY + "/"
					+ LauncherProvider.TABLE_FAVORITES + "/" + id + "?"
					+ LauncherProvider.PARAMETER_NOTIFY + "=" + notify);
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

		/**
		 * The screen holding the favorite (if container is CONTAINER_DESKTOP)
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String SCREEN = "screen";

		// public static final String SCREEN = "screen";

		/**
		 * The X coordinate of the cell holding the favorite (if container is
		 * CONTAINER_HOTSEAT or CONTAINER_HOTSEAT)
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
		// 添加新的widget类型需要的参数 liangxiaoling@2014/12/30 ADD START
		public static final String DESKTOP = "desktop";
		public static final String LOADBYINTERNAL = "loadByInternal";
		public static final String PKGNAME = "packageName";
		public static final String CLSNAME = "className";
		// 添加新的widget类型需要的参数 liangxiaoling@2014/12/30 ADD END
		/**
		 * The favorite is a user created folder
		 */
		public static final int ITEM_TYPE_FOLDER = 2;

		/**
		 * The favorite is a live folder
		 * 
		 * Note: live folders can no longer be added to Launcher, and any live
		 * folders which exist within the launcher database will be ignored when
		 * loading. That said, these entries in the database may still exist,
		 * and are not automatically stripped.
		 */
		static final int ITEM_TYPE_LIVE_FOLDER = 3;

		/**
		 * The favorite is a widget
		 */
		public static final int ITEM_TYPE_APPWIDGET = 4;

		// 添加widget新的类型liangxiaoling@2014/12/30 ADD START
		public static final int ITEM_TYPE_COOEEWIDGET = 6;
		// 添加widget新的类型 liangxiaoling@2014/12/30 ADD END
		// God loves Zhongqihong@2014/12/22 ADD START
		// The gesture is an application created virtual
		public static final int ITEM_TYPE_VIRTUALITY = 5;
		// God loves Zhongqihong@2014/12/22 ADD END

		/**
		 * The favorite is a clock
		 */
		static final int ITEM_TYPE_WIDGET_CLOCK = 1000;

		/**
		 * The favorite is a search widget
		 */
		static final int ITEM_TYPE_WIDGET_SEARCH = 1001;

		/**
		 * The favorite is a photo frame
		 */
		static final int ITEM_TYPE_WIDGET_PHOTO_FRAME = 1002;

		/**
		 * The appWidgetId of the widget
		 * 
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		static final String APPWIDGET_ID = "appWidgetId";

		/**
		 * Indicates whether this favorite is an application-created shortcut or
		 * not. If the value is 0, the favorite is not an application-created
		 * shortcut, if the value is 1, it is an application-created shortcut.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		@Deprecated
		static final String IS_SHORTCUT = "isShortcut";

		/**
		 * The URI associated with the favorite. It is used, for instance, by
		 * live folders to find the content provider.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		static final String URI = "uri";

		/**
		 * The display mode if the item is a live folder.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * 
		 * @see android.provider.LiveFolders#DISPLAY_MODE_GRID
		 * @see android.provider.LiveFolders#DISPLAY_MODE_LIST
		 */
		static final String DISPLAY_MODE = "displayMode";
	}
}
