package com.iLoong.Shortcuts.SystemTool;


import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


// 未实现
public class ApnAdmin implements ISystemAdmin
{
	
	Context mContext;
	ConnectivityManager mConnectivityManager;
	private GPRSStateReceiver mGprsStateReceiver;
	public String key;
	
	public ApnAdmin(
			Context context ,
			String key ,
			IAdminCallback callback )
	{
		this.key = key;
		//loonglunch
		mContext = context;
		changecallback = callback;
		mGprsStateReceiver = new GPRSStateReceiver();
		//注册广播接收器，只监听gprs网络状态的改变
		context.registerReceiver( mGprsStateReceiver , new IntentFilter( ConnectivityManager.CONNECTIVITY_ACTION ) );
	}
	
	public void onDelete()
	{
		mContext.unregisterReceiver( mGprsStateReceiver );
	}
	
	public boolean getState()
	{
		// 获得网络连接服务
		ConnectivityManager connManager = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		State state = null;
		if( connManager != null )
		{
			state = connManager.getNetworkInfo( ConnectivityManager.TYPE_MOBILE ).getState(); // 获取网络连接状态
		}
		Log.d( "Gprs State" , "" + state );
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
	
	public void select()
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
	
	public final void setMobileNetEnable()
	{
		mConnectivityManager = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
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
	
	public final void setMobileNetUnable()
	{
		mConnectivityManager = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
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
				if( changecallback != null )
					changecallback.StateChange( key );
			}
		}
	}
	
	@Override
	public boolean getReadyState()
	{
		// TODO Auto-generated method stub
		return getState();
	}
	
	IAdminCallback changecallback;
	
	public void setCallback(
			IAdminCallback callback )
	{
		changecallback = callback;
	}
}
