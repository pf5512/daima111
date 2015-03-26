package com.iLoong.launcher.DesktopEdit;


import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Bitmap;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.DesktopEdit.DesktopEditMenu.SingleMenu;
import com.iLoong.launcher.DesktopEdit.MessageData.VIEW_TYPE;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class MenuContainer extends ViewGroup3D
{
	
	public boolean captureTouchEvent = false;
	public TabListenner tabListenner;
	public static SingleMenu mSingleMenu;
	ArrayList<View3D> mItems;
	ArrayList<Widget3DShortcut> mWidget3D;
	NinePatch indicatorBar = null;
	MessageData message;
	MenuItemPage currPage;
	public IndicatorLine indicatorLine;
	
	public MenuContainer(
			String name )
	{
		super( name );
		this.height = R3D.pop_menu_container_height;
		this.width = Utils3D.getScreenWidth();
		Bitmap bitmap = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/backgroupd.png" );
		TextureRegion bgRegion = new TextureRegion( new BitmapTexture( bitmap ) );
		if( !bitmap.isRecycled() )
			bitmap.recycle();
		NinePatch popup_bg = new NinePatch( bgRegion , 1 , 1 , 1 , 7 );
		this.setBackgroud( popup_bg );
		mSingleMenu = new SingleMenu();
		mSingleMenu.setTitle( name );
		this.addView( mSingleMenu );
		indicatorLine = new IndicatorLine( "indicatorLine" );
		this.addView( indicatorLine );
		tabListenner = mSingleMenu;
	}
	
	class IndicatorLine extends View3D
	{
		
		public IndicatorLine(
				String name )
		{
			super( name );
			Bitmap tmp = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/line.png" );
			indicatorBar = new NinePatch( new TextureRegion( new BitmapTexture( tmp ) ) , 1 , 1 , 1 , 1 );
			if( !tmp.isRecycled() )
			{
				tmp.recycle();
			}
			this.y = R3D.pop_menu_container_height - R3D.pop_menu_title_height;
			this.x = 0;
			this.height = R3D.pop_menu_indicator_bar_height;
			this.width = Utils3D.getScreenWidth();
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			super.draw( batch , parentAlpha );
			indicatorBar.draw( batch , 0 , y , width , height );
		}
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( tabListenner == null )
		{
			return true;
		}
		if( y <= tabListenner.getEndY() && y >= tabListenner.getStartY() )
		{
			float cellW = tabListenner.getMenuWidth() / tabListenner.getMenuCount();
			for( int i = 0 ; i < tabListenner.getMenuCount() ; i++ )
			{
				if( x >= cellW * i && x <= cellW * ( i + 1 ) )
				{
					//tabListenner.onMenuClick( i );
					return true;
				}
			}
		}
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( tabListenner == null )
		{
			return true;
		}
		if( y <= tabListenner.getEndY() && y >= tabListenner.getStartY() )
		{
			float cellW = tabListenner.getMenuWidth() / tabListenner.getMenuCount();
			for( int i = 0 ; i < tabListenner.getMenuCount() ; i++ )
			{
				if( x >= cellW * i && x <= cellW * ( i + 1 ) )
				{
					tabListenner.onMenuClick( i );
					return true;
				}
			}
		}
		return super.onTouchUp( x , y , pointer );
	}
	
	public void releasePage(){
		if(currPage!=null){
			currPage.unloadPage();
		}
	}
	@SuppressWarnings( { "unchecked" } )
	public void setContent(
			MessageData message )
	{
		if( mItems != null && mItems.size() > 0 )
		{
			for( int i = 0 ; i < mItems.size() ; i++ )
			{
				mItems.get( i ).dispose();
			}
		}
		this.message = message;
		VIEW_TYPE type = message.getMessageType();
		Log.i( "VIEW_TYPE" , type + "" );
		if( type == VIEW_TYPE.TYPE_BUTTON_ICON3D )
		{
			mItems = (ArrayList<View3D>)message.getObject();
			currPage = new MenuItemPage( "page" ) {
				
				@Override
				public boolean pageInitialize()
				{
					setButtons( mItems , 4 , 2 , (float)Utils3D.getScreenWidth() , (float)R3D.pop_menu_container_height - R3D.pop_menu_title_height );
					show();
					return false;
				}
				
				@Override
				public boolean pageFinalize()
				{
					for( int i = 0 ; i < this.getCurrList().size() ; i++ )
						DesktopEditHost.disposeAllViews( this.getCurrList().get( i ) );
					return true;
				}
			};
		}
		else if( type == VIEW_TYPE.TYPE_SHORTCUT )
		{
			mItems = (ArrayList<View3D>)message.getObject();
			currPage = new MenuItemPage( "page" ) {
				
				@Override
				public boolean pageInitialize()
				{
					setButtons( mItems , 4 , 2 , (float)Utils3D.getScreenWidth() , (float)R3D.pop_menu_container_height - R3D.pop_menu_title_height );
					show();
					return false;
				}
				
				@Override
				public boolean pageFinalize()
				{
					// TODO Auto-generated method stub
					return false;
				}
			};
		}
		else if( type == VIEW_TYPE.TYPE_WIDGET3D )
		{
			mItems = (ArrayList<View3D>)message.getObject();
			currPage = new MenuItemPage( message.getTitle() ) {
				
				@Override
				public boolean pageInitialize()
				{
					setWidgets( mItems , 2 , 2 , (float)Utils3D.getScreenWidth() , (float)R3D.pop_menu_container_height - R3D.pop_menu_title_height );
					show();
					return false;
				}
				
				@Override
				public boolean pageFinalize()
				{
					if(this.currList!=null&&this.currList.size()>0){
						Iterator<View3D> ite=(Iterator<View3D>)this.currList.iterator();
						while(ite.hasNext()){
							View3D v=ite.next();
							if( iLoongApplication.BuiltIn == false )
							{
								if(v!= Desktop3DListener.otherToolsHost )
									v.releaseRegion();
							}
							else
								v.releaseRegion();
							
						}
					}
					// TODO Auto-generated method stub
					for( int i = 0 ; i < this.getCurrList().size() ; i++ )
					{
						DesktopEditHost.disposeAllViews( this.getCurrList().get( i ) );
					}
					this.getCurrList().clear();
					this.removeAllViews();
					return true;
				}
			};
			currPage.setDisposable( true );
		}
		else if( type == VIEW_TYPE.TYPE_APP )
		{
			mItems = (ArrayList<View3D>)message.getObject();
			currPage = new MenuItemPage( message.getTitle() ) {
				
				@Override
				public boolean pageInitialize()
				{
					setWidgets( mItems , 4 , 2 , (float)Utils3D.getScreenWidth() , (float)R3D.pop_menu_container_height - R3D.pop_menu_title_height );
					show();
					return false;
				}
				
				@Override
				public boolean pageFinalize()
				{
					// TODO Auto-generated method stub
					return false;
				}
			};
		}
		else
		{
			//you can add other types for your data into specified list.etc 
		}
		this.addView( currPage );
		currPage.setProgressMargin( (int)( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - ( R3D.pop_menu_container_height - 20 * iLoongLauncher.getInstance().getResources()
				.getDisplayMetrics().density ) / 2 ) );
		currPage.loadPage();
		mSingleMenu.setMessage( this.message );
	}
}
