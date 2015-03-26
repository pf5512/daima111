package com.coco.theme.themebox;

import java.io.File;
import com.coco.lock2.lockbox.DownloadLockBoxService;
import com.coco.lock2.lockbox.TabLockFactory;
import com.coco.lock2.lockbox.util.DownModule;
import com.coco.theme.themebox.service.ThemeService;
import com.coco.theme.themebox.util.PathTool;
import com.coco.theme.themebox.util.ThemeDownModule;
import com.iLoong.base.themebox.R;
import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import com.coco.theme.themebox.util.Log;

public class MainActivity extends Activity {
	private final String TAG_THEME = "tagTheme";
	private final String TAG_LOCK = "tagLock";
	private DownModule downModule;
	private TabLockFactory tabLock;
	private ThemeDownModule themedownModule;
	private TabThemeFactory tabTheme;
	private TabHost tabHost;

	// teapotXu_20130304: add start
	// set a flag that indicates whether the ThemeSelectIcon launched the
	// ThemeBox or Launcher app.
	private boolean b_theme_icon_start_launcher = false;
	// teapotXu_20130304: add end
	private Context mContext;

	public static String PATH_ENABLE_LOG = "enablelog.log"; // added by
															// zhenNan.ye

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActivityManager.pushActivity(this);
		super.onCreate(savedInstanceState);
		mContext = this;

		// added by zhenNan.ye begin
		String sdpath = getSDPath();
		if (sdpath != null) {
			PATH_ENABLE_LOG = sdpath + File.separator + "enablelog.log";
			File dir = new File(PATH_ENABLE_LOG);
			if (dir.exists()) {
				Log.setEnableLog(true);
			}
		}
		// added by zhenNan.ye end

	

		if (!com.coco.theme.themebox.StaticClass.isAllowDownload(mContext)
				&& com.coco.theme.themebox.util.FunctionConfig
						.isDownToInternal()) {
			com.coco.theme.themebox.StaticClass.canDownToInternal = true;
		}
		PathTool.makeDirApp();
		// teapotXu_20130304: add start
		// set a flag that indicates whether the ThemeSelectIcon launched the
		// ThemeBox or Launcher app.
		String pkgNameFromThemeBox = getIntent().getStringExtra("FROM_PACKAGE");

		if (pkgNameFromThemeBox != null
				&& pkgNameFromThemeBox.length() > 16
				&& pkgNameFromThemeBox.substring(0, 16).equals(
						"com.coco.themes.")) {
			b_theme_icon_start_launcher = true;
			return;
		}

		// teapotXu_20130304: add end
		downModule = new DownModule(this);
		themedownModule = new ThemeDownModule(this);
		if (!com.coco.theme.themebox.util.FunctionConfig.isLockVisible()
				&& !com.coco.lock2.lockbox.StaticClass.isLockBoxInstalled(this)) {
			setContentView(R.layout.man_tab_nolock);
		} else {
			setContentView(R.layout.main_tab_lock);
		}
		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		// 添加主题页面

		tabTheme = new TabThemeFactory(MainActivity.this, themedownModule);
		final View indicatorTheme = View.inflate(MainActivity.this,
				R.layout.indicator_theme, null);
		tabHost.addTab(tabHost.newTabSpec(TAG_THEME)
				.setIndicator(indicatorTheme).setContent(tabTheme));

		if (com.coco.theme.themebox.util.FunctionConfig.isLockVisible()
				|| com.coco.lock2.lockbox.StaticClass.isLockBoxInstalled(this)) {
			int Screen_W = getWindowManager().getDefaultDisplay().getWidth();
			int Screen_H = getWindowManager().getDefaultDisplay().getHeight();
			// 添加锁屏页面
			tabLock = new TabLockFactory(MainActivity.this, downModule,
					Screen_W, Screen_H);
			View indicatorLock = View.inflate(MainActivity.this,
					R.layout.indicator_lock, null);
			tabHost.addTab(tabHost.newTabSpec(TAG_LOCK)
					.setIndicator(indicatorLock).setContent(tabLock));

			int tabIndex = getIntent().getIntExtra(
					StaticClass.EXTRA_MAIN_TAB_INDEX, 0);
			switch (tabIndex) {
			default:
			case 0:
				tabHost.setCurrentTabByTag(TAG_THEME);
				break;
			case 1:
				tabHost.setCurrentTabByTag(TAG_LOCK);
				break;
			}
		}

		// 友盟 进入主题盒子统计
		MobclickAgent.onEvent(mContext, "StartBox");
		// 友盟 新用户统计
		NewUser(mContext);
		// 友盟 活跃用户统计
		ActiveUser(mContext);
	}

	// teapotXu_20130304: add start
	// set a flag that indicates whether the ThemeSelectIcon launched the
	// ThemeBox or Launcher app.
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (b_theme_icon_start_launcher == true) {
			String theme_icon_pkgName = getIntent().getStringExtra(
					"FROM_PACKAGE");


			// start the launcher directly
			ThemeService sv = new ThemeService(this);

			sv.applyTheme(sv.queryComponent(theme_icon_pkgName));
			sendBroadcast(new Intent(StaticClass.ACTION_DEFAULT_THEME_CHANGED));
			ActivityManager.KillActivity();
		}
	}

	// teapotXu_20130304: add end

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			if (com.coco.theme.themebox.util.FunctionConfig.isLockVisible()
					&& com.coco.lock2.lockbox.StaticClass
							.isLockBoxInstalled(this)) {
				// 在lock界面按设置才有用
				int tabIndex = tabHost.getCurrentTab();
				if (tabIndex == 1) {
					Intent intentSetting = new Intent();
					intentSetting.setClassName(
							StaticClass.LOCKBOX_PACKAGE_NAME,
							StaticClass.LOCKBOX_SETTING_ACTIVITY);
					startActivity(intentSetting);
				}
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		ActivityManager.popupActivity(this);
		// teapotXu_20130304: add start
		// set a flag that indicates whether the ThemeSelectIcon launched the
		// ThemeBox or Launcher app.
		if (b_theme_icon_start_launcher == false) {
			if (tabLock != null)
				tabLock.onDestroy();
			tabTheme.onDestroy();
			downModule.dispose();
			themedownModule.dispose();
		}
		// teapotXu_20130304: add end
		super.onDestroy();
	}

	public static int getProxyPort(Context context) {
		int res = Proxy.getPort(context);
		if (res == -1)
			res = Proxy.getDefaultPort();
		return res;
	}

	public static boolean isCWWAPConnect(Context context) {
		boolean result = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
			if ((Proxy.getDefaultHost() != null || Proxy.getHost(context) != null)
					&& (Proxy.getPort(context) != -1 || Proxy.getDefaultPort() != -1)) {
				result = true;
			}
		}
		return result;
	}

	public static int getNetWorkType(Context context) {
		int netType = -1;

		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String simOperator = manager.getSimOperator();
		if (simOperator != null) {
			if (simOperator.startsWith("46000")
					|| simOperator.startsWith("46002")) {
				netType = DownloadLockBoxService.NETTYPE_MOBILE;
			} else if (simOperator.startsWith("46001")) {
				netType = DownloadLockBoxService.NETTYPE_UNICOM;
			} else if (simOperator.startsWith("46003")) {
				netType = DownloadLockBoxService.NETTYPE_TELECOM;
			}
		}
		return netType;
	}

	public static String getProxyHost(Context context) {
		String res = Proxy.getHost(context);
		if (res == null)
			res = Proxy.getDefaultHost();
		return res;
	}

	// added by zhenNan.ye 20130724
	private String getSDPath() {
		File SDdir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			SDdir = Environment.getExternalStorageDirectory();
		}
		if (SDdir != null) {
			return SDdir.toString();
		} else {
			return null;
		}
	}

	private void NewUser(Context content) {
		SharedPreferences sp = content.getSharedPreferences("theme_analytics",
				Context.MODE_PRIVATE);
		Boolean flag = sp.getBoolean("NewUser", false);
		if (!flag) {
			MobclickAgent.onEvent(content, "NewUser");
			Editor sharedata = sp.edit();
			sharedata.putBoolean("NewUser", true);
			sharedata.commit();
		}

	}

	private void ActiveUser(Context content) {
		Time time = new Time("GMT+8");
		time.setToNow();
		int day = time.yearDay;
		SharedPreferences sp = content.getSharedPreferences("theme_analytics",
				Context.MODE_PRIVATE);
		int aday = sp.getInt("Day", -1);
		if (day != aday) {
			MobclickAgent.onEvent(content, "ActiveUser");
			Editor sharedata = sp.edit();
			sharedata.putInt("Day", day);
			sharedata.commit();
		}

	}
}
