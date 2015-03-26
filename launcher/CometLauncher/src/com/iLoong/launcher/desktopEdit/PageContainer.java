package com.iLoong.launcher.desktopEdit;


import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class PageContainer extends ViewGroup3D
{
	
	public static final int MSG_PAGE_APPEND = 0;
	public static final int MSG_PAGE_SET_HOME = 1;
	public static final int MSG_PAGE_CLICK = 2;
	public static final int MSG_PAGE_LONGCLICK = 3;
	public int pageIndex;
	public boolean isHomePage;
	private float page_add_size = 95f;
	private float page_home_size = 85f;
	private float page_home_padding_bottom = 15f;
	
	public PageContainer()
	{
		// TODO Auto-generated constructor stub
	}
	
	public PageContainer(
			String name ,
			float pageWidth ,
			float pageHeight )
	{
		// TODO Auto-generated constructor stub
		super( name );
		this.transform = true;
		setSize( pageWidth , pageHeight );
		setOrigin( pageWidth / 2 , pageHeight / 2 );
		page_add_size *= DesktopEdit.scaleWidth;
		page_home_size *= DesktopEdit.scaleWidth;
		page_home_padding_bottom *= DesktopEdit.scaleHeight;
	}
	
	public void buildEditPage(
			View3D cellLayout ,
			int pageIndex )
	{
		this.pageIndex = pageIndex;
		float posX = 0;
		float posY = 0;
		TextureRegion textureRegion;
		textureRegion = R3D.getTextureRegion( R3D.desktopEdit_page_bg );
		textureRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		View3D pageBg = new View3D( "pageBg" , textureRegion );
		pageBg.setSize( this.getWidth() , this.getHeight() );
		pageBg.setOrigin( pageBg.getWidth() / 2 , pageBg.getHeight() / 2 );
		this.addView( pageBg );
		this.addView( cellLayout );
		HandleIcon pageHomeView;
		if( pageIndex == DesktopEdit.getInstance().getHomePageIndex() )
		{
			isHomePage = true;
			textureRegion = R3D.getTextureRegion( R3D.desktopEdit_page_cur_home );
			textureRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
			pageHomeView = new HandleIcon( "pageHomeView" , textureRegion , this );
		}
		else
		{
			textureRegion = R3D.getTextureRegion( R3D.desktopEdit_page_home );
			textureRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
			pageHomeView = new HandleIcon( "pageHomeView" , textureRegion , this );
		}
		pageHomeView.setSize( page_home_size , page_home_size );
		pageHomeView.setOrigin( pageHomeView.getWidth() / 2 , pageHomeView.getHeight() / 2 );
		posX = ( this.getWidth() - pageHomeView.getWidth() ) / 2;
		posY = page_home_padding_bottom;
		pageHomeView.setPosition( posX , posY );
		this.addView( pageHomeView );
	}
	
	public void buildAddPage(
			int pageIndex )
	{
		this.pageIndex = pageIndex;
		float posX = 0;
		float posY = 0;
		TextureRegion textureRegion;
		textureRegion = R3D.getTextureRegion( R3D.desktopEdit_page_bg );
		textureRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		View3D pageBg = new View3D( "pageBg" , textureRegion );
		pageBg.setSize( this.getWidth() , this.getHeight() );
		pageBg.setOrigin( pageBg.getWidth() / 2 , pageBg.getHeight() / 2 );
		this.addView( pageBg );
		textureRegion = R3D.getTextureRegion( R3D.desktopEdit_page_add );
		textureRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		HandleIcon pageAddView = new HandleIcon( "pageAddView" , textureRegion , this );
		pageAddView.setSize( page_add_size , page_add_size );
		pageAddView.setOrigin( pageAddView.getWidth() / 2 , pageAddView.getHeight() / 2 );
		posX = ( this.getWidth() - pageAddView.getWidth() ) / 2;
		posY = this.getHeight() - pageAddView.getHeight();
		pageAddView.setPosition( posX , posY );
		this.addView( pageAddView );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( super.onClick( x , y ) )
		{
			return true;
		}
		if( this.name.equals( "addPage" ) )
		{
			return true;
		}
		viewParent.onCtrlEvent( this , MSG_PAGE_CLICK );
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		super.onLongClick( x , y );
		if( this.name.equals( "addPage" ) )
		{
			SendMsgToAndroid.vibrator( R3D.vibrator_duration );
		}
		else
		{
			if( DesktopEdit.curDesktopEditMode == DesktopEdit.DESKTOP_EDIT_MODE_CYLINDER )
			{
				this.setTag( new Vector2( CylinderEditMode.page3DPosX , CylinderEditMode.page3DPosY ) );
				DragLayer3D.dragStartX = x;
				DragLayer3D.dragStartY = y;
				this.setOriginZ( CylinderEditMode.PAGE_GROUP_ORIGIN_Z );
				this.setRotationX( CylinderEditMode.PAGE_GROUP_X_ANGLE );
			}
			else
			{
				this.toAbsoluteCoords( point );
				this.setTag( new Vector2( point.x , point.y ) );
				point.x = x;
				point.y = y;
				this.toAbsolute( point );
				DragLayer3D.dragStartX = point.x;
				DragLayer3D.dragStartY = point.y;
			}
			viewParent.onCtrlEvent( this , MSG_PAGE_LONGCLICK );
		}
		return true;
	}
	
	@Override
	public boolean pointerInParent(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( !this.name.equals( "addPage" ) && pageIndex != DesktopEdit.getInstance().getCurrentPageIndex() )
		{
			return false;
		}
		if( DesktopEdit.curDesktopEditMode == DesktopEdit.DESKTOP_EDIT_MODE_CYLINDER )
		{
			if( this.name.equals( "addPage" ) )
			{
				return super.pointerInParent( x , y );
			}
			else
			{
				boolean ret = view3dTouchCheck.isPointIn( this , 0 , 0 , this.getWidth() , this.getHeight() , x , y );
				CylinderEditMode.page3DPosX = view3dTouchCheck.v1.x;
				CylinderEditMode.page3DPosY = view3dTouchCheck.v1.y;
				return ret;
			}
		}
		return super.pointerInParent( x , y );
	}
	
	@Override
	public boolean toLocalCoordinates(
			View3D descendant ,
			Vector2 point )
	{
		// TODO Auto-generated method stub
		if( DesktopEdit.curDesktopEditMode == DesktopEdit.DESKTOP_EDIT_MODE_CYLINDER )
		{
			if( this.name.equals( "addPage" ) )
			{
				super.toLocalCoordinates( descendant , point );
			}
			return true;
		}
		return super.toLocalCoordinates( descendant , point );
	}
	
	private class HandleIcon extends View3D
	{
		
		private boolean isHilight;
		private PageContainer pageContainer;
		
		public HandleIcon(
				String name )
		{
			// TODO Auto-generated constructor stub
			super( name );
		}
		
		public HandleIcon(
				String name ,
				TextureRegion region ,
				PageContainer container )
		{
			super( name , region );
			// TODO Auto-generated constructor stub
			pageContainer = container;
		}
		
		@Override
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			// TODO Auto-generated method stub
			requestDark();
			return true;
		}
		
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			// TODO Auto-generated method stub
			releaseDark();
			return true;
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			// TODO Auto-generated method stub
			handleAction();
			return true;
		}
		
		@Override
		public boolean onLongClick(
				float x ,
				float y )
		{
			// TODO Auto-generated method stub
			releaseDark();
			return super.onLongClick( x , y );
		}
		
		@Override
		public boolean is3dRotation()
		{
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean pointerInParent(
				float x ,
				float y )
		{
			// TODO Auto-generated method stub
			if( DesktopEdit.curDesktopEditMode == DesktopEdit.DESKTOP_EDIT_MODE_CYLINDER )
			{
				if( viewParent.name.equals( "addPage" ) )
				{
					return super.pointerInParent( x , y );
				}
				else
				{
					return view3dTouchCheck.isPointIn( this , this.x , this.y , this.getWidth() , this.getHeight() , x , y );
				}
			}
			return super.pointerInParent( x , y );
		}
		
		public void requestDark()
		{
			if( !isHilight )
			{
				hilightHandleIcon( true );
				isHilight = true;
			}
		}
		
		@Override
		public void releaseDark()
		{
			// TODO Auto-generated method stub
			if( isHilight )
			{
				hilightHandleIcon( false );
				isHilight = false;
			}
		}
		
		private void hilightHandleIcon(
				boolean hilight )
		{
			if( hilight )
			{
				if( this.name.equals( "pageHomeView" ) )
				{
					if( pageContainer.pageIndex != DesktopEdit.getInstance().getHomePageIndex() )
					{
						this.region = R3D.getTextureRegion( R3D.desktopEdit_page_home_hilight );
					}
				}
				else if( this.name.equals( "pageAddView" ) )
				{
					this.region = R3D.getTextureRegion( R3D.desktopEdit_page_add_hilight );
				}
			}
			else
			{
				if( this.name.equals( "pageHomeView" ) )
				{
					if( pageContainer.pageIndex != DesktopEdit.getInstance().getHomePageIndex() )
					{
						this.region = R3D.getTextureRegion( R3D.desktopEdit_page_home );
					}
				}
				else if( this.name.equals( "pageAddView" ) )
				{
					this.region = R3D.getTextureRegion( R3D.desktopEdit_page_add );
				}
			}
			this.region.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
		
		private void handleAction()
		{
			if( this.name.equals( "pageHomeView" ) )
			{
				if( pageContainer.pageIndex != DesktopEdit.getInstance().getHomePageIndex() )
				{
					pageContainer.isHomePage = true;
					this.region = R3D.getTextureRegion( R3D.desktopEdit_page_cur_home );
					viewParent.onCtrlEvent( pageContainer , MSG_PAGE_SET_HOME );
				}
			}
			else if( this.name.equals( "pageAddView" ) )
			{
				this.startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.3f , 0 , 0 , 0 ).setCallback( this );
				viewParent.onCtrlEvent( pageContainer , MSG_PAGE_APPEND );
			}
		}
		
		@Override
		public void onEvent(
				int type ,
				BaseTween source )
		{
			// TODO Auto-generated method stub
			if( this.name.equals( "pageAddView" ) )
			{
				this.color.a = 1;
			}
		}
	}
}
