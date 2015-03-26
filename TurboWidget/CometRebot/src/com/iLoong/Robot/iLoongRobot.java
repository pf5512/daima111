package com.iLoong.Robot;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooeeui.cometrobot.R;
import com.iLoong.Robot.View.WidgetRobot;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.CacheManager;
import com.iLoong.launcher.Widget3D.IWidget3DPlugin;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;

public class iLoongRobot implements IWidget3DPlugin {
	CooGdx cooGdx = null;

	@Override
	public View3D getWidget(MainAppContext globalContext, int widgetId) {
		// TODO Auto-generated method stub
		return new WidgetRobot("Toggle", globalContext, widgetId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void preInitialize(MainAppContext context) {
		// TODO Auto-generated method stub
		Cache<String, Mesh> meshCache;

		Cache<String, TextureRegion> textureCache = (Cache<String, TextureRegion>) CacheManager
				.getCache(WidgetRobot.TEXTURE_CACHE_NAME);
		cooGdx = new CooGdx(context.mGdxApplication);
		try {
			meshCache = (Cache<String, Mesh>) CacheManager
					.getCache(WidgetRobot.MESH_CACHE_NAME);
			loadMesh(meshCache, context, RobotHelper.getThemeObjPath(
					context.mThemeName, "robot_body.obj"),
					WidgetRobot.ROBOT_BODY);
			loadMesh(meshCache, context, RobotHelper.getThemeObjPath(
					context.mThemeName, "robot_lighting.obj"),
					WidgetRobot.ROBOT_LIGHTING);
			loadMesh(meshCache, context, RobotHelper.getThemeObjPath(
					context.mThemeName, "robot_eye.obj"), WidgetRobot.ROBOT_EYE);
			loadMesh(meshCache, context, RobotHelper.getThemeObjPath(
					context.mThemeName, "battery_outer.obj"),
					WidgetRobot.BATTERY_OUTER);
			loadMesh(meshCache, context, RobotHelper.getThemeObjPath(
					context.mThemeName, "robot_arm.obj"), WidgetRobot.ROBOT_ARM);
			loadMesh(meshCache, context, RobotHelper.getThemeObjPath(
					context.mThemeName, "battery.obj"), WidgetRobot.BATTERY);
			loadMesh(meshCache, context, RobotHelper.getThemeObjPath(
					context.mThemeName, "robot_clear_button_ring.obj"),
					WidgetRobot.ROBOT_CLEAR_BUTTON_RING);

			loadTexture(textureCache, context, RobotHelper.getThemeImagePath(
					context.mThemeName, "robot_body.png"),
					WidgetRobot.ROBOT_BODY);
			loadTexture(textureCache, context, RobotHelper.getThemeImagePath(
					context.mThemeName, "robot_lighting_4.png"),
					WidgetRobot.ROBOT_LIGHTING);
			loadTexture(textureCache, context, RobotHelper.getThemeImagePath(
					context.mThemeName, "robot_eye_3.png"),
					WidgetRobot.ROBOT_EYE);
			loadTexture(textureCache, context, RobotHelper.getThemeImagePath(
					context.mThemeName, "battery.png"),
					WidgetRobot.BATTERY_OUTER);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadTexture(Cache<String, TextureRegion> textureCache,
			MainAppContext context, String imageFile, String key) {
		Texture texture = RobotHelper.getThemeTexture(context, imageFile);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion region = new TextureRegion(texture);
		textureCache.put(key, region);
	}

	private void loadMesh(Cache<String, Mesh> meshCache,
			MainAppContext context, String filePath, String key) {
		// Log.v("iLoongRobot", "loadMesh:" + filePath);
		InputStream stream = null;
		Mesh mesh = null;
		try {
			stream = RobotHelper.getThemeObjStream(context, filePath);
			mesh = (Mesh) RobotHelper.loadMesh(cooGdx.gdx, stream);
			if (mesh != null) {
				meshCache.put(key, mesh);
			}
			stream.close();
		} catch (IOException e) {
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
	public WidgetPluginViewMetaData getWidgetPluginMetaData(
			MainAppContext mainAppContext, int widgetId) {
		// TODO Auto-generated method stub
		Context mContext = mainAppContext.mWidgetContext;
		WidgetPluginViewMetaData metaData = new WidgetPluginViewMetaData();
		metaData.spanX = Integer.valueOf(mContext.getResources().getInteger(
				R.integer.spanX));
		metaData.spanY = Integer.valueOf(mContext.getResources().getInteger(
				R.integer.spanY));
		metaData.maxInstanceCount = mContext.getResources().getInteger(
				R.integer.max_instance);
		metaData.maxInstanceAlert = mContext.getResources().getString(
				R.string.max_instance_alert);
		metaData.iconResourceId = R.drawable.widget_ico;
		return metaData;
	}
}
