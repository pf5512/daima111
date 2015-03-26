package com.iLoong.launcher.desktop;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.iLoong.launcher.Desktop3D.Log;

public class BootCompletedReceiver extends BroadcastReceiver {  
	  @Override
	  public void onReceive(Context c, Intent intent) {
		  //if(iLoongLauncher.getInstance() == null)return;
		  iLoongLauncher.writeBootTime = true;
		  iLoongLauncher.bootTime = System.currentTimeMillis();
		  Log.e("boot","BootCompletedReceiver");
	  }
}
