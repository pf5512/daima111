
package com.iLoong.WeatherClock.view;
import com.iLoong.WeatherClock.common.CityResult;
import com.iLoong.WeatherClock.common.Weather;
import com.iLoong.WeatherClock.common.YahooClient;
import com.iLoong.launcher.desktop.iLoongLauncher;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class Weatherwebservice {
    public static FLAG_UPDATE Update_Result_Flag = FLAG_UPDATE.WEBSERVICE_ERROR;
    public static enum FLAG_UPDATE {
        UPDATE_SUCCES,
        WEBSERVICE_ERROR
    }

    public static void updateWeatherData(Context context, CityResult cr ,
			String unit ) {
    	Weather weather=YahooClient.getWeatherInfo( cr , unit , context,2 );
    	if(weather != null && weather.getList() != null && weather.getList().size() == 5){
    		Update_Result_Flag = FLAG_UPDATE.UPDATE_SUCCES;
    		Editor ed = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit();
			ed.putBoolean( "weatherstate" , true );
			ed.putString( "weathercityname" , weather.getWeathercity() );
			ed.putString( "weathercode" , weather.getWeathercode() );
			ed.putString( "weathercondition" , weather.getWeathercondition() );
			ed.putString( "weathercurrenttmp" , weather.getCurrtmp() );
			ed.putString( "weathershidu" , weather.getShidu() );
			ed.putString( "listweathercode0" , weather.getList().get( 0 ).getWeathercode() );
			ed.putString( "listweatherhighTmp0" , weather.getList().get( 0 ).getHightmp() );
			ed.putString( "listweatherlowTmp0" , weather.getList().get( 0 ).getLowtmp() );
			ed.putString( "listweatherweek0" , weather.getList().get( 0 ).getWeatherweek() );
			ed.putString( "listweathercode1" , weather.getList().get( 1 ).getWeathercode() );
			ed.putString( "listweatherhighTmp1" , weather.getList().get( 1 ).getHightmp() );
			ed.putString( "listweatherlowTmp1" , weather.getList().get( 1 ).getLowtmp() );
			ed.putString( "listweatherweek1" , weather.getList().get( 1 ).getWeatherweek() );
			ed.putString( "listweathercode2" , weather.getList().get( 2 ).getWeathercode() );
			ed.putString( "listweatherhighTmp2" , weather.getList().get( 2 ).getHightmp() );
			ed.putString( "listweatherlowTmp2" , weather.getList().get( 2 ).getLowtmp() );
			ed.putString( "listweatherweek2" , weather.getList().get( 2 ).getWeatherweek() );
			ed.putString( "listweathercode3" , weather.getList().get( 3 ).getWeathercode() );
			ed.putString( "listweatherhighTmp3" , weather.getList().get( 3 ).getHightmp() );
			ed.putString( "listweatherlowTmp3" , weather.getList().get( 3 ).getLowtmp() );
			ed.putString( "listweatherweek3" , weather.getList().get( 3 ).getWeatherweek() );
			ed.putString( "listweathercode4" , weather.getList().get( 4 ).getWeathercode() );
			ed.putString( "listweatherhighTmp4" , weather.getList().get( 4 ).getHightmp() );
			ed.putString( "listweatherlowTmp4" , weather.getList().get( 4 ).getLowtmp() );
			ed.putString( "listweatherweek4" , weather.getList().get( 4 ).getWeatherweek() );
			ed.commit();
    	}else{
    		Log.d( "mytag" , "天气时钟更新失败" );
    		Update_Result_Flag = FLAG_UPDATE.WEBSERVICE_ERROR;
    	}
    }

}
