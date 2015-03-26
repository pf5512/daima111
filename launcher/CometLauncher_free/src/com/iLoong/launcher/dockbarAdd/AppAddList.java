package com.iLoong.launcher.dockbarAdd;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.iLoong.launcher.Desktop3D.NPageBase;
import com.iLoong.launcher.UI3DEngine.Utils3D;


public class AppAddList extends NPageBase
{
	
	public AppAddList(
			String name ,
			int width )
	{
		super( name );
		transform = true;
		setWholePageList();
		setEffectType( 0 );
		indicatorView = new IndicatorView( "npage_indicator" , width );
	}
	
	public void removeAllChild(
			int num )
	{
		for( int i = 0 ; i < num ; i++ )
		{
			this.removeView( view_list.get( i ) );
		}
		view_list.clear();
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		viewParent.releaseFocus();
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		boolean ret = false;
		ret = super.onTouchUp( x , y , pointer );
		viewParent.requestFocus();
		return ret;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		batch.flush();
		Gdx.gl.glEnable( GL10.GL_SCISSOR_TEST );
		Gdx.gl.glScissor( (int)( (float)63 * Utils3D.getScreenWidth() / 720 ) , 0 , (int)( width - (float)125 * Utils3D.getScreenWidth() / 720 ) , Utils3D.getScreenHeight() );
		super.draw( batch , parentAlpha );
		Gdx.gl.glDisable( GL10.GL_SCISSOR_TEST );
	}
}
