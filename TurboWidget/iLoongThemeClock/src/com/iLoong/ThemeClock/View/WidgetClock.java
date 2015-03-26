package com.iLoong.ThemeClock.View;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.ThemeClock.Common.ClockHelper;
import com.iLoong.ThemeClock.Timer.ClockTimer;
import com.iLoong.ThemeClock.Timer.ClockTimerHandler;
import com.iLoong.ThemeClock.Timer.ClockTimerListener;
import com.iLoong.ThemeClock.Timer.ClockTimerReceiver;
import com.iLoong.ThemeClock.Timer.ClockTimerService;
import com.iLoong.Widget3D.BaseView.PluginViewGroup3D;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class WidgetClock extends WidgetPluginView3D implements ClockTimerListener , TweenCallback
{
	
	public MainAppContext mAppContext;
	public Context mContext;
	public String THEME_NAME;
	public CooGdx cooGdx;
	public final static String TAG = "WidgetClock";
	public static int CLOCK_MODE_TYPE = 0; //true --> 3D  ,  false --> 2D
	public static float MODEL_WIDTH = 330;
	public static float MODEL_HEIGHT = 330;
	public static float MODEL_SCALE = 1F;
	public static ArrayList<String> hourObjList = null;
	public static ArrayList<String> minuteObjList = null;
	public static ArrayList<String> secondObjList = null;
	public static ArrayList<String> dotObjList = null;
	public PluginViewGroup3D hourGroupView = null;
	public PluginViewGroup3D minuteGroupView = null;
	public PluginViewGroup3D secondGroupView = null;
	public PluginViewGroup3D dotGroupView = null;
	public ClockMemberView watchBackView = null;
	//2D模型widget view
	public Texture hourGroupTexture;
	public Texture minuteGroupTexture;
	public Texture secondGroupTexture;
	public Texture dotGroupTexture;
	public Texture watchBackTexture;
	public ClockMember2DView hourGroup2DView = null;
	public ClockMember2DView minuteGroup2DView = null;
	public ClockMember2DView secondGroup2DView = null;
	public ClockMember2DView dotGroup2DView = null;
	public ClockMember2DView watchBack2DView = null;
	public CommonSimulationClock clock2DSimulationView = null;
	public CommonDigitalClock clock2DDigitalView = null;
	// 定时器类型
	private TimerTypeEnum mTimer = TimerTypeEnum.timerTask;
	// 时钟定时器
	private ClockTimer mClockTimer = null;
	private int mHeadHour;
	private int mHeadMinute;
	private int mHeadSecond;
	private float mRotateX = 0;
	private float mRotateY = 0;
	private boolean isfilling;
	
	public WidgetClock(
			String name ,
			MainAppContext context ,
			int widgetId )
	{
		super( name );
		new WidgetThemeManager( context );
		mAppContext = context;
		mContext = context.mWidgetContext;
		THEME_NAME = context.mThemeName;
		cooGdx = new CooGdx( context.mGdxApplication );
		startClockTimer();
		initConfig();
		Log.v( TAG , "WidgetClock creating this Width: " + this.width + ", this height: " + this.height );
		//initView();
		LoadViewByModeType();
		Log.v( TAG , "WidgetClock creating this x: " + this.originX + ", this y: " + this.originY );
	}
	
	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		this.startClockTimer();
		super.onResume();
		/*数字时钟时候12小时制与24小时制切刷新时间*/
		if( CLOCK_MODE_TYPE == 2 && clock2DDigitalView != null )
		{
			mAppContext.mGdxApplication.postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					clock2DDigitalView.ReloadClockView();
				}
			} );
		}
	}
	
	public void addWatchView()
	{
		watchBackView = new ClockMemberView( mAppContext , "watch_back" , "watch_back.png" , "watch_back.obj" );
		watchBackView.bringToFront();
		Log.v( TAG , "watchBackView x: " + watchBackView.x + ", watchBackView y: " + watchBackView.y );
		this.addView( watchBackView );
	}
	
	public void addHourView()
	{
		if( hourObjList == null )
		{
			Log.d( TAG , "hourObjList = " + hourObjList );
			return;
		}
		else
		{
			hourGroupView = new PluginViewGroup3D( mAppContext , "clock_hour" );
			hourGroupView.setSize( MODEL_WIDTH , MODEL_HEIGHT );
			hourGroupView.setOrigin( MODEL_WIDTH / 2 , MODEL_HEIGHT / 2 );
			for( int i = 0 ; i < hourObjList.size() ; i++ )
			{
				ClockMemberView memberView = new ClockMemberView( mAppContext , "clock_member_hour" + i , "watch_back.png" , hourObjList.get( i ) );
				hourGroupView.addView( memberView );
			}
			hourGroupView.bringToFront();
			this.addView( hourGroupView );
		}
	}
	
	public void addMinuteView()
	{
		if( minuteObjList == null )
		{
			Log.d( TAG , "minuteObjList = " + minuteObjList );
			return;
		}
		else
		{
			minuteGroupView = new PluginViewGroup3D( mAppContext , "clock_minute" );
			minuteGroupView.setSize( MODEL_WIDTH , MODEL_HEIGHT );
			minuteGroupView.setOrigin( MODEL_WIDTH / 2 , MODEL_HEIGHT / 2 );
			for( int i = 0 ; i < minuteObjList.size() ; i++ )
			{
				ClockMemberView memberView = new ClockMemberView( mAppContext , "clock_member_minute" + i , "watch_back.png" , minuteObjList.get( i ) );
				minuteGroupView.addView( memberView );
			}
			minuteGroupView.bringToFront();
			this.addView( minuteGroupView );
		}
	}
	
	public void addSecondView()
	{
		if( secondObjList == null )
		{
			Log.d( TAG , "secondObjList = " + secondObjList );
			return;
		}
		else
		{
			secondGroupView = new PluginViewGroup3D( mAppContext , "clock_second" );
			secondGroupView.setSize( MODEL_WIDTH , MODEL_HEIGHT );
			secondGroupView.setOrigin( MODEL_WIDTH / 2 , MODEL_HEIGHT / 2 );
			for( int i = 0 ; i < secondObjList.size() ; i++ )
			{
				ClockMemberView memberView = new ClockMemberView( mAppContext , "clock_member_second" + i , "watch_back.png" , secondObjList.get( i ) );
				secondGroupView.addView( memberView );
			}
			secondGroupView.bringToFront();
			this.addView( secondGroupView );
		}
	}
	
	public void addDotView()
	{
		if( dotObjList == null )
		{
			Log.d( TAG , "dotObjList = " + dotObjList );
			return;
		}
		else
		{
			dotGroupView = new PluginViewGroup3D( mAppContext , "clock_dot" );
			dotGroupView.setSize( MODEL_WIDTH , MODEL_HEIGHT );
			dotGroupView.setOrigin( MODEL_WIDTH / 2 , MODEL_HEIGHT / 2 );
			for( int i = 0 ; i < dotObjList.size() ; i++ )
			{
				ClockMemberView memberView = new ClockMemberView( mAppContext , "clock_member_dot" + i , "watch_back.png" , dotObjList.get( i ) );
				dotGroupView.addView( memberView );
			}
			dotGroupView.bringToFront();
			this.addView( dotGroupView );
		}
	}
	
	public void initConfig()
	{
		Log.d( TAG , " initConfig  " );
		this.scaleX = this.scaleY = this.scaleZ = MODEL_SCALE = WidgetThemeManager.getInstance().getDimension( "model_scale" );
		this.width = MODEL_WIDTH = WidgetThemeManager.getInstance().getFloat( "clock_width" );//*this.scaleX;
		this.height = MODEL_HEIGHT = WidgetThemeManager.getInstance().getFloat( "clock_height" );//*this.scaleY;
		CLOCK_MODE_TYPE = WidgetThemeManager.getInstance().getInteger( "clock_mode_type" );
		Log.d( TAG , "xy == " + this.x + "/" + this.y );
		Log.d( TAG , "initConfig CLOCK_MODE_TYPE == " + CLOCK_MODE_TYPE );
		this.setSize( MODEL_WIDTH , MODEL_HEIGHT );
		this.setOrigin( MODEL_WIDTH / 2 , MODEL_HEIGHT / 2 );
	}
	
	public void Load3DObjMode()
	{
		hourObjList = WidgetThemeManager.getInstance().getStringArray( "watch_hour_obj" );
		minuteObjList = WidgetThemeManager.getInstance().getStringArray( "watch_minute_obj" );
		secondObjList = WidgetThemeManager.getInstance().getStringArray( "watch_second_obj" );
		dotObjList = WidgetThemeManager.getInstance().getStringArray( "watch_dot_obj" );
	}
	
	/*
	 * 根据不同的主题类型加载不同的模型的Widget
	 */
	public void LoadViewByModeType()
	{
		Log.d( TAG , "LoadViewByModeType CLOCK_MODE_TYPE = " + CLOCK_MODE_TYPE );
		if( CLOCK_MODE_TYPE == 0 )
		{
			Load3DObjMode();
			initView();
		}
		else if( CLOCK_MODE_TYPE == 1 )
		{
			clock2DSimulationView = new CommonSimulationClock( "2dclock" , this.mAppContext );
			this.addView( clock2DSimulationView );
		}
		else if( CLOCK_MODE_TYPE == 2 )
		{
			clock2DDigitalView = new CommonDigitalClock( "digitalclock" , this.mAppContext );
			this.addView( clock2DDigitalView );
		}
	}
	
	public void initView()
	{
		Log.v( TAG , "widgetClock initView!!!" );
		addWatchView();
		addHourView();
		addMinuteView();
		addSecondView();
		addDotView();
	}
	
	public void startClockTimer()
	{
		clockTimeChanged();
		if( mTimer == TimerTypeEnum.service )
		{
			//定时器类型为后台Service
			Intent intent = new Intent( mContext , ClockTimerService.class );
			mContext.startService( intent );
			ClockTimerReceiver receiver = new ClockTimerReceiver( this );
			IntentFilter localIntentFilter = new IntentFilter();
			localIntentFilter.addAction( "com.iLoong.widget.clock.change" );
			mContext.registerReceiver( receiver , localIntentFilter );
		}
		else if( mTimer == TimerTypeEnum.timerTask )
		{
			mClockTimer = new ClockTimer( this , 1000 );
			mClockTimer.start();
		}
		else if( mTimer == TimerTypeEnum.handler )
		{
			ClockTimerHandler thread = new ClockTimerHandler( null , this );
			thread.start();
		}
	}
	
	public void stopClockTimer()
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
		if( CLOCK_MODE_TYPE == 0 )
		{
			isfilling = false;
			mRotateX += deltaX;
			mRotateY += deltaY;
			setRotationAngle( mRotateY / height * 360 , mRotateX / width * 360 , 0 );
			return true;
		}
		else
		{
			return false;
		}
	}
	
	Tween scrollTween = null;
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		// TODO Auto-generated method stub
		if( CLOCK_MODE_TYPE == 0 )
		{
			if( !isfilling )
			{
				isfilling = true;
				if( scrollTween != null )
				{
					scrollTween.free();
					scrollTween = null;
				}
				scrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , 0.5f ).ease( Linear.INOUT ).target( 0 , 0 , 0 );
				scrollTween.start( View3DTweenAccessor.manager ).setCallback( this );
			}
		}
		return super.fling( velocityX , velocityY );
	}
	
	@SuppressWarnings( "rawtypes" )
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// TODO Auto-generated method stub
		if( type == TweenCallback.COMPLETE && source.equals( scrollTween ) )
		{
			scrollTween.free();
			scrollTween = null;
		}
	}
	
	@Override
	public void clockTimeChanged()
	{
		// TODO Auto-generated method stub
		//		Log.v(TAG, "clockTimeChanged !!!!");
		Calendar mCalendar = Calendar.getInstance();
		long milliseconds = System.currentTimeMillis();
		mCalendar.setTimeInMillis( milliseconds );
		mHeadHour = mCalendar.get( Calendar.HOUR );
		mHeadMinute = mCalendar.get( Calendar.MINUTE );
		mHeadSecond = mCalendar.get( Calendar.SECOND );
		final float secondRotation = ClockHelper.getSecondHandRotation( mHeadSecond );
		final float minuteRotation = ClockHelper.getMinuteHandRotation( mHeadMinute );
		final float hourRotation = ClockHelper.getHourHandRotation( mHeadMinute , mHeadHour );
		mAppContext.mGdxApplication.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				if( CLOCK_MODE_TYPE == 0 )
				{
					if( secondGroupView != null )
					{
						secondGroupView.rotation = secondRotation - 90;
					}
					if( minuteGroupView != null )
					{
						minuteGroupView.rotation = minuteRotation - 90;
					}
					if( hourGroupView != null )
					{
						hourGroupView.rotation = hourRotation - 90;
					}
				}
				else if( CLOCK_MODE_TYPE == 1 )
				{
					if( clock2DSimulationView != null )
					{
						clock2DSimulationView.ClockTimeChanged( hourRotation , minuteRotation , secondRotation );
					}
				}
				else if( CLOCK_MODE_TYPE == 2 )
				{
					if( clock2DDigitalView != null )
					{
						clock2DDigitalView.ClockTimeUpdate( mHeadHour , mHeadMinute );
					}
				}
			}
		} );
	}
	
	private HashMap<String , Object> item = new HashMap<String , Object>();
	private List<String> pagList = new ArrayList<String>();
	
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
	
	private void listPackages()
	{
		/*
		 * false = no system
		 * packages
		 */
		ArrayList<PInfo> apps = getInstalledApps( false );
		final int max = apps.size();
		for( int i = 0 ; i < max ; i++ )
		{
			apps.get( i ).prettyPrint();
			item = new HashMap<String , Object>();
			int aa = apps.get( i ).pname.length();
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
		}
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
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( isPointInClock( x , y ) )
		{
			try
			{
//				String packageName = null;
//				SharedPreferences p = mAppContext.mContainerContext.getSharedPreferences( "iLoong.Theme.Clock" , 0 );
//				packageName = p.getString( "clock_package" , null );
//				if( packageName == null )
//				{
//					listPackages();
//					Editor editor = p.edit();
//					if( pagList.size() != 0 )
//					{
//						packageName = pagList.get( 0 );
//						editor.putString( "clock_package" , packageName );
//					}
//					editor.commit();
//				}
//				PackageManager pm = mContext.getPackageManager();
//				if( packageName != null )
//				{
//					Intent intent = pm.getLaunchIntentForPackage( packageName );
//					if( intent != null )
//					{
//						intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//						mContext.startActivity( intent );
//					}
//					else
//					{
//						Intent i2 = new Intent( Settings.ACTION_DATE_SETTINGS );
//						i2.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//						mContext.startActivity( i2 );
//					}
//				}
//				else
//				{
//					Intent i2 = new Intent( Settings.ACTION_DATE_SETTINGS );
//					mContext.startActivity( i2 );
//				}
				
				Intent i2 = new Intent( Settings.ACTION_DATE_SETTINGS );
				i2.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				mContext.startActivity( i2 );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
			this.releaseFocus();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		Log.v( TAG , "WidgetClock onTouchDown !!" );
		mRotateX = 0;
		mRotateY = 0;
		if( CLOCK_MODE_TYPE == 0 )
		{
			this.requestFocus();
		}
		else
		{
			this.releaseFocus();
		}
		return false;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		Log.v( TAG , "WidgetClock onTouchUp!!!" );
		{
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
				if( scrollTween != null )
				{
					scrollTween.free();
					scrollTween = null;
				}
				scrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( cur ).delay( 0 );
				scrollTween.start( View3DTweenAccessor.manager );
			}
			this.releaseFocus();
			return false;
		}
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		this.setOrigin( width / 2 , height / 2 );
		this.point.x = x;
		this.point.y = y;
		this.toAbsolute( point );
		Vector2 obj = new Vector2( point.x , point.y );
		this.setTag( obj );
		this.releaseFocus();
		return viewParent.onCtrlEvent( this , 0 );
	}
	
	//	@Override
	//	public void onThemeChanged()
	//	{
	//		// TODO Auto-generated method stub
	//		Log.d( TAG , "onThemeChanged" );
	//		removeAllView();
	//		new WidgetThemeManager( mAppContext );
	//		initConfig();
	//		LoadViewByModeType();
	//		super.onThemeChanged();
	//	}
	public void removeAllView()
	{
		if( CLOCK_MODE_TYPE == 0 )
		{
			if( hourGroupView != null )
			{
				for( int i = 0 ; i < hourGroupView.getActors().size() ; i++ )
				{
					hourGroupView.getActors().get( i ).dispose();
				}
				hourGroupView = null;
			}
			if( minuteGroupView != null )
			{
				for( int i = 0 ; i < minuteGroupView.getActors().size() ; i++ )
				{
					minuteGroupView.getActors().get( i ).dispose();
				}
				minuteGroupView = null;
			}
			if( secondGroupView != null )
			{
				for( int i = 0 ; i < secondGroupView.getActors().size() ; i++ )
				{
					secondGroupView.getActors().get( i ).dispose();
				}
				secondGroupView = null;
			}
			if( dotGroupView != null )
			{
				for( int i = 0 ; i < dotGroupView.getActors().size() ; i++ )
				{
					dotGroupView.getActors().get( i ).dispose();
				}
				dotGroupView = null;
			}
		}
		else if( CLOCK_MODE_TYPE == 1 )
		{
			if( clock2DSimulationView != null )
			{
				clock2DSimulationView.dispose();
				clock2DSimulationView = null;
			}
		}
		else if( CLOCK_MODE_TYPE == 2 )
		{
			if( clock2DDigitalView != null )
			{
				clock2DDigitalView.dispose();
				clock2DDigitalView = null;
			}
		}
		this.removeAllViews();
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
		removeAllView();
	}
}
