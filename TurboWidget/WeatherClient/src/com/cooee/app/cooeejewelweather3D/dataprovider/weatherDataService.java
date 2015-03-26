
package com.cooee.app.cooeejewelweather3D.dataprovider;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.text.format.Time;

import java.util.LinkedList;
import java.util.Queue;

import com.cooee.app.cooeejewelweather3D.dataentity.PostalCodeEntity;
import com.cooee.app.cooeejewelweather3D.dataentity.SettingEntity;
import com.cooee.app.cooeejewelweather3D.dataprovider.weatherwebservice.FLAG_UPDATE;
import com.cooee.app.cooeejewelweather3D.filehelp.Log;
import com.cooee.weather.com.weatherdataentity;

//public class weatherDataService extends Service implements Runnable {
public class weatherDataService extends IntentService{
    private static String TAG = "com.cooee.weather.dataprovider.weatherDataService";
    private static boolean isThreadRun = false;

//    private static Object sLock = new Object();

    private static Queue<Integer> requestWidgetIDs = new LinkedList<Integer>();

    public static final String ACTION_UPDATE_ALL = "com.cooee.weather.dataprovider.UPDATE_ALL";
    public static final String[] widgetProjection = new String[] {
            weatherdataentity.IS_CONFIGURED,
            weatherdataentity.LAST_UPDATE_TIME, weatherdataentity.UPDATE_MILIS
    };

    public static final String NUM_COUNT_RECEIVER = "com.cooee.weather.datacom.action.NUM_COUNT";

    public static final String UPDATE_RESULT = "com.cooee.weather.data.action.UPDATE_RESULT";

    public final String SETTING_URI = "content://com.cooee.app.cooeejewelweather3D.dataprovider/setting";

    private String mPostalCode = null;
    private String allPostalCode = null;
    private int mUserId = 0;
    private int allUserId = 0;
    private int mForcedUpdate = 0; // ǿ�и��У���ʹ��ʱ���������

    private SettingEntity mSettingEntity = new SettingEntity();

    
    public weatherDataService(String name) {
		super("aaaaaaaaaa");
	}
    
    public weatherDataService() {
		super("aaaaaaaaaa");
	}
    
    public void readSetting() {
        // ��ȡ����
        ContentResolver resolver = getContentResolver();
        Cursor cursor = null;
        boolean found = false;
        Uri uri = Uri.parse(SETTING_URI);

        cursor = resolver
                .query(uri, SettingEntity.projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                found = true;
            }
        }

        // ���û�ҵ����ã��趨Ĭ��ֵ
        if (!found) {
            // ��������Ĭ��ֵ
            ContentValues values = new ContentValues();
            mSettingEntity.setUpdateWhenOpen(0);
            mSettingEntity.setUpdateRegularly(1);
            mSettingEntity.setUpdateInterval(1);
            mSettingEntity.setSoundEnable(0);

            values.put(SettingEntity.UPDATE_WHEN_OPEN, mSettingEntity.getUpdateWhenOpen());
            values.put(SettingEntity.UPDATE_REGULARLY, mSettingEntity.getUpdateRegularly());
            values.put(SettingEntity.UPDATE_INTERVAL, mSettingEntity.getUpdateInterval());
            values.put(SettingEntity.SOUND_ENABLE, mSettingEntity.getSoundEnable());
            resolver.insert(uri, values);
        } else {
            mSettingEntity.setUpdateWhenOpen(cursor.getInt(0));
            mSettingEntity.setUpdateRegularly(cursor.getInt(1));
            mSettingEntity.setUpdateInterval(cursor.getInt(2));
            mSettingEntity.setSoundEnable(cursor.getInt(3));
        }

        if (cursor != null) {
            cursor.close();
        }
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.v(TAG, "weather data service Intent action = " + intent.getAction());

        if (ACTION_UPDATE_ALL.equals(intent.getAction())) {

        } else {

        }

        // Only start processing thread if not already running
  //      synchronized (sLock) {
            //if (!isThreadRun) {
            
     //   }
        return START_REDELIVER_INTENT;
    }

    public void mySendBroadcast() {
    	Log.v(TAG ,"mySendBroadcast");
        Intent intent = new Intent();
        intent.setAction(UPDATE_RESULT);
        intent.putExtra("cooee.weather.updateResult.postalcode",
                mPostalCode);
        intent.putExtra("cooee.weather.updateResult.userId", mUserId);
        if (weatherwebservice.Update_Result_Flag == FLAG_UPDATE.UPDATE_SUCCES) {
            intent.putExtra("cooee.weather.updateResult", "UPDATE_SUCCESED");
        } else if (weatherwebservice.Update_Result_Flag == FLAG_UPDATE.AVAILABLE_DATA) {
            intent.putExtra("cooee.weather.updateResult", "AVAILABLE_DATA");
		} else if (weatherwebservice.Update_Result_Flag == FLAG_UPDATE.INVILIDE_VALUE) {
			intent.putExtra("cooee.weather.updateResult", "INVILIDE_DATA");
        } else {
            intent.putExtra("cooee.weather.updateResult", "UPDATE_FAILED");
        }
        sendBroadcast(intent);
    }

    public void allSendBroadcast() {
        if (weatherwebservice.Update_Result_Flag == FLAG_UPDATE.UPDATE_SUCCES) {
        	Log.v(TAG ,"allSendBroadcast:FLAG_UPDATE.UPDATE_SUCCES");
            Intent intent = new Intent();
            intent.setAction(UPDATE_RESULT);
            intent.putExtra("cooee.weather.updateResult", "UPDATE_SUCCESED");
            intent.putExtra("cooee.weather.updateResult.postalcode",
                    allPostalCode);
            intent.putExtra("cooee.weather.updateResult.userId", allUserId);
            sendBroadcast(intent);
        } else {
        	Log.v(TAG ,"allSendBroadcast:FLAG_UPDATE.UPDATE_FAILED");
            Intent intent = new Intent();
            intent.setAction(UPDATE_RESULT);
            intent.putExtra("cooee.weather.updateResult", "UPDATE_FAILED");
            intent.putExtra("cooee.weather.updateResult.postalcode",
                    allPostalCode);
            intent.putExtra("cooee.weather.updateResult.userId", allUserId);
            sendBroadcast(intent);
        }
    }

   // @Override
  //  public void run() {}

    /**
     * function addWedgetIDs ����������UserId
     */
    public static void addWidgetIDs(int[] widgetIDs) {
     //   synchronized (sLock) {
            for (int id : widgetIDs) {
                Log.d(TAG, "add widget ID:" + id);
                requestWidgetIDs.add(id);
            }
     //   }
    }

    /**
     * function hasMoreWidgetIds �ж϶������Ƿ���UserId
     */
    public static boolean hasMoreWidgetIDs() {
    //    synchronized (sLock) {
            boolean hasMore = !requestWidgetIDs.isEmpty();
            if (!hasMore) {
                isThreadRun = hasMore;
            }
            return hasMore;
   //     }
    }

    /**
     * ��ȡ����ͷ������
     */
    public static Integer nextWidgetIDs() {
    //    synchronized (sLock) {
            if (requestWidgetIDs.peek() != null) {
                return requestWidgetIDs.poll();
            } else {
                return 0;
            }
     //   }
    }

	@Override
	protected void onHandleIntent(Intent arg0) {

   //     synchronized (sLock) {
            long now = System.currentTimeMillis();

            readSetting();
            
            if(true){
                mPostalCode = arg0.getStringExtra("postalCode");
                mUserId = arg0.getIntExtra("userId", 0);
                mForcedUpdate = arg0.getIntExtra("forcedUpdate", 0);

                if (mPostalCode == null) {
                    mPostalCode = "all";
                }

                Log.v(TAG, "onStartCommand mPostalCode = " + mPostalCode
                        + ", mUserId = " + mUserId);

                isThreadRun = true;
                //new Thread(this).start();
            }
            
            
            if (mPostalCode.equals("bootup")) {
            } else if (mPostalCode.equals("all")) {
                ContentResolver resolver = this.getContentResolver();
                Cursor cursor = null;
                int i = 0;
                String[] postalcode = null;
                int[] id = null;
                Uri uri = Uri
                        .parse("content://com.cooee.app.cooeejewelweather3D.dataprovider/postalCode");
                cursor = resolver.query(uri,
                        PostalCodeEntity.projection, null,
                        null, null);
                if (cursor != null) {
                    int count = cursor.getCount();
                    postalcode = new String[count];
                    id = new int[count];
                    while (cursor != null && cursor.moveToNext()) {
                        postalcode[i] = cursor.getString(0);
                        id[i] = cursor.getInt(1);
                        i++;
                    }
                    addWidgetIDs(id);
                    i = 0;
                    while (hasMoreWidgetIDs())
                    {
                        int widgetid = nextWidgetIDs();
                        boolean ignored = false;
                        allUserId = id[i];
                        allPostalCode = postalcode[i];
                        Log.v(TAG, "allUserId = " + id[i] + " allPostalCode = " + postalcode[i]
                                + " WidgetIDs = " + widgetid);
                        for (int j = 0; j < i; j++) {
                            if (postalcode[i].equals(postalcode[j])) {
                                ignored = true;
                                break;
                            }
                        }
                        if (!ignored) { 
                            Uri data_uri = Uri
                                    .parse("content://com.cooee.app.cooeejewelweather3D.dataprovider/weather/"
                                            + postalcode[i]);
                            weatherwebservice.updateWeatherData(this, data_uri);
                            allSendBroadcast();
                        }
                        i++;
                    }
                }
                cursor.close();
            } else {
                boolean needUpdate = true;
                if (mForcedUpdate == 0) {
                    ContentResolver resolver = this.getContentResolver();
                    Cursor cursor = null;
                    String selection = null;
                    Uri uri = Uri.parse("content://"
                            + weatherdataprovider.AUTHORITY + "/weather/"
                            + mPostalCode);
                    selection = weatherdataentity.POSTALCODE + "=" + "'" + mPostalCode + "'";
                    cursor = resolver.query(uri, weatherdataentity.projection, selection, null,
                            null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            long lastUpdateTime = cursor.getLong(10);
                            if (now - lastUpdateTime < mSettingEntity.getUpdateInterval())
                            {
                                needUpdate = false;
                            }
                        }
                        cursor.close();
                    }
                }

                Log.v(TAG, "needUpdate = " + needUpdate);
                if (needUpdate) {
                    Uri uri = Uri.parse("content://"
                            + weatherdataprovider.AUTHORITY + "/weather/"
                            + mPostalCode);
                    weatherwebservice.updateWeatherData(this, uri);
                } else {
                    weatherwebservice.Update_Result_Flag = FLAG_UPDATE.AVAILABLE_DATA;
                }

                mySendBroadcast();
            }

            if (mSettingEntity.getUpdateRegularly() != 0) {
                Time time = new Time();
                long interval = mSettingEntity.getUpdateInterval();
                if(interval<60*1000*60*6)
                	interval = 60*1000*60*6;
                
                if(weatherwebservice.Update_Result_Flag != FLAG_UPDATE.UPDATE_SUCCES){
                	interval = 60*1000*10;
                }
                time.set(now + interval);
                // time.set(now + 2 * 60 * 1000);
                long nextUpdate = time.toMillis(true);

                Intent updateIntent = new Intent(ACTION_UPDATE_ALL);
                updateIntent.setClass(this, weatherDataService.class);

                PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                        updateIntent, 0);

                // Schedule alarm, and force the device awake for this update
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, nextUpdate,
                        pendingIntent);
            }

            isThreadRun = false;

            // No updates remaining, so stop service
            stopSelf();
   //     }
    
	}
}
