package com.iLoong.launcher.media;


import com.iLoong.launcher.desktop.iLoongLauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.iLoong.launcher.Desktop3D.Log;


public class MediaReceiver extends BroadcastReceiver
{
	
	@Override
	public void onReceive(
			Context context ,
			Intent intent )
	{
		Log.e( "media" , "action:" + intent.getAction() );
		if( iLoongLauncher.getInstance() == null )
			return;
		String action = intent.getAction();
		if( action.equals( Intent.ACTION_MEDIA_MOUNTED ) )
		{
			Log.e( "media" , "scan mount" );
			//MediaCache.getInstance().syncData();
		}
		else if( action.equals( Intent.ACTION_MEDIA_EJECT ) || action.equals( Intent.ACTION_MEDIA_UNMOUNTED ) )
		{
			Log.e( "media" , "scan unmount" );
		}
		else if( action.equals( Intent.ACTION_MEDIA_SCANNER_STARTED ) )
		{
		}
		else if( action.equals( Intent.ACTION_MEDIA_SCANNER_FINISHED ) )
		{
			Log.e( "media" , "scan finish" );
			MediaCache.getInstance().syncData( MediaCache.SYNC_ALL );
		}
		else if( action.equals( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE ) )
		{
			Log.e( "media" , "scan file:" + intent.getData() );
		}
	}
}
