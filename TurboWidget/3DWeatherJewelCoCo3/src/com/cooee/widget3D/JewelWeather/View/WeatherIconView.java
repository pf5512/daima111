package com.cooee.widget3D.JewelWeather.View;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.os.BatteryManager;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooee.weather.com.WeatherCondition;
import com.cooee.widget3D.JewelWeather.Common.Animation;
import com.cooee.widget3D.JewelWeather.Common.FrameAnimation;
import com.cooee.widget3D.JewelWeather.Common.WeatherHelper;
import com.cooee.widget3D.JewelWeather.Common.WidgetTimer;
import com.cooee.widget3D.JewelWeather.Common.WidgetTimerListener;
import com.cooee.widget3D.JewelWeather.Common.FrameAnimation.IFrameRefreshCallback;
import com.cooee.widget3D.JewelWeather.View.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.UI3DEngine.adapter.IRefreshRender;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Object3DBase;

public class WeatherIconView extends PluginViewObject3D implements
	WidgetTimerListener, IFrameRefreshCallback{
	private static final String WEATHER = "widget_weather.obj";
	private static final String WEATHER_IMG_NAME = "widget_weather_";//cloud_day.png";
	private Cache<String, Mesh> mMeshCache = null;
	private MainAppContext mAppContext;
	public CooGdx cooGdx;
	public boolean mFrist = true;
	public TextureRegion[] walksFrame = null;
	// 图片帧动画
	private FrameAnimation mFrameAnimation = null;
//	private WidgetTimer mTimer = null;
	private boolean AnimationIsDoing = false;
	private TextureRegion fristTR = null;
//	private boolean initRegionOK = false;
	private WeatherCondition.Condition mCurrCondition = WeatherCondition.Condition.WEATHER_UNKOWN;

	public WeatherIconView(String name, MainAppContext appContext) {
		super(appContext, name, WEATHER_IMG_NAME+"cloud_day.png", WEATHER);
		mAppContext = appContext;
		this.setSize(WidgetWeather.MODEL_WIDTH,WidgetWeather.MODEL_HEIGHT);
		this.setMoveOffset(WidgetWeather.MODEL_WIDTH /2 +WidgetWeather.mClock_Center_Offset_X, 
				WidgetWeather.MODEL_HEIGHT /2,
				0);
		mFrist = true;
		this.setDepthMode(true);
		fristTR = this.region = getTextureRegion(mAppContext,null);	
	//	initRegionOK = true;
		this.build();

	}
	public void setRefreshRender(IRefreshRender refreshRender) {
		this.mRefreshRender = refreshRender;
	}
	


	private TextureRegion getTextureRegion(MainAppContext appContext,String weathet) 
	{
		BitmapTexture bt = null;
		if(mFrist)
		{
			bt = getTransparentBitmap();
			mFrist = false;
		}
		else
			bt = getBitmapTextureByWeather(weathet);
		
		bt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return new TextureRegion(bt);
	}
	
	private BitmapTexture getTransparentBitmap()
	{
		BitmapTexture ret = null;
		AndroidFiles gdxFile = new AndroidFiles(appContext.mWidgetContext.getAssets());
		String texturePath = getThemeTexturePath("widget_weather_init.png");
		FileHandle fileHandle = gdxFile.internal(texturePath);
		Bitmap bm = BitmapFactory.decodeStream(fileHandle.read());
		ret = new BitmapTexture(bm);
		bm.recycle();
		return ret;
	}
	
	private BitmapTexture getBitmapTextureByWeather(String weather)
	{
		BitmapTexture ret = null;
		String name = getwidgetConditionImagePath(weather,0);
		AndroidFiles gdxFile = new AndroidFiles(appContext.mWidgetContext.getAssets());
		String texturePath = getThemeTexturePath(name);
		FileHandle fileHandle = gdxFile.internal(texturePath);
		Bitmap bm = BitmapFactory.decodeStream(fileHandle.read());
		ret = new BitmapTexture(bm);
		bm.recycle();
		return ret;
	}
	
	private WeatherCondition.Condition checkCondition(WeatherCondition.Condition weather)
	{
		WeatherCondition.Condition ret = WeatherCondition.Condition.WEATHER_UNKOWN;
		switch (weather) 
		{
	        case WEATHER_FINE:
	        	ret = WeatherCondition.Condition.WEATHER_FINE;
	            break;
	        case WEATHER_CLOUDY:
	        	ret = WeatherCondition.Condition.WEATHER_CLOUDY;
	            break;
	        case WEATHER_OVERCAST:
	        	ret = WeatherCondition.Condition.WEATHER_OVERCAST;
	            break;
	        case WEATHER_SNOW:
	        	ret = WeatherCondition.Condition.WEATHER_SNOW;
	            break;
	        case WEATHER_THUNDERSTORM:
	        	ret = WeatherCondition.Condition.WEATHER_THUNDERSTORM;
	            break;
	        case WEATHER_SLEET:
	        case WEATHER_STORM:
	        case WEATHER_LIGHTRAIN:
	        case WEATHER_RAIN:
	        case WEATHER_RAINSTORM:
	        	ret = WeatherCondition.Condition.WEATHER_RAIN;
	            break;
	        case WEATHER_HAZE:
	        case WEATHER_FOG:
	        	ret = WeatherCondition.Condition.WEATHER_FOG;
	        default:
	        	ret = WeatherCondition.Condition.WEATHER_OVERCAST;
	            break;
		}
		return ret;
	}
	
	private String getwidgetConditionImagePath(String weather,int index)
	{
		WeatherCondition.Condition c = WeatherCondition.convertCondition(weather);
		String path = WEATHER_IMG_NAME+"overcast";   
		switch (c) {
	        case WEATHER_FINE:
	            path = "fine/"+WEATHER_IMG_NAME+"fine_"+Integer.toString(index+1)+".png";
	            mCurrCondition = WeatherCondition.Condition.WEATHER_FINE;
	            break;
	        case WEATHER_CLOUDY:
	            path = "cloudy/"+WEATHER_IMG_NAME+"cloudy_"+Integer.toString(index+1)+".png";
	            mCurrCondition = WeatherCondition.Condition.WEATHER_CLOUDY;
	            break;
	        case WEATHER_OVERCAST:
	        	path = "overcast/"+WEATHER_IMG_NAME+"overcast_"+Integer.toString(index+1)+".png";
	        	mCurrCondition = WeatherCondition.Condition.WEATHER_OVERCAST;
	            break;
	        case WEATHER_SNOW:
	        	path = "snow/"+WEATHER_IMG_NAME+"snow_"+Integer.toString(index+1)+".png";
	        	mCurrCondition = WeatherCondition.Condition.WEATHER_SNOW;
	            break;
	        case WEATHER_THUNDERSTORM:
	        	path = "thunder/"+WEATHER_IMG_NAME+"thunder_"+Integer.toString(index+1)+".png";
	        	mCurrCondition = WeatherCondition.Condition.WEATHER_THUNDERSTORM;
	            break;
	        case WEATHER_SLEET:
	        case WEATHER_STORM:
	        case WEATHER_LIGHTRAIN:
	        case WEATHER_RAIN:
	        case WEATHER_RAINSTORM:
	        	path = "rain/"+WEATHER_IMG_NAME+"rain_"+Integer.toString(index+1)+".png";
	        	mCurrCondition = WeatherCondition.Condition.WEATHER_RAIN;
	            break;
	        case WEATHER_HAZE:
	        case WEATHER_FOG:
	        	path = "fog/"+WEATHER_IMG_NAME+"fog_"+Integer.toString(index+1)+".png";
	        	mCurrCondition = WeatherCondition.Condition.WEATHER_FOG;
	        default:
	        	path = "overcast/"+WEATHER_IMG_NAME+"overcast_"+Integer.toString(index+1)+".png";
	        	mCurrCondition = WeatherCondition.Condition.WEATHER_OVERCAST;
	            break;
		}
		return path;
	}
	
	public void updataDate(final String weathet)
	{
		// TODO Auto-generated method stub	
		Log.e("weijie", "updataDate AnimationIsDoing="+AnimationIsDoing);
		if(AnimationIsDoing)
		{
			stop();	
		}
		
		if(weathet == null)
		{
			Log.e("weijie", "updataDate walksFrame="+walksFrame);
			if(walksFrame != null && mFrameAnimation != null)
			{
				for (int i = 0; i < walksFrame.length; i++) 
				{
					BitmapTexture oldTexture = (BitmapTexture) walksFrame[i].getTexture();
					if (oldTexture != null) 
					{
						oldTexture.dispose();
					}
				}
				mFrameAnimation = null;
				walksFrame = null;
			}
			mFrist = true;
			this.region = getTextureRegion(mAppContext,null);				
		}	
		else
		{
		appContext.mGdxApplication.postRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				WeatherCondition.Condition c = WeatherCondition.convertCondition(weathet);
	//			Log.e("aq", "updataDate mCurrCondition="+mCurrCondition+",c="+c);
				if(mCurrCondition == checkCondition(c))
				{
					start();
				}
				else
				{
	//				initRegionOK = false;
					resetFrameAnimation(weathet);
	//				initRegionOK = true;
					start();
				}
				
				if(fristTR != null)
				{
					BitmapTexture old = (BitmapTexture)fristTR.getTexture();
					if(old != null)
						old.dispose();
					fristTR = null;
				}
			}
		});	
	}
	//	Log.e("aq", "updataDate22 AnimationIsDoing="+AnimationIsDoing);
		
	}

	private void initFrameAnimation(String weather)
	{
		if(weather == null)
		{
			mFrist = true;
			this.region = getTextureRegion(mAppContext,null);	
			mFrameAnimation = null;
			walksFrame = null;
			return;
		}
		
		
		int framecount = getFrameCount(weather);
		if(framecount <= 0 || framecount >6) 
			return;
		walksFrame = new TextureRegion[framecount];
		AndroidFiles gdxFile = new AndroidFiles(appContext.mWidgetContext.getAssets());
		for (int i = 0; i < framecount; i++) {
			String path = getThemeName();
			path = path + "/image/" + getwidgetConditionImagePath(weather,i);
			FileHandle fileHandle = gdxFile.internal(path);
			Bitmap bm = BitmapFactory.decodeStream(fileHandle.read());
			BitmapTexture bt = new BitmapTexture(bm);
			bt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			walksFrame[i] = new TextureRegion(bt);
			if(i<=0)
			{
				
				BitmapTexture oldTexture = (BitmapTexture) this.region.getTexture();	
				if (oldTexture != null) 
				{
					oldTexture.dispose();
				}
				
				this.region = walksFrame[i];
			}
		}
		mFrameAnimation = new FrameAnimation(walksFrame);
		mFrameAnimation.callback = this;
		
	}
	
	private void resetFrameAnimation(String weather)
	{
		Log.e("weijie", "resetFrameAnimation mFrameAnimation="+mFrameAnimation+",walksFrame="+walksFrame);
		if (mFrameAnimation != null && walksFrame != null) 
		{
			
			{
				AndroidFiles gdxFile = new AndroidFiles(appContext.mWidgetContext.getAssets());
				String path = getThemeName();
				path = path + "/image/" + getwidgetConditionImagePath(weather,0);
				FileHandle fileHandle = gdxFile.internal(path);
				Bitmap bm = BitmapFactory.decodeStream(fileHandle.read());
				BitmapTexture bt = new BitmapTexture(bm);
				bt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				this.region = new TextureRegion(bt);
			}
		
			for (int i = 0; i < walksFrame.length; i++) 
			{
				BitmapTexture oldTexture = (BitmapTexture) walksFrame[i].getTexture();
				if (oldTexture != null) 
				{
					oldTexture.dispose();
				}
			}
			mFrameAnimation = null;
			walksFrame = null;
		}
		
		
		initFrameAnimation(weather);	
	}
	
	private int getFrameCount(String weather)
	{
		WeatherCondition.Condition c = WeatherCondition.convertCondition(weather);
		int ret = 0;
		switch (c) {
	        case WEATHER_FINE:
	        case WEATHER_CLOUDY:
	        case WEATHER_OVERCAST:
	        	
	        case WEATHER_SLEET:
	        case WEATHER_STORM:
	        case WEATHER_LIGHTRAIN:
	        case WEATHER_RAIN:
	        case WEATHER_RAINSTORM:
	        	
	        case WEATHER_SNOW:
	        case WEATHER_THUNDERSTORM:
	        	ret = 6;
	            break;
	        
	        case WEATHER_HAZE:
	        case WEATHER_FOG:
	        	ret = 5;
	        	break;
	        default:
	            break;
		}
		return ret;
	}

	public void start() {
		if (mFrameAnimation != null) {
			mFrameAnimation.start();
			
		}
	//	Log.v("aq", "mRefreshRender = "+mRefreshRender);
		if (mRefreshRender != null) {
			mRefreshRender.RefreshRender();
		}
	}

	public void stop() {
		if (mFrameAnimation != null) {
			mFrameAnimation.stop();
		}
	}
	
	@Override
	public void timeChanged() {
		if (mFrameAnimation != null) {
			mFrameAnimation.start();
		}
		if (mRefreshRender != null) {
			mRefreshRender.RefreshRender();
		}
	}
	
	@Override
	public void refreshRegion(final TextureRegion region) {
		// TODO Auto-generated method stub
		if (mRefreshRender != null) {
			mRefreshRender.RefreshRender();
		}
	//	Log.v("aq", "refreshRegion region= "+region);
		// TODO Auto-generated method stub
		appContext.mGdxApplication.postRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				WeatherIconView.this.region.setRegion(region);
			}
		});
	}

	@Override
	public void endRefreshRegion() {
		// TODO Auto-generated method stub
		AnimationIsDoing = false;
	//	Log.e("aq", "endRefreshRegion AnimationIsDoing="+AnimationIsDoing);
	}

	@Override
	public void beginRefreshRegion() {
		// TODO Auto-generated method stub
		
		AnimationIsDoing = true;
	//	Log.e("aq", "beginRefreshRegion AnimationIsDoing="+AnimationIsDoing);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		if (mFrameAnimation != null && walksFrame.length > 0) {
			for (int i = 0; i < walksFrame.length; i++) {
				BitmapTexture tempTexture = (BitmapTexture) walksFrame[i].getTexture();
				if (tempTexture != null) {
					tempTexture.dispose();
				}
			}
		}

		if (mFrameAnimation != null) {
			mFrameAnimation.stop();
			mFrameAnimation = null;
		}	
	}
	
	public void onWidgetDeleted() {
		// 释放纹理
		if (mFrameAnimation != null && walksFrame != null) 
		{
			if(!mFrameAnimation.isTaskStopped)
				mFrameAnimation.stop();
			for (int i = 0; i < walksFrame.length; i++) 
			{
				BitmapTexture oldTexture = (BitmapTexture) walksFrame[i].getTexture();
				if (oldTexture != null) 
				{
					oldTexture.dispose();
				}
			}
			mFrameAnimation = null;
			walksFrame = null;
		}
	}
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
	//	if(initRegionOK)
		super.draw(batch, parentAlpha);
	}
	
	
}
