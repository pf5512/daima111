package com.iLoong.Shortcuts.SystemTool;


import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.Uri;


// 有些问题
public class GpsAdmin implements ISystemAdmin
{
	
	private LocationManager mLocationManager;
	private GpsStateReceiver mGpsStateReceiver;
	private boolean isNeizhi = false;
	private Context mContext;
	public String key;
	
	public GpsAdmin(
			Context context ,
			String key ,
			IAdminCallback callback )
	{
		mLocationManager = (LocationManager)context.getSystemService( Context.LOCATION_SERVICE );
		Changecallback = callback;
		mGpsStateReceiver = new GpsStateReceiver();
		context.registerReceiver( mGpsStateReceiver , new IntentFilter( LocationManager.PROVIDERS_CHANGED_ACTION ) );
		mContext = context;
		isNeizhi = ( context.getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0;
		this.key = key;
	}
	
	public void onDelete()
	{
		mContext.unregisterReceiver( mGpsStateReceiver );
	}
	
	public boolean getReadyState()
	{
		return getState();
	}
	
	public boolean getState()
	{
		if( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) || !mLocationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) )
		{
			return false;
		}
		return true;
	}
	
	public void select()
	{
		if( isNeizhi == false )
		{
			Intent callGPSSettingIntent = new Intent( android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS );
			mContext.startActivity( callGPSSettingIntent );
		}
		else
		{
			toggleGPS();
		}
	}
	
	private void toggleGPS()
	{
		Intent gpsIntent = new Intent();
		gpsIntent.setClassName( "com.android.settings" , "com.android.settings.widget.SettingsAppWidgetProvider" );
		gpsIntent.addCategory( "android.intent.category.ALTERNATIVE" );
		gpsIntent.setData( Uri.parse( "custom:3" ) );
		try
		{
			PendingIntent.getBroadcast( mContext , 0 , gpsIntent , 0 ).send();
		}
		catch( CanceledException e )
		{
			e.printStackTrace();
		}
	}
	
	private final class GpsStateReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context c ,
				Intent intent )
		{
			String action = intent.getAction();
			if( LocationManager.PROVIDERS_CHANGED_ACTION.equals( action ) )
			{
				if( Changecallback != null )
					Changecallback.StateChange( key );
			}
		}
	}
	
	IAdminCallback Changecallback;
	
	@Override
	public void setCallback(
			IAdminCallback callback )
	{
		Changecallback = callback;
	}
}
