package com.iLoong.Robot.View;

import android.content.Context;
import android.util.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class RobotMessageLineView extends View3D {
	private RobotMessageView mRobotMessageView;
	private RobotArmView mRobotArmView;

	private Timeline mShowTimeline;
	private Timeline mStopTimeline;

	public RobotMessageLineView(String name, Context context,
			TextureRegion region) {
		super(name);
		x = 0;
		y = 0;
		this.region.setRegion(region);
		this.width = region.getRegionWidth();
		this.height = region.getRegionHeight();
		this.setOrigin(width / 2, height / 2);
	}

	public void setRobotMessageView(RobotMessageView robotMessageView) {
		this.mRobotMessageView = robotMessageView;
	}

	public void setRobotArmView(RobotArmView robotArmView) {
		this.mRobotArmView = robotArmView;
	}

	public void startShowAnimation() {
		Log.e("message", "startShowAnimation");
		float scaleToX = this.mRobotMessageView.getWidth() / this.getWidth();
		this.scaleX = 1;
		this.scaleY = 1;
		this.show();

		mShowTimeline = Timeline.createParallel();
		mShowTimeline.push(Tween.to(this, View3DTweenAccessor.SCALE_XY, 0.1f)
				.ease(Quad.INOUT).target(scaleToX, 1));
		mShowTimeline.start(View3DTweenAccessor.manager)
				.setCallbackTriggers(TweenCallback.COMPLETE).setCallback(this);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onEvent(int type, BaseTween source) {
		// TODO Auto-generated method stub
		if (source.equals(this.mShowTimeline) && TweenCallback.COMPLETE == type) {
			mShowTimeline.free();
			mShowTimeline = null;
			this.hide();
			this.mRobotMessageView.startShowAnimation();
		} else if (source.equals(this.mStopTimeline)
				&& TweenCallback.COMPLETE == type) {
			mStopTimeline.free();
			mStopTimeline = null;
			this.hide();
			if (this.mRobotArmView != null) {
				this.mRobotArmView.stopAnimation();
			}
		}
	}

	public void stopShowAnimation() {
		this.show();
		// TODO Auto-generated method stub
		mStopTimeline = Timeline.createParallel();

		mStopTimeline.push(Tween.to(this, View3DTweenAccessor.SCALE_XY, 0.1f)
				.ease(Quad.INOUT).target(1, 1));
		mStopTimeline.start(View3DTweenAccessor.manager)
				.setCallbackTriggers(TweenCallback.COMPLETE).setCallback(this);
	}
}
