package com.iLoong.launcher.macinfo;


import org.json.JSONException;
import org.json.JSONObject;

import com.iLoong.launcher.core.Assets;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


// import com.cooee.store.GlobalData;
// import com.cooee.store.packageInfoManager.PackageInfoEx;
// import com.cooee.store.packageInfoManager.PackageInfoManager;
// import com.cooee.store.service.SMSHandler;
// import com.cooee.store.service.SMSReceiver;
// import com.cooee.store.uuid.Installation;
public class MacInfo
{
	
	private static Context mContext;
	
	public static void initMacInfo(
			Context context )
	{
		mContext = context;
		OsInfo.initOsInfo( context );
		TelephonyInfo.initTelephonyInfo( mContext );
		WifiInfoEx.initWifi( mContext );
		NetInfo.initNetInfo( mContext );
		PackageInfoManager.initPackageInfoManager( mContext );
	}
	
	public static JSONObject getInfo()
	{
		JSONObject jObject = new JSONObject();
		JSONObject mac = new JSONObject();
		try
		{
			JSONObjectUitl.put( jObject , "wifi" , WifiInfoEx.getInfo() );
			JSONObjectUitl.put( jObject , "os" , OsInfo.getInfo() );
			JSONObjectUitl.put( jObject , "telephony" , TelephonyInfo.getInfo() );
			JSONObjectUitl.put( jObject , "network" , NetInfo.getInfo() );
			JSONObjectUitl.put( jObject , "generic" , getGeneric() );
			mac.put( "mac" , jObject );
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mac;
	}
	
	public static JSONObject getGeneric()
	{
		JSONObject jObject = new JSONObject();
		PackageInfoEx pinfo = PackageInfoManager.getPackageInfoEx( mContext.getPackageName() );
		try
		{
			JSONObjectUitl.put( jObject , "model" , OsInfo.getModel() );
			if( pinfo != null )
			{
				JSONObjectUitl.put( jObject , "version_code" , pinfo.getVersionCode() );
				JSONObjectUitl.put( jObject , "version_name" , pinfo.getVersionName() );
				JSONObjectUitl.put( jObject , "uuid" , Installation.id( mContext ) );
				JSONObjectUitl.put( jObject , "smsc_address_iccid" , SMSHandler.loadCenterAddress( mContext ) );
				JSONObject tmp = Assets.config;
				if( jObject != null )
				{
					JSONObject config = tmp.getJSONObject( "config" );
					JSONObjectUitl.put( jObject , "web_domain" , config.get( "domain" ) );
					JSONObjectUitl.put( jObject , "app_id" , config.get( "app_id" ) );
					JSONObjectUitl.put( jObject , "template_id" , config.get( "template_id" ) );
					JSONObjectUitl.put( jObject , "channel_id" , config.get( "channel_id" ) );
				}
				JSONObjectUitl.put( jObject , "bind_install_assist" , 0 );
				//JSONObjectUitl.put(jObject, "package_name",	GlobalData.packageName);
				// ���˫��ڻ��ǵ����
				// if (isActivityExist("com.cooee.store",
				// "com.cooee.store.AssistActivity"))
				// {
				// JSONObjectUitl.put(jObject, "bind_install_assist", 1);
				// }
				// else
				// {
				// JSONObjectUitl.put(jObject, "bind_install_assist", 0);
				// }
			}
			return jObject;
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	// Add by XFK 2012.07.02
	private static boolean isActivityExist(
			String strPackageName ,
			String strActivityName )
	{
		Intent intent = new Intent();
		//intent.setClassName("����", "����.Activity��");
		intent.setClassName( strPackageName , strActivityName );
		if( mContext.getPackageManager().resolveActivity( intent , 0 ) == null )
		{
			//˵��ϵͳ�в��������activity  
			return false;
		}
		else
		{
			//�е�ActivityҲ��ȷִ������
			return true;
		}
	}
	
	public static String getWebDomain()
	{
		JSONObject jObject = Assets.config;
		if( jObject != null )
		{
			JSONObject config = null;
			try
			{
				config = jObject.getJSONObject( "config" );
			}
			catch( JSONException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try
			{
				return (String)config.get( "web_domain" );
			}
			catch( JSONException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String getUUID()
	{
		return Installation.id( mContext );
	}
	
	public static boolean isNetworkEnable()
	{
		ConnectivityManager cwjManager = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo nInfo = cwjManager.getActiveNetworkInfo();
		if( nInfo == null )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
}
