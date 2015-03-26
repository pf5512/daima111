package com.iLoong.launcher.DesktopEdit;


import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.Widget3D.List3DShortcut;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class MenuShortcut extends List3DShortcut
{
	
	public MenuShortcut(
			String name )
	{
		super( name );
	}
	
	public void makeShortcut()
	{
		Bitmap sBmp = null;
		Bitmap bg = null;
		InputStream is = null;
		Bitmap bmp = null;
		boolean needScale = false;
		try
		{
			bg = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/desktopEdit/shortcut_bg.png" ) );
			is = ThemeManager.getInstance().getCurrThemeInput( "theme/desktopEdit/system_shortcut.png" );
			if( is == null )
			{
				needScale = true;
				is = iLoongLauncher.getInstance().getAssets().open( "theme/desktopEdit/system_shortcut.png" );
			}
			sBmp = Tools.getImageFromInStream( is );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		finally
		{
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
		}
		if( needScale )
		{
			Canvas canvas = new Canvas();
			bmp = Bitmap.createBitmap( bg.getWidth() , bg.getHeight() , Bitmap.Config.ARGB_8888 );
			canvas.setBitmap( bmp );
			canvas.drawBitmap( bg , 0 , 0 , null );
			canvas.drawBitmap( sBmp , ( bg.getWidth() - sBmp.getWidth() ) / 2 , ( bg.getHeight() - sBmp.getHeight() ) / 2 , null );
			bg.recycle();
			sBmp.recycle();
			bmp = Tools.resizeBitmap(
					bmp ,
					(int)( DefaultLayout.app_icon_size * DefaultLayout.thirdapk_icon_scaleFactor ) ,
					(int)( DefaultLayout.app_icon_size * DefaultLayout.thirdapk_icon_scaleFactor ) );
			bg = Icon3D.getIconBg();
			bmp = Utils3D.titleToBitmap(
					bmp ,
					iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_sys_shortcut ) ,
					bg ,
					null ,
					R3D.workspace_cell_width ,
					R3D.workspace_cell_height ,
					true ,
					false );
		}
		else
		{
			sBmp = Tools.resizeBitmap( sBmp , Utils3D.getScreenWidth() / 720f );
			bg = Tools.resizeBitmap( bg , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
			bmp = Utils3D.titleToBitmap(
					sBmp ,
					iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_sys_shortcut ) ,
					bg ,
					null ,
					R3D.workspace_cell_width ,
					R3D.workspace_cell_height ,
					true ,
					false );
		}
		region = new TextureRegion( new BitmapTexture( bmp , true ) );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( ParticleManager.particleManagerEnable )
		{
			drawParticle( batch );
		}
		int x = Math.round( this.x );
		int y = Math.round( this.y );
		if( region.getTexture() == null )
			return;
		if( region != null && region.getTexture() != null && region.getTexture() instanceof BitmapTexture )
			( (BitmapTexture)region.getTexture() ).dynamicLoad();
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		batch.draw( region , x , y , region.getRegionWidth() , region.getRegionHeight() );
	}
}
