package com.cooee.widget3D.JewelWeather.DataProvider;


import com.cooee.weather.com.PostalCodeEntity;
import com.cooee.weather.com.weatherdataentity;
import com.cooee.weather.com.weatherforecastentity;
import com.iLoong.launcher.Desktop3D.Log;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class WeatherData {

	private static final String TAG = "com.iLoong.Weather.DataProvider.WeatherData";

	// 城市列表的uri
	public final static String POSTALCODE_URI = "content://com.cooee.app.cooeejewelweather3D.dataprovider/postalCode";
	// 天气数据的uri
	public final static String WEATHER_URI = "content://com.cooee.app.cooeejewelweather3D.dataprovider/weather";
	// 数据服务的action
	public final static String DATA_SERVICE_ACTION = "com.cooee.weather.dataprovider.weatherDataService";

	// 广播的地址
	public final static String BROATCAST_URI = "com.cooee.weather.data.action.UPDATE_RESULT";
	public final static String CHANGE_POSTALCODE = "com.cooee.weather.Weather.action.CHANGE_POSTALCODE";

	/**
	 * 通过widgetId，来获得所对应的城市
	 * 
	 * @param context
	 * @param widgetId
	 * @return
	 */
	public static String getPostalCode(Context context, int widgetId) {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse(POSTALCODE_URI);
		String selection;
		Cursor cursor = null;
		String postalCode = null;

		selection = PostalCodeEntity.USER_ID + "=" + "'" + widgetId + "'";
		cursor = resolver.query(uri, PostalCodeEntity.projection, selection,
				null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				postalCode = cursor.getString(0);
			}

			cursor.close();
		}

		return postalCode;
	}

	public static void addPostalCode(Context context, String postalCode,
			int widgetId) {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse(POSTALCODE_URI);
		ContentValues values = new ContentValues();

		values.put(PostalCodeEntity.POSTAL_CODE, postalCode);
		values.put(PostalCodeEntity.USER_ID, widgetId);

		resolver.insert(uri, values);
	}

	public static void deletePostalCode(Context context, int widgetId) {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse(POSTALCODE_URI);
		String selection;

		selection = PostalCodeEntity.USER_ID + "=" + "'" + widgetId + "'";
		resolver.delete(uri, selection, null);
	}

	public static weatherdataentity readData(Context context, String postalCode) {
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = null;
		Uri uri;
		String selection;
		weatherdataentity dataEntity = null;

		uri = Uri.parse(WEATHER_URI + "/" + postalCode);
		selection = weatherdataentity.POSTALCODE + "=" + "'" + postalCode + "'";
		cursor = resolver.query(uri, weatherdataentity.projection, selection,
				null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				dataEntity = new weatherdataentity();

				dataEntity.setUpdateMilis(cursor.getInt(0));
				dataEntity.setCity(cursor.getString(1));
				dataEntity.setPostalCode(cursor.getString(2));
				dataEntity.setForecastDate(cursor.getLong(3));
				dataEntity.setCondition(cursor.getString(4));
				dataEntity.setTempF(cursor.getInt(5));
				dataEntity.setTempC(cursor.getInt(6));
				dataEntity.setHumidity(cursor.getString(7));
				dataEntity.setIcon(cursor.getString(8));
				dataEntity.setWindCondition(cursor.getString(9));
				dataEntity.setLastUpdateTime(cursor.getLong(10));
				dataEntity.setIsConfigured(cursor.getInt(11));
				dataEntity.setTempH(cursor.getInt(12));
				dataEntity.setTempL(cursor.getInt(13));
			}

			cursor.close();
		}
		return dataEntity;
	}

	public static weatherdataentity readFullData(Context context,
			String postalCode) {
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = null;
		Uri uri;
		String selection;
		weatherdataentity dataEntity = null;

		dataEntity = readData(context, postalCode);

		int details_count = 0;
		if (dataEntity != null) {
			uri = Uri.parse(WEATHER_URI + "/" + postalCode + "/detail");
			selection = weatherforecastentity.CITY + "=" + "'" + postalCode
					+ "'";
			cursor = resolver.query(uri,
					weatherforecastentity.forecastProjection, selection, null,
					null);
			if (cursor != null) {
				weatherforecastentity forecast;
				while (cursor.moveToNext()) {
					forecast = new weatherforecastentity();
					forecast.setDayOfWeek(cursor.getInt(2));
					forecast.setLow(cursor.getInt(3));
					forecast.setHight(cursor.getInt(4));
					forecast.setIcon(cursor.getString(5));
					forecast.setCondition(cursor.getString(6));
					// forecast.setWidgetId(cursor.getInt(6));

					dataEntity.getDetails().add(forecast);

					details_count = details_count + 1;
				}
				cursor.close();
			}
		}

		Log.v(TAG, "details_count = " + details_count);
		if (details_count < 4) {
			dataEntity = null;
		}

		return dataEntity;
	}
}
