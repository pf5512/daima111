package com.iLoong.launcher.dockbarAdd;


import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.UI3DEngine.TextField3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktopEdit.DesktopEdit;
import com.iLoong.launcher.dockbarAdd.ButtonView3D.Event;


public class AddFolder extends ViewGroup3D
{
	
	public static final int MSG_HIDE_ADD_VIEW = 0;
	public static final int MSG_SHOW_ADD_APP = 1;
	boolean once = true;
	private ButtonView3D buttonOK;
	private ButtonView3D buttonCancel;
	private TextField3D inputTextField3D;
	private float mScale = 1f;
	public boolean bFolderRename;
	
	public void setviewSize(
			View3D view )
	{
		view.setSize( (float)( view.width * mScale * 1.3 / 1.5f ) , (float)( view.height * mScale * 1.3 / 1.5f ) );
		view.setOrigin( view.width / 2 , view.height / 2 );
	}
	
	public AddFolder(
			String name ,
			boolean folderRename ,
			String folderTitle )
	{
		super( name );
		width = Utils3D.getScreenWidth();
		height = Utils3D.getScreenHeight();
		mScale = (float)width / 480;
		bFolderRename = folderRename;
		AtlasRegion texture = null;
		if( folderRename )
		{
			texture = R3D.getTextureRegion( R3D.dockbar_editmode_addfolder_frame2 );
		}
		else
		{
			texture = R3D.getTextureRegion( R3D.dockbar_editmode_addfolder_frame );
		}
		ImageView3D viewbg = new ImageView3D( "viewbg" , texture );
		setviewSize( viewbg );
		viewbg.x = Utils3D.getScreenWidth() / 2 - viewbg.getWidth() / 2;
		viewbg.y = Utils3D.getScreenHeight() / 2 - viewbg.getHeight() / 2 + Utils3D.getScreenHeight() * 0.2f;
		addView( viewbg );
		float textHeight = (float)( viewbg.height * 0.20 ) , textWidth = (float)( viewbg.width * 0.7 );
		Paint paint = new Paint();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		paint.setTextSize( textHeight / 2 );
		inputTextField3D = new MyTextField3D( "inputTextaddView" , textWidth , textHeight , paint );
		float inputTextField3D_X = viewbg.x + viewbg.width * 0.14f;
		float inputTextField3D_Y = viewbg.y + viewbg.height * 0.41f;
		inputTextField3D.setPosition( inputTextField3D_X , inputTextField3D_Y );
		inputTextField3D.setOrigin( textWidth / 2 , textHeight / 2 );
		inputTextField3D.setText( folderTitle );
		// inputTextField3D.setSelection(0, title.trim().length());
		inputTextField3D.setKeyboardAdapter( null );
		inputTextField3D.setEditable( true );
		TextureRegion gridbgTexture = R3D.getTextureRegion( "miui-rename-bg" );
		NinePatch gridbackground = new NinePatch( gridbgTexture , 10 , 10 , 10 , 10 );
		// inputTextField3D.setBackgroud(gridbackground);
		inputTextField3D.show();
		inputTextField3D.popKeyBoard();
		addView( inputTextField3D );
		AtlasRegion textureOK = R3D.getTextureRegion( R3D.dockbar_editmode_addfolder_done );
		AtlasRegion textureOK1 = R3D.getTextureRegion( R3D.dockbar_editmode_addfolder_doned );
		buttonOK = new ButtonView3D( "buttonOK" , textureOK , textureOK1 , textureOK );
		buttonOK.region = textureOK;
		buttonOK.setSize( textureOK.getRegionWidth() * mScale * 22 / 26f , textureOK.getRegionHeight() * mScale * 22 / 26f );
		buttonOK.x = (float)( viewbg.x + viewbg.width * 3 / 4 - buttonOK.width / 2 - 5 * mScale );
		buttonOK.y = (float)( viewbg.y + viewbg.height * 0.075 );
		buttonOK.setEvent( new Event() {
			
			public void callback()
			{
				String name = inputTextField3D.getText();
				DesktopEdit.getInstance().setFoldername( name );
				inputTextField3D.hideInputKeyboard();
				AddFolder.this.viewParent.onCtrlEvent( AddFolder.this , AddFolder.MSG_SHOW_ADD_APP );
			}
		} );
		addView( buttonOK );
		AtlasRegion textureCancel = R3D.getTextureRegion( R3D.dockbar_editmode_addfolder_cancel );
		AtlasRegion textureCancel1 = R3D.getTextureRegion( R3D.dockbar_editmode_addfolder_canceled );
		buttonCancel = new ButtonView3D( "buttonCancel" , textureCancel , textureCancel1 , textureCancel );
		buttonCancel.region = textureCancel;
		buttonCancel.setSize( textureCancel.getRegionWidth() * mScale * 22 / 26f , textureCancel.getRegionHeight() * mScale * 22 / 26f );
		buttonCancel.x = (float)( viewbg.x + viewbg.width * 1 / 4 - buttonCancel.width / 2 + 5 * mScale );
		buttonCancel.y = (float)( viewbg.y + viewbg.height * 0.075 );
		buttonCancel.setEvent( new Event() {
			
			@Override
			public void callback()
			{
				exit();
			}
		} );
		addView( buttonCancel );
		AtlasRegion texturecha = R3D.getTextureRegion( R3D.dockbar_editmode_addfolder_exit );
		AtlasRegion texturecha1 = R3D.getTextureRegion( R3D.dockbar_editmode_addfolder_exited );
		ButtonView3D buttoncha = new ButtonView3D( "buttoncha" , texturecha , texturecha1 , texturecha );
		buttoncha.region = texturecha;
		buttoncha.setSize( texturecha.getRegionWidth() * mScale * 22 / 26f , texturecha.getRegionHeight() * mScale * 22 / 26f );
		buttoncha.x = (float)( viewbg.x + viewbg.width * 0.85 - buttoncha.width / 2 );
		buttoncha.y = (float)( viewbg.y + viewbg.height * 0.85 - buttoncha.height / 2 );
		buttoncha.setEvent( new Event() {
			
			@Override
			public void callback()
			{
				exit();
			}
		} );
		addView( buttoncha );
		once = true;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		super.draw( batch , parentAlpha );
		if( once )
		{
			inputTextField3D.onTouchDown( 0 , 0 , 0 );
			once = false;
		}
	}
	
	public void exit()
	{
		String name = inputTextField3D.getText();
		DesktopEdit.getInstance().setFoldername( name );
		inputTextField3D.hideInputKeyboard();
		AddFolder.this.viewParent.onCtrlEvent( AddFolder.this , AddFolder.MSG_HIDE_ADD_VIEW );
	}
	
	public static class myAppAdd extends AddApp
	{
		
		public myAppAdd(
				String name )
		{
			super( name );
			ImageButton3D done = (ImageButton3D)this.findView( "button done" );
			if( done != null )
			{
				done.setEvent( new ImageButton3D.Event() {
					
					@Override
					public void callback()
					{
						Workspace3D.getInstance().getSelectApp( CooeeIcon3D.icons );
						CooeeIcon3D.icons.clear();
						Workspace3D.getInstance().createVirturFolder( Root3D.folder );
						myAppAdd.this.viewParent.onCtrlEvent( myAppAdd.this , AddApp.MSG_HIDE_ADD_VIEW );
					}
				} );
			}
		}
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		super.onTouchDown( x , y , pointer );
		return true;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		super.onTouchUp( x , y , pointer );
		return true;
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		super.fling( velocityX , velocityY );
		return true;
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		super.scroll( x , y , deltaX , deltaY );
		return true;
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		if( keycode == KeyEvent.KEYCODE_BACK )
			return true;
		else if( keycode == KeyEvent.KEYCODE_MENU )
		{
		}
		return super.keyDown( keycode );
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			exit();
			return true;
		}
		return super.keyUp( keycode );
	}
	
	public class MyTextField3D extends TextField3D
	{
		
		public MyTextField3D(
				String name ,
				float width ,
				float height ,
				Paint paint )
		{
			super( name , width , height , paint );
			TextureRegion gridbgTexture = R3D.getTextureRegion( R3D.dockbar_editmode_addfolder_input );
			cursorPatch = new NinePatch( gridbgTexture , 1 , 1 , 0 , 0 );
		}
	}
}
