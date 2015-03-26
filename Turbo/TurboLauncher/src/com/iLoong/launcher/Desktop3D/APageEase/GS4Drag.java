package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class GS4Drag
{
	
	final public static float DEFAULT_ANGLE = -60;
	final public static float DEFAULT_SCALE = 0.69f;
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		Color color;
		float distance = this_width * ( 1.0f - DEFAULT_SCALE );
		if( cur_view != null )
		{
			cur_view.setPosition( degree * ( this_width + distance ) , 0 );
			cur_view.originX = 0;
			cur_view.setRotationY( degree * DEFAULT_ANGLE );
			color = cur_view.getColor();
			color.a = 1.0f;
			cur_view.setColor( color );
		}
		if( next_view != null )
		{
			next_view.setPosition( ( degree + 1 ) * ( this_width + distance ) , 0 );
			next_view.originX = next_view.width;
			next_view.setRotationY( ( degree + 1 ) * DEFAULT_ANGLE );
			color = next_view.getColor();
			color.a = 1.0f;
			next_view.setColor( color );
		}
	}
}
