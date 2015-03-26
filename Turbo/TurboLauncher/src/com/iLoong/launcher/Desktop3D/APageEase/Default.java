package com.iLoong.launcher.Desktop3D.APageEase;


import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Default
{
	
	public static void updateEffect(
			ViewGroup3D pre_view ,
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width ,
			boolean is_standard )
	{
		float xAngle = yScale * 90;
		//		Color color;
		//		color = cur_view.getColor();
		//		color.a = 1f - Math.abs(degree);
		//		cur_view.setColor(color);
		if( cur_view != null )
		{
			cur_view.setPosition( degree * this_width , 0 );
		}
		//		color = next_view.getColor();
		//		color.a = Math.abs(degree);
		//		next_view.setColor(color);
		if( next_view != null )
		{
			next_view.setPosition( ( degree + 1 ) * this_width , 0 );
		}
		if( pre_view != null )
		{
			if( APageEase.is_scroll_anim_in_eidt_mode )
			{
				if( APageEase.is_scroll_from_right_to_left )
				{
					pre_view.setPosition( ( degree + 2 ) * this_width , 0 );
				}
				else
				{
					pre_view.setPosition( ( degree - 1 ) * this_width , 0 );
				}
			}
			else
			{
				if( APageEase.is_scroll_from_right_to_left )
				{
					pre_view.setPosition( ( degree - 1 ) * this_width , 0 );
				}
				else
				{
					pre_view.setPosition( ( degree + 2 ) * this_width , 0 );
				}
			}
		}
		if( !DefaultLayout.disable_x_effect && !is_standard )
		{
			if( cur_view != null )
			{
				cur_view.setRotationX( xAngle );
			}
			if( next_view != null )
			{
				next_view.setRotationX( xAngle );
			}
			if( pre_view != null )
			{
				pre_view.setRotationX( xAngle );
			}
		}
	}
}
