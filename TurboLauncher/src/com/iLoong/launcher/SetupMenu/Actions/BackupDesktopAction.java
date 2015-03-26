package com.iLoong.launcher.SetupMenu.Actions;


import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.CopyDirectory;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.desktop.iLoongApplication;


public class BackupDesktopAction extends Action
{
	
	private static final String DATABASE_NAME = "launcher.db";
	
	public BackupDesktopAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		putIntentAction( SetupMenu.getContext() , BackupActivity.class );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_BACKUP_DESKTOP , new BackupDesktopAction( ActionSetting.ACTION_BACKUP_DESKTOP , BackupDesktopAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		SynRunAction();
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
	
	public static class BackupActivity extends PreferenceActivity
	{
		
		@Override
		public void onCreate(
				Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );
			addPreferencesFromResource( RR.xml.backuprestore );
			showitem();
		}
		
		private void showitem()
		{
			String key = SetupMenu.getContext().getResources().getString( RR.string.setting_key_restore );
			Preference pf = (Preference)findPreference( key );
			if( !checkBackup() )
			{
				pf.setEnabled( false );
			}
			else
			{
				pf.setEnabled( true );
			}
		}
		
		@Override
		public boolean onPreferenceTreeClick(
				PreferenceScreen preferenceScreen ,
				Preference preference )
		{
			String key = preference.getKey();
			if( key.equals( SetupMenu.getContext().getResources().getString( RR.string.setting_key_backup ) ) )
			{
				backdialog( R3D.getString( RR.string.backup_title_back ) , R3D.getString( RR.string.backup_back_to_SD ) );
			}
			else if( key.equals( SetupMenu.getContext().getResources().getString( RR.string.setting_key_restore ) ) )
			{
				restoredialog( R3D.getString( RR.string.backup_title_restore ) , R3D.getString( RR.string.backup_restore ) );
			}
			return false;
		}
		
		private boolean checkBackup()
		{
			boolean bret = false;
			String key = SetupMenu.getContext().getResources().getString( RR.string.setting_key_backup );
			Preference pf = (Preference)findPreference( key );
			File file = new File( iLoongApplication.getAppPath() , DATABASE_NAME );
			String strSumm = new String();
			if( file.exists() )
			{
				long createtime = file.lastModified();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis( createtime );
				strSumm = R3D.getString( RR.string.backup_latest ) + cal.getTime().toLocaleString();
				bret = true;
			}
			else
			{
				strSumm = R3D.getString( RR.string.backup_no_back );
			}
			pf.setSummary( strSumm );
			return bret;
		}
		
		private void backdialog(
				String title ,
				String msg )
		{
			AlertDialog.Builder builder = new Builder( this );
			builder.setMessage( msg );
			builder.setTitle( title );
			builder.setPositiveButton( R3D.getString( RR.string.circle_ok_action ) , new OnClickListener() {
				
				@Override
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					dialog.dismiss();
					bakcupdb();
					showitem();
				}
			} );
			builder.setNegativeButton( R3D.getString( RR.string.circle_cancel_action ) , new OnClickListener() {
				
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
		
		private void restoredialog(
				String title ,
				String msg )
		{
			AlertDialog.Builder builder = new Builder( this );
			builder.setMessage( msg );
			builder.setTitle( title );
			builder.setPositiveButton( R3D.getString( RR.string.circle_ok_action ) , new OnClickListener() {
				
				@Override
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					dialog.dismiss();
					if( restoredb() )
					{
						BackupActivity.this.finish();
						SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_RESTART );
					}
				}
			} );
			builder.setNegativeButton( R3D.getString( RR.string.circle_cancel_action ) , new OnClickListener() {
				
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
		
		public void bakcupdb()
		{
			String sourcepath = iLoongApplication.getInstance().getDatabasePath( DATABASE_NAME ).getPath();
			String targetpath = iLoongApplication.getAppPath() + "/" + DATABASE_NAME;
			String prefsSourceDir = iLoongApplication.getPreferencePath();
			String prefsTargetDir = iLoongApplication.getAppPath() + "/prefs/";
			File sourceFile = new File( sourcepath );
			File targetFile = new File( targetpath );
			try
			{
				CopyDirectory.copyFile( sourceFile , targetFile );
				File file = new File( prefsSourceDir );
				if( file != null )
				{
					File[] files = file.listFiles();
					if( files != null )
					{
						for( int i = 0 ; i < files.length ; i++ )
						{
							CopyDirectory.copyFile( files[i] , new File( prefsTargetDir + files[i].getName() ) );
						}
					}
				}
				Toast.makeText( this , R3D.getString( RR.string.backup_success ) , Toast.LENGTH_SHORT ).show();
			}
			catch( IOException e )
			{
				e.printStackTrace();
				Toast.makeText( this , R3D.getString( RR.string.backup_fail ) , Toast.LENGTH_SHORT ).show();
			}
		}
		
		public boolean restoredb()
		{
			boolean bret = true;
			String targetpath = iLoongApplication.getInstance().getDatabasePath( DATABASE_NAME ).getPath();
			String sourcepath = iLoongApplication.getAppPath() + "/" + DATABASE_NAME;
			String prefsTargetDir = iLoongApplication.getPreferencePath();
			String prefsSourceDir = iLoongApplication.getAppPath() + "/prefs/";
			File sourceFile = new File( sourcepath );
			File targetFile = new File( targetpath );
			try
			{
				CopyDirectory.copyFile( sourceFile , targetFile );
				File file = new File( prefsSourceDir );
				if( file != null )
				{
					File[] files = file.listFiles();
					if( files != null )
					{
						for( int i = 0 ; i < files.length ; i++ )
						{
							CopyDirectory.copyFile( files[i] , new File( prefsTargetDir + files[i].getName() ) );
						}
					}
				}
			}
			catch( IOException e )
			{
				bret = false;
				Toast.makeText( this , R3D.getString( RR.string.restore_fail ) , Toast.LENGTH_SHORT ).show();
				e.printStackTrace();
			}
			return bret;
		}
	}
}
