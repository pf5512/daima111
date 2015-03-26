package com.iLoong.launcher.Functions.Tab;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.os.Environment;

import com.iLoong.launcher.Desktop3D.Log;


public class SerializeHelper
{
	
	public static void writeToXml(
			Context context ,
			String filePath ,
			ArrayList<TabPluginMetaData> metaDataList )
	{
		if( metaDataList == null )
		{
			metaDataList = new ArrayList<TabPluginMetaData>();
		}
		String xml = writeXml( metaDataList );
		writeToFile( context , filePath , xml );
	}
	
	public static boolean writeToFile(
			Context context ,
			String filePath ,
			String str )
	{
		try
		{
			try
			{
				boolean sdCardExist = Environment.getExternalStorageState().equals( android.os.Environment.MEDIA_MOUNTED );
				OutputStream out = null;
				if( sdCardExist )
				{
					try
					{
						File dir = new File( filePath.substring( 0 , filePath.lastIndexOf( "/" ) ) );
						Log.e( "test" , "dir:" + dir );
						if( !dir.exists() )
						{
							Log.e( "test" , "dir 2:" + dir );
							dir.mkdirs();
						}
						File file = new File( filePath );
						Log.e( "test" , "filePath:" + filePath );
						if( !file.exists() )
						{
							file.createNewFile();
						}
						out = new FileOutputStream( filePath );
						OutputStreamWriter outw = new OutputStreamWriter( out );
						outw.write( str );
						outw.close();
						out.close();
					}
					catch( Exception ex )
					{
						ex.printStackTrace();
					}
				}
				try
				{
					File f = new File( context.getFilesDir().getAbsolutePath() + "/cooee/launcher/plugin/tab" );
					if( !f.exists() )
					{
						f.mkdirs();
					}
					f = new File( f.getAbsolutePath() + "/remotePluginList.xml" );
					if( !f.exists() )
					{
						f.createNewFile();
					}
					out = new FileOutputStream( f.getAbsolutePath() );
					OutputStreamWriter outw = new OutputStreamWriter( out );
					outw.write( str );
					outw.close();
					out.close();
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}
				return true;
			}
			catch( Exception e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			// TODO Auto-generated catch block
			return false;
		}
	}
	
	// 写Xml数据
	public static String writeXml(
			ArrayList<TabPluginMetaData> metaDataList )
	{
		StringWriter xmlWriter = new StringWriter();
		try
		{
			// 创建XmlSerializer,有两种方式
			XmlPullParserFactory pullFactory;
			// 方式一:使用工厂类XmlPullParserFactory的方式
			pullFactory = XmlPullParserFactory.newInstance();
			XmlSerializer xmlSerializer = pullFactory.newSerializer();
			// 方式二:使用Android提供的实用工具类android.util.Xml
			// XmlSerializer xmlSerializer = Xml.newSerializer();
			xmlSerializer.setOutput( xmlWriter );
			// 开始具体的写xml
			// <?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
			xmlSerializer.startDocument( "UTF-8" , true );
			// <feed number="25">
			xmlSerializer.startTag( "" , "plugins" );
			// xmlSerializer.attribute("", "number",
			// String.valueOf(earthquakeEntryList.size()));
			for( TabPluginMetaData metaData : metaDataList )
			{
				// <entry>
				xmlSerializer.startTag( "" , "plugin" );
				// <title>pluginid</title>
				xmlSerializer.startTag( "" , "pluginid" );
				xmlSerializer.text( String.valueOf( metaData.pluginId ) );
				xmlSerializer.endTag( "" , "pluginid" );
				// <magnitude>5.3</magnitude>
				xmlSerializer.startTag( "" , "enname" );
				xmlSerializer.text( metaData.enName == null ? "" : metaData.enName );
				xmlSerializer.endTag( "" , "enname" );
				// <updated>2010-09-26 06:44:37</updated>
				xmlSerializer.startTag( "" , "cnname" );
				// SimpleDateFormat sdf = new
				// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				// String dateString = sdf.format(earthquakeEntry.getDate());
				xmlSerializer.text( metaData.cnName == null ? "" : metaData.cnName );
				xmlSerializer.endTag( "" , "cnname" );
				// <link>http://earthquake.usgs.gov/earthquakes/recenteqsww/Quakes/us2010brar.php</link>
				xmlSerializer.startTag( "" , "twname" );
				xmlSerializer.text( metaData.twName == null ? "" : metaData.twName );
				xmlSerializer.endTag( "" , "twname" );
				// <latitude>-14.3009</latitude>
				xmlSerializer.startTag( "" , "classname" );
				xmlSerializer.text( metaData.className == null ? "" : metaData.className );
				xmlSerializer.endTag( "" , "classname" );
				xmlSerializer.startTag( "" , "packagename" );
				xmlSerializer.text( metaData.packageName == null ? "" : metaData.packageName );
				xmlSerializer.endTag( "" , "packagename" );
				// <longitude>167.9491</longitude>
				xmlSerializer.startTag( "" , "url" );
				xmlSerializer.text( metaData.url == null ? "" : metaData.url );
				xmlSerializer.endTag( "" , "url" );
				// <elev>-80100.0</elev>
				xmlSerializer.startTag( "" , "order" );
				xmlSerializer.text( String.valueOf( metaData.order ) );
				xmlSerializer.endTag( "" , "order" );
				xmlSerializer.startTag( "" , "show" );
				if( metaData.show )
				{
					xmlSerializer.text( "1" );
				}
				else
				{
					xmlSerializer.text( "0" );
				}
				// </entry>
				xmlSerializer.endTag( "" , "show" );
				xmlSerializer.endTag( "" , "plugin" );
			}
			// </feed>
			xmlSerializer.endTag( "" , "plugins" );
			xmlSerializer.endDocument();
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xmlWriter.toString();
	}
}
