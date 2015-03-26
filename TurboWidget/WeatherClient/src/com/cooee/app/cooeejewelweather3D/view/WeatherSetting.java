
package com.cooee.app.cooeejewelweather3D.view;



//import com.cooee.widget.samweatherclock.MainActivity;
//import com.cooee.widget.samweatherclock.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.cooee.app.cooeejewelweather3D.dataentity.SettingEntity;
import com.cooee.app.cooeejewelweather3D.filehelp.Log;
import com.cooeeui.weatherclient.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class WeatherSetting extends Activity {
    private static int UpdateWhenStart = 1;
    private static int TimingSync = 0;
    private static int TimeToUpdate = 1;
    private static int SoundEffect = 0;
    private static final String TAG = "com.cooee.weather.WeatherEditPost";
    private final String SETTING_URI = "content://com.cooee.app.cooeejewelweather3D.dataprovider/setting";
    WeatherOptionAdapter mAdapter;
    private SettingEntity mSettingEntity;
    private int mTimeToUpdateIndex = 0;

    
    private float textsize1 = 16.0f;
    private float textsize2 = 14.0f;
    
    private int TimeToUpdateTable[] = {
            6*60*1000*60,
            12*60*1000*60,
            24*60*1000*60
    };

    public enum SKINTABLE {  
    	CLASSIC_STYLE, FASHION_STYLE
  	}
  public static int mSkinToUpdate = 0;
  
    private void popSettingTime(){
    	/*new AlertDialog.Builder(this)
		.setTitle(getResources().getString(R.string.liststr5))
		.setMultiChoiceItems(R.array.updatetimesetting,null, null*/ 
    	
    	new AlertDialog.Builder(this)  
    	.setTitle(getResources().getString(R.string.liststr5))                
    	.setSingleChoiceItems(R.array.updatetimesetting, mTimeToUpdateIndex,   
    	  new DialogInterface.OnClickListener() {      	                              
    	     public void onClick(DialogInterface dialog, int which) { 
    	    	 mTimeToUpdateIndex = which;
    	    	 String[] list = getResources().getStringArray(
							R.array.updatetimesetting);
                 TimeToUpdate = TimeToUpdateTable[mTimeToUpdateIndex];
                 WeatherOptions s = mAdapter.getItem(2);
                 s.isTickoff = TimingSync;
                 s.description = list[which];
                 mAdapter.notifyDataSetChanged();
                 saveSetting(); 
    	        dialog.dismiss();  
    	     }  
    	  }  
    	).show();
    	
    }
    private void popSettingSkin(){
    	/*new AlertDialog.Builder(this)
		.setTitle(getResources().getString(R.string.liststr5))
		.setMultiChoiceItems(R.array.updatetimesetting,null, null*/ 
    	
    	new AlertDialog.Builder(this)  
    	.setTitle(getResources().getString(R.string.setSkinTitle))                
    	.setSingleChoiceItems(R.array.updateSkinsetting, mSkinToUpdate,   
    	  new DialogInterface.OnClickListener() {      	                              
    	     public void onClick(DialogInterface dialog, int which) { 
    	    	 //mTimeToUpdateIndex = which;
    	    	 String[] list = getResources().getStringArray(
							R.array.updateSkinsetting);
    	    	 
    	    	 mSkinToUpdate = which;
    	    	 
                 WeatherOptions s = mAdapter.getItem(3);
                 s.isTickoff = TimingSync;
                 s.description = list[which];
                 mAdapter.notifyDataSetChanged();
                // saveSetting();
                 	//发送广播通知所有widget改变w
    	        dialog.dismiss();  
    	     }  
    	  }  
    	).show();
    	
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        mSettingEntity = new SettingEntity();
        readSetting();
        initData();

   //     textsize1 = this.getResources().getDimension(R.dimen.setting_textview_textsize1);
    //    textsize2 = this.getResources().getDimension(R.dimen.setting_textview_textsize2);
        
        final ListView list = (ListView) findViewById(R.id.setting_list);

        final WeatherOptionAdapter adapter = new WeatherOptionAdapter(this/*,textsize1,textsize2*/);
        mAdapter = adapter;
        
        for (mTimeToUpdateIndex= 0; mTimeToUpdateIndex < TimeToUpdateTable.length; mTimeToUpdateIndex++) {
            if (TimeToUpdateTable[mTimeToUpdateIndex] == TimeToUpdate)
                break;
        }
        if (mTimeToUpdateIndex == TimeToUpdateTable.length) {
            mTimeToUpdateIndex = 0;
            TimeToUpdate = TimeToUpdateTable[mTimeToUpdateIndex];
        }
        adapter.add(new WeatherOptions(getString(R.string.liststr1), getString(R.string.liststr2),
                1, UpdateWhenStart));
        adapter.add(new WeatherOptions(getString(R.string.liststr3), getString(R.string.liststr4),
                1, TimingSync));
        String[] listTime = getResources().getStringArray(R.array.updatetimesetting);
        adapter.add(new WeatherOptions(getString(R.string.liststr5), listTime[mTimeToUpdateIndex], 0, 0));
        
    //    list.setBackgroundColor(0xffffffff);
        
        //康佳定制
        /*String[] listSkin = getResources().getStringArray(R.array.updateSkinsetting);
        adapter.add(new WeatherOptions(getString(R.string.liststr7), listSkin[mSkinToUpdate], 0, 0));*/
        
        // ????????,????
        // adapter.add(new WeatherOptions(getString(R.string.liststr12),
        // getString(R.string.liststr13), 1, SoundEffect));

        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                WeatherOptions s;
                switch (position) {
                    case 0:
                        if (UpdateWhenStart == 1) {
                            UpdateWhenStart = 0;
                        }
                        else {
                            UpdateWhenStart = 1;
                        }
                        s = adapter.getItem(0);
                        s.isTickoff = UpdateWhenStart;
                        adapter.notifyDataSetChanged();
                        break;
                    case 1:
                        if (TimingSync == 1) {
                            TimingSync = 0;
                            s = mAdapter.getItem(1);
                            s.isTickoff = TimingSync;
                            s.description = getString(R.string.liststr14);
                            mAdapter.notifyDataSetChanged();
                        }
                        else {
                            s = mAdapter.getItem(1);
                            TimingSync = 1;
                            s.isTickoff = TimingSync;
                            s.description = getString(R.string.liststr4);
                            mAdapter.notifyDataSetChanged();                        
                            //popupWarning();
                        }
                        break;
                    case 2:
                        if (TimingSync == 1) {
                            //popupList(ctx, list);
                            popSettingTime();
                        }

                        break;
                    case 3:
                            popSettingSkin();

                        break;
                    case 4:
                        if (SoundEffect == 1) {
                            SoundEffect = 0;
                        }
                        else {
                            SoundEffect = 1;
                        }
                        s = adapter.getItem(3);
                        s.isTickoff = SoundEffect;
                        adapter.notifyDataSetChanged();
                        break;
                }
                saveSetting();
                Log.d("xujihao", "position is" + position);
            }
        });
        adapter.notifyDataSetChanged();//
        list.setAdapter(adapter);
    }

    void popupWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("?????");
        // builder.setCancelable(false);
        builder.setPositiveButton("?", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        })
                .setNegativeButton("?", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        WeatherOptions s = mAdapter.getItem(1);
                        TimingSync = 1;
                        s.isTickoff = TimingSync;
                        s.description = getString(R.string.liststr4);
                        mAdapter.notifyDataSetChanged();
                    }
                });

        AlertDialog alertdialog = builder.create();
        alertdialog.show();
    }


    public void initData() {
        UpdateWhenStart = mSettingEntity.getUpdateWhenOpen();
        TimingSync = mSettingEntity.getUpdateRegularly();
        TimeToUpdate = mSettingEntity.getUpdateInterval();
        SoundEffect = mSettingEntity.getSoundEnable();
    }

    public void readSetting() {
        // 先在数据库中搜索
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

        // 如果没找到则添加
        if (!found) {
            ContentValues values = new ContentValues();
            values.put(SettingEntity.UPDATE_WHEN_OPEN, 1);
            values.put(SettingEntity.UPDATE_REGULARLY, 1);
            values.put(SettingEntity.UPDATE_INTERVAL, 0);
            values.put(SettingEntity.SOUND_ENABLE, 0);
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
        // 先在数据库中搜索
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

        if (found) {
            ContentValues values = new ContentValues();
            values.put(SettingEntity.UPDATE_WHEN_OPEN,
                    UpdateWhenStart);
            values.put(SettingEntity.UPDATE_REGULARLY,
                    TimingSync);
            values.put(SettingEntity.UPDATE_INTERVAL,
                    TimeToUpdate);
            values.put(SettingEntity.SOUND_ENABLE,
                    SoundEffect);

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
}

class WeatherOptionAdapter extends ArrayAdapter<WeatherOptions> {
    LayoutInflater inflator;
//    private float textsize1;
 //   private float textsize2;
    public WeatherOptionAdapter(Context context/*,float s1,float s2*/) {
        super(context, 0);
        inflator = LayoutInflater.from(context);
    //    textsize1 = s1;
    //    textsize2 = s2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflator.inflate(
                    R.layout.list_item, parent, false);
        }

        TextView text1 = (TextView) convertView
                .findViewById(R.id.text1);
        TextView text2 = (TextView) convertView
                .findViewById(R.id.text2);
        
  //      text1.setTextSize(textsize1);
  //      text2.setTextSize(textsize2);
        ImageView image = (ImageView) convertView
                .findViewById(R.id.tickoff);
        WeatherOptions s = this.getItem(position);
        if (position == 2) {
            WeatherOptions s2 = this.getItem(1);
            if (s2.isTickoff == 0) {
                text1.setTextColor(0xffbbbbbb);
                text2.setTextColor(0xffbbbbbb);
            }
            else {
                text1.setTextColor(0xff101010);
                text2.setTextColor(0xff505050);
            }
        }
        else{
            text1.setTextColor(0xff101010);
            text2.setTextColor(0xff505050);
        }// ��һ������һ�����ƣ������һ��ѡ������һ������

        text1.setText(s.option);
        text2.setText(s.description);
        if (s.isShowTickoff == 1) {
            if (s.isTickoff == 1) {
                image.setImageResource(R.drawable.tickoff_select);
            }
            else {
                image.setImageResource(R.drawable.tickoff);
            }
        }
        else{
        	image.setImageResource(0);
        }
        return convertView;
    }
}

class WeatherOptions {
    String option;
    String description;
    int isShowTickoff;
    int isTickoff;

    public WeatherOptions(String option, String description, int isShowTickoff, int isTickoff) {
        this.option = option;
        this.description = description;
        this.isShowTickoff = isShowTickoff;
        this.isTickoff = isTickoff;
    }
}

class PopupListAdapter extends ArrayAdapter<PopupListItem> {
    LayoutInflater inflator;

    public PopupListAdapter(Context context) {
        super(context, 0);
        inflator = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflator.inflate(
                    R.layout.popup_list_item_layout, parent, false);
        }

        TextView text = (TextView) convertView
                .findViewById(R.id.text1);
        ImageView image = (ImageView) convertView
                .findViewById(R.id.radiobutton);
        PopupListItem s = this.getItem(position);
        text.setText(s.str);
        if (s.isTickoff == 1) {
            image.setImageResource(R.drawable.option_select);
        }
        else {
            image.setImageResource(R.drawable.option);
        }

        return convertView;
    }
}

class PopupListItem {
    String str;
    int isTickoff;

    public PopupListItem(String str, int isTickoff) {
        this.str = str;
        this.isTickoff = isTickoff;
    }
}
