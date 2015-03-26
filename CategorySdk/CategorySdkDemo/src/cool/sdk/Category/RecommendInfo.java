package cool.sdk.Category;


import java.util.Map;


public class RecommendInfo
{
	
	int folderID;
	String folderType;
	String folderCN;
	String folderEN;
	String folderFN;
	Map<String , RecommendApkInfo> apkinfoMap;
	
	public RecommendInfo(
			int folderID ,
			String folderType ,
			String folderCN ,
			String folderEN ,
			String folderFN ,
			Map<String , RecommendApkInfo> apkinfoMap )
	{
		// TODO Auto-generated constructor stub
		this.folderID = folderID;
		this.folderType = folderType;
		this.folderCN = folderCN;
		this.folderEN = folderEN;
		this.folderFN = folderFN;
		this.apkinfoMap = apkinfoMap;
	}
	
	public int getFolderID()
	{
		return folderID;
	}
	
	public String getFolderType()
	{
		return folderType;
	}
	
	public String getFolderCN()
	{
		return folderCN;
	}
	
	public String getFolderEN()
	{
		return folderEN;
	}
	
	public String getFolderFN()
	{
		return folderFN;
	}
	
	public Map<String , RecommendApkInfo> getApkinfoMap()
	{
		return apkinfoMap;
	}
}
