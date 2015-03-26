package com.iLoong.launcher.Functions.Tab;


public interface RemoteDataObserver
{
	
	void onPluginInstall(
			String packageName );
	
	void onPluginUninstall(
			String packageName );
}
