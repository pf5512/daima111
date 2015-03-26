package com.iLoong.ThemeClock.View;


import java.util.Calendar;

import android.content.Context;
import android.graphics.Paint;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.iLoong.ThemeClock.Common.TextureUtil;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class CommonDigitalClock extends ViewGroup3D
{
	
	public final static String TAG = "CommonDigitalClock";
	public ClockMemberView watchBackView = null;
	//2D模型widget view
	public Texture watchBackTexture;
	public Texture hourGroupTextureLeft;
	public Texture hourGroupTextureRight;
	public Texture dotGroupTexture;
	public Texture minuteGroupTextureLeft;
	public Texture minuteGroupTextureRight;
	public Texture monthTexture;
	public Texture apmTexture;
	public ClockMember2DView watchBack2DView = null;
	public ClockMember2DView hourGroup2DViewLeft = null;
	public ClockMember2DView hourGroup2DViewRight = null;
	public ClockMember2DView dotGroup2DView = null;
	public ClockMember2DView minuteGroup2DViewLeft = null;
	public ClockMember2DView minuteGroup2DViewRight = null;
	public ClockMember2DView monthGroup2DView = null;
	public ClockMember2DView apmGroup2DView = null;
	public Context mContext;
	public static int TIME_HOUR = 0;
	public static int TIME_MINUTE = 0;
	public static boolean IS_USE_SYSTEM_SOURCE = true;
	public static boolean isAM = false;
	
	public CommonDigitalClock(
			String name ,
			MainAppContext context )
	{
		super( name );
		// TODO Auto-generated constructor stub
		mContext = context.mWidgetContext;
		this.setOrigin( this.width / 2 , this.height / 2 );
		Load2DTexture();
		initView();
	}
	
	public void ReloadClockView()
	{
		resetAllView();
		Load2DTexture();
		initView();
	}
	
	public void initView()
	{
		addWatchView();
		addHourViewLeft();
		addHourViewRight();
		addDotView();
		addMinuteViewLeft();
		addMinuteViewRight();
		addMonthView();
		addAPMView();
	}
	
	public void Load2DTexture()
	{
		/*获取系统的时间,分为12小时制和24小时制*/
		Calendar mCalendar = Calendar.getInstance();
		int mHeadHour = mCalendar.get( Calendar.HOUR_OF_DAY );
		int mHeadMinute = mCalendar.get( Calendar.MINUTE );
		int mYear = mCalendar.get( Calendar.YEAR );
		int mMonth = mCalendar.get( Calendar.MONTH ) + 1;
		int mDay = mCalendar.get( Calendar.DAY_OF_MONTH );
		boolean is_24hour = android.text.format.DateFormat.is24HourFormat( mContext );
		if( !is_24hour )
		{
			if( mHeadHour > 11 )
			{
				if( mHeadHour != 12 )
				{
					mHeadHour -= 12;
				}
				isAM = false;
			}
			else
			{
				if( mHeadHour == 0 )
				{
					mHeadHour = 12;
				}
				isAM = true;
			}
			//LoadAPMTexture(); //暂时不需要显示AM PM
		}
		IS_USE_SYSTEM_SOURCE = WidgetThemeManager.getInstance().getBoolean( "use_system_source" );
		/*根据系统时间获取Widget的时间资源*/
		if( IS_USE_SYSTEM_SOURCE )
		{
			String watchBackFileName = "watch_back.png";
			String watchHourFileNameLeft = String.valueOf( mHeadHour / 10 );
			String watchHourFileNameRight = String.valueOf( mHeadHour % 10 );
			String watchMinuteFileNameLeft = String.valueOf( mHeadMinute / 10 );
			String watchMinuteFileNameRight = String.valueOf( mHeadMinute % 10 );
			String watchDotFileName = ":";
			String monthStr = String.format( "%s.%s.%s" , mYear , mMonth , mDay );
			Paint p = new Paint();
			p.setTextSize( WidgetThemeManager.getInstance().getFloat( "time_font_size" ) );
			/*加载对应的Texture*/
			watchBackTexture = WidgetThemeManager.getInstance().getThemeTexture( watchBackFileName );
			hourGroupTextureLeft = TextureUtil.createFontsTexture( watchHourFileNameLeft , p );
			hourGroupTextureRight = TextureUtil.createFontsTexture( watchHourFileNameRight , p );
			minuteGroupTextureLeft = TextureUtil.createFontsTexture( watchMinuteFileNameLeft , p );
			minuteGroupTextureRight = TextureUtil.createFontsTexture( watchMinuteFileNameRight , p );
			dotGroupTexture = TextureUtil.createFontsTexture( watchDotFileName , p );
			if( watchBackTexture != null )
			{
				watchBackTexture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			}
			if( hourGroupTextureLeft != null )
			{
				hourGroupTextureLeft.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			}
			if( hourGroupTextureRight != null )
			{
				hourGroupTextureRight.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			}
			if( minuteGroupTextureLeft != null )
			{
				minuteGroupTextureLeft.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			}
			if( minuteGroupTextureRight != null )
			{
				minuteGroupTextureRight.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			}
			if( dotGroupTexture != null )
			{
				dotGroupTexture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			}
			p.setTextSize( WidgetThemeManager.getInstance().getFloat( "ddmmyy_font_size" ) );
			monthTexture = TextureUtil.createFontsTexture( monthStr , p );
		}
		else
		{
			String watchBackFileName = "watch_back.png";
			String watchHourFileNameLeft = "clock_num_" + mHeadHour / 10 + ".png";
			String watchHourFileNameRight = "clock_num_" + mHeadHour % 10 + ".png";
			String watchMinuteFileNameLeft = "clock_num_" + mHeadMinute / 10 + ".png";
			String watchMinuteFileNameRight = "clock_num_" + mHeadMinute % 10 + ".png";
			String watchDotFileName = "clock_dot.png";
			int formatType = WidgetThemeManager.getInstance().getInteger( "ddmmyy_format_type" );
			String monthStr = GetDDMMYYstr( formatType , mYear , mMonth , mDay );
			/*加载对应的Texture*/
			watchBackTexture = WidgetThemeManager.getInstance().getThemeTexture( watchBackFileName );
			hourGroupTextureLeft = WidgetThemeManager.getInstance().getThemeTexture( watchHourFileNameLeft );
			hourGroupTextureRight = WidgetThemeManager.getInstance().getThemeTexture( watchHourFileNameRight );
			minuteGroupTextureLeft = WidgetThemeManager.getInstance().getThemeTexture( watchMinuteFileNameLeft );
			minuteGroupTextureRight = WidgetThemeManager.getInstance().getThemeTexture( watchMinuteFileNameRight );
			dotGroupTexture = WidgetThemeManager.getInstance().getThemeTexture( watchDotFileName );
			Paint p = new Paint();
			p.setTextSize( WidgetThemeManager.getInstance().getFloat( "ddmmyy_font_size" ) );
			monthTexture = TextureUtil.createFontsTexture( monthStr , p );
		}
	}
	
	public String GetDDMMYYstr(
			int type ,
			int mYear ,
			int mMonth ,
			int mDay )
	{
		String timeStr = "";
		switch( type )
		{
			case 0:
				timeStr = String.format( "%s-%s-%s" , mYear , mMonth , mDay );
				break;
			case 1:
				timeStr = String.format( "%s.%s.%s" , mYear , mMonth , mDay );
				break;
			default:
				timeStr = String.format( "%s.%s.%s" , mYear , mMonth , mDay );
				break;
		}
		return timeStr;
	}
	
	public void LoadAPMTexture()
	{
		String apm = "";
		Paint p = new Paint();
		if( isAM )
		{
			apm = "AM";
		}
		else
		{
			apm = "PM";
		}
		p.setTextSize( WidgetThemeManager.getInstance().getFloat( "apm_font_size" ) );
		apmTexture = TextureUtil.createFontsTexture( apm , p );
	}
	
	public void addWatchView()
	{
		if( watchBackTexture == null )
		{
			return;
		}
		watchBack2DView = new ClockMember2DView( "watch_back" , watchBackTexture );
		watchBack2DView.setOrigin( WidgetClock.MODEL_WIDTH / 2 , WidgetClock.MODEL_WIDTH / 2 );
		this.addView( watchBack2DView );
	}
	
	public void addHourViewLeft()
	{
		if( hourGroupTextureLeft == null )
		{
			return;
		}
		hourGroup2DViewLeft = new ClockMember2DView( "clock_hour_left" , hourGroupTextureLeft );
		hourGroup2DViewLeft.x = WidgetThemeManager.getInstance().getFloat( "hour_left_x" );
		hourGroup2DViewLeft.y = WidgetThemeManager.getInstance().getFloat( "hour_left_y" );
		this.addView( hourGroup2DViewLeft );
	}
	
	public void addHourViewRight()
	{
		if( hourGroupTextureRight == null )
		{
			return;
		}
		hourGroup2DViewRight = new ClockMember2DView( "clock_hour_right" , hourGroupTextureRight );
		hourGroup2DViewRight.x = WidgetThemeManager.getInstance().getFloat( "hour_right_x" );
		hourGroup2DViewRight.y = WidgetThemeManager.getInstance().getFloat( "hour_right_y" );
		this.addView( hourGroup2DViewRight );
	}
	
	public void addDotView()
	{
		if( dotGroupTexture == null )
		{
			return;
		}
		dotGroup2DView = new ClockMember2DView( "clock_dot" , dotGroupTexture );
		this.addView( dotGroup2DView );
	}
	
	public void addMinuteViewLeft()
	{
		if( minuteGroupTextureLeft == null )
		{
			return;
		}
		minuteGroup2DViewLeft = new ClockMember2DView( "clock_minute_left" , minuteGroupTextureLeft );
		minuteGroup2DViewLeft.x = WidgetThemeManager.getInstance().getFloat( "minute_left_x" );
		minuteGroup2DViewLeft.y = WidgetThemeManager.getInstance().getFloat( "minute_left_y" );
		this.addView( minuteGroup2DViewLeft );
	}
	
	public void addMinuteViewRight()
	{
		if( minuteGroupTextureRight == null )
		{
			return;
		}
		minuteGroup2DViewRight = new ClockMember2DView( "clock_minute_right" , minuteGroupTextureRight );
		minuteGroup2DViewRight.x = WidgetThemeManager.getInstance().getFloat( "minute_right_x" );
		minuteGroup2DViewRight.y = WidgetThemeManager.getInstance().getFloat( "minute_right_y" );
		this.addView( minuteGroup2DViewRight );
	}
	
	public void addMonthView()
	{
		if( monthTexture == null )
		{
			return;
		}
		monthGroup2DView = new ClockMember2DView( "clock_month" , monthTexture );
		monthGroup2DView.x = WidgetThemeManager.getInstance().getFloat( "ddmmyy_x" );
		monthGroup2DView.y = WidgetThemeManager.getInstance().getFloat( "ddmmyy_y" );
		this.addView( monthGroup2DView );
	}
	
	public void addAPMView()
	{
		if( apmTexture == null )
		{
			return;
		}
		apmGroup2DView = new ClockMember2DView( "clock_apm" , apmTexture );
		apmGroup2DView.x = WidgetThemeManager.getInstance().getFloat( "apm_x" );
		apmGroup2DView.y = WidgetThemeManager.getInstance().getFloat( "apm_y" );
		this.addView( apmGroup2DView );
	}
	
	public void ClockTimeUpdate(
			int hour ,
			int minute )
	{
		if( ( TIME_HOUR != hour ) || ( TIME_MINUTE != minute ) )
		{
			TIME_HOUR = hour;
			TIME_MINUTE = minute;
			resetAllView();
			Load2DTexture();
			initView();
		}
	}
	
	public void resetAllView()
	{
		if( watchBack2DView != null )
		{
			watchBack2DView.remove();
			watchBack2DView.dispose();
			watchBack2DView = null;
		}
		if( hourGroup2DViewLeft != null )
		{
			hourGroup2DViewLeft.remove();
			hourGroup2DViewLeft.dispose();
			hourGroup2DViewLeft = null;
		}
		if( hourGroup2DViewRight != null )
		{
			hourGroup2DViewRight.remove();
			hourGroup2DViewRight.dispose();
			hourGroup2DViewRight = null;
		}
		if( dotGroup2DView != null )
		{
			dotGroup2DView.remove();
			dotGroup2DView.dispose();
			dotGroup2DView = null;
		}
		if( minuteGroup2DViewLeft != null )
		{
			minuteGroup2DViewLeft.remove();
			minuteGroup2DViewLeft.dispose();
			minuteGroup2DViewLeft = null;
		}
		if( minuteGroup2DViewRight != null )
		{
			minuteGroup2DViewRight.remove();
			minuteGroup2DViewRight.dispose();
			minuteGroup2DViewRight = null;
		}
		if( monthGroup2DView != null )
		{
			monthGroup2DView.remove();
			monthGroup2DView.dispose();
			monthGroup2DView = null;
		}
		if( apmGroup2DView != null )
		{
			apmGroup2DView.remove();
			apmGroup2DView.dispose();
			apmGroup2DView = null;
		}
	}
}
