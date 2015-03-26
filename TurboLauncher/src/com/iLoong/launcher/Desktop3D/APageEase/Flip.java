package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Flip
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
		if( cur_view != null )
		{
			cur_view.setPosition( 0 , 0 );
		}
		if( next_view != null )
		{
			next_view.setPosition( 0 , 0 );
		}
		if( degree <= 0 && degree >= -1 / 2f )
		{
			if( cur_view != null )
			{
				cur_view.show();
				cur_view.setRotationY( degree * 180 );
				color = cur_view.getColor();
				color.a = ( 0.5f - Math.abs( degree ) ) * 2;
				cur_view.setColor( color );
			}
			if( next_view != null )
			{
				next_view.hide();
				next_view.setRotationY( ( degree + 1 ) * 180 );
			}
		}
		else
		{
			if( next_view != null )
			{
				next_view.show();
				next_view.setRotationY( ( degree + 1 ) * 180 );
				color = next_view.getColor();
				color.a = ( Math.abs( degree ) - 0.5f ) * 2;
				next_view.setColor( color );
			}
			if( cur_view != null )
			{
				cur_view.hide();
				cur_view.setRotationY( degree * 180 );
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
