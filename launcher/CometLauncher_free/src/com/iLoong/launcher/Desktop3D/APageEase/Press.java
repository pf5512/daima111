package com.iLoong.launcher.Desktop3D.APageEase;


import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Press
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		float xAngle = yScale * 90;
		cur_view.setOrigin( cur_view.width , cur_view.height / 2 );
		cur_view.setPosition( degree * this_width , 0 );
		cur_view.setScale( ( 1 + degree ) , 1f );
		next_view.setOrigin( 0 , next_view.height / 2 );
		next_view.setPosition( ( degree + 1 ) * this_width , 0 );
		next_view.setScale( Math.abs( degree ) , 1f );
		if( !DefaultLayout.disable_x_effect )
		{
			cur_view.setRotationX( xAngle );
			next_view.setRotationX( xAngle );
		}
	}
}
