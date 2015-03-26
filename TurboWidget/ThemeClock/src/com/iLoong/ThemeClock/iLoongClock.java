package com.iLoong.ThemeClock;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.iLoong.ThemeClock.Common.ClockHelper;
import com.iLoong.ThemeClock.View.ClockBackView;
import com.iLoong.ThemeClock.View.ClockHourView;
import com.iLoong.ThemeClock.View.ClockMinuteView;
import com.iLoong.ThemeClock.View.ClockSecondView;
import com.iLoong.ThemeClock.View.WidgetClock;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.UI3DEngine.adapter.Texture;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.CacheManager;
import com.iLoong.launcher.Widget3D.IWidget3DPlugin;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;

public class iLoongClock implements IWidget3DPlugin {
	CooGdx cooGdx = null;

	@Override
	public View3D getWidget(MainAppContext context, int widgetId) {
		// TODO Auto-generated method stub
		return new WidgetClock("widgetClock", context, widgetId);
	}

	/**
	 * Launcher加载时预加载Clock的一些资源，加快Clock拖出的速度
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void preInitialize(MainAppContext context) {
		cooGdx = new CooGdx(context.mGdxApplication);
		Cache<String, Mesh> meshCache;
		// WidgetClock.transformTheme(context.mWidgetContext,
		// context.mThemeComponentName);
		float MODEL_WIDTH = context.mWidgetContext.getResources().getDimension(
				R.dimen.clock_width);
		float MODEL_HEIGHT = context.mWidgetContext.getResources()
				.getDimension(R.dimen.clock_height);
		WidgetClock.SCALE_X = WidgetClock.SCALE_Y = WidgetClock.SCALE_Z = WidgetClock.SCALE_SIZE = ((float) context.mWidgetContext
				.getResources().getDisplayMetrics().density /(float) 2.0);
		try {
			meshCache = (Cache<String, Mesh>) CacheManager
					.getCache(WidgetClock.MESH_CACHE_NAME);
			Cache<String, TextureRegion> textureCache = (Cache<String, TextureRegion>) CacheManager
					.getCache(WidgetClock.TEXTURE_CACHE_NAME);
			loadMesh(context, meshCache, ClockBackView.WATCH_BACK_OBJ,
					MODEL_WIDTH / 2, MODEL_HEIGHT / 2, 0);
			loadMesh(context, meshCache, ClockHourView.WATCH_HOURHAND_OBJ,
					MODEL_WIDTH / 2, MODEL_HEIGHT / 2, 0);
			loadMesh(context, meshCache, ClockMinuteView.WATCH_MINUTEHAND_OBJ,
					MODEL_WIDTH / 2, MODEL_HEIGHT / 2, 0);
			loadMesh(context, meshCache, ClockSecondView.WATCH_SECONDHAND_OBJ,
					MODEL_WIDTH / 2, MODEL_HEIGHT / 2, 0);
			loadTexture(textureCache, context, ClockHelper.getThemeImagePath(
					context.mThemeName, WidgetClock.WATCH_BACK_TEXTURE),
					WidgetClock.WATCH_BACK_TEXTURE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadTexture(Cache<String, TextureRegion> textureCache,
			MainAppContext context, String imageFile, String key) {
		Texture texture = new Texture(cooGdx.gdx, new AndroidFiles(
				context.mWidgetContext.getAssets()).internal(imageFile));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion region = new TextureRegion(texture);
		textureCache.put(key, region);
	}

	private void loadMesh(MainAppContext mainAppContext,
			Cache<String, Mesh> meshCache, String objName, float offsetX,
			float offsetY, float offsetZ) {
		String objPath = ClockHelper.getThemeObjPath(mainAppContext.mThemeName,
				objName);
		Mesh mesh = getMesh(mainAppContext.gdx, mainAppContext.mWidgetContext,
				objName, objPath, offsetX, offsetY, offsetZ);
		meshCache.put(objName, mesh);
		Log.v("iLoongRobot", "loadMesh:" + objName);
	}

	public Mesh getMesh(Gdx gdx, Context widtgetContext, String objName,
			String objPath, float offsetX, float offsetY, float offsetZ) {
		Mesh mesh = null;
		if (WidgetClock.loadOriginalObj) {
			mesh = ClockHelper.loadOriginalMesh(gdx, widtgetContext, objName,
					objPath, offsetX, offsetY, 0, WidgetClock.SCALE_X,
					WidgetClock.SCALE_Y, WidgetClock.SCALE_Z);

		} else {
			mesh = ClockHelper.loadCompressedMesh(gdx, widtgetContext, objPath,
					offsetX, offsetY, offsetZ, WidgetClock.SCALE_X,
					WidgetClock.SCALE_Y, WidgetClock.SCALE_Z);
		}
		return mesh;
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
