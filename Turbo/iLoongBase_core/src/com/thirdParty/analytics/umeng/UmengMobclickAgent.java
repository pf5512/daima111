package com.thirdParty.analytics.umeng;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.umeng.analytics.MobclickAgent;


public class UmengMobclickAgent
{
	
	public static void FirstTime(
			Context context ,
			String key )
	{
		SharedPreferences sp = context.getSharedPreferences( "analytics" , Context.MODE_PRIVATE );
		Boolean flag = sp.getBoolean( key , false );
		if( !flag )
		{
			MobclickAgent.onEvent( context , key );
			Editor sharedata = sp.edit();
			sharedata.putBoolean( key , true );
			sharedata.commit();
		}
	}
	
	public static void NewUser(
			Context content )
	{
		SharedPreferences sp = content.getSharedPreferences( "analytics" , 0 );
		Boolean flag = sp.getBoolean( "LNewUser" , false );
		if( !flag )
		{
			MobclickAgent.onEvent( content , "LNewUser" );
			Editor sharedata = sp.edit();
			sharedata.putBoolean( "LNewUser" , true );
			sharedata.commit();
		}
	}
	
	public static void add(
			Activity context ,
			String string )
	{
		Activity act = context;
		SharedPreferences sp = act.getSharedPreferences( "analytics" , 0 );
		int num = sp.getInt( string , 0 );
		num++;
		Editor sharedata = sp.edit();
		sharedata.putInt( string , num );
		sharedata.commit();
	}
}
