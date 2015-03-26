package com.iLoong.launcher.Widget3D;


import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class Contact3DShortcut extends Widget3DShortcut
{
	
	Icon3D icon;
	Bitmap bmp;
	private ShortcutInfo info;
	
	public Contact3DShortcut(
			String name )
	{
		super( name );
		//if(!AppHost3D.V2)addShortcut();
		canUninstall = false;
		packageName = "com.iLoong.widget.contact";
		isHideWidget = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getBoolean( "HIDE:" + packageName , false );
		if( DefaultLayout.show_widget_shortcut_bg )
		{
			bg = R3D.findRegion( "widget-shortcut-bg" );
		}
		scale = SetupMenu.mScale;
	}
	
	//	@Override
	//	public void addShortcut() {
	//		info = addContactInfo();
	//		FileHandle file = ThemeManager.getInstance().getGdxTextureResource("theme/iconbg/contactperson-icon.png");
	//		Bitmap bmp1 = BitmapFactory.decodeStream(file.read());
	//		Bitmap bmp = Bitmap.createScaledBitmap(bmp1, R3D.sidebar_widget_w, R3D.sidebar_widget_h, true);
	//		icon = new Icon3D(R3D.contact_name, bmp,R3D.contact_name);
	//		icon.setItemInfo(info);
	//		addView(icon);
	//		setSize(icon.width,icon.height);
	//		setOrigin(width/2, height/2);
	//		bmp1.recycle();
	//		bmp.recycle();
	//	}
	public void makeShortcut()
	{
		if( titleRegion == null || cellRegion == null || previewRegion == null )
		{
			title = R3D.contact_name;
			int[] span = { 1 , 1 };
			cellTitle = span[0] + "x" + span[1];
			titleRegion = new TextureRegion( new Texture3D( AppBar3D.titleToPixmap(
					title ,
					(int)( width * 3 / 4 ) ,
					(int)( R3D.widget_preview_title_weight * height ) ,
					AppBar3D.TEXT_ALIGN_LEFT ,
					AppBar3D.TEXT_ALIGN_CENTER ,
					true ,
					Color.WHITE ) ) );
			cellRegion[0] = R3D.findRegion( span[0] + "" );
			cellRegion[1] = R3D.findRegion( span[1] + "" );
			//			preview = ThemeManager.getInstance().getBitmap( "theme/iconbg/contactperson-icon.png" );
			//			previewRegion = new TextureRegion( new Texture3D( preview ) );
		}
		if( info == null )
			info = addContactInfo();
	}
	
	static String CONTACT_ACTION = Intent.ACTION_CREATE_SHORTCUT;// Intent.ACTION_PICK
	static String CONTACT_TYPE = "vnd.android.cursor.dir/contact";
	public static int CONTACT_NONE = 0;
	public static int CONTACT_DEFAULT = 1;
	public static int CONTACT_NO_ICON = 2;
	
	public static int isAContactShortcut(
			Intent intent )
	{
		if( intent == null )
			return CONTACT_NONE;
		if( intent.getAction().equals( CONTACT_ACTION ) )
			return CONTACT_DEFAULT;
		else if( intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
			return CONTACT_NO_ICON;
		return CONTACT_NONE;
	}
	
	public static ShortcutInfo addContactInfo()
	{
		ShortcutInfo info;
		info = new ShortcutInfo();
		info.title = R3D.contact_name;
		//		Uri uri = Uri.parse("content://contacts/people");
		//		Intent localIntent1 = new Intent(CONTACT_ACTION);
		//		localIntent1.setType(CONTACT_TYPE);
		Intent localIntent1 = new Intent( CONTACT_ACTION );
		localIntent1.setComponent( new ComponentName( "com.android.contacts" , "com.android.contacts.ContactShortcut" ) );
		info.intent = localIntent1;
		return info;
	}
	
	@Override
	public View3D getWidget3D()
	{
		//		return icon.clone();
		Icon3D icon2;
		if( info == null )
			info = addContactInfo();
		ShortcutInfo info = new ShortcutInfo( this.info );
		//		info.title = R3D.contact_name;
		//		info.intent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
		//		info.intent.setComponent(new ComponentName("com.android.contacts","com.android.contacts.ContactShortcut"));
		////		FileHandle file = ThemeManager.getInstance().getGdxTextureResource("theme/iconbg/contactperson-icon.png");
		////		Bitmap bmp1 = BitmapFactory.decodeStream(file.read());
		////		this.bmp = Bitmap.createScaledBitmap(bmp1, R3D.sidebar_widget_w, R3D.sidebar_widget_h, true);
		icon2 = new Icon3D( R3D.contact_name , R3D.findRegion( R3D.contact_name ) );
		icon2.setItemInfo( info );
		icon2.setPosition( longClickX - icon2.width / 2 , longClickY - icon2.height / 2 );
		return (View3D)icon2;
	}
}
