package com.iLoong.launcher.dockbarAdd;


import java.util.ArrayList;
import java.util.List;

import android.view.KeyEvent;

import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.dockbarAdd.ImageButton3D.Event;


public class AddApp extends ViewGroup3D
{
	
	public static final int MSG_HIDE_ADD_VIEW = 0;
	private AppAddList addAppView = null;
	private static AddApp addApp = null;
	private List<View3D> allapps = null;
	private int mCellCountX = 3;
	private int mCellCountY = 4;
	private int pagenum = 0;
	private int mPaddingLeft = 80;
	private int mPaddingRight = 80;
	private int mPaddingTop = 130;
	private int mPaddingBottom = 130;// 设置gridview的间距
	private float mappGridWidth = 654;// 644;//554
	private float mappGridHeight = 997;// 850;//664
	private float mScale = 1f;
	
	public AddApp(
			String name )
	{
		super( name );
		int mWidth = Utils3D.getScreenWidth();
		int mHeight = Utils3D.getScreenHeight();
		mScale = (float)mWidth / 720;
		addApp = this;
		ImageView3D appAddBg = new ImageView3D( "add app background" );
		appAddBg.region = R3D.getTextureRegion( R3D.dockbar_add_app_bg );
		appAddBg.setSize( appAddBg.region.getRegionWidth() * mScale , appAddBg.region.getRegionHeight() * mScale );
		appAddBg.setPosition( ( this.width - appAddBg.getWidth() ) / 2 , 0 );
		addView( appAddBg );
		height = appAddBg.getHeight();
		mappGridWidth = Utils3D.getScreenWidth();
		mappGridHeight = mappGridHeight * mScale;
		mPaddingTop = (int)( mPaddingTop * mScale );
		mPaddingLeft = (int)( mPaddingLeft * mScale );
		mPaddingRight = (int)( mPaddingRight * mScale );
		mPaddingBottom = (int)( mPaddingBottom * mScale );
		addAppView = new AppAddList( "app add page" , (int)( appAddBg.getWidth() - 100 * mScale ) );
		addAppView.setSize( width , height );
		addAppView.setPosition( 0 , 0 );
		addAppView.indicatorView.setPosition( ( Utils3D.getScreenWidth() - addAppView.indicatorView.width ) / 2 , mHeight - 140 * mScale - addAppView.indicatorView.height / 2 );
		addView( addAppView );
		ImageView3D imageView3D = new ImageView3D( "add application" );
		imageView3D.region = R3D.getTextureRegion( R3D.dockbar_add_applications );
		imageView3D.setSize( imageView3D.region.getRegionWidth() * mScale , imageView3D.region.getRegionHeight() * mScale );
		imageView3D.setPosition( 50 * mScale , height - 96 * mScale );
		addView( imageView3D );
		ImageButton3D buttonView3DBack = new ImageButton3D( "button back" , R3D.getTextureRegion( R3D.dockbar_add_button_back ) , R3D.getTextureRegion( R3D.dockbar_add_button_back_hilight ) );
		buttonView3DBack.region = R3D.getTextureRegion( R3D.dockbar_add_button_back );
		buttonView3DBack.setSize( buttonView3DBack.region.getRegionWidth() * mScale , buttonView3DBack.region.getRegionHeight() * mScale );
		buttonView3DBack.setPosition( 602 * mScale , height - 90 * mScale );
		buttonView3DBack.setEvent( new Event() {
			
			public void callback()
			{
				CooeeIcon3D.icons.clear();
				addApp.viewParent.onCtrlEvent( addApp , AddApp.MSG_HIDE_ADD_VIEW );
			}
		} );
		addView( buttonView3DBack );
		ImageButton3D buttonView3DDone = new ImageButton3D( "button done" , R3D.getTextureRegion( R3D.dockbar_add_button_done ) , R3D.getTextureRegion( R3D.dockbar_add_button_done_hilight ) );
		buttonView3DDone.region = R3D.getTextureRegion( R3D.dockbar_add_button_done );
		buttonView3DDone.setSize( buttonView3DDone.region.getRegionWidth() * mScale , buttonView3DDone.region.getRegionHeight() * mScale );
		buttonView3DDone.setPosition( width * 3 / 4 - buttonView3DDone.width / 2 , 45 * mScale );
		buttonView3DDone.setEvent( new Event() {
			
			public void callback()
			{
				Workspace3D.getInstance().getSelectApp( CooeeIcon3D.icons );
				Workspace3D.getInstance().addToDesk();
				CooeeIcon3D.icons.clear();
				addApp.viewParent.onCtrlEvent( addApp , AddApp.MSG_HIDE_ADD_VIEW );
			}
		} );
		addView( buttonView3DDone );
		ImageButton3D buttonView3DCancel = new ImageButton3D( "button cancel" , R3D.getTextureRegion( R3D.dockbar_add_button_cancel ) , R3D.getTextureRegion( R3D.dockbar_add_button_cancel_hilight ) );
		buttonView3DCancel.region = R3D.getTextureRegion( R3D.dockbar_add_button_cancel );
		buttonView3DCancel.setSize( buttonView3DCancel.region.getRegionWidth() * mScale , buttonView3DCancel.region.getRegionHeight() * mScale );
		buttonView3DCancel.setPosition( width / 4 - buttonView3DDone.width / 2 , 45 * mScale );
		buttonView3DCancel.setEvent( new Event() {
			
			public void callback()
			{
				CooeeIcon3D.icons.clear();
				addApp.viewParent.onCtrlEvent( addApp , AddApp.MSG_HIDE_ADD_VIEW );
			}
		} );
		addView( buttonView3DCancel );
		setPosition( ( mWidth - width ) / 2 , mHeight - height - 25 * mScale );
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				startapp();
			}
		} );
		setSize( mWidth , mHeight );
	}
	
	public void startapp()
	{
		allapps = getAllApp();
		int allsize = allapps.size();
		int page = 0;
		if( ( allsize % ( mCellCountX * mCellCountY ) ) == 0 )
		{
			page = allsize / ( mCellCountX * mCellCountY );
		}
		else
		{
			page = allsize / ( mCellCountX * mCellCountY ) + 1;
		}
		if( addAppView != null && pagenum > 0 )
		{
			addAppView.removeAllChild( pagenum );
		}
		Log.v( "dzapp" , "allsize is " + allsize + " page is " + page );
		for( int i = 0 ; i < page ; i++ )
		{
			GridView3D appGridView = new GridView3D( "" , mappGridWidth , mappGridHeight , mCellCountX , mCellCountY );
			appGridView.setPadding( mPaddingLeft , mPaddingRight , mPaddingTop , mPaddingBottom );
			appGridView.setPosition( 0 , 0 );
			appGridView.transform = true;
			appGridView.enableAnimation( false );
			appGridView.setAutoDrag( false );
			if( i < page - 1 )
			{
				for( int j = mCellCountX * mCellCountY * i ; j < mCellCountX * mCellCountY * ( i + 1 ) ; j++ )
				{
					appGridView.addItem( allapps.get( j ) );
				}
			}
			else if( i == page - 1 )
			{
				for( int j = mCellCountX * mCellCountY * i ; j < allsize ; j++ )
				{
					appGridView.addItem( allapps.get( j ) );
				}
			}
			addAppView.addPage( i , appGridView );
		}
		pagenum = page;
	}
	
	private List<View3D> getAllApp()
	{
		List<View3D> allApps = new ArrayList<View3D>();
		CooeeIcon3D cooeeicon = null;
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
								if( icon != null )
								{
									// if ( CooeeMainScene.getInstance().iconV2
									// == 0){
									// CooeeMainScene.getInstance().iconV2 =
									// icon.region.getV2();
									// }
									cooeeicon = new CooeeIcon3D( icon.name , icon.region );
									cooeeicon.setItemInfo( new ShortcutInfo( (ShortcutInfo)icon.getItemInfo() ) );
									cooeeicon.setSize( icon.getWidth() , icon.getHeight() );
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
						if( icon != null )
						{
							cooeeicon = new CooeeIcon3D( icon.name , icon.region );
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
	
	public static AddApp getInstance()
	{
		return addApp;
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK )
			return true;
		return super.keyDown( keycode );
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			CooeeIcon3D.icons.clear();
			viewParent.onCtrlEvent( this , AddApp.MSG_HIDE_ADD_VIEW );
			return true;
		}
		return super.keyUp( keycode );
	}
}
