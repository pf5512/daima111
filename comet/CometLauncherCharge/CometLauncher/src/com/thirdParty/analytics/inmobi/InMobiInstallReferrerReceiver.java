package com.thirdParty.analytics.inmobi;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.analytics.tracking.android.CampaignTrackingReceiver;
import com.inmobi.commons.analytics.androidsdk.IMAdTrackerReceiver;


public class InMobiInstallReferrerReceiver extends BroadcastReceiver
{
	
	public InMobiInstallReferrerReceiver()
	{
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onReceive(
			Context context ,
			Intent intent )
	{
		// TODO Auto-generated method stub
		if( intent.getAction().equals( "com.android.vending.INSTALL_REFERRER" ) )
		{
			// Pass the intent to the IAT receiver using
			new IMAdTrackerReceiver().onReceive( context , intent );
			// Pass the intent to the google receiver using
			new CampaignTrackingReceiver().onReceive( context , intent );
		}
	}
}
