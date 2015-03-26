package com.iLoong.launcher.setting;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.RR;
import com.iLoong.launcher.desktop.DefaultLauncher;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class DefaultSetting extends Activity implements View.OnClickListener
{
	
	static String TAG = "DefaultSetting";
	Button startBtn;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.dssetdefault );
		startBtn = (Button)findViewById( R.id.set_default_start );
		startBtn.setOnClickListener( this );
		//		
	}
	
	public void setDefaultLauncher()
	{
		PackageManager p = getPackageManager();
		ComponentName cN = new ComponentName( iLoongLauncher.getInstance() , DefaultLauncher.class );
		p.setComponentEnabledSetting( cN , PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP );
		Intent selector = new Intent( Intent.ACTION_MAIN );
		selector.addCategory( Intent.CATEGORY_HOME );
		startActivity( selector );
		p.setComponentEnabledSetting( cN , PackageManager.COMPONENT_ENABLED_STATE_DEFAULT , PackageManager.DONT_KILL_APP );
	}
	
	public static boolean isMyLauncher()
	{
		final Intent intent = new Intent( Intent.ACTION_MAIN );
		intent.addCategory( Intent.CATEGORY_HOME );
		final ResolveInfo res = iLoongLauncher.getInstance().getPackageManager().resolveActivity( intent , 0 );
		if( res != null && res.activityInfo == null )
		{
			// should not happen. A home is always installed, isn't it?
		}
		if( "android".equals( res.activityInfo.packageName ) )
		{
			Log.v( TAG , "DEFAULT LAUNCHER IS " + res.activityInfo.packageName );
		}
		else
		{
			if( res.activityInfo.packageName.equals( RR.getPackageName() ) )
			{
				return true;
			}
			Log.v( TAG , "res.activityInfo.packageName " + res.activityInfo.packageName );
			//
		}
		return false;
	}
	
	public void resetDefault()
	{
		PackageManager pm = getPackageManager();
		ComponentName cN = new ComponentName( iLoongLauncher.getInstance() , iLoongLauncher.class );
		int flag = ( ( pm.getComponentEnabledSetting( cN ) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED ) ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED : PackageManager.COMPONENT_ENABLED_STATE_ENABLED );
		pm.setComponentEnabledSetting( cN , flag , PackageManager.DONT_KILL_APP );
		Intent selector = new Intent( Intent.ACTION_MAIN );
		selector.addCategory( Intent.CATEGORY_HOME );
		startActivity( selector );
		pm.setComponentEnabledSetting( cN , PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP );
	}
	
	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.set_default_start:
			{
				//resetDefault();
				setDefaultLauncher();
			}
				break;
		}
	}
	
	private void startActivityForId(
			Class<?> cls )
	{
		Intent in = new Intent( DefaultSetting.this , cls );
		startActivity( in );
		if( Integer.valueOf( Build.VERSION.SDK ).intValue() >= 5 )
		{
			overridePendingTransition( R.anim.dsmove_in_right , R.anim.dsalphaout );
		}
	}
}
