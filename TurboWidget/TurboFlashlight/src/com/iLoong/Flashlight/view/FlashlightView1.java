package com.iLoong.Flashlight.view;


import java.util.ArrayList;
import java.util.List;

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


public class FlashlightView1 extends View
{
	
	private float widthScale;
	private float heightScale;
	private Paint paint;
	private View2D flHandler;
	private View2D lightSwitch;
	private List<View2D> twistBars;
	private int barsCount = 21;
	private Bitmap barBitmapBlack;
	private Bitmap barBitmapBule;
	private Bitmap switchOn;
	private Bitmap switchOff;
	private Rect srcH;
	private RectF dstH;
	private Rect srcS;
	private RectF dstS;
	private List<Rect> srcBars;
	private List<RectF> dstBars;
	private OnClickListener onClickListener;
	private OnItemSelectedChanged onItemSelectedChanged;
	private float downX;
	private float moveX;
	private static float speedX = 8f;
	private int currTap = 0;
	private static final float tap1Position = 168f;
	private static final float tap2Position = 288f;
	private static final float tap3Position = 408f;
	private static final float tap4Position = 552f;
	
	public FlashlightView1(
			Context context )
	{
		super( context );
	}
	
	public FlashlightView1(
			Context context ,
			AttributeSet attrs )
	{
		super( context , attrs );
		paint = new Paint();
		paint.setAntiAlias( true );
		widthScale = MainActivity.screenWidth / 720f;
		heightScale = MainActivity.screenHeight / 1280f;
		speedX = speedX / widthScale;
		barBitmapBlack = BitmapFactory.decodeResource( getResources() , R.drawable.twist_bar_black );
		barBitmapBule = BitmapFactory.decodeResource( getResources() , R.drawable.twist_bar_blue );
		switchOn = BitmapFactory.decodeResource( getResources() , R.drawable.switch_on_nor );
		switchOff = BitmapFactory.decodeResource( getResources() , R.drawable.switch_off_nor );
		srcH = new Rect( 0 , 0 , 720 , 1280 );
		dstH = new RectF( 0 , 0 , 720f * widthScale , 1280f * widthScale );
		srcS = new Rect( 0 , 0 , 114 , 114 );
		dstS = new RectF( 302 * widthScale , 900f * widthScale , ( 302f + 114f ) * widthScale , ( 900f + 114f ) * widthScale );
		flHandler = new View2D( BitmapFactory.decodeResource( getResources() , R.drawable.flashlight_bg ) , 0 , 0 );
		lightSwitch = new View2D( switchOff , 0 , 0 );
		twistBars = new ArrayList<View2D>();
		srcBars = new ArrayList<Rect>();
		dstBars = new ArrayList<RectF>();
		for( int i = 0 ; i < barsCount ; i++ )
		{
			View2D bar = new View2D( barBitmapBlack , 0 + i * 24f , 0 );
			twistBars.add( bar );
		}
		for( int i = 0 ; i < barsCount ; i++ )
		{
			Rect src = new Rect( 0 , 0 , 24 , 76 );
			srcBars.add( src );
		}
		for( int i = 0 ; i < barsCount ; i++ )
		{
			RectF dst = new RectF( ( ( 108f ) + 24f * i ) * widthScale , 558f * widthScale , ( ( 108f ) + 24f * ( i + 1 ) ) * widthScale , ( 558f + 76f ) * widthScale );
			dstBars.add( dst );
		}
		twistBars.get( 2 ).setBitmap( barBitmapBule );
	}
	
	public FlashlightView1(
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
		for( int i = 0 ; i < twistBars.size() ; i++ )
		{
			canvas.drawBitmap( twistBars.get( i ).getBitmap() , srcBars.get( i ) , dstBars.get( i ) , paint );
		}
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
				downX = event.getX();
				if( event.getX() >= 302f * widthScale && event.getX() <= ( 302f + 114f ) * widthScale && event.getY() >= 900f * widthScale && event.getY() <= ( 900f + 114f ) * widthScale )
				{
					Log.v( "MotionEvent" , "onClick" );
					onClickListener.onClick();
				}
				break;
			case MotionEvent.ACTION_UP:
				//				if( event.getX() >= 108f * widthScale && event.getX() <= ( 108f + 504f ) * widthScale && event.getY() >= 558f * widthScale && event.getY() <= ( 558f + 76f ) * widthScale )
				//				{
				if( currTap == 0 )
				{
					if( dstBars.get( 2 ).right - 12f * widthScale < ( tap1Position + ( tap2Position - tap1Position ) / 2 ) * widthScale )
					{
						moveToTap( 0 );
						invalidate();
					}
					else if( dstBars.get( 2 ).right - 12f * widthScale >= ( tap1Position + ( tap2Position - tap1Position ) / 2 ) * widthScale && dstBars.get( 2 ).right - 12f * widthScale < ( tap2Position + ( tap3Position - tap2Position ) / 2 ) * widthScale )
					{
						moveToTap( 1 );
						invalidate();
					}
					else if( dstBars.get( 2 ).right - 12f * widthScale >= ( tap2Position + ( tap3Position - tap2Position ) / 2 ) * widthScale && dstBars.get( 2 ).right - 12f * widthScale < ( tap3Position + ( tap4Position - tap3Position ) / 2 ) * widthScale )
					{
						moveToTap( 2 );
						invalidate();
					}
					else if( dstBars.get( 2 ).right - 12f * widthScale >= ( tap3Position + ( tap4Position - tap3Position ) / 2 ) * widthScale )
					{
						moveToTap( 3 );
						invalidate();
					}
				}
				else if( currTap == 1 )
				{
					if( dstBars.get( 7 ).right - 12f * widthScale < ( tap1Position + ( tap2Position - tap1Position ) / 2 ) * widthScale )
					{
						moveToTap( 0 );
						invalidate();
					}
					else if( dstBars.get( 7 ).right - 12f * widthScale >= ( tap1Position + ( tap2Position - tap1Position ) / 2 ) * widthScale && dstBars.get( 7 ).right - 12f * widthScale < ( tap2Position + ( tap3Position - tap2Position ) / 2 ) * widthScale )
					{
						moveToTap( 1 );
						invalidate();
					}
					else if( dstBars.get( 7 ).right - 12f * widthScale >= ( tap2Position + ( tap3Position - tap2Position ) / 2 ) * widthScale && dstBars.get( 7 ).right - 12f * widthScale < ( tap3Position + ( tap4Position - tap3Position ) / 2 ) * widthScale )
					{
						moveToTap( 2 );
						invalidate();
					}
					else if( dstBars.get( 7 ).right - 12f * widthScale >= ( tap3Position + ( tap4Position - tap3Position ) / 2 ) * widthScale )
					{
						moveToTap( 3 );
						invalidate();
					}
				}
				else if( currTap == 2 )
				{
					if( dstBars.get( 12 ).right - 12f * widthScale < ( tap1Position + ( tap2Position - tap1Position ) / 2 ) * widthScale )
					{
						moveToTap( 0 );
						invalidate();
					}
					else if( dstBars.get( 12 ).right - 12f * widthScale >= ( tap1Position + ( tap2Position - tap1Position ) / 2 ) * widthScale && dstBars.get( 12 ).right - 12f * widthScale < ( tap2Position + ( tap3Position - tap2Position ) / 2 ) * widthScale )
					{
						moveToTap( 1 );
						invalidate();
					}
					else if( dstBars.get( 12 ).right - 12f * widthScale >= ( tap2Position + ( tap3Position - tap2Position ) / 2 ) * widthScale && dstBars.get( 12 ).right - 12f * widthScale < ( tap3Position + ( tap4Position - tap3Position ) / 2 ) * widthScale )
					{
						moveToTap( 2 );
						invalidate();
					}
					else if( dstBars.get( 12 ).right - 12f * widthScale >= ( tap3Position + ( tap4Position - tap3Position ) / 2 ) * widthScale )
					{
						moveToTap( 3 );
						invalidate();
					}
				}
				else if( currTap == 3 )
				{
					if( dstBars.get( 18 ).right - 12f * widthScale < ( tap1Position + ( tap2Position - tap1Position ) / 2 ) * widthScale )
					{
						moveToTap( 0 );
						invalidate();
					}
					else if( dstBars.get( 18 ).right - 12f * widthScale >= ( tap1Position + ( tap2Position - tap1Position ) / 2 ) * widthScale && dstBars.get( 18 ).right - 12f * widthScale < ( tap2Position + ( tap3Position - tap2Position ) / 2 ) * widthScale )
					{
						moveToTap( 1 );
						invalidate();
					}
					else if( dstBars.get( 18 ).right - 12f * widthScale >= ( tap2Position + ( tap3Position - tap2Position ) / 2 ) * widthScale && dstBars.get( 18 ).right - 12f * widthScale < ( tap3Position + ( tap4Position - tap3Position ) / 2 ) * widthScale )
					{
						moveToTap( 2 );
						invalidate();
					}
					else if( dstBars.get( 18 ).right - 12f * widthScale >= ( tap3Position + ( tap4Position - tap3Position ) / 2 ) * widthScale )
					{
						moveToTap( 3 );
						invalidate();
					}
					//					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				moveX = event.getX() - downX;
				if( event.getX() >= 108f * widthScale && event.getX() <= ( 108f + 504f ) * widthScale && event.getY() >= 558f * widthScale && event.getY() <= ( 558f + 76f ) * widthScale )
				{
					for( int i = 0 ; i < twistBars.size() ; i++ )
					{
						if( moveX > 0 )
						{
							dstBars.get( i ).left += speedX * widthScale;
							dstBars.get( i ).right += speedX * widthScale;
						}
						else
						{
							dstBars.get( i ).left += -speedX * widthScale;
							dstBars.get( i ).right += -speedX * widthScale;
						}
						if( dstBars.get( i ).left < ( 108f - 24f / 2f ) * widthScale )
						{
							dstBars.get( i ).right = ( 108f + 504f + 24f / 2f ) * widthScale;
							dstBars.get( i ).left = ( 108f + 504f - 24f / 2f ) * widthScale;
						}
						else if( dstBars.get( i ).right > ( 108f + 504f + 24f / 2f ) * widthScale )
						{
							dstBars.get( i ).left = ( 108f - 24f / 2f ) * widthScale;
							dstBars.get( i ).right = ( 108f + 24f / 2f ) * widthScale;
						}
					}
					invalidate();
					Log.v( "MotionEvent" , "ACTION_MOVE:" + moveX );
				}
				break;
			default:
				break;
		}
		return true;
	}
	
	public void moveToTap(
			int index )
	{
		for( int i = 0 ; i < dstBars.size() ; i++ )
		{
			dstBars.get( i ).left = ( ( 108f ) + 24f * i ) * widthScale;
			dstBars.get( i ).top = 558f * widthScale;
			dstBars.get( i ).right = ( ( 108f ) + 24f * ( i + 1 ) ) * widthScale;
			dstBars.get( i ).bottom = ( 558f + 76f ) * widthScale;
		}
		switch( index )
		{
			case 0:
				twistBars.get( 2 ).setBitmap( barBitmapBule );
				twistBars.get( 7 ).setBitmap( barBitmapBlack );
				twistBars.get( 12 ).setBitmap( barBitmapBlack );
				twistBars.get( 18 ).setBitmap( barBitmapBlack );
				break;
			case 1:
				twistBars.get( 2 ).setBitmap( barBitmapBlack );
				twistBars.get( 7 ).setBitmap( barBitmapBule );
				twistBars.get( 12 ).setBitmap( barBitmapBlack );
				twistBars.get( 18 ).setBitmap( barBitmapBlack );
				break;
			case 2:
				twistBars.get( 2 ).setBitmap( barBitmapBlack );
				twistBars.get( 7 ).setBitmap( barBitmapBlack );
				twistBars.get( 12 ).setBitmap( barBitmapBule );
				twistBars.get( 18 ).setBitmap( barBitmapBlack );
				break;
			case 3:
				twistBars.get( 2 ).setBitmap( barBitmapBlack );
				twistBars.get( 7 ).setBitmap( barBitmapBlack );
				twistBars.get( 12 ).setBitmap( barBitmapBlack );
				twistBars.get( 18 ).setBitmap( barBitmapBule );
				break;
			default:
				break;
		}
		currTap = index;
		onItemSelectedChanged.onChanged( index );
	}
	
	public void setOnClickListener(
			OnClickListener onClickListener )
	{
		this.onClickListener = onClickListener;
	}
	
	public void setOnItemSelectedChanged(
			OnItemSelectedChanged onItemSelectedChanged )
	{
		this.onItemSelectedChanged = onItemSelectedChanged;
	}
	
	public interface OnClickListener
	{
		
		public void onClick();
	}
	
	public interface OnItemSelectedChanged
	{
		
		public void onChanged(
				int itemId );
	}
}
