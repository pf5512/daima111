package com.iLoong.launcher.app;


import android.view.View;

import com.iLoong.launcher.UI3DEngine.Desktop3D;


public interface LauncherBase
{
	
	public boolean isWorkspace3DTouchable();
	
	public View getGLView();
	
	public Desktop3D getDesktop();
	
	public void setCurrentFocusX(
			int x );
	
	public boolean hasCancelDialog();
}
