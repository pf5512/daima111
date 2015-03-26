package com.iLoong.launcher.media;


import java.util.ArrayList;

import com.badlogic.gdx.graphics.Pixmap;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.ManagedPixmapTextureData;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.media.ReusableTextureHolder;


public class ReusableTexturePool
{
	
	public static final int TEXTURE_BUFFER_NUM_MAX = 40;
	public static final int TEXTURE_BUFFER_NUM_INIT = 12;
	private static ReusableTexturePool texturePool;
	private ArrayList<ReusableTexture> textures;
	
	public static ReusableTexturePool getInstance()
	{
		if( texturePool == null )
		{
			synchronized( ReusableTexturePool.class )
			{
				if( texturePool == null )
					texturePool = new ReusableTexturePool();
			}
		}
		return texturePool;
	}
	
	public ReusableTexturePool()
	{
		textures = new ArrayList<ReusableTexture>( TEXTURE_BUFFER_NUM_INIT );
	}
	
	private ReusableTexture create(
			ReusableTextureHolder holder )
	{
		ReusableTexture texture = new ReusableTexture( new ManagedPixmapTextureData( holder.pixmap , holder.pixmap.getFormat() , false ) );
		texture.setHolder( holder );
		return texture;
	}
	
	public ReusableTexture get(
			ReusableTextureHolder holder )
	{
		if( textures.isEmpty() )
		{
			ReusableTexture texture = create( holder );
			return texture;
		}
		else
		{
			ReusableTexture texture = textures.remove( textures.size() - 1 );
			texture.setHolder( holder );
			texture.reload();
			return texture;
		}
	}
	
	public void free(
			ReusableTexture texture )
	{
		if( !textures.contains( texture ) )
		{
			textures.add( texture );
			texture.free();
			texture.setHolder( null );
		}
	}
}
