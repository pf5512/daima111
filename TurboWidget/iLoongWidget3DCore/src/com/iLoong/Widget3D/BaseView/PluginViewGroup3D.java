package com.iLoong.Widget3D.BaseView;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.iLoong.Widget3D.Layout.ShareRegion;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class PluginViewGroup3D extends ViewGroup3D
{
	
	private HashMap<String , ShareRegion> shareRegionMap = new HashMap<String , ShareRegion>();
	public float scale = 1f;
	public String regionName;
	public String backgroundName;
	public String refRegion = "";
	private MainAppContext appContext;
	
	public PluginViewGroup3D(
			MainAppContext appContext ,
			String name )
	{
		super( name );
		this.appContext = appContext;
		transform = true;
	}
	
	public void addChild(
			View3D child )
	{
		this.addView( child );
	}
	
	public void addShareRegion(
			String key ,
			ShareRegion shareRegion )
	{
		if( !shareRegionMap.containsKey( key ) )
		{
			shareRegionMap.put( key , shareRegion );
		}
	}
	
	public void build()
	{
		Iterator<Entry<String , ShareRegion>> it = shareRegionMap.entrySet().iterator();
		while( it.hasNext() )
		{
			Entry<String , ShareRegion> entry = (Entry<String , ShareRegion>)it.next();
			ShareRegion shareRegion = entry.getValue();
			if( shareRegion.name != null )
			{
				shareRegion.texture = WidgetThemeManager.getInstance().getThemeTexture( shareRegion.name );
			}
		}
		if( refRegion != null && !refRegion.isEmpty() )
		{
			ShareRegion shareRegion = shareRegionMap.get( refRegion );
			if( shareRegion.texture != null )
			{
				this.region.setTexture( shareRegion.texture );
			}
		}
		else
		{
			if( regionName != null && !regionName.isEmpty() )
			{
				BitmapTexture texture = (BitmapTexture)WidgetThemeManager.getInstance().getThemeTexture( regionName );
				if( texture != null )
				{
					this.region.setTexture( texture );
				}
			}
		}
		int count = this.getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			View3D child = this.getChildAt( i );
			if( child instanceof PluginViewObject3D )
			{
				PluginViewObject3D object3D = (PluginViewObject3D)child;
				if( object3D.mRefRegionName != null && !object3D.mRefRegionName.isEmpty() )
				{
					ShareRegion shareRegion = shareRegionMap.get( object3D.mRefRegionName );
					if( shareRegion != null && shareRegion.texture != null )
					{
						object3D.region.setTexture( shareRegion.texture );
					}
				}
				( (PluginViewObject3D)child ).build();
			}
		}
	}
	
	@Override
	public boolean is3dRotation()
	{
		// TODO Auto-generated method stub
		return true;
	}
}
