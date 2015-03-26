package com.iLoong.launcher.clean;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.UI3DEngine.Utils3D;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 此类为安装清理大师时弹出的dialog类 是在没有安装闪电清理大师的情况下，点击了桌面上的清理插件后弹出的安装提示对话框
 * 
 * @author Administrator
 * 
 */
public class InstallCleanMasterActivity extends Activity implements
		OnClickListener {

	private ImageView iv_install_cleanmaster_icon;// 清理大师icon
	private TextView tv_install_cleanmaster_title;// 清理大师称谓-----闪电清理大师
	private TextView tv_install_cleanmaster_message;// 清理大师口号-----一键清理您的手机
	private ImageButton btn_install_cleanmaster_download;// 安装按钮
	private RelativeLayout rl_install_cleanmaster_close;// 不安装关闭窗口按钮
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_instal_cleanmasterl_show);
		init();
	}

	/**
	 * 初始化函数
	 */
	private void init() {
		iv_install_cleanmaster_icon = (ImageView) findViewById(R.id.iv_install_cleanmaster_icon);
		tv_install_cleanmaster_title = (TextView) findViewById(R.id.tv_install_cleanmaster_title);
		tv_install_cleanmaster_message = (TextView) findViewById(R.id.tv_install_cleanmaster_message);
		btn_install_cleanmaster_download = (ImageButton) findViewById(R.id.btn_install_cleanmaster);
		rl_install_cleanmaster_close = (RelativeLayout) findViewById(R.id.rl_install_cleanmaster_close);
		iv_install_cleanmaster_icon
				.setImageResource(R.drawable.icon_install_clean_master_72);
		tv_install_cleanmaster_title
				.setText(R.string.install_cleanmaster_title);
		tv_install_cleanmaster_message
				.setText(R.string.install_cleanmaster_message);
		btn_install_cleanmaster_download.setOnClickListener(this);
		rl_install_cleanmaster_close.setOnClickListener(this);

	}

	/**
	 * 添加监听事件函数
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_install_cleanmaster:
			Utils3D.installAssetsAPK(InstallCleanMasterActivity.this,
					"CleanMaster.apk");
			finish();
			break;
		case R.id.rl_install_cleanmaster_close:
			finish();
			break;
		}
	}

}
