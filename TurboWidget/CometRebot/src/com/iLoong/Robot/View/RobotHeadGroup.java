package com.iLoong.Robot.View;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Robot.RobotHelper;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class RobotHeadGroup extends WidgetPluginView3D {
	private MainAppContext mAppContext;
	private RobotheadView mRobotHeadView = null;
	private RobotFace mRobotFaceView = null;
	private RobotEye mRobotEye = null;
	private TextureRegion mBackRegion;
	private WidgetRobot mRobot;
	private Timeline mStartAnimationTimeline;
	public static final float mAnimationDuration = 0.5f;
	
	public RobotHeadGroup(String name,MainAppContext appContext,WidgetRobot robot) {
		super(name);
		// TODO Auto-generated constructor stub
		this.mAppContext = appContext;
		this.mRobot = robot;
		this.width = WidgetRobot.MODEL_WIDTH;
		this.height = WidgetRobot.MODEL_HEIGHT;
		this.setOrigin(this.width / 2, this.height / 2);
		
		if (mBackRegion == null) {
			Texture texture = RobotHelper.getThemeTexture(mAppContext,
					"robot_body.png");
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			mBackRegion = new TextureRegion(texture);
		}
		
		addRobotHead();
		addRobotFace();
		addRobotEye();
	}
	
	private void addRobotHead() {
		// TODO Auto-generated method stub
		mRobotHeadView = new RobotheadView("headView",mAppContext, mBackRegion);
		this.addView(mRobotHeadView);
		mRobotHeadView.bringToFront();
	}
	
	private void addRobotFace() {
		// TODO Auto-generated method stub
		mRobotFaceView = new RobotFace(mAppContext, "faceView","face.png","face.obj");
		this.addView(mRobotFaceView);
		mRobotFaceView.bringToFront();
	}
	
	private void addRobotEye() {
		// TODO Auto-generated method stub
		mRobotEye = new RobotEye(mAppContext, "mRobotEye", "robot_eye.png", "face.obj");
		this.addView(mRobotEye);
		mRobotEye.bringToFront();
		
	}
	
	public void startAnimation() {
		this.setRotationVector(0, 1, 0);
		mStartAnimationTimeline = Timeline.createParallel();
		mStartAnimationTimeline.push(Tween
				.to(this, View3DTweenAccessor.ROTATION, mAnimationDuration).ease(Linear.INOUT)
				.target(-360));
//		mStartAnimationTimeline.push(Tween
//				.to(this, View3DTweenAccessor.ROTATION, 0.8f).ease(Bounce.OUT)
//				.target(0).delay(2f));
		mStartAnimationTimeline.start(View3DTweenAccessor.manager).setCallback(
				this);
	}
	
	public Timeline getTimeLine(){
		return mStartAnimationTimeline;
	}

	public void stopAnimation() {
		if (mStartAnimationTimeline != null) {
			mStartAnimationTimeline.free();
			mStartAnimationTimeline = null;
		}
		this.rotation = 0;
	}

	@Override
	public void onEvent(int type, BaseTween source) {
		if (source.equals(mStartAnimationTimeline)
				&& type == TweenCallback.COMPLETE) {
			stopAnimation();
			this.mRobot.showClearResult();
			return;
		}
		// TODO Auto-generated method stub
		super.onEvent(type, source);
	}

	@Override
	public void onDelete() {
		// TODO Auto-generated method stub

	}

}
