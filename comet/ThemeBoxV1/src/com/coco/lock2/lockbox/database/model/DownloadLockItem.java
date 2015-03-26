package com.coco.lock2.lockbox.database.model;

public class DownloadLockItem {

	private LockInfoItem lockInfo = new LockInfoItem();
	private long downloadSize = 0;
	private DownloadStatus downloadStatus = DownloadStatus.StatusInit;

	public LockInfoItem getLockInfo() {
		return lockInfo;
	}

	public long getDownloadSize() {
		return downloadSize;
	}

	public void setDownloadSize(long downloadSize) {
		this.downloadSize = downloadSize;
	}

	public DownloadStatus getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(DownloadStatus status) {
		this.downloadStatus = status;
	}

	public void copyFromLockInfo(LockInfoItem item) {
		lockInfo.copyFrom(item);
	}

	public String getPackageName() {
		return lockInfo.getPackageName();
	}

	public void setPackageName(String packageName) {
		lockInfo.setPackageName(packageName);
	}

	public String getApplicationName() {
		return lockInfo.getApplicationName();
	}

	public void setApplicationName(String applicationName) {
		lockInfo.setApplicationName(applicationName);
	}

	public String getApplicationName_en() {
		return lockInfo.getApplicationName_en();
	}

	public void setApplicationName_en(String applicationName) {
		lockInfo.setApplicationName_en(applicationName);
	}
	
	public int getVersionCode() {
		return lockInfo.getVersionCode();
	}

	public void setVersionCode(int versionCode) {
		lockInfo.setVersionCode(versionCode);
	}

	public String getVersionName() {
		return lockInfo.getVersionName();
	}

	public void setVersionName(String versionName) {
		lockInfo.setVersionName(versionName);
	}

	public long getApplicationSize() {
		return lockInfo.getApplicationSize();
	}

	public void setApplicationSize(long applicationSize) {
		lockInfo.setApplicationSize(applicationSize);
	}

	public String getAuthor() {
		return lockInfo.getAuthor();
	}

	public void setAuthor(String author) {
		lockInfo.setAuthor(author);
	}

	public String getIntroduction() {
		return lockInfo.getIntroduction();
	}

	public void setIntroduction(String introduction) {
		lockInfo.setIntroduction(introduction);
	}

	public String getIntroduction_en() {
		return lockInfo.getIntroduction_en();
	}

	public void setIntroduction_en(String introduction) {
		lockInfo.setIntroduction_en(introduction);
	}
	
	public String getUpdateTime() {
		return lockInfo.getUpdateTime();
	}

	public void setUpdateTime(String updateTime) {
		lockInfo.setUpdateTime(updateTime);
	}
}
