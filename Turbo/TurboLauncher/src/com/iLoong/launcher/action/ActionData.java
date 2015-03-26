package com.iLoong.launcher.action;

import android.graphics.Bitmap;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.data.Widget3DInfo;
import com.iLoong.launcher.recent.RecentAppHolder;


public class ActionData
{
	public static final int VALIDITY_LOCK_NORMAL=0;
	//允许时钟翻转但是屏蔽其他任何操作
	public static final int VALIDITY_LOCK_TIMECLOCK=2;
	//屏蔽时钟任何操作
	public static final int VALIDITY_LOCK_WEATHERCLOCK=1;
	//允许Dockbar翻转但是屏蔽其他任何操作
	public static final int VALIDITY_LOCK_DOCKBAR=3;
	//运行dockbar左右滑动，但是屏蔽其他任何操作
	public static final int VALIDITY_LOCK_HOTMAINGROUP=4;


	
	
	
	public static ActionData instance=null;
	public static  View3D focusView;
	public Widget3DInfo clockInfo;
	public ActionView currAction;
	private boolean isShowHotFront;
	private boolean isShowWorkspaceFront;
	public static NinePatch bg;
	private float []pos=new float[2];
	private int validity=0;
	private boolean focusLock=false;
	 public static boolean isActionShow=false;
	private  ActionData(){
		
		
	}
	
	public static ActionData getInstance(){
		if(instance==null){
			instance=new ActionData();
		}
		return instance;
	}
	
	
	public void setClockInfo(Widget3DInfo clockInfo){
		this.clockInfo=clockInfo;
	}
	
	public Widget3DInfo  getClockInfo(){
		return this.clockInfo;
	}
	
	public void setFocusView(View3D view){
		this.focusView=view;
	}
	
	public View3D getFocusView(){
		return this.focusView;
	}
	
	public void setCurrAction(ActionView v){
		currAction=v;
	}

	public View3D getCurrAction(){
		return currAction;
	}

	public boolean isShowHotFront(){
		return isShowHotFront;
	}
	
	public void setShowHotFront(boolean visible){
		this.isShowHotFront=visible;
	}
	
	public boolean isShowWorkspaceFront(){
		return isShowWorkspaceFront;
	}
	
	public void setShowWorkspaceFront(boolean visible){
		this.isShowWorkspaceFront=visible;
	}
	
	
	public void setFrontPosition(float startY,float endY){
		
		pos[0]=startY;
		pos[1]=endY;
		
		
	}
	
	public float[] getFrontPosition(){	
		return pos;
	}
	public void setValidity(int validity){
		this.validity=validity;
	}
	public int checkValidity(){
		return this.validity;
	}
	
	public void setLockFocusView(boolean lock){
		this.focusLock=lock;
	}
	public boolean getLockFocusView(){
		return this.focusLock;
	}
	
	
}
