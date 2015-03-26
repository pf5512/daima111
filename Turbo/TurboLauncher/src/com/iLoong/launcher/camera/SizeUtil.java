package com.iLoong.launcher.camera;


import java.util.Iterator;
import java.util.List;

import android.hardware.Camera;

import com.iLoong.launcher.Desktop3D.Log;


public class SizeUtil
{
	
	public static int frameHeight = 0;
	public static int frameWidth = 0;
	
	public static Camera.Size getSuitablePictureSize(
			Camera.Parameters parameters )
	{
		return getSuitableSize( getSupportedPictureSizes( parameters ) );
	}
	
	public static Camera.Size getSuitablePictureSizeFromPreview(
			Camera.Parameters params )
	{
		if( params == null )
		{
			return null;
		}
		Camera.Size pictureSize = getSuitablePictureSize( params );
		return pictureSize;
	}
	
	public static Camera.Size getSuitablePreviewSize(
			Camera.Parameters paramParameters )
	{
		return getSuitableSize( getSupportedPreviewSizes( paramParameters ) );
	}
	
	public static Camera.Size getSuitablePreviewSizeFromPicture(
			Camera.Parameters paramParameters )
	{
		Camera.Size localSize1 = getSuitablePictureSize( paramParameters );
		Iterator localIterator = getSupportedPreviewSizes( paramParameters ).iterator();
		Camera.Size localSize2 = getSuitablePreviewSize( paramParameters );
		do
		{
			if( !localIterator.hasNext() )
				break;
			localSize2 = (Camera.Size)localIterator.next();
		}
		while( !isSameSize( localSize2 , localSize1 ) );
		return localSize2;
	}
	
	public static Camera.Size getSuitableSize(
			List<Camera.Size> paramList )
	{
		return getSuitableSize( paramList , getCameraFrameHeight() );
	}
	
	/**
	 * 给定高度，获得最相近的camera 尺寸
	 * 
	 * @param sizeList
	 *            尺寸了列表
	 * @param height
	 *            frame高度
	 * @return camera大小
	 */
	public static Camera.Size getSuitableSize(
			List<Camera.Size> sizeList ,
			int height )
	{
		if( sizeList == null )
		{
			return null;
		}
		if( height == 0 )
		{
			Log.e( "jinxu" , "getSuitableSize height = 0" );
		}
		int min = -1;
		Camera.Size ret = null;
		for( int i = 0 ; i < sizeList.size() ; ++i )
		{
			Camera.Size size = sizeList.get( i );
			if( 3 * size.width != 4 * size.height )
				continue;
			// 大小必须小于480，以避免预览速度下降
			//			if (size.height > 480) {
			//				continue;
			//			}
			if( min == -1 )
			{
				min = Math.abs( size.height - height );
				ret = size;
			}
			int diff = Math.abs( size.height - height );
			if( diff < min )
			{
				min = diff;
				ret = size;
			}
		}
		return ret;
	}
	
	/**
	 * 获得camera支持的图片大小
	 * 
	 * @param params
	 *            camera配置参数
	 * @return 尺寸列表
	 */
	public static List<Camera.Size> getSupportedPictureSizes(
			Camera.Parameters params )
	{
		if( params == null )
		{
			return null;
		}
		return params.getSupportedPictureSizes();
	}
	
	/**
	 * 通过参数获得camera支持的preview大小
	 * 
	 * @param params
	 *            camera配置参数
	 * @return 尺寸列表
	 */
	public static List<Camera.Size> getSupportedPreviewSizes(
			Camera.Parameters params )
	{
		if( params == null )
		{
			return null;
		}
		return params.getSupportedPreviewSizes();
	}
	
	/**
	 * 判断两个大小是否相同
	 * 
	 * @param size1
	 *            尺寸1
	 * @param size2
	 *            尺寸2
	 * @return
	 */
	public static boolean isSameSize(
			Camera.Size size1 ,
			Camera.Size size2 )
	{
		if( ( size1 == null ) || ( size2 == null ) )
		{
			return false;
		}
		return size1.width == size2.width && size1.height == size2.height;
	}
	
	/**
	 * 计算高度
	 * 
	 * @return
	 */
	public static int getCameraFrameHeight()
	{
		return frameHeight;
	}
	
	/**
	 * 
	 * @param height
	 */
	public static void setCameraFrameHeight(
			int height )
	{
		if( height < 0 )
		{
			height = 0;
		}
		frameHeight = height;
	}
}
