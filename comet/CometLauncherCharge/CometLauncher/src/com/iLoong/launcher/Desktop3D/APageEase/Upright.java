package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Upright
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float this_width )
	{
		Color color;
		cur_view.setPosition( 0 , degree * this_width );
		color = cur_view.getColor();
		color.a = 1 - Math.abs( degree );
		cur_view.setColor( color );
		next_view.setPosition( 0 , ( degree + 1 ) * this_width );
		color = next_view.getColor();
		color.a = Math.abs( degree );
		next_view.setColor( color );
	}
}
