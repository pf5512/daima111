package com.iLoong.launcher.core;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5
{
	
	private static char[] HEXCHAR = { '0' , '1' , '2' , '3' , '4' , '5' , '6' , '7' , '8' , '9' , 'a' , 'b' , 'c' , 'd' , 'e' , 'f' };
	
	/**
	 * byte[]转换成字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String toHexString(
			byte[] b )
	{
		StringBuilder sb = new StringBuilder( b.length * 2 );
		for( int i = 0 ; i < b.length ; i++ )
		{
			sb.append( HEXCHAR[( b[i] & 0xf0 ) >>> 4] );
			sb.append( HEXCHAR[b[i] & 0x0f] );
		}
		return sb.toString();
	}
	
	public static String byte2HexString(
			byte[] b )
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0 ; i < b.length ; i++ )
		{
			String stmp = Integer.toHexString( b[i] & 0xff );
			if( stmp.length() == 1 )
				sb.append( "0" + stmp );
			else
				sb.append( stmp );
		}
		return sb.toString();
	}
	
	public static byte[] String2Byte(
			String hexString )
	{
		if( hexString.length() % 2 == 1 )
			return null;
		byte[] ret = new byte[hexString.length() / 2];
		for( int i = 0 ; i < hexString.length() ; i += 2 )
		{
			ret[i / 2] = Integer.decode( "0x" + hexString.substring( i , i + 2 ) ).byteValue();
		}
		return ret;
	}
	
	public static String getMD5EncruptKey(
			byte[] bytes )
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
		messagedigest.update( bytes );
		res = bufferToHex( messagedigest.digest() );
		// Log.v("http", "getMD5EncruptKey res =  " + res);
		return res;
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
		// Log.v("http", "getMD5EncruptKey res =  " + res);
		return res;
	}
	
	public static String bufferToHex(
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
		char c0 = HEXCHAR[( bt & 0xf0 ) >> 4]; // 取字节中高 4 位的数字转换, >>>
												// 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
		char c1 = HEXCHAR[bt & 0xf]; // 取字节中低 4 位的数字转换
		stringbuffer.append( c0 );
		stringbuffer.append( c1 );
	}
}
