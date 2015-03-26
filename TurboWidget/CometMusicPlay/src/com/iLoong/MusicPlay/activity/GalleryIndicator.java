package com.iLoong.MusicPlay.activity;


import java.util.ArrayList;
import java.util.List;

import com.cooeeui.cometmusicplay.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;


public class GalleryIndicator
{
	
	private Context mContext;
	private LinearLayout parentLayout;
	private int pageCount;
	private int pageIndex;
	private Drawable drawCurPage;
	private Drawable drawOtherPage;
	private List<ImageView> imgViewList;
	
	public GalleryIndicator(
			LinearLayout layout ,
			int count )
	{
		parentLayout = layout;
		mContext = layout.getContext();
		drawCurPage = mContext.getResources().getDrawable( R.drawable.pointdown );
		drawOtherPage = mContext.getResources().getDrawable( R.drawable.point );
		imgViewList = new ArrayList<ImageView>();
		pageCount = Math.max( count , 1 );
		pageIndex = 0;
		for( int i = 0 ; i < pageCount ; i++ )
		{
			ImageView iv = new ImageView( mContext );
			iv.setAdjustViewBounds( true );
			LayoutParams lParams = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT );
			if( i != 0 )
			{
				lParams.setMargins( dip2px( mContext , 1f ) , 0 , 0 , 0 );
			}
			iv.setLayoutParams( lParams );
			if( i == pageIndex )
			{
				iv.setImageDrawable( drawCurPage );
			}
			else
			{
				iv.setImageDrawable( drawOtherPage );
			}
			parentLayout.addView( iv );
			imgViewList.add( iv );
		}
	}
	
	public static int dip2px(
			Context context ,
			float dpValue )
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)( dpValue * scale + 0.5f );
	}
	
	public static int px2dip(
			Context context ,
			float pxValue )
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)( pxValue / scale + 0.5f );
	}
	
	public void setFocus(
			int index )
	{
		if( pageIndex != index )
		{
			imgViewList.get( pageIndex ).setImageDrawable( drawOtherPage );
			pageIndex = index;
			imgViewList.get( pageIndex ).setImageDrawable( drawCurPage );
		}
	}
}
