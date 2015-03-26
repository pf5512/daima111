package com.cooee.launcher.desktopsettings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooee.launcher.Launcher;
import com.cooeeui.brand.turbolauncher.R;
import com.umeng.fb.ConversationActivity;

/**
 * 桌面设置模块代码
 * 
 * @author liangxiaoling
 * 
 */
public class AboutActivity extends Activity implements View.OnClickListener {

	String qqGroupKey = "RDorNY7zvFJjUvbxj41y9pgkSx4DigUQ";
	RelativeLayout llBack = null;
	LinearLayout llCheckUpdates = null;
	LinearLayout llSocial = null;
	LinearLayout llContact = null;
	LinearLayout llFeedback = null;
	LinearLayout llPrivacyNotice = null;
	public static boolean isAllowOnclick = true;
	TextView versionTextView;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ds_aboutlayout);
		llBack = (RelativeLayout) findViewById(R.id.ds_rl_aboutback);
		llBack.setOnClickListener(this);

		String appVersionName = getAppVersionName(Launcher.getInstance());
		versionTextView = (TextView) findViewById(R.id.dsabout_id_launcher_version_cn);
		versionTextView.setText(appVersionName);

		llSocial = (LinearLayout) findViewById(R.id.dsabout_button_social);
		llSocial.setOnClickListener(this);
		llContact = (LinearLayout) findViewById(R.id.dsabout_button_contact);
		llContact.setOnClickListener(this);
		llFeedback = (LinearLayout) findViewById(R.id.dsabout_button_feedback);
		llFeedback.setOnClickListener(this);
		llPrivacyNotice = (LinearLayout) findViewById(R.id.dsabout_button_privacy_notice);
		llPrivacyNotice.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ds_rl_aboutback:
			finish();
			break;
		case R.id.dsabout_button_social:
			social();
			break;
		case R.id.dsabout_button_contact:
			contact();
			break;
		case R.id.dsabout_button_feedback:
			startActivityForId(ConversationActivity.class);
			break;
		case R.id.dsabout_button_privacy_notice:
			startActivityForId(PrivacyNoticeActivity.class);
			break;
		default:
			break;
		}
	}

	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return versionName;
	}

	private void startActivityForId(Class<?> cls) {
		Intent in = new Intent(AboutActivity.this, cls);
		startActivity(in);
	}

	/****************
	 * 
	 * 发起添加群流程。群号：Turbo桌面官方群(223934516) 的 key 为：
	 * RDorNY7zvFJjUvbxj41y9pgkSx4DigUQ 调用
	 * joinQQGroup(RDorNY7zvFJjUvbxj41y9pgkSx4DigUQ) 即可发起手Q客户端申请加群
	 * Turbo桌面官方群(223934516)
	 * 
	 * @param key
	 *            由官网生成的key
	 * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
	 ******************/
	public boolean joinQQGroup(String key) {
		Intent intent = new Intent();
		intent.setData(Uri
				.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D"
						+ key));
		// 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
		// //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		try {
			startActivity(intent);
			return true;
		} catch (Exception e) {
			// 未安装手Q或安装的版本不支持
			String str = getResources().getText(
					R.string.ds_about_qq_installation_alert).toString();
			if (toast != null) {
				toast.setText(str);
			} else {
				toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
			}
			toast.show();
			return false;
		}
	}

	public void contact() {
		// if (!DefaultLayout.enable_google_version) {
		joinQQGroup(qqGroupKey);
		// } else {
		// String[] receiver = new String[] { "turbolauncher@cooee.cn" };
		// String subject = "Feedback on Turbo Launcher";
		// String content = "";
		// Intent email = new Intent(Intent.ACTION_SEND);
		// email.setType("message/rfc822");
		// // 设置邮件发收人
		// email.putExtra(Intent.EXTRA_EMAIL, receiver);
		// // 设置邮件标题
		// email.putExtra(Intent.EXTRA_SUBJECT, subject);
		// // 设置邮件内容
		// email.putExtra(Intent.EXTRA_TEXT, content);
		// // 调用系统的邮件系统
		// startActivity(Intent.createChooser(email, "Please select."));
		// }
	}

	public void social() {
		// if (!DefaultLayout.enable_google_version) {
		final Uri uri = Uri.parse("http://weibo.com/turboui");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		// } else {
		// final Uri uri = Uri.parse("http://www.facebook.com/turbolauncher");
		// Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(intent);
		// }
	}

}
