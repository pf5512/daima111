package com.iLoong.launcher.SetupMenu.Actions;


import java.util.List;

import org.apache.http.NameValuePair;

import com.iLoong.RR;
import com.iLoong.launcher.core.CustomerHttpClient;
import com.iLoong.launcher.desktop.iLoongApplication;


public class CommitReport implements Runnable
{
	
	private static final String httpUrl = iLoongApplication.getInstance().getResources().getString( RR.string.setting_report );
	List<NameValuePair> mParams;
	
	public CommitReport(
			List<NameValuePair> formparams )
	{
		mParams = formparams;
	}
	
	@Override
	public void run()
	{
		CustomerHttpClient.post( httpUrl , mParams );
	}
}
