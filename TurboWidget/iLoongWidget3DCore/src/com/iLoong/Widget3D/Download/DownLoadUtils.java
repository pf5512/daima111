// xiatian add whole file //OperateFolder
package com.iLoong.Widget3D.Download;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;


public class DownLoadUtils
{
	
	public static final int DPI_DEFAULT = 0;
	public static final int DPI_LOW = 1;
	public static final int DPI_MEDIUM = 2;
	public static final int DPI_HIGH = 3;
	public static final int DPI_XHIGH = 4;
	public static final String APP_rCN = "appchname";
	public static final String APP_rTW = "appbigname";
	public static final String APP_default = "appname";
	public static final String FOLDER_rCN = "foldercname";
	public static final String FOLDER_rTW = "folderbname";
	public static final String FOLDER_default = "folderename";
	
	public static String getAppName(
			String local )
	{
		if( local.contains( "CN" ) )
			return APP_rCN;
		else if( local.contains( "TW" ) )
			return APP_rTW;
		else
			return APP_default;
	}
	
	public static String getFolderName(
			String local )
	{
		if( local.contains( "CN" ) )
			return FOLDER_rCN;
		else if( local.contains( "TW" ) )
			return FOLDER_rTW;
		else
			return FOLDER_default;
	}
	
	public static int getDPI(
			Context context )
	{
		int dpi = context.getResources().getDisplayMetrics().densityDpi;
		switch( dpi )
		{
			case 120:
				return DPI_LOW;
			case 160:
				return DPI_MEDIUM;
			case 240:
				return DPI_HIGH;
			case 320:
				return DPI_XHIGH;
		}
		return DPI_DEFAULT;
	}
	
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
}
