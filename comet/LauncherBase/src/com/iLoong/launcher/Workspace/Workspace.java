package com.iLoong.launcher.Workspace;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.IMTKWidget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidInput;
import com.iLoong.base.R;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.PageScrollListener;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.app.LauncherBase;
import com.iLoong.launcher.data.Widget2DInfo;
import com.iLoong.launcher.widget.Widget;
import com.iLoong.launcher.widget.WidgetHostView;


/**
 * The workspace is a wide area with a wallpaper and a finite number of screens. Each
 * screen contains a number of icons, folders or widgets the user can interact with.
 * A workspace is meant to be used with a fixed width only.
 */
public class Workspace extends ViewGroup implements PageScrollListener
{
	
	@SuppressWarnings( { "UnusedDeclaration" } )
	private static final String TAG = "Launcher.Workspace";
	/**
	 * The velocity at which a fling gesture will cause us to snap to the next screen
	 */
	public static WorkspaceStatusEnum WorkspaceStatus = WorkspaceStatusEnum.NormalMode;
	private int mCurrentScreen = 0;
	private boolean canSendMessage;
	private boolean needStopCover = false;
	private boolean down;
	private boolean normal = true;
	private long downTime;
	private int downX;
	private int downY;
	private int downIndex;
	private boolean visible;
	public static WidgetHostView scrollWidget;
	public static WidgetHostView notScrollWidget;
	private LauncherBase launcher;
	
	/**
	 * Used to inflate the Workspace from XML.
	 *
	 * @param context The application's context.
	 * @param attrs The attribtues set containing the Workspace's customization values.
	 */
	public Workspace(
			Context context ,
			AttributeSet attrs )
	{
		this( context , attrs , 0 );
	}
	
	/**
	 * Used to inflate the Workspace from XML.
	 *
	 * @param context The application's context.
	 * @param attrs The attribtues set containing the Workspace's customization values.
	 * @param defStyle Unused.
	 */
	public Workspace(
			Context context ,
			AttributeSet attrs ,
			int defStyle )
	{
		super( context , attrs , defStyle );
	}
	
	public void addInCurrentScreen(
			View child ,
			int x ,
			int y ,
			int spanX ,
			int spanY ,
			boolean insert )
	{
		addInScreen( child , mCurrentScreen , x , y , spanX , spanY , insert );
	}
	
	public void setLauncher(
			LauncherBase launcher )
	{
		this.launcher = launcher;
	}
	
	public LauncherBase getLauncher()
	{
		return launcher;
	}
	
	public enum WorkspaceStatusEnum
	{
		NormalMode , EditMode;
	}
	
	/**
	 * Adds the specified child in the specified screen. The position and dimension of
	 * the child are defined by x, y, spanX and spanY.
	 *
	 * @param child The child to add in one of the workspace's screens.
	 * @param screen The screen in which to add the child.
	 * @param x The X position of the child in the screen's grid.
	 * @param y The Y position of the child in the screen's grid.
	 * @param spanX The number of cells spanned horizontally by the child.
	 * @param spanY The number of cells spanned vertically by the child.
	 * @param insert When true, the child is inserted at the beginning of the children list.
	 */
	public void addInScreen(
			View child ,
			int screen ,
			int x ,
			int y ,
			int spanX ,
			int spanY ,
			boolean insert )
	{
		Log.d( "widget" , "add widget:" + child + " screen=" + screen );
		if( screen < 0 || screen >= getChildCount() )
		{
			Log.e( TAG , "The screen must be >= 0 and < " + getChildCount() + " (was " + screen + "); skipping child" );
			return;
		}
		final CellLayout group = (CellLayout)getChildAt( screen );
		CellLayout.LayoutParams lp = (CellLayout.LayoutParams)child.getLayoutParams();
		if( lp == null )
		{
			lp = new CellLayout.LayoutParams( x , y , spanX , spanY );
		}
		else
		{
			lp.cellX = x;
			lp.cellY = y;
			lp.cellHSpan = spanX;
			lp.cellVSpan = spanY;
		}
		group.addView( child , insert ? 0 : -1 , lp );
		View mtkWidgetView = searchIMTKWidget( group );
		if( mtkWidgetView != null )
		{
			( (IMTKWidget)mtkWidgetView ).setScreen( mCurrentScreen );
			ViewParent pView = (ViewParent)mtkWidgetView.getParent();
			if( pView instanceof WidgetHostView )
			{
				Widget widget = ( (WidgetHostView)pView ).getWidget();
				if( widget != null )
				{
					Widget2DInfo info = widget.getItemInfo();
					( (IMTKWidget)mtkWidgetView ).setWidgetId( info.appWidgetId );
					Log.e( "launcher" , "set WidgetId:" + info.appWidgetId );
				}
				if( ( (WidgetHostView)pView ).isWidget3D )
					( (WidgetHostView)pView ).showSysView();
			}
		}
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
		// The children are given the same width and height as the workspace
		final int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			getChildAt( i ).measure( widthMeasureSpec , heightMeasureSpec );
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
	
	public void addCell()
	{
		CellLayout cell = (CellLayout)LayoutInflater.from( getContext() ).inflate( R.layout.workspace_screen , null );
		this.addView( cell );
	}
	
	/*
	 *  -1表示加到最后面
	 *  -2表示加到最前面
	 * 
	 * 
	 * */
	public void addCellAt(
			int i )
	{
		if( i == -1 )
		{
			addCell();
		}
		else if( i == -2 )
		{
			CellLayout cell = (CellLayout)LayoutInflater.from( getContext() ).inflate( R.layout.workspace_screen , null );
			this.addView( cell , 0 );
		}
		else
		{
			CellLayout cell = (CellLayout)LayoutInflater.from( getContext() ).inflate( R.layout.workspace_screen , null );
			this.addView( cell , i );
		}
	}
	
	public void removeCellAt(
			int i )
	{
		this.removeViewAt( i );
	}
	
	public void reorderCell(
			int oldIndex ,
			int newIndex )
	{
		CellLayout cell = (CellLayout)this.getChildAt( oldIndex );
		this.removeView( cell );
		this.addView( cell , newIndex );
	}
	
	public void exchangeCell(
			int minIndex ,
			int maxIndex )
	{
		View view1 = this.getChildAt( minIndex );
		View view2 = this.getChildAt( maxIndex );
		this.removeView( view1 );
		this.removeView( view2 );
		this.addView( view2 , minIndex );
		this.addView( view1 , maxIndex );
	}
	
	public void removeWidget(
			Widget widget ,
			int screen )
	{
		for( int i = 0 ; i < this.getChildCount() ; i++ )
		{
			// CellLayout l = (CellLayout) this.getChildAt(screen);
			CellLayout l = (CellLayout)this.getChildAt( i );
			l.removeWidget( widget );
		}
	}
	
	public int getCurrentScreen()
	{
		// TODO Auto-generated method stub
		return mCurrentScreen;
	}
	
	@Override
	public void pageScroll(
			float degree ,
			int index ,
			int count )
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public void setCurrentPage(
			int current )
	{
		mCurrentScreen = Math.max( 0 , Math.min( current , getChildCount() - 1 ) );
		Messenger.sendMsg( Messenger.MSG_SCROLL_WORKSPACE , mCurrentScreen * getWidth() , 0 );
	}
	
	@Override
	public int getIndex()
	{
		return mCurrentScreen;
	}
	
	public void startCoverMTKWidgetView()
	{
		//View hostView = getChildAt(mCurrentScreen);
		if( needStopCover )
			return;
		View mtkWidgetView = searchIMTKWidget( this );
		if( mtkWidgetView != null )
		{
			( (IMTKWidget)mtkWidgetView ).startCovered( mCurrentScreen );
			( (IMTKWidget)mtkWidgetView ).leaveAppwidgetScreen();
			ViewParent pView = (ViewParent)mtkWidgetView.getParent();
			if( pView instanceof WidgetHostView )
			{
				if( ( (WidgetHostView)pView ).isWidget3D )
					( (WidgetHostView)pView ).hideSysView();
			}
			needStopCover = true;
			Log.e( "launcher" , "startCovered" );
		}
	}
	
	public void stopCoverMTKWidgetView()
	{
		if( !needStopCover )
			return;
		View hostView = getChildAt( mCurrentScreen );
		View mtkWidgetView = searchIMTKWidget( this );
		if( mtkWidgetView != null )
		{
			( (IMTKWidget)mtkWidgetView ).stopCovered( mCurrentScreen );
			ViewParent pView = (ViewParent)mtkWidgetView.getParent();
			if( pView instanceof WidgetHostView )
			{
				if( ( (WidgetHostView)pView ).isWidget3D )
					( (WidgetHostView)pView ).showSysView();
			}
			( (IMTKWidget)mtkWidgetView ).enterAppwidgetScreen();
			needStopCover = false;
			Log.e( "launcher" , "stopCovered" );
		}
	}
	
	public void moveInMTKWidgetView()
	{
		View hostView = getChildAt( mCurrentScreen );
		View mtkWidgetView = searchIMTKWidget( hostView );
		if( mtkWidgetView != null )
		{
			if( !canSendMessage )
			{
				ViewParent pView = (ViewParent)mtkWidgetView.getParent();
				if( pView instanceof WidgetHostView )
				{
					if( ( (WidgetHostView)pView ).isWidget3D && WorkspaceStatus != WorkspaceStatusEnum.NormalMode )
					{
						canSendMessage = true;
						return;
					}
					if( ( (WidgetHostView)pView ).isWidget3D )
						( (WidgetHostView)pView ).showSysView();
				}
				( (IMTKWidget)mtkWidgetView ).moveIn( mCurrentScreen );
				( (IMTKWidget)mtkWidgetView ).enterAppwidgetScreen();
				Log.e( "launcher" , "moveIn:" );
				canSendMessage = true;
			}
		}
	}
	
	public void moveOutMTKWidgetView()
	{
		View hostView = getChildAt( mCurrentScreen );
		View mtkWidgetView = searchIMTKWidget( hostView );
		if( mtkWidgetView != null )
		{
			if( canSendMessage )
			{
				canSendMessage = false;
				Log.e( "launcher" , "moveOut:" + ( (IMTKWidget)mtkWidgetView ).moveOut( mCurrentScreen ) );
				( (IMTKWidget)mtkWidgetView ).leaveAppwidgetScreen();
				ViewParent pView = (ViewParent)mtkWidgetView.getParent();
				if( pView instanceof WidgetHostView )
				{
					if( !( (WidgetHostView)pView ).isWidget3D )
						Messenger.sendMsg( Messenger.EVENT_WIDGET_GET_VIEW , (WidgetHostView)pView );
					else
					{
						( (WidgetHostView)pView ).hideSysView();
					}
				}
			}
		}
	}
	
	public void moveInMTK3DWidget()
	{
		View hostView = getChildAt( mCurrentScreen );
		View mtkWidgetView = searchIMTKWidget( hostView );
		if( mtkWidgetView != null )
		{
			if( !canSendMessage )
			{
				ViewParent pView = (ViewParent)mtkWidgetView.getParent();
				if( pView instanceof WidgetHostView )
				{
					if( ( (WidgetHostView)pView ).isWidget3D )
					{
						( (WidgetHostView)pView ).showSysView();
						( (IMTKWidget)mtkWidgetView ).moveIn( mCurrentScreen );
						( (IMTKWidget)mtkWidgetView ).enterAppwidgetScreen();
						Log.e( "launcher" , "moveIn:" );
					}
				}
				canSendMessage = true;
			}
		}
	}
	
	public void moveOutMTK3DWidget()
	{
		for( int i = 0 ; i < this.getChildCount() ; i++ )
		{
			View hostView = getChildAt( i );
			View mtkWidgetView = searchIMTKWidget( hostView );
			if( mtkWidgetView != null )
			{
				if( canSendMessage )
				{
					canSendMessage = false;
					ViewParent pView = (ViewParent)mtkWidgetView.getParent();
					if( pView instanceof WidgetHostView )
					{
						if( ( (WidgetHostView)pView ).isWidget3D )
						{
							( (WidgetHostView)pView ).hideSysView();
							Log.e( "launcher" , "moveOut:" + ( (IMTKWidget)mtkWidgetView ).moveOut( i ) );
							( (IMTKWidget)mtkWidgetView ).leaveAppwidgetScreen();
						}
					}
				}
			}
		}
	}
	
	public View searchIMTKWidget(
			View hostView )
	{
		if( hostView instanceof IMTKWidget )
		{
			return hostView;
		}
		else if( hostView instanceof ViewGroup )
		{
			int childCount = ( (ViewGroup)hostView ).getChildCount();
			for( int i = 0 ; i < childCount ; i++ )
			{
				View mtkWidgetView = searchIMTKWidget( ( (ViewGroup)hostView ).getChildAt( i ) );
				if( mtkWidgetView != null )
					return mtkWidgetView;
			}
		}
		return null;
	}
	
	public void show()
	{
		visible = true;
	}
	
	public void hide()
	{
		visible = false;
	}
	
	@Override
	public boolean dispatchTouchEvent(
			MotionEvent ev )
	{
		boolean consume = false;
		int action = ev.getAction();
		int pointer = ev.getPointerId( ev.getActionIndex() );
		//if(!normal && pointer != 0)return true;
		if( action == MotionEvent.ACTION_DOWN )
			normal = true;
		if( launcher.isWorkspace3DTouchable() )
		{
			consume = super.dispatchTouchEvent( ev );
		}
		if( scrollWidget != null && action == MotionEvent.ACTION_DOWN )
		{
			scrollWidget.hideSysView();
			//Log.e("opt", "hide!!!!!!!!!");
		}
		if( consume && action == MotionEvent.ACTION_DOWN )
		{
			normal = false;
			down = true;
			downTime = System.nanoTime();
			downX = (int)ev.getX();
			downY = (int)ev.getY();
			downIndex = pointer;
		}
		//Log.d("widget", "workspace dispatch:"+ev.getAction()+"consume="+consume);
		if( !consume )
		{
			if( normal )
			{
				//Log.d("widget", "gl onTouch 1:"+action+","+pointer);
				( (AndroidInput)Gdx.input ).onTouch( launcher.getGLView() , ev );
			}
			else
			{
				//Log.d("widget", "gl onTouch 2:"+action+","+pointer);
				if( down )
				{
					//Log.d("widget", "gl onTouch 2:down"+","+downIndex+","+downTime);
					( (AndroidInput)Gdx.input ).setCurrentEventTime( downTime );
					launcher.getDesktop().touchDown( downX , downY , downIndex , 0 );
					if( Gdx.graphics != null )
						Gdx.graphics.requestRendering();
					down = false;
				}
				( (AndroidInput)Gdx.input ).setCurrentEventTime( System.nanoTime() );
				switch( action )
				{
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
					case MotionEvent.ACTION_OUTSIDE:
					case MotionEvent.ACTION_CANCEL:
						//Log.d("widget", "gl onTouch 2:up"+","+pointer+","+System.nanoTime());
						launcher.getDesktop().touchUp( (int)ev.getX() , (int)ev.getY() , pointer , 0 );
						break;
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN:
						//Log.d("widget", "gl onTouch 2:down"+","+pointer);
						launcher.getDesktop().touchDown( (int)ev.getX() , (int)ev.getY() , pointer , 0 );
						break;
					case MotionEvent.ACTION_MOVE:
						//Log.d("widget", "gl onTouch 2:drag"+","+pointer);
						launcher.getDesktop().touchDragged( (int)ev.getX() , (int)ev.getY() , pointer );
						break;
				}
			}
		}
		if( notScrollWidget != null && ev.getAction() == MotionEvent.ACTION_UP )
		{
			notScrollWidget.clear();
		}
		return true;
	}
	
	public void longPress()
	{
		if( down )
		{
			Log.d( "widget" , "gl onTouch 2:down" + "," + downIndex + "," + downTime );
			( (AndroidInput)Gdx.input ).setCurrentEventTime( downTime );
			launcher.getDesktop().touchDown( downX , downY , downIndex , 0 );
			if( Gdx.graphics != null )
				Gdx.graphics.requestRendering();
			down = false;
			//			launcher.getDesktop().longPress(downX, downY);
		}
	}
	
	@Override
	public boolean onKeyDown(
			int keyCode ,
			KeyEvent event )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean onKeyUp(
			int keyCode ,
			KeyEvent event )
	{
		// TODO Auto-generated method stub
		return false;
	}
}
