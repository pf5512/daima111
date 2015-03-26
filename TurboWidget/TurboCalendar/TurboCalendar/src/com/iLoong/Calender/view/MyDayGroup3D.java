package com.iLoong.Calender.view;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.iLoong.Calender.common.Parameter;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;

public class MyDayGroup3D extends ViewGroup3D {
	public MyDayGroup3D(String name) {
		super(name);
		this.transform = true;
	}

	public boolean is3dRotation() {
		return true;
	}
	
//	float firstHeight = (Parameter.Origin_To_Origin_Height
//			+ Parameter.Origin_To_Sun_Height
//			- Parameter.Calender_Day_Glass_Height / 2
//			- Parameter.Calender_Week_Glass_Height / 2 - Parameter.Calender_Day_Glass_Height * 5)
//			* WidgetCalender.scale
//			- (WidgetCalender.scale - WidgetCalender.height_scale)
//			/ 2
//			* 100f
//			- Parameter.Calender_Day_Glass_Height
//			* WidgetCalender.height_scale / 2 - 21f * WidgetCalender.scale-Utils3D.getStatusBarHeight();
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// Log.d("Calender", "ifTweenStart" + WidgetCalender.ifTweenStart);
		float glscissordownmoveHeight = WidgetCalender.getIntance().calenderalldown
				.getY();
		float glscissorupmoveHeight = -WidgetCalender.getIntance().calenderalltop
				.getY();
		float nowglscissorHeight = Parameter.Calender_Week_Glass_Height
				* WidgetCalender.scale + Parameter.Calender_Day_Glass_Height
				* WidgetCalender.height_scale * 6 - glscissordownmoveHeight
				- glscissorupmoveHeight;
		if (WidgetCalender.ifstartTween||WidgetCalender.ifstartclickbacktimeline) {
			Gdx.gl.glEnable(GL10.GL_SCISSOR_TEST);
			Gdx.gl.glScissor(
					0 ,
					(int)( WidgetCalender.getIntance().calenderalldown.getY() + WidgetCalender.firstHeight + Utils3D.getScreenHeight() - R3D.Workspace_cell_each_height * 4 ) ,
					Utils3D.getScreenWidth() ,
					(int)nowglscissorHeight );
			super.draw(batch, parentAlpha);
			Gdx.gl.glDisable(GL10.GL_SCISSOR_TEST);
		} else {
			super.draw(batch, parentAlpha);
		}

	}
}
