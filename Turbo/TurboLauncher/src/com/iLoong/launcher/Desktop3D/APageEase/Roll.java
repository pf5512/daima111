package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Roll
{
	
	private static float grid_items_padding_right = -1f;
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width ,
			boolean scroll_right_to_left )
	{
		//		float xAngle = yScale * 90;
		if( scroll_right_to_left == true )
		{
			cur_view.setChildrenDrawOrder( false );
			//cur_view.bringToFront();
			drawRollEffect( cur_view , next_view , degree , true );
		}
		else
		{
			//next_view.bringToFront();
			next_view.setChildrenDrawOrder( true );
			drawRollEffect( next_view , cur_view , 1 + degree , false );
		}
		//    	if (!DefaultLayout.disable_x_effect) {
		//			cur_view.addRotationX(xAngle);
		//			next_view.addRotationX(xAngle);
		//    	}
	}
	
	//
	private static void drawRollEffect(
			ViewGroup3D c_view ,
			ViewGroup3D n_view ,
			float degree ,
			boolean roll_from_right_to_left )
	{
		int CUR_VIEW_COLUMNS = ( (GridView3D)c_view ).getCellCountX();
		int CUR_VIEW_ROWS = ( (GridView3D)c_view ).getCellCountY();
		int NEXT_VIEW_COLUMNS = ( (GridView3D)n_view ).getCellCountX();
		int NEXT_VIEW_ROWS = ( (GridView3D)n_view ).getCellCountY();
		//获取当前View中的第一个item的初始位置
		float view_left_padding = 0f/*((Vector2)(c_view.getChildAt(0).getTag())).x + 2.0f*/;
		float ratio = degree * CUR_VIEW_COLUMNS;
		float ratio_angle = Math.abs( ratio ) * 90;
		int roll_stage_index = 0;
		View3D item;
		//每个item位置直接的空隙,某些项目布局中各个item直接有空隙
		float item_padding_right = 0f;
		if( grid_items_padding_right < 0 )
		{
			if( c_view.getChildCount() > 1 )
			{
				Vector2 icon_0_old_pos;
				Vector2 icon_1_old_pos;
				if( (Vector2)c_view.getChildAt( 0 ).getTag() instanceof Vector2 )
					icon_0_old_pos = (Vector2)c_view.getChildAt( 0 ).getTag();
				else
				{
					icon_0_old_pos = new Vector2( c_view.getChildAt( 0 ).getX() , c_view.getChildAt( 0 ).getY() );
				}
				if( (Vector2)c_view.getChildAt( 1 ).getTag() instanceof Vector2 )
					icon_1_old_pos = (Vector2)c_view.getChildAt( 1 ).getTag();
				else
				{
					icon_1_old_pos = new Vector2( c_view.getChildAt( 1 ).getX() , c_view.getChildAt( 1 ).getY() );
				}
				grid_items_padding_right = ( icon_1_old_pos.x - 1 ) - icon_0_old_pos.x - c_view.getChildAt( 0 ).getWidth();
			}
			else if( n_view.getChildCount() > 1 )
			{
				Vector2 icon_0_old_pos;
				Vector2 icon_1_old_pos;
				if( (Vector2)c_view.getChildAt( 0 ).getTag() instanceof Vector2 )
					icon_0_old_pos = (Vector2)c_view.getChildAt( 0 ).getTag();
				else
				{
					icon_0_old_pos = new Vector2( c_view.getChildAt( 0 ).getX() , c_view.getChildAt( 0 ).getY() );
				}
				if( (Vector2)c_view.getChildAt( 1 ).getTag() instanceof Vector2 )
					icon_1_old_pos = (Vector2)c_view.getChildAt( 1 ).getTag();
				else
				{
					icon_1_old_pos = new Vector2( c_view.getChildAt( 1 ).getX() , c_view.getChildAt( 1 ).getY() );
				}
				grid_items_padding_right = ( icon_1_old_pos.x - 1 ) - icon_0_old_pos.x - n_view.getChildAt( 0 ).getWidth();
			}
			else
			{
				Log.e( "cooee" , "---Roll ---- drawRollEffect ---error--- cannot get available grid_items_padding_right ---" );
			}
		}
		item_padding_right = grid_items_padding_right < 0 ? 0f : grid_items_padding_right;
		if( ratio_angle <= 90 )
		{
			//目前是翻转第一列，
			roll_stage_index = 1;
		}
		else if( ratio_angle > 90 && ratio_angle <= 180 )
		{
			roll_stage_index = 2;
		}
		else if( ratio_angle > 180 && ratio_angle < 270 )
		{
			//three columns
			roll_stage_index = 3;
		}
		else if( ratio_angle > 270 && ratio_angle <= 360 )
		{
			//four
			roll_stage_index = 4;
		}
		//cur_view,
		for( int index = 0 ; index < roll_stage_index ; index++ )
		{
			for( int c_row = 0 ; c_row < CUR_VIEW_ROWS ; c_row++ )
			{
				int c_column = 0;
				if( false == roll_from_right_to_left )
				{
					c_column = index;
					if( ( c_column == CUR_VIEW_COLUMNS - 1 ) || ( c_column == 0 && roll_stage_index == 4 ) )
					{
						Vector2 icon_0_old_pos;
						if( (Vector2)c_view.getChildAt( 0 ).getTag() instanceof Vector2 )
							icon_0_old_pos = (Vector2)c_view.getChildAt( 0 ).getTag();
						else
						{
							icon_0_old_pos = new Vector2( c_view.getChildAt( 0 ).getX() , c_view.getChildAt( 0 ).getY() );
						}
						//此为翻转的最后一列 || 第一列最后一次翻转
						view_left_padding = icon_0_old_pos.x + 2.0f;
					}
				}
				else
				{
					c_column = CUR_VIEW_COLUMNS - 1 - index;
					if( ( c_column == 0 ) || ( c_column == CUR_VIEW_COLUMNS - 1 && roll_stage_index == 4 ) )
					{
						Vector2 icon_0_old_pos;
						if( (Vector2)c_view.getChildAt( 0 ).getTag() instanceof Vector2 )
							icon_0_old_pos = (Vector2)c_view.getChildAt( 0 ).getTag();
						else
						{
							icon_0_old_pos = new Vector2( c_view.getChildAt( 0 ).getX() , c_view.getChildAt( 0 ).getY() );
						}
						//此为翻转的最后一列 || 第一列最后一次翻转
						view_left_padding = icon_0_old_pos.x + 2.0f;
					}
				}
				int item_index = c_row * CUR_VIEW_COLUMNS + c_column;
				if( item_index < c_view.getChildCount() )
				{
					item = c_view.getChildAt( item_index );
					rollActionStage( item , roll_stage_index - index , Math.abs( ratio ) - index , view_left_padding , item_padding_right , roll_from_right_to_left );
				}
			}
		}
		//these columns items keep the original status
		for( int keepIdx = roll_stage_index ; keepIdx < CUR_VIEW_COLUMNS ; keepIdx++ )
		{
			for( int c_row = 0 ; c_row < CUR_VIEW_ROWS ; c_row++ )
			{
				int c_column = 0;
				if( false == roll_from_right_to_left )
				{
					c_column = roll_stage_index;
				}
				else
				{
					c_column = CUR_VIEW_COLUMNS - 1 - roll_stage_index;
				}
				int item_index = c_row * CUR_VIEW_COLUMNS + c_column;
				if( item_index < c_view.getChildCount() )
				{
					item = c_view.getChildAt( item_index );
					Vector2 old_pos;
					if( item.getTag() instanceof Vector2 )
						old_pos = (Vector2)item.getTag();
					else
					{
						old_pos = new Vector2( item.getX() , item.getY() );
					}
					item.setPosition( old_pos.x , old_pos.y );
					item.setOrigin( item.width / 2f , item.height / 2f );
					item.setOriginZ( 0f );
					item.setRotationAngle( 0f , 0f , 0f );
				}
			}
		}
		//next_view
		for( int c_column = 0 ; c_column < NEXT_VIEW_COLUMNS ; c_column++ )
		{
			for( int n_row = 0 ; n_row < NEXT_VIEW_ROWS ; n_row++ )
			{
				int item_index = n_row * NEXT_VIEW_COLUMNS + c_column;
				if( item_index < n_view.getChildCount() )
				{
					item = n_view.getChildAt( item_index );
					Color color = item.getColor();
					if( true == roll_from_right_to_left )
					{
						//c_column = NEXT_VIEW_COLUMNS -1 -index;
						if( c_column > NEXT_VIEW_COLUMNS - roll_stage_index )
						{
							color.a = 1.0f;
						}
						else if( c_column == NEXT_VIEW_COLUMNS - roll_stage_index )
						{
							float color_a = (float)( Math.abs( ratio ) - ( roll_stage_index - 1 ) );
							color.a = ( color_a > 0.5f ) ? 2 * ( color_a - 0.5f ) : 0f;
						}
						else
						{
							color.a = 0f;
						}
					}
					else
					{
						if( c_column < roll_stage_index - 1 )
						{
							color.a = 1.0f;
						}
						else if( c_column == roll_stage_index - 1 )
						{
							float color_a = (float)( Math.abs( ratio ) - ( roll_stage_index - 1 ) );
							color.a = ( color_a > 0.5f ) ? 2 * ( color_a - 0.5f ) : 0f;
							//color.a = (float)(Math.abs(ratio) -(roll_stage_index - 1));
						}
						else
						{
							color.a = 0f;
						}
					}
					item.setColor( color );
				}
			}
		}
	}
	
	private static void rollActionStage(
			View3D item ,
			int stage_index ,
			float cur_ratio ,
			float view_left_padding ,
			float item_padding_right ,
			boolean roll_from_right_to_left )
	{
		Vector2 old_pos;
		float cur_ratio_angle = Math.abs( cur_ratio ) * 90;
		//float view_left_padding = 10.0f;
		int coefficient = ( roll_from_right_to_left == true ) ? 1 : -1;
		if( item.getTag() instanceof Vector2 )
			old_pos = (Vector2)item.getTag();
		else
		{
			old_pos = new Vector2( item.getX() , item.getY() );
		}
		switch( stage_index )
		{
			case 1:
				item.setPosition( old_pos.x , old_pos.y );
				item.setOriginZ( 0f );
				item.setOrigin( item.getWidth() / 2 - coefficient * ( ( item.getWidth() ) / 2 + view_left_padding ) , item.originY );
				item.setRotationY( -coefficient * cur_ratio_angle );
				break;
			case 2:
				item.setPosition( old_pos.x - coefficient * ( item.width - 2 + ( item_padding_right ) * Math.abs( cur_ratio ) ) , item.y );
				item.setOriginZ( item.getWidth() + item_padding_right * Math.abs( cur_ratio ) );
				item.setRotationY( -coefficient * cur_ratio_angle );
				break;
			case 3:
				//item.bringToFront();
				item.setPosition( old_pos.x - coefficient * ( Math.abs( cur_ratio ) - 1f ) * ( item.getWidth() + item_padding_right + 2 ) , item.y );
				item.setOriginZ( item.getWidth() + item_padding_right * Math.abs( cur_ratio ) );
				item.setRotationY( -coefficient * cur_ratio_angle );
				break;
			case 4:
				item.setPosition( old_pos.x - coefficient * 4 * ( item.getWidth() + item_padding_right + view_left_padding ) , item.y );
				item.setOriginZ( 0f );
				item.setRotationY( -coefficient * cur_ratio_angle );
				break;
		}
	}
}
