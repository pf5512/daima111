package com.iLoong.scan;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class DialogScanDetail extends Activity implements OnClickListener
{
	
	private LinearLayout ll_dl_detail;
	private ImageView iv_type_icon;
	private TextView tv_title;
	private TextView tv_content;
	private ImageButton btn_goon_scan;
	private ImageButton btn_open;
	private ImageButton btn_copy;
	private ImageButton btn_share;
	private String content;
	private int contentType;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.dl_scan );
		findViewById();
		setlistener();
		init();
	}
	
	private void init()
	{
		content = getIntent().getStringExtra( "content" );
		contentType = getIntent().getIntExtra( "type" , 2 );
		if( contentType == 1 )
		{
			iv_type_icon.setImageResource( R.drawable.browser_icon );
			tv_title.setText( R.string.string_interlinkage );
		}
		else
		{
			iv_type_icon.setImageResource( R.drawable.text_icon );
			tv_title.setText( R.string.string_text );
		}
		tv_content.setText( content );
	}
	
	private void findViewById()
	{
		ll_dl_detail = (LinearLayout)findViewById( R.id.ll_dl_detail );
		iv_type_icon = (ImageView)findViewById( R.id.iv_type_icon );
		tv_title = (TextView)findViewById( R.id.tv_title );
		tv_content = (TextView)findViewById( R.id.tv_content );
		btn_goon_scan = (ImageButton)findViewById( R.id.btn_goon_scan );
		btn_open = (ImageButton)findViewById( R.id.btn_open );
		btn_copy = (ImageButton)findViewById( R.id.btn_copy );
		btn_share = (ImageButton)findViewById( R.id.btn_share );
	}
	
	private void setlistener()
	{
		ll_dl_detail.setOnClickListener( this );
		btn_goon_scan.setOnClickListener( this );
		btn_open.setOnClickListener( this );
		btn_copy.setOnClickListener( this );
		btn_share.setOnClickListener( this );
	}
	
	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.ll_dl_detail:
				break;
			case R.id.btn_goon_scan:
				finish();
				break;
			case R.id.btn_open:
				if( contentType == 1 )
				{
					open( content );
				}
				else
				{
					AlertDialog.Builder builder = new Builder( DialogScanDetail.this );
					builder.setMessage( content );
					builder.setTitle( getResources().getString( R.string.string_text ) );
					builder.setNegativeButton( android.R.string.ok , null );
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				break;
			case R.id.btn_copy:
				copy( content );
				break;
			case R.id.btn_share:
				share();
				break;
		}
		//		finish();
	}
	
	private void share()
	{
		Intent shareIntent = new Intent( Intent.ACTION_SEND );
		shareIntent.setType( "text/plain" );
		shareIntent.putExtra( Intent.EXTRA_SUBJECT , getString( R.string.share_title ) );
		shareIntent.putExtra( Intent.EXTRA_TEXT , content );
		shareIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		startActivity( shareIntent );
	}
	
	//	@Override
	//	public boolean onTouchEvent(
	//			MotionEvent event )
	//	{
	//		finish();
	//		return true;
	//	}
	private void copy(
			String content )
	{
		ClipboardManager cmb = (ClipboardManager)getSystemService( Context.CLIPBOARD_SERVICE );
		cmb.setText( content );
		Toast.makeText( getApplicationContext() , "复制成功" , Toast.LENGTH_SHORT ).show();
	}
	
	private void open(
			String content )
	{
		Uri uri = Uri.parse( content );
		Intent intent = new Intent( Intent.ACTION_VIEW , uri );
		startActivity( intent );
	}
}
