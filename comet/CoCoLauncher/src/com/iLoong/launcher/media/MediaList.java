package com.iLoong.launcher.media;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.iLoong.launcher.Desktop3D.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.NPageBase;
import com.iLoong.launcher.Desktop3D.NPageBase.IndicatorView;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.cut;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.MenuActionListener;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.*;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.WidgetShortcutInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.cling.*;

public class MediaList extends NPageBase {
	public static boolean inited = false;
	public static NinePatch translucentBg;
	public static int pageCount = 0;
	private AppBar3D appBar;
	private MediaListAdapter adapter;
	public MediaListAdapter getAdapter() {
		return adapter;
	}

	private MediaListAdapter preAdapter;

	public static HashMap<Integer, String> bg_icon_name = new HashMap<Integer, String>();	//xiatian add	//Mainmenu Bg
	
	public MediaList(String name) {
		super(name);
		x = 0;
		y = 0;
		width = Utils3D.getScreenWidth();
		height = Utils3D.getScreenHeight() - R3D.appbar_height;
		setEffectType(SetupMenuActions.getInstance().getStringToIntger(
				"appeffects"));

//		if (DefaultLayout.mainmenu_addbackgroud
//				|| DefaultLayout.mainmenu_add_black_ground) {
//			translucentBg = new NinePatch(R3D.findRegion("translucent-bg-opa"),
//					3, 3, 3, 3);
//		} else {
//			translucentBg = new NinePatch(R3D.findRegion("translucent-bg"), 3,
//					3, 3, 3);
//		}

		//xiatian start	//Mainmenu Bg
		initBgIconName();	//xiatian add
		//xiatian del start
//		String bg_name = null;
//		if (DefaultLayout.mainmenu_add_black_ground) {
//			bg_name = "theme/pack_source/translucent-black.png";
//		} else if (DefaultLayout.mainmenu_addbackgroud) {
//			bg_name = "theme/pack_source/translucent-bg-opa.png";
//		} else {
//			bg_name = "theme/pack_source/translucent-bg.png";
//		}
//		Bitmap bmp = ThemeManager.getInstance().getBitmap(bg_name);
//		if (bmp.getConfig() != Config.ARGB_8888)
//		{
//			bmp = bmp.copy(Config.ARGB_8888, false);
//		}
//		Texture t = new BitmapTexture(bmp);
////		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//		translucentBg = new NinePatch(new TextureRegion(t),1,1,1,1);
//		bmp.recycle();
		//xiatian del end
		//xiatian end
		
		pageCount = 0;
		indicatorView = new IndicatorView("npage_indicator");
		transform = true;
	}

	@Override
	protected int getIndicatorPageCount() {
		// TODO Auto-generated method stub
		return pageCount;
	}

	@Override
	protected int getIndicatorPageIndex() {
		return page_index;
	}

	public void refresh() {
		if (adapter != null && adapter.curPage != page_index) {
			adapter.curPage = page_index;
			for (int i = 0; i < view_list.size(); i++) {
				adapter.refreshPageItems(view_list.get(i), i, page_index);
			}
		}
	}

	public void clearSelect() {
		if (adapter != null) {
			adapter.curPage = page_index;
			for (int i = 0; i < view_list.size(); i++) {
				adapter.clearSelect(view_list.get(i));
			}
		}
	}

	public void selectAll() {
		if (adapter != null) {
			for (int i = 0; i < view_list.size(); i++) {
				adapter.selectPageItems(view_list.get(i));
			}
		}
	}

	public ArrayList<Uri> getSelectedData() {
		ArrayList<Uri> data = new ArrayList<Uri>();
		if (adapter != null) {
			if (page_index >= 0 && page_index < view_list.size()) {
				adapter.sharePageItems(view_list.get(page_index), data);
			}
		}
		return data;
	}

	public void onDelete() {
		if (adapter != null) {
			for (int i = 0; i < view_list.size(); i++) {
				adapter.onDelete(view_list.get(i));
			}
			// if (page_index >= 0 && page_index < view_list.size()) {
			// adapter.onDelete(view_list.get(page_index));
			// }
		}
	}

	public void syncPages() {
		for (int i = 0; i < pageCount && view_list.size() > 0; i++) {
			if (preAdapter != null)
				preAdapter.free((ViewGroup3D) view_list.get(0));
			this.removeView(view_list.get(0));
			view_list.remove(0);
		}
		if(adapter instanceof AudioAdapter){
			this.indicatorView.hide();
		}else{
			this.indicatorView.show();
		}
		pageCount = adapter.syncDataPageCount();
		if (page_index >= pageCount)
			page_index = pageCount - 1;
		for (int i = 0; i < pageCount; i++) {
			addPage(i, adapter.obtainView());
			adapter.syncPageItems(view_list.get(i), i, page_index, true);
		}
		if (pageCount == 0) {
			pageCount = 1;
			page_index = 0;
			addPage(0, adapter.obtainView());
			iLoongLauncher.getInstance().mMainHandler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (adapter instanceof PhotoBucketAdapter) {

						Toast.makeText(iLoongLauncher.getInstance(), 
								iLoongLauncher.getInstance().getResources().getString(RR.string.medialist_no_photo),
								Toast.LENGTH_SHORT).show();
					} else if (adapter instanceof AudioAlbumAdapter) {

						Toast.makeText(iLoongLauncher.getInstance(),
								iLoongLauncher.getInstance().getResources().getString(RR.string.medialist_no_audio),
								Toast.LENGTH_SHORT).show();
					} else if (adapter instanceof VideoAdapter) {

						Toast.makeText(iLoongLauncher.getInstance(),
								iLoongLauncher.getInstance().getResources().getString(RR.string.medialist_no_video),
								Toast.LENGTH_SHORT).show();
					}
				}

			});

		}
		this.preAdapter = this.adapter;
		super.initView();
	}

	public void setAdapter(MediaListAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		y = this.getUser();
		int h = iLoongLauncher.getInstance().getResources().getDisplayMetrics().heightPixels;
		
		refreshBg();	//xiatian add	//Mainmenu Bg
		
		if (translucentBg != null) {
			batch.setColor(color.r, color.g, color.b, color.a * parentAlpha
					* (height + y) / height);

			translucentBg.draw(batch, 0, 0, Utils3D.getScreenWidth(), h);
		}
		super.draw(batch, parentAlpha);
	}

	public void show() {
		super.show();
		Color c = indicatorView.getColor();
		indicatorView.setColor(c.r, c.g, c.b, 0);
		indicatorView.startTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
				0.5f, 1.0f, 0, 0);

	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if (keycode == KeyEvent.KEYCODE_BACK)
			return true;
		return super.keyDown(keycode);
	}

	public void setAppBar(AppBar3D appBar) {
		this.appBar = appBar;
	}

	protected int nextIndex() {
		if (page_index == pageCount - 1) {
			return 0;
		} else {
			return page_index + 1;
		}
	}

	protected int preIndex() {
		if (page_index == 0) {
			return pageCount - 1;
		} else {
			return page_index - 1;
		}
	}

	protected void updateEffect() {
		// Log.v("AppList3D", "updateEffect");
		if (view_list.size() == 0)
			return;
		if (page_index<0) {
			page_index = 0;
			return;
		}
		if(page_index>view_list.size()-1){
			page_index = view_list.size()-1;
			return;
		}
		if (needLayout) {
			for (View3D i : view_list) {
				if (i instanceof GridView3D) {
					((GridView3D) i).layout_pub(0, false);
				}
			}
			needLayout = false;
		}
		ViewGroup3D cur_view = (ViewGroup3D) view_list.get(page_index);
		ViewGroup3D pre_view = (ViewGroup3D) view_list.get(preIndex());
		ViewGroup3D next_view = (ViewGroup3D) view_list.get(nextIndex());

		if (!moving) {
			changeEffect();
			moving = true;
			for (View3D i : view_list) {
				if (i instanceof GridView3D) {
					for (int j = 0; j < ((GridView3D) i).getChildCount(); j++) {
						View3D icon = ((GridView3D) i).getChildAt(j);
						icon.setTag(new Vector2(icon.getX(), icon.getY()));
					}
				}
			}

		}
		float tempYScale = 0;

		// if (needXRotation) {
		if (yScale > MAX_X_ROTATION) {
			tempYScale = MAX_X_ROTATION;
		} else if (yScale < -MAX_X_ROTATION) {
			tempYScale = -MAX_X_ROTATION;
		} else {
			tempYScale = yScale;
		}
		// }

		tempYScale = -tempYScale;

		if (super.getRandom() == false && this.mType == 0) {
			APageEase.setStandard(true);
		} else {
			APageEase.setStandard(false);
		}
		// Log.v("abc",
		// "xScale="+xScale+" yScale="+yScale+" pre="+pre_view.getChildAt(0).name+" cur="+cur_view.getChildAt(0).name+" next="+next_view.getChildAt(0).name);

		if (xScale > 0) {
			next_view.hide();
			// initData(next_view);
			APageEase.updateEffect(pre_view, cur_view, xScale - 1, tempYScale,
					mTypelist.get(mType));

		} else if (xScale < 0) {
			pre_view.hide();
			// initData(pre_view);
			APageEase.updateEffect(cur_view, next_view, xScale, tempYScale,
					mTypelist.get(mType));

		} else if (yScale != 0) {
			APageEase.updateEffect(cur_view, next_view, xScale, tempYScale,
					mTypelist.get(mType));
		}

		if (xScale < -1f) {
			cur_view.hide();
			// initData(cur_view);
			page_index = nextIndex();
			setDegree(xScale + 1f);
			changeEffect();
		}
		if (xScale > 1f) {
			cur_view.hide();
			// initData(cur_view);
			page_index = preIndex();
			changeEffect();
		}
	}

	@Override
	protected void initView() {
		refresh();
		super.initView();
	}

	@Override
	protected void finishAutoEffect() {
		super.finishAutoEffect();
	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		return super.onTouchDown(x, y, pointer);
	}

	@Override
	public boolean onTouchUp(float x, float y, int pointer) {
		return super.onTouchUp(x, y, pointer);
	}

	@Override
	public boolean fling(float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		if (pageCount == 1)
			return true;

		return super.fling(velocityX, velocityY);
	}

	@Override
	public boolean scroll(float x, float y, float deltaX, float deltaY) {
		// Log.v("AppList3D", "scroll: x:" + x + " y:" + y + " deltaX:" + deltaX
		// + " deltaY:" + deltaY + " xScale:" + xScale);
		if (pageCount == 1)
			return true;
		return super.scroll(x, y, deltaX, deltaY);
	}
	
	//xiatian add start	//Mainmenu Bg
	private void initBgIconName()
	{
		bg_icon_name.put(0, "theme/pack_source/translucent-bg.png");
		bg_icon_name.put(1, "theme/pack_source/translucent-bg-opa.png");
		bg_icon_name.put(2, "theme/pack_source/translucent-black.png");
	}	
	
	private void refreshBg()
	{
		String mainmenu_bg_key = iLoongLauncher.getInstance().getResources()
				.getString(RR.string.mainmenu_bg_key);
		String mainmenu_bg_value = PreferenceManager
				.getDefaultSharedPreferences(SetupMenu.getContext())
				.getString(mainmenu_bg_key, "-1");
		if (mainmenu_bg_value.equals("-1")) 
		{
			PreferenceManager
				.getDefaultSharedPreferences(SetupMenu.getContext())
				.edit()
				.putString(mainmenu_bg_key, DefaultLayout.defaultMainmenuBgIndex + "")
				.commit();
			mainmenu_bg_value = DefaultLayout.defaultMainmenuBgIndex + "";
			
			DefaultLayout.lastMediaListMainmenuBgIndex = DefaultLayout.defaultMainmenuBgIndex;
		}
		else if(
				(mainmenu_bg_value.equals(DefaultLayout.lastMediaListMainmenuBgIndex + ""))
				&&(translucentBg != null)
		)
		{
			return;
		}
		else
		{
			DefaultLayout.lastMediaListMainmenuBgIndex = Integer.valueOf(mainmenu_bg_value).intValue();
		} 

		int mainmenu_bg_index = Integer.valueOf(mainmenu_bg_value).intValue();	
		String bg_name = bg_icon_name.get(mainmenu_bg_index);
		
		Bitmap bmp = ThemeManager.getInstance().getBitmap(bg_name);
		if (bmp.getConfig() != Config.ARGB_8888)
		{
			bmp = bmp.copy(Config.ARGB_8888, false);
		}
		Texture t = new BitmapTexture(bmp);
		translucentBg = new NinePatch(new TextureRegion(t),1,1,1,1);
		bmp.recycle();
	}
	//xiatian add end

}
