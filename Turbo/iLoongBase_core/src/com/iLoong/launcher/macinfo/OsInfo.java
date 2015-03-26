package com.iLoong.launcher.macinfo;


import java.lang.reflect.Method;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


public class OsInfo
{
	
	public static int widthPixels; // ��Ļ��
	public static int heightPixels;// ��Ļ��
	public static float density;// ��Ļ�ܶ�
	public static String DEVICE;
	public String ID;
	public static String DISPLAY; // �汾��
	public static String PRODUCT;
	public static String BOARD;
	public static String BRAND;
	public static String MODEL; // �豸�ͺ�
	public static String RELEASE; // androidϵͳ�汾
	public static String SDK; // sdk�汾��Ϣ
	public static String RESOLUTION; // �ֱ��� ����480*800
	
	public static void initOsInfo(
			Context context )
	{
		WindowManager wm = (WindowManager)context.getSystemService( "window" );
		// ���巵�ض�����WindowMangerIml��
		Display display = wm.getDefaultDisplay();
		DisplayMetrics displaysMetrics = new DisplayMetrics();
		display.getMetrics( displaysMetrics ); // �����
		widthPixels = displaysMetrics.widthPixels;
		heightPixels = displaysMetrics.heightPixels;
		density = displaysMetrics.density;
		RESOLUTION = widthPixels + "*" + heightPixels;
		DEVICE = Build.DEVICE;
		DISPLAY = Build.DISPLAY;
		PRODUCT = Build.PRODUCT;
		BOARD = Build.BOARD;
		BRAND = Build.BRAND;
		MODEL = Build.MODEL;
		RELEASE = Build.VERSION.RELEASE;
		SDK = Build.VERSION.SDK;
	}
	
	public static JSONObject getInfo()
	{
		JSONObject jObject = new JSONObject();
		try
		{
			JSONObjectUitl.put( jObject , "width_pixels" , widthPixels );
			JSONObjectUitl.put( jObject , "height_pixels" , heightPixels );
			JSONObjectUitl.put( jObject , "density" , density );
			JSONObjectUitl.put( jObject , "device" , DEVICE );
			JSONObjectUitl.put( jObject , "resolution" , RESOLUTION );
			JSONObjectUitl.put( jObject , "device" , DISPLAY );
			JSONObjectUitl.put( jObject , "product" , PRODUCT );
			JSONObjectUitl.put( jObject , "board" , BOARD );
			JSONObjectUitl.put( jObject , "brand" , BRAND );
			JSONObjectUitl.put( jObject , "model" , MODEL );
			JSONObjectUitl.put( jObject , "release_version" , RELEASE );
			JSONObjectUitl.put( jObject , "sdk_version" , SDK );
			try
			{
				Class cl = Class.forName( "android.os.SystemProperties" );
				Object invoker = cl.newInstance();
				Method m = cl.getMethod( "get" , new Class[]{ String.class , String.class } );
				Object result = m.invoke( invoker , new Object[]{ "gsm.version.baseband" , "no message" } );
				System.out.println( ">>>>>>><<<<<<<" + (String)result );
				JSONObjectUitl.put( jObject , "base_board_version" , (String)result );
			}
			catch( Exception e )
			{
			}
			try
			{
				Class cl1 = Class.forName( "android.os.SystemProperties" );
				Object invoker = cl1.newInstance();
				Method m = cl1.getMethod( "get" , new Class[]{ String.class , String.class } );
				Object result1 = m.invoke( invoker , new Object[]{ "http.agent" , "no message" } );
				System.out.println( ">>>>>>><<<<<<<" + (String)result1 );
				String ss = System.getProperties().getProperty( "http.agent" );
				System.out.println( ">>>>>>><<<<<<<" + ss );
			}
			catch( Exception e )
			{
			}
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jObject;
	}
	
	public static String getModel()
	{
		return BRAND + "|" + MODEL;
	}
}
