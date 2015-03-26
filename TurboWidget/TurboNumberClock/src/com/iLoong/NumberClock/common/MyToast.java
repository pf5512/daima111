package com.iLoong.NumberClock.common;
import android.content.Context;
import android.widget.Toast;

public class MyToast
{
	private static Context context = null;
	private static Toast toast = null;

	public static Toast getToast(Context context, String hint)
	{
		if (MyToast.context == context)
		{
			toast.cancel();
			toast.setText(hint);
		} else
		{
			MyToast.context = context;
			toast = Toast.makeText(context, hint, Toast.LENGTH_SHORT);
		}
		return toast;
	}
}
