package com.iLoong.Robot.View;

import com.iLoong.launcher.Widget3D.MainAppContext;

public class RobotLight extends PluginViewObject3D{

	public RobotLight(MainAppContext appContext, String name,
			String textureName, String objName) {
		super(appContext, name, textureName, objName);
		// TODO Auto-generated constructor stub
		this.setSize(WidgetRobot.MODEL_WIDTH, WidgetRobot.MODEL_HEIGHT);
		this.setMoveOffset(WidgetRobot.MODEL_WIDTH / 2,
				WidgetRobot.MODEL_HEIGHT / 2, 0);
		super.build();
//		if ( mesh != null)
//		{
//			mesh.scale(WidgetRobot.SCALE_X, WidgetRobot.SCALE_Y,
//					WidgetRobot.SCALE_Z);
//		}
	}
	
}
