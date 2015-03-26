package com.iLoong.launcher.Desktop3D.APageEase;


import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


// 一列一列按图标反转
public class Chord
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float this_width )
	{
		View3D icon;
		int cur_size = cur_view.getChildCount();
		int next_size = next_view.getChildCount();
		if( degree <= 0 && degree >= -1 / 2f )
		{
			cur_view.show();
			next_view.hide();
			for( int i = 0 ; i < cur_size ; i++ )
			{
				if( i < cur_view.getChildCount() )
				{
					icon = cur_view.getChildAt( i );
					icon.setRotationY( Math.abs( degree ) * 180 );
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
			cur_view.hide();
			next_view.show();
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
					icon.setRotationY( Math.abs( degree ) * 180 );
				}
			}
		}
	}
}
