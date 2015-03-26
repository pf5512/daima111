//wanghongjian add whole file	//enable_DefaultScene
package com.iLoong.launcher.scene;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import com.iLoong.launcher.Desktop3D.Log;
import com.umeng.analytics.MobclickAgent;

public class SceneDetailedActivity extends Activity {
	public SceneDetailed mThemeDetailed;
	private int index;
	private SceneDescription theme;
	private static SceneDetailedActivity sdactivity = null;

	public void onCreate(Bundle bundle) {
		Log.v("ThemeDetailedActivity", "onCreate");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(bundle);
		requestWindowFeature(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		sdactivity = this;
		mThemeDetailed = new SceneDetailed(this);
		Bundle extras = getIntent().getExtras();
		index = extras.getInt(SceneDetailedActivity.class.getSimpleName());

		if (index >= SceneManager.getInstance().getThemeDescriptions().size()) {
			finish();
		} else {
			theme = SceneManager.getInstance().getThemeDescriptions()
					.elementAt(index);
			if (!SceneManager.getInstance().FindThemes(
					theme.componentName.getPackageName())) {
				finish();
			} else {
				SceneManager.getInstance().pushActivity(this);
				setContentView(mThemeDetailed);
				mThemeDetailed.LoadData(index);
			}
		}
	}

	public static SceneDetailedActivity getInstance() {
		return sdactivity;
	}

	protected void onStart() {
		Log.v("ThemeDetailedActivity", "onStart");
		super.onStart();
		if (!SceneManager.getInstance().FindThemes(
				theme.componentName.getPackageName())) {
			finish();
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		mThemeDetailed.Release();
		SceneManager.getInstance().popupActivity(this);
		Log.v("ThemeDetailedActivity", "onDestroy");
	}
	@Override
	protected void onResume() {
		super.onResume();
	
		//友盟统计
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//友盟统计
		MobclickAgent.onPause(this);
	}
}
