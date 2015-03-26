package com.iLoong.launcher.SetupMenu.Actions;

import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.theme.ThemeManagerActivity;

public class UpdateAction extends Action {

	public UpdateAction(int actionid, String action) {
		super(actionid, action);
		putIntentAction(SetupMenu.getContext(), ThemeManagerActivity.class);
	}
	
	public static void Init(){
		SetupMenuActions.getInstance().RegisterAction(ActionSetting.ACTION_UPDATE, 
				new UpdateAction(ActionSetting.ACTION_UPDATE, UpdateAction.class.getName()));
	}		
	
	
	@Override
	protected void OnRunAction() {
		SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
		UpdateManager.getInstance().checkClientVersion();
	}

	@Override
	protected void OnActionFinish() {
		
	}

	@Override
	protected void OnPutValue(String key) {
		
	}

	
	
}




