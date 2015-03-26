package com.iLoong.launcher.SetupMenu.Actions.DesktopSettings;

import com.badlogic.gdx.Gdx;
import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.Desktop3D.AppHost3D;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


public class DrawerBackgroundActiivity extends Activity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener , CompoundButton.OnCheckedChangeListener
{
	LinearLayout drawbg_back=null;
	
	LinearLayout drawer_bgdefault=null;
	RadioButton drawer_bgradiodefault=null;
	
	LinearLayout dsdrawer_bgcustom=null;
	RadioButton dsdrawer_bgradiocustom=null;
	
	
	LinearLayout drawer_bgshowtxt=null;
	RelativeLayout drawer_txt=null;
	RelativeLayout drawer_bgrlsb=null;
	
	SeekBar drawer_seekbarbg=null;
	TextView drawer_bgopacitytxt=null;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.dsdrawerbackground );
		drawbg_back=(LinearLayout)findViewById( R.id.drawerbg_backll );
		drawbg_back.setOnClickListener( this );
		
		drawer_bgdefault=(LinearLayout)findViewById( R.id.drawer_bgdefault );
		drawer_bgdefault.setOnClickListener( this );
		drawer_bgradiodefault=(RadioButton)findViewById( R.id.drawer_bgradiodefault );
		drawer_bgradiodefault.setOnClickListener( this );
		
		dsdrawer_bgcustom=(LinearLayout)findViewById( R.id.dsdrawer_bgcustom );
		dsdrawer_bgcustom.setOnClickListener( this );
		dsdrawer_bgradiocustom=(RadioButton)findViewById( R.id.dsdrawer_bgradiocustom );
		dsdrawer_bgradiocustom.setOnClickListener( this );
		
		drawer_bgshowtxt=(LinearLayout)findViewById( R.id.drawer_bgshowtxt );
		drawer_txt=(RelativeLayout)findViewById( R.id.drawer_txt );
		drawer_bgrlsb=(RelativeLayout)findViewById( R.id.drawer_bgrlsb );
		
		drawer_seekbarbg=(SeekBar)findViewById( R.id.drawer_seekbarbg );
		drawer_seekbarbg.setOnSeekBarChangeListener( this );
		drawer_bgopacitytxt=(TextView)findViewById( R.id.drawer_bgopacitytxt );
		
		
		
		drawer_bgradiodefault.setChecked( true );
		drawer_seekbarbg.setProgress( AppHost3D.mainmenuBgAlpha );
		drawer_bgopacitytxt.setText( AppHost3D.mainmenuBgAlpha+"%" );
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

	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.drawerbg_backll:
				finish();
				overridePendingTransition(R.anim.dsalphain,
						R.anim.dsmove_out_right);
				break;
			case R.id.drawer_bgdefault:
				drawer_bgradiodefault.setChecked( true );
				dsdrawer_bgradiocustom.setChecked( false );
				drawer_bgshowtxt.setVisibility( View.VISIBLE );
				drawer_txt.setVisibility( View.VISIBLE );
				drawer_bgrlsb.setVisibility( View.VISIBLE );
				break;
			case R.id.drawer_bgradiodefault:
				drawer_bgradiodefault.setChecked( true );
				dsdrawer_bgradiocustom.setChecked( false );
				drawer_bgshowtxt.setVisibility( View.VISIBLE );
				drawer_txt.setVisibility( View.VISIBLE );
				drawer_bgrlsb.setVisibility( View.VISIBLE );
				break;
			case R.id.dsdrawer_bgcustom:
				drawer_bgradiodefault.setChecked( false );
				dsdrawer_bgradiocustom.setChecked( true );
				drawer_bgshowtxt.setVisibility( View.GONE);
				drawer_txt.setVisibility( View.GONE );
				drawer_bgrlsb.setVisibility( View.GONE );
				break;
			case R.id.dsdrawer_bgradiocustom:
				drawer_bgradiodefault.setChecked( false );
				dsdrawer_bgradiocustom.setChecked( true );
				drawer_bgshowtxt.setVisibility( View.GONE);
				drawer_txt.setVisibility( View.GONE );
				drawer_bgrlsb.setVisibility( View.GONE );
				break;
			default:
				break;
		}
	}

	@Override
	public void onCheckedChanged(
			CompoundButton buttonView ,
			boolean isChecked )
	{
		
	}

	@Override
	public void onProgressChanged(
			SeekBar seekBar ,
			int progress ,
			boolean fromUser )
	{
		drawer_bgopacitytxt.setText( progress+"%" );
		AppHost3D.mainmenuBgAlpha = progress;
		Gdx.graphics.requestRendering();
	}

	@Override
	public void onStartTrackingTouch(
			SeekBar seekBar )
	{
		
	}

	@Override
	public void onStopTrackingTouch(
			SeekBar seekBar )
	{
		
	}
}
