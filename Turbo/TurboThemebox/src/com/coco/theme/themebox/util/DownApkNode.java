package com.coco.theme.themebox.util;


public class DownApkNode
{
	
	public String packname;
	public DownType downType;
	public String tabType;
	public String apkName;
	
	public DownApkNode(
			String packname ,
			DownType downType )
	{
		this.packname = packname;
		this.downType = downType;
		tabType = apkName = "";
	}
	
	public DownApkNode(
			String packname ,
			DownType downType ,
			String tabType ,
			String name )
	{
		this.packname = packname;
		this.downType = downType;
		this.tabType = tabType;
		this.apkName = name;
	}
	
	public String getParams()
	{
		int p06 = -1;
		if( downType == DownType.TYPE_APK_DOWNLOAD )
		{
			p06 = 1;
		}
		else if( downType == DownType.TYPE_APK_UPDATE )
		{
			p06 = 2;
		}
		String result = "p01=" + packname + "&p06=" + p06;
		return result;
	}
}
