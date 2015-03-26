package com.iLoong.launcher.media;


import java.util.ArrayList;

import android.net.Uri;

import com.iLoong.launcher.UI3DEngine.View3D;


public interface MediaView
{
	
	public void prepare(
			int priority );
	
	public void free();
	
	public void refresh();
	
	public void select();
	
	public void share(
			ArrayList<Uri> list );
	
	public void onDelete();
	
	public void clearSelect();
}
