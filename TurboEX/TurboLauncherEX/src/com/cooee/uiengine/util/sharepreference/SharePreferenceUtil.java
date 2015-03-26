package com.cooee.uiengine.util.sharepreference;

import android.content.Context;
import android.content.SharedPreferences;

import com.cooee.launcher.theme.ThemeConstants;

public class SharePreferenceUtil {

	public static SharedPreferences sp;
	public static SharedPreferences.Editor editor;
	public static Context mContext;
	public final static String file = "launcher_sharepre";

	public static void InitPreferenceUtil(Context context) {

		sp = context.getSharedPreferences(file, context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public static void setSLState(boolean value) {
		editor.putBoolean(ThemeConstants.smart_layout_injection, value);
		editor.commit();
	}

	public static boolean getSLstate() {
		return sp.getBoolean(ThemeConstants.smart_layout_injection, false);
	}

	public static boolean getVirtualState() {

		return sp.getBoolean(ThemeConstants.virtual_icon_injection, false);
	}

	public static void setVirtualState(boolean value) {
		editor.putBoolean(ThemeConstants.virtual_icon_injection, value);
		editor.commit();
	}

}
