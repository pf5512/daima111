package com.iLoong.Calender.view;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.cooee.android.launcher.framework.IconCache;
import com.iLoong.Calendar.R;
import com.iLoong.Calender.common.CalendarAllEvents;
import com.iLoong.Calender.common.CalenderHelper;
import com.iLoong.Calender.common.MyBitmapPacker;
import com.iLoong.Calender.common.Parameter;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.umeng.analytics.MobclickAgent;


public class WidgetCalender extends WidgetPluginView3D
{
	
	public MainAppContext mAppContext;
	private Context mContext = null;
	public CooGdx cooGdx;
	private MainAppContext maincontext = null;
	public static WidgetCalender widgetcalender;
	public static float scale = 1f;
	public static float height_scale = 1f;
	public float offset = 1f;
	private float origionTopMoveWidth;
	private float origionTopMoveHeight;
	private float origionTop01MoveWidth;
	private float origionTop01MoveHeight;
	private float origionDownMoveWidth;
	private float origionDownMoveHeight;
	private float origionWeekMoveWidth;
	private float origionWeekMoveHeight;
	private float origionDayMoveWidth;
	private float origionDayMoveHeight;
	private float origionBackMoveWidth;
	private float origionBackMoveHeight;
	private float origionMonthMoveWidth;
	private float origionMonthMoveHeight;
	private float origionDay1MoveWidth;
	private float origionDay1MoveHeight;
	private float origionDay2MoveWidth;
	private float origionDay2MoveHeight;
	private float origionDaythMoveWidth;
	private float origionDaythMoveHeight;
	private float origionYearMoveWidth;
	private float origionYearMoveHeight;
	private float origionDayPlaneMoveWidth;
	private float origionDayPlaneMoveHeight;
	private PluginViewObject3D calendertop = null;
	private PluginViewObject3D calenderdown = null;
	private View3D calenderback = null;
	private View3D calendermonth = null;
	private View3D calenderday1 = null;
	private View3D calenderday2 = null;
	private View3D calenderdayth = null;
	private View3D calenderyear = null;
	public BackgroundGroup calenderalltop = null;
	public BackgroundGroup calenderalldown = null;
	private Timeline timelineclickin = null;
	private Timeline timelineclickout = null;
	//翻转动画
	private Timeline animation_rotation = null;
	private Timeline timelineback = null;
	private Timeline timelinebackout = null;
	private String[] days = CalenderHelper.caculateDay( 5 , 2014 );
	private int nowMonth;
	private int nowYear;
	private int nowDay;
	private DateReceiver datereceiver;
	private List<View3D> daypitchlistqian = new ArrayList<View3D>();
	private List<View3D> daypitchlisthou = new ArrayList<View3D>();
	private List<MyDayGroup3D> dayupviewgroup = new ArrayList<MyDayGroup3D>();
	private List<MyDayGroup3D> daydownviewgroup = new ArrayList<MyDayGroup3D>();
	private List<DayGroup3D> daygroup = new ArrayList<DayGroup3D>();
	public static boolean ifstartRotateTween = false;
	public static boolean ifstartTween = false;
	public static boolean ifstartclickbacktimeline = false;
	private float nowWidth;
	private float nowHeight;
	private float leftLowerCornerWidth;
	private float leftLowerCornerHeight;
	private MyBitmapPacker packer = null;
	private static TextureAtlas packerAtlas = null;
	private boolean ifBack = false;
	private DayGlasses calenderdayplane = null;
	private final int STATUS_UP = 0;
	private final int STATUS_DOWN = 1;
	private int m_status = STATUS_UP;
	private boolean m_changeOrder;
	private boolean m_clickBack = false;
	public static final int  LeftMove=1;
	public static final int  RightMove=2;
	public static final int  TopMove=3;
	public static final int  BottomMove=4;
	//	public static float TempPositionX = 0;
	//	public static float TempPositionY = 0;
	public static final WidgetCalender getIntance()
	{
		return widgetcalender;
	}
	
	private TextureRegion texcalendar_region = null;
	private TextureRegion texcalendarday_region = null;
	private TextureRegion texbackan_region = null;
	private TextureRegion texback_region = null;
	private TextureRegion texday_region = null;
	private TextureRegion texdayliang_region = null;
	private TextureRegion texxing_region = null;
	private TextureRegion textoday_region = null;
	private TextureRegion texxuanzhong_region = null;
	public static TextureRegion texjiahao_region = null;
	private Handler mHandler = null;
	private static final int MSG_SUCCESS = 0;//获取图片成功的标识  
	private static final int MSG_FAILURE = 1;//获取图片失败的标识  
	public int versionCode = 0;
	public static float firstHeight = 0;
	
	public WidgetCalender(
			String name ,
			MainAppContext context ,
			int widgetId )
	{
		super( name );
		this.transform = true;
		widgetcalender = this;
		this.maincontext = context;
		this.mContext = context.mWidgetContext;
		new WidgetThemeManager( context );
		mAppContext = context;
		cooGdx = new CooGdx( context.mGdxApplication );
		this.width = Utils3D.getScreenWidth();
		this.height = R3D.Workspace_cell_each_height * 4;
		this.transform = true;
		//		TempPositionX = 0;
		//		TempPositionY = 0;
		scale = Math.min( Utils3D.getScreenWidth() / 720f , Utils3D.getScreenHeight() / 1280f );
		height_scale = ( this.height - Parameter.GlassesHeight * scale ) / ( Parameter.Calender_Day_Glass_Height * 6 );
		offset = ( scale - height_scale ) / 2 * Parameter.Calender_Day_Glass_Height;
		nowMonth = CalenderHelper.getCurrentMonth();
		nowYear = CalenderHelper.getCurrentYear();
		nowDay = CalenderHelper.getCurrentDay();
		Declarevariables();
		nowWidth = Parameter.Calender_Day_Glass_Width * scale;
		nowHeight = Parameter.Calender_Day_Glass_Height * height_scale;
		leftLowerCornerWidth = origionDayMoveWidth - nowWidth / 2;
		leftLowerCornerHeight = origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * 5 * scale - offset - nowHeight / 2;
		initBitmapPacker();
		texcalendar_region = getRegion( "tex_turbo.jpg" );
		texcalendarday_region = getRegion( "tex_calendar.png" );
		texbackan_region = getRegion( "backan.png" );
		texback_region = getRegion( "back.png" );
		texday_region = getRegion( "day_0.png" );
		texdayliang_region = getRegion( "dayliang_1.png" );
		texxing_region = getRegion( "xing.png" );
		textoday_region = getRegion( "today.png" );
		texxuanzhong_region = getRegion( "xuanzhong.png" );
		texjiahao_region = getRegion( "jiahao.png" );
		PackageManager manager = iLoongLauncher.getInstance().getPackageManager();
		PackageInfo info;
		try
		{
			info = manager.getPackageInfo( iLoongLauncher.getInstance().getPackageName() , 0 );
			versionCode = info.versionCode;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		if( versionCode > 29445 )
		{
			firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 21f * scale - Utils3D
					.getStatusBarHeight();
		}
		else
		{
			firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * WidgetCalender.scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 21f * scale;
		}
		init();
		//		packBigMesh( dayglasses );
		updateRegion( nowMonth , nowYear , nowDay );
		setToday();
		//		mAppContext.mGdxApplication.postRunnable( new Runnable() {
		//			
		//			@Override
		//			public void run()
		//			{
		updateEvents( nowYear , nowMonth );
		//			}
		//		} );
		datereceiver = new DateReceiver();
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction( Intent.ACTION_TIME_CHANGED );
		intentfilter.addAction( Intent.ACTION_DATE_CHANGED );
		intentfilter.addAction( Intent.ACTION_TIMEZONE_CHANGED );
		intentfilter.addAction( Intent.ACTION_TIME_TICK );
		mContext.registerReceiver( datereceiver , intentfilter );
		maincontext.mGdxApplication.runOnUiThread( new Runnable() {
			
			@Override
			public void run()
			{
				mHandler = new Handler() {
					
					public void handleMessage(
							final Message msg )
					{//此方法在ui线程运行  
						switch( msg.what )
						{
							case MSG_SUCCESS:
								final List<String> list = (List<String>)msg.obj;
								if( list.size() != 0 )
								{
									mAppContext.mGdxApplication.postRunnable( new Runnable() {
										
										@Override
										public void run()
										{
											//										Log.d( "mytag" , ( (List<String>)msg.obj ).size() + "**" );
											updateE( list );
										}
									} );
								}
								break;
							case MSG_FAILURE:
								break;
						}
					}
				};
			}
		} );
		ChangeResize();
	}
	
	public void ChangeResize()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		if( sp.getBoolean( "com.iLoong.Calendar" , false ) )
		{
			int spanX = sp.getInt( "com.iLoong.Calendar" + ":spanX" , -1 );
			int spanY = sp.getInt( "com.iLoong.Calendar" + ":spanY" , -1 );
			int cellX = sp.getInt( "Calendar:cellX" , -1 );
			int cellY = sp.getInt( "Calendar:cellY" , -1 );
			if( spanX != -1 && spanY != -1 && cellX != -1 && cellY != -1 )
			{
				this.width = spanX * R3D.Workspace_cell_each_width;
				this.height = spanY * R3D.Workspace_cell_each_height;
				float nowscaleX = this.width / Utils3D.getScreenWidth();
				float nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 4 );
				scale = Math.min( nowscaleX , nowscaleY ) * Math.min( Utils3D.getScreenWidth() / 720f , Utils3D.getScreenHeight() / 1280f );
				height_scale = ( R3D.Workspace_cell_each_height * 4 * Math.min( nowscaleX , nowscaleY ) - Parameter.GlassesHeight * scale ) / ( Parameter.Calender_Day_Glass_Height * 6 );
				offset = ( scale - height_scale ) / 2 * Parameter.Calender_Day_Glass_Height;
				changePosition( cellX , cellY );
			}
		}
	}
	
	private void Declarevariables()
	{
		origionTopMoveWidth = this.width / 2 + Parameter.Origin_To_Up_Width * scale;
		origionTopMoveHeight = this.height / 2 + Parameter.Origin_To_Up_Height * scale;
		origionTop01MoveWidth = this.width / 2 + Parameter.Origin_To_Up01_Width * scale;
		origionTop01MoveHeight = this.height / 2 + Parameter.Origin_To_Up01_Height * scale;
		origionDownMoveWidth = this.width / 2 + Parameter.Origin_To_Down_Width * scale;
		origionDownMoveHeight = this.height / 2 + Parameter.Origin_To_Down_Height * scale;
		origionWeekMoveWidth = this.width / 2 + Parameter.Origin_To_Sun_Width * scale;
		origionWeekMoveHeight = this.height / 2 + Parameter.Origin_To_Sun_Height * scale;
		origionDayMoveWidth = this.width / 2 + Parameter.Origin_To_Sun_Width * scale;
		origionDayMoveHeight = this.height / 2 + Parameter.Origin_To_Sun_Height * scale - Parameter.Calender_Day_Glass_Height / 2 * scale - Parameter.Calender_Week_Glass_Height / 2 * scale;
		origionBackMoveWidth = this.width / 2 + Parameter.Origin_To_Back_Width * scale;
		origionBackMoveHeight = this.height / 2 + Parameter.Origin_To_Back_Height * scale;
		origionMonthMoveWidth = this.width / 2 + Parameter.Origin_To_Month_Width * scale;
		origionMonthMoveHeight = this.height / 2 + Parameter.Origin_To_Month_Height * scale;
		origionDay1MoveWidth = this.width / 2 + Parameter.Origin_To_Day1_Width * scale;
		origionDay1MoveHeight = this.height / 2 + Parameter.Origin_To_Day1_Height * scale;
		origionDay2MoveWidth = this.width / 2 + Parameter.Origin_To_Day2_Width * scale;
		origionDay2MoveHeight = this.height / 2 + Parameter.Origin_To_Day2_Height * scale;
		origionDaythMoveWidth = this.width / 2 + Parameter.Origin_To_Dayth_Width * scale;
		origionDaythMoveHeight = this.height / 2 + Parameter.Origin_To_Dayth_Height * scale;
		origionYearMoveWidth = this.width / 2 + Parameter.Origin_To_Year_Width * scale;
		origionYearMoveHeight = this.height / 2 + Parameter.Origin_To_Year_Height * scale;
		origionDayPlaneMoveWidth = this.width / 2 + Parameter.Origin_To_DAYPLANE_Width * scale;
		origionDayPlaneMoveHeight = this.height / 2 + Parameter.Origin_To_DAYPLANE_Height * scale;
	}
	
	public boolean judgetifShowEvents()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( maincontext.mContainerContext );
		if( preferences.getBoolean( CalenderHelper.UPGRADE_VERIFICATION , false ) )
		{
			return true;
		}
		else
		{
			return true;
		}
	}
	
	private void init()
	{
		addBackGround();
		addWeek();
		addDay();
		addEvent();
	}
	
	@Override
	public void onCellMove(
			int cellX ,
			int cellY )
	{
		super.onCellMove( cellX , cellY );
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		Editor editor = sp.edit();
		editor.putInt( "Calendar:cellX" , cellX );
		editor.putInt( "Calendar:cellY" , cellY );
		editor.commit();
		switch( Math.min( (int)( this.width / R3D.Workspace_cell_each_width ) , (int)( this.height / R3D.Workspace_cell_each_height ) ) )
		{
			case 1:
				if( versionCode > 29445 )
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 72f * scale - Utils3D
							.getStatusBarHeight() + cellY * R3D.Workspace_cell_each_height;
				}
				else
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 72f * scale + cellY * R3D.Workspace_cell_each_height;
				}
				break;
			case 2:
				if( versionCode > 29445 )
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 35f * scale - Utils3D
							.getStatusBarHeight() + cellY * R3D.Workspace_cell_each_height;
				}
				else
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 35f * scale + cellY * R3D.Workspace_cell_each_height;
				}
				break;
			case 3:
				if( versionCode > 29445 )
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 25f * scale - Utils3D
							.getStatusBarHeight() + cellY * R3D.Workspace_cell_each_height;
				}
				else
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 25f * scale + cellY * R3D.Workspace_cell_each_height;
				}
				break;
			case 4:
				if( versionCode > 29445 )
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 21f * scale - Utils3D
							.getStatusBarHeight() + cellY * R3D.Workspace_cell_each_height;
				}
				else
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 21f * scale + cellY * R3D.Workspace_cell_each_height;
				}
				break;
			default:
				break;
		}
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
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		Editor editor = sp.edit();
		editor.putInt( "Calendar:cellX" , cellX );
		editor.putInt( "Calendar:cellY" , cellY );
		editor.commit();
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
				//					TempPositionX = TempPositionX - TempX;
				//				}
				//				else
				//				{
				//					TempPositionX = TempPositionX + TempX;
				//				}
				nowscaleX = this.width / Utils3D.getScreenWidth();
				nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 4 );
				scale = Math.min( nowscaleX , nowscaleY ) * Math.min( Utils3D.getScreenWidth() / 720f , Utils3D.getScreenHeight() / 1280f );
				height_scale = ( R3D.Workspace_cell_each_height * 4 * Math.min( nowscaleX , nowscaleY ) - Parameter.GlassesHeight * scale ) / ( Parameter.Calender_Day_Glass_Height * 6 );
				offset = ( scale - height_scale ) / 2 * Parameter.Calender_Day_Glass_Height;
				changePosition( cellX , cellY );
				break;
			case RightMove:
				nowscaleX = this.width / Utils3D.getScreenWidth();
				nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 4 );
				scale = Math.min( nowscaleX , nowscaleY ) * Math.min( Utils3D.getScreenWidth() / 720f , Utils3D.getScreenHeight() / 1280f );
				height_scale = ( R3D.Workspace_cell_each_height * 4 * Math.min( nowscaleX , nowscaleY ) - Parameter.GlassesHeight * scale ) / ( Parameter.Calender_Day_Glass_Height * 6 );
				offset = ( scale - height_scale ) / 2 * Parameter.Calender_Day_Glass_Height;
				changePosition( cellX , cellY );
				break;
			case TopMove:
				nowscaleX = this.width / Utils3D.getScreenWidth();
				nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 4 );
				scale = Math.min( nowscaleX , nowscaleY ) * Math.min( Utils3D.getScreenWidth() / 720f , Utils3D.getScreenHeight() / 1280f );
				height_scale = ( R3D.Workspace_cell_each_height * 4 * Math.min( nowscaleX , nowscaleY ) - Parameter.GlassesHeight * scale ) / ( Parameter.Calender_Day_Glass_Height * 6 );
				offset = ( scale - height_scale ) / 2 * Parameter.Calender_Day_Glass_Height;
				changePosition( cellX , cellY );
				break;
			case BottomMove:
				//				if( moveY > 0 )
				//				{
				//					TempPositionY = TempPositionY - TempY;
				//				}
				//				else
				//				{
				//					TempPositionY = TempPositionY + TempY;
				//				}
				nowscaleX = this.width / Utils3D.getScreenWidth();
				nowscaleY = this.height / ( R3D.Workspace_cell_each_height * 4 );
				scale = Math.min( nowscaleX , nowscaleY ) * Math.min( Utils3D.getScreenWidth() / 720f , Utils3D.getScreenHeight() / 1280f );
				height_scale = ( R3D.Workspace_cell_each_height * 4 * Math.min( nowscaleX , nowscaleY ) - Parameter.GlassesHeight * scale ) / ( Parameter.Calender_Day_Glass_Height * 6 );
				offset = ( scale - height_scale ) / 2 * Parameter.Calender_Day_Glass_Height;
				changePosition( cellX , cellY );
				break;
			default:
				break;
		}
	}
	
	private void changePosition(
			int cellX ,
			int cellY )
	{
		Declarevariables();
		nowWidth = Parameter.Calender_Day_Glass_Width * scale;
		nowHeight = Parameter.Calender_Day_Glass_Height * height_scale;
		leftLowerCornerWidth = origionDayMoveWidth - nowWidth / 2;
		leftLowerCornerHeight = origionDayMoveHeight - ( Parameter.Calender_Day_Glass_Height * 5 ) * scale - offset - nowHeight / 2;
		switch( Math.min( (int)( this.width / R3D.Workspace_cell_each_width ) , (int)( this.height / R3D.Workspace_cell_each_height ) ) )
		{
			case 1:
				if( versionCode > 29445 )
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 72f * scale - Utils3D
							.getStatusBarHeight() + cellY * R3D.Workspace_cell_each_height;
				}
				else
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 72f * scale + cellY * R3D.Workspace_cell_each_height;
				}
				break;
			case 2:
				if( versionCode > 29445 )
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 35f * scale - Utils3D
							.getStatusBarHeight() + cellY * R3D.Workspace_cell_each_height;
				}
				else
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 35f * scale + cellY * R3D.Workspace_cell_each_height;
				}
				break;
			case 3:
				if( versionCode > 29445 )
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 25f * scale - Utils3D
							.getStatusBarHeight() + cellY * R3D.Workspace_cell_each_height;
				}
				else
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 25f * scale + cellY * R3D.Workspace_cell_each_height;
				}
				break;
			case 4:
				if( versionCode > 29445 )
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 21f * scale - Utils3D
							.getStatusBarHeight() + cellY * R3D.Workspace_cell_each_height;
				}
				else
				{
					firstHeight = this.getHeight() / 2 + ( Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5 ) * scale - ( scale - height_scale ) / 2 * 100f - Parameter.Calender_Day_Glass_Height * height_scale / 2 - 21f * scale + cellY * R3D.Workspace_cell_each_height;
				}
				break;
			default:
				break;
		}
		calendertop.build();
		calendertop.move( origionTopMoveWidth , origionTopMoveHeight - offset * 12f , 0 );
		backViewGroup3D.setSize( Parameter.Calender_Back * scale , Parameter.Calender_Back * scale );
		backViewGroup3D.setPosition( origionBackMoveWidth - backViewGroup3D.width / 2 , origionBackMoveHeight - offset * 12f - backViewGroup3D.height / 2 );
		backViewGroup3D.setOriginZ( Parameter.Origin_To_Back_Z * scale );
		calenderback.setSize( Parameter.Calender_Back * scale , Parameter.Calender_Back * scale );
		monthkViewGroup3D.setSize( 199 * scale , 34 * scale );
		monthkViewGroup3D.setPosition( origionMonthMoveWidth - monthkViewGroup3D.width / 2 , origionMonthMoveHeight - offset * 12f - monthkViewGroup3D.height / 2 );
		monthkViewGroup3D.setOriginZ( Parameter.Origin_To_Month_Z * scale );
		calendermonth.setSize( 199 * scale , 34 * scale );
		day1kViewGroup3D.setSize( 21 * scale , 34 * scale );
		day1kViewGroup3D.setPosition( origionDay1MoveWidth - day1kViewGroup3D.width / 2 , origionDay1MoveHeight - offset * 12f - day1kViewGroup3D.height / 2 );
		day1kViewGroup3D.setOriginZ( Parameter.Origin_To_Day1_Z * scale );
		calenderday1.setSize( 21 * scale , 34 * scale );
		day2kViewGroup3D.setSize( 21 * scale , 34 * scale );
		day2kViewGroup3D.setPosition( origionDay2MoveWidth - day2kViewGroup3D.width / 2 , origionDay2MoveHeight - offset * 12f - day2kViewGroup3D.height / 2 );
		day2kViewGroup3D.setOriginZ( Parameter.Origin_To_Day2_Z * scale );
		calenderday2.setSize( 21 * scale , 34 * scale );
		daythkViewGroup3D.setSize( 28 * scale , 34 * scale );
		daythkViewGroup3D.setPosition( origionDaythMoveWidth - daythkViewGroup3D.width / 2 , origionDaythMoveHeight - offset * 12f - daythkViewGroup3D.height / 2 );
		daythkViewGroup3D.setOriginZ( Parameter.Origin_To_Dayth_Z * scale );
		calenderdayth.setSize( 28 * scale , 34 * scale );
		yearkViewGroup3D.setSize( 96 * scale , 35 * scale );
		yearkViewGroup3D.setPosition( origionYearMoveWidth - yearkViewGroup3D.width / 2 , origionYearMoveHeight - offset * 12f - yearkViewGroup3D.height / 2 );
		yearkViewGroup3D.setOriginZ( Parameter.Origin_To_Year_Z * scale );
		calenderyear.setSize( 96 * scale , 35 * scale );
		calenderdown.build();
		calenderdown.move( origionDownMoveWidth , origionDownMoveHeight , 0 );
		calenderweek.build();
		calenderweek.move( origionTop01MoveWidth , origionTop01MoveHeight - offset * 12f , 0 );
		for( int i = 0 ; i < 7 ; i++ )
		{
			ViewGroup3D weekViewGroup3D = (ViewGroup3D)this.findView( "weekViewGroup3D" + i );
			if( weekViewGroup3D != null )
			{
				weekViewGroup3D.setSize( Parameter.Calender_Week_Patch_Width * scale , Parameter.Calender_Week_Patch_Height * scale );
				weekViewGroup3D.setPosition(
						origionWeekMoveWidth + Parameter.Calender_Week_Patch_Width * i * scale - Parameter.Calender_Week_Patch_Height * scale / 2 ,
						origionWeekMoveHeight - offset * 12f - Parameter.Calender_Week_Patch_Height * scale / 2 );
				weekViewGroup3D.setOriginZ( 6.5804f * scale );
				View3D weekView = weekViewGroup3D.findView( "weekView" );
				if( weekView != null )
				{
					weekView.setSize( Parameter.Calender_Week_Patch_Height * scale , Parameter.Calender_Week_Patch_Height * scale );
				}
			}
		}
		calenderdayplane.build();
		float myoffset = ( scale - height_scale ) / 2 * Parameter.Calender_Day_Glass_Height * 6;
		calenderdayplane.move( origionDayPlaneMoveWidth , origionDayPlaneMoveHeight - myoffset , 6.8504f * scale );
		//		calenderdayplane.hide();
		for( int j = 0 ; j < 6 ; j++ )
		{
			for( int i = 0 ; i < 7 ; i++ )
			{
				DayGroup3D viewgroup = (DayGroup3D)this.findView( "daygroup" + j + i );
				if( viewgroup != null )
				{
					viewgroup.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
					viewgroup.setPosition(
							origionDayMoveWidth + ( Parameter.Calender_Day_Glass_Width * i ) * scale - Parameter.Calender_Day_Glass_Width / 2 * scale ,
							origionDayMoveHeight - ( Parameter.Calender_Day_Glass_Height * j ) * scale - offset * ( 11f - 2 * j ) - Parameter.Calender_Day_Glass_Height / 2 * height_scale );
					viewgroup.setOrigin( viewgroup.width / 2 , viewgroup.height / 2 );
					MyDayGroup3D m_downViewGroup3D = (MyDayGroup3D)viewgroup.findView( "downViewGroup3D" );
					if( m_downViewGroup3D != null )
					{
						m_downViewGroup3D.setZ( -4.9236f * scale );
						m_downViewGroup3D.setPosition( 0 , ( Parameter.Calender_Day_Glass_Height * height_scale - Parameter.Calender_Day_Patch_Height * scale ) / 2 );
						m_downViewGroup3D.setSize( Parameter.Calender_Day_Patch_Width * scale , Parameter.Calender_Day_Patch_Height * scale );
						m_downViewGroup3D.setOrigin( m_downViewGroup3D.width / 2 , m_downViewGroup3D.height / 2 );
						m_downViewGroup3D.setOriginZ( -4.9236f * scale );
						View3D downView = m_downViewGroup3D.findView( "downView" );
						if( downView != null )
						{
							downView.setSize( Parameter.Calender_Day_Patch_Width * scale , Parameter.Calender_Day_Patch_Height * scale );
							downView.setPosition( 0 , 0 );
						}
					}
					DayGlasses calenderday = (DayGlasses)viewgroup.findView( "calenderday" );
					if( calenderday != null )
					{
						calenderday.build();
						calenderday.move( Parameter.Calender_Day_Glass_Width / 2 * scale , Parameter.Calender_Day_Glass_Height / 2 * height_scale , 0 );
					}
					MyDayGroup3D m_upViewGroup3D = (MyDayGroup3D)viewgroup.findView( "upViewGroup3D" );
					if( m_upViewGroup3D != null )
					{
						m_upViewGroup3D.setZ( 5.0764f * scale );
						m_upViewGroup3D.setPosition( 0 , ( Parameter.Calender_Day_Glass_Height * height_scale - Parameter.Calender_Day_Patch_Height * scale ) / 2 );
						m_upViewGroup3D.setSize( Parameter.Calender_Day_Patch_Width * scale , Parameter.Calender_Day_Patch_Height * scale );
						m_upViewGroup3D.setOrigin( m_upViewGroup3D.width , m_upViewGroup3D.height );
						View3D upView = m_upViewGroup3D.findView( "upView" );
						if( upView != null )
						{
							upView.setSize( Parameter.Calender_Day_Patch_Width * scale , Parameter.Calender_Day_Patch_Height * scale );
							upView.setPosition( 0 , 0 );
						}
					}
				}
			}
		}
		if( todayglasses != null )
		{
			todayglasses.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
			View3D todayView = todayglasses.findView( "todayView" );
			if( todayView != null )
			{
				todayView.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
			}
		}
		if( listxinghou.size() > 0 )
		{
			for( int j = 0 ; j < listxinghou.size() ; j++ )
			{
				MyDayGroup3D dgxinggroup = listxinghou.get( j );
				dgxinggroup.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
				dgxinggroup.setOrigin( dgxinggroup.width / 2 , dgxinggroup.height / 2 );
				View3D xingView = dgxinggroup.findView( "xingView" );
				xingView.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
			}
		}
		if( listxingqian.size() > 0 )
		{
			for( int j = 0 ; j < listxingqian.size() ; j++ )
			{
				MyDayGroup3D dgxinggroup = listxingqian.get( j );
				dgxinggroup.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
				dgxinggroup.setOrigin( dgxinggroup.width / 2 , dgxinggroup.height / 2 );
				View3D xingView = dgxinggroup.findView( "xingView" );
				xingView.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
			}
		}
		clicktodayXiaoGuo = (DayGlasses)this.findView( "clicktodayXiaoGuo" );
		if( clicktodayXiaoGuo != null )
		{
			clicktodayXiaoGuo.build();
			clicktodayXiaoGuo.move( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * lie * scale , origionDayMoveHeight - offset * 11f , 0 );
		}
		if( addenentgroup != null )
		{
			addenentgroup.onpositionchangeforAdd();
			addenentgroup.removeEvents();
		}
		//		addenentgroup.setSize( Parameter.BigBackgroundWidth * scale , Parameter.BigBackgroundHeight * height_scale );
		if( addenentgroup != null && addenentgroup.visible )
		{
			for( int j = 0 ; j < 7 ; j++ )
			{
				if( !days[hang * 7 + j].contains( "*" ) )
				{
					if( list.size() != 0 )
					{
						for( int k = 0 ; k < list.size() ; k++ )
						{
							if( list.get( k ).equals( days[hang * 7 + j] ) )
							{
								DayGlasses dayhasevents = (DayGlasses)WidgetCalender.this.findView( "dayhasevents" + k );
								if( dayhasevents != null )
								{
									dayhasevents.build();
									dayhasevents.move( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * j * scale , origionDayMoveHeight - offset * 11f , 0 );
								}
							}
						}
					}
				}
			}
			addenentgroup.updateNowEvents( nowYear , nowMonth , Integer.parseInt( days[hang * 7 + lie] ) );
		}
		Gdx.graphics.requestRendering();
	}
	
	private void addEvent()
	{
		addenentgroup = new AddEventGroup( "addenentgroup" , maincontext , nowYear , nowMonth , Integer.parseInt( days[hang * 7 + lie] ) , maincontext.mContainerContext );
		//		addenentgroup.setSize( Parameter.BigBackgroundWidth * scale , Parameter.BigBackgroundHeight * height_scale );
		//		addenentgroup.setPosition( (WidgetCalender.getIntance().getWidth()-addenentgroup.width)/2 , markday );
		addenentgroup.hide();
		this.addView( addenentgroup );
	}
	
	/**
	 * This function will be called when a calendar widget instance be deleted.
	 * We call {@link #dispose()} in it. 
	 */
	@Override
	public void onDelete()
	{
		super.onDelete();
		this.dispose();
	}
	
	/**
	 * This function will be called after calendar widget apk uninstalled.
	 * We call {@link #dispose()} in it. 
	 * 
	 * @deprecated
	 * NOTICE: There is a bug on uninstalling widget, the function will not be called
	 * and widget will not be removed from workspace.
	 */
	@Override
	public void onUninstall()
	{
		super.onUninstall();
		this.dispose();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		// unregister receiver.
		if( datereceiver != null )
		{
			mContext.unregisterReceiver( datereceiver );
			datereceiver = null;
		}
		// remove all children.
		this.removeAllViews();
		// we should dispose all disposables on delete.
		if( packer != null )
		{
			packer.dispose();
			packer = null;
		}
		if( packerAtlas != null )
		{
			packerAtlas.dispose();
			packerAtlas = null;
		}
		if( texcalendar_region != null )
		{
			texcalendar_region.getTexture().dispose();
		}
		if( texcalendarday_region != null )
		{
			texcalendarday_region.getTexture().dispose();
		}
		if( texbackan_region != null )
		{
			texbackan_region.getTexture().dispose();
		}
		if( texback_region != null )
		{
			texback_region.getTexture().dispose();
		}
		if( texday_region != null )
		{
			texday_region.getTexture().dispose();
		}
		if( texdayliang_region != null )
		{
			texdayliang_region.getTexture().dispose();
		}
		if( texxing_region != null )
		{
			texxing_region.getTexture().dispose();
		}
		if( textoday_region != null )
		{
			textoday_region.getTexture().dispose();
		}
		if( texxuanzhong_region != null )
		{
			texxuanzhong_region.getTexture().dispose();
		}
		if( texjiahao_region != null )
		{
			texjiahao_region.getTexture().dispose();
		}
		// set null static variables.
		widgetcalender = null;
		// we call gc since it's not time-sensitive when a widget has been removed.
		System.gc();
	}
	
	private int nowday = CalenderHelper.getCurrentDay();
	
	public class DateReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context context ,
				Intent intent )
		{
			String action = intent.getAction();
			if( action.equals( Intent.ACTION_TIME_TICK ) )
			{
				int currentday = CalenderHelper.getCurrentDay();
				if( nowday != currentday )
				{
					mAppContext.mGdxApplication.postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							if( ifBack )
							{
							}
							else
							{
								nowYear = CalenderHelper.getCurrentYear();
								nowMonth = CalenderHelper.getCurrentMonth();
								nowDay = CalenderHelper.getCurrentDay();
								updateRegion( nowMonth , nowYear , nowDay );
								if( listxinghou.size() != 0 )
								{
									for( int j = 0 ; j < listxinghou.size() ; j++ )
									{
										daygroup.get( countxinghou.get( j ) ).removeView( listxinghou.get( j ) );
									}
									countxinghou.clear();
									listxinghou.clear();
								}
								if( listxingqian.size() != 0 )
								{
									for( int j = 0 ; j < listxingqian.size() ; j++ )
									{
										daygroup.get( countxingqian.get( j ) ).removeView( listxingqian.get( j ) );
									}
									listxingqian.clear();
									countxingqian.clear();
								}
								updateEvents( nowYear , nowMonth );
								if( markday != -1 )
								{
									daygroup.get( markday ).removeView( todayglasses );
								}
								setToday();
							}
						}
					} );
					nowday = CalenderHelper.getCurrentDay();
				}
			}
			if( action.equals( Intent.ACTION_TIME_CHANGED ) || action.equals( Intent.ACTION_DATE_CHANGED ) || action.equals( Intent.ACTION_TIMEZONE_CHANGED ) )
			{
				mAppContext.mGdxApplication.postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						if( ifBack )
						{
						}
						else
						{
							nowYear = CalenderHelper.getCurrentYear();
							nowMonth = CalenderHelper.getCurrentMonth();
							nowDay = CalenderHelper.getCurrentDay();
							updateRegion( nowMonth , nowYear , nowDay );
							if( listxinghou.size() != 0 )
							{
								for( int j = 0 ; j < listxinghou.size() ; j++ )
								{
									daygroup.get( countxinghou.get( j ) ).removeView( listxinghou.get( j ) );
								}
								countxinghou.clear();
								listxinghou.clear();
							}
							if( listxingqian.size() != 0 )
							{
								for( int j = 0 ; j < listxingqian.size() ; j++ )
								{
									daygroup.get( countxingqian.get( j ) ).removeView( listxingqian.get( j ) );
								}
								listxingqian.clear();
								countxingqian.clear();
							}
							updateEvents( nowYear , nowMonth );
							if( markday != -1 )
							{
								daygroup.get( markday ).removeView( todayglasses );
							}
							setToday();
						}
					}
				} );
				nowday = CalenderHelper.getCurrentDay();
			}
		}
	}
	
	@Override
	public void onResume()
	{
		if( addenentgroup != null && addenentgroup.visible )
		{
			mAppContext.mGdxApplication.postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					if( listxinghou.size() != 0 )
					{
						for( int j = 0 ; j < listxinghou.size() ; j++ )
						{
							daygroup.get( countxinghou.get( j ) ).removeView( listxinghou.get( j ) );
						}
						countxinghou.clear();
						listxinghou.clear();
					}
					if( listxingqian.size() != 0 )
					{
						for( int j = 0 ; j < listxingqian.size() ; j++ )
						{
							daygroup.get( countxingqian.get( j ) ).removeView( listxingqian.get( j ) );
						}
						listxingqian.clear();
						countxingqian.clear();
					}
					if( recordEvents.size() != 0 )
					{
						for( int i = 0 ; i < recordEvents.size() ; i++ )
						{
							WidgetCalender.this.removeView( recordEvents.get( i ) );
						}
						recordEvents.clear();
					}
					for( int j = 0 ; j < 7 ; j++ )
					{
						if( !days[hang * 7 + j].contains( "*" ) )
						{
							boolean ifhasEvents = CalenderHelper.judgeDayIfEvents( mAppContext.mContainerContext.getContentResolver() , nowYear , nowMonth , Integer.parseInt( days[hang * 7 + j] ) );
							if( ifhasEvents )
							{
								DayGlasses dayhasevents = new DayGlasses( mAppContext , "dayhasevents" , texxing_region , "mod_shadow.obj" );
								dayhasevents.build();
								dayhasevents.move( origionDayMoveWidth + ( Parameter.Calender_Day_Glass_Width * j ) * scale , origionDayMoveHeight - offset * 11f , 0 );
								WidgetCalender.this.addView( dayhasevents );
								recordEvents.add( dayhasevents );
							}
						}
					}
					addenentgroup.updateNowEvents( nowYear , nowMonth , Integer.parseInt( days[hang * 7 + lie] ) );
				}
			} );
		}
		else
		{
			if( judgetifShowEvents() )
			{
				//				mAppContext.mGdxApplication.postRunnable( new Runnable() {
				//					
				//					@Override
				//					public void run()
				//					{
				updateEvents( nowYear , nowMonth );
				//					}
				//				} );
			}
		}
		super.onResume();
	}
	
	private ViewGroup3D backViewGroup3D;
	private ViewGroup3D monthkViewGroup3D;
	private ViewGroup3D day1kViewGroup3D;
	private ViewGroup3D day2kViewGroup3D;
	private ViewGroup3D daythkViewGroup3D;
	private ViewGroup3D yearkViewGroup3D;
	
	private void addBackGround()
	{
		calenderalltop = new BackgroundGroup( "calenderalltop" );
		calenderalldown = new BackgroundGroup( "calenderalldown" );
		calendertop = new PluginViewObject3D( maincontext , "calendertop" , texcalendar_region , "mod_calendarUp.obj" );
		calendertop.build();
		calendertop.move( origionTopMoveWidth , origionTopMoveHeight - offset * 12f , 0 );
		calenderalltop.addView( calendertop );
		backViewGroup3D = new ViewGroup3D( "backViewGroup3D" );
		backViewGroup3D.transform = true;
		backViewGroup3D.setSize( Parameter.Calender_Back * scale , Parameter.Calender_Back * scale );
		backViewGroup3D.setPosition( origionBackMoveWidth - backViewGroup3D.width / 2 , origionBackMoveHeight - offset * 12f - backViewGroup3D.height / 2 );
		backViewGroup3D.setOriginZ( Parameter.Origin_To_Back_Z * scale );
		calenderback = new View3D( "calenderback" , texbackan_region );
		calenderback.setSize( Parameter.Calender_Back * scale , Parameter.Calender_Back * scale );
		backViewGroup3D.addView( calenderback );
		calenderalltop.addView( backViewGroup3D );
		monthkViewGroup3D = new ViewGroup3D( "monthkViewGroup3D" );
		monthkViewGroup3D.transform = true;
		monthkViewGroup3D.setSize( 199 * scale , 34 * scale );
		monthkViewGroup3D.setPosition( origionMonthMoveWidth - monthkViewGroup3D.width / 2 , origionMonthMoveHeight - offset * 12f - monthkViewGroup3D.height / 2 );
		monthkViewGroup3D.setOriginZ( Parameter.Origin_To_Month_Z * scale );
		calendermonth = new View3D( "calendermonth" , getRegion( "month_1.png" ) );
		calendermonth.setSize( 199 * scale , 34 * scale );
		monthkViewGroup3D.addView( calendermonth );
		calenderalltop.addView( monthkViewGroup3D );
		day1kViewGroup3D = new ViewGroup3D( "day1kViewGroup3D" );
		day1kViewGroup3D.transform = true;
		day1kViewGroup3D.setSize( 21 * scale , 34 * scale );
		day1kViewGroup3D.setPosition( origionDay1MoveWidth - day1kViewGroup3D.width / 2 , origionDay1MoveHeight - offset * 12f - day1kViewGroup3D.height / 2 );
		day1kViewGroup3D.setOriginZ( Parameter.Origin_To_Day1_Z * scale );
		calenderday1 = new View3D( "calenderday1" , texday_region );
		calenderday1.setSize( 21 * scale , 34 * scale );
		day1kViewGroup3D.addView( calenderday1 );
		calenderalltop.addView( day1kViewGroup3D );
		day2kViewGroup3D = new ViewGroup3D( "day2kViewGroup3D" );
		day2kViewGroup3D.transform = true;
		day2kViewGroup3D.setSize( 21 * scale , 34 * scale );
		day2kViewGroup3D.setPosition( origionDay2MoveWidth - day2kViewGroup3D.width / 2 , origionDay2MoveHeight - offset * 12f - day2kViewGroup3D.height / 2 );
		day2kViewGroup3D.setOriginZ( Parameter.Origin_To_Day2_Z * scale );
		calenderday2 = new View3D( "calenderday1" , texday_region );
		calenderday2.setSize( 21 * scale , 34 * scale );
		day2kViewGroup3D.addView( calenderday2 );
		calenderalltop.addView( day2kViewGroup3D );
		daythkViewGroup3D = new ViewGroup3D( "daythkViewGroup3D" );
		daythkViewGroup3D.transform = true;
		daythkViewGroup3D.setSize( 28 * scale , 34 * scale );
		daythkViewGroup3D.setPosition( origionDaythMoveWidth - daythkViewGroup3D.width / 2 , origionDaythMoveHeight - offset * 12f - daythkViewGroup3D.height / 2 );
		daythkViewGroup3D.setOriginZ( Parameter.Origin_To_Dayth_Z * scale );
		calenderdayth = new View3D( "calenderdayth" , getRegion( "day_th.png" ) );
		calenderdayth.setSize( 28 * scale , 34 * scale );
		daythkViewGroup3D.addView( calenderdayth );
		calenderalltop.addView( daythkViewGroup3D );
		yearkViewGroup3D = new ViewGroup3D( "yearkViewGroup3D" );
		yearkViewGroup3D.transform = true;
		yearkViewGroup3D.setSize( 96 * scale , 35 * scale );
		yearkViewGroup3D.setPosition( origionYearMoveWidth - yearkViewGroup3D.width / 2 , origionYearMoveHeight - offset * 12f - yearkViewGroup3D.height / 2 );
		yearkViewGroup3D.setOriginZ( Parameter.Origin_To_Year_Z * scale );
		calenderyear = new View3D( "calenderyear" , CalenderHelper.getRegion( maincontext , 2014 ) );
		calenderyear.setSize( 96 * scale , 35 * scale );
		yearkViewGroup3D.addView( calenderyear );
		calenderalltop.addView( yearkViewGroup3D );
		this.addView( calenderalltop );
		calenderdown = new PluginViewObject3D( maincontext , "calenderdown" , texcalendar_region , "mod_calendarDown.obj" );
		calenderdown.build();
		calenderdown.move( origionDownMoveWidth , origionDownMoveHeight , 0 );
		calenderalldown.addView( calenderdown );
		this.addView( calenderalldown );
	}
	
	private WeekGlasses calenderweek;
	
	private void addWeek()
	{
		calenderweek = new WeekGlasses( maincontext , "calenderweek" , texcalendar_region , "mod_calendarUp01.obj" );
		calenderweek.build();
		calenderweek.move( origionTop01MoveWidth , origionTop01MoveHeight - offset * 12f , 0 );
		this.addView( calenderweek );
		for( int i = 0 ; i < 7 ; i++ )
		{
			MyDayGroup3D weekViewGroup3D = new MyDayGroup3D( "weekViewGroup3D" + i );
			weekViewGroup3D.transform = true;
			weekViewGroup3D.setSize( Parameter.Calender_Week_Patch_Width * scale , Parameter.Calender_Week_Patch_Height * scale );
			weekViewGroup3D.setPosition(
					origionWeekMoveWidth + Parameter.Calender_Week_Patch_Width * i * scale - Parameter.Calender_Week_Patch_Height * scale / 2 ,
					origionWeekMoveHeight - offset * 12f - Parameter.Calender_Week_Patch_Height * scale / 2 );
			weekViewGroup3D.setOriginZ( 6.5804f * scale );
			View3D weekView = new View3D( "weekView" , getRegion( Parameter.CalenderPitchWeek[i] ) );
			weekView.setSize( Parameter.Calender_Week_Patch_Height * scale , Parameter.Calender_Week_Patch_Height * scale );
			weekViewGroup3D.addView( weekView );
			this.addView( weekViewGroup3D );
		}
	}
	
	List<DayGlasses> dayglasses = new ArrayList<DayGlasses>();
	
	private void addDay()
	{
		calenderdayplane = new DayGlasses( maincontext , "calenderdayplane" , CalenderHelper.DrawPlane( mAppContext , days ) , "mod_plane.obj" );
		calenderdayplane.build();
		float myoffset = ( scale - height_scale ) / 2 * Parameter.Calender_Day_Glass_Height * 6;
		calenderdayplane.move( origionDayPlaneMoveWidth , origionDayPlaneMoveHeight - myoffset , 6.8504f * scale );
		calenderdayplane.hide();
		this.addView( calenderdayplane );
		for( int j = 0 ; j < 6 ; j++ )
		{
			for( int i = 0 ; i < 7 ; i++ )
			{
				DayGroup3D viewgroup = new DayGroup3D( "daygroup" + j + i );
				viewgroup.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
				viewgroup.setPosition(
						origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * i * scale - Parameter.Calender_Day_Glass_Width / 2 * scale ,
						origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * j * scale - offset * ( 11f - 2 * j ) - Parameter.Calender_Day_Glass_Height / 2 * height_scale );
				viewgroup.setOrigin( viewgroup.width / 2 , viewgroup.height / 2 );
				MyDayGroup3D m_downViewGroup3D = new MyDayGroup3D( "downViewGroup3D" );
				m_downViewGroup3D.transform = true;
				m_downViewGroup3D.setZ( -4.9236f * scale );
				m_downViewGroup3D.setPosition( 0 , ( Parameter.Calender_Day_Glass_Height * height_scale - Parameter.Calender_Day_Patch_Height * scale ) / 2 );
				m_downViewGroup3D.setSize( Parameter.Calender_Day_Patch_Width * scale , Parameter.Calender_Day_Patch_Height * scale );
				m_downViewGroup3D.setOrigin( m_downViewGroup3D.width / 2 , m_downViewGroup3D.height / 2 );
				m_downViewGroup3D.setOriginZ( -4.9236f * scale );
				m_downViewGroup3D.setRotationVector( 1 , 0 , 0 );
				m_downViewGroup3D.setRotationAngle( 180 , 0 , 0 );
				View3D downView = new View3D( "downView" , texdayliang_region );
				downView.setSize( Parameter.Calender_Day_Patch_Width * scale , Parameter.Calender_Day_Patch_Height * scale );
				downView.setPosition( 0 , 0 );
				m_downViewGroup3D.addView( downView );
				m_downViewGroup3D.show();
				viewgroup.addView( m_downViewGroup3D );
				daydownviewgroup.add( m_downViewGroup3D );
				daypitchlisthou.add( downView );
				DayGlasses calenderday = new DayGlasses( maincontext , "calenderday" , texcalendarday_region , "mod_number(day)_glass02.obj" );
				calenderday.build();
				calenderday.move( Parameter.Calender_Day_Glass_Width / 2 * scale , Parameter.Calender_Day_Glass_Height / 2 * height_scale , 0 );
				calenderday.show();
				calenderday.setPosition( 0 , 0 );
				viewgroup.addView( calenderday );
				dayglasses.add( calenderday );
				MyDayGroup3D m_upViewGroup3D = new MyDayGroup3D( "upViewGroup3D" );
				m_upViewGroup3D.transform = true;
				m_upViewGroup3D.setZ( 5.0764f * scale );
				m_upViewGroup3D.setPosition( 0 , ( Parameter.Calender_Day_Glass_Height * height_scale - Parameter.Calender_Day_Patch_Height * scale ) / 2 );
				m_upViewGroup3D.setSize( Parameter.Calender_Day_Patch_Width * scale , Parameter.Calender_Day_Patch_Height * scale );
				View3D upView = new View3D( "upView" , texdayliang_region );
				upView.setSize( Parameter.Calender_Day_Patch_Width * scale , Parameter.Calender_Day_Patch_Height * scale );
				upView.setPosition( 0 , 0 );
				m_upViewGroup3D.addView( upView );
				m_upViewGroup3D.show();
				viewgroup.addView( m_upViewGroup3D );
				dayupviewgroup.add( m_upViewGroup3D );
				daypitchlistqian.add( upView );
				this.addView( viewgroup );
				daygroup.add( viewgroup );
			}
		}
	}
	
	//	DayGlasses bigMesh = null;
	//	private void packBigMesh(
	//			List<DayGlasses> dayglasses )
	//	{
	//		if( dayglasses.size() != 0 )
	//		{
	//			float bigvertices[] = new float[dayglasses.get( 0 ).getMesh().getNumVertices() * dayglasses.get( 0 ).getMesh().getVertexSize() / 4 * 42];
	//			short bigindices[] = new short[dayglasses.get( 0 ).getMesh().getNumIndices() * 42];
	//			float tempvertices[] = new float[dayglasses.get( 0 ).getMesh().getNumVertices() * dayglasses.get( 0 ).getMesh().getVertexSize() / 4];
	//			short tempindices[] = new short[dayglasses.get( 0 ).getMesh().getNumIndices()];
	//			VertexAttributes attributes = null;
	//			for( int i = 0 ; i < dayglasses.size() ; i++ )
	//			{
	//				dayglasses.get( i ).getMesh().getVertices( tempvertices );
	//				dayglasses.get( i ).getMesh().getIndices( tempindices );
	//				System.arraycopy( tempvertices , 0 , bigvertices , i * tempvertices.length , tempvertices.length );
	//				System.arraycopy( tempindices , 0 , bigindices , i * tempindices.length , tempindices.length );
	//			}
	//			dayglasses.get( 0 ).getMesh().getVertices( tempvertices );
	//			dayglasses.get( 0 ).getMesh().getIndices( tempindices );
	//			attributes = dayglasses.get( 0 ).getMesh().getVertexAttributes();
	//			Mesh mesh = new Mesh( true , bigvertices.length , bigindices.length , attributes );
	//			mesh.setVertices( bigvertices );
	//			mesh.setIndices( bigindices );
	//			bigMesh = new DayGlasses( maincontext , "calenderday111" , getRegion( "tex_calendar.png" ) , "mod_number(day)_glass01.obj" );
	//			bigMesh.build();
	//			bigMesh.setMesh( mesh );
	//			this.addView( bigMesh );
	//			
	////			for( int i = 0 ; i < daygroup.size() ; i++ )
	////			{
	////				this.addView( daygroup.get( i ) );
	////			}
	//		}
	//	}
	//	
	MyDayGroup3D todayglasses = null;
	private int markday = -1;
	
	public void setToday()
	{
		int today = CalenderHelper.getCurrentDay();
		for( int i = 0 ; i < days.length ; i++ )
		{
			if( !days[i].contains( "*" ) )
			{
				if( today == Integer.parseInt( days[i] ) )
				{
					//					todayglasses = new DayGlasses( mAppContext , "todayglasses" , getRegion( "today.png" ) , "mod_shadow.obj" );
					//					todayglasses.build();
					//					todayglasses.move( Parameter.Calender_Day_Glass_Width / 2 * scale , Parameter.Calender_Day_Glass_Height / 2 * height_scale , 0 );
					//					todayglasses.setOrigin( daygroup.get( i ).width / 2 , daygroup.get( i ).height / 2 );
					//					daygroup.get( i ).addView( todayglasses );
					//					markday = i;
					todayglasses = new MyDayGroup3D( "todayglasses" );
					todayglasses.transform = true;
					todayglasses.setPosition( 0 , 0 );
					todayglasses.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
					todayglasses.setZ( 6.8504f * scale );
					View3D todayView = new View3D( "todayView" , textoday_region );
					todayView.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
					todayglasses.addView( todayView );
					daygroup.get( i ).addView( todayglasses );
					markday = i;
				}
			}
		}
	}
	
	public void updateRegionObj(
			PluginViewObject3D obj ,
			TextureRegion new_region )
	{
		float old_region_U = obj.region.getU();
		float old_region_V = obj.region.getV();
		obj.region.setRegion( new_region );
		float[] vertices = new float[obj.getMesh().getNumVertices() * 9];
		obj.getMesh().getVertices( vertices );
		int vertice_size = obj.getMesh().getNumVertices() * 9;
		for( int i = 0 ; i < vertice_size ; i += 9 )
		{
			if( obj.region != null )
			{
				vertices[i + 7] += obj.region.getU() - old_region_U;
				vertices[i + 8] += obj.region.getV() - old_region_V;
			}
		}
		obj.getMesh().setVertices( vertices );
	}
	
	public void updateRegion(
			int currentMonth ,
			int currentYear ,
			int currentDay )
	{
		calendermonth.region.setRegion( getRegion( "month_" + ( currentMonth + 1 ) + ".png" ) );
		char[] mydays = getNum( currentDay );
		if( mydays.length == 2 )
		{
			calenderday1.region.setRegion( getRegion( "day_" + mydays[0] + ".png" ) );
			calenderday2.region.setRegion( getRegion( "day_" + mydays[1] + ".png" ) );
		}
		else if( mydays.length == 1 )
		{
			calenderday1.region.setRegion( texday_region );
			calenderday2.region.setRegion( getRegion( "day_" + mydays[0] + ".png" ) );
		}
		// we create a new texture region, so we need dispose the old one.
		calenderyear.disposeTexture();
		calenderyear.region.setRegion( CalenderHelper.getRegion( maincontext , currentYear ) );
		days = CalenderHelper.caculateDay( currentMonth , currentYear );
		if( days.length == 42 && daypitchlistqian.size() == 42 )
		{
			for( int i = 0 ; i < days.length ; i++ )
			{
				if( daypitchlisthou.size() == 0 )
				{
					if( days[i].contains( "*" ) )
					{
						String replace = days[i].replace( "*" , "" );
						daypitchlistqian.get( i ).region.setRegion( getRegion( "dayan_" + replace + ".png" ) );
					}
					else
					{
						if( i == 0 || i == 6 || i == 7 || i == 13 || i == 14 || i == 20 || i == 21 || i == 27 || i == 28 || i == 34 || i == 35 || i == 41 )
						{
							daypitchlistqian.get( i ).region.setRegion( getRegion( "daylianglan_" + days[i] + ".png" ) );
						}
						else
						{
							daypitchlistqian.get( i ).region.setRegion( getRegion( "dayliang_" + days[i] + ".png" ) );
						}
					}
				}
				else
				{
					if( scrollcount_all % 2 == 0 )
					{
						if( days[i].contains( "*" ) )
						{
							String replace = days[i].replace( "*" , "" );
							daypitchlistqian.get( i ).region.setRegion( getRegion( "dayan_" + replace + ".png" ) );
						}
						else
						{
							if( i == 0 || i == 6 || i == 7 || i == 13 || i == 14 || i == 20 || i == 21 || i == 27 || i == 28 || i == 34 || i == 35 || i == 41 )
							{
								daypitchlistqian.get( i ).region.setRegion( getRegion( "daylianglan_" + days[i] + ".png" ) );
							}
							else
							{
								daypitchlistqian.get( i ).region.setRegion( getRegion( "dayliang_" + days[i] + ".png" ) );
							}
						}
					}
					else
					{
						if( days[i].contains( "*" ) )
						{
							String replace = days[i].replace( "*" , "" );
							daypitchlisthou.get( i ).region.setRegion( getRegion( "dayan_" + replace + ".png" ) );
						}
						else
						{
							if( i == 0 || i == 6 || i == 7 || i == 13 || i == 14 || i == 20 || i == 21 || i == 27 || i == 28 || i == 34 || i == 35 || i == 41 )
							{
								daypitchlisthou.get( i ).region.setRegion( getRegion( "daylianglan_" + days[i] + ".png" ) );
							}
							else
							{
								daypitchlisthou.get( i ).region.setRegion( getRegion( "dayliang_" + days[i] + ".png" ) );
							}
						}
					}
				}
			}
		}
		// we create a new texture region, so we need dispose the old one.
		calenderdayplane.disposeTexture();
		updateRegionObj( calenderdayplane , CalenderHelper.DrawPlane( mAppContext , days ) );
	}
	
	List<MyDayGroup3D> listxingqian = new ArrayList<MyDayGroup3D>();
	List<Integer> countxingqian = new ArrayList<Integer>();
	List<MyDayGroup3D> listxinghou = new ArrayList<MyDayGroup3D>();
	List<Integer> countxinghou = new ArrayList<Integer>();
	List<String> list = null;
	
	public void updateEvents(
			int currentYear ,
			int currentMonth )
	{
		if( CalenderHelper.judgeMonthIfEvents( maincontext.mContainerContext.getContentResolver() , currentYear , currentMonth ) == null )
		{
		}
		else
		{
			list = CalenderHelper.judgeMonthIfEvents( maincontext.mContainerContext.getContentResolver() , currentYear , currentMonth );
			if( days.length == 42 && list.size() != 0 )
			{
				for( int i = 0 ; i < days.length ; i++ )
				{
					for( int j = 0 ; j < list.size() ; j++ )
					{
						if( days[i].equals( list.get( j ) ) )
						{
							if( scrollcount_all % 2 != 0 )
							{
								//								DayGroup3D dgxinggroup = new DayGroup3D( "dayxinggroup" );
								//								DayGlasses dayxing = new DayGlasses( mAppContext , "dayxing" , getRegion( "xing.png" ) , "mod_shadow.obj" );
								//								dayxing.build();
								//								dayxing.move( Parameter.Calender_Day_Glass_Width / 2 * scale , Parameter.Calender_Day_Glass_Height / 2 * height_scale , 0 );
								//								dgxinggroup.addView( dayxing );
								//								dgxinggroup.setOrigin( daygroup.get( i ).width / 2 , daygroup.get( i ).height / 2 );
								//								//							dgxinggroup.setOrigin(
								//								//									( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * ( i % 7 ) ) * scale ,
								//								//									( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * ( i / 7 ) ) * scale - offset * ( 11f - 2 * ( i / 7 ) ) );
								//								//							dgxinggroup.setOriginZ( ( -0.0764f ) * scale );
								//								dgxinggroup.setRotationVector( 1 , 0 , 0 );
								//								dgxinggroup.setRotationAngle( 180 , 0 , 0 );
								//								daygroup.get( i ).addView( dgxinggroup );
								//								listxinghou.add( dgxinggroup );
								//								countxinghou.add( i );
								MyDayGroup3D dgxinggroup = new MyDayGroup3D( "dayxinggroup" );
								dgxinggroup.setPosition( 0 , 0 );
								dgxinggroup.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
								dgxinggroup.setZ( 7.9488f * scale );
								dgxinggroup.setOrigin( daygroup.get( i ).width / 2 , daygroup.get( i ).height / 2 );
								dgxinggroup.setRotationVector( 1 , 0 , 0 );
								dgxinggroup.setOriginZ( 0f );
								dgxinggroup.setRotationAngle( 180 , 0 , 0 );
								View3D xingView = new View3D( "xingView" , texxing_region );
								xingView.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
								dgxinggroup.addView( xingView );
								daygroup.get( i ).addView( dgxinggroup );
								listxinghou.add( dgxinggroup );
								countxinghou.add( i );
							}
							else
							{
								//								DayGroup3D dgxinggroup = new DayGroup3D( "dayxinggroup" );
								//								DayGlasses dayxing = new DayGlasses( mAppContext , "dayxing" , getRegion( "xing.png" ) , "mod_shadow.obj" );
								//								dayxing.build();
								//								dayxing.move( Parameter.Calender_Day_Glass_Width / 2 * scale , Parameter.Calender_Day_Glass_Height / 2 * height_scale , 0 );
								//								dgxinggroup.setOrigin( daygroup.get( i ).width / 2 , daygroup.get( i ).height / 2 );
								//								dgxinggroup.addView( dayxing );
								//								daygroup.get( i ).addView( dgxinggroup );
								//								listxingqian.add( dgxinggroup );
								//								countxingqian.add( i );
								MyDayGroup3D dgxinggroup = new MyDayGroup3D( "dayxinggroup" );
								dgxinggroup.setPosition( 0 , 0 );
								dgxinggroup.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
								dgxinggroup.setOriginZ( 7.9488f * scale );
								View3D xingView = new View3D( "xingView" , texxing_region );
								xingView.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
								dgxinggroup.addView( xingView );
								dgxinggroup.setOrigin( daygroup.get( i ).width / 2 , daygroup.get( i ).height / 2 );
								daygroup.get( i ).addView( dgxinggroup );
								listxingqian.add( dgxinggroup );
								countxingqian.add( i );
							}
						}
					}
				}
			}
		}
	}
	
	public void updateE(
			List<String> list )
	{
		if( list != null && days.length == 42 && list.size() != 0 )
		{
			for( int i = 0 ; i < days.length ; i++ )
			{
				for( int j = 0 ; j < list.size() ; j++ )
				{
					if( days[i].equals( list.get( j ) ) )
					{
						if( scrollcount_all % 2 != 0 )
						{
							MyDayGroup3D dgxinggroup = new MyDayGroup3D( "dayxinggroup" );
							dgxinggroup.setPosition( 0 , 0 );
							dgxinggroup.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
							dgxinggroup.setZ( 7.9488f * scale );
							dgxinggroup.setOrigin( daygroup.get( i ).width / 2 , daygroup.get( i ).height / 2 );
							dgxinggroup.setRotationVector( 1 , 0 , 0 );
							dgxinggroup.setOriginZ( 0f );
							dgxinggroup.setRotationAngle( 180 , 0 , 0 );
							View3D xingView = new View3D( "xingView" , texxing_region );
							xingView.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
							dgxinggroup.addView( xingView );
							daygroup.get( i ).addView( dgxinggroup );
							//							dgxinggroup.hide();
							listxinghou.add( dgxinggroup );
							countxinghou.add( i );
						}
						else
						{
							MyDayGroup3D dgxinggroup = new MyDayGroup3D( "dayxinggroup" );
							dgxinggroup.setPosition( 0 , 0 );
							dgxinggroup.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
							dgxinggroup.setOriginZ( 7.9488f * scale );
							View3D xingView = new View3D( "xingView" , texxing_region );
							xingView.setSize( Parameter.Calender_Day_Glass_Width * scale , Parameter.Calender_Day_Glass_Height * height_scale );
							dgxinggroup.addView( xingView );
							dgxinggroup.setOrigin( daygroup.get( i ).width / 2 , daygroup.get( i ).height / 2 );
							daygroup.get( i ).addView( dgxinggroup );
							//							dgxinggroup.hide();
							listxingqian.add( dgxinggroup );
							countxingqian.add( i );
						}
					}
				}
			}
		}
	}
	
	public void startRunnable(
			final int currentYear ,
			final int currentMonth )
	{
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				list = CalenderHelper.judgeMonthIfEvents( maincontext.mContainerContext.getContentResolver() , currentYear , currentMonth );
				if( list.size() != 0 )
				{
					try
					{
						Thread.sleep( 500 );
					}
					catch( InterruptedException e )
					{
						e.printStackTrace();
					}
					mHandler.obtainMessage( MSG_SUCCESS , list ).sendToTarget();
				}
			}
		} ).start();
	}
	
	public void startBackRunnable(
			final int currentYear ,
			final int currentMonth )
	{
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				list = CalenderHelper.judgeMonthIfEvents( maincontext.mContainerContext.getContentResolver() , currentYear , currentMonth );
				if( list.size() != 0 )
				{
					mHandler.obtainMessage( MSG_SUCCESS , list ).sendToTarget();
				}
			}
		} ).start();
	}
	
	public char[] getNum(
			int number )
	{
		String str = number + "";
		return str.toCharArray();
	}
	
	private void initBitmapPacker()
	{
		packerAtlas = new TextureAtlas();
		if( Utils3D.getScreenWidth() < 500 )
		{
			packer = new MyBitmapPacker( iLoongLauncher.getInstance() , 32 , 1 , false , R3D.filter , R3D.Magfilter , 300 , 300 , true , iLoongLauncher.mainThreadId , iLoongLauncher.getInstance()
					.getApplicationInfo().dataDir + "/" );
		}
		else
		{
			packer = new MyBitmapPacker( iLoongLauncher.getInstance() , 32 , 1 , false , R3D.filter , R3D.Magfilter , 400 , 400 , true , iLoongLauncher.mainThreadId , iLoongLauncher.getInstance()
					.getApplicationInfo().dataDir + "/" );
		}
		pack( "back.png" , CalenderHelper.getBitmap( "back.png" , mAppContext ) );
		pack( "backan.png" , CalenderHelper.getBitmap( "backan.png" , mAppContext ) );
		pack( "click.png" , CalenderHelper.getBitmap( "click.png" , mAppContext ) );
		pack( "jiahao.png" , CalenderHelper.getBitmap( "jiahao.png" , mAppContext ) );
		pack( "tex_calendar.png" , CalenderHelper.getBitmap( "tex_calendar.png" , mAppContext ) );
		pack( "xing.png" , CalenderHelper.getBitmap( "xing.png" , mAppContext ) );
		pack( "xuanzhong.png" , CalenderHelper.getBitmap( "xuanzhong.png" , mAppContext ) );
		pack( "background.jpg" , CalenderHelper.getBitmap( "background.jpg" , mAppContext ) );
		pack( "today.png" , CalenderHelper.getBitmap( "today.png" , mAppContext ) );
		pack( "week_1.png" , CalenderHelper.getBitmap( "week_1.png" , mAppContext ) );
		pack( "week_2.png" , CalenderHelper.getBitmap( "week_2.png" , mAppContext ) );
		pack( "week_3.png" , CalenderHelper.getBitmap( "week_3.png" , mAppContext ) );
		pack( "week_4.png" , CalenderHelper.getBitmap( "week_4.png" , mAppContext ) );
		pack( "week_5.png" , CalenderHelper.getBitmap( "week_5.png" , mAppContext ) );
		pack( "week_6.png" , CalenderHelper.getBitmap( "week_6.png" , mAppContext ) );
		pack( "week_7.png" , CalenderHelper.getBitmap( "week_7.png" , mAppContext ) );
		pack( "month_1.png" , CalenderHelper.getBitmap( "month_1.png" , mAppContext ) );
		pack( "month_2.png" , CalenderHelper.getBitmap( "month_2.png" , mAppContext ) );
		pack( "month_3.png" , CalenderHelper.getBitmap( "month_3.png" , mAppContext ) );
		pack( "month_4.png" , CalenderHelper.getBitmap( "month_4.png" , mAppContext ) );
		pack( "month_5.png" , CalenderHelper.getBitmap( "month_5.png" , mAppContext ) );
		pack( "month_6.png" , CalenderHelper.getBitmap( "month_6.png" , mAppContext ) );
		pack( "month_7.png" , CalenderHelper.getBitmap( "month_7.png" , mAppContext ) );
		pack( "month_8.png" , CalenderHelper.getBitmap( "month_8.png" , mAppContext ) );
		pack( "month_9.png" , CalenderHelper.getBitmap( "month_9.png" , mAppContext ) );
		pack( "month_10.png" , CalenderHelper.getBitmap( "month_10.png" , mAppContext ) );
		pack( "month_11.png" , CalenderHelper.getBitmap( "month_11.png" , mAppContext ) );
		pack( "month_12.png" , CalenderHelper.getBitmap( "month_12.png" , mAppContext ) );
		pack( "dayliang_1.png" , CalenderHelper.getBitmap( "1" , 99 , 99 , 99 ) );
		pack( "dayliang_2.png" , CalenderHelper.getBitmap( "2" , 99 , 99 , 99 ) );
		pack( "dayliang_3.png" , CalenderHelper.getBitmap( "3" , 99 , 99 , 99 ) );
		pack( "dayliang_4.png" , CalenderHelper.getBitmap( "4" , 99 , 99 , 99 ) );
		pack( "dayliang_5.png" , CalenderHelper.getBitmap( "5" , 99 , 99 , 99 ) );
		pack( "dayliang_6.png" , CalenderHelper.getBitmap( "6" , 99 , 99 , 99 ) );
		pack( "dayliang_7.png" , CalenderHelper.getBitmap( "7" , 99 , 99 , 99 ) );
		pack( "dayliang_8.png" , CalenderHelper.getBitmap( "8" , 99 , 99 , 99 ) );
		pack( "dayliang_9.png" , CalenderHelper.getBitmap( "9" , 99 , 99 , 99 ) );
		pack( "dayliang_10.png" , CalenderHelper.getBitmap( "10" , 99 , 99 , 99 ) );
		pack( "dayliang_11.png" , CalenderHelper.getBitmap( "11" , 99 , 99 , 99 ) );
		pack( "dayliang_12.png" , CalenderHelper.getBitmap( "12" , 99 , 99 , 99 ) );
		pack( "dayliang_13.png" , CalenderHelper.getBitmap( "13" , 99 , 99 , 99 ) );
		pack( "dayliang_14.png" , CalenderHelper.getBitmap( "14" , 99 , 99 , 99 ) );
		pack( "dayliang_15.png" , CalenderHelper.getBitmap( "15" , 99 , 99 , 99 ) );
		pack( "dayliang_16.png" , CalenderHelper.getBitmap( "16" , 99 , 99 , 99 ) );
		pack( "dayliang_17.png" , CalenderHelper.getBitmap( "17" , 99 , 99 , 99 ) );
		pack( "dayliang_18.png" , CalenderHelper.getBitmap( "18" , 99 , 99 , 99 ) );
		pack( "dayliang_19.png" , CalenderHelper.getBitmap( "19" , 99 , 99 , 99 ) );
		pack( "dayliang_20.png" , CalenderHelper.getBitmap( "20" , 99 , 99 , 99 ) );
		pack( "dayliang_21.png" , CalenderHelper.getBitmap( "21" , 99 , 99 , 99 ) );
		pack( "dayliang_22.png" , CalenderHelper.getBitmap( "22" , 99 , 99 , 99 ) );
		pack( "dayliang_23.png" , CalenderHelper.getBitmap( "23" , 99 , 99 , 99 ) );
		pack( "dayliang_24.png" , CalenderHelper.getBitmap( "24" , 99 , 99 , 99 ) );
		pack( "dayliang_25.png" , CalenderHelper.getBitmap( "25" , 99 , 99 , 99 ) );
		pack( "dayliang_26.png" , CalenderHelper.getBitmap( "26" , 99 , 99 , 99 ) );
		pack( "dayliang_27.png" , CalenderHelper.getBitmap( "27" , 99 , 99 , 99 ) );
		pack( "dayliang_28.png" , CalenderHelper.getBitmap( "28" , 99 , 99 , 99 ) );
		pack( "dayliang_29.png" , CalenderHelper.getBitmap( "29" , 99 , 99 , 99 ) );
		pack( "dayliang_30.png" , CalenderHelper.getBitmap( "30" , 99 , 99 , 99 ) );
		pack( "dayliang_31.png" , CalenderHelper.getBitmap( "31" , 99 , 99 , 99 ) );
		pack( "daylianglan_1.png" , CalenderHelper.getBitmap( "1" , 1 , 174 , 155 ) );
		pack( "daylianglan_2.png" , CalenderHelper.getBitmap( "2" , 1 , 174 , 155 ) );
		pack( "daylianglan_3.png" , CalenderHelper.getBitmap( "3" , 1 , 174 , 155 ) );
		pack( "daylianglan_4.png" , CalenderHelper.getBitmap( "4" , 1 , 174 , 155 ) );
		pack( "daylianglan_5.png" , CalenderHelper.getBitmap( "5" , 1 , 174 , 155 ) );
		pack( "daylianglan_6.png" , CalenderHelper.getBitmap( "6" , 1 , 174 , 155 ) );
		pack( "daylianglan_7.png" , CalenderHelper.getBitmap( "7" , 1 , 174 , 155 ) );
		pack( "daylianglan_8.png" , CalenderHelper.getBitmap( "8" , 1 , 174 , 155 ) );
		pack( "daylianglan_9.png" , CalenderHelper.getBitmap( "9" , 1 , 174 , 155 ) );
		pack( "daylianglan_10.png" , CalenderHelper.getBitmap( "10" , 1 , 174 , 155 ) );
		pack( "daylianglan_11.png" , CalenderHelper.getBitmap( "11" , 1 , 174 , 155 ) );
		pack( "daylianglan_12.png" , CalenderHelper.getBitmap( "12" , 1 , 174 , 155 ) );
		pack( "daylianglan_13.png" , CalenderHelper.getBitmap( "13" , 1 , 174 , 155 ) );
		pack( "daylianglan_14.png" , CalenderHelper.getBitmap( "14" , 1 , 174 , 155 ) );
		pack( "daylianglan_15.png" , CalenderHelper.getBitmap( "15" , 1 , 174 , 155 ) );
		pack( "daylianglan_16.png" , CalenderHelper.getBitmap( "16" , 1 , 174 , 155 ) );
		pack( "daylianglan_17.png" , CalenderHelper.getBitmap( "17" , 1 , 174 , 155 ) );
		pack( "daylianglan_18.png" , CalenderHelper.getBitmap( "18" , 1 , 174 , 155 ) );
		pack( "daylianglan_19.png" , CalenderHelper.getBitmap( "19" , 1 , 174 , 155 ) );
		pack( "daylianglan_20.png" , CalenderHelper.getBitmap( "20" , 1 , 174 , 155 ) );
		pack( "daylianglan_21.png" , CalenderHelper.getBitmap( "21" , 1 , 174 , 155 ) );
		pack( "daylianglan_22.png" , CalenderHelper.getBitmap( "22" , 1 , 174 , 155 ) );
		pack( "daylianglan_23.png" , CalenderHelper.getBitmap( "23" , 1 , 174 , 155 ) );
		pack( "daylianglan_24.png" , CalenderHelper.getBitmap( "24" , 1 , 174 , 155 ) );
		pack( "daylianglan_25.png" , CalenderHelper.getBitmap( "25" , 1 , 174 , 155 ) );
		pack( "daylianglan_26.png" , CalenderHelper.getBitmap( "26" , 1 , 174 , 155 ) );
		pack( "daylianglan_27.png" , CalenderHelper.getBitmap( "27" , 1 , 174 , 155 ) );
		pack( "daylianglan_28.png" , CalenderHelper.getBitmap( "28" , 1 , 174 , 155 ) );
		pack( "daylianglan_29.png" , CalenderHelper.getBitmap( "29" , 1 , 174 , 155 ) );
		pack( "daylianglan_30.png" , CalenderHelper.getBitmap( "30" , 1 , 174 , 155 ) );
		pack( "daylianglan_31.png" , CalenderHelper.getBitmap( "31" , 1 , 174 , 155 ) );
		pack( "dayan_1.png" , CalenderHelper.getBitmap( "1" , 200 , 200 , 200 ) );
		pack( "dayan_2.png" , CalenderHelper.getBitmap( "2" , 200 , 200 , 200 ) );
		pack( "dayan_3.png" , CalenderHelper.getBitmap( "3" , 200 , 200 , 200 ) );
		pack( "dayan_4.png" , CalenderHelper.getBitmap( "4" , 200 , 200 , 200 ) );
		pack( "dayan_5.png" , CalenderHelper.getBitmap( "5" , 200 , 200 , 200 ) );
		pack( "dayan_6.png" , CalenderHelper.getBitmap( "6" , 200 , 200 , 200 ) );
		pack( "dayan_7.png" , CalenderHelper.getBitmap( "7" , 200 , 200 , 200 ) );
		pack( "dayan_8.png" , CalenderHelper.getBitmap( "8" , 200 , 200 , 200 ) );
		pack( "dayan_9.png" , CalenderHelper.getBitmap( "9" , 200 , 200 , 200 ) );
		pack( "dayan_10.png" , CalenderHelper.getBitmap( "10" , 200 , 200 , 200 ) );
		pack( "dayan_11.png" , CalenderHelper.getBitmap( "11" , 200 , 200 , 200 ) );
		pack( "dayan_12.png" , CalenderHelper.getBitmap( "12" , 200 , 200 , 200 ) );
		pack( "dayan_13.png" , CalenderHelper.getBitmap( "13" , 200 , 200 , 200 ) );
		pack( "dayan_14.png" , CalenderHelper.getBitmap( "14" , 200 , 200 , 200 ) );
		pack( "dayan_15.png" , CalenderHelper.getBitmap( "15" , 200 , 200 , 200 ) );
		pack( "dayan_16.png" , CalenderHelper.getBitmap( "16" , 200 , 200 , 200 ) );
		pack( "dayan_17.png" , CalenderHelper.getBitmap( "17" , 200 , 200 , 200 ) );
		pack( "dayan_18.png" , CalenderHelper.getBitmap( "18" , 200 , 200 , 200 ) );
		pack( "dayan_19.png" , CalenderHelper.getBitmap( "19" , 200 , 200 , 200 ) );
		pack( "dayan_20.png" , CalenderHelper.getBitmap( "20" , 200 , 200 , 200 ) );
		pack( "dayan_21.png" , CalenderHelper.getBitmap( "21" , 200 , 200 , 200 ) );
		pack( "dayan_22.png" , CalenderHelper.getBitmap( "22" , 200 , 200 , 200 ) );
		pack( "dayan_23.png" , CalenderHelper.getBitmap( "23" , 200 , 200 , 200 ) );
		pack( "dayan_24.png" , CalenderHelper.getBitmap( "24" , 200 , 200 , 200 ) );
		pack( "dayan_25.png" , CalenderHelper.getBitmap( "25" , 200 , 200 , 200 ) );
		pack( "dayan_26.png" , CalenderHelper.getBitmap( "26" , 200 , 200 , 200 ) );
		pack( "dayan_27.png" , CalenderHelper.getBitmap( "27" , 200 , 200 , 200 ) );
		pack( "dayan_28.png" , CalenderHelper.getBitmap( "28" , 200 , 200 , 200 ) );
		pack( "dayan_29.png" , CalenderHelper.getBitmap( "29" , 200 , 200 , 200 ) );
		pack( "dayan_30.png" , CalenderHelper.getBitmap( "30" , 200 , 200 , 200 ) );
		pack( "dayan_31.png" , CalenderHelper.getBitmap( "31" , 200 , 200 , 200 ) );
		pack( "changtiaoclick.png" , CalenderHelper.getBitmap( "changtiaoclick.png" , mAppContext ) );
		pack( "tex_turbo.jpg" , CalenderHelper.getBitmap( "tex_turbo.jpg" , mAppContext ) );
		pack( "day_0.png" , CalenderHelper.getBitmap( "day_0.png" , mAppContext ) );
		pack( "day_1.png" , CalenderHelper.getBitmap( "day_1.png" , mAppContext ) );
		pack( "day_2.png" , CalenderHelper.getBitmap( "day_2.png" , mAppContext ) );
		pack( "day_3.png" , CalenderHelper.getBitmap( "day_3.png" , mAppContext ) );
		pack( "day_4.png" , CalenderHelper.getBitmap( "day_4.png" , mAppContext ) );
		pack( "day_5.png" , CalenderHelper.getBitmap( "day_5.png" , mAppContext ) );
		pack( "day_6.png" , CalenderHelper.getBitmap( "day_6.png" , mAppContext ) );
		pack( "day_7.png" , CalenderHelper.getBitmap( "day_7.png" , mAppContext ) );
		pack( "day_8.png" , CalenderHelper.getBitmap( "day_8.png" , mAppContext ) );
		pack( "day_9.png" , CalenderHelper.getBitmap( "day_9.png" , mAppContext ) );
		pack( "day_th.png" , CalenderHelper.getBitmap( "day_th.png" , mAppContext ) );
		pack( "plane.jpg" , CalenderHelper.getBitmap( "plane.jpg" , mAppContext ) );
		packer.updateTextureAtlas( packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
	}
	
	public static TextureRegion getRegion(
			String name )
	{
		return findRegion( name );
	}
	
	public static AtlasRegion findRegion(
			String name )
	{
		AtlasRegion ret = packerAtlas.findRegion( name );
		if( ret == null )
		{
			ret = findRegion( "tex_calendar.png" );
		}
		return ret;
	}
	
	public void pack(
			String name ,
			Bitmap p )
	{
		pack( name , p , true , false );
	}
	
	public void pack(
			String name ,
			Bitmap p ,
			boolean recycle ,
			boolean isDefault )
	{
		packer.pack( name , p , isDefault );
		if( p != IconCache.mDefaultIcon && recycle && !p.isRecycled() )
			p.recycle();
	}
	
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		calenderback.show();
		calenderback.bringToFront();
		if( m_changeOrder )
		{
			for( int i = 0 ; i < 6 ; i++ )
			{
				for( int j = 0 ; j < 7 ; j++ )
				{
					if( m_status == STATUS_UP )
					{
						if( scroll_count > 0 )
						{
							if( scrollY > 0 )
							{
								if( Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 >= 75 && Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 < 180 )
								{
									daydownviewgroup.get( i * 7 + j ).show();
									daydownviewgroup.get( i * 7 + j ).bringToFront();
									dayupviewgroup.get( i * 7 + j ).hide();
									if( markday == i * 7 + j )
									{
										todayglasses.hide();
									}
									if( listxingqian.size() != 0 )
									{
										for( int m = 0 ; m < countxingqian.size() ; m++ )
										{
											if( countxingqian.get( m ) == i * 7 + j )
											{
												listxingqian.get( m ).hide();
												break;
											}
										}
									}
									if( listxinghou.size() != 0 )
									{
										for( int m = 0 ; m < countxinghou.size() ; m++ )
										{
											if( countxinghou.get( m ) == i * 7 + j )
											{
												listxinghou.get( m ).show();
												listxinghou.get( m ).bringToFront();
												break;
											}
										}
									}
								}
							}
							else
							{
								if( Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 > 0 && Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 <= 105 )
								{
									daydownviewgroup.get( i * 7 + j ).show();
									daydownviewgroup.get( i * 7 + j ).bringToFront();
									dayupviewgroup.get( i * 7 + j ).hide();
									if( markday == i * 7 + j )
									{
										todayglasses.hide();
									}
									if( listxingqian.size() != 0 )
									{
										for( int m = 0 ; m < countxingqian.size() ; m++ )
										{
											if( countxingqian.get( m ) == i * 7 + j )
											{
												listxingqian.get( m ).hide();
												break;
											}
										}
									}
									if( listxinghou.size() != 0 )
									{
										for( int m = 0 ; m < countxinghou.size() ; m++ )
										{
											if( countxinghou.get( m ) == i * 7 + j )
											{
												listxinghou.get( m ).show();
												listxinghou.get( m ).bringToFront();
												break;
											}
										}
									}
								}
							}
						}
						else
						{
							if( scrollY > 0 )
							{
								if( Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 > 0 && Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 <= 105 )
								{
									daydownviewgroup.get( i * 7 + j ).show();
									daydownviewgroup.get( i * 7 + j ).bringToFront();
									dayupviewgroup.get( i * 7 + j ).hide();
									if( markday == i * 7 + j )
									{
										todayglasses.hide();
									}
									if( listxingqian.size() != 0 )
									{
										for( int m = 0 ; m < countxingqian.size() ; m++ )
										{
											if( countxingqian.get( m ) == i * 7 + j )
											{
												listxingqian.get( m ).hide();
												break;
											}
										}
									}
									if( listxinghou.size() != 0 )
									{
										for( int m = 0 ; m < countxinghou.size() ; m++ )
										{
											if( countxinghou.get( m ) == i * 7 + j )
											{
												listxinghou.get( m ).show();
												listxinghou.get( m ).bringToFront();
												break;
											}
										}
									}
								}
							}
							else
							{
								if( Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 >= 75 && Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 < 180 )
								{
									daydownviewgroup.get( i * 7 + j ).show();
									daydownviewgroup.get( i * 7 + j ).bringToFront();
									dayupviewgroup.get( i * 7 + j ).hide();
									if( markday == i * 7 + j )
									{
										todayglasses.hide();
									}
									if( listxingqian.size() != 0 )
									{
										for( int m = 0 ; m < countxingqian.size() ; m++ )
										{
											if( countxingqian.get( m ) == i * 7 + j )
											{
												listxingqian.get( m ).hide();
												break;
											}
										}
									}
									if( listxinghou.size() != 0 )
									{
										for( int m = 0 ; m < countxinghou.size() ; m++ )
										{
											if( countxinghou.get( m ) == i * 7 + j )
											{
												listxinghou.get( m ).show();
												listxinghou.get( m ).bringToFront();
												break;
											}
										}
									}
								}
							}
						}
					}
					else
					{
						if( scroll_count >= 0 )
						{
							if( scrollY > 0 )
							{
								if( Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 >= 75 && Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 < 180 )
								{
									dayupviewgroup.get( i * 7 + j ).show();
									dayupviewgroup.get( i * 7 + j ).bringToFront();
									daydownviewgroup.get( i * 7 + j ).hide();
									if( markday == i * 7 + j && nowMonth == CalenderHelper.getCurrentMonth() && nowYear == CalenderHelper.getCurrentYear() && nowDay == CalenderHelper.getCurrentDay() )
									{
										todayglasses.show();
										todayglasses.bringToFront();
									}
									if( listxingqian.size() != 0 )
									{
										for( int m = 0 ; m < countxingqian.size() ; m++ )
										{
											if( countxingqian.get( m ) == i * 7 + j )
											{
												listxingqian.get( m ).show();
												listxingqian.get( m ).bringToFront();
												break;
											}
										}
									}
									if( listxinghou.size() != 0 )
									{
										for( int m = 0 ; m < countxinghou.size() ; m++ )
										{
											if( countxinghou.get( m ) == i * 7 + j )
											{
												listxinghou.get( m ).hide();
												break;
											}
										}
									}
								}
							}
							else
							{
								if( Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 > 0 && Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 <= 105 )
								{
									dayupviewgroup.get( i * 7 + j ).show();
									dayupviewgroup.get( i * 7 + j ).bringToFront();
									daydownviewgroup.get( i * 7 + j ).hide();
									if( markday == i * 7 + j && nowMonth == CalenderHelper.getCurrentMonth() && nowYear == CalenderHelper.getCurrentYear() && nowDay == CalenderHelper.getCurrentDay() )
									{
										todayglasses.show();
										todayglasses.bringToFront();
									}
									if( listxingqian.size() != 0 )
									{
										for( int m = 0 ; m < countxingqian.size() ; m++ )
										{
											if( countxingqian.get( m ) == i * 7 + j )
											{
												listxingqian.get( m ).show();
												listxingqian.get( m ).bringToFront();
												break;
											}
										}
									}
									if( listxinghou.size() != 0 )
									{
										for( int m = 0 ; m < countxinghou.size() ; m++ )
										{
											if( countxinghou.get( m ) == i * 7 + j )
											{
												listxinghou.get( m ).hide();
												break;
											}
										}
									}
								}
							}
						}
						else
						{
							if( scrollY > 0 )
							{
								if( Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 > 0 && Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 <= 105 )
								{
									dayupviewgroup.get( i * 7 + j ).show();
									dayupviewgroup.get( i * 7 + j ).bringToFront();
									daydownviewgroup.get( i * 7 + j ).hide();
									if( markday == i * 7 + j && nowMonth == CalenderHelper.getCurrentMonth() && nowYear == CalenderHelper.getCurrentYear() && nowDay == CalenderHelper.getCurrentDay() )
									{
										todayglasses.show();
										todayglasses.bringToFront();
									}
									if( listxingqian.size() != 0 )
									{
										for( int m = 0 ; m < countxingqian.size() ; m++ )
										{
											if( countxingqian.get( m ) == i * 7 + j )
											{
												listxingqian.get( m ).show();
												listxingqian.get( m ).bringToFront();
												break;
											}
										}
									}
									if( listxinghou.size() != 0 )
									{
										for( int m = 0 ; m < countxinghou.size() ; m++ )
										{
											if( countxinghou.get( m ) == i * 7 + j )
											{
												listxinghou.get( m ).hide();
												break;
											}
										}
									}
								}
							}
							else
							{
								if( Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 >= 75 && Math.abs( daygroup.get( i * 7 + j ).getRotation() ) % 180 < 180 )
								{
									dayupviewgroup.get( i * 7 + j ).show();
									dayupviewgroup.get( i * 7 + j ).bringToFront();
									daydownviewgroup.get( i * 7 + j ).hide();
									if( markday == i * 7 + j && nowMonth == CalenderHelper.getCurrentMonth() && nowYear == CalenderHelper.getCurrentYear() && nowDay == CalenderHelper.getCurrentDay() )
									{
										todayglasses.show();
										todayglasses.bringToFront();
									}
									if( listxingqian.size() != 0 )
									{
										for( int m = 0 ; m < countxingqian.size() ; m++ )
										{
											if( countxingqian.get( m ) == i * 7 + j )
											{
												listxingqian.get( m ).show();
												listxingqian.get( m ).bringToFront();
												break;
											}
										}
									}
									if( listxinghou.size() != 0 )
									{
										for( int m = 0 ; m < countxinghou.size() ; m++ )
										{
											if( countxinghou.get( m ) == i * 7 + j )
											{
												listxinghou.get( m ).hide();
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if( m_clickBack )
		{
			for( int i = 0 ; i < 6 ; i++ )
			{
				for( int j = 0 ; j < 7 ; j++ )
				{
					dayupviewgroup.get( i * 7 + j ).show();
					dayupviewgroup.get( i * 7 + j ).bringToFront();
					daydownviewgroup.get( i * 7 + j ).hide();
				}
			}
		}
		//		long prevTime = System.currentTimeMillis();
		//		cooGdx.gl.glEnable( GL10.GL_CULL_FACE );
		//		cooGdx.gl.glCullFace( GL10.GL_BACK );
		super.draw( batch , parentAlpha );
		//		cooGdx.gl.glDisable( GL10.GL_CULL_FACE );
		// batch.end();
	}
	
	AddEventGroup addenentgroup = null;
	DayGlasses clicktoday = null;
	DayGlasses clicktodayXiaoGuo = null;
	private int record = -1;
	private List<DayGlasses> recordEvents = new ArrayList<DayGlasses>();
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( type == TweenCallback.BEGIN && source.equals( timelineclickin ) )
		{
			if( clickXiaoGuo != null )
			{
				this.removeView( clickXiaoGuo );
			}
		}
		if( type == TweenCallback.COMPLETE && source.equals( timelineclickin ) )
		{
			if( listxinghou.size() != 0 )
			{
				for( int j = 0 ; j < listxinghou.size() ; j++ )
				{
					daygroup.get( countxinghou.get( j ) ).removeView( listxinghou.get( j ) );
				}
				countxinghou.clear();
				listxinghou.clear();
			}
			if( listxingqian.size() != 0 )
			{
				for( int j = 0 ; j < listxingqian.size() ; j++ )
				{
					daygroup.get( countxingqian.get( j ) ).removeView( listxingqian.get( j ) );
				}
				listxingqian.clear();
				countxingqian.clear();
			}
			todayglasses.hide();
			if( clickXiaoGuo != null )
			{
				this.removeView( clickXiaoGuo );
			}
			calenderdayplane.hide();
			for( int i = 0 ; i < dayglasses.size() ; i++ )
			{
				dayglasses.get( i ).show();
			}
			for( int i = 0 ; i < daypitchlistqian.size() ; i++ )
			{
				daypitchlistqian.get( i ).show();
			}
			if( daypitchlisthou.size() > 0 )
			{
				for( int i = 0 ; i < daypitchlisthou.size() ; i++ )
				{
					daypitchlisthou.get( i ).show();
				}
			}
			for( int i = 7 ; i < daygroup.size() ; i++ )
			{
				daygroup.get( i ).hide();
			}
			for( int i = 0 ; i < 7 ; i++ )
			{
				if( nowYear == CalenderHelper.getCurrentYear() && nowMonth == CalenderHelper.getCurrentMonth() )
				{
					if( !days[hang * 7 + i].contains( "*" ) )
					{
						if( Integer.parseInt( days[hang * 7 + i] ) == CalenderHelper.getCurrentDay() )
						{
							clicktoday = new DayGlasses( mAppContext , "clicktoday" , textoday_region , "mod_shadow.obj" );
							clicktoday.build();
							clicktoday.move( origionDayMoveWidth + ( Parameter.Calender_Day_Glass_Width * i ) * scale , origionDayMoveHeight - offset * 11f , 0 );
							daygroup.get( i ).addView( clicktoday );
							record = i;
						}
					}
				}
				if( scrollcount_all % 2 == 0 )
				{
					if( days[hang * 7 + i].contains( "*" ) )
					{
						String replace = days[hang * 7 + i].replace( "*" , "" );
						daypitchlistqian.get( i ).region.setRegion( getRegion( "dayan_" + replace + ".png" ) );
					}
					else
					{
						daypitchlistqian.get( i ).region.setRegion( getRegion( "dayliang_" + days[hang * 7 + i] + ".png" ) );
					}
				}
				else
				{
					if( days[hang * 7 + i].contains( "*" ) )
					{
						String replace = days[hang * 7 + i].replace( "*" , "" );
						daypitchlisthou.get( i ).region.setRegion( getRegion( "dayan_" + replace + ".png" ) );
					}
					else
					{
						daypitchlisthou.get( i ).region.setRegion( getRegion( "dayliang_" + days[hang * 7 + i] + ".png" ) );
					}
				}
			}
			clicktodayXiaoGuo = new DayGlasses( mAppContext , "clicktodayXiaoGuo" , texxuanzhong_region , "mod_shadow.obj" );
			clicktodayXiaoGuo.build();
			clicktodayXiaoGuo.move( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * lie * scale , origionDayMoveHeight - offset * 11f , 0 );
			this.addView( clicktodayXiaoGuo );
			//这时需要数据库操作，需要在线程里进行，否则会卡
			addenentgroup.show();
			for( int j = 0 ; j < 7 ; j++ )
			{
				if( !days[hang * 7 + j].contains( "*" ) )
				{
					//					boolean ifhasEvents = CalenderHelper.judgeDayIfEvents( mAppContext.mContainerContext.getContentResolver() , nowYear , nowMonth , Integer.parseInt( days[hang * 7 + j] ) );
					//					if( ifhasEvents )
					//					{
					if( list.size() != 0 )
					{
						for( int k = 0 ; k < list.size() ; k++ )
						{
							if( list.get( k ).equals( days[hang * 7 + j] ) )
							{
								DayGlasses dayhasevents = new DayGlasses( mAppContext , "dayhasevents" + k , texxing_region , "mod_shadow.obj" );
								dayhasevents.build();
								dayhasevents.move( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * j * scale , origionDayMoveHeight - offset * 11f , 0 );
								WidgetCalender.this.addView( dayhasevents );
								recordEvents.add( dayhasevents );
							}
						}
					}
					//					}
				}
			}
			if( timelineclickout != null )
			{
				timelineclickout.free();
				timelineclickout = null;
			}
			timelineclickout = Timeline.createParallel();
			timelineclickout.push( Tween.to( calenderalltop , View3DTweenAccessor.POS_XY , 0.75f ).target( 0 , 0 , 0 ).ease( Quad.INOUT ) );
			timelineclickout.push( Tween.to( calenderalldown , View3DTweenAccessor.POS_XY , 0.75f ).target( 0 , 0 , 0 ).ease( Quad.INOUT ) );
			timelineclickout.start( View3DTweenAccessor.manager ).setCallback( this );
			calenderback.region.setRegion( texback_region );
			ifBack = true;
		}
		if( type == TweenCallback.COMPLETE && source.equals( timelineclickout ) )
		{
			addenentgroup.updateNowEvents( nowYear , nowMonth , Integer.parseInt( days[hang * 7 + lie] ) );
			ifstartTween = false;
		}
		if( type == TweenCallback.COMPLETE && source.equals( timelineback ) )
		{
			if( addenentgroup != null )
			{
				addenentgroup.hide();
			}
			for( int i = 7 ; i < daygroup.size() ; i++ )
			{
				daygroup.get( i ).show();
			}
			for( int i = 0 ; i < 6 ; i++ )
			{
				for( int j = 0 ; j < 7 ; j++ )
				{
					daygroup.get( i * 7 + j ).setRotationAngle( 0 , 0 , 0 );
				}
			}
			if( daypitchlistqian.size() != 0 && daypitchlisthou.size() != 0 )
			{
				for( int i = 0 ; i < daypitchlistqian.size() ; i++ )
				{
					daypitchlistqian.get( i ).show();
					daypitchlisthou.get( i ).hide();
				}
			}
			if( clicktoday != null && record != -1 )
			{
				daygroup.get( record ).removeView( clicktoday );
			}
			if( clicktodayXiaoGuo != null )
			{
				this.removeView( clicktodayXiaoGuo );
			}
			if( recordEvents.size() != 0 )
			{
				for( int i = 0 ; i < recordEvents.size() ; i++ )
				{
					this.removeView( recordEvents.get( i ) );
				}
				recordEvents.clear();
			}
			nowMonth = CalenderHelper.getCurrentMonth();
			nowYear = CalenderHelper.getCurrentYear();
			nowDay = CalenderHelper.getCurrentDay();
			scrollcount_all = 0;
			scroll_count = 0;
			updateRegion( nowMonth , nowYear , nowDay );
			if( listxinghou.size() != 0 )
			{
				for( int j = 0 ; j < listxinghou.size() ; j++ )
				{
					daygroup.get( countxinghou.get( j ) ).removeView( listxinghou.get( j ) );
				}
				countxinghou.clear();
				listxinghou.clear();
			}
			if( listxingqian.size() != 0 )
			{
				for( int j = 0 ; j < listxingqian.size() ; j++ )
				{
					daygroup.get( countxingqian.get( j ) ).removeView( listxingqian.get( j ) );
				}
				listxingqian.clear();
				countxingqian.clear();
			}
			startBackRunnable( nowYear , nowMonth );
			if( markday != -1 )
			{
				daygroup.get( markday ).removeView( todayglasses );
			}
			setToday();
			m_clickBack = true;
			if( timelinebackout != null )
			{
				timelinebackout.free();
				timelinebackout = null;
			}
			timelinebackout = Timeline.createParallel();
			timelinebackout.push( Tween.to( calenderalltop , View3DTweenAccessor.POS_XY , 0.75f ).target( 0 , 0 , 0 ).ease( Quad.INOUT ) );
			timelinebackout.push( Tween.to( calenderalldown , View3DTweenAccessor.POS_XY , 0.75f ).target( 0 , 0 , 0 ).ease( Quad.INOUT ) );
			timelinebackout.start( View3DTweenAccessor.manager ).setCallback( this );
			calenderback.region.setRegion( texbackan_region );
			ifBack = false;
		}
		if( type == TweenCallback.COMPLETE && source.equals( timelinebackout ) )
		{
			m_status = STATUS_UP;
			//			updateEvents( nowYear , nowMonth );
			//			startRunnable( nowYear , nowMonth );
			m_clickBack = false;
			ifstartclickbacktimeline = false;
		}
		if( type == TweenCallback.COMPLETE && source.equals( animation_rotation ) )
		{
			if( !ifShowNowday )
			{
				todayglasses.hide();
			}
			if( !ifShowBack )
			{
				ifBack = false;
				calenderback.region.setRegion( texbackan_region );
			}
			if( scrollcount_all % 2 == 0 )
			{
				for( int i = 0 ; i < 6 ; i++ )
				{
					for( int j = 0 ; j < 7 ; j++ )
					{
						dayupviewgroup.get( i * 7 + j ).show();
						dayupviewgroup.get( i * 7 + j ).bringToFront();
						daydownviewgroup.get( i * 7 + j ).hide();
					}
				}
				m_status = STATUS_UP;
				m_changeOrder = false;
				if( listxinghou.size() != 0 )
				{
					for( int j = 0 ; j < listxinghou.size() ; j++ )
					{
						daygroup.get( countxinghou.get( j ) ).removeView( listxinghou.get( j ) );
					}
					countxinghou.clear();
					listxinghou.clear();
				}
				if( daypitchlisthou.size() != 0 )
				{
					for( int i = 0 ; i < daypitchlisthou.size() ; i++ )
					{
						daypitchlisthou.get( i ).hide();
					}
				}
			}
			else
			{
				for( int i = 0 ; i < 6 ; i++ )
				{
					for( int j = 0 ; j < 7 ; j++ )
					{
						daydownviewgroup.get( i * 7 + j ).show();
						daydownviewgroup.get( i * 7 + j ).bringToFront();
						dayupviewgroup.get( i * 7 + j ).hide();
					}
				}
				m_status = STATUS_DOWN;
				m_changeOrder = false;
				if( listxingqian.size() != 0 )
				{
					for( int j = 0 ; j < listxingqian.size() ; j++ )
					{
						daygroup.get( countxingqian.get( j ) ).removeView( listxingqian.get( j ) );
					}
					listxingqian.clear();
					countxingqian.clear();
				}
				if( daypitchlistqian.size() != 0 )
				{
					for( int i = 0 ; i < daypitchlistqian.size() ; i++ )
					{
						daypitchlistqian.get( i ).hide();
					}
				}
			}
			ifstartRotateTween = false;
			this.releaseFocus();
		}
		super.onEvent( type , source );
	}
	
	@Override
	public WidgetPluginViewMetaData getPluginViewMetaData()
	{
		WidgetPluginViewMetaData metaData = new WidgetPluginViewMetaData();
		metaData.spanX = Integer.valueOf( mAppContext.mWidgetContext.getResources().getInteger( R.integer.spanX ) );
		metaData.spanY = 4;
		metaData.maxInstanceCount = mAppContext.mWidgetContext.getResources().getInteger( R.integer.max_instance );
		String lan = Locale.getDefault().getLanguage();
		if( lan.equals( "zh" ) )
		{
			metaData.maxInstanceAlert = "已存在，不可重新添加";
		}
		else
		{
			metaData.maxInstanceAlert = "Already exists, can not add another one";
		}
		return metaData;
	}
	
	private int hang;
	private int lie;
	private DayGlasses clickXiaoGuo;
	private int prelie = 0;
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		float movediatance = ( nowHeight * 6 + Parameter.Calender_Week_Glass_Height * scale ) / 2;
		float backHeight = origionWeekMoveHeight * scale - offset * 12f + Parameter.Calender_Week_Glass_Height / 2;
		if( ifstartTween || ifstartRotateTween || ifstartclickbacktimeline )
		{
			return true;
		}
		//origionBackMoveWidth - backViewGroup3D.width/2 , origionBackMoveHeight  - offset * 12f - backViewGroup3D.height/2
		//calenderback
		if( ifBack )
		{
			//			if( x > leftLowerCornerWidth + nowWidth * 6 && x < leftLowerCornerWidth + nowWidth * 7 + 20 * scale && y > backHeight && y < backHeight + Parameter.Calender_Top_JinShuTiao + 20 * scale )
			if( x > origionBackMoveWidth - backViewGroup3D.width / 2 && x < origionBackMoveWidth + backViewGroup3D.width / 2 && y > origionBackMoveHeight - offset * 12f - backViewGroup3D.height / 2 && y < origionBackMoveHeight - offset * 12f + backViewGroup3D.height / 2 )
			{
				ifstartclickbacktimeline = true;
				if( timelineback != null )
				{
					timelineback.free();
					timelineback = null;
				}
				timelineback = Timeline.createParallel();
				timelineback.push( Tween.to( calenderalltop , View3DTweenAccessor.POS_XY , 0.75f ).target( 0 , -movediatance , 0 ).ease( Quad.INOUT ) );
				timelineback.push( Tween.to( calenderalldown , View3DTweenAccessor.POS_XY , 0.75f ).target( 0 , movediatance , 0 ).ease( Quad.INOUT ) );
				timelineback.start( View3DTweenAccessor.manager ).setCallback( this );
				return true;
			}
		}
		if( !( addenentgroup.visible ) )
		{
			if( x > leftLowerCornerWidth && x < leftLowerCornerWidth + nowWidth * 7 && y < leftLowerCornerHeight + nowHeight * 6 && y > leftLowerCornerHeight )
			{
				hang = 5 - (int)( ( y - leftLowerCornerHeight ) / nowHeight );
				lie = (int)( ( x - leftLowerCornerWidth ) / nowWidth );
				if( !days[hang * 7 + lie].contains( "*" ) )
				{
					ifstartTween = true;
					if( timelineclickin != null )
					{
						timelineclickin.free();
						timelineclickin = null;
					}
					clickXiaoGuo = new DayGlasses( mAppContext , "clickday" , getRegion( "click.png" ) , "mod_shadow.obj" );
					clickXiaoGuo.build();
					clickXiaoGuo.move(
							origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * lie * scale ,
							origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * hang * scale - offset * ( 11f - 2 * hang ) ,
							0 );
					this.addView( clickXiaoGuo );
					timelineclickin = Timeline.createParallel();
					timelineclickin.push( Tween.to( calenderalltop , View3DTweenAccessor.POS_XY , 0.75f ).target( 0 , -movediatance , 0 ).ease( Quad.INOUT ) );
					timelineclickin.push( Tween.to( calenderalldown , View3DTweenAccessor.POS_XY , 0.75f ).target( 0 , movediatance , 0 ).ease( Quad.INOUT ) );
					timelineclickin.start( View3DTweenAccessor.manager ).setCallback( this );
				}
			}
			return true;
		}
		if( addenentgroup != null && addenentgroup.visible )
		{
			if( x > leftLowerCornerWidth && x < leftLowerCornerWidth + nowWidth * 7 && y < leftLowerCornerHeight + nowHeight * 6 && y > leftLowerCornerHeight + nowHeight * 5 )
			{
				if( !days[hang * 7 + lie].contains( "*" ) )
				{
					prelie = lie;
				}
				lie = (int)( ( x - leftLowerCornerWidth ) / nowWidth );
				if( !days[hang * 7 + lie].contains( "*" ) )
				{
					clicktodayXiaoGuo.move( ( lie - prelie ) * Parameter.Calender_Day_Glass_Width * scale , 0 , 0 );
					mAppContext.mGdxApplication.postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							addenentgroup.updateNowEvents( nowYear , nowMonth , Integer.parseInt( days[hang * 7 + lie] ) );
							AddEventGroup.year = nowYear;
							AddEventGroup.month = nowMonth;
							AddEventGroup.day = Integer.parseInt( days[hang * 7 + lie] );
						}
					} );
				}
				return true;
			}
			return super.onClick( x , y );
		}
		return super.onClick( x , y );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		ifworkspacemove = false;
		ifscroll = false;
		if( !( addenentgroup != null && addenentgroup.visible ) )
		{
			this.requestFocus();
		}
		return super.onTouchDown( x , y , pointer );
	}
	
	private int scroll_count = 0;
	private int scrollcount_all = 0;
	private boolean ifShowNowday = true;
	private boolean ifShowBack = false;
	private boolean ifworkspacemove = false;
	private boolean ifscroll = false;
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( ifstartTween || ifstartRotateTween || ifstartclickbacktimeline )
		{
			return true;
		}
		if( clickXiaoGuo != null )
		{
			this.removeView( clickXiaoGuo );
		}
		if( addenentgroup != null )
		{
			if( addenentgroup.visible )
			{
				return super.scroll( x , y , deltaX , deltaY );
			}
		}
		//		this.requestFocus();
		float k = 0;
		if( deltaY != 0 && deltaY != 0 )
		{
			k = deltaY / deltaX;
		}
		if( !ifscroll )
		{
			if( ifworkspacemove )
			{
				calenderdayplane.show();
				for( int i = 0 ; i < dayglasses.size() ; i++ )
				{
					dayglasses.get( i ).hide();
				}
				for( int i = 0 ; i < daypitchlistqian.size() ; i++ )
				{
					daypitchlistqian.get( i ).hide();
				}
				if( daypitchlisthou.size() > 0 )
				{
					for( int i = 0 ; i < daypitchlisthou.size() ; i++ )
					{
						daypitchlisthou.get( i ).hide();
					}
				}
				this.releaseFocus();
				return super.scroll( x , y , deltaX , deltaY );
			}
			if( k >= -1 && k <= 1 )
			{
				calenderdayplane.show();
				for( int i = 0 ; i < dayglasses.size() ; i++ )
				{
					dayglasses.get( i ).hide();
				}
				for( int i = 0 ; i < daypitchlistqian.size() ; i++ )
				{
					daypitchlistqian.get( i ).hide();
				}
				if( daypitchlisthou.size() > 0 )
				{
					for( int i = 0 ; i < daypitchlisthou.size() ; i++ )
					{
						daypitchlisthou.get( i ).hide();
					}
				}
				ifworkspacemove = true;
				ifscroll = false;
				this.releaseFocus();
				return super.scroll( x , y , deltaX , deltaY );
			}
			else
			{
				ifworkspacemove = false;
				ifscroll = true;
			}
		}
		startTween( x , y , deltaX , deltaY );
		return true;
	}
	
	//	private void addNewPitch()
	//	{
	//		//		Log.d( "Calender" , "addNewPitch" );
	//		for( int i = 0 ; i < 6 ; i++ )
	//		{
	//			for( int j = 0 ; j < 7 ; j++ )
	//			{
	//				DayGroup3D dg = new DayGroup3D( "dg" );
	//				WeekGlasses calenderpitchday = new WeekGlasses( mAppContext , "calenderpitchday" , getRegion( "dayliang_1.png" ) , "mod_number(day).obj" );
	//				calenderpitchday.build();
	//				calenderpitchday.move(
	//						( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * j ) * scale ,
	//						( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * i ) * scale - offset * ( 11f - 2 * i ) ,
	//						0 );
	//				dg.addView( calenderpitchday );
	//				daypitchlisthou.add( calenderpitchday );
	//				calenderpitchday.hide();
	//				dg.setOrigin(
	//						( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * j ) * scale ,
	//						( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * i ) * scale - offset * ( 11f - 2 * i ) );
	//				dg.setOriginZ( 0 );
	//				dg.setRotationVector( 1 , 0 , 0 );
	//				dg.setRotationAngle( 180 , 0 , 0 );
	//				//				daygroup.get( i * 7 + j ).addView( dg );
	//			}
	//		}
	//	}
	public float scrollY = 0;
	
	public void startTween(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		m_changeOrder = true;
		m_clickBack = false;
		scrollY = deltaY;
		if( x > leftLowerCornerWidth && x < leftLowerCornerWidth + nowWidth * 7 && y < leftLowerCornerHeight + nowHeight * 6 && y > leftLowerCornerHeight )
		{
			calenderdayplane.hide();
			for( int i = 0 ; i < dayglasses.size() ; i++ )
			{
				dayglasses.get( i ).show();
			}
			for( int i = 0 ; i < daypitchlistqian.size() ; i++ )
			{
				daypitchlistqian.get( i ).show();
			}
			for( int i = 0 ; i < 42 ; i++ )
			{
				if( daypitchlistqian.get( i ).visible )
				{
					daypitchlisthou.get( i ).show();
				}
				else
				{
					daypitchlistqian.get( i ).show();
				}
			}
			if( animation_rotation != null )
			{
				animation_rotation.free();
				animation_rotation = null;
			}
			animation_rotation = Timeline.createParallel();
			if( deltaY > 0 )
			{
				if( nowMonth == 11 )
				{
					nowMonth = 0;
					nowYear += 1;
				}
				else
				{
					nowMonth += 1;
				}
				scrollcount_all++;
				scroll_count++;
				updateRegion( nowMonth , nowYear , nowDay );
				startRunnable( nowYear , nowMonth );
				ifstartRotateTween = true;
				for( int i = 0 ; i < 6 ; i++ )
				{
					for( int j = 0 ; j < 7 ; j++ )
					{
						daygroup.get( i * 7 + j ).setOriginZ( 0 );
						daygroup.get( i * 7 + j ).setRotationVector( 1 , 0 , 0 );
						animation_rotation.push( Tween.to( daygroup.get( i * 7 + j ) , View3DTweenAccessor.ROTATION , 0.3f ).target( 180 * scroll_count , 0 , 0 ).ease( Linear.INOUT )
								.delay( 0.12f * ( i + j + 1 ) ) );
					}
				}
				if( nowMonth == CalenderHelper.getCurrentMonth() && nowYear == CalenderHelper.getCurrentYear() && calenderback.visible )
				{
					ifBack = false;
					calenderback.region.setRegion( texbackan_region );
					ifShowNowday = true;
					ifShowBack = false;
				}
				else if( ifBack )
				{
				}
				else
				{
					ifBack = true;
					calenderback.region.setRegion( texback_region );
					ifShowNowday = false;
					ifShowBack = true;
				}
				MobclickAgent.onEvent( mContext , "CalenderOverturn" );
				animation_rotation.start( View3DTweenAccessor.manager ).setCallback( this );
			}
			else if( deltaY < 0 )
			{
				if( nowMonth == 0 )
				{
					nowMonth = 11;
					nowYear -= 1;
				}
				else
				{
					nowMonth -= 1;
				}
				scrollcount_all++;
				scroll_count--;
				updateRegion( nowMonth , nowYear , nowDay );
				startRunnable( nowYear , nowMonth );
				ifstartRotateTween = true;
				for( int i = 0 ; i < 6 ; i++ )
				{
					for( int j = 0 ; j < 7 ; j++ )
					{
						daygroup.get( i * 7 + j ).setOriginZ( 0 );
						daygroup.get( i * 7 + j ).setRotationVector( 1 , 0 , 0 );
						animation_rotation.push( Tween.to( daygroup.get( i * 7 + j ) , View3DTweenAccessor.ROTATION , 0.3f ).target( 180 * scroll_count , 0 , 0 ).ease( Linear.INOUT )
								.delay( 0.12f * ( Math.abs( i - 5 ) + Math.abs( j - 6 ) + 1 ) ) );
					}
				}
				if( nowMonth == CalenderHelper.getCurrentMonth() && nowYear == CalenderHelper.getCurrentYear() && calenderback.visible )
				{
					ifBack = false;
					calenderback.region.setRegion( texbackan_region );
					ifShowNowday = true;
					ifShowBack = false;
				}
				else if( ifBack )
				{
				}
				else
				{
					calenderback.show();
					//					moveTimeline( topoffSet * scale );
					ifBack = true;
					//					calenderback.region = getRegion( "back.png" );
					//					updateRegionObj( calenderback , getRegion( "back.png" ) );
					calenderback.region.setRegion( texback_region );
					ifShowNowday = false;
					ifShowBack = true;
				}
				MobclickAgent.onEvent( mContext , "CalenderOverturn" );
				animation_rotation.start( View3DTweenAccessor.manager ).setCallback( this );
			}
		}
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( clickXiaoGuo != null )
		{
			this.removeView( clickXiaoGuo );
		}
		this.releaseFocus();
		return false;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( clickXiaoGuo != null )
		{
			this.removeView( clickXiaoGuo );
		}
		//		Log.d( "Calendar" , "onLongClick" );
		this.point.x = x;
		this.point.y = y;
		this.toAbsolute( point );
		Vector2 obj = new Vector2( point.x , point.y );
		this.setTag( obj );
		this.releaseFocus();
		return viewParent.onCtrlEvent( this , 0 );
	}
	
	@Override
	public boolean pointerInParent(
			float x ,
			float y )
	{
		return super.pointerInParent( x , y );
	}
}
