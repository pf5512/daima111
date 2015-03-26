package com.iLoong.launcher.Desktop3D;


import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class GuidXinSHouList extends NPageBase
{
	
	private int mWidth;
	private int mHight;
	private float scaleFactor;
	private Timeline lasttween = null;
	
	public GuidXinSHouList(
			String name )
	{
		super( name );
		mWidth = Utils3D.getScreenWidth();
		mHight = Utils3D.getScreenHeight();
		scaleFactor = Utils3D.getScreenWidth() / 720f;
		float posY = 340 * scaleFactor;
		ViewGroup3D vg1 = new ViewGroup3D( "vg1" );
		ViewGroup3D guid_1 = new ViewGroup3D( "guid_1" );
		View3D guid_1_bg = new View3D( "guid_1_bg" , R3D.findRegion( "guid_1_bg" ) );
		//		guid_1_bg.setSize( guid_1_bg.width * scaleFactor , guid_1_bg.height * scaleFactor );
		guid_1_bg.setPosition( ( mWidth - guid_1_bg.width ) / 2 , posY );
		guid_1.addView( guid_1_bg );
		View3D guid_1_title = new View3D( "guid_1_title" , R3D.findRegion( "guid_1_title" ) );
		//		guid_1_title.setSize( guid_1_title.width * scaleFactor , guid_1_title.height * scaleFactor );
		guid_1_title.setPosition( ( mWidth - guid_1_title.width ) / 2 , posY );
		guid_1.addView( guid_1_title );
		vg1.addView( guid_1 );
		this.addPage( vg1 );
		ViewGroup3D vg2 = new ViewGroup3D( "vg2" );
		ViewGroup3D guid_2 = new ViewGroup3D( "guid_2" );
		View3D guid_2_bg = new View3D( "guid_2_bg" , R3D.findRegion( "guid_2_bg" ) );
		//		guid_2_bg.setSize( guid_2_bg.width * scaleFactor , guid_2_bg.height * scaleFactor );
		guid_2_bg.setPosition( ( mWidth - guid_2_bg.width ) / 2 , posY );
		guid_2.addView( guid_2_bg );
		View3D guid_2_title = new View3D( "guid_2_title" , R3D.findRegion( "guid_2_title" ) );
		//		guid_2_title.setSize( guid_2_title.width * scaleFactor , guid_2_title.height * scaleFactor );
		guid_2_title.setPosition( ( mWidth - guid_2_title.width ) / 2 , posY );
		guid_2.addView( guid_2_title );
		vg2.addView( guid_2 );
		this.addPage( vg2 );
		ViewGroup3D vg3 = new ViewGroup3D( "vg3" );
		ViewGroup3D guid_3 = new ViewGroup3D( "guid_3" );
		View3D guid_3_bg = new View3D( "guid_3_bg" , R3D.findRegion( "guid_3_bg" ) );
		//		guid_3_bg.setSize( guid_3_bg.width * scaleFactor , guid_3_bg.height * scaleFactor );
		guid_3_bg.setPosition( ( mWidth - guid_3_bg.width ) / 2 , posY );
		guid_3.addView( guid_3_bg );
		View3D guid_3_title = new View3D( "guid_3_title" , R3D.findRegion( "guid_3_title" ) );
		//		guid_3_title.setSize( guid_3_title.width * scaleFactor , guid_3_title.height * scaleFactor );
		guid_3_title.setPosition( ( mWidth - guid_3_title.width ) / 2 , posY - 20 * scaleFactor );
		guid_3.addView( guid_3_title );
		vg3.addView( guid_3 );
		this.addPage( vg3 );
		ViewGroup3D vg4 = new ViewGroup3D( "vg4" );
		ViewGroup3D guid_4 = new ViewGroup3D( "guid_4" );
		View3D guid_4_bg = new View3D( "guid_4_bg" , R3D.findRegion( "guid_4_bg" ) );
		//		guid_4_bg.setSize( guid_4_bg.width * scaleFactor , guid_4_bg.height * scaleFactor );
		guid_4_bg.setPosition( ( mWidth - guid_4_bg.width ) / 2 , posY );
		guid_4.addView( guid_4_bg );
		View3D guid_4_title = new View3D( "guid_4_title" , R3D.findRegion( "guid_4_title" ) );
		//		guid_4_title.setSize( guid_4_title.width * scaleFactor , guid_4_title.height * scaleFactor );
		guid_4_title.setPosition( ( mWidth - guid_4_title.width ) / 2 , posY );
		guid_4.addView( guid_4_title );
		vg4.addView( guid_4 );
		this.addPage( vg4 );
		//		ViewGroup3D vg5 = new ViewGroup3D( "vg5" );
		//		ViewGroup3D guid_5 = new ViewGroup3D( "guid_5" );
		//		View3D guid_5_bg = new View3D( "guid_5_bg" , R3D.findRegion( "guid_5_bg" ) );
		//		guid_5_bg.setSize( guid_5_bg.width * scaleFactor , guid_5_bg.height * scaleFactor );
		//		guid_5_bg.setPosition( ( mWidth - guid_5_bg.width ) / 2 , posY +20*scaleFactor);
		//		guid_5.addView( guid_5_bg );
		//		View3D guid_5_title = new View3D( "guid_5_title" , R3D.findRegion( "guid_5_title" ) );
		//		guid_5_title.setSize( guid_5_title.width * scaleFactor , guid_5_title.height * scaleFactor );
		//		guid_5_title.setPosition( ( mWidth - guid_5_title.width ) / 2 , posY-90*scaleFactor );
		//		guid_5.addView( guid_5_title );
		//		vg5.addView( guid_5 );
		//		this.addPage( vg5 );
		ViewGroup3D vg6 = new ViewGroup3D( "vg6" );
		ViewGroup3D guid_6 = new ViewGroup3D( "guid_6" );
		View3D guid_6_bg = new View3D( "guid_6_bg" , R3D.findRegion( "guid_6_bg" ) );
		//		guid_6_bg.setSize( guid_6_bg.width * scaleFactor , guid_6_bg.height * scaleFactor );
		guid_6_bg.setPosition( ( mWidth - guid_6_bg.width ) / 2 , posY + 20 * scaleFactor );
		guid_6.addView( guid_6_bg );
		View3D guid_6_title = new View3D( "guid_6_title" , R3D.findRegion( "guid_6_title" ) );
		//		guid_6_title.setSize( guid_6_title.width * scaleFactor , guid_6_title.height * scaleFactor );
		guid_6_title.setPosition( ( mWidth - guid_6_title.width ) / 2 , posY - 90 * scaleFactor );
		guid_6.addView( guid_6_title );
		vg6.addView( guid_6 );
		this.addPage( vg6 );
		//		ViewGroup3D vg7 = new ViewGroup3D( "vg7" );
		//		ViewGroup3D guid_7 = new ViewGroup3D( "guid_7" );
		//		View3D guid_7_bg = new View3D( "guid_7_bg" , R3D.findRegion( "guid_7_bg" ) );
		//		guid_7_bg.setSize( guid_7_bg.width * scaleFactor , guid_7_bg.height * scaleFactor );
		//		guid_7_bg.setPosition( ( mWidth - guid_7_bg.width ) / 2 , posY+20*scaleFactor);
		//		guid_7.addView( guid_7_bg );
		//		View3D guid_7_title = new View3D( "guid_7_title" , R3D.findRegion( "guid_7_title" ) );
		//		guid_7_title.setSize( guid_7_title.width * scaleFactor , guid_7_title.height * scaleFactor );
		//		guid_7_title.setPosition( ( mWidth - guid_7_title.width ) / 2 , posY -90*scaleFactor);
		//		guid_7.addView( guid_7_title );
		//		vg7.addView( guid_7 );
		//		this.addPage( vg7 );
		//		
		ViewGroup3D vg8 = new ViewGroup3D( "vg8" );
		ViewGroup3D guid_8 = new ViewGroup3D( "guid_8" );
		View3D guid_8_bg = new View3D( "guid_8_bg" , R3D.findRegion( "guid_8_bg" ) );
		//		guid_8_bg.setSize( guid_8_bg.width * scaleFactor , guid_8_bg.height * scaleFactor );
		guid_8_bg.setPosition( ( mWidth - guid_8_bg.width ) / 2 , posY + 20 * scaleFactor );
		guid_8.addView( guid_8_bg );
		View3D guid_8_title = new View3D( "guid_8_title" , R3D.findRegion( "guid_8_title" ) );
		//		guid_8_title.setSize( guid_8_title.width * scaleFactor , guid_8_title.height * scaleFactor );
		guid_8_title.setPosition( ( mWidth - guid_8_title.width ) / 2 , posY - 90 * scaleFactor );
		guid_8.addView( guid_8_title );
		vg8.addView( guid_8 );
		this.addPage( vg8 );
		ViewGroup3D vg9 = new ViewGroup3D( "vg9" );
		ViewGroup3D guid_9 = new ViewGroup3D( "guid_9" );
		View3D guid_9_bg = new View3D( "guid_9_bg" , R3D.findRegion( "guid_9_bg" ) );
		//		guid_9_bg.setSize( guid_9_bg.width * scaleFactor , guid_9_bg.height * scaleFactor );
		guid_9_bg.setPosition( ( mWidth - guid_9_bg.width ) / 2 , posY );
		guid_9.addView( guid_9_bg );
		View3D guid_9_title = new View3D( "guid_9_title" , R3D.findRegion( "guid_9_title" ) );
		//		guid_9_title.setSize( guid_9_title.width * scaleFactor , guid_9_title.height * scaleFactor );
		guid_9_title.setPosition( ( mWidth - guid_9_title.width ) / 2 , posY );
		guid_9.addView( guid_9_title );
		vg9.addView( guid_9 );
		//		this.addPage( vg9 );
		View3D guid_start = new View3D( "guid_start" , R3D.findRegion( "guid_start" ) );
		//		guid_start.setSize( guid_start.width * scaleFactor , guid_start.height * scaleFactor );
		guid_start.setPosition( ( mWidth - guid_start.width ) / 2 , posY - 120 * scaleFactor );
		vg9.addView( guid_start );
		this.addPage( vg9 );
		setWholePageList();
		setEffectType( 1 );
	}
	
	@Override
	protected int preIndex()
	{
		// TODO Auto-generated method stub
		return( page_index == 0 ? 0 : page_index - 1 );
	}
	
	@Override
	protected int nextIndex()
	{
		// TODO Auto-generated method stub
		return( page_index == view_list.size() - 1 ? view_list.size() - 1 : page_index + 1 );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		if( getPageNum() == 1 )
		{
			requestFocus();
		}
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		if( getPageNum() == 1 )
		{
			releaseFocus();
		}
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		// TODO Auto-generated method stub
		if( getPageNum() == 1 )
		{
			return true;
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	private GuidXinShou addEditMode = null;
	
	@Override
	protected void updateEffect()
	{
		// TODO Auto-generated method stub
		if( ( page_index == 0 && xScale > 0 ) )
		{
			return;
		}
		else if( page_index == getPageNum() - 1 && xScale < 0 )
		{
			addEditMode = (GuidXinShou)Root3D.getInstance().findView( "guid" );
			if( addEditMode != null )
			{
				lasttween = Timeline.createSequence();
				lasttween.push( addEditMode.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 1f , -mWidth , 0 , 0 ) );
				lasttween.start( View3DTweenAccessor.manager ).setCallback( this );
			}
			return;
		}
		else
		{
		}
		super.updateEffect();
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
	
	@Override
	public void startAutoEffect()
	{
		// TODO Auto-generated method stub
		if( ( page_index == 0 && xScale > 0 ) || ( page_index == getPageNum() - 1 && xScale < 0 ) )
		{
			xScale = 0;
			yScale = 0;
			return;
		}
		if( xScale + mVelocityX / 1000 > 0.5 )
		{
			float scaleFactor = Utils3D.getScreenWidth() / 720f;
			GuidXinShou.dian_click.setPosition( ( 260 + 30 * ( getCurrentPage() - 1 ) ) * scaleFactor , 82 * scaleFactor );
		}
		else if( xScale + mVelocityX / 1000 < -0.5 )
		{
			float scaleFactor = Utils3D.getScreenWidth() / 720f;
			GuidXinShou.dian_click.setPosition( ( 260 + 30 * ( getCurrentPage() + 1 ) ) * scaleFactor , 82 * scaleFactor );
		}
		super.startAutoEffect();
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		View3D hitView = hit( x , y );
		if( hitView != null && hitView.name.equals( "guid_start" ) )
		{
			addEditMode = (GuidXinShou)Root3D.getInstance().findView( "guid" );
			if( addEditMode != null )
			{
				lasttween = Timeline.createSequence();
				lasttween.push( addEditMode.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 1f , -mWidth , 0 , 0 ) );
				lasttween.start( View3DTweenAccessor.manager ).setCallback( this );
			}
		}
		return super.onClick( x , y );
	}
}
