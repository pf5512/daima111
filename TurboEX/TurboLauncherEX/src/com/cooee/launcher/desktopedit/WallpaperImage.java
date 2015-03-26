package com.cooee.launcher.desktopedit;

import android.graphics.Bitmap;

/**
 * 桌面编辑模块代码
 * 
 * @author LinYu
 * 
 */
public class WallpaperImage {
	private String name;
	private Bitmap bit;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Bitmap getBit() {
		return bit;
	}

	public void setBit(Bitmap bit) {
		this.bit = bit;
	}
}
