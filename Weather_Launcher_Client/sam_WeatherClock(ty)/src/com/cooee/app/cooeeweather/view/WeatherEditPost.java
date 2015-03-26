package com.cooee.app.cooeeweather.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cooee.app.cooeeweather.component.cityButton;
import com.cooee.app.cooeeweather.dataentity.PostalCodeEntity;
import com.cooee.app.cooeeweather.dataentity.weatherdataentity;
import com.cooee.app.cooeeweather.dataentity.weatherforecastentity;
import com.cooee.app.cooeeweather.dataprovider.weatherdataprovider;
import com.cooee.widget.samweatherclock.MainActivity;
import com.cooee.widget.samweatherclock.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class WeatherEditPost extends Activity {

	private static final String TAG = "com.cooee.weather.WeatherEditPost";
	//20130709-liuhailin-<auto change authority>
	//public final static String WEATHER_URI = "content://com.cooee.app.cooeeweather.dataprovider/weather";
	//public final static String POSTALCODE_URI = "content://com.cooee.app.cooeeweather.dataprovider/postalCode";
	public final static String WEATHER_URI = "content://"+ weatherdataprovider.AUTHORITY +"/weather";
	public final static String POSTALCODE_URI = "content://"+ weatherdataprovider.AUTHORITY +"/postalCode";
	//20130709-liuhailin-<auto change authority>
	private ArrayList<String> mPoscalCodList;

	 private DisplayMetrics dm = new DisplayMetrics();
	 
	Map<String, weatherdataentity> mapDate = new HashMap<String, weatherdataentity>();

	private boolean mCheckArray[];
	private citysAdapter mcitysAdapter = null;
	GridView mgridview = null;
	private ProgressDialog mpDialog;

    public List<Map<String, Object>> getListData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        for (int i = 0; i < mPoscalCodList.size(); i++) {
            map = new HashMap<String, Object>();
            map.put("checkbox", R.drawable.check_box_icon);
            map.put("text", mPoscalCodList.get(i));
            list.add(map);
        }

        return list;
    }

    public void readPostalCodeList() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = null;
        Uri uri = Uri.parse(MainActivity.POSTALCODE_URI);

        // 先清空mPoscalCodList
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
					mapDate.put(mPostalCodeEntity.getPostalCode(),
							new weatherdataentity());
					readData(mPostalCodeEntity.getPostalCode());
					Log.v(TAG, "mPostalCodeEntity.getPostalCode() = "
							+ mPostalCodeEntity.getPostalCode());
				} while (cursor.moveToNext());
			}

			cursor.close();
		}
	}

	public void requestData(String cityname) {
		Intent intent = new Intent(MainActivity.DATA_SERVICE_ACTION);
		intent.putExtra("postalCode", cityname);
		intent.putExtra("forcedUpdate", 1); // ǿ�Ƹ���
		startService(intent);
	}

	public boolean readData(String cityname) {
		ContentResolver resolver = getContentResolver();
		Cursor cursor = null;
		Uri uri;
		String selection;
		weatherdataentity weatherdate = null;
		uri = Uri.parse(WEATHER_URI + "/" + cityname);
		selection = weatherdataentity.POSTALCODE + "=" + "'" + cityname + "'";
		cursor = resolver.query(uri, weatherdataentity.projection, selection,
				null, null);
		if (cursor != null) {
			weatherdate = mapDate.get(cityname);
			if (weatherdate == null)
				weatherdate = new weatherdataentity();
			if (cursor.moveToFirst()) {
				weatherdate.setUpdateMilis(cursor.getInt(0));
				weatherdate.setCity(cursor.getString(1));
				weatherdate.setPostalCode(cursor.getString(2));
				weatherdate.setForecastDate(cursor.getLong(3));
				weatherdate.setCondition(cursor.getString(4));
				weatherdate.setTempF(cursor.getInt(5));
				weatherdate.setTempC(cursor.getInt(6));

				weatherdate.setHumidity(cursor.getString(7));
				weatherdate.setIcon(cursor.getString(8));
				weatherdate.setWindCondition(cursor.getString(9));
				weatherdate.setLastUpdateTime(cursor.getLong(10));
				weatherdate.setIsConfigured(cursor.getInt(11));
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
		if (weatherdate != null) {
			uri = Uri.parse(WEATHER_URI + "/" + cityname + "/detail");
			selection = weatherforecastentity.CITY + "=" + "'" + cityname + "'";
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

					weatherdate.getDetails().add(forecast);

					details_count = details_count + 1;
				}
				cursor.close();
			}
		}

		Log.v(TAG, "details_count = " + details_count);
		if (details_count < 4) {
			weatherdate = null;
		}
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_post_layout);

		 WindowManager mWm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
	        mWm.getDefaultDisplay().getMetrics(dm);
	        
	        /*TextView textview_city = (TextView)findViewById(R.id.textcity_item); 
	        LayoutParams laParams0 = (LayoutParams)textview_city.getLayoutParams();
	        laParams0.height=dm.heightPixels - dm.widthPixels;
	        laParams0.width = dm.widthPixels;
	        textview_city.setPadding(0, 0, 100, 200);*/
	        //textview_city.setLayoutParams(laParams0);
	        
		// ����mContext

		mPoscalCodList = new ArrayList<String>();

		// ��ȡ���
		readPostalCodeList();

		mCheckArray = new boolean[mPoscalCodList.size()];
		for (int i = 0; i < mCheckArray.length; i++) {
			mCheckArray[i] = false;
		}

		mgridview = (GridView) findViewById(R.id.grid_citys);
		mgridview.requestFocus();
		mcitysAdapter = new citysAdapter(this);
		mgridview.setAdapter(mcitysAdapter);

		WeatherReceiver.setEditHandler(mHandler);

	}

	private void addactivte() {
		Intent addintent = new Intent(this, WeatherAddPost.class);
		startActivityForResult(addintent, CONTEXT_RESTRICTED);
	}

	@SuppressLint("NewApi")
	private void popactivety(final String cityname) {
		// �Զ����
		/*
		 * LayoutInflater inflater = getLayoutInflater(); View layout =
		 * inflater.inflate(R.layout.popsetting, (ViewGroup)
		 * findViewById(R.id.dialogcity)); new
		 * AlertDialog.Builder(this).setTitle("�Ϻ�").setView(layout)
		 * .setPositiveButton("ȷ��", null) .setNegativeButton("ȡ��", null).show();
		 */
		/*
		 * DialogInterface.OnClickListener listener = null;
		 * listener.onClick(dialog, which)
		 */
		new AlertDialog.Builder(this)
				.setTitle(cityname)
				.setItems(R.array.dialoginfo,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ContentResolver resolver = getContentResolver();
								Uri uri = Uri.parse(MainActivity.POSTALCODE_URI);
								String selection = "";
								if (which == 1) {
									for (int i = 0; i < mCheckArray.length; i++) {
										if (!selection.equals("")) {
											selection += " or ";
										}
										// ��ɾ��userid=0�����ֹwidget�޷����£�widget�Ƴ�ʱ������ɾ�����
										selection = selection
												+ PostalCodeEntity.POSTAL_CODE
												+ "=" + "'" + cityname + "' "
												+ " and "
												+ PostalCodeEntity.USER_ID
												+ "=" + "'0'";
									}

									if (!selection.equals("")) {
										Log.v(TAG, "selection = " + selection);
										resolver.delete(uri, selection, null);
					//					if(cityname.equals(getResources().getString(R.string.defaultcity)) ){
					//						MainActivity.setdeldefault(false);
					//						Intent intent = new Intent();
					//						intent.setAction("com.cooee.weather.data.action.DEL_DAFAULT_CITY");
					//						sendBroadcast(intent);
					//					
					//					}
									}
									Message message = Message.obtain();
									message.what = WeatherReceiver.MSG_REFRESH; //�h�����
									mHandler.sendMessage(message);
									// citysAdapter.notifyDataSetChanged();
								}
								if (which == 0) {
									requestData(cityname);
									mpDialog = new ProgressDialog(
											WeatherEditPost.this);
									mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��ΪԲ�ν����
									// mpDialog.setTitle("��ʾ");//���ñ���
									// mpDialog.setIcon(R.drawable.ic_launcher);//����ͼ��
									mpDialog.setMessage(getResources().getString(R.string.updataingstr));
									mpDialog.setIndeterminate(false);// ���ý�����Ƿ�Ϊ����ȷ
									mpDialog.getWindow().setBackgroundDrawable(
											new ColorDrawable(0x00000000));
									mpDialog.getWindow().setLayout(30, 30);
									mpDialog.setCancelable(true);// ���ý�����Ƿ���԰��˻ؼ�ȡ��
									mpDialog.show();
								}
							}

						}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((0 != resultCode)
				&& ((mPoscalCodList != null) || (mPoscalCodList.size() != 0))) {
			
			 Message message = Message.obtain();
             message.what = WeatherReceiver.MSG_REFRESH;
             mHandler.sendMessage(message);
             
			/*if (0 != resultCode) {
				Bundle bunde = data.getExtras();
				String cityname = bunde.getString("citys");
				mpDialog = new ProgressDialog(
						WeatherEditPost.this);
				// mpDialog = (ProgressDialog)(
				// this.findViewById(R.id.pb));
				mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��ΪԲ�ν����
				// mpDialog.setTitle("��ʾ");//���ñ���
				// mpDialog.setIcon(R.drawable.ic_launcher);//����ͼ��
				mpDialog.setMessage("���ڸ���...");
				mpDialog.setIndeterminate(false);// ���ý�����Ƿ�Ϊ����ȷ
				mpDialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(0x00000000));
				mpDialog.getWindow().setLayout(30, 30);
				mpDialog.setCancelable(true);// ���ý�����Ƿ���԰��˻ؼ�ȡ��
				mpDialog.show();
				requestData(cityname);
			}*/
			/*
			 * Message message = Message.obtain(); message.what = 1;
			 * mHandler.sendMessage(message);
			 */
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WeatherReceiver.MSG_REFRESH:
				mcitysAdapter.notifyDataSetChanged(); // �˴��Ѿ�ִ���ˣ���ListViewû�и���
				break;
			default:
				 Toast.makeText(getApplicationContext(), getResources().getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
				break;	
			}
			if(mpDialog!= null){
				mpDialog.cancel();
				mpDialog = null;
			}
			//mpDialog.dismiss();
		}

	};

	private class citysAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub
			readPostalCodeList();
			super.notifyDataSetChanged();

		}

		public citysAdapter(Context context) {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public final int getCount() {
			// return popcitys.length;
			if (mPoscalCodList.size() == 9)
				return mPoscalCodList.size();
			else
				return mPoscalCodList.size() + 1;
		}

		public final Object getItem(int position) {
			return null;
		}

		public final long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (position < (mPoscalCodList.size())) {
				// if(convertView == null)
				convertView = mInflater.inflate(R.layout.citymanage_item, null);
				TextView textcityname = (TextView) convertView
						.findViewById(R.id.textcityname);
				textcityname.setText(mPoscalCodList.get(position));

				textcityname = (TextView) convertView
						.findViewById(R.id.textweathertype);
				ImageView image = (ImageView) convertView
						.findViewById(R.id.imageweathertype);
				if (mapDate.get(mPoscalCodList.get(position)).getCondition() != null) {
					textcityname.setText(mapDate.get(
							mPoscalCodList.get(position)).getCondition());
					image.setImageResource(WeatherConditionImage
							.getConditionImage(mapDate.get(
									mPoscalCodList.get(position))
									.getCondition()));
				} else {
					textcityname.setText(getResources().getString(
							R.string.waitinfo));
				}

				textcityname = (TextView) convertView
						.findViewById(R.id.textcitytemp);
				ArrayList<weatherforecastentity> dadf = mapDate.get(
						mPoscalCodList.get(position)).getDetails();
				if ((dadf != null) && (dadf.size() != 0)) {
					String str = getResources().getString(
							R.string.date_format_string);
					String temp = String.format(str,
							mapDate.get(mPoscalCodList.get(position))
									.getDetails().get(0).getHight().toString(),
							mapDate.get(mPoscalCodList.get(position))
									.getDetails().get(0).getLow().toString());
					Log.v(temp, temp);
					textcityname.setText(temp);
				} else
					textcityname.setText(" ");
				// textcityname.setText(mapDate.get(mPoscalCodList.get(position)).getCondition());

				/*cityButton btn = (cityButton) convertView
						.findViewById(R.id.imageweatherbg);*/
				
				Button btn = (Button) convertView
						.findViewById(R.id.imageweatherbg);
				btn.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						popactivety(mPoscalCodList.get(position));
						// mPoscalCodList.remove(position);;
						// mcitysAdapter.notifyDataSetChanged();
						return true;
					}
				});

				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent data = new Intent();
						data.putExtra("citys", mPoscalCodList.get(position));
						// �����������Լ����ã��������ó�20
						setResult(1, data);
						finish();
					}
				});
			} else {
				// if(convertView == null)
				convertView = mInflater.inflate(R.layout.citymanage_item, null);
				LinearLayout linecitys = (LinearLayout) convertView
						.findViewById(R.id.linearcitys);
				linecitys.setVisibility(View.INVISIBLE);
				cityButton addcity = (cityButton) convertView
						.findViewById(R.id.imageaddcitys);
				addcity.setVisibility(View.VISIBLE);
				
			TextView text = (TextView) convertView.findViewById(R.id.addtextspace);
				text.setVisibility(View.VISIBLE);
				
				addcity.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						addactivte();
					}
				});
			}

			return convertView;
		}
	}

	public static class ViewHolder {
		public TextView textView;
		public boolean flag;
	}

}
