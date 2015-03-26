package com.iLoong.launcher.setting;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cooeeui.brand.turbolauncher.R;


public class FakeLauncher extends Activity implements View.OnClickListener
{
	
	private TextView yes;
	private TextView no;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.dsclear_defautlsetting );
		yes = (TextView)this.findViewById( R.id.dsclear_yes );
		yes.setText( R.string.ds_default_ask_yes );
		yes.setOnClickListener( this );
		no = (TextView)this.findViewById( R.id.dsclear_no );
		no.setOnClickListener( this );
		no.setText( R.string.ds_default_ask_no );
		( (TextView)this.findViewById( R.id.dsclear_title ) ).setText( R.string.ds_default_ask_title );
		( (TextView)this.findViewById( R.id.dsclear_content ) ).setText( R.string.ds_default_ask_content );
		if( VERSION.SDK_INT >= 11 )
			setFinishOnTouchOutside( false );
	}
	
	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.dsclear_yes:
				startActivityForId( DefaultSetting.class );
				this.finish();
				break;
			case R.id.dsclear_no:
				setResult( Activity.RESULT_OK );
				this.finish();
				break;
		}
	}
	
	private void startActivityForId(
			Class<?> cls )
	{
		Intent in = new Intent( FakeLauncher.this , cls );
		startActivity( in );
		if( Integer.valueOf( Build.VERSION.SDK ).intValue() >= 5 )
		{
			overridePendingTransition( R.anim.dsmove_in_right , R.anim.dsalphaout );
		}
	}
}
