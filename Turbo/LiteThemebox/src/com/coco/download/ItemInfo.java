package com.coco.download;


public class ItemInfo
{
	
	private String resid;
	private String enname;
	private String cnname;
	// private String twname;
	private String resurl;
	private String packname;
	private String size;
	private String author;
	private String aboutchinese;
	private String version;
	private String versionname;
	private String aboutenglish;
	private String price;
	private String pricePoint;
	private String pricedetail;
	private String icon;
	private String thumbimg;
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
	
	public String getResid()
	{
		return resid;
	}
	
	public void setResid(
			String resid )
	{
		this.resid = resid;
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
	
	// public String getTwname() {
	// return twname;
	// }
	// public void setTwname(String twname) {
	// this.twname = twname;
	// }
	public String getResurl()
	{
		return resurl;
	}
	
	public void setResurl(
			String resurl )
	{
		this.resurl = resurl;
	}
	
	public String getPackname()
	{
		return packname;
	}
	
	public void setPackname(
			String packname )
	{
		this.packname = packname;
	}
	
	public String getSize()
	{
		return size;
	}
	
	public void setSize(
			String size )
	{
		this.size = size;
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
	
	public String getAboutchinese()
	{
		return aboutchinese;
	}
	
	public void setAboutchinese(
			String aboutchinese )
	{
		this.aboutchinese = aboutchinese;
	}
	
	public String getVersion()
	{
		return version;
	}
	
	public void setVersion(
			String version )
	{
		this.version = version;
	}
	
	public String getVersionname()
	{
		return versionname;
	}
	
	public void setVersionname(
			String versionname )
	{
		this.versionname = versionname;
	}
	
	public String getAboutenglish()
	{
		return aboutenglish;
	}
	
	public void setAboutenglish(
			String aboutenglish )
	{
		this.aboutenglish = aboutenglish;
	}
	
	public String getPrice()
	{
		return price;
	}
	
	public void setPrice(
			String price )
	{
		if( price == null )
		{
			this.price = "0";
			return;
		}
		this.price = price;
	}
	
	public String getPricedetail()
	{
		return pricedetail;
	}
	
	public void setPricedetail(
			String pricedetail )
	{
		this.pricedetail = pricedetail;
	}
	
	public String getIcon()
	{
		return icon;
	}
	
	public void setIcon(
			String icon )
	{
		this.icon = icon;
	}
	
	public String getThumbimg()
	{
		return thumbimg;
	}
	
	public void setThumbimg(
			String thumbimg )
	{
		this.thumbimg = thumbimg;
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
	
	public String getPricePoint()
	{
		return pricePoint;
	}
	
	public void setPricePoint(
			String pricePoint )
	{
		this.pricePoint = pricePoint;
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
