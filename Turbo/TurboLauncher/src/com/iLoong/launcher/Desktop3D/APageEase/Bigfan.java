package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Bigfan
{
	
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
			cur_view.setRotationY( degree * 90 );
			color = cur_view.getColor();
			color.a = 1f - Math.abs( degree );
			cur_view.setColor( color );
		}
		if( next_view != null )
		{
			next_view.setRotationY( ( degree + 1 ) * 90 );
			color = next_view.getColor();
			color.a = Math.abs( degree );
			next_view.setColor( color );
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
