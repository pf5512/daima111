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

public class TempleteOnesView extends PluginViewObject3D {
	private static final String TEMPLETE_ONES = "widget_templete_ones.obj";
	private static final String TEMPLETE_ONES_IMG_NAME = "widget_digit_";
	private Cache<String, Mesh> mMeshCache = null;
	private MainAppContext mAppContext;
	public CooGdx cooGdx;
	public boolean mFrist = true;
	public TempleteOnesView(String name, MainAppContext appContext) {
		super(appContext, name, TEMPLETE_ONES_IMG_NAME+"7.png", TEMPLETE_ONES);
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
	//	Log.v("aq", "updataDate ones temp="+temp);
		if(this.region != null)
		{
			BitmapTexture old = (BitmapTexture)this.region.getTexture();
			this.region = getTextureRegion(mAppContext,temp);
			if(old != null)
				old.dispose();
		}
		if(!display)
			mFrist = true;
		this.region = getTextureRegion(mAppContext,temp);
	}

	private TextureRegion getTextureRegion(MainAppContext appContext,int digit) 
	{
		BitmapTexture bt = null;
	//	Log.v("aq", "getTextureRegion ones mFrist="+mFrist);
		if(mFrist)
		{
			bt = getTransparentBitmap();
			mFrist = false;
		}
		else
			bt = getBitmapTextureByid(Math.abs(digit%10));
		
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
		switch(id)
		{
			case 1:
				name = TEMPLETE_ONES_IMG_NAME+"1.png";
				break;
			case 2:
				name = TEMPLETE_ONES_IMG_NAME+"2.png";
				break;
			case 3:
				name = TEMPLETE_ONES_IMG_NAME+"3.png";
				break;
			case 4:
				name = TEMPLETE_ONES_IMG_NAME+"4.png";
				break;
			case 5:
				name = TEMPLETE_ONES_IMG_NAME+"5.png";
				break;
			case 6:
				name = TEMPLETE_ONES_IMG_NAME+"6.png";
				break;
			case 7:
				name = TEMPLETE_ONES_IMG_NAME+"7.png";
				break;
			case 8:
				name = TEMPLETE_ONES_IMG_NAME+"8.png";
				break;
			case 9:
				name = TEMPLETE_ONES_IMG_NAME+"9.png";
				break;
			case 0:
				name = TEMPLETE_ONES_IMG_NAME+"0.png";
				break;
			default:
				return getTransparentBitmap();
		}
		AndroidFiles gdxFile = new AndroidFiles(appContext.mWidgetContext.getAssets());
		String texturePath = getThemeTexturePath(name);
		FileHandle fileHandle = gdxFile.internal(texturePath);
		Bitmap bm = BitmapFactory.decodeStream(fileHandle.read());
		ret = new BitmapTexture(bm);
		bm.recycle();
		ret.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return ret;
	}
}
