package com.iLoong.launcher.SetupMenu.Actions;


import com.iLoong.launcher.Desktop3D.Log;
import com.umeng.fb.FeedbackAgent;


public class SuggestFeedback extends Action
{
	
	public SuggestFeedback(
			int actionid ,
			String action )
	{
		super( actionid , action );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_SUGGEST_FEEDBACK , new SuggestFeedback( ActionSetting.ACTION_SUGGEST_FEEDBACK , SuggestFeedback.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		//Jone
		FeedbackAgent agent = new FeedbackAgent( SetupMenuActions.getInstance().getContext() );
		agent.startFeedbackActivity();
	}
	
	@Override
	protected void OnActionFinish()
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void OnPutValue(
			String key )
	{
		// TODO Auto-generated method stub
	}
}
