package com.coco.lock2.lockbox.util;


public class DownApkNode
{
	
	public String packname;
	public DownType downType; //p07 = 1 下载的是解锁。p07=2下载的是主题	
	
	public DownApkNode(
			String packname ,
			DownType downType )
	{
		this.packname = packname;
		this.downType = downType;
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
