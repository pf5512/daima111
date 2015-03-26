package com.coco.theme.themebox;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
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
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.database.service.HotService;
import com.coco.theme.themebox.util.DownApkNode;
import com.coco.theme.themebox.util.DownType;
import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.PathTool;
import com.iLoong.base.themebox.R;


public class DownloadApkContentService extends Service
{
	
	private final String LOG_TAG = "ApkDownload";
	private Context mContext;
	private Object syncObject = new Object();
	private List<DownApkNode> downApkList = new ArrayList<DownApkNode>();
	private DownloadThemeService downApkDb;
	private DownloadApkThread downApkThread = null;
	private final int id = 1000;
	private Notification notification;
	private NotificationManager mNotificationManager;
	private RemoteViews contentView;
	public static final int MSG_UPDATE_PROGRESS = 0;
	public static final int MSG_DOWNLOAD_PAUSE = 1;
	public static final int MSG_SUCCESS = 2;
	public static final int MSG_START_DOWNLOAD = 3;
	public static final int MSG_CANCEL_INDICATE = 4;
	private Handler mMainHandler;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mContext = this;
		downApkDb = new DownloadThemeService( this );
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
		String pkg = intent.getStringExtra( "packageName" );
		String type = intent.getStringExtra( "type" );
		String status = intent.getStringExtra( "status" );
		String name = intent.getStringExtra( "name" );
		if( pkg != null && type != null && "download".equals( status ) )
		{
			notifyManager( name );
			downloadApk( pkg , type , name );
		}
		else if( pkg != null && type != null && "pause".equals( status ) )
		{
			stopDownApk( pkg , type );
		}
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
	
	private void notifyManager(
			String title )
	{
		if( notification == null )
			notification = new Notification( android.R.drawable.stat_sys_download , getString( R.string.notify_add_download , title ) , System.currentTimeMillis() );
		else
		{
			notification.icon = android.R.drawable.stat_sys_download;
			notification.tickerText = getString( R.string.notify_add_download , title );
			notification.when = System.currentTimeMillis();
		}
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		if( contentView == null )
			contentView = new RemoteViews( mContext.getPackageName() , R.layout.download_notification );
		notification.contentView = contentView;
		Intent intent = new Intent();
		intent.putExtra( "notifyID" , 1000 );
		PendingIntent contentIntent = PendingIntent.getActivity( mContext , (int)System.currentTimeMillis() , intent , PendingIntent.FLAG_UPDATE_CURRENT );
		notification.contentIntent = contentIntent;
		if( mNotificationManager == null )
			mNotificationManager = (NotificationManager)mContext.getSystemService( Context.NOTIFICATION_SERVICE );
		mNotificationManager.notify( id , notification );
		mMainHandler = new Handler() {
			
			@Override
			public void handleMessage(
					Message msg )
			{
				super.handleMessage( msg );
				switch( msg.what )
				{
					case MSG_UPDATE_PROGRESS:
						RemoteViews contentView = notification.contentView;
						contentView.setTextViewText( R.id.notificationTitle , mContext.getResources().getString( R.string.notify_downloading ) + msg.obj );
						contentView.setTextViewText( R.id.notificationPercent , msg.arg1 + "%" );
						contentView.setViewVisibility( R.id.notificationPercent , View.VISIBLE );
						contentView.setProgressBar( R.id.notificationProgress , 100 , msg.arg1 , false );
						contentView.setViewVisibility( R.id.notificationProgress , View.VISIBLE );
						mNotificationManager.notify( msg.arg2 , notification );
						break;
					case MSG_SUCCESS:
						notification.icon = R.drawable.download;
						notification.flags = 0;
						notification.flags |= Notification.FLAG_AUTO_CANCEL;
						RemoteViews contentView1 = notification.contentView;
						contentView1.setTextViewText( R.id.notificationPercent , "100%" );
						contentView1.setViewVisibility( R.id.notificationProgress , View.INVISIBLE );
						mNotificationManager.notify( msg.arg2 , notification );
						break;
					case MSG_DOWNLOAD_PAUSE:
						Toast.makeText( mContext , getString( R.string.notify_pause_download , msg.obj ) , Toast.LENGTH_SHORT ).show();
						break;
					case MSG_START_DOWNLOAD:
						RemoteViews contentView4 = notification.contentView;
						contentView4.setTextViewText( R.id.notificationTitle , mContext.getResources().getString( R.string.notify_downloading ) + msg.obj );
						contentView4.setViewVisibility( R.id.notificationPercent , View.INVISIBLE );
						contentView4.setProgressBar( R.id.notificationProgress , 100 , 0 , true );
						contentView4.setViewVisibility( R.id.notificationProgress , View.VISIBLE );
						mNotificationManager.notify( msg.arg2 , notification );
						break;
					case MSG_CANCEL_INDICATE:
						mNotificationManager.cancel( id );
						break;
				}
			}
		};
	}
	
	private boolean findApkDownData(
			String pkgName ,
			DownType type ,
			String tabtype )
	{
		for( int i = 0 ; i < downApkList.size() ; i++ )
		{
			DownApkNode node = downApkList.get( i );
			if( node.packname.equals( pkgName ) && node.downType == type && node.tabType.equals( tabtype ) )
			{
				return true;
			}
		}
		return false;
	}
	
	public void downloadApk(
			String pkgName ,
			String type ,
			String name )
	{
		Log.v( LOG_TAG , "downloadApk=" + pkgName );
		if( !isAllowDownload( mContext ) )
		{
			return;
		}
		synchronized( this.syncObject )
		{
			if( findApkDownData( pkgName , DownType.TYPE_APK_DOWNLOAD , type ) )
			{
				return;
			}
			if( downApkThread != null && downApkThread.isPackage( pkgName , type ) )
			{
				return;
			}
			downApkList.add( new DownApkNode( pkgName , DownType.TYPE_APK_DOWNLOAD , type , name ) );
			downloadApkStatusUpdate( pkgName , DownloadStatus.StatusDownloading , type );
			if( downApkThread == null )
			{
				downApkThread = new DownloadApkThread();
				downApkThread.start();
			}
		}
	}
	
	public void stopDownApk(
			String pkgName ,
			String type )
	{
		Log.v( LOG_TAG , "stopDownApk=" + pkgName );
		synchronized( this.syncObject )
		{
			for( int i = downApkList.size() - 1 ; i >= 0 ; i-- )
			{
				DownApkNode node = downApkList.get( i );
				if( node.packname.equals( pkgName ) && node.downType == DownType.TYPE_APK_DOWNLOAD && node.tabType.equals( type ) )
				{
					Log.v( LOG_TAG , "remove array" );
					downApkList.remove( i );
				}
			}
			if( downApkThread != null )
			{
				Log.v( LOG_TAG , "stop apk thread" );
				downApkThread.stopApk( pkgName , type );
			}
			downloadApkStatusUpdate( pkgName , DownloadStatus.StatusPause , type );
		}
	}
	
	private void downloadApkStatusUpdate(
			String pkgName ,
			DownloadStatus status ,
			String type )
	{
		synchronized( downApkDb )
		{
			downApkDb.updateDownloadStatus( pkgName , status , type );
		}
		Intent intent = new Intent( getActionDownloadStatusChanged( type ) );
		intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , pkgName );
		mContext.sendBroadcast( intent );
	}
	
	private String getDownloadingApp(
			String pkgName ,
			String type )
	{
		if( type.equals( DownloadList.Theme_Type ) )
		{
			return com.coco.theme.themebox.util.PathTool.getDownloadingApp( pkgName );
		}
		else if( type.equals( DownloadList.Widget_Type ) )
		{
			return com.coco.widget.widgetbox.PathTool.getDownloadingApp( pkgName );
		}
		else if( type.equals( DownloadList.Wallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getDownloadingApp( pkgName );
		}
		else if( type.equals( DownloadList.Font_Type ) )
		{
			return com.coco.font.fontbox.PathTool.getDownloadingApp( pkgName );
		}
		else if( type.equals( DownloadList.LiveWallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getDownloadingApp( pkgName );
		}
		return null;
	}
	
	private String getAppFile(
			String pkgName ,
			String type )
	{
		if( type.equals( DownloadList.Theme_Type ) )
		{
			return com.coco.theme.themebox.util.PathTool.getAppFile( pkgName );
		}
		else if( type.equals( DownloadList.Widget_Type ) )
		{
			return com.coco.widget.widgetbox.PathTool.getAppFile( pkgName );
		}
		else if( type.equals( DownloadList.Wallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getAppFile( pkgName );
		}
		else if( type.equals( DownloadList.Font_Type ) )
		{
			return com.coco.font.fontbox.PathTool.getAppFile( pkgName );
		}
		else if( type.equals( DownloadList.LiveWallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getAppLiveFile( pkgName );
		}
		return null;
	}
	
	private String getActionDownloadStatusChanged(
			String type )
	{
		if( type.equals( DownloadList.Theme_Type ) )
		{
			return com.coco.theme.themebox.StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED;
		}
		else if( type.equals( DownloadList.Widget_Type ) )
		{
			return com.coco.widget.widgetbox.StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED;
		}
		else if( type.equals( DownloadList.Wallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED;
		}
		else if( type.equals( DownloadList.Font_Type ) )
		{
			return com.coco.font.fontbox.StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED;
		}
		else if( type.equals( DownloadList.LiveWallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.StaticClass.ACTION_LIVE_DOWNLOAD_STATUS_CHANGED;
		}
		return null;
	}
	
	private String getActionDownloadSizeChanged(
			String type )
	{
		if( type.equals( DownloadList.Theme_Type ) )
		{
			return com.coco.theme.themebox.StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED;
		}
		else if( type.equals( DownloadList.Widget_Type ) )
		{
			return com.coco.widget.widgetbox.StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED;
		}
		else if( type.equals( DownloadList.Wallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED;
		}
		else if( type.equals( DownloadList.Font_Type ) )
		{
			return com.coco.font.fontbox.StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED;
		}
		else if( type.equals( DownloadList.LiveWallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.StaticClass.ACTION_LIVE_DOWNLOAD_SIZE_CHANGED;
		}
		return null;
	}
	
	private void downloadApkFinish(
			String pkgName ,
			String type )
	{
		synchronized( downApkDb )
		{
			downApkDb.updateDownloadStatus( pkgName , DownloadStatus.StatusFinish , type );
		}
		String downloading = getDownloadingApp( pkgName , type );
		String app = getAppFile( pkgName , type );
		if( downloading != null && app != null )
			PathTool.moveFile( downloading , app );
		if( type.equals( DownloadList.Wallpaper_Type ) || type.equals( DownloadList.LiveWallpaper_Type ) )
		{
			com.coco.wallpaper.wallpaperbox.PathTool.copyFile( com.coco.wallpaper.wallpaperbox.PathTool.getThumbFile( pkgName ) , com.coco.wallpaper.wallpaperbox.PathTool.getAppSmallFile( pkgName ) );
		}
		Intent intent = new Intent( getActionDownloadStatusChanged( type ) );
		intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , pkgName );
		mContext.sendBroadcast( intent );
		if( !type.equals( DownloadList.Wallpaper_Type ) && !type.equals( DownloadList.Font_Type ) )
		{
			installApk( pkgName , type );
		}
		HotService sv = new HotService( mContext );
		String resid = sv.queryResid( pkgName , type );
		if( resid != null )
		{
			DownloadList.getInstance( mContext ).startUICenterLog( DownloadList.ACTION_DOWNLOAD_LOG , sv.queryResid( pkgName , type ) , pkgName );
		}
	}
	
	public void installApk(
			String pkgName ,
			String type )
	{
		String filepath = getAppFile( pkgName , type );
		File file = new File( filepath );
		Log.v( "OpenFile" , file.getName() );
		Intent intent = new Intent();
		intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent.setAction( android.content.Intent.ACTION_VIEW );
		intent.setDataAndType( Uri.fromFile( file ) , "application/vnd.android.package-archive" );
		mContext.startActivity( intent );
	}
	
	private class DownloadApkThread extends Thread
	{
		
		private volatile DownApkNode curDownApk;
		private volatile HttpURLConnection urlConn;
		private volatile boolean isExit = false;
		private HotService hotServer = new HotService( mContext );
		private DownloadThemeService threadDb = new DownloadThemeService( mContext );
		
		private String getUrlAddress(
				String packageName ,
				String type )
		{
			return hotServer.queryResurlAddress( packageName , type );
		}
		
		public void stopRun()
		{
			isExit = true;
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
			DownApkNode dNode = curDownApk;
			if( dNode != null )
			{
				threadDb.updateDownloadStatus( dNode.packname , DownloadStatus.StatusPause , dNode.tabType );
			}
		}
		
		public void stopApk(
				String pkgName ,
				String type )
		{
			DownApkNode dNode = curDownApk;
			if( dNode != null && pkgName.equals( dNode.packname ) && dNode.tabType.equals( type ) )
			{
				stopRun();// 停止当前的
				isExit = false;// 启动之后的
			}
			else
			{
			}
		}
		
		public boolean isPackage(
				String pkgName ,
				String type )
		{
			DownApkNode node = curDownApk;
			if( node != null && node.packname.equals( pkgName ) && node.tabType.equals( type ) )
			{
				return true;
			}
			return false;
		}
		
		private long sizeChangeTimeMillis = 0;
		
		private void downloadApkContinue(
				String pkgName ,
				int curSize ,
				int totalSize ,
				String type )
		{
			threadDb.updateDownloadSizeAndStatus( pkgName , curSize , totalSize , DownloadStatus.StatusDownloading , type );
			long currentTimeMillis = System.currentTimeMillis();
			if( currentTimeMillis - sizeChangeTimeMillis > 0 && currentTimeMillis - sizeChangeTimeMillis < 1000 )
			{
				return;
			}
			sizeChangeTimeMillis = currentTimeMillis;
			Intent intent = new Intent( getActionDownloadSizeChanged( type ) );
			intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , pkgName );
			intent.putExtra( StaticClass.EXTRA_DOWNLOAD_SIZE , curSize );
			intent.putExtra( StaticClass.EXTRA_TOTAL_SIZE , totalSize );
			mContext.sendBroadcast( intent );
			if( mMainHandler != null )
			{
				mMainHandler.sendMessage( Message.obtain( mMainHandler , MSG_UPDATE_PROGRESS , curSize * 100 / totalSize , id , curDownApk.apkName ) );
			}
		}
		
		private Handler mHandler = new Handler() {
			
			@Override
			public void handleMessage(
					Message msg )
			{
				// TODO Auto-generated method stub
				switch( msg.what )
				{
					case 0:
						Toast.makeText( mContext , mContext.getString( R.string.reLoadApk ) , Toast.LENGTH_SHORT ).show();
						break;
					default:
						break;
				}
				super.handleMessage( msg );
			}
		};
		
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
				}
				if( mMainHandler != null )
					mMainHandler.sendMessage( Message.obtain( mMainHandler , MSG_START_DOWNLOAD , 0 , id , curDownApk.apkName ) );
				RandomAccessFile fileOut = null;
				File file = null;
				InputStream netStream = null;
				boolean isSucceed = false;
				try
				{
					downloadApkStatusUpdate( curDownApk.packname , DownloadStatus.StatusDownloading , curDownApk.tabType );
					String sdpath = getDownloadingApp( curDownApk.packname , curDownApk.tabType );
					file = new File( sdpath );
					URL url = new URL( getUrlAddress( curDownApk.packname , curDownApk.tabType ) );
					urlConn = (HttpURLConnection)url.openConnection();
					if( isExit )
					{
						break;
					}
					DownloadThemeItem item = threadDb.queryByPackageName( curDownApk.packname , curDownApk.tabType );
					int curSize = 0;
					if( item == null )
					{
						curSize = 0;
					}
					else
						curSize = (int)item.getDownloadSize();
					if( curSize > 0 )
					{
						if( file.exists() && file.length() != curSize )
						{
							file.delete();
							curSize = 0;
							mHandler.sendEmptyMessage( 0 );
						}
						else if( !file.exists() )
						{
							curSize = 0;
							mHandler.sendEmptyMessage( 0 );
						}
						fileOut = new RandomAccessFile( sdpath , "rw" );
						fileOut.seek( curSize );
						String ranges = String.format( "bytes=%d-" , curSize );
						urlConn.addRequestProperty( "RANGE" , ranges );
					}
					else
					{
						if( file.exists() )
							file.delete();
						fileOut = new RandomAccessFile( sdpath , "rw" );
					}
					urlConn.connect();
					// 获取文件大小
					// int length = conn.getContentLength();
					int totalLength = urlConn.getContentLength();
					if( curSize > 0 )
					{
						totalLength = (int)item.getApplicationSize();
					}
					if( curSize == totalLength )
					{
						isSucceed = true;
					}
					if( mMainHandler != null )
					{
						mMainHandler.sendMessage( Message.obtain( mMainHandler , MSG_UPDATE_PROGRESS , curSize * 100 / totalLength , id , curDownApk.apkName ) );
					}
					// 创建输入�?
					{
						netStream = urlConn.getInputStream();
						int count = 0;
						// 缓存
						byte buf[] = new byte[1024 * 10];
						// 写入到文件中
						while( true )
						{
							int numread = netStream.read( buf );
							count += numread;
							// 计算进度条位�?
							// int progress = (int) (((float) count / length) *
							// 100);
							if( numread <= 0 )
							{
								// 下载完成
								break;
							}
							// 更新进度
							downloadApkContinue( curDownApk.packname , curSize + count , totalLength , curDownApk.tabType );
							// 写入文件
							fileOut.write( buf , 0 , numread );
						}
						isSucceed = true;
					}
				}
				catch( MalformedURLException e )
				{
					e.printStackTrace();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				finally
				{
					if( fileOut != null )
					{
						try
						{
							fileOut.close();
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
						fileOut = null;
					}
					if( netStream != null )
					{
						try
						{
							netStream.close();
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
						netStream = null;
					}
					if( urlConn != null )
					{
						urlConn.disconnect();
						urlConn = null;
					}
				}
				if( isSucceed )
				{
					// downloadApkStatusUpdate(curDownApk.packname,
					// DownloadStatus.StatusFinish);
					downloadApkFinish( curDownApk.packname , curDownApk.tabType );
				}
				else
				{
					if( file.exists() )
						file.delete();
					downloadApkStatusUpdate( curDownApk.packname , DownloadStatus.StatusPause , curDownApk.tabType );
					if( mMainHandler != null )
						mMainHandler.sendMessage( Message.obtain( mMainHandler , MSG_DOWNLOAD_PAUSE , 0 , id , curDownApk.apkName ) );
				}
			}
			synchronized( syncObject )
			{
				downApkThread = null;
				if( mMainHandler != null )
					mMainHandler.sendMessage( Message.obtain( mMainHandler , MSG_CANCEL_INDICATE , 0 , id , null ) );
				stopSelf();
			}
		}
	}
}
