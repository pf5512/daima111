package com.iLoong.launcher.media;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import com.iLoong.launcher.Desktop3D.Log;
import android.view.KeyEvent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.media.ReusableTextureHolder;
import com.iLoong.launcher.media.ThumbnailThread.ThumbnailClient;
import com.iLoong.launcher.theme.ThemeManager;

public class PhotoItem extends ReusableTextureHolder{
	public static final int EVENT_PHOTO_LONGCLICK = 0;
	public String data;
	public String title;
	public String bucketDisplayName;
	public int bucketId;
	public long id;
	public PhotoBucket bucket;
	public MediaCache mediaCache;
	private PhotoView view;
	
	public Bitmap thumbnailBmp;
	private static TextureRegion defaultRegion;
	private float defaultPhotoWidth;
	private float defaultPhotoHeight;
	
	public PhotoItem(){
		if(defaultRegion == null)
			defaultRegion = R3D.findRegion("default-photo");
		defaultPhotoWidth = R3D.photo_width+2*R3D.photo_padding;
		defaultPhotoHeight = R3D.photo_height+2*R3D.photo_padding;
	}
	
	public View3D obtainView(){
		if(view == null){
			view = new PhotoView(title);
			view.photo = this;
		}
		return view;
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
				texture = ReusableTexturePool.getInstance().get(PhotoItem.this);
				pixmap.dispose();
				disposed = true;
				Gdx.graphics.requestRendering();
			}
			
		});
	}
	
	public long getThumbnailId(){
		return id;
	}
	
	@Override
	public String getThumbnailPath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getResType()
	{
		return AppHost3D.CONTENT_TYPE_PHOTO;
	}
	
	public void onDelete(){
		if(mediaCache.deletePhoto(this)){
			free();
		}
		
	}
	
	public class PhotoView extends View3D implements MediaView{

		public PhotoItem photo;
		public boolean selected = false;
		
		public PhotoView(String name) {
			super(name);
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			int x = Math.round(this.x);
			int y = Math.round(this.y);
			int _x = 0,_y = 0;
			if(photo.texture != null){
				region.setRegion(photo.texture);
				_x = Math.round((width-region.getRegionWidth())/2);
				_y = Math.round((height-region.getRegionHeight())/2);
			}
			batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
			int _xDefault = Math.round((width-defaultPhotoWidth)/2);
			int _yDefault = Math.round((height-defaultPhotoHeight)/2);
			if (is3dRotation()){
				if(AppHost3D.currentContentType==AppHost3D.CONTENT_TYPE_PHOTO)batch.draw(defaultRegion, x+_xDefault, y+_yDefault, defaultPhotoWidth, defaultPhotoHeight);
				if(photo.texture != null)batch.draw(region, x+_x, y+_y, region.getRegionWidth(), region.getRegionHeight());
				if(AppHost3D.selectState && AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_PHOTO){
					if(selected)batch.draw(R3D.selectedRegion, x+_xDefault+defaultPhotoWidth-R3D.selectedRegion.getRegionWidth(), y+_yDefault+6,
							R3D.selectedRegion.getRegionWidth(), R3D.selectedRegion.getRegionHeight());
					else batch.draw(R3D.unselectRegion, x+_xDefault+defaultPhotoWidth-R3D.unselectRegion.getRegionWidth(), y+_yDefault+6,
							R3D.unselectRegion.getRegionWidth(), R3D.unselectRegion.getRegionHeight());
				}
			}
			else{
				if(AppHost3D.currentContentType==AppHost3D.CONTENT_TYPE_PHOTO)batch.draw(defaultRegion, x+_xDefault, y+_yDefault, originX-_xDefault, originY-_yDefault, defaultPhotoWidth, defaultPhotoHeight, scaleX,
						scaleY, rotation);
				if(photo.texture != null)batch.draw(region, x+_x, y+_y, originX-_x, originY-_y, region.getRegionWidth(), region.getRegionHeight(), scaleX,
						scaleY, rotation);
				if(AppHost3D.selectState && AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_PHOTO){
					if(selected)batch.draw(R3D.selectedRegion, x+_xDefault+defaultPhotoWidth-R3D.selectedRegion.getRegionWidth(), y+_yDefault+6,
							originX-_x, originY-_y,R3D.selectedRegion.getRegionWidth(), R3D.selectedRegion.getRegionHeight(), scaleX,scaleY, rotation);
					else batch.draw(R3D.unselectRegion, x+_xDefault+defaultPhotoWidth-R3D.unselectRegion.getRegionWidth(), y+_yDefault+6,
							originX-_x, originY-_y,R3D.unselectRegion.getRegionWidth(), R3D.unselectRegion.getRegionHeight(), scaleX,scaleY, rotation);
				}
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
			    intent.setDataAndType(Uri.fromFile(new File(photo.data)), "image/*");
			    iLoongLauncher.getInstance().startActivity(intent); 
			}
			
			return true;
		}

		@Override
		public boolean onLongClick(float x, float y) {
			if(AppHost3D.selectState)return true;
			else{
				viewParent.onCtrlEvent(this, EVENT_PHOTO_LONGCLICK);
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
			photo.prepare(priority);
		}

		@Override
		public void free() {
			photo.free();
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
			if(photo.data != null){
				list.add(Uri.parse("file://"+photo.data));
			}
		}

		@Override
		public void onDelete() {
			if(!selected)return;
			photo.onDelete();
		}
		
		@Override
		public void clearSelect() {
			selected = false;
		}
	}

}
