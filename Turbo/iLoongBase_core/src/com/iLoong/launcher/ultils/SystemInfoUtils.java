package com.iLoong.launcher.ultils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class SystemInfoUtils {
	public static final String DEVICE_MANUFACTURER_SAMSUNG = "Samsung";
	private static String mCarrierName;
	private static String mManufacturer;
	private static String mManufacturerModelName;
	private static String mModel;
	private static String mProductName;
	private static String mBrand;
	private static String mOSVersion;
	private static Integer mScreenHeight = null;
	private static Integer mScreenWidth = null;

	private SystemInfoUtils() {

	}

	private static String generateMergedManufacturerModel() {
		String str;
		if (!getDeviceModel().startsWith(getDeviceManufacturer()))
			str = getDeviceManufacturer() + " " + getDeviceModel();
		else
			str = getDeviceModel();
		return str;
	}

	public static String getDeviceCarrierName(Context paramContext) {
		String str;
		if (TextUtils.isEmpty(mCarrierName)) {
			mCarrierName = ((TelephonyManager) paramContext
					.getSystemService("phone")).getNetworkOperatorName();
			str = mCarrierName;
		} else {
			str = mCarrierName;
		}
		return str;
	}

	public static String getDeviceManufacturer() {
		String str;
		if (TextUtils.isEmpty(mManufacturer)) {
			mManufacturer = Build.MANUFACTURER;
			str = mManufacturer;
		} else {
			str = mManufacturer;
		}
		return str;
	}

	public static String getDeviceManufacturerAndModel() {
		String str;
		if (TextUtils.isEmpty(mManufacturerModelName)) {
			mManufacturerModelName = generateMergedManufacturerModel();
			str = mManufacturerModelName;
		} else {
			str = mManufacturerModelName;
		}
		return str;
	}

	public static String getDeviceModel() {
		String str;
		if (TextUtils.isEmpty(mModel)) {
			mModel = Build.MODEL;
			str = mModel;
		} else {
			str = mModel;
		}
		return str;
	}

	public static String getProductName() {
		String str;
		if (TextUtils.isEmpty(mProductName)) {
			mProductName = Build.PRODUCT;
			str = mProductName;
		} else {
			str = mProductName;
		}
		return str;
	}
	
	public static String getProductBrand() {
		String str;
		if (TextUtils.isEmpty(mBrand)) {
			mBrand = Build.BRAND;
			str = mBrand;
		} else {
			str = mBrand;
		}
		return str;
	}
	
	public static String getDeviceOSVersion() {
		String str;
		if (TextUtils.isEmpty(mOSVersion)) {
			mOSVersion = Build.VERSION.RELEASE;
			str = mOSVersion;
		} else {
			str = mOSVersion;
		}
		return str;
	}

	public static int getDeviceScreenHeight(Context paramContext) {
		int i;
		if (mScreenHeight == null) {
			mScreenHeight = Integer
					.valueOf(getDeviceScreenResolution(paramContext).heightPixels);
			i = mScreenHeight.intValue();
		} else {
			i = mScreenHeight.intValue();
		}
		return i;
	}

	public static DisplayMetrics getDeviceScreenResolution(Context paramContext) {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		((WindowManager) paramContext.getSystemService("window"))
				.getDefaultDisplay().getMetrics(localDisplayMetrics);
		return localDisplayMetrics;
	}

	public static int getDeviceScreenWidth(Context paramContext) {
		int i;
		if (mScreenWidth == null) {
			mScreenWidth = Integer
					.valueOf(getDeviceScreenResolution(paramContext).widthPixels);
			i = mScreenWidth.intValue();
		} else {
			i = mScreenWidth.intValue();
		}
		return i;
	}

	public static boolean isGalaxyS3() {
		boolean i;
		if ((!getProductName().contains("s3"))//GT-I9300, I939I etc
				|| (!getDeviceManufacturer().equalsIgnoreCase("Samsung")))
			i = false;
		else
			i = true;
		return i;
	}
	
	public static boolean isNexus5() {
		boolean i;
		if ((!getDeviceModel().contains("Nexus 5"))
				|| (!getProductBrand().equalsIgnoreCase("google")))
			i = false;
		else
			i = true;
		return i;
	}	
	public static boolean isHtcOne() {
		boolean i;
		if ((!getDeviceModel().contains("HTC 802"))
				|| (!getDeviceManufacturer().equalsIgnoreCase("htc")))
			i = false;
		else
			i = true;
		return i;
	}
	
	public static boolean isMeiZuMx2(){
		boolean i;
		if ((!getDeviceModel().contains("M040"))
				|| (!getDeviceManufacturer().equalsIgnoreCase("Meizu")))
			i = false;
		else
			i = true;
		return i;	    	
	}
	


	public static boolean isGenyMotion() {
		return getDeviceManufacturer().equals("Genymotion");
	}

	public static boolean isBelowICS() {
		return Build.VERSION.SDK_INT < 14 ? true : false;
	}

	public static boolean isKitKat() {
		return Build.VERSION.SDK_INT < 19 ? false : true;
	}

}