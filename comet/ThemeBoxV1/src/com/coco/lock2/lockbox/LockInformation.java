package com.coco.lock2.lockbox;

import java.io.File;
import java.util.Locale;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.coco.theme.themebox.util.Log;
import com.coco.lock2.lockbox.database.model.DownloadLockItem;
import com.coco.lock2.lockbox.database.model.DownloadStatus;
import com.coco.lock2.lockbox.database.model.LockInfoItem;
import com.coco.lock2.lockbox.database.remoting.DownloadLockService;
import com.coco.lock2.lockbox.util.ContentConfig;
import com.coco.lock2.lockbox.util.PathTool;

public class LockInformation {
	private String className = "";
	private boolean installed = false;
	private String displayName = "";
	private LockInfoItem lockInfo = new LockInfoItem();
	private long downloadSize = 0;
	private DownloadStatus downloadStatus = DownloadStatus.StatusInit;
	private Bitmap thumbImage = null;
	private String settingClassName = "";
	private boolean needLoadDetail = true;
	private boolean mSystem = false;
	private boolean downloaded = false;
	private boolean needUpdate = false;
	private LockInfoItem relativeinfo = new LockInfoItem();
	
	public String getPackageName() {
		return lockInfo.getPackageName();
	}

	public String getLockType(){
		return lockInfo.getLockType();
	}
	
	public String getClassName() {
		return className;
	}

	public void setNeedUpdate(boolean state){
		needUpdate = state;
	}
	
	public boolean getNeedUpdate(){
		return needUpdate;
	}
	
	public String getVersionName(){
		return lockInfo.getVersionName();
	}
	
	public int getVersionCode(){
		return lockInfo.getVersionCode();
	}
	
	public boolean isInstalled() {
		return installed;
	}

	public void setInastalled(boolean state){
		installed = state;
	}
	
	public boolean isSystem() {
		return mSystem;
	}

	public boolean isDownloaded(Context context) {
		File file = new File(PathTool.getAppDir()+"/"+getPackageName()+".apk");
		
		Log.v("isDownload", "file exists = "+file.exists()+"    downstatus = "+getDownloadStatus()+"    downloaded = "+downloaded);
		Log.v("isDownload", "file path = "+PathTool.getAppDir()+getPackageName()+".apk");
		if (!file.exists()&& (getDownloadStatus() == DownloadStatus.StatusInit || getDownloadStatus() == DownloadStatus.StatusFinish)) {
			File f1 = new File(PathTool.getDownloadingDir()+getPackageName()+"_app.tmp");
			f1.delete();
			DownloadLockService dSv = new DownloadLockService(context);
			dSv.updateDownloadSizeAndStatus(getPackageName(), 0, DownloadStatus.StatusInit);
			return false;
		}
		return downloaded;
	}

	public void setSystem(boolean sys) {
		mSystem = sys;
	}

	public long getApplicationSize() {
		return lockInfo.getApplicationSize();
	}

	public String getAuthor() {
		return lockInfo.getAuthor();
	}

	public String getIntroduction() {
		return lockInfo.getIntroduction();
	}

	public String getApplicationName_en(){
		return lockInfo.getApplicationName_en();
	}
	
	public String getIntroduction_en(){
		return lockInfo.getIntroduction_en();
	}
	
	public LockInfoItem getInfoItem() {
		return lockInfo;
	}

	public LockInfoItem getRelativeInfo(){
		return relativeinfo;
	}
	
	public int getDownloadPercent() {
		if (lockInfo.getApplicationSize() <= 0) {
			return 0;
		}

		int result = (int) (downloadSize * 100 / lockInfo.getApplicationSize());
		if (result < 0) {
			result = 0;
		} else if (result > 100) {
			result = 100;
		}

		return result;
	}

	public String getSettingClassName() {
		return settingClassName;
	}

	public boolean isSettingExist() {
		if (settingClassName.equals("")) {
			return false;
		}

		return true;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Bitmap getThumbImage() {
		return thumbImage;
	}
	
	public void setThumbImage(Context mContext, String pkgName, String actName) {
		File f = mContext.getDir("coco", Context.MODE_PRIVATE);
		String thumbPath = f + "/" + pkgName + "/" + actName + ".tupian";
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
		lockInfo.setApplicationSize(totalSize);
	}

	public boolean isComponent(String pkgName, String clsName) {
		if (lockInfo.getPackageName().equals(pkgName)
				&& className.equals(clsName)) {
			return true;
		}

		return false;
	}

	public boolean isComponent(ComponentName comName) {
		if (lockInfo.getPackageName().equals(comName.getPackageName())
				&& className.equals(comName.getClassName())) {
			return true;
		}

		return false;
	}

	public boolean isPackage(String pkgName) {
		return lockInfo.getPackageName().equals(pkgName);
	}

	public void setActivity(Context cxt, ActivityInfo activity) {
		className = activity.name;
		installed = true;
		thumbImage = null;
		needLoadDetail = true;
		downloadStatus = DownloadStatus.StatusFinish;
		lockInfo = new LockInfoItem();
		lockInfo.setPackageName(activity.packageName);
		String versionName="";
		try {
			versionName = cxt.getPackageManager().getPackageInfo(activity.packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		lockInfo.setVersionName(versionName);
		int versionCode = 0;
		try {
			versionCode = cxt.getPackageManager().getPackageInfo(activity.packageName, 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		lockInfo.setVersionCode(versionCode);
		downloadSize = 0;
		displayName = activity.loadLabel(cxt.getPackageManager()).toString();
		checkLockPrefix();
		settingClassName = "";
		mSystem = cxt.getPackageName().equals(activity.packageName);
		downloaded = true;
	}

	public void setDownloadItem(DownloadLockItem item) {
		setLockItem(item.getLockInfo());
		downloadStatus = item.getDownloadStatus();
		downloadSize = item.getDownloadSize();
		downloaded = true;
	}

	public void setLockItem(LockInfoItem item) {
		className = "";
		installed = false;
		thumbImage = null;
		needLoadDetail = true;
		downloadStatus = DownloadStatus.StatusInit;
		lockInfo.copyFrom(item);
		downloadSize = 0;
		String systemLauncher = Locale.getDefault().getLanguage().toString();
		if(systemLauncher.equals("en")){
			displayName = lockInfo.getApplicationName_en();
		}
		else
		{
			displayName = lockInfo.getApplicationName();			
		}
		checkLockPrefix();
		settingClassName = "";
		downloaded = false;
		mSystem = false;
	}
	
	public void setRelativeItem(LockInfoItem item){
		lockInfo.copyFrom(item);
	}
	
	
	private void checkLockPrefix() {
		String namePrefix[]={"CoCo锁屏","CoCo鎖屏","CoCo Locker "};
		for(String name:namePrefix){
			if (displayName!=null 
					&& displayName.startsWith(name)) {
				displayName = displayName.substring(name.length());
				break;
			}			
		}
	}

	public void loadDetail(Context cxt) {
		needLoadDetail = false;
		thumbImage = null;
		if (installed) {
			ContentConfig cfg = new ContentConfig();

			try {
				Context remoteContext = cxt.createPackageContext(
						lockInfo.getPackageName(),
						Context.CONTEXT_IGNORE_SECURITY);
				cfg.loadConfig(remoteContext, className);
				loadInstallDetail(remoteContext, cfg);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			String thumbPath = PathTool.getThumbFile(lockInfo.getPackageName());
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
		lockInfo.setApplicationName(cfg.getApplicationName());
		lockInfo.setVersionCode(cfg.getVersionCode());
		lockInfo.setVersionName(cfg.getVersionName());
		lockInfo.setApplicationSize(cfg.getApplicationSize());
		downloadSize = cfg.getApplicationSize();
		lockInfo.setAuthor(cfg.getAuthor());
		lockInfo.setIntroduction(cfg.getIntroduction());
		lockInfo.setUpdateTime(cfg.getUpdateTime());
		lockInfo.setApplicationName_en(cfg.getApplicationName());
		lockInfo.setIntroduction_en(cfg.getIntroduction_en());
		settingClassName = cfg.getSettingClassName();
	}

	public void loadUpdateInstallDetail(Context remoteContext, ContentConfig cfg) {
		needLoadDetail = false;
		thumbImage = null;

		thumbImage = cfg.loadThumbImage(remoteContext);
		lockInfo.setApplicationName(cfg.getApplicationName());
		lockInfo.setVersionCode(cfg.getVersionCode());
		lockInfo.setVersionName(cfg.getVersionName());
		lockInfo.setAuthor(cfg.getAuthor());
		lockInfo.setIntroduction(cfg.getIntroduction());
		lockInfo.setUpdateTime(cfg.getUpdateTime());
		lockInfo.setApplicationName_en(cfg.getApplicationName());
		lockInfo.setIntroduction_en(cfg.getIntroduction_en());
		settingClassName = cfg.getSettingClassName();
	}
	
	public void reloadThumb() {
		if (installed) {
			thumbImage = BitmapFactory.decodeFile(PathTool
					.getThumbFile(lockInfo.getPackageName()));
		} else {
			String thumbPath = PathTool.getThumbFile(lockInfo.getPackageName());
			if (new File(thumbPath).exists()) {
				thumbImage = BitmapFactory.decodeFile(thumbPath);
			}
		}
	}
}
