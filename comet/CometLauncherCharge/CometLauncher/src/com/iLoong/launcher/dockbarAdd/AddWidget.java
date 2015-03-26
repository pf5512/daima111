package com.iLoong.launcher.dockbarAdd;


import java.util.ArrayList;
import java.util.List;

import android.view.KeyEvent;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Contact3DShortcut;
import com.iLoong.launcher.Widget3D.Folder3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3DVirtual;
import com.iLoong.launcher.Widget3D.WidgetMyShortcut;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.dockbarAdd.ImageButton3D.Event;
import com.iLoong.launcher.dockbarAdd.ImageTable3D.CallBack;


public class AddWidget extends ViewGroup3D
{
	
	public static final int MSG_HIDE_ADD_VIEW = 0;
	public static final int MSG_HIDE_ADD_WIDGET = 1;
	public static final int MSG_SHOW_SYSTEM_WIDGET_VIEW = 2;
	public static final int MSG_WIDGET3D_SHORTCUT_CLICK = 3;
	private List<View3D> allwidgets = null;
	private float mScale = 1f;
	private int mPaddingLeft = 50;
	private int mPaddingRight = 50;
	private int mPaddingTop = 147;
	private int mPaddingBottom = 122;// 设置gridview的间距
	private float mappGridWidth = 654;//644;//554
	private float mappGridHeight = 997;//850;//664
	WidgetAddList addAppView;
	private int mCellCountX = 2;
	private int mCellCountY = 3;
	private int pagenum = 0;
	
	public AddWidget(
			String name )
	{
		super( name );
		int mWidth = Utils3D.getScreenWidth();
		int mHeight = Utils3D.getScreenHeight();
		mScale = (float)mWidth / 720;
		final ImageTable3D imageTable3DComet = new ImageTable3D( "table comet" , R3D.getTextureRegion( R3D.dockbar_unselected_table1 ) , R3D.getTextureRegion( R3D.dockbar_selected_table1 ) );
		imageTable3DComet.region = R3D.getTextureRegion( R3D.dockbar_selected_table1 );
		imageTable3DComet.setSize( imageTable3DComet.region.getRegionWidth() * mScale , imageTable3DComet.region.getRegionHeight() * mScale );
		imageTable3DComet.setPosition( 4 * mScale , 1 * mScale );
		imageTable3DComet.setBackgroud( new NinePatch( imageTable3DComet.region ) );
		imageTable3DComet.getTitleView().region = R3D.getTextureRegion( R3D.dockbar_editmode_addwdiget_comet );
		imageTable3DComet.getTitleView().setSize( imageTable3DComet.getTitleView().region.getRegionWidth() * mScale , imageTable3DComet.getTitleView().region.getRegionHeight() * mScale );
		imageTable3DComet.getTitleView().setPosition(
				( imageTable3DComet.width - imageTable3DComet.getTitleView().width ) / 2 ,
				( imageTable3DComet.height - imageTable3DComet.getTitleView().height ) / 2 );
		addView( imageTable3DComet );
		ImageView3D addShortcutBg = new ImageView3D( "add app background" );
		addShortcutBg.region = R3D.getTextureRegion( R3D.dockbar_add_shortcut_bg );
		addShortcutBg.setSize( addShortcutBg.region.getRegionWidth() * mScale , addShortcutBg.region.getRegionHeight() * mScale );
		addShortcutBg.setPosition( 0 , imageTable3DComet.getHeight() );
		addView( addShortcutBg );
		width = addShortcutBg.getWidth();
		height = addShortcutBg.getHeight() + imageTable3DComet.getHeight();
		mappGridWidth = mappGridWidth * mScale;
		mappGridHeight = mappGridHeight * mScale;
		mPaddingTop = (int)( mPaddingTop * mScale );
		mPaddingLeft = (int)( mPaddingLeft * mScale );
		mPaddingRight = (int)( mPaddingRight * mScale );
		mPaddingBottom = (int)( mPaddingBottom * mScale );
		final ImageTable3D imageTable3DSystem = new ImageTable3D( "table system" , R3D.getTextureRegion( R3D.dockbar_unselected_table2 ) , R3D.getTextureRegion( R3D.dockbar_selected_table2 ) );
		imageTable3DSystem.region = R3D.getTextureRegion( R3D.dockbar_unselected_table2 );
		imageTable3DSystem.setSize( imageTable3DSystem.region.getRegionWidth() * mScale , imageTable3DSystem.region.getRegionHeight() * mScale );
		imageTable3DSystem.setPosition( width - imageTable3DSystem.width - 4 * mScale , 1 * mScale );
		imageTable3DSystem.setBackgroud( new NinePatch( imageTable3DSystem.region ) );
		imageTable3DSystem.getTitleView().region = R3D.getTextureRegion( R3D.dockbar_editmode_addwdiget_sysed );
		imageTable3DSystem.getTitleView().setSize( imageTable3DSystem.getTitleView().region.getRegionWidth() * mScale , imageTable3DSystem.getTitleView().region.getRegionHeight() * mScale );
		imageTable3DSystem.getTitleView().setPosition(
				( imageTable3DSystem.width - imageTable3DSystem.getTitleView().width ) / 2 ,
				( imageTable3DSystem.height - imageTable3DSystem.getTitleView().height ) / 2 );
		addView( imageTable3DSystem );
		addAppView = new WidgetAddList( "app add page" , (int)( width * 0.95 ) );
		//		addAppView.region = R3D.getTextureRegion(R3D.dockbar_add_app_bg);
		addAppView.setSize( width , height );
		//		addAppView.setBackgroud(new NinePatch(addAppView.region));
		addAppView.setPosition( 0 , 0 );
		addAppView.indicatorView.setPosition( 0 , mHeight - 140 * mScale - addAppView.indicatorView.height / 2 );
		addView( addAppView );
		ImageView3D imageView3D = new ImageView3D( "add shortcut" );
		imageView3D.region = R3D.getTextureRegion( R3D.dockbar_editmode_addwdiget_title );
		imageView3D.setSize( imageView3D.region.getRegionWidth() * mScale , imageView3D.region.getRegionHeight() * mScale );
		imageView3D.setPosition( 10 * mScale , height - 96 * mScale );
		addView( imageView3D );
		ImageButton3D buttonView3DBack = new ImageButton3D( "button back" , R3D.getTextureRegion( R3D.dockbar_add_button_back ) , R3D.getTextureRegion( R3D.dockbar_add_button_back_hilight ) );
		buttonView3DBack.region = R3D.getTextureRegion( R3D.dockbar_add_button_back );
		buttonView3DBack.setSize( buttonView3DBack.region.getRegionWidth() * mScale , buttonView3DBack.region.getRegionHeight() * mScale );
		buttonView3DBack.setPosition( 562 * mScale , height - 90 * mScale );
		buttonView3DBack.setEvent( new Event() {
			
			public void callback()
			{
				AddWidget.this.viewParent.onCtrlEvent( AddWidget.this , AddWidget.MSG_HIDE_ADD_VIEW );
			}
		} );
		addView( buttonView3DBack );
		imageTable3DComet.setEvent( new CallBack() {
			
			@Override
			public void callback()
			{
				imageTable3DComet.setBackgroud( new NinePatch( R3D.getTextureRegion( R3D.dockbar_selected_table1 ) ) );
				imageTable3DComet.getTitleView().region = R3D.getTextureRegion( R3D.dockbar_editmode_addwdiget_comet );
				imageTable3DSystem.setBackgroud( new NinePatch( R3D.getTextureRegion( R3D.dockbar_unselected_table2 ) ) );
				imageTable3DSystem.getTitleView().region = R3D.getTextureRegion( R3D.dockbar_editmode_addwdiget_sysed );
			}
		} );
		imageTable3DSystem.setEvent( new CallBack() {
			
			@Override
			public void callback()
			{
				imageTable3DSystem.setBackgroud( new NinePatch( R3D.getTextureRegion( R3D.dockbar_selected_table2 ) ) );
				imageTable3DSystem.getTitleView().region = R3D.getTextureRegion( R3D.dockbar_editmode_addwdiget_sys );
				imageTable3DComet.setBackgroud( new NinePatch( R3D.getTextureRegion( R3D.dockbar_unselected_table1 ) ) );
				imageTable3DComet.getTitleView().region = R3D.getTextureRegion( R3D.dockbar_editmode_addwdiget_cometed );
				AddWidget.this.viewParent.onCtrlEvent( AddWidget.this , AddWidget.MSG_SHOW_SYSTEM_WIDGET_VIEW );
			}
		} );
		setPosition( ( mWidth - width ) / 2 , mHeight - height - 25 * mScale );
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				startapp();
			}
		} );
		//		width=mWidth;
		//		height=mHeight;
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
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
			viewParent.onCtrlEvent( this , AddWidget.MSG_HIDE_ADD_VIEW );
			return true;
		}
		return super.keyUp( keycode );
	}
	
	public void removelist()
	{
		allwidgets.retainAll( allwidgets );
		//		removeView(AppHost3D.appList);
		//		AppHost3D.appBar.addView(AppHost3D.appList);
	}
	
	static float view_width = 0 , view_height = 0;
	
	public void startapp()
	{
		allwidgets = getAllWidget();
		int allsize = allwidgets.size();
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
					if( view_width == 0 || view_height == 0 )
					{
						view_width = allwidgets.get( j ).getWidth() * 0.8f;
						view_height = allwidgets.get( j ).getHeight() * 0.70f;
					}
					allwidgets.get( j ).setSize( view_width , view_height );
					appGridView.addItem( allwidgets.get( j ) );
				}
			}
			else if( i == page - 1 )
			{
				View3D widget = null;
				for( int j = mCellCountX * mCellCountY * i ; j < allsize ; j++ )
				{
					//					if( view_width == 0 || view_height == 0 )
					//					{
					//						view_width = allwidgets.get( j ).getWidth() * 0.8f;
					//						view_height = allwidgets.get( j ).getHeight() * 0.70f;
					//					}
					//					allwidgets.get( j ).setSize( view_width , view_height );
					widget = allwidgets.get( j );
					if( widget instanceof Widget3DVirtual )
					{
						Widget3DVirtual widgetIcon = (Widget3DVirtual)widget;
						if( widgetIcon.uninstalled )
						{
							allwidgets.remove( widgetIcon );
							continue;
						}
						widgetIcon.setSize( appGridView.getCellWidth() , appGridView.getCellHeight() );
						widgetIcon.makeShortcut();
						widgetIcon.oldX = widgetIcon.x;
						widgetIcon.oldY = widgetIcon.y;
					}
					else if( widget instanceof Widget3DShortcut )
					{
						Widget3DShortcut widgetShortcut = (Widget3DShortcut)widget;
						widgetShortcut.setSize( appGridView.getCellWidth() , appGridView.getCellHeight() );
						widgetShortcut.makeShortcut();
						widgetShortcut.oldX = widgetShortcut.x;
						widgetShortcut.oldY = widgetShortcut.y;
					}
					appGridView.addItem( allwidgets.get( j ) );
				}
			}
			addAppView.addPage( i , appGridView );
		}
		pagenum = page;
	}
	
	private List<View3D> getAllWidget()
	{
		List<View3D> allApps = new ArrayList<View3D>();
		List<Widget3DShortcut> mWidgetList = Widget3DManager.getInstance().getWidgetList();
		//		for (int i = 0; i < mWidgetList.size(); i++) {
		//			Widget3DShortcut view = mWidgetList.get(i);
		//			allApps.add(view);
		//		}
		List<View3D> allwidgets = AppHost3D.appList.mWidget3DList;
		for( int j = 0 ; j < allwidgets.size() ; j++ )
		{
			if( allwidgets.get( j ) instanceof Folder3DShortcut )
			{
			}
			else if( allwidgets.get( j ) instanceof Contact3DShortcut )
			{
			}
			else if( allwidgets.get( j ) instanceof Widget3DVirtual )
			{
				allApps.add( allwidgets.get( j ) );
			}
			else if( allwidgets.get( j ) instanceof Widget3DShortcut )
			{
				WidgetMyShortcut widgetmy = new WidgetMyShortcut( "newWidget" );
				widgetmy.resolve_info = ( (Widget3DShortcut)allwidgets.get( j ) ).resolve_info;
				widgetmy.packageName = ( (Widget3DShortcut)allwidgets.get( j ) ).packageName;
				widgetmy.setSize( allwidgets.get( j ).width , allwidgets.get( j ).height );
				widgetmy.makeShortcut();
				widgetmy.oldX = widgetmy.x;
				widgetmy.oldY = widgetmy.y;
				allApps.add( widgetmy );
			}
		}
		return allApps;
	}
}
