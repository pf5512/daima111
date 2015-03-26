package com.iLoong.ThemeClock.View;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;
//import com.iLoong.launcher.Desktop3D.R3D;

public class BitmapTexture extends Texture{
	BitmapTextureData data;
	public BitmapTexture(Bitmap bmp){
		super(new BitmapTextureData(bmp));
		data = (BitmapTextureData) this.getTextureData();
		//setFilter(R3D.filter, R3D.Magfilter);
		setFilter(TextureFilter.Nearest,TextureFilter.Nearest);
	}
	public void setBitmap(Bitmap bmp)
	{
		if(data.disposed)
		{
			data.setBitmap(bmp);
			load(data);
		}
		else
		{
			data.setBitmap(bmp);
			Gdx.gl.glBindTexture(GL10.GL_TEXTURE_2D, this.getTextureObjectHandle());
			GLUtils.texSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, bmp);
		}
		//setFilter(R3D.filter, R3D.Magfilter);
		setFilter(TextureFilter.Nearest,TextureFilter.Nearest);
	}
	public boolean isDisposed()
	{
		return data.disposed;
	}
}


class BitmapTextureData implements TextureData{
	private Bitmap bmp;
	public boolean disposed = false;
	private int width = 0;
	private int height = 0;
	
	public BitmapTextureData(Bitmap bmp){	
		this.bmp = bmp;
		width = bmp.getWidth();
		height = bmp.getHeight();
	}
	
	public void setBitmap(Bitmap bitmap)
	{
		bmp = bitmap;
		disposed = false;
	}
	@Override
	public int getWidth() {
		//if(bmp == null || bmp.isRecycled())return 0;
		return bmp.getWidth();
	}

	@Override
	public int getHeight() {
		//if(bmp == null || bmp.isRecycled())return 0;
		return bmp.getHeight();
	}

	@Override
	public TextureDataType getType() {
		return TextureDataType.Compressed;
	}

	@Override
	public boolean isPrepared() {
		return true;
	}

	@Override
	public void prepare() {
	}

	@Override
	public Pixmap consumePixmap() {
		return null;
	}

	@Override
	public boolean disposePixmap() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void consumeCompressedData() {
		// TODO Auto-generated method stub
		if(bmp!=null&&!bmp.isRecycled()){
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
		}	
		else{
			disposed = true;
		}
	}

	@Override
	public Format getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean useMipMaps() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isManaged() {
		// TODO Auto-generated method stub
		return true;
	}
}

class BitmapFileTextureData implements TextureData{
	final FileHandle file;
	int width = 0;
	int height = 0;
	private Bitmap bmp;
	boolean isPrepared = false;
	
	public BitmapFileTextureData(FileHandle file){	
		this.file = file;
	}
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public TextureDataType getType() {
		return TextureDataType.Compressed;
	}

	@Override
	public boolean isPrepared() {
		return isPrepared;
	}

	@Override
	public void prepare() {
		if (isPrepared) throw new GdxRuntimeException("Already prepared");
		if (bmp == null) {
			bmp = BitmapFactory.decodeStream(file.read());
			width = bmp.getWidth();
			height = bmp.getHeight();
		}
		isPrepared = true;
	}

	@Override
	public Pixmap consumePixmap() {
		return null;
	}

	@Override
	public boolean disposePixmap() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void consumeCompressedData() {
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
		bmp.recycle();
		bmp = null;
		isPrepared = false;
	}

	@Override
	public Format getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean useMipMaps() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isManaged() {
		// TODO Auto-generated method stub
		return true;
	}
}
