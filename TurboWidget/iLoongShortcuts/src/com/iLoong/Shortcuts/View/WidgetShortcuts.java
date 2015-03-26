package com.iLoong.Shortcuts.View;


import java.util.Locale;

import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Shortcuts.LogoHelper;
import com.iLoong.Shortcuts.R;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.UI3DEngine.adapter.Texture;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;


public class WidgetShortcuts extends WidgetPluginView3D
{
	
	public MainAppContext mAppContext;
	// 3D模型统一宽度高度
	public static float MODEL_WIDTH = 320;
	public static float MODEL_HEIGHT = 320;
	public static CooGdx cooGdx;
	public static WidgetShortcuts widgetShortcuts;
	public WidgetShortcutsC widgetShortcutsC[];
	public String Objkuang[] = { "kuang01.obj" , "kuang.obj" , "kuang02.obj" };
	public final static String ImageNmae[] = { "kuang.png" , "wifi.png" , "lanya.png" , "gps.png" , "liuliang.png" , "tex_light01.png" };
	private TextureRegion mBackRegion[] = new TextureRegion[ImageNmae.length];
	ShortcutsView wifi;
	ShortcutsView lanya;
	ShortcutsView gps;
	ShortcutsView liuliang;
	ShortcutsView liangdu;
	
	public WidgetShortcuts(
			String name ,
			MainAppContext appContext ,
			int widgetId )
	{
		super( name );
		mAppContext = appContext;
		widgetShortcuts = this;
		MODEL_WIDTH = mAppContext.mWidgetContext.getResources().getDimension( R.dimen.robot_width );
		MODEL_HEIGHT = mAppContext.mWidgetContext.getResources().getDimension( R.dimen.robot_height );
		cooGdx = new CooGdx( appContext.mGdxApplication );
		width = MODEL_WIDTH;
		height = MODEL_HEIGHT;
		this.setOrigin( this.width / 2 , this.height / 2 );
		for( int i = 0 ; i < ImageNmae.length ; i++ )
		{
			if( mBackRegion[i] == null )
			{
				Texture texture = new Texture( mAppContext.gdx , new AndroidFiles( mAppContext.mWidgetContext.getAssets() ).internal( LogoHelper.getThemeImagePath(
						mAppContext.mThemeName ,
						ImageNmae[i] ) ) );
				texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
				mBackRegion[i] = new TextureRegion( texture );
			}
		}
		widgetShortcutsC = new WidgetShortcutsC[3];
		for( int i = 0 ; i < widgetShortcutsC.length ; i++ )
		{
			widgetShortcutsC[i] = new WidgetShortcutsC( name + i , appContext , widgetId );
			PluginViewObject3D kuang1 = new PluginViewObject3D( appContext , "kuang" + i , mBackRegion[0] , Objkuang[i] );
			kuang1.setMoveOffset( WidgetShortcuts.MODEL_WIDTH / 2 , WidgetShortcuts.MODEL_HEIGHT / 2 , 0 );
			kuang1.build();
			widgetShortcutsC[i].addView( kuang1 );
		}
		wifi = new ShortcutsView( "wifi" , appContext , mBackRegion[1] , "wifi.obj" );
		wifi.setOrigin( wifi.originX , WidgetShortcuts.MODEL_HEIGHT * 0.515f );
		widgetShortcutsC[0].add( wifi );
		lanya = new ShortcutsView( "lanya" , appContext , mBackRegion[2] , "lanya.obj" );
		lanya.setOrigin( lanya.originX , WidgetShortcuts.MODEL_HEIGHT * 0.52f );
		widgetShortcutsC[0].add( lanya );
		gps = new ShortcutsView( "gps" , appContext , mBackRegion[3] , "gps.obj" );
		gps.setOrigin( gps.originX , WidgetShortcuts.MODEL_HEIGHT * 0.51f );
		widgetShortcutsC[1].add( gps );
		liuliang = new ShortcutsView( "liuliang" , appContext , mBackRegion[4] , "liuliang1.obj" );
		liuliang.setOrigin( liuliang.originX , WidgetShortcuts.MODEL_HEIGHT * 0.515f );
		widgetShortcutsC[2].add( liuliang );
		liangdu = new LiangduView( "liangdu" , appContext , mBackRegion[5] , "liangdu1.obj" );
		liangdu.setOrigin( liangdu.originX , WidgetShortcuts.MODEL_HEIGHT * 0.515f );
		widgetShortcutsC[2].add( liangdu );
		widgetShortcutsC[0].setOrigin( WidgetShortcuts.MODEL_WIDTH * 0.444f , WidgetShortcuts.MODEL_HEIGHT / 2 );
		widgetShortcutsC[0].setOriginZ( -WidgetShortcuts.MODEL_HEIGHT * 0.01f );
		widgetShortcutsC[0].setRotationVector( 0 , 1 , 0 );
		widgetShortcutsC[0].setRotationY( -20 );
		widgetShortcutsC[2].setOrigin( WidgetShortcuts.MODEL_WIDTH * 0.565f , WidgetShortcuts.MODEL_HEIGHT / 2 );
		widgetShortcutsC[2].setOriginZ( -WidgetShortcuts.MODEL_HEIGHT * 0.01f );
		widgetShortcutsC[2].setRotationVector( 0 , 1 , 0 );
		widgetShortcutsC[2].setRotationY( 20 );
		for( int i = 0 ; i < widgetShortcutsC.length ; i++ )
		{
			addView( widgetShortcutsC[i] );
		}
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		Log.d( "song" , "onClick x=" + x + ",y=" + y + "  x*5/(MODEL_WIDTH) =" + (int)( x * 5 / ( MODEL_WIDTH ) ) );
		Log.d( "song" , "onClick this.x=" + this.x + ",this.y=" + this.y );
		Log.d( "song" , "onClick w=" + this.width + ",h=" + height );
		int i = (int)( x * 5 / ( width ) );
		switch( i )
		{
			case 0:
				if( wifi != null )
					wifi.Rotation();
				break;
			case 1:
				if( lanya != null )
					lanya.Rotation();
				break;
			case 2:
				if( gps != null )
					gps.Rotation();
				break;
			case 3:
				if( liuliang != null )
					liuliang.Rotation();
				break;
			case 4:
				if( liangdu != null )
					liangdu.Rotation();
				break;
		}
		//		if(i+1<objnameS.length && Obj[i+1]!=null)
		//		Obj[i+1].Rotation();
		return super.onClick( x , y );
	}
	
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		cooGdx.gl.glEnable( GL10.GL_CULL_FACE );
		cooGdx.gl.glCullFace( GL10.GL_BACK );
		super.draw( batch , parentAlpha );
		cooGdx.gl.glDisable( GL10.GL_CULL_FACE );
	}
	
	@Override
	public void onDelete()
	{
	}
	
	@Override
	public WidgetPluginViewMetaData getPluginViewMetaData()
	{
		// TODO Auto-generated method stub
		WidgetPluginViewMetaData metaData = new WidgetPluginViewMetaData();
		metaData.spanX = Integer.valueOf( mAppContext.mWidgetContext.getResources().getInteger( R.integer.spanX ) );
		metaData.spanY = 1; //Integer.valueOf(mAppContext.mWidgetContext.getResources().getInteger(
		//R.integer.spanY));
		metaData.maxInstanceCount = mAppContext.mWidgetContext.getResources().getInteger( R.integer.max_instance );
		String lan = Locale.getDefault().getLanguage();
		if( lan.equals( "zh" ) )
		{
			metaData.maxInstanceAlert = "快捷开关已存在，不可以重复拖出"; //mAppContext.mWidgetContext.getResources().getString(
			//R.string.max_instance_alert);
		}
		else
		{
			metaData.maxInstanceAlert = "Already exists, can not add another one";
		}
		return metaData;
	}
}
