package com.cooee.searchbar;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.telephony.TelephonyManager;
import android.util.Log;


public class SearchEngineParams
{
	
	private static final String DEFAULT_KEY = "f24657aafcb842b185c98a9d3d7c6f4725f6cc4597c3a4d531c70631f7c7210fd7afd2f8287814f3dfa662ad82d1b02268104e8ab3b2baee13fab062b3d27bff";
	
	public SearchEngineParams()
	{
	}
	
	public String getP1(
			Context context )
	{
		try
		{
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo( context.getPackageName() , 0 );
			JSONObject config = JsonFile.getConfig( context ).getJSONObject( "config" );
			String appid = config.getString( "app_id" );
			String sn = config.getString( "serialno" );
			TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService( Context.TELEPHONY_SERVICE );
			StringBuilder sb = new StringBuilder();
			sb.append( "h1:" ).append( context.getPackageName() ).append( "|h2:" ).append( pi.versionCode ).append( "|h3:" ).append( pi.versionName ).append( "|h4:" ).append( sn ).append( "|h5:" )
					.append( appid ).append( "|h6:1|h7:" ).append( mTelephonyMgr.getSubscriberId() == null ? "" : mTelephonyMgr.getSubscriberId() ).append( "|h8:" )
					.append( mTelephonyMgr.getSimSerialNumber() == null ? "" : mTelephonyMgr.getSimSerialNumber() ).append( "|h9:" )
					.append( mTelephonyMgr.getLine1Number() == null ? "" : mTelephonyMgr.getLine1Number() ).append( "|h10:turbosearch" );
			String p1Src = sb.toString();
//			Log.d( "ButtonActivity" , "p1src=" + p1Src );
			String p1Enc = desEncrypt( p1Src );
//			Log.d( "ButtonActivity" , "p1Enc=" + p1Enc );
			return p1Enc;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
			return "";
		}
		catch( JSONException e )
		{
			e.printStackTrace();
			return "";
		}
	}
	
	public String getP2Source(
			Context context )
	{
		return "http://m.v9.com/web/";
	}
	
	public String getP3Source(
			Context context ,
			String keyWord )
	{
		try
		{
			return "from=mdd-kuyu&type=navserach&q=" + URLEncoder.encode( keyWord , "UTF8" );
		}
		catch( UnsupportedEncodingException e )
		{
			e.printStackTrace();
			return "";
		}
	}
	
	public String getParams(
			Context context ,
			String keyWord )
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			String p1 = getP1( context );
			String p2Source = getP2Source( context );
			String p3Source = getP3Source( context , keyWord );
			String p2Dest = URLEncoder.encode( p2Source , "UTF8" );
			String p3Dest = URLEncoder.encode( p3Source , "UTF8" );
			sb.append( "Action=2000&p1=" ).append( p1 ).append( "&p2=" ).append( p2Dest ).append( "&p3=" ).append( p3Dest );
			String md5Src = "2000" + p1 + p2Source + p3Source + DEFAULT_KEY;
//			Log.d( "SearchEngineParams" , "md5Src=" + md5Src );
			String md5Result = getMD5EncruptKey( md5Src );
//			Log.d( "SearchEngineParams" , "md5Result=" + md5Result );
			sb.append( "&md5=" ).append( md5Result );
			return sb.toString();
		}
		catch( UnsupportedEncodingException e )
		{
			e.printStackTrace();
			return "";
		}
	}
	
	public String getSearchAddr(
			Context context ,
			String keyWord )
	{
		//http://192.168.1.225/iloong/pui/ServicesEngine/Service
		//http://service.coolauncher.com/iloong/pui/ServicesEngine/Service
		String[] urls = new String[]{
				"http://service.coolauncher.com/iloong/pui/ServicesEngine/Service?" ,
				"http://service1.coolauncher.com/iloong/pui/ServicesEngine/Service?" ,
				"http://service2.coolauncher.com/iloong/pui/ServicesEngine/Service?" ,
				"http://service3.coolauncher.com/iloong/pui/ServicesEngine/Service?" };
		String url = urls[(int)( Math.random() * 3 )];
		return url + getParams( context , keyWord );
	}
	
	private static String getMD5EncruptKey(
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
	
	private static char hexDigits[] = { '0' , '1' , '2' , '3' , '4' , '5' , '6' , '7' , '8' , '9' , 'a' , 'b' , 'c' , 'd' , 'e' , 'f' };
	
	private static String bufferToHex(
			byte bytes[] )
	{
		StringBuffer stringbuffer = new StringBuffer( 2 * bytes.length );
		for( int l = 0 ; l < bytes.length ; l++ )
		{
			byte bt = bytes[l];
			char c0 = hexDigits[( bt & 0xf0 ) >> 4]; // 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同    
			char c1 = hexDigits[bt & 0xf]; // 取字节中低 4 位的数字转换    
			stringbuffer.append( c0 );
			stringbuffer.append( c1 );
		}
		return stringbuffer.toString();
	}
	
	private String desEncrypt(
			String content )
	{
		DESUtil util = new DESUtil( "8ufD05pL" );
		byte[] bytes;
		try
		{
			bytes = util.encryptByte( content.getBytes( "UTF8" ) );
			return bufferToHex( bytes );
		}
		catch( UnsupportedEncodingException e )
		{
			e.printStackTrace();
			return "";
		}
	}
	
	private static class DESUtil
	{
		
		Key key;
		
		public DESUtil(
				String str )
		{
			setKey( str ); // 生成密匙  
		}
		
		private void setKey(
				String strKey )
		{
			SecretKey secretKey = new SecretKeySpec( strKey.getBytes() , "DES" );
			key = secretKey;
		}
		
		public byte[] encryptByte(
				byte[] byteS )
		{
			try
			{
				Cipher cipher = Cipher.getInstance( "DES/ECB/PKCS5Padding" );
				cipher.init( Cipher.ENCRYPT_MODE , key );
				byte[] byteFina = cipher.doFinal( byteS );
				return byteFina;
			}
			catch( NoSuchAlgorithmException e )
			{
				e.printStackTrace();
			}
			catch( NoSuchPaddingException e )
			{
				e.printStackTrace();
			}
			catch( InvalidKeyException e )
			{
				e.printStackTrace();
			}
			catch( IllegalBlockSizeException e )
			{
				e.printStackTrace();
			}
			catch( BadPaddingException e )
			{
				e.printStackTrace();
			}
			return new byte[]{};
		}
	}
	
	private static class JsonFile
	{
		
		public static JSONObject getConfig(
				Context context )
		{
			return getConfig( context , CONFIG_FILE_NAME );
		}
		
		private static final String CONFIG_FILE_NAME = "config.ini";
		
		private static JSONObject getConfig(
				Context context ,
				String fileName )
		{
			AssetManager assetManager = context.getAssets();
			InputStream inputStream = null;
			try
			{
				inputStream = assetManager.open( fileName );
				String config = readTextFile( inputStream );
				JSONObject jObject;
				try
				{
					jObject = new JSONObject( config );
					return jObject;
				}
				catch( JSONException e1 )
				{
					e1.printStackTrace();
				}
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			return null;
		}
		
		private static String readTextFile(
				InputStream inputStream )
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte buf[] = new byte[1024];
			int len;
			try
			{
				while( ( len = inputStream.read( buf ) ) != -1 )
				{
					outputStream.write( buf , 0 , len );
				}
				outputStream.close();
				inputStream.close();
			}
			catch( IOException e )
			{
			}
			return outputStream.toString();
		}
	}
}
