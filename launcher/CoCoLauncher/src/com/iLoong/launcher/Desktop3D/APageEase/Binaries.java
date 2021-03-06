package com.iLoong.launcher.Desktop3D.APageEase;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;

public class Binaries {
	public static void updateEffect(ViewGroup3D cur_view,ViewGroup3D next_view,float degree, float yScale, float this_width)
	{
		View3D icon;
		Vector2 pos;
		Vector2 old_pos;
		float radio;
		float xAngle = yScale * 90;
		float this_height = cur_view.getHeight();
		pos = new Vector2(this_width/2.0f,this_height/2.0f);
		if(degree<=0&&degree>(-1/2f))
		{
			radio = degree*(-2f);
		}
		else
		{
			radio = (degree+1)*(2f);
		}
		
		cur_view.setPosition(degree*this_width, 0);
		int cur_size = cur_view.getChildCount();
		for(int i = 0;i<cur_size;i++)
		{
			if(i<cur_view.getChildCount())
			{
				icon = cur_view.getChildAt(i);
				old_pos = (Vector2)icon.getTag();
				icon.setPosition(old_pos.x+ (pos.x - old_pos.x)*radio, old_pos.y+ (pos.y-old_pos.y)*radio);
			}
		}
		
		next_view.setPosition((degree+1)*this_width, 0);		
		int next_size = next_view.getChildCount();
		for(int i = 0;i<next_size;i++)
		{
			if(i<next_view.getChildCount())
			{
				icon = next_view.getChildAt(i);
				old_pos = (Vector2)icon.getTag();
				icon.setPosition(old_pos.x+ (pos.x - old_pos.x)*radio, old_pos.y+ (pos.y-old_pos.y)*radio);
			}
		}
		
		if (!DefaultLayout.disable_x_effect) {		
			cur_view.setRotationX(xAngle);
			next_view.setRotationX(xAngle);
		}
	}
}
