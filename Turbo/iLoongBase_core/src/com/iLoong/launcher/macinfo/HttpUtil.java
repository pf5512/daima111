package com.iLoong.launcher.macinfo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;


public class HttpUtil
{
	
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
			Map<String , List<String>> map = conn.getHeaderFields();
			// 遍历所有的响应头字段
			//            for (String key : map.keySet())
			//            {
			//                System.out.println(key + "--->" + map.get(key));
			//            }
			return conn.getInputStream();
		}
		catch( Exception e )
		{
			//System.out.println("发送GET请求出现异常！" + e);
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
	
	public static InputStream sendPost(
			String url ,
			String params )
	{
		PrintWriter out = null;
		BufferedReader in = null;
		try
		{
			//构造一个URL对象  
			URL httpUrl = new URL( url );
			// 使用HttpURLConnection打开连接  
			HttpURLConnection urlConn = (HttpURLConnection)httpUrl.openConnection();
			//因为这个是post请求,设立需要设置为true  
			urlConn.setDoOutput( true );
			urlConn.setDoInput( true );
			// 设置以POST方式  
			urlConn.setRequestMethod( "POST" );
			// Post 请求不能使用缓存  
			urlConn.setUseCaches( false );
			urlConn.setInstanceFollowRedirects( true );
			// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的  
			urlConn.setRequestProperty( "Content-Type" , "application/x-www-form-urlencoded" );
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter( urlConn.getOutputStream() );
			// 发送请求参数
			out.print( params );
			// flush输出流的缓冲
			out.flush();
			//            // 定义BufferedReade r输入流来读取URL的响应
			//            in = new BufferedReader(
			//                new InputStreamReader(urlConn.getInputStream()));
			//            String line;
			//            while ((line = in.readLine()) != null)
			//            {
			//                result += "\n" + line;
			//            }
			return urlConn.getInputStream();
		}
		catch( Exception e )
		{
			//System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally
		{
			try
			{
				if( out != null )
				{
					out.close();
				}
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
}
