package com.iLoong.launcher.update;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;


public class UpdateActivity extends Activity
{
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		// TODO Auto-generated method stub
		super.onCreate( savedInstanceState );
		UpdateManager updateManager = new UpdateManager( UpdateActivity.this );
		String name = getIntent().getStringExtra( UpdateManager.EXTRA_PARAM );
		SharedPreferences prefs = this.getSharedPreferences( "launcher" , Activity.MODE_PRIVATE );
		if( UpdateManager.EXTRA_VALUE_SERVICE.equals( name ) )
		{
			if( !updateManager.DialogIsShow() )
			{
				updateManager.showNoticeDialog( true );
			}
		}
	}
}
