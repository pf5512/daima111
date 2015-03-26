package com.iLoong.launcher.SetupMenu.Actions;


import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.theme.ThemeManagerActivity;


public class ScreenEditAction extends Action
{
	
	public ScreenEditAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		putIntentAction( SetupMenu.getContext() , ThemeManagerActivity.class );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_SCREEN_EDITING , new ScreenEditAction( ActionSetting.ACTION_SCREEN_EDITING , ScreenEditAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		Log.v( "ScreenEditAction" , "ScreenEditAction" );
		if( SetupMenu.getInstance() != null )
			SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
		SetupMenuActions.getInstance().ActivityFinish( ActionSetting.ACTION_SCREEN_EDITING );
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
