package com.coco.download;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.coco.theme.themebox.util.Log;


public class CustomerHttpClient
{
	
	private static final String CHARSET = HTTP.UTF_8;
	private HttpClient customerHttpClient;
	private Context context;
	
	public CustomerHttpClient(
			Context context )
	{
		this.context = context;
	}
	
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
	
	public synchronized HttpClient getHttpClient()
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
			schReg.register( new Scheme( "https" , (SocketFactory)SSLSocketFactory.getSocketFactory() , 443 ) );
			// 使用线程安全的连接管理来创建HttpClient
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager( params , schReg );
			customerHttpClient = new DefaultHttpClient( conMgr , params );
		}
		return customerHttpClient;
	}
	
	public String[] post(
			String url ,
			String content )
	{
		if( IsHaveInternet( context ) == false )
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
}
