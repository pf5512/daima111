package com.coco.theme.themebox.database.model;


public class DownloadThemeItem
{
	
	private ThemeInfoItem themeInfo = new ThemeInfoItem();
	private long downloadSize = 0;
	private DownloadStatus downloadStatus = DownloadStatus.StatusInit;
	
	public ThemeInfoItem getThemeInfo()
	{
		return themeInfo;
	}
	
	public long getDownloadSize()
	{
		return downloadSize;
	}
	
	public void setDownloadSize(
			long downloadSize )
	{
		this.downloadSize = downloadSize;
	}
	
	public DownloadStatus getDownloadStatus()
	{
		return downloadStatus;
	}
	
	public void setDownloadStatus(
			DownloadStatus status )
	{
		this.downloadStatus = status;
	}
	
	public void copyFromThemeInfo(
			ThemeInfoItem item )
	{
		themeInfo.copyFrom( item );
	}
	
	public String getPackageName()
	{
		return themeInfo.getPackageName();
	}
	
	public void setPackageName(
			String packageName )
	{
		themeInfo.setPackageName( packageName );
	}
	
	public String getApplicationName()
	{
		return themeInfo.getApplicationName();
	}
	
	public void setApplicationName(
			String applicationName )
	{
		themeInfo.setApplicationName( applicationName );
	}
	
	public String getApplicationName_en()
	{
		return themeInfo.getApplicationName_en();
	}
	
	public void setApplicationName_en(
			String applicationName )
	{
		themeInfo.setApplicationName_en( applicationName );
	}
	
	public int getVersionCode()
	{
		return themeInfo.getVersionCode();
	}
	
	public void setVersionCode(
			int versionCode )
	{
		themeInfo.setVersionCode( versionCode );
	}
	
	public String getVersionName()
	{
		return themeInfo.getVersionName();
	}
	
	public void setVersionName(
			String versionName )
	{
		themeInfo.setVersionName( versionName );
	}
	
	public long getApplicationSize()
	{
		return themeInfo.getApplicationSize();
	}
	
	public void setApplicationSize(
			long applicationSize )
	{
		themeInfo.setApplicationSize( applicationSize );
	}
	
	public String getAuthor()
	{
		return themeInfo.getAuthor();
	}
	
	public int getPrice()
	{
		return themeInfo.getPrice();
	}
	
	public String getType()
	{
		return themeInfo.getType();
	}
	
	public void setType(
			String type )
	{
		themeInfo.setType( type );
	}
	
	public void setPrice(
			int price )
	{
		themeInfo.setPrice( price );
	}
	
	public void setPricePoint(
			String point )
	{
		themeInfo.setPricepoint( point );
	}
	
	public String getPricePoint()
	{
		return themeInfo.getPricepoint();
	}
	
	public void setAuthor(
			String author )
	{
		themeInfo.setAuthor( author );
	}
	
	public String getIntroduction()
	{
		return themeInfo.getIntroduction();
	}
	
	public void setIntroduction(
			String introduction )
	{
		themeInfo.setIntroduction( introduction );
	}
	
	public String getIntroduction_en()
	{
		return themeInfo.getIntroduction_en();
	}
	
	public void setIntroduction_en(
			String introduction )
	{
		themeInfo.setIntroduction_en( introduction );
	}
	
	public String getUpdateTime()
	{
		return themeInfo.getUpdateTime();
	}
	
	public void setUpdateTime(
			String updateTime )
	{
		themeInfo.setUpdateTime( updateTime );
	}
	
	public void setEnginepackname(
			String enginepackname )
	{
		themeInfo.setEnginepackname( enginepackname );
	}
	
	public void setEngineurl(
			String engineurl )
	{
		themeInfo.setEngineurl( engineurl );
	}
	
	public void setEnginesize(
			String enginesize )
	{
		themeInfo.setEnginesize( enginesize );
	}
	
	public void setEnginedesc(
			String enginedesc )
	{
		themeInfo.setEnginedesc( enginedesc );
	}
	
	public String getEnginepackname()
	{
		return themeInfo.getEnginepackname();
	}
	
	public String getEngineurl()
	{
		return themeInfo.getEngineurl();
	}
	
	public String getEnginesize()
	{
		return themeInfo.getEnginesize();
	}
	
	public String getEnginedesc()
	{
		return themeInfo.getEnginedesc();
	}
}
