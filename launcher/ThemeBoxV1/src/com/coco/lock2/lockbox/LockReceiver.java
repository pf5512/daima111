package com.coco.lock2.lockbox;

import java.io.File;
import com.coco.lock2.lockbox.util.LockManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.coco.theme.themebox.util.Log;

public class LockReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			
			LockManager lockManager = new LockManager(context);
			ComponentName comName = lockManager.queryComponent(packageName);
			if (comName != null) {
				LockInformation infor = lockManager.queryLock(
						comName.getPackageName(), comName.getClassName());
				infor.loadDetail(context);
				if (infor.getThumbImage() != null) {
					StaticClass.saveMyBitmap(context, infor.getPackageName(),
							infor.getClassName(), infor.getThumbImage());
				}
			}
		} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
			Log.d("TabLockContentFactory",
					String.format("action=%s", "dsfdggd"));
			String packageName = intent.getData().getSchemeSpecificPart();
			File f = context.getDir("coco", Context.MODE_PRIVATE);
			File f1 = new File(f + "/" + packageName);
			recursionDeleteFile(f1);
		}
	}
	
	private static void recursionDeleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null) {
				return;
			}
			for (File f : childFile) {
				recursionDeleteFile(f);
			}
			file.delete();
		}
	}
}
