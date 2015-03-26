package com.iLoong.Shortcuts.SystemTool;


import com.iLoong.Shortcuts.View.LiangduView;
import com.iLoong.launcher.desktop.iLoongLauncher;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import android.util.Log;
import android.view.WindowManager;
import android.widget.SeekBar;


public class BrightnessAdmin implements ISystemAdmin
{
	
	private Context mContext;
	private int mNowBrightnessValue;
	private SeekBar mSb = null;
	private int mBrightness;
	String key;
	BrightnessStateReceiver mBrightnessStateReceiver;
	
	public BrightnessAdmin(
			Context context ,
			String key )
	{
		this.key = key;
		mContext = context;
		mBrightnessStateReceiver = new BrightnessStateReceiver();
		IntentFilter filter = new IntentFilter( "com.comet.shortcut.Brightness.change" );
		context.registerReceiver( mBrightnessStateReceiver , filter ); // 不要忘了之后解除绑定
		mContext = context;
	}
	
	public void setSeekBar(
			SeekBar sb )
	{
		mSb = sb;
	}
	
	public void onDelete()
	{
		mSb = null;
		mContext.unregisterReceiver( mBrightnessStateReceiver );
	}
	
	/**
	 * 判断是否开启了自动亮度调节
	 * 
	 * @param aContext
	 * @return
	 */
	public static boolean isAutoBrightness(
			ContentResolver aContentResolver )
	{
		boolean automicBrightness = false;
		try
		{
			automicBrightness = Settings.System.getInt( aContentResolver , Settings.System.SCREEN_BRIGHTNESS_MODE ) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
		}
		catch( SettingNotFoundException e )
		{
			e.printStackTrace();
		}
		return automicBrightness;
	}
	
	/**
	 * 获取屏幕的亮度
	 * 
	 * @param activity
	 * @return
	 */
	public boolean getReadyState()
	{
		return getState();
	}
	
	public void setBrightness(
			final int brightness )
	{
		mBrightness = brightness;
		if( mBrightness == 0 )
		{
			mBrightness = 5;
		}
		if( isAutoBrightness( mContext.getContentResolver() ) )
		{
			stopAutoBrightness( mContext.getContentResolver() );
		}
		Handler handler = new Handler( mContext.getMainLooper() );
		handler.post( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				WindowManager.LayoutParams lp = ( (Activity)mContext ).getWindow().getAttributes();
				lp.screenBrightness = mBrightness / 255.0f;
				( (Activity)mContext ).getWindow().setAttributes( lp );
			}
		} );
		setScreenBrightness( mContext.getContentResolver() , brightness );
		if( changecallback != null )
			changecallback.StateChange( key + brightness );
	}
	
	public int getBrightness()
	{
		return getScreenBrightness( mContext.getContentResolver() );
	}
	
	public static int getScreenBrightness(
			ContentResolver resolver )
	{
		int nowBrightnessValue = 0;
		try
		{
			nowBrightnessValue = android.provider.Settings.System.getInt( resolver , Settings.System.SCREEN_BRIGHTNESS , 100 );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		Log.d( "get brightness" , "" + nowBrightnessValue );
		return nowBrightnessValue;
	}
	
	/**
	 * 获取屏幕的亮度
	 * 
	 * @param activity
	 * @return
	 */
	public static void setScreenBrightness(
			ContentResolver resolver ,
			int bright )
	{
		Log.d( "set brightness" , "" + bright );
		try
		{
			android.provider.Settings.System.putInt( resolver , Settings.System.SCREEN_BRIGHTNESS , bright );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			Log.d( "set brightness" , "failed" );
		}
	}
	
	public static void stopAutoBrightness(
			ContentResolver resolver )
	{
		Settings.System.putInt( resolver , Settings.System.SCREEN_BRIGHTNESS_MODE , Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL );
		Log.d( "stop auto brightness" , "ok" );
	}
	
	public static void startAutoBrightness(
			ContentResolver resolver )
	{
		Settings.System.putInt( resolver , Settings.System.SCREEN_BRIGHTNESS_MODE , Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC );
		Log.d( "startAutoBrightness" , "ok" );
	}
	
	/**
	 * 保存亮度设置状态
	 * 
	 * @param resolver
	 * @param brightness
	 */
	public static void saveBrightness(
			ContentResolver resolver ,
			int brightness )
	{
		Uri uri = android.provider.Settings.System.getUriFor( "screen_brightness" );
		android.provider.Settings.System.putInt( resolver , "screen_brightness" , brightness );
		// resolver.registerContentObserver(uri, true, myContentObserver);
		resolver.notifyChange( uri , null );
	}
	
	public int getLevelState()
	{
		int brightness = getScreenBrightness( mContext.getContentResolver() );
		if( isAutoBrightness( mContext.getContentResolver() ) )
		{
			return 0;
		}
		else if( brightness <= 20 )
		{
			return 1;
		}
		else if( brightness >= 240 )
		{
			return 3;
		}
		else
		{
			return 2;
		}
	}
	
	public boolean getState()
	{
		int brightness = getScreenBrightness( mContext.getContentResolver() );
		if( brightness <= 20 )
		{
			return false;
		}
		else if( brightness >= 240 )
		{
			return true;
		}
		else
		{
			return true;
		}
	}
	
	public void select()
	{
		int brightness = getScreenBrightness( mContext.getContentResolver() );
		if( isAutoBrightness( mContext.getContentResolver() ) )
		{
			brightness = 5;
		}
		else if( brightness <= 20 )
		{
			brightness = 255 / 2;
		}
		else if( brightness >= 240 )
		{
			startAutoBrightness( mContext.getContentResolver() );
			if( changecallback != null )
				changecallback.StateChange( key + brightness );
			return;
		}
		else
		{
			brightness = 255;
		}
		setBrightness( brightness );
		if( mSb != null )
		{
			mSb.setProgress( brightness );
		}
	}
	
	IAdminCallback changecallback;
	
	public void setCallback(
			IAdminCallback callback )
	{
		// TODO Auto-generated method stub
		changecallback = callback;
	}
	
	private final class BrightnessStateReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context c ,
				Intent intent )
		{
			String action = intent.getAction();
			if( "com.comet.shortcut.Brightness.change".equals( action ) )
			{
				Log.d( "Broadcast" , "bluetooth" );
				if( changecallback != null )
				{
					LiangduView view = (LiangduView)changecallback;
					view.statec();
				}
			}
		}
	}
}
