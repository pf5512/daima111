package com.iLoong.launcher.SetupMenu.Actions;


import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.theme.ThemeManagerActivity;


public class InstallHelpAction extends Action
{
	
	public InstallHelpAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		putIntentAction( SetupMenu.getContext() , ThemeManagerActivity.class );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_INSTALL_HELP , new InstallHelpAction( ActionSetting.ACTION_INSTALL_HELP , InstallHelpAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		Log.v( "launcher" , "ACTION_INSTALL_HELP" );
		if( SetupMenu.getInstance() != null )
			SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
		SetupMenuActions.getInstance().ActivityFinish( ActionSetting.ACTION_INSTALL_HELP );
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
