package com.cooee.launcher.theme;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.cooee.uiengine.util.xml.ResXmlHandler;
import com.cooeeui.brand.turbolauncher.R;

public class ThemeIconHandler implements ResXmlHandler {

	private final String TAG = "IconsHandler";
	private final String TAG_ICONS = "icons";
	private final String TAG_ICON = "icon";
	private final String xml = "icons";
	private Context launcherContext;
	private Context targetContext;
	private ArrayList<DefaultIcon> defaultIcons = new ArrayList<DefaultIcon>();

	public ThemeIconHandler(Context context, String targetPackage) {
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
				R.styleable.icons);
		DefaultIcon icon = new DefaultIcon();
		icon.title = parser.getAttributeValue(0);
		icon.className = parser.getAttributeValue(1);
		icon.imageName = parser.getAttributeValue(3);
		icon.pkgName = parser.getAttributeValue(4);

		// perhaps we should parse following arrays in the future to make app be
		// launched quicker.
		icon.pkgNameArray = new ArrayList<String>();
		icon.pkgNameArray.clear();
		resloveArrayString(icon.pkgNameArray, icon.pkgName);
		icon.classNameArray = new ArrayList<String>();
		icon.classNameArray.clear();
		resloveArrayString(icon.classNameArray, icon.className);
		defaultIcons.add(icon);
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
