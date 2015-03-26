package com.cooee.widget3D.JewelWeather.View;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.view.View;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.cooee.weather.com.weatherdataentity;
import com.cooee.widget3D.JewelWeather.R;
import com.cooee.widget3D.JewelWeather.iLoongWeather;
import com.cooee.widget3D.JewelWeather.Common.WeatherHelper;
import com.cooee.widget3D.JewelWeather.DataProvider.WeatherReceiver;
import com.cooee.widget3D.JewelWeather.Timer.ClockHandler;
import com.cooee.widget3D.JewelWeather.Timer.ClockTimer;
import com.cooee.widget3D.JewelWeather.Timer.ClockTimerHandler;
import com.cooee.widget3D.JewelWeather.Timer.ClockTimerListener;
import com.cooee.widget3D.JewelWeather.Timer.ClockTimerReceiver;
import com.cooee.widget3D.JewelWeather.Timer.ClockTimerService;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.UI3DEngine.adapter.IRefreshRender;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.CacheManager;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WidgetWeather extends WidgetPluginView3D implements ClockTimerListener {
	private static final String TAG = "com.iLoong.Weather.View.WidgetWeather";

	public final static String MESH_CACHE_NAME = "com.iLoong.Weather.meshCache";
	public final static String TEXTURE_CACHE_NAME = "com.iLoong.Weather.textureCache";
	public static float MODEL_WIDTH = 280;
	public static float MODEL_HEIGHT = 320;
	
	private final String WATCH_BACK = "/image/widget_back.png";
	public String THEME_NAME = "iLoong/jewelweather/";
	
	
	// 璁板綍褰撳墠浣嶇Щ
	private float mRotateX = 0;
	private float mRotateY = 0;
	
	// 婊戝姩鍔ㄧ敾
	Tween mScrollTween = null;
	private boolean isfilling = false;
	private boolean isTouchdownTrigered = false;
	
	// 鏄惁宸茬粡鏈夎繃TouchDown锛屼互姝ゆ潵鍒ゆ柇鑳藉惁scroll
	private boolean mIsTouchdownTrigered = false;

	private boolean mIsFling = false;

	public static MainAppContext mAppContext;
	public static CooGdx cooGdx;
	public Context mContext = null;

	// 鏃嬭浆鐨勫亸绉诲�
	private float XValue;

	//鏄汉涓虹殑鏃嬭浆 weijie
	private boolean XScroll = false;
	private float last_rotast = 0;
	private float curr_rotast = 0;
	
	public static float SCALE_SIZE = 1F;
	public static float SCALE_X = 1F;
	public static float SCALE_Y = 1F;
	public static float SCALE_Z = 1F;

	// 璁板綍涓媤idgetId
	public int mWidgetId;
	private final static float SHAKE_ROTATE_ANGLE = 5.0f;
	
	// 瀹氭椂鍣ㄧ被鍨�
	private enum TimerTypeEnum {
		handler, timerTask, service
	}
	private TimerTypeEnum mTimer = TimerTypeEnum.timerTask;
	// 鏃堕挓瀹氭椂鍣�
	private ClockTimer mClockTimer = null;
	
	// 澶╂皵鏁版嵁
	public static weatherdataentity mDataEntity = null;
	public static String mpostcode = null;

	// 骞挎挱鎺ユ敹鍣�
	public WeatherReceiver mWeatherReceiver;
	public weatherdataentity textdataEntity;
	
	// 褰撳墠鏃堕棿灏忔椂鏃跺埢
	private int mCurrentHour;

	// 褰撳墠鏃堕棿鍒嗛挓鏃跺埢
	private int mCurrentMinute;

	// 褰撳墠鏃堕棿绉掗挓鏃跺埢
	private int mCurrentSecond;
	// 鏃堕挓鑳屾櫙
	private ClockBackView mClockBackView = null;
	
	// 閿氱偣
	private ClockCenterView mClockCenterView = null;

	// 鏃堕挓鏃堕拡
	private ClockHourView mClockHourView = null;

	// 鏃堕挓鍒嗛拡
	private ClockMinuteView mClockMinuteView = null;

	// 鏃堕挓绉掗拡
	private ClockSecondView mClockSecondView = null;
	
	// 鏃堕挓鏃堕拡
//	    private ClockHourBView mClockHourBView = null;

		// 鏃堕挓鍒嗛拡
//		private ClockMinuteBView mClockMinuteBView = null;

		// 鏃堕挓绉掗拡
//		private ClockSecondBView mClockSecondBView = null;
		
	// 鏃堕挓鍒诲害	
	private ClockMarkView mClockMarkView = null;
	//澶╂皵
	private WeatherIconView mWeatherIconView = null;
	//娓╁害
	private TempleteDownZeroView mTempleteDownZeroView = null;
	//娓╁害 涓綅
	private TempleteOnesView mTempleteOnesView = null;
	//娓╁害 鍗佷綅
	private TempleteTensView mTempleteTensView = null;
	//娓╁害  搴�
	private TempleteOcView mTempleteOcView = null;
	
	private TempleteDownZeroBackView mTempleteDownZeroBackView = null;
	private TempleteBackOnesView mTempleteBackOnesView = null;
	private TempleteBackTensView mTempleteBackTensView = null;
	private WeatherBackIconView mWeatherBackIconView = null;
	private TempleteBackOcView mTempleteBackOcView = null;
	
	public static float mClock_Center_Offset_X = 0f;
	//鐐瑰嚮鍖哄煙
	public enum POINT_IN
	{
		POINT_IN_UNKOWN,
		POINT_IN_CLOCK,
		POINT_IN_WEATHER
	}
	private POINT_IN mPointIn = POINT_IN.POINT_IN_UNKOWN;
	
	private TextureRegion mBackRegion = null;
	private EnterInAnim mEnterInAnim = null;
	private float mPointInWeather_x = 0f;
	private float mPointInWeather_y = 0f;
	private float mPoint_in_weather_width = 0f;
	public static boolean StartWidgetAnimation = false;
	private Tween shakeAnimation = null;
	private float shakeTime;
	private boolean shakeAnimationing = false;
	private String mCurrCity = "";
	private boolean startEnterAmin = false;
	@SuppressWarnings("unchecked")
	public WidgetWeather(String name, MainAppContext appContext, int widgetId) 
	{
		super(name);
		
		
		mAppContext = appContext;
		mAppContext.mThemeName+="/jewelweather";
		mWidgetId = widgetId;
		cooGdx = new CooGdx(appContext.mGdxApplication);
		mContext = mAppContext.mWidgetContext;
		
		SCALE_X = SCALE_Y = SCALE_Z = SCALE_SIZE = ((float) mContext
				//		.getResources().getDisplayMetrics().density / (float) 1.5 * (float) 0.75);
				.getResources().getDisplayMetrics().density / (float) 1.5);
		
		mClock_Center_Offset_X = mContext.getResources().getDimension(R.dimen.clock_center_offset_x);
		mPointInWeather_x = mContext.getResources().getDimension(R.dimen.point_in_weather_x);
		mPointInWeather_y = mContext.getResources().getDimension(R.dimen.point_in_weather_y);
		mPoint_in_weather_width = mContext.getResources().getDimension(R.dimen.point_in_weather_width);
		
		MODEL_WIDTH = mContext.getResources().getDimension(R.dimen.model_width);
		MODEL_HEIGHT = mContext.getResources().getDimension(R.dimen.model_height);
		
		mCurrCity = "";
		
		this.setSize(MODEL_WIDTH, MODEL_HEIGHT);
		this.setOrigin(MODEL_WIDTH /2 + mClock_Center_Offset_X, MODEL_HEIGHT /2);
	
		Log.e("memorytest", "init 1 start");
		displayMemory(mAppContext);
		//	THEME_NAME = appContext.mThemeName;
		if(mBackRegion == null)
		{
			AndroidFiles gdxFile = new AndroidFiles(mAppContext.mWidgetContext.getAssets());
			displayMemory(mAppContext);
			FileHandle fileHandle = gdxFile.internal(THEME_NAME + WATCH_BACK);
			displayMemory(mAppContext);
			Bitmap bm = BitmapFactory.decodeStream(fileHandle.read());
			displayMemory(mAppContext);
			BitmapTexture texture = new BitmapTexture(bm);	
			displayMemory(mAppContext);
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			
			displayMemory(mAppContext);
			mBackRegion = new TextureRegion(texture);
			
			displayMemory(mAppContext);
			bm.recycle();
			displayMemory(mAppContext);
			bm = null;
			displayMemory(mAppContext);
	//		System.gc();
		}
		Log.e("memorytest", "init 2 start");
		displayMemory(mAppContext);
		//娣诲姞鑳屾櫙閮ㄥ垎
		addbackgoundView();
		//娣诲姞鏃堕挓閮ㄥ垎
		addTimeclockView();
		//娣诲姞澶╂皵閮ㄥ垎
		addweatherView();
		//鑳岄潰
	//	addweatherBackView();
		// 鍚姩瀹氭椂鍣�
		mTimer = TimerTypeEnum.timerTask;
		startClockTimer();
				
		// 娉ㄥ唽骞挎挱鎺ユ敹鍣�
		registerReceiver();	
		Log.e("memorytest", "init end");
		displayMemory(mAppContext);
	}

	private void registerReceiver() {
		Log.v(TAG, "registerReceiver");
		mWeatherReceiver = new WeatherReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.cooee.weather.data.action.UPDATE_RESULT");
		filter.addAction("com.cooee.weather.Weather.action.CHANGE_POSTALCODE");
		mAppContext.mContainerContext.registerReceiver(mWeatherReceiver, filter);
	
		mEnterInAnim = new EnterInAnim(mAppContext.mWidgetContext,mWidgetId,null);
	}

	private void unregisterReceiver() {
		Log.v(TAG, "unregisterReceiver");
		mAppContext.mContainerContext.unregisterReceiver(mWeatherReceiver);
		mWeatherReceiver = null;
		if(mEnterInAnim != null)
		{
			mEnterInAnim.dispose();
			mEnterInAnim = null;
		}
	}
	
	//鍋滄瀹氭椂鍣�
	private void stopClockTimer() {
		if (mTimer == TimerTypeEnum.service) {
			Intent intent = new Intent(mContext, ClockTimerService.class);
			intent.setAction("com.iLoong.widget.Clock.start");
			mContext.stopService(intent);

			ClockTimerReceiver receiver = new ClockTimerReceiver(this);
			mContext.unregisterReceiver(receiver);

		} else if (mTimer == TimerTypeEnum.timerTask) {
			if (mClockTimer != null) {
				mClockTimer.stop();
			}
		} else if (mTimer == TimerTypeEnum.handler) {
			ClockTimerHandler thread = new ClockTimerHandler(null, this);
			thread.stop();
		}
	}
	// 鍚姩瀹氭椂鍣�
	private void startClockTimer() {
		clockTimeChanged();
		if (mTimer == TimerTypeEnum.service) {
			// 瀹氭椂鍣ㄧ被鍨嬩负鍚庡彴Service
			Intent intent = new Intent(mContext, ClockTimerService.class);
			intent.setAction("com.iLoong.widget.Clock.start");
			mContext.startService(intent);

			ClockTimerReceiver receiver = new ClockTimerReceiver(this);
			IntentFilter localIntentFilter = new IntentFilter();
			localIntentFilter.addAction("com.iLoong.widget.clock.change");
			mContext.registerReceiver(receiver, localIntentFilter);

		} else if (mTimer == TimerTypeEnum.timerTask) {
			// 瀹氭椂鍣ㄧ被鍨嬩负Timer
			mClockTimer = new ClockTimer(this, 1000);
			mClockTimer.start();

		} else if (mTimer == TimerTypeEnum.handler) {
			// 瀹氭椂鍣ㄧ被鍨嬩负Handle鏂瑰紡瑙﹀彂
			ClockTimerHandler thread = new ClockTimerHandler(null, this);
			thread.start();
		}
	}

	/**
	 * 鎺ュ彈鏃堕挓娑堟伅锛屽clock杩涜鏇存柊锛岀洰鍓嶅彧鏄湪姝ゆ洿鏂版鏃跺埢鏃堕棿鐐归渶瑕佺殑鍙傛暟
	 */
	@Override
	public void clockTimeChanged() {
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
		float hourRotation = WeatherHelper.getHourHandRotation(mCurrentMinute,
				mCurrentHour);
		float minuteRotation = WeatherHelper
				.getMinuteHandRotation(mCurrentMinute);
		float secondRotation = WeatherHelper
				.getSecondHandRotation(mCurrentSecond);

		if (mClockHourView != null) {
			mClockHourView.updateHourView(hourRotation);
		}
		if (mClockMinuteView != null) {
			mClockMinuteView.updateMinuteView(minuteRotation);
		}
		if (mClockSecondView != null) {
			mClockSecondView.updateSecondView(secondRotation);
		}
		/*
		if (mClockHourBView != null) {
			mClockHourBView.updateHourView(hourRotation);
		}
		if (mClockMinuteBView != null) {
			mClockMinuteBView.updateMinuteView(minuteRotation);
		}
		if (mClockSecondBView != null) {
			mClockSecondBView.updateSecondView(secondRotation);
		}
		*/
//		Tween.to(this, View3DTweenAccessor.ROTATION, 0.5f)
//		.ease(Quint.OUT).target(secondRotation).start(View3DTweenAccessor.manager);
	}
	
	private void addbackgoundView()
	{	
		mClockBackView = new ClockBackView("clockbackview",mAppContext,mBackRegion);
		this.addView(mClockBackView);
		mClockBackView.bringToFront();
		
		mClockMarkView = new ClockMarkView("clockmarkview",mAppContext,mBackRegion);
		this.addView(mClockMarkView);
		mClockMarkView.bringToFront();
	}
	
	private void addTimeclockView()
	{
		/*
		mClockHourBView = new ClockHourBView("clockhourbview",mAppContext,mBackRegion);
		this.addView(mClockHourBView);
	//	mClockHourBView.bringToFront();
		
		mClockMinuteBView = new ClockMinuteBView("clockminutebview",mAppContext,mBackRegion);
		this.addView(mClockMinuteBView);
	//	mClockMinuteBView.bringToFront();
		
		mClockSecondBView = new ClockSecondBView("clocksecondbview",mAppContext,mBackRegion);
		this.addView(mClockSecondBView);
	//	mClockSecondBView.bringToFront();
		*/
		
		/*
		mClockCenterView = new ClockCenterView("clockcenterview",mAppContext,mBackRegion);
		this.addView(mClockCenterView);
		mClockCenterView.bringToFront();
		*/
		
		mClockHourView = new ClockHourView("clockhourview",mAppContext,mBackRegion);
		this.addView(mClockHourView);
		mClockHourView.bringToFront();
		
		mClockMinuteView = new ClockMinuteView("clockminuteview",mAppContext,mBackRegion);
		this.addView(mClockMinuteView);
		mClockMinuteView.bringToFront();
		
		mClockSecondView = new ClockSecondView("clocksecondview",mAppContext,mBackRegion);
		this.addView(mClockSecondView);
		mClockSecondView.bringToFront();
		
		
	}

	private void addweatherBackView()
	{
		mWeatherBackIconView = new WeatherBackIconView("weatherbackiconview",mAppContext);
		this.addView(mWeatherBackIconView);
		mWeatherBackIconView.bringToFront();
		
		mTempleteDownZeroBackView = new TempleteDownZeroBackView("templetedownzerobackview",mAppContext);
		this.addView(mTempleteDownZeroBackView);
		mTempleteDownZeroBackView.bringToFront();
		
		mTempleteBackOnesView = new TempleteBackOnesView("templetebackonesview",mAppContext);
		this.addView(mTempleteBackOnesView);
		mTempleteBackOnesView.bringToFront();
		
		mTempleteBackTensView = new TempleteBackTensView("templetebacktensview",mAppContext);
		this.addView(mTempleteBackTensView);
		mTempleteBackTensView.bringToFront();
		
		mTempleteBackOcView = new TempleteBackOcView("templetebackocview",mAppContext);
		this.addView(mTempleteBackOcView);
		mTempleteBackOcView.bringToFront();
	}
	private void addweatherView()
	{
		mWeatherIconView = new WeatherIconView("weathericonview",mAppContext);
		this.addView(mWeatherIconView);
		mWeatherIconView.bringToFront();
		
		mTempleteDownZeroView = new TempleteDownZeroView("templetedownzeroview",mAppContext);
		this.addView(mTempleteDownZeroView);
		mTempleteDownZeroView.bringToFront();
		
		mTempleteOnesView = new TempleteOnesView("templeteonesview",mAppContext);
		this.addView(mTempleteOnesView);
		mTempleteOnesView.bringToFront();
		
		mTempleteTensView = new TempleteTensView("templetetensview",mAppContext);
		this.addView(mTempleteTensView);
		mTempleteTensView.bringToFront();
		
		mTempleteOcView = new TempleteOcView("templeteocview",mAppContext);
		this.addView(mTempleteOcView);
		mTempleteOcView.bringToFront();
	}
	
	@Override
	public boolean scroll(float x, float y, float deltaX, float deltaY) {
	//	Log.v("3D_WEATHER", ">>>>scroll isTouchdownTrigered="+isTouchdownTrigered+"<<<<");
	//	Log.v("3D_WEATHER", ">>>>scroll<<<<");
		if(shakeAnimationing)
		{
			return true;
		}
		if (!isTouchdownTrigered) {
			return true;
		}
		isfilling = false;

		mRotateX += deltaX;
		mRotateY += deltaY;
		setRotationAngle(mRotateY / height * 360, mRotateX / width * 360, 0);
		return true;
	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		isTouchdownTrigered = true;
		mRotateX = 0;
		mRotateY = 0;
		this.rotation = 0;
		if (mScrollTween != null && !mScrollTween.isFinished()) {
			mScrollTween.free();
		}
		isfilling = false;
		this.requestFocus();
		return true;
	}

	
	@Override
	public boolean onTouchUp(float x, float y, int pointer) {
	//	Log.v("WidgetClock", "onTouchUp");
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

	@Override
	public boolean onLongClick(float x, float y) {
		if(shakeAnimationing)
		{
			return true;
			//shakeAnimationing= false;
		}
		this.point.x = x;
		this.point.y = y;
		this.toAbsolute(point);
		Vector2 obj = new Vector2(point.x, point.y);
		this.setTag(obj);
		this.releaseFocus();
		return viewParent.onCtrlEvent(this, 0);
	//	iLoongWeather.mWidgetWeatherViewRoot.onLongClick(x, y);
	//	return true;
	}

	public int SHAKE_TOTLE = 8;
	public int mSake_count = 0;
	@Override
	public void onEvent(int type, @SuppressWarnings("rawtypes") BaseTween source) {
		// TODO Auto-generated method stub
			//teapotXu add start for FolderMainMenu && shake
			if (source == shakeAnimation && type == TweenCallback.COMPLETE) 
			{
				
			//	setRotationAngle( 0, 0, 0);
				
				/*
				float r;
				if(mSake_count >= SHAKE_TOTLE)
				{
					mSake_count = 0;
					shakeAnimation.free();
					shakeAnimation = null;
					shakeAnimationing = false;
					return;
				}
				
				if (this.rotation == SHAKE_ROTATE_ANGLE) {
//					this.setOrigin(point1.x, point1.y);
					r = 0f;
				} else {
//					this.setOrigin(point2.x, point2.y);
					r = SHAKE_ROTATE_ANGLE;
				}
			//	shakeAnimation = this.startTween(View3DTweenAccessor.ROTATION,
			//			Linear.INOUT, shakeTime, r, 0, 0).setCallback(this);
				if(mSake_count == SHAKE_TOTLE-1)
				{
					shakeAnimation = Tween.to(this, View3DTweenAccessor.ROTATION, shakeTime)//.delay(1.3f)
							.ease(Linear.INOUT)
							.target(0)
						//	.targetRelative(r,0f)
							.start(View3DTweenAccessor.manager)
							.setCallback(this);
				}
				else
				{
				shakeAnimation = Tween.to(this, View3DTweenAccessor.ROTATION, shakeTime)//.delay(1.3f)
						.ease(Linear.INOUT)
						.target(r)
					//	.targetRelative(r,0f)
						.start(View3DTweenAccessor.manager)
						.setCallback(this);
				}
				mSake_count++;
				
				*/
			} 	
				
			//teapotXu add end for FolderMainMenu && shake				
		
		super.onEvent(type, source);
	
	}

	@Override
	public boolean fling(float velocityX, float velocityY) {
		// TODO Auto-generated method stub
	//	Log.v("3D_WEATHER", ">>>>fling<<<<");
		if(shakeAnimationing)
		{
			return true;
			//shakeAnimationing= false;
		}
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
/*	
	private POINT_IN isPointIn(float x, float y)
	{
		POINT_IN ret = POINT_IN.POINT_IN_UNKOWN;
		if(isPointInClock(x,y))
		{
			ret = POINT_IN.POINT_IN_CLOCK;
		}
		else if(isPointInWeather(x,y))
		{
			ret = POINT_IN.POINT_IN_WEATHER;
		}	
		return ret;
	}
	*/
	private boolean isPointInWeather(float x, float y) {
		if((x>(this.originX+mPointInWeather_x) && x<this.width+ mPoint_in_weather_width) &&
				(y<(this.originY+mPointInWeather_y) && y>0))
			return true;
		else 
			return false;
		
	}
	private boolean isPointInClock(float x, float y) {
	//	Log.e("aq", "this.originX="+this.originX+",mPointInWeather_x="+mPointInWeather_x+",this.height="+this.height);
	//	Log.e("aq", "X="+ x +",Y="+y);
		
		if((x>(this.originX-mPointInWeather_x) && x<this.originX + mPointInWeather_x) &&
				(y<(this.height) && y>0))
			return true;
		else 
			return false;
		
	}
	///////////////////////////////////////////////////////////////////////////////
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
	private HashMap<String, Object> item = new HashMap<String, Object>();
	private List<String> pagList = new ArrayList<String>();
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
	private void listPackages() {
		ArrayList<PInfo> apps = getInstalledApps(false); 
		final int max = apps.size();
		for (int i = 0; i < max; i++) {
			apps.get(i).prettyPrint();
			item = new HashMap<String, Object>();

			int aa = apps.get(i).pname.length();
			// String
			// bb=apps.get(i).pname.substring(apps.get(i).pname.length()-11);
			// Log.d("mxt", bb);

			if (aa > 11) {
				if (apps.get(i).pname.indexOf("clock") != -1) {
					if (!(apps.get(i).pname.indexOf("widget") != -1)) {
						try {
							PackageInfo pInfo = mContext.getPackageManager()
									.getPackageInfo(apps.get(i).pname, 0);
							if (isSystemApp(pInfo) || isSystemUpdateApp(pInfo)) {
								
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
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onClick(float x, float y) {	
	//	android.util.Log.v("aq", "onClick");
		if(shakeAnimationing)
		{
			shakeAnimation.kill();
			return true;
			//shakeAnimationing= false;
		}
		if(isPointInWeather(x,y))
		{
		//	android.util.Log.v("aq", "isPointInWeather = true");
			try
			{
				Intent intent = new Intent();//(mAppContext.mWidgetContext,MainActivity.class);
				intent.setComponent(new ComponentName("com.cooeeui.weatherclient", "com.cooee.app.cooeejewelweather3D.view.MainActivity"));
			//	intent.setClassName(mAppContext.mWidgetContext,"com.cooee.app.cooeejewelweather3D.MainActivity");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction("com.cooee.weather.MAIN");
				intent.putExtra("userId", mWidgetId);
				intent.putExtra("city",mCurrCity);
				mAppContext.mWidgetContext.startActivity(intent);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return true;
		}
		else if(isPointInClock(x,y))
		{
		//	android.util.Log.v("aq", "isPointInWeather = false");
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
			//test
			this.releaseFocus();
			return true;
		}
		return false;
	}

	/**
	 * 閲婃斁鎵�湁绾圭悊
	 */
	public void disposeAllTexture() {
		if (mClockBackView != null)
			mClockBackView.disposeTexture();
		if (mClockCenterView != null)
			mClockCenterView.disposeTexture();
		if (mClockHourView != null)
			mClockHourView.disposeTexture();
		if (mClockMarkView != null)
			mClockMarkView.disposeTexture();
		if (mClockMinuteView != null)
			mClockMinuteView.disposeTexture();
		if (mClockSecondView != null)
			mClockSecondView.disposeTexture();
		if (mTempleteDownZeroView != null)
			mTempleteDownZeroView.disposeTexture();
		if (mTempleteOcView != null)
			mTempleteOcView.disposeTexture();
		if (mTempleteOnesView != null)
			mTempleteOnesView.disposeTexture();
		if (mTempleteTensView != null)
			mTempleteTensView.disposeTexture();
		if (mWeatherIconView != null){
			mWeatherIconView.disposeTexture();
		}
		
		if (mTempleteDownZeroBackView != null){
			mTempleteDownZeroBackView.disposeTexture();
		}
		if (mTempleteBackOnesView != null){
			mTempleteBackOnesView.disposeTexture();
		}
		if (mTempleteBackTensView != null){
			mTempleteBackTensView.disposeTexture();
		}
		if (mWeatherBackIconView != null){
			mWeatherBackIconView.disposeTexture();
		}
		if (mTempleteBackOcView != null){
			mTempleteBackOcView.disposeTexture();
		}
			
		if (mBackRegion != null){
			BitmapTexture tmp = (BitmapTexture) mBackRegion.getTexture();
			if(tmp!= null)
				tmp.dispose();
		}	
	}
	
	public void disposeAll() {
		if (mClockBackView != null)
		{
			mClockBackView.dispose();
			mClockBackView = null;
		}
		if (mClockCenterView != null)
		{
			mClockCenterView.dispose();
			mClockCenterView = null;
		}
		if (mClockHourView != null)
		{
			mClockHourView.dispose();
			mClockHourView = null;
		}
		if (mClockMarkView != null)
		{
			mClockMarkView.dispose();
			mClockMarkView = null;
		}
		if (mClockMinuteView != null)
		{
			mClockMinuteView.dispose();
			mClockMinuteView = null;
		}
		if (mClockSecondView != null)
		{
			mClockSecondView.dispose();
			mClockSecondView = null;
		}
		if (mTempleteDownZeroView != null)
		{
			mTempleteDownZeroView.dispose();
			mTempleteDownZeroView = null;
		}
		if (mTempleteOcView != null)
		{
			mTempleteOcView.dispose();
			mTempleteOcView = null;
		}
		if (mTempleteOnesView != null)
		{
			mTempleteOnesView.dispose();
			mTempleteOnesView = null;
		}
		if (mTempleteTensView != null)
		{
			mTempleteTensView.dispose();
			mTempleteTensView = null;
		}
		if (mWeatherIconView != null)
		{
			mWeatherIconView.dispose();
			mWeatherIconView = null;
		}
		if (mTempleteDownZeroBackView != null)
		{
			mTempleteDownZeroBackView.dispose();
			mTempleteDownZeroBackView = null;
		}
		if (mTempleteBackOnesView != null)
		{
			mTempleteBackOnesView.dispose();
			mTempleteBackOnesView = null;
		}
		if (mTempleteBackTensView != null)
		{
			mTempleteBackTensView.dispose();
			mTempleteBackTensView = null;
		}
		if (mWeatherBackIconView != null)
		{
			mWeatherBackIconView.dispose();
			mWeatherBackIconView = null;
		}
		
		if (mTempleteBackOcView != null)
		{
			mTempleteBackOcView.dispose();
			mTempleteBackOcView = null;
		}
		mBackRegion = null;
		mDataEntity = null;
		mWeatherReceiver =null; 
	}

//	@Override
	public void onDelete() {
		Log.v("3D_WEATHER", "WidgetWeather onDelete");
		Log.e("memorytest", "onDelete 1");
		displayMemory(mAppContext);
		stopClockTimer();
		disposeAllTexture();
		disposeAll();
		super.onDelete();
		Log.e("memorytest", "onDelete 2");
		displayMemory(mAppContext);
	}

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.v("3D_WEATHER", "WidgetWeather onDestroy");
		stopClockTimer();
		disposeAllTexture();
		disposeAll();
		super.onDestroy();
		Log.e("memorytest", "onDestroy");
		displayMemory(mAppContext);
	}

	@Override
	public void onUninstall() {
		// TODO Auto-generated method stub
		Log.v("aq", "WidgetWeather onUninstall");
		stopClockTimer();
		disposeAllTexture();
		disposeAll();
		super.onUninstall();
		Log.e("memorytest", "onUninstall");
		displayMemory(mAppContext);
	}

	
	public void UpdataDateEntity()
	{
		android.util.Log.v("weijie", ">>>>UpdataDateEntity mDataEntity="+mDataEntity);
		if(mDataEntity != null)
		{
	//		android.util.Log.v("aq", "UpdataDateEntity mWeatherIconView="+mWeatherIconView);
			
			if(mTempleteDownZeroView!= null)
			{
				mTempleteDownZeroView.updataDate(mDataEntity.getTempC(),true);
			}
			if(mTempleteOnesView!= null)
			{
				mTempleteOnesView.updataDate(mDataEntity.getTempC(),true);
			}
			if(mTempleteTensView!= null)
			{
				mTempleteTensView.updataDate(mDataEntity.getTempC(),true);
			}
		//	Log.v("aq", "mTempleteOcView = "+mTempleteOcView);
			if(mTempleteOcView!= null)
			{
				mTempleteOcView.updataDate(true);
			}
			if(mWeatherIconView != null)
			{	
				mWeatherIconView.updataDate(mDataEntity.getCondition());
				mCurrCity = mDataEntity.getCity();
			}
			
			if(mTempleteDownZeroBackView!= null)
			{
				mTempleteDownZeroBackView.updataDate(mDataEntity.getTempC(),true);
			}
			if(mTempleteBackOnesView!= null)
			{
				mTempleteBackOnesView.updataDate(mDataEntity.getTempC(),true);
			}
			if(mTempleteBackTensView!= null)
			{
				mTempleteBackTensView.updataDate(mDataEntity.getTempC(),true);
			}
			if(mTempleteBackOcView!= null)
			{
				mTempleteBackOcView.updataDate(true);
			}
			if(mWeatherBackIconView!= null)
			{
				mWeatherBackIconView.updataDate(mDataEntity.getCondition());
			}	
		}
		else 
		{
	
			if(mTempleteDownZeroView!= null)
			{
				mTempleteDownZeroView.updataDate(0,false);
			}
	
			if(mTempleteOnesView!= null)
			{
				mTempleteOnesView.updataDate(0,false);
			}
	
			if(mTempleteTensView!= null)
			{
				mTempleteTensView.updataDate(0,false);
			}
	
		//	Log.v("aq", "mTempleteOcView = "+mTempleteOcView);
			if(mTempleteOcView!= null)
			{
				mTempleteOcView.updataDate(false);
			}
	
			
			if(mWeatherIconView != null)
			{	
				mWeatherIconView.updataDate(null);
				mCurrCity = null;
			}
	
			if(mTempleteDownZeroBackView!= null)
			{
				mTempleteDownZeroBackView.updataDate(0,false);
			}
	
			if(mTempleteBackOnesView!= null)
			{
				mTempleteBackOnesView.updataDate(0,false);
			}
	
			if(mTempleteBackTensView!= null)
			{
				mTempleteBackTensView.updataDate(0,false);
			}
	
			if(mTempleteBackOcView!= null)
			{
				mTempleteBackOcView.updataDate(false);
			}
	
			if(mWeatherBackIconView!= null)
			{
				mWeatherBackIconView.updataDate(null);
			}
	
		}
		
		
		displayMemory(appContext);
		Log.e("weijie", "updata end");
	}
	
	//public static boolean frist = false;
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		/*
		if(this.startEnterAmin)
		{
			if (shakeAnimation != null) {
				shakeAnimation.kill();
			}
				
			shakeAnimation =  Tween.to(this, View3DTweenAccessor.ROTATION, 1.2f)
					.ease(Quint.OUT).target(0).delay(0f);
			shakeAnimation.start(View3DTweenAccessor.manager);
			this.startEnterAmin = false;
		}
		*/
		super.draw(batch, parentAlpha);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		Log.e("aq", ">>>>hide");
		super.hide();
		/*
		if(shakeAnimationing)
		{
			shakeAnimation.kill();
			//shakeAnimationing= false;
		}
		*/
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		Log.e("aq", ">>>>show");
		super.show();
		/*
		if(mWeatherIconView != null)
		{
			mWeatherIconView.start();
		}
		*/
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.e("ap", ">>>>start");
		super.onStart();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.e("aq", ">>>>onResume");
	//	onStartWidgetAnimation();
		startClockTimer();
		super.onResume();
	}
	/*
	public void setRefreshRender(IRefreshRender refreshRender) {
		this.refreshRender = refreshRender;
		if (mWeatherIconView != null) {
			mWeatherIconView.setRefreshRender(refreshRender);
		}
	}
	*/
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.e("aq", ">>>>onPause");
		stopClockTimer();
		super.onPause();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.e("aq", ">>>>onStop");
		stopClockTimer();
		super.onStop();
	}

	@Override
	public boolean onStartWidgetAnimation(Object obj, int widgetAnimType,
			int widgetAnimDirection) {
		// TODO Auto-generated method stub
	//	return super.onStartWidgetAnimation(obj, widgetAnimType);
	//	Log.e("aq", "onStartWidgetAnimation widgetAnimType ="+widgetAnimType);
		if(widgetAnimType == 0 || widgetAnimType ==2)
		{
			if (shakeAnimation != null) {
				shakeAnimation.kill();
			}
			setRotationAngle( -60/ height * 360, 0, 0);	
			shakeAnimation =  Tween.to(this, View3DTweenAccessor.ROTATION, 1.2f)
					.ease(Quint.OUT).target(0).delay(0.7f);
			shakeAnimation.start(View3DTweenAccessor.manager);
			
		}
		else if(widgetAnimType == 1)
		{
			/*
			if (shakeAnimation != null) {
				if(shakeAnimationing == true)
					shakeAnimation.kill();
			}
			*/
			if(mWeatherIconView != null)
			{
				mWeatherIconView.start();
			}
		}
		
		//shakeAnimation = this.startTween(View3DTweenAccessor.ROTATION,Linear.INOUT, 1.0f, SHAKE_ROTATE_ANGLE, 0, 0);
		return true;
	}



	public void displayMemory(MainAppContext context) 
	{        
		
		if(context==null){
			return;
		}
		ActivityManager am = (ActivityManager) context.mWidgetContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo   info = new ActivityManager.MemoryInfo();   
		am.getMemoryInfo(info);    
        Log.i("memorytest","绯荤粺鍓╀綑鍐呭瓨:"+(info.availMem >> 10)+"k");   
    //    Log.i("memorytest","绯荤粺鏄惁澶勪簬浣庡唴瀛樿繍琛岋細"+info.lowMemory);
    //    Log.i("memorytest","褰撶郴缁熷墿浣欏唴瀛樹綆浜�+info.threshold+"鏃跺氨鐪嬫垚浣庡唴瀛樿繍琛�);

    }

	/*
	@Override
	public boolean onStartWidgetAnimation() {
		// TODO Auto-generated method stub
		Log.e("aq", ">>>>onStartWidgetAnimation");
		//StartWidgetAnimation = true;
		//this.refreshRender.RefreshRender();
	//	Tween.to(this, View3DTweenAccessor.ROTATION, 3f)//.delay(1.3f)
	//	.ease(Linear.INOUT).targetRelative(360*3,0).start(View3DTweenAccessor.manager);
		if (shakeAnimation != null) {
			shakeAnimation.kill();
		}
		Double d = Math.random();
		shakeTime = (float) (d * 0.02f + 0.09f);
		shakeAnimationing = true;
		shakeAnimation = Tween.to(this, View3DTweenAccessor.ROTATION, shakeTime)//.delay(1.3f)
			.ease(Linear.INOUT)
			.target(SHAKE_ROTATE_ANGLE)
			//.targetRelative(SHAKE_ROTATE_ANGLE,0f)
			.start(View3DTweenAccessor.manager)
			.setCallback(this);
		//shakeAnimation = this.startTween(View3DTweenAccessor.ROTATION,Linear.INOUT, 1.0f, SHAKE_ROTATE_ANGLE, 0, 0);
		return false;
	}
	*/

}
