package com.iLoong.launcher.Desktop3D.APageEase;

import com.iLoong.launcher.Desktop3D.Log;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;

public class Ceraunite {
	public static void updateEffect(ViewGroup3D cur_view,ViewGroup3D next_view,float degree, float yScale, float this_width,boolean is_right_to_left)
	{
		float xAngle = yScale * 90;
		float this_height = cur_view.getHeight();
		final int cur_countX= ((GridView3D)cur_view).getCellCountX();
		final int cur_countY= ((GridView3D)cur_view).getCellCountY();
		final int next_countX= ((GridView3D)next_view).getCellCountX();
		final int next_countY= ((GridView3D)next_view).getCellCountY();
		final int CUR_ROWS_COLUMNS = cur_countX + cur_countY - 1;
		final int NEXT_ROWS_COLUMNS = next_countX + next_countY - 1;
		final int CONST_VALUE = cur_countY - 1; //4
		
		
//		Log.v("cooee","------degree = " + degree);
		
		if(is_right_to_left == true)
		{
			
//			Log.v("cooee","------is_right_to_left  == true ----- 从右到左移动----- ");
			
			if(degree<=0 && degree >= -1/2f)
			{
				boolean is_next_icon_group_moving_in_turn = false;
				float ratio = degree * 2;
				
				cur_view.show();//
				next_view.hide();
				
				
				for(int a=0; a < NEXT_ROWS_COLUMNS; a++)
				{
					if(a == 0)
					{
						//因为a =0,为第一组移动的icon
						is_next_icon_group_moving_in_turn = true;
					}
					
					if(is_next_icon_group_moving_in_turn == false)
					{
						//前一组的icon还未移动完成，需要等待一个degree，等前一组的icon移动完成
						break;
					}
										
					for(int column=0; column<cur_countX;column++)
					{
						for(int row=0; row<cur_countY; row++)
						{
							//(row-column) --> 4,3,2,1,0,-1,-2,-3,....
							if(CONST_VALUE - a == row - column)
							{
								
								int icon_idx = row* cur_countX + column;
								if(icon_idx < cur_view.getChildCount())
								{

									//目前本组的icon需要移动
									float tmp_ratio = Math.abs(ratio) * NEXT_ROWS_COLUMNS - a;
									
									View3D icon = cur_view.getChildAt(icon_idx);
									Vector2 old_pos = (Vector2)icon.getTag();
									
									if(tmp_ratio >= 0f &&tmp_ratio < 1.0f)
									{
										ExaminePreGroupsAtOriginalPos(cur_view, CONST_VALUE - a - 1,  true);
										
										icon.setPosition(old_pos.x - (this_width)*tmp_ratio, old_pos.y - (this_height)*tmp_ratio);
										is_next_icon_group_moving_in_turn = false;
									}
									else if(tmp_ratio >= 1.0f)
									{
										icon.setPosition(old_pos.x - this_width, old_pos.y - this_height);
										is_next_icon_group_moving_in_turn = true;
									}
									else
									{
										//tmp_ratio < 0
										//do nothing
									}
									
								}
							}
						}
					}
				}	
				
/*				//next_view hide
				for(int i=0;i<next_view.getChildCount();i++)
				{
					if(i < next_view.getChildCount())
					{
						View3D item = next_view.getChildAt(i);
						Color color = item.getColor();
						
						color.a = 0f;
						item.setColor(color);
					}
				}*/
			}
			else
			{
				//
				float ratio = 2*degree + 1;
				boolean is_next_icon_group_moving_in_turn = false;
				
				//next_view hide
				for(int i=0;i<next_view.getChildCount();i++)
				{
					if(i < next_view.getChildCount())
					{
						View3D item = next_view.getChildAt(i);
						Color color = item.getColor();
						
						color.a = 0f;
						item.setColor(color);
					}
				}				
				
				next_view.show();//next_view.show();
				cur_view.hide();
				
				//以下的icon是按a的顺序移动的
				for(int a=0; a < NEXT_ROWS_COLUMNS; a++)
				{
					if(a == 0)
					{
						//因为a =0,为第一组移动的icon
						is_next_icon_group_moving_in_turn = true;
					}
					
					if(is_next_icon_group_moving_in_turn == false)
					{
						//前一组的icon还未移动完成，需要等待一个degree，等前一组的icon移动完成
						break;
					}
					
					for(int column=0; column<next_countX;column++)
					{
						for(int row=0; row<next_countY; row++)
						{
							//(row-column) --> 4,3,2,1,0,-1,-2,-3,....
							if(CONST_VALUE - a == row - column)
							{
								int icon_idx = row* next_countX + column;
								
								if(icon_idx < next_view.getChildCount())
								{

									//目前本组的icon需要移动
									float tmp_ratio = Math.abs(ratio) * NEXT_ROWS_COLUMNS - a;
									
									View3D icon = next_view.getChildAt(icon_idx);
									Vector2 old_pos = (Vector2)icon.getTag();

									
									if(tmp_ratio >= 0f &&tmp_ratio < 1.0f)
									{
										Color color = icon.getColor();
										color.a = 1.0f;
										icon.setColor(color);
										
										//ExaminePreGroupsAtOriginalPos(next_view, CONST_VALUE - a - 1,  true);
										
										icon.setPosition(old_pos.x  + (this_width) - (this_width)*tmp_ratio, old_pos.y + this_height - (this_height)*tmp_ratio);
										is_next_icon_group_moving_in_turn = false;
									}
									else if(tmp_ratio >= 1.0f)
									{
										Color color = icon.getColor();
										color.a = 1.0f;
										icon.setColor(color);
										
										icon.setPosition(old_pos.x, old_pos.y);
										is_next_icon_group_moving_in_turn = true;
									}
									else
									{
										//tmp_ratio < 0 , do nothing
									}

								}
							}
						}
					}
				}
			}
			

		}
		else
		{
			//from left to right
			
			if(degree<=0 && degree <= -1/2f)
			{
				//先消失
				float ratio = (1+degree) * 2;
				boolean is_next_icon_group_moving_in_turn = false;
				
				cur_view.hide();
				next_view.show();
				
				for(int a = NEXT_ROWS_COLUMNS - 1; a >= 0; a--)
				{
					if(a == NEXT_ROWS_COLUMNS - 1)
					{
						//因为a = NEXT_ROWS_COLUMNS - 1,为第一组移动的icon
						is_next_icon_group_moving_in_turn = true;
					}
					
					if(is_next_icon_group_moving_in_turn == false)
					{
						//前一组的icon还未移动完成，需要等待一个degree，等前一组的icon移动完成
						break;
					}
					
					for(int column=0; column<next_countX;column++)
					{
						for(int row=0; row<next_countY; row++)
						{
							if(a == column + row)
							{
								int icon_idx = row* next_countX + column;
								
								if(icon_idx < next_view.getChildCount())
								{
	
									float tmp_ratio = Math.abs(ratio) * NEXT_ROWS_COLUMNS - (NEXT_ROWS_COLUMNS-a-1);
									View3D icon = next_view.getChildAt(icon_idx);
									Vector2 old_pos = (Vector2)icon.getTag();


									if(tmp_ratio >= 0f &&tmp_ratio < 1.0f)
									{
										//先判断preGroup的icon是否在原先位置上
										ExaminePreGroupsAtOriginalPos(next_view, a-1,false);
										icon.setPosition(old_pos.x + (this_width)*tmp_ratio, old_pos.y - (this_height)*tmp_ratio);
										is_next_icon_group_moving_in_turn = false;
									}
									else if(tmp_ratio >= 1.0f)
									{
										icon.setPosition(old_pos.x + (this_width), old_pos.y - (this_height));
										is_next_icon_group_moving_in_turn = true;
									}
									else
									{
										//tmp_ratio < 0 , do nothing
									}
								}
							}
						}
					}
				}
/*				//cur_view hide
				for(int i=0;i<cur_view.getChildCount();i++)
				{
					if(i < cur_view.getChildCount())
					{
						View3D item = cur_view.getChildAt(i);
						Color color = item.getColor();
						
						color.a = 0f;
						item.setColor(color);
					}
				}			
				*/
			}
			else
			{
				float ratio = (degree + 1/2f) * 2;
				boolean is_next_icon_group_moving_in_turn = false;
				
				//cur_view hide
				for(int i=0;i<cur_view.getChildCount();i++)
				{
					if(i < cur_view.getChildCount())
					{
						View3D item = cur_view.getChildAt(i);
						Color color = item.getColor();
						
						color.a = 0f;
						item.setColor(color);
					}
				}			
				
				
				next_view.hide();
				cur_view.show();
				
				for(int a = CUR_ROWS_COLUMNS - 1; a >= 0; a--)
				{
					if(a == CUR_ROWS_COLUMNS - 1)
					{
						//因为a = NEXT_ROWS_COLUMNS - 1,为第一组移动的icon
						is_next_icon_group_moving_in_turn = true;
					}
					
					if(is_next_icon_group_moving_in_turn == false)
					{
						//前一组的icon还未移动完成，需要等待一个degree，等前一组的icon移动完成
						break;
					}
					
					for(int column=0; column<cur_countX;column++)
					{
						for(int row=0; row<cur_countY; row++)
						{
							if(a == column + row)
							{
								int icon_idx = row* cur_countX + column;
								
								if(icon_idx < cur_view.getChildCount())
								{
	
									float tmp_ratio = Math.abs(ratio) * NEXT_ROWS_COLUMNS - (NEXT_ROWS_COLUMNS-a-1);
									View3D icon = cur_view.getChildAt(icon_idx);
									Vector2 old_pos = (Vector2)icon.getTag();
									
									if(tmp_ratio >= 0f &&tmp_ratio < 1.0f)
									{
										Color color = icon.getColor();
										color.a = 1.0f;
										icon.setColor(color);
										
										icon.setPosition(old_pos.x - (this_width) + (this_width)*tmp_ratio, old_pos.y + this_height - (this_height)*tmp_ratio);
										is_next_icon_group_moving_in_turn = false;
									}
									else if(tmp_ratio >= 1.0f)
									{
										Color color = icon.getColor();
										color.a = 1.0f;
										icon.setColor(color);
										
										icon.setPosition(old_pos.x , old_pos.y);
										is_next_icon_group_moving_in_turn = true;
									}
									else
									{
										//tmp_ratio < 0 , do nothing
									}
								}
							}
						}
					}
				}				
			}
		}
		
		if (!DefaultLayout.disable_x_effect) {
			cur_view.setRotationX(xAngle);
			next_view.setRotationX(xAngle);
		}
	}
	
	//在scroll的过程中，当往回滑动时，需要保证之前消失的item恢复原来的位置
	private static void ExaminePreGroupsAtOriginalPos(ViewGroup3D cur_view, /*int total_group,*/ int pre_group_id, boolean is_order )
	{
		View3D cur_icon;
		int view_size = cur_view.getChildCount();
		int mCountX = ((GridView3D)cur_view).getCellCountX();
		int mCountY = ((GridView3D)cur_view).getCellCountY();
		final int CONST_VALUE = mCountY - 1; //4
		final int VIEW_ROWS_COLUMNS = mCountX + mCountY - 1;
		
//		Log.v("cooee","---ExaminePreGroupsAtOriginalPos --- enter --- pre_group_id = " + pre_group_id);
		
		if(is_order == false)
		{
			//消失的组别数次序从大到小
			
			if(pre_group_id < 0)
			{
				return;
			}
			
			for(int i=0;i<mCountX;i++)
			{
				for(int j=0; j<mCountY;j++)
				{
					int index = j*mCountX + i;
					
					if(index < view_size && ((i+j)<=pre_group_id))
					{
						for(int groupId = pre_group_id; groupId>=0;groupId--)
						{
							if((i+j) == groupId)
							{
								cur_icon = cur_view.getChildAt(index);
								Vector2 old_pos = (Vector2)cur_icon.getTag();
								
//								if(cur_icon.x <= view_width || (cur_icon.y + 2*cur_icon.originY)>=0 )
//								{
//									cur_icon.setPosition(cur_icon.x + view_width, cur_icon.y - view_height - 2*cur_icon.originY);
//								}
								cur_icon.setPosition(old_pos.x, old_pos.y);
							}
						}
					}
				}
			}

		}
		else
		{
			//消失的组别数次序从小到大
			//pre_group_id 为 Row-column 的值
			
//			Log.v("cooee","---ExaminePreGroupsAtOriginalPos --- case 1111111  min_group_id = " + (CONST_VALUE - (VIEW_ROWS_COLUMNS -1)));
			
			if(pre_group_id < CONST_VALUE - (VIEW_ROWS_COLUMNS -1) )
			{
				return;
			}
			
			for(int i=0;i<mCountX;i++)
			{
				for(int j=0; j<mCountY;j++)
				{
					int index = j*mCountX + i;
					
					if(index < view_size && ((j-i) <= pre_group_id))
					{
						for(int groupId = pre_group_id; groupId>=(CONST_VALUE - VIEW_ROWS_COLUMNS +1);groupId--)
						{
							if((j-i) == groupId)
							{
								cur_icon = cur_view.getChildAt(index);
								Vector2 old_pos = (Vector2)cur_icon.getTag();
								
//								Log.v("cooee","---ExaminePreGroupsAtOriginalPos ---11111---huifu--- cur_icon = " + cur_icon.name);								
								
								//此时该icon应该回到原来的位置
								{
									cur_icon.setPosition(old_pos.x , old_pos.y);
								}
							}
						}
					}
				}
			}			
		}
		
	}
	
}
