package com.iLoong.launcher.action;

import android.graphics.Bitmap;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.theme.ThemeManager;


public class ActionNextButton extends ImageView3D
{
	protected  ActionTitleUtil actionUtil = new ActionTitleUtil();
	protected static IOnBtnClickEvent mOnBtnClickEventImpl;
	public ActionNextButton(
			String name  )
	{
		super( name );
		
	}
	

	public void setTitle(String title,int fontSize){
		float mScale=Utils3D.getScreenWidth()/720.0F;
		Bitmap btnbmp =ThemeManager.getInstance().getBitmap( "theme/desktopAction/button.png" );
		btnbmp=Tools.resizeBitmap( btnbmp , mScale);
		
		actionUtil.setFontSize( fontSize );
		actionUtil.setTitle( title );
		this.region=new TextureRegion( actionUtil.getTextureRegion(title,btnbmp  ));
		this.setSize( region.getRegionWidth(), region.getRegionHeight() );
		btnbmp.recycle();
	}
	
	


	public void setIOnBtnClickEvent(IOnBtnClickEvent clickEvent){
		this. mOnBtnClickEventImpl=clickEvent;
	}
	

	
	public boolean onClick(
			float x ,
			float y )
	{
		
		return (Boolean)mOnBtnClickEventImpl.onBtnClick();
				
		
	}
	
	public static interface IOnBtnClickEvent{
		
		public Object onBtnClick();
	}
	
	public boolean onLongClick(
			float x ,
			float y )
	{
		
		return (Boolean)mOnBtnClickEventImpl.onBtnClick();
	}
	
	
	
}
