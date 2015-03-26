package com.iLoong.NumberClock.view;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.NumberClock.R;
import com.iLoong.NumberClock.Timer.ClockTimer;
import com.iLoong.NumberClock.Timer.ClockTimerListener;
import com.iLoong.NumberClock.common.NumberClockHelper;
import com.iLoong.NumberClock.common.Weather;
import com.iLoong.NumberClock.common.WeatherEntity;
import com.iLoong.NumberClock.common.WeatherForestEntity;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quint;


public class WidgetNumberClock extends WidgetPluginView3D implements ClockTimerListener
{
	
	// 3D模型统一宽度高度
	public static float MODEL_WIDTH = 320;
	public static float MODEL_HEIGHT = 320;
	public static CooGdx cooGdx;
	public MainAppContext mAppContext;
	private Context mContext = null;
	public static float scale = 1f;
	//	private WidgetThemeManager widgetthememanager = null;
	public final static String PATH = "theme/widget/com.iLoong.NumberClock/";
	public static final String IMGPATH = PATH + "theme/image/";
	public static final String WEATHERICONPATH = PATH + "theme/weather/";
	public static final String WEATHERICONSMALlPATH = PATH + "theme/weathersmall/";
	public static final String NUMPATH = PATH + "theme/number/";
	public static final float NumWidth = 62f;
	public static final float NumHight = 91f;
	public static final float ColonWidth = 31f;
	public static final float ColonHight = 91f;
	public static final float NumSpace = 5f;
	public final float WeatherWidth = 172f;
	public final float WeatherHeight = 123f;
	public final float WeatherX = 80f;
	public static final float TempWidth = 172f;
	public static final float TempHeight = 24f;
	public final float TempX = 80f;
	public final float Halving_lineWidth = 6f;
	public final float Halving_lineHeight = 137f;
	public final float Halving_lineX = 262f;
	public static final float TimeWidth = 299f;
	public static final float TimeHeight = 91f;
	//	public final float TimeX = 318f;
	public static final float DateWidth = 299f;
	public static final float DateHeight = 47f;
	public static final float AMPMWidth = 50f;
	public static final float AMPMHeight = 50f;
	//	public final float DateX = 318f;
	public final float CityWidth = 299f;
	public final float CityHeight = 47f;
	//	public final float CityX = 318f;
	public static final float WEATHERSMALLWidth = 53f;
	public static final float WEATHERSMALLHeight = 38f;
	public static final float RotationGroupWidth = 389f;
	public static final float RotationGroupHeight = 185f;
	
	public static final float CurveGroupWidth = 339f;
	public static final float CurveGroupHeight = 185f;
	
	//	public static final float RotationGroupX = 318f;
	public final float MOVEY = 40f;
	private DateReceiver datereceiver = null;
	public View3D weatherview = null;
	public View3D tempview = null;
	public View3D halving_lineview = null;
	public View3D timeview = null;
	public View3D amorpmview = null;
	public View3D dateview = null;
	public View3D cityview = null;
	public View3D curveview = null;
	public ViewGroup3D rotationgroup = null;
	public ViewGroup3D rotationfrontgroup = null;
	public ViewGroup3D rotationbackgroup = null;
	private int mHeadHour;
	private int mHeadMinute;
	private int mHeadYear;
	private int mHeadMonth;
	private int mHeadDay;
	private int mHeadWeek;
	private boolean is_24hour;
	private String amOrpm = "AM";
	// 时钟定时器
	private ClockTimer mClockTimer = null;
	//做动画需要的变量
	private boolean isTouchdownTrigered = false;
	// 滚动时钟记录当前位移
	private float mRotateX = 0;
	private float mRotateY = 0;
	// 时钟滑动动画
	private Tween mScrollTween = null;
	// 是否处理点击事件
	private boolean openDFClock = true;
	private Tween mAutoScrollTween = null;
	private Tween clockTween = null;
	private Tween weatherTween = null;
	boolean isfilling = false;
	private boolean isNeedRotation = false;
	public static boolean isOnbackSide = false;
	private boolean isScroolLocked = false;
	private float lastRotationX = 0;
	public boolean animalInService = false;
	private float deltaRotationX;
	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
	public static final int LeftMove = 1;
	public static final int RightMove = 2;
	public static final int TopMove = 3;
	public static final int BottomMove = 4;
	public TextureRegion curveRegion = null;
	public TextureRegion cityRegion = null;
	public static Handler updatehandle = null;
	public static final int MSG_UpdateInlandSUCCESS = 7;
	public static final int MSG_UpdateInlandFAILURE = 8;
	public static final int MSG_UpdateForSUCCESS = 9;
	public static final int MSG_UpdateForFAILURE = 10;
	public static boolean ifIsScrolling=false;
	public WidgetNumberClock(
			String name ,
			MainAppContext context ,
			int widgetId )
	{
		super( name );
		this.mContext = context.mWidgetContext;
		this.mAppContext = context;
		//		widgetthememanager = new WidgetThemeManager( context );
		cooGdx = new CooGdx( context.mGdxApplication );
		scale = Utils3D.getScreenWidth() / 720f;
		this.width = Utils3D.getScreenWidth();
		this.height = R3D.Workspace_cell_each_height * 2;
		this.setOrigin( this.width/2 , this.height/2 );
		isOnbackSide = false;
		sharedPref = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		Calendar mCalendar = Calendar.getInstance();
		mHeadHour = mCalendar.get( Calendar.HOUR_OF_DAY );
		mHeadMinute = mCalendar.get( Calendar.MINUTE );
		mHeadYear = mCalendar.get( Calendar.YEAR );
		mHeadMonth = mCalendar.get( Calendar.MONTH ) + 1;
		mHeadDay = mCalendar.get( Calendar.DAY_OF_MONTH );
		mHeadWeek = mCalendar.get( Calendar.DAY_OF_WEEK );
		is_24hour = android.text.format.DateFormat.is24HourFormat( mContext );
		if( !is_24hour )
		{
			if( mHeadHour > 11 )
			{
				if( mHeadHour != 12 )
				{
					mHeadHour -= 12;
				}
				amOrpm = "PM";
			}
			else
			{
				if( mHeadHour == 0 )
				{
					mHeadHour = 12;
				}
				amOrpm = "AM";
			}
		}
		rotationgroup = new ViewGroup3D( "rotationgroup" );
		rotationgroup.transform = true;
		rotationfrontgroup = new ViewGroup3D( "rotationfrontgroup" );
		rotationfrontgroup.transform = true;
		rotationbackgroup = new ViewGroup3D( "rotationbackgroup" );
		rotationbackgroup.transform = true;
		if( DefaultLayout.enable_google_version )
		{
			curveRegion = NumberClockHelper
					.drawCurveRegion( mAppContext , "27" , "27" , "31" , "31" , "31" , 31 , 30 , 34 , 32 , 29 , 28 , 27 , 30 , 31 , 25 , "Mon" , "Tues" , "Wed" , "Thur" , "Fri" );
			cityRegion = NumberClockHelper.drawCityRegion( mAppContext , "unknow" );
		}
		else
		{
			curveRegion = NumberClockHelper.drawCurveRegionInLand( mAppContext , "多云" , "多云" , "晴" , "晴" , "晴" , 31 , 30 , 34 , 32 , 29 , 28 , 27 , 30 , 31 , 25 , 1 , 2 , 3 , 4 , 5 );
			cityRegion = NumberClockHelper.drawCityRegion( mAppContext , "未知" );
		}
		weatherview = new View3D( "weatherview" , NumberClockHelper.getRegion( mContext , WEATHERICONPATH , "sunny.png" ) ) {
			
			@Override
			public boolean onClick(
					float x ,
					float y )
			{
				if( checkValidity() > 0 )
				{
					return super.onClick( x , y );
				}
				if( !DefaultLayout.enable_google_version )
				{
					Intent intent = new Intent();
					intent.setComponent( new ComponentName( mContext , "com.iLoong.NumberClock.view.NumberCityFindInLand" ) );
//					intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
					intent.setAction( "com.iLoong.cityfinderinland.MAIN" );
					mContext.startActivity( intent );
				}
				else
				{
					Intent intent = new Intent();
					intent.setComponent( new ComponentName( mContext , "com.iLoong.NumberClock.view.NumberCityFinderActivity" ) );
//					intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
					intent.setAction( "com.iLoong.cityfinder.MAIN" );
					mContext.startActivity( intent );
				}
				return true;
			}
		};
		weatherview.setSize( WeatherWidth * scale , WeatherHeight * scale );
		weatherview.setPosition( this.width / 2 - 0.3889f * this.width , ( this.height - weatherview.height ) / 2 + Halving_lineHeight * scale * 0.2f + MOVEY * scale );
		this.addView( weatherview );
		tempview = new View3D( "tempview" , NumberClockHelper.drawTmpRegion( "31" , "28" ) );
		tempview.setSize( TempWidth * scale , TempHeight * scale );
		tempview.setPosition( this.width / 2 - 0.3889f * this.width , ( this.height - tempview.height ) / 2 - Halving_lineHeight * scale * 0.6f + MOVEY * scale );
		this.addView( tempview );
		halving_lineview = new View3D( "halving_lineview" , NumberClockHelper.getRegion( mContext , IMGPATH , "halving_line.png" ) );
		halving_lineview.setSize( Halving_lineWidth * scale , Halving_lineHeight * scale );
		halving_lineview.setPosition( this.width / 2 - 0.1361f * this.width , ( this.height - halving_lineview.height ) / 2 + MOVEY * scale );
		this.addView( halving_lineview );
		addRotationViewGroup();
		startClockTimer();
		datereceiver = new DateReceiver();
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction( Intent.ACTION_TIME_TICK );
		intentfilter.addAction( "com.iLoong.numberclock.forsearch" );
		intentfilter.addAction( "com.iLoong.numberclock.inlandsearch" );
		intentfilter.addAction( NumberWeatherDataService.UPDATE_RESULT );
		mContext.registerReceiver( datereceiver , intentfilter );
		if( DefaultLayout.enable_google_version )
		{
			Weather weather = getWeather();
			if( weather != null && weather.getList() != null && weather.getList().size() == 5 )
			{
				changeRegion( weather , sharedPref.getString( "numbertmpType" , "f" ) );
			}
		}
		else
		{
			WeatherEntity weatherentity = getWeatherInland();
			if( weatherentity != null && weatherentity.getDetails() != null && weatherentity.getDetails().size() == 5 )
			{
				changeRegionInland( weatherentity );
			}
		}
		Intent intent = new Intent( iLoongLauncher.getInstance() , NumberWeatherDataService.class );
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
							case MSG_UpdateInlandSUCCESS:
								Bundle bundle = (Bundle)msg.obj;
								WeatherEntity weatherentity = (WeatherEntity)bundle.getSerializable( "weatherupdatedataentity" );
								if( weatherentity != null && weatherentity.getDetails() != null && weatherentity.getDetails().size() == 5 )
								{
									Log.d( "mytag" , "数字时钟进入到国内更新数据发送广播" );
									DoWhenInLandSuccess( weatherentity );
								}
								break;
							case MSG_UpdateInlandFAILURE:
								Log.d( "mytag" , "数字时钟进入到国内突然断网" );
								Toast.makeText( iLoongLauncher.getInstance() , iLoongLauncher.getInstance().getString( R.string.networt_notconnetederror ) , Toast.LENGTH_SHORT ).show();
								break;
							case MSG_UpdateForSUCCESS:
								Bundle bundles = (Bundle)msg.obj;
								Weather weathers = (Weather)bundles.getSerializable( "weatherinfoupdate" );
								if( weathers != null && weathers.getList() != null && weathers.getList().size() == 5 )
								{
									Log.d( "mytag" , "数字时钟进入到国外更新数据发送广播" );
									DoWhenForSuccess( weathers );
								}
								break;
							case MSG_UpdateForFAILURE:
								Log.d( "mytag" , "数字时钟进入到国外突然断网" );
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
	
	public void ChangeResize()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		if( sp.getBoolean( "com.iLoong.NumberClock" , false ) )
		{
			int spanX = sp.getInt( "com.iLoong.NumberClock" + ":spanX" , -1 );
			int spanY = sp.getInt( "com.iLoong.NumberClock" + ":spanY" , -1 );
			if( spanX != -1 && spanY != -1 )
			{
				this.width = spanX * R3D.Workspace_cell_each_width;
				this.height = spanY * R3D.Workspace_cell_each_height;
				this.setOrigin( this.width/2 , this.height/2 );
				float nowscaleX = this.width / Utils3D.getScreenWidth();
				float nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 2 );
				scale = Math.min( nowscaleX , nowscaleY ) * Math.min( Utils3D.getScreenWidth() / 720f , R3D.Workspace_cell_each_height / 240f );
				changePosition();
			}
		}
	}
	
	public Weather getWeather()
	{
		if( sharedPref.getBoolean( "numberweatherstate" , false ) )
		{
			Weather weather = new Weather();
			weather.setWeathercity( sharedPref.getString( "numberweathercityname" , null ) );
			weather.setWeathercode( sharedPref.getString( "numberweathercode" , null ) );
			weather.setWeathercondition( sharedPref.getString( "numberweathercondition" , null ) );
			weather.setCurrtmp( sharedPref.getString( "numberweathercurrenttmp" , null ) );
			weather.setShidu( sharedPref.getString( "numberweathershidu" , null ) );
			List<Weather> list = new ArrayList<Weather>();
			Weather weather01 = new Weather();
			weather01.setWeathercode( sharedPref.getString( "numberlistweathercode0" , null ) );
			weather01.setHightmp( sharedPref.getString( "numberlistweatherhighTmp0" , null ) );
			weather01.setLowtmp( sharedPref.getString( "numberlistweatherlowTmp0" , null ) );
			weather01.setWeatherweek( sharedPref.getString( "numberlistweatherweek0" , null ) );
			list.add( weather01 );
			Weather weather02 = new Weather();
			weather02.setWeathercode( sharedPref.getString( "numberlistweathercode1" , null ) );
			weather02.setHightmp( sharedPref.getString( "numberlistweatherhighTmp1" , null ) );
			weather02.setLowtmp( sharedPref.getString( "numberlistweatherlowTmp1" , null ) );
			weather02.setWeatherweek( sharedPref.getString( "numberlistweatherweek1" , null ) );
			list.add( weather02 );
			Weather weather03 = new Weather();
			weather03.setWeathercode( sharedPref.getString( "numberlistweathercode2" , null ) );
			weather03.setHightmp( sharedPref.getString( "numberlistweatherhighTmp2" , null ) );
			weather03.setLowtmp( sharedPref.getString( "numberlistweatherlowTmp2" , null ) );
			weather03.setWeatherweek( sharedPref.getString( "numberlistweatherweek2" , null ) );
			list.add( weather03 );
			Weather weather04 = new Weather();
			weather04.setWeathercode( sharedPref.getString( "numberlistweathercode3" , null ) );
			weather04.setHightmp( sharedPref.getString( "numberlistweatherhighTmp3" , null ) );
			weather04.setLowtmp( sharedPref.getString( "numberlistweatherlowTmp3" , null ) );
			weather04.setWeatherweek( sharedPref.getString( "numberlistweatherweek3" , null ) );
			list.add( weather04 );
			Weather weather05 = new Weather();
			weather05.setWeathercode( sharedPref.getString( "numberlistweathercode4" , null ) );
			weather05.setHightmp( sharedPref.getString( "numberlistweatherhighTmp4" , null ) );
			weather05.setLowtmp( sharedPref.getString( "numberlistweatherlowTmp4" , null ) );
			weather05.setWeatherweek( sharedPref.getString( "numberlistweatherweek4" , null ) );
			list.add( weather05 );
			weather.setList( list );
			return weather;
		}
		return null;
	}
	
	public WeatherEntity getWeatherInland()
	{
		if( sharedPref.getBoolean( "inlandnumberweatherstate" , false ) )
		{
			WeatherEntity weatherentity = new WeatherEntity();
			weatherentity.setCity( sharedPref.getString( "inlandnumberweathercityname" , null ) );
			weatherentity.setCondition( sharedPref.getString( "inlandnumberweathercondition" , null ) );
			ArrayList<WeatherForestEntity> list = new ArrayList<WeatherForestEntity>();
			WeatherForestEntity weather01 = new WeatherForestEntity();
			weather01.setCondition( sharedPref.getString( "inlandnumberlistweathercode0" , null ) );
			weather01.setHight( sharedPref.getString( "inlandnumberlistweatherhighTmp0" , null ) );
			weather01.setLow( sharedPref.getString( "inlandnumberlistweatherlowTmp0" , null ) );
			weather01.setDayOfWeek( sharedPref.getInt( "inlandnumberlistweatherweek0" , 0 ) );
			list.add( weather01 );
			WeatherForestEntity weather02 = new WeatherForestEntity();
			weather02.setCondition( sharedPref.getString( "inlandnumberlistweathercode1" , null ) );
			weather02.setHight( sharedPref.getString( "inlandnumberlistweatherhighTmp1" , null ) );
			weather02.setLow( sharedPref.getString( "inlandnumberlistweatherlowTmp1" , null ) );
			weather02.setDayOfWeek( sharedPref.getInt( "inlandnumberlistweatherweek1" , 0 ) );
			list.add( weather02 );
			WeatherForestEntity weather03 = new WeatherForestEntity();
			weather03.setCondition( sharedPref.getString( "inlandnumberlistweathercode2" , null ) );
			weather03.setHight( sharedPref.getString( "inlandnumberlistweatherhighTmp2" , null ) );
			weather03.setLow( sharedPref.getString( "inlandnumberlistweatherlowTmp2" , null ) );
			weather03.setDayOfWeek( sharedPref.getInt( "inlandnumberlistweatherweek2" , 0 ) );
			list.add( weather03 );
			WeatherForestEntity weather04 = new WeatherForestEntity();
			weather04.setCondition( sharedPref.getString( "inlandnumberlistweathercode3" , null ) );
			weather04.setHight( sharedPref.getString( "inlandnumberlistweatherhighTmp3" , null ) );
			weather04.setLow( sharedPref.getString( "inlandnumberlistweatherlowTmp3" , null ) );
			weather04.setDayOfWeek( sharedPref.getInt( "inlandnumberlistweatherweek3" , 0 ) );
			list.add( weather04 );
			WeatherForestEntity weather05 = new WeatherForestEntity();
			weather05.setCondition( sharedPref.getString( "inlandnumberlistweathercode4" , null ) );
			weather05.setHight( sharedPref.getString( "inlandnumberlistweatherhighTmp4" , null ) );
			weather05.setLow( sharedPref.getString( "inlandnumberlistweatherlowTmp4" , null ) );
			weather05.setDayOfWeek( sharedPref.getInt( "inlandnumberlistweatherweek4" , 0 ) );
			list.add( weather05 );
			weatherentity.setDetails( list );
			return weatherentity;
		}
		return null;
	}
	
	public void addRotationViewGroup()
	{
		rotationgroup.setSize( RotationGroupWidth * scale , RotationGroupHeight * scale );
		rotationgroup.setPosition( this.width / 2 - this.width * 0.1278f , ( this.height - rotationgroup.height ) / 2 + MOVEY * scale );
		rotationgroup.setOrigin( rotationgroup.width / 2 , rotationgroup.height / 2 );
		rotationgroup.setOriginZ( -40f * scale );
		this.addView( rotationgroup );
		rotationfrontgroup.setSize( RotationGroupWidth * scale , RotationGroupHeight * scale );
		rotationfrontgroup.setPosition( 0 , 0 );
		rotationfrontgroup.setZ( 0f );
		amorpmview = new View3D( "amorpmview" , NumberClockHelper.drawAMORPMRegion( amOrpm )){
			@Override
			public boolean onClick(
					float x ,
					float y )
			{
				if( checkValidity() > 0 )
				{
					return super.onClick( x , y );
				}
				if( !WidgetNumberClock.isOnbackSide )
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
							SharedPreferences p = mContext.getSharedPreferences( "iLoong.Widget.Clock" , 0 );
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
				}
				return true;
			}
		};
		amorpmview.setSize( AMPMWidth * scale , AMPMHeight * scale );
		amorpmview.setPosition( 0 , ( rotationfrontgroup.height - amorpmview.height ) / 2 + Halving_lineHeight * scale * 1 / 2 );
		if( is_24hour )
		{
			amorpmview.hide();
		}
		else
		{
			amorpmview.show();
		}
		rotationfrontgroup.addView( amorpmview );
		timeview = new View3D( "timeview" , NumberClockHelper.drawTimeRegion( mAppContext , mHeadHour , mHeadMinute )){
			@Override
			public boolean onClick(
					float x ,
					float y )
			{
				if( checkValidity() > 0 )
				{
					return super.onClick( x , y );
				}
				if( !WidgetNumberClock.isOnbackSide )
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
							SharedPreferences p = mContext.getSharedPreferences( "iLoong.Widget.Clock" , 0 );
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
				}
				return true;
			}
		};
		timeview.setSize( TimeWidth * scale , TimeHeight * scale );
		timeview.setPosition( 60 * scale , ( rotationfrontgroup.height - timeview.height ) / 2 + Halving_lineHeight * scale * 1 / 4 );
		rotationfrontgroup.addView( timeview );
		dateview = new View3D( "dateview" , NumberClockHelper.drawDateRegion( mHeadWeek , mHeadMonth , mHeadDay , mHeadYear ));
		dateview.setSize( DateWidth * scale , DateHeight * scale );
		dateview.setPosition( 60 * scale , ( rotationfrontgroup.height - dateview.height ) / 2 - Halving_lineHeight * scale * 0.35f );
		rotationfrontgroup.addView( dateview );
		cityview = new View3D( "cityview" , cityRegion){
			@Override
			public boolean onClick(
					float x ,
					float y )
			{
				if( checkValidity() > 0 )
				{
					return super.onClick( x , y );
				}
				if( !WidgetNumberClock.isOnbackSide )
				{
					if( !DefaultLayout.enable_google_version )
					{
						Intent intent = new Intent();
						intent.setComponent( new ComponentName( mContext , "com.iLoong.NumberClock.view.NumberCityFindInLand" ) );
//						intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
						intent.setAction( "com.iLoong.cityfinderinland.MAIN" );
						mContext.startActivity( intent );
					}
					else
					{
						Intent intent = new Intent();
						intent.setComponent( new ComponentName( mContext , "com.iLoong.NumberClock.view.NumberCityFinderActivity" ) );
//						intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
						intent.setAction( "com.iLoong.cityfinder.MAIN" );
						mContext.startActivity( intent );
					}
				}
				return true;
			}
		};
		cityview.setSize( CityWidth * scale , CityHeight * scale );
		cityview.setPosition( 60 * scale , ( rotationfrontgroup.height - cityview.height ) / 2 - Halving_lineHeight * scale * 0.6f );
		rotationfrontgroup.addView( cityview );
		rotationbackgroup.setSize( RotationGroupWidth * scale , RotationGroupHeight * scale );
		rotationbackgroup.setPosition( 0 , 0 );
		rotationbackgroup.setZ( -80f * scale );
		rotationbackgroup.setOrigin( rotationbackgroup.width / 2 , rotationbackgroup.height / 2 );
		rotationbackgroup.setRotationVector( 1 , 0 , 0 );
		rotationbackgroup.setRotationAngle( 180 , 0 , 0 );
		curveview = new View3D( "curveview" , curveRegion );
		curveview.setSize( CurveGroupWidth*scale , CurveGroupHeight*scale );
		curveview.setPosition( 50 * scale , 0 );
		rotationbackgroup.addView( curveview );
		rotationgroup.addView( rotationbackgroup );
		rotationbackgroup.color.a = 0;
		rotationgroup.addView( rotationfrontgroup );
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
				if( hour == 18 && minute == 0 )
				{
					if( DefaultLayout.enable_google_version )
					{
						mAppContext.mGdxApplication.postRunnable( new Runnable() {
							
							@Override
							public void run()
							{
								Weather weather = getWeather();
								if( weather != null && weather.getList() != null && weather.getList().size() == 5 )
								{
									if( weather.getWeathercode() != null )
									{
										if( weatherview.region != null )
										{
											weatherview.region.getTexture().dispose();
											weatherview.region = null;
										}
										weatherview.region = NumberClockHelper.getRegion( mContext , WEATHERICONPATH , NumberClockHelper.codeForPath( weather.getWeathercode() ) );
									}
								}
							}
						} );
					}
					else
					{
						mAppContext.mGdxApplication.postRunnable( new Runnable() {
							
							@Override
							public void run()
							{
								WeatherEntity weatherinland = getWeatherInland();
								if( weatherinland != null && weatherinland.getDetails() != null && weatherinland.getDetails().size() == 5 )
								{
									if( weatherinland.getCondition() != null )
									{
										if( weatherview.region != null )
										{
											weatherview.region.getTexture().dispose();
											weatherview.region = null;
										}
										weatherview.region = NumberClockHelper.getRegion( mContext , WEATHERICONPATH , NumberClockHelper.StringForPath( weatherinland.getCondition() ) );
									}
								}
							}
						} );
					}
				}
				else if( hour == 0 && minute == 0 )
				{
					if( DefaultLayout.enable_google_version )
					{
						mAppContext.mGdxApplication.postRunnable( new Runnable() {
							
							@Override
							public void run()
							{
								Weather weather = getWeather();
								if( weather != null && weather.getList() != null && weather.getList().size() == 5 )
								{
									if( weather.getList().get( 1 ).getHightmp() != null && weather.getList().get( 1 ).getLowtmp() != null )
									{
										if( weather.getWeathercode() != null )
										{
											if( weatherview.region != null )
											{
												weatherview.region.getTexture().dispose();
												weatherview.region = null;
											}
											weatherview.region = NumberClockHelper.getRegion( mContext , WEATHERICONPATH , NumberClockHelper.codeForPath( weather.getWeathercode() ) );
										}
										if( tempview.region != null )
										{
											tempview.region.getTexture().dispose();
											tempview.region = null;
										}
										tempview.region = NumberClockHelper.drawTmpRegion( weather.getList().get( 1 ).getHightmp() , weather.getList().get( 1 ).getLowtmp() );
									}
								}
							}
						} );
					}
					else
					{
						mAppContext.mGdxApplication.postRunnable( new Runnable() {
							
							@Override
							public void run()
							{
								WeatherEntity weatherinland = getWeatherInland();
								if( weatherinland != null && weatherinland.getDetails() != null && weatherinland.getDetails().size() == 5 )
								{
									if( weatherinland.getCondition() != null )
									{
										if( weatherview.region != null )
										{
											weatherview.region.getTexture().dispose();
											weatherview.region = null;
										}
										weatherview.region = NumberClockHelper.getRegion( mContext , WEATHERICONPATH , NumberClockHelper.StringForPath( weatherinland.getCondition() ) );
									}
									if( weatherinland.getDetails().get( 1 ).getHight() != null && weatherinland.getDetails().get( 1 ).getLow() != null )
									{
										if( tempview.region != null )
										{
											tempview.region.getTexture().dispose();
											tempview.region = null;
										}
										tempview.region = NumberClockHelper.drawTmpRegion( weatherinland.getDetails().get( 1 ).getHight() , weatherinland.getDetails().get( 1 ).getLow() );
									}
								}
							}
						} );
					}
				}
			}
			else if( action.equals( "com.iLoong.numberclock.forsearch" ) )
			{
				Bundle bundle = intent.getExtras();
				Weather weather = (Weather)bundle.getSerializable( "numberweatherinfo" );
				String tmpType = sharedPref.getString( "numbertmpType" , "f" );
				if( weather != null && weather.getList() != null && weather.getList().size() == 5 )
				{
					changeRegion( weather , tmpType );
					Editor ed = sharedPref.edit();
					ed.putBoolean( "numberweatherstate" , true );
					ed.putString( "numberweathercityname" , weather.getWeathercity() );
					ed.putString( "numberweathercode" , weather.getWeathercode() );
					ed.putString( "numberweathercondition" , weather.getWeathercondition() );
					ed.putString( "numberweathercurrenttmp" , weather.getCurrtmp() );
					ed.putString( "numberweathershidu" , weather.getShidu() );
					ed.putString( "numberlistweathercode0" , weather.getList().get( 0 ).getWeathercode() );
					ed.putString( "numberlistweatherhighTmp0" , weather.getList().get( 0 ).getHightmp() );
					ed.putString( "numberlistweatherlowTmp0" , weather.getList().get( 0 ).getLowtmp() );
					ed.putString( "numberlistweatherweek0" , weather.getList().get( 0 ).getWeatherweek() );
					ed.putString( "numberlistweathercode1" , weather.getList().get( 1 ).getWeathercode() );
					ed.putString( "numberlistweatherhighTmp1" , weather.getList().get( 1 ).getHightmp() );
					ed.putString( "numberlistweatherlowTmp1" , weather.getList().get( 1 ).getLowtmp() );
					ed.putString( "numberlistweatherweek1" , weather.getList().get( 1 ).getWeatherweek() );
					ed.putString( "numberlistweathercode2" , weather.getList().get( 2 ).getWeathercode() );
					ed.putString( "numberlistweatherhighTmp2" , weather.getList().get( 2 ).getHightmp() );
					ed.putString( "numberlistweatherlowTmp2" , weather.getList().get( 2 ).getLowtmp() );
					ed.putString( "numberlistweatherweek2" , weather.getList().get( 2 ).getWeatherweek() );
					ed.putString( "numberlistweathercode3" , weather.getList().get( 3 ).getWeathercode() );
					ed.putString( "numberlistweatherhighTmp3" , weather.getList().get( 3 ).getHightmp() );
					ed.putString( "numberlistweatherlowTmp3" , weather.getList().get( 3 ).getLowtmp() );
					ed.putString( "numberlistweatherweek3" , weather.getList().get( 3 ).getWeatherweek() );
					ed.putString( "numberlistweathercode4" , weather.getList().get( 4 ).getWeathercode() );
					ed.putString( "numberlistweatherhighTmp4" , weather.getList().get( 4 ).getHightmp() );
					ed.putString( "numberlistweatherlowTmp4" , weather.getList().get( 4 ).getLowtmp() );
					ed.putString( "numberlistweatherweek4" , weather.getList().get( 4 ).getWeatherweek() );
					ed.commit();
				}
			}
			else if( action.equals( "com.iLoong.numberclock.inlandsearch" ) )
			{
				Bundle inlandbundle = intent.getExtras();
				WeatherEntity inlandweather = (WeatherEntity)inlandbundle.getSerializable( "weatherdataentity" );
				if( inlandweather != null && inlandweather.getDetails() != null && inlandweather.getDetails().size() == 5 )
				{
					changeRegionInland( inlandweather );
					Editor ed = sharedPref.edit();
					ed.putBoolean( "inlandnumberweatherstate" , true );
					ed.putString( "inlandnumberweathercityname" , inlandweather.getCity() );
					ed.putString( "inlandnumberweathercondition" , inlandweather.getCondition() );
					ed.putString( "inlandnumberlistweathercode0" , inlandweather.getDetails().get( 0 ).getCondition() );
					ed.putString( "inlandnumberlistweatherhighTmp0" , inlandweather.getDetails().get( 0 ).getHight() );
					ed.putString( "inlandnumberlistweatherlowTmp0" , inlandweather.getDetails().get( 0 ).getLow() );
					ed.putInt( "inlandnumberlistweatherweek0" , inlandweather.getDetails().get( 0 ).getDayOfWeek() );
					ed.putString( "inlandnumberlistweathercode1" , inlandweather.getDetails().get( 1 ).getCondition() );
					ed.putString( "inlandnumberlistweatherhighTmp1" , inlandweather.getDetails().get( 1 ).getHight() );
					ed.putString( "inlandnumberlistweatherlowTmp1" , inlandweather.getDetails().get( 1 ).getLow() );
					ed.putInt( "inlandnumberlistweatherweek1" , inlandweather.getDetails().get( 1 ).getDayOfWeek() );
					ed.putString( "inlandnumberlistweathercode2" , inlandweather.getDetails().get( 2 ).getCondition() );
					ed.putString( "inlandnumberlistweatherhighTmp2" , inlandweather.getDetails().get( 2 ).getHight() );
					ed.putString( "inlandnumberlistweatherlowTmp2" , inlandweather.getDetails().get( 2 ).getLow() );
					ed.putInt( "inlandnumberlistweatherweek2" , inlandweather.getDetails().get( 2 ).getDayOfWeek() );
					ed.putString( "inlandnumberlistweathercode3" , inlandweather.getDetails().get( 3 ).getCondition() );
					ed.putString( "inlandnumberlistweatherhighTmp3" , inlandweather.getDetails().get( 3 ).getHight() );
					ed.putString( "inlandnumberlistweatherlowTmp3" , inlandweather.getDetails().get( 3 ).getLow() );
					ed.putInt( "inlandnumberlistweatherweek3" , inlandweather.getDetails().get( 3 ).getDayOfWeek() );
					ed.putString( "inlandnumberlistweathercode4" , inlandweather.getDetails().get( 4 ).getCondition() );
					ed.putString( "inlandnumberlistweatherhighTmp4" , inlandweather.getDetails().get( 4 ).getHight() );
					ed.putString( "inlandnumberlistweatherlowTmp4" , inlandweather.getDetails().get( 4 ).getLow() );
					ed.putInt( "inlandnumberlistweatherweek4" , inlandweather.getDetails().get( 4 ).getDayOfWeek() );
					ed.commit();
				}
			}
			else if( action.equals( NumberWeatherDataService.UPDATE_RESULT ) )
			{
				String str = intent.getStringExtra( "cooee.numberweather.updateResult" );
				if( str.equals( "UPDATE_SUCCESED" ) )
				{
					if( DefaultLayout.enable_google_version )
					{
						changeRegion( getWeather() , sharedPref.getString( "numbertmpType" , "f" ) );
					}
					else
					{
						changeRegionInland( getWeatherInland() );
					}
				}
			}
		}
	}
	
	public void DoWhenInLandSuccess(
			WeatherEntity inlandweather )
	{
		if( inlandweather != null && inlandweather.getDetails() != null && inlandweather.getDetails().size() == 5 )
		{
			changeRegionInland( inlandweather );
			Editor ed = sharedPref.edit();
			ed.putBoolean( "inlandnumberweatherstate" , true );
			ed.putString( "inlandnumberweathercityname" , inlandweather.getCity() );
			ed.putString( "inlandnumberweathercondition" , inlandweather.getCondition() );
			ed.putString( "inlandnumberlistweathercode0" , inlandweather.getDetails().get( 0 ).getCondition() );
			ed.putString( "inlandnumberlistweatherhighTmp0" , inlandweather.getDetails().get( 0 ).getHight() );
			ed.putString( "inlandnumberlistweatherlowTmp0" , inlandweather.getDetails().get( 0 ).getLow() );
			ed.putInt( "inlandnumberlistweatherweek0" , inlandweather.getDetails().get( 0 ).getDayOfWeek() );
			ed.putString( "inlandnumberlistweathercode1" , inlandweather.getDetails().get( 1 ).getCondition() );
			ed.putString( "inlandnumberlistweatherhighTmp1" , inlandweather.getDetails().get( 1 ).getHight() );
			ed.putString( "inlandnumberlistweatherlowTmp1" , inlandweather.getDetails().get( 1 ).getLow() );
			ed.putInt( "inlandnumberlistweatherweek1" , inlandweather.getDetails().get( 1 ).getDayOfWeek() );
			ed.putString( "inlandnumberlistweathercode2" , inlandweather.getDetails().get( 2 ).getCondition() );
			ed.putString( "inlandnumberlistweatherhighTmp2" , inlandweather.getDetails().get( 2 ).getHight() );
			ed.putString( "inlandnumberlistweatherlowTmp2" , inlandweather.getDetails().get( 2 ).getLow() );
			ed.putInt( "inlandnumberlistweatherweek2" , inlandweather.getDetails().get( 2 ).getDayOfWeek() );
			ed.putString( "inlandnumberlistweathercode3" , inlandweather.getDetails().get( 3 ).getCondition() );
			ed.putString( "inlandnumberlistweatherhighTmp3" , inlandweather.getDetails().get( 3 ).getHight() );
			ed.putString( "inlandnumberlistweatherlowTmp3" , inlandweather.getDetails().get( 3 ).getLow() );
			ed.putInt( "inlandnumberlistweatherweek3" , inlandweather.getDetails().get( 3 ).getDayOfWeek() );
			ed.putString( "inlandnumberlistweathercode4" , inlandweather.getDetails().get( 4 ).getCondition() );
			ed.putString( "inlandnumberlistweatherhighTmp4" , inlandweather.getDetails().get( 4 ).getHight() );
			ed.putString( "inlandnumberlistweatherlowTmp4" , inlandweather.getDetails().get( 4 ).getLow() );
			ed.putInt( "inlandnumberlistweatherweek4" , inlandweather.getDetails().get( 4 ).getDayOfWeek() );
			ed.commit();
		}
	}
	
	public void DoWhenForSuccess(
			Weather weather )
	{
		String tmpType = sharedPref.getString( "numbertmpType" , "f" );
		if( weather != null && weather.getList() != null && weather.getList().size() == 5 )
		{
			changeRegion( weather , tmpType );
			Editor ed = sharedPref.edit();
			ed.putBoolean( "numberweatherstate" , true );
			ed.putString( "numberweathercityname" , weather.getWeathercity() );
			ed.putString( "numberweathercode" , weather.getWeathercode() );
			ed.putString( "numberweathercondition" , weather.getWeathercondition() );
			ed.putString( "numberweathercurrenttmp" , weather.getCurrtmp() );
			ed.putString( "numberweathershidu" , weather.getShidu() );
			ed.putString( "numberlistweathercode0" , weather.getList().get( 0 ).getWeathercode() );
			ed.putString( "numberlistweatherhighTmp0" , weather.getList().get( 0 ).getHightmp() );
			ed.putString( "numberlistweatherlowTmp0" , weather.getList().get( 0 ).getLowtmp() );
			ed.putString( "numberlistweatherweek0" , weather.getList().get( 0 ).getWeatherweek() );
			ed.putString( "numberlistweathercode1" , weather.getList().get( 1 ).getWeathercode() );
			ed.putString( "numberlistweatherhighTmp1" , weather.getList().get( 1 ).getHightmp() );
			ed.putString( "numberlistweatherlowTmp1" , weather.getList().get( 1 ).getLowtmp() );
			ed.putString( "numberlistweatherweek1" , weather.getList().get( 1 ).getWeatherweek() );
			ed.putString( "numberlistweathercode2" , weather.getList().get( 2 ).getWeathercode() );
			ed.putString( "numberlistweatherhighTmp2" , weather.getList().get( 2 ).getHightmp() );
			ed.putString( "numberlistweatherlowTmp2" , weather.getList().get( 2 ).getLowtmp() );
			ed.putString( "numberlistweatherweek2" , weather.getList().get( 2 ).getWeatherweek() );
			ed.putString( "numberlistweathercode3" , weather.getList().get( 3 ).getWeathercode() );
			ed.putString( "numberlistweatherhighTmp3" , weather.getList().get( 3 ).getHightmp() );
			ed.putString( "numberlistweatherlowTmp3" , weather.getList().get( 3 ).getLowtmp() );
			ed.putString( "numberlistweatherweek3" , weather.getList().get( 3 ).getWeatherweek() );
			ed.putString( "numberlistweathercode4" , weather.getList().get( 4 ).getWeathercode() );
			ed.putString( "numberlistweatherhighTmp4" , weather.getList().get( 4 ).getHightmp() );
			ed.putString( "numberlistweatherlowTmp4" , weather.getList().get( 4 ).getLowtmp() );
			ed.putString( "numberlistweatherweek4" , weather.getList().get( 4 ).getWeatherweek() );
			ed.commit();
		}
	}
	
	public void changeRegion(
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
						Log.d( "mytag" , "数字时钟国外更新数据成功" );
						if( weather.getWeathercode() != null )
						{
							if( weatherview.region != null )
							{
								weatherview.region.getTexture().dispose();
								weatherview.region = null;
							}
							weatherview.region = NumberClockHelper.getRegion( mContext , WEATHERICONPATH , NumberClockHelper.codeForPath( weather.getWeathercode() ) );
						}
						if( weather.getWeathercity() != null )
						{
							if( cityview.region != null )
							{
								cityview.region.getTexture().dispose();
								cityview.region = null;
							}
							cityview.region = NumberClockHelper.drawCityRegion( mAppContext , weather.getWeathercity() );
						}
						if( weather.getList().get( 0 ).getHightmp() != null && weather.getList().get( 0 ).getLowtmp() != null )
						{
							if( tempview.region != null )
							{
								tempview.region.getTexture().dispose();
								tempview.region = null;
							}
							tempview.region = NumberClockHelper.drawTmpRegion( weather.getList().get( 0 ).getHightmp() , weather.getList().get( 0 ).getLowtmp() );
						}
						if( curveview.region != null )
						{
							curveview.region.getTexture().dispose();
							curveview.region = null;
						}
						curveview.region = NumberClockHelper.drawCurveRegion(
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
								Integer.parseInt( weather.getList().get( 4 ).getHightmp() ) ,
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
		} );
	}
	
	public void changeRegionInland(
			final WeatherEntity weatherinland )
	{
		mAppContext.mGdxApplication.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				if( weatherinland != null )
				{
					if( weatherinland.getDetails() != null && weatherinland.getDetails().size() == 5 )
					{
						Log.d( "mytag" , "数字时钟国内更新数据成功" );
						if( weatherinland.getCondition() != null )
						{
							if( weatherview.region != null )
							{
								weatherview.region.getTexture().dispose();
								weatherview.region = null;
							}
							weatherview.region = NumberClockHelper.getRegion( mContext , WEATHERICONPATH , NumberClockHelper.StringForPath( weatherinland.getCondition() ) );
						}
						if( weatherinland.getCity() != null )
						{
							if( cityview.region != null )
							{
								cityview.region.getTexture().dispose();
								cityview.region = null;
							}
							cityview.region = NumberClockHelper.drawCityRegion( mAppContext , weatherinland.getCity() );
						}
						if( weatherinland.getDetails().get( 0 ).getHight() != null && weatherinland.getDetails().get( 0 ).getLow() != null )
						{
							if( tempview.region != null )
							{
								tempview.region.getTexture().dispose();
								tempview.region = null;
							}
							tempview.region = NumberClockHelper.drawTmpRegion( weatherinland.getDetails().get( 0 ).getHight() , weatherinland.getDetails().get( 0 ).getLow() );
						}
						if( curveview.region != null )
						{
							curveview.region.getTexture().dispose();
							curveview.region = null;
						}
						curveview.region = NumberClockHelper.drawCurveRegionInLand(
								mAppContext ,
								weatherinland.getDetails().get( 0 ).getCondition() ,
								weatherinland.getDetails().get( 1 ).getCondition() ,
								weatherinland.getDetails().get( 2 ).getCondition() ,
								weatherinland.getDetails().get( 3 ).getCondition() ,
								weatherinland.getDetails().get( 4 ).getCondition() ,
								Integer.parseInt( weatherinland.getDetails().get( 0 ).getHight() ) ,
								Integer.parseInt( weatherinland.getDetails().get( 1 ).getHight() ) ,
								Integer.parseInt( weatherinland.getDetails().get( 2 ).getHight() ) ,
								Integer.parseInt( weatherinland.getDetails().get( 3 ).getHight() ) ,
								Integer.parseInt( weatherinland.getDetails().get( 4 ).getHight() ) ,
								Integer.parseInt( weatherinland.getDetails().get( 0 ).getLow() ) ,
								Integer.parseInt( weatherinland.getDetails().get( 1 ).getLow() ) ,
								Integer.parseInt( weatherinland.getDetails().get( 2 ).getLow() ) ,
								Integer.parseInt( weatherinland.getDetails().get( 3 ).getLow() ) ,
								Integer.parseInt( weatherinland.getDetails().get( 4 ).getLow() ) ,
								weatherinland.getDetails().get( 0 ).getDayOfWeek() ,
								weatherinland.getDetails().get( 1 ).getDayOfWeek() ,
								weatherinland.getDetails().get( 2 ).getDayOfWeek() ,
								weatherinland.getDetails().get( 3 ).getDayOfWeek() ,
								weatherinland.getDetails().get( 4 ).getDayOfWeek() );
					}
				}
			}
		} );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( checkValidity() == 1 )
		{
			return super.onTouchDown( x , y , pointer );
		}
		isTouchdownTrigered = true;
		mRotateX = 0;
		mRotateY = 0;
		if( isOnbackSide )
		{
			rotationbackgroup.color.a = 1;
			rotationfrontgroup.color.a = 0;
		}
		else
		{
			rotationbackgroup.color.a = 0;
			rotationfrontgroup.color.a = 1;
		}
		if( mScrollTween != null && !mScrollTween.isFinished() )
		{
			mScrollTween.free();
			mScrollTween = null;
		}
		if( openDFClock )
		{
			if( mAutoScrollTween != null && !mAutoScrollTween.isFinished() )
			{
				mAutoScrollTween.free();
				mAutoScrollTween = null;
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
			rotationgroup.rotation = 0;
		}
		isfilling = false;
		this.requestFocus();
		return true;
	}
	
	@Override
	public void onResume()
	{
		startClockTimer();
		super.onResume();
	}
	
	@Override
	public void onStop()
	{
		stopClockTimer();
		super.onStop();
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( checkValidity() > 0 )
		{
			return super.onClick( x , y );
		}
		if(ifIsScrolling){
			return true;
		}
		if( isOnbackSide && ifCanScroll( x , y ) )
		{
			if( !DefaultLayout.enable_google_version )
			{
				Intent intent = new Intent();
				intent.setComponent( new ComponentName( mContext , "com.iLoong.NumberClock.view.NumberCityFindInLand" ) );
//				intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				intent.setAction( "com.iLoong.cityfinderinland.MAIN" );
				mContext.startActivity( intent );
			}
			else
			{
				Intent intent = new Intent();
				intent.setComponent( new ComponentName( mContext , "com.iLoong.NumberClock.view.NumberCityFinderActivity" ) );
//				intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				intent.setAction( "com.iLoong.cityfinder.MAIN" );
				mContext.startActivity( intent );
			}
			return true;
		}
		return super.onClick( x , y );
	}
	
	public void startAutoEffect()
	{
		if( !isScroolLocked )
		{
			if( ( rotationgroup.rotation < 90 && rotationgroup.rotation != 0 && rotationgroup.rotation != 360 ) || ( rotationgroup.rotation > 90 && rotationgroup.rotation < 180 ) || ( isOnbackSide && ( rotationgroup.rotation > 180 && rotationgroup.rotation < 270 ) ) )
			{
				float deltaTime = 1.2f;
				if( lastRotationX < 180 )
					deltaTime = ( ( Math.abs( rotationgroup.rotation ) + 90 ) % 90 ) / 100.0f;
				else
					deltaTime = ( Math.abs( ( 180 - Math.abs( rotationgroup.rotation ) ) ) % 90 ) / 100.0f;
				animalInService = true;
				mAutoScrollTween = Tween.to( rotationgroup , View3DTweenAccessor.ROTATION , deltaTime ).ease( Linear.INOUT ).target( lastRotationX ).delay( 0 ).start( View3DTweenAccessor.manager )
						.setCallback( this );
				if( isOnbackSide )
				{
					if( deltaTime - 0.1f <= 0 )
					{
						clockTween = Tween.to( rotationfrontgroup , View3DTweenAccessor.OPACITY , 0 ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager )
								.setCallback( this );
					}
					else
					{
						clockTween = Tween.to( rotationfrontgroup , View3DTweenAccessor.OPACITY , deltaTime - 0.1f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager )
								.setCallback( this );
					}
					weatherTween = Tween.to( rotationbackgroup , View3DTweenAccessor.OPACITY , deltaTime ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager )
							.setCallback( this );
				}
				else
				{
					clockTween = Tween.to( rotationfrontgroup , View3DTweenAccessor.OPACITY , deltaTime ).ease( Linear.INOUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager )
							.setCallback( this );
					if( deltaTime - 0.1f <= 0 )
					{
						weatherTween = Tween.to( rotationbackgroup , View3DTweenAccessor.OPACITY , 0f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager )
								.setCallback( this );
					}
					else
					{
						weatherTween = Tween.to( rotationbackgroup , View3DTweenAccessor.OPACITY , deltaTime - 0.1f ).ease( Linear.INOUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager )
								.setCallback( this );
					}
				}
			}
			else if( !isOnbackSide && ( rotationgroup.rotation > 270 && rotationgroup.rotation < 360 ) )
			{
				float deltaTime = ( ( Math.abs( 360 - rotationgroup.rotation ) ) % 90 ) / 100.0f;
				animalInService = true;
				mAutoScrollTween = Tween.to( rotationgroup , View3DTweenAccessor.ROTATION , deltaTime ).ease( Linear.INOUT ).target( 360 ).delay( 0 ).start( View3DTweenAccessor.manager )
						.setCallback( this );
			}
		}
		isScroolLocked = false;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( checkValidity() == 1 )
		{
			this.releaseFocus();
			return super.onTouchUp( x , y , pointer );
		}
		isTouchdownTrigered = false;
		isNeedRotation = false;
		if( !isfilling )
		{
			float cur = 0;
			if( rotationgroup.rotation <= 180 )
			{
				cur = 0;
			}
			else
			{
				cur = 360;
			}
			if( openDFClock )
			{
				startAutoEffect();
			}
			else
			{
				mScrollTween = Tween.to( rotationgroup , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( cur ).delay( 0 ).start( View3DTweenAccessor.manager );
			}
			this.releaseFocus();
		}
		return false;
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
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
		if( ( k == 0 || k > 1.7 || k < -1.7 ) && ifCanScroll( x , y ) )
		{
			isNeedRotation = true;
		}
		else if( !isNeedRotation )
		{
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
		isfilling = false;
		ifIsScrolling=true;
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
			mAutoScrollTween = Tween.to( rotationgroup , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( lastRotationX + 180 ).delay( 0 ).setCallback( this )
					.start( View3DTweenAccessor.manager );
			lastRotationX = ( lastRotationX + 180 ) % 360;
			if( isOnbackSide )
			{
				clockTween = Tween.to( rotationfrontgroup , View3DTweenAccessor.OPACITY , 0.4f ).ease( Quint.OUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( rotationbackgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Quint.OUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
			else
			{
				clockTween = Tween.to( rotationfrontgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Quint.OUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( rotationbackgroup , View3DTweenAccessor.OPACITY , 0.4f ).ease( Quint.OUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
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
			mAutoScrollTween = Tween.to( rotationgroup , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( ( lastRotationX + 180 ) % 360 ).delay( 0 ).setCallback( this )
					.start( View3DTweenAccessor.manager );
			lastRotationX = ( lastRotationX + 180 ) % 360;
			if( isOnbackSide )
			{
				clockTween = Tween.to( rotationfrontgroup , View3DTweenAccessor.OPACITY , 0.4f ).ease( Quint.OUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( rotationbackgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Quint.OUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
			else
			{
				clockTween = Tween.to( rotationfrontgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Quint.OUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( rotationbackgroup , View3DTweenAccessor.OPACITY , 0.4f ).ease( Quint.OUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
		}
		else
		{
			if( !isScroolLocked )
			{
				rotationgroup.setRotationAngle( lastRotationX + mRotateY / height * 360 , 0 , 0 );
				float judget = mRotateY / height * 360;
				if( lastRotationX == 0 && judget > 0 && judget < 90 )
				{
					rotationfrontgroup.color.a = 1 - ( mRotateY / height * 360 ) / 180f;
					rotationbackgroup.color.a = ( mRotateY / height * 360 ) / 180f;
				}
				else if( lastRotationX == 0 && judget < 0 && judget > -90 )
				{
					rotationfrontgroup.color.a = 1 - Math.abs( mRotateY / height * 360 ) / 180f;
					rotationbackgroup.color.a = Math.abs( mRotateY / height * 360 ) / 180f;
				}
				else if( lastRotationX == 180 && judget > 0 && judget < 90 )
				{
					rotationbackgroup.color.a = 1 - ( mRotateY / height * 360 ) / 180f;
					rotationfrontgroup.color.a = ( mRotateY / height * 360 ) / 180f;
				}
				else if( lastRotationX == 180 && judget < 0 && judget > -90 )
				{
					rotationbackgroup.color.a = 1 - Math.abs( mRotateY / height * 360 ) / 180f;
					rotationfrontgroup.color.a = Math.abs( mRotateY / height * 360 ) / 180f;
				}
			}
			else
			{
			}
		}
		return true;
	}
	
	@Override
	public void onEvent(
			int type ,
			@SuppressWarnings( "rawtypes" ) BaseTween source )
	{
		if( source.equals( mAutoScrollTween ) && type == TweenCallback.COMPLETE )
		{
			ifIsScrolling=false;
			animalInService = false;
			if( isOnbackSide )
			{
				rotationbackgroup.color.a = 1;
				rotationfrontgroup.color.a = 0;
			}
			else
			{
				rotationbackgroup.color.a = 0;
				rotationfrontgroup.color.a = 1;
			}
		}
		else if( source.equals( clockTween ) && type == TweenCallback.COMPLETE )
		{
		}
		else if( source.equals( weatherTween ) && type == TweenCallback.COMPLETE )
		{
		}
		else if( source.equals( mScrollTween ) && type == TweenCallback.COMPLETE )
		{
		}
	}
	
	public static int TIME_HOUR = 0;
	public static int TIME_MINUTE = 0;
	public static int TIME_WEEK = 0;
	public static int TIME_YEAR = 0;
	public static int TIME_MONTH = 0;
	public static int TIME_DAY = 0;
	public static boolean TIME_IS24 = true;
	
	@Override
	public void clockTimeChanged()
	{
		Calendar mCalendar = Calendar.getInstance();
		long milliseconds = System.currentTimeMillis();
		mCalendar.setTimeInMillis( milliseconds );
		mHeadHour = mCalendar.get( Calendar.HOUR_OF_DAY );
		mHeadMinute = mCalendar.get( Calendar.MINUTE );
		mHeadYear = mCalendar.get( Calendar.YEAR );
		mHeadMonth = mCalendar.get( Calendar.MONTH ) + 1;
		mHeadDay = mCalendar.get( Calendar.DAY_OF_MONTH );
		mHeadWeek = mCalendar.get( Calendar.DAY_OF_WEEK );
		is_24hour = android.text.format.DateFormat.is24HourFormat( mContext );
		mAppContext.mGdxApplication.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				if( ( TIME_HOUR != mHeadHour ) || ( TIME_MINUTE != mHeadMinute ) || ( TIME_WEEK != mHeadWeek ) || ( TIME_YEAR != mHeadYear ) || ( TIME_MONTH != mHeadMonth ) || ( TIME_DAY != mHeadDay ) || ( TIME_IS24 != is_24hour ) )
				{
					TIME_HOUR = mHeadHour;
					TIME_MINUTE = mHeadMinute;
					TIME_WEEK = mHeadWeek;
					TIME_YEAR = mHeadYear;
					TIME_MONTH = mHeadMonth;
					TIME_DAY = mHeadDay;
					TIME_IS24 = is_24hour;
					//					if( !ifIsStartTween )
					//					{
					loadClock();
					//					}
				}
			}
		} );
	}
	
	private void loadClock()
	{
		Calendar mCalendar = Calendar.getInstance();
		int mHeadHour = mCalendar.get( Calendar.HOUR_OF_DAY );
		int mHeadMinute = mCalendar.get( Calendar.MINUTE );
		int mYear = mCalendar.get( Calendar.YEAR );
		int mMonth = mCalendar.get( Calendar.MONTH ) + 1;
		int mDay = mCalendar.get( Calendar.DAY_OF_MONTH );
		int mWeek = mCalendar.get( Calendar.DAY_OF_WEEK );
		boolean is_24hour = android.text.format.DateFormat.is24HourFormat( mContext );
		if( !is_24hour )
		{
			if( mHeadHour > 11 )
			{
				if( mHeadHour != 12 )
				{
					mHeadHour -= 12;
				}
				amOrpm = "PM";
			}
			else
			{
				if( mHeadHour == 0 )
				{
					mHeadHour = 12;
				}
				amOrpm = "AM";
			}
		}
		if( amorpmview != null )
		{
			if( amorpmview.region != null )
			{
				amorpmview.region.getTexture().dispose();
				amorpmview.region = null;
			}
			amorpmview.region = NumberClockHelper.drawAMORPMRegion( amOrpm );
			if( is_24hour )
			{
				amorpmview.hide();
			}
			else
			{
				amorpmview.show();
			}
		}
		if( timeview != null )
		{
			if( timeview.region != null )
			{
				timeview.region.getTexture().dispose();
				timeview.region = null;
			}
			timeview.region = NumberClockHelper.drawTimeRegion( mAppContext , mHeadHour , mHeadMinute );
		}
		if( dateview != null )
		{
			if( dateview.region != null )
			{
				dateview.region.getTexture().dispose();
				dateview.region = null;
			}
			dateview.region = NumberClockHelper.drawDateRegion( mWeek , mMonth , mDay , mYear );
		}
		if( weatherview != null )
		{
			if( DefaultLayout.enable_google_version )
			{
				mAppContext.mGdxApplication.postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						Weather weather = getWeather();
						if( weather != null )
						{
							if( weather.getList() != null && weather.getList().size() == 5 )
							{
								if( weather.getWeathercode() != null )
								{
									if( weatherview.region != null )
									{
										weatherview.region.getTexture().dispose();
										weatherview.region = null;
									}
									weatherview.region = NumberClockHelper.getRegion( mContext , WEATHERICONPATH , NumberClockHelper.codeForPath( weather.getWeathercode() ) );
								}
							}
						}
					}
				} );
			}
			else
			{
				mAppContext.mGdxApplication.postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						WeatherEntity weatherinland = getWeatherInland();
						if( weatherinland != null && weatherinland.getDetails() != null && weatherinland.getDetails().size() == 5 )
						{
							if( weatherinland.getCondition() != null )
							{
								if( weatherview.region != null )
								{
									weatherview.region.getTexture().dispose();
									weatherview.region = null;
								}
								weatherview.region = NumberClockHelper.getRegion( mContext , WEATHERICONPATH , NumberClockHelper.StringForPath( weatherinland.getCondition() ) );
							}
						}
					}
				} );
			}
		}
	}
	
	public void startClockTimer()
	{
		clockTimeChanged();
		mClockTimer = new ClockTimer( this , 1000 );
		mClockTimer.start();
	}
	
	public void stopClockTimer()
	{
		// 定时器类型为Timer
		if( mClockTimer != null )
		{
			mClockTimer.stop();
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
		super.draw( batch , parentAlpha );
	}
	
	public boolean ifCanScroll(
			float x ,
			float y )
	{
		if( x > this.originX - this.width * 0.1278f && x < this.originX + this.width / 2 )
		{
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( checkValidity() > 0 )
		{
			return true;
		}
		this.point.x = x;
		this.point.y = y;
		this.toAbsolute( point );
		Vector2 obj = new Vector2( point.x , point.y );
		this.setTag( obj );
		this.releaseFocus();
		return viewParent.onCtrlEvent( this , 0 );
	}
	
	@Override
	public void onChangeSize(
			float moveX ,
			float moveY ,
			int what ,
			int cellX ,
			int cellY )
	{
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
				nowscaleX = this.width / Utils3D.getScreenWidth();
				nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 2 );
				scale = Math.min( nowscaleX , nowscaleY ) * ( Utils3D.getScreenWidth() / 720f );
				changePosition();
				break;
			case RightMove:
				nowscaleX = this.width / Utils3D.getScreenWidth();
				nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 2 );
				scale = Math.min( nowscaleX , nowscaleY ) * ( Utils3D.getScreenWidth() / 720f );
				changePosition();
				break;
			case TopMove:
				nowscaleX = this.width / Utils3D.getScreenWidth();
				nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 2 );
				scale = Math.min( nowscaleX , nowscaleY ) * ( Utils3D.getScreenWidth() / 720f );
				changePosition();
				break;
			case BottomMove:
				nowscaleX = this.width / Utils3D.getScreenWidth();
				nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 2 );
				scale = Math.min( nowscaleX , nowscaleY ) * ( Utils3D.getScreenWidth() / 720f );
				changePosition();
				break;
			default:
				break;
		}
	}
	
	public void changePosition()
	{
		
		this.setOrigin( this.width/2 , this.height/2 );
		
		weatherview.setSize( WeatherWidth * scale , WeatherHeight * scale );
		weatherview.setPosition( this.width / 2 - this.width * 0.3889f , ( this.height - weatherview.height ) / 2 + Halving_lineHeight * scale * 0.2f + MOVEY * scale );
		tempview.setSize( TempWidth * scale , TempHeight * scale );
		tempview.setPosition( this.width / 2 - this.width * 0.3889f , ( this.height - tempview.height ) / 2 - Halving_lineHeight * scale * 0.6f + MOVEY * scale );
		halving_lineview.setSize( Halving_lineWidth * scale , Halving_lineHeight * scale );
		halving_lineview.setPosition( this.width / 2 - this.width * 0.1361f , ( this.height - halving_lineview.height ) / 2 + MOVEY * scale );
		
		rotationgroup.setSize( RotationGroupWidth * scale , RotationGroupHeight * scale );
		rotationgroup.setPosition( this.width / 2 - this.width * 0.1278f , ( this.height - rotationgroup.height ) / 2 + MOVEY * scale );
		rotationgroup.setOrigin( rotationgroup.width / 2 , rotationgroup.height / 2 );
		rotationgroup.setOriginZ( -40f * scale );
		rotationfrontgroup.setSize( RotationGroupWidth * scale , RotationGroupHeight * scale );
		rotationfrontgroup.setPosition( 0 , 0 );
		rotationfrontgroup.setZ( 0f );
		
		amorpmview.setSize( AMPMWidth * scale , AMPMHeight * scale );
		amorpmview.setPosition( 0 , ( rotationfrontgroup.height - amorpmview.height ) / 2 + Halving_lineHeight * scale * 1 / 2 );
		if( is_24hour )
		{
			amorpmview.hide();
		}
		else
		{
			amorpmview.show();
		}
		timeview.setSize( TimeWidth * scale , TimeHeight * scale );
		timeview.setPosition( 60 * scale , ( rotationfrontgroup.height - timeview.height ) / 2 + Halving_lineHeight * scale * 1 / 4 );
		dateview.setSize( DateWidth * scale , DateHeight * scale );
		dateview.setPosition( 60 * scale , ( rotationfrontgroup.height - dateview.height ) / 2 - Halving_lineHeight * scale * 0.35f );
		cityview.setSize( CityWidth * scale , CityHeight * scale );
		cityview.setPosition( 60 * scale , ( rotationfrontgroup.height - cityview.height ) / 2 - Halving_lineHeight * scale * 0.6f );
		rotationbackgroup.setSize( RotationGroupWidth * scale , RotationGroupHeight * scale );
		rotationbackgroup.setZ( -80f * scale );
		rotationbackgroup.setOrigin( rotationbackgroup.width / 2 , rotationbackgroup.height / 2 );
		curveview.setSize( CurveGroupWidth*scale , CurveGroupHeight*scale );
		curveview.setPosition( 50 * scale , 0 );
		Gdx.graphics.requestRendering();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		stopClockTimer();
		if( datereceiver != null )
		{
			mContext.unregisterReceiver( datereceiver );
		}
		dispose();
	}
	
	@Override
	public void onDelete()
	{
		super.onDelete();
		stopClockTimer();
		if( datereceiver != null )
		{
			mContext.unregisterReceiver( datereceiver );
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
		datereceiver = null;
		if( weatherview.region != null )
		{
			weatherview.region.getTexture().dispose();
		}
		weatherview = null;
		if( tempview.region != null )
		{
			tempview.region.getTexture().dispose();
		}
		tempview = null;
		if( halving_lineview.region != null )
		{
			halving_lineview.region.getTexture().dispose();
		}
		halving_lineview = null;
		if( timeview.region != null )
		{
			timeview.region.getTexture().dispose();
		}
		timeview = null;
		if( amorpmview.region != null )
		{
			amorpmview.region.getTexture().dispose();
		}
		amorpmview = null;
		if( dateview.region != null )
		{
			dateview.region.getTexture().dispose();
		}
		dateview = null;
		if( cityview.region != null )
		{
			cityview.region.getTexture().dispose();
		}
		cityview = null;
		if( curveview.region != null )
		{
			curveview.region.getTexture().dispose();
		}
		curveview = null;
		rotationgroup = null;
		rotationfrontgroup = null;
		rotationbackgroup = null;
		mClockTimer = null;
		mScrollTween = null;
		mAutoScrollTween = null;
		clockTween = null;
		weatherTween = null;
		if( curveRegion != null )
		{
			curveRegion.getTexture().dispose();
		}
		curveRegion = null;
		if( cityRegion != null )
		{
			cityRegion.getTexture().dispose();
		}
		cityRegion = null;
		updatehandle = null;
	}
	
	@Override
	public void onClockStateChanged(
			int state )
	{
		super.onClockStateChanged( state );
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
			mAutoScrollTween = Tween.to( rotationgroup , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( 0 ).delay( 0 ).setCallback( this ).start( View3DTweenAccessor.manager );
			lastRotationX = 0;
			if( isOnbackSide )
			{
				clockTween = Tween.to( rotationfrontgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Quint.OUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( rotationbackgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Quint.OUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
			else
			{
				clockTween = Tween.to( rotationfrontgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Quint.OUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( rotationbackgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Quint.OUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
		}
		else
		{
			isOnbackSide = true;
			onClockStateChanged( 1 );
			isScroolLocked = true;
			animalInService = true;
			mAutoScrollTween = Tween.to( rotationgroup , View3DTweenAccessor.ROTATION , 1.2f ).ease( Quint.OUT ).target( 180 ).delay( 0 ).setCallback( this ).start( View3DTweenAccessor.manager );
			lastRotationX = 180;
			if( isOnbackSide )
			{
				clockTween = Tween.to( rotationfrontgroup , View3DTweenAccessor.OPACITY , 0.4f ).ease( Quint.OUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( rotationbackgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Quint.OUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
			else
			{
				clockTween = Tween.to( rotationfrontgroup , View3DTweenAccessor.OPACITY , 0.8f ).ease( Quint.OUT ).target( 1 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
				weatherTween = Tween.to( rotationbackgroup , View3DTweenAccessor.OPACITY , 0.4f ).ease( Quint.OUT ).target( 0 ).delay( 0 ).start( View3DTweenAccessor.manager ).setCallback( this );
			}
		}
		super.changeWidgetState( state );
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
	
}
