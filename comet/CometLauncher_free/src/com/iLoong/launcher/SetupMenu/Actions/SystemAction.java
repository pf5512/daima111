package com.iLoong.launcher.SetupMenu.Actions;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class SystemAction extends Action
{
	
	public SystemAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_SYSTEM_SETTINGS , getAction( ActionSetting.ACTION_SYSTEM_SETTINGS ) );
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_SOFTWARE_MANAGEMENT , getAction( ActionSetting.ACTION_SOFTWARE_MANAGEMENT ) );
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_WALLPAPER , getAction( ActionSetting.ACTION_WALLPAPER ) );
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_LOCK_SCREEN , getAction( ActionSetting.ACTION_LOCK_SCREEN ) );
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_RESTART , getAction( ActionSetting.ACTION_RESTART ) );
	}
	
	private static Action getAction(
			int actionid )
	{
		final String action;
		switch( actionid )
		{
			case ActionSetting.ACTION_SYSTEM_SETTINGS:
				action = android.provider.Settings.ACTION_SETTINGS;
				break;
			case ActionSetting.ACTION_SOFTWARE_MANAGEMENT:
				action = android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS;
				break;
			case ActionSetting.ACTION_WALLPAPER:
				action = Intent.ACTION_SET_WALLPAPER;
				break;
			case ActionSetting.ACTION_LOCK_SCREEN:
				action = Intent.ACTION_SCREEN_OFF;
				break;
			case ActionSetting.ACTION_RESTART:
				action = "ACTION_RESTART";
				break;
			default:
				action = null;
		}
		if( action != null )
			return new SystemAction( actionid , action );
		return null;
	}
	
	@Override
	public void OnRunAction()
	{
		switch( mActionID )
		{
			case ActionSetting.ACTION_SYSTEM_SETTINGS:
				AsynRunAction();
				break;
			case ActionSetting.ACTION_SOFTWARE_MANAGEMENT:
				SynRunAction();
				break;
			case ActionSetting.ACTION_WALLPAPER:
				iLoongLauncher.getInstance().startWallpaper();
				break;
			case ActionSetting.ACTION_LOCK_SCREEN:
				LockScreen();
				break;
			case ActionSetting.ACTION_RESTART:
				RestartSystem();
				break;
			default:
				break;
		}
	}
	
	@Override
	public void OnActionFinish()
	{
	}
	
	@Override
	protected void OnPutValue(
			String key )
	{
	}
	
	private void LockScreen()
	{
		DevicePolicyManager mDPM = (DevicePolicyManager)SetupMenu.getContext().getSystemService( Activity.DEVICE_POLICY_SERVICE );
		ComponentName mAdminName = new ComponentName( SetupMenu.getContext() , LockScreenAdmin.class );
		if( mDPM.isAdminActive( mAdminName ) )
		{
			mDPM.lockNow();
		}
		else
		{
			Intent localIntent = new Intent( DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN );
			localIntent.putExtra( DevicePolicyManager.EXTRA_DEVICE_ADMIN , mAdminName );
			localIntent.putExtra( DevicePolicyManager.EXTRA_ADD_EXPLANATION , "" );
			SetupMenu.getContext().startActivity( localIntent );
		}
	}
	
	public static class ResestActivity extends Activity
	{
		
		public void onCreate(
				Bundle bundle )
		{
			super.onCreate( bundle );
		}
		
		public void onStart()
		{
			if( iLoongLauncher.getInstance() != null )
				iLoongLauncher.getInstance().finish();
			final Intent intent = new Intent();
			intent.setClass( this , iLoongLauncher.class );
			intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
			startActivity( intent );
			finish();
			System.exit( 0 );
		}
	}
	
	public static void RestartSystem()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		prefs.edit().putBoolean( iLoongLauncher.RUNTIME_STATE_RESTART , true ).commit();
		prefs.edit().putBoolean( iLoongLauncher.RUNTIME_STATE_SHOWALLAPP , !iLoongLauncher.getInstance().isWorkspaceVisible() ).commit();
		Intent startMain = new Intent();
		startMain.setClass( SetupMenu.getContext() , ResestActivity.class );
		SetupMenu.getContext().startActivity( startMain );
	}
	
	public static class LockScreenAdmin extends DeviceAdminReceiver
	{
		
		@Override
		public void onEnabled(
				Context context ,
				Intent intent )
		{
		}
		
		@Override
		public void onDisabled(
				Context context ,
				Intent intent )
		{
		}
	}
}
