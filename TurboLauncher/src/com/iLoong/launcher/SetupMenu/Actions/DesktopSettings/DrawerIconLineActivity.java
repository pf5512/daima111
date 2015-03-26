package com.iLoong.launcher.SetupMenu.Actions.DesktopSettings;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.umeng.analytics.MobclickAgent;


public class DrawerIconLineActivity extends Activity implements View.OnClickListener , SeekBar.OnSeekBarChangeListener , CompoundButton.OnCheckedChangeListener
{
	
	LinearLayout drawergl_back = null;
	LinearLayout dsdrawer_adjust = null;
	RadioButton dsdrawer_radioadjust = null;
	LinearLayout dsdrawer_thin = null;
	RadioButton dsdrawer_radiothin = null;
	LinearLayout dsdrawer_modest = null;
	RadioButton dadrawer_radiomodest = null;
	LinearLayout dsdrawer_dense = null;
	RadioButton dsdrawer_radiodense = null;
	LinearLayout dsdrawer_custom = null;
	RadioButton dadrawer_radiocustom = null;
	LinearLayout dsdrawer_hangshutxt = null;
	RelativeLayout dsdrawer_hang = null;
	RelativeLayout dsdrawer_hangshuzitxt = null;
	LinearLayout dsdrawer_lieshutxt = null;
	RelativeLayout dsdrawer_lie = null;
	RelativeLayout dsdrawer_lieshuzitxt = null;
	TextView drawer_linenumber = null;
	TextView drawer_columnnumber = null;
	SeekBar drawer_seekbarhang = null;
	SeekBar drawer_seekbarlie = null;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.dsdrawergridline );
		drawergl_back = (LinearLayout)findViewById( R.id.drawergl_backll );
		drawergl_back.setOnClickListener( this );
		dsdrawer_adjust = (LinearLayout)findViewById( R.id.dsdrawer_adjust );
		dsdrawer_adjust.setOnClickListener( this );
		dsdrawer_radioadjust = (RadioButton)findViewById( R.id.dsdrawer_radioadjust );
		dsdrawer_radioadjust.setOnClickListener( this );
		dsdrawer_radioadjust.setOnCheckedChangeListener( this );
		dsdrawer_thin = (LinearLayout)findViewById( R.id.dsdrawer_thin );
		dsdrawer_thin.setOnClickListener( this );
		dsdrawer_radiothin = (RadioButton)findViewById( R.id.dsdrawer_radiothin );
		dsdrawer_radiothin.setOnClickListener( this );
		dsdrawer_radiothin.setOnCheckedChangeListener( this );
		dsdrawer_modest = (LinearLayout)findViewById( R.id.dsdrawer_modest );
		dsdrawer_modest.setOnClickListener( this );
		dadrawer_radiomodest = (RadioButton)findViewById( R.id.dadrawer_radiomodest );
		dadrawer_radiomodest.setOnClickListener( this );
		dadrawer_radiomodest.setOnCheckedChangeListener( this );
		dsdrawer_dense = (LinearLayout)findViewById( R.id.dsdrawer_dense );
		dsdrawer_dense.setOnClickListener( this );
		dsdrawer_radiodense = (RadioButton)findViewById( R.id.dsdrawer_radiodense );
		dsdrawer_radiodense.setOnClickListener( this );
		dsdrawer_radiodense.setOnCheckedChangeListener( this );
		dsdrawer_custom = (LinearLayout)findViewById( R.id.dsdrawer_custom );
		dsdrawer_custom.setOnClickListener( this );
		dadrawer_radiocustom = (RadioButton)findViewById( R.id.dadrawer_radiocustom );
		dadrawer_radiocustom.setOnClickListener( this );
		dadrawer_radiocustom.setOnCheckedChangeListener( this );
		dsdrawer_hangshutxt = (LinearLayout)findViewById( R.id.dsdrawer_hangshutxt );
		dsdrawer_hang = (RelativeLayout)findViewById( R.id.dsdrawer_hang );
		dsdrawer_hangshuzitxt = (RelativeLayout)findViewById( R.id.dsdrawer_hangshuzitxt );
		dsdrawer_lieshutxt = (LinearLayout)findViewById( R.id.dsdrawer_lieshutxt );
		dsdrawer_lie = (RelativeLayout)findViewById( R.id.dsdrawer_lie );
		dsdrawer_lieshuzitxt = (RelativeLayout)findViewById( R.id.dsdrawer_lieshuzitxt );
		drawer_linenumber = (TextView)findViewById( R.id.drawer_linenumber );
		drawer_columnnumber = (TextView)findViewById( R.id.drawer_columnnumber );
		drawer_seekbarhang = (SeekBar)findViewById( R.id.drawer_seekbarhang );
		drawer_seekbarhang.setOnSeekBarChangeListener( this );
		drawer_seekbarlie = (SeekBar)findViewById( R.id.drawer_seekbarlie );
		drawer_seekbarlie.setOnSeekBarChangeListener( this );
		SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
		int nowstate = sp.getInt( "nowstate" , -1 );
		int nowhang = sp.getInt( "nowhang" , 3 );
		int nowlie = sp.getInt( "nowlie" , 3 );
		switch( nowstate )
		{
			case R.id.dsdrawer_radioadjust:
				dsdrawer_radioadjust.setChecked( true );
				dsdrawer_radiothin.setChecked( false );
				dadrawer_radiomodest.setChecked( false );
				dsdrawer_radiodense.setChecked( false );
				dadrawer_radiocustom.setChecked( false );
				dsdrawer_hangshutxt.setVisibility( View.GONE );
				dsdrawer_hang.setVisibility( View.GONE );
				dsdrawer_hangshuzitxt.setVisibility( View.GONE );
				dsdrawer_lieshutxt.setVisibility( View.GONE );
				dsdrawer_lie.setVisibility( View.GONE );
				dsdrawer_lieshuzitxt.setVisibility( View.GONE );
				break;
			case R.id.dsdrawer_radiothin:
				dsdrawer_radioadjust.setChecked( false );
				dsdrawer_radiothin.setChecked( true );
				dadrawer_radiomodest.setChecked( false );
				dsdrawer_radiodense.setChecked( false );
				dadrawer_radiocustom.setChecked( false );
				dsdrawer_hangshutxt.setVisibility( View.GONE );
				dsdrawer_hang.setVisibility( View.GONE );
				dsdrawer_hangshuzitxt.setVisibility( View.GONE );
				dsdrawer_lieshutxt.setVisibility( View.GONE );
				dsdrawer_lie.setVisibility( View.GONE );
				dsdrawer_lieshuzitxt.setVisibility( View.GONE );
				break;
			case R.id.dadrawer_radiomodest:
				dsdrawer_radioadjust.setChecked( false );
				dsdrawer_radiothin.setChecked( false );
				dadrawer_radiomodest.setChecked( true );
				dsdrawer_radiodense.setChecked( false );
				dadrawer_radiocustom.setChecked( false );
				dsdrawer_hangshutxt.setVisibility( View.GONE );
				dsdrawer_hang.setVisibility( View.GONE );
				dsdrawer_hangshuzitxt.setVisibility( View.GONE );
				dsdrawer_lieshutxt.setVisibility( View.GONE );
				dsdrawer_lie.setVisibility( View.GONE );
				dsdrawer_lieshuzitxt.setVisibility( View.GONE );
				break;
			case R.id.dsdrawer_radiodense:
				dsdrawer_radioadjust.setChecked( false );
				dsdrawer_radiothin.setChecked( false );
				dadrawer_radiomodest.setChecked( false );
				dsdrawer_radiodense.setChecked( true );
				dadrawer_radiocustom.setChecked( false );
				dsdrawer_hangshutxt.setVisibility( View.GONE );
				dsdrawer_hang.setVisibility( View.GONE );
				dsdrawer_hangshuzitxt.setVisibility( View.GONE );
				dsdrawer_lieshutxt.setVisibility( View.GONE );
				dsdrawer_lie.setVisibility( View.GONE );
				dsdrawer_lieshuzitxt.setVisibility( View.GONE );
				break;
			case R.id.dadrawer_radiocustom:
				dsdrawer_radioadjust.setChecked( false );
				dsdrawer_radiothin.setChecked( false );
				dadrawer_radiomodest.setChecked( false );
				dsdrawer_radiodense.setChecked( false );
				dadrawer_radiocustom.setChecked( true );
				dsdrawer_hangshutxt.setVisibility( View.VISIBLE );
				dsdrawer_hang.setVisibility( View.VISIBLE );
				dsdrawer_hangshuzitxt.setVisibility( View.VISIBLE );
				dsdrawer_lieshutxt.setVisibility( View.VISIBLE );
				dsdrawer_lie.setVisibility( View.VISIBLE );
				dsdrawer_lieshuzitxt.setVisibility( View.VISIBLE );
				drawer_seekbarhang.setProgress( (int)( ( nowlie - 3 ) * 100 / 3f ) );
				drawer_linenumber.setText( nowlie + "" );
				drawer_seekbarlie.setProgress( (int)( ( nowhang - 3 ) * 100 / 3f ) );
				drawer_columnnumber.setText( nowhang + "" );
				break;
			default:
				dsdrawer_radioadjust.setChecked( true );
				dsdrawer_radiothin.setChecked( false );
				dadrawer_radiomodest.setChecked( false );
				dsdrawer_radiodense.setChecked( false );
				dadrawer_radiocustom.setChecked( false );
				dsdrawer_hangshutxt.setVisibility( View.GONE );
				dsdrawer_hang.setVisibility( View.GONE );
				dsdrawer_hangshuzitxt.setVisibility( View.GONE );
				dsdrawer_lieshutxt.setVisibility( View.GONE );
				dsdrawer_lie.setVisibility( View.GONE );
				dsdrawer_lieshuzitxt.setVisibility( View.GONE );
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
	
	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.drawergl_backll:
				finish();
				overridePendingTransition( R.anim.dsalphain , R.anim.dsmove_out_right );
				break;
			case R.id.dsdrawer_radioadjust:
				dsdrawer_radioadjust.setChecked( true );
				dsdrawer_radiothin.setChecked( false );
				dadrawer_radiomodest.setChecked( false );
				dsdrawer_radiodense.setChecked( false );
				dadrawer_radiocustom.setChecked( false );
				dsdrawer_hangshutxt.setVisibility( View.GONE );
				dsdrawer_hang.setVisibility( View.GONE );
				dsdrawer_hangshuzitxt.setVisibility( View.GONE );
				dsdrawer_lieshutxt.setVisibility( View.GONE );
				dsdrawer_lie.setVisibility( View.GONE );
				dsdrawer_lieshuzitxt.setVisibility( View.GONE );
				break;
			case R.id.dsdrawer_adjust:
				dsdrawer_radioadjust.setChecked( true );
				dsdrawer_radiothin.setChecked( false );
				dadrawer_radiomodest.setChecked( false );
				dsdrawer_radiodense.setChecked( false );
				dadrawer_radiocustom.setChecked( false );
				dsdrawer_hangshutxt.setVisibility( View.GONE );
				dsdrawer_hang.setVisibility( View.GONE );
				dsdrawer_hangshuzitxt.setVisibility( View.GONE );
				dsdrawer_lieshutxt.setVisibility( View.GONE );
				dsdrawer_lie.setVisibility( View.GONE );
				dsdrawer_lieshuzitxt.setVisibility( View.GONE );
				MobclickAgent.onEvent( this , "DSettingIconLineAuto" );
				break;
			case R.id.dsdrawer_radiothin:
				dsdrawer_radioadjust.setChecked( false );
				dsdrawer_radiothin.setChecked( true );
				dadrawer_radiomodest.setChecked( false );
				dsdrawer_radiodense.setChecked( false );
				dadrawer_radiocustom.setChecked( false );
				dsdrawer_hangshutxt.setVisibility( View.GONE );
				dsdrawer_hang.setVisibility( View.GONE );
				dsdrawer_hangshuzitxt.setVisibility( View.GONE );
				dsdrawer_lieshutxt.setVisibility( View.GONE );
				dsdrawer_lie.setVisibility( View.GONE );
				dsdrawer_lieshuzitxt.setVisibility( View.GONE );
				break;
			case R.id.dsdrawer_thin:
				dsdrawer_radioadjust.setChecked( false );
				dsdrawer_radiothin.setChecked( true );
				dadrawer_radiomodest.setChecked( false );
				dsdrawer_radiodense.setChecked( false );
				dadrawer_radiocustom.setChecked( false );
				dsdrawer_hangshutxt.setVisibility( View.GONE );
				dsdrawer_hang.setVisibility( View.GONE );
				dsdrawer_hangshuzitxt.setVisibility( View.GONE );
				dsdrawer_lieshutxt.setVisibility( View.GONE );
				dsdrawer_lie.setVisibility( View.GONE );
				dsdrawer_lieshuzitxt.setVisibility( View.GONE );
				MobclickAgent.onEvent( this , "DSettingIconLine4X4" );
				break;
			case R.id.dadrawer_radiomodest:
				dsdrawer_radioadjust.setChecked( false );
				dsdrawer_radiothin.setChecked( false );
				dadrawer_radiomodest.setChecked( true );
				dsdrawer_radiodense.setChecked( false );
				dadrawer_radiocustom.setChecked( false );
				dsdrawer_hangshutxt.setVisibility( View.GONE );
				dsdrawer_hang.setVisibility( View.GONE );
				dsdrawer_hangshuzitxt.setVisibility( View.GONE );
				dsdrawer_lieshutxt.setVisibility( View.GONE );
				dsdrawer_lie.setVisibility( View.GONE );
				dsdrawer_lieshuzitxt.setVisibility( View.GONE );
				break;
			case R.id.dsdrawer_modest:
				dsdrawer_radioadjust.setChecked( false );
				dsdrawer_radiothin.setChecked( false );
				dadrawer_radiomodest.setChecked( true );
				dsdrawer_radiodense.setChecked( false );
				dadrawer_radiocustom.setChecked( false );
				dsdrawer_hangshutxt.setVisibility( View.GONE );
				dsdrawer_hang.setVisibility( View.GONE );
				dsdrawer_hangshuzitxt.setVisibility( View.GONE );
				dsdrawer_lieshutxt.setVisibility( View.GONE );
				dsdrawer_lie.setVisibility( View.GONE );
				dsdrawer_lieshuzitxt.setVisibility( View.GONE );
				MobclickAgent.onEvent( this , "DSettingIconLine4x5" );
				break;
			case R.id.dsdrawer_radiodense:
				dsdrawer_radioadjust.setChecked( false );
				dsdrawer_radiothin.setChecked( false );
				dadrawer_radiomodest.setChecked( false );
				dsdrawer_radiodense.setChecked( true );
				dadrawer_radiocustom.setChecked( false );
				dsdrawer_hangshutxt.setVisibility( View.GONE );
				dsdrawer_hang.setVisibility( View.GONE );
				dsdrawer_hangshuzitxt.setVisibility( View.GONE );
				dsdrawer_lieshutxt.setVisibility( View.GONE );
				dsdrawer_lie.setVisibility( View.GONE );
				dsdrawer_lieshuzitxt.setVisibility( View.GONE );
				break;
			case R.id.dsdrawer_dense:
				dsdrawer_radioadjust.setChecked( false );
				dsdrawer_radiothin.setChecked( false );
				dadrawer_radiomodest.setChecked( false );
				dsdrawer_radiodense.setChecked( true );
				dadrawer_radiocustom.setChecked( false );
				dsdrawer_hangshutxt.setVisibility( View.GONE );
				dsdrawer_hang.setVisibility( View.GONE );
				dsdrawer_hangshuzitxt.setVisibility( View.GONE );
				dsdrawer_lieshutxt.setVisibility( View.GONE );
				dsdrawer_lie.setVisibility( View.GONE );
				dsdrawer_lieshuzitxt.setVisibility( View.GONE );
				MobclickAgent.onEvent( this , "DSettingIconLine5x5" );
				break;
			case R.id.dadrawer_radiocustom:
				dsdrawer_radioadjust.setChecked( false );
				dsdrawer_radiothin.setChecked( false );
				dadrawer_radiomodest.setChecked( false );
				dsdrawer_radiodense.setChecked( false );
				dadrawer_radiocustom.setChecked( true );
				dsdrawer_hangshutxt.setVisibility( View.VISIBLE );
				dsdrawer_hang.setVisibility( View.VISIBLE );
				dsdrawer_hangshuzitxt.setVisibility( View.VISIBLE );
				dsdrawer_lieshutxt.setVisibility( View.VISIBLE );
				dsdrawer_lie.setVisibility( View.VISIBLE );
				dsdrawer_lieshuzitxt.setVisibility( View.VISIBLE );
				break;
			case R.id.dsdrawer_custom:
				dsdrawer_radioadjust.setChecked( false );
				dsdrawer_radiothin.setChecked( false );
				dadrawer_radiomodest.setChecked( false );
				dsdrawer_radiodense.setChecked( false );
				dadrawer_radiocustom.setChecked( true );
				dsdrawer_hangshutxt.setVisibility( View.VISIBLE );
				dsdrawer_hang.setVisibility( View.VISIBLE );
				dsdrawer_hangshuzitxt.setVisibility( View.VISIBLE );
				dsdrawer_lieshutxt.setVisibility( View.VISIBLE );
				dsdrawer_lie.setVisibility( View.VISIBLE );
				dsdrawer_lieshuzitxt.setVisibility( View.VISIBLE );
				MobclickAgent.onEvent( this , "DSettingIconLineCustom" );
				break;
			default:
				break;
		}
	}
	
	@Override
	protected void onPause()
	{
		int mCellCountX;
		int mCellCountY;
		if( Utils3D.getScreenDisplayMetricsHeight() >= 800 )
		{
			mCellCountX = 4;
			mCellCountY = 5;
		}
		else
		{
			mCellCountX = 4;
			mCellCountY = 4;
		}
		SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
		int nowstate = sp.getInt( "nowstate" , -1 );
		int nowhang = sp.getInt( "nowhang" , 3 );
		int nowlie = sp.getInt( "nowlie" , 3 );
		switch( nowstate )
		{
			case R.id.dsdrawer_radioadjust:
				break;
			case R.id.dsdrawer_radiothin:
				mCellCountX = 4;
				mCellCountY = 4;
				break;
			case R.id.dadrawer_radiomodest:
				mCellCountX = 5;
				mCellCountY = 4;
				break;
			case R.id.dsdrawer_radiodense:
				mCellCountX = 5;
				mCellCountY = 5;
				break;
			case R.id.dadrawer_radiocustom:
				mCellCountX = nowhang;
				mCellCountY = nowlie;
				break;
			default:
				break;
		}
		Intent intent = new Intent( "com.cooee.desktopsettings.drawer.gridlinechange" );
		intent.putExtra( "gridlinehang" , mCellCountX );
		intent.putExtra( "gridlinelie" , mCellCountY );
		sendBroadcast( intent );
		super.onPause();
	}
	
	@Override
	public void onProgressChanged(
			SeekBar seekBar ,
			int progress ,
			boolean fromUser )
	{
		switch( seekBar.getId() )
		{
			case R.id.drawer_seekbarhang:
				drawer_linenumber.setText( ( (int)( 3f / 100f * progress ) + 3 ) + "" );
				break;
			case R.id.drawer_seekbarlie:
				drawer_columnnumber.setText( ( (int)( 3f / 100f * progress ) + 3 ) + "" );
				break;
			default:
				break;
		}
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
		SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
		Editor ed = sp.edit();
		switch( seekBar.getId() )
		{
			case R.id.drawer_seekbarhang:
				ed.putInt( "nowlie" , ( (int)( 3f / 100f * seekBar.getProgress() ) + 3 ) );
				ed.commit();
				break;
			case R.id.drawer_seekbarlie:
				ed.putInt( "nowhang" , ( (int)( 3f / 100f * seekBar.getProgress() ) + 3 ) );
				ed.commit();
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
		SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
		Editor ed = sp.edit();
		switch( buttonView.getId() )
		{
			case R.id.dsdrawer_radioadjust:
				if( isChecked )
				{
					ed.putInt( "nowstate" , R.id.dsdrawer_radioadjust );
					ed.commit();
				}
				break;
			case R.id.dsdrawer_radiothin:
				if( isChecked )
				{
					ed.putInt( "nowstate" , R.id.dsdrawer_radiothin );
					ed.commit();
				}
				break;
			case R.id.dadrawer_radiomodest:
				if( isChecked )
				{
					ed.putInt( "nowstate" , R.id.dadrawer_radiomodest );
					ed.commit();
				}
				break;
			case R.id.dsdrawer_radiodense:
				if( isChecked )
				{
					ed.putInt( "nowstate" , R.id.dsdrawer_radiodense );
					ed.commit();
				}
				break;
			case R.id.dadrawer_radiocustom:
				if( isChecked )
				{
					ed.putInt( "nowstate" , R.id.dadrawer_radiocustom );
					ed.commit();
				}
				break;
			default:
				break;
		}
	}
}
