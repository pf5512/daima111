package com.iLoong.Calender.common;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.Instances;
import android.util.Log;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Calender.view.WidgetCalender;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.Widget3D.MainAppContext;


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
		// int currentDayIndex = -1;
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
				// currentDayIndex = i;
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
	
	public static long DateToLong(
			int year ,
			int month ,
			int day ,
			int hour ,
			int minute ,
			int second )
	{
		// We do it use Calendar in android.
//		Calendar c = Calendar.getInstance();
//		c.set( Calendar.YEAR , year );
//		c.set( Calendar.MONTH , month );
//		c.set( Calendar.DAY_OF_MONTH , day );
//		c.set( Calendar.HOUR , hour );
//		c.set( Calendar.MINUTE , minute );
//		c.set( Calendar.SECOND , second );
//		c.set( Calendar.MILLISECOND , 0 );
//		return c.getTimeInMillis();
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=year+"-"+(month+1)+"-"+day+" "+hour+":"+minute+":"+second;
//		Log.d( "mytag" , time );
		try
		{
			long timeEnd =sdf.parse(time).getTime();
			return timeEnd;
		}
		catch( ParseException e )
		{
			e.printStackTrace();
		}
		return 0L;
	}
	
	public static String LongToDate(
			long time )
	{
		Date d = new Date( time );
		SimpleDateFormat sdf = new SimpleDateFormat( "MM.dd  HH:mm" );
		return sdf.format( d );
	}
	
	public static String LongToDatePart(
			long time )
	{
		Date d = new Date( time );
		SimpleDateFormat sdf = new SimpleDateFormat( "MM.dd" );
		return sdf.format( d );
	}
	
	public static List<CalenderEvent> QueryAllCalendar(
			ContentResolver contentResolver )
	{
		List<CalenderEvent> list = new ArrayList<CalenderEvent>();
		// 日历里面相应的Event的URI
		if( WidgetCalender.getIntance().judgetifShowEvents() )
		{
			Uri uri = null;
			if( Build.VERSION.SDK_INT >= 8 )
			{
				uri = Uri.parse( "content://com.android.calendar/events" );
			}
			else
			{
				uri = Uri.parse( "content://calendar/events" );
			}
			Cursor cur = contentResolver.query( uri , null , null , null , null );
			if( cur != null )
			{
				while( cur.moveToNext() )
				{
					CalenderEvent calenderevent = new CalenderEvent();
					calenderevent.set_id( cur.getString( cur.getColumnIndex( "_id" ) ) );
					calenderevent.setTitle( cur.getString( cur.getColumnIndex( "title" ) ) );
					calenderevent.setDtstart( cur.getString( cur.getColumnIndex( "dtstart" ) ) );
					calenderevent.setDtend( cur.getString( cur.getColumnIndex( "dtend" ) ) );
					calenderevent.setAll_day( cur.getString( cur.getColumnIndex( "allDay" ) ) );
					calenderevent.setDuration( cur.getString( cur.getColumnIndex( "duration" ) ) );
					calenderevent.setRrule( cur.getString( cur.getColumnIndex( "rrule" ) ) );
					calenderevent.setRdate( cur.getString( cur.getColumnIndex( "rdate" ) ) );
					list.add( calenderevent );
				}
				cur.close();
			}
		}
		return list;
	}
	
	public static List<CalendarIntance> QueryAllCalendarIntances(
			ContentResolver contentResolver ,
			long a ,
			long b )
	{
		List<CalendarIntance> list = new ArrayList<CalendarIntance>();
		if( WidgetCalender.getIntance().judgetifShowEvents() )
		{
			Uri CONTENT_URI = null;
			if( Build.VERSION.SDK_INT >= 8 )
			{
				CONTENT_URI = Uri.parse( "content://com.android.calendar/instances/when" );
			}
			else
			{
				CONTENT_URI = Uri.parse( "content://calendar/instances/when" );
			}
			//		Uri CONTENT_URI = Uri.parse( "content://" + "com.android.calendar" + "/instances/when" );
			Uri.Builder builder = CONTENT_URI.buildUpon();
			ContentUris.appendId( builder , a );
			ContentUris.appendId( builder , b );
			Cursor cur = contentResolver.query( builder.build() , new String[]{ Instances.EVENT_ID , Instances.BEGIN , Instances.END } , null , null , null );
			if( cur != null )
			{
				while( cur.moveToNext() )
				{
					CalendarIntance ci = new CalendarIntance();
					ci.setEvent_id( cur.getString( cur.getColumnIndex( Instances.EVENT_ID ) ) );
					ci.setBegin( cur.getString( cur.getColumnIndex( Instances.BEGIN ) ) );
					ci.setEnd( cur.getString( cur.getColumnIndex( Instances.END ) ) );
					list.add( ci );
				}
				cur.close();
			}
		}
		return list;
	}
	
	public static List<CalendarAllEvents> MergeNewList(
			ContentResolver contentResolver ,
			long a ,
			long b )
	{
		List<CalenderEvent> list = CalenderHelper.QueryAllCalendar( contentResolver );
		List<CalendarIntance> calendarIntance = CalenderHelper.QueryAllCalendarIntances( contentResolver , a , b );
		List<CalendarAllEvents> all = new ArrayList<CalendarAllEvents>();
		if( list.size() > 0 && calendarIntance.size() > 0 )
		{
			for( int i = 0 ; i < list.size() ; i++ )
			{
				for( int j = 0 ; j < calendarIntance.size() ; j++ )
				{
					if( calendarIntance.get( j ).getEvent_id().equals( list.get( i ).get_id() ) )
					{
						CalendarAllEvents allevents = new CalendarAllEvents();
						allevents.set_id( list.get( i ).get_id() );
						allevents.setDtstart( list.get( i ).getDtstart() );
						allevents.setDtend( list.get( i ).getDtend() );
						allevents.setDuration( list.get( i ).getDuration() );
						allevents.setAll_day( list.get( i ).getAll_day() );
						allevents.setTitle( list.get( i ).getTitle() );
						allevents.setRdate( list.get( i ).getRdate() );
						allevents.setRrule( list.get( i ).getRrule() );
						allevents.setBegin( calendarIntance.get( j ).getBegin() );
						allevents.setEnd( calendarIntance.get( j ).getEnd() );
						allevents.setEvent_id( calendarIntance.get( j ).getEvent_id() );
						all.add( allevents );
					}
				}
			}
		}
		return all;
	}
	
	public static List<String> judgeMonthIfEvents(
			ContentResolver contentResolver ,
			int year ,
			int month )
	{
		long alla = Long.parseLong( CalenderHelper.DateToLong( year , month , 1 , 0 , 0 , 0 ) + "" );
		long allb = Long.parseLong( CalenderHelper.DateToLong( year , month , CalenderHelper.getMonthDays( year , month ) , 23 , 59 , 59 ) + "" );
		List<CalendarAllEvents> calendarIntance = CalenderHelper.MergeNewList( contentResolver , alla , allb );
		List<String> saveeventdays = new ArrayList<String>();
		if( calendarIntance.size() > 0 )
		{
			for( int i = 0 ; i < CalenderHelper.getMonthDays( year , month ) ; i++ )
			{
				long a = Long.parseLong( CalenderHelper.DateToLong( year , month , i + 1 , 0 , 0 , 0 ) + "" );
				long b = Long.parseLong( CalenderHelper.DateToLong( year , month , i + 1 , 23 , 59 , 59 ) + "" );
				for( int j = 0 ; j < calendarIntance.size() ; j++ )
				{
					String startTime = calendarIntance.get( j ).getBegin();
					String endTime = calendarIntance.get( j ).getEnd();
					long x = Long.parseLong( startTime );
					long y = Long.parseLong( endTime );
					if( calendarIntance.get( j ).getAll_day().equals( "1" ) )
					{
						if( x >= a && x <= b )
						{
							saveeventdays.add( ( i + 1 ) + "" );
							break;
						}
					}
					else
					{
						if( ( x >= a && x <= b && y >= a && y <= b ) || ( x <= a && y >= b ) || ( x <= a && y >= a && y <= b ) || ( x >= a && x <= b && y >= b ) )
						{
							saveeventdays.add( ( i + 1 ) + "" );
							break;
						}
					}
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
		long a = Long.parseLong( CalenderHelper.DateToLong( year , month , day , 0 , 0 , 0 ) + "" );
		long b = Long.parseLong( CalenderHelper.DateToLong( year , month , day , 23 , 59 , 59 ) + "" );
		List<CalendarAllEvents> list = CalenderHelper.MergeNewList( contentResolver , a , b );
		if( list.size() == 0 )
		{
			return false;
		}
		else
		{
			for( int j = 0 ; j < list.size() ; j++ )
			{
				String startTime = list.get( j ).getBegin();
				long x = Long.parseLong( startTime );
				String endTime = list.get( j ).getEnd();
				long y = Long.parseLong( endTime );
				if( list.get( j ).getAll_day().equals( "1" ) )
				{
					if( x >= a && x <= b )
					{
						return true;
					}
				}
				else
				{
					if( ( x >= a && x <= b && y >= a && y <= b ) || ( x <= a && y >= b ) || ( x <= a && y >= a && y <= b ) || ( x >= a && x <= b && y >= b ) )
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static final String UPGRADE_VERIFICATION = "upgrade_verification";
	
	public static boolean isUpgradePacketInstalled(
			Context context )
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
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public static Bitmap getBitmap(
			String Imagename ,
			MainAppContext mAppContext )
	{
		String FileName;
		try
		{
			FileName = "theme/widget/cometcalendar/comet/image/" + Imagename;
			return BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( FileName ) );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Bitmap getBitmap(
			String num ,
			int R ,
			int G ,
			int B )
	{
		Bitmap backImage = null;
		float width = Parameter.Calender_Day_Patch_Width * WidgetCalender.scale;
		float height = Parameter.Calender_Day_Patch_Height * WidgetCalender.scale;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setARGB( 255 , R , G , B );
		paint.setSubpixelText( true );
		paint.setTextSize( 26 * WidgetCalender.scale );
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = height - ( height - lineHeight ) / 2 - fontMetrics.bottom;
		canvas.drawText( num , ( width - paint.measureText( num ) ) / 2 , posY , paint );
		return backImage;
	}
	
	//	public static void makeRootDirectory(
	//			String filePath )
	//	{
	//		File file = null;
	//		try
	//		{
	//			file = new File( filePath );
	//			if( !file.exists() )
	//			{
	//				file.mkdir();
	//			}
	//		}
	//		catch( Exception e )
	//		{
	//		}
	//	}
	//	
	//	public static void saveBitmapToSdcard(
	//			String bitName ,
	//			Bitmap mBitmap )
	//	{
	//		File file = null;
	//		makeRootDirectory( "/sdcard/111/" );
	//		try
	//		{
	//			file = new File( "/sdcard/111/" + bitName + ".png" );
	//		}
	//		catch( Exception e )
	//		{
	//			e.printStackTrace();
	//		}
	//		FileOutputStream fOut = null;
	//		try
	//		{
	//			fOut = new FileOutputStream( file );
	//		}
	//		catch( FileNotFoundException e )
	//		{
	//			e.printStackTrace();
	//		}
	//		mBitmap.compress( Bitmap.CompressFormat.PNG , 100 , fOut );
	//		try
	//		{
	//			fOut.flush();
	//		}
	//		catch( IOException e )
	//		{
	//			e.printStackTrace();
	//		}
	//		try
	//		{
	//			fOut.close();
	//		}
	//		catch( IOException e )
	//		{
	//			e.printStackTrace();
	//		}
	//	}
	//	
	public static TextureRegion getRegion(
			MainAppContext maincontext ,
			int year )
	{
		Bitmap backImage = null;
		float width = 96 * WidgetCalender.scale;
		float height = 35 * WidgetCalender.scale;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setARGB( 255 , 255 , 255 , 255 );
		paint.setSubpixelText( true );
		paint.setTextSize( 34 * WidgetCalender.scale );
		//		FontMetrics fontMetrics = paint.getFontMetrics();
		//		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		//		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		String str = year + "";
		char years[] = str.toCharArray();
		//		Bitmap bt1 = null;
		//		Bitmap newbt1 = null;
		//		Bitmap bt2 = null;
		//		Bitmap newbt2 = null;
		//		Bitmap bt3 = null;
		//		Bitmap newbt3 = null;
		//		Bitmap bt4 = null;
		//		Bitmap newbt4 = null;
		if( years.length == 4 )
		{
			canvas.drawText( "." + year , 0 , height - 1f * WidgetCalender.scale , paint );
			//			bt1 = getBitmap( "day_" + years[0] + ".png" , maincontext );
			//			newbt1 = resizeBitmap( bt1 , (int)( 21 * WidgetCalender.scale ) , (int)( 34 * WidgetCalender.scale ) );
			//			canvas.drawBitmap( newbt1 , paint.measureText( "." ) , height - 1f * WidgetCalender.scale , paint );
			//			saveBitmapToSdcard( "test1" , bt1 );
			//			saveBitmapToSdcard( "testtest" , newbt1 );
			//			bt2 = getBitmap( "day_" + years[1] + ".png" , maincontext );
			//			newbt2 = resizeBitmap( bt2 , (int)( 21 * WidgetCalender.scale ) , (int)( 34 * WidgetCalender.scale ) );
			//			canvas.drawBitmap( newbt2 , paint.measureText( "." ) + ( 21 * WidgetCalender.scale ) , height - 1f * WidgetCalender.scale , paint );
			//			bt3 = getBitmap( "day_" + years[2] + ".png" , maincontext );
			//			newbt3 = resizeBitmap( bt3 , (int)( 21 * WidgetCalender.scale ) , (int)( 34 * WidgetCalender.scale ) );
			//			canvas.drawBitmap( newbt3 , paint.measureText( "." ) + ( 21 * WidgetCalender.scale ) * 2 , height - 1f * WidgetCalender.scale , paint );
			//			bt4 = getBitmap( "day_" + years[3] + ".png" , maincontext );
			//			newbt4 = resizeBitmap( bt4 , (int)( 21 * WidgetCalender.scale ) , (int)( 34 * WidgetCalender.scale ) );
			//			canvas.drawBitmap( newbt4 , paint.measureText( "." ) + ( 21 * WidgetCalender.scale ) * 3 , height - 1f * WidgetCalender.scale , paint );
		}
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		//		if( bt1 != null )
		//		{
		//			bt1.recycle();
		//		}
		//		if( newbt1 != null )
		//		{
		//			newbt1.recycle();
		//		}
		//		if( bt2 != null )
		//		{
		//			bt2.recycle();
		//		}
		//		if( newbt2 != null )
		//		{
		//			newbt2.recycle();
		//		}
		//		if( bt3 != null )
		//		{
		//			bt3.recycle();
		//		}
		//		if( newbt3 != null )
		//		{
		//			newbt3.recycle();
		//		}
		//		if( bt4 != null )
		//		{
		//			bt4.recycle();
		//		}
		//		if( newbt4 != null )
		//		{
		//			newbt4.recycle();
		//		}
		return newTextureRegion;
	}
	
	public static Bitmap resizeBitmap(
			Bitmap bm ,
			int w ,
			int h )
	{
		Bitmap BitmapOrg = bm;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		if( width == w && height == h )
			return bm;
		float scaleWidth = ( (float)w ) / width;
		float scaleHeight = ( (float)h ) / height;
		Matrix matrix = new Matrix();
		matrix.postScale( scaleWidth , scaleHeight );
		Bitmap tmp = Bitmap.createBitmap( BitmapOrg , 0 , 0 , width , height , matrix , true );
		bm.recycle();
		return tmp;
	}
	
	public static TextureRegion DrawPlane(
			MainAppContext maincontext ,
			String[] days )
	{
		Bitmap backImage = null;
		float width = ( Parameter.Calender_Day_Glass_Width * 7 ) * WidgetCalender.scale;
		float height = ( Parameter.Calender_Day_Glass_Height * 6 ) * WidgetCalender.height_scale;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setARGB( 255 , 99 , 99 , 99 );
		paint.setSubpixelText( true );
		paint.setTextSize( 26 * WidgetCalender.scale );
		Bitmap bt = null;
		Bitmap newbt = null;
		float Swidth = ( Parameter.Calender_Day_Glass_Width ) * WidgetCalender.scale;
		float Sheight = ( Parameter.Calender_Day_Glass_Height ) * WidgetCalender.height_scale;
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = Sheight - ( Sheight - lineHeight ) / 2 - fontMetrics.bottom;
		try
		{
			bt = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( "theme/widget/cometcalendar/comet/image/" + "plane.jpg" ) );
			newbt = CalenderHelper.resizeBitmap( bt , (int)width , (int)height );
			canvas.drawBitmap( newbt , 0f , 0f , paint );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		if( days.length == 42 )
		{
			for( int i = 0 ; i < days.length ; i++ )
			{
				if( days[i].contains( "*" ) )
				{
					String replace = days[i].replace( "*" , "" );
					paint.setARGB( 255 , 200 , 200 , 200 );
					canvas.drawText( replace , ( i % 7 ) * Swidth + ( Swidth - paint.measureText( replace ) ) / 2 , ( i / 7 ) * Sheight + posY , paint );
				}
				else
				{
					if( i == 0 || i == 6 || i == 7 || i == 13 || i == 14 || i == 20 || i == 21 || i == 27 || i == 28 || i == 34 || i == 35 || i == 41 )
					{
						paint.setARGB( 255 , 1 , 174 , 155 );
						canvas.drawText( days[i] , ( i % 7 ) * Swidth + ( Swidth - paint.measureText( days[i] ) ) / 2 , ( i / 7 ) * Sheight + posY , paint );
					}
					else
					{
						paint.setARGB( 255 , 99 , 99 , 99 );
						canvas.drawText( days[i] , ( i % 7 ) * Swidth + ( Swidth - paint.measureText( days[i] ) ) / 2 , ( i / 7 ) * Sheight + posY , paint );
					}
				}
			}
		}
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		if( bt != null )
		{
			bt.recycle();
		}
		if( newbt != null )
		{
			newbt.recycle();
		}
		return newTextureRegion;
	}
}
