package com.iLoong.launcher.SetupMenu.Actions;


import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.DesktopEdit.DesktopEditHost;
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
		if( SetupMenu.getInstance() != null )
			SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
		AddSystemWidget();
	}
	
	private void AddSystemWidget()
	{
		//Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
		//int appWidgetId = iLoongLauncher.getInstance().getAppWidgetHost().allocateAppWidgetId();
		//pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		//iLoongLauncher.getInstance().startActivityForResult(pickIntent, iLoongLauncher.REQUEST_PICK_APPWIDGET);
		//teapotXu add start
		if(!RR.net_version){
			if( DefaultLayout.enable_desktop_longclick_to_add_widget )
			{
				iLoongLauncher.getInstance().getD3dListener().getRoot().showWidgetListFromWorkspace();
			}
			else
			{
				iLoongLauncher.getInstance().displaySystemWidget();
			}
		}
		else{
			iLoongLauncher.getInstance().d3dListener.root.startMIUIEditEffect();
			DesktopEditHost.popup( iLoongLauncher.getInstance().d3dListener.root );
		}
		//teapotXu add end		
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
