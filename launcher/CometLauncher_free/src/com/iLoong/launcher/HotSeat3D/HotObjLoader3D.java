package com.iLoong.launcher.HotSeat3D;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupOBJ3D;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class HotObjLoader3D extends ViewGroupOBJ3D
{
	
	//	protected HotObjBackGround3D mBackViewUp = null;
	//	protected HotObjBackGround3D mBackViewDown = null;
	protected HotObjMenuFront objMenuUp;
	protected HotObjMenuBack objMenuDown;
	private Context mContext = null;
	private Tween mAutoScrollTween = null;
	private float lastRotationX = 0;
	
	public HotObjLoader3D(
			String name ,
			int width ,
			int height )
	{
		super( name );
		setPosition( 0 , 0 );
		setSize( width , height );
		this.setOrigin( width / 2 , height / 2 );
		transform = true;
	}
	
	public void HotObj_Loader()
	{
		mContext = iLoongApplication.ctx;
		HotObj_addBackView();
		//HotObj_addBackView1();
	}
	
	public Context getContext()
	{
		return mContext;
	}
	
	private void HotObj_addBackView()
	{
		objMenuUp = new HotObjMenuFront( "objMenuUp" , mContext , this.width , this.height );
		this.addView( objMenuUp.getRootView() );
	}
	
	public void swapSide()
	{
		//        this.removeView(objMenuUp.getRootView());
		//        this.addView(objMenuUp.getSwapedDock());
	}
	
	private void HotObj_addBackView1()
	{
		objMenuDown = new HotObjMenuBack( "objMenuDown" , mContext , this.width , this.height );
		this.addView( objMenuDown );
	}
	
	public void startScrollDownView(
			float duration )
	{
		objMenuUp.startRotation( duration );
		// objMenuDown.startRotation();
	}
	
	public void closeMenu()
	{
		objMenuUp.closeMenu();
	}
}
