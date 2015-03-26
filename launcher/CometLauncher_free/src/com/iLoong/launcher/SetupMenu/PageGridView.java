package com.iLoong.launcher.SetupMenu;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


public class PageGridView extends ViewGroup
{
	
	private int mCellWidth;
	private int mCellHeight;
	private int mWidthGap;
	private int mHeightGap;
	
	public PageGridView(
			Context context )
	{
		super( context );
	}
	
	public void setCellDimensions(
			int cellWidth ,
			int cellHeight ,
			int widthGap ,
			int heightGap )
	{
		mCellWidth = cellWidth;
		mCellHeight = cellHeight;
		mWidthGap = widthGap;
		mHeightGap = heightGap;
	}
	
	@Override
	protected void onMeasure(
			int widthMeasureSpec ,
			int heightMeasureSpec )
	{
		int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			View child = getChildAt( i );
			measureChild( child );
		}
		int widthSpecSize = MeasureSpec.getSize( widthMeasureSpec );
		int heightSpecSize = MeasureSpec.getSize( heightMeasureSpec );
		setMeasuredDimension( widthSpecSize , heightSpecSize );
	}
	
	public void setupLp(
			LayoutParams lp )
	{
		lp.setup( mCellWidth , mCellHeight , mWidthGap , mHeightGap );
	}
	
	public void measureChild(
			View child )
	{
		final int cellWidth = mCellWidth;
		final int cellHeight = mCellHeight;
		LayoutParams lp = (LayoutParams)child.getLayoutParams();
		lp.setup( cellWidth , cellHeight , mWidthGap , mHeightGap );
		int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec( lp.width , MeasureSpec.EXACTLY );
		int childheightMeasureSpec = MeasureSpec.makeMeasureSpec( lp.height , MeasureSpec.EXACTLY );
		child.measure( childWidthMeasureSpec , childheightMeasureSpec );
	}
	
	@Override
	protected void onLayout(
			boolean changed ,
			int l ,
			int t ,
			int r ,
			int b )
	{
		int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			final View child = getChildAt( i );
			if( child.getVisibility() != GONE )
			{
				LayoutParams lp = (LayoutParams)child.getLayoutParams();
				int childLeft = lp.x;
				int childTop = lp.y;
				child.layout( childLeft , childTop , childLeft + lp.width , childTop + lp.height );
			}
		}
	}
	
	public View getChildAt(
			int x ,
			int y )
	{
		final int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			View child = getChildAt( i );
			LayoutParams lp = (LayoutParams)child.getLayoutParams();
			if( ( lp.cellX == x ) && ( lp.cellY == y ) )
			{
				return child;
			}
		}
		return null;
	}
	
	public static class LayoutParams extends ViewGroup.MarginLayoutParams
	{
		
		public int cellX = 0;
		public int cellY = 0;
		public int x = 0;
		public int y = 0;
		
		public LayoutParams(
				int cellX ,
				int cellY )
		{
			super( LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT );
			//  super(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			this.cellX = cellX;
			this.cellY = cellY;
		}
		
		public void setup(
				int cellWidth ,
				int cellHeight ,
				int widthGap ,
				int heightGap )
		{
			width = cellWidth - leftMargin - rightMargin;
			height = cellHeight - topMargin - bottomMargin;
			x = cellX * ( cellWidth + widthGap ) + leftMargin;
			y = cellY * ( cellHeight + heightGap ) + topMargin + Tools.dip2px( SetMenuDesktop.mContext , SetMenuDesktop.frameTopMargin * SetupMenu.mScale );
		}
	}
}
