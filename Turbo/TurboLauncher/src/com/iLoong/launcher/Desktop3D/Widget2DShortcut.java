package com.iLoong.launcher.Desktop3D;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooee.android.launcher.framework.LauncherModel;
import com.iLoong.RR;
import com.iLoong.launcher.DesktopEdit.DesktopEditHost;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.data.WidgetShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.miui.MIUIWidgetList;
import com.iLoong.launcher.theme.ThemeManager;


public class Widget2DShortcut extends View3D
{
	
	public WidgetShortcutInfo widgetInfo;
	private TextureRegion titleRegion;
	private TextureRegion[] cellRegion = new TextureRegion[2];
	private TextureRegion miuiCellRegion;
	private String title;
	private String cellTitle;
	public boolean isDragView = false;
	public int oldAppGridIndex;
	public int newAppGridIndex;
	public float oldX;
	public float oldY;
	public boolean oldVisible = false;
	protected boolean hide = false;
	//ImageView3D resumeImage;
	//ImageView3D hideImage;
	public static TextureRegion bg;
	public static TextureRegion xRegion;
	private float longClickX;
	private float longClickY;
	private static NinePatch widget_holo = null;
	public static final int MSG_WIDGET_SHORTCUT_LONGCLICK = 0;
	public static final int MSG_WIDGET_SHORTCUT_CLICK = 1;
	protected boolean widget2d_shown_in_workspace_edit_mode = false;
	private Texture widget_holoTexture = null;
	
	public Widget2DShortcut(
			String name )
	{
		super( name );
		// TODO Auto-generated constructor stub
	}
	
	public Widget2DShortcut(
			String name ,
			TextureRegion region )
	{
		super( name , region );
		if( bg == null )
			bg = R3D.findRegion( "widget-shortcut-bg" );
		if( xRegion == null )
			xRegion = R3D.findRegion( "x" );
		if( widget_holo == null )
		{
			Bitmap bm = ThemeManager.getInstance().getBitmap( "theme/pack_source/widget_holo.png" );
			widget_holoTexture = new BitmapTexture( bm , true );
			widget_holo = new NinePatch( new TextureRegion( widget_holoTexture ) , 3 , 3 , 3 , 3 );
			bm.recycle();
			bm = null;
			if( !iLoongLauncher.releaseTexture )
			{
			}
		}
	}
	
	public boolean getWidget2DShortcutShownPlace()
	{
		return widget2d_shown_in_workspace_edit_mode;
	}
	
	public void setWidget2DShortcutShownPlace(
			boolean is_shown_worksapce_edit_mode )
	{
		widget2d_shown_in_workspace_edit_mode = is_shown_worksapce_edit_mode;
	}
	
	public void setInfo(
			WidgetShortcutInfo widgetInfo )
	{
		this.widgetInfo = widgetInfo;
		this.title = widgetInfo.label;
		this.cellTitle = widgetInfo.cellHSpan + "x" + widgetInfo.cellVSpan;
		if( !isDragView )
		{
			if( titleRegion == null )
			{
				int titleWidth = (int)( width * 3 / 4 );
				int alignH = AppBar3D.TEXT_ALIGN_LEFT;
				int titleHeight = (int)( R3D.widget_preview_title_weight * height );
				if( !RR.net_version && DefaultLayout.enable_workspace_miui_edit_mode == true && widget2d_shown_in_workspace_edit_mode == true )
				{
					titleWidth = (int)( width );
					alignH = AppBar3D.TEXT_ALIGN_CENTER;
					titleHeight = MIUIWidgetList.getSingleLineHeight();
				}
				titleRegion = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( title , titleWidth , titleHeight , alignH , AppBar3D.TEXT_ALIGN_CENTER , true , Color.WHITE ) , true ) );
			}
			//				titleRegion = new TextureRegion(new BitmapTexture(AppBar3D.titleToPixmap(title, (int)(width*3/4), 
			//						(int)(R3D.widget_preview_title_weight*height),AppBar3D.TEXT_ALIGN_LEFT,AppBar3D.TEXT_ALIGN_CENTER,true,Color.WHITE),true));
			if( cellRegion[0] == null )
			{
				cellRegion[0] = R3D.findRegion( widgetInfo.cellHSpan + "" );
				cellRegion[1] = R3D.findRegion( widgetInfo.cellVSpan + "" );
			}
		}
		if( DefaultLayout.enable_workspace_miui_edit_mode == true && widget2d_shown_in_workspace_edit_mode == true )
		{
			if( !RR.net_version )
				if( miuiCellRegion == null )
				{
					miuiCellRegion = R3D.findRegion( "miui-widget-indicator" + widgetInfo.cellHSpan + widgetInfo.cellVSpan );
				}
		}
	}
	
	@Override
	public void setSize(
			float w ,
			float h )
	{
		super.setSize( w , h );
		this.setOrigin( w / 2 , h / 2 );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( region.getTexture() == null )
		{
			Log.d( "launcher" , "name=" + name );
			return;
		}
		float alpha = color.a;
		if( widgetInfo.isHide && hide )
		{
			alpha *= 0.2;
		}
		batch.setColor( color.r , color.g , color.b , alpha * parentAlpha );
		float previewX = width / 2 - region.getRegionWidth() / 2;
		float previewY = height * ( 1 - R3D.widget_preview_title_weight ) / 2 - region.getRegionHeight() / 2 + height * R3D.widget_preview_title_weight;
		if( DefaultLayout.widget_shortcut_title_top )
		{
			previewY = previewY - height * R3D.widget_preview_title_weight;
		}
		if( DefaultLayout.widget_shortcut_lefttop )
		{
			previewX = width / 20;
			previewY = height - height * ( 1 - R3D.widget_preview_title_weight ) / 20 - region.getRegionHeight() * scaleY;
			if( DefaultLayout.widget_shortcut_title_top )
			{
				previewY = previewY - height * R3D.widget_preview_title_weight;
			}
		}
		if( DefaultLayout.enable_workspace_miui_edit_mode == true && widget2d_shown_in_workspace_edit_mode == true )
		{
			previewY = R3D.miui_widget_indicator_height + MIUIWidgetList.getSingleLineHeight();
			previewY += ( height - previewY - region.getRegionHeight() ) / 2;
		}
		//		if (is3dRotation()){
		//			if(!isDragView)batch.draw(region, previewX+x, previewY+y);
		//			else batch.draw(region, x, y,width,height);
		//		}
		//			
		//		else{
		if( !isDragView )
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
			batch.draw( region , previewX + x , previewY + y , originX , originY , region.getRegionWidth() , region.getRegionHeight() , scaleX , scaleY , rotation );
		}
		else
		{
			if( widgetInfo.isShortcut )
			{
				batch.draw( region , x , y , originX , originY , region.getRegionWidth() , region.getRegionHeight() , scaleX , scaleY , rotation );
			}
			else
			{
				batch.draw( region , x , y , originX , originY , width , height , scaleX , scaleY , rotation );
			}
			if( widget_holo != null )
			{
				int holo_w = widgetInfo.cellHSpan * R3D.workspace_cell_width;
				int holo_h = widgetInfo.cellVSpan * R3D.workspace_cell_height;
				float holo_x = x + ( width - holo_w ) / 2;
				float holo_y = y + ( height - holo_h ) / 2;
				widget_holo.draw( batch , holo_x , holo_y , holo_w , holo_h );
			}
		}
		//		}
		if( titleRegion != null )
		{
			//			if (is3dRotation())
			//				batch.draw(titleRegion, x, y);
			//			else
			if( DefaultLayout.widget_shortcut_title_top )
			{
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
				if( DefaultLayout.enable_workspace_miui_edit_mode == true && widget2d_shown_in_workspace_edit_mode == true )
				{
					batch.draw(
							titleRegion ,
							x ,
							y + R3D.miui_widget_indicator_height ,
							originX ,
							originY ,
							titleRegion.getRegionWidth() * 1.5f ,
							titleRegion.getRegionHeight() * 1.5f ,
							scaleX ,
							scaleY ,
							rotation );
				}
				else
				{
					batch.draw( titleRegion , x , y , originX , originY , titleRegion.getRegionWidth() , titleRegion.getRegionHeight() , scaleX , scaleY , rotation );
				}
			}
		}
		if( !RR.net_version && DefaultLayout.enable_workspace_miui_edit_mode == true && widget2d_shown_in_workspace_edit_mode == true )
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
		else
		{
			if( cellRegion[0] != null )
			{
				float titleX = 10;
				titleX = (float)( width / 4 - 2 * cellRegion[0].getRegionWidth() - xRegion.getRegionWidth() ) - titleX;
				if( titleX < 0 )
					titleX = 0;
				if( DefaultLayout.widget_shortcut_title_top )
				{
					batch.draw(
							cellRegion[0] ,
							width * 3 / 4 + x + titleX ,
							y + ( ( R3D.widget_preview_title_weight * height ) - cellRegion[0].getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) ,
							originX ,
							originY ,
							cellRegion[0].getRegionWidth() ,
							cellRegion[0].getRegionHeight() ,
							scaleX ,
							scaleY ,
							rotation );
					batch.draw(
							xRegion ,
							width * 3 / 4 + x + titleX + cellRegion[0].getRegionWidth() ,
							y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) ,
							originX ,
							originY ,
							xRegion.getRegionWidth() ,
							xRegion.getRegionHeight() ,
							scaleX ,
							scaleY ,
							rotation );
					batch.draw(
							cellRegion[1] ,
							width * 3 / 4 + x + titleX + cellRegion[0].getRegionWidth() + xRegion.getRegionWidth() ,
							y + ( ( R3D.widget_preview_title_weight * height ) - cellRegion[1].getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) ,
							originX ,
							originY ,
							cellRegion[1].getRegionWidth() ,
							cellRegion[1].getRegionHeight() ,
							scaleX ,
							scaleY ,
							rotation );
				}
				else
				{
					if( DesktopEditHost.getPopMenuStyle() == 1 )
					{
						batch.draw(
								cellRegion[0] ,
								width * 3 / 4 + x + titleX - 25 * ( Utils3D.getScreenWidth() / 720f ) ,
								y + ( ( R3D.widget_preview_title_weight * height ) - cellRegion[0].getRegionHeight() ) / 2 ,
								originX ,
								originY ,
								cellRegion[0].getRegionWidth() * 1.5f ,
								cellRegion[0].getRegionHeight() * 1.5f ,
								scaleX ,
								scaleY ,
								rotation );
						batch.draw(
								xRegion ,
								width * 3 / 4 + x + titleX + cellRegion[0].getRegionWidth() - 14 * ( Utils3D.getScreenWidth() / 720f ) ,
								y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 ,
								originX ,
								originY ,
								xRegion.getRegionWidth() * 1.5f ,
								xRegion.getRegionHeight() * 1.5f ,
								scaleX ,
								scaleY ,
								rotation );
						batch.draw(
								cellRegion[1] ,
								width * 3 / 4 + x + titleX + cellRegion[0].getRegionWidth() + xRegion.getRegionWidth() - 5 * ( Utils3D.getScreenWidth() / 720f ) ,
								y + ( ( R3D.widget_preview_title_weight * height ) - cellRegion[1].getRegionHeight() ) / 2 ,
								originX ,
								originY ,
								cellRegion[1].getRegionWidth() * 1.5f ,
								cellRegion[1].getRegionHeight() * 1.5f ,
								scaleX ,
								scaleY ,
								rotation );
					}
					else
					{
						batch.draw(
								cellRegion[0] ,
								width * 3 / 4 + x + titleX ,
								y + ( ( R3D.widget_preview_title_weight * height ) - cellRegion[0].getRegionHeight() ) / 2 ,
								originX ,
								originY ,
								cellRegion[0].getRegionWidth() ,
								cellRegion[0].getRegionHeight() ,
								scaleX ,
								scaleY ,
								rotation );
						batch.draw(
								xRegion ,
								width * 3 / 4 + x + titleX + cellRegion[0].getRegionWidth() ,
								y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 ,
								originX ,
								originY ,
								xRegion.getRegionWidth() ,
								xRegion.getRegionHeight() ,
								scaleX ,
								scaleY ,
								rotation );
						batch.draw(
								cellRegion[1] ,
								width * 3 / 4 + x + titleX + cellRegion[0].getRegionWidth() + xRegion.getRegionWidth() ,
								y + ( ( R3D.widget_preview_title_weight * height ) - cellRegion[1].getRegionHeight() ) / 2 ,
								originX ,
								originY ,
								cellRegion[1].getRegionWidth() ,
								cellRegion[1].getRegionHeight() ,
								scaleX ,
								scaleY ,
								rotation );
					}
				}
			}
		}
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		Log.v( "Widget2DShortcut" , "onClick:x,y=" + x + " " + y );
		if( hide )
		{
			if( !widgetInfo.textureName.equals( "" ) )
			{
				widgetInfo.isHide = !widgetInfo.isHide;
				PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().putBoolean( "HIDE:" + widgetInfo.textureName , widgetInfo.isHide ).commit();
			}
			return true;
		}
		viewParent.onCtrlEvent( this , MSG_WIDGET_SHORTCUT_CLICK );
		//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "AddSysWidget" , widgetInfo.component.getPackageName() );
		return true;
	}
	
	public boolean onLongClick(
			float x ,
			float y )
	{
		Log.v( "click" , " widgetShortcut onLongClick:" + name + " x:" + x + " y:" + y );
		//		if( !this.isDragging )
		//		{
		//			point.x = width / 2;
		//			point.y = height / 2;
		//			this.toAbsolute( point );
		//			this.setTag( new Vector2( point.x , point.y ) );
		//			point.x = x;
		//			point.y = y;
		//			this.toAbsolute( point );
		//			longClickX = point.x;
		//			longClickY = point.y;
		//			DragLayer3D.dragStartX = point.x;
		//			DragLayer3D.dragStartY = point.y;
		//			return viewParent.onCtrlEvent( this , MSG_WIDGET_SHORTCUT_LONGCLICK );
		//		}
		return true;
	}
	
	/**
	 * �ж��Ƿ���Լ������widget
	 * 
	 * @param resolveInfo
	 * @return
	 */
	public boolean canAddWidget()
	{
		if( DefaultLayout.enable_allow_add_more_widgets_to_desktop )
			return true;
		if( this.widgetInfo == null || this.widgetInfo.component == null )
			return false;
		if( LauncherModel.isWidgetExists( iLoongLauncher.getInstance() , this.widgetInfo.component.getPackageName() , this.widgetInfo.component.getClassName() ) )
		{
			return false;
		}
		return true;
	}
	
	public Widget2DShortcut createDragView()
	{
		Widget2DShortcut widget = new Widget2DShortcut( name , region );
		//widget.setSize(widgetInfo.cellHSpan*R3D.workspace_cell_width, widgetInfo.cellVSpan*R3D.workspace_cell_height);
		widget.setPosition( longClickX - widget.width / 2 , longClickY - widget.height / 2 );
		widget.isDragView = true;
		widget.setInfo( widgetInfo );
		return widget;
	}
	
	public void showHide()
	{
		hide = true;
	}
	
	public void clearState()
	{
		hide = false;
	}
	
	@Override
	public void releaseRegion()
	{
		// TODO Auto-generated method stub
		AppList3D.allmemnum += region.getRegionHeight() * region.getRegionWidth() * 4 / 1024;
		Log.v( "" , "releaseRegion name is " + name + " allmemnum is " + AppList3D.allmemnum );
		super.releaseRegion();
		if( titleRegion != null && titleRegion.getTexture() != null )
		{
			AppList3D.allmemnum += titleRegion.getRegionHeight() * titleRegion.getRegionWidth() * 4 / 1024;
			Log.v( "" , "releaseRegion name is titleRegion allmemnum is " + AppList3D.allmemnum );
			titleRegion.getTexture().dispose();
			titleRegion = null;
		}
		// if (cellRegion != null) {
		// for (int i = 0; i < cellRegion.length; i++) {
		// if (cellRegion[i] != null && cellRegion[i].getTexture() != null) {
		// cellRegion[i].getTexture().dispose();
		// cellRegion[i] = null;
		// }
		// }
		// }
		//		if (miuiCellRegion != null && miuiCellRegion.getTexture() != null) {
		//			miuiCellRegion.getTexture().dispose();
		//			miuiCellRegion = null;
		//		}
		// if (xRegion != null && xRegion.getTexture() != null) {
		// xRegion.getTexture().dispose();
		// xRegion = null;
		// }
		// if (bg != null && bg.getTexture() != null) {
		// bg.getTexture().dispose();
		// bg = null;
		// }
		if( widget_holo != null && widget_holoTexture != null )
		{
			AppList3D.allmemnum += widget_holoTexture.getHeight() * widget_holoTexture.getWidth() * 4 / 1024;
			Log.v( "" , "releaseRegion name is widget_holoTexture allmemnum is " + AppList3D.allmemnum );
			widget_holoTexture.dispose();
			widget_holoTexture = null;
			widget_holo = null;
		}
		cellRegion = null;
		miuiCellRegion = null;
		bg = null;
		xRegion = null;
	}
	
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		Log.v( "" , " appwidgetmey Widget2DShortcutonTouchUp" );
		return super.onTouchUp( x , y , pointer );
	}
}
