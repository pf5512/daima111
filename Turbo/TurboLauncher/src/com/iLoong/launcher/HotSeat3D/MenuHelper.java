package com.iLoong.launcher.HotSeat3D;

import android.content.Intent;

import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.DesktopEdit.DesktopEditHost;
import com.iLoong.launcher.SetupMenu.Actions.DesktopSettings.FirstActivity;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class MenuHelper
{
	public void onDesktopSetting(){
		Intent setIntent = new Intent( iLoongLauncher.getInstance() , FirstActivity.class );
		 iLoongLauncher.getInstance().startActivity( setIntent );
		
	}
	
	public void onScreenEdit(){
		Desktop3DListener.root.onCtrlEvent( iLoongLauncher.getInstance().d3dListener.getWorkspace3D() , Workspace3D.MSG_PAGE_SHOW_EDIT );
		
	}
	
	public void onAdd(){
		iLoongLauncher.getInstance().d3dListener.root.startMIUIEditEffect();
		DesktopEditHost.popup( iLoongLauncher.getInstance().d3dListener.root );
		if( DesktopEditHost.getInstance() != null && DesktopEditHost.getInstance().mulpMenuHost != null )
		{
			DesktopEditHost.getInstance().mulpMenuHost.MenuCallBack( 0 );
		}
	}
	
	public void onSystemSetting(){
		Intent setIntent = new Intent( android.provider.Settings.ACTION_SETTINGS);
		 iLoongLauncher.getInstance().startActivity( setIntent );
	}
	
	public void onBeautify(){
		SendMsgToAndroid.sendSelectHotZhuTi();
	}
	
}
