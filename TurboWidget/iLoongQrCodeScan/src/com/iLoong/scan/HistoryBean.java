package com.iLoong.scan;


public class HistoryBean
{
	
	private int _id;
	private String text;
	private int type;
	private String currtime;
	
	public String getCurrtime()
	{
		return currtime;
	}
	
	public void setCurrtime(
			String currtime )
	{
		this.currtime = currtime;
	}
	
	public int getType()
	{
		return type;
	}
	
	public void setType(
			int type )
	{
		this.type = type;
	}
	
	public int get_id()
	{
		return _id;
	}
	
	public void set_id(
			int _id )
	{
		this._id = _id;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(
			String text )
	{
		this.text = text;
	}
}
