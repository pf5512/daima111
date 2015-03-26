package com.iLoong.launcher.desktop;


import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.Actions.DesktopAction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;


/**
 * 
 * @author xujia add
 * 
 */
public class SingleHandlerReceiver extends BroadcastReceiver
{
	
	@Override
	public void onReceive(
			Context context ,
			Intent intent )
	{
		PreferenceActivity preferenceActivity = (PreferenceActivity)( DesktopAction.instanceDesktopSettingActivity );
		CheckBoxPreference singleHandlerFlag = (CheckBoxPreference)preferenceActivity.findPreference( DesktopAction.DesktopSettingActivity.getKey( RR.string.setting_single_handler_flag ) );
		singleHandlerFlag.setChecked( intent.getBooleanExtra( "singleHandler_config_flag" , false ) );
	}
}
