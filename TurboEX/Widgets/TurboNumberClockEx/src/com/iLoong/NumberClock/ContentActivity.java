package com.iLoong.NumberClock;

import java.util.ArrayList;
import java.util.List;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.iLoong.NumberClock.common.NumberClockHelper;
import com.iLoong.NumberClock.common.Parameter;
import com.iLoong.NumberClock.common.Weather;
import com.iLoong.NumberClock.common.WeatherEntity;
import com.iLoong.NumberClockEx.R;

public class ContentActivity extends Activity {
	private ViewPager viewpager;
	private PreviewAdapter previewadapter;
	private ImageView curve;
	private ImageView city;
	private DateReceiver datereceiver = null;
	private SharedPreferences sharepreference = null;
	private CityFinderView cityfinderview = null;
	private WeatherCurveView weathercurveview = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contentlayout);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		sharepreference = PreferenceManager.getDefaultSharedPreferences(this);
		curve = (ImageView) findViewById(R.id.iv_indicatorcurve);
		city = (ImageView) findViewById(R.id.iv_indicatorcity);

		viewpager = (ViewPager) findViewById(R.id.viewpager);
		previewadapter = new PreviewAdapter(this);
		viewpager.setAdapter(previewadapter);

		// try {
		// Field mScroller;
		// mScroller = ViewPager.class.getDeclaredField("mScroller");
		// mScroller.setAccessible(true);
		// FixedSpeedScroller scroller = new FixedSpeedScroller(
		// viewpager.getContext());
		// mScroller.set(viewpager, scroller);
		// } catch (NoSuchFieldException e) {
		// } catch (IllegalArgumentException e) {
		// } catch (IllegalAccessException e) {
		// }

		Intent intent = getIntent();
		int flag = intent.getIntExtra("currentState", 0);

		if (Parameter.enable_google_version) {
			if (sharepreference.getBoolean("numberweatherstate", false)) {
				viewpager.setCurrentItem(flag);
				if (flag == 0) {
					AnimSetTo(ContentActivity.this, city, curve, 0f);
				} else if (flag == 1) {
					AnimSetTo(ContentActivity.this, curve, city, -44f);
				}
			} else {
				viewpager.setCurrentItem(1);
				AnimSetTo(ContentActivity.this, curve, city, -44f);
			}
		} else {
			if (sharepreference.getBoolean("inlandnumberweatherstate", false)) {
				viewpager.setCurrentItem(flag);
				if (flag == 0) {
					AnimSetTo(ContentActivity.this, city, curve, 0f);
				} else if (flag == 1) {
					AnimSetTo(ContentActivity.this, curve, city, -44f);
				}
			} else {
				viewpager.setCurrentItem(1);
				AnimSetTo(ContentActivity.this, curve, city, -44f);
			}
		}
		viewpager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {
						if (position == 0) {
							AnimSetTo(ContentActivity.this, city, curve, 0f);
						} else if (position == 1) {
							AnimSetTo(ContentActivity.this, curve, city, -44f);
						}
					}
				});

		if (Parameter.enable_google_version) {
			if (sharepreference.getBoolean("numberweatherstate", false)) {
				Weather weather = NumberClockHelper
						.getWeatherForeign(sharepreference);
				if (weather != null && weather.getList() != null
						&& weather.getList().size() == 5) {
					if (weathercurveview != null) {
						weathercurveview.flushAllViewsForeign(this, weather);
					}
				}
			}
		} else {
			if (sharepreference.getBoolean("inlandnumberweatherstate", false)) {
				WeatherEntity weatherentity = NumberClockHelper
						.getWeatherInland(sharepreference);
				if (weatherentity != null && weatherentity.getDetails() != null
						&& weatherentity.getDetails().size() == 5) {
					if (weathercurveview != null) {
						weathercurveview.flushAllViews(this, weatherentity);
					}
				}
			}
		}

	}

	@Override
	protected void onStart() {
		if (datereceiver == null) {
			datereceiver = new DateReceiver();
			IntentFilter intentfilter = new IntentFilter();
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
			this.registerReceiver(datereceiver, intentfilter);
		}
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		if (datereceiver != null) {
			this.unregisterReceiver(datereceiver);
		}
		super.onDestroy();
	}

	public class PreviewAdapter extends PagerAdapter {

		private List<RelativeLayout> layoutArray;

		public PreviewAdapter(Context context) {
			layoutArray = new ArrayList<RelativeLayout>();
			cityfinderview = new CityFinderView(context);
			weathercurveview = new WeatherCurveView(context);
			if (cityfinderview != null && weathercurveview != null) {
				layoutArray.add(weathercurveview);
				layoutArray.add(cityfinderview);
			}
		}

		@Override
		public int getCount() {
			return layoutArray.size();
		}

		@Override
		public Object instantiateItem(View collection, int position) {
			((ViewPager) collection).addView(layoutArray.get(position));
			return layoutArray.get(position);
		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView(layoutArray.get(position));
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}

	private class DateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Parameter.BROADCASE_WeatherInLandSearch)) {
				viewpager.setCurrentItem(0);
				AnimSetTo(ContentActivity.this, city, curve, 0f);
				Bundle bundle = intent.getExtras();
				WeatherEntity inlandweather = (WeatherEntity) bundle
						.getSerializable(Parameter.SerializableBroadcastName);
				if (inlandweather != null && inlandweather.getDetails() != null
						&& inlandweather.getDetails().size() == 5) {
					if (weathercurveview != null) {
						weathercurveview.flushAllViews(context, inlandweather);
					}
				}
			} else if (action.equals(Parameter.BROADCASE_WeatherCurveFlush)
					|| action.equals(Parameter.BROADCASE_WeatherWidgetFlush)
					|| action.equals(Parameter.UPDATE_RESULT)) {
				Bundle bundle = intent.getExtras();
				WeatherEntity inlandweather = (WeatherEntity) bundle
						.getSerializable(Parameter.SerializableBroadcastName);
				if (inlandweather != null && inlandweather.getDetails() != null
						&& inlandweather.getDetails().size() == 5) {
					if (weathercurveview != null) {
						weathercurveview.flushAllViews(context, inlandweather);
					}
				}
			} else if (action.equals(Parameter.BROADCASE_WeatherForeignSearch)) {
				viewpager.setCurrentItem(0);
				AnimSetTo(ContentActivity.this, city, curve, 0f);
				Bundle bundle = intent.getExtras();
				Weather weather = (Weather) bundle
						.getSerializable(Parameter.SerializableBroadcastName);
				if (weather != null && weather.getList() != null
						&& weather.getList().size() == 5) {
					if (weathercurveview != null) {
						weathercurveview.flushAllViewsForeign(context, weather);
					}
				}
			} else if (action
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
					if (weathercurveview != null) {
						weathercurveview.flushAllViewsForeign(context, weather);
					}
				}
			}
		}
	}

	public void AnimSetTo(Context context, View view1, View view2, float x) {
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(view1, "alpha", 1f, 0.3f);
		ObjectAnimator anim2 = ObjectAnimator
				.ofFloat(view1, "scaleX", 1f, 0.5f);
		ObjectAnimator anim3 = ObjectAnimator
				.ofFloat(view1, "scaleY", 1f, 0.5f);
		ObjectAnimator anim4 = ObjectAnimator.ofFloat(view1, "translationX",
				NumberClockHelper.dip2px(context, x));
		view2.setAlpha(0.3f);
		ObjectAnimator anim5 = ObjectAnimator.ofFloat(view2, "alpha", 0.3f, 1f);
		ObjectAnimator anim6 = ObjectAnimator
				.ofFloat(view2, "scaleX", 0.5f, 1f);
		ObjectAnimator anim7 = ObjectAnimator
				.ofFloat(view2, "scaleY", 0.5f, 1f);
		ObjectAnimator anim8 = ObjectAnimator.ofFloat(view2, "translationX",
				NumberClockHelper.dip2px(context, x));

		AnimatorSet animset = new AnimatorSet();
		animset.play(anim1).with(anim2).with(anim3).with(anim4).with(anim5)
				.with(anim6).with(anim7).with(anim8);
		animset.setDuration(500);
		animset.start();
	}

	public class FixedSpeedScroller extends Scroller {

		private int mDuration = 500;

		public FixedSpeedScroller(Context context) {
			super(context);
		}

		public FixedSpeedScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		public FixedSpeedScroller(Context context, Interpolator interpolator,
				boolean flywheel) {
			super(context, interpolator, flywheel);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy,
				int duration) {
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {
			super.startScroll(startX, startY, dx, dy, mDuration);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}
		return super.onKeyDown(keyCode, event);
	}

}
