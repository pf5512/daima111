package com.iLoong.launcher.search;


import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coco.theme.themebox.util.Tools;
import com.cooeeui.brand.turbolauncher.R.string;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.umeng.analytics.MobclickAgent;


public class GuessGroup extends ViewGroup3D
{
	
	private TitleGroup mTitleGroup;
	public GuessPage mGuessPage;
	
	public GuessGroup(
			String name )
	{
		super( name );
		this.width = UtilsBase.getScreenWidth();
		setSize( QsearchConstants.W_GUESS_GROUP , QsearchConstants.H_GUESS_GROUP );
		setPosition( 0 , QsearchConstants.H_QUICK_SEARCH - this.height );
		initView();
	}
	
	private void initView()
	{
		mTitleGroup = new TitleGroup( "titleGroup" );
		mGuessPage = new GuessPage( "guessPage" );
		addView( mTitleGroup );
		addView( mGuessPage );
		mTitleGroup.setPosition( 0 , this.height - mTitleGroup.height );
		try
		{
			Bitmap bgBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/quick_search/qs_guess_bg.png" ) );
			NinePatch bgNp = new NinePatch( new BitmapTexture( bgBmp , true ) , 1 , 1 , 1 , 1 );
			setBackgroud( bgNp );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	public void load()
	{
		mGuessPage.load();
	}
	
	public void reLoad()
	{
		mGuessPage.reLoad();
	}
	
	class TitleGroup extends ViewGroup3D
	{
		
		private View3D mTitleText;
		private View3D mLine;
		private TextureRegion mTextTitle;
		private NinePatch mLineNp;
		
		public TitleGroup(
				String name )
		{
			super( name );
			setSize( QsearchConstants.W_GUESS_GROUP_TITLE , QsearchConstants.H_GUESS_GROUP_TITLE );
			mTextTitle = drawNameTextureRegion( iLoongLauncher.getInstance().getString( string.guess_title_text ) , this.width , this.height );
			Bitmap lineBmp;
			try
			{
				lineBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/quick_search/qs_guess_line.png" ) );
				mLineNp = new NinePatch( new BitmapTexture( lineBmp , true ) , 1 , 1 , 0 , 0 );
				mTitleText = new View3D( "titleText" , mTextTitle );
				mLine = new View3D( "line" );
				mLine.setSize( QsearchConstants.W_GUESS_GROUP_DIVIDER , QsearchConstants.H_GUESS_GROUP_DIVIDER );
				mLine.setBackgroud( mLineNp );
				addView( mTitleText );
				addView( mLine );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	public static TextureRegion drawNameTextureRegion(
			String name ,
			final float width ,
			final float height )
	{
		Bitmap backImage = null;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );
		Paint paint = new Paint();
		paint.setAntiAlias( true );
		paint.setDither( true );
		paint.setColor( R3D.qs_guess_title_text_color );
		paint.setSubpixelText( true );
		paint.setTextSize( QsearchConstants.S_GUESS_TITLE_TEXT );
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		if( name != null )
		{
			canvas.drawText( name , QsearchConstants.S_GUESS_TITLE_TEXT , posY , paint );
		}
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		return newTextureRegion;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( SearchEditTextGroup.mStatus == SearchEditTextGroup.POS_STATUS_TOP )
		{
			return true;
		}
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( SearchEditTextGroup.mStatus == SearchEditTextGroup.POS_STATUS_TOP )
		{
			return true;
		}
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		MobclickAgent.onEvent( iLoongLauncher.getInstance() , "ClickGuessedApp" );
		if( SearchEditTextGroup.mStatus == SearchEditTextGroup.POS_STATUS_TOP )
		{
			return true;
		}
		return super.onClick( x , y );
	}
}
