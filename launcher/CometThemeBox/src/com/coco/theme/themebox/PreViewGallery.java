package com.coco.theme.themebox;


import com.coco.theme.themebox.preview.ThemePreviewHotActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;


public class PreViewGallery extends Gallery
{
	
	private Context mContext;
	public boolean fullScreen = false;
	
	public PreViewGallery(
			Context context ,
			AttributeSet attrs )
	{
		super( context , attrs );
		mContext = context;
	}
	
	public PreViewGallery(
			Context context )
	{
		super( context );
		mContext = context;
	}
	
	public PreViewGallery(
			Context context ,
			AttributeSet attrs ,
			int defStyle )
	{
		super( context , attrs , defStyle );
		mContext = context;
	}
	
	@Override
	public boolean onFling(
			MotionEvent e1 ,
			MotionEvent e2 ,
			float velocityX ,
			float velocityY )
	{
		int kEvent;
		if( isScrollingLeft( e1 , e2 ) )
		{ // Check if scrolling left
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		}
		else
		{
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown( kEvent , null );
		return true;
	}
	
	private boolean isScrollingLeft(
			MotionEvent e1 ,
			MotionEvent e2 )
	{
		return e2.getX() > e1.getX();
	}
	
	public int getMaxOffset()
	{
		View sv = getSelectedView();
		int j = ( getCount() - 1 ) * sv.getWidth();
		return j;
	}
	
	public int getCurrentOffset()
	{
		View sv = getSelectedView();
		int sp = getSelectedItemPosition();
		int ret = sp * sv.getWidth() - sv.getLeft() + ( getCenterOfGallery() - sv.getWidth() / 2 );
		return ret;
	}
	
	private int getCenterOfGallery()
	{
		return ( getWidth() - getPaddingLeft() - getPaddingLeft() ) / 2 + getPaddingLeft();
	}
	
	@Override
	protected void onScrollChanged(
			int l ,
			int t ,
			int oldl ,
			int oldt )
	{
		super.onScrollChanged( l , t , oldl , oldt );
		if( fullScreen )
		{
			( (ThemePreviewHotActivity)mContext ).setPageControlPosition( getCurrentOffset() );
		}
		Log.d( "onScrollChanged" , "c=" + getCurrentOffset() + ",m=" + getMaxOffset() );
	}
	
	public void setFullScreen(
			boolean b )
	{
		fullScreen = b;
	}
	// @Override
	// public boolean onScroll(MotionEvent paramMotionEvent1,
	// MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
	//
	// float f = 3F * paramFloat1;
	//
	// return super.onScroll(paramMotionEvent1, paramMotionEvent2, f,
	// paramFloat2);
	//
	// }
}
