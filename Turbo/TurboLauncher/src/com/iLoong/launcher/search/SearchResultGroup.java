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


public class SearchResultGroup extends ViewGroup3D
{
	
	private View3D mTopFill;
	public View3D mNoResultTip;
	public SearchResultList mSearchResultList;
	
	public SearchResultGroup(
			String name )
	{
		super( name );
		setSize( QsearchConstants.W_SEARCH_RESULT_GROUP , QsearchConstants.H_SEARCH_RESULT_GROUP );
		try
		{
			Bitmap bgBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/quick_search/qs_bg.png" ) );
			NinePatch bgNp = new NinePatch( new BitmapTexture( bgBmp , true ) , 1 , 1 , 1 , 1 );
			setBackgroud( bgNp );
			mTopFill = new View3D( "topFill" );
			mTopFill.setSize( QsearchConstants.W_SEARCH_RESULT_TOP_FILL , QsearchConstants.H_SEARCH_RESULT_TOP_FILL );
			mTopFill.setPosition( 0 , UtilsBase.getScreenHeight() - mTopFill.height );
			mSearchResultList = new SearchResultList( "searchResultList" );
			mSearchResultList.setSize( QsearchConstants.W_SEARCH_RESULT_LIST , QsearchConstants.H_SEARCH_RESULT_LIST );
			TextureRegion noResultTipRegion = drawNameTextureRegion(
					iLoongLauncher.getInstance().getString( string.qs_no_search_result_tip_text ) ,
					QsearchConstants.W_NO_SEARCH_RESULT_TIP ,
					QsearchConstants.H_NO_SEARCH_RESULT_TIP );
			mNoResultTip = new View3D( "noResultTip" , noResultTipRegion );
			mNoResultTip.setPosition( 0 , QsearchConstants.Y_NO_SEARCH_RESULT_TIP );
			mNoResultTip.hide();
			addView( mTopFill );
			addView( mSearchResultList );
			addView( mNoResultTip );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	public void load()
	{
		mSearchResultList.load();
	}
	
	public void reLoad()
	{
		mSearchResultList.reLoad();
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
		paint.setColor( R3D.qs_no_search_result_tip_color );
		paint.setSubpixelText( true );
		paint.setTextSize( QsearchConstants.S_NO_SEARCH_RESULT_TIP );
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
