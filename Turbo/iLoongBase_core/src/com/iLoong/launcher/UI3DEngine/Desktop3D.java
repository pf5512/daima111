package com.iLoong.launcher.UI3DEngine;


import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.GestureDetector3D.GestureListener3D;


public class Desktop3D extends Stage implements GestureListener3D
{
	
	private boolean waitDouble = true;
	private boolean waitLongClick = true;
	private static int DOUBLE_CLICK_TIME = 250;// 两次单击的时间间�?
	public static boolean doubleClick = false;
	private int touchDownX , touchDownY;
	private int lastDragX , lastDragY;
	private View3D foucusView = null;
	//teapotXu add start for handle actions when touch area leaves current view
	private ArrayList<View3D> touchedViewList = new ArrayList<View3D>();
	//teapotXu add end
	private View3D keyFoucusView = null;
	private long curTimeMillis;
	private boolean hasDown = false;
	public static boolean haskeyDown = false;
	private boolean ignoreClick = false;
	private boolean ignoreLongClick = false;
	private Context mContext = null;
	public boolean isOnLongClick=false;
	public static final int SCROLL_HORIZ0TAL =0;
	public static final int SCROLL_VERTICAL=1;
	public static final int SCROLL_UNINITED=-1;
	public  boolean mLeaveDesktop;
    public int mScrollTempDir;
	public Desktop3D(
			float width ,
			float height ,
			boolean stretch )
	{
		// TODO Auto-generated constructor stub
		super( width , height , stretch );
		//		if (ConfigBase.disable_double_click) {
		//			DOUBLE_CLICK_TIME = 50;
		//		}
		float fieldOfView = 35f;
		float cameraZ = (float)( height / 2 / Math.tan( fieldOfView / 2 * MathUtils.degreesToRadians ) );
		float near = 100f;
		float far = cameraZ + height;
		Camera camera = new PerspectiveCamera( fieldOfView , width , height );
		camera.near = near;
		camera.far = far;
		camera.position.set( width / 2 , height / 2 , cameraZ );
		camera.direction.set( 0 , 0 , -1 );
		this.setCamera( camera );
	}
	
	public void addView(
			View3D v3d )
	{
		if( !( v3d instanceof View3D ) )
		{
			throw new IllegalArgumentException( "A Desktop3D can only have View3D children." );
		}
		super.addActor( (Actor)v3d );
	}
	
	public void addViewAt(
			View3D v3d ,
			int index )
	{
		if( !( v3d instanceof View3D ) )
		{
			throw new IllegalArgumentException( "A Desktop3D can only have View3D children." );
		}
		this.root.addActorAt( index , (Actor)v3d );
	}
	
	public void RemoveView(
			View3D v3d )
	{
		super.removeActor( (Actor)v3d );
	}
	
	/*
	 * get the counts of ViewGroup
	 */
	public int getChildCount()
	{
		return getActors().size();
	}
	
	public View3D getViewAt(
			int index )
	{
		return (View3D)this.root.getActors().get( index );
	}
	
	public void RemoveGroupInSceen(
			int which )
	{
	}
	
	public void ignoreClick(
			boolean bool )
	{
		ignoreClick = bool;
	}
	
	public void ignoreLongClick(
			boolean bool )
	{
		ignoreLongClick = bool;
	}
	
	public boolean hasDown()
	{
		return hasDown;
	}
	
	@Override
	public void draw()
	{
		gesture.handleLongPress();
		if( !waitDouble && !ConfigBase.disable_double_click )
		{
			//			Log.v("test", "--------double waitDouble");
			if( System.currentTimeMillis() - curTimeMillis > DOUBLE_CLICK_TIME )
			{
				Log.v( "test" , "--------double single" );
				waitDouble = true;
				doubleClick = false;
				singleClick( coords.x , coords.y );
			}
			Gdx.graphics.requestRendering();
		}
		//Log.v("render", "d3d:"+root.visible);
		camera.update();
		if( !root.visible )
			return;
		batch.setProjectionMatrix( camera.combined );
		batch.begin();
		root.draw( batch , 1 );
		batch.end();
		if( hasDown )
			Gdx.graphics.requestRendering();
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		haskeyDown = true;
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getViewAt( i );
			if( !child.visible )
				continue;
			if( child.keyDown( keycode ) )
			{
				keyFoucusView = child;
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean keyTyped(
			char character )
	{
		// TODO Auto-generated method stub
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getViewAt( i );
			if( !child.visible )
				continue;
			if( child.keyTyped( character ) )
			{
				return true;
			}
		}
		return false;
	}
	
	/*keyUp消息的处理，前提是必须先收到keyDown消息，这样保证收到的消息是成对的*/
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		//		if(keyFoucusView != null){
		//			boolean ret = keyFoucusView.keyUp(keycode);
		//			keyFoucusView = null;
		//			return ret;
		//		}
		if( haskeyDown )
		{
			haskeyDown = false;
		}
		else
		{
			return false;
		}
		isOnLongClick=false;
			
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getViewAt( i );
			if( !child.visible )
				continue;
			if( child.keyUp( keycode ) )
			{
				return true;
			}
		}
		return false;
	}
	
	final Vector2 point = new Vector2();
	final Vector2 coords = new Vector2();
	GestureDetector3D gesture = new GestureDetector3D( this );
	/**
	 * for touch up and touch down
	 */
	boolean isAPairEvent = true;
	
	public boolean forceTouchUp()
	{
		//Log.v("widget", "forceTouchUp isAPairEvent:"+isAPairEvent);
		boolean[] pointerPressed = gesture.getPointerPressDown();
		if( !isAPairEvent || ( isAPairEvent && ( pointerPressed[0] || pointerPressed[1] ) ) )
		{
			isAPairEvent = true;/* now is a pair */
			boolean result = true;
			if( pointerPressed[0] == true )
			{
				result = touchUp( 0 , 0 , 0 , 0 );
			}
			if( pointerPressed[1] == true )
				result = touchUp( 0 , 0 , 1 , 0 );
			return result;
			//return touchUp(0, 0, 0, 0);
		}
		return false;
	}
	
	/**
	 * Call this to distribute a touch down event to the stage.
	 * 
	 * @param x
	 *            the x coordinate of the touch in screen coordinates
	 * @param y
	 *            the y coordinate of the touch in screen coordinates
	 * @param pointer
	 *            the pointer index
	 * @return whether an {@link Actor} in the scene processed the event or not
	 */
	public boolean touchDown(
			final int x ,
			final int y ,
			final int pointer ,
			int newParam )
	{
		long timeStamp = Gdx.input.getCurrentEventTime();
		//		if(mContext != null){
		//			AccessibilityManager accessibilityManager =
		//			        (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
		//			accessibilityManager.sendAccessibilityEvent(AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START));
		//			Log.v("cooee", "AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START");
		//		}
		//Log.e("click", "down time="+timeStamp);
		if( mContext != null )
		{
			Intent localIntent = new Intent();
			localIntent.setAction( "com.konkagene.TOUCHED" );
			mContext.sendBroadcast( localIntent );
			//		    Log.v("FaceDetcting", "sendBroadcast(localIntent):com.konkagene.TOUCHED=" + String.valueOf(System.currentTimeMillis()));
		}
		if( timeStamp > UtilsBase.resumeTime && timeStamp < UtilsBase.pauseTime )
		{
		}
		else
		{
			//Log.i("click", "down dismiss");
			return true;
		}
		isAPairEvent = false;
		// Log.v("click","Desktop touchDown");
		if( !waitDouble )
		{//等待单击时 不发第二个消息	
			return true;
		}
		gesture.touchDown( x , y , pointer , newParam );
		return true;
		// return super.touchDown(x, y, pointer, newParam);
	}
	
	/**
	 * Call this to distribute a touch Up event to the stage.
	 * 
	 * @param x
	 *            the x coordinate of the touch in screen coordinates
	 * @param y
	 *            the y coordinate of the touch in screen coordinates
	 * @param pointer
	 *            the pointer index
	 * @return whether an {@link Actor} in the scene processed the event or not
	 */
	public boolean touchUp(
			int x ,
			int y ,
			int pointer ,
			int button )
	{
		mScrollTempDir =Desktop3D.SCROLL_UNINITED;
		isOnLongClick =false;
		//		Log.v("test","Desktop touchUp:" + waitDouble);
		long timeStamp = Gdx.input.getCurrentEventTime();
		//Log.e("click", "up time="+timeStamp);
		if( timeStamp > UtilsBase.resumeTime && timeStamp < UtilsBase.pauseTime )
		{
		}
		else if( isAPairEvent )
		{
			Log.i( "widget" , "up dismiss" );
			return true;
		}
		isAPairEvent = true;
		if( !waitDouble )
		{
			tap( x , y , 2 );
			return true; //等待单击时 直接发tap
		}
		gesture.touchUp( x , y , pointer , button );
		return true;
		// return super.touchUp(x, y, pointer, button);
	}
	
	/**
	 * Call this to distribute a touch dragged event to the stage.
	 * 
	 * @param x
	 *            the x coordinate of the touch in screen coordinates
	 * @param y
	 *            the y coordinate of the touch in screen coordinates
	 * @param pointer
	 *            the pointer index
	 * @return whether an {@link Actor} in the scene processed the event or not
	 */
	public boolean touchDragged(
			int x ,
			int y ,
			int pointer )
	{
		long timeStamp = Gdx.input.getCurrentEventTime();
		//Log.e("click", "drag time="+timeStamp);
		if( timeStamp > UtilsBase.resumeTime && timeStamp < UtilsBase.pauseTime )
		{
		}
		else
		{
			//Log.i("click", "drag dismiss");
			return true;
		}
		if( !waitDouble )
			return true; //等待单击时 不发第二个消息
		gesture.touchDragged( x , y , pointer );
		return true;
		// return super.touchDragged(x, y, pointer);
	}
	
	public boolean singleClick(
			float x ,
			float y )
	{
		//		Log.v("click", "single x:" + x + " y:" + y);
		if( ignoreClick )
			return true;
		if( this.foucusView != null )
		{
			point.x = x;
			point.y = y;
			foucusView.toLocalCoordinates( point );
			this.foucusView.onClick( point.x , point.y );
			return true;
		}
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getViewAt( i );
			if( !child.touchable || !child.visible )
				continue;
			point.x = x;
			point.y = y;
			// Group.toChildCoordinates(child, x, y, point);
			if( child.pointerInParent( point.x , point.y ) )
			{
				child.toLocalCoordinates( point );
				if( child.onClick( point.x , point.y ) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean doubleClick(
			float x ,
			float y )
	{
		//		Log.v("click", "double x:" + x + " y:" + y);
		if( this.foucusView != null )
		{
			foucusView.toLocalCoordinates( point );
			this.foucusView.onDoubleClick( point.x , point.y );
			return true;
		}
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getViewAt( i );
			if( !child.touchable || !child.visible )
				continue;
			point.x = x;
			point.y = y;
			// Group.toChildCoordinates(child, x, y, point);
			if( child.pointerInParent( point.x , point.y ) )
			{
				child.toLocalCoordinates( point );
				if( child.onDoubleClick( point.x , point.y ) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean longClick(
			float x ,
			float y )
	{
		//		Log.v("click", "long");
		if( this.foucusView != null )
		{
			foucusView.toLocalCoordinates( point );
			this.foucusView.onLongClick( point.x , point.y );
			return true;
		}
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getViewAt( i );
			if( !child.touchable || !child.visible )
				continue;
			point.x = x;
			point.y = y;
			// Group.toChildCoordinates(child, x, y, point);
			if( child.pointerInParent( point.x , point.y ) )
			{
				child.toLocalCoordinates( point );
				if( child.onLongClick( point.x , point.y ) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private View3D onDoubleView = null;
	
	@Override
	public boolean tap(
			int x ,
			int y ,
			int count )
	{
		// TODO Auto-generated method stub
		//		Log.v("tap", " tap:" + count + " x:" + x + " y:" + y);
		this.toStageCoordinates( x , y , point );
		ViewGroup3D root = (ViewGroup3D)this.getViewAt( 0 );
		View3D focus = null;
		if( root != null )
		{
			focus = root.hit( point.x , point.y );
		}
		//		if(!doubleClick){
		//			singleClick(point.x, point.y);
		//			waitDouble = true;
		//			return true;
		//		}
		if( waitDouble == true )
		{
			waitDouble = false;
			curTimeMillis = System.currentTimeMillis();
			onDoubleView = focus;
			//			Log.v("test", "--------double one:" + focus);
		}
		else
		{
			if( true/*onDoubleView == focus*/)
			{
				waitDouble = true;
				doubleClick = false;
				doubleClick( point.x , point.y );
				//				Log.v("test", "--------double occur");
			}
			else
			{
				//				Log.v("test", "--------double not occur:" + onDoubleView + " focus:" + focus);
			}
			onDoubleView = null;
			//			Log.v("test", "--------double two");
		}
		return true;
	}
	
	@Override
	public boolean longPress(
			int x ,
			int y )
	{
		// TODO Auto-generated method stub
		//		Log.v("gesture", "longPress");
		if( ignoreLongClick )
			return true;
		this.toStageCoordinates( x , y , point );
		return this.longClick( point.x , point.y );
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		// TODO Auto-generated method stub
		//		Log.v("gesture", "fling");
		if( this.foucusView != null )
		{
			this.foucusView.fling( velocityX , velocityY );
			return true;
		}
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getViewAt( i );
			if( !child.touchable || !child.visible )
				continue;
			if( child.fling( velocityX , velocityY ) )
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean pan(
			int x ,
			int y ,
			int deltaX ,
			int deltaY )
	{
		// TODO Auto-generated method stub
		this.toStageCoordinates( x , y , point );
		waitDouble = true; // 取消前一次的单击操作
		if( this.foucusView != null )
		{
			foucusView.toLocalCoordinates( point );
			foucusView.scroll( (int)point.x , (int)point.y , deltaX , deltaY );
			return true;
		}
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getViewAt( i );
			if( !child.touchable || !child.visible )
				continue;
			if( child.pointerInParent( point.x , point.y ) )
			{
				child.toLocalCoordinates( point );
				if( child.scroll( (int)point.x , (int)point.y , deltaX , deltaY ) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean zoom(
			float originalDistance ,
			float currentDistance )
	{
		// TODO Auto-generated method stub
		//		Log.v("gesture", "zoom");
		if( this.foucusView != null )
		{
			foucusView.zoom( originalDistance , currentDistance );
			return true;
		}
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getViewAt( i );
			if( !child.touchable || !child.visible )
				continue;
			if( child.zoom( originalDistance , currentDistance ) )
			{
				return true;
			}
		}
		return false;
	}
	
	Vector2 point1 = new Vector2();
	Vector2 point2 = new Vector2();
	
	@Override
	public boolean pinch(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		// TODO Auto-generated method stub
		//		Log.v("gesture", "pinch");
		toStageCoordinates( (int)initialFirstPointer.x , (int)initialFirstPointer.y , point );
		toStageCoordinates( (int)initialSecondPointer.x , (int)initialSecondPointer.y , coords );
		toStageCoordinates( (int)firstPointer.x , (int)firstPointer.y , point1 );
		toStageCoordinates( (int)secondPointer.x , (int)secondPointer.y , point2 );
		if( this.foucusView != null )
		{
			foucusView.multiTouch2( point , coords , point1 , point2 );
			return true;
		}
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getViewAt( i );
			if( !child.touchable || !child.visible )
				continue;
			if( child.multiTouch2( point , coords , point1 , point2 ) )
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onTouchDown(
			int x ,
			int y ,
			int pointer )
	{
		mScrollTempDir =Desktop3D.SCROLL_UNINITED;
		mLeaveDesktop=false;
		//Utils3D.isLowMemory();
		//Utils3D.showPidMemoryInfo("mem");
		//Log.i("pack", "texture3D,minPack,realPack="+MyPixmapPacker.texture3DSize+","+MyPixmapPacker.minPackSize+","+MyPixmapPacker.realPackSize);
		Log.v( "desktop3d" , "onTouchDown" );
		hasDown = true;
		this.toStageCoordinates( x , y , point );
		if( this.foucusView != null )
		{
			foucusView.toLocalCoordinates( point );
			foucusView.onTouchDown( point.x , point.y , pointer );
			return true;
		}
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getViewAt( i );
			if( !child.touchable || !child.visible )
				continue;
			coords.x = point.x;
			coords.y = point.y;
			// Group.toChildCoordinates(child, x, y, point);
			if( child.pointerInParent( coords.x , coords.y ) )
			{
				child.toLocalCoordinates( coords );
				if( child.onTouchDown( coords.x , coords.y , pointer ) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	// public void toStageCoordinates (int x, int y, Vector2 out) {
	// //final Vector3 tmp = new Vector3();
	// //camera.unproject(tmp.set(x, y, 99));
	// out.x = x;
	// out.y = Utils3D.getScreenHeight()-y;
	// }
	@Override
	public boolean onTouchDragged(
			int x ,
			int y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		this.toStageCoordinates( x , y , point );
		//teapotXu add start for handle actions when touch area leaves current view
		if( this.touchedViewList != null && this.touchedViewList.size() > 0 )
		{
			for( View3D view : touchedViewList )
			{
				//when touch area is out of the views received touch down event, so HandleTouchedLeave function
				if( false == view.pointerInAbs( point.x , point.y ) )
				{
					view.handleActionWhenTouchLeave();
					break;
				}
			}
		}
		//teapotXu add end
		if( this.foucusView != null )
		{
			foucusView.toLocalCoordinates( point );
			foucusView.onTouchDragged( point.x , point.y , pointer );
			return true;
		}
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getViewAt( i );
			if( !child.touchable || !child.visible )
				continue;
			point.x = point.x;
			point.y = point.y;
			// Group.toChildCoordinates(child, x, y, point);
			if( child.pointerInParent( point.x , point.y ) )
			{
				child.toLocalCoordinates( coords );
				if( child.onTouchDragged( point.x , point.y , pointer ) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onTouchUp(
			int x ,
			int y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		Log.v( "desktop3d" , "onTouchUp  x:" + x + " y:" + y );
		hasDown = false;
		this.toStageCoordinates( x , y , point );
		this.toStageCoordinates( x , y , coords );
		//teapotXu add start for handle actions when touch area leaves current view
		if( this.touchedViewList != null && this.touchedViewList.size() > 0 )
		{
			//when touchUp, release all the touchedView 
			Iterator<View3D> iter = touchedViewList.iterator();
			while( iter.hasNext() )
			{
				View3D view = iter.next();
				if( touchedViewList.contains( view ) )
				{
					iter.remove();
				}
			}
		}
		//teapotXu add end
		if( this.foucusView != null )
		{
			//Log.e("desktop3d", "onTouchUp 2 foucusView:"+foucusView);
			foucusView.toLocalCoordinates( point );
			if( foucusView.onTouchUp( point.x , point.y , pointer ) )
			{
				waitDouble = true;//不发click消息
				return true;
			}
			if( ( !doubleClick && !waitDouble ) || ( !waitDouble && ConfigBase.disable_double_click ) )
			{ //直接发click
				waitDouble = true;
				doubleClick = false;
				singleClick( coords.x , coords.y );
			}
			return true;
		}
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getViewAt( i );
			if( !child.touchable || !child.visible )
				continue;
			coords.x = point.x;
			coords.y = point.y;
			// Group.toChildCoordinates(child, x, y, point);
			//Log.e("desktop3d", "onTouchUp 3 child:"+child+" coords.x:"+coords.x+" coords.y:"+coords.y);
			if( child.pointerInParent( coords.x , coords.y ) )
			{
				child.toLocalCoordinates( coords );
				//Log.e("desktop3d", "onTouchUp 4 child:"+child+" coords.x:"+coords.x+" coords.y:"+coords.y);
				if( child.onTouchUp( coords.x , coords.y , pointer ) )
				{
					//Log.e("desktop3d", "onTouchUp 5 child:"+child);
					waitDouble = true;//不发click消息
					return true;
				}
			}
		}
		if( ( !doubleClick && !waitDouble ) || ( !waitDouble && ConfigBase.disable_double_click ) )
		{ //直接发click
			waitDouble = true;
			doubleClick = false;
			return singleClick( coords.x , coords.y );
		}
		return false;
	}
	
	@Override
	public void toStageCoordinates(
			int x ,
			int y ,
			Vector2 out )
	{
		out.x = x;
		out.y = UtilsBase.getScreenHeight() - y - 1;
		if( out.x < 0 )
		{
			out.x = 0;
		}
		if( out.y < 0 )
		{
			out.y = 0;
		}
	}
	
	public void setFocus(
			View3D focus )
	{
		foucusView = focus;
	}
	
	public View3D getFocus()
	{
		return this.foucusView;
	}
	
	//teapotXu add start for handle actions when touch area leaves current view
	public void addTouchedView(
			View3D touched )
	{
		//this.touchedView = touched;
		if( !touchedViewList.contains( touched ) )
		{
			touchedViewList.add( touched );
		}
	}
	
	public void removeTouchedView(
			View3D touched )
	{
		//this.touchedView = touched;
		if( touchedViewList.contains( touched ) )
		{
			touchedViewList.remove( touched );
		}
	}
	
	//teapotXu add end
	public void resetGesture()
	{
		gesture.reset();
	}
	
	public void setContext(
			Context c )
	{
		this.mContext = c;
	}
}
