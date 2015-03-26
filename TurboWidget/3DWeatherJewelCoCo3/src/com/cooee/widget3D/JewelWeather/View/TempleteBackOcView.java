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

public class TempleteBackOcView extends PluginViewObject3D {
	private static final String TEMPLETE_OC = "widget_templete_back_oc.obj";
	private static final String TEMPLETE_OC_IMG_NAME = "widget_oc.png";
	private Cache<String, Mesh> mMeshCache = null;
	private MainAppContext mAppContext;
	public CooGdx cooGdx;
	private TextureRegion fristTR = null;
	public boolean mFrist = true;
	public TempleteBackOcView(String name, MainAppContext appContext) 
	{
		super(appContext, name,TEMPLETE_OC_IMG_NAME, TEMPLETE_OC);
		mAppContext = appContext;
		this.setSize(WidgetWeather.MODEL_WIDTH,WidgetWeather.MODEL_HEIGHT);
		this.setMoveOffset(WidgetWeather.MODEL_WIDTH /2 +WidgetWeather.mClock_Center_Offset_X, 
				WidgetWeather.MODEL_HEIGHT /2,
				0);
		this.setDepthMode(true);
		mFrist = true;
		fristTR = this.region = getTextureRegion(mAppContext,false);
		this.build();
		
	}
	
	public void updataDate(boolean display)
	{
		if(this.region != null)
		{
			BitmapTexture old = (BitmapTexture)this.region.getTexture();
			this.region = getTextureRegion(mAppContext,display);
			if(old != null)
				old.dispose();
		}
		else
			this.region = getTextureRegion(mAppContext,display);
	}

	private TextureRegion getTextureRegion(MainAppContext appContext, boolean display) 
	{
		BitmapTexture bt = null;
	//	Log.v("aq", "oc getTextureRegion mFrist="+mFrist);
		if(mFrist)
		{
			bt = getTransparentBitmap();
			mFrist = false;
		}
		else
			bt = getBitmapTextureByid(display);
		
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
	
	private BitmapTexture getBitmapTextureByid(boolean display)
	{
		String name;
		BitmapTexture ret = null;
	//	Log.v("aq", "oc getBitmapTextureByid display="+display);
		if(!display)
		{
			return getTransparentBitmap();
		}
	//	Log.v("aq", "oc getBitmapTextureByid TEMPLETE_OC_IMG_NAME="+TEMPLETE_OC_IMG_NAME);
		AndroidFiles gdxFile = new AndroidFiles(appContext.mWidgetContext.getAssets());
		String texturePath = getThemeTexturePath(TEMPLETE_OC_IMG_NAME);
		FileHandle fileHandle = gdxFile.internal(texturePath);
		Bitmap bm = BitmapFactory.decodeStream(fileHandle.read());
		ret = new BitmapTexture(bm);
		ret.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bm.recycle();
		return ret;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		if(fristTR != null)
		{
			BitmapTexture bt = (BitmapTexture)fristTR.getTexture();
			bt.dispose();
			fristTR = null;
		}
	}
}
