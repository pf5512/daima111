package com.coco.theme.themebox;

import java.io.File;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.model.ThemeInfoItem;
import com.coco.theme.themebox.service.ThemeDescription;
import com.coco.theme.themebox.service.ThemesDB;
import com.coco.theme.themebox.util.ContentConfig;
import com.coco.theme.themebox.util.PathTool;
import com.iLoong.base.themebox.R;

public class ThemeInformation {
	private String className = "";
	private boolean installed = false;
	private String displayName = "";
	private ThemeInfoItem themeInfo = new ThemeInfoItem();
	private long downloadSize = 0;
	private DownloadStatus downloadStatus = DownloadStatus.StatusInit;
	private Bitmap thumbImage = null;
	private boolean needLoadDetail = true;
	private boolean mSystem = false;
	private boolean downloaded = false;

	public String getPackageName() {
		return themeInfo.getPackageName();
	}

	public String getClassName() {
		return className;
	}

	public boolean isInstalled() {
		return installed;
	}
	
	public boolean isSystem() {
		return mSystem;
	}
	
	public boolean isDownloaded() {
		return downloaded;
	}
	
	public void setSystem(boolean system) {
		mSystem = system;
	}
	
	public long getApplicationSize() {
		return themeInfo.getApplicationSize();
	}
	
	public String getAuthor() {
		return themeInfo.getAuthor();
	}
	
	public String getIntroduction() {
		return themeInfo.getIntroduction();
	}
	
	public ThemeInfoItem getInfoItem() {
		return themeInfo;
	}

	public int getDownloadPercent() {
		if (themeInfo.getApplicationSize() <= 0) {
			return 0;
		}

		int result = (int) (downloadSize * 100 / themeInfo.getApplicationSize());
		if (result < 0) {
			result = 0;
		} else if (result > 100) {
			result = 100;
		}

		return result;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public Bitmap getThumbImage() {
		return thumbImage;
	}

	public void setThumbImage(Context mContext, String pkgName, String actName) {
		File f = mContext.getDir("theme", Context.MODE_PRIVATE);
		String thumbPath = f + "/" + pkgName + "/" + actName+".tupian";
		thumbImage = BitmapFactory.decodeFile(thumbPath);
	}
	public boolean isNeedLoadDetail() {
		return needLoadDetail;
	}

	public DownloadStatus getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(DownloadStatus status) {
		downloadStatus = status;
	}

	public void setDownloadSize(long downSize) {
		downloadSize = downSize;
	}

	public void setTotalSize(long totalSize) {
		themeInfo.setApplicationSize(totalSize);
	}

	public boolean isComponent(String pkgName, String clsName) {
		if (themeInfo.getPackageName().equals(pkgName)
				&& className.equals(clsName)) {
			return true;
		}

		return false;
	}
	
	public boolean isComponent(ComponentName comName) {
		if (themeInfo.getPackageName().equals(comName.getPackageName())
				&& className.equals(comName.getClassName())) {
			return true;
		}

		return false;
	}

	public boolean isPackage(String pkgName) {
		return themeInfo.getPackageName().equals(pkgName);
	}

	public void setActivity(Context cxt, ActivityInfo activity) {
		className = activity.name;
		installed = true;
		thumbImage = null;
		needLoadDetail = true;
		downloadStatus = DownloadStatus.StatusFinish;
		themeInfo = new ThemeInfoItem();
		themeInfo.setPackageName(activity.packageName);
		downloadSize = 0;
		displayName = activity.loadLabel(cxt.getPackageManager()).toString();
		checkThemePrefix();
		mSystem = activity.packageName.equals(ThemesDB.LAUNCHER_PACKAGENAME);
		//CoCo桌面修改成默认主�?
		if(mSystem)
		{
			displayName = cxt.getString(R.string.default_theme);
		}
	}
	
	public void setTheme(Context cxt, ThemeDescription des) {
		className = des.componentName.getClassName();
		installed = true;
		mSystem = des.mSystem;
		thumbImage = null;
		needLoadDetail = true;
		downloadStatus = DownloadStatus.StatusFinish;
		themeInfo = new ThemeInfoItem();
		themeInfo.setPackageName(des.componentName.getPackageName());
		downloadSize = 0;
		displayName = des.title.toString();
		checkThemePrefix();
	}

	public void setDownloadItem(DownloadThemeItem item) {
		setThemeItem(item.getThemeInfo());
		downloadStatus = item.getDownloadStatus();
		downloadSize = item.getDownloadSize();
		downloaded = true;
	}

	public void setThemeItem(ThemeInfoItem item) {
		className = "";
		installed = false;
		thumbImage = null;
		needLoadDetail = true;
		downloadStatus = DownloadStatus.StatusInit;
		themeInfo.copyFrom(item);
		downloadSize = 0;
		displayName = themeInfo.getApplicationName();
		checkThemePrefix();
		downloaded = false;
		mSystem = false;
	}

	public void loadDetail(Context cxt) {
		needLoadDetail = false;
		thumbImage = null;
		if (installed) {
			ContentConfig cfg = new ContentConfig();

			try {
				Context remoteContext = cxt.createPackageContext(
						themeInfo.getPackageName(),
						Context.CONTEXT_IGNORE_SECURITY);
				cfg.loadConfig(remoteContext, className);
				loadInstallDetail(remoteContext, cfg);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			String thumbPath = PathTool
					.getThumbFile(themeInfo.getPackageName());
			if (new File(thumbPath).exists()) {
				thumbImage = BitmapFactory.decodeFile(thumbPath);
			}
		}
	}
	
	public void loadInstallDetail(Context remoteContext, ContentConfig cfg) {
		needLoadDetail = false;
		thumbImage = null;
		installed = true;
		
		thumbImage = cfg.loadThumbImage(remoteContext);
		themeInfo.setApplicationName(cfg.getApplicationName());
		themeInfo.setVersionCode(cfg.getVersionCode());
		themeInfo.setVersionName(cfg.getVersionName());
		themeInfo.setApplicationSize(cfg.getApplicationSize());
		downloadSize = cfg.getApplicationSize();
		themeInfo.setAuthor(cfg.getAuthor());
		themeInfo.setIntroduction(cfg.getIntroduction());
		themeInfo.setUpdateTime(cfg.getUpdateTime());
	}

	public void reloadThumb() {
//		Log.d(LOG_TAG, "reloadThumb,pkg="+lockInfo.getPackageName());
		if (installed) {
			thumbImage = BitmapFactory.decodeFile(PathTool
					.getThumbFile(themeInfo.getPackageName()));
		} else {
			String thumbPath = PathTool
					.getThumbFile(themeInfo.getPackageName());
			if (new File(thumbPath).exists()) {
				thumbImage = BitmapFactory.decodeFile(thumbPath);
			}
		}
	}
	
	private void checkThemePrefix() {
			String namePrefix[]={"CoCo主题_","CoCoTheme_","CoCo主題_"};
			for(String name:namePrefix){
				if (displayName!=null 
						&& displayName.startsWith(name)) {
					displayName = displayName.substring(name.length());
					break;
				}			
			}
	}
}
