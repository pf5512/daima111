package com.coco.theme.themebox;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coco.theme.themebox.preview.ThemePreviewHotActivity;
import com.coco.theme.themebox.service.ThemeService;
import com.coco.theme.themebox.service.ThemesDB;
import com.coco.theme.themebox.util.Log;
import com.iLoong.base.themebox.R;
import com.umeng.analytics.MobclickAgent;


public class ThemeGridLocalAdapter extends BaseAdapter
{
	
	private static final String GOOGLE_PLAY_STORE_PACKAGE_NAME = "com.android.vending";
	private static final String GOOGLE_PLAY_STORE_CLASS_NAME = "com.android.vending.AssetBrowserActivity";
	private static String themeDownloadUri = "https://play.google.com/store/apps/details?id=";
	private List<ThemeInformation> localList = new ArrayList<ThemeInformation>();
	private Context context;
	private Bitmap imgDefaultThumb;
	private Set<String> packageNameSet = new HashSet<String>();
	private ComponentName currentTheme = new ComponentName( "" , "" );
	private Handler mMainHandler = new Handler();
	private Toast toast;
	
	public ThemeGridLocalAdapter(
			Context cxt )
	{
		context = cxt;
		imgDefaultThumb = ( (BitmapDrawable)cxt.getResources().getDrawable( R.drawable.default_img ) ).getBitmap();
		if( com.coco.theme.themebox.util.FunctionConfig.isLoadVisible() )
		{
			mMainHandler.postDelayed( new Runnable() {
				
				@Override
				public void run()
				{
					queryPackage();
					notifyDataSetChanged();
				}
			} , 50 );
		}
	}
	
	private void queryPackage()
	{
		packageNameSet.clear();
		localList.clear();
		ThemeService themeSv = new ThemeService( context );
		List<ThemeInformation> installList = themeSv.queryInstallList();
		for( ThemeInformation info : installList )
		{
			info.setThumbImage( context , info.getPackageName() , info.getClassName() );
			localList.add( info );
			packageNameSet.add( info.getPackageName() );
		}
		currentTheme = themeSv.queryCurrentTheme();
		List<ThemeInformation> virtualList = queryVirtualThemes();
		ThemeInformation info = null;
		for( int i = 0 ; i < virtualList.size() ; i++ )
		{
			info = virtualList.get( i );
			if( !packageNameSet.contains( info.getPackageName() ) )
			{
				localList.add( info );
			}
		}
	}
	
	public void reloadPackage()
	{
		queryPackage();
		( (Activity)context ).runOnUiThread( new Runnable() {
			
			@Override
			public void run()
			{
				notifyDataSetChanged();
			}
		} );
	}
	
	public void updateDownloadSize(
			String pkgName ,
			long downSize ,
			long totalSize )
	{
		int findIndex = findPackageIndex( pkgName );
		if( findIndex < 0 )
		{
			return;
		}
		ThemeInformation info = localList.get( findIndex );
		info.setDownloadSize( downSize );
		info.setTotalSize( totalSize );
		notifyDataSetChanged();
	}
	
	public Set<String> getPackageNameSet()
	{
		return packageNameSet;
	}
	
	public boolean containPackage(
			String packageName )
	{
		return findPackageIndex( packageName ) >= 0;
	}
	
	private int findPackageIndex(
			String packageName )
	{
		int i = 0;
		for( i = 0 ; i < localList.size() ; i++ )
		{
			if( packageName.equals( localList.get( i ).getPackageName() ) )
			{
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public int getCount()
	{
		return localList.size();
	}
	
	@Override
	public Object getItem(
			int position )
	{
		return localList.get( position );
	}
	
	@Override
	public long getItemId(
			int position )
	{
		return position;
	}
	
	@Override
	public View getView(
			int position ,
			View convertView ,
			ViewGroup parent )
	{
		View retView = convertView;
		if( retView == null )
		{
			retView = View.inflate( context , R.layout.theme_grid_item , null );
		}
		final ThemeInformation themeInfo = (ThemeInformation)getItem( position );
		if( !themeInfo.bIsVirtualTheme )
		{
			if( themeInfo.isNeedLoadDetail() )
			{
				Bitmap imgThumb = themeInfo.getThumbImage();
				if( imgThumb == null )
				{
					themeInfo.loadDetail( context );
					if( themeInfo.getThumbImage() != null )
					{
						StaticClass.saveMyBitmap( context , themeInfo.getPackageName() , themeInfo.getClassName() , themeInfo.getThumbImage() );
					}
				}
			}
		}
		Bitmap imgThumb = themeInfo.getThumbImage();
		if( imgThumb == null )
		{
			imgThumb = imgDefaultThumb;
		}
		ImageView viewThumb = (ImageView)retView.findViewById( R.id.imageThumb );
		viewThumb.setImageBitmap( imgThumb );
		viewThumb.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				if( !themeInfo.bIsVirtualTheme )
				{
					Intent i = new Intent();
					i.putExtra( StaticClass.EXTRA_PACKAGE_NAME , themeInfo.getPackageName() );
					Log.v( "************" , "000000000000packname = " + themeInfo.getPackageName() );
					i.putExtra( StaticClass.EXTRA_CLASS_NAME , themeInfo.getClassName() );
					i.putExtra( "current_theme_packagename" , currentTheme.getPackageName() );
					i.putExtra( "current_theme_classname" , currentTheme.getClassName() );
					i.setClass( context , ThemePreviewHotActivity.class );
					context.startActivity( i );
					( (Activity)context ).overridePendingTransition( 0 , 0 );
				}
				else
				{
					if( isHaveInternet() )
					{
						downloadTheme( themeDownloadUri + themeInfo.getPackageName() );
					}
					else
					{
						if( toast != null )
						{
							toast.setText( R.string.internet_err );
						}
						else
						{
							toast = Toast.makeText( context , R.string.internet_err , Toast.LENGTH_SHORT );
						}
						toast.show();
					}
				}
			}
		} );
		ImageButton buttonApply = (ImageButton)retView.findViewById( R.id.buttonApply );
		buttonApply.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				//友盟  应用统计
				MobclickAgent.onEvent( context , "Apply" , themeInfo.getPackageName() + "-" + themeInfo.getClassName() );
				ThemeService sv = new ThemeService( context );
				sv.applyTheme( new ComponentName( themeInfo.getPackageName() , themeInfo.getClassName() ) );
				Toast.makeText( context , context.getString( R.string.toastPreviewApply ) , Toast.LENGTH_SHORT ).show();
				context.sendBroadcast( new Intent( StaticClass.ACTION_DEFAULT_THEME_CHANGED ) );
				ActivityManager.KillActivity();
			}
		} );
		ImageButton buttonUsed = (ImageButton)retView.findViewById( R.id.buttonUsed );
		ImageButton buttonDownload = (ImageButton)retView.findViewById( R.id.buttonDownload );
		buttonDownload.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				// TODO Auto-generated method stub
				if( isHaveInternet() )
				{
					downloadTheme( themeDownloadUri + themeInfo.getPackageName() );
				}
				else
				{
					if( toast != null )
					{
						toast.setText( R.string.internet_err );
					}
					else
					{
						toast = Toast.makeText( context , R.string.internet_err , Toast.LENGTH_SHORT );
					}
					toast.show();
				}
			}
		} );
		TextView themeName = (TextView)retView.findViewById( R.id.themeName );
		String showName=themeInfo.getDisplayName();
		int index = showName.indexOf( "-" );
		if( index >= 0 )
		{
			showName = showName.substring( 0 , index );
		}
		themeName.setText( showName );
		if( currentTheme.getPackageName().equals( themeInfo.getPackageName() ) && currentTheme.getClassName().equals( themeInfo.getClassName() ) )
		{
			if( !themeInfo.bIsVirtualTheme )
			{
				buttonApply.setVisibility( View.INVISIBLE );
				buttonUsed.setVisibility( View.VISIBLE );
				buttonDownload.setVisibility( View.INVISIBLE );
			}
			else
			{
				buttonApply.setVisibility( View.INVISIBLE );
				buttonUsed.setVisibility( View.INVISIBLE );
				buttonDownload.setVisibility( View.VISIBLE );
			}
			retView.findViewById( R.id.imageUsed ).setVisibility( View.VISIBLE );
		}
		else
		{
			if( !themeInfo.bIsVirtualTheme )
			{
				buttonApply.setVisibility( View.VISIBLE );
				buttonUsed.setVisibility( View.INVISIBLE );
				buttonDownload.setVisibility( View.INVISIBLE );
			}
			else
			{
				buttonApply.setVisibility( View.INVISIBLE );
				buttonUsed.setVisibility( View.INVISIBLE );
				buttonDownload.setVisibility( View.VISIBLE );
			}
			retView.findViewById( R.id.imageUsed ).setVisibility( View.INVISIBLE );
		}
		return retView;
	}
	
	private List<ThemeInformation> queryVirtualThemes()
	{
		List<ThemeInformation> list = new ArrayList<ThemeInformation>();
		try
		{
			Context remoteContext = context.createPackageContext( ThemesDB.LAUNCHER_PACKAGENAME , Context.CONTEXT_IGNORE_SECURITY );
			loadVirtualConfig( remoteContext , list );
		}
		catch( NameNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return list;
		}
		return list;
	}
	
	public void loadVirtualConfig(
			Context remoteContext ,
			List<ThemeInformation> list )
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		InputStream configStream = null;
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			configStream = remoteContext.getAssets().open( "preview_config.xml" );
			Document doc = builder.parse( configStream );
			Element rootElement = doc.getDocumentElement();
			Element parentElement = rootElement;
			List<Node> virtualList = getChildNodeList( parentElement , "virtual" );
			for( int i = 0 ; i < virtualList.size() ; i++ )
			{
				Node node = virtualList.get( i );
				if( node instanceof Element )
				{
					parentElement = (Element)node;
					ThemeInformation info = new ThemeInformation();
					info.bIsVirtualTheme = true;
					String packageName = parentElement.getAttribute( "packageName" );
					info.getInfoItem().setPackageName( packageName );
					String introduce = getElementValue( parentElement , "displayname" );
					info.setDisplayName( introduce );
					String thumbPath = getAttributeValue( parentElement , "thumb" , "path" );
					info.setThumbImageOnly( BitmapFactory.decodeStream( remoteContext.getAssets().open( thumbPath ) ) );
					list.add( info );
				}
			}
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
	
	private void downloadTheme(
			String uriString )
	{
		Intent intent = new Intent( Intent.ACTION_VIEW , Uri.parse( uriString ) );
		if( isPlayStoreInstalled() )
		{
			intent.setClassName( GOOGLE_PLAY_STORE_PACKAGE_NAME , GOOGLE_PLAY_STORE_CLASS_NAME );
		}
		else
		{
			intent.addCategory( Intent.CATEGORY_BROWSABLE );
		}
		context.startActivity( intent );
	}
	
	private boolean isHaveInternet()
	{
		try
		{
			ConnectivityManager manger = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo info = manger.getActiveNetworkInfo();
			return( info != null && info.isConnected() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean isPlayStoreInstalled()
	{
		try
		{
			context.getPackageManager().getPackageInfo( GOOGLE_PLAY_STORE_PACKAGE_NAME , PackageManager.GET_ACTIVITIES );
			return true;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
			return false;
		}
	}
}
