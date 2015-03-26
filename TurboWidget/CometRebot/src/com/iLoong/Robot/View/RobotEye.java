package com.iLoong.Robot.View;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Robot.RobotHelper;
import com.iLoong.Robot.WidgetTimer;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class RobotEye extends PluginViewObject3D{

	private boolean isAnim = false;//判断动画是否执行
	private boolean iaCharAnim = false;//判断是否为充电状态 
	private TextureRegion smileRegion = null;
	private TextureRegion normalRegion = null;
	
	private Tween charAinmOneTween = null;
	private Tween charAnimTwoTween = null;
	
	private Tween eyeTween = null;
	private Tween smileTween = null;
	private Tween normalTween = null;
	private float eyebiTime = 1.5f;//正常眼睛显示的时间
	private float eyeSmileTime = 1.5f;//笑眼出现的时间
	private float eyeNormalTime = 0.2f;//眼睛消失的动画时间
	private MainAppContext appContext = null;
	private ChargerReceive chargerReceive = null;
	public RobotEye(MainAppContext appContext, String name, String textureName,
			String objName) {
		super(appContext, name, textureName, objName);
		this.appContext = appContext;
		this.setSize(WidgetRobot.MODEL_WIDTH, WidgetRobot.MODEL_HEIGHT);
		this.setMoveOffset(WidgetRobot.MODEL_WIDTH / 2,
				WidgetRobot.MODEL_HEIGHT / 2, 0);
		super.build();
		normalRegion = new TextureRegion(this.region);
		Texture texture = RobotHelper.getThemeTexture(appContext, "robot_eye_smile.png");
//		Log.v("cooee","normalRegion:" + this.region.getTexture() + " smileRegion:" + texture);
		smileRegion = new TextureRegion(texture);

//		if ( mesh != null)
//		{
//			mesh.scale(WidgetRobot.SCALE_X, WidgetRobot.SCALE_Y,
//					WidgetRobot.SCALE_Z);
//		}
		isAnim = true;
//		
		
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		filter.addAction(Intent.ACTION_POWER_CONNECTED);
		filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
		chargerReceive = new ChargerReceive();
		appContext.mGdxApplication.registerReceiver(chargerReceive, filter);
//		this.region.setRegion(smileRegion);
		
		// TODO Auto-generated constructor stub
	}
	
	public void resume() {
		isAnim = true;
		startEyeAinmNormal();
	}
	public void pause()
	{
		isAnim = false;
	}
	public void start() {
		//Log.e(TAG, "start");
		isAnim = true;
		startEyeAinmNormal();
	}
	@Override
	public void onEvent(int type, BaseTween source) {
		// TODO Auto-generated method stub
//		if ( source == eyeTween && type == TweenCallback.COMPLETE)
//		{
//			eyeTween = null;
//			startEyeSmileAnim();
//		}
		if (source == smileTween && type == TweenCallback.COMPLETE)
		{
			smileTween = null;
			startNormalEye();
		}
		if (source == normalTween && type == TweenCallback.COMPLETE)
		{
			normalTween = null;
			startEyeSmileAnim();
//			startEyeAinmNormal();
		}
		
		if (source == charAinmOneTween && type == TweenCallback.COMPLETE)
		{
			charAinmOneTween = null;
			startCharAinmOne();
		}
		super.onEvent(type, source);
	}
	
	private void startCharAnimTwo() {
		// TODO Auto-generated method stub
		charAnimTwoTween  = Tween.to(this, View3DTweenAccessor.OPACITY, 1f)
				.ease(Linear.INOUT).target(0f)
				.start(View3DTweenAccessor.manager).setCallback(this);
	}

	private float getRandom()
	{
		int num = (int) (Math.random() * 100);
		num = num % 4 + 2;
		Log.v("robot", "random is " + (float)num);
		return (float)num;
	}
	
	public void startNormalEye()
	{
		if ( isAnim)
		{
			
			this.color.a = 1f;
//			Log.v("robot", "robot is " + eyebiTime + " getrandom is " + getRandom() + " eyeNormalTime is " + eyeNormalTime + " eyeSmileTime is " + eyeSmileTime);
//			Log.v("", "robot 正常眼睛动画 ");
			this.region.setRegion(normalRegion);
			Gdx.graphics.requestRendering();
			eyebiTime = (float)(getRandom()) - eyeNormalTime - eyeSmileTime;
			
			normalTween = Tween.to(this, View3DTweenAccessor.OPACITY, (float)eyebiTime)
							.ease(Linear.INOUT).target(1f)
							.start(View3DTweenAccessor.manager).setCallback(this);
		}
		
	}
	public void startEyeSmileAnim() {
		// TODO Auto-generated method stub
		if ( isAnim )
		{
//			Log.v("", "robot 笑眼慢慢消失动画 ");
			this.region.setRegion(smileRegion);
			this.color.a = 1f;
			
			Gdx.graphics.requestRendering();
			smileTween = Tween.to(this, View3DTweenAccessor.OPACITY, eyeSmileTime)
				.ease(Linear.INOUT).target(1f)
				.start(View3DTweenAccessor.manager).setCallback(this);
			
		}
		
	}
	
	
	public void startEyeAinmNormal()
	{
//		if ( isAnim)
//		{
//			Log.v("", "robot 闭眼动画 ");
//			this.color.a = 0;
//			this.region.setRegion(normalRegion);
//			eyeTween = Tween.to(this, View3DTweenAccessor.OPACITY, eyeNormalTime)
//						.ease(Linear.INOUT).target(0f)
//						.start(View3DTweenAccessor.manager).setCallback(this);
//		}
//		
	}
	private void startCharAinmOne()
	{
		if( iaCharAnim)
		{
			this.color.a = 1;
			charAinmOneTween = Tween.to(this, View3DTweenAccessor.OPACITY, 1f)
							.ease(Linear.INOUT).target(0f)
							.start(View3DTweenAccessor.manager).setCallback(this);
		}
		
	}
	private class ChargerReceive extends BroadcastReceiver{
		String BatteryStatus = "";
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
//			if ( intent.getAction().equals(Intent.ACTION_POWER_CONNECTED))
//			{
//				Log.v("BatteryStatus", "BatteryStatus 电源连接");
//				int current = intent.getExtras().getInt("level");
//				if ( current != 100)
//				{
//					iaCharAnim = true;
//					isAnim = false;
//					RobotEye.this.region.setRegion(normalRegion);
//					startCharAinmOne();
//				}
//				
//			}
			if ( intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED))
			{
				iaCharAnim = false;
				isAnim = true;
				startNormalEye();
			}
			
			if ( intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED))
			{
//				isCharger = true;
				switch (intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN)) { 
				case BatteryManager.BATTERY_STATUS_CHARGING: 
					RobotEye.this.region.setRegion(normalRegion);
					iaCharAnim = true;
					isAnim = false;
					startCharAinmOne();
				break; 
				case BatteryManager.BATTERY_STATUS_DISCHARGING: BatteryStatus = "放电状态"; 
				break; 
				case BatteryManager.BATTERY_STATUS_NOT_CHARGING: BatteryStatus = "未充电"; 
				break; 
				case BatteryManager.BATTERY_STATUS_FULL: 
					BatteryStatus = "充满电";
					iaCharAnim = false;
					isAnim = false;
					RobotEye.this.region.setRegion(normalRegion);
					RobotEye.this.color.a = 1;
//					if ( RobotEye.this.color.a < 1)
//					{
//						RobotEye.this.color.a = 1;
//					}
					
//					RobotEye.this.region.setRegion(normalRegion);
//					isAnim = true;
//					startNormalEye();
					
//					iaCharAnim = true;
//					startCharAinmOne();
				break; 
				case BatteryManager.BATTERY_STATUS_UNKNOWN: BatteryStatus = "未知道状态"; 
				break; 
				} 
				
				Log.v("BatteryStatus", "BatteryStatus is " + BatteryStatus);

//				Log.v("Battery V", "BatteryStatus total is " + total + " current is " + current);
			}
			
		}
	}

	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
//		Log.v("cooee","texture region:" + this.region.getTexture());
		super.draw(batch, parentAlpha);
	}
}
