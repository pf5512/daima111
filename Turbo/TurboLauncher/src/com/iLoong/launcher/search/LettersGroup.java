package com.iLoong.launcher.search;


import java.io.IOException;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.search.QSearchGroup.PopGroup;


public class LettersGroup extends ViewGroup3D
{
	
	private NinePatch mLettersBg;
	public static final String b[] = { "#" , "A" , "B" , "C" , "D" , "E" , "F" , "G" , "H" , "I" , "J" , "K" , "L" , "M" , "N" , "O" , "P" , "Q" , "R" , "S" , "T" , "U" , "V" , "W" , "X" , "Y" , "Z" };
	
	public LettersGroup(
			String name )
	{
		super( name );
		setSize( QsearchConstants.W_LETTERS_GROUP , QsearchConstants.H_LETTERS_GROUP );
		setPosition( QsearchConstants.X_LETTERS_GROUP , QsearchConstants.Y_LETTERS_GROUP );
		try
		{
			Bitmap lettersBgBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/quick_search/qs_letter_bg.png" ) );
			mLettersBg = new NinePatch( new BitmapTexture( lettersBgBmp , true ) , 1 , 1 , 1 , 1 );
			setBackgroud( mLettersBg );
			initView();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	private void initView()
	{
		for( int i = 0 ; i < b.length ; i++ )
		{
			Letter letter = new Letter( b[i] );
			addView( letter );
			letter.setPosition( ( this.width - letter.width ) / 2 , ( this.height / 27 ) * ( b.length - 1 - i ) );
		}
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( Desktop3DListener.root.qSearchGroup.mAllAppGroup.mAllAppList.isMove() )
		{
			Desktop3DListener.root.qSearchGroup.mAllAppGroup.mAllAppList.onTouchDown( x , y , pointer );
			Desktop3DListener.root.qSearchGroup.mAllAppGroup.mAllAppList.onTouchUp( x , y , pointer );
		}
		if( SearchEditTextGroup.mStatus == SearchEditTextGroup.POS_STATUS_TOP )
		{
			return true;
		}
		requestFocus();
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		releaseFocus();
		Desktop3DListener.root.qSearchGroup.startPopFadeOutAnim();
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		for( int i = 0 ; i < b.length ; i++ )
		{
			if( y < ( this.height / b.length ) * ( b.length - ( i ) ) && y > ( this.height / b.length ) * ( b.length - ( i + 1 ) ) )
			{
				int index = 0;
				for( Iterator<String> it = AllAppList.mLetterList.iterator() ; it.hasNext() ; )
				{
					String firstLetter = it.next();
					if( firstLetter.equals( b[i] ) )
					{
						PopGroup pop = Desktop3DListener.root.qSearchGroup.mPopGroup;
						Timeline timeline = Desktop3DListener.root.qSearchGroup.qs_pop_timeline;
						if( timeline != null )
						{
							timeline.free();
							timeline = null;
						}
						pop.color.a = 1;
						pop.getLetter().region = pop.getLetterRegions().get( i );
						float totalScroll = 0;
						for( int j = 0 ; j < index ; j++ )
						{
							totalScroll += Desktop3DListener.root.qSearchGroup.mAllAppGroup.mAllAppList.getChildAt( j ).height;
						}
						if( totalScroll > Desktop3DListener.root.qSearchGroup.mAllAppGroup.mAllAppList.getScrollMax() )
							totalScroll = Desktop3DListener.root.qSearchGroup.mAllAppGroup.mAllAppList.getScrollMax();
						Desktop3DListener.root.qSearchGroup.mAllAppGroup.mAllAppList.setUser( totalScroll );
					}
					index++;
				}
			}
		}
		return true;
	}
	
	class Letter extends View3D
	{
		
		public Letter(
				String name )
		{
			super( name );
			setSize( QsearchConstants.W_LETTER , QsearchConstants.H_LETTER );
			this.region = drawNameTextureRegion( name , this.width , this.height );
		}
		
		@Override
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			int index = 0;
			for( Iterator<String> it = AllAppList.mLetterList.iterator() ; it.hasNext() ; )
			{
				String letter = it.next();
				if( letter.equals( name ) )
				{
					for( int j = 0 ; j < b.length ; j++ )
					{
						if( b[j].equals( name ) )
						{
							PopGroup pop = Desktop3DListener.root.qSearchGroup.mPopGroup;
							Timeline timeline = Desktop3DListener.root.qSearchGroup.qs_pop_timeline;
							if( timeline != null )
							{
								timeline.free();
								timeline = null;
							}
							pop.getLetter().region = pop.getLetterRegions().get( j );
							pop.color.a = 1;
						}
					}
					float totalScroll = 0;
					for( int i = 0 ; i < index ; i++ )
					{
						totalScroll += Desktop3DListener.root.qSearchGroup.mAllAppGroup.mAllAppList.getChildAt( i ).height;
					}
					if( totalScroll > Desktop3DListener.root.qSearchGroup.mAllAppGroup.mAllAppList.getScrollMax() )
						totalScroll = Desktop3DListener.root.qSearchGroup.mAllAppGroup.mAllAppList.getScrollMax();
					Desktop3DListener.root.qSearchGroup.mAllAppGroup.mAllAppList.setUser( totalScroll );
				}
				index++;
			}
			return true;
		}
		
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			Desktop3DListener.root.qSearchGroup.startPopFadeOutAnim();
			return super.onTouchUp( x , y , pointer );
		}
	}
	
	public static TextureRegion drawNameTextureRegion(
			String name ,
			final float width ,
			final float height )
	{
		Bitmap backImage = null;
		if( ( (int)width ) <= 0 || ( (int)height ) <= 0 )
		{
			backImage = Bitmap.createBitmap( 40 , 11 , Config.ARGB_8888 );
		}
		else
		{
			backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		}
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );
		Paint paint = new Paint();
		paint.setAntiAlias( true );
		paint.setDither( true );
		paint.setColor( R3D.qs_letter_text_color );
		paint.setSubpixelText( true );
		paint.setTextSize( QsearchConstants.S_LETTER );
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		if( name != null )
		{
			canvas.drawText( name , ( width - paint.measureText( name ) ) / 2 , posY , paint );
		}
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		return newTextureRegion;
	}
}
