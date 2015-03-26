package com.iLoong.launcher.media;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.desktop.iLoongLauncher;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.provider.MediaStore.Video;
import com.iLoong.launcher.Desktop3D.Log;


public class MediaCache
{
	
	public static final String tag = "MediaCache";
	public static final Object lock = new Object();
	public static final String[] PROJECTION_IMAGES = new String[]{ Images.ImageColumns._ID , Images.ImageColumns.TITLE ,
			// Images.ImageColumns.MIME_TYPE,
			Images.ImageColumns.DATA ,
			Images.ImageColumns.BUCKET_ID ,
			Images.ImageColumns.BUCKET_DISPLAY_NAME };
	public static final String[] PROJECTION_VIDEOS = new String[]{ MediaStore.Video.Media.DATA , MediaStore.Video.Media._ID , MediaStore.Video.Media.TITLE ,
	// MediaStore.Video.Media.MIME_TYPE
	};
	public static final String[] PROJECTION_AUDIOS = new String[]{ MediaStore.Audio.Media._ID , MediaStore.Audio.Media.TITLE ,
			// MediaStore.Audio.Media.DISPLAY_NAME,
			// MediaStore.Audio.Media.DURATION,
			// MediaStore.Audio.Media.MIME_TYPE,
			MediaStore.Audio.Media.SIZE ,
			MediaStore.Audio.Media.DATA ,
			MediaStore.Audio.Media.ALBUM ,
			MediaStore.Audio.Media.ARTIST ,
			MediaStore.Audio.Media.ALBUM_ID };
	public static final int OBSERVER_TYPE_AUDIO = 0;
	public static final int OBSERVER_TYPE_VIDEO = 1;
	public static final int OBSERVER_TYPE_PHOTO = 2;
	public final static int SYNC_ALL = 0x111;
	public final static int SYNC_AUDIO = 0x001;
	public final static int SYNC_VIDEO = 0x010;
	public final static int SYNC_PHOTO = 0x100;
	private Context context;
	private Cursor cursor;
	public ArrayList<PhotoItem> photos;
	public HashMap<Integer , PhotoBucket> photoBuckets;
	public ArrayList<Integer> photoBucketIds;
	public ArrayList<VideoItem> videos;// zqh
	public ArrayList<AudioItem> audios;
	public HashMap<String , AudioAlbum> audioAlbums;
	public ArrayList<String> audioAlbumIds;
	public HashMap<String , AudioAlbum> audioArtists;
	public ArrayList<String> audioArtistIds;
	public HashMap<String , AudioAlbum> audioFolders;
	public ArrayList<String> audioFolderIds;
	public ArrayList<PhotoItem> photosCache;
	public HashMap<Integer , PhotoBucket> photoBucketsCache;
	public ArrayList<Integer> photoBucketIdsCache;
	public ArrayList<VideoItem> videosCache;// zqh
	public ArrayList<AudioItem> audiosCache;
	public HashMap<String , AudioAlbum> audioAlbumsCache;
	public ArrayList<String> audioAlbumIdsCache;
	public HashMap<String , AudioAlbum> audioArtistsCache;
	public ArrayList<String> audioArtistIdsCache;
	public HashMap<String , AudioAlbum> audioFoldersCache;
	public ArrayList<String> audioFolderIdsCache;
	private static MediaCache mediaCache;
	private ArrayList<MediaDataObserver> observers;
	public boolean init = false;
	private int taskNum = 0;
	private int taskType = 0x000;
	private MediaObserver audioObserver;
	private MediaObserver photoObserver;
	private MediaObserver videoObserver;
	private boolean ignoreMediaChange = false;
	private ArrayList<File> deleteFiles;
	private int deleteTaskNum = 0;
	
	public static MediaCache getInstance()
	{
		if( mediaCache == null )
		{
			synchronized( MediaCache.class )
			{
				if( mediaCache == null )
					mediaCache = new MediaCache();
			}
		}
		return mediaCache;
	}
	
	private MediaCache()
	{
		init = false;
		taskNum = 0;
		taskType = 0x000;
		// ignoreMediaChange = false;
		context = iLoongLauncher.getInstance();
		photos = new ArrayList<PhotoItem>();
		photoBuckets = new HashMap<Integer , PhotoBucket>();
		photoBucketIds = new ArrayList<Integer>();
		videos = new ArrayList<VideoItem>();// zqh
		audios = new ArrayList<AudioItem>();
		audioAlbums = new HashMap<String , AudioAlbum>();
		audioArtists = new HashMap<String , AudioAlbum>();
		audioFolders = new HashMap<String , AudioAlbum>();
		audioAlbumIds = new ArrayList<String>();
		audioArtistIds = new ArrayList<String>();
		audioFolderIds = new ArrayList<String>();
		photosCache = new ArrayList<PhotoItem>();
		photoBucketsCache = new HashMap<Integer , PhotoBucket>();
		photoBucketIdsCache = new ArrayList<Integer>();
		videosCache = new ArrayList<VideoItem>();// zqh
		audiosCache = new ArrayList<AudioItem>();
		audioAlbumsCache = new HashMap<String , AudioAlbum>();
		audioArtistsCache = new HashMap<String , AudioAlbum>();
		audioFoldersCache = new HashMap<String , AudioAlbum>();
		audioAlbumIdsCache = new ArrayList<String>();
		audioArtistIdsCache = new ArrayList<String>();
		audioFolderIdsCache = new ArrayList<String>();
		observers = new ArrayList<MediaDataObserver>();
		audioObserver = new MediaObserver( SYNC_AUDIO );
		photoObserver = new MediaObserver( SYNC_PHOTO );
		videoObserver = new MediaObserver( SYNC_VIDEO );
		context.getContentResolver().registerContentObserver( MediaStore.Video.Media.EXTERNAL_CONTENT_URI , true , videoObserver );
		context.getContentResolver().registerContentObserver( Images.Media.EXTERNAL_CONTENT_URI , true , photoObserver );
		context.getContentResolver().registerContentObserver( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI , true , audioObserver );
	}
	
	public synchronized void attach(
			MediaDataObserver observer )
	{
		observers.add( observer );
	}
	
	public synchronized void attach(
			int index ,
			MediaDataObserver observer )
	{
		observers.add( index , observer );
	}
	
	public synchronized void detach(
			MediaDataObserver observer )
	{
	}
	
	public PhotoBucket getPhotoBucket(
			Integer bucketId )
	{
		return photoBucketsCache.get( bucketId );
	}
	
	public synchronized void notifyObservers(
			int type )
	{
		for( MediaDataObserver observer : observers )
		{
			observer.update( type );
		}
	}
	
	public void initData()
	{
		if( init )
			return;
		init = true;
		syncData( SYNC_ALL );
	}
	
	public void syncData(
			int syncType )
	{
		// Log.e("media", "syncData");
		if( !init )
			return;
		if( deleteTaskNum > 0 )
			return;
		taskType |= syncType;
		taskNum++;
		Task task = new Task();
		task.type = syncType;
		task.start();
	}
	
	class MediaObserver extends ContentObserver
	{
		
		private int type = SYNC_ALL;
		
		public MediaObserver(
				int syncType )
		{
			super( null );
			type = syncType;
		}
		
		public void onChange(
				boolean selfChange )
		{
			// Log.e("media", "onChange:"+selfChange);
			if( !ignoreMediaChange )
			{
				syncData( type );
			}
		}
	}
	
	class Task extends Thread
	{
		
		public int type;
		
		@Override
		public void run()
		{
			synchronized( lock )
			{
				taskNum--;
				if( taskNum > 0 )
					return;
				if( ( taskType & SYNC_VIDEO ) == SYNC_VIDEO )
				{
					taskType -= SYNC_VIDEO;
					syncVideoData();
				}
				if( taskNum > 0 )
					return;
				if( ( taskType & SYNC_PHOTO ) == SYNC_PHOTO )
				{
					taskType -= SYNC_PHOTO;
					syncPhotoData();
				}
				if( taskNum > 0 )
					return;
				if( ( taskType & SYNC_AUDIO ) == SYNC_AUDIO )
				{
					taskType -= SYNC_AUDIO;
					syncAudioData();
				}
			}
			super.run();
		}
		
		private void syncVideoData()
		{
			// Log.v("resVideo", "syncVideoData");
			videosCache.clear();
			StringBuilder where = new StringBuilder();
			where.append( MediaStore.Video.Media._ID + " != ''" );
			where.append( " AND " + MediaStore.Video.Media.MIME_TYPE + " NOT LIKE 'audio%'" );
			cursor = context.getContentResolver().query( MediaStore.Video.Media.EXTERNAL_CONTENT_URI , PROJECTION_VIDEOS , where.toString() , null , null );
			if( cursor == null )
				return;
			int idIndex = cursor.getColumnIndexOrThrow( MediaStore.Video.Media._ID );
			int titleIndex = cursor.getColumnIndexOrThrow( MediaStore.Video.Media.TITLE );
			int dataIndex = cursor.getColumnIndexOrThrow( MediaStore.Video.Media.DATA );
			if( !cursor.moveToFirst() )
				return;
			do
			{
				if( taskNum > 0 && ( taskType & SYNC_VIDEO ) == SYNC_VIDEO )
					return;
				int id = cursor.getInt( idIndex ); // id
				String data = cursor.getString( dataIndex );// 路径
				String title = cursor.getString( titleIndex );// 标题
				if( data == null )
					continue;
				if( !new File( data ).exists() )
					continue;
				VideoItem video = new VideoItem();
				video.data = data;
				video.title = title;
				video.id = id;
				videosCache.add( video );
				video.mediaCache = MediaCache.this;
			}
			while( cursor.moveToNext() );
			cursor.close();
			if( taskNum > 0 && ( taskType & SYNC_VIDEO ) == SYNC_VIDEO )
				return;
			iLoongLauncher.getInstance().postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					if( deleteTaskNum > 0 )
						return;
					videos.clear();
					videos.addAll( videosCache );
					notifyObservers( OBSERVER_TYPE_VIDEO );
				}
			} );
		}
		
		private void syncPhotoData()
		{
			// Log.v("resVideo", "syncPhotoData");
			photosCache.clear();
			photoBucketsCache.clear();
			photoBucketIdsCache.clear();
			cursor = context.getContentResolver().query( Images.Media.EXTERNAL_CONTENT_URI , PROJECTION_IMAGES , null , null , null );
			if( cursor == null )
				return;
			int idIndex = cursor.getColumnIndexOrThrow( Images.ImageColumns._ID );
			int dataIndex = cursor.getColumnIndexOrThrow( Images.ImageColumns.DATA );
			// int displayIndex =
			// cursor.getColumnIndexOrThrow(Images.ImageColumns.DISPLAY_NAME);
			int titleIndex = cursor.getColumnIndexOrThrow( Images.ImageColumns.TITLE );
			int bucketDisplayIndex = cursor.getColumnIndexOrThrow( Images.ImageColumns.BUCKET_DISPLAY_NAME );
			int bucketIdIndex = cursor.getColumnIndexOrThrow( Images.ImageColumns.BUCKET_ID );
			// if (!cursor.moveToFirst())
			// return;
			if( cursor.moveToFirst() )
			{
				do
				{
					if( taskNum > 0 && ( taskType & SYNC_PHOTO ) == SYNC_PHOTO )
						return;
					int id = cursor.getInt( idIndex );
					String data = cursor.getString( dataIndex );
					if( data == null )
						continue;
					if( !new File( data ).exists() )
						continue;
					// String displayName = cursor.getString(displayIndex);
					String title = cursor.getString( titleIndex );
					String bucketDisplayName = cursor.getString( bucketDisplayIndex );
					int bucketId = cursor.getInt( bucketIdIndex );
					PhotoItem photo = new PhotoItem();
					photo.data = data;
					photo.bucketDisplayName = bucketDisplayName;
					photo.title = title;
					photo.bucketId = bucketId;
					photo.id = id;
					photosCache.add( photo );
					PhotoBucket bucket = photoBucketsCache.get( bucketId );
					if( bucket == null )
					{
						bucket = new PhotoBucket();
						bucket.bucketId = bucketId;
						bucket.mediaCache = MediaCache.this;
						bucket.title = bucketDisplayName;
						photoBucketsCache.put( bucketId , bucket );
						photoBucketIdsCache.add( bucketId );
					}
					bucket.photos.add( photo );
					photo.bucket = bucket;
					photo.mediaCache = MediaCache.this;
					// Log.d(tag,
					// "data:"+data+",display:"+bucketDisplayName+",title:"+title);
				}
				while( cursor.moveToNext() );
			}
			cursor.close();
			if( taskNum > 0 && ( taskType & SYNC_PHOTO ) == SYNC_PHOTO )
				return;
			iLoongLauncher.getInstance().postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					if( deleteTaskNum > 0 )
						return;
					photos.clear();
					photos.addAll( photosCache );
					photoBuckets.clear();
					photoBuckets.putAll( photoBucketsCache );
					photoBucketIds.clear();
					photoBucketIds.addAll( photoBucketIdsCache );
					notifyObservers( OBSERVER_TYPE_PHOTO );
				}
			} );
		}
		
		private void syncAudioData()
		{
			// Log.v("resVideo", "syncAudioData");
			audiosCache.clear();
			audioAlbumsCache.clear();
			audioArtistsCache.clear();
			audioFoldersCache.clear();
			audioAlbumIdsCache.clear();
			audioArtistIdsCache.clear();
			audioFolderIdsCache.clear();
			Cursor cursor = null;
			String mAudioSortOrder = MediaStore.Audio.Media._ID;
			StringBuilder where = new StringBuilder();
			where.append( MediaStore.Audio.Media._ID + " != ''" );
			where.append( " AND " + MediaStore.Audio.Media.MIME_TYPE + " NOT LIKE 'video%'" );
			cursor = context.getContentResolver().query( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI , PROJECTION_AUDIOS , where.toString() , null , mAudioSortOrder );
			if( cursor != null )
			{
				if( !cursor.moveToFirst() )
					return;
				AudioAlbum defaultAlbum = null;
				int audioIdIdx = cursor.getColumnIndexOrThrow( MediaStore.Audio.Media._ID );
				// int audioMimetypeIdx =
				// cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE);
				// int audioDurationIdx =
				// cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
				int audioSizeIdx = cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.SIZE );
				int audioDataIdx = cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.DATA );
				int audioArtistIdx = cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST );
				int albumIdIdx = cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ID );
				int albumNameIdx = cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM );
				int audioTitleIdx = cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE );
				do
				{
					if( taskNum > 0 && ( taskType & SYNC_AUDIO ) == SYNC_AUDIO )
						return;
					AudioItem audio = new AudioItem();
					audio.id = cursor.getInt( audioIdIdx );
					audio.data = cursor.getString( audioDataIdx );
					if( audio.data == null )
						continue;
					if( !new File( audio.data ).exists() )
						continue;
					audio.parseFolder();
					// audio.duration = cursor.getInt(audioDurationIdx);
					audio.size = cursor.getInt( audioSizeIdx );
					// audio.mimetype = cursor.getString(audioMimetypeIdx);
					audio.artist = cursor.getString( audioArtistIdx );
					audio.title = cursor.getString( audioTitleIdx );
					audio.albumId = cursor.getInt( albumIdIdx );
					String albumDisplayName = cursor.getString( albumNameIdx );
					audio.mediaCache = MediaCache.this;
					audiosCache.add( audio );
					audio.setAlbumDisplayName( albumDisplayName );
					// Log.e("media", "audio:"+audio.title);
					if( audio.albumId >= 0 )
					{
						String thumbnailPath = "";
						String[] projection = new String[]{ MediaStore.Audio.Albums.ALBUM_ART };
						Cursor ret2 = context.getContentResolver().query(
								Uri.withAppendedPath( MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI , Long.valueOf( audio.albumId ).toString() ) ,
								projection ,
								null ,
								null ,
								null );
						if( ret2.getCount() > 0 && ret2.getColumnCount() > 0 )
						{
							ret2.moveToNext();
							thumbnailPath = ret2.getString( ret2.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
							if( thumbnailPath != null )
							{
								audio.thumbnailPath = thumbnailPath;
							}
						}
						ret2.close();
						AudioAlbum album = audioAlbumsCache.get( audio.albumId + "" );
						if( album == null )
						{
							album = new AudioAlbum();
							album.albumId = audio.albumId + "";
							album.title = audio.getAlbumDisplayName();
							album.thumbnailPath = audio.thumbnailPath;
							album.mediaCache = MediaCache.this;
							audioAlbumsCache.put( album.albumId , album );
							audioAlbumIdsCache.add( album.albumId );
						}
						album.audios.add( audio );
						audio.album = album;
					}
					else
					{
						if( defaultAlbum == null )
						{
							defaultAlbum = new AudioAlbum();
							defaultAlbum.albumId = -1 + "";
							defaultAlbum.mediaCache = MediaCache.this;
							defaultAlbum.title = context.getResources().getString( RR.string.default_album_name );
							audioAlbumsCache.put( defaultAlbum.albumId , defaultAlbum );
							audioAlbumIdsCache.add( defaultAlbum.albumId );
						}
						defaultAlbum.audios.add( audio );
						audio.album = defaultAlbum;
					}
					AudioAlbum folder = audioFoldersCache.get( audio.folder );
					if( folder == null )
					{
						folder = new AudioAlbum();
						folder.type = AudioAlbum.TYPE_FOLDER;
						folder.folder = audio.folder;
						folder.mediaCache = MediaCache.this;
						audioFoldersCache.put( folder.folder , folder );
						audioFolderIdsCache.add( folder.folder );
					}
					folder.audios.add( audio );
					audio.folderAlbum = folder;
					AudioAlbum artist = audioArtistsCache.get( audio.artist );
					if( artist == null )
					{
						artist = new AudioAlbum();
						artist.type = AudioAlbum.TYPE_ARTIST;
						artist.artist = audio.artist;
						artist.mediaCache = MediaCache.this;
						audioArtistsCache.put( artist.artist , artist );
						audioArtistIdsCache.add( artist.artist );
					}
					artist.audios.add( audio );
					audio.artistAlbum = artist;
				}
				while( cursor.moveToNext() );
				cursor.close();
				if( defaultAlbum != null )
				{
					if( !audioAlbumsCache.containsKey( defaultAlbum.albumId ) )
						audioAlbumsCache.put( defaultAlbum.albumId , defaultAlbum );
				}
				else
					audioAlbumsCache.remove( -1 + "" );
			}
			if( taskNum > 0 && ( taskType & SYNC_AUDIO ) == SYNC_AUDIO )
				return;
			iLoongLauncher.getInstance().postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					if( deleteTaskNum > 0 )
						return;
					audios.clear();
					audios.addAll( audiosCache );
					audioAlbums.clear();
					audioAlbums.putAll( audioAlbumsCache );
					audioAlbumIds.clear();
					audioAlbumIds.addAll( audioAlbumIdsCache );
					audioArtists.clear();
					audioArtists.putAll( audioArtistsCache );
					audioArtistIds.clear();
					audioArtistIds.addAll( audioArtistIdsCache );
					audioFolders.clear();
					audioFolders.putAll( audioFoldersCache );
					audioFolderIds.clear();
					audioFolderIds.addAll( audioFolderIdsCache );
					notifyObservers( OBSERVER_TYPE_AUDIO );
				}
			} );
		}
	}
	
	public ArrayList<File> startDelete()
	{
		deleteTaskNum++;
		ArrayList<File> deleteFiles = new ArrayList<File>();
		this.deleteFiles = deleteFiles;
		return deleteFiles;
	}
	
	public void exeDelete(
			final ArrayList<File> deleteFiles )
	{
		new Thread() {
			
			@Override
			public void run()
			{
				for( int i = 0 ; i < deleteFiles.size() ; i++ )
				{
					if( deleteFiles.get( i ).delete() )
						SendMsgToAndroid.sendToastMsg( deleteFiles.get( i ).getAbsolutePath() + " " + iLoongLauncher.getInstance().getResources().getString( RR.string.media_delete_ok ) + "!" );
				}
				super.run();
				deleteTaskNum--;
				if( deleteTaskNum == 0 )
				{
					syncData( SYNC_ALL );
					ignoreMediaChange = false;
				}
			}
		}.start();
	}
	
	public boolean deleteVideo(
			VideoItem video )
	{
		videos.remove( video );
		// ignoreMediaChange = true;
		int num = context.getContentResolver().delete( MediaStore.Video.Media.EXTERNAL_CONTENT_URI , MediaStore.Video.Media._ID + "=" + Long.toString( video.id ) , null );
		// if(num <= 0)ignoreMediaChange = false;
		File file = new File( video.data );
		if( file.exists() )
		{
			deleteFiles.add( file );
		}
		return true;
	}
	
	public boolean deletePhoto(
			PhotoItem photo )
	{
		photos.remove( photo );
		photo.bucket.photos.remove( photo );
		ignoreMediaChange = true;
		int num = context.getContentResolver().delete( Images.Media.EXTERNAL_CONTENT_URI , Images.ImageColumns._ID + "=" + Long.toString( photo.id ) , null );
		//
		// if(num <= 0)ignoreMediaChange = false;
		File file = new File( photo.data );
		if( file.exists() )
		{
			deleteFiles.add( file );
			// file.delete();
			// SendMsgToAndroid.sendCircleToastMsg(iLoongLauncher.getInstance().getResources().getString(RR.string.media_delete_ok));
		}
		if( photo.bucket.photos.size() == 0 )
		{
			photo.bucket.onDelete();
		}
		return true;
	}
	
	public boolean deletePhotoBucket(
			PhotoBucket bucket )
	{
		photoBuckets.remove( bucket.bucketId );
		photoBucketIds.remove( (Integer)bucket.bucketId );
		return true;
	}
	
	public boolean deleteAudio(
			AudioItem audio )
	{
		audios.remove( audio );
		if( audio.album != null )
			audio.album.audios.remove( audio );
		if( audio.artistAlbum != null )
			audio.artistAlbum.audios.remove( audio );
		if( audio.folderAlbum != null )
			audio.folderAlbum.audios.remove( audio );
		// ignoreMediaChange = true;
		int num = context.getContentResolver().delete( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI , MediaStore.Audio.AudioColumns._ID + "=" + Long.toString( audio.id ) , null );
		// if(num <= 0)ignoreMediaChange = false;
		File file = new File( audio.data );
		if( file.exists() )
		{
			deleteFiles.add( file );
			// file.delete();
			// SendMsgToAndroid.sendCircleToastMsg(iLoongLauncher.getInstance().getResources().getString(RR.string.media_delete_ok));
		}
		// xiatian add start //fix the bug which some as 0001732 in audio
		if( audio.album != null )
		{
			if( audio.album.audios.size() == 0 )
				audio.album.onDelete();
		}
		if( audio.artistAlbum != null )
		{
			if( audio.artistAlbum.audios.size() == 0 )
				audio.artistAlbum.onDelete();
		}
		if( audio.folderAlbum != null )
		{
			if( audio.folderAlbum.audios.size() == 0 )
				audio.folderAlbum.onDelete();
		}
		// xiatian add end
		return true;
	}
	
	public boolean deleteAudioAlbum(
			AudioAlbum album )
	{
		switch( album.type )
		{
			case AudioAlbum.TYPE_ALBUM:
				audioAlbums.remove( album.albumId );
				audioAlbumIds.remove( album.albumId );
				break;
			case AudioAlbum.TYPE_ARTIST:
				audioArtists.remove( album.artist );
				audioArtistIds.remove( album.artist );
				break;
			case AudioAlbum.TYPE_FOLDER:
				audioFolders.remove( album.folder );
				audioFolderIds.remove( album.folder );
				break;
		}
		return true;
	}
	
	public interface MediaDataObserver
	{
		
		public void update(
				int type );
	}
}
