package com.iLoong.launcher.newspage;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.cooeeui.brand.turbolauncher.R;


public class NewspageVirtual extends LinearLayout
{
	
	public static final String SP_KEY_NEWSPAGE_DOWNLOAD_ID = "newspage_download_id";
	
	public NewspageVirtual(
			final Context context )
	{
		super( context );
		LayoutInflater mInflater = LayoutInflater.from( context );
		View view = mInflater.inflate( R.layout.newspage_virtual , null );
		addView( view );
		view.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
				long downId = sp.getLong( SP_KEY_NEWSPAGE_DOWNLOAD_ID , -1 );
				DownloadApkManager manager = new DownloadApkManager( context );
				if( !manager.queryDownloadStatus( downId ) )
				{
					Intent intent = new Intent( context , DownloadDialog.class );
					intent.putExtra( DownloadDialog.KEY_EXTRA_ICON , R.drawable.news_icon );
					intent.putExtra( DownloadDialog.KEY_EXTRA_TITLE , R.string.newspage_download_dialog_title_text );
					intent.putExtra( DownloadDialog.KEY_EXTRA_MESSAGE , R.string.newspage_download_dialog_message_text );
					intent.putExtra( DownloadDialog.KEY_EXTRA_URL , "http://www.coolauncher.cn/download/apk/news.apk" );
					intent.putExtra( DownloadDialog.KEY_EXTRA_FILE_NAME , "news.apk" );
					intent.putExtra( DownloadDialog.KEY_EXTRA_DOWN_ID , downId );
					intent.putExtra( DownloadDialog.KEY_EXTRA_DOWN_ID_SP_KEY , SP_KEY_NEWSPAGE_DOWNLOAD_ID );
					context.startActivity( intent );
				}
			}
		} );
	}
}
