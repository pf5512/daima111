package com.iLoong.launcher.camera;


import java.io.File;

import android.os.Environment;
import android.os.StatFs;


/**
 * 存储相关工具类，主要用于检测当前系统是否有足够的存储空间
 * @author Xu Jin
 *
 */
public class StorageUtil
{
	
	/**
	 * 判断是否有外存储空间
	 * @return
	 */
	public static boolean hasExternalStorage()
	{
		String state = Environment.getExternalStorageState();
		if( !Environment.MEDIA_MOUNTED.equals( state ) )
		{
			return false;
		}
		return true;
	}
	
	/**
	 * 判断sd卡是否还有足够的空间
	 * @return
	 */
	public static boolean hasFreeExternalSpace()
	{
		if( !hasExternalStorage() )
		{
			return false;
		}
		File file = Environment.getExternalStorageDirectory();
		int count = new StatFs( file.toString() ).getAvailableBlocks();
		return count > 0;
	}
	
	public static boolean externalSpaceIsWritable()
	{
		if( !hasExternalStorage() )
		{
			return false;
		}
		return false;
	}
}
