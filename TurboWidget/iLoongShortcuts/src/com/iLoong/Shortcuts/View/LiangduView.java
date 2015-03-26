package com.iLoong.Shortcuts.View;


import android.util.Log;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.iLoong.Shortcuts.LogoHelper;
import com.iLoong.Shortcuts.SystemTool.BrightnessAdmin;
import com.iLoong.launcher.UI3DEngine.adapter.Texture;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class LiangduView extends ShortcutsView
{
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		super.draw( batch , parentAlpha );
		if( state )
		{
			//		if(image!=((BrightnessAdmin)sysAdmin).getLevelState()){
			if( sysAdmin != null )
			{
				int index = ( (BrightnessAdmin)sysAdmin ).getLevelState();
				loadImage( ( index + 3 ) % ImageFileName.length );
				StateChange( "" );
			}
			//		}
			state = false;
		}
	}
	
	@Override
	public void reflush()
	{
		Log.d( "song" , "LiangduView reflush" );
		// TODO Auto-generated method stub
		super.reflush();
	}
	
	public int image = 0;
	public final static String ImageFileName[] = { "tex_light02.png" , "tex_light03.png" , "tex_light04.png" , "tex_light01.png" , };
	
	//	public final static String ImageFileName[]={"test.png","test.png","test.png","test.png",};
	public void loadImage(
			int i )
	{
		image = i;
		Texture texture = new Texture( MappContext.gdx , new AndroidFiles( MappContext.mWidgetContext.getAssets() ).internal( LogoHelper.getThemeImagePath(
				MappContext.mThemeName ,
				ImageFileName[i % ImageFileName.length] ) ) );
		texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
		TextureRegion region1 = new TextureRegion( texture );
		originalRegion = region1;
		if( region1 != null )
		{
			this.region.setRegion( region1 );
		}
		super.build();
		texture = null;
		region1 = null;
	}
	
	boolean state = false;
	
	public void statec()
	{
		state = true;
	}
	
	@Override
	public void Init()
	{
		if( sysAdmin == null )
		{
			sysAdmin = new BrightnessAdmin( appContext.mContainerContext , name );
			sysAdmin.setCallback( this );
			loadImage( ( (BrightnessAdmin)sysAdmin ).getLevelState() );
		}
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		if( sysAdmin != null )
			sysAdmin.onDelete();
	}
	
	public LiangduView(
			String name ,
			MainAppContext appContext ,
			TextureRegion region ,
			String objname )
	{
		super( name , appContext , region , objname );
		sysAdmin = new BrightnessAdmin( appContext.mContainerContext , name );
		sysAdmin.setCallback( this );
		//		Log.d("song","LiangduView  x,y,z,w,h="+x+","+y+","+z+","+width+","+height);
		//		Log.d("song","LiangduView  originX,originY,OriginZ="+originX+","+originY+","+getOriginZ() );
		//		Log.d("song","LiangduView  X,Y,w,h="+x+","+y+","+width+","+height );
	}
	
	@Override
	public void Rotation()
	{
		loadImage( ( (BrightnessAdmin)sysAdmin ).getLevelState() );
		if( sysAdmin != null )
			sysAdmin.select();
	}
	
	public boolean flag = false;
	
	@Override
	public void StateChange(
			String key )
	{
		setRotationX( 0 );
		stopAutoEffect();
		animation_line = Timeline.createParallel();
		animation_line.push( Tween.to( this , View3DTweenAccessor.ROTATION , 0.8f ).target( 180 , 0 , 0 ).ease( Cubic.OUT ) );
		animation_line.start( View3DTweenAccessor.manager ).setUserData( rot_to_reset_effect ).setCallback( this );
		//		this.setRotationVector(1, 1, 0);
	}
}
