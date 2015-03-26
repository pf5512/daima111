package com.iLoong.ThemeClock.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.provider.Settings;
import android.util.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.ThemeClock.R;
import com.iLoong.ThemeClock.Common.ClockHelper;
import com.iLoong.ThemeClock.Common.ImageHelper;
import com.iLoong.ThemeClock.Common.ThemeParse;
import com.iLoong.ThemeClock.DigitClock.DigitClock;
import com.iLoong.ThemeClock.Simulator.SimulatorClock;
import com.iLoong.ThemeClock.Timer.ClockTimer;
import com.iLoong.ThemeClock.Timer.ClockTimerHandler;
import com.iLoong.ThemeClock.Timer.ClockTimerListener;
import com.iLoong.ThemeClock.Timer.ClockTimerReceiver;
import com.iLoong.ThemeClock.Timer.ClockTimerService;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.UI3DEngine.adapter.Texture;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.CacheManager;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.min3d.Object3DBase;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class WidgetClock extends WidgetPluginView3D implements
		ClockTimerListener {
	private static final String TAG = "WidgetClock";
	public static final String MESH_CACHE_NAME = "com.iLoong.Clock.meshCache";
	public static final String TEXTURE_CACHE_NAME = "com.iLoong.Clock.textureCache";
	public static final String WATCH_BACK_TEXTURE = "watch_back.png";
	public String THEME_NAME = "iLoong";
	// 3D模型缩放比例
	public static float SCALE_SIZE = 1F;
	public static float SCALE_X = 1F;
	public static float SCALE_Y = 1F;
	public static float SCALE_Z = 1F;

	// 加载原始模型
	public static boolean loadOriginalObj = false;

	// 加载压缩模型
	public static boolean saveCompressObj = false;

	// 3D模型统一宽度高度
	public static float MODEL_WIDTH = 320;
	public static float MODEL_HEIGHT = 320;

	// widget自身运行上下文环境
	private Context mContext = null;

	// 定时器类型
	private TimerTypeEnum mTimer = TimerTypeEnum.timerTask;

	// 当前时间小时时刻
	private int mCurrentHour;

	// 当前时间分钟时刻
	private int mCurrentMinute;

	// 当前时间秒钟时刻
	private int mCurrentSecond;

	// 时钟背景
	private ClockBackView mClockBackView = null;
	private ClockBackAhplaView mClockBackAhplaView = null;
	private ClockBackNOAhplaView mClockBackNoAhplaView = null;
	
	// 时钟时针
	private ClockHourView mClockHourView = null;

	// 时钟分针
	private ClockMinuteView mClockMinuteView = null;

	// 时钟秒针
	private ClockSecondView mClockSecondView = null;

	// 时钟背景纹理
	private TextureRegion mBackRegion = null;

	// 时钟定时器
	private ClockTimer mClockTimer = null;

	// 滚动时钟记录当前位移
	private float mRotateX = 0;
	private float mRotateY = 0;

	// 时钟滑动动画
	Tween mScrollTween = null;

	// 是否处理点击事件
	private boolean mIsOnClickEvent = false;

	// 点击时钟动画
	private Tween onClickDownTween = null;
	private Tween onClickUpTween = null;
	private Tween onClickReboundOne = null;
	private Tween onClickReboundTwo = null;
	private Tween onClickStopTween = null;

	private Cache<String, Mesh> mMeshCache = null;
	private Cache<String, TextureRegion> mTextureCache = null;
	// 是否使用缓存
	public static boolean useCache = false;
	private boolean isTouchdownTrigered = false;

	public MainAppContext mAppContext;
	public CooGdx cooGdx;
	private BitmapTexture texture;

	//add weijie
	private SimulatorClock mSimulatorClock = null;
	private DigitClock mDigitClock = null;
	private int mselect = 0;
	private ThemeParse mThemeParse = null;
	private Context mThemeContext = null;
	//add end
	@SuppressWarnings("unchecked")
	public WidgetClock(String name, MainAppContext globalContext, int widgetId) {
		super(name);
		long start = System.currentTimeMillis();
		mAppContext = globalContext;
		this.mContext = globalContext.mWidgetContext;
		THEME_NAME = globalContext.mThemeName;
		cooGdx = new CooGdx(globalContext.mGdxApplication);
		
		
		try
		{
			mThemeParse = new ThemeParse(globalContext.mContainerContext);
			mselect = mThemeParse.getInteger("widget_clock_mode");//widget_clock_mode
			mThemeContext = mThemeParse.getThemeContext();
			//have not installed the theme,
			//getgetThemeContext is null;
			if(mThemeContext == null)
				mThemeContext = mAppContext.mContainerContext;	
		}
		catch(Exception ex)
		{
		    Log.v("context", " ex "+mselect);
			mselect = 0;
		}
		Log.e("ddddff", "mThemeContext="+mThemeContext);	
		if(mThemeContext == null)
			mThemeContext = mAppContext.mContainerContext;	
			
		Log.e("ddddff", "mThemeContext packname="+mThemeContext.getPackageName());	
		// 初始化时钟模型宽度高度
		MODEL_WIDTH = mContext.getResources().getDimension(R.dimen.clock_width);
		MODEL_HEIGHT = mContext.getResources().getDimension(
				R.dimen.clock_height);
		

        
		Log.e("ddddff", "MODEL_WIDTH"+MODEL_WIDTH+" ,MODEL_HEIGHT"+MODEL_HEIGHT);  
		if(Utils3D.getScreenHeight()<800){
		    SCALE_X = SCALE_Y = SCALE_Z = SCALE_SIZE = ((float) mContext
	                //      .getResources().getDisplayMetrics().density / (float) 1.5 * (float) 0.75);
	                //.getResources().getDisplayMetrics().density / (float) 1.7);
	                .getResources().getDisplayMetrics().density / (float) 3);
		}
		else{
		    SCALE_X = SCALE_Y = SCALE_Z = SCALE_SIZE = ((float) mContext
				//		.getResources().getDisplayMetrics().density / (float) 1.5 * (float) 0.75);
				.getResources().getDisplayMetrics().density / (float) 1.7);

		}

		this.width = MODEL_WIDTH;
		this.height = MODEL_HEIGHT;
		Log.e("ddddff", "THEME_NAME="+THEME_NAME);	
		if(mselect == 1)
		{
			mSimulatorClock = new SimulatorClock("SimulatorClock",mAppContext,mThemeContext,mThemeParse);
			this.addView(mSimulatorClock);
			
			// 启动定时器
			mTimer = TimerTypeEnum.timerTask;
			startClockTimer();
			
		}
		else if(mselect == 2)
		{
			mDigitClock = new DigitClock("DigitClock",mAppContext,mThemeContext,mThemeParse);
			this.addView(mDigitClock);
			//mDigitClock.bringToFront();
			mTimer = TimerTypeEnum.timerTask;
			startClockTimer();
		}
		else
		{
		
		
		
		
		this.setOrigin(this.width / 2, this.height / 2);

		// this.setBackgroud(new NinePatch(new
		// Texture3D(globalContext.gdx,ImageHelper
		// .bmp2Pixmap(ImageHelper.getImageFromResource(mContext,
		// R.drawable.red)))));

		// 创建纹理，后面传递给各个子view，公共一张纹理
		mMeshCache = ((Cache<String, Mesh>) CacheManager
				.getCache(MESH_CACHE_NAME));
		mTextureCache = (Cache<String, TextureRegion>) CacheManager
				.getCache(TEXTURE_CACHE_NAME);

		mMeshCache.clear();
		mTextureCache.clear();

		if (useCache) {
			mBackRegion = mTextureCache.get(WATCH_BACK_TEXTURE);
		}
		if (mBackRegion == null) 
		{
			texture = new BitmapTexture(ImageHelper.getImageFromAssetsFile(mThemeContext,"theme/widget/clock/iLoong/image/" + WATCH_BACK_TEXTURE));
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			mBackRegion = new TextureRegion(texture);
			if (useCache) {
				mTextureCache.put(WATCH_BACK_TEXTURE, mBackRegion);
			}
		}

		// 添加背景，时针，分针， 秒针
		addClockBackNoAhpla();
		addClockBackAhpla();
	//	addClockBack();
		addHourHandView();
		addMinuteHandView();
		addSecondHandView();
		
		// 启动定时器
		mTimer = TimerTypeEnum.timerTask;
		startClockTimer();

		long end = System.currentTimeMillis(); // 排序后取得当前时间
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(end - start);
		Log.v("创建时钟总时间",
				"耗时: " + c.get(Calendar.MINUTE) + "分 " + c.get(Calendar.SECOND)
						+ "秒 " + c.get(Calendar.MILLISECOND) + " 微秒");
		Utils3D.showPidMemoryInfo(mAppContext.mContainerContext,
				"com.iLoong.Clock");
		}
	}

	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		// batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
//		super.draw(batch,parentAlpha);
/*
		if(mSimulatorClock !=null)
		{
			return false;		
		}
		else if(mDigitClock != null)
		{
			return false;
		}
		else
		*/
		if(true)
		{
		cooGdx.gl.glDepthMask(true);
		cooGdx.gl.glEnable(GL10.GL_DEPTH_TEST);
		cooGdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓存
		cooGdx.gl.glDepthFunc(GL10.GL_LEQUAL);
		super.draw(batch, parentAlpha);
		cooGdx.gl.glDisable(GL10.GL_DEPTH_TEST);
		cooGdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓存
		cooGdx.gl.glDepthMask(false);
		}
	}

	/**
	 * 添加时钟Ahpla背景
	 */
	private void addClockBackAhpla() {
		// long timeStart = System.currentTimeMillis();	
		mClockBackAhplaView = new ClockBackAhplaView("clockBackAhpla", mAppContext,
				this.mBackRegion,mThemeContext,mThemeParse);
		mClockBackAhplaView.setMeshCache(mMeshCache);
		mClockBackAhplaView.renderMesh(this.x, this.y);
		this.addView(mClockBackAhplaView);
		mClockBackAhplaView.bringToFront();
	}
	/**
	 * 添加时钟no Ahpla背景
	 */
	private void addClockBackNoAhpla() {
		// long timeStart = System.currentTimeMillis();

		mClockBackNoAhplaView = new ClockBackNOAhplaView("clockBackNoAhpla", mAppContext,
				this.mBackRegion,mThemeContext,mThemeParse);
		mClockBackNoAhplaView.setMeshCache(mMeshCache);
		mClockBackNoAhplaView.renderMesh(this.x, this.y);
		this.addView(mClockBackNoAhplaView);
		mClockBackNoAhplaView.bringToFront();
	}
	
	/**
	 * 添加时钟背景
	 */
	private void addClockBack() {
		// long timeStart = System.currentTimeMillis();

		mClockBackView = new ClockBackView("clockBack", mAppContext,
				this.mBackRegion);
		mClockBackView.setMeshCache(mMeshCache);
		mClockBackView.renderMesh(this.x, this.y);
		this.addView(mClockBackView);
		mClockBackView.bringToFront();
	}

	/**
	 * 添加时钟时针
	 */
	private void addHourHandView() {
		// long timeStart = System.currentTimeMillis();

		mClockHourView = new ClockHourView("clockHour", mAppContext,
				this.mBackRegion,mThemeContext,mThemeParse);
		mClockHourView.setMeshCache(mMeshCache);
		mClockHourView.renderMesh(this.x, this.y);
		this.addView(mClockHourView);
		mClockHourView.bringToFront();

		// long timeEnd = System.currentTimeMillis();
		// Calendar backCalendar = Calendar.getInstance();
		// backCalendar.setTimeInMillis(timeEnd - timeStart);
		// Log.v("创建时针总时间",
		// "耗时: " + backCalendar.get(Calendar.MINUTE) + "分 "
		// + backCalendar.get(Calendar.SECOND) + "秒 "
		// + backCalendar.get(Calendar.MILLISECOND) + " 微秒");
		// Log.v("**", "***********************************************");
	}

	/**
	 * 添加时钟分针
	 */
	private void addMinuteHandView() {
		// long timeStart = System.currentTimeMillis();

		mClockMinuteView = new ClockMinuteView("clockMinute", mAppContext,
				this.mBackRegion,mThemeContext,mThemeParse);
		mClockMinuteView.setMeshCache(mMeshCache);
		mClockMinuteView.renderMesh(this.x, this.y);
		this.addView(mClockMinuteView);
		mClockMinuteView.bringToFront();
		//
		// long timeEnd = System.currentTimeMillis();
		// Calendar backCalendar = Calendar.getInstance();
		// backCalendar.setTimeInMillis(timeEnd - timeStart);
		// Log.v("创建分针总时间",
		// "耗时: " + backCalendar.get(Calendar.MINUTE) + "分 "
		// + backCalendar.get(Calendar.SECOND) + "秒 "
		// + backCalendar.get(Calendar.MILLISECOND) + " 微秒");
		// Log.v("**", "***********************************************");
	}

	/**
	 * 添加时钟秒针
	 */
	private void addSecondHandView() {
		// long timeStart = System.currentTimeMillis();

		mClockSecondView = new ClockSecondView("clockSecond", mAppContext,
				this.mBackRegion,mThemeContext,mThemeParse);
		mClockSecondView.setMeshCache(mMeshCache);
		mClockSecondView.renderMesh(this.x, this.y);
		this.addView(mClockSecondView);
		this.bringToFront();

		// long timeEnd = System.currentTimeMillis();
		// Calendar backCalendar = Calendar.getInstance();
		// backCalendar.setTimeInMillis(timeEnd - timeStart);
		// Log.v("创建秒针总时间",
		// "耗时: " + backCalendar.get(Calendar.MINUTE) + "分 "
		// + backCalendar.get(Calendar.SECOND) + "秒 "
		// + backCalendar.get(Calendar.MILLISECOND) + " 微秒");
		// Log.v("**", "***********************************************");
	}

	private void stopClockTimer() {
		if (mTimer == TimerTypeEnum.service) {
			// 定时器类型为后台Service
			Intent intent = new Intent(mContext, ClockTimerService.class);
			intent.setAction("com.iLoong.widget.Clock.start");
			mContext.stopService(intent);

			ClockTimerReceiver receiver = new ClockTimerReceiver(this);
			mContext.unregisterReceiver(receiver);

		} else if (mTimer == TimerTypeEnum.timerTask) {
			// 定时器类型为Timer
			if (mClockTimer != null) {
				mClockTimer.stop();
			}
		} else if (mTimer == TimerTypeEnum.handler) {
			// 定时器类型为Handle方式触发
			ClockTimerHandler thread = new ClockTimerHandler(null, this);
			thread.stop();
		}
	}

	// 启动定时器
	private void startClockTimer() {
		clockTimeChanged();
		if (mTimer == TimerTypeEnum.service) {
			// 定时器类型为后台Service
			Intent intent = new Intent(mContext, ClockTimerService.class);
			intent.setAction("com.iLoong.widget.Clock.start");
			mContext.startService(intent);

			ClockTimerReceiver receiver = new ClockTimerReceiver(this);
			IntentFilter localIntentFilter = new IntentFilter();
			localIntentFilter.addAction("com.iLoong.widget.clock.change");
			mContext.registerReceiver(receiver, localIntentFilter);

		} else if (mTimer == TimerTypeEnum.timerTask) {
			// 定时器类型为Timer
			mClockTimer = new ClockTimer(this, 1000);
			mClockTimer.start();

		} else if (mTimer == TimerTypeEnum.handler) {
			// 定时器类型为Handle方式触发
			ClockTimerHandler thread = new ClockTimerHandler(null, this);
			thread.start();
		}
	}

	/**
	 * 接受时钟消息，对clock进行更新，目前只是在此更新此时刻时间点需要的参数
	 */
	@Override
	public void clockTimeChanged() {
		Log.v("asdf","clockTimeChanged refreshRender = "+refreshRender);
		if (super.refreshRender != null) {
			super.refreshRender.RefreshRender();
		}
		// TODO Auto-generated method stub
		// Log.v("iLoongClock", "timeChanged");
		Calendar mCalendar = Calendar.getInstance();
		long milliseconds = System.currentTimeMillis();
		mCalendar.setTimeInMillis(milliseconds);
		mCurrentHour = mCalendar.get(Calendar.HOUR);
		mCurrentMinute = mCalendar.get(Calendar.MINUTE);
		mCurrentSecond = mCalendar.get(Calendar.SECOND);
		float hourRotation = ClockHelper.getHourHandRotation(mCurrentMinute,
				mCurrentHour);
		float minuteRotation = ClockHelper
				.getMinuteHandRotation(mCurrentMinute);
		float secondRotation = ClockHelper
				.getSecondHandRotation(mCurrentSecond);

		if(mSimulatorClock !=null)
		{
			mSimulatorClock.updateSimulatorClockView(hourRotation,minuteRotation,secondRotation);
		}
		else if(mDigitClock != null)
		{
			
		}
		else
		{
			if (mClockHourView != null) {
				mClockHourView.updateHourView(hourRotation);
			}
			if (mClockMinuteView != null) {
				mClockMinuteView.updateMinuteView(minuteRotation);
			}
			if (mClockSecondView != null) {
				mClockSecondView.updateSecondView(secondRotation);
			}
		}
	}

	private enum TimerTypeEnum {
		handler, timerTask, service
	}

	@Override
	public boolean scroll(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		if(mSimulatorClock !=null)
		{
			return false;		
		}
		else if(mDigitClock != null)
		{
			return false;
		}
		else
		{
			Log.v("WidgetClock", "scroll");
			if (!isTouchdownTrigered) {
				return true;
			}
			isfilling = false;
			// this.requestFocus();
			// Log.v("WidgetClock", "deltaX:" + deltaX + " deltaY：" + deltaY
			// + " mRotateX:" + mRotateX + " mRotateY:" + mRotateY
			// + " rotateAngleX:" + mRotateY / height * 360 + " rotateAngleY:"
			// + mRotateX / width * 360);
			mRotateX += deltaX;
			mRotateY += deltaY;
			setRotationAngle(mRotateY / height * 360, mRotateX / width * 360, 0);
			mIsOnClickEvent = false;
			return true;
		}
	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		if(mSimulatorClock !=null)
		{	
			return false;		
		}
		else if(mDigitClock != null)
		{
			return false;
		}
		else
		{
			Log.v("WidgetClock", "onTouchDown");
			isTouchdownTrigered = true;
			mRotateX = 0;
			mRotateY = 0;
			this.rotation = 0;
			if (mScrollTween != null && !mScrollTween.isFinished()) {
				mScrollTween.free();
			}
			isfilling = false;
			mIsOnClickEvent = true;
			this.requestFocus();
			return true;
		}
	}

	@Override
	public boolean onTouchUp(float x, float y, int pointer) {
		if(mSimulatorClock !=null)
		{
			return false;		
		}
		else if(mDigitClock != null)
		{
			return false;
		}
		else
		{
			Log.v("WidgetClock", "onTouchUp");
			isTouchdownTrigered = false;
			if (!isfilling) {
				float cur = 0;
				if (this.rotation <= 180) {
					cur = 0;
				} else {
					cur = 360;
				}
				mScrollTween = Tween.to(this, View3DTweenAccessor.ROTATION, 1.2f)
						.ease(Quint.OUT).target(cur).delay(0);
				mScrollTween.start(View3DTweenAccessor.manager);
				this.releaseFocus();
			}
			return false;
		}
	}

	@Override
	public boolean onLongClick(float x, float y) {
		if(mSimulatorClock !=null)
		{
			
				return false;
		}
		else if(mDigitClock != null)
		{

				return false;
		}
		else
		{
			if (isPointInClock(x, y)) {
				this.mIsOnClickEvent = false;
				// TODO Auto-generated method stub
				this.point.x = x;
				this.point.y = y;
				this.toAbsolute(point);
				Vector2 obj = new Vector2(point.x, point.y);
				this.setTag(obj);
				this.releaseFocus();
				return viewParent.onCtrlEvent(this, 0);
			} else {
				return false;
			}
		}
	}

	@Override
	public void onEvent(int type, @SuppressWarnings("rawtypes") BaseTween source) {
		// TODO Auto-generated method stub
		if (source.equals(onClickDownTween) && type == TweenCallback.COMPLETE) {
			onClickDownTween = null;
			onClickUpTween = Tween.to(this, View3DTweenAccessor.ROTATION, 0.2f)
					.target(-25, -25, 0).ease(Quad.INOUT).setCallback(this);
			onClickUpTween.start(View3DTweenAccessor.manager);

		} else if (source.equals(onClickUpTween)
				&& type == TweenCallback.COMPLETE) {
			onClickUpTween = null;
			// Quad.INOUT
			onClickReboundOne = Tween
					.to(this, View3DTweenAccessor.ROTATION, 0.2f)
					.target(20, 20, 0).ease(Circ.OUT).setCallback(this);
			onClickReboundOne.start(View3DTweenAccessor.manager);

		} else if (source.equals(onClickReboundOne)
				&& type == TweenCallback.COMPLETE) {
			onClickReboundOne = null;
			onClickReboundTwo = Tween
					.to(this, View3DTweenAccessor.ROTATION, 0.2f)
					.target(-15, -15, 0).ease(Circ.OUT).setCallback(this);
			onClickReboundTwo.start(View3DTweenAccessor.manager);
			// Elastic.OUT

		} else if (source.equals(onClickReboundTwo)
				&& type == TweenCallback.COMPLETE) {
			onClickReboundTwo = null;
			// Quad.INOUT
			onClickStopTween = Tween
					.to(this, View3DTweenAccessor.ROTATION, 0.3f)
					.target(0, 0, 0).ease(Back.OUT).setCallback(this);
			onClickStopTween.start(View3DTweenAccessor.manager);
		} else if (source.equals(onClickStopTween)
				&& type == TweenCallback.COMPLETE) {
			onClickStopTween = null;
			this.releaseFocus();
		}
	}

	@Override
	public boolean onDoubleClick(float x, float y) {
		// TODO Auto-generated method stub
		// Log.v("WidgetClock", "onDoubleClick");
		return true;
	}

	boolean isfilling = false;

	@Override
	public boolean fling(float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		Log.v("WidgetClock", "fling");
		isTouchdownTrigered = false;
		isfilling = true;
		float cur = 0;
		if (this.rotation <= 180) {
			cur = 0;
		} else {
			cur = 360;
		}
		mScrollTween = Tween.to(this, View3DTweenAccessor.ROTATION, 1.2f)
				.ease(Quint.OUT).target(cur);
		mScrollTween.start(View3DTweenAccessor.manager);
		this.releaseFocus();
		return true;
	}

	private boolean isPointInClock(float x, float y) {
		double r = Math.sqrt((x - this.originX) * (x - this.originX)
				+ (y - this.originY) * (y - this.originY));
		if (r > this.width / 2 || r > this.height / 2) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean onClick(float x, float y) {
		// TODO Auto-generated method stub
		if(mSimulatorClock !=null)
		{
				return mSimulatorClock.onClick(x, y);
		}
		else if(mDigitClock != null)
		{ 
				return mDigitClock.onClick(x, y);
		}
		else
		{
			Log.v("WidgetClock", "onClick");
			if (isPointInClock(x, y)) {
				try {
					String packageName = null;
					SharedPreferences p = mAppContext.mContainerContext
							.getSharedPreferences("iLoong.Widget.Clock", 0);
					packageName = p.getString("clock_package", null);
					if (packageName == null) {
						listPackages();
						Editor editor = p.edit();
						if (pagList.size() != 0) {
							packageName = pagList.get(0);
							editor.putString("clock_package", packageName);
						}
						editor.commit();
					}
					PackageManager pm = mContext.getPackageManager();
					if (packageName != null) {
						Intent intent = pm.getLaunchIntentForPackage(packageName);
						if (intent != null) {
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							mContext.startActivity(intent);
						} else {
							Intent i2 = new Intent(Settings.ACTION_DATE_SETTINGS);
							i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							mContext.startActivity(i2);
						}
					} else {
						Intent i2 = new Intent(Settings.ACTION_DATE_SETTINGS);
						mContext.startActivity(i2);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				this.releaseFocus();
				return true;
			} else {
				return false;
			}
		}
	}

	// public static void transformTheme(Context widgetContext,
	// ComponentName themeComponentName) {
	// THEME_NAME = ClockHelper.transformThemeName(widgetContext,
	// themeComponentName);
	// }

	private HashMap<String, Object> item = new HashMap<String, Object>();
	private List<String> pagList = new ArrayList<String>();

	private void listPackages() {
		ArrayList<PInfo> apps = getInstalledApps(false); /*
														 * false = no system
														 * packages
														 */
		final int max = apps.size();
		for (int i = 0; i < max; i++) {
			apps.get(i).prettyPrint();
			item = new HashMap<String, Object>();

			int aa = apps.get(i).pname.length();
			// String
			// bb=apps.get(i).pname.substring(apps.get(i).pname.length()-11);
			// Log.d("mxt", bb);

			if (aa > 11) {
				Log.d("lxf", "������11");
				if (apps.get(i).pname.indexOf("clock") != -1) {
					if (!(apps.get(i).pname.indexOf("widget") != -1)) {
						try {
							PackageInfo pInfo = mContext.getPackageManager()
									.getPackageInfo(apps.get(i).pname, 0);
							if (isSystemApp(pInfo) || isSystemUpdateApp(pInfo)) {
								Log.d("mxt", "��ϵͳ�Դ��");
								Log.d("mxt",
										"�ҵ���"
												+ apps.get(i).pname
														.substring(apps.get(i).pname
																.length() - 5)
												+ "  ȫ��" + apps.get(i).pname
												+ " " + apps.get(i).appname);
								item.put("pname", apps.get(i).pname);
								item.put("appname", apps.get(i).appname);
								pagList.add(apps.get(i).pname);
							}
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				}
			}

			/*
			 * if(apps.get(i).pname.subSequence(apps.get(i).pname.length()-11,
			 * apps.get(i).pname.length()) != null){
			 * 
			 * }
			 */
		}
	}

	private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
		ArrayList<PInfo> res = new ArrayList<PInfo>();
		List<PackageInfo> packs = mContext.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			if ((!getSysPackages) && (p.versionName == null)) {
				continue;
			}
			PInfo newInfo = new PInfo();
			newInfo.appname = p.applicationInfo.loadLabel(
					mContext.getPackageManager()).toString();
			newInfo.pname = p.packageName;
			newInfo.versionName = p.versionName;
			newInfo.versionCode = p.versionCode;
			res.add(newInfo);
		}
		return res;
	}

	public boolean isSystemApp(PackageInfo pInfo) {
		return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
	}

	public boolean isSystemUpdateApp(PackageInfo pInfo) {
		return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
	}

	class PInfo {
		private String appname = "";
		private String pname = "";
		private String versionName = "";
		private int versionCode = 0;

		private void prettyPrint() {
			Log.i("taskmanger", appname + "\t" + pname + "\t" + versionName
					+ "\t" + versionCode + "\t");
		}
	}

	@Override
	public void onDelete() {
		Log.v(TAG, "onDelete");
		stopClockTimer();
		if (mBackRegion != null) {
			mBackRegion.getTexture().dispose();
			mBackRegion = null;
		}
		dispose();

	}

	@Override
	public void onPause() {
		Log.v(TAG, "onPause");
		stopClockTimer();
	}

	@Override
	public void onResume() {
		Log.v(TAG, "onResume");
		startClockTimer();
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop");
		stopClockTimer();
	}

	public void onDestroy() {
		Log.v(TAG, "onDestroy");
		stopClockTimer();
		if (mBackRegion != null) {
			mBackRegion.getTexture().dispose();
			mBackRegion = null;
		}
		dispose();
	}

	@Override
	public void dispose() {
		super.dispose();
		if(mSimulatorClock !=null)
		{
			mSimulatorClock.dispose();
		}
		else if(mDigitClock != null)
		{ 
			mDigitClock.dispose();
		}
		else
		{	
			for (int i = 0; i < this.getChildCount(); i++) {
				if (this.getChildAt(i) instanceof View3D) {
					this.getChildAt(i).dispose();
				}
			}
		}
		Utils3D.showPidMemoryInfo(mAppContext.mContainerContext,
				"com.iLoong.Clock");
		mContext = null;
		mAppContext = null;
		cooGdx = null;
		texture = null;
		mClockBackView = null;
		mClockHourView = null;
		mClockMinuteView = null;
		mClockSecondView = null;
		mBackRegion = null;
		mClockTimer = null;
		mScrollTween = null;
		mIsOnClickEvent = false;
		onClickDownTween = null;
		onClickUpTween = null;
		onClickReboundOne = null;
		onClickReboundTwo = null;
		onClickStopTween = null;
		mClockBackAhplaView = null;
		mClockBackNoAhplaView = null;
		// Utils3D.showPidMemoryInfo("com.iLoong.Clock");
	}

	public void onUninstall() {
		super.onUninstall();
		Log.v(TAG, "onUninstall");
		stopClockTimer();
		if (mBackRegion != null) {
			mBackRegion.getTexture().dispose();
			mBackRegion = null;
		}
		dispose();
	}
	
	@Override
	public WidgetPluginViewMetaData getPluginViewMetaData() {
		// TODO Auto-generated method stub
		WidgetPluginViewMetaData metaData = new WidgetPluginViewMetaData();
		metaData.spanX = Integer.valueOf(mContext.getResources().getInteger(
				R.integer.spanX));
		metaData.spanY = Integer.valueOf(mContext.getResources().getInteger(
				R.integer.spanY));
		metaData.maxInstanceCount = mContext.getResources().getInteger(
				R.integer.max_instance);
		metaData.maxInstanceAlert = mContext.getResources().getString(
				R.string.max_instance_alert);
		return metaData;
	}
}
