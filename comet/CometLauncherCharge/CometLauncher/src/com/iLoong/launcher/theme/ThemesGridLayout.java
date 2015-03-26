package com.iLoong.launcher.theme;


import com.iLoong.launcher.SetupMenu.PagedView;

import android.content.Context;
import android.view.MotionEvent;


public class ThemesGridLayout extends PagedView
{
	
	public ThemesGridLayout(
			Context context )
	{
		super( context );
	}
	
	@Override
	public void computeScroll()
	{
		super.computeScroll();
	}
	
	@Override
	public boolean onInterceptTouchEvent(
			MotionEvent ev )
	{
		return super.onInterceptTouchEvent( ev );
	}
	
	@Override
	public boolean onTouchEvent(
			MotionEvent ev )
	{
		return super.onTouchEvent( ev );
	}
}
