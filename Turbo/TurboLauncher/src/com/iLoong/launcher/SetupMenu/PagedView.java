package com.iLoong.launcher.SetupMenu;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.Utils3D;


public abstract class PagedView extends LinearLayout
{
	
	private static final int SNAP_VELOCITY = 550;
	private static final int INVALID_SCREEN = Integer.MAX_VALUE;
	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	private static final int MOVE_LEFT = 0;
	private static final int MOVE_RIGHT = 1;
	private static final float mLayoutScale = 1.0f;
	private int mTouchState = TOUCH_STATE_REST;
	private float mLastMotionX;
	private float mLastDownX;
	private int mDirection;
	private int mTouchSlop;
	private int mMaximumVelocity;
	public static int mCurrentScreen = 0;
	private int mNextScreen = INVALID_SCREEN;
	private int mFristMove = -1;
	private int[] mTempVisiblePagesRange = new int[2];
	private float mScrollingSpeed = 0.5F;
	private float mMoveThreshold = 0.3f;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private ViewSwitchListener mSwitchListener;
	private boolean mLoop = true;
	private boolean mAllowOverScroll = true;
	//add for net version 2014-1-15,Jone start
	public static boolean isMenuAlreadyExist = false;
	static int temDeltaX = 0;
	static int currOffset = 0;
	static int currDeltaX = 0;
	public static float indicatorStep = 0;
	public static boolean isInited = false;//setup_menu_support_scroll_page
	public static boolean isActionCancel = false;
	private static float mDeltaX = 0;
	private static boolean pageInProgress = false;
	public static int PageCount = 0;
	
	//Jone end
	public static interface ViewSwitchListener
	{
		
		void onSwitched(
				View view ,
				int position );
	}
	
	public PagedView(
			Context context )
	{
		this( context , null );
	}
	
	public PagedView(
			Context context ,
			AttributeSet attrs )
	{
		super( context , attrs );
		setHapticFeedbackEnabled( false );
		init();
	}
	
	public void setLoop(
			boolean loop )
	{
		mLoop = loop;
	}
	
	public void setOverScroll(
			boolean overscroll )
	{
		mAllowOverScroll = overscroll;
	}
	
	public void setScrollingSpeed(
			float speed )
	{
		mScrollingSpeed = speed;
	}
	
	public void setSwitchListener(
			ViewSwitchListener vsl )
	{
		mSwitchListener = vsl;
	}
	
	protected void init()
	{
		setOrientation( LinearLayout.HORIZONTAL );
		mScroller = new Scroller( getContext() );
		final ViewConfiguration configuration = ViewConfiguration.get( getContext() );
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}
	
	int getPageCount()
	{
		return getChildCount();
	}
	
	View getPageAt(
			int index )
	{
		return getChildAt( index );
	}
	
	public int getViewsCount()
	{
		return getChildCount();
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
		final int screenWidth = getWidth();
		final int whichScreen = ( getScrollX() + ( screenWidth / 2 ) ) / screenWidth;
		isActionCancel = true;
		snapToScreen( whichScreen );
		isActionCancel = false;
	}
	
	public void InitToScreen(
			int screen ,
			int pagewidth )
	{
		mCurrentScreen = screen;
		super.scrollTo( mCurrentScreen * pagewidth , 0 );
	}
	
	public void snapToScreen(
			int whichScreen )
	{
		int originalScreen = whichScreen;
		whichScreen = Math.max( ( mLoop ? -1 : 0 ) , Math.min( whichScreen , getChildCount() - ( mLoop ? 0 : 1 ) ) );
		mNextScreen = whichScreen;
		final int newX = whichScreen * getWidth();
		final int delta = newX - getScrollX();
		if( DefaultLayout.disable_theme_preview_tween || ( !PagedView.isInited && DefaultLayout.setup_menu_support_scroll_page ) )
			mScroller.startScroll( getScrollX() , 0 , delta , 0 , 0 );
		else
			mScroller.startScroll( getScrollX() , 0 , delta , 0 , (int)( Math.abs( delta ) * mScrollingSpeed ) );
		if( mNextScreen != INVALID_SCREEN )
		{
			if( mNextScreen == -1 && mLoop )
			{
				mCurrentScreen = getChildCount() - 1;
			}
			else if( mNextScreen == getChildCount() && mLoop )
			{
				mCurrentScreen = 0;
			}
			else
			{
				mCurrentScreen = Math.max( 0 , Math.min( mNextScreen , getChildCount() - 1 ) );
			}
			if( mSwitchListener != null )
				mSwitchListener.onSwitched( getChildAt( mCurrentScreen ) , mCurrentScreen );
		}
		//2014-1-15 ,Jone start
		if( RR.net_version )
			setTabTitleColor( whichScreen );
		//2014-1-15 ,Jone end
		invalidate();
	}
	
	//2014-1-15 ,Jone start
	public void setTabTitleColor(
			int index )
	{
		if( SetMenuDesktop.tabTitles != null )
		{
			for( int i = 0 ; i < SetMenuDesktop.tabTitles.size() ; i++ )
			{
				if( index == i )
				{
					SetMenuDesktop.tabTitles.get( index ).setTextColor( Color.rgb( 0x92 , 0x92 , 0x92 ) );
				}
				else
				{
					SetMenuDesktop.tabTitles.get( i ).setTextColor( Color.rgb( 0xe0 , 0xe0 , 0xe0 ) );
				}
			}
		}
	}
	
	//2014-1-15 ,Jone end
	public void OnClicksnapToScreen(
			int whichScreen )
	{
		if( whichScreen == mCurrentScreen )
			return;
		if( !mLoop || !mAllowOverScroll )
		{
			snapToScreen( whichScreen );
			return;
		}
		int statX = 0;
		int newX = 0;
		int delta = 0;
		int pageCount = getChildCount();
		if( mCurrentScreen == 0 && whichScreen == pageCount - 1 )
		{
			statX = 0;
			newX = -getWidth();
			mNextScreen = -1;
			mCurrentScreen = getChildCount() - 1;
		}
		else if( mCurrentScreen == pageCount - 1 && whichScreen == 0 )
		{
			statX = ( pageCount - 1 ) * getWidth();
			newX = pageCount * getWidth();
			mNextScreen = getChildCount();
			mCurrentScreen = 0;
		}
		else
		{
			statX = mCurrentScreen * getWidth();
			newX = whichScreen * getWidth();
			mCurrentScreen = whichScreen;
		}
		delta = newX - statX;
		mScroller.startScroll( statX , 0 , delta , 0 , (int)( Math.abs( delta ) * mScrollingSpeed ) );
		if( mSwitchListener != null )
			mSwitchListener.onSwitched( getChildAt( mCurrentScreen ) , mCurrentScreen );
		invalidate();
	}
	
	public void computeScroll()
	{
		if( mScroller.computeScrollOffset() )
		{
			scrollTo( mScroller.getCurrX() , mScroller.getCurrY() );
			//Jone start
			if( RR.net_version )
			{
				setIndicatorMargin( ( mScroller.getCurrX() - temDeltaX ) / PageCount - ( currDeltaX - temDeltaX ) / PageCount );
				currDeltaX = mScroller.getCurrX();
			}
			//Jone end
			postInvalidate();
		}
		else if( mNextScreen != INVALID_SCREEN )
		{
			if( mNextScreen == -1 && mLoop )
			{
				mCurrentScreen = getChildCount() - 1;
				scrollTo( mCurrentScreen * getWidth() , getScrollY() );
			}
			else if( mNextScreen == getChildCount() && mLoop )
			{
				mCurrentScreen = 0;
				scrollTo( 0 , getScrollY() );
			}
			else
			{
				//Jone start
				if( RR.net_version && !isMenuAlreadyExist )
				{
					isMenuAlreadyExist = true;
					mCurrentScreen = Math.max( 0 , Math.min( mNextScreen , getChildCount() - 1 ) );
				}
				//Jone end
			}
			mNextScreen = INVALID_SCREEN;
		}
	}
	
	public void scrollTo(
			int x ,
			int y )
	{
		if( mAllowOverScroll )
		{
			super.scrollTo( x , y );
			return;
		}
		final int pageWidth = getScaledMeasuredWidth( getPageAt( 0 ) );
		final int mMaxScrollX = ( getChildCount() - 1 ) * pageWidth;
		if( x < 0 )
		{
			super.scrollTo( 0 , y );
		}
		else if( x > mMaxScrollX )
		{
			super.scrollTo( mMaxScrollX , y );
		}
		else
		{
			super.scrollTo( x , y );
		}
	}
	
	public boolean onInterceptTouchEvent(
			MotionEvent ev )
	{
		if( getChildCount() == 0 )
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
				mLastDownX = mLastMotionX = x;
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
		if( getChildCount() == 0 )
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
				if( RR.net_version )
				{
					setAlphaAnimation( false );
					pageInProgress = true;
				}
				if( !mScroller.isFinished() )
				{
					mScroller.abortAnimation();
				}
				mLastDownX = mLastMotionX = x;
				mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
				break;
			case MotionEvent.ACTION_MOVE:
			{
				final int xDiff = (int)Math.abs( x - mLastMotionX );
				boolean xMoved = xDiff > mTouchSlop;
				if( RR.net_version )
				{
					setAlphaAnimation( false );
					pageInProgress = true;
				}
				if( xMoved )
				{
					mTouchState = TOUCH_STATE_SCROLLING;
					mFristMove++;
				}
				if( mLastMotionX > mLastDownX )
					mDirection = MOVE_RIGHT;
				else
					mDirection = MOVE_LEFT;
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
					//Jone start
					if( !DefaultLayout.disable_theme_preview_tween || DefaultLayout.setup_menu_support_scroll_page )
					{
						// currOffset+=(deltaX)/PageCount;
						// setIndicatorMargin((deltaX)/PageCount);
						scrollBy( deltaX , 0 );
					}
					//Jone end
				}
				break;
			}
			case MotionEvent.ACTION_UP:
			{
				//Jone start
				if( RR.net_version )
					pageInProgress = false;
				//Jone end
				if( mTouchState == TOUCH_STATE_SCROLLING )
				{
					final VelocityTracker velocityTracker = mVelocityTracker;
					velocityTracker.computeCurrentVelocity( 1000 , mMaximumVelocity );
					int velocityX = (int)velocityTracker.getXVelocity();
					int whichScreen = 0;
					final int screenWidth = getWidth();
					// final int whichScreen = (getScrollX() + (screenWidth / 2)) /
					// screenWidth;
					// final int whichScreen = (int) Math.floor((getScrollX() +
					// (screenWidth * mMoveThreshold)) / screenWidth);
					final float movex = Math.abs( mLastMotionX - mLastDownX );
					if( mDirection == MOVE_LEFT && movex > screenWidth * mMoveThreshold )
					{
						whichScreen = Math.max( ( mLoop ? -1 : 0 ) , Math.min( mCurrentScreen + 1 , getChildCount() - ( mLoop ? 0 : 1 ) ) );
					}
					else if( mDirection == MOVE_RIGHT && movex > screenWidth * mMoveThreshold )
					{
						whichScreen = Math.max( ( mLoop ? -1 : 0 ) , Math.min( mCurrentScreen - 1 , getChildCount() - ( mLoop ? 0 : 1 ) ) );
					}
					else
					{
						whichScreen = mCurrentScreen;
					}
					final float scrolledPos = (float)getScrollX() / screenWidth;
					if( velocityX > SNAP_VELOCITY && mCurrentScreen > ( mLoop ? -1 : 0 ) )
					{
						final int bound = scrolledPos < whichScreen ? mCurrentScreen - 1 : mCurrentScreen;
						snapToScreen( Math.min( whichScreen , bound ) );
					}
					else if( velocityX < -SNAP_VELOCITY && mCurrentScreen < getChildCount() - ( mLoop ? 0 : 1 ) )
					{
						final int bound = scrolledPos > whichScreen ? mCurrentScreen + 1 : mCurrentScreen;
						snapToScreen( Math.max( whichScreen , bound ) );
					}
					else
					{
						snapToScreen( whichScreen );
					}
					if( mVelocityTracker != null )
					{
						mVelocityTracker.recycle();
						mVelocityTracker = null;
					}
				}
				mFristMove = -1;
				mTouchState = TOUCH_STATE_REST;
				if( RR.net_version )
				{
					setAlphaAnimation( true );
				}
				break;
			}
			case MotionEvent.ACTION_CANCEL:
				snapToDestination();
				mTouchState = TOUCH_STATE_REST;
		}
		return true;
	}
	
	//Jone add start
	public static void setAlphaAnimation(
			boolean visible )
	{
		//		if( pageInProgress )
		//		{
		//			return;
		//		}
		//		for( int i = 0 ; i < SetMenuDesktop.mbgwin.getChildCount() ; i++ )
		//		{
		//			View view = SetMenuDesktop.mbgwin.getChildAt( i );
		//			if( view instanceof ImageView )
		//			{
		//				if( visible )
		//				{
		//					if( view.getTag() != null && (Integer)( view.getTag() ) == 1000 )
		//					{
		//						Animation animation = new AlphaAnimation( 1f , 0.0f );
		//						animation.setDuration( 250 );
		//						animation.setStartOffset( 1000 );
		//						animation.setInterpolator( new LinearInterpolator() );
		//						animation.setFillAfter( true );
		//						( (ImageView)view ).startAnimation( animation );
		//						break;
		//					}
		//					else if( view.getTag() != null && (Integer)( view.getTag() ) == 1001 )
		//					{
		//						Animation animation = new AlphaAnimation( 1f , 0.0f );
		//						animation.setDuration( 250 );
		//						animation.setStartOffset( 1000 );
		//						animation.setFillAfter( true );
		//						animation.setInterpolator( new LinearInterpolator() );
		//						( (ImageView)view ).startAnimation( animation );
		//					}
		//				}
		//				else
		//				{
		//					if( view.getTag() != null && (Integer)( view.getTag() ) == 1000 )
		//					{
		//						Animation animation = new AlphaAnimation( 0f , 1 );
		//						animation.setDuration( 0 );
		//						animation.setStartOffset( 0 );
		//						animation.setFillAfter( true );
		//						( (ImageView)view ).startAnimation( animation );
		//						break;
		//					}
		//					else if( view.getTag() != null && (Integer)( view.getTag() ) == 1001 )
		//					{
		//						Animation animation = new AlphaAnimation( 0f , 1.0f );
		//						animation.setDuration( 0 );
		//						animation.setStartOffset( 0 );
		//						animation.setFillAfter( true );
		//						animation.setInterpolator( new LinearInterpolator() );
		//						( (ImageView)view ).startAnimation( animation );
		//					}
		//				}
		//			}
		//			SetMenuDesktop.mbgwin.invalidate();
		//		}
	}
	
	public static void setIndicatorMargin(
			int deltaX )
	{
//		int[] location = new int[2];
//		SetMenuDesktop.indicatorView.getLocationOnScreen( location );
//		if( deltaX == Integer.MAX_VALUE )
//		{
//			SetMenuDesktop.indicatorView.offsetLeftAndRight( (int)0 - location[0] );
//			return;
//		}
//		if( SetMenuDesktop.indicatorView != null )
//		{
//			if( deltaX + location[0] > Utils3D.getScreenWidth() - indicatorStep )
//			{
//				deltaX = (int)( Utils3D.getScreenWidth() - indicatorStep - location[0] );
//			}
//			else if( deltaX + location[0] < 0 )
//			{
//				deltaX = 0 - location[0];
//			}
//			SetMenuDesktop.indicatorView.offsetLeftAndRight( (int)deltaX );
//		}
	}
	
	//Jone add end
	protected int getScaledRelativeChildOffset(
			int index )
	{
		final int offset = ( getMeasuredWidth() - getScaledMeasuredWidth( getPageAt( index ) ) ) / 2;
		return offset;
	}
	
	protected int getScaledMeasuredWidth(
			View child )
	{
		final int measuredWidth = child.getMeasuredWidth();
		final int minWidth = 0;
		final int maxWidth = ( minWidth > measuredWidth ) ? minWidth : measuredWidth;
		return (int)( maxWidth * mLayoutScale + 0.5f );
	}
	
	protected void getVisiblePages(
			int[] range )
	{
		final int pageCount = getChildCount();
		if( pageCount > 0 )
		{
			final int pageWidth = getScaledMeasuredWidth( getPageAt( 0 ) );
			final int screenWidth = getMeasuredWidth();
			int x = getScaledRelativeChildOffset( 0 ) + pageWidth;
			int leftScreen = 0;
			int rightScreen = 0;
			if( mLoop && getScrollX() > screenWidth * ( pageCount - 1 ) || getScrollX() < 0 )
			{
				rightScreen = 0;
				leftScreen = pageCount - 1;
			}
			else
			{
				while( x <= getScrollX() && leftScreen < pageCount - 1 )
				{
					leftScreen++;
					x += getScaledMeasuredWidth( getPageAt( leftScreen ) );
				}
				rightScreen = leftScreen;
				while( x < getScrollX() + screenWidth && rightScreen < pageCount - 1 )
				{
					rightScreen++;
					x += getScaledMeasuredWidth( getPageAt( rightScreen ) );
				}
			}
			range[0] = leftScreen;
			range[1] = rightScreen;
		}
		else
		{
			range[0] = -1;
			range[1] = -1;
		}
	}
	
	public void dispatchDraw(
			Canvas canvas )
	{
		final int pageCount = getChildCount();
		getVisiblePages( mTempVisiblePagesRange );
		final int screenWidth = getMeasuredWidth();
		final int leftScreen = mTempVisiblePagesRange[0];
		final int rightScreen = mTempVisiblePagesRange[1];
		if( leftScreen == -1 || rightScreen == -1 )
			return;
		canvas.save();
		canvas.clipRect( getScrollX() , getScrollY() , getScrollX() + this.getRight() - this.getLeft() , getScrollY() + this.getBottom() - this.getTop() );
		final long drawingTime = getDrawingTime();
		if( mLoop )
		{
			if( getScrollX() > screenWidth * ( pageCount - 1 ) )
			{
				if( leftScreen == pageCount - 1 && rightScreen == 0 )
				{
					int offset = pageCount * getWidth();
					canvas.translate( +offset , 0 );
					drawChild( canvas , getPageAt( rightScreen ) , drawingTime );
					canvas.translate( -offset , 0 );
				}
				drawChild( canvas , getPageAt( leftScreen ) , drawingTime );
			}
			else if( getScrollX() < 0 )
			{
				if( leftScreen == pageCount - 1 && rightScreen == 0 )
				{
					int offset = pageCount * getWidth();
					canvas.translate( -offset , 0 );
					drawChild( canvas , getPageAt( leftScreen ) , drawingTime );
					canvas.translate( +offset , 0 );
				}
				else
				{
					drawChild( canvas , getPageAt( leftScreen ) , drawingTime );
				}
				drawChild( canvas , getPageAt( rightScreen ) , drawingTime );
			}
			else
			{
				drawChild( canvas , getPageAt( rightScreen ) , drawingTime );
				drawChild( canvas , getPageAt( leftScreen ) , drawingTime );
			}
		}
		else
		{
			drawChild( canvas , getPageAt( rightScreen ) , drawingTime );
			drawChild( canvas , getPageAt( leftScreen ) , drawingTime );
		}
		canvas.restore();
	}
}
