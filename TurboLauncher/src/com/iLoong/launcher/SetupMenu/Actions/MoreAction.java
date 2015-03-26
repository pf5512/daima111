package com.iLoong.launcher.SetupMenu.Actions;


import com.iLoong.launcher.desktop.iLoongLauncher;


public class MoreAction extends Action
{
	
	public MoreAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
	}
	
	@Override
	protected void OnRunAction()
	{
		//Jone
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_MORE , new MoreAction( ActionSetting.ACTION_MORE , MoreAction.class.getName() ) );
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
