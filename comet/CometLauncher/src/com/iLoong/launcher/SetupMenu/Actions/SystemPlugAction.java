package com.iLoong.launcher.SetupMenu.Actions;


import android.appwidget.AppWidgetManager;
import android.content.Intent;

import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class SystemPlugAction extends Action
{
	
	public SystemPlugAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		putIntentAction( SetupMenu.getContext() , SystemPlugAction.class );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_SYSTEM_PLUG , new SystemPlugAction( ActionSetting.ACTION_SYSTEM_PLUG , SystemPlugAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
		AddSystemWidget();
	}
	
	private void AddSystemWidget()
	{
		//Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
		//int appWidgetId = iLoongLauncher.getInstance().getAppWidgetHost().allocateAppWidgetId();
		//pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		//iLoongLauncher.getInstance().startActivityForResult(pickIntent, iLoongLauncher.REQUEST_PICK_APPWIDGET);
		iLoongLauncher.getInstance().displaySystemWidget();
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
