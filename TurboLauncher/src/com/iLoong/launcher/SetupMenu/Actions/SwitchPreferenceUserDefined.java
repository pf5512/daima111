package com.iLoong.launcher.SetupMenu.Actions;


import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.Log;


public class SwitchPreferenceUserDefined extends SwitchPreference
{
	
	public SwitchPreferenceUserDefined(
			Context context )
	{
		super( context );
		// TODO Auto-generated constructor stub
		this.setWidgetLayoutResource( RR.layout.preference_widget_switch );
	}
	
	public SwitchPreferenceUserDefined(
			Context context ,
			AttributeSet attrs )
	{
		super( context , attrs );
		// TODO Auto-generated constructor stub
		this.setWidgetLayoutResource( RR.layout.preference_widget_switch );
	}
	
	public SwitchPreferenceUserDefined(
			Context context ,
			AttributeSet attrs ,
			int defStyle )
	{
		super( context , attrs , defStyle );
		// TODO Auto-generated constructor stub
		this.setWidgetLayoutResource( RR.layout.preference_widget_switch );
	}
	
	@Override
	protected void onBindView(
			View view )
	{
		super.onBindView( view );
		View checkableView = view.findViewById( RR.id.switchWidget );
		if( checkableView != null && checkableView instanceof Checkable )
		{
			( (Checkable)checkableView ).setChecked( isChecked() );
			if( checkableView instanceof Switch )
			{
				final Switch switchView = (Switch)checkableView;
				switchView.setOnCheckedChangeListener( new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(
							CompoundButton buttonView ,
							boolean isChecked )
					{
						// TODO Auto-generated method stub
						Log.v( "cooee" , "preference:" + SwitchPreferenceUserDefined.this.getTitle() + "  ----- isChecked : " + isChecked );
						DesktopAction.getInstance().mKey.add( SwitchPreferenceUserDefined.this.getKey() );
						if( !callChangeListener( isChecked ) )
						{
							// Listener didn't like it, change it back.
							// CompoundButton will make sure we don't recurse.
							buttonView.setChecked( !isChecked );
							return;
						}
						SwitchPreferenceUserDefined.this.setChecked( isChecked );
					}
				} );
			}
		}
	}
}
