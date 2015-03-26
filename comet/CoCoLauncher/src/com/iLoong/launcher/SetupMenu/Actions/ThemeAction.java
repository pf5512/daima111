package com.iLoong.launcher.SetupMenu.Actions;

import com.coco.theme.themebox.MainActivity;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.desktop.FeatureConfig;
import com.iLoong.launcher.theme.ThemeManagerActivity;

public class ThemeAction extends Action {

	public ThemeAction(int actionid, String action) {
		super(actionid, action);
		if(!FeatureConfig.enable_themebox)putIntentAction(SetupMenu.getContext(), com.iLoong.launcher.theme.ThemeManagerActivity.class);
		else putIntentAction(SetupMenu.getContext(),MainActivity.class);
	}
	
	public static void Init(){
		SetupMenuActions.getInstance().RegisterAction(ActionSetting.ACTION_THEME, 
				new ThemeAction(ActionSetting.ACTION_THEME, ThemeAction.class.getName()));
	}		
	
	
	@Override
	protected void OnRunAction() {
		SynRunAction();
	}

	@Override
	protected void OnActionFinish() {
		
	}

	@Override
	protected void OnPutValue(String key) {
		
	}

}
