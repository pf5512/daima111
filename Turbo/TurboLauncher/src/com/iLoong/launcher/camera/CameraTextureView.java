package com.iLoong.launcher.camera;


import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;


public class CameraTextureView extends TextureView
{
	
	public CameraTextureView(
			Context context ,
			AttributeSet attrs ,
			int defStyle )
	{
		super( context , attrs , defStyle );
	}
	
	public CameraTextureView(
			Context context ,
			AttributeSet attrs )
	{
		super( context , attrs );
	}
	
	public CameraTextureView(
			Context context )
	{
		super( context );
	}
	
	@Override
	protected void onMeasure(
			int widthMeasureSpec ,
			int heightMeasureSpec )
	{
		// 根据w的大小设置 以4：3方式设置view大小
		int w = (int)( View.MeasureSpec.getSize( widthMeasureSpec ) );
		int h = (int)( 4.0f / 3.0f * w );
		setMeasuredDimension( w , h );
	}
}
