package com.cooee.launcher.theme;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import com.android.launcher.framework.LauncherSettings;
import com.cooee.uiengine.util.xml.ResXmlHandler;
import com.cooeeui.brand.turbolauncher.R;

public class ThemeFavoriteHandler implements ResXmlHandler {

	private final String TAG = "ThemeHotseatHandler";
	private final String TAG_ICONS = "favorites";
	private final String TAG_ICON = "favorite";
	private final String xml = "default_workspace";
	private Context launcherContext;
	private Context targetContext;
	private ArrayList<DefaultIcon> defaultIcons = new ArrayList<DefaultIcon>();

	public ThemeFavoriteHandler(Context context, String targetPackage) {
		this.targetContext = this.launcherContext = context;
		// try
		// {
		// targetContext = launcherContext.createPackageContext( targetPackage ,
		// Context.CONTEXT_IGNORE_SECURITY );
		// }
		// catch( NameNotFoundException e )
		// {
		// e.printStackTrace();
		// }
	}

	@Override
	public String getStartTag() {
		return TAG_ICONS;
	}

	@Override
	public int file() {
		if (targetContext == null)
			return -1;

		return targetContext.getResources().getIdentifier(xml, "xml",
				targetContext.getPackageName());
	}

	@Override
	public boolean handle(XmlPullParser parser, AttributeSet attrs) {

		final String name = parser.getName();
		if (!name.equals(TAG_ICON))
			return true;
		TypedArray a = targetContext.obtainStyledAttributes(attrs,
				R.styleable.Favorite);
		FavoriteInfo info = new FavoriteInfo();
		String containerStr = a.getString(R.styleable.Favorite_container);
		if (containerStr != null && !containerStr.equals(" ")) {
			if (Integer.parseInt(containerStr) == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
				info.intentUri = a.getString(R.styleable.Favorite_uri);
				info.cellX = Integer.parseInt(a
						.getString(R.styleable.Favorite_x));
				info.cellY = Integer.parseInt(a
						.getString(R.styleable.Favorite_y));
				info.screen = Integer.parseInt(a
						.getString(R.styleable.Favorite_screen));
				info.icon = a.getString(R.styleable.Favorite_icon);
				info.title = a.getString(R.styleable.Favorite_title);
				info.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
				info.className = a.getString(R.styleable.Favorite_className);

				boolean flag = false;
				for (int i = 0; i < ThemeData.hotseatIcons.size(); i++) {
					if (ThemeData.hotseatIcons.get(i).intentUri
							.equalsIgnoreCase(info.intentUri)) {
						flag = true;
						break;
					}

				}
				if (flag == false) {
					ThemeData.hotseatIcons.add(info);
				}

			}
		}

		int itemType = a.getInt(R.styleable.Favorite_itemType, -1);
		int virtualType = Integer.parseInt(launcherContext
				.getString(R.integer.item_type_virtuality));
		if (itemType == virtualType) {
			info.intentUri = a.getString(R.styleable.Favorite_uri);
			info.cellX = Integer.parseInt(a.getString(R.styleable.Favorite_x));
			info.cellY = Integer.parseInt(a.getString(R.styleable.Favorite_y));
			info.screen = Integer.parseInt(a
					.getString(R.styleable.Favorite_screen));
			info.icon = a.getString(R.styleable.Favorite_icon);
			info.title = a.getString(R.styleable.Favorite_title);
			// info.spanX = Integer.parseInt(a
			// .getString(R.styleable.Favorite_spanX));
			// info.spanY = Integer.parseInt(a
			// .getString(R.styleable.Favorite_spanY));
			info.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
			info.className = a.getString(R.styleable.Favorite_className);
			info.pkgName = a.getString(R.styleable.Favorite_packageName);
			info.itemType = LauncherSettings.Favorites.ITEM_TYPE_VIRTUALITY;
			boolean flag = false;
			for (int i = 0; i < ThemeData.virtualIcons.size(); i++) {
				if (ThemeData.virtualIcons.get(i).intentUri
						.equalsIgnoreCase(info.intentUri)) {
					flag = true;
					break;
				}

			}
			if (flag == false) {
				ThemeData.virtualIcons.add(info);
			}

			Log.v(TAG, info.toString());
		}

		a.recycle();
		return true;
	}

	public ArrayList<DefaultIcon> getDefaultIcons() {
		return defaultIcons;
	}

	public static void resloveArrayString(ArrayList<String> stringArray,
			String allName) {
		if (allName == null || allName.length() <= 0) {
			return;
		}
		String[] result = allName.split(";");
		if (result.length <= 0) {
			return;
		} else {
			for (String temp : result) {
				if (!stringArray.contains(temp)) {
					stringArray.add(temp);
				}
			}
		}
	}

	@Override
	public void parseEnd(boolean complete) {

	}
}
