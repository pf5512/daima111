package com.iLoong.launcher.media;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import com.iLoong.launcher.Desktop3D.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.ListView3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.media.PhotoItem.PhotoView;
import com.iLoong.launcher.media.ThumbnailThread.ThumbnailClient;
import com.iLoong.launcher.theme.ThemeManager;

public class AudioItem  extends ReusableTextureHolder implements ThumbnailClient{
	//public static float itemHeight = 130;//xiatian del	//explorer to adaptive difference resolution 
	public static float itemHeight;//xiatian add	//explorer to adaptive difference resolution 
	public static final int EVENT_AUDIO_LONGCLICK = 0;
	public String data;
	public String title;
	private String albumDisplayName;
	public int albumId;
	public long id;
	public int duration;
	public int size;
	public String mimetype;
	public String artist;
	public String thumbnailPath;
	public String folder;
	public AudioAlbum album;
	public AudioAlbum artistAlbum;
	public AudioAlbum folderAlbum;
	public MediaCache mediaCache;
	public ReusableTexture texture;
	public Bitmap thumbnailBmp;
	public TextureRegion defaultRegion;
	public static TextureRegion[] defaultRegions;
	private AudioView view;
	private float bgWidth;
	private float bgHeight;
	private float defaultAudioWidth;
	private float defaultAudioHeight;
	 
	//gets default texture for display
	public AudioItem(){
		if(defaultRegions == null){
			defaultRegions = new TextureRegion[6];
			for(int i = 0;i < defaultRegions.length;i++){
				defaultRegions[i] = R3D.findRegion("default-audio-"+i);
			}
		}
		itemHeight = R3D.audio_height+4*R3D.audio_bottom_padding;
		defaultAudioWidth = R3D.audio_width;
		defaultAudioHeight = R3D.audio_height;
		bgWidth = R3D.audio_width+R3D.audio_bottom_padding+R3D.audio_left_padding;
		bgHeight = R3D.audio_height+2*R3D.audio_bottom_padding;
	}
	
	public void setAlbumDisplayName(String name){
		if(name == null)name = "";
		albumDisplayName = name;
		defaultRegion = defaultRegions[Math.abs(name.hashCode())%6];
	}
	
	public String getAlbumDisplayName(){
		return albumDisplayName;
	}
	
	public View3D obtainView(){
		if(view == null){
			view = new AudioView(title);
			view.audio = this;
		}
		return view;
	}

	//删除图片
	public void onDelete(){
		if(mediaCache.deleteAudio(this)){
			free();
		}
		
	}
	
	public void parseFolder(){
		int index = data.lastIndexOf("/");
		if(index == -1)folder = "";
		else folder = data.substring(0, index);
	}
	
	public void setThumbnailBmp(Bitmap bmp){
		if(free || texture != null){
			bmp.recycle();
			return;
		}
		thumbnailBmp = Utils3D.resizeBmp(bmp, view.width, view.height);
		pixmap = Utils3D.bmp2Pixmap(thumbnailBmp);
		disposed = false;
		iLoongLauncher.getInstance().postRunnable(new Runnable(){

			@Override
			public void run() {
				if(free){
					return;
				}
				if(texture != null)return;
				texture = ReusableTexturePool.getInstance().get(AudioItem.this);
				pixmap.dispose();
				disposed = true;
				Gdx.graphics.requestRendering();
			}
			
		});
	}
	
	public long getThumbnailId(){
		return id;
	}
	
	public String getThumbnailPath(){
		return thumbnailPath;
	}
	
	@Override
	public int getResType() {
		return AppHost3D.CONTENT_TYPE_AUDIO;
	}
	public class AudioView extends View3D implements MediaView{

		public AudioItem audio;
		public boolean selected = false;
		public TextureRegion titleRegion;
		public TextureRegion artistRegion;
		
		public AudioView(String name) {
			super(name);
		}
		
		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			int x = Math.round(this.x);
			int y = Math.round(this.y);
			int _thumbnailX = 2*R3D.audio_left_padding,_thumbnailY = 0,_xDefault = 2*R3D.audio_left_padding,_yDefault = 0;
			int _titleX = 0,_titleY = 0,_artistX = 0,_artistY = 0;
			if(AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_AUDIO){
				if(titleRegion==null){
					titleRegion = new TextureRegion(new BitmapTexture((
							AppBar3D.titleToPixmapWidthLimit(title, (int)(width-R3D.selectedRegion.getRegionWidth()-defaultAudioWidth-25),R3D.photo_title_size, Color.WHITE, true))));
				}
				if(artistRegion==null){
					artistRegion = new TextureRegion(new BitmapTexture((
							AppBar3D.titleToPixmapWidthLimit(artist, (int)(width-R3D.selectedRegion.getRegionWidth()-defaultAudioWidth-25),R3D.photo_title_size, Color.WHITE, true))));
				}
				_titleY = Math.round((height/2-titleRegion.getRegionHeight())/2+height/2);
				_artistY = Math.round((height/2-artistRegion.getRegionHeight())/2);
			}
			if(audio.texture != null){
				region.setRegion(audio.texture);
				if(AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_AUDIO_ALBUM){
					_thumbnailX = Math.round((width-region.getRegionWidth())/2);
				}
				_thumbnailY = Math.round((height-region.getRegionHeight())/2);
			}
			
			if(AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_AUDIO_ALBUM){
				_xDefault = Math.round((width-defaultAudioWidth)/2);
			}
			_yDefault = Math.round((height-defaultAudioHeight)/2);
			
			_titleX = (int) (defaultAudioWidth+_xDefault);
			_artistX = (int) (defaultAudioWidth+_xDefault);
			
			batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
			if (is3dRotation()){
				if(AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_AUDIO){
					if(AudioAlbum.bgRegion != null){
						batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
						batch.draw(AudioAlbum.bgRegion, x+_xDefault-R3D.audio_left_padding, y+_yDefault-R3D.audio_bottom_padding,bgWidth,bgHeight);
					}
				}
				if(audio.texture != null)batch.draw(region, x+_thumbnailX, y+_thumbnailY, region.getRegionWidth(), region.getRegionHeight());
				else batch.draw(defaultRegion, x+_xDefault, y+_yDefault, defaultAudioWidth, defaultAudioHeight);
				
				if(AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_AUDIO){
					batch.draw(titleRegion, x+_titleX, y+_titleY, titleRegion.getRegionWidth(), titleRegion.getRegionHeight());
					batch.draw(artistRegion, x+_artistX, y+_artistY, artistRegion.getRegionWidth(), artistRegion.getRegionHeight());
					if(AppHost3D.selectState){
						if(selected)batch.draw(R3D.selectedRegion, x+width-R3D.selectedRegion.getRegionWidth()-10, y+(height-R3D.selectedRegion.getRegionHeight())/2,
								R3D.selectedRegion.getRegionWidth(), R3D.selectedRegion.getRegionHeight());
						else batch.draw(R3D.unselectRegion, x+width-R3D.unselectRegion.getRegionWidth()-10, y+(height-R3D.unselectRegion.getRegionHeight())/2,
								R3D.unselectRegion.getRegionWidth(), R3D.unselectRegion.getRegionHeight());
					}
				}
			}
			else{
				if(AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_AUDIO){
					if(AudioAlbum.bgRegion != null){
						batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
						batch.draw(AudioAlbum.bgRegion, x+_xDefault-R3D.audio_left_padding, y+_yDefault-R3D.audio_bottom_padding,originX-_thumbnailX, originY-_thumbnailY,
								bgWidth,bgHeight,scaleX,scaleY, rotation);
					}
				}
				if(audio.texture != null)batch.draw(region, x+_thumbnailX, y+_thumbnailY, originX-_thumbnailX, originY-_thumbnailY, region.getRegionWidth(), region.getRegionHeight(), scaleX,
						scaleY, rotation);
				else batch.draw(defaultRegion, x+_xDefault, y+_yDefault, originX-_xDefault, originY-_yDefault, defaultAudioWidth, defaultAudioHeight, scaleX,
						scaleY, rotation);
				if(AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_AUDIO){
					batch.draw(titleRegion, x+_titleX, y+_titleY, titleRegion.getRegionWidth(), titleRegion.getRegionHeight());
					batch.draw(artistRegion, x+_artistX, y+_artistY, artistRegion.getRegionWidth(), artistRegion.getRegionHeight());
					if(AppHost3D.selectState){
						if(selected)batch.draw(R3D.selectedRegion, x+width-R3D.selectedRegion.getRegionWidth()-10, y+(height-R3D.selectedRegion.getRegionHeight())/2,
								originX-_thumbnailX, originY-_thumbnailY,R3D.selectedRegion.getRegionWidth(), R3D.selectedRegion.getRegionHeight(), scaleX,scaleY, rotation);
						else batch.draw(R3D.unselectRegion, x+width-R3D.unselectRegion.getRegionWidth()-10, y+(height-R3D.unselectRegion.getRegionHeight())/2,
								originX-_thumbnailX, originY-_thumbnailY,R3D.unselectRegion.getRegionWidth(), R3D.unselectRegion.getRegionHeight(), scaleX,scaleY, rotation);
					}
				}
			}	
			if(AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_AUDIO){
				batch.draw(R3D.findRegion("appbar-popmenu-divider"), x, y, width, 1);
			}
		}
		
		@Override
		public boolean onClick(float x, float y) {
			if(AppHost3D.selectState){
				selected = !selected;
			}
			else{
				if(!Utils3D.ExistSDCard()){
					SendMsgToAndroid.sendCircleToastMsg("SD卡已移除，无法打开文件");
					return true;
				}
				Intent intent = new Intent();
			    intent.setAction(android.content.Intent.ACTION_VIEW);
			    intent.setDataAndType(Uri.fromFile(new File(audio.data)), "audio/*");
			    iLoongLauncher.getInstance().startActivity(intent); 
			}
			
			return true;
		}

		@Override
		public boolean onLongClick(float x, float y) {
			if(AppHost3D.selectState)return true;
			else{
				viewParent.onCtrlEvent(this, EVENT_AUDIO_LONGCLICK);
				selected = true;
			}
			return true;
		}

		@Override
		public void setSize(float w, float h) {
			super.setSize(w, h);
			this.setOrigin(w / 2, h / 2);
		}

		@Override
		public void prepare(int priority) {
			audio.prepare(priority);
		}

		@Override
		public void free() {
			audio.free();
		}

		@Override
		public void refresh() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void select() {
			selected = true;
		}

		@Override
		public void share(ArrayList<Uri> list) {
			if(!selected)return;
			if(audio.data != null){
				list.add(Uri.parse("file://"+audio.data));
			}
		}

		@Override
		public void onDelete() {
			if(!selected)return;
			audio.onDelete();
		}
		
		@Override
		public void clearSelect() {
			selected = false;
		}
	}

}
