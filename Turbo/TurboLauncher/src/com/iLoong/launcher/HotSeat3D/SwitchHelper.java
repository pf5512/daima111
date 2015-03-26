package com.iLoong.launcher.HotSeat3D;


import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class SwitchHelper
{
	
	private final String bluetoothAction = "android.bluetooth.adapter.action.STATE_CHANGED";
	SwitchHelperCallBack mCallback;
	private final String TAG = "SwitchHelper";
	Context mContext;
	private WifiManager mWifiManager;
	private LocationManager mLocationManager;
	WifiStateReceiver wifiReceiver;
	GpsStateReceiver gpsReceiver;
	private BlueToothStateReceiver mBluetoothStateReceiver;
	private static BluetoothAdapter mBluetoothAdapter;
	public final static int BLUETOOTH_START = 0;
	public final static int BLUETOOTH_STOP = 1;
	public final static int BLUETOOTH_READY = 2;
	public int bluetoothState = BLUETOOTH_STOP;
	private int mNowBrightnessValue;
	private SeekBar mSb = null;
	private int mBrightness;
	public final static int BRIGHTNESS_LOW = 0;
	public final static int BRIGHTNESS_MIDDLE = 1;
	public final static int BRIGHTNESS_HIGH = 2;
	public final static int BRIGHTNESS_AUTO = 3;
	private GPRSStateReceiver mGprsStateReceiver;
	private ConnectivityManager mConnectivityManager;
	
	public SwitchHelper(
			Context context ,
			SwitchHelperCallBack callback )
	{
		this.mContext = context;
		mCallback = callback;
		mLocationManager = (LocationManager)context.getSystemService( Context.LOCATION_SERVICE );
		mWifiManager = (WifiManager)context.getSystemService( Context.WIFI_SERVICE );
		mConnectivityManager = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		registerReceiver();
	}
	
	public void registerReceiver()
	{
		wifiReceiver = new WifiStateReceiver();
		mContext.registerReceiver( wifiReceiver , new IntentFilter( WifiManager.WIFI_STATE_CHANGED_ACTION ) );
		gpsReceiver = new GpsStateReceiver();
		mContext.registerReceiver( gpsReceiver , new IntentFilter( LocationManager.PROVIDERS_CHANGED_ACTION ) );
		mBluetoothStateReceiver = new BlueToothStateReceiver();
		mContext.registerReceiver( mBluetoothStateReceiver , new IntentFilter( bluetoothAction ) );
		setBluetoothAdapter();
		//		mBrightnessStateReceiver = new BrightStateReceiver();
		//		mContext.registerReceiver( mBrightnessStateReceiver , new IntentFilter( brightAction ) );
		//		
		mGprsStateReceiver = new GPRSStateReceiver();
		if( mGprsStateReceiver != null )
		{
			mContext.registerReceiver( mGprsStateReceiver , new IntentFilter( ConnectivityManager.CONNECTIVITY_ACTION ) );
		}
	}
	
	public void setBluetoothAdapter()
	{
		if( mBluetoothAdapter == null )
		{
			Handler handler = new Handler( mContext.getMainLooper() );
			if( handler != null )
			{
				handler.post( new Runnable() {
					
					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
						if( mBluetoothAdapter != null )
						{
							if( mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON )
							{
								bluetoothState = BLUETOOTH_START;
							}
							else if( mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON || mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF )
							{
								bluetoothState = BLUETOOTH_READY;
							}
							else
							{
								bluetoothState = BLUETOOTH_STOP;
							}
						}
					}
				} );
			}
		}
	}
	
	public void unregisterReceiver()
	{
		mContext.unregisterReceiver( wifiReceiver );
		mContext.unregisterReceiver( gpsReceiver );
	}
	
	public void toggleWifi()
	{
		if( getWifiState() )
		{
			mWifiManager.setWifiEnabled( false );
		}
		else
		{
			mWifiManager.setWifiEnabled( true );
		}
	}
	
	public boolean getWifiState()
	{
		if( mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED )
		{
			return true;
		}
		else if( mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private final class WifiStateReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context c ,
				Intent intent )
		{
			int statusInt = -1;
			Bundle bundle = intent.getExtras();
			if( bundle != null )
			{
				statusInt = bundle.getInt( "wifi_state" );
			}
			switch( statusInt )
			{
				case WifiManager.WIFI_STATE_UNKNOWN:
				{
					Log.v( TAG , "wifi state: WIFI_STATE_UNKNOWN" );
				}
					break;
				case WifiManager.WIFI_STATE_ENABLED:
					if( mCallback != null )
						mCallback.onWifiStateChanged( true );
					break;
				case WifiManager.WIFI_STATE_DISABLED:
					if( mCallback != null )
						mCallback.onWifiStateChanged( false );
					break;
				default:
					break;
			}
		}
	}
	
	public void toggleGPS()
	{
		Intent intent = new Intent();
		intent.setClassName( "com.android.settings" , "com.android.settings.widget.SettingsAppWidgetProvider" );
		intent.addCategory( "android.intent.category.ALTERNATIVE" );
		intent.setData( Uri.parse( "custom:3" ) );
		mContext.sendBroadcast( intent );
	}
	
	public boolean getGpsState()
	{
		if( mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )//if( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) || !mLocationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) )
		{
			return true;
		}
		return false;
	}
	
	private final class GpsStateReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context c ,
				Intent intent )
		{
			String action = intent.getAction();
			Log.v( TAG , " action " + action );
			if( LocationManager.PROVIDERS_CHANGED_ACTION.equals( action ) )
			{
				if( mCallback != null )
					mCallback.onGpsStateChanged( getGpsState() );
			}
		}
	}
	
	public static interface SwitchHelperCallBack
	{
		
		public void onWifiStateChanged(
				boolean state );
		
		public void onGpsStateChanged(
				boolean state );
		
		public void onBluetoothStateChanged(
				int state );
		
		public void onBrightnessChanged(
				int state );
		
		public void onGPRSNetChanged(
				boolean state );
	}
	
	public int getBlueToothState()
	{
		if( mBluetoothAdapter != null )
		{
			if( mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON )
			{
				bluetoothState = BLUETOOTH_START;
			}
			else if( mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON || mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF )
			{
				bluetoothState = BLUETOOTH_READY;
			}
			else
			{
				bluetoothState = BLUETOOTH_STOP;
			}
		}
		return bluetoothState;
	}
	
	public void toggleBlueTooth()
	{
		if( mBluetoothAdapter != null )
		{
			if( mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON )
			{
				mBluetoothAdapter.disable();
				Log.d( "Broadcast" , "off" );
			}
			else if( mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF )
			{
				Log.d( "Broadcast" , "on" );
				mBluetoothAdapter.enable();
			}
		}
	}
	
	private final class BlueToothStateReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context c ,
				Intent intent )
		{
			String action = intent.getAction();
			if( bluetoothAction.equals( action ) )
			{
				if( mCallback != null )
					mCallback.onBluetoothStateChanged( getBlueToothState() );
			}
		}
	}
	
	public void setBrightness(
			final int brightness )
	{
		mBrightness = brightness;
		if( mBrightness == 0 )
		{
			mBrightness = 5;
		}
		if( mContext.getContentResolver() != null )
		{
			if( isAutoBrightness( mContext.getContentResolver() ) )
			{
				stopAutoBrightness( mContext.getContentResolver() );
			}
			Handler handler = new Handler( mContext.getMainLooper() );
			if( handler != null )
			{
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
			}
			setScreenBrightness( mContext.getContentResolver() , brightness );
		}
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
	
	public void adjustBrightness()
	{
		int brightness = getScreenBrightness( mContext.getContentResolver() );
		if( isAutoBrightness( mContext.getContentResolver() ) )
		{
			brightness = 5;
			if( mCallback != null )
				mCallback.onBrightnessChanged( BRIGHTNESS_LOW );
		}
		else if( brightness <= 20 )
		{
			brightness = 255 / 2;
			if( mCallback != null )
				mCallback.onBrightnessChanged( BRIGHTNESS_MIDDLE );
		}
		else if( brightness >= 240 )
		{
			startAutoBrightness( mContext.getContentResolver() );
			if( mCallback != null )
				mCallback.onBrightnessChanged( BRIGHTNESS_AUTO );
			return;
		}
		else
		{
			brightness = 255;
			if( mCallback != null )
				mCallback.onBrightnessChanged( BRIGHTNESS_HIGH );
		}
		setBrightness( brightness );
		if( mSb != null )
		{
			mSb.setProgress( brightness );
		}
	}
	
	public int getBrightnessState()
	{
		int brightness = getScreenBrightness( mContext.getContentResolver() );
		if( isAutoBrightness( mContext.getContentResolver() ) )
		{
			return BRIGHTNESS_AUTO;
		}
		else if( brightness <= 20 )
		{
			return BRIGHTNESS_LOW;
		}
		else if( brightness <= 240 )
		{
			return BRIGHTNESS_MIDDLE;
		}
		else
			//if(brightness<=20){
			return BRIGHTNESS_HIGH;
	}
	
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
	
	public void showCustomToast(
			final String word )
	{
		Handler handler = new Handler( mContext.getMainLooper() );
		if( handler != null )
		{
			handler.post( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					Toast.makeText( mContext , word , Toast.LENGTH_SHORT ).show();
				}
			} );
		}
	}
	
	public final void setMobileNetEnable()
	{
		Object[] arg = null;
		try
		{
			boolean isMobileDataEnable = invokeMethod( "getMobileDataEnabled" , arg );
			if( !isMobileDataEnable )
			{
				invokeBooleanArgMethod( "setMobileDataEnabled" , true );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public boolean invokeMethod(
			String methodName ,
			Object[] arg ) throws Exception
	{
		ConnectivityManager mConnectivityManager = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		Class ownerClass = mConnectivityManager.getClass();
		Class[] argsClass = null;
		if( arg != null )
		{
			argsClass = new Class[1];
			argsClass[0] = arg.getClass();
		}
		Method method = ownerClass.getMethod( methodName , argsClass );
		Boolean isOpen = (Boolean)method.invoke( mConnectivityManager , arg );
		return isOpen;
	}
	
	public final void setMobileNetUnable()
	{
		Object[] arg = null;
		try
		{
			boolean isMobileDataEnable = invokeMethod( "getMobileDataEnabled" , arg );
			if( isMobileDataEnable )
			{
				invokeBooleanArgMethod( "setMobileDataEnabled" , false );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public Object invokeBooleanArgMethod(
			String methodName ,
			boolean value ) throws Exception
	{
		ConnectivityManager mConnectivityManager = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		Class ownerClass = mConnectivityManager.getClass();
		Class[] argsClass = new Class[1];
		argsClass[0] = boolean.class;
		Method method = ownerClass.getMethod( methodName , argsClass );
		return method.invoke( mConnectivityManager , value );
	}
	
	private final class GPRSStateReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context c ,
				Intent intent )
		{
			String action = intent.getAction();
			if( action.equals( ConnectivityManager.CONNECTIVITY_ACTION ) )
			{
				if( mCallback != null )
					mCallback.onGPRSNetChanged( getDataState() );
			}
		}
	}
	
	public void toggleNetData()
	{
		Log.d( "apn mode" , "ok" );
		NetworkInfo.State localState = ( (ConnectivityManager)mContext.getSystemService( "connectivity" ) ).getNetworkInfo( 0 ).getState();
		if( localState == NetworkInfo.State.CONNECTED )
		{
			Log.d( "apn select" , "CONNECTED" );
			setMobileNetUnable();
		}
		else if( localState == NetworkInfo.State.DISCONNECTED )
		{
			Log.d( "apn select" , "DISCONNECTED" );
			setMobileNetEnable();
		}
		else if( localState == NetworkInfo.State.UNKNOWN )
		{
			Log.d( "apn select" , "UNKNOWN" );
			//weijie_21121123_02 这里是一个临时的解决方案
			//其实在 Widg额头Knife。java中weijie_notice1的地方，将WIDGET的CONTEXT传下来，让各个注册来的比较好
			Context otherContext = null;
			try
			{
				otherContext = mContext.createPackageContext( "com.iLoong.Shortcuts" , Context.CONTEXT_IGNORE_SECURITY );
				//				showToast(mContext,otherContext.getResources().getString(R.string.gprs_err));
			}
			catch( NameNotFoundException e )
			{
				// TODO Auto-generated catch block
				//				showToast(mContext,"gprs error");
				e.printStackTrace();
			}
			//showToast(mContext,otherContext.getResources().getString(R.string.gps_err));
		}
		else if( localState == NetworkInfo.State.CONNECTING )
		{
			Log.d( "apn select" , "CONNECTING" );
		}
		else if( localState == NetworkInfo.State.DISCONNECTING )
		{
			Log.d( "apn select" , "DISCONNECTING" );
		}
		else if( localState == NetworkInfo.State.SUSPENDED )
		{
			Log.d( "apn select" , "SUSPENDED" );
		}
	}
	
	public boolean getDataState()
	{
		// 获得网络连接服务
		ConnectivityManager connManager = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		State state = null;
		if( connManager != null && connManager.getNetworkInfo( ConnectivityManager.TYPE_MOBILE ) != null)
		{
			state = connManager.getNetworkInfo( ConnectivityManager.TYPE_MOBILE ).getState(); // 获取网络连接状态
		}
		if( state == null )
		{
			return false;
		}
		//		Log.d( "Gprs State" , "" + state );
		if( State.UNKNOWN == state )
		{ // 判断是否正在使用GPRS网络
			return false;
		}
		else if( state == NetworkInfo.State.CONNECTED )
		{
			Log.d( "apn state" , "CONNECTED" );
			return true;
		}
		else if( state == NetworkInfo.State.DISCONNECTED )
		{
			Log.d( "apn state" , "DISCONNECT" );
			return false;
		}
		else if( state == NetworkInfo.State.DISCONNECTING || state == NetworkInfo.State.CONNECTING )
		{
			Log.d( "apn state" , "DISCONNECTING || CONNECTING" );
			return true;
		}
		return false;
	}
	
	public void entryWirelessSettings()
	{
		startSpecifiedIntent( android.provider.Settings.ACTION_WIFI_SETTINGS , iLoongLauncher.ACTION_WIFI_SETTINGS );
	}
	
	public void entryBlueToothSettings()
	{
		startSpecifiedIntent( android.provider.Settings.ACTION_BLUETOOTH_SETTINGS , iLoongLauncher.ACTION_BLUETOOTH_SETTINGS );
	}
	
	public void entryAPNSettings()
	{
		startSpecifiedIntent( android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS , iLoongLauncher.ACTION_DATA_ROAMING_SETTINGS );
	}
	
	public void entryGPSSettings()
	{
		startSpecifiedIntent( android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS , iLoongLauncher.ACTION_LOCATION_SOURCE_SETTINGS );
	}
	
	public void entryBrightSettings()
	{
		startSpecifiedIntent( Settings.ACTION_DISPLAY_SETTINGS , iLoongLauncher.ACTION_DISPLAY_SETTINGS );
	}
	
	public void startSpecifiedIntent(
			String action ,
			int requestCode )
	{
		Intent intent = new Intent( action , null );
		final PackageManager mPackageManager = mContext.getPackageManager();
		ArrayList<ResolveInfo> mAppsResolve = (ArrayList)mPackageManager.queryIntentActivities( intent , requestCode );
		if( mAppsResolve == null || mAppsResolve.size() == 0 )
		{
			SendMsgToAndroid.sendOurToastMsg( "该引用没有找到" );
		}
		else
		{
			intent.putExtra( "aaaaa" , "xxxxxx" );
			SendMsgToAndroid.vibrator( 10 );
			iLoongLauncher.getInstance().d3dListener.d3d.mLeaveDesktop = true;
			iLoongLauncher.getInstance().startActivityForResult( intent , requestCode );
		}
	}
}
