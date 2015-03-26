package com.iLoong.NumberClock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iLoong.NumberClock.NumberWeatherwebservice.FLAG_UPDATE;
import com.iLoong.NumberClock.Timer.TimeService;
import com.iLoong.NumberClock.common.CityResult;
import com.iLoong.NumberClock.common.CooeeClient;
import com.iLoong.NumberClock.common.NumberClockHelper;
import com.iLoong.NumberClock.common.Parameter;
import com.iLoong.NumberClock.common.Weather;
import com.iLoong.NumberClock.common.WeatherEntity;
import com.iLoong.NumberClock.common.YahooClient;
import com.iLoong.NumberClockEx.R;

public class NumberClockView extends RelativeLayout implements
		View.OnClickListener {

	private String amOrpm = "AM";
	private int TIME_HOUR = 0;
	private int TIME_MINUTE = 0;
	private int TIME_WEEK = 0;
	private int TIME_YEAR = 0;
	private int TIME_MONTH = 0;
	private int TIME_DAY = 0;
	private boolean TIME_IS24 = false;
	private SharedPreferences sharepreference = null;

	private Toast toast;

	private HashMap<String, Object> item = new HashMap<String, Object>();
	private List<String> pagList = new ArrayList<String>();

	public static Handler mHandler = null;
	public static final int MSG_SUCCESS = 7;
	public static final int MSG_FAILURE = 8;
	public static final int MSG_NETWORK_FAILURE = 9;
	public static final int MSG_FOREIGNSUCCESS = 13;
	public static final int MSG_FOREIGNFAILURE = 14;
	public static final int MSG_FOREIGNNETWORK_FAILURE = 15;

	public static final int MSG_AUTOUPDATE_INLANDSUCCESS = 10;
	public static final int MSG_AUTOUPDATE_INLANDFAILURE = 11;
	public static final int MSG_AUTOUPDATE_NETWORK_FAILURE = 12;

	public static final int MSG_AUTOUPDATE_FOREIGNSUCCESS = 16;

	private RelativeLayout myclocknumberview;
	private ImageView img_weathercode;
	private TextView tv_tempture;
	private TextView tv_ampm;
	private ImageView iv_num1;
	private ImageView iv_num2;
	private ImageView iv_num3;
	private ImageView iv_num4;
	private ImageView iv_ncfluash;
	private TextView tv_date;
	private TextView tv_cityname;
	private LinearLayout ll_time;
	private LinearLayout ll_city;

	private final Context mcontext;

	private DateReceiver datereceiver = null;

	public NumberClockView(Context context) {
		super(context);
		this.mcontext = context;
		sharepreference = PreferenceManager
				.getDefaultSharedPreferences(context);
		mHandler = new Handler() {
			public void handleMessage(final Message msg) {
				switch (msg.what) {
				case MSG_SUCCESS:
					Bundle bundles = (Bundle) msg.obj;
					WeatherEntity weathers = (WeatherEntity) bundles
							.getSerializable(Parameter.SerializableWidgetFlushName);

					Intent intents = new Intent();
					intents.setAction(Parameter.BROADCASE_WeatherWidgetFlush);
					Bundle myBundle = new Bundle();
					myBundle.putSerializable(
							Parameter.SerializableBroadcastName, weathers);
					intents.putExtras(myBundle);
					mcontext.sendBroadcast(intents);

					String str8 = mcontext.getResources().getString(
							R.string.flushsuccess);
					if (toast != null) {
						toast.setText(str8);
					} else {
						toast = Toast.makeText(mcontext, str8,
								Toast.LENGTH_SHORT);
					}
					toast.show();

					break;

				case MSG_FAILURE:
					String str1 = mcontext.getResources().getString(
							R.string.flushfailed);
					if (toast != null) {
						toast.setText(str1);
					} else {
						toast = Toast.makeText(mcontext, str1,
								Toast.LENGTH_SHORT);
					}
					toast.show();
					break;
				case MSG_NETWORK_FAILURE:
					String str2 = mcontext.getResources().getString(
							R.string.networkerror);
					if (toast != null) {
						toast.setText(str2);
					} else {
						toast = Toast.makeText(mcontext, str2,
								Toast.LENGTH_SHORT);
					}
					toast.show();
					break;
				case MSG_AUTOUPDATE_INLANDSUCCESS:
					Bundle bundle = (Bundle) msg.obj;
					WeatherEntity weather = (WeatherEntity) bundle
							.getSerializable(Parameter.SerializableAutoUpdateName);
					if (weather != null && weather.getDetails() != null
							&& weather.getDetails().size() == 5) {
						Intent intent = new Intent();
						intent.setAction(Parameter.UPDATE_RESULT);
						Bundle sendbundle = new Bundle();
						sendbundle.putSerializable(
								Parameter.SerializableBroadcastName, weather);
						intent.putExtras(sendbundle);
						mcontext.sendBroadcast(intent);
						NumberWeatherwebservice.Update_Result_Flag = FLAG_UPDATE.UPDATE_SUCCES;
						String str3 = mcontext.getResources().getString(
								R.string.flushsuccess);
						if (toast != null) {
							toast.setText(str3);
						} else {
							toast = Toast.makeText(mcontext, str3,
									Toast.LENGTH_SHORT);
						}
						toast.show();
					} else {
						NumberWeatherwebservice.Update_Result_Flag = FLAG_UPDATE.WEBSERVICE_ERROR;
					}
					break;
				case MSG_AUTOUPDATE_INLANDFAILURE:
					String str3 = mcontext.getResources().getString(
							R.string.flushfailed);
					if (toast != null) {
						toast.setText(str3);
					} else {
						toast = Toast.makeText(mcontext, str3,
								Toast.LENGTH_SHORT);
					}
					toast.show();
					break;
				case MSG_AUTOUPDATE_NETWORK_FAILURE:
					String str4 = mcontext.getResources().getString(
							R.string.networkerror);
					if (toast != null) {
						toast.setText(str4);
					} else {
						toast = Toast.makeText(mcontext, str4,
								Toast.LENGTH_SHORT);
					}
					toast.show();
					break;
				case MSG_FOREIGNSUCCESS:
					Bundle bundleforeign = (Bundle) msg.obj;
					Weather weatherforeign = (Weather) bundleforeign
							.getSerializable(Parameter.SerializableWidgetFlushForeignName);

					Intent intentsforeign = new Intent();
					intentsforeign
							.setAction(Parameter.BROADCASE_WeatherWidgetForeignFlush);
					Bundle bundlforeign = new Bundle();
					bundlforeign
							.putSerializable(
									Parameter.SerializableBroadcastName,
									weatherforeign);
					intentsforeign.putExtras(bundlforeign);
					mcontext.sendBroadcast(intentsforeign);

					String str6 = mcontext.getResources().getString(
							R.string.Flushsuccess_foreign);
					if (toast != null) {
						toast.setText(str6);
					} else {
						toast = Toast.makeText(mcontext, str6,
								Toast.LENGTH_SHORT);
					}
					toast.show();

					break;
				case MSG_FOREIGNFAILURE:
					String str5 = mcontext.getResources().getString(
							R.string.Flushfailed_foreign);
					if (toast != null) {
						toast.setText(str5);
					} else {
						toast = Toast.makeText(mcontext, str5,
								Toast.LENGTH_SHORT);
					}
					toast.show();
					break;
				case MSG_FOREIGNNETWORK_FAILURE:
					String str9 = mcontext.getResources().getString(
							R.string.networkerror_foreign);
					if (toast != null) {
						toast.setText(str9);
					} else {
						toast = Toast.makeText(mcontext, str9,
								Toast.LENGTH_SHORT);
					}
					toast.show();
					break;
				case MSG_AUTOUPDATE_FOREIGNSUCCESS:
					Bundle bundleauto = (Bundle) msg.obj;
					Weather weatherauto = (Weather) bundleauto
							.getSerializable(Parameter.SerializableAutoUpdateForeignName);
					if (weatherauto != null && weatherauto.getList() != null
							&& weatherauto.getList().size() == 5) {
						Intent intent = new Intent();
						intent.setAction(Parameter.UPDATE_RESULT_FOREIGN);
						Bundle sendbundle = new Bundle();
						sendbundle.putSerializable(
								Parameter.SerializableBroadcastName,
								weatherauto);
						intent.putExtras(sendbundle);
						mcontext.sendBroadcast(intent);
						NumberWeatherwebservice.Update_Result_Flag = FLAG_UPDATE.UPDATE_SUCCES;
						String str7 = mcontext.getResources().getString(
								R.string.Flushsuccess_foreign);
						if (toast != null) {
							toast.setText(str7);
						} else {
							toast = Toast.makeText(mcontext, str7,
									Toast.LENGTH_SHORT);
						}
						toast.show();
					} else {
						NumberWeatherwebservice.Update_Result_Flag = FLAG_UPDATE.WEBSERVICE_ERROR;
					}
					break;
				}
			}
		};

		registerBroadCast();

		initviews(context);

		updateClockTime(context);

		if (Parameter.enable_google_version) {
			tv_cityname.setText(context.getResources().getString(
					R.string.default_foreigncity));
			tv_tempture.setText("51°/49°");
		}

		if (Parameter.enable_google_version) {
			if (sharepreference.getBoolean("numberweatherstate", false)) {
				Weather weather = NumberClockHelper
						.getWeatherForeign(sharepreference);
				if (weather != null && weather.getList() != null
						&& weather.getList().size() == 5) {
					updateWeatherShow(weather.getWeathercity(),
							weather.getWeathercode(), weather.getList().get(0)
									.getHightmp(), weather.getList().get(0)
									.getLowtmp());
				}
			}
		} else {
			if (sharepreference.getBoolean("inlandnumberweatherstate", false)) {
				WeatherEntity weatherentity = NumberClockHelper
						.getWeatherInland(sharepreference);
				if (weatherentity != null && weatherentity.getDetails() != null
						&& weatherentity.getDetails().size() == 5) {
					updateWeatherShow(weatherentity.getCity(),
							weatherentity.getCondition(), weatherentity
									.getDetails().get(0).getHight(),
							weatherentity.getDetails().get(0).getLow());
				}
			}
		}

		Intent intent = new Intent(context, TimeService.class);
		context.startService(intent);
		Intent dateintent = new Intent(context, NumberWeatherDataService.class);
		context.startService(dateintent);
	}

	private void registerBroadCast() {
		if (datereceiver == null) {
			datereceiver = new DateReceiver();
			IntentFilter intentfilter = new IntentFilter();
			intentfilter.addAction(Parameter.BROADCASE_UPDATE);
			intentfilter.addAction(Intent.ACTION_TIME_TICK);
			intentfilter.addAction(Parameter.BROADCASE_WeatherInLandSearch);
			intentfilter.addAction(Parameter.BROADCASE_WeatherCurveFlush);
			intentfilter.addAction(Parameter.BROADCASE_WeatherWidgetFlush);
			intentfilter.addAction(Parameter.UPDATE_RESULT);
			intentfilter.addAction(Parameter.BROADCASE_WeatherForeignSearch);
			intentfilter
					.addAction(Parameter.BROADCASE_WeatherCurveForeignFlush);
			intentfilter
					.addAction(Parameter.BROADCASE_WeatherWidgetForeignFlush);
			intentfilter.addAction(Parameter.UPDATE_RESULT_FOREIGN);
			intentfilter.addAction(Parameter.BROADCASE_WeatherCFFlush);
			mcontext.registerReceiver(datereceiver, intentfilter);
		}
	}

	private void initviews(Context context) {
		if (myclocknumberview == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			myclocknumberview = (RelativeLayout) inflater.inflate(
					R.layout.appwidget_numberclock, this);
		}

		if (myclocknumberview != null) {
			img_weathercode = (ImageView) myclocknumberview
					.findViewById(R.id.img_weathercode);
			img_weathercode.setOnClickListener(this);
			tv_tempture = (TextView) myclocknumberview
					.findViewById(R.id.tv_tempture);
			tv_tempture.setOnClickListener(this);
			tv_ampm = (TextView) myclocknumberview.findViewById(R.id.tv_ampm);
			iv_num1 = (ImageView) myclocknumberview.findViewById(R.id.iv_num1);
			iv_num2 = (ImageView) myclocknumberview.findViewById(R.id.iv_num2);
			iv_num3 = (ImageView) myclocknumberview.findViewById(R.id.iv_num3);
			iv_num4 = (ImageView) myclocknumberview.findViewById(R.id.iv_num4);
			iv_ncfluash = (ImageView) myclocknumberview
					.findViewById(R.id.iv_ncfluash);
			iv_ncfluash.setOnClickListener(this);
			tv_date = (TextView) myclocknumberview.findViewById(R.id.tv_date);
			tv_date.setOnClickListener(this);
			tv_cityname = (TextView) myclocknumberview
					.findViewById(R.id.tv_cityname);
			ll_time = (LinearLayout) myclocknumberview
					.findViewById(R.id.ll_time);
			ll_time.setOnClickListener(this);
			ll_city = (LinearLayout) myclocknumberview
					.findViewById(R.id.ll_city);
			ll_city.setOnClickListener(this);
		}
	}

	private void updateClockTime(Context context) {

		Calendar mCalendar = Calendar.getInstance();
		int mHeadHour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int mHeadMinute = mCalendar.get(Calendar.MINUTE);
		int mHeadYear = mCalendar.get(Calendar.YEAR);
		int mHeadMonth = mCalendar.get(Calendar.MONTH) + 1;
		int mHeadDay = mCalendar.get(Calendar.DAY_OF_MONTH);
		int mHeadWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
		boolean is_24hour = android.text.format.DateFormat
				.is24HourFormat(context);
		if (!is_24hour) {
			if (mHeadHour > 11) {
				if (mHeadHour != 12) {
					mHeadHour -= 12;
				}
				amOrpm = "PM";
			} else {
				if (mHeadHour == 0) {
					mHeadHour = 12;
				}
				amOrpm = "AM";
			}
		}
		if ((TIME_HOUR != mHeadHour) || (TIME_MINUTE != mHeadMinute)
				|| (TIME_WEEK != mHeadWeek) || (TIME_YEAR != mHeadYear)
				|| (TIME_MONTH != mHeadMonth) || (TIME_DAY != mHeadDay)
				|| (TIME_IS24 != is_24hour)) {
			TIME_HOUR = mHeadHour;
			TIME_MINUTE = mHeadMinute;
			TIME_WEEK = mHeadWeek;
			TIME_YEAR = mHeadYear;
			TIME_MONTH = mHeadMonth;
			TIME_DAY = mHeadDay;
			TIME_IS24 = is_24hour;

			iv_num1.setImageResource(NumberClockHelper.returnId(mHeadHour / 10));
			iv_num2.setImageResource(NumberClockHelper.returnId(mHeadHour % 10));
			iv_num3.setImageResource(NumberClockHelper
					.returnId(mHeadMinute / 10));
			iv_num4.setImageResource(NumberClockHelper
					.returnId(mHeadMinute % 10));

			String weekstring = NumberClockHelper.WeekChange(mHeadWeek);
			String AllCHar;
			if (Parameter.enable_google_version) {
				AllCHar = weekstring + mHeadDay + "/" + mHeadMonth + "/"
						+ mHeadYear;
			} else {
				AllCHar = mHeadYear + "/" + mHeadMonth + "/" + mHeadDay + "  "
						+ weekstring;
			}
			tv_date.setText(AllCHar);

			if (is_24hour) {
				tv_ampm.setVisibility(View.INVISIBLE);
			} else {
				tv_ampm.setVisibility(View.VISIBLE);
				tv_ampm.setText(amOrpm);
			}
		}
	}

	private void updateWeatherShow(String cityName, String weathercondition,
			String HighTmp, String lowTmp) {
		tv_tempture.setText(HighTmp + "°/" + lowTmp + "°");
		tv_cityname.setText(cityName);
		if (Parameter.enable_google_version) {
			img_weathercode.setImageResource(NumberClockHelper
					.codeForPath(weathercondition));
		} else {
			img_weathercode.setImageResource(NumberClockHelper
					.getResourceFromString(weathercondition));
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ll_time) {
			AlphaAnimSetTo(ll_time);
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(mcontext);
			String pkgname = prefs.getString("CLOCKpackagerName", null);
			if (pkgname != null) {
				Intent intent1 = mcontext.getPackageManager()
						.getLaunchIntentForPackage(pkgname);
				if (intent1 != null) {
					intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mcontext.startActivity(intent1);
				} else {
					Intent i2 = new Intent(Settings.ACTION_DATE_SETTINGS);
					i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mcontext.startActivity(i2);
				}
			} else {
				try {
					String packageName = null;
					SharedPreferences p = mcontext.getSharedPreferences(
							"iLoong.Widget.Clock", 0);
					packageName = p.getString("clock_package", null);
					if (packageName == null) {
						listPackages(mcontext);
						Editor editor = p.edit();
						if (pagList.size() != 0) {
							packageName = pagList.get(0);
							editor.putString("clock_package", packageName);
						}
						editor.commit();
					}
					PackageManager pm = mcontext.getPackageManager();
					if (packageName != null) {
						Intent intentss = pm
								.getLaunchIntentForPackage(packageName);
						if (intentss != null) {
							intentss.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							mcontext.startActivity(intentss);
						} else {
							Intent i2 = new Intent(
									Settings.ACTION_DATE_SETTINGS);
							i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							mcontext.startActivity(i2);
						}
					} else {
						Intent i2 = new Intent(Settings.ACTION_DATE_SETTINGS);
						mcontext.startActivity(i2);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} else if (v.getId() == R.id.ll_city || v.getId() == R.id.tv_date) {
			AlphaAnimSetTo(ll_city);
			AlphaAnimSetTo(tv_date);
			Intent cityintent = new Intent(mcontext, ContentActivity.class);
			cityintent.putExtra("currentState", 1);
			cityintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mcontext.startActivity(cityintent);
		} else if (v.getId() == R.id.img_weathercode
				|| v.getId() == R.id.tv_tempture) {
			AlphaAnimSetTo(img_weathercode);
			AlphaAnimSetTo(tv_tempture);
			Intent cityintent = new Intent(mcontext, ContentActivity.class);
			cityintent.putExtra("currentState", 0);
			cityintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mcontext.startActivity(cityintent);
		} else if (v.getId() == R.id.iv_ncfluash) {

			NumberClockHelper.RotationAnimal(iv_ncfluash);

			final String currentCityName = sharepreference.getString(
					Parameter.currentCityName, null);
			if (currentCityName != null) {
				if (Parameter.enable_google_version) {
					String str1 = mcontext.getResources().getString(
							R.string.flush_Prompt_foreign);
					if (toast != null) {
						toast.setText(str1);
					} else {
						toast = Toast.makeText(mcontext, str1,
								Toast.LENGTH_SHORT);
					}
					toast.show();
					new Thread(new Runnable() {
						@Override
						public void run() {
							String cityId = sharepreference.getString(
									Parameter.currentCityId, null);
							if (cityId != null) {
								CityResult cr = new CityResult(cityId,
										currentCityName,
										sharepreference.getString(
												Parameter.currentCountry, null));
								YahooClient.getWeatherInfo(cr, sharepreference
										.getString(Parameter.currentunit, "f"),
										mcontext, 3);
							}
						}
					}).start();
				} else {
					String str1 = mcontext.getResources().getString(
							R.string.flush_Prompt);
					if (toast != null) {
						toast.setText(str1);
					} else {
						toast = Toast.makeText(mcontext, str1,
								Toast.LENGTH_SHORT);
					}
					toast.show();
					new Thread(new Runnable() {
						@Override
						public void run() {
							CooeeClient.getWeatherInfo(mcontext,
									currentCityName, 3);
						}
					}).start();
				}
			} else {
				if (Parameter.enable_google_version) {
					String str1 = mcontext.getResources().getString(
							R.string.notsetcity_foreign);
					if (toast != null) {
						toast.setText(str1);
					} else {
						toast = Toast.makeText(mcontext, str1,
								Toast.LENGTH_SHORT);
					}
					toast.show();
				} else {
					String str1 = mcontext.getResources().getString(
							R.string.notsetcity);
					if (toast != null) {
						toast.setText(str1);
					} else {
						toast = Toast.makeText(mcontext, str1,
								Toast.LENGTH_SHORT);
					}
					toast.show();
				}
			}
		}
	}

	private void listPackages(Context mContext) {
		ArrayList<PInfo> apps = getInstalledApps(false, mContext);
		final int max = apps.size();
		for (int i = 0; i < max; i++) {
			item = new HashMap<String, Object>();
			int aa = apps.get(i).pname.length();
			if (aa > 11) {
				if (apps.get(i).pname.indexOf("clock") != -1
						|| apps.get(i).pname.indexOf("xtime") != -1) {
					if (!(apps.get(i).pname.indexOf("widget") != -1)) {
						try {
							PackageInfo pInfo = mContext.getPackageManager()
									.getPackageInfo(apps.get(i).pname, 0);
							if (isSystemApp(pInfo) || isSystemUpdateApp(pInfo)) {
								item.put("pname", apps.get(i).pname);
								item.put("appname", apps.get(i).appname);
								pagList.add(apps.get(i).pname);
							}
						} catch (Exception e) {
						}
					}
				}
			}
		}
	}

	private ArrayList<PInfo> getInstalledApps(boolean getSysPackages,
			Context mContext) {
		ArrayList<PInfo> res = new ArrayList<PInfo>();
		List<PackageInfo> packs = mContext.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			if ((!getSysPackages) && (p.versionName == null)) {
				continue;
			}
			PInfo newInfo = new PInfo();
			newInfo.appname = p.applicationInfo.loadLabel(
					mContext.getPackageManager()).toString();
			newInfo.pname = p.packageName;
			newInfo.versionName = p.versionName;
			newInfo.versionCode = p.versionCode;
			res.add(newInfo);
		}
		return res;
	}

	public boolean isSystemApp(PackageInfo pInfo) {
		return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
	}

	public boolean isSystemUpdateApp(PackageInfo pInfo) {
		return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
	}

	class PInfo {

		private String appname = "";
		private String pname = "";
		private String versionName = "";
		private int versionCode = 0;
	}

	private class DateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Parameter.BROADCASE_UPDATE)) {
				updateClockTime(context);
			} else if (action.equals(Intent.ACTION_TIME_TICK)) {
				Calendar c = Calendar.getInstance();// 可以对每个时间域单独修改
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);

				if (Parameter.enable_google_version) {
					if (sharepreference.getBoolean("numberweatherstate", false)) {
						Weather weather = NumberClockHelper
								.getWeatherForeign(sharepreference);
						if (weather != null && weather.getList() != null
								&& weather.getList().size() == 5) {
							if (hour == 18 && minute == 0) {
								updateWeatherShow(weather.getWeathercity(),
										weather.getWeathercode(), weather
												.getList().get(0).getHightmp(),
										weather.getList().get(0).getLowtmp());
							} else if (hour == 0 && minute == 0) {
								updateWeatherShow(weather.getWeathercity(),
										weather.getWeathercode(), weather
												.getList().get(1).getHightmp(),
										weather.getList().get(1).getLowtmp());
							}
						}
					}
				} else {
					if (sharepreference.getBoolean("inlandnumberweatherstate",
							false)) {
						WeatherEntity weatherentity = NumberClockHelper
								.getWeatherInland(sharepreference);
						if (weatherentity != null
								&& weatherentity.getDetails() != null
								&& weatherentity.getDetails().size() == 5) {
							if (hour == 18 && minute == 0) {
								updateWeatherShow(weatherentity.getCity(),
										weatherentity.getCondition(),
										weatherentity.getDetails().get(0)
												.getHight(), weatherentity
												.getDetails().get(0).getLow());
							} else if (hour == 0 && minute == 0) {
								updateWeatherShow(weatherentity.getCity(),
										weatherentity.getCondition(),
										weatherentity.getDetails().get(1)
												.getHight(), weatherentity
												.getDetails().get(1).getLow());
							}
						}
					}
				}
			} else if (action.equals(Parameter.BROADCASE_DateClick)) {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(context);
				String pkgname = prefs.getString("CLOCKpackagerName", null);
				if (pkgname != null) {
					Intent intent1 = context.getPackageManager()
							.getLaunchIntentForPackage(pkgname);
					if (intent1 != null) {
						intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent1);
					} else {
						Intent i2 = new Intent(Settings.ACTION_DATE_SETTINGS);
						i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(i2);
					}
				} else {
					try {
						String packageName = null;
						SharedPreferences p = context.getSharedPreferences(
								"iLoong.Widget.Clock", 0);
						packageName = p.getString("clock_package", null);
						if (packageName == null) {
							listPackages(context);
							Editor editor = p.edit();
							if (pagList.size() != 0) {
								packageName = pagList.get(0);
								editor.putString("clock_package", packageName);
							}
							editor.commit();
						}
						PackageManager pm = context.getPackageManager();
						if (packageName != null) {
							Intent intentss = pm
									.getLaunchIntentForPackage(packageName);
							if (intentss != null) {
								intentss.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(intentss);
							} else {
								Intent i2 = new Intent(
										Settings.ACTION_DATE_SETTINGS);
								i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(i2);
							}
						} else {
							Intent i2 = new Intent(
									Settings.ACTION_DATE_SETTINGS);
							context.startActivity(i2);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} else if (action.equals(Parameter.BROADCASE_WeatherInLandSearch)
					|| action.equals(Parameter.BROADCASE_WeatherCurveFlush)
					|| action.equals(Parameter.BROADCASE_WeatherWidgetFlush)
					|| action.equals(Parameter.UPDATE_RESULT)) {
				Bundle bundle = intent.getExtras();
				WeatherEntity inlandweather = (WeatherEntity) bundle
						.getSerializable(Parameter.SerializableBroadcastName);
				if (inlandweather != null && inlandweather.getDetails() != null
						&& inlandweather.getDetails().size() == 5) {
					updateWeatherShow(inlandweather.getCity(),
							inlandweather.getCondition(), inlandweather
									.getDetails().get(0).getHight(),
							inlandweather.getDetails().get(0).getLow());
					NumberClockHelper.setWeatherInland(sharepreference,
							inlandweather);
				}
			} else if (action.equals(Parameter.BROADCASE_WeatherForeignSearch)
					|| action
							.equals(Parameter.BROADCASE_WeatherCurveForeignFlush)
					|| action
							.equals(Parameter.BROADCASE_WeatherWidgetForeignFlush)
					|| action.equals(Parameter.UPDATE_RESULT_FOREIGN)
					|| action.equals(Parameter.BROADCASE_WeatherCFFlush)) {
				Bundle bundle = intent.getExtras();
				Weather weather = (Weather) bundle
						.getSerializable(Parameter.SerializableBroadcastName);
				if (weather != null && weather.getList() != null
						&& weather.getList().size() == 5) {
					updateWeatherShow(weather.getWeathercity(),
							weather.getWeathercode(), weather.getList().get(0)
									.getHightmp(), weather.getList().get(0)
									.getLowtmp());
					NumberClockHelper.setWeatherForeign(sharepreference,
							weather);
				}
			}
		}
	}

	public void AlphaAnimSetTo(View view) {
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "alpha", 0.3f);
		ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "alpha", 1f);
		AnimatorSet animset = new AnimatorSet();
		animset.play(anim1).before(anim2);
		animset.setDuration(300);
		animset.start();
	}
}
