/* Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.iLoong.launcher.Workspace;


import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;

import com.iLoong.base.R;
import com.iLoong.launcher.CooeePlugin.CooeePluginHostView;
import com.iLoong.launcher.UI3DEngine.ConfigBase;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.widget.Widget;


public class CellLayout extends ViewGroup
{
	
	private boolean mPortrait;
	private int mCellWidth;
	private int mCellHeight;
	private int mLongAxisStartPadding;
	private int mLongAxisEndPadding;
	private int mShortAxisStartPadding;
	private int mShortAxisEndPadding;
	private int mShortAxisCells;
	private int mLongAxisCells;
	private int mWidthGap;
	private int mHeightGap;
	private final CellInfo mCellInfo = new CellInfo();
	int[] mCellXY = new int[2];
	boolean[][] mOccupied;
	public Workspace workspace;
	public boolean editable = true;
	public boolean visible = true;
	
	public CellLayout(
			Context context )
	{
		this( context , null );
	}
	
	public CellLayout(
			Context context ,
			AttributeSet attrs )
	{
		this( context , attrs , 0 );
	}
	
	public CellLayout(
			Context context ,
			AttributeSet attrs ,
			int defStyle )
	{
		super( context , attrs , defStyle );
		final Resources resources = getResources();
		mCellWidth = resources.getDimensionPixelSize( R.dimen.workspace_cell_width );
		mCellHeight = resources.getDimensionPixelSize( R.dimen.workspace_cell_height );
		mLongAxisStartPadding = 0;
		//            a.getDimensionPixelSize(R.styleable.CellLayout_longAxisStartPadding, 10);
		mLongAxisEndPadding = 0;
		//            a.getDimensionPixelSize(R.styleable.CellLayout_longAxisEndPadding, 10);
		mShortAxisStartPadding = 0;
		//            a.getDimensionPixelSize(R.styleable.CellLayout_shortAxisStartPadding, 10);
		mShortAxisEndPadding = 0;
		//            a.getDimensionPixelSize(R.styleable.CellLayout_shortAxisEndPadding, 10);
		mShortAxisCells = 4; //a.getInt(R.styleable.CellLayout_shortAxisCells, 4);
		mLongAxisCells = 4; //a.getInt(R.styleable.CellLayout_longAxisCells, 4);
		setAlwaysDrawnWithCacheEnabled( false );
	}
	
	public void setWorkspace(
			Workspace workspace )
	{
		this.workspace = workspace;
	}
	
	@Override
	public void addView(
			View child ,
			int index ,
			ViewGroup.LayoutParams params )
	{
		// Generate an id for each view, this assumes we have at most 256x256 cells
		// per workspace screen
		final LayoutParams cellParams = (LayoutParams)params;
		cellParams.regenerateId = true;
		super.addView( child , index , params );
	}
	
	public void removeWidget(
			Widget widget )
	{
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			View view = getChildAt( i );
			if( widget.getItemInfo().hostView == view )
			{
				removeView( view );
			}
		}
	}
	
	@Override
	protected void onMeasure(
			int widthMeasureSpec ,
			int heightMeasureSpec )
	{
		// TODO: currently ignoring padding
		int widthSpecMode = MeasureSpec.getMode( widthMeasureSpec );
		int widthSpecSize = MeasureSpec.getSize( widthMeasureSpec );
		int heightSpecMode = MeasureSpec.getMode( heightMeasureSpec );
		int heightSpecSize = MeasureSpec.getSize( heightMeasureSpec );
		if( widthSpecMode == MeasureSpec.UNSPECIFIED || heightSpecMode == MeasureSpec.UNSPECIFIED )
		{
			throw new RuntimeException( "CellLayout cannot have UNSPECIFIED dimensions" );
		}
		final int shortAxisCells = mShortAxisCells;
		final int longAxisCells = mLongAxisCells;
		final int longAxisStartPadding = mLongAxisStartPadding;
		final int longAxisEndPadding = mLongAxisEndPadding;
		final int shortAxisStartPadding = mShortAxisStartPadding;
		final int shortAxisEndPadding = mShortAxisEndPadding;
		final int cellWidth = mCellWidth;
		final int cellHeight = mCellHeight;
		mPortrait = heightSpecSize > widthSpecSize;
		int numShortGaps = shortAxisCells - 1;
		int numLongGaps = longAxisCells - 1;
		if( mPortrait )
		{
			int vSpaceLeft = heightSpecSize - longAxisStartPadding - longAxisEndPadding - ( cellHeight * longAxisCells );
			mHeightGap = vSpaceLeft / numLongGaps;
			int hSpaceLeft = widthSpecSize - shortAxisStartPadding - shortAxisEndPadding - ( cellWidth * shortAxisCells );
			if( numShortGaps > 0 )
			{
				mWidthGap = hSpaceLeft / numShortGaps;
			}
			else
			{
				mWidthGap = 0;
			}
		}
		else
		{
			int hSpaceLeft = widthSpecSize - longAxisStartPadding - longAxisEndPadding - ( cellWidth * longAxisCells );
			mWidthGap = hSpaceLeft / numLongGaps;
			int vSpaceLeft = heightSpecSize - shortAxisStartPadding - shortAxisEndPadding - ( cellHeight * shortAxisCells );
			if( numShortGaps > 0 )
			{
				mHeightGap = vSpaceLeft / numShortGaps;
			}
			else
			{
				mHeightGap = 0;
			}
		}
		int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			View child = getChildAt( i );
			LayoutParams lp = (LayoutParams)child.getLayoutParams();
			if( mPortrait )
			{
				lp.setup( cellWidth , cellHeight , mWidthGap , mHeightGap , shortAxisStartPadding , longAxisStartPadding );
			}
			else
			{
				lp.setup( cellWidth , cellHeight , mWidthGap , mHeightGap , longAxisStartPadding , shortAxisStartPadding );
			}
			if( lp.regenerateId )
			{
				child.setId( ( ( getId() & 0xFF ) << 16 ) | ( lp.cellX & 0xFF ) << 8 | ( lp.cellY & 0xFF ) );
				lp.regenerateId = false;
			}
			int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec( lp.width , MeasureSpec.EXACTLY );
			int childheightMeasureSpec = MeasureSpec.makeMeasureSpec( lp.height , MeasureSpec.EXACTLY );
			child.measure( childWidthMeasureSpec , childheightMeasureSpec );
		}
		setMeasuredDimension( widthSpecSize , heightSpecSize );
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
			View child = getChildAt( i );
			if( child.getVisibility() != GONE )
			{
				CellLayout.LayoutParams lp = (CellLayout.LayoutParams)child.getLayoutParams();
				int childLeft = lp.x;
				int childTop = lp.y;
				child.layout( childLeft , childTop , childLeft + lp.width , childTop + lp.height );
			}
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(
			MotionEvent ev )
	{
		if( ConfigBase.enable_news )
		{
			int index = workspace.indexOfChild( this );
			int cur = workspace.getCurrentScreen();
			if( index != cur )
				return false;
		}
		return super.dispatchTouchEvent( ev );
	}
	
	@Override
	public void dispatchDraw(
			Canvas canvas )
	{
		if( !ConfigBase.enable_news )
		{
			super.dispatchDraw( canvas );
			return;
		}
		long time = System.nanoTime();
		int index = workspace.indexOfChild( this );
		int count = workspace.getChildCount();
		int cur = workspace.getCurrentScreen();
		int width = getWidth();
		int x = (int)( ( workspace.scrollX - cur + index ) * width );
		if( x < -width * ( count - 1 ) )
			x += width * ( count );
		if( x > width * ( count - 1 ) )
			x -= width * ( count );
		if( x > width || x < -width )
		{
			if( visible )
			{
				visible = false;
				for( int i = 0 ; i < getChildCount() ; i++ )
				{
					getChildAt( i ).setVisibility( View.INVISIBLE );
				}
			}
		}
		else
		{
			int save = canvas.save();
			canvas.translate( x , 0 );
			super.dispatchDraw( canvas );
			canvas.restoreToCount( save );
			View child = null;
			for( int i = 0 ; i < getChildCount() ; i++ )
			{
				child = getChildAt( i );
				if( !visible )
					child.setVisibility( View.VISIBLE );
				if( child instanceof CooeePluginHostView )
				{
					( (CooeePluginHostView)child ).notifyScrollX( x , getWidth() );
				}
			}
			visible = true;
			//    		Log.i("widget", "celllayout draw count,cur,index,x="+count+","+cur+","+index+","+x);
			//    		Log.i("widget", "cell time:"+index+","+(System.nanoTime()-time)/1000);
		}
	}
	
	@Override
	public ViewGroup.LayoutParams generateLayoutParams(
			AttributeSet attrs )
	{
		return new CellLayout.LayoutParams( getContext() , attrs );
	}
	
	@Override
	protected boolean checkLayoutParams(
			ViewGroup.LayoutParams p )
	{
		return p instanceof CellLayout.LayoutParams;
	}
	
	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(
			ViewGroup.LayoutParams p )
	{
		return new CellLayout.LayoutParams( p );
	}
	
	public static class LayoutParams extends ViewGroup.MarginLayoutParams
	{
		
		/**
		 * Horizontal location of the item in the grid.
		 */
		@ViewDebug.ExportedProperty
		public int cellX;
		/**
		 * Vertical location of the item in the grid.
		 */
		@ViewDebug.ExportedProperty
		public int cellY;
		/**
		 * Number of cells spanned horizontally by the item.
		 */
		@ViewDebug.ExportedProperty
		public int cellHSpan;
		/**
		 * Number of cells spanned vertically by the item.
		 */
		@ViewDebug.ExportedProperty
		public int cellVSpan;
		/**
		 * Is this item currently being dragged
		 */
		public boolean isDragging;
		// X coordinate of the view in the layout.
		@ViewDebug.ExportedProperty
		int x;
		// Y coordinate of the view in the layout.
		@ViewDebug.ExportedProperty
		int y;
		boolean regenerateId;
		boolean dropped;
		boolean fullscreen = false;
		
		public LayoutParams(
				Context c ,
				AttributeSet attrs )
		{
			super( c , attrs );
			cellHSpan = 1;
			cellVSpan = 1;
		}
		
		public LayoutParams(
				ViewGroup.LayoutParams source )
		{
			super( source );
			cellHSpan = 1;
			cellVSpan = 1;
		}
		
		public LayoutParams(
				int cellX ,
				int cellY ,
				int cellHSpan ,
				int cellVSpan )
		{
			super( LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT );
			this.cellX = cellX;
			this.cellY = cellY;
			this.cellHSpan = cellHSpan;
			this.cellVSpan = cellVSpan;
		}
		
		public void setup(
				int cellWidth ,
				int cellHeight ,
				int widthGap ,
				int heightGap ,
				int hStartPadding ,
				int vStartPadding )
		{
			final int myCellHSpan = cellHSpan;
			final int myCellVSpan = cellVSpan;
			final int myCellX = cellX;
			final int myCellY = cellY;
			if( ConfigBase.widget_revise_complete )
			{
				width = myCellHSpan * cellWidth;
				height = myCellVSpan * cellHeight;
			}
			else
			{
				width = myCellHSpan * ConfigBase.Workspace_cell_each_width;
				height = myCellVSpan * ConfigBase.Workspace_cell_each_height;
			}
			if( fullscreen )
			{
				width = UtilsBase.getScreenHeight();
				height = UtilsBase.getScreenHeight();
			}
			x = cellX;
			y = UtilsBase.getScreenHeight() - cellY - height;
		}
	}
	
	public static final class CellInfo implements ContextMenu.ContextMenuInfo
	{
		
		/**
		 * See View.AttachInfo.InvalidateInfo for futher explanations about
		 * the recycling mechanism. In this case, we recycle the vacant cells
		 * instances because up to several hundreds can be instanciated when
		 * the user long presses an empty cell.
		 */
		static final class VacantCell
		{
			
			int cellX;
			int cellY;
			int spanX;
			int spanY;
			// We can create up to 523 vacant cells on a 4x4 grid, 100 seems
			// like a reasonable compromise given the size of a VacantCell and
			// the fact that the user is not likely to touch an empty 4x4 grid
			// very often 
			private static final int POOL_LIMIT = 100;
			private static final Object sLock = new Object();
			private static int sAcquiredCount = 0;
			private static VacantCell sRoot;
			private VacantCell next;
			
			static VacantCell acquire()
			{
				synchronized( sLock )
				{
					if( sRoot == null )
					{
						return new VacantCell();
					}
					VacantCell info = sRoot;
					sRoot = info.next;
					sAcquiredCount--;
					return info;
				}
			}
			
			void release()
			{
				synchronized( sLock )
				{
					if( sAcquiredCount < POOL_LIMIT )
					{
						sAcquiredCount++;
						next = sRoot;
						sRoot = this;
					}
				}
			}
			
			@Override
			public String toString()
			{
				return "VacantCell[x=" + cellX + ", y=" + cellY + ", spanX=" + spanX + ", spanY=" + spanY + "]";
			}
		}
		
		View cell;
		int cellX;
		int cellY;
		int spanX;
		int spanY;
		public int screen;
		boolean valid;
		final ArrayList<VacantCell> vacantCells = new ArrayList<VacantCell>( VacantCell.POOL_LIMIT );
		int maxVacantSpanX;
		int maxVacantSpanXSpanY;
		int maxVacantSpanY;
		int maxVacantSpanYSpanX;
		final Rect current = new Rect();
		
		void clearVacantCells()
		{
			final ArrayList<VacantCell> list = vacantCells;
			final int count = list.size();
			for( int i = 0 ; i < count ; i++ )
				list.get( i ).release();
			list.clear();
		}
		
		/**
		 * This method can be called only once! Calling #findVacantCellsFromOccupied will
		 * restore the ability to call this method.
		 *
		 * Finds the upper-left coordinate of the first rectangle in the grid that can
		 * hold a cell of the specified dimensions.
		 *
		 * @param cellXY The array that will contain the position of a vacant cell if such a cell
		 *               can be found.
		 * @param spanX The horizontal span of the cell we want to find.
		 * @param spanY The vertical span of the cell we want to find.
		 *
		 * @return True if a vacant cell of the specified dimension was found, false otherwise.
		 */
		public boolean findCellForSpan(
				int[] cellXY ,
				int spanX ,
				int spanY )
		{
			return findCellForSpan( cellXY , spanX , spanY , true );
		}
		
		boolean findCellForSpan(
				int[] cellXY ,
				int spanX ,
				int spanY ,
				boolean clear )
		{
			final ArrayList<VacantCell> list = vacantCells;
			final int count = list.size();
			boolean found = false;
			if( this.spanX >= spanX && this.spanY >= spanY )
			{
				cellXY[0] = cellX;
				cellXY[1] = cellY;
				found = true;
			}
			// Look for an exact match first
			for( int i = 0 ; i < count ; i++ )
			{
				VacantCell cell = list.get( i );
				if( cell.spanX == spanX && cell.spanY == spanY )
				{
					cellXY[0] = cell.cellX;
					cellXY[1] = cell.cellY;
					found = true;
					break;
				}
			}
			// Look for the first cell large enough
			for( int i = 0 ; i < count ; i++ )
			{
				VacantCell cell = list.get( i );
				if( cell.spanX >= spanX && cell.spanY >= spanY )
				{
					cellXY[0] = cell.cellX;
					cellXY[1] = cell.cellY;
					found = true;
					break;
				}
			}
			if( clear )
				clearVacantCells();
			return found;
		}
		
		@Override
		public String toString()
		{
			return "Cell[view=" + ( cell == null ? "null" : cell.getClass() ) + ", x=" + cellX + ", y=" + cellY + "]";
		}
	}
}
