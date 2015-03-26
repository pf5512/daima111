package com.iLoong.launcher.Desktop3D;


import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class ZoomBox extends View3D
{
	
	float scale = Utils3D.getScreenWidth() / 720f;
	public static float recordW = 0;
	public static float recordH = 0;
	View3D view;
	public ZoomBox(
			String name,View3D view )
	{
		super( name );
		this.view=view;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		ViewGroup3D vg = iLoongLauncher.getInstance().getD3dListener().getRoot().zoomview;
		if( vg != null )
		{
			ZoomArrow viewarrowleft = (ZoomArrow)vg.findView( "viewarrowleft");
			ZoomArrow viewarrowright = (ZoomArrow)vg.findView( "viewarrowright");
			ZoomArrow viewarrowtop = (ZoomArrow)vg.findView( "viewarrowtop");
			ZoomArrow viewarrowbottom = (ZoomArrow)vg.findView( "viewarrowbottom");
			if( x > 0 - CellLayout3D.zoomWidth * scale/2 && x < 0 + CellLayout3D.zoomWidth * scale/2 && y >0+ this.height / 2 - CellLayout3D.zoomHeight * scale/2 && y <0+ this.height / 2 + CellLayout3D.zoomHeight * scale/2 )
			{
				this.releaseFocus();
				viewarrowleft.requestFocus();
				recordW=this.width;
				recordH=this.height;
				iLoongLauncher.getInstance().getD3dListener().getRoot().ReturnClor();
			}
			else if( x > 0+this.width  - CellLayout3D.zoomWidth * scale/2 && x < 0+this.width  + CellLayout3D.zoomWidth * scale/2 && y >  0+this.height / 2 - CellLayout3D.zoomHeight * scale/2 && y < 0+ this.height / 2 + CellLayout3D.zoomHeight * scale/2 )
			{
				this.releaseFocus();
				viewarrowright.requestFocus();
				recordW=this.width;
				recordH=this.height;
				iLoongLauncher.getInstance().getD3dListener().getRoot().ReturnClor();
			}
			else if( x > 0+this.width / 2 - CellLayout3D.zoomWidth * scale/2 && x <0+ this.width / 2 + CellLayout3D.zoomWidth * scale/2 && y >this.height - CellLayout3D.zoomHeight * scale/2 && y <this.height + CellLayout3D.zoomHeight * scale/2 )
			{
				this.releaseFocus();
				viewarrowtop.requestFocus();
				recordW=this.width;
				recordH=this.height;
				iLoongLauncher.getInstance().getD3dListener().getRoot().ReturnClor();
			}
			else if( x >0+ this.width / 2 - CellLayout3D.zoomWidth * scale/2 && x <0+ this.width / 2 + CellLayout3D.zoomWidth * scale/2 && y > 0 - CellLayout3D.zoomHeight * scale/2 && y < 0 + CellLayout3D.zoomHeight * scale/2 )
			{
				this.releaseFocus();
				viewarrowbottom.requestFocus();
				recordW=this.width;
				recordH=this.height;
				iLoongLauncher.getInstance().getD3dListener().getRoot().ReturnClor();
			}
	}
		return true;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( !( x > 0 && x <0+ this.width&& y > 0&& y < 0+this.height ) )
		{
			if( iLoongLauncher.getInstance().getD3dListener().getRoot().zoomview != null )
			{
				ViewGroup3D zoom = iLoongLauncher.getInstance().getD3dListener().getRoot().zoomview;
				if( zoom != null )
				{
					iLoongLauncher.getInstance().getD3dListener().getRoot().removeView( zoom );
					zoom = null;
					iLoongLauncher.getInstance().getD3dListener().getRoot().zoomview = null;
				}
				iLoongLauncher.getInstance().getD3dListener().getRoot().ReturnClor();
				this.releaseFocus();
				return true;
			}
		}
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
	public boolean onClick(
			float x ,
			float y )
	{
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		return true;
	}
}
