package com.iLoong.launcher.Functions.Tab;


import java.io.Serializable;

import android.content.pm.ResolveInfo;


public class TabPluginMetaData implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String pluginId;
	public String pluginTitle;
	public String url;
	public String cnName;
	public String enName;
	public String twName;
	public String packageName;
	public String className;
	public int order;
	public boolean show;
	public int versionCode;
	public String versionName;
	public boolean loadFromInternal = false;
	public ResolveInfo resolveInfo;
	
	public String getChineseName()
	{
		return cnName;
	}
	
	public void setChineseName(
			String chineseName )
	{
		this.cnName = chineseName;
	}
	
	public String getEnglishName()
	{
		return enName;
	}
	
	public void setEnglishName(
			String englishName )
	{
		this.enName = englishName;
	}
	
	public String getPackageName()
	{
		return packageName;
	}
	
	public void setPackageName(
			String packageName )
	{
		this.packageName = packageName;
	}
	
	public String getClassName()
	{
		return className;
	}
	
	public void setClassName(
			String className )
	{
		this.className = className;
	}
	
	public int getOrder()
	{
		return order;
	}
	
	public void setOrder(
			int order )
	{
		this.order = order;
	}
	
	public boolean isShow()
	{
		return show;
	}
	
	public void setShow(
			boolean show )
	{
		this.show = show;
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
	
	public String getVersionName()
	{
		return versionName;
	}
	
	public void setVersionName(
			String versionName )
	{
		this.versionName = versionName;
	}
	
	public ResolveInfo getResolveInfo()
	{
		return resolveInfo;
	}
	
	public void setResolveInfo(
			ResolveInfo resolveInfo )
	{
		this.resolveInfo = resolveInfo;
		this.packageName = resolveInfo.activityInfo.packageName;
		this.className = resolveInfo.activityInfo.name;
	}
}
