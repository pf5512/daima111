package com.cooee.searchbar;


public class HistoryBean
{
	
	private int _id;
	private String keywords;
	private String url;
	
	public int get_id()
	{
		return _id;
	}
	
	public void set_id(
			int _id )
	{
		this._id = _id;
	}
	
	public String getKeywords()
	{
		return keywords;
	}
	
	public void setKeywords(
			String keywords )
	{
		this.keywords = keywords;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public void setUrl(
			String url )
	{
		this.url = url;
	}
}
