package com.iLoong.launcher.Desktop3D;

import android.content.Intent;
import android.graphics.Bitmap;
import com.iLoong.launcher.Desktop3D.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.DLManager;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.ParticleAnim.ParticleCallback;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.WidgetDownload;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongApplication;



public class WidgetIcon extends Icon3D {
	private Object tag2;
	
	public boolean oldVisible = false;
	public int oldAppGridIndex;
    public int newAppGridIndex;
    public float oldX;
    public float oldY;
    public boolean inited = false;
	
	public WidgetIcon (String name) {
		super(name);
	}
	
	public WidgetIcon (String name, Texture texture) {
		super(name,texture);
	}
	
	public WidgetIcon (String name,Bitmap bmp,String title) {
		super(name,bmp,title);
	}

	public WidgetIcon (String name, TextureRegion region) {
		super(name,region);
	}
	public void setTag2(Object obj){
		this.tag2 = obj;
	}   
	public Object getTag2(){
		return this.tag2;
	}
	public boolean onLongClick(float x,float y) {
		// TODO Auto-generated method stub
	
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();

		if (info.itemType != LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW) {
			if (info.intent != null && info.intent.getAction().equals(Intent.ACTION_PACKAGE_INSTALL)) {
				SendMsgToAndroid.sendOurToastMsg(R3D.getString(RR.string.download_to_install));
				return true;
			}
		}
		
		return super.onLongClick(x, y);
	}
	
	public boolean onClick(float x,float y) {
		// TODO Auto-generated method stub
		Log.v("click","View3D onClick:" + name +" x:" + x + " y:"+ y);
		
		/************************ added by zhenNan.ye begin *************************/
		if (isSelected()) {
			cancelSelected();
			
			if (DefaultLayout.enable_particle)
			{
				if (ParticleManager.particleManagerEnable)
				{
					doubleClickFlag = true;
				}
			}
			
			return true;
		}
		/************************ added by zhenNan.ye end ***************************/
		
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		
		//xiatian start	//add 3 virtueIcon
		
		if(fireFeatureShortcut()){
			return true;
		}
		else
		//xiatian end
			return WidgetDownload.checkToDownload(info, true);
		
	}
	
	public boolean isFeatureShortcut(){
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		String pkgname = info.intent.getComponent().getPackageName();
		if(pkgname.equalsIgnoreCase("coco.bizhi"))
		{
			return true;
		}
		else if(pkgname.equalsIgnoreCase("coco.zhutixuanze"))
		{
			return true;
		}
		else if(pkgname.equalsIgnoreCase("coco.zhuomianshezhi"))
		{
			return true;
		}
		
		//xiatian add start	//New Requirement 20130507
		else if(pkgname.equalsIgnoreCase("coco.changjingzhuomian"))
		{
			return true;
		}
		//xiatian add end
		
		return false;
	}
	
	public boolean fireFeatureShortcut(){
		/************************ added by zhenNan.ye begin ***************************/
		if (DefaultLayout.enable_particle)
		{
			if (ParticleManager.particleManagerEnable)
			{
				if (particleCanRender)
				{
					return true;
				}
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		String pkgname = info.intent.getComponent().getPackageName();
		if(pkgname.equalsIgnoreCase("coco.bizhi"))
		{
			SendMsgToAndroid.sendSelectWallpaper();
			return true;
		}
		else if(pkgname.equalsIgnoreCase("coco.zhutixuanze"))
		{
			SendMsgToAndroid.sendSelectZhuTi();
			return true;
		}
		else if(pkgname.equalsIgnoreCase("coco.zhuomianshezhi"))
		{
			SendMsgToAndroid.sendSelectZhuMianSheZhi();
			return true;
		}
		
		//xiatian add start	//New Requirement 20130507
		else if(pkgname.equalsIgnoreCase("coco.changjingzhuomian"))
		{
			SendMsgToAndroid.sendSelectChangJingZhuoMian();
			return true;
		}
		//xiatian add end
		
		return false;
	}

	/************************ added by zhenNan.ye begin *************************/
	@Override
	public void onParticleCallback(int type) {
		// TODO Auto-generated method stub
		if (type == ParticleCallback.END)
		{
			if (!isSelected() && !doubleClickFlag)
			{
				stopParticle(ParticleManager.PARTICLE_TYPE_NAME_CLICK_ICON);
				
				ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
				String pkgname = info.intent.getComponent().getPackageName();
				if(pkgname.equalsIgnoreCase("coco.bizhi"))
				{
					SendMsgToAndroid.sendSelectWallpaper();
				}
				else if(pkgname.equalsIgnoreCase("coco.zhutixuanze"))
				{
					SendMsgToAndroid.sendSelectZhuTi();
				}
				else if(pkgname.equalsIgnoreCase("coco.zhuomianshezhi"))
				{
					SendMsgToAndroid.sendSelectZhuMianSheZhi();
				}
				
				//xiatian add start	//New Requirement 20130507
				else if(pkgname.equalsIgnoreCase("coco.changjingzhuomian"))
				{
					SendMsgToAndroid.sendSelectChangJingZhuoMian();
				}
				//xiatian add end
				
				else
				{
					WidgetDownload.checkToDownload(info, true);
				}
			} else {
				doubleClickFlag = false;
			}
		}
	}
	/************************ added by zhenNan.ye end *************************/
	
}
