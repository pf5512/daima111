package com.iLoong.launcher.SetupMenu;


import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


public class PageScroller
{
	
	private static final int SNAP_VELOCITY = 550;
	private static final int INVALID_SCREEN = -1;
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private int mTouchState = TOUCH_STATE_REST;
	private float mLastMotionX;
	private int mTouchSlop;
	private int mMaximumVelocity;
	private int mCurrentScreen;
	private int mNextScreen = INVALID_SCREEN;
	private int mFristMove = -1;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private ViewGroup scrollView;
	private ViewSwitchListener mSwitchListener;
	
	public static interface ViewSwitchListener
	{
		
		void onSwitched(
				View view ,
				int position );
	}
	
	public PageScroller(
			ViewGroup v ,
			ViewSwitchListener vsl )
	{
		scrollView = v;
		mSwitchListener = vsl;
		mScroller = new Scroller( scrollView.getContext() );
		final ViewConfiguration configuration = ViewConfiguration.get( scrollView.getContext() );
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}
	
	public int getViewsCount()
	{
		return scrollView.getChildCount();
	}
	
	public int getCurrentScreen()
	{
		return mCurrentScreen;
	}
	
	public boolean isFinished()
	{
		return this.mScroller.isFinished();
	}
	
	public void snapToDestination()
	{
		final int screenWidth = scrollView.getWidth();
		final int whichScreen = ( scrollView.getScrollX() + ( screenWidth / 2 ) ) / screenWidth;
		snapToScreen( whichScreen );
	}
	
	public void snapToScreen(
			int whichScreen )
	{
		whichScreen = Math.max( 0 , Math.min( whichScreen , scrollView.getChildCount() - 1 ) );
		mNextScreen = whichScreen;
		final int newX = whichScreen * scrollView.getWidth();
		final int delta = newX - scrollView.getScrollX();
		mScroller.startScroll( scrollView.getScrollX() , 0 , delta , 0 , Math.abs( delta ) * 2 );
		scrollView.invalidate();
		if( mSwitchListener != null )
			mSwitchListener.onSwitched( scrollView.getChildAt( whichScreen ) , whichScreen );
	}
	
	public void computeScroll()
	{
		if( mScroller.computeScrollOffset() )
		{
			scrollView.scrollTo( mScroller.getCurrX() , mScroller.getCurrY() );
			scrollView.postInvalidate();
		}
		else if( mNextScreen != INVALID_SCREEN )
		{
			mCurrentScreen = Math.max( 0 , Math.min( mNextScreen , scrollView.getChildCount() - 1 ) );
			mNextScreen = INVALID_SCREEN;
		}
	}
	
	public boolean onInterceptTouchEvent(
			MotionEvent ev )
	{
		if( scrollView.getChildCount() == 0 )
			return false;
		if( mVelocityTracker == null )
		{
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement( ev );
		final int action = ev.getAction();
		if( ( action == MotionEvent.ACTION_MOVE ) && ( mTouchState != TOUCH_STATE_REST ) )
		{
			return true;
		}
		final float x = ev.getX();
		switch( action )
		{
			case MotionEvent.ACTION_DOWN:
				if( !mScroller.isFinished() )
				{
					mScroller.abortAnimation();
				}
				mLastMotionX = x;
				mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
				break;
			case MotionEvent.ACTION_MOVE:
				final int xDiff = (int)Math.abs( x - mLastMotionX );
				boolean xMoved = xDiff > mTouchSlop;
				if( xMoved )
				{
					mTouchState = TOUCH_STATE_SCROLLING;
				}
				break;
			case MotionEvent.ACTION_UP:
				mTouchState = TOUCH_STATE_REST;
				break;
			case MotionEvent.ACTION_CANCEL:
				mTouchState = TOUCH_STATE_REST;
				break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}
	
	public boolean onTouchEvent(
			MotionEvent ev )
	{
		if( scrollView.getChildCount() == 0 )
			return false;
		if( mVelocityTracker == null )
		{
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement( ev );
		final int action = ev.getAction();
		final float x = ev.getX();
		switch( action )
		{
			case MotionEvent.ACTION_DOWN:
				if( !mScroller.isFinished() )
				{
					mScroller.abortAnimation();
				}
				mLastMotionX = x;
				mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
				break;
			case MotionEvent.ACTION_MOVE:
				final int xDiff = (int)Math.abs( x - mLastMotionX );
				boolean xMoved = xDiff > mTouchSlop;
				if( xMoved )
				{
					mTouchState = TOUCH_STATE_SCROLLING;
					mFristMove++;
				}
				if( mFristMove == 0 )
				{
					if( x > mLastMotionX )
						mLastMotionX += xDiff;
					else
						mLastMotionX -= xDiff;
					mFristMove++;
				}
				if( mTouchState == TOUCH_STATE_SCROLLING )
				{
					final int deltaX = (int)( mLastMotionX - x );
					mLastMotionX = x;
					scrollView.scrollBy( deltaX , 0 );
				}
				break;
			case MotionEvent.ACTION_UP:
				if( mTouchState == TOUCH_STATE_SCROLLING )
				{
					final VelocityTracker velocityTracker = mVelocityTracker;
					velocityTracker.computeCurrentVelocity( 1000 , mMaximumVelocity );
					int velocityX = (int)velocityTracker.getXVelocity();
					if( velocityX > SNAP_VELOCITY && mCurrentScreen > 0 )
					{
						snapToScreen( mCurrentScreen - 1 );
					}
					else if( velocityX < -SNAP_VELOCITY && mCurrentScreen < scrollView.getChildCount() - 1 )
					{
						snapToScreen( mCurrentScreen + 1 );
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
				}
				mFristMove = -1;
				mTouchState = TOUCH_STATE_REST;
				break;
			case MotionEvent.ACTION_CANCEL:
				snapToDestination();
				mTouchState = TOUCH_STATE_REST;
		}
		return true;
	}
}
