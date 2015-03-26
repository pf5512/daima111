package com.iLoong.launcher.Desktop3D.APageEase;


import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


// 整体按图标反转
public class Blind
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		View3D icon;
		float xAngle = yScale * 90;
		int cur_size = 0;
		int next_size = 0;
		if( degree <= 0 && degree >= -1 / 2f )
		{
			if( cur_view != null )
			{
				cur_view.show();
				cur_size = cur_view.getChildCount();
			}
			if( next_view != null )
			{
				next_view.hide();
				next_size = next_view.getChildCount();
			}
			for( int i = 0 ; i < cur_size ; i++ )
			{
				if( i < cur_view.getChildCount() )
				{
					icon = cur_view.getChildAt( i );
					icon.setRotationY( 2 * Math.abs( degree ) * 90 );
					if( !DefaultLayout.disable_x_effect )
						icon.addRotationX( xAngle );
				}
			}
			for( int i = 0 ; i < next_size ; i++ )
			{
				if( i < next_view.getChildCount() )
				{
					icon = next_view.getChildAt( i );
					icon.setRotationY( 0 );
				}
			}
		}
		else
		{
			if( cur_view != null )
			{
				cur_view.hide();
				cur_size = cur_view.getChildCount();
			}
			if( next_view != null )
			{
				next_view.show();
				next_size = next_view.getChildCount();
			}
			for( int i = 0 ; i < cur_size ; i++ )
			{
				if( i < cur_view.getChildCount() )
				{
					icon = cur_view.getChildAt( i );
					icon.setRotationY( 0 );
				}
			}
			for( int i = 0 ; i < next_size ; i++ )
			{
				if( i < next_view.getChildCount() )
				{
					icon = next_view.getChildAt( i );
					icon.setRotationY( ( 2 - 2 * Math.abs( degree ) ) * ( -90 ) );
					if( !DefaultLayout.disable_x_effect )
						icon.addRotationX( xAngle );
				}
			}
		}
	}
}
