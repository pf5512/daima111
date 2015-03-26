package com.iLoong.MusicPlay.common;


import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;


// 根据基准分辨率的一系列参数（720*1280）工作空间：720*988 以工作空间为外面大的容器
public class Parameter
{
	
	public static final float POINT_TO_POINTX = Utils3D.getScreenWidth() / 2;
	public static final float POINT_TO_POINTY = R3D.Workspace_cell_each_height;
	
	public static final float JIAOPIAN_TOX = -1.294f;
	public static final float JIAOPIAN_TOY = 6.756f;
	public static final float JIAOPIAN_TOZ = 49.602f;
	
	public static final float JINDUTIAO_TOX = -166.839f;
	public static final float JINDUTIAO_TOY = -190.224f;
	public static final float JINDUTIAO_TOZ = 13.779f;
	
	public static final float PREVIEW_TOX = -109.958f;
	public static final float PREVIEW_TOY = -189.507f;
	public static final float PREVIEW_TOZ = -34.162f;
	
	public static final float PAUSE_TOX = -0.258f;
	public static final float PAUSE_TOY = -189.507f;
	public static final float PAUSE_TOZ = -34.162f;
	
	public static final float NEXT_TOX = 112.66f;
	public static final float NEXT_TOY = -189.507f;
	public static final float NEXT_TOZ = -34.162f;
	
	public static final float YINGUI_TOX=0.0f;
	public static final float YINGUI_TOY=158.903f;
	public static final float YINGUI_TOZ=32.16f;
	
	public static final float YINGUIGAOLIANG_TOX=-169.415f;
	public static final float YINGUIGAOLIANG_TOY=181.842f;
	public static final float YINGUIGAOLIANG_TOZ=-32.411f;
}
