package com.cooee.widget3D.JewelWeather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooee.widget3D.JewelWeather.R;
import com.cooee.widget3D.JewelWeather.Common.WeatherHelper;
import com.cooee.widget3D.JewelWeather.DataProvider.WeatherData;
import com.cooee.widget3D.JewelWeather.DataProvider.WeatherUpdate;
import com.cooee.widget3D.JewelWeather.View.EnterInAnim;
import com.cooee.widget3D.JewelWeather.View.WidgetWeather;
import com.cooee.widget3D.JewelWeather.View.PluginViewObject3D.LoadObjType;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.UI3DEngine.adapter.Texture;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.CacheManager;
import com.iLoong.launcher.Widget3D.IWidget3DPlugin;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;

public class iLoongWeather implements IWidget3DPlugin {
	private static final String TAG = "com.iLoong.Weather.iLoongWeather";

	
	CooGdx cooGdx = null;
	private int mWidgetId = -1;
	protected boolean mSaveCompressedObj = false;
	LoadObjType mObjType = LoadObjType.original;
	public static WidgetWeather mWidgetWeather = null;
//	public static WidgetWeatherViewRoot mWidgetWeatherViewRoot = null;
	@Override
	public View3D getWidget(MainAppContext context, int widgetId) {
		mWidgetId = widgetId;
		// 尝试获取当前位置
		// LocationUtils.getCNBylocation(context.mWidgetContext);
		mWidgetWeather = new WidgetWeather("widgetWeather", context, widgetId);
	//	mWidgetWeatherViewRoot = new WidgetWeatherViewRoot("widgetWeatherViewRoot", context, widgetId);

		//weijie 20130122
		if (null != WeatherData.getPostalCode(context.mWidgetContext,widgetId))
		{
			// 更新Weather相关的View
			WeatherUpdate.updateViews(context.mWidgetContext, widgetId);
		}
		return mWidgetWeather;
	}

	/**
	 * Launcher加载时预加载Weather的一些资源，加快Weather拖出的速度
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void preInitialize(MainAppContext appContext) {
		cooGdx = new CooGdx(appContext.mGdxApplication);
		Cache<String, Mesh> meshCache;
		Cache<String, Mesh> meshCacheboat;
		float move_offset_x = appContext.mWidgetContext.getResources()
				.getDimension(R.dimen.move_offset_x);
		float move_offset_y = appContext.mWidgetContext.getResources()
				.getDimension(R.dimen.move_offset_y);

		float scaleX = 1f;
		float scaleY = 1f;
		float scaleZ = 1f;
		scaleX = scaleY = scaleZ = ((float) appContext.mWidgetContext
				.getResources().getDisplayMetrics().density / (float) 1.5);
		mObjType = WeatherHelper.getObjType(appContext);
		mSaveCompressedObj = appContext.mWidgetContext.getResources()
				.getBoolean(R.string.save_compressed_obj);
/*
		try {
			meshCache = (Cache<String, Mesh>) CacheManager
					.getCache(WidgetWeather.MESH_CACHE_NAME);
			Cache<String, TextureRegion> textureCache = (Cache<String, TextureRegion>) CacheManager
					.getCache(WidgetWeather.TEXTURE_CACHE_NAME);
			loadMesh(appContext, meshCache, MarineShelfView.OBJ,
					move_offset_x / 2, move_offset_y / 2, 0, scaleX, scaleY,
					scaleZ);
			loadTexture(textureCache, appContext,
					WeatherHelper.getThemeImagePath(
							WeatherHelper.getThemeName(appContext),
							MarineShelfView.TEXTURE), MarineShelfView.TEXTURE);
			
			meshCache = (Cache<String, Mesh>) CacheManager
					.getCache(WidgetWeather.MESH_CACHE_NAME);
			Cache<String, TextureRegion> textureCachebaot = (Cache<String, TextureRegion>) CacheManager
					.getCache(WidgetWeather.TEXTURE_CACHE_NAME);
			loadMesh(appContext, meshCache, Boatview.OBJ,
					move_offset_x / 2, move_offset_y / 2, 0, scaleX, scaleY,
					scaleZ);
			loadTexture(textureCachebaot, appContext,
					WeatherHelper.getThemeImagePath(
							WeatherHelper.getThemeName(appContext),
							Boatview.TEXTURE), Boatview.TEXTURE);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
	}

	private void loadTexture(Cache<String, TextureRegion> textureCache,
			MainAppContext context, String imageFile, String key) {
		Texture texture = new Texture(cooGdx.gdx, new AndroidFiles(
				context.mWidgetContext.getAssets()).internal(imageFile));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion region = new TextureRegion(texture);
		textureCache.put(key, region);
	}

	private void loadMesh(MainAppContext appContext,
			Cache<String, Mesh> meshCache, String objName, float offsetX,
			float offsetY, float offsetZ, float scaleX, float scaleY,
			float scaleZ) {
		String objPath = WeatherHelper.getThemeObjPath(
				WeatherHelper.getThemeName(appContext), objName, mObjType);
		Mesh mesh = getMesh(appContext.gdx, appContext.mWidgetContext, objName,
				objPath, offsetX, offsetY, offsetZ, scaleX, scaleY, scaleZ);
		meshCache.put(objName, mesh);
	//	Log.v("iLoongRobot", "loadMesh:" + objName);
	}

	public Mesh getMesh(Gdx gdx, Context widtgetContext, String objName,
			String objPath, float offsetX, float offsetY, float offsetZ,
			float scaleX, float scaleY, float scaleZ) {
		Mesh mesh = null;
		if (mObjType == LoadObjType.original) {
			mesh = WeatherHelper.loadOriginalMesh(gdx, widtgetContext, objName,
					objPath, offsetX, offsetY, 0, scaleX, scaleY, scaleZ,
					mSaveCompressedObj);

		} else if (mObjType == LoadObjType.compressed) {
			mesh = WeatherHelper.loadCompressedMesh(gdx, widtgetContext,
					objPath, offsetX, offsetY, offsetZ, scaleX, scaleY, scaleZ);
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
	//	metaData.width = mContext.getResources().getInteger(R.integer.width);
	//	metaData.height = mContext.getResources().getInteger(R.integer.height);
		return metaData;
	}
	
	
}
