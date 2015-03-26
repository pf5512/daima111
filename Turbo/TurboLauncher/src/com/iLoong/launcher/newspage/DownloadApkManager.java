package com.iLoong.launcher.newspage;


import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class DownloadApkManager
{
	
	private DownloadManager mDownloadManager;
	private Context mContext;
	
	public DownloadApkManager(
			Context context )
	{
		mContext = context;
		mDownloadManager = (DownloadManager)mContext.getSystemService( Context.DOWNLOAD_SERVICE );
	}
	
	public boolean queryDownloadStatus(
			long downloadId )
	{
		boolean result = false;
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterById( downloadId );
		Cursor c = mDownloadManager.query( query );
		if( c == null )
		{
			return result;
		}
		if( c.moveToFirst() )
		{
			int status = c.getInt( c.getColumnIndex( DownloadManager.COLUMN_STATUS ) );
			switch( status )
			{
				case DownloadManager.STATUS_PAUSED:
					Log.v( "download" , "STATUS_PAUSED" );
					result = true;
					break;
				case DownloadManager.STATUS_PENDING:
					Log.v( "download" , "STATUS_PENDING" );
					result = true;
					break;
				case DownloadManager.STATUS_RUNNING:
					Log.v( "download" , "STATUS_RUNNING" );
					result = true;
					SendMsgToAndroid.sendToastMsg( mContext.getString( R.string.umeng_common_action_info_exist ) );
					break;
				case DownloadManager.STATUS_SUCCESSFUL:
					Log.v( "download" , "STATUS_SUCCESSFUL" );
					String uri = c.getString( c.getColumnIndex( DownloadManager.COLUMN_LOCAL_URI ) );
					result = DownloadReceiver.installApk( mContext , uri.substring( 5 ) );
					break;
				case DownloadManager.STATUS_FAILED:
					Log.v( "download" , "STATUS_FAILED" );
					result = false;
					break;
				default:
					break;
			}
		}
		return result;
	}
	
	public void doDownload(
			String url ,
			String fileName ,
			Context context ,
			String downIdSpKey )
	{
		try
		{
			DownloadManager.Request request = new DownloadManager.Request( Uri.parse( url ) );
			request.setAllowedNetworkTypes( DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI );
			request.setShowRunningNotification( true );
			request.setVisibleInDownloadsUi( false );
			request.setDestinationInExternalPublicDir( "/CooeeDownload/" , fileName );
			long downId = mDownloadManager.enqueue( request );
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
			Editor editor = sp.edit();
			editor.putLong( downIdSpKey , downId );
			editor.commit();
		}
		catch( IllegalArgumentException e )
		{
			SendMsgToAndroid.sendToastMsg( iLoongLauncher.getInstance().getString( RR.string.dlmng_sys_download_unable ) );
			return;
		}
	}
}
