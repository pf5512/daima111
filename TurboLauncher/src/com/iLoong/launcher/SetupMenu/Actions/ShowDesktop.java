package com.iLoong.launcher.SetupMenu.Actions;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.desktop.iLoongLauncher;


// import com.iLoong.launcher.Desktop3D.FeaturePanel;
public class ShowDesktop extends Action
{
	
	Dialog progressDialog = null;
	
	public ShowDesktop(
			int actionid ,
			String action )
	{
		super( actionid , action );
	}
	
	public ShowDesktop()
	{
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_SHOW_DESKTOP , new ShowDesktop( ActionSetting.ACTION_SHOW_DESKTOP , ShowDesktop.class.getName() ) );
	}
	
	public void delayShare()
	{
		File fileDir = iLoongLauncher.getInstance().getFilesDir();
		Log.v( "pckCls" , "dir" );
		try
		{
			File file = new File( Utils3D.getScreenShotPath() );
			if( !file.exists() )
			{
				Messenger.sendMsg( Messenger.MSG_NOT_FIND_SD_CARD , null );
				return;
			}
		}
		catch( Exception e )
		{
			Messenger.sendMsg( Messenger.MSG_NOT_FIND_SD_CARD , null );
			return;
		}
		Uri data = Uri.fromFile( new File( Utils3D.getScreenShotPath() ) );
		// Uri data =Uri.fromFile(new
		// File(fileDir.getAbsolutePath()+File.separator+Utils3D.getScreenShotPath()));
		Intent shareIntent = new Intent();
		shareIntent.setAction( Intent.ACTION_SEND );// Intent.ACTION_SEND_MULTIPLE);
		shareIntent.putExtra( Intent.EXTRA_STREAM , data );
		// shareIntent.setType("text/plain");
		shareIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		shareIntent.setType( "image/*" );
		shareIntent.putExtra( "sms_body" , SetupMenu.getContext().getResources().getString( RR.string.desktop_download_info ) );
		SendMsgToAndroid.startActivity( Intent.createChooser( shareIntent , iLoongLauncher.getInstance().getResources().getString( RR.string.media_share ) ) );
	}
	
	public static Bitmap getWallpaper()
	{
		int wpOffsetX;
		Resources res = iLoongLauncher.getInstance().getResources();
		int screenWidth = res.getDisplayMetrics().widthPixels;
		int screenHeight = res.getDisplayMetrics().heightPixels;
		WallpaperManager wallpaperManager = WallpaperManager.getInstance( iLoongLauncher.getInstance() );
		//	WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
		Drawable drawable = wallpaperManager.getDrawable();
		Bitmap wallpaperBitmap = ( (BitmapDrawable)drawable ).getBitmap();
		int wpWidth = wallpaperBitmap.getWidth();
		int wpHeight = wallpaperBitmap.getHeight();
		//		int[] pixData = new int[wallpaperBitmap.getWidth()
		//				* wallpaperBitmap.getHeight()];
		if( wpWidth > screenWidth )
		{
			int currScreen = iLoongLauncher.getInstance().getCurrentScreen();
			int screenNum = iLoongLauncher.getInstance().getScreenCount();
			int gapWidth = wpWidth - screenWidth;
			wpOffsetX = (int)( (float)gapWidth * currScreen / ( screenNum - 1 ) );
			Bitmap screenShot = Bitmap.createBitmap( wallpaperBitmap , wpOffsetX , Utils3D.getStatusBarHeight() , screenWidth , screenHeight - Utils3D.getStatusBarHeight() );
			return screenShot;
		}
		return wallpaperBitmap;
	}
	
	@Override
	public void OnRunAction()
	{
		boolean sdExist = Environment.getExternalStorageState().equals( android.os.Environment.MEDIA_MOUNTED );
		if( !sdExist )
		{
			Messenger.sendMsg( Messenger.MSG_NOT_FIND_SD_CARD , null );
			return;
		}
		SendMsgToAndroid.showCustomDialog( 500 , 500 );//sendCreateShareProgressDialogMsg();
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			public void run()
			{
				Log.v( "Hotseat " , " reget show image" );
				Utils3D.getScreenShot( getWallpaper() );
				if( Utils3D.getScreenShotPath() != null )
				{
					SendMsgToAndroid.sendCancelShareProgressDialogMsg();
				}
				delayShare();
				// Utils3D.getScreenShot();
			}
		} );
	}
	
	public static boolean saveBmpToSystem(
			Bitmap thumbnail ,
			String name )
	{
		File fileDir = iLoongLauncher.getInstance().getFilesDir();
		File file = new File( fileDir.getAbsolutePath() + File.separator + Utils3D.getScreenShotPath() );
		FileOutputStream os;
		try
		{
			os = new FileOutputStream( file );
			// os=iLoongLauncher.getInstance().openFileOutput(name,Activity.MODE_PRIVATE);
			thumbnail.compress( Bitmap.CompressFormat.PNG , 100 , os );
			os.flush();
			os.close();
		}
		catch( IOException e1 )
		{
		}
		return true;
	}
	
	public static boolean saveBmp(
			Bitmap thumbnail ,
			String path )
	{
		File f = new File( path );
		try
		{
			if( f.exists() )
			{
				f.delete();
			}
			f.createNewFile();
		}
		catch( IOException e1 )
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		FileOutputStream fOut = null;
		try
		{
			fOut = new FileOutputStream( f );
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		if( fOut == null )
			return false;
		thumbnail.compress( Bitmap.CompressFormat.PNG , 100 , fOut );
		try
		{
			fOut.flush();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		try
		{
			fOut.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	protected void OnActionFinish()
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void OnPutValue(
			String key )
	{
		// TODO Auto-generated method stub
	}
	//
	//	 private class ShareHandler extends Handler{
	//	
	//	 public static final int ON_SHARE =1;
	//	 public static final int NOT_FIND_SD=2;
	//	 public static final int SHOW_PROGRESS =3;
	//	
	//	 @Override
	//	 public void handleMessage(Message msg) {
	//    	 int wpOffsetX;
	//    	 super.handleMessage(msg);
	//    	 int what =msg.what;
	//    	 String path =msg.obj.toString();
	//    	 switch(what){
	//    	
	//    	     case SHOW_PROGRESS:{
	//    	         showProgressDialog();
	//    	     }break;
	//    	
	//    	    
	//    	 }
	//	
	//	 }
	//	
	//	
	//	
	//	 }
	// @Override
	// public void onAction() {
	// OnRunAction();
	//
	// }
	//
	// @Override
	// public void setParams(Object obj) {
	// // TODO Auto-generated method stub
	//
	// }
}
