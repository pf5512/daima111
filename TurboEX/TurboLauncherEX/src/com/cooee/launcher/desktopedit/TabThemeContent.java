package com.cooee.launcher.desktopedit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.cooee.launcher.Launcher;
import com.cooeeui.brand.turbolauncher.R;

/**
 * 桌面编辑模块代码
 * 
 * @author LinYu
 * 
 */
public class TabThemeContent extends RelativeLayout implements
		View.OnClickListener {

	private RelativeLayout mHotThemeBtn;
	private RelativeLayout mDefaultThemeBtn;
	private Launcher mLauncher;

	public TabThemeContent(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mLauncher = Launcher.getInstance();
	}

	public TabThemeContent(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mLauncher = Launcher.getInstance();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mHotThemeBtn = (RelativeLayout) findViewById(R.id.rl_hot);
		mHotThemeBtn.setOnClickListener(this);
		mDefaultThemeBtn = (RelativeLayout) findViewById(R.id.rl_local);
		mDefaultThemeBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_hot:
			final String TAG_THEME = "tagTheme";
			final String TAG_STRING = "currentTab";
			Intent i = new Intent();
			i.setComponent(new ComponentName(mLauncher,
					"com.coco.theme.themebox.MainActivity"));

			i.putExtra(TAG_STRING, TAG_THEME);
			i.putExtra("selcetHotTheme", true);
			mLauncher.startActivity(i);
			mLauncher.overridePendingTransition(R.anim.fade_in_fast,
					R.anim.fade_out_fast);
			break;
		case R.id.rl_local:

			break;

		default:
			break;
		}
	}
}
