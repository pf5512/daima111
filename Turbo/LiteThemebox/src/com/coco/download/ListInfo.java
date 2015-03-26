package com.coco.download;


import java.util.ArrayList;
import java.util.List;


public class ListInfo
{
	
	private String tabid;
	private String enname;
	private String cnname;
	private String twname;
	private String typeid;
	/***********************************************
	 * 1 主题 2 锁屏 3 小组件 4 场景 5 壁纸 6 字体
	 ************************************************/
	private List<ItemInfo> itemList = new ArrayList<ItemInfo>();
	
	public String getTabid()
	{
		return tabid;
	}
	
	public void setTabid(
			String tabid )
	{
		this.tabid = tabid;
	}
	
	public String getEnname()
	{
		return enname;
	}
	
	public void setEnname(
			String enname )
	{
		this.enname = enname;
	}
	
	public String getCnname()
	{
		return cnname;
	}
	
	public void setCnname(
			String cnname )
	{
		this.cnname = cnname;
	}
	
	public String getTwname()
	{
		return twname;
	}
	
	public void setTwname(
			String twname )
	{
		this.twname = twname;
	}
	
	public List<ItemInfo> getItemList()
	{
		return itemList;
	}
	
	public void setItemList(
			List<ItemInfo> itemList )
	{
		this.itemList = itemList;
	}
	
	public String getTypeid()
	{
		return typeid;
	}
	
	public void setTypeid(
			String typeid )
	{
		this.typeid = typeid;
	}
}
