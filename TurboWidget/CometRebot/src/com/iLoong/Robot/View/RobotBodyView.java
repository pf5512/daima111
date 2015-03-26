package com.iLoong.Robot.View;

import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Robot.RobotHelper;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.ObjLoader;

public class RobotBodyView extends PluginViewObject3D {

	private Cache<String, Mesh> mMeshCache = null;

	public RobotBodyView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name, region, "robot_body.obj");
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

	public void setMeshCache(Cache<String, Mesh> cache) {
		this.mMeshCache = cache;
	}

	public void renderMesh(float dx, float dy) {
		InputStream stream = null;
		Mesh mesh = null;
		try {
			if (!WidgetRobot.loadOriginalObj) {
				Mesh originalMesh = null;
				if (WidgetRobot.useCache) {
					originalMesh = (Mesh) mMeshCache
							.get(WidgetRobot.ROBOT_BODY);
				}

				if (originalMesh == null) {
					originalMesh = RobotHelper.loadMesh(appContext, RobotHelper
							.getThemeObjPath(appContext.mThemeName,
									"robot_body.obj"));
					if (WidgetRobot.useCache) {
						mMeshCache.put(WidgetRobot.ROBOT_BODY, originalMesh);
					}
				}
				if (WidgetRobot.useCache) {
					mesh = RobotHelper.copyMesh(originalMesh, appContext);
				} else {
					mesh = originalMesh;
				}
			} else {
				stream = RobotHelper.getThemeObjStream(appContext,
						"robot_body.obj");
				mesh = (Mesh) ObjLoader.loadObj(appContext.gdx, stream, true);
				RobotHelper.move(mesh, 0, 0, WidgetRobot.MODEL_BACK_SCALE_Z);
				if (WidgetRobot.saveObj) {
					RobotHelper.saveMesh(mesh, "robot_body.obj");
				}
				stream.close();
			}
			
			Log.v("cooee","mesh scale:" + WidgetRobot.SCALE_SIZE + " x:"+ WidgetRobot.SCALE_X + " y:" + WidgetRobot.SCALE_Y + " z:" + WidgetRobot.SCALE_Z);
			if (WidgetRobot.SCALE_SIZE != 1) {
				mesh.scale(WidgetRobot.SCALE_X, WidgetRobot.SCALE_Y,
						WidgetRobot.SCALE_Z);
							}
			RobotHelper.move(mesh, (width - dx) / 2, (height - dy) / 2, 0);
			// mesh.scale(20, 20, 20);
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

	@Override
	public boolean onClick(float x, float y) {
		// TODO Auto-generated method stub
		return super.onClick(x, y);
	}

}
