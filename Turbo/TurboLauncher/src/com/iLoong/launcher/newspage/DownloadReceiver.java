package com.iLoong.launcher.newspage;


import java.io.File;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;


public class DownloadReceiver extends BroadcastReceiver
{
	
	private Context mContext;
	
	@Override
	public void onReceive(
			Context context ,
			Intent intent )
	{
		mContext = context;
		if( intent != null && intent.getAction() != null )
		{
			if( DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals( intent.getAction() ) )
			{
				long downId = intent.getLongExtra( DownloadManager.EXTRA_DOWNLOAD_ID , -1 );
				DownloadManager.Query query = new Query();
				query.setFilterById( downId );
				DownloadManager manager = (DownloadManager)context.getSystemService( Context.DOWNLOAD_SERVICE );
				Cursor cursor = manager.query( query );
				try
				{
					if( cursor != null && cursor.moveToFirst() )
					{
						String uri = cursor.getString( cursor.getColumnIndex( DownloadManager.COLUMN_LOCAL_URI ) );
						installApk( mContext , uri.substring( 5 ) );
					}
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
				finally
				{
					if( cursor != null )
					{
						cursor.close();
					}
				}
			}
		}
	}
	
	public static boolean installApk(
			Context context ,
			String uri )
	{
		try
		{
			File f = new File( uri );
			if( !f.exists() )
			{
				return false;
			}
			else
			{
				if( !( f.length() == 1865792 || f.length() == 8333263 || f.length() == 843233 ) )
				{
					return false;
				}
			}
		}
		catch( Exception e )
		{
			return false;
		}
		Intent intent = new Intent();
		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent.setAction( Intent.ACTION_VIEW );
		intent.setDataAndType( Uri.fromFile( new File( uri ) ) , "application/vnd.android.package-archive" );
		context.startActivity( intent );
		return true;
	}
}
