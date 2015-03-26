package com.iLoong.ThemeClock.View;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.ThemeClock.Common.ClockHelper;
import com.iLoong.ThemeClock.Common.ThemeParse;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Object3DBase;

public class ClockSecondView extends Object3DBase {
	public static final String WATCH_SECONDHAND_OBJ = "watch_secondhand.obj";
	private Cache<String, Mesh> mMeshCache = null;
	private MainAppContext mAppContext;
	private Context mThemeContext;
	private ThemeParse mThemeParse;
	public ClockSecondView(String name, MainAppContext appContext,
			TextureRegion region,Context themeContext, ThemeParse themeParse) {
		super(appContext, name);
		mAppContext = appContext;
		this.mThemeContext = themeContext;
		this.mThemeParse = themeParse;
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
	//	ClockHelper.move(mesh, 0, 0, -45);
		setMesh(mesh);
		enableDepthMode(true);
	}

	public Mesh getMesh() {
		return ClockHelper.getMesh(mMeshCache, mAppContext,mThemeContext,
				WATCH_SECONDHAND_OBJ, width / 2, height / 2, 0,
				WidgetClock.SCALE_X, WidgetClock.SCALE_Y, WidgetClock.SCALE_Z);
	}

	public void updateSecondView(float secondRotation) {
		Log.v("asdf", "updateSecondView= "+ secondRotation);
		setRotationAngle(0, 0, secondRotation - 90);
	}

	public boolean is3dRotation() {
		return true;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		super.draw(batch, parentAlpha);
		Log.v("asdf", "ClockSecondView draw");
	}
	
	
}
