package com.cooee.launcher.desktopsettings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cooee.launcher.Launcher;
import com.cooeeui.brand.turbolauncher.R;
import com.tencent.open.yyb.AppbarAgent;
import com.tencent.tauth.Tencent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * 桌面设置模块代码
 * 
 * @author liangxiaoling
 * 
 */
public class DesktopSettingsActivity extends Activity implements
		View.OnClickListener {

	RelativeLayout ds_rl_defaultlaucher;
	RelativeLayout ds_rl_screen;
	RelativeLayout ds_rl_appdrawer;
	RelativeLayout ds_rl_icons;
	RelativeLayout ds_rl_gesture;
	RelativeLayout ds_rl_appbar;
	RelativeLayout ds_rl_system;
	RelativeLayout ds_rl_update;
	RelativeLayout ds_rl_about;
	public boolean isAllowOnclick = true;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ds_desktopsettingslayout);

		ds_rl_defaultlaucher = (RelativeLayout) findViewById(R.id.ds_rl_defaultlaucher);
		ds_rl_defaultlaucher.setOnClickListener(this);

		ds_rl_screen = (RelativeLayout) findViewById(R.id.ds_rl_screen);
		ds_rl_screen.setOnClickListener(this);

		ds_rl_appdrawer = (RelativeLayout) findViewById(R.id.ds_rl_appdrawer);
		ds_rl_appdrawer.setOnClickListener(this);

		ds_rl_icons = (RelativeLayout) findViewById(R.id.ds_rl_icon);
		ds_rl_icons.setOnClickListener(this);

		ds_rl_gesture = (RelativeLayout) findViewById(R.id.ds_rl_gesture);
		ds_rl_gesture.setOnClickListener(this);

		ds_rl_appbar = (RelativeLayout) findViewById(R.id.ds_rl_appbar);
		ds_rl_appbar.setOnClickListener(this);

		ds_rl_system = (RelativeLayout) findViewById(R.id.ds_rl_system);
		ds_rl_system.setOnClickListener(this);

		ds_rl_update = (RelativeLayout) findViewById(R.id.ds_rl_update);
		ds_rl_update.setOnClickListener(this);

		ds_rl_about = (RelativeLayout) findViewById(R.id.ds_rl_about);
		ds_rl_about.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ds_rl_defaultlaucher:

			break;
		case R.id.ds_rl_screen:
			startActivityForId(ScreenActivity.class);
			break;
		case R.id.ds_rl_appdrawer:
			startActivityForId(AppDrawerActivity.class);
			break;

		case R.id.ds_rl_icon:
			startActivityForId(IconsActivity.class);
			break;

		case R.id.ds_rl_gesture:
			startActivityForId(GestureActivity.class);
			break;
		case R.id.ds_rl_appbar:
			Tencent mTencent = Tencent.createInstance("1101476808",
					DesktopSettingsActivity.this);
			mTencent.startAppbar(DesktopSettingsActivity.this,
					AppbarAgent.TO_APPBAR_DETAIL);
			break;
		case R.id.ds_rl_system:
			startActivityForId(SystemActivity.class);
			break;
		case R.id.ds_rl_update:
			if (isAllowOnclick) {
				isAllowOnclick = false;
				checkUpdates();
			}
			break;
		case R.id.ds_rl_about:
			startActivityForId(AboutActivity.class);
			break;
		default:
			break;
		}
	}

	private void startActivityForId(Class<?> cls) {
		Intent in = new Intent(DesktopSettingsActivity.this, cls);
		startActivity(in);
	}

	public void checkUpdates() {
		Toast.makeText(this, R.string.ds_umeng_dstoastmsg, Toast.LENGTH_SHORT)
				.show();
		UmengUpdateAgent.setDefault();
		UmengUpdateAgent.update(Launcher.getInstance());
		UmengUpdateAgent.setUpdateOnlyWifi(true);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		// UmengUpdateAgent.setDeltaUpdate(true);//增量更新
		UmengUpdateAgent.setUpdateListener(updateListener);
	}

	UmengUpdateListener updateListener = new UmengUpdateListener() {

		@Override
		public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
			switch (updateStatus) {
			case UpdateStatus.Yes: // has update
				UmengUpdateAgent.showUpdateDialog(DesktopSettingsActivity.this,
						updateInfo);
				break;
			case UpdateStatus.No: // has no update
				showDialog(DesktopSettingsActivity.this.getResources()
						.getString(R.string.ds_umeng_update_dialogmsg));
				break;
			case UpdateStatus.NoneWifi: // none wifi
				String str = DesktopSettingsActivity.this.getResources()
						.getString(R.string.ds_umeng_update_wifi);
				if (toast != null) {
					toast.setText(str);
				} else {
					toast = Toast.makeText(DesktopSettingsActivity.this, str,
							Toast.LENGTH_SHORT);
				}
				toast.show();
				break;
			case UpdateStatus.Timeout: // time out
				String str1 = DesktopSettingsActivity.this.getResources()
						.getString(R.string.ds_umeng_update_timeout);
				if (toast != null) {
					toast.setText(str1);
				} else {
					toast = Toast.makeText(DesktopSettingsActivity.this, str1,
							Toast.LENGTH_SHORT);
				}
				toast.show();
				break;
			default:
				break;
			}
			isAllowOnclick = true;
		}
	};

	private void showDialog(String content) {
		AlertDialog.Builder builder = new Builder(this)
				.setTitle(R.string.version_update).setMessage(content)
				.setPositiveButton(android.R.string.ok, null);
		builder.show();
	}
}
