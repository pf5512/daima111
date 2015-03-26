package com.iLoong.launcher.UI3DEngine;


import android.graphics.Paint;

import com.badlogic.gdx.graphics.g2d.TextureRegion;


public interface DisplayTextFormatter
{
	
	public void setPaint(
			Paint paint );
	
	public TextureRegion getDisplayTexture(
			String title ,
			int width ,
			int height );
	
	public String formatDisplayTitle(
			String title ,
			int width ,
			int height );
}
