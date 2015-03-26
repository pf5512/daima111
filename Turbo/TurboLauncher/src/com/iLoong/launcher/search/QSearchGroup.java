package com.iLoong.launcher.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coco.theme.themebox.util.Tools;
import com.cooee.android.launcher.framework.LauncherModel;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.HotSeat3D.HotDockGroup;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.action.ActionHolder;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.FolderInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class QSearchGroup extends ViewGroup3D {

	private GuessGroup mGuessGroup;
	public AllAppGroup mAllAppGroup;
	private LettersGroup mLettersGroup;
	public static SearchEditTextGroup mSearchEditTextGroup;
	public SearchResultGroup mSearchResultGroup;
	public View3D mTopFill;
	public View3D mButtonFill;
	public View3D mLettersGroupButtomFill;
	public PopGroup mPopGroup;
	private boolean mLock = false;
	public static boolean mLoaded;
	private Timeline qs_timeline = null;
	public Timeline qs_pop_timeline = null;
	private Timeline qs_reset_timeline = null;
	public static boolean canShow = true;
	private Tween searchEditorAni;
	public static boolean sNeedChange = false;

	public QSearchGroup(String name) {
		super(name);
		setSize(QsearchConstants.W_QUICK_SEARCH,
				QsearchConstants.H_QUICK_SEARCH);
		initView();
	}

	private void initView() {
		try {
			Bitmap topFillBmp = Tools.getImageFromInStream(iLoongLauncher
					.getInstance().getAssets()
					.open("theme/quick_search/qs_guess_bg.png"));
			NinePatch topFillNp = new NinePatch(new BitmapTexture(topFillBmp,
					true), 1, 1, 1, 1);
			mTopFill = new View3D("topFill");
			mTopFill.setBackgroud(topFillNp);
			mTopFill.setSize(QsearchConstants.W_QUICK_SEARCH_FILL_TOP,
					QsearchConstants.H_QUICK_SEARCH_FILL_TOP);
			mTopFill.setPosition(0, UtilsBase.getScreenHeight()
					- mTopFill.height);
			Bitmap bottomFillBmp = Tools.getImageFromInStream(iLoongLauncher
					.getInstance().getAssets()
					.open("theme/quick_search/search_edit_bg.png"));
			NinePatch bottomFillNp = new NinePatch(new BitmapTexture(
					bottomFillBmp, true), 1, 1, 0, 0);
			mButtonFill = new View3D("bottomFill");
			mButtonFill.setBackgroud(bottomFillNp);
			mButtonFill.setSize(QsearchConstants.W_SEARCH_EDIT_GROUP,
					QsearchConstants.H_SEARCH_EDIT_GROUP);
			mGuessGroup = new GuessGroup("guessGroup");
			mSearchEditTextGroup = new SearchEditTextGroup(
					"searchEditTextGroup");
			mLettersGroup = new LettersGroup("lettersGroup");
			mAllAppGroup = new AllAppGroup("allAppGroup");
			mSearchResultGroup = new SearchResultGroup("searchResultGroup");
			mPopGroup = new PopGroup("popGroup");
			Bitmap mLettersGroupButtomFillBmp = Tools
					.getImageFromInStream(iLoongLauncher.getInstance()
							.getAssets()
							.open("theme/quick_search/qs_letter_bg.png"));
			NinePatch mLettersGroupButtomFillNp = new NinePatch(
					new BitmapTexture(mLettersGroupButtomFillBmp, true), 1, 1,
					1, 1);
			mLettersGroupButtomFill = new View3D("lettersGroupButtomFill");
			mLettersGroupButtomFill.setBackgroud(mLettersGroupButtomFillNp);
			mLettersGroupButtomFill.setSize(40 * QsearchConstants.S_SCALE,
					140 * QsearchConstants.S_SCALE);
			mLettersGroupButtomFill.setPosition(UtilsBase.getScreenWidth()
					- mLettersGroupButtomFill.width, 0);
			addView(mGuessGroup);
			addView(mAllAppGroup);
			addView(mLettersGroup);
			addView(mLettersGroupButtomFill);
			addView(mSearchResultGroup);
			addView(mButtonFill);
			addView(mSearchEditTextGroup);
			addView(mTopFill);
			addView(mPopGroup);
			resetPostion();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void disposeAllViews(View3D view) {
		if (view instanceof ViewGroup3D) {
			ViewGroup3D vg = (ViewGroup3D) view;
			for (int i = 0; i < vg.getChildCount(); i++) {
				disposeAllViews(vg.getChildAt(i));
			}
		} else {
			view.dispose();
		}
	}

	@Override
	public boolean onLongClick(float x, float y) {
		return true;
	}

	public void release() {
		mAllAppGroup.release();
	}

	public void doAnimPrepare() {
		mTopFill.setPosition(0, -mTopFill.height);
		mGuessGroup.setPosition(0, -mGuessGroup.height);
		mGuessGroup.mGuessPage.indicatorView
				.setPosition(0, -mGuessGroup.height);
		mAllAppGroup.setPosition(0, -UtilsBase.getScreenHeight());
		mLettersGroup.setPosition(UtilsBase.getScreenWidth()
				- mLettersGroup.width,
				-(mGuessGroup.height + mLettersGroup.height));
		mLettersGroupButtomFill.setPosition(UtilsBase.getScreenWidth()
				- mLettersGroupButtomFill.width, -UtilsBase.getScreenHeight());
		mSearchResultGroup.setPosition(0, -mSearchResultGroup.height);
		mButtonFill.setPosition(0, -UtilsBase.getScreenHeight());
	}

	public void resetPostion() {
		if (!isVisible()) {
			return;
		}
		if (qs_reset_timeline != null) {
			qs_reset_timeline.free();
			qs_reset_timeline = null;
		}
		if (qs_timeline != null) {
			qs_timeline.free();
			qs_timeline = null;
		}
		mTopFill.setPosition(0, -mTopFill.height);
		mSearchEditTextGroup.setPosition(UtilsBase.getScreenWidth(), 0);
		mGuessGroup.setPosition(0, -mGuessGroup.height);
		mGuessGroup.mGuessPage.indicatorView
				.setPosition(0, -mGuessGroup.height);
		mAllAppGroup.setPosition(0, -UtilsBase.getScreenHeight());
		mLettersGroup.setPosition(UtilsBase.getScreenWidth()
				- mLettersGroup.width,
				-(mGuessGroup.height + mLettersGroup.height));
		mLettersGroupButtomFill.setPosition(UtilsBase.getScreenWidth()
				- mLettersGroupButtomFill.width, -UtilsBase.getScreenHeight());
		mSearchResultGroup.setPosition(0, -mSearchResultGroup.height);
		mButtonFill.setPosition(0, -UtilsBase.getScreenHeight());
		Root3D.hotseatBar.setPosition(0, 0);
		mSearchEditTextGroup.resetPostion();
		Root3D.hotseatBar.dockGroup.backgroud.setPosition(0, 0);
		iLoongLauncher.getInstance().d3dListener.workspace.color.a = 1;
		Root3D.hotseatBar.color.a = 1;
		Desktop3DListener.root.pageIndicator.color.a = 1;
		mGuessGroup.mGuessPage.indicatorView.color.a = 1;
		mGuessGroup.color.a = 1;
		mAllAppGroup.color.a = 1;
		mLettersGroupButtomFill.color.a = 1;
		mButtonFill.color.a = 1;
		mTopFill.color.a = 1;
		mLettersGroup.color.a = 1;
		hide();
		if (DefaultLayout.enable_news
				&& Desktop3DListener.root.newsHandle != null) {
			if (DefaultLayout.show_newspage_with_handle) {
				Desktop3DListener.root.show();
				Messenger.sendMsg(Messenger.MSG_SHOW_NEWSVIEW_HANDLE, 0);
			}
		}
		mLock = false;
	}

	public void startPopFadeOutAnim() {
		if (qs_pop_timeline != null) {
			qs_pop_timeline.free();
			qs_pop_timeline = null;
		}
		qs_pop_timeline = Timeline.createParallel();
		float duration = 0.5f;
		qs_pop_timeline.push(Tween.to(mPopGroup, View3DTweenAccessor.OPACITY,
				duration).target(0));
		qs_pop_timeline.start(View3DTweenAccessor.manager).setCallback(this);
	}

	public void startResetAnim(boolean outside) {
		if (mLock) {
			return;
		}
		if (qs_reset_timeline != null) {
			qs_reset_timeline.free();
			qs_reset_timeline = null;
		}
		qs_reset_timeline = Timeline.createParallel();
		TweenEquation equation = Cubic.OUT;
		float duration = 0.5f;
		if (outside) {
			qs_reset_timeline.push(Tween
					.to(Root3D.hotseatBar.dockGroup.backgroud,
							View3DTweenAccessor.POS_XY, duration).target(0, 0)
					.ease(equation));
			qs_reset_timeline.push(Tween
					.to(mSearchEditTextGroup, View3DTweenAccessor.POS_XY,
							duration).target(UtilsBase.getScreenWidth(), 0)
					.ease(equation));
			qs_reset_timeline
					.push(Tween
							.to(Root3D.hotseatBar, View3DTweenAccessor.POS_XY,
									duration).target(0, 0).ease(equation));
		} else {
			qs_reset_timeline.push(Tween
					.to(Root3D.hotseatBar.dockGroup.backgroud,
							View3DTweenAccessor.POS_XY, duration).target(0, 0)
					.ease(equation));
			qs_reset_timeline.push(Tween
					.to(mSearchEditTextGroup, View3DTweenAccessor.POS_XY,
							duration).target(0, 0).ease(equation));
			qs_reset_timeline
					.push(Tween
							.to(Root3D.hotseatBar, View3DTweenAccessor.POS_XY,
									duration)
							.target(-UtilsBase.getScreenWidth(), 0)
							.ease(equation));
		}
		qs_reset_timeline.start(View3DTweenAccessor.manager).setCallback(this);
		mLock = true;
	}

	public void searchEditAni() {
		float editGroupDuration = 0.5f;
		TweenEquation equation = Cubic.OUT;
		searchEditorAni = Tween
				.to(mSearchEditTextGroup, View3DTweenAccessor.POS_XY,
						editGroupDuration).target(0, 0).ease(equation)
				.setCallback(this).start(View3DTweenAccessor.manager);
		Tween.to(Root3D.hotseatBar.dockGroup.backgroud,
				View3DTweenAccessor.POS_XY, editGroupDuration)
				.target(UtilsBase.getScreenWidth(), 0).ease(equation)
				.start(View3DTweenAccessor.manager);
		Tween.to(Root3D.hotseatBar, View3DTweenAccessor.POS_XY,
				editGroupDuration).target(-UtilsBase.getScreenWidth(), 0)
				.ease(equation).start(View3DTweenAccessor.manager);
	}

	public void startQuickSearchAnimIn() {
		if (mLock) {
			return;
		}
		this.addView(mSearchEditTextGroup);
		if (qs_timeline != null) {
			qs_timeline.free();
			qs_timeline = null;
		}
		qs_timeline = Timeline.createParallel();
		TweenEquation equation = Cubic.OUT;
		float editGroupDuration = 0.5f;
		float displayGroupDuration = 0.4f;
		qs_timeline.push(Tween
				.to(Root3D.hotseatBar.dockGroup.backgroud,
						View3DTweenAccessor.POS_XY, editGroupDuration)
				.target(UtilsBase.getScreenWidth(), 0).ease(equation));
		qs_timeline.push(Tween
				.to(mSearchEditTextGroup, View3DTweenAccessor.POS_XY,
						editGroupDuration).target(0, 0).ease(equation));
		qs_timeline.push(Tween
				.to(Root3D.hotseatBar, View3DTweenAccessor.POS_XY,
						editGroupDuration)
				.target(-UtilsBase.getScreenWidth(), 0).ease(equation));
		qs_timeline.push(Tween
				.to(mTopFill, View3DTweenAccessor.POS_XY, displayGroupDuration)
				.target(0, UtilsBase.getScreenHeight() - mTopFill.height)
				.ease(equation));
		qs_timeline
				.push(Tween
						.to(mGuessGroup, View3DTweenAccessor.POS_XY,
								displayGroupDuration)
						.target(0,
								QsearchConstants.H_QUICK_SEARCH
										- mGuessGroup.height).ease(equation));
		qs_timeline.push(Tween
				.to(mGuessGroup.mGuessPage.indicatorView,
						View3DTweenAccessor.POS_XY, displayGroupDuration)
				.target(0,
						QsearchConstants.H_QUICK_SEARCH
								- QsearchConstants.H_GUESS_GROUP)
				.ease(equation));
		qs_timeline.push(Tween
				.to(mLettersGroup, View3DTweenAccessor.POS_XY,
						displayGroupDuration)
				.target(UtilsBase.getScreenWidth() - mLettersGroup.width,
						QsearchConstants.H_QUICK_SEARCH - mGuessGroup.height
								- mLettersGroup.height).ease(equation));
		qs_timeline.push(Tween
				.to(mLettersGroupButtomFill, View3DTweenAccessor.POS_XY,
						displayGroupDuration)
				.target(UtilsBase.getScreenWidth()
						- mLettersGroupButtomFill.width, 0).ease(equation));
		qs_timeline.push(Tween
				.to(mAllAppGroup, View3DTweenAccessor.POS_XY,
						displayGroupDuration)
				.target(0,
						QsearchConstants.H_QUICK_SEARCH - mGuessGroup.height
								- mAllAppGroup.height).ease(equation));
		qs_timeline.push(Tween
				.to(mButtonFill, View3DTweenAccessor.POS_XY,
						displayGroupDuration).target(0, 0).ease(equation));
		qs_timeline.push(Tween
				.to(Root3D.hotseatBar, View3DTweenAccessor.OPACITY,
						editGroupDuration).target(0).ease(equation));
		qs_timeline.push(Tween
				.to(Desktop3DListener.root.pageIndicator,
						View3DTweenAccessor.OPACITY, editGroupDuration)
				.target(0).ease(equation));
		qs_timeline.push(Tween
				.to(iLoongLauncher.getInstance().d3dListener.workspace,
						View3DTweenAccessor.OPACITY, editGroupDuration)
				.target(0).ease(equation));
		qs_timeline.start(View3DTweenAccessor.manager).setCallback(this);
		mLock = true;
	}

	public void startQuickSearchAnimQuit(boolean manual) {
		if (mLock) {
			return;
		}
		if (qs_timeline != null) {
			qs_timeline.free();
			qs_timeline = null;
		}
		qs_timeline = Timeline.createParallel();
		TweenEquation equation = Cubic.OUT;
		float editGroupDuration = 0.5f;
		float displayGroupDuration = 0.3f;
		if (manual) {
			qs_timeline.push(Tween
					.to(mSearchEditTextGroup, View3DTweenAccessor.POS_XY,
							editGroupDuration)
					.target(UtilsBase.getScreenWidth(), 0).ease(equation));
			qs_timeline.push(Tween
					.to(Root3D.hotseatBar, View3DTweenAccessor.POS_XY,
							editGroupDuration).target(0, 0).ease(equation));
			qs_timeline.push(Tween
					.to(mTopFill, View3DTweenAccessor.POS_XY,
							displayGroupDuration).target(0, -mTopFill.height)
					.ease(equation));
			qs_timeline.push(Tween
					.to(mGuessGroup, View3DTweenAccessor.POS_XY,
							displayGroupDuration)
					.target(0, -mGuessGroup.height).ease(equation));
			qs_timeline.push(Tween
					.to(mGuessGroup.mGuessPage.indicatorView,
							View3DTweenAccessor.POS_XY, displayGroupDuration)
					.target(0, -mGuessGroup.height).ease(equation));
			qs_timeline.push(Tween
					.to(mLettersGroup, View3DTweenAccessor.POS_XY,
							displayGroupDuration)
					.target(UtilsBase.getScreenWidth() - mLettersGroup.width,
							-(mGuessGroup.height + mLettersGroup.height))
					.ease(equation));
			qs_timeline.push(Tween
					.to(mLettersGroupButtomFill, View3DTweenAccessor.POS_XY,
							displayGroupDuration)
					.target(UtilsBase.getScreenWidth()
							- mLettersGroupButtomFill.width,
							-UtilsBase.getScreenHeight()).ease(equation));
			qs_timeline.push(Tween
					.to(mAllAppGroup, View3DTweenAccessor.POS_XY,
							displayGroupDuration)
					.target(0, -(mGuessGroup.height + mAllAppGroup.height))
					.ease(equation));
			qs_timeline.push(Tween
					.to(mButtonFill, View3DTweenAccessor.POS_XY,
							displayGroupDuration)
					.target(0, -UtilsBase.getScreenHeight()).ease(equation));
			qs_timeline.push(Tween
					.to(Root3D.hotseatBar.dockGroup.backgroud,
							View3DTweenAccessor.POS_XY, editGroupDuration)
					.target(0, 0).ease(equation));
		} else {
			qs_timeline.push(Tween
					.to(mTopFill, View3DTweenAccessor.POS_XY,
							displayGroupDuration).target(0, -mTopFill.height)
					.ease(equation));
			qs_timeline.push(Tween
					.to(mGuessGroup, View3DTweenAccessor.POS_XY,
							displayGroupDuration)
					.target(0, -mGuessGroup.height).ease(equation));
			qs_timeline.push(Tween
					.to(mGuessGroup.mGuessPage.indicatorView,
							View3DTweenAccessor.POS_XY, displayGroupDuration)
					.target(0, -mGuessGroup.height).ease(equation));
			qs_timeline.push(Tween
					.to(mLettersGroup, View3DTweenAccessor.POS_XY,
							displayGroupDuration)
					.target(UtilsBase.getScreenWidth() - mLettersGroup.width,
							-(mGuessGroup.height + mLettersGroup.height))
					.ease(equation));
			qs_timeline.push(Tween
					.to(mLettersGroupButtomFill, View3DTweenAccessor.POS_XY,
							displayGroupDuration)
					.target(UtilsBase.getScreenWidth()
							- mLettersGroupButtomFill.width,
							-UtilsBase.getScreenHeight()).ease(equation));
			qs_timeline.push(Tween
					.to(mAllAppGroup, View3DTweenAccessor.POS_XY,
							displayGroupDuration)
					.target(0, -(mGuessGroup.height + mAllAppGroup.height))
					.ease(equation));
			qs_timeline.push(Tween
					.to(mButtonFill, View3DTweenAccessor.POS_XY,
							displayGroupDuration)
					.target(0, -UtilsBase.getScreenHeight()).ease(equation));
			qs_timeline.push(Tween
					.to(mSearchEditTextGroup, View3DTweenAccessor.POS_XY,
							editGroupDuration)
					.target(UtilsBase.getScreenWidth(), 0).ease(equation)
					.delay(displayGroupDuration));
			qs_timeline.push(Tween
					.to(Root3D.hotseatBar, View3DTweenAccessor.POS_XY,
							editGroupDuration).target(0, 0).ease(equation)
					.delay(displayGroupDuration));
			qs_timeline.push(Tween
					.to(Root3D.hotseatBar.dockGroup.backgroud,
							View3DTweenAccessor.POS_XY, editGroupDuration)
					.target(0, 0).ease(equation).delay(displayGroupDuration));
		}
		qs_timeline.push(Tween
				.to(Root3D.hotseatBar, View3DTweenAccessor.OPACITY,
						editGroupDuration).target(1).ease(equation));
		qs_timeline.push(Tween
				.to(Desktop3DListener.root.pageIndicator,
						View3DTweenAccessor.OPACITY, editGroupDuration)
				.target(1).ease(equation));
		qs_timeline.push(Tween
				.to(iLoongLauncher.getInstance().d3dListener.workspace,
						View3DTweenAccessor.OPACITY, editGroupDuration)
				.target(1).ease(equation));
		qs_timeline.start(View3DTweenAccessor.manager).setCallback(this);
		mLock = true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onEvent(int type, BaseTween source) {
		if (source.equals(qs_timeline) && type == TweenCallback.COMPLETE) {
			if (mTopFill.y > 0) {
				Messenger.sendMsg(Messenger.MSG_HIDE_NEWSVIEW_HANDLE, null);
				if (ActionHolder.getInstance() != null)
					ActionHolder.getInstance().onQuickSearchStarted();
				mGuessGroup.mGuessPage.load();
				if (QSearchGroup.sNeedChange) {
					reLoad();
					QSearchGroup.sNeedChange = false;
				}
			} else {
				hide();
				Messenger.sendMsg(Messenger.MSG_SHOW_NEWSVIEW_HANDLE, null);
			}
			mLock = false;
		} else if (source.equals(qs_reset_timeline)
				&& type == TweenCallback.COMPLETE) {
			if (mSearchEditTextGroup.x != 0) {
				hide();
			}
			mLock = false;
		} else if (source.equals(searchEditorAni)
				&& type == TweenCallback.COMPLETE) {
			if (LauncherModel.appListLoaded) {
				load();
				Messenger.sendMsg(Messenger.MSG_HIDE_NEWSVIEW_HANDLE, null);
				if (ActionHolder.getInstance() != null)
					ActionHolder.getInstance().onQuickSearchStarted();
				mGuessGroup.mGuessPage.load();
				SendMsgToAndroid.cancelCustomDialog();
				HotDockGroup.dialogBg.region.getTexture().dispose();
				HotDockGroup.dialogBg.remove();
				this.addView(mSearchEditTextGroup);
				startQuickSearchAnimIn();
			} else {
				iLoongApplication.getInstance().getModel().loadAppList();
				iLoongLauncher.getInstance().postRunnable(new Runnable() {

					@Override
					public void run() {
						load();
						Messenger.sendMsg(Messenger.MSG_HIDE_NEWSVIEW_HANDLE,
								null);
						if (ActionHolder.getInstance() != null)
							ActionHolder.getInstance().onQuickSearchStarted();
						mGuessGroup.mGuessPage.load();
						SendMsgToAndroid.cancelCustomDialog();
						HotDockGroup.dialogBg.region.getTexture().dispose();
						HotDockGroup.dialogBg.remove();
						startQuickSearchAnimIn();
					}
				});
			}
		}
		super.onEvent(type, source);
	}

	public GuessGroup getGuessGroup() {
		return mGuessGroup;
	}

	public LettersGroup getLettersGroup() {
		return mLettersGroup;
	}

	public SearchEditTextGroup getSearchEditTextGroup() {
		return mSearchEditTextGroup;
	}

	public boolean isLock() {
		return mLock;
	}

	public void load() {
		if (!mLoaded) {
			mGuessGroup.load();
			mAllAppGroup.load();
			mSearchResultGroup.load();
			mLoaded = true;
		}
	}

	public void reLoad() {
		mGuessGroup.reLoad();
		mAllAppGroup.reLoad();
		mSearchResultGroup.reLoad();
	}

	public static List<SearchApp> getAllApp() {
		List<SearchApp> allApps = new ArrayList<SearchApp>();
		SearchApp cooeeicon = null;
		if (AppHost3D.appList.mApps != null) {
			if (AppHost3D.appList.mApps.size() == 0) {
				if (AppHost3D.appList.mItemInfos != null
						&& AppHost3D.appList.mItemInfos.size() > 0) {
					for (int i = 0; i < AppHost3D.appList.mItemInfos.size(); i++) {
						if (AppHost3D.appList.mItemInfos.get(i) instanceof ApplicationInfo) {
							ApplicationInfo info = (ApplicationInfo) AppHost3D.appList.mItemInfos
									.get(i);
							if (info.intent.getComponent() != null) {
								ShortcutInfo sInfo = info.makeShortcut();
								String appName = R3D.getInfoName(sInfo);
								Icon3D icon = AppHost3D.appList.iconMap
										.get(appName);
								if (icon != null && !icon.getHideStatus()) {
									cooeeicon = new SearchApp(icon.name,
											icon.region);
									cooeeicon.setItemInfo(new ShortcutInfo(
											(ShortcutInfo) icon.getItemInfo()));
									cooeeicon.setSize(icon.getWidth(),
											icon.getHeight());
									allApps.add(cooeeicon);
								}
							}
						} else if (AppHost3D.appList.mItemInfos.get(i) instanceof FolderInfo) {
							FolderInfo info = (FolderInfo) AppHost3D.appList.mItemInfos
									.get(i);
							ArrayList<ShortcutInfo> contents = ((UserFolderInfo) info).contents;
							if (contents != null && contents.size() > 0) {
								for (int j = 0; j < contents.size(); j++) {
									ShortcutInfo sInfo = contents.get(j);
									cooeeicon = new SearchApp(
											sInfo.title.toString(),
											R3D.findRegion(sInfo));
									cooeeicon.setSize(R3D.workspace_cell_width,
											R3D.workspace_cell_height);
									cooeeicon.setItemInfo(sInfo);
									allApps.add(cooeeicon);
								}
							}
						}
					}
				}
			} else {
				for (int i = 0; i < AppHost3D.appList.mApps.size(); i++) {
					ApplicationInfo info = AppHost3D.appList.mApps.get(i);
					if (info.intent.getComponent() != null) {
						ShortcutInfo sInfo = info.makeShortcut();
						String appName = R3D.getInfoName(sInfo);
						Icon3D icon = AppHost3D.appList.iconMap.get(appName);
						if (icon != null && !icon.getHideStatus()) {
							cooeeicon = new SearchApp(icon.name, icon.region);
							cooeeicon.setItemInfo(new ShortcutInfo(
									(ShortcutInfo) icon.getItemInfo()));
							cooeeicon
									.setSize(icon.getWidth(), icon.getHeight());
							allApps.add(cooeeicon);
						}
					}
				}
			}
		}
		return allApps;
	}

	class PopGroup extends ViewGroup3D {

		private View3D mBg;
		private View3D mLetter;
		private ArrayList<TextureRegion> mLetterRegions;

		public PopGroup(String name) {
			super(name);
			try {
				initLetterRegions();
				setSize(QsearchConstants.W_LETTER_POP,
						QsearchConstants.H_LETTER_POP);
				setPosition(
						(UtilsBase.getScreenWidth() - QsearchConstants.W_LETTER_POP) / 2,
						(UtilsBase.getScreenHeight() - QsearchConstants.H_LETTER_POP) / 2);
				Bitmap bgBmp = Tools.getImageFromInStream(iLoongLauncher
						.getInstance().getAssets()
						.open("theme/quick_search/qs_pop.png"));
				mBg = new View3D("bg", new BitmapTexture(bgBmp, true));
				mBg.setSize(QsearchConstants.W_LETTER_POP,
						QsearchConstants.H_LETTER_POP);
				mLetter = new View3D("letter");
				mLetter.setSize(width, height);
				mLetter.region = mLetterRegions.get(LettersGroup.b.length - 1);
				addView(mBg);
				addView(mLetter);
				this.color.a = 0;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void initLetterRegions() {
			mLetterRegions = new ArrayList<TextureRegion>();
			for (int i = 0; i < LettersGroup.b.length; i++) {
				mLetterRegions.add(drawNameTextureRegion(LettersGroup.b[i],
						QsearchConstants.W_LETTER_POP,
						QsearchConstants.H_LETTER_POP));
			}
		}

		public View3D getLetter() {
			return mLetter;
		}

		public ArrayList<TextureRegion> getLetterRegions() {
			return mLetterRegions;
		}
	}

	public static TextureRegion drawNameTextureRegion(String name,
			final float width, final float height) {
		Bitmap backImage = null;
		backImage = Bitmap.createBitmap((int) width, (int) height,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(backImage);
		canvas.drawColor(Color.TRANSPARENT);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(Color.WHITE);
		paint.setSubpixelText(true);
		paint.setTextSize(QsearchConstants.S_LETTER_POP_TEXT);
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float) Math.ceil(fontMetrics.descent
				- fontMetrics.ascent);
		float posY = backImage.getHeight()
				- (backImage.getHeight() - lineHeight) / 2 - fontMetrics.bottom;
		if (name != null) {
			canvas.drawText(name, (width - paint.measureText(name)) / 2, posY,
					paint);
		}
		TextureRegion newTextureRegion = new TextureRegion(new BitmapTexture(
				backImage));
		if (backImage != null) {
			backImage.recycle();
		}
		return newTextureRegion;
	}

	public void onThemeChanged() {
		reLoad();
	}
}
