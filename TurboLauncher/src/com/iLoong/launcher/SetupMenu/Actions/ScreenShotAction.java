package com.iLoong.launcher.SetupMenu.Actions;


import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.theme.ThemeManagerActivity;


public class ScreenShotAction extends Action
{
	
	public ScreenShotAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		putIntentAction( SetupMenu.getContext() , ThemeManagerActivity.class );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_SCREENSHOT , new ScreenShotAction( ActionSetting.ACTION_SCREENSHOT , ScreenShotAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		if( SetupMenu.getInstance() != null )
			SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
	}
	
	@Override
	protected void OnActionFinish()
	{
	}
	
	@Override
	protected void OnPutValue(
			String key )
	{
	}
}
