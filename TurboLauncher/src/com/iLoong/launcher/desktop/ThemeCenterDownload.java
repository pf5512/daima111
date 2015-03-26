package com.iLoong.launcher.desktop;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.RemoteViews;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.core.Assets;
import com.iLoong.launcher.core.CustomerHttpClient;
import com.iLoong.launcher.macinfo.Installation;


public class ThemeCenterDownload
{
	
	//	public static final String SERVER_URL_TEST = "http://58.246.135.237:20180/iloong/pui/ServicesEngine/DataService";
	public static final String SERVER_URL_TEST = "http://uifolder.coolauncher.com.cn/iloong/pui/ServicesEngine/DataService";
	private static Context context;
	public static final int MSG_UPDATE_PROGRESS = 0;
	public static final int MSG_FAIL = 1;
	public static final int MSG_SUCCESS = 2;
	private boolean watchNetwork = false;
	private static String curDownloadPkgName = null;
	private static String curResID = null;
	public static int notifyID = 20130710;
	public static final int REQUEST_ACTION_URL = 1301;
	public static Handler handler;
	public static ArrayList<DownloadingItem> mDownloadingList = new ArrayList<DownloadingItem>();
	public static HashMap<String , Integer> mDownloadFinish = new HashMap<String , Integer>();
	public static final String DEFAULT_KEY = "f24657aafcb842b185c98a9d3d7c6f4725f6cc4597c3a4d531c70631f7c7210fd7afd2f8287814f3dfa662ad82d1b02268104e8ab3b2baee13fab062b3d27bff";
	
	static class DownloadingItem
	{
		
		String packageName;
		String title;
		int id;
		Notification notification;
	}
	
	public ThemeCenterDownload()
	{
	}
	
	public void ToDownloadApkDialog(
			Context context ,
			final String title ,
			final String pkgName )
	{
		this.context = context;
		AlertDialog.Builder builder = new Builder( context );
		builder.setIcon( RR.drawable.ic_launcher );
		builder.setTitle( title );
		builder.setMessage( title + this.context.getResources().getString( RR.string.widget_download_content ) );
		builder.setPositiveButton( this.context.getResources().getString( RR.string.circle_ok_action ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				downloadAndInstallApp( title , pkgName , "122" );
				dialog.dismiss();
			}
		} );
		builder.setNegativeButton( this.context.getResources().getString( RR.string.circle_cancel_action ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
			}
		} );
		builder.create().show();
	}
	
	// 下载并安装APK
	public synchronized void downloadAndInstallApp(
			final String title ,
			final String pkgName ,
			final String resID )
	{
		String mDownloadFilePath = getDownloadFilePath();
		File dir = new File( mDownloadFilePath );
		if( !dir.isDirectory() )
			dir.delete();
		dir.mkdirs();
		final String path = mDownloadFilePath + "/" + pkgName + ".apk";
		if( verifyAPKFile( context , path ) == 2 )
		{
			installAPKFile( context , path );
			return;
		}
		new Thread() {
			
			@Override
			public void run()
			{
				super.run();
				( (Activity)context ).runOnUiThread( new Runnable() {
					
					@Override
					public void run()
					{
						downloadFile( context , title , path , pkgName , resID );
					}
				} );
			}
		}.start();
	}
	
	private String getDownloadFilePath()
	{
		String sdDir = getSDPath();
		if( sdDir == null )
		{
			return null;
		}
		return( sdDir + "/" + "Coco/download" );
	}
	
	// 生成请求参数
	public static String getParams(
			int action )
	{
		String appid = null;
		String sn = null;
		JSONObject tmp = Assets.config;
		PackageManager pm;
		JSONObject res;
		int networktype = -1;
		int networksubtype = -1;
		ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if( netInfo != null )
		{
			networktype = netInfo.getType();
			networksubtype = netInfo.getSubtype();
		}
		switch( action )
		{
			case REQUEST_ACTION_URL:
				if( tmp != null )
				{
					try
					{
						JSONObject config = tmp.getJSONObject( "config" );
						appid = config.getString( "app_id" );
						sn = config.getString( "serialno" );
					}
					catch( JSONException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if( appid == null || sn == null )
					return null;
				pm = context.getPackageManager();
				res = new JSONObject();
				try
				{
					res.put( "Action" , REQUEST_ACTION_URL + "" );
					res.put( "packname" , context.getPackageName() );
					res.put( "versioncode" , pm.getPackageInfo( context.getPackageName() , 0 ).versionCode );
					res.put( "versionname" , pm.getPackageInfo( context.getPackageName() , 0 ).versionName );
					res.put( "sn" , sn );
					res.put( "appid" , appid );
					res.put( "timestamp" , "0" );
					res.put( "uuid" , Installation.id( context ) );
					TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService( Context.TELEPHONY_SERVICE );
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
					res.put( "androidid" , android.provider.Settings.Secure.getString( context.getContentResolver() , android.provider.Settings.Secure.ANDROID_ID ) );
					res.put( "buildtime" , Build.TIME );
					res.put( "heightpixels" , context.getResources().getDisplayMetrics().heightPixels );
					res.put( "widthpixels" , context.getResources().getDisplayMetrics().widthPixels );
					res.put( "networktype" , networktype );
					res.put( "networksubtype" , networksubtype );
					res.put( "producttype" , 4 );
					res.put( "productname" , "uifolder" );
					res.put( "respackname" , "com.iLoong.base.themebox" );
					res.put( "count" , 0 );
					res.put( "resid" , curResID );
					String content = res.toString();
					String newContent = content.substring( 0 , content.lastIndexOf( '}' ) );
					String params = newContent;
					return params;
				}
				catch( Exception e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
		}
		return null;
	}
	
	public synchronized static void downloadFile(
			final Context context ,
			final String title ,
			final String path ,
			final String pkgName ,
			final String resID )
	{
		if( isDownloading( pkgName ) )
		{
			Messenger.sendMsg( Messenger.MSG_TOAST , title + "正在下载" + "，请等待" );
			return;
		}
		initHandler( context );
		final Notification notification = new Notification( android.R.drawable.stat_sys_download , title , System.currentTimeMillis() );
		// notification.defaults |= Notification.DEFAULT_SOUND;
		// notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		RemoteViews contentView = new RemoteViews( context.getPackageName() , RR.layout.operate_folder_notification );
		contentView.setTextViewText( RR.id.notificationTitle , context.getResources().getString( RR.string.notify_downloading ) + title );
		contentView.setTextViewText( RR.id.notificationPercent , "0%" );
		contentView.setProgressBar( RR.id.notificationProgress , 100 , 0 , true );
		notification.contentView = contentView;
		Intent intent = new Intent();
		intent.setClassName( context , "com.iLoong.launcher.desktop.iLoongLauncher" );
		intent.putExtra( "OperateFolderNotifyID" , notifyID );
		PendingIntent contentIntent = PendingIntent.getActivity( context , (int)System.currentTimeMillis() , intent , PendingIntent.FLAG_UPDATE_CURRENT );
		notification.contentIntent = contentIntent;
		NotificationManager mNotificationManager = (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );
		final int id = notifyID;
		mNotificationManager.notify( id , notification );
		addToDownloadList( pkgName , title , id , notification );
		notifyID++;
		Thread thread = new Thread() {
			
			@Override
			public void run()
			{
				super.run();
				final String url = getDownloadUrl( pkgName , resID );
				if( url == null )
				{
					if( handler != null )
					{
						RemoteViews contentView2 = notification.contentView;
						contentView2.setTextViewText( RR.id.notificationTitle , title + context.getResources().getString( RR.string.notify_download_fail ) );
						handler.sendMessage( Message.obtain( handler , MSG_FAIL , 0 , id , notification ) );
						removeFromDownloadList( pkgName );
					}
					return;
				}
				Log.i( "OPFolder" , "url=" + url );
				InputStream in = null;
				try
				{
					long downloadLength = 0;
					long totalLength = 0;
					int progress = 0;
					RandomAccessFile fos = null;
					final File file = new File( path );
					if( file.exists() )
					{
						long curPosition = file.length();
						if( curPosition == -1 )
						{
							file.delete();
							file.createNewFile();
						}
						else
						{
							downloadLength = curPosition;
							fos = new RandomAccessFile( file , "rw" );
							fos.seek( downloadLength );
						}
					}
					else
						file.createNewFile();
					if( fos == null )
						fos = new RandomAccessFile( file , "rw" );
					totalLength = getDownloadLength( url );
					in = sendDownload( url , downloadLength , totalLength );
					if( in == null )
					{
						if( handler != null )
						{
							RemoteViews contentView2 = notification.contentView;
							contentView2.setTextViewText( RR.id.notificationTitle , title + context.getResources().getString( RR.string.notify_download_fail ) );
							handler.sendMessage( Message.obtain( handler , MSG_FAIL , 0 , id , notification ) );
							removeFromDownloadList( pkgName );
						}
						// ackDownloadFail(info);
						return;
					}
					byte[] buf = new byte[256];
					while( true )
					{
						if( url != null )
						{
							int numRead = in.read( buf );
							if( numRead <= 0 )
							{
								fos.close();
								if( downloadLength == 0 )
								{
									file.delete();
									// ackDownloadFail(info);
									return;
								}
								installAPKFile( context , path );
								if( handler != null )
								{
									Intent intent = new Intent();
									intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
									intent.setAction( android.content.Intent.ACTION_VIEW );
									intent.setDataAndType( Uri.fromFile( new File( path ) ) , "application/vnd.android.package-archive" );
									PendingIntent contentIntent = PendingIntent.getActivity( context , 0 , intent , 0 );
									notification.contentIntent = contentIntent;
									RemoteViews contentView1 = notification.contentView;
									contentView1.setTextViewText( RR.id.notificationTitle , title + context.getResources().getString( RR.string.notify_download_finish ) );
									handler.sendMessage( Message.obtain( handler , MSG_SUCCESS , 100 , id , notification ) );
									removeFromDownloadList( pkgName );
									mDownloadFinish.put( pkgName , id );
								}
								// ackDownloadFinish(info);
								break;
							}
							else
							{
								fos.write( buf , 0 , numRead );
								downloadLength += numRead;
								int tmp = (int)( downloadLength * 100 / totalLength );
								if( tmp != progress && progress != 100 && tmp > 0 )
								{
									progress = tmp;
									if( progress > 100 )
										progress = 100;
									if( handler != null )
									{
										handler.sendMessage( Message.obtain( handler , MSG_UPDATE_PROGRESS , progress , id , notification ) );
									}
								}
							}
						}
						else
						{
							// ackDownloadFail(info);
							break;
						}
					}
				}
				catch( IOException e )
				{
					if( handler != null )
					{
						RemoteViews contentView2 = notification.contentView;
						contentView2.setTextViewText( RR.id.notificationTitle , title + context.getResources().getString( RR.string.notify_download_fail ) );
						handler.sendMessage( Message.obtain( handler , MSG_FAIL , 0 , id , notification ) );
						removeFromDownloadList( pkgName );
					}
				}
				finally
				{
					try
					{
						if( in != null )
						{
							in.close();
						}
					}
					catch( IOException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
	}
	
	public synchronized static void initHandler(
			final Context context )
	{
		if( handler == null )
		{
			Runnable runnable = new Runnable() {
				
				@Override
				public void run()
				{
					handler = new Handler() {
						
						@Override
						public void handleMessage(
								Message msg )
						{
							super.handleMessage( msg );
							switch( msg.what )
							{
								case MSG_UPDATE_PROGRESS:
									Notification notification = (Notification)msg.obj;
									RemoteViews contentView = notification.contentView;
									contentView.setTextViewText( RR.id.notificationPercent , msg.arg1 + "%" );
									contentView.setProgressBar( RR.id.notificationProgress , 100 , msg.arg1 , false );
									NotificationManager mNotificationManager = (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );
									mNotificationManager.notify( msg.arg2 , (Notification)msg.obj );
									break;
								case MSG_SUCCESS:
									Notification notification1 = (Notification)msg.obj;
									notification1.icon = RR.drawable.download;
									notification1.flags = 0;
									notification1.flags |= Notification.FLAG_AUTO_CANCEL;
									RemoteViews contentView1 = notification1.contentView;
									// contentView1.setTextViewText(R.id.notificationTitle,
									// title);
									contentView1.setTextViewText( RR.id.notificationPercent , "100%" );
									contentView1.setViewVisibility( RR.id.notificationProgress , View.INVISIBLE );
									NotificationManager mNotificationManager2 = (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );
									mNotificationManager2.notify( msg.arg2 , (Notification)msg.obj );
									break;
								case MSG_FAIL:
									Notification notification2 = (Notification)msg.obj;
									notification2.icon = RR.drawable.download;
									notification2.flags = 0;
									notification2.flags |= Notification.FLAG_AUTO_CANCEL;
									RemoteViews contentView2 = notification2.contentView;
									contentView2.setViewVisibility( RR.id.notificationProgress , View.INVISIBLE );
									NotificationManager mNotificationManager3 = (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );
									mNotificationManager3.notify( msg.arg2 , (Notification)msg.obj );
									break;
							}
						}
					};
				}
			};
			( (Activity)context ).runOnUiThread( runnable );
		}
	}
	
	private static void addToDownloadList(
			String pkgName ,
			String title ,
			int id ,
			Notification notification )
	{
		DownloadingItem mDownloadingItem = new DownloadingItem();
		mDownloadingItem.packageName = pkgName;
		mDownloadingItem.title = title;
		mDownloadingItem.id = id;
		mDownloadingItem.notification = notification;
		mDownloadingList.add( mDownloadingItem );
	}
	
	private static void removeFromDownloadList(
			String pkgName )
	{
		DownloadingItem mDownloadingItem = getDownloadingItem( pkgName );
		if( mDownloadingItem != null )
		{
			mDownloadingList.remove( mDownloadingItem );
		}
	}
	
	private static boolean isDownloading(
			String pkgName )
	{
		DownloadingItem mDownloadingItem = getDownloadingItem( pkgName );
		if( mDownloadingItem != null )
		{
			return true;
		}
		return false;
	}
	
	private static DownloadingItem getDownloadingItem(
			String pkgName )
	{
		for( DownloadingItem mDownloadingItem : mDownloadingList )
		{
			if( mDownloadingItem.packageName.equals( pkgName ) )
			{
				return mDownloadingItem;
			}
		}
		return null;
	}
	
	public static void failAllDowningNotification(
			Context context )
	{
		for( DownloadingItem mDownloadingItem : mDownloadingList )
		{
			RemoteViews contentView2 = mDownloadingItem.notification.contentView;
			contentView2.setTextViewText( RR.id.notificationTitle , mDownloadingItem.title + context.getResources().getString( RR.string.notify_download_fail ) );
			handler.sendMessage( Message.obtain( handler , MSG_FAIL , 0 , mDownloadingItem.id , mDownloadingItem.notification ) );
			mDownloadingList.remove( mDownloadingItem );
		}
	}
	
	public void removeFinishNotification(
			Context context ,
			String pkgName )
	{
		if( mDownloadFinish == null )
			return;
		if( !mDownloadFinish.containsKey( pkgName ) )
			return;
		int id = mDownloadFinish.get( pkgName );
		NotificationManager mNotificationManager = (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );
		mNotificationManager.cancel( id );
		mDownloadFinish.remove( pkgName );
	}
	
	public static void removeFailNotification(
			Context context ,
			int id )
	{
		if( mDownloadingList != null )
		{
			for( DownloadingItem mDownloadingItem : mDownloadingList )
			{
				if( mDownloadingItem.id == id )
					return;
			}
		}
		NotificationManager mNotificationManager = (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );
		mNotificationManager.cancel( id );
	}
	
	public static String getShellID(
			Context context )
	{
		return "";
	}
	
	public static String getVersion()
	{
		// String clientVersionCode = OperateFolderProxy.VERSION_CODE;
		// String interfaceVersionCode =
		// ConfigBase.OPERATE_FOLDER_CLIENT_VERSION;
		// return clientVersionCode + "." + interfaceVersionCode;
		return "";
	}
	
	// public static String getAppName(String local){
	// if(local.contains("CN"))return APP_rCN;
	// else if(local.contains("TW"))return APP_rTW;
	// else return APP_default;
	// }
	//
	// public static String getFolderName(String local){
	// if(local.contains("CN"))return FOLDER_rCN;
	// else if(local.contains("TW"))return FOLDER_rTW;
	// else return FOLDER_default;
	// }
	// public static int getDPI(Context context){
	// int dpi = context.getResources().getDisplayMetrics().densityDpi;
	// switch(dpi){
	// case 120:
	// return DPI_LOW;
	// case 160:
	// return DPI_MEDIUM;
	// case 240:
	// return DPI_HIGH;
	// case 320:
	// return DPI_XHIGH;
	// }
	// return DPI_DEFAULT;
	// }
	public static String getSDPath()
	{
		File SDdir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals( android.os.Environment.MEDIA_MOUNTED );
		if( sdCardExist )
		{
			SDdir = Environment.getExternalStorageDirectory();
		}
		if( SDdir != null )
		{
			return SDdir.toString();
		}
		else
		{
			return null;
		}
	}
	
	// 0:文件不存在，1：文件存在但不完整，2：文件完整
	public static int verifyAPKFile(
			Context context ,
			String path )
	{
		File packageFile = new File( path );
		if( packageFile.exists() )
		{
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo( path , PackageManager.GET_ACTIVITIES );
			if( info != null )
			{
				return 2;
			}
			else
				return 1;
		}
		else
			return 0;
	}
	
	// 安装APK文件
	public static void installAPKFile(
			Context context ,
			String path )
	{
		Intent intent = new Intent();
		intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent.setAction( android.content.Intent.ACTION_VIEW );
		intent.setDataAndType( Uri.fromFile( new File( path ) ) , "application/vnd.android.package-archive" );
		context.startActivity( intent );
	}
	
	public static boolean isNetworkAvailable(
			Context context )
	{
		try
		{
			ConnectivityManager cm = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo info = cm.getActiveNetworkInfo();
			return( info != null && info.isConnected() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static InputStream sendDownload(
			String url ,
			long start ,
			long length )
	{
		try
		{
			String urlName;
			urlName = url;
			URL realUrl = new URL( urlName );
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setReadTimeout( 30000 );
			conn.setRequestProperty( "accept" , "*/*" );
			conn.setRequestProperty( "connection" , "Keep-Alive" );
			conn.setRequestProperty( "user-agent" , "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)" );
			conn.setAllowUserInteraction( true );
			// 设置当前线程下载的起点，终点
			conn.setRequestProperty( "Range" , "bytes=" + start + "-" + length );
			// 建立实际的连接
			conn.connect();
			return conn.getInputStream();
		}
		catch( Exception e )
		{
			// System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		return null;
	}
	
	public static long getDownloadLength(
			String url )
	{
		long length = 0;
		try
		{
			String urlName;
			urlName = url;
			URL realUrl = new URL( urlName );
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			conn.setReadTimeout( 30000 );
			conn.connect();
			length = conn.getContentLength();
			return length;
		}
		catch( Exception e )
		{
			// System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		return length;
	}
	
	public static InputStream sendGet(
			String url ,
			String params )
	{
		BufferedReader in = null;
		try
		{
			String urlName;
			if( ( params != null ) && ( !"".equals( params ) ) )
			{
				urlName = url + "?" + params;
			}
			else
			{
				urlName = url;
			}
			URL realUrl = new URL( urlName );
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setReadTimeout( 30000 );
			conn.setRequestProperty( "accept" , "*/*" );
			conn.setRequestProperty( "connection" , "Keep-Alive" );
			conn.setRequestProperty( "user-agent" , "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)" );
			// 建立实际的连接
			conn.connect();
			// 获取所有响应头字段
			return conn.getInputStream();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally
		{
			try
			{
				if( in != null )
				{
					in.close();
				}
			}
			catch( IOException ex )
			{
				ex.printStackTrace();
			}
		}
		return null;
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
			e.printStackTrace();
			return null;
		}
		messagedigest.update( logInfo.getBytes() );
		res = bufferToHex( messagedigest.digest() );
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
		char c0 = hexDigits[( bt & 0xf0 ) >> 4]; // 取字节中高 4 位的数字转换, >>>
													// 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
		char c1 = hexDigits[bt & 0xf]; // 取字节中低 4 位的数字转换
		stringbuffer.append( c0 );
		stringbuffer.append( c1 );
	}
	
	// 通过包名获取下载url
	public static String getDownloadUrl(
			String packageName ,
			String resID )
	{
		curDownloadPkgName = packageName;
		curResID = resID;
		String url = SERVER_URL_TEST;
		String params = getParams( REQUEST_ACTION_URL );
		String downloadUrl = null;
		if( params != null )
		{
			String[] res = CustomerHttpClient.post( url , params );
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
						// JSONArray urlList = json.getString("url");
						// if(urlList.length() > 0){
						// String[] urls = new String[urlList.length()];
						// for(int i = 0;i < urlList.length();i++){
						// JSONObject object = urlList.getJSONObject(i);
						// urls[i] = object.getString("url");
						// }
						// downloadUrl = urls[new
						// Random().nextInt(urlList.length())];
						// }
						downloadUrl = json.getString( "url" );
					}
				}
				catch( JSONException e )
				{
					e.printStackTrace();
				}
			}
		}
		return downloadUrl;
	}
}
