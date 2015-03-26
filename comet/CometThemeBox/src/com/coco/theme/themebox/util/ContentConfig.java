package com.coco.theme.themebox.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.coco.theme.themebox.MainActivity;
import com.coco.theme.themebox.StaticClass;
import com.coco.theme.themebox.service.ThemesDB;


public class ContentConfig
{
	
	private static final String LOG_TAG = "ContentConfig";
	private DocumentBuilderFactory factory = null;
	private String thumbPath = "";
	private String themePath = "";
	private String[] previewFiles = new String[]{};
	private String packageName = "";
	private String applicationName = "";
	private int versionCode = 0;
	private String versionName = "";
	private long applicationSize = 0;
	private String author = "";
	private String introduction = "";
	private String updateTime = "";
	
	public int getVersionCode()
	{
		return versionCode;
	}
	
	public String getVersionName()
	{
		return versionName;
	}
	
	public String getPackageName()
	{
		return packageName;
	}
	
	public String getApplicationName()
	{
		return applicationName;
	}
	
	public long getApplicationSize()
	{
		return applicationSize;
	}
	
	public String getAuthor()
	{
		return author;
	}
	
	public String getIntroduction()
	{
		return introduction;
	}
	
	public String getUpdateTime()
	{
		return updateTime;
	}
	
	public boolean saveShare(
			Context remoteContext ,
			String filePath )
	{
		InputStream thumbStream = null;
		FileOutputStream fileOut = null;
		byte[] buffer = new byte[10 * 1024];
		try
		{
			fileOut = new FileOutputStream( filePath );
			thumbStream = remoteContext.getAssets().open( themePath + "/preview/" + previewFiles[0] );
			int readSize = thumbStream.read( buffer );
			while( readSize > 0 )
			{
				fileOut.write( buffer , 0 , readSize );
				readSize = thumbStream.read( buffer );
			}
			return true;
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
			return false;
		}
		catch( IOException e )
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if( thumbStream != null )
			{
				try
				{
					thumbStream.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
			if( fileOut != null )
			{
				try
				{
					fileOut.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public Bitmap loadThumbImage(
			Context remoteContext )
	{
		Bitmap result = null;
		if( remoteContext.getPackageName().equals( ThemesDB.LAUNCHER_PACKAGENAME ) )
		{
			if( StaticClass.set_default_theme_thumb != null )
			{
				if( !StaticClass.set_default_theme_thumb.equals( "" ) )
				{
					String str = StaticClass.set_default_theme_thumb + "/default.jpg";
					File file = new File( str );
					if( file.exists() )
					{
						result = BitmapFactory.decodeFile( str );
						return result;
					}
				}
			}
		}
		result = Tools.getScaledBitmapBySpecifiedRes( remoteContext , 720 , MainActivity.getIntance().getWindowManager().getDefaultDisplay().getWidth() , thumbPath );
		//		try
		//		{
		//			result = BitmapFactory.decodeStream( remoteContext.getAssets().open( thumbPath ) );
		//		}
		//		catch( FileNotFoundException e )
		//		{
		//			result = null;
		//		}
		//		catch( IOException e )
		//		{
		//			e.printStackTrace();
		//			result = null;
		//		}
		return result;
	}
	
	public Bitmap loadPreviewImage(
			Context remoteContext ,
			int index )
	{
		if( index < 0 || index >= previewFiles.length )
		{
			return null;
		}
		Bitmap result = null;
		if( remoteContext.getPackageName().equals( ThemesDB.LAUNCHER_PACKAGENAME ) )
		{
			if( StaticClass.set_default_theme_thumb != null )
			{
				if( !StaticClass.set_default_theme_thumb.equals( "" ) )
				{
					String str = themePath + "/preview/" + previewFiles[index];
					File file = new File( str );
					if( file.exists() )
					{
						result = BitmapFactory.decodeFile( str );
						return result;
					}
				}
			}
		}
		result = Tools
				.getScaledBitmapBySpecifiedRes( remoteContext , 720 , MainActivity.getIntance().getWindowManager().getDefaultDisplay().getWidth() , themePath + "/preview/" + previewFiles[index] );
		//		try
		//		{
		//			result = BitmapFactory.decodeStream( remoteContext.getAssets().open( themePath + "/preview/" + previewFiles[index] ) );
		//		}
		//		catch( IOException e )
		//		{
		//			e.printStackTrace();
		//			result = null;
		//		}
		return result;
	}
	
	public int getPreviewArrayLength()
	{
		Log.v( LOG_TAG , "getPreviewArrayLength()=" + previewFiles.length );
		return previewFiles.length;
	}
	
	public void reset()
	{
		thumbPath = "";
		themePath = "";
		previewFiles = new String[]{};
		versionCode = 0;
		versionName = "";
		packageName = "";
		applicationName = "";
		applicationSize = 0;
		author = "";
		introduction = "";
		updateTime = "";
	}
	
	public boolean loadConfig(
			Context remoteContext ,
			String className )
	{
		reset();
		if( factory == null )
		{
			factory = DocumentBuilderFactory.newInstance();
		}
		InputStream configStream = null;
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			configStream = remoteContext.getAssets().open( "preview_config.xml" );
			Document doc = builder.parse( configStream );
			Element rootElement = doc.getDocumentElement();
			Element parentElement = rootElement;
			List<Node> appsList = getChildNodeList( parentElement , "app" );
			for( int i = 0 ; i < appsList.size() ; i++ )
			{
				Node appNode = appsList.get( i );
				if( appNode instanceof Element )
				{
					Element ele = (Element)appNode;
					if( className.equals( ele.getAttribute( "className" ) ) )
					{
						parentElement = ele;
						break;
					}
				}
			}
			//			if (remoteContext.getPackageName().equals(ThemesDB.LAUNCHER_PACKAGENAME)) {
			//				if (StaticClass.set_default_theme_thumb != null && !StaticClass.set_default_theme_thumb.equals("")) {
			//					File file = new File(StaticClass.set_default_theme_thumb+"/preview.xml");  
			//					configStream = new FileInputStream(file);
			//					previewPath = StaticClass.set_default_theme_thumb;
			//				}else {
			//					configStream = remoteContext.getAssets().open("theme/preview/preview.xml");
			//					previewPath = "theme/preview";
			//				}
			//			}else {
			//				configStream = remoteContext.getAssets().open("theme/preview/preview.xml");
			//				previewPath = "theme/preview";
			//			}
			themePath = getAttributeValue( parentElement , "theme" , "path" );
			{
				//				previewPath = "theme/preview";
				List<Node> itemList = getChildNodeList( parentElement , "item" );
				previewFiles = new String[itemList.size()];
				for( int i = 0 ; i < itemList.size() ; i++ )
				{
					Element item = (Element)itemList.get( i );
					previewFiles[i] = item.getAttribute( "image" );
				}
			}
			String strThumbPath = getAttributeValue( parentElement , "thumb" , "path" );
			if( !strThumbPath.equals( "" ) )
			{
				thumbPath = themePath + "/preview/" + strThumbPath;
			}
			else if( strThumbPath.equals( "" ) && previewFiles.length > 0 )
			{
				thumbPath = themePath + "/preview/" + previewFiles[0];
			}
			else
			{
				thumbPath = "";
			}
			Log.d( LOG_TAG , "thumb=" + thumbPath );
			author = getAttributeValue( parentElement , "info" , "author" );
			Log.d( LOG_TAG , "author=" + author );
			updateTime = getAttributeValue( parentElement , "info" , "date" );
			Log.d( LOG_TAG , "updatetime=" + updateTime );
			introduction = getElementValue( parentElement , "introduction" );
			Log.d( LOG_TAG , "introduction=" + introduction );
			packageName = remoteContext.getPackageName();
			PackageInfo pkgInfo = remoteContext.getPackageManager().getPackageInfo( packageName , 0 );
			versionCode = pkgInfo.versionCode;
			versionName = pkgInfo.versionName;
			ApplicationInfo appInfo = pkgInfo.applicationInfo;
			applicationName = remoteContext.getPackageManager().getApplicationLabel( appInfo ).toString();
			applicationSize = new File( appInfo.publicSourceDir ).length();
			Log.d( LOG_TAG , String.format( "pkgName=%s,appName=%s,size=%d,vCode=%d,vName=%s" , packageName , applicationName , applicationSize , versionCode , versionName ) );
			return true;
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		catch( ParserConfigurationException e )
		{
			e.printStackTrace();
		}
		catch( SAXException e )
		{
			e.printStackTrace();
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		finally
		{
			if( configStream != null )
			{
				try
				{
					configStream.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	private String getAttributeValue(
			Element parent ,
			String tagName ,
			String attName )
	{
		Element ele = getChildElementByTag( parent , tagName );
		if( ele == null )
		{
			return "";
		}
		attName = ele.getAttribute( attName );
		return attName;
	}
	
	private String getElementValue(
			Element parent ,
			String tagName )
	{
		Element ele = getChildElementByTag( parent , tagName );
		if( ele == null )
		{
			return "";
		}
		return ele.getTextContent();
	}
	
	private Element getChildElementByTag(
			Element parent ,
			String name )
	{
		List<Node> nodeList = getChildNodeList( parent , name );
		if( nodeList.size() <= 0 )
		{
			return null;
		}
		Node node = nodeList.get( 0 );
		if( node instanceof Element )
		{
			return (Element)node;
		}
		return null;
	}
	
	private List<Node> getChildNodeList(
			Element parent ,
			String name )
	{
		List<Node> ret = new ArrayList<Node>();
		NodeList childList = parent.getChildNodes();
		for( int i = 0 ; i < childList.getLength() ; i++ )
		{
			Node node = childList.item( i );
			if( node.getNodeName().equals( name ) )
			{
				ret.add( node );
			}
		}
		return ret;
	}
}
