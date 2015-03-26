// xiatian add whole file //Widget3D adaptation "Naked eye 3D"
package com.iLoong.launcher.SetupMenu;


import android.content.Intent;

import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.desktop.iLoongLauncher;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.iLoong.launcher.Desktop3D.Log;


public class SensorListener
{
	
	private Context mContext;
	private SensorManager mSensorMgr = null;
	private Sensor mSensor = null;
	private SensorEventListener mSensorEventListener = null;
	private float x , y , z , xBase , yBase , zBase;
	private boolean locking = false;
	private boolean haveSetBase = false;
	public static float SENSOR_SENSITIVITY = 1; //【可配置】launcher侧，重力感应器灵敏度。
	private float xAngleLast , yAngleLast;
	private long lastSendDataTime;
	public static int SENSOR_ANGLE_OFFSET_MIN = 3; //【可配置】launcher侧，有效数据最小偏移角度，大于该值才向Widget3D发送数据。
	public static int SENSOR_SEND_DATA_RATE = 200; //【可配置】launcher侧，数据发送时间间隔。单位为"毫秒"
	
	public SensorListener(
			Context context )
	{
		if( DefaultLayout.show_sensor )
		{
			mContext = context;
			//		Log.v("xiatian","SensorListener -- init");
			if( iLoongLauncher.getInstance().isPhoneSupportSensor() )
			{
				//			Log.v("xiatian","SensorListener -- init -- IsSupportSensor:[ok]");
				mSensorMgr = (SensorManager)mContext.getSystemService( Context.SENSOR_SERVICE );
				mSensor = mSensorMgr.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
				mSensorEventListener = new SensorEventListener() {
					
					public void onSensorChanged(
							SensorEvent e )
					{
						if( locking == true )
						{
							return;
						}
						x = e.values[SensorManager.DATA_X] * SENSOR_SENSITIVITY;
						y = e.values[SensorManager.DATA_Y] * SENSOR_SENSITIVITY;
						z = e.values[SensorManager.DATA_Z] * SENSOR_SENSITIVITY;
						//					Log.v("xiatian","SensorListener -- onSensorChanged:"+"x=" + (int)x + ",y=" + (int)y + ",z=" + (int)z);
						if( haveSetBase == false )
						{
							xBase = x;
							yBase = y;
							zBase = z;
							xAngleLast = xBase;
							yAngleLast = yBase;
							haveSetBase = true;
							Widget3DManager.getInstance().sentSensorAngleToAllWidget3D( 0 , 0 , true );
							lastSendDataTime = System.currentTimeMillis();
							return;
						}
						sentSensorAngleToAllWidget3D( x , y , z );
					}
					
					public void onAccuracyChanged(
							Sensor s ,
							int accuracy )
					{
					}
				};
				IntentFilter filter = new IntentFilter();
				filter.addAction( Intent.ACTION_USER_PRESENT/*android.intent.action.USER_PRESENT*/);
				filter.addAction( Intent.ACTION_SCREEN_OFF/*android.intent.action.SCREEN_OFF*/);
				filter.addAction( Intent.ACTION_SCREEN_ON/*android.intent.action.SCREEN_OFF*/);
				mContext.registerReceiver( mBroadcastReceiver , filter );
			}
			else
			{
				//			Log.v("xiatian","SensorListener -- init -- IsSupportSensor:[no]");
			}
		}
	}
	
	public void resume()
	{
		if( DefaultLayout.show_sensor )
		{
			if( !iLoongLauncher.getInstance().isPhoneSupportSensor() )
			{
				//			Toast.makeText(
				//				SetupMenu.getContext(),//iLoongLauncher.getInstance(),
				//				SetupMenu.getContext().getResources().getString(RR.string.sensor_not_supported),//iLoongLauncher.getInstance().getResources().getString(RR.string.medialist_no_photo),
				//				Toast.LENGTH_SHORT
				//				)
				//			.show();
				return;
			}
			mSensorMgr.registerListener( mSensorEventListener , mSensor , SensorManager.SENSOR_DELAY_GAME );
			if( locking == true )
			{
				locking = false;
			}
			//		Log.v("xiatian","SensorListener -- registerListener");
		}
	}
	
	public void pause()
	{
		if( DefaultLayout.show_sensor )
		{
			if( !iLoongLauncher.getInstance().isPhoneSupportSensor() )
			{
				return;
			}
			mSensorMgr.unregisterListener( mSensorEventListener , mSensor );
			haveSetBase = false;
			Widget3DManager.getInstance().sentSensorAngleToAllWidget3D( 0 , 0 , false );
			//		Log.v("xiatian","SensorListener -- unregisterListener");
		}
	}
	
	public void sentSensorAngleToAllWidget3D(
			float x ,
			float y ,
			float z )
	{
		if( DefaultLayout.show_sensor )
		{
			/*
			 * xAngle - 水平方向偏移角度,范围为（-180,180）
			 * 		(0,180):先把手机水平向上，抬起手机右侧并翻向左侧,直至手机面向下 。  
			 * 		(0,-180):先把手机水平向上，抬起手机左侧并翻向右侧,直至手机面向下 。  
			 * 
			 * yAngle - 垂直方向偏移角度,范围为（-180,180）
			 * 		(0,180):先把手机水平向上，抬起手机上侧并翻向下侧,直至手机面向下 。  
			 * 		(0,-180):先把手机水平向上，抬起手机下侧并翻向上侧,直至手机面向下 。  
			*/
			//是否设置初始值
			if( haveSetBase == true )
			{
				x -= xBase;
				y -= yBase;
			}
			else
			{
				Widget3DManager.getInstance().sentSensorAngleToAllWidget3D( 0 , 0 , false );
				return;
			}
			//是否超过数据发送时间间隔
			if( System.currentTimeMillis() - lastSendDataTime < SENSOR_SEND_DATA_RATE )
			{
				return;
			}
			else
			{
				lastSendDataTime = System.currentTimeMillis();
			}
			//将重力感应器的（-10,10）的数值，转化为（-180,180）的角度值
			float xAngle = 0;
			float yAngle = 0;
			//		Log.v("xiatian","transformSensorEventToAngle -- before:"+"x=" + x + ",y=" + y + ",z=" + z);
			xAngle = 9 * x;
			yAngle = 9 * y;
			if( z < 0 )
			{
				if( xAngle > 0 )
				{
					xAngle = 180 - xAngle;
				}
				else
				{
					xAngle = -180 - xAngle;
				}
				if( yAngle > 0 )
				{
					yAngle = 180 - yAngle;
				}
				else
				{
					yAngle = -180 - yAngle;
				}
			}
			//		Log.v("xiatian","transformSensorEventToAngle -- after:"+"xAngle=" + xAngle + ",yAngle=" + yAngle);		
			//第一次有效数据修正角度
			if( ( xAngleLast == xBase ) && ( yAngleLast == yBase ) )
			{
				xAngleLast = 0;
				yAngleLast = 0;
				xAngleLast = 9 * xBase;
				yAngleLast = 9 * yBase;
				if( zBase < 0 )
				{
					if( xAngleLast > 0 )
					{
						xAngleLast = 180 - xAngleLast;
					}
					else
					{
						xAngleLast = -180 - xAngleLast;
					}
					if( yAngleLast > 0 )
					{
						yAngleLast = 180 - yAngleLast;
					}
					else
					{
						yAngleLast = -180 - yAngleLast;
					}
				}
			}
			//是否超过有效数据最小偏移角度
			if( !isDataValid( xAngle , xAngleLast ) && !isDataValid( yAngle , yAngleLast ) )
			{
				return;
			}
			if( !isDataValid( xAngle , xAngleLast ) )
			{
				xAngle = xAngleLast;
			}
			if( !isDataValid( yAngle , yAngleLast ) )
			{
				yAngle = yAngleLast;
			}
			xAngleLast = xAngle;
			yAngleLast = yAngle;
			Widget3DManager.getInstance().sentSensorAngleToAllWidget3D( yAngle , -xAngle , false );//Widget3DManager.getInstance().resumeAllWidget3D();
		}
	}
	
	public boolean isDataValid(
			float mDataCur ,
			float mDataLast )
	{
		if( DefaultLayout.show_sensor )
		{
			boolean mDataCurIsPositiveNumber = ( mDataCur > 0 );
			boolean xDataLastIsPositiveNumber = ( mDataLast > 0 );
			if( ( ( mDataCurIsPositiveNumber ) && ( xDataLastIsPositiveNumber ) && ( ( Math.abs( mDataLast - mDataCur ) > SENSOR_ANGLE_OFFSET_MIN ) ) ) || ( ( !mDataCurIsPositiveNumber ) && ( !xDataLastIsPositiveNumber ) && ( ( Math
					.abs( mDataLast - mDataCur ) > SENSOR_ANGLE_OFFSET_MIN ) ) ) || ( Math.abs( mDataLast - mDataCur ) > SENSOR_ANGLE_OFFSET_MIN ) )
			{
				return true;
			}
			return false;
		}
		else
		{
			return false;
		}
	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		public void onReceive(
				Context context ,
				Intent intent )
		{
			if( DefaultLayout.show_sensor )
			{
				String action = intent.getAction();
				//          	Log.v("xiatian", "onReceive, action=" + action);
				if( Intent.ACTION_SCREEN_ON.equals( action ) )
				{
					resume();
				}
				if( ( Intent.ACTION_USER_PRESENT.equals( action ) ) || ( Intent.ACTION_SCREEN_ON.equals( action ) ) )
				{
					locking = false;
					haveSetBase = false;
					Widget3DManager.getInstance().sentSensorAngleToAllWidget3D( 0 , 0 , false );
				}
				else if( Intent.ACTION_SCREEN_OFF.equals( action ) )
				{
					//					locking = true;
					pause();
				}
			}
		}
	};
}
