/* 文件名: ThemeReceiver.java 2014年8月26日
 * 
 * 描述:主题广播接收器,主要接收主题相关操作的广播,例如切换主题
 * 
 * 作者: cooee */
package com.cooee.launcher.theme;

import com.cooee.launcher.Launcher;
import com.cooee.launcher.pub.provider.PubProviderHelper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

public class ThemeReceiver extends BroadcastReceiver {

	private static final String TAG = "ThemeReceiver";
	public static final String ACTION_DEFAULT_THEME_CHANGED = "com.coco.theme.action.DEFAULT_THEME_CHANGED";
	public static final String ACTION_LAUNCHER_CLICK_THEME = "com.cooee.launcher.click_theme";
	public static final String ACTION_LAUNCHER_REQ_RESUME_TIME = "com.cooee.launcher.req_resume_time";
	public static final String ACTION_LAUNCHER_RSP_RESUME_TIME = "com.cooee.launcher.rsp_resume_time";
	public static String ACTION_LAUNCHER_RESTART = "com.coco.launcher.restart";
	public static final String ACTION_LAUNCHER_APPLY_THEME = "com.coco.launcher.apply_theme";
	public static final String ACTI0N_LAUNCHER_ADD_WIDGET = "com.coco.launcher.add.widget";
	private Handler mHandler = new Handler();

	@Override
	public void onReceive(Context c, Intent intent) {
		Log.d(TAG, "intent:" + intent.getAction());

		final String action = intent.getAction();
		if (action.equals(ACTION_LAUNCHER_RESTART)) {
			restart(c, false);
		} else if (action.equals(ACTION_LAUNCHER_APPLY_THEME)) {
			// UtilsShortcut.setWallpaperPositon( -1 );
			applyTheme(c, intent);
		} else if (action.equals(ACTION_LAUNCHER_CLICK_THEME)) {
			String selectedLauncher = intent
					.getStringExtra("selected_launcher");
			String themePkgName = intent.getStringExtra("theme_pkg_name");
			if (selectedLauncher != null
					&& selectedLauncher.equals(c.getPackageName())
					&& themePkgName != null) {
				Intent intent2 = new Intent(ACTION_LAUNCHER_APPLY_THEME);// add
																			// start
																			// for
																			// personal_center
																			// separate
																			// 2014.01.23
																			// by
																			// hupeng
				intent2.putExtra("theme_status", 1);
				intent2.putExtra("theme", themePkgName);
				applyTheme(c, intent2);
				restart(c, true);
				c.sendBroadcast(new Intent(ACTION_DEFAULT_THEME_CHANGED));// add
																			// start
																			// for
																			// personal_center
																			// separate
																			// 2014.01.23
																			// by
																			// hupeng
			}
		} else if (action.equals(ACTION_LAUNCHER_REQ_RESUME_TIME)) {
			SharedPreferences prefs2 = c.getSharedPreferences("launcher",
					Context.MODE_WORLD_READABLE);
			Intent intent2 = new Intent(ACTION_LAUNCHER_RSP_RESUME_TIME);
			intent2.putExtra("resume_time", prefs2.getLong("resume_time", -1));
			intent2.putExtra("launcher_pkg_name", c.getPackageName());
			c.sendBroadcast(intent2);
		}
	}

	/**
	 * 应用主题的接口
	 * 
	 * @param c
	 *            上下文
	 * @param intent
	 *            Intent对象
	 */
	public void applyTheme(final Context c, Intent intent) {
		SharedPreferences prefs = c.getSharedPreferences("theme",
				Activity.MODE_WORLD_WRITEABLE);
		prefs.edit().putString("theme", intent.getStringExtra("theme"))
				.commit();
		prefs.edit()
				.putInt("theme_status", intent.getIntExtra("theme_status", 1))
				.commit();

		PubProviderHelper.addOrUpdateValue("theme", "theme",
				intent.getStringExtra("theme"));
		PubProviderHelper.addOrUpdateValue("theme", "theme_status",
				String.valueOf(intent.getIntExtra("theme_status", 1)));

		restart(c, false);
	}

	/**
	 * 桌面重新启动的接口
	 * 
	 * @param c
	 *            上下文
	 * @param fromThemeClick
	 *            是否是从桌面直接点击主题的操作
	 */
	public void restart(Context c, boolean fromThemeClick) {

		if (ThemeManager.getInstance() == null) {
			final Intent intent2 = new Intent();
			intent2.setClass(c, Launcher.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			c.startActivity(intent2);
			System.exit(0);
		} else if (fromThemeClick) {
			final Intent intent2 = new Intent();
			intent2.setClass(c, Launcher.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			c.startActivity(intent2);
		} else {
			Log.v("Launcher", "exit========");
			final Intent intent2 = new Intent();
			intent2.setClass(c, Launcher.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			c.startActivity(intent2);
			System.exit(0);
		}
	}
}
// enable_themebox
