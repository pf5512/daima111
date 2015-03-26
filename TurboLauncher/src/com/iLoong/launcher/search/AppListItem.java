package com.iLoong.launcher.search;


import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class AppListItem extends ViewGroup3D
{
	
	private ViewGroup3D mTitle;
	private GridView3D mGridView;
	private int mCellCountX = 4;
	private int mRowCount;
	
	public AppListItem(
			String name ,
			int rowCount )
	{
		super( name );
		this.mRowCount = rowCount;
		setSize( QsearchConstants.W_ALL_APP_LIST_ITEM , mRowCount * R3D.qs_app_list_row_height + QsearchConstants.H_ALL_APP_LIST_ITEM_TITLE_GROUP );
		initView();
	}
	
	private void initView()
	{
		mTitle = new ViewGroup3D();
		mTitle.setSize( QsearchConstants.W_ALL_APP_LIST_ITEM_TITLE_GROUP , QsearchConstants.H_ALL_APP_LIST_ITEM_TITLE_GROUP );
		mTitle.setPosition( 0 , this.height - mTitle.height );
		View3D titleText = new View3D( name , drawNameTextureRegion( name , mTitle.width , mTitle.height ) );
		mTitle.addView( titleText );
		try
		{
			Bitmap titleBgBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/quick_search/qs_app_list_title_bg.9.png" ) );
			NinePatch titleBgNp = new NinePatch( new BitmapTexture( titleBgBmp , true ) , 1 , 1 , 1 , 1 );
			mTitle.setBackgroud( titleBgNp );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		addView( mTitle );
		initGridView();
	}
	
	public void load()
	{
		for( int i = 0 ; i < QSearchGroup.allApps.size() ; i++ )
		{
			SearchApp btn = QSearchGroup.allApps.get( i );
			String firstLetter = btn.title_pinyin.substring( 0 , 1 );
			btn.setScale( 0.8f , 0.8f );
			if( this.name.equals( "#" ) )
			{
				if( !firstLetter.equals( "A" ) && !firstLetter.equals( "a" ) && !firstLetter.equals( "B" ) && !firstLetter.equals( "b" ) && !firstLetter.equals( "C" ) && !firstLetter.equals( "c" ) && !firstLetter
						.equals( "D" ) && !firstLetter.equals( "d" ) && !firstLetter.equals( "E" ) && !firstLetter.equals( "e" ) && !firstLetter.equals( "F" ) && !firstLetter.equals( "f" ) && !firstLetter
						.equals( "G" ) && !firstLetter.equals( "g" ) && !firstLetter.equals( "H" ) && !firstLetter.equals( "h" ) && !firstLetter.equals( "I" ) && !firstLetter.equals( "i" ) && !firstLetter
						.equals( "J" ) && !firstLetter.equals( "j" ) && !firstLetter.equals( "K" ) && !firstLetter.equals( "k" ) && !firstLetter.equals( "L" ) && !firstLetter.equals( "l" ) && !firstLetter
						.equals( "M" ) && !firstLetter.equals( "m" ) && !firstLetter.equals( "N" ) && !firstLetter.equals( "n" ) && !firstLetter.equals( "O" ) && !firstLetter.equals( "o" ) && !firstLetter
						.equals( "P" ) && !firstLetter.equals( "p" ) && !firstLetter.equals( "Q" ) && !firstLetter.equals( "q" ) && !firstLetter.equals( "R" ) && !firstLetter.equals( "r" ) && !firstLetter
						.equals( "S" ) && !firstLetter.equals( "s" ) && !firstLetter.equals( "T" ) && !firstLetter.equals( "t" ) && !firstLetter.equals( "U" ) && !firstLetter.equals( "u" ) && !firstLetter
						.equals( "V" ) && !firstLetter.equals( "v" ) && !firstLetter.equals( "W" ) && !firstLetter.equals( "w" ) && !firstLetter.equals( "X" ) && !firstLetter.equals( "x" ) && !firstLetter
						.equals( "Y" ) && !firstLetter.equals( "Z" ) && !firstLetter.equals( "z" ) )
				{
					mGridView.addItem( btn );
				}
			}
			else if( firstLetter.equals( name ) )
			{
				mGridView.addItem( btn );
			}
		}
	}
	
	public void reLoad()
	{
		load();
	}
	
	private void initGridView()
	{
		mGridView = new GridView3D( "gridView" , UtilsBase.getScreenWidth() - QsearchConstants.W_LETTERS_GROUP , mRowCount * R3D.qs_app_list_row_height , mCellCountX , mRowCount );
		mGridView.setPosition( 0 , 0 );
		mGridView.setPadding( R3D.qs_category_grid_padding_left , R3D.qs_category_grid_padding_right , R3D.qs_category_grid_padding_top , R3D.qs_category_grid_padding_bottom );
		addView( mGridView );
	}
	
	public TextureRegion drawNameTextureRegion(
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
		paint.setColor( R3D.qs_app_list_item_title_text_color );
		paint.setSubpixelText( true );
		paint.setTextSize( QsearchConstants.S_APP_LIST_ITEM_TITLE_TEXT );
		if( name != null )
		{
			canvas.drawText( name , 8 * QsearchConstants.S_SCALE , QsearchConstants.S_APP_LIST_ITEM_TITLE_TEXT , paint );
		}
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		return newTextureRegion;
	}
}
