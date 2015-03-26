package com.coco.theme.themebox.apprecommend;


import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.Time;

import com.coco.download.Assets;


public class SelfRefresh
{
	
	private Context mContext;
	private Timer timerUpdate;
	private TimerTask taskUpdate;
	private Handler mHandler = new Handler();
	
	public SelfRefresh(
			Context context )
	{
		mContext = context;
		if( timerUpdate == null )
		{
			timerUpdate = new Timer( true );
		}
		if( taskUpdate == null )
		{
			taskUpdate = new TimerTask() {
				
				@Override
				public void run()
				{
					mHandler.post( new Runnable() {
						
						@Override
						public void run()
						{
							judgeUpdate();
						}
					} );
				}
			};
			timerUpdate.schedule( taskUpdate , 10 , 5 * 60 * 1000 );
		}
	}
	
	/**
	 * 判断是否联网
	 */
	public boolean IsHaveInternet(
			final Context context )
	{
		try
		{
			ConnectivityManager manger = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo info = manger.getActiveNetworkInfo();
			return( info != null && info.isConnected() );
		}
		catch( Exception e )
		{
			return false;
		}
	}
	
	// 判断是否超过1天，是否需要检查更新
	private void judgeUpdate()
	{
		Time curTime = new Time();
		curTime.setToNow(); // 取得系统时间。
		int cur_year = curTime.year;
		int cur_month = curTime.month + 1;
		int cur_day = curTime.monthDay;
		int cur_hour = curTime.hour;
		SharedPreferences sharedPrefer = PreferenceManager.getDefaultSharedPreferences( mContext );
		int record_year = sharedPrefer.getInt( "entrance_year" , cur_year );
		int record_month = sharedPrefer.getInt( "entrance_month" , cur_month );
		int record_day = sharedPrefer.getInt( "entrance_day" , cur_day );
		int record_hour = sharedPrefer.getInt( "entrance_hour" , cur_hour );
		if( cur_year != record_year || cur_month != record_month || Math.abs( ( record_day * 24 + record_hour ) - ( cur_day * 24 + cur_hour ) ) >= 24 )
		{
			// 下载version.xml判断更新
			if( isHaveInternet( mContext ) )
			{
				String[] str = { "http://yu01.coomoe.com/uimenu/getlist.ashx" , "http://yu02.coomoe.com/uimenu/getlist.ashx" };
				String oldVersion = "";
				String url = "";
				SharedPreferences sharedPrefer1 = PreferenceManager.getDefaultSharedPreferences( mContext );
				oldVersion = sharedPrefer1.getString( "recommendVersion" , "" );
				if( oldVersion != null )
				{
					if( oldVersion.equals( "" ) )
					{
						url = str[(int)( Math.random() * 10 ) % ( str.length )] + "?p07=com.coco.lock2.lockbox" + "&p02=" + getVersionCode( mContext ) + "&" + Assets.getPhoneParams( mContext );
					}
					else
					{
						url = str[(int)( Math.random() * 10 ) % ( str.length )] + "?p07=com.coco.lock2.lockbox" + "&p02=" + getVersionCode( mContext ) + "&p08=" + oldVersion + "&" + Assets
								.getPhoneParams( mContext );
					}
				}
				else
				{
					url = str[(int)( Math.random() * 10 ) % ( str.length )] + "?p07=com.coco.lock2.lockbox" + "&p02=" + getVersionCode( mContext ) + "&" + Assets.getPhoneParams( mContext );
				}
				new MyAsyncTask( mContext ).execute( url );
			}
		}
	}
	
	/**
	 * 获取软件版本号
	 */
	private int getVersionCode(
			Context context )
	{
		int versionCode = 0;
		try
		{
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo( context.getPackageName() , 0 ).versionCode;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		return versionCode;
	}
	
	public void dispose()
	{
		if( timerUpdate != null )
		{
			timerUpdate.cancel();
			if( taskUpdate != null )
			{
				taskUpdate.cancel();
			}
		}
	}
	
	private boolean isHaveInternet(
			Context context )
	{
		try
		{
			ConnectivityManager manger = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo info = manger.getActiveNetworkInfo();
			return( info != null && info.isConnected() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return false;
		}
	}
}
