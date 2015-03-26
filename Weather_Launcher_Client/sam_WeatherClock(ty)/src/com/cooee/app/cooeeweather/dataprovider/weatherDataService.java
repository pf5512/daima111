
package com.cooee.app.cooeeweather.dataprovider;
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

import com.cooee.app.cooeeweather.dataentity.PostalCodeEntity;
import com.cooee.app.cooeeweather.dataentity.SettingEntity;
import com.cooee.app.cooeeweather.dataentity.weatherdataentity;
import com.cooee.app.cooeeweather.dataprovider.weatherwebservice.FLAG_UPDATE;
import com.cooee.app.cooeeweather.filehelp.Log;
import com.cooee.widget.samweatherclock.R;

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
  //20130709-liuhailin-<auto change authority>
    public static final String UPDATE_RESULT = "com.cooee.weather.data.action.UPDATE_RESULT";
  //20130709-liuhailin
    
  //20130709-liuhailin-<auto change authority>
    //public final String SETTING_URI = "content://com.cooee.app.cooeeweather.dataprovider/setting";
    public String SETTING_URI = "content://"+ weatherdataprovider.AUTHORITY +"/setting";
    //20130709-liuhailin
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
        Log.d(TAG, "weather data service started");

        Log.v(TAG, "Intent action = " + intent.getAction());

		// }
		return START_REDELIVER_INTENT;
    }

    public void mySendBroadcast() {
        Intent intent = new Intent();
        intent.setAction(UPDATE_RESULT);
      //20130709-liuhailin-<auto change authority>
        intent.putExtra("packagename", getPackageName().toString());
      //20130709-liuhailin
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
            Intent intent = new Intent();
            intent.setAction(UPDATE_RESULT);
            //20130709-liuhailin-<auto change authority>
            intent.putExtra("packagename", getPackageName().toString());
          //20130709-liuhailin
            intent.putExtra("cooee.weather.updateResult", "UPDATE_SUCCESED");
            intent.putExtra("cooee.weather.updateResult.postalcode",
                    allPostalCode);
            intent.putExtra("cooee.weather.updateResult.userId", allUserId);
            sendBroadcast(intent);
        } else {
            Intent intent = new Intent();
            intent.setAction(UPDATE_RESULT);
            //20130709-liuhailin-<auto change authority>
            intent.putExtra("packagename", getPackageName().toString());
            //20130709-liuhailin
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
	protected void onHandleIntent(Intent intent) {

   //     synchronized (sLock) {
            long now = System.currentTimeMillis();

		//from startCommand to here
		if (ACTION_UPDATE_ALL.equals(intent.getAction())) {

		} else {

		}

		// Only start processing thread if not already running
		// synchronized (sLock) {
		// if (!isThreadRun) {
		if (true) {
			// ��ò���
			mPostalCode = intent.getStringExtra("postalCode");
			mUserId = intent.getIntExtra("userId", 0);
			mForcedUpdate = intent.getIntExtra("forcedUpdate", 0);

			if (mPostalCode == null) {
				mPostalCode = "all";
			}

			Log.v(TAG, "onStartCommand mPostalCode = " + mPostalCode
					+ ", mUserId = " + mUserId);

			isThreadRun = true;
			// new Thread(this).start();
		}
            // ��ȡ����
            readSetting();
            
            if (true) {
                // 获得参数
            	Log.d(TAG, "weather data service started1");
                mPostalCode = intent.getStringExtra("postalCode");
                Log.d(TAG, "weather data service started mPostalCode = "+ mPostalCode);
                mUserId = intent.getIntExtra("userId", 0);
                mForcedUpdate = intent.getIntExtra("forcedUpdate", 0);

                if (mPostalCode == null) {
                    mPostalCode = "all";
                }

                Log.v(TAG, ">>>>onStartCommand mPostalCode = " + mPostalCode
                        + ", mUserId = " + mUserId);

                isThreadRun = true;
                //new Thread(this).start();
            }

            if (mPostalCode.equals("bootup")) {
                // �����������񣬲�����������ݲ�������������ʱ������������д򿪶�ʱ���£�
            } else if (mPostalCode.equals("all")) {
                // �Ը���ʱ�������еĳ������������ر�Ĺ㲥
                ContentResolver resolver = this.getContentResolver();
                Cursor cursor = null;
                int i = 0;
                String[] postalcode = null;
                int[] id = null;
              //20130709-liuhailin-<auto change authority>
               // Uri uri = Uri
                 //       .parse("content://com.cooee.app.cooeeweather.dataprovider/postalCode");
              Uri uri = Uri
                       .parse("content://"+ weatherdataprovider.AUTHORITY +"/postalCode");
              //20130709-liuhailin-<auto change authority>
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
                        // �ж��Ƿ��Ѿ����¹�
                        for (int j = 0; j < i; j++) {
                            if (postalcode[i].equals(postalcode[j])) {
                                ignored = true;
                                break;
                            }
                        }
                        if (!ignored) { // �ظ��ĳ����������
                        	//20130709-liuhailin-<auto change authority>	
                        	/*
                            Uri data_uri = Uri
                                    .parse("content://com.cooee.app.cooeeweather.dataprovider/weather/"
                                            + postalcode[i]);
                            */
                            Uri data_uri = Uri
                                    .parse("content://"+ weatherdataprovider.AUTHORITY +"/weather/"
                                            + postalcode[i]);
                            //20130709-liuhailin
                            weatherwebservice.updateWeatherData(this, data_uri);
                            // ������ɣ����͹㲥
                            allSendBroadcast();
                        }
                        i++;
                    }
                }
                cursor.close();
            } else {
                boolean needUpdate = true;
                if (mForcedUpdate == 0) {
                    // ��ȡ��ݿ�
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
                            // ����ڶ�ʱ���ڶ�����ݣ����������
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
                    // ʹ��google api��������Ԥ����Ϣ
                    Uri uri = Uri.parse("content://"
                            + weatherdataprovider.AUTHORITY + "/weather/"
                            + mPostalCode);
                    weatherwebservice.updateWeatherData(this, uri);
                } else {
                    weatherwebservice.Update_Result_Flag = FLAG_UPDATE.AVAILABLE_DATA;
                }

                // ������ɣ����͹㲥
                mySendBroadcast();
            }

            // ��ʱ������service
            if (mSettingEntity.getUpdateRegularly() != 0) {
                Time time = new Time();
                // ������ʱ������
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
