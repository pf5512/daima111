package com.iLoong.launcher.dockbarAdd;


import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DragSource3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.NPageBase;
import com.iLoong.launcher.Desktop3D.NPageBase.IndicatorView;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;


public class AddShortcutList extends NPageBase implements DragSource3D
{
	
	private View3D dragObj;
	
	public AddShortcutList(
			String name )
	{
		super( name );
		transform = true;
		setWholePageList();
		setEffectType( 0 );
		indicatorView = new IndicatorView( "npage_indicator" );
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
	
	//	@Override
	//	public boolean onCtrlEvent(View3D sender, int event_id) {
	//		if (sender instanceof GridView3D) {
	//			Icon3D icon = (Icon3D) sender;
	//			Vector2 vec = null;
	//			if (icon.getTag() instanceof Vector2) {
	//				vec = (Vector2) icon.getTag();
	//				vec.y += icon.height / 2;// 向上偏移
	//			}
	//			switch (event_id) {
	//
	//			case Icon3D.MSG_ICON_LONGCLICK:
	//				if (icon.getParent() instanceof Widget3DShortcut) {
	//					Widget3DShortcut shortcut = (Widget3DShortcut) icon
	//							.getParent();
	//					dragObj = shortcut.getWidget3D();
	//					icon.toAbsoluteCoords(point);
	//					if (dragObj != null) {
	//						dragObj.setPosition(point.x, point.y);
	//					} else {
	//						return true;
	//					}
	//				} else
	//					dragObj = icon;
	//				vec.x += icon.width / 2 - dragObj.width / 2;
	//				this.setTag(vec);
	//				return viewParent
	//						.onCtrlEvent(this, DragSource3D.MSG_START_DRAG);
	//			}
	//		}
	//		return viewParent.onCtrlEvent(sender, event_id);
	//	}
	@Override
	public void onDropCompleted(
			View3D target ,
			boolean success )
	{
	}
	
	@Override
	public ArrayList<View3D> getDragList()
	{
		ArrayList<View3D> l = new ArrayList<View3D>();
		l.add( dragObj );
		return l;
	}
}
