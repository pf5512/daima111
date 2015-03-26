package com.iLoong.launcher.tween;


import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.Color;
import com.iLoong.launcher.UI3DEngine.View3D;


public class View3DTweenAccessor implements TweenAccessor<View3D>
{
	
	public static final int POS_XY = 1;
	public static final int CPOS_XY = 2;
	public static final int SCALE_XY = 3;
	public static final int ROTATION = 4;
	public static final int OPACITY = 5;
	public static final int TINT = 6;
	public static final int USER = 7;
	public static final int USER2 = 8;
	public final static TweenManager manager = new TweenManager();
	
	@Override
	public int getValues(
			View3D target ,
			int tweenType ,
			float[] returnValues )
	{
		switch( tweenType )
		{
			case POS_XY:
				returnValues[0] = target.getX();
				returnValues[1] = target.getY();
				return 2;
			case CPOS_XY:
				returnValues[0] = target.getX() + target.getWidth() / 2;
				returnValues[1] = target.getY() + target.getHeight() / 2;
				return 2;
			case SCALE_XY:
				returnValues[0] = target.getScaleX();
				returnValues[1] = target.getScaleY();
				return 2;
			case ROTATION:
				returnValues[0] = target.getRotation();
				return 1;
			case OPACITY:
				returnValues[0] = target.getColor().a;
				return 1;
			case TINT:
				returnValues[0] = target.getColor().r;
				returnValues[1] = target.getColor().g;
				returnValues[2] = target.getColor().b;
				return 3;
			case USER:
				returnValues[0] = target.getUser();
				return 1;
			case USER2:
				returnValues[0] = target.getUser2();
				return 1;
			default:
				assert false;
				return -1;
		}
	}
	
	@Override
	public void setValues(
			View3D target ,
			int tweenType ,
			float[] newValues )
	{
		switch( tweenType )
		{
			case POS_XY:
				target.setPosition( newValues[0] , newValues[1] );
				break;
			case CPOS_XY:
				target.setPosition( newValues[0] - target.getWidth() / 2 , newValues[1] - target.getHeight() / 2 );
				break;
			case SCALE_XY:
				target.setScale( newValues[0] , newValues[1] );
				break;
			case ROTATION:
				target.setRotation( newValues[0] );
				break;
			case OPACITY:
				Color c = target.getColor();
				c.set( c.r , c.g , c.b , newValues[0] );
				target.setColor( c );
				break;
			case TINT:
				c = target.getColor();
				c.set( newValues[0] , newValues[1] , newValues[2] , c.a );
				target.setColor( c );
				break;
			case USER:
				target.setUser( newValues[0] );
				break;
			case USER2:
				target.setUser2( newValues[0] );
				break;
			default:
				assert false;
		}
	}
}
