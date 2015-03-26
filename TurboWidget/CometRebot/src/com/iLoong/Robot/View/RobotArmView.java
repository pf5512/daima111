package com.iLoong.Robot.View;

import java.io.IOException;
import java.io.InputStream;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooeeui.cometrobot.R;
import com.iLoong.Robot.RobotHelper;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.ObjLoader;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class RobotArmView extends PluginViewObject3D {
	private Cache<String, Mesh> mMeshCache = null;
	private Timeline mStartAnimationTimeline;
	private Timeline mStopAnimationTimeline;

	public RobotArmView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name, region, "robot_arm.obj");
		x = 0;
		y = 0;

		this.setSize(WidgetRobot.MODEL_WIDTH, WidgetRobot.MODEL_HEIGHT);
		this.setMoveOffset(WidgetRobot.MODEL_WIDTH / 2,
				WidgetRobot.MODEL_HEIGHT / 2, 0);
		float armDeltaY = appContext.mWidgetContext.getResources()
				.getDimension(R.dimen.robot_arm_delta_y);
		float armDeltaX = appContext.mWidgetContext.getResources()
				.getDimension(R.dimen.robot_arm_delta_x);
		super.build();
		this.setOrigin(width / 2 + armDeltaX, width / 2 + armDeltaY);
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
					originalMesh = (Mesh) mMeshCache.get(WidgetRobot.ROBOT_ARM);
				}
				if (originalMesh == null) {
					originalMesh = RobotHelper.loadMesh(appContext,RobotHelper
									.getThemeObjPath(appContext.mThemeName,
											"robot_arm.obj"));
					if (WidgetRobot.useCache) {
						mMeshCache.put(WidgetRobot.ROBOT_ARM, originalMesh);
					}
				}
				if (WidgetRobot.useCache) {
					mesh = RobotHelper.copyMesh(originalMesh, appContext);
				} else {
					mesh = originalMesh;
				}
			} else {
				stream=RobotHelper.getThemeObjStream(appContext, "robot_arm.obj");
				mesh = (Mesh) ObjLoader.loadObj(appContext.gdx, stream, true);
				RobotHelper.move(mesh, 0, 0, WidgetRobot.MODEL_BACK_SCALE_Z);
				if (WidgetRobot.saveObj) {
					RobotHelper.saveMesh(mesh, "robot_arm.obj");
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

	public void startAinimation(String message) {
		this.setOriginZ(WidgetRobot.MODEL_BACK_SCALE_Z);
		this.setRotationVector(1, 0, 0);
		mStartAnimationTimeline = Timeline.createParallel();
		mStartAnimationTimeline.push(Tween
				.to(this, View3DTweenAccessor.ROTATION, 0.8f).ease(Bounce.OUT)
				.target(-160));
		mStartAnimationTimeline.push(Tween
				.to(this, View3DTweenAccessor.ROTATION, 0.8f).ease(Bounce.OUT)
				.target(0).delay(2f));
		mStartAnimationTimeline.start(View3DTweenAccessor.manager).setCallback(
				this);
		Handler handler = new Handler(
				appContext.mContainerContext.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast toast = Toast.makeText(appContext.mContainerContext,
						WidgetRobot.REBOT_MESSAGE, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.BOTTOM, toast.getXOffset() / 2,
						toast.getYOffset() / 2);
				toast.show();
			}
		});
	}

	public void stopAnimation() {
		mStopAnimationTimeline = Timeline.createParallel();
		mStopAnimationTimeline.push(Tween
				.to(this, View3DTweenAccessor.ROTATION, 0.5f).delay(-0.1f)
				.ease(Bounce.OUT).target(0));
		mStopAnimationTimeline.start(View3DTweenAccessor.manager).setCallback(
				this);
	}

	public boolean isTweenStopped() {
		if (this.mStartAnimationTimeline != null) {
			return false;
		}
		return true;
	}

	@Override
	public void onEvent(int type, @SuppressWarnings("rawtypes") BaseTween source) {
		// TODO Auto-generated method stub
		if (source.equals(this.mStartAnimationTimeline)
				&& type == TweenCallback.COMPLETE) {
//			mStartAnimationTimeline.free();	//xiatian del	//fix bug:0001990
			mStartAnimationTimeline = null;
			if (this.rotation != 0) {
				Log.v("Robot", "onEvent arm to down");
				Tween.to(this, View3DTweenAccessor.ROTATION, 0.8f)
						.ease(Bounce.OUT).target(0)
						.start(View3DTweenAccessor.manager);
			}
			WidgetRobot.mIsAnimationStopped = true;
		}
		if (source.equals(this.mStopAnimationTimeline)
				&& type == TweenCallback.COMPLETE) {
//			mStopAnimationTimeline.free();	//xiatian del	//fix bug:0001990
			mStopAnimationTimeline = null;
			WidgetRobot.mIsAnimationStopped = true;
		}
	}
}
