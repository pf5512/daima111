package com.iLoong.launcher.smartLaytout;

import android.content.pm.ResolveInfo;


public class SLApplicationMetaData
{
	
	private String label;
	private String packageName;
	private String className;
	private ResolveInfo resolve;
	
	public SLApplicationMetaData(String label,String pkgName,String clsName){
		this.label=label;
		this.packageName=pkgName;
		this.className=clsName;
	}


	
	
	
	public ResolveInfo getResolve()
	{
		return resolve;
	}




	
	public void setResolve(
			ResolveInfo resolve )
	{
		this.resolve = resolve;
	}




	public String getLabel()
	{
		return label;
	}


	
	public void setLabel(
			String label )
	{
		this.label = label;
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
	
	
}
