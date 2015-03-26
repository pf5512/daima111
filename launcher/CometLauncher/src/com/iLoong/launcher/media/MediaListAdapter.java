package com.iLoong.launcher.media;


import java.util.ArrayList;

import android.net.Uri;
import com.iLoong.launcher.Desktop3D.Log;
import android.view.View;

import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public abstract class MediaListAdapter
{
	
	protected int mCellCountX;
	protected int mCellCountY;
	protected GridPool gridPool;
	protected int pageCount;
	public int curPage;
	
	public abstract int getDataCount();
	
	public abstract View3D addView(
			int i ,
			ViewGroup3D vg );
	
	public int syncDataPageCount()
	{
		int numCells = mCellCountX * mCellCountY;
		pageCount = ( getDataCount() + numCells - 1 ) / numCells;
		return pageCount;
	}
	
	public void syncPageItems(
			View3D view ,
			int page ,
			int currentPage ,
			boolean immediate )
	{
		curPage = currentPage;
		int numCells = mCellCountX * mCellCountY;
		int startIndex = page * numCells;
		int endIndex = Math.min( startIndex + numCells , getDataCount() );
		ViewGroup3D layout = (ViewGroup3D)view;
		layout.removeAllViews();
		for( int i = endIndex - 1 ; i >= startIndex ; i-- )
		{
			View3D v = addView( i , layout );
			if( page == currentPage )
				( (MediaView)v ).prepare( ThumbnailThread.VISIBLE );
			if( nearCurrentPage( page , currentPage ) )
				( (MediaView)v ).prepare( ThumbnailThread.CACHE );
			else
				( (MediaView)v ).free();
		}
	}
	
	public void refreshPageItems(
			View3D view ,
			int page ,
			int currentPage )
	{
		ViewGroup3D layout = (ViewGroup3D)view;
		for( int i = layout.getChildCount() - 1 ; i >= 0 ; i-- )
		{
			MediaView v = (MediaView)( layout.getChildAt( i ) );
			if( page == currentPage )
				( (MediaView)v ).prepare( ThumbnailThread.VISIBLE );
			if( nearCurrentPage( page , currentPage ) )
				( (MediaView)v ).prepare( ThumbnailThread.CACHE );
			else
				v.free();
			v.refresh();
		}
	}
	
	public void clearSelect(
			View3D view )
	{
		ViewGroup3D layout = (ViewGroup3D)view;
		for( int i = 0 ; i < layout.getChildCount() ; i++ )
		{
			MediaView v = (MediaView)( layout.getChildAt( i ) );
			v.clearSelect();
		}
	}
	
	public void selectPageItems(
			View3D view )
	{
		ViewGroup3D layout = (ViewGroup3D)view;
		for( int i = 0 ; i < layout.getChildCount() ; i++ )
		{
			MediaView v = (MediaView)( layout.getChildAt( i ) );
			v.select();
		}
	}
	
	public void sharePageItems(
			View3D view ,
			ArrayList<Uri> list )
	{
		ViewGroup3D layout = (ViewGroup3D)view;
		for( int i = 0 ; i < layout.getChildCount() ; i++ )
		{
			MediaView v = (MediaView)( layout.getChildAt( i ) );
			v.share( list );
		}
	}
	
	public void onDelete(
			View3D view )
	{
		ViewGroup3D layout = (ViewGroup3D)view;
		for( int i = 0 ; i < layout.getChildCount() ; i++ )
		{
			MediaView v = (MediaView)( layout.getChildAt( i ) );
			v.onDelete();
		}
	}
	
	private boolean nearCurrentPage(
			int page ,
			int currentPage )
	{
		if( page == currentPage )
			return true;
		int tmp = currentPage + 1;
		if( tmp == pageCount )
			tmp = 0;
		if( page == tmp )
			return true;
		tmp = currentPage - 1;
		if( tmp == -1 )
			tmp = pageCount - 1;
		if( page == tmp )
			return true;
		return false;
	}
	
	public void free(
			ViewGroup3D viewGroup3D )
	{
		if( gridPool != null && viewGroup3D != null )
			gridPool.free( viewGroup3D );
	}
	
	public ViewGroup3D obtainView()
	{
		GridView3D grid = gridPool.get();
		grid.enableAnimation( false );
		grid.transform = true;
		grid.setAutoDrag( false );
		grid.show();
		return grid;
	}
	
	class GridPool
	{
		
		private ArrayList<GridView3D> grids;
		private float width;
		private float height;
		private int countX;
		private int countY;
		private float paddingBottom;
		
		public GridPool(
				int initCapacity ,
				float width ,
				float height ,
				float paddingBottom ,
				int countX ,
				int countY )
		{
			grids = new ArrayList<GridView3D>( initCapacity );
			this.width = width;
			this.height = height;
			this.countX = countX;
			this.countY = countY;
			this.paddingBottom = paddingBottom;
		}
		
		private GridView3D create()
		{
			GridView3D grid = new GridView3D( "allapplist" , width , height , countX , countY );
			grid.setPadding( R3D.applist_padding_left , R3D.applist_padding_right , R3D.applist_padding_top , (int)( R3D.applist_padding_bottom + paddingBottom ) );
			return grid;
		}
		
		public GridView3D get()
		{
			GridView3D grid = grids.isEmpty() ? create() : grids.remove( grids.size() - 1 );
			return grid;
		}
		
		public void free(
				ViewGroup3D viewGroup3D )
		{
			if( !grids.contains( viewGroup3D ) )
			{
				int size = viewGroup3D.getChildCount();
				MediaView view = null;
				for( int i = 0 ; i < size ; i++ )
				{
					view = (MediaView)viewGroup3D.getChildAt( i );
					view.free();
				}
				viewGroup3D.removeAllViews();
				grids.add( (GridView3D)viewGroup3D );
			}
		}
	}
}
