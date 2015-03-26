package com.iLoong.Widget3D.Download;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;


public class DownloadManager
{
	
	private static String mPGName;
	private String DOWNLOAD_PATH = Environment.getExternalStorageDirectory() + "/Coco/download/";
	private String DOWNLOAD_APK_PATH = ".apk";
	private Context mContext;
	//server config
	public static final String SERVER_URL_TEST = "http://uifolder.coolauncher.com.cn/iloong/pui/ServicesEngine/DataService";
	public static final int REQUEST_ACTION_URL = 1003;
	public static final String DEFAULT_KEY = "f24657aafcb842b185c98a9d3d7c6f4725f6cc4597c3a4d531c70631f7c7210fd7afd2f8287814f3dfa662ad82d1b02268104e8ab3b2baee13fab062b3d27bff";
	public static boolean downloading = false;
	
	public DownloadManager(
			Context context ,
			String pgn )
	{
		mContext = context;
		mPGName = pgn;
		DOWNLOAD_APK_PATH = DOWNLOAD_PATH + mPGName + ".apk";
	}
	
	public int DownloadAPK(
			Context context ,
			String pgn )
	{
		boolean ret = false;
		File dir = new File( DOWNLOAD_PATH );
		dir.mkdirs();
		if( verifyAPKFile( mContext , DOWNLOAD_APK_PATH ) == 2 )
		{
			Log.e( "weijie" , "DownloadAPK installAPKFile" );
			installAPKFile( mContext , DOWNLOAD_APK_PATH );
			return 0;
		}
		if( !isNetworkAvailable( mContext ) )
		{
			Log.e( "weijie" , "DownloadAPK isNetworkAvailable false" );
			return 1;
		}
		Log.e( "weijie" , "DownloadAPK downloading=" + downloading );
		if( !downloading )
		{
			downloading = true;
			downloadFile( mContext , "" , DOWNLOAD_APK_PATH , mPGName , null );
		}
		else
			return 2;
		return 3;
	}
	
	//0:文件不存在，1：文件存在但不完整，2：文件完整
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
	
	//安装APK文件
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
	
	public boolean isNetworkAvailable(
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
	
	static class DownloadingItem
	{
		
		String packageName;
		String title;
		int id;
		Notification notification;
	}
	
	public static ArrayList<DownloadingItem> mDownloadingList = new ArrayList<DownloadingItem>();
	
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
	
	public synchronized static void downloadFile(
			final Context context ,
			final String title ,
			final String path ,
			final String pkgName ,
			final String resID )
	{
		Log.e( "weijie" , "downloadFile path=" + path );
		Log.e( "weijie" , "downloadFile pkgName=" + pkgName );
		Thread thread = new Thread() {
			
			@Override
			public void run()
			{
				super.run();
				String url = null;
				url = getDownloadUrl( context , pkgName , resID );
				Log.e( "weijie" , "downloadFile url =" + url );
				if( url == null )
				{
					return;
				}
				try
				{
					long downloadLength = 0;
					long totalLength = 0;
					int progress = 0;
					RandomAccessFile fos = null;
					totalLength = getDownloadLength( url );
					Log.e( "weijie" , "downloadFile totalLength =" + totalLength );
					final File file = new File( path );
					Log.e( "weijie" , "downloadFile file.exists() =" + file.exists() );
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
							if( downloadLength >= totalLength )
							{
								downloadLength = 0;
								file.delete();
								file.createNewFile();
							}
							else
							{
								fos = new RandomAccessFile( file , "rw" );
								fos.seek( downloadLength );
							}
							//Log.d(tag, "continue download:"+downloadLength);
						}
					}
					else
						file.createNewFile();
					if( fos == null )
						fos = new RandomAccessFile( file , "rw" );
					InputStream in = sendDownload( url , downloadLength , totalLength );
					if( in == null )
					{
						//ackDownloadFail(info);
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
									//ackDownloadFail(info);
									return;
								}
								installAPKFile( context , path );
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
								}
							}
						}
						else
						{
							//ackDownloadFail(info);
							break;
						}
					}
				}
				catch( IOException e )
				{
				}
				downloading = false;
			}
		};
		thread.start();
	}
	
	/*
	public static List<PackageInfo> getInstallAPKs(Context context)
	{
		return context.getPackageManager().getInstalledPackages(0);
	}
	
	public static boolean checkInstallAPK(Context context,String pkname)
	{
		boolean ret = false;
		List<PackageInfo> install = getInstallAPKs(context);
		if(install == null) return ret;
		
		for(int i=0;i<install.size();i++)
		{
			PackageInfo info = install.get(i);
			if(info.packageName.equals(pkname))
			{
				ret = true;
				break;
			}
		}
		return ret;
	}
	*/
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
			//设置当前线程下载的起点，终点  
			conn.setRequestProperty( "Range" , "bytes=" + start + "-" + length );
			// 建立实际的连接
			conn.connect();
			return conn.getInputStream();
		}
		catch( Exception e )
		{
			//System.out.println("发送GET请求出现异常！" + e);
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
			//System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		return length;
	}
	
	//通过包名获取下载url
	public synchronized static String getDownloadUrl(
			Context context ,
			String packageName ,
			String resID )
	{
		String curDownloadPkgName = packageName;
		String curResID = resID;
		String url = SERVER_URL_TEST;
		String params = getParams( context , REQUEST_ACTION_URL );
		String downloadUrl = null;
		Log.e( "weijie" , "getDownloadUrl params =" + params );
		if( params != null )
		{
			String[] res = post( url , params );
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
						JSONArray urlList = json.getJSONArray( "urllist" );
						if( urlList.length() > 0 )
						{
							String[] urls = new String[urlList.length()];
							for( int i = 0 ; i < urlList.length() ; i++ )
							{
								JSONObject object = urlList.getJSONObject( i );
								urls[i] = object.getString( "url" );
							}
							downloadUrl = urls[new Random().nextInt( urlList.length() )];
						}
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
	
	//生成请求参数
	private static String getParams(
			Context context ,
			int action )
	{
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
		String productname = "uifolder";
		//	if( folder_default_show )
		{
			productname = "uishowfolder";
		}
		Log.e( "weijie" , "getParams action=" + action );
		switch( action )
		{
			case REQUEST_ACTION_URL:
				pm = context.getPackageManager();
				res = new JSONObject();
				try
				{
					res.put( "Action" , REQUEST_ACTION_URL + "" );
					res.put( "packname" , context.getPackageName() );
					res.put( "versioncode" , pm.getPackageInfo( context.getPackageName() , 0 ).versionCode );
					res.put( "versionname" , pm.getPackageInfo( context.getPackageName() , 0 ).versionName );
					res.put( "sn" , "" );
					res.put( "appid" , "" );
					res.put( "shellid" , "" );
					res.put( "resid" , "" );
					res.put( "respackname" , mPGName );
					res.put( "uuid" , "" );
					TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService( Context.TELEPHONY_SERVICE );
					res.put( "imsi" , "" );
					res.put( "iccid" , "" );
					res.put( "imei" , "" );
					res.put( "phone" , "" );
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
					res.put( "productname" , productname );
					res.put( "count" , 0 );
					res.put( "opversion" , "" );
					String content = res.toString();
					String md5_res = getMD5EncruptKey( content + DEFAULT_KEY );
					//res.put("md5", md5_res);
					String newContent = content.substring( 0 , content.lastIndexOf( '}' ) );
					String params = newContent + ",\"md5\":\"" + md5_res + "\"}";
					Log.e( "weijie" , "getParams params=" + params );
					return params;
				}
				catch( Exception e )
				{
					// TODO Auto-generated catch block
					Log.e( "weijie" , "getParams Exception" );
					e.printStackTrace();
				}
				return null;
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
	
	public static String[] post(
			String url ,
			String content )
	{
		try
		{
			StringEntity entity = new StringEntity( content , HTTP.UTF_8 );
			HttpPost request = new HttpPost( url );
			request.setHeader( "Content-Type" , "application/json; charset=UTF-8" );
			request.setEntity( entity );
			HttpClient client = getHttpClient();
			HttpResponse response = client.execute( request );
			if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK )
			{
				HttpEntity resEntity = response.getEntity();
				String strResult = ( resEntity == null ) ? null : EntityUtils.toString( resEntity , HTTP.UTF_8 );
				String[] res = new String[2];
				res[0] = strResult;
				res[1] = resEntity.getContentLength() + "";
				return res;
			}
			else
			{
				HttpEntity resEntity = response.getEntity();
				String strResult = ( resEntity == null ) ? null : EntityUtils.toString( resEntity , HTTP.UTF_8 );
				return null;
			}
		}
		catch( UnsupportedEncodingException e )
		{
		}
		catch( ClientProtocolException e )
		{
		}
		catch( IOException e )
		{
		}
		return null;
	}
	
	public static synchronized HttpClient getHttpClient()
	{
		HttpParams params = new BasicHttpParams();
		// 设置一些基本参数
		HttpProtocolParams.setVersion( params , HttpVersion.HTTP_1_1 );
		HttpProtocolParams.setContentCharset( params , HTTP.UTF_8 );
		HttpProtocolParams.setUseExpectContinue( params , true );
		HttpProtocolParams.setUserAgent( params , "Android 2.2.1" );
		// 超时设置
		/* 从连接池中取连接的超时时间 */
		ConnManagerParams.setTimeout( params , 5000 );
		/* 连接超时 */
		HttpConnectionParams.setConnectionTimeout( params , 10000 );
		/* 请求超时 */
		HttpConnectionParams.setSoTimeout( params , 10000 );
		// 设置我们的HttpClient支持HTTP和HTTPS两种模式
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register( new Scheme( "http" , (SocketFactory)PlainSocketFactory.getSocketFactory() , 80 ) );
		schReg.register( new Scheme( "https" , (SocketFactory)SSLSocketFactory.getSocketFactory() , 443 ) );
		// 使用线程安全的连接管理来创建HttpClient
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager( params , schReg );
		return new DefaultHttpClient( conMgr , params );
	}
}
