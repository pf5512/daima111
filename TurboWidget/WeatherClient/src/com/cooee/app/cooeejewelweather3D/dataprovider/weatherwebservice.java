
package com.cooee.app.cooeejewelweather3D.dataprovider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.cooee.app.cooeejewelweather3D.dataentity.weatherforecastentity;
import com.cooee.app.cooeejewelweather3D.filehelp.Log;
import com.cooee.weather.com.weatherdataentity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class weatherwebservice {
    private static final String TAG = "com.cooee.app.cooeeweather.dataprovider.WebServiceHelper";
    public static FLAG_UPDATE Update_Result_Flag = FLAG_UPDATE.INVILIDE_VALUE;
    public static enum WeatherDataSource {
        GOOGLE,
        SINA,
        WEATHER_CN,
        COOEE
    };

    //数据源来自COOEE
    public static final WeatherDataSource dataSourceFlag = WeatherDataSource.COOEE;

    public static enum FLAG_UPDATE {
        UPDATE_SUCCES,
        WEBSERVICE_ERROR,
        DATAPROVIDER_ERROR,
        AVAILABLE_DATA, 
        INVILIDE_VALUE
    }

    public static void updateWeatherData(Context context, Uri uri) {
        Uri forecastUri = Uri.withAppendedPath(uri, "detail");
        String postalCode = uri.getPathSegments().get(1);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = null;
        String selection = null;

        Log.v(TAG, "updateWeatherData context = " + context + "widgetUri = "
                + uri);
        weatherdataentity DataEntity = null;
        Log.v(TAG, "dataSourceFlag = " + dataSourceFlag);

        DataEntity =  cooeeServiceQuery.CooeeWeatherDataUpdate(postalCode);

        if (Update_Result_Flag == FLAG_UPDATE.UPDATE_SUCCES) { // ���³ɹ����޸����
            ContentValues values = new ContentValues();
            values.clear();
            values.put(weatherdataentity.UPDATE_MILIS, DataEntity.getUpdateMilis());
            values.put(weatherdataentity.CITY, DataEntity.getCity());
            values.put(weatherdataentity.POSTALCODE, DataEntity.getPostalCode());
            values.put(weatherdataentity.FORECASTDATE, DataEntity.getForecastDate());
            values.put(weatherdataentity.CONDITION, DataEntity.getCondition());
            values.put(weatherdataentity.HUMIDITY, DataEntity.getHumidity());
            values.put(weatherdataentity.TEMPF, DataEntity.getTempF());
            values.put(weatherdataentity.TEMPC, DataEntity.getTempC());
            values.put(weatherdataentity.ICON, DataEntity.getIcon());
            values.put(weatherdataentity.WINDCONDITION,
                    DataEntity.getWindCondition());
            values.put(weatherdataentity.LAST_UPDATE_TIME,
                    System.currentTimeMillis());
            values.put(weatherdataentity.TEMPH, DataEntity.getDetails().get(0).getHight());
            values.put(weatherdataentity.TEMPL, DataEntity.getDetails().get(0).getLow());

            selection = weatherdataentity.POSTALCODE + "=" + "'" + postalCode + "'";
            cursor = resolver.query(uri, null, selection, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    resolver.delete(uri, selection, null);
                }
            }
            cursor.close();

            // 保存数据
            resolver.insert(uri, values);

            selection = weatherforecastentity.CITY + "=" + "'" + postalCode + "'";
            Log.v(TAG, "delete details, uri = " + forecastUri + ", selection " + selection);
            cursor = resolver.query(forecastUri, null, selection, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    resolver.delete(forecastUri, selection, null);
                }
            }
            cursor.close();

            for (weatherforecastentity forecast : DataEntity.getDetails()) {
                values.clear();
                values.put(weatherforecastentity.CITY, postalCode);
                values.put(weatherforecastentity.DAYOFWEEK, forecast.getDayOfWeek());
                values.put(weatherforecastentity.HIGHT, forecast.getHight());
                values.put(weatherforecastentity.LOW, forecast.getLow());
                values.put(weatherforecastentity.ICON, forecast.getIcon());
                values.put(weatherforecastentity.CONDITION, forecast.getCondition());
                resolver.insert(forecastUri, values);
            }
        }
    }



    public static Date convertStr2Date(String str) {
        Date d = null;
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            d = f.parse(str);
        } catch (ParseException e) {
            Log.d(TAG, "date format exception");
        }
        return d;
    }
}
