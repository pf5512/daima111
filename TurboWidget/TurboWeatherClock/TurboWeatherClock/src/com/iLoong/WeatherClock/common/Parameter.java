package com.iLoong.WeatherClock.common;

import com.iLoong.WeatherClock.view.WidgetWeatherClock;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;


public class Parameter
{
//	public static float scale=Math.min( Utils3D.getScreenWidth() / 720f , R3D.Workspace_cell_each_height / 240f );
	
//	public static float WIDTH=Utils3D.getScreenWidth();
//	public static float HEIGHT=R3D.Workspace_cell_each_height*2;
	
	
//	public static float MOVE_TO_DOT_WIDTH=-(Utils3D.getScreenWidth()/2-WIDTH/2);
//	public static float MOVE_TO_DOT_HEIGHT=-(R3D.Workspace_cell_each_height-HEIGHT/2);
	
	
	public static float CLOCK_MOVE_X=Utils3D.getScreenWidth()/2;
	public static float CLOCK_MOVE_Y=R3D.Workspace_cell_each_height;
	public static float CLOCK_MOVE_Z=-88.686f*WidgetWeatherClock.scale;
	
	public static float WEATHER_MOVE_X=Utils3D.getScreenWidth()/2;
	public static float WEATHER_MOVE_Y=R3D.Workspace_cell_each_height;
	public static float WEATHER_MOVE_Z=0f;
	
	public static float WEATHER_WIDTH=455f*WidgetWeatherClock.scale;
	public static float WEATHER_HEIGHT=455*WidgetWeatherClock.scale;
	
	public static float WEATHER_CLOCK_ROTATION_Z=-78.0997f*WidgetWeatherClock.scale;
	
	public static float WEATHER_SEMICIRCLE_ROTATION_Z=-83.10295f * WidgetWeatherClock.scale;
	
	public static float WEATHER_ROUND_WIDTH=410f*WidgetWeatherClock.scale;
	public static float WEATHER_ROUND_HEIGHT=205f*WidgetWeatherClock.scale;
	
	public static float WEATHER_ICON_WIDTH=172f*WidgetWeatherClock.scale;
	public static float WEATHER_ICON_HEIGHT=123f*WidgetWeatherClock.scale;
	
	
	public static float WEATHER_NAME_WIDTH=123f*WidgetWeatherClock.scale;
	public static float WEATHER_NAME_HEIGHT=37f*WidgetWeatherClock.scale;
	
	public static float WEATHER_AIR_WIDTH=387f*WidgetWeatherClock.scale;
	public static float WEATHER_AIR_HEIGHT=39f*WidgetWeatherClock.scale;
	
	public static float WEATHER_CITY_WIDTH=123f*WidgetWeatherClock.scale;
	public static float WEATHER_CITY_HEIGHT=37f*WidgetWeatherClock.scale;
	
	public static float WEATHER_DATE_WIDTH=105f*WidgetWeatherClock.scale;
	public static float WEATHER_DATE_HEIGHT=64f*WidgetWeatherClock.scale;
	
	public static float WEATHER_TEM_WIDTH=150f*WidgetWeatherClock.scale;
	public static float WEATHER_TEM_HEIGHT=95f*WidgetWeatherClock.scale;
	
	
	public static float WEATHER_CURVETOP_WIDTH=307f*WidgetWeatherClock.scale;
	public static float WEATHER_CURVETOP_HEIGHT=128f*WidgetWeatherClock.scale;
	
	public static float WEATHER_CURVEBOTTOM_WIDTH=307f*WidgetWeatherClock.scale;
	public static float WEATHER_CURVEBOTTOM_HEIGHT=74f*WidgetWeatherClock.scale;
	
	public static float WEATHER_CURVEICON_WIDTH=287f*WidgetWeatherClock.scale;
	public static float WEATHER_CURVEICON_HEIGHT=33f*WidgetWeatherClock.scale;
	
	public static float WEATHER_CURVEICONSMALL_WIDTH=36f*WidgetWeatherClock.scale;
	public static float WEATHER_CURVEICONSMALL_HEIGHT=26f*WidgetWeatherClock.scale;
	
	
	public static float WEATHER_CURVEDATE_WIDTH=287f*WidgetWeatherClock.scale;
	public static float WEATHER_CURVEDATE_HEIGHT=33f*WidgetWeatherClock.scale;
	
	public static float WEATHER_DOT_WIDTH=25f*WidgetWeatherClock.scale;
	public static float WEATHER_DOT_HEIGHT=24f*WidgetWeatherClock.scale;
	
	public static float WEATHER_DOT_MyHEIGHT=30f*WidgetWeatherClock.scale;
	
	public static float WEATHER_DOTTODAY_WIDTH=23f*WidgetWeatherClock.scale;
	public static float WEATHER_DOTTODAY_HEIGHT=25f*WidgetWeatherClock.scale;
	
	public static float WEATHER_FONT_HEIGHT=35f*WidgetWeatherClock.scale;
	
	
	public static float WEATHER_TMP_WIDTH=48f*WidgetWeatherClock.scale;
	public static float WEATHER_TMP_HEIGHT=84f*WidgetWeatherClock.scale;
	
	
	public static float WEATHER_TOP_WIDTH=414f*WidgetWeatherClock.scale;
	public static float WEATHER_TOP_HEIGHT=414*WidgetWeatherClock.scale;
	public static void changePoition(){
//		scale=Math.min( Utils3D.getScreenWidth() / 720f , R3D.Workspace_cell_each_height / 240f );
		
//		WIDTH=715f*scale;
//		HEIGHT=455f*scale;
		
		CLOCK_MOVE_X=Utils3D.getScreenWidth()/2;
		CLOCK_MOVE_Y=R3D.Workspace_cell_each_height;
		CLOCK_MOVE_Z=-88.686f*WidgetWeatherClock.scale;
		
		WEATHER_MOVE_X=Utils3D.getScreenWidth()/2;
		WEATHER_MOVE_Y=R3D.Workspace_cell_each_height;
		WEATHER_MOVE_Z=0f;
		
		WEATHER_WIDTH=455f*WidgetWeatherClock.scale;
		WEATHER_HEIGHT=455*WidgetWeatherClock.scale;
		
		WEATHER_CLOCK_ROTATION_Z=-78.0997f*WidgetWeatherClock.scale;
		
		WEATHER_SEMICIRCLE_ROTATION_Z=-83.10295f * WidgetWeatherClock.scale;
		
		WEATHER_ROUND_WIDTH=410f*WidgetWeatherClock.scale;
		WEATHER_ROUND_HEIGHT=205f*WidgetWeatherClock.scale;
		
		WEATHER_ICON_WIDTH=172f*WidgetWeatherClock.scale;
		WEATHER_ICON_HEIGHT=123f*WidgetWeatherClock.scale;
		
		
		WEATHER_NAME_WIDTH=123f*WidgetWeatherClock.scale;
		WEATHER_NAME_HEIGHT=37f*WidgetWeatherClock.scale;
		
		WEATHER_AIR_WIDTH=387f*WidgetWeatherClock.scale;
		WEATHER_AIR_HEIGHT=39f*WidgetWeatherClock.scale;
		
		WEATHER_CITY_WIDTH=123f*WidgetWeatherClock.scale;
		WEATHER_CITY_HEIGHT=37f*WidgetWeatherClock.scale;
		
		WEATHER_DATE_WIDTH=105f*WidgetWeatherClock.scale;
		WEATHER_DATE_HEIGHT=64f*WidgetWeatherClock.scale;
		
		WEATHER_TEM_WIDTH=150f*WidgetWeatherClock.scale;
		WEATHER_TEM_HEIGHT=95f*WidgetWeatherClock.scale;
		
		
		WEATHER_CURVETOP_WIDTH=307f*WidgetWeatherClock.scale;
		WEATHER_CURVETOP_HEIGHT=128f*WidgetWeatherClock.scale;
		
		WEATHER_CURVEBOTTOM_WIDTH=307f*WidgetWeatherClock.scale;
		WEATHER_CURVEBOTTOM_HEIGHT=74f*WidgetWeatherClock.scale;
		
		WEATHER_CURVEICON_WIDTH=287f*WidgetWeatherClock.scale;
		WEATHER_CURVEICON_HEIGHT=33f*WidgetWeatherClock.scale;
		
		WEATHER_CURVEICONSMALL_WIDTH=36f*WidgetWeatherClock.scale;
		WEATHER_CURVEICONSMALL_HEIGHT=26f*WidgetWeatherClock.scale;
		
		
		WEATHER_CURVEDATE_WIDTH=287f*WidgetWeatherClock.scale;
		WEATHER_CURVEDATE_HEIGHT=33f*WidgetWeatherClock.scale;
		
		WEATHER_DOT_WIDTH=25f*WidgetWeatherClock.scale;
		WEATHER_DOT_HEIGHT=24f*WidgetWeatherClock.scale;
		
		WEATHER_DOT_MyHEIGHT=30f*WidgetWeatherClock.scale;
		
		WEATHER_DOTTODAY_WIDTH=23f*WidgetWeatherClock.scale;
		WEATHER_DOTTODAY_HEIGHT=25f*WidgetWeatherClock.scale;
		
		WEATHER_FONT_HEIGHT=35f*WidgetWeatherClock.scale;
		
		
		WEATHER_TMP_WIDTH=48f*WidgetWeatherClock.scale;
		WEATHER_TMP_HEIGHT=84f*WidgetWeatherClock.scale;
		
		
		WEATHER_TOP_WIDTH=414f*WidgetWeatherClock.scale;
		WEATHER_TOP_HEIGHT=414*WidgetWeatherClock.scale;
	}
}
