package com.iLoong.Shortcuts.SystemTool;


public interface ISystemAdmin
{
	
	public void onDelete();
	
	public boolean getReadyState();
	
	public void select();
	
	public void setCallback(
			IAdminCallback callback );
}
