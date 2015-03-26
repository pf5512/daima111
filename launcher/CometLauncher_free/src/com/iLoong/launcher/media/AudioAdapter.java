package com.iLoong.launcher.media;


import java.util.ArrayList;
import java.util.HashMap;

import com.iLoong.launcher.Desktop3D.Log;

import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.ListView3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.media.MediaListAdapter.GridPool;


public class AudioAdapter extends MediaListAdapter
{
	
	private ArrayList<AudioItem> audios;
	private ListView3D list;
	
	public AudioAdapter(
			float width ,
			float height )
	{
		list = new ListView3D( "audios" );
		list.width = width;
		list.height = height;
		list.paddingBottom = R3D.bottom_bar_height;
	}
	
	public void setAudios(
			ArrayList<AudioItem> audios )
	{
		this.audios = audios;
	}
	
	public ViewGroup3D obtainView()
	{
		list.removeAllViews();
		list.show();
		return list;
	}
	
	public void free(
			ViewGroup3D viewGroup3D )
	{
	}
	
	public int syncDataPageCount()
	{
		pageCount = 1;
		return pageCount;
	}
	
	public void syncPageItems(
			View3D view ,
			int page ,
			int currentPage ,
			boolean immediate )
	{
		curPage = currentPage;
		int startIndex = 0;
		int endIndex = getDataCount();
		Log.v( "resMan" , "syncPageItems " + " start" + startIndex + " end:" + endIndex );
		ViewGroup3D layout = (ViewGroup3D)view;
		layout.removeAllViews();
		for( int i = endIndex - 1 ; i >= startIndex ; i-- )
		{
			View3D v = addView( i , layout );
			( (MediaView)v ).prepare( ThumbnailThread.VISIBLE );
		}
	}
	
	@Override
	public int getDataCount()
	{
		if( audios == null )
			return 0;
		else
			return audios.size();
	}
	
	@Override
	public View3D addView(
			int i ,
			ViewGroup3D vg )
	{
		if( audios == null )
			return null;
		ListView3D list = (ListView3D)vg;
		AudioItem audio = audios.get( i );
		View3D view = audio.obtainView();
		view.setSize( list.width , AudioItem.itemHeight );
		list.addItem( view );
		view.setRotationZ( 0 );
		return view;
	}
}
