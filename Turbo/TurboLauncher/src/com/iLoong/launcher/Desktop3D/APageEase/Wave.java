package com.iLoong.launcher.Desktop3D.APageEase;


import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Wave
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		float xAngle = yScale * 90;
		if( cur_view != null )
		{
			cur_view.setOrigin( cur_view.width , cur_view.height / 2 );
			cur_view.setPosition( degree * this_width , 0 );
			cur_view.setScale( ( 1 - Math.abs( degree ) ) * 0.8f + 0.2f , ( 1 - Math.abs( degree ) ) * 0.8f + 0.2f );
		}
		if( next_view != null )
		{
			next_view.setOrigin( 0 , next_view.height / 2 );
			next_view.setPosition( ( degree + 1 ) * this_width , 0 );
			next_view.setScale( Math.abs( degree ) * 0.8f + 0.2f , Math.abs( degree ) * 0.8f + 0.2f );
		}
		if( !DefaultLayout.disable_x_effect )
		{
			if( cur_view != null )
			{
				cur_view.setRotationX( xAngle );
			}
			if( next_view != null )
			{
				next_view.setRotationX( xAngle );
			}
		}
	}
}
