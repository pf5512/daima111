package com.cooee.launcher.theme;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.android.launcher.framework.LauncherProvider;
import com.android.launcher.framework.LauncherSettings;
import com.android.launcher.framework.LauncherSettings.Favorites;
import com.cooee.launcher.LauncherApplication;

/**
 * 
 * @author zhongqihong
 * 
 * 
 *         handles all layout on workspace by default configuration from xml.
 * 
 * 
 * */
public class ThemeLayout {

	public void insertVirtualIcons(Context context) {

		ContentValues values = new ContentValues();
		LauncherApplication app = (LauncherApplication) context
				.getApplicationContext();
		LauncherProvider provider = app.getLauncherProvider();
		for (int i = 0; i < ThemeData.virtualIcons.size(); i++) {
			FavoriteInfo favorite = ThemeData.virtualIcons.get(i);
			Intent intent = new Intent();
			values.clear();
			values.put(LauncherSettings.Favorites.CONTAINER, favorite.container);
			values.put(LauncherSettings.Favorites.SCREEN, favorite.screen);
			values.put(LauncherSettings.Favorites.CELLX, favorite.cellX);
			values.put(LauncherSettings.Favorites.CELLY, favorite.cellY);
			// values.put(LauncherSettings.Favorites.INTENT,
			// favorite.intentUri);
			long id = provider.generateNewId();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			intent.setComponent(new ComponentName(favorite.pkgName,
					favorite.className));
			values.put(LauncherSettings.Favorites.INTENT, intent.toUri(0));
			values.put(LauncherSettings.Favorites.TITLE,
					favorite.title.toString());
			values.put(LauncherSettings.Favorites.ITEM_TYPE,
					Favorites.ITEM_TYPE_VIRTUALITY);
			values.put(LauncherSettings.Favorites.SPANX, 1);
			values.put(LauncherSettings.Favorites.SPANY, 1);
			values.put(LauncherSettings.Favorites._ID, id);
			values.put(Favorites.ICON_TYPE,
					LauncherSettings.Favorites.ICON_TYPE_CUSTOM);

			values.put(Favorites.ICON_PACKAGE, context.getPackageName());
			values.put(Favorites.ICON_RESOURCE, favorite.intentUri);
			if (provider.FavoriteInsertAndCheck(values) < 0) {

				throw new RuntimeException(
						"failed to insert an Virsul icon to database..");
			}

		}
	}

	public void insertSmartLayout() {

	}

}
