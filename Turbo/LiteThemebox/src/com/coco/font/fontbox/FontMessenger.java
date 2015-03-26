package com.coco.font.fontbox;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import cn.moppo.fontstore.flipfont.FontParcelable;

import com.coco.theme.themebox.util.Log;


public class FontMessenger
{
	
	private static final boolean DEBUG = true;
	private static final String TAG = "FontMessenger";
	private Messenger mService = null;
	public static String PROP_NAME = "flip_pkg_flag";
	public static final String DEFAULT_FONT_VALUE = "default";
	
	public interface ServiceConnected
	{
		
		public void onServiceConnected();
	}
	
	private ServiceConnected mServiceConnected;
	public boolean mBound;
	
	public FontMessenger(
			ServiceConnected serviceConnected )
	{
		mServiceConnected = serviceConnected;
	}
	
	public boolean bindService(
			Context context )
	{
		Intent intent = new Intent();
		ComponentName componentName = new ComponentName( "com.android.settings" , "com.android.settings.flipfont.FontStoreService" );
		intent.setComponent( componentName );
		return context.bindService( intent , mConnection , Context.BIND_AUTO_CREATE );
	}
	
	public void unbindService(
			Context context )
	{
		if( !mBound )
			return;
		context.unbindService( mConnection );
		mService = null;
		mBound = false;
	}
	
	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		
		public void onServiceConnected(
				ComponentName className ,
				IBinder service )
		{
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service. We are communicating with the
			// service using a Messenger, so here we get a client-side
			// representation of that from the raw IBinder object.
			mService = new Messenger( service );
			mBound = true;
			mServiceConnected.onServiceConnected();
			if( DEBUG )
				Log.d( TAG , "-mConnection onServiceConnected" );
		}
		
		public void onServiceDisconnected(
				ComponentName className )
		{
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
			mBound = false;
			if( DEBUG )
				Log.d( TAG , "-mConnection onServiceDisconnected" );
		}
	};
	
	public void applyInterface(
			int what ,
			String arg )
	{
		if( !mBound )
			return;
		// Create and send a message to the service, using a supported 'what'
		// value
		// FontParcelable fontParcelable = new FontParcelable(pkgName);
		// Message msg = Message.obtain(null, 1, 0, 0, fontParcelable);
		Message msg = Message.obtain( null , what , 0 , 0 );
		Bundle data = new Bundle();
		FontParcelable fontParcelable = new FontParcelable( arg );
		data.putParcelable( "fontservice_data" , fontParcelable );
		msg.setData( data );
		try
		{
			mService.send( msg );
		}
		catch( RemoteException e )
		{
			e.printStackTrace();
		}
		Log.d( TAG , "-applyFont=" );
	}
	
	public String getCurrentSystemFontPackageName(
			Context context )
	{
		String pkg = Settings.System.getString( context.getContentResolver() , PROP_NAME );
		if( DEBUG )
			Log.d( TAG , "getCurrentSystemFontPackageName-pkg=" + pkg );
		if( pkg == null || pkg.length() < 1 )
		{
			pkg = DEFAULT_FONT_VALUE;
		}
		return pkg;
	}
}
