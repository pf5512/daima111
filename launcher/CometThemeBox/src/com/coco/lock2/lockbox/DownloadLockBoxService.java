package com.coco.lock2.lockbox;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import com.coco.theme.themebox.MainActivity;
import com.coco.theme.themebox.StaticClass;
import com.iLoong.base.themebox.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import com.coco.theme.themebox.util.Log;
import android.widget.Toast;


public class DownloadLockBoxService extends Service
{
	
	public static int NETTYPE_MOBILE = 0;
	public static int NETTYPE_UNICOM = 1;
	public static int NETTYPE_TELECOM = 2;
	private final int DOWNLOAD_COMPLETE = 0;
	private final int DOWNLOAD_FAIL = 1;
	public static final String DOWNLOAD_URL_KEY = "downloadUrl";
	public static final String DOWNLOAD_FILE_NAME = "downloadFileName";
	private NotificationManager mUpdateNotificationManager = null;
	public static String DOWNLOAD_PATH = Environment.getExternalStorageDirectory() + "/Coco/download/";
	private Handler mHandler = new DownloadHandler();
	private int iconResourceId = R.drawable.cocologo;
	private HashMap<String , String> downloadList = new HashMap<String , String>();
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		if( com.coco.theme.themebox.StaticClass.canDownToInternal )
		{
			DOWNLOAD_PATH = com.coco.theme.themebox.StaticClass.INTERNAL_PATH + "/Coco/download/";
		}
		createDownloadPath();
		Log.v( "**" , "service" );
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
		String logo = intent.getStringExtra( "logo" );
		if( logo != null )
		{
			if( logo.equals( "cocolauncher" ) )
			{
				iconResourceId = R.drawable.coco_launcher;
			}
			else if( logo.equals( "cocolock" ) )
			{
				iconResourceId = R.drawable.cocologo;
			}
		}
		else
		{
			iconResourceId = R.drawable.cocologo;
		}
		if( downloadUrl != null && !"".equals( downloadUrl.trim() ) && downloadFileName != null && !"".equals( downloadFileName.trim() ) )
		{
			if( downloadList.containsKey( downloadUrl ) )
			{
				Toast.makeText( getApplicationContext() , downloadFileName + getResources().getString( R.string.is_downloading ) , Toast.LENGTH_SHORT ).show();
				return super.onStartCommand( intent , flags , startId );
			}
			downloadList.put( downloadUrl , downloadFileName );
			new Thread( new DownloadRunnable( downloadUrl , downloadFileName ) ).start();
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
					Notification finishNotification = new Notification( iconResourceId , "" , System.currentTimeMillis() );
					finishNotification.setLatestEventInfo(
							DownloadLockBoxService.this ,
							downloadRunnable.mDownloadFileName ,
							getResources().getString( R.string.server_download_finish ) ,
							downCompletedIntent );
					finishNotification.flags |= Notification.FLAG_AUTO_CANCEL;
					mUpdateNotificationManager.notify( downloadRunnable.mNotifyId , finishNotification );
					// xiatian add start //fix bug same as 0001880
					// Log.d("InstallAPKReceiver" ,
					// "sendBroadcast - DownloadAPKComplete - mNotifyId:" +
					// downloadRunnable.mNotifyId);
					Intent intentDownloadComplete = new Intent( "com.coco.lock2.lockbox.DownloadAPKComplete" );
					intentDownloadComplete.putExtra( "mNotifyId" , downloadRunnable.mNotifyId );
					sendBroadcast( intentDownloadComplete );
					// xiatian add end
					openFile( downloadRunnable.mDownloadFile );
				}
					break;
				case DOWNLOAD_FAIL:
				{
					mUpdateNotificationManager.cancel( downloadRunnable.mNotifyId );
					Notification failNotification = new Notification( iconResourceId , "" , System.currentTimeMillis() );
					failNotification.setLatestEventInfo(
							DownloadLockBoxService.this ,
							downloadRunnable.mDownloadFileName ,
							getResources().getString( R.string.server_download_fail ) ,
							downloadRunnable.mUpdatePendingIntent );
					failNotification.flags |= Notification.FLAG_AUTO_CANCEL;
					mUpdateNotificationManager.notify( downloadRunnable.mNotifyId , failNotification );
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
			mUpdatePendingIntent = PendingIntent.getActivity( DownloadLockBoxService.this , 0 , mUpdateIntent , 0 );
			String tickerText = getResources().getString( R.string.server_begin_download );
			mUpdateNotification = new Notification( iconResourceId , tickerText , System.currentTimeMillis() );
			mUpdateNotification.setLatestEventInfo( DownloadLockBoxService.this , downloadFileName , "0%" , mUpdatePendingIntent );
			// xiatian add start //fix bug same as 0001880
			// Log.d("InstallAPKReceiver" , "sendBroadcast - ToDownloadAPK");
			Intent intentToDownload = new Intent( "com.coco.lock2.lockbox.ToDownloadAPK" );
			sendBroadcast( intentToDownload );
			// xiatian add end
			mUpdateNotification.flags = Notification.FLAG_ONGOING_EVENT;
			mUpdateNotificationManager.notify( mNotifyId , mUpdateNotification );
		}
		
		public void run()
		{
			Message message = mHandler.obtainMessage();
			message.obj = this;
			try
			{
				mDownloadFile = new File( DOWNLOAD_PATH , mDownloadFileName + ".tmp" );
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
							mDownloadFile = new File( DOWNLOAD_PATH , mDownloadFileName + mNotifyId + ".tmp" );
						}
						mDownloadFile.createNewFile();
					}
					catch( SecurityException e )
					{
						e.printStackTrace();
						mDownloadFile = new File( DOWNLOAD_PATH , mDownloadFileName + mNotifyId + ".tmp" );
						mDownloadFile.createNewFile();
					}
				}
				long downloadSize = downloadFile( mDownloadUrl , mDownloadFile );
				if( downloadSize > 0 )
				{
					boolean res = mDownloadFile.renameTo( new File( DOWNLOAD_PATH , mDownloadFileName + ".apk" ) );
					if( res )
					{
						mDownloadFile = new File( DOWNLOAD_PATH , mDownloadFileName + ".apk" );
						message.what = DOWNLOAD_COMPLETE;
						mHandler.sendMessage( message );
					}
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
				downloadList.remove( mDownloadUrl );
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
				if( MainActivity.isCWWAPConnect( getApplicationContext() ) && MainActivity.getNetWorkType( getApplicationContext() ) != NETTYPE_UNICOM )
				{
					String host = MainActivity.getProxyHost( getApplicationContext() );
					int port = MainActivity.getProxyPort( getApplicationContext() );
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
						mUpdateNotification.setLatestEventInfo( DownloadLockBoxService.this , loading + " - " + mDownloadFileName , curPercent + "%" , mUpdatePendingIntent );
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
