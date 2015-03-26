package com.iLoong.launcher.media;


import java.util.ArrayList;

import android.graphics.Bitmap;
import com.iLoong.launcher.Desktop3D.Log;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.media.MediaCache.MediaDataObserver;
import com.iLoong.launcher.media.MediaListAdapter.GridPool;


public class VideoAdapter extends MediaListAdapter implements MediaDataObserver
{
	
	private final int observer_type = MediaCache.OBSERVER_TYPE_VIDEO;
	private ArrayList<VideoItem> videos;
	private MediaList list;
	private float minCellWidth;
	private float minCellHeight;
	
	public VideoAdapter(
			MediaList list ,
			float width ,
			float height )
	{
		this.list = list;
		minCellWidth = R3D.video_width + 2 * R3D.video_padding;
		minCellHeight = R3D.video_height + Utils3D.getTitleHeight( R3D.photo_title_size , R3D.photo_title_line ) + 2 * R3D.video_padding;
		mCellCountX = (int)( ( width - R3D.applist_padding_left - R3D.applist_padding_right ) / minCellWidth );
		mCellCountY = (int)( ( height - R3D.applist_padding_bottom - R3D.applist_padding_top ) / minCellHeight );
		gridPool = new GridPool( 3 , width , height , 0 , mCellCountX , mCellCountY );
		this.videos = MediaCache.getInstance().videos;
	}
	
	@Override
	public int getDataCount()
	{
		if( videos == null )
			return 0;
		else
			return videos.size();
	}
	
	@Override
	public View3D addView(
			int i ,
			ViewGroup3D vg )
	{
		if( videos == null )
			return null;
		GridView3D layout = (GridView3D)vg;
		VideoItem video = videos.get( i );
		View3D view = video.obtainView();
		view.setSize( layout.getCellWidth() , layout.getCellHeight() - R3D.video_padding );
		layout.addItem( view );
		view.setRotationZ( 0 );
		return view;
	}
	
	@Override
	public void update(
			int type )
	{
		if( type != observer_type )
			return;
		if( AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_VIDEO )
		{
			list.syncPages();
		}
	}
}
