// xiatian add whole file //for mainmenu sort by user
package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class App2Workspace3D extends ViewGroup3D
{
	
	public static TextureRegion normal = null;
	public static TextureRegion focus = null;
	public static boolean isFocus = false;
	public AppHost3D appHost;
	private TextureRegion titleTextureRegion;
	public long areaStayTime = 0;
	
	public App2Workspace3D(
			String name )
	{
		super( name );
		x = 0;
		y = Utils3D.getScreenHeight() - R3D.appbar_height;
		width = Utils3D.getScreenWidth();
		height = R3D.appbar_height;
		this.originY = height / 2;
		setPosition( 0 , Utils3D.getScreenHeight() );
		normal = R3D.findRegion( "app-to-workspace-normal" );
		focus = R3D.findRegion( "app-to-workspace-focus" );
		transform = true;
		if( titleTextureRegion == null )
		{
			String tips = iLoongLauncher.getInstance().getResources().getString( RR.string.ApplistItemMoveToWorkspace );
			titleTextureRegion = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( tips , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true ) );
		}
	}
	
	public void onThemeChanged()
	{
	}
	
	public void setAppHost(
			AppHost3D mAppHost3D )
	{
		appHost = mAppHost3D;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		batch.draw( region , x , y , width , height );
		batch.draw( titleTextureRegion , ( width - titleTextureRegion.getRegionWidth() ) / 2 , y + ( height - titleTextureRegion.getRegionHeight() ) / 2 );
		super.draw( batch , parentAlpha );
	}
	
	public boolean onDropOver(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		if( isFocus == false )
		{
			setFocus( true );
			areaStayTime = System.currentTimeMillis();
		}
		if( System.currentTimeMillis() - areaStayTime > 500 )
		{
			appHost.mMoveDrag2Workspace = true;
		}
		return true;
	}
	
	public void setFocus(
			boolean isFocus )
	{
		this.isFocus = isFocus;
		areaStayTime = 0;
		region = isFocus ? focus : normal;
	}
}
