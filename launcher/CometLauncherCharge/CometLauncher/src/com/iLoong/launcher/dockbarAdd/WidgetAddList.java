package com.iLoong.launcher.dockbarAdd;


import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.DragSource3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.NPageBase;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3DVirtual;
import com.iLoong.launcher.Widget3D.WidgetMyShortcut;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktopEdit.DesktopEdit;


public class WidgetAddList extends NPageBase implements DragSource3D
{
	
	public WidgetAddList(
			String name ,
			int width )
	{
		super( name );
		transform = true;
		setWholePageList();
		setEffectType( 1 );
		indicatorView = new IndicatorView( "npage_indicator" , width );
		//		indicatorView.setPosition(0, Utils3D.getScreenHeight()-116*Utils3D.getScreenWidth()/720);
	}
	
	public void removeAllChild(
			int num )
	{
		for( int i = 0 ; i < num ; i++ )
		{
			this.removeView( view_list.get( i ) );
		}
		view_list.clear();
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		if( sender instanceof Widget3DVirtual )
		{
			Widget3DVirtual widget = (Widget3DVirtual)sender;
			ShortcutInfo info = (ShortcutInfo)widget.getItemInfo();
			switch( event_id )
			{
				case Widget3DVirtual.MSG_WIDGET3D_SHORTCUT_LONGCLICK:
				{
					String className = DefaultLayout.getWidgetItemClassName( info.intent.getComponent().getPackageName() );
					Widget3D dragObj = Widget3DManager.getInstance().getWidget3D( info.intent.getComponent().getPackageName() , className );
					if( dragObj == null )
						return true;
					dragObj.setPosition( DragLayer3D.dragStartX - dragObj.width / 2 , DragLayer3D.dragStartY - dragObj.height / 2 );
					this.setTag( new Vector2( dragObj.x , dragObj.y ) );
					Vector2 v = (Vector2)widget.getTag();
					dragObj.setPosition( v.x - dragObj.width / 2 , v.y - dragObj.height / 2 );
					releaseFocus();
					dragObjects.clear();
					dragObjects.add( dragObj );
					DesktopEdit.getInstance().setWidget( dragObj );
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				}
				case Widget3DVirtual.MSG_WIDGET3D_SHORTCUT_CLICK:
				{
					String className = DefaultLayout.getWidgetItemClassName( info.intent.getComponent().getPackageName() );
					Widget3D dragObj = Widget3DManager.getInstance().getWidget3D( info.intent.getComponent().getPackageName() , className );
					if( dragObj == null )
						return true;
					dragObj.setPosition( DragLayer3D.dragStartX - dragObj.width / 2 , DragLayer3D.dragStartY - dragObj.height / 2 );
					this.setTag( new Vector2( dragObj.x , dragObj.y ) );
					Vector2 v = (Vector2)widget.getTag();
					dragObj.setPosition( v.x - dragObj.width / 2 , v.y - dragObj.height / 2 );
					releaseFocus();
					DesktopEdit.getInstance().setWidget( dragObj );
					return viewParent.onCtrlEvent( this , AddWidget.MSG_WIDGET3D_SHORTCUT_CLICK );
				}
			}
		}
		if( sender instanceof WidgetMyShortcut )
		{
			WidgetMyShortcut widget = (WidgetMyShortcut)sender;
			switch( event_id )
			{
				case WidgetMyShortcut.MSG_WIDGET3D_SHORTCUT_LONGCLICK:
				{
					View3D dragObj = widget.getWidget3D();
					if( dragObj == null )
						return true;
					dragObjects.clear();
					dragObjects.add( dragObj );
					this.setTag( new Vector2( dragObj.x , dragObj.y ) );
					Vector2 v = (Vector2)widget.getTag();
					dragObj.setPosition( v.x - dragObj.width / 2 , v.y - dragObj.height / 2 );
					releaseFocus();
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				}
				case WidgetMyShortcut.MSG_WIDGET3D_SHORTCUT_CLICK:
				{
					View3D dragObj = widget.getWidget3D();
					if( dragObj == null )
						return true;
					dragObj.setPosition( 0 , Utils3D.getScreenHeight() - R3D.Workspace_cell_each_height * 4 );
					this.setTag( new Vector2( dragObj.x , dragObj.y ) );
					Vector2 v = (Vector2)widget.getTag();
					dragObj.setPosition( v.x - dragObj.width / 2 , v.y - dragObj.height / 2 );
					releaseFocus();
					DesktopEdit.getInstance().setWidget( dragObj );
					return viewParent.onCtrlEvent( this , AddWidget.MSG_WIDGET3D_SHORTCUT_CLICK );
				}
			}
		}
		return super.onCtrlEvent( sender , event_id );
	}
	
	//	private ArrayList<View3D> selectedObjects = new ArrayList<View3D>();
	//	protected void clearDragObjs() {
	//		for (View3D view : selectedObjects) {
	//			((Icon3D) view).hideSelectedIcon();
	//		}
	//		selectedObjects.clear();
	//	}
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		//		Gdx.gl.glEnable(GL10.GL_SCISSOR_TEST);
		//		Gdx.gl.glScissor((int)((Utils3D.getScreenWidth()-width)/2+(float)40*Utils3D.getScreenWidth()/720),
		//				0,(int)(width-(float)80*Utils3D.getScreenWidth()/720),
		//				Utils3D.getScreenHeight());
		super.draw( batch , parentAlpha );
		//Gdx.gl.glDisable(GL10.GL_SCISSOR_TEST);
	}
	
	@Override
	public void onDropCompleted(
			View3D target ,
			boolean success )
	{
		// TODO Auto-generated method stub
	}
	
	private ArrayList<View3D> dragObjects = new ArrayList<View3D>();;
	
	@Override
	public ArrayList<View3D> getDragList()
	{
		// TODO Auto-generated method stub
		return dragObjects;
	}
}
