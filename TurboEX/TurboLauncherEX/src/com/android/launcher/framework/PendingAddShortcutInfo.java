package com.android.launcher.framework;

import android.content.pm.ActivityInfo;

public class PendingAddShortcutInfo extends PendingAddItemInfo {

	public ActivityInfo shortcutActivityInfo;

	public PendingAddShortcutInfo(ActivityInfo activityInfo) {
		shortcutActivityInfo = activityInfo;
	}

	@Override
	public String toString() {
		return "Shortcut: " + shortcutActivityInfo.packageName;
	}
}
