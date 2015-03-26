package com.iLoong.launcher.Desktop3D.APageEase;


import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Tilt
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float this_width )
	{
		cur_view.setPosition( degree * this_width , 0 );
		next_view.setPosition( ( degree + 1 ) * this_width , 0 );
	}
}
