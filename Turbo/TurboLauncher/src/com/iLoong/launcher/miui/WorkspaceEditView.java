package com.iLoong.launcher.miui;


import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class WorkspaceEditView extends ViewGroup3D
{
	
	public static final int MSG_CHANGE_TO_APPEND_PAGE = 0;
	private static NinePatch bgNinePatch;
	private TextureRegion focus;
	private TextureRegion normal;
	private ImageView3D buttonView;
	private boolean firstPage = false;
	
	public WorkspaceEditView(
			String string ,
			boolean pagePos )
	{
		super( string );
		transform = true;
		normal = R3D.findRegion( "miui-addpage" );
		focus = R3D.findRegion( "miui-addpage-focus" );
		buttonView = new ImageView3D( "buttonView" , normal );
		buttonView.setSize( normal.getRegionWidth() * SetupMenu.mScale , normal.getRegionHeight() * SetupMenu.mScale );
		buttonView.setOrigin( buttonView.width / 2 , buttonView.height / 2 );
		firstPage = pagePos;
		buttonView.setPosition( ( this.width - buttonView.width ) / 2 , ( this.height - buttonView.height + R3D.Workspace_celllayout_bottompadding ) / 2 );
		addView( buttonView );
		if( Workspace3D.cellLayoutBg != null )
		{
			bgNinePatch = Workspace3D.cellLayoutBg;
		}
	}
	
	public boolean isFirstPage()
	{
		return firstPage;
	}
	
	public void setNormalRegion()
	{
		if( buttonView != null )
		{
			buttonView.region = normal;
		}
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
		if( bgNinePatch != null )
		{
			batch.setColor( 1.0f , 1.0f , 1.0f , 1.0f );
			bgNinePatch.draw(
					batch ,
					0 ,
					R3D.Workspace_celllayout_bottompadding - R3D.workspace_celllayout_offset_y ,
					this.width ,
					this.height - Utils3D.getStatusBarHeight() - R3D.Workspace_celllayout_toppadding - R3D.Workspace_celllayout_bottompadding + R3D.workspace_celllayout_bg_padding_top );
		}
		drawChildren( batch , parentAlpha );
		if( transform )
			resetTransform( batch );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer == 1 )
		{
			return super.onTouchDown( x , y , pointer );
		}
		View3D hitView = hit( x , y );
		if( hitView != null && hitView.name.equals( buttonView.name ) )
		{
			this.requestFocus();
			hitView.region = focus;
			return true;
		}
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer == 1 )
		{
			return super.onTouchUp( x , y , pointer );
		}
		SendMsgToAndroid.sendShowWorkspaceMsg();
		View3D hitView = hit( x , y );
		if( hitView != null && hitView.name.equals( buttonView.name ) )
		{
			/*添加新页*/
			setTag( this );
			hitView.region = normal;
			viewParent.onCtrlEvent( this , MSG_CHANGE_TO_APPEND_PAGE );
			this.releaseFocus();
			return true;
		}
		this.releaseFocus();
		buttonView.region = normal;
		return super.onTouchUp( x , y , pointer );
	}
}
