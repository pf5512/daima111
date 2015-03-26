package com.iLoong.launcher.miui;


import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.data.WidgetShortcutInfo;


public class MIUIWidgetHost extends ViewGroup3D
{
	
	public MIUIWidgetList widgetList;
	private final float arrowWidth_const = 12;
	private final float arrowHeight_const = 18;
	private TextureRegion leftArrow;
	private TextureRegion leftShadowArrow;
	private TextureRegion rightArrow;
	private TextureRegion rightShadowArrow;
	public static final int MSG_RELEASE_HIGHLIGHT_VIEW = 1;
	
	public MIUIWidgetHost(
			String name )
	{
		super( name );
		width = Utils3D.getScreenWidth();
		height = DefaultLayout.app_icon_size + R3D.miui_widget_indicator_height + R3D.app_widget3d_gap + MIUIWidgetList.getSingleLineHeight();
		widgetList = new MIUIWidgetList( "applist" );
		leftArrow = R3D.findRegion( "miui-leftArrow" );
		leftShadowArrow = R3D.findRegion( "miui-shadow-leftArrow" );
		rightArrow = R3D.findRegion( "miui-rightArrow" );
		rightShadowArrow = R3D.findRegion( "miui-shadow-rightArrow" );
		this.addView( widgetList );
		this.setPosition( 0 , -height * 1.5f );
		hide();
	}
	
	public void show()
	{
		super.show();
		widgetList.show();
	}
	
	public void hide()
	{
		super.hide();
		widgetList.hide();
	}
	
	public void clearDragObjs()
	{
		widgetList.clearDragObjs();
	}
	
	public void addWidget(
			Widget3DShortcut widget )
	{
		widgetList.addWidget( widget );
	}
	
	public void setWidgets(
			ArrayList<WidgetShortcutInfo> widgets )
	{
		widgetList.setWidgets( widgets );
	}
	
	public void setInited()
	{
		widgetList.setInited();
	}
	
	public void removeWidget(
			String packageName )
	{
		widgetList.removeWidget( packageName );
	}
	
	public int estimateWidgetCellWidth(
			int cellHSpan )
	{
		// TODO Auto-generated method stub
		return widgetList.estimateWidgetCellWidth( cellHSpan );
	}
	
	public int estimateWidgetCellHeight(
			int cellVSpan )
	{
		// TODO Auto-generated method stub
		return widgetList.estimateWidgetCellHeight( cellVSpan );
	}
	
	public void resume()
	{
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( Desktop3DListener.bDesktopDone == false )
		{
			Log.v( "workspace3D" , " WidgetHost onTouchDown initDone false" );
			return true;
		}
		super.onTouchDown( x , y , pointer );
		return true;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		viewParent.onCtrlEvent( this , MSG_RELEASE_HIGHLIGHT_VIEW );
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( Desktop3DListener.bDesktopDone == false )
		{
			Log.v( "workspace3D" , " WidgetHost onTouchDown initDone false" );
			return true;
		}
		viewParent.onCtrlEvent( this , MSG_RELEASE_HIGHLIGHT_VIEW );
		return super.onLongClick( x , y );
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		return super.keyUp( keycode );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		super.draw( batch , parentAlpha );
		float arrowWidth = arrowWidth_const * SetupMenu.mScale;
		float arrowHeight = arrowHeight_const * SetupMenu.mScale;
		if( widgetList.getPageNum() == 1 )
		{
			//			batch.draw(leftShadowArrow,x,y+(height-arrowHeight)/2f,
			//					arrowWidth,arrowHeight);
			//			batch.draw(rightShadowArrow,width-arrowWidth,y+(height-arrowHeight)/2f,
			//					arrowWidth,arrowHeight);
		}
		else
		{
			if( widgetList.getCurrentPage() == 0 )
			{
				batch.draw( leftShadowArrow , x , y + ( height - arrowHeight ) / 2f , arrowWidth , arrowHeight );
				batch.draw( rightArrow , width - arrowWidth , y + ( height - arrowHeight ) / 2f , arrowWidth , arrowHeight );
			}
			else if( widgetList.getCurrentPage() == widgetList.getPageNum() - 1 )
			{
				batch.draw( leftArrow , x , y + ( height - arrowHeight ) / 2f , arrowWidth , arrowHeight );
				batch.draw( rightShadowArrow , width - arrowWidth , y + ( height - arrowHeight ) / 2f , arrowWidth , arrowHeight );
			}
			else
			{
				batch.draw( leftArrow , x , y + ( height - arrowHeight ) / 2f , arrowWidth , arrowHeight );
				batch.draw( rightArrow , width - arrowWidth , y + ( height - arrowHeight ) / 2f , arrowWidth , arrowHeight );
			}
		}
	}
}
