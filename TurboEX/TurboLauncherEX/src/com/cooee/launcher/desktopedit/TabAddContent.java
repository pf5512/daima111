package com.cooee.launcher.desktopedit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cooee.launcher.Launcher;
import com.cooeeui.brand.turbolauncher.R;

/**
 * 桌面编辑模块代码
 * 
 * @author LinYu
 * 
 */
public class TabAddContent extends LinearLayout implements View.OnClickListener {

	private Button mBtnWidgets;
	private Button mBtnShortcuts;
	private Button mBtnApps;
	private Button mBtnFolder;

	private Context mContext;

	private Launcher mLauncher;

	public TabAddContent(Context context) {
		this(context, null);
		mContext = context;
		mLauncher = Launcher.getInstance();
	}

	public TabAddContent(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mContext = context;
		mLauncher = Launcher.getInstance();
	}

	public TabAddContent(final Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		mLauncher = Launcher.getInstance();
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		mBtnWidgets = (Button) findViewById(R.id.btn_widgets);
		mBtnWidgets.setOnClickListener(this);
		mBtnShortcuts = (Button) findViewById(R.id.btn_shortcuts);
		mBtnShortcuts.setOnClickListener(this);
		mBtnApps = (Button) findViewById(R.id.btn_apps);
		mBtnApps.setOnClickListener(this);
		mBtnFolder = (Button) findViewById(R.id.btn_folder);
		mBtnFolder.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_widgets:
			Toast.makeText(mContext, "Show all widgets!!!", Toast.LENGTH_SHORT)
					.show();
			mLauncher.getDesktopEditBottomSecondaryLayout().setVisibility(
					View.VISIBLE);
			mLauncher.getDesktopEditModeTabHost().setVisibility(View.GONE);
			break;
		case R.id.btn_shortcuts:
			mLauncher.getDesktopEditBottomSecondaryLayout().setVisibility(
					View.VISIBLE);
			mLauncher.getDesktopEditModeTabHost().setVisibility(View.GONE);
			break;
		case R.id.btn_apps:
			mLauncher.getDesktopEditBottomSecondaryLayout().setVisibility(
					View.VISIBLE);
			mLauncher.getDesktopEditModeTabHost().setVisibility(View.GONE);
			break;
		case R.id.btn_folder:
			mLauncher.getDesktopEditBottomSecondaryLayout().setVisibility(
					View.VISIBLE);
			mLauncher.getDesktopEditModeTabHost().setVisibility(View.GONE);
			break;
		}
	}
}
