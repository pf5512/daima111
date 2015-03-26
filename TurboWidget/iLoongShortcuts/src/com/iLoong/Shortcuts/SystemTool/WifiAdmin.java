package com.iLoong.Shortcuts.SystemTool;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.iLoong.Shortcuts.R;


public class WifiAdmin implements ISystemAdmin
{
	
	private WifiManager mWifiManager;
	private WifiStateReceiver mWifiStateReceiver;
	private Context mContext;
	String key;
	
	public WifiAdmin(
			Context context ,
			String key ,
			IAdminCallback callback )
	{
		this.key = key;
		changecallback = callback;
		mWifiManager = (WifiManager)context.getSystemService( Context.WIFI_SERVICE );
		mWifiStateReceiver = new WifiStateReceiver();
		//注册广播接收器，只监听Wifi网络状态的改变
		context.registerReceiver( mWifiStateReceiver , new IntentFilter( WifiManager.WIFI_STATE_CHANGED_ACTION ) );
		mContext = context;
	}
	
	public void onDelete()
	{
		mContext.unregisterReceiver( mWifiStateReceiver );
	}
	
	public boolean getState()
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
	
	public void select()
	{
		Log.d( "select wifi" , "ok" );
		if( mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED )
		{
			mWifiManager.setWifiEnabled( false );
		}
		else if( mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED )
		{
			mWifiManager.setWifiEnabled( true );
		}
	}
	
	public boolean getReadyState()
	{
		return getState();
	}
	
	public void showToast(
			final Context context ,
			final String word )
	{
		Handler handler = new Handler( context.getMainLooper() );
		handler.post( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				Toast.makeText( context , word , Toast.LENGTH_SHORT ).show();
			}
		} );
	}
	
	/**
	  * 监控Wifi状态的广播接收器
	  * @author Liusy
	  * 2012-6-29
	  */
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
					//weijie_21121123_02 这里是一个临时的解决方案
					//其实在 Widg额头Knife。java中weijie_notice1的地方，将WIDGET的CONTEXT传下来，让各个注册来的比较好
					Context otherContext = null;
					try
					{
						otherContext = mContext.createPackageContext( "com.iLoong.Shortcuts" , Context.CONTEXT_IGNORE_SECURITY );
						showToast( mContext , otherContext.getResources().getString( R.string.wifi_err ) );
					}
					catch( NameNotFoundException e )
					{
						// TODO Auto-generated catch block
						showToast( mContext , "wifi error" );
						e.printStackTrace();
					}
					//   showToast(mContext,mContext.getResources().getString(R.string.wifi_err));
				}
					break;
				//	   case WifiManager.WIFI_STATE_ENABLING:
				case WifiManager.WIFI_STATE_ENABLED:
					//	   case WifiManager.WIFI_STATE_DISABLING:
				case WifiManager.WIFI_STATE_DISABLED:
					if( changecallback != null )
						changecallback.StateChange( key );
					break;
				default:
					break;
			}
		}
	}
	
	IAdminCallback changecallback;
	
	public void setCallback(
			IAdminCallback callback )
	{
		changecallback = callback;
	}
}
