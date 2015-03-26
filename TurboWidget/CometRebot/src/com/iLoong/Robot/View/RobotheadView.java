package com.iLoong.Robot.View;

import android.content.Context;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class RobotheadView extends PluginViewObject3D{

	public RobotheadView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name, region, "head.obj");
//		x = 0;
//		y = 0;

		this.setSize(WidgetRobot.MODEL_WIDTH, WidgetRobot.MODEL_HEIGHT);
		this.setMoveOffset(WidgetRobot.MODEL_WIDTH / 2,
				WidgetRobot.MODEL_HEIGHT / 2, 0);
		super.build();
//		if ( mesh != null)
//		{
//			mesh.scale(WidgetRobot.SCALE_X, WidgetRobot.SCALE_Y,
//					WidgetRobot.SCALE_Z);
//		}
		
		
		// TODO Auto-generated constructor stub
	}

}
