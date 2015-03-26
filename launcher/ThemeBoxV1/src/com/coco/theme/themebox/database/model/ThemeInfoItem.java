package com.coco.theme.themebox.database.model;

public class ThemeInfoItem {

	private String packageName = "";
	private String applicationName = "";
	private int versionCode = 0;
	private String versionName = "";
	private long applicationSize = 0;
	private String author = "";
	private String introduction = "";
	private String updateTime = "";

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public long getApplicationSize() {
		return applicationSize;
	}

	public void setApplicationSize(long applicationSize) {
		this.applicationSize = applicationSize;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public void copyFrom(ThemeInfoItem item) {
		this.applicationName = item.getApplicationName();
		this.applicationSize = item.getApplicationSize();
		this.author = item.getAuthor();
		this.introduction = item.getIntroduction();
		this.packageName = item.getPackageName();
		this.updateTime = item.getUpdateTime();
		this.versionCode = item.getVersionCode();
		this.versionName = item.getVersionName();
	}
}
