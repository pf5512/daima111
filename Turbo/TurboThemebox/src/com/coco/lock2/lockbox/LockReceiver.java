package com.coco.lock2.lockbox;


import java.io.File;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.coco.lock2.lockbox.util.LockManager;
import com.coco.theme.themebox.util.Log;


public class LockReceiver extends BroadcastReceiver
{
	
	@Override
	public void onReceive(
			final Context context ,
			Intent intent )
	{
		if( intent.getAction().equals( Intent.ACTION_PACKAGE_ADDED ) )
		{
			new Thread( new addRunnable( intent , context ) ).start();
		}
		else if( intent.getAction().equals( Intent.ACTION_PACKAGE_REMOVED ) )
		{
			new Thread( new removeRunnable( intent , context ) ).start();
		}
		else if( intent.getAction().equals( Intent.ACTION_USER_PRESENT ) )
		{
			if( !PlatformInfo.getInstance( context ).isSupportViewLock() )
			{
				Intent intent1 = new Intent( context , LockService.class );
				intent1.setAction( StaticClass.ACTION_KILL_SYSLOCK );
				context.startService( intent1 );
			}
		}
		else if( intent.getAction().equals( Intent.ACTION_BOOT_COMPLETED ) )
		{
			if( !PlatformInfo.getInstance( context ).isSupportViewLock() )
			{
				context.startService( new Intent( context , LockService.class ) );
			}
		}
	}
	
	private class addRunnable implements Runnable
	{
		
		public Intent intent;
		public Context mContext;
		
		public addRunnable(
				Intent i ,
				Context c )
		{
			intent = i;
			mContext = c;
		}
		
		public void run()
		{
			String packageName = intent.getData().getSchemeSpecificPart();
			LockManager lockManager = new LockManager( mContext );
			ComponentName comName = lockManager.queryComponent( packageName );
			if( comName != null )
			{
				LockInformation infor = lockManager.queryLock( comName.getPackageName() , comName.getClassName() );
				infor.loadDetail( mContext );
				if( infor.getThumbImage() != null )
				{
					StaticClass.saveMyBitmap( mContext , infor.getPackageName() , infor.getClassName() , infor.getThumbImage() );
				}
			}
		}
	}
	
	private class removeRunnable implements Runnable
	{
		
		public Intent intent;
		public Context mContext;
		
		public removeRunnable(
				Intent i ,
				Context c )
		{
			intent = i;
			mContext = c;
		}
		
		public void run()
		{
			Log.d( "TabLockContentFactory" , String.format( "action=%s" , "dsfdggd" ) );
			String packageName = intent.getData().getSchemeSpecificPart();
			File f = mContext.getDir( "coco" , Context.MODE_PRIVATE );
			File f1 = new File( f + "/" + packageName );
			recursionDeleteFile( f1 );
			LockManager mgr = new LockManager( mContext );
			ComponentName currentLock = mgr.queryCurrentLock();
			if( packageName.equals( currentLock.getPackageName() ) )
			{
				AppConfig config = AppConfig.getInstance( mContext );
				mgr.applyLock( config.getDefaultLockscreenPackage() , config.getDefaultLockscreenClass() , config.getDefaultLockscreenWrap() );
				mContext.sendBroadcast( new Intent( StaticClass.ACTION_DEFAULT_LOCK_CHANGED ) );
				if( !PlatformInfo.getInstance( mContext ).isSupportViewLock() )
				{
					Intent intent1 = new Intent( mContext , LockService.class );
					intent1.setAction( StaticClass.ACTION_KILL_SYSLOCK );
					mContext.startService( intent1 );
				}
			}
		}
	}
	
	private static void recursionDeleteFile(
			File file )
	{
		if( file.isFile() )
		{
			file.delete();
			return;
		}
		if( file.isDirectory() )
		{
			File[] childFile = file.listFiles();
			if( childFile == null )
			{
				return;
			}
			for( File f : childFile )
			{
				recursionDeleteFile( f );
			}
			file.delete();
		}
	}
}
