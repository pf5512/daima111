package com.iLoong.ThemeClock.View;

import android.content.Context;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.ThemeClock.Common.ClockHelper;
import com.iLoong.ThemeClock.Common.ThemeParse;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Object3DBase;

public class ClockMinuteView extends Object3DBase {
	public static final String WATCH_MINUTEHAND_OBJ = "watch_minutehand.obj";
	private Cache<String, Mesh> mMeshCache = null;
	private MainAppContext mAppContext;
	private Context mThemeContext;
	private ThemeParse mThemeParse;
	public ClockMinuteView(String name, MainAppContext appContext,
			TextureRegion region,Context themeContext, ThemeParse themeParse) {
		super(appContext, name);
		mAppContext = appContext;
		this.mThemeContext = themeContext;
		this.mThemeParse = themeParse;
		x = 0;
		y = 0;
		x = 0;
		y = 0;
		this.width = WidgetClock.MODEL_WIDTH;
		this.height = WidgetClock.MODEL_HEIGHT;
		this.setOrigin(width / 2, height / 2);
		this.region.setRegion(region);
	}

	public void setMeshCache(Cache<String, Mesh> cache) {
		this.mMeshCache = cache;
	}

	public void renderMesh(float dx, float dy) {
		Mesh mesh = getMesh();
		// if (WidgetClock.SCALE_SIZE != 1) {
		// mesh.scale(WidgetClock.SCALE_X, WidgetClock.SCALE_Y,
		// WidgetClock.SCALE_Z);
		// }
		setMesh(mesh);
		enableDepthMode(true);
	}

	public Mesh getMesh() {
		return ClockHelper.getMesh(mMeshCache, mAppContext, mThemeContext,
				WATCH_MINUTEHAND_OBJ, width / 2,
				height / 2, 0, WidgetClock.SCALE_X, WidgetClock.SCALE_Y,
				WidgetClock.SCALE_Z);
	}

	
	public void updateMinuteView(float minuteRotation) {
		setRotationAngle(0, 0, minuteRotation - 90);
	}

	public boolean is3dRotation() {
		return true;
	}
}
