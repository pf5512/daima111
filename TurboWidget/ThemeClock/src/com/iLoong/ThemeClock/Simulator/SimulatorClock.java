package com.iLoong.ThemeClock.Simulator;

import java.util.Calendar;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.ThemeClock.R;
import com.iLoong.ThemeClock.Common.ClockHelper;
import com.iLoong.ThemeClock.Common.ImageHelper;
import com.iLoong.ThemeClock.Common.ThemeParse;
import com.iLoong.ThemeClock.View.BitmapTexture;
import com.iLoong.ThemeClock.View.WidgetClock;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Object3DBase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class SimulatorClock extends Object3DBase{
	private MainAppContext mappContext = null;
	private ThemeParse mThemeParse = null;
	private Context mThemeContext = null;
	private boolean mIsShowSecond = true;
	private float mHourRotation = 0f;
	private float mMinuteRotation = 0f;
	private float mSecondRotation = 0f;
	
	private float mback_width = 0f;
	private float mback_height = 0f;
	private int mwidthcenter_offset = 0;
	private int mHeightcenter_offset = 0;
	
	private Bitmap mFinalBitmap = null;
	private Bitmap mbackBitmap = null;
	private Bitmap mHourBitmap = null;
	private Bitmap mMinuteBitmap = null;
	private Bitmap mSecondBitmap = null;
	private Bitmap mOverBitmap = null;
	
	private Matrix mMatrix = new Matrix();
	private BitmapTexture mBt = null;
	private TextureRegion mTr = null;
	private Paint mPaint = null;
	private boolean mCanDraw = true;
//	private boolean mIsUpdate = true;
	
	private final String TIME_BACK_IMG ="theme/widget/clock/simulator/time_back.png";
	private final String TIME_HOUR_IMG ="theme/widget/clock/simulator/time_clock_hour.png";
	private final String TIME_MINUTE_IMG ="theme/widget/clock/simulator/time_clock_minute.png";
	private final String TIME_SECOND_IMG ="theme/widget/clock/simulator/time_clock_second.png";
	private final String TIME_OVER_IMG ="theme/widget/clock/simulator/time_over.png";
	private final boolean TEST = true;
	
	public SimulatorClock(String name, MainAppContext appContext,Context themeContext,ThemeParse themeParse)
	{
		super(appContext, name);
		mappContext = appContext;
		mThemeContext = themeContext; 
		mThemeParse = themeParse;
		init();
		x = 0;
		y = 0;
	//	this.region = getTextureRegion();
		Log.v("asdf", "SimulatorClock");
		this.width = WidgetClock.MODEL_WIDTH;
		this.height = WidgetClock.MODEL_HEIGHT;
	}

	public void updateSimulatorClockView(float hourRotation,
										float minuteRotation,
										float secondRotation) 
	{
		mHourRotation = 360-(hourRotation-90);
		mMinuteRotation = 360-(minuteRotation-90);
		mSecondRotation = 360-(secondRotation-90);
		Log.v("asdf", ">>>>SimulatorClock updateSimulatorClockView");
		mCanDraw = true;
	}

	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		Log.v("asdf", ">>>>SimulatorClock draw mCanDraw="+mCanDraw);
		if(mCanDraw == false)
		{
			batch.setColor(color.r, color.g, color.b, 1);
			batch.draw(mTr,0,0);
			return;
		}
		
		mFinalBitmap = Bitmap.createBitmap((int)mback_width, (int)mback_height,Config.ARGB_8888);
		Canvas canvas = new Canvas(mFinalBitmap);
		canvas.drawColor(Color.TRANSPARENT);
		//back bitmap
		mMatrix.setTranslate(ImageHelper.getAdaptInParentCenterWitdth((int)mback_width,mbackBitmap.getWidth()),
				ImageHelper.getAdaptInParentCenterWitdth((int)mback_height,mbackBitmap.getHeight()));
		canvas.drawBitmap(mbackBitmap, mMatrix,mPaint);
		//hour bitmap
		mMatrix.setRotate(mHourRotation, mHourBitmap.getWidth()/2, mHourBitmap.getHeight()/2);
		mMatrix.postTranslate(ImageHelper.getAdaptInParentCenterWitdth((int)mback_width,mHourBitmap.getWidth()),
				ImageHelper.getAdaptInParentCenterWitdth((int)mback_height,mHourBitmap.getHeight()));
		canvas.drawBitmap(mHourBitmap,mMatrix,mPaint);
		//minute bitmap
		mMatrix.setRotate(mMinuteRotation, mMinuteBitmap.getWidth()/2, mMinuteBitmap.getHeight()/2);
		mMatrix.postTranslate(ImageHelper.getAdaptInParentCenterWitdth((int)mback_width,mMinuteBitmap.getWidth()),
				ImageHelper.getAdaptInParentCenterWitdth((int)mback_height,mMinuteBitmap.getHeight()));
		canvas.drawBitmap(mMinuteBitmap,mMatrix,mPaint);
		//second bitmap
		if(mIsShowSecond)
		{
			mMatrix.setRotate(mSecondRotation, mSecondBitmap.getWidth()/2, mSecondBitmap.getHeight()/2);
			mMatrix.postTranslate(ImageHelper.getAdaptInParentCenterWitdth((int)mback_width,mSecondBitmap.getWidth()),
					ImageHelper.getAdaptInParentCenterWitdth((int)mback_height,mSecondBitmap.getHeight()));
			canvas.drawBitmap(mSecondBitmap, mMatrix, mPaint);
		}
		//over bitmap
		mMatrix.setTranslate(ImageHelper.getAdaptInParentCenterWitdth((int)mback_width,mOverBitmap.getWidth()),
				ImageHelper.getAdaptInParentCenterWitdth((int)mback_height,mOverBitmap.getHeight()));
		canvas.drawBitmap(mOverBitmap, mMatrix, mPaint);
		
		if(mTr != null)
		{
			if((BitmapTexture)mTr.getTexture()!= null)
			{
				BitmapTexture bt = (BitmapTexture)mTr.getTexture();
				bt.dispose();
				bt = null;
			}
		}
		if(mBt != null)
		{
			mBt.dispose();
			mBt = null;
		}
		mBt = new BitmapTexture(mFinalBitmap);
		mBt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		mTr = new TextureRegion(mBt);
		batch.setColor(color.r, color.g, color.b, 1);
		batch.draw(mTr,0,0);
		mFinalBitmap.recycle();
		mFinalBitmap = null;
		System.gc();
		Log.v("asdf", ">>>>SimulatorClock draw");
		mCanDraw = false;
	}
	
	@Override
	public boolean onClick(float x, float y) {
		// TODO Auto-generated method stub
		Log.v("WidgetClock", "SimulatorClock onClick");	
		try {
			
			Intent i2 = new Intent(Settings.ACTION_DATE_SETTINGS);
			i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mappContext.mWidgetContext.startActivity(i2);
			return true;
				
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
		return false;
	}

	private void readconfig()
	{
		try
		{
			Log.v("asdf","readconfig mThemeParse="+mThemeParse);
			if(mThemeParse != null)
			{
				Log.v("asdf","readconfig SimulatorClock_Use_This_Config="+
							mThemeParse.getInteger("SimulatorClock_Use_This_Config"));
				
				if(mThemeParse.getInteger("SimulatorClock_Use_This_Config") != 0)
				{
					Log.v("asdf","readconfig SimulatorClock_Need_Second="+
							mThemeParse.getInteger("SimulatorClock_Need_Second"));

					if(mThemeParse.getInteger("SimulatorClock_Need_Second") == 1)
						mIsShowSecond = true;
					else
						mIsShowSecond = false;
					mback_width = mThemeParse.getInteger("SimulatorClock_Width");
					mback_height = mThemeParse.getInteger("SimulatorClock_Height");
					
					Log.v("asdf","readconfig 1mback_width="+mback_width);
					Log.v("asdf","readconfig 1mback_height="+mback_height);
				}
				else
				{
					mback_width = mappContext.mWidgetContext.getResources().getDimension(
							R.dimen.simulatorclock_width);
					mback_height = mappContext.mWidgetContext.getResources().getDimension(
							R.dimen.simulatorclock_height);
					mIsShowSecond = true;
					Log.v("asdf","readconfig 2mback_width="+mback_width);
					Log.v("asdf","readconfig 2mback_height="+mback_height);
				}
			}
			else
			{
				mback_width = mappContext.mWidgetContext.getResources().getDimension(
						R.dimen.simulatorclock_width);
				mback_height = mappContext.mWidgetContext.getResources().getDimension(
						R.dimen.simulatorclock_height);
				mIsShowSecond = true;
				Log.v("asdf","readconfig 3mback_width="+mback_width);
				Log.v("asdf","readconfig 3mback_height="+mback_height);
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	private void init()
	{
		
		
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);//防锯齿
		mPaint.setDither(true);//防抖动
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		
		/*
		mback_width = mappContext.mWidgetContext.getResources().getDimension(
				R.dimen.digitclock_width);
		mback_height = mappContext.mWidgetContext.getResources().getDimension(
				R.dimen.digitclock_height);
		*/
		Calendar mCalendar = Calendar.getInstance();
		long milliseconds = System.currentTimeMillis();
		mCalendar.setTimeInMillis(milliseconds);
		int mCurrentHour = mCalendar.get(Calendar.HOUR);
		int mCurrentMinute = mCalendar.get(Calendar.MINUTE);
		int mCurrentSecond = mCalendar.get(Calendar.SECOND);
		mHourRotation = ClockHelper
				.getHourHandRotation(mCurrentMinute,mCurrentHour);
		mMinuteRotation = ClockHelper
				.getMinuteHandRotation(mCurrentMinute);
		mSecondRotation = ClockHelper
				.getSecondHandRotation(mCurrentSecond);
		
		try
		{
			 readconfig();
		//	 mFinalBitmap = Bitmap.createBitmap((int)mback_width, (int)mback_height,Config.ARGB_8888);
			if(TEST)
			{
				mbackBitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_BACK_IMG);
				 mbackBitmap = ImageHelper.makeAdaptBitmap(mappContext.mWidgetContext,mbackBitmap);
		
				 mwidthcenter_offset = ImageHelper.getAdaptInParentCenterWitdth((int)mback_width,mbackBitmap.getWidth());
				 mHeightcenter_offset = ImageHelper.getAdaptInParentCenterWitdth((int)mback_height,mbackBitmap.getHeight());
		
				 mHourBitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_SECOND_IMG);
				 mHourBitmap = ImageHelper.makeAdaptBitmap(mappContext.mWidgetContext,mHourBitmap);
				 
				 mMinuteBitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_MINUTE_IMG);
				 mMinuteBitmap = ImageHelper.makeAdaptBitmap(mappContext.mWidgetContext,mMinuteBitmap);
							 
				 if(mIsShowSecond)
				 {
					 mSecondBitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_SECOND_IMG);
					 mSecondBitmap = ImageHelper.makeAdaptBitmap(mappContext.mWidgetContext,mSecondBitmap);			 
				 }
				 
				 mOverBitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_OVER_IMG);
				 mOverBitmap = ImageHelper.makeAdaptBitmap(mappContext.mWidgetContext,mOverBitmap);
			
			}
			else
			{
			 
			 mbackBitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_BACK_IMG);
			 mbackBitmap = ImageHelper.makeAdaptBitmap(mappContext.mWidgetContext,mbackBitmap);
	
			 mwidthcenter_offset = ImageHelper.getAdaptInParentCenterWitdth((int)mback_width,mbackBitmap.getWidth());
			 mHeightcenter_offset = ImageHelper.getAdaptInParentCenterWitdth((int)mback_height,mbackBitmap.getHeight());
	
			 mHourBitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_SECOND_IMG);
			 mHourBitmap = ImageHelper.makeAdaptBitmap(mappContext.mWidgetContext,mHourBitmap);
			 
			 mMinuteBitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_MINUTE_IMG);
			 mMinuteBitmap = ImageHelper.makeAdaptBitmap(mappContext.mWidgetContext,mMinuteBitmap);
						 
			 if(mIsShowSecond)
			 {
				 mSecondBitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_SECOND_IMG);
				 mSecondBitmap = ImageHelper.makeAdaptBitmap(mappContext.mWidgetContext,mSecondBitmap);			 
			 }
			 
			 mOverBitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_OVER_IMG);
			 mOverBitmap = ImageHelper.makeAdaptBitmap(mappContext.mWidgetContext,mOverBitmap);
			}
			
		}
		catch(Exception ex)
		{
			 Log.v("asdf","ex="+"1111111111111111");

			ex.printStackTrace();
		}
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		Log.v("asdf", ">>>>SimulatorClock dispose");
		if(mbackBitmap != null)
		{
			mbackBitmap.recycle();
			mbackBitmap = null;
		}
		if(mHourBitmap != null)
		{
			mHourBitmap.recycle();
			mHourBitmap = null;
		}
		if(mMinuteBitmap != null)
		{
			mMinuteBitmap.recycle();
			mMinuteBitmap = null;
		}
		if(mIsShowSecond)
		{
			if(mSecondBitmap != null)
			{
				mSecondBitmap.recycle();
				mSecondBitmap = null;
			}
		}
		if(mOverBitmap != null)
		{
			mOverBitmap.recycle();
			mOverBitmap = null;
		}
		if(mFinalBitmap != null)
		{
			mFinalBitmap.recycle();
			mFinalBitmap = null;
		}
		if(mMatrix != null)
			mMatrix = null;
		if(mBt != null)
		{
			mBt.dispose();
			mBt = null;
		}
		if(mTr != null)
		{
			if((BitmapTexture)mTr.getTexture()!= null)
			{
				BitmapTexture bt = (BitmapTexture)mTr.getTexture();
				bt.dispose();
				bt = null;
			}
		}
	}
	@Override
	public void hide() {
		// TODO Auto-generated method stub
		super.hide();
		Log.v("asdf", ">>>>SimulatorClock hide");
		mCanDraw = false;
	}
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		Log.v("asdf", ">>>>SimulatorClock show");
		mCanDraw = true;
	}
	
}
