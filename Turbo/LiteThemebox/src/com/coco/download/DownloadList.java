package com.coco.download;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;

import com.coco.theme.themebox.MainActivity;
import com.coco.theme.themebox.database.service.ConfigurationTabService;
import com.coco.theme.themebox.database.service.HotService;
import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.ThemeXmlParser;
import com.umeng.analytics.AnalyticsConfig;


public class DownloadList
{
	
	private final String DEFAULT_KEY = "f24657aafcb842b185c98a9d3d7c6f4725f6cc4597c3a4d531c70631f7c7210fd7afd2f8287814f3dfa662ad82d1b02268104e8ab3b2baee13fab062b3d27bff";
	// private final String SERVER_URL_TEST =
	// "http://192.168.1.225/iloong/pui/ServicesEngine/DataService";
	// private final String LOG_URL_TEST =
	// "http://192.168.1.225/iloong/pui/LogEngine/DataService";
	//	private final String SERVER_URL_TEST = "http://58.246.135.237:20180/iloong/pui/ServicesEngine/DataService";
	//	private final String LOG_URL_TEST = "http://58.246.135.237:20180/iloong/pui/LogEngine/DataService";
	private final String SERVER_URL_TEST = "http://uifolder.coolauncher.com.cn/iloong/pui/ServicesEngine/DataService";
	private final String LOG_URL_TEST = "http://uifolder.coolauncher.com.cn/iloong/pui/LogEngine/DataService";
	private final String ACTION_LIST = "1300";
	public static final String ACTION_DOWNLOAD_LOG = "0030";
	public static final String ACTION_INSTALL_LOG = "0031";
	public static final String ACTION_UNINSTALL_LOG = "0032";
	private final String LOG_TAG = "DownloadTab";
	private final String VERSION_CODE = "2";
	private final String APP_VERSION_CODE = "19627";//需要手动填写，这个版本号跟Manifest中的versionCode一致
	public static final String ACTION_HOTLIST_CHANGED = "com.coco.action.HOTLIST_CHANGED";
	private final String HOT_LIST_DATE = "hotListDate";
	private static DownloadList proxy;
	private Context mContext;
	private DownloadListThread downListThread = null;
	private Object syncObject = new Object();
	private List<ListInfo> info = new ArrayList<ListInfo>();
	public static final String Theme_Type = "1";
	public static final String Wallpaper_Type = "2";
	public static final String Lock_Type = "3";
	public static final String Widget_Type = "4";
	public static final String Scene_Type = "5";
	public static final String Font_Type = "6";
	public static final String LiveWallpaper_Type = "21";
	public static String types[] = new String[]{ Theme_Type , Lock_Type , Scene_Type , Wallpaper_Type , Widget_Type , Font_Type , LiveWallpaper_Type };
	
	public DownloadList(
			Context context )
	{
		mContext = context;
	}
	
	public static DownloadList getInstance(
			Context context )
	{
		if( proxy == null )
		{
			synchronized( DownloadList.class )
			{
				if( proxy == null )
				{
					proxy = new DownloadList( context );
				}
			}
		}
		return proxy;
	}
	
	public void downList()
	{
		synchronized( this.syncObject )
		{
			if( downListThread == null )
			{
				downListThread = new DownloadListThread();
				downListThread.start();
			}
		}
	}
	
	public void dispose()
	{
		stopDownloadList();
		resetListInfo();
		stopDownloadTab();
		tabInfo.clear();
	}
	
	public void stopDownloadList()
	{
		if( downListThread != null )
		{
			downListThread.stopRun();
			downListThread = null;
		}
	}
	
	private void downloadListFinish()
	{
		Log.v( LOG_TAG , "downloadListFinish:" + ACTION_HOTLIST_CHANGED );
		ThemeXmlParser parserXml = new ThemeXmlParser( mContext );
		HotService sv = new HotService( mContext );
		Intent intent = new Intent();
		sv.clearTable();
		for( String item : types )
		{
			if( parserXml.parseList( DownloadList.getInstance( mContext ).getListInfo() , item , mContext ) )
			{
				sv.batchInsert( parserXml.getThemeList() );
			}
		}
		saveListTime();
		intent.setAction( ACTION_HOTLIST_CHANGED );
		mContext.sendBroadcast( intent );
	}
	
	private void saveListTime()
	{
		Time curTime = new Time();
		String curDateString = curTime.format( "yyyyMMdd" );
		SharedPreferences sharedPrefer = PreferenceManager.getDefaultSharedPreferences( mContext );
		Editor edit = sharedPrefer.edit();
		edit.putString( HOT_LIST_DATE , curDateString );
		edit.commit();
	}
	
	public boolean isRefreshList()
	{
		SharedPreferences sharedPrefer = PreferenceManager.getDefaultSharedPreferences( mContext );
		String downListDate = sharedPrefer.getString( HOT_LIST_DATE , "" );
		Time curTime = new Time();
		String curDateString = curTime.format( "yyyyMMdd" );
		if( curDateString.equals( downListDate ) )
		{
			return false;
		}
		return true;
	}
	
	private void resetListInfo()
	{
		for( int i = 0 ; i < info.size() ; i++ )
		{
			info.get( i ).getItemList().clear();
		}
		info.clear();
	}
	
	public List<ListInfo> getListInfo()
	{
		return info;
	}
	
	/*
	 * 下载列表的线程
	 */
	private class DownloadListThread extends Thread
	{
		
		private volatile boolean isExit = false;
		
		public DownloadListThread()
		{
		}
		
		public void stopRun()
		{
			isExit = true;
		}
		
		@Override
		public void run()
		{
			String url = SERVER_URL_TEST;
			String params = getParams( ACTION_LIST , true );
			boolean isSucceed = false;
			if( params != null )
			{
				CustomerHttpClient client = new CustomerHttpClient( mContext );
				String[] res = client.post( url , params );
				if( isExit )
				{
					synchronized( syncObject )
					{
						downListThread = null;
						return;
					}
				}
				if( res != null )
				{
					String content = res[0];
					Log.v( "downloadlist" , "content = " + content );
					//					Tools.writelogTosd( content );
					JSONObject json = null;
					try
					{
						json = new JSONObject( content );
						int retCode = json.getInt( "retcode" );
						if( retCode == 0 )
						{
							String configUrl = json.getString( "reslist" );
							json = new JSONObject( configUrl );
							for( Iterator iter = json.keys() ; iter.hasNext() ; )
							{
								String key = (String)iter.next();
								JSONObject tmJson = (JSONObject)json.get( key );
								ListInfo listInfo = new ListInfo();
								for( Iterator mIterator = tmJson.keys() ; mIterator.hasNext() ; )
								{
									String mKey = (String)mIterator.next();
									if( mKey.equals( "tabid" ) )
									{
										listInfo.setTabid( tmJson.get( mKey ).toString() );
									}
									else if( mKey.equals( "enname" ) )
									{
										listInfo.setEnname( tmJson.get( mKey ).toString() );
									}
									else if( mKey.equals( "cnname" ) )
									{
										listInfo.setCnname( tmJson.get( mKey ).toString() );
									}
									else if( mKey.equals( "twname" ) )
									{
										listInfo.setTwname( tmJson.get( mKey ).toString() );
									}
									else if( mKey.equals( "typeid" ) )
									{
										listInfo.setTypeid( tmJson.get( mKey ).toString() );
									}
									else
									{
										JSONObject jsObj = (JSONObject)tmJson.get( mKey );
										ItemInfo itemInfo = new ItemInfo();
										itemInfo.setIndex( mKey );
										System.out.println( "mKey = " + mKey + " jsObj.getString() = " + jsObj.getString( "cnname" ) );
										itemInfo.setResid( jsObj.getString( "resid" ) );
										itemInfo.setEnname( jsObj.getString( "enname" ) );
										itemInfo.setCnname( jsObj.getString( "cnname" ) );
										// itemInfo.setTwname(jsObj.getString("twname"));
										itemInfo.setResurl( jsObj.getString( "resurl" ) );
										itemInfo.setPackname( jsObj.getString( "packname" ) );
										itemInfo.setSize( jsObj.getString( "size" ) );
										itemInfo.setAuthor( jsObj.getString( "author" ) );
										itemInfo.setAboutchinese( jsObj.getString( "aboutchinese" ) );
										itemInfo.setVersion( jsObj.getString( "version" ) );
										itemInfo.setVersionname( jsObj.getString( "versionname" ) );
										itemInfo.setAboutenglish( jsObj.getString( "aboutenglish" ) );
										itemInfo.setPrice( jsObj.getString( "price" ) );
										itemInfo.setPricedetail( jsObj.getString( "pricedetail" ) );
										itemInfo.setPricePoint( jsObj.getString( "pricepoint" ) );
										itemInfo.setIcon( jsObj.getString( "icon" ) );
										itemInfo.setThumbimg( jsObj.getString( "thumbimg" ) );
										try
										{
											itemInfo.setEnginepackname( jsObj.getString( "enginepackname" ) );
											itemInfo.setEngineurl( jsObj.getString( "engineurl" ) );
											itemInfo.setEnginesize( jsObj.getString( "enginesize" ) );
										}
										catch( JSONException e )
										{
											itemInfo.setEnginepackname( null );
											itemInfo.setEngineurl( null );
											itemInfo.setEnginesize( null );
										}
										try
										{
											itemInfo.setEnginedesc( jsObj.getString( "enginedesc" ) );
										}
										catch( JSONException e )
										{
											itemInfo.setEnginedesc( null );
										}
										try
										{
											itemInfo.setThirdparty( jsObj.getString( "thirdparty" ) );
										}
										catch( JSONException e )
										{
											itemInfo.setThirdparty( null );
										}
										try
										{
											JSONArray preview = jsObj.getJSONArray( "previewlist" );
											String[] pre = new String[preview.length()];
											for( int k = 0 ; k < preview.length() ; k++ )
											{
												pre[k] = preview.getString( k );
											}
											itemInfo.setPreviewlist( pre );
										}
										catch( JSONException e )
										{
											itemInfo.setPreviewlist( new String[]{ itemInfo.getThumbimg() } );
										}
										listInfo.getItemList().add( itemInfo );
									}
								}
								info.add( listInfo );
							}
							isSucceed = true;
						}
					}
					catch( JSONException e )
					{
						e.printStackTrace();
					}
				}
			}
			if( isSucceed )
			{
				downloadListFinish();
			}
			synchronized( syncObject )
			{
				downListThread = null;
				return;
			}
		}
	}
	
	private boolean enable_Statistics_LOG = false;
	public static final String ACTION_USE_LOG = "0033";
	private final int LOGTEXT_NUM = 50;
	private HandlerThread handlerThread;
	private Handler mHandler;
	private Object threadSync = new Object();
	private ArrayList<Integer> idSet = new ArrayList<Integer>();
	private static String DEFAULT_ERRTIME = "YYYYMMDDHHMMSS";
	private String errtime = "YYYYMMDDHHMMSS";
	private final String ERRTIME = "ErrorTime";
	private final String ERRCOUNT = "ErrorCount";
	private final String SUCCESSTIME = "SuccessTime";
	private boolean isUseLog = false;
	
	private enum TYPE
	{
		RETRYINTERVAL , // 重试
		ONEDAYINTERVAL // 一天后重试
	};
	
	// 上传线程
	private Runnable UploadRun = new Runnable() {
		
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			mHandler.removeCallbacks( UploadRun );
			if( enable_Statistics_LOG )
			{
				Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=UploadRun" + "\n--上传线程" );
			}
			// 判断是否联网
			if( !IsHaveInternet( mContext ) )
			{
				if( enable_Statistics_LOG )
					Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=UploadRun" + "\n--没有线程" );
				exitThread();
				if( enable_Statistics_LOG )
					Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=UploadRun" + "\n--没有网退出线程" + "---------------------exitThread--------------------" );
				return;
			}
			else
			{
				String params = getLogInformation();
				if( params != null )
				{
					CustomerHttpClient client = new CustomerHttpClient( mContext );
					String[] res = client.post( LOG_URL_TEST , params );
					if( res != null )
					{
						String content = res[0];
						JSONObject json = null;
						try
						{
							json = new JSONObject( content );
							int retCode = json.getInt( "retcode" );
							if( retCode == 0 )
							{
								delAllData();
								clearErrTimeAndCount();
								recordSuccessTime();
								if( enable_Statistics_LOG )
									Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=UploadRun" + "\n--上传成功 =============ok====================" + "\n--params    = " + params );
							}
							else
							{
								isUseLog = false;
								int errcount = getErrTimes();
								recordErrCount( ++errcount );
								recordErrTime();
								if( enable_Statistics_LOG )
									Log.v(
											"UICenter - StatisticsNew" ,
											LOG_TAG + "\n=UploadRun" + "\n--上传失败=============not ok====================" + "\n--params    = " + params + "\n--retCode    =" + retCode );
							}
						}
						catch( JSONException e )
						{
							isUseLog = false;
							int errcount = getErrTimes();
							recordErrCount( ++errcount );
							recordErrTime();
							if( enable_Statistics_LOG )
								Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=UploadRun" + "\n--上传失败=============not ok====================" + "\n--params    = " + params );
							e.printStackTrace();
						}
					}
					else
					{
						isUseLog = false;
						int errcount = getErrTimes();
						recordErrCount( ++errcount );
						recordErrTime();
						if( enable_Statistics_LOG )
							Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=UploadRun" + "\n--上传失败=============not ok====================" + "\n--params    = " + params + "\n--res      =" + res );
					}
				}
				if( enable_Statistics_LOG )
					Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=UploadRun" + "\n--关闭上传线程" );
				exitThread();
			}
		}
	};
	
	private boolean exitErrTime()
	{
		if( mContext == null )
		{
			return true;
		}
		ConfigDB config = new ConfigDB( mContext );
		config.open();
		errtime = config.getRecord( ERRTIME );
		config.close();
		if( errtime.equals( DEFAULT_ERRTIME ) )
		{
			return false;
		}
		return true;
	}
	
	private boolean SuccessTimeTransfinite()
	{
		if( mContext == null )
		{
			return false;
		}
		ConfigDB config = new ConfigDB( mContext );
		config.open();
		String successtime = config.getRecord( SUCCESSTIME );
		config.close();
		if( successtime.equals( DEFAULT_ERRTIME ) )
		{
			return true;
		}
		else
		{
			try
			{
				SimpleDateFormat df = new SimpleDateFormat( "yyyyMMddHHmmss" );
				Calendar calendar = Calendar.getInstance();
				calendar.setTime( df.parse( successtime ) );
				Calendar calendar1 = Calendar.getInstance();
				calendar1.setTime( df.parse( getCurTime() ) );
				if( enable_Statistics_LOG )
					Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=SuccessTimeTransfinite" + "\n--successtime1:" + successtime + "\n--curtime     :" + getCurTime() );
				long delta = calendar1.getTimeInMillis() - calendar.getTimeInMillis();
				if( delta > 1440 * 60 * 1000 || delta < 0 )
				{
					return true;
				}
			}
			catch( ParseException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private boolean ErrTimeTransfinite(
			TYPE type )
	{
		if( errtime.equals( DEFAULT_ERRTIME ) )
		{
			if( enable_Statistics_LOG )
				Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=ErrTimeTransfinite" + "\n--DEFAULT_ERRTIME " );
			return false;
		}
		else
		{
			int errtime1 = Integer.parseInt( errtime.substring( 4 , 12 ) );
			int curtime = Integer.parseInt( getCurTime().substring( 4 , 12 ) );
			if( enable_Statistics_LOG )
				Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=ErrTimeTransfinite" + "\n--errtime1:" + errtime1 + "\n--curtime :" + curtime );
			if( type == TYPE.RETRYINTERVAL )
			{
				if( curtime - errtime1 > 1 || curtime - errtime1 < 0 )
				{
					return true;
				}
			}
			else if( type == TYPE.ONEDAYINTERVAL )
			{
				if( curtime - errtime1 > 10000 || curtime - errtime1 < 0 )
				{
					return true;
				}
			}
			return false;
		}
	}
	
	private int getErrTimes()
	{
		if( mContext == null )
		{
			return 0;
		}
		ConfigDB config = new ConfigDB( mContext );
		config.open();
		int count = Integer.parseInt( config.getRecord( ERRCOUNT ) );
		if( enable_Statistics_LOG )
			Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=getErrTimes" + "\n--geterrtimes:" + count );
		config.close();
		return count;
	}
	
	private void recordErrCount(
			int count )
	{
		if( mContext != null )
		{
			ConfigDB config = new ConfigDB( mContext );
			config.open();
			config.updateRecord( ERRCOUNT , Integer.toString( count ) );
			config.close();
		}
	}
	
	private void recordErrTime()
	{
		if( mContext != null )
		{
			ConfigDB config = new ConfigDB( mContext );
			config.open();
			config.updateRecord( ERRTIME , getCurTime() );
			config.close();
		}
	}
	
	private void clearErrTimeAndCount()
	{
		if( mContext != null )
		{
			ConfigDB config = new ConfigDB( mContext );
			config.open();
			config.updateRecord( ERRTIME , DEFAULT_ERRTIME );
			config.updateRecord( ERRCOUNT , "0" );
			config.close();
		}
	}
	
	private void recordSuccessTime()
	{
		if( mContext != null )
		{
			ConfigDB config = new ConfigDB( mContext );
			config.open();
			config.updateRecord( SUCCESSTIME , getCurTime() );
			config.close();
		}
	}
	
	private static String getCurTime()
	{
		String time = "";
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss" );
		Date curDate = new Date( System.currentTimeMillis() );
		time = formatter.format( curDate );
		return time;
	}
	
	public void startUICenterLog(
			String action ,
			String resid ,
			String packageName )
	{
		String logText = action + "#" + resid + "#" + packageName;
		boolean isFind = false;
		if( mContext == null )
		{
			if( enable_Statistics_LOG )
				Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=packageOnEvent" + "\n--[(mContext == null)] --------------------- return" );
			return;
		}
		if( enable_Statistics_LOG )
			Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=packageOnEvent" + "\n--order:" + logText );
		LogDB logDB = new LogDB( mContext );
		logDB.open();
		Cursor cursor = logDB.getAllLogText();
		while( cursor.moveToNext() )
		{
			String text = cursor.getString( cursor.getColumnIndexOrThrow( "logtext" ) );
			if( text.equals( logText ) )
			{
				isFind = true;
				break;
			}
		}
		cursor.close();
		if( !isFind )
		{
			logDB.insertRecord( logText );
		}
		logDB.close();
		if( mHandler != null )
		{
			if( enable_Statistics_LOG )
				Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=packageOnEvent" + "\n--存在上传线程 ---------------------return" );
			return;
		}
		checkThread();
		if( !exitErrTime() )
		{
			if( enable_Statistics_LOG )
				Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=packageOnEvent" + "\n--不存在错误时间" );
			if( SuccessTimeTransfinite() )
			{
				if( enable_Statistics_LOG )
					Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=packageOnEvent" + "\n--成功时间超过时间间隔 ==========================" );
				// 上传数据
				mHandler.post( UploadRun );
			}
			else
			{
				exitThread();
				if( enable_Statistics_LOG )
					Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=packageOnEvent" + "\n--exitThread成功时间没有超过时间间隔 ---------------------exitThread--------------------" );
			}
		}
		else
		{
			if( enable_Statistics_LOG )
				Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=packageOnEvent" + "\n--存在错误时间" );
			if( getErrTimes() > 3 && ErrTimeTransfinite( TYPE.ONEDAYINTERVAL ) )
			{
				if( enable_Statistics_LOG )
					Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=packageOnEvent" + "\n--错误时间超过一小时 ==========================" );
				// 更新失败次数&失败时间
				recordErrCount( 1 );
				recordErrTime();
				// 上传数据
				mHandler.post( UploadRun );
			}
			else if( getErrTimes() <= 3 && ErrTimeTransfinite( TYPE.RETRYINTERVAL ) )
			{
				if( enable_Statistics_LOG )
					Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=packageOnEvent" + "\n--错误时间间隔超过1分钟 ========================== " );
				// 上传数据
				mHandler.post( UploadRun );
			}
			else
			{
				exitThread();
				if( enable_Statistics_LOG )
					Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=packageOnEvent" + "\n--exitThread错误时间没有超过时间间隔 ---------------------exitThread-------------------- " );
			}
		}
	}
	
	private void checkThread()
	{
		synchronized( threadSync )
		{
			if( handlerThread == null )
			{
				handlerThread = new HandlerThread( "handlerThread" );
				handlerThread.start();
				mHandler = new Handler( handlerThread.getLooper() );
				if( enable_Statistics_LOG )
					Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=checkThread" );
			}
		}
	}
	
	private void exitThread()
	{
		synchronized( threadSync )
		{
			if( handlerThread != null )
			{
				if( enable_Statistics_LOG )
					Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=exitThread" );
				handlerThread.quit();
				handlerThread = null;
				mHandler = null;
			}
		}
	}
	
	/**
	 * 清除日志信息
	 */
	private void delAllData()
	{
		if( mContext != null )
		{
			LogDB logDB = new LogDB( mContext );
			logDB.open();
			for( int i = 0 ; i < idSet.size() ; i++ )
			{
				logDB.deleteRecord( idSet.get( i ) );
			}
			idSet.clear();
			logDB.close();
			if( isUseLog )
			{
				isUseLog = false;
				resetUseCount();
			}
		}
	}
	
	/**
	 * 获取<=50条日志信息并拼接
	 */
	private String getLogInformation()
	{
		String text = "";
		if( mContext == null )
		{
			return null;
		}
		LogDB logDB = new LogDB( mContext );
		logDB.open();
		Cursor cursor = logDB.getAllLogText();
		boolean isSingleLog = false;
		String log0017 = null;
		String log0017List = null;
		for( int i = 0 ; i < LOGTEXT_NUM && cursor.moveToNext() ; i++ )
		{
			String id = cursor.getString( cursor.getColumnIndexOrThrow( "_id" ) );
			String logtext = cursor.getString( cursor.getColumnIndexOrThrow( "logtext" ) );
			String[] itemsTemp = logtext.split( "#" );
			if( itemsTemp[0].equals( ACTION_USE_LOG ) )
			{
				isUseLog = true;
			}
			idSet.add( Integer.valueOf( id ) );
			if( enable_Statistics_LOG )
				Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=getLogInformation" + "\n--id = " + Integer.valueOf( id ) + "  logText = " + logtext );
			if( cursor.getCount() == 1 )
			{
				text = getParams( logtext , true );
				isSingleLog = true;
				break;
			}
			else
			{
				if( log0017 == null )
				{
					log0017 = getParams0017NoMd5();
				}
				text = getParams( logtext , false );
				log0017List = getParams0017List( log0017List , text );
			}
		}
		cursor.close();
		logDB.close();
		if( !isSingleLog )
		{
			if( ( log0017 != null ) && ( log0017List != null ) )
			{
				text = getParams0017WithMd5( log0017 , log0017List );
			}
			else
			{
				text = null;
			}
		}
		if( enable_Statistics_LOG )
			Log.v( "UICenter - StatisticsNew" , LOG_TAG + "\n=getLogInformation" + "\n--informationsize = " + idSet.size() + "\n--text = " + text );
		return text;
	}
	
	private String getParams0017WithMd5(
			String logtext ,
			String listItems )
	{
		JSONArray array = null;
		try
		{
			array = new JSONArray( listItems );
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject res = null;
		try
		{
			res = new JSONObject( logtext );
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			res.put( "list" , array );
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String content = res.toString();
		String md5_res = getMD5EncruptKey( content + DEFAULT_KEY );
		String newContent = content.substring( 0 , content.lastIndexOf( '}' ) );
		String params = newContent + ",\"md5\":\"" + md5_res + "\"}";
		return params;
	}
	
	private String getParams0017List(
			String logtext ,
			String listItem )
	{
		JSONArray array = null;
		if( logtext == null )
		{
			array = new JSONArray();
		}
		else
		{
			try
			{
				array = new JSONArray( logtext );
			}
			catch( JSONException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		JSONObject res = null;
		try
		{
			res = new JSONObject( listItem );
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		array.put( res );
		return array.toString();
	}
	
	private String getParams0017NoMd5()
	{
		String action = "0017";
		String appid = null;
		String sn = null;
		PackageManager pm;
		JSONObject res;
		int networktype = -1;
		int networksubtype = -1;
		ConnectivityManager connMgr = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if( netInfo != null )
		{
			networktype = netInfo.getType();
			networksubtype = netInfo.getSubtype();
		}
		appid = Assets.getAppId( mContext );
		sn = Assets.getSerialNo( mContext );
		if( appid == null || sn == null )
			return null;
		pm = mContext.getPackageManager();
		res = new JSONObject();
		try
		{
			res.put( "Action" , action );
			res.put( "packname" , mContext.getPackageName() );
			res.put( "versioncode" , pm.getPackageInfo( mContext.getPackageName() , 0 ).versionCode );
			res.put( "versionname" , pm.getPackageInfo( mContext.getPackageName() , 0 ).versionName );
			res.put( "sn" , sn );
			res.put( "appid" , appid );
			res.put( "shellid" , getShellID() );
			res.put( "timestamp" , 0 );
			res.put( "uuid" , Installation.id( mContext ) );
			TelephonyManager mTelephonyMgr = (TelephonyManager)mContext.getSystemService( Context.TELEPHONY_SERVICE );
			res.put( "imsi" , mTelephonyMgr.getSubscriberId() == null ? "" : mTelephonyMgr.getSubscriberId() );
			res.put( "iccid" , mTelephonyMgr.getSimSerialNumber() == null ? "" : mTelephonyMgr.getSimSerialNumber() );
			res.put( "imei" , mTelephonyMgr.getDeviceId() == null ? "" : mTelephonyMgr.getDeviceId() );
			res.put( "phone" , mTelephonyMgr.getLine1Number() == null ? "" : mTelephonyMgr.getLine1Number() );
			java.text.DateFormat format = new java.text.SimpleDateFormat( "yyyyMMddhhmmss" );
			res.put( "localtime" , format.format( new Date() ) );
			res.put( "model" , Build.MODEL );
			res.put( "display" , Build.DISPLAY );
			res.put( "product" , Build.PRODUCT );
			res.put( "device" , Build.DEVICE );
			res.put( "board" , Build.BOARD );
			res.put( "manufacturer" , Build.MANUFACTURER );
			res.put( "brand" , Build.BRAND );
			res.put( "hardware" , Build.HARDWARE );
			res.put( "buildversion" , Build.VERSION.RELEASE );
			res.put( "sdkint" , Build.VERSION.SDK_INT );
			res.put( "androidid" , android.provider.Settings.Secure.getString( mContext.getContentResolver() , android.provider.Settings.Secure.ANDROID_ID ) );
			res.put( "buildtime" , Build.TIME );
			res.put( "heightpixels" , mContext.getResources().getDisplayMetrics().heightPixels );
			res.put( "widthpixels" , mContext.getResources().getDisplayMetrics().widthPixels );
			res.put( "networktype" , networktype );
			res.put( "networksubtype" , networksubtype );
			res.put( "producttype" , 4 );
			res.put( "productname" , "uipersonalcenter" );
			res.put( "opversion" , getVersion() );
			res.put( "count" , 0 );
			String content = res.toString();
			String params = content;
			return params;
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private int getUseCount()
	{
		SharedPreferences sharedPrefer = PreferenceManager.getDefaultSharedPreferences( mContext );
		int count = sharedPrefer.getInt( "useCount" , 0 );
		return count;
	}
	
	private void resetUseCount()
	{
		PreferenceManager.getDefaultSharedPreferences( mContext ).edit().putInt( "useCount" , 0 ).commit();
	}
	
	private String getVersion()
	{
		String clientVersionCode = VERSION_CODE;
		String interfaceVersionCode = "0.0";
		String appVersionCode = APP_VERSION_CODE;
		return clientVersionCode + "." + interfaceVersionCode + "." + appVersionCode;
	}
	
	private String getShellID()
	{
		// return "R001_SHELLTST";
		return "";//CooeeSdk.cooeeGetCooeeId( mContext );
	}
	
	private String getMD5EncruptKey(
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
		// Log.v("http", "getMD5EncruptKey res =  " + res);
		return res;
	}
	
	protected char hexDigits[] = { '0' , '1' , '2' , '3' , '4' , '5' , '6' , '7' , '8' , '9' , 'a' , 'b' , 'c' , 'd' , 'e' , 'f' };
	
	private String bufferToHex(
			byte bytes[] )
	{
		return bufferToHex( bytes , 0 , bytes.length );
	}
	
	private String bufferToHex(
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
	
	private void appendHexPair(
			byte bt ,
			StringBuffer stringbuffer )
	{
		char c0 = hexDigits[( bt & 0xf0 ) >> 4]; // 取字节中�?4 位的数字转换, >>>
													// 为逻辑右移，将符号位一起右�?此处未发现两种符号有何不�?
		char c1 = hexDigits[bt & 0xf]; // 取字节中�?4 位的数字转换
		stringbuffer.append( c0 );
		stringbuffer.append( c1 );
	}
	
	private String getParams(
			String logText ,
			boolean isAddMd5 )
	{
		String action = null;
		String resid = null;
		String packageName = null;
		String[] itemsTemp = logText.split( "#" );
		int len = itemsTemp.length;
		if( len > 0 )
		{
			action = itemsTemp[0];
			if( len > 1 )
				resid = itemsTemp[1];
			if( len > 2 )
				packageName = itemsTemp[2];
		}
		String appid = null;
		String sn = null;
		PackageManager pm;
		JSONObject res;
		int networktype = -1;
		int networksubtype = -1;
		ConnectivityManager connMgr = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if( netInfo != null )
		{
			networktype = netInfo.getType();
			networksubtype = netInfo.getSubtype();
		}
		appid = Assets.getAppId( mContext );
		String channelString = AnalyticsConfig.getChannel(mContext);
		if( MainActivity.sbGoogleVersion==true && channelString.equals( "amazon" ))
		{
			if(appid.equals( "f2986" ))
			{
				appid="f566";
			}
		}
		sn = Assets.getSerialNo( mContext );
		Log.v( "downloadlist" , "sn = " + sn );
		if( appid == null || sn == null )
			return null;
		pm = mContext.getPackageManager();
		res = new JSONObject();
		try
		{
			res.put( "Action" , action );
			if( isAddMd5 )
			{
				res.put( "packname" , mContext.getPackageName() );
				res.put( "versioncode" , pm.getPackageInfo( mContext.getPackageName() , 0 ).versionCode );
				res.put( "versionname" , pm.getPackageInfo( mContext.getPackageName() , 0 ).versionName );
				res.put( "sn" , sn );
				res.put( "appid" , appid );
				res.put( "shellid" , getShellID() );
				res.put( "timestamp" , 0 );
				res.put( "uuid" , Installation.id( mContext ) );
				TelephonyManager mTelephonyMgr = (TelephonyManager)mContext.getSystemService( Context.TELEPHONY_SERVICE );
				res.put( "imsi" , mTelephonyMgr.getSubscriberId() == null ? "" : mTelephonyMgr.getSubscriberId() );
				res.put( "iccid" , mTelephonyMgr.getSimSerialNumber() == null ? "" : mTelephonyMgr.getSimSerialNumber() );
				res.put( "imei" , mTelephonyMgr.getDeviceId() == null ? "" : mTelephonyMgr.getDeviceId() );
				res.put( "phone" , mTelephonyMgr.getLine1Number() == null ? "" : mTelephonyMgr.getLine1Number() );
				java.text.DateFormat format = new java.text.SimpleDateFormat( "yyyyMMddhhmmss" );
				res.put( "localtime" , format.format( new Date() ) );
				res.put( "model" , Build.MODEL );
				res.put( "display" , Build.DISPLAY );
				res.put( "product" , Build.PRODUCT );
				res.put( "device" , Build.DEVICE );
				res.put( "board" , Build.BOARD );
				res.put( "manufacturer" , Build.MANUFACTURER );
				res.put( "brand" , Build.BRAND );
				res.put( "hardware" , Build.HARDWARE );
				res.put( "buildversion" , Build.VERSION.RELEASE );
				res.put( "sdkint" , Build.VERSION.SDK_INT );
				res.put( "androidid" , android.provider.Settings.Secure.getString( mContext.getContentResolver() , android.provider.Settings.Secure.ANDROID_ID ) );
				res.put( "buildtime" , Build.TIME );
				res.put( "heightpixels" , mContext.getResources().getDisplayMetrics().heightPixels );
				res.put( "widthpixels" , mContext.getResources().getDisplayMetrics().widthPixels );
				res.put( "networktype" , networktype );
				res.put( "networksubtype" , networksubtype );
				res.put( "producttype" , 4 );
				res.put( "productname" , "uipersonalcenter" );
				res.put( "count" , 0 );
				res.put( "opversion" , getVersion() );
				Log.v( "downloadlist" , logText + "下载线程启动" );
			}
			if( action.equals( ACTION_USE_LOG ) )
			{
				res.put( "param1" , "" );
				res.put( "param2" , "" );
				res.put( "count" , getUseCount() );
			}
			else if( action.equals( ACTION_UNINSTALL_LOG ) )
			{
				res.put( "param1" , resid );
				res.put( "param2" , packageName );
				res.put( "count" , 0 );
			}
			else if( action.equals( ACTION_DOWNLOAD_LOG ) || action.equals( ACTION_INSTALL_LOG ) )
			{
				res.put( "param1" , resid );
				res.put( "param2" , packageName );
				res.put( "count" , 0 );
			}
			String content = res.toString();
			String params = content;
			if( isAddMd5 )
			{
				String md5_res = getMD5EncruptKey( content + DEFAULT_KEY );
				// res.put("md5", md5_res);
				String newContent = content.substring( 0 , content.lastIndexOf( '}' ) );
				params = newContent + ",\"md5\":\"" + md5_res + "\"}";
			}
			return params;
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 判断是否联网
	 */
	private boolean IsHaveInternet(
			final Context context )
	{
		try
		{
			ConnectivityManager manger = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo info = manger.getActiveNetworkInfo();
			return( info != null && info.isConnected() );
		}
		catch( Exception e )
		{
			return false;
		}
	}
	
	private final String ACTION_TAB = "1302";
	public static final String ACTION_TAB_CHANGED = "com.coco.action.TAB_CHANGED";
	private final String TAB_DATE = "tabDate";
	private List<Map<String , String>> tabInfo = new ArrayList<Map<String , String>>();
	private DownloadTabThread downTabThread = null;
	private Object syncTabObject = new Object();
	
	public void downTab()
	{
		synchronized( this.syncTabObject )
		{
			if( downTabThread == null )
			{
				downTabThread = new DownloadTabThread();
				downTabThread.start();
			}
		}
	}
	
	public void stopDownloadTab()
	{
		if( downTabThread != null )
		{
			downTabThread.stopRun();
			downTabThread = null;
		}
	}
	
	private void downloadTabFinish()
	{
		Log.v( LOG_TAG , "downloadListFinish:" + ACTION_TAB_CHANGED );
		ConfigurationTabService sv = new ConfigurationTabService( mContext );
		sv.clearTable();
		sv.batchInsert( tabInfo );
		saveTabTime();
		Intent intent = new Intent();
		intent.setAction( ACTION_TAB_CHANGED );
		mContext.sendBroadcast( intent );
	}
	
	private void saveTabTime()
	{
		Time curTime = new Time();
		String curDateString = curTime.format( "yyyyMMdd" );
		SharedPreferences sharedPrefer = PreferenceManager.getDefaultSharedPreferences( mContext );
		Editor edit = sharedPrefer.edit();
		edit.putString( TAB_DATE , curDateString );
		edit.commit();
	}
	
	public boolean isRefreshTab()
	{
		SharedPreferences sharedPrefer = PreferenceManager.getDefaultSharedPreferences( mContext );
		String downListDate = sharedPrefer.getString( TAB_DATE , "" );
		Time curTime = new Time();
		String curDateString = curTime.format( "yyyyMMdd" );
		if( curDateString.equals( downListDate ) )
		{
			return false;
		}
		return true;
	}
	
	/*
	 * 下载tab的线程
	 */
	private class DownloadTabThread extends Thread
	{
		
		private volatile boolean isExit = false;
		
		public DownloadTabThread()
		{
		}
		
		public void stopRun()
		{
			isExit = true;
		}
		
		@Override
		public void run()
		{
			String url = SERVER_URL_TEST;
			String params = getParams( ACTION_TAB , true );
			boolean isSucceed = false;
			if( params != null )
			{
				CustomerHttpClient client = new CustomerHttpClient( mContext );
				String[] res = client.post( url , params );
				if( isExit )
				{
					synchronized( syncTabObject )
					{
						downTabThread = null;
						return;
					}
				}
				if( res != null )
				{
					String content = res[0];
					Log.v( "downloadtab" , "content = " + content );
					JSONObject json = null;
					try
					{
						json = new JSONObject( content );
						int retCode = json.getInt( "retcode" );
						if( retCode == 0 )
						{
							String configUrl = json.getString( "tablist" );
							json = new JSONObject( configUrl );
							tabInfo.clear();
							for( Iterator iter = json.keys() ; iter.hasNext() ; )
							{
								String key = (String)iter.next();
								JSONObject tmJson = (JSONObject)json.get( key );
								Map<String , String> map = new HashMap<String , String>();
								map.put( "tabid" , tmJson.getString( "tabid" ) );
								map.put( "enname" , tmJson.getString( "enname" ) );
								map.put( "cnname" , tmJson.getString( "cnname" ) );
								map.put( "twname" , tmJson.getString( "twname" ) );
								map.put( "id" , key );
								tabInfo.add( map );
							}
							Collections.sort( tabInfo , new ByStringValue() );
							isSucceed = true;
						}
					}
					catch( JSONException e )
					{
						e.printStackTrace();
					}
				}
			}
			if( isSucceed )
			{
				downloadTabFinish();
			}
			synchronized( syncTabObject )
			{
				downTabThread = null;
				return;
			}
		}
	}
	
	class ByStringValue implements Comparator<Map<String , String>>
	{
		
		@Override
		public int compare(
				Map<String , String> lhs ,
				Map<String , String> rhs )
		{
			// TODO Auto-generated method stub
			try
			{
				int left = Integer.parseInt( lhs.get( "id" ) );
				int right = Integer.parseInt( rhs.get( "id" ) );
				if( left > right )
				{
					return 1;
				}
				else if( left < right )
				{
					return -1;
				}
				return 0;
			}
			catch( Exception e )
			{
				return 0;
			}
		}
	}
}
