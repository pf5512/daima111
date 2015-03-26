package com.iLoong.launcher.Desktop3D.APageEase;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.UI3DEngine.View3D;

public class Cross {
	public static void updateEffect(GridView3D cur_view,GridView3D next_view,float degree, float yScale, float this_width)
	{
		float xAngle = yScale * 90;
		View3D icon;
		Vector2 old_pos;
		Color item_color;
		int cur_CountX = ((GridView3D)cur_view).getCellCountX();
		int next_CountX = ((GridView3D)next_view).getCellCountX();
		int cur_size = cur_view.getChildCount();
		int next_size = next_view.getChildCount();
		
		for(int i=0;i<cur_size;i++)
		{
			if(i<cur_view.getChildCount())
			{
				int item_row_idx = (int)(i-(i%cur_CountX))/cur_CountX;
				icon = cur_view.getChildAt(i);
				old_pos = (Vector2)icon.getTag();
				item_color = icon.getColor();
				
				item_color.a = 1.0f - Math.abs(degree);
				icon.setColor(item_color);
				
				//偶数向右移动
				if(item_row_idx%2 == 0)
				{
					icon.setPosition(old_pos.x + this_width*Math.abs(degree), icon.getY());
				}
				else
				{
					//奇数左移
					icon.setPosition(old_pos.x - this_width*Math.abs(degree) , icon.getY());
				}
			}
		}
		
		for(int i=0;i<next_size;i++)
		{
			if(i<next_view.getChildCount())
			{
				int item_row_idx = (int)(i-(i%next_CountX))/next_CountX;
				icon = next_view.getChildAt(i);
				old_pos = (Vector2)icon.getTag();
				item_color = icon.getColor();
				
				item_color.a = Math.abs(degree);
				icon.setColor(item_color);
								
				//偶数向右移动
				if(item_row_idx%2 == 0)
				{
					icon.setPosition(old_pos.x + this_width*Math.abs(degree+1), icon.getY());
				}
				else
				{
					//奇数左移
					icon.setPosition(old_pos.x - this_width*Math.abs(degree+1) , icon.getY());
				}
			}
		}
				
		
//		cur_view.setPosition(degree*this_width, 0);
//		next_view.setPosition((degree+1)*this_width, 0);
		

		if (!DefaultLayout.disable_x_effect) {
			cur_view.setRotationX(xAngle);
			next_view.setRotationX(xAngle);
		}
	}
}
