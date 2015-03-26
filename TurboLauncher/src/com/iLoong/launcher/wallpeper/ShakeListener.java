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
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.Workspace3D;


public class ShakeListener implements SensorEventListener
{
	
	//	private static final int FORCE_THRESHOLD = 500;
	private static final int TIME_THRESHOLD = 100;
	private static final int SHAKE_TIMEOUT = 500;
	private static final int SHAKE_DURATION = 1000;
	private static final int SHAKE_COUNT = 6;
	private SensorManager mSensorMgr;
	private float mLastX = -1.0f;
	private float mLastY = -1.0f;
	private float mLastZ = -1.0f;
	private long mLastTime;
	private OnShakeListener mShakeListener;
	private Context mContext;
	private int mShakeCount = 0;
	private long mLastShake;
	private long mLastForce;
	
	public interface OnShakeListener
	{
		
		public void onShake();
	}
	
	public ShakeListener(
			Context context )
	{
		mContext = context;
		// initSounds();
	}
	
	public void setOnShakeListener(
			OnShakeListener listener )
	{
		mShakeListener = listener;
	}
	
	//	private int getSensorConfigRate()
	//	{
	//		if (DefaultLayout.sensor_delay_level==0)
	//		{
	//			return SensorManager.SENSOR_DELAY_FASTEST ; //10
	//		}
	//		else if (DefaultLayout.sensor_delay_level==1)
	//		{
	//			return SensorManager.SENSOR_DELAY_GAME ;//20
	//		}
	//		else if (DefaultLayout.sensor_delay_level==2)
	//		{
	//			return SensorManager.SENSOR_DELAY_UI ;//60
	//		}
	//		else if (DefaultLayout.sensor_delay_level==3)
	//		{
	//			return SensorManager.SENSOR_DELAY_NORMAL ;//160
	//		}
	//		else
	//		{
	//			return SensorManager.SENSOR_DELAY_GAME ;
	//		}
	//	}
	private int getSensorForceThreshold()
	{
		if( DefaultLayout.sensor_delay_level > 0 && DefaultLayout.sensor_delay_level <= 10 )
			return DefaultLayout.sensor_delay_level * 100; //100~1000
		else
			return 500;
	}
	
	public boolean checkSensorSupported()
	{
		try
		{
			SensorManager tmpSensorMgr = (SensorManager)mContext.getSystemService( Context.SENSOR_SERVICE );
			ShakeListener tempListener = new ShakeListener( mContext );
			boolean supported = tmpSensorMgr.registerListener( tempListener , tmpSensorMgr.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ) , SensorManager.SENSOR_DELAY_NORMAL );
			Log.e( "test" , "sensor:" + supported );
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
			// throw new UnsupportedOperationException("Sensors not supported");
			return;
		}
		boolean supported = mSensorMgr.registerListener( this , mSensorMgr.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ) , SensorManager.SENSOR_DELAY_GAME );
		if( !supported )
		{
			mSensorMgr.unregisterListener( this );
			// throw new UnsupportedOperationException(
			// "Accelerometer not supported");
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
		//		if( event.sensor.getType() != Sensor.TYPE_ACCELEROMETER )
		//		{
		//			return;
		//		}
		//		long now = System.currentTimeMillis();
		//		if( ( now - mLastForce ) > SHAKE_TIMEOUT )
		//		{
		//			mShakeCount = 0;
		//		}
		//		if( ( now - mLastTime ) > TIME_THRESHOLD )
		//		{
		//			long diff = now - mLastTime;
		//			float speed = Math.abs( event.values[SensorManager.DATA_X] + event.values[SensorManager.DATA_Y] + event.values[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ ) / diff * 10000;
		//			if( speed > getSensorForceThreshold() )
		//			{
		//				if( ( ++mShakeCount >= SHAKE_COUNT ) && ( now - mLastShake > SHAKE_DURATION ) )
		//				{
		//					mLastShake = now;
		//					mShakeCount = 0;
		//					if( mShakeListener != null && !Workspace3D.isDown )
		//					{
		//						mShakeListener.onShake();
		//					}
		//				}
		//				mLastForce = now;
		//			}
		//			mLastTime = now;
		//			mLastX = event.values[SensorManager.DATA_X];
		//			mLastY = event.values[SensorManager.DATA_Y];
		//			mLastZ = event.values[SensorManager.DATA_Z];
		//		}
		int sensorType = event.sensor.getType();
		float[] values = event.values;
		int sensitivity = 19;
		if( sensorType == Sensor.TYPE_ACCELEROMETER )
		{
			if( !Workspace3D.isDown )
			{
				if( Math.abs( values[0] ) > sensitivity || Math.abs( values[1] ) > sensitivity || Math.abs( values[2] ) > sensitivity )
				{
					mShakeListener.onShake();
				}
			}
		}
	}
}
