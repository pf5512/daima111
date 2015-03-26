package com.iLoong.launcher.Desktop3D;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.cooee.android.launcher.framework.LauncherSettings.Favorites;
import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ParticleAnim.ParticleCallback;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.Widget3D.WidgetDownload;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.umeng.analytics.MobclickAgent;


public class WidgetIcon extends Icon3D
{
	
	private Object tag2;
	public boolean oldVisible = false;
	public int oldAppGridIndex;
	public int newAppGridIndex;
	public float oldX;
	public float oldY;
	public boolean inited = false;
	
	public WidgetIcon(
			String name )
	{
		super( name );
	}
	
	public WidgetIcon(
			String name ,
			Texture texture )
	{
		super( name , texture );
	}
	
	public WidgetIcon(
			String name ,
			Bitmap bmp ,
			String title )
	{
		super( name , bmp , title );
	}
	
	public WidgetIcon(
			String name ,
			Bitmap bmp ,
			String title ,
			Bitmap iconBg ,
			boolean ifShadow )
	{
		super( name , bmp , title , iconBg , ifShadow );
	}
	
	public static WidgetIcon createWidgetIcon(
			ShortcutInfo info ,
			boolean ifShadow )
	{
		WidgetIcon widgetIcon = null;
		InputStream is = null;
		String imagePath = DefaultLayout.getDefaultVirtureImage( info.intent.getComponent().getPackageName() );
		boolean scale = false;
		if( imagePath == null )
		{
			scale = true;
			imagePath = DefaultLayout.GetVirtureImageWithPkgClassName( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() );
			if( imagePath == null )
				return null;
			if( DefaultLayout.useCustomVirtual )
			{
				try
				{
					is = new FileInputStream( imagePath );
				}
				catch( FileNotFoundException e )
				{
					e.printStackTrace();
				}
			}
			else
			{
				is = ThemeManager.getInstance().getInputStream( imagePath );
			}
			if( info.intent.getComponent().getPackageName().equals( "coco.desktopsettings" ) )
			{
				InputStream dtis = ThemeManager.getInstance().getCurrThemeInput( "theme/icon/80/desksettings.png" );
				if( dtis != null )
				{
					scale = false;
					try
					{
						dtis.close();
					}
					catch( IOException e )
					{
					}
				}
			}
		}
		else
		{
			is = ThemeManager.getInstance().getInputStream( imagePath );
			scale = !( ThemeManager.getInstance().loadFromTheme( imagePath ) );
		}
		Bitmap origBmp = ThemeManager.getInstance().getBitmap( is );
		try
		{
			if( is != null )
			{
				is.close();
			}
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int iconSize = DefaultLayout.app_icon_size;
		if( DefaultLayout.thirdapk_icon_scaleFactor != 1.0f && !R3D.doNotNeedScale( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() ) && scale )
		{
			iconSize *= DefaultLayout.thirdapk_icon_scaleFactor;
		}
		if( origBmp == null )
			return null;
		BitmapDrawable drawable = new BitmapDrawable( origBmp );
		drawable.setTargetDensity( iLoongLauncher.getInstance().getResources().getDisplayMetrics() );
		Bitmap bmp = Utilities.createIconBitmap( drawable , iLoongLauncher.getInstance() , iconSize );
		origBmp.recycle();
		// teapotXu add start:
		Bitmap bg = null;
		if( iconSize != DefaultLayout.app_icon_size )
			bg = Icon3D.getIconBg();
		widgetIcon = new WidgetIcon( (String)info.title , bmp , (String)info.title , bg , ifShadow );
		// teapotXu add end
		widgetIcon.setItemInfo( info );
		bmp.recycle();
		bmp = null;
		/************************ added by diaosixu begin ***************************/
		AppReminder.Info aInfo = iLoongLauncher.getInstance().appReminder.new Info();
		boolean r = iLoongLauncher.getInstance().appReminder.isRemindApp( info.intent.getComponent().getPackageName() , aInfo );
		if( r )
		{
			Gdx.app.log( "diaosixu" , "show red point" );
			widgetIcon.setAppRemind( true );
			iLoongLauncher.getInstance().appReminder.startReminding( aInfo.packageName , widgetIcon , aInfo.remindNo );
		}
		/************************ added by diaosixu end ***************************/
		return widgetIcon;
	}
	
	public WidgetIcon(
			String name ,
			TextureRegion region )
	{
		super( name , region );
	}
	
	public void onThemeChanged()
	{
		if( !( info instanceof ShortcutInfo ) )
			return;
		Log.d( "theme" , "WidgetIcon onThemeChanged:" + info.title );
		ShortcutInfo shortcutInfo = (ShortcutInfo)info;
		if( shortcutInfo.intent == null )
		{
			return;
		}
		width = R3D.workspace_cell_width;
		height = R3D.workspace_cell_height;
		originX = width / 2.0f;
		originY = height / 2.0f;
		String imagePath = DefaultLayout.getDefaultVirtureImage( shortcutInfo.intent.getComponent().getPackageName() );
		boolean needbg = false;
		if( imagePath == null )
		{
			imagePath = DefaultLayout.GetVirtureImageWithPkgClassName( shortcutInfo.intent.getComponent().getPackageName() , shortcutInfo.intent.getComponent().getClassName() );
			needbg = true;
			if( shortcutInfo.intent.getComponent().getPackageName().equals( "coco.desktopsettings" ) )
			{
				InputStream is = ThemeManager.getInstance().getCurrThemeInput( "theme/icon/80/desksettings.png" );
				if( is != null )
				{
					needbg = false;
					try
					{
						is.close();
					}
					catch( IOException e )
					{
					}
				}
			}
			if( imagePath == null )
				return;
		}
		InputStream is = null;
		Bitmap origBmp = null;
		if( DefaultLayout.useCustomVirtual )
		{
			try
			{
				is = new FileInputStream( imagePath );
				origBmp = BitmapFactory.decodeStream( is );
				is.close();
			}
			catch( FileNotFoundException e )
			{
				e.printStackTrace();
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			origBmp = ThemeManager.getInstance().getBitmap( imagePath );
		}
		if( origBmp == null )
		{
			return;
		}
		Bitmap bmp = Utilities.createIconBitmap( new BitmapDrawable( origBmp ) , iLoongLauncher.getInstance() );
		origBmp.recycle();
		if( region != null && shortcutInfo.title != null )
		{
			if( needbg )
			{
				bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
				if( shortcutInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
				{
					( (BitmapTexture)this.region.getTexture() ).changeBitmap( Utils3D.IconToPixmap3D( bmp , shortcutInfo.title.toString() , Icon3D.getIconBg() , titleBg , false , false ) , true );
				}
				else
				{
					( (BitmapTexture)this.region.getTexture() ).changeBitmap( Utils3D.IconToPixmap3D( bmp , shortcutInfo.title.toString() , Icon3D.getIconBg() , titleBg , false ) , true );
				}
			}
			else
			{
				if( shortcutInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
				{
					( (BitmapTexture)this.region.getTexture() ).changeBitmap( Utils3D.IconToPixmap3D( bmp , shortcutInfo.title.toString() , Icon3D.getIconBg() , titleBg , false , false ) , true );
				}
				else
				{
					( (BitmapTexture)this.region.getTexture() ).changeBitmap( Utils3D.IconToPixmap3D( bmp , shortcutInfo.title.toString() , null , titleBg , false ) , true );
				}
			}
			region.setV2( 1 );
		}
		if( DefaultLayout.hotseat_hide_title && shortcutInfo.container == Favorites.CONTAINER_HOTSEAT )
		{
			Utils3D.changeTextureRegion( this , Utils3D.getIconBmpHeight() , true );
		}
	}
	
	public void setTag2(
			Object obj )
	{
		this.tag2 = obj;
	}
	
	public Object getTag2()
	{
		return this.tag2;
	}
	
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		if( info.itemType != LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW )
		{
			if( info.intent != null && info.intent.getAction().equals( Intent.ACTION_PACKAGE_INSTALL ) )
			{
				SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.download_to_install ) );
				return true;
			}
		}
		return super.onLongClick( x , y );
	}
	
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		Log.v( "click" , "View3D onClick:" + name + " x:" + x + " y:" + y );
		//teapotXu add start for icon3D's double-click optimization
		if( this.selected == true )
		{
			//
			if( isSelected() )
			{
				cancelSelected();
			}
			else
			{
				selected();
			}
			return true;
		}
		//teapotXu add end	
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			return true;
		}
		/************************ added by zhenNan.ye begin *************************/
		if( isSelected() )
		{
			cancelSelected();
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					doubleClickFlag = true;
				}
			}
			return true;
		}
		/************************ added by zhenNan.ye end ***************************/
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		//xiatian start	//add 3 virtueIcon
		if( fireFeatureShortcut() )
		{
			return true;
		}
		else
			//xiatian end
			return WidgetDownload.checkToDownload( info , true );
	}
	
	public boolean isFeatureShortcut()
	{
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		String pkgname = info.intent.getComponent().getPackageName();
		return iLoongLauncher.isVirtureIcon( pkgname );
		//		if( pkgname.equalsIgnoreCase( "coco.bizhi" ) )
		//		{
		//			return true;
		//		}
		//		else if( pkgname.equalsIgnoreCase( "coco.zhutixuanze" ) || pkgname.equalsIgnoreCase( "coco.uicenter" ) )
		//		{
		//			return true;
		//		}
		//		else if( pkgname.equalsIgnoreCase( "coco.zhuomianshezhi" ) )
		//		{
		//			return true;
		//		}
		//		//teapotXu add start
		//		else if( pkgname.equalsIgnoreCase( "coco.texiao" ) )
		//		{
		//			return true;
		//		}
		//		else if( pkgname.equalsIgnoreCase( "coco.ziti" ) )
		//		{
		//			return true;
		//		}
		//		else if( pkgname.equalsIgnoreCase( "coco.lock" ) )
		//		{
		//			return true;
		//		}
		//		else if( pkgname.equalsIgnoreCase( "coco.scene" ) )
		//		{
		//			return true;
		//		}
		//		else if( pkgname.equalsIgnoreCase( "coco.desktopsettings" ) )
		//		{
		//			return true;
		//		}
		//		//teapotXu add end
		//		//xiatian add start	//New Requirement 20130507
		//		//		else if(pkgname.equalsIgnoreCase("coco.changjingzhuomian"))
		//		//		{
		//		//			return true;
		//		//		}
		//		//xiatian add end
		//		return false;
	}
	
	public boolean fireFeatureShortcut()
	{
		/************************ added by zhenNan.ye begin ***************************/
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				if( particleCanRender )
				{
					return true;
				}
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		String pkgname = info.intent.getComponent().getPackageName();
		for( int i = 0 ; i < DefaultLayout.allVirture.size() ; i++ )
		{
			VirtureIcon vi = DefaultLayout.allVirture.get( i );
			if( pkgname.equalsIgnoreCase( vi.pkgName ) )
			{
				Log.v( "RecentAppBiz" , "find VI: " + pkgname );
				iLoongLauncher.getInstance().addRecentApp( info );
				break;
			}
		}
		if( pkgname.equalsIgnoreCase( "coco.bizhi" ) )
		{
			MobclickAgent.onEvent( iLoongLauncher.getInstance() , "DesktopToWallpaper" );
			SendMsgToAndroid.sendSelectWallpaper();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.zhutixuanze" ) || pkgname.equalsIgnoreCase( "coco.uicenter" ) )
		{
			MobclickAgent.onEvent( iLoongLauncher.getInstance() , "DesktopToUiCenter" );
			SendMsgToAndroid.sendSelectZhuTi();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.zhuomianshezhi" ) )
		{
			SendMsgToAndroid.sendSelectZhuMianSheZhi();
			return true;
		}
		//teapotXu add start 
		else if( pkgname.equalsIgnoreCase( "coco.texiao" ) )
		{
			SendMsgToAndroid.sendSelectPersonalTeXiao();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.ziti" ) )
		{
			SendMsgToAndroid.sendSelectPersonalZiTi();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.lock" ) )
		{
			SendMsgToAndroid.sendSelectPersonalLock();
			return true;
		}
		//teapotXu add end
		//xiatian add start	//New Requirement 20130507
		else if( pkgname.equalsIgnoreCase( "coco.scene" ) )
		{
			SendMsgToAndroid.sendSelectChangJingZhuoMian();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.desktopsettings" ) )
		{
			MobclickAgent.onEvent( iLoongLauncher.getInstance() , "DesktopToDSettings" );
			SendMsgToAndroid.senddesktopsettings();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.pingmuyulan" ) )
		{
			Desktop3DListener.root.onCtrlEvent( iLoongLauncher.getInstance().d3dListener.getWorkspace3D() , Workspace3D.MSG_PAGE_SHOW_EDIT );
			return true;
		}
		//xiatian add end
		return false;
	}
	
	/************************ added by zhenNan.ye begin *************************/
	@Override
	public void onParticleCallback(
			int type )
	{
		// TODO Auto-generated method stub
		if( type == ParticleCallback.END )
		{
			if( !isSelected() && !doubleClickFlag )
			{
				stopParticle( ParticleManager.PARTICLE_TYPE_NAME_CLICK_ICON );
				ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
				String pkgname = info.intent.getComponent().getPackageName();
				if( pkgname.equalsIgnoreCase( "coco.bizhi" ) )
				{
					SendMsgToAndroid.sendSelectWallpaper();
				}
				else if( pkgname.equalsIgnoreCase( "coco.zhutixuanze" ) || pkgname.equalsIgnoreCase( "coco.uicenter" ) )
				{
					SendMsgToAndroid.sendSelectZhuTi();
				}
				else if( pkgname.equalsIgnoreCase( "coco.zhuomianshezhi" ) )
				{
					SendMsgToAndroid.sendSelectZhuMianSheZhi();
				}
				else if( pkgname.equalsIgnoreCase( "coco.scene" ) )
				{
					SendMsgToAndroid.sendSelectChangJingZhuoMian();
				}
				else if( pkgname.equalsIgnoreCase( "coco.desktopsettings" ) )
				{
					SendMsgToAndroid.senddesktopsettings();
				}
				else if( pkgname.equalsIgnoreCase( "coco.pingmuyulan" ) )
				{
					Desktop3DListener.root.onCtrlEvent( iLoongLauncher.getInstance().d3dListener.getWorkspace3D() , Workspace3D.MSG_PAGE_SHOW_EDIT );
				}
				//xiatian add start	//New Requirement 20130507
				//				else if(pkgname.equalsIgnoreCase("coco.changjingzhuomian"))
				//				{
				//					SendMsgToAndroid.sendSelectChangJingZhuoMian();
				//				}
				//xiatian add end
				else
				{
					WidgetDownload.checkToDownload( info , true );
				}
			}
			else
			{
				doubleClickFlag = false;
			}
		}
	}
	/************************ added by zhenNan.ye end *************************/
}
