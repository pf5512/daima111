package cool.sdk.Category;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;


public class DatabaseHelper extends SQLiteOpenHelper
{
	
	private static final int VERSION = 1;
	
	public DatabaseHelper(
			Context context ,
			String name ,
			CursorFactory factory ,
			int version )
	{
		//必须通过super调用父类当中的构造函数  
		super( context , name , factory , version );
	}
	
	public DatabaseHelper(
			Context context ,
			String name ,
			int version )
	{
		this( context , name , null , version );
	}
	
	public DatabaseHelper(
			Context context ,
			String name )
	{
		this( context , name , VERSION );
	}
	
	@Override
	public void onCreate(
			SQLiteDatabase db )
	{
		// TODO Auto-generated method stub
		db.execSQL( "CREATE TABLE IF NOT EXISTS dict (id integer primary key, cn varchar(255), en varchar(255), od integer)" );
		db.execSQL( "CREATE TABLE IF NOT EXISTS tree (idc integer primary key, id integer)" );
		db.execSQL( "CREATE TABLE IF NOT EXISTS cateinfo (pn varchar(255) primary key, fid integer)" );
		db.execSQL( "CREATE TABLE IF NOT EXISTS recommend_info (fidiid_key varchar(255) primary key, apk_pn varchar(255), apk_type char(4), apk_dlinfo text, apk_vc text, apk_vn text, apk_size integer, apk_cn text, apk_en text, apk_fn text, apk_iconpath text,apk_flag integer,f_id integer, f_type char(4), f_en text, f_cn text, f_fn text)" );
	}
	
	@Override
	public void onUpgrade(
			SQLiteDatabase db ,
			int arg1 ,
			int arg2 )
	{
		// TODO Auto-generated method stub
	}
}
