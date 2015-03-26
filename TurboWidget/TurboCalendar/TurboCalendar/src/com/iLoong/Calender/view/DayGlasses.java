package com.iLoong.Calender.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Calender.common.Parameter;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class DayGlasses extends PluginViewObject3D {
	public DayGlasses(MainAppContext appContext, String name,
			TextureRegion region, String objName) {
		super(appContext, name,region,objName);
	}

	protected void initObjScale() {
		this.mScaleX = this.mScaleZ = WidgetCalender.scale;
		this.mScaleY = WidgetCalender.height_scale;
		// ((float) appContext.mWidgetContext
		// .getResources().getDisplayMetrics().density);
		// Log.d("weijie",
		// ">>>>PluginViewObject3D initObjScale end mScaleX="+this.mScaleX);
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
				- glscissorupmoveHeight+4*WidgetCalender.scale;
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
