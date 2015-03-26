package com.iLoong.ThemeClock.View;
import com.badlogic.gdx.graphics.Texture;
import com.iLoong.launcher.UI3DEngine.View3D;

public class ClockMember2DView extends View3D{
	
	public ClockMember2DView(String name, Texture texture ) {
		super(name, texture);
		// TODO Auto-generated constructor stub
		this.width *= WidgetClock.MODEL_SCALE;
		this.height *=WidgetClock.MODEL_SCALE;
		this.setOrigin(this.width / 2, this.height / 2);  
		this.x = (WidgetClock.MODEL_WIDTH - this.width)/2;
		this.y = (WidgetClock.MODEL_HEIGHT - this.height)/2;
		this.bringToFront();
	}
	
}
