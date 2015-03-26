package com.iLoong.MusicPlay.view;


import java.io.IOException;

import android.graphics.BitmapFactory;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Bounce;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.MusicPlay.common.Parameter;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class WidgetMusicPlayGroup extends ViewGroup3D
{
	
	// 3D模型统一宽度高度
	public static float MODEL_WIDTH = 320;
	public static float MODEL_HEIGHT = 320;
	public static MainAppContext mAppContext;
	public static CooGdx cooGdx;
	public static WidgetMusicPlayGroup widgetcalender;
	public static float scale = 1f;
	private Timeline animation_rotation = null;
	private float JIAOPIANX;
	private float JIAOPIANY;
	private float JINDUTIAOX;
	private float JINDUTIAOY;
	public static float PREVIEWX;
	public static float PREVIEWY;
	public static float PAUSEX;
	public static float PAUSEY;
	public static float NEXTX;
	public static float NEXTY;
	public static float YINGUIX;
	private float YINGUIY;
	private float YINGUIGAOLIANGX;
	private float YINGUIGAOLIANGY;
	private float mRotateX;
	private float mRotateY;
	private boolean ifworkspacemove = false;
	private boolean ifscroll = false;
	private MusicController musicController;
	
	public static final WidgetMusicPlayGroup getIntance()
	{
		return widgetcalender;
	}
	
	public WidgetMusicPlayGroup(
			String name ,
			MainAppContext context )
	{
		super( name );
		this.transform = true;
		widgetcalender = this;
		new WidgetThemeManager( context );
		mAppContext = context;
		MODEL_WIDTH = Utils3D.getScreenWidth();
		MODEL_HEIGHT = R3D.Workspace_cell_each_height * 2;
		cooGdx = new CooGdx( context.mGdxApplication );
		this.width = MODEL_WIDTH;
		this.height = MODEL_HEIGHT;
		this.transform = true;
		//247f表示720的R3D.Workspace_cell_each_height的大小
		scale = Math.min( Utils3D.getScreenWidth() / 720f , R3D.Workspace_cell_each_height / 247f );
		JIAOPIANX = Parameter.POINT_TO_POINTX + Parameter.JIAOPIAN_TOX * scale;
		JIAOPIANY = Parameter.POINT_TO_POINTY + Parameter.JIAOPIAN_TOY * scale;
		JINDUTIAOX = Parameter.POINT_TO_POINTX + Parameter.JINDUTIAO_TOX * scale;
		JINDUTIAOY = Parameter.POINT_TO_POINTY + Parameter.JINDUTIAO_TOY * scale;
		PREVIEWX = Parameter.POINT_TO_POINTX + Parameter.PREVIEW_TOX * scale;
		PREVIEWY = Parameter.POINT_TO_POINTY + Parameter.PREVIEW_TOY * scale;
		PAUSEX = Parameter.POINT_TO_POINTX + Parameter.PAUSE_TOX * scale;
		PAUSEY = Parameter.POINT_TO_POINTY + Parameter.PAUSE_TOY * scale;
		NEXTX = Parameter.POINT_TO_POINTX + Parameter.NEXT_TOX * scale;
		NEXTY = Parameter.POINT_TO_POINTY + Parameter.NEXT_TOY * scale;
		YINGUIX = Parameter.POINT_TO_POINTX + Parameter.YINGUI_TOX * scale;
		YINGUIY = Parameter.POINT_TO_POINTY + Parameter.YINGUI_TOY * scale;
		YINGUIGAOLIANGX = Parameter.POINT_TO_POINTX + Parameter.YINGUIGAOLIANG_TOX * scale;
		YINGUIGAOLIANGY = Parameter.POINT_TO_POINTY + Parameter.YINGUIGAOLIANG_TOY * scale;
		musicController = new MusicController( mAppContext );
		initMusicBox();
		initJiaoPian();
		initJinDuTiao();
		initButton();
		initYinGui();
	}
	
	private void initMusicBox()
	{
		PluginViewObject3D musicbox = new PluginViewObject3D( mAppContext , "musicbox" , getRegion( "music_box.jpg" ) , "musicBox.obj" );
		musicbox.build();
		musicbox.move( Parameter.POINT_TO_POINTX , Parameter.POINT_TO_POINTY , 0 );
		this.addView( musicbox );
	}
	
	private void initJiaoPian()
	{
		PluginViewObject3D jiaopian = new PluginViewObject3D( mAppContext , "jiaopian" , getRegion( "music_box.jpg" ) , "jiaopian.obj" );
		jiaopian.build();
		jiaopian.move( JIAOPIANX , JIAOPIANY , Parameter.JIAOPIAN_TOZ * scale );
		this.addView( jiaopian );
	}
	
	private void initJinDuTiao()
	{
		PluginViewObject3D jindutiao = new PluginViewObject3D( mAppContext , "jindutiao" , getRegion( "music_box.jpg" ) , "jindutiao.obj" );
		jindutiao.build();
		jindutiao.move( JINDUTIAOX , JINDUTIAOY , Parameter.JINDUTIAO_TOZ * scale );
		this.addView( jindutiao );
	}
	
	private void initButton()
	{
		//		PreviousView previous=new PreviousView( "previous" , mAppContext);
		////		previous.x=PREVIEWX-49*scale;
		////		previous.y=PREVIEWY-20.5f*scale;
		//		previous.setSize( 98*scale , 41*scale );
		//		previous.setMusicController( musicController );
		//		this.addView( previous );
		PauseViewGroup pausegroup = new PauseViewGroup( "pausegroup" );
		pausegroup.setSize( 100 * scale , 45 * scale );
		pausegroup.x = PAUSEX - 100 / 2f * scale;
		pausegroup.y = PAUSEY - 45 / 2f * scale;
		PauseView pauseView = new PauseView( "pause" , mAppContext );
		pauseView.setMusicController( this.musicController );
		pausegroup.addView( pauseView );
		this.addView( pausegroup );
		//		NextView nextView = new NextView("next", mAppContext);
		////		nextView.x=NEXTX-49*scale;
		////		nextView.y=NEXTY-20.5f*scale;
		//		nextView.setSize( 98*scale , 41*scale );
		//		nextView.setMusicController(this.musicController);
		//		this.addView(nextView);
	}
	
	private void initYinGui()
	{
		PluginViewObject3D yingui = new PluginViewObject3D( mAppContext , "yingui" , getRegion( "yingui.jpg" ) , "yingui.obj" );
		yingui.build();
		yingui.move( YINGUIX , YINGUIY , Parameter.YINGUI_TOZ * scale );
		this.addView( yingui );
	}
	
	@Override
	public boolean pointerInParent(
			float x ,
			float y )
	{
		if( this.getParent() != null && this.getParent().getParent()!=null)
		{
			x = x + this.getParent().getParent().getX();
			y = y + this.getParent().getParent().getY();
			
		}
		boolean ret = view3dTouchCheck.isPointIn( this , 0 , 0 , this.getWidth() , this.getHeight() , x , y );
		return ret;
	}
	@Override
	public boolean toLocalCoordinates(
			View3D descendant ,
			Vector2 point )
	{
		return true;
	}
	private TextureRegion getRegion(
			String name )
	{
		try
		{
			BitmapTexture bt = new BitmapTexture( BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( "theme/widget/" + name ) ) , true );
			bt.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			TextureRegion mBackRegion = new TextureRegion( bt );
			return mBackRegion;
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return null;
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
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		mRotateX = 0;
		mRotateY = 0;
		ifworkspacemove = false;
		ifscroll = false;
		this.requestFocus();
		// TODO Auto-generated method stub
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		mRotateX += deltaX;
		mRotateY += deltaY;
		float k = 0;
		if( mRotateY != 0 && mRotateX != 0 )
		{
			k = mRotateY / mRotateX;
		}else if(mRotateX!=0&&mRotateY==0){
			return super.scroll( x , y , deltaX , deltaY );
		}else if(mRotateX==0&&mRotateY!=0){
			startTween( x , y , deltaX , deltaY );
			return true;
		}else{
		}
		if( !ifscroll )
		{
			if( ifworkspacemove )
			{
				return super.scroll( x , y , deltaX , deltaY );
			}
			if( k >= -1.7f && k <= 1.7f )
			{
				this.releaseFocus();
				ifworkspacemove = true;
				ifscroll = false;
				return super.scroll( x , y , deltaX , deltaY );
			}
			else
			{
				ifworkspacemove = false;
				ifscroll = true;
			}
		}
		startTween( x , y , deltaX , deltaY );
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( isPointInMusicPlay( x , y ) )
		{
			this.point.x = x;
			this.point.y = y;
			this.toAbsolute( point );
			Vector2 obj = new Vector2( point.x , point.y );
			this.setTag( obj );
			this.releaseFocus();
			return viewParent.onCtrlEvent( this , 0 );
		}
		return false;
	}
	
	private boolean isPointInMusicPlay(
			float x ,
			float y )
	{
		double r = Math.sqrt( ( x - this.originX ) * ( x - this.originX ) + ( y - this.originY ) * ( y - this.originY ) );
		if( r > this.width / 2 || r > this.height / 2 )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	private void startTween(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( animation_rotation != null )
		{
			animation_rotation.free();
			animation_rotation = null;
		}
		animation_rotation = Timeline.createParallel();
		this.setOrigin( Parameter.POINT_TO_POINTX , Parameter.POINT_TO_POINTY );
		this.setOriginZ( 0 * scale );
		this.setRotationVector( 1 , 0 , 0 );
		if( deltaY > 0 )
		{
			//往下
			animation_rotation.push( Tween.to( this , View3DTweenAccessor.ROTATION , 0.75f ).target( 0 , 0 , 0 ).ease( Bounce.OUT ) );
		}
		else
		{
			//往上
			animation_rotation.push( Tween.to( this , View3DTweenAccessor.ROTATION , 0.75f ).target( -65 , 0 , 0 ).ease( Bounce.OUT ) );
		}
		animation_rotation.start( View3DTweenAccessor.manager ).setCallback( this );
	}
}
