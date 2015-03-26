package com.thirdParty.analytics.google;


import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.DateFormat;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.MapBuilder;


public class GoogleAgent
{
	
	private static final String PRE_ACTIVE_USER_DATE = "active_user_date";
	private static String activeUserDate = null;
	
	public static void checkActiveState(
			Context context )
	{
		String curDate = DateFormat.format( "yyyyMMdd" , new Date() ).toString();
		if( activeUserDate != null && activeUserDate.equals( curDate ) )
		{
			return;
		}
		GAServiceManager.getInstance().setLocalDispatchPeriod( 60 );
		SharedPreferences sp = context.getSharedPreferences( "google_agent" , 0 );
		if( activeUserDate == null )
		{
			activeUserDate = sp.getString( PRE_ACTIVE_USER_DATE , "" );
		}
		if( !activeUserDate.equals( curDate ) )
		{
			activeUserDate = curDate;
			Editor edit = sp.edit();
			edit.putString( PRE_ACTIVE_USER_DATE , curDate );
			edit.commit();
			EasyTracker tracker = EasyTracker.getInstance( context );
			tracker.send( MapBuilder.createEvent( "EUserStat" , "EActiveUser" , "" , null ).build() );
		}
	}
}
