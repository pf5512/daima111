package com.cooee.launcher.desktopedit;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.cooee.launcher.Launcher;
import com.cooeeui.brand.turbolauncher.R;

/**
 * 桌面编辑模块代码
 * 
 * @author LinYu
 * 
 */
public class DesktopEditModeTabHost extends TabHost implements
		TabHost.OnTabChangeListener {

	private static final String TAG = "DesktopEditModeTabHost";

	private static final String TAB_TAG_ADD = "ADD";
	private static final String TAB_TAG_THEME = "THEME";
	private static final String TAB_TAG_WALLPAPER = "WALLPAPER";
	private static final String TAB_TAG_EFFECT = "EFFECT";

	private Context mContext;
	private Launcher mLauncher;
	private final LayoutInflater mLayoutInflater;
	private ViewGroup mTabs;
	private ViewGroup mTabsContainer;
	private LinearLayout mContent;
	private TabSelecteLine mTabSelecteLine;
	private boolean mInTransition;

	public DesktopEditModeTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mLauncher = Launcher.getInstance();
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public void onTabChanged(String tabId) {
		Log.i(TAG, "tabId : " + tabId);
		if (tabId.equals(TAB_TAG_ADD)) {
			mTabSelecteLine.onTabChange(0);
		} else if (tabId.equals(TAB_TAG_THEME)) {
			mTabSelecteLine.onTabChange(1);
		} else if (tabId.equals(TAB_TAG_WALLPAPER)) {
			mTabSelecteLine.onTabChange(2);
		}
	}

	@Override
	protected void onFinishInflate() {
		setup();
		mTabSelecteLine = (TabSelecteLine) findViewById(R.id.tsl);
		final ViewGroup tabsContainer = (ViewGroup) findViewById(R.id.tabs_container);
		final TabWidget tabs = getTabWidget();
		final TabAddContent tabAddContent = (TabAddContent) findViewById(R.id.ll_tab_add);
		final TabThemeContent tabThemeContent = (TabThemeContent) findViewById(R.id.fl_tab_theme);
		final TabWallpaperContent tabWallpaperContent = (TabWallpaperContent) findViewById(R.id.fl_tab_wallpaper);

		// final TabEffectContent tabEffectContent = (TabEffectContent)
		// findViewById(R.id.fl_tab_effect);
		mTabs = tabs;
		mTabsContainer = tabsContainer;
		mContent = (LinearLayout) findViewById(R.id.ll_content);
		if (tabs == null) {
			throw new Resources.NotFoundException();
		}
		TabContentFactory contentFactory = new TabContentFactory() {

			@Override
			public View createTabContent(String tag) {
				if (tag.equals(TAB_TAG_ADD)) {
					return tabAddContent;
				} else if (tag.equals(TAB_TAG_THEME)) {
					return tabThemeContent;
				} else if (tag.equals(TAB_TAG_WALLPAPER)) {
					return tabWallpaperContent;
				}
				// else if (tag.equals(TAB_TAG_EFFECT)) {
				// return tabEffectContent;
				// }
				else {
					return new TextView(getContext());
				}

			}
		};

		TextView tabView;
		String label;
		label = getContext().getString(R.string.de_tab_title_add);
		tabView = (TextView) mLayoutInflater.inflate(R.layout.de_tab_view,
				tabs, false);
		tabView.setText(label);
		tabView.setContentDescription(label);
		addTab(newTabSpec(TAB_TAG_ADD).setIndicator(tabView).setContent(
				contentFactory));
		label = getContext().getString(R.string.de_tab_title_theme);
		tabView = (TextView) mLayoutInflater.inflate(
				R.layout.tab_widget_indicator, tabs, false);
		tabView.setText(label);
		tabView.setContentDescription(label);
		addTab(newTabSpec(TAB_TAG_THEME).setIndicator(tabView).setContent(
				contentFactory));
		label = getContext().getString(R.string.de_tab_title_wallpaper);
		tabView = (TextView) mLayoutInflater.inflate(
				R.layout.tab_widget_indicator, tabs, false);
		tabView.setText(label);
		tabView.setContentDescription(label);
		addTab(newTabSpec(TAB_TAG_WALLPAPER).setIndicator(tabView).setContent(
				contentFactory));
		for (int i = 0; i < tabs.getChildCount(); i++) {
			tabs.getChildAt(i).setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT, 1f));
		}
		setOnTabChangedListener(this);
	}
}
