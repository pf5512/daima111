package com.iLoong.launcher.Widget3D;


import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.miui.MIUIWidgetList;
import com.iLoong.launcher.theme.ThemeManager;


public class Contact3DShortcut extends Widget3DShortcut
{
	
	//	Icon3D icon;
	//	Bitmap bmp;
	private ShortcutInfo info;
	float Contact3DWidth;
	public static TextureRegion contactPreviewRegion = null;
	
	public Contact3DShortcut(
			String name )
	{
		super( name );
		//if(!AppHost3D.V2)addShortcut();
		canUninstall = false;
		packageName = "com.iLoong.widget.contact";
		//		resumeImage = new ImageView3D("resumeicon",Icon3D.resumeTexture,R3D.workspace_multicon_width,R3D.workspace_multicon_height); 
		//		hideImage = new ImageView3D("hideicon",Icon3D.hideTexture,R3D.workspace_multicon_width,R3D.workspace_multicon_height); 
		isHideWidget = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getBoolean( "HIDE:" + packageName , false );
		bg = R3D.findRegion( "widget-shortcut-bg" );
		scale = SetupMenu.mScale;
		//		Contact3DWidth =  iLoongApplication.getInstance()
		//				.getResources().getDimension(RR.dimen.app_icon_size);
		//		Contact3DWidth=Contact3DWidth*Root3D.scaleFactor;
	}
	
	public void onThemeChanged()
	{
		Bitmap bmp1 = ThemeManager.getInstance().getCurrentThemeBitmap( "theme/iconbg/contactperson-icon.png" );//ThemeManager.getInstance().getBitmap( "theme/iconbg/contactperson-icon.png" );
		Bitmap bmp = null;
		if( bmp1 == null )
		{
			bmp1 = ThemeManager.getInstance().getBitmap( "theme/iconbg/contactperson-icon.png" );
			if( Icon3D.getIconBg() != null )
			{
				if( bmp1.getWidth() != R3D.sidebar_widget_w * DefaultLayout.thirdapk_icon_scaleFactor )
				{
					bmp = Tools.resizeBitmap( bmp1 , (int)( R3D.sidebar_widget_w * DefaultLayout.thirdapk_icon_scaleFactor ) , (int)( R3D.sidebar_widget_w * DefaultLayout.thirdapk_icon_scaleFactor ) );
				}
				else
				{
					bmp = Bitmap.createScaledBitmap( bmp1 , bmp1.getWidth() , bmp1.getWidth() , true );
				}
			}
			else
			{
				bmp = Bitmap.createScaledBitmap( bmp1 , R3D.sidebar_widget_w , R3D.sidebar_widget_h , true );
			}
		}
		else
		{
			bmp = Bitmap.createScaledBitmap( bmp1 , R3D.sidebar_widget_w , R3D.sidebar_widget_h , true );
		}
		R3D.repack( R3D.contact_name , Utils3D.IconToPixmap3D( bmp , R3D.contact_name , null , Icon3D.titleBg ) );
		if( preview != null )
			preview.recycle();
		preview = ThemeManager.getInstance().getBitmap( "theme/iconbg/contactperson-icon.png" );
		if( contactPreviewRegion == null )
		{
			contactPreviewRegion = new TextureRegion( new BitmapTexture( preview , true ) );
			contactPreviewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
		else
		{
			( (BitmapTexture)contactPreviewRegion.getTexture() ).changeBitmap( preview , true );
			contactPreviewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
		previewRegion = contactPreviewRegion;
		if( bmp1 != null && !bmp1.isRecycled() )
		{
			bmp1.recycle();
		}
	}
	
	public void makeShortcut()
	{
		if( titleRegion == null || cellRegion == null || contactPreviewRegion == null )
		{
			title = R3D.contact_name;
			int[] span = { 1 , 1 };
			cellTitle = span[0] + "x" + span[1];
			int titleWidth = (int)( width * 3 / 4 );
			int alignH = AppBar3D.TEXT_ALIGN_LEFT;
			int titleHeight = (int)( R3D.widget_preview_title_weight * height );
			if( !RR.net_version && DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_shown_in_workspace_edit_mode == true )
			{
				titleWidth = (int)( width );
				alignH = AppBar3D.TEXT_ALIGN_CENTER;
				titleHeight = MIUIWidgetList.getSingleLineHeight();
			}
			titleRegion = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( title , titleWidth , titleHeight , alignH , AppBar3D.TEXT_ALIGN_CENTER , true , Color.WHITE ) ) );
			cellRegion[0] = R3D.findRegion( span[0] + "" );
			cellRegion[1] = R3D.findRegion( span[1] + "" );
			if( DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_shown_in_workspace_edit_mode == true )
			{
				if( !RR.net_version )
					miuiCellRegion = R3D.findRegion( "miui-widget-indicator" + span[0] + span[1] );
			}
			preview = ThemeManager.getInstance().getBitmap( "theme/iconbg/contactperson-icon.png" );
			//teapotXu add start
			if( !( DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_shown_in_workspace_edit_mode == true ) )
			{
				preview = Desktop3DListener.getWidgetPreview( preview , 1 , 1 );
			}
			//teapotXu add end
			contactPreviewRegion = new TextureRegion( new BitmapTexture( preview , true ) );
			//			float realWidth=previewRegion.getRegionWidth();
			//			scale=Contact3DWidth/realWidth;
			if( DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_shown_in_workspace_edit_mode == true )
			{
				float Contact3DWidth = iLoongApplication.getInstance().getResources().getDimension( RR.dimen.app_icon_size );
				//Contact3DWidth=Contact3DWidth*SetupMenu.mScale;
				float realWidth = contactPreviewRegion.getRegionWidth();
				scale = Contact3DWidth / realWidth;
				contactPreviewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
			}
		}
		previewRegion = contactPreviewRegion;
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
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( !RR.net_version && DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_shown_in_workspace_edit_mode == true )
		{
			if( contactPreviewRegion != null )
			{
				float Contact3DWidth = iLoongApplication.getInstance().getResources().getDimension( RR.dimen.app_icon_size );
				// Contact3DWidth=Contact3DWidth*SetupMenu.mScale;
				float realWidth = contactPreviewRegion.getRegionWidth();
				scale = Contact3DWidth / realWidth;
			}
		}
		super.draw( batch , parentAlpha );
	}
	
	@Override
	public void releaseRegion()
	{
		// TODO Auto-generated method stub
		Log.v( "" , "releaseRegion name is Contact3DShortcut" );
		super.releaseRegion();
		if( contactPreviewRegion != null && contactPreviewRegion.getTexture() != null )
		{
			contactPreviewRegion.getTexture().dispose();
			contactPreviewRegion = null;
		}
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		return true;
	}
}
