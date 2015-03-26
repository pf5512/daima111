package com.iLoong.Clean4.view;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.preference.PreferenceManager;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.Clean4.R;
import com.iLoong.Clean4.theme.WidgetThemeManager;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.umeng.analytics.MobclickAgent;


public class WidgetClean4 extends WidgetPluginView3D
{
	
	private boolean isOrnotUseCleanMaster = false;
	private static final int BG_WIDTH = 674;
	private static final int BG_HEIGHT = 114;
	private static final int BAR_BG_WIDTH = 526;
	private static final int BAR_BG_HEIGHT = 40;
	private static final int BROOM_WIDTH = 118;//45
	private static final int BROOM_HEIGHT = 114;//54
	public static Canvas canvas = new Canvas();
	public static Paint paint = new Paint();
	public static FontMetrics fontMetrics = new FontMetrics();
	private static boolean isStart = false;
	private TextureRegion region_red;
	private TextureRegion region_yellow;
	private TextureRegion region_green;
	private TextureRegion region_current;
	private long totalMemory;
	private long clearedMemory = 0;
	private long preTime;
	private boolean isFirstIn = true;
	public static MainAppContext mAppContext;
	private Context mContext = null;
	private MainAppContext maincontext;
	public static WidgetClean4 widgetClean4;
	public static float scale = 1f;
	private View3D bg3D = null;
	// private View3D barBg3D = null;
	private View3D bar3D = null;
	private View3D bar3Dcopy = null;
	private View3D broom3D = null;
	private View3D memoryScale3D = null;
	private View3D memoryUse3D = null;
	private View3D memoryFree3D = null;
	private Timeline animation_line = null;
	public static final String ACTION_KILL_PROGRESS = "action_kill_progress";
	private boolean isInitiativeRefresh = false;
	private KillProgressReceiver mKillProgressReceiver;
	private ViewGroup3D widgetgroup = null;
	public static final int LeftMove = 1;
	public static final int RightMove = 2;
	public static final int TopMove = 3;
	public static final int BottomMove = 4;
	private static final String ACTION_CLEAN_OVER = "action_clean_over";
	private static final String ACTION_CLEAN_FINAL = "action_clean_fail";
	private static boolean isAllowOnClick = true;
	
	public WidgetClean4(
			String name ,
			MainAppContext context ,
			int widgetId )
	{
		super( name );
		this.maincontext = context;
		this.mContext = context.mWidgetContext;
		new WidgetThemeManager( context.mGdxApplication );
		mAppContext = context;
		this.width = Utils3D.getScreenWidth();
		this.height = R3D.Workspace_cell_each_height;
		totalMemory = getTotalMemory();
		scale = Utils3D.getScreenWidth() / 720f;
		this.setUser( BAR_BG_WIDTH * scale * ( 1 - getAvailMemory() / (float)totalMemory ) );
		region_current = new TextureRegion();
		region_red = new TextureRegion( getRegion( "bar_red.png" ) );
		region_yellow = new TextureRegion( getRegion( "bar_yellow.png" ) );
		region_green = new TextureRegion( getRegion( "bar_green.png" ) );
		widgetgroup = new ViewGroup3D( "widgetgroup" );
		widgetgroup.setSize( BG_WIDTH * scale , BG_HEIGHT * scale );
		widgetgroup.setPosition( ( this.width - widgetgroup.width ) / 2 , ( this.height - widgetgroup.height ) / 2 );
		widgetgroup.transform = true;
		isOrnotUseCleanMaster = Utils3D.isOrnotFileExists( mContext , "CleanMaster.apk" );
		bg3D = new View3D( "bg3D" , getRegion( "bg.png" ) ) {
			
			@Override
			public boolean onClick(
					float x ,
					float y )
			{
				if( isOrnotUseCleanMaster )
				{
					Messenger.sendMsg( Messenger.MSG_ENTER_MASTER_CLEAN , "WidgetClean4" );
					return super.onClick( x , y );
				}
				else
				{
					return super.onClick( x , y );
				}
			}
		};
		bg3D.setSize( BG_WIDTH * scale , BG_HEIGHT * scale );
		if( Utils3D.getScreenWidth() == 540 && Utils3D.getScreenHeight() == 960 )
		{
			bg3D.setPosition( 0 , 1 );
		}
		else
		{
			bg3D.setPosition( 0 , 0 );
		}
		// barBg3D = new View3D( "barBg3D" , getRegion( "bar_bg.png" ) );
		// barBg3D.setSize( BAR_BG_WIDTH * scale , BAR_BG_HEIGHT * scale );
		// barBg3D.setPosition( ( Utils3D.getScreenWidth() - BG_WIDTH * scale )
		bar3Dcopy = new View3D( "bar3Dcopy" );
		bar3Dcopy.setSize( BAR_BG_WIDTH * scale , BAR_BG_HEIGHT * scale );
		bar3Dcopy.setPosition( ( widgetgroup.width - bg3D.width ) / 2 + 25 * scale , ( widgetgroup.height - bg3D.height ) / 2 + 22 * scale );
		bar3Dcopy.hide();
		bar3D = new View3D( "bar3D" , getRegion( "bar_yellow.png" ) );
		bar3D.setSize( BAR_BG_WIDTH * scale , BAR_BG_HEIGHT * scale );
		bar3D.setPosition( ( widgetgroup.width - bg3D.width ) / 2 + 25 * scale , ( widgetgroup.height - bg3D.height ) / 2 + 22 * scale );
		broom3D = new View3D( "broom3D" , getRegion( "saozhou.png" ) ) {
			
			@Override
			public boolean onClick(
					float x ,
					float y )
			{
				if( isOrnotUseCleanMaster )
				{
					if( isAllowOnClick )
					{
						isAllowOnClick = false;
					}
					else
					{
						return true;
					}
					final long beforeClearMemory = getAvailMemory();
					final long afterClearMemory = getAvailMemory();
					// Log.i( "afterClearMemory" , afterClearMemory + "" );
					clearedMemory = ( afterClearMemory - beforeClearMemory ) / 1024;
					if( animation_line != null )
					{
						animation_line.free();
						animation_line = null;
					}
					float targetUser = BAR_BG_WIDTH * scale * ( 1 - getAvailMemory() / (float)totalMemory );
					animation_line = Timeline.createParallel();
					if( Utils3D.isAPKInstalled( mContext , "com.blueflash.kingscleanmaster" ) )
					{
						/*animation_line.push(Tween  
								.to(WidgetClean4.this, View3DTweenAccessor.USER, 1)
								.target(0f, 0, 0).ease(Linear.INOUT));
						animation_line.push(Tween
								.to(WidgetClean4.this, View3DTweenAccessor.USER, 1)
								.target(targetUser, 0, 0).ease(Linear.INOUT).delay(1f));
						animation_line.start(View3DTweenAccessor.manager).setCallback(
								this);*/
						//isStart = true;
					}
					else
					{
						isStart = false;
						isAllowOnClick = true;
					}
					kill2();
					//mContext.sendBroadcast(new Intent(ACTION_KILL_PROGRESS));
					MobclickAgent.onEventValue( mContext , "CleanClick4" , null , 24 * 60 * 60 );
					return true;
				}
				else
				{
					if( isStart )
					{
						return true;
					}
					isStart = true;
					Log.i( "WidgetClean4" , "onClick" );
					Log.i( "WidgetClean4" , "x:" + x );
					Log.i( "WidgetClean4" , "y:" + y );
					// Log.e( "totalMemory" , getTotalMemory() + "" );
					final long beforeClearMemory = getAvailMemory();
					// Log.i( "beforeClearMemory" , beforeClearMemory + "" );
					long start = System.currentTimeMillis();
					// Log.i( "startTime" , start + "" );
					kill();
					long end = System.currentTimeMillis();
					// Log.i( "endTime" , end + "" );
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis( end - start );
					int seconds = c.get( Calendar.SECOND );
					// Log.i( "seconds" , seconds + "" );
					Log.i( "WidgetClean4" , "seconds:" + seconds );
					final long afterClearMemory = getAvailMemory();
					// Log.i( "afterClearMemory" , afterClearMemory + "" );
					clearedMemory = ( afterClearMemory - beforeClearMemory ) / 1024;
					if( animation_line != null )
					{
						animation_line.free();
						animation_line = null;
					}
					float targetUser = BAR_BG_WIDTH * scale * ( 1 - getAvailMemory() / (float)totalMemory );
					animation_line = Timeline.createParallel();
					animation_line.push( Tween.to( WidgetClean4.this , View3DTweenAccessor.USER , 1 ).target( 0f , 0 , 0 ).ease( Linear.INOUT ) );
					animation_line.push( Tween.to( WidgetClean4.this , View3DTweenAccessor.USER , 1 ).target( targetUser , 0 , 0 ).ease( Linear.INOUT ).delay( 1f ) );
					animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
					MobclickAgent.onEventValue( mContext , "CleanClick4" , null , 24 * 60 * 60 );
					return true;
				}
			}
			
			@Override
			public void onEvent(
					int type ,
					BaseTween source )
			{
				if( source == animation_line && type == TweenCallback.COMPLETE )
				{
					isStart = false;
					animation_line.free();
					animation_line = null;
					if( clearedMemory <= 0 )
					{
						SendMsgToAndroid.sendOurToastMsg( mContext.getResources().getString( R.string.complete_clean ) );
					}
					else
					{
						SendMsgToAndroid.sendOurToastMsg( mContext.getResources().getString( R.string.cleaning , clearedMemory ) );
					}
					mContext.sendBroadcast( new Intent( ACTION_KILL_PROGRESS ) );
					// Log.i( "clearedMemory" , clearedMemory + "" );
				}
			}
		};
		broom3D.setSize( BROOM_WIDTH * scale , BROOM_HEIGHT * scale );
		broom3D.setPosition( ( widgetgroup.width - bg3D.width ) / 2 + 547 * scale , ( widgetgroup.height - broom3D.height ) / 2 );
		memoryScale3D = new View3D( "MemoryScale3D" , getTextureRegion2(
				mAppContext ,
				mContext.getResources().getString( R.string.memory ) + ( Math.round( ( ( 1 - getAvailMemory() / (float)totalMemory ) * 100 ) ) ) + "%" ,
				20f * scale ,
				bg3D.width / 2 ,
				bg3D.height ,
				40 * scale ) );
		memoryScale3D.setPosition( ( widgetgroup.width - bg3D.width ) / 2 , ( widgetgroup.height - bg3D.height ) / 2 );
		memoryUse3D = new View3D( "memoryUse3D" , getTextureRegion( mAppContext , getAvailMemory() / 1024 + "M" , 18f * scale , bar3Dcopy.width , bar3Dcopy.height , 15 * scale ) );
		memoryUse3D.setPosition( ( widgetgroup.width - bg3D.width ) / 2 + 25 * scale , ( widgetgroup.height - bg3D.height ) / 2 + 19 * scale );
		memoryFree3D = new View3D( "memoryUse3D" , getTextureRegion1( mAppContext , getTotalMemory() / 1024 + "M" , 18f * scale , bar3Dcopy.width , bar3Dcopy.height ) );
		memoryFree3D.setPosition( ( widgetgroup.width - bg3D.width ) / 2 + 25 * scale , ( widgetgroup.height - bg3D.height ) / 2 + 19 * scale );
		widgetgroup.addView( bg3D );
		// addView( barBg3D );
		widgetgroup.addView( bar3Dcopy );
		widgetgroup.addView( bar3D );
		widgetgroup.addView( broom3D );
		widgetgroup.addView( memoryScale3D );
		widgetgroup.addView( memoryUse3D );
		widgetgroup.addView( memoryFree3D );
		addView( widgetgroup );
		preTime = System.currentTimeMillis();
		mKillProgressReceiver = new KillProgressReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction( ACTION_KILL_PROGRESS );
		filter.addAction( ACTION_CLEAN_OVER );
		filter.addAction( ACTION_CLEAN_FINAL );
		mContext.registerReceiver( mKillProgressReceiver , filter );
		//mContext.registerReceiver( mKillProgressReceiver , new IntentFilter( ACTION_KILL_PROGRESS ) );
		ChangeResize();
	}
	
	public void cleanOverDraw()
	{
		isStart = true;
		float targetUser = BAR_BG_WIDTH * scale * ( 1 - getAvailMemory() / (float)totalMemory );
		animation_line.push( Tween.to( WidgetClean4.this , View3DTweenAccessor.USER , 1 ).target( 0f , 0 , 0 ).ease( Linear.INOUT ) );
		animation_line.push( Tween.to( WidgetClean4.this , View3DTweenAccessor.USER , 1 ).target( targetUser , 0 , 0 ).ease( Linear.INOUT ).delay( 1f ) );
		Log.i( "WidgetClean4" , "before start" + System.currentTimeMillis() );
		animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
		Log.i( "WidgetClean4" , "after start" + System.currentTimeMillis() );
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source == animation_line && type == TweenCallback.COMPLETE )
		{
			isStart = false;
			isAllowOnClick = true;
			animation_line.free();
			animation_line = null;
			mContext.sendBroadcast( new Intent( ACTION_KILL_PROGRESS ) );
			Log.i( "WidgetClean4" , "onEvent" + System.currentTimeMillis() );
		}
		super.onEvent( type , source );
	}
	
	public void ChangeResize()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		if( sp.getBoolean( "com.iLoong.Clean4" , false ) )
		{
			int spanX = sp.getInt( "com.iLoong.Clean4" + ":spanX" , -1 );
			int spanY = sp.getInt( "com.iLoong.Clean4" + ":spanY" , -1 );
			if( spanX != -1 && spanY != -1 )
			{
				this.width = spanX * R3D.Workspace_cell_each_width;
				this.height = spanY * R3D.Workspace_cell_each_height;
				float nowscale = this.width / R3D.Workspace_cell_each_width / 4;
				scale = nowscale * Utils3D.getScreenWidth() / 720f;
				AdjustX();
			}
		}
	}
	
	@Override
	public void onChangeSize(
			float moveX ,
			float moveY ,
			int what ,
			int cellX ,
			int cellY )
	{
		super.onChangeSize( moveX , moveY , what , cellX , cellY );
		float TempX = 0;
		float TempY = 0;
		if( moveX > 0 )
		{
			if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
			{
				TempX = moveX - moveX % R3D.Workspace_cell_each_width;
				this.width = this.width + TempX;
			}
			else
			{
				TempX = moveX - moveX % R3D.Workspace_cell_each_width + R3D.Workspace_cell_each_width;
				this.width = this.width + TempX;
			}
		}
		else
		{
			if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
			{
				TempX = Math.abs( moveX ) - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
				this.width = this.width - TempX;
			}
			else
			{
				TempX = Math.abs( moveX ) - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) + R3D.Workspace_cell_each_width;
				this.width = this.width - TempX;
			}
		}
		if( moveY > 0 )
		{
			if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
			{
				TempY = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
				this.height = this.height + TempY;
			}
			else
			{
				TempY = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
				this.height = this.height + TempY;
			}
		}
		else
		{
			if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
			{
				TempY = Math.abs( moveY ) - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
				this.height = this.height - TempY;
			}
			else
			{
				TempY = Math.abs( moveY ) - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
				this.height = this.height - TempY;
			}
		}
		float nowscale = 1f;
		switch( what )
		{
			case LeftMove:
				nowscale = this.width / R3D.Workspace_cell_each_width / 4;
				scale = nowscale * Utils3D.getScreenWidth() / 720f;
				//				if( moveX > 0 )
				//				{
				//					this.setPosition( this.x - TempX , this.y );
				//				}else{
				//					this.setPosition( this.x + TempX , this.y );
				//				}
				AdjustX();
				break;
			case RightMove:
				nowscale = this.width / R3D.Workspace_cell_each_width / 4;
				scale = nowscale * Utils3D.getScreenWidth() / 720f;
				AdjustX();
				break;
			case TopMove:
				//				if( moveY > 0 )
				//				{
				//					widgetgroup.setPosition( widgetgroup.x , widgetgroup.y+TempY / 2 );
				//				}
				//				else
				//				{
				//					widgetgroup.setPosition(  widgetgroup.x , widgetgroup.y-TempY / 2 );
				//				}
				widgetgroup.setPosition( ( this.width - widgetgroup.width ) / 2 , ( this.height - widgetgroup.height ) / 2 );
				break;
			case BottomMove:
				widgetgroup.setPosition( ( this.width - widgetgroup.width ) / 2 , ( this.height - widgetgroup.height ) / 2 );
				//				if( moveY > 0 )
				//				{
				//					widgetgroup.setPosition( widgetgroup.x , widgetgroup.y-TempY / 2 );
				//				}
				//				else
				//				{
				//					widgetgroup.setPosition(  widgetgroup.x , widgetgroup.y+TempY / 2 );
				//				}
				break;
			default:
				break;
		}
	}
	
	public void AdjustX()
	{
		this.setUser( BAR_BG_WIDTH * scale * ( 1 - getAvailMemory() / (float)totalMemory ) );
		widgetgroup.setSize( BG_WIDTH * scale , BG_HEIGHT * scale );
		widgetgroup.setPosition( ( this.width - widgetgroup.width ) / 2 , ( this.height - widgetgroup.height ) / 2 );
		bg3D.setSize( BG_WIDTH * scale , BG_HEIGHT * scale );
		broom3D.setSize( BROOM_WIDTH * scale , BROOM_HEIGHT * scale );
		broom3D.setPosition( ( widgetgroup.width - bg3D.width ) / 2 + 547 * scale , ( widgetgroup.height - broom3D.height ) / 2 );
		bar3Dcopy.setSize( BAR_BG_WIDTH * scale , BAR_BG_HEIGHT * scale );
		bar3Dcopy.setPosition( ( widgetgroup.width - bg3D.width ) / 2 + 25 * scale , ( widgetgroup.height - bg3D.height ) / 2 + 22 * scale );
		bar3Dcopy.hide();
		bar3D.setPosition( ( widgetgroup.width - bg3D.width ) / 2 + 25 * scale , ( widgetgroup.height - bg3D.height ) / 2 + 22 * scale );
		float userData = this.getUser();
		if( userData > bar3Dcopy.width * 0.9 )
		{
			region_current = region_red;
			region_current.setU2( userData / ( bar3Dcopy.width ) );
			bar3D.region.setRegion( region_current );
		}
		else if( userData < bar3Dcopy.width * 0.6 )
		{
			region_current = region_green;
			region_current.setU2( userData / ( bar3Dcopy.width ) );
			bar3D.region.setRegion( region_current );
		}
		else
		{
			region_current = region_yellow;
			region_current.setU2( userData / ( bar3Dcopy.width ) );
			bar3D.region.setRegion( region_current );
		}
		bar3D.setSize( userData , bar3Dcopy.height );
		if( memoryScale3D != null )
		{
			memoryScale3D.remove();
			memoryScale3D.dispose();
			memoryScale3D = null;
		}
		if( memoryUse3D != null )
		{
			memoryUse3D.remove();
			memoryUse3D.dispose();
			memoryUse3D = null;
		}
		if( memoryFree3D != null )
		{
			memoryFree3D.remove();
			memoryFree3D.dispose();
			memoryFree3D = null;
		}
		memoryScale3D = new View3D( "MemoryScale3D" , getTextureRegion2(
				mAppContext ,
				mContext.getResources().getString( R.string.memory ) + ( Math.round( ( ( 1 - getAvailMemory() / (float)totalMemory ) * 100 ) ) ) + "%" ,
				20f * scale ,
				bg3D.width / 2 ,
				bg3D.height ,
				40 * scale ) );
		memoryScale3D.setPosition( ( widgetgroup.width - bg3D.width ) / 2 , ( widgetgroup.height - bg3D.height ) / 2 );
		memoryUse3D = new View3D( "memoryUse3D" , getTextureRegion( mAppContext , getAvailMemory() / 1024 + "M" , 18f * scale , bar3Dcopy.width , bar3Dcopy.height , 15 * scale ) );
		memoryUse3D.setPosition( ( widgetgroup.width - bg3D.width ) / 2 + 25 * scale , ( widgetgroup.height - bg3D.height ) / 2 + 19 * scale );
		memoryFree3D = new View3D( "memoryUse3D" , getTextureRegion1( mAppContext , getTotalMemory() / 1024 + "M" , 18f * scale , bar3Dcopy.width , bar3Dcopy.height ) );
		memoryFree3D.setPosition( ( widgetgroup.width - bg3D.width ) / 2 + 25 * scale , ( widgetgroup.height - bg3D.height ) / 2 + 19 * scale );
		widgetgroup.addView( memoryScale3D );
		widgetgroup.addView( memoryUse3D );
		widgetgroup.addView( memoryFree3D );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		this.requestFocus();
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		this.releaseFocus();
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		this.releaseFocus();
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		this.point.x = x;
		this.point.y = y;
		this.toAbsolute( point );
		Vector2 obj = new Vector2( point.x , point.y );
		this.setTag( obj );
		this.releaseFocus();
		return viewParent.onCtrlEvent( this , 0 );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( isStart )
		{
			float userData = this.getUser();
			Log.i( "WidgetClean4 - draw" , "userData:" + userData );
			if( userData > bar3Dcopy.width * 0.9 )
			{
				region_current = region_red;
				region_current.setU2( userData / ( bar3Dcopy.width ) );
				bar3D.region.setRegion( region_current );
			}
			else if( userData < bar3Dcopy.width * 0.6 )
			{
				region_current = region_green;
				region_current.setU2( userData / ( bar3Dcopy.width ) );
				bar3D.region.setRegion( region_current );
			}
			else
			{
				region_current = region_yellow;
				region_current.setU2( userData / ( bar3Dcopy.width ) );
				bar3D.region.setRegion( region_current );
			}
			bar3D.setSize( userData , bar3Dcopy.height );
			if( memoryScale3D.region != null )
			{
				memoryScale3D.region.getTexture().dispose();
			}
			memoryScale3D.region.setRegion( getTextureRegion2(
					mAppContext ,
					mContext.getResources().getString( R.string.memory ) + ( Math.round( ( ( userData / ( (float)bar3Dcopy.width ) ) * 100 ) ) ) + "%" ,
					20f * scale ,
					bg3D.width / 2 ,
					bg3D.height ,
					40 * scale ) );
			if( memoryUse3D.region != null )
			{
				memoryUse3D.region.getTexture().dispose();
			}
			memoryUse3D.region.setRegion( getTextureRegion(
					mAppContext ,
					( Math.round( ( ( userData / ( (float)bar3Dcopy.width ) ) * totalMemory ) / 1024 ) ) + "M" ,
					18f * scale ,
					bar3Dcopy.width ,
					bar3Dcopy.height ,
					15 * scale ) );
			if( memoryFree3D.region != null )
			{
				memoryFree3D.region.getTexture().dispose();
			}
			memoryFree3D.region.setRegion( getTextureRegion1(
					mAppContext ,
					( Math.round( ( ( 1 - userData / ( (float)bar3Dcopy.width ) ) * totalMemory ) / 1024 ) ) + "M" ,
					18f * scale ,
					bar3Dcopy.width ,
					bar3Dcopy.height ) );
		}
		else
		{
			long currTime = System.currentTimeMillis();
			if( currTime - preTime > 30000 || isFirstIn || isInitiativeRefresh )
			{
				preTime = currTime;
				isFirstIn = false;
				float memoryScale = ( 1 - getAvailMemory() / (float)totalMemory );
				if( memoryScale > 0.9 )
				{
					region_current = region_red;
					region_current.setU2( memoryScale );
					bar3D.region.setRegion( region_current );
				}
				else if( memoryScale < 0.6 )
				{
					region_current = region_green;
					region_current.setU2( memoryScale );
					bar3D.region.setRegion( region_current );
				}
				else
				{
					region_current = region_yellow;
					region_current.setU2( memoryScale );
					bar3D.region.setRegion( region_current );
				}
				bar3D.setSize( memoryScale * bar3Dcopy.width , bar3Dcopy.height );
				if( memoryScale3D.region != null )
				{
					memoryScale3D.region.getTexture().dispose();
				}
				memoryScale3D.region.setRegion( getTextureRegion2(
						mAppContext ,
						mContext.getResources().getString( R.string.memory ) + ( Math.round( ( ( 1 - getAvailMemory() / (float)totalMemory ) * 100 ) ) ) + "%" ,
						20f * scale ,
						bg3D.width / 2 ,
						bg3D.height ,
						40 * scale ) );
				if( memoryUse3D.region != null )
				{
					memoryUse3D.region.getTexture().dispose();
				}
				memoryUse3D.region.setRegion( getTextureRegion( mAppContext , ( totalMemory - getAvailMemory() ) / 1024 + "M" , 18f * scale , bar3Dcopy.width , bar3Dcopy.height , 15 * scale ) );
				if( memoryFree3D.region != null )
				{
					memoryFree3D.region.getTexture().dispose();
				}
				memoryFree3D.region.setRegion( getTextureRegion1( mAppContext , ( getAvailMemory() ) / 1024 + "M" , 18f * scale , bar3Dcopy.width , bar3Dcopy.height ) );
				isInitiativeRefresh = false;
			}
		}
		super.draw( batch , parentAlpha );
	}
	
	@Override
	public WidgetPluginViewMetaData getPluginViewMetaData()
	{
		WidgetPluginViewMetaData metaData = new WidgetPluginViewMetaData();
		metaData.spanX = 4;
		metaData.spanY = 1;
		metaData.maxInstanceCount = 1;
		String lan = Locale.getDefault().getLanguage();
		if( lan.equals( "zh" ) )
		{
			metaData.maxInstanceAlert = "已存在，不可重复添加";
		}
		else
		{
			metaData.maxInstanceAlert = "Already exists, can not add another one";
		}
		return metaData;
	}
	
	private TextureRegion getRegion(
			String name )
	{
		try
		{
			BitmapTexture bt = new BitmapTexture( BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( "theme/widget/clean4/iLoongClean4/image/" + name ) ) , true );
			bt.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			TextureRegion mBackRegion = new TextureRegion( bt );
			return mBackRegion;
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		// Messenger.sendMsg(Messenger., obj);
		return null;
	}
	
	private int clearedAppCount = 0;
	
	private void kill2()
	{
		Messenger.sendMsg( Messenger.MSG_WIDGETClEAN4_CLEAN , "WidgetClean4" );
	}
	
	private void kill()
	{
		clearedAppCount = 0;
		ActivityManager activityManager = (ActivityManager)mAppContext.mWidgetContext.getSystemService( Context.ACTIVITY_SERVICE );
		List<ActivityManager.RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
		if( list != null )
		{
			for( int i = 0 ; i < list.size() ; i++ )
			{
				ActivityManager.RunningAppProcessInfo appInfo = list.get( i );
				String[] pkgList = appInfo.pkgList;
				if( appInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE )
				{
					for( int j = 0 ; j < pkgList.length ; j++ )
					{
						activityManager.killBackgroundProcesses( pkgList[j] );
						clearedAppCount++;
					}
				}
			}
		}
		// Log.i( "----------KILL----------" , clearedAppCount + "" );
	}
	
	private long getAvailMemory()
	{
		long MEM_UNUSED;
		// 得到ActivityManager
		ActivityManager am = (ActivityManager)mContext.getSystemService( Context.ACTIVITY_SERVICE );
		// 创建ActivityManager.MemoryInfo对象
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo( mi );
		// 取得剩余的内存空间
		MEM_UNUSED = mi.availMem / 1024;
		return MEM_UNUSED;
	}
	
	private long getTotalMemory()
	{
		long mTotal;
		// /proc/meminfo读出的内核信息进行解释
		String path = "/proc/meminfo";
		String content = null;
		BufferedReader br = null;
		try
		{
			br = new BufferedReader( new FileReader( path ) , 8 );
			String line;
			if( ( line = br.readLine() ) != null )
			{
				content = line;
			}
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		finally
		{
			if( br != null )
			{
				try
				{
					br.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
		// /proc/meminfo读出的内核信息进行解释
		// Log.i("WidgetClean4", "content:"+content);
		// beginIndex
		int begin = content.indexOf( ':' );
		// endIndex
		int end = content.indexOf( 'k' );
		// 截取字符串信息
		content = content.substring( begin + 1 , end ).trim();
		mTotal = Integer.parseInt( content );
		return mTotal;
	}
	
	public TextureRegion getTextureRegion(
			MainAppContext appContext ,
			String title ,
			float textSize ,
			float width ,
			float height ,
			float x )
	{
		Bitmap backImage = null;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );//.TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );//防锯齿
		paint.setDither( true );//防抖动
		paint.setColor( Color.WHITE );
		paint.setSubpixelText( true );
		//		paint.setStrokeJoin( Paint.Join.ROUND );
		//		paint.setStrokeCap( Paint.Cap.ROUND );
		paint.setTextSize( textSize );
		//		paint.setShadowLayer( 2.0f , 0.0f , 2.0f , 0xDD000000 );
		canvas.drawText( title , x , posY , paint );
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		backImage.recycle();
		return newTextureRegion;
	}
	
	public TextureRegion getTextureRegion2(
			MainAppContext appContext ,
			String title ,
			float textSize ,
			float width ,
			float height ,
			float x )
	{
		Bitmap backImage = null;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );//.TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );//防锯齿
		paint.setDither( true );//防抖动
		paint.setColor( Color.WHITE );
		paint.setSubpixelText( true );
		//		paint.setStrokeJoin( Paint.Join.ROUND );
		//		paint.setStrokeCap( Paint.Cap.ROUND );
		paint.setTextSize( textSize );
		//		paint.setShadowLayer( 2.0f , 0.0f , 2.0f , 0xDD000000 );
		canvas.drawText( title , x , posY - 20 * scale , paint );
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		backImage.recycle();
		return newTextureRegion;
	}
	
	public TextureRegion getTextureRegion1(
			MainAppContext appContext ,
			String title ,
			float textSize ,
			float width ,
			float height )
	{
		Bitmap backImage = null;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );//.TRANSPARENT .TRANSPARENT
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		Paint paint = new Paint();
		paint.setAntiAlias( true );//防锯齿
		paint.setDither( true );//防抖动
		paint.setColor( Color.WHITE );
		paint.setSubpixelText( true );
		//		paint.setStrokeJoin( Paint.Join.ROUND );
		//		paint.setStrokeCap( Paint.Cap.ROUND );
		paint.setTextSize( textSize );
		//		paint.setShadowLayer( 2.0f , 0.0f , 2.0f , 0xDD000000 );
		canvas.drawText( title , width - paint.measureText( title ) - 15 * scale , posY , paint );
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		backImage.recycle();
		return newTextureRegion;
	}
	
	class KillProgressReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context context ,
				Intent intent )
		{
			// Log.d( "kill" , "killProgressReceiver4" );
			if( intent.getAction().equals( ACTION_KILL_PROGRESS ) )
			{
				//Log.i("Receive", "Receive---------WidgetClean4");
				isInitiativeRefresh = true;
			}
			else if( intent.getAction().equals( ACTION_CLEAN_OVER ) )
			{
				Log.i( "KillProgressReceiverReceive" , "clean-----over" );
				cleanOverDraw();
			}
			else if( intent.getAction().equals( ACTION_CLEAN_FINAL ) )
			{
				Log.i( "KillProgressReceiverReceive" , "clean-----final" );
				isAllowOnClick = true;
			}
		}
	}
}
