package com.coco.theme.themebox;

import java.util.Vector;


import android.app.Activity;


public class ActivityManager {
	private static Vector<Activity> mActivitys = new Vector<Activity>();
	
	public static  void pushActivity(Activity activity) {
		mActivitys.addElement(activity);
	}

	public static void popupActivity(Activity activity) {
		mActivitys.removeElement(activity);
	}
	
	public static void KillActivity() {
		for (int i = 0; i < mActivitys.size(); i++) {
			mActivitys.elementAt(i).finish();
		}
		mActivitys.clear();
	}

}
