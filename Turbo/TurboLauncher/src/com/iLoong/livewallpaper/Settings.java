package com.iLoong.livewallpaper;


import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.clean.InstallCleanMasterActivity;
import com.iLoong.launcher.desktop.iLoongLauncher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;


public class Settings extends PreferenceActivity
{
	
	Preference startTurbo = null;
	
	@Override
	protected void onCreate(
			Bundle icicle )
	{
		super.onCreate( icicle );
		addPreferencesFromResource( R.xml.lwp_settings );
		startTurbo = (Preference) findPreference( "preference_launch_launhcer" );
		startTurbo.setOnPreferenceClickListener( new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(
					Preference preference )
			{
				Intent intent = new Intent( iLoongLauncher.getInstance() , iLoongLauncher.class );
				intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				startActivity( intent );
				return true;
			}
		} );
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	public void onSharedPreferenceChanged(
			SharedPreferences sharedPreferences ,
			String key )
	{
	}
	
}
