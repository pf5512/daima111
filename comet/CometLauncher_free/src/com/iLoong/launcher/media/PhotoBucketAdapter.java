package com.iLoong.launcher.media;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.iLoong.launcher.Desktop3D.Log;

import com.badlogic.gdx.Gdx;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.media.MediaCache.MediaDataObserver;


public class PhotoBucketAdapter extends MediaListAdapter implements MediaDataObserver
{
	
	private final int observer_type = MediaCache.OBSERVER_TYPE_PHOTO;
	private HashMap<Integer , PhotoBucket> photoBuckets;
	private MediaList list;
	private float minCellWidth;
	private float minCellHeight;
	
	public PhotoBucketAdapter(
			MediaList list ,
			float width ,
			float height )
	{
		this.list = list;
		minCellWidth = R3D.photo_bucket_width;
		minCellHeight = R3D.photo_bucket_height + Utils3D.getTitleHeight( R3D.photo_title_size , R3D.photo_title_line );
		mCellCountX = (int)( ( width - R3D.applist_padding_left - R3D.applist_padding_right ) / minCellWidth );
		mCellCountY = (int)( ( height - R3D.applist_padding_bottom - R3D.applist_padding_top ) / minCellHeight );
		gridPool = new GridPool( 3 , width , height , 0 , mCellCountX , mCellCountY );
		this.photoBuckets = MediaCache.getInstance().photoBuckets;
		MediaCache.getInstance().attach( this );
	}
	
	@Override
	public int getDataCount()
	{
		if( photoBuckets == null )
			return 0;
		else
			return photoBuckets.size();
	}
	
	@Override
	public View3D addView(
			int i ,
			ViewGroup3D vg )
	{
		GridView3D layout = (GridView3D)vg;
		PhotoBucket bucket = photoBuckets.get( MediaCache.getInstance().photoBucketIds.get( i ) );
		View3D view = bucket.obtainView();
		view.setSize( layout.getCellWidth() , layout.getCellHeight() + R3D.photo_padding );
		layout.addItem( view );
		return view;
	}
	
	@Override
	public void update(
			int type )
	{
		if( type != observer_type )
			return;
		if( AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_PHOTO_BUCKET )
		{
			list.syncPages();
		}
		else if( AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_PHOTO )
		{
			MediaListAdapter adapter = list.getAdapter();
			if( adapter instanceof PhotoAdapter )
			{
				PhotoAdapter photoAdapter = (PhotoAdapter)adapter;
				PhotoBucket bucket = MediaCache.getInstance().getPhotoBucket( photoAdapter.getPhotoBucketView().bucket.bucketId );
				if( bucket != null )
				{
					photoAdapter.setPhotos( bucket.photos );
				}
				else
				{
					photoAdapter.setPhotos( new ArrayList<PhotoItem>( 0 ) );
				}
			}
			list.syncPages();
		}
	}
}
