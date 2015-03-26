package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Cylinder
{
	
	private static void build_cylinder(
			View3D icon ,
			float rotaDeg ,
			float ratio ,
			float this_width ,
			float colora )
	{
		Color color;
		Vector2 old_pos;
		if( icon.getTag() instanceof Vector2 )
			old_pos = (Vector2)icon.getTag();
		else
		{
			old_pos = new Vector2( icon.getX() , icon.getY() );
		}
		icon.setPosition( old_pos.x + ( this_width / 2 - old_pos.x - icon.originX ) * ratio , old_pos.y );
		color = icon.getColor();
		color.a = colora;
		icon.setColor( color );
		icon.setRotationY( rotaDeg * ratio );
	}
	
	private static void destroy_cylinder(
			View3D icon ,
			float rotaDeg ,
			float ratio ,
			float this_width ,
			float colora )
	{
		Color color;
		Vector2 old_pos;
		if( icon.getTag() instanceof Vector2 )
			old_pos = (Vector2)icon.getTag();
		else
		{
			old_pos = new Vector2( icon.getX() , icon.getY() );
		}
		icon.setPosition( this_width / 2 - icon.originX + ( old_pos.x - ( this_width / 2 - icon.originX ) ) * ratio , old_pos.y );
		color = icon.getColor();
		color.a = colora;
		icon.setColor( color );
		icon.setRotationY( rotaDeg * ( 1 - ratio ) );
	}
	
	private static void rotate_cylinder(
			View3D icon ,
			float degree ,
			float rotaDeg ,
			float des_rotaDeg ,
			float ratio ,
			float this_width )
	{
		Color color;
		Vector2 old_pos;
		if( icon.getTag() instanceof Vector2 )
			old_pos = (Vector2)icon.getTag();
		else
		{
			old_pos = new Vector2( icon.getX() , icon.getY() );
		}
		color = icon.getColor();
		float temp = -MathUtils.cosDeg( rotaDeg );
		if( temp < 0 )
		{
			color.a = 1;
		}
		else
		{
			float des_temp = -MathUtils.cosDeg( des_rotaDeg );
			float des_a = ( 1 - des_temp ) * 0.7f + 0.3f;
			if( degree > -0.21875 )//-0.125~-0.21875
			{
				color.a = des_a * ( degree - ( -0.125f ) ) / ( -0.21875f - ( -0.125f ) );//0~des_a
			}
			else if( degree < -0.78125f )//-0.875~-0.78125
			{
				color.a = des_a * ( degree - ( -0.875f ) ) / ( -0.78125f - ( -0.875f ) );//0~des_a
			}
			else
			{
				color.a = ( 1 - temp ) * 0.7f + 0.3f;
			}
		}
		icon.setColor( color );
		icon.setPosition( this_width / 2 - icon.originX , old_pos.y );
		icon.setRotationY( rotaDeg );
	}
	
	public static void updateEffect(
			GridView3D cur_view ,
			GridView3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		View3D icon;
		int cur_countX = 0;
		if( cur_view != null )
		{
			cur_countX = ( (GridView3D)cur_view ).getCellCountX();
		}
		if( next_view != null )
		{
			cur_countX = ( (GridView3D)next_view ).getCellCountX();
		}
		final float rotaDegConst = 180 / cur_countX;
		float rotaDeg = 0;
		float ratio = 0;
		final float cylinder_radius = (float)( this_width / 2 );
		float xAngle = yScale * 90;
		int cur_size = 0;
		if( cur_view != null )
		{
			cur_size = cur_view.getChildCount();
			cur_view.setOriginZ( -cylinder_radius );
		}
		int next_size = 0;
		if( next_view != null )
		{
			next_size = next_view.getChildCount();
			next_view.setOriginZ( -cylinder_radius );
		}
		if( degree <= 0 && degree > ( -1 / 8f ) )
		{
			ratio = ( -8f ) * degree;
			if( cur_view != null )
			{
				for( int i = 0 ; i < cur_size ; i++ )
				{
					icon = cur_view.getChildAt( i );
					rotaDeg = ( (float)( 67.5 - ( i % cur_countX ) * rotaDegConst ) );//67.5 22.5 -22.5 -67.5
					icon.setOriginZ( -cylinder_radius );
					build_cylinder( icon , -rotaDeg , ratio , this_width , 1.0f );
				}
			}
			if( next_view != null )
			{
				for( int i = 0 ; i < next_size ; i++ )
				{
					icon = next_view.getChildAt( i );
					rotaDeg = (float)( 67.5 - ( i % cur_countX ) * rotaDegConst - 180 );
					icon.setOriginZ( -cylinder_radius );
					build_cylinder( icon , -rotaDeg , ratio , this_width , 0 );
				}
			}
		}
		else if( degree <= ( -1 / 8f ) && degree > ( -7 / 8f ) )
		{
			float des_degree = 0;
			float des_rotaDeg = 0;
			float des_ratio = 0;
			ratio = ( -degree - 1 / 8f ) * 8 / 6f;
			ViewGroup3D parent = null;
			if( cur_view != null )
			{
				parent = cur_view.getParent();
			}
			if( next_view != null )
			{
				parent = next_view.getParent();
			}
			if( degree <= ( -1 / 8f ) && degree > ( -1 / 2f ) )
			{
				if( cur_view != null && next_view != null )
				{
					if( cur_view.getIndexInParent() < next_view.getIndexInParent() )
						parent.addViewBefore( cur_view , next_view );
				}
			}
			else
			{
				if( cur_view != null && next_view != null )
				{
					if( cur_view.getIndexInParent() > next_view.getIndexInParent() )
						parent.addViewBefore( next_view , cur_view );
				}
			}
			{
				if( cur_view != null )
				{
					cur_view.setChildrenDrawOrder( false );
				}
				if( next_view != null )
				{
					next_view.setChildrenDrawOrder( true );
				}
			}
			if( cur_view != null )
			{
				for( int i = 0 ; i < cur_size ; i++ )
				{
					icon = cur_view.getChildAt( i );
					rotaDeg = (float)( 67.5 - ( i % cur_countX ) * rotaDegConst );
					rotaDeg = rotaDeg + ratio * 180;
					icon.setOriginZ( -cylinder_radius );
					float tt = -MathUtils.cosDeg( -rotaDeg );
					if( tt > 0 )
					{
						if( degree > -0.21875f )//-0.125~-0.21875
						{
							des_degree = -0.21875f;
						}
						else if( degree < -0.78125f )
						{
							des_degree = -0.78125f;
						}
						des_ratio = ( -des_degree - 1 / 8f ) * 8 / 6f;
						des_rotaDeg = (float)( 67.5 - ( i % cur_countX ) * rotaDegConst );
						des_rotaDeg = des_rotaDeg + des_ratio * 180;
					}
					rotate_cylinder( icon , degree , -rotaDeg , -des_rotaDeg , ratio , this_width );
				}
			}
			if( next_view != null )
			{
				for( int i = 0 ; i < next_size ; i++ )
				{
					icon = next_view.getChildAt( i );
					rotaDeg = (float)( 67.5 - ( i % cur_countX ) * rotaDegConst - 180 );
					rotaDeg = rotaDeg + ratio * 180;
					icon.setOriginZ( -cylinder_radius );
					float tt = -MathUtils.cosDeg( -rotaDeg );
					if( tt > 0 )
					{
						if( degree > -0.21875f )//-0.125~-0.21875
						{
							des_degree = -0.21875f;
						}
						else if( degree < -0.78125f )
						{
							des_degree = -0.78125f;
						}
						des_ratio = ( -des_degree - 1 / 8f ) * 8 / 6f;
						des_rotaDeg = (float)( 67.5 - ( i % cur_countX ) * rotaDegConst - 180 );
						des_rotaDeg = des_rotaDeg + des_ratio * 180;
					}
					rotate_cylinder( icon , degree , -rotaDeg , -des_rotaDeg , ratio , this_width );
				}
			}
		}
		else
		{
			ratio = -8f * degree - 7f;
			if( next_view != null )
			{
				for( int i = 0 ; i < next_size ; i++ )
				{
					icon = next_view.getChildAt( i );
					rotaDeg = ( (float)( 67.5 - ( i % cur_countX ) * rotaDegConst ) );
					icon.setOriginZ( -cylinder_radius );
					destroy_cylinder( icon , -rotaDeg , ratio , this_width , 1.0f );
				}
			}
			if( cur_view != null )
			{
				for( int i = 0 ; i < cur_size ; i++ )
				{
					icon = cur_view.getChildAt( i );
					rotaDeg = (float)( 67.5 - ( i % cur_countX ) * rotaDegConst );
					icon.setOriginZ( -cylinder_radius );
					destroy_cylinder( icon , -rotaDeg , ratio , this_width , 0 );
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
