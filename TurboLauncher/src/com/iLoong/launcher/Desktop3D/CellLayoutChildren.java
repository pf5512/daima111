/* Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.iLoong.launcher.Desktop3D;


import android.graphics.Rect;
import android.view.View;
import android.view.View.MeasureSpec;

import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class CellLayoutChildren extends ViewGroup3D
{
	
	static final String TAG = "CellLayoutChildren";
	// These are temporary variables to prevent having to allocate a new object just to
	// return an (x, y) value from helper functions. Do NOT use them to maintain other state.
	private final int[] mTmpCellXY = new int[2];
	private int mCellWidth;
	private int mCellHeight;
	private int mWidthGap;
	private int mHeightGap;
	
	public CellLayoutChildren()
	{
		super( TAG );
	}
	
	public void enableHardwareLayers()
	{
		//        setLayerType(LAYER_TYPE_HARDWARE, null);
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
	
	public View getChildAt(
			int x ,
			int y )
	{
		final int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			View3D child = getChildAt( i );
			//            if ((lp.cellX <= x) && (x < lp.cellX + lp.cellHSpan) &&
			//                    (lp.cellY <= y) && (y < lp.cellY + lp.cellVSpan)) {
			//                return child;
			//            }
		}
		return null;
	}
	
	protected void onMeasure(
			int widthMeasureSpec ,
			int heightMeasureSpec )
	{
		int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			View3D child = getChildAt( i );
			//            measureChild(child);
		}
		int widthSpecSize = MeasureSpec.getSize( widthMeasureSpec );
		int heightSpecSize = MeasureSpec.getSize( heightMeasureSpec );
		//        setMeasuredDimension(widthSpecSize, heightSpecSize);
	}
	
	public void setupLp(
			CellLayout3D.LayoutParams lp )
	{
		lp.setup( mCellWidth , mCellHeight , mWidthGap , mHeightGap );
	}
	
	public void measureChild(
			View child )
	{
		final int cellWidth = mCellWidth;
		final int cellHeight = mCellHeight;
		CellLayout3D.LayoutParams lp = (CellLayout3D.LayoutParams)child.getLayoutParams();
		lp.setup( cellWidth , cellHeight , mWidthGap , mHeightGap );
		int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec( lp.width , MeasureSpec.EXACTLY );
		int childheightMeasureSpec = MeasureSpec.makeMeasureSpec( lp.height , MeasureSpec.EXACTLY );
		child.measure( childWidthMeasureSpec , childheightMeasureSpec );
	}
	
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
			final View3D child = getChildAt( i );
			if( !child.isVisible() )
			{
				//                CellLayout2.LayoutParams lp = (CellLayout2.LayoutParams) child.getLayoutParams();
				//                int childLeft = lp.x;
				//                int childTop = lp.y;
				//                child.layout(childLeft, childTop, childLeft + lp.width, childTop + lp.height);
				//                if (lp.dropped) {
				//                    lp.dropped = false;
				//
				//                    final int[] cellXY = mTmpCellXY;
				//                    getLocationOnScreen(cellXY);
				//                    mWallpaperManager.sendWallpaperCommand(getWindowToken(),
				//                            WallpaperManager.COMMAND_DROP,
				//                            cellXY[0] + childLeft + lp.width / 2,
				//                            cellXY[1] + childTop + lp.height / 2, 0, null);
				//                }
			}
		}
	}
	
	//    @Override
	//    public boolean shouldDelayChildPressedState() {
	//        return false;
	//    }
	public void requestChildFocus(
			View child ,
			View focused )
	{
		//        super.requestChildFocus(child, focused);
		if( child != null )
		{
			Rect r = new Rect();
			child.getDrawingRect( r );
			//            requestRectangleOnScreen(r);
		}
	}
	
	public void cancelLongPress()
	{
		//        super.cancelLongPress();
		// Cancel long press for all children
		final int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			final View3D child = getChildAt( i );
			//            child.cancelLongPress();
		}
	}
	
	protected void setChildrenDrawingCacheEnabled(
			boolean enabled )
	{
		final int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			final View3D view = getChildAt( i );
			//            view.setDrawingCacheEnabled(enabled);
			// Update the drawing caches
			//            if (!view.isHardwareAccelerated() && enabled) {
			//                view.buildDrawingCache(true);
			//            }
		}
	}
	
	protected void setChildrenDrawnWithCacheEnabled(
			boolean enabled )
	{
		//        super.setChildrenDrawnWithCacheEnabled(enabled);
	}
	
	public int indexOfChild(
			View3D child )
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void removeViewsInLayout(
			int start ,
			int count )
	{
		// TODO Auto-generated method stub
	}
	
	public void removeViews(
			int start ,
			int count )
	{
		// TODO Auto-generated method stub
	}
	
	public void removeViewInLayout(
			View3D view )
	{
		// TODO Auto-generated method stub
	}
	
	public void removeViewAt(
			int index )
	{
		// TODO Auto-generated method stub
	}
	
	public void removeAllViewsInLayout()
	{
		// TODO Auto-generated method stub
	}
}
