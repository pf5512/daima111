package com.iLoong.launcher.setting;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.Actions.DesktopSettings.FirstActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DefaultClear extends Activity  implements View.OnClickListener
{
	
	private TextView yes;
	private TextView no;
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView(R.layout.dsclear_defautlsetting);
		yes=(TextView)this.findViewById( R.id.dsclear_yes );
		yes.setOnClickListener( this );
		no=(TextView)this.findViewById( R.id.dsclear_no );
		no.setOnClickListener( this );
//		
	}

	@Override
	public void onClick(
			View v )
	{
	
		switch(v.getId()){
			case R.id.dsclear_yes:
				getPackageManager().clearPackagePreferredActivities(RR.getPackageName());
				//startActivityForId(	FirstActivity.class );
				this.finish();
				break;
			case R.id.dsclear_no:
				//startActivityForId(	FirstActivity.class );
				this.finish();
				break;
			
		}
		
	}

	private void startActivityForId(
			Class<?> cls )
	{
		Intent in = new Intent( DefaultClear.this, cls );
		startActivity( in );
		if( Integer.valueOf( Build.VERSION.SDK ).intValue() >= 5 )
		{
			overridePendingTransition( R.anim.dsmove_in_right , R.anim.dsalphaout );
		}
	}
}
