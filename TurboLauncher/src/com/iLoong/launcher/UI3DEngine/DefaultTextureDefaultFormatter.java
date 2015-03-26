package com.iLoong.launcher.UI3DEngine;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class DefaultTextureDefaultFormatter implements DisplayTextFormatter
{
	
	public Paint paint;
	
	@Override
	public TextureRegion getDisplayTexture(
			String displayTitle ,
			int titleWidth ,
			int titleHeight )
	{
		// TODO Auto-generated method stub
		if( paint == null )
		{
			throw new IllegalArgumentException( "paint cannot be null." );
		}
		Bitmap bmp = Bitmap.createBitmap( titleWidth , titleHeight , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		// canvas.drawColor(android.graphics.Color.RED);
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );//zjp
		FontMetrics fontMetrics = paint.getFontMetrics();
		float offsetX = 0;
		float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float fontPosY = titleHeight - ( titleHeight - singleLineHeight ) / 2f - fontMetrics.bottom;
		canvas.drawText( displayTitle , offsetX , fontPosY , paint );
		TextureRegion region = new TextureRegion( new BitmapTexture( bmp ) );
		bmp.recycle();
		return region;
	}
	
	@Override
	public String formatDisplayTitle(
			String title ,
			int titleWidth ,
			int titleHeight )
	{
		// TODO Auto-generated method stub
		String tmpTitle = title;
		if( Utils3D.measureText( paint , tmpTitle ) > titleWidth )
		{
			for( int i = 0 ; i < title.length() - 1 ; i++ )
			{
				tmpTitle = title.substring( i );
				if( Utils3D.measureText( paint , tmpTitle ) <= titleWidth )
				{
					break;
				}
			}
		}
		return tmpTitle;
	}
	
	@Override
	public void setPaint(
			Paint paint )
	{
		// TODO Auto-generated method stub
		this.paint = paint;
	}
}
