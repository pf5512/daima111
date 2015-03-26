package com.cooee.widget3D.JewelWeather.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooee.widget3D.JewelWeather.Common.WeatherHelper;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Object3DBase;

public class TempleteDownZeroView extends PluginViewObject3D {
	private static final String TEMPLETE_DOWNZERO = "widget_templete_downzero.obj";
	private static final String TEMPLETE_DOWNZERO_IMG_NAME = "widget_digit_downzero.png";
	private Cache<String, Mesh> mMeshCache = null;
	private MainAppContext mAppContext;
	public CooGdx cooGdx;
	public boolean mFrist = true;
	public TempleteDownZeroView(String name, MainAppContext appContext) {
		super(appContext, name, TEMPLETE_DOWNZERO_IMG_NAME, TEMPLETE_DOWNZERO);
		mAppContext = appContext;
		this.setSize(WidgetWeather.MODEL_WIDTH,WidgetWeather.MODEL_HEIGHT);
		this.setMoveOffset(WidgetWeather.MODEL_WIDTH /2 +WidgetWeather.mClock_Center_Offset_X, 
				WidgetWeather.MODEL_HEIGHT /2,
				0);
		mFrist = true;
		this.setDepthMode(true);
		this.region = getTextureRegion(mAppContext,0);
		this.build();
	}


	public void updataDate(int temp,boolean display)
	{
		
		if(display)
		{
			if(this.region != null)
			{
				BitmapTexture old = (BitmapTexture)this.region.getTexture();
				this.region = getTextureRegion(mAppContext,temp);
				if(old != null)
					old.dispose();
			}
			else
				this.region = getTextureRegion(mAppContext,temp);
		}
		else
		{
			if(this.region != null)
			{
				BitmapTexture old = (BitmapTexture)this.region.getTexture();
				this.region = getTextureRegion(mAppContext,temp);
				if(old != null)
					old.dispose();
			}
			else
			{
				mFrist = true;
				this.region = getTextureRegion(mAppContext,temp);
			}
		}
	}

	private TextureRegion getTextureRegion(MainAppContext appContext,int digit) 
	{
		BitmapTexture bt = null;
		if(mFrist)
		{
			bt = getTransparentBitmap();
			mFrist = false;
		}
		else
			bt = getBitmapTextureByid(digit);
		
		bt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return new TextureRegion(bt);
	}
	
	private BitmapTexture getTransparentBitmap()
	{
		BitmapTexture ret = null;
		Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.TRANSPARENT);
		ret = new BitmapTexture(bitmap);
		bitmap.recycle();
		ret.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return ret;
	}
	
	private BitmapTexture getBitmapTextureByid(int id)
	{
		String name;
		BitmapTexture ret = null;
		if(id >= 0)
		{
			return getTransparentBitmap();
		}
		
		AndroidFiles gdxFile = new AndroidFiles(appContext.mWidgetContext.getAssets());
		String texturePath = getThemeTexturePath(TEMPLETE_DOWNZERO_IMG_NAME);
		FileHandle fileHandle = gdxFile.internal(texturePath);
		Bitmap bm = BitmapFactory.decodeStream(fileHandle.read());
		ret = new BitmapTexture(bm);
		bm.recycle();
		ret.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return ret;
	}
}
