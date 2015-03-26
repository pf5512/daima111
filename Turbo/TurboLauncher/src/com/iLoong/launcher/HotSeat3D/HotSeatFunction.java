package com.iLoong.launcher.HotSeat3D;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.NPageBase;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.HotSeat3D.MusicHelper.OnPlayStateChanged;
import com.iLoong.launcher.HotSeat3D.SwitchHelper.SwitchHelperCallBack;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Desktop3D;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.action.ActionData;
import com.iLoong.launcher.action.ActionHolder;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.umeng.analytics.MobclickAgent;


public class HotSeatFunction extends NPageBase
{
	
	public static Paint paint = new Paint();
	public static Canvas canvas = new Canvas();
	public static FontMetrics fontMetrics = new FontMetrics();
	public float titleGap = measureSize( 15 );
	public final String PAUSE = "pause";
	public final String PLAY = "play";
	public final String SOUND = "sound";
	public final String WIFI = "wifi";
	public final String GPS = "gps";
	public final String BLUETOOTH = "bluetooth";
	public final String BRIGHTNESS = "brightness";
	public final String SHORTCUT_DESKSETTING = "SHORTCUT_DESKSETTING";
	public final String GPRS = "gprs";
	private TextureRegion pauseRegion = null;
	private TextureRegion playRegion = null;
	private TextureRegion soundOffRegion = null;
	private TextureRegion soundOnRegion = null;
	private TextureRegion wifiOpenedRegion = null;
	private TextureRegion wifiClosedRegion = null;
	private TextureRegion gpsOpenedRegion = null;
	private TextureRegion gpsClosedRegion = null;
	private TextureRegion bluetoothOpenedRegion = null;
	private TextureRegion bluetoothClosedRegion = null;
	private TextureRegion brightnessLowRegion = null;
	private TextureRegion brightnessMiddleRegion = null;
	private TextureRegion brightnessHighRegion = null;
	private TextureRegion brightnessAutoRegion = null;
	private TextureRegion gprsOpenedRegion = null;
	private TextureRegion gprsClosedRegion = null;
	private NinePatch hightlight;
	public Map<String , View3D> viewCache = new HashMap<String , View3D>();
	final String TAG = "HotSeatFunction";
	LinkedList<View3D> view_list = new LinkedList<View3D>();
	SwitchHelper switchHelper;
	MenuHelper menuHelper;
	MusicHelper musicHelper;
	HashMap<String , HashMap<Float , ImageView3D>> titleTexture;
	final int paddingTop = 30;
	final int paddingBottom = 30;
	
	public HotSeatFunction(
			String name ,
			float width ,
			float height )
	{
		super( name );
		this.width = width;
		this.height = height;
		setOrigin( this.width / 2 , this.height / 2 );
		hightlight = new NinePatch( new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/dock3dbar/highlight.png" ) ) ) );
		initFunctions();
		this.setEffectType( APageEase.COOLTOUCH_EFFECT_DEFAULT );
	}
	
	public void loadSwitchItems()
	{
		switchHelper = new SwitchHelper( iLoongLauncher.getInstance() , mSwitchHelperCB );
		ViewGroup3D layout = (ViewGroup3D)getChildAt( 2 );
		layout.removeAllViews();
		FunctionView wifi = new FunctionView( "wifi" , FunctionView.switch_wifi );
		wifi.setPosition( measureSize( 40 ) , ( this.height - wifi.height ) / 2 );
		layout.addView( wifi );
		viewCache.put( WIFI , wifi );
		FunctionView bluetooth = new FunctionView( "bluetooth" , FunctionView.switch_bluetooth );
		bluetooth.setPosition( wifi.x + wifi.width + measureSize( 60 ) , ( this.height - bluetooth.height ) / 2 );
		layout.addView( bluetooth );
		viewCache.put( BLUETOOTH , bluetooth );
		FunctionView net = new FunctionView( "net" , FunctionView.switch_net );
		net.setPosition( ( this.width - net.width ) / 2 , ( this.height - net.height ) / 2 );
		layout.addView( net );
		viewCache.put( GPRS , net );
		FunctionView bright = new FunctionView( "bright" , FunctionView.switch_bright );
		bright.setPosition( this.width - bright.width - measureSize( 40 ) , ( this.height - bright.height ) / 2 );
		layout.addView( bright );
		viewCache.put( BRIGHTNESS , bright );
		FunctionView gps = new FunctionView( "gps" , FunctionView.switch_gps );
		gps.setPosition( this.width - gps.width - measureSize( 40 ) - bright.width - measureSize( 60 ) , ( this.height - gps.height ) / 2 );
		layout.addView( gps );
		viewCache.put( GPS , gps );
	}
	
	public void setMyEffectType()
	{
		this.setEffectType( APageEase.COOLTOUCH_EFFECT_DEFAULT );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( deltaX == 0 || Math.abs( deltaY ) / Math.abs( deltaX ) > 1 )
		{
			if( iLoongLauncher.getInstance().d3dListener.d3d.mScrollTempDir == Desktop3D.SCROLL_UNINITED )
				iLoongLauncher.getInstance().d3dListener.d3d.mScrollTempDir = Desktop3D.SCROLL_VERTICAL;
			return viewParent.scroll( x , y , deltaX , deltaY );
		}
		if( iLoongLauncher.getInstance().d3dListener.d3d.mScrollTempDir == Desktop3D.SCROLL_UNINITED )
			iLoongLauncher.getInstance().d3dListener.d3d.mScrollTempDir = Desktop3D.SCROLL_HORIZ0TAL;
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	public void loadMusicItems()
	{
		ViewGroup3D layout = (ViewGroup3D)this.getChildAt( 0 );
		layout.removeAllViews();
		musicHelper = MusicHelper.getInstance( iLoongLauncher.getInstance() );
		musicHelper.setOnPlayStateListerner( playStateListener );
		FunctionView Sound = new FunctionView( "Sound" , FunctionView.music_sound );
		Sound.x = measureSize( 42 );
		Sound.y = ( this.height - Sound.height ) / 2.0f;
		layout.addView( Sound );//addItem( Sound );
		viewCache.put( SOUND , Sound );
		FunctionView Pause = new FunctionView( "Pause" , FunctionView.music_pause );
		Pause.x = ( this.width - Pause.width ) / 2;
		Pause.y = ( this.height - Pause.height ) / 2.0f;
		layout.addView( Pause );
		viewCache.put( PAUSE , Pause );
		FunctionView PreSong = new FunctionView( "PreSong" , FunctionView.music_previous );
		PreSong.x = Pause.x - measureSize( 60 ) - PreSong.width;
		PreSong.y = ( this.height - Sound.height ) / 2.0f;
		PreSong.region.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		layout.addView( PreSong );
		viewCache.put( "PreSong" , PreSong );
		FunctionView NextSong = new FunctionView( "PreSong" , FunctionView.music_next );
		NextSong.x = Pause.x + Pause.width + measureSize( 60 );
		NextSong.y = ( this.height - PreSong.height ) / 2.0f;
		layout.addView( NextSong );
		viewCache.put( "NextSong" , NextSong );
		FunctionView SongList = new FunctionView( "SongList" , FunctionView.music_list );
		SongList.x = this.width - SongList.width - +measureSize( 42 );
		SongList.y = ( this.height - SongList.height ) / 2.0f;
		layout.addView( SongList );
		viewCache.put( "SongList" , SongList );
	}
	
	public void initPages()
	{
		for( int i = 0 ; i < 3 ; i++ )
		{
			ViewGroup3D grid = new ViewGroup3D( "gridview" );
			grid.x = 0;
			grid.y = 0;
			grid.height = this.height;
			grid.width = this.width;
			grid.transform = true;
			addPage( i , grid );
		}
	}
	
	public int getPageCount()
	{
		return 3;
	}
	
	public void loadMenu()
	{
		menuHelper = new MenuHelper();
		loadMenuItems();
	}
	
	public void loadMenuItems()
	{
		titleTexture = new HashMap<String , HashMap<Float , ImageView3D>>();
		ViewGroup3D layout = (ViewGroup3D)getChildAt( 1 );
		layout.removeAllViews();
		FunctionView deskSetting = new FunctionView( FunctionView.shortcut_desksetting , FunctionView.shortcut_desksetting );
		deskSetting.setPosition( measureSize( 40 ) , ( this.height - measureSize( 30 ) - deskSetting.height ) );
		layout.addView( deskSetting );
		ImageView3D dstitle = new ImageView3D( deskSetting.title , getTextureRegion( deskSetting.title ) );
		dstitle.setPosition( deskSetting.x + deskSetting.width / 2 - dstitle.width / 2 , measureSize( 30 ) );
		layout.addView( dstitle );
		ActionView dsa = new ActionView();
		dsa.setLabel( FunctionView.shortcut_desksetting );
		dsa.setSize( dstitle.width , this.height - measureSize( paddingTop ) - measureSize( paddingBottom ) );
		dsa.setPosition( dstitle.x , dstitle.y );
		layout.addView( dsa );
		//=========================================================
		FunctionView edit = new FunctionView( FunctionView.shortcut_edit , FunctionView.shortcut_edit );
		edit.setPosition( deskSetting.x + deskSetting.width + measureSize( 60 ) , this.height - edit.height - measureSize( 30 ) );
		layout.addView( edit );
		ImageView3D edtitle = new ImageView3D( edit.title , getTextureRegion( edit.title ) );
		edtitle.setPosition( edit.x + edit.width / 2 - edtitle.width / 2 , measureSize( 30 ) );
		layout.addView( edtitle );
		ActionView ea = new ActionView();
		ea.setLabel( FunctionView.shortcut_edit );
		ea.setSize( edtitle.width , this.height - measureSize( paddingTop ) - measureSize( paddingBottom ) );
		ea.setPosition( edtitle.x , edtitle.y );
		layout.addView( ea );
		//===========================================================
		FunctionView add = new FunctionView( FunctionView.shortcut_add , FunctionView.shortcut_add );
		add.setPosition( ( this.width - add.width ) / 2 , this.height - add.height - measureSize( 30 ) );
		layout.addView( add );
		ImageView3D addtitle = new ImageView3D( add.title , getTextureRegion( add.title ) );
		addtitle.setPosition( add.x + add.width / 2 - addtitle.width / 2 , measureSize( 30 ) );
		layout.addView( addtitle );
		ActionView aa = new ActionView();
		aa.setLabel( FunctionView.shortcut_add );
		aa.setSize( addtitle.width , this.height - measureSize( paddingTop ) - measureSize( paddingBottom ) );
		aa.setPosition( addtitle.x , addtitle.y );
		layout.addView( aa );
		//==========================================================
		FunctionView beautify = new FunctionView( FunctionView.shortcut_beautify , FunctionView.shortcut_beautify );
		beautify.setPosition( this.width - beautify.width - measureSize( 40 ) , this.height - beautify.height - measureSize( 30 ) );
		layout.addView( beautify );
		ImageView3D bytitle = new ImageView3D( beautify.title , getTextureRegion( beautify.title ) );
		bytitle.setPosition( beautify.x + beautify.width / 2 - bytitle.width / 2 , measureSize( 30 ) );
		layout.addView( bytitle );
		ActionView ba = new ActionView();
		ba.setLabel( FunctionView.shortcut_beautify );
		ba.setSize( bytitle.width , this.height - measureSize( paddingTop ) - measureSize( paddingBottom ) );
		ba.setPosition( bytitle.x , bytitle.y );
		layout.addView( ba );
		//============================================================
		FunctionView sysSetting = new FunctionView( FunctionView.shortcut_systemsetting , FunctionView.shortcut_systemsetting );
		sysSetting.setPosition( this.width - beautify.width - measureSize( 40 ) - sysSetting.width - measureSize( 60 ) , this.height - sysSetting.height - measureSize( 30 ) );
		layout.addView( sysSetting );
		ImageView3D sstitle = new ImageView3D( sysSetting.title , getTextureRegion( sysSetting.title ) );
		sstitle.setPosition( sysSetting.x + sysSetting.width / 2 - sstitle.width / 2 , measureSize( 30 ) );
		layout.addView( sstitle );
		ActionView sa = new ActionView();
		sa.setLabel( FunctionView.shortcut_systemsetting );
		sa.setSize( sstitle.width , this.height - measureSize( paddingTop ) - measureSize( paddingBottom ) );
		sa.setPosition( sstitle.x , sstitle.y );
		layout.addView( sa );
	}
	
	public void initFunctions()
	{
		initPages();
		loadMusicItems();
		loadMenu();
		loadSwitchItems();
	}
	
	/**
	 * 
	 * Specification: this class is only for dealing with action of {@link #onClick} for menu page}
	 *  
	 * **/
	class ActionView extends ViewGroup3D
	{
		
		public String label;
		
		public void setLabel(
				String label )
		{
			this.label = label;
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			Log.v( TAG , "ActionView onClick" );
			if( label.equals( FunctionView.shortcut_desksetting ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatToDSetting" );
				menuHelper.onDesktopSetting();
			}
			else if( label.equals( FunctionView.shortcut_edit ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatToPreview" );
				menuHelper.onScreenEdit();
			}
			else if( label.equals( FunctionView.shortcut_add ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatToEditModel" );
				menuHelper.onAdd();
			}
			else if( label.equals( FunctionView.shortcut_systemsetting ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatToSysSetting" );
				menuHelper.onSystemSetting();
			}
			else if( label.equals( FunctionView.shortcut_beautify ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatToUiCenter" );
				menuHelper.onBeautify();
			}
			return true;
		}
		
		@Override
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			View3D v = this.viewParent.findView( this.label );
			if( v != null )
				return v.onTouchDown( x , y , pointer );
			else
				return super.onTouchDown( x , y , pointer );
		}
		
		//
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			View3D v = this.viewParent.findView( this.label );
			if( v != null )
				return v.onTouchUp( x , y , pointer );
			else
				return super.onTouchUp( x , y , pointer );
		}
		
		@Override
		public boolean fling(
				float velocityX ,
				float velocityY )
		{
			View3D v = this.viewParent.findView( this.label );
			if( v != null )
				return v.fling( velocityX , velocityY );
			else
				return super.fling( velocityX , velocityY );
		}
		
		@Override
		public boolean scroll(
				float x ,
				float y ,
				float deltaX ,
				float deltaY )
		{
			View3D v = this.viewParent.findView( this.label );
			if( v != null )
				return v.scroll( x , y , deltaX , deltaY );
			else
				return super.scroll( x , y , deltaX , deltaY );
		}
	}
	
	class FunctionView extends View3D
	{
		
		public static final String music_sound = "music_sound";
		public static final String music_previous = "music_previous";
		public static final String music_pause = "music_pause";
		public static final String music_next = "music_next";
		public static final String music_list = "music_list";
		//page 2
		public static final String shortcut_desksetting = "shortcut_desksetting";
		public static final String shortcut_edit = "shortcut_edit";
		public static final String shortcut_add = "shortcut_add";
		public static final String shortcut_systemsetting = "shortcut_systemsetting";
		public static final String shortcut_beautify = "shortcut_beautify";
		//page 3
		public static final String switch_wifi = "switch_wifi";
		public static final String switch_bluetooth = "switch_bluetooth";
		public static final String switch_net = "switch_net";
		public static final String switch_gps = "switch_gps";
		public static final String switch_bright = "switch_bright";
		public String label = "";
		public Paint paint = new Paint();
		public Canvas canvas = new Canvas();
		public FontMetrics fontMetrics = new FontMetrics();
		String title = "";
		public int titleHeiht = (int)getTitleHeight();
		
		public FunctionView(
				String name ,
				String label )
		{
			super( name );
			this.label = label;
			getTextureRegion();
		}
		
		public void getTextureRegion()
		{
			String path = "theme/dock3dbar/";
			if( label.equals( music_sound ) )
			{
				this.width = measureSize( 61 );
				this.height = measureSize( 64 );
				path += "icon_soundoff.png";
				soundOffRegion = getBitmapRegion( "theme/dock3dbar/icon_soundoff.png" , (int)this.width );
				soundOffRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				soundOnRegion = getBitmapRegion( "theme/dock3dbar/icon_soundon.png" , (int)this.width );
				soundOnRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				this.region = soundOnRegion;
			}
			else if( label.equals( music_previous ) )
			{
				this.width = this.height = measureSize( 64 );
				if( Utils3D.getScreenWidth() <= 540 )
				{
					path += "icon_previous_small.png";
				}
				else
				{
					path += "icon_previous.png";
				}
				this.region = getBitmapRegion( path , (int)this.width );
			}
			else if( label.equals( music_pause ) )
			{
				this.width = this.height = measureSize( 80 );
				path += "icon_play.png";
				if( Utils3D.getScreenWidth() <= 540 )
				{
					pauseRegion = getBitmapRegion( "theme/dock3dbar/icon_pause_small.png" , (int)this.width );
					playRegion = getBitmapRegion( "theme/dock3dbar/icon_play_small.png" , (int)this.width );
				}
				else
				{
					pauseRegion = getBitmapRegion( "theme/dock3dbar/icon_pause_big.png" , (int)this.width );
					playRegion = getBitmapRegion( "theme/dock3dbar/icon_play_big.png" , (int)this.width );
				}
				pauseRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				playRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				this.region = playRegion;
			}
			else if( label.equals( music_next ) )
			{
				this.width = measureSize( 64 );
				this.height = measureSize( 64 );
				if( Utils3D.getScreenWidth() <= 540 )
				{
					path += "icon_next_small.png";
				}
				else
				{
					path += "icon_next.png";
				}
				this.region = getBitmapRegion( path , (int)this.width );
			}
			else if( label.equals( music_list ) )
			{
				this.width = measureSize( 61 );
				this.height = measureSize( 64 );
				path += "icon_list.png";
				this.region = getBitmapRegion( path , (int)this.width );
			}
			else if( label.equals( shortcut_desksetting ) )
			{
				this.width = measureSize( 80 );
				this.height = measureSize( 80 );
				path += "icon_desktop set.png";
				title = iLoongLauncher.getInstance().getString( RR.string.desktop_setting );
				this.region = getBitmapRegion( path , (int)this.width );
			}
			else if( label.equals( shortcut_edit ) )
			{
				this.width = measureSize( 80 );
				this.height = measureSize( 80 );
				path += "icon_edit.png";
				title = iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_preview );
				this.region = getBitmapRegion( path , (int)this.width );
			}
			else if( label.equals( shortcut_add ) )
			{
				this.width = measureSize( 80 );
				this.height = measureSize( 80 );
				if( Utils3D.getScreenWidth() <= 540 )
				{
					path += "icon_add_small.png";
				}
				else
				{
					path += "icon_add.png";
				}
				title = iLoongLauncher.getInstance().getString( RR.string.title_tab_add );
				this.region = getBitmapRegion( path , (int)this.width );
			}
			else if( label.equals( shortcut_systemsetting ) )
			{
				this.width = measureSize( 80 );
				this.height = measureSize( 80 );
				path += "icon_system set.png";
				title = iLoongLauncher.getInstance().getString( RR.string.system_setting );
				this.region = getBitmapRegion( path , (int)this.width );
			}
			else if( label.equals( shortcut_beautify ) )
			{
				this.width = measureSize( 80 );
				this.height = measureSize( 80 );
				path += "icon_beautify.png";
				title = iLoongLauncher.getInstance().getString( RR.string.virtue_personal_center );
				this.region = getBitmapRegion( path , (int)this.width );
			}
			else if( label.equals( switch_wifi ) )
			{
				this.width = measureSize( 80 );
				this.height = measureSize( 80 );
				path += "icon_wifi_open.png";
				title = iLoongLauncher.getInstance().getString( RR.string.dockbar_musicoff );
				wifiOpenedRegion = getBitmapRegion( "theme/dock3dbar/icon_wifi_open.png" , (int)this.width );
				wifiClosedRegion = getBitmapRegion( "theme/dock3dbar/icon_wifi_close.png" , (int)this.width );
				if( switchHelper.getWifiState() )
					this.region = wifiOpenedRegion;//getBitmapRegion( path , (int)this.width );
				else
					this.region = wifiClosedRegion;//getBitmapRegion( path , (int)this.width );
			}
			else if( label.equals( switch_bluetooth ) )
			{
				this.width = measureSize( 80 );
				this.height = measureSize( 80 );
				path += "icon_bluetooth_open.png";
				bluetoothOpenedRegion = getBitmapRegion( "theme/dock3dbar/icon_bluetooth_open.png" , (int)this.width );
				bluetoothClosedRegion = getBitmapRegion( "theme/dock3dbar/icon_bluetooth_close.png" , (int)this.width );
				if( switchHelper.getBlueToothState() == SwitchHelper.BLUETOOTH_START )
				{
					this.region = bluetoothOpenedRegion;// getBitmapRegion( path , (int)this.width );
				}
				else
				{
					this.region = bluetoothClosedRegion;// getBitmapRegion( path , (int)this.width );
				}
			}
			else if( label.equals( switch_net ) )
			{
				this.width = measureSize( 80 );
				this.height = measureSize( 80 );
				path += "icon_data_open.png";
				gprsOpenedRegion = getBitmapRegion( "theme/dock3dbar/icon_data_open.png" , (int)this.width );
				gprsClosedRegion = getBitmapRegion( "theme/dock3dbar/icon_data_close.png" , (int)this.width );
				if( switchHelper.getDataState() )
				{
					this.region = gprsOpenedRegion;
				}
				else
				{
					this.region = gprsClosedRegion;
				}
			}
			else if( label.equals( switch_gps ) )
			{
				this.width = measureSize( 80 );
				this.height = measureSize( 80 );
				path += "icon_gps_open.png";
				if( Utils3D.getScreenWidth() <= 540 )
				{
					gpsOpenedRegion = getBitmapRegion( "theme/dock3dbar/icon_gps_open_small.png" , (int)this.width );
					gpsClosedRegion = getBitmapRegion( "theme/dock3dbar/icon_gps_close_small.png" , (int)this.width );
				}
				else
				{
					gpsOpenedRegion = getBitmapRegion( "theme/dock3dbar/icon_gps_open_big.png" , (int)this.width );
					gpsClosedRegion = getBitmapRegion( "theme/dock3dbar/icon_gps_close_big.png" , (int)this.width );
				}
				if( switchHelper.getGpsState() )
					this.region = gpsOpenedRegion;
				else
					this.region = gpsClosedRegion;
			}
			else if( label.equals( switch_bright ) )
			{
				this.width = measureSize( 80 );
				this.height = measureSize( 80 );
				path += "icon_all_ bright.png";
				int state = switchHelper.getBrightnessState();
				brightnessLowRegion = getBitmapRegion( "theme/dock3dbar/icon_no_bright.png" , (int)this.width );
				brightnessMiddleRegion = getBitmapRegion( "theme/dock3dbar/icon_ban_bright.png" , (int)this.width );
				brightnessHighRegion = getBitmapRegion( "theme/dock3dbar/icon_all_bright.png" , (int)this.width );
				brightnessAutoRegion = getBitmapRegion( "theme/dock3dbar/icon_auto_bright.png" , (int)this.width );
				if( state == SwitchHelper.BRIGHTNESS_AUTO )
				{
					this.region = brightnessAutoRegion;
				}
				else if( state == SwitchHelper.BRIGHTNESS_HIGH )
				{
					this.region = brightnessHighRegion;
				}
				else if( state == SwitchHelper.BRIGHTNESS_MIDDLE )
				{
					this.region = brightnessLowRegion;
				}
				else
				{
					this.region = brightnessMiddleRegion;
				}
			}
		}
		
		public boolean onClick(
				float x ,
				float y )
		{
			if( label.equals( music_sound ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatMusicSoundSwitch" );
				musicHelper.playSound();
			}
			else if( label.equals( music_previous ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatPreSoundSwitch" );
				musicHelper.prevMusic();
			}
			else if( label.equals( music_pause ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatPlayMusicSwitch" );
				musicHelper.togglePlay();
			}
			else if( label.equals( music_next ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatPauseSoundSwitch" );
				musicHelper.nextMusic();
			}
			else if( label.equals( music_list ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatMusicListSwitch" );
				musicHelper.entryList();
			}
			//			else if( label.equals( shortcut_desksetting ) )
			//			{
			//				menuHelper.onDesktopSetting();
			//			}
			//			else if( label.equals( shortcut_edit ) )
			//			{
			//				menuHelper.onScreenEdit();
			//			}
			//			else if( label.equals( shortcut_add ) )
			//			{
			//				menuHelper.onAdd();
			//			}
			//			else if( label.equals( shortcut_systemsetting ) )
			//			{
			//				menuHelper.onSystemSetting();
			//			}
			//			else if( label.equals( shortcut_beautify ) )
			//			{
			//				menuHelper.onBeautify();
			//			}
			else if( label.equals( switch_wifi ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatWifiSwitch" );
				switchHelper.toggleWifi();
			}
			else if( label.equals( switch_bluetooth ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatBluetoothSwitch" );
				switchHelper.toggleBlueTooth();
			}
			else if( label.equals( switch_net ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatNetSwitch" );
				switchHelper.toggleNetData();
			}
			else if( label.equals( switch_gps ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatGpsSwitch" );
				switchHelper.entryGPSSettings();
			}
			else if( label.equals( switch_bright ) )
			{
				//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HostSeatBrightSwitch" );
				switchHelper.adjustBrightness();
			}
			return true;
		}
		
		@Override
		public boolean onLongClick(
				float x ,
				float y )
		{
			if( label.equals( switch_wifi ) )
			{
				switchHelper.entryWirelessSettings();
			}
			else if( label.equals( switch_bluetooth ) )
			{
				switchHelper.entryBlueToothSettings();
			}
			else if( label.equals( switch_net ) )
			{
				switchHelper.entryAPNSettings();
			}
			else if( label.equals( switch_gps ) )
			{
				switchHelper.entryGPSSettings();
			}
			else if( label.equals( switch_bright ) )
			{
				switchHelper.entryBrightSettings();
			}
			else
				return false;
			return true;
		}
		
		@Override
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			//			    if( label.equals( shortcut_desksetting )||  label.equals( shortcut_edit )||label.equals( shortcut_add ) ||label.equals( shortcut_systemsetting )||label.equals( shortcut_beautify ) )
			//				{
			//				}
			//				
			//				else
			this.setBackgroud( hightlight );
			return super.onTouchDown( x , y , pointer );
		}
		
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			this.setBackgroud( null );
			return super.onTouchUp( x , y , pointer );
		}
		
		@Override
		public boolean fling(
				float velocityX ,
				float velocityY )
		{
			this.setBackgroud( null );
			return super.fling( velocityX , velocityY );
		}
		
		@Override
		public boolean scroll(
				float x ,
				float y ,
				float deltaX ,
				float deltaY )
		{
			this.setBackgroud( null );
			return super.scroll( x , y , deltaX , deltaY );
		}
		
		public TextureRegion getBitmapRegion(
				String path ,
				int size )
		{
			TextureRegion region = null;
			int iconSize = size;
			//			if( title != null && !title.equals( "" ) ){
			//				iconSize=size-(int)titleHeiht;
			//			}
			try
			{
				InputStream inputStream = ThemeManager.getInstance().getInputStream( path );
				if( inputStream != null )
				{
					Bitmap b = ThemeManager.getInstance().getBitmap( inputStream );
					b = Tools.resizeBitmap( b , iconSize , iconSize );
					region = new TextureRegion( new BitmapTexture( b ) );
					inputStream.close();
				}
				//				if( title != null && !title.equals( "" ) )
				//					b = drawTitleBitmap( b , title , size , size );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			return region;
		}
		
		public Bitmap getRegionWithTitle(
				Bitmap b ,
				String title ,
				int targetWidth ,
				int targetHeight ,
				boolean recycle )
		{
			Bitmap bmp = Bitmap.createBitmap( targetWidth , targetHeight , Config.ARGB_8888 );
			canvas.setBitmap( bmp );
			paint.reset();
			paint.setColor( Color.WHITE );
			paint.setAntiAlias( true );
			paint.setTextSize( R3D.icon_title_font );
			paint.getFontMetrics( fontMetrics );
			int bmpHeight = b.getHeight();
			float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
			float space_height;
			float paddingTop;
			String[] splitTitle;
			float paddingLeft = 0;
			space_height = bmpHeight / 10;
			paddingTop = ( targetHeight - bmpHeight - space_height - singleLineHeight ) / 2;
			if( title != null )
			{
				splitTitle = new String[1];
				splitTitle[0] = title;
			}
			else
			{
				splitTitle = null;
			}
			if( paddingTop < 0 )
				paddingTop = 0;
			if( b != null && !b.isRecycled() )
			{
				if( b.getWidth() != Utilities.sIconTextureWidth )
				{
					paddingLeft = paddingLeft - ( b.getWidth() - targetWidth ) / 2;
				}
				if( paddingTop < 0 )
				{
					paddingTop = 0;
				}
				if( paddingLeft < 0 )
				{
					paddingLeft = 0;
				}
				canvas.drawBitmap( b , paddingLeft , paddingTop , null );
				if( recycle )
					b.recycle();
			}
			//在图片下面画文字
			if( splitTitle != null && splitTitle.length > 0 )
			{
				for( int i = 0 ; i < splitTitle.length ; i++ )
				{
					if( splitTitle[i].equals( "" ) )
						break;
					paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );
					float textWidth = paint.measureText( splitTitle[i] );
					float paddintText = ( targetWidth - textWidth ) / 2;
					if( textWidth > targetWidth )
					{
						paddintText = 0;
					}
					float titleY = bmpHeight - fontMetrics.ascent + i * singleLineHeight;
					paint.setColor( Color.WHITE );
					{
						canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
					}
				}
			}
			return bmp;
		}
	}
	
	OnPlayStateChanged playStateListener = new OnPlayStateChanged() {
		
		@Override
		public boolean onPlayStateChanged(
				boolean isPlaying )
		{
			Log.v( TAG , "IS PLAYING : " + isPlaying );
			if( viewCache != null && viewCache.get( PAUSE ) != null && playRegion != null && pauseRegion != null )
			{
				if( !isPlaying )
				{
					viewCache.get( PAUSE ).region = playRegion;
				}
				else
				{
					viewCache.get( PAUSE ).region = pauseRegion;
				}
			}
			Gdx.graphics.requestRendering();
			return false;
		}
		
		@Override
		public boolean onSoundModeChanged(
				boolean mode )
		{
			if( viewCache != null && viewCache.get( SOUND ) != null && soundOffRegion != null && soundOnRegion != null )
			{
				if( mode )
				{
					viewCache.get( SOUND ).region = soundOffRegion;
				}
				else
				{
					viewCache.get( SOUND ).region = soundOnRegion;
				}
			}
			Gdx.graphics.requestRendering();
			return false;
		}
	};
	SwitchHelperCallBack mSwitchHelperCB = new SwitchHelperCallBack() {
		
		@Override
		public void onWifiStateChanged(
				boolean state )
		{
			if( viewCache != null && viewCache.get( WIFI ) != null && wifiOpenedRegion != null && wifiClosedRegion != null )
			{
				if( state )
				{
					viewCache.get( WIFI ).region = wifiOpenedRegion;
				}
				else
				{
					viewCache.get( WIFI ).region = wifiClosedRegion;
				}
			}
			Gdx.graphics.requestRendering();
		}
		
		@Override
		public void onGpsStateChanged(
				boolean state )
		{
			Log.v( TAG , "gps state " + state );
			if( viewCache != null && viewCache.get( GPS ) != null && gpsOpenedRegion != null && gpsClosedRegion != null )
			{
				if( state )
				{
					viewCache.get( GPS ).region = gpsOpenedRegion;
				}
				else
				{
					viewCache.get( GPS ).region = gpsClosedRegion;
				}
			}
			Gdx.graphics.requestRendering();
		}
		
		@Override
		public void onBluetoothStateChanged(
				int state )
		{
			if( viewCache != null && viewCache.get( BLUETOOTH ) != null && bluetoothOpenedRegion != null && bluetoothClosedRegion != null )
			{
				if( state == SwitchHelper.BLUETOOTH_START )
				{
					viewCache.get( BLUETOOTH ).region = bluetoothOpenedRegion;
				}
				else
				{
					viewCache.get( BLUETOOTH ).region = bluetoothClosedRegion;
				}
			}
			Gdx.graphics.requestRendering();
		}
		
		@Override
		public void onBrightnessChanged(
				int state )
		{
			if( viewCache != null && viewCache.get( BRIGHTNESS ) != null && brightnessLowRegion != null && brightnessMiddleRegion != null && brightnessHighRegion != null && brightnessAutoRegion != null )
			{
				if( SwitchHelper.BRIGHTNESS_LOW == state )
				{
					viewCache.get( BRIGHTNESS ).region = brightnessLowRegion;
				}
				else if( SwitchHelper.BRIGHTNESS_MIDDLE == state )
				{
					viewCache.get( BRIGHTNESS ).region = brightnessMiddleRegion;
				}
				if( SwitchHelper.BRIGHTNESS_HIGH == state )
				{
					viewCache.get( BRIGHTNESS ).region = brightnessHighRegion;
				}
				else if( state == SwitchHelper.BRIGHTNESS_AUTO )
				{
					viewCache.get( BRIGHTNESS ).region = brightnessAutoRegion;
				}
			}
			Gdx.graphics.requestRendering();
		}
		
		@Override
		public void onGPRSNetChanged(
				boolean state )
		{
			if( viewCache != null && viewCache.get( GPRS ) != null && gprsOpenedRegion != null && gprsClosedRegion != null )
			{
				if( state )
				{
					viewCache.get( GPRS ).region = gprsOpenedRegion;
				}
				else
				{
					viewCache.get( GPRS ).region = gprsClosedRegion;
				}
			}
			Gdx.graphics.requestRendering();
		}
	};
	
	public void setPageIndex(
			int index )
	{
		//setCurrentPage( index );
	}
	
	public int measureSize(
			int size )
	{
		return (int)( size * Utils3D.getScreenWidth() / 720.0f );
	}
	
	public Bitmap drawTitleBitmap(
			String title )
	{
		int paddingLeft = 0;
		int paddingTop = 3;
		float textWidth = paint.measureText( title );
		paint.reset();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		paint.setTextSize( R3D.icon_title_font - measureSize( 3 ) );
		paint.getFontMetrics( fontMetrics );
		float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );
		Bitmap bmp = Bitmap.createBitmap( (int)( textWidth ) + measureSize( 6 ) , (int)singleLineHeight + measureSize( 6 ) , Config.ARGB_8888 );
		canvas.setBitmap( bmp );
		float paddintText = measureSize( 3 );
		float titleY = singleLineHeight;//measureSize( 3 );
		canvas.drawText( title , paddintText , titleY , paint );
		Log.v( TAG , "singleLineHeight " + singleLineHeight + " ,FONT: " + R3D.icon_title_font + " textWidth" + textWidth );
		return bmp;
	}
	
	public TextureRegion getTextureRegion(
			String title )
	{
		Bitmap bitmap = drawTitleBitmap( title );
		TextureRegion t = new TextureRegion( new BitmapTexture( bitmap ) );
		if( !bitmap.isRecycled() )
		{
			bitmap.recycle();
			bitmap = null;
		}
		return t;
	}
	
	public float getTitleHeight()
	{
		paint.reset();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		paint.setTextSize( R3D.icon_title_font - measureSize( 3 ) );
		paint.getFontMetrics( fontMetrics );
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );
		float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		singleLineHeight += titleGap;
		return singleLineHeight;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( DefaultLayout.WorkspaceActionGuide )
		{
			if( ActionHolder.getInstance() != null && ActionData.getInstance().checkValidity() > 0 )
				return true;
		}
		return super.onLongClick( x , y );
	}
}
