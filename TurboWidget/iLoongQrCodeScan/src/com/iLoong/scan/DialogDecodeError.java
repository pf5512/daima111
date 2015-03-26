package com.iLoong.scan;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class DialogDecodeError extends Dialog
{
	
	Context context;
	private Button btn_back_choose;
	private Button btn_back_scan;
	private OnClickListener onClickListener;
	
	public DialogDecodeError(
			Context context )
	{
		super( context );
		this.context = context;
	}
	
	public DialogDecodeError(
			Context context ,
			int theme )
	{
		super( context , theme );
		this.context = context;
	}
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.dl_crop );
		findViewById();
		setListener();
	}
	
	private void findViewById()
	{
		btn_back_choose = (Button)findViewById( R.id.btn_back_choose );
		btn_back_scan = (Button)findViewById( R.id.btn_back_scan );
	}
	
	private void setListener()
	{
		btn_back_choose.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				onClickListener.OnClick();
				//				Intent pickIntent = new Intent( Intent.ACTION_PICK , null );
				//				pickIntent.setDataAndType( MediaStore.Images.Media.EXTERNAL_CONTENT_URI , "image/*" );
				//				context.startActivityForResult( pickIntent , 11 );
			}
		} );
		btn_back_scan.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				dismiss();
			}
		} );
	}
	
	public void setOnClickListener(
			OnClickListener onClickListener )
	{
		this.onClickListener = onClickListener;
	}
	
	public interface OnClickListener
	{
		
		public void OnClick();
	}
}
