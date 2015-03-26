package com.cooee.launcher.desktopsettings;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.cooeeui.brand.turbolauncher.R;

/**
 * 桌面设置模块代码
 * 
 * @author liangxiaoling
 * 
 */
public class ScreenActivity extends Activity implements View.OnClickListener {
	RelativeLayout ds_rl_back;
	RelativeLayout ds_rl_wallpaper;
	RelativeLayout ds_rl_wallpaperscroll;
	RelativeLayout ds_rl_wallpapereffect;
	RelativeLayout ds_rl_wallpaperinfinite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ds_screenlayout);
		ds_rl_back = (RelativeLayout) findViewById(R.id.ds_rl_sceeenback);
		ds_rl_back.setOnClickListener(this);
		ds_rl_wallpaper = (RelativeLayout) findViewById(R.id.ds_rl_wallpaper);
		ds_rl_wallpaper.setOnClickListener(this);
		ds_rl_wallpaperscroll = (RelativeLayout) findViewById(R.id.ds_rl_wallpaperscroll);
		ds_rl_wallpaperscroll.setOnClickListener(this);
		ds_rl_wallpapereffect = (RelativeLayout) findViewById(R.id.ds_rl_wallpapereffect);
		ds_rl_wallpapereffect.setOnClickListener(this);
		ds_rl_wallpaperinfinite = (RelativeLayout) findViewById(R.id.ds_rl_wallpaperinfinite);
		ds_rl_wallpaperinfinite.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ds_rl_sceeenback:
			this.finish();
			break;
		case R.id.ds_rl_wallpaper:

			break;
		case R.id.ds_rl_wallpaperscroll:

			break;
		case R.id.ds_rl_wallpapereffect:

			break;
		case R.id.ds_rl_wallpaperinfinite:

			break;

		default:
			break;
		}
	}
}
