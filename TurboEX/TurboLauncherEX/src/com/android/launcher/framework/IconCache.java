/*
\ * Copyright (C) 2008 The Android Open Source Project
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

import java.util.HashMap;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import com.cooee.launcher.LauncherApplication;
import com.cooee.launcher.theme.ThemeData;
import com.cooee.launcher.theme.ThemeManager;

/**
 * Cache of application icons. Icons can be made from any thread.
 */
public class IconCache {
	@SuppressWarnings("unused")
	private static final String TAG = "Launcher.IconCache";

	private static final int INITIAL_ICON_CACHE_CAPACITY = 50;

	private static class CacheEntry {
		public Bitmap icon;
		public String title;
	}

	private final Bitmap mDefaultIcon;
	private final LauncherApplication mContext;
	private final PackageManager mPackageManager;
	private final HashMap<ComponentName, CacheEntry> mCache = new HashMap<ComponentName, CacheEntry>(
			INITIAL_ICON_CACHE_CAPACITY);
	private int mIconDpi;

	public IconCache(LauncherApplication context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		mContext = context;
		mPackageManager = context.getPackageManager();
		mIconDpi = activityManager.getLauncherLargeIconDensity();

		// need to set mIconDpi before getting default icon
		mDefaultIcon = makeDefaultIcon();
	}

	public Drawable getFullResDefaultActivityIcon() {
		return getFullResIcon(Resources.getSystem(),
				android.R.mipmap.sym_def_app_icon);
	}

	public Drawable getFullResIcon(Resources resources, int iconId) {
		Drawable d = null;
		try {
			if (Build.VERSION.SDK_INT >= 15) { // Modified by cooee Hugo.ye for
												// API level
				d = resources.getDrawableForDensity(iconId, mIconDpi);
			}
		} catch (Resources.NotFoundException e) {
			d = null;
		}

		return (d != null) ? d : getFullResDefaultActivityIcon();
	}

	public Drawable getFullResIcon(String packageName, int iconId) {
		Resources resources;
		try {
			resources = mPackageManager.getResourcesForApplication(packageName);
		} catch (PackageManager.NameNotFoundException e) {
			resources = null;
		}
		if (resources != null) {
			if (iconId != 0) {
				return getFullResIcon(resources, iconId);
			}
		}
		return getFullResDefaultActivityIcon();
	}

	public Drawable getFullResIcon(ResolveInfo info) {
		return getFullResIcon(info.activityInfo);
	}

	public Drawable getFullResIcon(ActivityInfo info) {

		Resources resources;
		try {
			resources = mPackageManager
					.getResourcesForApplication(info.applicationInfo);
		} catch (PackageManager.NameNotFoundException e) {
			resources = null;
		}
		if (resources != null) {
			int iconId = info.getIconResource();
			if (iconId != 0) {
				return getFullResIcon(resources, iconId);
			}
		}
		return getFullResDefaultActivityIcon();
	}

	private Bitmap makeDefaultIcon() {
		Drawable d = getFullResDefaultActivityIcon();
		Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1),
				Math.max(d.getIntrinsicHeight(), 1), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		d.setBounds(0, 0, b.getWidth(), b.getHeight());
		d.draw(c);
		c.setBitmap(null);
		return b;
	}

	/**
	 * Remove any records for the supplied ComponentName.
	 */
	public void remove(ComponentName componentName) {
		synchronized (mCache) {
			mCache.remove(componentName);
		}
	}

	/**
	 * Empty out the cache.
	 */
	public void flush() {
		synchronized (mCache) {
			mCache.clear();
		}
	}

	/**
	 * Fill in "application" with the icon and label for "info."
	 */
	public void getTitleAndIcon(ApplicationInfo application, ResolveInfo info,
			HashMap<Object, CharSequence> labelCache) {
		synchronized (mCache) {
			CacheEntry entry = cacheLocked(application.componentName, info,
					labelCache);

			application.title = entry.title;
			application.iconBitmap = entry.icon;
		}
	}

	// God loves Zhongqihong@2014/12/16 ADD START
	// this function is for filling in application by ResolveInfo.
	public void getTitleAndIcon(ApplicationInfo application, ResolveInfo info) {
		getTitleAndIcon(application, info, null);
		// synchronized (mCache) {

		// CacheEntry entry = cacheLocked(application.componentName, info,
		// null);
		// application.title = entry.title;
		// application.iconBitmap = entry.icon;
		// }
	}

	// God loves Zhongqihong@2014/12/16 ADD END
	public Bitmap getIcon(Intent intent) {
		synchronized (mCache) {
			final ResolveInfo resolveInfo = mPackageManager.resolveActivity(
					intent, 0);
			ComponentName component = intent.getComponent();

			if (resolveInfo == null || component == null) {
				return mDefaultIcon;
			}

			CacheEntry entry = cacheLocked(component, resolveInfo, null);
			return entry.icon;
		}
	}

	public Bitmap getIcon(ComponentName component, ResolveInfo resolveInfo,
			HashMap<Object, CharSequence> labelCache) {
		synchronized (mCache) {
			if (resolveInfo == null || component == null) {
				return null;
			}

			CacheEntry entry = cacheLocked(component, resolveInfo, labelCache);
			return entry.icon;
		}
	}

	public boolean isDefaultIcon(Bitmap icon) {
		return mDefaultIcon == icon;
	}

	private CacheEntry cacheLocked(ComponentName componentName,
			ResolveInfo info, HashMap<Object, CharSequence> labelCache) {
		CacheEntry entry = mCache.get(componentName);

		// zhongqihong -2014/12/8
		if (entry == null) {
			Bitmap bitmap = ThemeManager.getInstance().getDefaultIcon(info);
			if (bitmap != null) {
				this.setIcon(info, bitmap);
				entry = mCache.get(componentName);
			}

		}
		// zhongqihong end

		if (entry == null) {
			entry = new CacheEntry();

			mCache.put(componentName, entry);

			ComponentName key = LauncherModel
					.getComponentNameFromResolveInfo(info);
			if (labelCache != null && labelCache.containsKey(key)) {
				entry.title = labelCache.get(key).toString();
			} else {
				entry.title = info.loadLabel(mPackageManager).toString();
				if (labelCache != null) {
					labelCache.put(key, entry.title);
				}
			}
			if (entry.title == null) {
				entry.title = info.activityInfo.name;
			}

			// entry.icon = Utilities.createIconBitmap(getFullResIcon(info),
			// mContext);
			// zhongqihong -2014/12/8

			Drawable icon;
			icon = getFullResIcon(info);

			entry.icon = beautyIcon(
					Utilities.createIconBitmap(icon, mContext, true), true);
			// zhongqihong end
		}
		return entry;
	}

	public HashMap<ComponentName, Bitmap> getAllIcons() {
		synchronized (mCache) {
			HashMap<ComponentName, Bitmap> set = new HashMap<ComponentName, Bitmap>();
			for (ComponentName cn : mCache.keySet()) {
				final CacheEntry e = mCache.get(cn);
				set.put(cn, e.icon);
			}
			return set;
		}
	}

	// zhongqihong -2014/12/8
	public void setIcon(ResolveInfo info, Bitmap bitmap) {
		ComponentName component = new ComponentName(
				info.activityInfo.applicationInfo.packageName,
				info.activityInfo.name);
		if (info == null || component == null) {
			if (bitmap != null) {
				bitmap.recycle();
			}
			return;
		}
		CacheEntry entry = mCache.get(component);
		if (entry == null) {
			entry = new CacheEntry();
			mCache.put(component, entry);
			entry.title = info.loadLabel(mPackageManager).toString();
			if (entry.title == null) {
				entry.title = info.activityInfo.name;
			}
			// entry.icon = Utilities.resampleIconBitmap(bitmap, mContext);
			entry.icon = beautyIcon(bitmap, false);
		} else {
			if (entry.icon != null) {
				entry.icon.recycle();
				// Log.e( TAG , "IconCache.oldIcon:" + entry.icon + " newIcon:"
				// + bitmap + " old.isRecycle:" + entry.icon.isRecycled() );
			}
			entry.icon = entry.icon = entry.icon = beautyIcon(bitmap, false);
			// Utilities.resampleIconBitmap(bitmap,
			// mContext);
			mCache.put(component, entry);
		}
	}

	public Bitmap beautyIcon(Bitmap icon, boolean needBeauty) {

		ThemeManager tm = ThemeManager.getInstance();
		Bitmap bmp = null;

		Bitmap dup = null;
		float sizeScale = tm
				.getThemeConfigInteger("theme_thirdapk_icon_scaleFactor") / 100.0f;

		if (needBeauty) {
			dup = Utilities.resizeImage(icon,
					(int) (icon.getWidth() * sizeScale),
					(int) (icon.getHeight() * sizeScale));
		} else {
			dup = icon;
		}

		Bitmap bitmap = Utilities.resampleIconBitmap(dup, mContext);

		if (bitmap != icon) {
			icon.recycle();

		}
		if (dup != bitmap) {
			dup.recycle();

		}

		if (needBeauty) {

			if (tm != null)
				bmp = Utilities.combineIcon(mContext, bitmap, tm.getIconBg(),
						tm.getIconMask(), tm.getIconCover());
			if (bmp != null)
				return bmp;
		}

		return bitmap;

	}

	// God loves Zhongqihong@2014/12/20 ADD START
	public void setHotseatIcon() {
		synchronized (mCache) {
			for (int i = 0; i < ThemeData.defaultIcons.size(); i++) {

				String pkgName = ThemeData.defaultIcons.get(i).pkgName;
				String clsName = ThemeData.defaultIcons.get(i).className;
				ComponentName cn = new ComponentName(pkgName, clsName);
				for (ComponentName cacheCn : mCache.keySet()) {
					if (cacheCn.getPackageName().equals(cn.getPackageName())
							&& cacheCn.getClassName().equals(cn.getClassName())) {

						Log.v(TAG, "hotseat[" + i + "]:pkg=" + pkgName
								+ " cls=" + clsName);
					}
				}
			}
		}

	}
	// God loves Zhongqihong@2014/12/20 ADD END
	// zhongqihong end
}
