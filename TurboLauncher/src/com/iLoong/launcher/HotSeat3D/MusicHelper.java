package com.iLoong.launcher.HotSeat3D;


import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.provider.MediaStore;
import android.view.KeyEvent;

import com.iLoong.launcher.Desktop3D.Log;


public class MusicHelper
{
	
	private final String TAG = "MusicHelper";
	public static MusicHelper instance = null;
	public Context mContext;
	public boolean isPlaying = false;
	public String resolveName;
	public String TOGGLEPAUSE_ACTION = "com.android.music.musicservicecommand.togglepause";
	public String PAUSE_ACTION = "com.android.music.musicservicecommand.pause";
	public String PREVIOUS_ACTION = "com.android.music.musicservicecommand.previous";
	public String NEXT_ACTION = "com.android.music.musicservicecommand.next";
	public String META_CHANGED = "com.android.music.metachanged";
	public String QUEUE_CHANGED = "com.android.music.queuechanged";
	public String PLAY_COMPLETED = "com.android.music.playbackcomplete";
	public String PLAY_STATE_CHANGED = "com.android.music.playstatechanged";
	public static final String CMDNAME = "command";
	public static final String CMDTOGGLEPAUSE = "togglepause";
	public static final String CMDSTOP = "stop";
	public static final String CMDPAUSE = "pause";
	public static final String CMDPREVIOUS = "previous";
	public static final String CMDNEXT = "next";
	public MusicHelperReceiver musicHelperReceiver = new MusicHelperReceiver();
	public OnPlayStateChanged playStateListener;
	
	private MusicHelper(
			Context context )
	{
		this.mContext = context;
		resolveDefaultPkgName();
		startReceiver();
	}
	
	public void setOnPlayStateListerner(
			OnPlayStateChanged playStateListener )
	{
		this.playStateListener = playStateListener;
	}
	
	public void startReceiver()
	{
		IntentFilter commandFilter = new IntentFilter();
		commandFilter.addAction( TOGGLEPAUSE_ACTION );
		commandFilter.addAction( PAUSE_ACTION );
		commandFilter.addAction( NEXT_ACTION );
		commandFilter.addAction( PREVIOUS_ACTION );
		commandFilter.addAction( META_CHANGED );
		commandFilter.addAction( QUEUE_CHANGED );
		commandFilter.addAction( PLAY_COMPLETED );
		commandFilter.addAction( PLAY_STATE_CHANGED );
		commandFilter.addAction( META_CHANGED );
		commandFilter.addAction( QUEUE_CHANGED );
		commandFilter.addAction( PLAY_COMPLETED );
		commandFilter.addAction( PLAY_STATE_CHANGED );
		commandFilter.addAction( resolveName + ".playstatechanged" );
		mContext.registerReceiver( musicHelperReceiver , commandFilter );
	}
	
	public void stopReceiver()
	{
		if( musicHelperReceiver != null )
		{
			mContext.unregisterReceiver( musicHelperReceiver );
		}
	}
	
	public String getResolvePackage()
	{
		return resolveName;
	}
	
	public static MusicHelper getInstance(
			Context mContext )
	{
		if( instance == null )
		{
			instance = new MusicHelper( mContext );
		}
		return instance;
	}
	
	private void resolveDefaultPkgName()
	{
		PackageManager pm = mContext.getPackageManager();
		Intent intent = new Intent( MediaStore.INTENT_ACTION_MUSIC_PLAYER );
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
		if( resolveInfos != null )
		{
			for( int i = 0 ; i < resolveInfos.size() ; i++ )
			{
				if( ( resolveInfos.get( i ).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) > 0 )
				{
					resolveName = resolveInfos.get( i ).activityInfo.packageName;
					return;
				}
			}
		}
		else
		{
			intent = new Intent( Intent.ACTION_MAIN );
			intent.addCategory( Intent.CATEGORY_APP_MUSIC );
			resolveInfos = pm.queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
			for( int i = 0 ; i < resolveInfos.size() ; i++ )
			{
				if( ( resolveInfos.get( i ).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) > 0 )
				{
					resolveName = resolveInfos.get( i ).activityInfo.packageName;
					return;
				}
			}
		}
	}
	
	public void nextMusic()
	{
		Intent intent = new Intent();
		int keyCode;
		KeyEvent keyEvent;
		keyCode = KeyEvent.KEYCODE_MEDIA_NEXT;
		intent.setAction( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_DOWN , keyCode );
		intent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		intent.setPackage( resolveName );
		mContext.sendOrderedBroadcast( intent , null );
		intent = new Intent( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_UP , keyCode );
		intent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		intent.setPackage( resolveName );
		mContext.sendOrderedBroadcast( intent , null );
		isPlaying = true;
	}
	
	public synchronized void prevMusic()
	{
		Intent intent = new Intent();
		int keyCode;
		KeyEvent keyEvent;
		keyCode = KeyEvent.KEYCODE_MEDIA_PREVIOUS;
		Intent meidaButtonIntent = new Intent( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_DOWN , keyCode );
		meidaButtonIntent.setPackage( resolveName );
		meidaButtonIntent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		mContext.sendOrderedBroadcast( meidaButtonIntent , null );
		meidaButtonIntent = new Intent( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_UP , keyCode );
		meidaButtonIntent.setPackage( resolveName );
		meidaButtonIntent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		mContext.sendOrderedBroadcast( meidaButtonIntent , null );
		isPlaying = true;
	}
	
	public void pauseMusic()
	{
		Intent intent = new Intent();
		int keyCode;
		KeyEvent keyEvent;
		keyCode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
		intent.setAction( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_DOWN , keyCode );
		intent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		intent.setPackage( resolveName );
		mContext.sendOrderedBroadcast( intent , null );
		intent = new Intent( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_UP , keyCode );
		intent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		intent.setPackage( resolveName );
		mContext.sendOrderedBroadcast( intent , null );
		isPlaying = false;
	}
	
	public void playMusic()
	{
		Intent intent = new Intent();
		int keyCode;
		KeyEvent keyEvent;
		keyCode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
		intent.setAction( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_DOWN , keyCode );
		intent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		intent.setPackage( resolveName );
		mContext.sendOrderedBroadcast( intent , null );
		intent = new Intent( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_UP , keyCode );
		intent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		intent.setPackage( resolveName );
		mContext.sendOrderedBroadcast( intent , null );
		isPlaying = true;
	}
	
	public void togglePlay()
	{
		if( isPlaying )
		{
			pauseMusic();
		}
		else
		{
			playMusic();
		}
	}
	
	public void entryList()
	{
		PackageManager pm = mContext.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage( resolveName );
		mContext.startActivity( intent );
	}
	
	public boolean mode = false;
	
	public void playSound()
	{
		AudioManager audioManager = (AudioManager)mContext.getSystemService( Context.AUDIO_SERVICE );
		Log.v( TAG , "AUDIO MODE: " + audioManager.getMode() );
		mode = !mode;
		audioManager.setStreamMute( AudioManager.STREAM_MUSIC , mode );
		playStateListener.onSoundModeChanged( mode );
	}
	
	public class MusicHelperReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context context ,
				Intent intent )
		{
			String action = intent.getAction();
			isPlaying = intent.getBooleanExtra( "playing" , false );
			String cmd = intent.getStringExtra( CMDNAME );
			if( action.equals( PLAY_STATE_CHANGED ) || action.equals( resolveName + ".playstatechanged" ) )
			{
				if( playStateListener != null )
					playStateListener.onPlayStateChanged( isPlaying );
			}
			else if( action.equals( META_CHANGED ) )
			{
			}
			else if( CMDNEXT.equals( cmd ) || NEXT_ACTION.equals( action ) )
			{
			}
			else if( CMDPREVIOUS.equals( cmd ) || PREVIOUS_ACTION.equals( action ) )
			{
			}
			else if( CMDTOGGLEPAUSE.equals( cmd ) || TOGGLEPAUSE_ACTION.equals( action ) )
			{
			}
			else if( CMDPAUSE.equals( cmd ) || PAUSE_ACTION.equals( action ) )
			{
			}
			else if( CMDSTOP.equals( cmd ) )
			{
			}
		}
	};
	
	public static interface OnPlayStateChanged
	{
		
		public boolean onSoundModeChanged(
				boolean mode );
		
		public boolean onPlayStateChanged(
				boolean isPlaying );
	}
}
