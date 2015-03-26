package com.iLoong.launcher.SetupMenu.Actions;


import android.os.Bundle;


public interface MenuActionListener
{
	
	public void setActionListener();
	
	public void OnAction(
			int actionid ,
			Bundle bundle );
}
