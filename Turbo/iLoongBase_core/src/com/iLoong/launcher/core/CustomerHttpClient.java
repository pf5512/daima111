package com.iLoong.launcher.core;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
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
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.macinfo.LaunchStatistics;


public class CustomerHttpClient
{
	
	private static final String CHARSET = HTTP.UTF_8;
	private static HttpClient customerHttpClient;
	private static Context context;
	static final byte[] decodekey = {
			22 ,
			-21 ,
			-32 ,
			-24 ,
			101 ,
			111 ,
			105 ,
			114 ,
			10 ,
			5 ,
			72 ,
			-76 ,
			-30 ,
			17 ,
			-109 ,
			-40 ,
			27 ,
			20 ,
			-126 ,
			-8 ,
			-46 ,
			-113 ,
			118 ,
			31 ,
			49 ,
			78 ,
			-18 ,
			95 ,
			-6 ,
			69 ,
			120 ,
			62 ,
			51 ,
			-119 ,
			113 ,
			-58 ,
			26 ,
			-101 ,
			-22 ,
			64 ,
			91 ,
			-56 ,
			-100 ,
			6 ,
			-42 ,
			-85 ,
			29 ,
			56 ,
			24 ,
			-39 ,
			16 ,
			-37 ,
			1 ,
			82 ,
			-120 ,
			-115 ,
			-9 ,
			-4 ,
			121 ,
			63 ,
			9 ,
			116 ,
			-5 ,
			-77 ,
			-59 ,
			-15 ,
			-13 ,
			-25 ,
			-111 ,
			48 ,
			97 ,
			4 ,
			32 ,
			19 ,
			-121 ,
			119 ,
			30 ,
			-43 ,
			-29 ,
			-118 ,
			-83 ,
			104 ,
			94 ,
			-74 ,
			110 ,
			-50 ,
			45 ,
			-45 ,
			-128 ,
			-75 ,
			55 ,
			-99 ,
			41 ,
			60 ,
			44 ,
			-78 ,
			-52 ,
			-92 ,
			52 ,
			-96 ,
			70 ,
			98 ,
			33 ,
			-41 ,
			-66 ,
			-91 ,
			107 ,
			109 ,
			61 ,
			37 ,
			-72 ,
			40 ,
			122 ,
			35 ,
			74 ,
			54 ,
			87 ,
			-107 ,
			-124 ,
			-68 ,
			65 ,
			80 ,
			-112 ,
			18 ,
			96 ,
			67 ,
			-14 ,
			-87 ,
			-16 ,
			-12 ,
			-57 ,
			-89 ,
			-63 ,
			3 ,
			-10 ,
			42 ,
			50 ,
			73 ,
			108 ,
			124 ,
			99 ,
			43 ,
			-26 ,
			-67 ,
			-2 ,
			-20 ,
			38 ,
			76 ,
			-44 ,
			21 ,
			-7 ,
			-31 ,
			117 ,
			2 ,
			66 ,
			46 ,
			25 ,
			-97 ,
			34 ,
			93 ,
			-71 ,
			-108 ,
			13 ,
			-36 ,
			92 ,
			-70 ,
			15 ,
			-69 ,
			106 ,
			36 ,
			88 ,
			123 ,
			-93 ,
			-79 ,
			-103 ,
			-51 ,
			-48 ,
			-94 ,
			77 ,
			-81 ,
			-17 ,
			79 ,
			0 ,
			103 ,
			-55 ,
			-19 ,
			-73 ,
			23 ,
			-49 ,
			83 ,
			-61 ,
			-102 ,
			-106 ,
			-95 ,
			7 ,
			-123 ,
			-127 ,
			-125 ,
			-35 ,
			90 ,
			-104 ,
			71 ,
			-84 ,
			-117 ,
			102 ,
			-105 ,
			-54 ,
			58 ,
			11 ,
			47 ,
			39 ,
			-23 ,
			127 ,
			-65 ,
			14 ,
			84 ,
			-80 ,
			-3 ,
			8 ,
			112 ,
			100 ,
			-33 ,
			-53 ,
			-122 ,
			-27 ,
			28 ,
			-110 ,
			68 ,
			59 ,
			12 ,
			-62 ,
			125 ,
			85 ,
			-64 ,
			89 ,
			-34 ,
			86 ,
			-90 ,
			-88 ,
			-28 ,
			57 ,
			-98 ,
			-11 ,
			53 ,
			126 ,
			115 ,
			-60 ,
			-82 ,
			-116 ,
			-38 ,
			-114 ,
			81 ,
			-86 ,
			-47 ,
			-1 ,
			75 };
	public static final String DEFAULT_KEY = "f24657aafcb842b185c98a9d3d7c6f4725f6cc4597c3a4d531c70631f7c7210fd7afd2f8287814f3dfa662ad82d1b02268104e8ab3b2baee13fab062b3d27bff";
	static final int COMPRESS_MIN_SIZE = 128;
	static final short HEADER_SIZE = 32;
	private static int sequence = 0;
	
	public CustomerHttpClient(
			Context context )
	{
		CustomerHttpClient.context = context;
	}
	
	public static void Translate(
			byte[] data ,
			byte[] key )
	{
		for( int i = 0 ; i < data.length ; i++ )
		{
			int d = data[i];
			if( d < 0 )
			{
				d = 256 + d;
			}
			data[i] = key[d];
		}
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
			customerHttpClient = new DefaultHttpClient( conMgr , params );
		}
		return customerHttpClient;
	}
	
	// public static String logHeaders(Header[] headers){
	// if(headers!=null){
	// StringBuffer sb = new StringBuffer();
	// for (int i = 0; i < headers.length; i++) {
	// sb.append(headers[i].getName());
	// sb.append(headers[i].getValue());
	// }
	// logData(sb.toString());
	// }
	// }
	public static String getParams(
			HttpParams httpParams )
	{
		StringBuffer sb = new StringBuffer();
		if( httpParams != null )
		{
			String userAgent = HttpProtocolParams.getUserAgent( httpParams );
			if( userAgent != null )
			{
				sb.append( "User-Agent:" + userAgent );
			}
			ProtocolVersion version = HttpProtocolParams.getVersion( httpParams );
			if( version != null )
			{
				sb.append( "version:" + version );
			}
		}
		return sb.toString();
	}
	
	public static void logRequestData(
			HttpClient client ,
			HttpPost request )
	{
		if( request != null )
		{
			String reqLine = request.getRequestLine().toString();
			StringBuffer header = new StringBuffer();
			Header[] headers = request.getAllHeaders();
			for( int i = 0 ; i < headers.length ; i++ )
			{
				header.append( headers[i].getName() );
				header.append( headers[i].getValue() );
			}
			HttpEntity entity = request.getEntity();
			if( entity != null )
			{
				header.append( "Content-Length:" + entity.getContentLength() );
			}
			String params = getParams( client.getParams() );
			String data = "";
			try
			{
				if( entity != null )
				{
					data = EntityUtils.toString( entity );
				}
			}
			catch( ParseException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logData( 0 , reqLine , "" , header.toString() , params , data );
		}
	}
	
	public static void logResponseData(
			HttpResponse response ,
			String data )
	{
		if( response != null )
		{
			String resLine = response.getStatusLine().toString();
			StringBuffer header = new StringBuffer();
			Header[] headers = response.getAllHeaders();
			for( int i = 0 ; i < headers.length ; i++ )
			{
				header.append( headers[i].getName() );
				header.append( headers[i].getValue() );
			}
			String params = getParams( response.getParams() );
			logData( 1 , "" , resLine , header.toString() , params , data );
		}
	}
	
	public static long logData(
			int type ,
			String reqLine ,
			String resLine ,
			String header ,
			String params ,
			String data )
	{
		try
		{
			ContentResolver resolver = context.getContentResolver();
			Uri uri = Uri.parse( "content://" + "com.coco.op.pub.provider" + "/" + "config" );
			ContentValues values = new ContentValues();
			values.put( "type" , type );
			values.put( "reqline" , reqLine );
			values.put( "resline" , resLine );
			values.put( "header" , header );
			values.put( "params" , params );
			values.put( "data" , data );
			Uri result = resolver.insert( uri , values );
			if( result != null )
			{
				return ContentUris.parseId( result );
			}
			else
			{
				return 0;
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		return 0;
	}
	
	public static void logSendGet(
			URLConnection conn ,
			Map<String , List<String>> properties ,
			String data )
	{
		try
		{
			if( conn != null )
			{
				StringBuilder params = new StringBuilder();
				if( properties != null )
				{
					Iterator<Entry<String , List<String>>> iter = properties.entrySet().iterator();
					while( iter.hasNext() )
					{
						Map.Entry<String , List<String>> entry = (Map.Entry<String , List<String>>)iter.next();
						Object key = entry.getKey();
						Object val = entry.getValue();
						params.append( key.toString() + val.toString() );
					}
				}
				StringBuilder header = new StringBuilder();
				Map<String , List<String>> fields = conn.getHeaderFields();
				if( fields != null )
				{
					Iterator<Entry<String , List<String>>> iter = fields.entrySet().iterator();
					while( iter.hasNext() )
					{
						Map.Entry<String , List<String>> entry = (Map.Entry<String , List<String>>)iter.next();
						Object key = entry.getKey();
						Object val = entry.getValue();
						header.append( key.toString() + val.toString() );
					}
				}
				logData( 2 , conn.getURL().toString() , "" , header.toString() , params.toString() , data );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	public static String sendGet(
			String url ,
			String params ,
			boolean calcDataSize )
	{
		InputStream inputStream = null;
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
			Map<String , List<String>> requestProperties = conn.getRequestProperties();
			conn.connect();
			// 获取所有响应头字段
			inputStream = conn.getInputStream();
			if( inputStream == null )
				return null;
			BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ) );
			String result = "";
			try
			{
				String line;
				while( ( line = reader.readLine() ) != null )
				{
					result += "\n" + line;
				}
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			if( calcDataSize )
			{
				logSendGet( conn , requestProperties , result );
			}
			return result;
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
				if( inputStream != null )
				{
					inputStream.close();
				}
			}
			catch( IOException ex )
			{
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	public static String[] post(
			String url ,
			String content ,
			boolean calcData )
	{
		if( !calcData )
		{
			return post( url , content );
		}
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
				logRequestData( client , request );
				HttpEntity resEntity = response.getEntity();
				String strResult = ( resEntity == null ) ? null : EntityUtils.toString( resEntity , CHARSET );
				logResponseData( response , strResult );
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
	
	public static ResultEntity sendGetEntity(
			String url ,
			String params )
	{
		InputStream inputStream = null;
		ResultEntity result = new ResultEntity();
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
			inputStream = conn.getInputStream();
			if( inputStream != null )
			{
				BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ) );
				StringBuilder sb = new StringBuilder();
				try
				{
					String line;
					while( ( line = reader.readLine() ) != null )
					{
						sb.append( "\n" );
						sb.append( line );
					}
					result.content = sb.toString();
					result.contentLength = result.content.length();
				}
				catch( IOException e )
				{
					e.printStackTrace();
					result.exception = e;
				}
			}
			else
			{
				result.exception = new Exception( "getInputStream return null " );
			}
			return result;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			result.exception = e;
		}
		// 使用finally块来关闭输入流
		finally
		{
			try
			{
				if( inputStream != null )
				{
					inputStream.close();
				}
			}
			catch( IOException ex )
			{
				result.exception = ex;
				ex.printStackTrace();
			}
		}
		return result;
	}
	
	public static byte[] wrapContentBody(
			String content )
	{
		byte[] byteContent = content.getBytes();
		boolean compress = false;
		Translate( byteContent , decodekey );
		if( byteContent.length > COMPRESS_MIN_SIZE )
		{
			try
			{
				byteContent = Compress.byteCompress( byteContent );
				compress = true;
			}
			catch( Exception e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		byte[] keys = DEFAULT_KEY.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate( byteContent.length + HEADER_SIZE + keys.length );
		// 标识 3
		buffer.put( (byte)'C' );
		buffer.put( (byte)'O' );
		buffer.put( (byte)'E' );
		// 包头长度 2
		buffer.putShort( HEADER_SIZE );
		// 协议版本号 1
		buffer.put( (byte)1 );
		// 压缩协议 1
		if( compress )
		{
			buffer.put( (byte)1 );
		}
		else
		{
			buffer.put( (byte)0 );
		}
		// 加密标识 1
		buffer.put( (byte)1 );
		// 序号 4
		buffer.putInt( sequence++ );
		int md5Index = buffer.position();
		// 初始MD5
		for( int i = 0 ; i < 16 ; i++ )
		{
			buffer.put( (byte)0 );// 覆盖
		}
		// 内容长度
		buffer.putInt( byteContent.length );
		// 内容
		buffer.put( byteContent );
		buffer.put( keys );
		String md5_res = MD5.getMD5EncruptKey( buffer.array() );
		byte[] md5 = MD5.String2Byte( md5_res );
		for( int i = 0 ; i < md5.length ; i++ )
		{
			buffer.put( md5Index + i , md5[i] );
		}
		byte[] result = new byte[byteContent.length + HEADER_SIZE];
		for( int i = 0 ; i < result.length ; i++ )
		{
			result[i] = buffer.get( i );
		}
		return result;
	}
	
	public static ResultEntity postEntity(
			String url ,
			String content )
	{
		ResultEntity result = new ResultEntity();
		HttpResponse response = null;
		try
		{
			byte[] byteContent = wrapContentBody( content );
			ByteArrayEntity entity = new ByteArrayEntity( byteContent );
			HttpPost request = new HttpPost( url );
			request.setEntity( entity );
			HttpClient client = getHttpClient();
			response = client.execute( request );
			if( response != null )
			{
				result.httpCode = response.getStatusLine().getStatusCode();
				HttpEntity resEntity = response.getEntity();
				result.content = ( resEntity == null ) ? null : EntityUtils.toString( resEntity , CHARSET );
				result.contentLength = ( resEntity == null ) ? 0 : resEntity.getContentLength();
				result.exception = null;
			}
		}
		catch( Exception e )
		{
			Log.v( "http" , "UnsupportedEncodingException...." + e.toString() );
			e.printStackTrace();
			if( response != null )
			{
				result.httpCode = response.getStatusLine().getStatusCode();
			}
			result.exception = e;
		}
		return result;
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
				// Log.v("http", response.getStatusLine().getProtocolVersion() +
				// " "
				// + response.getStatusLine().getStatusCode() + " " +
				// response.getStatusLine().getReasonPhrase());
				// for (int i = 0; i < response.getAllHeaders().length; i++) {
				// Log.v("http", response.getAllHeaders()[i].getName() + ":" +
				// response.getAllHeaders()[i].getValue());
				// }
				// Log.v("http", "");
				// Log.v("http", EntityUtils.toString(resEntity, CHARSET));
				return ( resEntity == null ) ? null : EntityUtils.toString( resEntity , CHARSET );
			}
			else
			{
				// Log.v("http", response.getStatusLine().getProtocolVersion() +
				// " "
				// + response.getStatusLine().getStatusCode() + " " +
				// response.getStatusLine().getReasonPhrase());
				// for (int i = 0; i < response.getAllHeaders().length; i++) {
				// Log.v("http", response.getAllHeaders()[i].getName() + ":" +
				// response.getAllHeaders()[i].getValue());
				// }
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
		Log.i( "statistics" , "post res = " + resultString );
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
				// Log.i("new count res", res_obj.toString());
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
		// Log.v("http", "url="+url);
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
				// Log.v("http", response.getStatusLine().getProtocolVersion() +
				// " "
				// + response.getStatusLine().getStatusCode() + " " +
				// response.getStatusLine().getReasonPhrase());
				// for (int i = 0; i < response.getAllHeaders().length; i++) {
				// Log.v("http", response.getAllHeaders()[i].getName() + ":" +
				// response.getAllHeaders()[i].getValue());
				// }
				// Log.v("http", "");
				// Log.v("http", strResult);
				return strResult;
			}
			else
			{
				HttpEntity resEntity = response.getEntity();
				// String strResult = (resEntity == null) ? null :
				// EntityUtils.toString(resEntity, CHARSET);
				// Log.v("http", response.getStatusLine().getProtocolVersion() +
				// " "
				// + response.getStatusLine().getStatusCode() + " " +
				// response.getStatusLine().getReasonPhrase());
				// for (int i = 0; i < response.getAllHeaders().length; i++) {
				// Log.v("http", response.getAllHeaders()[i].getName() + ":" +
				// response.getAllHeaders()[i].getValue());
				// }
				// Log.v("http", "");
				// Log.v("http", strResult);
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
		// Log.v("http", "url="+url);
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
