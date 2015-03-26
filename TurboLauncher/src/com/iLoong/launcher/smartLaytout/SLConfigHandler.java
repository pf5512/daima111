package com.iLoong.launcher.smartLaytout;


import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;


import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class SLConfigHandler extends DefaultHandler
{
	
	String TAG = "SLConfigHandler";
	private final String configFile = "theme/smartLayout/config.xml";
	LinkedList<SLApplicationMetaData> appList = new LinkedList<SLApplicationMetaData>();
	LinkedList<SLLayout> layout=new LinkedList<SLLayout>();
	LinkedList<ResolveInfo>infoList=new LinkedList<ResolveInfo>();
	
	public SLConfigHandler()
	{
		loadConfig();
	}
	
	public void clear(){
		appList.clear();
		layout.clear();
		infoList.clear();
		System.gc();
	}
	public LinkedList<SLLayout> getLayout(){
		return layout;
	}
	
	public LinkedList<ResolveInfo> getApplicationInfo(){
		infoList.clear();
		final Intent mainIntent = new Intent( Intent.ACTION_MAIN , null );
		mainIntent.addCategory( Intent.CATEGORY_LAUNCHER );
		final PackageManager packageManager = iLoongLauncher.getInstance().getPackageManager();
		List<ResolveInfo> resInfos = packageManager.queryIntentActivities( mainIntent , 0 );
		Iterator<SLApplicationMetaData> ite = appList.iterator();
		while( ite.hasNext() )
		{
			boolean find=false;
			int index=-1;
			SLApplicationMetaData data = ite.next();
			for(int j=0;j<resInfos.size();j++){
				if(data.getPackageName()!=null&&!data.getPackageName().equals( "" )){
					if(resInfos.get( j ).activityInfo.applicationInfo.packageName !=null)
					{
						if(data.getPackageName().equalsIgnoreCase( resInfos.get( j ).activityInfo.applicationInfo.packageName )){
							if(data.getClassName()!=null&&!data.getClassName().equals( "" )){
								if( resInfos.get( j ).activityInfo.applicationInfo.packageName!=null){
									if(data.getClassName().equalsIgnoreCase( resInfos.get( j ).activityInfo.applicationInfo.className )){
										index=j;
										break;
									}
								}
							}
							else{
								index=j;
								break;
							}
						}
					}
					
				}
				
				
				
			}
			if(index!=-1){
				infoList.add( resInfos.get( index ) );
				
			}
			
		}
		
		return infoList;
	}
	
	public LinkedList<SLApplicationMetaData> getAppMetaData(){
		return appList;
	}
	
	public void loadConfig()
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try
		{
			// 加载自身配置文件
			SAXParser parser = null;
			XMLReader xmlreader = null;
			InputSource xmlin = null;
			// 加载config_base文件
			InputStream is = ThemeManager.getInstance().getInputStream( configFile );
			if( is != null )
			{
				xmlin = new InputSource( is );
				parser = factory.newSAXParser();
				xmlreader = parser.getXMLReader();
				xmlreader.setContentHandler( this );
				xmlreader.parse( xmlin );
				is.close();
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void startElement(
			String uri ,
			String localName ,
			String qName ,
			Attributes atts ) throws SAXException
	{
		if(!DefaultLayout.enable_google_version){
			if( localName.equals( "application_cn" ) )
			{
				String label = atts.getValue( "label" );
				String pkgname = atts.getValue( "package" );
				String clsname = atts.getValue( "class" );
				SLApplicationMetaData meta = new SLApplicationMetaData( label , pkgname , clsname );
				appList.add( meta );
			}
		}
		else{
			if( localName.equals( "application_en" ) )
			{
				String label = atts.getValue( "label" );
				String pkgname = atts.getValue( "package" );
				String clsname = atts.getValue( "class" );
				SLApplicationMetaData meta = new SLApplicationMetaData( label , pkgname , clsname );
				appList.add( meta );
			}
		}
		if( localName.equals( "layout" ) )
		{
			int cellX = Integer.parseInt( atts.getValue( "cellX" ) );
			int cellY = Integer.parseInt( atts.getValue( "cellY" ) );
			int screen = Integer.parseInt( atts.getValue( "screen" ) );
			SLLayout sl=new SLLayout(screen,cellX,cellY);
			Log.v( TAG , "cellX " + cellX + " ,cellY " + cellY + " ,screen " + screen );
			layout.add( sl );
		}
	}
}
