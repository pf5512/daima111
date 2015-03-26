package com.iLoong.launcher.Desktop3D;


import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class GuidXinShou extends ViewGroup3D
{
	
	public static final int MSG_HIDE_ADD_VIEW = 0;
	private int mWidth;
	private int mHeight;
	private View3D beijing = null;
	private GuidXinSHouList xinshoulist;
	private View3D dian1 = null;
	private View3D dian2 = null;
	private View3D dian3 = null;
	private View3D dian4 = null;
	//	private View3D dian5 = null;
	private View3D dian6 = null;
	//	private View3D dian7 = null;
	private View3D dian8 = null;
	private View3D dian9 = null;
	public static View3D dian_click = null;
	private float scaleFactor;
	
	public GuidXinShou(
			String name )
	{
		super( name );
		mWidth = Utils3D.getScreenWidth();
		mHeight = Utils3D.getScreenHeight();
		scaleFactor = Utils3D.getScreenWidth() / 720f;
		beijing = new View3D( "beijing" , R3D.findRegion( "beijing" ) );
		beijing.setPosition( 0 , 0 );
		//		beijing.setSize( mWidth , beijing.height * scaleFactor );
		this.addView( beijing );
		xinshoulist = new GuidXinSHouList( "nppage" );
		this.addView( xinshoulist );
		dian1 = new View3D( "dian1" , R3D.findRegion( "dian" ) );
		dian1.setPosition( 260 * scaleFactor , 82 * scaleFactor );
		//		dian1.setSize( dian1.width * scaleFactor , dian1.height * scaleFactor );
		this.addView( dian1 );
		dian2 = new View3D( "dian2" , R3D.findRegion( "dian" ) );
		dian2.setPosition( 290 * scaleFactor , 82 * scaleFactor );
		//		dian2.setSize( dian2.width * scaleFactor , dian2.height * scaleFactor );
		this.addView( dian2 );
		dian3 = new View3D( "dian3" , R3D.findRegion( "dian" ) );
		dian3.setPosition( 320 * scaleFactor , 82 * scaleFactor );
		//		dian3.setSize( dian3.width * scaleFactor , dian3.height * scaleFactor );
		this.addView( dian3 );
		dian4 = new View3D( "dian4" , R3D.findRegion( "dian" ) );
		dian4.setPosition( 350 * scaleFactor , 82 * scaleFactor );
		//		dian4.setSize( dian4.width * scaleFactor , dian4.height * scaleFactor );
		this.addView( dian4 );
		//		dian5 = new View3D( "dian5" , R3D.findRegion( "dian" ) );
		//		dian5.setPosition( 350 * scaleFactor , 82 * scaleFactor );
		//		dian5.setSize( dian5.width * scaleFactor , dian5.height * scaleFactor );
		//		this.addView( dian5 );
		dian6 = new View3D( "dian6" , R3D.findRegion( "dian" ) );
		dian6.setPosition( 380 * scaleFactor , 82 * scaleFactor );
		//		dian6.setSize( dian6.width * scaleFactor , dian6.height * scaleFactor );
		this.addView( dian6 );
		//		dian7 = new View3D( "dian7" , R3D.findRegion( "dian" ) );
		//		dian7.setPosition( 410 * scaleFactor , 82 * scaleFactor );
		//		dian7.setSize( dian7.width * scaleFactor , dian7.height * scaleFactor );
		//		this.addView( dian7 );
		dian8 = new View3D( "dian8" , R3D.findRegion( "dian" ) );
		dian8.setPosition( 410 * scaleFactor , 82 * scaleFactor );
		//		dian8.setSize( dian8.width * scaleFactor , dian8.height * scaleFactor );
		this.addView( dian8 );
		dian9 = new View3D( "dian9" , R3D.findRegion( "dian" ) );
		dian9.setPosition( 440 * scaleFactor , 82 * scaleFactor );
		//		dian9.setSize( dian9.width * scaleFactor , dian9.height * scaleFactor );
		this.addView( dian9 );
		dian_click = new View3D( "dian_click" , R3D.findRegion( "dian_click" ) );
		dian_click.setPosition( 230 * scaleFactor , 82 * scaleFactor );
		//		dian_click.setSize( dian_click.width * scaleFactor , dian_click.height * scaleFactor );
		this.addView( dian_click );
	}
	
	private GuidXinShou addEditMode = null;
	private Timeline lasttween = null;
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			addEditMode = (GuidXinShou)Root3D.getInstance().findView( "guid" );
			if( addEditMode != null )
			{
				lasttween = Timeline.createSequence();
				lasttween.push( addEditMode.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 1f , -mWidth , 0 , 0 ) );
				lasttween.start( View3DTweenAccessor.manager ).setCallback( this );
			}
			return true;
		}
		return super.keyDown( keycode );
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( type == TweenCallback.COMPLETE && source == lasttween )
		{
			if( addEditMode != null )
			{
				addEditMode.releaseFocus();
				addEditMode.disposeRecursive();
				Root3D.getInstance().removeView( addEditMode );
				System.gc();
			}
			return;
		}
		super.onEvent( type , source );
	}
}
