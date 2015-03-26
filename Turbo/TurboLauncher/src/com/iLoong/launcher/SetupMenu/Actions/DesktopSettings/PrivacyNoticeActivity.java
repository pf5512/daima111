package com.iLoong.launcher.SetupMenu.Actions.DesktopSettings;

import com.cooeeui.brand.turbolauncher.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.ConversationActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

public class PrivacyNoticeActivity extends Activity implements View.OnClickListener{
	LinearLayout llBack = null;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )	{
		super.onCreate( savedInstanceState );	
		setContentView( R.layout.dsaboutprivacy );
		llBack = (LinearLayout)findViewById( R.id.privacy_notice_backll);
		llBack.setOnClickListener( this );
		
	}
	
	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.privacy_notice_backll:
				finish();
				overridePendingTransition( R.anim.dsalphain , R.anim.dsmove_out_right );
				break;
			default:
				break;
		}
	}
	
	
	@Override
	public boolean onKeyDown(
			int keyCode ,
			KeyEvent event )
	{
		if( keyCode == KeyEvent.KEYCODE_BACK )
		{
			finish();
			overridePendingTransition( R.anim.dsalphain , R.anim.dsmove_out_right );
		}
		return super.onKeyDown( keyCode , event );
	}

}
