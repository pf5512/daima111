package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Tornado
{
	
	//final public static float DEFAULT_ANGLE = 70;
	final static float DEFAULT_ANGLE = 67.5f;
	
	private static void build_tornado(
			View3D icon ,
			float rotaDegX ,
			float rotaDeg ,
			float ratio ,
			float scale_x ,
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
		icon.setScale( scale_x , 1.0f );
		icon.setRotationAngle( rotaDegX * ratio , rotaDeg * ratio , 0 );
		//icon.setRotationY(rotaDeg*ratio);
	}
	
	private static void destroy_tornado(
			View3D icon ,
			float rotaDegX ,
			float rotaDeg ,
			float ratio ,
			float scale_x ,
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
		//icon.setRotationY(rotaDeg*(1-ratio));
		icon.setScale( scale_x , 1.0f );
		icon.setRotationAngle( rotaDegX * ( 1 - ratio ) , rotaDeg * ( 1 - ratio ) , 0 );
	}
	
	private static void rotate_tornado(
			View3D icon ,
			float degree ,
			float rotaDegX ,
			float rotaDeg ,
			float des_rotaDeg ,
			float ratio ,
			float scale_x ,
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
		//icon.setRotationY(rotaDeg);
		icon.setScale( scale_x , 1.0f );
		icon.setRotationAngle( rotaDegX , rotaDeg , 0 );
	}
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		View3D icon;
		int cur_countX = 0;
		int cur_countY = 0;
		if( cur_view != null )
		{
			cur_countX = ( (GridView3D)cur_view ).getCellCountX();
			cur_countY = ( (GridView3D)cur_view ).getCellCountY();
		}
		if( next_view != null )
		{
			cur_countX = ( (GridView3D)next_view ).getCellCountX();
			cur_countY = ( (GridView3D)next_view ).getCellCountY();
		}
		final float rotaDegConst = 180 / cur_countX;
		float rowAngleArrayX = 0;
		float rotaDeg = 0;
		float ratio = 0;
		float rowAngleStart;
		final float top_circle_radius = (float)( this_width ) * 2 / 3;
		final float bottom_circle_radius = (float)top_circle_radius / 5;
		float xAngle = yScale * 90;
		float scaleX = 0f;
		if( cur_countY > 4 )
		{
			rowAngleStart = 30;
		}
		else
		{
			rowAngleStart = 20;
		}
		int cur_size = 0;
		if( cur_view != null )
		{
			cur_size = cur_view.getChildCount();
		}
		//cur_view.setOriginZ(-top_circle_radius);
		int next_size = 0;
		if( next_view != null )
		{
			next_size = next_view.getChildCount();
		}
		//next_view.setOriginZ(-top_circle_radius);
		if( degree <= 0 && degree > ( -1 / 8f ) )
		{
			ratio = ( -8f ) * degree;//0~1
			if( cur_view != null )
			{
				for( int i = 0 ; i < cur_size ; i++ )
				{
					int icon_row_num = (int)( i - ( i % cur_countX ) ) / cur_countX;
					float icon_circle_radius = top_circle_radius - ( top_circle_radius - bottom_circle_radius ) * ( icon_row_num ) / ( cur_countY - 1 );
					if( icon_row_num == 0 )
					{
						icon_circle_radius -= bottom_circle_radius;
					}
					icon = cur_view.getChildAt( i );
					rotaDeg = ( (float)( DEFAULT_ANGLE - ( i % cur_countX ) * rotaDegConst ) );//67.5 22.5 -22.5 -67.5
					icon.setOriginZ( -icon_circle_radius );
					rowAngleArrayX = /*-rowAngleStart+*/rowAngleStart / ( cur_countY - 1 ) * icon_row_num;
					scaleX = 1.0f - (float)( icon_row_num ) / ( cur_countY ) * ratio;
					build_tornado( icon , rowAngleArrayX , -rotaDeg , ratio , scaleX , this_width , 1.0f );
				}
			}
			if( next_view != null )
			{
				next_view.hide();
				for( int i = 0 ; i < next_size ; i++ )
				{
					int icon_row_num = (int)( i - ( i % cur_countX ) ) / cur_countX;
					float icon_circle_radius = top_circle_radius - ( top_circle_radius - bottom_circle_radius ) * ( icon_row_num ) / ( cur_countY - 1 );
					if( icon_row_num == 0 )
					{
						icon_circle_radius -= bottom_circle_radius;
					}
					icon = next_view.getChildAt( i );
					rotaDeg = (float)( DEFAULT_ANGLE - ( i % cur_countX ) * rotaDegConst - 180 );
					icon.setOriginZ( -icon_circle_radius );
					rowAngleArrayX = rowAngleStart / ( cur_countY - 1 ) * icon_row_num;
					scaleX = 1.0f - (float)( icon_row_num ) / ( cur_countY ) * ratio;
					build_tornado( icon , rowAngleArrayX , -rotaDeg , ratio , scaleX , this_width , 0 );
				}
			}
		}
		else if( degree <= ( -1 / 8f ) && degree > ( -7 / 8f ) )
		{
			float des_degree = 0;
			float des_rotaDeg = 0;
			float des_ratio = 0;
			ratio = ( -degree - 1 / 8f ) * 8 / 6f;//0~1		
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
					int icon_row_num = (int)( i - ( i % cur_countX ) ) / cur_countX;
					float icon_circle_radius = top_circle_radius - ( top_circle_radius - bottom_circle_radius ) * ( icon_row_num ) / ( cur_countY - 1 );
					if( icon_row_num == 0 )
					{
						icon_circle_radius -= bottom_circle_radius;
					}
					icon = cur_view.getChildAt( i );
					rotaDeg = (float)( DEFAULT_ANGLE - ( i % cur_countX ) * rotaDegConst );
					rotaDeg = rotaDeg + ratio * 180;
					icon.setOriginZ( -icon_circle_radius/*top_circle_radius*icon.getY()/cur_view.getHeight()*/);
					rowAngleArrayX = rowAngleStart / ( cur_countY - 1 ) * icon_row_num;
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
						des_rotaDeg = (float)( DEFAULT_ANGLE - ( i % cur_countX ) * rotaDegConst );
						des_rotaDeg = des_rotaDeg + des_ratio * 180;
					}
					scaleX = 1.0f - (float)( icon_row_num ) / ( cur_countY );
					rotate_tornado( icon , degree , rowAngleArrayX , -rotaDeg , -des_rotaDeg , ratio , scaleX , this_width );
				}
			}
			if( next_view != null )
			{
				for( int i = 0 ; i < next_size ; i++ )
				{
					int icon_row_num = (int)( i - ( i % cur_countX ) ) / cur_countX;
					float icon_circle_radius = top_circle_radius - ( top_circle_radius - bottom_circle_radius ) * ( icon_row_num ) / ( cur_countY - 1 );
					if( icon_row_num == 0 )
					{
						icon_circle_radius -= bottom_circle_radius;
					}
					icon = next_view.getChildAt( i );
					rotaDeg = (float)( DEFAULT_ANGLE - ( i % cur_countX ) * rotaDegConst - 180 );
					rotaDeg = rotaDeg + ratio * 180;
					icon.setOriginZ( -icon_circle_radius/*top_circle_radius*icon.getY()/next_view.getHeight()*/);
					rowAngleArrayX = rowAngleStart / ( cur_countY - 1 ) * icon_row_num;
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
						des_rotaDeg = (float)( DEFAULT_ANGLE - ( i % cur_countX ) * rotaDegConst - 180 );
						des_rotaDeg = des_rotaDeg + des_ratio * 180;
					}
					scaleX = 1.0f - (float)( icon_row_num ) / ( cur_countY );
					rotate_tornado( icon , degree , rowAngleArrayX , -rotaDeg , -des_rotaDeg , ratio , scaleX , this_width );
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
					int icon_row_num = (int)( i - ( i % cur_countX ) ) / cur_countX;
					float icon_circle_radius = top_circle_radius - ( top_circle_radius - bottom_circle_radius ) * ( icon_row_num ) / ( cur_countY - 1 );
					if( icon_row_num == 0 )
					{
						icon_circle_radius -= bottom_circle_radius;
					}
					icon = next_view.getChildAt( i );
					rotaDeg = ( (float)( DEFAULT_ANGLE - ( i % cur_countX ) * rotaDegConst ) );
					icon.setOriginZ( -icon_circle_radius/*top_circle_radius*icon.getY()/next_view.getHeight()*/);
					rowAngleArrayX = rowAngleStart / ( cur_countY - 1 ) * icon_row_num;
					scaleX = 1.0f - (float)( icon_row_num ) / ( cur_countY ) * ( 1 - ratio );
					destroy_tornado( icon , rowAngleArrayX , -rotaDeg , ratio , scaleX , this_width , 1.0f );
				}
			}
			if( cur_view != null )
			{
				for( int i = 0 ; i < cur_size ; i++ )
				{
					int icon_row_num = (int)( i - ( i % cur_countX ) ) / cur_countX;
					float icon_circle_radius = top_circle_radius - ( top_circle_radius - bottom_circle_radius ) * ( icon_row_num ) / ( cur_countY - 1 );
					if( icon_row_num == 0 )
					{
						icon_circle_radius -= bottom_circle_radius;
					}
					icon = cur_view.getChildAt( i );
					rotaDeg = (float)( DEFAULT_ANGLE - ( i % cur_countX ) * rotaDegConst );
					icon.setOriginZ( -icon_circle_radius/*top_circle_radius*icon.getY()/cur_view.getHeight()*/);
					rowAngleArrayX = rowAngleStart / ( cur_countY - 1 ) * icon_row_num;
					scaleX = 1.0f - (float)( icon_row_num ) / ( cur_countY ) * ( 1 - ratio );
					destroy_tornado( icon , rowAngleArrayX , -rotaDeg , ratio , scaleX , this_width , 0 );
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
