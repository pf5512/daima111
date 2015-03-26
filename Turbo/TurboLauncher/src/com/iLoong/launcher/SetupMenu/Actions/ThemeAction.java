package com.iLoong.launcher.SetupMenu.Actions;


import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class ThemeAction extends Action
{
	
	public ThemeAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		if( !DefaultLayout.enable_themebox )
		{
			putIntentAction( SetupMenu.getContext() , com.iLoong.launcher.theme.ThemeManagerActivity.class );
		}
		else
		{
			if( DefaultLayout.personal_center_internal )
			{
				putIntentAction( new ComponentName( SetupMenu.getContext() , "com.coco.theme.themebox.MainActivity" ) );
			}
			else
			{
				putIntentAction( new ComponentName( "com.iLoong.base.themebox" , "com.coco.theme.themebox.MainActivity" ) );
			}
		}
		;
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_THEME , new ThemeAction( ActionSetting.ACTION_THEME , ThemeAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		String TAG_STRING = "currentTab";
		String TAG_THEME = "tagTheme";
		final Intent intent = new Intent( mAction );
		intent.setComponent( mComponent );
		intent.putExtra( TAG_STRING , TAG_THEME );
		try
		{
			if( DefaultLayout.personal_center_internal )
			{
				iLoongLauncher.getInstance().bindThemeActivityData( intent );
				SetupMenuActions.getInstance().getContext().startActivity( intent );
			}
			else
			{
				PackageManager pm = SetupMenuActions.getInstance().getContext().getPackageManager();
				if( pm.queryIntentActivities( intent , 0 ).size() == 0 )
				{
					iLoongLauncher.getInstance().mMainHandler.post( new Runnable() {
						
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							iLoongLauncher.getInstance().themeCenterDown.ToDownloadApkDialog(
									iLoongLauncher.getInstance() ,
									iLoongLauncher.getInstance().getResources().getString( RR.string.theme ) ,
									"com.iLoong.base.themebox" );
						}
					} );
				}
				else
				{
					iLoongLauncher.getInstance().bindThemeActivityData( intent );
					SetupMenuActions.getInstance().getContext().startActivity( intent );
				}
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return;
	}
	
	@Override
	protected void OnActionFinish()
	{
	}
	
	@Override
	protected void OnPutValue(
			String key )
	{
	}
}
