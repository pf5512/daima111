package com.iLoong.launcher.SetupMenu.Actions.DesktopSettings;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.umeng.analytics.MobclickAgent;


public class DrawerActivity extends Activity implements View.OnClickListener
{
	
	LinearLayout drawerllback = null;
	LinearLayout drawer_infinitescroll = null;
	CheckBox drawer_infinitescroll_checkbox = null;
	boolean ifdrawerscroll = true;
	LinearLayout drawer_folderfirst = null;
	CheckBox drawer_folderfirst_checkbox = null;
	boolean ifforderfirst = false;
	LinearLayout drawer_transitioneffect = null;
	LinearLayout drawer_background = null;
	LinearLayout drawer_gridsize = null;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.dsdraweractivity );
		drawerllback = (LinearLayout)findViewById( R.id.drawer_backll );
		drawerllback.setOnClickListener( this );
		drawer_gridsize = (LinearLayout)findViewById( R.id.drawer_gridsize );
		drawer_gridsize.setOnClickListener( this );
		drawer_background = (LinearLayout)findViewById( R.id.drawer_background );
		//		drawer_background.setOnClickListener( this );
		drawer_background.setVisibility( View.GONE );
		drawer_infinitescroll = (LinearLayout)findViewById( R.id.drawer_infinitescroll );
		drawer_infinitescroll.setOnClickListener( this );
		drawer_infinitescroll_checkbox = (CheckBox)findViewById( R.id.drawer_infinitescroll_checkbox );
		SharedPreferences sps = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
		boolean ifchecked = sps.getBoolean( "drawer_infinitescroll_checkbox" , true );
		ifdrawerscroll = ifchecked;
		drawer_infinitescroll_checkbox.setChecked( ifchecked );
		drawer_infinitescroll_checkbox.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(
					CompoundButton buttonView ,
					boolean isChecked )
			{
				ifdrawerscroll = isChecked;
				SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
				Editor ed = sp.edit();
				ed.putBoolean( "drawer_infinitescroll_checkbox" , isChecked );
				ed.commit();
			}
		} );
		drawer_folderfirst = (LinearLayout)findViewById( R.id.drawer_folderfirst );
		drawer_folderfirst.setVisibility( View.GONE );
		drawer_folderfirst.setOnClickListener( this );
		drawer_folderfirst_checkbox = (CheckBox)findViewById( R.id.drawer_folderfirst_checkbox );
		boolean iffolderchecked = sps.getBoolean( "drawer_folderfirst_checkbox" , false );
		ifforderfirst = iffolderchecked;
		drawer_folderfirst_checkbox.setChecked( iffolderchecked );
		drawer_folderfirst_checkbox.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(
					CompoundButton buttonView ,
					boolean isChecked )
			{
				ifforderfirst = isChecked;
				SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
				Editor ed = sp.edit();
				ed.putBoolean( "drawer_folderfirst_checkbox" , isChecked );
				ed.commit();
			}
		} );
		drawer_transitioneffect = (LinearLayout)findViewById( R.id.drawer_transitioneffect );
		drawer_transitioneffect.setOnClickListener( this );
	}
	
	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.drawer_backll:
				finish();
				overridePendingTransition( R.anim.dsalphain , R.anim.dsmove_out_right );
				break;
			case R.id.drawer_infinitescroll:
				drawer_infinitescroll_checkbox.setChecked( !ifdrawerscroll );
				MobclickAgent.onEvent( this , "DSettingDrawerInfiniteScroll" );
				break;
			case R.id.drawer_folderfirst:
				drawer_folderfirst_checkbox.setChecked( !ifforderfirst );
				break;
			case R.id.drawer_transitioneffect:
				MobclickAgent.onEvent( this , "DSettingToDrawerEffect" );
				if( iLoongLauncher.entryWorksapceCount == iLoongLauncher.DOCKBAR_DELAY_COUNT )
				{
					iLoongLauncher.entryWorksapceCount--;
				}
				Intent intent = new Intent( this , iLoongLauncher.class );
				startActivity( intent );
				iLoongLauncher.getInstance().getD3dListener().getRoot().showAllAppFromWorkspace();
				//				SendMsgToAndroid.sendShowAppEffectDialogMsg();
				final int INDEX_WORKSPACE = 0;
				final int INDEX_APP = 1;
				int position = 0;
				final String ACTION_EFFECT_PREVIEW = "com.cool.action.EffectPreview";
				final String ACTION_EFFECT_PREVIEW_EXTRA_TYPE = "EffectPreviewExtraType";
				final String ACTION_EFFECT_PREVIEW_EXTRA_INDEX = "EffectPreviewExtraIndex";
				Intent it = new Intent( ACTION_EFFECT_PREVIEW );
				it.putExtra( ACTION_EFFECT_PREVIEW_EXTRA_TYPE , INDEX_APP );
				String strposition = "";
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( DrawerActivity.this );
				strposition = sharedPreferences.getString( "appeffects" , "" );
				if( !strposition.equals( "" ) )
				{
					position = Integer.parseInt( strposition );
				}
				else
				{
					position = Desktop3DListener.root.getAppHost().appList.getEffectType();
				}
				it.putExtra( ACTION_EFFECT_PREVIEW_EXTRA_INDEX , position );
				if( ( iLoongLauncher.getInstance() != null ) && ( iLoongLauncher.getInstance().getD3dListener() != null ) && ( iLoongLauncher.getInstance().getD3dListener().getRoot() != null ) )
					iLoongLauncher.getInstance().getD3dListener().getRoot().dealEffectPreview( it );
				break;
			case R.id.drawer_background:
				Intent in = new Intent( this , DrawerBackgroundActiivity.class );
				startActivity( in );
				if( Integer.valueOf( Build.VERSION.SDK ).intValue() >= 5 )
				{
					overridePendingTransition( R.anim.dsmove_in_right , R.anim.dsalphaout );
				}
				break;
			case R.id.drawer_gridsize:
				//MobclickAgent.onEvent( this , "DSettingToIconLineSetting" );
				Intent inte = new Intent( this , DrawerIconLineActivity.class );
				startActivity( inte );
				if( Integer.valueOf( Build.VERSION.SDK ).intValue() >= 5 )
				{
					overridePendingTransition( R.anim.dsmove_in_right , R.anim.dsalphaout );
				}
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
	
	@Override
	protected void onPause()
	{
		Intent intent = new Intent( "com.cooee.desktopsettings.drawer.drawerchange" );
		intent.putExtra( "drawerscrollstate" , ifdrawerscroll );
		sendBroadcast( intent );
		super.onPause();
	}
}
