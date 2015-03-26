package com.iLoong.launcher.UI3DEngine;

import android.graphics.Bitmap;
import com.iLoong.launcher.Desktop3D.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.MyPixmapPacker.Page;

public class FileTexture extends Texture{

	private Page page;
	public FileTexture(TextureData data,Page page) {
		super(data);
		this.page = page;
	}

	@Override
	public void reload() {
		if(page.disposed){
			long time = System.currentTimeMillis();
			Log.v("pack", "disposed!read from file");
			page.image = PixmapIO.readCIM(page.file);
			Log.v("pack", "read time="+(System.currentTimeMillis()-time));
			page.disposed = false;
		}
		((ManagedPixmapTextureData)page.texture.getTextureData()).setPixmap(page.image);
		super.reload();
		setFilter(R3D.filter, R3D.Magfilter);
	}

//	public FileTexture(Gdx gdx,Pixmap pixmap){
//		this(pixmap);
//	}
//	
//	public FileTexture(Bitmap bmp,boolean usePixmap){
//		super(new BitmapTextureData(bmp));
//		setFilter(R3D.filter,R3D.filter);
//	}
//	public FileTexture(Bitmap bmp){
//		this(Utils3D.bmp2Pixmap(bmp));
////		super(new BitmapTextureData(bmp));
////		setFilter(R3D.filter,R3D.filter);
//	}
//	public FileTexture(Pixmap pixmap) {
//		super(new MyPixmapTextureData(pixmap,null,false ,true));
//		setFilter(R3D.filter,R3D.filter);
//		// TODO Auto-generated constructor stub
//	}
//	public FileTexture(Pixmap pixmap,boolean disposePixmap) {
//		super(new MyPixmapTextureData(pixmap,null,false ,disposePixmap));
//		setFilter(R3D.filter,R3D.filter);
//	}
//	
//	public FileTexture (Pixmap pixmap, Format format, boolean useMipMaps) {
//		super(new MyPixmapTextureData(pixmap, format, useMipMaps, false));
//	}
//
//	public FileTexture (int width, int height, Format format) {
//		super(new MyPixmapTextureData(new Pixmap(width, height, format), null, false, true));
//	}	
	
}