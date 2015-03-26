package com.iLoong.launcher.wallpeper;


import java.util.HashMap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Log;


public class ShakeListener implements SensorEventListener
{
	
	private SensorManager mSensorMgr;
	private OnShakeListener mShakeListener;
	private Context mContext;
	
	public interface OnShakeListener
	{
		
		public void onShake();
	}
	
	public ShakeListener(
			Context context )
	{
		mContext = context;
	}
	
	public void setOnShakeListener(
			OnShakeListener listener )
	{
		mShakeListener = listener;
	}
	
	public boolean checkSensorSupported()
	{
		try
		{
			SensorManager tmpSensorMgr = (SensorManager)mContext.getSystemService( Context.SENSOR_SERVICE );
			ShakeListener tempListener = new ShakeListener( mContext );
			boolean supported = tmpSensorMgr.registerListener( tempListener , tmpSensorMgr.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ) , SensorManager.SENSOR_DELAY_NORMAL );
			if( !supported )
			{
				tmpSensorMgr.unregisterListener( tempListener );
				return false;
			}
			else
			{
				tmpSensorMgr.unregisterListener( tempListener );
				return true;
			}
		}
		catch( Exception e )
		{
			return false;
		}
	}
	
	public void resume()
	{
		mSensorMgr = (SensorManager)mContext.getSystemService( Context.SENSOR_SERVICE );
		if( mSensorMgr == null )
		{
			return;
		}
		boolean supported = mSensorMgr.registerListener( this , mSensorMgr.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ) , SensorManager.SENSOR_DELAY_GAME );
		if( !supported )
		{
			mSensorMgr.unregisterListener( this );
			mSensorMgr = null;
		}
	}
	
	public void pause()
	{
		if( mSensorMgr != null )
		{
			mSensorMgr.unregisterListener( this );
			mSensorMgr = null;
		}
	}
	
	@Override
	public void onAccuracyChanged(
			Sensor sensor ,
			int accuracy )
	{
	}
	
	SoundPool soundPool; // SoundPool对象引用
	HashMap<Integer , Integer> soundPoolMap;// 声音的管理容器
	boolean SOUND_PLAY = false;
	
	public void initSounds()
	{
		soundPool = new SoundPool( 1 , AudioManager.STREAM_MUSIC , 100 );
		soundPoolMap = new HashMap<Integer , Integer>();
		soundPool.setOnLoadCompleteListener( new OnLoadCompleteListener() {
			
			@Override
			public void onLoadComplete(
					SoundPool soundPool ,
					int sampleId ,
					int status )
			{
				if( status != 0 )
				{
					Log.e( "SOUND LOAD" , " Sound ID: " + sampleId + " Failed to load." );
				}
				else
				{
					Log.i( "SOUND LOAD" , " Sound ID: " + sampleId + " loaded." );
					playSound( 1 , 1 ); // 播放5次
				}
			}
		} );
		soundPoolMap.put( 1 , soundPool.load( mContext , RR.raw.shakenotify , 1 ) );// 加载声音
	}
	
	public void playSound(
			int sound ,
			int loop )// 播放声音的方法
	{
		AudioManager mgr = (AudioManager)mContext.getSystemService( Context.AUDIO_SERVICE );
		float streamVolumeCurrent = mgr.getStreamVolume( AudioManager.STREAM_MUSIC );
		float streamVolumeMax = mgr.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
		float volume = streamVolumeCurrent / streamVolumeMax;// 得到音量的大小
		int RingerMode = mgr.getRingerMode();
		if( RingerMode == AudioManager.RINGER_MODE_NORMAL )
		{
			soundPool.play( sound , volume , volume , 1 , loop , 1f );
		}
	}
	
	@Override
	public void onSensorChanged(
			SensorEvent event )
	{
		int sensorType = event.sensor.getType();
		float[] values = event.values;
		int sensitivity = 19;
		if( sensorType == Sensor.TYPE_ACCELEROMETER )
		{
			if( Desktop3DListener.d3d != null && !Desktop3DListener.d3d.hasDown() )
			{
				if( Math.abs( values[0] ) > sensitivity || Math.abs( values[1] ) > sensitivity || Math.abs( values[2] ) > sensitivity )
				{
					mShakeListener.onShake();
				}
			}
		}
	}
}
