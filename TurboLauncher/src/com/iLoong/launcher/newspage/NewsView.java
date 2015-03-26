package com.iLoong.launcher.newspage;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.CooeePlugin.CooeePluginHostView;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.Widget3D.NewsFlowReflect;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class NewsView extends ViewGroup
{
	
	public static final String SP_KEY_NEWSPAGE_DOWNLOAD_ID = "newspage_download_id";
	private static final int SCREEN_LEFT_HANDLE = 0;
	private static final int SCREEN_MAIN = 1;
	private static final int SCREEN_RIGHT_HANDLE = 2;
	private static final int SCREEN_INVALID = -1;
	private int mCurrentScreen;
	private int mNextScreen = SCREEN_INVALID;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;
	private float mLastMotionX;
	private float mLastMotionY;
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private final static int SNAP_VELOCITY = 1000;
	public int mTouchState = TOUCH_STATE_REST;
	private boolean mLocked;
	private boolean mAllowLongPress;
	private Context mContext;
	private ImageView mVirtualView;
	private CooeePluginHostView newsView;
	private ImageView newsLeftHandle;
	private ImageView newsRightHandle;
	private boolean isLeftIn = true;
	
	public NewsView(
			Context context )
	{
		this( context , null , 0 );
	}
	
	public NewsView(
			Context context ,
			AttributeSet attrs )
	{
		this( context , attrs , 0 );
	}
	
	public NewsView(
			Context context ,
			AttributeSet attrs ,
			int defStyle )
	{
		super( context , attrs , defStyle );
		mContext = context;
		mScroller = new Scroller( getContext() );
		mCurrentScreen = SCREEN_RIGHT_HANDLE;
		mTouchSlop = ViewConfiguration.get( getContext() ).getScaledTouchSlop();
		init();
	}
	
	private void init()
	{
		if( newsView == null )
		{
			if( isNewsInstalled() )
				newsView = NewsFlowReflect.getNewsFlow( mContext );
		}
		mVirtualView = new ImageView( mContext );
		newsLeftHandle = new ImageView( mContext );
		newsRightHandle = new ImageView( mContext );
		int mScreenWidth = UtilsBase.getScreenWidth();//屏幕宽  
		int mScreenHeight = UtilsBase.getScreenHeight();//屏幕高   
		Bitmap bmp = ( (BitmapDrawable)getResources().getDrawable( R.drawable.virtual_newspage ) ).getBitmap();
		Bitmap mBitmap = Bitmap.createScaledBitmap( bmp , mScreenWidth , mScreenHeight , true );
		//mVirtualView.setImageResource( R.drawable.virtual_newspage );
		mVirtualView.setImageBitmap( mBitmap );
		newsLeftHandle.setImageResource( R.drawable.news_handle_right );
		newsRightHandle.setImageResource( R.drawable.news_handle_left );
		addView( newsLeftHandle );
		if( newsView != null )
		{
			addView( newsView );
		}
		else
		{
			addView( mVirtualView );
		}
		addView( newsRightHandle );
		if( !DefaultLayout.show_newspage_with_handle )
		{
			newsLeftHandle.setVisibility( View.INVISIBLE );
			newsRightHandle.setVisibility( View.INVISIBLE );
		}
		onVirtualViewClick( mContext );
	}
	
	public void onNewsViewInstall()
	{
		removeAllViews();
		if( newsView != null )
		{
			newsView.removeAllViews();
			newsView = null;
		}
		else
		{
			if( isNewsInstalled() )
				newsView = NewsFlowReflect.getNewsFlow( mContext );
		}
		addView( newsLeftHandle );
		if( newsView != null )
		{
			addView( newsView );
		}
		else
		{
			addView( mVirtualView );
		}
		addView( newsRightHandle );
	}
	
	@Override
	protected void onMeasure(
			int widthMeasureSpec ,
			int heightMeasureSpec )
	{
		measureViews( widthMeasureSpec , heightMeasureSpec );
		super.onMeasure( widthMeasureSpec , heightMeasureSpec );
	}
	
	private void measureViews(
			int widthMeasureSpec ,
			int heightMeasureSpec )
	{
		View leftHandle = getChildAt( 0 );
		leftHandle.measure( (int)( NewsHandle.mWidth ) , (int)( NewsHandle.mHeight ) );
		View newsView = getChildAt( 1 );
		newsView.measure( UtilsBase.getScreenWidth() , UtilsBase.getScreenHeight() );
		View handleView = getChildAt( 2 );
		handleView.measure( (int)( NewsHandle.mWidth ) , (int)( NewsHandle.mHeight ) );
	}
	
	@Override
	protected void onLayout(
			boolean changed ,
			int l ,
			int t ,
			int r ,
			int b )
	{
		int childCount = getChildCount();
		if( childCount != 3 )
		{
			throw new IllegalStateException( "The childCount of NewsView must be 2" );
		}
		View leftHandle = getChildAt( 0 );
		leftHandle.layout(
				-getChildAt( 1 ).getMeasuredWidth() - leftHandle.getMeasuredWidth() ,
				(int)( ( UtilsBase.getScreenHeight() - leftHandle.getMeasuredHeight() ) / 2.0f - Utils3D.getStatusBarHeight() ) ,
				-getChildAt( 1 ).getMeasuredWidth() ,
				(int)( ( UtilsBase.getScreenHeight() - leftHandle.getMeasuredHeight() ) / 2.0f ) + leftHandle.getMeasuredHeight() - Utils3D.getStatusBarHeight() );
		View newsView = getChildAt( 1 );
		newsView.layout( -getChildAt( 1 ).getMeasuredWidth() , 0 , 0 , getChildAt( 1 ).getMeasuredHeight() );
		View rightHandle = getChildAt( 2 );
		rightHandle.layout(
				0 ,
				(int)( ( UtilsBase.getScreenHeight() - rightHandle.getMeasuredHeight() ) / 2.0f - Utils3D.getStatusBarHeight() ) ,
				rightHandle.getMeasuredWidth() ,
				rightHandle.getMeasuredHeight() + (int)( ( UtilsBase.getScreenHeight() - rightHandle.getMeasuredHeight() ) / 2.0f ) - Utils3D.getStatusBarHeight() );
	}
	
	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		View child;
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			child = getChildAt( i );
			child.setFocusable( true );
			child.setClickable( true );
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(
			MotionEvent ev )
	{
		if( mLocked )
		{
			return true;
		}
		final int action = ev.getAction();
		if( ( action == MotionEvent.ACTION_MOVE ) && ( mTouchState != TOUCH_STATE_REST ) )
		{
			return true;
		}
		final float x = ev.getX();
		final float y = ev.getY();
		switch( action )
		{
			case MotionEvent.ACTION_MOVE:
				final int xDiff = (int)Math.abs( x - mLastMotionX );
				final int yDiff = (int)Math.abs( y - mLastMotionY );
				final int touchSlop = mTouchSlop;
				boolean xMoved = xDiff > touchSlop;
				boolean yMoved = yDiff > touchSlop;
				if( xMoved || yMoved )
				{
					if( xMoved )
					{
						// Scroll if the user moved far enough along the X axis
						mTouchState = TOUCH_STATE_SCROLLING;
						enableChildrenCache();
					}
					// Either way, cancel any pending longpress
					if( mAllowLongPress )
					{
						mAllowLongPress = false;
						// Try canceling the long press. It could also have been
						// scheduled
						// by a distant descendant, so use the mAllowLongPress flag
						// to block
						// everything
						final View currentScreen = getChildAt( mCurrentScreen );
						currentScreen.cancelLongPress();
					}
				}
				break;
			case MotionEvent.ACTION_DOWN:
				// Remember location of down touch
				mLastMotionX = x;
				mLastMotionY = y;
				mAllowLongPress = true;
				mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				// Release the drag
				clearChildrenCache();
				mTouchState = TOUCH_STATE_REST;
				mAllowLongPress = false;
				break;
		}
		/*
		 * The only time we want to intercept motion events is if we are in the
		 * drag mode.
		 */
		return mTouchState != TOUCH_STATE_REST;
	}
	
	@Override
	public boolean onTouchEvent(
			MotionEvent event )
	{
		if( mLocked )
		{
			return true;
		}
		if( mVelocityTracker == null )
		{
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement( event );
		super.onTouchEvent( event );
		float x = event.getX();
		switch( event.getAction() )
		{
			case MotionEvent.ACTION_DOWN:
				if( mScroller != null )
				{
					if( !mScroller.isFinished() )
					{
						mScroller.abortAnimation();
					}
				}
				mLastMotionX = x;
				break;
			case MotionEvent.ACTION_MOVE:
				if( mTouchState == TOUCH_STATE_SCROLLING )
				{
					final int deltaX = (int)( mLastMotionX - x );
					mLastMotionX = x;
					if( isLeftIn )
					{
						if( deltaX < 0 )
						{
							if( deltaX + getScrollX() >= -getChildAt( 1 ).getWidth() )
							{
								scrollBy( deltaX , 0 );
							}
						}
						else if( deltaX > 0 )
						{
							final int availableToScroll = UtilsBase.getScreenWidth() - getScrollX() - getWidth();
							if( availableToScroll > 0 )
							{
								scrollBy( Math.min( availableToScroll , deltaX ) , 0 );
							}
						}
					}
					else
					{
						if( deltaX < 0 )
						{
							final int availableToScroll = UtilsBase.getScreenWidth() - getScrollX() - getWidth();
							if( availableToScroll > 0 )
							{
								scrollBy( Math.min( availableToScroll , deltaX ) , 0 );
							}
						}
						else if( deltaX > 0 )
						{
							if( deltaX + getScrollX() <= -getChildAt( 1 ).getWidth() )
							{
								scrollBy( deltaX , 0 );
							}
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if( mTouchState == TOUCH_STATE_SCROLLING )
				{
					final VelocityTracker velocityTracker = mVelocityTracker;
					velocityTracker.computeCurrentVelocity( SNAP_VELOCITY );
					int velocityX = (int)velocityTracker.getXVelocity();
					if( velocityX > SNAP_VELOCITY && !isLeftIn )
					{
						snapToScreen( SCREEN_LEFT_HANDLE );
					}
					else if( ( velocityX < -SNAP_VELOCITY || getScrollX() > -UtilsBase.getScreenWidth() / 2 ) && isLeftIn )
					{
						snapToScreen( SCREEN_RIGHT_HANDLE );
					}
					else
					{
						snapToScreen( SCREEN_MAIN );
					}
					if( mVelocityTracker != null )
					{
						mVelocityTracker.recycle();
						mVelocityTracker = null;
					}
				}
				mTouchState = TOUCH_STATE_REST;
				break;
			case MotionEvent.ACTION_CANCEL:
				mTouchState = TOUCH_STATE_REST;
				break;
		}
		return true;
	}
	
	void enableChildrenCache()
	{
		final int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			final View layout = (View)getChildAt( i );
			layout.setDrawingCacheEnabled( true );
		}
	}
	
	void clearChildrenCache()
	{
		final int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			final View layout = (View)getChildAt( i );
			layout.setDrawingCacheEnabled( false );
		}
	}
	
	protected void snapToDestination()
	{
		if( getScrollX() == 0 )
		{
			return;
		}
		final int screenWidth = getChildAt( 0 ).getWidth();
		final int whichScreen = ( screenWidth + getScrollX() + ( screenWidth / 2 ) ) / screenWidth;
		snapToScreen( whichScreen );
	}
	
	protected void snapToScreen(
			int whichScreen )
	{
		enableChildrenCache();
		whichScreen = Math.max( 0 , Math.min( whichScreen , getChildCount() - 1 ) );
		boolean changingScreens = whichScreen != mCurrentScreen;
		mNextScreen = whichScreen;
		View focusedChild = getFocusedChild();
		if( focusedChild != null && changingScreens && focusedChild == getChildAt( mCurrentScreen ) )
		{
			focusedChild.clearFocus();
		}
		int newX;
		int delta;
		if( mNextScreen == SCREEN_MAIN )
		{
			newX = -UtilsBase.getScreenWidth();
			delta = newX - getScrollX();
		}
		else if( mNextScreen == SCREEN_LEFT_HANDLE )
		{
			newX = -UtilsBase.getScreenWidth() * 2;
			delta = newX - getScrollX();
			iLoongLauncher.isShowNews = false;
		}
		else if( mNextScreen == SCREEN_RIGHT_HANDLE )
		{
			newX = 0;
			delta = newX - getScrollX();
			iLoongLauncher.isShowNews = false;
		}
		else
		{
			newX = -UtilsBase.getScreenWidth();
			delta = newX - getScrollX();
		}
		mScroller.startScroll( getScrollX() , 0 , delta , 0 , Math.abs( delta ) );
		invalidate();
	}
	
	public ImageView getNewsLeftHandle()
	{
		return newsLeftHandle;
	}
	
	public ImageView getNewsRightHandle()
	{
		return newsRightHandle;
	}
	
	@Override
	public void computeScroll()
	{
		if( mScroller.isFinished() )
		{
			mLocked = false;
			if( DefaultLayout.enable_news )
			{
				iLoongLauncher.getInstance().postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						if( Desktop3DListener.root != null )
						{
							if( Desktop3DListener.root.newsHandle != null && DefaultLayout.enable_news && DefaultLayout.show_newspage_with_handle && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode )
							{
								if( newsLeftHandle.getVisibility() == View.VISIBLE || Desktop3DListener.root.newsHandle.isDragging )
								{
									Desktop3DListener.root.newsHandle.show();
								}
								else
								{
									Desktop3DListener.root.newsHandle.hide();
								}
							}
						}
					}
				} );
			}
		}
		else
		{
			mLocked = true;
			if( DefaultLayout.enable_news && DefaultLayout.show_newspage_with_handle )
			{
				newsLeftHandle.setVisibility( View.VISIBLE );
				newsRightHandle.setVisibility( View.VISIBLE );
			}
		}
		if( mScroller.computeScrollOffset() )
		{
			scrollTo( mScroller.getCurrX() , mScroller.getCurrY() );
			postInvalidate();
		}
		else if( mNextScreen != SCREEN_INVALID )
		{
			mCurrentScreen = Math.max( 0 , Math.min( mNextScreen , getChildCount() - 1 ) );
			mNextScreen = SCREEN_INVALID;
			clearChildrenCache();
		}
	}
	
	public void startLeftShow()
	{
		if( mLocked )
		{
			return;
		}
		mScroller.startScroll( 0 , 0 , -UtilsBase.getScreenWidth() , 0 , 500 );
		invalidate();
		iLoongLauncher.isShowNews = true;
		isLeftIn = true;
		mCurrentScreen = SCREEN_MAIN;
	}
	
	public void startRightShow()
	{
		if( mLocked )
		{
			return;
		}
		mScroller.startScroll( -UtilsBase.getScreenWidth() * 2 , 0 , UtilsBase.getScreenWidth() , 0 , 500 );
		invalidate();
		iLoongLauncher.isShowNews = true;
		isLeftIn = false;
		mCurrentScreen = SCREEN_MAIN;
	}
	
	public void startLeftClose()
	{
		if( mLocked )
		{
			return;
		}
		mScroller.startScroll( getScrollX() , 0 , UtilsBase.getScreenWidth() , 0 , 500 );
		invalidate();
		iLoongLauncher.isShowNews = false;
	}
	
	public void startRightClose()
	{
		if( mLocked )
		{
			return;
		}
		mScroller.startScroll( getScrollX() , 0 , -UtilsBase.getScreenWidth() , 0 , 500 );
		invalidate();
		iLoongLauncher.isShowNews = false;
	}
	
	public void startClose()
	{
		if( mLocked || !iLoongLauncher.isShowNews )
		{
			return;
		}
		if( isLeftIn )
		{
			mScroller.startScroll( getScrollX() , 0 , UtilsBase.getScreenWidth() , 0 , 500 );
			iLoongLauncher.isShowNews = false;
		}
		else
		{
			mScroller.startScroll( getScrollX() , 0 , -UtilsBase.getScreenWidth() , 0 , 500 );
			iLoongLauncher.isShowNews = false;
		}
		invalidate();
	}
	
	public void setLeftHandlePos(
			int x ,
			int y )
	{
		newsLeftHandle.setX( x );
		newsLeftHandle.setY( y );
	}
	
	public void setRightHandlePos(
			int x ,
			int y )
	{
		newsRightHandle.setX( x );
		newsRightHandle.setY( y );
	}
	
	public boolean isLeftIn()
	{
		return isLeftIn;
	}
	
	public boolean isNewsInstalled()
	{
		try
		{
			iLoongLauncher.getInstance().createPackageContext( "com.inveno.newpiflow" , Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY );
		}
		catch( NameNotFoundException e )
		{
			return false;
		}
		return true;
	}
	
	private void onVirtualViewClick(
			final Context context )
	{
		if( mVirtualView != null )
		{
			mVirtualView.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(
						View v )
				{
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
					long downId = sp.getLong( SP_KEY_NEWSPAGE_DOWNLOAD_ID , -1 );
					DownloadApkManager manager = new DownloadApkManager( context );
					if( !manager.queryDownloadStatus( downId ) )
					{
						Intent intent = new Intent( context , DownloadDialog.class );
						intent.putExtra( DownloadDialog.KEY_EXTRA_ICON , R.drawable.news_icon );
						intent.putExtra( DownloadDialog.KEY_EXTRA_TITLE , R.string.newspage_download_dialog_title_text );
						intent.putExtra( DownloadDialog.KEY_EXTRA_MESSAGE , R.string.newspage_download_dialog_message_text );
						intent.putExtra( DownloadDialog.KEY_EXTRA_URL , "http://www.coolauncher.cn/download/apk/news.apk" );
						intent.putExtra( DownloadDialog.KEY_EXTRA_FILE_NAME , "news.apk" );
						intent.putExtra( DownloadDialog.KEY_EXTRA_DOWN_ID , downId );
						intent.putExtra( DownloadDialog.KEY_EXTRA_DOWN_ID_SP_KEY , SP_KEY_NEWSPAGE_DOWNLOAD_ID );
						context.startActivity( intent );
					}
				}
			} );
		}
	}
}
