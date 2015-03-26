package com.iLoong.Robot.View;

import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Robot.RobotHelper;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class RobotFace extends PluginViewObject3D{

	private TextureRegion eyeSmileRegion = null;
	public RobotFace(MainAppContext appContext, String name,
			String textureName, String objName) {
		super(appContext, name, textureName, objName);
		this.setSize(WidgetRobot.MODEL_WIDTH, WidgetRobot.MODEL_HEIGHT);
		this.setMoveOffset(WidgetRobot.MODEL_WIDTH / 2,
				WidgetRobot.MODEL_HEIGHT / 2, 0);
		super.build();
//		if ( mesh != null)
//		{
//			mesh.scale(WidgetRobot.SCALE_X, WidgetRobot.SCALE_Y,
//					WidgetRobot.SCALE_Z);
//		}
//		Texture texture = RobotHelper.getThemeTexture(appContext, "robot_eye_smile.png");
//		eyeSmileRegion = new TextureRegion(texture);
		
		// TODO Auto-generated constructor stub
	}
//	@Override
//	public void draw(SpriteBatch batch, float parentAlpha) {
//		// TODO Auto-generated method stub
//		super.draw(batch, parentAlpha);
//		Log.v("face", "eye the width is " + eyeSmileRegion.getRegionWidth() * WidgetRobot.SCALE_X + " yheight is " + eyeSmileRegion.getRegionHeight() * WidgetRobot.SCALE_Y);
//		batch.draw(eyeSmileRegion, 0, 0, eyeSmileRegion.getRegionWidth() * WidgetRobot.SCALE_X, eyeSmileRegion.getRegionHeight() * WidgetRobot.SCALE_Y);
//	}

}
