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

public class AudioAlbumAdapter extends MediaListAdapter implements MediaDataObserver{
	private final int observer_type = MediaCache.OBSERVER_TYPE_AUDIO;
	private HashMap<String,AudioAlbum> audioAlbums;
	private HashMap<String,AudioAlbum> audioArtists;
	private HashMap<String,AudioAlbum> audioFolders;
	private HashMap<String,AudioAlbum> currentAlbums;
	private ArrayList<String> currentAlbumIds;
	public int currentAlbumType = -1;
	private MediaList list;
	private float minCellWidth;
	private float minCellHeight;
	
	public AudioAlbumAdapter(MediaList list,float width,float height){
		this.list = list;
		minCellWidth = R3D.audio_width+R3D.audio_bottom_padding+R3D.audio_left_padding;
		minCellHeight = R3D.audio_height+Utils3D.getTitleHeight(R3D.photo_title_size, R3D.photo_title_line)+2*R3D.audio_bottom_padding;
		mCellCountX = (int)((width-R3D.applist_padding_left-R3D.applist_padding_right)/minCellWidth);
		mCellCountY = (int)((height-R3D.applist_padding_bottom-R3D.applist_padding_top)/minCellHeight);
		gridPool = new GridPool(3, width, height,0, mCellCountX, mCellCountY);
		
		this.audioAlbums = MediaCache.getInstance().audioAlbums;
		this.audioArtists = MediaCache.getInstance().audioArtists;
		this.audioFolders = MediaCache.getInstance().audioFolders;
		setCurrentAlbums(AudioAlbum.TYPE_ALBUM);
		MediaCache.getInstance().attach(this);
	}
	
	public boolean setCurrentAlbums(int type){
		if(currentAlbumType == type)return false;
		currentAlbumType = type;
		switch(currentAlbumType){
		case AudioAlbum.TYPE_ALBUM:
			currentAlbums = audioAlbums;
			currentAlbumIds = MediaCache.getInstance().audioAlbumIds;
			break;
		case AudioAlbum.TYPE_ARTIST:
			currentAlbums = audioArtists;
			currentAlbumIds = MediaCache.getInstance().audioArtistIds;
			break;
		case AudioAlbum.TYPE_FOLDER:
			currentAlbums = audioFolders;
			currentAlbumIds = MediaCache.getInstance().audioFolderIds;
			break;
		}
		return true;
	}

	@Override
	public int getDataCount() {
		if(currentAlbums == null)
			return 0;
		else return currentAlbums.size();
	}

	@Override
	public View3D addView(int i,ViewGroup3D vg) {
		GridView3D layout = (GridView3D)vg;
		AudioAlbum album = currentAlbums.get(currentAlbumIds.get(i));
		View3D view = album.obtainView();
		view.setSize(layout.getCellWidth(), layout.getCellHeight());
		layout.addItem(view);
		return view;
	}

	@Override
	public void update(int type) {
		if(type != observer_type)return;
		if(AppHost3D.currentContentType==AppHost3D.CONTENT_TYPE_AUDIO_ALBUM){
			list.syncPages();
		}
	}
	
}
