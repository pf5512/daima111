package com.iLoong.Calender.activity;

import com.cooeeui.cometcalendar.R;
import com.iLoong.Calender.view.WidgetCalender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class AdPreviewFullActivity extends Activity
{
	private ViewPager previewPager;
	public static AdPreviewFullActivity widgetcalender;
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		widgetcalender=this;
		setContentView( R.layout.adfullactivity );
		previewPager=(ViewPager)findViewById( R.id.adfullviewPager );
		
		final AdFullAdapter preAdapter = new AdFullAdapter( this );
		previewPager.setAdapter( preAdapter );
		
		Intent in=this.getIntent();
		int position=in.getIntExtra( "position" , 0 );
		previewPager.setCurrentItem( position+800 );
		
	}
	public static final AdPreviewFullActivity getIntance()
	{
		return widgetcalender;
	}
}
