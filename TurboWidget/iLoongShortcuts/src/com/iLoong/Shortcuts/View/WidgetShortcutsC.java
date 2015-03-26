package com.iLoong.Shortcuts.View;


import java.util.ArrayList;
import java.util.List;

import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;


public class WidgetShortcutsC extends WidgetPluginView3D
{
	
	public MainAppContext mAppContext;
	
	public WidgetShortcutsC(
			String name ,
			MainAppContext appContext ,
			int widgetId )
	{
		super( name );
		mAppContext = appContext;
		//		this.setOrigin(WidgetShortcuts.MODEL_WIDTH / 2, WidgetShortcuts.MODEL_WIDTH / 2);
	}
	
	public List<ShortcutsView> listview = new ArrayList<ShortcutsView>();
	
	public void add(
			ShortcutsView view )
	{
		listview.add( view );
		addView( view );
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		for( ShortcutsView view : listview )
		{
			view.Init();
		}
	}
	
	@Override
	public void onDelete()
	{
		for( ShortcutsView view : listview )
		{
			view.dispose();
		}
	}
}
