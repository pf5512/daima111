package com.umeng.analytics;

import com.iLoong.launcher.desktop.iLoongLauncher;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.Time;

public class CoCoMobclickAgent {
	public static void NewUser(Context content) {
		SharedPreferences sp = content.getSharedPreferences("analytics", 0);
		Boolean flag = sp.getBoolean("LNewUser", false);
		if (!flag) {
			MobclickAgent.onEvent(content, "LNewUser");
			Editor sharedata = sp.edit();
			sharedata.putBoolean("LNewUser", true);
			sharedata.commit();
		}

	}
	public static final String Type[]={"CONTENT_TYPE_PHOTO_BUCKET",
		"CONTENT_TYPE_AUDIO_ALBUM",
		"CONTENT_TYPE_VIDEO",};
	//每天执行一次函数
	public static void OnceEvent(Context content) {
		Time time = new Time("GMT+8");
		time.setToNow();
		int day = time.yearDay;
		SharedPreferences sp = content.getSharedPreferences("analytics", 0);
		int aday = sp.getInt("Day", -1);
		int StartLuancher=sp.getInt("StartLuancher", 0);
		Editor sharedata = sp.edit();
		
		StartLuancher++;
		
		
		if (day != aday) {
			MobclickAgent.onEvent(content, "LActiveUser");//活跃用户
			MobclickAgent.onEvent(content, "StartLuancher",""+(StartLuancher/10*10+StartLuancher%10>4?10:0));//启动Launcher次数
			sharedata.putInt("StartLuancher",0);
			
			MobclickAgent.onError(content);//错误信息
			
			for(int i=0;i<Type.length;i++){
				int num = sp.getInt(Type[i], 0);
				MobclickAgent.onEvent(content, Type[i],""+num);
				sharedata.putInt(Type[i], 0);
			}
			
			
			sharedata.putInt("Day", day);
			sharedata.commit();
		}else{
			
			sharedata.putInt("StartLuancher", StartLuancher);
			sharedata.commit();
		}

	}

	public static void add(String string) {
		Activity act= iLoongLauncher.getInstance();
		SharedPreferences sp = act.getSharedPreferences("analytics", 0);
		
		int num = sp.getInt(string, 0);
		num++;
		Editor sharedata = sp.edit();
		sharedata.putInt(string, num);
		sharedata.commit();
		
		
	}
}
