package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Wind
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		View3D icon;
		Vector2 old_pos;
		float xAngle = yScale * 90;
		float move_interval_between_column = 80.0f/*cur_view.getChildAt(0).getX() + cur_view.getChildAt(0).originX*/;
		int is_next_column_following_moving = 0;
		float this_height = cur_view.getHeight();
		new Vector2( this_width / 2.0f , this_height / 2.0f );
		if( degree <= 0 && degree >= -1 / 2f )
		{
			int cur_items_CountX = ( (GridView3D)cur_view ).getCellCountX();
			int cur_items_CountY = ( (GridView3D)cur_view ).getCellCountY();
			next_view.hide();
			cur_view.show();
			for( int column = 0 ; column < cur_items_CountX ; column++ )
			{
				for( int row = 0 ; row < cur_items_CountY ; row++ )
				{
					int item_idx = row * cur_items_CountX + column;
					if( item_idx < cur_view.getChildCount() )
					{
						float ratio = 2 * degree;
						icon = cur_view.getChildAt( item_idx );
						if( icon.getTag() instanceof Vector2 )
							old_pos = (Vector2)icon.getTag();
						else
						{
							old_pos = new Vector2( icon.getX() , icon.getY() );
						}
						if( column == 0 || is_next_column_following_moving == 1 )
						{
							float distance_should_move_x = Math.abs( ratio ) * ( this_width + ( cur_items_CountX - 1 ) * move_interval_between_column ) - column * move_interval_between_column;
							float distance_should_move_y = /*2.0f**/distance_should_move_x * this_width / ( this_height );
							if( old_pos.y >= this_height / 2 )
							{
								icon.setPosition( old_pos.x - distance_should_move_x , /*icon.getY()*/old_pos.y + distance_should_move_y );
							}
							else
							{
								icon.setPosition( old_pos.x - distance_should_move_x , /*icon.getY()*/old_pos.y - distance_should_move_y );
							}
							icon.setRotationZ( -ratio * 720 );
						}
						if( ( row + 1 ) * cur_items_CountX + column >= cur_view.getChildCount() )
						{
							if( Math.abs( old_pos.x - icon.x ) >= move_interval_between_column )
							{
								is_next_column_following_moving = 1;
							}
							else
							{
								is_next_column_following_moving = 0;
								if( column < cur_items_CountX - 1 )
								{
									for( int i = 0 ; i < cur_items_CountY ; i++ )
									{
										int tmp_index = i * cur_items_CountX + column + 1;
										if( tmp_index < cur_view.getChildCount() )
										{
											View3D item = cur_view.getChildAt( tmp_index );
											Vector2 item_old_pos;
											if( icon.getTag() instanceof Vector2 )
												item_old_pos = (Vector2)item.getTag();
											else
											{
												item_old_pos = new Vector2( item.getX() , item.getY() );
											}
											item.setPosition( item_old_pos.x , item_old_pos.y );
											item.setRotationZ( 0f );
										}
									}
								}
							}
						}
					}
				}
			}
		}
		else
		// -1/2 ~ -1
		{
			int next_items_CountX = ( (GridView3D)next_view ).getCellCountX();
			int next_items_CountY = ( (GridView3D)next_view ).getCellCountY();
			cur_view.hide();
			next_view.show();
			for( int column = next_items_CountX - 1 ; column >= 0 ; column-- )
			{
				for( int row = 0 ; row < next_items_CountY ; row++ )
				{
					int item_idx = row * next_items_CountX + column;
					if( item_idx < next_view.getChildCount() )
					{
						float ratio = 2 * Math.abs( 1 + degree ); // 1 --->0
						icon = next_view.getChildAt( item_idx );
						if( icon.getTag() instanceof Vector2 )
							old_pos = (Vector2)icon.getTag();
						else
						{
							old_pos = new Vector2( icon.getX() , icon.getY() );
						}
						if( column == next_items_CountX - 1 || is_next_column_following_moving == 1 )
						{
							float distance_should_move_x = Math.abs( ratio ) * ( this_width + ( next_items_CountX - 1 ) * move_interval_between_column ) - ( next_items_CountX - 1 - column ) * move_interval_between_column;
							float distance_should_move_y = distance_should_move_x * this_width / ( this_height );
							if( old_pos.y >= this_height / 2 )
							{
								icon.setPosition( old_pos.x + distance_should_move_x , old_pos.y + distance_should_move_y );
							}
							else
							{
								icon.setPosition( old_pos.x + distance_should_move_x , old_pos.y - distance_should_move_y );
							}
							icon.setRotationZ( -ratio * 720 );
						}
						if( ( row + 1 ) * next_items_CountX + column >= next_view.getChildCount() )
						{
							if( Math.abs( icon.x - old_pos.x ) >= move_interval_between_column )
							{
								is_next_column_following_moving = 1;
							}
							else
							{
								is_next_column_following_moving = 0;
								if( column > 0 )
								{
									for( int i = 0 ; i < next_items_CountY ; i++ )
									{
										int tmp_index = i * next_items_CountX + column - 1;
										if( tmp_index >= 0 && tmp_index < next_view.getChildCount() )
										{
											View3D item = next_view.getChildAt( tmp_index );
											Vector2 item_old_pos;
											if( icon.getTag() instanceof Vector2 )
												item_old_pos = (Vector2)item.getTag();
											else
											{
												item_old_pos = new Vector2( item.getX() , item.getY() );
											}
											item.setPosition( item_old_pos.x , item_old_pos.y );
											item.setRotationZ( 0f );
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if( !DefaultLayout.disable_x_effect )
		{
			cur_view.setRotationX( xAngle );
			next_view.setRotationX( xAngle );
		}
	}
}
