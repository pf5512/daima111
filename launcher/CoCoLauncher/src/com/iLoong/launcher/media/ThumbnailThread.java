package com.iLoong.launcher.media;

import java.io.File;
import java.util.ArrayList;

import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.desktop.iLoongLauncher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import com.iLoong.launcher.Desktop3D.Log;

public class ThumbnailThread {
	private final String tag = "ThumbnailThread";
	private static final int MICRO_THUMBNAIL_WIDTH = 96;
    private static final int MICRO_THUMBNAIL_HEIGHT = 96;
    private static final int DECODE_TIMEOUT = 500;
    
    public static final int VISIBLE = 0;
    public static final int CACHE = 1;
    
    private float minPhotoWidth;
    private float minPhotoHeight;
    private float minAudioWidth;
    private float minAudioHeight;
    private float minVideoWidth;
    private float minVideoHeight;
	public Object clientLock = new Object();
	public Object waitLock = new Object();
	public Object decodeLock = new Object();
	public Object timeoutLock = new Object();
	private static ThumbnailThread thumbnailThread;
	private Thread clientThread;
	private Thread decodeThread;
	private ArrayList<ThumbnailClient> clients;
	private boolean wait = false;
	private boolean destroy = false;
	private BitmapFactory.Options options;
	private boolean decoding = false;
	private ThumbnailClient curClient = null;
	
	public static ThumbnailThread getInstance(){
		if(thumbnailThread == null){
    		synchronized(ThumbnailThread.class){
    			if(thumbnailThread == null)thumbnailThread = new ThumbnailThread();
    		}
    	}
    	return thumbnailThread;
	}
	
	public ThumbnailThread(){
		options = new BitmapFactory.Options();  
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		clients = new ArrayList<ThumbnailClient>();
		clientThread = new Thread(clientRunnable);
		clientThread.start();
		clientThread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		decodeThread = new Thread(decodeRunnable);
		decodeThread.start();
		decodeThread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		minPhotoWidth = R3D.photo_width;
		minPhotoHeight = R3D.photo_height;
		minVideoWidth = R3D.video_width;
		minVideoHeight = R3D.video_height;
		minAudioWidth = R3D.audio_width;
		minAudioHeight = R3D.audio_height;
	}
	
	public void push(ThumbnailClient client,int pos){
		synchronized(waitLock){
			wait = true;
			synchronized(clientLock){
				if(pos == CACHE)clients.add(client);
				else if(pos == VISIBLE)clients.add(0, client);
				//Log.i("media", "add client");
				wait = false;
				clientLock.notify();
			}
		}
	}
	
	public void delete(ThumbnailClient client){
		synchronized(waitLock){
			wait = true;
			synchronized(clientLock){
				if(clients.remove(client)){
					//Log.i("media", "delete client");
				}
				wait = false;
				clientLock.notify();
			}
		}
	}
	
	public void clear(){
		synchronized(waitLock){
			wait = true;
			synchronized(clientLock){
				clients.clear();
				Log.i("media", "clear client");
				wait = false;
				clientLock.notify();
			}
		}
	}
	
	private Runnable clientRunnable = new Runnable(){

		@Override
		public void run() {
			
			while(!destroy){
				synchronized(clientLock){
					if(clients.size() == 0 || wait){
						try {
							clientLock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(clients.size()==0)continue;
					curClient = clients.get(0);
					if(curClient == null)continue;
				}
				long id = curClient.getThumbnailId();
				int type = curClient.getResType();
				synchronized(decodeLock){
					decodeLock.notifyAll();
				}
				if(type==AppHost3D.CONTENT_TYPE_PHOTO || type==AppHost3D.CONTENT_TYPE_VIDEO){
					synchronized(timeoutLock){
						try {
							if(curClient != null)timeoutLock.wait(DECODE_TIMEOUT);
							while(!decoding && curClient != null)timeoutLock.wait(DECODE_TIMEOUT);
							if(curClient != null){
								if(type==AppHost3D.CONTENT_TYPE_PHOTO)
									MediaStore.Images.Thumbnails.cancelThumbnailRequest(iLoongLauncher.getInstance().getContentResolver(), id);
								else if(type==AppHost3D.CONTENT_TYPE_VIDEO)
									MediaStore.Video.Thumbnails.cancelThumbnailRequest(iLoongLauncher.getInstance().getContentResolver(), id);
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				while(curClient != null){
					synchronized(decodeLock){
						try {
							if(curClient != null){
								decodeLock.wait(50);
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	};
	
	private Runnable decodeRunnable = new Runnable(){

		@Override
		public void run() {
			
			while(!destroy){
				synchronized(decodeLock){
					if(curClient == null){
						try {
							decodeLock.notifyAll();
							decoding = false;
							decodeLock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(curClient == null)continue;
						decoding = true;
					}
					long id = curClient.getThumbnailId();
					Bitmap thumbnail = null;
					if(curClient.getResType()==AppHost3D.CONTENT_TYPE_PHOTO){
						int kind = MediaStore.Images.Thumbnails.MICRO_KIND;
						if(MICRO_THUMBNAIL_WIDTH<minPhotoWidth||MICRO_THUMBNAIL_HEIGHT<minPhotoHeight){
							kind = MediaStore.Images.Thumbnails.MINI_KIND;
						}
						Log.i("thumbnail","decode:"+id);
						try {
							thumbnail= MediaStore.Images.Thumbnails.getThumbnail(iLoongLauncher.getInstance().getContentResolver(), id,
									kind, options);
						} catch (Exception e) {
							if(kind == MediaStore.Images.Thumbnails.MINI_KIND){
								thumbnail= MediaStore.Images.Thumbnails.getThumbnail(iLoongLauncher.getInstance().getContentResolver(), id,
										MediaStore.Images.Thumbnails.MICRO_KIND, options);
							}
							e.printStackTrace();
						}
						
						if(thumbnail != null){
							thumbnail = DecodeThumbHelper.resizeBmp(thumbnail, minPhotoWidth, minPhotoHeight);
							curClient.setThumbnailBmp(thumbnail);
						}
					}
					else if(curClient.getResType()==AppHost3D.CONTENT_TYPE_VIDEO){
						int kind = MediaStore.Video.Thumbnails.MICRO_KIND;
						if(MICRO_THUMBNAIL_WIDTH<minVideoWidth&&MICRO_THUMBNAIL_HEIGHT<minVideoHeight){
							kind = MediaStore.Video.Thumbnails.MINI_KIND;
						}
						try {
							thumbnail =MediaStore.Video.Thumbnails.getThumbnail(iLoongLauncher.getInstance().getContentResolver(), id, 
									kind,options);
						} catch (Exception e) {
							if(kind == MediaStore.Video.Thumbnails.MINI_KIND){
								thumbnail =MediaStore.Video.Thumbnails.getThumbnail(iLoongLauncher.getInstance().getContentResolver(), id, 
										MediaStore.Video.Thumbnails.MICRO_KIND,options);
							}
							e.printStackTrace();
						}
						if(thumbnail != null){
							thumbnail = DecodeThumbHelper.resizeBmp(thumbnail, minVideoWidth, minVideoHeight);
							curClient.setThumbnailBmp(thumbnail);
						}
					}
					else if(curClient.getResType()==AppHost3D.CONTENT_TYPE_AUDIO){
						String s = curClient.getThumbnailPath();
						if(s != null && !s.equals("")){
							File file = new File(s);
							if(file.exists()) {
								thumbnail = DecodeThumbHelper.DecodeFile(s,(int)minAudioWidth,(int)minAudioHeight);
								if(thumbnail != null){
									thumbnail = DecodeThumbHelper.resizeBmp(thumbnail, minAudioWidth, minAudioHeight);
									curClient.setThumbnailBmp(thumbnail);
								}
								//Log.v("thumbnail", "audio res="+(thumbnail!=null));
							}
						}
					}
					clients.remove(curClient);
					curClient = null;
					synchronized(timeoutLock){
						timeoutLock.notifyAll();
					}
				}
			}
			
		}
	};
	
	public void destroy(){
		destroy = true;
		synchronized(clientLock){
			clientLock.notify();
		}
	}
	
	public interface ThumbnailClient{
		public long getThumbnailId();
		public String getThumbnailPath();
		public void setThumbnailBmp(Bitmap bmp);
		public int getResType();
	}
}
