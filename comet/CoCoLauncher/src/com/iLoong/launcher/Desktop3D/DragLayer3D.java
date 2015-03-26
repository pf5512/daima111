package com.iLoong.launcher.Desktop3D;

import java.util.ArrayList;

import com.iLoong.launcher.Desktop3D.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.HotSeat3D.HotSeatMainGroup;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.widget.Widget;

public class DragLayer3D extends ViewGroup3D {

	public static final int MSG_DRAG_END = 0;
	public static final int MSG_DRAG_OVER = 1;
	public static final int MSG_DRAG_INBORDER = 2;
	private DragView3D dragView = new DragView3D("dragview");
	private ArrayList<DropTarget3D> dropTargets = new ArrayList<DropTarget3D>();
	private ArrayList<View3D> dragList = new ArrayList<View3D>();

	private float dropX, dropY;

	public static float dragStartX = -1f,dragStartY = -1f;
	public float offsetX = -1f, offsetY = -1f;
	public boolean draging = false;
	
	private DropTarget3D overTarget = null;

	private NinePatch moveToLeft = new NinePatch(R3D.getTextureRegion("move_to_left_screen_bar_bg"),0,0,30,30);
	private NinePatch moveToRight = new NinePatch(R3D.getTextureRegion("move_to_right_screen_bar_bg"),0,0,30,30);
	private float borderWidth = R3D.getInteger("drag_border_width");
	private float borderHeight = Utils3D.getScreenHeight()/5*4;
	private int showBorder = 0;
	private long borderStayTime = 0;
	private float borderOpcity = 0f;
	private boolean dismissUp = false;
	private float lastX = 0;
	private float lastY = 0;
	
	//teapotXu add start for Folder in Mainmenu
	public boolean is_dragging_in_apphost = false;
	//teapotXu add end for Folder in Mainmenu	
	
	/************************ added by zhenNan.ye begin *************************/
	private float last_x = 0;
	private float last_y = 0;
	/************************ added by zhenNan.ye end ***************************/

	
//	static Texture texture = new Texture(Gdx.files.internal("bgtest.png"));
	public DragLayer3D(String name) {
		super(name);
		dragView.transform = true;
		dragView.color.a = 0.8f;
//		dragView.setBackgroud(new NinePatch(texture));
		// TODO Auto-generated constructor stub
		// dragView.setBackgroud(new
		// Texture(Gdx.files.internal("pageselectitembg_128_128.png")));
	}

	public ArrayList<View3D> getDragList() {
//		if (this.dragView.getChildCount() > 0) {
//			this.dragView.removeAllViews();
//
//		}
		return dragList;
	}

	public void startDrag(ArrayList<View3D> list, float x, float y) {

		dragList.clear();
		removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			View3D view = list.get(i);
			if(view.getParent() != null){
				view.toAbsoluteCoords(point);
				view.x = point.x;
				view.y = point.y;
			}
			view.remove();
			view.isDragging = true;
			dragList.add(view);
		}
		list.clear();
		createDragView(x, y);
		draging = true;
	}

//	public void startDragCopy(ArrayList<View3D> list, float x, float y) {
//		dragList.clear();
//		for (int i = 0; i < list.size(); i++) {
//			View3D view = list.get(i);
//			dragList.add(view.clone());
//		}
//		list.clear();
//		createDragView(x, y);
//	}

	private void createDragView(float x, float y) {
		if (this.dragView.getChildCount() > 0) {
			this.dragView.removeAllViews();
		}
		
		int offsetX = R3D.workspace_multiviews_offset;
		int offsetY = R3D.workspace_multiviews_offset;
		int size = dragList.size();
		View3D view0 = dragList.get(0);
		dragView.setSize(view0.width + (size - 1) * offsetX, view0.height
				+ (size - 1) * offsetY);
		dragView.setOrigin(dragView.width / 2, dragView.height / 2);
		dragView.x = x;
		dragView.y = y;
		Log.d("launcher", "x,y="+x+","+y);
		addView(dragView);
		for (View3D view : dragList) {
			float tx = (size - 1 - dragView.getChildCount()) * offsetX;
			float ty = (size - 1 - dragView.getChildCount()) * offsetY;
			
			view.x = view.x - dragView.x;
			view.y = view.y - dragView.y;
//			dragView.calcCoordinate(view);
			view.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT, 0.5f, tx, ty, 0);	
//			view.x = tx;
//			view.y = ty;
			dragView.addView(view);
		}
		if (dragView.getChildCount() == 1) {// only one object tween
			dragView.setScale(0.6f, 0.6f);
			dragView.startTween(View3DTweenAccessor.SCALE_XY, Elastic.OUT,
					0.8f, 1f, 1f, 0f);
		}
	}

	public void addDropTarget(DropTarget3D target) {
		if(!dropTargets.contains(target))
		{
			this.dropTargets.add(target);
		}
		
	}
	
	public void addDropTargetBefore(DropTarget3D before, DropTarget3D target) {
		
		if(!dropTargets.contains(target))
		{
		int index = dropTargets.indexOf(before);
		dropTargets.add(index, target);
		}
		//this.dropTargets.add(target);
	}
//	public void addDropTarget(DropTarget3D target) {
//		this.dropTargets.add(target);
//	}

	public void removeDropTarget(DropTarget3D target) {
		this.dropTargets.remove(target);
	}

	private DropTarget3D dropTarget(float x, float y) {
		//Log.v("launcher", " dropTarget:" + " x:" + x + " y:" + y);
		int count = this.dropTargets.size();
		for (int i = count - 1; i >= 0; i--) {
			DropTarget3D target = dropTargets.get(i);
			if(target.pointerInAbs(x, y) && target.isVisibleInParent())
				if(target.onDrop(getDragList(), x, y)){
					return target;
				}
					
			
		}
		return null;
	}
	
	public DropTarget3D onDrop(){
		/************************ added by zhenNan.ye begin ***************************/
		View3D particleView = dragList.get(0);
		/************************ added by zhenNan.ye end ***************************/
		
		DropTarget3D result;
		for(View3D view : dragList){
			view.stopTween();
			view.isDragging = false;
		}
		result = dropTarget(dropX,dropY);
		
		/************************ added by zhenNan.ye begin ***************************/
		if (DefaultLayout.enable_particle)
		{
			if (ParticleManager.particleManagerEnable)
			{
				particleView.stopParticle(ParticleManager.PARTICLE_TYPE_NAME_START_DRAG);
				if (result instanceof FolderIcon3D
						|| result instanceof HotSeat3D)
				{
					particleView.particleCanRender = false;
					particleView.particleType = null;
					particleView.stopParticle(ParticleManager.PARTICLE_TYPE_NAME_DRAG);
				}
				else {
					particleView.pauseParticle(ParticleManager.PARTICLE_TYPE_NAME_DRAG);
				}
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		
		return result;
//		dragList.clear();
	}
	
	public DropTarget3D dropTargetOver(float x, float y) {
		//Log.v("launcher", " dropTargetOver:" + " x:" + x + " y:" + y);
		int count = this.dropTargets.size();
		for (int i = count - 1; i >= 0; i--) {
			DropTarget3D target = dropTargets.get(i);
			if(target.pointerInAbs(x, y) && target.isVisibleInParent())
				if (target.onDropOver(dragList, x, y)) {
					return target;
				}

			
		}
		return null;
	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		lastX = x;
		lastY = y;
		/************************ added by zhenNan.ye begin ***************************/
		if (DefaultLayout.enable_particle)
		{
			if (ParticleManager.particleManagerEnable)
			{
				last_x = x;
				last_y = y;
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		
		return super.onTouchDown(x, y, pointer);
	}

	
//	@Override
//	public boolean onTouchDragged(float x, float y, int pointer) {
//		// TODO Auto-generated method stub
//		if(pointer > 1) return true;
//		float curX = x;
//		float curY = y;
//		if (offsetX == -1f && offsetY == -1f) {
//			offsetX = curX - dragView.x;
//			offsetY = curY - dragView.y;
//		}
//		
//		dragView.setPosition(curX - offsetX, curY - offsetY);		
//		dropTargetOver(curX,curY);
//		return super.onTouchDragged(x, y, pointer);
//	}

	@Override
	public boolean scroll(float x, float y, float deltaX, float deltaY) {
		lastX = x;
		lastY = y;
		float curX = x + deltaX;
		float curY = y - deltaY;
		
		//Log.v("launcher", " scroll:" + name + " x:" + curX + " y:" + curY + " offsetX:"+offsetX+" offsetY:"+offsetY);

		if(offsetX == -1 || offsetY == -1){
			offsetX = dragStartX - dragView.x;
			offsetY = dragStartY - dragView.y;
		}
		
		dragView.setPosition(curX - offsetX, curY - offsetY);
		final DropTarget3D target = dropTargetOver(curX,curY);
		if(overTarget != target){
			overTarget = target;
			this.setTag(overTarget);
			viewParent.onCtrlEvent(this, MSG_DRAG_OVER);
		}
		
		/************************ added by zhenNan.ye begin ***************************/
		if (DefaultLayout.enable_particle)
		{
			if (ParticleManager.particleManagerEnable)
			{
				View3D particleView = dragList.get(0);
				if (Math.abs(x - last_x) > 10 || Math.abs(y - last_y) > 10)
				{
					float positionX, positionY;
					Vector2 point = new Vector2();
					particleView.toAbsoluteCoords(point);
					if (dragList.size() > 1)
					{
						float iconHeight = Utils3D.getIconBmpHeight();
						positionX = point.x+particleView.width/2;
						positionY = point.y+(particleView.height-iconHeight)+iconHeight/2;
					}else {
						if (particleView instanceof WidgetView || particleView instanceof Widget3D
								|| particleView instanceof Widget)
						{
							positionX = point.x+particleView.width/2;
							positionY = point.y+particleView.height/2;
						} else {
							float iconHeight = Utils3D.getIconBmpHeight();
							positionX = point.x+particleView.width/2;
							positionY = point.y+(particleView.height-iconHeight)+iconHeight/2;
						}
					}
					particleView.updateParticle(ParticleManager.PARTICLE_TYPE_NAME_DRAG, 
							positionX, positionY);
					
					last_x = x;
					last_y = y;
				}
				else
				{
					particleView.pauseParticle(ParticleManager.PARTICLE_TYPE_NAME_DRAG);
				}
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		
		View3D parent = this.getParent();
		if (parent instanceof Root3D) {
			Root3D root = (Root3D) parent;
			//teapotXu add start for Folder in Mainmenu
			//when folder in Mainmenu is open, no need to send MSG_DRAG_INBORDER, wait folder closed
			if(root.folderOpened || root.appHost.folderOpened){
//				if(root.folderOpened ){
			//teapotXu add end for Folder in Mainmenu
				borderStayTime = 0;
				showBorder = 0;
				return true;
			}
			else if (root.isPageContainerVisible()) {
				borderStayTime = 0;
				showBorder = 0;
				return false;
			} else {
				
				float px = curX,py = curY;
				if((curX > Utils3D.getScreenWidth()/2 && offsetX < dragView.width/2)
						|| (curX < Utils3D.getScreenWidth()/2 && offsetX > dragView.width/2)){
					px = dragView.x + dragView.width/2;
						
				}
				if(inBorder(px, py)){
					
					if(borderStayTime == 0){
						borderStayTime = System.currentTimeMillis();
					}
					else{
						 if(System.currentTimeMillis() -  borderStayTime > 500){
							viewParent.onCtrlEvent(this, MSG_DRAG_INBORDER);
						}
					}
					
					if(borderOpcity == 0f){
						this.startTween(View3DTweenAccessor.USER, Cubic.INOUT, 0.5f, 1f, 0f, 0f);
					}
					else{
						borderOpcity = 1f;
					}
				}
				else{
					if(borderOpcity == 1f){
						this.startTween(View3DTweenAccessor.USER, Cubic.INOUT, 0.5f, 0f, 0f, 0f).setCallback(this);
					}
					else{
						showBorder = 0;
					}
				
					borderStayTime = 0;
				}	
				return true;
			}
		}	
		return super.scroll(x, y, deltaX, deltaY);
	}

	// @Override
	// public boolean onTouchDragged (float x, float y, int pointer) {
	//
	// Log.v("touch"," onTouchDragged:" + name +" x:" + x + " y:"+ y);
	// if(this.touchable){
	// dragView.setPosition(x, y);
	// return true;
	// }
	// return super.onTouchDragged(x, y, pointer);
	// }
	@Override
	public boolean onTouchUp(float x, float y, int pointer) {
		Log.v("launcher", " onTouchUp:" + name + " x:" + x + " y:" + y);
		if(pointer > 0) return true;
		if(dismissUp){
			Log.v("launcher", "dismiss up");
			dismissUp = false;
			x = lastX;
			y = lastY;
		}
		offsetX = offsetY = -1f;
		//dragView.setPosition(x - offsetX, y - offsetY);
		dropX = x;
		dropY = y;
		this.setColor(color.r, color.g, color.b ,color.a);
		this.viewParent.onCtrlEvent(this, MSG_DRAG_END);	
		
		draging = false;
		overTarget = null;
		showBorder = 0;
		return true;//super.onTouchUp(x, y, pointer);
	}


	@Override
	public boolean fling(float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean multiTouch2(Vector2 initialFirstPointer,
			Vector2 initialSecondPointer, Vector2 firstPointer,
			Vector2 secondPointer) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void setColor(float r, float g,float b,float a){
		for(View3D view : dragList){
			view.setColor(r,g,b,a);
		}
	}
	
	
	private boolean inBorder(float x, float y){

		showBorder = 0;
		if(y >= R3D.workspace_cell_height && y < (Utils3D.getScreenHeight()-R3D.workspace_cell_height))
			if(x <= borderWidth * 2 )
				showBorder = -1;
			else if(x >= (Utils3D.getScreenWidth() - borderWidth * 2))
				showBorder = 1;
		return showBorder != 0;
	}
	public DropTarget3D getTargetOver(){
		return overTarget;
	}
	
	//teapotXu add start for Folder in Mainmenu
	public ArrayList<DropTarget3D> getDropTargetList()
	{
		return this.dropTargets;
	}

	public int getBorder(){
		return showBorder;
	}
	//teapotXu add end for Folder in Mainmenu		

	public void setBorder(int isShow){
		showBorder = isShow;
	}
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		super.draw(batch, parentAlpha);
		if(showBorder != 0){
			batch.setColor(color.r, color.g, color.b, color.a * borderOpcity);
			float y = (Utils3D.getScreenHeight() - borderHeight)/2;
			if(showBorder == -1)
				moveToLeft.draw(batch, 0, y , borderWidth, borderHeight);
			else if(showBorder == 1)
				moveToRight.draw(batch, Utils3D.getScreenWidth() - borderWidth, y, borderWidth, borderHeight);
		}
	}

	@Override
	public void setUser(float value) {
		// TODO Auto-generated method stub
		this.borderOpcity = value;
	}

	@Override
	public float getUser() {
		// TODO Auto-generated method stub
		return this.borderOpcity;
	}

	@Override
	public void onEvent(int type, BaseTween source) {
		// TODO Auto-generated method stub
		if(source == this.getTween() && type == TweenCallback.COMPLETE){
			showBorder = 0;
			this.stopTween();
		}
	}

	public void forceTouchUp() {
		if(draging){
			dismissUp  = true;
		}
	}

	public void onResume(){
		if(draging){
			Log.v("DragLayer", "onResume");
			dismissUp = true;
			onTouchUp(0, 0, 0);
		}
	}
}