package com.coco.theme.themebox;


import java.util.ArrayList;

import com.iLoong.base.themebox.R;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class PageControl extends LinearLayout
{
	
	private int mIndicatorSize = 7;
	private ArrayList<ImageView> indicators;
	private int mPageCount = 0;
	private int mCurrentPage = 0;
	private Context mContext;
	
	public PageControl(
			Context context ,
			int width )
	{
		super( context );
		mContext = context;
		initPageControl();
	}
	
	public PageControl(
			Context context ,
			AttributeSet attrs )
	{
		super( context , attrs );
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate()
	{
		initPageControl();
	}
	
	private void initPageControl()
	{
		indicators = new ArrayList<ImageView>();
		mIndicatorSize = (int)( mIndicatorSize * getResources().getDisplayMetrics().density );
	}
	
	public void setPageCount(
			int pageCount ,
			int width )
	{
		Bitmap nopress = BitmapFactory.decodeResource( mContext.getResources() , R.drawable.scroll_nopress );
		mPageCount = pageCount;
		for( int i = 0 ; i < pageCount ; i++ )
		{
			final ImageView imageView = new ImageView( mContext );
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( nopress.getWidth() , nopress.getHeight() );
			params.setMargins( 0 , 0 , 0 , 0 );
			imageView.setLayoutParams( params );
			imageView.setBackgroundDrawable( new BitmapDrawable( nopress ) );
			indicators.add( imageView );
			addView( imageView );
		}
	}
	
	public void setCurrentPage(
			int currentPage )
	{
		if( currentPage < mPageCount )
		{
			Bitmap nopress = BitmapFactory.decodeResource( mContext.getResources() , R.drawable.scroll_nopress );
			Bitmap press = BitmapFactory.decodeResource( mContext.getResources() , R.drawable.comet_indicator );
			indicators.get( mCurrentPage ).setBackgroundDrawable( new BitmapDrawable( nopress ) );
			indicators.get( currentPage ).setBackgroundDrawable( new BitmapDrawable( press ) );
			mCurrentPage = currentPage;
		}
	}
}
