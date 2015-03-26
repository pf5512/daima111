package com.cooee.widget3D.JewelWeather.DataProvider;

import com.cooee.widget3D.JewelWeather.iLoongWeather;
import com.cooee.widget3D.JewelWeather.View.WidgetWeather;
import com.iLoong.launcher.Desktop3D.Log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class WeatherReceiver extends BroadcastReceiver {
	private static String TAG = "com.iLoong.Weather.DataProvider.WeatherReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
	//	Log.v("aq", ">>>onReceive " + intent.getAction());
		
		if (intent.getAction().equals(WeatherData.BROATCAST_URI)) { // 天气数据服务发来的广播
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String result = bundle.getString("cooee.weather.updateResult");
				int widgetId = bundle
						.getInt("cooee.weather.updateResult.userId");
				String postalCode = bundle
						.getString("cooee.weather.updateResult.postalcode");
				Log.v(TAG, "widget receive " + result + ", widgetId = "
						+ widgetId + ", postalCode = " + postalCode);

				// 事实上updateResult中带的extra:userId是无效的，直接根据城市来更新数据就好
				if (postalCode != null) {
					if (postalCode.equals(WeatherData.getPostalCode(context,
							widgetId))) {
						// 更新Weather相关的View
				//		Log.v("aq", "onReceive " + intent.getAction()+",postalCode="+postalCode);
				//		Log.v("aq", "onReceive  cooee.weather.updateResult！！");
						
						WeatherUpdate.updateViews(context, widgetId);
					}
				}
			}
		} else if (intent.getAction().equals(WeatherData.CHANGE_POSTALCODE)) { // 天气发来的广播
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String postalCode = bundle
						.getString("com.cooee.weather.Weather.postalCode");
				int widgetId = bundle.getInt(
						"com.cooee.weather.Weather.userId", 0);
			//	Log.v("aq", "widget receive " + "CHANGE_POSTALCODE"
			//			+ ", widgetId = " + widgetId + ", postalCode = "
			//			+ postalCode);
				if (postalCode != null && widgetId == iLoongWeather.mWidgetWeather.mWidgetId) {
					// 改变城市
					changePostalCode(context, widgetId, postalCode);
				}
			}
		}
	}

	public static void changePostalCode(Context context, int widgetId,
			String postalCode) {
		// 更改widgetId和postalCode的表
		WeatherData.deletePostalCode(context, widgetId);
		WeatherData.addPostalCode(context, postalCode, widgetId);
	//	Log.v("aq", "changePostalCode  postalCode="+postalCode+",widgetId="+widgetId);
		// 更新widget
		WeatherUpdate.updateViews(context, widgetId);
	}
}