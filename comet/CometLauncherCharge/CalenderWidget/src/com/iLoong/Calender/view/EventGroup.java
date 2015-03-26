package com.iLoong.Calender.view;


import java.util.Calendar;

import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.util.Log;

import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.iLoong.Calender.activity.AdActivity;
import com.iLoong.Calender.common.CalenderHelper;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.thirdParty.analytics.umeng.UmengMobclickAgent;
import com.umeng.analytics.MobclickAgent;


public class EventGroup extends ViewGroup3D
{
	
	private MainAppContext maincontext = null;
	private Context context = null;
	
	public EventGroup(
			String name ,
			MainAppContext maincontext ,
			Context context )
	{
		super( name );
		this.maincontext = maincontext;
		this.context = context;
		this.transform = true;
	}
	
	public boolean is3dRotation()
	{
		return true;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		GAServiceManager.getInstance().setLocalDispatchPeriod( 10 ); //表示延迟10秒提交 
		GoogleAnalytics ga = GoogleAnalytics.getInstance( context ); //context使用comet launcher的context 
		Tracker tracker = ga.getTracker( "UA-48567460-2" ); //使用日历widget的property 
		tracker.send( MapBuilder.createEvent( "CometCalendar" , "ClickAdd" , "" , null ).build() ); 
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
		if( preferences.getBoolean( CalenderHelper.UPGRADE_VERIFICATION , false ) )
		{
		}
		else
		{
			if( CalenderHelper.isUpgradePacketInstalled( context ) )
			{
				Intent intent = new Intent();
				//						intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				intent.setClassName( "com.cooeecomet.launcher.key" , "com.cooeecomet.launcher.key.PrimeActivity" );
				iLoongLauncher.getInstance().startActivityForResult( intent , 12 );
				//						( (Activity)context ).startActivityForResult( intent ,12);
			}
			else
			{
				MobclickAgent.onEvent( iLoongLauncher.getInstance() , UmengMobclickAgent.EVENT_ID_ENTRYPRIMEADS , "Calendar_addEvent" );
				Intent intent = new Intent( maincontext.mWidgetContext , AdActivity.class );
				intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				maincontext.mWidgetContext.startActivity( intent );
			}
			return true;
		}
		if( name.equals( "addEvent" ) )
		{
			Calendar c = Calendar.getInstance();
			int hour = c.get( Calendar.HOUR_OF_DAY );
			int minute = c.get( Calendar.MINUTE );
			Calendar beginTime = Calendar.getInstance();
			beginTime.set( AddEventGroup.year , AddEventGroup.month , AddEventGroup.day , hour , minute );
			Log.d( "Calender" , AddEventGroup.year + ":" + AddEventGroup.month + ":" + AddEventGroup.day );
			Calendar endTime = Calendar.getInstance();
			endTime.set( AddEventGroup.year , AddEventGroup.month , AddEventGroup.day , hour + 1 , minute );
			Intent intent = new Intent( Intent.ACTION_INSERT );
			intent.setType( "vnd.android.cursor.item/event" );
			intent.putExtra( CalendarContract.EXTRA_EVENT_BEGIN_TIME , beginTime.getTimeInMillis() );
			intent.putExtra( CalendarContract.EXTRA_EVENT_END_TIME , endTime.getTimeInMillis() );
			try
			{
				maincontext.mContainerContext.startActivity( intent );
			}
			catch( ActivityNotFoundException e )
			{
				Intent in = new Intent( "android.intent.action.EDIT" );
				in.addCategory( "android.intent.category.DEFAULT" );
				in.setType( "vnd.android.cursor.item/event" );
				in.putExtra( CalendarContract.EXTRA_EVENT_BEGIN_TIME , beginTime.getTimeInMillis() );
				in.putExtra( CalendarContract.EXTRA_EVENT_END_TIME , endTime.getTimeInMillis() );
				try
				{
					maincontext.mContainerContext.startActivity( in );
				}
				catch( ActivityNotFoundException e1 )
				{
					e1.printStackTrace();
					return false;
				}
			}
		}
		else
		{
//			long eventID=0l;
//			if( name != null )
//			{
			long eventID= Long.parseLong( name );
//			}
			//			Uri uri = ContentUris.withAppendedId( Events.CONTENT_URI , eventID );
			//			Intent intent = new Intent( Intent.ACTION_VIEW ).setData( uri );
			//			maincontext.mContainerContext.startActivity( intent );
			Uri uri = ContentUris.withAppendedId( Uri.parse( "content://com.android.calendar/events" ) , eventID );
			//			Log.d( "Calender" , "uri:"+uri);
			Intent intent = new Intent( "android.intent.action.VIEW" );
			//			intent.setAction( "android.intent.action.EventInfo" );
			intent.addCategory( "android.intent.category.DEFAULT" );
			//			intent.setData( Uri.parse("content://com.android.calendar/events/" + String.valueOf(7))); 
			intent.setType( "vnd.android.cursor.item/event" );
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
				in.setData( uri );
				try
				{
					maincontext.mContainerContext.startActivity( in );
				}
				catch( ActivityNotFoundException e1 )
				{
					e1.printStackTrace();
					return false;
				}
			}
		}
		return super.onClick( x , y );
	}
}
