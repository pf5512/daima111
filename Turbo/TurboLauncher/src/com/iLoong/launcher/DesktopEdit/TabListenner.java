package com.iLoong.launcher.DesktopEdit;



public interface TabListenner
{
	public void onMenuClick(int Position);
	
	public float getStartX();
	
	public float getStartY();
	
	public float getEndX();
	
	public float getEndY();
	
	public int getMenuCount();
	
	public float getMenuWidth(); 
}
