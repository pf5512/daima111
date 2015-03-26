package com.cooee.launcher.smartlayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.android.launcher.framework.ApplicationInfo;
import com.android.launcher.framework.LauncherModel;
import com.android.launcher.framework.LauncherSettings;
import com.android.launcher.framework.ShortcutInfo;
import com.cooee.launcher.LauncherApplication;
import com.cooee.launcher.theme.DefaultIcon;
import com.cooee.launcher.theme.ThemeData;

/**
 * @author zhongqihong
 * 
 *         this class is designed for injecting specific layout into desktop.
 * 
 * **/
public class SLInjection {

	Context context;

	public SLInjection(Context context) {
		this.context = context;

		insertSmartLayout();
	}

	void insertSmartLayout() {
		LauncherApplication application = (LauncherApplication) context
				.getApplicationContext();
		SLConfigHandler SLConfig = new SLConfigHandler(this.context);
		List<ResolveInfo> apps = SLConfig.getApplicationInfo();
		LinkedList<SLLayout> layout = SLConfig.getLayout();
		if (apps.size() < layout.size()) {
			int num = layout.size() - apps.size();
			getDefaultIcon(apps, num);
		}
		for (int i = 0; i < layout.size(); i++) {
			if (i < apps.size()) {
				ApplicationInfo appInfo = new ApplicationInfo(apps.get(i),
						application.mIconCache);
				final ShortcutInfo info = new ShortcutInfo(appInfo);
				info.screen = layout.get(i).getScreen();
				info.spanX = 1;
				info.spanY = 1;
				info.cellX = layout.get(i).getCellX();
				info.cellY = layout.get(i).getCellY();
				info.itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
				info.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;

				// God loves Zhongqihong@2014/12/17 ADD START
				// we don't need to assign a value to info.id,because
				// {#addItemToDatabase} will do it automatically.
				// info.id = application.getLauncherProvider().generateNewId();
				LauncherModel.addItemToDatabase(context, info, info.container,
						info.screen, info.cellX, info.cellY, false);
				// God loves Zhongqihong@2014/12/17 ADD END

			}
		}
		SLConfig.clear();
	}

	public List<ResolveInfo> getDefaultIcon(List<ResolveInfo> apps, int num) {
		num += 4;
		if (apps == null)
			return null;
		List<ResolveInfo> newList = new LinkedList<ResolveInfo>();
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> resInfos = packageManager.queryIntentActivities(
				mainIntent, 0);
		int index = 0;
		if (resInfos != null) {
			for (ResolveInfo info : resInfos) {
				ArrayList<String> allNamePatten;
				String packageName = info.activityInfo.applicationInfo.packageName;
				DefaultIcon tem = new DefaultIcon();
				for (int inter = 0; inter < ThemeData.defaultIcons.size()
						&& index < num; inter++) {
					int ih = 0;
					// for (; ih <
					// iLoongLauncher.getInstance().mHotseatIconsName.length;
					// ih++) {
					// String imgName =
					// iLoongLauncher.getInstance().mHotseatIconsName[ih];
					// int imgIndex = imgName.lastIndexOf("/");
					// String image = imgName.substring(imgIndex + 1);
					// if (ThemeData.defaultIcons.get(inter).imageName
					// .equalsIgnoreCase(image)) {
					// break;
					// }
					// }
					// if (ih <
					// iLoongLauncher.getInstance().mHotseatIconsName.length) {
					// Log.v("SMARTLAYOUT", "SET NEW INSTANCE :"
					// + info.activityInfo.packageName);
					// continue;
					// }
					allNamePatten = ThemeData.defaultIcons.get(inter).pkgNameArray;
					if (allNamePatten == null || allNamePatten.size() <= 0)
						continue;
					for (int k = 0; k < allNamePatten.size(); k++) {
						if (packageName.equals(allNamePatten.get(k))) {
							// if (ThemeData.defaultIcons.get(inter).duplicate)
							// {
							ArrayList<String> allCompNames = ThemeData.defaultIcons
									.get(inter).classNameArray;
							int m = 0;
							for (; m < allCompNames.size(); m++) {
								if (info.activityInfo.name.equals(allCompNames
										.get(m))) {
									int l = 0;
									for (l = 0; l < apps.size(); l++) {
										if (apps.get(l).activityInfo.applicationInfo.packageName
												.equalsIgnoreCase(packageName)
												&& apps.get(l).activityInfo.applicationInfo.className
														.equalsIgnoreCase(info.activityInfo.name)) {
											break;
										}
									}
									if (l >= apps.size()) {
										index++;
										newList.add(info);
										break;
									}
								}
							}
							if (m < allCompNames.size()) {
								break;
							}
							// } else {
							// int l = 0;
							// for (l = 0; l < apps.size(); l++) {
							// if
							// (apps.get(l).activityInfo.applicationInfo.packageName
							// .equalsIgnoreCase(packageName)) {
							// break;
							// }
							// }
							// if (l >= apps.size()) {
							// index++;
							// newList.add(info);
							// break;
							// }
							// }
						}
					}
				}
			}
		}
		/*
		 * if(iLoongLauncher.getInstance().mHotseats!=null&&iLoongLauncher.
		 * getInstance().mHotseats.length>0){ int
		 * lenght=iLoongLauncher.getInstance().mHotseats.length; int l=0;
		 * Iterator<ResolveInfo> ite=newList.iterator();
		 * while(ite.hasNext()&&l<lenght){ ResolveInfo info=ite.next(); Intent
		 * intent=iLoongLauncher.getInstance().mHotseats[l];
		 * 
		 * Log.v( "SMARTLAYOUT" ,
		 * "SET NEW INSTANCE :"+info.activityInfo.packageName );
		 * if(info.activityInfo.packageName.equalsIgnoreCase(
		 * intent.getComponent().getPackageName() )) { l++; ite.remove(); } }
		 * 
		 * }
		 */
		//
		apps.addAll(newList);
		return apps;
	}
}
