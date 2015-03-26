package com.iLoong.launcher.Widget3D;


import java.io.InputStream;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.Widget3DHost.Widget3DProvider;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class WidgetMyShortcut extends Widget3DShortcut
{
	
	public ResolveInfo resolve_info;
	private ApplicationInfo app_info;
	protected AndroidFiles files;
	private Icon3D icon;
	// private int clingState = ClingManager.CLING_STATE_WAIT;
	public String title;
	public String cellTitle;
	public Bitmap preview;
	public TextureRegion titleRegion;
	public TextureRegion[] cellRegion = new TextureRegion[2];;
	public TextureRegion previewRegion;
	public String packageName = "";
	public boolean uninstall = false;
	public boolean canUninstall = false;
	public boolean hide = false;
	public boolean isHideWidget = false;
	protected float longClickX = 0;
	protected float longClickY = 0;
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
	
	public WidgetMyShortcut(
			String name )
	{
		super( name );
		resolve_info = null;
		app_info = null;
		files = null;
	}
	
	public WidgetMyShortcut(
			String name ,
			ResolveInfo resolve_info )
	{
		super( name );
		app_info = new ApplicationInfo( resolve_info , iLoongApplication.mIconCache );
		this.resolve_info = resolve_info;
		try
		{
			Context widgetContext = iLoongApplication.ctx.createPackageContext( resolve_info.activityInfo.packageName , Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY );
			files = new AndroidFiles( widgetContext.getAssets() );
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		// if (!AppHost3D.V2)
		// addShortcut();
		// clingState = ClingManager.getInstance().fireWidgetCling(this);
		if( DefaultLayout.show_widget_shortcut_bg )
		{
			bg = R3D.findRegion( "widget-shortcut-bg" );
		}
		packageName = resolve_info.activityInfo.packageName;
		isHideWidget = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getBoolean( "HIDE:" + packageName , false );
	}
	
	public WidgetMyShortcut clone()
	{
		return new WidgetMyShortcut( name , resolve_info );
	}
	
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
			titleRegion = new TextureRegion( new Texture3D( AppBar3D.titleToPixmap(
					title ,
					(int)( width * 3 / 4 ) ,
					(int)( R3D.widget_preview_title_weight * height * 0.8f ) ,
					AppBar3D.TEXT_ALIGN_LEFT ,
					AppBar3D.TEXT_ALIGN_CENTER ,
					true ,
					Color.WHITE ) ) );
			cellRegion[0] = R3D.findRegion( provider.spanX + "" );
			cellRegion[1] = R3D.findRegion( provider.spanY + "" );
			preview = null;
			InputStream stream = null;
			try
			{
				Context widgetPluginContext = iLoongApplication.ctx.createPackageContext( resolve_info.activityInfo.packageName , Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY );
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
				try
				{
					stream = ThemeManager.getInstance().getCurrentThemeInputStream( final_wdgrespath );
					if( stream == null )
					{
						stream = widgetPluginContext.getAssets().open( iconPath );
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
			if( preview != null )
			{
				float scale = 1 , scale1 = 1 , scale2 = 1;
				float totalscale = Utils3D.getScreenWidth() / 720f;
				if( preview.getWidth() * totalscale > width * 0.7f )
				{
					scale1 = width * 0.7f / ( (float)preview.getWidth() * totalscale );
				}
				if( preview.getHeight() * totalscale > height * 0.5f )
				{
					scale2 = height * 0.5f / ( (float)preview.getHeight() * totalscale );
				}
				scale = scale1 > scale2 ? scale2 : scale1;
				int bitmapWidth = (int)( scale * preview.getWidth() * totalscale );
				int bitmapHeight = (int)( scale * preview.getHeight() * totalscale );
				Bitmap bitmap = Bitmap.createScaledBitmap( preview , bitmapWidth , bitmapHeight , false );
				if( !bitmap.equals( preview ) )
					preview.recycle();
				previewRegion = new TextureRegion( new BitmapTexture( bitmap , true ) );
				previewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
			}
			//			if( preview == null )
			//			{
			//				preview = iLoongApplication.mIconCache.getIcon( new ComponentName( resolve_info.activityInfo.applicationInfo.packageName , resolve_info.activityInfo.name ) , resolve_info );
			//			}
			//			if( provider.spanX != -1 && provider.spanX != -1 )
			//				preview = Desktop3DListener.getWidgetPreview( preview , provider.spanX , provider.spanY );
			//			previewRegion = new TextureRegion( new BitmapTexture( preview , true ) );
			//			previewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
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
			//			int drawWidth = (int)( previewRegion.getRegionWidth() * scale );
			//			// if (drawWidth<R3D.workspace_cell_width)
			//			// {
			//			// drawWidth=R3D.workspace_cell_width;
			//			// }
			//			int drawHeight = (int)( previewRegion.getRegionHeight() * scale );
			// if (drawHeight<R3D.workspace_cell_width)
			// {
			// drawHeight=R3D.workspace_cell_width;
			// }
			int drawWidth = (int)( previewRegion.getRegionWidth() );
			if( drawWidth < R3D.workspace_cell_height )
			{
				drawWidth = R3D.workspace_cell_height;
			}
			int drawHeight = (int)( previewRegion.getRegionHeight() );
			if( drawHeight < R3D.workspace_cell_height )
			{
				drawHeight = R3D.workspace_cell_height;
			}
			float previewX = width / 2 - drawWidth / 2;
			float previewY = height * ( 1 - R3D.widget_preview_title_weight ) / 2 - drawHeight / 2 + height * R3D.widget_preview_title_weight;
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
				batch.draw( titleRegion , x , y );
			}
		}
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
				batch.draw( cellRegion[0] , width * 3 / 4 + x + titleX , y + ( ( R3D.widget_preview_title_weight * height ) - cellRegion[0].getRegionHeight() ) / 2 );
				batch.draw( xRegion , width * 3 / 4 + x + titleX + cellRegion[0].getRegionWidth() , y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 );
				batch.draw(
						cellRegion[1] ,
						width * 3 / 4 + x + titleX + cellRegion[0].getRegionWidth() + xRegion.getRegionWidth() ,
						y + ( ( R3D.widget_preview_title_weight * height ) - cellRegion[1].getRegionHeight() ) / 2 );
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
		//		Log.v( "Widget3DShortcut" , "onClick:x,y=" + x + " " + y );
		//		SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.can_drag_to_desktop ) );
		//		return true;
		point.x = x;
		point.y = y;
		this.toAbsolute( point );
		longClickX = point.x;
		longClickY = point.y;
		point.x = width / 2;
		point.y = height / 2;
		this.toAbsolute( point );
		this.setTag( new Vector2( point.x , point.y ) );
		Log.d( "launcher" , "long click:" + point.x + "," + point.y );
		// clingState = ClingManager.CLING_STATE_DISMISSED;
		// ClingManager.getInstance().cancelWidgetCling();
		DragLayer3D.dragStartX = longClickX;
		DragLayer3D.dragStartY = longClickY;
		return viewParent.onCtrlEvent( this , MSG_WIDGET3D_SHORTCUT_CLICK );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		//		if (hide || uninstall)
		//			return true;
		point.x = x;
		point.y = y;
		this.toAbsolute( point );
		longClickX = point.x;
		longClickY = point.y;
		point.x = width / 2;
		point.y = height / 2;
		this.toAbsolute( point );
		this.setTag( new Vector2( point.x , point.y ) );
		Log.d( "launcher" , "long click:" + point.x + "," + point.y );
		// clingState = ClingManager.CLING_STATE_DISMISSED;
		// ClingManager.getInstance().cancelWidgetCling();
		DragLayer3D.dragStartX = longClickX;
		DragLayer3D.dragStartY = longClickY;
		return viewParent.onCtrlEvent( this , MSG_WIDGET3D_SHORTCUT_LONGCLICK );
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
}
