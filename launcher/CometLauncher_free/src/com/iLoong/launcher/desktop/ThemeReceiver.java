// enable_themebox
package com.iLoong.launcher.desktop;


import com.iLoong.launcher.SetupMenu.Actions.SystemAction;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.iLoong.launcher.Desktop3D.Log;


public class ThemeReceiver extends BroadcastReceiver
{
	
	public static final String ACTION_LAUNCHER_RESTART = "com.cooeecomet.launcher.restart";
	public static final String ACTION_LAUNCHER_APPLY_THEME = "com.cooeecomet.launcher.apply_theme";
	
	@Override
	public void onReceive(
			Context c ,
			Intent intent )
	{
		Log.d( "launcher" , "intent:" + intent.getAction() );
		if( !FeatureConfig.enable_themebox )
			return;
		final String action = intent.getAction();
		if( action.equals( ACTION_LAUNCHER_RESTART ) )
		{
			if( !iLoongApplication.init )
			{
				final Intent intent2 = new Intent();
				intent2.setClass( c , iLoongLauncher.class );
				intent2.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
				c.startActivity( intent2 );
			}
			else if( iLoongLauncher.getInstance() == null )
			{
				final Intent intent2 = new Intent();
				intent2.setClass( c , iLoongLauncher.class );
				intent2.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
				c.startActivity( intent2 );
				System.exit( 0 );
			}
			else if( iLoongLauncher.getInstance().stoped )
			{
				iLoongApplication.needRestart = true;
			}
			else
				SystemAction.RestartSystem();
			return;
		}
		else if( action.equals( ACTION_LAUNCHER_APPLY_THEME ) )
		{
			SharedPreferences prefs = c.getSharedPreferences( "theme" , Activity.MODE_WORLD_WRITEABLE );
			Log.d( "launcher" , "apply theme start:" + prefs.getString( "theme" , "test1" ) );
			Log.d( "launcher" , "apply theme start:" + prefs.getInt( "theme_status" , -1 ) );
			prefs.edit().putString( "theme" , intent.getStringExtra( "theme" ) ).commit();
			prefs.edit().putString( "class_name" , intent.getStringExtra( "class_name" ) ).commit();
			prefs.edit().putInt( "theme_status" , intent.getIntExtra( "theme_status" , 1 ) ).commit();
			Log.d( "launcher" , "apply theme end:" + prefs.getString( "theme" , "test3" ) );
			Log.d( "launcher" , "apply theme end:" + prefs.getInt( "theme_status" , -1 ) );
			return;
		}
	}
}
//enable_themebox
