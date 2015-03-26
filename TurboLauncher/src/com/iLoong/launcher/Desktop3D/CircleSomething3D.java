package com.iLoong.launcher.Desktop3D;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewCircled3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupCircled3D;


/* 为解决圈选滑动和翻页滑动的冲突问题，在这里添加一个View3D，专门用来绘制圈选轨迹 */
public class CircleSomething3D extends View3D
{
	
	private static final int CIRCLE_DST_TOLERANCE = 20;
	//private static final int CIRCLE_RADIUS = 50;
	private static boolean bSendCircleDownEvent = false;
	private Vector2 downPath = new Vector2();
	private Vector2 dragPath = new Vector2();
	private float totalDragDst = 0;
	private CellLayout3D currentLayout;
	private Workspace3D workspace3D;
	private static boolean touchDownPointer0 = false;
	private static boolean touchDownPointer1 = false;
	
	public CircleSomething3D(
			String name )
	{
		super( name );
	}
	
	private void circle_somethingReset()
	{
		View3D childView;
		Color selectColor = new Color( 1 , 1 , 1 , 1 );
		if( currentLayout == null )
			return;
		int Count = currentLayout.getChildCount();
		for( int i = 0 ; i < Count ; i++ )
		{
			childView = currentLayout.getChildAt( i );
			{
				selectColor.r = 1.0f;
				selectColor.g = 1.0f;
				selectColor.b = 1.0f;
			}
			if( childView instanceof ViewCircled3D )
			{
				if( ( (ViewCircled3D)childView ).getCircled() == true )
				{
					( (ViewCircled3D)childView ).setCircled( false );
					childView.setColor( selectColor );
				}
			}
			if( childView instanceof ViewGroupCircled3D )
			{
				if( ( (ViewGroupCircled3D)childView ).getCircled() == true )
				{
					( (ViewGroupCircled3D)childView ).setCircled( false );
					( (ViewGroupCircled3D)childView ).setGroupColor( selectColor );
				}
			}
			if( childView instanceof Icon3D )
			{
				if( ( (Icon3D)childView ).isSelected() == true )
				{
					( (Icon3D)childView ).cancelSelected();
				}
			}
		}
	}
	
	private void setCurrentLayout()
	{
		workspace3D = (Workspace3D)this.viewParent;
		currentLayout = workspace3D.getCurrentCellLayout();
	}
	
	private boolean multiTouch2DiscardDrag()
	{
		if( totalDragDst > ( getWidth() + getHeight() ) * 2.5 )
		{
			return true;
		}
		return false;
	}
	
	private void circle_somethingSelected()
	{
		boolean bSelected = false;
		ViewCircled3D tmpViewCircled;
		View3D childView;
		Color selectColor = new Color( 1 , 1 , 1 , 1 );
		selectColor.r = 0.7f;
		selectColor.g = 0.0f;
		selectColor.b = 0.1f;
		selectColor.a = 0.8f;
		if( currentLayout == null )
			return;
		int Count = currentLayout.getChildCount();
		for( int i = 0 ; i < Count ; i++ )
		{
			childView = currentLayout.getChildAt( i );
			if( childView instanceof ViewCircled3D )
			{
				tmpViewCircled = (ViewCircled3D)childView;
				if( tmpViewCircled.getCircled() == false )
				{
					bSelected = Utils3D.lineInActor( childView , downPath , dragPath );
					if( bSelected == true )
					{
						tmpViewCircled.setCircled( true );
						tmpViewCircled.setColor( selectColor );
					}
				}
			}
			if( childView instanceof ViewGroupCircled3D )
			{
				ViewGroupCircled3D temp = (ViewGroupCircled3D)childView;
				if( temp.getCircled() == false )
				{
					bSelected = Utils3D.lineInActor( childView , downPath , dragPath );
					if( bSelected == true )
					{
						temp.setCircled( true );
						temp.setGroupColor( selectColor );
					}
				}
			}
		}
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer == 0 )
			touchDownPointer0 = false;
		if( pointer == 1 )
		{
			touchDownPointer1 = false;
		}
		if( bSendCircleDownEvent == true && touchDownPointer1 == false )
		{
			circle_sendUpMsg( x , y );
			releaseFocus();
			workspace3D.DealCircleSomethingResult( x , y );
			this.hide();
			return false;
		}
		if( touchDownPointer0 == false && touchDownPointer1 == false )
		{
			this.hide();
			return false;
		}
		return true;
	}
	
	/*完成圈选和轨迹移动*/
	private void circle_sendMoveMsg(
			float x ,
			float y )
	{
		SendMsgToAndroid.sendOurEventMsg( Messenger.CIRCLE_EVENT_DRAG , x , height - y );
		//		circleSendMsg(CIRCLE_EVENT_DRAG, x,
		//				y, null);
		circle_somethingSelected();
	}
	
	private void circle_sendUpMsg(
			float x ,
			float y )
	{
		//circleSendMsg(CIRCLE_EVENT_UP, x, y, null);
		SendMsgToAndroid.sendOurEventMsg( Messenger.CIRCLE_EVENT_UP , x , height - y );
		totalDragDst = 0;
		bSendCircleDownEvent = false;
	}
	
	private void circle_sendToastMsg(
			float x ,
			float y )
	{
		circle_somethingReset();
		SendMsgToAndroid.sendCircleToastMsg( R3D.circle_DstOverToast );
	}
	
	private void circle_sendDownMsg()
	{
		SendMsgToAndroid.sendOurEventMsg( Messenger.CIRCLE_EVENT_DOWN , downPath.x , height - downPath.y );
		setCurrentLayout();
		circle_somethingReset();
		totalDragDst = 0;
		Log.v( "CircleSomething" , "send CIRCLE_EVENT_DOWN" );
	}
	
	private void circle_operate(
			float x ,
			float y )
	{
		float moveDst = dragPath.dst( x , y );
		if( moveDst > CIRCLE_DST_TOLERANCE )
		{
			totalDragDst += moveDst;
			dragPath.set( x , y );
			if( multiTouch2DiscardDrag() == true )
			{
				circle_sendToastMsg( x , y );
			}
			else
			{
				circle_sendMoveMsg( x , y );
			}
		}
	}
	
	@Override
	public boolean onTouchDragged(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer == 1 && touchDownPointer1 == true && bSendCircleDownEvent == true )
		{
			if( multiTouch2DiscardDrag() == true )
			{
				return true;
			}
			circle_operate( x , y );
		}
		return true;
	}
	
	@Override
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		touchDownPointer0 = true;
		touchDownPointer1 = true;
		if( multiTouch2DiscardDrag() == true )
		{
			return true;
		}
		if( bSendCircleDownEvent == false )
		{
			( (Workspace3D)this.viewParent ).releaseFocus();
			requestFocus();
			downPath.set( firstPointer );
			dragPath.set( firstPointer );
			circle_sendDownMsg();
			bSendCircleDownEvent = true;
		}
		circle_operate( secondPointer.x , secondPointer.y );
		return true;
	}
}
