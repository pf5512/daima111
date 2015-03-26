package com.iLoong.Calender.view;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;

public class DayGroup3D extends ViewGroup3D {

	public DayGroup3D(String name) {
		super(name);
		this.transform = true;
	}

	public boolean is3dRotation() {
		return true;
	}
}
