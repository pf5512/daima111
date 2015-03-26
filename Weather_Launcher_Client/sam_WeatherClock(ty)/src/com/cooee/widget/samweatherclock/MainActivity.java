package com.cooee.widget.samweatherclock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.cooee.app.cooeeweather.dataentity.PostalCodeEntity;
import com.cooee.app.cooeeweather.dataentity.SettingEntity;
import com.cooee.app.cooeeweather.dataentity.weatherdataentity;
import com.cooee.app.cooeeweather.dataentity.weatherforecastentity;
import com.cooee.app.cooeeweather.dataprovider.weatherdataprovider;
import com.cooee.app.cooeeweather.filehelp.Log;
import com.cooee.app.cooeeweather.view.WeatherConditionImage;
import com.cooee.app.cooeeweather.view.WeatherEditPost;
import com.cooee.app.cooeeweather.view.WeatherReceiver;
import com.cooee.app.cooeeweather.view.WeatherSetting;
import com.cooee.app.cooeeweather.view.WeatherObserver;
import com.cooee.widget.samweatherclock.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends  Activity implements View.OnClickListener,
View.OnTouchListener {
	public final static String TAG = "com.cooee.weather";
	//20130709-liuhailin-<auto change authority>
    //public final static String DATA_SERVICE_ACTION = "com.cooee.app.cooeeweather.dataprovider.weatherDataService";
    public static String DATA_SERVICE_ACTION = "com.cooee.app.cooeeweather.dataprovider.weatherDataService";
    //20130709-liuhailin
    //20130709-liuhailin-<auto change authority>
    //public final static String WEATHER_URI = "content://com.cooee.app.cooeeweather.dataprovider/weather";
    //public final static String POSTALCODE_URI = "content://com.cooee.app.cooeeweather.dataprovider/postalCode";
    //public final static String SETTING_URI = "content://com.cooee.app.cooeeweather.dataprovider/setting";
    public final static String WEATHER_URI = "content://"+ weatherdataprovider.AUTHORITY +"/weather";
    public final static String POSTALCODE_URI = "content://"+ weatherdataprovider.AUTHORITY +"/postalCode";
    public final static String SETTING_URI = "content://"+ weatherdataprovider.AUTHORITY +"/setting";
    //20130709-liuhailin
    
  //20130709-liuhailin-<auto change authority>
    public final static String BROATCAST_URI = "com.cooee.weather.data.action.UPDATE_RESULT";
  //20130709-liuhailin-<auto change authority>

    public static boolean city_is_faulu = true;
    private DisplayMetrics dm = new DisplayMetrics();
    // ������߷��͸�ĳ��еĹ㲥Action
    private final String CHANGE_POSTALCODE = "com.cooee.weather.Weather.action.CHANGE_POSTALCODE";

    //weijie_20130422
    private final String CLOSED_UPDATE_LAUNCHER = "com.cooee.weather.Weather.action.CLOSED_UPDATE_LAUNCHER";
    private Context mContext;
    private PopupWindow mPop;
    private weatherdataentity mDataEntity; // �������
    private SettingEntity mSettingEntity; // ����

	public static boolean defaultcity = false;
    
	private Handler mHandler;
	// ����Observer����broadcast
	// private WeatherObserver mObserver;

    private ArrayList<String> mPoscalCodList;

    private int mUserId; // ���ô�APP�Ķ���ID��Ϊ0˵���޵����ߣ�����ΪWidgetId
    private String mCurrentPostalCode;
    private int mCurrentIndex = 0;

    private boolean requesting = false;
    private boolean requestingFailed = false;

    private final int POSTALCODE_LIST_COUNT = 10;

    private Float mInitialY;
    private final Float SLIDE_SENSITIVITY = 30.0f;

    public void mySendBroadcast() {
        // ���mUserId��Ϊ0����˵����widget����ģ����͹㲥
        if (mUserId != 0) {
            Intent intent = new Intent();
            intent.setAction(CHANGE_POSTALCODE);
            intent.putExtra("com.cooee.weather.Weather.postalCode",
                    mCurrentPostalCode);            
            intent.putExtra("com.cooee.weather.Weather.userId",
            		mUserId);
            intent.putExtra("com.cooee.weather.Weather.skin",
            		WeatherSetting.mSkinToUpdate);
            
            sendBroadcast(intent);

			Log.v(TAG, "send broadcast: userId = " + mUserId
					+ ", postalCode = " + mCurrentPostalCode);
		}
	}
    
    //weijie_20130422
    public void SendBroadcastToLauncherByClosed() 
    {
    	try
    	{
            Intent intent = new Intent();
            intent.setAction(CLOSED_UPDATE_LAUNCHER);
            intent.putExtra("postalCode",mCurrentPostalCode);
            intent.putExtra("postalListId",mCurrentIndex);
                   
            readData();
            if(mDataEntity != null)
            {
            	intent.putExtra("T0_tempc_now",mDataEntity.getTempC());
            	intent.putExtra("T0_tempc_high",mDataEntity.getDetails().get(0).getHight());
            	intent.putExtra("T0_tempc_low",mDataEntity.getDetails().get(0).getLow());
            	intent.putExtra("T0_condition",mDataEntity.getCondition());
            	
            	intent.putExtra("T1_tempc_high",mDataEntity.getDetails().get(1).getHight());
            	intent.putExtra("T1_tempc_low",mDataEntity.getDetails().get(1).getLow());
            	intent.putExtra("T1_condition",mDataEntity.getDetails().get(1).getCondition());

            	intent.putExtra("T2_tempc_high",mDataEntity.getDetails().get(2).getHight());
            	intent.putExtra("T2_tempc_low",mDataEntity.getDetails().get(2).getLow());
            	intent.putExtra("T2_condition",mDataEntity.getDetails().get(2).getCondition());

            	intent.putExtra("T3_tempc_high",mDataEntity.getDetails().get(3).getHight());
            	intent.putExtra("T3_tempc_low",mDataEntity.getDetails().get(3).getLow());
            	intent.putExtra("T3_condition",mDataEntity.getDetails().get(3).getCondition());
            	
            	intent.putExtra("result","OK");

            	Log.e("T0_condition", "getDetails condition = "+mDataEntity.getDetails().get(0).getCondition());
            	Log.e("T0_condition", "condition = "+mDataEntity.getCondition());
            }
            else
            {
            	intent.putExtra("T0_tempc_now",0);
            	intent.putExtra("T0_tempc_high",0);
            	intent.putExtra("T0_tempc_low",0);
            	intent.putExtra("T0_condition","none");
            	
            	intent.putExtra("T1_tempc_high",0);
            	intent.putExtra("T1_tempc_low",0);
            	intent.putExtra("T1_condition","none");

            	intent.putExtra("T2_tempc_high",0);
            	intent.putExtra("T2_tempc_low",0);
            	intent.putExtra("T2_condition","none");

            	intent.putExtra("T3_tempc_high",0);
            	intent.putExtra("T3_tempc_low",0);
            	intent.putExtra("T3_condition","none");
            	
            	intent.putExtra("result","ERROR");
            }           
            sendBroadcast(intent);
    	}
    	catch(Exception ex)
        {
        	 Log.d(TAG, "SendBroadcastToLauncherByClosed exception");
        }
	}
    

	public static void setdeldefault(boolean boolvalue)
	{
		defaultcity = boolvalue;
	}
	
	public static boolean getdeldefault()
	{
		return defaultcity;
	}
	public static String convertDate(long milis) {
		String timeString;
		Date date = new Date(milis);
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyMMdd:HH:mm");
		timeString = sDateFormat.format(date);
		return timeString;
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onAttachedToWindow() {
        // 4.0���޷�ʹ�ô����
        // this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        super.onAttachedToWindow();
    }

    @Override
    public void onNewIntent(Intent intent) {
        mUserId = intent.getIntExtra("userId", 0);
        String strcity  = intent.getStringExtra("defaultcity");
        if(strcity == null){
        	defaultcity = false;
        	return;
        }
        if(!strcity.equals("none")){
        	defaultcity = true;
        }
        else{
        	defaultcity = false;
        }
        Log.v(TAG, "onNewIntent mUserId = " + mUserId);

        // ���mUserId��ȡpostalCode����ȷ����ʼ��mCurrentIndex��ֻ��onCreateʱ��һ��
        readPostalCodeByUserId();
    }

    @Override
    public void onDestroy() {
        // ����Observer����broadcast
        // ContentResolver resolver = getContentResolver();
        // resolver.unregisterContentObserver(mObserver);
        Log.v(TAG, "onDestroy");

        WeatherReceiver.setHandler(null);

        // �����widget����ģ����widget�����㲥����ĳ���
        mySendBroadcast();
        //weijie_20130422
        SendBroadcastToLauncherByClosed();
        super.onDestroy();
    }

	@Override
	public void onResume() {
		super.onResume();
		Log.v(TAG, "onResume");


		/*		
		ImageView img2 = (ImageView)findViewById(R.id.refresh_imageview);
		img2.setImageResource(R.drawable.refresh_button);			
		TextView text2 = (TextView)findViewById(R.id.refresh_textview);
		text2.setTextColor(0xffffffff);
		
		ImageView img3 = (ImageView)findViewById(R.id.menu_imageview);
		img3.setImageResource(R.drawable.menu_button);			
		TextView text3 = (TextView)findViewById(R.id.menu_textview);
		text3.setTextColor(0xffffffff);*/
		// ��ȡ���
		updateEntity();

        // ���½���
        updateViews();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");

        // �����widget����ģ����widget�����㲥����ĳ���
        mySendBroadcast();
        //weijie_20130422
        SendBroadcastToLauncherByClosed();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @SuppressLint({ "NewApi", "NewApi" })
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //20130709-liuhailin-<auto change authority>
        DATA_SERVICE_ACTION = getResources().getString(R.string.data_service_action);
      //20130709-liuhailin
        WindowManager mWm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        mWm.getDefaultDisplay().getMetrics(dm);
        
        setContentView(R.layout.app_layout);
        Log.v(TAG, "onCreate");
        
        ImageView imageview0 = (ImageView)findViewById(R.id.imgeview); 
        LayoutParams laParams0 = (LayoutParams)imageview0.getLayoutParams();
        laParams0.height=dm.heightPixels - dm.widthPixels;
        if(dm.heightPixels == 854)
        	laParams0.height += 54;
        laParams0.width = dm.widthPixels;
        //imageview0.setBottom(0);
        imageview0.setBackgroundResource(R.drawable.feature_weather_background);
        imageview0.setLayoutParams(laParams0);
        
        for(int i=0;i<4;i++){
        	LinearLayout cell = (LinearLayout)findViewById(R.id.celllayout01+i); 
            LayoutParams cellparmas = (LayoutParams)cell.getLayoutParams();
           // cellparmas.height=dm.heightPixels - dm.widthPixels;
            cellparmas.width = dm.widthPixels/4;
            cell.setLayoutParams(cellparmas);
            
            TextView textview_condition1 = (TextView)findViewById(R.id.cell_textview_week01+i); 
            if(dm.widthPixels == 800){
            	 textview_condition1.setTextSize(20);
            }     
            
            TextView textview_condition = (TextView)findViewById(R.id.textview_condition1+i); 
            if(dm.widthPixels == 800){
            	textview_condition.setTextSize(18);
            }    
            
            TextView textview_tmp1 = (TextView)findViewById(R.id.textview_tmp01+i); 
            if(dm.widthPixels == 800){
            	textview_tmp1.setTextSize(20);
           } 
            
            ImageView imageview_week = (ImageView)findViewById(R.id.imageview_week01+i); 
            if(dm.widthPixels == 800){
            	LayoutParams pimageview_week = (LayoutParams)imageview_week.getLayoutParams();
                pimageview_week.width = dm.widthPixels/4 -3;
                imageview0.setLayoutParams(laParams0);
            }else{
            	LayoutParams pimageview_week = (LayoutParams)imageview_week.getLayoutParams();
                pimageview_week.width = dm.widthPixels/4 -3;
                pimageview_week.height = pimageview_week.width- 26;
                imageview0.setLayoutParams(laParams0);
            }
            
            
        }
        
        //imageview.setLayoutParams(laParams);
        
		/*ImageView img = (ImageView)findViewById(R.id.add_imageview);
		img.setImageResource(R.drawable.add_button_bg);		
		TextView text = (TextView)findViewById(R.id.add_textview); 
		text.setTextColor(0xffffffff); */	
		
/*		LinearLayout addlayout = (LinearLayout)findViewById(R.id.add_button);		
		LinearLayout menulayout = (LinearLayout)findViewById(R.id.menu_button);		
		LinearLayout reflayout = (LinearLayout)findViewById(R.id.refresh_button);	*/
		
		mContext = this;

        // ���ö����ť��onClickListener
        setViewsOnClickListener();

        // ����Handler�����Դ���receiver���յ�����Ϣ
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WeatherObserver.MSG_REFRESH:
                        Log.v(TAG, "WeatherObserver.MSG_REFRESH");
                        readData();
                        break;
                    case WeatherReceiver.MSG_REFRESH:
                        Log.v(TAG, "WeatherReceiver.MSG_REFRESH");
                        requesting = false;
                        readData();
                        break;
                    case WeatherReceiver.MSG_AVAILABLE:
                        Log.v(TAG, "WeatherReceiver.MSG_AVAILABLE");
                        requesting = false;
                        readData();
                        break;
                    	//shanjie 20130130
    				case WeatherReceiver.MSG_WEBSERVICE_ERROR:
    					Log.v("shanjie", "Handler handleMessage WeatherReceiver.MSG_WEBSERVICE_ERROR");
    					requesting = false;
    					requestingFailed = true;
    					// 弹出提示
    					Toast.makeText(mContext, R.string.update_failed,
    							Toast.LENGTH_SHORT).show();
    					// 读取数据
    					readData();
    					// 如果没有数据，显示无数据
    					if (mDataEntity == null) {
    						requesting = false;
    						requestingFailed = true;
    						Log.v("shanjie", "updateViews");
    						updateViews();
    					}
    					requestingFailed = false;
    					break;	    
                    case WeatherReceiver.MSG_FAILED:
                        // ����ʧ��
                        Log.v(TAG, "WeatherReceiver.MSG_FAILED");
                        requesting = false;
                        requestingFailed = true;
                        // ������ʾ
                        Toast.makeText(mContext, R.string.update_failed, Toast.LENGTH_SHORT).show();
                        // ��ȡ���
                        readData();
                        // ���û����ݣ���ʾ�����
                        if (mDataEntity == null) {
                            requesting = false;
                            requestingFailed = true;
                            updateViews();
                        }
                        requestingFailed = false;
                        break;
                    case WeatherReceiver.MSG_INVILIDE:
                        requesting = false;
                        requestingFailed = true;
                        readData();
                        // ���û����ݣ���ʾ�����
                        if (mDataEntity == null) {
                            requesting = false;
                            requestingFailed = true;
                            updateViews();
                        }
                        requestingFailed = false;
                        MainActivity.city_is_faulu = false;
                        break;
                    default:
                        break;
                }

                super.handleMessage(msg);
            }
        };

        // ����Observer����broadcast
        // mObserver = new WeatherObserver(this, mHandler);
        // ContentResolver resolver = getContentResolver();
        // Uri uri = Uri.parse(WEATHER_URI);
        // Log.v(TAG, "mObserver uri = " + uri);
        // resolver.registerContentObserver(uri, true, mObserver);
        WeatherReceiver.setHandler(mHandler);

        mSettingEntity = new SettingEntity();
        mPoscalCodList = new ArrayList<String>();

        Intent intent = getIntent();
        mUserId = intent.getIntExtra("userId", 0);
        String strcity  = intent.getStringExtra("defaultcity");
        if(strcity!= null && !(strcity.equals("none"))){
        	defaultcity = true;
        }
        else{
        	defaultcity = false;
        }
        Log.v(TAG, "onCreate mUserId = " + mUserId);

        // ���mUserId��ȡpostalCode����ȷ����ʼ��mCurrentIndex��ֻ��onCreateʱ��һ��
        readPostalCodeByUserId();

        // ������ͼƬ���ô������?��
        ImageView iv = (ImageView) findViewById(R.id.weather_image_shoot);
        iv.setOnTouchListener((OnTouchListener) this);
		//addlayout.setOnTouchListener((OnTouchListener) this);
		//menulayout.setOnTouchListener((OnTouchListener) this);
		//reflayout.setOnTouchListener((OnTouchListener) this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {    	
         
    	 MenuItem setting=menu.add(0,1,0, getResources().getString(R.string.setting));
         setting.setOnMenuItemClickListener(new OnMenuItemClickListener(){
             public boolean onMenuItemClick(MenuItem item) {
            	// �������ý���
					Intent intent = new Intent(MainActivity.this,
							WeatherSetting.class);
					startActivity(intent);
                 return true;
             }
         });
         setting.setIcon(R.drawable.setting_icon);
         //setting.setHeaderIcon(R.drawable.setting_icon);
         
         MenuItem edit=menu.add(0,2,0,  getResources().getString(R.string.exit));
         edit.setOnMenuItemClickListener(new OnMenuItemClickListener(){	
             public boolean onMenuItemClick(MenuItem item) {
            	 finish();
                 return true;
             }
         });
         edit.setIcon(R.drawable.exit_icon);
        // edit.setHeaderIcon(R.drawable.setting_icon);
    
         return true; 
    
    }
	
    // ���mUserId��ȡpostalCode����ȷ����ʼ��mCurrentIndex��ֻ��onCreateʱ��һ��
    public void readPostalCodeByUserId() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = null;
        Uri uri;
        String selection;


        if (mUserId == 0) {
            mCurrentIndex = 0;
            return;
        }

        uri = Uri.parse(POSTALCODE_URI);
        selection = PostalCodeEntity.USER_ID + "=" + "'" + mUserId + "'";
        cursor = resolver.query(uri, PostalCodeEntity.projection, selection,
                null, null);
        
        // �����ȶ�ȡ�����б�
        readPostalCodeList();
        
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String post = cursor.getString(0);
                for (int i = 0; i < mPoscalCodList.size(); i++)
                    if (post.equals((String) mPoscalCodList.get(i))) {
                        mCurrentIndex = i;
                        break;
                    }
            }
            cursor.close();
        }
    }

    public void setViewsOnClickListener() {
        View v;
        v = findViewById(R.id.combo_layout);
        v.setOnClickListener(this);

		/*v = findViewById(R.id.add_button);
		v.setOnClickListener(this);

		v = findViewById(R.id.refresh_button);
		v.setOnClickListener(this);

		v = findViewById(R.id.menu_button);
		v.setOnClickListener(this);*/

		/*v = findViewById(R.id.refresh_button2);
		v.setOnClickListener(this);*/
	}

    public List<Map<String, Object>> getListData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        if (mPoscalCodList.size() == 0) {
            map = new HashMap<String, Object>();
            map.put("bg", R.drawable.popup_item_bg_normal);
            map.put("text", getResources().getString(R.string.none));
            map.put("divider", null);
            list.add(map);
        } else if (mPoscalCodList.size() == 1) {
            map = new HashMap<String, Object>();
            map.put("bg", R.drawable.popup_item_bg_single);
            map.put("text", mPoscalCodList.get(0));
            map.put("divider", null);
            list.add(map);
        } else {
            for (int i = 0; i < mPoscalCodList.size(); i++) {
                map = new HashMap<String, Object>();
                if (i == 0) {
                    map.put("bg", R.drawable.popup_item_bg_up);
                    map.put("divider", null);
                } else if (i == mPoscalCodList.size() - 1) {
                    map.put("bg", R.drawable.popup_item_bg_down);
                    map.put("divider", R.drawable.popup_item_divider);
                } else {
                    map.put("bg", R.drawable.popup_item_bg_normal);
                    map.put("divider", R.drawable.popup_item_divider);
                }
                map.put("text", mPoscalCodList.get(i));
                list.add(map);
            }
        }

        return list;
    }

    public void selectPostalCode() {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View pop_view = mLayoutInflater.inflate(R.layout.popup_window_layout,
                null, false);
        ListView listView = (ListView) pop_view.findViewById(R.id.listview);

		listView.setAdapter(new SimpleAdapter(this, getListData(),
				R.layout.popup_window_item_layout, new String[] { "bg", "text",
						"divider" }, new int[] { R.id.listitem_bg,
						R.id.listitem_text, R.id.listitem_divider }));
		listView.setDivider(null);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// �˳�popup
				if (mPop != null) {
					mPop.dismiss();
				}
				if (mPoscalCodList.size() > 0) {
					mCurrentIndex = position;
					mCurrentPostalCode = mPoscalCodList.get(position);
					changePostalCode();
				}
			}
        	
		});

		// ����popup window
		int width = (int) getResources().getDimension(
				R.dimen.popup_window_width);
		int height = (int) getResources().getDimension(
				R.dimen.popup_item_height) + 2;
		if (mPoscalCodList.size() > 0) {
			if (mPoscalCodList.size() > POSTALCODE_LIST_COUNT)
				height = height * POSTALCODE_LIST_COUNT;
			else
				height = height * mPoscalCodList.size();
		}
		mPop = new PopupWindow(pop_view, width, height);

        // mPop.setAnimationStyle(R.style.popwindow);
        mPop.setFocusable(true); // ����PopupWindow�ɻ�ý���
        mPop.setTouchable(true); // ����PopupWindow�ɴ���
        mPop.setOutsideTouchable(true); // ���÷�PopupWindow����ɴ���
        ShapeDrawable mShapeDrawable = new ShapeDrawable(new OvalShape());
        mShapeDrawable.getPaint().setColor(0x00000000);
        mShapeDrawable.setBounds(0, 0, width, height);
        mPop.setBackgroundDrawable(mShapeDrawable); // �����ñ����޷������˳�
        mPop.showAsDropDown(findViewById(R.id.combo_layout), 0, 8);
    }

    public void updateViews() {
        TextView tv;
        ImageView iv;
        try {

            if (mPoscalCodList == null)
                mPoscalCodList = new ArrayList<String>();
            if (mPoscalCodList.size() == 0) {
                TextView textView = (TextView) findViewById(R.id.cur_postalCode);
                textView.setText(R.string.na);
                mCurrentPostalCode = "none";
            } else {
                TextView textView = (TextView) findViewById(R.id.cur_postalCode);
                mCurrentPostalCode = mPoscalCodList.get(mCurrentIndex);
                textView.setText(mCurrentPostalCode);
            }

            TextView textView = (TextView) findViewById(R.id.textview_page);
            if (mPoscalCodList.size() > 0) {
                textView.setText((mCurrentIndex + 1) + "/" + mPoscalCodList.size());
            } else {
                textView.setText(R.string.na);
            }

            // ���û��ݣ����������
            if (!mCurrentPostalCode.equals("none")) {
                if (mDataEntity == null) {
                    if (requestingFailed == false) {
                        requestData();
                    }
                }
            } else{ //weather 数据重新读取20130121
            	readData();
            }

            // ���û��ݣ�����ʾ�����
            if (mDataEntity == null) {
                findViewById(R.id.nodata_layout).setVisibility(View.VISIBLE);

                if (requesting && !requestingFailed) { // ������ʧ�ܣ���ʾ�����
                    // ������
                    findViewById(R.id.updating_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.no_data_layout).setVisibility(View.INVISIBLE);
                } else {
                    // �����
                    findViewById(R.id.updating_layout).setVisibility(View.INVISIBLE);
                    findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
                    if (mCurrentPostalCode.equals("none"))
                    {
                        ((TextView) findViewById(R.id.nodate)).setText(R.string.please_add_city);
                        ((TextView) findViewById(R.id.pleaseupdate)).setVisibility(View.INVISIBLE);
                        
                        if(true)
                        {
                        	Intent intent = new Intent();
                            intent.setClassName(this,
                                    "com.cooee.app.cooeeweather.view.WeatherAddPost");
                            
                            intent.putExtra("citys", "0");//��������Ļ������Բ��Ӵ��д���
                            this.startActivityForResult(intent, CONTEXT_RESTRICTED);//CONTEXT_RESTRICTED int�ͱ��������Զ���
                           // startActivity(intent); 
                        }
                                               
                    }
                    else
                    {
                        if (!city_is_faulu)// sxd ���ƺ��ױ���������bug
                        {
                            ((TextView) findViewById(R.id.nodate)).setText(R.string.update_fault);
                            ((TextView) findViewById(R.id.pleaseupdate))
                                    .setVisibility(View.INVISIBLE);
                            city_is_faulu = true;
                        }
                        else
                        {
                            ((TextView) findViewById(R.id.nodate)).setText(R.string.no_data);
                            ((TextView) findViewById(R.id.pleaseupdate))
                                    .setVisibility(View.VISIBLE);
                        }
                    }
                }

                return;
            }

            findViewById(R.id.nodata_layout).setVisibility(View.INVISIBLE);

            // ��ǰ����
            tv = (TextView) findViewById(R.id.textview_now_tmp);
            tv.setText(mDataEntity.getTempC().toString());

            // ����������������
            tv = (TextView) findViewById(R.id.textview_totay_high_tmp);
            tv.setText(mDataEntity.getDetails().get(0).getHight().toString() + "℃");
            tv = (TextView) findViewById(R.id.textview_totay_low_tmp);
            tv.setText(mDataEntity.getDetails().get(0).getLow().toString() + "℃");

            // ��ǰ����
            /*
             * tv = (TextView) findViewById(R.id.textview_condition);
             * tv.setText(mDataEntity.getCondition());
             * WeatherVideoShoot.loadScr(mDataEntity.getCondition()); if
             * (WeatherVideoShoot.videoPlayFlag == videoFlag.VIDEO_PLAY_SUCCESS)
             * { vp.setVisibility(View.VISIBLE); } else
             */{
                // if video can not play,display image
                iv = (ImageView) findViewById(R.id.weather_image_shoot);
                // ͼƬ̫��Ҫ����һ��
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 2;// ͼƬ��߶�Ϊԭ���Ķ���֮һ����ͼƬΪԭ�����ķ�֮һ
                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                        WeatherConditionImage.getFullConditionImage(mDataEntity
                                .getCondition()), opts);
                iv.setImageBitmap(bitmap);
                iv.setVisibility(View.VISIBLE);
            }

        String[] weekdayArry = getResources().getStringArray(R.array.weekday);
		// ��һ��
		tv = (TextView) findViewById(R.id.cell_textview_week01);
		tv.setText(weekdayArry[mDataEntity.getDetails().get(0).getDayOfWeek()]);
		tv = (TextView) findViewById(R.id.textview_tmp01);
		tv.setText(mDataEntity.getDetails().get(0).getHight() + "℃" + "/"
				+ mDataEntity.getDetails().get(0).getLow() + "℃");
		iv = (ImageView) findViewById(R.id.imageview_week01);
		//shanjie 20130130
		String condition;
		if(false)
		{
			condition = mDataEntity.getDetails().get(0).getCondition();
			String[] str1 = condition.split("转");
			iv.setImageResource(WeatherConditionImage
					.getConditionImage(str1[0]));
			tv = (TextView) findViewById(R.id.textview_condition1);
			tv.setText(str1[0]);
		}
		else
		{
			iv.setImageResource(WeatherConditionImage
					.getConditionImage(mDataEntity.getDetails().get(0)
							.getCondition()));
			tv = (TextView) findViewById(R.id.textview_condition1);
			condition = mDataEntity.getDetails().get(0).getCondition();
			String[] str1 = condition.split("转");
			tv.setText(str1[0]);
		}

		// �ڶ���
		tv = (TextView) findViewById(R.id.cell_textview_week02);	
		tv.setText(weekdayArry[mDataEntity.getDetails().get(1).getDayOfWeek()]);
		tv = (TextView) findViewById(R.id.textview_tmp02);
		tv.setText(mDataEntity.getDetails().get(1).getHight() + "℃" + "/"
				+ mDataEntity.getDetails().get(1).getLow() + "℃");
		iv = (ImageView) findViewById(R.id.imageview_week02);
		iv.setImageResource(WeatherConditionImage.getConditionImage(mDataEntity
				.getDetails().get(1).getCondition()));
		tv = (TextView) findViewById(R.id.textview_condition2);
		condition = mDataEntity.getDetails().get(1).getCondition();
		String[] str2 = condition.split("转"); 
		tv.setText(str2[0]);
		// ������
		tv = (TextView) findViewById(R.id.cell_textview_week03);
		tv.setText(weekdayArry[mDataEntity.getDetails().get(2).getDayOfWeek()]);
		tv = (TextView) findViewById(R.id.textview_tmp03);
		tv.setText(mDataEntity.getDetails().get(2).getHight() + "℃" + "/"
				+ mDataEntity.getDetails().get(2).getLow() + "℃");
		iv = (ImageView) findViewById(R.id.imageview_week03);
		iv.setImageResource(WeatherConditionImage.getConditionImage(mDataEntity
				.getDetails().get(2).getCondition()));
		tv = (TextView) findViewById(R.id.textview_condition3);
		condition = mDataEntity.getDetails().get(2).getCondition();
		String[] str3 = condition.split("转"); 
		tv.setText(str3[0]);

		// ������
		tv = (TextView) findViewById(R.id.cell_textview_week04);
		tv.setText(weekdayArry[mDataEntity.getDetails().get(3).getDayOfWeek()]);
		tv = (TextView) findViewById(R.id.textview_tmp04);
		tv.setText(mDataEntity.getDetails().get(3).getHight() + "℃" + "/"
				+ mDataEntity.getDetails().get(3).getLow() + "℃");
		iv = (ImageView) findViewById(R.id.imageview_week04);
		iv.setImageResource(WeatherConditionImage.getConditionImage(mDataEntity
				.getDetails().get(3).getCondition()));
		tv = (TextView) findViewById(R.id.textview_condition4);
		condition = mDataEntity.getDetails().get(3).getCondition();
		/*if (condition.length() > 6) {
			condition = condition.substring(0, 6);
		}*/
		String[] str4 = condition.split("转"); 
		tv.setText(str4[0]);

            // ��ʾ����ʱ��
            tv = (TextView) findViewById(R.id.textview_time);
            long milis = mDataEntity.getLastUpdateTime();
            // updateTime = "" + DateFormat.format("yy/MM/dd/hh:mm:ss", milis);
            // updateTime = "" + DateFormat.format("HH:mm", milis);
            // HHΪ24Сʱ�ƣ�hhΪ12Сʱ��
            String updateTime;
            Date date = new Date(milis);
            SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm");
            updateTime = sDateFormat.format(date);
            Log.v(TAG, "milis = " + milis + ", updateTime = " + updateTime);
            // print now & milis
            if (true) {
                String nowTime = convertDate(System.currentTimeMillis());
                String milisTime = convertDate(milis);
			Log.v(TAG, "now = " + nowTime + ", milis = " 
                        + milisTime);
            }
            long diff = System.currentTimeMillis() - milis;

            if (mContext == null)
                return;

            String str = mContext.getResources().getString(
					R.string.lasttime);
            if (diff < 0)
			tv.setText(mContext.getResources().getString(
					R.string.systemtimeerror));
            else if (diff < 24 * 60 * 60 * 1000) // 24Сʱ����
                tv.setText(updateTime+str);
            else if (diff < 48 * 60 * 60 * 1000) // 48Сʱ������ʾ����
                tv.setText(mContext.getResources().getString(R.string.yestoday)+str);
            else if (diff < 72 * 60 * 60 * 1000) // 72Сʱ������ʾǰ��
			tv.setText(mContext.getResources().getString(
					R.string.thedaybeforeyestoday)+str);
		else
			tv.setText(mContext.getResources().getString(R.string.afewdaysago));
        } catch (Exception e)
        {
            Log.e(TAG, "com.cooee.widget.weither ERROR");
            e.printStackTrace();
        }
    }

    public void setTestData() {
        mDataEntity = new weatherdataentity();
        mDataEntity.setTestData();
    }

    public void requestData() {
        if (!requesting) {
            Intent intent = new Intent(DATA_SERVICE_ACTION);
            intent.putExtra("postalCode", mCurrentPostalCode);
            intent.putExtra("forcedUpdate", 1); // ǿ�Ƹ���
            startService(intent);

            requesting = true;
        }
    }

    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
    {  
        //���Ը�ݶ���������������Ӧ�Ĳ���  
        if((0==resultCode) && ((mPoscalCodList==null) || (mPoscalCodList.size()==0)))  
        {  
        	finish();
        }  
        else{
        	if(0!=resultCode){
	        	Bundle bunde = data.getExtras();    
	            String cityname = bunde.getString("citys");            
	            updateEntity();
	            
	        	for(int i=0; i<mPoscalCodList.size(); i++){
	        		if(mPoscalCodList.get(i).equals(cityname))
	        		{
	        			mCurrentIndex = i;
	        			mCurrentPostalCode = cityname;
	        			break;
	        		}
	        	}
	    		updateViews();
        	}
			//changePostalCode();
        }
    }  
    
    public void readData() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = null;
        Uri uri;
        String selection;

        uri = Uri.parse(WEATHER_URI + "/" + mCurrentPostalCode);
        selection = weatherdataentity.POSTALCODE + "=" + "'"
                + mCurrentPostalCode + "'";
        cursor = resolver.query(uri, weatherdataentity.projection, selection,
                null, null);

        if (cursor != null) {
            mDataEntity = new weatherdataentity();

            if (cursor.moveToFirst()) {
                mDataEntity.setUpdateMilis(cursor.getInt(0));
                mDataEntity.setCity(cursor.getString(1));
                mDataEntity.setPostalCode(cursor.getString(2));
                mDataEntity.setForecastDate(cursor.getLong(3));
                mDataEntity.setCondition(cursor.getString(4));
                mDataEntity.setTempF(cursor.getInt(5));
                mDataEntity.setTempC(cursor.getInt(6));
                mDataEntity.setHumidity(cursor.getString(7));
                mDataEntity.setIcon(cursor.getString(8));
                mDataEntity.setWindCondition(cursor.getString(9));
                mDataEntity.setLastUpdateTime(cursor.getLong(10));
                mDataEntity.setIsConfigured(cursor.getInt(11));
            }

            int count = 0;
            while (cursor.moveToNext()) {
                Log.v(TAG, "updateMilis[" + count + "] = " + cursor.getInt(0));
                Log.v(TAG, "city[" + count + "] = " + cursor.getString(1));
                Log.v(TAG,
                        "postcalCode[" + count + "] = " + cursor.getString(2));
                count++;
            }

            cursor.close();
        }

        int details_count = 0;
        if (mDataEntity != null) {
            uri = Uri.parse(WEATHER_URI + "/" + mCurrentPostalCode + "/detail");
            selection = weatherforecastentity.CITY + "=" + "'"
                    + mCurrentPostalCode + "'";
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

                    mDataEntity.getDetails().add(forecast);

                    details_count = details_count + 1;
                }
                cursor.close();
            }
        }

        Log.v(TAG, "details_count = " + details_count);
        if (details_count < 4) {
            mDataEntity = null;
        }

        // 读完数据重新显示
        if (mDataEntity != null) {
            updateViews();
        }
    }

    public void readPostalCodeList() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = null;
        Uri uri = Uri.parse(POSTALCODE_URI);

        // �����mPoscalCodList
        mPoscalCodList.clear();

        String selection;
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

                    mPoscalCodList.add(mPostalCodeEntity.getPostalCode());
                    Log.v(TAG, "mPostalCodeEntity.getPostalCode() = "
                            + mPostalCodeEntity.getPostalCode());
                } while (cursor.moveToNext());
            }  
            cursor.close();
        }
        
       //��ʼ����ӱ�����ΪĬ�ϳ���
	//		if(defaultcity){
	//			boolean f = true;
	//			for(int i = 0; i<mPoscalCodList.size(); i++){
	//				String a = mPoscalCodList.get(i);					
//					if(a.equals(getResources().getString(R.string.defaultcity)));
	//				f = false;
	//				break;
	//			}
	//			if(f){ 
	//			ContentValues values = new ContentValues();
	//            values.put(PostalCodeEntity.POSTAL_CODE, getResources().getString(R.string.defaultcity));
	 //           values.put(PostalCodeEntity.USER_ID, 0); // 0ΪĬ��userId
	 //           resolver.insert(uri, values);
	//			}
	            //mPoscalCodList.addALL(getResources().getString(R.string.defaultcity));
	            //mPoscalCodList.addAll(getResources().getString(R.string.defaultcity));
	//		}
        
    }

    public void setCurrentPostalCode() {
        // ���mCurrentIndex��mCurrentPostalCode
        if (mPoscalCodList.size() == 0) {
            mCurrentIndex = 0;
            mCurrentPostalCode = "none";
        } else if (mCurrentIndex > mPoscalCodList.size() - 1) { // ���mCurrentIndex�ǲ��Ǳ�ɾ��
            // ���ó����һ��
            mCurrentIndex = mPoscalCodList.size() - 1;
            mCurrentPostalCode = mPoscalCodList.get(mCurrentIndex);
        } else {
            // ��һ�ν���ʱ��Ĭ��Ϊ��mCurrentIndex��
            mCurrentPostalCode = mPoscalCodList.get(mCurrentIndex);
        }
    }

    /**
     * ������CurrentPostalCode��CurrentIndex���ٵ��ô˺������ػ���Ļ
     */
    public void changePostalCode() {
        mDataEntity = null;

        // ���û�������������£���ֱ�Ӷ�ȡ���
        if (mSettingEntity.getUpdateWhenOpen() != 1) {
            readData();
        }

        updateViews();
    }

    // �ֶ�����
    public void refreshData() {
        mDataEntity = null;

        updateViews();
    }

    public void updateEntity() {
        // ��ȡ����
        readSetting();

        // ��ȡ�����б�
        readPostalCodeList();

        // ����mCurrentIndex��mCurrentPostalCode
        setCurrentPostalCode();

        // setTestData();

        // ���û�������������£���ֱ�Ӷ�ȡ���
        if (mSettingEntity.getUpdateWhenOpen() != 1) {
            readData();
        }
    }

	public List<Map<String, Object>> getMenuListData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		final int string_id[] = { R.string.edit_list, R.string.setting };

        for (int i = 0; i < string_id.length; i++) {
            map = new HashMap<String, Object>();
            if (i == 0) {
                map.put("bg", R.drawable.popup_item_bg_up);
                map.put("divider", null);
            } else if (i == string_id.length - 1) {
                map.put("bg", R.drawable.popup_item_bg_down);
                map.put("divider", R.drawable.popup_item_divider);
            } else {
                map.put("bg", R.drawable.popup_item_bg_normal);
                map.put("divider", R.drawable.popup_item_divider);
            }
            map.put("text", getResources().getString(string_id[i]));
            list.add(map);
        }

        return list;
    }

    public void launcherSettingMenu() {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View pop_view = mLayoutInflater.inflate(R.layout.popup_window_layout,
                null, false);
        ListView listView = (ListView) pop_view.findViewById(R.id.listview);

		listView.setAdapter(new SimpleAdapter(this, getMenuListData(),
				R.layout.popup_window_item_layout, new String[] { "bg", "text",
						"divider" }, new int[] { R.id.listitem_bg,
						R.id.listitem_text, R.id.listitem_divider }));
		listView.setDivider(null);

		// ��ʺ��ɫ�ĸ���������
		listView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
			}

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // �˳�popup
                if (mPop != null) {
                    mPop.dismiss();
                }
                switch (position) {
                    case 0: {
                        // ������б༭����
                        Intent intent = new Intent(MainActivity.this,
                                WeatherEditPost.class);
                        startActivity(intent);
                        break;
                    }
                    case 1: {
                        // �������ý���
                        Intent intent = new Intent(MainActivity.this,
                                WeatherSetting.class);
                        startActivity(intent);
                        break;
                    }
                    default:
                        break;
                }
            }
        });

        // ����popup window
        int width = (int) getResources().getDimension(
                R.dimen.popup_window_width);
        int height = (int) getResources().getDimension(
                R.dimen.popup_item_height) + 2;
        // ��ʱд������Ϊ2
        height = height * 2;
        mPop = new PopupWindow(pop_view, width, height);

        // mPop.setAnimationStyle(R.style.popwindow);
        mPop.setFocusable(true); // ����PopupWindow�ɻ�ý���
        mPop.setTouchable(true); // ����PopupWindow�ɴ���
        mPop.setOutsideTouchable(true); // ���÷�PopupWindow����ɴ���
        ShapeDrawable mShapeDrawable = new ShapeDrawable(new OvalShape());
        mShapeDrawable.getPaint().setColor(0x00000000);
        mShapeDrawable.setBounds(0, 0, width, height);
        mPop.setBackgroundDrawable(mShapeDrawable); // �����ñ����޷������˳�
        mPop.showAsDropDown(findViewById(R.id.combo_layout), 0, 8);
        //mPop.showAsDropDown(findViewById(R.id.menu_button), -184 + 30, 8);
    }

	@Override
	public void onClick(View v) {
		if (v == findViewById(R.id.combo_layout)) {
			selectPostalCode();
		} 
		 else if ((v == findViewById(R.id.button_citymange))) {		
			Intent intent = new Intent(this, WeatherEditPost.class);
			startActivityForResult(intent, CONTEXT_RESTRICTED);//CONTEXT_RESTRICTED int�ͱ��������Զ���
			//startActivity(intent);
		} else if ( v == findViewById(R.id.button_refresh)){	
			refreshData();
		} 
	}

    public void readSetting() {
        // ��ȡ����
        ContentResolver resolver = getContentResolver();
        Cursor cursor = null;
        boolean found = false;
        Uri uri = Uri.parse(MainActivity.SETTING_URI);

        Log.v(TAG, "readSetting uri = " + uri);

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

    public void saveSetting() {
        // ��������
        ContentResolver resolver = getContentResolver();
        Cursor cursor = null;
        boolean found = false;
        Uri uri = Uri.parse(MainActivity.SETTING_URI);

        cursor = resolver
                .query(uri, SettingEntity.projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                found = true;
            }
        }

        if (found) {
            ContentValues values = new ContentValues();
            values.put(SettingEntity.UPDATE_WHEN_OPEN,
                    mSettingEntity.getUpdateWhenOpen());
            values.put(SettingEntity.UPDATE_REGULARLY,
                    mSettingEntity.getUpdateRegularly());
            values.put(SettingEntity.UPDATE_INTERVAL,
                    mSettingEntity.getUpdateInterval());
            values.put(SettingEntity.SOUND_ENABLE,
                    mSettingEntity.getSoundEnable());

            int updateRows;
            updateRows = resolver.update(uri, values, null, null);
            Log.v(TAG, "update setting rows = " + updateRows);
        } else {
            throw new UnsupportedOperationException();
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == findViewById(R.id.weather_image_shoot)) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mInitialY = event.getY();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                Float deltaY = mInitialY - event.getY();
                if (deltaY < -SLIDE_SENSITIVITY) {
                    // ���ϻ������Ƶ���һҳ
                    if (mPoscalCodList.size() > 1) {
                        mCurrentIndex = mCurrentIndex + 1;
                        if (mCurrentIndex == mPoscalCodList.size()) {
                            mCurrentIndex = 0;
                        }
                        mCurrentPostalCode = mPoscalCodList.get(mCurrentIndex);
                        changePostalCode();
                    }
                } else if (deltaY > SLIDE_SENSITIVITY) {
                    // ���»������Ƶ���һҳ
                    if (mPoscalCodList.size() > 1) {
                        mCurrentIndex = mCurrentIndex - 1;
                        if (mCurrentIndex == -1) {
                            mCurrentIndex = mPoscalCodList.size() - 1;
                        }
                        mCurrentPostalCode = mPoscalCodList.get(mCurrentIndex);
                        changePostalCode();
                    }
                }
            }
        }

		return true;
	}
}
