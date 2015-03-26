package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Ball
{
	
	private static void build_ball(
			View3D icon ,
			float rotaDegX ,
			float rotaDeg ,
			float ratio ,
			float this_width ,
			float colora ,
			float this_height )
	{
		Color color;
		Vector2 old_pos;
		if( icon.getTag() instanceof Vector2 )
			old_pos = (Vector2)icon.getTag();
		else
		{
			old_pos = new Vector2( icon.getX() , icon.getY() );
		}
		icon.setPosition( old_pos.x + ( this_width / 2 - old_pos.x - icon.originX ) * ratio , old_pos.y + ( this_height / 2 - old_pos.y - icon.originY ) * ratio );
		color = icon.getColor();
		color.a = colora;
		icon.setColor( color );
		icon.setRotationAngle( rotaDegX * ratio , rotaDeg * ratio , 0 );
	}
	
	private static void destroy_ball(
			View3D icon ,
			float rotaDegX ,
			float rotaDeg ,
			float ratio ,
			float this_width ,
			float colora ,
			float this_height )
	{
		Color color;
		Vector2 old_pos;
		if( icon.getTag() instanceof Vector2 )
			old_pos = (Vector2)icon.getTag();
		else
		{
			old_pos = new Vector2( icon.getX() , icon.getY() );
		}
		icon.setPosition( old_pos.x + ( this_width / 2 - old_pos.x - icon.originX ) * ratio , old_pos.y + ( this_height / 2 - old_pos.y - icon.originY ) * ratio );
		icon.setPosition(
				this_width / 2 - icon.originX + ( old_pos.x - ( this_width / 2 - icon.originX ) ) * ratio ,
				this_height / 2 - icon.originY + ( old_pos.y - ( this_height / 2 - icon.originY ) ) * ratio );
		color = icon.getColor();
		color.a = colora;
		icon.setColor( color );
		icon.setRotationAngle( rotaDegX * ( 1 - ratio ) , rotaDeg * ( 1 - ratio ) , 0 );
	}
	
	private static void rotate_ball(
			View3D icon ,
			float degree ,
			float rotaDegX ,
			float rotaDeg ,
			float des_rotaDeg ,
			float ratio ,
			float this_width ,
			float this_height )
	{
		Color color;
		color = icon.getColor();
		float temp = MathUtils.cosDeg( rotaDeg ) * MathUtils.cosDeg( rotaDegX );
		if( temp > 0 )
		{
			color.a = 1;
		}
		else
		{
			float des_temp = MathUtils.cosDeg( des_rotaDeg ) * MathUtils.cosDeg( rotaDegX );
			float des_a = ( 1 + des_temp ) * 0.7f + 0.3f;
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
				color.a = ( 1 + temp ) * 0.7f + 0.3f;
			}
		}
		icon.setColor( color );
		icon.setPosition( this_width / 2 - icon.originX , this_height / 2 - icon.originY );
		icon.setRotationAngle( rotaDegX , rotaDeg , 0 );
	}
	
	public static void updateEffect(
			GridView3D cur_view ,
			GridView3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		View3D icon;
		float xAngle = yScale * 90;
		int cur_countX = 0;
		int cur_countY = 0;
		float this_height = 0;
		if( cur_view != null )
		{
			cur_countX = ( (GridView3D)cur_view ).getCellCountX();
			cur_countY = ( (GridView3D)cur_view ).getCellCountY();
			this_height = cur_view.getHeight();
		}
		if( next_view != null )
		{
			cur_countX = ( (GridView3D)next_view ).getCellCountX();
			cur_countY = ( (GridView3D)next_view ).getCellCountY();
			this_height = next_view.getHeight();
		}
		final float rotaDegConst = 180 / cur_countX;
		float rotaDeg = 0;
		float ratio = 0;
		float rowAngleStart;
		float rowAngleArrayX = 0;
		final float cylinder_radius = this_width / 1.8f;
		if( cur_countY > 4 )
		{
			rowAngleStart = 60;
		}
		else
		{
			rowAngleStart = 50;
		}
		if( cur_view != null )
		{
			cur_view.setOriginZ( -cylinder_radius );
		}
		if( next_view != null )
		{
			next_view.setOriginZ( -cylinder_radius );
		}
		if( degree <= 0 && degree > ( -1 / 8f ) )
		{
			ratio = ( -8f ) * degree;
			if( cur_view != null )
			{
				for( int j = 0 ; j < cur_countY ; j++ )
				{
					for( int i = 0 ; i < cur_countX ; i++ )
					{
						if( i + j * cur_countX < cur_view.getChildCount() )
						{
							icon = cur_view.getChildAt( i + j * cur_countX );
							rotaDeg = ( (float)( 67.5 - ( i % cur_countX ) * rotaDegConst ) );
							rowAngleArrayX = -rowAngleStart + rowAngleStart * 2 / ( cur_countY - 1 ) * j;
							icon.setOriginZ( -cylinder_radius );
							build_ball( icon , rowAngleArrayX , -rotaDeg , ratio , this_width , 1.0f , this_height );
						}
					}
				}
			}
			if( next_view != null )
			{
				for( int j = 0 ; j < cur_countY ; j++ )
				{
					for( int i = 0 ; i < cur_countX ; i++ )
					{
						if( i + j * cur_countX < next_view.getChildCount() )
						{
							icon = next_view.getChildAt( i + j * cur_countX );
							rotaDeg = (float)( 67.5 - ( i % cur_countX ) * rotaDegConst - 180 );
							rowAngleArrayX = -rowAngleStart + rowAngleStart * 2 / ( cur_countY - 1 ) * j;
							icon.setOriginZ( -cylinder_radius );
							build_ball( icon , rowAngleArrayX , -rotaDeg , ratio , this_width , 0 , this_height );
						}
					}
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
					parent.addViewBefore( cur_view , next_view );
			}
			else
			{
				if( cur_view != null && next_view != null )
					parent.addViewBefore( next_view , cur_view );
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
				for( int j = 0 ; j < cur_countY ; j++ )
				{
					for( int i = 0 ; i < cur_countX ; i++ )
					{
						if( i + j * cur_countX < cur_view.getChildCount() )
						{
							icon = cur_view.getChildAt( i + j * cur_countX );
							rotaDeg = ( (float)( 67.5 - ( i % cur_countX ) * rotaDegConst ) + ratio * 180 );
							rowAngleArrayX = -rowAngleStart + rowAngleStart * 2 / ( cur_countY - 1 ) * j;
							icon.setOriginZ( -cylinder_radius );
							float tt = MathUtils.cosDeg( rotaDeg ) * MathUtils.cosDeg( rowAngleArrayX );
							if( tt < 0 )
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
								des_rotaDeg = ( (float)( 67.5 - ( i % cur_countX ) * rotaDegConst ) + des_ratio * 180 );
							}
							rotate_ball( icon , degree , rowAngleArrayX , -rotaDeg , -des_rotaDeg , ratio , this_width , this_height );
						}
					}
				}
			}
			if( next_view != null )
			{
				for( int j = 0 ; j < cur_countY ; j++ )
				{
					for( int i = 0 ; i < cur_countX ; i++ )
					{
						if( i + j * cur_countX < next_view.getChildCount() )
						{
							icon = next_view.getChildAt( i + j * cur_countX );
							rotaDeg = (float)( 67.5 - ( i % cur_countX ) * rotaDegConst - 180 + ratio * 180 );
							rowAngleArrayX = -rowAngleStart + rowAngleStart * 2 / ( cur_countY - 1 ) * j;
							icon.setOriginZ( -cylinder_radius );
							float tt = MathUtils.cosDeg( rotaDeg ) * MathUtils.cosDeg( rowAngleArrayX );
							if( tt < 0 )
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
								des_rotaDeg = (float)( 67.5 - ( i % cur_countX ) * rotaDegConst - 180 + des_ratio * 180 );
							}
							rotate_ball( icon , degree , rowAngleArrayX , -rotaDeg , -des_rotaDeg , ratio , this_width , this_height );
						}
					}
				}
			}
		}
		else
		{
			ratio = -8f * degree - 7f;
			if( cur_view != null )
			{
				for( int j = 0 ; j < cur_countY ; j++ )
				{
					for( int i = 0 ; i < cur_countX ; i++ )
					{
						if( i + j * cur_countX < cur_view.getChildCount() )
						{
							icon = cur_view.getChildAt( i + j * cur_countX );
							rotaDeg = ( (float)( 67.5 - ( i % cur_countX ) * rotaDegConst ) - 180 );
							rowAngleArrayX = -rowAngleStart + rowAngleStart * 2 / ( cur_countY - 1 ) * j;
							icon.setOriginZ( -cylinder_radius );
							destroy_ball( icon , rowAngleArrayX , -rotaDeg , ratio , this_width , 0 , this_height );
						}
					}
				}
			}
			if( next_view != null )
			{
				for( int j = 0 ; j < cur_countY ; j++ )
				{
					for( int i = 0 ; i < cur_countX ; i++ )
					{
						if( i + j * cur_countX < next_view.getChildCount() )
						{
							icon = next_view.getChildAt( i + j * cur_countX );
							rotaDeg = (float)( 67.5 - ( i % cur_countX ) * rotaDegConst );
							rowAngleArrayX = -rowAngleStart + rowAngleStart * 2 / ( cur_countY - 1 ) * j;
							icon.setOriginZ( -cylinder_radius );
							destroy_ball( icon , rowAngleArrayX , -rotaDeg , ratio , this_width , 1.0f , this_height );
						}
					}
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
