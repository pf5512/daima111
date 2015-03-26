package com.coco.theme.themebox.database.model;

public enum ApplicationType {

	AppTheme(0), AppLock(1);

	private int value;

	private ApplicationType(int v) {
		this.value = v;
	}

	public int getValue() {
		return value;
	}

	public static ApplicationType fromValue(int v) {
		for (ApplicationType appType : ApplicationType.values()) {
			if (appType.getValue() == v) {
				return appType;
			}
		}
		return AppTheme;
	}
}
