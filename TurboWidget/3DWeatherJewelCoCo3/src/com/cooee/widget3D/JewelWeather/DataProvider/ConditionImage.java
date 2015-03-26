
package com.cooee.widget3D.JewelWeather.DataProvider;

import java.text.SimpleDateFormat;

import com.cooee.weather.com.WeatherCondition;


public class ConditionImage {
    private static String TAG = "com.iLoong.Weather.DataProvider.ConditionImage";

    public static String getConditionImage(String condition) {
        return getConditionImage(condition, false);
    }

    public static String getConditionImage(String condition, boolean need_day) {
        WeatherCondition.Condition c = WeatherCondition
                .convertCondition(condition);
        String image = null;
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH");
        String date = sDateFormat.format(new java.util.Date());
        boolean day = false;
        if (date.compareTo("18") < 0 && date.compareTo("06") > 0) {
            day = true;
        }
        if (!need_day)
            day = true;

        switch (c) {
            case WEATHER_FINE:
                if (day)
                    image = "/condition/sunny.png";
                else
                    image = "/condition/sunny_night.png";
                break;
            case WEATHER_CLOUDY:
                if (day)
                    image = "/condition/cloudy_day.png";
                else
                    image = "/condition/cloudy_night.png";
                break;
            case WEATHER_OVERCAST:
                image = "/condition/overcast.png";
                break;
            case WEATHER_SNOW:
                image = "/condition/snow.png";
                break;
            case WEATHER_THUNDERSTORM:
                image = "/condition/thunder.png";
                break;
            case WEATHER_SLEET:
            case WEATHER_STORM:
            case WEATHER_LIGHTRAIN:
            case WEATHER_RAIN:
            case WEATHER_RAINSTORM:
                image = "/condition/rain.png";
                break;
            case WEATHER_FOG:
            default:
                image = "/condition/sunny.png";
                break;
        }
        return image;
    }
}
