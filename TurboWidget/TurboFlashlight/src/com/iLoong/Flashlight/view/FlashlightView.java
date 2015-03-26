package com.iLoong.Flashlight.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cooeeui.turboflashlight.R;
import com.iLoong.Flashlight.MainActivity;
import com.iLoong.Flashlight.common.View2D;


public class FlashlightView extends View
{
	
	private float widthScale;
	private float heightScale;
	private Paint paint;
	private View2D flHandler;
	private View2D lightSwitch;
	private Bitmap switchOn;
	private Bitmap switchOff;
	private Rect srcH;
	private RectF dstH;
	private Rect srcS;
	private RectF dstS;
	private OnClickListener onClickListener;
	
	public FlashlightView(
			Context context )
	{
		super( context );
	}
	
	public FlashlightView(
			Context context ,
			AttributeSet attrs )
	{
		super( context , attrs );
		paint = new Paint();
		paint.setAntiAlias( true );
		widthScale = MainActivity.screenWidth / 720f;
		heightScale = MainActivity.screenHeight / 1280f;
		switchOn = BitmapFactory.decodeResource( getResources() , R.drawable.switch_on_nor );
		switchOff = BitmapFactory.decodeResource( getResources() , R.drawable.switch_off_nor );
		srcH = new Rect( 0 , 0 , 720 , 1280 );
		dstH = new RectF( 0 , 0 , 720f * widthScale , 1280f * widthScale );
		srcS = new Rect( 0 , 0 , 114 , 114 );
		dstS = new RectF( 302 * widthScale , 900f * widthScale , ( 302f + 114f ) * widthScale , ( 900f + 114f ) * widthScale );
		flHandler = new View2D( BitmapFactory.decodeResource( getResources() , R.drawable.flashlight_bg ) , 0 , 0 );
		lightSwitch = new View2D( switchOff , 0 , 0 );
	}
	
	public FlashlightView(
			Context context ,
			AttributeSet attrs ,
			int defStyleAttr )
	{
		super( context , attrs , defStyleAttr );
	}
	
	@Override
	protected void onDraw(
			Canvas canvas )
	{
		super.onDraw( canvas );
		canvas.drawBitmap( flHandler.getBitmap() , srcH , dstH , paint );
		canvas.drawBitmap( lightSwitch.getBitmap() , srcS , dstS , paint );
	}
	
	public void setOn()
	{
		lightSwitch.setBitmap( switchOn );
		invalidate();
	}
	
	public void setOff()
	{
		lightSwitch.setBitmap( switchOff );
		invalidate();
	}
	
	@Override
	public boolean onTouchEvent(
			MotionEvent event )
	{
		switch( event.getAction() )
		{
			case MotionEvent.ACTION_DOWN:
				if( event.getX() >= 302f * widthScale && event.getX() <= ( 302f + 114f ) * widthScale && event.getY() >= 900f * widthScale && event.getY() <= ( 900f + 114f ) * widthScale )
				{
					Log.v( "MotionEvent" , "onClick" );
					onClickListener.onClick();
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				break;
		}
		return true;
	}
	
	public void setOnClickListener(
			OnClickListener onClickListener )
	{
		this.onClickListener = onClickListener;
	}
	
	public interface OnClickListener
	{
		
		public void onClick();
	}
}
