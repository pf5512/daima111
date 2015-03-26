package com.cooee.launcher.theme;

import android.content.ComponentName;
import android.content.Context;

public class ThemeDescriptor {

	Context mContext;
	public ComponentName mComponentName;
	public CharSequence mTitle;
	public boolean mUse = false;
	public boolean mSystem = false;
	public boolean mBuiltIn = false;

	public ThemeConfigHandler config;

	public ThemeDescriptor(Context context) {
		mContext = context;
	}

	public int getInteger(String key) {
		return config.getInteger(key);
	}

	public String getString(String key) {
		return config.getString(key);
	}
}
