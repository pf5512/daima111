package com.cooee.widget3D.JewelWeather.View;

import com.cooee.widget3D.JewelWeather.iLoongWeather;
import com.iLoong.launcher.tween.View3DTweenAccessor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Quint;

public class EnterInAnim {
	private static final String LOADEND = "loadend";
	private static final String WIDGETID = "widgetid";
	private static final String RESULT = "result";
	private int mWidgetId = -1;
	private Context mContext = null;
	private LoadReceiver mLoadReceiver = null;
	
	private Tween mInAnimTween = null;
	private Object mObject = null;
	public EnterInAnim(Context mycontext,int myWidgetId,Object obj)
	{
		mContext = mycontext;
		mWidgetId = myWidgetId;
		mObject = obj;
		IntentFilter filter = new IntentFilter();
		filter.addAction(LOADEND);
		mLoadReceiver = new LoadReceiver();
		mContext.registerReceiver(mLoadReceiver, filter);
	}

	public void dispose()
	{
		mContext.unregisterReceiver(mLoadReceiver);
		mLoadReceiver = null;
	}
	
	private class LoadReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(LOADEND.equals(intent.getAction()) && 
					intent.getBooleanExtra(RESULT, false) && 
					(intent.getIntExtra(WIDGETID, -2) == mWidgetId))
			{
			//	Log.v("aq", "onReceive");
			//	WidgetWeather.frist = true;
				/*
				if(mObject == null)
				{
					mInAnimTween = Tween.to(iLoongWeather.mWidgetWeather, View3DTweenAccessor.ROTATION, 1.2f)
						.ease(Quint.OUT).targetRelative(90,0);
				}
				else
				{
					mInAnimTween = Tween.to(mObject, View3DTweenAccessor.ROTATION, 1.2f)
							.ease(Quint.OUT).targetRelative(90,0);
				}
				mInAnimTween.start(View3DTweenAccessor.manager);
				*/
			}
			
		}
	}
}
