package com.iLoong.launcher.desktop;


import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import com.iLoong.launcher.Desktop3D.Log;


public class AdminReceiver extends DeviceAdminReceiver
{
	
	@Override
	public void onReceive(
			Context c ,
			Intent intent )
	{
		Log.d( "lockNow" , "ok" );
	}
}
