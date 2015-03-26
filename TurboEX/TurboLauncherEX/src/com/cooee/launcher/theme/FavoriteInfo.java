package com.cooee.launcher.theme;

import com.android.launcher.framework.ItemInfo;

/**
 * @author zhongqihong
 * 
 *         FavouriteInfo holds info of a favorite
 * 
 * **/
public class FavoriteInfo extends ItemInfo {

	public String intentUri;
	public String pkgName;
	public String className;

	// God loves Zhongqihong@2014/12/20 ADD START
	public String icon;

	public FavoriteInfo() {

	}

	public FavoriteInfo(ItemInfo info) {
		super(info);
	}

	// God loves Zhongqihong@2014/12/20 ADD END

	@Override
	public String toString() {
		return "Item(id=" + this.id + " type=" + this.itemType + " container="
				+ this.container + " screen=" + screen + " cellX=" + cellX
				+ " cellY=" + cellY + " spanX=" + spanX + " spanY=" + spanY
				+ " dropPos=" + dropPos + " pkgNameArray=" + pkgName
				+ " classNameArray=" + className + " uri=" + intentUri
				+ " icon=" + icon + ")";
	}

}
