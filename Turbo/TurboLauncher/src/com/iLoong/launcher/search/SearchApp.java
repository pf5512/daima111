package com.iLoong.launcher.search;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.Icon3D;

public class SearchApp extends Icon3D {

	public String title_pinyin;
	public String initial_pinyin;
	public Intent intent;

	public SearchApp(String name, TextureRegion region) {
		super(name, region);
		Log.i("SearchApp", name);
		title_pinyin = PinYin.getPingYin(name);
		initial_pinyin = PinYin.getPinYinHeadChar(name);
	}

	public SearchApp(String name, Bitmap bmp, String title, Bitmap iconBg,
			boolean ifShadow) {
		super(name, bmp, title, iconBg, ifShadow);
		title_pinyin = PinYin.getPingYin(title);
		initial_pinyin = PinYin.getPinYinHeadChar(name);
	}

	@Override
	public boolean onLongClick(float x, float y) {
		return true;
	}

	@Override
	public boolean onDoubleClick(float x, float y) {
		return true;
	}
}
