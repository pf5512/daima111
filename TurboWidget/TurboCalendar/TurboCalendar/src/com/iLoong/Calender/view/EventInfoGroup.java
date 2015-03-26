package com.iLoong.Calender.view;


import java.io.IOException;

import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Calender.common.CalendarAllEvents;
import com.iLoong.Calender.common.CalenderHelper;
import com.iLoong.Calender.common.Parameter;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.MainAppContext;
//import com.google.analytics.tracking.android.GAServiceManager;
//import com.google.analytics.tracking.android.GoogleAnalytics;
//import com.google.analytics.tracking.android.MapBuilder;
//import com.google.analytics.tracking.android.Tracker;


public class EventInfoGroup extends ViewGroup3D
{
	
	private MainAppContext maincontext = null;
	private CalendarAllEvents cleinfo = null;
	
	public EventInfoGroup(
			String name ,
			MainAppContext maincontext ,
			Context context ,
			CalendarAllEvents cleinfo )
	{
		super( name );
		this.maincontext = maincontext;
		this.transform = true;
		this.cleinfo = cleinfo;
	}
	
	public boolean is3dRotation()
	{
		return true;
	}
	
	EventGlasses stringadd = null;
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		return super.onTouchDown( x , y , pointer );
	}
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( stringadd != null )
		{
			this.removeView( stringadd );
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( stringadd != null )
		{
			this.removeView( stringadd );
		}
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
//		GAServiceManager.getInstance().setLocalDispatchPeriod( 10 ); //表示延迟10秒提交 
//		GoogleAnalytics ga = GoogleAnalytics.getInstance( context ); //context使用comet launcher的context 
//		Tracker tracker = ga.getTracker( "UA-48567460-2" ); //使用日历widget的property 
//		tracker.send( MapBuilder.createEvent( "CometCalendar" , "ClickAdd" , "" , null ).build() );
//		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
//		if( preferences.getBoolean( CalenderHelper.UPGRADE_VERIFICATION , false ) )
//		{
//		}
//		else
//		{
//			if( CalenderHelper.isUpgradePacketInstalled( context ) )
//			{
//				Intent intent = new Intent();
//				//						intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//				intent.setClassName( "com.cooeecomet.launcher.key" , "com.cooeecomet.launcher.key.PrimeActivity" );
//				iLoongLauncher.getInstance().startActivityForResult( intent , 12 );
//				//						( (Activity)context ).startActivityForResult( intent ,12);
//			}
//			else
//			{
//				MobclickAgent.onEvent( iLoongLauncher.getInstance() , "EntryPrimeADS" , "Calendar_addEvent" );
//				Intent intent = new Intent( maincontext.mWidgetContext , AdActivity.class );
//				intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//				maincontext.mWidgetContext.startActivity( intent );
//			}
//			return true;
//		}
//		
		String message = null;
		if( cleinfo.getAll_day().equals( "1" ) )
		{
			message = CalenderHelper.LongToDatePart( Long.parseLong( cleinfo.getBegin() ) );
		}
		else
		{
			message = CalenderHelper.LongToDate( Long.parseLong( cleinfo.getBegin() ) ) + "-" + CalenderHelper.LongToDate( Long.parseLong(cleinfo.getEnd() ) );
		}
		
		
		stringadd = new EventGlasses( maincontext , "stringadd" , getTextureRegion( maincontext,cleinfo.getTitle() ,message) , "mod_add01.obj" );
		stringadd.build();
		stringadd.move(
				WidgetCalender.getIntance().getHeight()/2 + (Parameter.Calender_Little_BianKuang_Width ) * WidgetCalender.scale ,
				( Parameter.Calender_Workspace_Bottom + Parameter.Calender_Bottom_JinShuTiao ) * WidgetCalender.scale + ( 112 / 2 ) * ( Parameter.Calender_Day_Glass_Height * WidgetCalender.height_scale * 5 - 142f ) / ( 112 * 4f ) ,
				0f );
		this.addView( stringadd );
		long eventID = Long.parseLong( cleinfo.get_id() );
		long startTime = Long.parseLong( cleinfo.getBegin() );
		long endTime = Long.parseLong( cleinfo.getEnd() );
		Uri uri = null;
		if( Build.VERSION.SDK_INT >= 8 )
		{
			uri = ContentUris.withAppendedId( Uri.parse( "content://com.android.calendar/events" ) , eventID );
		}
		else
		{
			uri = ContentUris.withAppendedId( Uri.parse( "content://calendar/events" ) , eventID );
		}
		Intent intent = new Intent( "android.intent.action.VIEW" , uri );
		//			intent.setAction( "android.intent.action.EventInfo" );
		intent.addCategory( "android.intent.category.DEFAULT" );
		//			intent.setData( Uri.parse("content://com.android.calendar/events/" + String.valueOf(7))); 
		intent.setType( "vnd.android.cursor.item/event" );
		intent.putExtra( CalendarContract.EXTRA_EVENT_BEGIN_TIME , startTime );
		intent.putExtra( CalendarContract.EXTRA_EVENT_END_TIME , endTime );
		intent.setData( uri );
		try
		{
			maincontext.mContainerContext.startActivity( intent );
		}
		catch( ActivityNotFoundException e )
		{
			Intent in = new Intent( "android.intent.action.VIEW" );
			in.setAction( "android.intent.action.EventInfo" );
			in.addCategory( "android.intent.category.DEFAULT" );
			//				intent.setData( Uri.parse("content://com.android.calendar/events/" + String.valueOf(7))); 
			in.setType( "vnd.android.cursor.item/event" );
			in.putExtra( CalendarContract.EXTRA_EVENT_BEGIN_TIME , startTime );
			in.putExtra( CalendarContract.EXTRA_EVENT_END_TIME , endTime );
			in.setData( uri );
			try
			{
				maincontext.mContainerContext.startActivity( in );
			}
			catch( ActivityNotFoundException e1 )
			{
				Intent in1 = new Intent( "android.intent.action.VIEW" );
				in1.addCategory( "android.intent.category.DEFAULT" );
				in1.setType( "vnd.android.cursor.item/uni-event" );
				in1.putExtra( CalendarContract.EXTRA_EVENT_BEGIN_TIME , startTime );
				in1.putExtra( CalendarContract.EXTRA_EVENT_END_TIME , endTime );
				in1.setData( uri );
				try
				{
					maincontext.mContainerContext.startActivity( in1 );
				}
				catch( ActivityNotFoundException e2 )
				{
					Intent in2 = new Intent( "android.intent.action.VIEW" );
					in2.addCategory( "android.intent.category.DEFAULT" );
					in2.setType( "vnd.android.cursor.item/the-event" );
					in2.putExtra( CalendarContract.EXTRA_EVENT_BEGIN_TIME , startTime );
					in2.putExtra( CalendarContract.EXTRA_EVENT_END_TIME , endTime );
					in2.setData( uri );
					try
					{
						maincontext.mContainerContext.startActivity( in2 );
					}
					catch( ActivityNotFoundException e3 )
					{
						e3.printStackTrace();
						return false;
					}
				}
			}
		}
		return super.onClick( x , y );
	}
	
	
	public TextureRegion getTextureRegion(
			MainAppContext appContext ,
			String title ,
			String time )
	{
		Bitmap backImage = null;
		float width = Parameter.BianKuang_Width * WidgetCalender.scale;
		float height;
		float Titletextsize;
		float Timetextsize;
		if( Utils3D.getScreenWidth() < 500 )
		{
			height = Parameter.BianKuang_Height * WidgetCalender.scale;
			Titletextsize = 35f * WidgetCalender.scale;
			Timetextsize = 23f * WidgetCalender.scale;
		}
		else
		{
			height = Parameter.BianKuang_Height * AddEventGroup.kuangoffset;
			Titletextsize = 35f *  AddEventGroup.kuangoffset;
			Timetextsize = 20f *  AddEventGroup.kuangoffset;
		}
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setARGB( 255 , 99 , 99 , 99 );
		paint.setSubpixelText( true );
		//		paint.setStrokeJoin( Paint.Join.ROUND );
		//		paint.setStrokeCap( Paint.Cap.ROUND );
		paint.setTextSize( Titletextsize );
		//		paint.setShadowLayer( (float)( 4 / 1.7 ) , (float)( 2 / 1.7 ) , (float)( 2 / 1.7 ) , Color.BLACK );// Color.BLACK
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		Bitmap bt = null;
		Bitmap newbt = null;
		try
		{
			bt = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( "theme/widget/cometcalendar/comet/image/" + "changtiaoclick.png" ) );
			newbt = CalenderHelper.resizeBitmap( bt , (int)width , (int)height );
			canvas.drawBitmap( newbt , 0f , 0f , paint );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		if( paint.measureText( title ) > width - 15 * WidgetCalender.scale )
		{
			while( paint.measureText( title ) > width - paint.measureText( "..." ) - 2 * WidgetCalender.scale - 26 * WidgetCalender.scale )
			{
				title = title.substring( 0 , title.length() - 1 );
			}
			title += "...";
		}
		canvas.drawText( title , 26 * WidgetCalender.scale , posY - 10 *AddEventGroup. kuangoffset , paint );
		paint.setTextSize( Timetextsize );
		if( Utils3D.getScreenWidth() < 500 )
		{
			canvas.drawText( time , 590 * WidgetCalender.scale - paint.measureText( time ) - 12 * WidgetCalender.scale , posY + 25 * WidgetCalender.scale , paint );
		}
		else
		{
			canvas.drawText( time , 590 * WidgetCalender.scale - paint.measureText( time ) - 12 * WidgetCalender.scale , posY + 25 *AddEventGroup.kuangoffset , paint );
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
