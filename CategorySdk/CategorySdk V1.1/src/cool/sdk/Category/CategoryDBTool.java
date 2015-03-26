package cool.sdk.Category;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class CategoryDBTool
{
	
	DatabaseHelper CategorydbHelper = null;
	private Object syncObj = new Object();
	static final String DICT_TABLE = "dict";
	static final String TREE_TABLE = "tree";
	static final String CATEINFO_TABLE = "cateinfo";
	static final String RECOMMEDINFO_TABLE = "recommend_info";
	
	public CategoryDBTool(
			Context context )
	{
		// TODO Auto-generated constructor stub
		CategorydbHelper = new DatabaseHelper( context , "category_db" );
	}
	
	public void CleanAllTables()
	{
		// TODO Auto-generated method stub
		CleanDictTables();
		CleanTreeTables();
		CleanCateInfoTables();
		CleanRecommendInfoTables();
	}
	
	void CleanDictTables()
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			db.execSQL( "delete from dict" );
		}
	}
	
	void CleanTreeTables()
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			db.execSQL( "delete from tree" );
		}
	}
	
	void CleanCateInfoTables()
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			db.execSQL( "delete from cateinfo" );
		}
	}
	
	void CleanRecommendInfoTables()
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			db.execSQL( "delete from recommend_info" );
		}
	}
	
	public void updateTree(
			ContentValues values ,
			int id )
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			Cursor c = db.query( TREE_TABLE , null , "id=" + id , null , null , null , null );
			if( c.moveToFirst() )
			{
				db.update( TREE_TABLE , values , "id=" + id , null );
			}
			else
			{
				db.insert( TREE_TABLE , null , values );
			}
			c.close();
			db.close();
		}
	}
	
	public void updateCateinfo(
			ContentValues values ,
			String pkgName )
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			Cursor c = db.query( CATEINFO_TABLE , null , "pn=" + pkgName , null , null , null , null );
			if( c.moveToFirst() )
			{
				db.update( CATEINFO_TABLE , values , "pn=" + pkgName , null );
			}
			else
			{
				db.insert( CATEINFO_TABLE , null , values );
			}
			c.close();
			db.close();
		}
	}
	
	public void updatedDict(
			ContentValues values ,
			int id )
	{
		// TODO Auto-generated method stub
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			Cursor c = db.query( DICT_TABLE , null , "id=" + id , null , null , null , null );
			if( c.moveToFirst() )
			{
				db.update( DICT_TABLE , values , "id=" + id , null );
			}
			else
			{
				db.insert( DICT_TABLE , null , values );
			}
			c.close();
			db.close();
		}
	}
	
	public void updateRecommendInfo(
			ContentValues values ,
			String pkgName )
	{
		// TODO Auto-generated method stub
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			Cursor c = db.query( RECOMMEDINFO_TABLE , null , "apk_pn=" + pkgName , null , null , null , null );
			if( c.moveToFirst() )
			{
				db.update( RECOMMEDINFO_TABLE , values , "apk_pn=" + pkgName , null );
			}
			else
			{
				db.insert( RECOMMEDINFO_TABLE , null , values );
			}
			c.close();
			db.close();
		}
	}
	
	public void InsertDict(
			List<ContentValues> ContentValuesList )
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			try
			{
				db.beginTransaction();
				for( ContentValues contentValues : ContentValuesList )
				{
					db.insert( DICT_TABLE , null , contentValues );
				}
			}
			finally
			{
				// TODO: handle exception
				db.setTransactionSuccessful();
				db.endTransaction();
			}
			db.close();
		}
	}
	
	public void InsertDict(
			ContentValues values )
	{
		// TODO Auto-generated method stub
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			db.insert( DICT_TABLE , null , values );
			db.close();
		}
	}
	
	public void InsertTree(
			List<ContentValues> ContentValuesList )
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			try
			{
				db.beginTransaction();
				for( ContentValues contentValues : ContentValuesList )
				{
					db.insert( TREE_TABLE , null , contentValues );
				}
			}
			finally
			{
				// TODO: handle exception
				db.setTransactionSuccessful();
				db.endTransaction();
			}
			db.close();
		}
	}
	
	public void InsertTree(
			ContentValues values )
	{
		// TODO Auto-generated method stub
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			db.insert( TREE_TABLE , null , values );
			db.close();
		}
	}
	
	public void InsertCaeInfo(
			List<ContentValues> ContentValuesList )
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			try
			{
				db.beginTransaction();
				for( ContentValues contentValues : ContentValuesList )
				{
					db.insert( CATEINFO_TABLE , null , contentValues );
				}
			}
			finally
			{
				// TODO: handle exception
				db.setTransactionSuccessful();
				db.endTransaction();
			}
			db.close();
		}
	}
	
	public void InsertCaeInfo(
			ContentValues values )
	{
		// TODO Auto-generated method stub
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			db.insert( CATEINFO_TABLE , null , values );
			db.close();
		}
	}
	
	public void InsertRecommendInfo(
			List<ContentValues> ContentValuesList )
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			try
			{
				db.beginTransaction();
				for( ContentValues contentValues : ContentValuesList )
				{
					db.insert( RECOMMEDINFO_TABLE , null , contentValues );
				}
			}
			finally
			{
				db.setTransactionSuccessful();
				db.endTransaction();
			}
			db.close();
		}
	}
	
	public void InsertRecommendInfo(
			ContentValues values )
	{
		// TODO Auto-generated method stub
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			db.beginTransaction();
			db.insert( RECOMMEDINFO_TABLE , null , values );
			db.close();
		}
	}
	
	public void deleteDict(
			int id )
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			db.delete( DICT_TABLE , "id=" + id , null );
			db.close();
		}
	}
	
	public void deleteTree(
			int id )
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			db.delete( TREE_TABLE , "id=" + id , null );
			db.close();
		}
	}
	
	public void deleteRecommendInfo(
			String pkgName )
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			db.delete( CATEINFO_TABLE , "apk_pn=" + pkgName , null );
			db.close();
		}
	}
	
	public void deleteCateInfo(
			String pkgName )
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getWritableDatabase();
			db.delete( CATEINFO_TABLE , "pn=" + pkgName , null );
			db.close();
		}
	}
	
	public void getDict(
			int IDindex )
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getReadableDatabase();
			Cursor c = db.query( DICT_TABLE , null , "id=" + IDindex , null , null , null , null );
			if( c.moveToFirst() )
			{
				int id = c.getInt( c.getColumnIndexOrThrow( "id" ) );
				String cn = c.getString( c.getColumnIndexOrThrow( "cn" ) );
				String en = c.getString( c.getColumnIndexOrThrow( "en" ) );
				int od = c.getInt( c.getColumnIndexOrThrow( "od" ) );
				//Log.v( "COOL" , "id:" + id + ", cn" + cn + ", en" + en + ", od" + od );
			}
			c.close();
			db.close();
		}
		//return info;
	}
	
	public void getTree(
			int IDindex )
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getReadableDatabase();
			Cursor c = db.query( TREE_TABLE , null , "id=" + IDindex , null , null , null , null );
			if( c.moveToFirst() )
			{
				int idc = c.getInt( c.getColumnIndexOrThrow( "idc" ) );
				int id = c.getInt( c.getColumnIndexOrThrow( "id" ) );
				//Log.v( "COOL" , "idc:" + idc + "id:" + id );
			}
			c.close();
			db.close();
		}
		//return info;
	}
	
	public void getCateinfo(
			String pkgName )
	{
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getReadableDatabase();
			Cursor c = db.query( CATEINFO_TABLE , null , "pn=" + "'" + pkgName + "'" , null , null , null , null );
			if( c.moveToFirst() )
			{
				String PkgName = c.getString( c.getColumnIndexOrThrow( "pn" ) );
				int fid = c.getInt( c.getColumnIndexOrThrow( "fid" ) );
				//Log.v( "COOL" , "pn:" + PkgName + "  fid:" + fid );
			}
			c.close();
			db.close();
		}
		//return info;
	}
	
	public void getAllDict()
	{
		// TODO Auto-generated method stub
		synchronized( syncObj )
		{
			CategoryHelper.dictMap.clear();
			SQLiteDatabase db = CategorydbHelper.getReadableDatabase();
			Cursor c = db.query( DICT_TABLE , null , null , null , null , null , null );
			while( c.moveToNext() )
			{
				int id = c.getInt( c.getColumnIndexOrThrow( "id" ) );
				String cn = c.getString( c.getColumnIndexOrThrow( "cn" ) );
				String en = c.getString( c.getColumnIndexOrThrow( "en" ) );
				int od = c.getInt( c.getColumnIndexOrThrow( "od" ) );
				//Log.v( "COOL" , "id:" + id + ", cn" + cn + ", en" + en + ", od" + od );
				CategoryHelper.dictMap.put( id , new DictData( id , cn , en , od ) );
			}
			c.close();
			db.close();
		}
	}
	
	public void getAllTree()
	{
		// TODO Auto-generated method stub
		synchronized( syncObj )
		{
			CategoryHelper.treeMap.clear();
			SQLiteDatabase db = CategorydbHelper.getReadableDatabase();
			Cursor c = db.query( TREE_TABLE , null , null , null , null , null , null );
			while( c.moveToNext() )
			{
				int idc = c.getInt( c.getColumnIndexOrThrow( "idc" ) );
				int id = c.getInt( c.getColumnIndexOrThrow( "id" ) );
				//Log.v( "COOL" , "idc:" + idc + "id:" + id );
				CategoryHelper.treeMap.put( idc , id );
			}
			c.close();
			db.close();
		}
	}
	
	public void getAllCateInfo()
	{
		// TODO Auto-generated method stub
		synchronized( syncObj )
		{
			CategoryHelper.cateinfoMap.clear();
			SQLiteDatabase db = CategorydbHelper.getReadableDatabase();
			Cursor c = db.query( CATEINFO_TABLE , null , null , null , null , null , null );
			while( c.moveToNext() )
			{
				String PkgName = c.getString( c.getColumnIndexOrThrow( "pn" ) );
				int fid = c.getInt( c.getColumnIndexOrThrow( "fid" ) );
				//Log.v( "COOL" , "pn:" + PkgName + "  fid:" + fid );
				CategoryHelper.cateinfoMap.put( PkgName , fid );
			}
			c.close();
			db.close();
		}
	}
	
	public void getAllRecommendInfo()
	{
		// TODO Auto-generated method stub
		synchronized( syncObj )
		{
			SQLiteDatabase db = CategorydbHelper.getReadableDatabase();
			Cursor c = db.query( RECOMMEDINFO_TABLE , null , null , null , null , null , null );
			if( c.getCount() >= 1 )
			{
				CategoryHelper.RecommendInfoMap.clear();
			}
			while( c.moveToNext() )
			{
				String apkinfoMap_key = c.getString( c.getColumnIndexOrThrow( "fidiid_key" ) );
				String apk_pn = c.getString( c.getColumnIndexOrThrow( "apk_pn" ) );
				String apk_type = c.getString( c.getColumnIndexOrThrow( "apk_type" ) );
				String apk_dlinfo = c.getString( c.getColumnIndexOrThrow( "apk_dlinfo" ) );
				String apk_vc = c.getString( c.getColumnIndexOrThrow( "apk_vc" ) );
				String apk_vn = c.getString( c.getColumnIndexOrThrow( "apk_vn" ) );
				int apk_size = c.getInt( c.getColumnIndexOrThrow( "apk_size" ) );
				String apk_en = c.getString( c.getColumnIndexOrThrow( "apk_en" ) );
				String apk_cn = c.getString( c.getColumnIndexOrThrow( "apk_cn" ) );
				String apk_fn = c.getString( c.getColumnIndexOrThrow( "apk_fn" ) );
				String apk_iconpath = c.getString( c.getColumnIndexOrThrow( "apk_iconpath" ) );
				int apk_flag = c.getInt( c.getColumnIndexOrThrow( "apk_flag" ) );
				int f_id = c.getInt( c.getColumnIndexOrThrow( "f_id" ) );
				String f_type = c.getString( c.getColumnIndexOrThrow( "f_type" ) );
				String f_en = c.getString( c.getColumnIndexOrThrow( "f_en" ) );
				String f_cn = c.getString( c.getColumnIndexOrThrow( "f_cn" ) );
				String f_fn = c.getString( c.getColumnIndexOrThrow( "f_fn" ) );
				RecommendApkInfo apkInfo = new RecommendApkInfo( apk_pn , apk_type , apk_dlinfo , apk_vc , apk_vn , apk_size , apk_cn , apk_en , apk_fn , apk_iconpath , apk_flag );
				RecommendInfo RInfo = CategoryHelper.RecommendInfoMap.get( f_id );
				if( null != RInfo && f_id == RInfo.getFolderID() )
				{
					RInfo.apkinfoMap.put( apkinfoMap_key , apkInfo );
				}
				else
				{
					Map<String , RecommendApkInfo> apkInfoMap = new HashMap<String , RecommendApkInfo>();
					apkInfoMap.put( apkinfoMap_key , apkInfo );
					RecommendInfo RinfoNew = new RecommendInfo( f_id , f_type , f_cn , f_en , f_fn , apkInfoMap );
					CategoryHelper.RecommendInfoMap.put( f_id , RinfoNew );
				}
			}
			c.close();
			db.close();
		}
	}
}
