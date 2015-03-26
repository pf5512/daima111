package com.iLoong.launcher.SetupMenu.Actions;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.cooee.android.launcher.framework.LauncherModel;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.DLManager;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.core.CustomerHttpClient;
import com.iLoong.launcher.core.DeferredHandler;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class UpdateManager implements DLManager.DownLoadListener
{
	
	private static final String APKNAME = "CoCoLauncher.apk";
	private static final String DownLoadTitle = "CoCoLauncher";
	private static final String DownLoadPath = iLoongApplication.getDownloadPath();
	private static final String TAG_ILOONG = "iloong";
	private static final String TAG_DOWNLOAD = "download";
	private static final String TAG_VERSIONCODE = "versioncode";
	private static final String TAG_VERSIONNAME = "versionname";
	private static final String TAG_URL = "url";
	private static final String TAG_MESSAGE = "message";
	private DeferredHandler mHandler = iLoongApplication.getInstance().getModel().getDeferredHandler();
	private static final String APK_MIMETYPE = "application/vnd.android.package-archive";
	private boolean mdownload = false;
	private int mVersionCode = 0;
	private String mVersionName;
	private String murl;
	private String mmessage;
	private static UpdateManager mInstance;
	DownloadManager manager;
	
	public UpdateManager()
	{
	}
	
	public synchronized static UpdateManager getInstance()
	{
		if( mInstance == null )
			mInstance = new UpdateManager();
		return mInstance;
	}
	
	class CheckClient implements Runnable
	{
		
		List<NameValuePair> mParams;
		
		public CheckClient(
				List<NameValuePair> formparams )
		{
			mParams = formparams;
		}
		
		@Override
		public void run()
		{
			final String xml = CustomerHttpClient.post( DLManager.httpUrl , mParams );
			mHandler.post( new Runnable() {
				
				public void run()
				{
					UpdateManager.this.CheckUpdate( xml );
				}
			} );
		}
	}
	
	public void checkClientVersion()
	{
		NameValuePair param1 = new BasicNameValuePair( "type" , "0" );
		NameValuePair param2 = new BasicNameValuePair( "versioncode" , iLoongApplication.getInstance().getVersionCode() );
		NameValuePair param3 = new BasicNameValuePair( "packname" , iLoongApplication.getInstance().getPackageName() );
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add( param1 );
		formparams.add( param2 );
		formparams.add( param3 );
		LauncherModel.getWorkerThread().post( new CheckClient( formparams ) );
	}
	
	private void CheckUpdate(
			String xml )
	{
		if( xml == null )
		{
			noupdatedialog( R3D.getString( RR.string.warning_msg ) , R3D.getString( RR.string.dlmng_check_network ) );
			return;
		}
		ParseXml( xml );
		if( !mdownload )
		{
			noupdatedialog( R3D.getString( RR.string.version_msg_title ) , R3D.getString( RR.string.version_msg ) );
		}
		else
		{
			updatedialog();
		}
	}
	
	private void noupdatedialog(
			String title ,
			String msg )
	{
		AlertDialog.Builder builder = new Builder( iLoongLauncher.getInstance() );
		builder.setMessage( msg );
		builder.setTitle( title );
		builder.setPositiveButton( R3D.getString( RR.string.circle_ok_action ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
			}
		} );
		builder.setNegativeButton( R3D.getString( RR.string.circle_cancel_action ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
			}
		} );
		builder.create().show();
	}
	
	private void updatedialog()
	{
		AlertDialog.Builder builder = new Builder( iLoongLauncher.getInstance() );
		String msg = R3D.getString( RR.string.update_msg_title ) + iLoongApplication.getInstance().getVersionName() + "\n";
		msg += R3D.getString( RR.string.update_needed_version ) + mVersionName + "\n";
		msg += R3D.getString( RR.string.update_need_now );
		msg += mmessage;
		builder.setMessage( msg );
		builder.setTitle( R3D.getString( RR.string.version_msg_title ) );
		builder.setPositiveButton( R3D.getString( RR.string.circle_ok_action ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				DownLoadApk();
				dialog.dismiss();
			}
		} );
		builder.setNegativeButton( R3D.getString( RR.string.circle_cancel_action ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
			}
		} );
		builder.create().show();
	}
	
	public void ParseXml(
			String xml )
	{
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try
		{
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			UpdateXMLHandler handler = new UpdateXMLHandler();
			xmlreader.setContentHandler( handler );
			InputStream is = new ByteArrayInputStream( xml.getBytes() );
			InputSource xmlin = new InputSource( is );
			xmlreader.parse( xmlin );
			handler = null;
			xmlin = null;
			is.close();
			is = null;
		}
		catch( ParserConfigurationException e )
		{
			e.printStackTrace();
		}
		catch( SAXException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	class UpdateXMLHandler extends DefaultHandler
	{
		
		boolean btagiloong = false;
		boolean btagdownload = false;
		boolean btagurl = false;
		boolean btagvercode = false;
		boolean btagvername = false;
		boolean btagmessage = false;
		
		public void startDocument() throws SAXException
		{
		}
		
		public void endDocument() throws SAXException
		{
		}
		
		public void startElement(
				String namespaceURI ,
				String localName ,
				String qName ,
				Attributes atts ) throws SAXException
		{
			if( localName.equals( TAG_ILOONG ) )
			{
				btagiloong = true;
			}
			else if( localName.equals( TAG_DOWNLOAD ) )
			{
				btagdownload = true;
			}
			else if( localName.equals( TAG_VERSIONCODE ) )
			{
				btagvercode = true;
			}
			else if( localName.equals( TAG_VERSIONNAME ) )
			{
				btagvername = true;
			}
			else if( localName.equals( TAG_URL ) )
			{
				btagurl = true;
			}
			else if( localName.equals( TAG_MESSAGE ) )
			{
				mmessage = new String();
				btagmessage = true;
			}
		}
		
		public void endElement(
				String namespaceURI ,
				String localName ,
				String qName ) throws SAXException
		{
			if( localName.equals( TAG_ILOONG ) )
			{
				btagiloong = false;
			}
			else if( localName.equals( TAG_DOWNLOAD ) )
			{
				btagdownload = false;
			}
			else if( localName.equals( TAG_VERSIONCODE ) )
			{
				btagvercode = false;
			}
			else if( localName.equals( TAG_VERSIONNAME ) )
			{
				btagvername = false;
			}
			else if( localName.equals( TAG_URL ) )
			{
				btagurl = false;
			}
			else if( localName.equals( TAG_MESSAGE ) )
			{
				btagmessage = false;
			}
		}
		
		public void characters(
				char ch[] ,
				int start ,
				int length )
		{
			if( btagiloong && btagdownload )
			{
				String download = new String( ch , start , length );
				mdownload = download.equals( "0" ) ? false : true;
			}
			else if( btagiloong && btagvercode )
			{
				mVersionCode = Integer.valueOf( new String( ch , start , length ) );
			}
			else if( btagiloong && btagvername )
			{
				mVersionName = new String( ch , start , length );
			}
			else if( btagiloong && btagurl )
			{
				murl = new String( ch , start , length );
			}
			else if( btagiloong && btagmessage )
			{
				String msg = new String( ch , start , length );
				mmessage += msg;
			}
		}
	}
	
	public static int getApkVersionCode(
			String apk )
	{
		PackageInfo info = null;
		PackageManager pm = iLoongApplication.getInstance().getPackageManager();
		info = pm.getPackageArchiveInfo( apk , PackageManager.GET_ACTIVITIES );
		if( info != null )
		{
			return info.versionCode;
		}
		return -1;
	}
	
	public void downloadhome()
	{
		DLManager.getInstance().DownloadLauncher( DownLoadTitle , APKNAME , iLoongApplication.getInstance().getPackageName() , murl , iLoongApplication.getInstance().getVersionCode() , this );
	}
	
	public void DownLoadApk()
	{
		String apkfile = DownLoadPath + "/" + APKNAME;
		int versioncode = getApkVersionCode( apkfile );
		if( mVersionCode == versioncode )
		{
			Intent localIntent = new Intent( Intent.ACTION_VIEW );
			Uri localUri = Uri.parse( "file://" + apkfile );
			localIntent.setDataAndType( localUri , APK_MIMETYPE );
			iLoongLauncher.getInstance().startActivity( localIntent );
			return;
		}
		downloadhome();
	}
	
	@Override
	public void OnError(
			String ApkName ,
			String packname ,
			String errmsg )
	{
		SetupMenu.DialogMessage( R3D.getString( RR.string.system_update ) , errmsg );
	}
	
	@Override
	public void OnDownLoadComplete(
			String ApkName ,
			String packname )
	{
		String paramString = DownLoadPath + "/" + APKNAME;
		Intent localIntent = new Intent( Intent.ACTION_VIEW );
		Uri localUri = Uri.parse( "file://" + paramString );
		String str = MimeTypeMap.getSingleton().getMimeTypeFromExtension( MimeTypeMap.getFileExtensionFromUrl( paramString ) );
		if( ( str == null ) && ( paramString.toLowerCase().endsWith( "apk" ) ) )
			str = APK_MIMETYPE;
		if( str != null )
			localIntent.setDataAndType( localUri , str );
		try
		{
			iLoongLauncher.getInstance().startActivity( localIntent );
		}
		catch( Exception e )
		{
		}
	}
	
	@Override
	public void OnCheckedComplete(
			String ApkName ,
			String packname ,
			int flag ,
			String VersionCode ,
			String VersionName )
	{
		// TODO Auto-generated method stub
	}
}
