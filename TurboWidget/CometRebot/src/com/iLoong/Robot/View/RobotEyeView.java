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

public class RobotEyeView extends PluginViewObject3D implements
		WidgetTimerListener, IFrameRefreshCallback {
	private static String TAG = "RobotEyeView";

	private Cache<String, Mesh> mMeshCache = null;
	private FrameAnimation mFrameAnimation = null;
	private WidgetTimer mTimer = null;
	private boolean mRobotEyeEnable = false;
	private IRefreshRender mRefreshRender = null;
	private TextureRegion[] walksFrame = null;
	private boolean mShowEyeAlternate = false;
	private Intent mBatteryStickyIntent = null;
	private TextureRegion mOriginalRegion = null;

	public void setBatteryStickIntent(Intent intent) {
		mBatteryStickyIntent = intent;
	}

	public void setRefreshRender(IRefreshRender refreshRender) {
		this.mRefreshRender = refreshRender;
	}

	public RobotEyeView(String name, MainAppContext appContext) {
		super(appContext, name, "robot_eye_3.png", "robot_eye.obj");
		this.setSize(WidgetRobot.MODEL_WIDTH, WidgetRobot.MODEL_HEIGHT);
		this.setMoveOffset(WidgetRobot.MODEL_WIDTH / 2,
				WidgetRobot.MODEL_HEIGHT / 2, 0);
		super.build();

		mOriginalRegion = this.region;
		Texture texture1 = RobotHelper.getThemeTexture(appContext,
				"robot_eye_1.png");
		Texture texture2 = RobotHelper.getThemeTexture(appContext,
				"robot_eye_2.png");
		Texture texture3 = RobotHelper.getThemeTexture(appContext,
				"robot_eye_3.png");

		TextureRegion region1;
		TextureRegion region2;
		TextureRegion region3;
		region1 = new TextureRegion(texture1);
		region2 = new TextureRegion(texture2);
		region3 = new TextureRegion(texture3);
		walksFrame = new TextureRegion[3];
		int frameCount = 3;
		for (int i = 0; i < frameCount; i++) {
			if (i % frameCount == 0) {
				walksFrame[i] = region1;
			} else if (i % frameCount == 1) {
				walksFrame[i] = region2;
			} else if (i % frameCount == 2) {
				walksFrame[i] = region3;
			}
		}
		// mFrameAnimation = new Animation(0.25f, walksFrame);
		mFrameAnimation = new FrameAnimation(walksFrame);
		mFrameAnimation.callback = this;
		// mFrameAnimation.start();
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		super.draw(batch, parentAlpha);
		if (!mShowEyeAlternate) {
			this.region.setRegion(mOriginalRegion);
		}
	}

	public void renderMesh(float dx, float dy) {
		InputStream stream = null;
		Mesh mesh = null;
		try {
			if (!WidgetRobot.loadOriginalObj) {
				Mesh originalMesh = null;
				if (WidgetRobot.useCache) {
					originalMesh = (Mesh) mMeshCache.get(WidgetRobot.ROBOT_EYE);
				}
				if (originalMesh == null) {
					originalMesh = RobotHelper.loadMesh(appContext, RobotHelper
							.getThemeObjPath(appContext.mThemeName,
									"robot_eye.obj"));
					if (WidgetRobot.useCache) {
						mMeshCache.put(WidgetRobot.ROBOT_EYE, originalMesh);
					}
				}
				if (WidgetRobot.useCache) {
					mesh = RobotHelper.copyMesh(originalMesh, appContext);
				} else {
					mesh = originalMesh;
				}

			} else {
				stream=RobotHelper.getThemeObjStream(appContext, "robot_eye.obj");
				mesh = (Mesh) ObjLoader.loadObj(appContext.gdx, stream, true);
				RobotHelper.move(mesh, 0, 0, WidgetRobot.MODEL_BACK_SCALE_Z);
				if (WidgetRobot.saveObj) {
					RobotHelper.saveMesh(mesh, "robot_eye.obj");
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

	public void setMeshCache(Cache<String, Mesh> cache) {
		this.mMeshCache = cache;
	}

	public void resume() {
		if (mRobotEyeEnable && mShowEyeAlternate) {
			if (mTimer == null) {
				start();
			} else {
				mTimer.resume();
			}
			mShowEyeAlternate = true;
		}
	}

	public void pause() {
		//Log.v(TAG, "pause");
		if (mTimer != null) {
			mTimer.pause();
		}

		if (mFrameAnimation != null) {
			mFrameAnimation.pause();
		}
		mShowEyeAlternate = false;
	}

	public void start() {
		//Log.e(TAG, "start");
		mRobotEyeEnable = true;
		mShowEyeAlternate = true;
		if (mTimer == null) {
			mTimer = new WidgetTimer("RobotEyeTimer", this, 2000);
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
		mRobotEyeEnable = false;
		mShowEyeAlternate = false;
	}

	@Override
	public void timeChanged() {
		// mShowEyeAlternate = true;
		// Log.e("eyeView", "timeChanged");
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
				mRobotEyeEnable = true;
				// mShowEyeAlternate = true;
			} else {
				mRobotEyeEnable = false;
				// mShowEyeAlternate = false;
			}
		} else {
			mRobotEyeEnable = true;
		}
	}

	@Override
	public void refreshRegion(final TextureRegion region) {
		// TODO Auto-generated method stub
		if (mRefreshRender != null) {
			mRefreshRender.RefreshRender();
		}
		// TODO Auto-generated method stub
		appContext.mGdxApplication.postRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				RobotEyeView.this.region.setRegion(region);
			}
		});
	}

	public void dispose() {
		super.dispose();
		if (mFrameAnimation != null && walksFrame.length > 0) {
			for (int i = 0; i < walksFrame.length; i++) {
				Texture tempTexture = (Texture) walksFrame[i].getTexture();
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
		// TODO Auto-generated method stub

	}

	@Override
	public void beginRefreshRegion() {
		// TODO Auto-generated method stub

	}
}
