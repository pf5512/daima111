package com.iLoong.NumberClock;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.iLoong.NumberClock.common.CityResult;
import com.iLoong.NumberClock.common.CooeeClient;
import com.iLoong.NumberClock.common.NumberClockHelper;
import com.iLoong.NumberClock.common.Parameter;
import com.iLoong.NumberClock.common.Weather;
import com.iLoong.NumberClock.common.WeatherEntity;
import com.iLoong.NumberClock.common.YahooClient;
import com.iLoong.NumberClockEx.R;

public class WeatherCurveView extends RelativeLayout {
	private RelativeLayout weathercurveview = null;
	private ImageView iv_curve;
	private ImageView iv_flush;
	private ImageView firstIcon;
	private ImageView secondIcon;
	private ImageView thirdIcon;
	private ImageView forthIcon;
	private ImageView fiveIcon;

	private ImageView ivweatherconditionname;

	private ImageView ivweekname;
	private RelativeLayout rl_tempertureunit;
	private SharedPreferences sharepreference = null;
	private CustomProcessDialog progressDialog = null;

	public static Handler mHandler = null;
	public static final int MSG_SUCCESS = 4;
	public static final int MSG_FAILURE = 5;
	public static final int MSG_NETWORK_FAILURE = 6;
	public static final int MSG_FOREIGNSUCCESS = 7;
	public static final int MSG_FOREIGNFAILURE = 8;
	public static final int MSG_FOREIGNNETWORK_FAILURE = 9;
	public static final int MSG_CHANGECFSUCCESS = 10;

	private Drawable drawCurrentC;
	private Drawable drawCurrentF;
	private ImageView iv_corf;

	private Toast toast;

	public WeatherCurveView(final Context context) {
		super(context);
		initviews(context);

		sharepreference = PreferenceManager
				.getDefaultSharedPreferences(context);

		drawCurrentC = this.getResources().getDrawable(R.drawable.current_c);
		drawCurrentF = this.getResources().getDrawable(R.drawable.current_f);
		mHandler = new Handler() {
			public void handleMessage(final Message msg) {
				switch (msg.what) {
				case MSG_SUCCESS:
					Bundle bundles = (Bundle) msg.obj;
					WeatherEntity weathers = (WeatherEntity) bundles
							.getSerializable(Parameter.SerializableCurveFlushName);

					Intent intents = new Intent();
					intents.setAction(Parameter.BROADCASE_WeatherCurveFlush);
					Bundle myBundle = new Bundle();
					myBundle.putSerializable(
							Parameter.SerializableBroadcastName, weathers);
					intents.putExtras(myBundle);
					context.sendBroadcast(intents);

					if (progressDialog != null) {
						progressDialog.dismiss();
					}

					AlphaAnimSetTo();

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
				case MSG_FOREIGNSUCCESS:
					Bundle bundleforeign = (Bundle) msg.obj;
					Weather weatherforeign = (Weather) bundleforeign
							.getSerializable(Parameter.SerializableCurveFlushForeignName);

					Intent intentforeign = new Intent();
					intentforeign
							.setAction(Parameter.BROADCASE_WeatherCurveForeignFlush);
					Bundle bundle = new Bundle();
					bundle.putSerializable(Parameter.SerializableBroadcastName,
							weatherforeign);
					intentforeign.putExtras(bundle);
					context.sendBroadcast(intentforeign);

					if (progressDialog != null) {
						progressDialog.dismiss();
					}

					AlphaAnimSetTo();

					break;
				case MSG_FOREIGNFAILURE:
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
					String str3 = context.getResources().getString(
							R.string.Flushfailed_foreign);
					if (toast != null) {
						toast.setText(str3);
					} else {
						toast = Toast.makeText(context, str3,
								Toast.LENGTH_SHORT);
					}
					toast.show();
					break;
				case MSG_FOREIGNNETWORK_FAILURE:
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
				case MSG_CHANGECFSUCCESS:
					Bundle bundlecf = (Bundle) msg.obj;
					Weather weathercf = (Weather) bundlecf
							.getSerializable(Parameter.SerializableChangeCF);

					Intent intentcf = new Intent();
					intentcf.setAction(Parameter.BROADCASE_WeatherCFFlush);
					Bundle bundlecfcf = new Bundle();
					bundlecfcf.putSerializable(
							Parameter.SerializableBroadcastName, weathercf);
					intentcf.putExtras(bundlecfcf);
					context.sendBroadcast(intentcf);

					if (progressDialog != null) {
						progressDialog.dismiss();
					}

					AlphaAnimSetTo();

					break;
				}
			}
		};

		if (weathercurveview != null) {
			iv_curve = (ImageView) weathercurveview.findViewById(R.id.iv_curve);
			iv_flush = (ImageView) weathercurveview.findViewById(R.id.iv_flush);
			firstIcon = (ImageView) weathercurveview
					.findViewById(R.id.iv_firsticon);
			secondIcon = (ImageView) weathercurveview
					.findViewById(R.id.iv_secondicon);
			thirdIcon = (ImageView) weathercurveview
					.findViewById(R.id.iv_thirdicon);
			forthIcon = (ImageView) weathercurveview
					.findViewById(R.id.iv_forthicon);
			fiveIcon = (ImageView) weathercurveview
					.findViewById(R.id.iv_fiveicon);
			rl_tempertureunit = (RelativeLayout) weathercurveview
					.findViewById(R.id.rl_tempertureunit);
			iv_corf = (ImageView) weathercurveview.findViewById(R.id.iv_corf);
			if ("f".equals(sharepreference
					.getString(Parameter.currentunit, "f"))) {
				iv_corf.setImageDrawable(drawCurrentF);
			} else {
				iv_corf.setImageDrawable(drawCurrentC);
			}
			if (Parameter.enable_google_version) {
				rl_tempertureunit.setVisibility(View.VISIBLE);
				iv_corf.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						final String currentCityName = sharepreference
								.getString(Parameter.currentCityName, null);
						final String cityId = sharepreference.getString(
								Parameter.currentCityId, null);
						final String coutry = sharepreference.getString(
								Parameter.currentCountry, null);
						if (currentCityName != null && cityId != null
								&& coutry != null) {
							if ("f".equals(sharepreference.getString(
									Parameter.currentunit, "f"))) {
								iv_corf.setImageDrawable(drawCurrentC);
								sharepreference.edit()
										.putString(Parameter.currentunit, "c")
										.commit();

							} else {
								iv_corf.setImageDrawable(drawCurrentF);
								sharepreference.edit()
										.putString(Parameter.currentunit, "f")
										.commit();
							}

							progressDialog = CustomProcessDialog
									.createDialog(context);
							progressDialog.show();
							new Thread(new Runnable() {
								@Override
								public void run() {
									CityResult cr = new CityResult(cityId,
											currentCityName, coutry);
									YahooClient.getWeatherInfo(
											cr,
											sharepreference.getString(
													Parameter.currentunit, "f"),
											context, 5);
								}
							}).start();
						} else {
							String str4 = context.getResources().getString(
									R.string.notsetcity_foreign);
							if (toast != null) {
								toast.setText(str4);
							} else {
								toast = Toast.makeText(context, str4,
										Toast.LENGTH_SHORT);
							}
							toast.show();
						}

					}
				});
			} else {
				rl_tempertureunit.setVisibility(View.INVISIBLE);
			}
			ivweatherconditionname = (ImageView) findViewById(R.id.iv_weatherconditionname);
			ivweekname = (ImageView) findViewById(R.id.iv_weekname);

			if (Parameter.enable_google_version) {
				iv_curve.setImageBitmap(NumberClockHelper.drawCurve(context,
						51, 58, 48, 48, 40, 49, 56, 44, 46, 36));
				iv_curve.setAlpha(0f);
				AlphaAnimToShow();

				ivweatherconditionname
						.setImageBitmap(NumberClockHelper
								.drawString(
										context,
										context.getResources()
												.getString(
														R.string.defaultWeatherconditionName_foreign),
										context.getResources()
												.getString(
														R.string.defaultWeatherconditionName_foreign),
										context.getResources()
												.getString(
														R.string.defaultWeatherconditionName_foreign),
										context.getResources()
												.getString(
														R.string.defaultWeatherconditionName_foreign),
										context.getResources()
												.getString(
														R.string.defaultWeatherconditionName_foreign)));
				ivweekname.setImageBitmap(NumberClockHelper.drawString(
						context,
						context.getResources().getString(
								R.string.firstweek_foreign),
						context.getResources().getString(
								R.string.secondweek_foreign),
						context.getResources().getString(
								R.string.thirdweek_foreign),
						context.getResources().getString(
								R.string.forthweek_foreign),
						context.getResources().getString(
								R.string.fiveweek_foreign)));

			} else {
				iv_curve.setImageBitmap(NumberClockHelper.drawCurve(context,
						26, 28, 32, 28, 40, 22, 16, 18, 16, 36));
				iv_curve.setAlpha(0f);
				AlphaAnimToShow();

				ivweatherconditionname.setImageBitmap(NumberClockHelper
						.drawString(
								context,
								context.getResources().getString(
										R.string.defaultWeatherconditionName),
								context.getResources().getString(
										R.string.defaultWeatherconditionName),
								context.getResources().getString(
										R.string.defaultWeatherconditionName),
								context.getResources().getString(
										R.string.defaultWeatherconditionName),
								context.getResources().getString(
										R.string.defaultWeatherconditionName)));
				ivweekname.setImageBitmap(NumberClockHelper.drawString(context,
						context.getResources().getString(R.string.firstweek),
						context.getResources().getString(R.string.secondweek),
						context.getResources().getString(R.string.thirdweek),
						context.getResources().getString(R.string.forthweek),
						context.getResources().getString(R.string.fiveweek)));
			}

			iv_flush.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					NumberClockHelper.RotationAnimal(iv_flush);
					final String currentCityName = sharepreference.getString(
							Parameter.currentCityName, null);
					if (currentCityName != null) {
						progressDialog = CustomProcessDialog
								.createDialog(context);
						progressDialog.show();
						new Thread(new Runnable() {
							@Override
							public void run() {
								if (Parameter.enable_google_version) {
									String cityId = sharepreference.getString(
											Parameter.currentCityId, null);
									if (cityId != null) {
										CityResult cr = new CityResult(
												cityId,
												currentCityName,
												sharepreference
														.getString(
																Parameter.currentCountry,
																null));
										YahooClient.getWeatherInfo(cr,
												sharepreference.getString(
														Parameter.currentunit,
														"f"), context, 2);
									}
								} else {
									CooeeClient.getWeatherInfo(context,
											currentCityName, 2);
								}
							}
						}).start();
					} else {
						String str4 = context.getResources().getString(
								R.string.notsetcity);
						if (toast != null) {
							toast.setText(str4);
						} else {
							toast = Toast.makeText(context, str4,
									Toast.LENGTH_SHORT);
						}
						toast.show();
					}
				}
			});

		}
	}

	private void initviews(Context context) {
		if (weathercurveview == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			weathercurveview = (RelativeLayout) inflater.inflate(
					R.layout.weathercurvelayout, this);
		}
	}

	public void flushAllViews(Context context, WeatherEntity weatherentity) {
		if (weatherentity != null && weatherentity.getDetails() != null
				&& weatherentity.getDetails().size() == 5) {
			firstIcon.setImageResource(NumberClockHelper
					.SmallgetResourceFromString(weatherentity.getDetails()
							.get(0).getCondition()));
			secondIcon.setImageResource(NumberClockHelper
					.SmallgetResourceFromString(weatherentity.getDetails()
							.get(1).getCondition()));
			thirdIcon.setImageResource(NumberClockHelper
					.SmallgetResourceFromString(weatherentity.getDetails()
							.get(2).getCondition()));
			forthIcon.setImageResource(NumberClockHelper
					.SmallgetResourceFromString(weatherentity.getDetails()
							.get(3).getCondition()));
			fiveIcon.setImageResource(NumberClockHelper
					.SmallgetResourceFromString(weatherentity.getDetails()
							.get(4).getCondition()));

			ivweatherconditionname.setImageBitmap(NumberClockHelper.drawString(
					context,
					NumberClockHelper.StringChangeInland(weatherentity
							.getDetails().get(0).getCondition()),
					NumberClockHelper.StringChangeInland(weatherentity
							.getDetails().get(1).getCondition()),
					NumberClockHelper.StringChangeInland(weatherentity
							.getDetails().get(2).getCondition()),
					NumberClockHelper.StringChangeInland(weatherentity
							.getDetails().get(3).getCondition()),
					NumberClockHelper.StringChangeInland(weatherentity
							.getDetails().get(4).getCondition())));

			ivweekname.setImageBitmap(NumberClockHelper.drawString(
					context,
					NumberClockHelper.WeekChangeinCurve(context, weatherentity
							.getDetails().get(0).getDayOfWeek()),
					NumberClockHelper.WeekChangeinCurve(context, weatherentity
							.getDetails().get(1).getDayOfWeek()),
					NumberClockHelper.WeekChangeinCurve(context, weatherentity
							.getDetails().get(2).getDayOfWeek()),
					NumberClockHelper.WeekChangeinCurve(context, weatherentity
							.getDetails().get(3).getDayOfWeek()),
					NumberClockHelper.WeekChangeinCurve(context, weatherentity
							.getDetails().get(4).getDayOfWeek())));
			iv_curve.setImageBitmap(NumberClockHelper.drawCurve(context,
					Integer.parseInt(weatherentity.getDetails().get(0)
							.getHight()), Integer.parseInt(weatherentity
							.getDetails().get(1).getHight()), Integer
							.parseInt(weatherentity.getDetails().get(2)
									.getHight()), Integer
							.parseInt(weatherentity.getDetails().get(3)
									.getHight()), Integer
							.parseInt(weatherentity.getDetails().get(4)
									.getHight()), Integer
							.parseInt(weatherentity.getDetails().get(0)
									.getLow()), Integer.parseInt(weatherentity
							.getDetails().get(1).getLow()), Integer
							.parseInt(weatherentity.getDetails().get(2)
									.getLow()), Integer.parseInt(weatherentity
							.getDetails().get(3).getLow()), Integer
							.parseInt(weatherentity.getDetails().get(4)
									.getLow())));
		}
	}

	public void flushAllViewsForeign(Context context, Weather weather) {
		if (weather != null && weather.getList() != null
				&& weather.getList().size() == 5) {
			firstIcon
					.setImageResource(NumberClockHelper
							.codeForSmallPath(weather.getList().get(0)
									.getWeathercode()));
			secondIcon
					.setImageResource(NumberClockHelper
							.codeForSmallPath(weather.getList().get(1)
									.getWeathercode()));
			thirdIcon
					.setImageResource(NumberClockHelper
							.codeForSmallPath(weather.getList().get(2)
									.getWeathercode()));
			forthIcon
					.setImageResource(NumberClockHelper
							.codeForSmallPath(weather.getList().get(3)
									.getWeathercode()));
			fiveIcon.setImageResource(NumberClockHelper
					.codeForSmallPath(weather.getList().get(4).getWeathercode()));

			ivweatherconditionname.setImageBitmap(NumberClockHelper.drawString(
					context,
					NumberClockHelper.StringChange(weather.getList().get(0)
							.getWeathercondition()),
					NumberClockHelper.StringChange(weather.getList().get(1)
							.getWeathercondition()),
					NumberClockHelper.StringChange(weather.getList().get(2)
							.getWeathercondition()),
					NumberClockHelper.StringChange(weather.getList().get(3)
							.getWeathercondition()),
					NumberClockHelper.StringChange(weather.getList().get(4)
							.getWeathercondition())));

			ivweekname.setImageBitmap(NumberClockHelper.drawString(context,
					weather.getList().get(0).getWeatherweek() + ".", weather
							.getList().get(1).getWeatherweek()
							+ ".", weather.getList().get(2).getWeatherweek()
							+ ".", weather.getList().get(3).getWeatherweek()
							+ ".", weather.getList().get(4).getWeatherweek()
							+ "."));

			iv_curve.setImageBitmap(NumberClockHelper.drawCurve(context,
					Integer.parseInt(weather.getList().get(0).getHightmp()),
					Integer.parseInt(weather.getList().get(1).getHightmp()),
					Integer.parseInt(weather.getList().get(2).getHightmp()),
					Integer.parseInt(weather.getList().get(3).getHightmp()),
					Integer.parseInt(weather.getList().get(4).getHightmp()),
					Integer.parseInt(weather.getList().get(0).getLowtmp()),
					Integer.parseInt(weather.getList().get(1).getLowtmp()),
					Integer.parseInt(weather.getList().get(2).getLowtmp()),
					Integer.parseInt(weather.getList().get(3).getLowtmp()),
					Integer.parseInt(weather.getList().get(4).getLowtmp())));
		}
	}

	public void AlphaAnimSetTo() {
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(iv_curve, "alpha", 0f);
		ObjectAnimator anim2 = ObjectAnimator.ofFloat(iv_curve, "alpha", 1f);
		AnimatorSet animset = new AnimatorSet();
		animset.play(anim1).before(anim2);
		animset.setDuration(700);
		animset.start();
	}

	public void AlphaAnimToShow() {
		ObjectAnimator anim = ObjectAnimator.ofFloat(iv_curve, "alpha", 1f);
		anim.setDuration(1000);
		anim.start();
	}
}
