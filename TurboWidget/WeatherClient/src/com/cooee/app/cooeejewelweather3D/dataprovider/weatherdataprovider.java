
package com.cooee.app.cooeejewelweather3D.dataprovider;

//import com.cooee.weather.dataentity.CitysEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cooee.app.cooeejewelweather3D.dataentity.PostalCodeEntity;
import com.cooee.app.cooeejewelweather3D.dataentity.SettingEntity;
import com.cooee.app.cooeejewelweather3D.dataentity.weatherforecastentity;
import com.cooee.app.cooeejewelweather3D.filehelp.Log;
import com.cooee.weather.com.weatherdataentity;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

public class weatherdataprovider extends ContentProvider {
    private static final String TAG = "com.cooee.weather.dataprovider.WeatherDataProvider";
    // Content URI
    public static final String AUTHORITY = "com.cooee.app.cooeejewelweather3D.dataprovider";
   // public static String AUTHORITY;
    // table weather name
    public static final String TABLE_WEATHERDATA = "weather_data";
    // table weather detail name;
    private static final String TABLE_WEATHERDATA_FORECAST = "weather_forecast";
    // table postalCode and userId
    private static final String TABLE_POSTALCODE = "postalCode";
    // table setting
    private static final String TABLE_SETTING = "setting";
    // table citys
    //private static final String TABLE_CITYS = "citys";
    private static final String TABLE_CITYS = "CITY_LIST"; //sxd 新浪城市数据�?

 //   private static final String DB_PATH = "/data/data/com.cooee.widget3D.Weather/databases/";
    private static String DB_PATH;
    private static final String DB_NAME = "city_db.db";

    public enum WEATHER_CONDITION {

    }

    public static String cur_city;

    private DatabaseHelper dbHelper;
   
    private SQLiteDatabase CitysDb;

    //private CitysDbHelper citysDbHelper;
    
    public static class WeatherDataColumns implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/weather");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/weather";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/weather";

        public static final int MATCH = 101;
    }

    public static class WeatherDetailColumns implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/weather/*/detail");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/weather/*/detail";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/weather/*/detail";

        public static final int MATCH = 102;
    }

    public static class PostalCodeColumns implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/postalCode");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/postalCode";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/postalCode";

        public static final int MATCH = 103;
    }

    public static class SettingColumns implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/setting");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/setting";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/setting";

        public static final int MATCH = 104;
    }

    public static class CitysColumns implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/citys");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/citys";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/citys";

        public static final int MATCH = 105;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete uri=" + uri + ", selection = " + selection);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count = 0;

        switch (uriMatcher.match(uri)) {
            case WeatherDataColumns.MATCH: {
                count = db.delete(TABLE_WEATHERDATA, selection, selectionArgs);
                break;
            }
            case WeatherDetailColumns.MATCH: {
                count = db.delete(TABLE_WEATHERDATA_FORECAST, selection,
                        selectionArgs);
                break;
            }
            case PostalCodeColumns.MATCH: {
                count = db.delete(TABLE_POSTALCODE, selection, selectionArgs);
                break;
            }
            case SettingColumns.MATCH: {
                count = db.delete(TABLE_SETTING, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }

        Log.v(TAG, "delete count = " + count);

        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case WeatherDataColumns.MATCH:
                return WeatherDataColumns.CONTENT_ITEM_TYPE;
            case WeatherDetailColumns.MATCH:
                return WeatherDetailColumns.CONTENT_TYPE;
            case PostalCodeColumns.MATCH:
                return PostalCodeColumns.CONTENT_ITEM_TYPE;
            case SettingColumns.MATCH: {
                return SettingColumns.CONTENT_ITEM_TYPE;
            }
            case CitysColumns.MATCH: {
                return CitysColumns.CONTENT_ITEM_TYPE;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert() with uri=" + uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Uri resultUri = null;

        switch (uriMatcher.match(uri)) {
            case WeatherDataColumns.MATCH: {
                long rowId = db.insert(TABLE_WEATHERDATA,
                        weatherdataentity.POSTALCODE, values);
                if (rowId != -1) {
                    resultUri = ContentUris.withAppendedId(
                            WeatherDataColumns.CONTENT_URI, rowId);
                }
                break;
            }
            case WeatherDetailColumns.MATCH: {
                // Insert a forecast into a specific widget
                long rowId = db.insert(TABLE_WEATHERDATA_FORECAST, null, values);
                if (rowId != -1) {
                    resultUri = ContentUris.withAppendedId(
                            WeatherDetailColumns.CONTENT_URI, rowId);
                }
                break;
            }
            case PostalCodeColumns.MATCH: {
                long rowId = db.insert(TABLE_POSTALCODE, PostalCodeEntity.USER_ID,
                        values);
                if (rowId != -1) {
                    resultUri = ContentUris.withAppendedId(
                            PostalCodeColumns.CONTENT_URI, rowId);
                }
                break;
            }
            case SettingColumns.MATCH: {
                long rowId = db.insert(TABLE_SETTING, null, values);
                if (rowId != -1) {
                    resultUri = ContentUris.withAppendedId(
                            SettingColumns.CONTENT_URI, rowId);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }

        // Notify any listeners that the data backing the content provider has
        // changed, and return
        // the number of rows affected.
        getContext().getContentResolver().notifyChange(uri, null);

        return resultUri;
    }

   
    private static boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        String myPath = DB_PATH + DB_NAME;
        try {
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.v("Database", "Error");
            e.printStackTrace();
        }
        if (checkDB != null) {
            checkDB.close();
        }
        Log.v(TAG, "check citys DataBase checkDB = " + checkDB);
        return checkDB != null ? true : false;
    }

    private static void copyDatabase(Context context) {
        // 拷贝assets中的数据�?
        String outFileName;
        InputStream myInput;
        Log.v(TAG, "copyDatabase");
        if (checkDataBase() == false) {
            try {
                myInput = context.getAssets().open(DB_NAME);
                outFileName = DB_PATH + DB_NAME;
                // �?��目录
                File f = new File(DB_PATH);
                if (!f.exists()) {
                    f.mkdirs();
                    Log.v(TAG, "mkdir: " + DB_PATH);
                }
                // �?��文件
                f = new File(outFileName);
                if (!f.exists()) {
                    f.createNewFile();
                    Log.v(TAG, "create new file: " + outFileName);
                }
                OutputStream myOutput = new FileOutputStream(outFileName);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
                myOutput.close();
                myInput.close();
                Log.v(TAG, "copy complete.");
            } catch (IOException e) {
                e.printStackTrace();
                Log.v(TAG, "error");
            }
        }
    }

/*    private static void deleteDatabase(Context context) {
        String filename = DB_PATH + DB_NAME;
        File f = new File(filename);
        if (f.exists()) {
            f.delete();
        }
    }*/

    @Override
    public boolean onCreate() {
    	{
	    //	AUTHORITY = getContext().getPackageName()+".dataprovider";
	    	DB_PATH = "/data/data/"+ getContext().getPackageName() +"/databases/";
			uriMatcher.addURI(AUTHORITY, "weather/*", WeatherDataColumns.MATCH);
		    uriMatcher.addURI(AUTHORITY, "weather/*/detail",
		             WeatherDetailColumns.MATCH);
		    uriMatcher.addURI(AUTHORITY, "postalCode", PostalCodeColumns.MATCH);
		    uriMatcher.addURI(AUTHORITY, "setting", SettingColumns.MATCH);
		    uriMatcher.addURI(AUTHORITY, "citys", CitysColumns.MATCH);
    	}
        dbHelper = new DatabaseHelper(getContext());

        copyDatabase(getContext());
        CitysDb = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
                SQLiteDatabase.OPEN_READONLY);

        // citysDbHelper = new CitysDbHelper(getContext());
        return (dbHelper == null) ? false : true;
    }

    private Cursor queryCitys(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // SQLiteDatabase db = citysDbHelper.getReadableDatabase();
        // �?��数据库是否存�?
        copyDatabase(getContext());

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String limit = null;

        qb.setTables(TABLE_CITYS);

        // 屏蔽三级城市
        // if (weatherwebservice.dataSourceFlag != WeatherDataSource.WEATHER_CN)
        // {
        // selection = selection + " AND " + " NOT " + CitysEntity.NAME +
        // " LIKE " + "'%.%'";
        // }

        return qb.query(CitysDb, projection, selection, selectionArgs, null, null,
                sortOrder, limit);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // 城市数据�?
        if (uriMatcher.match(uri) == CitysColumns.MATCH) {
            return queryCitys(uri, projection, selection, selectionArgs, sortOrder);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String limit = null;

        Log.v(TAG, "query uri = " + uri + ", match = " + uriMatcher.match(uri));
        switch (uriMatcher.match(uri)) {
            case WeatherDataColumns.MATCH: {
                qb.setTables(TABLE_WEATHERDATA);
                break;
            }
            case WeatherDetailColumns.MATCH: {
                // Pick all the forecasts for given widget, sorted by date and
                // importance
                qb.setTables(TABLE_WEATHERDATA_FORECAST);
                sortOrder = BaseColumns._ID + " ASC";
                break;
            }
            case PostalCodeColumns.MATCH: {
                qb.setTables(TABLE_POSTALCODE);
                break;
            }
            case SettingColumns.MATCH: {
                qb.setTables(TABLE_SETTING);
                break;
            }
        }

        return qb.query(db, projection, selection, selectionArgs, null, null,
                sortOrder, limit);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = -1;

        Log.d(TAG, "update() with uri = " + uri);

        switch (uriMatcher.match(uri)) {
            case WeatherDataColumns.MATCH: {
                ret = db.update(TABLE_WEATHERDATA, values, selection, selectionArgs);
                break;
            }
            case WeatherDetailColumns.MATCH: {
                ret = db.update(TABLE_WEATHERDATA_FORECAST, values, selection, null);
                break;
            }
            case PostalCodeColumns.MATCH: {
                ret = db.update(TABLE_POSTALCODE, values, selection, selectionArgs);
                break;
            }
            case SettingColumns.MATCH: {
                ret = db.update(TABLE_SETTING, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }

        // Notify any listeners that the data backing the content provider has
        // changed, and return
        // the number of rows affected.
        getContext().getContentResolver().notifyChange(uri, null);

        return ret;
    }

/*    private static class CitysDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "city_db.db";
        private static final int DATABASE_VERSION = 6;

        private final String TAG = weatherdataprovider.TAG + ".CitysDbHelper";

        private Context mContext;

        public CitysDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            int version = oldVersion;

            if (version != DATABASE_VERSION) {
                Log.w(TAG, "onUpgrade oldVersion = " + oldVersion
                        + ", newVersion = " + newVersion
                        + " ,DATABASE_VERSION = " + DATABASE_VERSION);

                deleteDatabase(mContext);

                copyDatabase(mContext);
            }
        }
    }*/

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private final String TAG = weatherdataprovider.TAG + ".DatabaseHelper";

        private static final String DATABASE_NAME = "forecasts.db";
        private static final int DATABASE_VERSION = 5;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.v("TAG", "DatabaseHelper(context = " + context + ")");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_WEATHERDATA + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                    + weatherdataentity.CITY + " TEXT,"
                    + weatherdataentity.UPDATE_MILIS + " INTEGER,"
                    + weatherdataentity.IS_CONFIGURED + " INTEGER,"
                    + weatherdataentity.POSTALCODE + " TEXT,"
                    + weatherdataentity.FORECASTDATE + " INTEGER,"
                    + weatherdataentity.CONDITION + " TEXT,"
                    + weatherdataentity.TEMPF + " INTEGER,"
                    + weatherdataentity.TEMPC + " INTEGER,"
                    + weatherdataentity.TEMPH + " INTEGER,"
                    + weatherdataentity.TEMPL + " INTEGER,"
                    + weatherdataentity.HUMIDITY + " TEXT,"
                    + weatherdataentity.ICON + " TEXT,"
                    + weatherdataentity.WINDCONDITION + " TEXT,"
                    + weatherdataentity.LAST_UPDATE_TIME + " INTEGER);");

            db.execSQL("CREATE TABLE " + TABLE_WEATHERDATA_FORECAST + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                    + weatherforecastentity.CITY + " TEXT,"
                    + weatherforecastentity.DAYOFWEEK + " TEXT,"
                    + weatherforecastentity.LOW + " INTEGER,"
                    + weatherforecastentity.HIGHT + " INTEGER,"
                    + weatherforecastentity.ICON + " TEXT,"
                    + weatherforecastentity.CONDITION + " TEXT);");

            db.execSQL("CREATE TABLE " + TABLE_POSTALCODE + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                    + PostalCodeEntity.POSTAL_CODE + " TEXT,"
                    + PostalCodeEntity.USER_ID + " TEXT,"
                    + PostalCodeEntity.CITY_NUM + " TEXT);");

            db.execSQL("CREATE TABLE " + TABLE_SETTING + " (" + BaseColumns._ID
                    + " INTEGER PRIMARY KEY," + SettingEntity.UPDATE_WHEN_OPEN
                    + " INTEGER," + SettingEntity.UPDATE_REGULARLY
                    + " INTEGER," + SettingEntity.UPDATE_INTERVAL + " INTEGER,"
                    + SettingEntity.SOUND_ENABLE + " INTEGER);");
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            int version = oldVersion;

            if (version != DATABASE_VERSION) {
                Log.w(TAG, "onDowngrade oldVersion = " + oldVersion
                        + ", newVersion = " + newVersion
                        + " ,DATABASE_VERSION = " + DATABASE_VERSION);

                Log.w(TAG, "Destroying old data during upgrade.");
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHERDATA);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHERDATA_FORECAST);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTALCODE);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTING);
                onCreate(db);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            int version = oldVersion;

            if (version != DATABASE_VERSION) {
                Log.w(TAG, "onUpgrade oldVersion = " + oldVersion
                        + ", newVersion = " + newVersion
                        + " ,DATABASE_VERSION = " + DATABASE_VERSION);

                Log.w(TAG, "Destroying old data during upgrade.");
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHERDATA);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHERDATA_FORECAST);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTALCODE);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTING);
                onCreate(db);
            }
        }
    }

    /**
     * Matcher used to filter an incoming {@link Uri}.
     */
    private static final UriMatcher uriMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "weather/*", WeatherDataColumns.MATCH);
        uriMatcher.addURI(AUTHORITY, "weather/*/detail",
                WeatherDetailColumns.MATCH);
        uriMatcher.addURI(AUTHORITY, "postalCode", PostalCodeColumns.MATCH);
        uriMatcher.addURI(AUTHORITY, "setting", SettingColumns.MATCH);
        uriMatcher.addURI(AUTHORITY, "citys", CitysColumns.MATCH);
    }
}
