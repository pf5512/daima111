package com.cooee.launcher.desktopedit;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

import com.coco.theme.themebox.StaticClass;
import com.coco.theme.themebox.ThemeInformation;
import com.coco.theme.themebox.service.ThemeService;

public class ThemeQuery {

	public List<ThemeInformation> localList = new ArrayList<ThemeInformation>();
	private Context context;

	public ThemeQuery(Context cxt) {
		context = cxt;
		localList = queryPackage();
	}

	public void free() {
		if (localList != null) {
			localList.clear();
			localList = null;
		}
	}

	private List<ThemeInformation> queryPackage() {
		List<ThemeInformation> localList = new ArrayList<ThemeInformation>();
		ThemeService themeSv = new ThemeService(context);
		List<ThemeInformation> installList = themeSv.queryInstallList();
		for (ThemeInformation info : installList) {
			if (info.getThumbImage() == null) {
				getItemThumb(info);
			}
			localList.add(info);
		}
		return localList;
	}

	public void onDestory() {
		for (ThemeInformation info : localList) {
			info.disposeThumb();
			info = null;
		}
	}

	private void getItemThumb(ThemeInformation themeInfo) {
		if (themeInfo.getThumbImage() == null) {
			themeInfo.setThumbImage(context, themeInfo.getPackageName(),
					themeInfo.getClassName());
		}
		if (themeInfo.isNeedLoadDetail()) {
			Bitmap imgThumb = themeInfo.getThumbImage();
			if (imgThumb == null) {
				themeInfo.loadDetail(context);
				if (themeInfo.getThumbImage() != null) {
					StaticClass
							.saveMyBitmap(context, themeInfo.getPackageName(),
									themeInfo.getClassName(),
									themeInfo.getThumbImage());
				}
			}
		}
	}
}
