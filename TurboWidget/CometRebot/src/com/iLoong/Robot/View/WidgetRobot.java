package com.iLoong.Robot.View;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.BatteryManager;
import android.util.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.Robot.RobotHelper;
import com.cooeeui.cometrobot.R;
import com.iLoong.Widget.theme.WidgetThemeManager;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.UI3DEngine.adapter.IRefreshRender;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.CacheManager;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class WidgetRobot extends WidgetPluginView3D
{
	
	public static String THEME_NAME = "male";
	private static final String TAG = "robot";
	// 3D模型缓存
	public static final String MESH_CACHE_NAME = "com.iLoong.Robot.meshCache";
	public static final String TEXTURE_CACHE_NAME = "com.iLoong.Robot.textureCache";
	// 3D模型缓存key
	public static final String ROBOT_BODY = "robot_body";
	public static final String ROBOT_EYE = "robot_eye";
	public static final String ROBOT_LIGHTING = "robot_lighting";
	public static final String BATTERY = "battery";
	public static final String ROBOT_ARM = "robot_arm";
	public static final String BATTERY_OUTER = "battery_outer";
	public static final String ROBOT_CLEAR_BUTTON = "robot_clear_button";
	public static final String ROBOT_CLEAR_BUTTON_RING = "robot_clear_button_ring";
	// 3D模型缩放比例
	public static float SCALE_SIZE = 1F;
	public static float SCALE_X = 1F;
	public static float SCALE_Y = 1F;
	public static float SCALE_Z = 1F;
	public static float MODEL_BACK_SCALE_Z = 0f;//-18.458445f;// 效果为放大效果则调大点
	// 生成压缩模型开关
	public static boolean loadOriginalObj = false;
	public static boolean saveObj = false;
	// 缓存实例
	public static boolean useCache = false;
	// 3D模型统一宽度高度
	public static float MODEL_WIDTH = 320;
	public static float MODEL_HEIGHT = 320;
	// 电源变化接收器
	//	private BatteryReceiver mBatteryReceiver = null;
	// 机器人各个View实例
	//	private BatteryOuterView mBatteryOuterView;
	//	private BatteryInnerView mBatteryInnerView;
	//	private RobotEyeView mRobotEyeView;
	//	private RobotLightingView mRobotLightingView;
	//	private ClearButtonRingView mRobotClearButtonRingView;//azmohan
	//	private RobotArmView mRobotArmView;
	private RobotheadView mRobotHeadView = null;
	private RobotFace mRobotFaceView = null;
	private RobotLight mRobotLightView = null;
	private RobotRightHand mRobotRightHand = null;
	private RobotLeftHand mRobotLeftHand = null;
	private RobotEye mRobotEye = null;
	// 初次获取电池静态电量Intent
	private Intent mBatteryStickyIntent = null;
	private BatteryInfo mBatteryInfo = null;
	// 动画部分
	private float mRotateX;
	private float mRotateY;
	private Tween mScrollTween;
	private boolean mIsFilling = false;
	boolean mIsTouchDownTriggered = false;
	public static String REBOT_MESSAGE = "";
	public static boolean mIsAnimationStopped = true;
	public MainAppContext mAppContext;
	public CooGdx mCooGdx = null;
	private TextureRegion mBackRegion = null;
	// xiatian add start //Widget3D adaptation "Naked eye 3D"
	// 【可配置】Wiidget3D侧，X方向最大偏移角度。
	public static float MAX_SENSOR_ANGLE_X = 30;
	// 【可配置】Wiidget3D侧，Y方向最大偏移角度。
	public static float MAX_SENSOR_ANGLE_Y = 3.001f;
	// 【可配置】Wiidget3D侧，角度转化系数。可将launcher侧发送过来的角度值相应的翻倍或缩小。
	public static float SENSOR_ANGLE_TRANSFER_COEFFICIENT = (float)0.6;
	// 【可配置】Wiidget3D侧，有效数据最小偏移角度。大于该值才启动动画。
	public static int SENSOR_ANGLE_OFFSET_MIN = 3;
	// 【可配置】Wiidget3D侧，动画持续时间。
	public static float SENSOR_TWEEN_DURATION = 0.75f;
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
	// xiatian add end
	private long clearedMemory = 0;
	private Timeline mTimeLine;
	private RobotHeadGroup mHeadGroup;
	
	public WidgetRobot(
			String name ,
			MainAppContext appContext ,
			int widgetId )
	{
		super( name );
		new WidgetThemeManager( appContext.mContainerContext );
		mAppContext = appContext;
		mCooGdx = new CooGdx( appContext.mGdxApplication );
		long start = System.currentTimeMillis();
		// 获取3D模型初始化参数
		MODEL_WIDTH = mAppContext.mWidgetContext.getResources().getDimension( R.dimen.robot_width );
		MODEL_HEIGHT = mAppContext.mWidgetContext.getResources().getDimension( R.dimen.robot_height );
		float density = (float)mAppContext.mWidgetContext.getResources().getDisplayMetrics().density;
		SCALE_X = SCALE_Y = SCALE_Z = SCALE_SIZE = density;
		//		SCALE_X = SCALE_Y = SCALE_Z = SCALE_SIZE = ((float) mAppContext.mWidgetContext
		//				.getResources().getDisplayMetrics().density / (float) 1.5) * ( 2 / 3f);
		Log.v( "cooee" , "SCALE_X is " + SCALE_X );
		this.width = MODEL_WIDTH;
		this.height = MODEL_HEIGHT;
		this.setOrigin( this.width / 2 , this.height / 2 );
		this.setRotationVector( 0 , 1 , 0 );
		// test zhenNan.ye begin
		//		SCALE_X = SCALE_Y = SCALE_Z = SCALE_SIZE = 1f;
		//		this.setBackgroud(new NinePatch(new Texture3D(appContext.gdx,
		//				ImageHelper.bmp2Pixmap(ImageHelper.getImageFromResource(
		//						appContext.mWidgetContext, R.drawable.red)))));
		// test zhenNan.ye end
		mBatteryStickyIntent = mAppContext.mWidgetContext.registerReceiver( null , new IntentFilter( Intent.ACTION_BATTERY_CHANGED ) );
		// 添加机器人主体
		addRobotBody();
		//		addRobotHead();
		//		addRobotFace();
		//		addRobotEye();
		addRobotHeadGroup();//azmohan test
		addRobotLight();
		addRobotRightHand();
		addRobotLeftHand();
		//		addRobotLightingView();// 添加机器人闪电		
		//		addRobotEyeView();// 添加机器人眼睛		
		//		addBattery();// 添加机器人电量显示内部液体模型		
		//		addBatteryOuter();// 添加机器人电量显示部分		
		//		addRobotArm();// 添加机器人胳膊
		//		addClearButtonRing();// 添加一键清理按钮外环
		//mRobotClearButtonRingView.setRobotArmView(mRobotArmView);
		// 注册电源变化接收器
		//		mBatteryReceiver = new BatteryReceiver();
		//		IntentFilter filter = new IntentFilter();
		//		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		//		filter.addAction(Intent.ACTION_BATTERY_LOW);
		//		filter.addAction(Intent.ACTION_BATTERY_OKAY);
		//		mAppContext.mWidgetContext.registerReceiver(mBatteryReceiver, filter);
		long end = System.currentTimeMillis();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis( end - start );
		Log.v( "ClockMinuteView" , "创建机器人总时间: " + c.get( Calendar.MINUTE ) + "分" + c.get( Calendar.SECOND ) + "秒" + c.get( Calendar.MILLISECOND ) + " 毫秒" );
		Utils3D.showPidMemoryInfo( appContext.mContainerContext , "com.iLoong.Robot" );
	}
	
	private void addRobotEye()
	{
		// TODO Auto-generated method stub
		mRobotEye = new RobotEye( mAppContext , "mRobotEye" , "robot_eye.png" , "face.obj" );
		this.addView( mRobotEye );
		mRobotEye.bringToFront();
	}
	
	private void addRobotHeadGroup()
	{
		mHeadGroup = new RobotHeadGroup( "RobotHeadGroup" , mAppContext , this );
		this.addView( mHeadGroup );
		mHeadGroup.bringToFront();
	}
	
	private void addRobotLeftHand()
	{
		// TODO Auto-generated method stub
		mRobotLeftHand = new RobotLeftHand( mAppContext , "mRobotLeftHand" , mBackRegion , "lefthand.obj" );
		this.addView( mRobotLeftHand );
		mRobotLeftHand.bringToFront();
	}
	
	private void addRobotRightHand()
	{
		// TODO Auto-generated method stub
		mRobotRightHand = new RobotRightHand( mAppContext , "rightHand" , mBackRegion , "righthand.obj" );
		this.addView( mRobotRightHand );
		mRobotRightHand.bringToFront();
	}
	
	private void addRobotLight()
	{
		// TODO Auto-generated method stub
		mRobotLightView = new RobotLight( mAppContext , "lightView" , "light.png" , "light.obj" );
		this.addView( mRobotLightView );
		mRobotLightView.bringToFront();
	}
	
	private void addRobotFace()
	{
		// TODO Auto-generated method stub
		mRobotFaceView = new RobotFace( mAppContext , "faceView" , "face.png" , "face.obj" );
		this.addView( mRobotFaceView );
		mRobotFaceView.bringToFront();
	}
	
	private void addRobotHead()
	{
		// TODO Auto-generated method stub
		mRobotHeadView = new RobotheadView( "headView" , mAppContext , mBackRegion );
		this.addView( mRobotHeadView );
		mRobotHeadView.bringToFront();
	}
	
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		mCooGdx.gl.glDepthMask( true );
		mCooGdx.gl.glEnable( GL10.GL_DEPTH_TEST );
		mCooGdx.gl.glClear( GL10.GL_DEPTH_BUFFER_BIT ); // 清除屏幕及深度缓存
		mCooGdx.gl.glDepthFunc( GL10.GL_LEQUAL );
		super.draw( batch , parentAlpha );
		mCooGdx.gl.glDisable( GL10.GL_DEPTH_TEST );
		mCooGdx.gl.glClear( GL10.GL_DEPTH_BUFFER_BIT ); // 清除屏幕及深度缓存
		mCooGdx.gl.glDepthMask( false );
	}
	
	private long getAvailMemory()
	{
		// 获取android当前可用内存大小
		ActivityManager am = (ActivityManager)mAppContext.mWidgetContext.getSystemService( Context.ACTIVITY_SERVICE );
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo( mi );
		// mi.availMem; 当前系统的可用内存
		// return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
		return mi.availMem / ( 1024 * 1024 );
	}
	
	@SuppressWarnings( "unused" )
	private long getTotalMemory()
	{
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		try
		{
			FileReader localFileReader = new FileReader( str1 );
			BufferedReader localBufferedReader = new BufferedReader( localFileReader , 8192 );
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
			arrayOfString = str2.split( "\\s+" );
			for( String num : arrayOfString )
			{
				Log.i( str2 , num + "\t" );
			}
			initial_memory = Integer.valueOf( arrayOfString[1] ).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();
		}
		catch( IOException e )
		{
		}
		// return Formatter.formatFileSize(context, initial_memory);//
		// Byte转换为KB或者MB，内存大小规格化
		return initial_memory / ( 1024 * 1024 );
	}
	
	private int clearedAppCount = 0;
	
	private void kill()
	{
		clearedAppCount = 0;
		ActivityManager activityManger = (ActivityManager)mAppContext.mWidgetContext.getSystemService( Context.ACTIVITY_SERVICE );
		List<ActivityManager.RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();
		if( list != null )
			for( int i = 0 ; i < list.size() ; i++ )
			{
				ActivityManager.RunningAppProcessInfo apinfo = list.get( i );
				// Log.v("清理进程", "pid:" + apinfo.pid + " processName:"
				// + apinfo.processName + "importance:"
				// + apinfo.importance);
				String[] pkgList = apinfo.pkgList;
				if( apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE )
				{
					// Process.killProcess(apinfo.pid);
					for( int j = 0 ; j < pkgList.length ; j++ )
					{
						// 2.2以上是过时的,请用killBackgroundProcesses代替
						Integer sdkVersion = Integer.valueOf( android.os.Build.VERSION.SDK );
						if( sdkVersion > 8 )
						{
							activityManger.killBackgroundProcesses( pkgList[j] );
						}
						else
						{
							activityManger.restartPackage( pkgList[j] );
						}
						// Log.e("kill", pkgList[j]);
						clearedAppCount++;
					}
				}
			}
	}
	
	public void showClearResult()
	{
		if( this.mRobotLeftHand != null )
		{
			String title = mAppContext.mWidgetContext.getResources().getString( R.string.clear_success_msg ).replace( "${count}" , "" + clearedAppCount ).replace( "${memory}" , clearedMemory + "" );
			if( clearedMemory == 0 )
			{
				title = mAppContext.mWidgetContext.getResources().getString( R.string.clear_empty_msg );
			}
			WidgetRobot.REBOT_MESSAGE = title;
			this.mRobotLeftHand.startAinimation( title );
		}
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		Log.v( "Robot" , "WidgetRobot onclick" );
		if( !WidgetRobot.mIsAnimationStopped )
		{
			return true;
		}
		mTimeLine = this.mHeadGroup.getTimeLine();
		// TODO Auto-generated method stub
		if( mTimeLine != null || !this.mRobotLeftHand.isTweenStopped() )
		{
			return false;
		}
		// 全局标志动画开始
		WidgetRobot.mIsAnimationStopped = false;
		this.mHeadGroup.startAnimation();
		mAppContext.mGdxApplication.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				final long beforeClearMemory = getAvailMemory();
				long start = System.currentTimeMillis();
				kill();
				long end = System.currentTimeMillis();
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis( end - start );
				int seconds = c.get( Calendar.SECOND );
				final long afterClearMemory = getAvailMemory();
				clearedMemory = afterClearMemory - beforeClearMemory;
				if( seconds > RobotHeadGroup.mAnimationDuration )
				{
					Log.v( "clear" , "清理时间：" + seconds );
					if( mTimeLine != null )
					{
						mHeadGroup.stopAnimation();
					}
					showClearResult();
				}
			}
		} );
		return true;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		Log.v( TAG , "onTouchDown" );
		mIsTouchDownTriggered = true;
		mIsFilling = false;
		
		ifscroll=false;
		ifworkspacemove=false;
		
		mRotateX = 0;
		mRotateY = 0;
		this.rotation = 0;
		//		this.requestFocus();
		if( mScrollTween != null && !mScrollTween.isFinished() )
		{
			mScrollTween.free();
			mScrollTween = null;
		}
		//azmohan test
		//		if ( mRobotClearButtonRingView != null)
		//		{
		//			if (mRobotClearButtonRingView.isPointInSelf(x, y)) {
		//				return super.onTouchDown(x, y, pointer);
		//			} else {
		//				return true;
		//			}
		//		}
		Gdx.graphics.getDensity();
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		Log.v( TAG , "onTouchUp" );
		mIsTouchDownTriggered = false;
		if( !mIsFilling )
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
			if( mScrollTween != null )
			{
				mScrollTween.free();
			}
			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if( !onIsOpenSensor() )
			{
				mScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( cur ).delay( 0 );
				mScrollTween.start( View3DTweenAccessor.manager );
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
		}
		this.releaseFocus();
		Workspace3D.instance.requestFocus();
		return false;
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		Log.v( TAG , "fling" );
		mIsTouchDownTriggered = false;
		mIsFilling = true;
		float cur = 0;
		if( this.rotation <= 180 )
		{
			cur = 0;
		}
		else
		{
			cur = 360;
		}
		if( mScrollTween != null )
		{
			mScrollTween.free();
		}
		// xiatian add start //Widget3D adaptation "Naked eye 3D"
		if( !onIsOpenSensor() )
		{
			mScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( cur );
			mScrollTween.start( View3DTweenAccessor.manager );
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
			}// xiatian add //fix bug:0001988
		}
		// xiatian add end
		//		this.releaseFocus();
		return true;
	}
	
	private void addRobotBody()
	{
		long start = System.currentTimeMillis();
		if( mBackRegion == null )
		{
			Texture texture = RobotHelper.getThemeTexture( mAppContext , "robot_body.png" );
			texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			mBackRegion = new TextureRegion( texture );
		}
		RobotBodyView view = new RobotBodyView( "robotBodyView" , mAppContext , mBackRegion );
		this.addView( view );
		view.bringToFront();
		long end = System.currentTimeMillis();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis( end - start );
		Log.v( "ClockMinuteView" , "创建机器人身体显示时间: " + c.get( Calendar.MINUTE ) + "分" + c.get( Calendar.SECOND ) + "秒" + c.get( Calendar.MILLISECOND ) + " 毫秒" );
	}
	
	//	private void addBatteryOuter() {
	//		long start = System.currentTimeMillis();
	//		int level = mBatteryStickyIntent.getIntExtra(
	//				BatteryManager.EXTRA_LEVEL, -1);
	//		int scale = mBatteryStickyIntent.getIntExtra(
	//				BatteryManager.EXTRA_SCALE, -1);
	//		int percent = level * 100 / scale;
	//		TextureRegion mRegion = BatteryOuterView.getBatteryCapacityTexture(
	//				mAppContext, percent);
	//		mBatteryOuterView = new BatteryOuterView("batteryOuterView",
	//				mAppContext, mRegion);
	//		mBatteryOuterView.setBatteryPercent(percent);
	//		this.addView(mBatteryOuterView);
	//		mBatteryOuterView.bringToFront();
	//		long end = System.currentTimeMillis();
	//		Calendar c = Calendar.getInstance();
	//		c.setTimeInMillis(end - start);
	//		Log.v("ClockMinuteView", "创建机器人电量显示时间: " + c.get(Calendar.MINUTE) + "分"
	//				+ c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND)
	//				+ " 毫秒");
	//	}
	//	private void addBattery() {
	//		long start = System.currentTimeMillis();
	//		int level = mBatteryStickyIntent.getIntExtra(
	//				BatteryManager.EXTRA_LEVEL, -1);
	//		int scale = mBatteryStickyIntent.getIntExtra(
	//				BatteryManager.EXTRA_SCALE, -1);
	//		int percent = level * 100 / scale;
	//		TextureRegion mRegion = BatteryInnerView.getBatteryCapacityTexture(
	//				mAppContext, percent);
	//
	//		mBatteryInnerView = new BatteryInnerView("batteryView", mAppContext,
	//				mRegion);
	//		mBatteryInnerView.setBatteryPercent(percent);
	//		this.addView(mBatteryInnerView);
	//		mBatteryInnerView.bringToFront();
	//		long end = System.currentTimeMillis();
	//		Calendar c = Calendar.getInstance();
	//		c.setTimeInMillis(end - start);
	//		Log.v("ClockMinuteView", "创建机器人电池时间: " + c.get(Calendar.MINUTE) + "分"
	//				+ c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND)
	//				+ " 毫秒");
	//	}
	//	private void addRobotArm() {
	//		long start = System.currentTimeMillis();
	//		mRobotArmView = new RobotArmView("robotArmView", mAppContext,
	//				mBackRegion);
	//		this.addView(mRobotArmView);
	//		mRobotArmView.bringToFront();
	//		long end = System.currentTimeMillis();
	//		Calendar c = Calendar.getInstance();
	//		c.setTimeInMillis(end - start);
	//		Log.v("ClockMinuteView", "创建机器人胳膊时间: " + c.get(Calendar.MINUTE) + "分"
	//				+ c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND)
	//				+ " 毫秒");
	//	}
	//	private void addRobotEyeView() {
	//		long start = System.currentTimeMillis();
	//		mRobotEyeView = new RobotEyeView("robotEyeView", mAppContext);
	//		mRobotEyeView.setBatteryStickIntent(mBatteryStickyIntent);
	//		mRobotEyeView.setRefreshRender(refreshRender);
	//		this.addView(mRobotEyeView);
	//		mRobotEyeView.bringToFront();
	//		long end = System.currentTimeMillis();
	//		Calendar c = Calendar.getInstance();
	//		c.setTimeInMillis(end - start);
	//		Log.v("ClockMinuteView", "创建机器人眼睛时间: " + c.get(Calendar.MINUTE) + "分"
	//				+ c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND)
	//				+ " 毫秒");
	//	}
	//
	//	private void addRobotLightingView() {
	//		long start = System.currentTimeMillis();
	//		mRobotLightingView = new RobotLightingView("robotLightingView",
	//				mAppContext);
	//		mRobotLightingView.setBatteryStickIntent(mBatteryStickyIntent);
	//		mRobotLightingView.setRefreshRender(refreshRender);
	//		mRobotLightingView.hide();
	//		this.addView(mRobotLightingView);
	//		mRobotLightingView.bringToFront();
	//		long end = System.currentTimeMillis();
	//		Calendar c = Calendar.getInstance();
	//		c.setTimeInMillis(end - start);
	//		Log.v("ClockMinuteView", "创建机器人闪电时间: " + c.get(Calendar.MINUTE) + "分"
	//				+ c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND)
	//				+ " 毫秒");
	//	}
	//
	//azmohan test
	//	private void addClearButtonRing() {
	//		long start = System.currentTimeMillis();
	//		mRobotClearButtonRingView = new ClearButtonRingView(
	//				"clear_button_ring", mAppContext, mBackRegion);
	//		this.addView(mRobotClearButtonRingView);
	//		mRobotClearButtonRingView.bringToFront();
	//		long end = System.currentTimeMillis();
	//		Calendar c = Calendar.getInstance();
	//		c.setTimeInMillis(end - start);
	//		Log.v("ClockMinuteView", "创建机器人清理按钮时间: " + c.get(Calendar.MINUTE) + "分"
	//				+ c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND)
	//				+ " 毫秒");
	//	}
	//	@Override
	//	public boolean onLongClick(float x, float y) {
	//		Log.v(TAG, "onLongClick");
	//		// TODO Auto-generated method stub
	//		this.point.x = x;
	//		this.point.y = y;
	//		this.toAbsolute(point);
	//		Vector2 obj = new Vector2(point.x, point.y);
	//		this.setTag(obj);
	//		this.releaseFocus();
	//
	//		// xiatian add start //Widget3D adaptation "Naked eye 3D"
	//		mIsTouchDownTriggered = false;
	//		// xiatian add end
	//
	//		return viewParent.onCtrlEvent(this, 0);
	//	}
	//	private class BatteryReceiver extends BroadcastReceiver {
	//		@Override
	//		public void onReceive(Context context, Intent intent) {
	//			if (refreshRender != null) {
	//				refreshRender.RefreshRender();
	//			}
	//			if (mBatteryInfo == null) {
	//				mBatteryInfo = new BatteryInfo();
	//			}
	//			// TODO Auto-generated method stub
	//			String action = intent.getAction();
	//			//Log.v(TAG, "action:" + action);
	//			// Toast.makeText(mMainAppContext.mContainerContext, "OnResume",
	//			// Toast.LENGTH_LONG).show();
	//			/*
	//			 * 如果捕捉到的action是ACTION_BATTERY_CHANGED， 就运行onBatteryInfoReceiver()
	//			 */
	//			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
	//				// 目前电量
	//				mBatteryInfo.mBatteryLevel = intent.getIntExtra("level", 0);
	//				// 电池电压
	//				// mBatteryInfo.mBatteryVoltage = intent.getIntExtra("voltage",
	//				// 0);
	//				// 电池温度
	//				// mBatteryInfo.mBatteryTemperature = intent.getIntExtra(
	//				// "temperature", 0);
	//				// 电池目前充电方式
	//				mBatteryInfo.mBatteryPlugged = intent.getIntExtra("plugged", 0);
	//
	//				// 电池电量总量
	//				mBatteryInfo.mBatteryLevelTotal = intent.getExtras().getInt(
	//						"scale");
	//
	//				// 电池目前状态
	//				// mBatteryInfo.mBatteryStatus = intent.getIntExtra("status",
	//				// BatteryManager.BATTERY_STATUS_UNKNOWN);
	//
	//				// 电池健康状态
	//				// mBatteryInfo.mBatteryHealth = intent.getIntExtra("health",
	//				// BatteryManager.BATTERY_HEALTH_UNKNOWN);
	//
	//				onBatteryInfoReceiver();
	//			}
	//		}
	//	}
	boolean ifworkspacemove = false;
	boolean ifscroll = false;
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		Log.v( TAG , "scroll" );
		if( mIsTouchDownTriggered == true )
		{
			mRotateX += deltaX;
			mRotateY += deltaY;
			//			setRotationAngle(mRotateY / height * 360, mRotateX / width * 360, 0);
			if( !ifscroll )
			{
				if( ifworkspacemove )
				{
					return true;
				}
				if(Math.abs( mRotateX )>Utils3D.getScreenWidth())
				{
					ifworkspacemove = true;
					ifscroll = false;
					return true;
				}
				else
				{
					Workspace3D.instance.releaseFocus();
					this.requestFocus();
					ifworkspacemove = false;
					ifscroll = true;
				}
			}
			setRotationAngle( 0 , mRotateX / width * 360 , 0 );//azmohan test
		}
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
			// Log.v("xiatian","yAngleLastScrollTween:" + yAngleLastScrollTween
			// + "xAngleLastScrollTween" + xAngleLastScrollTween);
		}
		// xiatian add end
		return true;
	}
	
	//	private void onBatteryInfoReceiver() {
	//		// 电量数字显示
	//		//Log.v(TAG, "onBatteryInfoReceiver");
	//		// 更新电量显示
	//		if (mBatteryOuterView != null) {
	//			mBatteryOuterView.showBatteryCapacity(mBatteryInfo.getPercent());
	//		}
	//		if (mBatteryInnerView != null) {
	//			mBatteryInnerView.showBatteryCapacity(mBatteryInfo.getPercent());
	//		}
	//		if (mBatteryInfo.mBatteryPlugged == BatteryManager.BATTERY_PLUGGED_AC
	//				|| mBatteryInfo.mBatteryPlugged == BatteryManager.BATTERY_PLUGGED_USB) {
	//			if (mBatteryInfo.getPercent() == 100) {
	//				if (mRobotLightingView != null) {
	//					mRobotLightingView.stop();
	//				}
	//				if (mRobotEyeView != null) {
	//					mRobotEyeView.start();
	//				}
	//			} else {
	//				if (mRobotLightingView != null) {
	//					mRobotLightingView.start();
	//				}
	//				if (mRobotEyeView != null) {
	//					mRobotEyeView.stop();
	//				}
	//			}
	//		} else {
	//			if (mRobotLightingView != null) {
	//				mRobotLightingView.stop();
	//			}
	//			if (mRobotEyeView != null) {
	//				mRobotEyeView.start();
	//			}
	//		}
	//	}
	/**
	 * 电池基本信息
	 * 
	 * @author Administrator
	 * 
	 */
	public class BatteryInfo
	{
		
		// 表示是否提供电池。有些手机在使用USB电源的情况下，即使拔出了电池，仍然可以正常工作
		// private boolean mBatteryPresent = true;
		// 表示电池使用的技术。比如，对于锂电池是Li-ion
		// private String mBatteryTechnology;
		// 电池电量
		private int mBatteryLevel;
		// 电池电量的最大值
		private int mBatteryLevelTotal = 100;
		// 电池电压
		// private int mBatteryVoltage;
		// 电池温度
		// private int mBatteryTemperature;
		// 电池充电方式，0 表示电源是电池
		private int mBatteryPlugged = 0;
		
		// 电池状态
		// private int mBatteryStatus = BatteryManager.BATTERY_STATUS_UNKNOWN;
		// 电池健康状态
		// private int mBatteryHealth;
		private int getPercent()
		{
			return mBatteryLevel * 100 / mBatteryLevelTotal;
		}
	}
	
	//	public void setRefreshRender(IRefreshRender refreshRender) {
	//		this.refreshRender = refreshRender;
	//		if (mRobotEyeView != null) {
	//			mRobotEyeView.setRefreshRender(refreshRender);
	//		}
	//		if (mRobotLightingView != null) {
	//			mRobotLightingView.setRefreshRender(refreshRender);
	//		}
	//	}
	@Override
	public void onDelete()
	{
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		//		Log.e(TAG, "deleteWidget");
		//		if (mBatteryReceiver != null) {
		//			try {
		//				mAppContext.mWidgetContext.unregisterReceiver(mBatteryReceiver);
		//				mBatteryReceiver = null;
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//		}
		//		if (mRobotLightingView != null) {
		//			mRobotLightingView.stop();
		//		}
		//		if (mRobotEyeView != null) {
		//			mRobotEyeView.stop();
		//		}
		dispose();
	}
	
	//	@Override
	//	public void onPause() {
	//		Log.v(TAG, "onPause");
	//		
	//		if ( mRobotEyeView != null && mRobotLightingView != null)
	//		{
	//			mRobotEyeView.pause();
	//			mRobotLightingView.pause();
	//		}
	//		
	////		if (mBatteryReceiver != null) {
	////			try {
	////				mAppContext.mWidgetContext.unregisterReceiver(mBatteryReceiver);
	////				mBatteryReceiver = null;
	////			} catch (Exception e) {
	////				e.printStackTrace();
	////			}
	////		}
	//	}
	//
	//	@Override
	//	public void onResume() {
	//		// Toast.makeText(mMainAppContext.mContainerContext, "OnResume",
	//		// Toast.LENGTH_LONG).show();
	//		Log.v(TAG, "onResume");
	//		if ( mRobotEyeView != null && mRobotLightingView != null)
	//		{
	//			mRobotEyeView.resume();
	//			mRobotLightingView.resume();
	//		}
	//		
	////		IntentFilter filter = new IntentFilter();
	////		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
	////		filter.addAction(Intent.ACTION_BATTERY_LOW);
	////		filter.addAction(Intent.ACTION_BATTERY_OKAY);
	////		if (mBatteryReceiver == null) {
	////			try {
	////				mBatteryReceiver = new BatteryReceiver();
	////				mAppContext.mWidgetContext.registerReceiver(mBatteryReceiver,
	////						filter);
	////			} catch (Exception e) {
	////				e.printStackTrace();
	////			}
	////		}
	////		if ( mBatteryOuterView != null)
	////		{
	////			mBatteryOuterView.refreshBatteryOuter(mBatteryStickyIntent);
	////			mBatteryInnerView.refreshBatteryInner(mBatteryStickyIntent);
	////		}
	//		
	//	}
	//
	//	@Override
	//	public void onStop() {
	//		Log.v(TAG, "onStop");
	////		if (mBatteryReceiver != null) {
	////			try {
	////				mAppContext.mWidgetContext.unregisterReceiver(mBatteryReceiver);
	////				mBatteryReceiver = null;
	////			} catch (Exception e) {
	////				e.printStackTrace();
	////			}
	////		}
	//		if ( mRobotEyeView != null && mRobotLightingView != null)
	//		{
	//			mRobotEyeView.stop();
	//			mRobotLightingView.stop();
	//		}
	//		
	//	}
	//
	//	@Override
	//	public void onDestroy() {
	//		// TODO Auto-generated method stub
	//		// TODO Auto-generated method stub
	//		Log.e(TAG, "deleteWidget");
	////		if (mBatteryReceiver != null) {
	////			try {
	////				mAppContext.mWidgetContext.unregisterReceiver(mBatteryReceiver);
	////				mBatteryReceiver = null;
	////			} catch (Exception e) {
	////				e.printStackTrace();
	////			}
	////		}
	//		if (mRobotLightingView != null) {
	//			mRobotLightingView.stop();
	//		}
	//		if (mRobotEyeView != null) {
	//			mRobotEyeView.stop();
	//		}
	//		dispose();
	//	}
	//
	public void dispose()
	{
		super.dispose();
		Log.e( TAG , "**************************" );
		for( int i = 0 ; i < this.getChildCount() ; i++ )
		{
			this.getChildAt( i ).dispose();
		}
		if( mBackRegion != null )
		{
			mBackRegion.getTexture().dispose();
		}
		//		mBatteryReceiver = null;
		//		mBatteryOuterView = null;
		//		mBatteryInnerView = null;
		//		mRobotEyeView = null;
		//		mRobotLightingView = null;
		//		mRobotClearButtonRingView = null;
		//		mRobotArmView = null;
		// 初次获取电池静态电量Intent
		mBatteryStickyIntent = null;
		mBatteryInfo = null;
		mScrollTween = null;
		mCooGdx = null;
		Utils3D.showPidMemoryInfo( mAppContext.mContainerContext , "com.iLoong.Robot" );
		mAppContext = null;
	}
	
	//	public void onUninstall() {
	//		Log.e(TAG, "onUninstall");
	////		if (mBatteryReceiver != null) {
	////			try {
	////				mAppContext.mWidgetContext.unregisterReceiver(mBatteryReceiver);
	////				mBatteryReceiver = null;
	////			} catch (Exception e) {
	////				e.printStackTrace();
	////			}
	////		}
	//		if (mRobotLightingView != null) {
	//			mRobotLightingView.stop();
	//		}
	//		if (mRobotEyeView != null) {
	//			mRobotEyeView.stop();
	//		}
	//		dispose();
	//	}
	// xiatian add start //Widget3D adaptation "Naked eye 3D"
	@Override
	public void onEvent(
			int type ,
			@SuppressWarnings( "rawtypes" ) BaseTween source )
	{
		if( source.equals( mScrollTween ) && type == TweenCallback.COMPLETE && onIsOpenSensor() )
		{
			mIsScrollTween = false;
			mIsInitScrollTween = false;
			xAngleLastScrollTween = 0;
			yAngleLastScrollTween = 0;
		}
		else if( source.equals( mSensorTween ) && type == TweenCallback.COMPLETE && onIsOpenSensor() )
		{
			mSensorTween = null;
		}
	}
	
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
				mIsTouchDownTriggered = false;
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
			if( mIsTouchDownTriggered )
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
	// xiatian add end
}
