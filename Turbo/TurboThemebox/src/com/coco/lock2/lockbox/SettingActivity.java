package com.coco.lock2.lockbox;


import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.iLoong.base.themebox.R;


public class SettingActivity extends PreferenceActivity
{
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		addPreferencesFromResource( R.xml.lock_setting );
		startService( new Intent( this , LockService.class ) );
		// 锁定home键
		Preference lockHome = findPreference( StaticClass.LOCK_HOME );
		lockHome.setOnPreferenceClickListener( new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(
					Preference preference )
			{
				Intent intent = new Intent();
				intent.setClass( SettingActivity.this , LockHomeSettingActivity.class );
				startActivity( intent );
				return false;
			}
		} );
	}
}
