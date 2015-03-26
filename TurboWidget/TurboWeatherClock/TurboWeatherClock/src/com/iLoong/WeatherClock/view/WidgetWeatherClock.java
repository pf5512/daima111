package com.iLoong.WeatherClock.view;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.WeatherClock.R;
import com.iLoong.WeatherClock.Theme.WidgetThemeManager;
import com.iLoong.WeatherClock.Timer.ClockTimer;
import com.iLoong.WeatherClock.Timer.ClockTimerHandler;
import com.iLoong.WeatherClock.Timer.ClockTimerListener;
import com.iLoong.WeatherClock.Timer.ClockTimerReceiver;
import com.iLoong.WeatherClock.Timer.ClockTimerService;
import com.iLoong.WeatherClock.Timer.TweenTimer;
import com.iLoong.WeatherClock.Timer.TweenTimerListener;
import com.iLoong.WeatherClock.common.ClockHelper;
import com.iLoong.WeatherClock.common.Parameter;
import com.iLoong.WeatherClock.common.Weather;
import com.iLoong.WeatherClock.common.WeatherClockHelper;
import com.iLoong.WeatherClock.common.WeatherInfo;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.newspage.DownloadDialog;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.thirdParty.analytics.umeng.UmengMobclickAgent;
import com.umeng.analytics.MobclickAgent;


public class WidgetWeatherClock extends WidgetPluginView3D implements ClockTimerListener , TweenTimerListener
{
	
	// 3D模型统一宽度高度
	public static float MODEL_WIDTH = 320;
	public static float MODEL_HEIGHT = 320;
	public static CooGdx cooGdx;
	public MainAppContext mAppContext;
	//	private MainAppContext maincontext = null;
	private Context mContext = null;
	private Timeline animation_click = null;
	private Timeline animation_click_front = null;
	private Timeline animation_click_back = null;
	private Timeline animation_click_shadowup = null;
	private Timeline animation_click_shadowdown = null;
	private Timeline animation_return = null;
	private Timeline animation_return_front = null;
	private Timeline animation_return_back = null;
	private Timeline animation_return_shadowdown = null;
	private Timeline animation_return_shadowup = null;
	private Timeline animation_flash = null;
	Tween scrollTween = null;
	// 定时器类型
	private TimerTypeEnum mTimer = TimerTypeEnum.timerTask;
	// 时钟定时器
	private ClockTimer mClockTimer = null;
	private TweenTimer mTweenTimer = null;
	private int mHeadHour;
	private int mHeadMinute;
	private int mHeadSecond;
	private WeatherViewGroup clockviewgroup = null;
	private ClockViewGroup clockhourview = null;
	private ClockViewGroup clockminuteview = null;
	private ClockViewGroup clocksecondview = null;
	private WeatherViewGroup clockshadow = null;
	private WeatherViewGroup weathershadow = null;
	private WeatherViewGroup weatherviewgroup = null;
	private WeatherViewGroup weathersemicirclegroup = null;
	private WeatherViewGroup upshadowgroup = null;
	private WeatherViewGroup downshadowgroup = null;
	private WeatherView weatherbutton = null;
	private WeatherView weathercurvebottom = null;
	private WeatherView weathershawowview = null;
	private WeatherView weathercurvetop = null;
	private WeatherView weathertop = null;
	private WeatherView weatherround = null;
	private WeatherView weatherbottom = null;
	private WeatherView upshadow = null;
	private WeatherView downshadow = null;
	private WeatherView weathertop1;
	private WeatherView weathercurvebottom1;
	private static WidgetWeatherClock weatherclock = null;
	private TextureRegion clockbackviewregion = null;
	private TextureRegion clockhourregion = null;
	private TextureRegion clockminutrregion = null;
	private TextureRegion clocksecondregion = null;
	private TextureRegion clockpointregion = null;
	private TextureRegion clockshadowregion = null;
	private TextureRegion weatherroundregion = null;
	private TextureRegion weathertopcityregion = null;
	private TextureRegion weatherbottomtmpregion = null;
	private TextureRegion weathercurvetopregion = null;
	private TextureRegion weathercurvebottomregion = null;
	private TextureRegion weatherbuttonregion = null;
	private TextureRegion weathershadowregion = null;
	private TextureRegion shadowtopregion = null;
	private TextureRegion shadowbottomregion = null;
	//翻转动画需要的参数
	private boolean mIsScrollTween = false;
	private boolean mIsInitScrollTween = false;
	public float xAngleLastScrollTween = 0;
	public float yAngleLastScrollTween = 0;
	private boolean isTouchdownTrigered = false;
	// 滚动时钟记录当前位移
	private float mRotateX = 0;
	private float mRotateY = 0;
	// 时钟滑动动画
	private Tween mScrollTween = null;
	// 是否处理点击事件
	private boolean mIsOnClickEvent = false;
	private boolean openDFClock = true;
	private Tween mAutoScrollTween = null;
	private Tween clockshadowTween = null;
	private Tween weathershadowTween = null;
	private Tween clockTween = null;
	private Tween weatherTween = null;
	boolean isfilling = false;
	private boolean isNeedRotation = false;
	public float xAngleStart = 0;
	public float yAngleStart = 0;
	public float xAngleEnd = 0;
	public float yAngleEnd = 0;
	public float xAngleLast = 0;
	public float yAngleLast = 0;
	private boolean mIsInitSensorTween = false;
	private boolean isOnbackSide = false;
	private boolean isOnReturn = false;
	private boolean isScroolLocked = false;
	private float lastRotationX = 0;
	public boolean animalInService = false;
	private float deltaRotationX;
	private ContentObserver contenobserver = null;
	private DateReceiver datereceiver = null;
	private boolean ifClickTween = false;
	private boolean ifReturnTween = false;
	public boolean ifScroll = false;
	public boolean ifFirstScroll = true;
	public static final String ACTION_LAUNCHER_APPLY_THEME = "com.coco.theme.action.DEFAULT_THEME_CHANGED";
	public static final String SP_KEY_AMI_DOWNLOAD_ID = "Ami_download_id";
	WidgetThemeManager widgetthememanager = null;
	//	private static Weather weather = null;
	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
	List<WeatherInfo> list = new ArrayList<WeatherInfo>();
	public static float scale = 1f;
	public static final int LeftMove = 1;
	public static final int RightMove = 2;
	public static final int TopMove = 3;
	public static final int BottomMove = 4;
	public static Handler updatehandle = null;
	public static final int MSG_UpdateForSUCCESS = 9;
	public static final int MSG_UpdateForFAILURE = 10;
	
	public static WidgetWeatherClock getIntance()
	{
		return weatherclock;
	}
	
	private enum TimerTypeEnum
	{
		handler , timerTask , service
	}
	
	public WidgetWeatherClock(
			String name ,
			MainAppContext context ,
			int widgetId )
	{
		super( name );
		weatherclock = this;
		this.transform = true;
		//		this.maincontext = context;
		this.mContext = context.mContainerContext;
		this.mAppContext = context;
		widgetthememanager = new WidgetThemeManager( context );
		//		MODEL_WIDTH = Utils3D.getScreenWidth();
		//		MODEL_HEIGHT = R3D.Workspace_cell_each_height;
		cooGdx = new CooGdx( context.mGdxApplication );
		scale = Math.min( Utils3D.getScreenWidth() / 720f , R3D.Workspace_cell_each_height / 240f );
		Parameter.changePoition();
		this.width = Utils3D.getScreenWidth();
		this.height = R3D.Workspace_cell_each_height * 2;
		this.setOrigin( this.width / 2 , this.height / 2 );
		this.setOriginZ( Parameter.WEATHER_CLOCK_ROTATION_Z );
		this.setRotationVector( 1 , 0 , 0 );
		//		Log.d( "mytag" , "this.originX:"+this.originX+":"+"this.originY:"+this.originY );
		clockviewgroup = new WeatherViewGroup( "clockviewgroup" );
		clockhourview = new ClockViewGroup( "clockhourview" );
		clockminuteview = new ClockViewGroup( "clockminuteview" );
		clocksecondview = new ClockViewGroup( "clocksecondview" );
		clockshadow = new WeatherViewGroup( "clockshadow" );
		weatherviewgroup = new WeatherViewGroup( "weatherviewgroup" );
		weathersemicirclegroup = new WeatherViewGroup( "weathersemicirclegroup" );
		upshadowgroup = new WeatherViewGroup( "upshadowgroup" );
		downshadowgroup = new WeatherViewGroup( "downshadowgroup" );
		weathershadow = new WeatherViewGroup( "weathershadow" );
		clockbackviewregion = WeatherClockHelper.getRegion( mContext , ThemeManager.getInstance().getCurrentThemeContext() , "clock.png" );
		clockhourregion = WeatherClockHelper.getRegion( mContext , ThemeManager.getInstance().getCurrentThemeContext() , "hour.png" );
		clockminutrregion = WeatherClockHelper.getRegion( mContext , ThemeManager.getInstance().getCurrentThemeContext() , "minute.png" );
		clocksecondregion = WeatherClockHelper.getRegion( mContext , ThemeManager.getInstance().getCurrentThemeContext() , "second.png" );
		clockpointregion = WeatherClockHelper.getRegion( mContext , ThemeManager.getInstance().getCurrentThemeContext() , "point.png" );
		clockshadowregion = WeatherClockHelper.getRegion( mContext , ThemeManager.getInstance().getCurrentThemeContext() , "clockshadow.png" );
		weatherbuttonregion = WeatherClockHelper.getRegion( mContext , "button.png" );
		shadowtopregion = WeatherClockHelper.getRegion( mContext , "weatherupshadow.png" );
		shadowbottomregion = WeatherClockHelper.getRegion( mContext , "weatherdownshow.png" );
		weathershadowregion = WeatherClockHelper.getRegion( mContext , "bigshadow.png" );
		weatherroundregion = WeatherClockHelper.getRegion( mContext , "weather.png" );
		if( !DefaultLayout.enable_google_version )
		{
			weathertopcityregion = WeatherClockHelper.drawWeatherTopCity( mAppContext , "未知" , "未知" );
			weatherbottomtmpregion = WeatherClockHelper.drawWeatherBottomTmp( mAppContext , "空气质量：未知" , "星期一" , "42" , "36" , "36" );
			weathercurvetopregion = WeatherClockHelper.drawCurveTopTextureRegion( mAppContext , "晴" , "晴" , "晴" , "晴" , "晴" , 42 , 42 , 42 , 42 , 42 );
			weathercurvebottomregion = WeatherClockHelper.drawCurveBottomTextureRegion( mAppContext , 36 , 36 , 36 , 36 , 36 , "今天" , "明天" , "周一" , "周二" , "周三" );
		}
		else
		{
			weathertopcityregion = WeatherClockHelper.drawWeatherTopCityfor( mAppContext , "unknow" , "unknow" , "32" );
			weatherbottomtmpregion = WeatherClockHelper.drawWeatherBottomTmpFor( mAppContext , "Mon" , "81" , "72" , "80" , sharedPref.getString( "tmpType" , "f" ) , "50" );
			weathercurvetopregion = WeatherClockHelper.drawCurveTopTextureRegion( mAppContext , "32" , "32" , "32" , "32" , "32" , 81 , 81 , 81 , 81 , 81 );
			weathercurvebottomregion = WeatherClockHelper.drawCurveBottomTextureRegion( mAppContext , 72 , 72 , 72 , 72 , 72 , "Mon" , "Tues" , "Wed" , "Thur" , "Fri" );
		}
		initobjs();
		startClockTimer();
		startTweenTimer();
		if( !DefaultLayout.enable_google_version )
		{
			mAppContext.mGdxApplication.runOnUiThread( new Runnable() {
				
				@Override
				public void run()
				{
					contenobserver = new ContentObserver( new Handler() ) {
						
						@Override
						public void onChange(
								boolean selfChange )
						{
							super.onChange( selfChange );
							//下面是数据库改变后做的动作
							//						Log.d( "mytag" , "数据库改变喽！！！" );
							if( WeatherClockHelper.ifHaveAMIapp( mContext ) )
							{
								//							Log.d( "mytag" , "数据库改变了" );
								changeRegion();
							}
						}
					};
					mContext.getContentResolver().registerContentObserver( WeatherClockHelper.WEATHER_SEARCH_FROM_WALLPAPER , true , contenobserver );
				}
			} );
		}
		datereceiver = new DateReceiver();
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction( Intent.ACTION_TIME_TICK );
		intentfilter.addAction( ACTION_LAUNCHER_APPLY_THEME );
		intentfilter.addAction( "com.iLoong.weatherclock.search" );
		intentfilter.addAction( WeatherDataService.UPDATE_RESULT );
		mContext.registerReceiver( datereceiver , intentfilter );
		if( !DefaultLayout.enable_google_version )
		{
			if( WeatherClockHelper.ifHaveAMIapp( mContext ) )
			{
				changeRegion();
			}
		}
		else
		{
			Weather weather = getWeather();
			if( weather != null )
			{
				changeRegiobFor( weather , sharedPref.getString( "tmpType" , "f" ) );
			}
		}
		//		Log.d( "mytag" , "oncreat" );
		Intent intent = new Intent( iLoongLauncher.getInstance() , WeatherDataService.class );
		iLoongLauncher.getInstance().startService( intent );
		ChangeResize();
		mAppContext.mGdxApplication.runOnUiThread( new Runnable() {
			
			@Override
			public void run()
			{
				updatehandle = new Handler() {
					
					@Override
					public void handleMessage(
							Message msg )
					{
						switch( msg.what )
						{
							case MSG_UpdateForSUCCESS:
								Bundle bundles = (Bundle)msg.obj;
								Weather weathers = (Weather)bundles.getSerializable( "myupdateweatherinfo" );
								if( weathers != null && weathers.getList() != null && weathers.getList().size() == 5 )
								{
									Log.d( "mytag" , "天气时钟进入到国外更新数据发送广播" );
									DoWhenForSuccess( weathers );
								}
								break;
							case MSG_UpdateForFAILURE:
								Log.d( "mytag" , "天气时钟进入到国外突然断网" );
								Toast.makeText( iLoongLauncher.getInstance() , iLoongLauncher.getInstance().getString( R.string.networt_notconnetederror ) , Toast.LENGTH_SHORT ).show();
								break;
							default:
								break;
						}
						super.handleMessage( msg );
					}
				};
			}
		} );
	}
	
	public void DoWhenForSuccess(
			Weather weather )
	{
		String tmpType = sharedPref.getString( "tmpType" , "f" );
		if( weather != null && weather.getList() != null && weather.getList().size() == 5 )
		{
			changeRegiobFor( weather , tmpType );
			Editor ed = sharedPref.edit();
			ed.putBoolean( "weatherstate" , true );
			ed.putString( "weathercityname" , weather.getWeathercity() );
			ed.putString( "weathercode" , weather.getWeathercode() );
			ed.putString( "weathercondition" , weather.getWeathercondition() );
			ed.putString( "weathercurrenttmp" , weather.getCurrtmp() );
			ed.putString( "weathershidu" , weather.getShidu() );
			ed.putString( "listweathercode0" , weather.getList().get( 0 ).getWeathercode() );
			ed.putString( "listweatherhighTmp0" , weather.getList().get( 0 ).getHightmp() );
			ed.putString( "listweatherlowTmp0" , weather.getList().get( 0 ).getLowtmp() );
			ed.putString( "listweatherweek0" , weather.getList().get( 0 ).getWeatherweek() );
			ed.putString( "listweathercode1" , weather.getList().get( 1 ).getWeathercode() );
			ed.putString( "listweatherhighTmp1" , weather.getList().get( 1 ).getHightmp() );
			ed.putString( "listweatherlowTmp1" , weather.getList().get( 1 ).getLowtmp() );
			ed.putString( "listweatherweek1" , weather.getList().get( 1 ).getWeatherweek() );
			ed.putString( "listweathercode2" , weather.getList().get( 2 ).getWeathercode() );
			ed.putString( "listweatherhighTmp2" , weather.getList().get( 2 ).getHightmp() );
			ed.putString( "listweatherlowTmp2" , weather.getList().get( 2 ).getLowtmp() );
			ed.putString( "listweatherweek2" , weather.getList().get( 2 ).getWeatherweek() );
			ed.putString( "listweathercode3" , weather.getList().get( 3 ).getWeathercode() );
			ed.putString( "listweatherhighTmp3" , weather.getList().get( 3 ).getHightmp() );
			ed.putString( "listweatherlowTmp3" , weather.getList().get( 3 ).getLowtmp() );
			ed.putString( "listweatherweek3" , weather.getList().get( 3 ).getWeatherweek() );
			ed.putString( "listweathercode4" , weather.getList().get( 4 ).getWeathercode() );
			ed.putString( "listweatherhighTmp4" , weather.getList().get( 4 ).getHightmp() );
			ed.putString( "listweatherlowTmp4" , weather.getList().get( 4 ).getLowtmp() );
			ed.putString( "listweatherweek4" , weather.getList().get( 4 ).getWeatherweek() );
			ed.commit();
		}
	}
	
	public void ChangeResize()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		if( sp.getBoolean( "com.iLoong.WeatherClock" , false ) )
		{
			int spanX = sp.getInt( "com.iLoong.WeatherClock" + ":spanX" , -1 );
			int spanY = sp.getInt( "com.iLoong.WeatherClock" + ":spanY" , -1 );
			if( spanX != -1 && spanY != -1 )
			{
				this.width = spanX * R3D.Workspace_cell_each_width;
				this.height = spanY * R3D.Workspace_cell_each_height;
				float nowscaleX = this.width / Utils3D.getScreenWidth();
				float nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 2 );
				scale = Math.min( nowscaleX , nowscaleY ) * Math.min( Utils3D.getScreenWidth() / 720f , R3D.Workspace_cell_each_height / 240f );
				changePosition();
			}
		}
	}
	
	@Override
	public void onClockStateChanged(
			int state )
	{
		super.onClockStateChanged( state );
	}
	
	public Weather getWeather()
	{
		if( sharedPref.getBoolean( "weatherstate" , false ) )
		{
			Weather weather = new Weather();
			weather.setWeathercity( sharedPref.getString( "weathercityname" , null ) );
			weather.setWeathercode( sharedPref.getString( "weathercode" , null ) );
			weather.setWeathercondition( sharedPref.getString( "weathercondition" , null ) );
			weather.setCurrtmp( sharedPref.getString( "weathercurrenttmp" , null ) );
			weather.setShidu( sharedPref.getString( "weathershidu" , null ) );
			List<Weather> list = new ArrayList<Weather>();
			Weather weather01 = new Weather();
			weather01.setWeathercode( sharedPref.getString( "listweathercode0" , null ) );
			weather01.setHightmp( sharedPref.getString( "listweatherhighTmp0" , null ) );
			weather01.setLowtmp( sharedPref.getString( "listweatherlowTmp0" , null ) );
			weather01.setWeatherweek( sharedPref.getString( "listweatherweek0" , null ) );
			list.add( weather01 );
			Weather weather02 = new Weather();
			weather02.setWeathercode( sharedPref.getString( "listweathercode1" , null ) );
			weather02.setHightmp( sharedPref.getString( "listweatherhighTmp1" , null ) );
			weather02.setLowtmp( sharedPref.getString( "listweatherlowTmp1" , null ) );
			weather02.setWeatherweek( sharedPref.getString( "listweatherweek1" , null ) );
			list.add( weather02 );
			Weather weather03 = new Weather();
			weather03.setWeathercode( sharedPref.getString( "listweathercode2" , null ) );
			weather03.setHightmp( sharedPref.getString( "listweatherhighTmp2" , null ) );
			weather03.setLowtmp( sharedPref.getString( "listweatherlowTmp2" , null ) );
			weather03.setWeatherweek( sharedPref.getString( "listweatherweek2" , null ) );
			list.add( weather03 );
			Weather weather04 = new Weather();
			weather04.setWeathercode( sharedPref.getString( "listweathercode3" , null ) );
			weather04.setHightmp( sharedPref.getString( "listweatherhighTmp3" , null ) );
			weather04.setLowtmp( sharedPref.getString( "listweatherlowTmp3" , null ) );
			weather04.setWeatherweek( sharedPref.getString( "listweatherweek3" , null ) );
			list.add( weather04 );
			Weather weather05 = new Weather();
			weather05.setWeathercode( sharedPref.getString( "listweathercode4" , null ) );
			weather05.setHightmp( sharedPref.getString( "listweatherhighTmp4" , null ) );
			weather05.setLowtmp( sharedPref.getString( "listweatherlowTmp4" , null ) );
			weather05.setWeatherweek( sharedPref.getString( "listweatherweek4" , null ) );
			list.add( weather05 );
			weather.setList( list );
			return weather;
		}
		return null;
	}
	
	public void addWeatherView()
	{
		weatherround = new WeatherView( mAppContext , "weatherround" , weatherroundregion , "weatherbg_round.obj" );
		weatherviewgroup.addView( weatherround );
		weatherbutton = new WeatherView( mAppContext , "weatherbutton" , weatherbuttonregion , "button.obj" );
		weatherviewgroup.addView( weatherbutton );
		weathercurvetop = new WeatherView( mAppContext , "weathercurvetop" , weathercurvetopregion , "curvetop.obj" );
		weatherviewgroup.addView( weathercurvetop );
		weatherbottom = new WeatherView( mAppContext , "weatherbottom" , weatherbottomtmpregion , "degree.obj" );
		weatherviewgroup.addView( weatherbottom );
		downshadow = new WeatherView( mAppContext , "downshadow" , shadowbottomregion , "shadowdown.obj" );
		downshadowgroup.addView( downshadow );
		downshadowgroup.color.a = 0;
		weatherviewgroup.addView( downshadowgroup );
		upshadow = new WeatherView( mAppContext , "upshadow" , shadowtopregion , "shadowup.obj" );
		upshadowgroup.addView( upshadow );
		upshadowgroup.color.a = 0;
		weatherviewgroup.addView( upshadowgroup );
		weathertop1 = new WeatherView( mAppContext , "weathertop1" , WeatherClockHelper.getRegion( mContext , "round_bottom.png" ) , "city.obj" );
		weathersemicirclegroup.addView( weathertop1 );
		weathertop = new WeatherView( mAppContext , "weathertop" , weathercurvebottomregion , "city.obj" );
		weathersemicirclegroup.addView( weathertop );
		weathercurvebottom1 = new WeatherView( mAppContext , "weathercurvebottom1" , WeatherClockHelper.getRegion( mContext , "round_top.png" ) , "curvedown.obj" );
		weathersemicirclegroup.addView( weathercurvebottom1 );
		weathercurvebottom = new WeatherView( mAppContext , "weathercurvebottom" , weathertopcityregion , "curvedown.obj" );
		weathersemicirclegroup.addView( weathercurvebottom );
		weathershawowview = new WeatherView( mAppContext , "weathershawowview" , weathershadowregion , "weathershadow.obj" );
		weathershadow.addView( weathershawowview );
		weathershadow.color.a = 0;
		weatherviewgroup.addView( weathershadow );
		weatherviewgroup.addView( weathersemicirclegroup );
		weatherviewgroup.color.a = 0;
		this.addView( weatherviewgroup );
	}
	
	private class DateReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context context ,
				Intent intent )
		{
			String action = intent.getAction();
			if( action.equals( Intent.ACTION_TIME_TICK ) )
			{
				Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
				int hour = c.get( Calendar.HOUR_OF_DAY );
				int minute = c.get( Calendar.MINUTE );
				int second = c.get( Calendar.SECOND );
				if( hour == 18 && minute == 0 && second == 0 )
				{
					if( !DefaultLayout.enable_google_version )
					{
						if( WeatherClockHelper.ifHaveAMIapp( mContext ) )
						{
							changeRegion();
						}
					}
					else
					{
						Weather weather = getWeather();
						if( weather != null )
						{
							changeRegiobFor( weather , sharedPref.getString( "tmpType" , "f" ) );
						}
					}
				}
			}
			else if( action.equals( ACTION_LAUNCHER_APPLY_THEME ) )
			{
				//				maincontext.mGdxApplication.postRunnable( new Runnable() {
				//					
				//					@Override
				//					public void run()
				//					{
				//						TextureRegion trbg = WeatherClockHelper.getRegion( mContext , ThemeManager.getInstance().getCurrentThemeContext() , "clock.png" );
				//						if( trbg != null )
				//						{
				//							if( clockbackview.region != null )
				//							{
				//								clockbackview.region.getTexture().dispose();
				//							}
				//							clockbackview.region.setRegion( trbg );
				//						}
				//						TextureRegion trhour = WeatherClockHelper.getRegion( mContext , ThemeManager.getInstance().getCurrentThemeContext() , "hour.png" );
				//						if( trhour != null )
				//						{
				//							if( clockhour.region != null )
				//							{
				//								clockhour.region.getTexture().dispose();
				//							}
				//							clockhour.region.setRegion( trhour );
				//						}
				//						TextureRegion trminute = WeatherClockHelper.getRegion( mContext , ThemeManager.getInstance().getCurrentThemeContext() , "minute.png" );
				//						if( trminute != null )
				//						{
				//							if( clockminute.region != null )
				//							{
				//								clockminute.region.getTexture().dispose();
				//							}
				//							clockminute.region.setRegion( trminute );
				//						}
				//						TextureRegion trsecond = WeatherClockHelper.getRegion( mContext , ThemeManager.getInstance().getCurrentThemeContext() , "second.png" );
				//						if( trsecond != null )
				//						{
				//							if( clocksecond.region != null )
				//							{
				//								clocksecond.region.getTexture().dispose();
				//							}
				//							clocksecond.region.setRegion( trsecond );
				//						}
				//						TextureRegion trpoint = WeatherClockHelper.getRegion( mContext , ThemeManager.getInstance().getCurrentThemeContext() , "point.png" );
				//						if( trpoint != null )
				//						{
				//							if( clockpoint.region != null )
				//							{
				//								clockpoint.region.getTexture().dispose();
				//							}
				//							clockpoint.region.setRegion( trpoint );
				//						}
				//						TextureRegion trshadow = WeatherClockHelper.getRegion( mContext , ThemeManager.getInstance().getCurrentThemeContext() , "clockshadow.png" );
				//						if( trshadow != null )
				//						{
				//							if( clockshawowview.region != null )
				//							{
				//								clockshawowview.region.getTexture().dispose();
				//							}
				//							clockshawowview.region.setRegion( trshadow );
				//						}
				//					}
				//				} );
			}
			else if( action.equals( "com.iLoong.weatherclock.search" ) )
			{
				Bundle bundle = intent.getExtras();
				Weather weather = (Weather)bundle.getSerializable( "weatherinfo" );
				String tmpType = sharedPref.getString( "tmpType" , "f" );
				if( weather != null && weather.getList().size() == 5 )
				{
					changeRegiobFor( weather , tmpType );
					Editor ed = sharedPref.edit();
					ed.putBoolean( "weatherstate" , true );
					ed.putString( "weathercityname" , weather.getWeathercity() );
					ed.putString( "weathercode" , weather.getWeathercode() );
					ed.putString( "weathercondition" , weather.getWeathercondition() );
					ed.putString( "weathercurrenttmp" , weather.getCurrtmp() );
					ed.putString( "weathershidu" , weather.getShidu() );
					ed.putString( "listweathercode0" , weather.getList().get( 0 ).getWeathercode() );
					ed.putString( "listweatherhighTmp0" , weather.getList().get( 0 ).getHightmp() );
					ed.putString( "listweatherlowTmp0" , weather.getList().get( 0 ).getLowtmp() );
					ed.putString( "listweatherweek0" , weather.getList().get( 0 ).getWeatherweek() );
					ed.putString( "listweathercode1" , weather.getList().get( 1 ).getWeathercode() );
					ed.putString( "listweatherhighTmp1" , weather.getList().get( 1 ).getHightmp() );
					ed.putString( "listweatherlowTmp1" , weather.getList().get( 1 ).getLowtmp() );
					ed.putString( "listweatherweek1" , weather.getList().get( 1 ).getWeatherweek() );
					ed.putString( "listweathercode2" , weather.getList().get( 2 ).getWeathercode() );
					ed.putString( "listweatherhighTmp2" , weather.getList().get( 2 ).getHightmp() );
					ed.putString( "listweatherlowTmp2" , weather.getList().get( 2 ).getLowtmp() );
					ed.putString( "listweatherweek2" , weather.getList().get( 2 ).getWeatherweek() );
					ed.putString( "listweathercode3" , weather.getList().get( 3 ).getWeathercode() );
					ed.putString( "listweatherhighTmp3" , weather.getList().get( 3 ).getHightmp() );
					ed.putString( "listweatherlowTmp3" , weather.getList().get( 3 ).getLowtmp() );
					ed.putString( "listweatherweek3" , weather.getList().get( 3 ).getWeatherweek() );
					ed.putString( "listweathercode4" , weather.getList().get( 4 ).getWeathercode() );
					ed.putString( "listweatherhighTmp4" , weather.getList().get( 4 ).getHightmp() );
					ed.putString( "listweatherlowTmp4" , weather.getList().get( 4 ).getLowtmp() );
					ed.putString( "listweatherweek4" , weather.getList().get( 4 ).getWeatherweek() );
					ed.commit();
				}
			}
			else if( action.equals( WeatherDataService.UPDATE_RESULT ) )
			{
				String str = intent.getStringExtra( "cooee.weather.updateResult" );
				if( str.equals( "UPDATE_SUCCESED" ) )
				{
					changeRegiobFor( getWeather() , sharedPref.getString( "tmpType" , "f" ) );
				}
			}
		}
	}
	
	public void changeRegiobFor(
			final Weather weather ,
			final String TmpType )
	{
		sharedPref.edit().putString( "currentType" , TmpType ).commit();
		mAppContext.mGdxApplication.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				if( weather != null )
				{
					if( weather.getList() != null && weather.getList().size() == 5 )
					{
						Log.d( "mytag" , "天气时钟更新成功" );
						if( weather.getWeathercity() != null && weather.getWeathercode() != null && weather.getWeathercondition() != null )
						{
							if( weathercurvebottom != null )
							{
								if( weathercurvebottom.region != null )
								{
									weathercurvebottom.region.getTexture().dispose();
								}
								weathercurvebottom.region = WeatherClockHelper.drawWeatherTopCityfor( mAppContext , weather.getWeathercondition() , weather.getWeathercity() , weather.getWeathercode() );
							}
						}
						if( weather.getCurrtmp() != null && weather.getList().get( 0 ).getHightmp() != null && weather.getList().get( 0 ).getLowtmp() != null && weather.getList().get( 0 )
								.getWeatherweek() != null )
						{
							if( weatherbottom != null )
							{
								if( weatherbottom.region != null )
								{
									weatherbottom.region.getTexture().dispose();
								}
								if( Integer.parseInt( weather.getList().get( 0 ).getHightmp() ) < Integer.parseInt( weather.getCurrtmp() ) )
								{
									weatherbottom.region = WeatherClockHelper.drawWeatherBottomTmpFor( mAppContext , weather.getList().get( 0 ).getWeatherweek() , weather.getCurrtmp() , weather
											.getList().get( 0 ).getLowtmp() , weather.getCurrtmp() , TmpType , weather.getShidu() );
								}
								else if( Integer.parseInt( weather.getList().get( 0 ).getLowtmp() ) > Integer.parseInt( weather.getCurrtmp() ) )
								{
									weatherbottom.region = WeatherClockHelper.drawWeatherBottomTmpFor( mAppContext , weather.getList().get( 0 ).getWeatherweek() , weather.getList().get( 0 )
											.getHightmp() , weather.getCurrtmp() , weather.getCurrtmp() , TmpType , weather.getShidu() );
								}
								else
								{
									weatherbottom.region = WeatherClockHelper.drawWeatherBottomTmpFor( mAppContext , weather.getList().get( 0 ).getWeatherweek() , weather.getList().get( 0 )
											.getHightmp() , weather.getList().get( 0 ).getLowtmp() , weather.getCurrtmp() , TmpType , weather.getShidu() );
								}
							}
						}
						if( weathercurvetop != null )
						{
							if( weathercurvetop.region != null )
							{
								weathercurvetop.region.getTexture().dispose();
							}
							weathercurvetop.region = WeatherClockHelper.drawCurveTopTextureRegion(
									mAppContext ,
									weather.getList().get( 0 ).getWeathercode() ,
									weather.getList().get( 1 ).getWeathercode() ,
									weather.getList().get( 2 ).getWeathercode() ,
									weather.getList().get( 3 ).getWeathercode() ,
									weather.getList().get( 4 ).getWeathercode() ,
									Integer.parseInt( weather.getList().get( 0 ).getHightmp() ) ,
									Integer.parseInt( weather.getList().get( 1 ).getHightmp() ) ,
									Integer.parseInt( weather.getList().get( 2 ).getHightmp() ) ,
									Integer.parseInt( weather.getList().get( 3 ).getHightmp() ) ,
									Integer.parseInt( weather.getList().get( 4 ).getHightmp() ) );
						}
						if( weathertop != null )
						{
							if( weathertop.region != null )
							{
								weathertop.region.getTexture().dispose();
							}
							weathertop.region = WeatherClockHelper.drawCurveBottomTextureRegion(
									mAppContext ,
									Integer.parseInt( weather.getList().get( 0 ).getLowtmp() ) ,
									Integer.parseInt( weather.getList().get( 1 ).getLowtmp() ) ,
									Integer.parseInt( weather.getList().get( 2 ).getLowtmp() ) ,
									Integer.parseInt( weather.getList().get( 3 ).getLowtmp() ) ,
									Integer.parseInt( weather.getList().get( 4 ).getLowtmp() ) ,
									weather.getList().get( 0 ).getWeatherweek() ,
									weather.getList().get( 1 ).getWeatherweek() ,
									weather.getList().get( 2 ).getWeatherweek() ,
									weather.getList().get( 3 ).getWeatherweek() ,
									weather.getList().get( 4 ).getWeatherweek() );
						}
					}
				}
			}
		} );
	}
	
	public static void makeRootDirectory(
			String filePath )
	{
		File file = null;
		try
		{
			file = new File( filePath );
			if( !file.exists() )
			{
				file.mkdir();
			}
		}
		catch( Exception e )
		{
		}
	}
	
	public static void saveBitmapToSdcard(
			String bitName ,
			Bitmap mBitmap )
	{
		File file = null;
		makeRootDirectory( "/sdcard/111/" );
		try
		{
			file = new File( "/sdcard/111/" + bitName + ".png" );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		FileOutputStream fOut = null;
		try
		{
			fOut = new FileOutputStream( file );
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		mBitmap.compress( Bitmap.CompressFormat.PNG , 100 , fOut );
		try
		{
			fOut.flush();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		try
		{
			fOut.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	private void changeRegion()
	{
		mAppContext.mGdxApplication.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				ContentResolver cr = iLoongLauncher.getInstance().getContentResolver();
				if( cr != null )
				{
					list = WeatherClockHelper.QueryByUri( cr );
				}
				if( list != null && list.size() == 7 )
				{
					String weatherName = list.get( 0 ).getStatus();
					if( WeatherClockHelper.splitString( list.get( 0 ).getCity() ) != null && WeatherClockHelper.ResolveAir( weatherName ) != null )
					{
						if( weathercurvebottom != null )
						{
							if( weathercurvebottom.region != null )
							{
								weathercurvebottom.region.getTexture().dispose();
							}
							weathercurvebottom.region = WeatherClockHelper.drawWeatherTopCity(
									mAppContext ,
									WeatherClockHelper.ResolveAir( weatherName ) ,
									WeatherClockHelper.splitString( list.get( 0 ).getCity() ) );
						}
					}
					if( list.get( 0 ).getAqiDec() != null && WeatherClockHelper.splitTem( list.get( 0 ).getTgd1() ) != null )
					{
						if( weatherbottom != null )
						{
							if( weatherbottom.region != null )
							{
								weatherbottom.region.getTexture().dispose();
							}
							weatherbottom.region = WeatherClockHelper.drawWeatherBottomTmp(
									mAppContext ,
									"空气质量：" + list.get( 0 ).getAqiDec() ,
									WeatherClockHelper.changeWeek( list.get( 0 ).getWeek() ) ,
									list.get( 0 ).getTemperature1() + "" ,
									list.get( 0 ).getTemperature2() + "" ,
									WeatherClockHelper.splitTem( list.get( 0 ).getTgd1() ) );
						}
					}
					if( weathercurvetop != null )
					{
						if( weathercurvetop.region != null )
						{
							weathercurvetop.region.getTexture().dispose();
						}
						weathercurvetop.region = WeatherClockHelper.drawCurveTopTextureRegion( mAppContext , list.get( 0 ).getStatus() , list.get( 1 ).getStatus() , list.get( 2 ).getStatus() , list
								.get( 3 ).getStatus() , list.get( 4 ).getStatus() , list.get( 0 ).getTemperature1() , list.get( 1 ).getTemperature1() , list.get( 2 ).getTemperature1() , list.get( 3 )
								.getTemperature1() , list.get( 4 ).getTemperature1() );
					}
					if( weathertop != null )
					{
						if( weathertop.region != null )
						{
							weathertop.region.getTexture().dispose();
						}
						weathertop.region = WeatherClockHelper.drawCurveBottomTextureRegion(
								mAppContext ,
								list.get( 0 ).getTemperature2() ,
								list.get( 1 ).getTemperature2() ,
								list.get( 2 ).getTemperature2() ,
								list.get( 3 ).getTemperature2() ,
								list.get( 4 ).getTemperature2() ,
								"今天" ,
								"明天" ,
								WeatherClockHelper.changeForWeek( list.get( 2 ).getWeek() ) ,
								WeatherClockHelper.changeForWeek( list.get( 3 ).getWeek() ) ,
								WeatherClockHelper.changeForWeek( list.get( 4 ).getWeek() ) );
					}
				}
			}
		} );
	}
	
	public void initobjs()
	{
		//		weatherviewgroup.setZ( -78.099701f );
		addClockView();
		addWeatherView();
	}
	
	private ClockView clockbackview = null;
	private ClockView clockhour = null;
	private ClockView clockminute = null;
	private ClockView clocksecond = null;
	private ClockView clockpoint = null;
	private ClockView clockshawowview = null;
	
	public void addClockView()
	{
		if( clockbackviewregion != null )
		{
			clockbackview = new ClockView( mAppContext , "clockbackground" , clockbackviewregion , "clock.obj" );
			clockviewgroup.addView( clockbackview );
		}
		if( clockhourregion != null )
		{
			clockhour = new ClockView( mAppContext , "clockhour" , clockhourregion , "hour.obj" );
			clockhourview.addView( clockhour );
			clockviewgroup.addView( clockhourview );
		}
		if( clockminutrregion != null )
		{
			clockminute = new ClockView( mAppContext , "clockminute" , clockminutrregion , "minute.obj" );
			clockminuteview.addView( clockminute );
			clockviewgroup.addView( clockminuteview );
		}
		if( clocksecondregion != null )
		{
			clocksecond = new ClockView( mAppContext , "clocksecond" , clocksecondregion , "second.obj" );
			clocksecondview.addView( clocksecond );
			clockviewgroup.addView( clocksecondview );
		}
		if( clockpointregion != null )
		{
			clockpoint = new ClockView( mAppContext , "clockpoint" , clockpointregion , "point.obj" );
			clockviewgroup.addView( clockpoint );
		}
		if( clockshadowregion != null )
		{
			clockshawowview = new ClockView( mAppContext , "clockshawowview" , clockshadowregion , "clockshadow.obj" );
			clockshadow.addView( clockshawowview );
			clockshadow.color.a = 0;
			clockviewgroup.addView( clockshadow );
		}
		this.addView( clockviewgroup );
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
	
	public void startTweenTimer()
	{
		tweenTimeChanged();
		mTweenTimer = new TweenTimer( this , 1000 );
		mTweenTimer.start();
	}
	
	public void stopTweenTimer()
	{
		// 定时器类型为Timer
		if( mTweenTimer != null )
		{
			mTweenTimer.stop();
			ifFirstClick = false;
		}
	}
	
	public boolean ispointinbutton(
			float curx ,
			float cury )
	{
		//默认205
		double r = Math.sqrt( ( curx - this.originX ) * ( curx - this.originX ) + ( cury - ( this.originY - 215 * scale ) ) * ( cury - ( this.originY - 215 * scale ) ) );
		if( r > 100 * scale )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	private boolean ifFirstClick = true;
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( checkValidity() > 0 )
		{
			return super.onClick( x , y );
		}
		if( ifClickTween || ifReturnTween )
		{
			return true;
		}
		if( !isPointInClock( x , y ) )
		{
			Workspace3D.getInstance().setIconsSelectState();
		}
		if( DefaultLayout.enable_google_version )
		{
			if( isOnbackSide )
			{
				x = this.width - x;
				y = this.height - y;
				if( ispointinbutton( x , y ) )
				{
					if( ifFirstClick )
					{
						stopTweenTimer();
						weatherbutton.color.a = 1;
					}
					weathertop.show();
					weathertop1.show();
					weatherbottom.show();
					weathercurvetop.show();
					weathercurvebottom.show();
					weathercurvebottom1.show();
					//上下翻转动画
					if( !isOnReturn )
					{
						if( animation_click != null )
						{
							animation_click.free();
							animation_click = null;
						}
						if( animation_click_front != null )
						{
							animation_click_front.free();
							animation_click_front = null;
						}
						if( animation_click_back != null )
						{
							animation_click_back.free();
							animation_click_back = null;
						}
						if( animation_click_shadowup != null )
						{
							animation_click_shadowup.free();
							animation_click_shadowup = null;
						}
						if( animation_click_shadowdown != null )
						{
							animation_click_shadowdown.free();
							animation_click_shadowdown = null;
						}
						upshadowgroup.color.a = 0;
						downshadowgroup.color.a = 0;
						ifClickTween = true;
						animation_click = Timeline.createSequence();
						animation_click_front = Timeline.createParallel();
						animation_click_back = Timeline.createParallel();
						animation_click_shadowup = Timeline.createSequence();
						animation_click_shadowdown = Timeline.createSequence();
						weathersemicirclegroup.setOrigin( this.width / 2 , this.height / 2 );
						weathersemicirclegroup.setOriginZ( Parameter.WEATHER_SEMICIRCLE_ROTATION_Z );
						weathersemicirclegroup.setRotationVector( 1 , 0 , 0 );
						animation_click_front.push( Tween.to( weathersemicirclegroup , View3DTweenAccessor.ROTATION , 0.5f ).target( 90 , 0 , 0 ).ease( Quad.IN ) );
						animation_click_shadowup.push( Tween.to( upshadowgroup , View3DTweenAccessor.OPACITY , 0.1f ).target( 1f , 0 , 0 ).ease( Linear.INOUT ) );
						animation_click_shadowup.push( Tween.to( upshadowgroup , View3DTweenAccessor.OPACITY , 0.4f ).target( 0 , 0 , 0 ).ease( Cubic.IN ) );
						animation_click_front.push( animation_click_shadowup );
						animation_click_back.push( Tween.to( weathersemicirclegroup , View3DTweenAccessor.ROTATION , 0.5f ).target( 180 , 0 , 0 ).ease( Quad.OUT ) );
						animation_click_shadowdown.push( Tween.to( downshadowgroup , View3DTweenAccessor.OPACITY , 0.4f ).target( 0.75f , 0 , 0 ).ease( Quad.OUT ) );
						animation_click_shadowdown.push( Tween.to( downshadowgroup , View3DTweenAccessor.OPACITY , 0.1f ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
						animation_click_back.push( animation_click_shadowdown );
						animation_click.push( animation_click_front );
						animation_click.push( animation_click_back );
						animation_click.start( View3DTweenAccessor.manager ).setCallback( this );
						weatherbutton.region = WeatherClockHelper.getRegion( mContext , "buttonback.png" );
					}
					else
					{
						upshadowgroup.color.a = 0;
						downshadowgroup.color.a = 0;
						if( animation_return != null )
						{
							animation_return.free();
							animation_return = null;
						}
						if( animation_return_front != null )
						{
							animation_return_front.free();
							animation_return_front = null;
						}
						if( animation_return_back != null )
						{
							animation_return_back.free();
							animation_return_back = null;
						}
						if( animation_return_shadowdown != null )
						{
							animation_return_shadowdown.free();
							animation_return_shadowdown = null;
						}
						if( animation_return_shadowup != null )
						{
							animation_return_shadowup.free();
							animation_return_shadowup = null;
						}
						ifReturnTween = true;
						animation_return = Timeline.createSequence();
						animation_return_front = Timeline.createParallel();
						animation_return_back = Timeline.createParallel();
						animation_return_shadowdown = Timeline.createSequence();
						animation_return_shadowup = Timeline.createSequence();
						weathersemicirclegroup.setOrigin( this.width / 2 , this.height / 2 );
						weathersemicirclegroup.setOriginZ( Parameter.WEATHER_SEMICIRCLE_ROTATION_Z );
						weathersemicirclegroup.setRotationVector( 1 , 0 , 0 );
						animation_return_front.push( Tween.to( weathersemicirclegroup , View3DTweenAccessor.ROTATION , 0.5f ).target( 90 , 0 , 0 ).ease( Quad.IN ) );
						animation_return_shadowdown.push( Tween.to( downshadowgroup , View3DTweenAccessor.OPACITY , 0.1f ).target( 0.75f , 0 , 0 ).ease( Linear.INOUT ) );
						animation_return_shadowdown.push( Tween.to( downshadowgroup , View3DTweenAccessor.OPACITY , 0.4f ).target( 0 , 0 , 0 ).ease( Cubic.IN ) );
						animation_return_front.push( animation_return_shadowdown );
						animation_return_back.push( Tween.to( weathersemicirclegroup , View3DTweenAccessor.ROTATION , 0.5f ).target( 0 , 0 , 0 ).ease( Quad.OUT ) );
						animation_return_shadowup.push( Tween.to( upshadowgroup , View3DTweenAccessor.OPACITY , 0.4f ).target( 1f , 0 , 0 ).ease( Quad.OUT ) );
						animation_return_shadowup.push( Tween.to( upshadowgroup , View3DTweenAccessor.OPACITY , 0.1f ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
						animation_return_back.push( animation_return_shadowup );
						animation_return.push( animation_return_front );
						animation_return.push( animation_return_back );
						animation_return.start( View3DTweenAccessor.manager ).setCallback( this );
						weatherbutton.region = WeatherClockHelper.getRegion( mContext , "button.png" );
						//					weathertop.region = weatherregion;
						//					weatherbottom.region = weatherregion;
						weatherround.region = weatherroundregion;
					}
				}
				else if( isPointInClock( x , y ) && !ispointinbutton( x , y ) )
				{
					Intent intent = new Intent();
					intent.setComponent( new ComponentName( mContext , "com.iLoong.WeatherClock.view.CityFinderActivity" ) );
					intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
					intent.setAction( "com.iLoong.cityfinder.MAIN" );
					mContext.startActivity( intent );
				}
			}
			else
			{
				if( isPointInClock( x , y ) )
				{
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
					String pkgname = prefs.getString( "CLOCKpackagerName" , null );
					if( pkgname != null )
					{
						Intent intent1 = mContext.getPackageManager().getLaunchIntentForPackage( pkgname );
						if( intent1 != null )
						{
							intent1.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
							mContext.startActivity( intent1 );
						}
						else
						{
							Intent i2 = new Intent( Settings.ACTION_DATE_SETTINGS );
							i2.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
							mContext.startActivity( i2 );
						}
					}
					else
					{
						try
						{
							String packageName = null;
							SharedPreferences p = mAppContext.mContainerContext.getSharedPreferences( "iLoong.Widget.Clock" , 0 );
							packageName = p.getString( "clock_package" , null );
							if( packageName == null )
							{
								listPackages();
								Editor editor = p.edit();
								if( pagList.size() != 0 )
								{
									packageName = pagList.get( 0 );
									editor.putString( "clock_package" , packageName );
								}
								editor.commit();
							}
							PackageManager pm = mContext.getPackageManager();
							if( packageName != null )
							{
								Intent intent = pm.getLaunchIntentForPackage( packageName );
								if( intent != null )
								{
									intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
									mContext.startActivity( intent );
								}
								else
								{
									Intent i2 = new Intent( Settings.ACTION_DATE_SETTINGS );
									i2.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
									mContext.startActivity( i2 );
								}
							}
							else
							{
								Intent i2 = new Intent( Settings.ACTION_DATE_SETTINGS );
								mContext.startActivity( i2 );
							}
							UmengMobclickAgent.FirstTime( mContext , "WeatherClockClick" );
						}
						catch( Exception ex )
						{
							ex.printStackTrace();
						}
					}
					this.releaseFocus();
					return true;
				}
			}
		}
		else
		{
			if( isOnbackSide )
			{
				x = this.width - x;
				y = this.height - y;
				if( ispointinbutton( x , y ) )
				{
					if( ifFirstClick )
					{
						stopTweenTimer();
						weatherbutton.color.a = 1;
					}
					weathertop.show();
					weathertop1.show();
					weatherbottom.show();
					weathercurvetop.show();
					weathercurvebottom.show();
					weathercurvebottom1.show();
					//上下翻转动画
					if( !isOnReturn )
					{
						if( animation_click != null )
						{
							animation_click.free();
							animation_click = null;
						}
						if( animation_click_front != null )
						{
							animation_click_front.free();
							animation_click_front = null;
						}
						if( animation_click_back != null )
						{
							animation_click_back.free();
							animation_click_back = null;
						}
						if( animation_click_shadowup != null )
						{
							animation_click_shadowup.free();
							animation_click_shadowup = null;
						}
						if( animation_click_shadowdown != null )
						{
							animation_click_shadowdown.free();
							animation_click_shadowdown = null;
						}
						upshadowgroup.color.a = 0;
						downshadowgroup.color.a = 0;
						ifClickTween = true;
						animation_click = Timeline.createSequence();
						animation_click_front = Timeline.createParallel();
						animation_click_back = Timeline.createParallel();
						animation_click_shadowup = Timeline.createSequence();
						animation_click_shadowdown = Timeline.createSequence();
						weathersemicirclegroup.setOrigin( this.width / 2 , this.height / 2 );
						weathersemicirclegroup.setOriginZ( Parameter.WEATHER_SEMICIRCLE_ROTATION_Z );
						weathersemicirclegroup.setRotationVector( 1 , 0 , 0 );
						animation_click_front.push( Tween.to( weathersemicirclegroup , View3DTweenAccessor.ROTATION , 0.5f ).target( 90 , 0 , 0 ).ease( Quad.IN ) );
						animation_click_shadowup.push( Tween.to( upshadowgroup , View3DTweenAccessor.OPACITY , 0.1f ).target( 1f , 0 , 0 ).ease( Linear.INOUT ) );
						animation_click_shadowup.push( Tween.to( upshadowgroup , View3DTweenAccessor.OPACITY , 0.4f ).target( 0 , 0 , 0 ).ease( Cubic.IN ) );
						animation_click_front.push( animation_click_shadowup );
						animation_click_back.push( Tween.to( weathersemicirclegroup , View3DTweenAccessor.ROTATION , 0.5f ).target( 180 , 0 , 0 ).ease( Quad.OUT ) );
						animation_click_shadowdown.push( Tween.to( downshadowgroup , View3DTweenAccessor.OPACITY , 0.4f ).target( 0.75f , 0 , 0 ).ease( Quad.OUT ) );
						animation_click_shadowdown.push( Tween.to( downshadowgroup , View3DTweenAccessor.OPACITY , 0.1f ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
						animation_click_back.push( animation_click_shadowdown );
						animation_click.push( animation_click_front );
						animation_click.push( animation_click_back );
						animation_click.start( View3DTweenAccessor.manager ).setCallback( this );
						weatherbutton.region = WeatherClockHelper.getRegion( mContext , "buttonback.png" );
					}
					else
					{
						upshadowgroup.color.a = 0;
						downshadowgroup.color.a = 0;
						if( animation_return != null )
						{
							animation_return.free();
							animation_return = null;
						}
						if( animation_return_front != null )
						{
							animation_return_front.free();
							animation_return_front = null;
						}
						if( animation_return_back != null )
						{
							animation_return_back.free();
							animation_return_back = null;
						}
						if( animation_return_shadowdown != null )
						{
							animation_return_shadowdown.free();
							animation_return_shadowdown = null;
						}
						if( animation_return_shadowup != null )
						{
							animation_return_shadowup.free();
							animation_return_shadowup = null;
						}
						ifReturnTween = true;
						animation_return = Timeline.createSequence();
						animation_return_front = Timeline.createParallel();
						animation_return_back = Timeline.createParallel();
						animation_return_shadowdown = Timeline.createSequence();
						animation_return_shadowup = Timeline.createSequence();
						weathersemicirclegroup.setOrigin( this.width / 2 , this.height / 2 );
						weathersemicirclegroup.setOriginZ( Parameter.WEATHER_SEMICIRCLE_ROTATION_Z );
						weathersemicirclegroup.setRotationVector( 1 , 0 , 0 );
						animation_return_front.push( Tween.to( weathersemicirclegroup , View3DTweenAccessor.ROTATION , 0.5f ).target( 90 , 0 , 0 ).ease( Quad.IN ) );
						animation_return_shadowdown.push( Tween.to( downshadowgroup , View3DTweenAccessor.OPACITY , 0.1f ).target( 0.75f , 0 , 0 ).ease( Linear.INOUT ) );
						animation_return_shadowdown.push( Tween.to( downshadowgroup , View3DTweenAccessor.OPACITY , 0.4f ).target( 0 , 0 , 0 ).ease( Cubic.IN ) );
						animation_return_front.push( animation_return_shadowdown );
						animation_return_back.push( Tween.to( weathersemicirclegroup , View3DTweenAccessor.ROTATION , 0.5f ).target( 0 , 0 , 0 ).ease( Quad.OUT ) );
						animation_return_shadowup.push( Tween.to( upshadowgroup , View3DTweenAccessor.OPACITY , 0.4f ).target( 1f , 0 , 0 ).ease( Quad.OUT ) );
						animation_return_shadowup.push( Tween.to( upshadowgroup , View3DTweenAccessor.OPACITY , 0.1f ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
						animation_return_back.push( animation_return_shadowup );
						animation_return.push( animation_return_front );
						animation_return.push( animation_return_back );
						animation_return.start( View3DTweenAccessor.manager ).setCallback( this );
						weatherbutton.region = WeatherClockHelper.getRegion( mContext , "button.png" );
						weatherround.region = weatherroundregion;
					}
				}
				else if( !WeatherClockHelper.ifHaveAMIapp( mContext ) && isPointInClock( x , y ) && !ispointinbutton( x , y ) )
				{
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
					long downId = sp.getLong( SP_KEY_AMI_DOWNLOAD_ID , -1 );
					Intent intent = new Intent( iLoongLauncher.getInstance() , DownloadDialog.class );
					intent.putExtra( DownloadDialog.KEY_EXTRA_ICON , R.drawable.ami_logo );
					intent.putExtra( DownloadDialog.KEY_EXTRA_TITLE , R.string.ami_download_dialog_title_text );
					intent.putExtra( DownloadDialog.KEY_EXTRA_MESSAGE , R.string.ami_download_dialog_message_text );
					intent.putExtra( DownloadDialog.KEY_EXTRA_URL , "http://www.coolauncher.cn/download/apk/Amiweather.apk" );
					intent.putExtra( DownloadDialog.KEY_EXTRA_FILE_NAME , "Amiweather.apk" );
					intent.putExtra( DownloadDialog.KEY_EXTRA_DOWN_ID , downId );
					intent.putExtra( DownloadDialog.KEY_EXTRA_DOWN_ID_SP_KEY , SP_KEY_AMI_DOWNLOAD_ID );
					iLoongLauncher.getInstance().startActivity( intent );
					return true;
					//没有下载Ami客户端的时候，下载客户端
					//				mAppContext.mGdxApplication.runOnUiThread( new Runnable() {
					//					
					//					@Override
					//					public void run()
					//					{
					//						return viewParent.onCtrlEvent( this , MSG_CLICK_AMI );
					//						AlertDialog.Builder builder = null;
					//						int androidSDKVersion = Integer.parseInt( VERSION.SDK );
					//						if( androidSDKVersion >= 11 )
					//						{
					//							builder = new AlertDialog.Builder( mContext , AlertDialog.THEME_HOLO_LIGHT );
					//						}
					//						else
					//						{
					//							builder = new AlertDialog.Builder( mContext );
					//						}
					//						builder.setTitle( maincontext.mWidgetContext.getResources().getString( R.string.dialog_title ) )
					//								.setMessage( maincontext.mWidgetContext.getResources().getString( R.string.dialog_message ) )
					//								.setPositiveButton( maincontext.mWidgetContext.getResources().getString( R.string.yes ) , new Dialog.OnClickListener() {
					//									
					//									@Override
					//									public void onClick(
					//											DialogInterface dialog ,
					//											int which )
					//									{
					//										if( WeatherClockHelper.isHaveInternet( mContext ) )
					//										{
					//											Uri playUri = Uri.parse( "http://www.coolauncher.cn/download/apk/Amiweather.apk" );
					//											Intent browserIntent = new Intent( Intent.ACTION_VIEW , playUri );
					//											browserIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
					//											mContext.startActivity( browserIntent );
					//											MobclickAgent.onEvent( mContext , "DownLoadAmi" );
					//										}
					//										else
					//										{
					//											Toast.makeText( mContext , maincontext.mWidgetContext.getResources().getString( R.string.download_toast ) , Toast.LENGTH_SHORT ).show();
					//										}
					//									}
					//								} ).setNegativeButton( maincontext.mWidgetContext.getResources().getString( R.string.no ) , new Dialog.OnClickListener() {
					//									
					//									@Override
					//									public void onClick(
					//											DialogInterface dialog ,
					//											int which )
					//									{
					//									}
					//								} );
					//						builder.create().show();
					//					}
					//				} );
				}
				else if( WeatherClockHelper.ifHaveAMIapp( mContext ) && isPointInClock( x , y ) && !ispointinbutton( x , y ) )
				{
					MobclickAgent.onEventValue( mContext , "WeatherClockToAmi" , null , 24 * 60 * 60 );
					Intent intent = new Intent();
					intent.setComponent( new ComponentName( WeatherClockHelper.Amipkgname , WeatherClockHelper.Amiclsname ) );
					mContext.startActivity( intent );
				}
			}
			else
			{
				if( isPointInClock( x , y ) )
				{
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
					String pkgname = prefs.getString( "CLOCKpackagerName" , null );
					if( pkgname != null )
					{
						Intent intent1 = mContext.getPackageManager().getLaunchIntentForPackage( pkgname );
						if( intent1 != null )
						{
							intent1.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
							mContext.startActivity( intent1 );
						}
						else
						{
							Intent i2 = new Intent( Settings.ACTION_DATE_SETTINGS );
							i2.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
							mContext.startActivity( i2 );
						}
					}
					else
					{
						try
						{
							String packageName = null;
							SharedPreferences p = mAppContext.mContainerContext.getSharedPreferences( "iLoong.Widget.Clock" , 0 );
							packageName = p.getString( "clock_package" , null );
							if( packageName == null )
							{
								listPackages();
								Editor editor = p.edit();
								if( pagList.size() != 0 )
								{
									packageName = pagList.get( 0 );
									editor.putString( "clock_package" , packageName );
								}
								editor.commit();
							}
							PackageManager pm = mContext.getPackageManager();
							if( packageName != null )
							{
								Intent intent = pm.getLaunchIntentForPackage( packageName );
								if( intent != null )
								{
									intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
									mContext.startActivity( intent );
								}
								else
								{
									Intent i2 = new Intent( Settings.ACTION_DATE_SETTINGS );
									i2.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
									mContext.startActivity( i2 );
								}
							}
							else
							{
								Intent i2 = new Intent( Settings.ACTION_DATE_SETTINGS );
								mContext.startActivity( i2 );
							}
						}
						catch( Exception ex )
						{
							ex.printStackTrace();
						}
					}
					this.releaseFocus();
					return true;
				}
			}
		}
		return true;
	}
	
	private HashMap<String , Object> item = new HashMap<String , Object>();
	private List<String> pagList = new ArrayList<String>();
	
	private void listPackages()
	{
		ArrayList<PInfo> apps = getInstalledApps( false );
		final int max = apps.size();
		for( int i = 0 ; i < max ; i++ )
		{
			item = new HashMap<String , Object>();
			int aa = apps.get( i ).pname.length();
			if( aa > 11 )
			{
				if( apps.get( i ).pname.indexOf( "clock" ) != -1 || apps.get( i ).pname.indexOf( "xtime" ) != -1 )
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
			ClockTimerHandler thread = new ClockTimerHandler( null , this );
			thread.stop();
		}
	}
	
	public static String DATA_SERVICE_ACTION = "com.cooee.app.cooeeweather.dataprovider.weatherDataService";
	
	@Override
	public void onResume()
	{
		startClockTimer();
		super.onResume();
		MobclickAgent.onResume( mContext );
		if( !DefaultLayout.enable_google_version )
		{
			if( WeatherClockHelper.ifHaveAMIapp( mContext ) )
			{
				changeRegion();
			}
		}
		else
		{
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		MobclickAgent.onPause( mContext );
	}
	
	@Override
	public void clockTimeChanged()
	{
		Calendar mCalendar = Calendar.getInstance();
		long milliseconds = System.currentTimeMillis();
		mCalendar.setTimeInMillis( milliseconds );
		mHeadHour = mCalendar.get( Calendar.HOUR );
		mHeadMinute = mCalendar.get( Calendar.MINUTE );
		mHeadSecond = mCalendar.get( Calendar.SECOND );
		final float secondRotation = ClockHelper.getSecondHandRotation( mHeadSecond );
		final float minuteRotation = ClockHelper.getMinuteHandRotation( mHeadMinute );
		final float hourRotation = ClockHelper.getHourHandRotation( mHeadMinute , mHeadHour );
		if( mAppContext != null )
		{
			mAppContext.mGdxApplication.postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					if( clocksecondview != null )
					{
						clocksecondview.rotation = secondRotation - 90;
					}
					if( clockminuteview != null )
					{
						clockminuteview.rotation = minuteRotation - 90;
					}
					if( clockhourview != null )
					{
						clockhourview.rotation = hourRotation - 90;
					}
				}
			} );
		}
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( ifClickTween || ifReturnTween )
		{
			return true;
		}
		if( checkValidity() == 1 )
		{
			return super.onTouchDown( x , y , pointer );
		}
		if( isOnbackSide )
		{
			x = this.width - x;
			y = this.height - y;
			if( ispointinbutton( x , y ) )
			{
				if( !isOnReturn )
				{
					weatherbutton.region = WeatherClockHelper.getRegion( mContext , "buttonpress.png" );
				}
				else
				{
					weatherbutton.region = WeatherClockHelper.getRegion( mContext , "buttonbackpress.png" );
				}
			}
		}
		isTouchdownTrigered = true;
		mRotateX = 0;
		mRotateY = 0;
		ifScroll = true;
		ifFirstScroll = true;
		clockshadow.color.a = 0;
		weathershadow.color.a = 0;
		if( isOnbackSide )
		{
			weatherviewgroup.color.a = 1;
			clockviewgroup.color.a = 0;
		}
		else
		{
			weatherviewgroup.color.a = 0;
			clockviewgroup.color.a = 1;
		}
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
			}
			if( clockshadowTween != null && !clockshadowTween.isFinished() )
			{
				clockshadowTween.free();
				clockshadowTween = null;
			}
			if( weathershadowTween != null && !weathershadowTween.isFinished() )
			{
				weathershadowTween.free();
				weathershadowTween = null;
			}
			if( clockTween != null && !clockTween.isFinished() )
			{
				clockTween.free();
				clockTween = null;
			}
			if( weatherTween != null && !weatherTween.isFinished() )
			{
				weatherTween.free();
				weatherTween = null;
			}
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
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		ifScroll = false;
		if( ifClickTween || ifReturnTween )
		{
			return true;
		}
		if( checkValidity() == 1 )
		{
			this.releaseFocus();
			return super.onTouchUp( x , y , pointer );
		}
		if( isOnbackSide )
		{
			x = this.width - x;
			y = this.height - y;
			if( ispointinbutton( x , y ) )
			{
				if( !isOnReturn )
				{
					weatherbutton.region = WeatherClockHelper.getRegion( mContext , "button.png" );
				}
				else
				{
					weatherbutton.region = WeatherClockHelper.getRegion( mContext , "buttonback.png" );
				}
			}
		}
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
			//			if( !onIsOpenSensor() )
			//			{
			if( openDFClock )
			{
				//				Log.d( "mytag" , "自动翻转" );
				startAutoEffect();
			}
			else
			{
				//				Log.d( "mytag" , "做动画" );
				mScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( cur ).delay( 0 ).start( View3DTweenAccessor.manager );
			}
			//			}
			//			else
			//			{
			//				if( ( xAngleEnd != xAngleLastScrollTween ) && ( yAngleEnd != yAngleLastScrollTween ) )
			//				{
			//					mIsScrollTween = true;
			//					mIsInitScrollTween = true;
			//					setPosition( xAngleEnd , yAngleEnd );
			//					mScrollTween = Tween.to( this , View3DTweenAccessor.POS_XY , 1.2f ).target( xAngleEnd , yAngleEnd ).ease( Cubic.OUT ).setCallback( this ).delay( 0 )
			//							.start( View3DTweenAccessor.manager );
			//				}
			//			}
			this.releaseFocus();
		}
		return false;
	}
	
	public void startAutoEffect()
	{
		if( !isScroolLocked )
		{
			if( ( this.rotation < 90 && this.rotation != 0 && this.rotation != 360 ) || ( rotation > 90 && rotation < 180 ) || ( isOnbackSide && ( rotation > 180 && rotation < 270 ) ) )
			{
				float deltaTime = 1.2f;
				if( lastRotationX < 180 )
					deltaTime = ( ( Math.abs( this.rotation ) + 90 ) % 90 ) / 100.0f;
				else
					deltaTime = ( Math.abs( ( 180 - Math.abs( this.rotation ) ) ) % 90 ) / 100.0f;
				animalInService = true;
				mAutoScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , deltaTime ).ease( Linear.INOUT ).target( lastRotationX ).delay( 0 ).start( View3DTweenAccessor.manager )
						.setCallback( this );
				clockshadowTween = Tween.to( clockshadow , View3DTweenAccessor.OPACITY , deltaTime ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager )
						.setCallback( this );
				weathershadowTween = Tween.to( weathershadow , View3DTweenAccessor.OPACITY , deltaTime ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager )
						.setCallback( this );
				if( isOnbackSide )
				{
					clockTween = Tween.to( clockviewgroup , View3DTweenAccessor.OPACITY , deltaTime ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager )
							.setCallback( this );
					weatherTween = Tween.to( weatherviewgroup , View3DTweenAccessor.OPACITY , deltaTime ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager )
							.setCallback( this );
				}
				else
				{
					clockTween = Tween.to( clockviewgroup , View3DTweenAccessor.OPACITY , deltaTime ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager )
							.setCallback( this );
					weatherTween = Tween.to( weatherviewgroup , View3DTweenAccessor.OPACITY , deltaTime ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager )
							.setCallback( this );
				}
			}
			else if( !isOnbackSide && ( rotation > 270 && rotation < 360 ) )
			{
				float deltaTime = ( ( Math.abs( 360 - this.rotation ) ) % 90 ) / 100.0f;
				animalInService = true;
				mAutoScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , deltaTime ).ease( Linear.INOUT ).target( 360 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				//				Log.d( "mytag" , "翻转" );
			}
		}
		isScroolLocked = false;
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		ifScroll = false;
		if( ifClickTween || ifReturnTween )
		{
			return true;
		}
		if( checkValidity() == 1 )
		{
			return true;
		}
		if( isOnbackSide )
		{
			x = this.width - x;
			y = this.height - y;
			if( ispointinbutton( x , y ) )
			{
				if( !isOnReturn )
				{
					weatherbutton.region = WeatherClockHelper.getRegion( mContext , "button.png" );
				}
				else
				{
					weatherbutton.region = WeatherClockHelper.getRegion( mContext , "buttonback.png" );
				}
			}
		}
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
		if( ( k == 0 || k > 1.7 || k < -1.7 ) && isPointInClock( x , y ) )
		{
			isNeedRotation = true;
		}
		else if( !isNeedRotation )
		{
			if( ifFirstScroll )
			{
				if( isOnReturn )
				{
					weathertop.show();
					weathertop1.hide();
					weatherbottom.hide();
					weathercurvetop.show();
					weathercurvebottom.show();
					weathercurvebottom1.hide();
					ifFirstScroll = false;
				}
				else
				{
					weathertop.show();
					weathertop1.hide();
					weatherbottom.show();
					weathercurvetop.hide();
					weathercurvebottom.show();
					weathercurvebottom1.hide();
					ifFirstScroll = false;
				}
			}
			isNeedRotation = false;
			startAutoEffect();
			isScroolLocked = false;
			this.releaseFocus();
			return false;
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
		//		if( DefaultLayout.enable_google_version )
		//		{
		//			return true;
		//		}
		isfilling = false;
		if( ifFirstScroll )
		{
			if( isOnReturn )
			{
				weathertop.show();
				weathertop1.hide();
				weatherbottom.hide();
				weathercurvetop.show();
				weathercurvebottom.show();
				weathercurvebottom1.hide();
				ifFirstScroll = false;
			}
			else
			{
				weathertop.show();
				weathertop1.hide();
				weatherbottom.show();
				weathercurvetop.hide();
				weathercurvebottom.show();
				weathercurvebottom1.hide();
				ifFirstScroll = false;
			}
		}
		if( deltaRotationX >= 90 )
		{
			isOnbackSide = !isOnbackSide;
			if( isOnbackSide )
			{
				onClockStateChanged( 1 );
			}
			else
			{
				onClockStateChanged( 0 );
			}
			isScroolLocked = true;
			animalInService = true;
			mAutoScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( lastRotationX + 180 ).delay( 0 ).setCallback( this )
					.start( View3DTweenAccessor.manager );
			lastRotationX = ( lastRotationX + 180 ) % 360;
			if( isOnbackSide )
			{
				clockshadowTween = Tween.to( clockshadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weathershadowTween = Tween.to( weathershadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				clockTween = Tween.to( clockviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( weatherviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
			else
			{
				clockshadowTween = Tween.to( clockshadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weathershadowTween = Tween.to( weathershadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				clockTween = Tween.to( clockviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( weatherviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
		}
		else if( deltaRotationX <= -90 )
		{
			isOnbackSide = !isOnbackSide;
			if( isOnbackSide )
			{
				onClockStateChanged( 1 );
			}
			else
			{
				onClockStateChanged( 0 );
			}
			isScroolLocked = true;
			animalInService = true;
			mAutoScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( ( lastRotationX + 180 ) % 360 ).delay( 0 ).setCallback( this )
					.start( View3DTweenAccessor.manager );
			lastRotationX = ( lastRotationX + 180 ) % 360;
			if( isOnbackSide )
			{
				clockshadowTween = Tween.to( clockshadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weathershadowTween = Tween.to( weathershadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				clockTween = Tween.to( clockviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( weatherviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
			else
			{
				clockshadowTween = Tween.to( clockshadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weathershadowTween = Tween.to( weathershadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				clockTween = Tween.to( clockviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( weatherviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
		}
		else
		{
			if( !isScroolLocked )
			{
				UmengMobclickAgent.FirstTime( mContext , "WeatherClockOverturn" );
				setRotationAngle( lastRotationX + mRotateY / height * 360 , 0 , 0 );
				float judget = mRotateY / height * 360;
				if( lastRotationX == 0 && judget > 0 && judget < 90 )
				{
					clockshadow.color.a = ( mRotateY / height * 360 ) / 180f;
					weathershadow.color.a = 1 - ( mRotateY / height * 360 ) / 180f;
					clockviewgroup.color.a = 1 - ( mRotateY / height * 360 ) / 180f;
					weatherviewgroup.color.a = ( mRotateY / height * 360 ) / 180f;
				}
				else if( lastRotationX == 0 && judget < 0 && judget > -90 )
				{
					clockshadow.color.a = Math.abs( mRotateY / height * 360 ) / 180f;
					weathershadow.color.a = 1 - Math.abs( mRotateY / height * 360 ) / 180f;
					clockviewgroup.color.a = 1 - Math.abs( mRotateY / height * 360 ) / 180f;
					weatherviewgroup.color.a = Math.abs( mRotateY / height * 360 ) / 180f;
				}
				else if( lastRotationX == 180 && judget > 0 && judget < 90 )
				{
					weathershadow.color.a = ( mRotateY / height * 360 ) / 180f;
					clockshadow.color.a = 1 - ( mRotateY / height * 360 ) / 180f;
					weatherviewgroup.color.a = 1 - ( mRotateY / height * 360 ) / 180f;
					clockviewgroup.color.a = ( mRotateY / height * 360 ) / 180f;
				}
				else if( lastRotationX == 180 && judget < 0 && judget > -90 )
				{
					weathershadow.color.a = Math.abs( mRotateY / height * 360 ) / 180f;
					clockshadow.color.a = 1 - Math.abs( mRotateY / height * 360 ) / 180f;
					weatherviewgroup.color.a = 1 - Math.abs( mRotateY / height * 360 ) / 180f;
					clockviewgroup.color.a = Math.abs( mRotateY / height * 360 ) / 180f;
				}
			}
			else
			{
			}
		}
		mIsOnClickEvent = false;
		return true;
	}
	
	@Override
	public void onEvent(
			int type ,
			@SuppressWarnings( "rawtypes" ) BaseTween source )
	{
		if( source.equals( mAutoScrollTween ) && type == TweenCallback.COMPLETE )
		{
			animalInService = false;
		}
		else if( source.equals( clockshadowTween ) && type == TweenCallback.COMPLETE )
		{
			clockshadow.color.a = 0;
		}
		else if( source.equals( weathershadowTween ) && type == TweenCallback.COMPLETE )
		{
			weathershadow.color.a = 0;
		}
		else if( source.equals( clockTween ) && type == TweenCallback.COMPLETE )
		{
			if( isOnbackSide )
			{
				weatherviewgroup.color.a = 1;
				clockviewgroup.color.a = 0;
			}
			else
			{
				weatherviewgroup.color.a = 0;
				clockviewgroup.color.a = 1;
			}
		}
		else if( source.equals( weatherTween ) && type == TweenCallback.COMPLETE )
		{
			if( isOnbackSide )
			{
				weatherviewgroup.color.a = 1;
				clockviewgroup.color.a = 0;
			}
			else
			{
				weatherviewgroup.color.a = 0;
				clockviewgroup.color.a = 1;
			}
		}
		else if( source.equals( mScrollTween ) && type == TweenCallback.COMPLETE && onIsOpenSensor() )
		{
			mIsScrollTween = false;
			mIsInitScrollTween = false;
			xAngleLastScrollTween = 0;
			yAngleLastScrollTween = 0;
		}
		else if( source.equals( animation_click ) && type == TweenCallback.COMPLETE )
		{
			//			weathertop.region = WeatherClockHelper.getRegion( mContext , "weather_1.png" );
			//			weatherbottom.region = WeatherClockHelper.getRegion( mContext , "weather_1.png" );
			weatherround.region = WeatherClockHelper.getRegion( mContext , "weather_1.png" );
			upshadowgroup.color.a = 0;
			downshadowgroup.color.a = 0;
			ifClickTween = false;
			isOnReturn = true;
			if( isOnReturn )
			{
				weathertop.show();
				weathertop1.hide();
				weatherbottom.hide();
				weathercurvetop.show();
				weathercurvebottom.show();
				weathercurvebottom1.hide();
			}
			else
			{
				weathertop.show();
				weathertop1.hide();
				weatherbottom.show();
				weathercurvetop.hide();
				weathercurvebottom.show();
				weathercurvebottom1.hide();
			}
		}
		else if( source.equals( animation_return ) && type == TweenCallback.COMPLETE )
		{
			upshadowgroup.color.a = 0;
			downshadowgroup.color.a = 0;
			ifReturnTween = false;
			isOnReturn = false;
			if( isOnReturn )
			{
				weathertop.show();
				weathertop1.hide();
				weatherbottom.hide();
				weathercurvetop.show();
				weathercurvebottom.show();
				weathercurvebottom1.hide();
			}
			else
			{
				weathertop.show();
				weathertop1.hide();
				weatherbottom.show();
				weathercurvetop.hide();
				weathercurvebottom.show();
				weathercurvebottom1.hide();
			}
		}
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		cooGdx.gl.glDepthMask( false );
		cooGdx.gl.glDisable( GL10.GL_DEPTH_TEST );
		cooGdx.gl.glClear( GL10.GL_DEPTH_BUFFER_BIT ); // 清除屏幕及深度缓存
		//		cooGdx.gl.glDepthFunc( GL10.GL_LEQUAL );
		super.draw( batch , parentAlpha );
		//		cooGdx.gl.glDisable( GL10.GL_DEPTH_TEST );
		//		cooGdx.gl.glClear( GL10.GL_DEPTH_BUFFER_BIT ); // 清除屏幕及深度缓存
		//		cooGdx.gl.glDepthMask( false );
	}
	
	public static final int MSG_Widget3D_LONGCLICK_FOR_WEATHERCLOCK = 222;
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( checkValidity() > 0 )
		{
			return true;
		}
		if( animalInService )
		{
			startAutoEffect();
		}
		//		if( isOnbackSide )
		//		{
		//			x = this.width - x;
		//			y = this.height - y;
		//		}
		//		if( isPointInClock( x , y ) )
		//		{
		//			if( isOnReturn )
		//			{
		//				weathertop.show();
		//				weathertop1.hide();
		//				weatherbottom.hide();
		//				weathercurvetop.show();
		//				weathercurvebottom.show();
		//				weathercurvebottom1.hide();
		//			}
		//			else
		//			{
		//				weathertop.show();
		//				weathertop1.hide();
		//				weatherbottom.show();
		//				weathercurvetop.hide();
		//				weathercurvebottom.show();
		//				weathercurvebottom1.hide();
		//			}
		//			this.mIsOnClickEvent = false;
		//			this.point.x = x;
		//			this.point.y = y;
		//			this.toAbsolute( point );
		//			Vector2 obj = new Vector2( point.x , point.y );
		//			this.setTag( obj );
		//			this.releaseFocus();
		//			isTouchdownTrigered = false;
		//			return viewParent.onCtrlEvent( this , 0 );
		//		}
		//		else
		//		{
		//			return false;
		//		}
		//		
		//		
		if( isOnbackSide )
		{
			x = this.width - x;
			y = this.height - y;
			if( isPointInWeather( x , y ) )
			{
				if( isOnReturn )
				{
					weathertop.show();
					weathertop1.hide();
					weatherbottom.hide();
					weathercurvetop.show();
					weathercurvebottom.show();
					weathercurvebottom1.hide();
				}
				else
				{
					weathertop.show();
					weathertop1.hide();
					weatherbottom.show();
					weathercurvetop.hide();
					weathercurvebottom.show();
					weathercurvebottom1.hide();
				}
				this.mIsOnClickEvent = false;
				this.point.x = x;
				this.point.y = y;
				this.toAbsolute( point );
				Vector2 obj = new Vector2( point.x , point.y );
				this.setTag( obj );
				this.releaseFocus();
				isTouchdownTrigered = false;
				return viewParent.onCtrlEvent( this , 0 );
			}
			else
			{
				return viewParent.onCtrlEvent( this , MSG_Widget3D_LONGCLICK_FOR_WEATHERCLOCK );
			}
		}
		else
		{
			if( isPointInClock( x , y ) )
			{
				if( isOnReturn )
				{
					weathertop.show();
					weathertop1.hide();
					weatherbottom.hide();
					weathercurvetop.show();
					weathercurvebottom.show();
					weathercurvebottom1.hide();
				}
				else
				{
					weathertop.show();
					weathertop1.hide();
					weatherbottom.show();
					weathercurvetop.hide();
					weathercurvebottom.show();
					weathercurvebottom1.hide();
				}
				this.mIsOnClickEvent = false;
				this.point.x = x;
				this.point.y = y;
				this.toAbsolute( point );
				Vector2 obj = new Vector2( point.x , point.y );
				this.setTag( obj );
				this.releaseFocus();
				isTouchdownTrigered = false;
				return viewParent.onCtrlEvent( this , 0 );
			}
			else
			{
				return viewParent.onCtrlEvent( this , MSG_Widget3D_LONGCLICK_FOR_WEATHERCLOCK );
			}
		}
	}
	
	private boolean isPointInWeather(
			float x ,
			float y )
	{
		double r = Math.sqrt( ( x - this.originX ) * ( x - this.originX ) + ( y - this.originY ) * ( y - this.originY ) );
		if( r > Parameter.WEATHER_WIDTH / 2 || r > Parameter.WEATHER_HEIGHT / 2 )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	private boolean isPointInClock(
			float x ,
			float y )
	{
		double r = Math.sqrt( ( x - this.originX ) * ( x - this.originX ) + ( y - this.originY ) * ( y - this.originY ) );
		if( r > Parameter.WEATHER_WIDTH / 2 || r > Parameter.WEATHER_HEIGHT / 2 )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	//	public boolean isCurLauncherSupportSensor()
	//	{
	//		if( mAppContext == null )
	//		{
	//			return false;
	//		}
	//		int versionCode = -1;
	//		String CurLauncherName = mAppContext.mContainerContext.getPackageName();
	//		try
	//		{
	//			PackageManager pm = mAppContext.mContainerContext.getPackageManager();
	//			PackageInfo pinfo = pm.getPackageInfo( CurLauncherName , PackageManager.GET_CONFIGURATIONS );
	//			versionCode = pinfo.versionCode;
	//		}
	//		catch( NameNotFoundException e )
	//		{
	//		}
	//		if( ( CurLauncherName.equals( "com.coco.launcher" ) ) && ( versionCode >= 7260 ) )
	//		{
	//			return true;
	//		}
	//		else
	//		{
	//			return false;
	//		}
	//	}
	//	@Override
	//	public boolean onIsShowSensor()
	//	{
	//		if( mAppContext == null )
	//		{
	//			return false;
	//		}
	//		if( isCurLauncherSupportSensor() == false )
	//		{
	//			return false;
	//		}
	//		return super.onIsShowSensor();
	//	}
	//	@Override
	//	public boolean onIsOpenSensor()
	//	{
	//		if( mAppContext == null )
	//		{
	//			return false;
	//		}
	//		if( onIsShowSensor() )
	//		{
	//			if( super.onIsOpenSensor() )
	//			{
	//				return true;
	//			}
	//			else
	//			{
	//				xAngleStart = 0;
	//				yAngleStart = 0;
	//				xAngleEnd = 0;
	//				yAngleEnd = 0;
	//				xAngleLast = 0;
	//				yAngleLast = 0;
	//				mIsInitSensorTween = false;
	//				xAngleLastScrollTween = 0;
	//				yAngleLastScrollTween = 0;
	//				mIsScrollTween = false;
	//				mIsInitScrollTween = false;
	//				return false;
	//			}
	//		}
	//		else
	//		{
	//			return false;
	//		}
	//	}
	//	@Override
	//	public void setPosition(
	//			float x ,
	//			float y )
	//	{
	//		if( mAppContext == null )
	//		{
	//			return;
	//		}
	//		if( onIsShowSensor() )
	//		{
	//			if( !mIsScrollTween )
	//			{
	//				if( mIsInitSensorTween )
	//				{
	//					mIsInitSensorTween = false;
	//					return;
	//				}
	//			}
	//			else
	//			{
	//				if( mIsInitScrollTween )
	//				{
	//					mIsInitScrollTween = false;
	//					return;
	//				}
	//			}
	//			xAngleLast = x;
	//			yAngleLast = y;
	//			setRotationAngle( y , x , 0 );
	//			Gdx.graphics.requestRendering();
	//		}
	//	}
	@Override
	public void onDelete()
	{
		stopClockTimer();
		if( contenobserver != null )
		{
			mContext.getContentResolver().unregisterContentObserver( contenobserver );
		}
		if( datereceiver != null )
		{
			mContext.unregisterReceiver( datereceiver );
		}
		super.onDelete();
		if( clockbackviewregion != null )
		{
			clockbackviewregion.getTexture().dispose();
			clockbackviewregion = null;
		}
		if( clockhourregion != null )
		{
			clockhourregion.getTexture().dispose();
			clockhourregion = null;
		}
		if( clockminutrregion != null )
		{
			clockminutrregion.getTexture().dispose();
			clockminutrregion = null;
		}
		if( clocksecondregion != null )
		{
			clocksecondregion.getTexture().dispose();
			clocksecondregion = null;
		}
		if( clockpointregion != null )
		{
			clockpointregion.getTexture().dispose();
			clockpointregion = null;
		}
		if( weatherroundregion != null )
		{
			weatherroundregion.getTexture().dispose();
			weatherroundregion = null;
		}
		if( weathertopcityregion != null )
		{
			weathertopcityregion.getTexture().dispose();
			weathertopcityregion = null;
		}
		if( weatherbottomtmpregion != null )
		{
			weatherbottomtmpregion.getTexture().dispose();
			weatherbottomtmpregion = null;
		}
		if( weathershadowregion != null )
		{
			weathershadowregion.getTexture().dispose();
			weathershadowregion = null;
		}
		if( shadowtopregion != null )
		{
			shadowtopregion.getTexture().dispose();
			shadowtopregion = null;
		}
		if( shadowbottomregion != null )
		{
			shadowbottomregion.getTexture().dispose();
			shadowbottomregion = null;
		}
		if( weatherbuttonregion != null )
		{
			weatherbuttonregion.getTexture().dispose();
			weatherbuttonregion = null;
		}
		if( weathercurvebottomregion != null )
		{
			weathercurvebottomregion.getTexture().dispose();
			weathercurvebottomregion = null;
		}
		if( weathercurvetopregion != null )
		{
			weathercurvetopregion.getTexture().dispose();
			weathercurvetopregion = null;
		}
		dispose();
	}
	
	@Override
	public void onDestroy()
	{
		stopClockTimer();
		if( contenobserver != null )
		{
			mContext.getContentResolver().unregisterContentObserver( contenobserver );
		}
		if( datereceiver != null )
		{
			mContext.unregisterReceiver( datereceiver );
		}
		super.onDestroy();
		if( clockbackviewregion != null )
		{
			clockbackviewregion.getTexture().dispose();
			clockbackviewregion = null;
		}
		if( clockhourregion != null )
		{
			clockhourregion.getTexture().dispose();
			clockhourregion = null;
		}
		if( clockminutrregion != null )
		{
			clockminutrregion.getTexture().dispose();
			clockminutrregion = null;
		}
		if( clocksecondregion != null )
		{
			clocksecondregion.getTexture().dispose();
			clocksecondregion = null;
		}
		if( clockpointregion != null )
		{
			clockpointregion.getTexture().dispose();
			clockpointregion = null;
		}
		if( weatherroundregion != null )
		{
			weatherroundregion.getTexture().dispose();
			weatherroundregion = null;
		}
		if( weathertopcityregion != null )
		{
			weathertopcityregion.getTexture().dispose();
			weathertopcityregion = null;
		}
		if( weatherbottomtmpregion != null )
		{
			weatherbottomtmpregion.getTexture().dispose();
			weatherbottomtmpregion = null;
		}
		if( weathershadowregion != null )
		{
			weathershadowregion.getTexture().dispose();
			weathershadowregion = null;
		}
		if( shadowtopregion != null )
		{
			shadowtopregion.getTexture().dispose();
			shadowtopregion = null;
		}
		if( shadowbottomregion != null )
		{
			shadowbottomregion.getTexture().dispose();
			shadowbottomregion = null;
		}
		if( weatherbuttonregion != null )
		{
			weatherbuttonregion.getTexture().dispose();
			weatherbuttonregion = null;
		}
		if( weathercurvebottomregion != null )
		{
			weathercurvebottomregion.getTexture().dispose();
			weathercurvebottomregion = null;
		}
		if( weathercurvetopregion != null )
		{
			weathercurvetopregion.getTexture().dispose();
			weathercurvetopregion = null;
		}
		dispose();
	}
	
	@Override
	public void onUninstall()
	{
		super.onUninstall();
		stopClockTimer();
		if( contenobserver != null )
		{
			mContext.getContentResolver().unregisterContentObserver( contenobserver );
		}
		if( datereceiver != null )
		{
			mContext.unregisterReceiver( datereceiver );
		}
		if( clockbackviewregion != null )
		{
			clockbackviewregion.getTexture().dispose();
			clockbackviewregion = null;
		}
		if( clockhourregion != null )
		{
			clockhourregion.getTexture().dispose();
			clockhourregion = null;
		}
		if( clockminutrregion != null )
		{
			clockminutrregion.getTexture().dispose();
			clockminutrregion = null;
		}
		if( clocksecondregion != null )
		{
			clocksecondregion.getTexture().dispose();
			clocksecondregion = null;
		}
		if( clockpointregion != null )
		{
			clockpointregion.getTexture().dispose();
			clockpointregion = null;
		}
		if( weatherroundregion != null )
		{
			weatherroundregion.getTexture().dispose();
			weatherroundregion = null;
		}
		if( weathertopcityregion != null )
		{
			weathertopcityregion.getTexture().dispose();
			weathertopcityregion = null;
		}
		if( weatherbottomtmpregion != null )
		{
			weatherbottomtmpregion.getTexture().dispose();
			weatherbottomtmpregion = null;
		}
		if( weathershadowregion != null )
		{
			weathershadowregion.getTexture().dispose();
			weathershadowregion = null;
		}
		if( shadowtopregion != null )
		{
			shadowtopregion.getTexture().dispose();
			shadowtopregion = null;
		}
		if( shadowbottomregion != null )
		{
			shadowbottomregion.getTexture().dispose();
			shadowbottomregion = null;
		}
		if( weatherbuttonregion != null )
		{
			weatherbuttonregion.getTexture().dispose();
			weatherbuttonregion = null;
		}
		if( weathercurvebottomregion != null )
		{
			weathercurvebottomregion.getTexture().dispose();
			weathercurvebottomregion = null;
		}
		if( weathercurvetopregion != null )
		{
			weathercurvetopregion.getTexture().dispose();
			weathercurvetopregion = null;
		}
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
		Utils3D.showPidMemoryInfo( mAppContext.mContainerContext , "com.iLoong.WeatherClock" );
		//		maincontext = null;
		mAppContext = null;
		mContext = null;
		animation_click = null;
		animation_click_front = null;
		animation_click_back = null;
		animation_click_shadowup = null;
		animation_click_shadowdown = null;
		animation_return = null;
		animation_return_front = null;
		animation_return_back = null;
		animation_return_shadowdown = null;
		animation_return_shadowup = null;
		animation_flash = null;
		scrollTween = null;
		mClockTimer = null;
		mTweenTimer = null;
		clockviewgroup = null;
		clockhourview = null;
		clockminuteview = null;
		clocksecondview = null;
		clockshadow = null;
		weathershadow = null;
		weatherviewgroup = null;
		weathersemicirclegroup = null;
		upshadowgroup = null;
		downshadowgroup = null;
		if( clockbackview.region != null )
		{
			clockbackview.region.getTexture().dispose();
		}
		clockbackview = null;
		if( clockhour.region != null )
		{
			clockhour.region.getTexture().dispose();
		}
		clockhour = null;
		if( clockminute.region != null )
		{
			clockminute.region.getTexture().dispose();
		}
		clockminute = null;
		if( clocksecond.region != null )
		{
			clocksecond.region.getTexture().dispose();
		}
		clocksecond = null;
		if( clockpoint.region != null )
		{
			clockpoint.region.getTexture().dispose();
		}
		clockpoint = null;
		if( clockshawowview.region != null )
		{
			clockshawowview.region.getTexture().dispose();
		}
		clockshawowview = null;
		if( weatherbutton.region != null )
		{
			weatherbutton.region.getTexture().dispose();
		}
		weatherbutton = null;
		if( weathercurvebottom.region != null )
		{
			weathercurvebottom.region.getTexture().dispose();
		}
		weathercurvebottom = null;
		if( weathercurvetop.region != null )
		{
			weathercurvetop.region.getTexture().dispose();
		}
		weathercurvetop = null;
		if( weatherround.region != null )
		{
			weatherround.region.getTexture().dispose();
		}
		weatherround = null;
		if( weatherbottom.region != null )
		{
			weatherbottom.region.getTexture().dispose();
		}
		weatherbottom = null;
		if( upshadow.region != null )
		{
			upshadow.region.getTexture().dispose();
		}
		upshadow = null;
		if( downshadow.region != null )
		{
			downshadow.region.getTexture().dispose();
		}
		downshadow = null;
		if( weathertop.region != null )
		{
			weathertop.region.getTexture().dispose();
		}
		weathertop = null;
		if( weathertop1.region != null )
		{
			weathertop1.region.getTexture().dispose();
		}
		weathertop1 = null;
		if( weathercurvebottom1.region != null )
		{
			weathercurvebottom1.region.getTexture().dispose();
		}
		weathercurvebottom1 = null;
		weatherclock = null;
		mScrollTween = null;
		mAutoScrollTween = null;
		clockshadowTween = null;
		weathershadowTween = null;
		clockTween = null;
		weatherTween = null;
		contenobserver = null;
		datereceiver = null;
	}
	
	@Override
	public void tweenTimeChanged()
	{
		if( mAppContext != null )
		{
			mAppContext.mGdxApplication.postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					if( weatherbutton != null )
					{
						if( animation_flash != null )
						{
							animation_flash.free();
							animation_flash = null;
						}
						weatherbutton.color.a = 1;
						animation_flash = Timeline.createSequence();
						animation_flash.push( Tween.to( weatherbutton , View3DTweenAccessor.OPACITY , 0.5f ).target( 0.5f , 0 , 0 ).ease( Linear.INOUT ) );
						animation_flash.push( Tween.to( weatherbutton , View3DTweenAccessor.OPACITY , 0.5f ).target( 1 , 0 , 0 ).ease( Linear.INOUT ) );
						animation_flash.start( View3DTweenAccessor.manager ).setCallback( WidgetWeatherClock.getIntance() );
					}
				}
			} );
		}
	}
	
	@Override
	public void changeWidgetState(
			Object state )
	{
		if( (Integer)state == 0 )
		{
			isOnbackSide = false;
			onClockStateChanged( 0 );
			isScroolLocked = true;
			animalInService = true;
			mAutoScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( 0 ).delay( 0 ).setCallback( this ).start( View3DTweenAccessor.manager );
			lastRotationX = 0;
			if( isOnbackSide )
			{
				clockshadowTween = Tween.to( clockshadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weathershadowTween = Tween.to( weathershadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				clockTween = Tween.to( clockviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( weatherviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
			else
			{
				clockshadowTween = Tween.to( clockshadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weathershadowTween = Tween.to( weathershadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				clockTween = Tween.to( clockviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( weatherviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
		}
		else
		{
			isOnbackSide = true;
			onClockStateChanged( 1 );
			isScroolLocked = true;
			animalInService = true;
			mAutoScrollTween = Tween.to( this , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( 180 ).delay( 0 ).setCallback( this ).start( View3DTweenAccessor.manager );
			lastRotationX = 180;
			if( isOnbackSide )
			{
				clockshadowTween = Tween.to( clockshadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weathershadowTween = Tween.to( weathershadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				clockTween = Tween.to( clockviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( weatherviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
			else
			{
				clockshadowTween = Tween.to( clockshadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weathershadowTween = Tween.to( weathershadow , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				clockTween = Tween.to( clockviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( weatherviewgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
		}
		super.changeWidgetState( state );
	}
	
	@Override
	public void onChangeSize(
			float moveX ,
			float moveY ,
			int what ,
			int cellX ,
			int cellY )
	{
		MobclickAgent.onEvent( iLoongLauncher.getInstance() , "ChangeTheClockSize" );
		super.onChangeSize( moveX , moveY , what , cellX , cellY );
		float TempX = 0;
		float TempY = 0;
		if( moveX > 0 )
		{
			if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
			{
				TempX = moveX - moveX % R3D.Workspace_cell_each_width;
				this.width = this.width + TempX;
			}
			else
			{
				TempX = moveX - moveX % R3D.Workspace_cell_each_width + R3D.Workspace_cell_each_width;
				this.width = this.width + TempX;
			}
		}
		else
		{
			if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
			{
				TempX = Math.abs( moveX ) - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
				this.width = this.width - TempX;
			}
			else
			{
				TempX = Math.abs( moveX ) - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) + R3D.Workspace_cell_each_width;
				this.width = this.width - TempX;
			}
		}
		if( moveY > 0 )
		{
			if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
			{
				TempY = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
				this.height = this.height + TempY;
			}
			else
			{
				TempY = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
				this.height = this.height + TempY;
			}
		}
		else
		{
			if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
			{
				TempY = Math.abs( moveY ) - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
				this.height = this.height - TempY;
			}
			else
			{
				TempY = Math.abs( moveY ) - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
				this.height = this.height - TempY;
			}
		}
		float nowscaleX = 1f;
		float nowscaleY = 1f;
		switch( what )
		{
			case LeftMove:
				//				if( moveX > 0 )
				//				{
				nowscaleX = this.width / Utils3D.getScreenWidth();
				nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 2 );
				//				}
				//				else
				//				{
				//					nowscaleX = this.width / (715f*Parameter.scale);
				//					nowscaleY=this.height / (455f*Parameter.scale);
				//				}
				scale = Math.min( nowscaleX , nowscaleY ) * Math.min( Utils3D.getScreenWidth() / 720f , R3D.Workspace_cell_each_height / 240f );
				changePosition();
				break;
			case RightMove:
				nowscaleX = this.width / Utils3D.getScreenWidth();
				nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 2 );
				scale = Math.min( nowscaleX , nowscaleY ) * Math.min( Utils3D.getScreenWidth() / 720f , R3D.Workspace_cell_each_height / 240f );
				changePosition();
				break;
			case TopMove:
				//				if( moveY > 0 )
				//				{
				//					searchBarBg.move( 0 , TempY / 2 , 0f );
				//				}
				//				else
				//				{
				//					searchBarBg.move( 0 , -TempY / 2 , 0f );
				//				}
				nowscaleX = this.width / Utils3D.getScreenWidth();
				nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 2 );
				scale = Math.min( nowscaleX , nowscaleY ) * Math.min( Utils3D.getScreenWidth() / 720f , R3D.Workspace_cell_each_height / 240f );
				changePosition();
				break;
			case BottomMove:
				//				if( moveY > 0 )
				//				{
				//					searchBarBg.move( 0 , -TempY / 2 , 0f );
				//				}
				//				else
				//				{
				//					searchBarBg.move( 0 , TempY / 2 , 0f );
				//				}
				nowscaleX = this.width / Utils3D.getScreenWidth();
				nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 2 );
				scale = Math.min( nowscaleX , nowscaleY ) * Math.min( Utils3D.getScreenWidth() / 720f , R3D.Workspace_cell_each_height / 240f );
				changePosition();
				break;
			default:
				break;
		}
	}
	
	public void changePosition()
	{
		Parameter.changePoition();
		//		this.width = Parameter.WIDTH;
		//		this.height = Parameter.HEIGHT;
		this.setOrigin( this.width / 2 , this.height / 2 );
		this.setOriginZ( Parameter.WEATHER_CLOCK_ROTATION_Z );
		this.setRotationVector( 1 , 0 , 0 );
		clockbackview.build();
		clockbackview.move( this.width / 2 , this.height / 2 , Parameter.CLOCK_MOVE_Z );
		clockhour.build();
		clockhour.move( this.width / 2 , this.height / 2 , Parameter.CLOCK_MOVE_Z );
		clockhourview.setOrigin( this.width / 2 , this.height / 2 );
		clockhourview.setRotationVector( 0 , 0 , 1 );
		clockminute.build();
		clockminute.move( this.width / 2 , this.height / 2 , Parameter.CLOCK_MOVE_Z );
		clockminuteview.setOrigin( this.width / 2 , this.height / 2 );
		clockminuteview.setRotationVector( 0 , 0 , 1 );
		clocksecond.build();
		clocksecond.move( this.width / 2 , this.height / 2 , Parameter.CLOCK_MOVE_Z );
		clocksecondview.setOrigin( this.width / 2 , this.height / 2 );
		clocksecondview.setRotationVector( 0 , 0 , 1 );
		clockpoint.build();
		clockpoint.move( this.width / 2 , this.height / 2 , Parameter.CLOCK_MOVE_Z );
		clockshawowview.build();
		clockshawowview.move( this.width / 2 , this.height / 2 , Parameter.CLOCK_MOVE_Z );
		weatherround.build();
		weatherround.move( this.width / 2 , this.height / 2 , Parameter.WEATHER_MOVE_Z );
		weatherbutton.build();
		weatherbutton.move( this.width / 2 , this.height / 2 , Parameter.WEATHER_MOVE_Z );
		weathercurvetop.build();
		weathercurvetop.move( this.width / 2 , this.height / 2 , Parameter.WEATHER_MOVE_Z );
		weatherbottom.build();
		weatherbottom.move( this.width / 2 , this.height / 2 , Parameter.WEATHER_MOVE_Z );
		downshadow.build();
		downshadow.move( this.width / 2 , this.height / 2 , Parameter.WEATHER_MOVE_Z );
		upshadow.build();
		upshadow.move( this.width / 2 , this.height / 2 , Parameter.WEATHER_MOVE_Z );
		weathershawowview.build();
		weathershawowview.move( this.width / 2 , this.height / 2 , Parameter.WEATHER_MOVE_Z );
		weathertop1.build();
		weathertop1.move( this.width / 2 , this.height / 2 , Parameter.WEATHER_MOVE_Z );
		if( isOnReturn )
		{
			weathertop.show();
			weathertop1.show();
			weatherbottom.show();
			weathercurvetop.show();
			weathercurvebottom.show();
			weathercurvebottom1.show();
			upshadowgroup.color.a = 0;
			downshadowgroup.color.a = 0;
			if( animation_return != null )
			{
				animation_return.free();
				animation_return = null;
			}
			if( animation_return_front != null )
			{
				animation_return_front.free();
				animation_return_front = null;
			}
			if( animation_return_back != null )
			{
				animation_return_back.free();
				animation_return_back = null;
			}
			if( animation_return_shadowdown != null )
			{
				animation_return_shadowdown.free();
				animation_return_shadowdown = null;
			}
			if( animation_return_shadowup != null )
			{
				animation_return_shadowup.free();
				animation_return_shadowup = null;
			}
			ifReturnTween = true;
			animation_return = Timeline.createSequence();
			animation_return_front = Timeline.createParallel();
			animation_return_back = Timeline.createParallel();
			animation_return_shadowdown = Timeline.createSequence();
			animation_return_shadowup = Timeline.createSequence();
			weathersemicirclegroup.setOrigin( this.width / 2 , this.height / 2 );
			weathersemicirclegroup.setOriginZ( Parameter.WEATHER_SEMICIRCLE_ROTATION_Z );
			weathersemicirclegroup.setRotationVector( 1 , 0 , 0 );
			animation_return_front.push( Tween.to( weathersemicirclegroup , View3DTweenAccessor.ROTATION , 0.01f ).target( 90 , 0 , 0 ).ease( Quad.IN ) );
			animation_return_front.push( animation_return_shadowdown );
			animation_return_back.push( Tween.to( weathersemicirclegroup , View3DTweenAccessor.ROTATION , 0.01f ).target( 0 , 0 , 0 ).ease( Quad.OUT ) );
			animation_return_back.push( animation_return_shadowup );
			animation_return.push( animation_return_front );
			animation_return.push( animation_return_back );
			animation_return.start( View3DTweenAccessor.manager ).setCallback( this );
			weatherbutton.region = WeatherClockHelper.getRegion( mContext , "button.png" );
			weatherround.region = weatherroundregion;
			weathertop.build();
			weathertop.move( this.width / 2 , this.height / 2 , Parameter.WEATHER_MOVE_Z );
			weathertop.show();
			weathertop1.show();
			weatherbottom.show();
			weathercurvetop.show();
			weathercurvebottom.show();
			weathercurvebottom1.show();
			if( animation_click != null )
			{
				animation_click.free();
				animation_click = null;
			}
			if( animation_click_front != null )
			{
				animation_click_front.free();
				animation_click_front = null;
			}
			if( animation_click_back != null )
			{
				animation_click_back.free();
				animation_click_back = null;
			}
			if( animation_click_shadowup != null )
			{
				animation_click_shadowup.free();
				animation_click_shadowup = null;
			}
			if( animation_click_shadowdown != null )
			{
				animation_click_shadowdown.free();
				animation_click_shadowdown = null;
			}
			upshadowgroup.color.a = 0;
			downshadowgroup.color.a = 0;
			ifClickTween = true;
			animation_click = Timeline.createSequence();
			animation_click_front = Timeline.createParallel();
			animation_click_back = Timeline.createParallel();
			animation_click_shadowup = Timeline.createSequence();
			animation_click_shadowdown = Timeline.createSequence();
			weathersemicirclegroup.setOrigin( this.width / 2 , this.height / 2 );
			weathersemicirclegroup.setOriginZ( Parameter.WEATHER_SEMICIRCLE_ROTATION_Z );
			weathersemicirclegroup.setRotationVector( 1 , 0 , 0 );
			animation_click_front.push( Tween.to( weathersemicirclegroup , View3DTweenAccessor.ROTATION , 0.01f ).target( 90 , 0 , 0 ).ease( Quad.IN ) );
			animation_click_front.push( animation_click_shadowup );
			animation_click_back.push( Tween.to( weathersemicirclegroup , View3DTweenAccessor.ROTATION , 0.01f ).target( 180 , 0 , 0 ).ease( Quad.OUT ) );
			animation_click_back.push( animation_click_shadowdown );
			animation_click.push( animation_click_front );
			animation_click.push( animation_click_back );
			animation_click.start( View3DTweenAccessor.manager ).setCallback( this );
			weatherbutton.region = WeatherClockHelper.getRegion( mContext , "buttonback.png" );
		}
		else
		{
			weathertop.build();
			weathertop.move( this.width / 2 , this.height / 2 , Parameter.WEATHER_MOVE_Z );
		}
		weathercurvebottom1.build();
		weathercurvebottom1.move( this.width / 2 , this.height / 2 , Parameter.WEATHER_MOVE_Z );
		weathercurvebottom.build();
		weathercurvebottom.move( this.width / 2 , this.height / 2 , Parameter.WEATHER_MOVE_Z );
	}
}
