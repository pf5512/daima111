package com.iLoong.launcher.Desktop3D;


import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.UI3DEngine.Desktop3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class HotButton extends View3D
{
	
	public static int MSG_HOTBUTTON_CLICK = 0;
	public static boolean animating = false;
	private TextureRegion focus = R3D.getTextureRegion( "control-del-Telephone-dial2" );
	private TextureRegion normal;
	private boolean isFocus;
	
	public HotButton(
			String name ,
			TextureRegion region )
	{
		super( name , region );
		normal = region;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void show()
	{
		super.show();
		this.setUser( 0f );
		this.stopTween();
		this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.8f , Utils3D.getScreenWidth() - getWidth() , 0 , 0 ).setCallback( this );
		animating = true;
	}
	
	@Override
	public void hide()
	{
		this.stopTween();
		this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.8f , Utils3D.getScreenWidth() , 0 , 0 ).setCallback( this );
		animating = true;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		requestFocus();
		isFocus = true;
		return true;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		if( ( (Desktop3D)getStage() ).getFocus() != this )
			return false;
		releaseFocus();
		isFocus = false;
		SendMsgToAndroid.launchHotButton();
		return true;
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		animating = false;
		super.onEvent( type , source );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		if( isFocus )
			region = focus;
		else
			region = normal;
		super.draw( batch , parentAlpha );
	}
}
