/**
 * 
 */
package com.cooee.launcher.common;

import java.io.File;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewConfiguration;

import com.cooee.launcher.Launcher;

/**
 * @author Hugo.ye added 20141223
 * 
 */
final public class CommonUtils {

	/**
	 * 获取设备像素级别的屏幕宽度
	 * 
	 * @return
	 */
	public static int getScreenPixelsWidth() {

		if (Launcher.getInstance() != null) {
			int width = Launcher.getInstance().getResources()
					.getDisplayMetrics().widthPixels;
			return width;
		}
		return -1;
	}

	/**
	 * 获取设备像素级别的屏幕高度
	 * 
	 * @return
	 */
	public static int getScreenPixelsHeight() {

		if (Launcher.getInstance() != null) {
			int height = Launcher.getInstance().getResources()
					.getDisplayMetrics().heightPixels;
			return height;
		}
		return -1;
	}

	/**
	 * 判断魅族机型，是否有smartBar（魅族自己研发的虚拟导航栏）
	 * 
	 * @return
	 */
	public static boolean hasMeiZuSmartBar() {
		boolean result = false;
		try {
			// 新型号可用反射调用Build.hasSmartBar()
			Method method = Class.forName("android.os.Build").getMethod(
					"hasSmartBar");
			return ((Boolean) method.invoke(null)).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
		if (Build.DEVICE.equals("mx2")) {
			result = true;
		} else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
			result = false;
		}
		return result;
	}

	/**
	 * 通过判断设备是否有返回键、菜单键（不是虚拟键，是手机屏幕外的物理按键）来确定是否有navigation bar
	 * 
	 * @return true if has navigation bar, false otherwise.
	 */
	@SuppressLint("NewApi")
	public static boolean checkDeviceHasNavigationBar() {

		if (Build.VERSION.SDK_INT >= 17) {
			Display d = Launcher.getInstance().getWindowManager()
					.getDefaultDisplay();

			DisplayMetrics realDisplayMetrics = new DisplayMetrics();
			d.getRealMetrics(realDisplayMetrics);

			int realHeight = realDisplayMetrics.heightPixels;
			int realWidth = realDisplayMetrics.widthPixels;

			DisplayMetrics displayMetrics = new DisplayMetrics();
			d.getMetrics(displayMetrics);

			int displayHeight = displayMetrics.heightPixels;
			int displayWidth = displayMetrics.widthPixels;

			return (realWidth > displayWidth) || (realHeight > displayHeight);
		} else {
			boolean hasMenuKey = ViewConfiguration.get(Launcher.getInstance())
					.hasPermanentMenuKey();

			if (!hasMenuKey) {
				return true;
			}
		}
		return false;
	}
//添加桌面设置系统设置方法 liangxiaoling@2014/12/27 ADD START
	public static String getSDPath() {
		File SDdir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			SDdir = Environment.getExternalStorageDirectory();
		}
		if (SDdir != null) {
			return SDdir.toString();
		} else {
			return null;
		}
	}

	public static String getBackupPath() {
		String sd = getSDPath();
		if (sd == null)
			return null;
		String backup = "/turbolauncherex/backup/"
				+ Launcher.getInstance().getPackageName();
		File file = new File(sd, backup);
		if (!file.exists())
			file.mkdirs();
		return file.getAbsolutePath();
	}

	public static String dataDir = "";

	public static String getPreferencePath() {
		dataDir = Launcher.getInstance().getApplication().getApplicationInfo().dataDir;
		if (dataDir == null)
			return null;
		return dataDir + "/shared_prefs/";
	}
	//添加桌面设置系统设置方法 liangxiaoling@2014/12/27 ADD END
}
