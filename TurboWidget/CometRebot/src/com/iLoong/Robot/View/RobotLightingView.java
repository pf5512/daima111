package com.iLoong.Robot.View;

import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Robot.RobotHelper;
import com.iLoong.Robot.WidgetTimer;
import com.iLoong.Robot.WidgetTimerListener;
import com.iLoong.Robot.View.FrameAnimation.IFrameRefreshCallback;
import com.iLoong.launcher.UI3DEngine.adapter.IRefreshRender;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.ObjLoader;

public class RobotLightingView extends PluginViewObject3D implements
		WidgetTimerListener, IFrameRefreshCallback {
	private static String TAG = "RobotLightingView";
	// 模型缓存
	private Cache<String, Mesh> mMeshCache = null;

	// 动画定时器
	private WidgetTimer mTimer = null;

	// 是否显示闪电动画
	private boolean mLightingEnable = true;

	private Intent mBatteryStickyIntent;

	private IRefreshRender mRefreshRender = null;

	private TextureRegion[] mFrameTextures;

	private FrameAnimation mFrameAnimation;

	private TextureRegion mOriginalRegion = null;

	public void setRefreshRender(IRefreshRender refreshRender) {
		this.mRefreshRender = refreshRender;
	}

	public RobotLightingView(String name, MainAppContext appContext) {
		super(appContext, name, "robot_lighting_4.png", "robot_lighting.obj");

		this.setSize(WidgetRobot.MODEL_WIDTH, WidgetRobot.MODEL_HEIGHT);

		this.setMoveOffset(WidgetRobot.MODEL_WIDTH / 2,
				WidgetRobot.MODEL_HEIGHT / 2, 0);
		super.build();

		mOriginalRegion = region;
		AndroidFiles gdxFiles = new AndroidFiles(
				appContext.mWidgetContext.getAssets());

		Texture texture1 = RobotHelper.getThemeTexture(appContext,
				"robot_lighting_1.png");
		Texture texture2 = RobotHelper.getThemeTexture(appContext,
				"robot_lighting_2.png");
		Texture texture3 = RobotHelper.getThemeTexture(appContext,
				"robot_lighting_3.png");
		Texture texture4 = RobotHelper.getThemeTexture(appContext,
				"robot_lighting_4.png");

		TextureRegion region1;
		TextureRegion region2;
		TextureRegion region3;
		TextureRegion region4;

		region1 = new TextureRegion(texture1);
		region2 = new TextureRegion(texture2);
		region3 = new TextureRegion(texture3);
		region4 = new TextureRegion(texture4);
		int frameCount = 4;
		mFrameTextures = new TextureRegion[frameCount];
		for (int i = 0; i < frameCount; i++) {
			if (i % frameCount == 0) {
				mFrameTextures[i] = region1;
			} else if (i % frameCount == 1) {
				mFrameTextures[i] = region2;
			} else if (i % frameCount == 2) {
				mFrameTextures[i] = region3;
			} else if (i % frameCount == 3) {
				mFrameTextures[i] = region4;
			}
		}

		mFrameAnimation = new FrameAnimation(mFrameTextures);
		mFrameAnimation.callback = this;

	}

	public boolean isLightingEnable() {
		return mLightingEnable;
	}

	public void setLightingEnable(boolean mLightingEnable) {
		this.mLightingEnable = mLightingEnable;
	}

	public void setBatteryStickIntent(Intent intent) {
		mBatteryStickyIntent = intent;
	}

	public void start() {
		//Log.e(TAG, "start");
		mLightingEnable = true;
		if (mTimer == null) {
			mTimer = new WidgetTimer("RobotLightingTimer", this, 3000);
			mTimer.start();
		} else {
			mTimer.resume();
		}
	}

	public void stop() {
		//Log.v(TAG, "onStop");
		if (mTimer != null) {
			mTimer.stop();
			mTimer = null;
		}
		if (mFrameAnimation != null) {
			mFrameAnimation.stop();
		}
		mLightingEnable = false;
		this.hide();
	}

	public void resume() {
		if (mLightingEnable) {
			if (mTimer == null) {
				start();
			} else {
				mTimer.resume();
			}
			// mShowLightingAlternate = true;
		}
	}

	public void pause() {
		Log.v(TAG, "pause");
		if (mTimer != null) {
			mTimer.pause();
		}

		if (mFrameAnimation != null) {
			mFrameAnimation.pause();
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		super.draw(batch, parentAlpha);
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
							.get(WidgetRobot.ROBOT_LIGHTING);
				}
				if (originalMesh == null) {
					originalMesh = RobotHelper.loadMesh(appContext, RobotHelper
							.getThemeObjPath(appContext.mThemeName,
									"robot_lighting.obj"));
					if (WidgetRobot.useCache) {
						mMeshCache
								.put(WidgetRobot.ROBOT_LIGHTING, originalMesh);
					}
				}
				if (WidgetRobot.useCache) {
					mesh = RobotHelper.copyMesh(originalMesh, appContext);
				} else {
					mesh = originalMesh;
				}

			} else {
				stream = RobotHelper.getThemeObjStream(appContext,
						"robot_lighting.obj");
				mesh = (Mesh) ObjLoader.loadObj(appContext.gdx, stream, true);
				RobotHelper.move(mesh, 0, 0, WidgetRobot.MODEL_BACK_SCALE_Z);
				if (WidgetRobot.saveObj) {
					RobotHelper.saveMesh(mesh, "robot_lighting.obj");
				}
				stream.close();
			}
			// 屏幕适配
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
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void timeChanged() {
		// Log.e("lightingView", "timeChanged");
		if (mFrameAnimation != null) {
			mFrameAnimation.start();
		}
		if (mRefreshRender != null) {
			mRefreshRender.RefreshRender();
		}
		if (mBatteryStickyIntent == null) {
			try {
				mBatteryStickyIntent = appContext.mWidgetContext
						.registerReceiver(null, new IntentFilter(
								Intent.ACTION_BATTERY_CHANGED));
			} catch (Exception e) {

			}
		}
		int plugged = mBatteryStickyIntent.getIntExtra("plugged", 0);
		int level = mBatteryStickyIntent.getIntExtra("level", 0);
		if (plugged == BatteryManager.BATTERY_PLUGGED_AC
				|| plugged == BatteryManager.BATTERY_PLUGGED_USB) {
			if (level == 100) {
				mLightingEnable = false;
			} else {
				mLightingEnable = true;
			}
		} else {
			mLightingEnable = false;
		}
		if (!mLightingEnable) {
			this.hide();
		}

	}

	@Override
	public void refreshRegion(final TextureRegion region) {
		if (mRefreshRender != null) {
			mRefreshRender.RefreshRender();
		}
		// TODO Auto-generated method stub
		appContext.mGdxApplication.postRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				RobotLightingView.this.region.setRegion(region);
			}
		});
	}

	public void dispose() {
		super.dispose();
		if (mFrameTextures != null && mFrameTextures.length > 0) {
			for (int i = 0; i < mFrameTextures.length; i++) {
				Texture tempTexture = (Texture) mFrameTextures[i].getTexture();
				if (tempTexture != null) {
					tempTexture.dispose();
				}
			}
		}
		if (mOriginalRegion != null) {
			mOriginalRegion.getTexture().dispose();
		}
		if (mTimer != null) {
			mTimer.stop();
			mTimer = null;
		}
		if (mFrameAnimation != null) {
			mFrameAnimation.stop();
			mFrameAnimation = null;
		}
	}

	@Override
	public void endRefreshRegion() {
		// Log.e(TAG, "endRefreshRegion");
		if (mRefreshRender != null) {
			mRefreshRender.RefreshRender();
		}
		this.hide();
		// Log.e(TAG, "visible:" + this.visible);
	}

	@Override
	public void beginRefreshRegion() {
		if (mRefreshRender != null) {
			mRefreshRender.RefreshRender();
		}
		this.show();
		// Log.e(TAG, "visible:" + this.visible);
	}
}
