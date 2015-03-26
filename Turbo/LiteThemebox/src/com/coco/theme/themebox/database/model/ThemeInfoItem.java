package com.coco.theme.themebox.database.model;


public class ThemeInfoItem
{
	
	private String packageName = "";
	private String applicationName = "";
	private int versionCode = 0;
	private String versionName = "";
	private long applicationSize = 0;
	private String author = "";
	private String introduction = "";
	private String updateTime = "";
	private String applicationName_en = "";
	private String introduction_en = "";
	private int price = 0;// zjp
	private String pricepoint = "";
	private String type = "";
	private String resid = "";
	private String resurl = "";
	private String thumbimgUrl = "";
	private String[] previewlist = new String[]{};
	private String index = "";
	private String enginepackname;
	private String engineurl;
	private String enginesize;
	private String enginedesc;
	private String thirdparty;
	
	public String getIndex()
	{
		return index;
	}
	
	public void setIndex(
			String index )
	{
		this.index = index;
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
	
	public String getApplicationName()
	{
		return applicationName;
	}
	
	public void setApplicationName(
			String applicationName )
	{
		this.applicationName = applicationName;
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
	
	public long getApplicationSize()
	{
		return applicationSize;
	}
	
	public void setApplicationSize(
			long applicationSize )
	{
		this.applicationSize = applicationSize;
	}
	
	public String getAuthor()
	{
		return author;
	}
	
	public void setAuthor(
			String author )
	{
		this.author = author;
	}
	
	public String getIntroduction()
	{
		return introduction;
	}
	
	public void setIntroduction(
			String introduction )
	{
		this.introduction = introduction;
	}
	
	public String getUpdateTime()
	{
		return updateTime;
	}
	
	public void setUpdateTime(
			String updateTime )
	{
		this.updateTime = updateTime;
	}
	
	public String getThumbimgUrl()
	{
		return thumbimgUrl;
	}
	
	public void setThumbimgUrl(
			String thumbimgUrl )
	{
		this.thumbimgUrl = thumbimgUrl;
	}
	
	public String[] getPreviewlist()
	{
		return previewlist;
	}
	
	public void setPreviewlist(
			String[] previewlist )
	{
		this.previewlist = previewlist;
	}
	
	public String getResurl()
	{
		return resurl;
	}
	
	public void setResurl(
			String resurl )
	{
		this.resurl = resurl;
	}
	
	public String getResid()
	{
		return resid;
	}
	
	public void setResid(
			String resid )
	{
		this.resid = resid;
	}
	
	public void copyFrom(
			ThemeInfoItem item )
	{
		this.applicationName = item.getApplicationName();
		this.applicationSize = item.getApplicationSize();
		this.author = item.getAuthor();
		this.introduction = item.getIntroduction();
		this.packageName = item.getPackageName();
		this.updateTime = item.getUpdateTime();
		this.applicationName_en = item.getApplicationName_en();
		this.introduction_en = item.getIntroduction_en();
		this.versionCode = item.getVersionCode();
		this.versionName = item.getVersionName();
		this.thumbimgUrl = item.getThumbimgUrl();
		this.previewlist = item.getPreviewlist();
		this.resurl = item.getResurl();
		this.resid = item.getResid();
		this.type = item.getType();
		this.price = item.getPrice();
		this.pricepoint = item.getPricepoint();
		this.enginepackname = item.getEnginepackname();
		this.engineurl = item.getEngineurl();
		this.enginedesc = item.getEnginedesc();
		this.enginesize = item.getEnginesize();
		this.thirdparty = item.getThirdparty();
	}
	
	public String getType()
	{
		return type;
	}
	
	public void setType(
			String type )
	{
		this.type = type;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public void setPrice(
			int price )
	{
		this.price = price;
	}
	
	public String getApplicationName_en()
	{
		return applicationName_en;
	}
	
	public void setApplicationName_en(
			String packageName )
	{
		this.applicationName_en = packageName;
	}
	
	public String getIntroduction_en()
	{
		return introduction_en;
	}
	
	public void setIntroduction_en(
			String introduction )
	{
		this.introduction_en = introduction;
	}
	
	public String getPricepoint()
	{
		return pricepoint;
	}
	
	public void setPricepoint(
			String pricepoint )
	{
		this.pricepoint = pricepoint;
	}
	
	public String getEnginepackname()
	{
		return enginepackname;
	}
	
	public void setEnginepackname(
			String enginepackname )
	{
		this.enginepackname = enginepackname;
	}
	
	public String getEngineurl()
	{
		return engineurl;
	}
	
	public void setEngineurl(
			String engineurl )
	{
		this.engineurl = engineurl;
	}
	
	public String getEnginesize()
	{
		return enginesize;
	}
	
	public void setEnginesize(
			String enginesize )
	{
		this.enginesize = enginesize;
	}
	
	public String getEnginedesc()
	{
		return enginedesc;
	}
	
	public void setEnginedesc(
			String enginedesc )
	{
		this.enginedesc = enginedesc;
	}
	
	public String getThirdparty()
	{
		return thirdparty;
	}
	
	public void setThirdparty(
			String thirdparty )
	{
		this.thirdparty = thirdparty;
	}
}
