package com.iLoong.NumberClock;

import android.content.Context;

import com.iLoong.NumberClock.common.CityResult;
import com.iLoong.NumberClock.common.CooeeClient;
import com.iLoong.NumberClock.common.YahooClient;

public class NumberWeatherwebservice {

	public static FLAG_UPDATE Update_Result_Flag = FLAG_UPDATE.WEBSERVICE_ERROR;

	public static enum FLAG_UPDATE {
		UPDATE_SUCCES, WEBSERVICE_ERROR
	}

	public static void updateWeatherData(final Context context,
			final CityResult cr, final String unit) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				YahooClient.getWeatherInfo(cr, unit, context, 4);
			}
		}).start();
		// Weather weather = YahooClient.getWeatherInfo(cr, unit, context, 2);
		// if (weather != null && weather.getList() != null
		// && weather.getList().size() == 5) {
		// Update_Result_Flag = FLAG_UPDATE.UPDATE_SUCCES;
		// Editor ed = PreferenceManager.getDefaultSharedPreferences(
		// iLoongLauncher.getInstance()).edit();
		// ed.putBoolean("numberweatherstate", true);
		// ed.putString("numberweathercityname", weather.getWeathercity());
		// ed.putString("numberweathercode", weather.getWeathercode());
		// ed.putString("numberweathercondition",
		// weather.getWeathercondition());
		// ed.putString("numberweathercurrenttmp", weather.getCurrtmp());
		// ed.putString("numberweathershidu", weather.getShidu());
		// ed.putString("numberlistweathercode0", weather.getList().get(0)
		// .getWeathercode());
		// ed.putString("numberlistweatherhighTmp0", weather.getList().get(0)
		// .getHightmp());
		// ed.putString("numberlistweatherlowTmp0", weather.getList().get(0)
		// .getLowtmp());
		// ed.putString("numberlistweatherweek0", weather.getList().get(0)
		// .getWeatherweek());
		// ed.putString("numberlistweathercode1", weather.getList().get(1)
		// .getWeathercode());
		// ed.putString("numberlistweatherhighTmp1", weather.getList().get(1)
		// .getHightmp());
		// ed.putString("numberlistweatherlowTmp1", weather.getList().get(1)
		// .getLowtmp());
		// ed.putString("numberlistweatherweek1", weather.getList().get(1)
		// .getWeatherweek());
		// ed.putString("numberlistweathercode2", weather.getList().get(2)
		// .getWeathercode());
		// ed.putString("numberlistweatherhighTmp2", weather.getList().get(2)
		// .getHightmp());
		// ed.putString("numberlistweatherlowTmp2", weather.getList().get(2)
		// .getLowtmp());
		// ed.putString("numberlistweatherweek2", weather.getList().get(2)
		// .getWeatherweek());
		// ed.putString("numberlistweathercode3", weather.getList().get(3)
		// .getWeathercode());
		// ed.putString("numberlistweatherhighTmp3", weather.getList().get(3)
		// .getHightmp());
		// ed.putString("numberlistweatherlowTmp3", weather.getList().get(3)
		// .getLowtmp());
		// ed.putString("numberlistweatherweek3", weather.getList().get(3)
		// .getWeatherweek());
		// ed.putString("numberlistweathercode4", weather.getList().get(4)
		// .getWeathercode());
		// ed.putString("numberlistweatherhighTmp4", weather.getList().get(4)
		// .getHightmp());
		// ed.putString("numberlistweatherlowTmp4", weather.getList().get(4)
		// .getLowtmp());
		// ed.putString("numberlistweatherweek4", weather.getList().get(4)
		// .getWeatherweek());
		// ed.commit();
		// } else {
		// Log.d("mytag", "数字时钟国外没有更新到数据");
		// Update_Result_Flag = FLAG_UPDATE.WEBSERVICE_ERROR;
		// }
	}

	public static void updateInLandWeatherData(final Context context,
			final String city_num) {
		// WeatherEntity inlandweather = CooeeClient.getWeatherInfo(context,
		// city_num, 4);
		// if (inlandweather != null && inlandweather.getDetails() != null
		// && inlandweather.getDetails().size() == 5) {
		// Update_Result_Flag = FLAG_UPDATE.UPDATE_SUCCES;
		// NumberClockHelper.setWeatherInland(
		// PreferenceManager.getDefaultSharedPreferences(context),
		// inlandweather);
		// } else {
		// Log.d("mytag", "数字时钟国内没有更新到数据");
		// Update_Result_Flag = FLAG_UPDATE.WEBSERVICE_ERROR;
		// }
		new Thread(new Runnable() {
			@Override
			public void run() {
				CooeeClient.getWeatherInfo(context, city_num, 4);
			}
		}).start();
	}
}
