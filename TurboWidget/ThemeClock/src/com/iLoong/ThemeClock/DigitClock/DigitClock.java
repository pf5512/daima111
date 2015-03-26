package com.iLoong.ThemeClock.DigitClock;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.graphics.RectF;
import android.provider.Settings;
import android.util.Log;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.ThemeClock.R;
import com.iLoong.ThemeClock.Common.ImageHelper;
import com.iLoong.ThemeClock.Common.ThemeParse;
import com.iLoong.ThemeClock.View.BitmapTexture;
import com.iLoong.ThemeClock.View.WidgetClock;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Object3DBase;

public class DigitClock extends Object3DBase{
	private MainAppContext mAppContext = null;
	private ThemeParse mThemeParse = null;
	private Context mThemeContext = null;
	private boolean mCanDraw = true;
	private Paint mPaint = null;
	private Paint mPaint_Text_date = null;
	private Paint mPaint_Text_AM = null;
	private final boolean USE_DRAW_BITMAP_1 = false;
	private final String TIME_BACK_IMG ="theme/widget/clock/digit/time_back.png";
	private final String TIME_0 ="theme/widget/clock/digit/time_0.png";
	private final String TIME_1 ="theme/widget/clock/digit/time_1.png";
	private final String TIME_2 ="theme/widget/clock/digit/time_2.png";
	private final String TIME_3 ="theme/widget/clock/digit/time_3.png";
	private final String TIME_4 ="theme/widget/clock/digit/time_4.png";
	private final String TIME_5 ="theme/widget/clock/digit/time_5.png";
	private final String TIME_6 ="theme/widget/clock/digit/time_6.png";
	private final String TIME_7 ="theme/widget/clock/digit/time_7.png";
	private final String TIME_8 ="theme/widget/clock/digit/time_8.png";
	private final String TIME_9 ="theme/widget/clock/digit/time_9.png";
	private final String TIME_AM ="theme/widget/clock/digit/time_am.png";
	private final String TIME_PM ="theme/widget/clock/digit/time_pm.png";
	private final String TIME_DOT ="theme/widget/clock/digit/time_dot.png";
//	private Bitmap mfinalBitmap = null;
	private Bitmap mbackBitmap = null;
	private Bitmap mTime0Bitmap = null;
	private Bitmap mTime1Bitmap = null;
	private Bitmap mTime2Bitmap = null;
	private Bitmap mTime3Bitmap = null;
	private Bitmap mTime4Bitmap = null;
	private Bitmap mTime5Bitmap = null;
	private Bitmap mTime6Bitmap = null;
	private Bitmap mTime7Bitmap = null;
	private Bitmap mTime8Bitmap = null;
	private Bitmap mTime9Bitmap = null;
	private Bitmap mTimeAMBitmap = null;
	private Bitmap mTimePMBitmap = null;
	private Bitmap mFinalBitmap = null;
	private BitmapTexture mBtt = null;
	private TextureRegion mTr = null;
	//
	private int mCurrentWidthPixels = 0;
	private int mCurrentHeightPixels = 0;
	
	private int m_back_Left_offset = 0;
	private int m_back_Top_offset = 0;
	
	private int m_digit_Left_offset= 0;
	private int m_digit_Top_offset= 0;
	private int m_digit_space = 0;
	private int m_digit_LRspace = 0;
	
	private int m_date_Left_offset = 0;
	private int m_date_Top_offset = 0;
	private float m_date_textsize = 0;
	
	private int m_AM_Left_offset = 0;
	private int m_AM_Top_offset = 0;
	private float m_AM_textsize = 0;
	private IntentFilter mFilter = null;
	public DigitClock(String name, MainAppContext appContext, Context themeContext, ThemeParse themeParse)
	{
		super(appContext, name);
		mAppContext = appContext;
		mThemeContext = themeContext;
		mThemeParse = themeParse;
		init();
		x = 0;
		y = 0;
		Log.v("asdf", "DigitClock");
		this.width = WidgetClock.MODEL_WIDTH;
		this.height = WidgetClock.MODEL_HEIGHT;
	}
	
	private void readconfig()
	{
		try
		{
			if(mThemeParse != null)
			{
				if(mThemeParse.getInteger("DigitalClock_Use_This_Config") != 0)
				{
					mCurrentWidthPixels = mThemeParse.getInteger("DigitalClock_Width");
					mCurrentWidthPixels = mThemeParse.getInteger("DigitalClock_Height");
				
					m_back_Left_offset = ImageHelper.getAdaptInParentCenterWitdth(mCurrentWidthPixels, mbackBitmap.getWidth());
					m_back_Top_offset = ImageHelper.getAdaptInParentCenterWitdth(mCurrentHeightPixels, mbackBitmap.getHeight());

					m_digit_Left_offset =  mThemeParse.getInteger("DigitClock_Digit_MarginLeft") + m_back_Left_offset;
					m_digit_Top_offset = mThemeParse.getInteger("DigitClock_Digit_MarginTop") + m_back_Top_offset;

					m_digit_space = mThemeParse.getInteger("DigitClock_Digit_Space");
					m_digit_LRspace = mThemeParse.getInteger("DigitClock_Digit_LRSpace");
					
					m_date_Left_offset =  mThemeParse.getInteger("DigitClock_Date_MarginLeft") + m_back_Left_offset;
					m_date_Top_offset =  mThemeParse.getInteger("DigitClock_Date_MarginTop")+ m_back_Top_offset;
					m_date_textsize =  mThemeParse.getInteger("DigitClock_Date_TextSize");
					
					m_AM_Left_offset =  mThemeParse.getInteger("DigitClock_AM_MarginLeft") + m_back_Left_offset;
					m_AM_Top_offset =  mThemeParse.getInteger("DigitClock_AM_MarginTop")+ m_back_Top_offset;
					m_AM_textsize =  mThemeParse.getInteger("DigitClock_AM_TextSize");
				}
				else
				{
					mCurrentWidthPixels = (int)mAppContext.mWidgetContext.getResources().getDimension(
								R.dimen.digitclock_width);
					mCurrentHeightPixels = (int)mAppContext.mWidgetContext.getResources().getDimension(
								R.dimen.digitclock_height);
					 
			
					m_back_Left_offset = ImageHelper.getAdaptInParentCenterWitdth(mCurrentWidthPixels, mbackBitmap.getWidth());
					m_back_Top_offset = ImageHelper.getAdaptInParentCenterWitdth(mCurrentHeightPixels, mbackBitmap.getHeight());
						
					m_digit_Left_offset = (int)mAppContext.mWidgetContext.getResources().getDimension(
							R.dimen.digitclock_digit_marginLeft) + m_back_Left_offset;
					m_digit_Top_offset = (int)mAppContext.mWidgetContext.getResources().getDimension(
							R.dimen.digitclock_digit_marginTop) + m_back_Top_offset;
					m_digit_space = (int)mAppContext.mWidgetContext.getResources().getDimension(
							R.dimen.digitclock_digit_space);
					m_digit_LRspace = (int)mAppContext.mWidgetContext.getResources().getDimension(
							R.dimen.digitclock_digit_LRspace);
					Log.e("asdf", "readconfig m_back_Left_offset="+m_back_Left_offset+",m_back_Top_offset="+m_back_Top_offset);
					Log.e("asdf", "readconfig m_digit_Left_offset="+m_digit_Left_offset+",m_digit_Top_offset="+m_digit_Top_offset);
					
					
					m_date_Left_offset =  (int)mAppContext.mWidgetContext.getResources().getDimension(
							R.dimen.digitclock_date_marginLeft) + m_back_Left_offset;
					m_date_Top_offset =  (int)mAppContext.mWidgetContext.getResources().getDimension(
							R.dimen.digitclock_date_marginTop)+ m_back_Top_offset;
					
					m_date_textsize =  (int)mAppContext.mWidgetContext.getResources().getDimension(
							R.dimen.digitclock_date_textsize);
						
					m_AM_Left_offset =  (int)mAppContext.mWidgetContext.getResources().getDimension(
							R.dimen.digitclock_AM_marginLeft) + m_back_Left_offset;
					m_AM_Top_offset =  (int)mAppContext.mWidgetContext.getResources().getDimension(
							R.dimen.digitclock_AM_marginTop) + m_back_Top_offset;
					m_AM_textsize =  (int)mAppContext.mWidgetContext.getResources().getDimension(
							R.dimen.digitclock_AM_textsize);
				}
			}
			else
			{
				mCurrentWidthPixels = (int)mAppContext.mWidgetContext.getResources().getDimension(
							R.dimen.digitclock_width);
				mCurrentHeightPixels = (int)mAppContext.mWidgetContext.getResources().getDimension(
							R.dimen.digitclock_height);
				 
				m_back_Left_offset = ImageHelper.getAdaptInParentCenterWitdth(mCurrentWidthPixels, mbackBitmap.getWidth());
				m_back_Top_offset = ImageHelper.getAdaptInParentCenterWitdth(mCurrentHeightPixels, mbackBitmap.getHeight());
					
				m_digit_Left_offset = (int)mAppContext.mWidgetContext.getResources().getDimension(
						R.dimen.digitclock_digit_marginLeft) + m_back_Left_offset;
				m_digit_Top_offset = (int)mAppContext.mWidgetContext.getResources().getDimension(
						R.dimen.digitclock_digit_marginTop) + m_back_Top_offset;
				m_digit_space = (int)mAppContext.mWidgetContext.getResources().getDimension(
						R.dimen.digitclock_digit_space);
				m_digit_LRspace = (int)mAppContext.mWidgetContext.getResources().getDimension(
						R.dimen.digitclock_digit_LRspace);
					
				m_date_Left_offset =  (int)mAppContext.mWidgetContext.getResources().getDimension(
						R.dimen.digitclock_date_marginLeft) + m_back_Left_offset;
				m_date_Top_offset =  (int)mAppContext.mWidgetContext.getResources().getDimension(
						R.dimen.digitclock_date_marginTop)+ m_back_Top_offset;
				m_date_textsize =  (int)mAppContext.mWidgetContext.getResources().getDimension(
						R.dimen.digitclock_date_textsize);
					
				m_AM_Left_offset =  (int)mAppContext.mWidgetContext.getResources().getDimension(
						R.dimen.digitclock_AM_marginLeft) + m_back_Left_offset;
				m_AM_Top_offset =  (int)mAppContext.mWidgetContext.getResources().getDimension(
						R.dimen.digitclock_AM_marginTop) + m_back_Top_offset;
				m_AM_textsize =  (int)mAppContext.mWidgetContext.getResources().getDimension(
						R.dimen.digitclock_AM_textsize);
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	private void init()
	{
		
		//read 
		 mbackBitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_BACK_IMG);
		
		 mTime0Bitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_0);
		 mTime1Bitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_1);
		 mTime2Bitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_2);
		 mTime3Bitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_3);
		 mTime4Bitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_4);
		 mTime5Bitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_5);
		 mTime6Bitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_6);
		 mTime7Bitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_7);
		 mTime8Bitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_8);
		 mTime9Bitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_9);
		 mTimeAMBitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_AM);
		 mTimePMBitmap = ImageHelper.getImageFromAssetsFile(this.mThemeContext,TIME_PM);
		 
		 mbackBitmap = ImageHelper.makeAdaptBitmap(mAppContext.mWidgetContext,mbackBitmap);
		 mTime0Bitmap = ImageHelper.makeAdaptBitmap(mAppContext.mWidgetContext,mTime0Bitmap);
		 mTime1Bitmap = ImageHelper.makeAdaptBitmap(mAppContext.mWidgetContext,mTime1Bitmap);
		 mTime2Bitmap = ImageHelper.makeAdaptBitmap(mAppContext.mWidgetContext,mTime2Bitmap);
		 mTime3Bitmap = ImageHelper.makeAdaptBitmap(mAppContext.mWidgetContext,mTime3Bitmap);
		 mTime4Bitmap = ImageHelper.makeAdaptBitmap(mAppContext.mWidgetContext,mTime4Bitmap);
		 mTime5Bitmap = ImageHelper.makeAdaptBitmap(mAppContext.mWidgetContext,mTime5Bitmap);
		 mTime6Bitmap = ImageHelper.makeAdaptBitmap(mAppContext.mWidgetContext,mTime6Bitmap);
		 mTime7Bitmap = ImageHelper.makeAdaptBitmap(mAppContext.mWidgetContext,mTime7Bitmap);
		 mTime8Bitmap = ImageHelper.makeAdaptBitmap(mAppContext.mWidgetContext,mTime8Bitmap);
		 mTime9Bitmap = ImageHelper.makeAdaptBitmap(mAppContext.mWidgetContext,mTime9Bitmap);
		 mTimeAMBitmap= ImageHelper.makeAdaptBitmap(mAppContext.mWidgetContext,mTimeAMBitmap);
		 mTimePMBitmap= ImageHelper.makeAdaptBitmap(mAppContext.mWidgetContext,mTimePMBitmap);
	 
		
		 readconfig();
		 mFinalBitmap = Bitmap.createBitmap(mCurrentWidthPixels,mCurrentHeightPixels,Config.ARGB_8888);
		 
		 
		 mPaint = new Paint();
		 mPaint.setAntiAlias(true);//防锯齿
		 mPaint.setDither(true);//防抖动
		 mPaint.setStyle(Paint.Style.STROKE);
		 mPaint.setStrokeJoin(Paint.Join.ROUND);
		 mPaint.setStrokeCap(Paint.Cap.ROUND);
		 
		 mPaint_Text_date = new Paint();
		 mPaint_Text_date.setAntiAlias(true);//防锯齿
		 mPaint_Text_date.setDither(true);//防抖动
		 mPaint_Text_date.setStyle(Paint.Style.STROKE);
		 mPaint_Text_date.setStrokeJoin(Paint.Join.ROUND);
		 mPaint_Text_date.setStrokeCap(Paint.Cap.ROUND);
		 mPaint_Text_date.setSubpixelText(true);
		 mPaint_Text_date.setStyle(Paint.Style.STROKE);//.STROKE //.FILL_AND_STROKE
		 mPaint_Text_date.setStrokeJoin(Paint.Join.ROUND);
		 mPaint_Text_date.setStrokeCap(Paint.Cap.ROUND);
		 mPaint_Text_date.setTextSize(m_date_textsize);
		 mPaint_Text_date.setShadowLayer((float)(4/1.7), (float)(2/1.7), (float)(2/1.7), Color.BLACK);
		 mPaint_Text_date.setColor(0xffffffff);
		 
		 mPaint_Text_AM = new Paint();
		 mPaint_Text_AM.setAntiAlias(true);//防锯齿
		 mPaint_Text_AM.setDither(true);//防抖动
		 mPaint_Text_AM.setStyle(Paint.Style.STROKE);
		 mPaint_Text_AM.setStrokeJoin(Paint.Join.ROUND);
		 mPaint_Text_AM.setStrokeCap(Paint.Cap.ROUND);
		 mPaint_Text_AM.setSubpixelText(true);
		 mPaint_Text_AM.setStyle(Paint.Style.STROKE);//.STROKE //.FILL_AND_STROKE
		 mPaint_Text_AM.setStrokeJoin(Paint.Join.ROUND);
		 mPaint_Text_AM.setStrokeCap(Paint.Cap.ROUND);
		 mPaint_Text_AM.setTextSize(m_AM_textsize);
		 mPaint_Text_AM.setShadowLayer((float)(4/1.7), (float)(2/1.7), (float)(2/1.7), Color.BLACK);
		 mPaint_Text_AM.setColor(0xffffffff);
		 //
		 
		 
		 mFilter = new IntentFilter();
		 mFilter.addAction(Intent.ACTION_DATE_CHANGED);
		 mFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		 mFilter.addAction(Intent.ACTION_TIME_CHANGED);
		 mFilter.addAction(Intent.ACTION_TIME_TICK);
		 mFilter.addAction("android.intent.action.TIME_SET");
		 mAppContext.mWidgetContext.registerReceiver(mIntentReceiver, mFilter);
	        
		 Log.v("asdf", "init end mbackBitmap= "+mbackBitmap);
		 Log.v("asdf", "init end mFinalBitmap= "+mFinalBitmap);
		 /*
		 mCurrentWidthPixels = ImageHelper.getCurrentWidthPixels(mAppContext.mWidgetContext);	
		 mCurrentHeightPixels = ImageHelper.getCurrentHeightPixels(mAppContext.mWidgetContext);
		 m_left_offset = ImageHelper.getAdaptInParentCenterWitdth(mAppContext.mWidgetContext, mbackBitmap.getWidth());
		  */
	}
	
	private Bitmap timeToBitmap(int digit,boolean ishourl1)
	{
		if(digit<0 || digit>9)
			return null;
		switch(digit)
		{
			case 0:
				return mTime0Bitmap;
			case 1:
				return mTime1Bitmap;
			case 2:
				return mTime2Bitmap;
			case 3:
				return mTime3Bitmap;
			case 4:
				return mTime4Bitmap;
			case 5:
				return mTime5Bitmap;
			case 6:
				return mTime6Bitmap;
			case 7:
				return mTime7Bitmap;
			case 8:
				return mTime8Bitmap;
			case 9:	
				return mTime9Bitmap;
			default:				
		}
		return null;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		//super.draw(batch, parentAlpha);
		Log.v("asdf", ">>>>DigitClock draw mCanDraw="+mCanDraw);
		if(mCanDraw)
		{
			if(USE_DRAW_BITMAP_1)
			{
				
				Canvas canvas = new Canvas(mFinalBitmap);
				canvas.drawColor(Color.BLUE/*.TRANSPARENT*/);
	
				canvas.drawBitmap(mbackBitmap, m_back_Left_offset, m_back_Top_offset, mPaint);
	
				// get calendar
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int min = calendar.get(Calendar.MINUTE);
				int second = calendar.get(Calendar.SECOND);
				
				boolean is24Hour = android.text.format.DateFormat.is24HourFormat(mAppContext.mWidgetContext);
				Bitmap l1 = null;
				Bitmap l2 = null;
				Bitmap r1 = null;
				Bitmap r2 = null;
				boolean useAMPM = false;
				boolean isPM = false;
				if(is24Hour)
				{
					useAMPM = false;
				}
				else
				{
					if (hour > 11) 
					{
						if (hour != 12)
							hour -= 12;
						isPM  = true;
					}
					else 
					{
						if (hour == 0) 
							hour = 12;
					}
					useAMPM = true;
				}
				
				if(hour>=0 && hour<=9)
					l1 = timeToBitmap(0,true);
				else
					l1 = timeToBitmap(hour/10,true);
				l2 = timeToBitmap(hour%10,true);			
				r1 = timeToBitmap(min/10,true);
				r2 = timeToBitmap(min%10,true);
				
				//review time
				int time_left_offset = m_digit_Left_offset;
				canvas.drawBitmap(l1, time_left_offset, m_digit_Top_offset, mPaint);
				int time_left_offset_l2 = time_left_offset + l1.getWidth() + m_digit_space;
				canvas.drawBitmap(l2, time_left_offset_l2, m_digit_Top_offset, mPaint);
				
				int time_left_offset_r1 = time_left_offset_l2 + l2.getWidth() + m_digit_LRspace;
				canvas.drawBitmap(r1, time_left_offset_r1, m_digit_Top_offset, mPaint);
				int time_left_offset_r2 = time_left_offset_r1 + r1.getWidth() + m_digit_space;
				canvas.drawBitmap(r2, time_left_offset_r2, m_digit_Top_offset, mPaint);
				
				
				//text date
				int curr_month = 0, curr_day = 0, curr_week = 0;
	
				curr_month = calendar.get(Calendar.MONTH) + 1;
				curr_day = calendar.get(Calendar.DAY_OF_MONTH);
				curr_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
				
				String str = mAppContext.mWidgetContext.getResources().getString(
						R.string.date_widget_format_string);
				String sTextshow = String.format(str,
						mAppContext.mWidgetContext.getString(R.string.week0 + curr_week),
						curr_day,
						mAppContext.mWidgetContext.getString(R.string.monthA + curr_month - 1)
						);
				canvas.drawText(sTextshow, m_date_Left_offset , m_date_Top_offset, mPaint_Text_date);
				
				//text ampm
				if(useAMPM)
				{
					if(isPM)
						canvas.drawText("PM", m_AM_Left_offset, m_AM_Top_offset, mPaint_Text_AM);
					else
						canvas.drawText("AM", m_AM_Left_offset, m_AM_Top_offset, mPaint_Text_AM);
						
				}
		
				
				if(mTr != null)
				{
					if(mTr.getTexture() != null)
					{
						BitmapTexture bt = (BitmapTexture)mTr.getTexture();
						bt.dispose();
						bt = null;
					}
				}
				if(mBtt != null)
				{
					mBtt.dispose();
					mBtt = null;
				}
				
				mBtt = new BitmapTexture(mFinalBitmap);
				mBtt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				mTr = new TextureRegion(mBtt);
				batch.setColor(color.r, color.g, color.b, 1);
				batch.draw(mTr, 0, 0);
	
				System.gc();
			}
			else
			{
				Canvas canvas = new Canvas(mFinalBitmap);
				canvas.drawColor(Color.TRANSPARENT);

				RectF rect = new RectF(m_back_Left_offset,m_back_Top_offset,mbackBitmap.getWidth()+m_back_Left_offset,mbackBitmap.getHeight()+m_back_Top_offset);
				canvas.drawBitmap(mbackBitmap,
						new Rect(0,0,mbackBitmap.getWidth(),mbackBitmap.getHeight()),
						rect,
						mPaint);
			//	canvas.drawBitmap(mbackBitmap, m_back_Left_offset, m_back_Top_offset, mPaint);

				// get calendar
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int min = calendar.get(Calendar.MINUTE);
				int second = calendar.get(Calendar.SECOND);
				
				boolean is24Hour = android.text.format.DateFormat.is24HourFormat(mAppContext.mWidgetContext);
				Bitmap l1 = null;
				Bitmap l2 = null;
				Bitmap r1 = null;
				Bitmap r2 = null;
				boolean useAMPM = false;
				boolean isPM = false;
				if(is24Hour)
				{
					useAMPM = false;
				}
				else
				{
					if (hour > 11) 
					{
						if (hour != 12)
							hour -= 12;
						isPM  = true;
					}
					else 
					{
						if (hour == 0) 
							hour = 12;
					}
					useAMPM = true;
				}
				
				if(hour>=0 && hour<=9)
					l1 = timeToBitmap(0,true);
				else
					l1 = timeToBitmap(hour/10,true);
				l2 = timeToBitmap(hour%10,true);			
				r1 = timeToBitmap(min/10,true);
				r2 = timeToBitmap(min%10,true);
				
				//review time
				int time_left_offset = m_digit_Left_offset;
				//canvas.drawBitmap(l1, time_left_offset, m_digit_Top_offset, mPaint);
				Log.v("asdf", "time_left_offset = "+time_left_offset+",m_digit_Top_offset="+m_digit_Top_offset);
				RectF rect1 = new RectF(time_left_offset,m_digit_Top_offset,l1.getWidth()+time_left_offset,l1.getHeight()+m_digit_Top_offset);
				canvas.drawBitmap(l1,
						new Rect(0,0,l1.getWidth(),l1.getHeight()),
						rect1,
						mPaint);

				int time_left_offset_l2 = time_left_offset + l1.getWidth() + m_digit_space;
				//canvas.drawBitmap(l2, time_left_offset_l2, m_digit_Top_offset, mPaint);
				RectF rect2 = new RectF(time_left_offset_l2,m_digit_Top_offset,l2.getWidth()+time_left_offset_l2,l2.getHeight()+m_digit_Top_offset);
				canvas.drawBitmap(l2,
						new Rect(0,0,l2.getWidth(),l2.getHeight()),
						rect2,
						mPaint);
				
				int time_left_offset_r1 = time_left_offset_l2 + l2.getWidth() + m_digit_LRspace;
				//canvas.drawBitmap(r1, time_left_offset_r1, m_digit_Top_offset, mPaint);
				RectF rect3 = new RectF(time_left_offset_r1,m_digit_Top_offset,r1.getWidth()+time_left_offset_r1,r1.getHeight()+m_digit_Top_offset);
				canvas.drawBitmap(r1,
						new Rect(0,0,r1.getWidth(),r1.getHeight()),
						rect3,
						mPaint);
				int time_left_offset_r2 = time_left_offset_r1 + r1.getWidth() + m_digit_space;
				//canvas.drawBitmap(r2, time_left_offset_r2, m_digit_Top_offset, mPaint);
				RectF rect4 = new RectF(time_left_offset_r2,m_digit_Top_offset,r2.getWidth()+time_left_offset_r2,r2.getHeight()+m_digit_Top_offset);
				canvas.drawBitmap(r2,
						new Rect(0,0,r2.getWidth(),r2.getHeight()),
						rect4,
						mPaint);

				
				//text date
				int curr_month = 0, curr_day = 0, curr_week = 0;

				curr_month = calendar.get(Calendar.MONTH) + 1;
				curr_day = calendar.get(Calendar.DAY_OF_MONTH);
				curr_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
				
				String str = mAppContext.mWidgetContext.getResources().getString(
						R.string.date_widget_format_string);
				String sTextshow = String.format(str,
						mAppContext.mWidgetContext.getString(R.string.week0 + curr_week),
						mAppContext.mWidgetContext.getString(R.string.monthA + curr_month - 1),
						curr_day
						);
				canvas.drawText(sTextshow, m_date_Left_offset , m_date_Top_offset, mPaint_Text_date);
				
				//text ampm
				if(useAMPM)
				{
					if(isPM)
						canvas.drawText("PM", m_AM_Left_offset, m_AM_Top_offset, mPaint_Text_AM);
					else
						canvas.drawText("AM", m_AM_Left_offset, m_AM_Top_offset, mPaint_Text_AM);
						
				}
		
				
				if(mTr != null)
				{
					if(mTr.getTexture() != null)
					{
						BitmapTexture bt = (BitmapTexture)mTr.getTexture();
						bt.dispose();
						bt = null;
					}
				}
				if(mBtt != null)
				{
					mBtt.dispose();
					mBtt = null;
				}
				
				mBtt = new BitmapTexture(mFinalBitmap);
				mBtt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				mTr = new TextureRegion(mBtt);
				batch.setColor(color.r, color.g, color.b, 1);
				batch.draw(mTr, 0, 0);		
				System.gc();
				mCanDraw = false;
			}
	
		}
		else
		{
			batch.setColor(color.r, color.g, color.b, 1);
			batch.draw(mTr, 0, 0);
		}
	}
	
	@Override
	public void hide() {
		// TODO Auto-generated method stub
		super.hide();
		mCanDraw = false;
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		mCanDraw = true;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		Log.v("asdf", "disposem backBitmap = "+mbackBitmap);
		if(mbackBitmap != null)
		{
			mbackBitmap.recycle();
			mbackBitmap = null;
		}
		if(mTime0Bitmap != null)
		{
			mTime0Bitmap.recycle();
			mTime0Bitmap = null;
		}
		if(mTime1Bitmap != null)
		{
			mTime1Bitmap.recycle();
			mTime1Bitmap = null;
		}
		if(mTime2Bitmap != null)
		{
			mTime2Bitmap.recycle();
			mTime2Bitmap = null;
		}
		if(mTime3Bitmap != null)
		{
			mTime3Bitmap.recycle();
			mTime3Bitmap = null;
		}
		if(mTime4Bitmap != null)
		{
			mTime4Bitmap.recycle();
			mTime4Bitmap = null;
		}
		if(mTime5Bitmap != null)
		{
			mTime5Bitmap.recycle();
			mTime5Bitmap = null;
		}
		if(mTime6Bitmap != null)
		{
			mTime6Bitmap.recycle();
			mTime6Bitmap = null;
		}
		if(mTime7Bitmap != null)
		{
			mTime7Bitmap.recycle();
			mTime7Bitmap = null;
		}
		if(mTime8Bitmap != null)
		{
			mTime8Bitmap.recycle();
			mTime8Bitmap = null;
		}
		if(mTime9Bitmap != null)
		{
			mTime9Bitmap.recycle();
			mTime9Bitmap = null;
		}
		if(mTimeAMBitmap != null)
		{
			mTimeAMBitmap.recycle();
			mTimeAMBitmap = null;
		}
		if(mTimePMBitmap != null)
		{
			mTimePMBitmap.recycle();
			mTimePMBitmap = null;
		}
		Log.v("asdf", "disposem mFinalBitmap = "+mFinalBitmap);
		if(mFinalBitmap != null)
		{
			mFinalBitmap.recycle();
			mFinalBitmap = null;
		}
		Log.v("asdf", "disposem mTr = "+mTr);
		if(mTr != null)
		{
			if(mTr.getTexture() != null)
			{
				BitmapTexture bt = (BitmapTexture)mTr.getTexture();
				bt.dispose();
				bt = null;
			}
		}
		if(mBtt != null)
		{
			mBtt.dispose();
			mBtt = null;
		}
		if(mFilter != null)
		{
			this.mAppContext.mWidgetContext.unregisterReceiver(mIntentReceiver);
			mFilter = null;
		}
	}
	
	@Override
	public boolean onClick(float x, float y) {
		// TODO Auto-generated method stub
		Log.v("WidgetClock", "SimulatorClock onClick");	
		try {
			
			Intent i2 = new Intent(Settings.ACTION_DATE_SETTINGS);
			i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mAppContext.mWidgetContext.startActivity(i2);
			return true;
				
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
		return false;
	}
	
	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED))
                    || (intent.getAction().equals(Intent.ACTION_TIME_TICK))
                    || (intent.getAction().equals(Intent.ACTION_DATE_CHANGED))
                    || (intent.getAction().equals(Intent.ACTION_TIME_CHANGED))
                    || (intent.getAction().equals("android.intent.action.TIME_SET"))) {
            	mCanDraw = true;
            }
        }
    };
}
