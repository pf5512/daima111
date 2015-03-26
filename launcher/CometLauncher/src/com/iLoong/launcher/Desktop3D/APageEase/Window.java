package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Window
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
		final int cur_countX = ( (GridView3D)cur_view ).getCellCountX();
		final int cur_countY = ( (GridView3D)cur_view ).getCellCountY();
		for( int column = 0 ; column < cur_countX ; column++ )
		{
			for( int row = 0 ; row < cur_countY ; row++ )
			{
				int icon_index = row * cur_countX + column;
				if( icon_index < cur_view.getChildCount() )
				{
					icon = cur_view.getChildAt( icon_index );
					if( column < cur_countX / 2 )
					{
						float origin_posX = -icon.getX();
						icon.setOrigin( origin_posX , icon.originY );
						icon.setRotationY( -1 * Math.abs( degree ) * 90 );
					}
					else
					{
						float origin_posX = this_width - icon.getX();
						icon.setOrigin( origin_posX , icon.originY );
						icon.setRotationY( 1 * Math.abs( degree ) * 90 );
					}
				}
			}
		}
		//next view 放大，淡入
		//		int next_view_size = next_view.getChildCount();
		//		for(int index =0;index<next_view_size;index++)
		//		{
		//			if(index < next_view.getChildCount())
		//			{
		//				View3D nextViewIcon = next_view.getChildAt(index);
		//				Color color = nextViewIcon.getColor();
		//				float scale = Math.abs(degree);
		//				
		//				color.a = Math.abs(degree);
		//				nextViewIcon.setColor(color);
		//				nextViewIcon.setScale(scale, scale);
		//				
		//			}
		//		}		
		Color color = next_view.getColor();
		float scale = Math.abs( degree );
		color.a = Math.abs( degree );
		next_view.setColor( color );
		next_view.setScale( scale , scale );
		if( !DefaultLayout.disable_x_effect )
		{
			cur_view.setRotationX( xAngle );
			next_view.setRotationX( xAngle );
		}
	}
}
