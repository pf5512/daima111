package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.UI3DEngine.View3D;


public class Cross
{
	
	public static void updateEffect(
			GridView3D cur_view ,
			GridView3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		float xAngle = yScale * 90;
		View3D icon;
		Vector2 old_pos;
		Color item_color;
		int cur_CountX = 0;
		int next_CountX = 0;
		int cur_size = 0;
		int next_size = 0;
		if( cur_view != null )
		{
			cur_CountX = ( (GridView3D)cur_view ).getCellCountX();
			cur_size = cur_view.getChildCount();
		}
		if( next_view != null )
		{
			next_CountX = ( (GridView3D)next_view ).getCellCountX();
			next_size = next_view.getChildCount();
		}
		if( cur_view != null )
		{
			for( int i = 0 ; i < cur_size ; i++ )
			{
				if( i < cur_view.getChildCount() )
				{
					int item_row_idx = (int)( i - ( i % cur_CountX ) ) / cur_CountX;
					icon = cur_view.getChildAt( i );
					if( icon.getTag() instanceof Vector2 )
						old_pos = (Vector2)icon.getTag();
					else
					{
						old_pos = new Vector2( icon.getX() , icon.getY() );
					}
					item_color = icon.getColor();
					item_color.a = 1.0f - Math.abs( degree );
					icon.setColor( item_color );
					if( item_row_idx % 2 == 0 )
					{
						icon.setPosition( old_pos.x + this_width * Math.abs( degree ) , icon.getY() );
					}
					else
					{
						icon.setPosition( old_pos.x - this_width * Math.abs( degree ) , icon.getY() );
					}
				}
			}
		}
		if( next_view != null )
		{
			for( int i = 0 ; i < next_size ; i++ )
			{
				if( i < next_view.getChildCount() )
				{
					int item_row_idx = (int)( i - ( i % next_CountX ) ) / next_CountX;
					icon = next_view.getChildAt( i );
					if( icon.getTag() instanceof Vector2 )
						old_pos = (Vector2)icon.getTag();
					else
					{
						old_pos = new Vector2( icon.getX() , icon.getY() );
					}
					item_color = icon.getColor();
					item_color.a = Math.abs( degree );
					icon.setColor( item_color );
					if( item_row_idx % 2 == 0 )
					{
						icon.setPosition( old_pos.x + this_width * Math.abs( degree + 1 ) , icon.getY() );
					}
					else
					{
						icon.setPosition( old_pos.x - this_width * Math.abs( degree + 1 ) , icon.getY() );
					}
				}
			}
		}
		//		cur_view.setPosition(degree*this_width, 0);
		//		next_view.setPosition((degree+1)*this_width, 0);
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
