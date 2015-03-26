package com.iLoong.launcher.Desktop3D.APageEase;


import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


// 弹性
public class Elasticity
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float original_degree_touchup ,
			float yScale ,
			float this_width ,
			boolean is_touch_up_effect )
	{
		float xAngle = yScale * 90;
		if( is_touch_up_effect == true )
		{
			cur_view.setPosition( degree * this_width , 0 );
			next_view.setPosition( ( 1 + degree ) * this_width , 0 );
			if( degree <= 0f && degree > -1 / 2f )
			{
				//				cur_view.show();
				//				next_view.hide();
				if( /*degree>-1/2f &&*/degree <= original_degree_touchup / 2 )
				{
					cur_view.setScale( ( 1 + ( original_degree_touchup - degree ) ) , 1f );
				}
				else
				{
					cur_view.setScale( 1 + degree , 1f );
				}
				//cur_view.setPosition(degree*this_width, 0);
				//cur_view.setScale((1+degree),1f);
				next_view.setScale( 1f , 1f );
			}
			else
			{
				//				next_view.show();
				//				cur_view.hide();			
				//				next_view.setPosition((1+ degree)*this_width, 0);	
				//				float tmpScale = Math.abs(degree) + 0.1f;
				//				next_view.setScale(tmpScale>1.0f?1.0f:tmpScale,1f);
				float half_origin_touchup_degree = ( original_degree_touchup - 1 ) / 2;
				if( degree >= half_origin_touchup_degree )
				{
					next_view.setScale( 1 + ( degree - original_degree_touchup ) , 1f );
				}
				else
				{
					next_view.setScale( -degree , 1f );
				}
				cur_view.setScale( 1f , 1f );
			}
		}
		else
		{
			cur_view.setPosition( degree * this_width , 0 );
			next_view.setPosition( ( degree + 1 ) * this_width , 0 );
			//			if (!DefaultLayout.disable_x_effect) {
			//				cur_view.setRotationX(xAngle);
			//				next_view.setRotationX(xAngle);
			//			}
		}
	}
}
