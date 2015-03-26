package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Melt
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		Color color;
		float xAngle = yScale * 90;
		if( degree <= 0 && degree >= -1 / 2f )
		{
			cur_view.show();
			next_view.hide();
			color = cur_view.getColor();
			color.a = 1f - 2f * Math.abs( degree );
			cur_view.setColor( color );
		}
		else
		{
			cur_view.hide();
			next_view.show();
			color = next_view.getColor();
			color.a = 2f * Math.abs( degree ) - 1f;
			next_view.setColor( color );
		}
		if( !DefaultLayout.disable_x_effect )
		{
			cur_view.setRotationX( xAngle );
			next_view.setRotationX( xAngle );
		}
	}
}
