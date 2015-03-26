package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewCircled3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupCircled3D;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class DealCircleSomething extends View3D
{
	
	private Workspace3D mworkspace3D;
	private View3D actorBefore;
	private ArrayList<View3D> mCircleSomething;
	private boolean bPopWndExist = false;
	private View3D childView;
	private FolderIcon3D folderIcon;
	private Tween animTween;
	
	public DealCircleSomething(
			String name ,
			Workspace3D workspace3D )
	{
		super( name );
		mworkspace3D = workspace3D;
		mCircleSomething = new ArrayList<View3D>();
	}
	
	public void addIconView(
			ViewGroup3D view3D )
	{
		actorBefore = mworkspace3D.getChildAt( mworkspace3D.getChildCount() - 1 );
		mworkspace3D.addViewAfter( actorBefore , view3D );
	}
	
	public void addFolderView(
			FolderIcon3D folder )
	{
		if( mworkspace3D.addInCurrenScreen( folder , (int)folder.x , (int)folder.y , false ) )
		{
			iLoongLauncher.getInstance().addFolderInfoToSFolders( folder.mInfo );
		}
	}
	
	private void buildCircleSomethingData()
	{
		final CellLayout3D currentLayout = mworkspace3D.getCurrentCellLayout();
		if( currentLayout == null )
		{
			Log.v( "cooee" , "buildCircleSomethingData --- current cell is null " );
			return;
		}
		int Count = currentLayout.getChildCount();
		for( int i = 0 ; i < Count ; i++ )
		{
			childView = currentLayout.getChildAt( i );
			if( childView instanceof ViewCircled3D )
			{
				if( ( (ViewCircled3D)childView ).getCircled() == true )
				{
					mCircleSomething.add( childView );
				}
			}
			if( childView instanceof ViewGroupCircled3D )
			{
				if( ( (ViewGroupCircled3D)childView ).getCircled() == true )
				{
					mCircleSomething.add( childView );
				}
			}
		}
	}
	
	private int getCircleFolderCount()
	{
		int folderCount = 0;
		int Count = mCircleSomething.size();
		View3D myActor;
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = mCircleSomething.get( i );
			if( myActor instanceof Icon3D )
			{
			}
			else
			{
				folderIcon = (FolderIcon3D)myActor;
				folderCount++;
			}
		}
		return folderCount;
	}
	
	private void startMergeAnimation()
	{
		folderIcon.addFolderNode( mCircleSomething , true );
		mCircleSomething.clear();
	}
	
	private void dealEvent_CircleAutoMerge(
			float moveX ,
			float moveY )
	{
		int folderCount = getCircleFolderCount();
		int Count = mCircleSomething.size();
		/*
		 * 只包含一个文件夹 包含至少一个Icon3D 文件夹包含的图标数量和圈选的数量和小于最大数，自动归并；否则提示用户
		 */
		if( folderCount == 1 && Count > 1 )
		{
			int IconNum = folderIcon.mInfo.contents.size();
			resetToNothingCircle();
			if( ( IconNum + Count - 1 ) <= R3D.folder_max_num )
			{
				startMergeAnimation();
			}
			else
			{
				SendMsgToAndroid.sendOurToastMsg( R3D.folder3D_full );
				SendMsgToAndroid.sendShowWorkspaceMsg();
			}
		}
		else
		{
			dealEvent_CirclePopupWnd( moveX , moveY );
		}
	}
	
	private void dealEvent_CirclePopupWnd(
			float moveX ,
			float moveY )
	{
		if( bPopWndExist == false && mCircleSomething.size() > 0 )
		{
			circlePopWnd3D circlePop = new circlePopWnd3D( "circlePopWnd3D" , moveX , moveY );
			actorBefore = mworkspace3D.getChildAt( mworkspace3D.getChildCount() - 1 );
			mworkspace3D.addViewAfter( actorBefore , circlePop );
			// circlePop.requestFocus();
			circlePop.buildElements();
			bPopWndExist = true;
			circlePop.setScale( 0 , 0 );
			circlePop.startTween( View3DTweenAccessor.SCALE_XY , Quad.IN , 0.2f , 1 , 1 , 0 );
		}
		else
			SendMsgToAndroid.sendShowWorkspaceMsg();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iLoong.launcher.UI3DEngine.View3D#onEvent(int,
	 * aurelienribon.tweenengine.BaseTween)
	 */
	// @Override
	// public void onEvent(int type, BaseTween source) {
	// // TODO Auto-generated method stub
	// if (source ==animTween && type == TweenCallback.COMPLETE )
	// {
	// animTween=null;
	// resetToNothingCircle();
	// SendMsgToAndroid.sendShowWorkspaceMsg();
	// }
	// }
	public void DealCircleSomethingResult(
			float x ,
			float y ,
			int circle_State )
	{
		// buildCircleSomethingData();
		mCircleSomething.clear();
		buildCircleSomethingData();
		if( circle_State == Workspace3D.CIRCLE_POPUP_WND )
		{
			dealEvent_CirclePopupWnd( x , y );
		}
		else
		{
			dealEvent_CircleAutoMerge( x , y );
		}
	}
	
	public boolean dealEvent_circlePopWnd3D(
			int event_id )
	{
		boolean retVal = true;
		Log.v( "ICONGROUP3D" , " CellLayout3D dealEvent_circlePopWnd3D mCircleSomething.size=" + mCircleSomething.size() );
		switch( event_id )
		{
			case circlePopWnd3D.CIRCLE_POP_AUTOSORT_EVENT:
				resetToNothingCircle();
				dealAutoSort();
				break;
			case circlePopWnd3D.CIRCLE_POP_DELALL_EVENT:
				dealDelAll();
				break;
			case circlePopWnd3D.CIRCLE_POP_MULTISEL_EVENT:
				dealMultiSelect();
				SendMsgToAndroid.sendShowWorkspaceMsg();
				break;
			case circlePopWnd3D.CICLE_POP_OVERLAP_EVENT:
				dealOverLap();
				SendMsgToAndroid.sendShowWorkspaceMsg();
				break;
			case circlePopWnd3D.CIRCLE_POP_CREATEFOLDER_EVENT:
				dealCreateFolder();
				break;
			case circlePopWnd3D.CIRCLE_POP_DESTROY_EVENT:
				resetToNothingCircle();
				mCircleSomething.clear();
				// SendMsgToAndroid.sendShowWorkspaceMsg();
				break;
			default:
				retVal = false;
				break;
		}
		return retVal;
	}
	
	public void resetToNothingCircle()
	{
		bPopWndExist = false;
		View3D findPopView = mworkspace3D.findView( "circlePopWnd3D" );
		if( findPopView != null )
		{
			findPopView.releaseFocus();
			mworkspace3D.removeView( findPopView );
		}
		// int Count = mworkspace3D.getCurrentCellLayout().getChildCount() - 1;
		int Count = mCircleSomething.size();
		// if (mCircleSomething.size() > 0 && Count >= 0)
		if( Count >= 0 )
		{
			Color selectColor = new Color( 1 , 1 , 1 , 1 );
			for( int i = 0 ; i < Count ; i++ )
			{
				// childView =
				// mworkspace3D.getCurrentCellLayout().getChildAt(i);
				childView = mCircleSomething.get( i );
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
						childView.setColor( selectColor );
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
	}
	
	private void dealAutoSort()
	{
		if( mCircleSomething.size() > 1 )
		{
			// Point point = new Point();
			CellLayout3D cellLayout = mworkspace3D.getCurrentCellLayout();
			if( cellLayout == null )
			{
				Log.v( "cooee" , "dealAutoSort --- current cell is null " );
				return;
			}
			cellLayout.calcCoordinate( mCircleSomething.get( 0 ) );
			// point.x = (int) mCircleSomething.get(0).x;
			// point.y = (int) mCircleSomething.get(0).y;
			for( int i = 0 ; i < mCircleSomething.size() ; i++ )
			{
				childView = mCircleSomething.get( i );
				if( childView.getParent() instanceof CellLayout3D )
				{
					cellLayout.removeView( childView );
				}
			}
			cellLayout.addAutoSortView( mCircleSomething );
		}
		else
		{
			/* 提示用户选择多个图标排列 */
			SendMsgToAndroid.sendOurToastMsg( R3D.circle_selectMutiToOperToast );
			SendMsgToAndroid.sendShowWorkspaceMsg();
		}
		// SendMsgToAndroid.sendOurToastMsg(R3D.circle_notSupportToast);
		mCircleSomething.clear();
	}
	
	private void dealMultiSelect()
	{
		View3D findPopView = mworkspace3D.findView( "circlePopWnd3D" );
		if( findPopView != null )
		{
			mworkspace3D.removeView( findPopView );
		}
		bPopWndExist = false;
		int Icon3DCount = 0;
		int Count = mCircleSomething.size();
		// if (mCircleSomething.size() > 0 && Count >= 0) {
		if( Count >= 0 )
		{
			Color selectColor = new Color( 1 , 1 , 1 , 1 );
			for( int i = 0 ; i < Count ; i++ )
			{
				// childView =
				// mworkspace3D.getCurrentCellLayout().getChildAt(i);
				childView = mCircleSomething.get( i );
				if( childView instanceof Icon3D )
				{
					if( ( (ViewCircled3D)childView ).getCircled() == true )
					{
						( (ViewCircled3D)childView ).setCircled( false );
						( (Icon3D)childView ).selected();
						childView.setColor( selectColor );
						Icon3DCount++;
					}
				}
				if( childView instanceof ViewGroupCircled3D )
				{
					if( ( (ViewGroupCircled3D)childView ).getCircled() == true )
					{
						( (ViewGroupCircled3D)childView ).setCircled( false );
						( (ViewGroupCircled3D)childView ).setGroupColor( selectColor );
						( (ViewGroupCircled3D)childView ).setColor( selectColor );
					}
				}
			}
		}
		mCircleSomething.clear();
		if( Icon3DCount == 0 )
		{
			SendMsgToAndroid.sendOurToastMsg( R3D.circle_unselectAppIconToast );
		}
	}
	
	// private boolean topestWndIsMe() {
	// int Count = mworkspace3D.getChildCount();
	// for (int i = 0; i < Count; i++) {
	// childView = mworkspace3D.getChildAt(i);
	// if (childView.name == "PopIconGroupView"
	// || childView.name == "circlePopWnd3D") {
	// return false;
	// }
	// }
	// return true;
	//
	// }
	public void Process_delALL(
			int result )
	{
		// resetToNothingCircle();
		if( result == Workspace3D.CIRCLE_POP_ACK_ACTION ) /* Delete ALL */
		{
			mworkspace3D.setTag( mCircleSomething );
		}
		if( result == Workspace3D.CIRCLE_POP_CANCEL_ACTION ) /* Cancle ALL */
		{
			mCircleSomething.clear();
			SendMsgToAndroid.sendShowWorkspaceMsg();
		}
		// iLoongLauncher.getInstance().popResult = 0;
	}
	
	private void dealDelAll()
	{
		// circleSendMsg(CIRCLE_EVENT_CREATE_DIALOG, 0, 0, null);
		View3D findPopView = mworkspace3D.findView( "circlePopWnd3D" );
		if( findPopView != null )
		{
			mworkspace3D.removeView( findPopView );
		}
		bPopWndExist = false;
		resetToNothingCircle();
		SendMsgToAndroid.sendCreatePopDialogMsg();
	}
	
	private void dealOverLap()
	{
		resetToNothingCircle();
		// if (mCircleSomething.size()>1)
		// {
		// IconTrans3D iconTrans3D = new IconTrans3D("IconTrans3D");
		// addIconView(iconTrans3D);
		// iconTrans3D.buildElements(mCircleSomething);
		//
		// }
		// else {
		// /* 提示用户选择多个图标排列 */
		// SendMsgToAndroid.sendOurToastMsg(R3D.circle_selectMutiToOperToast);
		// SendMsgToAndroid.sendShowWorkspaceMsg();
		// }
		SendMsgToAndroid.sendOurToastMsg( R3D.circle_notSupportToast );
		mCircleSomething.clear();
	}
	
	private void reserveIcon3DOnly()
	{
		int Count = mCircleSomething.size() - 1;
		View3D myActor;
		for( int i = Count ; i >= 0 ; i-- )
		{
			myActor = mCircleSomething.get( i );
			if( myActor instanceof Icon3D )
			{
				CellLayout3D cellLayout = mworkspace3D.getCurrentCellLayout();
				if( cellLayout != null )
				{
					cellLayout.removeView( myActor );
				}
			}
			else
			{
				// childView.setTag(myActor);
				mCircleSomething.remove( myActor );
			}
		}
	}
	
	private void dealCreateFolder()
	{
		resetToNothingCircle();
		reserveIcon3DOnly();
		if( mCircleSomething.size() > 0 )
		{
			int FolderNum = ( mCircleSomething.size() + R3D.folder_max_num - 1 ) / R3D.folder_max_num;
			int offset = 0;
			int count = mCircleSomething.size();
			for( int i = 0 ; i < FolderNum ; i++ )
			{
				View3D temp = mCircleSomething.get( i * R3D.folder_max_num );
				Vector2 pos = new Vector2();
				pos.x = temp.x;
				pos.y = temp.y;
				if( pos.x < Utils3D.getScreenWidth() / 2 && pos.y > Utils3D.getScreenWidth() / 2 )
				{
					pos.x = temp.x + R3D.folder_front_width / 2;
					pos.y = temp.y - R3D.Workspace_cell_each_height;
				}
				else if( pos.x < Utils3D.getScreenWidth() / 2 && pos.y < Utils3D.getScreenWidth() / 2 )
				{
					pos.x = temp.x + R3D.folder_front_width / 2;
					pos.y = temp.y + R3D.Workspace_cell_each_height;
				}
				else if( pos.x > Utils3D.getScreenWidth() / 2 && pos.y < Utils3D.getScreenWidth() / 2 )
				{
					pos.x = temp.x - R3D.folder_front_width / 2;
					pos.y = temp.y + R3D.Workspace_cell_each_height;
				}
				else
				{
					pos.x = temp.x - R3D.folder_front_width / 2;
					pos.y = temp.y - R3D.Workspace_cell_each_height;
				}
				UserFolderInfo folderInfo = iLoongLauncher.getInstance().addFolder( (int)pos.x , (int)pos.y );
				FolderIcon3D folderIcon3D = new FolderIcon3D( "FolderIcon3DView" + i , folderInfo );
				addFolderView( folderIcon3D );
				offset = i * R3D.folder_max_num;
				for( int j = 0 ; j < R3D.folder_max_num ; j++ )
				{
					if( j + offset < count )
					{
						temp = mCircleSomething.get( j + offset );
						folderIcon3D.addFolderNode( (Icon3D)temp );
					}
					else
					{
						break;
					}
				}
			}
			// ViewCircled3D temp = mCircleSomething.get(0);
			// UserFolderInfo
			// folderInfo=iLoongLauncher.getInstance().addFolder((int)temp.x,(int)temp.y);
			// FolderIcon3D folderIcon3D =new FolderIcon3D("FolderIcon3DView",
			// folderInfo);
			// addFolderView(folderIcon3D);
			// folderIcon3D.buildFolderInfo(mCircleSomething);
		}
		else
		{
			SendMsgToAndroid.sendOurToastMsg( R3D.circle_unselectAppIconToast );
		}
		mCircleSomething.clear();
	}
	// public void circle_somethingReset() {
	// resetToNothingCircle();
	// mCircleSomething.clear();
	// }
}
