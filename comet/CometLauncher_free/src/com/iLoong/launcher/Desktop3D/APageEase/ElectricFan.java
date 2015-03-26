package com.iLoong.launcher.Desktop3D.APageEase;


import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class ElectricFan
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		float xAngle = yScale * 90;
		cur_view.setPosition( ( degree ) * this_width , 0 );
		cur_view.setRotationZ( -degree * 360 % 360 );
		next_view.setPosition( ( degree + 1 ) * this_width , 0 );
		next_view.setRotationZ( -degree * 360 % 360 );
		if( !DefaultLayout.disable_x_effect )
		{
			cur_view.addRotationX( xAngle );
			next_view.addRotationX( xAngle );
		}
	}
}
