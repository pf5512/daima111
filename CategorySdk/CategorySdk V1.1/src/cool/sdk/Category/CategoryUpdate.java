package cool.sdk.Category;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import cool.sdk.common.CoolHttpClient;
import cool.sdk.common.CoolHttpClient.ResultEntity;
import cool.sdk.common.JsonUtil;
import cool.sdk.common.UrlUtil;
import cool.sdk.download.CoolDLCallback;
import cool.sdk.download.CoolDLMgr;
import cool.sdk.download.CoolDLResType;
import cool.sdk.download.manager.DlMethod;
import cool.sdk.download.manager.dl_info;
import cool.sdk.log.CoolLog;
import cool.sdk.log.LogHelper;
import cool.sdk.update.UpdateManagerImpl;
import cool.sdk.update.manager.UpdateConfig;
import cool.sdk.update.manager.UpdateHelper;


public abstract class CategoryUpdate extends UpdateHelper
{
	
	public static Map<Integer , DictData> dictMap = new HashMap<Integer , DictData>();
	public static Map<Integer , Integer> treeMap = new HashMap<Integer , Integer>();
	public static Map<String , Integer> cateinfoMap = new HashMap<String , Integer>();
	public static Map<Integer , RecommendInfo> RecommendInfoMap = new HashMap<Integer , RecommendInfo>();
	private static boolean isInsideCategoryData = false;
	static UpdateConfig config;
	static
	{
		config = new UpdateConfig();
		config.UPDATE_DEFAULT_MINUTES = 1 * 24 * 60;// 默认更新间隔
		config.UPDATE_MIN_MINUTES = 8 * 60;// 最小更新间隔
		config.UPDATE_MAX_MINUTES = 15 * 24 * 60;// 最大更新间隔
		config.MAX_UPDATE_TIMES_PER_DAY = 3;// 每天最大更新次数
		config.RETRY_TIMES_WHEN_ONLINE = 3;// 有网络下的重试次数
	};
	protected Context context;
	protected CoolLog Log;
	static final float CONST_SUCCESS_RATE = 0.0f;
	CategoryDBTool dbTool = null;
	
	protected CategoryUpdate(
			Context context )
	{
		super( context , Category.h13 , config );
		// TODO Auto-generated constructor stub
		this.context = context;
		Log = new CoolLog( context );
		//Log.setEnableLog( bEnable )
		dbTool = new CategoryDBTool( context );
	}
	
	abstract class MyIconCoolDLCallback implements CoolDLCallback
	{
		
		public int successCount = 0;// 本次成功个数
		public int failCount = 0;// 本次失败个数
		public int downloadCount = 0;// 本次下载个数
		public int totalCount = 0;// 总个数
		public int fid;
	}
	
	CoolDLMgr dlMgrIcon;
	CoolDLMgr dlMgrApk;
	Object dlMgrIconSync = new Object();
	
	public CoolDLMgr getCoolDLMgrIcon()
	{
		synchronized( dlMgrIconSync )
		{
			if( dlMgrIcon == null )
			{
				dlMgrIcon = CoolDLMgr.getInstance( context , "DICON" + Category.h12 + "D" , Category.h12 , Category.h13 );
				dlMgrIcon.dl_mgr.setMaxConnectionCount( 3 );
				dlMgrIcon.dl_mgr.setDownloadPath( dlMgrIcon.getInternalPath() );
				dlMgrIcon.setCheckPathEverytime( false );
			}
		}
		return dlMgrIcon;
	}
	
	public CoolDLMgr getCoolDLMgrApk()
	{
		synchronized( dlMgrIconSync )
		{
			if( dlMgrApk == null )
			{
				dlMgrApk = Category.CoolDLMgr( context , "DAPP" );
				dlMgrApk.dl_mgr.setMaxConnectionCount( 3 );
			}
		}
		return dlMgrApk;
	}
	
	public void setMaxIconDownloadTaskCount(
			int maxConnectionCount )
	{
		getCoolDLMgrIcon().dl_mgr.setMaxConnectionCount( maxConnectionCount );
	}
	
	protected static final String ACTION_CONFIG_REQUEST = "3601";
	protected static final String ACTION_CATEGORY_REQUEST = "3602";
	protected static final String ACTION_CATEGORY_SELECT = "3603";
	protected static final String ACTION_CATEGORY_RECOMMEND = "3604";
	// private static final String ACTION_MSG_COLLECT = "3000";
	protected static final String ACTION_CATEGORY_STATISTICS_ACTIVE = "3601";
	protected static final String ACTION_CATEGORY_STATISTICS_DISMISS = "3602";
	protected static final String ACTION_CATEGORY_STATISTICS_RECOMMEND_ACTIVE = "3603";
	protected static final int PLAFORM_VERSION = 1;// 分组功能客户端版本号
	protected static final String DEFAULT_VERSION = "0";
	protected boolean isForeground;
	
	// protected JSONArray appList;
	public JSONArray GetAllInstallAppList()
	{
		try
		{
			JSONArray appList = new JSONArray();
			final Intent mainIntent = new Intent( Intent.ACTION_MAIN , null );
			mainIntent.addCategory( Intent.CATEGORY_LAUNCHER );
			PackageManager Pkgmanger = context.getPackageManager();
			List<ResolveInfo> allApps = Pkgmanger.queryIntentActivities( mainIntent , 0 );
			//List<PackageInfo> list = Pkgmanger.getInstalledPackages( 0 );
			for( ResolveInfo info : allApps )
			{
				if( info.activityInfo.applicationInfo.packageName == null || info.activityInfo.name == null )
				{
					continue;
				}
				JSONObject curPkgJson = new JSONObject();
				curPkgJson.put( "pn" , info.activityInfo.applicationInfo.packageName );
				if( ( info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) != 0 || ( info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP ) != 0 )
				{
					// 系统应用
					curPkgJson.put( "sy" , "0" );
				}
				else
				{
					// 非系统应用
					curPkgJson.put( "sy" , "1" );
				}
				curPkgJson.put( "cn" , info.loadLabel( Pkgmanger ) );
				curPkgJson.put( "en" , info.loadLabel( Pkgmanger ) );
				// curPkgJson.put( "cn" , info.applicationInfo.)
				curPkgJson.put( "vr" , Pkgmanger.getPackageInfo( info.activityInfo.applicationInfo.packageName , 0 ).versionName );
				curPkgJson.put( "vn" , Pkgmanger.getPackageInfo( info.activityInfo.applicationInfo.packageName , 0 ).versionCode );
				// info.packageName;
				appList.put( curPkgJson );
				curPkgJson = null;
			}
			return appList;
		}
		catch( Exception e )
		{
			// TODO: handle exception
			return null;
		}
	}
	
	@Override
	protected boolean OnUpdate(
			Context context ) throws Exception
	{
		// TODO Auto-generated method stub
		Log.v( "COOL" , "CategoryUpdate  OnUpdate" );
		// getCoolDLMgrIcon();
		int type = getConfigType();
		if( type == 0 )
		{
			return true;
		}
		JSONObject reqJson = JsonUtil.NewRequestJSON( context , Category.h12 , Category.h13 );
		reqJson.put( "Action" , ACTION_CONFIG_REQUEST );
		reqJson.put( "p1" , getString( "c0" , DEFAULT_VERSION ) );
		Log.v( "COOL" , "CategoryUpdate req:" + reqJson.toString() );
		ResultEntity result = CoolHttpClient.postEntity( UrlUtil.getDataServerUrl() , reqJson.toString() );
		if( result.exception != null )
		{
			Log.v( "COOL" , "CategoryUpdate rsp:(error)" + result.httpCode + " " + result.exception );
			return false;
		}
		Log.v( "COOL" , "CategoryUpdate rsp:" + result.httpCode + " " + result.content );
		JSONObject resJson = new JSONObject( result.content );
		int rc0 = resJson.getInt( "rc0" );
		if( rc0 == 0 )
		{
			if( resJson.has( "c0" ) )// 配置文件版本号
			{
				setValue( "c0" , resJson.getString( "c0" ) );
			}
			if( resJson.has( "c1" ) )// 更新间隔，同文件夹的参数
			{
				setGapMinute( resJson.getInt( "c1" ) );
			}
			if( resJson.has( "c2" ) )// 显性做文件夹分类的功能是否开启
			{
				setValue( "c2" , resJson.getInt( "c2" ) );
			}
			if( resJson.has( "c3" ) )// 显性做文件夹分类的功能是否开启
			{
				setValue( "c3" , resJson.getInt( "c3" ) );
			}
		}
		else if( rc0 == 100 )
		{
		}
		if( type == 1 || type == 2 && getInt( "c2" , 0 ) == 1 )
		{
			doCategoryStatisticsActive();
		}
		if( ( !isCategoryState() ) && ( canDoCategory() ) )
		//if( !( "TRUE".equals( getString( "IsDidForeCategoryReques" ) ) ) && 1 == getInt( "c2" , 0 ) )
		{
			doCategoryRequest( false , GetAllInstallAppList() );
		}
		// if( "TRUE".equals( getString( "IsDidForeCategoryReques" ) ))
		// {
		// doCategoryRecommend();
		// }
		if( !doCategoryRecommend( null ) )
		{
			return false;
		}
		return true;
	}
	
	protected static final String KEY_CategoryRequest_START_TIME = "Category_start_time";
	
	// 请求分类
	// isInitiative：是否主动 true：主动 false:被动
	protected int doCategoryRequest(
			boolean isForeground ,
			JSONArray appList ) throws Exception
	{
		Log.v( "COOL" , "doCategoryRequest 11111111111111  " + System.currentTimeMillis() );
		int res = -1;
		//没有网络直接返回
		if( !DlMethod.IsNetworkAvailable( context ) )
		{
			return res;
		}
		JSONObject reqJson = JsonUtil.NewRequestJSON( context , Category.h12 , Category.h13 );
		reqJson.put( "Action" , ACTION_CATEGORY_REQUEST );
		reqJson.put( "p1" , 0 );
		reqJson.put( "p2" , isForeground ? 0 : 1 );// 发起方式标致 1： 程序后台自己启动 0： 用户主动发起
		reqJson.put( "p3" , PLAFORM_VERSION );// 客户端支持协议版本号
		reqJson.put( "p4" , 1 );// 定制配置信息
		reqJson.put( "p5" , getString( "c0" , DEFAULT_VERSION ) );//
		reqJson.put( "p6" , Locale.getDefault().getLanguage() );//
		reqJson.put( "applist" , appList );
		Log.v( "COOL" , "CategoryUpdate doCategoryRequest req:" + reqJson.toString() );
		ResultEntity result = CoolHttpClient.postEntity( UrlUtil.getDataServerUrl() , reqJson.toString() );
		if( result.exception != null )
		{
			Log.v( "COOL" , "CategoryUpdate doCategoryRequest rsp:(error)" + result.httpCode + " " + result.exception );
			return res;
		}
		Log.v( "COOL" , "CategoryUpdate doCategoryRequest rsp:" + result.httpCode + " " + result.content );
		JSONObject resJson = new JSONObject( result.content );
		if( !resJson.has( "rl0" ) || 100 == resJson.getInt( "rl0" ) )
		{
			if( resJson.has( "msg" ) )
			{
				String msg = resJson.getString( "msg" );
				Log.v( "COOL" , "doCategoryRequest ERROR:" + msg );
			}
			return res;
		}
		try
		{
			lockMap.writeLock().lock();
			if( resJson.has( "rc0" ) && 0 == resJson.getInt( "rc0" ) && resJson.has( "cateframe" ) )
			{
				JSONObject cateframeJson = resJson.getJSONObject( "cateframe" );
				if( cateframeJson.has( "ver" ) )
				{
					String strVer = cateframeJson.getString( "ver" );
				}
				// 解析dict 数据段
				if( cateframeJson.has( "dict" ) )
				{
					dictMap.clear();
					JSONArray dictJSONArray = cateframeJson.getJSONArray( "dict" );
					for( int i = 0 ; i < dictJSONArray.length() ; i++ )
					{
						JSONObject curJson = dictJSONArray.getJSONObject( i );
						// Log.v( "COOL" , i + ":  " + curJson.toString() );
						if( curJson.has( "id" ) && curJson.has( "cn" ) && curJson.has( "en" ) && curJson.has( "od" ) )
						{
							int id = curJson.getInt( "id" );
							String cn = curJson.getString( "cn" );
							String en = curJson.getString( "en" );
							int od = curJson.getInt( "od" );
							// Log.v( "COOL" , "id =" + id + ", cn=" + cn + ", en=" + en + ", od=" + od );
							// 写入数据
							dictMap.put( id , new DictData( id , cn , en , od ) );
						}
					}
				}
				// 解析tree数据段
				if( cateframeJson.has( "tree" ) )
				{
					treeMap.clear();
					JSONObject treeJSON = cateframeJson.getJSONObject( "tree" );
					Iterator<?> it = treeJSON.keys();
					while( it.hasNext() )
					{
						String key = (String)it.next();
						JSONObject curJson = treeJSON.getJSONObject( key );
						// Log.v( "COOL" , key + ":  " + curJson.toString() );
						int id = curJson.getInt( "id" );
						JSONArray childIDJSONArray = curJson.getJSONArray( "idc" );
						for( int i = 0 ; i < childIDJSONArray.length() ; i++ )
						{
							int childID = childIDJSONArray.getInt( i );
							// Log.v( "COOL" , "childID:" + childID + "ID" + id );
							// 写入数据
							treeMap.put( childID , id );
						}
					}
				}
			}
			// cateinfo
			if( resJson.has( "cateinfo" ) )
			{
				JSONArray cateinfoFloderIDJSONArray = new JSONArray();
				cateinfoMap.clear();
				JSONArray cateinfoJSONArray = resJson.getJSONArray( "cateinfo" );
				for( int i = 0 ; i < cateinfoJSONArray.length() ; i++ )
				{
					JSONObject cateinfoChildJson = cateinfoJSONArray.getJSONObject( i );
					if( cateinfoChildJson.has( "fid" ) && cateinfoChildJson.has( "list" ) )
					{
						try
						{
							int fid = cateinfoChildJson.getInt( "fid" );
							cateinfoFloderIDJSONArray.put( fid );
							JSONArray listJSONArray = cateinfoChildJson.getJSONArray( "list" );
							for( int j = 0 ; j < listJSONArray.length() ; j++ )
							{
								JSONObject listChildJSONObject = listJSONArray.getJSONObject( j );
								if( listChildJSONObject.has( "pn" ) )
								{
									String strPkgName = listChildJSONObject.getString( "pn" );
									// Log.v( "COOL" , "fid=" + fid +
									// ", strPkgName=" + strPkgName );
									// 写入数据
									cateinfoMap.put( strPkgName , fid );
								}
							}
						}
						catch( Exception e )
						{
							// TODO: handle exception
						}
					}
				}
				Log.v( "COOL" , "doCategoryRequest FloderIDList " + cateinfoFloderIDJSONArray.toString() );
				setValue( "cateinfoFloderIDJSONArray" , cateinfoFloderIDJSONArray.toString() );
			}
			Log.v( "COOL" , "doCategoryRequest 22222222222222 " + System.currentTimeMillis() );
		}
		finally
		{
			lockMap.writeLock().unlock();
		}
		try
		{
			lockMap.readLock().lock();
			if( !dictMap.isEmpty() && !treeMap.isEmpty() && !cateinfoMap.isEmpty() )
			{
				res = 0;
				isInsideCategoryData = false;
				setValue( "IsDidForeCategoryReques" , "TRUE" );
				SaveMapToDB();
			}
			Log.v( "COOL" , "doCategoryRequest 33333333333  " + System.currentTimeMillis() );
		}
		finally
		{
			lockMap.readLock().unlock();
		}
		// Log.v( "COOL" , "doCategoryRequest  EEEEEEEEEEEEEE" );
		// Log.v( "COOL" , "dictMap: " + dictMap );
		// Log.v( "COOL" , "treeMap: " + treeMap );
		// Log.v( "COOL" , "cateinfoMap: " + cateinfoMap );
		if( !isForeground && res == 0 )
		{
			try
			{
				onCategoryBGRequestComplete();
			}
			catch( Exception e )
			{
				// TODO: handle exception
			}
		}
		return res;
	}
	
	/*	doCategoryRequestByInsdedata
	*   通过内置数据源获取分类数据（3个MAP， dictMap， treeMap 和  cateinfoMap），但是MAP不存数据库
	*	JSONArray appList : 需要分类的APK列表，用GetAllInstallAppList()获取
	*	CaregoryReqCallBack callback  : callback 函数，重载后可以获取分类成功与否的相关信息
	*/
	public void doCategoryRequestByInsdedata(
			JSONArray appList ,
			CaregoryReqCallBack callback )
	{
		doCategoryRequestByInsdedata( appList );
		if( !dictMap.isEmpty() && !treeMap.isEmpty() && !cateinfoMap.isEmpty() && callback != null )
		{
			callback.ReqSucess( CaregoyReqType.CATEGORY_REQUEST_FORCE_TYPE , null );
		}
		else if( callback != null )
		{
			callback.ReqFailed( CaregoyReqType.CATEGORY_REQUEST_FORCE_TYPE , "Not open CategoryRequest function" );
		}
	}
	
	private void SaveMapToDB()
	{
		// TODO Auto-generated method stub
		//SQLiteDatabase db = dbTool.GetSQLiteDatabase();
		//db.beginTransaction();
		List<ContentValues> DictDataList = new ArrayList<ContentValues>();
		List<ContentValues> TreeDataList = new ArrayList<ContentValues>();
		List<ContentValues> CateinfDataList = new ArrayList<ContentValues>();
		if( !dictMap.isEmpty() )
		{
			dbTool.CleanDictTables();
			for( DictData dictData : dictMap.values() )
			{
				ContentValues values = new ContentValues();
				values.put( "id" , dictData.getId() );
				values.put( "cn" , dictData.getCn() );
				values.put( "en" , dictData.getEn() );
				values.put( "od" , dictData.getOd() );
				//dbTool.InsertDict( values );
				DictDataList.add( values );
			}
			dbTool.InsertDict( DictDataList );
		}
		else
		{
			//dbTool.getAllDict();
		}
		if( !treeMap.isEmpty() )
		{
			dbTool.CleanTreeTables();
			for( Map.Entry<Integer , Integer> entry : treeMap.entrySet() )
			{
				// System.out.println( "key= " + entry.getKey() + " and value= "
				// + entry.getValue() );
				ContentValues values = new ContentValues();
				values.put( "idc" , entry.getKey() );
				values.put( "id" , entry.getValue() );
				//dbTool.InsertTree( values );
				TreeDataList.add( values );
			}
			dbTool.InsertTree( TreeDataList );
		}
		else
		{
			//dbTool.getAllTree();
		}
		if( !cateinfoMap.isEmpty() )
		{
			dbTool.CleanCateInfoTables();
			for( Map.Entry<String , Integer> entry : cateinfoMap.entrySet() )
			{
				ContentValues values = new ContentValues();
				values.put( "pn" , entry.getKey() );
				values.put( "fid" , entry.getValue() );
				//dbTool.InsertCaeInfo( values );
				CateinfDataList.add( values );
			}
			dbTool.InsertCaeInfo( CateinfDataList );
		}
		else
		{
			//dbTool.getAllCateInfo();
		}
	}
	
	// 智能分类查询
	public void doCategoryQuery(
			JSONArray Applist ,
			CaregoryReqCallBack callback )
	{
		// if( !canDoCategory() )
		//没有网络直接返回
		if( !DlMethod.IsNetworkAvailable( context ) )
		{
			if( null != callback )
			{
				callback.ReqFailed( CaregoyReqType.CATEGORY_SELECT_TYPE , " can not connect network!!!" );
			}
			return;
		}
		if( !"TRUE".equals( getString( "IsDidForeCategoryReques" ) ) )
		{
			if( null != callback )
			{
				callback.ReqFailed( CaregoyReqType.CATEGORY_SELECT_TYPE , "you must did Fore Category Reques frist!!!" );
			}
			return;
		}
		try
		{
			List<Integer> dictList = null;
			List<Integer> treeList = null;
			List<String> cateinfoList = null;
			JSONObject reqJson = JsonUtil.NewRequestJSON( context , Category.h12 , Category.h13 );
			reqJson.put( "Action" , ACTION_CATEGORY_SELECT );
			// reqJson.put( "p1" , 0 );
			// reqJson.put( "p2" , isInitiative ? 1 : 0 );//发起方式标致 0： 程序后台自己启动
			// 1： 用户主动发起
			reqJson.put( "p3" , PLAFORM_VERSION );// 客户端支持协议版本号
			// reqJson.put( "p4" , 1 );//定制配置信息
			reqJson.put( "p5" , getString( "c0" , DEFAULT_VERSION ) );//
			reqJson.put( "p6" , Locale.getDefault().getLanguage() );//
			reqJson.put( "applist" , Applist );
			Log.v( "COOL" , "CategoryUpdate doCategoryQuery req:" + reqJson.toString() );
			ResultEntity result = CoolHttpClient.postEntity( UrlUtil.getDataServerUrl() , reqJson.toString() );
			if( result.exception != null )
			{
				Log.v( "COOL" , "CategoryUpdate doCategoryQuery rsp:(error)" + result.httpCode + " " + result.exception );
				if( null != callback )
				{
					callback.ReqFailed( CaregoyReqType.CATEGORY_SELECT_TYPE , result.exception.toString() );
				}
				return;
			}
			Log.v( "COOL" , "CategoryUpdate doCategoryQuery rsp:" + result.httpCode + " " + result.content );
			// return new JSONObject( result.content );
			JSONObject resJson = new JSONObject( result.content );
			if( !resJson.has( "rl0" ) || 100 == resJson.getInt( "rl0" ) )
			{
				String msg = null;
				if( resJson.has( "msg" ) )
				{
					msg = resJson.getString( "msg" );
					Log.v( "COOL" , "doCategoryRequest ERROR:" + msg );
				}
				if( null != callback )
				{
					callback.ReqFailed( CaregoyReqType.CATEGORY_SELECT_TYPE , msg );
				}
				return;
			}
			if( resJson.has( "rc0" ) && 0 == resJson.getInt( "rc0" ) && resJson.has( "cateframe" ) )
			{
				JSONObject cateframeJson = resJson.getJSONObject( "cateframe" );
				if( cateframeJson.has( "ver" ) )
				{
					String strVer = cateframeJson.getString( "ver" );
				}
				// 解析dict 数据段
				if( cateframeJson.has( "dict" ) )
				{
					dictList = new ArrayList<Integer>();
					JSONArray dictJSONArray = cateframeJson.getJSONArray( "dict" );
					for( int i = 0 ; i < dictJSONArray.length() ; i++ )
					{
						JSONObject curJson = dictJSONArray.getJSONObject( i );
						Log.v( "COOL" , i + ":  " + curJson.toString() );
						if( curJson.has( "id" ) && curJson.has( "cn" ) && curJson.has( "en" ) && curJson.has( "od" ) )
						{
							int id = curJson.getInt( "id" );
							String cn = curJson.getString( "cn" );
							String en = curJson.getString( "en" );
							int od = curJson.getInt( "od" );
							Log.v( "COOL" , "id =" + id + ", cn=" + cn + ", en=" + en + ", od=" + od );
							// 写入数据
							try
							{
								lockMap.writeLock().lock();
								dictMap.put( id , new DictData( id , cn , en , od ) );
							}
							finally
							{
								lockMap.writeLock().unlock();
							}
							dictList.add( id );
						}
					}
				}
				// 解析tree数据段
				if( cateframeJson.has( "tree" ) )
				{
					treeList = new ArrayList<Integer>();
					JSONObject treeJSON = cateframeJson.getJSONObject( "tree" );
					Iterator<?> it = treeJSON.keys();
					while( it.hasNext() )
					{
						String key = (String)it.next();
						JSONObject curJson = treeJSON.getJSONObject( key );
						Log.v( "COOL" , key + ":  " + curJson.toString() );
						int id = curJson.getInt( "id" );
						JSONArray childIDJSONArray = curJson.getJSONArray( "idc" );
						for( int i = 0 ; i < childIDJSONArray.length() ; i++ )
						{
							int childID = childIDJSONArray.getInt( i );
							Log.v( "COOL" , "childID:" + childID + "ID" + id );
							// 写入数据
							try
							{
								lockMap.writeLock().lock();
								treeMap.put( childID , id );
							}
							finally
							{
								lockMap.writeLock().unlock();
							}
							treeList.add( childID );
						}
					}
				}
			}
			// cateinfo
			if( resJson.has( "cateinfo" ) )
			{
				cateinfoList = new ArrayList<String>();
				JSONArray cateinfoJSONArray = resJson.getJSONArray( "cateinfo" );
				for( int i = 0 ; i < cateinfoJSONArray.length() ; i++ )
				{
					JSONObject cateinfoChildJson = cateinfoJSONArray.getJSONObject( i );
					if( cateinfoChildJson.has( "fid" ) && cateinfoChildJson.has( "list" ) )
					{
						int fid = cateinfoChildJson.getInt( "fid" );
						JSONArray listJSONArray = cateinfoChildJson.getJSONArray( "list" );
						for( int j = 0 ; j < listJSONArray.length() ; j++ )
						{
							JSONObject listChildJSONObject = listJSONArray.getJSONObject( j );
							if( listChildJSONObject.has( "pn" ) )
							{
								String strPkgName = listChildJSONObject.getString( "pn" );
								Log.v( "COOL" , "fid=" + fid + ", strPkgName=" + strPkgName );
								// 写入数据
								try
								{
									lockMap.writeLock().lock();
									cateinfoMap.put( strPkgName , fid );
								}
								finally
								{
									lockMap.writeLock().unlock();
								}
								cateinfoList.add( strPkgName );
							}
						}
					}
				}
			}
			if( null != cateinfoList && !cateinfoList.isEmpty() )
			{
				if( null != callback )
				{
					callback.ReqSucess( CaregoyReqType.CATEGORY_SELECT_TYPE , cateinfoList );
				}
				try
				{
					lockMap.readLock().lock();
					for( String pkgName : cateinfoList )
					{
						ContentValues values = new ContentValues();
						int fid = cateinfoMap.get( pkgName );
						values.put( "pn" , pkgName );
						values.put( "fid" , fid );
						dbTool.updateCateinfo( values , pkgName );
					}
				}
				finally
				{
					lockMap.readLock().unlock();
				}
			}
			else
			{
				if( null != callback )
				{
					callback.ReqFailed( CaregoyReqType.CATEGORY_SELECT_TYPE , "Can not get categoy info!!!" );
				}
			}
			try
			{
				lockMap.readLock().lock();
				if( null != dictList && !dictList.isEmpty() )
				{
					for( int id : dictList )
					{
						DictData dictData = dictMap.get( id );
						ContentValues values = new ContentValues();
						values.put( "id" , dictData.getId() );
						values.put( "cn" , dictData.getCn() );
						values.put( "en" , dictData.getEn() );
						values.put( "od" , dictData.getOd() );
						dbTool.updatedDict( values , dictData.getId() );
					}
				}
				if( null != treeList && !treeList.isEmpty() )
				{
					for( int idc : treeList )
					{
						int id = treeMap.get( idc );
						ContentValues values = new ContentValues();
						values.put( "idc" , idc );
						values.put( "id" , id );
						dbTool.updateTree( values , idc );
					}
				}
			}
			finally
			{
				lockMap.readLock().unlock();
			}
		}
		catch( Exception e )
		{
			// TODO: handle exception
		}
	}
	
	/* doCategoryRequestForegroundWithoutInsidedata
	
	*   前台分类请求, 用户在launcher上主动触发， 只访问网络数据，如果不成功，不会访问内置数据
	
	* JSONArray appList : 需要分类的APK列表，建议用GetAllInstallAppList()获取
	
	* CaregoryReqCallBack callback  : callback 函数，重载后可以获取分类成功与否的相关信息
	
	*/
	public void doCategoryRequestForegroundWithoutInsidedata(
			JSONArray appList ,
			CaregoryReqCallBack callback )
	{
		// if( isUpdating )
		// {
		int ret;
		Log.v( "COOL" , "doCategoryRequestForeground" );
		// this.isForeground = true;
		if( !UpdateManagerImpl.allowUpdate( context ) )
		{
			if( callback != null )
			{
				callback.ReqFailed( CaregoyReqType.CATEGORY_REQUEST_FORCE_TYPE , "Not open CategoryRequest function" );
			}
		}
		try
		{
			//MELOG.v( "COOL" , "doCategoryRequest  SSSSSSSSSSSSS" );
			ret = doCategoryRequest( true , appList );
			//MELOG.v( "COOL" , "doCategoryRequest  EEEEEEEEEEEEE" );
		}
		catch( Exception e )
		{
			ret = -1;
		}
		if( 0 == ret )
		{
			if( getLong( KEY_CategoryRequest_START_TIME ) == null )
			{
				setValue( KEY_CategoryRequest_START_TIME , System.currentTimeMillis() / ( 60 * 1000 ) );
			}
			if( callback != null )
			{
				callback.ReqSucess( CaregoyReqType.CATEGORY_REQUEST_FORCE_TYPE , null );
			}
		}
		else if( callback != null )
		{
			callback.ReqFailed( CaregoyReqType.CATEGORY_REQUEST_FORCE_TYPE , "CategoryRequest failed" );
		}
		//			else
		//			{
		//				//如果分类请求不成功，使用内部数据库来获取分类数据
		//				if( dictMap.isEmpty() || treeMap.isEmpty() || cateinfoMap.isEmpty() )
		//				{
		//					doCategoryRequestByInsdedata( appList );
		//					if( !dictMap.isEmpty() && !treeMap.isEmpty() && !cateinfoMap.isEmpty() && callback != null )
		//					{
		//						callback.ReqSucess( CaregoyReqType.CATEGORY_REQUEST_FORCE_TYPE , null );
		//					}
		//					else if( callback != null )
		//					{
		//						callback.ReqFailed( CaregoyReqType.CATEGORY_REQUEST_FORCE_TYPE , "Not open CategoryRequest function" );
		//					}
		//				}
		//				else if( callback != null )
		//				{
		//					callback.ReqFailed( CaregoyReqType.CATEGORY_REQUEST_FORCE_TYPE , "Not open CategoryRequest function" );
		//				}
		//			}
		//网络请求分类成功（不包括内置数据分类成功），并且有WIFI网络的情况下，进行分类推荐请求
		if( /* DlMethod.IsWifiConnected( context )&&   */0 == ret )
		{
			try
			{
				doCategoryRecommend( null );
			}
			catch( Exception e )
			{
				// TODO: handle exception
			}
		}
		//	Update( true );
		// this.Update( true );
	}
	
	// 前台分类请求, 用户在launcher上主动触发，只要用户不禁止使用分类功能就立即执行
	public void doCategoryRequestForeground(
			JSONArray appList ,
			CaregoryReqCallBack callback )
	{
		// if( isUpdating )
		// {
		int ret;
		Log.v( "COOL" , "doCategoryRequestForeground" );
		// this.isForeground = true;
		if( !UpdateManagerImpl.allowUpdate( context ) )
		{
			if( callback != null )
			{
				callback.ReqFailed( CaregoyReqType.CATEGORY_REQUEST_FORCE_TYPE , "Not open CategoryRequest function" );
			}
		}
		try
		{
			//MELOG.v( "COOL" , "doCategoryRequest  SSSSSSSSSSSSS" );
			ret = doCategoryRequest( true , appList );
			//MELOG.v( "COOL" , "doCategoryRequest  EEEEEEEEEEEEE" );
		}
		catch( Exception e )
		{
			ret = -1;
		}
		if( 0 == ret )
		{
			if( getLong( KEY_CategoryRequest_START_TIME ) == null )
			{
				setValue( KEY_CategoryRequest_START_TIME , System.currentTimeMillis() / ( 60 * 1000 ) );
			}
			if( callback != null )
			{
				callback.ReqSucess( CaregoyReqType.CATEGORY_REQUEST_FORCE_TYPE , null );
			}
		}
		else
		{
			//如果分类请求不成功，使用内部数据库来获取分类数据
			if( dictMap.isEmpty() || treeMap.isEmpty() || cateinfoMap.isEmpty() )
			{
				doCategoryRequestByInsdedata( appList );
				if( !dictMap.isEmpty() && !treeMap.isEmpty() && !cateinfoMap.isEmpty() && callback != null )
				{
					callback.ReqSucess( CaregoyReqType.CATEGORY_REQUEST_FORCE_TYPE , null );
				}
				else if( callback != null )
				{
					callback.ReqFailed( CaregoyReqType.CATEGORY_REQUEST_FORCE_TYPE , "Not open CategoryRequest function" );
				}
			}
			else if( callback != null )
			{
				callback.ReqFailed( CaregoyReqType.CATEGORY_REQUEST_FORCE_TYPE , "Not open CategoryRequest function" );
			}
		}
		//网络请求分类成功（不包括内置数据分类成功），并且有WIFI网络的情况下，进行分类推荐请求
		if( /* DlMethod.IsWifiConnected( context )&&   */0 == ret )
		{
			try
			{
				doCategoryRecommend( null );
			}
			catch( Exception e )
			{
				// TODO: handle exception
			}
		}
		//	Update( true );
		// this.Update( true );
	}
	
	//String filePath = android.os.Environment.getExternalStorageDirectory() + "/weather";
	//	public void WriteCategoryInsdeDBToSD(
	//			String filePath )
	//	{
	//		InputStream inputStream = null;
	//		try
	//		{
	//			inputStream = context.getResources().getAssets().open( "category_insde.sqlite3" );
	//			java.io.File file = new java.io.File( filePath );
	//			if( !file.exists() )
	//			{
	//				file.mkdirs();
	//			}
	//			FileOutputStream fileOutputStream = new FileOutputStream( filePath + "category_insde.sqlite3" );
	//			byte[] buffer = new byte[512];
	//			int count = 0;
	//			while( ( count = inputStream.read( buffer ) ) > 0 )
	//			{
	//				fileOutputStream.write( buffer , 0 , count );
	//			}
	//			fileOutputStream.flush();
	//			fileOutputStream.close();
	//			inputStream.close();
	//			//System.out.println( "success" );
	//		}
	//		catch( IOException e )
	//		{
	//			e.printStackTrace();
	//		}
	//	}
	//	private boolean isExist()
	//	{
	//		java.io.File file = new java.io.File( filePath + "/database.db" );
	//		if( file.exists() )
	//		{
	//			return true;
	//		}
	//		else
	//		{
	//			return false;
	//		}
	//	}
	private void doCategoryRequestByInsdedata(
			JSONArray appList )
	{
		CateBloom cb = null;
		//		SQLiteDatabase db = null;
		//		String strStoragePath = null;
		//		if( Environment.MEDIA_MOUNTED.equals( Environment.getExternalStorageState() ) )
		//		{
		//			strStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
		//		}
		//		else
		//		{
		//			strStoragePath = context.getFilesDir().getAbsolutePath();
		//		}
		//		// TODO Auto-generated method stub
		//		java.io.File db_file = new java.io.File( strStoragePath + "/cooee/category_insde.sqlite3" );
		//		if( !db_file.exists() )
		//		{
		//			WriteCategoryInsdeDBToSD( strStoragePath + "/cooee/" );
		//		}
		try
		{
			dictMap.clear();
			treeMap.clear();
			cateinfoMap.clear();
			int SystemAppFid = 0;
			int MoreAppFid = -1;
			JSONArray cateinfoFloderIDJSONArray = new JSONArray();
			//			db = SQLiteDatabase.openDatabase( strStoragePath + "/cooee/category_insde.sqlite3" , null , SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS );
			//			//处理系统自带文件夹数据
			//			//sp_flord 打开
			//			Cursor sp_flord = db.query( "cate_frame" , null , "id=" + "'" + SystemAppFid + "'" , null , null , null , null );
			//			//sp_flord 查询
			//			if( null != sp_flord && sp_flord.moveToFirst() )
			//			{
			//				int pid = sp_flord.getInt( sp_flord.getColumnIndexOrThrow( "pid" ) );
			//				String cn_name = sp_flord.getString( sp_flord.getColumnIndexOrThrow( "label_cn" ) );
			//				String en_name = sp_flord.getString( sp_flord.getColumnIndexOrThrow( "label_en" ) );
			//				int od = sp_flord.getInt( sp_flord.getColumnIndexOrThrow( "od" ) );
			//				treeMap.put( SystemAppFid , pid );
			//				dictMap.put( SystemAppFid , new DictData( SystemAppFid , cn_name , en_name , od ) );
			//			}
			//			//sp_flord 关闭
			//			if( sp_flord != null && !sp_flord.isClosed() )
			//			{
			//				sp_flord.close();
			//			}
			//			//处理更多应用文件夹数据
			//			//sp_flord 打开
			//			sp_flord = db.query( "cate_frame" , null , "id=" + "'" + MoreAppFid + "'" , null , null , null , null );
			//			//sp_flord 查询
			//			if( null != sp_flord && sp_flord.moveToFirst() )
			//			{
			//				int pid = sp_flord.getInt( sp_flord.getColumnIndexOrThrow( "pid" ) );
			//				String cn_name = sp_flord.getString( sp_flord.getColumnIndexOrThrow( "label_cn" ) );
			//				String en_name = sp_flord.getString( sp_flord.getColumnIndexOrThrow( "label_en" ) );
			//				int od = sp_flord.getInt( sp_flord.getColumnIndexOrThrow( "od" ) );
			//				treeMap.put( MoreAppFid , pid );
			//				dictMap.put( MoreAppFid , new DictData( MoreAppFid , cn_name , en_name , od ) );
			//			}
			//			//sp_flord 关闭
			//			if( sp_flord != null && !sp_flord.isClosed() )
			//			{
			//				sp_flord.close();
			//			}
			//			for( int i = 0 ; i < appList.length() ; i++ )
			//			{
			//				JSONObject curJson = appList.getJSONObject( i );
			//				String strPkgName = curJson.getString( "pn" );
			//				int appType = curJson.getInt( "sy" );
			//				Log.v( "CF_RTFSC" , "packageName:" + strPkgName + " appType:" + appType );
			//				//				if( 0 == appType )
			//				//				{
			//				//					cateinfoMap.put( strPkgName , SystemAppFid );
			//				//				}
			//				//				else
			//				//				{
			//				//c_app 打开
			//				Cursor c_app = db.query( "cate_offline" , null , "pname = " + "'" + strPkgName + "'" , null , null , null , null );
			//				//查询到响应的packageName
			//				//c_app 查询
			//				if( null != c_app && c_app.moveToFirst() )
			//				{
			//					int child_fid = c_app.getInt( c_app.getColumnIndexOrThrow( "caid" ) );
			//					Log.v( "CF_RTFSC" , "child_fid:" + child_fid );
			//					//c_app.close();
			//					//c_flord  打开
			//					Cursor c_flord = db.query( "cate_frame" , null , "id=" + "'" + child_fid + "'" , null , null , null , null );
			//					//c_flord 查询
			//					if( null != c_flord && c_flord.moveToFirst() )
			//					{
			//						int fid = c_flord.getInt( c_flord.getColumnIndexOrThrow( "pid" ) );
			//						cateinfoMap.put( strPkgName , fid );
			//						treeMap.put( child_fid , fid );
			//						//c_flord.close();
			//						//root_flord 打开
			//						Cursor root_flord = db.query( "cate_frame" , null , "id=" + "'" + fid + "'" , null , null , null , null );
			//						//root_flord 查询
			//						if( null != root_flord && root_flord.moveToFirst() )
			//						{
			//							String cn_name = root_flord.getString( root_flord.getColumnIndexOrThrow( "label_cn" ) );
			//							String en_name = root_flord.getString( root_flord.getColumnIndexOrThrow( "label_en" ) );
			//							int od = root_flord.getInt( root_flord.getColumnIndexOrThrow( "od" ) );
			//							dictMap.put( fid , new DictData( fid , cn_name , en_name , od ) );
			//							//root_flord.close();
			//							Log.v( "CF_RTFSC" , "packageName:" + strPkgName + " child_fid:" + child_fid + " fid" + fid + " cn_name:" + cn_name + " en_name:" + en_name );
			//						}
			//						//root_flord 关闭
			//						if( null != root_flord && !root_flord.isClosed() )
			//						{
			//							root_flord.close();
			//						}
			//					}
			//					//c_flord 关闭
			//					if( null != c_flord && !c_flord.isClosed() )
			//					{
			//						c_flord.close();
			//					}
			//				}
			//				else
			//				{
			//					if( 0 == appType )
			//					{
			//						cateinfoMap.put( strPkgName , SystemAppFid );
			//					}
			//					else
			//					{
			//						cateinfoMap.put( strPkgName , MoreAppFid );
			//					}
			//				}
			//				//c_app 关闭
			//				if( null != c_app && !c_app.isClosed() )
			//				{
			//					c_app.close();
			//				}
			//			}
			cb = CateBloom.getInstince( context );
			CateFrameItem[] frameList = cb.getCateFrameItemList();
			for( CateFrameItem item : frameList )
			{
				treeMap.put( item.id , item.pid );
				dictMap.put( item.id , new DictData( item.id , item.cn , item.en , item.od ) );
			}
			for( int i = 0 ; i < appList.length() ; i++ )
			{
				JSONObject curJson = appList.getJSONObject( i );
				String strPkgName = curJson.getString( "pn" );
				int appType = curJson.getInt( "sy" );
				Log.v( "CF_RTFSC" , "packageName:" + strPkgName + " appType:" + appType );
				int fid = cb.pnameToCaid( strPkgName );
				if( fid <= 0 )
				{
					if( 0 == appType )
					{
						fid = SystemAppFid;
					}
					else
					{
						fid = MoreAppFid;
					}
				}
				cateinfoMap.put( strPkgName , fid );
			}
			for( Integer id : dictMap.keySet() )
			{
				cateinfoFloderIDJSONArray.put( id );
			}
			Log.v( "CF_RTFSC" , "doCategoryRequest FloderIDList " + cateinfoFloderIDJSONArray.toString() );
			setValue( "cateinfoFloderIDJSONArray" , cateinfoFloderIDJSONArray.toString() );
			isInsideCategoryData = true;
		}
		catch( Exception e )
		{
			// TODO: handle exception
			Log.v( "CF_RTFSC" , "ERROR:" + e.toString() );
		}
		finally
		{
			//			if( null != db )
			//			{
			//				db.close();
			//			}
		}
	}
	
	// 后台分类请求，只有满足了条件，在请求时间间隔满足的情况下更新
	// public void doCategoryRequestBackground(
	// JSONArray appList )
	// {
	// if( isUpdating )
	// {
	// return;
	// }
	// this.isForeground = false;
	// this.appList = appList;
	// this.Update( false );
	// }
	public abstract JSONArray getIdList();
	
	// 请求分类
	// isInitiative：是否主动 true：主动 false:被动
	// public void doCategoryRecommend() throws Exception
	// {
	// JSONObject reqJson = JsonUtil.NewRequestJSON( context , Category.h12 ,
	// Category.h13 );
	// reqJson.put( "Action" , ACTION_CATEGORY_RECOMMEND );
	// reqJson.put( "p5" , getString( "c0" , DEFAULT_VERSION ) );//
	// reqJson.put( "p6" , PLAFORM_VERSION );//
	// reqJson.put( "p7" , getIdList() );//分类文件夹ID信息 格式：ID1,;ID2,
	// Log.v( "COOL" , "CategoryUpdate doCategoryRequest req:" +
	// reqJson.toString() );
	// ResultEntity result = CoolHttpClient.postEntity(
	// UrlUtil.getDataServerUrl() , reqJson.toString() );
	// if( result.exception != null )
	// {
	// Log.v( "COOL" , "CategoryUpdate doCategoryRequest rsp:(error)" +
	// result.httpCode + " " + result.exception );
	// return;
	// }
	// Log.v( "COOL" , "CategoryUpdate doCategoryRequest rsp:" + result.httpCode
	// + " " + result.content );
	// }
	public ReadWriteLock lockMap = new ReentrantReadWriteLock();
	
	public void checkCategoryRecommend()
	{
		try
		{
			if( CategoryHelper.getInstance( context ).canDoCategory() )
			{
				CategoryRecommendDownloadIcon( null , true );
			}
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void checkRecommendChange(
			int successCnt )
	{
		String curConfigVersion = getString( "Recommend_c2" , DEFAULT_VERSION );
		String thisConfigVersion = getString( "thisRecommend_c2" , DEFAULT_VERSION );
		if( !thisConfigVersion.equals( curConfigVersion ) )
		{
			try
			{
				onCategoryRecommendConfigChange( thisConfigVersion );
			}
			catch( Exception e )
			{
				// TODO: handle exception
			}
			setValue( "thisRecommend_c2" , curConfigVersion );
		}
		String curVersion = getString( "Recommend_c3" , DEFAULT_VERSION );
		String thisVersion = getString( "thisRecommend_c3" , DEFAULT_VERSION );
		if( !thisVersion.equals( curVersion ) )
		{
			try
			{
				onCategoryRecommendListChange( thisVersion );
			}
			catch( Exception e )
			{
				// TODO: handle exception
			}
			setValue( "thisRecommend_c3" , curVersion );
		}
		if( successCnt > 0 )
		{
			try
			{
				onCategoryRecommendIconChange();
			}
			catch( Exception e )
			{
				// TODO: handle exception
			}
		}
	}
	
	private void downloadIconMap(
			final Map<Integer , Set<String>> iconMap ,
			final int successCnt )
	{
		if( iconMap.isEmpty() )
		{
			checkRecommendChange( successCnt );
			return;
		}
		MyIconCoolDLCallback iconDownloadCB = new MyIconCoolDLCallback() {
			
			//static final float successRate = CONST_SUCCESS_RATE;
			private void mySuccessCheck()
			{
				if( successCount + failCount != downloadCount )
				{
					return;
				}
				iconMap.remove( fid );
				//Log.v( "COOL" , "Category mySuccessCheck:" + successCount + " " + failCount + " " + downloadCount + " " + totalCount + " " + ( 1.0f - (float)failCount / (float)totalCount ) );
				if( successCount > 0 )// (float)failCount / (float)totalCount > 1.0f - successRate )
				{
					try
					{
						Log.v( "COOL" , "Category onRecommendIconDownload:" + fid );
						onRecommendIconDownload( fid );
					}
					catch( Exception e )
					{
						// TODO: handle exception
					}
				}
				downloadIconMap( iconMap , successCnt + successCount );
			}
			
			@Override
			public synchronized void onSuccess(
					CoolDLResType type ,
					String name ,
					dl_info info )
			{
				// TODO Auto-generated method stub
				successCount++;
				Log.v( "COOL" , "Category iconDownloadCB success:" + name );
				mySuccessCheck();
			}
			
			@Override
			public synchronized void onFail(
					CoolDLResType type ,
					String name ,
					dl_info info )
			{
				// TODO Auto-generated method stub
				failCount++;
				Log.v( "COOL" , "Category iconDownloadCB fail:" + name );
				mySuccessCheck();
			}
			
			@Override
			public void onDoing(
					CoolDLResType type ,
					String name ,
					dl_info info )
			{
				// TODO Auto-generated method stub
			}
		};
		Iterator<Integer> iconMapIterator = iconMap.keySet().iterator();
		while( iconMapIterator.hasNext() )
		{
			iconDownloadCB.fid = iconMapIterator.next();
			Set<String> iconSet = iconMap.get( iconDownloadCB.fid );
			Iterator<String> onSetIterator = iconSet.iterator();
			iconDownloadCB.totalCount = iconSet.size();
			iconDownloadCB.successCount = 0;// 本次成功个数
			iconDownloadCB.failCount = 0;// 本次失败个数
			iconDownloadCB.downloadCount = 0;// 本次下载个数
			getCoolDLMgrIcon();
			while( onSetIterator.hasNext() )
			{
				String pkgName = onSetIterator.next();
				//下载url图标
				dl_info info = dlMgrIcon.IconGetInfo( pkgName );
				if( info == null || !info.IsDownloadSuccess() )
				{
					iconDownloadCB.downloadCount++;
				}
				else
				{
					onSetIterator.remove();
					//Log.v( "COOL" , "Category iconDownloadCB already exists:" + pkgName );
				}
			}
			//Log.v( "COOL" , "iconSet.size():" + iconSet.size());
			if( 0 == iconSet.size() )
			{
				if( !iconMapIterator.hasNext() )
				{
					checkRecommendChange( successCnt );
				}
				continue;
			}
			else
			{
				for( String pkg : iconSet )
				{
					dlMgrIcon.IconDownload( pkg , iconDownloadCB ); //dlMgrIcon.dl_mgr.stopAllTask();
				}
				break;
			}
		}
	}
	
	protected synchronized void CategoryRecommendDownloadIcon(
			final CaregoryReqCallBack callback ,
			boolean limited ) throws Exception
	{
		Log.v( "COOL" , "CategoryRecommendDownloadIcon   SSSSSSSSSS " );
		final Map<Integer , Set<String>> iconMap = new HashMap<Integer , Set<String>>();
		try
		{
			lockMap.readLock().lock();
			for( Entry<Integer , RecommendInfo> curRecommendInfoEntry : RecommendInfoMap.entrySet() )
			{
				RecommendInfo curRecommendInfo = curRecommendInfoEntry.getValue();
				Map<String , RecommendApkInfo> ApkInfoMap = curRecommendInfo.getApkinfoMap();
				Set<String> iconSet = new HashSet<String>();
				int getcount = 0;
				//循环获取当前文件夹中的推荐APP
				for( int i = 0 ; i < ApkInfoMap.size() ; i++ )
				{
					String pkgName = ApkInfoMap.get( curRecommendInfo.getFolderID() + "_" + i ).pkgName;
					//超过8个，就不要下载图标了
					//					if( getcount >= 8 )
					//					{
					//						break;
					//					}
					//已安装或者动态入口里的APP icon 不算在需要下载图标中
					//if( !DefaultLayout.checkApkExist( iLoongLauncher.getInstance() , pkgName ) && !OperateDynamicProxy.getInstance( iLoongLauncher.getInstance() ).containsApp( pkgName ) )
					{
						Log.v( "COOL" , "pkgName:" + pkgName + "  key: " + curRecommendInfo.getFolderID() + "_" + i );
						iconSet.add( pkgName );
						getcount++;
					}
				}
				if( !iconSet.isEmpty() )
				{
					iconMap.put( curRecommendInfoEntry.getKey() , iconSet );
				}
			}
		}
		finally
		{
			lockMap.readLock().unlock();
			Log.v( "LOCK" , "unlock8" );
		}
		if( iconMap.isEmpty() )
		{
			if( callback != null )
			{
				callback.ReqSucess( CaregoyReqType.CATEGORY_RECOMMEND_TYPE , null );
			}
			return;
		}
		downloadIconMap( iconMap , 0 );
	}
	
	public synchronized boolean doCategoryRecommend(
			final CaregoryReqCallBack callback ) throws Exception
	{
		//没有网络直接返回
		if( !DlMethod.IsNetworkAvailable( context ) )
		{
			if( callback != null )
			{
				callback.ReqFailed( CaregoyReqType.CATEGORY_RECOMMEND_TYPE , "no network !!!" );
			}
			return false;
		}
		if( !canDoCategory() )
		{
			return true;
		}
		JSONObject reqJson = JsonUtil.NewRequestJSON( context , Category.h12 , Category.h13 );
		reqJson.put( "Action" , ACTION_CATEGORY_RECOMMEND );
		reqJson.put( "p4" , getString( "Recommend_c2" , DEFAULT_VERSION ) );// 配置时间戳
		reqJson.put( "p5" , getString( "Recommend_c3" , DEFAULT_VERSION ) );// 列表时间戳
		reqJson.put( "p6" , PLAFORM_VERSION );// 动态入口平台版本号
		reqJson.put( "p7" , getIdList() );// 显示入口ID
		Log.v( "COOL" , "CategoryUpdate doCategoryRecommend req:" + reqJson.toString() );
		ResultEntity result = CoolHttpClient.postEntity( UrlUtil.getDataServerUrl() , reqJson.toString() );
		if( result.exception != null )
		{
			Log.v( "COOL" , "CategoryUpdate  doCategoryRecommend rsp:(error)" + result.httpCode + " " + result.exception );
			if( callback != null )
			{
				callback.ReqFailed( CaregoyReqType.CATEGORY_RECOMMEND_TYPE , "network error!" );
			}
			return false;
		}
		Log.v( "COOL" , "CategoryUpdate doCategoryRecommend rsp:" + result.httpCode + " " + result.content );
		JSONObject resJson = new JSONObject( result.content );
		if( resJson.has( "rc0" ) )
		{
			int rc0 = resJson.getInt( "rc0" );
			if( 0 == rc0 )
			{
				if( resJson.has( "config" ) )
				{
					JSONObject configJson = resJson.getJSONObject( "config" );
					if( configJson.has( "c2" ) )
					{
						// 保存分类请求回复的配置版本号
						setValue( "Recommend_c2" , configJson.getString( "c2" ) );
					}
					if( configJson.has( "c4" ) )
					{
						setValue( "Recommend_c4" , configJson.getInt( "c4" ) );
					}
				}
			}
		}
		if( resJson.has( "rl0" ) )
		{
			JSONObject listJSON = null;
			// 0:有记录 100：无记录
			int rl0 = resJson.getInt( "rl0" );
			//如果没记录，并且没有分类推荐数据，读取存储的数据
			if( 100 == rl0 && 0 == RecommendInfoMap.size() )
			{
				listJSON = new JSONObject( getString( "Recommend_list" ) );
			}
			else if( 0 == rl0 )
			{
				if( resJson.has( "config" ) )
				{
					JSONObject configJson = resJson.getJSONObject( "config" );
					if( configJson.has( "c3" ) )
					{
						// 保存分类请求回复的列表版本号
						setValue( "Recommend_c3" , configJson.getString( "c3" ) );
					}
				}
				doCategoryStatisticsRecommendActive();
				listJSON = resJson.getJSONObject( "list" );
				setValue( "Recommend_list" , listJSON.toString() );
			}
			if( listJSON != null )
			{
				decodeRecommendListJsonObject( listJSON );
			}
		}
		CategoryRecommendDownloadIcon( callback , true );
		return true;
	}
	
	protected void decodeRecommendListJsonObject()
	{
		String jStr = getString( "Recommend_list" );
		if( jStr != null )
		{
			try
			{
				JSONObject listJSON = new JSONObject( jStr );
				decodeRecommendListJsonObject( listJSON );
			}
			catch( Exception e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected void decodeRecommendListJsonObject(
			JSONObject listJSON )
	{
		try
		{
			lockMap.writeLock().lock();
			RecommendInfoMap.clear();
			Iterator<?> it = listJSON.keys();
			while( it.hasNext() )
			{
				String key = (String)it.next();
				JSONObject listChildJson = listJSON.getJSONObject( key );
				if( listChildJson.has( "r1" ) && listChildJson.has( "r2" ) && listChildJson.has( "r3" ) && listChildJson.has( "r4" ) && listChildJson.has( "r5" ) && listChildJson.has( "folder" ) )
				{
					int r1 = listChildJson.getInt( "r1" );// 分类ID
					String r2 = listChildJson.getString( "r2" );// 类型
					String r3 = listChildJson.getString( "r3" );// 英文
					String r4 = listChildJson.getString( "r4" );// 中文
					String r5 = listChildJson.getString( "r5" );// 繁体
					//MELOG.v( "COOL" , "=========foloder name:" +  r4);
					Map<String , RecommendApkInfo> ApkInfoMap = new HashMap<String , RecommendApkInfo>();
					RecommendInfo curRecommendInfo = new RecommendInfo( r1 , r2 , r3 , r4 , r5 , ApkInfoMap );
					RecommendInfoMap.put( r1 , curRecommendInfo );
					JSONArray folderJSONArray = listChildJson.getJSONArray( "folder" );
					{
						int index = 0;
						for( int i = 0 ; i < folderJSONArray.length() ; i++ )
						{
							JSONObject folderChildJson = folderJSONArray.getJSONObject( i );
							if( folderChildJson.has( "f0" ) && folderChildJson.has( "f1" ) && folderChildJson.has( "f2" ) && folderChildJson.has( "f3" ) && folderChildJson.has( "f4" ) && folderChildJson
									.has( "f5" ) && folderChildJson.has( "f6" ) && folderChildJson.has( "f7" ) && folderChildJson.has( "f8" ) && folderChildJson.has( "f10" ) )
							{
								String f0 = folderChildJson.getString( "f0" );// 类型
								// 2：应用类型
								String f1 = folderChildJson.getString( "f1" );// packageName
								String f2 = folderChildJson.getString( "f2" );// 下载的时候的
																				// 文字提示
								String f3 = folderChildJson.getString( "f3" );// 应用版本号
								String f4 = folderChildJson.getString( "f4" );// 版本名称
								String f5 = folderChildJson.getString( "f5" );// APK
																				// 文件大小
								String f6 = folderChildJson.getString( "f6" );// 中文名
								String f7 = folderChildJson.getString( "f7" );// 英文名
								String f8 = folderChildJson.getString( "f8" );// 繁体名
								//String f9 = folderChildJson.getString( "f9" );// icon
								String f10 = folderChildJson.getString( "f10" );// flag
								//MELOG.v( "COOL" , "icon name:" +  f6);
								//									Log.v(
								//											"COOL" ,
								//											" f0:" + f0 + "  f1:" + f1 + "  f2:" + f2 + "  f3:" + f3 + "  f4:" + f4 + "  f5:" + f5 + "  f6:" + f6 + "  f7:" + f7 + "  f8:" + f8 + "  f10:" + f10 );
								//									Log.v( "COOL" , "  r1" + r1 + "  r2" + r2 + "  r3" + r3 + "  r4" + r4 + "  r5:" + r5 );
								if( f1 != null && f1.length() > 0 )
								{
									RecommendApkInfo ApkInfo = new RecommendApkInfo( f1 , f0 , f2 , f3 , f4 , Integer.parseInt( f5 ) , f6 , f7 , f8 , null , Integer.parseInt( f10 ) );
									ApkInfoMap.put( r1 + "_" + index , ApkInfo );
									index++;
								}
							}
						}
						Log.v( "COOL" , "ApkInfoMap:" + ApkInfoMap );
					}
				}
			}
		}
		catch( Exception e )
		{
		}
		finally
		{
			lockMap.writeLock().unlock();
		}
		try
		{
			lockMap.readLock().lock();
			if( !RecommendInfoMap.isEmpty() )
			{
				SaveRecommendInfoToDB();
			}
		}
		finally
		{
			lockMap.readLock().unlock();
		}
	}
	
	private void SaveRecommendInfoToDB()
	{
		// TODO Auto-generated method stub
		List<ContentValues> ContentValuesList = new ArrayList<ContentValues>();
		dbTool.CleanRecommendInfoTables();
		for( RecommendInfo RInfo : CategoryHelper.RecommendInfoMap.values() )
		{
			Map<String , RecommendApkInfo> apkinfoMap = RInfo.getApkinfoMap();
			Set<String> keySet = apkinfoMap.keySet();
			for( String key : keySet )
			{
				RecommendApkInfo apkInfo = apkinfoMap.get( key );
				ContentValues values = new ContentValues();
				//values.put( "pnid_key" , apkInfo.getPkgName() + "_" + RInfo.getFolderID() );
				values.put( "fidiid_key" , key );
				values.put( "apk_pn" , apkInfo.getPkgName() );// varchar(255)
				values.put( "apk_type" , apkInfo.getApkType() );// char(4)
				values.put( "apk_dlinfo" , apkInfo.getApkDLInfo() );// text
				values.put( "apk_vc" , apkInfo.getApkVersionCode() );// text
				values.put( "apk_vn" , apkInfo.getApkVersionName() );// text
				values.put( "apk_size" , apkInfo.getApkSize() );// integer
				values.put( "apk_en" , apkInfo.getApkEN() );// text
				values.put( "apk_cn" , apkInfo.getApkCN() );// text
				values.put( "apk_fn" , apkInfo.getApkFN() );// text
				values.put( "apk_iconpath" , "" );// text
				values.put( "apk_flag" , apkInfo.getFlag() );// flag
				values.put( "f_id" , RInfo.getFolderID() );// integer
				values.put( "f_type" , RInfo.getFolderType() );// char(4)
				values.put( "f_en" , RInfo.getFolderEN() );// text
				values.put( "f_cn" , RInfo.getFolderCN() );// text
				values.put( "f_fn" , RInfo.getFolderFN() );// text
				//dbTool.InsertRecommendInfo( values );
				ContentValuesList.add( values );
			}
		}
		dbTool.InsertRecommendInfo( ContentValuesList );
	}
	
	protected abstract void onRecommendIconDownload(
			int fid );
	
	protected abstract void onCategoryBGRequestComplete();
	
	protected abstract void onCategoryRecommendConfigChange(
			String version );
	
	protected abstract void onCategoryRecommendListChange(
			String version );
	
	protected abstract void onCategoryRecommendIconChange();
	
	// 获取是否处于智能分类状态：true:处于智能分类  false:不处于智能分类
	public abstract boolean isCategoryState();
	
	// 获取DefaultLayout的配置 0: 禁止 1:显式 2：运营出来
	protected abstract int getConfigType();
	
	// 是否允许做智能分类
	public boolean canDoCategory()
	{
		// TODO Auto-generated method stub
		int type = getConfigType();
		if( type == 0 )
		{
			return false;
		}
		if( type == 2 )
		{
			if( getInt( "c2" , 0 ) == 0 )
			{
				// 0:不开启
				// 1:开启
				return false;
			}
		}
		return true;
	}
	
	//是否允许分类快捷
	public boolean allowCategoryFast()
	{
		long flag = getLong( "Recommend_c4" , 0L );
		return ( flag & ( 0x4 ) ) == 0x4;
	}
	
	// 是否显示更多应用
	public boolean canShowMoreEntry()
	{
		return getInt( "c3" , 0 ) == 1;
	}
	
	// 激活统计 3601
	protected void doCategoryStatisticsActive()
	{
		if( getInt( "doCategoryStatisticsActive" ) == null )
		{
			Log.v( "ZZY" , "doCategoryStatisticsActive" );
			try
			{
				JSONObject json = new JSONObject();
				json.put( "Action" , ACTION_CATEGORY_STATISTICS_ACTIVE );
				json.put( "h12" , Category.h12 );
				json.put( "h13" , Category.h13 );
				json.put( "p1" , PLAFORM_VERSION );
				LogHelper.Item( context , json , null );
			}
			catch( Exception e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setValue( "doCategoryStatisticsActive" , 1 );
		}
	}
	
	// abstract int SetClassification();
	// 解散统计 3062
	public void doCategoryStatisticsDismiss()
	{
		Long start = getLong( KEY_CategoryRequest_START_TIME );
		if( start != null )
		{
			start = System.currentTimeMillis() / ( 60 * 1000 ) - start;
			if( start > 0 )// 如果时间为正
			{
				Log.v( "ZZY" , "doCategoryStatisticsDismiss:" + start );
				try
				{
					JSONObject json = new JSONObject();
					json.put( "Action" , ACTION_CATEGORY_STATISTICS_DISMISS );
					json.put( "h12" , Category.h12 );
					json.put( "h13" , Category.h13 );
					json.put( "p1" , start );
					LogHelper.Item( context , json , null );
				}
				catch( Exception e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			setValue( KEY_CategoryRequest_START_TIME , (Long)null );
		}
	}
	
	// 推荐激活 3603
	protected void doCategoryStatisticsRecommendActive()
	{
		if( getInt( "doCategoryStatisticsRecommendActive" ) == null )
		{
			try
			{
				JSONObject json = new JSONObject();
				json.put( "Action" , ACTION_CATEGORY_STATISTICS_RECOMMEND_ACTIVE );
				json.put( "h12" , Category.h12 );
				json.put( "h13" , Category.h13 );
				json.put( "p1" , getString( "Recommend_c2" , DEFAULT_VERSION ) );// 激活推荐配置版本号
				LogHelper.Item( context , json , null );
			}
			catch( Exception e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setValue( "doCategoryStatisticsRecommendActive" , 1 );
		}
	}
	
	//是否从内置数据中获取的分类数据，初始值为false
	public boolean IsInsideCategoryData()
	{
		return isInsideCategoryData;
	}
}
