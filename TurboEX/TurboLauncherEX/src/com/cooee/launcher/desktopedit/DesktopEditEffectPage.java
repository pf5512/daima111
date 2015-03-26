package com.cooee.launcher.desktopedit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

/**
 * 桌面编辑模块代码
 * 
 * @author LinYu
 * 
 */
public class DesktopEditEffectPage extends DesktopEditPage {

	private LayoutInflater mInflater;

	public DesktopEditEffectPage(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DesktopEditEffectPage(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContentIsRefreshable = false;
		mInflater = LayoutInflater.from(context);
	}
}
