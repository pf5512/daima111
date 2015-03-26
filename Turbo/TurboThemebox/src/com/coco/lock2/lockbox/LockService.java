package com.coco.lock2.lockbox;


import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.coco.lock2.lockbox.util.LockManager;
import com.coco.theme.themebox.MainActivity;
import com.coco.theme.themebox.util.Tools;


public class LockService extends Service
{
	
	private KeyguardManager mKeyguardManager;
	private KeyguardLock mKeyguardLock;
	private SharedPreferences sharedPrefer;
	
	@Override
	public IBinder onBind(
			Intent arg0 )
	{
		return null;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		// 吕江滨 安卓4.0不支持此接口
		// setForeground(true);
		sharedPrefer = PreferenceManager.getDefaultSharedPreferences( this );
		if( !isPreferencesLockScreen() && !isApplicationRunning( this ) )
		{
			stopSelf();
			if( !Tools.isServiceRunning( this , "com.coco.theme.themebox.DownloadApkContentService" ) && !Tools.isServiceRunning( this , "com.coco.theme.themebox.update.UpdateService" ) )
				System.exit( 0 );
		}
		sharedPrefer.registerOnSharedPreferenceChangeListener( mPreferListener );
		// // 添加统计
		// StatisticsBase.setApplicationContext(this.getApplicationContext());
		// StatisticsBase.setLogSenderDelayed(60);
		// StatisticsBase.loadAppChannel(this);
		// StatisticsExpand.Start(this, MainActivity.class);
		// if(sharedPrefer.getBoolean("Activate", false)==false){
		// SharedPreferences.Editor editor = sharedPrefer.edit();
		// StatisticsExpand.Activate(this);
		// editor.putBoolean("Activate", true);
		// editor.commit();
		// }
		mKeyguardManager = (KeyguardManager)this.getApplicationContext().getSystemService( Context.KEYGUARD_SERVICE );
		mKeyguardLock = mKeyguardManager.newKeyguardLock( "lock" );
		Log.v( "service" , "create1" );
		if( isPreferencesLockScreen() )
		{
			if( isenableCooeeLock() )
				enableCooeeLock();
			Log.v( "service" , "create2" );
		}
		// 吕江滨 安卓4.0不支持此接口
		// setForeground(true);
	}
	
	private boolean isApplicationRunning(
			Context ctx )
	{
		ActivityManager activityManager = (ActivityManager)ctx.getSystemService( Context.ACTIVITY_SERVICE );
		List<RunningTaskInfo> appTask = activityManager.getRunningTasks( 30 );
		boolean isRunning = false;
		for( RunningTaskInfo task : appTask )
		{
			if( task.topActivity.getPackageName().equals( ctx.getPackageName() ) )
			{
				isRunning = true;
			}
		}
		return isRunning;
	}
	
	private boolean isenableCooeeLock()
	{// 判断锁屏是否存在
		LockManager mgr = new LockManager( this );
		return mgr.isenableCooeeLock();
	}
	
	@Override
	public int onStartCommand(
			Intent intent ,
			int flags ,
			int startId )
	{
		Log.v( "service" , "onStartCommand1" );
		if( intent != null && StaticClass.ACTION_KILL_SYSLOCK.equals( intent.getAction() ) )
		{
			if( isPreferencesLockScreen() )
			{
				Log.v( "service" , "onStartCommand2" );
				disableCooeeLock();
				if( isenableCooeeLock() )
				{
					enableCooeeLock();
				}
			}
			Notification localNotification = new Notification( 0 , "CoCoLocker Start" , System.currentTimeMillis() );
			localNotification.setLatestEventInfo( this , "CoCoLocker " , "CoCoLocker Start" , PendingIntent.getActivity( this , 0 , new Intent( this , MainActivity.class ) , 0 ) );
			startForeground( 20130122 , localNotification );
		}
		// boolean open = intent.getBooleanExtra("lockenable", true);
		//
		// editor.putBoolean(StaticClass.ENABLE_LOCK, open);
		// editor.commit();
		//
		// if(sharedPrefer.getBoolean(StaticClass.ENABLE_LOCK, true)){
		// Log.v("service","onStartCommand1 = true");
		// enableCooeeLock();
		// }
		// else{
		// Log.v("service","onStartCommand1 = false");
		// disableCooeeLock();
		// }
		return START_STICKY;
	}
	
	@Override
	public void onDestroy()
	{
		stopForeground( true );
		Log.v( "service" , "onDestroy" );
		sharedPrefer.unregisterOnSharedPreferenceChangeListener( mPreferListener );
		if( isPreferencesLockScreen() )
		{
			disableCooeeLock();
			Intent localIntent = new Intent();
			localIntent.setClass( this , LockService.class ); // 销毁时重新启动Service
			this.startService( localIntent );
		}
		super.onDestroy();
	}
	
	private boolean isPreferencesLockScreen()
	{
		return sharedPrefer.getBoolean( StaticClass.ENABLE_LOCK , true );
	}
	
	private boolean isSimReady()
	{
		TelephonyManager manager = (TelephonyManager)getSystemService( TELEPHONY_SERVICE );
		switch( manager.getSimState() )
		{
			case TelephonyManager.SIM_STATE_PIN_REQUIRED:
			case TelephonyManager.SIM_STATE_PUK_REQUIRED:
				return false;
			default:
				return true;
		}
	}
	
	/*
	 * 启用锁定
	 */
	private void enableCooeeLock()
	{
		Log.v( "service" , "enableCooeeLock1" );
		if( isSimReady() )
		{
			mKeyguardLock.disableKeyguard();
			Log.v( "service" , "enableCooeeLock2" );
		}
		IntentFilter screenFilter = new IntentFilter();
		screenFilter.addAction( Intent.ACTION_SCREEN_OFF );
		// screenFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver( mServiceReceiver , screenFilter );
		IntentFilter disableFilter = new IntentFilter();
		disableFilter.addAction( StaticClass.ACTION_DISABLE_SYSLOCK );
		registerReceiver( mServiceReceiver , disableFilter );
	}
	
	/*
	 * 取消锁定
	 */
	private void disableCooeeLock()
	{
		Log.v( "service" , "disableCooeeLock1" );
		if( isSimReady() )
		{
			mKeyguardLock.reenableKeyguard();
			Log.v( "service" , "disableCooeeLock2" );
		}
		try
		{
			unregisterReceiver( mServiceReceiver );
		}
		catch( Exception e )
		{// 没有register
		}
	}
	
	private BroadcastReceiver mServiceReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(
				Context context ,
				Intent intent )
		{
			String actionName = intent.getAction();
			if( actionName.equals( Intent.ACTION_SCREEN_OFF ) )
			{
				Log.v( "service" , "BroadcastReceiver --off" );
				callLock();
			}
			else if( actionName.equals( StaticClass.ACTION_DISABLE_SYSLOCK ) )
			{
			}
		}
	};
	
	private void callLock()
	{
		Log.v( "service" , "callLock1" );
		if( !isPreferencesLockScreen() )
		{
			Log.v( "service" , "callLock2" );
			return;
		}
		TelephonyManager tm = (TelephonyManager)getSystemService( Context.TELEPHONY_SERVICE );
		int state = tm.getCallState();
		if( state == TelephonyManager.CALL_STATE_OFFHOOK || state == TelephonyManager.CALL_STATE_RINGING )
		{
			Log.v( "service" , "callLock3" );
			return;
		}
		Log.v( "service" , "callLock4" );
		startLockActivity();
	}
	
	private void startLockActivity()
	{
		Log.v( "service" , "startLockActivity1" );
		LockManager mgr = new LockManager( this );
		ComponentName comName = mgr.queryCurrentLock();
		// //添加唤起统计
		// StatisticsExpand.Call(LockService.this,
		// comName.getPackageName(),comName.getClassName());
		// Log.v("Statistics","call packname ="+comName.getClassName());
		Intent intentActivity = new Intent();
		intentActivity.setComponent( comName );
		intentActivity.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS );
		Log.v( "service" , "startLockActivity comName = " + comName );
		ResolveInfo info = getPackageManager().resolveActivity( intentActivity , 0 );
		if( info != null )
		{
			Log.v( "service" , "startLockActivity2" );
			startActivity( intentActivity );
		}
	}
	
	private OnSharedPreferenceChangeListener mPreferListener = new OnSharedPreferenceChangeListener() {
		
		// sharedPreferences:句柄
		// key: 改变 键值
		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences ,
				String key )
		{
			if( key.equals( StaticClass.ENABLE_LOCK ) )
			{
				Boolean lockScreen = sharedPreferences.getBoolean( StaticClass.ENABLE_LOCK , false );
				if( lockScreen )
				{
					Log.v( "service" , "mPreferListener1" );
					if( isenableCooeeLock() )
						enableCooeeLock();
				}
				else
				{
					Log.v( "service" , "mPreferListener2" );
					disableCooeeLock();
				}
			}
		}
	};
}
