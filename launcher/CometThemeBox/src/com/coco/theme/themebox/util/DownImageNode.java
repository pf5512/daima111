package com.coco.theme.themebox.util;


public class DownImageNode
{
	
	public String packname;
	public DownType downType;
	
	public DownImageNode(
			String packname ,
			DownType downType )
	{
		this.packname = packname;
		this.downType = downType;
	}
	
	public String getParams()
	{
		int p09 = -1;
		if( downType == DownType.TYPE_IMAGE_PREVIEW )
		{
			p09 = 2;
		}
		else if( downType == DownType.TYPE_IMAGE_THUMB )
		{
			p09 = 1;
		}
		String result = "p01=" + packname + "&p09=" + p09;
		return result;
	}
}
