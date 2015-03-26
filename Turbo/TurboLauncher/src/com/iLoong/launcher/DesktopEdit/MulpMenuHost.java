package com.iLoong.launcher.DesktopEdit;


import java.util.ArrayList;

import android.graphics.Bitmap;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.DesktopEdit.DesktopEditMenu.SingleMenu;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.umeng.analytics.MobclickAgent;


public class MulpMenuHost extends ViewGroup3D
{
	
	public final static int TAB_CHANGED_EVENT = 0;
	public TabListenner tabListenner;
	public boolean captureTouchEvent = false;
	ArrayList<Icon3D> menuItems = new ArrayList<Icon3D>();
	public DesktopEditMenuItem editMenuItem;
	public DesktopEditMenu editMenu;
	SingleMenu singleMenu;
	View3D bottomView3D;
	public NinePatch popup_bg;
	public NinePatch tab_bg;
	public static int menuBarHeight;
	Bitmap middleImgView;
	
	public MulpMenuHost(
			String name )
	{
		super( name );
		this.x = 0;
		this.y = 0;
		this.width = Utils3D.getScreenWidth();
		this.height = R3D.pop_menu_height;
		tab_bg = new NinePatch( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/desktopEdit/tab_bg.png" ) , true ) );
		initSurface();
		setTabListenner( editMenu );
	}
	
	public void setTabListenner(
			TabListenner listenner )
	{
		tabListenner = listenner;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		super.draw( batch , parentAlpha );
		//popup_bg.draw( batch , 0 , -R3D.workspace_offset_low_y * 2 , Utils3D.getScreenWidth() , R3D.workspace_offset_low_y * 2 );
		//		tab_bg.draw(
		//				batch ,
		//				0 ,
		//				R3D.pop_menu_height - R3D.pop_menu_title_height ,
		//				Utils3D.getScreenWidth() ,
		//				R3D.pop_menu_title_height + R3D.pop_menu_indicator_bar_height + R3D.pop_menu_indicator_height );
	}
	
	public void initSurface()
	{
		Bitmap bitmap = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/backgroupd.png" );
		TextureRegion bgRegion = new TextureRegion( new BitmapTexture( bitmap ) );
		if( !bitmap.isRecycled() )
			bitmap.recycle();
		popup_bg = new NinePatch( bgRegion , 1 , 1 , 1 , 7 );
		this.setBackgroud( popup_bg );
		editMenu = new DesktopEditMenu( "desktopEditMenu" );
		editMenuItem = new DesktopEditMenuItem( "desktopEditMenuItem" );
		editMenu.setPages( editMenuItem.getPopPages() );
		bottomView3D = new View3D( "bottomView3D" );
		bottomView3D.setSize( Utils3D.getScreenWidth() , R3D.workspace_offset_low_y * 2 );
		bottomView3D.setPosition( 0 , -R3D.workspace_offset_low_y * 2 );
		bottomView3D.setBackgroud( popup_bg );
		//singleMenu = DesktopEditMenu.getSingleInstance();
		this.addView( editMenu );
		this.addView( editMenuItem );
		this.addView( bottomView3D );
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
			//return super.onTouchDown( x , y , pointer );
		}
		if( y <= tabListenner.getEndY() && y >= tabListenner.getStartY() )
		{
			float cellW = tabListenner.getMenuWidth() / tabListenner.getMenuCount();
			for( int i = 0 ; i < tabListenner.getMenuCount() ; i++ )
			{
				switch( i )
				{
					case 0:
						MobclickAgent.onEvent( iLoongLauncher.getInstance() , "EditModelMenuAdd" );
						break;
					case 1:
						MobclickAgent.onEvent( iLoongLauncher.getInstance() , "EditModelMenuTheme" );
						break;
					case 2:
						MobclickAgent.onEvent( iLoongLauncher.getInstance() , "EditModelMenuWallpaper" );
						break;
					case 3:
						MobclickAgent.onEvent( iLoongLauncher.getInstance() , "EditModelMenuEffect" );
						break;
				}
				if( x >= cellW * i && x <= cellW * ( i + 1 ) )
				{
					tabListenner.onMenuClick( i );
					captureTouchEvent = true;
					return true;
				}
			}
		}
		captureTouchEvent = false;
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( captureTouchEvent )
			return true;
		else
			return super.onTouchUp( x , y , pointer );
	}
	
	public void MenuCallBack(
			int position )
	{
		viewParent.onCtrlEvent( this , TAB_CHANGED_EVENT );
		if( tabListenner != null )
			tabListenner.onMenuClick( position );
	}
}
