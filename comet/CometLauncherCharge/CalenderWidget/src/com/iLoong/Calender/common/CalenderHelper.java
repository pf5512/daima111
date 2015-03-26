package com.iLoong.Calender.common;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.iLoong.Calender.view.WidgetCalender;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;


public class CalenderHelper
{
	
	public static int getCurrentDay()
	{
		Calendar calender = Calendar.getInstance();
		int day = calender.get( Calendar.DATE );
		return day;
	}
	
	public static int getCurrentMonth()
	{
		Calendar calender = Calendar.getInstance();
		int month = calender.get( Calendar.MONTH );
		return month;
	}
	
	public static int getCurrentYear()
	{
		Calendar calender = Calendar.getInstance();
		int year = calender.get( Calendar.YEAR );
		return year;
	}
	
	public static String[] caculateDay(
			int currentMonth ,
			int currentYear )
	{
		String[] days = new String[42];
		int currentDay = -1;
		int currentDayIndex = -1;
		Calendar calendar = Calendar.getInstance();
		calendar.set( currentYear , currentMonth , 1 );
		// 获得当前月的第1天是所在周围的第几天
		int week = calendar.get( Calendar.DAY_OF_WEEK );
		int monthDays = 0;
		int prevMonthDays = 0;
		monthDays = getMonthDays( currentYear , currentMonth );
		// 当前是1月
		if( currentMonth == 0 )
			// 计算一月份的上月的天数，也就是前一年最后一个月的天数
			prevMonthDays = getMonthDays( currentYear - 1 , 11 );
		else
			// 计算当前月份的上月的天数
			prevMonthDays = getMonthDays( currentYear , currentMonth - 1 );
		// 生成上月份配到当前的日期文字（ 前面添加星号，在显示时会去掉星号）
		for( int i = week , day = prevMonthDays ; i > 1 ; i-- , day-- )
		{
			days[i - 2] = "*" + String.valueOf( day );
		}
		// 生成普通日期的文字
		for( int day = 1 , i = week - 1 ; day <= monthDays ; day++ , i++ )
		{
			days[i] = String.valueOf( day );
			if( day == currentDay )
			{
				currentDayIndex = i;
			}
		}
		// 生成下月份配到当前的日期文字（ 前面添加星号，在显示时会去掉星号）
		for( int i = week + monthDays - 1 , day = 1 ; i < days.length ; i++ , day++ )
		{
			days[i] = "*" + String.valueOf( day );
		}
		return days;
	}
	
	public static int getMonthDays(
			int year ,
			int month )
	{
		month++;
		switch( month )
		{
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
			{
				return 31;
			}
			case 4:
			case 6:
			case 9:
			case 11:
			{
				return 30;
			}
			case 2:
			{
				if( ( ( year % 4 == 0 ) && ( year % 100 != 0 ) ) || ( year % 400 == 0 ) )
					return 29;
				else
					return 28;
			}
		}
		return 0;
	}
	
	public static Long DateToLong(
			int year ,
			int month ,
			int day ,
			int hour ,
			int minute ,
			int second )
	{
		Date loginTD = new Date( ( year - 1900 ) , month , day , hour , minute , second );
		return loginTD.getTime();
	}
	
	public static String LongToDate(
			Long time )
	{
		Date date = new Date();
		Date d = new Date( time );
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy.MM.dd  hh:mm" );
		return sdf.format( d );
	}
	
	public static String LongToDateOnlyDay(
			Long time )
	{
		Date date = new Date();
		Date d = new Date( time );
		SimpleDateFormat sdf = new SimpleDateFormat( "d" );
		return sdf.format( d );
	}
	
	public static List<CalenderEvent> QueryAllCalendar(
			ContentResolver contentResolver )
	{
		List<CalenderEvent> list = new ArrayList<CalenderEvent>();
		//日历里面相应的Event的URI 
		if( WidgetCalender.getIntance().judgetifShowEvents() )
		{
			Uri uri = null;
			if( Integer.parseInt( Build.VERSION.SDK ) >= 8 )
			{
				uri = Uri.parse( "content://com.android.calendar/events" );
			}
			else
			{
				uri = Uri.parse( "content://calendar/events" );
			}
			Cursor cur = contentResolver.query( uri , null , null , null , null );
			while( cur.moveToNext() )
			{
				CalenderEvent calenderevent = new CalenderEvent();
				calenderevent.set_id( cur.getString( cur.getColumnIndex( "_id" ) ) );
				calenderevent.setTitle( cur.getString( cur.getColumnIndex( "title" ) ) );
				calenderevent.setDtstart( cur.getString( cur.getColumnIndex( "dtstart" ) ) );
				calenderevent.setDtend( cur.getString( cur.getColumnIndex( "dtend" ) ) );
				list.add( calenderevent );
			}
			cur.close();
		}
		return list;
	}
	
	public static List<String> judgeMonthIfEvents(
			ContentResolver contentResolver ,
			int year ,
			int month )
	{
		List<CalenderEvent> list = CalenderHelper.QueryAllCalendar( contentResolver );
		List<String> saveeventdays = new ArrayList<String>();
		Log.d( "Calender" , "year" + year + ":" + "month" + month );
		Log.d( "Calender" , "天数" + CalenderHelper.getMonthDays( year , month ) );
		for( int i = 0 ; i < CalenderHelper.getMonthDays( year , month ) ; i++ )
		{
			Long a = Long.parseLong( CalenderHelper.DateToLong( year , month , i + 1 , 0 , 0 , 0 ) + "" );
			Long b = Long.parseLong( CalenderHelper.DateToLong( year , month , i + 1 , 23 , 59 , 59 ) + "" );
			for( int j = 0 ; j < list.size() ; j++ )
			{
				String startTime = list.get( j ).getDtstart();
				String endTime = list.get( j ).getDtend();
				Long x = Long.parseLong( startTime );
				Long y = Long.parseLong( endTime );
				if( ( x > a && y < b ) || ( x < a && y > b ) || ( x < a && y > a && y < b ) || ( x > a && x < b && y > b ) )
				{
					saveeventdays.add( ( i + 1 ) + "" );
					break;
				}
			}
		}
		return saveeventdays;
	}
	
	public static boolean judgeDayIfEvents(
			ContentResolver contentResolver ,
			int year ,
			int month ,
			int day )
	{
		List<CalenderEvent> list = CalenderHelper.QueryAllCalendar( contentResolver );
		Long a = Long.parseLong( CalenderHelper.DateToLong( year , month , day , 0 , 0 , 0 ) + "" );
		Long b = Long.parseLong( CalenderHelper.DateToLong( year , month , day , 23 , 59 , 59 ) + "" );
		for( int j = 0 ; j < list.size() ; j++ )
		{
			String startTime = list.get( j ).getDtstart();
			String endTime = list.get( j ).getDtend();
			Long x = Long.parseLong( startTime );
			Long y = Long.parseLong( endTime );
			if( ( x > a && y < b ) || ( x < a && y > b ) || ( x < a && y > a && y < b ) || ( x > a && x < b && y > b ) )
			{
				return true;
			}
		}
		return false;
	}
public static final String UPGRADE_VERIFICATION = "upgrade_verification";
	
	public static boolean isUpgradePacketInstalled(Context context)
	{
		PackageManager manager = context.getPackageManager();
		try
		{
			PackageInfo info = manager.getPackageInfo( "com.cooeecomet.launcher.key" , PackageManager.GET_ACTIVITIES );
			if( info != null )
			{
				return true;
			}
		}
		catch( NameNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;
	}
}
