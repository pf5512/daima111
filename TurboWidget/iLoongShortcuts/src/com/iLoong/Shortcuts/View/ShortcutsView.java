package com.iLoong.Shortcuts.View;


import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Shortcuts.LogoHelper;
import com.iLoong.Shortcuts.SystemTool.ApnAdmin;
import com.iLoong.Shortcuts.SystemTool.BlueToothAdmin;
import com.iLoong.Shortcuts.SystemTool.GpsAdmin;
import com.iLoong.Shortcuts.SystemTool.IAdminCallback;
import com.iLoong.Shortcuts.SystemTool.ISystemAdmin;
import com.iLoong.Shortcuts.SystemTool.WifiAdmin;
import com.iLoong.launcher.UI3DEngine.adapter.Texture;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class ShortcutsView extends PluginViewObject3D1 implements IAdminCallback
{
	
	public ISystemAdmin sysAdmin = null;
	public MainAppContext MappContext;
	public Timeline animation_line = null;
	public boolean flag = false;
	public String name;
	
	public ShortcutsView(
			String name ,
			MainAppContext appContext ,
			TextureRegion region ,
			String objname )
	{
		super( appContext , name , region , objname );
		this.name = name;
		setSize( WidgetShortcuts.MODEL_WIDTH / 2 , WidgetShortcuts.MODEL_HEIGHT / 2 );
		this.setMoveOffset( WidgetShortcuts.MODEL_WIDTH / 2 , WidgetShortcuts.MODEL_HEIGHT / 2 , z );
		super.build();
		MappContext = appContext;
		setRotationVector( 1 , 0 , 0 );
		Init();
		if( ambientTexture == null )
		{
			Texture texture = new Texture( mAppContext.gdx , new AndroidFiles( mAppContext.mWidgetContext.getAssets() ).internal( LogoHelper.getThemeImagePath( mAppContext.mThemeName , "012.png" ) ) );
			texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			ambientTexture = new TextureRegion( texture );
		}
	}
	
	Timer time = new Timer();
	
	public void Init()
	{
		Log.d( "song" , "ShortcutsView name=" + name );
		if( name.equals( "gps" ) )
		{
			sysAdmin = new GpsAdmin( appContext.mContainerContext , name , this );
		}
		else if( name.equals( "wifi" ) )
		{
			sysAdmin = new WifiAdmin( appContext.mContainerContext , name , this );
		}
		else if( name.equals( "lanya" ) )
		{
			sysAdmin = new BlueToothAdmin( appContext.mContainerContext , name , this );
		}
		else if( name.equals( "liuliang" ) )
		{
			sysAdmin = new ApnAdmin( appContext.mContainerContext , name , this );
		}
		time.schedule( new TimerTask() {
			
			@Override
			public void run()
			{
				Log.d( "Init" , "time TimerTask  name=" + name );
				reflush();
				Gdx.graphics.requestRendering();
			}
		} , 1000 );
		Log.d( "song" , "ShortcutsView end1  name=" + name );
	}
	
	public void reflush()
	{
		if( sysAdmin != null )
		{
			if( !sysAdmin.getReadyState() )
			{
				flag = true;
				setRotationAngle( 0 , 0 , 0 );
			}
			else
			{
				flag = false;
				setRotationAngle( 180 , 0 , 0 );
			}
		}
	}
	
	public void stopAutoEffect()
	{
		if( animation_line != null && !animation_line.isFinished() )
		{
			animation_line.free();
			animation_line = null;
		}
	}
	
	public boolean ttt = false;
	public static final int rot_to_reset_effect = 3;
	public boolean Rotationflag = true;
	
	public void Rotation()
	{
		int rog = 0;
		Log.d( "song" , "Rotation  flag=" + flag );
		// setRotationVector(1f, 0,-3f );
		if( !name.equals( "gps" ) )
		{
			if( !flag )
			{
				flag = true;
				rog = 0;
				setRotationX( 180 );
			}
			else
			{
				flag = false;
				setRotationX( 0 );
				rog = 180;
			}
			stopAutoEffect();
			animation_line = Timeline.createParallel();
			animation_line.push( Tween.to( this , View3DTweenAccessor.ROTATION , 0.8f ).target( rog , 0 , 0 ).ease( Cubic.OUT ) );
			Rotationflag = false;
			animation_line.start( View3DTweenAccessor.manager ).setUserData( rot_to_reset_effect ).setCallback( this );
		}
		if( sysAdmin != null )
			sysAdmin.select();
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// TODO Auto-generated method stub
		if( source == animation_line && type == TweenCallback.COMPLETE )
		{
			Rotationflag = true;
			if( !name.equals( "liangdu" ) )
				time.schedule( new TimerTask() {
					
					@Override
					public void run()
					{
						if( Rotationflag )
							StateChange( name );
					}
				} , 3000 );
		}
		super.onEvent( type , source );
	}
	
	@Override
	public void StateChange(
			String key )
	{
		int rog = 0;
		if( !Rotationflag )
			return;
		Log.d( "song" , "StateChange flag=" + flag );
		if( sysAdmin != null )
		{
			if( !sysAdmin.getReadyState() )
			{
				if( flag == !sysAdmin.getReadyState() )
					return;
				flag = true;
				rog = 0;
				setRotationAngle( 180 , 0 , 0 );
			}
			else
			{
				if( flag == !sysAdmin.getReadyState() )
					return;
				flag = false;
				setRotationAngle( 1 , 0 , 0 );
				rog = 180;
			}
		}
		stopAutoEffect();
		animation_line = Timeline.createParallel();
		animation_line.push( Tween.to( this , View3DTweenAccessor.ROTATION , 0.8f ).target( rog , 0 , 0 ).ease( Cubic.OUT ) );
		animation_line.start( View3DTweenAccessor.manager ).setUserData( rot_to_reset_effect ).setCallback( this );
	}
	
	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub
		super.dispose();
		if( sysAdmin != null )
		{
			sysAdmin.onDelete();
			sysAdmin = null;
		}
	}
}
