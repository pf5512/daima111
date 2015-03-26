package com.cooee.launcher.desktopsettings;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooee.launcher.Launcher;
import com.cooee.launcher.common.CommonUtils;
import com.cooee.launcher.common.CopyDirectory;
import com.cooee.launcher.common.DataCleanManager;
import com.cooeeui.brand.turbolauncher.R;

/**
 * 桌面设置模块代码
 * 
 * @author liangxiaoling
 * 
 */
public class SystemActivity extends Activity implements View.OnClickListener {
	RelativeLayout ds_rl_systemback;
	RelativeLayout ds_rl_systemvibrator;
	RelativeLayout ds_rl_backup;
	RelativeLayout ds_rl_restore;
	RelativeLayout ds_rl_reset;
	TextView ds_tv_backdesktop;
	TextView ds_tv_restoredesktop;
	ImageView ds_iv_vibrator;
	private static final String DATABASE_NAME = "launcher.db";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ds_systemlayout);
		initViews();
		setCheckBackup();
	}

	public void initViews() {
		ds_rl_systemback = (RelativeLayout) findViewById(R.id.ds_rl_systemback);
		ds_rl_systemback.setOnClickListener(this);
		ds_rl_systemvibrator = (RelativeLayout) findViewById(R.id.ds_rl_systemvibrator);
		ds_rl_systemvibrator.setOnClickListener(this);
		ds_rl_backup = (RelativeLayout) findViewById(R.id.ds_rl_systembackup);
		ds_rl_backup.setOnClickListener(this);
		ds_rl_restore = (RelativeLayout) findViewById(R.id.ds_rl_systemrestore);
		ds_rl_restore.setOnClickListener(this);
		ds_rl_reset = (RelativeLayout) findViewById(R.id.ds_rl_systemreset);
		ds_rl_reset.setOnClickListener(this);
		ds_tv_backdesktop = (TextView) findViewById(R.id.ds_tv_backupdesktop);
		ds_tv_restoredesktop = (TextView) findViewById(R.id.ds_tv_restoredesktop);
		ds_iv_vibrator = (ImageView) findViewById(R.id.ds_iv_vibrator);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ds_rl_systemback:
			finish();
			break;
		case R.id.ds_rl_systembackup:
			if (CommonUtils.getSDPath() == null) {
				Toast.makeText(this,
						getString(R.string.ds_system_backup_pls_insert_SD),
						Toast.LENGTH_SHORT).show();
			} else {
				backdialog(getString(R.string.ds_system_backup_title_back),
						getString(R.string.ds_system_backup_back_to_SD));
			}
			break;
		case R.id.ds_rl_systemrestore:
			restoredialog(getString(R.string.ds_system_backup_title_restore),
					getString(R.string.ds_system_backup_restore));
			break;
		case R.id.ds_rl_systemreset:
			resetialog(getResources().getString(R.string.ds_system_reset),
					getResources().getString(R.string.ds_system_reset_msg));
			break;
		default:
			break;
		}

	}

	private void backdialog(String title, String msg) {
		AlertDialog.Builder builder = null;
		builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		builder.setMessage(msg);
		builder.setTitle(title);
		builder.setPositiveButton(getString(R.string.rename_action),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						backupdb();
					}
				});
		builder.setNegativeButton(getString(R.string.cancel_action),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	public void backupdb() {
		String dbSourceDir = Launcher.getInstance()
				.getDatabasePath(DATABASE_NAME).getParent();
		String dbTargetDir = CommonUtils.getBackupPath() + "/databases/";
		String prefsSourceDir = CommonUtils.getPreferencePath();
		String prefsTargetDir = CommonUtils.getBackupPath() + "/shared_prefs/";
		CopyDirectory.delete(new File(CommonUtils.getBackupPath()));
		try {
			CopyDirectory.copyDirectiory(dbSourceDir, dbTargetDir);
			CopyDirectory.copyDirectiory(prefsSourceDir, prefsTargetDir);
			Toast.makeText(this, getString(R.string.ds_system_backup_success),
					Toast.LENGTH_SHORT).show();
			setCheckBackup();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, getString(R.string.ds_system_backup_fail),
					Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressLint("NewApi")
	private void restoredialog(String title, String msg) {
		AlertDialog.Builder builder = null;
		builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		builder.setMessage(msg);
		builder.setTitle(title);
		builder.setPositiveButton(getString(R.string.rename_action),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (restoredb()) {
							finish();
							RestartSystem();
						}
					}
				});
		builder.setNegativeButton(getString(R.string.cancel_action),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	public boolean restoredb() {
		boolean bret = true;
		String dbTargetDir = Launcher.getInstance()
				.getDatabasePath(DATABASE_NAME).getParent();
		String dbSourceDir = CommonUtils.getBackupPath() + "/databases/";
		String prefsTargetDir = CommonUtils.getPreferencePath();
		String prefsSourceDir = CommonUtils.getBackupPath() + "/shared_prefs/";
		CopyDirectory.delete(new File(dbTargetDir));
		CopyDirectory.delete(new File(prefsTargetDir));
		try {
			CopyDirectory.copyDirectiory(dbSourceDir, dbTargetDir);
			CopyDirectory.copyDirectiory(prefsSourceDir, prefsTargetDir);
		} catch (IOException e) {
			bret = false;
			Toast.makeText(this, getString(R.string.ds_system_restore_fail),
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		return bret;
	}

	public void setCheckBackup() {
		if (checkBackup()) {
			ds_rl_restore.setEnabled(true);
		} else {
			ds_rl_restore.setEnabled(false);
		}
	}

	private void resetialog(String title, String msg) {
		AlertDialog.Builder builder = null;
		builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		builder.setMessage(msg);
		builder.setTitle(title);
		builder.setPositiveButton(getString(R.string.rename_action),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						DataCleanManager.cleanInternalCache(Launcher
								.getInstance());
						DataCleanManager.cleanDatabases(Launcher.getInstance());
						DataCleanManager.cleanSharedPreference(Launcher
								.getInstance());
						DataCleanManager.cleanFiles(Launcher.getInstance());
						DataCleanManager.cleanExternalCache(Launcher
								.getInstance());
						Launcher.getInstance().finish();
						Intent intent = new Intent(SystemActivity.this,
								Launcher.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						finish();
					}
				});
		builder.setNegativeButton(getString(R.string.cancel_action),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private boolean checkBackup() {
		boolean bret = false;
		File file = new File(CommonUtils.getBackupPath() + "/databases/",
				DATABASE_NAME);
		String strSumm = new String();
		String restoretxt = new String();
		if (file.exists()) {
			long createtime = file.lastModified();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(createtime);
			strSumm = getString(R.string.ds_system_backup_latest) + "  "
					+ cal.getTime().toLocaleString();
			restoretxt = getString(R.string.ds_system_restore_desktoptxt);
			bret = true;
		} else {
			strSumm = getString(R.string.ds_system_backup_default);
			restoretxt = getString(R.string.ds_system_restore_default);
		}
		ds_tv_backdesktop.setText(strSumm);
		ds_tv_restoredesktop.setText(restoretxt);
		return bret;
	}

	public static void RestartSystem() {

	}
}
