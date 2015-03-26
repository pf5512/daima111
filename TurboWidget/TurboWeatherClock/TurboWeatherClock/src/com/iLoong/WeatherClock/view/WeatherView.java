package com.iLoong.WeatherClock.view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.WeatherClock.common.Parameter;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class WeatherView extends PluginViewObject3D
{
	public WeatherView(
			MainAppContext appContext ,
			String name ,
			TextureRegion region ,
			String objName )
	{
		super( appContext , name , region , objName );
		super.build();
		move( Parameter.WEATHER_MOVE_X , Parameter.WEATHER_MOVE_Y , Parameter.WEATHER_MOVE_Z );
	}
	protected void initObjScale()
	{
		this.mScaleX = this.mScaleY = WidgetWeatherClock.scale*0.9f;
		this.mScaleZ = WidgetWeatherClock.scale;
	}
}
