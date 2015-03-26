package com.iLoong.launcher.core;


import android.view.View;


public interface IPage
{
	
	public int getPageChildCount();
	
	public View getChildOnPageAt(
			int i );
	
	public void removeAllViewsOnPage();
	
	public void removeViewOnPageAt(
			int i );
	
	public int indexOfChildOnPage(
			View v );
}
