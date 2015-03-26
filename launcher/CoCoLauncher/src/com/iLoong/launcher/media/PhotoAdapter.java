package com.iLoong.launcher.media;

import java.util.ArrayList;
import java.util.HashMap;

import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.media.MediaCache.MediaDataObserver;
import com.iLoong.launcher.media.MediaListAdapter.GridPool;
import com.iLoong.launcher.media.PhotoBucket.PhotoBucketView;

public class PhotoAdapter extends MediaListAdapter {
	private ArrayList<PhotoItem> photos;
	private float minCellWidth;
	private float minCellHeight;
	private PhotoBucketView photoBucketView;

	public PhotoAdapter(float width, float height) {
		minCellWidth = R3D.photo_width + 3 * R3D.photo_padding;
		minCellHeight = R3D.photo_height + 3 * R3D.photo_padding;
		mCellCountX = (int) ((width - R3D.applist_padding_left - R3D.applist_padding_right) / minCellWidth);
		mCellCountY = (int) ((height - R3D.applist_padding_bottom
				- R3D.applist_padding_top - R3D.bottom_bar_height) / minCellHeight);
		gridPool = new GridPool(3, width, height, R3D.bottom_bar_height,
				mCellCountX, mCellCountY);
	}

	public void setPhotos(ArrayList<PhotoItem> photos) {
		this.photos = photos;
	}

	@Override
	public int getDataCount() {
		if (photos == null)
			return 0;
		else
			return photos.size();
	}

	@Override
	public View3D addView(int i, ViewGroup3D vg) {
		if (photos == null)
			return null;
		GridView3D layout = (GridView3D) vg;
		PhotoItem photo = photos.get(i);
		View3D view = photo.obtainView();
		view.setSize(layout.getCellWidth(), layout.getCellHeight()
				- R3D.photo_padding);
		view.setRotationZ(0);
		layout.addItem(view);
		return view;
	}

	public void setPhotoBucketView(PhotoBucketView photoBucketView) {
		this.photoBucketView = photoBucketView;
	}

	public PhotoBucketView getPhotoBucketView() {
		return this.photoBucketView;
	}
}
