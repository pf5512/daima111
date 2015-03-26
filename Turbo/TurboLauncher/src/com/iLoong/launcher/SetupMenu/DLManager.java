package com.iLoong.launcher.SetupMenu;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

import com.cooee.android.launcher.framework.LauncherModel;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.core.Assets;
import com.iLoong.launcher.core.CustomerHttpClient;
import com.iLoong.launcher.core.DeferredHandler;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.update.UpdateManager;
import com.iLoong.theme.adapter.DownloadLockBoxService;


public class DLManager
{
	
	public static final String httpUrl = iLoongApplication.getInstance().getResources().getString( RR.string.setting_apkdownload );
	public static final String serverAPPUrl = iLoongApplication.getInstance().getResources().getString( RR.string.server_app_url );
	public static final String otherAPPUrl = iLoongApplication.getInstance().getResources().getString( RR.string.other_app_url );
	public static final String DLResponseUrl = iLoongApplication.getInstance().getResources().getString( RR.string.download_response_url );
	private static final String TYPE_HOME = "0";
	private static final String TYPE_WIDGET = "1";
	private static final String DownLoadPath = iLoongApplication.getDownloadPath();
	private Vector<downloadinfo> mDownloads = new Vector<downloadinfo>();
	private NotificationManager mNM = (NotificationManager)SetupMenu.getContext().getSystemService( Context.NOTIFICATION_SERVICE );
	private DeferredHandler mHandler = iLoongApplication.getInstance().getModel().getDeferredHandler();
	private static final String TAG_ILOONG = "iloong";
	private static final String TAG_DOWNLOAD = "download";
	private static final String TAG_VERSIONCODE = "versioncode";
	private static final String TAG_VERSIONNAME = "versionname";
	private static final String TAG_URL = "url";
	private static final String TAG_MESSAGE = "message";
	private static final int DLTYPE_DOWNLOAD = 0;
	private static final int DLTYPE_CHECKED = 1;
	private static int m_downid = 1;
	private DownLoadDB m_dldb = new DownLoadDB( SetupMenu.getContext() );
	public static DLManager mInstance;
	
	public synchronized static DLManager getInstance()
	{
		if( mInstance == null )
			mInstance = new DLManager();
		return mInstance;
	}
	
	public DownLoadDB getDB()
	{
		return m_dldb;
	}
	
	public interface DownLoadListener
	{
		
		public void OnError(
				String ApkName ,
				String packname ,
				String errmsg );
		
		public void OnDownLoadComplete(
				String ApkName ,
				String packname );
		
		public void OnCheckedComplete(
				String ApkName ,
				String packname ,
				int flag ,
				String VersionCode ,
				String VersionName );
	}
	
	public static class downloadinfo
	{
		
		public int downloadtype;
		public String packname;
		public String name;
		public String title;
		public String type;
		public int downloadid;
		public boolean download;
		public String VersionCode;
		public String VersionName;
		public String url;
		public String customerid;
		public DownLoadListener dll;
		public Notification statusnotify;
	}
	
	public downloadinfo GetDownLoadInfo(
			long downid )
	{
		for( int i = 0 ; i < mDownloads.size() ; i++ )
		{
			if( mDownloads.get( i ).downloadid == downid )
			{
				return mDownloads.get( i );
			}
		}
		return null;
	}
	
	public String getAPKid(
			String DLurl )
	{
		String res = null;
		String sub = DLurl;
		int index = -1;
		Log.v( "http" , "getAPKid: " + DLurl );
		index = sub.lastIndexOf( 'f' );
		if( -1 == index )
			return res;
		sub = sub.substring( index );
		index = -1;
		index = sub.indexOf( '_' );
		if( index == -1 )
			return res;
		res = sub.substring( 0 , index );
		Log.v( "http" , "getAPKid: " + res );
		return res;
	}
	
	public void responseToServe(
			downloadinfo dli )
	{
		String apkid = null;
		String url = null;
		ArrayList<NameValuePair> mParams = new ArrayList<NameValuePair>();
		apkid = getAPKid( dli.url );
		JSONObject config = Assets.config;
		JSONObject tmp = null;
		try
		{
			tmp = config.getJSONObject( "config" );
		}
		catch( JSONException e1 )
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String appID = "f36";
		try
		{
			appID = tmp.getString( "app_id" );
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String serialno = "androidsubnewabcabc12312";
		try
		{
			serialno = tmp.getString( "serialno" );
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String package_name = "";
		if( getAppType( dli.packname ) == -1 )
			package_name = dli.packname;
		url = DLResponseUrl + "?" + "apkid=" + apkid + "&" + "UIapkid=" + appID + "&" + "serialno=" + serialno + "&" + "package_name=" + package_name;
		Log.v( "http" , "responseToServe: " + url );
		CustomerHttpClient.get( url , mParams );
	}
	
	public void DownloadComplete(
			downloadinfo dli )
	{
		if( dli.dll != null )
		{
			responseToServe( dli );
			dli.dll.OnDownLoadComplete( dli.name , dli.packname );
		}
		mDownloads.remove( dli );
		m_dldb.Remove( dli.name );
	}
	
	public boolean isAPKExist(
			String apkname )
	{
		if( apkname == null )
			return false;
		try
		{
			PackageInfo info = null;
			PackageManager pm = iLoongApplication.getInstance().getPackageManager();
			info = pm.getPackageArchiveInfo( DownLoadPath + "/" + apkname , PackageManager.GET_SIGNATURES );
			if( info != null )
				return true;
		}
		catch( Exception e )
		{
		}
		return false;
	}
	
	public void CheckWidgetApk(
			String title ,
			String name ,
			String packname ,
			DownLoadListener dll )
	{
		checkserverapk( title , name , packname , TYPE_WIDGET , "0" , dll );
	}
	
	public void DownloadWidget(
			String title ,
			String name ,
			String packname ,
			String customerid ,
			DownLoadListener dll )
	{
		download( title , name , packname , customerid , TYPE_WIDGET , "0" , dll );
	}
	
	public void DownloadLauncher(
			String title ,
			String name ,
			String packname ,
			String url ,
			String versioncode ,
			DownLoadListener dll )
	{
		if( !FindDownLoad( packname ) )
		{
			downloadinfo dli = new downloadinfo();
			dli.downloadtype = DLTYPE_DOWNLOAD;
			dli.downloadid = m_downid++;
			dli.name = name;
			dli.packname = packname;
			dli.title = title;
			dli.type = TYPE_HOME;
			dli.VersionCode = versioncode;
			dli.url = url;
			dli.dll = dll;
			CreateNotify( dli );
			mDownloads.add( dli );
			DownLoadApk( dli );
		}
	}
	
	private void checkserverapk(
			String title ,
			String name ,
			String packname ,
			String type ,
			String versioncode ,
			DownLoadListener dll )
	{
		if( !FindDownLoad( packname ) )
		{
			downloadinfo dli = new downloadinfo();
			dli.downloadtype = DLTYPE_CHECKED;
			dli.downloadid = m_downid++;
			dli.name = name;
			dli.packname = packname;
			dli.title = title;
			dli.type = type;
			dli.VersionCode = versioncode;
			dli.dll = dll;
			CreateNotify( dli );
			mDownloads.add( dli );
			LauncherModel.getWorkerThread().post( new downloadClient( dli ) );
		}
	}
	
	private void onError(
			downloadinfo dli ,
			String errormsg ,
			boolean flag )
	{
		dli.dll.OnError( dli.name , dli.packname , errormsg );
		if( flag )
			mDownloads.remove( dli );
	}
	
	public static void downloadAPK(
			Context context ,
			String srcPackageName ,
			String destPackageName ,
			String fileName )
	{
		Intent intent = new Intent();
		intent.setClass( context , DownloadLockBoxService.class );
		intent.putExtra( "downloadFileName" , fileName );
		intent.putExtra( "downloadUrl" , new UpdateManager( context ).getApkUrl( context , srcPackageName , destPackageName ) );
		Intent intentGetToDownloadAPKName = new Intent( "com.iLoong.launcher.GetToDownloadAPKName" );
		intentGetToDownloadAPKName.putExtra( "ToDownloadAPKName" , destPackageName );
		context.sendBroadcast( intentGetToDownloadAPKName );
		ComponentName res = context.startService( intent );
	}
	
	private void download(
			String title ,
			String name ,
			String packname ,
			String customerid ,
			String type ,
			String versioncode ,
			DownLoadListener dll )
	{
		boolean fromAirpush = DefaultLayout.GetDefaultWidgetFromAirpush( packname , null );
		if( fromAirpush )
		{
			//			String apkUrl = StaticClass.DEFAULT_APK_URL;
			//			String result = String.format("%s?p01=%s&p06=1&%s", apkUrl,
			//					packname, getPhoneParams());
			//			Intent intent1 = new Intent();
			//			intent1.setAction("android.intent.action.VIEW");
			//			Uri content_url = Uri.parse(result);
			//			intent1.setData(content_url);
			//			SendMsgToAndroid.startActivity(intent1);
			File file;
			if( !RR.net_version )
				file = new File( Environment.getExternalStorageDirectory() + "/Coco/download/" , title + ".apk" );
			else
				file = new File( DownloadLockBoxService.DOWNLOAD_PATH , title + ".apk" );
			if( file.exists() )
			{
				Intent intent2 = new Intent();
				intent2.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				intent2.setAction( android.content.Intent.ACTION_VIEW );
				intent2.setDataAndType( Uri.fromFile( file ) , "application/vnd.android.package-archive" );
				iLoongLauncher.getInstance().startActivity( intent2 );
			}
			else
				downloadAPK( iLoongLauncher.getInstance() , RR.getPackageName() , packname , title );
		}
		//		else if (!FindDownLoad(packname)) {
		//			downloadinfo dli = new downloadinfo();
		//			dli.packname = packname;
		//			dli.downloadtype = DLTYPE_DOWNLOAD;
		//			dli.downloadid = m_downid++;
		//			dli.name = name;
		//			dli.title = title;
		//			dli.type = type;
		//			dli.VersionCode = versioncode;
		//			dli.dll = dll;
		//			dli.customerid = customerid;
		//			CreateNotify(dli);
		//			mDownloads.add(dli);
		//			
		//			if (customerid == null || customerid.equals("server"))
		//				LauncherModel.getWorkerThread().post(new downloadGetClient(dli));/*从运营的服务器取得正确的下载地址下载*/
		//			else// if (customerid.equals("widget"))
		//				LauncherModel.getWorkerThread().post(new downloadClient(dli));
		//		}
	}
	
	private boolean FindDownLoad(
			String packname )
	{
		for( int i = 0 ; i < mDownloads.size() ; i++ )
		{
			if( mDownloads.get( i ).packname.equals( packname ) )
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean HaveDownLoad(
			String packname )
	{
		if( packname == null )
			return false;
		return FindDownLoad( packname );
	}
	
	class downloadClient implements Runnable
	{
		
		List<NameValuePair> mParams = new ArrayList<NameValuePair>();
		downloadinfo mDoanloadInfo;
		
		public downloadClient(
				downloadinfo dli )
		{
			mDoanloadInfo = dli;
			NameValuePair param1 = new BasicNameValuePair( "type" , dli.type );
			NameValuePair param2 = new BasicNameValuePair( "versioncode" , dli.VersionCode );
			NameValuePair param3 = new BasicNameValuePair( "packname" , dli.packname );
			NameValuePair param4 = new BasicNameValuePair( "customerid" , dli.customerid );
			mParams.add( param1 );
			mParams.add( param2 );
			mParams.add( param3 );
			mParams.add( param4 );
		}
		
		@Override
		public void run()
		{
			final String xml = CustomerHttpClient.post( httpUrl , mParams );
			mHandler.post( new Runnable() {
				
				public void run()
				{
					DLManager.this.CheckDownload( xml , mDoanloadInfo );
				}
			} );
		}
	}
	
	public int getAppType(
			String pkgName )
	{
		int res = -1;
		if( pkgName.equals( "com.cooee.instAssist" ) )
			res = 3;
		else if( pkgName.equals( "com.cooee.store" ) )
			res = 1;
		else if( pkgName.equals( "com.cooee.gameCenter" ) )
			res = 5;
		return res;
	}
	
	class downloadGetClient implements Runnable
	{
		
		ArrayList<NameValuePair> mParams = new ArrayList<NameValuePair>();
		downloadinfo mDoanloadInfo;
		String mUrl = null;
		
		public downloadGetClient(
				downloadinfo dli )
		{
			mDoanloadInfo = dli;
			JSONObject config = Assets.config;
			JSONObject tmp = null;
			try
			{
				tmp = config.getJSONObject( "config" );
			}
			catch( JSONException e1 )
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if( dli.customerid != null && dli.customerid.equals( "server" ) )
			{
				String appID = "f36";
				try
				{
					appID = tmp.getString( "app_id" );
				}
				catch( JSONException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int typeID = getAppType( dli.packname );
				String channelid = "0";
				try
				{
					channelid = tmp.getString( "channel_id" );
				}
				catch( JSONException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mUrl = serverAPPUrl + "?" + "apkid=" + appID + "&" + "type=" + Integer.toString( typeID ) + "&" + "channelid=" + channelid;
				//				para1 = new BasicNameValuePair("apkid", appID);
				//				para2 = new BasicNameValuePair("type", Integer.toString(typeID));
				//				para3 = new BasicNameValuePair("channelid", channelid);
				//
				//				mParams.add(para1);
				//				mParams.add(para2);
				//				mParams.add(para3);
			}
			else
			{
				String domain = "http://androidsub2.cooee.com.cn";
				try
				{
					domain = tmp.getString( "domain" );
				}
				catch( JSONException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String serialno = "androidsubnewabcabc12312";
				try
				{
					serialno = tmp.getString( "serialno" );
				}
				catch( JSONException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mUrl = otherAPPUrl + "?" + "package_name=" + dli.packname + "&" + "domain=" + domain + "&" + "serialno=" + serialno;
				Log.v( "http" , "downloadGetClient get url = " + mUrl );
			}
		}
		
		@Override
		public void run()
		{
			final String jsonObj = CustomerHttpClient.get( mUrl , mParams );
			mHandler.post( new Runnable() {
				
				public void run()
				{
					DLManager.this.CheckGetDownload( jsonObj , mDoanloadInfo );
				}
			} );
		}
	}
	
	private void CheckDownload(
			String xml ,
			downloadinfo dli )
	{
		if( xml == null )
		{
			Log.e( "testDrag" , " checkDownload xml null" );
			onError( dli , R3D.getString( RR.string.dlmng_check_network ) , true );
			return;
		}
		ParseXml( xml , dli );
		if( dli.download && dli.downloadtype == DLTYPE_DOWNLOAD )
		{
			DownLoadApk( dli );
		}
		else if( dli.download && dli.downloadtype == DLTYPE_CHECKED )
		{
			OnCheckedComplete( 0 , dli );
		}
		else if( dli.downloadtype == DLTYPE_CHECKED )
		{
			OnCheckedComplete( -1 , dli );
		}
		else if( dli.download == false )
		{
			onError( dli , R3D.getString( RR.string.dlmng_check_network ) , true );
		}
		else
		{
			onError( dli , R3D.getString( RR.string.dlmng_comeout_soon ) , true );
		}
	}
	
	private void CheckGetDownload(
			String jsonObj ,
			downloadinfo dli )
	{
		if( jsonObj == null )
		{
			onError( dli , R3D.getString( RR.string.dlmng_check_network ) , true );
			return;
		}
		JSONObject jLocal = null;
		try
		{
			jLocal = new JSONObject( jsonObj );
		}
		catch( JSONException e )
		{
			e.printStackTrace();
			onError( dli , R3D.getString( RR.string.dlmng_check_network ) , true );
			return;
		}
		String res = "-1";/* default error */
		try
		{
			res = jLocal.getString( "errno" );
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			onError( dli , R3D.getString( RR.string.dlmng_check_network ) , true );
			return;
		}
		int resInt;
		if( res != null )
			resInt = Integer.valueOf( res );
		else
			resInt = 1;
		if( resInt == 0 )
		{
			try
			{
				dli.url = jLocal.getString( "downurl" );
			}
			catch( JSONException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				onError( dli , R3D.getString( RR.string.dlmng_check_network ) , true );
				return;
			}
			DownLoadApk( dli );
		}
		else if( resInt == 1 )
		{
			OnCheckedComplete( -1 , dli );
		}
		else
		{
			onError( dli , R3D.getString( RR.string.dlmng_comeout_soon ) , true );
		}
	}
	
	public void OnCheckedComplete(
			int flag ,
			downloadinfo dli )
	{
		dli.dll.OnCheckedComplete( dli.name , dli.packname , flag , dli.VersionCode , dli.VersionName );
		mDownloads.remove( dli );
	}
	
	public void ParseXml(
			String xml ,
			downloadinfo dli )
	{
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try
		{
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			DownloadXMLHandler handler = new DownloadXMLHandler( dli );
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
	
	class DownloadXMLHandler extends DefaultHandler
	{
		
		boolean btagiloong = false;
		boolean btagdownload = false;
		boolean btagurl = false;
		boolean btagvercode = false;
		boolean btagvername = false;
		boolean btagmessage = false;
		downloadinfo mdli;
		
		DownloadXMLHandler(
				downloadinfo dli )
		{
			mdli = dli;
		}
		
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
				mdli.download = download.equals( "0" ) ? false : true;
			}
			else if( btagiloong && btagvercode )
			{
				mdli.VersionCode = new String( ch , start , length );
			}
			else if( btagiloong && btagvername )
			{
				mdli.VersionName = new String( ch , start , length );
			}
			else if( btagiloong && btagurl )
			{
				mdli.url = new String( ch , start , length );
			}
			else if( btagiloong && btagmessage )
			{
			}
		}
	}
	
	private void DownLoadApk(
			downloadinfo dli )
	{
		if( iLoongApplication.getSDPath() == null )
		{
			onError( dli , R3D.getString( RR.string.dlmng_pls_insert_SD ) , true );
			return;
		}
		SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.app_downloading ) + iLoongLauncher.languageSpace + dli.title );
		new downloadTask( handler , dli ).start();
	}
	
	public void CreateNotify(
			downloadinfo dli )
	{
		long when = System.currentTimeMillis();
		dli.statusnotify = new Notification( RR.drawable.download , dli.title , when );
		dli.statusnotify.contentView = new RemoteViews( SetupMenu.getContext().getPackageName() , RR.layout.notification );
		dli.statusnotify.contentView.setTextColor( RR.id.downtitle , 0xFFFFFFFF );
		dli.statusnotify.contentView.setTextViewText( RR.id.downtitle , dli.title );
		dli.statusnotify.contentView.setProgressBar( RR.id.pb , 100 , 0 , false );
		Intent notificationIntent = new Intent( iLoongLauncher.getInstance() , iLoongLauncher.getInstance().getClass() );
		PendingIntent contentIntent = PendingIntent.getActivity( iLoongLauncher.getInstance() , 0 , notificationIntent , 0 );
		dli.statusnotify.contentIntent = contentIntent;
	}
	
	private void notify(
			downloadinfo dli ,
			int progress )
	{
		if( progress == 100 )
		{
			dli.statusnotify.contentView.setProgressBar( RR.id.pb , 100 , progress , false );
			String strpb = " 100%";
			dli.statusnotify.contentView.setTextColor( RR.id.downprog , 0xFFFFFFFF );
			dli.statusnotify.contentView.setTextViewText( RR.id.downprog , strpb );
			mNM.notify( dli.downloadid , dli.statusnotify );
			mNM.cancel( dli.downloadid );
		}
		else if( progress == -1 )
		{
			mNM.cancel( dli.downloadid );
		}
		else
		{
			dli.statusnotify.contentView.setProgressBar( RR.id.pb , 100 , progress , false );
			String strpb = " " + String.valueOf( progress ) + "%";
			dli.statusnotify.contentView.setTextColor( RR.id.downprog , 0xFFFFFFFF );
			dli.statusnotify.contentView.setTextViewText( RR.id.downprog , strpb );
			mNM.notify( dli.downloadid , dli.statusnotify );
		}
	}
	
	Handler handler = new Handler( iLoongApplication.getInstance().getMainLooper() ) {
		
		@Override
		public void handleMessage(
				Message msg )
		{
			switch( msg.what )
			{
				case 100:
					DLManager.this.notify( ( (downloadinfo)msg.obj ) , 100 );
					DownloadComplete( (downloadinfo)msg.obj );
					break;
				case 101:
					DLManager.this.notify( ( (downloadinfo)msg.obj ) , -1 );
					DLManager.this.onError( ( (downloadinfo)msg.obj ) , R3D.getString( RR.string.dlmng_network_error ) , true );
					break;
				case 404:
					DLManager.this.notify( ( (downloadinfo)msg.obj ) , -1 );
					DLManager.this.onError( ( (downloadinfo)msg.obj ) , R3D.getString( RR.string.dlmng_download_configwrong ) , true );
					break;
				case -1:
					DLManager.this.notify( ( (downloadinfo)msg.obj ) , -1 );
					DLManager.this.onError( ( (downloadinfo)msg.obj ) , R3D.getString( RR.string.dlmng_download_error ) , true );
					break;
				default:
					int progress = ( Double.valueOf( ( msg.arg2 * 1.0 / msg.arg1 * 100 ) ) ).intValue();
					DLManager.this.notify( ( (downloadinfo)msg.obj ) , progress );
					break;
			}
		}
	};
}
