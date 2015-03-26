package com.iLoong.launcher.SetupMenu;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class ForegroundService extends Service
{
	
	public static final String ACTION_FOREGROUNDSERVICE = "com.iLoong.service.ForegroundService";
	
	@Override
	public IBinder onBind(
			Intent intent )
	{
		return null;
	}
	
	@Override
	public void onCreate()
	{
		Log.v( "service" , "onCreate" );
		super.onCreate();
		//		UpdateTask updateTask = new UpdateTask(this);
		//		updateTask.startTask();
	}
	
	@Override
	public void onDestroy()
	{
		Log.v( "service" , "onDestroy" );
		stopForeground( true );
	}
	
	@Override
	public int onStartCommand(
			Intent intent ,
			int flags ,
			int startId )
	{
		Log.v( "service" , "onStartCommand:" + intent );
		if( ( intent != null ) && ( ACTION_FOREGROUNDSERVICE.equals( intent.getAction() ) ) )
		{
			Notification status = new Notification( 0 , getString( RR.string.lwp_description ) , System.currentTimeMillis() );
			status.setLatestEventInfo(
					this ,
					getString( RR.string.lwp_description ) ,
					getString( RR.string.launch_launcher_summary ) ,
					PendingIntent.getActivity( this , 0 , new Intent( this , iLoongLauncher.class ).addFlags( Intent.FLAG_ACTIVITY_NEW_TASK ) , 0 ) );
			try
			{
				status.getClass().getField( "priority" ).setInt( status , Notification.PRIORITY_MIN );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
			startForeground( 2014116 , status );
		}
		return START_NOT_STICKY;
	}
}
