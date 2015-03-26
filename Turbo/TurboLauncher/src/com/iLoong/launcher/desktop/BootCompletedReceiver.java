package com.iLoong.launcher.desktop;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootCompletedReceiver extends BroadcastReceiver
{
	
	@Override
	public void onReceive(
			Context c ,
			Intent intent )
	{
		if( intent.getAction().equals( Intent.ACTION_BOOT_COMPLETED ) )
		{
			iLoongLauncher.writeBootTime = true;
			iLoongLauncher.bootTime = System.currentTimeMillis();
			Intent intent2 = new Intent( c , iLoongLauncher.class );
			intent2.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			c.startActivity( intent2 );
		}
	}
}
