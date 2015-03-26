package com.iLoong.launcher.DesktopEdit;


public class MessageData<T>
{
	
	public Object object;
	public String title;
	public VIEW_TYPE view_type;
	public boolean dispose;
	public int tabIndex;
	
	//当前处于第几个tab
	public int getTabIndex()
	{
		return this.tabIndex;
	}
	
	public void setTabIndex(
			int tabIndex )
	{
		this.tabIndex = tabIndex;
	}
	
	//跳转界面后是否需要释放原来的view
	public void setDispose(
			boolean dispose )
	{
		this.dispose = dispose;
	}
	
	public boolean getDispose()
	{
		return this.dispose;
	}
	
	//数据类型
	public MessageData(
			VIEW_TYPE type )
	{
		this.view_type = type;
	}
	
	public void setTitle(
			String title )
	{
		this.title = title;
	}
	
	//标题名称
	public String getTitle()
	{
		return this.title;
	}
	
	public void setObject(
			Object object )
	{
		this.object = object;
	}
	
	//需要传送的流
	public Object getObject()
	{
		return this.object;
	}
	
	public VIEW_TYPE getMessageType()
	{
		return this.view_type;
	}
	
	//数据类型，需要新增的自己添加到后面
	public enum VIEW_TYPE
	{
		TYPE_ICON3D , TYPE_BUTTON_ICON3D , TYPE_SHORTCUT , TYPE_WIDGET3D , TYPE_SINGLE_MENU , TYPE_APP
	};
}
