package com.iLoong.launcher.search;


import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class AllAppGroup extends ViewGroup3D
{
	
	public AllAppList mAllAppList;
	
	public AllAppGroup(
			String name )
	{
		super( name );
		setSize( QsearchConstants.W_ALL_APP_GROUP , QsearchConstants.H_ALL_APP_GROUP );
		mAllAppList = new AllAppList( "allAppList" );
		addView( mAllAppList );
	}
	
	public void load()
	{
		mAllAppList.load();
	}
	
	public void reLoad()
	{
		mAllAppList.reLoad();
	}
	
	public void release()
	{
		mAllAppList.release();
	}
}
