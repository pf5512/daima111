// xiatian add whole file //Introduction
package com.iLoong.launcher.Desktop3D;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.iLoong.RR;


public class IntroductionCircleActivity extends Activity
{
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( RR.layout.introduction_circle );
		ImageButton btnReturn = (ImageButton)findViewById( RR.id.btnReturn );
		btnReturn.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View arg0 )
			{
				finish();
			}
		} );
	}
}
