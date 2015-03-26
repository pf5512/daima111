package com.iLoong.Clock.View;


import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class ClockSecondGroup extends ViewGroup3D
{
	
	public static final String WATCH_SECONDHAND_OBJ = "test_trans.obj";// "watch_secondhand.obj";
	private Cache<String , Mesh> mMeshCache = null;
	private CooGdx cooGdx;
	private MainAppContext mAppContext;
	private Map<Integer , View3D> mViews;
	
	public ClockSecondGroup(
			String name ,
			MainAppContext appContext ,
			Cache<String , Mesh> cache ,
			CooGdx cooGdx )
	{
		this.cooGdx = cooGdx;
		mAppContext = appContext;
		mMeshCache = cache;
		mViews = new HashMap<Integer , View3D>();
		this.x = 0;
		this.y = 0;
		this.transform = true;
		//this.region.setRegion(region);
		this.width = WidgetClock.MODEL_WIDTH;
		this.height = WidgetClock.MODEL_HEIGHT;
		this.setOrigin( width / 2 , height / 2 );
		initViews();
	}
	
	public void initViews()
	{
		int angle = 0;
		for( int i = 1 ; i < 19 ; i++ )
		{
			ObjBaseView view = new ObjBaseView( "subView_" + i , mAppContext , mMeshCache , "mask.png" , "mask.obj" , 0 );//"mask.obj"
			int r = ( 360 - angle + i * 20 ) % 360;
			view.setRotationAngle( 0 , 0 , r );
			//  view.setBlendSrcAlpha(0);
			view.hide();
			mViews.put( i , view );
			// view.enableDepthMode(true);
			this.addView( view );
		}
	}
	
	public void updateSecondHand(
			float rotationAngle )
	{
		boolean bool = false;
		Calendar mCalendar = Calendar.getInstance();
		long millis = System.currentTimeMillis();
		int second = ( (int)( millis / 1000 ) ) % 180;
		if( second == 0 )
		{
			second = 180;
		}
		int index = second % 18;
		if( index == 0 )
		{
			index = 18;
		}
		if( second % 18 == 0 )
		{
			bool = ( ( second - 1 ) / 18 ) % 2 == 0;
		}
		else
		{
			bool = ( ( second ) / 18 ) % 2 == 0;
		}
		if( bool )
		{
			for( int i = 1 ; i <= index ; i++ )
			{
				mViews.get( 19 - i ).show();
			}
			for( int j = index + 1 ; j < 19 ; j++ )
			{
				mViews.get( 19 - j ).hide();
			}
		}
		else
		{
			for( int i = 1 ; i <= index ; i++ )
			{
				mViews.get( 19 - i ).hide();
			}
			for( int j = index + 1 ; j < 19 ; j++ )
			{
				mViews.get( 19 - j ).show();
			}
		}
	}
	
	public void updateSecondHandEx(
			float rotationAngle )
	{
		boolean bool = false;
		Calendar mCalendar = Calendar.getInstance();
		long millis = System.currentTimeMillis();
		int second = ( (int)( millis / 1000 ) ) % 180;
		int index = second % 36;
		if( index == 0 )
		{
			index = 36;
		}
		if( second % 36 == 0 )
		{
			bool = ( ( second - 1 ) / 36 ) % 2 == 0;
		}
		else
		{
			bool = ( ( second ) / 36 ) % 2 == 0;
		}
		if( bool )
		{
			for( int i = 1 ; i <= index ; i++ )
			{
				mViews.get( 37 - i ).show();
			}
			for( int j = index + 1 ; j < 36 ; j++ )
			{
				mViews.get( 37 - j ).hide();
			}
		}
		else
		{
			for( int i = 1 ; i <= index ; i++ )
			{
				mViews.get( 37 - i ).hide();
			}
			for( int j = index + 1 ; j < 37 ; j++ )
			{
				mViews.get( 37 - j ).show();
			}
		}
	}
}
