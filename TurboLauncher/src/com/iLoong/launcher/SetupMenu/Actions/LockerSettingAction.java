package com.iLoong.launcher.SetupMenu.Actions;


import android.content.ComponentName;
import android.content.Intent;

import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Actions.DesktopAction.DesktopSettingActivity;


public class LockerSettingAction extends Action
{
	
	public LockerSettingAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		putIntentAction( SetupMenu.getContext() , DesktopSettingActivity.class );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_LOCKER_SETTING , new LockerSettingAction( ActionSetting.ACTION_LOCKER_SETTING , LockerSettingAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		// TODO Auto-generated method stub
		final Intent intent = new Intent();
		intent.setComponent( new ComponentName( "com.cooee.lock.net.cooeelocker" , "com.cooee.lock.net.cooeelocker.VisualSetScreen" ) );
		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
		SetupMenuActions.getInstance().getContext().startActivity( intent );
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
