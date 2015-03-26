package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Erase
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width )
	{
		Color color;
		View3D icon;
		float xAngle = yScale * 90;
		if( degree <= 0 && degree < -1 / 2f )
		{
			//show next_view items gradual changed column by column
			int next_view_row_num = 0;
			int next_view_colomn_num = 0;
			if( cur_view != null )
			{
				next_view_row_num = ( (GridView3D)cur_view ).getCellCountY();
				next_view_colomn_num = ( (GridView3D)cur_view ).getCellCountX();
			}
			if( next_view != null )
			{
				next_view_row_num = ( (GridView3D)next_view ).getCellCountY();
				next_view_colomn_num = ( (GridView3D)next_view ).getCellCountX();
			}
			if( cur_view != null )
			{
				cur_view.hide();
			}
			if( next_view != null )
			{
				next_view.show();
			}
			if( next_view != null )
			{
				for( int column = 0 ; column < next_view_colomn_num ; column++ )
				{
					for( int row = 0 ; row < next_view_row_num ; row++ )
					{
						int item_index = row * next_view_colomn_num + column;
						if( item_index < next_view.getChildCount() )
						{
							icon = next_view.getChildAt( item_index );
							color = icon.getColor();
							if( Math.abs( degree ) * 2 < 1f + (float)( next_view_colomn_num - column ) / ( next_view_colomn_num ) && Math.abs( degree ) * 2 > 1f + ( next_view_colomn_num - column - 1f ) / next_view_colomn_num )
							{
								//确保它的后一列显示
								ExaminePreColumnsStatusAndImplement( next_view , column , column + 1 , true );//true means shown
								//前一列消失
								ExaminePreColumnsStatusAndImplement( next_view , column , column - 1 , false );
								color.a = Math.abs( degree ) * 2f * next_view_colomn_num - ( 2 * next_view_colomn_num - 1f - column );
								icon.setColor( color );
							}
						}
					}
				}
			}
			if( cur_view != null )
			{
				int cur_view_size = cur_view.getChildCount();
				for( int i = 0 ; i < cur_view_size ; i++ )
				{
					View3D cur_view_icon = cur_view.getChildAt( i );
					Color curViewIconColor = cur_view_icon.getColor();
					if( curViewIconColor.a != 0f )
					{
						curViewIconColor.a = 0f;
						cur_view_icon.setColor( curViewIconColor );
					}
				}
			}
			//set next view's color transparent 
		}
		else
		{
			//hide cur_view items gradual changed column by column
			int cur_view_row_num = 0;
			int cur_view_colomn_num = 0;
			if( cur_view != null )
			{
				cur_view_row_num = ( (GridView3D)cur_view ).getCellCountY();
				cur_view_colomn_num = ( (GridView3D)cur_view ).getCellCountX();
			}
			if( next_view != null )
			{
				cur_view_row_num = ( (GridView3D)next_view ).getCellCountY();
				cur_view_colomn_num = ( (GridView3D)next_view ).getCellCountX();
			}
			if( cur_view != null )
			{
				cur_view.show();
			}
			if( next_view != null )
			{
				next_view.hide();
			}
			if( cur_view != null )
			{
				for( int column = cur_view_colomn_num - 1 ; column >= 0 ; column-- )
				{
					for( int row = 0 ; row < cur_view_row_num ; row++ )
					{
						int item_index = row * cur_view_colomn_num + column;
						if( item_index < cur_view.getChildCount() )
						{
							float ratio = -4 * degree;
							icon = cur_view.getChildAt( item_index );
							color = icon.getColor();
							if( Math.abs( degree ) > (float)( cur_view_colomn_num - column - 1 ) / ( 2 * cur_view_colomn_num ) && Math.abs( degree ) < (float)( cur_view_colomn_num - column ) / ( 2 * cur_view_colomn_num ) )
							{
								//检查前一列是否的状态，是否已经消失
								ExaminePreColumnsStatusAndImplement( cur_view , column , column + 1 , false );//false means disappeared
								//确保它的后一列显示
								ExaminePreColumnsStatusAndImplement( cur_view , column , column - 1 , true );//true means shown
								color.a = ( cur_view_colomn_num - Math.abs( degree ) * 2 * cur_view_colomn_num - column );
								icon.setColor( color );
							}
						}
					}
				}
			}
			if( next_view != null )
			{
				//set next view's color transparent 
				int next_view_size = next_view.getChildCount();
				for( int i = 0 ; i < next_view_size ; i++ )
				{
					View3D next_view_icon = next_view.getChildAt( i );
					Color nextViewIconColor = next_view_icon.getColor();
					if( nextViewIconColor.a != 0f )
					{
						nextViewIconColor.a = 0f;
						next_view_icon.setColor( nextViewIconColor );
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
	
	private static void ExaminePreColumnsStatusAndImplement(
			ViewGroup3D cur_view ,
			int cur_column ,
			int pre_column ,
			boolean is_shown_or_disappeard )
	{
		int cur_CountX = ( (GridView3D)cur_view ).getCellCountX();
		int cur_CountY = ( (GridView3D)cur_view ).getCellCountY();
		float user_intend_color_a = -1f;
		if( pre_column < 0 || pre_column >= cur_CountX )
		{
			return;
		}
		if( is_shown_or_disappeard == true )
		{
			user_intend_color_a = 1f;
		}
		else
		{
			user_intend_color_a = 0f;
		}
		for( int row = 0 ; row < cur_CountY ; row++ )
		{
			if( cur_column > pre_column )
			{
				//reset the precolumns
				for( int column = 0 ; column <= pre_column ; column++ )
				{
					int index = row * cur_CountX + column;
					if( index < cur_view.getChildCount() )
					{
						View3D icon = cur_view.getChildAt( index );
						Color color = icon.getColor();
						if( color.a != user_intend_color_a )
						{
							color.a = user_intend_color_a;
							icon.setColor( color );
						}
					}
				}
			}
			else
			{
				for( int column = pre_column ; column < cur_CountX ; column++ )
				{
					int index = row * cur_CountX + column;
					if( index < cur_view.getChildCount() )
					{
						View3D icon = cur_view.getChildAt( index );
						Color color = icon.getColor();
						if( color.a != user_intend_color_a )
						{
							color.a = user_intend_color_a;
							icon.setColor( color );
						}
					}
				}
			}
		}
	}
}
