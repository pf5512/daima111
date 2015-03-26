package com.iLoong.launcher.macinfo;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.iLoong.launcher.core.Assets;
import com.iLoong.launcher.core.CustomerHttpClient;


public class LaunchStatistics
{
	
	//public static String SERVER_URL_COUNT = "http://192.168.0.10/iloong/pservices/ServicesEngine/DataService";
	public static String SERVER_URL_COUNT = "http://www.coolauncher.cn/iloong/pservices/ServicesEngine/DataService";
	public static String SERVER_URL = "http://www.coolauncher.cn/iloong/pservices/Active/Insert";
	//public static String SERVER_URL = "http://192.168.0.10/iloong/pservices/Active/Insert";
	public static String DEFAULT_KEY = "f24657aafcb842b185c98a9d3d7c6f4725f6cc4597c3a4d531c70631f7c7210fd7afd2f8287814f3dfa662ad82d1b02268104e8ab3b2baee13fab062b3d27bff";
	public static final String CONFIG_FILE_NAME = "config.ini";
	public static final String BASE_URI_SLAVE_ACTIVE = "/openapi/LoginHandler.ashx";
	public static final String BASE_URI_SLAVE_LAUNCH = "/openapi/Coco_bootHandler.ashx";//"http://test.mumucloud.com/openapi/LoginHandler.ashx";
	public static final String BASE_URI_DOMAIN_LAUNCH = "http://w1.mumucloud.com";
	public static final String EXACT_LOGIN_TIME_KEY = "ExactLoginTime";
	public static final String LAUNCH_COUNT_KEY_OLD = "LoginCountOld";
	public static final String LOGIN_ACTIVE_KEY_OLD = "LoginActiveOld";
	public static final String LAUNCH_COUNT_KEY_NEW = "LoginCountNew";
	public static final String LOGIN_ACTIVE_KEY_NEW = "LoginActiveNew";
	public String ACTION_LOGIN_CONFIG = "statistics.action.login";
	private final int LOGIN_TIME_INTERVAL = 86400000;//上传数据时间间隔
	private final boolean ENABLE_OLD_STATISTICS = true;
	private final boolean ENABLE_NEW_STATISTICS = true;
	public final static int OLD_STATISTICS = 1;
	public final static int NEW_STATISTICS = 2;
	public final static int NEW_STATISTICS_COUNT = 3;
	private Context context;
	private SharedPreferences prefs;
	private long exactLoginTime = 0;
	
	//	private AlarmReceiver alarmReceiver;
	public LaunchStatistics(
			Context context )
	{
		this.context = context;
		ACTION_LOGIN_CONFIG = context.getApplicationInfo().packageName + "." + ACTION_LOGIN_CONFIG;
		prefs = context.getSharedPreferences( "statistics" , Activity.MODE_PRIVATE );
		//		alarmReceiver = new AlarmReceiver();
		//		IntentFilter filter2 = new IntentFilter();
		//		filter2.addAction( ACTION_LOGIN_CONFIG );
		//		context.registerReceiver( alarmReceiver , filter2 );
	}
	
	public void onLaunchFinish()
	{
		long now = System.currentTimeMillis();
		exactLoginTime = prefs.getLong( EXACT_LOGIN_TIME_KEY , 0 );
		long loginDelay = exactLoginTime - now;
		if( loginDelay < 0 )
			loginDelay = 60000;
		setLoginAlarm( loginDelay );
	}
	
	public void onResume()
	{
		int loginCountOld = prefs.getInt( LAUNCH_COUNT_KEY_OLD , 0 );
		loginCountOld++;
		prefs.edit().putInt( LAUNCH_COUNT_KEY_OLD , loginCountOld ).commit();
		int loginCountNew = prefs.getInt( LAUNCH_COUNT_KEY_NEW , 0 );
		loginCountNew++;
		prefs.edit().putInt( LAUNCH_COUNT_KEY_NEW , loginCountNew ).commit();
	}
	
	public void onDestroy()
	{
		//		if( alarmReceiver != null )
		//			context.unregisterReceiver( alarmReceiver );
	}
	
	private void setLoginAlarm(
			long delay )
	{
		exactLoginTime = System.currentTimeMillis() + delay;
		prefs.edit().putLong( EXACT_LOGIN_TIME_KEY , exactLoginTime ).commit();
		Intent intent = new Intent();
		intent.setAction( ACTION_LOGIN_CONFIG );
		PendingIntent pi = PendingIntent.getBroadcast( context , 0 , intent , 0 );
		AlarmManager am = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );
		am.set( AlarmManager.RTC , exactLoginTime , pi );
	}
	
	//	public int[] requestLoginToServe()
	//	{
	//		int launchCountOld = prefs.getInt( LAUNCH_COUNT_KEY_OLD , 0 );
	//		int launchCountNew = prefs.getInt( LAUNCH_COUNT_KEY_NEW , 0 );
	//		MacInfo.initMacInfo( context );
	//		JSONObject loginFile = MacInfo.getInfo();
	//		//Log.i("statistics", "login File = " + loginFile.toString());
	//		String loginInfo = loginFile.toString();
	//		boolean resOld = false;
	//		boolean resNew = false;
	//		if( ENABLE_OLD_STATISTICS )
	//		{
	//			boolean active = prefs.getBoolean( LOGIN_ACTIVE_KEY_OLD , false );
	//			if( !active )
	//			{
	//				boolean activeResult = requestLogin( loginInfo , OLD_STATISTICS );
	//				prefs.edit().putBoolean( LOGIN_ACTIVE_KEY_OLD , activeResult ).commit();
	//				Log.i( "sta" , "active old:" + activeResult );
	//			}
	//			if( CustomerHttpClient.isNetworkAvailable() )
	//			{
	//				String params = getLaunchParamsOld( launchCountOld );
	//				if( params != null && CustomerHttpClient.isNetworkAvailable() )
	//				{
	//					InputStream in = HttpUtil.sendGet( BASE_URI_DOMAIN_LAUNCH + BASE_URI_SLAVE_LAUNCH , params );
	//					if( in != null )
	//					{
	//						BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
	//						String line;
	//						String result = "";
	//						try
	//						{
	//							while( ( line = reader.readLine() ) != null )
	//							{
	//								result += "\n" + line;
	//							}
	//						}
	//						catch( IOException e )
	//						{
	//							e.printStackTrace();
	//						}
	//						resOld = CustomerHttpClient.parseResult( result , OLD_STATISTICS );
	//						try
	//						{
	//							in.close();
	//						}
	//						catch( IOException e )
	//						{
	//							// TODO Auto-generated catch block
	//							e.printStackTrace();
	//						}
	//					}
	//				}
	//			}
	//		}
	//		if( ENABLE_NEW_STATISTICS )
	//		{
	//			boolean active = prefs.getBoolean( LOGIN_ACTIVE_KEY_NEW , false );
	//			if( !active )
	//			{
	//				boolean activeResult = requestLogin( loginInfo , NEW_STATISTICS );
	//				prefs.edit().putBoolean( LOGIN_ACTIVE_KEY_NEW , activeResult ).commit();
	//				Log.i( "sta" , "active new:" + activeResult );
	//			}
	//			if( CustomerHttpClient.isNetworkAvailable() )
	//			{
	//				String url = SERVER_URL_COUNT;
	//				String logInfo = getLaunchParamsNew( launchCountNew );
	//				//Log.i("statistics", "post count:"+logInfo);
	//				if( logInfo != null )
	//				{
	//					resNew = CustomerHttpClient.post( url , logInfo , NEW_STATISTICS_COUNT );
	//				}
	//			}
	//		}
	//		int[] res = new int[2];
	//		if( resOld )
	//			res[0] = launchCountOld;
	//		else
	//			res[0] = 0;
	//		if( resNew )
	//			res[1] = launchCountNew;
	//		else
	//			res[1] = 0;
	//		return res;
	//	}
	public String getLaunchParamsOld(
			int launchCount )
	{
		//?app_id=&bootnum=&serialno=&uuid=&mark=
		String uuid = Installation.id( context );
		String appid = null;
		String serialno = null;
		JSONObject tmp = Assets.config;
		if( tmp != null )
		{
			try
			{
				JSONObject config = tmp.getJSONObject( "config" );
				appid = config.getString( "app_id" );
				serialno = config.getString( "serialno" );
			}
			catch( JSONException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if( uuid == null | appid == null | serialno == null )
			return null;
		return "app_id=" + appid + "&" + "bootnum=" + launchCount + "&" + "serialno=" + serialno + "&" + "uuid=" + uuid + "&" + "mark=" + context.getApplicationInfo().packageName;
	}
	
	public String getLaunchParamsNew(
			int launchCount )
	{
		//?app_id=&bootnum=&serialno=&uuid=&mark=
		String uuid = Installation.id( context );
		String appid = null;
		JSONObject tmp = Assets.config;
		if( tmp != null )
		{
			try
			{
				JSONObject config = tmp.getJSONObject( "config" );
				appid = config.getString( "app_id" );
			}
			catch( JSONException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if( uuid == null | appid == null )
			return null;
		JSONObject res = new JSONObject();
		try
		{
			res.put( "Action" , "1002" );
			res.put( "appid" , appid );
			res.put( "bootnum" , launchCount );
			res.put( "uuid" , uuid );
			String content = res.toString();
			String md5_res = getMD5EncruptKey( content + DEFAULT_KEY );
			//res.put("md5", md5_res);
			String newContent = content.substring( 0 , content.lastIndexOf( '}' ) );
			String logInfo = newContent + ",\"md5\":\"" + md5_res + "\"}";
			return logInfo;
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean requestLogin(
			String content ,
			int type )
	{
		String logInfo;
		String url;
		if( type == OLD_STATISTICS )
		{
			url = getRequesURL( "domain" , BASE_URI_SLAVE_ACTIVE );
			logInfo = "{\"errno\":\"0\",\"data\":" + content + "}";
		}
		else
		{
			//			String test = "{\"ss\":{\"121\":\"111\"}}";
			//			Log.v("http", "requestLogin orignal info = " + content);
			String md5_res = getMD5EncruptKey( content + DEFAULT_KEY );
			String newContent = content.substring( 0 , content.lastIndexOf( '}' ) );
			url = SERVER_URL;
			logInfo = newContent + ",\"md5\":" + "{\"md5\":\"" + md5_res + "\"}}";
		}
		return CustomerHttpClient.post( url , logInfo , type );
	}
	
	public static String getMD5EncruptKey(
			String logInfo )
	{
		String res = null;
		MessageDigest messagedigest;
		try
		{
			messagedigest = MessageDigest.getInstance( "MD5" );
		}
		catch( NoSuchAlgorithmException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		messagedigest.update( logInfo.getBytes() );
		res = bufferToHex( messagedigest.digest() );
		//		Log.v("http", "getMD5EncruptKey res =  " + res);
		return res;
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
	
	public static String getRequesURL(
			String domainKey ,
			String slaveUri )
	{
		String res = null;
		JSONObject config = Assets.config;
		JSONObject tmp = null;
		try
		{
			tmp = config.getJSONObject( "config" );
		}
		catch( JSONException e1 )
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String domain = "http://androidsub2.cooee.com.cn";
		try
		{
			domain = tmp.getString( domainKey );
			if( !domain.contains( "http://" ) )
			{
				domain = "http://" + domain;
			}
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res = domain + slaveUri;
		return res;
	}
	//	public class AlarmReceiver extends BroadcastReceiver
	//	{
	//		
	//		@Override
	//		public void onReceive(
	//				Context context ,
	//				Intent intent )
	//		{
	//			String action = intent.getAction();
	//			//Log.d("statistics", "alarm receive:action="+action);
	//			if( action.equals( ACTION_LOGIN_CONFIG ) )
	//			{
	//				Thread request = new Thread() {
	//					
	//					public void run()
	//					{
	//						android.os.Process.setThreadPriority( android.os.Process.THREAD_PRIORITY_BACKGROUND );
	//						int[] uploadCount = requestLoginToServe();
	//						int oldUploadCount = uploadCount[0];
	//						if( oldUploadCount > 0 )
	//						{
	//							int loginCount = prefs.getInt( LAUNCH_COUNT_KEY_OLD , 0 );
	//							loginCount -= oldUploadCount;
	//							if( loginCount < 0 )
	//								loginCount = 0;
	//							prefs.edit().putInt( LAUNCH_COUNT_KEY_OLD , loginCount ).commit();
	//							Log.i( "sta" , "old upload,remain:" + oldUploadCount + "," + loginCount );
	//						}
	//						int newUploadCount = uploadCount[1];
	//						if( newUploadCount > 0 )
	//						{
	//							int loginCount = prefs.getInt( LAUNCH_COUNT_KEY_NEW , 0 );
	//							loginCount -= newUploadCount;
	//							if( loginCount < 0 )
	//								loginCount = 0;
	//							prefs.edit().putInt( LAUNCH_COUNT_KEY_NEW , loginCount ).commit();
	//							Log.i( "sta" , "new upload,remain:" + newUploadCount + "," + loginCount );
	//						}
	//						setLoginAlarm( LOGIN_TIME_INTERVAL );
	//					}
	//				};
	//				request.start();
	//			}
	//		}
	//	}
}
