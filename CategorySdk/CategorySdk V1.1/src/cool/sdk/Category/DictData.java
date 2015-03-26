package cool.sdk.Category;


import java.util.Locale;


public class DictData
{
	
	int id;
	String cn;
	String en;
	int od;
	
	public DictData(
			int id ,
			String cn ,
			String en ,
			int od )
	{
		// TODO Auto-generated constructor stub
		this.id = id;
		this.cn = cn;
		this.en = en;
		this.od = od;
	}
	
	public DictData(
			DictData data )
	{
		this.id = data.id;
		this.cn = data.cn;
		this.en = data.en;
		this.od = data.od;
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getCn()
	{
		return cn;
	}
	
	public String getEn()
	{
		return en;
	}
	
	public int getOd()
	{
		return od;
	}
	
	public String getTitle()
	{
		if( Locale.CHINA.equals( Locale.getDefault() ) )
		{
			return cn;
		}
		return en;
	}
}
