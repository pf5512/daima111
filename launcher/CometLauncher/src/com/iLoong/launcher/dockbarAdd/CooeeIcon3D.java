package com.iLoong.launcher.dockbarAdd;


import java.util.ArrayList;
import java.util.List;
import android.util.Log;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;


public class CooeeIcon3D extends Icon3D
{
	
	public static List<Icon3D> icons = new ArrayList<Icon3D>();
	public static float clickX = 0;
	public static float clickY = 0;
	public static boolean addToFolder = false;
	
	public CooeeIcon3D(
			String name )
	{
		super( name );
		// TODO Auto-generated constructor stub
	}
	
	public CooeeIcon3D(
			String name ,
			TextureRegion t )
	{
		super( name , t );
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		//		com.iLoong.launcher.Desktop3D.Log.v("isSelected", "CooeeIcon3D onClick 33333");
		//		if (hide || uninstall){
		//			com.iLoong.launcher.Desktop3D.Log.v("isSelected", "CooeeIcon3D onClick");
		//			return true;
		//		}
		//		if (isSelected()) {
		//			cancelSelected();
		//			icons.remove(this);
		//			com.iLoong.launcher.Desktop3D.Log.v("isSelected", "CooeeIcon3D onClick 11111");
		//		} else {
		//			selected();
		//			icons.add(this);
		//			com.iLoong.launcher.Desktop3D.Log.v("isSelected", "CooeeIcon3D onClick 22222");
		//		}
		return true;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer == 1 )
		{
			return false;
		}
		clickX = this.x;
		clickY = this.y;
		if( hide || uninstall )
		{
			return true;
		}
		if( isSelected() )
		{
			cancelSelected();
			icons.remove( this );
		}
		else
		{
			if( addToFolder )
			{
				selected();
				icons.add( this );
			}
			else
			{
				if( icons.size() >= Workspace3D.getInstance().getCurCellIconCount() )
				{
					//					SendMsgToAndroid.sendOurToastMsg(R3D.getString(RR.string.exceed_num_add_icon));
					SendMsgToAndroid.sendToastMsg( R3D.getString( RR.string.exceed_num_add_icon ) );
				}
				else
				{
					selected();
					icons.add( this );
				}
			}
		}
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public void onParticleCallback(
			int type )
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( getX() >= clickX && getX() <= clickX + this.width && getY() >= clickY && getY() <= clickY + this.height )
		{
			return true;
		}
		this.onTouchUp( x , y , 0 );
		return true;
	}
}
