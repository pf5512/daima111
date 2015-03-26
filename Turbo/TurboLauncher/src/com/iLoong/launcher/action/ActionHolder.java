package com.iLoong.launcher.action;


import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.data.Widget3DInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.search.QSearchGroup;


public class ActionHolder extends ViewGroup3D implements IActionNext
{
	
	public static ActionHolder instance;
	ActionData mActionData;
	public ActionView action;
	private int currStep = 0;
	private final int STEP_CLOCK = 0;
	private final int STEP_WEATHER = 1;
	private final int STEP_DOCKBAR = 2;
	private final int STEP_HOTFUNCTION = 3;
	private final int STEP_RECENT = 4;
	private final int STEP_DOCKBAR_EX = 5;
	private final int STEP_QUICK_SEARCH = 6;
	private final ArrayList<Integer> taskList = new ArrayList<Integer>();
	public boolean IsSearchShow = false;
	public boolean IsRecentShow = true;
	ArrayList<ActionView> mActionViews = new ArrayList<ActionView>();
	
	public ActionHolder(
			String name )
	{
		super( name );
		ActionData.isActionShow = true;
		instance = this;
		init();
	}
	
	public static ActionHolder getInstance()
	{
		return instance;
	}
	
	private void init()
	{
		QSearchGroup.canShow = false;
		mActionData = ActionData.getInstance();
		mActionData.setShowHotFront( true );
		mActionData.setShowWorkspaceFront( true );
		//mActionData.setFrontPosition(0,  iLoongLauncher.getInstance().d3dListener.root.getHotSeatBar().height );
		mActionData.setFrontPosition( iLoongLauncher.getInstance().d3dListener.root.getHotSeatBar().height , iLoongLauncher.getInstance().d3dListener.root.getHotSeatBar().height );
		addAction( ActionFactory() );
	}
	
	public void addAction(
			ActionView v )
	{
		for( int i = 0 ; i < this.getChildCount() ; i++ )
		{
			if( this.getChildAt( i ) instanceof ActionView )
			{
				ActionView action = (ActionView)this.getChildAt( i );
				//action.ondetach();
				action.remove();
			}
		}
		mActionViews.add( v );
		this.addView( v );
		//		if(currStep!=mActionViews.size()-1)
		//			v.hide();
		//		else
		//			v.show();
	}
	
	public void setClockIns(
			View3D clock )
	{
		ActionData.getInstance().setFocusView( clock );
		action.setFocusView( clock );
		action.bringToFront();
	}
	
	public void setClockInfo(
			Widget3DInfo clockInfo )
	{
		ActionData.getInstance().setClockInfo( clockInfo );
	}
	
	public void hide()
	{
		super.hide();
		action.hide();
	}
	
	public void show()
	{
		super.show();
		action.show();
	}
	
	public void ActionChanged()
	{
		//if(mActionData.getCurrAction()!=action)
		{
			//action.requestFocus();
			action.next();
		}
	}
	
	public void onClockStateChanged()
	{
		if( currStep == STEP_CLOCK )
		{
			ActionChanged();
		}
	}
	
	public void onDockBarChanged()
	{
		if( currStep == STEP_DOCKBAR )
		{
			ActionChanged();
		}
	}
	
	public void onQuickSearchStarted()
	{
		//用户自己发现功能后从这里标志已经关掉了
		IsSearchShow = true;
		ActionData.isActionShow = false;
		this.bringToFront();
		if( currStep == STEP_QUICK_SEARCH )
		{
			action.show();
		}
		else
		{
			if( currStep == STEP_DOCKBAR_EX )
			{
				action.ondetach();
			}
			action.remove();
			action.hide();
			mActionData.setValidity( ActionData.VALIDITY_LOCK_NORMAL );
			if( IsRecentShow )
				onDetach();
		}
	}
	
	public void onDockBarStarted()
	{
		if( IsSearchShow )
			return;
		if( action instanceof ActionDockbarEx )
		{
			action.show();
		}
		else
		{
			//			if(action!=null){
			//				action.ondetach();
			//			}
			this.bringToFront();
			currStep = STEP_DOCKBAR_EX;
			addAction( ActionFactory() );
			action.bringToFront();
		}
	}
	
	public void onRecentStarted()
	{
		if( IsRecentShow )
			return;
		this.bringToFront();
		if( action instanceof ActionRecent )
		{
			action.show();
		}
		else
		{
			if( action != null )
			{
				action.ondetach();
			}
			currStep = STEP_RECENT;
			addAction( ActionFactory() );
			action.bringToFront();
		}
	}
	
	public boolean isOver()
	{
		if( IsRecentShow && IsSearchShow )
			return true;
		else
			return false;
	}
	
	public ActionView ActionFactory()
	{
		taskList.add( currStep );
		switch( currStep )
		{
			case STEP_CLOCK:
				action = new ActionTimeClock( "TimeClockAction" );
				break;
			case STEP_WEATHER:
				action = new ActionWeatherClock( "WeatherClockAction" );
				break;
			case STEP_DOCKBAR:
				action = new ActionDockbar( "ActionDockbar" );
				break;
			case STEP_HOTFUNCTION:
				action = new ActionHotFunction( "ActionHotFunction" );
				break;
			case STEP_RECENT:
				IsRecentShow = true;
				action = new ActionRecent( "ActionRecent" );
				break;
			case STEP_DOCKBAR_EX:
				//				if(IsSearchShow){
				//					onDetach();
				//					action =null;
				//					return null;
				//				}
				//					
				IsSearchShow = true;
				action = new ActionDockbarEx( "ActionDockbarEx" );
				break;
			case STEP_QUICK_SEARCH:
				action = new ActionQuckSearch( "ActionQuckSearch" );
				break;
		//			default:
		//				onDetach();
		}
		return action;
	}
	
	public Object next()
	{
		currStep++;
		//如果是最近使用调用这个函数，先判断是否已经显示了快速搜索，如果已经显示表示需要释放所有内容了
		if( currStep == STEP_DOCKBAR_EX )
		{
			action.hide();
			if( IsSearchShow )
			{
				action.ondetach();
				action = null;
				onDetach();
			}
			return true;
		}
		if( currStep > STEP_QUICK_SEARCH )
		{
			action.hide();
			if( IsRecentShow )
			{
				action.remove();
				onDetach();
				action = null;
			}
			return true;
		}
		ActionFactory();
		if( action != null )
		{
			addAction( action );
			action.bringToFront();
		}
		return true;
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		if( sender instanceof ActionView )
		{
			switch( event_id )
			{
				case ActionView.EVENT_DO_NEXT:
				{
					next();
					break;
				}
				case ActionView.EVENT_DO_HIDE:
				{
					action.hide();
					break;
				}
				case ActionView.EVENT_DO_DESTORY:
				{
					onDetach();
					break;
				}
			}
		}
		return super.onCtrlEvent( sender , event_id );
	}
	
	public void onDetach()
	{
		//if( !isOver() )
		//{
		//}
		ActionData.isActionShow = false;
		mActionViews.clear();
		this.removeAllViews();
		this.remove();
		TextureRegion[] tr = ActionData.bg.getPatches();
		for( int i = 0 ; i < tr.length ; i++ )
		{
			if( tr[i] != null && tr[i].getTexture() != null )
			{
				tr[i].getTexture().dispose();
			}
		}
		ActionData.getInstance().setShowHotFront( false );
		ActionData.getInstance().setShowWorkspaceFront( false );
		ActionData.getInstance().setValidity( ActionData.VALIDITY_LOCK_NORMAL );
		instance = null;
	}
}
