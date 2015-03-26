package com.iLoong.launcher.media;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class BottomBar extends View3D
{
	
	public static final int SELECT_ALL = 0;
	public static final int SHARE = 1;
	public static final int SETTING = 2;
	public static final int DELETE = 3;
	public static final int ALBUM = 4;
	public static final int ARTIST = 5;
	public static final int FOLDER = 6;
	public static final int BACK = 7;
	public static final int EDIT = 8;
	public static final int STATE_EDIT = 0;
	public static final int STATE_DISPLAY = 1;
	public int curState = STATE_EDIT;
	public HashMap<Integer , Boolean> content;
	public TextureRegion selectallTitle;
	public TextureRegion shareTitle;
	public TextureRegion settingTitle;
	public TextureRegion deleteTitle;
	public TextureRegion selectallImage;
	public TextureRegion shareImage;
	public TextureRegion settingImage;
	public TextureRegion deleteImage;
	public TextureRegion albumTitle;
	public TextureRegion artistTitle;
	public TextureRegion folderTitle;
	public TextureRegion albumImage;
	public TextureRegion artistImage;
	public TextureRegion folderImage;
	private TextureRegion naviBack;
	private TextureRegion editImage;
	private float naviBackX;
	private float naviBackY;
	private float naviBackOffsetX;
	private float naviBackWidth;
	private float naviBackHeight;
	private float editWidth;
	private float editHeight;
	public float itemWidth;
	public static NinePatch bg;
	public TextureRegion displayRegion;
	
	public BottomBar(
			String name )
	{
		super( name );
		height = R3D.bottom_bar_height;
		//y = Utils3D.getScreenHeight()-height;
		bg = new NinePatch( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/bottom-bar-bg.png" ) ) , 5 , 5 , 5 , 5 );
		content = new HashMap<Integer , Boolean>();
		content.put( SELECT_ALL , true );
		content.put( SHARE , true );
		content.put( SETTING , true );
		content.put( DELETE , true );
		content.put( ALBUM , false );
		content.put( ARTIST , false );
		content.put( FOLDER , false );
		selectallTitle = new TextureRegion( new BitmapTexture( ( AppBar3D.titleToPixmapWidthLimit(
				iLoongLauncher.getInstance().getResources().getString( RR.string.bottombar_selectall ) ,
				(int)width / 4 ,
				18 ,
				Color.WHITE ,
				true ) ) ) );
		shareTitle = new TextureRegion( new BitmapTexture( ( AppBar3D.titleToPixmapWidthLimit(
				iLoongLauncher.getInstance().getResources().getString( RR.string.bottombar_share ) ,
				(int)width / 4 ,
				18 ,
				Color.WHITE ,
				true ) ) ) );
		settingTitle = new TextureRegion( new BitmapTexture( ( AppBar3D.titleToPixmapWidthLimit(
				iLoongLauncher.getInstance().getResources().getString( RR.string.bottombar_setting ) ,
				(int)width / 4 ,
				18 ,
				Color.WHITE ,
				true ) ) ) );
		deleteTitle = new TextureRegion( new BitmapTexture( ( AppBar3D.titleToPixmapWidthLimit(
				iLoongLauncher.getInstance().getResources().getString( RR.string.bottombar_delete ) ,
				(int)width / 4 ,
				18 ,
				Color.WHITE ,
				true ) ) ) );
		albumTitle = new TextureRegion( new BitmapTexture( ( AppBar3D.titleToPixmapWidthLimit(
				iLoongLauncher.getInstance().getResources().getString( RR.string.bottombar_album ) ,
				(int)width / 4 ,
				18 ,
				Color.WHITE ,
				true ) ) ) );
		artistTitle = new TextureRegion( new BitmapTexture( ( AppBar3D.titleToPixmapWidthLimit(
				iLoongLauncher.getInstance().getResources().getString( RR.string.bottombar_artist ) ,
				(int)width / 4 ,
				18 ,
				Color.WHITE ,
				true ) ) ) );
		folderTitle = new TextureRegion( new BitmapTexture( ( AppBar3D.titleToPixmapWidthLimit(
				iLoongLauncher.getInstance().getResources().getString( RR.string.bottombar_folder ) ,
				(int)width / 4 ,
				18 ,
				Color.WHITE ,
				true ) ) ) );
		selectallImage = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/ic_menu_mark.png" ) ) );
		shareImage = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/ic_menu_share.png" ) ) );
		settingImage = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/ic_menu_set_as.png" ) ) );
		deleteImage = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/ic_menu_close_clear_cancel.png" ) ) );
		albumImage = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/ic_menu_audioalbum.png" ) ) );
		artistImage = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/ic_menu_artist.png" ) ) );
		folderImage = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/ic_menu_folder.png" ) ) );
		naviBack = R3D.findRegion( "appbar-navi-back" );
		naviBackX = R3D.appbar_tab_padding;
		naviBackY = height / 2 - R3D.appbar_menu_height / 2;
		naviBackOffsetX = 0;//R3D.appbar_menu_width / 2;
		naviBackWidth = naviBack.getRegionWidth() * Utils3D.getDensity() / 1.5f;
		naviBackHeight = naviBack.getRegionHeight() * Utils3D.getDensity() / 1.5f;
		editImage = R3D.findRegion( "ic_menu_edit" );
		editWidth = editImage.getRegionWidth() * Utils3D.getDensity() / 1.5f;
		editHeight = editImage.getRegionHeight() * Utils3D.getDensity() / 1.5f;
	}
	
	public void showAudio()
	{
		content.put( SELECT_ALL , false );
		content.put( SHARE , false );
		content.put( SETTING , false );
		content.put( DELETE , false );
		content.put( ALBUM , true );
		content.put( ARTIST , true );
		content.put( FOLDER , true );
	}
	
	public void hideAudio()
	{
		content.put( ALBUM , false );
		content.put( ARTIST , false );
		content.put( FOLDER , false );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		switch( curState )
		{
			case STATE_EDIT:
				int index = (int)( x / itemWidth );
				int id = getItemId( index );
				viewParent.onCtrlEvent( this , id );
				break;
			case STATE_DISPLAY:
				if( x <= R3D.appbar_tab_padding + R3D.appbar_menu_width + displayRegion.getRegionWidth() )
					viewParent.onCtrlEvent( this , BACK );
				else if( x >= width - 2 * R3D.appbar_tab_padding - editWidth )
				{
					viewParent.onCtrlEvent( this , EDIT );
				}
				break;
		}
		return true;
	}
	
	private int getItemId(
			int index )
	{
		int id = 0;
		int i = 0;
		Set<Entry<Integer , Boolean>> set = content.entrySet();
		Iterator<Entry<Integer , Boolean>> ite = set.iterator();
		while( ite.hasNext() )
		{
			Entry<Integer , Boolean> entry = ite.next();
			if( entry.getValue() )
			{
				if( i == index )
				{
					id = entry.getKey();
					return id;
				}
				i++;
			}
		}
		return id;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		int x = Math.round( this.x );
		int y = Math.round( this.y );
		switch( curState )
		{
			case STATE_EDIT:
				Set<Entry<Integer , Boolean>> set = content.entrySet();
				Iterator<Entry<Integer , Boolean>> ite = set.iterator();
				int i = 0;
				while( ite.hasNext() )
				{
					Entry<Integer , Boolean> entry = ite.next();
					if( entry.getValue() )
						i++;
				}
				itemWidth = width / i;
				batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
				bg.draw( batch , x , y , width , height );
				if( is3dRotation() )
					batch.draw( region , x , y , width , height );
				else
				{
					int n = 0;
					ite = set.iterator();
					while( ite.hasNext() )
					{
						Entry<Integer , Boolean> entry = ite.next();
						if( entry.getValue() )
						{
							int j = entry.getKey();
							TextureRegion title = getTitleRegion( j );
							TextureRegion image = getImageRegion( j );
							int titleX = (int)( x + itemWidth * n + ( itemWidth - title.getRegionWidth() ) / 2 );
							int titleY = (int)( ( height - title.getRegionHeight() - image.getRegionHeight() ) / 3 );
							batch.draw( title , titleX , titleY , originX , originY , title.getRegionWidth() , title.getRegionHeight() , scaleX , scaleY , rotation );
							int imageX = (int)( x + itemWidth * n + ( itemWidth - image.getRegionWidth() ) / 2 );
							int imageY = 2 * titleY + title.getRegionHeight();
							batch.draw( image , imageX , imageY , originX , originY , image.getRegionWidth() , image.getRegionHeight() , scaleX , scaleY , rotation );
							n++;
						}
					}
				}
				break;
			case STATE_DISPLAY:
				batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
				bg.draw( batch , x , y , width , height );
				batch.draw( naviBack , naviBackX + naviBackOffsetX - naviBackWidth / 2 , naviBackY + ( R3D.appbar_menu_height - naviBackHeight ) / 2 , naviBackWidth , naviBackHeight );
				batch.draw( displayRegion , R3D.appbar_tab_padding + R3D.appbar_menu_width / 2 , y + height / 3 );
				batch.draw( editImage , x + width - R3D.appbar_tab_padding - editWidth , y + ( height - editHeight ) / 2 );
				break;
		}
	}
	
	private TextureRegion getTitleRegion(
			int i )
	{
		switch( i )
		{
			case SELECT_ALL:
				return selectallTitle;
			case SHARE:
				return shareTitle;
			case SETTING:
				return settingTitle;
			case DELETE:
				return deleteTitle;
			case ALBUM:
				return albumTitle;
			case ARTIST:
				return artistTitle;
			case FOLDER:
				return folderTitle;
			default:
				return null;
		}
	}
	
	private TextureRegion getImageRegion(
			int i )
	{
		switch( i )
		{
			case SELECT_ALL:
				return selectallImage;
			case SHARE:
				return shareImage;
			case SETTING:
				return settingImage;
			case DELETE:
				return deleteImage;
			case ALBUM:
				return albumImage;
			case ARTIST:
				return artistImage;
			case FOLDER:
				return folderImage;
			default:
				return null;
		}
	}
}
