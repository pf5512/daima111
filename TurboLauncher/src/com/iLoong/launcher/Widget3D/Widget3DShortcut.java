package com.iLoong.launcher.Widget3D;


import java.io.InputStream;
import java.util.Locale;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.AppList3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.DesktopEdit.DesktopEditHost;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3DHost.Widget3DProvider;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.miui.MIUIWidgetList;
import com.iLoong.launcher.theme.ThemeManager;


public class Widget3DShortcut extends ViewGroup3D
{
	
	public ResolveInfo resolve_info;
	private ApplicationInfo app_info;
	protected AndroidFiles files;
	//	private Icon3D icon;
	// private int clingState = ClingManager.CLING_STATE_WAIT;
	public String title;
	public String cellTitle;
	public Bitmap preview;
	public TextureRegion titleRegion;
	public TextureRegion[] cellRegion = new TextureRegion[2];
	public TextureRegion previewRegion;
	public String packageName = "";
	public boolean uninstall = false;
	public boolean canUninstall = false;
	public boolean hide = false;
	public boolean isHideWidget = false;
	protected float longClickX = 0;
	protected float longClickY = 0;
	//	ImageView3D uninstallImage;
	//	ImageView3D resumeImage;
	//	ImageView3D hideImage;
	TextureRegion bg;
	public boolean oldVisible = false;
	public int oldAppGridIndex;
	public int newAppGridIndex;
	public float oldX;
	public float oldY;
	public boolean inited = false;
	protected float scale = 1;
	public static final int MSG_WIDGET3D_SHORTCUT_LONGCLICK = 0;
	public static final int MSG_WIDGET3D_SHORTCUT_CLICK = 1;
	protected boolean widget_shown_in_workspace_edit_mode = false;
	public TextureRegion miuiCellRegion;
	
	public Widget3DShortcut(
			String name )
	{
		super( name );
		resolve_info = null;
		app_info = null;
		files = null;
	}
	
	public boolean getWidget3DShortcutShownPlace()
	{
		return widget_shown_in_workspace_edit_mode;
	}
	
	public void setWidget3DShortcutShownPlace(
			boolean is_shown_worksapce_edit_mode )
	{
		widget_shown_in_workspace_edit_mode = is_shown_worksapce_edit_mode;
	}
	
	public Widget3DShortcut(
			String name ,
			ResolveInfo resolve_info )
	{
		super( name );
		app_info = new ApplicationInfo( resolve_info , iLoongApplication.mIconCache );
		this.resolve_info = resolve_info;
		try
		{
			Context widgetContext = iLoongApplication.getInstance().createPackageContext( resolve_info.activityInfo.packageName , Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY );
			files = new AndroidFiles( widgetContext.getAssets() );
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		// if (!AppHost3D.V2)
		// addShortcut();
		//		uninstallImage = new ImageView3D("uninstallicon",
		//				Icon3D.uninstallTexture, R3D.workspace_multicon_width,
		//				R3D.workspace_multicon_height);
		//		resumeImage = new ImageView3D("resumeicon", Icon3D.resumeTexture,
		//				R3D.workspace_multicon_width, R3D.workspace_multicon_height);
		//		hideImage = new ImageView3D("hideicon", Icon3D.hideTexture,
		//				R3D.workspace_multicon_width, R3D.workspace_multicon_height);
		// clingState = ClingManager.getInstance().fireWidgetCling(this);
		bg = R3D.findRegion( "widget-shortcut-bg" );
		packageName = resolve_info.activityInfo.packageName;
		isHideWidget = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getBoolean( "HIDE:" + packageName , false );
	}
	
	public Widget3DShortcut clone()
	{
		return new Widget3DShortcut( name , resolve_info );
	}
	
	// public void addShortcut() {
	// ShortcutInfo info = app_info.makeShortcut();
	// // Icon3D icon = new Icon3D(info.title.toString(),
	// // info.getIcon(iLoongApplication.mIconCache),
	// // info.title.toString());
	// Bitmap bitmap = null;
	// InputStream stream = null;
	// Bitmap iconBitmap = null;
	// try {
	// Context widgetPluginContext = iLoongApplication.ctx
	// .createPackageContext(
	// resolve_info.activityInfo.packageName,
	// Context.CONTEXT_INCLUDE_CODE
	// | Context.CONTEXT_IGNORE_SECURITY);
	// stream = widgetPluginContext.getAssets().open("widget_ico.png");
	// bitmap = BitmapFactory.decodeStream(stream);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// try {
	// if (stream != null) {
	// stream.close();
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// if (bitmap != null) {
	// iconBitmap = Bitmap.createScaledBitmap(bitmap,
	// R3D.sidebar_widget_w, R3D.sidebar_widget_h, true);
	// bitmap.recycle();
	// } else {
	// iconBitmap = info.getIcon(iLoongApplication.mIconCache);
	// }
	//
	// // Pixmap iconPixmap =
	// // Utils3D.IconToPixmap3D(iconBitmap,info.title.toString(), null, null);
	// iconBitmap.recycle();
	//
	// icon = new Icon3D(info.title.toString(), new Texture3D(iconBitmap));
	// icon.setItemInfo(info);
	// addView(icon);
	// setSize(icon.width, icon.height);
	// setOrigin(width / 2, height / 2);
	// }
	@Override
	public void setSize(
			float w ,
			float h )
	{
		super.setSize( w , h );
		this.setOrigin( w / 2 , h / 2 );
	}
	
	public void makeShortcut()
	{
		if( titleRegion == null || cellRegion == null || previewRegion == null )
		{
			packageName = resolve_info.activityInfo.packageName;
			title = iLoongApplication.mIconCache.getLabel( resolve_info );
			Widget3DProvider provider = Widget3DManager.getInstance().getWidget3DProvider( packageName );
			cellTitle = provider.spanX + "x" + provider.spanY;
			int titleWidth = (int)( width * 3 / 4 );
			int alignH = AppBar3D.TEXT_ALIGN_LEFT;
			int titleHeight = (int)( R3D.widget_preview_title_weight * height );
			if( !RR.net_version && DefaultLayout.enable_workspace_miui_edit_mode == true && widget_shown_in_workspace_edit_mode )
			{
				titleWidth = (int)( width );
				alignH = AppBar3D.TEXT_ALIGN_CENTER;
				titleHeight = MIUIWidgetList.getSingleLineHeight();
			}
			titleRegion = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( title , titleWidth , titleHeight , alignH , AppBar3D.TEXT_ALIGN_CENTER , true , Color.WHITE ) ) );
			cellRegion[0] = R3D.findRegion( provider.spanX + "" );
			cellRegion[1] = R3D.findRegion( provider.spanY + "" );
			if( DefaultLayout.enable_workspace_miui_edit_mode == true && widget_shown_in_workspace_edit_mode )
			{
				if( !RR.net_version )
					miuiCellRegion = R3D.findRegion( "miui-widget-indicator" + provider.spanX + provider.spanY );
			}
			preview = null;
			InputStream stream = null;
			try
			{
				Context widgetPluginContext = iLoongApplication.getInstance().createPackageContext(
						resolve_info.activityInfo.packageName ,
						Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY );
				Bundle metaData = resolve_info.activityInfo.applicationInfo.metaData;
				boolean useTheme = false;
				if( metaData != null )
				{
					useTheme = metaData.getBoolean( "useTheme" , false );
				}
				String iconPath = "widget_ico.png";
				if( useTheme )
				{
					String widgetThemeName = ThemeManager.getInstance().getCurrentThemeDescription().widgettheme;
					if( widgetThemeName == null || widgetThemeName.trim().equals( "" ) )
					{
						widgetThemeName = Widget3DManager.getInstance().getWidget3DTheme(
								resolve_info.activityInfo.packageName ,
								ThemeManager.getInstance().getCurrentThemeDescription().componentName.getPackageName() );
					}
					// 检查主题在Widget中是否存�?
					// if (!widgetThemeName.equals("iLoong")) {
					// String widgetName = packageName.substring(
					// packageName.lastIndexOf(".") + 1).toLowerCase();
					// widgetThemeName = Widget3DManager.getInstance()
					// .checkThemeExist(ThemeManager.getInstance().getCurrentThemeContext(),
					// widgetName, widgetThemeName);
					// }
					iconPath = widgetThemeName + "/image/widget_ico.png";
				}
				// teapotXu_20130228: add start
				// modified for: get resource from themeBox of Launcher
				String widgetShortName = packageName.substring( packageName.lastIndexOf( "." ) + 1 ).toLowerCase( Locale.ENGLISH );
				String final_wdgrespath = "theme/widget/" + widgetShortName + "/" + iconPath;
				String final_theme_wdgrespath = "theme/widget/" + packageName + "/" + iconPath;
				try
				{
					stream = ThemeManager.getInstance().getCurrentThemeInputStream( final_theme_wdgrespath );
					if( stream == null )
						stream = ThemeManager.getInstance().getCurrentThemeInputStream( final_wdgrespath );
					if( stream == null )
					{
						try
						{
							DisplayMetrics displayMetrics = ThemeManager.getInstance().getCurrentThemeDescription().getResourcesDisplayMetrics();
							String screen_density_str = "/" + displayMetrics.widthPixels + "x" + displayMetrics.heightPixels;
							String icon_name = "widget_ico.png";
							String final_res_path;
							if( DefaultLayout.enable_workspace_miui_edit_mode && widget_shown_in_workspace_edit_mode )
							{
								icon_name = "menu_ico.png";
							}
							final_res_path = ThemeManager.getInstance().getCurrentThemeDescription().widgettheme + "/image" + screen_density_str + "/" + icon_name;
							Log.v( "cooee" , "Widget3DShortcut ---- makeshortcut ---- widget icon final_res_path: " + final_res_path );
							stream = widgetPluginContext.getAssets().open( final_res_path );
						}
						catch( Exception e )
						{
							//e.printStackTrace();
						}
						if( stream == null )
						{
							stream = widgetPluginContext.getAssets().open( iconPath );
						}
					}
				}
				catch( Exception e )
				{
					// stream = widgetPluginContext.getAssets().open(iconPath);
				}
				if( stream == null )
				{
					iconPath = "iLoong" + "/image/widget_ico.png";
					final_wdgrespath = "theme/widget/" + widgetShortName + "/" + iconPath;
					stream = ThemeManager.getInstance().getCurrentThemeInputStream( final_wdgrespath );
					if( stream == null )
					{
						stream = widgetPluginContext.getAssets().open( iconPath );
					}
				}
				// before:
				// stream = widgetPluginContext.getAssets().open(iconPath);
				// teapotXu_20130228: add end
				preview = ThemeManager.getInstance().getBitmap( stream );
			}
			catch( Exception e )
			{
			}
			finally
			{
				try
				{
					if( stream != null )
					{
						stream.close();
					}
				}
				catch( Exception e )
				{
				}
			}
			if( preview == null )
			{
				preview = iLoongApplication.mIconCache.getIcon( new ComponentName( resolve_info.activityInfo.applicationInfo.packageName , resolve_info.activityInfo.name ) , resolve_info );
			}
			if( provider.spanX != -1 && provider.spanX != -1 )
			{
				if( DefaultLayout.enable_workspace_miui_edit_mode && widget_shown_in_workspace_edit_mode )
				{
					preview = Desktop3DListener.getWidgetPreviewWorkspaceEditMode( preview , provider.spanX , provider.spanY );
				}
				else
				{
					preview = Desktop3DListener.getWidgetPreview( preview , provider.spanX , provider.spanY );
				}
			}
			previewRegion = new TextureRegion( new BitmapTexture( preview , true ) );
			previewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
	}
	
	public ResolveInfo getResolveInfo()
	{
		return this.resolve_info;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( debug && debugTexture != null && parent != null )
			batch.draw(
					debugTexture ,
					x ,
					y ,
					originX ,
					originY ,
					width == 0 ? 200 : width ,
					height == 0 ? 200 : height ,
					scaleX ,
					scaleY ,
					rotation ,
					0 ,
					0 ,
					debugTexture.getWidth() ,
					debugTexture.getHeight() ,
					false ,
					false );
		if( transform )
			applyTransform( batch );
		float alpha = color.a;
		if( isHideWidget && ( uninstall || hide ) )
		{
			alpha *= 0.2;
		}
		batch.setColor( color.r , color.g , color.b , alpha * parentAlpha );
		this.applyTransformChild( batch );
		if( previewRegion != null )
		{
			if( DefaultLayout.show_widget_shortcut_bg )
			{
				if( bg != null )
				{
					if( DefaultLayout.widget_shortcut_title_top )
					{
						batch.draw( bg , x , y , width , height * ( 1 - R3D.widget_preview_title_weight ) );
					}
					else
					{
						batch.draw( bg , x , y + height * R3D.widget_preview_title_weight , width , height * ( 1 - R3D.widget_preview_title_weight ) );
					}
				}
			}
			int drawWidth = (int)( previewRegion.getRegionWidth() * scale );
			// if (drawWidth<R3D.workspace_cell_width)
			// {
			// drawWidth=R3D.workspace_cell_width;
			// }
			int drawHeight = (int)( previewRegion.getRegionHeight() * scale );
			// if (drawHeight<R3D.workspace_cell_width)
			// {
			// drawHeight=R3D.workspace_cell_width;
			// }
			float previewX = width / 2 - drawWidth / 2;
			float previewY = height * ( 1 - R3D.widget_preview_title_weight ) / 2 - drawHeight / 2 + height * R3D.widget_preview_title_weight;
			if( DefaultLayout.enable_workspace_miui_edit_mode == true && widget_shown_in_workspace_edit_mode )
			{
				previewY = R3D.miui_widget_indicator_height + MIUIWidgetList.getSingleLineHeight();
				previewY += ( height - previewY - drawHeight ) / 2;
			}
			if( DefaultLayout.widget_shortcut_title_top )
			{
				previewY = previewY - height * R3D.widget_preview_title_weight;
			}
			if( DefaultLayout.widget_shortcut_lefttop )
			{
				previewX = width / 20;
				previewY = height - height * ( 1 - R3D.widget_preview_title_weight ) / 20 - previewRegion.getRegionHeight() * scale;
				if( DefaultLayout.widget_shortcut_title_top )
				{
					previewY = previewY - height * R3D.widget_preview_title_weight;
				}
			}
			batch.draw( previewRegion , previewX + x , previewY + y , drawWidth , drawHeight );
		}
		if( titleRegion != null )
		{
			if( DefaultLayout.widget_shortcut_title_top )
			{
				batch.draw( titleRegion , x , y + height * ( 1 - R3D.widget_preview_title_weight ) );
			}
			else
			{
				if( DefaultLayout.enable_workspace_miui_edit_mode == true && widget_shown_in_workspace_edit_mode )
				{
					batch.draw( titleRegion , x , y + R3D.miui_widget_indicator_height , titleRegion.getRegionWidth() * 1.5f , titleRegion.getRegionHeight() * 1.5f );
				}
				else
				{
					batch.draw( titleRegion , x , y );
				}
			}
		}
		if( !RR.net_version && DefaultLayout.enable_workspace_miui_edit_mode == true && widget_shown_in_workspace_edit_mode )
		{
			if( miuiCellRegion != null )
			{
				batch.draw( miuiCellRegion , x + ( width - R3D.miui_widget_indicator_height ) / 2 , y , R3D.miui_widget_indicator_height , R3D.miui_widget_indicator_height );
			}
		}
		else
		{
			if( cellRegion[0] != null )
			{
				TextureRegion xRegion = R3D.findRegion( "x" );
				float titleX = 10;
				titleX = (float)( width / 4 - 2 * cellRegion[0].getRegionWidth() - xRegion.getRegionWidth() ) - titleX;
				if( titleX < 0 )
					titleX = 0;
				if( DefaultLayout.widget_shortcut_title_top )
				{
					batch.draw(
							cellRegion[0] ,
							width * 3 / 4 + x + titleX ,
							y + ( ( R3D.widget_preview_title_weight * height ) - cellRegion[0].getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) );
					batch.draw(
							xRegion ,
							width * 3 / 4 + x + titleX + cellRegion[0].getRegionWidth() ,
							y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) );
					batch.draw(
							cellRegion[1] ,
							width * 3 / 4 + x + titleX + cellRegion[0].getRegionWidth() + xRegion.getRegionWidth() ,
							y + ( ( R3D.widget_preview_title_weight * height ) - cellRegion[1].getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) );
				}
				else
				{
					if( DesktopEditHost.getPopMenuStyle() == 1 )
					{
						batch.draw(
								cellRegion[0] ,
								width * 3 / 4 + x + titleX - DefaultLayout.widget_preview_title_span_offsetX - 25 * ( Utils3D.getScreenWidth() / 720f ) ,
								y + ( ( R3D.widget_preview_title_weight * height ) - cellRegion[0].getRegionHeight() ) / 2 ,
								cellRegion[0].getRegionWidth() * 1.5f ,
								cellRegion[0].getRegionHeight() * 1.5f );
						batch.draw(
								xRegion ,
								width * 3 / 4 + x + titleX - DefaultLayout.widget_preview_title_span_offsetX + cellRegion[0].getRegionWidth() - 14 * ( Utils3D.getScreenWidth() / 720f ) ,
								y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 ,
								xRegion.getRegionWidth() * 1.5f ,
								xRegion.getRegionHeight() * 1.5f );
						batch.draw(
								cellRegion[1] ,
								width * 3 / 4 + x + titleX - DefaultLayout.widget_preview_title_span_offsetX + cellRegion[0].getRegionWidth() + xRegion.getRegionWidth() - 5 * ( Utils3D
										.getScreenWidth() / 720f ) ,
								y + ( ( R3D.widget_preview_title_weight * height ) - cellRegion[1].getRegionHeight() ) / 2 ,
								cellRegion[1].getRegionWidth() * 1.5f ,
								cellRegion[1].getRegionHeight() * 1.5f );
					}
					else
					{
						batch.draw(
								cellRegion[0] ,
								width * 3 / 4 + x + titleX - DefaultLayout.widget_preview_title_span_offsetX ,
								y + ( ( R3D.widget_preview_title_weight * height ) - cellRegion[0].getRegionHeight() ) / 2 ,
								cellRegion[0].getRegionWidth() ,
								cellRegion[0].getRegionHeight() );
						batch.draw(
								xRegion ,
								width * 3 / 4 + x + titleX - DefaultLayout.widget_preview_title_span_offsetX + cellRegion[0].getRegionWidth() ,
								y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 ,
								xRegion.getRegionWidth() ,
								xRegion.getRegionHeight() );
						batch.draw(
								cellRegion[1] ,
								width * 3 / 4 + x + titleX - DefaultLayout.widget_preview_title_span_offsetX + cellRegion[0].getRegionWidth() + xRegion.getRegionWidth() ,
								y + ( ( R3D.widget_preview_title_weight * height ) - cellRegion[1].getRegionHeight() ) / 2 ,
								cellRegion[1].getRegionWidth() ,
								cellRegion[1].getRegionHeight() );
					}
				}
			}
		}
		if( uninstall && canUninstall && Icon3D.uninstallTexture != null )
		{
			batch.draw(
					Icon3D.uninstallTexture ,
					this.x + this.width - R3D.workspace_multicon_width ,
					this.y + this.height - R3D.workspace_multicon_height ,
					R3D.workspace_multicon_width ,
					R3D.workspace_multicon_height );
		}
		if( hide && isHideWidget && Icon3D.resumeTexture != null )
		{
			batch.draw(
					Icon3D.resumeTexture ,
					this.x + this.width - R3D.workspace_multicon_width ,
					this.y + this.height - R3D.workspace_multicon_height ,
					R3D.workspace_multicon_width ,
					R3D.workspace_multicon_height );
		}
		if( hide && !isHideWidget && Icon3D.hideTexture != null )
		{
			batch.draw(
					Icon3D.hideTexture ,
					this.x + this.width - R3D.workspace_multicon_width ,
					this.y + this.height - R3D.workspace_multicon_height ,
					R3D.workspace_multicon_width ,
					R3D.workspace_multicon_height );
		}
		this.resetTransformChild( batch );
		drawChildren( batch , alpha * parentAlpha );
		if( transform )
			resetTransform( batch );
	}
	
	public void showUninstall()
	{
		uninstall = true;
		hide = false;
	}
	
	public void hideUninstall()
	{
		uninstall = false;
	}
	
	public void showHide()
	{
		hide = true;
		uninstall = false;
	}
	
	public void clearState()
	{
		uninstall = false;
		hide = false;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		Log.v( "Widget3DShortcut" , "onClick:x,y=" + x + " " + y );
		// if (uninstall) {
		// if (!canUninstall) {
		//
		// } else if (!packageName.equals("")) {
		// Uri packageURI = Uri.parse("package:" + packageName);
		// Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
		// // 执行卸载程序
		// iLoongLauncher.getInstance().startActivity(intent);
		// }
		// return true;
		// } else if (hide) {
		// if (!packageName.equals("")) {
		// isHideWidget = !isHideWidget;
		// PreferenceManager
		// .getDefaultSharedPreferences(
		// iLoongLauncher.getInstance()).edit()
		// .putBoolean("HIDE:" + packageName, isHideWidget)
		// .commit();
		// }
		// return true;
		// }
		if( ( DefaultLayout.enable_workspace_miui_edit_mode ) && ( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) )
		{
			Workspace3D mWorkspace3D = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
			if( mWorkspace3D.edit_mode_click_2_add_our_widget != null )
			{
				return true;
			}
		}
		viewParent.onCtrlEvent( this , MSG_WIDGET3D_SHORTCUT_CLICK );
		//SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.can_drag_to_desktop ) );
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		//		if (hide || uninstall)
		//			return true;
		//		point.x = x;
		//		point.y = y;
		//		this.toAbsolute( point );
		//		longClickX = point.x;
		//		longClickY = point.y;
		//		point.x = width / 2;
		//		point.y = height / 2;
		//		this.toAbsolute( point );
		//		this.setTag( new Vector2( point.x , point.y ) );
		//		Log.d( "launcher" , "long click:" + point.x + "," + point.y );
		//		// clingState = ClingManager.CLING_STATE_DISMISSED;
		//		// ClingManager.getInstance().cancelWidgetCling();
		//		DragLayer3D.dragStartX = longClickX;
		//		DragLayer3D.dragStartY = longClickY;
		//		return viewParent.onCtrlEvent( this , MSG_WIDGET3D_SHORTCUT_LONGCLICK );
		return true;
	}
	
	public View3D getWidget3D()
	{
		Widget3D widget = Widget3DManager.getInstance().getWidget3D( resolve_info );
		if( widget != null )
			widget.setPosition( longClickX - widget.width / 2 , longClickY - widget.height / 2 );
		return widget;
	}
	
	// @Override
	// public boolean visible() {
	// if (SideBar.getState() != SideBar.STATE_SHOW)
	// return false;
	// if (!isVisible())
	// return false;
	// if (this.viewParent != null) {
	// if (!this.viewParent.isVisible())
	// return false;
	// } else
	// return false;
	// return true;
	// }
	// @Override
	// public int getClingPriority() {
	// return ClingManager.WIDGET_CLING;
	// }
	//
	// @Override
	// public void dismissCling() {
	// clingState = ClingManager.CLING_STATE_DISMISSED;
	// }
	//
	// @Override
	// public void setPriority(int priority) {
	// // TODO Auto-generated method stub
	//
	// }
	public void removeHide()
	{
		if( !packageName.equals( "" ) )
		{
			PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().remove( "HIDE:" + packageName ).commit();
		}
	}
	
	public void onThemeChanged()
	{
		if( titleRegion == null || cellRegion == null || previewRegion == null || resolve_info == null )
			return;
		if( preview != null )
			preview.recycle();
		packageName = resolve_info.activityInfo.packageName;
		title = iLoongApplication.mIconCache.getLabel( resolve_info );
		Widget3DProvider provider = Widget3DManager.getInstance().getWidget3DProvider( packageName );
		cellTitle = provider.spanX + "x" + provider.spanY;
		int titleWidth = (int)( width * 3 / 4 );
		int alignH = AppBar3D.TEXT_ALIGN_LEFT;
		int titleHeight = (int)( R3D.widget_preview_title_weight * height );
		if( DefaultLayout.enable_workspace_miui_edit_mode == true && widget_shown_in_workspace_edit_mode )
		{
			titleWidth = (int)( width );
			alignH = AppBar3D.TEXT_ALIGN_CENTER;
			titleHeight = MIUIWidgetList.getSingleLineHeight();
		}
		titleRegion = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( title , titleWidth , titleHeight , alignH , AppBar3D.TEXT_ALIGN_CENTER , true , Color.WHITE ) ) );
		cellRegion[0] = R3D.findRegion( provider.spanX + "" );
		cellRegion[1] = R3D.findRegion( provider.spanY + "" );
		if( DefaultLayout.enable_workspace_miui_edit_mode == true && widget_shown_in_workspace_edit_mode )
		{
			if( !RR.net_version )
				miuiCellRegion = R3D.findRegion( "miui-widget-indicator" + provider.spanX + provider.spanY );
		}
		preview = null;
		InputStream stream = null;
		try
		{
			Context widgetPluginContext = iLoongApplication.getInstance().createPackageContext( resolve_info.activityInfo.packageName , Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY );
			Bundle metaData = resolve_info.activityInfo.applicationInfo.metaData;
			boolean useTheme = false;
			if( metaData != null )
			{
				useTheme = metaData.getBoolean( "useTheme" , false );
			}
			String iconPath = "widget_ico.png";
			if( useTheme )
			{
				String widgetThemeName = ThemeManager.getInstance().getCurrentThemeDescription().widgettheme;
				if( widgetThemeName == null || widgetThemeName.trim().equals( "" ) )
				{
					widgetThemeName = Widget3DManager.getInstance().getWidget3DTheme(
							resolve_info.activityInfo.packageName ,
							ThemeManager.getInstance().getCurrentThemeDescription().componentName.getPackageName() );
				}
				iconPath = widgetThemeName + "/image/widget_ico.png";
			}
			String widgetShortName = packageName.substring( packageName.lastIndexOf( "." ) + 1 ).toLowerCase( Locale.ENGLISH );
			String final_wdgrespath = "theme/widget/" + widgetShortName + "/" + iconPath;
			String final_theme_wdgrespath = "theme/widget/" + packageName + "/" + iconPath;
			try
			{
				stream = ThemeManager.getInstance().getCurrentThemeInputStream( final_theme_wdgrespath );
				if( stream == null )
					stream = ThemeManager.getInstance().getCurrentThemeInputStream( final_wdgrespath );
				if( stream == null )
				{
					try
					{
						DisplayMetrics displayMetrics = ThemeManager.getInstance().getCurrentThemeDescription().getResourcesDisplayMetrics();
						String screen_density_str = "/" + displayMetrics.widthPixels + "x" + displayMetrics.heightPixels;
						String icon_name = "widget_ico.png";
						String final_res_path;
						if( DefaultLayout.enable_workspace_miui_edit_mode && widget_shown_in_workspace_edit_mode )
						{
							icon_name = "menu_ico.png";
						}
						final_res_path = ThemeManager.getInstance().getCurrentThemeDescription().widgettheme + "/image" + screen_density_str + "/" + icon_name;
						Log.v( "cooee" , "Widget3DShortcut ---- makeshortcut ---- widget icon final_res_path: " + final_res_path );
						stream = widgetPluginContext.getAssets().open( final_res_path );
					}
					catch( Exception e )
					{
						//e.printStackTrace();
					}
					if( stream == null )
					{
						stream = widgetPluginContext.getAssets().open( iconPath );
					}
				}
			}
			catch( Exception e )
			{
				// stream = widgetPluginContext.getAssets().open(iconPath);
			}
			if( stream == null )
			{
				iconPath = "iLoong" + "/image/widget_ico.png";
				final_wdgrespath = "theme/widget/" + widgetShortName + "/" + iconPath;
				stream = ThemeManager.getInstance().getCurrentThemeInputStream( final_wdgrespath );
				if( stream == null )
				{
					stream = widgetPluginContext.getAssets().open( iconPath );
				}
			}
			preview = ThemeManager.getInstance().getBitmap( stream );
		}
		catch( Exception e )
		{
		}
		finally
		{
			try
			{
				if( stream != null )
				{
					stream.close();
				}
			}
			catch( Exception e )
			{
			}
		}
		if( preview == null )
		{
			preview = iLoongApplication.mIconCache.getIcon( new ComponentName( resolve_info.activityInfo.applicationInfo.packageName , resolve_info.activityInfo.name ) , resolve_info );
		}
		if( provider.spanX != -1 && provider.spanX != -1 )
		{
			if( DefaultLayout.enable_workspace_miui_edit_mode && widget_shown_in_workspace_edit_mode )
			{
				preview = Desktop3DListener.getWidgetPreviewWorkspaceEditMode( preview , provider.spanX , provider.spanY );
			}
			else
			{
				preview = Desktop3DListener.getWidgetPreview( preview , provider.spanX , provider.spanY );
			}
		}
		previewRegion = new TextureRegion( new BitmapTexture( preview , true ) );
		previewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
	}
	
	@Override
	public void releaseRegion()
	{
		// TODO Auto-generated method stub
		if( region != null && region.getTexture() != null )
		{
			AppList3D.allmemnum += region.getRegionHeight() * region.getRegionWidth() * 4 / 1024;
			Log.v( "" , "releaseRegion name is " + name + " allmemnum is " + AppList3D.allmemnum );
		}
		super.releaseRegion();
		//		Log.v( "" , "releaseRegion name is Widget3DShortcut" );
		if( preview != null && !preview.isRecycled() )
		{
			AppList3D.allmemnum += preview.getHeight() * preview.getWidth() * 4 / 1024;
			Log.v( "" , "releaseRegion name is preview allmemnum is " + AppList3D.allmemnum );
			preview.recycle();
			preview = null;
		}
		if( titleRegion != null && titleRegion.getTexture() != null )
		{
			AppList3D.allmemnum += titleRegion.getRegionHeight() * titleRegion.getRegionWidth() * 4 / 1024;
			Log.v( "" , "releaseRegion name is titleRegion allmemnum is " + AppList3D.allmemnum );
			titleRegion.getTexture().dispose();
			titleRegion = null;
		}
		if( previewRegion != null && previewRegion.getTexture() != null )
		{
			AppList3D.allmemnum += previewRegion.getRegionHeight() * previewRegion.getRegionWidth() * 4 / 1024;
			Log.v( "" , "releaseRegion name is previewRegion allmemnum is " + AppList3D.allmemnum );
			previewRegion.getTexture().dispose();
			previewRegion = null;
		}
		bg = null;
		miuiCellRegion = null;
		// if (bg != null && bg.getTexture() != null) {
		// bg.getTexture().dispose();
		// bg = null;
		// }
		// if (cellRegion != null) {
		// for (int i = 0; i < cellRegion.length; i++) {
		// if (cellRegion[i] != null && cellRegion[i].getTexture() != null) {
		// cellRegion[i].getTexture().dispose();
		// cellRegion[i] = null;
		// }
		// }
		//
		// }
		//		if (miuiCellRegion != null && miuiCellRegion.getTexture() != null) {
		//			miuiCellRegion.getTexture().dispose();
		//			miuiCellRegion = null;
		//		}
	}
}
