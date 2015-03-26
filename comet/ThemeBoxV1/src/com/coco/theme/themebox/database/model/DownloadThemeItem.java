package com.coco.theme.themebox.database.model;

public class DownloadThemeItem {

	private ThemeInfoItem themeInfo = new ThemeInfoItem();
	private long downloadSize = 0;
	private DownloadStatus downloadStatus = DownloadStatus.StatusInit;

	public ThemeInfoItem getThemeInfo() {
		return themeInfo;
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

	public void copyFromThemeInfo(ThemeInfoItem item) {
		themeInfo.copyFrom(item);
	}

	public String getPackageName() {
		return themeInfo.getPackageName();
	}

	public void setPackageName(String packageName) {
		themeInfo.setPackageName(packageName);
	}

	public String getApplicationName() {
		return themeInfo.getApplicationName();
	}

	public void setApplicationName(String applicationName) {
		themeInfo.setApplicationName(applicationName);
	}

	public int getVersionCode() {
		return themeInfo.getVersionCode();
	}

	public void setVersionCode(int versionCode) {
		themeInfo.setVersionCode(versionCode);
	}

	public String getVersionName() {
		return themeInfo.getVersionName();
	}

	public void setVersionName(String versionName) {
		themeInfo.setVersionName(versionName);
	}

	public long getApplicationSize() {
		return themeInfo.getApplicationSize();
	}

	public void setApplicationSize(long applicationSize) {
		themeInfo.setApplicationSize(applicationSize);
	}

	public String getAuthor() {
		return themeInfo.getAuthor();
	}

	public void setAuthor(String author) {
		themeInfo.setAuthor(author);
	}

	public String getIntroduction() {
		return themeInfo.getIntroduction();
	}

	public void setIntroduction(String introduction) {
		themeInfo.setIntroduction(introduction);
	}

	public String getUpdateTime() {
		return themeInfo.getUpdateTime();
	}

	public void setUpdateTime(String updateTime) {
		themeInfo.setUpdateTime(updateTime);
	}
}
