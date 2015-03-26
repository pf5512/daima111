package com.iLoong.launcher.UI3DEngine;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class TextFieldStyle
{
	
	/** Optional. */
	public NinePatch background , cursor;
	public BitmapFont font;
	public Color fontColor;
	/** Optional. */
	public TextureRegion selection;
	/** Optional. */
	public BitmapFont messageFont;
	/** Optional. */
	public Color messageFontColor;
	
	public TextFieldStyle()
	{
	}
	
	public TextFieldStyle(
			BitmapFont font ,
			Color fontColor ,
			BitmapFont messageFont ,
			Color messageFontColor ,
			NinePatch cursor ,
			TextureRegion selection ,
			NinePatch background )
	{
		this.messageFont = messageFont;
		this.messageFontColor = messageFontColor;
		this.background = background;
		this.cursor = cursor;
		this.font = font;
		this.fontColor = fontColor;
		this.selection = selection;
	}
}
