package com.cooee.launcher.desktopedit;

import android.content.Context;
import android.util.AttributeSet;

import com.cooee.uiengine.guiwidgets.PagedView;

/**
 * 桌面编辑模块代码
 * 
 * @author LinYu
 * 
 */
public class DesktopEditPage extends PagedView {

	public DesktopEditPage(Context context) {
		this(context, null, 0);
	}

	public DesktopEditPage(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DesktopEditPage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void syncPages() {
	}

	@Override
	public void syncPageItems(int page, boolean immediate) {
	}

}
