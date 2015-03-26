package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.List;

import com.iLoong.launcher.Desktop3D.Log;
import android.view.KeyEvent;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.data.ItemInfo;


public class IconTrans3D extends ViewGroup3D
{
	
	private float min_x;
	private float min_y;
	private float max_x;
	private float max_y;
	private ImageView3D buttonOK;
	private ViewGroup3D viewContainer;
	float transLateX = 0;
	float translateY = 0;
	boolean bPermitTrans = false;
	
	private void findMinAndMax_x_y(
			List<View3D> mCircleSomething )
	{
		int Count = mCircleSomething.size();
		float tempX = 0;
		float tempY = 0;
		float temp_max_x;
		float temp_max_y;
		float offset = R3D.icongroup_button_width / 4;
		//offset=0;
		/*
		 * x,y,width,heigth
		 */
		min_x = mCircleSomething.get( 0 ).x;
		min_y = mCircleSomething.get( 0 ).y;
		max_x = min_x + mCircleSomething.get( 0 ).width;
		max_y = min_y + mCircleSomething.get( 0 ).height;
		for( int i = 1 ; i < Count ; i++ )
		{
			tempX = mCircleSomething.get( i ).x;
			tempY = mCircleSomething.get( i ).y;
			temp_max_x = tempX + mCircleSomething.get( i ).width;
			temp_max_y = tempY + mCircleSomething.get( i ).height;
			if( tempX < min_x )
			{
				min_x = tempX;
			}
			if( temp_max_x > max_x )
			{
				max_x = temp_max_x;
			}
			if( tempY < min_y )
			{
				min_y = tempY;
			}
			if( temp_max_y > max_y )
			{
				max_y = temp_max_y;
			}
		}
		min_x = min_x - offset;
		if( min_x <= 0 )
		{
			min_x = 0;
		}
		min_y = min_y - offset;
		if( min_y <= 0 )
		{
			min_y = 0;
		}
		max_x = max_x + offset;
		if( max_x >= Utils3D.getScreenWidth() )
		{
			max_x = Utils3D.getScreenWidth();
		}
		max_y = max_y + offset;
		if( max_y >= Utils3D.getScreenHeight() )
		{
			max_y = Utils3D.getScreenHeight();
		}
	}
	
	public void DealButtonOKDown()
	{
		final CellLayout3D currentLayout = ( (Workspace3D)this.viewParent ).getCurrentCellLayout();
		int Count = viewContainer.getChildCount();
		ArrayList<View3D> templist = new ArrayList<View3D>();
		View3D tempActor;
		templist.clear();
		releaseFocus();
		for( int i = 0 ; i < Count ; i++ )
		{
			tempActor = viewContainer.getChildAt( i );
			tempActor.x = tempActor.x + viewContainer.x;
			tempActor.y = tempActor.y + viewContainer.y;
			templist.add( tempActor );
		}
		viewContainer.removeAllViews();
		for( int i = 0 ; i < templist.size() ; i++ )
		{
			tempActor = templist.get( i );
			currentLayout.addView( tempActor );
			if( tempActor instanceof IconBase3D )
			{
				ItemInfo info = ( (IconBase3D)tempActor ).getItemInfo();
				info.screen = ( (Workspace3D)this.viewParent ).getCurrentScreen();
				info.x = (int)tempActor.x;
				info.y = (int)tempActor.y;
				Root3D.addOrMoveDB( info );
			}
		}
		this.viewParent.removeView( this );
	}
	
	public IconTrans3D()
	{
		this( null );
	}
	
	public IconTrans3D(
			String name )
	{
		super( name );
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK )
			return true;
		return super.keyDown( keycode );
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			DealButtonOKDown();
			return true;
		}
		return super.keyUp( keycode );
	}
	
	private void translateIconGroup(
			float offsetX ,
			float offsetY )
	{
		min_x += offsetX;
		min_y -= offsetY;
		if( min_y <= 0 )
		{
			min_y = 0;
		}
		if( min_x <= 0 )
		{
			min_x = 0;
		}
		if( min_x >= this.getWidth() - viewContainer.getWidth() )
		{
			min_x = (int)( this.getWidth() - viewContainer.getWidth() );
		}
		if( min_y >= this.getHeight() - viewContainer.getHeight() )
		{
			min_y = (int)( this.getHeight() - viewContainer.getHeight() );
		}
		max_y = (int)( min_y + viewContainer.getHeight() );
		max_x = (int)( min_x + viewContainer.getWidth() );
		buttonOK.setPosition( max_x - buttonOK.getWidth() , max_y - buttonOK.getWidth() );
		viewContainer.setPosition( min_x , min_y );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		//View3D hitView = hit(x, y);
		if( bPermitTrans )
		{
			float transX;
			float transY;
			transX = ( x - transLateX );
			transY = ( translateY - y );
			translateIconGroup( transX , transY );
			transLateX = x;
			translateY = y;
		}
		return true;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		View3D hitView = hit( x , y );
		if( pointer == 1 )
		{
			return true;
		}
		if( hitView.name == buttonOK.name )
		{
			DealButtonOKDown();
		}
		if( bPermitTrans == true )
		{
			bPermitTrans = false;
		}
		return true;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		View3D hitView = hit( x , y );
		if( pointer == 1 )
		{
			return true;
		}
		bPermitTrans = false;
		if( x > min_x && x < max_x && y > min_y && y < max_y )
		{
			transLateX = x;
			translateY = y;
			bPermitTrans = true;
			requestFocus();
			return true;
		}
		else
		{
			if( hitView.name == this.name )
			{
				DealButtonOKDown();
				return true;
			}
		}
		return true;
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		return true;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.iLoong.launcher.UI3DEngine.ViewGroup3D#onTouchDragged(float, float, int)
	 */
	@Override
	public boolean onTouchDragged(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		return true;
	}
	
	public void buildElements(
			List<View3D> circleGroup )
	{
		Color color = new Color( 0.85f , 0.85f , 1f , 1f );
		findMinAndMax_x_y( circleGroup );
		buttonOK = new ImageView3D( "button_ok" , R3D.getTextureRegion( "public-button-return" ) );
		buttonOK.setSize( R3D.icongroup_button_width , R3D.icongroup_button_height );
		buttonOK.x = max_x - buttonOK.width;
		buttonOK.y = max_y - buttonOK.width;
		TextureRegion gridbgTexture = R3D.getTextureRegion( "shell-interactive-grid-bg" );
		NinePatch gridbackground = new NinePatch( gridbgTexture , R3D.icongroup_round_radius , R3D.icongroup_round_radius , R3D.icongroup_round_radius , R3D.icongroup_round_radius );
		viewContainer = new ViewGroup3D( "viewContainer" );
		addView( viewContainer );
		addView( buttonOK );
		viewContainer.setPosition( min_x , min_y );
		viewContainer.setSize( max_x - min_x , max_y - min_y );
		viewContainer.setBackgroud( gridbackground );
		viewContainer.setColor( color );
		int Count = circleGroup.size();
		View3D myActor;
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = circleGroup.get( i );
			viewContainer.calcCoordinate( myActor );
			viewContainer.addView( myActor );
		}
	}
}
