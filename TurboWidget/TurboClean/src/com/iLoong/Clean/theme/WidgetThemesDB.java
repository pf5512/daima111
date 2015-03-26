package com.iLoong.Clean.theme;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.iLoong.launcher.Desktop3D.Log;

import com.iLoong.launcher.Desktop3D.DefaultLayout;

public class WidgetThemesDB {
	private SQLiteDatabase m_Database = null;
	private static final String DATABASE_NAME = "launcher.db";
	Context mContext;

	public WidgetThemesDB(Context context) {
		mContext = context;
		//Init();
	}

	public void Init() {
		CreateDataBase();
		DBInit();
	}

	private void CreateDataBase() {
		try {
			m_Database = mContext.openOrCreateDatabase(DATABASE_NAME, Activity.MODE_WORLD_WRITEABLE, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void DBInit() {
		CreateTable();
	}

	public void Close() {
	}

	private void CreateTable() {
		CreateTabThemes();
		CreateTabRun();
		CreateTabScreenCount();
	}

	
	private void CreateTabThemes() {

		String sql;
		sql = "create table IF NOT EXISTS tb_themes ( ";
		sql += "theme TEXT";
		sql += " );";
		try {
			m_Database.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}

	
	private void CreateTabRun() {

		String sql;
		sql = "create table IF NOT EXISTS tb_runstatus ( ";
		sql += "run int,";
		sql += "themes int";
		sql += " );";
		ExecSql(sql);

		
		sql = "select run from tb_runstatus;";
		Cursor result = m_Database.rawQuery(sql, null);
		result.moveToFirst();
			if (result.isAfterLast()) {
				//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
				//prefs.edit().putInt("theme_status", 0).commit();
				if (DefaultLayout.getInstance().install_change_wallpaper==true)
				{
					 sql="insert into tb_runstatus values(0,1);"; 
				}
				else
				{
				   sql="insert into tb_runstatus values(0,0);"; 
				}
				ExecSql(sql);
			} 
		result.close();
		return;
	}	
	private void CreateTabScreenCount()
	{
		String sql;
		sql = "create table IF NOT EXISTS tbscreen_count ( ";
		sql += "screen_count INTEGER";
		sql += " );";
		try {
			m_Database.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//SaveScreenCount(0);
		return;
	}
	public int getScreenCount() {

		String sql;
		int screen_count=0;
		sql = "select screen_count from tbscreen_count;";
		Cursor result = m_Database.rawQuery(sql, null);

		result.moveToFirst();
		while (!result.isAfterLast()) {
			screen_count = result.getInt(0);
			break;
		}
		result.close();
		return screen_count;
	}
	public void SaveScreenCount(int screen_count) {
		String sql;
		sql = "select screen_count from tbscreen_count;";
		Cursor result = m_Database.rawQuery(sql, null);
		result.moveToFirst();
		if (result.isAfterLast()) {
			sql = "insert into tbscreen_count (screen_count) ";
			sql += "values(";
			sql +=  String.valueOf(screen_count) + " ";
			sql += ");";
		} else {
			sql = "update tbscreen_count set ";
			sql += " screen_count=" +  String.valueOf(screen_count) + " ";
			sql += " where 1=1;";
		}
		ExecSql(sql);
		result.close();
	}
	public ThemeConfig getTheme() {
		ThemeConfig themeconf = new ThemeConfig();

		SharedPreferences prefs = mContext.getSharedPreferences("theme", Activity.MODE_WORLD_WRITEABLE);
		themeconf.theme = prefs.getString("theme", "");
		return themeconf;
	}

	public void RemoveThemes(ThemeConfig themeconf) {
		String sql;
		if (themeconf.theme == null)
			return;
		sql = "delete from tb_themes where theme='" + themeconf.theme + "';";
		ExecSql(sql);
	}

	public void SaveThemes(ThemeConfig themeconf) {
		SharedPreferences prefs = mContext.getSharedPreferences("theme", Activity.MODE_WORLD_WRITEABLE);
		prefs.edit().putString("theme", themeconf.theme).commit();
//		Log.e("theme", "save theme 3:"+themeconf.theme);
	}

	
	public void SaveRunStatus(int status) {
		String sql;
		sql = "update tb_runstatus set ";
		sql += " run=" + String.valueOf(status) + " ";
		sql += " where 1=1;";
		ExecSql(sql);
	}	
	
	public int getRunStatus() {
		int ret=0;
		String sql;
		sql = "select run from tb_runstatus;";
		Cursor result = m_Database.rawQuery(sql, null);

		result.moveToFirst();
		while (!result.isAfterLast()) {
			ret = result.getInt(0);
			break;
		}
		result.close();
		return ret;
	}	

	
	
	public void SaveThemesStatus(int status) {
		SharedPreferences prefs = mContext.getSharedPreferences("theme", Activity.MODE_WORLD_WRITEABLE);
		prefs.edit().putInt("theme_status", status).commit();
	}	
	
	public int getThemesStatus() {
		SharedPreferences prefs = mContext.getSharedPreferences("theme", Activity.MODE_WORLD_WRITEABLE);
		int ret;
		if (DefaultLayout.getInstance().install_change_wallpaper==true)
		{
			ret = prefs.getInt("theme_status", 1);
		}
		else
		{
		    ret = prefs.getInt("theme_status", 0);
		}
		return ret;
	}	
	
	
	private void ExecSql(String sql) {
		try {
			m_Database.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}
}
