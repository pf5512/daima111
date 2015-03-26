package com.iLoong.WeatherClock.view;

import com.iLoong.WeatherClock.common.Parameter;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class ClockViewGroup extends ViewGroup3D
{
	
	public ClockViewGroup(String name)
	{
		super( name );
		this.transform=true;
		this.setOrigin(Parameter.CLOCK_MOVE_X , Parameter.CLOCK_MOVE_Y );
		this.setRotationVector( 0 , 0 , 1 );
	}
	
	
}
