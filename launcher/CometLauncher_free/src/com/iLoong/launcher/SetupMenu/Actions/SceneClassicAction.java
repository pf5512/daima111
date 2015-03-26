// wanghongjian add whole file //enable_DefaultScene
package com.iLoong.launcher.SetupMenu.Actions;


import com.iLoong.launcher.SetupMenu.SetupMenu;


public class SceneClassicAction extends Action
{
	
	public SceneClassicAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		// putIntentAction(SetupMenu.getContext(),
		// com.iLoong.launcher.theme.ThemeManagerActivity.class);
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_CLASSIC_LAUNCHER , new SceneClassicAction( ActionSetting.ACTION_CLASSIC_LAUNCHER , SceneClassicAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		// TODO Auto-generated method stub
		SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
		SetupMenuActions.getInstance().ActivityFinish( ActionSetting.ACTION_CLASSIC_LAUNCHER );
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
