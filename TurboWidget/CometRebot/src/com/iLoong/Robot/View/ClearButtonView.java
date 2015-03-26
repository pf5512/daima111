package com.iLoong.Robot.View;

import java.io.IOException;
import java.io.InputStream;

import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Robot.RobotHelper;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.ObjLoader;
import com.iLoong.launcher.min3d.Object3DBase;

public class ClearButtonView extends Object3DBase {

	private Cache<String, Mesh> mMeshCache = null;
	private MainAppContext mAppContext;

	public ClearButtonView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name);
		this.mAppContext = appContext;
		x = 0;
		y = 0;
		this.region.setRegion(region);
		this.width = WidgetRobot.MODEL_WIDTH;
		this.height = WidgetRobot.MODEL_HEIGHT;
		this.setOrigin(width / 2, height / 2);
	}

	public void setMeshCache(Cache<String, Mesh> cache) {
		this.mMeshCache = cache;
	}

	public void renderMesh(float dx, float dy) {
		InputStream stream = null;
		Mesh mesh = null;
		try {
			if (!WidgetRobot.loadOriginalObj) {
				Mesh originalMesh = (Mesh) mMeshCache
						.get(WidgetRobot.ROBOT_CLEAR_BUTTON);
				if (originalMesh == null) {
					originalMesh = RobotHelper.loadMesh(mAppContext,
							"robot_clear_button.obj");
					mMeshCache
							.put(WidgetRobot.ROBOT_CLEAR_BUTTON, originalMesh);
				}
				mesh = RobotHelper.copyMesh(originalMesh, mAppContext);
			} else {
				stream = RobotHelper.getThemeObjStream(mAppContext,
						"robot_clear_button.obj");
				mesh = (Mesh) ObjLoader.loadObj(mAppContext.gdx, stream, true);
				RobotHelper.move(mesh, 0, 0, WidgetRobot.MODEL_BACK_SCALE_Z);
				if (WidgetRobot.saveObj) {
					RobotHelper.saveMesh(mesh, "robot_clear_button.obj");
				}
				stream.close();
			}
			if (WidgetRobot.SCALE_SIZE != 1) {
				mesh.scale(WidgetRobot.SCALE_X, WidgetRobot.SCALE_Y,
						WidgetRobot.SCALE_Z);
			}
			RobotHelper.move(mesh, (width - dx) / 2, (height - dy) / 2, 0);
			setMesh(mesh);
			enableDepthMode(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	@Override
	public boolean onClick(float x, float y) {
		double r = Math.sqrt((x - this.originX) * (x - this.originX)
				+ (y - this.originY) * (y - this.originY));
		if (r > 80) {
			return false;
		} else {
			return true;
		}
	}

	Timeline mButtonRingTimeline;

	@Override
	public boolean is3dRotation() {
		// TODO Auto-generated method stub
		return true;
	}
}
