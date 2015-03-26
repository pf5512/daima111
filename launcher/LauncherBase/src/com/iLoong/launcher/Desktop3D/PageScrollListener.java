package com.iLoong.launcher.Desktop3D;


public interface PageScrollListener
{
	
	void pageScroll(
			float degree ,
			int index ,
			int count );
	
	public void setCurrentPage(
			int current );
	
	int getIndex();
}
