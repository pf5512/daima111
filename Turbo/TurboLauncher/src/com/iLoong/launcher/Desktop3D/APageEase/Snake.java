package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.AppList3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Snake
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		int cur_countX = AppList3D.mCellCountX;
		int cur_countY = AppList3D.mCellCountY;
		int next_countX = AppList3D.mCellCountX;
		int next_countY = AppList3D.mCellCountY;
		if( cur_view != null )
		{
			cur_countX = ( (GridView3D)cur_view ).getCellCountX();
			cur_countY = ( (GridView3D)cur_view ).getCellCountY();
		}
		if( next_view != null )
		{
			next_countX = ( (GridView3D)next_view ).getCellCountX();
			next_countY = ( (GridView3D)next_view ).getCellCountY();
		}
		Vector2 icon_old_pos = new Vector2();
		if( cur_view != null )
		{
			if( (Vector2)cur_view.getChildAt( 0 ).getTag() instanceof Vector2 )
			{
				icon_old_pos = icon_old_pos.set( (Vector2)cur_view.getChildAt( 0 ).getTag() );
			}
			else
			{
				icon_old_pos = icon_old_pos.set( cur_view.getChildAt( 0 ).getX() , cur_view.getChildAt( 0 ).getY() );
			}
		}
		if( next_view != null )
		{
			if( (Vector2)next_view.getChildAt( 0 ).getTag() instanceof Vector2 )
			{
				icon_old_pos = icon_old_pos.set( (Vector2)next_view.getChildAt( 0 ).getTag() );
			}
			else
			{
				icon_old_pos = icon_old_pos.set( next_view.getChildAt( 0 ).getX() , next_view.getChildAt( 0 ).getY() );
			}
		}
		float item_begin_pos_x = icon_old_pos.x;
		float item_begin_pos_y = icon_old_pos.y;
		float item_end_pos_x = 0f;
		float item_end_pos_y = 0f;
		float item_width = 0;
		float item_height = 0;
		float item_padding_x = 0.0f;
		float item_padding_y = 0.0f;
		float CUR_VIEW_GROUP_WIDTH = 0;
		//表示该icon移动到退出界面总共所需要移动的距离
		float icon_total_moving_distance = 0f;
		//先确定 各个icon之间的布局的间距 item_padding_x，item_padding_y
		item_end_pos_x = item_begin_pos_x + ( item_width + item_padding_x ) * ( cur_countX - 1 );
		item_end_pos_y = item_begin_pos_y - ( item_height + item_padding_y ) * ( cur_countY - 1 );
		if( cur_view != null )
		{
			item_width = cur_view.getChildAt( 0 ).getWidth();
			item_height = cur_view.getChildAt( 0 ).getHeight();
			CUR_VIEW_GROUP_WIDTH = cur_view.getWidth();
			if( 1 < cur_view.getChildCount() )
			{
				if( (Vector2)cur_view.getChildAt( 1 ).getTag() instanceof Vector2 )
					icon_old_pos = icon_old_pos.set( (Vector2)cur_view.getChildAt( 1 ).getTag() );
				else
				{
					icon_old_pos = icon_old_pos.set( next_view.getChildAt( 1 ).getX() , next_view.getChildAt( 1 ).getY() );
				}
				item_padding_x = icon_old_pos.x - item_begin_pos_x - item_width;
			}
			if( cur_countX < cur_view.getChildCount() )
			{
				if( (Vector2)cur_view.getChildAt( cur_countX ).getTag() instanceof Vector2 )
					icon_old_pos = icon_old_pos.set( (Vector2)cur_view.getChildAt( cur_countX ).getTag() );
				else
				{
					icon_old_pos = icon_old_pos.set( cur_view.getChildAt( cur_countX ).getX() , cur_view.getChildAt( cur_countX ).getY() );
				}
				item_padding_y = item_begin_pos_y - icon_old_pos.y - item_height;
			}
			if( cur_countX - 1 < cur_view.getChildCount() )
			{
				if( (Vector2)cur_view.getChildAt( cur_countX - 1 ).getTag() instanceof Vector2 )
					icon_old_pos = icon_old_pos.set( (Vector2)cur_view.getChildAt( cur_countX - 1 ).getTag() );
				else
				{
					icon_old_pos = icon_old_pos.set( cur_view.getChildAt( cur_countX - 1 ).getX() , cur_view.getChildAt( cur_countX - 1 ).getY() );
				}
				item_end_pos_x = icon_old_pos.x;
			}
			if( ( cur_countY - 1 ) * cur_countX < cur_view.getChildCount() )
			{
				if( (Vector2)cur_view.getChildAt( ( cur_countY - 1 ) * cur_countX ).getTag() instanceof Vector2 )
					icon_old_pos = icon_old_pos.set( (Vector2)cur_view.getChildAt( ( cur_countY - 1 ) * cur_countX ).getTag() );
				else
				{
					icon_old_pos = icon_old_pos.set( cur_view.getChildAt( ( cur_countY - 1 ) * cur_countX ).getX() , cur_view.getChildAt( ( cur_countY - 1 ) * cur_countX ).getY() );
				}
				item_end_pos_y = icon_old_pos.y;
			}
		}
		if( next_view != null )
		{
			item_width = next_view.getChildAt( 0 ).getWidth();
			item_height = next_view.getChildAt( 0 ).getHeight();
			CUR_VIEW_GROUP_WIDTH = next_view.getWidth();
			if( 1 < next_view.getChildCount() )
			{
				if( (Vector2)next_view.getChildAt( 1 ).getTag() instanceof Vector2 )
					icon_old_pos = icon_old_pos.set( (Vector2)next_view.getChildAt( 1 ).getTag() );
				else
				{
					icon_old_pos = icon_old_pos.set( next_view.getChildAt( 1 ).getX() , next_view.getChildAt( 1 ).getY() );
				}
				item_padding_x = icon_old_pos.x - item_begin_pos_x - item_width;
			}
			if( next_countX < next_view.getChildCount() )
			{
				if( (Vector2)next_view.getChildAt( next_countX ).getTag() instanceof Vector2 )
					icon_old_pos = icon_old_pos.set( (Vector2)next_view.getChildAt( next_countX ).getTag() );
				else
				{
					icon_old_pos = icon_old_pos.set( next_view.getChildAt( next_countX ).getX() , next_view.getChildAt( next_countX ).getY() );
				}
				item_padding_y = item_begin_pos_y - icon_old_pos.y - item_height;
			}
			if( next_countX - 1 < next_view.getChildCount() )
			{
				if( (Vector2)next_view.getChildAt( cur_countX - 1 ).getTag() instanceof Vector2 )
					icon_old_pos = icon_old_pos.set( (Vector2)next_view.getChildAt( cur_countX - 1 ).getTag() );
				else
				{
					icon_old_pos = icon_old_pos.set( next_view.getChildAt( cur_countX - 1 ).getX() , next_view.getChildAt( cur_countX - 1 ).getY() );
				}
				item_end_pos_x = icon_old_pos.x;
			}
			if( ( next_countY - 1 ) * next_countX < next_view.getChildCount() )
			{
				if( (Vector2)next_view.getChildAt( ( next_countY - 1 ) * next_countX ).getTag() instanceof Vector2 )
					icon_old_pos = icon_old_pos.set( (Vector2)next_view.getChildAt( ( next_countY - 1 ) * next_countX ).getTag() );
				else
				{
					icon_old_pos = icon_old_pos.set( next_view.getChildAt( ( next_countY - 1 ) * next_countX ).getX() , next_view.getChildAt( ( next_countY - 1 ) * next_countX ).getY() );
				}
				item_end_pos_y = icon_old_pos.y;
			}
		}
		//		if(item_padding_x < 0)
		//			item_padding_x = 0;
		//		if(item_padding_y < 0)
		//			item_padding_y = 0;		
		if( cur_countY % 2 == 1 )//总行数是奇数还是偶数
			icon_total_moving_distance = ( item_begin_pos_y - item_end_pos_y ) + ( cur_countY ) * ( item_end_pos_x - item_begin_pos_x ) + ( item_begin_pos_x + item_width + 5.0f );
		else
			icon_total_moving_distance = ( item_begin_pos_y - item_end_pos_y ) + ( cur_countY ) * ( item_end_pos_x - item_begin_pos_x ) + ( CUR_VIEW_GROUP_WIDTH - item_end_pos_x + 5.0f );
		SnakeEffectMovingRight2Left( cur_view , degree , item_begin_pos_x , item_begin_pos_y , item_end_pos_x , item_end_pos_y , item_padding_x , item_padding_y , icon_total_moving_distance );
		SnakeEffectMovingLeft2Right( next_view , 1 + degree , item_begin_pos_x , item_begin_pos_y , item_end_pos_x , item_end_pos_y , item_padding_x , item_padding_y , icon_total_moving_distance );
		//    	if (!DefaultLayout.disable_x_effect) {
		//			cur_view.setRotationX(xAngle);
		//			next_view.setRotationX(xAngle);
		//    	}
	}
	
	private static void SnakeEffectMovingRight2Left(
			ViewGroup3D cur_view ,
			float degree ,
			float item_begin_pos_x ,
			float item_begin_pos_y ,
			float item_end_pos_x ,
			float item_end_pos_y ,
			float item_padding_x ,
			float item_padding_y ,
			float icon_total_moving_distance )
	{
		if( cur_view == null )
		{
			return;
		}
		View3D icon;
		Vector2 old_pos;
		final int cur_countX = ( (GridView3D)cur_view ).getCellCountX();
		final int cur_countY = ( (GridView3D)cur_view ).getCellCountY();
		//表示该icon从最初的位置到目前总共所移动了的距离
		float icon_has_moved_distance = 0f;
		//表示该icon目前这次所需要移动的距离
		float icon_moving_dis = 0f;
		float item_height = 0f;
		int cur_view_size = cur_view.getChildCount();
		float ratio = /*1.1f * */degree;
		for( int index = 0 ; index < cur_view_size ; index++ )
		{
			if( index < cur_view.getChildCount() )
			{
				int original_row_num = ( index - index % cur_countX ) / cur_countX;
				icon = cur_view.getChildAt( index );
				item_height = icon.getHeight();
				if( icon.getTag() instanceof Vector2 )
					old_pos = (Vector2)icon.getTag();
				else
				{
					old_pos = new Vector2( icon.getX() , icon.getY() );
				}
				//表示此ratio下，按照规律icon需要移动如下的位移
				float icon_moving_dis_ex = Math.abs( ratio ) * icon_total_moving_distance;
				//计算移动上述的位移，能将该Icon移动到哪个位置，
				if( original_row_num % 2 == 0 )
				{
					//开始位置是偶数行，从右往左移动，
					float icon_mv_dis_tmp = 0f;
					//					float icon_pos_x = 0f;
					float icon_pos_y = 0f;
					float icon_mv_dis_tmp_old = 0f;
					int accumulate_ratio = 0;
					for( int ix = 0 ; ix < 2 * cur_countY - 1 ; ix++ )
					{
						if( ix % 2 == 0 && ix != 0 )
							accumulate_ratio++;
						if( ix % 4 == 0 )
						{
							//偶数行，从右往左移动，包含第 0，4，8...行
							float calc_end_pos_x = 0f;
							if( ix == 0 )
							{
								accumulate_ratio = 0;
								icon_mv_dis_tmp_old = 0f;
								icon_mv_dis_tmp += old_pos.x - item_begin_pos_x;
								icon_pos_y = old_pos.y;
								calc_end_pos_x = old_pos.x;
							}
							else
							{
								icon_mv_dis_tmp_old = icon_mv_dis_tmp;
								icon_mv_dis_tmp += item_end_pos_x - item_begin_pos_x;
								icon_pos_y = old_pos.y - ( accumulate_ratio ) * ( item_height + item_padding_y );
								calc_end_pos_x = item_end_pos_x;
							}
							if( icon_mv_dis_tmp >= icon_moving_dis_ex )
							{
								//那么此时icon只需要在所在行从右往左移动即可
								icon.setPosition( /*item_begin_pos_x + */calc_end_pos_x - ( icon_moving_dis_ex - icon_mv_dis_tmp_old ) , icon_pos_y );
								//退出该算法
								break;
							}
							else
							{
								//判断此时是否时最后一行
								int cur_row = original_row_num + ix / 2;
								if( cur_row == cur_countY - 1 )
								{
									//此时是最后一行了，继续往左移动出屏幕
									icon.setPosition( /*item_begin_pos_x +*/calc_end_pos_x - ( icon_moving_dis_ex - icon_mv_dis_tmp_old ) , icon_pos_y );
									//退出该算法
									break;
								}
							}
						}
						else if( ix % 4 == 1 )
						{
							//在Y方向上移动，从上往下移动,进入奇数行
							icon_mv_dis_tmp_old = icon_mv_dis_tmp;
							icon_mv_dis_tmp += item_height + item_padding_y;
							if( icon_mv_dis_tmp >= icon_moving_dis_ex )
							{
								icon_pos_y = old_pos.y - ( accumulate_ratio ) * ( item_height + item_padding_y ) - ( icon_moving_dis_ex - icon_mv_dis_tmp_old );
								icon.setPosition( item_begin_pos_x , icon_pos_y );
								break;
							}
							else
							{
								//进入第三阶段，
							}
						}
						else if( ix % 4 == 2 )
						{
							//奇数行，从左到右移动
							icon_mv_dis_tmp_old = icon_mv_dis_tmp;
							icon_pos_y = old_pos.y - ( accumulate_ratio ) * ( item_height + item_padding_y );
							icon_mv_dis_tmp += item_end_pos_x - item_begin_pos_x;
							if( icon_mv_dis_tmp >= icon_moving_dis_ex )
							{
								icon.setPosition( item_begin_pos_x + icon_moving_dis_ex - icon_mv_dis_tmp_old , icon_pos_y );
								break;
							}
							else
							{
								//判断此时是否时最后一行
								int cur_row = original_row_num + ix / 2;
								if( cur_row == cur_countY - 1 )
								{
									//此时是最后一行了，继续往左移动出屏幕
									icon.setPosition( item_begin_pos_x + icon_moving_dis_ex - icon_mv_dis_tmp_old , icon_pos_y );
									//退出该算法
									break;
								}
							}
						}
						else
						{
							//Y方向移动，从上到下,进入偶数行
							icon_mv_dis_tmp_old = icon_mv_dis_tmp;
							icon_mv_dis_tmp += item_height + item_padding_y;
							if( icon_mv_dis_tmp > icon_moving_dis_ex )
							{
								icon_pos_y = old_pos.y - ( accumulate_ratio ) * ( item_height + item_padding_y ) - ( icon_moving_dis_ex - icon_mv_dis_tmp_old );
								icon.setPosition( item_end_pos_x , icon_pos_y );
								break;
							}
							else
							{
								//进入第五阶段，
							}
						}
					}
				}
				else
				{
					//开始位置是奇数行，从左往右移动
					float icon_mv_dis_tmp = 0f;
					float icon_pos_y = 0f;
					float icon_mv_dis_tmp_old = 0f;
					int accumulate_ratio = 0;
					for( int ix = 0 ; ix < 2 * cur_countY - 3 ; ix++ )
					{
						if( ix % 2 == 0 && ix != 0 )
							accumulate_ratio++;
						if( ix % 4 == 0 )
						{
							//奇数行，从左往右移动，包含第1，5，9...行
							float calc_end_pos_x = 0f;
							if( ix == 0 )
							{
								accumulate_ratio = 0;
								icon_mv_dis_tmp += item_end_pos_x - old_pos.x;
								icon_pos_y = old_pos.y;
								calc_end_pos_x = old_pos.x;
							}
							else
							{
								icon_mv_dis_tmp_old = icon_mv_dis_tmp;
								icon_mv_dis_tmp += item_end_pos_x - item_begin_pos_x;
								icon_pos_y = old_pos.y - ( accumulate_ratio ) * ( item_height + item_padding_y );
								calc_end_pos_x = item_begin_pos_x;
							}
							if( icon_mv_dis_tmp >= icon_moving_dis_ex )
							{
								//那么此时icon只需要在所在行从右往左移动即可
								icon.setPosition( calc_end_pos_x + icon_moving_dis_ex - icon_mv_dis_tmp_old , icon_pos_y );
								//退出该算法
								break;
							}
							else
							{
								//判断此时是否时最后一行
								int cur_row = original_row_num + ix / 2;
								if( cur_row == cur_countY - 1 )
								{
									//此时是最后一行了，继续往左移动出屏幕
									icon.setPosition( calc_end_pos_x + icon_moving_dis_ex - icon_mv_dis_tmp_old , icon_pos_y );
									//退出该算法
									break;
								}
							}
						}
						else if( ix % 4 == 1 )
						{
							//在Y方向上移动，从上往下移动,进入偶数行，
							icon_mv_dis_tmp_old = icon_mv_dis_tmp;
							icon_mv_dis_tmp += item_height + item_padding_y;
							if( icon_mv_dis_tmp >= icon_moving_dis_ex )
							{
								icon_pos_y = old_pos.y - ( accumulate_ratio ) * ( item_height + item_padding_y ) - ( icon_moving_dis_ex - icon_mv_dis_tmp_old );
								icon.setPosition( item_end_pos_x , icon_pos_y );
								break;
							}
							else
							{
								//进入第三阶段，
							}
						}
						else if( ix % 4 == 2 )
						{
							//偶数行，从右往左移动，包含第2，6，10...行
							icon_mv_dis_tmp_old = icon_mv_dis_tmp;
							icon_pos_y = old_pos.y - ( accumulate_ratio ) * ( item_height + item_padding_y );
							icon_mv_dis_tmp += item_end_pos_x - item_begin_pos_x;
							if( icon_mv_dis_tmp >= icon_moving_dis_ex )
							{
								icon.setPosition( item_begin_pos_x + item_end_pos_x - ( icon_moving_dis_ex - icon_mv_dis_tmp_old ) , icon_pos_y );
								break;
							}
							else
							{
								//判断此时是否时最后一行
								int cur_row = original_row_num + ix / 2;
								if( cur_row == cur_countY - 1 )
								{
									//此时是最后一行了，继续往左移动出屏幕
									icon.setPosition( item_begin_pos_x + item_end_pos_x - ( icon_moving_dis_ex - icon_mv_dis_tmp_old ) , icon_pos_y );
									//退出该算法
									break;
								}
							}
						}
						else
						//ix%4 == 3
						{
							//Y方向移动，从上到下,进入奇数行
							icon_mv_dis_tmp_old = icon_mv_dis_tmp;
							icon_mv_dis_tmp += item_height + item_padding_y;
							if( icon_mv_dis_tmp > icon_moving_dis_ex )
							{
								icon_pos_y = old_pos.y - ( accumulate_ratio ) * ( item_height + item_padding_y ) - ( icon_moving_dis_ex - icon_mv_dis_tmp_old );
								icon.setPosition( item_begin_pos_x , icon_pos_y );
								break;
							}
							else
							{
								//进入第五阶段，
							}
						}
					}
				}
			}
		}
	}
	
	private static void SnakeEffectMovingLeft2Right(
			ViewGroup3D cur_view ,
			float degree ,
			float item_begin_pos_x ,
			float item_begin_pos_y ,
			float item_end_pos_x ,
			float item_end_pos_y ,
			float item_padding_x ,
			float item_padding_y ,
			float icon_total_moving_distance )
	{
		if( cur_view == null )
		{
			return;
		}
		View3D icon;
		Vector2 old_pos;
		final int cur_countX = ( (GridView3D)cur_view ).getCellCountX();
		final int cur_countY = ( (GridView3D)cur_view ).getCellCountY();
		float item_height = 0f;
		int cur_view_size = cur_view.getChildCount();
		//表示该icon从最初的位置到目前总共所移动了的距离
		float icon_has_moved_distance = 0f;
		//表示该icon目前这次所需要移动的距离
		float icon_moving_dis = 0f;
		float ratio = /*1.1f * */degree;
		for( int index = 0 ; index < cur_view_size ; index++ )
		{
			if( index < cur_view.getChildCount() )
			{
				int original_row_num = ( index - index % cur_countX ) / cur_countX;
				//				int current_row_num = 0;
				icon = cur_view.getChildAt( index );
				item_height = icon.getHeight();
				if( icon.getTag() instanceof Vector2 )
					old_pos = (Vector2)icon.getTag();
				else
				{
					old_pos = new Vector2( icon.getX() , icon.getY() );
				}
				//if(original_row_num%2 == 0)
				{
					//表示此ratio下，按照规律icon需要移动如下的位移
					float icon_moving_dis_ex = Math.abs( ratio ) * icon_total_moving_distance;
					//计算移动上述的位移，能将该Icon移动到哪个位置，
					if( original_row_num % 2 == 0 )
					{
						//开始位置是偶数行，从左往右移动，
						float icon_mv_dis_tmp = 0f;
						float icon_pos_y = 0f;
						float icon_mv_dis_tmp_old = 0f;
						int accumulate_ratio = 0;
						for( int ix = 0 ; ix < 2 * cur_countY - 1 ; ix++ )
						{
							if( ix % 2 == 0 && ix != 0 )
								accumulate_ratio++;
							if( ix % 4 == 0 )
							{
								//偶数行，从左到右移动，包含
								float calc_end_pos_x = 0f;
								if( ix == 0 )
								{
									accumulate_ratio = 0;
									icon_mv_dis_tmp_old = 0f;
									icon_mv_dis_tmp += item_end_pos_x - old_pos.x;
									icon_pos_y = old_pos.y;
									calc_end_pos_x = old_pos.x;
								}
								else
								{
									icon_mv_dis_tmp_old = icon_mv_dis_tmp;
									icon_mv_dis_tmp += item_end_pos_x - item_begin_pos_x;
									icon_pos_y = old_pos.y + ( accumulate_ratio ) * ( item_height + item_padding_y );
									calc_end_pos_x = item_begin_pos_x;
								}
								if( icon_mv_dis_tmp >= icon_moving_dis_ex )
								{
									//那么此时icon只需要在所在行从右往左移动即可
									icon.setPosition( calc_end_pos_x + ( icon_moving_dis_ex - icon_mv_dis_tmp_old ) , icon_pos_y );
									//退出该算法
									break;
								}
								else
								{
									//判断此时是否时第一行
									int cur_row = original_row_num - ix / 2;
									if( cur_row == 0 )
									{
										//此时是第一行了，继续往右移动出屏幕
										icon.setPosition( calc_end_pos_x + ( icon_moving_dis_ex - icon_mv_dis_tmp_old ) , icon_pos_y );
										//退出该算法
										break;
									}
								}
							}
							else if( ix % 4 == 1 )
							{
								//在Y方向上移动，从下往上移动,进入奇数行
								icon_mv_dis_tmp_old = icon_mv_dis_tmp;
								icon_mv_dis_tmp += item_height + item_padding_y;
								if( icon_mv_dis_tmp >= icon_moving_dis_ex )
								{
									icon_pos_y = old_pos.y + ( accumulate_ratio ) * ( item_height + item_padding_y ) + ( icon_moving_dis_ex - icon_mv_dis_tmp_old );
									icon.setPosition( item_end_pos_x , icon_pos_y );
									break;
								}
							}
							else if( ix % 4 == 2 )
							{
								//奇数行，从右往左移动
								icon_mv_dis_tmp_old = icon_mv_dis_tmp;
								icon_pos_y = old_pos.y + ( accumulate_ratio ) * ( item_height + item_padding_y );
								icon_mv_dis_tmp += item_end_pos_x - item_begin_pos_x;
								if( icon_mv_dis_tmp >= icon_moving_dis_ex )
								{
									icon.setPosition( item_begin_pos_x + item_end_pos_x - ( icon_moving_dis_ex - icon_mv_dis_tmp_old ) , icon_pos_y );
									break;
								}
								else
								{
									//此处无需判断，因为是奇数行 ！=0
								}
							}
							else
							{
								//Y方向移动，从下往上,进入偶数行
								icon_mv_dis_tmp_old = icon_mv_dis_tmp;
								icon_mv_dis_tmp += item_height + item_padding_y;
								if( icon_mv_dis_tmp > icon_moving_dis_ex )
								{
									icon_pos_y = old_pos.y + ( accumulate_ratio ) * ( item_height + item_padding_y ) + ( icon_moving_dis_ex - icon_mv_dis_tmp_old );
									icon.setPosition( item_begin_pos_x , icon_pos_y );
									break;
								}
							}
						}
					}
					else
					{
						//开始位置是奇数行，从右往左移动，
						float icon_mv_dis_tmp = 0f;
						float icon_pos_y = 0f;
						float icon_mv_dis_tmp_old = 0f;
						int accumulate_ratio = 0;
						for( int ix = 0 ; ix < 2 * cur_countY - 1 ; ix++ )
						{
							if( ix % 2 == 0 && ix != 0 )
								accumulate_ratio++;
							if( ix % 4 == 0 )
							{
								//奇数行，从右往左移动
								float calc_end_pos_x = 0f;
								if( ix == 0 )
								{
									accumulate_ratio = 0;
									icon_mv_dis_tmp_old = 0f;
									icon_mv_dis_tmp += old_pos.x - item_begin_pos_x;
									icon_pos_y = old_pos.y;
									calc_end_pos_x = old_pos.x;
								}
								else
								{
									icon_mv_dis_tmp_old = icon_mv_dis_tmp;
									icon_mv_dis_tmp += item_end_pos_x - item_begin_pos_x;
									icon_pos_y = old_pos.y + ( accumulate_ratio ) * ( item_height + item_padding_y );
									calc_end_pos_x = item_end_pos_x;
								}
								if( icon_mv_dis_tmp >= icon_moving_dis_ex )
								{
									//那么此时icon只需要在所在行从右往左移动即可
									icon.setPosition( /*item_begin_pos_x +*/calc_end_pos_x - ( icon_moving_dis_ex - icon_mv_dis_tmp_old ) , icon_pos_y );
									//退出该算法
									break;
								}
								else
								{
									//此处无需判断，因为是奇数行 ！= 0
								}
							}
							else if( ix % 4 == 1 )
							{
								//在Y方向上移动，从下往上移动,进入偶数行
								icon_mv_dis_tmp_old = icon_mv_dis_tmp;
								icon_mv_dis_tmp += item_height + item_padding_y;
								if( icon_mv_dis_tmp >= icon_moving_dis_ex )
								{
									icon_pos_y = old_pos.y + ( accumulate_ratio ) * ( item_height + item_padding_y ) + ( icon_moving_dis_ex - icon_mv_dis_tmp_old );
									icon.setPosition( item_begin_pos_x , icon_pos_y );
									break;
								}
							}
							else if( ix % 4 == 2 )
							{
								//偶数行，从左往右移动
								icon_mv_dis_tmp_old = icon_mv_dis_tmp;
								icon_pos_y = old_pos.y + ( accumulate_ratio ) * ( item_height + item_padding_y );
								icon_mv_dis_tmp += item_end_pos_x - item_begin_pos_x;
								if( icon_mv_dis_tmp >= icon_moving_dis_ex )
								{
									icon.setPosition( item_begin_pos_x + ( icon_moving_dis_ex - icon_mv_dis_tmp_old ) , icon_pos_y );
									break;
								}
								else
								{
									//判断此时是否时第一行
									int cur_row = original_row_num - ix / 2;
									if( cur_row == 0 )
									{
										//此时是第一行了，继续往右移动出屏幕
										icon.setPosition( item_begin_pos_x + ( icon_moving_dis_ex - icon_mv_dis_tmp_old ) , icon_pos_y );
										//退出该算法
										break;
									}
								}
							}
							else
							{
								//Y方向移动，从下往上,进入奇数行
								icon_mv_dis_tmp_old = icon_mv_dis_tmp;
								icon_mv_dis_tmp += item_height + item_padding_y;
								if( icon_mv_dis_tmp > icon_moving_dis_ex )
								{
									icon_pos_y = old_pos.y + ( accumulate_ratio ) * ( item_height + item_padding_y ) + ( icon_moving_dis_ex - icon_mv_dis_tmp_old );
									icon.setPosition( item_end_pos_x , icon_pos_y );
									break;
								}
							}
						}
					}
				}
			}
		}
	}
}
