package cool.sdk.Category;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.content.Context;
import cool.sdk.download.manager.dl_info;


public class CategoryHelper extends CategoryUpdate
{
	
	//Context mContext = null;
	protected CategoryHelper(
			Context context )
	{
		super( context );
		//mContext = context;
		// TODO Auto-generated constructor stub
		//DatabaseHelper dh = new DatabaseHelper( context , "category_db" );
	}
	
	static CategoryHelper instance = null;
	
	public static CategoryHelper getInstance(
			Context context )
	{
		synchronized( CategoryHelper.class )
		{
			if( instance == null )
			{
				instance = new CategoryHelper( context );
			}
		}
		return instance;
	}
	
	@Override
	public JSONArray getIdList()
	{
		// TODO Auto-generated method stub
		JSONArray idList = new JSONArray();
		//先简单粗暴的把所有的一级目录提交
		idList.put( -1 );
		idList.put( 0 );
		idList.put( 800 );
		idList.put( 801 );
		idList.put( 802 );
		idList.put( 803 );
		idList.put( 804 );
		idList.put( 805 );
		idList.put( 806 );
		idList.put( 807 );
		idList.put( 808 );
		return idList;
	}
	
	// 获取是否处于智能分类状态：true:处于智能分类  false:不处于智能分类
	@Override
	public boolean isCategoryState()
	{
		// TODO Auto-generated method stub
		return false;//CategoryUtil.isCategoryMode();
	}
	
	// 获取DefaultLayout的配置 0: 禁止 1:显式 2：运营出来
	@Override
	protected int getConfigType()
	{
		// TODO Auto-generated method stub
		return 1;//DefaultLayout.enable_icon_category;
	}
	
	public void UpdateMapData()
	{
		try
		{
			lockMap.readLock().lock();
			if( !treeMap.isEmpty() )
			{
				treeMap.clear();
			}
			dbTool.getAllTree();
			if( !dictMap.isEmpty() )
			{
				dictMap.clear();
			}
			dbTool.getAllDict();
			if( !cateinfoMap.isEmpty() )
			{
				cateinfoMap.clear();
			}
			dbTool.getAllCateInfo();
			if( !RecommendInfoMap.isEmpty() )
			{
				RecommendInfoMap.clear();
			}
			dbTool.getAllRecommendInfo();
		}
		finally
		{
			lockMap.readLock().unlock();
		}
		if( RecommendInfoMap.isEmpty() )
		{
			decodeRecommendListJsonObject();
		}
	}
	
	//此API仅供测试使用
	public void ClearAllData()
	{
		setValue( "cateinfoFloderIDJSONArray" , (String)null );
		setValue( "Recommend_list" , (String)null );
		setValue( "Recommend_c2" , (String)null );
		setValue( "Recommend_c3" , (String)null );
		setValue( "Recommend_c4" , (String)null );
		setValue( "c0" , (String)null );
		setValue( "c1" , (String)null );
		setValue( "c2" , (String)null );
		setValue( "c3" , (String)null );
		setValue( "IsDidForeCategoryReques" , (String)null );
		setValue( KEY_CategoryRequest_START_TIME , (Long)null );
		setValue( "doCategoryStatisticsActive" , (Long)null );
		setValue( "doCategoryStatisticsRecommendActive" , (Long)null );
		dbTool.CleanAllTables();
	}
	
	@Override
	protected void onRecommendIconDownload(
			int fid )
	{
		// TODO Auto-generated method stub
		Log.v( "COOL" , "onRecommendIconDownload:" + fid );
		//CategoryParse.getInstance().addRecommendate( fid );
	}
	
	@Override
	protected void onCategoryBGRequestComplete()
	{
		// TODO Auto-generated method stub
		Log.v( "COOL" , "onCategoryBGRequestComplete" );
	}
	
	@Override
	protected void onCategoryRecommendConfigChange(
			String version )
	{
		// TODO Auto-generated method stub
		if( DEFAULT_VERSION.equals( version ) )
		{
			Log.v( "COOL" , "onCategoryRecommendConfigChange first" );
		}
		else
		{
			Log.v( "COOL" , "onCategoryRecommendConfigChange" );
		}
	}
	
	@Override
	protected void onCategoryRecommendListChange(
			String version )
	{
		// TODO Auto-generated method stub
		if( DEFAULT_VERSION.equals( version ) )
		{
			Log.v( "COOL" , "onCategoryRecommendListChange first" );
		}
		else
		{
			Log.v( "COOL" , "onCategoryRecommendListChange" );
		}
	}
	
	@Override
	protected void onCategoryRecommendIconChange()
	{
		// TODO Auto-generated method stub
		Log.v( "COOL" , "onCategoryRecommendIconChange" );
	}
	
	public List<RecommendApkInfo> getRecommendApkInfoList(
			int fid )
	{
		try
		{
			lockMap.readLock().lock();
			List<RecommendApkInfo> recommendApkInfoList = new ArrayList<RecommendApkInfo>();
			RecommendInfo recommendInfo = RecommendInfoMap.get( fid );
			if( recommendInfo != null )
			{
				Map<String , RecommendApkInfo> infoMap = recommendInfo.getApkinfoMap();
				for( int i = 0 ; i < infoMap.size() ; i++ )
				{
					String itemKey = recommendInfo.getFolderID() + "_" + i;
					RecommendApkInfo itemVal = infoMap.get( itemKey );
					if( itemVal != null )
					{
						dl_info info = getCoolDLMgrIcon().IconGetInfo( itemVal.getPkgName() );
						if( info != null && info.IsDownloadSuccess() )
						{
							itemVal.apkIconpath = info.getFilePath();
							recommendApkInfoList.add( itemVal );
						}
					}
				}
			}
			return recommendApkInfoList;
		}
		finally
		{
			lockMap.readLock().unlock();
		}
	}
}
