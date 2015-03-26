package com.iLoong.launcher.desktop;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.pub.provider.PubProviderHelper;
import com.iLoong.launcher.theme.ThemeDescription;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.theme.adapter.ContentConfig;
import com.iLoong.theme.adapter.LockManager;
import com.iLoong.theme.adapter.PlatformInfo;
import com.iLoong.theme.adapter.WallpaperInfo;


public class OneKeyUpdateReciever extends BroadcastReceiver
{
	
	public static final String ONEKEY_THEME_ACTION = "com.cooee.doov.ONEKEY_THEME_UPDATE";
	public static final String ONEKEY_LOCKSCREEN_ACTION = "com.cooee.doov.ONEKEY_LOCKSCREEN_UPDATE";
	public static final String ONEKEY_TYPEFACE_ACTION = "com.cooee.doov.ONEKEY_TYPEFACE_UPDATE";
	public static final String ONEKEY_EFFECTS_ACTION = "com.cooee.doov.ONEKEY_EFFECTS_UPDATE";
	public static final String ONEKEY_ALLSKIN_ACTION = "com.cooee.doov.ONEKEY_ALLSKIN_UPDATE";
	public static final String ONEKEY_WALLPAPER_ACTION = "com.cooee.doov.ONEKEY_WALLPAPER_UPDATE";
	private static final String SYS_SETTING_CURRENT_LOCK = "cooee_current_lock";
	private static final String REDRAW_ALL = "com.font.type.changed.ACTION";
	private static final int SYS_SETTING_LOCK_VERSION = 2001;
	Context mContext;
	private Handler mHandler = new Handler();
	
	@Override
	public void onReceive(
			Context context ,
			Intent intent )
	{
		// TODO Auto-generated method stub
		String action = intent.getAction();
		mContext = context;
		if( action.equals( ONEKEY_THEME_ACTION ) )
		{
			applyNewTheme();
		}
		else if( action.equals( ONEKEY_LOCKSCREEN_ACTION ) )
		{
			applyNewLockScreen();
		}
		else if( action.equals( ONEKEY_TYPEFACE_ACTION ) )
		{
			applyNewFont();
		}
		else if( action.equals( ONEKEY_EFFECTS_ACTION ) )
		{
			applyNewEffect();
		}
		else if( action.equals( ONEKEY_ALLSKIN_ACTION ) )
		{
			ThemeManager mThemeManager = ThemeManager.getInstance();
			Vector<ThemeDescription> vector = mThemeManager.getThemeDescriptions();
			if( vector.size() <= 1 )
			{
				return;
			}
			ThemeDescription current = mThemeManager.getCurrentThemeDescription();
			int currentIndex = vector.indexOf( current );
			int random = getRandomIndex( vector.size() , currentIndex );
			SharedPreferences prefs = context.getSharedPreferences( "theme" , Activity.MODE_WORLD_WRITEABLE );
			prefs.edit().putString( "theme" , vector.elementAt( random ).componentName.getPackageName() ).commit();
			prefs.edit().putInt( "theme_status" , 1 ).commit();
			PubProviderHelper.addOrUpdateValue( "theme" , "theme" , vector.elementAt( random ).componentName.getPackageName() );
			PubProviderHelper.addOrUpdateValue( "theme" , "theme_status" , "1" );
			applyNewFont();
			ThemeManager.getInstance().ApplyTheme( vector.elementAt( random ) );
		}
		else if( action.equals( REDRAW_ALL ) )
		{
			//			System.out.println("redraw all launcher");
			//			Gdx.graphics.requestRendering();
		}
		else if( action.equals( ONEKEY_WALLPAPER_ACTION ) )
		{
			applyWallpaper();
		}
	}
	
	private int getRandomIndex(
			int max ,
			int cur )
	{
		Random rand = new Random();
		if( max == 0 )
		{
			return max;
		}
		int newIndex = rand.nextInt( max );
		int count = 0;
		while( newIndex == cur )
		{
			newIndex = rand.nextInt( max );
			count++;
			if( count > 100 )
			{
				count = 0;
				break;
			}
		}
		return newIndex;
	}
	
	private void applyNewTheme()
	{
		ThemeManager mThemeManager = ThemeManager.getInstance();
		Vector<ThemeDescription> vector = mThemeManager.getThemeDescriptions();
		if( vector.size() <= 1 )
		{
			return;
		}
		ThemeDescription current = mThemeManager.getCurrentThemeDescription();
		int currentIndex = vector.indexOf( current );
		int random = getRandomIndex( vector.size() , currentIndex );
		mThemeManager.ApplyTheme( vector.elementAt( random ) );
	}
	
	private void applyNewEffect()
	{
		Context context = SetupMenu.getContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
		Root3D root = iLoongLauncher.getInstance().d3dListener.getRoot();
		String[] value = null;
		String initValue;
		int random = 0;
		if( root != null )
		{
			//			value = context.getResources().getStringArray(
			//					RR.array.workspace_effectsvalue_list_preference);
			//			initValue = preferences.getString(SetupMenu.getKey(RR.string.setting_key_desktopeffects), "0");
			//			String language = Locale.getDefault().getLanguage();
			//			
			//			//teapotXu add start
			//			int effect_max_num = value.length;
			//			
			//			if (DefaultLayout.page_effect_no_radom_style){
			//				effect_max_num -= 1;
			//			}
			//			if(DefaultLayout.disable_crystal_effect_in_workspace && (language!=null&&(language.equals("en")||language.equals("zh")))){
			//				effect_max_num -= 1;
			//			}
			initValue = preferences.getString( SetupMenu.getKey( RR.string.setting_key_desktopeffects ) , "0" );
			value = R3D.workSpace_list_string;
			if( Integer.parseInt( initValue ) >= value.length )
			{
				initValue = "0";
			}
			int effect_max_num = value.length;
			random = getRandomIndex( effect_max_num , Integer.parseInt( initValue ) );
			//			preferences.edit().putString(SetupMenu.getKey(RR.string.setting_key_desktopeffects),
			//					value[random]).commit();
			preferences.edit().putString( SetupMenu.getKey( RR.string.setting_key_desktopeffects ) , "" + random ).commit();
			PubProviderHelper.addOrUpdateValue( "effect" , SetupMenu.getKey( RR.string.setting_key_desktopeffects ) , random + "" );
			SetupMenu.getContext().sendBroadcast( new Intent( "com.coco.effect.action.DEFAULT_EFFECT_CHANGED" ) );
			root.setWorkspaceEffectType( random );
		}
	}
	
	private void applyNewLockScreen()
	{
		LockManager lockmgr = new LockManager( mContext );
		ComponentName currentLock = lockmgr.queryCurrentLock();
		List<ResolveInfo> installList = lockmgr.queryInstallList();
		if( installList.size() <= 1 )
		{
			return;
		}
		int cur = 0;
		for( ResolveInfo lockInfo : installList )
		{
			if( lockInfo.activityInfo.packageName.equals( currentLock.getPackageName() ) && lockInfo.activityInfo.name.equals( currentLock.getClassName() ) )
			{
				break;
			}
			cur++;
		}
		int random = getRandomIndex( installList.size() , cur );
		ResolveInfo newlock = installList.get( random );
		Context dstContext = null;
		try
		{
			dstContext = mContext.createPackageContext( newlock.activityInfo.packageName , Context.CONTEXT_IGNORE_SECURITY );
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
			return;
		}
		ContentConfig destContent = new ContentConfig();
		destContent.loadContentWrap( dstContext , newlock.activityInfo.packageName );
		applyLock( newlock.activityInfo.packageName , newlock.activityInfo.name , destContent.getWrapName() );
		mContext.sendBroadcast( new Intent( "com.coco.lock.action.DEFAULT_LOCK_CHANGED" ) );
	}
	
	private void applyLock(
			String packageName ,
			String className ,
			String wrapName )
	{
		if( PlatformInfo.getInstance( mContext ).isSupportViewLock() )
		{
			System.out.println( "className = " + className + " wrapName = " + wrapName );
			String strValue = String.format( Locale.getDefault() , "%d,%s,%s,%s" , SYS_SETTING_LOCK_VERSION , packageName , wrapName , className );
			android.provider.Settings.System.putString( mContext.getContentResolver() , SYS_SETTING_CURRENT_LOCK , strValue );
		}
		else
		{
			Uri CURENT_LOCK_URI = Uri.parse( "content://" + "com.coco.lock2.lockbox" + "/currentLock" );
			ContentResolver contentResolver = mContext.getContentResolver();
			ContentValues values = new ContentValues();
			values.put( "lockPackageName" , packageName );
			values.put( "lockClassName" , className );
			values.put( "lockWrapName" , wrapName );
			contentResolver.update( CURENT_LOCK_URI , values , null , null );
		}
	}
	
	private void applyNewFont()
	{
		List<Integer> fonts = new ArrayList<Integer>();
		fonts.clear();
		getFontList( fonts );
		if( fonts.size() <= 1 )
		{
			return;
		}
		int cur = 0;
		int currentType = Settings.System.getInt( mContext.getContentResolver() , "font_type" , 1 );
		for( int item : fonts )
		{
			if( currentType == item )
			{
				break;
			}
			cur++;
		}
		if( DefaultLayout.enable_apply_saved_cur_screen_when_reboot )
		{
			Workspace3D workspace = iLoongLauncher.getInstance().d3dListener.getWorkspace3D();
			//save the current screen num
			PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().putBoolean( "apply_current_screen_num" , true ).commit();
			PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().putInt( "current_screen_num" , workspace.getCurrentScreen() ).commit();
		}
		int random = getRandomIndex( fonts.size() , cur );
		Intent intent = new Intent( "com.cooee.font.type.ACTION" );
		intent.putExtra( "FONT_TYPE" , fonts.get( random ) );
		mContext.sendBroadcast( intent );
	}
	
	private void getFontList(
			List<Integer> list )
	{
		Context themeboxContext = null;
		if( DefaultLayout.personal_center_internal )
		{
			themeboxContext = iLoongLauncher.getInstance();
		}
		else
		{
			try
			{
				themeboxContext = iLoongLauncher.getInstance().createPackageContext( "com.iLoong.base.themebox" , 0 );
			}
			catch( NameNotFoundException e1 )
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
				themeboxContext = iLoongLauncher.getInstance();
			}
		}
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try
		{
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			FontConfigHandler handler = new FontConfigHandler( list );
			xmlreader.setContentHandler( handler );
			InputSource xmlin;
			InputStream in = themeboxContext.getAssets().open( "fonts/font_config.xml" );
			if( in == null )
			{
				return;
			}
			else
			{
				xmlin = new InputSource( in );
			}
			xmlreader.parse( xmlin );
			handler = null;
			xmlin = null;
			if( in != null )
			{
				in.close();
			}
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
	
	private void applyWallpaper()
	{
		WallpaperInfo info = new WallpaperInfo( iLoongLauncher.getInstance() );
		List<String> images = new ArrayList<String>();
		List<String> thumbs = new ArrayList<String>();
		info.findWallpapers( images , thumbs , iLoongApplication.themeConfig.customWallpaperPath );
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( mContext );
		String currentWallpaper = pref.getString( "currentWallpaper" , "default" );
		int current = 0;
		if( currentWallpaper.equals( "default" ) )
		{
			current = 0;
		}
		else
		{
			current = images.indexOf( currentWallpaper );
		}
		if( images.size() <= 1 && current == 0 )
		{
			return;
		}
		int position = getRandomIndex( images.size() , current );
		if( DefaultLayout.enable_scene_wallpaper )
		{
			Intent it = new Intent( WallpaperChangedReceiver.SCENE_WALLPAPER_CHANGE );
			it.putExtra( "wallpaper" , images.get( position ) );
			mContext.sendBroadcast( it );
		}
		else
		{
			info.selectWallpaper( position );
			pref.edit().putString( "currentWallpaper" , images.get( position ) ).commit();
			pref.edit().putBoolean( "cooeechange" , true ).commit();
			PubProviderHelper.addOrUpdateValue( "wallpaper" , "currentWallpaper" , images.get( position ) );
			PubProviderHelper.addOrUpdateValue( "wallpaper" , "cooeechange" , "true" );
		}
	}
	
	class FontConfigHandler extends DefaultHandler
	{
		
		private final String TAG_ITEM = "item";
		List<Integer> fontsList;
		
		public FontConfigHandler(
				List<Integer> list )
		{
			fontsList = list;
		}
		
		public void startElement(
				String namespaceURI ,
				String localName ,
				String qName ,
				Attributes atts ) throws SAXException
		{
			if( localName.equals( TAG_ITEM ) )
			{
				fontsList.add( Integer.parseInt( atts.getValue( "font_type" ) ) );
			}
		}
	}
}
