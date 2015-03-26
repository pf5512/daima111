package com.iLoong.launcher.Desktop3D.APageEase;


import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Hump
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width ,
			boolean is_right_to_left )
	{
		float xAngle = yScale * 90;
		float this_height = cur_view.getHeight();
		( (GridView3D)cur_view ).getCellCountX();
		( (GridView3D)cur_view ).getCellCountY();
		new ArrayList<Integer[]>();
		if( is_right_to_left == true )
		{
			if( degree <= 0 && degree >= -3 / 4f )
			{
				float ratio = -4f * degree / 3f; //0--> 1
				next_view.hide();
				cur_view.show();
				//cur_view gather together
				build_gather_together( cur_view , ratio , this_width , this_height , 1.0f );
				cur_view.setScale( 1.0f + ratio / 2 , 1.0f + ratio / 2 );
			}
			else
			{
				//degree:  -3/4 ~ -1
				float ratio = 4 * ( 1 + degree );//1->0
				Color color = cur_view.getColor();
				color.a = ratio/*>0.1f?ratio-0.1f:0f*/;
				cur_view.setColor( color );
				cur_view.setScale( 2.0f - ratio / 2 , 2.0f - ratio / 2 );
				Color color1 = next_view.getColor();
				color1.a = 1 - ratio;
				next_view.setColor( color1 );
				next_view.setScale( 1.0f - 0.4f * ratio , 1.0f - 0.4f * ratio );
			}
		}
		else
		{
			if( degree <= 0 && degree <= -1 / 4f )
			{
				float ratio = 4 * ( 1 + degree ) / 3; //0--> 1
				cur_view.hide();
				next_view.show();
				//cur_view gather together
				next_view.setScale( 1.0f + ratio / 2 , 1.0f + ratio / 2 );
				build_gather_together( next_view , ratio , this_width , this_height , 1.0f );
			}
			else
			{
				//degree:  -1/4 ~ 0
				float ratio = -4 * degree;//1->0
				Color color = next_view.getColor();
				color.a = ratio;
				next_view.setColor( color );
				next_view.setScale( 2.0f - ratio / 2 , 2.0f - ratio / 2 );
				Color color1 = cur_view.getColor();
				color1.a = 1 - ratio;
				cur_view.setColor( color1 );
				cur_view.setScale( 1.0f - 0.4f * ratio , 1.0f - 0.4f * ratio );
			}
			//next_view.setScale(2.0f+degree,2.0f+degree);
		}
		if( !DefaultLayout.disable_x_effect )
		{
			cur_view.setRotationX( xAngle );
			next_view.setRotationX( xAngle );
		}
	}
	
	private static void build_gather_together(
			ViewGroup3D view ,
			float ratio ,
			float this_width ,
			float this_height ,
			float color_a )
	{
		View3D icon;
		int view_countX = ( (GridView3D)view ).getCellCountX(); //columns
		int view_countY = ( (GridView3D)view ).getCellCountY(); //columns
		float section_ratio = ratio;
		final float rotaDegConst = 90 / view_countX;
		float rotaDeg = 0;
		float rowAngleStart;
		float rowAngleArrayX = 0;
		final float cylinder_radius = this_width / 1.8f;
		if( view_countY > 4 )
		{
			rowAngleStart = 45;
		}
		else
		{
			rowAngleStart = 35;
		}
		for( int j = 0 ; j < view_countY ; j++ )
		{
			for( int i = 0 ; i < view_countX ; i++ )
			{
				if( i + j * view_countX < view.getChildCount() )
				{
					Color color;
					Vector2 old_pos;
					icon = view.getChildAt( i + j * view_countX );
					rotaDeg = ( (float)( 34.75 - ( i % view_countX ) * rotaDegConst ) );
					rowAngleArrayX = -rowAngleStart + rowAngleStart * 2 / ( view_countY - 1 ) * j;
					icon.setOriginZ( -cylinder_radius );
					if( icon.getTag() instanceof Vector2 )
						old_pos = (Vector2)icon.getTag();
					else
					{
						old_pos = new Vector2( icon.getX() , icon.getY() );
					}
					if( Math.abs( ratio ) <= 1.0f / 4.0f )
					{
						section_ratio = 2 * ratio;
					}
					else
					{
						section_ratio = 1.0f / 2.0f + ( ratio - 1.0f / 4.0f ) * 2.0f / 3.0f;
					}
					icon.setPosition( old_pos.x + ( this_width / 2 - old_pos.x - icon.originX ) * section_ratio , old_pos.y + ( this_height / 2 - old_pos.y - icon.originY ) * section_ratio );
					color = icon.getColor();
					color.a = color_a;
					icon.setColor( color );
					icon.setRotationAngle( rowAngleArrayX * section_ratio , -rotaDeg * section_ratio , 0 );
				}
			}
		}
	}
}
