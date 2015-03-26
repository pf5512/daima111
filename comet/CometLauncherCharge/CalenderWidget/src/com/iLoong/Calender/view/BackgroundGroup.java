package com.iLoong.Calender.view;

import com.iLoong.launcher.UI3DEngine.ViewGroup3D;

public class BackgroundGroup extends ViewGroup3D{
	public BackgroundGroup(String name) {
		super(name);
		this.transform=true;
	}
	
	public boolean is3dRotation()
	{
		return true;
	}
	
}
