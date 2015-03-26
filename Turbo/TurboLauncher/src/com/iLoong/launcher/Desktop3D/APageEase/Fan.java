package com.iLoong.launcher.Desktop3D.APageEase;


import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Fan
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
			cur_view.setOrigin( cur_view.width / 2 , cur_view.height + cur_view.originX );
			cur_view.setRotationZ( degree * 90 );
		}
		if( next_view != null )
		{
			next_view.setOrigin( next_view.width / 2 , next_view.height + next_view.originX );
			next_view.setRotationZ( ( degree + 1 ) * 90 );
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
