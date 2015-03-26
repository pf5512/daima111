package com.iLoong.Calender.view;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooeeui.cometcalendar.R;
import com.iLoong.Calender.activity.AdActivity;
import com.iLoong.Calender.common.CalenderHelper;
import com.iLoong.Calender.common.Parameter;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class WidgetCalender extends WidgetPluginView3D
{
	
	// 3D模型统一宽度高度
	public static float MODEL_WIDTH = 320;
	public static float MODEL_HEIGHT = 320;
	public static MainAppContext mAppContext;
	private Context mContext = null;
	public static CooGdx cooGdx;
	private MainAppContext maincontext = null;
	public static WidgetCalender widgetcalender;
	public static float scale = 1f;
	public static float height_scale = 1f;
	public static float offset = 1f;
	private final float topoffSet = Parameter.Calender_Back;
	private final float origionTopMoveWidth = Parameter.Origin_To_Origin_Width + Parameter.Origin_To_Up_Width;
	private final float origionTopMoveHeight = Parameter.Origin_To_Origin_Height + Parameter.Origin_To_Up_Height;
	private final float origionTop01MoveWidth = Parameter.Origin_To_Origin_Width + Parameter.Origin_To_Up01_Width;
	private final float origionTop01MoveHeight = Parameter.Origin_To_Origin_Height + Parameter.Origin_To_Up01_Height;
	private final float origionDownMoveWidth = Parameter.Origin_To_Origin_Width + Parameter.Origin_To_Down_Width;
	private final float origionDownMoveHeight = Parameter.Origin_To_Origin_Height + Parameter.Origin_To_Down_Height;
	private final float origionWeekMoveWidth = Parameter.Origin_To_Origin_Width + Parameter.Origin_To_Sun_Width;
	private final float origionWeekMoveHeight = Parameter.Origin_To_Origin_Height + Parameter.Origin_To_Sun_Height;
	private final float origionDayMoveWidth = Parameter.Origin_To_Origin_Width + Parameter.Origin_To_Sun_Width;
	private final float origionDayMoveHeight = Parameter.Origin_To_Origin_Height + Parameter.Origin_To_Sun_Height - Parameter.Calender_Day_Glass_Height / 2 - Parameter.Calender_Week_Glass_Height / 2;
	private final float origionBackMoveWidth = Parameter.Origin_To_Origin_Width + Parameter.Origin_To_Back_Width;
	private final float origionBackMoveHeight = Parameter.Origin_To_Origin_Height + Parameter.Origin_To_Back_Height;
	private final float origionMonthMoveWidth = Parameter.Origin_To_Origin_Width + Parameter.Origin_To_Month_Width;
	private final float origionMonthMoveHeight = Parameter.Origin_To_Origin_Height + Parameter.Origin_To_Month_Height;
	private final float origionYear01MoveWidth = Parameter.Origin_To_Origin_Width + Parameter.Origin_To_Year01_Width;
	private final float origionYear01MoveHeight = Parameter.Origin_To_Origin_Height + Parameter.Origin_To_Year01_Height;
	private final float origionYear02MoveWidth = Parameter.Origin_To_Origin_Width + Parameter.Origin_To_Year02_Width;
	private final float origionYear02MoveHeight = Parameter.Origin_To_Origin_Height + Parameter.Origin_To_Year02_Height;
	private final float origionYear03MoveWidth = Parameter.Origin_To_Origin_Width + Parameter.Origin_To_Year03_Width;
	private final float origionYear03MoveHeight = Parameter.Origin_To_Origin_Height + Parameter.Origin_To_Year03_Height;
	private final float origionYear04MoveWidth = Parameter.Origin_To_Origin_Width + Parameter.Origin_To_Year04_Width;
	private final float origionYear04MoveHeight = Parameter.Origin_To_Origin_Height + Parameter.Origin_To_Year04_Height;
	private PluginViewObject3D calendertop = null;
	private PluginViewObject3D calendertop01 = null;
	private PluginViewObject3D calenderdown = null;
	private PluginViewObject3D calenderback = null;
	private PluginViewObject3D calendermonth = null;
	private PluginViewObject3D calenderyear01 = null;
	private PluginViewObject3D calenderyear02 = null;
	private PluginViewObject3D calenderyear03 = null;
	private PluginViewObject3D calenderyear04 = null;
	public BackgroundGroup calenderalltop = null;
	public BackgroundGroup calenderalldown = null;
	private Timeline timelineclickin = null;
	private Timeline timelineclickout = null;
	//翻转动画
	private Timeline animation_rotation = null;
	private Timeline timelineback = null;
	private Timeline timelinebackout = null;
	private Timeline movetimeline = null;
	private String[] days = null;
	private int nowMonth;
	private int nowYear;
	private DateReceiver datereceiver;
	private List<WeekGlasses> daypitchlistqian = new ArrayList<WeekGlasses>();
	private List<WeekGlasses> daypitchlisthou = new ArrayList<WeekGlasses>();
	private List<DayGroup3D> daygroup = new ArrayList<DayGroup3D>();
	private DayGroup3D calenderTopMove;
	private DayGroup3D calenderDate;
	public static boolean ifstartRotateTween = false;
	public static boolean ifstartTween = false;
	public static boolean ifstartclickbacktimeline = false;
	private float nowWidth;
	private float nowHeight;
	private float leftLowerCornerWidth;
	private float leftLowerCornerHeight;
	private static final int REQUEST_UPGRADE_VERIFICATION = 1;
	
	//	public static boolean ifisfree=true;
	public static final WidgetCalender getIntance()
	{
		return widgetcalender;
	}
	
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
		MODEL_WIDTH = Utils3D.getScreenWidth();
		MODEL_HEIGHT = R3D.Workspace_cell_each_height * 4;
		cooGdx = new CooGdx( context.mGdxApplication );
		this.width = MODEL_WIDTH;
		this.height = MODEL_HEIGHT;
		this.transform = true;
		scale = Utils3D.getScreenWidth() / 720f;
		height_scale = ( R3D.Workspace_cell_each_height * 4 - Parameter.GlassesHeight * scale ) / ( Parameter.Calender_Day_Glass_Height * 6 );
		Log.d( "Calender" , R3D.Workspace_cell_each_height * 4 + ": scale:" + scale + ":" + height_scale + "**" + scale );
		offset = ( scale - height_scale ) / 2 * Parameter.Calender_Day_Glass_Height;
		nowMonth = CalenderHelper.getCurrentMonth();
		nowYear = CalenderHelper.getCurrentYear();
		nowWidth = Parameter.Calender_Day_Glass_Width * scale;
		nowHeight = Parameter.Calender_Day_Glass_Height * height_scale;
		leftLowerCornerWidth = origionDayMoveWidth * scale - nowWidth / 2;
		leftLowerCornerHeight = ( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * 5 ) * scale - offset - nowHeight / 2;
		init();
		updateRegion( nowMonth , nowYear );
		setToday();
		updateEvents( nowYear , nowMonth );
		datereceiver = new DateReceiver();
		Log.v( "Calender" , "register brocast" );
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction( Intent.ACTION_TIME_CHANGED );
		intentfilter.addAction( Intent.ACTION_DATE_CHANGED );
		intentfilter.addAction( Intent.ACTION_TIMEZONE_CHANGED );
		intentfilter.addAction( Intent.ACTION_TIME_TICK );
		mContext.registerReceiver( datereceiver , intentfilter );
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
			return false;
		}
	}
	
	private void init()
	{
		addWeek();
		addDay();
		addBackGround();
	}
	
	@Override
	public void onDelete()
	{
		if( datereceiver != null )
		{
			mContext.unregisterReceiver( datereceiver );
		}
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
							if( calenderback.visible )
							{
							}
							else
							{
								nowYear = CalenderHelper.getCurrentYear();
								nowMonth = CalenderHelper.getCurrentMonth();
								updateRegion( nowMonth , nowYear );
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
						if( calenderback.visible )
						{
						}
						else
						{
							nowYear = CalenderHelper.getCurrentYear();
							nowMonth = CalenderHelper.getCurrentMonth();
							updateRegion( nowMonth , nowYear );
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
		Log.d( "Calender" , "onResume()" );
		if( addenentgroup != null && addenentgroup.visible)
		{
			mAppContext.mGdxApplication.postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					Log.d( "Calender" , "OnResume" );
					if( recordEvents.size() != 0 )
					{
						for( int i = 0 ; i < recordEvents.size() ; i++ )
						{
							widgetcalender.getIntance().removeView( recordEvents.get( i ) );
						}
						recordEvents.clear();
					}
					for( int j = 0 ; j < 7 ; j++ )
					{
						if( !days[hang * 7 + j].contains( "*" ) )
						{
							boolean ifhasEvents = CalenderHelper.judgeDayIfEvents( mAppContext.mContainerContext.getContentResolver() , nowYear , nowMonth , Integer.parseInt( days[hang * 7 + j] ) );
							Log.d( "Calender" , "**" + days[hang * 7 + j] + ":" + ifhasEvents );
							if( ifhasEvents )
							{
								DayGlasses dayhasevents = new DayGlasses( mAppContext , "dayhasevents" , getRegion( "xing.png" ) , "mod_shadow.obj" );
								dayhasevents.build();
								dayhasevents.move( ( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * j ) * scale , origionDayMoveHeight * scale - offset * 11f , 0 );
								widgetcalender.getIntance().addView( dayhasevents );
								recordEvents.add( dayhasevents );
							}
						}
					}
					addenentgroup.updateNowEvents( nowYear , nowMonth , Integer.parseInt( days[hang * 7 + lie] ) );
				}
			} );
		}
		
		if(judgetifShowEvents()){
			mAppContext.mGdxApplication.postRunnable(new Runnable() {
				
				@Override
				public void run()
				{
					Log.d( "Calender" , "onResume()111111111" );
					updateEvents( nowYear , nowMonth );
				}
			});
			
		}
		super.onResume();
	}
	
	private void addBackGround()
	{
		calenderalltop = new BackgroundGroup( "calenderalltop" );
		calenderalldown = new BackgroundGroup( "calenderalldown" );
		calenderTopMove = new DayGroup3D( "calenderTopMove" );
		calenderDate = new DayGroup3D( "calenderDate" );
		calenderback = new PluginViewObject3D( maincontext , "calenderback" , getRegion( "back.png" ) , "mod_back.obj" );
		calenderback.build();
		calenderback.move( origionBackMoveWidth * scale , origionBackMoveHeight * scale - offset * 12f , 0 );
		calenderback.hide();
		calenderalltop.addView( calenderback );
		calendertop = new PluginViewObject3D( maincontext , "calendertop" , getRegion( "tex_calendar.png" ) , "mod_calendarUp.obj" );
		calendertop.build();
		calendertop.move( origionTopMoveWidth * scale , origionTopMoveHeight * scale - offset * 12f , 0 );
		calenderalltop.addView( calendertop );
		calendertop01 = new PluginViewObject3D( maincontext , "calendertop01" , getRegion( "tex_calendar.png" ) , "mod_calendarUp01.obj" );
		calendertop01.build();
		calendertop01.move( ( origionTop01MoveWidth - topoffSet ) * scale , origionTop01MoveHeight * scale - offset * 12f , 0 );
		calenderTopMove.addView( calendertop01 );
		calenderdown = new PluginViewObject3D( maincontext , "calenderdown" , getRegion( "tex_calendar.png" ) , "mod_calendarDown.obj" );
		calenderdown.build();
		calenderdown.move( origionDownMoveWidth * scale , origionDownMoveHeight * scale , 0 );
		calenderalldown.addView( calenderdown );
		this.addView( calenderalldown );
		calendermonth = new PluginViewObject3D( maincontext , "calendermonth" , getRegion( "month_1.png" ) , "mod_month(year).obj" );
		calendermonth.build();
		calendermonth.move( origionMonthMoveWidth * scale - topoffSet * scale , origionMonthMoveHeight * scale - offset * 12f , 0 );
		calenderDate.addView( calendermonth );
		calenderyear01 = new PluginViewObject3D( maincontext , "calenderyear01" , getRegion( "year_2.png" ) , "mod_01(year).obj" );
		calenderyear01.build();
		calenderyear01.move( origionYear01MoveWidth * scale - topoffSet * scale , origionYear01MoveHeight * scale - offset * 12f , 0 );
		calenderDate.addView( calenderyear01 );
		calenderyear02 = new PluginViewObject3D( maincontext , "calenderyear02" , getRegion( "year_0.png" ) , "mod_01(year).obj" );
		calenderyear02.build();
		calenderyear02.move( origionYear02MoveWidth * scale - topoffSet * scale , origionYear02MoveHeight * scale - offset * 12f , 0 );
		calenderDate.addView( calenderyear02 );
		calenderyear03 = new PluginViewObject3D( maincontext , "calenderyear02" , getRegion( "year_1.png" ) , "mod_01(year).obj" );
		calenderyear03.build();
		calenderyear03.move( origionYear03MoveWidth * scale - topoffSet * scale , origionYear03MoveHeight * scale - offset * 12f , 0 );
		calenderDate.addView( calenderyear03 );
		calenderyear04 = new PluginViewObject3D( maincontext , "calenderyear02" , getRegion( "year_4.png" ) , "mod_01(year).obj" );
		calenderyear04.build();
		calenderyear04.move( origionYear04MoveWidth * scale - topoffSet * scale , origionYear04MoveHeight * scale - offset * 12f , 0 );
		calenderDate.addView( calenderyear04 );
		calenderTopMove.addView( calenderDate );
		calenderalltop.addView( calenderTopMove );
		this.addView( calenderalltop );
	}
	
	private void addWeek()
	{
		for( int i = 0 ; i < 7 ; i++ )
		{
			WeekGlasses calenderweek = null;
			if( i == 0 )
			{
				calenderweek = new WeekGlasses( maincontext , "calenderweek" , getRegion( "tex_calendar.png" ) , "mod_week_glass01.obj" );
			}
			else if( i == 6 )
			{
				calenderweek = new WeekGlasses( maincontext , "calenderweek" , getRegion( "tex_calendar.png" ) , "mod_week_glass03.obj" );
			}
			else
			{
				calenderweek = new WeekGlasses( maincontext , "calenderweek" , getRegion( "tex_calendar.png" ) , "mod_week_glass02.obj" );
			}
			calenderweek.build();
			calenderweek.move( ( origionWeekMoveWidth + Parameter.Calender_Week_Glass_Width * i ) * scale , origionWeekMoveHeight * scale - offset * 12f , 0 );
			this.addView( calenderweek );
			WeekGlasses calenderpitchweek = new WeekGlasses( maincontext , "calenderpitchweek" , getRegion( Parameter.CalenderPitchWeek[i] ) , "mod_Week.obj" );
			calenderpitchweek.build();
			calenderpitchweek.move( ( origionWeekMoveWidth + Parameter.Calender_Week_Patch_Width * i ) * scale , origionWeekMoveHeight * scale - offset * 12f , 0 );
			this.addView( calenderpitchweek );
		}
	}
	
	private void addDay()
	{
		Log.d( "Calender" , "addDay" );
		for( int j = 0 ; j < 5 ; j++ )
		{
			for( int i = 0 ; i < 7 ; i++ )
			{
				DayGroup3D viewgroup = new DayGroup3D( "daygroup" );
				DayGlasses calenderday = null;
				if( i == 0 )
				{
					calenderday = new DayGlasses( maincontext , "calenderday" , getRegion( "tex_calendar.png" ) , "mod_number(day)_glass01.obj" );
				}
				else if( i == 6 )
				{
					calenderday = new DayGlasses( maincontext , "calenderday" , getRegion( "tex_calendar.png" ) , "mod_number(day)_glass03.obj" );
				}
				else
				{
					calenderday = new DayGlasses( maincontext , "calenderday" , getRegion( "tex_calendar.png" ) , "mod_number(day)_glass02.obj" );
				}
				calenderday.build();
				calenderday.move(
						( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * i ) * scale ,
						( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * j ) * scale - offset * ( 11f - 2 * j ) ,
						0 );
				// this.addView(calenderday);
				viewgroup.addView( calenderday );
				WeekGlasses calenderpitchday = new WeekGlasses( mAppContext , "calenderpitchday" , getRegion( "dayliang_1.png" ) , "mod_number(day).obj" );
				calenderpitchday.build();
				calenderpitchday.move(
						( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * i ) * scale ,
						( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * j ) * scale - offset * ( 11f - 2 * j ) ,
						0 );
				// this.addView(calenderpitchday);
				viewgroup.addView( calenderpitchday );
				daypitchlistqian.add( calenderpitchday );
				this.addView( viewgroup );
				daygroup.add( viewgroup );
			}
		}
		for( int i = 0 ; i < 7 ; i++ )
		{
			DayGroup3D viewgroup = new DayGroup3D( "daygroup" );
			DayGlasses calenderday = null;
			if( i == 0 )
			{
				calenderday = new DayGlasses( maincontext , "calenderday" , getRegion( "tex_calendar.png" ) , "mod_number(down)_glass01.obj" );
			}
			else if( i == 6 )
			{
				calenderday = new DayGlasses( maincontext , "calenderday" , getRegion( "tex_calendar.png" ) , "mod_number(down)_glass03.obj" );
			}
			else
			{
				calenderday = new DayGlasses( maincontext , "calenderday" , getRegion( "tex_calendar.png" ) , "mod_number(down)_glass02.obj" );
			}
			calenderday.build();
			calenderday.move( ( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * i ) * scale , ( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * 5 ) * scale - offset , 0 );
			// this.addView(calenderday);
			viewgroup.addView( calenderday );
			WeekGlasses calenderpitchday = new WeekGlasses( mAppContext , "calenderpitchday" , getRegion( "dayliang_1.png" ) , "mod_number(day).obj" );
			calenderpitchday.build();
			calenderpitchday.move( ( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * i ) * scale , ( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * 5 ) * scale - offset , 0 );
			// this.addView(calenderpitchday);
			viewgroup.addView( calenderpitchday );
			daypitchlistqian.add( calenderpitchday );
			this.addView( viewgroup );
			daygroup.add( viewgroup );
		}
	}
	
	DayGlasses todayglasses = null;
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
					todayglasses = new DayGlasses( mAppContext , "todayglasses" , getRegion( "today.png" ) , "mod_shadow.obj" );
					todayglasses.build();
					todayglasses.move(
							( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * ( i % 7 ) ) * scale ,
							( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * ( i / 7 ) ) * scale - offset * ( 11f - 2 * ( i / 7 ) ) ,
							0 );
					daygroup.get( i ).addView( todayglasses );
					markday = i;
				}
			}
		}
	}
	
	public void updateRegion(
			int currentMonth ,
			int currentYear )
	{
		//		Log.d( "Calender" , "month_" + ( currentMonth + 1 ) + ".png" + "" );
		if( calendermonth.region != null )
		{
			calendermonth.region.getTexture().dispose();
		}
		calendermonth.region.setRegion( getRegion( "month_" + ( currentMonth + 1 ) + ".png" ) );
		char[] year = getNum( currentYear );
		if( year.length == 4 )
		{
			if( calenderyear01.region != null )
			{
				calenderyear01.region.getTexture().dispose();
			}
			calenderyear01.region.setRegion( getRegion( "year_" + year[0] + ".png" ) );
			if( calenderyear02.region != null )
			{
				calenderyear02.region.getTexture().dispose();
			}
			calenderyear02.region.setRegion( getRegion( "year_" + year[1] + ".png" ) );
			if( calenderyear03.region != null )
			{
				calenderyear03.region.getTexture().dispose();
			}
			calenderyear03.region.setRegion( getRegion( "year_" + year[2] + ".png" ) );
			if( calenderyear04.region != null )
			{
				calenderyear04.region.getTexture().dispose();
			}
			calenderyear04.region.setRegion( getRegion( "year_" + year[3] + ".png" ) );
		}
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
						if( daypitchlistqian.get( i ).region != null )
						{
							daypitchlistqian.get( i ).region.getTexture().dispose();
						}
						daypitchlistqian.get( i ).region.setRegion( getRegion( "dayan_" + replace + ".png" ) );
					}
					else
					{
						if( daypitchlistqian.get( i ).region != null )
						{
							daypitchlistqian.get( i ).region.getTexture().dispose();
						}
						daypitchlistqian.get( i ).region.setRegion( getRegion( "dayliang_" + days[i] + ".png" ) );
					}
				}
				else
				{
					if( scrollcount_all % 2 == 0 )
					{
						if( days[i].contains( "*" ) )
						{
							String replace = days[i].replace( "*" , "" );
							if( daypitchlistqian.get( i ).region != null )
							{
								daypitchlistqian.get( i ).region.getTexture().dispose();
							}
							daypitchlistqian.get( i ).region.setRegion( getRegion( "dayan_" + replace + ".png" ) );
						}
						else
						{
							if( daypitchlistqian.get( i ).region != null )
							{
								daypitchlistqian.get( i ).region.getTexture().dispose();
							}
							daypitchlistqian.get( i ).region.setRegion( getRegion( "dayliang_" + days[i] + ".png" ) );
						}
					}
					else
					{
						if( days[i].contains( "*" ) )
						{
							String replace = days[i].replace( "*" , "" );
							if( daypitchlisthou.get( i ).region != null )
							{
								daypitchlisthou.get( i ).region.getTexture().dispose();
							}
							daypitchlisthou.get( i ).region.setRegion( getRegion( "dayan_" + replace + ".png" ) );
						}
						else
						{
							if( daypitchlisthou.get( i ).region != null )
							{
								daypitchlisthou.get( i ).region.getTexture().dispose();
							}
							daypitchlisthou.get( i ).region.setRegion( getRegion( "dayliang_" + days[i] + ".png" ) );
						}
					}
				}
			}
		}
	}
	
	List<DayGroup3D> listxingqian = new ArrayList<DayGroup3D>();
	List<Integer> countxingqian = new ArrayList<Integer>();
	List<DayGroup3D> listxinghou = new ArrayList<DayGroup3D>();
	List<Integer> countxinghou = new ArrayList<Integer>();
	List<String> list = null;
	
	public void updateEvents(
			int currentYear ,
			int currentMonth )
	{
		Log.d( "Calender" , "updateEvents" );
		list = CalenderHelper.judgeMonthIfEvents( maincontext.mContainerContext.getContentResolver() , currentYear , currentMonth );
		Log.d( "Calender" , "list.size()***" + list.size() );
		//		listxingqian = new ArrayList<DayGroup3D>();
		//		countxingqian = new ArrayList<Integer>();
		//		listxinghou = new ArrayList<DayGroup3D>();
		//		countxinghou = new ArrayList<Integer>();
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
							DayGroup3D dgxinggroup = new DayGroup3D( "dayxinggroup" );
							DayGlasses dayxing = new DayGlasses( mAppContext , "dayxing" , getRegion( "xing.png" ) , "mod_shadow.obj" );
							dayxing.build();
							dayxing.move(
									( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * ( i % 7 ) ) * scale ,
									( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * ( i / 7 ) ) * scale - offset * ( 11f - 2 * ( i / 7 ) ) ,
									0 );
							dgxinggroup.addView( dayxing );
							dgxinggroup.setOrigin(
									( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * ( i % 7 ) ) * scale ,
									( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * ( i / 7 ) ) * scale - offset * ( 11f - 2 * ( i / 7 ) ) );
							dgxinggroup.setOriginZ( ( -0.0764f ) * scale );
							dgxinggroup.setRotationVector( 1 , 0 , 0 );
							dgxinggroup.setRotationAngle( 180 , 0 , 0 );
							daygroup.get( i ).addView( dgxinggroup );
							listxinghou.add( dgxinggroup );
							countxinghou.add( i );
						}
						else
						{
							DayGroup3D dgxinggroup = new DayGroup3D( "dayxinggroup" );
							DayGlasses dayxing = new DayGlasses( mAppContext , "dayxing" , getRegion( "xing.png" ) , "mod_shadow.obj" );
							dayxing.build();
							dayxing.move(
									( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * ( i % 7 ) ) * scale ,
									( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * ( i / 7 ) ) * scale - offset * ( 11f - 2 * ( i / 7 ) ) ,
									0 );
							dgxinggroup.addView( dayxing );
							daygroup.get( i ).addView( dgxinggroup );
							listxingqian.add( dgxinggroup );
							countxingqian.add( i );
						}
					}
				}
			}
		}
	}
	
	public char[] getNum(
			int number )
	{
		String str = number + "";
		return str.toCharArray();
	}
	
	private TextureRegion getRegion(
			String name )
	{
		try
		{
			BitmapTexture bt = new BitmapTexture( BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( "theme/widget/cometcalendar/comet/image/" + name ) ) , true );
			bt.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			TextureRegion mBackRegion = new TextureRegion( bt );
			return mBackRegion;
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		long prevTime = System.currentTimeMillis();
		cooGdx.gl.glEnable( GL10.GL_CULL_FACE );
		cooGdx.gl.glCullFace( GL10.GL_BACK );
		super.draw( batch , parentAlpha );
		cooGdx.gl.glDisable( GL10.GL_CULL_FACE );
		// batch.end();
		//		Log.d("Calender",(long)(System.currentTimeMillis()-prevTime)+"WidgetCalender");
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
			this.removeView( clickXiaoGuo );
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
							clicktoday = new DayGlasses( mAppContext , "clicktoday" , getRegion( "today.png" ) , "mod_shadow.obj" );
							clicktoday.build();
							clicktoday.move( ( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * i ) * scale , origionDayMoveHeight * scale - offset * 11f , 0 );
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
			clicktodayXiaoGuo = new DayGlasses( mAppContext , "clicktodayXiaoGuo" , getRegion( "xuanzhong.png" ) , "mod_shadow.obj" );
			clicktodayXiaoGuo.build();
			clicktodayXiaoGuo.move( ( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * lie ) * scale , origionDayMoveHeight * scale - offset * 11f , 0 );
			this.addView( clicktodayXiaoGuo );
			addenentgroup = new AddEventGroup( "addenentgroup" , maincontext , nowYear , nowMonth , Integer.parseInt( days[hang * 7 + lie] ) , maincontext.mContainerContext );
			addenentgroup.setSize( Parameter.BigBackgroundWidth * scale , Parameter.BigBackgroundHeight * height_scale );
			this.addView( addenentgroup );
			for( int j = 0 ; j < 7 ; j++ )
			{
				if( !days[hang * 7 + j].contains( "*" ) )
				{
					boolean ifhasEvents = CalenderHelper.judgeDayIfEvents( mAppContext.mContainerContext.getContentResolver() , nowYear , nowMonth , Integer.parseInt( days[hang * 7 + j] ) );
					Log.d( "Calender" , "**" + days[hang * 7 + j] + ":" + ifhasEvents );
					if( ifhasEvents )
					{
						DayGlasses dayhasevents = new DayGlasses( mAppContext , "dayhasevents" , getRegion( "xing.png" ) , "mod_shadow.obj" );
						dayhasevents.build();
						dayhasevents.move( ( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * j ) * scale , origionDayMoveHeight * scale - offset * 11f , 0 );
						this.addView( dayhasevents );
						recordEvents.add( dayhasevents );
					}
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
			calenderback.show();
			moveTimeline( topoffSet * scale );
		}
		if( type == TweenCallback.COMPLETE && source.equals( timelineclickout ) )
		{
			ifstartTween = false;
		}
		if( type == TweenCallback.COMPLETE && source.equals( timelineback ) )
		{
			if( addenentgroup != null )
			{
				addenentgroup.hide();
				this.removeView( addenentgroup );
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
			scrollcount_all = 0;
			scroll_count = 0;
			updateRegion( nowMonth , nowYear );
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
			if( timelinebackout != null )
			{
				timelinebackout.free();
				timelinebackout = null;
			}
			timelinebackout = Timeline.createParallel();
			timelinebackout.push( Tween.to( calenderalltop , View3DTweenAccessor.POS_XY , 0.75f ).target( 0 , 0 , 0 ).ease( Quad.INOUT ) );
			timelinebackout.push( Tween.to( calenderalldown , View3DTweenAccessor.POS_XY , 0.75f ).target( 0 , 0 , 0 ).ease( Quad.INOUT ) );
			timelinebackout.start( View3DTweenAccessor.manager ).setCallback( this );
			moveTimeline( -offset * scale );
			calenderback.hide();
		}
		if( type == TweenCallback.COMPLETE && source.equals( timelinebackout ) )
		{
			ifstartclickbacktimeline = false;
		}
		if( type == TweenCallback.COMPLETE && source.equals( animation_rotation ) )
		{
			ifstartRotateTween = false;
			if( !ifShowNowday )
			{
				todayglasses.hide();
			}
			if( !ifShowBack )
			{
				calenderback.hide();
			}
			Log.d( "Calender" , " listxingqian.size():" + listxingqian.size() + "countxingqian.size():" + countxingqian.size() );
			Log.d( "Calender" , " listxinghou.size():" + listxinghou.size() + "countxinghou.size():" + countxinghou.size() );
			if( scrollcount_all % 2 == 0 )
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
		Log.d( "Calender" , "onclick" );
		float movediatance = ( nowHeight * 6 + Parameter.Calender_Week_Glass_Height * scale ) / 2;
		float backHeight = origionWeekMoveHeight * scale - offset * 12f + Parameter.Calender_Week_Glass_Height / 2;
		if( ifstartTween || ifstartRotateTween || ifstartclickbacktimeline )
		{
			Log.d( "Calender" , "ifstartTween:" + ifstartTween + "ifstartRotateTween:" + ifstartRotateTween + "ifstartclickbacktimeline:" + ifstartclickbacktimeline );
			return true;
		}
		if( calenderback.visible )
		{
			if( x > leftLowerCornerWidth && x < leftLowerCornerWidth + topoffSet * scale + 20 * scale && y > backHeight && y < backHeight + Parameter.Calender_Top_JinShuTiao + 20 * scale )
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
			//			return true;
		}
		Log.d( "Calender" , "!(addenentgroup!=null&&addenentgroup.visible):" + !( addenentgroup != null && addenentgroup.visible ) );
		if( !( addenentgroup != null && addenentgroup.visible ) )
		{
			if( x > leftLowerCornerWidth && x < leftLowerCornerWidth + nowWidth * 7 && y < leftLowerCornerHeight + nowHeight * 6 && y > leftLowerCornerHeight )
			{
				hang = 5 - (int)( ( y - leftLowerCornerHeight ) / nowHeight );
				lie = (int)( ( x - leftLowerCornerWidth ) / nowWidth );
				// Log.d("Calender", "hang:" + hang + "**lie:" + lie);
				// Log.d("Calender", "对应点击的数字为："+days[hang*7+lie]);
				if( !days[hang * 7 + lie].contains( "*" ) )
				{
					//					Log.d( "Calender" , "对应点击的数字为：" + days[hang * 7 + lie] );
					//					Log.d( "Calender" , "开始做动画" );
					ifstartTween = true;
					if( timelineclickin != null )
					{
						timelineclickin.free();
						timelineclickin = null;
					}
					clickXiaoGuo = new DayGlasses( mAppContext , "clickday" , getRegion( "click.png" ) , "mod_shadow.obj" );
					clickXiaoGuo.build();
					clickXiaoGuo.move(
							( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * lie ) * scale ,
							( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * hang ) * scale - offset * ( 11f - 2 * hang ) ,
							0 );
					this.addView( clickXiaoGuo );
					timelineclickin = Timeline.createParallel();
					timelineclickin.push( Tween.to( calenderalltop , View3DTweenAccessor.POS_XY , 0.75f ).target( 0 , -movediatance , 0 ).ease( Quad.INOUT ) );
					timelineclickin.push( Tween.to( calenderalldown , View3DTweenAccessor.POS_XY , 0.75f ).target( 0 , movediatance , 0 ).ease( Quad.INOUT ) );
					timelineclickin.start( View3DTweenAccessor.manager ).setCallback( this );
					// timelineclickin.push(temTine);
				}
			}
			return true;
		}
		if( addenentgroup != null && addenentgroup.visible )
		{
			//			Log.d( "Calender" , "addenentgroup.visible" );
			if( x > leftLowerCornerWidth && x < leftLowerCornerWidth + nowWidth * 7 && y < leftLowerCornerHeight + nowHeight * 6 && y > leftLowerCornerHeight + nowHeight * 5 )
			{
				if( !days[hang * 7 + lie].contains( "*" ) )
				{
					prelie = lie;
				}
				lie = (int)( ( x - leftLowerCornerWidth ) / nowWidth );
				//				Log.d("Calender", "对应点击的数字为："+days[hang*7+lie]);
				if( !days[hang * 7 + lie].contains( "*" ) )
				{
					clicktodayXiaoGuo.move( ( lie - prelie ) * Parameter.Calender_Day_Glass_Width * scale , 0 , 0 );
					mAppContext.mGdxApplication.postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							addenentgroup.updateNowEvents( nowYear , nowMonth , Integer.parseInt( days[hang * 7 + lie] ) );
							addenentgroup.year = nowYear;
							addenentgroup.month = nowMonth;
							addenentgroup.day = Integer.parseInt( days[hang * 7 + lie] );
						}
					} );
				}
				return true;
			}
			return super.onClick( x , y );
		}
		//		return true;
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
		// TODO Auto-generated method stub
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
		//		Log.d( "Calender" , "scroll......" );
		if( ifstartTween || ifstartRotateTween || ifstartclickbacktimeline )
		{
			return true;
		}
		if( addenentgroup != null )
		{
			if( addenentgroup.visible )
			{
				return super.scroll( x , y , deltaX , deltaY );
			}
		}
		float k = 0;
		if( deltaY != 0 && deltaY != 0 )
		{
			k = deltaY / deltaX;
		}
		if( !ifscroll )
		{
			if( ifworkspacemove )
			{
				return super.scroll( x , y , deltaX , deltaY );
			}
			if(k>=-1.7&&k<=1.7)
			{
				ifworkspacemove = true;
				ifscroll = false;
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
//		return super.scroll( x , y , deltaX , deltaY );
	}
	
	private void addNewPitch()
	{
		Log.d( "Calender" , "addNewPitch" );
		for( int i = 0 ; i < 6 ; i++ )
		{
			for( int j = 0 ; j < 7 ; j++ )
			{
				DayGroup3D dg = new DayGroup3D( "dg" );
				WeekGlasses calenderpitchday = new WeekGlasses( mAppContext , "calenderpitchday" , getRegion( "dayliang_1.png" ) , "mod_number(day).obj" );
				calenderpitchday.build();
				calenderpitchday.move(
						( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * j ) * scale ,
						( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * i ) * scale - offset * ( 11f - 2 * i ) ,
						0 );
				dg.addView( calenderpitchday );
				daypitchlisthou.add( calenderpitchday );
				calenderpitchday.hide();
				dg.setOrigin(
						( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * j ) * scale ,
						( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * i ) * scale - offset * ( 11f - 2 * i ) );
				dg.setOriginZ( ( -0.0764f ) * scale );
				dg.setRotationVector( 1 , 0 , 0 );
				dg.setRotationAngle( 180 , 0 , 0 );
				daygroup.get( i * 7 + j ).addView( dg );
			}
		}
	}
	
	public void startTween(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( x > leftLowerCornerWidth && x < leftLowerCornerWidth + nowWidth * 7 && y < leftLowerCornerHeight + nowHeight * 6 && y > leftLowerCornerHeight )
		{
			if( daypitchlisthou.size() == 0 )
			{
				addNewPitch();
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
				updateRegion( nowMonth , nowYear );
				updateEvents( nowYear , nowMonth );
				ifstartRotateTween = true;
				for( int i = 0 ; i < 6 ; i++ )
				{
					for( int j = 0 ; j < 7 ; j++ )
					{
						daygroup.get( i * 7 + j ).setOrigin(
								( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * j ) * scale ,
								( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * i ) * scale - offset * ( 11f - 2 * i ) );
						daygroup.get( i * 7 + j ).setOriginZ( ( -0.0764f ) * scale );
						daygroup.get( i * 7 + j ).setRotationVector( 1 , 0 , 0 );
						animation_rotation.push( Tween.to( daygroup.get( i * 7 + j ) , View3DTweenAccessor.ROTATION , 0.3f ).target( 180 * scroll_count , 0 , 0 ).ease( Cubic.IN )
								.delay( 0.1f * ( i + j ) ) );
					}
				}
				if( nowMonth == CalenderHelper.getCurrentMonth() && nowYear == CalenderHelper.getCurrentYear() && calenderback.visible )
				{
					todayglasses.show();
					moveTimeline( 0 );
					//					calenderback.hide();
					ifShowNowday = true;
					ifShowBack = false;
				}
				else if( calenderback.visible )
				{
				}
				else
				{
					calenderback.show();
					moveTimeline( topoffSet * scale );
					ifShowNowday = false;
					ifShowBack = true;
					//					todayglasses.hide();
				}
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
				updateRegion( nowMonth , nowYear );
				updateEvents( nowYear , nowMonth );
				ifstartRotateTween = true;
				for( int i = 0 ; i < 6 ; i++ )
				{
					for( int j = 0 ; j < 7 ; j++ )
					{
						daygroup.get( i * 7 + j ).setOrigin(
								( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * j ) * scale ,
								( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * i ) * scale - offset * ( 11f - 2 * i ) );
						daygroup.get( i * 7 + j ).setOriginZ( ( -0.0764f ) * scale );
						daygroup.get( i * 7 + j ).setRotationVector( 1 , 0 , 0 );
						animation_rotation.push( Tween.to( daygroup.get( i * 7 + j ) , View3DTweenAccessor.ROTATION , 0.3f ).target( 180 * scroll_count , 0 , 0 ).ease( Cubic.IN )
								.delay( 0.1f * ( Math.abs( i - 5 ) + Math.abs( j - 6 ) ) ) );
					}
				}
				if( nowMonth == CalenderHelper.getCurrentMonth() && nowYear == CalenderHelper.getCurrentYear() && calenderback.visible )
				{
					todayglasses.show();
					moveTimeline( 0 );
					ifShowNowday = true;
					ifShowBack = false;
				}
				else if( calenderback.visible )
				{
				}
				else
				{
					calenderback.show();
					moveTimeline( topoffSet * scale );
					ifShowNowday = false;
					ifShowBack = true;
				}
				animation_rotation.start( View3DTweenAccessor.manager ).setCallback( this );
			}
		}
	}
	
	private void moveTimeline(
			float moveDistance )
	{
		if( movetimeline != null )
		{
			movetimeline.free();
			movetimeline = null;
		}
		movetimeline = Timeline.createParallel();
		movetimeline.push( Tween.to( calenderTopMove , View3DTweenAccessor.POS_XY , 0.5f ).target( moveDistance , 0 , 0 ).ease( Quad.INOUT ) );
		movetimeline.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	//		DayGlasses clickday = null;
	//		@Override
	//		public boolean onTouchUp(
	//				float x ,
	//				float y ,
	//				int pointer )
	//		{
	//			
	//			if( ifstartTween || ifstartRotateTween || ifstartclickbacktimeline )
	//			{
	//				return true;
	//			}
	//			if( x > leftLowerCornerWidth && x < leftLowerCornerWidth + nowWidth * 7 && y < leftLowerCornerHeight + nowHeight * 6 && y > leftLowerCornerHeight )
	//			{
	//				for( int i = 0 ; i < daygroup.size() ; i++ )
	//				{
	//					List<View3D> childlist = daygroup.get( i ).getActors();
	//					for( int j = 0 ; j < childlist.size() ; j++ )
	//					{
	//						if(childlist.get( i ).equals( clickday )){
	//							daygroup.get( i ).removeView( childlist.get( j ) );
	//						}
	//					}
	//				}
	//			}
	//			return super.onTouchUp( x , y , pointer );
	//		}
	//		@Override
	//		public boolean onTouchDown(
	//				float x ,
	//				float y ,
	//				int pointer )
	//		{
	//			float nowWidth = Parameter.Calender_Day_Glass_Width * scale;
	//			float nowHeight = Parameter.Calender_Day_Glass_Height * height_scale;
	//			float leftLowerCornerWidth = origionDayMoveWidth * scale - nowWidth / 2;
	//			float leftLowerCornerHeight = ( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * 5 ) * scale - offset - nowHeight / 2;
	//			if( ifstartTween || ifstartRotateTween || ifstartclickbacktimeline )
	//			{
	//				return true;
	//			}
	//			if( x > leftLowerCornerWidth && x < leftLowerCornerWidth + nowWidth * 7 && y < leftLowerCornerHeight + nowHeight * 6 && y > leftLowerCornerHeight )
	//			{
	//				int hang = 5 - (int)( ( y - leftLowerCornerHeight ) / nowHeight );
	//				int lie = (int)( ( x - leftLowerCornerWidth ) / nowWidth );
	//				clickday = new DayGlasses( mAppContext , "clickday" , getRegion( "click.png" ) , "mod_shadow.obj" );
	//				clickday.build();
	//				clickday.move(
	//						( origionDayMoveWidth + Parameter.Calender_Day_Glass_Width * lie ) * scale ,
	//						( origionDayMoveHeight - Parameter.Calender_Day_Glass_Height * hang ) * scale - offset * ( 11f - 2 * hang ) ,
	//						0 );
	//				daygroup.get( hang * 7 + lie ).addView( clickday );
	//			}
	//			return super.onTouchDown( x , y , pointer );
	//		}
}
