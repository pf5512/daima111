package com.iLoong.launcher.Desktop3D;


import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.HotSeat3D.MediaSeat3D;
import com.iLoong.launcher.Widget3D.IWidget3DPlugin;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class MediaView3D extends CellLayout3D
{
	
	MediaSeat3D mediaSeat;
	
	public MediaView3D(
			String name ,
			String pkgName ,
			String className ,
			MediaSeat3D mediaSeat3D )
	{
		super( name );
		Widget3D widget3D = getWidget3D( pkgName , className );
		addView( widget3D );
		setMediaSeat( mediaSeat3D );
	}
	
	public void setMediaSeat(
			MediaSeat3D mediaSeat )
	{
		this.mediaSeat = mediaSeat;
	}
	
	public MediaSeat3D getMediaSeat()
	{
		return this.mediaSeat;
	}
	
	public void setSeatY(
			float y )
	{
		//xujin
		if( mediaSeat == null )
		{
			return;
		}
		mediaSeat.setPosition( mediaSeat.getX() , y );
	}
	
	public void setSeatTouchable(
			boolean touchable )
	{
		//xujin
		if( mediaSeat == null )
		{
			return;
		}
		mediaSeat.touchable = touchable;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	//	@Override
	//	public boolean onClick(float x, float y) {
	//		// TODO Auto-generated method stub
	//		if (x>=mediaSeat.getX() && y>=mediaSeat.getY() && x<=mediaSeat.getX()+mediaSeat.getWidth() && y<=mediaSeat.getY()+mediaSeat.getHeight()) {
	//			return mediaSeat.onClick(x, y);
	//		}
	//		return super.onClick(x, y);
	//	}
	//	@Override
	//	public void draw(SpriteBatch batch, float parentAlpha) {
	//		// TODO Auto-generated method stub
	//		super.draw(batch, parentAlpha);
	//		mediaSeat.draw(batch, parentAlpha);
	//	}
	public Widget3D getWidget3D(
			String packageName ,
			final String className )
	{
		Widget3D widget3D = null;
		int widgetId = -1;
		WidgetPluginView3D pluginView = this.ReflectPluginView( packageName , className , widgetId );
		if( pluginView != null )
		{
			widget3D = new Widget3D( "Widget3D" , pluginView );
			widget3D.setWidgetId( widgetId );
			widget3D.setPackageName( packageName );
		}
		return widget3D;
	}
	
	public WidgetPluginView3D ReflectPluginView(
			String packageName ,
			final String className ,
			int widgetId )
	{
		Class<?> clazz = null;
		WidgetPluginView3D pluginView = null;
		try
		{
			clazz = iLoongLauncher.getInstance().getClassLoader().loadClass( className );
			IWidget3DPlugin plugin = null;
			if( clazz != null )
			{
				plugin = (IWidget3DPlugin)clazz.newInstance();
				MainAppContext appContext = new MainAppContext( iLoongLauncher.getInstance() , iLoongLauncher.getInstance() , iLoongLauncher.getInstance() , null );
				appContext.paramsMap.put( "launchcellwidth" , R3D.Workspace_cell_each_width );
				appContext.paramsMap.put( "launchcellheight" , R3D.Workspace_cell_each_height );
				Log.i( "jinxu" , "Workspace_cell_each_width = " + R3D.Workspace_cell_each_width );
				Log.i( "jinxu" , "Workspace_cell_each_height = " + R3D.Workspace_cell_each_height );
				//				if (ThemeManager.getInstance().getCurrentThemeDescription().widgettheme == null
				//						|| ThemeManager.getInstance()
				//								.getCurrentThemeDescription().widgettheme
				//								.trim().equals("")) {
				appContext.mThemeName = "iLoong";
				//				} else {
				//					appContext.mThemeName = ThemeManager.getInstance()
				//							.getCurrentThemeDescription().widgettheme;
				//				}
				// if (!appContext.mThemeName.equals("iLoong")) {
				// String widgetName = packageName.substring(
				// packageName.lastIndexOf(".") + 1).toLowerCase();
				// appContext.mThemeName = checkThemeExist(ThemeManager
				// .getInstance().getCurrentThemeContext(),
				// widgetName, appContext.mThemeName);
				// }
				pluginView = (WidgetPluginView3D)plugin.getWidget( appContext , widgetId );
				//				if (!mWidget3DHost.containsWidget3DProvider(packageName)) {
				//
				//					WidgetPluginViewMetaData meta = pluginView
				//							.getPluginViewMetaData();
				//					Widget3DProvider provider = mWidget3DHost
				//							.newWidget3DProvider();
				//					provider.spanX = meta.spanX;
				//					provider.spanY = meta.spanY;
				//					provider.maxInstanceCount = meta.maxInstanceCount;
				//					mWidget3DHost.addWidget3DProvider(packageName, provider);
				//				}
				//				pluginView.setRefreshRender(refreshRender);
			}
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pluginView;
	}
}
