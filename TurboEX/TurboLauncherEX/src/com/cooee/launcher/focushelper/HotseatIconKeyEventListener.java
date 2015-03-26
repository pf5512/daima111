package com.cooee.launcher.focushelper;

import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.View;

/**
 * A keyboard listener we set on all the hotseat buttons.
 */
public class HotseatIconKeyEventListener implements View.OnKeyListener {
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		final Configuration configuration = v.getResources().getConfiguration();
		return FocusHelper.handleHotseatButtonKeyEvent(v, keyCode, event,
				configuration.orientation);
	}
}