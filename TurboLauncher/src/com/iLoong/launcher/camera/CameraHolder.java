package com.iLoong.launcher.camera;


import android.hardware.Camera;


/**
 * 管理设备的Camera
 * 
 * @author Xu Jin
 * 
 */
public class CameraHolder
{
	
	// 前向摄像头id
	private int mBackCameraId = -1;
	// 背面摄像头id
	private int mFrontCameraId = -1;
	// 摄像头信息
	private Camera.CameraInfo[] mInfo;
	//
	private int mCameraCount;
	// 实例
	private static CameraHolder instance;
	
	private CameraHolder()
	{
	}
	
	public static CameraHolder getInstance()
	{
		if( instance == null )
		{
			instance = new CameraHolder();
		}
		return instance;
	}
	
	public void init()
	{
		mCameraCount = Camera.getNumberOfCameras();
		mInfo = new Camera.CameraInfo[mCameraCount];
		// 获取camera info 信息
		for( int i = 0 ; i < mInfo.length ; ++i )
		{
			mInfo[i] = new Camera.CameraInfo();
			Camera.getCameraInfo( i , mInfo[i] );
			// 设置正反照相机
			if( mInfo[i].facing == Camera.CameraInfo.CAMERA_FACING_FRONT )
			{
				mFrontCameraId = i;
			}
			else if( mInfo[i].facing == Camera.CameraInfo.CAMERA_FACING_BACK )
			{
				mBackCameraId = i;
			}
		}
		// 如果没有正面相机，就使用默认的背面或者均无
		if( mFrontCameraId == -1 )
		{
			mFrontCameraId = mBackCameraId;
		}
	}
	
	/**
	 * 释放信息，调用getInstance()可以重新创建对象
	 */
	public void release()
	{
		this.mCameraCount = -1;
		this.mInfo = null;
		this.mBackCameraId = -1;
		this.mFrontCameraId = -1;
		instance = null;
	}
	
	public int getmCameraCount()
	{
		return mCameraCount;
	}
	
	public int getBackCameraId()
	{
		return mBackCameraId;
	}
	
	public int getFrontCameraId()
	{
		return mFrontCameraId;
	}
	
	public Camera.CameraInfo[] getmInfo()
	{
		return mInfo;
	}
}
