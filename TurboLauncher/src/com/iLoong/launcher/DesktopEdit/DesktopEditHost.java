package com.iLoong.launcher.DesktopEdit;


import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;

import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.DesktopEdit.DesktopEditMenu.SingleMenu;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class DesktopEditHost extends ViewGroup3D
{
	
	public static final int POP_MENU_STYLE_4X1 = 0;
	public static final int POP_MENU_STYLE_4X2 = 1;
	public static float scaleFactor = 0.85f;
	public static float scaleFactor2 = 0.7f;
	public static MessageData messageData;
	public static int curPopMenuStyle = -1;
	public static DesktopEditHost instance;
	public MenuContainer menuContainer;
	public static boolean isOnshow = false;
	public static boolean inited = false;
	public MulpMenuHost mulpMenuHost;
	private Timeline timeline = null;
	private Timeline backline = null;
	
	private DesktopEditHost(
			String name )
	{
		super( name );
		this.height = R3D.pop_menu_container_height;
		this.width = Utils3D.getScreenWidth();
		setOrigin( this.width / 2 , this.height / 2 );
		mulpMenuHost = new MulpMenuHost( "mulpMenuHost" );
		this.addView( mulpMenuHost );
	}
	
	public void show()
	{
		super.show();
		if( timeline != null )
		{
			timeline.free();
			timeline = null;
		}
		timeline = Timeline.createParallel();
		if( curPopMenuStyle != POP_MENU_STYLE_4X2 )
		{
			mulpMenuHost.show();
			mulpMenuHost.color.a = 0f;
			mulpMenuHost.y = -mulpMenuHost.height * 2;
			timeline.push( Tween.to( mulpMenuHost , View3DTweenAccessor.CPOS_XY , 0.2f ).ease( Linear.INOUT ).target( mulpMenuHost.width / 2 , mulpMenuHost.height / 2 , 0 ).delay( 0.2f ) );
			timeline.push( Tween.to( mulpMenuHost , View3DTweenAccessor.OPACITY , 0.2f ).ease( Quart.IN ).target( 1f , 0 , 0 ).delay( 0.2f ) );
		}
		else
		{
			timeline.push( Tween.to( menuContainer , View3DTweenAccessor.CPOS_XY , 0.2f ).ease( Linear.INOUT ).delay( 0.15f ).target( menuContainer.width / 2 , menuContainer.height / 2 , 0 ) );
			timeline.push( Tween.to( menuContainer , View3DTweenAccessor.OPACITY , 0.2f ).ease( Quart.IN ).delay( 0.15f ).target( 1f , 0 , 0 ) );
		}
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source == backline && type == TweenCallback.COMPLETE )
		{
			if( menuContainer != null )
			{
				menuContainer.hide();
			}
		}
		if( source == timeline && type == TweenCallback.COMPLETE && isOnshow == false )
		{
			if( mulpMenuHost != null )
			{
				mulpMenuHost.hide();
			}
			if(source.getUserData()!=null&&(Integer)source.getUserData()== POP_MENU_STYLE_4X2 ){
				if(menuContainer!=null)
					menuContainer.releasePage();
			}
		}
		super.onEvent( type , source );
	}
	
	public static void popup(
			ViewGroup3D view )
	{
		if( instance == null )
		{
			DesktopEditHost.curPopMenuStyle = DesktopEditHost.POP_MENU_STYLE_4X1;
			instance = new DesktopEditHost( "DesktopEditHost" );
		}
		if( inited == false )
		{
			view.addView( instance );
		}
		if( isOnshow == false )
		{
			isOnshow = true;
			Root3D.hotseatBar.hide();
			instance.show();
		}
	}
	
	public static DesktopEditHost getInstance()
	{
		return instance;
	}
	
	public void recyle()
	{
		isOnshow = false;
		Root3D.hotseatBar.show();
		Root3D.hotseatBar.color.a = 0f;
		if( timeline != null )
		{
			timeline.free();
			timeline = null;
		}
		timeline = Timeline.createParallel();
		if( curPopMenuStyle != POP_MENU_STYLE_4X2 )
		{
			timeline.push( Tween.to( mulpMenuHost , View3DTweenAccessor.CPOS_XY , 0.5f ).ease( Linear.INOUT ).target( mulpMenuHost.width / 2 , -R3D.pop_menu_height , 0 ) );
			timeline.push( Tween.to( mulpMenuHost , View3DTweenAccessor.OPACITY , 0.5f ).ease( Quart.OUT ).target( 0 , 0 , 0 ) );
			timeline.push( Tween.to( Root3D.hotseatBar , View3DTweenAccessor.OPACITY , 0.5f ).ease( Quart.IN ).target( 1f , 0 , 0 ) );
			timeline.start( View3DTweenAccessor.manager ).setCallback( this ).setUserData( POP_MENU_STYLE_4X1 );
			
		}
		else
		{
			timeline.push( Tween.to( menuContainer , View3DTweenAccessor.CPOS_XY , 0.5f ).ease( Linear.INOUT ).target( menuContainer.width / 2 , -R3D.pop_menu_container_height , 0 ) );
			timeline.push( Tween.to( menuContainer , View3DTweenAccessor.OPACITY , 0.5f ).ease( Quart.OUT ).target( 0 , 0 , 0 ) );
			timeline.push( Tween.to( Root3D.hotseatBar , View3DTweenAccessor.OPACITY , 0.5f ).ease( Quart.IN ).target( 1f , 0 , 0 ) );
			timeline.start( View3DTweenAccessor.manager ).setCallback( this ).setUserData( POP_MENU_STYLE_4X2 );
			
		}
		
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		//		if( sender instanceof MulpMenuHost ){
		//			switch( event_id ){
		//				case MulpMenuHost.TAB_CHANGED_EVENT:
		//					curPopMenuStyle = POP_MENU_STYLE_4X1;
		//					mulpMenuHost.color.a=1;
		//					menuContainer.y=-(R3D.pop_menu_container_height-R3D.pop_menu_height);
		//					mulpMenuHost.show();
		//					menuContainer.hide();
		//					break;
		//					
		//			}
		//		}
		if( sender instanceof DesktopEditMenuItem )
		{
			switch( event_id )
			{
				case DesktopEditMenuItem.SET_CONTAINER:
					MessageData msgData = (MessageData)sender.getTag();
					this.setContainer( msgData );
					break;
			}
		}
		else if( sender instanceof SingleMenu )
		{
			switch( event_id )
			{
				case SingleMenu.EVENT_BACK:
					if( backline != null )
					{
						backline.free();
						backline = null;
					}
					backline = Timeline.createParallel();
					curPopMenuStyle = POP_MENU_STYLE_4X1;
					iLoongLauncher.getInstance().d3dListener.root.workspaceChangeForPopMenu();
					MessageData msg = (MessageData)sender.getTag();
					menuContainer.currPage.unloadPage();
					mulpMenuHost.show();
					mulpMenuHost.MenuCallBack( msg.getTabIndex() );
					backline.push( Tween.to( menuContainer , View3DTweenAccessor.POS_XY , 0.4f ).ease( Linear.INOUT ).target( 0 , -( R3D.pop_menu_container_height - R3D.pop_menu_height ) , 0 ) );
					backline.push( Tween.to( menuContainer , View3DTweenAccessor.OPACITY , 0.28f ).ease( Quad.INOUT ).target( 0 , 0 , 0 ) );
					backline.push( Tween.to( mulpMenuHost , View3DTweenAccessor.POS_XY , 0.4f ).ease( Linear.INOUT ).target( 0 , 0 , 0 ) );
					backline.push( Tween.to( mulpMenuHost , View3DTweenAccessor.OPACITY , 0.28f ).ease( Quad.INOUT ).delay( 0.12f ).target( 1f , 0 , 0 ) );
					backline.start( View3DTweenAccessor.manager ).setCallback( this );
					System.gc();
					break;
			}
		}
		return super.onCtrlEvent( sender , event_id );
	}
	
	public static void disposeAllViews(
			View3D view )
	{
		if( view instanceof ViewGroup3D )
		{
			ViewGroup3D vg = (ViewGroup3D)view;
			for( int i = 0 ; i < vg.getChildCount() ; i++ )
			{
				disposeAllViews( vg.getChildAt( i ) );
			}
		}
		else
		{
			view.dispose();
		}
	}
	
	public void setContainer(
			MessageData msgData )
	{
		if( timeline != null )
		{
			timeline.free();
			timeline = null;
		}
		timeline = Timeline.createParallel();
		for( int i = 0 ; i < this.getChildCount() ; i++ )
		{
			if( this.getChildAt( i ) instanceof MenuContainer )
			{
				this.removeView( this.getChildAt( i ) );
			}
		}
		for( int i = 0 ; i < this.getChildCount() ; i++ )
		{
			if( i == msgData.getTabIndex() && msgData.getDispose() )
			{
				disposeAllViews( this.getChildAt( i ) );
			}
			//			if( i == msgData.getTabIndex() )
			//				mulpMenuHost.color.a = 0;
			//else
			//this.getChildAt( i ).hide();
		}
		curPopMenuStyle = POP_MENU_STYLE_4X2;
		menuContainer = new MenuContainer( msgData.getTitle() );
		menuContainer.y = -( R3D.pop_menu_container_height - R3D.pop_menu_height );
		menuContainer.setContent( msgData );
		this.addView( menuContainer );
		menuContainer.color.a = 0f;
		timeline.push( Tween.to( mulpMenuHost , View3DTweenAccessor.CPOS_XY , 0.4f ).ease( Quad.INOUT )
				.target( Utils3D.getScreenWidth() / 2 , R3D.pop_menu_container_height - R3D.pop_menu_height / 2 , 0 ) );
		timeline.push( Tween.to( mulpMenuHost , View3DTweenAccessor.OPACITY , 0.28f ).ease( Quad.INOUT ).target( 0f , 0 , 0 ) );
		timeline.push( Tween.to( menuContainer , View3DTweenAccessor.POS_XY , 0.4f ).ease( Quad.INOUT ).target( 0 , 0 , 0 ) );
		timeline.push( Tween.to( menuContainer , View3DTweenAccessor.OPACITY , 0.28f ).ease( Quad.INOUT ).delay( 0.12f ).target( 1f , 0 , 0 ) );
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	public static int getPopMenuStyle()
	{
		if( isOnshow == false )
		{
			return -1;// 菜单处于关闭状态
		}
		else
		{
			//status: 1 表示处于4x2状态 ，0表示处于4x1状态
			return curPopMenuStyle;
		}
	}
}
