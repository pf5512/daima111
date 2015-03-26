package com.cooee.launcher.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.util.Log;

/**
 * the whole file is added by cooee Hugo.ye
 * 
 * @author user
 * 
 */
final public class ReflectionMethodsInvoke {
	public static boolean bindAppWidgetId(AppWidgetManager widgetManager,
			int appWidgetId, ComponentName componentName) {
		boolean rst = false;
		try {
			Class<?> clz = widgetManager.getClass();
			Method method = clz.getMethod("bindAppWidgetId", Integer.class,
					ComponentName.class);
			method.invoke(widgetManager, appWidgetId, componentName);
			rst = true;
		} catch (NoSuchMethodException e) {
			Log.e("TurboLauncherEX", e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			Log.e("TurboLauncherEX", e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.e("TurboLauncherEX", e.getLocalizedMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Log.e("TurboLauncherEX", e.getLocalizedMessage());
			e.printStackTrace();
		}
		return rst;
	}
}
