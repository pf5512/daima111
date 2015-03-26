package com.iLoong.launcher.SetupMenu;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.iLoong.launcher.Desktop3D.Log;

import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.update.UpdateTask;

public class ForegroundService extends Service {
	public static final String ACTION_FOREGROUNDSERVICE = "com.iLoong.service.ForegroundService";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.v("service", "onCreate");
		super.onCreate();
		UpdateTask updateTask = new UpdateTask(this);
		updateTask.startTask();
	}

	@Override
	public void onDestroy() {
		Log.v("service", "onDestroy");
		stopForeground(true);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("service", "onStartCommand:" + intent);
		if ((intent != null)
				&& (ACTION_FOREGROUNDSERVICE.equals(intent.getAction()))) {
			Notification localNotification = new Notification(0,
					"CoCoLauncher Start", System.currentTimeMillis());
			localNotification.setLatestEventInfo(this, "CoCoLauncher ",
					"CoCoLauncher Start", PendingIntent.getActivity(this, 0,
							new Intent(this, iLoongLauncher.class), 0));

			// final Intent localintent = new Intent();
			// localintent.setClass(this, iLoongLauncher.class);
			// localintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
			// Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			// startActivity(localintent);
			startForeground(20120701, localNotification);
		}
		return START_NOT_STICKY;
	}
}
