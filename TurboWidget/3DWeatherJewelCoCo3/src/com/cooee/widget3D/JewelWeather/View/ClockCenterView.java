package com.cooee.widget3D.JewelWeather.View;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooee.widget3D.JewelWeather.Common.WeatherHelper;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Object3DBase;

public class ClockCenterView extends PluginViewObject3D {
	private static final String WATCH_CENTER_OBJ = "watch_center.obj";
	private Cache<String, Mesh> mMeshCache = null;
	private MainAppContext mAppContext;
	public CooGdx cooGdx;
	public ClockCenterView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name, region, WATCH_CENTER_OBJ);
		mAppContext = appContext;
		x = 0;
		y = 0;
		this.region.setRegion(region);
		this.width = WidgetWeather.MODEL_WIDTH;
		this.height = WidgetWeather.MODEL_HEIGHT;
		cooGdx = new CooGdx(appContext.mGdxApplication);
	}
}
