package com.iLoong.launcher.custom;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.iLoong.launcher.Desktop3D.Log;

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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class HttpHelper
{
	
	private static HttpClient httpClient;
	public static Context mContext;
	
	public HttpHelper(
			Context context )
	{
		mContext = context;
	}
	
	public static boolean isNetworkAvailable()
	{
		try
		{
			ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo info = cm.getActiveNetworkInfo();
			return( info != null && info.isConnected() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static synchronized HttpClient getHttpClient()
	{
		if( null == httpClient )
		{
			HttpParams params = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion( params , HttpVersion.HTTP_1_1 );
			HttpProtocolParams.setContentCharset( params , HTTP.UTF_8 );
			HttpProtocolParams.setUseExpectContinue( params , true );
			//            HttpProtocolParams.setUserAgent(params, "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
			//                    + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
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
			httpClient = new DefaultHttpClient( conMgr , params );
		}
		return httpClient;
	}
	
	public static synchronized boolean post(
			String url ,
			String content )
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
			Log.v( "retrieve" , "ackCode " + response.getStatusLine().getStatusCode() );
			if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK )
			{
				return true;
				//                HttpEntity resEntity = response.getEntity();
				//                String strResult = (resEntity == null) ? null : EntityUtils.toString(resEntity, HTTP.UTF_8);
				//                String[] res = new String[2];
				//                res[0] = strResult;
				//                res[1] = resEntity.getContentLength()+"";
				//                if(res[0]!=null)
				//                if(res[1]!=null)
				//                return res;
			}
			else
			{
				return false;
				//                HttpEntity resEntity = response.getEntity();
				//                String strResult = (resEntity == null) ? null : EntityUtils.toString(resEntity, HTTP.UTF_8);
				//                return null;
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
}
