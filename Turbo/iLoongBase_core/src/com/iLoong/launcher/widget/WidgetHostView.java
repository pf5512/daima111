package com.iLoong.launcher.widget;


import java.util.Timer;
import java.util.TimerTask;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.iLoong.base.R;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.ConfigBase;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.Workspace.Workspace;
import com.iLoong.launcher.app.LauncherBase;


public class WidgetHostView extends AppWidgetHostView
{
	
	private boolean mHasPerformedLongPress;
	private CheckForLongPress mPendingCheckForLongPress;
	private CheckForStopDraw checkForStopDraw;
	private DrawWidget drawWidget;
	public static Object drawLock = new Object();
	public Object bmpLock = new Object();
	public Widget widget = null;
	//public boolean drawAndroidView = true;
	public long mUpdated = 0;
	public int update_suc = 0;
	public Bitmap mCustomCache = null;
	public Bitmap textureViewCache = null;
	public Canvas localCanvas;
	public static final int DRAW_NONE = 0;
	public static final int DRAW_START = 1;
	public static final int DRAW_RUN = 2;
	public static final int DRAW_STOP = 3;
	public int state = DRAW_NONE;
	public boolean hasRun = true;
	public boolean forceDrawSystem = false;
	public boolean isWidget3D = false;
	public static boolean left = false;
	public static boolean right = false;
	public static boolean up = false;
	public static boolean down = false;
	public boolean checkDrawSequence = true;
	public boolean checkScrollV = true;
	public boolean enableScrollV = false;
	private boolean longPressed = false;
	private long downTime = 0;
	private float downX = 0;
	private float downY = 0;
	public static float SCROLL_DISTANCE = 25;
	public static final long LONG_PRESS_DURATION = 350;
	public int sysVersion;
	public boolean hasDraw = false;
	public int drawState = 0;//0:start,1:end
	public boolean ignoreDraw = false;
	public boolean invalidateChild = false;
	
	public WidgetHostView(
			Context context )
	{
		super( context );
		setFocusable( true );
		setFocusableInTouchMode( true );
		this.setDrawingCacheEnabled( false );
		this.destroyDrawingCache();
		sysVersion = Integer.parseInt( VERSION.SDK );
		if( sysVersion >= 14 && searchTextureView( this ) != null )
		{
			isWidget3D = true;
			state = DRAW_RUN;
			widget.state = DRAW_NONE;
			//Log.e("widget", "draw system first!!!");
		}
		//SCROLL_DISTANCE *= Gdx.graphics.getDensity();
	}
	
	public void setWidget(
			Widget widget )
	{
		this.widget = widget;
		this.widget.sys_view = this;
	}
	
	public Widget getWidget()
	{
		return this.widget;
	}
	
	public void reset()
	{
		synchronized( drawLock )
		{
			state = DRAW_NONE;
			widget.state = DRAW_RUN;
		}
	}
	
	//xiatian add start	//Widget adaptation "com.android.gallery3d"
	@Override
	public ViewParent invalidateChildInParent(
			int[] location ,
			Rect dirty )
	{
		synchronized( drawLock )
		{
			this.mUpdated++;
			invalidateChild = true;
			AndroidGraphics graphics = ( (AndroidGraphics)( Gdx.graphics ) );
			if( !graphics.launcherPause )
			{
				Gdx.graphics.requestRendering();
			}
			//xiatian add	//Optimization of the refresh rate
		}
		return super.invalidateChildInParent( location , dirty );
	}
	
	//xiatian add end
	public boolean isDescendantOfWidget(
			View hostView ,
			View view )
	{
		if( hostView instanceof ViewGroup )
		{
			if( hostView == view )
				return true;
			int childCount = ( (ViewGroup)hostView ).getChildCount();
			for( int i = 0 ; i < childCount ; i++ )
			{
				if( ( isDescendantOfWidget( ( (ViewGroup)hostView ).getChildAt( i ) , view ) ) )
					return true;
			}
		}
		else if( hostView == view )
			return true;
		return false;
	}
	
	public String getState()
	{
		switch( state )
		{
			case WidgetHostView.DRAW_NONE:
				return "none";
			case WidgetHostView.DRAW_START:
				return "start";
			case WidgetHostView.DRAW_RUN:
				return "run";
			case WidgetHostView.DRAW_STOP:
				return "stop";
		}
		return "";
	}
	
	public void ignoreDraw()
	{
		//ignoreDraw = true;
	}
	
	public void superDraw(
			Canvas paramCanvas )
	{
		//Log.e("widget", "dispatchDraw end:"+this.widget.itemInfo.getPackageName());
		super.dispatchDraw( paramCanvas );
		drawState = 1;
	}
	
	@Override
	public void dispatchDraw(
			Canvas paramCanvas )
	{
		if( ignoreDraw && paramCanvas != localCanvas )
		{
			if( !invalidateChild )
			{
				//Log.e("widget", "widget dispatchDraw,ignore");
				return;
			}
			invalidateChild = false;
			ignoreDraw = false;
		}
		if( paramCanvas != localCanvas )
		{
			//Log.e("widget", "dispatchDraw start:"+this.widget.itemInfo.getPackageName());
			if( drawState == 0 )
				hasDraw = false;
			else
				hasDraw = true;
			drawState = 0;
		}
		if( this.getParent() != null && this.getParent().getParent() != null )
		{
			ViewParent v = this.getParent().getParent();
			if( v instanceof Workspace )
			{
				Workspace workspace = (Workspace)v;
				if( ( workspace.getVisibility() != View.VISIBLE || workspace.getLauncher().getDesktop().hasDown() ) && this.mCustomCache != null && widget.texture != null )
				{
					if( paramCanvas == localCanvas )
					{
						mUpdated = 0;
						update_suc = 0;
					}
					return;
				}
			}
		}
		if( ConfigBase.scale_sys_widget != 1 && ConfigBase.scale_sys_widget > 0 )
		{
			View v = null;
			if( ( v = this.getChildAt( 0 ) ) != null )
			{
				v.setScaleX( ConfigBase.scale_sys_widget );
				v.setScaleY( ConfigBase.scale_sys_widget );
			}
		}
		if( checkDrawSequence )
		{
			checkDrawSequence = false;
			if( sysVersion >= 14 && searchTextureView( this ) != null )
			{
				isWidget3D = true;
				state = DRAW_RUN;
				widget.state = DRAW_NONE;
				//Log.e("widget", "draw system first!!!");
			}
		}
		//super.dispatchDraw(paramCanvas);
		View focus = this.findFocus();
		if( focus != null )
		{
			down = false;
			up = false;
			left = false;
			right = false;
			View downFocus = focus.focusSearch( View.FOCUS_DOWN );
			down = downFocus != null && downFocus != focus && isDescendantOfWidget( this , downFocus ) && !( downFocus instanceof WidgetHostView );
			View upFocus = focus.focusSearch( View.FOCUS_UP );
			up = upFocus != null && upFocus != focus && isDescendantOfWidget( this , upFocus ) && !( upFocus instanceof WidgetHostView );
			View leftFocus = focus.focusSearch( View.FOCUS_LEFT );
			left = leftFocus != null && leftFocus != focus && isDescendantOfWidget( this , leftFocus ) && !( leftFocus instanceof WidgetHostView );
			View rightFocus = focus.focusSearch( View.FOCUS_RIGHT );
			right = rightFocus != null && rightFocus != focus && isDescendantOfWidget( this , rightFocus ) && !( rightFocus instanceof WidgetHostView );
			ViewGroup parent = (ViewGroup)this.getParent().getParent();
			if( parent instanceof Workspace )
			{
				Workspace workspace = (Workspace)parent;
				LauncherBase launcher = workspace.getLauncher();
				if( launcher != null )
				{
					launcher.setCurrentFocusX( focus.getBottom() + widget.itemInfo.x );
				}
			}
		}
		if( widget.canScrollV() )
		{
			synchronized( drawLock )
			{
				if( widget.scrollVCapture() )
				{
					if( paramCanvas == localCanvas )
					{
						capture();
					}
					else
					{
						buildCustomCache();
					}
				}
				else
				{
					//					if(checkScrollV)Log.e("widget", "host.state,widget.state,local canvas:"+state+","+widget.state+","+
					//				(paramCanvas==localCanvas));
					switch( state )
					{
						case DRAW_NONE:
							if( widget.state == DRAW_STOP )
							{
								if( paramCanvas != localCanvas )
								{
									superDraw( paramCanvas );
									state = DRAW_START;
								}
								break;
							}
							if( paramCanvas == localCanvas )
							{
								capture();
							}
							else
							{
								if( forceDrawSystem )
								{
									forceDrawSystem = false;
									superDraw( paramCanvas );
									invalidate();
									//buildCustomCache();
								}
								else
								{
									this.mUpdated++;
									//Log.e("widget", "update++");
								}
							}
							break;
						case DRAW_START:
							if( paramCanvas != localCanvas )
							{
								superDraw( paramCanvas );
							}
							break;
						case DRAW_RUN:
							hasRun = true;
							if( paramCanvas != localCanvas )
							{
								superDraw( paramCanvas );
							}
							return;
						case DRAW_STOP:
							if( widget.state == DRAW_START || widget.state == DRAW_RUN )
							{
								if( paramCanvas == localCanvas )
								{
									capture();
								}
								else
								{
									superDraw( paramCanvas );
								}
								if( widget.state == DRAW_RUN && widget.hasRun )
								{
									state = DRAW_NONE;
								}
								else
									invalidate();
								break;
							}
							if( paramCanvas != localCanvas )
							{
								superDraw( paramCanvas );
								postStopDraw();
							}
							break;
					}
					//Log.d("opt", "sys draw state2="+getState());
				}
			}
			if( isWidget3D && widget.state == DRAW_NONE )
				return;
			if( Gdx.graphics != null )
				( (AndroidGraphics)Gdx.graphics ).forceRender( 5 );
			return;
		}
		synchronized( drawLock )
		{
			if( paramCanvas == localCanvas )
			{
				//Log.e("launcher", "super.dispatchDraw:"+this.widget.getItemInfo().appWidgetId);
				capture();
			}
			else
			{
				this.mUpdated++;
			}
			if( Gdx.graphics != null )
				( (AndroidGraphics)Gdx.graphics ).forceRender( 5 );
			//if (this.mUpdated) {
			//	this.mUpdated = false;
			//	buildCustomCache();
			//}
			return;
		}
	}
	
	public void capture()
	{
		if( localCanvas != null )
		{
			super.dispatchDraw( localCanvas );
		}
		else
		{
			return;
		}
		boolean hasTextureView = false;
		if( sysVersion >= 14 && widget.canScrollV() )
		{
			TextureView textureView = searchTextureView( this );
			if( textureView != null )
			{
				hasTextureView = true;
				if( textureViewCache == null )
					textureViewCache = textureView.getBitmap();
				else
					textureView.getBitmap( textureViewCache );
				if( textureViewCache != null && isValidCache( textureViewCache ) )
				{
					localCanvas.drawBitmap( textureViewCache , ( this.getWidth() - textureViewCache.getWidth() ) / 2 , ( this.getHeight() - textureViewCache.getHeight() ) / 2 , null );
					mUpdated = 0;
					update_suc = 2;
				}
				else
				{
					mUpdated = 0;
					update_suc = 0;
					forceDrawSystem = true;
					invalidate();
				}
			}
		}
		if( !hasTextureView )
		{
			mUpdated = 0;
			update_suc = 2;
		}
	}
	
	public boolean isValidCache(
			Bitmap bmp )
	{
		boolean res = false;
		int i = 0;
		int w = bmp.getWidth();
		int h = bmp.getHeight();
		while( i < 20 )
		{
			i++;
			int x = (int)( Math.random() * w );
			int y = (int)( Math.random() * h );
			int color = bmp.getPixel( x , y );
			if( color != 0xFF000000 )
			{
				res = true;
				break;
			}
		}
		return res;
	}
	
	public TextureView searchTextureView(
			View hostView )
	{
		if( hostView instanceof TextureView )
		{
			return (TextureView)hostView;
		}
		else if( hostView instanceof ViewGroup )
		{
			int childCount = ( (ViewGroup)hostView ).getChildCount();
			for( int i = 0 ; i < childCount ; i++ )
			{
				TextureView textureView = searchTextureView( ( (ViewGroup)hostView ).getChildAt( i ) );
				if( textureView != null )
					return textureView;
			}
		}
		return null;
	}
	
	public void hideSysView()
	{
		//Log.i("widget", "hideSysView");
		if( state == DRAW_NONE )
			return;
		if( checkForStopDraw != null )
			removeCallbacks( checkForStopDraw );
		synchronized( drawLock )
		{
			state = DRAW_NONE;
			widget.state = DRAW_START;
			widget.hasRun = false;
			buildCustomCache();
		}
		if( Gdx.graphics != null )
			Gdx.graphics.requestRendering();
		int i = 0;
		while( !widget.hasRun && i < 20 )
		{
			try
			{
				Thread.sleep( 20 );
				i++;
				//Log.i("opt", "sleep...");
			}
			catch( InterruptedException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//setVisibility(View.INVISIBLE);
		invalidate();
		return;
	}
	
	public void showSysView()
	{
		if( state == DRAW_RUN )
			return;
		if( checkForStopDraw != null )
			removeCallbacks( checkForStopDraw );
		synchronized( drawLock )
		{
			hasRun = false;
			state = DRAW_RUN;
			widget.state = DRAW_NONE;
			invalidate();
		}
		if( Gdx.graphics != null )
			Gdx.graphics.requestRendering();
		return;
	}
	
	class DrawWidget implements Runnable
	{
		
		@Override
		public void run()
		{
			startDrawWidget();
			if( Gdx.graphics != null )
				Gdx.graphics.requestRendering();
		}
	}
	
	class CheckForStopDraw implements Runnable
	{
		
		public void run()
		{
			synchronized( drawLock )
			{
				//Log.i("widget", "runnable");
				widget.hasRun = false;
				widget.state = DRAW_START;
				buildCustomCache();
			}
			if( Gdx.graphics != null )
				Gdx.graphics.requestRendering();
		}
	}
	
	private void postStopDraw()
	{
		if( checkForStopDraw == null )
		{
			checkForStopDraw = new CheckForStopDraw();
		}
		else
		{
			removeCallbacks( checkForStopDraw );
		}
		postDelayed( checkForStopDraw , 500 );
	}
	
	public boolean buildCustomCache()
	{
		long time2 = System.currentTimeMillis();
		//Log.e("widget", "start cache:   "+(time2-Widget.time));
		if( widget != null )
		{
			if( !widget.getItemInfo().canMove )
			{
				if( mCustomCache != null && !mCustomCache.isRecycled() && !widget.canScrollV() )
				{
					mCustomCache.recycle();
					mCustomCache = null;
					localCanvas = null;
				}
				return true;
			}
		}
		synchronized( bmpLock )
		{
			if( mCustomCache != null && !mCustomCache.isRecycled() && !widget.canScrollV() )
			{
				mCustomCache.recycle();
				mCustomCache = null;
				localCanvas = null;
			}
			int width = getWidth();
			int height = getHeight();
			if( width <= 0 || height <= 0 )
			{
				Timer ReDrawTimer = new Timer();
				ReDrawTimer.schedule( new TimerTask() {
					
					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						Messenger.sendMsg( Messenger.MSG_HIDE_WORKSPACE_EX , null );
						Messenger.sendMsg( Messenger.MSG_SHOW_WORKSPACE_EX , null );
						update_suc = 0;
						mUpdated++;
						if( Gdx.graphics != null )
							Gdx.graphics.requestRendering();
						Log.v( "buildCustomCache" , "width <=0 buildCustomCache aaaaa WidgetId=" + widget.getItemInfo().appWidgetId );
					}
				} , 100 );
				return false;
			}
			try
			{
				if( mCustomCache == null )
				{
					mCustomCache = Bitmap.createBitmap( width , height , Bitmap.Config.ARGB_8888 );
				}
			}
			catch( OutOfMemoryError e )
			{
				Log.v( "OOM" , "OOM la aa" );
				e.printStackTrace();
			}
			if( mCustomCache == null )
			{
				localCanvas = new Canvas();
			}
			else
				localCanvas = new Canvas( mCustomCache );
			//				//			AppBar3D.saveBmp(mCustomCache,"empty");
			localCanvas.drawColor( 0 , PorterDuff.Mode.CLEAR );
			//localCanvas.translate(-getScrollX(), -getScrollY());
			synchronized( this.drawLock )
			{
				//this.drawAndroidView = false;
				dispatchDraw( localCanvas );
				//this.drawAndroidView = true;
			}
			//Log.e("widget", "finish cache:  " + (System.currentTimeMillis() - Widget.time)+" "+"cache:"+(System.currentTimeMillis()-time2));
		}
		return true;
	}
	
	@Override
	protected View getErrorView()
	{
		return LayoutInflater.from( this.getContext() ).inflate( R.layout.appwidget_error , this , false );
	}
	
	public boolean longClickable()
	{
		if( !widget.canScrollV() )
			return true;
		else if( !checkScrollV )
			return false;
		else if( enableScrollV )
			return false;
		return true;
	}
	
	@Override
	public boolean dispatchTouchEvent(
			MotionEvent ev )
	{
		//Log.d("widget", "ev:"+ev.getAction()+","+ev.getPointerId(ev.getActionIndex()));
		boolean ignore = false;
		if( widget.canScrollV() )
		{
			final float x = ev.getX();
			final float y = ev.getY();
			switch( ev.getAction() )
			{
				case MotionEvent.ACTION_DOWN:
					if( drawWidget != null )
					{
						removeCallbacks( drawWidget );
					}
					postCheckForLongClick();
					downTime = System.currentTimeMillis();
					longPressed = false;
					checkScrollV = true;
					enableScrollV = false;
					downX = x;
					downY = y;
					break;
				case MotionEvent.ACTION_MOVE:
					if( longPressed )
						return false;
					if( enableScrollV )
						break;
					if( !checkScrollV )
					{
						ignore = true;
						break;
					}
					else if( Math.abs( y - downY ) > SCROLL_DISTANCE )
					{
						if( mPendingCheckForLongPress != null )
						{
							removeCallbacks( mPendingCheckForLongPress );
						}
						enableScrollV = true;
						startDrawWidgetHostView();
						//Log.e("widget", "scrollV");
					}
					else if( Math.abs( x - downX ) > SCROLL_DISTANCE )
					{
						if( mPendingCheckForLongPress != null )
						{
							removeCallbacks( mPendingCheckForLongPress );
						}
						ignore = true;
						checkScrollV = false;
						enableScrollV = false;
						widget.scroll = false;
						Workspace.notScrollWidget = this;
						//Log.e("widget", "scrollH");
					}
					else
					{
						ignore = true;
						long time = System.currentTimeMillis();
						if( time - downTime > LONG_PRESS_DURATION )
						{
							//Log.e("opt", "longPressed");
							hideSysView();
							reset();
							//widget.onLongClick(x, y);
							Workspace.scrollWidget = null;
							longPressed = true;
						}
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					if( mPendingCheckForLongPress != null )
					{
						removeCallbacks( mPendingCheckForLongPress );
					}
					if( longPressed )
						return false;
					widget.scroll = false;
					Workspace.notScrollWidget = null;
					if( !checkScrollV )
						break;
					if( enableScrollV )
					{
						startDrawWidget();
					}
					else
					{
						checkClick();
					}
					break;
			}
		}
		else
		{
			if( ev.getAction() == MotionEvent.ACTION_DOWN )
			{
				Workspace.notScrollWidget = this;
			}
			else if( ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL )
			{
				this.removeCallbacks( dispatchCancelRunnable );
				Workspace.notScrollWidget = null;
			}
		}
		if( longPressed )
			return false;
		//Log.i("widget", "action,ignore:"+ev.getAction()+","+ignore);
		if( !ignore )
			super.dispatchTouchEvent( ev );
		if( !widget.canScrollV() )
			return false;
		if( checkScrollV )
			return true;
		return false;
	}
	
	public void checkClick()
	{
		if( !ConfigBase.widget_click_opt )
			return;
		if( System.currentTimeMillis() - downTime <= ViewConfiguration.getTapTimeout() )
		{
			startDrawWidgetHostView();
			Workspace.scrollWidget = this;
			if( drawWidget == null )
			{
				drawWidget = new DrawWidget();
			}
			else
			{
				removeCallbacks( drawWidget );
			}
			postDelayed( drawWidget , 4000 );
		}
	}
	
	public void startDrawWidgetHostView()
	{
		synchronized( drawLock )
		{
			if( !isWidget3D )
			{
				if( state == DRAW_STOP )
				{
					state = DRAW_RUN;
					widget.state = DRAW_NONE;
					if( checkForStopDraw != null )
						removeCallbacks( checkForStopDraw );
				}
				else
				{
					//Log.e("widget", "stop!!!");
					widget.state = DRAW_STOP;
				}
			}
			Workspace.scrollWidget = null;
		}
		this.invalidate();
	}
	
	public void startDrawWidget()
	{
		synchronized( drawLock )
		{
			enableScrollV = false;
			if( !isWidget3D )
			{
				if( state == DRAW_NONE )
				{
					widget.state = WidgetHostView.DRAW_RUN;
					return;
				}
				if( state != DRAW_RUN )
				{
					widget.state = WidgetHostView.DRAW_NONE;
					state = WidgetHostView.DRAW_RUN;
				}
				else
					state = DRAW_STOP;
				Workspace.scrollWidget = this;
			}
		}
	}
	
	class CheckForLongPress implements Runnable
	{
		
		private int mOriginalWindowAttachCount;
		
		public void run()
		{
			if( ( getParent() != null ) && hasWindowFocus() && mOriginalWindowAttachCount == getWindowAttachCount() && !mHasPerformedLongPress )
			{
				if( performLongClick() )
				{
					mHasPerformedLongPress = true;
				}
			}
			if( ( getParent() != null ) )
			{
				ViewParent parent = getParent().getParent();
				if( parent != null && parent instanceof Workspace )
				{
					( (Workspace)parent ).longPress();
					if( !longPressed )
					{
						hideSysView();
						reset();
						Workspace.scrollWidget = null;
						longPressed = true;
					}
				}
			}
		}
		
		public void rememberWindowAttachCount()
		{
			mOriginalWindowAttachCount = getWindowAttachCount();
		}
	}
	
	private void postCheckForLongClick()
	{
		mHasPerformedLongPress = false;
		if( mPendingCheckForLongPress == null )
		{
			mPendingCheckForLongPress = new CheckForLongPress();
		}
		mPendingCheckForLongPress.rememberWindowAttachCount();
		//Log.d("launcher", "postCheckForLongClick:"+ViewConfiguration.getLongPressTimeout());
		postDelayed( mPendingCheckForLongPress , LONG_PRESS_DURATION );
	}
	
	@Override
	public void cancelLongPress()
	{
		super.cancelLongPress();
		mHasPerformedLongPress = false;
		if( mPendingCheckForLongPress != null )
		{
			removeCallbacks( mPendingCheckForLongPress );
		}
	}
	
	public Runnable dispatchCancelRunnable = new Runnable() {
		
		@Override
		public void run()
		{
			MotionEvent ev = MotionEvent.obtain( 0 , 0 , MotionEvent.ACTION_CANCEL , 0 , 0 , 0 );
			WidgetHostView.this.dispatchTouchEvent( ev );
		}
	};
	
	public void clear()
	{
		this.removeCallbacks( dispatchCancelRunnable );
		this.postDelayed( dispatchCancelRunnable , 200 );
	}
}
