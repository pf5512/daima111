package com.iLoong.launcher.SetupMenu;

import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.SetupMenu.SetupMenu.SetupMenuItem;
import com.iLoong.launcher.cling.ClingManager;

import android.graphics.Color;
import com.iLoong.launcher.Desktop3D.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class SetupMenuControl implements View.OnClickListener, View.OnTouchListener {

	private SetMenuDesktop mMenuDesktop;

	public SetupMenuControl(SetMenuDesktop menudesk) {
		mMenuDesktop = menudesk;
	}

	@Override
	public void onClick(View v) {
		if (mMenuDesktop.IsClose())
			return;
		if (v != null) {
			SetupMenuItem item = (SetupMenuItem) v.getTag();
//			if(item.id == ActionSetting.ACTION_DESKTOP_SETTINGS){
//				mMenuDesktop.removeTip();
//			}
			mMenuDesktop.close();
			SetupMenuActions.getInstance().Handle(item.id);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final int action = event.getAction();
		
		if(v== null)
			return false;
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
				((MenuButton) v).ButtonDown();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
				((MenuButton) v).ButtonUp();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		}
		return false;
	}

}
