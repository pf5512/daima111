package com.iLoong.launcher.SideBar;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.View3D;

public class SidebarButton extends View3D{

	private TextureRegion normal = null;
	private TextureRegion focus = null;
	private TextureRegion texture = null;
	public SidebarButton(String name,TextureRegion normal, TextureRegion focus,int width,int height) {
		super(name);
		setSize(width,height);
		texture = this.normal = normal;
		this.focus = focus;
		// TODO Auto-generated constructor stub
	}
	
	
	public void onPress() {
		texture = focus;
	}
	
//	@Override
//	public boolean onTouchDown(float x, float y, int pointer) {
//		// TODO Auto-generated method stub
//		texture = focus;
//		return super.onTouchDown(x, y, pointer);
//	}
//


	public void onRelease(){
		texture = normal;
	}



	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
//		super.draw(batch, parentAlpha);
		if(texture != null){
			batch.draw(texture, x, y,width,height);
		}
		
	}
	
	
	
}