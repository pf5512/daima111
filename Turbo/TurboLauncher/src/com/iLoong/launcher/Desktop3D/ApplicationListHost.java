package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.RR;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ConfigBase;
import com.iLoong.launcher.UI3DEngine.TextField3D;
import com.iLoong.launcher.UI3DEngine.TextFieldListener;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class ApplicationListHost extends ViewGroup3D implements TextFieldListener
{
	
	public static final int MSG_VIEW_TO_ADD_FOLDER = 1;
	public static final int MSG_FOCUS_FOLDER = 2;
	public static final int MSG_NEW_FOLDER_IN_APPLIST = 3;
	public static float scaleFactor;
	public static float buttonHeight = 50;
	public static int bottomPadding = 45;
	public static int topPadding = 50;
	public static int leftPadding = 15;
	private NinePatch m_bgNinePatch;
	private float m_bgWidth;
	private float m_bgHeight;
	private TextField3D m_editableTilte;
	private int m_titleBarHeight = 100;
	private int m_titleWidth = 330;
	private int m_titleHeight = 50;
	private static TextureRegion m_dividerRegion;
	private ButtonView3D m_buttonCancel;
	private ButtonView3D m_buttonOK;
	private ButtonView3D m_buttonSort;
	private ApplicationList m_applicationList;
	private int buttonPadding = 5;
	private float translucentBg_alpha = 0;
	public boolean bNewFolderInApplist;
	public boolean bNewFolderInWorkspace;
	public static String folderName;
	private Vector2 m_point = new Vector2();
	private NinePatch bg;
	private Tween folderShowTween = null;
	private ArrayList<View3D> popupView3DList = null;
	
	// ArrayList<View3D> arr=null;
	public ApplicationListHost(
			String name )
	{
		super( name );
		this.transform = true;
		scaleFactor = Utils3D.getScreenWidth() / 720f;
		float listWidth = UtilsBase.getScreenWidth();
		float listHeight = UtilsBase.getScreenHeight();
		setSize( listWidth , listHeight );
		buttonHeight *= Gdx.graphics.getDensity();
		bottomPadding *= Gdx.graphics.getDensity();
		topPadding *= Gdx.graphics.getDensity();
		leftPadding *= Gdx.graphics.getDensity();
		buttonPadding *= Gdx.graphics.getDensity();
		m_titleBarHeight *= scaleFactor;
		m_titleWidth *= scaleFactor;
		m_titleHeight *= scaleFactor;
		if( m_bgNinePatch == null )
		{
			m_bgNinePatch = new NinePatch( R3D.getTextureRegion( R3D.folder_bg_name ) , 20 , 20 , 20 , 20 );
		}
		m_bgWidth = this.width - leftPadding * 2;
		m_bgHeight = this.height - topPadding - bottomPadding;
		Paint paint = new Paint();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		paint.setTextSize( m_titleHeight - 10 * scaleFactor );
		m_editableTilte = new TextField3D( "editableTilte" , m_titleWidth , m_titleHeight , paint );
		m_editableTilte.setPosition(
				this.x + ( this.width - m_titleWidth ) / 2 ,
				( this.y + bottomPadding ) + ( m_bgHeight - m_titleBarHeight ) + ( m_titleBarHeight - m_editableTilte.getHeight() ) / 2 );
		m_editableTilte.setOrigin( m_editableTilte.getWidth() / 2 , m_editableTilte.getHeight() / 2 );
		m_editableTilte.setText( R3D.folder3D_name );
		m_editableTilte.setTextFieldListener( this );
		m_editableTilte.setKeyboardAdapter( null );
		m_editableTilte.setEditable( true );
		m_editableTilte.show();
		if( m_dividerRegion == null )
		{
			m_dividerRegion = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/folder/folder_divider.9.png" ) , true ) );
		}
		if( m_buttonSort == null )
		{
			m_buttonSort = new ButtonView3D( "button_sort" , "theme/folder/floder_button_sort.png" , "theme/folder/floder_button_sort_focus.png" , null );
		}
		m_buttonSort.setPosition( listWidth - leftPadding * 2 - m_buttonSort.getWidth() , this.height - topPadding - m_titleBarHeight + ( m_titleBarHeight - m_buttonSort.height ) / 2 );
		if( m_buttonCancel == null )
		{
			m_buttonCancel = new ButtonView3D( "button_cancel" , null , null , R3D.getString( RR.string.cancel_action ) );
		}
		m_buttonCancel.setSize( ( listWidth - 2 * leftPadding - 2 * buttonPadding ) / 2 , buttonHeight - buttonPadding );
		m_buttonCancel.setPosition( leftPadding + buttonPadding , bottomPadding + buttonPadding );
		if( m_buttonOK == null )
		{
			m_buttonOK = new ButtonView3D( "button_ok" , null , null , R3D.getString( RR.string.rename_action ) );
		}
		m_buttonOK.setSize( ( listWidth - 2 * leftPadding - 2 * buttonPadding ) / 2 , buttonHeight - buttonPadding );
		m_buttonOK.setPosition( leftPadding + buttonPadding + m_buttonCancel.width , bottomPadding + buttonPadding );
		if( m_applicationList == null )
		{
			m_applicationList = new ApplicationList( "folder-applist" , m_bgWidth , m_bgHeight - m_titleBarHeight - buttonHeight );
		}
		m_applicationList.x = leftPadding;
		m_applicationList.y = buttonHeight + bottomPadding;
		bg = new NinePatch( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/translucent-bg.png" ) , true ) , 2 , 2 , 2 , 2 );
		addView( m_editableTilte );
		addView( m_buttonSort );
		addView( m_buttonCancel );
		addView( m_buttonOK );
		addView( m_applicationList );
		super.hide();
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		m_point.x = originX - scaleX * ( originX - x );
		m_point.y = originY - scaleY * ( originY - y );
		batch.setColor( color.r , color.g , color.b , 1f );
		bg.draw( batch , 0 , 0 , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
		batch.setColor( color.r , color.g , color.b , 1f );
		m_bgNinePatch.draw( batch , m_point.x + leftPadding * scaleX , m_point.y + bottomPadding * scaleY , (int)( m_bgWidth * scaleX ) , (int)( m_bgHeight * scaleY ) );
		batch.end();
		batch.begin();
		Gdx.gl.glEnable( GL10.GL_SCISSOR_TEST );
		Gdx.gl.glScissor( (int)x + leftPadding + 5 , (int)y + bottomPadding , (int)width - ( leftPadding + 5 ) * 2 , (int)height - bottomPadding - topPadding );
		super.draw( batch , parentAlpha );
		if( m_dividerRegion != null )
		{
			batch.setColor( color.r , color.g , color.b , color.a );
			batch.draw(
					m_dividerRegion ,
					m_point.x + ( leftPadding + 25 * scaleFactor ) * scaleX ,
					m_point.y + ( this.height - topPadding - m_titleBarHeight ) * scaleY ,
					( m_bgWidth - 2 * 25 * scaleFactor ) * scaleX ,
					m_dividerRegion.getRegionHeight() * scaleY );
			batch.draw(
					m_dividerRegion ,
					m_point.x + ( leftPadding + 25 * scaleFactor ) * scaleX ,
					m_point.y + ( buttonHeight + bottomPadding ) * scaleY ,
					( m_bgWidth - 2 * 25 * scaleFactor ) * scaleX ,
					m_dividerRegion.getRegionHeight() * scaleY );
			batch.draw(
					m_dividerRegion ,
					leftPadding + buttonPadding + m_buttonOK.width ,
					m_point.y + ( bottomPadding + 15 * scaleFactor ) * scaleY ,
					m_dividerRegion.getRegionWidth() * scaleX ,
					( buttonHeight - 2 * 10 * scaleFactor ) * scaleY );
		}
		Gdx.gl.glDisable( GL10.GL_SCISSOR_TEST );
	}
	
	public void showAndSelect(
			String titleName ,
			final ArrayList<View3D> list ,
			float oriX ,
			float oriY )
	{
		// TODO Auto-generated method stub
		ConfigBase.disable_double_click = true;
		if( titleName.endsWith( "x.z" ) )
		{
			int length = titleName.length();
			if( length > 3 )
			{
				titleName = titleName.substring( 0 , length - 3 );
			}
		}
		m_editableTilte.setText( titleName );
		popupView3DList = list;
		super.show();
		setScale( 0f , 0f );
		setOrigin( oriX , oriY );
		stopTween();
		folderShowTween = startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.2f , 1 , 1 , 0 ).setCallback( this );
		translucentBg_alpha = 1f;
	}
	
	public void forceSyncAppsPage()
	{
		if( m_applicationList != null )
		{
			m_applicationList.forceSyncAppsPage();
		}
	}
	
	public void sortApp(
			int checkId )
	{
		if( m_applicationList.sortId != checkId )
		{
			m_applicationList.sortApp( checkId , true );
		}
	}
	
	@Override
	public void hide()
	{
		// TODO Auto-generated method stub
		ConfigBase.disable_double_click = false;
		releaseFocus();
		m_applicationList.removeAllChild( m_applicationList.getPageNum() );
		stopTween();
		startTween( View3DTweenAccessor.SCALE_XY , Cubic.IN , 0.2f , 0 , 0 , 0 ).setCallback( this );
		startTween( View3DTweenAccessor.USER , Cubic.OUT , 0.2f , 0 , 0 , 0 );
	}
	
	@Override
	public void setUser(
			float value )
	{
		translucentBg_alpha = value;
	}
	
	@Override
	public float getUser()
	{
		return translucentBg_alpha;
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( type == TweenCallback.COMPLETE && source != folderShowTween )
		{
			super.hide();
			translucentBg_alpha = 0f;
		}
		if( type == TweenCallback.COMPLETE && source == folderShowTween )
		{
			if( !iLoongApplication.getInstance().getModel().appListLoaded )
			{
				int marginTop = (int)( ( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() ) / 2 );
				SendMsgToAndroid.showCustomDialog( (int)( Utils3D.getScreenWidth() - 40 * iLoongLauncher.getInstance().getResources().getDisplayMetrics().density ) / 2 , marginTop );
				iLoongApplication.getInstance().getModel().loadAppList();
				iLoongLauncher.getInstance().postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						m_applicationList.syncAppsPage( popupView3DList );
						SendMsgToAndroid.cancelCustomDialog();
					}
				} );
			}
			else
			{
				int marginTop = (int)( ( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() ) / 2 );
				SendMsgToAndroid.showCustomDialog( (int)( Utils3D.getScreenWidth() - 40 * iLoongLauncher.getInstance().getResources().getDisplayMetrics().density ) / 2 , marginTop );
				m_applicationList.syncAppsPage( popupView3DList );
				SendMsgToAndroid.cancelCustomDialog();
			}
		}
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK || keycode == KeyEvent.KEYCODE_MENU )
			return true;
		return super.keyDown( keycode );
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			if( m_applicationList != null && m_applicationList.getX() != 0 )
				return true;
			this.releaseFocus();
			viewParent.onCtrlEvent( this , MSG_FOCUS_FOLDER );
			this.hide();
		}
		return true;
	}
	
	@Override
	public boolean keyTyped(
			char character )
	{
		// TODO Auto-generated method stub
		boolean ret = super.keyTyped( character );
		if( character == '\n' || character == '\0' )
		{
			m_editableTilte.hideInputKeyboard();
		}
		return ret;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		if( pointer > 0 )
		{
			return false;
		}
		requestFocus();
		if( DefaultLayout.enable_new_particle )
		{
			Desktop3DListener.root.particleSetRepeatPosition( this.x , this.y , x , y );
		}
		View3D hitView = hit( x , y );
		if( hitView != null )
		{
			if( hitView.name.equals( m_applicationList.name ) )
			{
				releaseFocus();
				return super.onTouchDown( x , y , pointer );
			}
			if( hitView.name.equals( m_editableTilte.name ) )
			{
				return m_editableTilte.onTouchDown( x - m_editableTilte.x , y , pointer );
			}
			if( hitView.name.equals( "button_cancel" ) || hitView.name.equals( "button_ok" ) )
			{
				return super.onTouchDown( x , y , pointer );
			}
			return super.onTouchDown( x , y , pointer );
		}
		else
		{
			return true;
		}
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( DefaultLayout.enable_new_particle )
		{
			Desktop3DListener.root.particleScrollRefresh( this.x , this.y , x , y );
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		// TODO Auto-generated method stub
		if( sender instanceof ButtonView3D )
		{
			switch( event_id )
			{
				case ButtonView3D.MSG_VIEW_HIDE:
					if( m_applicationList != null && m_applicationList.getX() != 0 )
						break;
					this.releaseFocus();
					viewParent.onCtrlEvent( this , MSG_FOCUS_FOLDER );
					this.hide();
					break;
				case ButtonView3D.MSG_VIEW_TO_ADD_FOLDER:
					if( m_applicationList != null && m_applicationList.getX() != 0 )
						break;
					this.releaseFocus();
					this.hide();
					viewParent.onCtrlEvent( this , MSG_FOCUS_FOLDER );
					viewParent.onCtrlEvent( this , MSG_VIEW_TO_ADD_FOLDER );
					break;
				case ButtonView3D.MSG_NEW_FOLDER_IN_APPLIST:
					if( m_applicationList != null && m_applicationList.getX() != 0 )
					{
						bNewFolderInApplist = true;
						break;
					}
					this.releaseFocus();
					this.hide();
					viewParent.onCtrlEvent( this , MSG_NEW_FOLDER_IN_APPLIST );
					break;
				case ButtonView3D.MSG_NEW_FOLDER_IN_WORKSPACE:
					if( m_applicationList != null && m_applicationList.getX() != 0 )
						break;
					this.releaseFocus();
					this.hide();
					iLoongLauncher.getInstance().addNewFolder( ApplicationList.mSelected );
					break;
			}
			return true;
		}
		return super.onCtrlEvent( sender , event_id );
	}
	
	public ArrayList<View3D> listToAdd()
	{
		return m_applicationList.mSelected;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		if( pointer > 0 )
		{
			return false;
		}
		View3D hitView = hit( x , y );
		if( hitView != null )
		{
			if( hitView.name.equals( m_editableTilte.name ) )
			{
				m_editableTilte.showInputKeyboard();
				return true;
			}
			if( hitView.name.equals( "button_cancel" ) || hitView.name.equals( "button_ok" ) || hitView.name.equals( "title" ) )
			{
				m_applicationList.onTouchUp( x , y , pointer );
				return super.onTouchUp( x , y , pointer );
			}
			return super.onTouchUp( x , y , pointer );
		}
		else
		{
			m_applicationList.onTouchUp( x , y , pointer );
			return true;
		}
	}
	
	class ButtonView3D extends View3D
	{
		
		private TextureRegion bgNormalRegion = null;
		private TextureRegion bgFocusRegion = null;
		private TextureRegion titleRegion = null;
		private float titleTopOffset = 0;
		public static final int MSG_VIEW_HIDE = 0;
		public static final int MSG_VIEW_TO_ADD_FOLDER = 1;
		public static final int MSG_NEW_FOLDER_IN_APPLIST = 2;
		public static final int MSG_NEW_FOLDER_IN_WORKSPACE = 3;
		private String title = "";
		private boolean press = false;
		
		public ButtonView3D(
				String name ,
				String normal ,
				String focus ,
				String title )
		{
			super( name );
			Bitmap bmp = null;
			if( normal != null )
			{
				bmp = ThemeManager.getInstance().getBitmap( normal );
				bgNormalRegion = new TextureRegion( new BitmapTexture( bmp , true ) );
			}
			if( focus != null )
			{
				bmp = ThemeManager.getInstance().getBitmap( focus );
				bgFocusRegion = new TextureRegion( new BitmapTexture( bmp , true ) );
			}
			this.title = title;
			if( bgNormalRegion != null )
			{
				super.setSize( bgNormalRegion.getRegionWidth() * scaleFactor , bgNormalRegion.getRegionHeight() * scaleFactor );
			}
			else if( bgFocusRegion != null )
			{
				super.setSize( bgFocusRegion.getRegionWidth() * scaleFactor , bgFocusRegion.getRegionHeight() * scaleFactor );
			}
		}
		
		public void setSize(
				float w ,
				float h )
		{
			super.setSize( w , h );
			if( title != null )
			{
				Bitmap titleBmp = titleToPixmapWidthLimit( title , this.width , m_titleHeight , Color.WHITE );
				titleRegion = new TextureRegion( new BitmapTexture( titleBmp , true ) );
				titleRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				titleTopOffset = ( this.height - titleRegion.getRegionHeight() ) / 2f;
				if( titleTopOffset < 0 )
				{
					titleTopOffset = 0;
				}
			}
		}
		
		@Override
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			if( pointer > 0 )
			{
				return false;
			}
			requestFocus();
			press = true;
			this.addTouchedView();
			return true;
		}
		
		public boolean handleActionWhenTouchLeave()
		{
			press = false;
			this.removeTouchedView();
			return true;
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			super.draw( batch , parentAlpha );
			if( press )
			{
				if( bgFocusRegion != null )
				{
					batch.draw( bgFocusRegion , this.x , this.y , bgFocusRegion.getRegionWidth() * ApplicationListHost.scaleFactor , bgFocusRegion.getRegionHeight() * ApplicationListHost.scaleFactor );
				}
			}
			else
			{
				if( bgNormalRegion != null )
				{
					batch.draw(
							bgNormalRegion ,
							this.x ,
							this.y ,
							bgNormalRegion.getRegionWidth() * ApplicationListHost.scaleFactor ,
							bgNormalRegion.getRegionHeight() * ApplicationListHost.scaleFactor );
				}
			}
			if( titleRegion != null )
			{
				batch.draw( titleRegion , this.x + ( this.width - titleRegion.getRegionWidth() ) / 2f , this.y + titleTopOffset );
			}
		}
		
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			if( pointer > 0 )
			{
				return false;
			}
			releaseFocus();
			press = false;
			this.removeTouchedView();
			if( name.equals( "button_cancel" ) )
			{
				viewParent.onCtrlEvent( this , MSG_VIEW_HIDE );
			}
			else if( name.equals( "button_ok" ) )
			{
				if( bNewFolderInApplist )
				{
					bNewFolderInApplist = false;
					folderName = m_editableTilte.getText();
					viewParent.onCtrlEvent( this , MSG_NEW_FOLDER_IN_APPLIST );
				}
				else if( bNewFolderInWorkspace )
				{
					bNewFolderInWorkspace = false;
					folderName = m_editableTilte.getText();
					viewParent.onCtrlEvent( this , MSG_NEW_FOLDER_IN_WORKSPACE );
				}
				else
				{
					folderName = m_editableTilte.getText();
					viewParent.onCtrlEvent( this , MSG_VIEW_TO_ADD_FOLDER );
				}
			}
			else if( name.equals( "button_sort" ) )
			{
				SendMsgToAndroid.sendShowSortDialogMsg( m_applicationList.sortId , iLoongLauncher.SORT_ORIGIN_ADD_APP_TO_FOLDER );
			}
			return true;
		}
	}
	
	private Bitmap titleToPixmapWidthLimit(
			String title ,
			float widthLimit ,
			float size ,
			int color )
	{
		Paint paint = new Paint();
		paint.setColor( color );
		paint.setAntiAlias( true );
		paint.setTextSize( size );
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );// zjp
		if( paint.measureText( title ) > widthLimit - 2 )
		{
			while( paint.measureText( title ) > widthLimit - paint.measureText( ".." ) - 2 )
			{
				title = title.substring( 0 , title.length() - 1 );
			}
			title += "..";
		}
		int titleWidth = (int)( paint.measureText( title ) );
		FontMetrics fontMetrics = paint.getFontMetrics();
		int titleHeight = (int)Math.ceil( -fontMetrics.ascent + fontMetrics.descent );
		Bitmap bmp = Bitmap.createBitmap( titleWidth , titleHeight , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		float x = 0;
		float titleY = (float)( -fontMetrics.ascent );
		canvas.drawText( title , x , titleY , paint );
		return bmp;
	}
	
	// xiatian add start //for mainmenu sort by user
	public void hideNoAnim()
	{
		releaseFocus();
		m_applicationList.removeAllChild( m_applicationList.getPageNum() );
		stopTween();
		super.hide();
	}
	
	// xiatian add end
	@Override
	public void valueChanged(
			TextField3D textField ,
			String newValue )
	{
		// TODO Auto-generated method stub
	}
}
