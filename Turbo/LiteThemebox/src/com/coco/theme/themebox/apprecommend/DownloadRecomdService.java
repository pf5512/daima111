package com.coco.theme.themebox.apprecommend;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.coco.theme.themebox.util.Log;
import com.iLoong.base.themebox.R;


public class DownloadRecomdService extends Service
{
	
	public static int NETTYPE_MOBILE = 0;
	public static int NETTYPE_UNICOM = 1;
	public static int NETTYPE_TELECOM = 2;
	private final int DOWNLOAD_COMPLETE = 0;
	private final int DOWNLOAD_FAIL = 1;
	public static final String DOWNLOAD_URL_KEY = "downloadUrl";
	public static final String DOWNLOAD_FILE_NAME = "downloadFileName";
	private NotificationManager mUpdateNotificationManager = null;
	private String DOWNLOAD_PATH = Environment.getExternalStorageDirectory() + "/CooeeLocker/download/";
	private Handler mHandler = new DownloadHandler();
	private SharedPreferences.Editor editor;
	private String downState;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		if( !com.coco.theme.themebox.StaticClass.set_directory_path.equals( "" ) )
		{
			DOWNLOAD_PATH = com.coco.theme.themebox.StaticClass.set_directory_path + "/CooeeLocker/download/";
		}
		createDownloadPath();
		Log.v( "**" , "service" );
		editor = PreferenceManager.getDefaultSharedPreferences( this ).edit();
	}
	
	@Override
	public IBinder onBind(
			Intent arg0 )
	{
		return null;
	}
	
	private void createDownloadPath()
	{
		try
		{
			File downloadPath = new File( DOWNLOAD_PATH );
			if( downloadPath != null && !downloadPath.exists() )
			{
				downloadPath.mkdirs();
				Log.v( "**" , "service1" );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			Log.v( "**" , "service2" );
		}
	}
	
	@Override
	public int onStartCommand(
			Intent intent ,
			int flags ,
			int startId )
	{
		if( intent == null )
		{
			return super.onStartCommand( intent , flags , startId );
		}
		mUpdateNotificationManager = (NotificationManager)getSystemService( NOTIFICATION_SERVICE );
		String downloadUrl = intent.getStringExtra( DOWNLOAD_URL_KEY );
		String downloadFileName = intent.getStringExtra( "downloadFileName" );
		if( downloadUrl != null && !"".equals( downloadUrl.trim() ) && downloadFileName != null && !"".equals( downloadFileName.trim() ) )
		{
			SharedPreferences sharedPrefer1 = PreferenceManager.getDefaultSharedPreferences( this );
			String downloadName = sharedPrefer1.getString( downloadFileName , "finish" );
			downState = downloadFileName;
			if( downloadName.equals( "finish" ) )
			{
				new Thread( new DownloadRunnable( downloadUrl , downloadFileName ) ).start();
				editor.putString( downloadFileName , "downing" );
				editor.commit();
			}
		}
		return super.onStartCommand( intent , flags , startId );
	}
	
	private class DownloadHandler extends Handler
	{
		
		@Override
		public void handleMessage(
				Message msg )
		{
			super.handleMessage( msg );
			int result = msg.what;
			DownloadRunnable downloadRunnable = (DownloadRunnable)msg.obj;
			if( downloadRunnable == null )
			{
				return;
			}
			switch( result )
			{
				case DOWNLOAD_COMPLETE:
				{
					Intent intent = new Intent();
					intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
					intent.setAction( android.content.Intent.ACTION_VIEW );
					intent.setDataAndType( Uri.fromFile( downloadRunnable.mDownloadFile ) , "application/vnd.android.package-archive" );
					PendingIntent downCompletedIntent = PendingIntent.getActivity( getApplicationContext() , 0 , intent , 0 );
					mUpdateNotificationManager.cancel( downloadRunnable.mNotifyId );
					Notification finishNotification = new Notification( R.drawable.logo , "" , System.currentTimeMillis() );
					finishNotification.setLatestEventInfo(
							DownloadRecomdService.this ,
							downloadRunnable.mDownloadFileName ,
							getResources().getString( R.string.server_download_finish ) ,
							downCompletedIntent );
					finishNotification.flags |= Notification.FLAG_AUTO_CANCEL;
					mUpdateNotificationManager.notify( downloadRunnable.mNotifyId , finishNotification );
					openFile( downloadRunnable.mDownloadFile );
					editor.putString( downState , "finish" );
					editor.commit();
				}
					break;
				case DOWNLOAD_FAIL:
				{
					mUpdateNotificationManager.cancel( downloadRunnable.mNotifyId );
					Notification failNotification = new Notification( R.drawable.logo , "" , System.currentTimeMillis() );
					failNotification.setLatestEventInfo(
							DownloadRecomdService.this ,
							downloadRunnable.mDownloadFileName ,
							getResources().getString( R.string.server_download_fail ) ,
							downloadRunnable.mUpdatePendingIntent );
					failNotification.flags |= Notification.FLAG_AUTO_CANCEL;
					mUpdateNotificationManager.notify( downloadRunnable.mNotifyId , failNotification );
					editor.putString( downState , "finish" );
					editor.commit();
				}
					break;
				default:
					break;
			}
			downloadRunnable = null;
			stopSelf();
		}
		
		private void openFile(
				File file )
		{
			Intent intent = new Intent();
			intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			intent.setAction( android.content.Intent.ACTION_VIEW );
			intent.setDataAndType( Uri.fromFile( file ) , "application/vnd.android.package-archive" );
			startActivity( intent );
		}
	}
	
	private class DownloadRunnable implements Runnable
	{
		
		String mDownloadUrl = null;
		String mDownloadFileName = null;
		File mDownloadFile = null;
		int mNotifyId = (int)SystemClock.elapsedRealtime();
		Notification mUpdateNotification = null;
		Intent mUpdateIntent = null;
		PendingIntent mUpdatePendingIntent = null;
		
		public DownloadRunnable(
				String downloadUrl ,
				String downloadFileName )
		{
			mDownloadUrl = downloadUrl;
			mDownloadFileName = downloadFileName;
			// mUpdateIntent = new Intent(GoDownloadService.this,
			// GoStore.class);
			mUpdateIntent = new Intent();
			mUpdatePendingIntent = PendingIntent.getActivity( DownloadRecomdService.this , 0 , mUpdateIntent , 0 );
			String tickerText = getResources().getString( R.string.server_begin_download );
			mUpdateNotification = new Notification( R.drawable.logo , tickerText , System.currentTimeMillis() );
			mUpdateNotification.setLatestEventInfo( DownloadRecomdService.this , downloadFileName , "0%" , mUpdatePendingIntent );
			mUpdateNotification.flags = Notification.FLAG_ONGOING_EVENT;
			mUpdateNotificationManager.notify( mNotifyId , mUpdateNotification );
		}
		
		public void run()
		{
			Message message = mHandler.obtainMessage();
			message.obj = this;
			try
			{
				mDownloadFile = new File( DOWNLOAD_PATH , mDownloadFileName + ".apk" );
				if( !mDownloadFile.exists() )
				{
					mDownloadFile.createNewFile();
				}
				else
				{
					try
					{
						if( !mDownloadFile.delete() )
						{
							mDownloadFile = new File( DOWNLOAD_PATH , mDownloadFileName + mNotifyId + ".apk" );
						}
						mDownloadFile.createNewFile();
					}
					catch( SecurityException e )
					{
						e.printStackTrace();
						mDownloadFile = new File( DOWNLOAD_PATH , mDownloadFileName + mNotifyId + ".apk" );
						mDownloadFile.createNewFile();
					}
				}
				long downloadSize = downloadFile( mDownloadUrl , mDownloadFile );
				if( downloadSize > 0 )
				{
					message.what = DOWNLOAD_COMPLETE;
					mHandler.sendMessage( message );
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				mHandler.sendMessage( message );
			}
		}
		
		private long downloadFile(
				String downloadUrl ,
				File saveFile ) throws Exception
		{
			long totalSize = 0;
			HttpURLConnection httpConnection = null;
			InputStream is = null;
			FileOutputStream fos = null;
			try
			{
				URL url = new URL( downloadUrl );
				if( LoadRecomandActivity.isCWWAPConnect( getApplicationContext() ) && LoadRecomandActivity.getNetWorkType( getApplicationContext() ) != NETTYPE_UNICOM )
				{
					String host = LoadRecomandActivity.getProxyHost( getApplicationContext() );
					int port = LoadRecomandActivity.getProxyPort( getApplicationContext() );
					Proxy proxy = new Proxy( java.net.Proxy.Type.HTTP , new InetSocketAddress( host , port ) );
					httpConnection = (HttpURLConnection)url.openConnection( proxy );
					httpConnection.setConnectTimeout( 60000 );
					httpConnection.setReadTimeout( 60000 );
					httpConnection.setChunkedStreamingMode( 4096 );
					proxy = null;
				}
				else
				{
					httpConnection = (HttpURLConnection)url.openConnection();
					httpConnection.setConnectTimeout( 45000 );
					httpConnection.setReadTimeout( 45000 );
				}
				httpConnection.setDoInput( true );
				int responseCode = httpConnection.getResponseCode();
				if( responseCode != 200 )
				{
					throw new Exception( "fail!" );
				}
				is = httpConnection.getInputStream();
				int updateTotalSize = httpConnection.getContentLength();
				String loading = getResources().getString( R.string.server_downloading );
				int readsize = 0;
				int percentCount = 0;
				int curPercent = 0;
				byte buffer[] = new byte[4096];
				fos = new FileOutputStream( saveFile , false );
				while( ( readsize = is.read( buffer ) ) > 0 )
				{
					fos.write( buffer , 0 , readsize );
					totalSize += readsize;
					curPercent = (int)totalSize * 100 / updateTotalSize;
					if( curPercent > percentCount || percentCount >= 100 )
					{
						percentCount += 5;
						mUpdateNotification.setLatestEventInfo( DownloadRecomdService.this , loading + " - " + mDownloadFileName , curPercent + "%" , mUpdatePendingIntent );
						mUpdateNotificationManager.notify( mNotifyId , mUpdateNotification );
					}
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
				throw e;
			}
			finally
			{
				if( httpConnection != null )
				{
					httpConnection.disconnect();
				}
				if( is != null )
				{
					is.close();
				}
				if( fos != null )
				{
					fos.close();
				}
			}
			return totalSize;
		}
	}
}
