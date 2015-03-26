package com.iLoong.launcher.Desktop3D.APageEase;


import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Jump
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		float xAngle = yScale * 90;
		float this_height;
		if( cur_view != null )
			this_height = cur_view.getHeight();
		else
			this_height = next_view.getHeight();
		if( cur_view != null )
		{
			cur_view.setPosition( degree * this_width , -degree * this_height );
		}
		if( next_view != null )
		{
			next_view.setPosition( ( degree + 1 ) * this_width , ( degree + 1 ) * this_height );
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
