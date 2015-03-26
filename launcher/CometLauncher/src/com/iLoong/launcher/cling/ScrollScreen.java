/* Copyright 2011 cooee
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.iLoong.launcher.cling;


import com.iLoong.RR;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.iLoong.launcher.Desktop3D.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


public class ScrollScreen extends ViewGroup
{
	
	private ScreenIndicator indicator;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;
	private int mMaximumVelocity;
	private int mCurrentScreen;
	private boolean mFirstLayout = true;
	private float mLastMotionX;
	private int mTouchState = TOUCH_STATE_REST;
	private OnScreenChangeListener mOnScreenChangeListener;
	private static final int DEFAULT_SCREEN = 0;
	private static final int SNAP_VELOCITY = 400;
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	
	public ScrollScreen(
			Context context ,
			AttributeSet attrs )
	{
		super( context , attrs );
		initScrollScreen();
	}
	
	private void initScrollScreen()
	{
		Context context = getContext();
		mScroller = new Scroller( context );
		final ViewConfiguration configuration = ViewConfiguration.get( getContext() );
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		mCurrentScreen = DEFAULT_SCREEN;
	}
	
	@Override
	protected void onMeasure(
			int widthMeasureSpec ,
			int heightMeasureSpec )
	{
		super.onMeasure( widthMeasureSpec , heightMeasureSpec );
		final int width = MeasureSpec.getSize( widthMeasureSpec );
		final int widthMode = MeasureSpec.getMode( widthMeasureSpec );
		if( widthMode != MeasureSpec.EXACTLY )
		{
			throw new IllegalStateException( "Workspace can only be used in EXACTLY mode." );
		}
		final int heightMode = MeasureSpec.getMode( heightMeasureSpec );
		if( heightMode != MeasureSpec.EXACTLY )
		{
			throw new IllegalStateException( "Workspace can only be used in EXACTLY mode." );
		}
		final int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			getChildAt( i ).measure( widthMeasureSpec , heightMeasureSpec );
		}
		if( mFirstLayout )
		{
			setHorizontalScrollBarEnabled( false );
			scrollTo( mCurrentScreen * width , 0 );
			setHorizontalScrollBarEnabled( true );
			mFirstLayout = false;
			if( indicator != null )
			{
				indicator.setCurrentScreen( mCurrentScreen );
			}
		}
	}
	
	@Override
	protected void onLayout(
			boolean changed ,
			int left ,
			int top ,
			int right ,
			int bottom )
	{
		int childLeft = 0;
		final int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			final View child = getChildAt( i );
			if( child.getVisibility() != View.GONE )
			{
				final int childWidth = child.getMeasuredWidth();
				child.layout( childLeft , 0 , childLeft + childWidth , child.getMeasuredHeight() );
				childLeft += childWidth;
			}
		}
	}
	
	@Override
	public void computeScroll()
	{
		if( mScroller.computeScrollOffset() )
		{
			scrollTo( mScroller.getCurrX() , mScroller.getCurrY() );
			postInvalidate();
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(
			MotionEvent ev )
	{
		final int action = ev.getAction();
		if( ( action == MotionEvent.ACTION_MOVE ) && ( mTouchState != TOUCH_STATE_REST ) )
		{
			return true;
		}
		if( mVelocityTracker == null )
		{
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement( ev );
		switch( action )
		{
			case MotionEvent.ACTION_MOVE:
			{
				final float x = ev.getX();
				final int xDiff = (int)Math.abs( x - mLastMotionX );
				final int touchSlop = mTouchSlop;
				boolean xMoved = xDiff > touchSlop;
				if( xMoved )
				{
					mTouchState = TOUCH_STATE_SCROLLING;
					mLastMotionX = x;
				}
				break;
			}
			case MotionEvent.ACTION_DOWN:
			{
				final float x = ev.getX();
				mLastMotionX = x;
				mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
				break;
			}
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				mTouchState = TOUCH_STATE_REST;
				if( mVelocityTracker != null )
				{
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}
	
	@Override
	public boolean onTouchEvent(
			MotionEvent ev )
	{
		if( mVelocityTracker == null )
		{
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement( ev );
		final int action = ev.getAction();
		switch( action & MotionEvent.ACTION_MASK )
		{
			case MotionEvent.ACTION_DOWN:
				if( !mScroller.isFinished() )
				{
					mScroller.abortAnimation();
				}
				mLastMotionX = ev.getX();
				break;
			case MotionEvent.ACTION_MOVE:
				final float x = ev.getX();
				final float deltaX = mLastMotionX - x;
				mLastMotionX = x;
				//Log.d("launcher", "deltaX,scrollX="+deltaX+" "+mScroller.getCurrX());
				if( ( -deltaX - mScroller.getCurrX() ) <= 0 && ( -deltaX - mScroller.getCurrX() ) >= -( getChildCount() - 1 ) * getWidth() )
				{
					scrollBy( (int)deltaX , 0 );
				}
				break;
			case MotionEvent.ACTION_UP:
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity( 1000 , mMaximumVelocity );
				final int velocityX = (int)velocityTracker.getXVelocity();
				if( velocityX > SNAP_VELOCITY && mCurrentScreen > 0 )
				{
					snapToScreen( mCurrentScreen - 1 );
				}
				else if( velocityX < -SNAP_VELOCITY )
				{
					if( mCurrentScreen < getChildCount() - 1 )
						snapToScreen( mCurrentScreen + 1 );
					else if( mOnScreenChangeListener != null )
					{
						mOnScreenChangeListener.onScreenChanged( getCurrentScreen() + 1 );
					}
				}
				else
				{
					snapToDestination();
				}
				if( mVelocityTracker != null )
				{
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				mTouchState = TOUCH_STATE_REST;
				break;
			case MotionEvent.ACTION_CANCEL:
				mTouchState = TOUCH_STATE_REST;
				break;
		}
		return true;
	}
	
	public void snapToDestination()
	{
		final int screenWidth = getWidth();
		final int destScreen = ( getScrollX() + screenWidth / 2 ) / screenWidth;
		snapToScreen( destScreen );
	}
	
	private void snapToScreen(
			int whichScreen )
	{
		whichScreen = Math.max( 0 , Math.min( whichScreen , getChildCount() - 1 ) );
		if( getScrollX() != ( whichScreen * getWidth() ) )
		{
			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll( getScrollX() , 0 , delta , 0 , Math.abs( delta ) * 2 );
			mCurrentScreen = whichScreen;
			invalidate();
			if( indicator != null )
			{
				indicator.setCurrentScreen( mCurrentScreen );
			}
			invokeOnScreenChangeListener();
		}
	}
	
	public void addScreen(
			View view )
	{
		addView( view , new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT ) );
		if( indicator == null )
		{
			return;
		}
		indicator.addIndicator();
	}
	
	public void addScreen(
			int count ,
			ScreenContentFactory contentFactory )
	{
		for( int i = 0 ; i < count ; i++ )
		{
			addScreen( contentFactory.createScreenContent( i ) );
		}
	}
	
	public interface ScreenContentFactory
	{
		
		View createScreenContent(
				int index );
	}
	
	public void setScreenIndicator(
			ScreenIndicator indicator )
	{
		this.indicator = indicator;
	}
	
	public int getCurrentScreen()
	{
		return mCurrentScreen;
	}
	
	public void setToScreen(
			int whichScreen )
	{
		whichScreen = Math.max( 0 , Math.min( whichScreen , getChildCount() - 1 ) );
		mCurrentScreen = whichScreen;
		scrollTo( whichScreen * getWidth() , 0 );
		if( indicator != null )
		{
			indicator.setCurrentScreen( mCurrentScreen );
		}
	}
	
	public interface ScreenIndicator
	{
		
		void addIndicator();
		
		void setCurrentScreen(
				int index );
	}
	
	public void setOnScreenChangedListener(
			OnScreenChangeListener l )
	{
		mOnScreenChangeListener = l;
	}
	
	private void invokeOnScreenChangeListener()
	{
		if( mOnScreenChangeListener != null )
		{
			mOnScreenChangeListener.onScreenChanged( getCurrentScreen() );
		}
	}
	
	public interface OnScreenChangeListener
	{
		
		void onScreenChanged(
				int index );
	}
}
