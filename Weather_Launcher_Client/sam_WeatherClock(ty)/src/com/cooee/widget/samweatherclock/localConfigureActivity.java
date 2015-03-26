package com.cooee.widget.samweatherclock;

import java.util.ArrayList;

import com.cooee.app.cooeeweather.dataentity.PostalCodeEntity;
import com.cooee.app.cooeeweather.dataprovider.weatherdataprovider;
import com.cooee.widget.samweatherclock.R;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

public class localConfigureActivity extends Activity{

	 private int mWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	 private Context mContext;
	    //20130709-liuhailin-<auto change authority>
	    // 城市列表的uri
	    //public final static String POSTALCODE_URI = "content://com.cooee.app.cooeeweather.dataprovider/postalCode";
	    public final static String POSTALCODE_URI = "content://"+ weatherdataprovider.AUTHORITY+"/postalCode";
	    // 天气数据的uri
	    //public final static String WEATHER_URI = "content://com.cooee.app.cooeeweather.dataprovider/weather";
	    public final static String WEATHER_URI = "content://"+ weatherdataprovider.AUTHORITY+"/weather";
	  //20130709-liuhailin
	    // 数据服务的action
	  //20130709-liuhailin-<auto change authority>
	    //public final static String DATA_SERVICE_ACTION = "com.cooee.app.cooeeweather.dataprovider.weatherDataService";
	    public static String DATA_SERVICE_ACTION = MainActivity.DATA_SERVICE_ACTION;//"com.cooee.app.cooeeweather.dataprovider.weatherDataService";
	  //20130709-liuhailin
	    // 广播的地址
	    public final static String BROATCAST_URI = "com.cooee.weather.data.action.UPDATE_RESULT";
	    public final static String CHANGE_POSTALCODE = "com.cooee.weather.Weather.action.CHANGE_POSTALCODE";
	    
	    private ArrayList<PostalCodeEntity> mPostalCodeEntityList = null;
	    
	    public void readPostalCodeList() {
	        ContentResolver resolver = getContentResolver();
	        Cursor cursor = null;
	        Uri uri = Uri.parse(localConfigureActivity.POSTALCODE_URI);
	        String selection;

	        // 先清空mPoscalCodList
	        mPostalCodeEntityList = new ArrayList<PostalCodeEntity>();
	        mPostalCodeEntityList.clear();

	        selection = PostalCodeEntity.USER_ID + "=" + "'0'";
	        cursor = resolver.query(uri, PostalCodeEntity.projection, selection,
	                null, null);

	        if (cursor != null) {
	            if (cursor.moveToFirst()) {
	                do {
	                    PostalCodeEntity mPostalCodeEntity;
	                    mPostalCodeEntity = new PostalCodeEntity();
	                    mPostalCodeEntity.setPostalCode(cursor.getString(0));
	                    mPostalCodeEntity.setUserId(cursor.getString(1));

	                    mPostalCodeEntityList.add(mPostalCodeEntity);
	                } while (cursor.moveToNext());
	            }

	            cursor.close();
	        }
	    }
	    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		  
		//可以根据多个请求代码来作相应的操作  
		if(0==resultCode)  
		{  				
			finish();
		}  
		else{
			Bundle bunde = data.getExtras();    
            String cityname = bunde.getString("citys"); 
			savePostalCode(cityname);
			Intent intent = new Intent(DATA_SERVICE_ACTION);
            intent.putExtra("postalCode", cityname);
            intent.putExtra("userId", mWidgetId);
            intent.putExtra("city_num", cityname);
            startService(intent);

            // 更新一次widget，让widget显示出城市
            //WeatherProvider.updateWidget(mContext, mWidgetId);
            setConfigureResult(Activity.RESULT_OK);
            finish();
		}
	
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.select_postalcode_layout);

        // 获得widgetId
        mWidgetId = getIntent().getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
        setConfigureResult(Activity.RESULT_CANCELED);
        if (mWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        // 读取城市列表
        mPostalCodeEntityList = new ArrayList<PostalCodeEntity>();
        readPostalCodeList();
		readPostalCodeList();
		
		Intent intent = new Intent();
		mContext = this;
        intent.setClassName("com.cooee.weather",
                "com.cooee.weather.WeatherEditPost");
		startActivityForResult(intent, CONTEXT_RESTRICTED);
	}

	public void setConfigureResult(int resultCode) {
        final Intent data = new Intent();
        data.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
        setResult(resultCode, data);
    }
	
	public void savePostalCode(String postalCode) {
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse(localConfigureActivity.POSTALCODE_URI);
        ContentValues values = new ContentValues();

        values.put(PostalCodeEntity.POSTAL_CODE, postalCode);
        values.put(PostalCodeEntity.USER_ID, mWidgetId);

        resolver.insert(uri, values);
    }
	
	
}
