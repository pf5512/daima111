package com.cooee.launcher.layout.virtualicon;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.android.launcher.framework.ShortcutInfo;
import com.cooee.launcher.layout.virtualicon.VirtualClick.CallSpecification;
import com.cooee.launcher.theme.FavoriteInfo;
import com.cooee.launcher.theme.ThemeData;

/**
 * 
 * @author zhongqihong
 * **/

public class VirtualInvoker {

	final String TAG = "VirtualInvoker";

	public static Context mContext;

	public static void init(Context context) {
		mContext = context;
	}

	public VirtualInvoker() {

	}

	public void invoke(ShortcutInfo info) {
		String className = null;
		String functionName = null;

		for (int i = 0; i < ThemeData.virtualIcons.size(); i++) {

			FavoriteInfo fi = ThemeData.virtualIcons.get(i);
			ComponentName cn = new ComponentName(fi.pkgName, fi.className);
			if (info.intent.getComponent().getClassName().equals(fi.className)
					&& info.intent.getComponent().getPackageName()
							.equals(fi.pkgName)) {
				functionName = fi.className;
				className = fi.pkgName;
				break;
			}

		}
		if (className != null && functionName != null) {
			CallSpecification cs = new CallSpecification();
			// cs.argumentsTypes = new Class[2];
			// cs.argumentsTypes[0] = Integer.class;
			// cs.argumentsTypes[1] = Float.class;
			// cs.arguments = new Object[2];
			// cs.arguments[0] = 4;
			// cs.arguments[1] = Float.parseFloat("5.0");
			cs.callSpec = new String[2];
			cs.callSpec[1] = functionName;
			cs.callSpec[0] = className;
			VirtualClick.call(cs);
		}

	}

	public void virtual_invoke_themeBox() {
		final String TAG_THEME = "tagTheme";
		final String TAG_STRING = "currentTab";
		Intent i = new Intent();
		i.setComponent(new ComponentName(mContext,
				"com.coco.theme.themebox.MainActivity"));
		i.putExtra(TAG_STRING, TAG_THEME);
		mContext.startActivity(i);
	}

	public void virtual_invoke_wallpaper() {
		final String TAG_WALLPAPER = "tagWallpaper";
		final String TAG_STRING = "currentTab";
		Intent i = new Intent();
		i.setComponent(new ComponentName(mContext,
				"com.coco.theme.themebox.MainActivity"));
		i.putExtra(TAG_STRING, TAG_WALLPAPER);
		mContext.startActivity(i);
	}
}
