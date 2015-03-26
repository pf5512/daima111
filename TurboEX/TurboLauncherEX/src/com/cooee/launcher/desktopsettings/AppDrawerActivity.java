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
public class AppDrawerActivity extends Activity implements View.OnClickListener {
	RelativeLayout ds_rl_appdrawerback;
	RelativeLayout ds_rl_appdrawereffect;
	RelativeLayout ds_rl_appdrawergridsize;
	RelativeLayout ds_rl_appdrawerinfinite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ds_appdrawerlayout);
		ds_rl_appdrawerback = (RelativeLayout) findViewById(R.id.ds_rl_appdrawerback);
		ds_rl_appdrawerback.setOnClickListener(this);
		ds_rl_appdrawereffect = (RelativeLayout) findViewById(R.id.ds_rl_appdrawereffect);
		ds_rl_appdrawereffect.setOnClickListener(this);
		ds_rl_appdrawergridsize = (RelativeLayout) findViewById(R.id.ds_rl_appdrawergridsize);
		ds_rl_appdrawergridsize.setOnClickListener(this);
		ds_rl_appdrawerinfinite = (RelativeLayout) findViewById(R.id.ds_rl_appdrawerinfinite);
		ds_rl_appdrawerinfinite.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ds_rl_appdrawerback:
			finish();
			break;
		case R.id.ds_rl_appdrawereffect:
			break;
		case R.id.ds_rl_appdrawergridsize:
			break;
		case R.id.ds_rl_appdrawerinfinite:
			break;

		default:
			break;
		}

	}
}
