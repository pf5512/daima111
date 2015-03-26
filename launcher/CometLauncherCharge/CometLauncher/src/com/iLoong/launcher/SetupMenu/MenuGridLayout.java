package com.iLoong.launcher.SetupMenu;


import android.content.Context;
import android.view.MotionEvent;


public class MenuGridLayout extends PagedView
{
	
	private PageScroller mScroller;
	
	public MenuGridLayout(
			Context context )
	{
		super( context );
	}
	
	public void setScroller(
			PageScroller scroller )
	{
		mScroller = scroller;
	}
	
	@Override
	public void computeScroll()
	{
		if( mScroller != null )
			mScroller.computeScroll();
		else
			super.computeScroll();
	}
	
	@Override
	public boolean onInterceptTouchEvent(
			MotionEvent ev )
	{
		if( mScroller != null )
			return mScroller.onInterceptTouchEvent( ev );
		else
			return super.onInterceptTouchEvent( ev );
	}
	
	@Override
	public boolean onTouchEvent(
			MotionEvent ev )
	{
		if( mScroller != null )
			return mScroller.onTouchEvent( ev );
		else
			return super.onTouchEvent( ev );
	}
}
