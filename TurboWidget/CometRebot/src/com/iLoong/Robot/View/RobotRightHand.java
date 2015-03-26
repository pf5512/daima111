package com.iLoong.Robot.View;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class RobotRightHand extends PluginViewObject3D{

	public RobotRightHand(MainAppContext appContext, String name,
			TextureRegion region, String objName) {
		super(appContext, name, region, objName);
		this.setSize(WidgetRobot.MODEL_WIDTH, WidgetRobot.MODEL_HEIGHT);
		this.setMoveOffset(WidgetRobot.MODEL_WIDTH / 2,
				WidgetRobot.MODEL_HEIGHT / 2, 0);
		super.build();
//		move(-10.0F * WidgetRobot.SCALE_X, 0.0F, 0.0F);
//		if ( mesh != null)
//		{
//			mesh.scale(WidgetRobot.SCALE_X, WidgetRobot.SCALE_Y,
//					WidgetRobot.SCALE_Z);
//		}
		// TODO Auto-generated constructor stub
	}

}
