package com.cooee.app.cooeeweather.filehelp;

public class Log {
	private static int v = 0;
	private static int d = 1;
	private static int i = 2;
	private static int w = 3;
	private static int e = 4;
	private static int TAG = 5;

	public static void v(String tag, String msg) {
		if (v < TAG) {
			android.util.Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (d < TAG) {
			android.util.Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (i < TAG) {
			android.util.Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (w < TAG) {
			android.util.Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (e < TAG) {
			android.util.Log.e(tag, msg);
		}
	}

}
