package com.iLoong.launcher.recent;

import java.util.ArrayList;
import java.util.LinkedList;

import android.util.Log;

import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.data.ShortcutInfo;

/*
 * 这个类作为一个数据库专门处理最近使用中用户锁定的常用应用。
 * */
public class RecentAppFavariteDB
{
	private LinkedList< View3D> mFavariteDB;
	
	public RecentAppFavariteDB(){
		initFavarite();
	}
	public LinkedList<? extends View3D> getFavarite(){
		return mFavariteDB;
	}
	
	public void initFavarite(){
		if(mFavariteDB==null){
			mFavariteDB=new LinkedList<View3D>();
		}
		mFavariteDB.clear();
	}
	
	public boolean setFavartie(View3D app){
		if(mFavariteDB==null){
			throw new RuntimeException(" mFavariteDB is null");
		}
		if(equals( app )){
			return false;
		}
		mFavariteDB.add((View3D) app );
		Log.v( "RecentAppFavariteDB" , "add new app "+app.name );
		return true;
	}
	
	public boolean eques(View3D app){
		
		if( app instanceof RecentApp )
		{
			if( ( (RecentApp)app ).getItemInfo() instanceof ShortcutInfo )
			{
				ShortcutInfo info = (ShortcutInfo)( (RecentApp)app).getItemInfo();
				for(View3D v:mFavariteDB){
					ShortcutInfo infov = (ShortcutInfo)( (RecentApp)v).getItemInfo();
					if(info.intent.getComponent()!=null&&infov.intent.getComponent()!=null){
						if(info.intent.getComponent().getClassName().equals( infov.intent.getComponent().getClassName() )
							&&info.intent.getComponent().getPackageName().equals( infov.intent.getComponent().getPackageName()) )	
							{
								Log.v( "RecentAppFavariteDB " , "RecentAppFavariteDB eques true"  );
								return true;
							}
					}
				}
				
			}
		}
		Log.v( "RecentAppFavariteDB " , "RecentAppFavariteDB eques false"  );
		return false;
	}
}
