//wanghongjian add whole file	//enable_DefaultScene
package com.iLoong.launcher.scene;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import com.iLoong.launcher.Desktop3D.Log;
import com.umeng.analytics.MobclickAgent;

public class SceneManagerActivity extends Activity {
	public ScenesDesktop mThemeDesktop;

	public void onCreate(Bundle bundle) {
		Log.v("ThemeManagerActivity", "onCreate");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(bundle);
		SceneManager.getInstance().pushActivity(this);
		requestWindowFeature(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mThemeDesktop = new ScenesDesktop(this);
		setContentView(mThemeDesktop);
	}

	protected void onStart() {
		Log.v("ThemeManagerActivity", "onStart");
		super.onStart();
		mThemeDesktop.LoadData();
	}

	protected void onDestroy() {
		super.onDestroy();
		mThemeDesktop.Release();
		SceneManager.getInstance().popupActivity(this);
		Log.v("ThemeManagerActivity", "onDestroy");
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
