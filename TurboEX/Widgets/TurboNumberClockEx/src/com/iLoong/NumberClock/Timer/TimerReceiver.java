package com.iLoong.NumberClock.Timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iLoong.NumberClock.common.Parameter;

public class TimerReceiver extends BroadcastReceiver {
	public TimeService service;
	private Intent timerIntent;

	public TimerReceiver(TimeService service) {
		this.service = service;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (timerIntent == null) {
			timerIntent = new Intent();
		}
		if (Intent.ACTION_TIME_TICK.equals(intent.getAction())
				|| Intent.ACTION_TIME_CHANGED.equals(intent.getAction())
				|| Intent.ACTION_DATE_CHANGED.equals(intent.getAction())
				|| Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
			timerIntent.setAction(Parameter.BROADCASE_UPDATE);
			context.sendBroadcast(timerIntent);
		}
	}

}
