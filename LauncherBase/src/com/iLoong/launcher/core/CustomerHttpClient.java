package com.iLoong.launcher.core;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.macinfo.LaunchStatistics;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class CustomerHttpClient
{
	
	private static final String CHARSET = HTTP.UTF_8;
	private static HttpClient customerHttpClient;
	private static Context context;
	
	public CustomerHttpClient(
			Context context )
	{
		this.context = context;
	}
	
	public static synchronized HttpClient getHttpClient()
	{
		if( null == customerHttpClient )
		{
			HttpParams params = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion( params , HttpVersion.HTTP_1_1 );
			HttpProtocolParams.setContentCharset( params , CHARSET );
			HttpProtocolParams.setUseExpectContinue( params , true );
			HttpProtocolParams.setUserAgent( params , "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) " + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1" );
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
			// 使用线程安全的连接管理来创建HttpClient
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager( params , schReg );
			customerHttpClient = new DefaultHttpClient( conMgr , params );
		}
		return customerHttpClient;
	}
	
	public static String[] post(
			String url ,
			String content )
	{
		if( isNetworkAvailable() == false )
		{
			Log.v( "http" , "network unavailable---" );
			return null;
		}
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
				String strResult = ( resEntity == null ) ? null : EntityUtils.toString( resEntity , CHARSET );
				String[] res = new String[2];
				res[0] = strResult;
				res[1] = resEntity.getContentLength() + "";
				return res;
			}
			else
			{
				HttpEntity resEntity = response.getEntity();
				String strResult = ( resEntity == null ) ? null : EntityUtils.toString( resEntity , CHARSET );
				Log.v( "http" , "customerHttpClient post error = " + response.getStatusLine().getStatusCode() + " " + strResult );
				return null;
			}
		}
		catch( UnsupportedEncodingException e )
		{
			Log.v( "http" , "UnsupportedEncodingException...." + e.toString() );
		}
		catch( ClientProtocolException e )
		{
			Log.v( "http" , "ClientProtocolException...." + e.toString() );
		}
		catch( IOException e )
		{
			Log.v( "http" , "IOException...." + e.toString() );
		}
		return null;
	}
	
	public static boolean post(
			String url ,
			String content ,
			int type )
	{
		if( isNetworkAvailable() == false )
		{
			Log.v( "http" , "network unavailable---" );
			return false;
		}
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
				String strResult = ( resEntity == null ) ? null : EntityUtils.toString( resEntity , CHARSET );
				return parseResult( strResult , type );
			}
			else
			{
				HttpEntity resEntity = response.getEntity();
				String strResult = ( resEntity == null ) ? null : EntityUtils.toString( resEntity , CHARSET );
				Log.v( "http" , "customerHttpClient post error = " + response.getStatusLine().getStatusCode() + " " + strResult );
				return false;
			}
		}
		catch( UnsupportedEncodingException e )
		{
			Log.v( "http" , "UnsupportedEncodingException...." + e.toString() );
		}
		catch( ClientProtocolException e )
		{
			Log.v( "http" , "ClientProtocolException...." + e.toString() );
		}
		catch( IOException e )
		{
			Log.v( "http" , "IOException...." + e.toString() );
		}
		return false;
	}
	
	public static String post(
			String url ,
			NameValuePair ... params )
	{
		try
		{
			List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
			for( NameValuePair p : params )
			{
				formparams.add( p );
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity( formparams , CHARSET );
			HttpPost request = new HttpPost( url );
			request.setEntity( entity );
			HttpClient client = getHttpClient();
			HttpResponse response = client.execute( request );
			if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK )
			{
				HttpEntity resEntity = response.getEntity();
				//				Log.v("http", response.getStatusLine().getProtocolVersion() + " "
				//						+ response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
				//				for (int i = 0; i < response.getAllHeaders().length; i++) {
				//					Log.v("http", response.getAllHeaders()[i].getName() + ":" + response.getAllHeaders()[i].getValue());
				//				}
				//				Log.v("http", "");
				//				Log.v("http", EntityUtils.toString(resEntity, CHARSET));
				return ( resEntity == null ) ? null : EntityUtils.toString( resEntity , CHARSET );
			}
			else
			{
				//				Log.v("http", response.getStatusLine().getProtocolVersion() + " "
				//						+ response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
				//				for (int i = 0; i < response.getAllHeaders().length; i++) {
				//					Log.v("http", response.getAllHeaders()[i].getName() + ":" + response.getAllHeaders()[i].getValue());
				//				}
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
	
	public static boolean isNetworkAvailable()
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
	
	public static boolean parseResult(
			String resultString ,
			int type )
	{
		//Log.i("statistics", "post res = " + resultString);
		int res = 0;
		JSONObject res_obj = null;
		try
		{
			res_obj = new JSONObject( resultString );
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		try
		{
			if( type == LaunchStatistics.OLD_STATISTICS )
			{
				res = res_obj.getInt( "errno" );
			}
			else if( type == LaunchStatistics.NEW_STATISTICS )
			{
				res = res_obj.getInt( "code" );
			}
			else if( type == LaunchStatistics.NEW_STATISTICS_COUNT )
			{
				//Log.i("new count res", res_obj.toString());
				res = res_obj.getInt( "retcode" );
			}
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if( res != 0 )
			return false;
		else
			return true;
	}
	
	public static String post(
			String url ,
			List<NameValuePair> formparams )
	{
		if( isNetworkAvailable() == false )
		{
			return null;
		}
		//		Log.v("http", "url="+url);
		try
		{
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity( formparams , CHARSET );
			HttpPost request = new HttpPost( url );
			request.setEntity( entity );
			HttpClient client = getHttpClient();
			HttpResponse response = client.execute( request );
			if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK )
			{
				HttpEntity resEntity = response.getEntity();
				String strResult = ( resEntity == null ) ? null : EntityUtils.toString( resEntity , CHARSET );
				//				Log.v("http", response.getStatusLine().getProtocolVersion() + " "
				//						+ response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
				//				for (int i = 0; i < response.getAllHeaders().length; i++) {
				//					Log.v("http", response.getAllHeaders()[i].getName() + ":" + response.getAllHeaders()[i].getValue());
				//				}
				//				Log.v("http", "");
				//				Log.v("http", strResult);
				return strResult;
			}
			else
			{
				HttpEntity resEntity = response.getEntity();
				//				String strResult = (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
				//				Log.v("http", response.getStatusLine().getProtocolVersion() + " "
				//						+ response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
				//				for (int i = 0; i < response.getAllHeaders().length; i++) {
				//					Log.v("http", response.getAllHeaders()[i].getName() + ":" + response.getAllHeaders()[i].getValue());
				//				}
				//				Log.v("http", "");
				//				Log.v("http", strResult);
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
	
	public static String get(
			String url ,
			ArrayList<NameValuePair> heads )
	{
		NameValuePair temp;
		if( isNetworkAvailable() == false )
		{
			return null;
		}
		//		Log.v("http", "url="+url);
		try
		{
			HttpGet get = new HttpGet( url );
			for( int i = 0 ; i < heads.size() ; i++ )
			{
				temp = heads.get( i );
				get.addHeader( temp.getName() , temp.getValue() );
			}
			HttpClient client = getHttpClient();
			HttpResponse response = client.execute( get );
			if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK )
			{
				HttpEntity resEntity = response.getEntity();
				String strResult = ( resEntity == null ) ? null : EntityUtils.toString( resEntity , CHARSET );
				Log.v( "http" , "get ok res = " + strResult );
				return strResult;
			}
			else
			{
				Log.v( "http" , "get error = " );
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
}
