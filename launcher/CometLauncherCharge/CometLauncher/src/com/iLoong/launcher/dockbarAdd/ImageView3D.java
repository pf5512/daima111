package com.iLoong.launcher.dockbarAdd;


import android.graphics.Bitmap;
import android.graphics.Color;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;


public class ImageView3D extends View3D
{
	
	public ImageView3D(
			String name ,
			TextureRegion texture )
	{
		super( name );
		// TODO Auto-generated constructor stub
		setSize( texture.getRegionWidth() , texture.getRegionHeight() );
		region = texture;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		super.draw( batch , parentAlpha );
		//batch.draw(titleRegion, this.x, this.y + titleTopOffset);
	}
}
