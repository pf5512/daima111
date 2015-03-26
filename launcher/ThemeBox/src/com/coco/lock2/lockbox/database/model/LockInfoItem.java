package com.coco.lock2.lockbox.database.model;

public class LockInfoItem {
	private String packageName = "";
	private String applicationName = "";
	private int versionCode = 0;
	private String versionName = "";
	private long applicationSize = 0;
	private String author = "";
	private String introduction = "";
	private String updateTime = "";
	private String applicationName_en="";
	private String introduction_en = "";
	private String lockType = "";  //解锁类型
	private int price = 0; //解锁价格
	private String chargeInfo = "";//收费说明
	
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getLockType() {
		return lockType;
	}

	public void setLockTyp(String type) {
		this.lockType = type;
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

	public String getApplicationName_en() {
		return applicationName_en;
	}

	public void setApplicationName_en(String packageName) {
		this.applicationName_en = packageName;
	}
	
	public String getIntroduction_en() {
		return introduction_en;
	}

	public void setIntroduction_en(String introduction) {
		this.introduction_en = introduction;
	}
	
	public void copyFrom(LockInfoItem item) {
		this.applicationName = item.getApplicationName();
		this.applicationSize = item.getApplicationSize();
		this.author = item.getAuthor();
		this.introduction = item.getIntroduction();
		this.packageName = item.getPackageName();
		this.updateTime = item.getUpdateTime();
		this.versionCode = item.getVersionCode();
		this.versionName = item.getVersionName();
		this.applicationName_en = item.getApplicationName_en();
		this.introduction_en = item.getIntroduction_en();
	}
}
