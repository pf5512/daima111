//wanghongjian add whole file	//enable_DefaultScene
package com.iLoong.launcher.SetupMenu.Actions;

import com.iLoong.launcher.SetupMenu.SetupMenu;

public class ChangeSceneAction extends Action {

	public ChangeSceneAction(int actionid, String action) {
		super(actionid, action);
		// putIntentAction(SetupMenu.getContext(),
		// com.iLoong.launcher.theme.ThemeManagerActivity.class);
		putIntentAction(SetupMenu.getContext(),
				com.iLoong.launcher.scene.SceneManagerActivity.class);
	}

	public static void Init() {
		SetupMenuActions.getInstance().RegisterAction(
				ActionSetting.ACTION_CHANGE_SCENE,
				new ChangeSceneAction(ActionSetting.ACTION_CHANGE_SCENE,
						ChangeSceneAction.class.getName()));
	}

	@Override
	protected void OnRunAction() {
		// TODO Auto-generated method stub
		SynRunAction();
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
