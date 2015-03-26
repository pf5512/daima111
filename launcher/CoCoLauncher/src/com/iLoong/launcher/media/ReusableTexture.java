package com.iLoong.launcher.media;

import java.nio.IntBuffer;

import com.iLoong.launcher.Desktop3D.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.BufferUtils;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.ManagedPixmapTextureData;

public class ReusableTexture extends Texture{

	private ReusableTextureHolder holder;
	private ManagedPixmapTextureData data;
	private static final IntBuffer buffer = BufferUtils.newIntBuffer(1);
	
	public ReusableTexture(ManagedPixmapTextureData data) {
		super(data);
		this.data = data;
	}
	
	public void setHolder(ReusableTextureHolder holder){
		this.holder = holder;
	}
	
	public ReusableTextureHolder getHolder(){
		return holder;
	}
	
	public void free(){
		buffer.put(0, getTextureObjectHandle());
		Gdx.gl.glDeleteTextures(1, buffer);
	}

	@Override
	public void reload() {
		if(holder.disposed){
			holder.prepare(ThumbnailThread.CACHE);
			return;
		}
		data.setPixmap(holder.pixmap);
		super.reload();
		setFilter(R3D.filter, R3D.Magfilter);
	}
}
