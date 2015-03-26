//wanghongjian add whole file	//enable_DefaultScene
package com.iLoong.launcher.SetupMenu.Actions;

import com.iLoong.launcher.SetupMenu.SetupMenu;

public class SceneSettingAction extends Action {
	public SceneSettingAction(int actionid, String action) {
		super(actionid, action);
		// putIntentAction(SetupMenu.getContext(),
		// com.iLoong.launcher.theme.ThemeManagerActivity.class);
	}

	public static void Init() {
		SetupMenuActions.getInstance().RegisterAction(
				ActionSetting.ACTION_SETTING_SYSTEM,
				new SceneSettingAction(ActionSetting.ACTION_SETTING_SYSTEM,
						SceneSettingAction.class.getName()));
	}

	@Override
	protected void OnRunAction() {
		// TODO Auto-generated method stub
		SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
		SetupMenuActions.getInstance().ActivityFinish(
				ActionSetting.ACTION_SETTING_SYSTEM);
	}

	@Override
	protected void OnActionFinish() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void OnPutValue(String key) {
		// TODO Auto-generated method stub

	}

}
