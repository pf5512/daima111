package cool.sdk.Category;


import java.util.Locale;


public class RecommendApkInfo
{
	
	String pkgName;//包名
	String apkType;//类型：2：应用程序
	String apkDLInfo;//下载时文字提示
	String apkVersionCode;//版本号
	String apkVersionName;//版本名
	int apkSize;//apk大小
	String apkCN;//中文名
	String apkEN;//英文名
	String apkFN;//繁体名
	String apkIconpath;//icon路径
	int flag;//第一位标志该应用是否可以WIFI后台下载(flag&0x1)==0x1	第二位标志该应用是否用导入到APPSTORE下载(flag&0x2)==0x2
	
	public RecommendApkInfo(
			String pkgName ,
			String apkType ,
			String apkDLInfo ,
			String apkVersionCode ,
			String apkVersionName ,
			int apkSize ,
			String apkCN ,
			String apkEN ,
			String apkFN ,
			String apkIconpath ,
			int flag )
	{
		// TODO Auto-generated constructor stub
		this.pkgName = pkgName;
		this.apkType = apkType;
		this.apkDLInfo = apkDLInfo;
		this.apkVersionCode = apkVersionCode;
		this.apkVersionName = apkVersionName;
		this.apkSize = apkSize;
		this.apkCN = apkCN;
		this.apkEN = apkEN;
		this.apkFN = apkFN;
		this.apkIconpath = apkIconpath;
		this.flag = flag;
	}
	
	public String getPkgName()
	{
		return pkgName;
	}
	
	public String getApkType()
	{
		return apkType;
	}
	
	public String getApkDLInfo()
	{
		return apkDLInfo;
	}
	
	public String getApkVersionCode()
	{
		return apkVersionCode;
	}
	
	public String getApkVersionName()
	{
		return apkVersionName;
	}
	
	public int getApkSize()
	{
		return apkSize;
	}
	
	public String getApkCN()
	{
		return apkCN;
	}
	
	public String getApkEN()
	{
		return apkEN;
	}
	
	public String getApkFN()
	{
		return apkFN;
	}
	
	public String getApkIconpath()
	{
		return apkIconpath;
	}
	
	public int getFlag()
	{
		return flag;
	}
	
	public String getTitle()
	{
		if( Locale.CHINA.equals( Locale.getDefault() ) )
		{
			return apkCN;
		}
		else if( Locale.TAIWAN.equals( Locale.getDefault() ) )
		{
			return apkFN;
		}
		return apkEN;
	}
}
