package com.umeng.analytics;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.format.Time;
import android.util.Log;


public class CoCoMobclickAgent
{
	
	public static void NewUser(
			Context content )
	{
		SharedPreferences sp = content.getSharedPreferences( "analytics" , 0 );
		Boolean flag = sp.getBoolean( "NewUser" , false );
		Log.d( "song" , " NewUser 1" );
		if( !flag )
		{
			Log.d( "song" , " NewUser 2" );
			MobclickAgent.onEvent( content , "NewUser" , getPackageName( content ) );
			Editor sharedata = sp.edit();
			sharedata.putBoolean( "NewUser" , true );
			sharedata.commit();
		}
	}
	
	public static void ActiveUser(
			Context content )
	{
		Time time = new Time( "GMT+8" );
		time.setToNow();
		int day = time.yearDay;
		SharedPreferences sp = content.getSharedPreferences( "analytics" , 0 );
		int aday = sp.getInt( "Day" , -1 );
		if( day != aday )
		{
			MobclickAgent.onEvent( content , "ActiveUser" );
			Editor sharedata = sp.edit();
			sharedata.putInt( "Day" , day );
			sharedata.commit();
		}
	}
	
	private static String packagename = "";
	
	public static String getPackageName(
			Context content )
	{
		if( packagename != null && !packagename.equals( "" ) )
		{
			Log.d( "song" , "getPackageName1 =" + packagename );
			return packagename;
		}
		// 获取packagemanager的实例
		PackageManager packageManager = content.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		try
		{
			packInfo = packageManager.getPackageInfo( content.getPackageName() , 0 );
			packagename = packInfo.packageName;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		//packagename =content.getApplicationInfo().packageName;
		Log.d( "song" , "getPackageName2 =" + packagename );
		return packagename;
	}
}
