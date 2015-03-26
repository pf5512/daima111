package com.iLoong.launcher.CooeePlugin;


import android.content.Context;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.iLoong.launcher.UI3DEngine.UtilsBase;


public class CooeePluginHostView extends LinearLayout
{
	
	public boolean checkScrollV = true;
	public boolean enableScrollV = false;
	private float downX = 0;
	private float downY = 0;
	public static float SCROLL_DISTANCE = 25;
	public int sysVersion;
	private ICooeePlugin plugin;
	
	public CooeePluginHostView(
			Context context )
	{
		super( context );
		// TODO Auto-generated constructor stub
	}
	
	public void bindPlugin(
			ICooeePlugin plugin )
	{
		if( this.plugin == null )
		{
			this.plugin = plugin;
			addView( plugin.getPluginView() , new LinearLayout.LayoutParams( UtilsBase.getScreenWidth() , UtilsBase.getScreenHeight() ) );
		}
	}
	
	public void releasePluginView()
	{
		plugin.releasePluginView();
	}
	
	public void onPiflowIn()
	{
		plugin.onPiflowIn();
	}
	
	public void onPiflowOut()
	{
		plugin.onPiflowOut();
	}
	
	public void notifyScrollX(
			int scrollX ,
			int width )
	{
		// if(scrollX >= 0 && scrollX <= width){
		// float alpha = (width-(float)scrollX)/((float)width);
		// if(alpha < 0)alpha = 0;
		// if(alpha > 1)alpha = 1;
		// this.setAlpha(alpha);
		// }
		// if(scrollX < 0 && scrollX >= -width){
		// float alpha = ((float)scrollX)/((float)width)+1;
		// if(alpha < 0)alpha = 0;
		// if(alpha > 1)alpha = 1;
		// this.setAlpha(alpha);
		// }
	}
	
	@Override
	public boolean dispatchTouchEvent(
			MotionEvent ev )
	{
		// Log.d("widget",
		// "ev:"+ev.getAction()+","+ev.getPointerId(ev.getActionIndex()));
		boolean ignore = false;
		final float x = ev.getX();
		final float y = ev.getY();
		switch( ev.getAction() )
		{
			case MotionEvent.ACTION_DOWN:
				checkScrollV = true;
				enableScrollV = false;
				downX = x;
				downY = y;
				break;
			case MotionEvent.ACTION_MOVE:
				if( enableScrollV )
					break;
				if( !checkScrollV )
				{
					ignore = true;
					break;
				}
				else if( Math.abs( y - downY ) > SCROLL_DISTANCE )
				{
					enableScrollV = true;
					// Log.e("widget", "scrollV");
				}
				else if( Math.abs( x - downX ) > SCROLL_DISTANCE )
				{
					ignore = true;
					checkScrollV = false;
					enableScrollV = false;
					// Log.e("widget", "scrollH");
				}
				else
				{
					ignore = true;
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if( !checkScrollV )
					break;
				break;
		}
		if( !ignore )
			super.dispatchTouchEvent( ev );
		if( checkScrollV )
			return true;
		return false;
	}
}
