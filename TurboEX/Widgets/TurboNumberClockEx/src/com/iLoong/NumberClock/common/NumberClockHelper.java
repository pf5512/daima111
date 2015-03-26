package com.iLoong.NumberClock.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.iLoong.NumberClockEx.R;

public class NumberClockHelper {
	public static int returnId(int num) {
		int id = 0;
		if (num == 0) {
			id = R.drawable.num_0;
		} else if (num == 1) {
			id = R.drawable.num_1;
		} else if (num == 2) {
			id = R.drawable.num_2;
		} else if (num == 3) {
			id = R.drawable.num_3;
		} else if (num == 4) {
			id = R.drawable.num_4;
		} else if (num == 5) {
			id = R.drawable.num_5;
		} else if (num == 6) {
			id = R.drawable.num_6;
		} else if (num == 7) {
			id = R.drawable.num_7;
		} else if (num == 8) {
			id = R.drawable.num_8;
		} else if (num == 9) {
			id = R.drawable.num_9;
		}
		return id;
	}

	public static String StringChange(String str) {
		String[] names = str.split(" ");
		if (names.length == 1) {
			return str;
		} else if (names.length == 2) {
			return names[1];
		} else {
			return str;
		}
	}

	public static String StringChangeInland(String str) {
		String[] names = str.split("转");
		if (names.length == 1) {
			return str;
		} else if (names.length == 2) {
			return names[1];
		} else {
			return str;
		}
	}

	public static String WeekChange(int week) {
		String weekstring = null;
		if (!Parameter.enable_google_version) {
			switch (week) {
			case 1:
				weekstring = "星期日";
				break;
			case 2:
				weekstring = "星期一";
				break;
			case 3:
				weekstring = "星期二";
				break;
			case 4:
				weekstring = "星期三";
				break;
			case 5:
				weekstring = "星期四";
				break;
			case 6:
				weekstring = "星期五";
				break;
			case 7:
				weekstring = "星期六";
				break;
			default:
				weekstring = "未知";
				break;
			}
		} else {
			switch (week) {
			case 1:
				weekstring = "Sun.";
				break;
			case 2:
				weekstring = "Mon.";
				break;
			case 3:
				weekstring = "Tues.";
				break;
			case 4:
				weekstring = "Wed.";
				break;
			case 5:
				weekstring = "Thur.";
				break;
			case 6:
				weekstring = "Fri.";
				break;
			case 7:
				weekstring = "Sat.";
				break;
			default:
				weekstring = "unknow";
				break;
			}
		}
		return weekstring;
	}

	public static String WeekChangeinCurve(Context context, int week) {
		String weekstring = null;
		switch (week) {
		case 0:
			weekstring = context.getResources().getString(R.string.sevenweek);
			break;
		case 1:
			weekstring = context.getResources().getString(R.string.firstweek);
			break;
		case 2:
			weekstring = context.getResources().getString(R.string.secondweek);
			break;
		case 3:
			weekstring = context.getResources().getString(R.string.thirdweek);
			break;
		case 4:
			weekstring = context.getResources().getString(R.string.forthweek);
			break;
		case 5:
			weekstring = context.getResources().getString(R.string.fiveweek);
			break;
		case 6:
			weekstring = context.getResources().getString(R.string.sixweek);
			break;
		default:
			weekstring = context.getResources().getString(R.string.unkonwweek);
			break;
		}
		return weekstring;
	}

	public static boolean isHaveInternet(Context context) {
		try {
			ConnectivityManager manger = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manger.getActiveNetworkInfo();
			return (info != null && info.isConnected());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static int getResourceFromString(String weathercode) {
		int ResorceId = 0;
		Calendar c = Calendar.getInstance();// 可以对每个时间域单独修改
		int hour = c.get(Calendar.HOUR_OF_DAY);
		String weathericon = null;
		String[] names = weathercode.split("转");
		if (names.length == 2) {
			if (hour >= 18) {
				weathericon = names[1];
			} else {
				weathericon = names[0];
			}
		} else {
			weathericon = weathercode;
		}
		if (hour >= 18) {
			if (weathericon.contains("云") || weathericon.contains("阴")) {
				ResorceId = R.drawable.bigweather_latecloudy;
			} else if (weathericon.contains("雨")) {
				ResorceId = R.drawable.bigweather_rainshowerslate;
			} else if (weathericon.contains("雪")) {
				ResorceId = R.drawable.bigweather_snowshowerslate;
			} else {
				ResorceId = R.drawable.bigweather_reaching;
			}
		} else {
			if (weathericon.contains("霾")) {
				ResorceId = R.drawable.bigweather_mai;
			} else if (weathericon.contains("飓风")) {
				ResorceId = R.drawable.bigweather_jufeng;
			} else if (weathericon.contains("冰雹")) {
				ResorceId = R.drawable.bigweather_bingbao;
			} else if (weathericon.contains("暴雪")) {
				ResorceId = R.drawable.bigweather_baosnow;
			} else if (weathericon.contains("雷阵雨")) {
				ResorceId = R.drawable.bigweather_thunderstorms;
			} else if (weathericon.contains("雨加雪")) {
				ResorceId = R.drawable.bigweather_sleet;
			} else if (weathericon.contains("阵雨")) {
				ResorceId = R.drawable.bigweather_zhenrain;
			} else if (weathericon.contains("阵雪")) {
				ResorceId = R.drawable.bigweather_zhensnow;
			} else if (weathericon.contains("沙")) {
				ResorceId = R.drawable.bigweather_sand;
			} else if (weathericon.contains("雾")) {
				ResorceId = R.drawable.bigweather_fog;
			} else if (weathericon.contains("大雪")) {
				ResorceId = R.drawable.bigweather_bigsnow;
			} else if (weathericon.contains("大雨") || weathericon.contains("暴雨")) {
				ResorceId = R.drawable.bigweather_bigrain;
			} else if (weathericon.contains("小雪")) {
				ResorceId = R.drawable.bigweather_smallsnow;
			} else if (weathericon.contains("小雨")) {
				ResorceId = R.drawable.bigweather_smallrain;
			} else if (weathericon.contains("中雪")) {
				ResorceId = R.drawable.bigweather_middlesnow;
			} else if (weathericon.contains("中雨")) {
				ResorceId = R.drawable.bigweather_middlerain;
			} else if (weathericon.contains("云")) {
				ResorceId = R.drawable.bigweather_cloudy;
			} else if (weathericon.contains("阴")) {
				ResorceId = R.drawable.bigweather_cloudyday;
			} else if (weathericon.contains("晴")) {
				ResorceId = R.drawable.bigweather_sunny;
			} else {
				ResorceId = R.drawable.bigweather_unknow;
			}
		}
		return ResorceId;
	}

	public static int SmallgetResourceFromString(String weathercode) {
		int ResorceId = 0;
		String weathericon = null;
		String[] names = weathercode.split("转");
		if (names.length == 2) {
			weathericon = names[0];
		} else {
			weathericon = weathercode;
		}
		if (weathericon.contains("霾")) {
			ResorceId = R.drawable.smallweather_mai;
		} else if (weathericon.contains("飓风")) {
			ResorceId = R.drawable.smallweather_jufeng;
		} else if (weathericon.contains("冰雹")) {
			ResorceId = R.drawable.smallweather_bingbao;
		} else if (weathericon.contains("暴雪")) {
			ResorceId = R.drawable.smallweather_baosnow;
		} else if (weathericon.contains("雷阵雨")) {
			ResorceId = R.drawable.smallweather_thunderstorms;
		} else if (weathericon.contains("雨加雪")) {
			ResorceId = R.drawable.smallweather_sleet;
		} else if (weathericon.contains("阵雨")) {
			ResorceId = R.drawable.smallweather_zhenrain;
		} else if (weathericon.contains("阵雪")) {
			ResorceId = R.drawable.smallweather_zhensnow;
		} else if (weathericon.contains("沙")) {
			ResorceId = R.drawable.smallweather_sand;
		} else if (weathericon.contains("雾")) {
			ResorceId = R.drawable.smallweather_fog;
		} else if (weathericon.contains("大雪")) {
			ResorceId = R.drawable.smallweather_bigsnow;
		} else if (weathericon.contains("大雨") || weathericon.contains("暴雨")) {
			ResorceId = R.drawable.smallweather_bigrain;
		} else if (weathericon.contains("小雪")) {
			ResorceId = R.drawable.smallweather_smallsnow;
		} else if (weathericon.contains("小雨")) {
			ResorceId = R.drawable.smallweather_smallrain;
		} else if (weathericon.contains("中雪")) {
			ResorceId = R.drawable.smallweather_middlesnow;
		} else if (weathericon.contains("中雨")) {
			ResorceId = R.drawable.smallweather_middlerain;
		} else if (weathericon.contains("云")) {
			ResorceId = R.drawable.smallweather_cloudy;
		} else if (weathericon.contains("阴")) {
			ResorceId = R.drawable.smallweather_cloudyday;
		} else if (weathericon.contains("晴")) {
			ResorceId = R.drawable.smallweather_sunny;
		} else {
			ResorceId = R.drawable.smallweather_unknow;
		}
		return ResorceId;
	}

	public static int codeForPath(String weathercode) {
		int code = Integer.parseInt(weathercode);
		Calendar c = Calendar.getInstance();// 可以对每个时间域单独修改
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int ResorceId = 0;
		if (hour >= 18) {
			switch (code) {
			case 0:
			case 1:
			case 2:
			case 23:
			case 24:
			case 25:
			case 3:
			case 4:
			case 11:
			case 12:
			case 37:
			case 38:
			case 39:
			case 40:
			case 45:
			case 47:
			case 5:
			case 6:
			case 7:
			case 17:
			case 18:
			case 35:
			case 19:
			case 22:
			case 20:
			case 21:
			case 31:
			case 32:
			case 33:
			case 34:
			case 36:
				ResorceId = R.drawable.bigweather_reaching;
				break;
			case 8:
			case 9:
			case 10:
				ResorceId = R.drawable.bigweather_rainshowerslate;
				break;
			case 13:
			case 42:
			case 46:
			case 14:
			case 15:
			case 16:
			case 41:
			case 43:
				ResorceId = R.drawable.bigweather_snowshowerslate;
				break;
			case 26:
			case 27:
			case 28:
			case 29:
			case 30:
			case 44:
				ResorceId = R.drawable.bigweather_latecloudy;
				break;
			default:
				ResorceId = R.drawable.bigweather_unknow;
				break;
			}
		} else {
			switch (code) {
			case 0:
			case 1:
			case 2:
			case 23:
			case 24:
			case 25:
				ResorceId = R.drawable.bigweather_jufeng;
				break;
			case 3:
			case 4:
			case 11:
			case 12:
			case 37:
			case 38:
			case 39:
			case 40:
			case 45:
			case 47:
				ResorceId = R.drawable.bigweather_thunderstorms;
				break;
			case 5:
			case 6:
			case 7:
				ResorceId = R.drawable.bigweather_sleet;
				break;
			case 8:
			case 9:
			case 10:
				ResorceId = R.drawable.bigweather_smallrain;
				break;
			case 13:
			case 42:
			case 46:
				ResorceId = R.drawable.bigweather_baosnow;
				break;
			case 14:
			case 15:
			case 16:
				ResorceId = R.drawable.bigweather_smallsnow;
				break;
			case 17:
			case 18:
			case 35:
				ResorceId = R.drawable.bigweather_bingbao;
				break;
			case 19:
			case 22:
				ResorceId = R.drawable.bigweather_sand;
				break;
			case 20:
			case 21:
				ResorceId = R.drawable.bigweather_fog;
				break;
			case 26:
			case 27:
			case 28:
			case 29:
			case 30:
			case 44:
				ResorceId = R.drawable.bigweather_cloudyday;
				break;
			case 31:
			case 32:
			case 33:
			case 34:
			case 36:
				ResorceId = R.drawable.bigweather_sunny;
				break;
			case 41:
			case 43:
				ResorceId = R.drawable.bigweather_bigsnow;
				break;
			default:
				ResorceId = R.drawable.bigweather_unknow;
				break;
			}
		}
		return ResorceId;
	}

	public static int codeForSmallPath(String weathercode) {
		int code = Integer.parseInt(weathercode);
		String path = null;
		int ResorceId = 0;
		switch (code) {
		case 0:
		case 1:
		case 2:
		case 23:
		case 24:
		case 25:
			ResorceId = R.drawable.smallweather_jufeng;
			break;
		case 3:
		case 4:
		case 11:
		case 12:
		case 37:
		case 38:
		case 39:
		case 40:
		case 45:
		case 47:
			ResorceId = R.drawable.smallweather_thunderstorms;
			break;
		case 5:
		case 6:
		case 7:
			ResorceId = R.drawable.smallweather_sleet;
			break;
		case 8:
		case 9:
		case 10:
			ResorceId = R.drawable.smallweather_smallrain;
			break;
		case 13:
		case 42:
		case 46:
			ResorceId = R.drawable.smallweather_baosnow;
			break;
		case 14:
		case 15:
		case 16:
			ResorceId = R.drawable.smallweather_smallsnow;
			break;
		case 17:
		case 18:
		case 35:
			ResorceId = R.drawable.smallweather_bingbao;
			break;
		case 19:
		case 22:
			ResorceId = R.drawable.smallweather_sand;
			break;
		case 20:
		case 21:
			ResorceId = R.drawable.smallweather_fog;
			break;
		case 26:
		case 27:
		case 28:
		case 29:
		case 30:
		case 44:
			ResorceId = R.drawable.smallweather_cloudyday;
			break;
		case 31:
		case 32:
		case 33:
		case 34:
		case 36:
			ResorceId = R.drawable.smallweather_sunny;
			break;
		case 41:
		case 43:
			ResorceId = R.drawable.smallweather_bigsnow;
			break;
		default:
			ResorceId = R.drawable.smallweather_unknow;
			break;
		}
		return ResorceId;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int ReturnMaxInFive(int num1, int num2, int num3, int num4,
			int num5) {
		int[] intArray = { num1, num2, num3, num4, num5 };
		int max = intArray[0];
		for (int i = 0; i < intArray.length; i++) {
			if (intArray[i] > max) {
				max = intArray[i];
			}
		}
		return max;
	}

	public static int ReturnMinInFive(int num1, int num2, int num3, int num4,
			int num5) {
		int[] intArray = { num1, num2, num3, num4, num5 };
		int min = intArray[0];
		for (int i = 0; i < intArray.length; i++) {
			if (intArray[i] < min) {
				min = intArray[i];
			}
		}
		return min;
	}

	public static Bitmap drawCurve(Context context, int hightmp1, int hightmp2,
			int hightmp3, int hightmp4, int hightmp5, int lowtmp1, int lowtmp2,
			int lowtmp3, int lowtmp4, int lowtmp5) {
		Bitmap backImage = null;
		float width = dip2px(context, 280f);
		float height = 0;
		if (getScreenPixelsWidth(context) == 480) {
			height = dip2px(context, 182f);
		} else {
			height = getScreenPixelsHeight(context)
					- getStatusBarHeight(context) - dip2px(context, 210f)
					- dip2px(context, 245f);
		}
		backImage = Bitmap.createBitmap((int) width, (int) height,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(backImage);
		canvas.drawColor(Color.TRANSPARENT);// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias(true);// 防锯齿
		paint.setDither(true);// 防抖动
		paint.setSubpixelText(true);
		paint.setARGB(255, 255, 255, 255);
		paint.setStrokeWidth(dip2px(context, 1));
		paint.setTextSize(dip2px(context, 13));
		int maxHighNum = ReturnMaxInFive(hightmp1, hightmp2, hightmp3,
				hightmp4, hightmp5);
		int minHighNum = ReturnMinInFive(hightmp1, hightmp2, hightmp3,
				hightmp4, hightmp5);
		int maxLowNum = ReturnMaxInFive(lowtmp1, lowtmp2, lowtmp3, lowtmp4,
				lowtmp5);
		int minLowNum = ReturnMinInFive(lowtmp1, lowtmp2, lowtmp3, lowtmp4,
				lowtmp5);
		float moveY = dip2px(context, 13);
		float jianxi = dip2px(context, 20);
		float moveJx = dip2px(context, 45);
		Bitmap todayDot = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.dotoday);
		Bitmap Dot = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.dot);
		if (maxHighNum - minHighNum == 0) {
			canvas.drawLine(
					(width / 5 - todayDot.getWidth()) / 2 + todayDot.getWidth()
							/ 2,
					(height / 2 - todayDot.getHeight()) / 2
							+ todayDot.getHeight() / 2 - moveY,
					(width / 5 - Dot.getWidth()) / 2 + width / 5
							+ Dot.getWidth() / 2,
					(height / 2 + Dot.getHeight()) / 2 - Dot.getHeight() / 2
							- moveY, paint);

			canvas.drawLine(
					(width / 5 - Dot.getWidth()) / 2 + width / 5
							+ Dot.getWidth() / 2,
					(height / 2 - Dot.getHeight()) / 2 + Dot.getHeight() / 2
							- moveY, (width / 5 - Dot.getWidth()) / 2 + width
							/ 5 * 2 + Dot.getWidth() / 2,
					(height / 2 - Dot.getHeight()) / 2 + Dot.getHeight() / 2
							- moveY, paint);

			canvas.drawLine((width / 5 - Dot.getWidth()) / 2 + width / 5 * 2
					+ Dot.getWidth() / 2, (height / 2 - Dot.getHeight()) / 2
					+ Dot.getHeight() / 2 - moveY, (width / 5 - Dot.getWidth())
					/ 2 + width / 5 * 3 + Dot.getWidth() / 2,
					(height / 2 - Dot.getHeight()) / 2 + Dot.getHeight() / 2
							- moveY, paint);

			canvas.drawLine((width / 5 - Dot.getWidth()) / 2 + width / 5 * 3
					+ Dot.getWidth() / 2, (height / 2 - Dot.getHeight()) / 2
					+ Dot.getHeight() / 2 - moveY, (width / 5 - Dot.getWidth())
					/ 2 + width / 5 * 4 + Dot.getWidth() / 2,
					(height / 2 - Dot.getHeight()) / 2 + Dot.getHeight() / 2
							- moveY, paint);
			canvas.drawBitmap(todayDot, (width / 5 - todayDot.getWidth()) / 2,
					(height / 2 - todayDot.getHeight()) / 2 - moveY, paint);
			canvas.drawBitmap(Dot,
					(width / 5 - Dot.getWidth()) / 2 + width / 5,
					(height / 2 - Dot.getHeight()) / 2 - moveY, paint);
			canvas.drawBitmap(Dot, (width / 5 - Dot.getWidth()) / 2 + width / 5
					* 2, (height / 2 - Dot.getHeight()) / 2 - moveY, paint);
			canvas.drawBitmap(Dot, (width / 5 - Dot.getWidth()) / 2 + width / 5
					* 3, (height / 2 - Dot.getHeight()) / 2 - moveY, paint);
			canvas.drawBitmap(Dot, (width / 5 - Dot.getWidth()) / 2 + width / 5
					* 4, (height / 2 - Dot.getHeight()) / 2 - moveY, paint);

			canvas.drawText(hightmp1 + "°",
					(width / 5 - paint.measureText(hightmp1 + "°")) / 2, height
							/ 4 + todayDot.getHeight() / 2, paint);
			canvas.drawText(hightmp2 + "°",
					(width / 5 - paint.measureText(hightmp2 + "°")) / 2 + width
							/ 5, height / 4 + todayDot.getHeight() / 2, paint);
			canvas.drawText(hightmp3 + "°",
					(width / 5 - paint.measureText(hightmp3 + "°")) / 2 + width
							/ 5 * 2, height / 4 + todayDot.getHeight() / 2,
					paint);
			canvas.drawText(hightmp4 + "°",
					(width / 5 - paint.measureText(hightmp4 + "°")) / 2 + width
							/ 5 * 3, height / 4 + todayDot.getHeight() / 2,
					paint);
			canvas.drawText(hightmp5 + "°",
					(width / 5 - paint.measureText(hightmp5 + "°")) / 2 + width
							/ 5 * 4, height / 4 + todayDot.getHeight() / 2,
					paint);
		} else {
			float everyHeight = (height / 2 - moveJx - jianxi)
					/ (maxHighNum - minHighNum);
			canvas.drawLine(
					(width / 5 - todayDot.getWidth()) / 2 + todayDot.getWidth()
							/ 2, height / 2 - moveJx - (hightmp1 - minHighNum)
							* everyHeight, (width / 5 - Dot.getWidth()) / 2
							+ width / 5 + Dot.getWidth() / 2, height / 2
							- moveJx - (hightmp2 - minHighNum) * everyHeight,
					paint);

			canvas.drawLine(
					(width / 5 - todayDot.getWidth()) / 2 + todayDot.getWidth()
							/ 2 + width / 5,
					height / 2 - moveJx - (hightmp2 - minHighNum) * everyHeight,
					(width / 5 - Dot.getWidth()) / 2 + width / 5 * 2
							+ Dot.getWidth() / 2, height / 2 - moveJx
							- (hightmp3 - minHighNum) * everyHeight, paint);
			canvas.drawLine(
					(width / 5 - todayDot.getWidth()) / 2 + todayDot.getWidth()
							/ 2 + width / 5 * 2,
					height / 2 - moveJx - (hightmp3 - minHighNum) * everyHeight,
					(width / 5 - Dot.getWidth()) / 2 + width / 5 * 3
							+ Dot.getWidth() / 2, height / 2 - moveJx
							- (hightmp4 - minHighNum) * everyHeight, paint);
			canvas.drawLine(
					(width / 5 - todayDot.getWidth()) / 2 + todayDot.getWidth()
							/ 2 + width / 5 * 3,
					height / 2 - moveJx - (hightmp4 - minHighNum) * everyHeight,
					(width / 5 - Dot.getWidth()) / 2 + width / 5 * 4
							+ Dot.getWidth() / 2, height / 2 - moveJx
							- (hightmp5 - minHighNum) * everyHeight, paint);
			canvas.drawBitmap(todayDot, (width / 5 - todayDot.getWidth()) / 2,
					height / 2 - moveJx - (hightmp1 - minHighNum) * everyHeight
							- todayDot.getHeight() / 2, paint);
			canvas.drawBitmap(Dot,
					(width / 5 - Dot.getWidth()) / 2 + width / 5, height / 2
							- moveJx - (hightmp2 - minHighNum) * everyHeight
							- Dot.getHeight() / 2, paint);
			canvas.drawBitmap(Dot, (width / 5 - Dot.getWidth()) / 2 + width / 5
					* 2, height / 2 - moveJx - (hightmp3 - minHighNum)
					* everyHeight - Dot.getHeight() / 2, paint);
			canvas.drawBitmap(Dot, (width / 5 - Dot.getWidth()) / 2 + width / 5
					* 3, height / 2 - moveJx - (hightmp4 - minHighNum)
					* everyHeight - Dot.getHeight() / 2, paint);
			canvas.drawBitmap(Dot, (width / 5 - Dot.getWidth()) / 2 + width / 5
					* 4, height / 2 - moveJx - (hightmp5 - minHighNum)
					* everyHeight - Dot.getHeight() / 2, paint);

			canvas.drawText(hightmp1 + "°",
					(width / 5 - paint.measureText(hightmp1 + "°")) / 2, height
							/ 2 - moveJx - (hightmp1 - minHighNum)
							* everyHeight + todayDot.getHeight() / 2 + moveY,
					paint);
			canvas.drawText(hightmp2 + "°",
					(width / 5 - paint.measureText(hightmp2 + "°")) / 2 + width
							/ 5, height / 2 - moveJx - (hightmp2 - minHighNum)
							* everyHeight + todayDot.getHeight() / 2 + moveY,
					paint);
			canvas.drawText(hightmp3 + "°",
					(width / 5 - paint.measureText(hightmp3 + "°")) / 2 + width
							/ 5 * 2,
					height / 2 - moveJx - (hightmp3 - minHighNum) * everyHeight
							+ todayDot.getHeight() / 2 + moveY, paint);
			canvas.drawText(hightmp4 + "°",
					(width / 5 - paint.measureText(hightmp4 + "°")) / 2 + width
							/ 5 * 3,
					height / 2 - moveJx - (hightmp4 - minHighNum) * everyHeight
							+ todayDot.getHeight() / 2 + moveY, paint);
			canvas.drawText(hightmp5 + "°",
					(width / 5 - paint.measureText(hightmp5 + "°")) / 2 + width
							/ 5 * 4,
					height / 2 - moveJx - (hightmp5 - minHighNum) * everyHeight
							+ todayDot.getHeight() / 2 + moveY, paint);
		}

		if (maxLowNum - minLowNum == 0) {
			canvas.drawLine(
					(width / 5 - todayDot.getWidth()) / 2 + todayDot.getWidth()
							/ 2,
					(height / 2 - todayDot.getHeight()) / 2
							+ todayDot.getHeight() / 2 - moveY + height / 2,
					(width / 5 - Dot.getWidth()) / 2 + width / 5
							+ Dot.getWidth() / 2,
					(height / 2 + Dot.getHeight()) / 2 - Dot.getHeight() / 2
							- moveY + height / 2, paint);

			canvas.drawLine(
					(width / 5 - Dot.getWidth()) / 2 + width / 5
							+ Dot.getWidth() / 2,
					(height / 2 - Dot.getHeight()) / 2 + Dot.getHeight() / 2
							- moveY + height / 2, (width / 5 - Dot.getWidth())
							/ 2 + width / 5 * 2 + Dot.getWidth() / 2,
					(height / 2 - Dot.getHeight()) / 2 + Dot.getHeight() / 2
							- moveY + height / 2, paint);

			canvas.drawLine(
					(width / 5 - Dot.getWidth()) / 2 + width / 5 * 2
							+ Dot.getWidth() / 2,
					(height / 2 - Dot.getHeight()) / 2 + Dot.getHeight() / 2
							- moveY + height / 2,
					(width / 5 - Dot.getWidth()) / 2 + width / 5 * 3
							+ Dot.getWidth() / 2,
					(height / 2 - Dot.getHeight()) / 2 + Dot.getHeight() / 2
							- moveY + height / 2, paint);

			canvas.drawLine(
					(width / 5 - Dot.getWidth()) / 2 + width / 5 * 3
							+ Dot.getWidth() / 2,
					(height / 2 - Dot.getHeight()) / 2 + Dot.getHeight() / 2
							- moveY + height / 2,
					(width / 5 - Dot.getWidth()) / 2 + width / 5 * 4
							+ Dot.getWidth() / 2,
					(height / 2 - Dot.getHeight()) / 2 + Dot.getHeight() / 2
							- moveY + height / 2, paint);
			canvas.drawBitmap(todayDot, (width / 5 - todayDot.getWidth()) / 2,
					(height / 2 - todayDot.getHeight()) / 2 - moveY + height
							/ 2, paint);
			canvas.drawBitmap(Dot,
					(width / 5 - Dot.getWidth()) / 2 + width / 5,
					(height / 2 - Dot.getHeight()) / 2 - moveY + height / 2,
					paint);
			canvas.drawBitmap(Dot, (width / 5 - Dot.getWidth()) / 2 + width / 5
					* 2, (height / 2 - Dot.getHeight()) / 2 - moveY + height
					/ 2, paint);
			canvas.drawBitmap(Dot, (width / 5 - Dot.getWidth()) / 2 + width / 5
					* 3, (height / 2 - Dot.getHeight()) / 2 - moveY + height
					/ 2, paint);
			canvas.drawBitmap(Dot, (width / 5 - Dot.getWidth()) / 2 + width / 5
					* 4, (height / 2 - Dot.getHeight()) / 2 - moveY + height
					/ 2, paint);

			canvas.drawText(lowtmp1 + "°",
					(width / 5 - paint.measureText(lowtmp1 + "°")) / 2, height
							/ 4 + todayDot.getHeight() / 2 + height / 2, paint);
			canvas.drawText(lowtmp2 + "°",
					(width / 5 - paint.measureText(lowtmp2 + "°")) / 2 + width
							/ 5, height / 4 + todayDot.getHeight() / 2 + height
							/ 2, paint);
			canvas.drawText(lowtmp3 + "°",
					(width / 5 - paint.measureText(lowtmp3 + "°")) / 2 + width
							/ 5 * 2, height / 4 + todayDot.getHeight() / 2
							+ height / 2, paint);
			canvas.drawText(lowtmp4 + "°",
					(width / 5 - paint.measureText(lowtmp4 + "°")) / 2 + width
							/ 5 * 3, height / 4 + todayDot.getHeight() / 2
							+ height / 2, paint);
			canvas.drawText(lowtmp5 + "°",
					(width / 5 - paint.measureText(lowtmp5 + "°")) / 2 + width
							/ 5 * 4, height / 4 + todayDot.getHeight() / 2
							+ height / 2, paint);
		} else {
			float everyHeight = (height / 2 - moveJx - jianxi)
					/ (maxLowNum - minLowNum);
			canvas.drawLine(
					(width / 5 - todayDot.getWidth()) / 2 + todayDot.getWidth()
							/ 2, height - moveJx - (lowtmp1 - minLowNum)
							* everyHeight, (width / 5 - Dot.getWidth()) / 2
							+ width / 5 + Dot.getWidth() / 2, height - moveJx
							- (lowtmp2 - minLowNum) * everyHeight, paint);

			canvas.drawLine(
					(width / 5 - todayDot.getWidth()) / 2 + todayDot.getWidth()
							/ 2 + width / 5,
					height - moveJx - (lowtmp2 - minLowNum) * everyHeight,
					(width / 5 - Dot.getWidth()) / 2 + width / 5 * 2
							+ Dot.getWidth() / 2, height - moveJx
							- (lowtmp3 - minLowNum) * everyHeight, paint);
			canvas.drawLine(
					(width / 5 - todayDot.getWidth()) / 2 + todayDot.getWidth()
							/ 2 + width / 5 * 2,
					height - moveJx - (lowtmp3 - minLowNum) * everyHeight,
					(width / 5 - Dot.getWidth()) / 2 + width / 5 * 3
							+ Dot.getWidth() / 2, height - moveJx
							- (lowtmp4 - minLowNum) * everyHeight, paint);
			canvas.drawLine(
					(width / 5 - todayDot.getWidth()) / 2 + todayDot.getWidth()
							/ 2 + width / 5 * 3,
					height - moveJx - (lowtmp4 - minLowNum) * everyHeight,
					(width / 5 - Dot.getWidth()) / 2 + width / 5 * 4
							+ Dot.getWidth() / 2, height - moveJx
							- (lowtmp5 - minLowNum) * everyHeight, paint);
			canvas.drawBitmap(todayDot, (width / 5 - todayDot.getWidth()) / 2,
					height - moveJx - (lowtmp1 - minLowNum) * everyHeight
							- todayDot.getHeight() / 2, paint);
			canvas.drawBitmap(Dot,
					(width / 5 - Dot.getWidth()) / 2 + width / 5, height
							- moveJx - (lowtmp2 - minLowNum) * everyHeight
							- Dot.getHeight() / 2, paint);
			canvas.drawBitmap(Dot, (width / 5 - Dot.getWidth()) / 2 + width / 5
					* 2, height - moveJx - (lowtmp3 - minLowNum) * everyHeight
					- Dot.getHeight() / 2, paint);
			canvas.drawBitmap(Dot, (width / 5 - Dot.getWidth()) / 2 + width / 5
					* 3, height - moveJx - (lowtmp4 - minLowNum) * everyHeight
					- Dot.getHeight() / 2, paint);
			canvas.drawBitmap(Dot, (width / 5 - Dot.getWidth()) / 2 + width / 5
					* 4, height - moveJx - (lowtmp5 - minLowNum) * everyHeight
					- Dot.getHeight() / 2, paint);

			canvas.drawText(lowtmp1 + "°",
					(width / 5 - paint.measureText(lowtmp1 + "°")) / 2, height
							- moveJx - (lowtmp1 - minLowNum) * everyHeight
							+ todayDot.getHeight() / 2 + moveY, paint);
			canvas.drawText(lowtmp2 + "°",
					(width / 5 - paint.measureText(lowtmp2 + "°")) / 2 + width
							/ 5, height - moveJx - (lowtmp2 - minLowNum)
							* everyHeight + todayDot.getHeight() / 2 + moveY,
					paint);
			canvas.drawText(lowtmp3 + "°",
					(width / 5 - paint.measureText(lowtmp3 + "°")) / 2 + width
							/ 5 * 2, height - moveJx - (lowtmp3 - minLowNum)
							* everyHeight + todayDot.getHeight() / 2 + moveY,
					paint);
			canvas.drawText(lowtmp4 + "°",
					(width / 5 - paint.measureText(lowtmp4 + "°")) / 2 + width
							/ 5 * 3, height - moveJx - (lowtmp4 - minLowNum)
							* everyHeight + todayDot.getHeight() / 2 + moveY,
					paint);
			canvas.drawText(lowtmp5 + "°",
					(width / 5 - paint.measureText(lowtmp5 + "°")) / 2 + width
							/ 5 * 4, height - moveJx - (lowtmp5 - minLowNum)
							* everyHeight + todayDot.getHeight() / 2 + moveY,
					paint);
		}

		return backImage;
	}

	public static WeatherEntity getWeatherInland(SharedPreferences sharedPref) {
		if (sharedPref.getBoolean("inlandnumberweatherstate", false)) {
			WeatherEntity weatherentity = new WeatherEntity();
			weatherentity.setCity(sharedPref.getString(
					"inlandnumberweathercityname", null));
			weatherentity.setCondition(sharedPref.getString(
					"inlandnumberweathercondition", null));
			ArrayList<WeatherForestEntity> list = new ArrayList<WeatherForestEntity>();
			WeatherForestEntity weather01 = new WeatherForestEntity();
			weather01.setCondition(sharedPref.getString(
					"inlandnumberlistweathercode0", null));
			weather01.setHight(sharedPref.getString(
					"inlandnumberlistweatherhighTmp0", null));
			weather01.setLow(sharedPref.getString(
					"inlandnumberlistweatherlowTmp0", null));
			weather01.setDayOfWeek(sharedPref.getInt(
					"inlandnumberlistweatherweek0", 0));
			list.add(weather01);
			WeatherForestEntity weather02 = new WeatherForestEntity();
			weather02.setCondition(sharedPref.getString(
					"inlandnumberlistweathercode1", null));
			weather02.setHight(sharedPref.getString(
					"inlandnumberlistweatherhighTmp1", null));
			weather02.setLow(sharedPref.getString(
					"inlandnumberlistweatherlowTmp1", null));
			weather02.setDayOfWeek(sharedPref.getInt(
					"inlandnumberlistweatherweek1", 0));
			list.add(weather02);
			WeatherForestEntity weather03 = new WeatherForestEntity();
			weather03.setCondition(sharedPref.getString(
					"inlandnumberlistweathercode2", null));
			weather03.setHight(sharedPref.getString(
					"inlandnumberlistweatherhighTmp2", null));
			weather03.setLow(sharedPref.getString(
					"inlandnumberlistweatherlowTmp2", null));
			weather03.setDayOfWeek(sharedPref.getInt(
					"inlandnumberlistweatherweek2", 0));
			list.add(weather03);
			WeatherForestEntity weather04 = new WeatherForestEntity();
			weather04.setCondition(sharedPref.getString(
					"inlandnumberlistweathercode3", null));
			weather04.setHight(sharedPref.getString(
					"inlandnumberlistweatherhighTmp3", null));
			weather04.setLow(sharedPref.getString(
					"inlandnumberlistweatherlowTmp3", null));
			weather04.setDayOfWeek(sharedPref.getInt(
					"inlandnumberlistweatherweek3", 0));
			list.add(weather04);
			WeatherForestEntity weather05 = new WeatherForestEntity();
			weather05.setCondition(sharedPref.getString(
					"inlandnumberlistweathercode4", null));
			weather05.setHight(sharedPref.getString(
					"inlandnumberlistweatherhighTmp4", null));
			weather05.setLow(sharedPref.getString(
					"inlandnumberlistweatherlowTmp4", null));
			weather05.setDayOfWeek(sharedPref.getInt(
					"inlandnumberlistweatherweek4", 0));
			list.add(weather05);
			weatherentity.setDetails(list);
			return weatherentity;
		}
		return null;
	}

	public static void setWeatherInland(SharedPreferences sharedPref,
			WeatherEntity inlandweather) {
		Editor ed = sharedPref.edit();
		ed.putBoolean("inlandnumberweatherstate", true);
		ed.putString("inlandnumberweathercityname", inlandweather.getCity());
		ed.putString("inlandnumberweathercondition",
				inlandweather.getCondition());
		ed.putString("inlandnumberlistweathercode0", inlandweather.getDetails()
				.get(0).getCondition());
		ed.putString("inlandnumberlistweatherhighTmp0", inlandweather
				.getDetails().get(0).getHight());
		ed.putString("inlandnumberlistweatherlowTmp0", inlandweather
				.getDetails().get(0).getLow());
		ed.putInt("inlandnumberlistweatherweek0", inlandweather.getDetails()
				.get(0).getDayOfWeek());
		ed.putString("inlandnumberlistweathercode1", inlandweather.getDetails()
				.get(1).getCondition());
		ed.putString("inlandnumberlistweatherhighTmp1", inlandweather
				.getDetails().get(1).getHight());
		ed.putString("inlandnumberlistweatherlowTmp1", inlandweather
				.getDetails().get(1).getLow());
		ed.putInt("inlandnumberlistweatherweek1", inlandweather.getDetails()
				.get(1).getDayOfWeek());
		ed.putString("inlandnumberlistweathercode2", inlandweather.getDetails()
				.get(2).getCondition());
		ed.putString("inlandnumberlistweatherhighTmp2", inlandweather
				.getDetails().get(2).getHight());
		ed.putString("inlandnumberlistweatherlowTmp2", inlandweather
				.getDetails().get(2).getLow());
		ed.putInt("inlandnumberlistweatherweek2", inlandweather.getDetails()
				.get(2).getDayOfWeek());
		ed.putString("inlandnumberlistweathercode3", inlandweather.getDetails()
				.get(3).getCondition());
		ed.putString("inlandnumberlistweatherhighTmp3", inlandweather
				.getDetails().get(3).getHight());
		ed.putString("inlandnumberlistweatherlowTmp3", inlandweather
				.getDetails().get(3).getLow());
		ed.putInt("inlandnumberlistweatherweek3", inlandweather.getDetails()
				.get(3).getDayOfWeek());
		ed.putString("inlandnumberlistweathercode4", inlandweather.getDetails()
				.get(4).getCondition());
		ed.putString("inlandnumberlistweatherhighTmp4", inlandweather
				.getDetails().get(4).getHight());
		ed.putString("inlandnumberlistweatherlowTmp4", inlandweather
				.getDetails().get(4).getLow());
		ed.putInt("inlandnumberlistweatherweek4", inlandweather.getDetails()
				.get(4).getDayOfWeek());
		ed.commit();
	}

	public static Weather getWeatherForeign(SharedPreferences sharedPref) {
		if (sharedPref.getBoolean("numberweatherstate", false)) {
			Weather weather = new Weather();
			weather.setWeathercity(sharedPref.getString(
					"numberweathercityname", null));
			weather.setWeathercode(sharedPref.getString("numberweathercode",
					null));
			weather.setWeathercondition(sharedPref.getString(
					"numberweathercondition", null));
			weather.setCurrtmp(sharedPref.getString("numberweathercurrenttmp",
					null));
			weather.setShidu(sharedPref.getString("numberweathershidu", null));
			List<Weather> list = new ArrayList<Weather>();
			Weather weather01 = new Weather();
			weather01.setWeathercode(sharedPref.getString(
					"numberlistweathercode0", null));
			weather01.setHightmp(sharedPref.getString(
					"numberlistweatherhighTmp0", null));
			weather01.setLowtmp(sharedPref.getString(
					"numberlistweatherlowTmp0", null));
			weather01.setWeatherweek(sharedPref.getString(
					"numberlistweatherweek0", null));
			weather01.setWeathercondition(sharedPref.getString(
					"numberlistweathercodition0", null));
			list.add(weather01);
			Weather weather02 = new Weather();
			weather02.setWeathercode(sharedPref.getString(
					"numberlistweathercode1", null));
			weather02.setHightmp(sharedPref.getString(
					"numberlistweatherhighTmp1", null));
			weather02.setLowtmp(sharedPref.getString(
					"numberlistweatherlowTmp1", null));
			weather02.setWeatherweek(sharedPref.getString(
					"numberlistweatherweek1", null));
			weather02.setWeathercondition(sharedPref.getString(
					"numberlistweathercodition1", null));
			list.add(weather02);
			Weather weather03 = new Weather();
			weather03.setWeathercode(sharedPref.getString(
					"numberlistweathercode2", null));
			weather03.setHightmp(sharedPref.getString(
					"numberlistweatherhighTmp2", null));
			weather03.setLowtmp(sharedPref.getString(
					"numberlistweatherlowTmp2", null));
			weather03.setWeatherweek(sharedPref.getString(
					"numberlistweatherweek2", null));
			weather03.setWeathercondition(sharedPref.getString(
					"numberlistweathercodition2", null));
			list.add(weather03);
			Weather weather04 = new Weather();
			weather04.setWeathercode(sharedPref.getString(
					"numberlistweathercode3", null));
			weather04.setHightmp(sharedPref.getString(
					"numberlistweatherhighTmp3", null));
			weather04.setLowtmp(sharedPref.getString(
					"numberlistweatherlowTmp3", null));
			weather04.setWeatherweek(sharedPref.getString(
					"numberlistweatherweek3", null));
			weather04.setWeathercondition(sharedPref.getString(
					"numberlistweathercodition3", null));
			list.add(weather04);
			Weather weather05 = new Weather();
			weather05.setWeathercode(sharedPref.getString(
					"numberlistweathercode4", null));
			weather05.setHightmp(sharedPref.getString(
					"numberlistweatherhighTmp4", null));
			weather05.setLowtmp(sharedPref.getString(
					"numberlistweatherlowTmp4", null));
			weather05.setWeatherweek(sharedPref.getString(
					"numberlistweatherweek4", null));
			weather05.setWeathercondition(sharedPref.getString(
					"numberlistweathercodition4", null));
			list.add(weather05);
			weather.setList(list);
			return weather;
		}
		return null;
	}

	public static void setWeatherForeign(SharedPreferences sharedPref,
			Weather weather) {
		Editor ed = sharedPref.edit();
		if (weather != null && weather.getList() != null
				&& weather.getList().size() == 5) {
			ed.putBoolean("numberweatherstate", true);
			ed.putString("numberweathercityname", weather.getWeathercity());
			ed.putString("numberweathercode", weather.getWeathercode());
			ed.putString("numberweathercondition",
					weather.getWeathercondition());
			ed.putString("numberweathercurrenttmp", weather.getCurrtmp());
			ed.putString("numberweathershidu", weather.getShidu());
			ed.putString("numberlistweathercode0", weather.getList().get(0)
					.getWeathercode());
			ed.putString("numberlistweatherhighTmp0", weather.getList().get(0)
					.getHightmp());
			ed.putString("numberlistweatherlowTmp0", weather.getList().get(0)
					.getLowtmp());
			ed.putString("numberlistweatherweek0", weather.getList().get(0)
					.getWeatherweek());
			ed.putString("numberlistweathercodition0", weather.getList().get(0)
					.getWeathercondition());
			ed.putString("numberlistweathercode1", weather.getList().get(1)
					.getWeathercode());
			ed.putString("numberlistweatherhighTmp1", weather.getList().get(1)
					.getHightmp());
			ed.putString("numberlistweatherlowTmp1", weather.getList().get(1)
					.getLowtmp());
			ed.putString("numberlistweatherweek1", weather.getList().get(1)
					.getWeatherweek());
			ed.putString("numberlistweathercodition1", weather.getList().get(1)
					.getWeathercondition());
			ed.putString("numberlistweathercode2", weather.getList().get(2)
					.getWeathercode());
			ed.putString("numberlistweatherhighTmp2", weather.getList().get(2)
					.getHightmp());
			ed.putString("numberlistweatherlowTmp2", weather.getList().get(2)
					.getLowtmp());
			ed.putString("numberlistweatherweek2", weather.getList().get(2)
					.getWeatherweek());
			ed.putString("numberlistweathercodition2", weather.getList().get(2)
					.getWeathercondition());
			ed.putString("numberlistweathercode3", weather.getList().get(3)
					.getWeathercode());
			ed.putString("numberlistweatherhighTmp3", weather.getList().get(3)
					.getHightmp());
			ed.putString("numberlistweatherlowTmp3", weather.getList().get(3)
					.getLowtmp());
			ed.putString("numberlistweatherweek3", weather.getList().get(3)
					.getWeatherweek());
			ed.putString("numberlistweathercodition3", weather.getList().get(3)
					.getWeathercondition());
			ed.putString("numberlistweathercode4", weather.getList().get(4)
					.getWeathercode());
			ed.putString("numberlistweatherhighTmp4", weather.getList().get(4)
					.getHightmp());
			ed.putString("numberlistweatherlowTmp4", weather.getList().get(4)
					.getLowtmp());
			ed.putString("numberlistweatherweek4", weather.getList().get(4)
					.getWeatherweek());
			ed.putString("numberlistweathercodition4", weather.getList().get(4)
					.getWeathercondition());
			ed.commit();
		}
	}

	public static Bitmap drawString(Context context, String str1, String str2,
			String str3, String str4, String str5) {
		Bitmap backImage = null;
		float width = dip2px(context, 280f);
		float height = dip2px(context, 20f);
		backImage = Bitmap.createBitmap((int) width, (int) height,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(backImage);
		canvas.drawColor(Color.TRANSPARENT);// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias(true);// 防锯齿
		paint.setDither(true);// 防抖动
		paint.setSubpixelText(true);
		paint.setARGB(255, 255, 255, 255);
		paint.setTextSize(dip2px(context, 12));
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float) Math.ceil(fontMetrics.descent
				- fontMetrics.ascent);
		float posY = backImage.getHeight()
				- (backImage.getHeight() - lineHeight) / 2 - fontMetrics.bottom;
		canvas.drawText(str1, (width / 5 - paint.measureText(str1)) / 2, posY,
				paint);
		canvas.drawText(str2, (width / 5 - paint.measureText(str2)) / 2 + width
				/ 5, posY, paint);
		canvas.drawText(str3, (width / 5 - paint.measureText(str3)) / 2 + width
				/ 5 * 2, posY, paint);
		canvas.drawText(str4, (width / 5 - paint.measureText(str4)) / 2 + width
				/ 5 * 3, posY, paint);
		canvas.drawText(str5, (width / 5 - paint.measureText(str5)) / 2 + width
				/ 5 * 4, posY, paint);
		return backImage;
	}

	public static Bitmap takeScreenShot(Activity activity) {
		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		// 获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		// 获取屏幕长和高
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay()
				.getHeight();
		// 去掉标题栏
		// Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		return b;
	}

	public static void saveData(List<String> list, String PATH) {
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			// 存入数据
			File file = new File(PATH);
			if (file.exists()) {
				file.delete();
			}
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			fileOutputStream = new FileOutputStream(file.toString());
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(list);
		} catch (Exception e) {
		} finally {
			if (objectOutputStream != null) {
				try {
					objectOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static ArrayList<String> GetData(String PATH) {
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		ArrayList<String> savedArrayList = new ArrayList<String>();
		try {
			File file = new File(PATH);
			if (!file.exists()) {
				return savedArrayList;
			} else {
				fileInputStream = new FileInputStream(file.toString());
				objectInputStream = new ObjectInputStream(fileInputStream);
				savedArrayList = (ArrayList<String>) objectInputStream
						.readObject();
				return savedArrayList;
			}
		} catch (Exception e) {
		} finally {
			if (objectInputStream != null) {
				try {
					objectInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void saveDataForeign(List<CityResult> list, String PATH) {
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			File file = new File(PATH);
			if (file.exists()) {
				file.delete();
			}
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			fileOutputStream = new FileOutputStream(file.toString());
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(list);
		} catch (Exception e) {
		} finally {
			if (objectOutputStream != null) {
				try {
					objectOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static ArrayList<CityResult> GetDataForeign(String PATH) {
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		ArrayList<CityResult> savedArrayList = new ArrayList<CityResult>();
		try {
			File file = new File(PATH);
			if (!file.exists()) {
				return savedArrayList;
			} else {
				fileInputStream = new FileInputStream(file.toString());
				objectInputStream = new ObjectInputStream(fileInputStream);
				savedArrayList = (ArrayList<CityResult>) objectInputStream
						.readObject();
				return savedArrayList;
			}
		} catch (Exception e) {
		} finally {
			if (objectInputStream != null) {
				try {
					objectInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void RotationAnimal(View view) {
		ObjectAnimator anim = ObjectAnimator.ofFloat(view, "rotation", 0, 360);
		anim.setDuration(700);
		anim.start();
	}

	public static int getScreenPixelsHeight(Context context) {
		if (context != null) {
			int height = context.getResources().getDisplayMetrics().heightPixels;
			return height;
		}
		return -1;
	}

	public static int getScreenPixelsWidth(Context context) {

		if (context != null) {
			int width = context.getResources().getDisplayMetrics().widthPixels;
			return width;
		}
		return -1;
	}

	public static int getStatusBarHeight(Context context) {
		int statusBarHeight = 0;
		Resources res = context.getResources();
		int resourceId = res.getIdentifier("status_bar_height", "dimen",
				"android");
		if (resourceId > 0) {
			statusBarHeight = res.getDimensionPixelSize(resourceId);
		}
		return statusBarHeight;
	}
}
