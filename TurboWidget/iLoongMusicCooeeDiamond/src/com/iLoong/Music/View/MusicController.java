package com.iLoong.Music.View;


import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.KeyEvent;

import com.iLoong.Music.R;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class MusicController
{
	
	public static boolean isPlaying = false;
	public String SERVICECMD = "com.android.music.musicservicecommand";
	public String CMDNAME = "command";
	public String CMDTOGGLEPAUSE = "togglepause";
	public String CMDSTOP = "stop";
	public String CMDPAUSE = "pause";
	public String CMDPREVIOUS = "previous";
	public String CMDNEXT = "next";
	public String TOGGLEPAUSE_ACTION = "com.android.music.musicservicecommand.togglepause";
	public String PAUSE_ACTION = "com.android.music.musicservicecommand.pause";
	public String PREVIOUS_ACTION = "com.android.music.musicservicecommand.previous";
	public String NEXT_ACTION = "com.android.music.musicservicecommand.next";
	public String META_CHANGED = "com.android.music.metachanged";
	public String QUEUE_CHANGED = "com.android.music.queuechanged";
	public String PLAY_COMPLETED = "com.android.music.playbackcomplete";
	public String PLAY_STATE_CHANGED = "com.android.music.playstatechanged";
	public String PLAY_STATE_CHANGED_HTC_G11 = "com.htc.music.playstatechanged";
	private MusicControlIntentReceiver mIntentReceiver = new MusicControlIntentReceiver();
	private Context ct;
	private MainAppContext appContext;
	private boolean isServiceStarted = false;
	private ArrayList<MusicListener> musicListeners = new ArrayList<MusicListener>();
	HandlerThread sWorkerThread = null;
	Handler sWorker = null;
	
	public void addMusicListener(
			MusicListener listener )
	{
		if( !musicListeners.contains( listener ) )
		{
			musicListeners.add( listener );
		}
	}
	
	public MusicController(
			MainAppContext appContext )
	{
		this.appContext = appContext;
		Resources res = appContext.mWidgetContext.getResources();
		SERVICECMD = res.getString( R.string.music_togglepause_action );
		TOGGLEPAUSE_ACTION = res.getString( R.string.music_togglepause_action );
		PREVIOUS_ACTION = res.getString( R.string.music_previous_action );
		PAUSE_ACTION = res.getString( R.string.music_pause_action );
		NEXT_ACTION = res.getString( R.string.music_next_action );
		META_CHANGED = res.getString( R.string.music_changed_metachanged );
		QUEUE_CHANGED = res.getString( R.string.music_changed_queuechanged );
		PLAY_COMPLETED = res.getString( R.string.music_changed_playbackcomplete );
		PLAY_STATE_CHANGED = res.getString( R.string.music_changed_playstatechanged );
		sWorkerThread = new HandlerThread( "photo-cache" );
		sWorkerThread.start();
		sWorker = new Handler( sWorkerThread.getLooper() );
	}
	
	public void startListening()
	{
		IntentFilter commandFilter = new IntentFilter();
		commandFilter.addAction( SERVICECMD );
		commandFilter.addAction( TOGGLEPAUSE_ACTION );
		commandFilter.addAction( PAUSE_ACTION );
		commandFilter.addAction( NEXT_ACTION );
		commandFilter.addAction( PREVIOUS_ACTION );
		commandFilter.addAction( META_CHANGED );
		commandFilter.addAction( QUEUE_CHANGED );
		commandFilter.addAction( PLAY_COMPLETED );
		commandFilter.addAction( PLAY_STATE_CHANGED );
		commandFilter.addAction( PLAY_STATE_CHANGED_HTC_G11 );
		commandFilter.addAction( AlbumFrontView.defaultPkgName + ".playstatechanged" );
		ct = appContext.mContainerContext;
		ct.registerReceiver( mIntentReceiver , commandFilter );
	}
	
	public void nextMusic()
	{
		//		Intent intent = new Intent(NEXT_ACTION);
		//		intent.putExtra("command", CMDNEXT);
		//		ct.sendBroadcast(intent);
		Intent intent = new Intent();
		int keyCode;
		KeyEvent keyEvent;
		keyCode = KeyEvent.KEYCODE_MEDIA_NEXT;
		intent.setAction( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_DOWN , keyCode );
		intent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		intent.setPackage( AlbumFrontView.defaultPkgName );
		iLoongLauncher.getInstance().sendOrderedBroadcast( intent , null );
		intent = new Intent( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_UP , keyCode );
		intent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		intent.setPackage( AlbumFrontView.defaultPkgName );
		iLoongLauncher.getInstance().sendOrderedBroadcast( intent , null );
		isPlaying = true;
	}
	
	public synchronized void prevMusic()
	{
		//		Intent intent = new Intent( PREVIOUS_ACTION );
		//		intent.putExtra( "command" , CMDPREVIOUS );
		//		ct.sendBroadcast( intent );
		Intent intent = new Intent();
		int keyCode;
		KeyEvent keyEvent;
		keyCode = KeyEvent.KEYCODE_MEDIA_PREVIOUS;
		Intent meidaButtonIntent = new Intent( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_DOWN , keyCode );
		meidaButtonIntent.setPackage( AlbumFrontView.defaultPkgName );
		meidaButtonIntent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		iLoongLauncher.getInstance().sendOrderedBroadcast( meidaButtonIntent , null );
		meidaButtonIntent = new Intent( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_UP , keyCode );
		meidaButtonIntent.setPackage( AlbumFrontView.defaultPkgName );
		meidaButtonIntent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		iLoongLauncher.getInstance().sendOrderedBroadcast( meidaButtonIntent , null );
		isPlaying = true;
	}
	
	public void pauseMusic()
	{
		//		Intent intent = new Intent( PAUSE_ACTION );
		//		intent.putExtra( "command" , CMDPAUSE );
		//		ct.sendBroadcast( intent );
		Intent intent = new Intent();
		int keyCode;
		KeyEvent keyEvent;
		keyCode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
		intent.setAction( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_DOWN , keyCode );
		intent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		intent.setPackage( AlbumFrontView.defaultPkgName );
		iLoongLauncher.getInstance().sendOrderedBroadcast( intent , null );
		intent = new Intent( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_UP , keyCode );
		intent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		intent.setPackage( AlbumFrontView.defaultPkgName );
		iLoongLauncher.getInstance().sendOrderedBroadcast( intent , null );
		isPlaying = false;
	}
	
	public void playMusic()
	{
		//		boolean startSuccess = false;
		//		try
		//		{
		//			startSuccess = startMusicPlayer();
		//		}
		//		catch( Exception e )
		//		{
		//			startSuccess = false;
		//		}
		//		if( startSuccess )
		//		{
		//			// Intent intent = new Intent(TOGGLEPAUSE_ACTION);
		//			// intent.putExtra("command", CMDTOGGLEPAUSE);
		//			// ct.sendBroadcast(intent);
		//			isPlaying = true;
		//		}
		//		else
		//		{
		//			Intent intent = new Intent( TOGGLEPAUSE_ACTION );
		//			intent.putExtra( "command" , CMDTOGGLEPAUSE );
		//			ct.sendBroadcast( intent );
		//			isPlaying = true;
		//		}
		Intent intent = new Intent();
		int keyCode;
		KeyEvent keyEvent;
		keyCode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
		intent.setAction( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_DOWN , keyCode );
		intent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		intent.setPackage( AlbumFrontView.defaultPkgName );
		iLoongLauncher.getInstance().sendOrderedBroadcast( intent , null );
		intent = new Intent( Intent.ACTION_MEDIA_BUTTON );
		keyEvent = new KeyEvent( KeyEvent.ACTION_UP , keyCode );
		intent.putExtra( Intent.EXTRA_KEY_EVENT , keyEvent );
		intent.setPackage( AlbumFrontView.defaultPkgName );
		iLoongLauncher.getInstance().sendOrderedBroadcast( intent , null );
		isPlaying = true;
	}
	
	public synchronized void onReceiveMetaChanged(
			MusicData data )
	{
		Log.e( "MusicController	" , "onReceiveMetaChanged music:" + data + "musicListeners.size：" + musicListeners.size() );
		isServiceStarted = true;
		for( int i = 0 ; i < musicListeners.size() ; i++ )
		{
			MusicListener listener = musicListeners.get( i );
			Log.e( "MusicController" , "onReceiveMetaChanged listener:" + listener );
			listener.onMetaChanged( data );
		}
	}
	
	public synchronized void onReceivePause(
			MusicData data )
	{
		isServiceStarted = true;
		isPlaying = false;
		for( int i = 0 ; i < musicListeners.size() ; i++ )
		{
			MusicListener listener = musicListeners.get( i );
			Log.e( "MusicController" , "onReceivePause listener:" + listener );
			listener.onPause( data );
		}
	}
	
	public synchronized void onReceiveNext(
			MusicData data )
	{
		isServiceStarted = true;
		for( int i = 0 ; i < musicListeners.size() ; i++ )
		{
			MusicListener listener = musicListeners.get( i );
			Log.e( "MusicController" , "onReceiveNext listener:" + listener );
			listener.onNext( data );
		}
	}
	
	public synchronized void onReceivePrevious(
			MusicData data )
	{
		isServiceStarted = true;
		for( int i = 0 ; i < musicListeners.size() ; i++ )
		{
			MusicListener listener = musicListeners.get( i );
			Log.e( "MusicController" , "onReceivePrevious listener:" + listener );
			listener.onPrevious( data );
		}
	}
	
	public synchronized void onReceivePlay(
			MusicData data )
	{
		isServiceStarted = true;
		isPlaying = true;
		for( int i = 0 ; i < musicListeners.size() ; i++ )
		{
			MusicListener listener = musicListeners.get( i );
			Log.e( "MusicController" , "onReceivePlay listener:" + listener );
			listener.onPlay( data );
		}
	}
	
	public static boolean isPlaying()
	{
		return isPlaying;
	}
	
	public boolean startMusicPlayer()
	{
		boolean success = false;
		PackageManager packageManager = appContext.mContainerContext.getPackageManager();
		if( !isMusicServiceStarted() )
		{
			String[] services = appContext.mWidgetContext.getResources().getStringArray( R.array.music_services );
			for( int i = 0 ; i < services.length ; i++ )
			{
				String serviceStr = services[i];
				String[] serviceComponentarray = serviceStr.split( ";" );
				if( serviceComponentarray.length >= 2 )
				{
					Intent intent = new Intent();
					intent.setClassName( serviceComponentarray[0] , serviceComponentarray[1] );
					List<ResolveInfo> resoveInfos = packageManager.queryIntentServices( intent , 0 );
					if( resoveInfos.size() > 0 )
					{
						intent.putExtra( CMDNAME , CMDTOGGLEPAUSE );
						appContext.mWidgetContext.startService( intent );
						isServiceStarted = true;
						success = true;
						break;
					}
				}
			}
		}
		else
		{
			isServiceStarted = true;
			success = true;
		}
		return success;
	}
	
	private boolean isMusicServiceStarted()
	{
		String[] services = appContext.mWidgetContext.getResources().getStringArray( R.array.music_services );
		ActivityManager manager = (ActivityManager)appContext.mContainerContext.getSystemService( Context.ACTIVITY_SERVICE );
		String serviceStr = "";
		String servicePackageName = "";
		String serviceClassName = "";
		String[] array = new String[2];
		boolean started = false;
		out:
		for( RunningServiceInfo service : manager.getRunningServices( Integer.MAX_VALUE ) )
		{
			// Log.e("MusicController", "runningService: package:"
			// + service.service.getPackageName() + " class:"
			// + service.service.getClassName());
			for( int i = 0 ; i < services.length ; i++ )
			{
				serviceStr = services[i];
				if( serviceStr != null && serviceStr.length() > 0 )
				{
					array = serviceStr.split( ";" );
					if( array.length >= 2 )
					{
						servicePackageName = array[0];
						serviceClassName = array[1];
						if( servicePackageName.equals( service.service.getClassName() ) && serviceClassName.equals( service.service.getClassName() ) )
						{
							started = true;
							break out;
						}
					}
				}
			}
		}
		return started;
	}
	
	public class MusicControlIntentReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context context ,
				Intent intent )
		{
			MusicData data = new MusicData();
			// data.id = intent.getLongExtra("id", -1);
			// data.albumid = intent.getLongExtra("albumid", -1);
			// data.songId = intent.getLongExtra("songid", -1);
			// data.album = intent.getStringExtra("album");
			// data.album_artist = intent.getStringExtra("album_artist");
			// data.playing = intent.getBooleanExtra("playing", false);
			// data.track = intent.getStringExtra("track");
			// data.duration = intent.getLongExtra("duration", 3000);
			// data.position = intent.getLongExtra("position", 1000);
			// Log.e("test", "onReceive:" + data);
			String action = intent.getAction();
			String cmd = intent.getStringExtra( "command" );
			long paramId = intent.getLongExtra( "id" , -1 );
			Log.e( "MusicController" , "onReceive: action：" + action + " cmd:" + cmd + " paramId:" + paramId );
			if( paramId != -1 )
			{
				data = getMusicData( paramId );
			}
			if( data == null )
			{
				data = new MusicData();
			}
			data.playing = intent.getBooleanExtra( "playing" , false );
			if( action.equals( PLAY_STATE_CHANGED ) || action.equals( PLAY_STATE_CHANGED_HTC_G11 ) || action.equals( AlbumFrontView.defaultPkgName + ".playstatechanged" ) )
			{
				if( data.playing )
				{
					onReceivePlay( data );
				}
				else
				{
					onReceivePause( data );
				}
			}
			else if( action.equals( META_CHANGED ) )
			{
				onReceiveMetaChanged( data );
			}
			else if( CMDNEXT.equals( cmd ) || NEXT_ACTION.equals( action ) )
			{
				onReceiveNext( data );
			}
			else if( CMDPREVIOUS.equals( cmd ) || PREVIOUS_ACTION.equals( action ) )
			{
				onReceivePrevious( data );
			}
			else if( CMDTOGGLEPAUSE.equals( cmd ) || TOGGLEPAUSE_ACTION.equals( action ) )
			{
				if( data.playing )
				{
					onReceivePlay( data );
				}
				else
				{
					onReceivePause( data );
				}
			}
			else if( CMDPAUSE.equals( cmd ) || PAUSE_ACTION.equals( action ) )
			{
				//				if (data.playing) {
				//					onReceivePlay(data);
				//				} else {
				//					onReceivePause(data);
				//				}
			}
			else if( CMDSTOP.equals( cmd ) )
			{
				//				if (data.playing) {
				//					onReceivePlay(data);
				//				} else {
				//					onReceivePause(data);
				//				}
			}
		}
	};
	
	private boolean isExternalStorageAvailable()
	{
		boolean state = false;
		String extStorageState = Environment.getExternalStorageState();
		if( Environment.MEDIA_MOUNTED.equals( extStorageState ) )
		{
			state = true;
		}
		return state;
	}
	
	private MusicData getMusicData(
			long paramId )
	{
		ContentResolver contentResolver = ct.getContentResolver();
		Cursor cursor = null;
		String[] projection = new String[]{
				MediaStore.Audio.Media._ID ,
				MediaStore.Audio.Media.ARTIST ,
				MediaStore.Audio.Media.ARTIST_ID ,
				MediaStore.Audio.Media.TITLE ,
				MediaStore.Audio.Media.ALBUM ,
				MediaStore.Audio.Media.ALBUM_ID ,
				MediaStore.Audio.Media.TRACK };
		String where = null;
		String[] args = null;
		if( paramId != -1 )
		{
			where = " _id=? ";
			args = new String[1];
			args[0] = String.valueOf( paramId );
		}
		else
		{
			return null;
		}
		if( isExternalStorageAvailable() )
		{
			cursor = contentResolver.query( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI , projection , where , args , null );
		}
		else
		{
			cursor = contentResolver.query( MediaStore.Audio.Media.INTERNAL_CONTENT_URI , projection , where , args , null );
		}
		MusicData data = new MusicData();
		if( cursor != null )
		{
			if( cursor.moveToFirst() )
			{
				int titleIndex = cursor.getColumnIndex( MediaStore.Audio.Media.TITLE );
				int idIndex = cursor.getColumnIndex( MediaStore.Audio.Media._ID );
				int artistIndex = cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST );
				int albumIndex = cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM );
				int albumIdIndex = cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM_ID );
				int trackId = cursor.getColumnIndex( MediaStore.Audio.Media.TRACK );
				long id = cursor.getLong( idIndex );
				String track = null;
				long albumId = -1;
				String title = cursor.getString( titleIndex );
				if( trackId != -1 )
				{
					track = cursor.getString( trackId );
				}
				String artist = cursor.getString( artistIndex );
				String album = cursor.getString( albumIndex );
				if( albumIdIndex != -1 )
				{
					albumId = cursor.getLong( albumIdIndex );
				}
				data.id = id;
				data.songId = id;
				data.track = title;
				data.albumid = albumId;
				data.album = album;
				data.album_artist = artist;
				data.title = title;
				Log.e( "test" , "test data:" + data.toString() );
			}
			cursor.close();
		}
		return data;
	}
	
	private AudioAlbum getAudio(
			int audioId )
	{
		AudioAlbum audioAlbum = new AudioAlbum();
		ContentResolver contentResolver = ct.getContentResolver();
		Cursor cursor = null;
		String[] projection = new String[]{ MediaStore.Audio.Media._ID , MediaStore.Audio.Media.TITLE , MediaStore.Audio.Media.ALBUM , MediaStore.Audio.Media.ARTIST };
		cursor = contentResolver.query( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI , projection , null , null , null );
		if( cursor != null )
		{
			while( cursor.moveToNext() )
			{
				AudioItem item = new AudioItem();
				int titleIndex = cursor.getColumnIndex( MediaStore.Audio.Media.TITLE );
				int idIndex = cursor.getColumnIndex( MediaStore.Audio.Media._ID );
				int artistIndex = cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST );
				String title = cursor.getString( titleIndex );
				long id = cursor.getLong( idIndex );
				int album_id = cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM );
				String album = cursor.getString( album_id );
				String artist = cursor.getString( artistIndex );
				String subTitle = "";
				if( album != null && !album.isEmpty() && !album.equals( "<unknown>" ) )
				{
					subTitle += album;
				}
				if( artist != null && !artist.isEmpty() && !artist.equals( "<unknown>" ) )
				{
					subTitle += "-";
					subTitle += artist;
				}
				item.id = id;
				item.title = title;
				audioAlbum.albumId = album_id;
				audioAlbum.albumName = album;
				audioAlbum.artist = artist;
				audioAlbum.audios.add( item );
				// Log.v(TAG, " title:" + title + "album:" + album +
				// " artist:"
				// + artist + " subTitle:" + subTitle);
				// Uri personUri = ContentUris.withAppendedId(
				// MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
				// Intent intent = new Intent();
				// intent.setAction(Intent.ACTION_VIEW);
				// intent.setData(personUri);
			}
			cursor.close();
		}
		return audioAlbum;
	}
	
	public void togglePlay()
	{
		// TODO Auto-generated method stub
		if( isPlaying )
		{
			pauseMusic();
		}
		else
		{
			playMusic();
		}
	}
	
	public Bitmap getArtwork(
			Context context ,
			long song_id ,
			long album_id ,
			boolean allowdefault )
	{
		if( album_id < 0 )
		{
			// This is something that is not in the database, so get the album
			// art directly
			// from the file.
			if( song_id >= 0 )
			{
				Bitmap bm = getArtworkFromFile( context , song_id , -1 );
				if( bm != null )
				{
					return bm;
				}
			}
			if( allowdefault )
			{
				return getDefaultArtwork( context );
			}
			return null;
		}
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId( sArtworkUri , album_id );
		if( uri != null )
		{
			InputStream in = null;
			try
			{
				in = res.openInputStream( uri );
				return BitmapFactory.decodeStream( in , null , sBitmapOptions );
			}
			catch( FileNotFoundException ex )
			{
				// The album art thumbnail does not actually exist. Maybe the
				// user deleted it, or
				// maybe it never existed to begin with.
				Bitmap bm = getArtworkFromFile( context , song_id , album_id );
				if( bm != null )
				{
					if( bm.getConfig() == null )
					{
						bm = bm.copy( Bitmap.Config.RGB_565 , false );
						if( bm == null && allowdefault )
						{
							return getDefaultArtwork( context );
						}
					}
				}
				else if( allowdefault )
				{
					bm = getDefaultArtwork( context );
				}
				return bm;
			}
			finally
			{
				try
				{
					if( in != null )
					{
						in.close();
					}
				}
				catch( IOException ex )
				{
				}
			}
		}
		return null;
	}
	
	private Bitmap getArtworkFromFile(
			Context context ,
			long songid ,
			long albumid )
	{
		Bitmap bm = null;
		byte[] art = null;
		String path = null;
		if( albumid < 0 && songid < 0 )
		{
			throw new IllegalArgumentException( "Must specify an album or a song id" );
		}
		try
		{
			if( albumid < 0 )
			{
				Uri uri = Uri.parse( "content://media/external/audio/media/" + songid + "/albumart" );
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor( uri , "r" );
				if( pfd != null )
				{
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor( fd );
				}
			}
			else
			{
				Uri uri = ContentUris.withAppendedId( sArtworkUri , albumid );
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor( uri , "r" );
				if( pfd != null )
				{
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor( fd );
				}
			}
		}
		catch( FileNotFoundException ex )
		{
		}
		if( bm != null )
		{
			mCachedBit = bm;
		}
		return bm;
	}
	
	private Bitmap getDefaultArtwork(
			Context context )
	{
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		return WidgetThemeManager.getInstance().getBitmap( "default_album.png" );
		//		return BitmapFactory.decodeStream(context.getResources()
		//				.openRawResource(R.drawable.default_album), null, opts);
	}
	
	private static final Uri sArtworkUri = Uri.parse( "content://media/external/audio/albumart" );
	private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
	private static Bitmap mCachedBit = null;
	
	public void loadArtBitmap(
			MusicData music ,
			LoadBitmapCallback callback )
	{
		LoadArtTask r = new LoadArtTask();
		r.music = music;
		r.callback = callback;
		sWorker.post( r );
	}
	
	public class LoadArtTask implements Runnable
	{
		
		public MusicData music;
		public LoadBitmapCallback callback;
		
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			Bitmap bitmap = getArtwork( appContext.mContainerContext , music.songId , music.albumid , true );
			callback.loadCompleted( bitmap );
		}
	}
	
	public void postRunnable(
			Runnable runnable )
	{
		sWorker.post( runnable );
	}
	
	public void dispose()
	{
		if( mIntentReceiver != null )
		{
			ct.unregisterReceiver( mIntentReceiver );
		}
		if( musicListeners != null )
		{
			musicListeners.clear();
		}
		if( sWorkerThread != null )
		{
			sWorkerThread.interrupt();
			sWorkerThread = null;
		}
	}
}
