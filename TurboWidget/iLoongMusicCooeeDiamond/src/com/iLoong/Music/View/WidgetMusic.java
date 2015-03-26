package com.iLoong.Music.View;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.provider.MediaStore;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.Music.Timer.ClockTimer;
import com.iLoong.Music.Timer.ClockTimerHandler;
import com.iLoong.Music.Timer.ClockTimerListener;
import com.iLoong.Music.Timer.ClockTimerReceiver;
import com.iLoong.Music.Timer.ClockTimerService;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class WidgetMusic extends WidgetPluginView3D implements ClockTimerListener
{
	
	private static final String TAG = "WidgetMusic";
	public static final String WATCH_BACK_TEXTURE = "watch_back.png";
	public static String THEME_NAME = "iLoong";
	// widget自身运行上下文环境
	private Context mContext = null;
	// 定时器类型
	private TimerTypeEnum mTimer = TimerTypeEnum.timerTask;
	// 时钟背景
	private MusicBackView mClockBackView = null;
	// 时钟定时器
	private ClockTimer mClockTimer = null;
	// 滚动时钟记录当前位移
	private float mRotateX = 0;
	private float mRotateY = 0;
	// 时钟滑动动画
	Tween mScrollTween = null;
	Timeline mPlayTimeline = null;
	// 是否处理点击事件
	private boolean mIsOnClickEvent = false;
	private boolean isTouchdownTrigered = false;
	public MainAppContext mAppContext;
	public CooGdx cooGdx;
	// xiatian add start //Widget3D adaptation "Naked eye 3D"
	// 【可配置】Wiidget3D侧，X方向最大偏移角度。
	public static float MAX_SENSOR_ANGLE_X = 25;
	// 【可配置】Wiidget3D侧，Y方向最大偏移角度。
	public static float MAX_SENSOR_ANGLE_Y = 3.001f;
	// 【可配置】Wiidget3D侧，角度转化系数。可将launcher侧发送过来的角度值相应的翻倍或缩小。
	public static float SENSOR_ANGLE_TRANSFER_COEFFICIENT = (float)0.6;
	// 【可配置】Wiidget3D侧，有效数据最小偏移角度。大于该值才启动动画。
	public static int SENSOR_ANGLE_OFFSET_MIN = 3;
	// 【可配置】Wiidget3D侧，动画持续时间。
	public static float SENSOR_TWEEN_DURATION = 0.75f;
	private MusicController musicController;
	private ArrayList<String> subThemeImageList;
	private int curSubThemeImageIndex = 0;
	private float xOriginOffset = 8;
	private float zOriginOffset = -50;
	public static String defaultPkgName;
	
	public WidgetMusic(
			String name ,
			MainAppContext globalContext ,
			int widgetId )
	{
		super( name );
		new WidgetThemeManager( globalContext );
		long start = System.currentTimeMillis();
		mAppContext = globalContext;
		this.mContext = globalContext.mWidgetContext;
		THEME_NAME = globalContext.mThemeName;
		cooGdx = new CooGdx( globalContext.mGdxApplication );
		xOriginOffset = WidgetThemeManager.getInstance().getFloat( "x_origin_offset" );
		zOriginOffset = WidgetThemeManager.getInstance().getFloat( "z_origin_offset" );
		this.width = WidgetThemeManager.getInstance().getFloat( "clock_width" );
		this.height = WidgetThemeManager.getInstance().getFloat( "clock_height" );
		this.setOrigin( this.width / 2 , this.height / 2 );
		// this.setOriginZ(zOriginOffset);
		//
		// this.setBackgroud(new NinePatch(new TextureRegion(WidgetThemeManager
		// .getInstance().getThemeTexture("red.png"))));
		musicController = new MusicController( mAppContext );
		addMusicBack();
		addPreviousView();
		addPauseView();
		addAlbumFrontView();
		addNextView();
		//		addLyricsView();
		addAlbumFrontHighlightView();
		// addAlbumBackView();
		musicController.startListening();
		// 启动定时器
		mTimer = TimerTypeEnum.timerTask;
		// startClockTimer();
		loadSubThemeImageList();
		long end = System.currentTimeMillis(); // 排序后取得当前时间
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis( end - start );
		Log.v( "创建音乐总时间" , "耗时: " + c.get( Calendar.MINUTE ) + "分 " + c.get( Calendar.SECOND ) + "秒 " + c.get( Calendar.MILLISECOND ) + " 微秒" );
		// ViewInflater viewInflater = new ViewInflater(mAppContext);
		// PluginViewGroup3D viewRoot = viewInflater.inflaterWidget();
		// this.width = viewRoot.width;
		// this.height = viewRoot.height;
		// this.setOrigin(width / 2, height / 2);
		// this.addView(viewRoot);
		//		getDefaultPkgName();
	}
	
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		cooGdx.gl.glDepthMask( true );
		cooGdx.gl.glEnable( GL10.GL_DEPTH_TEST );
		cooGdx.gl.glClear( GL10.GL_DEPTH_BUFFER_BIT ); // 清除屏幕及深度缓存
		cooGdx.gl.glDepthFunc( GL10.GL_LEQUAL );
		super.draw( batch , parentAlpha );
		cooGdx.gl.glDisable( GL10.GL_DEPTH_TEST );
		cooGdx.gl.glClear( GL10.GL_DEPTH_BUFFER_BIT ); // 清除屏幕及深度缓存
		cooGdx.gl.glDepthMask( false );
	}
	
	private void addMusicBack()
	{
		mClockBackView = new MusicBackView( "clockBack" , mAppContext , null );
		mClockBackView.setSize( this.width , this.height );
		this.addView( mClockBackView );
	}
	
	NextView nextView;
	
	private void addNextView()
	{
		nextView = new NextView( "next" , mAppContext , null );
		nextView.setMusicController( this.musicController );
		this.addView( nextView );
	}
	
	PreviousView previouseView;
	
	private void addPreviousView()
	{
		previouseView = new PreviousView( "previous" , mAppContext , null );
		previouseView.setMusicController( this.musicController );
		this.addView( previouseView );
	}
	
	PauseView pauseView;
	
	private void addPauseView()
	{
		pauseView = new PauseView( "pause" , mAppContext , null );
		pauseView.setMusicController( this.musicController );
		this.addView( pauseView );
	}
	
	AlbumFrontView albumFrontView;
	
	private void addAlbumFrontView()
	{
		albumFrontView = new AlbumFrontView( "albumfront" , mAppContext , null );
		albumFrontView.setMusicController( musicController );
		this.addView( albumFrontView );
	}
	
	private void addLyricsView()
	{
		LyricsView view = new LyricsView( "LyricsView" , mAppContext , null );
		view.setSize( this.width , this.height );
		view.setMusicController( musicController );
		this.addView( view );
	}
	
	AlbumBackView albumBackView;
	
	private void addAlbumBackView()
	{
		albumBackView = new AlbumBackView( "albumback" , mAppContext , null );
		albumBackView.setSize( this.width , this.height );
		// albumBackView.setMusicController(musicController);
		this.addView( albumBackView );
	}
	
	AlbumFrontHighlibhgView highlightView;
	
	private void addAlbumFrontHighlightView()
	{
		highlightView = new AlbumFrontHighlibhgView( "pause" , mAppContext , null );
		highlightView.setSize( this.width , this.height );
		this.addView( highlightView );
	}
	
	private void stopClockTimer()
	{
		if( mTimer == TimerTypeEnum.service )
		{
			// 定时器类型为后台Service
			Intent intent = new Intent( mContext , ClockTimerService.class );
			intent.setAction( "com.iLoong.widget.Clock.start" );
			mContext.stopService( intent );
			ClockTimerReceiver receiver = new ClockTimerReceiver( this );
			mContext.unregisterReceiver( receiver );
		}
		else if( mTimer == TimerTypeEnum.timerTask )
		{
			// 定时器类型为Timer
			if( mClockTimer != null )
			{
				mClockTimer.stop();
			}
		}
		else if( mTimer == TimerTypeEnum.handler )
		{
			// 定时器类型为Handle方式触发
			ClockTimerHandler thread = new ClockTimerHandler( null , this );
			thread.stop();
		}
	}
	
	// 启动定时器
	private void startClockTimer()
	{
		clockTimeChanged();
		if( mTimer == TimerTypeEnum.service )
		{
			// 定时器类型为后台Service
			Intent intent = new Intent( mContext , ClockTimerService.class );
			intent.setAction( "com.iLoong.widget.Clock.start" );
			mContext.startService( intent );
			ClockTimerReceiver receiver = new ClockTimerReceiver( this );
			IntentFilter localIntentFilter = new IntentFilter();
			localIntentFilter.addAction( "com.iLoong.widget.clock.change" );
			mContext.registerReceiver( receiver , localIntentFilter );
		}
		else if( mTimer == TimerTypeEnum.timerTask )
		{
			// 定时器类型为Timer
			mClockTimer = new ClockTimer( this , 1000 );
			mClockTimer.start();
		}
		else if( mTimer == TimerTypeEnum.handler )
		{
			// 定时器类型为Handle方式触发
			ClockTimerHandler thread = new ClockTimerHandler( null , this );
			thread.start();
		}
	}
	
	/**
	 * 接受时钟消息，对clock进行更新，目前只是在此更新此时刻时间点需要的参数
	 */
	@Override
	public void clockTimeChanged()
	{
		if( super.refreshRender != null )
		{
			super.refreshRender.RefreshRender();
		}
		if( subThemeImageList != null && subThemeImageList.size() > 1 )
		{
			int subThemeIndex = getRandomSubThemeIndex();
			String subTheme = subThemeImageList.get( subThemeIndex );
			if( nextView != null )
			{
				nextView.changeSkin( subTheme );
			}
			if( previouseView != null )
			{
				previouseView.changeSkin( subTheme );
			}
			if( pauseView != null )
			{
				pauseView.changeSkin( subTheme );
			}
			if( highlightView != null )
			{
				highlightView.changeSkin( subTheme );
			}
		}
	}
	
	private enum TimerTypeEnum
	{
		handler , timerTask , service
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		// Log.v("WidgetClock", "scroll");
		if( !isTouchdownTrigered )
		{
			return true;
		}
		isfilling = false;
		mRotateX += deltaX;
		mRotateY += deltaY;
		setRotationAngle( mRotateY / height * 360 , mRotateX / width * 360 , 0 );
		mIsOnClickEvent = false;
		return true;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		Log.v( "WidgetClock" , "onTouchDown" );
		reset();
		this.setOrigin( this.width / 2 - xOriginOffset , this.height / 2 );
		// if (pointInPrevious(x, y)) {
		// previouseView.onTouchDown(x, y, pointer);
		// } else if (pointInPause(x, y)) {
		// pauseView.onTouchDown(x, y, pointer);
		// } else if (pointInNext(x, y)) {
		// nextView.onTouchDown(x, y, pointer);
		// } else if (pointInAlbum(x, y)) {
		// albumFrontView.onTouchDown(x, y, pointer);
		// }
		isTouchdownTrigered = true;
		mRotateX = 0;
		mRotateY = 0;
		this.rotation = 0;
		if( mScrollTween != null && !mScrollTween.isFinished() )
		{
			mScrollTween.free();
		}
		isfilling = false;
		mIsOnClickEvent = true;
		this.requestFocus();
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		Log.v( "WidgetClock" , "onTouchUp" );
		isTouchdownTrigered = false;
		// if (pointInPrevious(x, y)) {
		// previouseView.onTouchUp(x, y, pointer);
		// } else if (pointInPause(x, y)) {
		// pauseView.onTouchUp(x, y, pointer);
		// } else if (pointInNext(x, y)) {
		// nextView.onTouchUp(x, y, pointer);
		// } else if (pointInAlbum(x, y)) {
		// albumFrontView.onTouchUp(x, y, pointer);
		// }
		float target = 0;
		if( this.rotation <= 180 )
		{
			target = 0;
		}
		else
		{
			target = 360;
		}
		if( !isfilling )
		{
			if( mScrollTween != null )
			{
				mScrollTween.free();
				mScrollTween = null;
			}
			mScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( target );
			mScrollTween.start( View3DTweenAccessor.manager ).setCallback( this );
			this.releaseFocus();
		}
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		this.setOrigin( width / 2 , height / 2 );
		// if (pointInPrevious(x, y)) {
		// previouseView.onLongClick(x, y);
		// } else if (pointInPause(x, y)) {
		// pauseView.onLongClick(x, y);
		// } else if (pointInNext(x, y)) {
		// nextView.onLongClick(x, y);
		// } else if (pointInAlbum(x, y)) {
		// albumFrontView.onLongClick(x, y);
		// }
		this.mIsOnClickEvent = false;
		isTouchdownTrigered = false;
		this.point.x = x;
		this.point.y = y;
		this.toAbsolute( point );
		Vector2 obj = new Vector2( point.x , point.y );
		this.setTag( obj );
		this.releaseFocus();
		return viewParent.onCtrlEvent( this , 0 );
	}
	
	public void reset()
	{
		this.setOrigin( this.width / 2 , this.height / 2 );
		this.setOriginZ( 0 );
		this.setRotationVector( 0 , 0 , 1 );
		this.setRotation( 0 );
	}
	
	@Override
	public void onEvent(
			int type ,
			@SuppressWarnings( "rawtypes" ) BaseTween source )
	{
		// TODO Auto-generated method stub
		if( source == mScrollTween && type == TweenCallback.COMPLETE )
		{
			// reset();
			mScrollTween = null;
		}
		else if( source == mPlayTimeline && type == TweenCallback.COMPLETE )
		{
			mPlayTimeline = null;
		}
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		// Log.v("WidgetClock", "onDoubleClick");
		return true;
	}
	
	boolean isfilling = false;
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		// TODO Auto-generated method stub
		Log.v( "WidgetClock" , "fling" );
		isfilling = true;
		if( isTouchdownTrigered )
		{
			float target = 0;
			if( this.rotation <= 180 )
			{
				target = 0;
			}
			else
			{
				target = 360;
			}
			mScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( target );
			mScrollTween.start( View3DTweenAccessor.manager ).setCallback( this );
			isTouchdownTrigered = false;
		}
		else
		{
			isTouchdownTrigered = false;
		}
		this.releaseFocus();
		return true;
	}
	
	// public boolean pointInPrevious(float x, float y) {
	// int x1 = widgetRes.getDimensionPixelSize(R.dimen.previous_x_1);
	// int x2 = widgetRes.getDimensionPixelSize(R.dimen.previous_x_2);
	// int x3 = widgetRes.getDimensionPixelSize(R.dimen.previous_x_3);
	//
	// int y1 = widgetRes.getDimensionPixelSize(R.dimen.previous_y_1);
	// int y2 = widgetRes.getDimensionPixelSize(R.dimen.previous_y_2);
	// int y3 = widgetRes.getDimensionPixelSize(R.dimen.previous_y_3);
	// Point point1 = new Point(x1, y1);
	// Point point2 = new Point(x2, y2);
	// Point point3 = new Point(x3, y3);
	// float buttonSize=widgetRes.getDimension(R.dimen.button_size)
	// Point curPoint = new Point((int) x, (int) y);
	// return inTriangle(curPoint, point1, point2, point3);
	// }
	private double triangleArea(
			Point pos1 ,
			Point pos2 ,
			Point pos3 )
	{
		double result = Math.abs( ( pos1.x * pos2.y + pos2.x * pos3.y + pos3.x * pos1.y - pos2.x * pos1.y - pos3.x * pos2.y - pos1.x * pos3.y ) / 2.0D );
		return result;
	}
	
	private boolean inTriangle(
			Point pos ,
			Point posA ,
			Point posB ,
			Point posC )
	{
		double triangleArea = triangleArea( posA , posB , posC );
		double area = triangleArea( pos , posA , posB );
		area += triangleArea( pos , posA , posC );
		area += triangleArea( pos , posB , posC );
		double epsilon = 0.0001; // 由于浮点数的计算存在着误差，故指定一个足够小的数，用于判定两个面积是否(近似)相等。
		if( Math.abs( triangleArea - area ) < epsilon )
		{
			return true;
		}
		return false;
	}
	
	// public boolean pointInNext(float x, float y) {
	// int x1 = widgetRes.getDimensionPixelSize(R.dimen.next_x_1);
	// int x2 = widgetRes.getDimensionPixelSize(R.dimen.next_x_2);
	// int x3 = widgetRes.getDimensionPixelSize(R.dimen.next_x_3);
	//
	// int y1 = widgetRes.getDimensionPixelSize(R.dimen.next_y_1);
	// int y2 = widgetRes.getDimensionPixelSize(R.dimen.next_y_2);
	// int y3 = widgetRes.getDimensionPixelSize(R.dimen.next_y_3);
	// Point point1 = new Point(x1, y1);
	// Point point2 = new Point(x2, y2);
	// Point point3 = new Point(x3, y3);
	// Point curPoint = new Point((int) x, (int) y);
	// return inTriangle(curPoint, point1, point2, point3);
	// }
	//
	// public boolean pointInPause(float x, float y) {
	// int x1 = widgetRes.getDimensionPixelSize(R.dimen.play_x_1);
	// int x2 = widgetRes.getDimensionPixelSize(R.dimen.play_x_2);
	// int x3 = widgetRes.getDimensionPixelSize(R.dimen.play_x_3);
	//
	// int y1 = widgetRes.getDimensionPixelSize(R.dimen.play_y_1);
	// int y2 = widgetRes.getDimensionPixelSize(R.dimen.play_y_2);
	// int y3 = widgetRes.getDimensionPixelSize(R.dimen.play_y_3);
	// Point point1 = new Point(x1, y1);
	// Point point2 = new Point(x2, y2);
	// Point point3 = new Point(x3, y3);
	// Point curPoint = new Point((int) x, (int) y);
	// return inTriangle(curPoint, point1, point2, point3);
	// }
	// public boolean pointInAlbum(float x, float y) {
	// int x1 = widgetRes.getDimensionPixelSize(R.dimen.album_front_x_1);
	// int x2 = widgetRes.getDimensionPixelSize(R.dimen.album_front_x_2);
	// int x3 = widgetRes.getDimensionPixelSize(R.dimen.album_front_x_3);
	//
	// int y1 = widgetRes.getDimensionPixelSize(R.dimen.album_front_y_1);
	// int y2 = widgetRes.getDimensionPixelSize(R.dimen.album_front_y_2);
	// int y3 = widgetRes.getDimensionPixelSize(R.dimen.album_front_y_3);
	// Point point1 = new Point(x1, y1);
	// Point point2 = new Point(x2, y2);
	// Point point3 = new Point(x3, y3);
	// Point curPoint = new Point((int) x, (int) y);
	// return inTriangle(curPoint, point1, point2, point3);
	// }
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		Log.v( "WidgetClock" , "onClick" );
		// if (pointInPrevious(x, y)) {
		// previouseView.onClick(x, y);
		// } else if (pointInPause(x, y)) {
		// pauseView.onClick(x, y);
		// } else if (pointInNext(x, y)) {
		// nextView.onClick(x, y);
		// } else if (pointInAlbum(x, y)) {
		// albumFrontView.onClick(x, y);
		// }
		return super.onClick( x , y );
		// return true;
	}
	
	@Override
	public void onDelete()
	{
		super.onDelete();
		Log.v( TAG , "onDelete" );
		stopClockTimer();
		dispose();
	}
	
	@Override
	public void onPause()
	{
		Log.v( TAG , "onPause" );
		stopClockTimer();
	}
	
	@Override
	public void onResume()
	{
		Log.v( TAG , "onResume" );
		super.onResume();
		startClockTimer();
	}
	
	@Override
	public void onStop()
	{
		Log.v( TAG , "onStop" );
		stopClockTimer();
	}
	
	public void onDestroy()
	{
		Log.v( TAG , "onDestroy" );
		stopClockTimer();
		dispose();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		for( int i = 0 ; i < this.getChildCount() ; i++ )
		{
			if( this.getChildAt( i ) instanceof View3D )
			{
				this.getChildAt( i ).dispose();
			}
		}
		if( musicController != null )
		{
			musicController.dispose();
		}
		Utils3D.showPidMemoryInfo( mAppContext.mContainerContext , "com.iLoong.Music" );
		mContext = null;
		mAppContext = null;
		cooGdx = null;
		mClockBackView = null;
		mClockTimer = null;
		mScrollTween = null;
		mIsOnClickEvent = false;
		// Utils3D.showPidMemoryInfo("com.iLoong.Clock");
	}
	
	public void onUninstall()
	{
		super.onUninstall();
		Log.v( TAG , "onUninstall" );
		stopClockTimer();
		dispose();
	}
	
	public ArrayList<String> loadSubThemeImageList()
	{
		String[] subThemeArray = null;
		subThemeImageList = new ArrayList<String>();
		try
		{
			subThemeArray = mAppContext.mWidgetContext.getAssets().list( "iLoong" );
			if( subThemeArray != null && subThemeArray.length > 0 )
			{
				for( int i = 0 ; i < subThemeArray.length ; i++ )
				{
					if( subThemeArray[i].startsWith( "image" ) )
					{
						subThemeImageList.add( subThemeArray[i] );
					}
				}
			}
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if( subThemeImageList.contains( "image" ) )
		{
			subThemeImageList.remove( "image" );
			subThemeImageList.add( 0 , "image" );
		}
		return subThemeImageList;
	}
	
	private int getRandomSubThemeIndex()
	{
		Random rand = new Random();
		int max = 0;
		if( subThemeImageList == null || subThemeImageList.size() == 1 )
		{
			max = 0;
		}
		else
		{
			max = subThemeImageList.size();
		}
		if( max == 0 )
		{
			return max;
		}
		int newIndex = rand.nextInt( max );
		int count = 0;
		while( newIndex == curSubThemeImageIndex )
		{
			newIndex = rand.nextInt( max );
			count++;
			if( count > 100 )
			{
				count = 0;
				break;
			}
			Log.v( TAG , "newIndex:" + newIndex );
		}
		curSubThemeImageIndex = newIndex;
		return newIndex;
	}
	
	@Override
	public boolean onStartWidgetAnimation(
			Object obj ,
			int widgetAnimType ,
			int widgetAnimDirection )
	{
		float target = 0;
		if( this.rotation <= 180 )
		{
			target = 0;
		}
		else
		{
			target = 360;
		}
		float degree = 18;
		if( widgetAnimDirection == 1 )
		{
			degree = 18;
		}
		else if( widgetAnimDirection == 2 )
		{
			degree = -18;
		}
		if( widgetAnimType == 0 || widgetAnimType == 1 || widgetAnimType == 2 )
		{
			if( mPlayTimeline != null )
			{
				mPlayTimeline.free();
				mPlayTimeline = null;
			}
			mPlayTimeline = Timeline.createSequence();
			Log.e( "widgetmusic" , "onStartWidgetAnimation:" + this.getRotation() );
			this.setOrigin( this.width / 2 - xOriginOffset , this.height / 2 );
			this.setOriginZ( zOriginOffset );
			this.setRotationVector( 0 , 1 , 0 );
			mPlayTimeline.push( Tween.to( this , View3DTweenAccessor.ROTATION , 0.3f ).ease( Quad.OUT ).target( target + degree ) );
			// this.setRotationY(20);
			mPlayTimeline.push( Tween.to( this , View3DTweenAccessor.ROTATION , 1.2f ).ease( Bounce.OUT ).target( target ) );
			mPlayTimeline.start( View3DTweenAccessor.manager ).setCallback( this );
		}
		return false;
	}
	
	//	@Override
	//	public WidgetPluginViewMetaData getPluginViewMetaData() {
	//		// TODO Auto-generated method stub
	//		WidgetPluginViewMetaData metaData = new WidgetPluginViewMetaData();
	//		metaData.spanX = Integer.valueOf(mContext.getResources().getInteger(
	//				R.integer.spanX));
	//		metaData.spanY = Integer.valueOf(mContext.getResources().getInteger(
	//				R.integer.spanY));
	//		metaData.maxInstanceCount = mContext.getResources().getInteger(
	//				R.integer.max_instance);
	//		metaData.maxInstanceAlert = mContext.getResources().getString(
	//				R.string.max_instance_alert);
	//		return metaData;
	//	}
	private void getDefaultPkgName()
	{
		PackageManager pm = appContext.mContainerContext.getPackageManager();
		Intent intent = new Intent( MediaStore.INTENT_ACTION_MUSIC_PLAYER );
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
		if( resolveInfos != null )
		{
			for( int i = 0 ; i < resolveInfos.size() ; i++ )
			{
				if( ( resolveInfos.get( i ).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) > 0 )
				{
					defaultPkgName = resolveInfos.get( i ).activityInfo.packageName;
					return;
				}
			}
		}
		else
		{
			intent = new Intent( Intent.ACTION_MAIN );
			intent.addCategory( Intent.CATEGORY_APP_MUSIC );
			resolveInfos = pm.queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
			for( int i = 0 ; i < resolveInfos.size() ; i++ )
			{
				if( ( resolveInfos.get( i ).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) > 0 )
				{
					defaultPkgName = resolveInfos.get( i ).activityInfo.packageName;
					return;
				}
			}
		}
	}
}
