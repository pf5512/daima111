package com.iLoong.launcher.DesktopEdit;


import android.content.Intent;
import android.graphics.Bitmap;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.umeng.analytics.MobclickAgent;


public class PopImageView3D extends ImageView3D
{
	
	private Bitmap bmp = null;
	private TextureRegion titleRegion = null;
	private TextureRegion titleRegion1 = null;
	public static boolean result = true;//控制换壁纸
	
	public PopImageView3D(
			String name ,
			TextureRegion textureRegion )
	{
		super( name , textureRegion );
	}
	
	public PopImageView3D(
			String name ,
			TextureRegion orgRegion ,
			TextureRegion fosRegion )
	{
		super( name , orgRegion );
		titleRegion = orgRegion;
		titleRegion1 = fosRegion;
	}
	
	public PopImageView3D(
			String name ,
			Bitmap smallbmp ,
			Bitmap bigbmp )
	{
		super( name , new BitmapTexture( smallbmp ) );
		this.bmp = bigbmp;
	}
	
	public PopImageView3D(
			String name ,
			Bitmap smallbmp )
	{
		super( name , new BitmapTexture( smallbmp ) );
	}
	
	String displayname;
	String pkgname;
	
	public PopImageView3D(
			String name ,
			Bitmap bitmap ,
			String displayname ,
			String pkgname )
	{
		super( name , new BitmapTexture( bitmap ) );
		this.displayname = displayname;
		this.pkgname = pkgname;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( titleRegion1 != null )
		{
			if( pointer > 0 )
			{
				return false;
			}
			displayFocusBG( true );
			return true;
		}
		else
		{
			return super.onTouchDown( x , y , pointer );
		}
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( titleRegion1 != null )
		{
			if( pointer > 0 )
			{
				return false;
			}
			displayFocusBG( false );
		}
		return super.onTouchUp( x , y , pointer );
	}
	
	public void displayFocusBG(
			boolean bFocus )
	{
		if( bFocus )
		{
			// this.setBackgroud(backFocusground);
			this.region = titleRegion1;
		}
		else
		{
			// this.setBackgroud(backNormalground);
			this.region = titleRegion;
		}
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( name.equals( "iv_add" ) )
		{
			SendMsgToAndroid.sendSelectHotWallpaper();
			MobclickAgent.onEvent( iLoongLauncher.getInstance() , "EditModelToWallpaper" );
			return true;
		}
		else if( name.equals( "ivlocalpaper" ) )
		{
			final Intent pickWallpaper = new Intent( Intent.ACTION_SET_WALLPAPER );
			Intent chooser = Intent.createChooser( pickWallpaper , iLoongLauncher.getInstance().getText( RR.string.chooser_wallpaper ) );
			iLoongLauncher.getInstance().startActivity( chooser );
			return true;
		}
		else
		{
			if( result == true )
			{
				result = false;
				SendMsgToAndroid.changeWallpager( displayname , pkgname );
			}
			return true;
		}
	}
}
