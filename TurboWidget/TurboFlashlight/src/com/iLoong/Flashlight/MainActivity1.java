package com.iLoong.Flashlight;


import java.io.IOException;

import android.app.Activity;
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
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cooeeui.turboflashlight.R;
import com.iLoong.Flashlight.view.FlashlightView1;
import com.iLoong.Flashlight.view.FlashlightView1.OnClickListener;
import com.iLoong.Flashlight.view.FlashlightView1.OnItemSelectedChanged;


public class MainActivity1 extends Activity
{
	
	public static int screenWidth;
	public static int screenHeight;
	private static final int MODEL_FLASHLIGHT = 0;
	private static final int MODEL_FLICKER_SLOW = 1;
	private static final int MODEL_FLICKER_FAST = 2;
	private static final int MODEL_SOS = 3;
	private int currModel;
	private SosHandler sosHandler;
	private FlickerSlowHandler flickerSlowHandler;
	private FlickerFastHandler flickerFastHandler;
	private static final int MODEL_FLICKER_SLOW_INTERVAL = 900;
	private static final int MODEL_FLICKER_FAST_INTERVAL = 300;
	private int sosIdx;
	private int[] flickerSlowCode = new int[]{ 900 , 900 , 900 , 900 , 900 , 900 , 900 , 900 , 900 , 900 , 900 , 900 , 900 , 900 , 900 , 900 , 900 , 900 , 900 };
	private int[] flickerFastCode = new int[]{ 300 , 300 , 300 , 300 , 300 , 300 , 300 , 300 , 300 , 300 , 300 , 300 , 300 , 300 , 300 , 300 , 300 , 300 , 300 };
	private int[] sosCode = new int[]{ 300 , 300 , 300 , 300 , 300 , 300 , 900 , 900 , 900 , 900 , 900 , 900 , 300 , 300 , 300 , 300 , 300 , 300 };
	private int flag;
	private boolean isOn = true;
	private FlashlightView1 FlashlightView1;
	private ImageView iv_light;
	private LinearLayout ll_on_bg;
	private ToggleButton tb_sos;
	private MediaPlayer mediaPlayer;
	private boolean playBeep = false;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;
		setContentView( R.layout.activity_main );
		findViewById();
		setListener();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Log.i( "MainActivity" , "onResume" );
		turnOn();
		FlashlightView1.setOn();
		initBeepSound();
	}
	
	private void findViewById()
	{
		FlashlightView1 = (FlashlightView1)findViewById( R.id.flashlightView );
		ll_on_bg = (LinearLayout)findViewById( R.id.ll_on_bg );
		iv_light = (ImageView)findViewById( R.id.iv_light );
		tb_sos = (ToggleButton)findViewById( R.id.tb_sos );
	}
	
	private void setListener()
	{
		FlashlightView1.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick()
			{
				if( !isOn )
				{
					isOn = true;
					FlashlightView1.setOn();
					switch( currModel )
					{
						case 0:
							turnOn();
							break;
						case 1:
							break;
						case 2:
							break;
						case 3:
							break;
						default:
							break;
					}
				}
				else
				{
					isOn = false;
					FlashlightView1.setOff();
					tb_sos.setChecked( false );
					switch( currModel )
					{
						case 0:
							turnOff();
							break;
						case 1:
							break;
						case 2:
							break;
						case 3:
							break;
						default:
							break;
					}
				}
			}
		} );
		FlashlightView1.setOnItemSelectedChanged( new OnItemSelectedChanged() {
			
			@Override
			public void onChanged(
					int itemId )
			{
				Log.v( "Flashlight" , "itemId: " + itemId );
				playBeepSoundAndVibrate();
				switch( itemId )
				{
					case 0:
						if( isOn )
						{
							turnOn();
							FlashlightView1.setOn();
						}
						currModel = MODEL_FLASHLIGHT;
						break;
					case 1:
						if( isOn )
						{
							currModel = MODEL_FLICKER_SLOW;
							modelFlickerSlow( MODEL_FLICKER_SLOW_INTERVAL );
						}
						currModel = MODEL_FLICKER_SLOW;
						break;
					case 2:
						if( isOn )
						{
							currModel = MODEL_FLICKER_FAST;
							modelFlickerFast( MODEL_FLICKER_FAST_INTERVAL );
						}
						currModel = MODEL_FLICKER_FAST;
						break;
					case 3:
						if( isOn )
						{
							currModel = MODEL_SOS;
							modeSos();
						}
						currModel = MODEL_SOS;
						break;
					default:
						break;
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
					FlashlightView1.setOn();
					FlashlightView1.moveToTap( 3 );
					currModel = MODEL_SOS;
					modeSos();
				}
				else
				{
					isOn = false;
					FlashlightView1.setOff();
				}
			}
		} );
	}
	
	private void modelFlickerFast(
			int flicker )
	{
		flag = 1;
		sosIdx = 0;
		flickerFastHandler = new FlickerFastHandler();
		updateFlickerFast();
	}
	
	private void updateFlickerFast()
	{
		if( currModel != MODEL_FLICKER_FAST || !isOn )
		{
			turnOff();
			return;
		}
		flickerFastHandler.sleep( flickerFastCode[sosIdx] );
		if( sosIdx + 1 == sosCode.length )
		{
			sosIdx = 0;
			flag = 1;
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
	
	private void modelFlickerSlow(
			int flicker )
	{
		flag = 1;
		sosIdx = 0;
		flickerSlowHandler = new FlickerSlowHandler();
		updateFlickerSlow();
	}
	
	private void updateFlickerSlow()
	{
		if( currModel != MODEL_FLICKER_SLOW || !isOn )
		{
			turnOff();
			return;
		}
		flickerSlowHandler.sleep( flickerSlowCode[sosIdx] );
		if( sosIdx + 1 == sosCode.length )
		{
			sosIdx = 0;
			flag = 1;
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
	
	private void turnOn()
	{
		ll_on_bg.setVisibility( View.VISIBLE );
		iv_light.setVisibility( View.VISIBLE );
		Parameters params = FlashlightApplication.get().getParameters();
		FlashlightApplication.getParameters().setFlashMode( Parameters.FLASH_MODE_TORCH );
		FlashlightApplication.get().setParameters( FlashlightApplication.getParameters() );
		isOn = true;
	}
	
	private void turnOff()
	{
		if( currModel != MODEL_SOS )
		{
			ll_on_bg.setVisibility( View.INVISIBLE );
			iv_light.setVisibility( View.INVISIBLE );
		}
		Parameters params = FlashlightApplication.get().getParameters();
		FlashlightApplication.getParameters().setFlashMode( Parameters.FLASH_MODE_OFF );
		FlashlightApplication.get().setParameters( FlashlightApplication.getParameters() );
		//		flickThread = new Thread( flickRun );
		//		flickThread.start();
	}
	
	//	private Thread flickThread;
	//	private Runnable flickRun = new Runnable() {
	//		
	//		@Override
	//		public void run()
	//		{
	//			while( true )
	//			{
	//				flick();
	//				Thread.sleep( 500 );
	//			}
	//		}
	//	};
	class FlickerFastHandler extends Handler
	{
		
		@Override
		public void handleMessage(
				Message msg )
		{
			super.handleMessage( msg );
			MainActivity1.this.updateFlickerFast();
		}
		
		public void sleep(
				long delayMillis )
		{
			removeMessages( 0 );
			sendMessageDelayed( obtainMessage( 0 ) , delayMillis );
		}
	}
	
	class FlickerSlowHandler extends Handler
	{
		
		@Override
		public void handleMessage(
				Message msg )
		{
			super.handleMessage( msg );
			MainActivity1.this.updateFlickerSlow();
		}
		
		public void sleep(
				long delayMillis )
		{
			removeMessages( 0 );
			sendMessageDelayed( obtainMessage( 0 ) , delayMillis );
		}
	}
	
	class SosHandler extends Handler
	{
		
		@Override
		public void handleMessage(
				Message msg )
		{
			super.handleMessage( msg );
			MainActivity1.this.updateSosUi();
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
		super.onPause();
	}
	
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
	
	@Override
	protected void onDestroy()
	{
		FlashlightApplication.get().release();
		super.onDestroy();
	}
	
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
}
