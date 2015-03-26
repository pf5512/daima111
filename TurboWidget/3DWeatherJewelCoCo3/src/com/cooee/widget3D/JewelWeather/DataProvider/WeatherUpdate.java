package com.cooee.widget3D.JewelWeather.DataProvider;

import android.content.Context;
import android.content.Intent;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooee.weather.com.weatherdataentity;
import com.cooee.widget3D.JewelWeather.iLoongWeather;
import com.cooee.widget3D.JewelWeather.View.WidgetWeather;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class WeatherUpdate {
	private static final String TAG = "com.iLoong.Weather.DataProvider.WeatherUpdate";

	private static WidgetWeather mWidgetWeather;
	private static MainAppContext mAppContext;
	private final static String UPDATA_TO_CONSOLVEWEATHER_ChANGECITY = "com.cooee.Consolve_Weather_ChangeCity";
	//weijie 20130122
	public static synchronized void updateViews(Context context, int widgetId) {
//	public static void updateViews(Context context, int widgetId) {
		
	//	Log.v("aq", "updateViews widgetId = " + widgetId);
		mWidgetWeather = (WidgetWeather) iLoongWeather.mWidgetWeather;
		mAppContext = mWidgetWeather.mAppContext;
		Log.v("ConsolveWeather", "updateViews");
		// 读取postalCode
		String mpostalCode = WeatherData.getPostalCode(context, widgetId);
		Log.v("weijie", "updateViews mpostalCode = " + mpostalCode);
	//	Log.v("aq", "updateViews widgetId = " + widgetId);
		//weijie 20130122
		if(mpostalCode == null || mpostalCode.equals(""))
			return;
		final Context mcontext = context;
		// 读取dataEntity
		WidgetWeather.mDataEntity = null;
		WidgetWeather.mDataEntity = WeatherData.readFullData(context, mpostalCode);
		WidgetWeather.mpostcode = null;
		WidgetWeather.mpostcode = WeatherData.getPostalCode(context, widgetId);
		Log.v("weijie", "updateViews WidgetWeathermDataEntity = " + WidgetWeather.mDataEntity);
		Log.v("weijie", "updateViews WidgetWeathermpostalCode = " + WidgetWeather.mpostcode);
		// 对纹理的更改等必须放在gl的线程中，所以我们post过去
		mAppContext.mGdxApplication.postRunnable(new Runnable() {
			@Override
			public void run() {
				// 更新condition
				if (mWidgetWeather != null) {
					mWidgetWeather.UpdataDateEntity();
				}
			}});
		
		if (mWidgetWeather != null) {
			SendBroadcastToLauncherByClosed(mcontext,WidgetWeather.mDataEntity);
		}

	}

	
	public static void SendBroadcastToLauncherByClosed(Context context,weatherdataentity mDataEntity) 
    {
    	try
    	{
            Intent intent = new Intent();
            intent.setAction(UPDATA_TO_CONSOLVEWEATHER_ChANGECITY);
            
 //           Log.v("ConsolveWeather", "SendBroadcastToLauncherByClosed mDataEntity="+mDataEntity);
            if(mDataEntity != null)
            {
            	intent.putExtra("postalCode",mDataEntity.getPostalCode());
        /*    	
            	Log.v("ConsolveWeather", " T0_tempc_now ="+mDataEntity.getTempC());
            	Log.v("ConsolveWeather", " T0_tempc_high ="+mDataEntity.getDetails().get(0).getHight());
            	Log.v("ConsolveWeather", " T0_tempc_low ="+mDataEntity.getDetails().get(0).getLow());
            	Log.v("ConsolveWeather", " T0_condition ="+mDataEntity.getCondition());
            	Log.v("ConsolveWeather", " T0_updatemilis ="+mDataEntity.getUpdateMilis());
            	Log.v("ConsolveWeather", " T0_city ="+mDataEntity.getCity());
            	Log.v("ConsolveWeather", " T0_forecastdate ="+mDataEntity.getForecastDate());
            	Log.v("ConsolveWeather", " T0_humidity ="+mDataEntity.getHumidity());
            	Log.v("ConsolveWeather", " T0_tempf ="+mDataEntity.getTempF());
            	Log.v("ConsolveWeather", " T0_tempc ="+mDataEntity.getTempC());
            	Log.v("ConsolveWeather", " T0_temph ="+mDataEntity.getTempH());
            	Log.v("ConsolveWeather", " T0_templ ="+mDataEntity.getTempL());
            	Log.v("ConsolveWeather", " T0_icon ="+mDataEntity.getIcon());
            	Log.v("ConsolveWeather", " T0_windcondition ="+mDataEntity.getWindCondition());
            	Log.v("ConsolveWeather", " T0_lastupdatetime ="+mDataEntity.getLastUpdateTime());         	         	
            	Log.v("ConsolveWeather", " T0_dayofweek ="+mDataEntity.getDetails().get(0).getDayOfWeek());  
            	
            	Log.v("ConsolveWeather", " T1_city ="+mDataEntity.getCity());
            	Log.v("ConsolveWeather", " T1_dayofweek ="+mDataEntity.getDetails().get(1).getDayOfWeek());
            	Log.v("ConsolveWeather", " T1_icon ="+mDataEntity.getDetails().get(1).getIcon());
            	Log.v("ConsolveWeather", " T1_tempc_high ="+mDataEntity.getDetails().get(1).getHight());
            	Log.v("ConsolveWeather", " T1_tempc_low ="+mDataEntity.getDetails().get(1).getLow());
            	Log.v("ConsolveWeather", " T1_condition ="+mDataEntity.getDetails().get(1).getCondition());

            	Log.v("ConsolveWeather", " T2_city ="+mDataEntity.getCity());
            	Log.v("ConsolveWeather", " T2_dayofweek ="+mDataEntity.getDetails().get(2).getDayOfWeek());
            	Log.v("ConsolveWeather", " T2_icon ="+mDataEntity.getDetails().get(2).getIcon());
            	Log.v("ConsolveWeather", " T2_tempc_high ="+mDataEntity.getDetails().get(2).getHight());
            	Log.v("ConsolveWeather", " T2_tempc_low ="+mDataEntity.getDetails().get(2).getLow());
            	Log.v("ConsolveWeather", " T2_condition ="+mDataEntity.getDetails().get(2).getCondition());

            	Log.v("ConsolveWeather", "T3_city ="+mDataEntity.getCity());
            	Log.v("ConsolveWeather", " T3_dayofweek ="+mDataEntity.getDetails().get(3).getDayOfWeek());
            	Log.v("ConsolveWeather", " T3_icon ="+mDataEntity.getDetails().get(3).getIcon());
            	Log.v("ConsolveWeather", " T3_tempc_high ="+mDataEntity.getDetails().get(3).getHight());
            	Log.v("ConsolveWeather", " T3_tempc_low ="+mDataEntity.getDetails().get(3).getLow());
            	Log.v("ConsolveWeather", " T3_condition ="+mDataEntity.getDetails().get(3).getCondition());
            	
            	Log.v("ConsolveWeather", " result OK");
            	
           */
            	intent.putExtra("T0_tempc_now",mDataEntity.getTempC());
            	intent.putExtra("T0_tempc_high",mDataEntity.getDetails().get(0).getHight());
            	intent.putExtra("T0_tempc_low",mDataEntity.getDetails().get(0).getLow());
            	intent.putExtra("T0_condition",mDataEntity.getCondition());
            	intent.putExtra("T0_updatemilis",mDataEntity.getUpdateMilis());
            	intent.putExtra("T0_city",mDataEntity.getCity());
            	intent.putExtra("T0_forecastdate",mDataEntity.getForecastDate());
            	intent.putExtra("T0_humidity",mDataEntity.getHumidity());
            	intent.putExtra("T0_tempf",mDataEntity.getTempF());
            	intent.putExtra("T0_tempc",mDataEntity.getTempC());
            	intent.putExtra("T0_temph",mDataEntity.getTempH());
            	intent.putExtra("T0_templ",mDataEntity.getTempL());
            	intent.putExtra("T0_icon",mDataEntity.getIcon());
            	intent.putExtra("T0_windcondition",mDataEntity.getWindCondition());
            	intent.putExtra("T0_lastupdatetime",mDataEntity.getLastUpdateTime());         	         	
            	intent.putExtra("T0_dayofweek",mDataEntity.getDetails().get(0).getDayOfWeek());  
            	
                intent.putExtra("T1_city",mDataEntity.getCity());
                intent.putExtra("T1_dayofweek",mDataEntity.getDetails().get(1).getDayOfWeek());
                intent.putExtra("T1_icon",mDataEntity.getDetails().get(1).getIcon());
            	intent.putExtra("T1_tempc_high",mDataEntity.getDetails().get(1).getHight());
            	intent.putExtra("T1_tempc_low",mDataEntity.getDetails().get(1).getLow());
            	intent.putExtra("T1_condition",mDataEntity.getDetails().get(1).getCondition());

                intent.putExtra("T2_city",mDataEntity.getCity());
                intent.putExtra("T2_dayofweek",mDataEntity.getDetails().get(2).getDayOfWeek());
                intent.putExtra("T2_icon",mDataEntity.getDetails().get(2).getIcon());
            	intent.putExtra("T2_tempc_high",mDataEntity.getDetails().get(2).getHight());
            	intent.putExtra("T2_tempc_low",mDataEntity.getDetails().get(2).getLow());
            	intent.putExtra("T2_condition",mDataEntity.getDetails().get(2).getCondition());

                intent.putExtra("T3_city",mDataEntity.getCity());
                intent.putExtra("T3_dayofweek",mDataEntity.getDetails().get(3).getDayOfWeek());
                intent.putExtra("T3_icon",mDataEntity.getDetails().get(3).getIcon());
            	intent.putExtra("T3_tempc_high",mDataEntity.getDetails().get(3).getHight());
            	intent.putExtra("T3_tempc_low",mDataEntity.getDetails().get(3).getLow());
            	intent.putExtra("T3_condition",mDataEntity.getDetails().get(3).getCondition());
            	
            	intent.putExtra("result","OK");

   //         	Log.e("T0_condition", "getDetails condition = "+mDataEntity.getDetails().get(0).getCondition());
   //         	Log.e("T0_condition", "condition = "+mDataEntity.getCondition());
            }
            else
            {
            	intent.putExtra("T0_tempc_now",0);
            	intent.putExtra("T0_tempc_high",0);
            	intent.putExtra("T0_tempc_low",0);
            	intent.putExtra("T0_condition","none");
            	
            	intent.putExtra("T1_tempc_high",0);
            	intent.putExtra("T1_tempc_low",0);
            	intent.putExtra("T1_condition","none");

            	intent.putExtra("T2_tempc_high",0);
            	intent.putExtra("T2_tempc_low",0);
            	intent.putExtra("T2_condition","none");

            	intent.putExtra("T3_tempc_high",0);
            	intent.putExtra("T3_tempc_low",0);
            	intent.putExtra("T3_condition","none");
            	
            	intent.putExtra("result","ERROR");
            } 
            Log.e("ConsolveWeather", "SendBroadcastToLauncherByClosed sendBroadcast");
            Log.e("ConsolveWeather", "act = "+UPDATA_TO_CONSOLVEWEATHER_ChANGECITY);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.sendBroadcast(intent);
    	}
    	catch(Exception ex)
        {
        	 Log.d(TAG, "SendBroadcastToLauncherByClosed exception");
        }
	}
	
}