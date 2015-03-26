
package com.cooee.app.cooeeweather.view;


import com.cooee.app.cooeeweather.dataentity.PostalCodeEntity;
import com.cooee.app.cooeeweather.dataentity.SinaCitysEntity;
import com.cooee.app.cooeeweather.dataprovider.weatherdataprovider;
import com.cooee.widget.samweatherclock.MainActivity;
import com.cooee.widget.samweatherclock.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;




public class WeatherAddPost extends Activity implements OnItemClickListener {

    private enum StringType {
        LATIN,
        HANZI,
        MIX
    };

    private static final String TAG = "com.cooee.weather.WeatherAddPost";
  //20130709-liuhailin-<auto change authority>
    //private final String CITY_CONTENT_URI = "content://com.cooee.app.cooeeweather.dataprovider/citys";
    private final String CITY_CONTENT_URI = "content://"+ weatherdataprovider.AUTHORITY +"/citys";
  //20130709-liuhailin
    private LayoutInflater mInflater;
    
    private LinearLayout mpopcityLinear; 
    private String[] popcitys = {"北京", "上海", "广州", "深圳", "武汉", "南京", "杭州", "西安", "郑州", "成都", "东莞", "沈阳", "天津",
    		"哈尔滨", "长沙", "福州", "石家庄", "苏州", "重庆", "无锡", "济南", "大连", "佛山", "厦门", "南昌",
    		"太原", "长春", "合肥", "兰州", "青岛", "汕头", "昆明", "南宁"};
    
    private EditText mEditText;
    private ListView mListView;
    private MyAdapter mListAdapter;
    private Cursor mCursor = null;
    private StringType mEditContentType = StringType.LATIN; // 输入框中内容的类型
    //private List<Map<String, Object>> mListData = new ArrayList<Map<String, Object>>();;

    private boolean found = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post_layout);

        mEditText = (EditText) findViewById(R.id.add_post_text);
        // mEditText.setFocusable(true);
        // mEditText.setFocusableInTouchMode(true);
        mEditText.clearFocus();
        //mEditText.requestFocus();
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
        // 设置edit的图片
        mEditText.setBackgroundResource(R.drawable.editbox_background_normal);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v(TAG, "s = " + s + ", start = " + start + ", before = " + before
                        + ", count = " + count);
                String str = "" + s;
                if (mCursor != null) {
                    mCursor.close();
                    mCursor = null;
                }

                // 鍘婚櫎鍓嶅悗绌烘牸
                str = str.trim();

                // 鍒ゆ柇涓虹函姹夊瓧杩樻槸鎷奸煶
                int bl = str.getBytes().length;
                int l = str.length();
                if (bl == l * 3) {
                    mEditContentType = StringType.HANZI;
                } else if (bl == l) {
                    mEditContentType = StringType.LATIN;
                } else {
                    mEditContentType = StringType.MIX;
                }

                if (!str.equals("")) { // str涓嶄负绌�                    
                	String selection = null;
                	if (mEditContentType == StringType.HANZI) {
                        selection = SinaCitysEntity.NAME + " LIKE " + "'%" + str + "%'";
                    } 
                    else {
                    	selection = SinaCitysEntity.NAME + " LIKE " + "'%" + "其他语言处理" + "%'";
                    }
                   /* else if (mEditContentType == StringType.LATIN) {
                   	 selection = SinaCitysEntity.NAME + " LIKE " + "'%" + "鎷奸煶涓嶆敮鎸� + "%'";
                       //selection = "(" + SinaCitysEntity.NAME + " LIKE " + "'%" + str + "%'" + ")";
                   }*/

                   // if (mEditContentType != StringType.MIX) {
                    if(true){
                        mCursor = getContentResolver().query(Uri.parse(CITY_CONTENT_URI),
                        		SinaCitysEntity.projection, selection, null,
                                null);
                    }
                    mpopcityLinear = (LinearLayout) findViewById(R.id.popviewlayout);
                    mpopcityLinear.setVisibility(View.INVISIBLE);                    
                }
                else{
                	mpopcityLinear = (LinearLayout) findViewById(R.id.popviewlayout);
                	mpopcityLinear.setVisibility(View.VISIBLE);
                }
                
                
               // popviewlayout
                // getListData();
                mListAdapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // mListView
        mListView = (ListView) findViewById(R.id.listview_add);
        mListAdapter = new MyAdapter(this);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(this);
        
        GridView gridview = (GridView)findViewById(R.id.gridview);
        gridview.setAdapter(new citysAdapter(this));
        
    }

    public void onBackClick(View v) {
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();

        // finish();
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        if(found){
        	 Intent data=new Intent();  
             //璇锋眰浠ｇ爜鍙互鑷繁璁剧疆锛岃繖閲岃缃垚20  
             setResult(1, data);  
        }
        else
        {
        	 Intent data=new Intent();  
             setResult(0, data);  
        }
        	
        super.onDestroy();
    }

    public void onDoneClick(View v) {
        String postCode = mEditText.getText().toString();
        postCode = postCode.trim(); // 鍘绘帀鍓嶅悗绌烘牸
        Log.v(TAG, "add postCode = " + postCode);

        if (!postCode.equals("")) {
            // 鍏堝湪鏁版嵁搴撲腑鏌ユ壘鏄惁涔嬪墠宸茬粡娣诲姞杩�            
        	ContentResolver resolver = getContentResolver();
            Cursor cursor = null;
            found = false;
            Uri uri = Uri.parse(MainActivity.POSTALCODE_URI);

            cursor = resolver.query(uri, PostalCodeEntity.projection,
                    PostalCodeEntity.POSTAL_CODE + " = '" + postCode + "'" + " and "
                            + PostalCodeEntity.USER_ID + " = '0'",
                    null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    found = true;
                }
            }
            
  //          if(postCode.equals(getResources().getString(R.string.defaultcity))){
  //          	Intent intent = new Intent();
	//			intent.setAction("com.cooee.weather.data.action.ADD_DAFAULT_CITY");
	//			sendBroadcast(intent);
    //        }
            // 娌℃壘鍒帮紝鍒欐坊鍔�          
            if (!found) {
                ContentValues values = new ContentValues();
                values.put(PostalCodeEntity.POSTAL_CODE, postCode);
                values.put(PostalCodeEntity.USER_ID, 0); // 0涓洪粯璁serId
                resolver.insert(uri, values);
                
                Intent data=new Intent();  
                data.putExtra("citys", postCode);   
                //请求代码可以自己设置，这里设置成20  
                setResult(1, data); 
            }
            else{
            	Intent data=new Intent();  
                data.putExtra("citys", postCode);   
                //请求代码可以自己设置，这里设置成20  
                setResult(1, data); 
            }
            	            	
        }

        finish();
    }
    

    private class MyAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            if (mCursor != null) {
            	if( mCursor.getCount() == 0){
            		/*mCursor.close();
            		mCursor = null;*/
            		return 1;//杩欓噷涓昏鏄墠闈㈢殑mCursor娌℃湁閲婃斁涓�洿涓嶄负绌猴紝
            	}
            	else
            	{
            		 return mCursor.getCount();
            	}               
            } else {            	
                return 0;
            }
        }

        @Override
        public Object getItem(int arg0) {
            return arg0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.add_post_item_layout, null);

                holder = new ViewHolder();
                holder.city = (TextView) convertView.findViewById(R.id.textview_city);
               // holder.city_pinyin = (TextView) convertView.findViewById(R.id.textview_city_pinyin);//(涓浗澶╂皵缃戝煄甯傛暟鎹簱)
               // holder.province = (TextView) convertView.findViewById(R.id.textview_province);//(涓浗澶╂皵缃戝煄甯傛暟鎹簱)

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(mCursor.getCount() == 0)//sxd 淇敼鍩庡競
            {
                 holder.city.setText(R.string.city_fault);
                 //holder.city_pinyin.setText("");
            	 //holder.province.setText("");
         		return convertView;
            }
        
            mCursor.moveToPosition(position);
            String name = mCursor.getString(0);
            holder.city.setText(name);


            /* //(浠ヤ笅涓浗澶╂皵缃戝煄甯傛暟鎹簱)
            String city_pinyin = mCursor.getString(3);
            long province_id = mCursor.getInt(0);
            if (name.indexOf(".") == -1) {
                holder.city.setText(name);
            } else {
                holder.city.setText(SinaCitysEntity.getPrefix(name) + " - "
                        + SinaCitysEntity.getSuffix(name));
            }
            if (mEditContentType == StringType.LATIN) {
                if (city_pinyin.indexOf(".") == -1) {
                    holder.city_pinyin.setText(" " + city_pinyin);
                } else {
                    holder.city_pinyin.setText(" " + SinaCitysEntity.getPrefix(city_pinyin) + " - "
                            + SinaCitysEntity.getSuffix(city_pinyin));
                }
            } else {
                holder.city_pinyin.setText("");
            }
            holder.province.setText(provinces[(int) province_id]);
            */

            return convertView;
        }

        public final class ViewHolder {
            public TextView city;
        }
    }

  

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mCursor != null) {
        	if(mCursor.getCount() == 0) return;
            mCursor.moveToPosition(position);
            String name = mCursor.getString(0);
           // String city_num = mCursor.getString(2);
         //   Log.v(TAG, "onItemClick name = " + name + " ,city_num = " + city_num);

            // 鐩存帴鍙栧悗缂�紝鍒灏辨棤闇�慨鏀逛簡
            // String postCode = CitysEntity.getSuffix(name);
            String postCode = name;
            // 鍏堝湪鏁版嵁搴撲腑鏌ユ壘鏄惁涔嬪墠宸茬粡娣诲姞杩�          
            ContentResolver resolver = getContentResolver();
            Cursor cursor = null;
            boolean found = false;
            Uri uri = Uri.parse(MainActivity.POSTALCODE_URI);

            cursor = resolver.query(uri, PostalCodeEntity.projection,
                    PostalCodeEntity.POSTAL_CODE + " = '" + postCode + "'" + " and "
                            + PostalCodeEntity.USER_ID + " = '0'",
                    null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    found = true;
                }
            }

  //          if(postCode.equals(getResources().getString(R.string.defaultcity))){
   //         	Intent intent = new Intent();
	//			intent.setAction("com.cooee.weather.data.action.ADD_DAFAULT_CITY");
	//			sendBroadcast(intent);
   //         }
            // 没找到，则添加
            if (!found) {
                ContentValues values = new ContentValues();
                values.put(PostalCodeEntity.POSTAL_CODE, postCode);
                values.put(PostalCodeEntity.USER_ID, 0); // 0涓洪粯璁serId
                //values.put(PostalCodeEntity.CITY_NUM, city_num);
                resolver.insert(uri, values);
                
                
                Intent data=new Intent();  
                data.putExtra("citys", postCode);   
                //请求代码可以自己设置，这里设置成20  
                setResult(1, data); 
            }
            else{
            	Intent data=new Intent();  
                data.putExtra("citys", postCode);   
                //请求代码可以自己设置，这里设置成20  
                setResult(1, data); 
                Toast.makeText(getApplicationContext(), postCode+" "+this.getResources().getString(R.string.cityexitmessage), Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
    
    private class citysAdapter extends BaseAdapter {
    	private Context mcontext = null;
    	public citysAdapter(Context context){
    			mcontext = context;
    	       mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	     
    	  public final int getCount() {
    	   return popcitys.length;
    	  }
    	  public final Object getItem(int position) {
    	   return null;
    	  }
    	  public final long getItemId(int position) {
    	   return position;
    	  }
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			  ViewHolder holder = new ViewHolder();
	        // TextView label = (TextView)convertView;
	        
	            //鎴戜滑娴嬭瘯鍙戠幇锛岄櫎绗竴涓猚onvertView澶栵紝鍏朵綑鐨勯兘鏄疦ULL锛屽洜姝ゅ鏋滄病鏈塿iew锛屾垜浠渶瑕佸垱寤�	            
			 /*if(convertView == null){
	                convertView = new TextView(context);
	                int itemId = R.layout.popcity_item;   
	                //convertView.set
	                label = (TextView)convertView;
	            }
	             label.setText(popcitys[position]);   */
	         if (convertView == null) {
	                convertView = mInflater.inflate(R.layout.popcity_item, null);
                    holder.textView = (TextView)convertView.findViewById(R.id.textview_city); 
                    holder.flag = true;
	                convertView.setTag(holder);
	            } else {
	            	holder.flag = false;
	                holder = (ViewHolder)convertView.getTag();
	            }  
	         
	         if(holder != null)
	         {
	        	 holder.textView.setText(popcitys[position]);
                 holder.textView.setTextSize(18);
	             holder.textView.setTextColor(0xffc3c3c3);	
	         }
	         

	         convertView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					ViewHolder holder = new ViewHolder();;
					 if (event.getAction() == MotionEvent.ACTION_DOWN) {
						 holder.textView = (TextView)v.findViewById(R.id.textview_city);
						 holder.textView.setTextSize(24);
		                 holder.textView.setTextColor(0xffa2d853);
			            } else if (event.getAction() == MotionEvent.ACTION_UP) {
			            	holder.textView = (TextView)v.findViewById(R.id.textview_city);
							holder.textView.setTextSize(18);
			                holder.textView.setTextColor(0xffc3c3c3);	
			                
			                String postCode = popcitys[position];
				            // 鍏堝湪鏁版嵁搴撲腑鏌ユ壘鏄惁涔嬪墠宸茬粡娣诲姞杩�				            
			                ContentResolver resolver = getContentResolver();
				            Cursor cursor = null;
				            boolean found = false;
				            Uri uri = Uri.parse(MainActivity.POSTALCODE_URI);
				            cursor = resolver.query(uri, PostalCodeEntity.projection,
				                    PostalCodeEntity.POSTAL_CODE + " = '" + postCode + "'" + " and "
				                            + PostalCodeEntity.USER_ID + " = '0'",
				                    null, null);
				            if (cursor != null) {
				                if (cursor.moveToFirst()) {
				                    found = true;
				                }
				            }

		//		            if(postCode.equals(getResources().getString(R.string.defaultcity))){
	//			            	Intent intent = new Intent();
	//							intent.setAction("com.cooee.weather.data.action.ADD_DAFAULT_CITY");
		//						sendBroadcast(intent);
		//		            }
				            
				            // 娌℃壘鍒帮紝鍒欐坊鍔�				            
				            if (!found) {
				                ContentValues values = new ContentValues();
				                values.put(PostalCodeEntity.POSTAL_CODE, postCode);
				                values.put(PostalCodeEntity.USER_ID, 0); // 0涓洪粯璁serId
				                //values.put(PostalCodeEntity.CITY_NUM, city_num);
				                resolver.insert(uri, values);
				                
				                Intent data=new Intent();  
				                data.putExtra("citys", postCode);   
				                //请求代码可以自己设置，这里设置成20  
				                setResult(1, data);  
				            }
				            else
				            {
				            	Intent data=new Intent();  
				                data.putExtra("citys", postCode); 
				                setResult(1, data);
				                Toast.makeText(getApplicationContext(), postCode+ " "+mcontext.getResources().getString(R.string.cityexitmessage), Toast.LENGTH_SHORT).show();
				            }

				            finish();				            
			            }
			            else{
			            	holder.textView = (TextView)v.findViewById(R.id.textview_city);
							holder.textView.setTextSize(18);
			                holder.textView.setTextColor(0xffc3c3c3);	
			            }
					 
					return true;
				}
    
			});	                 
	        return convertView;
		}
		}
    
    public static class ViewHolder {
        public TextView textView;
        public boolean flag;
    }
    
    
}
