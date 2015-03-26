package com.iLoong.launcher.DesktopEdit;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.DesktopEdit.DesktopEditMenu.SingleMenu;
import com.iLoong.launcher.DesktopEdit.MessageData.VIEW_TYPE;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.FolderInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
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
	private Object applistLock = new Object();
	private boolean resourceLoaded = false;
	public int currentPageCode = 0;//说明没有设置界面
	public static int WIDGETPAGE = 1;
	public static int SHORTCUTPAGE = 2;
	public static int APPPAGE = 3;
	
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
					// tabListenner.onMenuClick( i );
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
	
	public void releasePage()
	{
		if( currPage != null )
		{
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
			currentPageCode = APPPAGE;
			if( !iLoongApplication.getInstance().getModel().appListLoaded )
			{
				int marginTop = (int)( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - ( R3D.pop_menu_container_height - 0 * iLoongLauncher.getInstance().getResources().getDisplayMetrics().density ) / 2 );
				SendMsgToAndroid.showCustomDialog( (int)( Utils3D.getScreenWidth() - 40 * iLoongLauncher.getInstance().getResources().getDisplayMetrics().density ) / 2 , marginTop );
			}
			currPage = new MenuItemPage( "page" ) {
				
				@Override
				public boolean pageInitialize()
				{
					new Thread( new Runnable() {
						
						@Override
						public void run()
						{
							iLoongApplication.getInstance().getModel().loadAppList();
							SendMsgToAndroid.cancelCustomDialog();
							iLoongLauncher.getInstance().postRunnable( new Runnable() {
								
								@Override
								public void run()
								{
									mItems = (ArrayList<View3D>)( (Object)getAllApp() );
									setButtons( mItems , 4 , 2 , (float)Utils3D.getScreenWidth() , (float)R3D.pop_menu_container_height - R3D.pop_menu_title_height );
									show();
									loadinited = true;
								}
							} );
						}
					} ).start();
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
			currentPageCode = SHORTCUTPAGE;
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
			currentPageCode = WIDGETPAGE;
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
					if( this.currList != null && this.currList.size() > 0 )
					{
						Iterator<View3D> ite = (Iterator<View3D>)this.currList.iterator();
						while( ite.hasNext() )
						{
							View3D v = ite.next();
							if( iLoongApplication.BuiltIn == false )
							{
								if( v != Desktop3DListener.otherToolsHost )
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
			// you can add other types for your data into specified list.etc
		}
		this.addView( currPage );
		currPage.setProgressMargin( (int)( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - ( R3D.pop_menu_container_height - 20 * iLoongLauncher.getInstance().getResources()
				.getDisplayMetrics().density ) / 2 ) );
		currPage.loadPage();
		mSingleMenu.setMessage( this.message );
	}
	
	public void onFreshPage(
			int code )
	{
		if( currPage != null && currentPageCode == code )
		{
			currPage.loadinited = false;
			currPage.loadPage();
		}
	}
	
	public static List<AppIcon3D> getAllApp()
	{
		List<AppIcon3D> allApps = new ArrayList<AppIcon3D>();
		AppIcon3D cooeeicon = null;
		if( AppHost3D.appList.mApps != null )
		{
			if( AppHost3D.appList.mApps.size() == 0 )
			{
				if( AppHost3D.appList.mItemInfos != null && AppHost3D.appList.mItemInfos.size() > 0 )
				{
					for( int i = 0 ; i < AppHost3D.appList.mItemInfos.size() ; i++ )
					{
						if( AppHost3D.appList.mItemInfos.get( i ) instanceof ApplicationInfo )
						{
							ApplicationInfo info = (ApplicationInfo)AppHost3D.appList.mItemInfos.get( i );
							if( info.intent.getComponent() != null )
							{
								ShortcutInfo sInfo = info.makeShortcut();
								String appName = R3D.getInfoName( sInfo );
								Icon3D icon = AppHost3D.appList.iconMap.get( appName );
								if( icon != null && !icon.getHideStatus() )
								{
									cooeeicon = new AppIcon3D( icon.name , icon.region );
									cooeeicon.setItemInfo( new ShortcutInfo( (ShortcutInfo)icon.getItemInfo() ) );
									cooeeicon.setSize( icon.getWidth() , icon.getHeight() );
									allApps.add( cooeeicon );
								}
							}
						}
						else if( AppHost3D.appList.mItemInfos.get( i ) instanceof FolderInfo )
						{
							FolderInfo info = (FolderInfo)AppHost3D.appList.mItemInfos.get( i );
							ArrayList<ShortcutInfo> contents = ( (UserFolderInfo)info ).contents;
							if( contents != null && contents.size() > 0 )
							{
								for( int j = 0 ; j < contents.size() ; j++ )
								{
									ShortcutInfo sInfo = contents.get( j );
									String appName = R3D.getInfoName( sInfo );
									cooeeicon = new AppIcon3D( appName , R3D.findRegion( sInfo ) );
									cooeeicon.setSize( R3D.workspace_cell_width , R3D.workspace_cell_height );
									cooeeicon.setItemInfo( sInfo );
									allApps.add( cooeeicon );
								}
							}
						}
					}
				}
			}
			else
			{
				for( int i = 0 ; i < AppHost3D.appList.mApps.size() ; i++ )
				{
					ApplicationInfo info = AppHost3D.appList.mApps.get( i );
					if( info.intent.getComponent() != null )
					{
						ShortcutInfo sInfo = info.makeShortcut();
						String appName = R3D.getInfoName( sInfo );
						Icon3D icon = AppHost3D.appList.iconMap.get( appName );
						if( icon != null && !icon.getHideStatus() )
						{
							cooeeicon = new AppIcon3D( icon.name , icon.region );
							cooeeicon.setItemInfo( new ShortcutInfo( (ShortcutInfo)icon.getItemInfo() ) );
							cooeeicon.setSize( icon.getWidth() , icon.getHeight() );
							allApps.add( cooeeicon );
						}
					}
				}
			}
		}
		return allApps;
	}
}
