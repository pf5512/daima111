package com.iLoong.launcher.SetupMenu;


import android.content.Context;
import android.content.Intent;


public class ForegroundServiceClient
{
	
	private Context mContext;
	
	public ForegroundServiceClient(
			Context context )
	{
		mContext = context;
	}
	
	public void StartServer()
	{
		Intent localIntent = new Intent( ForegroundService.ACTION_FOREGROUNDSERVICE );
		localIntent.setClass( mContext , ForegroundService.class );
		mContext.startService( localIntent );
	}
	
	public void StopServer()
	{
		Intent localIntent = new Intent( ForegroundService.ACTION_FOREGROUNDSERVICE );
		localIntent.setClass( mContext , ForegroundService.class );
		mContext.stopService( localIntent );
	}
}
