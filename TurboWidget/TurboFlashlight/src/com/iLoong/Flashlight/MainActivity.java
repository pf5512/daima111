package com.iLoong.Flashlight;


import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cooeeui.turboflashlight.R;
import com.iLoong.Flashlight.view.FlashlightView;
import com.iLoong.Flashlight.view.FlashlightView.OnClickListener;


public class MainActivity extends Activity implements Callback
{
	
	public static int screenWidth;
	public static int screenHeight;
	private static final int MODEL_FLASHLIGHT = 0;
	private static final int MODEL_SOS = 3;
	private int currModel;
	private SosHandler sosHandler;
	private int sosIdx;
	private int[] sosCode = new int[]{ 300 , 300 , 300 , 300 , 300 , 300 , 900 , 900 , 900 , 900 , 900 , 900 , 300 , 300 , 300 , 300 , 300 , 300 };
	private int flag;
	private boolean isOn = true;
	private FlashlightView flashlightView;
	private ImageView iv_light;
	private LinearLayout ll_on_bg;
	private ToggleButton tb_sos;
	private MediaPlayer mediaPlayer;
	private boolean playBeep = false;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private SurfaceHolder holder;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		Log.v( "Flashlight" , "onCreate" );
		super.onCreate( savedInstanceState );
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;
		setContentView( R.layout.activity_main );
		findViewById();
		setListener();
		IntentFilter filter = new IntentFilter( Intent.ACTION_SCREEN_OFF );
		registerReceiver( mBatInfoReceiver , filter );
		SurfaceView sv = (SurfaceView)findViewById( R.id.sv );
		holder = sv.getHolder();
		holder.addCallback( this );
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Log.i( "Flashlight" , "onResume" );
		if( isOn )
		{
			turnOn();
			flashlightView.setOn();
		}
		initBeepSound();
	}
	
	private void findViewById()
	{
		flashlightView = (FlashlightView)findViewById( R.id.flashlightView );
		ll_on_bg = (LinearLayout)findViewById( R.id.ll_on_bg );
		iv_light = (ImageView)findViewById( R.id.iv_light );
		tb_sos = (ToggleButton)findViewById( R.id.tb_sos );
	}
	
	private void setListener()
	{
		flashlightView.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick()
			{
				if( !isOn )
				{
					isOn = true;
					flashlightView.setOn();
					turnOn();
				}
				else
				{
					isOn = false;
					flashlightView.setOff();
					tb_sos.setChecked( false );
					turnOff();
				}
			}
		} );
		tb_sos.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(
					CompoundButton buttonView ,
					boolean isChecked )
			{
				if( isChecked )
				{
					isOn = true;
					flashlightView.setOn();
					currModel = MODEL_SOS;
					modeSos();
				}
				else
				{
					isOn = false;
					flashlightView.setOff();
					currModel = MODEL_FLASHLIGHT;
				}
			}
		} );
	}
	
	private void modeSos()
	{
		flag = 1;
		sosIdx = 0;
		sosHandler = new SosHandler();
		updateSosUi();
	}
	
	private void updateSosUi()
	{
		if( currModel != MODEL_SOS || !isOn )
		{
			turnOff();
			return;
		}
		sosHandler.sleep( sosCode[sosIdx] );
		if( sosIdx + 1 == sosCode.length )
		{
			Log.v( "Flashlight" , "Circulation" );
			sosIdx = 0;
			flag = 1;
			Log.v( "Flashlight" , "flag:" + flag );
		}
		else
		{
			sosIdx++;
		}
		if( flag == 1 )
		{
			turnOn();
			flag = 0;
		}
		else
		{
			turnOff();
			flag = 1;
		}
	}
	
	public void turnOn()
	{
		Log.i( "Flashlight" , "turnOn" );
		ll_on_bg.setVisibility( View.VISIBLE );
		iv_light.setVisibility( View.VISIBLE );
		Parameters params = FlashlightApplication.get().getParameters();
		FlashlightApplication.getParameters().setFlashMode( Parameters.FLASH_MODE_TORCH );
		FlashlightApplication.get().setParameters( FlashlightApplication.getParameters() );
		isOn = true;
	}
	
	public void turnOff()
	{
		Log.e( "Flashlight" , "turnOff" );
		if( currModel != MODEL_SOS )
		{
			ll_on_bg.setVisibility( View.INVISIBLE );
			iv_light.setVisibility( View.INVISIBLE );
		}
		Parameters params = FlashlightApplication.get().getParameters();
		FlashlightApplication.getParameters().setFlashMode( Parameters.FLASH_MODE_OFF );
		FlashlightApplication.get().setParameters( FlashlightApplication.getParameters() );
	}
	
	class SosHandler extends Handler
	{
		
		@Override
		public void handleMessage(
				Message msg )
		{
			super.handleMessage( msg );
			MainActivity.this.updateSosUi();
		}
		
		public void sleep(
				long delayMillis )
		{
			removeMessages( 0 );
			sendMessageDelayed( obtainMessage( 0 ) , delayMillis );
		}
	}
	
	@Override
	protected void onPause()
	{
		Log.v( "Flashlight" , "onPause" );
		turnOff();
		super.onPause();
	}
	
	@Override
	protected void onStop()
	{
		Log.v( "Flashlight" , "onStop" );
		super.onStop();
	}
	
	@Override
	protected void onDestroy()
	{
		Log.v( "Flashlight" , "onDestroy" );
		Intent intent = new Intent( "com.iLoong.Flashlight.FlashlightService" );
		stopService( intent );
		turnOff();
		FlashlightApplication.release();
		if( mBatInfoReceiver != null )
		{
			unregisterReceiver( mBatInfoReceiver );
		}
		super.onDestroy();
	}
	
	private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(
				Context context ,
				Intent intent )
		{
			final String action = intent.getAction();
			if( Intent.ACTION_SCREEN_OFF.equals( action ) )
			{
				if( isOn )
				{
					Log.v( "Flashlight" , "SCREEN_OFF" );
					Intent intent1 = new Intent( MainActivity.this , FlashlightService.class );
					intent1.addFlags( 11 );
					startService( intent1 );
				}
			}
		}
	};
	
	private void initBeepSound()
	{
		if( playBeep && mediaPlayer == null )
		{
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream( AudioManager.STREAM_MUSIC );
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType( AudioManager.STREAM_MUSIC );
			mediaPlayer.setOnCompletionListener( beepListener );
			AssetFileDescriptor file = getResources().openRawResourceFd( R.raw.beep );
			try
			{
				mediaPlayer.setDataSource( file.getFileDescriptor() , file.getStartOffset() , file.getLength() );
				file.close();
				mediaPlayer.setVolume( BEEP_VOLUME , BEEP_VOLUME );
				mediaPlayer.prepare();
			}
			catch( IOException e )
			{
				mediaPlayer = null;
			}
		}
	}
	
	private static final long VIBRATE_DURATION = 200L;
	
	private void playBeepSoundAndVibrate()
	{
		if( playBeep && mediaPlayer != null )
		{
			mediaPlayer.start();
		}
		if( vibrate )
		{
			Vibrator vibrator = (Vibrator)getSystemService( VIBRATOR_SERVICE );
			vibrator.vibrate( VIBRATE_DURATION );
		}
	}
	
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		
		public void onCompletion(
				MediaPlayer mediaPlayer )
		{
			mediaPlayer.seekTo( 0 );
		}
	};
	private long exitTime = 0;
	
	@Override
	public boolean onKeyDown(
			int keyCode ,
			KeyEvent event )
	{
		if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN )
		{
			if( System.currentTimeMillis() - exitTime > 2000 )
			{
				Toast.makeText( getApplicationContext() , getString( R.string.exit_toast ) , Toast.LENGTH_SHORT ).show();
				exitTime = System.currentTimeMillis();
			}
			else
			{
				finish();
				System.exit( 0 );
			}
			return true;
		}
		return super.onKeyDown( keyCode , event );
	}
	
	@Override
	public void surfaceCreated(
			SurfaceHolder holder )
	{
		this.holder = holder;
		if( FlashlightApplication.get() != null )
		{
			try
			{
				FlashlightApplication.get().setPreviewDisplay( holder );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void surfaceChanged(
			SurfaceHolder holder ,
			int format ,
			int width ,
			int height )
	{
	}
	
	@Override
	public void surfaceDestroyed(
			SurfaceHolder holder )
	{
		if( FlashlightApplication.get() != null )
		{
			FlashlightApplication.get().stopPreview();
			this.holder = null;
		}
	}
}
