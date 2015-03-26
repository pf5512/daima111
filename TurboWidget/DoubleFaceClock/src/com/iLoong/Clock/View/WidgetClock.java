package com.iLoong.Clock.View;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.cooeecomet.clock.R;
import com.iLoong.Clock.Common.ClockHelper;
import com.iLoong.Clock.Timer.ClockTimer;
import com.iLoong.Clock.Timer.ClockTimerHandler;
import com.iLoong.Clock.Timer.ClockTimerListener;
import com.iLoong.Clock.Timer.ClockTimerReceiver;
import com.iLoong.Clock.Timer.ClockTimerService;
import com.iLoong.Widget.theme.WidgetThemeManager;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.CacheManager;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class WidgetClock extends WidgetPluginView3D implements ClockTimerListener
{
	
	private static final String TAG = "WidgetClock";
	public static final String MESH_CACHE_NAME = "com.iLoong.Clock.meshCache";
	public static final String TEXTURE_CACHE_NAME = "com.iLoong.Clock.textureCache";
	public static final String WATCH_BACK_TEXTURE = "watch_db_back.png";
	public static String THEME_NAME = "iLoong";
	// 3D模型缩放比例
	public static float SCALE_SIZE = 1F;
	public static float SCALE_X = 1F;
	public static float SCALE_Y = 1F;
	public static float SCALE_Z = 1F;
	public static int live = 0;
	// 加载原始模型 
	public static boolean loadOriginalObj = false;
	// 加载压缩模型
	public static boolean saveCompressObj = false;
	// 3D模型统一宽度高度
	public static float MODEL_WIDTH = 320;
	public static float MODEL_HEIGHT = 320;
	// widget自身运行上下文环境
	private Context mContext = null;
	// 定时器类型
	private TimerTypeEnum mTimer = TimerTypeEnum.timerTask;
	// 当前时间小时时刻
	private int mCurrentHour;
	// 当前时间分钟时刻
	private int mCurrentMinute;
	// 当前时间秒钟时刻
	private int mCurrentSecond;
	// 时钟背景
	private ClockBackView mClockBackView = null;
	// 时钟时针
	private ClockHourView mClockHourView = null;
	// 时钟分针
	private ClockMinuteView mClockMinuteView = null;
	// 时钟秒针
	private ClockSecondView mClockSecondView = null;
	// 时钟背景纹理
	private TextureRegion mBackRegion = null;
	// 时钟定时器
	private ClockTimer mClockTimer = null;
	// 滚动时钟记录当前位移
	private float mRotateX = 0;
	private float mRotateY = 0;
	// 时钟滑动动画
	Tween mScrollTween = null;
	// 是否处理点击事件
	private boolean mIsOnClickEvent = false;
	// 点击时钟动画
	private Tween onClickDownTween = null;
	private Tween onClickUpTween = null;
	private Tween onClickReboundOne = null;
	private Tween onClickReboundTwo = null;
	private Tween onClickStopTween = null;
	private Cache<String , Mesh> mMeshCache = null;
	private Cache<String , TextureRegion> mTextureCache = null;
	// 是否使用缓存
	public static boolean useCache = false;
	private boolean isTouchdownTrigered = false;
	public MainAppContext mAppContext;
	public CooGdx cooGdx;
	private BitmapTexture texture;
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
	/**
	 * 
	 * 
	 */
	private int mCurrentWeek;
	private int mPresentWeek;
	private int mCurrentMonth;
	private int mPresentMonth;
	private int mCurrentMeridiem;
	private int mPresentMeridiem;
	private int mPresentSecond;
	private int mPresentMinute;
	private int mPresentHour;
	private int mCurrentDate;
	private int mPresentDate;
	public static Map<String , View3D> mViews = new HashMap<String , View3D>();
	public static Map<String , Texture> mTextures = new HashMap<String , Texture>();
	String path = "theme/widget/clock/comet/image/";
	private final String MAP_NAME_AM = "am";
	private final String MAP_NAME_PM = "pm";
	private final String MAP_NAME_SEPERATOR = "seperator";
	private final String MAP_NAME_TIME_HAND = "timeHand";
	private static final String MAP_NAME_BACK_DOT = "back_dot";
	private static final String MAP_NAME_BACK_MINUTE_HAND = "back_minute_hand";
	private static final String MAP_NAME_BACK_HOUR_HAND = "back_hour_hand";
	private static final String MAP_NAME_BACK_COLOR = "back_color";
	private static final String MAP_NAME_BACK_SECONDDOT = "back_sencond_dot";
	private final String MAP_NAME_BACK_MINUTE_SHADOW = "back_minute_shadow";
	private final String MAP_NAME_BACK_HOUR_SHADOW = "back_hour_shadow";
	private final String MAP_NAME_DATE = "date";
	private final String MAP_NAME_DEFAULT = "timeHand";
	public static final String imgPath = "theme/widget/clock/comet/image/";
	private Calendar mCalendar;
	private boolean isCoverColor = false;
	private float timeWidth = 0;
	private float timeHeight = 0;
	private float monthHeight = 0;
	private float horizontalGap = -10;
	private float verticalGap = 5;
	private float meridiemX = 0;
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	public float xAngleStart = 0;
	public float yAngleStart = 0;
	public float xAngleEnd = 0;
	public float yAngleEnd = 0;
	public float xAngleLast = 0;
	public float yAngleLast = 0;
	private Tween mSensorTween = null;
	private boolean mIsInitSensorTween = false;
	public float xAngleLastScrollTween = 0;
	public float yAngleLastScrollTween = 0;
	private boolean mIsScrollTween = false;
	private boolean mIsInitScrollTween = false;
	public boolean animalInService = false;
	// xiatian add end
	private boolean openDFClock = true;
	private float lastRotationX = 0;
	private Tween mAutoScrollTween = null;
	private boolean isScroolLocked = false;
	private boolean isNeedRotation = false;
	private boolean isOnbackSide = false;;
	private float deltaRotationX;
	private ClockMaskView clockMask;
	private ClockComponent components;
	private ClockSecondGroup secondGroup;
	private float[] dateLength;
	
	@SuppressWarnings( "unchecked" )
	public WidgetClock(
			String name ,
			MainAppContext globalContext ,
			int widgetId )
	{
		super( name );
		new WidgetThemeManager( globalContext.mContainerContext );
		long start = System.currentTimeMillis();
		globalContext.mThemeName = "iLoong";
		globalContext.mContainerContext = globalContext.mWidgetContext;
		mAppContext = globalContext;
		this.mContext = globalContext.mWidgetContext;
		//THEME_NAME = globalContext.mThemeName;
		cooGdx = new CooGdx( globalContext.mGdxApplication );
		// 初始化时钟模型宽度高度
		MODEL_WIDTH = mContext.getResources().getDimension( R.dimen.clock_width );
		MODEL_HEIGHT = mContext.getResources().getDimension( R.dimen.clock_height );
		initResource();
		if( Utils3D.getScreenHeight() < 900 )
		{
			SCALE_X = SCALE_Y = SCALE_Z = SCALE_SIZE = ( (float)mContext.getResources().getDisplayMetrics().density / (float)2.5 );
		}
		else
		{
			SCALE_X = SCALE_Y = SCALE_Z = SCALE_SIZE = ( (float)mContext.getResources().getDisplayMetrics().density / (float)2 );//1.5;
		}
		//		
		verticalGap *= 2;
		this.width = MODEL_WIDTH;
		this.height = MODEL_HEIGHT;
		this.setOrigin( this.width / 2 , this.height / 2 );
		//SCALE_X = SCALE_Y = SCALE_Z = SCALE_SIZE =MODEL_WIDTH/450;
		Log.v( "tween" , "MODEL_WIDTH: " + MODEL_WIDTH + " ,MODEL_HEIGHT" + MODEL_HEIGHT + " ,SCALE_X" + SCALE_X );
		// this.setBackgroud(new NinePatch(new
		// Texture3D(globalContext.gdx,ImageHelpero
		// .bmp2Pixmap(ImageHelper.getImageFromResource(mContext,
		// R.drawable.red)))));
		// 创建纹理，后面传递给各个子view，公共一张纹理
		mMeshCache = ( (Cache<String , Mesh>)CacheManager.getCache( MESH_CACHE_NAME ) );
		mTextureCache = (Cache<String , TextureRegion>)CacheManager.getCache( TEXTURE_CACHE_NAME );
		mMeshCache.clear();
		mTextureCache.clear();
		if( useCache )
		{
			mBackRegion = mTextureCache.get( WATCH_BACK_TEXTURE );
		}
		if( mBackRegion == null )
		{
			// texture = new Texture(mAppContext.gdx, new AndroidFiles(
			// mContext.getAssets()).internal(ClockHelper
			// .getThemeImagePath(THEME_NAME, "watch_back.png")));
			//			InputStream stream = null;
			//			stream = WidgetThemeManager.getInstance()
			//					.getCurrentThemeInputStream(
			//							ClockHelper.getLauncherThemeImagePath(THEME_NAME,
			//									"watch_back.png"));
			//			if (stream != null) {
			//				texture = new BitmapTexture(WidgetThemeManager
			//						.getInstance().getBitmap(stream));
			//			} else {
			//				if (mAppContext.mWidgetContext
			//						.equals(mAppContext.mContainerContext)) {
			//					stream = WidgetThemeManager.getInstance()
			//							.getSysteThemeInputStream(
			//									ClockHelper.getLauncherThemeImagePath(
			//											THEME_NAME, "watch_back.png"));
			//					if (stream == null) {
			//						String bitmapPath = ClockHelper
			//								.getLauncherThemeImagePath("iLoong",
			//										"watch_back.png");
			//						stream = WidgetThemeManager.getInstance()
			//								.getCurrentThemeInputStream(bitmapPath);
			//						if (stream == null) {
			//							stream = WidgetThemeManager.getInstance()
			//									.getSysteThemeInputStream(bitmapPath);
			//						}
			//					}
			//					if (stream != null) {
			//						texture = new BitmapTexture(WidgetThemeManager.getInstance().getBitmap(stream));
			//					}
			//				} else {
			//					String bitmapPath = ClockHelper.getThemeImagePath(
			//							THEME_NAME, "watch_back.png");
			//					FileHandle fileHandle = new AndroidFiles(
			//							mContext.getAssets()).internal(bitmapPath);
			//					if (!fileHandle.exists()) {
			//						bitmapPath = ClockHelper.getLauncherThemeImagePath(
			//								"iLoong", "watch_back.png");
			//						stream = WidgetThemeManager.getInstance()
			//								.getCurrentThemeInputStream(bitmapPath);
			//						if (stream == null) {
			//							bitmapPath = ClockHelper.getThemeImagePath(
			//									"iLoong", "watch_back.png");
			//							fileHandle = new AndroidFiles(mContext.getAssets())
			//									.internal(bitmapPath);
			//							if(fileHandle.exists()){
			//								texture = new BitmapTexture(WidgetThemeManager.getInstance().getBitmap(fileHandle.read()));
			//							}
			//						} else {
			//							texture = new BitmapTexture(WidgetThemeManager.getInstance().getBitmap(stream));
			//						}
			//					} else {
			//						texture = new BitmapTexture(WidgetThemeManager.getInstance().getBitmap(fileHandle.read()));
			//					}
			//				}
			//			}
			try
			{
				texture = new BitmapTexture( BitmapFactory.decodeStream( globalContext.mWidgetContext.getAssets().open( "theme/widget/clock/comet/image/" + "watch_db_back.png" ) ) );
			}
			catch( IOException e )
			{
			}
			texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			mBackRegion = new TextureRegion( texture );
			if( useCache )
			{
				mTextureCache.put( WATCH_BACK_TEXTURE , mBackRegion );
			}
		}
		//components =new ClockComponent("clockComponent",mAppContext, cooGdx, mMeshCache);
		//this.addView(components.backComponent);
		// initBackComponents();
		// 添加背景，时针，分针， 秒针
		// initBackComponents();
		addClockBack();
		secondGroup = new ClockSecondGroup( "secondGroup" , mAppContext , mMeshCache , cooGdx );
		this.addView( secondGroup );
		//	
		initBackComponents();
		initFrontComponents();
		// 启动定时器
		mTimer = TimerTypeEnum.timerTask;
		startClockTimer();
		long end = System.currentTimeMillis(); // 排序后取得当前时间
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis( end - start );
		//		Log.v("创建时钟总时间",
		//				"耗时: " + c.get(Calendar.MINUTE) + "分 " + c.get(Calendar.SECOND)
		//						+ "秒 " + c.get(Calendar.MILLISECOND) + " 微秒");
		setRotationAngle( 180 , 0 , 0 );
		lastRotationX = 180;
		isOnbackSide = true;
	}
	
	public void addComponents()
	{
		this.addView( components );
		components.bringToFront();
	}
	
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		// batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		Object obj = super.getObjectState();
		if( obj != null )
			live = (Integer)obj;
		cooGdx.gl.glDepthMask( true );
		cooGdx.gl.glEnable( GL10.GL_DEPTH_TEST );
		cooGdx.gl.glClear( GL10.GL_DEPTH_BUFFER_BIT ); // 清除屏幕及深度缓存
		cooGdx.gl.glDepthFunc( GL10.GL_LEQUAL );
		super.draw( batch , parentAlpha );
		cooGdx.gl.glDisable( GL10.GL_DEPTH_TEST );
		cooGdx.gl.glClear( GL10.GL_DEPTH_BUFFER_BIT ); // 清除屏幕及深度缓存
		cooGdx.gl.glDepthMask( false );
	}
	
	private void addClockMask()
	{
		clockMask = new ClockMaskView( "clockMask" , mAppContext , cooGdx );
		clockMask.setMeshCache( mMeshCache );
		clockMask.renderMesh( this.x , this.y );
		this.addView( clockMask );
	}
	
	/**
	 * 添加时钟背景
	 */
	private void addClockBack()
	{
		// long timeStart = System.currentTimeMillis();
		mClockBackView = new ClockBackView( "clockBack" , mAppContext , this.mBackRegion );
		mClockBackView.setMeshCache( mMeshCache );
		mClockBackView.renderMesh( this.x , this.y );
		this.addView( mClockBackView );
		mClockBackView.bringToFront();
		// long timeEnd = System.currentTimeMillis();
		// Calendar backCalendar = Calendar.getInstance();
		// backCalendar.setTimeInMillis(timeEnd - timeStart);
		// Log.v("创建表盘总时间",
		// "耗时: " + backCalendar.get(Calendar.MINUTE) + "分 "
		// + backCalendar.get(Calendar.SECOND) + "秒 "
		// + backCalendar.get(Calendar.MILLISECOND) + " 微秒");
		// Log.v("**", "***********************************************");
	}
	
	//	public void addTest(){
	//	    ObjBaseView view =new ObjBaseView("subView_", mAppContext, mMeshCache,"mask.png","mask.obj",0);//"mask.obj"
	//       
	//	    view.enableDepthMode(false);
	// 
	//        this.addView(view);
	//        
	//        view.bringToFront();
	//	}
	/**
	 * 添加时钟时针
	 */
	private void addHourHandView()
	{
		// long timeStart = System.currentTimeMillis();
		mClockHourView = new ClockHourView( "clockHour" , mAppContext , this.mBackRegion );
		mClockHourView.setMeshCache( mMeshCache );
		mClockHourView.renderMesh( this.x , this.y );
		this.addView( mClockHourView );
		mClockHourView.bringToFront();
		// long timeEnd = System.currentTimeMillis();
		// Calendar backCalendar = Calendar.getInstance();
		// backCalendar.setTimeInMillis(timeEnd - timeStart);
		// Log.v("创建时针总时间",
		// "耗时: " + backCalendar.get(Calendar.MINUTE) + "分 "
		// + backCalendar.get(Calendar.SECOND) + "秒 "
		// + backCalendar.get(Calendar.MILLISECOND) + " 微秒");
		// Log.v("**", "***********************************************");
	}
	
	//	private void addTestObject(){
	//	    test t =new test("test", mAppContext);
	//	    t.setMeshCache(mMeshCache);
	//        t.renderMesh(this.x, this.y);
	//        this.addView(t);
	//        t.bringToFront();
	//	}
	/**
	 * 添加时钟分针
	 */
	private void addMinuteHandView()
	{
		// long timeStart = System.currentTimeMillis();
		mClockMinuteView = new ClockMinuteView( "clockMinute" , mAppContext , this.mBackRegion );
		mClockMinuteView.setMeshCache( mMeshCache );
		mClockMinuteView.renderMesh( this.x , this.y );
		this.addView( mClockMinuteView );
		mClockMinuteView.bringToFront();
		//
		// long timeEnd = System.currentTimeMillis();
		// Calendar backCalendar = Calendar.getInstance();
		// backCalendar.setTimeInMillis(timeEnd - timeStart);
		// Log.v("创建分针总时间",
		// "耗时: " + backCalendar.get(Calendar.MINUTE) + "分 "
		// + backCalendar.get(Calendar.SECOND) + "秒 "
		// + backCalendar.get(Calendar.MILLISECOND) + " 微秒");
		// Log.v("**", "***********************************************");
	}
	
	/**
	 * 添加时钟秒针
	 */
	private void addSecondHandView()
	{
		// long timeStart = System.currentTimeMillis();
		mClockSecondView = new ClockSecondView( "clockSecond" , mAppContext , new TextureRegion( components.mTextures.get( "timeHand" ) ) );
		//this.mBackRegion);
		mClockSecondView.setMeshCache( mMeshCache );
		mClockSecondView.renderMesh( this.x , this.y );
		this.addView( mClockSecondView );
		this.bringToFront();
		// long timeEnd = System.currentTimeMillis();
		// Calendar backCalendar = Calendar.getInstance();
		// backCalendar.setTimeInMillis(timeEnd - timeStart);
		// Log.v("创建秒针总时间",
		// "耗时: " + backCalendar.get(Calendar.MINUTE) + "分 "
		// + backCalendar.get(Calendar.SECOND) + "秒 "
		// + backCalendar.get(Calendar.MILLISECOND) + " 微秒");
		// Log.v("**", "***********************************************");
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
		// if(components!=null)
		// components.clockTimeChanged();
		if( secondGroup != null )
		{
			secondGroup.updateSecondHand( mCurrentSecond );
		}
		if( super.refreshRender != null )
		{
			super.refreshRender.RefreshRender();
		}
		mCalendar = Calendar.getInstance();
		long milliseconds = System.currentTimeMillis();
		mCalendar.setTimeInMillis( milliseconds );
		onWeekChange();
		onSecondChange();
		onMinuteChange();
		onHourChange();
		onMeridiemChange();
		onMonthChange();
		onDateChange();
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
		// TODO Auto-generated method stub
		mRotateX += deltaX;
		mRotateY += deltaY;
		float k = 0;
		if( mRotateX != 0 && mRotateY != 0 )
			k = mRotateY / mRotateX;
		else if( mRotateY == 0 )
		{
			return super.scroll( x , y , deltaX , deltaY );
		}
		else
		{
		}
		if( k == 0 || k > 1.7 || k < -1.7 )
		{
			isNeedRotation = true;
		}
		else if( !isNeedRotation )
		{
			isNeedRotation = false;
			startAutoEffect();
			isScroolLocked = false;
			// this.onTouchUp(x,y,0);
			this.releaseFocus();
			return false;//super.scroll(x, y, deltaX, deltaY);
		}
		if( isScroolLocked )
		{
			return true;
		}
		deltaRotationX = mRotateY / height * 360;
		if( !isTouchdownTrigered )
		{
			return true;
		}
		isfilling = false;
		//openDFClock
		if( deltaRotationX >= 90 )
		{
			Log.v( "tween" , "angle >90 isOnbackSide:" + isOnbackSide );
			//            if(isOnbackSide){
			//                showTimeView();
			//            }
			//            else{
			//                hideTimeView();
			//            }
			isScroolLocked = true;
			animalInService = true;
			mAutoScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( lastRotationX + 180 ).delay( 0 ).setCallback( this )
					.start( View3DTweenAccessor.manager );
			lastRotationX = ( lastRotationX + 180 ) % 360;
			isOnbackSide = !isOnbackSide;
		}
		else if( deltaRotationX <= -90 )
		{
			isOnbackSide = !isOnbackSide;
			Log.v( "tween" , "angle <-90 isOnbackSide:" + isOnbackSide );
			//            if(!isOnbackSide){
			//                showTimeView();
			//            }
			//            else{
			//                hideTimeView();
			//            }
			isScroolLocked = true;
			animalInService = true;
			mAutoScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( ( lastRotationX + 180 ) % 360 ).delay( 0 ).setCallback( this )
					.start( View3DTweenAccessor.manager );
			lastRotationX = ( lastRotationX + 180 ) % 360;
		}
		else
		{
			if( !isScroolLocked )
			{
				setRotationAngle( lastRotationX + mRotateY / height * 360 , 0 , 0 );
			}
			else
			{
			}
		}
		mIsOnClickEvent = false;
		// xiatian add start //Widget3D adaptation "Naked eye 3D"
		if( onIsOpenSensor() )
		{
			yAngleLastScrollTween = mRotateY / height * 360;
			xAngleLastScrollTween = mRotateX / width * 360;
			if( yAngleLastScrollTween > 360 )
			{
				yAngleLastScrollTween = 360;
			}
			if( xAngleLastScrollTween > 360 )
			{
				xAngleLastScrollTween = 360;
			}
		}
		// xiatian add end
		return true;
	}
	
	//    private boolean isNeedRotation(float x,float y){
	//        Vector2 tPoint =new Vector2();
	//        tPoint.x=x;
	//        tPoint.y=y;
	//        this.toAbsolute(tPoint);
	//        Vector2 point = new Vector2();
	//        point.x = 0;
	//        point.y = 0;
	//        this.toAbsolute(point);
	//        float realX=point.x;
	//        float realY=point.y;
	//        float deltaX=tPoint.x -(realX+this.width/2);
	//        float deltaY=tPoint.y- (realY+this.height/2);
	//        
	//        float k =deltaY/deltaX;
	//        
	//        if(deltaX>0&&deltaY>0||deltaX<0&&deltaY<0){
	//            if(k>=1)
	//                return true;
	//        }
	//        
	//        if(deltaX>0&&deltaY<0||deltaX<0&&deltaY>0)
	//            if(k<-1)
	//                return true;
	//        
	//        
	//        return false;
	//    }
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		Log.v( "WidgetClock" , "onTouchDown" );
		isTouchdownTrigered = true;
		mRotateX = 0;
		mRotateY = 0;
		if( mScrollTween != null && !mScrollTween.isFinished() )
		{
			mScrollTween.free();
		}
		if( openDFClock )
		{
			if( mAutoScrollTween != null && !mAutoScrollTween.isFinished() )
			{
				mAutoScrollTween.free();
				mAutoScrollTween = null;
				Log.v( "Clock" , "free tween" );
			}
			Log.v( "Clock" , "not free tween" );
		}
		else
		{
			this.rotation = 0;
		}
		isfilling = false;
		mIsOnClickEvent = true;
		this.requestFocus();
		return true;
	}
	
	public void startAutoEffect()
	{
		Log.v( "tween" , "onTouchUp isOnbackSide:" + isOnbackSide + " rotation: " + rotation + " isScroolLocked " + isScroolLocked );
		if( !isScroolLocked )
		{
			if( ( this.rotation < 90 && this.rotation != 0 && this.rotation != 360 ) || ( rotation > 90 && rotation < 180 ) || ( isOnbackSide && ( rotation > 180 && rotation < 270 ) ) )
			{
				float deltaTime = 0.9f;
				if( lastRotationX < 180 )
					deltaTime = ( ( Math.abs( this.rotation ) + 90 ) % 90 ) / 100.0f;
				else
					deltaTime = ( Math.abs( ( 180 - Math.abs( this.rotation ) ) ) % 90 ) / 100.0f;
				Log.v( "tween" , " touch up deltaTime " + deltaTime + " rotation; " + rotation + "to " + lastRotationX );
				animalInService = true;
				mAutoScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , deltaTime ).ease( Linear.INOUT ).target( lastRotationX ).delay( 0 ).start( View3DTweenAccessor.manager )
						.setCallback( this );
			}
			else if( !isOnbackSide && ( rotation > 270 && rotation < 360 ) )
			{
				float deltaTime = ( ( Math.abs( 360 - this.rotation ) ) % 90 ) / 100.0f;
				Log.v( "tween" , " touch up deltaTime " + deltaTime + " rotation; " + rotation + " to 360" );
				animalInService = true;
				mAutoScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , deltaTime ).ease( Linear.INOUT ).target( 360 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
			//lastRotationX=(lastRotationX+180)%360;
		}
		isScroolLocked = false;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		Log.v( "WidgetClock" , "onTouchUp" );
		isTouchdownTrigered = false;
		isNeedRotation = false;
		if( !isfilling )
		{
			float cur = 0;
			if( this.rotation <= 180 )
			{
				cur = 0;
			}
			else
			{
				cur = 360;
			}
			//			if(isOnbackSide){
			//			    mViews.get(MAP_NAME_BACK_MINUTE_HAND).bringToFront();
			//			}
			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if( !onIsOpenSensor() )
			{
				if( openDFClock )
				{
					startAutoEffect();
				}
				else
				{
					mScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( cur ).delay( 0 ).start( View3DTweenAccessor.manager );
				}
			}
			else
			{
				// xiatian add start //fix bug:0001988
				if( ( xAngleEnd != xAngleLastScrollTween ) && ( yAngleEnd != yAngleLastScrollTween ) )
				{
					// xiatian add end
					mIsScrollTween = true;
					mIsInitScrollTween = true;
					setPosition( xAngleEnd , yAngleEnd );
					mScrollTween = Tween.to( this , View3DTweenAccessor.POS_XY , 1.2f ).target( xAngleEnd , yAngleEnd ).ease( Cubic.OUT ).setCallback( this ).delay( 0 )
							.start( View3DTweenAccessor.manager );
				}// xiatian add end //fix bug:0001988
			}
			// xiatian add end
			this.releaseFocus();
		}
		return false;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		Log.v( "Clock" , "animalInService  " + animalInService );
		if( animalInService )
		{
			//fix bug : when being long-clicked,clock will stop the animal during  scrolling. 
			startAutoEffect();
			//  return true;
		}
		if( isOnbackSide )
		{
			x = this.width - x;
			y = this.height - y;
		}
		if( isPointInClock( x , y ) )
		{
			this.mIsOnClickEvent = false;
			// TODO Auto-generated method stub
			this.point.x = x;
			this.point.y = y;
			this.toAbsolute( point );
			Vector2 obj = new Vector2( point.x , point.y );
			this.setTag( obj );
			this.releaseFocus();
			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			isTouchdownTrigered = false;
			// xiatian add end
			return viewParent.onCtrlEvent( this , 0 );
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public void onEvent(
			int type ,
			@SuppressWarnings( "rawtypes" ) BaseTween source )
	{
		// TODO Auto-generated method stub
		if( source.equals( mAutoScrollTween ) && type == TweenCallback.COMPLETE )
		{
			Log.v( "tween" , "xxxxxxx onEvent" );
			animalInService = false;
		}
		else if( source.equals( onClickDownTween ) && type == TweenCallback.COMPLETE )
		{
			onClickDownTween = null;
			onClickUpTween = Tween.to( this , View3DTweenAccessor.ROTATION , 0.2f ).target( -25 , -25 , 0 ).ease( Quad.INOUT ).setCallback( this );
			onClickUpTween.start( View3DTweenAccessor.manager );
		}
		else if( source.equals( onClickUpTween ) && type == TweenCallback.COMPLETE )
		{
			onClickUpTween = null;
			// Quad.INOUT
			onClickReboundOne = Tween.to( this , View3DTweenAccessor.ROTATION , 0.2f ).target( 20 , 20 , 0 ).ease( Circ.OUT ).setCallback( this );
			onClickReboundOne.start( View3DTweenAccessor.manager );
		}
		else if( source.equals( onClickReboundOne ) && type == TweenCallback.COMPLETE )
		{
			onClickReboundOne = null;
			onClickReboundTwo = Tween.to( this , View3DTweenAccessor.ROTATION , 0.2f ).target( -15 , -15 , 0 ).ease( Circ.OUT ).setCallback( this );
			onClickReboundTwo.start( View3DTweenAccessor.manager );
			// Elastic.OUT
		}
		else if( source.equals( onClickReboundTwo ) && type == TweenCallback.COMPLETE )
		{
			onClickReboundTwo = null;
			// Quad.INOUT
			onClickStopTween = Tween.to( this , View3DTweenAccessor.ROTATION , 0.3f ).target( 0 , 0 , 0 ).ease( Back.OUT ).setCallback( this );
			onClickStopTween.start( View3DTweenAccessor.manager );
		}
		else if( source.equals( onClickStopTween ) && type == TweenCallback.COMPLETE )
		{
			onClickStopTween = null;
			this.releaseFocus();
			// xiatian add start //Widget3D adaptation "Naked eye 3D"
		}
		else if( source.equals( mScrollTween ) && type == TweenCallback.COMPLETE && onIsOpenSensor() )
		{
			mIsScrollTween = false;
			mIsInitScrollTween = false;
			xAngleLastScrollTween = 0;
			yAngleLastScrollTween = 0;
		}
		else if( source.equals( mSensorTween ) && type == TweenCallback.COMPLETE && onIsOpenSensor() )
		{
			mSensorTween = null;
			// xiatian add end
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
	
	//	@Override
	//	public boolean fling(float velocityX, float velocityY) {
	//		// TODO Auto-generated method stub
	//		Log.v("WidgetClock", "fling");
	//		isTouchdownTrigered = false;
	//		isfilling = true;
	//		
	//		float cur = 0;
	//		if (this.rotation <= 180) {
	//			cur = 0;
	//		} else {
	//			cur = 360;
	//		}
	//
	//		// xiatian add start //Widget3D adaptation "Naked eye 3D"
	//		if (!onIsOpenSensor()) {
	////		    if(openDFClock){
	////                if(!isScroolLocked){
	////                    mAutoScrollTween = Tween
	////                          .to(this, View3DTweenAccessor.ROTATION, 1.2f)
	////                          .ease(Quint.OUT).target(lastRotationX).delay(0)
	////                          .start(View3DTweenAccessor.manager);
	////                    //lastRotationX=(lastRotationX+180)%360;
	////                }
	////                
	////                isScroolLocked =false;
	////            }
	////            else
	//                mScrollTween = Tween.to(this, View3DTweenAccessor.ROTATION, 1.2f)
	//					.ease(Quint.OUT).target(cur).delay(0)
	//					.start(View3DTweenAccessor.manager);
	//			;
	//		} else {
	//
	//			// xiatian add start //fix bug:0001988
	//			if ((xAngleEnd != xAngleLastScrollTween)
	//					&& (yAngleEnd != yAngleLastScrollTween)) {
	//				// xiatian add end
	//
	//				mIsScrollTween = true;
	//				mIsInitScrollTween = true;
	//				setPosition(xAngleEnd, yAngleEnd);
	//				mScrollTween = Tween.to(this, View3DTweenAccessor.POS_XY, 1.2f)
	//						.target(xAngleEnd, yAngleEnd).ease(Cubic.OUT)
	//						.setCallback(this).delay(0)
	//						.start(View3DTweenAccessor.manager);
	//			}// xiatian add end //fix bug:0001988
	//		}
	//		// xiatian add end
	//
	//		this.releaseFocus();
	//		return true;
	//	}
	private boolean isPointInClock(
			float x ,
			float y )
	{
		double r = Math.sqrt( ( x - this.originX ) * ( x - this.originX ) + ( y - this.originY ) * ( y - this.originY ) );
		if( r > this.width / 2 || r > this.height / 2 )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	//	@Override
	//	public boolean onClick(float x, float y) {
	//		// TODO Auto-generated method stub
	//		Log.v("WidgetClock", "onClick");
	//		if (isPointInClock(x, y)) {
	//			try {
	//				String packageName = null;
	//				SharedPreferences p = mAppContext.mContainerContext
	//						.getSharedPreferences("iLoong.Widget.Clock", 0);
	//				packageName = p.getString("clock_package", null);
	//				if (packageName == null) {
	//					listPackages();
	//					Editor editor = p.edit();
	//					if (pagList.size() != 0) {
	//						packageName = pagList.get(0);
	//						editor.putString("clock_package", packageName);
	//					}
	//					editor.commit();
	//				}
	//				PackageManager pm = mContext.getPackageManager();
	//				if (packageName != null) {
	//					Intent intent = pm.getLaunchIntentForPackage(packageName);
	//					if (intent != null) {
	//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	//						mContext.startActivity(intent);
	//					} else {
	//						Intent i2 = new Intent(Settings.ACTION_DATE_SETTINGS);
	//						i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	//						mContext.startActivity(i2);
	//					}
	//				} else {
	//					Intent i2 = new Intent(Settings.ACTION_DATE_SETTINGS);
	//					mContext.startActivity(i2);
	//				}
	//			} catch (Exception ex) {
	//				ex.printStackTrace();
	//			}
	//			this.releaseFocus();
	//			return true;
	//		} else {
	//			return false;
	//		}
	//	}
	// public static void transformTheme(Context widgetContext,
	// ComponentName themeComponentName) {
	// THEME_NAME = ClockHelper.transformThemeName(widgetContext,
	// themeComponentName);
	// }
	private HashMap<String , Object> item = new HashMap<String , Object>();
	private List<String> pagList = new ArrayList<String>();
	
	private void listPackages()
	{
		ArrayList<PInfo> apps = getInstalledApps( false ); /*
															* false = no system
															* packages
															*/
		final int max = apps.size();
		for( int i = 0 ; i < max ; i++ )
		{
			apps.get( i ).prettyPrint();
			item = new HashMap<String , Object>();
			int aa = apps.get( i ).pname.length();
			// String
			// bb=apps.get(i).pname.substring(apps.get(i).pname.length()-11);
			// Log.d("mxt", bb);
			if( aa > 11 )
			{
				if( apps.get( i ).pname.indexOf( "clock" ) != -1 )
				{
					if( !( apps.get( i ).pname.indexOf( "widget" ) != -1 ) )
					{
						try
						{
							PackageInfo pInfo = mContext.getPackageManager().getPackageInfo( apps.get( i ).pname , 0 );
							if( isSystemApp( pInfo ) || isSystemUpdateApp( pInfo ) )
							{
								item.put( "pname" , apps.get( i ).pname );
								item.put( "appname" , apps.get( i ).appname );
								pagList.add( apps.get( i ).pname );
							}
						}
						catch( Exception e )
						{
							// TODO: handle exception
						}
					}
				}
			}
			/*
			 * if(apps.get(i).pname.subSequence(apps.get(i).pname.length()-11,
			 * apps.get(i).pname.length()) != null){
			 * 
			 * }
			 */
		}
	}
	
	private ArrayList<PInfo> getInstalledApps(
			boolean getSysPackages )
	{
		ArrayList<PInfo> res = new ArrayList<PInfo>();
		List<PackageInfo> packs = mContext.getPackageManager().getInstalledPackages( 0 );
		for( int i = 0 ; i < packs.size() ; i++ )
		{
			PackageInfo p = packs.get( i );
			if( ( !getSysPackages ) && ( p.versionName == null ) )
			{
				continue;
			}
			PInfo newInfo = new PInfo();
			newInfo.appname = p.applicationInfo.loadLabel( mContext.getPackageManager() ).toString();
			newInfo.pname = p.packageName;
			newInfo.versionName = p.versionName;
			newInfo.versionCode = p.versionCode;
			res.add( newInfo );
		}
		return res;
	}
	
	public boolean isSystemApp(
			PackageInfo pInfo )
	{
		return( ( pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) != 0 );
	}
	
	public boolean isSystemUpdateApp(
			PackageInfo pInfo )
	{
		return( ( pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP ) != 0 );
	}
	
	class PInfo
	{
		
		private String appname = "";
		private String pname = "";
		private String versionName = "";
		private int versionCode = 0;
		
		private void prettyPrint()
		{
			Log.i( "taskmanger" , appname + "\t" + pname + "\t" + versionName + "\t" + versionCode + "\t" );
		}
	}
	
	@Override
	public void onDelete()
	{
		Log.v( TAG , "onDelete" );
		stopClockTimer();
		if( mBackRegion != null )
		{
			mBackRegion.getTexture().dispose();
			mBackRegion = null;
		}
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
		if( mBackRegion != null )
		{
			mBackRegion.getTexture().dispose();
			mBackRegion = null;
		}
		for( int i = 0 ; i < mViews.size() ; i++ )
		{
			mViews.get( i ).dispose();
		}
		mViews.clear();
		for( int i = 0 ; i < mTextures.size() ; i++ )
		{
			mTextures.get( i ).dispose();
		}
		mTextures.clear();
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
		Utils3D.showPidMemoryInfo( mAppContext.mContainerContext , "com.iLoong.Clock" );
		mContext = null;
		mAppContext = null;
		cooGdx = null;
		texture = null;
		mClockBackView = null;
		mClockHourView = null;
		mClockMinuteView = null;
		mClockSecondView = null;
		mBackRegion = null;
		mClockTimer = null;
		mScrollTween = null;
		mIsOnClickEvent = false;
		onClickDownTween = null;
		onClickUpTween = null;
		onClickReboundOne = null;
		onClickReboundTwo = null;
		onClickStopTween = null;
		mAutoScrollTween = null;
		// Utils3D.showPidMemoryInfo("com.iLoong.Clock");
	}
	
	public void onUninstall()
	{
		super.onUninstall();
		Log.v( TAG , "onUninstall" );
		stopClockTimer();
		if( mBackRegion != null )
		{
			mBackRegion.getTexture().dispose();
			mBackRegion = null;
		}
		dispose();
	}
	
	// xiatian add start //Widget3D adaptation "Naked eye 3D"
	public boolean isCurLauncherSupportSensor()
	{
		if( mAppContext == null )
		{
			return false;
		}
		int versionCode = -1;
		String CurLauncherName = mAppContext.mContainerContext.getPackageName();
		// Log.v("xiatian", "CurLauncherName:" + CurLauncherName);
		try
		{
			PackageManager pm = mAppContext.mContainerContext.getPackageManager();
			PackageInfo pinfo = pm.getPackageInfo( CurLauncherName , PackageManager.GET_CONFIGURATIONS );
			versionCode = pinfo.versionCode;
		}
		catch( NameNotFoundException e )
		{
		}
		// Log.v("xiatian", "CurLauncherVersionCode:" + versionCode);
		if( ( CurLauncherName.equals( "com.coco.launcher" ) ) && ( versionCode >= 7260 ) )
		{
			// Log.v("xiatian", "ok");
			return true;
		}
		else
		{
			// Log.v("xiatian", "not-ok");
			return false;
		}
	}
	
	@Override
	public boolean onIsShowSensor()
	{
		if( mAppContext == null )
		{
			return false;
		}
		if( isCurLauncherSupportSensor() == false )
		{
			return false;
		}
		return super.onIsShowSensor();
	}
	
	@Override
	public boolean onIsOpenSensor()
	{
		if( mAppContext == null )
		{
			return false;
		}
		if( onIsShowSensor() )
		{
			if( super.onIsOpenSensor() )
			{
				return true;
			}
			else
			{
				xAngleStart = 0;
				yAngleStart = 0;
				xAngleEnd = 0;
				yAngleEnd = 0;
				xAngleLast = 0;
				yAngleLast = 0;
				mSensorTween = null;
				mIsInitSensorTween = false;
				xAngleLastScrollTween = 0;
				yAngleLastScrollTween = 0;
				mIsScrollTween = false;
				mIsInitScrollTween = false;
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public void onSensorAngleChange(
			float yAngle ,
			float xAngle ,
			boolean isInit )
	{
		if( mAppContext == null )
		{
			return;
		}
		// 是否支持重力感应功能
		if( onIsShowSensor() )
		{
			// Log.v("xiatian", "onSensorAngleChange -- init - :"+"xAngle=" +
			// xAngle + ",yAngle=" + yAngle);
			// 是否打开重力感应
			if( onIsOpenSensor() == false )
			{
				return;
			}
			// 是否设置过初始值
			if( isInit == true )
			{
				// xiatian add start //fix bug:0001988
				isTouchdownTrigered = false;
				// xiatian add end
				xAngleStart = 0;
				yAngleStart = 0;
				xAngleEnd = 0;
				yAngleEnd = 0;
				xAngleLastScrollTween = 0;
				yAngleLastScrollTween = 0;
				setRotationAngle( yAngle , xAngle , 0 );
				Gdx.graphics.requestRendering();
				return;
			}
			// 是否点击在WIidget3D上并在滑动
			if( isTouchdownTrigered )
			{
				return;
			}
			// Log.v("xiatian", "onSensorAngleChange -- before:"+"xAngle=" +
			// xAngle + ",yAngle=" + yAngle);
			// 角度转化
			xAngle *= SENSOR_ANGLE_TRANSFER_COEFFICIENT;
			yAngle *= SENSOR_ANGLE_TRANSFER_COEFFICIENT;
			// 大于最大偏移角度时的角度修正
			if( xAngle < -MAX_SENSOR_ANGLE_X )
			{
				xAngle = -MAX_SENSOR_ANGLE_X;
			}
			else if( xAngle > MAX_SENSOR_ANGLE_X )
			{
				xAngle = MAX_SENSOR_ANGLE_X;
			}
			if( yAngle < -MAX_SENSOR_ANGLE_Y )
			{
				yAngle = -MAX_SENSOR_ANGLE_Y;
			}
			else if( yAngle > MAX_SENSOR_ANGLE_Y )
			{
				yAngle = MAX_SENSOR_ANGLE_Y;
			}
			// Log.v("xiatian", "onSensorAngleChange -- after:"+"xAngle=" +
			// xAngle + ",yAngle=" + yAngle);
			// //是否超过有效数据最小偏移角度
			if( !isDataValid( xAngleStart , xAngle ) )
			{
				xAngle = xAngleStart;
			}
			if( !isDataValid( yAngleStart , yAngle ) )
			{
				yAngle = yAngleStart;
			}
			if( ( xAngleStart == xAngle ) && ( yAngleStart == yAngle ) )
			{
				return;
			}
			if( mScrollTween != null )
			{
				mScrollTween = null;
			}
			// 动画参数赋初值
			if( ( xAngleStart == 0 ) && ( yAngleStart == 0 ) && ( xAngleEnd == 0 ) && ( yAngleEnd == 0 ) )
			{
				xAngleEnd = xAngle;
				yAngleEnd = yAngle;
			}
			else
			{
				xAngleStart = xAngleEnd;
				yAngleStart = yAngleEnd;
				xAngleEnd = xAngle;
				yAngleEnd = yAngle;
			}
			// 前一个动画没播放完之前收到有效数据，修正动画的初始值
			if( mSensorTween != null )
			{
				this.stopTween();
				mSensorTween.free();
				xAngleStart = xAngleLast;
				yAngleStart = yAngleLast;
			}
			// yAngleStart = yAngleLast = yAngleEnd = 0;//不要竖直方向偏转
			// 开始动画
			mIsInitSensorTween = true;
			mIsScrollTween = false;
			setPosition( xAngleEnd , yAngleEnd );
			mSensorTween = Tween.to( this , View3DTweenAccessor.POS_XY , SENSOR_TWEEN_DURATION ).target( xAngleEnd , yAngleEnd ).ease( Cubic.OUT ).setCallback( this ).delay( 0 )
					.start( View3DTweenAccessor.manager );
		}
	}
	
	@Override
	public float getX()
	{
		if( mAppContext == null )
		{
			return 0f;
		}
		if( onIsShowSensor() )
		{
			if( !mIsScrollTween )
			{
				return xAngleStart;
			}
			else
			{
				return xAngleLastScrollTween;
			}
		}
		else
		{
			return 0f;
		}
	}
	
	@Override
	public float getY()
	{
		if( mAppContext == null )
		{
			return 0f;
		}
		if( onIsShowSensor() )
		{
			if( !mIsScrollTween )
			{
				return yAngleStart;
			}
			else
			{
				return yAngleLastScrollTween;
			}
		}
		else
		{
			return 0f;
		}
	}
	
	@Override
	public void setPosition(
			float x ,
			float y )
	{
		if( mAppContext == null )
		{
			return;
		}
		if( onIsShowSensor() )
		{
			if( !mIsScrollTween )
			{
				if( mIsInitSensorTween )
				{
					mIsInitSensorTween = false;
					return;
				}
			}
			else
			{
				if( mIsInitScrollTween )
				{
					mIsInitScrollTween = false;
					return;
				}
			}
			xAngleLast = x;
			yAngleLast = y;
			setRotationAngle( y , x , 0 );
			Gdx.graphics.requestRendering();
		}
	}
	
	public boolean isDataValid(
			float mDataCur ,
			float mDataLast )
	{
		if( mAppContext == null )
		{
			return false;
		}
		if( onIsShowSensor() )
		{
			boolean mDataCurIsPositiveNumber = ( mDataCur > 0 );
			boolean xDataLastIsPositiveNumber = ( mDataLast > 0 );
			if( ( ( mDataCurIsPositiveNumber ) && ( xDataLastIsPositiveNumber ) && ( ( Math.abs( mDataLast - mDataCur ) > SENSOR_ANGLE_OFFSET_MIN ) ) ) || ( ( !mDataCurIsPositiveNumber ) && ( !xDataLastIsPositiveNumber ) && ( ( Math
					.abs( mDataLast - mDataCur ) > SENSOR_ANGLE_OFFSET_MIN ) ) ) || ( Math.abs( mDataLast - mDataCur ) > SENSOR_ANGLE_OFFSET_MIN ) )
			{
				return true;
			}
			return false;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public WidgetPluginViewMetaData getPluginViewMetaData()
	{
		// TODO Auto-generated method stub
		WidgetPluginViewMetaData metaData = new WidgetPluginViewMetaData();
		metaData.spanX = Integer.valueOf( mContext.getResources().getInteger( R.integer.spanX ) );
		metaData.spanY = 2;//Integer.valueOf(mContext.getResources().getInteger(R.integer.spanY));
		metaData.maxInstanceCount = mContext.getResources().getInteger( R.integer.max_instance );
		metaData.maxInstanceAlert = mContext.getResources().getString( R.string.max_instance_alert );
		Log.v( "Clock" , "1111metaData.spanY  " + metaData.spanY );
		return metaData;
	}
	
	// xiatian add end
	public void initBackComponents()
	{
		// initResource();
		addBackHands();
	}
	
	public void initFrontComponents()
	{
		float gap = 0;
		//Note: do not change the invoking order of following functions
		addTime();
		addClockMediriemView( "meridiem" , MAP_NAME_AM , MAP_NAME_AM );
		addClockMonthView( "month" , "month" , "month_1" , 0 , 0.0f );
		addClockDateView( "date" , "day" , "date_1" , 0 , 0.0f );
		addClockDateView( "date1" , "day" , "date_1" , 1 , 0.0f );
		float posX = this.width / 2 + ( dateLength[0] + dateLength[1] ) / 2.0f - dateLength[1] / 2;
		addClockDateView( "date2" , "day" , "date_1" , 2 , posX );
		addClockWeekView( "week" , "week" , "week_1" );
		float mposX = this.width / 2 - ( dateLength[0] + dateLength[1] + gap ) / 2 + dateLength[0] / 2;
		;
		addClockMonthView( "month1" , "month" , "month_1" , 1 , mposX );
		Log.v( "Clock" , "dateLength[0]: " + dateLength[0] + " ,dateLength[1]: " + dateLength[1] );
	}
	
	public void initResource()
	{
		try
		{
			mTextures.put( MAP_NAME_AM , new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + "am.png" ) ) ) );
			mTextures.put( MAP_NAME_PM , new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + "pm.png" ) ) ) );
			mTextures.put( MAP_NAME_SEPERATOR , new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + "seperator.png" ) ) ) );
			mTextures.put( MAP_NAME_BACK_MINUTE_HAND , new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + "back_minute_hand.png" ) ) ) );
			mTextures.put( MAP_NAME_BACK_HOUR_HAND , new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + "back_hour_hand.png" ) ) ) );
			mTextures.put( MAP_NAME_BACK_DOT , new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + "back_dot.png" ) ) ) );
			mTextures.put( MAP_NAME_BACK_SECONDDOT , new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + "seconddot.png" ) ) ) );
			mTextures.put( MAP_NAME_BACK_MINUTE_SHADOW , new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + "minute_shadow.png" ) ) ) );
			mTextures.put( MAP_NAME_BACK_HOUR_SHADOW , new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + "hour_shadow.png" ) ) ) );
			for( int i = 0 ; i < 10 ; i++ )
			{
				BitmapTexture texture = new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + "time_" + i + ".png" ) ) );
				texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
				mTextures.put( "time_" + i , texture );
			}
			for( int i = 1 ; i < 13 ; i++ )
			{
				mTextures.put( "month_" + i , new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + "month_" + i + ".png" ) ) ) );
			}
			for( int i = 0 ; i < 10 ; i++ )
			{
				mTextures.put( MAP_NAME_DATE + "_" + i , new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + "day_" + i + ".png" ) ) ) );
			}
			for( int i = 1 ; i <= 7 ; i++ )
			{
				mTextures.put( "week_" + i , new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + "week_" + i + ".png" ) ) ) );
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	private void addClockMediriemView(
			String name ,
			String objName ,
			String textureName )
	{
		ObjBaseView view = new ObjBaseView( name );
		Mesh mesh = getMesh( objName + ".obj" , meridiemX/*this.width/2-(timeWidth-horizontalGap)*2*/, this.height / 2 + timeHeight / 2 + verticalGap );
		view.setTexture( mTextures.get( textureName ) );
		view.setMesh( mesh );
		view.enableDepthMode( true );
		mViews.put( name , view );
		this.addView( view );
	}
	
	public void addBackHands()
	{
		//        addClockHandView(MAP_NAME_BACK_HOUR_SHADOW,"hour_shadow",MAP_NAME_BACK_HOUR_SHADOW,this.width/2,this.height/2);
		//        addClockHandView(MAP_NAME_BACK_MINUTE_SHADOW,"minute_shadow",MAP_NAME_BACK_MINUTE_SHADOW,this.width/2,this.height/2);
		addClockHandView( MAP_NAME_BACK_HOUR_HAND , "hourhand" , MAP_NAME_BACK_HOUR_HAND , this.width / 2 , this.height / 2 );
		addClockHandView( MAP_NAME_BACK_MINUTE_HAND , "minutehand" , MAP_NAME_BACK_MINUTE_HAND , this.width / 2 , this.height / 2 );
		addClockViewEx( MAP_NAME_BACK_DOT , "dot" , MAP_NAME_BACK_DOT , this.width / 2 , this.height / 2 );
		addClockViewEx( MAP_NAME_BACK_SECONDDOT , "seconddot" , MAP_NAME_BACK_SECONDDOT , this.width / 2 , this.height / 2 );
	}
	
	private void addClockWeekView(
			String name ,
			String objName ,
			String textureName )
	{
		ObjBaseView view = new ObjBaseView( name );
		Mesh mesh = getMesh( objName + ".obj" , this.width / 2 , this.height / 2 );
		float size[] = getMeshSize( mesh );
		mesh.dispose();
		Mesh mesh1 = getMesh( objName + ".obj" , this.width / 2 , this.height / 2 - timeHeight / 2 - monthHeight / 2 - verticalGap * 3 );
		move( mesh1 , 0 , 0 , 1 );
		view.setTexture( mTextures.get( textureName ) );
		view.setMesh( mesh1 );
		view.enableDepthMode( true );
		mViews.put( name , view );
		this.addView( view );
	}
	
	public void addTime()
	{
		addClockTimeView( "time1" , "time1" , "time_0" , 0 , true );
		addClockTimeView( "seperator" , "seperator" , "seperator" , 2 , true );
		addClockTimeView( "time3" , "time3" , "time_0" , 3 , true );
		addClockTimeView( "time4" , "time4" , "time_0" , 4 , true );
		addClockTimeView( "time2" , "time2" , "time_0" , 1 , true );
	}
	
	private void addClockHandView(
			String name ,
			String objName ,
			String textureName ,
			float x ,
			float y )
	{
		Texture texture = null;
		try
		{
			texture = new BitmapTexture( BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( ClockComponent.imgPath + textureName + ".png" ) ) );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		ObjHandView view = new ObjHandView( name );
		view.x = 0;
		view.y = 0;
		view.setSize( this.width , this.height );
		Mesh mesh = getMesh( objName + ".obj" , x , y );
		view.setTexture( mTextures.get( textureName ) );
		view.enableDepthMode( true );
		view.setMesh( mesh );
		view.setOrigin( this.width / 2 , this.height / 2 );
		mViews.put( name , view );
		this.addView( view );
		view.bringToFront();
	}
	
	public Mesh getMesh(
			String objName ,
			float x ,
			float y )
	{
		Mesh mesh = ClockHelper.getMesh( mMeshCache , mAppContext , objName , x , y , 0 , WidgetClock.SCALE_X , WidgetClock.SCALE_Y , WidgetClock.SCALE_Z );
		return mesh;
	}
	
	public void onMeridiemChange()
	{
		mCurrentMeridiem = mCalendar.get( Calendar.AM_PM );
		if( mPresentMeridiem != mCurrentMeridiem )
		{
			ObjBaseView view3 = (ObjBaseView)mViews.get( "meridiem" );
			if( view3 != null )
			{
				if( mCurrentMeridiem == 0 )
					view3.setTexture( mTextures.get( "am" ) );
				else if( mCurrentMeridiem == 1 )
				{
					view3.setTexture( mTextures.get( "pm" ) );
				}
			}
			mPresentMeridiem = mCurrentMeridiem;
		}
	}
	
	public void onMonthChange()
	{
		mCurrentMonth = mCalendar.get( Calendar.MONTH ) + 1;
		// if(mPresentMonth!=mCurrentMonth){
		if( true )
		{
			mCurrentDate = mCalendar.get( Calendar.DATE );
			String hour = String.valueOf( mCurrentDate );
			int cur1 = 0;
			int cur2 = 0;
			if( hour.length() > 1 )
			{
				char[] str = hour.toCharArray();
				cur1 = Integer.parseInt( String.valueOf( str[0] ) );
				cur2 = Integer.parseInt( String.valueOf( str[1] ) );
			}
			else
			{
				cur2 = Integer.parseInt( hour );
			}
			//            
			//             float gap=0;
			//             float posX=this.width/2-(dateLength[0]+dateLength[1]+gap)/2+dateLength[0]/2;;
			if( cur1 != 0 )
			{
				ObjBaseView view = (ObjBaseView)mViews.get( "month" );
				if( view != null )
				{
					view.show();
					view.setTexture( mTextures.get( "month_" + mCurrentMonth ) );
				}
				ObjBaseView view1 = (ObjBaseView)mViews.get( "month1" );
				if( view1 != null )
				{
					view1.hide();
					view1.setTexture( mTextures.get( "month_" + mCurrentMonth ) );
				}
				// addClockMonthView("month","month","month_1",0,posX);
			}
			else
			{
				ObjBaseView view = (ObjBaseView)mViews.get( "month" );
				if( view != null )
				{
					view.hide();
					//view.setTexture(mTextures.get("month_"+mCurrentMonth));
				}
				ObjBaseView view1 = (ObjBaseView)mViews.get( "month1" );
				if( view1 != null )
				{
					view1.show();
					view1.setTexture( mTextures.get( "month_" + mCurrentMonth ) );
				}
				// addClockMonthView("month","month","month_1",1,posX);
			}
			mPresentMonth = mCurrentMonth;
		}
	}
	
	public void onDateChange()
	{
		mCurrentDate = mCalendar.get( Calendar.DATE );
		//  Log.v("Clock", "mPresentDate "+mPresentDate+" mCurrentDate: "+mCurrentDate);
		float gap = 0;
		if( mPresentDate != mCurrentDate )
		{
			String hour = String.valueOf( mCurrentDate );
			int cur1 = 0;
			int cur2 = 0;
			if( hour.length() > 1 )
			{
				char[] str = hour.toCharArray();
				cur1 = Integer.parseInt( String.valueOf( str[0] ) );
				cur2 = Integer.parseInt( String.valueOf( str[1] ) );
			}
			else
			{
				cur2 = Integer.parseInt( hour );
			}
			Log.v( "Clock" , "cur1 " + cur1 + " cur2: " + cur2 );
			if( cur1 == 0 )
			{
				//                  float posX= this.width/2+(dateLength[0]+dateLength[1]+gap)/2.0f-dateLength[1]/2;
				//                  
				//                  
				//                  addClockDateView("date","day","date_1",2,posX);
				//                  ObjBaseView view3= (ObjBaseView)mViews.get("date");
				//                  
				//                  if(view3!=null){
				//                      view3.setTexture(mTextures.get("date_"+cur2));
				//                   }
				//                  
				//                  ObjBaseView view= (ObjBaseView)mViews.get("date1");
				//                  
				//                  if(view!=null){
				//            
				//                     view.hide();
				//                  
				//                  }
				ObjBaseView view = (ObjBaseView)mViews.get( "date" );
				if( view != null )
				{
					view.hide();
					// view.setTexture(mTextures.get("date_"+cur2));
				}
				ObjBaseView view1 = (ObjBaseView)mViews.get( "date1" );
				if( view1 != null )
				{
					view1.hide();
				}
				ObjBaseView view2 = (ObjBaseView)mViews.get( "date2" );
				if( view2 != null )
				{
					view2.setTexture( mTextures.get( "date_" + cur2 ) );
					view2.show();
				}
			}
			else
			{
				//                  float posX= this.width/2+(dateLength[0]+dateLength[1]+gap)/2.0f-dateLength[1]/2;
				//                  addClockDateView("date","day","date_1",0,posX);
				ObjBaseView view = (ObjBaseView)mViews.get( "date" );
				if( view != null )
				{
					view.show();
					view.setTexture( mTextures.get( "date_" + cur1 ) );
				}
				//   addClockDateView("date1","day","date_1",1,posX);
				ObjBaseView view1 = (ObjBaseView)mViews.get( "date1" );
				if( view1 != null )
				{
					view1.setTexture( mTextures.get( "date_" + cur2 ) );
					view1.show();
				}
				ObjBaseView view2 = (ObjBaseView)mViews.get( "date2" );
				if( view2 != null )
				{
					view2.hide();
				}
			}
			mPresentDate = mCurrentDate;
		}
	}
	
	public void hideTimeView()
	{
		mViews.get( "time1" ).hide();
		mViews.get( "time2" ).hide();
		mViews.get( "time3" ).hide();
		mViews.get( "time4" ).hide();
	}
	
	public void showTimeView()
	{
		mViews.get( "time1" ).show();
		mViews.get( "time2" ).show();
		mViews.get( "time3" ).show();
		mViews.get( "time4" ).show();
	}
	
	public void onHourChange()
	{
		mCurrentHour = mCalendar.get( Calendar.HOUR );
		if( mPresentHour != mCurrentHour )
		{
			String hour = String.valueOf( mCurrentHour );
			int cur1 = 0;
			int cur2 = 0;
			if( hour.length() > 1 )
			{
				char[] str = hour.toCharArray();
				cur1 = Integer.parseInt( String.valueOf( str[0] ) );
				cur2 = Integer.parseInt( String.valueOf( str[1] ) );
			}
			else
			{
				cur2 = Integer.parseInt( hour );
				if( cur2 == 0 && mCurrentMeridiem == 1 )
				{
					cur2 = 2;
					cur1 = 1;
				}
			}
			ObjBaseView view = (ObjBaseView)mViews.get( "time2" );
			if( view != null )
			{
				this.removeView( view );
				view.setTexture( mTextures.get( "time_" + cur2 ) );
				this.addView( view );
			}
			ObjBaseView view2 = (ObjBaseView)mViews.get( "time1" );
			if( view2 != null )
			{
				this.removeView( view2 );
				view2.setTexture( mTextures.get( "time_" + cur1 ) );
				this.addView( view2 );
			}
			int currentMinute = mCalendar.get( Calendar.MINUTE );
			ObjBaseView view3 = (ObjHandView)mViews.get( MAP_NAME_BACK_HOUR_HAND );
			float rotation = ( ( ( mCurrentHour ) * 30 ) ) % 360 + 30 * currentMinute / 60;
			Log.v( "Clock" , "currentMinute: " + currentMinute + " hour " + mCurrentHour + " rotation: " + rotation );
			if( view3 != null )
			{
				view3.setRotationAngle( 0 , 0 , rotation );
				mPresentHour = mCurrentHour;
			}
			//             ObjHandView view4= (ObjHandView)mViews.get(MAP_NAME_BACK_HOUR_SHADOW);
			//             if(mCurrentHour<30){
			//
			//                 view4.setRotationAngle(0, 0, rotation+2); 
			//             }
			//             else{
			//
			//                 view4.setRotationAngle(0, 0, rotation-2); 
			//             }
		}
	}
	
	public void onSecondChange()
	{
		int currentSecond = mCalendar.get( Calendar.SECOND );
		if( currentSecond != mPresentSecond )
		{
			ObjBaseView view = (ObjBaseView)mViews.get( MAP_NAME_BACK_SECONDDOT );// MAP_NAME_BACK_MINUTE_HAND);
			float mSecondHandRotation = ( ( ( currentSecond + 1 ) * 6 ) + 180 ) % 360;
			mPresentSecond = currentSecond;
			if( view != null )
			{
				view.setRotationAngle( 0 , 0 , mSecondHandRotation );
			}
		}
	}
	
	public void onMinuteChange()
	{
		mCurrentMinute = mCalendar.get( Calendar.MINUTE );
		//   int currentMinute = mCalendar.get(Calendar.MINUTE);
		if( mCurrentMinute != mPresentMinute )
		{
			String minute = String.valueOf( mCurrentMinute );
			int cur1 = 0;
			int cur2 = 0;
			if( minute.length() > 1 )
			{
				char[] str = minute.toCharArray();
				cur1 = Integer.parseInt( String.valueOf( str[0] ) );
				cur2 = Integer.parseInt( String.valueOf( str[1] ) );
			}
			else
			{
				cur2 = Integer.parseInt( minute );
			}
			ObjBaseView view = (ObjBaseView)mViews.get( "time4" );
			if( view != null )
			{
				view.setTexture( mTextures.get( "time_" + cur2 ) );
			}
			ObjBaseView view2 = (ObjBaseView)mViews.get( "time3" );
			if( view2 != null )
			{
				view2.setTexture( mTextures.get( "time_" + cur1 ) );
			}
			ObjHandView view3 = (ObjHandView)mViews.get( MAP_NAME_BACK_MINUTE_HAND );
			int rotation = ( ( ( mCurrentMinute ) * 6 ) ) % 360;
			mPresentMinute = mCurrentMinute;
			if( view3 != null )
			{
				view3.setRotationAngle( 0 , 0 , rotation );
			}
			// just for making a change in hour rotation
			mPresentHour = -1;
			//            ObjHandView view4 = (ObjHandView) mViews.get(MAP_NAME_BACK_MINUTE_SHADOW);
			//            if (currentMinute < 30) {
			//
			//                view4.setRotationAngle(0, 0, rotation + 2);
			//            }
			//            else {
			//
			//                view4.setRotationAngle(0, 0, rotation - 2);
			//            }
		}
	}
	
	private void addClockDateView(
			String name ,
			String objName ,
			String textureName ,
			int index ,
			float posx )
	{
		//        ObjBaseView view1 =(ObjBaseView)mViews.get(name);
		//        if(view1!=null){
		//          
		//            this.removeView(view1);
		//            view1.dispose();
		//            
		//            mViews.remove(name);
		//            Log.v("Clock", "remove view: "+name);
		//        } 
		ObjBaseView view = new ObjBaseView( name );
		int gap = 0;
		Mesh mesh1 = null;
		Mesh mesh = getMesh( objName + ".obj" , this.width / 2 , this.height / 2 );
		float size[] = getMeshSize( mesh );
		mesh.dispose();
		if( index == 0 )
		{
			mesh1 = getMesh( objName + ".obj" , this.width / 2 + size[0] / 2 + gap , this.height / 2 - timeHeight / 2 - verticalGap * 2 );
		}
		else if( index == 1 )
		{
			mesh1 = getMesh( objName + ".obj" , this.width / 2 + size[0] + gap * 2 , this.height / 2 - timeHeight / 2 - verticalGap * 2 );
			move( mesh1 , 0 , 0 , 1 );
		}
		else if( index == 2 )
		{
			mesh1 = getMesh( objName + ".obj" , posx , this.height / 2 - timeHeight / 2 - verticalGap * 2 );
		}
		// length of each date 
		dateLength[1] = size[0];
		view.setTexture( mTextures.get( textureName ) );
		view.setMesh( mesh1 );
		view.enableDepthMode( true );
		mViews.put( name , view );
		this.addView( view );
	}
	
	private void addClockViewEx(
			String name ,
			String objName ,
			String textureName ,
			float x ,
			float y )
	{
		ObjBaseView view = new ObjBaseView( name );
		view.x = 0;
		view.y = 0;
		view.setSize( this.width , this.height );
		Mesh mesh = getMesh( objName + ".obj" , x , y );
		// move(mesh,0,0,-1);
		view.setTexture( mTextures.get( textureName ) );
		view.setMesh( mesh );
		view.enableDepthMode( true );
		view.setOrigin( this.width / 2 , this.height / 2 );
		mViews.put( name , view );
		this.addView( view );
		view.bringToFront();
	}
	
	private void addClockMonthView(
			String name ,
			String objName ,
			String textureName ,
			int flag ,
			float posX )
	{
		ObjBaseView view1 = (ObjBaseView)mViews.get( name );
		if( view1 != null )
		{
			this.removeView( view1 );
			//   view1.dispose();
			mViews.remove( name );
			Log.v( "Clock" , "remove view: " + name );
		}
		ObjBaseView view = new ObjBaseView( name );
		int gap = (int)( 5 * scaleX );
		Mesh mesh = getMesh( objName + ".obj" , this.width / 2 , this.height / 2 );
		float size[] = getMeshSize( mesh );
		mesh.dispose();
		monthHeight = size[1];
		Mesh mesh1 = null;
		//-size[1]+verticalGap
		if( flag == 0 )
			mesh1 = getMesh( objName + ".obj" , this.width / 2 - size[0] / 2 - gap , this.height / 2 - timeHeight / 2 - verticalGap * 2 );
		else if( flag == 1 )
		{
			mesh1 = getMesh( objName + ".obj" , posX , this.height / 2 - timeHeight / 2 - verticalGap * 2 );
		}
		dateLength = getMeshSize( mesh1 );
		view.setTexture( mTextures.get( textureName ) );
		view.setMesh( mesh1 );
		view.enableDepthMode( true );
		mViews.put( name , view );
		this.addView( view );
	}
	
	public static void move(
			Mesh mesh ,
			float dx ,
			float dy ,
			float dz )
	{
		VertexAttribute posAttr = mesh.getVertexAttribute( Usage.Position );
		int offset = posAttr.offset / 4;
		int numVertices = mesh.getNumVertices();
		int vertexSize = mesh.getVertexSize() / 4;
		float[] vertices = new float[numVertices * vertexSize];
		mesh.getVertices( vertices );
		int idx = offset;
		for( int i = 0 ; i < numVertices ; i++ )
		{
			vertices[idx] += dx;
			vertices[idx + 1] += dy;
			vertices[idx + 2] += dz;
			idx += vertexSize;
		}
		mesh.setVertices( vertices );
	}
	
	public static float[] getMeshSize(
			Mesh mesh )
	{
		VertexAttribute posAttr = mesh.getVertexAttribute( Usage.Position );
		int offset = posAttr.offset / 4;
		int numVertices = mesh.getNumVertices();
		int vertexSize = mesh.getVertexSize() / 4;
		float[] ret = new float[2];
		float[] vertices = new float[numVertices * vertexSize];
		mesh.getVertices( vertices );
		int idx = offset;
		float maxX = vertices[idx];
		float minX = vertices[idx];
		float maxY = vertices[idx + 1];
		float minY = vertices[idx + 1];
		for( int i = 0 ; i < numVertices ; i++ )
		{
			if( vertices[idx] > maxX )
			{
				maxX = vertices[idx];
			}
			if( vertices[idx] < minX )
			{
				minX = vertices[idx];
			}
			if( vertices[idx + 1] > maxY )
			{
				maxY = vertices[idx + 1];
			}
			if( vertices[idx + 1] < minY )
			{
				minY = vertices[idx + 1];
			}
			idx += vertexSize;
		}
		ret[0] = Math.abs( maxX - minX );
		ret[1] = Math.abs( maxY - minY );
		return ret;
	}
	
	private void addClockTimeView(
			String name ,
			String objName ,
			String textureName ,
			int index ,
			boolean enableDepth )
	{
		ObjBaseView view = new ObjBaseView( name );
		Mesh mesh1 = null;
		Mesh mesh = getMesh( objName + ".obj" , x , ( 450 - y ) );
		float distance[] = getMeshSize( mesh );
		timeWidth = distance[0];
		timeHeight = distance[1];
		mesh.dispose();
		if( index == 0 )
		{
			mesh1 = getMesh( objName + ".obj" , this.width / 2 - ( distance[0] + horizontalGap ) * 2 , this.height / 2 );
			meridiemX = this.width / 2 - ( distance[0] + horizontalGap ) * 2;
			if( Utils3D.getScreenWidth() == 480 && Utils3D.getScreenHeight() <= 800 )
				move( mesh1 , -3 , 0 , 1 );
		}
		else if( index == 1 )
		{
			mesh1 = getMesh( objName + ".obj" , this.width / 2 - ( distance[0] + horizontalGap ) , this.height / 2 );
			if( Utils3D.getScreenWidth() == 480 && Utils3D.getScreenHeight() <= 800 )
				move( mesh1 , 0 , 0 , 0.5f );
			else
				move( mesh1 , 3 , 0 , 1 );
		}
		else if( index == 2 )
		{
			mesh1 = getMesh( objName + ".obj" , this.width / 2 , this.height / 2 );
		}
		else if( index == 3 )
		{
			mesh1 = getMesh( objName + ".obj" , this.width / 2 + ( distance[0] + horizontalGap ) , this.height / 2 );
		}
		else if( index == 4 )
		{
			mesh1 = getMesh( objName + ".obj" , this.width / 2 + ( distance[0] + horizontalGap ) * 2 , this.height / 2 );
			move( mesh1 , 3 , 0 , 1 );
		}
		view.setTexture( mTextures.get( textureName ) );
		view.setMesh( mesh1 );
		view.enableDepthMode( enableDepth );
		mViews.put( name , view );
		this.addView( view );
		view.bringToFront();
	}
	
	public void onWeekChange()
	{
		mCurrentWeek = mCalendar.get( Calendar.DAY_OF_WEEK );
		if( mPresentWeek != mCurrentWeek )
		{
			ObjBaseView view3 = (ObjBaseView)mViews.get( "week" );
			if( view3 != null )
			{
				switch( mCurrentWeek )
				{
					case 1:
						view3.setTexture( mTextures.get( "week_" + 7 ) );
						break;
					case 2:
						view3.setTexture( mTextures.get( "week_" + 1 ) );
						break;
					case 3:
						view3.setTexture( mTextures.get( "week_" + 2 ) );
						break;
					case 4:
						view3.setTexture( mTextures.get( "week_" + 3 ) );
						break;
					case 5:
						view3.setTexture( mTextures.get( "week_" + 4 ) );
						break;
					case 6:
						view3.setTexture( mTextures.get( "week_" + 5 ) );
						break;
					case 7:
						view3.setTexture( mTextures.get( "week_" + 6 ) );
						break;
					default:
						view3.setTexture( mTextures.get( "week_" + 1 ) );
						break;
				}
			}
			mPresentWeek = mCurrentWeek;
		}
	}
}
