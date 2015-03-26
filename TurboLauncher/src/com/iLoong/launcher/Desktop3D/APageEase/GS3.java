package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class GS3
{
	
	final public static float DEFAULT_ANGLE = -60;
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		Color color;
		if( cur_view != null )
		{
			cur_view.setPosition( degree * this_width , 0 );
			cur_view.originX = cur_view.x;
			cur_view.setRotationY( degree * DEFAULT_ANGLE );
			color = cur_view.getColor();
			color.a = 1f - Math.abs( degree );
			cur_view.setColor( color );
		}
		if( next_view != null )
		{
			next_view.setPosition( ( degree + 1 ) * this_width , 0 );
			next_view.originX = next_view.x + next_view.width;
			next_view.setRotationY( ( degree + 1 ) * DEFAULT_ANGLE );
			color = next_view.getColor();
			color.a = Math.abs( degree );
			next_view.setColor( color );
		}
	}
}
