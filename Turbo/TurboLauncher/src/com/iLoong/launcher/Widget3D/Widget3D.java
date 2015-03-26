package com.iLoong.launcher.Widget3D;


import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.DragView3D;
import com.iLoong.launcher.Desktop3D.IconBase3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.UI3DEngine.Desktop3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3DHost.Widget3DProvider;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.Widget3DInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


// import com.iLoong.WeatherClock.view.WidgetWeatherClock;
public class Widget3D extends ViewGroup3D implements IconBase3D
{
	
	// WidgetID，每个Widget的标志
	private int widgetId = INVALID_WIDGETID;
	public String packageName;
	private WidgetPluginView3D pluginInstance;
	// Widget长按消息标志
	public static final int MSG_Widget3D_LONGCLICK = 0;
	public static final int INVALID_WIDGETID = -1;
	//处理天气时钟的时候的特殊处理
	public static final int MSG_Widget3D_LONGCLICK_FOR_WEATHERCLOCK = 222;
	public static final String INTENT_UPDATE_WIDGET3D = "com.iLoong.updateWidget3D";
	public Widget3DInfo itemInfo;
	// teapotXu add start
	public static final int WIDGET_ANIMATION_TYPE_ENTRY = 0;
	public static final int WIDGET_ANIMATION_TYPE_SCROLL = 1;
	public static final int WIDGET_ANIMATION_TYPE_ENTRY_FROM_MAINMENU = 2;
	public static final int WIDGET_ANIMATION_DIRECTION_NONE = 0;
	public static final int WIDGET_ANIMATION_DIRECTION_TO_LEFT = 1;
	public static final int WIDGET_ANIMATION_DIRECTION_TO_RIGHT = 2;
	public static final String WIDGET3D_NEED_SCROLL_STR = "widget need scroll";
	private Object tag2;
	// teapotXu add end
	// widget3d视角
	public Camera mCamera;
	public Vector3 oldCameraPosition = new Vector3();
	public Vector3 newCameraPosition = new Vector3();
	public Vector2 widgetPoistion = new Vector2();
	public float posOffsetX = 0;
	public float posOffsetY = 0;
	public float oldX;
	public float oldY;
	public float oldScaleX;
	public float oldScaleY;
	public ViewGroup3D oldViewParent;
	public FrameBuffer fbo = null;
	public boolean folderOpened = false;
	public boolean enableFbo = false;
	public TextureRegion fboRegion = null;
	public Workspace3D workspace = null;
	public boolean snapCompleted = false;
	public HashMap<String , Object> params = null;
	
	public String getPackageName()
	{
		return packageName;
	}
	
	public void setPackageName(
			String packageName )
	{
		this.packageName = packageName;
	}
	
	public int getWidgetId()
	{
		return widgetId;
	}
	
	public void setWidgetId(
			int widgetID )
	{
		widgetId = widgetID;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		isOnLongClick = false;
		if( DefaultLayout.enable_effect_preview )
		{
			if( ( iLoongLauncher.getInstance() != null ) && ( iLoongLauncher.getInstance().getD3dListener() != null ) && ( iLoongLauncher.getInstance().getD3dListener().getRoot() != null ) && ( iLoongLauncher
					.getInstance().getD3dListener().getRoot().isWorkspaceEffectPreviewMode() ) )
			{
				return true;
			}
		}
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		isOnLongClick = false;
		return super.onTouchUp( x , y , pointer );
	}
	
	private void reset()
	{
		this.pluginInstance.reset();
	}
	
	public TextureRegion SnapWidgetShot(
			SpriteBatch batch ,
			float parentAlpha )
	{
		reset();
		batch.flush();
		fbo.begin();
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT );
		oldX = x;
		oldY = y;
		oldScaleX = scaleX;
		oldScaleY = scaleY;
		x = 0;
		y = 0;
		this.scaleX = Gdx.graphics.getWidth() / width;
		this.scaleY = Gdx.graphics.getHeight() / height;
		applyCameraPosition( batch );
		oldViewParent = viewParent;
		viewParent = null;
		super.draw( batch , 1 );
		viewParent = oldViewParent;
		resetCameraPosition( batch );
		x = oldX;
		y = oldY;
		this.scaleX = oldScaleX;
		this.scaleY = oldScaleY;
		fbo.end();
		return fboRegion;
	}
	
	public Desktop3D desktop = null;
	public int snapType = 1; // 0 不绘制fbo， 1 绘制一次 ，-1 持续绘制
	public int fboDepth = 0; // 0 关闭fbo深度缓存 1：打开fbo深度缓存
	private boolean isRequestFocus = false;
	private long lastFboTime = 0;
	private long curFboTime = 0;
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( folderOpened )
		{
			super.draw( batch , parentAlpha );
			snapCompleted = false;
		}
		else
		{
			if( enableFbo )
			{
				if( workspace.xScale != 0 )
				{
					if( !snapCompleted )
					{
						SnapWidgetShot( batch , parentAlpha );
						snapCompleted = true;
					}
					batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
					batch.draw( fboRegion , x , y );
				}
				else
				{
					if( viewParent instanceof DragView3D )
					{
						if( !snapCompleted )
						{
							fboRegion = new TextureRegion( fbo.getColorBufferTexture() , 0 , fbo.getHeight() , fbo.getWidth() , -fbo.getHeight() );
							SnapWidgetShot( batch , parentAlpha );
							snapCompleted = true;
						}
						batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
						batch.draw( fboRegion , x , y );
					}
					else
					{
						applyCameraPosition2( batch );
						super.draw( batch , parentAlpha );
						resetCameraPosition2( batch );
						snapCompleted = false;
					}
				}
			}
			else
			{
				super.draw( batch , parentAlpha );
				snapCompleted = false;
			}
		}
	}
	
	Vector3 vTemp = new Vector3();
	
	public Widget3D(
			String name ,
			WidgetPluginView3D widget )
	{
		super( name );
		this.pluginInstance = widget;
		params = pluginInstance.getParams();
		addView( widget );
		// 这一步必须要做，否则生成的View宽度和高度比较大，点击焦点位置会失灵
		this.width = widget.width;
		this.height = widget.height;
		this.setOrigin( this.width / 2 , this.height / 2 );
		transform = true;
		workspace = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
		if( params != null )
		{
			if( params.containsKey( "enableFbo" ) && params.get( "enableFbo" ).equals( "true" ) )
			{
				enableFbo = true;
			}
			else
			{
				enableFbo = false;
			}
			if( enableFbo )
			{
				if( params.containsKey( "fbo_depth" ) && params.get( "fbo_depth" ).equals( "false" ) )
				{
					fbo = new FrameBuffer( Format.RGBA8888 , (int)( width ) , (int)( height ) , false );
				}
				else
				{
					fbo = new FrameBuffer( Format.RGBA8888 , (int)( width ) , (int)( height ) , true );
				}
				fboRegion = new TextureRegion( fbo.getColorBufferTexture() , 0 , fbo.getHeight() , fbo.getWidth() , -fbo.getHeight() );
			}
		}
	}
	
	@Override
	public View3D clone()
	{
		Widget3D widget = new Widget3D( name , this.pluginInstance );
		widget.widgetId = this.widgetId;
		widget.packageName = this.packageName;
		return widget;
	}
	
	public boolean isOnLongClick = false;
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		isOnLongClick = true;
		// xiatian add start //EffectPreview
		if( DefaultLayout.enable_effect_preview )
		{
			if( ( iLoongLauncher.getInstance() != null ) && ( iLoongLauncher.getInstance().getD3dListener() != null ) && ( iLoongLauncher.getInstance().getD3dListener().getRoot() != null ) && ( iLoongLauncher
					.getInstance().getD3dListener().getRoot().isWorkspaceEffectPreviewMode() ) )
			{
				return true;
			}
		}
		// xiatian add end
		if( !this.isDragging )
		{
			this.toAbsoluteCoords( point );
			this.setTag( new Vector2( point.x , point.y ) );
			point.x = x;
			point.y = y;
			this.toAbsolute( point );
			DragLayer3D.dragStartX = point.x;
			DragLayer3D.dragStartY = point.y;
			Log.v( "launcher" , "onLongClick:" + name + " x:" + point.x + " y:" + point.y );
			return viewParent.onCtrlEvent( this , MSG_Widget3D_LONGCLICK );
		}
		return false;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			return true;
		}
		Log.v( "Widget3D" , "onClick" + name + " x:" + x + " y:" + y );
		// xiatian add start //EffectPreview
		if( DefaultLayout.enable_effect_preview )
		{
			if( ( iLoongLauncher.getInstance() != null ) && ( iLoongLauncher.getInstance().getD3dListener() != null ) && ( iLoongLauncher.getInstance().getD3dListener().getRoot() != null ) && ( iLoongLauncher
					.getInstance().getD3dListener().getRoot().isWorkspaceEffectPreviewMode() ) )
			{
				return true;
			}
		}
		// xiatian add end
		return super.onClick( x , y );
	}
	
	public Widget3DInfo getItemInfo()
	{
		if( itemInfo == null )
		{
			itemInfo = new Widget3DInfo();
			itemInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_WIDGET3D;
			itemInfo.packageName = this.packageName;
			itemInfo.widgetId = this.widgetId;
		}
		Widget3DProvider provider = Widget3DManager.getInstance().getWidget3DProvider( itemInfo.packageName );
		if( provider != null )
		{
			itemInfo.spanX = provider.spanX;
			itemInfo.spanY = provider.spanY;
		}
		// itemInfo.screen = screen;
		itemInfo.x = (int)this.x;
		itemInfo.y = (int)this.y;
		return itemInfo;
	}
	
	@Override
	public void setItemInfo(
			ItemInfo info )
	{
		if( info instanceof Widget3DInfo )
		{
			this.itemInfo = (Widget3DInfo)info;
		}
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		if( sender instanceof WidgetPluginView3D && event_id == MSG_Widget3D_LONGCLICK_FOR_WEATHERCLOCK )
		{
			return viewParent.onCtrlEvent( this , MSG_Widget3D_LONGCLICK_FOR_WEATHERCLOCK );
		}
		if( DefaultLayout.enable_effect_preview )
		{
			if( ( iLoongLauncher.getInstance() != null ) && ( iLoongLauncher.getInstance().getD3dListener() != null ) && ( iLoongLauncher.getInstance().getD3dListener().getRoot() != null ) && ( iLoongLauncher
					.getInstance().getD3dListener().getRoot().isWorkspaceEffectPreviewMode() ) )
			{
				return true;
			}
		}
		if( !this.isDragging )
		{
			this.toAbsoluteCoords( point );
			this.setTag( new Vector2( point.x , point.y ) );
			if( sender.getTag() != null )
			{
				Vector2 vector = (Vector2)sender.getTag();
				DragLayer3D.dragStartX = vector.x;
				DragLayer3D.dragStartY = vector.y;
			}
			isOnLongClick = true;
			return viewParent.onCtrlEvent( this , MSG_Widget3D_LONGCLICK );
		}
		return false;
	}
	
	@Override
	public void hide()
	{
		if( this.pluginInstance.isVisible() )
			this.pluginInstance.hide();
		super.hide();
	}
	
	@Override
	public void show()
	{
		if( !this.pluginInstance.isVisible() )
			this.pluginInstance.show();
		super.show();
	}
	
	public boolean isOpened()
	{
		return this.pluginInstance.isOpened();
	}
	
	public void onDelete()
	{
		this.pluginInstance.onDelete();
	}
	
	public void onChangeSize(
			float moveX ,
			float moveY ,
			int what ,
			int cellX ,
			int cellY )
	{
		this.pluginInstance.onChangeSize( moveX , moveY , what , cellX , cellY );
	}
	
	public void onCellMove(
			int cellX ,
			int cellY )
	{
		this.pluginInstance.onCellMove( cellX , cellY );
	}
	
	public void onStart()
	{
		this.pluginInstance.onStart();
	}
	
	public void onResume()
	{
		this.pluginInstance.onResume();
	}
	
	public void onPause()
	{
		this.pluginInstance.onPause();
	}
	
	public void onStop()
	{
		this.pluginInstance.onStop();
	}
	
	public void onDestroy()
	{
		this.pluginInstance.onDestroy();
	}
	
	public void onKeyEvent(
			int keycode ,
			int keyEventCode )
	{
		this.pluginInstance.onKeyEvent( keycode , keyEventCode );
	}
	
	public void dispose()
	{
		super.dispose();
		this.pluginInstance.dispose();
		if( fboRegion != null && fboRegion.getTexture() != null )
		{
			fboRegion.getTexture().dispose();
			fboRegion = null;
		}
		if( fbo != null )
		{
			fbo.dispose();
			fbo = null;
		}
	}
	
	public void onUninstall()
	{
		this.pluginInstance.onUninstall();
	}
	
	public WidgetPluginViewMetaData getPluginViewMetaData()
	{
		return pluginInstance.getPluginViewMetaData();
	}
	
	// teapotXu add start for doov customization
	public void setWidgetTag2(
			Object obj )
	{
		this.tag2 = obj;
	}
	
	public Object getWidgetTag2()
	{
		return this.tag2;
	}
	
	public boolean onStartWidgetAnimation(
			Object input_obj ,
			int widgetAnimType ,
			int widgetAnimDirection )
	{
		return this.pluginInstance.onStartWidgetAnimation( input_obj , widgetAnimType , widgetAnimDirection );
	}
	
	// teapotXu add end
	// widget3d视角
	private void applyCameraPosition2(
			SpriteBatch batch )
	{
		mCamera = Desktop3DListener.d3d.getCamera();
		oldCameraPosition.set( mCamera.position );
		toAbsoluteCoords( widgetPoistion );
		newCameraPosition.x = widgetPoistion.x + ( width * viewParent.scaleX ) / 2;
		newCameraPosition.y = widgetPoistion.y + ( height * viewParent.scaleY ) / 2;
		newCameraPosition.z = oldCameraPosition.z;
		posOffsetX = newCameraPosition.x - oldCameraPosition.x;
		posOffsetY = newCameraPosition.y - oldCameraPosition.y;
		mCamera.position.set( newCameraPosition );
		mCamera.update();
		batch.end();
		Gdx.gl.glViewport( (int)posOffsetX , (int)posOffsetY , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
		batch.setProjectionMatrix( mCamera.combined );
		batch.begin();
	}
	
	// widget3d视角
	private void applyCameraPosition(
			SpriteBatch batch )
	{
		mCamera = Desktop3DListener.d3d.getCamera();
		oldCameraPosition.set( mCamera.position );
		// toAbsoluteCoords(widgetPoistion);
		newCameraPosition.x = x + this.width / 2;
		newCameraPosition.y = y + this.height / 2;
		newCameraPosition.z = oldCameraPosition.z;
		posOffsetX = newCameraPosition.x - oldCameraPosition.x;
		posOffsetY = newCameraPosition.y - oldCameraPosition.y;
		mCamera.position.set( newCameraPosition );
		mCamera.update();
		batch.end();
		// Gdx.gl.glViewport((int) posOffsetX, (int) posOffsetY,
		// Utils3D.getScreenWidth(), Utils3D.getScreenHeight());
		batch.setProjectionMatrix( mCamera.combined );
		batch.begin();
	}
	
	private void resetCameraPosition2(
			SpriteBatch batch )
	{
		mCamera.position.set( oldCameraPosition );
		mCamera.update();
		batch.end();
		Gdx.gl.glViewport( 0 , 0 , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
		batch.setProjectionMatrix( mCamera.combined );
		batch.begin();
	}
	
	private void resetCameraPosition(
			SpriteBatch batch )
	{
		mCamera.position.set( oldCameraPosition );
		mCamera.update();
		batch.end();
		// Gdx.gl.glViewport(0, 0, Utils3D.getScreenWidth(),
		// Utils3D.getScreenHeight());
		batch.setProjectionMatrix( mCamera.combined );
		batch.begin();
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( DefaultLayout.enable_effect_preview )
		{
			if( ( iLoongLauncher.getInstance() != null ) && ( iLoongLauncher.getInstance().getD3dListener() != null ) && ( iLoongLauncher.getInstance().getD3dListener().getRoot() != null ) && ( iLoongLauncher
					.getInstance().getD3dListener().getRoot().isWorkspaceEffectPreviewMode() ) )
			{
				return true;
			}
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	public void onThemeChanged()
	{
		Widget3DTheme theme3D = new Widget3DTheme();
		theme3D.setWidgetThemeConfig( "/widget/config.xml" );
		if( pluginInstance != null )
		{
			if( pluginInstance.appContext != null )
			{
				this.pluginInstance.appContext.mThemeName = theme3D.getWidget3DThemeName( packageName , ThemeManager.getInstance().getCurrentThemeDescription().componentName.getPackageName() );
			}
			this.pluginInstance.onThemeChanged();
		}
	}
	
	public WidgetPluginView3D getWidgetPluginView3D()
	{
		return this.pluginInstance;
	}
}
