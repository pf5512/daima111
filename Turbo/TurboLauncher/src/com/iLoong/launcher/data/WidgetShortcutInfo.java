package com.iLoong.launcher.data;


import android.content.ComponentName;
import android.graphics.Bitmap;


public class WidgetShortcutInfo
{
	
	public int cellHSpan = 1;
	public int cellVSpan = 1;
	public String textureName = "";
	public String label = "";
	public ComponentName component;
	public boolean isHide = false;
	public boolean isWidget = false;
	public boolean isShortcut = false;
	public Bitmap widget2DBitmap = null;
	public Bitmap widgetHostBitmap = null;
}
