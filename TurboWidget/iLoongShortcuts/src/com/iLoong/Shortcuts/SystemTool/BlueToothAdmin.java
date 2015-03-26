package com.iLoong.Shortcuts.SystemTool;


import com.iLoong.Shortcuts.R;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


public class BlueToothAdmin implements ISystemAdmin
{
	
	// private Object lock = new Object();
	private static BluetoothAdapter bluetoothadapter;
	private BlueToothStateReceiver mBluetoothStateReceiver;
	private Context mContext;
	private final static int BLUETOOTH_START = 0;
	private final static int BLUETOOTH_STOP = 1;
	private final static int BLUETOOTH_READY = 2;
	private int state = BLUETOOTH_STOP;
	String key;
	
	public BlueToothAdmin(
			Context context ,
			String key ,
			IAdminCallback callback )
	{
		this.key = key;
		Changecallback = callback;
		mBluetoothStateReceiver = new BlueToothStateReceiver();
		IntentFilter filter = new IntentFilter( "android.bluetooth.adapter.action.STATE_CHANGED" );
		context.registerReceiver( mBluetoothStateReceiver , filter ); // 不要忘了之后解除绑定
		mContext = context;
		if( bluetoothadapter == null )
		{
			Handler handler = new Handler( mContext.getMainLooper() );
			handler.post( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
					if( bluetoothadapter == null )
					{
						//weijie_21121123_02 这里是一个临时的解决方案
						//其实在 Widg额头Knife。java中weijie_notice1的地方，将WIDGET的CONTEXT传下来，让各个注册来的比较好
						Context otherContext = null;
						try
						{
							otherContext = mContext.createPackageContext( "com.iLoong.Shortcuts" , Context.CONTEXT_IGNORE_SECURITY );
							showToast( mContext , otherContext.getResources().getString( R.string.bluetooth_err ) );
						}
						catch( NameNotFoundException e )
						{
							// TODO Auto-generated catch block
							showToast( mContext , "bluetooth error" );
							e.printStackTrace();
						}
						//showToast(mContext,mContext.getResources().getString(R.string.bluetooth_err));
					}
					else
					{
						if( bluetoothadapter.getState() == BluetoothAdapter.STATE_ON )
						{
							state = BLUETOOTH_START;
						}
						else if( bluetoothadapter.getState() == BluetoothAdapter.STATE_TURNING_ON || bluetoothadapter.getState() == BluetoothAdapter.STATE_TURNING_OFF )
						{
							state = BLUETOOTH_READY;
						}
						else
						{
							state = BLUETOOTH_STOP;
						}
					}
				}
			} );
		}
	}
	
	public void onDelete()
	{
		mContext.unregisterReceiver( mBluetoothStateReceiver );
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
	
	public boolean getState()
	{
		if( bluetoothadapter == null )
		{
			Handler handler = new Handler( mContext.getMainLooper() );
			handler.post( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
					if( bluetoothadapter == null )
					{
						//weijie_21121123_02 这里是一个临时的解决方案
						//其实在 Widg额头Knife。java中weijie_notice1的地方，将WIDGET的CONTEXT传下来，让各个注册来的比较好
						Context otherContext = null;
						try
						{
							otherContext = mContext.createPackageContext( "com.iLoong.Shortcuts" , Context.CONTEXT_IGNORE_SECURITY );
							showToast( mContext , otherContext.getResources().getString( R.string.bluetooth_err ) );
						}
						catch( NameNotFoundException e )
						{
							// TODO Auto-generated catch block
							showToast( mContext , "bluetooth error" );
							e.printStackTrace();
						}
						//showToast(mContext,mContext.getResources().getString(R.string.bluetooth_err));
					}
					else
					{
						if( bluetoothadapter.getState() == BluetoothAdapter.STATE_ON )
						{
							state = BLUETOOTH_START;
						}
						else if( bluetoothadapter.getState() == BluetoothAdapter.STATE_TURNING_ON || bluetoothadapter.getState() == BluetoothAdapter.STATE_TURNING_OFF )
						{
							state = BLUETOOTH_READY;
						}
						else
						{
							state = BLUETOOTH_STOP;
						}
					}
				}
			} );
		}
		else
		{
			if( bluetoothadapter.getState() == BluetoothAdapter.STATE_ON )
			{
				state = BLUETOOTH_START;
			}
			else if( bluetoothadapter.getState() == BluetoothAdapter.STATE_TURNING_ON || bluetoothadapter.getState() == BluetoothAdapter.STATE_TURNING_OFF )
			{
				state = BLUETOOTH_READY;
			}
			else
			{
				state = BLUETOOTH_STOP;
			}
		}
		if( state == BLUETOOTH_START )
		{
			return true;
		}
		else if( state == BLUETOOTH_READY )
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
		if( bluetoothadapter != null )
		{
			if( bluetoothadapter.getState() == BluetoothAdapter.STATE_ON )
			{
				bluetoothadapter.disable();
				Log.d( "Broadcast" , "off" );
			}
			else if( bluetoothadapter.getState() == BluetoothAdapter.STATE_OFF )
			{
				Log.d( "Broadcast" , "on" );
				bluetoothadapter.enable();
			}
		}
	}
	
	/**
	 * 监控BlueTooth状态的广播接收器
	 */
	private final class BlueToothStateReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context c ,
				Intent intent )
		{
			String action = intent.getAction();
			if( "android.bluetooth.adapter.action.STATE_CHANGED".equals( action ) )
			{
				Log.d( "Broadcast" , "bluetooth" );
				if( Changecallback != null )
					Changecallback.StateChange( key );
			}
		}
	}
	
	IAdminCallback Changecallback;
	
	public void setCallback(
			IAdminCallback callback )
	{
		Changecallback = callback;
	}
}
