package com.iLoong.launcher.SetupMenu;


import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class SetupMenu
{
	
	//	public static final String SETUPMENU_FILENAME = "/assets/setupmenu/setupmenu.xml";
	public static final String SETUPMENU_ORIGINAL_FOLDERNAME = "launcher/setupmenu/";
	public static final String SETUPMENU_ANDROID4_FOLDERNAME = "launcher/setupmenu_android4/";
	public static final String SETUPMENU_TOP_EDGE = "launcher/setupmenu/smtopedge.png";
	public static final String SETUPMENU_TOP_MID = "launcher/setupmenu/smtopmid.png";
	public static String SETUPMENU_BG_CLOLOR;
	public static final String SETUPMENU_SPACING = "launcher/setupmenu/smspacing.png";
	public static final String SETUPMENUT_INDICATOR_FOCUSED = "launcher/setupmenu/indicator_focused.png";
	public static final String SETUPMENUT_INDICATOR_NORMAL = "launcher/setupmenu/indicator_normal.png";
	public static final String TAG_SETUPMENU = "setupmenu";
	public static final String TAG_MENU = "menu";
	public static final String TAG_ITEM = "item";
	public static final float BITMAPSCALE = 1.5f;
	public static final int BITMAPCELLWIDTH = 80; // dip
	public static int BITMAPCELLHEIGHT;// dip
	public static final int POPMENU_STYLE_ORIGINAL = 0;
	public static final int POPMENU_STYLE_ANDROID4 = 1;
	public static final int POPMENU_STYLE_SQUARE = 2;
	public static String SETUPMENU_FOLDERNAME;
	private ArrayList<SetupTabMenu> mTabMenus = new ArrayList<SetupTabMenu>();
	private HashMap<Integer , ArrayList<SetupMenuItem>> mMenuItems = new HashMap<Integer , ArrayList<SetupMenuItem>>();
	private SetMenuDesktop mSetupMenuLanyout;
	public static float mScale = 1.0F;
	public static float mScreenScale;
	public int mcolumns = 0;
	public int mrows = 0;
	public int mWidth = 0;
	public int mHeight = 0;
	public int mWidthGap = 0;
	public int mHeightGap = 0;
	public int mCellWidth = 0;
	public int mCellHeight = 0;
	public int mTabCellWidth = 0;
	private static Context mContext;
	public boolean mOpen = false;
	public static SetupMenu mInstance;
	
	//	public PopupWindow p = null;
	public static SetupMenu getInstance()
	{
		return mInstance;
	}
	
	public SetMenuDesktop getMenuDeskTop()
	{
		return mSetupMenuLanyout;
	}
	
	public SetupMenuItem getMenuItem(
			int action )
	{
		Iterator<ArrayList<SetupMenuItem>> it = mMenuItems.values().iterator();
		while( it.hasNext() )
		{
			ArrayList<SetupMenuItem> menuitems = it.next();
			for( int i = 0 ; i < menuitems.size() ; i++ )
			{
				if( menuitems.get( i ).id == action )
				{
					return menuitems.get( i );
				}
			}
		}
		return null;
	}
	
	public SetupMenu(
			Context context )
	{
		mContext = context;
		mInstance = this;
		if( DefaultLayout.popmenu_style == POPMENU_STYLE_ANDROID4 )
		{
			SETUPMENU_FOLDERNAME = SETUPMENU_ANDROID4_FOLDERNAME;
			BITMAPCELLHEIGHT = 48;
		}
		else
		{
			SETUPMENU_FOLDERNAME = SETUPMENU_ORIGINAL_FOLDERNAME;
			if( RR.net_version )
			{
				BITMAPCELLHEIGHT = 77;
			}
			else
				BITMAPCELLHEIGHT = 67;
		}
		SETUPMENU_BG_CLOLOR = SETUPMENU_FOLDERNAME + "bg.png";
		if( RR.net_version )
		{
			SetupMenuActions.getInstance().init( context );
		}
		Utils3D.showTimeFromStart( "before load" );
		if( RR.net_version )
		{
			LoadNetSetupMenuXml();
		}
		else
			LoadSetupMenuXml();
		Utils3D.showTimeFromStart( "after load" );
		DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
		mWidth = displayMetrics.widthPixels;
		mHeight = displayMetrics.heightPixels;
		mScreenScale = displayMetrics.density;
		mScale = mScreenScale / BITMAPSCALE;
		mCellWidth = mWidth / mcolumns;//Tools.dip2px(mContext, BITMAPCELLWIDTH);// 3; //
		mCellHeight = Tools.dip2px( mContext , BITMAPCELLHEIGHT );
		mTabCellWidth = (int)( ( (float)mWidth / ( mTabMenus.size() > 0 ? mTabMenus.size() : 1 ) ) + 0.999 );
		mSetupMenuLanyout = new SetMenuDesktop( context );
		mSetupMenuLanyout.setSetupMenu( this );
		mSetupMenuLanyout.setMenuItems( mTabMenus , mMenuItems );
		if( RR.net_version )
		{
			mSetupMenuLanyout.LoadNetLayout();
		}
		else
			mSetupMenuLanyout.LoadLayout();
		mSetupMenuLanyout.setFocusable( true );
		mSetupMenuLanyout.setFocusableInTouchMode( true );
		mSetupMenuLanyout.setOnClickListener( new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				// TODO Auto-generated method stub
			}
		} );
		if( DefaultLayout.popmenu_style == POPMENU_STYLE_ANDROID4 )
		{
			float horMargin = mWidth / 15f;
			mSetupMenuLanyout.setPadding( (int)horMargin , 0 , (int)horMargin , 0 );
		}
		FrameLayout.LayoutParams popwinlp = new FrameLayout.LayoutParams( LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT );
		( (iLoongLauncher)context ).addContentView( mSetupMenuLanyout , popwinlp );
		mSetupMenuLanyout.setVisibility( View.INVISIBLE );
	}
	
	public void Release()
	{
		mSetupMenuLanyout.Release();
		Iterator<ArrayList<SetupMenuItem>> it = mMenuItems.values().iterator();
		while( it.hasNext() )
		{
			ArrayList<SetupMenuItem> menuitems = it.next();
			for( int i = 0 ; i < menuitems.size() ; i++ )
			{
				if( menuitems.get( i ).iconbmp != null )
					menuitems.get( i ).iconbmp.recycle();
				if( menuitems.get( i ).icon2bmp != null )
					menuitems.get( i ).icon2bmp.recycle();
			}
		}
		System.gc();
	}
	
	public static Context getContext()
	{
		return iLoongLauncher.getInstance();
	}
	
	public static String getKey(
			int key )
	{
		return iLoongLauncher.getInstance().getResources().getString( key );
	}
	
	public SetMenuDesktop getSetMenuDesktop()
	{
		return mSetupMenuLanyout;
	}
	
	//Jone start
	public void showScreenCover()
	{
		Root3D.screenFrontImg.color.a = 0;
		Root3D.screenFrontImg.show();
		Desktop3DListener.setCoverVisible( true );
	}
	
	public void LoadNetSetupMenuXml()
	{
		SetupTabMenu smenu;
		if( !DefaultLayout.setup_menu_support_scroll_page )
		{
			try
			{
				mrows = iLoongLauncher.getInstance().getResources().getInteger( RR.integer.setup_menu_row );
				mcolumns = iLoongLauncher.getInstance().getResources().getInteger( RR.integer.setup_menu_columns );
				String[] menus;
				menus = iLoongLauncher.getInstance().getResources().getStringArray( RR.array.setupMenuAll );
				ArrayList<String> menulistall = new ArrayList<String>();
				ArrayList<String> menulistallid = new ArrayList<String>();
				for( int i = 0 ; i < menus.length ; i++ )
				{
					menulistall.add( menus[i] );
					String[] tmp = menus[i].split( "," );
					menulistallid.add( tmp[0] );
				}
				for( int i = 0 ; i < menulistall.size() ; i++ )
				{
					if( DefaultLayout.setupmenu_show_theme )
					{
						if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_SOFTWARE_MANAGEMENT )
						{
							menulistall.remove( i );
							menulistallid.remove( i );
						}
					}
					else
					{
						if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_THEME )
						{
							menulistall.remove( i );
							menulistallid.remove( i );
						}
					}
				}
				// final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
				// final PackageManager pm = mContext.getPackageManager();
				// List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
				// final String name = "xxxxxxxxxxxxxxxxx";//不显示解锁
				// int n = 0;
				// for(n = 0; n < apps.size(); n++){
				// String pName = apps.get(n).activityInfo.packageName;
				// if(pName.equals(name)){
				// for (int i=0; i<menulistall.size(); i++)
				// {
				// if (Integer.parseInt(menulistallid.get(i)) ==
				// ActionSetting.ACTION_SCREEN_EDITING){
				// menulistall.remove(i);
				// menulistallid.remove(i);
				// break;
				// }
				// }
				// break;
				// }
				// }
				// if (n==apps.size()) {
				for( int i = 0 ; i < menulistall.size() ; i++ )
				{
					if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_LOCKER_SETTING )
					{
						menulistall.remove( i );
						menulistallid.remove( i );
						break;
					}
				}
				// }
				for( int i = 0 ; i < menulistall.size() ; i++ )
				{
					if( DefaultLayout.hide_desktop_setup )
					{
						if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_DESKTOP_SETTINGS )
						{
							menulistall.remove( i );
							menulistallid.remove( i );
						}
					}
				}
				if( DefaultLayout.popmenu_style == POPMENU_STYLE_ANDROID4 )
				{
					mrows = menulistall.size() - 1;
					mcolumns = 1;
				}
				SetupTabMenu menu = new SetupTabMenu();
				String[] menuAttrs = menulistall.get( 0 ).split( "," );
				menu.id = Integer.parseInt( menuAttrs[0] );
				Field f = (Field)RR.getStringClass().getDeclaredField( menuAttrs[1] );
				menu.name = R3D.getString( f.getInt( RR.getStringClass() ) );
				smenu = menu;
				mTabMenus.add( menu );
				ArrayList<SetupMenuItem> menulist = new ArrayList<SetupMenuItem>();
				mMenuItems.put( Integer.valueOf( menu.id ) , menulist );
				for( int i = 1 ; i < menulistall.size() ; i++ )
				{
					ArrayList<SetupMenuItem> menuitems = mMenuItems.get( Integer.valueOf( smenu.id ) );
					if( menuitems != null )
					{
						String[] tmp = menulistall.get( i ).split( "," );
						SetupMenuItem mi = new SetupMenuItem();
						mi.page = smenu.id;
						mi.id = Integer.parseInt( tmp[0] );
						Field tmpF = (Field)RR.getStringClass().getDeclaredField( tmp[1] );
						mi.name = R3D.getString( tmpF.getInt( RR.getStringClass() ) );
						mi.icon = SETUPMENU_FOLDERNAME + tmp[2] + ".png";
						mi.iconbmp = ThemeManager.getInstance().getBitmap( ( mi.icon ) );
						menuitems.add( mi );
					}
				}
			}
			catch( NumberFormatException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( NotFoundException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( IllegalArgumentException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( NoSuchFieldException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( IllegalAccessException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			XmlResourceParser xmlParser = iLoongLauncher.getInstance().getResources().getXml( RR.xml.setupmenu );
			int stringID;
			try
			{
				while( xmlParser.getEventType() != XmlResourceParser.END_DOCUMENT )
				{
					if( xmlParser.getEventType() == XmlResourceParser.START_TAG )
					{
						String s = xmlParser.getName();
						if( s.equals( TAG_SETUPMENU ) )
						{
							// int resid = xmlParser.getAttributeResourceValue(null, "id", 0);
							// String sn = xmlParser.getAttributeValue(null, "mystr");
							mrows = xmlParser.getAttributeIntValue( null , "row" , 0 );
							mcolumns = xmlParser.getAttributeIntValue( null , "columns" , 0 );
							xmlParser.next();
						}
						// tagName = xmlParser.getName();
						if( XmlResourceParser.START_TAG == xmlParser.getEventType() && xmlParser.getName().equals( "menu" ) )
						{
							PagedView.PageCount++;//zqh add ,this flag indicates the count of pages
							SetupTabMenu menu = new SetupTabMenu();
							menu.id = xmlParser.getAttributeIntValue( null , "id" , 0 );
							stringID = xmlParser.getAttributeResourceValue( null , "name" , 0 );
							menu.name = R3D.getString( stringID );
							menu.count = xmlParser.getAttributeIntValue( null , "count" , 0 );
							smenu = menu;
							mTabMenus.add( menu );
							ArrayList<SetupMenuItem> menulist = new ArrayList<SetupMenuItem>();
							mMenuItems.put( Integer.valueOf( menu.id ) , menulist );
							xmlParser.next();
							// tagName = xmlParser.getName();
							int count = 0;
							// while (count < mrows * mcolumns) {
							while( count < menu.count )
							{
								// tagName = xmlParser.getName();
								// int type = xmlParser.getEventType();
								if( XmlResourceParser.START_TAG == xmlParser.getEventType() && xmlParser.getName().equals( "item" ) )
								{
									ArrayList<SetupMenuItem> menuitems = mMenuItems.get( Integer.valueOf( smenu.id ) );
									if( menuitems != null )
									{
										SetupMenuItem mi = new SetupMenuItem();
										mi.page = smenu.id;
										mi.id = xmlParser.getAttributeIntValue( null , "id" , 0 );
										stringID = xmlParser.getAttributeResourceValue( null , "name" , 0 );
										mi.name = R3D.getString( stringID );
										mi.icon = SETUPMENU_FOLDERNAME + xmlParser.getAttributeValue( null , "icon" );
										mi.iconbmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( mi.icon ) );
										// mi.iconbmp =Tools.getImageFromInStream(SetMenuDesktop.class.getResourceAsStream(mi.icon));
										if( xmlParser.getAttributeValue( null , "icon2" ) != null )
										{
											mi.icon2 = SETUPMENU_FOLDERNAME + xmlParser.getAttributeValue( null , "icon2" );
											mi.icon2bmp = Tools.getImageFromInStream( SetMenuDesktop.class.getResourceAsStream( mi.icon2 ) );
										}
										menuitems.add( mi );
									}
									count++;
								}
								xmlParser.next();
							}
						}
						// }
					}
					else if( xmlParser.getEventType() == XmlResourceParser.END_TAG )
					{
						;
					}
					else if( xmlParser.getEventType() == XmlResourceParser.TEXT )
					{
						// String s1 = xmlParser.getText();
					}
					xmlParser.next();
				}
			}
			catch( XmlPullParserException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			xmlParser.close();
		}
	}
	
	//Jone end
	public void LoadSetupMenuXml()
	{
		SetupTabMenu smenu;
		try
		{
			mrows = iLoongLauncher.getInstance().getResources().getInteger( RR.integer.setup_menu_row );
			mcolumns = iLoongLauncher.getInstance().getResources().getInteger( RR.integer.setup_menu_columns );
			String[] menus;
			if( DefaultLayout.setupmenu_yitong )
			{
				menus = iLoongLauncher.getInstance().getResources().getStringArray( RR.array.setupMenu_eton );
			}
			else
				menus = iLoongLauncher.getInstance().getResources().getStringArray( RR.array.setupMenuAll );
			ArrayList<String> menulistall = new ArrayList<String>();
			ArrayList<String> menulistallid = new ArrayList<String>();
			for( int i = 0 ; i < menus.length ; i++ )
			{
				menulistall.add( menus[i] );
				String[] tmp = menus[i].split( "," );
				menulistallid.add( tmp[0] );
			}
			for( int i = 0 ; i < menulistall.size() ; i++ )
			{
				if( DefaultLayout.setupmenu_show_theme )
				{
					//teapotXu add start for doov special customization
					if( DefaultLayout.enable_doov_spec_customization == true )
					{
						if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_WALLPAPER )
						{
							menulistall.remove( i );
							menulistallid.remove( i );
						}
					}
					else
					{
						if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_SOFTWARE_MANAGEMENT )
						{
							menulistall.remove( i );
							menulistallid.remove( i );
						}
					}
					//teapotXu add end
				}
				else
				{
					if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_THEME )
					{
						menulistall.remove( i );
						menulistallid.remove( i );
					}
				}
			}
			for( int i = 0 ; i < menulistall.size() ; i++ )
			{
				if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_LOCKER_SETTING )
				{
					menulistall.remove( i );
					menulistallid.remove( i );
					break;
				}
			}
			//			}
			for( int i = 0 ; i < menulistall.size() ; i++ )
			{
				if( DefaultLayout.hide_desktop_setup )
				{
					if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_DESKTOP_SETTINGS )
					{
						menulistall.remove( i );
						menulistallid.remove( i );
					}
				}
			}
			if( DefaultLayout.popmenu_style == POPMENU_STYLE_ANDROID4 )
			{
				mrows = menulistall.size() - 1;
				mcolumns = 1;
			}
			SetupTabMenu menu = new SetupTabMenu();
			String[] menuAttrs = menulistall.get( 0 ).split( "," );
			menu.id = Integer.parseInt( menuAttrs[0] );
			Field f = (Field)RR.getStringClass().getDeclaredField( menuAttrs[1] );
			menu.name = R3D.getString( f.getInt( RR.getStringClass() ) );
			smenu = menu;
			mTabMenus.add( menu );
			ArrayList<SetupMenuItem> menulist = new ArrayList<SetupMenuItem>();
			mMenuItems.put( Integer.valueOf( menu.id ) , menulist );
			for( int i = 1 ; i < menulistall.size() ; i++ )
			{
				ArrayList<SetupMenuItem> menuitems = mMenuItems.get( Integer.valueOf( smenu.id ) );
				if( menuitems != null )
				{
					String[] tmp = menulistall.get( i ).split( "," );
					SetupMenuItem mi = new SetupMenuItem();
					mi.page = smenu.id;
					mi.id = Integer.parseInt( tmp[0] );
					Field tmpF = (Field)RR.getStringClass().getDeclaredField( tmp[1] );
					if( DefaultLayout.appbar_widgets_special_name == true )
					{
						if( tmp[1] != null && tmp[0] != null && ( tmp[0].equals( "1001" ) ) && ( tmp[1].equals( "system_widget" ) ) )
						{
							tmp[1] = "appbar_tab_widget_ex";
							tmpF = (Field)RR.getStringClass().getDeclaredField( tmp[1] );
						}
					}
					mi.name = R3D.getString( tmpF.getInt( RR.getStringClass() ) );
					mi.icon = SETUPMENU_FOLDERNAME + tmp[2] + ".png";
					mi.iconbmp = ThemeManager.getInstance().getBitmap( ( mi.icon ) );
					menuitems.add( mi );
				}
			}
		}
		catch( NumberFormatException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( NotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( IllegalArgumentException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( NoSuchFieldException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( IllegalAccessException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		XmlResourceParser xmlParser = iLoongLauncher.getInstance().getResources().getXml(RR.xml.setupmenu);
		//		
		//			try {
		//				while (xmlParser.getEventType() != XmlResourceParser.END_DOCUMENT) {
		//				     if (xmlParser.getEventType() == XmlResourceParser.START_TAG) { 
		//				          String s = xmlParser.getName(); 
		//				          if (s.equals(TAG_SETUPMENU)) { 
		////				               int resid = xmlParser.getAttributeResourceValue(null, "id", 0); 
		////				               String sn = xmlParser.getAttributeValue(null, "mystr");
		//				               
		//				               mrows = xmlParser.getAttributeIntValue(null, "row", 0);
		//				               mcolumns = xmlParser.getAttributeIntValue(null, "columns", 0);
		//				               
		//				               xmlParser.next();
		//				               
		////				               tagName = xmlParser.getName();
		//				               
		//				               if(XmlResourceParser.START_TAG == xmlParser.getEventType()
		//				                        && xmlParser.getName().equals("menu"))
		//				 
		//				                {
		//				            	   SetupTabMenu menu = new SetupTabMenu();
		//					   				menu.id = xmlParser.getAttributeIntValue(null, "id", 0);
		//					   				stringID = xmlParser.getAttributeResourceValue(null, "name", 0);
		//					   				menu.name = R3D.getString(stringID);
		//					   				smenu = menu;
		//					   				mTabMenus.add(menu);
		//					   				ArrayList<SetupMenuItem> menulist = new ArrayList<SetupMenuItem>();
		//					   				mMenuItems.put(Integer.valueOf(menu.id), menulist);
		//					   				
		//					   				xmlParser.next();
		////					   				tagName = xmlParser.getName();
		//					   				
		//					   				int count = 0;
		//					   				
		//					   				while (count < mrows * mcolumns) {
		////					   					tagName = xmlParser.getName();
		////					   					int type = xmlParser.getEventType();
		//					   					if(XmlResourceParser.START_TAG == xmlParser.getEventType()
		//						                        && xmlParser.getName().equals("item")) {
		//					   						ArrayList<SetupMenuItem> menuitems = mMenuItems.get(Integer.valueOf(smenu.id));
		//
		//					   						if (menuitems != null) {
		//
		//					   							SetupMenuItem mi = new SetupMenuItem();
		//					   							mi.page = smenu.id;
		//					   							mi.id = xmlParser.getAttributeIntValue(null, "id", 0);
		//					   							stringID = xmlParser.getAttributeResourceValue(null, "name", 0);
		//					   							mi.name = R3D.getString(stringID);
		//				
		//					   							mi.icon = SETUPMENU_FOLDERNAME + xmlParser.getAttributeValue(null, "icon");
		//					   							mi.iconbmp = Tools.getImageFromInStream(SetMenuDesktop.class.getResourceAsStream(mi.icon));
		//					   							if (xmlParser.getAttributeValue(null, "icon2") != null) {
		//					   								mi.icon2 = SETUPMENU_FOLDERNAME + xmlParser.getAttributeValue(null, "icon2");
		//					   								mi.icon2bmp = Tools.getImageFromInStream(SetMenuDesktop.class.getResourceAsStream(mi.icon2));
		//					   							}
		//					   							menuitems.add(mi);
		//					   						}
		//					   						
		//					   						count++;
		//					   					}
		//					   					
		//					   					xmlParser.next();
		//					   				}
		//				                }
		//				          }
		//				     } else if (xmlParser.getEventType() == XmlResourceParser.END_TAG) { 
		//				          ; 
		//				     } else if (xmlParser.getEventType() == XmlResourceParser.TEXT) { 
		////				          String s1 = xmlParser.getText();
		//				     } 
		//				     xmlParser.next();
		//				}
		//			} catch (XmlPullParserException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			} catch (IOException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			} 
		//			xmlParser.close();
	}
	
	public void CloseMenu()
	{
		if( RR.net_version )
		{
			if( SetupMenuControl.closeMenuOnClick )
			{
				Desktop3DListener.setCoverVisible( false );
				SetupMenuControl.closeMenuOnClick = false;
			}
		}
		mSetupMenuLanyout.setFocusable( false );
		mSetupMenuLanyout.setFocusableInTouchMode( false );
		mSetupMenuLanyout.setVisibility( View.INVISIBLE );
		mOpen = false;
		SendMsgToAndroid.sendShowWorkspaceMsg();
	}
	
	public void closeWithAnimal()
	{
		mSetupMenuLanyout.OnUnLoad();
	}
	
	public void PopSetupMenu(/*final View anchor*/)
	{
		if( RR.net_version )
		{
			showScreenCover();
		}
		if( mOpen )
			return;
		SendMsgToAndroid.sendHideWorkspaceMsg();
		mOpen = true;
		mSetupMenuLanyout.setVisibility( View.VISIBLE );
		mSetupMenuLanyout.setFocusable( true );
		mSetupMenuLanyout.setFocusableInTouchMode( true );
		mSetupMenuLanyout.requestFocus();
		mSetupMenuLanyout.Load();
		//		p = new PopupWindow(mSetupMenuLanyout, mWidth, mHeight, true);
		//		p.showAtLocation(anchor, Gravity.BOTTOM | Gravity.CENTER, 0, 0);
		//		p.setOnDismissListener(new PopupWindow.OnDismissListener() {
		//			public void onDismiss() {
		//				dismissSetupMenu(anchor);
		//			}
		//		});
		//		anchor.setTag(p);
		//		mSetupMenuLanyout.Load();
	}
	
	//	public void dismissSetupMenu(final View v) {
	//		if (v != null) {
	//			final PopupWindow window = (PopupWindow) v.getTag();
	//			if (window != null) {
	//				window.setOnDismissListener(new PopupWindow.OnDismissListener() {
	//					public void onDismiss() {
	//						window.setOnDismissListener(null);
	//					}
	//				});
	//				window.dismiss();
	//			}
	//			v.setTag(null);
	//		}
	//		mOpen = false;
	//	}
	class SetupTabMenu
	{
		
		int id;
		int page;
		String name;
		int count;
		
		public int getID()
		{
			return id;
		}
	}
	
	public class SetupMenuItem
	{
		
		public int page;
		public int id;
		public String name;
		public String icon;
		public Bitmap iconbmp;
		public String icon2;
		public Bitmap icon2bmp;
	}
	
	class SetupMenuHandler extends DefaultHandler
	{
		
		SetupTabMenu smenu;
		
		public SetupMenuHandler()
		{
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
			if( localName.equals( TAG_SETUPMENU ) )
			{
				mrows = Integer.valueOf( atts.getValue( "row" ) );
				mcolumns = Integer.valueOf( atts.getValue( "columns" ) );
			}
			else if( localName.equals( TAG_MENU ) )
			{
				SetupTabMenu menu = new SetupTabMenu();
				menu.id = Integer.valueOf( atts.getValue( "id" ) );
				menu.name = atts.getValue( "name" );
				smenu = menu;
				mTabMenus.add( menu );
				ArrayList<SetupMenuItem> menulist = new ArrayList<SetupMenuItem>();
				mMenuItems.put( Integer.valueOf( menu.id ) , menulist );
			}
			else if( localName.equals( TAG_ITEM ) )
			{
				ArrayList<SetupMenuItem> menuitems = mMenuItems.get( Integer.valueOf( smenu.id ) );
				if( menuitems != null )
				{
					SetupMenuItem mi = new SetupMenuItem();
					mi.page = smenu.id;
					mi.id = Integer.valueOf( atts.getValue( "id" ) );
					mi.name = atts.getValue( "name" );
					mi.icon = SETUPMENU_FOLDERNAME + atts.getValue( "icon" );
					try
					{
						if( DefaultLayout.getInstance().isCustomAssetsFileExist( "/" + mi.icon ) )
						{
							mi.iconbmp = BitmapFactory.decodeFile( DefaultLayout.custom_assets_path + "/" + mi.icon );
						}
						else
						{
							mi.iconbmp = ThemeManager.getInstance().getBitmap( mi.icon );
						}
						//mi.iconbmp = Tools.getImageFromInStream(iLoongLauncher.getInstance().getAssets().open(mi.icon));
					}
					catch( Exception e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if( atts.getValue( "icon2" ) != null )
					{
						mi.icon2 = SETUPMENU_FOLDERNAME + atts.getValue( "icon2" );
						try
						{
							if( DefaultLayout.getInstance().isCustomAssetsFileExist( "/" + mi.icon2 ) )
							{
								mi.icon2bmp = BitmapFactory.decodeFile( DefaultLayout.custom_assets_path + "/" + mi.icon2 );
							}
							else
							{
								mi.icon2bmp = ThemeManager.getInstance().getBitmap( mi.icon2 );
							}
							//							mi.icon2bmp = Tools.getImageFromInStream(iLoongLauncher.getInstance().getAssets().open(mi.icon2));
						}
						catch( Exception e )
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					menuitems.add( mi );
				}
			}
		}
		
		public void endElement(
				String namespaceURI ,
				String localName ,
				String qName ) throws SAXException
		{
			if( localName.equals( TAG_MENU ) )
			{
				smenu = null;
			}
			else if( localName.equals( TAG_ITEM ) )
			{
			}
		}
		
		public void characters(
				char ch[] ,
				int start ,
				int length )
		{
		}
	}
	
	public static String phoneinfo()
	{
		TelephonyManager phoneMgr = (TelephonyManager)iLoongLauncher.getInstance().getSystemService( Context.TELEPHONY_SERVICE );
		StringBuffer info = new StringBuffer();
		info.append( "PHONE:" );
		info.append( phoneMgr != null ? phoneMgr.getLine1Number() : "" );// 本机电话号码
		info.append( ";SDK:" );
		info.append( Build.VERSION.SDK );// SDK版本号
		info.append( ";RELEASE:" );
		info.append( Build.VERSION.RELEASE );// Firmware/OS 版本号
		info.append( ";BOARD:" );
		info.append( Build.BOARD );
		info.append( ";BOOTLOADER:" );
		info.append( Build.BOOTLOADER );
		info.append( ";BRAND:" );
		info.append( Build.BRAND );
		info.append( ";CPU_ABI:" );
		info.append( Build.CPU_ABI );
		info.append( ";CPU_ABI2:" );
		info.append( Build.CPU_ABI2 );
		info.append( ";DEVICE:" );
		info.append( Build.DEVICE );
		info.append( ";DISPLAY:" );
		info.append( Build.DISPLAY );
		info.append( ";FINGERPRINT:" );
		info.append( Build.FINGERPRINT );
		info.append( ";HARDWARE:" );
		info.append( Build.HARDWARE );
		info.append( ";HOST:" );
		info.append( Build.HOST );
		info.append( ";ID:" );
		info.append( Build.ID );
		info.append( ";MANUFACTURER:" );
		info.append( Build.MANUFACTURER );
		info.append( ";MODEL:" );
		info.append( Build.MODEL );
		info.append( ";PRODUCT:" );
		info.append( Build.PRODUCT );
		info.append( ";RADIO:" );
		info.append( Build.RADIO );
		//		info.append(";SERIAL:");
		//		info.append(Build.SERIAL);
		info.append( ";TAGS:" );
		info.append( Build.TAGS );
		info.append( ";TIME:" );
		info.append( Build.TIME );
		info.append( ";TYPE:" );
		info.append( Build.TYPE );
		info.append( ";USER:" );
		info.append( Build.USER );
		info.append( ";" );
		return info.toString();
	}
	
	public static void DialogMessage(
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
}
