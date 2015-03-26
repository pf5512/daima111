package com.iLoong.launcher.DesktopEdit;


import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Bitmap;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class DesktopEditMenu extends ViewGroup3D implements TabListenner
{
	
	TabChangeListenner tabChangeListenner;
	public static SingleMenu singleMenu;
	public MenuTitle menuTitle;
	public static MenuIndicatorHost bottomInd;
	public MenuIndicatorHost mIndicator;
	public ArrayList<MenuItemPage> pages;
	
	public DesktopEditMenu(
			String name )
	{
		super( name );
		x = 0;
		y = R3D.pop_menu_height - R3D.pop_menu_title_height;
		width = Utils3D.getScreenWidth();
		height = R3D.pop_menu_title_height;
		this.originY = height / 2;
		this.transform = true;
		setIndicator();
		setTitle();
	}
	
	public static SingleMenu getSingleInstance()
	{
		return singleMenu;
	}
	
	public static MenuIndicatorHost getBottomInd()
	{
		return bottomInd;
	}
	
	public void setSingleTitle(
			String title )
	{
		singleMenu.setTitle( title );
	}
	
	public void setPages(
			ArrayList<MenuItemPage> pages )
	{
		this.pages = pages;
	}
	
	public void setTitle()
	{
		menuTitle = new MenuTitle();
		this.addView( menuTitle );
	}
	
	public void setIndicator()
	{
		bottomInd = new MenuIndicatorHost();
		mIndicator = new MenuIndicatorHost();
		setTabListenner( mIndicator );
		this.addView( mIndicator );
	}
	
	public void onTabChange()
	{
		tabChangeListenner.onTabChange( 0 );
	}
	
	public void setTabListenner(
			TabChangeListenner listenner )
	{
		this.tabChangeListenner = listenner;
	}
	
	class MenuIndicatorHost extends ViewGroup3D implements TabChangeListenner
	{
		
		Indicator indicator;
		IndicatorBar indicatorBar;
		
		public MenuIndicatorHost()
		{
			this.height = R3D.pop_menu_indicator_bar_height + R3D.pop_menu_indicator_height;
			this.width = Utils3D.getScreenWidth();
			indicatorBar = new IndicatorBar( "indicator" );
			indicatorBar.setSize( width , R3D.pop_menu_indicator_bar_height );
			indicatorBar.barHeight = R3D.pop_menu_indicator_bar_height;
			indicator = new Indicator( "indicator" );
			this.addView( indicator );
			this.addView( indicatorBar );
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			super.draw( batch , parentAlpha );
		}
		
		public Indicator getIndicator()
		{
			return indicator;
		}
		
		public void setBarProperty(
				float indHeight ,
				float indWidth ,
				float barHeight )
		{
			indicatorBar.barHeight = barHeight;
			indicatorBar.setSize( Utils3D.getScreenWidth() , barHeight );
			indicatorBar.y = indHeight;
			indicator.setSize( indWidth , indHeight );
			indicator.onTabChange( 0 );
		}
		
		public void setBottomIndChange(
				int index ,
				float cellWidth )
		{
			indicator.onTabChange( index , cellWidth );
		}
		
		public void setBottomIndWidth(
				float width )
		{
			indicator.width = width;
		}
		
		public void setBarHeight(
				float height )
		{
			indicatorBar.barHeight = height;
		}
		
		public void onTabChange()
		{
		}
		
		class Indicator extends View3D
		{
			
			NinePatch indicator;
			
			public Indicator(
					String name )
			{
				super( name );
				TextureRegion tabLine;
				Bitmap tmp = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/indicator.png" );
				indicator = new NinePatch( new TextureRegion( new BitmapTexture( tmp ) ) , 1 , 1 , 1 , 1 );
				this.height = R3D.pop_menu_indicator_height;
				this.width = getMenuWidth() / getMenuCount();
				if( !tmp.isRecycled() )
				{
					tmp.recycle();
				}
				this.setBackgroud( indicator );
			}
			
			public void onTabChange(
					Object obj )
			{
				this.x = (Integer)obj * ( getMenuWidth() / getMenuCount() );
			}
			
			public void onTabChange(
					int index ,
					float cellWidth )
			{
				this.x = index * ( cellWidth );
			}
		}
		
		public class IndicatorBar extends View3D
		{
			
			NinePatch indicatorBar;
			private float barHeight;
			
			private IndicatorBar(
					String name )
			{
				super( name );
				TextureRegion tabLine;
				Bitmap tmp = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/bottom_indicator_bg.png" );
				indicatorBar = new NinePatch( new TextureRegion( new BitmapTexture( tmp ) ) , 1 , 1 , 1 , 1 );
				if( !tmp.isRecycled() )
				{
					tmp.recycle();
				}
			}
			
			@Override
			public void draw(
					SpriteBatch batch ,
					float parentAlpha )
			{
				super.draw( batch , parentAlpha );
				indicatorBar.draw( batch , 0 , y , width , barHeight );
			}
		}
		
		@Override
		public void onTabChange(
				Object obj )
		{
			indicator.onTabChange( obj );
		}
	}
	
	class MenuTitle extends ViewGroup3D
	{
		
		int highlight = 0;
		float menuWidth = getMenuWidth() / getMenuCount();
		TextureRegion addWidget;
		TextureRegion theme;
		TextureRegion wallpaper;
		TextureRegion effect;
		
		public MenuTitle()
		{
			this.x = 0;
			this.y = R3D.pop_menu_indicator_bar_height + R3D.pop_menu_indicator_height;
			this.height = R3D.pop_menu_title_height;
			this.width = Utils3D.getScreenWidth();
			addWidget = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmapNew(
					iLoongLauncher.getInstance().getString( RR.string.title_tab_add ) ,
					(int)R3D.appbar_height ,
					false ,
					R3D.pop_menu_title_color ,
					false ) , true ) );// R3D.findRegion("appbar-tab-widget");
			theme = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmapNew(
					iLoongLauncher.getInstance().getString( RR.string.title_tab_theme ) ,
					(int)R3D.appbar_height ,
					false ,
					R3D.pop_menu_title_color ,
					false ) , true ) );// R3D.findRegion("appbar-tab-widget");
			wallpaper = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmapNew(
					iLoongLauncher.getInstance().getString( RR.string.title_tab_wallpaper ) ,
					(int)R3D.appbar_height ,
					false ,
					R3D.pop_menu_title_color ,
					false ) , true ) );// R3D.findRegion("appbar-tab-widget");
			effect = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmapNew(
					iLoongLauncher.getInstance().getString( RR.string.title_tab_effect ) ,
					(int)R3D.appbar_height ,
					false ,
					R3D.pop_menu_title_color ,
					false ) , true ) );// R3D.findRegion("appbar-tab-widget");
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			super.draw( batch , parentAlpha );
			batch.draw( addWidget , x + ( menuWidth - addWidget.getRegionWidth() ) / 2 , ( height - addWidget.getRegionHeight() ) / 2 );
			batch.draw( theme , x + ( 3 * menuWidth - theme.getRegionWidth() ) / 2 , ( height - theme.getRegionHeight() ) / 2 );
			batch.draw( wallpaper , x + ( 5 * menuWidth - wallpaper.getRegionWidth() ) / 2 , ( height - wallpaper.getRegionHeight() ) / 2 );
			batch.draw( effect , x + ( 7 * menuWidth - effect.getRegionWidth() ) / 2 , ( height - effect.getRegionHeight() ) / 2 );
		}
		
		public void onTabChange(
				int position )
		{
		}
	}
	
	public static class SingleMenu extends ViewGroup3D implements TabListenner
	{
		
		public final static int EVENT_BACK = 0;
		private TextureRegion back;
		private TextureRegion title;
		public MessageData msg;
		
		public SingleMenu()
		{
			this.x = 0;
			this.y = R3D.pop_menu_container_height - ( R3D.pop_menu_title_height );
			this.height = R3D.pop_menu_title_height;
			this.width = Utils3D.getScreenWidth() / 4;
			try
			{
				Bitmap bmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/desktopEdit/back.png" ) );
				bmp = Tools.resizeBitmap( bmp , Utils3D.getScreenWidth() / 720f );
				this.back = new TextureRegion( new BitmapTexture( bmp , true ) );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		
		public void setTitle(
				String title )
		{
			if( this.title != null && this.title.getTexture() != null )
			{
				this.title.getTexture().dispose();
			}
			this.title = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( title , (int)R3D.appbar_height , false , R3D.pop_menu_title_color , false ) , true ) );
			this.width = this.title.getRegionWidth() + 10;
			this.show();
		}
		
		public void setMessage(
				MessageData msg )
		{
			this.msg = msg;
		}
		
		@Override
		public boolean fling(
				float velocityX ,
				float velocityY )
		{
			return true;
		}
		
		@Override
		public boolean scroll(
				float x ,
				float y ,
				float deltaX ,
				float deltaY )
		{
			return true;
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			super.draw( batch , parentAlpha );
			batch.draw( this.title , x + this.back.getRegionWidth() , y + ( height - this.title.getRegionHeight() ) / 2 );
			batch.draw( this.back , x + ( this.width - this.title.getRegionWidth() ) / 2 , y + ( height - this.back.getRegionHeight() ) / 2 );
		}
		
		@Override
		public void onMenuClick(
				int Position )
		{
			this.setTag( this.msg );
			viewParent.onCtrlEvent( this , EVENT_BACK );
			Log.v( "PopupMenu" , "onMenuClick" );
		}
		
		@Override
		public float getStartX()
		{
			return x;
		}
		
		@Override
		public float getStartY()
		{
			return y;
		}
		
		@Override
		public float getEndX()
		{
			return x + width;
		}
		
		@Override
		public float getEndY()
		{
			return y + height;
		}
		
		@Override
		public int getMenuCount()
		{
			return 1;
		}
		
		@Override
		public float getMenuWidth()
		{
			return Utils3D.getScreenWidth();
		}
		
		@Override
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			return true;
		}
		
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			return true;
		}
	}
	
	@Override
	public void onMenuClick(
			int position )
	{
		Log.v( "PopupMenu" , "position : " + position );
		if(!ifCanClick){
			return;
		}
		for( int i = 0 ; i < this.getChildCount() ; i++ )
		{
			if( getChildAt( i ) instanceof TabChangeListenner )
			{
				TabChangeListenner listener = (TabChangeListenner)getChildAt( i );
				listener.onTabChange( position );
			}
		}
		for( int i = 0 ; i < pages.size() ; i++ )
		{
			if( i == position )
			{
				pages.get( i ).loadPage();
			}
			else
				pages.get( i ).unloadPage();
		}
	}
	
	public static boolean ifCanClick = true;
	
	public void onMenuFresh(
			int position )
	{
		Log.v( "PopupMenu" , "position : " + position );
		//		for( int i = 0 ; i < this.getChildCount() ; i++ )
		//		{
		//			if( getChildAt( i ) instanceof TabChangeListenner )
		//			{
		//				TabChangeListenner listener = (TabChangeListenner)getChildAt( i );
		//				listener.onTabChange( position );
		//			}
		//		}
		ifCanClick = false;
		for( int i = 0 ; i < pages.size() ; i++ )
		{
			pages.get( i ).unloadPage();
		}
		pages.get( position ).loadWallpaperPage();
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public int getMenuCount()
	{
		return 4;
	}
	
	@Override
	public float getMenuWidth()
	{
		return width;
	}
	
	@Override
	public float getStartX()
	{
		return x;
	}
	
	@Override
	public float getStartY()
	{
		return y;
	}
	
	@Override
	public float getEndX()
	{
		return x + width;
	}
	
	@Override
	public float getEndY()
	{
		return y + height;
	}
}
