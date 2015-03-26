package com.iLoong.NumberClock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iLoong.NumberClock.common.CityResult;
import com.iLoong.NumberClock.common.CooeeClient;
import com.iLoong.NumberClock.common.NumberClockHelper;
import com.iLoong.NumberClock.common.Parameter;
import com.iLoong.NumberClock.common.Weather;
import com.iLoong.NumberClock.common.WeatherEntity;
import com.iLoong.NumberClock.common.YahooClient;
import com.iLoong.NumberClockEx.R;

public class CityFinderView extends RelativeLayout {

	private RelativeLayout cityfinderview = null;
	private EditText editcitysearch;
	private ImageView ivsearchcity;
	private ListView lvcityshow;

	private CityAdapter cityadapter;
	private TextView tv_cuttentcity;
	private static final String DB_PATH = "/data/data/com.cooeeui.brand.turbolauncher/databases/";
	private static final String DB_NAME = "city_db.db";
	private String PATH = null;
	private SQLiteDatabase CitysDb;
	private static final String TABLE_CITYS = "CITY_LIST";
	private SharedPreferences sharepreference = null;
	public static Handler mHandler = null;
	public static final int MSG_SUCCESS = 1;
	public static final int MSG_FAILURE = 2;
	public static final int MSG_NETWORK_FAILURE = 3;
	private CustomProcessDialog progressDialog = null;
	public static final int MSGFOREIGN_SUCCESS = 4;
	public static final int MSGFOREIGN_FAILURE = 5;
	public static final int MSGFOPREIGN_NETWORK_FAILURE = 6;

	private Toast toast;

	public CityFinderView(final Context context) {
		super(context);
		initviews(context);

		PATH = context.getFilesDir() + File.separator + "numberclock"
				+ File.separator + "list.dat";
		if (!Parameter.enable_google_version) {
			copyDatabase(context);
			CitysDb = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
					SQLiteDatabase.OPEN_READONLY);
		}

		sharepreference = PreferenceManager
				.getDefaultSharedPreferences(context);

		mHandler = new Handler() {
			public void handleMessage(final Message msg) {
				switch (msg.what) {
				case MSG_SUCCESS:
					Bundle bundles = (Bundle) msg.obj;
					WeatherEntity weathers = (WeatherEntity) bundles
							.getSerializable(Parameter.SerializableCitySearchName);

					Editor editor = sharepreference.edit();
					editor.putString(Parameter.currentCityName,
							weathers.getCity());
					editor.commit();

					tv_cuttentcity.setText(weathers.getCity());

					List<String> list = new ArrayList<String>();
					list.add(weathers.getCity());
					List<String> listdate = NumberClockHelper.GetData(PATH);
					if (listdate.size() != 0) {
						for (int i = 0; i < listdate.size(); i++) {
							if (list.size() < 10) {
								boolean ifExist = false;
								for (int j = 0; j < list.size(); j++) {
									if (list.get(j).equals(listdate.get(i))) {
										ifExist = true;
										break;
									}
								}
								if (!ifExist) {
									list.add(listdate.get(i));
								}
							} else {
								break;
							}
						}
					}
					NumberClockHelper.saveData(list, PATH);

					Intent intents = new Intent();
					intents.setAction(Parameter.BROADCASE_WeatherInLandSearch);
					Bundle myBundle = new Bundle();
					myBundle.putSerializable(
							Parameter.SerializableBroadcastName, weathers);
					intents.putExtras(myBundle);
					context.sendBroadcast(intents);

					if (progressDialog != null) {
						progressDialog.dismiss();
					}

					editcitysearch.setText(null);
					List<CityResult> crlist = new ArrayList<CityResult>();
					for (int j = 0; j < list.size(); j++) {
						CityResult cr = new CityResult();
						cr.setCityName(list.get(j));
						crlist.add(cr);
					}
					cityadapter = new CityAdapter(context, crlist);
					lvcityshow.setAdapter(cityadapter);

					break;
				case MSG_FAILURE:
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
					String str1 = context.getResources().getString(
							R.string.flushfailed);
					if (toast != null) {
						toast.setText(str1);
					} else {
						toast = Toast.makeText(context, str1,
								Toast.LENGTH_SHORT);
					}
					toast.show();
					break;
				case MSG_NETWORK_FAILURE:
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
					String str2 = context.getResources().getString(
							R.string.networkerror);
					if (toast != null) {
						toast.setText(str2);
					} else {
						toast = Toast.makeText(context, str2,
								Toast.LENGTH_SHORT);
					}
					toast.show();
					break;
				case MSGFOREIGN_SUCCESS:
					Bundle bundleforeign = (Bundle) msg.obj;
					Weather weather = (Weather) bundleforeign
							.getSerializable(Parameter.SerializableCitySearchForeignName);
					CityResult result = (CityResult) bundleforeign
							.getSerializable(Parameter.SerializableReturnCurrentCity);
					Editor editorforeign = sharepreference.edit();

					editorforeign.putString(Parameter.currentCityId,
							result.getWoeid());
					editorforeign.putString(Parameter.currentCityName,
							result.getCityName());
					editorforeign.putString(Parameter.currentCountry,
							result.getCountry());

					editorforeign.commit();

					tv_cuttentcity.setText(result.getCityName());

					List<CityResult> listforeign = new ArrayList<CityResult>();
					listforeign.add(result);
					List<CityResult> listforeigndate = NumberClockHelper
							.GetDataForeign(PATH);
					if (listforeigndate.size() != 0) {
						for (int i = 0; i < listforeigndate.size(); i++) {
							if (listforeign.size() < 10) {
								boolean ifExist = false;
								for (int j = 0; j < listforeign.size(); j++) {
									if (listforeign
											.get(j)
											.getWoeid()
											.equals(listforeigndate.get(i)
													.getWoeid())) {
										ifExist = true;
										break;
									}
								}
								if (!ifExist) {
									listforeign.add(listforeigndate.get(i));
								}
							} else {
								break;
							}
						}
					}
					NumberClockHelper.saveDataForeign(listforeign, PATH);

					Intent intent = new Intent();
					intent.setAction(Parameter.BROADCASE_WeatherForeignSearch);
					Bundle mBundle = new Bundle();
					mBundle.putSerializable(
							Parameter.SerializableBroadcastName, weather);
					intent.putExtras(mBundle);
					context.sendBroadcast(intent);

					if (progressDialog != null) {
						progressDialog.dismiss();
					}

					editcitysearch.setText(null);

					cityadapter = new CityAdapter(context, listforeign);

					lvcityshow.setAdapter(cityadapter);
					break;
				case MSGFOREIGN_FAILURE:
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
					String str3 = context.getResources().getString(
							R.string.searchfailed_foreign);
					if (toast != null) {
						toast.setText(str3);
					} else {
						toast = Toast.makeText(context, str3,
								Toast.LENGTH_SHORT);
					}
					toast.show();
					break;
				case MSGFOPREIGN_NETWORK_FAILURE:
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
					String str4 = context.getResources().getString(
							R.string.networkerror_foreign);
					if (toast != null) {
						toast.setText(str4);
					} else {
						toast = Toast.makeText(context, str4,
								Toast.LENGTH_SHORT);
					}
					toast.show();
					break;
				}
			}
		};

		if (cityfinderview != null) {
			editcitysearch = (EditText) cityfinderview
					.findViewById(R.id.et_searchcity);
			ivsearchcity = (ImageView) cityfinderview
					.findViewById(R.id.iv_search);
			lvcityshow = (ListView) cityfinderview
					.findViewById(R.id.lv_cityshow);
			tv_cuttentcity = (TextView) findViewById(R.id.tv_cuttentcity);
			if (Parameter.enable_google_version) {
				tv_cuttentcity.setText(context.getResources().getText(
						R.string.default_foreigncity));
			}
			cityadapter = new CityAdapter(context, null);

			editcitysearch.addTextChangedListener(filterTextWatcher);

			String currentString = sharepreference.getString(
					Parameter.currentCityName, null);
			if (currentString != null) {
				if (Parameter.enable_google_version) {
					ArrayList<CityResult> list = NumberClockHelper
							.GetDataForeign(PATH);
					cityadapter = new CityAdapter(context, list);

				} else {
					ArrayList<String> list = NumberClockHelper.GetData(PATH);
					List<CityResult> crlist = new ArrayList<CityResult>();
					for (int j = 0; j < list.size(); j++) {
						CityResult cr = new CityResult();
						cr.setCityName(list.get(j));
						crlist.add(cr);
					}
					cityadapter = new CityAdapter(context, crlist);
				}
				tv_cuttentcity.setText(currentString);
			}

			lvcityshow.setAdapter(cityadapter);
			lvcityshow.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (NumberClockHelper.isHaveInternet(context)) {
						final CityResult result = (CityResult) parent
								.getItemAtPosition(position);
						progressDialog = CustomProcessDialog
								.createDialog(context);
						progressDialog.show();
						new Thread(new Runnable() {
							@Override
							public void run() {
								if (Parameter.enable_google_version) {
									YahooClient
											.getWeatherInfo(
													result,
													sharepreference
															.getString(
																	Parameter.currentunit,
																	"f"),
													context, 1);
								} else {
									CooeeClient.getWeatherInfo(context,
											result.getCityName(), 1);
								}
							}
						}).start();
					} else {
						if (Parameter.enable_google_version) {
							String str1 = context.getResources().getString(
									R.string.networkerror_foreign);
							if (toast != null) {
								toast.setText(str1);
							} else {
								toast = Toast.makeText(context, str1,
										Toast.LENGTH_SHORT);
							}
							toast.show();
						} else {
							String str1 = context.getResources().getString(
									R.string.networkerror);
							if (toast != null) {
								toast.setText(str1);
							} else {
								toast = Toast.makeText(context, str1,
										Toast.LENGTH_SHORT);
							}
							toast.show();
						}
					}
				}
			});
			ivsearchcity.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (cityadapter.getCount() > 0) {
						if (NumberClockHelper.isHaveInternet(context)) {
							final CityResult result = (CityResult) cityadapter
									.getItem(0);
							progressDialog = CustomProcessDialog
									.createDialog(context);
							progressDialog.show();
							new Thread(new Runnable() {
								@Override
								public void run() {
									if (Parameter.enable_google_version) {
										YahooClient.getWeatherInfo(result,
												sharepreference.getString(
														Parameter.currentunit,
														"f"), context, 1);
									} else {
										CooeeClient.getWeatherInfo(context,
												result.getCityName(), 1);
									}
								}
							}).start();
						} else {
							if (Parameter.enable_google_version) {
								String str1 = context.getResources().getString(
										R.string.networkerror_foreign);
								if (toast != null) {
									toast.setText(str1);
								} else {
									toast = Toast.makeText(context, str1,
											Toast.LENGTH_SHORT);
								}
								toast.show();
							} else {
								String str1 = context.getResources().getString(
										R.string.networkerror);
								if (toast != null) {
									toast.setText(str1);
								} else {
									toast = Toast.makeText(context, str1,
											Toast.LENGTH_SHORT);
								}
								toast.show();
							}
						}
					} else {
						if (Parameter.enable_google_version) {
							String str1 = context.getResources().getString(
									R.string.citynotfound_foreign);
							if (toast != null) {
								toast.setText(str1);
							} else {
								toast = Toast.makeText(context, str1,
										Toast.LENGTH_SHORT);
							}
							toast.show();
							List<CityResult> crlist = new ArrayList<CityResult>();
							CityResult cr = new CityResult();
							cr.setCityName(context.getResources().getString(
									R.string.citynotfound_foreign));
							crlist.add(cr);
							cityadapter = new CityAdapter(context, crlist);
							lvcityshow.setAdapter(cityadapter);
						} else {
							String str1 = context.getResources().getString(
									R.string.citynotfound);
							if (toast != null) {
								toast.setText(str1);
							} else {
								toast = Toast.makeText(context, str1,
										Toast.LENGTH_SHORT);
							}
							toast.show();
							List<CityResult> crlist = new ArrayList<CityResult>();
							CityResult cr = new CityResult();
							cr.setCityName(context.getResources().getString(
									R.string.citynotfound));
							crlist.add(cr);
							cityadapter = new CityAdapter(context, crlist);
							lvcityshow.setAdapter(cityadapter);
						}
					}
				}
			});
		}
	}

	private void initviews(Context context) {
		if (cityfinderview == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			cityfinderview = (RelativeLayout) inflater.inflate(
					R.layout.cityfinderlayout, this);
		}
	}

	private TextWatcher filterTextWatcher = new TextWatcher() {
		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			cityadapter.getFilter().filter(s);
			cityadapter.notifyDataSetChanged();
		}
	};

	private class CityAdapter extends ArrayAdapter<CityResult> implements
			Filterable {

		private Context ctx;
		private List<CityResult> cityList = new ArrayList<CityResult>();

		public CityAdapter(Context ctx, List<CityResult> cityList) {
			super(ctx, R.layout.cityresult_layout, cityList);
			this.cityList = cityList;
			this.ctx = ctx;
		}

		@Override
		public CityResult getItem(int position) {
			if (cityList != null)
				return cityList.get(position);
			return null;
		}

		@Override
		public int getCount() {
			if (cityList != null)
				return cityList.size();
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View result = convertView;
			if (result == null) {
				LayoutInflater inf = (LayoutInflater) ctx
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				result = inf.inflate(R.layout.cityresult_layout, parent, false);
			}
			TextView tv = (TextView) result.findViewById(R.id.txtCityName);
			if (Parameter.enable_google_version) {
				if (cityList.get(position).getCountry() != null) {
					tv.setText(cityList.get(position).getCityName() + ","
							+ cityList.get(position).getCountry());
				} else {
					tv.setText(cityList.get(position).getCityName());
				}
			} else {
				tv.setText(cityList.get(position).getCityName());
			}
			return result;
		}

		@Override
		public long getItemId(int position) {
			if (cityList != null)
				return cityList.get(position).hashCode();
			return 0;
		}

		@Override
		public Filter getFilter() {
			Filter cityFilter = new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					if (constraint == null || constraint.length() < 1)
						return results;
					if (Parameter.enable_google_version) {
						if (NumberClockHelper.isHaveInternet(ctx)) {
							List<CityResult> cityResultList = YahooClient
									.getCityList(constraint.toString(), ctx);
							if (cityResultList.size() != 0) {
								results.values = cityResultList;
								results.count = cityResultList.size();
							}
							return results;
						} else {
							String str1 = ctx.getResources().getString(
									R.string.networkerror);
							if (toast != null) {
								toast.setText(str1);
							} else {
								toast = Toast.makeText(ctx, str1,
										Toast.LENGTH_SHORT);
							}
							toast.show();
							return results;
						}
					} else {

						String selection = null;
						selection = CityResult.NAME + " LIKE " + "'%"
								+ constraint + "%'";
						Cursor cursor = queryCitys(CityResult.projection,
								selection, null, null, ctx);
						List<CityResult> list = new ArrayList<CityResult>();
						if (cursor != null && cursor.getCount() > 0) {
							while (cursor.moveToNext()) {
								CityResult cityresult = new CityResult();
								cityresult.setCityName(cursor.getString(cursor
										.getColumnIndex("city")));
								list.add(cityresult);
							}
							cursor.close();
						}
						results.values = list;
						results.count = list.size();
						return results;
					}
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					cityList = (List) results.values;
					notifyDataSetChanged();
				}
			};
			return cityFilter;
		}
	}

	private static void copyDatabase(Context context) {
		String outFileName;
		InputStream myInput;
		if (checkDataBase() == false) {
			try {
				myInput = context.getAssets().open("db/" + DB_NAME);
				outFileName = DB_PATH + DB_NAME;
				File f = new File(DB_PATH);
				if (!f.exists()) {
					f.mkdirs();
				}
				f = new File(outFileName);
				if (!f.exists()) {
					f.createNewFile();
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean checkDataBase() {
		String myPath = DB_PATH + DB_NAME;
		File dbFile = new File(myPath);
		if (dbFile != null && dbFile.exists()) {
			return true;
		} else {
			return false;
		}
	}

	private Cursor queryCitys(String[] projection, String selection,
			String[] selectionArgs, String sortOrder, Context context) {
		copyDatabase(context);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String limit = null;
		qb.setTables(TABLE_CITYS);
		return qb.query(CitysDb, projection, selection, selectionArgs, null,
				null, sortOrder, limit);
	}

}
