package com.iLoong.launcher.Desktop3D.APageEase;


//import com.iLoong.launcher.Desktop3D.Log;

import com.iLoong.launcher.Desktop3D.Log;

import com.badlogic.gdx.graphics.Color;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;

public class FadeIn {
	public static void updateEffect(ViewGroup3D cur_view,ViewGroup3D next_view,float degree, float yScale, float this_width)
	{
		float xAngle = yScale * 90;
		Color color;
		float z = 0.8f + 0.2f * Math.abs(degree);		
		
		//make sure the cur_view is always on top
		ViewGroup3D parent = cur_view.getParent();
		if(parent == next_view.getParent()){			
			if(cur_view.getIndexInParent() < next_view.getIndexInParent()){
				parent.addViewBefore(cur_view, next_view);
			}
		}		
		
		cur_view.setPosition(degree*this_width, 0);
		color = cur_view.getColor();
		color.a = 1.0f;
		cur_view.setColor(color);
		cur_view.setScale(1.0f, 1.0f);
		
		next_view.setPosition(0, 0);
		color = next_view.getColor();
		color.a = Math.abs(degree);
		next_view.setColor(color);		
		next_view.setScale(z, z);
		
		if (!DefaultLayout.disable_x_effect) {
			cur_view.setRotationX(xAngle);
			next_view.setRotationX(xAngle);	
		}
	}
}
