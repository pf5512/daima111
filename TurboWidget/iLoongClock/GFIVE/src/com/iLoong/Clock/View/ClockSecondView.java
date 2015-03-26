package com.iLoong.Clock.View;

import android.content.Context;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Clock.Common.ClockHelper;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Object3DBase;

public class ClockSecondView extends Object3DBase {
	public static final String WATCH_SECONDHAND_OBJ = "watch_secondhand.obj";
	private Cache<String, Mesh> mMeshCache = null;
	private MainAppContext mAppContext;

	public ClockSecondView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name);
		mAppContext = appContext;
		x = 0;
		y = 0;
		this.region.setRegion(region);
		this.width = WidgetClock.MODEL_WIDTH;
		this.height = WidgetClock.MODEL_HEIGHT;
		this.setOrigin(width / 2, height / 2);
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
		return ClockHelper.getMesh(mMeshCache, mAppContext,
				WATCH_SECONDHAND_OBJ, width / 2, height / 2, 0,
				WidgetClock.SCALE_X, WidgetClock.SCALE_Y, WidgetClock.SCALE_Z);
	}

	public void updateSecondView(float secondRotation) {
		setRotationAngle(0, 0, secondRotation - 90);
	}

	public boolean is3dRotation() {
		return true;
	}
}
