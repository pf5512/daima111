package com.iLoong.launcher.Widget3D;


import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL11;

import android.content.ComponentName;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;

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
import com.iLoong.launcher.Desktop3D.WidgetIcon;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.DesktopEdit.DesktopEditHost;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.miui.MIUIWidgetList;
import com.iLoong.launcher.theme.ThemeManager;


public class Widget3DVirtual extends WidgetIcon
{
	
	private String title;
	private String cellTitle;
	private Bitmap preview;
	private TextureRegion titleRegion;
	private TextureRegion[] cellTitleRegion = new TextureRegion[2];
	private TextureRegion previewRegion;
	private TextureRegion miuiCellRegion;
	public boolean widget_icon_shown_workspace_edit_mode = false;
	public String packageName;
	public boolean uninstalled = false;
	public int iconResourceId;
	public boolean isInternal = false;
	public static final int MSG_WIDGET3D_SHORTCUT_LONGCLICK = 0;
	public static final int MSG_WIDGET3D_SHORTCUT_CLICK = 1;
	
	public Widget3DVirtual(
			String name ,
			Bitmap bmp ,
			String title )
	{
		super( name );
		preview = bmp;
		//setPreviewRegion();
	}
	
	public Bitmap getPreviewBitmap()
	{
		return preview;
	}
	
	public Widget3DVirtual(
			String name ,
			TextureRegion Previewregion ,
			String title )
	{
		super( name );
		//		//Jone add
		//		if(DefaultLayout.net_version){
		//			if(Previewregion==null&&preview!=null){
		//				previewRegion = new TextureRegion( new BitmapTexture(preview) );
		//			}
		//		}
		//		else
		//		//Jone end
		{
			preview = null;
			previewRegion = new TextureRegion( Previewregion );
		}
		previewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
	}
	
	public TextureRegion getPreviewRegion()
	{
		return previewRegion;
	}
	
	public void setPreviewRegion()
	{
		if( previewRegion == null && preview != null )
		{
			int spanX = 1 , spanY = 1;
			float scale = 1;
			if( packageName.equalsIgnoreCase( "com.iLoong.clock" ) )
			{
				spanX = 4;
				spanY = 2;
			}
			else if( packageName.equalsIgnoreCase( "com.cooee.searchbar.TurboSearchBar" ) )
			{
				spanX = 4;
				spanY = 1;
			}
			else if( packageName.equalsIgnoreCase( "com.iLoong.Clean.iLoongClean" ) )
			{
				spanX = 1;
				spanY = 1;
			}
			else if( packageName.equalsIgnoreCase( "com.iLoong.Clean4.iLoongClean4" ) )
			{
				spanX = 4;
				spanY = 1;
			}
			if( preview.getWidth() > width * 0.8f )
			{
				scale = width * 0.8f / (float)preview.getWidth();
			}
			if( preview.getHeight() * scale > height * 0.8f )
			{
				scale = height * 0.8f / (float)preview.getHeight();
			}
			Bitmap bitmap;
			if( DefaultLayout.enable_workspace_miui_edit_mode && widget_icon_shown_workspace_edit_mode )
			{
				bitmap = Desktop3DListener.getWidgetPreviewWorkspaceEditMode( preview , spanX , spanY );
			}
			else
			{
				bitmap = getWidgetPreview( preview , spanX , spanY );
			}
			previewRegion = new TextureRegion( new BitmapTexture( bitmap , true ) );
			previewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
	}
	
	public int estimateWidgetCellWidth(
			int cellHSpan )
	{
		if( cellHSpan > 4 )
			cellHSpan = 4;
		float widgetWidth = (float)( Utils3D.getScreenWidth() - R3D.applist_padding_left - R3D.applist_padding_right ) / ( (float)AppList3D.mWidgetCountX ) - R3D.app_widget3d_gap;
		float widgetCellWidth = widgetWidth / ( (float)4 );
		return (int)( widgetCellWidth * ( (float)cellHSpan ) );
	}
	
	public int estimateWidgetCellHeight(
			int cellVSpan )
	{
		if( cellVSpan > 4 )
			cellVSpan = 4;
		float widgetHeight = ( (float)( Utils3D.getScreenHeight() - R3D.appbar_height - R3D.applist_padding_top - R3D.applist_padding_bottom ) / ( (float)AppList3D.mWidgetCountY ) - R3D.app_widget3d_gap ) * ( 1 - R3D.widget_preview_title_weight );
		float widgetCellHeight = widgetHeight / ( (float)4 );
		return (int)( widgetCellHeight * ( (float)cellVSpan ) );
	}
	
	public Bitmap getWidgetPreview(
			Bitmap bitmap ,
			int cellHSpan ,
			int cellVSpan )
	{
		int bitmapWidth;
		int bitmapHeight;
		int maxWidth;
		int maxHeight;
		bitmapWidth = bitmap.getWidth();
		bitmapHeight = bitmap.getHeight();
		// Cap the size so widget previews don't appear larger than the actual
		// widget
		if( cellHSpan < 3 )
			cellHSpan = 3;
		if( cellVSpan < 3 )
			cellVSpan = 3;
		maxWidth = estimateWidgetCellWidth( cellHSpan );
		maxHeight = estimateWidgetCellHeight( cellVSpan );
		float scale = 1f;
		if( bitmapWidth > maxWidth )
		{
			scale = maxWidth / (float)bitmapWidth;
		}
		if( bitmapHeight * scale > maxHeight )
		{
			scale = maxHeight / (float)bitmapHeight;
		}
		if( DefaultLayout.show_widget_shortcut_bg || DefaultLayout.widget_shortcut_lefttop )
		{
			bitmapWidth *= 0.9f;
			bitmapHeight *= 0.9f;
		}
		// if (scale != 1f) {
		bitmapWidth = (int)( scale * bitmapWidth );
		bitmapHeight = (int)( scale * bitmapHeight );
		Bitmap preview = Bitmap.createScaledBitmap( bitmap , bitmapWidth , bitmapHeight , true );
		if( !bitmap.equals( preview ) )
			bitmap.recycle();
		return preview;
		// } else
		// return bitmap;
	}
	
	//Jone end
	public void setPckName(
			String pckName )
	{
		this.packageName = pckName;
		setPreviewRegion();
	}
	
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	public boolean getIsHideStatus()
	{
		return this.isHide;
	}
	
	public void setHideStatus(
			boolean hide_status )
	{
		this.hide = hide_status;
	}
	
	public void setUninstallStatus(
			boolean uninstall_status )
	{
		this.uninstall = uninstall_status;
	}
	
	public boolean onLongClick(
			float x ,
			float y )
	{
		//		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		//		boolean isLoadByInternal = DefaultLayout.isWidgetLoadByInternal( info.intent.getComponent().getPackageName() );
		//		if( isLoadByInternal )
		//		{
		//			Vector2 point = new Vector2();
		//			point.x = x;
		//			point.y = y;
		//			this.toAbsolute( point );
		//			float longClickX = point.x;
		//			float longClickY = point.y;
		//			point.x = width / 2;
		//			point.y = height / 2;
		//			this.toAbsolute( point );
		//			this.setTag( new Vector2( point.x , point.y ) );
		//			Log.d( "launcher" , "long click:" + point.x + "," + point.y );
		//			// clingState = ClingManager.CLING_STATE_DISMISSED;
		//			// ClingManager.getInstance().cancelWidgetCling();
		//			DragLayer3D.dragStartX = longClickX;
		//			DragLayer3D.dragStartY = longClickY;
		//			return viewParent.onCtrlEvent( this , MSG_WIDGET3D_SHORTCUT_LONGCLICK );
		//		}
		//		else
		//		{
		//			if( info.intent != null && info.intent.getAction().equals( Intent.ACTION_PACKAGE_INSTALL ) )
		//			{
		//				SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.download_to_install ) );
		//				return true;
		//			}
		//			return super.onLongClick( x , y );
		//		}
		return true;
	}
	
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		Log.v( "click" , "View3D onClick:" + name + " x:" + x + " y:" + y );
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		//		if (uninstall) {
		//			// PreferenceManager.getDefaultSharedPreferences(
		//			// iLoongLauncher.getInstance()).edit().putBoolean("UNINSTALL:"+packageName,true).commit();
		//			// this.remove();
		//			// uninstalled = true;
		//			return true;
		//		} else if (hide) {
		//			ComponentName name = info.intent.getComponent();
		//			if (name != null) {
		//				isHide = !isHide;
		//				PreferenceManager
		//						.getDefaultSharedPreferences(
		//								iLoongLauncher.getInstance()).edit()
		//						.putBoolean("HIDE:" + name.toString(), isHide).commit();
		//			}
		//			return true;
		//		}
		boolean isLoadByInternal = DefaultLayout.isWidgetLoadByInternal( info.intent.getComponent().getPackageName() );
		if( !isLoadByInternal )
		{
			return WidgetDownload.checkToDownload( info , true );
		}
		else
		{
			if( ( DefaultLayout.enable_workspace_miui_edit_mode ) && ( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) )
			{
				Workspace3D mWorkspace3D = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
				if( mWorkspace3D.edit_mode_click_2_add_our_widget != null )
				{
					return true;
				}
			}
			viewParent.onCtrlEvent( this , MSG_WIDGET3D_SHORTCUT_CLICK );
		}
		return true;
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
		if( titleRegion == null || cellTitleRegion == null
		/*|| previewRegion == null*/)
		{
			ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
			packageName = info.intent.getComponent().getPackageName();
			title = info.title.toString();
			if( title.equals( "clock" ) )
			{
				title = iLoongLauncher.getInstance().getResources().getString( RR.string.clock );
			}
			if( title.equals( "memo" ) )
			{
				title = iLoongLauncher.getInstance().getResources().getString( RR.string.memo );
			}
			if( title.equals( "robot" ) )
			{
				title = iLoongLauncher.getInstance().getResources().getString( RR.string.robot );
			}
			if( packageName.equalsIgnoreCase( "com.iLoong.clock" ) )
			{
				title = iLoongLauncher.getInstance().getResources().getString( RR.string.clock );
			}
			else if( packageName.equalsIgnoreCase( "com.cooee.searchbar" ) )
			{
				title = iLoongLauncher.getInstance().getResources().getString( RR.string.widget_search_bar );
			}
			else if( packageName.equalsIgnoreCase( "com.iLoong.Clean" ) )
			{
				title = iLoongLauncher.getInstance().getResources().getString( RR.string.widget_clean_up );
			}
			else if( packageName.equalsIgnoreCase( "com.iLoong.Clean4" ) )
			{
				title = iLoongLauncher.getInstance().getResources().getString( RR.string.widget_clean_up );
			}
			else if( packageName.equalsIgnoreCase( "com.iLoong.WeatherClock" ) )
			{
				title = iLoongLauncher.getInstance().getResources().getString( RR.string.widget_weather_clock );
			}
			// int minWidth = info.minWidth;
			// int minHeight = info.minHeight;
			// int[] span =
			// CellLayout3D.rectToCell(iLoongApplication.ctx.getResources(),
			// minWidth, minHeight, null);
			cellTitle = info.spanX + "x" + info.spanY;
			int titleWidth = (int)( width * 3 / 4 );
			int alignH = AppBar3D.TEXT_ALIGN_LEFT;
			int titleHeight = (int)( R3D.widget_preview_title_weight * height );
			if( !RR.net_version && DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_icon_shown_workspace_edit_mode == true )
			{
				titleWidth = (int)( width );
				alignH = AppBar3D.TEXT_ALIGN_CENTER;
				titleHeight = MIUIWidgetList.getSingleLineHeight();
			}
			titleRegion = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( title , titleWidth , titleHeight , alignH , AppBar3D.TEXT_ALIGN_CENTER , true , Color.WHITE ) , true ) );
			cellTitleRegion[0] = R3D.findRegion( info.spanX + "" );
			cellTitleRegion[1] = R3D.findRegion( info.spanY + "" );
			if( DefaultLayout.isWidgetLoadByInternal( packageName ) )
			{
				preview = ThemeManager.getInstance().getBitmap( DefaultLayout.InternalWidgetBitmap( packageName ) );
				if( preview != null )
				{
					float scale = 1;
					if( preview.getWidth() > width * 0.8f )
					{
						scale = width * 0.8f / (float)preview.getWidth();
					}
					if( preview.getHeight() * scale > height * 0.8f )
					{
						scale = height * 0.8f / (float)preview.getHeight();
					}
					Bitmap bitmap;
					if( DefaultLayout.enable_workspace_miui_edit_mode && widget_icon_shown_workspace_edit_mode )
					{
						bitmap = Desktop3DListener.getWidgetPreviewWorkspaceEditMode( preview , info.spanX , info.spanY );
					}
					else
					{
						bitmap = Desktop3DListener.getWidgetPreview( preview , info.spanX , info.spanY );
					}
					previewRegion = new TextureRegion( new BitmapTexture( bitmap , true ) );
					previewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				}
			}
			else
			{
				if( preview != null && previewRegion == null )
				{
					float scale = 1;
					if( preview.getWidth() > width * 0.8f )
					{
						scale = width * 0.8f / (float)preview.getWidth();
					}
					if( preview.getHeight() * scale > height * 0.8f )
					{
						scale = height * 0.8f / (float)preview.getHeight();
					}
					int bitmapWidth = (int)( scale * preview.getWidth() );
					int bitmapHeight = (int)( scale * preview.getHeight() );
					Bitmap bitmap = Bitmap.createScaledBitmap( preview , bitmapWidth , bitmapHeight , true );
					if( !bitmap.equals( preview ) )
						preview.recycle();
					previewRegion = new TextureRegion( new BitmapTexture( bitmap , true ) );
					previewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				}
			}
			if( DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_icon_shown_workspace_edit_mode == true )
			{
				if( !RR.net_version )
					miuiCellRegion = R3D.findRegion( "miui-widget-indicator" + info.spanX + info.spanY );
			}
		}
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		//		if(DefaultLayout.net_version){
		//			if(AppList3D.widget3DSize.x>0){
		//				this.width=AppList3D.widget3DSize.x;
		//				this.height=AppList3D.widget3DSize.y;
		//			}
		//		}
		float alpha = color.a;
		if( isHide && ( uninstall || hide ) )
		{
			alpha *= 0.2;
		}
		batch.setColor( color.r , color.g , color.b , alpha * parentAlpha );
		int srcBlendFunc = 0 , dstBlendFunc = 0;
		if( DefaultLayout.blend_func_dst_gl_one )
		{
			/* 获取获取混合方式 */
			srcBlendFunc = batch.getSrcBlendFunc();
			dstBlendFunc = batch.getDstBlendFunc();
			if( srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE_MINUS_SRC_ALPHA )
				batch.setBlendFunction( GL11.GL_SRC_ALPHA , GL11.GL_ONE_MINUS_SRC_ALPHA );
		}
		if( previewRegion != null )
		{
			TextureRegion bg = R3D.findRegion( "widget-shortcut-bg" );
			if( DefaultLayout.show_widget_shortcut_bg )
			{
				if( bg != null )
				{
					if( DefaultLayout.widget_shortcut_title_top )
					{
						if( is3dRotation() )
						{
							batch.draw( bg , x , y , width , height * ( 1 - R3D.widget_preview_title_weight ) );
						}
						else
						{
							batch.draw(
									bg ,
									x ,
									y ,
									originX ,
									originY - height * R3D.widget_preview_title_weight ,
									width ,
									height * ( 1 - R3D.widget_preview_title_weight ) ,
									scaleX ,
									scaleY ,
									rotation );
						}
					}
					else
					{
						if( is3dRotation() )
						{
							batch.draw( bg , x , y + height * R3D.widget_preview_title_weight , width , height * ( 1 - R3D.widget_preview_title_weight ) );
						}
						else
						{
							batch.draw(
									bg ,
									x ,
									y + height * R3D.widget_preview_title_weight ,
									originX ,
									originY - height * R3D.widget_preview_title_weight ,
									width ,
									height * ( 1 - R3D.widget_preview_title_weight ) ,
									scaleX ,
									scaleY ,
									rotation );
						}
					}
				}
			}
			int drawWidth = (int)( previewRegion.getRegionWidth() );
			int drawHeight = (int)( previewRegion.getRegionHeight() );
			if( DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_icon_shown_workspace_edit_mode == true )
			{
				//noting
			}
			else
			{
				if( drawWidth < R3D.workspace_cell_height )
				{
					drawWidth = R3D.workspace_cell_height;
				}
				if( drawHeight < R3D.workspace_cell_height )
				{
					drawHeight = R3D.workspace_cell_height;
				}
			}
			float previewX = width / 2 - drawWidth / 2;
			float previewY = height * ( 1 - R3D.widget_preview_title_weight ) / 2 - drawHeight / 2 + height * R3D.widget_preview_title_weight;
			if( DefaultLayout.enable_workspace_miui_edit_mode == true && widget_icon_shown_workspace_edit_mode )
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
				previewY = height - height * ( 1 - R3D.widget_preview_title_weight ) / 20 - drawHeight;
				if( DefaultLayout.widget_shortcut_title_top )
				{
					previewY = previewY - height * R3D.widget_preview_title_weight;
				}
			}
			//			batch.draw(previewRegion, previewX + x, previewY + y, drawWidth,
			//					drawHeight);
			if( is3dRotation() )
			{
				batch.draw( previewRegion , previewX + x , previewY + y , drawWidth , drawHeight );
			}
			else
			{
				batch.draw( previewRegion , previewX + x , previewY + y , originX - previewX , originY - previewY , drawWidth , drawHeight , scaleX , scaleY , rotation );
			}
		}
		if( DefaultLayout.blend_func_dst_gl_one )
		{
			batch.setBlendFunction( srcBlendFunc , dstBlendFunc );
		}
		if( titleRegion != null )
		{
			if( DefaultLayout.widget_shortcut_title_top )
			{
				if( is3dRotation() )
					batch.draw( titleRegion , x , y + height * ( 1 - R3D.widget_preview_title_weight ) );
				else
					batch.draw(
							titleRegion ,
							x ,
							y + height * ( 1 - R3D.widget_preview_title_weight ) ,
							originX ,
							originY ,
							titleRegion.getRegionWidth() ,
							titleRegion.getRegionHeight() ,
							scaleX ,
							scaleY ,
							rotation );
			}
			else
			{
				if( DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_icon_shown_workspace_edit_mode == true )
				{
					if( is3dRotation() )
						batch.draw( titleRegion , x , y + R3D.miui_widget_indicator_height );
					else
						batch.draw(
								titleRegion ,
								x ,
								y + R3D.miui_widget_indicator_height ,
								originX ,
								originY ,
								(float)( titleRegion.getRegionWidth() * 1.5 ) ,
								(float)( titleRegion.getRegionHeight() * 1.5 ) ,
								scaleX ,
								scaleY ,
								rotation );
				}
				else
				{
					if( is3dRotation() )
						batch.draw( titleRegion , x , y );
					else
						batch.draw( titleRegion , x , y , originX , originY , titleRegion.getRegionWidth() , titleRegion.getRegionHeight() , scaleX , scaleY , rotation );
				}
			}
		}
		if( !RR.net_version && DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_icon_shown_workspace_edit_mode == true )
		{
			if( is3dRotation() )
			{
				if( miuiCellRegion != null )
				{
					batch.draw( miuiCellRegion , x + ( width - R3D.miui_widget_indicator_height ) / 2 , y );
				}
			}
			else
			{
				if( miuiCellRegion != null )
				{
					batch.draw(
							miuiCellRegion ,
							x + ( width - R3D.miui_widget_indicator_height ) / 2 ,
							y ,
							originX ,
							originY ,
							R3D.miui_widget_indicator_height ,
							R3D.miui_widget_indicator_height ,
							scaleX ,
							scaleY ,
							rotation );
				}
			}
		}
		else
		{
			if( cellTitleRegion != null )
			{
				if( is3dRotation() )
				{
					TextureRegion xRegion = R3D.findRegion( "x" );
					float titleX = 10;
					titleX = (float)( width / 4 - 2 * cellTitleRegion[0].getRegionWidth() - xRegion.getRegionWidth() ) - titleX;
					if( titleX < 0 )
						titleX = 0;
					if( DefaultLayout.widget_shortcut_title_top )
					{
						batch.draw(
								cellTitleRegion[0] ,
								width * 3 / 4 + x + titleX ,
								y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[0].getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) );
						batch.draw(
								xRegion ,
								width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() ,
								y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) );
						batch.draw(
								cellTitleRegion[1] ,
								width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() + xRegion.getRegionWidth() ,
								y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[1].getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) );
					}
					else
					{
						batch.draw( cellTitleRegion[0] , width * 3 / 4 + x + titleX , y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[0].getRegionHeight() ) / 2 );
						batch.draw( xRegion , width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() , y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 );
						batch.draw(
								cellTitleRegion[1] ,
								width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() + xRegion.getRegionWidth() ,
								y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[1].getRegionHeight() ) / 2 );
					}
				}
				else
				{
					TextureRegion xRegion = R3D.findRegion( "x" );
					float titleX = 10;
					titleX = (float)( width / 4 - 2 * cellTitleRegion[0].getRegionWidth() - xRegion.getRegionWidth() ) - titleX;
					if( titleX < 0 )
						titleX = 0;
					if( DefaultLayout.widget_shortcut_title_top )
					{
						batch.draw(
								cellTitleRegion[0] ,
								width * 3 / 4 + x + titleX ,
								y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[0].getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) ,
								originX - width * 3 / 4 ,
								originY ,
								cellTitleRegion[0].getRegionWidth() ,
								cellTitleRegion[0].getRegionHeight() ,
								scaleX ,
								scaleY ,
								rotation );
						batch.draw(
								xRegion ,
								width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() ,
								y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) ,
								originX - width * 3 / 4 ,
								originY ,
								xRegion.getRegionWidth() ,
								xRegion.getRegionHeight() ,
								scaleX ,
								scaleY ,
								rotation );
						batch.draw(
								cellTitleRegion[1] ,
								width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() + xRegion.getRegionWidth() ,
								y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[1].getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) ,
								originX - width * 3 / 4 ,
								originY ,
								cellTitleRegion[1].getRegionWidth() ,
								cellTitleRegion[1].getRegionHeight() ,
								scaleX ,
								scaleY ,
								rotation );
					}
					else
					{
						if( DesktopEditHost.getPopMenuStyle() == 1 )
						{
							batch.draw(
									cellTitleRegion[0] ,
									width * 3 / 4 + x + titleX - 25 * ( Utils3D.getScreenWidth() / 720f ) ,
									y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[0].getRegionHeight() ) / 2 ,
									originX - width * 3 / 4 ,
									originY ,
									cellTitleRegion[0].getRegionWidth() * 1.5f ,
									cellTitleRegion[0].getRegionHeight() * 1.5f ,
									scaleX ,
									scaleY ,
									rotation );
							batch.draw(
									xRegion ,
									width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() - 14 * ( Utils3D.getScreenWidth() / 720f ) ,
									y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 ,
									originX - width * 3 / 4 ,
									originY ,
									xRegion.getRegionWidth() * 1.5f ,
									xRegion.getRegionHeight() * 1.5f ,
									scaleX ,
									scaleY ,
									rotation );
							batch.draw(
									cellTitleRegion[1] ,
									width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() + xRegion.getRegionWidth() - 5 * ( Utils3D.getScreenWidth() / 720f ) ,
									y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[1].getRegionHeight() ) / 2 ,
									originX - width * 3 / 4 ,
									originY ,
									cellTitleRegion[1].getRegionWidth() * 1.5f ,
									cellTitleRegion[1].getRegionHeight() * 1.5f ,
									scaleX ,
									scaleY ,
									rotation );
						}
						else
						{
							batch.draw(
									cellTitleRegion[0] ,
									width * 3 / 4 + x + titleX ,
									y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[0].getRegionHeight() ) / 2 ,
									originX - width * 3 / 4 ,
									originY ,
									cellTitleRegion[0].getRegionWidth() ,
									cellTitleRegion[0].getRegionHeight() ,
									scaleX ,
									scaleY ,
									rotation );
							batch.draw(
									xRegion ,
									width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() ,
									y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 ,
									originX - width * 3 / 4 ,
									originY ,
									xRegion.getRegionWidth() ,
									xRegion.getRegionHeight() ,
									scaleX ,
									scaleY ,
									rotation );
							batch.draw(
									cellTitleRegion[1] ,
									width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() + xRegion.getRegionWidth() ,
									y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[1].getRegionHeight() ) / 2 ,
									originX - width * 3 / 4 ,
									originY ,
									cellTitleRegion[1].getRegionWidth() ,
									cellTitleRegion[1].getRegionHeight() ,
									scaleX ,
									scaleY ,
									rotation );
						}
					}
				}
			}
		}
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		float stateX = this.x + this.width - stateIconWidth;
		float stateY = this.y + this.height - stateIconHeight;
		if( is3dRotation() )
		{
			if( uninstall )
			{
				// batch.draw(R3D.findRegion("app-uninstall"), stateX,
				// stateY,stateIconWidth,stateIconHeight);
			}
			if( hide && isHide )
			{
				batch.draw( Icon3D.resumeTexture , stateX , stateY , stateIconWidth , stateIconHeight );
			}
			if( hide && !isHide )
			{
				batch.draw( Icon3D.hideTexture , stateX , stateY , stateIconWidth , stateIconHeight );
			}
		}
		else
		{
			float stateOriginX = originX - this.width + stateIconWidth;
			float stateOriginY = originY - this.height + stateIconHeight;
			if( uninstall )
			{
				// batch.draw(R3D.findRegion("app-uninstall"), stateX, stateY,
				// stateOriginX, stateOriginY,stateIconWidth,stateIconHeight,
				// scaleX,scaleY, rotation);
			}
			if( hide && isHide )
			{
				batch.draw( Icon3D.resumeTexture , stateX , stateY , stateOriginX , stateOriginY , stateIconWidth , stateIconHeight , scaleX , scaleY , rotation );
			}
			if( hide && !isHide )
			{
				batch.draw( Icon3D.hideTexture , stateX , stateY , stateOriginX , stateOriginY , stateIconWidth , stateIconHeight , scaleX , scaleY , rotation );
			}
		}
	}
	
	public void releaseRegion()
	{
		super.releaseRegion();
//		if(cellTitleRegion[0]!=null){
//			cellTitleRegion[0].getTexture().dispose();
//		}
//		if(cellTitleRegion[1]!=null){
//			cellTitleRegion[1].getTexture().dispose();
//		}
		if(previewRegion!=null)
			previewRegion.getTexture().dispose();
		if(titleRegion!=null)
			titleRegion.getTexture().dispose();
			
	}
	public void removeHide()
	{
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		ComponentName name = info.intent.getComponent();
		if( name != null )
		{
			PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().remove( "HIDE:" + name.toString() ).commit();
		}
	}
	
	public void onThemeChanged()
	{
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		title = info.title.toString();
		if( title.equals( "clock" ) )
		{
			title = iLoongLauncher.getInstance().getResources().getString( RR.string.clock );
		}
		if( title.equals( "memo" ) )
		{
			title = iLoongLauncher.getInstance().getResources().getString( RR.string.memo );
		}
		if( title.equals( "robot" ) )
		{
			title = iLoongLauncher.getInstance().getResources().getString( RR.string.robot );
		}
		cellTitle = info.spanX + "x" + info.spanY;
		int titleWidth = (int)( width * 3 / 4 );
		int alignH = AppBar3D.TEXT_ALIGN_LEFT;
		int titleHeight = (int)( R3D.widget_preview_title_weight * height );
		if( DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_icon_shown_workspace_edit_mode == true )
		{
			titleWidth = (int)( width );
			alignH = AppBar3D.TEXT_ALIGN_CENTER;
			titleHeight = MIUIWidgetList.getSingleLineHeight();
		}
		titleRegion = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( title , titleWidth , titleHeight , alignH , AppBar3D.TEXT_ALIGN_CENTER , true , Color.WHITE ) , true ) );
		cellTitleRegion[0] = R3D.findRegion( info.spanX + "" );
		cellTitleRegion[1] = R3D.findRegion( info.spanY + "" );
		String widgetShortName = packageName;
		String imageName = "theme/widget/" + widgetShortName + "/" + ThemeManager.getInstance().getCurrentThemeDescription().widgettheme + "/image/widget_ico.png";
		preview = ThemeManager.getInstance().getBitmap( imageName );
		if( DefaultLayout.isWidgetLoadByInternal( packageName ) )
		{
			InputStream stream = null;
			stream = ThemeManager.getInstance().getCurrentThemeInputStream( DefaultLayout.InternalWidgetBitmap( packageName ) );
			if( stream != null )
			{
				preview = ThemeManager.getInstance().getBitmap( stream );
				try
				{
					stream.close();
				}
				catch( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if( preview != null )
			{
				float scale = 1;
				if( preview.getWidth() > width * 0.8f )
				{
					scale = width * 0.8f / (float)preview.getWidth();
				}
				if( preview.getHeight() * scale > height * 0.8f )
				{
					scale = height * 0.8f / (float)preview.getHeight();
				}
				Bitmap bitmap;
				if( DefaultLayout.enable_workspace_miui_edit_mode && widget_icon_shown_workspace_edit_mode )
				{
					bitmap = Desktop3DListener.getWidgetPreviewWorkspaceEditMode( preview , info.spanX , info.spanY );
				}
				else
				{
					bitmap = Desktop3DListener.getWidgetPreview( preview , info.spanX , info.spanY );
				}
				previewRegion = new TextureRegion( new BitmapTexture( bitmap , true ) );
				previewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
			}
		}
		else
		{
			if( preview != null )
			{
				float scale = 1;
				if( preview.getWidth() > width * 0.8f )
				{
					scale = width * 0.8f / (float)preview.getWidth();
				}
				if( preview.getHeight() * scale > height * 0.8f )
				{
					scale = height * 0.8f / (float)preview.getHeight();
				}
				int bitmapWidth = (int)( scale * preview.getWidth() );
				int bitmapHeight = (int)( scale * preview.getHeight() );
				Bitmap bitmap = Bitmap.createScaledBitmap( preview , bitmapWidth , bitmapHeight , true );
				if( !bitmap.equals( preview ) )
					preview.recycle();
				previewRegion = new TextureRegion( new BitmapTexture( bitmap , true ) );
				previewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
			}
		}
		if( DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_icon_shown_workspace_edit_mode == true )
		{
			if( !RR.net_version )
				miuiCellRegion = R3D.findRegion( "miui-widget-indicator" + info.spanX + info.spanY );
		}
	}
}
