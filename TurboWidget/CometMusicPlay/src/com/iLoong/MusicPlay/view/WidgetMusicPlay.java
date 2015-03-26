package com.iLoong.MusicPlay.view;


import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;

public class WidgetMusicPlay extends WidgetPluginView3D
{
	
	// 3D模型统一宽度高度
	public static float MODEL_WIDTH = 320;
	public static float MODEL_HEIGHT = 320;
	public static MainAppContext mAppContext;
	
	public WidgetMusicPlay(
			String name ,
			MainAppContext context ,
			int widgetId )
	{
		super( name );
		mAppContext = context;
		MODEL_WIDTH = Utils3D.getScreenWidth();
		MODEL_HEIGHT = R3D.Workspace_cell_each_height * 2;
		this.width = MODEL_WIDTH;
		this.height = MODEL_HEIGHT;
		//247f表示720的R3D.Workspace_cell_each_height的大小
		WidgetMusicPlayGroup musicgroup=new WidgetMusicPlayGroup( "musicgroup" , context );
		musicgroup.setSize( MODEL_WIDTH , MODEL_HEIGHT );
		musicgroup.setPosition( 0 , 0 );
		this.addView( musicgroup );
	}
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDelete() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public boolean toLocalCoordinates(
			View3D descendant ,
			Vector2 point )  
	{
		// TODO Auto-generated method stub
		if( this.getParent() != null )
		{
			this.point.x = point.x + this.getParent().getX();
			this.point.y = point.y + this.getParent().getY();
			
		}
		return true;
	}

}
