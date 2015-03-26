package com.coco.theme.themebox.update;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Dialog;
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

import com.coco.theme.themebox.UIStaticsReceiver;
import com.coco.theme.themebox.util.Log;
import com.iLoong.base.themebox.R;


public class UpdateService extends Service
{
	
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	private static final int TOAST_MESSAGE = 3;
	private String urlString = null;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;
	private DownloadApkThread downApk;
	private Context mContext;
	private Dialog mDownloadDialog;
	private final int id = 1000000;
	private Notification notification;
	private NotificationManager manager;
	private Handler mHandler = new Handler() {
		
		public void handleMessage(
				Message msg )
		{
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
					manager.notify( id , notification );
					break;
				case DOWNLOAD_FINISH:
					// 安装文件
					manager.cancel( id );
					installApk();
					stopSelf();
					break;
				case TOAST_MESSAGE:
					if( msg.obj != null )
						Toast.makeText( mContext , (String)msg.obj , Toast.LENGTH_SHORT ).show();
					break;
				default:
					break;
			}
		};
	};
	
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
		urlString = intent.getStringExtra( "url" );
		if( urlString != null )
			showDownloadNotification();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		cancelUpdate = true;
		manager.cancel( id );
		Log.v( "updateservice" , "service ondestroy" );
	}
	
	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadNotification()
	{
		// 构造软件下载对话框
		if( notification == null )
			notification = new Notification( android.R.drawable.stat_sys_download , mContext.getString( R.string.soft_updating ) , System.currentTimeMillis() );
		else
		{
			notification.icon = android.R.drawable.stat_sys_download;
			notification.tickerText = mContext.getString( R.string.soft_updating );
			notification.when = System.currentTimeMillis();
		}
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		RemoteViews contentView = new RemoteViews( mContext.getPackageName() , R.layout.download_notification );
		notification.contentView = contentView;
		contentView.setTextViewText( R.id.notificationTitle , mContext.getResources().getString( R.string.notify_downloading ) + mContext.getString( R.string.theme_box_name ) );
		contentView.setTextViewText( R.id.notificationPercent , "0%" );
		contentView.setProgressBar( R.id.notificationProgress , 100 , 0 , true );
		contentView.setViewVisibility( R.id.cancel , View.VISIBLE );
		Intent it = new Intent( mContext , UIStaticsReceiver.class );
		it.setAction( "com.coco.personalCenter.stop.update" );
		PendingIntent pendingIntent = PendingIntent.getBroadcast( mContext , 0 , it , 0 );
		contentView.setOnClickPendingIntent( R.id.cancel , pendingIntent );
		Intent intent = new Intent();
		intent.putExtra( "notifyID" , id );
		PendingIntent contentIntent = PendingIntent.getActivity( mContext , (int)System.currentTimeMillis() , intent , PendingIntent.FLAG_UPDATE_CURRENT );
		notification.contentIntent = contentIntent;
		manager = (NotificationManager)mContext.getSystemService( Context.NOTIFICATION_SERVICE );
		manager.notify( id , notification );
		// 下载文件
		downloadApk();
		// cancel(cancelUpdate = true;
		// dialogIsShow = false;)
	}
	
	/**
	 * 下载apk文件
	 */
	private void downloadApk()
	{
		// 启动新线程下载软件
		downApk = new DownloadApkThread();
		downApk.start();
	}
	
	private class DownloadApkThread extends Thread
	{
		
		@Override
		public void run()
		{
			// 判断SD卡是否存在，并且是否具有读写权限
			if( !Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) )
			{
				mDownloadDialog.dismiss();
				Message msg = new Message();
				msg.obj = mContext.getString( R.string.sdcard_not_available );
				msg.what = TOAST_MESSAGE;
				mHandler.sendMessage( msg );
				return;
			}
			// 获得存储卡的路径
			mSavePath = com.coco.theme.themebox.util.PathTool.getDownloadDir();
			HttpURLConnection conn = null;
			InputStream is = null;
			FileOutputStream fos = null;
			try
			{
				URL url = new URL( urlString );
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
				File apkFile = new File( mSavePath , mContext.getPackageName() + ".apk" );
				fos = new FileOutputStream( apkFile );
				int count = 0;
				// 缓存
				byte buf[] = new byte[1024];
				long sizeChangeTimeMillis = 0;
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
						mHandler.sendEmptyMessage( DOWNLOAD_FINISH );
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
				e.printStackTrace();
				Message msg = new Message();
				msg.obj = mContext.getString( R.string.internet_unusual );
				msg.what = TOAST_MESSAGE;
				mHandler.sendMessage( msg );
				stopSelf();
			}
			catch( IOException e )
			{
				e.printStackTrace();
				Message msg = new Message();
				msg.obj = mContext.getString( R.string.internet_unusual );
				msg.what = TOAST_MESSAGE;
				mHandler.sendMessage( msg );
				stopSelf();
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
	};
	
	/**
	 * 安装APK文件
	 */
	private void installApk()
	{
		File apkfile = new File( mSavePath , mContext.getPackageName() + ".apk" );
		if( !apkfile.exists() )
		{
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent( Intent.ACTION_VIEW );
		i.setDataAndType( Uri.parse( "file://" + apkfile.toString() ) , "application/vnd.android.package-archive" );
		i.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		mContext.startActivity( i );
	}
}
