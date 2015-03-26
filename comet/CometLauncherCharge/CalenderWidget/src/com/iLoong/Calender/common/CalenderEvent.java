package com.iLoong.Calender.common;


public class CalenderEvent
{
	private  String _id;
	private String title;
	private String dtstart;
	private String dtend;
	
	public String get_id()
	{
		return _id;
	}
	
	public void set_id(
			String _id )
	{
		this._id = _id;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(
			String title )
	{
		this.title = title;
	}
	
	public String getDtstart()
	{
		return dtstart;
	}
	
	public void setDtstart(
			String dtstart )
	{
		this.dtstart = dtstart;
	}
	
	public String getDtend()
	{
		return dtend;
	}
	
	public void setDtend(
			String dtend )
	{
		this.dtend = dtend;
	}
	
}
