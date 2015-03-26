//xiatian add whole file	//DefaultScene
package com.iLoong.launcher.SetupMenu.Actions;

import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.desktop.FeatureConfig;

public class DefaultSceneAction extends Action {

	public DefaultSceneAction(int actionid, String action) {
		super(actionid, action);
	}

	public static void Init() {
		SetupMenuActions.getInstance().RegisterAction(
				ActionSetting.ACTION_START_SCENE,
				new DefaultSceneAction(ActionSetting.ACTION_START_SCENE,
						DefaultSceneAction.class.getName()));
	}

	@Override
	protected void OnRunAction() {

		// wanghongjian add start //enable_DefaultScene
		if (FeatureConfig.enable_DefaultScene) {
			SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
			SetupMenuActions.getInstance().ActivityFinish(
					ActionSetting.ACTION_START_SCENE);
		}
		// wanghongjian add end

	}

	@Override
	protected void OnActionFinish() {

	}

	@Override
	protected void OnPutValue(String key) {

	}

}
