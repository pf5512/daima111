package com.iLoong.launcher.Widget3D;


import java.util.HashMap;


/**
 * 提供创建Cache容器
 * 
 * @author Administrator
 * 
 */
public class CacheManager
{
	
	private static final int CAPACITY = 50;
	private static HashMap<String , Cache<? , ?>> mCacheMap = null;
	static
	{
		mCacheMap = new HashMap<String , Cache<? , ?>>();
	}
	
	/**
	 * 根据name创建或者获取一个cache对象
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static Cache<? , ?> getCache(
			String name )
	{
		if( mCacheMap.containsKey( name ) )
		{
			return mCacheMap.get( name );
		}
		else
		{
			@SuppressWarnings( "rawtypes" )
			Cache<? , ?> cache = new Cache( CAPACITY );
			mCacheMap.put( name , cache );
			return cache;
		}
	}
	
	public static void removeCache(
			String name )
	{
		if( mCacheMap.containsKey( name ) )
		{
			Cache<? , ?> cache = mCacheMap.get( name );
			cache.clear();
			mCacheMap.remove( name );
		}
	}
}
