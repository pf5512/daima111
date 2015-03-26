package com.coco.theme.themebox.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;

import com.coco.theme.themebox.UIStaticsReceiver;
import com.iLoong.base.themebox.R;


public class DownloadEngineApkService extends Service
{
	
	private final String LOG_TAG = "ApkDownload";
	private Context mContext;
	private Object syncObject = new Object();
	private static List<DownloadNode> downApkList = new ArrayList<DownloadNode>();
	private static DownloadApkThread downApkThread = null;
	private final int content_id = 1001;
	private Notification notification;
	private NotificationManager manager;
	private static final int DOWNLOAD = 1;
	private static final int DOWNLOAD_FINISH = 2;
	private static final int DOWNLOAD_ALL_FINISH = 3;
	private static final int DOWNLOAD_STOP = 4;
	private Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(
				Message msg )
		{
			// TODO Auto-generated method stub
			switch( msg.what )
			{
			// 正在下载
				case DOWNLOAD:
					// 设置进度条位置
					RemoteViews contentView = notification.contentView;
					contentView.setTextViewText( R.id.notificationPercent , msg.arg1 + "%" );
					contentView.setProgressBar( R.id.notificationProgress , 100 , msg.arg1 , false );
					if( manager == null )
						manager = (NotificationManager)mContext.getSystemService( Context.NOTIFICATION_SERVICE );
					manager.notify( content_id , notification );
					break;
				case DOWNLOAD_FINISH:
					// 安装文件
					manager.cancel( content_id );
					Tools.installApk( mContext , (String)msg.obj );
					break;
				case DOWNLOAD_ALL_FINISH:
					manager.cancel( content_id );
					stopSelf();
					break;
				case DOWNLOAD_STOP:
					manager.cancel( content_id );
					stopSelf();
					break;
				default:
					break;
			}
		}
	};
	private boolean cancelUpdate = false;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mContext = this;
	}
	
	@Override
	public IBinder onBind(
			Intent intent )
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onStart(
			Intent intent ,
			int startId )
	{
		// TODO Auto-generated method stub
		super.onStart( intent , startId );
		if( intent == null )
		{
			return;
		}
		if( intent.getStringExtra( "stop" ) != null )
		{
			cancelUpdate = true;
			mHandler.sendEmptyMessage( DOWNLOAD_STOP );
			return;
		}
		String pkg = intent.getStringExtra( "packagename" );
		String url = intent.getStringExtra( "url" );
		notifyManager();
		downloadApk( pkg , url );
	}
	
	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		if( downApkThread != null )
		{
			downApkThread.stopRun();
			downApkThread = null;
		}
	}
	
	private boolean isAllowDownload(
			Context cxt )
	{
		if( !Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) )
		{
			return false;
		}
		return true;
	}
	
	private void notifyManager()
	{
		if( notification == null )
		{
			notification = new Notification( android.R.drawable.stat_sys_download , getString( R.string.notify_add_download , "" ) , System.currentTimeMillis() );
		}
		else
		{
			notification.icon = android.R.drawable.stat_sys_download;
			notification.tickerText = getString( R.string.notify_add_download , "" );
			notification.when = System.currentTimeMillis();
		}
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		RemoteViews contentView = new RemoteViews( mContext.getPackageName() , R.layout.download_notification );
		if( downApkThread == null )
		{
			contentView.setTextViewText( R.id.notificationTitle , mContext.getResources().getString( R.string.notify_downloading ) );
			contentView.setTextViewText( R.id.notificationPercent , "0%" );
			contentView.setProgressBar( R.id.notificationProgress , 100 , 0 , true );
		}
		contentView.setViewVisibility( R.id.cancel , View.VISIBLE );
		notification.contentView = contentView;
		Intent it = new Intent( mContext , UIStaticsReceiver.class );
		it.setAction( "com.coco.engine.stop.download" );
		PendingIntent pendingIntent = PendingIntent.getBroadcast( mContext , 0 , it , 0 );
		notification.contentView.setOnClickPendingIntent( R.id.cancel , pendingIntent );
		Intent intent = new Intent();
		PendingIntent contentIntent = PendingIntent.getActivity( mContext , (int)System.currentTimeMillis() , intent , PendingIntent.FLAG_UPDATE_CURRENT );
		notification.contentIntent = contentIntent;
		if( manager == null )
			manager = (NotificationManager)mContext.getSystemService( Context.NOTIFICATION_SERVICE );
		manager.notify( content_id , notification );
	}
	
	private static boolean findApkDownData(
			String pkgName ,
			String url )
	{
		for( int i = 0 ; i < downApkList.size() ; i++ )
		{
			DownloadNode node = downApkList.get( i );
			if( node.packname.equals( pkgName ) && node.url.equals( url ) )
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean isDownload(
			String pkgname ,
			String url )
	{
		if( findApkDownData( pkgname , url ) )
		{
			return true;
		}
		if( downApkThread != null && downApkThread.isPackage( pkgname , url ) )
		{
			return true;
		}
		return false;
	}
	
	private void downloadApk(
			String pkgName ,
			String url )
	{
		Log.v( LOG_TAG , "downloadApk=" + pkgName );
		if( !isAllowDownload( mContext ) )
		{
			return;
		}
		synchronized( this.syncObject )
		{
			if( findApkDownData( pkgName , url ) )
			{
				return;
			}
			if( downApkThread != null && downApkThread.isPackage( pkgName , url ) )
			{
				return;
			}
			downApkList.add( new DownloadNode( pkgName , url ) );
			if( downApkThread == null )
			{
				downApkThread = new DownloadApkThread();
				downApkThread.start();
			}
		}
	}
	
	private class DownloadApkThread extends Thread
	{
		
		private volatile DownloadNode curDownApk;
		private volatile HttpURLConnection urlConn;
		
		public void stopRun()
		{
			if( urlConn != null )
			{
				new Thread(new Runnable() {
					
					@Override
					public void run()
					{
						urlConn.disconnect();
					}
				}).start();
				
			}
		}
		
		public boolean isPackage(
				String pkgName ,
				String url )
		{
			DownloadNode node = curDownApk;
			if( node != null && node.packname.equals( pkgName ) && node.url.equals( url ) )
			{
				return true;
			}
			return false;
		}
		
		@Override
		public void run()
		{
			while( true )
			{
				synchronized( syncObject )
				{
					if( downApkList.size() == 0 )
					{
						break;
					}
					curDownApk = downApkList.get( 0 );
					downApkList.remove( 0 );
					cancelUpdate = false;
				}
				String mSavePath = com.coco.theme.themebox.util.PathTool.getDownloadDir();
				HttpURLConnection conn = null;
				InputStream is = null;
				FileOutputStream fos = null;
				try
				{
					URL url = new URL( curDownApk.url );
					// 创建连接
					conn = (HttpURLConnection)url.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					is = conn.getInputStream();
					File file = new File( mSavePath );
					// 判断文件目录是否存在
					if( !file.exists() )
					{
						file.mkdir();
					}
					File apkFile = new File( mSavePath , curDownApk.packname + ".apk" );
					fos = new FileOutputStream( apkFile );
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					long sizeChangeTimeMillis = 0;
					int progress = 0;
					// 写入到文件中
					do
					{
						int numread = is.read( buf );
						count += numread;
						// 计算进度条位置
						progress = (int)( ( (float)count / length ) * 100 );
						// 更新进度
						if( numread <= 0 )
						{
							// 下载完成
							Message msg = new Message();
							msg.what = DOWNLOAD_FINISH;
							msg.obj = curDownApk.packname;
							mHandler.sendMessage( msg );
							break;
						}
						else
						{
							long currentTimeMillis = System.currentTimeMillis();
							if( currentTimeMillis - sizeChangeTimeMillis < 0 || currentTimeMillis - sizeChangeTimeMillis > 1000 )
							{
								Message msg = new Message();
								msg.what = DOWNLOAD;
								msg.arg1 = progress;
								mHandler.sendMessage( msg );
								sizeChangeTimeMillis = currentTimeMillis;
							}
						}
						// 写入文件
						fos.write( buf , 0 , numread );
					}
					while( !cancelUpdate );// 点击取消就停止下载.
				}
				catch( MalformedURLException e )
				{
					Message msg = new Message();
					msg.what = DOWNLOAD_STOP;
					mHandler.sendMessage( msg );
					e.printStackTrace();
				}
				catch( IOException e )
				{
					Message msg = new Message();
					msg.what = DOWNLOAD_STOP;
					mHandler.sendMessage( msg );
					e.printStackTrace();
				}
				finally
				{
					if( fos != null )
					{
						try
						{
							fos.close();
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
					}
					if( is != null )
					{
						try
						{
							is.close();
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
					}
					if( conn != null )
					{
						conn.disconnect();
					}
				}
			}
			synchronized( syncObject )
			{
				downApkThread = null;
				if( mHandler != null )
					mHandler.sendEmptyMessage( DOWNLOAD_ALL_FINISH );
			}
		}
	}
	
	class DownloadNode
	{
		
		String url;
		String packname;
		
		public DownloadNode(
				String pkg ,
				String url )
		{
			this.packname = pkg;
			this.url = url;
		}
	}
}
