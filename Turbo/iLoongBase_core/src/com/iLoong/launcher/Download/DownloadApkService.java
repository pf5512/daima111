package com.iLoong.launcher.Download;


import java.io.File;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;


public class DownloadApkService extends Service
{
	
	private DownloadManager mDownloadManager;
	private DownloadCompleteReceiver mDownloadCompleteReceiver;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mDownloadManager = (DownloadManager)getSystemService( Context.DOWNLOAD_SERVICE );
		mDownloadCompleteReceiver = new DownloadCompleteReceiver();
		registerReceiver( mDownloadCompleteReceiver , new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE ) );
	}
	
	@Override
	public int onStartCommand(
			Intent intent ,
			int flags ,
			int startId )
	{
		String url = intent.getStringExtra( "download_url" );
		String name = intent.getStringExtra( "apk_name" );
		DownloadManager.Request request = new DownloadManager.Request( Uri.parse( url ) );
		request.setAllowedNetworkTypes( DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI );
		request.setShowRunningNotification( true );
		request.setVisibleInDownloadsUi( false );
		request.setDestinationInExternalPublicDir( "/download/" , name );
		mDownloadManager.enqueue( request );
		return super.onStartCommand( intent , flags , startId );
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if( mDownloadCompleteReceiver != null )
		{
			unregisterReceiver( mDownloadCompleteReceiver );
		}
	}
	
	@Override
	public IBinder onBind(
			Intent intent )
	{
		return null;
	}
	
	class DownloadCompleteReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context context ,
				Intent intent )
		{
			if( intent.getAction().equals( DownloadManager.ACTION_DOWNLOAD_COMPLETE ) )
			{
				long downId = intent.getLongExtra( DownloadManager.EXTRA_DOWNLOAD_ID , -1 );
				DownloadManager.Query query = new Query();
				query.setFilterById( downId );
				Cursor cursor = mDownloadManager.query( query );
				if( cursor.moveToFirst() )
				{
					String uri = cursor.getString( cursor.getColumnIndex( DownloadManager.COLUMN_LOCAL_URI ) );
					installApk( uri.substring( 5 ) );
					kill();
				}
			}
		}
	}
	
	private void installApk(
			String uri )
	{
		Intent intent = new Intent();
		intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent.setAction( Intent.ACTION_VIEW );
		intent.setDataAndType( Uri.fromFile( new File( uri ) ) , "application/vnd.android.package-archive" );
		startActivity( intent );
	}
	
	private void kill()
	{
		stopSelf();
	}
}
