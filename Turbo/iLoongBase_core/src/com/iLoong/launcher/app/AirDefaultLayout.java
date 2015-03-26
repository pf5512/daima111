package com.iLoong.launcher.app;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Environment;

import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.core.CustomerHttpClient;
import com.iLoong.launcher.macinfo.LaunchStatistics;


public class AirDefaultLayout
{
	
	private static AirDefaultLayout layout;
	private Context context;
	private SharedPreferences prefs;
	private AlarmReceiver alarmReceiver;
	private long exactUpdateTime = 0;
	private boolean listenNetwork = false;
	private long time = 0;
	private String DIR = "/coco/air/";
	private String FILE_TMP = "/coco/air/dl.tmp";
	public static final String FILE = "/coco/air/dl";
	public static String SERVER_URL_COUNT = "http://www.coolauncher.cn/iloong/pservices/ServicesEngine/DataService";
	private final int LOGIN_TIME_INTERVAL = 604800000;//更新数据时间间隔:7 day
	public static final String EXACT_UPDATE_TIME_KEY = "ExactUpdateTime";
	public String ACTION_UPDATE = "air.defaultlayout.action.update";
	
	public static AirDefaultLayout getInstance()
	{
		if( layout == null )
		{
			synchronized( AirDefaultLayout.class )
			{
				if( layout == null )
					layout = new AirDefaultLayout();
			}
		}
		return layout;
	}
	
	public void start(
			Context _context )
	{
		Log.i( "air" , "dl:start" );
		time = System.currentTimeMillis();
		context = _context;
		new File( Environment.getExternalStorageDirectory() + DIR ).mkdirs();
		ACTION_UPDATE = context.getApplicationInfo().packageName + "." + ACTION_UPDATE;
		prefs = context.getSharedPreferences( "air" , Activity.MODE_PRIVATE );
		alarmReceiver = new AlarmReceiver();
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction( ACTION_UPDATE );
		filter2.addAction( ConnectivityManager.CONNECTIVITY_ACTION );
		context.registerReceiver( alarmReceiver , filter2 );
		long now = System.currentTimeMillis();
		exactUpdateTime = prefs.getLong( EXACT_UPDATE_TIME_KEY , 0 );
		long loginDelay = exactUpdateTime - now;
		if( loginDelay < 0 )
			loginDelay = 60000;
		setUpdateAlarm( loginDelay );
	}
	
	private void setUpdateAlarm(
			long delay )
	{
		exactUpdateTime = System.currentTimeMillis() + delay;
		prefs.edit().putLong( EXACT_UPDATE_TIME_KEY , exactUpdateTime ).commit();
		Intent intent = new Intent();
		intent.setAction( ACTION_UPDATE );
		PendingIntent pi = PendingIntent.getBroadcast( context , 0 , intent , 0 );
		AlarmManager am = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );
		am.set( AlarmManager.RTC , exactUpdateTime , pi );
	}
	
	private void update()
	{
		Thread update = new Thread() {
			
			public void run()
			{
				Log.i( "air" , "dl:update1 " + ( System.currentTimeMillis() - time ) );
				android.os.Process.setThreadPriority( android.os.Process.THREAD_PRIORITY_BACKGROUND );
				if( CustomerHttpClient.isNetworkAvailable() )
				{
					Log.i( "air" , "dl:update2 " + ( System.currentTimeMillis() - time ) );
					listenNetwork = false;
					String url = SERVER_URL_COUNT;
					String params = getParams();
					if( params != null )
					{
						String[] res = CustomerHttpClient.post( url , params );
						if( res != null )
						{
							String dir = Environment.getExternalStorageDirectory() + FILE_TMP;
							File file = new File( dir );
							if( file.exists() )
							{
								file.delete();
							}
							try
							{
								file.createNewFile();
								FileWriter fw = new FileWriter( file );
								fw.write( res[0] );
								fw.close();
								if( res[1].equals( -1 + "" ) || res[1].equals( file.length() + "" ) )
								{
									String dir2 = Environment.getExternalStorageDirectory() + FILE;
									File file2 = new File( dir2 );
									if( file2.exists() )
									{
										file2.delete();
									}
									file.renameTo( file2 );
								}
							}
							catch( IOException e )
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					setUpdateAlarm( LOGIN_TIME_INTERVAL );
					Log.i( "air" , "dl:update3 " + ( System.currentTimeMillis() - time ) );
				}
				else
				{
					Log.i( "air" , "dl:update4 " + ( System.currentTimeMillis() - time ) );
					listenNetwork = true;
				}
			}
		};
		update.start();
	}
	
	public String getParams()
	{
		JSONObject res = new JSONObject();
		try
		{
			res.put( "Action" , "1003" );
			String content = res.toString();
			String md5_res = LaunchStatistics.getMD5EncruptKey( content + LaunchStatistics.DEFAULT_KEY );
			String newContent = content.substring( 0 , content.lastIndexOf( '}' ) );
			String params = newContent + ",\"md5\":\"" + md5_res + "\"}";
			return params;
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public class AlarmReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context context ,
				Intent intent )
		{
			String action = intent.getAction();
			//Log.d("statistics", "alarm receive:action="+action);
			if( action.equals( ACTION_UPDATE ) )
			{
				update();
			}
			else if( action.equals( ConnectivityManager.CONNECTIVITY_ACTION ) )
			{
				Log.d( "air" , "CONNECTIVITY_ACTION" );
				if( !listenNetwork || !CustomerHttpClient.isNetworkAvailable() )
					return;
				listenNetwork = false;
				update();
			}
		}
	}
}
