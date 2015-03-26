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
public class TabWallpaperContent extends RelativeLayout implements
		View.OnClickListener {

	private RelativeLayout mHotWallpaperBtn;
	private RelativeLayout mLocalWallpaperBtn;
	private Launcher mLauncher;

	public TabWallpaperContent(Context context) {
		this(context, null);
		mLauncher = Launcher.getInstance();
	}

	public TabWallpaperContent(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mLauncher = Launcher.getInstance();
	}

	public TabWallpaperContent(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mLauncher = Launcher.getInstance();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mHotWallpaperBtn = (RelativeLayout) findViewById(R.id.rl_hot);
		mHotWallpaperBtn.setOnClickListener(this);
		mLocalWallpaperBtn = (RelativeLayout) findViewById(R.id.rl_local);
		mLocalWallpaperBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_hot:
			final String TAG_WALLPAPER = "tagWallpaper";
			final String TAG_STRING = "currentTab";
			Intent i = new Intent();
			i.setComponent(new ComponentName(mLauncher,
					"com.coco.theme.themebox.MainActivity"));

			i.putExtra(TAG_STRING, TAG_WALLPAPER);
			i.putExtra("selcetHot", true);
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
