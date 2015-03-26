
package com.cooee.widget.samweatherclock;

import java.text.SimpleDateFormat;

import com.cooee.app.cooeeweather.dataentity.WeatherCondition;
import com.cooee.widget.samweatherclock.R;

public class WeatherConditionImage {
    //private static String TAG = "com.cooee.weather.dataentity.WeatherCondition";

    public static int getConditionImage(String condition) {
        return getConditionImage(condition, false);
    }

    public static int getConditionImage(String condition, boolean need_day) {
        WeatherCondition.Condition c = WeatherCondition
                .convertCondition(condition);
        int image = 0;
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH");
        String date = sDateFormat.format(new java.util.Date());
        boolean day = false;
        // 判断为白天或夜晚
        if (date.compareTo("18") < 0 && date.compareTo("06") > 0) {
            // 6点－18点为白天
            day = true;
        }
        // 如果不需要判断白天或夜晚，则默认为白天
        if (!need_day)
            day = true;

        switch (c) {
            case WEATHER_FINE:
                if (day)
                    image = R.drawable.sunny_day_medium;
                else
                    image = R.drawable.sunny_night_medium;
                break;
            case WEATHER_CLOUDY:
                if (day)
                    image = R.drawable.cloudy_day_medium;
                else
                    image = R.drawable.cloudy_night_medium;
                break;
            case WEATHER_OVERCAST:
                image = R.drawable.overcast_medium;
                break;
            case WEATHER_SNOW:
                image = R.drawable.snow_medium;
                break;
            case WEATHER_THUNDERSTORM:
                image = R.drawable.thunderstorm_medium;
                break;
            case WEATHER_SLEET:
            case WEATHER_STORM:
            case WEATHER_LIGHTRAIN:
            case WEATHER_RAIN:
            case WEATHER_RAINSTORM:
                image = R.drawable.rain_medium;
                break;

            default:
                image = R.drawable.overcast_medium;
                break;
        }
        return image;
    }
    
    
    public static int getwidgetConditionImage(String condition, boolean need_day) {
        WeatherCondition.Condition c = WeatherCondition
                .convertCondition(condition);
        int image = 0;
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH");
        String date = sDateFormat.format(new java.util.Date());
        boolean day = false;
        // 判断为白天或夜晚
        if (date.compareTo("18") < 0 && date.compareTo("06") > 0) {
            // 6点－18点为白天
            day = true;
        }
        // 如果不需要判断白天或夜晚，则默认为白�?
        if (!need_day)
            day = true;

        switch (c) {
            case WEATHER_FINE:
                if (day)
                    image = R.drawable.widget_sunny_day_medium;
                else
                    image = R.drawable.widget_sunny_night_medium;
                break;
            case WEATHER_CLOUDY:
                if (day)
                    image = R.drawable.widget_cloudy_day_medium;
                else
                    image = R.drawable.widget_cloudy_night_medium;
                break;
            case WEATHER_OVERCAST:
                image = R.drawable.widget_overcast_medium;
                break;
            case WEATHER_SNOW:
                image = R.drawable.widget_snow_medium;
                break;
            case WEATHER_THUNDERSTORM:
                image = R.drawable.widget_thunderstorm_medium;
                break;
            case WEATHER_SLEET:
            case WEATHER_STORM:
            case WEATHER_LIGHTRAIN:
            case WEATHER_RAIN:
            case WEATHER_RAINSTORM:
                image = R.drawable.widget_rain_medium;
                break;

            default:
                image = R.drawable.widget_overcast_medium;
                break;
        }
        return image;
    }
    
}
