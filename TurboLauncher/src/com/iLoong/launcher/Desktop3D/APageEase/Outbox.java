package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Outbox
{
	
	final public static float DEFAULT_ANGLE = 90;
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		float xAngle = yScale * 90;
		Color color;
		if( cur_view != null )
		{
			color = cur_view.getColor();
			color.a = 1 - Math.abs( degree );
			cur_view.setColor( color );
			cur_view.originX = cur_view.width;
			cur_view.setPosition( degree * this_width , 0 );
			cur_view.setRotationY( degree * DEFAULT_ANGLE );
			if( degree < -0.9f )
			{
				cur_view.hide();
			}
			else
			{
				cur_view.show();
			}
		}
		if( next_view != null )
		{
			color = next_view.getColor();
			color.a = Math.abs( degree );
			next_view.setColor( color );
			next_view.originX = 0;
			next_view.setPosition( ( degree + 1 ) * this_width , 0 );
			next_view.setRotationY( ( degree + 1 ) * DEFAULT_ANGLE );
			if( degree > -0.1f )
			{
				next_view.hide();
			}
			else
			{
				next_view.show();
			}
		}
		if( !DefaultLayout.disable_x_effect )
		{
			if( cur_view != null )
			{
				cur_view.addRotationX( xAngle );
			}
			if( next_view != null )
			{
				next_view.addRotationX( xAngle );
			}
		}
	}
}
