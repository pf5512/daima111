package com.iLoong.launcher.Desktop3D.APageEase;


import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class CylinderDynamic
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width ,
			int pageNumber )
	{
		float xAngle = yScale * 90;
		float yAngle = calcPolygonAngle( pageNumber );
		float radius = calcCylinderRadius( pageNumber );
		cur_view.setOriginZ( radius );
		cur_view.color.a = ( 1f - Math.abs( degree ) );
		cur_view.setRotationY( degree * yAngle );
		next_view.setOriginZ( radius );
		next_view.color.a = Math.abs( degree );
		next_view.setRotationY( ( degree + 1 ) * yAngle );
		if( degree < -1f )
		{
			cur_view.hide();
		}
		else
		{
			cur_view.show();
		}
		if( degree > 0f )
		{
			next_view.hide();
		}
		else
		{
			next_view.show();
		}
		if( !DefaultLayout.disable_x_effect )
		{
			cur_view.addRotationX( xAngle );
			next_view.addRotationX( xAngle );
		}
	}
	
	public static float calcPolygonAngle(
			int pageNum )
	{
		return (float)( 360f / pageNum );
	}
	
	public static float calcCylinderRadius(
			int pageNum )
	{
		if( pageNum == 2 )
		{
			return -Utils3D.getScreenWidth() / 4.5f;
		}
		else if( pageNum == 3 )
		{
			return -Utils3D.getScreenWidth() / 3.2f;
		}
		else if( pageNum == 4 )
		{
			return -Utils3D.getScreenWidth() / 1.88f;
		}
		else if( pageNum == 5 )
		{
			return -Utils3D.getScreenWidth() / 1.38f;
		}
		else if( pageNum == 6 )
		{
			return -Utils3D.getScreenWidth() / 1.1f;
		}
		else if( pageNum == 7 )
		{
			return -Utils3D.getScreenWidth() / 0.92f;
		}
		else if( pageNum == 8 )
		{
			return -Utils3D.getScreenWidth() / 0.79f;
		}
		else if( pageNum == 9 )
		{
			return -Utils3D.getScreenWidth() / 0.696f;
		}
		return 0;
	}
}
