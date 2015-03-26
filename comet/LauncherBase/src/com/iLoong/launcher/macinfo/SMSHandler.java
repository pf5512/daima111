package com.iLoong.launcher.macinfo;


import com.iLoong.launcher.Desktop3D.Log;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;


public class SMSHandler extends Handler
{
	
	public static final String TAG = "SMSHandler";
	public static final int SERVICE_CENTER_WHAT = 10003;
	private Context mContext;
	
	public SMSHandler(
			Context context )
	{
		super();
		this.mContext = context;
	}
	
	public void handleMessage(
			Message message )
	{
		Log.i( TAG , "handleMessage: " + message );
		if( message.what == SERVICE_CENTER_WHAT )
		{
			String serviceCenter = (String)message.obj;
			saveCenterAddress( mContext , serviceCenter );
		}
	}
	
	private void saveCenterAddress(
			Context context ,
			String serviceCenterAddress )
	{
		SharedPreferences preferences = context.getSharedPreferences( "cooee.appstore.ServiceCenterAddress" , Activity.MODE_PRIVATE );
		SharedPreferences.Editor editor = preferences.edit();
		// editor.putInt("maxid", serviceCenterAddress);
		editor.putString( "centerAddress" , serviceCenterAddress );
		editor.commit();
	}
	
	public static String loadCenterAddress(
			Context context )
	{
		SharedPreferences preferences = context.getSharedPreferences( "cooee.appstore.ServiceCenterAddress" , Activity.MODE_PRIVATE );
		String centerAddress = preferences.getString( "centerAddress" , null );
		return centerAddress;
	}
}
