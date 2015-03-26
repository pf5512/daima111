package com.iLoong.launcher.custom;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.core.CustomerHttpClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class PackageTransfer extends BroadcastReceiver
{
	
	private final String MD5_KEY = "XmqyjcJ8LsZCsm5CAzDp7G0WberkNeqw68GG0OHyrqQGK6OhMWdDgmkGkNJeUYZT2bELWuDda2qZOxXWpBwbYIzu8npizDg6DFlmhYZcMOjBhhiF8OPs6LXiEDKdxvie";
	public static final String ACTION_SEND_PACKAGE_INFO = "retrieve.action.send.package.info";
	private final String DEFAULT_URL ="http://www.coolauncher.cn/getIntoFileV2.php";// "http://www.coolauncher.cn/getIntoFile.php";////
	
	@Override
	public void onReceive(
			Context context ,
			Intent intent )
	{
		String action = intent.getAction();
		Log.v( "retrieve" , "PackageTransfer" );
		if( action.equals( ACTION_SEND_PACKAGE_INFO ) )
		{
			//just for initializing the context of HttpHelper
			HttpHelper http = new HttpHelper( context );
			new Thread() {
				
				@Override
				public void run()
				{
					if( HttpHelper.isNetworkAvailable() )
					{
						Log.v( "retrieve" , "run" );
						boolean ret = false;
						android.os.Process.setThreadPriority( android.os.Process.THREAD_PRIORITY_BACKGROUND );
						if( SystemAppRetriever.appInfo != null && !SystemAppRetriever.appInfo.equals( "" ) )
							ret = RequestNetwork( SystemAppRetriever.appInfo );
						SystemAppRetriever.httpResponseCallback( ret );
					}
				}
			}.start();
		}
	}
	
	public boolean RequestNetwork(
			String content )
	{
		String urlContent = "";
		String url = DEFAULT_URL;
		String finalString = "";
		String combineContent = content + MD5_KEY;
		MessageDigest messagedigest;
		try
		{
			messagedigest = MessageDigest.getInstance( "MD5" );
		}
		catch( NoSuchAlgorithmException e )
		{
			e.printStackTrace();
			return false;
		}
		messagedigest.update( combineContent.getBytes() );
		urlContent = bufferToHex( messagedigest.digest() );
		finalString = content + urlContent;
		return HttpHelper.post( url , finalString );
	}
	
	protected static char hexDigits[] = { '0' , '1' , '2' , '3' , '4' , '5' , '6' , '7' , '8' , '9' , 'a' , 'b' , 'c' , 'd' , 'e' , 'f' };
	
	private static String bufferToHex(
			byte bytes[] )
	{
		return bufferToHex( bytes , 0 , bytes.length );
	}
	
	private static String bufferToHex(
			byte bytes[] ,
			int m ,
			int n )
	{
		StringBuffer stringbuffer = new StringBuffer( 2 * n );
		int k = m + n;
		for( int l = m ; l < k ; l++ )
		{
			appendHexPair( bytes[l] , stringbuffer );
		}
		return stringbuffer.toString();
	}
	
	private static void appendHexPair(
			byte bt ,
			StringBuffer stringbuffer )
	{
		char c0 = hexDigits[( bt & 0xf0 ) >> 4]; // 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同    
		char c1 = hexDigits[bt & 0xf]; // 取字节中低 4 位的数字转换    
		stringbuffer.append( c0 );
		stringbuffer.append( c1 );
	}
}
