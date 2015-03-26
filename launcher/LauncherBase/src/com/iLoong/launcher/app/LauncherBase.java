package com.iLoong.launcher.app;


import com.iLoong.launcher.UI3DEngine.Desktop3D;

import android.view.View;


public interface LauncherBase
{
	
	public boolean isWorkspace3DTouchable();
	
	public View getGLView();
	
	public Desktop3D getDesktop();
	
	public void setCurrentFocusX(
			int x );
	
	public boolean hasCancelDialog();
}
