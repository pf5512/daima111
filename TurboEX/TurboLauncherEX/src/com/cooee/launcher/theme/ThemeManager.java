package com.cooee.launcher.theme;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.android.launcher.framework.IconCache;
import com.android.launcher.framework.Utilities;
import com.cooee.launcher.LauncherApplication;
import com.cooee.launcher.smartlayout.SLInjection;
import com.cooee.uiengine.util.sharepreference.SharePreferenceUtil;
import com.cooee.uiengine.util.xml.AssetsXmlHelper;
import com.cooee.uiengine.util.xml.ResXmlHelper;

/**
 * 
 * @author zhongqihong
 * 
 *         All bitmaps used in the launcher are distributed here.
 * 
 *         if any bitmap can't be found here ,you have to report this problem to @zhongqihong
 * 
 * **/
public class ThemeManager {
	private final String TAG = "ThemeManager";
	private static ThemeManager mInstance;
	private static Context mLauncherContext;
	private ThemeDescriptor mThemeDescriptor;
	private List<Bitmap> iconBgs = null;
	private List<Bitmap> mask = null;
	private List<Bitmap> cover = null;

	private final static String PREFIX_HOTSEAT = "hotseat_";
	private final static String PREFIX_VIRTUAL = "virtual_";
	private final static String EXTENSION_PNG = "png";
	private final static String URI_HOTSEAT = "intent";
	private final static String URI_VIRTUAL = "virtual";
	// The Availables below are created to mark the initiation of the resources
	// to avoid repeat.
	public boolean mIconBgInted = false;
	public boolean mIconMaskInted = false;
	public boolean mIconCoverInted = false;
	public IconCache mIconCache;
	public LauncherApplication application;

	public ThemeManager(Context context) {

		mInstance = this;
		mLauncherContext = context;
		application = (LauncherApplication) mLauncherContext
				.getApplicationContext();
		mIconCache = application.mIconCache;
		init();

		// God loves Zhongqihong@2014/12/16 ADD START
		// here to do something about default layout.
		// the functions below have to be called after {#init()}
		Context useContext = getUsefulContext();
		initIcons(useContext);
		initConfig(useContext);
		// God loves Zhongqihong@2014/12/16 ADD END

		// God loves Zhongqihong@2014/12/20 UPD START

		initHotseat();

		ThemeLayout tl = new ThemeLayout();

		if (!SharePreferenceUtil.getVirtualState()) {
			tl.insertVirtualIcons(useContext);
			SharePreferenceUtil.setVirtualState(true);
		}

		// God loves Zhongqihong@2014/12/20 UPD END

	}

	public static ThemeManager getInstance() {
		if (mInstance == null) {
			throw new RuntimeException(
					"zhongqihong:theme-instance has not been inited yet");
		}
		return mInstance;
	}

	public ThemeDescriptor getCurrentThemeContext() {

		return mThemeDescriptor;
	}

	public void init() {

		ResolveInfo resinfo = null;
		String currentThemePackageName = null;
		ThemeDescriptor ThemeDesc;
		ArrayList<ResolveInfo> localResDesList = getThemeList();
		Iterator<ResolveInfo> it = localResDesList.iterator();
		// get current Theme,if Preferences has not theme then currentTeme is
		// ThemeConstants.default_theme_package_name
		SharedPreferences prefs = mLauncherContext.getSharedPreferences(
				"theme", Activity.MODE_WORLD_WRITEABLE);
		String themeName = prefs.getString("theme", "none");
		if (themeName.equals("none")) {
			currentThemePackageName = ThemeConstants.default_theme_package_name;
		} else {
			currentThemePackageName = themeName;
		}

		// PubProviderHelper.addOrUpdateValue( "theme" , "theme" , defaultTheme
		// );
		while (it.hasNext()) {
			resinfo = it.next();
			if (currentThemePackageName != null
					&& resinfo.activityInfo.applicationInfo.packageName
							.equals(currentThemePackageName)) {
				mThemeDescriptor = getThemeDescription(resinfo);
			}
		}

	}

	private ArrayList<ResolveInfo> getThemeList() {
		ArrayList<ResolveInfo> reslist = new ArrayList<ResolveInfo>();
		Intent intent = null;

		intent = new Intent(ThemeConstants.theme_action, null);
		List<ResolveInfo> themesinfo = mLauncherContext.getPackageManager()
				.queryIntentActivities(intent, 0);
		Collections.sort(themesinfo, new ResolveInfo.DisplayNameComparator(
				mLauncherContext.getPackageManager()));
		int themescount = themesinfo.size();
		for (int index = 0; index < themescount; index++) {
			ResolveInfo resinfo = themesinfo.get(index);
			reslist.add(resinfo);
		}
		return reslist;
	}

	private ThemeDescriptor getThemeDescription(ResolveInfo resinfo) {
		Context slaveContext = null;
		try {
			if (!resinfo.activityInfo.applicationInfo.packageName
					.equals(mLauncherContext.getPackageName())) {
				slaveContext = mLauncherContext.createPackageContext(
						resinfo.activityInfo.applicationInfo.packageName,
						Context.CONTEXT_IGNORE_SECURITY);
			} else {
				slaveContext = mLauncherContext;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (slaveContext == null) {
			return null;
		}
		ThemeDescriptor themeDes = CreateThemeDescription(slaveContext, resinfo);
		return themeDes;
	}

	private ThemeDescriptor CreateThemeDescription(Context context,
			ResolveInfo resinfo) {
		ThemeDescriptor themeDescription = new ThemeDescriptor(context);
		themeDescription.mComponentName = new ComponentName(
				resinfo.activityInfo.applicationInfo.packageName,
				resinfo.activityInfo.name);
		String defaultTheme = mLauncherContext.getPackageName();
		if (ThemeConstants.default_theme_package_name != null)
			defaultTheme = ThemeConstants.default_theme_package_name;
		if (resinfo.activityInfo.applicationInfo.packageName
				.equals(defaultTheme))

			themeDescription.mTitle = ThemeConstants.defatul_theme_title;

		else
			themeDescription.mTitle = resinfo.loadLabel(mLauncherContext
					.getPackageManager());
		themeDescription.mBuiltIn = (resinfo.activityInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0;
		return themeDescription;
	}

	private Bitmap getResBitmap(String name) {

		name = name.toLowerCase();
		int resId = 0;
		Context context = getUsefulContext();

		resId = !TextUtils.isEmpty(name) ? context.getResources()
				.getIdentifier(name, "drawable", context.getPackageName()) : 0;

		if (resId == 0)
			return null;
		else
			return BitmapFactory.decodeResource(context.getResources(), resId);

	}

	// God loves Zhongqihong@2014/12/18 ADD START

	private Bitmap getBitmap(String name) {

		return getBitmap(name, false);

	}

	private Bitmap getBitmap(String nameWithPath, boolean force) {
		Bitmap bitmap = null;
		if (nameWithPath == null || nameWithPath.equalsIgnoreCase(" ")) {

			return null;
		}

		if (force == false) {
			int nameIndex = nameWithPath.lastIndexOf("/");

			int suffixIndex = nameWithPath.lastIndexOf(".");
			if (suffixIndex < 0)
				throw new RuntimeException("There is no image with name: "
						+ nameWithPath);
			String name = nameWithPath.substring(nameIndex + 1, suffixIndex);

			bitmap = getResBitmap(name);
		}

		// maybe the bitmap has been found from Res.
		if (bitmap != null) {
			return bitmap;
		}

		bitmap = getAssetsBitmap(nameWithPath);

		return bitmap;

	}

	// private Bitmap getBitmapFromAssets(String name) {
	//
	//
	// Bitmap bitmap = null;
	//
	// try {
	//
	// InputStream input = getInputStream(name);
	//
	// if (input != null) {
	// bitmap = getImageFromInStream(input);
	//
	// input.close();
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return bitmap;
	// }

	private InputStream getInputStream(String name) {
		InputStream input = null;
		Context context = getUsefulContext();
		try {
			AssetManager asset = context.getAssets();
			input = asset.open(name);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}

	public static Bitmap getImageFromInStream(InputStream is) {
		Bitmap image = null;
		try {
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
		}
		return image;
	}

	// God loves Zhongqihong@2014/12/18 ADD END

	private Bitmap getIconBitmap(String name) {

		Bitmap b = getResBitmap(name);

		if (b == null) {
			String path = "theme/icon/80/";
			String fullName = path + name + ".png";
			return getAssetsBitmap(fullName);
		}

		Bitmap bmp = Utilities.resizeImage(b, (int) (b.getWidth() * 0.8),
				(int) (b.getHeight() * 0.8));

		if (b != bmp)
			b.recycle();
		return bmp;
	}

	private Bitmap getBeautyBitmap(String name) {

		Bitmap b = getResBitmap(name);
		Bitmap bmp2 = null;

		// following codes work for adapting the old themes.
		if (b == null) {
			String path = "theme/iconbg/";
			String fullName = "";

			if (name.contains("mask")) {
				/**
				 * zhongqihong emphasizes : The old theme has only one
				 * "mask.png", as the result of which we should always give null
				 * here unless the given name is "maks_o"
				 * 
				 * **/
				if (name.equals("mask_0")) {
					fullName = path + "mask.png";
				} else
					return null;
			} else if (name.contains("cover")) {
				if (name.equals("icon_cover_plate")) {
					fullName = path + "icon_cover_plate.png";
				} else
					return null;
			} else if (name.contains("icon_")) {
				fullName = path + name + ".png";
			} else
				return null;

			b = getAssetsBitmap(fullName);
		}

		if (b != null) {
			bmp2 = Utilities.resizeImageAsIcon(b, mLauncherContext);
		}
		if (bmp2 != null && bmp2 != b)
			b.recycle();
		return bmp2;
	}

	private Bitmap getHotseatBitmap(String name) {

		// if (name == null) {
		// return null;
		// }
		// Bitmap icon = null;
		// Bitmap bitmap = getResBitmap(name);
		//
		// if (bitmap == null) {
		// int prefixIndex = name.indexOf("_") + 1;
		// String img = name.substring(prefixIndex, name.length()) + ".png";
		// String fileName = "theme/hotseatbar/" + img;
		// bitmap = getAssetsBitmap(fileName);
		// }
		//
		// if (bitmap != null) {
		// icon = mIconCache.beautyIcon(bitmap, false);
		// }
		//
		// if (bitmap != null && bitmap != icon)
		// bitmap.recycle();
		// return icon;

		return getSpecificBitmap(name, PREFIX_HOTSEAT, EXTENSION_PNG,
				"theme/hotseatbar/");

	}

	private Bitmap getVirtualBitmap(String name) {

		return getSpecificBitmap(name, PREFIX_VIRTUAL, EXTENSION_PNG,
				"theme/icon/80/");
	}

	private Bitmap getSpecificBitmap(String name, String prefix, String suffix,
			String path) {
		if (name == null) {
			return null;
		}
		Bitmap icon = null;
		Bitmap bitmap = getResBitmap(name);

		if (bitmap == null) {
			int prefixIndex = name.indexOf(prefix);
			String img = name.substring(prefixIndex + prefix.length(),
					name.length())
					+ "." + suffix;
			String fileName = path + img;
			bitmap = getAssetsBitmap(fileName);
		}

		if (bitmap != null) {
			icon = mIconCache.beautyIcon(bitmap, false);
		}

		if (bitmap != null && bitmap != icon)
			bitmap.recycle();
		return icon;
	}

	// God loves Zhongqihong@2014/12/20 UPD START

	public Bitmap imageInterpreter(String uri) {

		Bitmap image = null;

		if (uri == null)
			return null;
		if (uri.contains(URI_HOTSEAT)) {
			image = hotseatInterpreter(uri);
		} else if (uri.contains(URI_VIRTUAL)) {
			image = virtualInterpreter(uri);
		}

		return image;
	}

	private Bitmap virtualInterpreter(String uri) {

		boolean flag = false;
		String image = null;
		for (int i = 0; i < ThemeData.virtualIcons.size(); i++) {

			String intentUri = ThemeData.virtualIcons.get(i).intentUri;

			if (intentUri.equalsIgnoreCase(uri)) {
				image = ThemeData.virtualIcons.get(i).icon;
				flag = true;
				break;
			}
		}

		if (flag == false) {
			return null;
		}
		return getVirtualBitmap(image);
	}

	private Bitmap hotseatInterpreter(String uri) {

		String image = null;
		for (int i = 0; i < ThemeData.hotseatIcons.size(); i++) {

			String intentUri = ThemeData.hotseatIcons.get(i).intentUri;

			String name = intentUri.substring("intent:".length(),
					intentUri.length() - "end".length());
			if (uri.contains(name)) {
				image = ThemeData.hotseatIcons.get(i).icon;
				break;
			}
		}

		return getHotseatBitmap(image);
	}

	// God loves Zhongqihong@2014/12/20 UPD END

	private Bitmap getAssetsBitmap(String name) {

		try {
			InputStream instr = getInputStream(name);

			if (instr != null) {
				Bitmap bitmap = Utilities.getImageFromInStream(instr);
				instr.close();
				return bitmap;
			}

		} catch (IOException e) {
		}

		return null;
	}

	private Context getUsefulContext() {
		return mThemeDescriptor == null ? mLauncherContext
				: mThemeDescriptor.mContext;
	}

	private void initIcons(Context context) {

		resolveIcons(context);
		if (ThemeData.defaultIcons.size() == 0) {
			resolveIcons(mLauncherContext);
		}

	}

	// God loves Zhongqihong@2014/12/20 ADD START
	private void initHotseat() {
		ThemeFavoriteHandler hotseatHandler = new ThemeFavoriteHandler(
				mLauncherContext, mLauncherContext.getPackageName());
		new ResXmlHelper(mLauncherContext).parse(hotseatHandler);
	}

	// God loves Zhongqihong@2014/12/20 ADD END

	// God loves Zhongqihong@2014/12/16 ADD START
	//
	private void injectSmartLayout(Context context) {
		SLInjection slInjection = new SLInjection(context);
	}

	// God loves Zhongqihong@2014/12/16 ADD END

	private void resolveIcons(Context context) {

		ThemeIconHandler iconsHandler = new ThemeIconHandler(context,
				context.getPackageName());
		new ResXmlHelper(context).parse(iconsHandler);

		ThemeData.defaultIcons = iconsHandler.getDefaultIcons();
	}

	private void initConfig(Context context) {

		ThemeConfigHandler configHandler = new ThemeConfigHandler(context,
				"theme/cofig.xml");
		new AssetsXmlHelper(context).parse(configHandler);

		if (mThemeDescriptor != null)
			mThemeDescriptor.config = configHandler;
	}

	public Bitmap getDefaultIcon(ResolveInfo info) {
		ArrayList<DefaultIcon> defaultIcon = ThemeData.defaultIcons;
		ArrayList<String> allNamePatten;
		String appName = info.activityInfo.applicationInfo.packageName;
		String className = info.activityInfo.name;
		// if (checkAppType(appName) == 1) {/* 内置应用 */
		for (int inter = 0; inter < defaultIcon.size(); inter++) {
			// if (defaultIcon.get(inter).dealed)
			// continue;
			allNamePatten = defaultIcon.get(inter).pkgNameArray;
			if (null == allNamePatten || allNamePatten.size() <= 0)
				continue;
			for (int k = 0; k < allNamePatten.size(); k++) {
				if (appName.equals(allNamePatten.get(k))) {
					if (defaultIcon.get(inter).classNameArray.size() > 0) {
						for (int i = 0; i < defaultIcon.get(inter).classNameArray
								.size(); i++) {
							if (className
									.equals(defaultIcon.get(inter).classNameArray
											.get(i))) {
								// defaultIcon.get(inter).dealed = true;

								// changeIcon( info , defaultIcon.get( inter
								// ).imageName );
								return getIconBitmap(defaultIcon.get(inter).imageName);
								// break out;
							}
						}
					}
				}
			}
		}
		return null;
	}

	// icon bg mask cover , change by shlt@2014/11/11 ADD START

	private int randomIndex(int lenght) {
		if (lenght == 0)
			return -1;
		else if (lenght == 1)
			return 0;
		else
			return (int) (Math.random() * lenght);
	}

	public Bitmap getIconBg() {

		if (mIconBgInted == false) {
			if (iconBgs == null || iconBgs.size() == 0) {
				iconBgs = new LinkedList<Bitmap>();
				//
				for (int i = 0;; i++) {
					Bitmap bmp = getBeautyBitmap("icon_" + i);
					if (bmp == null) {
						break;
					} else {

						// Bitmap bmp2 = Utilities.createIconBitmap(bmp,
						// mLauncherContext);
						// iconBgs.add(bmp2);
						// if (bmp != bmp2)
						// bmp.recycle();

						iconBgs.add(bmp);

					}
				}
				// if (iconBgs.size() == 0 && mThemeDescriptor == null) {
				// iconBgs.add(Utilities.createIconBitmap(mLauncherContext
				// .getResources().getDrawable(R.drawable.icon_0),
				// mLauncherContext));
				// }

			}
			mIconBgInted = true;
		}

		int index = randomIndex(iconBgs.size());
		return index == -1 ? null : iconBgs.get(index);
	}

	public Bitmap getIconMask() {
		if (mIconMaskInted == false) {
			if (mask == null || mask.size() == 0) {
				mask = new LinkedList<Bitmap>();
				//
				for (int i = 0;; i++) {
					Bitmap bmp = getBeautyBitmap("mask_" + i);
					if (bmp == null) {
						break;
					} else {

						Bitmap bmp2 = Utilities.createIconBitmap(bmp,
								mLauncherContext);
						mask.add(bmp2);
						if (bmp != bmp2)
							bmp.recycle();
					}
				}
				if (mask.size() == 0) {
					Bitmap bmp = getBeautyBitmap("mask");
					if (bmp != null) {
						Bitmap bmp2 = Utilities.createIconBitmap(bmp,
								mLauncherContext);
						mask.add(bmp2);
						if (bmp != bmp2)
							bmp.recycle();
					}
				}
				// if (mask.size() == 0 && mThemeDescriptor == null) {
				// mask.add(Utilities.createIconBitmap(mLauncherContext
				// .getResources().getDrawable(R.drawable.mask_0),
				// mLauncherContext));
				// }
			}
			mIconMaskInted = true;
		}

		int index = randomIndex(mask.size());
		return index == -1 ? null : mask.get(index);
	}

	public Bitmap getIconCover() {

		if (mIconCoverInted == false) {
			if (cover == null || cover.size() == 0) {
				cover = new LinkedList<Bitmap>();
				//
				for (int i = 0;; i++) {
					Bitmap bmp = getBeautyBitmap("icon_cover_plate_" + i);
					if (bmp == null) {
						break;
					} else {

						Bitmap bmp2 = Utilities.createIconBitmap(bmp,
								mLauncherContext);
						cover.add(bmp2);
						if (bmp != bmp2)
							bmp.recycle();
					}
				}
				if (cover.size() == 0) {
					Bitmap bmp = getBeautyBitmap("icon_cover_plate");
					if (bmp != null) {
						Bitmap bmp2 = Utilities.createIconBitmap(bmp,
								mLauncherContext);
						cover.add(bmp2);
						if (bmp != bmp2)
							bmp.recycle();

					}
				}
				// if (cover.size() == 0 && mThemeDescriptor == null) {
				// cover.add(Utilities.createIconBitmap(
				// mLauncherContext.getResources().getDrawable(
				// R.drawable.icon_cover_plate),
				// mLauncherContext));
				// }
			}

			mIconCoverInted = true;
		}

		int index = randomIndex(cover.size());
		return index == -1 ? null : cover.get(index);
	}

	// icon bg mask cover , change by shlt@2014/11/11 ADD END
	public void destory() {
		ThemeData.defaultIcons.clear();
	}

	// public Bitmap getBitmap(String name) {
	// Bitmap bitmap = getResBitmap(name);
	// if (bitmap == null) {
	// bitmap = getAssetsBitmap(name);
	// }
	// return bitmap;
	// }

	public void RemovePackage(String packageName) {
		Log.v(TAG, "RemovePackage");

		if (mThemeDescriptor != null
				&& mThemeDescriptor.mComponentName.getPackageName().equals(
						packageName)) {

			Intent intent = new Intent(
					ThemeReceiver.ACTION_LAUNCHER_APPLY_THEME);
			intent.putExtra("theme_status", 1);
			intent.putExtra("theme", ThemeConstants.default_theme_package_name);
			mLauncherContext.sendBroadcast(intent);
		}

	}

	public int getThemeConfigInteger(String key) {
		if (mThemeDescriptor != null)
			return mThemeDescriptor.getInteger(key);
		else
			return 100;
	}
}
