package com.iLoong.launcher.update;


import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.widget.Toast;

import com.iLoong.RR;


public class UpdateTask
{
	
	private Context mContext;
	private Timer timerUpdate;
	private TimerTask taskUpdate;
	private Handler mHandler = new Handler();
	private XmlTask xmlTask;
	
	public UpdateTask(
			Context context )
	{
		mContext = context;
	}
	
	public void checkUpdate(
			int type )
	{
		xmlTask = new XmlTask( type );
		xmlTask.execute();
	}
	
	/**
	 * 判断是否联网
	 */
	public boolean IsHaveInternet(
			final Context context )
	{
		try
		{
			ConnectivityManager manger = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo info = manger.getActiveNetworkInfo();
			return( info != null && info.isConnected() );
		}
		catch( Exception e )
		{
			return false;
		}
	}
	
	public void startTask()
	{
		if( timerUpdate == null )
		{
			timerUpdate = new Timer( true );
		}
		if( taskUpdate == null )
		{
			taskUpdate = new TimerTask() {
				
				@Override
				public void run()
				{
					mHandler.post( new Runnable() {
						
						@Override
						public void run()
						{
							judgeUpdate();
						}
					} );
				}
			};
			timerUpdate.schedule( taskUpdate , 1000 , 7 * 12 * 60 * 60 * 1000 );
		}
	}
	
	public void dispose()
	{
		if( timerUpdate != null )
		{
			timerUpdate.cancel();
			timerUpdate.cancel();
		}
	}
	
	// 判断是否超过1天，是否需要检查更新
	private void judgeUpdate()
	{
		Time curTime = new Time();
		curTime.setToNow(); // 取得系统时间。
		int cur_year = curTime.year;
		int cur_month = curTime.month + 1;
		int cur_day = curTime.monthDay;
		SharedPreferences sharedPrefer = PreferenceManager.getDefaultSharedPreferences( mContext );
		int record_year = sharedPrefer.getInt( "year" , 1 );
		int record_month = sharedPrefer.getInt( "month" , 1 );
		int record_day = sharedPrefer.getInt( "day" , 1 );
		if( cur_year != record_year || cur_month != record_month || cur_day != record_day )
		{
			// 下载version.xml判断更新
			if( UpdateManager.isHaveInternet( mContext ) )
			{
				checkUpdate( 0 );
			}
		}
	}
	
	private class XmlTask extends AsyncTask<Void , Void , Boolean>
	{
		
		private UpdateManager updateManager;
		private ProgressDialog progressDialog;
		private int updateType = -1;
		
		// type=0表示无更新界面type=1表示有更新界面
		public XmlTask(
				int type )
		{
			updateType = type;
			updateManager = new UpdateManager( mContext );
		}
		
		@Override
		protected void onPreExecute()
		{
			if( updateType == 1 )
			{
				progressDialog = ProgressDialog.show( mContext , "" , mContext.getResources().getString( RR.string.soft_update_checking ) , true , true );
			}
		}
		
		@Override
		protected Boolean doInBackground(
				Void ... params )
		{
			try
			{
				if( updateManager.isUpdate() )
				{
					return Boolean.TRUE;
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
			return Boolean.FALSE;
		}
		
		@Override
		protected void onCancelled()
		{
		}
		
		@Override
		protected void onPostExecute(
				Boolean result )
		{
			if( updateType == 0 )
			{
				if( result )
				{
					addNotificaction();
				}
			}
			else if( updateType == 1 )
			{
				progressDialog.dismiss();
				progressDialog = null;
				if( result )
				{
					updateManager.showNoticeDialog( false );
				}
				else
				{
					Toast.makeText( mContext , RR.string.noneed_update , Toast.LENGTH_SHORT ).show();
				}
			}
		}
	}
	
	/**
	 * 
	 * 推送状态栏
	 */
	private void addNotificaction()
	{
		NotificationManager manager = (NotificationManager)mContext.getSystemService( Context.NOTIFICATION_SERVICE );
		// 创建一个Notification
		Notification notification = new Notification();
		// 设置显示在手机最上边的状态栏的图标
		notification.icon = RR.drawable.ic_launcher;
		// 当当前的notification被放到状态栏上的时候，提示内容
		notification.tickerText = mContext.getResources().getString( RR.string.soft_need_update );
		// 添加声音提示
		notification.defaults = Notification.DEFAULT_SOUND;
		// audioStreamType的值必须AudioManager中的值，代表着响铃的模式
		notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;
		Intent intent = new Intent( mContext , UpdateActivity.class );
		intent.putExtra( UpdateManager.EXTRA_PARAM , UpdateManager.EXTRA_VALUE_SERVICE );
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent pendingIntent = PendingIntent.getActivity( mContext , 0 , intent , 0 );
		// 点击状态栏的图标出现的提示信息设置
		notification.setLatestEventInfo( mContext , mContext.getString( RR.string.app_name ) , mContext.getString( RR.string.soft_check_update ) , pendingIntent );
		manager.notify( 1 , notification );
	}
}
