package com.iLoong.launcher.SetupMenu.Actions;


import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;

import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Actions.BackupDesktopAction.BackupActivity;
import com.iLoong.launcher.SetupMenu.Actions.SystemAction.LockScreenAdmin;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class TestAction extends Action
{
	
	public TestAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_TEST , new TestAction( ActionSetting.ACTION_TEST , TestAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		iLoongLauncher.getInstance().startWallpaper();
	}
	
	@Override
	protected void OnActionFinish()
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void OnPutValue(
			String key )
	{
		// TODO Auto-generated method stub
	}
}
