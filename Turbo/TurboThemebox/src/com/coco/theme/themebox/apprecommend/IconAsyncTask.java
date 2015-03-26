package com.coco.theme.themebox.apprecommend;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.database.CursorJoiner.Result;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.coco.theme.themebox.StaticClass;


public class IconAsyncTask extends AsyncTask<String , Integer , Result>
{
	
	private Context mContext;
	
	public IconAsyncTask(
			Context context )
	{
		mContext = context;
	}
	
	@Override
	protected Result doInBackground(
			String ... params )
	{
		File f = new File( params[1] );
		if( !f.exists() )
		{
			f.mkdir();
		}
		try
		{
			Bitmap bitmap = null;
			bitmap = BitmapFactory.decodeFile( params[1] + "/" + params[2] );
			if( params[0] != null && bitmap == null )
			{
				URL url = new URL( params[0] );
				File file = null;
				FileOutputStream fOut = null;
				HttpURLConnection con = (HttpURLConnection)url.openConnection();
				con.setDoInput( true );
				con.connect();
				InputStream is = con.getInputStream();
				String newFilename = params[1] + "/" + params[2];
				file = new File( newFilename );
				fOut = new FileOutputStream( file );
				bitmap = BitmapFactory.decodeStream( is );
				if( bitmap != null )
				{
					bitmap.compress( Bitmap.CompressFormat.PNG , 100 , fOut );
				}
				if( is != null )
				{
					is.close();
				}
				fOut.flush();
				fOut.close();
				if( con != null )
				{
					con.disconnect();
				}
				// 关闭连接
				con.disconnect();
				Intent intent = new Intent( StaticClass.ACTION_THEME_UPDATE_RECOMMEND );
				mContext.sendBroadcast( intent );
			}
		}
		catch( MalformedURLException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return null;
	}
}
