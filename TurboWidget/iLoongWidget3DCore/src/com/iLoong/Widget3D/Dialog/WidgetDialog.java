package com.iLoong.Widget3D.Dialog;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.iLoong.launcher.Widget3D.WidgetDownload;


public class WidgetDialog
{
	
	public static void downLoadDialog(
			Context context ,
			String Dialogtitle ,
			String Dialogcontent ,
			String Dialogbuttonok ,
			String Dialogbuttoncancel ,
			final String title ,
			final String apkName ,
			final String pkgName )
	{
		AlertDialog.Builder builder = new Builder( context );
		builder.setMessage( Dialogcontent );
		builder.setTitle( Dialogtitle );
		builder.setPositiveButton( Dialogbuttonok , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
				WidgetDownload.downloadWithoutCheckVersion( title , apkName , pkgName/*"com.cooee.widget.samweatherclock"*/, null );
			}
		} );
		builder.setNegativeButton( Dialogbuttoncancel , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
			}
		} );
		builder.create().show();
	}
}
