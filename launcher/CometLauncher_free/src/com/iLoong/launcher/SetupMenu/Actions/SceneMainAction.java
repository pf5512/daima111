// wanghongjian add whole file //enable_DefaultScene
package com.iLoong.launcher.SetupMenu.Actions;


import com.iLoong.launcher.SetupMenu.SetupMenu;


public class SceneMainAction extends Action
{
	
	public SceneMainAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_MAIN_MENU , new SceneMainAction( ActionSetting.ACTION_MAIN_MENU , SceneMainAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		// TODO Auto-generated method stub
		SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
		SetupMenuActions.getInstance().ActivityFinish( ActionSetting.ACTION_MAIN_MENU );
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
