package com.iLoong.launcher.macinfo;


import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;


public class PackageInfoEx
{
	
	private PackageInfo pInfo;
	private Drawable icon; // appӦ��ͼ��
	private String appName;// appӦ����
	private String packgeName; // app�İ���
	private long size; // app�Ĵ�С
	private String localtion; // app��װλ��
	private String versionName; // app�汾��
	private int versionCode; // app�汾��
	
	public Drawable getIcon()
	{
		return icon;
	}
	
	public void setIcon(
			Drawable icon )
	{
		this.icon = icon;
	}
	
	public String getAppName()
	{
		return appName;
	}
	
	public void setAppName(
			String appName )
	{
		this.appName = appName;
	}
	
	public String getPackgeName()
	{
		return packgeName;
	}
	
	public void setPackgeName(
			String packgeName )
	{
		this.packgeName = packgeName;
	}
	
	public long getSize()
	{
		return size;
	}
	
	public void setSize(
			long size )
	{
		this.size = size;
	}
	
	public String getLocaltion()
	{
		return localtion;
	}
	
	public void setLocaltion(
			String localtion )
	{
		this.localtion = localtion;
	}
	
	public PackageInfo getPackageInfo()
	{
		return pInfo;
	}
	
	public void setPackageInfo(
			PackageInfo pInfo )
	{
		this.pInfo = pInfo;
	}
	
	public String getVersionName()
	{
		return versionName;
	}
	
	public void setVersionName(
			String versionName )
	{
		this.versionName = versionName;
	}
	
	public int getVersionCode()
	{
		return versionCode;
	}
	
	public void setVersionCode(
			int versionCode )
	{
		this.versionCode = versionCode;
	}
}
