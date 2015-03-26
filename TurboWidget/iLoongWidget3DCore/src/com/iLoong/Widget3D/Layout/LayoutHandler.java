package com.iLoong.Widget3D.Layout;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.iLoong.Widget3D.BaseView.PluginViewGroup3D;
import com.iLoong.Widget3D.BaseView.PluginViewObject3D;
import com.iLoong.Widget3D.Theme.ThemeHelper;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class LayoutHandler extends DefaultHandler
{
	
	MainAppContext appContext;
	
	public LayoutHandler(
			MainAppContext appContext )
	{
		this.appContext = appContext;
	}
	
	private PluginViewGroup3D viewRoot = null;
	
	public PluginViewGroup3D getViewRoot()
	{
		return viewRoot;
	}
	
	public void startDocument() throws SAXException
	{
		// Utils3D.showPidMemoryInfo("startDocument");
	}
	
	public void endDocument() throws SAXException
	{
		// Utils3D.showPidMemoryInfo("endDocument");
	}
	
	public void startElement(
			String namespaceURI ,
			String localName ,
			String qName ,
			Attributes atts ) throws SAXException
	{
		if( localName.equals( "PluginRoot" ) )
		{
			String name = atts.getValue( "name" );
			viewRoot = new PluginViewGroup3D( appContext , name == null ? "viewRoot" : name );
			String xStr = atts.getValue( "x" );
			if( xStr != null )
			{
				viewRoot.x = Float.parseFloat( xStr );
			}
			String yStr = atts.getValue( "y" );
			if( yStr != null )
			{
				viewRoot.y = Float.parseFloat( yStr );
			}
			String widthStr = atts.getValue( "width" );
			if( widthStr != null )
			{
				viewRoot.width = Float.parseFloat( widthStr );
			}
			String heightStr = atts.getValue( "height" );
			if( heightStr != null )
			{
				viewRoot.height = Float.parseFloat( heightStr );
			}
			viewRoot.setOrigin( viewRoot.width / 2 , viewRoot.height / 2 );
			viewRoot.regionName = atts.getValue( "regionName" );
			viewRoot.backgroundName = atts.getValue( "backgroupName" );
			viewRoot.refRegion = atts.getValue( "refRegionName" );
			String scale = atts.getValue( "scale" );
			if( scale != null )
			{
				viewRoot.scale = Float.parseFloat( scale );
			}
		}
		else if( localName.equals( "ShareRegion" ) )
		{
			String name = atts.getValue( "name" );
			ShareRegion shareRegion = new ShareRegion();
			shareRegion.name = name;
			if( !name.isEmpty() )
			{
				viewRoot.addShareRegion( name , shareRegion );
			}
		}
		else if( localName.equals( "Object3D" ) )
		{
			String name = atts.getValue( "name" );
			PluginViewObject3D object3D = new PluginViewObject3D( name == null ? "Object3D" : name );
			String xStr = atts.getValue( "x" );
			if( xStr != null )
			{
				object3D.x = Float.parseFloat( xStr );
			}
			String yStr = atts.getValue( "y" );
			if( yStr != null )
			{
				object3D.y = Float.parseFloat( yStr );
			}
			String widthStr = atts.getValue( "width" );
			if( widthStr != null )
			{
				object3D.width = Float.parseFloat( widthStr );
			}
			String heightStr = atts.getValue( "height" );
			if( heightStr != null )
			{
				object3D.height = Float.parseFloat( heightStr );
			}
			object3D.mRegionName = atts.getValue( "regionName" );
			object3D.mRefRegionName = atts.getValue( "refRegionName" );
			object3D.mObjName = atts.getValue( "objName" );
			object3D.themeName = ThemeHelper.getThemeName( appContext );
			object3D.setMoveOffset( viewRoot.width / 2 , viewRoot.height / 2 , 0 );
			// object3D.mScale = viewRoot.scale;
			object3D.appContext = appContext;
			viewRoot.addChild( object3D );
		}
	}
	
	@Override
	public void endElement(
			String uri ,
			String localName ,
			String qName ) throws SAXException
	{
		// TODO Auto-generated method stub
		super.endElement( uri , localName , qName );
	}
}
