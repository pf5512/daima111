package com.iLoong.theme.adapter;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.HashMap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.Log;


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
	private int iconResourceId = RR.drawable.cocologo;
	private HashMap<String , String> downloadList = new HashMap<String , String>();
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		//		if (!com.coco.theme.themebox.StaticClass.set_directory_path.equals("")) {
		//			DOWNLOAD_PATH = com.coco.theme.themebox.StaticClass.set_directory_path + "/Coco/download/";
		//		}
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
				iconResourceId = RR.drawable.coco_launcher;
			}
			else if( logo.equals( "cocolock" ) )
			{
				iconResourceId = RR.drawable.cocologo;
			}
		}
		else
		{
			iconResourceId = RR.drawable.cocologo;
		}
		if( downloadUrl != null && !"".equals( downloadUrl.trim() ) && downloadFileName != null && !"".equals( downloadFileName.trim() ) )
		{
			if( downloadList.containsKey( downloadUrl ) )
			{
				Toast.makeText( getApplicationContext() , downloadFileName + getResources().getString( RR.string.is_downloading ) , Toast.LENGTH_SHORT ).show();
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
							getResources().getString( RR.string.server_download_finish ) ,
							downCompletedIntent );
					finishNotification.flags |= Notification.FLAG_AUTO_CANCEL;
					mUpdateNotificationManager.notify( downloadRunnable.mNotifyId , finishNotification );
					// xiatian add start //fix bug same as 0001880
					// Log.d("InstallAPKReceiver" ,
					// "sendBroadcast - DownloadAPKComplete - mNotifyId:" +
					// downloadRunnable.mNotifyId);
					Intent intentDownloadComplete = new Intent( "com.iLoong.launcher.DownloadAPKComplete" );
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
							getResources().getString( RR.string.server_download_fail ) ,
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
			String tickerText = getResources().getString( RR.string.server_begin_download );
			mUpdateNotification = new Notification( iconResourceId , tickerText , System.currentTimeMillis() );
			mUpdateNotification.setLatestEventInfo( DownloadLockBoxService.this , downloadFileName , "0%" , mUpdatePendingIntent );
			// xiatian add start //fix bug same as 0001880
			// Log.d("InstallAPKReceiver" , "sendBroadcast - ToDownloadAPK");
			Intent intentToDownload = new Intent( "com.iLoong.launcher.ToDownloadAPK" );
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
		
		public int getProxyPort(
				Context context )
		{
			int res = Proxy.getPort( context );
			if( res == -1 )
				res = Proxy.getDefaultPort();
			return res;
		}
		
		public boolean isCWWAPConnect(
				Context context )
		{
			boolean result = false;
			ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if( info != null && info.getType() == ConnectivityManager.TYPE_MOBILE )
			{
				if( ( Proxy.getDefaultHost() != null || Proxy.getHost( context ) != null ) && ( Proxy.getPort( context ) != -1 || Proxy.getDefaultPort() != -1 ) )
				{
					result = true;
				}
			}
			return result;
		}
		
		public int getNetWorkType(
				Context context )
		{
			int netType = -1;
			TelephonyManager manager = (TelephonyManager)context.getSystemService( Context.TELEPHONY_SERVICE );
			String simOperator = manager.getSimOperator();
			if( simOperator != null )
			{
				if( simOperator.startsWith( "46000" ) || simOperator.startsWith( "46002" ) )
				{
					netType = DownloadLockBoxService.NETTYPE_MOBILE;
				}
				else if( simOperator.startsWith( "46001" ) )
				{
					netType = DownloadLockBoxService.NETTYPE_UNICOM;
				}
				else if( isTelecomOperator( simOperator ) )
				{
					netType = DownloadLockBoxService.NETTYPE_TELECOM;
				}
			}
			return netType;
		}
		
		// 由于“46003”这个字符串被杀毒软件avast! mobile security当作木马病毒处理，
		// 因此将字符串的判断拆分开来	PS：很无语.......
		private boolean isTelecomOperator(
				String simOperator )
		{
			boolean ret = false;
			String tem = simOperator;
			if( tem.startsWith( "4600" ) )
			{
				if( tem.startsWith( "3" , 4 ) )
				{
					ret = true;
				}
			}
			return ret;
		}
		
		public String getProxyHost(
				Context context )
		{
			String res = Proxy.getHost( context );
			if( res == null )
				res = Proxy.getDefaultHost();
			return res;
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
				if( isCWWAPConnect( getApplicationContext() ) && getNetWorkType( getApplicationContext() ) != NETTYPE_UNICOM )
				{
					String host = getProxyHost( getApplicationContext() );
					int port = getProxyPort( getApplicationContext() );
					java.net.Proxy proxy = new java.net.Proxy( java.net.Proxy.Type.HTTP , new InetSocketAddress( host , port ) );
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
				String loading = getResources().getString( RR.string.server_downloading );
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
