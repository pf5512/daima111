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
public class GestureActivity extends Activity implements View.OnClickListener {

	RelativeLayout ds_rl_gestureback;
	RelativeLayout ds_rl_singledown;
	RelativeLayout ds_rl_singleup;
	RelativeLayout ds_rl_twoup;
	RelativeLayout ds_rl_twodown;
	RelativeLayout ds_rl_twotogether;
	RelativeLayout ds_rl_twoexpand;
	RelativeLayout ds_rl_doubleclick;
	RelativeLayout ds_rl_tworight;
	RelativeLayout ds_rl_twoleft;
	RelativeLayout ds_rl_palm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ds_gesturelayout);
		initViews();
	}

	private void initViews() {
		ds_rl_gestureback = (RelativeLayout) findViewById(R.id.ds_rl_gestureback);
		ds_rl_gestureback.setOnClickListener(this);
		ds_rl_singledown = (RelativeLayout) findViewById(R.id.ds_rl_singlesingerdown);
		ds_rl_singledown.setOnClickListener(this);
		ds_rl_singleup = (RelativeLayout) findViewById(R.id.ds_rl_singlesingerup);
		ds_rl_singleup.setOnClickListener(this);
		ds_rl_twoup = (RelativeLayout) findViewById(R.id.ds_rl_twosingerup);
		ds_rl_twoup.setOnClickListener(this);
		ds_rl_twodown = (RelativeLayout) findViewById(R.id.ds_rl_twosingerdown);
		ds_rl_twodown.setOnClickListener(this);
		ds_rl_twotogether = (RelativeLayout) findViewById(R.id.ds_rl_twofingertogether);
		ds_rl_twotogether.setOnClickListener(this);
		ds_rl_twoexpand = (RelativeLayout) findViewById(R.id.ds_rl_twofingerexpand);
		ds_rl_twoexpand.setOnClickListener(this);
		ds_rl_doubleclick = (RelativeLayout) findViewById(R.id.ds_rl_doubleclick);
		ds_rl_doubleclick.setOnClickListener(this);
		ds_rl_tworight = (RelativeLayout) findViewById(R.id.ds_rl_tworight);
		ds_rl_tworight.setOnClickListener(this);
		ds_rl_twoleft = (RelativeLayout) findViewById(R.id.ds_rl_twoleft);
		ds_rl_twoleft.setOnClickListener(this);
		ds_rl_palm = (RelativeLayout) findViewById(R.id.ds_rl_palm);
		ds_rl_palm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ds_rl_gestureback:
			finish();
			break;
		case R.id.ds_rl_singlesingerdown:

			break;
		case R.id.ds_rl_singlesingerup:

			break;
		case R.id.ds_rl_twosingerup:

			break;
		case R.id.ds_rl_twosingerdown:

			break;
		case R.id.ds_rl_twofingertogether:

			break;
		case R.id.ds_rl_twofingerexpand:

			break;
		case R.id.ds_rl_doubleclick:

			break;
		case R.id.ds_rl_tworight:

			break;
		case R.id.ds_rl_twoleft:

			break;
		case R.id.ds_rl_palm:

			break;
		default:
			break;
		}
	}

}
