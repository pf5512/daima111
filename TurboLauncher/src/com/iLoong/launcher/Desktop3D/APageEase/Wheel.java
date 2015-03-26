package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.UI3DEngine.View3D;


public class Wheel
{
	
	public static void updateEffect(
			GridView3D cur_view ,
			GridView3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		View3D icon;
		Vector2 pos;
		Vector2 old_pos;
		float this_height = 0;
		float radio;
		float Radius = this_width / 3.0f;
		float Angle;
		float add_angle = 90f;
		float cur_line_add_angle = 0f;
		float xAngle = yScale * 90;
		int cur_size = 0;
		int next_size = 0;
		if( cur_view != null )
		{
			this_height = cur_view.getHeight();
			cur_size = cur_view.getChildCount();
			cur_view.setPosition( degree * this_width , 0 );
		}
		if( next_view != null )
		{
			this_height = next_view.getHeight();
			next_view.setPosition( ( degree + 1 ) * this_width , 0 );
			next_size = next_view.getChildCount();
		}
		if( degree <= 0 && degree > ( -1 / 3f ) )
		{
			radio = degree * ( -3f );
			if( cur_view != null )
			{
				for( int i = 0 ; i < cur_size ; i++ )
				{
					icon = cur_view.getChildAt( i );
					icon.originX = icon.width / 2;
					icon.originY = icon.height / 2 + Radius;
					pos = new Vector2( this_width / 2.0f - icon.width / 2 , this_height / 2.0f - Radius - icon.height / 2 );
					if( icon.getTag() instanceof Vector2 )
						old_pos = (Vector2)icon.getTag();
					else
					{
						old_pos = new Vector2( icon.getX() , icon.getY() );
					}
					icon.setPosition( old_pos.x + ( pos.x - old_pos.x ) * radio , old_pos.y + ( pos.y - old_pos.y ) * radio );
					Angle = ( add_angle + i * 360 / cur_size ) % 360;
					if( Angle > 180 && ( i % cur_view.getCellCountX() == 0 ) )
						cur_line_add_angle = -360f;
					Angle += cur_line_add_angle;
					icon.setOrigin( icon.getWidth() / 2 , icon.getHeight() / 2 + Radius * radio );
					icon.setRotationZ( Angle * radio );
				}
			}
			if( next_view != null )
			{
				for( int i = 0 ; i < next_size ; i++ )
				{
					icon = next_view.getChildAt( i );
					icon.originX = icon.width / 2;
					icon.originY = icon.height / 2 + Radius;
					pos = new Vector2( this_width / 2.0f - icon.width / 2 , this_height / 2.0f - Radius - icon.height / 2 );
					if( icon.getTag() instanceof Vector2 )
						old_pos = (Vector2)icon.getTag();
					else
					{
						old_pos = new Vector2( icon.getX() , icon.getY() );
					}
					icon.setPosition( old_pos.x + ( pos.x - old_pos.x ) * radio , old_pos.y + ( pos.y - old_pos.y ) * radio );
					Angle = ( i * 360 / next_size ) % 360;
					if( Angle > 180 && ( i % next_view.getCellCountX() == 0 ) )
						cur_line_add_angle = -360f;
					Angle += cur_line_add_angle;
					icon.setOrigin( icon.getWidth() / 2 , icon.getHeight() / 2 + Radius * radio );
					icon.setRotationZ( Angle * radio );
				}
			}
		}
		else if( degree > ( -2 / 3f ) )
		{
			radio = ( 1 / 3f + degree ) * ( -3 );
			if( cur_view != null )
			{
				for( int i = 0 ; i < cur_size ; i++ )
				{
					icon = cur_view.getChildAt( i );
					Angle = ( i * 360 / cur_size ) % 360;
					if( Angle > 180 && ( i % cur_view.getCellCountX() == 0 ) )
						cur_line_add_angle = -360f;
					Angle += cur_line_add_angle;
					icon.setRotationZ( ( add_angle + Angle ) + add_angle * radio );
				}
			}
			if( next_view != null )
			{
				for( int i = 0 ; i < next_size ; i++ )
				{
					icon = next_view.getChildAt( i );
					Angle = ( i * 360 / next_size ) % 360;
					if( Angle > 180 && ( i % next_view.getCellCountX() == 0 ) )
						cur_line_add_angle = -360f;
					Angle += cur_line_add_angle;
					icon.setRotationZ( Angle + add_angle * radio );
				}
			}
		}
		else
		{
			radio = ( degree + 1 ) * ( 3f );
			if( cur_view != null )
			{
				for( int i = 0 ; i < cur_size ; i++ )
				{
					icon = cur_view.getChildAt( i );
					icon.originX = icon.width / 2;
					icon.originY = icon.height / 2 + Radius;
					pos = new Vector2( this_width / 2.0f - icon.width / 2 , this_height / 2.0f - Radius - icon.height / 2 );
					if( icon.getTag() instanceof Vector2 )
						old_pos = (Vector2)icon.getTag();
					else
					{
						old_pos = new Vector2( icon.getX() , icon.getY() );
					}
					icon.setPosition( old_pos.x + ( pos.x - old_pos.x ) * radio , old_pos.y + ( pos.y - old_pos.y ) * radio );
					Angle = ( add_angle * 2 + i * 360 / cur_size ) % 360;
					if( Angle > 180 && ( i % cur_view.getCellCountX() == 0 ) )
						cur_line_add_angle = -360f;
					Angle += cur_line_add_angle;
					icon.setOrigin( icon.getWidth() / 2 , icon.getHeight() / 2 + Radius * radio );
					icon.setRotationZ( Angle * radio );
				}
			}
			if( next_view != null )
			{
				for( int i = 0 ; i < next_size ; i++ )
				{
					icon = next_view.getChildAt( i );
					icon.originX = icon.width / 2;
					icon.originY = icon.height / 2 + Radius;
					pos = new Vector2( this_width / 2.0f - icon.width / 2 , this_height / 2.0f - Radius - icon.height / 2 );
					if( icon.getTag() instanceof Vector2 )
						old_pos = (Vector2)icon.getTag();
					else
					{
						old_pos = new Vector2( icon.getX() , icon.getY() );
					}
					icon.setPosition( old_pos.x + ( pos.x - old_pos.x ) * radio , old_pos.y + ( pos.y - old_pos.y ) * radio );
					Angle = ( add_angle + i * 360 / next_size ) % 360;
					if( Angle > 180 && ( i % next_view.getCellCountX() == 0 ) )
						cur_line_add_angle = -360f;
					Angle += cur_line_add_angle;
					icon.setOrigin( icon.getWidth() / 2 , icon.getHeight() / 2 + Radius * radio );
					icon.setRotationZ( Angle * radio );
				}
			}
		}
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
