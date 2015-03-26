package com.iLoong.Robot.View;

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
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class RobotLeftHand extends PluginViewObject3D{
	private Timeline mStartAnimationTimeline;
	private Timeline mStopAnimationTimeline;
	public RobotLeftHand(MainAppContext appContext, String name,
			TextureRegion region, String objName) {
		super(appContext, name, region, objName);
		this.setSize(WidgetRobot.MODEL_WIDTH, WidgetRobot.MODEL_HEIGHT);
		this.setMoveOffset(WidgetRobot.MODEL_WIDTH / 2,
				WidgetRobot.MODEL_HEIGHT / 2, 0);
		super.build();
//		move(10.0F * WidgetRobot.SCALE_X, 0.0F, 0.0F);
//		if ( mesh != null)
//		{
//			mesh.scale(WidgetRobot.SCALE_X, WidgetRobot.SCALE_Y,
//					WidgetRobot.SCALE_Z);
//		}
		// TODO Auto-generated constructor stub
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
