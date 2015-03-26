package com.iLoong.launcher.macinfo;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.json.JSONException;
import org.json.JSONObject;

import com.iLoong.launcher.Desktop3D.Log;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


public class WifiInfoEx
{
	
	private static Context mContext;
	
	public static void initWifi(
			Context context )
	{
		mContext = context;
	}
	
	public static boolean wifiIsEnable()
	{
		WifiManager wifi = (WifiManager)mContext.getSystemService( Context.WIFI_SERVICE );
		if( wifi.isWifiEnabled() )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static int getWifiState()
	{
		WifiManager wifi = (WifiManager)mContext.getSystemService( Context.WIFI_SERVICE );
		return wifi.getWifiState();
	}
	
	public static JSONObject getInfo()
	{
		JSONObject jObject = new JSONObject();
		WifiManager wifi = (WifiManager)mContext.getSystemService( Context.WIFI_SERVICE );
		WifiInfo info = wifi.getConnectionInfo();
		DhcpInfo dInfo = wifi.getDhcpInfo();
		try
		{
			JSONObjectUitl.put( jObject , "wifi_enabled" , wifi.isWifiEnabled() );
			JSONObjectUitl.put( jObject , "wifi_state" , wifi.getWifiState() );
			JSONObjectUitl.put( jObject , "mac_address" , info.getMacAddress() );
			JSONObjectUitl.put( jObject , "ip_address" , intToIp( info.getIpAddress() ) );
			JSONObjectUitl.put( jObject , "ssid" , info.getSSID() );
			JSONObjectUitl.put( jObject , "network_id" , info.getNetworkId() );
			JSONObjectUitl.put( jObject , "link_speed" , info.getLinkSpeed() );
			JSONObjectUitl.put( jObject , "dns1" , intToIp( dInfo.dns1 ) );
			JSONObjectUitl.put( jObject , "dns2" , intToIp( dInfo.dns2 ) );
			JSONObjectUitl.put( jObject , "gateway" , intToIp( dInfo.gateway ) );
			JSONObjectUitl.put( jObject , "netmask" , intToIp( dInfo.netmask ) );
			JSONObjectUitl.put( jObject , "server_address" , intToIp( dInfo.serverAddress ) );
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jObject;
	}
	
	public static String getLocalIpAddress()
	{
		try
		{
			for( Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces() ; en.hasMoreElements() ; )
			{
				NetworkInterface intf = en.nextElement();
				for( Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses() ; enumIpAddr.hasMoreElements() ; )
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if( !inetAddress.isLoopbackAddress() )
					{
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		}
		catch( SocketException ex )
		{
			Log.e( "WifiPreference IpAddress" , ex.toString() );
		}
		return null;
	}
	
	private static String intToIp(
			int ip )
	{
		return ( ip & 0xFF ) + "." + ( ( ip >> 8 ) & 0xFF ) + "." + ( ( ip >> 16 ) & 0xFF ) + "." + ( ( ip >> 24 ) & 0xFF );
	}
}
