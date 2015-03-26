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
public class IconsActivity extends Activity implements View.OnClickListener {
	RelativeLayout ds_rl_iconsback;
	RelativeLayout ds_rl_iconsbig;
	RelativeLayout ds_rl_iconsmiddle;
	RelativeLayout ds_rl_iconssmall;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ds_iconslayout);
		ds_rl_iconsback = (RelativeLayout) findViewById(R.id.ds_rl_iconsback);
		ds_rl_iconsback.setOnClickListener(this);
		ds_rl_iconsbig = (RelativeLayout) findViewById(R.id.ds_rl_iconsbig);
		ds_rl_iconsbig.setOnClickListener(this);
		ds_rl_iconsmiddle = (RelativeLayout) findViewById(R.id.ds_rl_iconmiddle);
		ds_rl_iconsmiddle.setOnClickListener(this);
		ds_rl_iconssmall = (RelativeLayout) findViewById(R.id.ds_rl_iconsmall);
		ds_rl_iconssmall.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ds_rl_iconsback:
			finish();
			break;
		case R.id.ds_rl_iconsbig:
			finish();
			break;
		case R.id.ds_rl_iconmiddle:
			finish();
			break;
		case R.id.ds_rl_iconsmall:
			finish();
			break;

		default:
			break;
		}
	}

}
