package com.iLoong.launcher.dockbarAdd;


import java.util.ArrayList;
import java.util.List;
import android.view.KeyEvent;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.WidgetIcon;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.dockbarAdd.ImageButton3D.Event;
import com.iLoong.launcher.dockbarAdd.ImageTable3D;
import com.iLoong.launcher.dockbarAdd.ImageTable3D.CallBack;


public class AddShortcut extends ViewGroup3D
{
	
	public static final int MSG_HIDE_ADD_VIEW = 0;
	public static final int MSG_SHOW_SYSTEM_SHORTCUT_VIEW = 1;
	private AddShortcutList addCometShortcut = null;
	private AddShortcutList addSystemShortcut = null;
	private static AddShortcut addShortcut = null;
	private List<View3D> allapps = null;
	private float mScale = 1f;
	private int mPaddingLeft = 50;
	private int mPaddingRight = 50;
	private int mPaddingTop = 130;
	private int mPaddingBottom = 130;// 设置gridview的间距
	private float mappGridWidth = 654;//644;//554
	private float mappGridHeight = 997;//850;//664
	private int mCellCountX = 3;
	private int mCellCountY = 4;
	private int pagenum = 0;
	
	public AddShortcut(
			String name )
	{
		super( name );
		int mWidth = Utils3D.getScreenWidth();
		int mHeight = Utils3D.getScreenHeight();
		mScale = (float)mWidth / 720;
		addShortcut = this;
		final ImageTable3D imageTable3DComet = new ImageTable3D( "table comet" , R3D.getTextureRegion( R3D.dockbar_unselected_table1 ) , R3D.getTextureRegion( R3D.dockbar_selected_table1 ) );
		imageTable3DComet.region = R3D.getTextureRegion( R3D.dockbar_selected_table1 );
		imageTable3DComet.setSize( imageTable3DComet.region.getRegionWidth() * mScale , imageTable3DComet.region.getRegionHeight() * mScale );
		imageTable3DComet.setPosition( 4 * mScale , 1 * mScale );
		imageTable3DComet.setBackgroud( new NinePatch( imageTable3DComet.region ) );
		imageTable3DComet.getTitleView().region = R3D.getTextureRegion( R3D.dockbar_comet_shortcut_hilight );
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
		imageTable3DSystem.getTitleView().region = R3D.getTextureRegion( R3D.dockbar_system_shortcut );
		imageTable3DSystem.getTitleView().setSize( imageTable3DSystem.getTitleView().region.getRegionWidth() * mScale , imageTable3DSystem.getTitleView().region.getRegionHeight() * mScale );
		imageTable3DSystem.getTitleView().setPosition(
				( imageTable3DSystem.width - imageTable3DSystem.getTitleView().width ) / 2 ,
				( imageTable3DSystem.height - imageTable3DSystem.getTitleView().height ) / 2 );
		addView( imageTable3DSystem );
		addCometShortcut = new AddShortcutList( "add comet shortcut" );
		//		addAppView.region = R3D.getTextureRegion(R3D.dockbar_add_app_bg);
		addCometShortcut.setSize( width , height );
		//		addAppView.setBackgroud(new NinePatch(addAppView.region));
		addCometShortcut.indicatorView.setPosition( 0 , mHeight - 140 * mScale - addCometShortcut.indicatorView.height / 2 );
		addView( addCometShortcut );
		ImageView3D imageView3D = new ImageView3D( "add shortcut" );
		imageView3D.region = R3D.getTextureRegion( R3D.dockbar_add_shortcut );
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
				addShortcut.viewParent.onCtrlEvent( addShortcut , AddShortcut.MSG_HIDE_ADD_VIEW );
			}
		} );
		addView( buttonView3DBack );
		imageTable3DComet.setEvent( new CallBack() {
			
			@Override
			public void callback()
			{
				imageTable3DComet.setBackgroud( new NinePatch( R3D.getTextureRegion( R3D.dockbar_selected_table1 ) ) );
				imageTable3DComet.getTitleView().region = R3D.getTextureRegion( R3D.dockbar_comet_shortcut_hilight );
				imageTable3DSystem.setBackgroud( new NinePatch( R3D.getTextureRegion( R3D.dockbar_unselected_table2 ) ) );
				imageTable3DSystem.getTitleView().region = R3D.getTextureRegion( R3D.dockbar_system_shortcut );
			}
		} );
		imageTable3DSystem.setEvent( new CallBack() {
			
			@Override
			public void callback()
			{
				imageTable3DSystem.setBackgroud( new NinePatch( R3D.getTextureRegion( R3D.dockbar_selected_table2 ) ) );
				imageTable3DSystem.getTitleView().region = R3D.getTextureRegion( R3D.dockbar_system_shortcut_hilight );
				imageTable3DComet.setBackgroud( new NinePatch( R3D.getTextureRegion( R3D.dockbar_unselected_table1 ) ) );
				imageTable3DComet.getTitleView().region = R3D.getTextureRegion( R3D.dockbar_comet_shortcut );
				addShortcut.viewParent.onCtrlEvent( addShortcut , AddShortcut.MSG_SHOW_SYSTEM_SHORTCUT_VIEW );
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
		width = mWidth;
		height = mHeight;
	}
	
	public static AddShortcut getInstance()
	{
		return addShortcut;
	}
	
	//	@Override
	//	public boolean onTouchDown(float x, float y, int pointer) {
	//		
	//		 if (super.onTouchDown(x, y, pointer)) {
	//			 return false;
	//		 }
	//		 return true;
	//	}
	//	@Override
	//	public boolean onTouchUp(float x, float y, int pointer) {
	//		
	//		 if (super.onTouchUp(x, y, pointer)) {
	//			 return false;
	//		 }
	//		 return false;
	//	}
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		return super.fling( velocityX , velocityY );
		//		 return true;
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		return super.scroll( x , y , deltaX , deltaY );
		//		 return true;
	}
	
	public void startapp()
	{
		allapps = getAllApps();
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
		if( addCometShortcut != null && pagenum > 0 )
		{
			addCometShortcut.removeAllChild( pagenum );
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
			addCometShortcut.addPage( i , appGridView );
		}
		pagenum = page;
	}
	
	public List<View3D> getAllApps()
	{
		List<View3D> allApps = new ArrayList<View3D>();
		ShortcutIcon3D shortcutIcon = null;
		//		if (DefaultLayout.virtureView != null) {
		//			if (DefaultLayout.virtureView.size() > 0){
		//				for (int i = 0; i < DefaultLayout.virtureView.size(); i++) {
		//					WidgetIcon icon = DefaultLayout.virtureView.get(i);
		//					shortcutIcon = new ShortcutIcon3D(icon.name, icon.region);
		//					shortcutIcon.setItemInfo(icon.getItemInfo());
		//					shortcutIcon.setSize(icon.getWidth(), icon.getHeight());
		//					allApps.add(shortcutIcon);
		//				}
		//			}
		//		}
		if( DefaultLayout.cometShortcutView != null )
		{
			if( DefaultLayout.cometShortcutView.size() > 0 )
			{
				for( int i = 0 ; i < DefaultLayout.cometShortcutView.size() ; i++ )
				{
					Icon3D icon = DefaultLayout.cometShortcutView.get( i );
					shortcutIcon = new ShortcutIcon3D( icon.name , icon.region );
					shortcutIcon.setItemInfo( icon.getItemInfo() );
					shortcutIcon.setSize( icon.getWidth() , icon.getHeight() );
					allApps.add( shortcutIcon );
				}
			}
		}
		return allApps;
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
			viewParent.onCtrlEvent( this , AddShortcut.MSG_HIDE_ADD_VIEW );
			return true;
		}
		return super.keyUp( keycode );
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		if( sender instanceof ShortcutIcon3D )
		{
			switch( event_id )
			{
				case ShortcutIcon3D.MSG_HIDE_ADD_VIEW:
					return viewParent.onCtrlEvent( this , AddShortcut.MSG_HIDE_ADD_VIEW );
			}
		}
		return super.onCtrlEvent( sender , event_id );
	}
}
