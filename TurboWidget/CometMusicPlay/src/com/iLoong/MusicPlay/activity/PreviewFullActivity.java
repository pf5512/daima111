package com.iLoong.MusicPlay.activity;

import com.cooeeui.cometmusicplay.R;
import com.iLoong.MusicPlay.view.WidgetMusicPlayGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class PreviewFullActivity extends Activity
{
	private ViewPager previewPager;
	public static PreviewFullActivity widgetcalender;
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		widgetcalender=this;
		setContentView( R.layout.adfullactivity );
		previewPager=(ViewPager)findViewById( R.id.adfullviewPager );
		
		final FullAdapter preAdapter = new FullAdapter( this );
		previewPager.setAdapter( preAdapter );
		
		Intent in=this.getIntent();
		int position=in.getIntExtra( "position" , 0 );
		previewPager.setCurrentItem( position+400 );
		
	}
	public static final PreviewFullActivity getIntance()
	{
		return widgetcalender;
	}
}
