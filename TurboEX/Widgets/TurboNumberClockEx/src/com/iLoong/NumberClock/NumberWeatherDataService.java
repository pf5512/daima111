package com.iLoong.NumberClock;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import com.iLoong.NumberClock.NumberWeatherwebservice.FLAG_UPDATE;
import com.iLoong.NumberClock.common.CityResult;
import com.iLoong.NumberClock.common.Parameter;

public class NumberWeatherDataService extends IntentService {

	public NumberWeatherDataService(String name) {
		super("WeatherDataService");
	}

	public NumberWeatherDataService() {
		super("WeatherDataService");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_REDELIVER_INTENT;
	}

	// public void mySendBroadcast() {
	// Intent intent = new Intent();
	// intent.setAction(Parameter.UPDATE_RESULT);
	// if (NumberWeatherwebservice.Update_Result_Flag ==
	// FLAG_UPDATE.UPDATE_SUCCES) {
	// intent.putExtra(Parameter.extraName, "UPDATE_SUCCESED");
	// } else {
	// intent.putExtra(Parameter.extraName, "UPDATE_FAILED");
	// }
	// sendBroadcast(intent);
	// }

	@Override
	protected void onHandleIntent(Intent intent) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		long now = System.currentTimeMillis();
		if (Parameter.enable_google_version) {
			String weatherid = sp.getString(Parameter.currentCityId, null);
			String weathername = sp.getString(Parameter.currentCityName, null);
			String weathercountry = sp
					.getString(Parameter.currentCountry, null);
			CityResult cr = new CityResult();
			cr.setWoeid(weatherid);
			cr.setCityName(weathername);
			cr.setCountry(weathercountry);
			if (weatherid != null && weathername != null
					&& weathercountry != null) {
				NumberWeatherwebservice.updateWeatherData(this, cr,
						sp.getString(Parameter.currentunit, "f"));
			} else {
				Log.d("mytag", "数字时钟国外还没有设置城市");
				NumberWeatherwebservice.Update_Result_Flag = FLAG_UPDATE.WEBSERVICE_ERROR;
			}
		} else {
			String updatename = sp.getString(Parameter.currentCityName, null);
			if (updatename != null) {
				NumberWeatherwebservice.updateInLandWeatherData(this,
						updatename);
			} else {
				Log.d("mytag", "数字时钟国内还没有设置城市");
				NumberWeatherwebservice.Update_Result_Flag = FLAG_UPDATE.WEBSERVICE_ERROR;
			}
		}
		// mySendBroadcast();
		Time time = new Time();
		// long interval = 60 * 1000 * 2;
		long interval = 60 * 1000 * 60 * 6;
		if (NumberWeatherwebservice.Update_Result_Flag != FLAG_UPDATE.UPDATE_SUCCES) {
			// interval = 60 * 1000 * 1;
			interval = 60 * 1000 * 30;
		}
		time.set(now + interval);
		long nextUpdate = time.toMillis(true);
		Intent updateIntent = new Intent(Parameter.ACTION_UPDATE_ALL);
		updateIntent.setClass(this, NumberWeatherDataService.class);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0,
				updateIntent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, nextUpdate, pendingIntent);
		stopSelf();
		// }
	}
}
