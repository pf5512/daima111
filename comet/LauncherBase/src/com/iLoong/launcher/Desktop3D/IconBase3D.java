package com.iLoong.launcher.Desktop3D;


import com.iLoong.launcher.data.ItemInfo;


/* 能放在桌面上的东西都要实现这个接口 */
public interface IconBase3D
{
	
	public void setItemInfo(
			ItemInfo info );
	
	public ItemInfo getItemInfo();
	
	// already implements in View3D
	public Object getTag();
	
	public float getWidth();
	
	public float getHeight();
	
	public float getX();
	
	public float getY();
}
