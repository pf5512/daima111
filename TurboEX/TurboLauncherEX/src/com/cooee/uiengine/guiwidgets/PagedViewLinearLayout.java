package com.cooee.uiengine.guiwidgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 桌面编辑模块代码
 * 
 * @author LinYu
 * 
 */
public class PagedViewLinearLayout extends LinearLayout {

	public PagedViewLinearLayout(Context context) {
		this(context, null);
	}

	public PagedViewLinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PagedViewLinearLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
}
