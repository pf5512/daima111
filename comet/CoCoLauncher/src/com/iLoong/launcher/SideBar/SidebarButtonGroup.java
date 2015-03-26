package com.iLoong.launcher.SideBar;

import com.iLoong.launcher.Desktop3D.Log;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class SidebarButtonGroup extends ViewGroup3D{
	public final static int MSG_BUTTON_SCROLL = 0;
	
	public final static int MSG_BUTTON_DOWN_SHORTCUT = 2;
	public final static int MSG_BUTTON_DOWN_WIDGET = 3;
	
	public final static int MSG_BUTTON_UP_SHORTCUT = 1;
	public final static int MSG_BUTTON_UP_WIDGET = 4;
	public final static int MSG_BUTTON_SCROLL_UP = 5;
	
	public final int STATE_FOLD = 0;
	public final int STATE_UNFOLD = 1;
	private SidebarButton shortcutButton = new SidebarButton("shortcutbtn",R3D.getTextureRegion("menu-user-button1"),R3D.getTextureRegion("menu-tool-button3"),R3D.sidebar_button_icon_width,R3D.sidebar_button_icon_height);
	private SidebarButton widgetButton = new SidebarButton("widgetbtn",R3D.getTextureRegion("menu-user-button2"),R3D.getTextureRegion("menu-tool-button2"),R3D.sidebar_button_icon_width,R3D.sidebar_button_icon_height);
	private TextureRegion focus = R3D.getTextureRegion("menu-user-button3");
	private float focusX = 0;
	private float focusY = 0;
	private float focusW = R3D.sidebar_button_foucs_size;
	private float focusH = R3D.sidebar_button_foucs_size;
	
	private View3D focusButton = null;
	private boolean isScroll  = false;
	private int state = STATE_UNFOLD;
	public SidebarButtonGroup(String name) {
		super(name);
		TextureRegion region = R3D.getTextureRegion("menu-tool-button1");
		setBackgroud(new NinePatch(region));
//		setBackgroud(new NinePatch(new Texture(Gdx.files.internal("bgtest.png"))));
		addView(shortcutButton);
		addView(widgetButton);
		shortcutButton.setPosition(R3D.sidebar_button_icon1_x - shortcutButton.getWidth()/2, R3D.sidebar_button_icon1_y - shortcutButton.getHeight()/2);
		widgetButton.setPosition(R3D.sidebar_button_icon2_x - widgetButton.getWidth()/2, R3D.sidebar_button_icon2_y - widgetButton.getHeight()/2);
		
		int count = getChildCount();
		this.width = R3D.sidebar_button_width;
		this.height = R3D.sidebar_button_height ;//  
		
		focusButton = shortcutButton;
		shortcutButton.onPress();

		focusX = focusButton.x + focusButton.width/2 - focusW/2;
		focusY = focusButton.y + focusButton.height/2 - focusH/2;
//		View3D view;
//		for(int i = count - 1; i >= 0; i--){
//			view = getChildAt(i);
//			if(focusButton == view) continue;
//			view.y = focusButton.y + focusButton.height*i - focusButton.height/3;
//		}
//		focusButton.bringToFront();
//		fold();
	}
	
	
//	@Override
//	public boolean onLongClick(float x, float y) {
//		// TODO Auto-generated method stub
//		isAdjust = true;
//		return super.onLongClick(x, y);
//	}


	public boolean scroll(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		Log.v("gesture","View3D scroll:" + name +" x:" + x + " y:"+ y + " dx:" + deltaX + " dy:" + deltaY);
	
		float px = x + deltaX;
		float py = y - deltaY;
		isScroll = true;
		this.setTag(deltaY);
		return this.viewParent.onCtrlEvent(this, MSG_BUTTON_SCROLL);	
		
	}
	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		// Log.e("sidebar", "onTouchDown x:" + this.x + " y:" + this.y);
		super.onTouchDown(x, y, pointer);
		requestFocus();
		isScroll = false;
		if(x > 0 && x <= this.width/2){
			focusButton = shortcutButton;
			viewParent.onCtrlEvent(this, MSG_BUTTON_DOWN_SHORTCUT);
		}
		else if(x > this.width/2 && x < this.width){
			focusButton = widgetButton;
			viewParent.onCtrlEvent(this, MSG_BUTTON_DOWN_WIDGET);
		}
		
		
//		if((focusButton = hit(x, y)) != null){
//			if(focusButton == shortcutButton ){
//				viewParent.onCtrlEvent(this, MSG_BUTTON_DOWN_SHORTCUT);
//			}
//			else if(focusButton == widgetButton){
//				viewParent.onCtrlEvent(this, MSG_BUTTON_DOWN_WIDGET);
//			}
//			Log.v("sidebar"," ontouchdown:" + focusButton);
//		}
		
//		reset(focusButton);
//		unfold();
		return true;
	}
	
	@Override
	public void setUser(float value) {
		// TODO Auto-generated method stub
		focusX = value;
	}


	@Override
	public float getUser() {
		// TODO Auto-generated method stub
		return focusX;
	}


	@Override
	public boolean onTouchUp(float x, float y, int pointer) {
		// Log.e("sidebar", "onTouchUp x:" + this.x + " y:" + this.y);	
		super.onTouchUp(x, y, pointer);
		releaseFocus();
		if(!isScroll ){
			
			if(focusButton == shortcutButton ){
//				widgetButton.onRelease();
				return viewParent.onCtrlEvent(this, MSG_BUTTON_UP_SHORTCUT);
			}
			else if(focusButton == widgetButton){
//				shortcutButton.onRelease();
				return viewParent.onCtrlEvent(this, MSG_BUTTON_UP_WIDGET);
			}
		}
		else{
			return viewParent.onCtrlEvent(this, MSG_BUTTON_SCROLL_UP);
		}
		
		
//		else{
//			if(this.getParent() != null){
//				SideBar parent = (SideBar) this.getParent();
//				if(parent.getState() == SideBar.STATE_HIDE){
////					fold();
//					reset(null);
//				}
//			}
//			isScroll = false;
//			return true;
//		}
		
		return false;
	}
	
	
	public void press(){
		if(focusButton != null){
			((SidebarButton)focusButton).onPress();
			float tx = focusButton.x + focusButton.width/2 - focusW/2;
			if(tx != focusX){
				this.startTween(View3DTweenAccessor.USER, Cubic.OUT, 0.5f, tx, 0, 0);
			}
			focusY = focusButton.y + focusButton.height/2 - focusH/2;
		
		}
	}
	public void reset(){
		for(int i = 0; i < getChildCount(); i++){
			SidebarButton button = (SidebarButton) getChildAt(i);
			if(focusButton != button) button.onRelease();
		}
	}


	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		super.draw(batch, parentAlpha);
		if(focus != null)
			batch.draw(focus,this.x + focusX, this.y + focusY,focusW,focusH);
		
	}
	
	
	
}