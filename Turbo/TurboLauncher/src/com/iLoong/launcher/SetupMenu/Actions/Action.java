package com.iLoong.launcher.SetupMenu.Actions;


import java.util.HashSet;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.iLoong.launcher.SetupMenu.SetupMenu;


public abstract class Action
{
	
	protected int mActionID;
	protected String mAction;
	protected ComponentName mComponent;
	protected Bundle mBundle;
	protected static HashSet<String> mKey = new HashSet<String>();
	
	public Action()
	{
	}
	
	public Action(
			int actionid ,
			String action )
	{
		mActionID = actionid;
		mAction = action;
		mBundle = new Bundle();
	}
	
	public int getActionID()
	{
		return mActionID;
	}
	
	public void putIntentAction(
			Context pkg ,
			Class<?> cls )
	{
		mComponent = new ComponentName( pkg , cls );
	}
	
	public void putIntentAction(
			ComponentName cn )
	{
		mComponent = cn;
	}
	
	protected abstract void OnRunAction();
	
	protected abstract void OnActionFinish();
	
	protected abstract void OnPutValue(
			String key );
	
	public void PutValue(
			String key ,
			int value )
	{
		mBundle.putInt( key , value );
	}
	
	public void RunAction()
	{
		OnRunAction();
	}
	
	public void ActionFinish()
	{
		for( String key : mKey )
		{
			OnPutValue( key );
		}
		mKey.clear();
		OnActionFinish();
	}
	
	public void Clear()
	{
		mKey.clear();
		mBundle.clear();
	}
	
	public void AsynRunAction()
	{
		final Intent intent = new Intent( mAction );
		intent.setComponent( mComponent );
		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
		SetupMenuActions.getInstance().getContext().startActivity( intent );
		intent.putExtra( "itemType" , 10 );
		intent.putExtra( "title" , String.valueOf( mActionID ) );
		return;
	}
	
	public void SynRunAction()
	{
		final Intent intent = new Intent( mAction );
		intent.setComponent( mComponent );
		SetupMenuActions.getInstance().getContext().startActivity( intent );
		intent.putExtra( "itemType" , 10 );
		intent.putExtra( "title" , String.valueOf( mActionID ) );
		return;
	}
	
	public void SynRunActionForResult()
	{
		final Intent intent = new Intent( mAction );
		intent.setComponent( mComponent );
		( (Activity)SetupMenuActions.getInstance().getContext() ).startActivityForResult( intent , mActionID );
		return;
	}
	
	public Bundle getBundle()
	{
		return mBundle;
	}
	
	public Bitmap getBitmap()
	{
		return SetupMenu.getInstance().getMenuItem( mActionID ).iconbmp;
	}
	
	public static Bitmap getBitmap(
			int actionid )
	{
		return SetupMenu.getInstance().getMenuItem( actionid ).iconbmp;
	}
}
