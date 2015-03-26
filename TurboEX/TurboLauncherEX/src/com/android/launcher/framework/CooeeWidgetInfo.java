package com.android.launcher.framework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.ComponentName;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.cooee.launcher.CooeeWidgetView;
import com.cooee.launcher.Launcher;

/**
 * liangxiaoling
 */

public class CooeeWidgetInfo extends ItemInfo {
	public ComponentName providerName;
	public CooeeWidgetView hostView = null;
	public boolean desktop;
	public boolean loadbyInternel;

	public CooeeWidgetInfo(ComponentName providerName) {
		itemType = LauncherSettings.Favorites.ITEM_TYPE_COOEEWIDGET;
		this.providerName = providerName;
		spanX = -1;
		spanY = -1;
	}

	public final ViewGroup createView(Context context,
			ComponentName providerName) {
		try {
			Class<?> clazz = Class.forName(providerName.getClassName());
			Class[] paramTypes = { Context.class };
			Object[] params = { Launcher.getInstance() }; // 方法传入的参数
			Constructor con = clazz.getConstructor(paramTypes); // 主要就是这句了
			RelativeLayout base = (RelativeLayout) con.newInstance(params); // BatcherBase

			if (base != null) {
				hostView = new CooeeWidgetView(context);
				hostView.addView(base);
				return hostView;
			} else {
				return null;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	void unbind() {
		super.unbind();
		hostView = null;
	}
}
