package com.iLoong.launcher.Desktop3D;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class ZoomArrow extends View3D
{
	
	//	public static boolean ifonTouchDown = false;
//	public float recordW = 0;
//	public float recordH = 0;
	View3D view;
	public ZoomArrow(
			String name ,
			TextureRegion region ,
			View3D view )
	{
		super( name , region );
		this.view = view;
	}
	
	ZoomBox zoombox = null;
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
//		ViewGroup3D vg = (ViewGroup3D)iLoongLauncher.getInstance().getD3dListener().getRoot().findView( "ZoomView" );
//		if( vg != null )
//		{
//			zoombox = (ZoomBox)vg.findView( "ZoomViewbg" );
//			recordW = zoombox.width;
//			recordH = zoombox.height;
//		}
//		//		ifonTouchDown = true;
//		//		requestFocus();
//		iLoongLauncher.getInstance().getD3dListener().getRoot().ReturnClor();
		return true;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
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
		CellLayout3D cellLayout = new CellLayout3D( "celllayout" );
		if( name.equals( "viewarrowright" ) )
		{
			cellLayout.scrollForChange( deltaX , 0 , CellLayout3D.RightMove , view );
		}
		else if( name.equals( "viewarrowleft" ) )
		{
			cellLayout.scrollForChange( deltaX , 0 , CellLayout3D.LeftMove , view );
		}
		else if( name.equals( "viewarrowtop" ) )
		{
			cellLayout.scrollForChange( 0 , deltaY , CellLayout3D.TopMove , view );
		}
		else if( name.equals( "viewarrowbottom" ) )
		{
			cellLayout.scrollForChange( 0 , deltaY , CellLayout3D.BottomMove , view );
		}
		return true;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		ViewGroup3D vg = iLoongLauncher.getInstance().getD3dListener().getRoot().zoomview;
		if( vg != null )
		{
			zoombox = (ZoomBox)vg.findView( "ZoomViewbg" );
			if( zoombox != null )
			{
				final CellLayout3D cellLayout = new CellLayout3D( "celllayout" );
				final float moveX = zoombox.width - ZoomBox.recordW;
				final float moveY = zoombox.height - ZoomBox.recordH;
				if( name.equals( "viewarrowright" ) )
				{
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							cellLayout.adjustZoombox( moveX , moveY , CellLayout3D.RightMove , view );
						}
					} );
				}
				else if( name.equals( "viewarrowleft" ) )
				{
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							cellLayout.adjustZoombox( moveX , moveY , CellLayout3D.LeftMove , view );
						}
					} );
				}
				else if( name.equals( "viewarrowtop" ) )
				{
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							cellLayout.adjustZoombox( moveX , moveY , CellLayout3D.TopMove , view );
						}
					} );
				}
				else if( name.equals( "viewarrowbottom" ) )
				{
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							cellLayout.adjustZoombox( moveX , moveY , CellLayout3D.BottomMove , view );
						}
					} );
				}
			}
		}
		releaseFocus();
		if( zoombox != null )
		{
			zoombox.requestFocus();
		}
		return true;
	}
}
