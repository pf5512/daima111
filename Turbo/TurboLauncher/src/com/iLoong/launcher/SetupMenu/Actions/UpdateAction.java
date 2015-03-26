package com.iLoong.launcher.SetupMenu.Actions;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;

import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManagerActivity;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;


public class UpdateAction extends Action
{
	
	public UpdateAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		putIntentAction( SetupMenu.getContext() , ThemeManagerActivity.class );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_UPDATE , new UpdateAction( ActionSetting.ACTION_UPDATE , UpdateAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		if( SetupMenu.getInstance() != null )
			SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
		if( RR.net_version )
		{
			//			Uri playUri = Uri.parse( "https://play.google.com/store/apps/details?id=com.cooeeui.brand.turbolauncher" );
			//			Intent browserIntent = new Intent( Intent.ACTION_VIEW , playUri );
			//			if( isPlayStoreInstalled() )
			//			{
			//				browserIntent.setClassName( "com.android.vending" , "com.android.vending.AssetBrowserActivity" );
			//			}
			//			browserIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			//			iLoongLauncher.getInstance().startActivity( browserIntent );
			UmengUpdateAgent.update( iLoongLauncher.getInstance() );
			UmengUpdateAgent.setUpdateOnlyWifi( true );
			UmengUpdateAgent.setUpdateAutoPopup( false );
			UmengUpdateAgent.setUpdateListener( updateListener );
		}
		else
		{
			UpdateManager.getInstance().checkClientVersion();
		}
	}
	
	@Override
	protected void OnActionFinish()
	{
	}
	
	@Override
	protected void OnPutValue(
			String key )
	{
	}
	
	UmengUpdateListener updateListener = new UmengUpdateListener() {
		
		@Override
		public void onUpdateReturned(
				int updateStatus ,
				UpdateResponse updateInfo )
		{
			switch( updateStatus )
			{
				case UpdateStatus.Yes: //has update
					UmengUpdateAgent.showUpdateDialog( iLoongLauncher.getInstance() , updateInfo );
					break;
				case UpdateStatus.No: //has no update
					showDialog( iLoongLauncher.getInstance().getApplicationContext().getString( RR.string.update_dialogmsg ) );
					Toast.makeText( iLoongLauncher.getInstance() , iLoongLauncher.getInstance().getApplicationContext().getString( RR.string.update_toastmsg ) , Toast.LENGTH_SHORT ).show();
					break;
				case UpdateStatus.NoneWifi: //none wifi
					Toast.makeText( iLoongLauncher.getInstance() , iLoongLauncher.getInstance().getApplicationContext().getString( RR.string.update_wifi ) , Toast.LENGTH_SHORT ).show();
					break;
				case UpdateStatus.Timeout: //time out
					Toast.makeText( iLoongLauncher.getInstance() , iLoongLauncher.getInstance().getApplicationContext().getString( RR.string.update_timeout ) , Toast.LENGTH_SHORT ).show();
					break;
				default:
					break;
			}
		}
	};
	
	private void showDialog(
			String content )
	{
		AlertDialog.Builder builder = new Builder( iLoongLauncher.getInstance() ).setTitle( RR.string.version_update ).setMessage( content ).setPositiveButton( android.R.string.ok , null );
		builder.show();
	}
	
	private boolean isPlayStoreInstalled()
	{
		String playPkgName = "com.android.vending";
		try
		{
			PackageInfo pckInfo = iLoongLauncher.getInstance().getPackageManager().getPackageInfo( playPkgName , PackageManager.GET_ACTIVITIES );
			return true;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
			return false;
		}
	}
}
