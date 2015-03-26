package com.coco.theme.themebox.apprecommend;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.database.CursorJoiner.Result;
import android.os.AsyncTask;


public class MyAsyncTask extends AsyncTask<String , Integer , Result>
{
	
	private DomXMLReader paraseXml;
	private Context mContext;
	
	public MyAsyncTask(
			Context context )
	{
		mContext = context;
	}
	
	@Override
	protected Result doInBackground(
			String ... params )
	{
		try
		{
			URL url = new URL( params[0] );
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setDoInput( true );
			con.connect();
			InputStream is = con.getInputStream();
			paraseXml = new DomXMLReader( mContext );
			paraseXml.readXML( is );
			is.close();
			// 关闭连接
			con.disconnect();
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
