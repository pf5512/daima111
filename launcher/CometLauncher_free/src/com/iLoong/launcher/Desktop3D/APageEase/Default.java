package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Default
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width ,
			boolean is_standard )
	{
		float xAngle = yScale * 90;
		//		Color color;
		//		
		//		color = cur_view.getColor();
		//		color.a = 1f - Math.abs(degree);
		//		cur_view.setColor(color);
		cur_view.setPosition( degree * this_width , 0 );
		//		color = next_view.getColor();
		//		color.a = Math.abs(degree);
		//		next_view.setColor(color);		
		next_view.setPosition( ( degree + 1 ) * this_width , 0 );
		if( !DefaultLayout.disable_x_effect && !is_standard )
		{
			cur_view.setRotationX( xAngle );
			next_view.setRotationX( xAngle );
		}
	}
}
