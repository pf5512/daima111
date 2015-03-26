package com.iLoong.ThemeClock.View;


import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class CommonSimulationClock extends ViewGroup3D
{
	
	public final static String TAG = "CommonSimulationClock";
	public ClockMemberView watchBackView = null;
	//2D模型widget view
	public Texture hourGroupTexture;
	public Texture minuteGroupTexture;
	public Texture secondGroupTexture;
	public Texture dotGroupTexture;
	public Texture watchBackTexture;
	public ClockMember2DView hourGroup2DView = null;
	public ClockMember2DView minuteGroup2DView = null;
	public ClockMember2DView secondGroup2DView = null;
	public ClockMember2DView dotGroup2DView = null;
	public ClockMember2DView watchBack2DView = null;
	
	public CommonSimulationClock(
			String name ,
			MainAppContext context )
	{
		super( name );
		// TODO Auto-generated constructor stub
		this.setOrigin( this.width / 2 , this.height / 2 );
		Load2DTexture();
		initView();
		Log.v( TAG , "CommonSimulationClock: " + this.x + ", y: " + this.y );
	}
	
	public void initView()
	{
		Log.v( TAG , "widgetClock initView!!!" );
		addWatchView();
		addHourView();
		addMinuteView();
		addSecondView();
		addDotView();
	}
	
	public void Load2DTexture()
	{
		String watchBackFileName = WidgetThemeManager.getInstance().getString( "watch_back_png" );
		String watchHourFileName = WidgetThemeManager.getInstance().getString( "watch_hour_png" );
		String watchMinuteFileName = WidgetThemeManager.getInstance().getString( "watch_minute_png" );
		String watchSecondFileName = WidgetThemeManager.getInstance().getString( "watch_second_png" );
		String watchDotFileName = WidgetThemeManager.getInstance().getString( "watch_dot_png" );
		watchBackTexture = WidgetThemeManager.getInstance().getThemeTexture( watchBackFileName );
		hourGroupTexture = WidgetThemeManager.getInstance().getThemeTexture( watchHourFileName );
		minuteGroupTexture = WidgetThemeManager.getInstance().getThemeTexture( watchMinuteFileName );
		secondGroupTexture = WidgetThemeManager.getInstance().getThemeTexture( watchSecondFileName );
		dotGroupTexture = WidgetThemeManager.getInstance().getThemeTexture( watchDotFileName );
		if( watchBackTexture != null )
		{
			watchBackTexture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
		if( hourGroupTexture != null )
		{
			hourGroupTexture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
		if( minuteGroupTexture != null )
		{
			minuteGroupTexture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
		if( secondGroupTexture != null )
		{
			secondGroupTexture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
		if( dotGroupTexture != null )
		{
			dotGroupTexture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
	}
	
	public void addWatchView()
	{
		if( watchBackTexture == null )
		{
			return;
		}
		watchBack2DView = new ClockMember2DView( "watch_back" , watchBackTexture );
		watchBack2DView.setOrigin( WidgetClock.MODEL_WIDTH / 2 , WidgetClock.MODEL_WIDTH / 2 );
		Log.v( TAG , "watchBack2DViewx: " + watchBack2DView.x + ", y: " + watchBack2DView.y );
		this.addView( watchBack2DView );
	}
	
	public void addHourView()
	{
		if( hourGroupTexture == null )
		{
			return;
		}
		hourGroup2DView = new ClockMember2DView( "clock_hour" , hourGroupTexture );
		this.addView( hourGroup2DView );
	}
	
	public void addMinuteView()
	{
		if( minuteGroupTexture == null )
		{
			return;
		}
		minuteGroup2DView = new ClockMember2DView( "clock_minute" , minuteGroupTexture );
		this.addView( minuteGroup2DView );
	}
	
	public void addSecondView()
	{
		if( secondGroupTexture == null )
		{
			return;
		}
		secondGroup2DView = new ClockMember2DView( "clock_second" , secondGroupTexture );
		this.addView( secondGroup2DView );
	}
	
	public void addDotView()
	{
		if( dotGroupTexture == null )
		{
			return;
		}
		dotGroup2DView = new ClockMember2DView( "clock_dot" , dotGroupTexture );
		this.addView( dotGroup2DView );
	}
	
	public void ClockTimeChanged(
			final float hourRotation ,
			final float minuteRotation ,
			final float secondRotation )
	{
		if( secondGroup2DView != null )
		{
			secondGroup2DView.rotation = secondRotation - 90;
		}
		if( minuteGroup2DView != null )
		{
			minuteGroup2DView.rotation = minuteRotation - 90;
		}
		if( hourGroup2DView != null )
		{
			hourGroup2DView.rotation = hourRotation - 90;
		}
	}
}
