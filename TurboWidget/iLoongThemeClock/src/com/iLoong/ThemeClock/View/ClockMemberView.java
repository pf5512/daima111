package com.iLoong.ThemeClock.View;

import android.util.Log;

import com.iLoong.Widget3D.BaseView.PluginViewObject3D;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class ClockMemberView extends PluginViewObject3D{

	private MainAppContext mAppContext;
	
	public ClockMemberView(MainAppContext appContext, String name,
			String textureName, String objName) {
		super(appContext, name, textureName, objName);
		mAppContext = appContext;
		this.setSize(WidgetClock.MODEL_WIDTH, WidgetClock.MODEL_HEIGHT);
		Log.v("wangjing", "ClockMemberView "+this.name+" this width: "+this.width+", height: "+this.height);
		this.setOrigin(WidgetClock.MODEL_WIDTH/2, WidgetClock.MODEL_HEIGHT/2);//liuhailin
		this.setMoveOffset(WidgetClock.MODEL_WIDTH/2, WidgetClock.MODEL_HEIGHT/2, 0);
		this.setDepthMode(true);
		this.enableCullFace(true);
		super.build();
	}

}
